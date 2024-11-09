package com.isar.imagine.inventory

import android.app.ProgressDialog
import android.os.Bundle
import android.os.Environment
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.AdapterView
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.viewModelScope
import androidx.navigation.NavController
import androidx.navigation.fragment.NavHostFragment
import com.google.android.material.snackbar.Snackbar
import com.google.firebase.firestore.FirebaseFirestore
import com.isar.imagine.Adapters.InventoryExpandableListAdapter
import com.isar.imagine.R
import com.isar.imagine.data.model.InventoryItem
import com.isar.imagine.databinding.FragmentInventory2Binding
import com.isar.imagine.inventory.models.DataClass
import com.isar.imagine.utils.CommonMethods
import com.isar.imagine.utils.CustomDialog
import com.isar.imagine.utils.CustomProgressBar
import com.isar.imagine.utils.Results
import com.isar.imagine.utils.getTextView
import kotlinx.coroutines.launch
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


    //
    private lateinit var viewModel: InventoryViewModel
    private lateinit var binding: FragmentInventory2Binding

    private var selectedBrand: String? = null
    private var selectedModel: String? = null
    private lateinit var progressDialog: ProgressDialog

    var items: MutableList<DataClass.InventoryData> = mutableListOf()

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
        // add item to list

        progressDialog = ProgressDialog(context)


        getUserInput()
        addItem()
        saveData()


    }

    private fun getUserInput(){

        observeFireBaseData()
        populateConditionSpinner()



    }
    private fun addItem(){
        addItemToExpandableList()
        calculateTotalPrice()


    }

    private fun saveData(){
        binding.submit.setOnClickListener{

            onSaveClick()
        }

    }


    private  fun onSaveClick() {

        viewModel.viewModelScope.launch {
            viewModel.onSave {
                if (it) {
                    CustomDialog.showAlertDialog(
                        requireContext(),
                        requireContext().getTextView("Successfull Done"),
                        "Sucess",{
                            NavHostFragment.findNavController(requireParentFragment()).navigateUp()
                        },{
                            NavHostFragment.findNavController(requireParentFragment()).navigateUp()
                        }
                    )
                } else {
                    CustomDialog.showAlertDialog(
                        requireContext(),
                        requireContext().getTextView("Error"),
                        "Error"
                    )
                }
            }

        }
        viewModel.inventoryListFinal.observe(viewLifecycleOwner){ items->
            Log.e("itemFinal","List final is $items")
        }
//        viewModel.inventoryList.value?.forEach{ _item ->
//            viewModel.generateUniqueBarcodes()
//
//
//        }
////
////        viewModel.generatedBarcodes.observe(viewLifecycleOwner) { barcodes ->
////            // Call the save method when new barcodes are generated
////            if (barcodes.isNotEmpty()) {
////                saveBarcodesToExcel(barcodes)
////
////
////
////
////            }
////        }
//
//        val quantity = binding.edittextQuantity.text.toString()
//        if (quantity.isNotBlank()){
//            viewModel.generateUniqueBarcodes(Integer.parseInt(quantity))
//        }
    }



    // on click add button check required item is not empty
    private fun addItemToExpandableList() {

        viewModel.inventoryList.observe(viewLifecycleOwner) { inventoryList ->
            updateExpandableListAdapter(inventoryList)
        }
        binding.listView.setOnGroupExpandListener { value->
            Log.e("groupTag","is Clicked $value")
        }
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
            when(brands){
                is Results.Loading ->{
                    CustomProgressBar.show(requireContext(),"Loadin Branda")
                    CommonMethods.showLogs("Success","Brand loading")
                }
                is Results.Success-> {
                    CustomProgressBar.dismiss()
                    CommonMethods.showLogs("Success","Brand successfull")
                    if (!brands.data.isNullOrEmpty()){
                        populateBrandSpinner(brands.data)
                    }
                }
                is Results.Error ->{
                    CustomProgressBar.dismiss()
                    CommonMethods.showLogs("Success","Brand error")
                    Toast.makeText(context, "Error in fetching brands ${brands.data}",Toast.LENGTH_LONG).show()
                }
            }


        }

        // Observe models
        viewModel.models.observe(viewLifecycleOwner) { models ->
            when(models){
                is Results.Loading ->{
                    CustomProgressBar.show(requireContext(),"Loading Variants")
                }
                is Results.Success-> {
                    CustomProgressBar.dismiss()
                    if (!models.data.isNullOrEmpty()){
                        populateModelSpinner(models.data)
                    }
                }
                is Results.Error ->{
                    CustomProgressBar.dismiss()
                    Toast.makeText(context, "Error in fetching brands ${models.data}",Toast.LENGTH_LONG).show()
                }
            }

        }

        viewModel.variants.observe(viewLifecycleOwner) { variants ->
            when(variants){
                is Results.Loading ->{
                    showDialog("Loading Variants")
                }
                is Results.Success-> {
                    progressDialog.hide()
                    if (!variants.data.isNullOrEmpty()){
                        populateVariantSpinner(variants.data)
                    }
                }
                is Results.Error ->{
                    progressDialog.hide()
                    Toast.makeText(context, "Error in fetching brands ${variants.data}",Toast.LENGTH_LONG).show()
                }
            }

        }





    }
    private fun showDialog(text :String ){
        progressDialog.setMessage(text);
        progressDialog.show()
    }

    private fun populateBrandSpinner(brands: List<String>) {

        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, brands)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerBrandName.setAdapter(adapter)

        binding.spinnerBrandName.setOnFocusChangeListener { _, _ ->
            binding.spinnerBrandName.showDropDown()
        }
        binding.spinnerBrandName.setOnItemClickListener { _, _, position, _ ->
            selectedBrand = brands[position]
            // Fetch models for selected brand
            viewModel.fetchModels(selectedBrand!!)
            binding.spinnerModel.setAdapter(null)
            binding.spinnerVariant.setAdapter(null)
        }
    }

    private fun populateModelSpinner(models: List<String>) {

        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, models)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerModel.setAdapter(adapter)
        binding.spinnerModel.setOnFocusChangeListener { _, _ ->
            binding.spinnerModel.showDropDown()
        }
        binding.spinnerModel.setOnItemClickListener { _, _, position, _ ->
            selectedModel = models[position]
            // Fetch variants for selected model
            viewModel.fetchVariants(selectedBrand!!,selectedModel!!)
            binding.spinnerVariant.setAdapter(null)
        }
    }
    private fun populateConditionSpinner() {
        val condition = listOf("Silver","Gold","Platinum")
        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, condition)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCondition.adapter = adapter

    }


    private fun populateVariantSpinner(variants: List<String>) {

        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, variants)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerVariant.setAdapter(adapter)
        binding.spinnerVariant.setOnClickListener { _, ->
            binding.spinnerVariant.showDropDown()
        }

    }



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

        //getting item from input fields
        val name = binding.spinnerBrandName.text.toString()
        val model = binding.spinnerModel.text.toString()
        val variant = binding.spinnerVariant.text.toString()
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

        Log.e("getGroupCount", "Titles: $expandableListTitle")
        Log.e("getGroupCount", "Details: $expandableListDetail")
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
//        hideProgressBar()

        // Notify the user
        println("Excel file with barcodes saved at: ${file.absolutePath}")
    }


    // Function to show the ProgressBar
//    private fun showProgressBar() {
//        binding.progressBar.visibility = View.VISIBLE
//        binding.buttonSave.isEnabled = false
//    }
//
//    // Function to hide the ProgressBar
//    private fun hideProgressBar() {
//        binding.progressBar.visibility = View.GONE
//        binding.buttonSave.isEnabled = true
//    }


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
