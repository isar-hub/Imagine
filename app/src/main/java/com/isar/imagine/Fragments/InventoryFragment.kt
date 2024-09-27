package com.isar.imagine.Fragments

import android.content.Context
import android.os.Bundle
import android.os.Environment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.google.firebase.firestore.FirebaseFirestore
import com.isar.imagine.Adapters.InventoryExpandableListAdapter
import com.isar.imagine.data.model.InventoryItem
import com.isar.imagine.databinding.FragmentInventory2Binding
import com.isar.imagine.inventory.InventoryRepositoryImpl
import com.isar.imagine.inventory.InventoryViewModel
import com.isar.imagine.inventory.MobileViewModelFactory
import com.isar.imagine.inventory.models.DataClass
import org.apache.poi.ss.usermodel.Cell
import org.apache.poi.ss.usermodel.CellStyle
import org.apache.poi.ss.usermodel.Font
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileOutputStream

/**
 * RoadMap :
 * start -> get all brands(getting in init viewmodel) -> add to brand dropdown -> user select brand -> get allow models -> user select model ->
 * -> get all variants -> user select variant -> get all input also -> user click on add button -> add item to expandable list -> give option to select more ->
 * -> user click save -> generate random number -> check all random -> ?yes -> generate excel with id -> push each data of list to backend seprately
 */


//left to implement post data
class InventoryFragment : Fragment() {

    private lateinit var context: Context
    private lateinit var variantList: List<VariantWithID>
    private lateinit var inventoryList: MutableList<InventoryItem>
    private val TAG = "InventoryFragment"
    private val conditionList = listOf("Good", "Best", "Bad")

    //
    private lateinit var viewModel: InventoryViewModel
    private lateinit var binding: FragmentInventory2Binding

    private var selectedBrand: DataClass.Brand? = null
    private var selectedModel: DataClass.Model? = null

    //
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?, savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        binding = FragmentInventory2Binding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)


        val repository = InventoryRepositoryImpl(FirebaseFirestore.getInstance())
        val factory = MobileViewModelFactory(repository)
        viewModel = factory.create(InventoryViewModel::class.java)



        inventoryList = mutableListOf()


        //
        calculateTotalPrice()
        onSaveClick()
        addItemToExpandableList()
        observeFireBaseData()
        //
    }

    private fun onSaveClick() {

        viewModel.generatedBarcodes.observe(viewLifecycleOwner) { barcodes ->
            // Call the save method when new barcodes are generated
            if (barcodes.isNotEmpty()) {
                saveBarcodesToExcel(barcodes)
            }
        }
        showProgressBar()
        val quantity = Integer.parseInt(binding.edittextQuantity.text.toString())
        viewModel.generateUniqueBarcodes(quantity)
    }


