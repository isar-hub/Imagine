package com.isar.imagine

import android.annotation.SuppressLint
import android.app.Activity
import android.app.AlertDialog
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.net.Uri
import android.os.Bundle
import android.provider.Settings
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.SurfaceHolder
import android.view.View
import android.view.ViewGroup
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Toast
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ActivityCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import com.isar.imagine.databinding.ActivityMainBinding
import com.isar.imagine.databinding.FragmentFirstBinding
import com.isar.imagine.databinding.FragmentRetailerPanelBinding
import com.karumi.dexter.Dexter
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionDeniedResponse
import com.karumi.dexter.listener.PermissionGrantedResponse
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.single.PermissionListener
import java.io.IOException




class RetailerPanelFragment :AppCompatActivity() {
    private var _binding: FragmentRetailerPanelBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    private lateinit var context : Context
    lateinit var dexter : Dexter
    lateinit var cameraSource: CameraSource
    private lateinit var barcodeDetector: BarcodeDetector
    private var scannedValue = ""



    private var resultLauncher = registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {
            result -> dexter.check()
    }


    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        _binding = FragmentRetailerPanelBinding.inflate(layoutInflater)
        setContentView(binding.root)


        binding.scanDevice.setOnClickListener{
            checkForPermissions()
        }
    }



    private fun checkForPermissions() {
        dexter = Dexter.withContext(context)
            .withPermission(
                android.Manifest.permission.CAMERA,
            ).withListener(object : PermissionListener {



                override fun onPermissionGranted(p0: PermissionGrantedResponse?) {
                    binding.cameraSurfaceView.visibility = View.VISIBLE
                    binding.barcodeLine.visibility = View.VISIBLE
                    binding.scanDevice.visibility = View.GONE
                    setupControls()
                }

                override fun onPermissionDenied(p0: PermissionDeniedResponse?) {
                    AlertDialog.Builder(context, R.style.Theme_Imagine).apply {
                        setMessage("Please allow the required permissions")
                            .setCancelable(false)
                            .setPositiveButton("Settings") { _, _ ->
                                val reqIntent = Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS)
                                    .apply {
                                        val uri = Uri.fromParts("package", context.packageName, null)
                                        data = uri
                                    }
                                startActivity(reqIntent)
                                resultLauncher.launch(reqIntent)
                            }
                            .setNegativeButton("Cancel") { dialog, _ -> dialog.cancel() }
                        val alert = this.create()
                        alert.show()
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    p0: PermissionRequest?,
                    token: PermissionToken?
                ) {
                    token?.continuePermissionRequest()
                }

            }).withErrorListener{
                Toast.makeText(context, it.name, Toast.LENGTH_SHORT).show()
            } as Dexter
        dexter.check()
    }


    private fun setupControls() {
        barcodeDetector =
            BarcodeDetector.Builder(context).setBarcodeFormats(Barcode.ALL_FORMATS).build()

        cameraSource = CameraSource.Builder(context, barcodeDetector)
            .setRequestedPreviewSize(1920, 1080)
            .setAutoFocusEnabled(true) //you should add this feature
            .build()

        binding.cameraSurfaceView.holder.addCallback(object : SurfaceHolder.Callback {
            @SuppressLint("MissingPermission")
            override fun surfaceCreated(holder: SurfaceHolder) {
                try {
                    //Start preview after 1s delay
                    cameraSource.start(holder)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            @SuppressLint("MissingPermission")
            override fun surfaceChanged(
                holder: SurfaceHolder,
                format: Int,
                width: Int,
                height: Int
            ) {
                try {
                    cameraSource.start(holder)
                } catch (e: IOException) {
                    e.printStackTrace()
                }
            }

            override fun surfaceDestroyed(holder: SurfaceHolder) {
                cameraSource.stop()
            }
        })


        barcodeDetector.setProcessor(object : Detector.Processor<Barcode> {
            override fun release() {
                Toast.makeText(context, "Scanner has been closed", Toast.LENGTH_SHORT)
                    .show()
            }

            override fun receiveDetections(detections: Detector.Detections<Barcode>) {
                val barcodes = detections.detectedItems
                if (barcodes.size() == 1) {
                    scannedValue = barcodes.valueAt(0).rawValue

                    //Don't forget to add this line printing value or finishing activity must run on main thread
                    runOnUiThread {
                        cameraSource.stop()
                        


                        Toast.makeText(context, "value- $scannedValue", Toast.LENGTH_SHORT).show()
                        val intent = Intent(this@RetailerPanelFragment,UserDetailsActivity::class.java).apply {
                            putExtra("id", scannedValue)
                        }
                        startActivity(intent)

                    }
                }else   
                {

                    Toast.makeText(context, "value- else", Toast.LENGTH_SHORT).show()

                }
            }
        })
    }

    override fun onDestroy() {
        super.onDestroy()
        _binding = null
        cameraSource.stop()
    }

}