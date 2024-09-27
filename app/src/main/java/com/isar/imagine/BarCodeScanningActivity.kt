package com.isar.imagine

import WorkflowModel
import android.animation.AnimatorInflater
import android.animation.AnimatorSet
import android.content.Intent
import android.hardware.Camera
import android.os.Bundle
import android.util.Log
import android.view.View
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.google.android.gms.common.internal.Objects
import com.google.android.material.chip.Chip
import com.isar.imagine.barcode.BarcodeProcessor
import com.isar.imagine.barcode.BarcodeResultFragment
import com.isar.imagine.barcode.CameraSource
import com.isar.imagine.barcode.CameraSourcePreview
import com.isar.imagine.barcode.GraphicOverlay
import com.isar.imagine.barcode.SettingsActivity
import com.isar.imagine.data.model.BarcodeField
import com.isar.imagine.data.model.ItemWithSerialResponse
import com.isar.imagine.retrofit.RetrofitInstance
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.util.ArrayList
import android.view.View.OnClickListener
import android.widget.Toast

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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContentView(R.layout.activity_live_barcode)
        preview = findViewById(R.id.camera_preview)
        graphicOverlay = findViewById<GraphicOverlay>(R.id.camera_preview_graphic_overlay).apply {
            setOnClickListener(this@BarCodeScanningActivity)
            cameraSource = CameraSource(this)
        }

        promptChip = findViewById(R.id.bottom_prompt_chip)
        promptChipAnimator =
            (AnimatorInflater.loadAnimator(this, R.animator.bottom_prompt_chip_enter) as AnimatorSet).apply {
                setTarget(promptChip)
            }

        findViewById<View>(R.id.close_button).setOnClickListener(this)
        flashButton = findViewById<View>(R.id.flash_button).apply {
            setOnClickListener(this@BarCodeScanningActivity)
        }
        settingsButton = findViewById<View>(R.id.settings_button).apply {
            setOnClickListener(this@BarCodeScanningActivity)
        }

        setUpWorkflowModel()
    }

    override fun onResume() {
        super.onResume()

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
        currentWorkflowState = WorkflowModel.WorkflowState.NOT_STARTED
        stopCameraPreview()
    }

    override fun onDestroy() {
        super.onDestroy()
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

            val shouldPlayPromptChipEnteringAnimation = wasPromptChipGone && promptChip?.visibility == View.VISIBLE
            promptChipAnimator?.let {
                if (shouldPlayPromptChipEnteringAnimation && !it.isRunning) it.start()
            }
        })

        workflowModel?.detectedBarcode?.observe(this, Observer { barcode ->
            if (barcode != null) {
//                val barcodeFieldList = ArrayList<BarcodeField>()
                callApiForSearch(barcode.rawValue ?: "")
//                barcodeFieldList.add(BarcodeField("Raw Value", barcode.rawValue?: ""))
//                BarcodeResultFragment.show(supportFragmentManager, barcodeFieldList)
            }
        })
    }
    private fun callApiForSearch(serialNum: String) {
        RetrofitInstance.getApiInterface().getItemWithId(serialNum)
            .enqueue(object : Callback<ItemWithSerialResponse> {
                override fun onResponse(
                    call: Call<ItemWithSerialResponse>, response: Response<ItemWithSerialResponse>
                ) {
                    if (response.isSuccessful) {
                        val item = response.body()
                        item?.let {
                            showSuccessItemDialog(it)
                        }
                    } else {


                        onResume()
                        Toast.makeText(applicationContext,"Doesn't Exist $serialNum",Toast.LENGTH_SHORT).show()
                        Log.e("API Error", "Response Code: ${response.code()} $serialNum")
                    }
                }

                override fun onFailure(call: Call<ItemWithSerialResponse>, t: Throwable) {
                    Log.e("API Error", "Failed to fetch item", t)
                }
            })
    }

    private fun showSuccessItemDialog(item: ItemWithSerialResponse) {
        val barcodeFieldList = arrayListOf(
            BarcodeField("Brand", item.brand),
            BarcodeField("Model", item.model),
            BarcodeField("Variant", item.variant.toString()),
            BarcodeField("Color", item.color),
            BarcodeField("Condition", item.condition),
            BarcodeField("Price", item.sellingPrice.toString()),
            BarcodeField("Description", item.description),
            BarcodeField("Notes", item.notes)
            // Add more fields as necessary
        )
        BarcodeResultFragment.show(supportFragmentManager, barcodeFieldList)
    }

    companion object {
        private const val TAG = "LiveBarcodeActivity"
    }
}