//////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    // on click add button check required item is not empty
    private fun addItemToExpandableList() {
        binding.buttonAdd.setOnClickListener {
            if (isNotEmptyEditText()) {
                //adding item to list
                addItemToList()
            }
        }
    }


    //get brand model variant  data and show in dropdown
    private fun observeFireBaseData() {


        viewModel.brands.observe(viewLifecycleOwner) { brands ->
            if (brands.isNotEmpty()) {
                populateBrandSpinner(brands)
            }
        }

        // Observe models
        viewModel.models.observe(viewLifecycleOwner) { models ->
            if (models.isNotEmpty()) {
                binding.spinnerModel.visibility = View.VISIBLE
                populateModelSpinner(models)
            } else {
                binding.spinnerModel.visibility = View.GONE
            }
        }

        // Observe variants
        viewModel.variants.observe(viewLifecycleOwner) { variants ->
            if (variants.isNotEmpty()) {
                binding.spinnerVariant.visibility = View.VISIBLE
                populateVariantSpinner(variants)
            } else {
                binding.spinnerVariant.visibility = View.GONE
            }
        }

        // Observe errors
        viewModel.error.observe(viewLifecycleOwner) { errorMessage ->
            Toast.makeText(requireContext(), errorMessage, Toast.LENGTH_LONG).show()
        }


    }

    private fun populateBrandSpinner(brands: List<DataClass.Brand>) {
        val brandNames = brands.map { it.name }
        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, brandNames)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerBrandName.adapter = adapter


        binding.spinnerBrandName.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {
                    // Do nothing
                }

                override fun onItemSelected(
                    parent: AdapterView<*>?, view: View?, position: Int, id: Long
                ) {
                    selectedBrand = brands[position]
                    // Fetch models for selected brand
                    viewModel.fetchModels(selectedBrand!!.id)
                    // Reset downstream selections
                    binding.spinnerModel.adapter = null
                    binding.spinnerModel.visibility = View.GONE
                    binding.spinnerVariant.adapter = null
                    binding.spinnerVariant.visibility = View.GONE
                }
            }
    }

    private fun populateModelSpinner(models: List<DataClass.Model>?) {
        val modelNames = models?.map { it.name }
        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, modelNames!!)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerModel.adapter = adapter

        binding.spinnerModel.onItemSelectedListener = object : AdapterView.OnItemSelectedListener {
            override fun onNothingSelected(parent: AdapterView<*>?) {
                // Do nothing
            }

            override fun onItemSelected(
                parent: AdapterView<*>?, view: View?, position: Int, id: Long
            ) {
                selectedModel = models.get(position)
                // Fetch variants for selected model
                viewModel.fetchVariants(selectedBrand!!.id, selectedModel!!.id)
                // Reset downstream selections
                binding.spinnerVariant.adapter = null
                binding.spinnerVariant.visibility = View.GONE

            }
        }
    }


    private fun populateVariantSpinner(variants: List<DataClass.Variant>?) {
        val variantNames = variants?.map { "${it.ram} - ${it.rom} - ${it.color}" }
        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, variantNames!!)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerVariant.adapter = adapter

        binding.spinnerVariant.onItemSelectedListener =
            object : AdapterView.OnItemSelectedListener {
                override fun onNothingSelected(parent: AdapterView<*>?) {

                }

                override fun onItemSelected(
                    parent: AdapterView<*>?, view: View?, position: Int, id: Long
                ) {
//                selectedVariant = variants[position]
//                displayVariantDetails(selectedVariant!!)
                }
            }
    }


    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    //check if quantity and selling price is not empty
    private fun isNotEmptyEditText(): Boolean {
        var isValid = true
        if (binding.edittextQuantity.text.toString().trim().isEmpty()) {
            binding.edittextQuantity.error = "Quantity is required"
            isValid = false
        }
        if (binding.edittextPrice.text.toString().trim().isEmpty()) {
            binding.edittextPrice.error = "Selling Price is required"
            isValid = false
        }
        return isValid
    }


    private fun addItemToList() {

        // observing inventory list to set in expandable list
        viewModel.inventoryList.observe(viewLifecycleOwner) { inventoryList ->
            updateExpandableListAdapter(inventoryList)
        }

        //getting item from input fields
        val name = binding.spinnerBrandName.selectedItem.toString()
        val model = binding.spinnerModel.selectedItem.toString()
        val variant = binding.spinnerVariant.selectedItemPosition.toString()
        val condition = binding.spinnerCondition.selectedItem.toString()
        val purchasePrice = binding.edittextPurchasePrice.text.toString().toDoubleOrNull() ?: 0.0
        val sellingPrice = binding.edittextPrice.text.toString().toDoubleOrNull() ?: 0.0
        val quantity = binding.edittextQuantity.text.toString().toIntOrNull() ?: 0
        val notes = binding.notes.text.toString()

        // add item to list
        viewModel.addItem(
            name, model, variant, condition, purchasePrice, sellingPrice, quantity, notes
        )

    }

    // sending data to expandable list adapter
    private fun updateExpandableListAdapter(inventoryList: List<InventoryItem>) {

        //separating tittle and details to show it
        val expandableListTitle = inventoryList.map { it.brand }.distinct()
        val expandableListDetail = inventoryList.groupBy { it.brand }

        // adapter of recyclerview
        val expandableListAdapter = InventoryExpandableListAdapter(
            requireContext(), expandableListTitle, expandableListDetail
        )
        binding.listView.setAdapter(expandableListAdapter)
    }


    private fun createExcelFile(fileName: String, barCodeList: List<String>): File {
        val workbook: Workbook = XSSFWorkbook()
        val sheet: Sheet = workbook.createSheet("Sheet1")
        val font: Font = workbook.createFont().apply {
            fontName =
                "IDAutomationHC39M Free Version"  // Set the custom font name (must be installed on the device)
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
        hideProgressBar()

        // Notify the user
        println("Excel file with barcodes saved at: ${file.absolutePath}")
    }


    // Function to show the ProgressBar
    private fun showProgressBar() {
        binding.progressBar.visibility = View.VISIBLE
        binding.buttonSave.isEnabled = false
    }

    // Function to hide the ProgressBar
    private fun hideProgressBar() {
        binding.progressBar.visibility = View.GONE
        binding.buttonSave.isEnabled = true
    }


    override fun onAttach(context: Context) {
        super.onAttach(context)
        this.context = context
    }


    private fun calculateTotalPrice() {
        val priceText = binding.edittextPrice.text.toString()
        val quantityText = binding.edittextQuantity.text.toString()

        val price = priceText.toDoubleOrNull() ?: 0.0
        val quantity = quantityText.toIntOrNull() ?: 0

        val totalPrice = price * quantity
        "Total Price: ₹$totalPrice".also { binding.textTotalPrice.text = it }
    }

    data class VariantWithID(
        val id: String, val ramRom: String
    )

}

