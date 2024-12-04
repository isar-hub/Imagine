package com.isar.imagine.barcode_scenning

import WorkflowModel
import android.Manifest
import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.content.Intent
import android.content.pm.PackageManager
import android.hardware.Camera
import android.os.Bundle
import android.text.InputType
import android.util.Log
import android.view.View
import android.view.View.OnClickListener
import android.view.ViewGroup.LayoutParams.MATCH_PARENT
import android.widget.EditText
import android.widget.TextView
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.common.internal.Objects
import com.google.android.material.button.MaterialButton
import com.google.android.material.chip.Chip
import com.google.firebase.firestore.FirebaseFirestore
import com.isar.imagine.R
import com.isar.imagine.barcode.utils.BarcodeProcessor
import com.isar.imagine.barcode.BarcodeResultFragment
import com.isar.imagine.barcode.BarcodeResultFragment.Companion.getQuantity
import com.isar.imagine.barcode.CameraSource
import com.isar.imagine.barcode.CameraSourcePreview
import com.isar.imagine.barcode.GraphicOverlay
import com.isar.imagine.barcode.SettingsActivity
import com.isar.imagine.barcode_scenning.models.BillingDataModel
import com.isar.imagine.barcode.data.BarcodeField
import com.isar.imagine.utils.CameraPermissionTextProvider
import com.isar.imagine.utils.CommonMethods
import com.isar.imagine.utils.CustomDialog
import com.isar.imagine.utils.CustomProgressBar
import com.isar.imagine.utils.PermissionDialog.code
import com.isar.imagine.utils.PermissionDialog.permissionDialog
import com.isar.imagine.utils.PermissionTextProvider
import com.isar.imagine.utils.Results
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.io.IOException

class BarCodeScanningActivity : AppCompatActivity(), OnClickListener {

    private var cameraSource: CameraSource? = null
    private var preview: CameraSourcePreview? = null
    private var graphicOverlay: GraphicOverlay? = null
    private var settingsButton: View? = null
    private var flashButton: View? = null
    private var promptChip: Chip? = null
    private var promptChipAnimator: AnimatorSet? = null
    private var workflowModel: WorkflowModel? = null
    private var currentWorkflowState: WorkflowModel.WorkflowState? = null
    private var listData: MutableList<BillingDataModel> = mutableListOf()

    private lateinit var textView: EditText

