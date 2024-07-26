package com.isar.imagine

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.Manifest.permission.READ_MEDIA_IMAGES
import android.Manifest.permission.READ_MEDIA_VIDEO
import android.Manifest.permission.READ_MEDIA_VISUAL_USER_SELECTED
import android.R
import android.app.Activity
import android.app.AlertDialog
import android.app.Dialog
import android.content.Context
import android.content.ContextWrapper
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Canvas
import android.graphics.Color
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.provider.MediaStore
import android.provider.Settings
import android.util.Log
import android.view.View
import android.view.ViewGroup
import android.view.Window
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Button
import android.widget.ImageView
import android.widget.LinearLayout
import android.widget.Toast
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.ActivityResultLauncher
import androidx.activity.result.PickVisualMediaRequest
import androidx.activity.result.contract.ActivityResultContracts
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import com.google.android.material.snackbar.Snackbar
import com.isar.imagine.utils.SignatureView
import com.karumi.dexter.Dexter
import com.karumi.dexter.MultiplePermissionsReport
import com.karumi.dexter.PermissionToken
import com.karumi.dexter.listener.PermissionRequest
import com.karumi.dexter.listener.multi.MultiplePermissionsListener
import java.io.File
import java.io.FileInputStream
import java.io.FileNotFoundException
import java.io.FileOutputStream


class UserDetailsActivity : AppCompatActivity() {


    private lateinit var signatureImageView: ImageView
    private lateinit var photoImageView: ImageView

    private lateinit var uploadSignatureButton: Button
    private lateinit var uploadPhotoButton: Button


    private var dialog_signature: Dialog? = null

    private lateinit var mSignature: SignatureView
    var file: File? = null

    private lateinit var signatureDestination: File
    var view: View? = null


    private var mContent: LinearLayout? = null
    private lateinit var mGetSign: Button
    private lateinit var mCancel: Button
    private lateinit var mClear: Button

    private var bitmap: Bitmap? = null

    private lateinit var cameraLauncher: ActivityResultLauncher<Intent>



    private lateinit var signatureView: SignatureView

    private lateinit var dexter: Dexter


