package com.isar.imagine.inventory.view

import android.app.ProgressDialog
import android.os.Bundle
import android.os.Environment
import android.os.StrictMode
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ArrayAdapter
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.lifecycle.viewModelScope
import androidx.navigation.fragment.NavHostFragment
import com.bumptech.glide.Glide
import com.google.firebase.firestore.FirebaseFirestore
import com.isar.imagine.billing.InventoryItem
import com.isar.imagine.databinding.FragmentInventory2Binding
import com.isar.imagine.inventory.data.InventoryData
import com.isar.imagine.inventory.data.repos.InventoryRepositoryImpl
import com.isar.imagine.inventory.view.adapters.InventoryExpandableListAdapter
import com.isar.imagine.inventory.viewmodel.InventoryViewModel
import com.isar.imagine.inventory.viewmodel.MobileViewModelFactory
import com.isar.imagine.utils.CommonMethods
import com.isar.imagine.utils.CustomDialog
import com.isar.imagine.utils.CustomProgressBar
import com.isar.imagine.utils.Results
import com.isar.imagine.utils.getTextView
import kotlinx.coroutines.launch
import java.io.File


class InventoryFragment : Fragment() {


    private lateinit var viewModel: InventoryViewModel
    private lateinit var binding: FragmentInventory2Binding

    private var selectedBrand: String? = null
    private var selectedModel: String? = null
    private lateinit var progressDialog: ProgressDialog

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
        progressDialog = ProgressDialog(context)


        val policy = StrictMode.ThreadPolicy.Builder().permitAll().build()
        StrictMode.setThreadPolicy(policy)

