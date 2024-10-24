package com.isar.imagine

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.media.AudioManager
import android.media.ToneGenerator
import android.net.Uri
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.SurfaceHolder
import android.view.SurfaceView
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import android.widget.Button
import android.widget.ExpandableListView
import android.widget.Toast
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.SearchView
import androidx.camera.core.CameraProvider
import androidx.camera.core.CameraSelector
import androidx.camera.core.ImageAnalysis
import androidx.camera.core.Preview
import androidx.camera.lifecycle.ProcessCameraProvider
import androidx.core.app.ActivityCompat
import androidx.core.app.DialogCompat
import androidx.core.content.ContextCompat
import com.google.android.gms.vision.CameraSource
import com.google.android.gms.vision.Detector
import com.google.android.gms.vision.barcode.Barcode
import com.google.android.gms.vision.barcode.BarcodeDetector
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.firebase.crashlytics.buildtools.reloc.com.google.common.util.concurrent.ListenableFuture
import com.isar.imagine.Adapters.InventoryExpandableListAdapter
import com.isar.imagine.Adapters.Retailer
import com.isar.imagine.data.model.InventoryItem
import com.isar.imagine.data.model.ItemWithSerialResponse
import com.isar.imagine.databinding.FragmentSecondBinding
import com.isar.imagine.retrofit.RetrofitInstance
import me.kariot.invoicegenerator.data.*
import me.kariot.invoicegenerator.utils.InvoiceGenerator
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.IOException
import java.util.concurrent.ExecutorService
import java.util.concurrent.Executors

class BillingPanelFragment : AppCompatActivity() {

    private lateinit var surfaceView: SurfaceView
    private lateinit var barcodeDetector: BarcodeDetector
    private lateinit var cameraSource: CameraSource
    private val requestCodeCameraPermission = 1001
    private lateinit var toneGen1: ToneGenerator
    private var scannedValue = ""
    private lateinit var expandableListView : ExpandableListView
    private lateinit var binding: FragmentSecondBinding

    private lateinit var inventoryItem: MutableList<ItemWithSerialResponse>
    private lateinit var cameraExecutor: ExecutorService

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = FragmentSecondBinding.inflate(layoutInflater)
        setContentView(binding.root)




        Log.d("BillingPanelFragment", "Checking camera permission")
        cameraExecutor = Executors.newSingleThreadExecutor()
        if (ContextCompat.checkSelfPermission(
                this, Manifest.permission.CAMERA
            ) != PackageManager.PERMISSION_GRANTED
        ) {
            askForCameraPermission()
        } else {
            Log.d("BillingPanelFragment", "Camera permission granted, setting up controls")

            setupControls()
        }
        inventoryItem = mutableListOf()
listViewInitialize(view = binding.root)




