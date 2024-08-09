package com.isar.imagine.Fragments

import android.content.Context
import android.os.Bundle
import android.os.Environment
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Button
import android.widget.EditText
import android.widget.ExpandableListView
import android.widget.ProgressBar
import android.widget.Spinner
import android.widget.TextView
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.isar.imagine.Adapters.InventoryExpandableListAdapter
import com.isar.imagine.R
import com.isar.imagine.data.model.BrandNamesResponse
import com.isar.imagine.data.model.InventoryItem
import com.isar.imagine.data.model.VariantsAndColorsResponse
import com.isar.imagine.retrofit.RetrofitInstance
import okhttp3.ResponseBody
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellStyle
import org.apache.poi.ss.usermodel.Font
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File
import java.io.FileOutputStream
import kotlin.random.Random


class InventoryFragment : Fragment() {

    private lateinit var spinnerBrandName: Spinner
    private lateinit var spinnerModel: Spinner
    private lateinit var spinnerVariant: Spinner
    private lateinit var spinnerColor: Spinner
    private lateinit var spinnerCondition: Spinner
    private lateinit var editTextDescription: EditText
    private lateinit var editTextSellingPrice: EditText
    private lateinit var editTextPurchasePrice: EditText
    private lateinit var editTextQuantity: EditText
    private lateinit var textViewTotalPrice: TextView
    private lateinit var buttonSave: Button
    private lateinit var context: Context
    private lateinit var progressBar: ProgressBar

    private lateinit var variantList: List<VariantWithID>


    private lateinit var inventoryList: MutableList<InventoryItem>


    private var TAG = "InventoryFragment"

    private val conditionList = listOf("Good", "Best", "Bad")



    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_inventory2, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        intialization(view)