        getUserInput()
        addItem()
        saveData()


    }

    fun barCodeProcess() {
        //
//        CommonMethods.showLogs("INVENTORY","ARE Not Pulled PULLED ${BarCodeArray.arrBarcode.size}")
//        CommonMethods.showLogs("INVENTORY","ARE UNIQUE ${BarCodeArray.areAllItemsUnique()}")
//        val list = BarCodeArray.pullFirstNItems(2)
//        CommonMethods.showLogs("INVENTORY","ARE PULLED ${list}")
//        CommonMethods.showLogs("INVENTORY","ARE PULLED ${BarCodeArray.arrBarcode.size}")

//        val list = BarCodeArray.arrBarcode
//        BarCodeArray.pushToFirebase(list)
    }

    private fun getUserInput() {

        observeFireBaseData()
        populateConditionSpinner()

    }

    private fun addItem() {
        addItemToExpandableList()
        calculateTotalPrice()
    }

    private fun saveData() {
        binding.submit.setOnClickListener {
            binding.submit.isEnabled = false
            CustomProgressBar.show(requireContext(), "Loading...")
            onSaveClick()
        }
    }


    private fun onSaveClick() {

        CommonMethods.showLogs("INVENTORY", "Started on save click")
        viewModel.viewModelScope.launch {
            viewModel.onSave(requireContext()) {
                if (it) {
                    CustomProgressBar.dismiss()
                    showSuccessDialog()
                } else {
                    CustomProgressBar.dismiss()
                    CustomDialog.showAlertDialog(
                        requireContext(), requireContext().getTextView("Error"), "Error"
                    )
                }
            }

        }
        viewModel.inventoryListFinal.observe(viewLifecycleOwner) { items ->
            Log.e("itemFinal", "List final is $items")
        }

    }

    private fun showSuccessDialog() {
        CustomDialog.showAlertDialog(
            requireContext(),
            requireContext().getTextView("Successfully Done"),
            "Success",
            {
                NavHostFragment.findNavController(requireParentFragment()).navigateUp()
            },
            {
                NavHostFragment.findNavController(requireParentFragment()).navigateUp()

            })
    }

    // on click add button check required item is not empty
    private fun addItemToExpandableList() {


        binding.listView.setOnGroupExpandListener { value ->
            Log.e("groupTag", "is Clicked $value")
        }
        binding.buttonAdd.setOnClickListener {
            if (isNotEmptyEditText()) {
                addItemToList()
            }
        }
    }


    //get brand model variant  data and show in dropdown
    private fun observeFireBaseData() {
        viewModel.inventoryList.observe(viewLifecycleOwner) { inventoryList ->
            CommonMethods.showLogs("TAG", "inventory list $inventoryList")
            if (inventoryList.isEmpty()) {
                binding.listView.visibility = View.GONE
                binding.emptyView.visibility = View.VISIBLE
            } else {
                updateExpandableListAdapter(inventoryList)
                binding.listView.visibility = View.VISIBLE
                binding.emptyView.visibility = View.GONE
            }
        }
        viewModel.brands.observe(viewLifecycleOwner) { brands ->
            when (brands) {
                is Results.Loading -> {
                    CustomProgressBar.show(requireContext(), "Loadin Branda")
                    CommonMethods.showLogs("Success", "Brand loading")
                }

                is Results.Success -> {
                    CustomProgressBar.dismiss()
                    CommonMethods.showLogs("Success", "Brand successfull")
                    if (!brands.data.isNullOrEmpty()) {
                        populateBrandSpinner(brands.data)
                    }
                }

                is Results.Error -> {
                    CustomProgressBar.dismiss()
                    CommonMethods.showLogs("Success", "Brand error")
                    Toast.makeText(
                        context, "Error in fetching brands ${brands.data}", Toast.LENGTH_LONG
                    ).show()
                }
            }


        }

        // Observe models
        viewModel.models.observe(viewLifecycleOwner) { models ->
            when (models) {
                is Results.Loading -> {
                    CustomProgressBar.show(requireContext(), "Loading Variants")
                }

                is Results.Success -> {
                    CustomProgressBar.dismiss()
                    if (!models.data.isNullOrEmpty()) {
                        populateModelSpinner(models.data)
                    }
                }

                is Results.Error -> {
                    CustomProgressBar.dismiss()
                    Toast.makeText(
                        context, "Error in fetching brands ${models.data}", Toast.LENGTH_LONG
                    ).show()
                }
            }

        }

        viewModel.image.observe(viewLifecycleOwner) { image ->
            when (image) {
                is Results.Error -> TODO()
                is Results.Loading -> {}
                is Results.Success -> getImage(image.data!!)
            }

        }
        viewModel.variants.observe(viewLifecycleOwner) { variants ->
            when (variants) {
                is Results.Loading -> {
                    showDialog("Loading Variants")
                }

                is Results.Success -> {
                    progressDialog.hide()
                    if (!variants.data.isNullOrEmpty()) {
                        populateVariantSpinner(variants.data)
                    }
                }

                is Results.Error -> {
                    progressDialog.hide()
                    Toast.makeText(
                        context, "Error in fetching brands ${variants.data}", Toast.LENGTH_LONG
                    ).show()
                }
            }

        }


    }

    private fun showDialog(text: String) {
        progressDialog.setMessage(text)
        progressDialog.show()
    }

    private fun populateBrandSpinner(brands: List<String>) {

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, brands)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerBrandName.setAdapter(adapter)

        binding.spinnerBrandName.setOnFocusChangeListener { _, _ ->
            binding.spinnerBrandName.showDropDown()
        }
        binding.spinnerBrandName.setOnClickListener {
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

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, models)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerModel.setAdapter(adapter)
        binding.spinnerModel.setOnFocusChangeListener { _, _ ->
            binding.spinnerModel.showDropDown()
        }
        binding.spinnerModel.setOnClickListener {
            binding.spinnerModel.showDropDown()
        }
        binding.spinnerModel.setOnItemClickListener { _, _, position, _ ->
            selectedModel = models[position]
            // Fetch variants for selected model
            viewModel.fetchVariants(selectedBrand!!, selectedModel!!)
            viewModel.fetchImage(selectedBrand!!, selectedModel!!)
            binding.spinnerVariant.setAdapter(null)
        }
    }

    private fun populateConditionSpinner() {
        val condition = listOf("Silver", "Gold", "Platinum")
        val adapter =
            ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, condition)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerCondition.adapter = adapter

    }


    private fun populateVariantSpinner(variants: List<String>) {

        val adapter = ArrayAdapter(requireContext(), android.R.layout.simple_spinner_item, variants)
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item)
        binding.spinnerVariant.setAdapter(adapter)
        binding.spinnerVariant.setOnFocusChangeListener { _, _ ->
            binding.spinnerVariant.showDropDown()
        }
        binding.spinnerVariant.setOnClickListener { _ ->
            binding.spinnerVariant.showDropDown()
        }


    }

    private fun getImage(image: String) {
        try {
            Glide.with(requireContext()).load(image).into(binding.productImage)
        } catch (e: Exception) {
            Log.e("Image", "Error in loading image ${e.message}")
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
        val brand = binding.spinnerBrandName.text.toString()
        val model = binding.spinnerModel.text.toString()
        val variant = binding.spinnerVariant.text.toString()
        val condition = binding.spinnerCondition.selectedItem.toString()
        val imei1 = binding.imeiOne.text.toString()
        val imei2 = binding.imeiTwo.text.toString()
        val purchasePrice = binding.edittextPurchasePrice.text.toString().toDoubleOrNull() ?: 0.0
        val sellingPrice = binding.edittextPrice.text.toString().toDoubleOrNull() ?: 0.0
        val quantity = binding.edittextQuantity.text.toString().toIntOrNull() ?: 0
        val notes = binding.notes.text.toString()

        // add item to list
        viewModel.addItem(
            brand = brand,
            model = model,
            variant = variant,
            condition = condition,
            imei1 = imei1,
            imei2 = imei2,
            purchasePrice = purchasePrice,
            sellingPrice = sellingPrice,
            quantity = quantity,
            notes = notes
        )

        clearForm()
    }

    // sending data to expandable list adapter
    private fun updateExpandableListAdapter(inventoryList: List<InventoryItem>) {
        val expandableListTitle = inventoryList.map { it.brand }.distinct()
        val expandableListDetail = inventoryList.groupBy { it.brand }
        Log.e("getGroupCount", "Titles: $expandableListTitle")
        Log.e("getGroupCount", "Details: $expandableListDetail")
        // adapter of recyclerview
        val expandableListAdapter = InventoryExpandableListAdapter(
            requireContext(), expandableListTitle, expandableListDetail
        ) { groupIndex, childIndex ->
            val brand = expandableListTitle[groupIndex]
            val groupItems = expandableListDetail[brand]
            val absoluteIdx = inventoryList.indexOf(groupItems?.get(childIndex))
            if (absoluteIdx != -1) {
                CommonMethods.showLogs("INVENTORY", "Deleting item at index $absoluteIdx")
                viewModel.deleteFromList(absoluteIdx) // Pass the correct absolute index
            } else {
                CommonMethods.showLogs("INVENTORY", "Error: Item not found in inventory list")
            }
        }
        binding.listView.setAdapter(expandableListAdapter)
    }


    private fun clearForm() {
        binding.edittextQuantity.text.clear()
        binding.edittextPrice.text.clear()
        binding.imeiOne.text.clear()
        binding.imeiTwo.text.clear()
        binding.edittextPurchasePrice.text.clear()
        binding.notes.text.clear()
        binding.spinnerBrandName.text = null
        binding.spinnerModel.text = null
        binding.spinnerVariant.text = null
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
//        hideProgressBar()

        // Notify the user
        println("Excel file with barcodes saved at: ${file.absolutePath}")
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