    //checking dexter for denied permission
    private var resultLauncher =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) { _ ->
            dexter.check()
        }


    // result after choosing image in gallery
    private val pickMedia = registerForActivityResult(ActivityResultContracts.PickVisualMedia()) { uri ->

        if (uri != null) {
            Log.e("PhotoPicker", "Selected URI: $uri")
            photoImageView.setImageURI(uri)
        } else {
            Log.e("PhotoPicker", "No media selected")
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContentView(com.isar.imagine.R.layout.activity_user_details)


        initialization()


    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun initialization() {
        uploadSignatureButton = findViewById(com.isar.imagine.R.id.btnUploadSignature)
        signatureImageView = findViewById(com.isar.imagine.R.id.ivCustomerSignature)
        uploadPhotoButton = findViewById(com.isar.imagine.R.id.btnUploadPhoto)
        photoImageView = findViewById(com.isar.imagine.R.id.ivCustomerPhoto)


        //upload photo functionality
        uploadPhotoButton.setOnClickListener {
            checkAndRequestPermissions()
        }

        //setting state option
        val languages = resources.getStringArray(com.isar.imagine.R.array.State)
        val arrayAdapter =
            ArrayAdapter(applicationContext, com.isar.imagine.R.layout.dropdown_menu, languages)
        val autocompleteTV = findViewById<AutoCompleteTextView>(com.isar.imagine.R.id.device_model)
        autocompleteTV.setAdapter(arrayAdapter)


        //initialization signature dialog
        dialog_signature = Dialog(this)
        dialog_signature!!.requestWindowFeature(Window.FEATURE_NO_TITLE)
        dialog_signature!!.setContentView(com.isar.imagine.R.layout.signature_dialog)
        dialog_signature!!.setCancelable(true)
        uploadSignatureButton.setOnClickListener {
            SignatureView(applicationContext, null)
            showDialog(applicationContext)

        }
    }

    /**
     * upload signature methods
     */
    @RequiresApi(Build.VERSION_CODES.O)
    //showing dialog of signature pad
    private fun showDialog(context: Context) {

        dialog_signature!!.setContentView(com.isar.imagine.R.layout.signature_dialog)
        mContent = dialog_signature!!.findViewById<LinearLayout>(com.isar.imagine.R.id.linearLayout)
        mSignature = SignatureView(context, null)
        mSignature.setBackgroundColor(Color.WHITE)
        mContent!!.addView(
            mSignature, ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT
        )
        mClear = dialog_signature!!.findViewById<Button>(com.isar.imagine.R.id.clear)
        mGetSign = dialog_signature!!.findViewById<Button>(com.isar.imagine.R.id.getsign)
        mCancel = dialog_signature!!.findViewById<Button>(com.isar.imagine.R.id.cancel)
        view = mContent

        mClear.setOnClickListener {
            mSignature.clear()
        }
        mGetSign.setOnClickListener {
            saveSignature(mContent!!)
            dialog_signature!!.dismiss()
        }
        mCancel.setOnClickListener {
            dialog_signature!!.dismiss()
        }

        dialog_signature!!.show()
    }


    @RequiresApi(Build.VERSION_CODES.O)
    //saving signature in user phone
    private fun saveSignature(mContent: LinearLayout) {
        if (bitmap == null) {
            bitmap = Bitmap.createBitmap(mContent.width, mContent.height, Bitmap.Config.RGB_565)
        }
        val canvas = Canvas(bitmap!!)
        mContent.draw(canvas)
        try {
            val cw = ContextWrapper(applicationContext)
            val directory = cw.getDir("imageDir", Context.MODE_PRIVATE)
            val filePath = File(directory, "${mSignature.getCurrentDateTime()}.png")
            signatureDestination = filePath
            val fos = FileOutputStream(filePath)
            bitmap!!.compress(Bitmap.CompressFormat.PNG, 90, fos)

            fos.flush()
            fos.close()
            loadImage()
        } catch (e: Exception) {
            Log.e("Test", "Exception: ${e.printStackTrace()}")
        }
    }

    //loading image into imageview signature
    private fun loadImage() {
        try {
            val file = File(signatureDestination.path)
            if (file.exists()) {
                val bitmap = BitmapFactory.decodeStream(FileInputStream(file))
                signatureImageView.visibility = View.VISIBLE
                signatureImageView.setImageBitmap(bitmap)
            } else {
                Log.e("Test", "File does not exist: ${signatureDestination.path}")
            }
        } catch (e: FileNotFoundException) {
            Log.e("Test", "Catch loading: ${e.printStackTrace()}")
        }
    }


    /**
     * upload photo methods
     */

    //showing dialog to choose camera or gallery
    private fun showImagePickerOptions() {

        val options = arrayOf("Take Photo", "Choose from Gallery")
        val builder = androidx.appcompat.app.AlertDialog.Builder(this)
        builder.setTitle("Choose an option")
        builder.setItems(options) { _, which ->
            when (which) {
                0 -> openCamera()
                1 -> openGallery()
            }
        }
        builder.show()
    }

    private fun openGallery() {
        try {
            pickMedia.launch(PickVisualMediaRequest(ActivityResultContracts.PickVisualMedia.ImageOnly))
        } catch (e: Exception) {
            view?.let {
                Snackbar.make(applicationContext, it, "Error : ${e.message}", Snackbar.LENGTH_LONG)
                    .setAction("OK") {}.show()
            }
        }

    }

    private val REQUEST_IMAGE_CAPTURE = 1

    //opening camera
    private fun openCamera() {
        val takePictureIntent = Intent(MediaStore.ACTION_IMAGE_CAPTURE)
        try {
            startActivityForResult(takePictureIntent, REQUEST_IMAGE_CAPTURE)
        } catch (e: Exception) {
            view?.let {
                Snackbar.make(applicationContext, it, "Error : ${e.message}", Snackbar.LENGTH_LONG)
                    .setAction("OK") {}.show()
            }
        }

    }

    //on activity result for camera image
    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        super.onActivityResult(requestCode, resultCode, data)
        if (requestCode == REQUEST_IMAGE_CAPTURE && resultCode == Activity.RESULT_OK) {
            val imageBitmap = data?.extras?.get("data") as Bitmap
            photoImageView.setImageBitmap(imageBitmap)
        }
    }


    //checking os for permissions
    private fun checkAndRequestPermissions() {
        // Permission request logic
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.UPSIDE_DOWN_CAKE) {

            val requiredPermissions = mutableListOf(
                READ_MEDIA_IMAGES, READ_MEDIA_VIDEO, READ_MEDIA_VISUAL_USER_SELECTED
            )
            getPermission(requiredPermissions)

        } else if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {

            val requiredPermissions = mutableListOf(
                READ_MEDIA_IMAGES, READ_MEDIA_VIDEO
            )
            getPermission(requiredPermissions)

        } else {

            val requiredPermissions = mutableListOf(
                READ_EXTERNAL_STORAGE
            )
            getPermission(requiredPermissions)

        }

    }

    //permissions
    private fun getPermission(permissions: MutableList<String>) {
        dexter = Dexter.withContext(this).withPermissions(
                permissions
            ).withListener(object : MultiplePermissionsListener {
                override fun onPermissionsChecked(report: MultiplePermissionsReport) {
                    report.let {

                        if (report.areAllPermissionsGranted()) {
                            showImagePickerOptions()
                            Toast.makeText(
                                this@UserDetailsActivity, "Permissions Granted", Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            AlertDialog.Builder(this@UserDetailsActivity, R.style.Theme_Dialog)
                                .apply {
                                    setMessage("please allow the required permissions").setCancelable(
                                            false
                                        ).setPositiveButton("Settings") { _, _ ->
                                            val reqIntent =
                                                Intent(Settings.ACTION_APPLICATION_DETAILS_SETTINGS).apply {
                                                        val uri = Uri.fromParts(
                                                            "package", packageName, null
                                                        )
                                                        data = uri
                                                    }
                                            resultLauncher.launch(reqIntent)
                                        }
                                    // setNegativeButton(R.string.cancel) { dialog, _ -> dialog.cancel() }
                                    val alert = this.create()
                                    alert.show()
                                }
                        }
                    }
                }

                override fun onPermissionRationaleShouldBeShown(
                    permissions: List<PermissionRequest?>?, token: PermissionToken?
                ) {
                    token?.continuePermissionRequest()
                }
            }).withErrorListener {
                Toast.makeText(this, it.name, Toast.LENGTH_SHORT).show()
            } as Dexter
        dexter.check()
    }


}