        inventoryList = mutableListOf()
        listViewInitialize(view)


    }

    private fun listViewInitialize(view: View) {
        val expandableListView: ExpandableListView = view.findViewById(R.id.listView)

        // Initialize the adapter with empty data initially
        val expandableListAdapter =
            InventoryExpandableListAdapter(requireContext(), emptyList(), emptyMap())
        expandableListView.setAdapter(expandableListAdapter)

        // Handle submitting items
        view.findViewById<Button>(R.id.buttonAdd).setOnClickListener {
            if (isNotEmptyEditText()) {
                addItemToList(expandableListView)
            }
        }

        // Handle submitting items
        view.findViewById<Button>(R.id.button_save).setOnClickListener {
            Log.e(TAG, "listViewInitialize: $inventoryList", )
            submitData()
        }


    }

    private fun isNotEmptyEditText(): Boolean {
        var isValid = true
        if (editTextQuantity.text.toString().trim().isEmpty()) {
            editTextQuantity.error = "Quantity is required"
            isValid = false
        }
        if (editTextSellingPrice.text.toString().trim().isEmpty()) {
            editTextSellingPrice.error = "Selling Price is required"
            isValid = false
        }
        return isValid
    }


    private fun addItemToList(listView: ExpandableListView) {
        val name = spinnerBrandName.selectedItem.toString()
        val model = spinnerModel.selectedItem.toString()
        val selectedVariantIndex = spinnerVariant.selectedItemPosition
        val variantId = variantList[selectedVariantIndex].id
        val color = spinnerColor.selectedItem.toString()
        val condition = spinnerCondition.selectedItem.toString()
        val purchasePrice = editTextPurchasePrice.text.toString().toDoubleOrNull() ?: 0.0
        val sellingPrice = editTextSellingPrice.text.toString().toDoubleOrNull() ?: 0.0
        val quantity = editTextQuantity.text.toString().toIntOrNull() ?: 0
        val notes = editTextDescription.text.toString()

        val newItem = InventoryItem(
            name, model, variantId, color, condition, purchasePrice, sellingPrice, quantity, notes
        )

        // Check for duplicates and increase quantity if exists
        val existingItem = inventoryList.find {
            it.brand == name && it.model == model && it.variant == variantId && it.color == color
        }
        if (existingItem != null) {
            existingItem.quantity += quantity
        } else {
            inventoryList.add(newItem)
        }

        val expandableListTitle = inventoryList.map { it.brand }.distinct()
        val expandableListDetail = inventoryList.groupBy { it.brand }

        Log.e(TAG, "addItemToList: $expandableListTitle $expandableListDetail")

        val expandableListAdapter = InventoryExpandableListAdapter(
            requireContext(), expandableListTitle, expandableListDetail
        )
        listView.setAdapter(expandableListAdapter)

    }

    private fun   submitData() {
        // Convert the list of items to the required API structure
        Log.e(TAG, "submitData: $inventoryList")


        showProgressBar()
        RetrofitInstance.getApiInterface().addNewInventoryItem(inventoryList)
            .enqueue(object : Callback<ResponseBody>{
                override fun onResponse(
                    call: Call<ResponseBody>,
                    response: Response<ResponseBody>
                ) {
                    if (response.isSuccessful){
                        Log.e(TAG, "onResponse: ${response.body()}  ${response.code()}", )
                        hideProgressBar()

                    }
                    else
                        Log.e(TAG, "onResponseFail: ${response.body()}  ${response.code()}", )
                }

                override fun onFailure(call: Call<ResponseBody>, t: Throwable) {
                    Log.e(TAG, "onFailure: ${t.message}", )
                }

            })


    }

    private fun intialization(view: View) {
        // Initialize Views
        spinnerBrandName = view.findViewById(R.id.spinner_brand_name)
        spinnerModel = view.findViewById(R.id.spinner_model)
        spinnerVariant = view.findViewById(R.id.spinner_variant)
        spinnerColor = view.findViewById(R.id.spinner_color)
        spinnerCondition = view.findViewById(R.id.spinner_condition)
        editTextDescription = view.findViewById(R.id.edittext_description)
        editTextSellingPrice = view.findViewById(R.id.edittext_price)
        editTextPurchasePrice = view.findViewById(R.id.edittext_purchase_price)
        editTextQuantity = view.findViewById(R.id.edittext_quantity)
        textViewTotalPrice = view.findViewById(R.id.text_total_price)
        buttonSave = view.findViewById(R.id.button_save)
        progressBar = view.findViewById(R.id.progressBar)

//        buttonSave.setOnClickListener {
//
//
//            showProgressBar()
//            val quantity = Integer.parseInt(editTextQuantity.text.toString())
//
//            val barCodeList = generateUniqueBarcodes(quantity)
//            Log.e("Test", "list $barCodeList")
//            saveBarcodesToExcel(barCodeList)
//
//        }


        val conditionAdapter = ArrayAdapter(
            requireContext(), android.R.layout.simple_spinner_dropdown_item, conditionList
        )
        conditionAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        spinnerCondition.adapter = conditionAdapter


        loadBrandNames()

        // Handle Price and Quantity changes to calculate Total Price
        val totalPriceWatcher = object : TextWatcher {
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}

            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                calculateTotalPrice()
            }

            override fun afterTextChanged(s: Editable?) {}
        }

        editTextSellingPrice.addTextChangedListener(totalPriceWatcher)
        editTextQuantity.addTextChangedListener(totalPriceWatcher)

    }

    private fun createExcelFile(fileName: String, barCodeList: List<String>): File {
        val workbook: Workbook = XSSFWorkbook()
        val sheet: Sheet = workbook.createSheet("Sheet1")
        val font: Font = workbook.createFont().apply {
            fontName =
                "IDAutomationHC39M Free Version"  // Set the custom font name (must be installed on the device)
            fontHeightInPoints = 12
            italic = true
            // Set font size
        }

        val nameCellStyle: CellStyle = workbook.createCellStyle().apply {
            setFont(font)
        }

        // Create header row
        val headerRow: Row = sheet.createRow(0)
        headerRow.createCell(0).setCellValue("BarcodeValue")
        headerRow.createCell(1).setCellValue("BarCode")


        // Add barcode data rows
        for ((index, barcode) in barCodeList.withIndex()) {
            val row: Row = sheet.createRow(index + 1)  // Start from row 1 (after header)
            row.createCell(0).setCellValue(barcode)
            val cell: Cell = row.createCell(1)
            cell.setCellValue(barcode)
            cell.cellStyle = nameCellStyle

        }

        // Save the file
        val file = File(fileName)
        val fileOut = FileOutputStream(file)
        workbook.write(fileOut)
        fileOut.close()
        workbook.close()

        return file
    }

    private fun saveBarcodesToExcel(
        barCodeList: List<String>
    ) { // Example quantity of barcodes

        val fileName = "barcodes.xlsx"
        val directory = File(
            Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS), "MyApp"
        )
        if (!directory.exists()) {
            directory.mkdirs()
        }

        val file = File(directory, fileName)
        createExcelFile(file.absolutePath, barCodeList)

        // Notify the user
        println("Excel file with barcodes saved at: ${file.absolutePath}")
    }

    private fun Fragment?.runOnUiThread(action: () -> Unit) {
        this ?: return
        if (!isAdded) return // Fragment not attached to an Activity
        activity?.runOnUiThread(action)
    }

    // Function to show the ProgressBar
    private fun showProgressBar() {
        runOnUiThread {
            progressBar.visibility = View.VISIBLE
            buttonSave.isEnabled = false

        }
    }

    // Function to hide the ProgressBar
    private fun hideProgressBar() {
        runOnUiThread {
            progressBar.visibility = View.GONE
            buttonSave.isEnabled = true

        }
    }


    private fun generateUniqueBarcodes(quantity: Int): List<String> {
        val barCodeSet = mutableSetOf<String>()

        while (barCodeSet.size < quantity) {
            barCodeSet.add(generateSerialNumber())
        }

        return barCodeSet.toList()
    }

    private fun generateSerialNumber(): String {
        val lettersUppercase = ('A'..'Z').toList()
        val digits = ('0'..'9').toList()

        val random = Random(System.currentTimeMillis())

        // Generate the first part (ABC)
        val part1 = (1..3).map { lettersUppercase.random(random) }.joinToString("")

        // Generate the second part (123456789)
        val part2 = (1..9).map { digits.random(random) }.joinToString("")

        // Combine both parts
        return "$part1$part2"
    }

    // Usage


    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.context = context
    }

    private fun loadBrandNames() {
        RetrofitInstance.getApiInterface().getBrandNames()
            .enqueue(object : Callback<BrandNamesResponse> {
                override fun onResponse(
                    call: Call<BrandNamesResponse>, response: Response<BrandNamesResponse>
                ) {
                    Log.e(TAG, "onResponse: ${response.code()}")
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        if (responseBody != null && responseBody.success) {
                            val brandList = responseBody.data

                            // Create and set up the adapter for the spinner
                            val brandAdapter = ArrayAdapter(
                                requireContext(),
                                android.R.layout.simple_spinner_dropdown_item,
                                brandList
                            )
                            brandAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                            spinnerBrandName.adapter = brandAdapter

                            // Set up an item selected listener to load models based on the selected brand
                            spinnerBrandName.onItemSelectedListener =
                                object : AdapterView.OnItemSelectedListener {
                                    override fun onItemSelected(
                                        parent: AdapterView<*>, view: View?, position: Int, id: Long
                                    ) {
                                        loadModels(brandList[position]) // Call a function to load models based on the selected brand
                                    }

                                    override fun onNothingSelected(parent: AdapterView<*>) {}
                                }
                        } else {
                            Log.e(TAG, "onResponse: Unsuccessful response or data field is empty")
                            Toast.makeText(
                                requireContext(), "Failed to load brands", Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }

                override fun onFailure(call: Call<BrandNamesResponse>, t: Throwable) {
                    // Handle API error
                    Log.e(TAG, "onFailure: ${t.message}")
                    Toast.makeText(requireContext(), "Failed to load brands", Toast.LENGTH_SHORT)
                        .show()
                }

            })

    }

    private fun loadModels(brandName: String) {

        RetrofitInstance.getApiInterface().getModels(brandName)
            .enqueue(object : Callback<BrandNamesResponse> {
                override fun onResponse(
                    call: Call<BrandNamesResponse>, response: Response<BrandNamesResponse>
                ) {
                    Log.e(TAG, "onResponse: ${response.code()}")
                    if (response.isSuccessful) {
                        val responseBody = response.body()

                        if (responseBody != null && responseBody.success) {
                            val modelList = responseBody.data

                            Log.e(TAG, "onResponse: ${responseBody.data}")

                            if (responseBody.data.isNotEmpty()) {
                                val modelAdapter = ArrayAdapter(
                                    requireContext(),
                                    android.R.layout.simple_spinner_dropdown_item,
                                    responseBody.data
                                )
                                modelAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                                spinnerModel.adapter = modelAdapter

                                // Load variants and colors based on selected model
                                spinnerModel.onItemSelectedListener =
                                    object : AdapterView.OnItemSelectedListener {
                                        override fun onItemSelected(
                                            parent: AdapterView<*>,
                                            view: View?,
                                            position: Int,
                                            id: Long
                                        ) {
                                            loadVariantsAndColors(brandName, modelList[position])
                                        }

                                        override fun onNothingSelected(parent: AdapterView<*>) {}
                                    }
                            }
                        }

                    }
                }

                override fun onFailure(call: Call<BrandNamesResponse>, t: Throwable) {
                    Log.e(TAG, "onFailure: ${t.printStackTrace()}")
                    // Handle API error
                    Toast.makeText(requireContext(), "Failed to load models", Toast.LENGTH_SHORT)
                        .show()
                }
            })
    }


    private fun loadVariantsAndColors(brandName: String, modelName: String) {

        RetrofitInstance.getApiInterface().getVariantsAndColors(brandName, modelName)
            .enqueue(object : Callback<VariantsAndColorsResponse> {
                override fun onResponse(
                    call: Call<VariantsAndColorsResponse>,
                    response: Response<VariantsAndColorsResponse>
                ) {
                    Log.e(TAG, "onResponse: ${response.code()}")
                    if (response.isSuccessful) {
                        val responseBody = response.body()
                        if (responseBody != null && responseBody.success) {
                            Log.e(TAG, "onResponse: ${responseBody.data}")


                            val colorList = responseBody.data.color
                            variantList = responseBody.data.variants.map {
                                VariantWithID(it._id, "${it.ram}GB RAM / ${it.rom}GB ROM")
                            }
                     
                            val variantAdapter = ArrayAdapter(
                                requireContext(),
                                android.R.layout.simple_spinner_dropdown_item,
                                variantList.map { it.ramRom }
                            )
                            variantAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                            spinnerVariant.adapter = variantAdapter

                            // Load variants and colors based on selected model
                            // Set Color Spinner with dummy data

                            val colorAdapter = ArrayAdapter(
                                requireContext(),
                                android.R.layout.simple_spinner_dropdown_item,
                                colorList
                            )
                            colorAdapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
                            spinnerColor.adapter = colorAdapter
                        }

                    }
                }

                override fun onFailure(call: Call<VariantsAndColorsResponse>, t: Throwable) {
                    // Handle API error
                    Toast.makeText(requireContext(), "Failed to load Variants", Toast.LENGTH_SHORT)
                        .show()
                }
            })
    }

    val charPool: List<Char> = ('a'..'z') + ('A'..'Z') + ('0'..'9')
    fun randomStringByKotlinRandom() =
        (1..10).map { Random.nextInt(0, charPool.size).let { charPool[it] } }.joinToString("")

    private fun calculateTotalPrice() {
        val priceText = editTextSellingPrice.text.toString()
        val quantityText = editTextQuantity.text.toString()

        val price = priceText.toDoubleOrNull() ?: 0.0
        val quantity = quantityText.toIntOrNull() ?: 0

        val totalPrice = price * quantity
        "Total Price: ₹$totalPrice".also { textViewTotalPrice.text = it }
    }

    data class VariantWithID(
        val id: String,
        val ramRom: String
    )

}