    private val viewModel: BarCodeScanningViewmodel by viewModels()
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_live_barcode)

        val dataList = intent.getSerializableExtra("dataList") as? ArrayList<BillingDataModel>
        val isBilling = intent.getBooleanExtra("isBilling", false)
        if (dataList != null && isBilling) {
            listData.addAll(dataList)
        }

        textView = EditText(this).apply {
            hint = getQuantity().toString()
            width = MATCH_PARENT
            inputType = InputType.TYPE_CLASS_TEXT
            maxLines = 1

        }

        obserVingViewmodel()

        preview = findViewById(R.id.camera_preview)
        graphicOverlay = findViewById<GraphicOverlay>(R.id.camera_preview_graphic_overlay).apply {
            setOnClickListener(this@BarCodeScanningActivity)
            cameraSource = CameraSource(this)
        }

        promptChip = findViewById(R.id.bottom_prompt_chip)
        promptChipAnimator = (AnimatorInflater.loadAnimator(
            this, R.animator.bottom_prompt_chip_enter
        ) as AnimatorSet).apply {
            setTarget(promptChip)
        }


        findViewById<MaterialButton>(R.id.loginButton).setOnClickListener {
            CustomDialog.showAlertDialog(
                this@BarCodeScanningActivity,
                textView,
                "Enter Serial Number",
                {
                    CoroutineScope(Dispatchers.IO).launch {
                        if (!textView.text.isNullOrEmpty()) {
                            viewModel.isSerialNumber(
                                textView.text.toString(), FirebaseFirestore.getInstance()
                            )

                        }
                    }
                })
        }

        findViewById<View>(R.id.close_button).setOnClickListener {
            finish()
        }
        flashButton = findViewById<View>(R.id.flash_button).apply {
            setOnClickListener(this@BarCodeScanningActivity)
        }
        settingsButton = findViewById<View>(R.id.settings_button).apply {
            setOnClickListener(this@BarCodeScanningActivity)
        }

        requestPermission(
            Manifest.permission.CAMERA, CameraPermissionTextProvider(Manifest.permission.CAMERA)
        )
        setUpWorkflowModel()
        openCamera()
        Log.e("tag", "on create called")
    }

    override fun onStart() {
        super.onStart()
        Log.e("Tag", "on start called")
        setUpWorkflowModel()

    }


    override fun onResume() {
        super.onResume()
        Log.e("tag", "on resume called")
        startCameraPreview()
        workflowModel?.markCameraFrozen()
        settingsButton?.isEnabled = true
        currentWorkflowState = WorkflowModel.WorkflowState.NOT_STARTED
        cameraSource?.setFrameProcessor(BarcodeProcessor(graphicOverlay!!, workflowModel!!))
        workflowModel?.setWorkflowState(WorkflowModel.WorkflowState.DETECTING)
    }

    override fun onPostResume() {
        super.onPostResume()
        BarcodeResultFragment.dismiss(supportFragmentManager)
    }

    override fun onPause() {
        super.onPause()

        CommonMethods.showLogs("BILLING", "ON PAUSE CALLED")
        currentWorkflowState = WorkflowModel.WorkflowState.NOT_STARTED
        stopCameraPreview()
    }

    override fun onDestroy() {
        super.onDestroy()
        Log.e("tag", "on destroy called")
        cameraSource?.release()
        cameraSource = null
    }

    override fun onClick(view: View) {
        when (view.id) {
            R.id.close_button -> onResume()
            R.id.flash_button -> {
                flashButton?.let {
                    if (it.isSelected) {
                        it.isSelected = false
                        cameraSource?.updateFlashMode(Camera.Parameters.FLASH_MODE_OFF)
                    } else {
                        it.isSelected = true
                        cameraSource!!.updateFlashMode(Camera.Parameters.FLASH_MODE_TORCH)
                    }
                }
            }

            R.id.settings_button -> {
                settingsButton?.isEnabled = false
                startActivity(Intent(this, SettingsActivity::class.java))
            }
        }
    }

    private fun startCameraPreview() {
        val workflowModel = this.workflowModel ?: return
        val cameraSource = this.cameraSource ?: return
        if (!workflowModel.isCameraLive) {
            try {
                workflowModel.markCameraLive()
                preview?.start(cameraSource)
            } catch (e: IOException) {
                Log.e(TAG, "Failed to start camera preview!", e)
                cameraSource.release()
                this.cameraSource = null
            }
        }
    }

    private fun stopCameraPreview() {
        val workflowModel = this.workflowModel ?: return
        if (workflowModel.isCameraLive) {
            workflowModel.markCameraFrozen()
            flashButton?.isSelected = false
            preview?.stop()
        }
    }

    private fun setUpWorkflowModel() {
        workflowModel = ViewModelProvider(this)[WorkflowModel::class.java]

        // Observes the workflow state changes, if happens, update the overlay view indicators and
        // camera preview state.
        workflowModel!!.workflowState.observe(this, Observer { workflowState ->
            if (workflowState == null || Objects.equal(currentWorkflowState, workflowState)) {
                return@Observer
            }

            currentWorkflowState = workflowState
            Log.d(TAG, "Current workflow state: ${currentWorkflowState!!.name}")

            val wasPromptChipGone = promptChip?.visibility == View.GONE

            when (workflowState) {
                WorkflowModel.WorkflowState.DETECTING -> {

                    promptChip?.visibility = View.VISIBLE
                    promptChip?.setText(R.string.prompt_point_at_a_barcode)
                    startCameraPreview()
                }

                WorkflowModel.WorkflowState.CONFIRMING -> {
                    promptChip?.visibility = View.VISIBLE
                    promptChip?.setText(R.string.prompt_move_camera_closer)
                    startCameraPreview()
                }

                WorkflowModel.WorkflowState.SEARCHING -> {
                    promptChip?.visibility = View.VISIBLE
                    promptChip?.setText(R.string.prompt_searching)
                    stopCameraPreview()
                }

                WorkflowModel.WorkflowState.DETECTED, WorkflowModel.WorkflowState.SEARCHED -> {
                    promptChip?.visibility = View.GONE
                    stopCameraPreview()
                }

                else -> promptChip?.visibility = View.GONE
            }

            val shouldPlayPromptChipEnteringAnimation =
                wasPromptChipGone && promptChip?.visibility == View.VISIBLE
            promptChipAnimator?.let {
                if (shouldPlayPromptChipEnteringAnimation && !it.isRunning) it.start()
            }
        })

        workflowModel?.detectedBarcode?.observe(this, Observer { barcode ->
            if (barcode != null) {
                CoroutineScope(Dispatchers.IO).launch {
                    if (barcode.rawValue != null) {
                        viewModel.isSerialNumber(
                            barcode.rawValue!!, FirebaseFirestore.getInstance()
                        )

                    } else {
                        Log.e("BARCODE", "barcode is nul ${barcode.displayValue}")
                    }
                }

            }
        })
    }


    private fun obserVingViewmodel() {
        val textView = TextView(this@BarCodeScanningActivity).apply {
            text = "Product Not Available"
        }
        viewModel.serialNumberLiveData.observe(this) {
            when (it) {

                is Results.Error -> {
                    CustomDialog.showAlertDialog(this@BarCodeScanningActivity,
                        textView,
                        "Error",
                        { onResume() },
                        { onResume() }

                    )
                    Log.e("tag", "not present...............")
                    CustomProgressBar.dismiss()
                }

                is Results.Loading -> {
                    CustomProgressBar.show(this@BarCodeScanningActivity, "Loading...")
                    Log.e("tag", "Loading...............")
                }

                is Results.Success -> {
                    showSuccessItemDialog(it.data!!)
                    CustomProgressBar.dismiss()
                    Log.e("BILLING", " data size is ${it.data}")
                }
            }

        }
    }

    private fun openCamera() {
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            requestPermission(
                Manifest.permission.CAMERA, CameraPermissionTextProvider(Manifest.permission.CAMERA)
            )
            // Permission is granted, open the camera

        } else {

            setUpWorkflowModel()

        }
    }


    fun requestPermission(permission: String, textProvider: PermissionTextProvider) {
        if (ActivityCompat.checkSelfPermission(
                this, permission
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            if (ActivityCompat.shouldShowRequestPermissionRationale(
                    this, permission
                )
            ) {

                permissionDialog(this, textProvider, false, onClick = {
                    ActivityCompat.requestPermissions(
                        this, arrayOf(permission), code
                    )
                })
            } else {

                requestPermissions(arrayOf(permission), code)
            }
        }

    }


    private fun showSuccessItemDialog(item: BillingDataModel) {

        if (listData.any { it.quantity == item.quantity }) {
            Log.e(TAG, "Item with serial number ${item.serialNumber} already exists in listData.")
        } else {
            listData.add(item)
        }
        val barcodeFieldList = arrayListOf(
            BarcodeField("Quantity", item.quantity.toString(), true),
            BarcodeField("Serial Number ", item.serialNumber),
            BarcodeField("Brand", item.brand),
            BarcodeField("Model", item.model),
            BarcodeField("Variant", item.variant),
            BarcodeField("Condition", item.condition),
            BarcodeField("Purchase Price", item.purchasePrice.toString()),
            BarcodeField("Selling Price", item.sellingPrice.toString()),
            BarcodeField("Notes", item.notes),
        )
        CommonMethods.showLogs("BILLING", "Size of list data is ${listData.size}")
        BarcodeResultFragment.setItem(listData)
        BarcodeResultFragment.setQuantity(item.quantity)
        BarcodeResultFragment.show(
            supportFragmentManager, barcodeFieldList
        )
    }

    companion object {
        private const val TAG = "LiveBarcodeActivity"
    }
}