        val aniSlide: Animation =
            AnimationUtils.loadAnimation(this, R.anim.scanner_animation)

    }

    private fun askForCameraPermission() {
        Log.d("BillingPanelFragment", "Requesting camera permission")
        ActivityCompat.requestPermissions(
            this, arrayOf(Manifest.permission.CAMERA), requestCodeCameraPermission
        )
    }

    override fun onRequestPermissionsResult(
        requestCode: Int, permissions: Array<out String>, grantResults: IntArray
    ) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults)
        if (requestCode == requestCodeCameraPermission) {
            if (grantResults.isNotEmpty() && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                Log.d("BillingPanelFragment", "Camera permission granted, setting up controls")

                setupControls()

            } else {
                AlertDialog.Builder(this)
                    .setTitle("Permission required")
                    .setMessage("This application needs to access the camera to process barcodes")
                    .setPositiveButton("Ok") { _, _ ->
                        // Keep asking for permission until granted
                        askForCameraPermission()
                    }
                    .setCancelable(false)
                    .create()
                    .apply {
                        setCanceledOnTouchOutside(false)
                        show()
                    }
            }
        }
    }

    private fun setupControls() {
        Log.d("BillingPanelFragment", "Setting up barcode detector and camera source")
        val cameraProviderFuture = ProcessCameraProvider.getInstance(this)
        cameraProviderFuture.addListener({
            val cameraProvider = cameraProviderFuture.get()
            val preview = Preview.Builder()
                .build()
                .also {

                }
            val imageAnalyzer = ImageAnalysis.Builder()
                .setBackpressureStrategy(ImageAnalysis.STRATEGY_KEEP_ONLY_LATEST)
                .build()
                .also {
//                    it.setAnalyzer(cameraExecutor, BarCodeAnalyzer())
                }
            val cameraSelector = CameraSelector.DEFAULT_BACK_CAMERA
            try {
                // Unbind use cases before rebinding
                cameraProvider.unbindAll()

                // Bind use cases to camera
                cameraProvider.bindToLifecycle(
                    this, cameraSelector, preview, imageAnalyzer
                )

            } catch (exc: Exception) {
                exc.printStackTrace()
            }
        }, ContextCompat.getMainExecutor(this))

    }



    private fun sendBill(retailerName: String) {
        // Implementation for sending bill
        val retailer = Retailer(
            name = retailerName
        )
        val billUri = generateBillDetails(retailer)
        // Use the generated bill URI for sharing or further actions
    }

    private fun generateBillDetails(retailer: Retailer): Uri {
        val tableHeader = ModelTableHeader(
            "Item", "Description", "Quantity"
        )
        val customerInfo = ModelInvoiceInfo.ModelCustomerInfo(
            retailer.name,
        )
        val invoiceInfo = ModelInvoiceInfo(
            customerInfo, "123456798", "07-08-24", "500"
        )
        val tableData: List<ModelInvoiceItem> = listOf(
            ModelInvoiceItem("Item 1", "Item 1 Description", "2", "500", "1000", "2000"),
            ModelInvoiceItem("Item 2", "Item 2 Description", "1", "1000", "1000", "1000")
        )

        val invoicePriceInfo = ModelInvoicePriceInfo(
            "Sub Total", "Tax Total", "Grand Total"
        )
        val footerData = ModelInvoiceFooter("Thank you for your purchase!")
        val tableHeader1 = ModelInvoiceHeader(
            "Invoice Header 1", "Invoice Header 2", "Invoice Header 3"
        )

        val pdfGenerator = InvoiceGenerator(this).apply {
            setInvoiceLogo(R.drawable.imagine_logo) // Set invoice logo
            setCurrency("₹") // Set invoice currency
            setInvoiceColor("#FFFFFF") // Set invoice color
        }

        val invoiceUri = pdfGenerator.generatePDF(
            "/isar"
        )

        return invoiceUri
    }

    private fun listViewInitialize(view: View) {
         expandableListView  = view.findViewById(R.id.listViewBilling)

        // Initialize the adapter with empty data initially
        val expandableListAdapter =
            InventoryExpandableListAdapter(applicationContext, emptyList(), emptyMap())
        expandableListView.setAdapter(expandableListAdapter)


        // Handle submitting items
//        view.findViewById<Button>(R.id.button_save).setOnClickListener {
//            Log.e(TAG, "listViewInitialize: $inventoryList", )
//            submitData()
//        }


    }
    private fun addItemToList(list :ItemWithSerialResponse) {
        val name = list.brand
        val model = list.model
        val variant = list.variant
        val color = list.color
        val condition =  list.condition
        val sellingPrice = list.sellingPrice
        val description = list.description
        val notes = list.notes
        val newItem = ItemWithSerialResponse(
            name, model, variant, color, description, condition,  sellingPrice, notes
        )

        // Check for duplicates and increase quantity if exists
        val existingItem = inventoryItem.find {
            it.brand == name && it.model == model && it.variant == variant && it.color == color &&
                    it.condition == condition && it.sellingPrice ==sellingPrice && it.description ==description&& it.notes == notes
        }
        if (existingItem == null) {
            inventoryItem.add(newItem)
        }
        val inventoryItemList: List<InventoryItem> = inventoryItem.map { item ->
            InventoryItem(
                brand = item.brand, // Assuming `ItemWithSerialResponse` has a `brand` field
                model = item.model, // Assuming `ItemWithSerialResponse` has a `model` field
                variant = item.variant.toString(), // Assuming `ItemWithSerialResponse` has a `variant` field
                condition = item.condition, // Assuming `ItemWithSerialResponse` has a `condition` field
                purchasePrice = 0.0, // Replace with actual value or handle appropriately
                sellingPrice = item.sellingPrice, // Assuming `ItemWithSerialResponse` has a `sellingPrice` field
                quantity = 0, // Replace with actual quantity
                notes = item.notes // Assuming `ItemWithSerialResponse` has a `notes` field
            )
        }
        val expandableListTitle = inventoryItemList.map { it.brand }.distinct()
        val expandableListDetail: Map<String, List<InventoryItem>> = inventoryItemList.groupBy { it.brand }


        Log.e("Billing", "addItemToList: $expandableListTitle $expandableListDetail")

        val expandableListAdapter = InventoryExpandableListAdapter(
            applicationContext, expandableListTitle, expandableListDetail
        )
        expandableListView.setAdapter(expandableListAdapter)

    }
    override fun onCreateOptionsMenu(menu: Menu?): Boolean {
        menuInflater.inflate(R.menu.menu_main, menu)

        val searchItem = menu?.findItem(R.id.action_settings)
        val searchView = searchItem?.actionView as SearchView

        searchView.setOnQueryTextListener(object : SearchView.OnQueryTextListener {
            override fun onQueryTextSubmit(query: String?): Boolean {
                // Handle query submission if needed
                if (!query.isNullOrEmpty() && query.length == 14) {
                    Log.e("Search", "new text ${query}")
                    callApiForSearch(query)
                } else if (query.isNullOrEmpty()) {
                    showItemDialog("Error!!", "Please write Serial Number", "Ok")

                } else {
                    showItemDialog("Error!!", "Please enter correct Serial Number", "Ok")
                }


                return false
            }


            override fun onQueryTextChange(newText: String?): Boolean {
                // Filter the RecyclerView items based on the search query
                return true
            }
        })
        return true
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
                            // Show the dialog with the item details
                            showSuceessItemDialog(serialNum,it)
                        }
                    } else {
                        // Handle the error case (e.g., show a message to the user)
                        Log.e("API Error", "Response Code: ${response.code()}")
                    }

                }

                override fun onFailure(call: Call<ItemWithSerialResponse>, t: Throwable) {
                    TODO("Not yet implemented")
                }

            })
    }
    private fun showSuceessItemDialog(serialNum: String, item :ItemWithSerialResponse) {
        val message =
            "Brand : ${item.brand}\n" + "Model : ${item.model}\n" + "Ram : ${item.variant.ram} + Rom : + ${item.variant.rom}" + "Color : ${item.color}" + "Condition : ${item.condition}"
        "Description: ${item.description}" + "Notes : ${item.condition}"
        AlertDialog.Builder(this@BillingPanelFragment).setTitle(serialNum).setMessage(message)
            .setNegativeButton("Cancel") { dialog, which ->
                // Respond to negative button press
                dialog.dismiss()

            }.setPositiveButton("Add Item") { dialog, which ->
                dialog.dismiss()

                // Respond to positive button press
                addItemToList(item)


            }.show()
    }
    // TODO : add save button function camera properly and generate bill and
    private fun showItemDialog(serialNum: String, message: String, positive: String) {

        AlertDialog.Builder(this@BillingPanelFragment).setTitle(serialNum).setMessage(message)
            .setNegativeButton("Cancel") { dialog, which ->
                // Respond to negative button press
                dialog.dismiss()

            }.setPositiveButton(positive) { dialog, which ->
                // Respond to positive button press

                dialog.dismiss()

            }.show()
    }
}
