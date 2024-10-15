package com.isar.imagine.inventory

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.isar.imagine.data.model.InventoryItem
import com.isar.imagine.inventory.models.DataClass
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlin.random.Random

class InventoryViewModel(private val repository: InventoryRepository) : ViewModel() {

    private val _brands = MutableLiveData<List<DataClass.Brand>>()
    val brands: LiveData<List<DataClass.Brand>> = _brands

    private val _models = MutableLiveData<List<DataClass.Model>>()
    val models: LiveData<List<DataClass.Model>> = _models

    private val _variants = MutableLiveData<List<DataClass.Variant>>()
    val variants: LiveData<List<DataClass.Variant>> = _variants

    private val _error = MutableLiveData<String>()
    val error: LiveData<String> = _error

    init {
        fetchBrands()
    }

    private fun fetchBrands() {
        viewModelScope.launch {
            try {
                val brandsList = repository.getBrands()
                _brands.value = brandsList
            } catch (e: Exception) {
                _error.value = "Failed to load brands: ${e.message}"
            }
        }
    }

    fun fetchModels(brandId: String) {
        viewModelScope.launch {
            try {
                val modelsList = repository.getModels(brandId)
                _models.value = modelsList
            } catch (e: Exception) {
                _error.value = "Failed to load models: ${e.message}"
            }
        }
    }

    fun fetchVariants(brandId: String, modelId: String) {
        viewModelScope.launch {
            try {
                val variantsList = repository.getVariants(brandId, modelId)
                _variants.value = variantsList
            } catch (e: Exception) {
                _error.value = "Failed to load variants: ${e.message}"
            }
        }
    }



    private val _inventoryList = MutableLiveData<List<InventoryItem>>(emptyList())
    val inventoryList: LiveData<List<InventoryItem>> get() = _inventoryList

    // adding item to show a list of inventory item
    fun addItem(
        name: String,
        model: String,
        variant: String,
        condition: String,
        purchasePrice: Double,
        sellingPrice: Double,
        quantity: Int,
        notes: String
    ) {


        //new item of inventory item with method parameters
        val newItem = InventoryItem(
            name, model,  variant, condition, purchasePrice, sellingPrice, quantity, notes
        )

        //temp list of inventory item
        val updatedList = _inventoryList.value?.toMutableList() ?: mutableListOf()

        // Check for duplicates and increase quantity if exists
        val existingItem = updatedList.find {
            it.brand == name && it.model == model && it.variant == variant
        }
        if (existingItem != null) {
            existingItem.quantity += quantity
        } else {
            updatedList.add(newItem)
        }

        //adding temp list to our inventory item to show in list view of inventory item
        _inventoryList.value = updatedList
    }

    private val _generatedBarcodes = MutableLiveData<List<String>>()
    val generatedBarcodes: LiveData<List<String>> get() = _generatedBarcodes


    private val firestore = FirebaseFirestore.getInstance()
     fun generateUniqueBarcodes(quantity: Int) {
        val barCodeSet = mutableSetOf<String>()

        while (barCodeSet.size < quantity) {
            val newBarcode = generateSerialNumber()
            barCodeSet.add(newBarcode)
        }

        checkBarcodesInFirebase(barCodeSet.toList())
    }
    private fun generateSerialNumber(): String {
        val lettersUppercase = ('A'..'Z').toList()
        val digits = ('0'..'9').toList()

        val random = Random(System.currentTimeMillis())

        // Generate the first part (ABC)
        val part1 = "IMG"

        // Generate the second part (123456789)
        val part2 = (1..9).map { digits.random(random) }.joinToString("")

        // Combine both parts
        return "$part1$part2"
    }
    private fun checkBarcodesInFirebase(barcodes: List<String>) {
        // Asynchronously check the uniqueness of barcodes in Firebase
        viewModelScope.launch {
            val uniqueBarcodes = mutableListOf<String>()
            for (barcode in barcodes) {
                val exists = firestore.collection("inventory")
                    .whereEqualTo("serialNumber", barcode)
                    .get()
                    .await()
                    .isEmpty // Check if the query returns no documents

                if (!exists) {
                    // If the barcode exists, generate a new one
                    uniqueBarcodes.add(generateSerialNumber())
                } else {
                    uniqueBarcodes.add(barcode)
                }
            }
            _generatedBarcodes.value = uniqueBarcodes
        }
    }

    private val _postInventoryItem = MutableLiveData<String>()
    val postInventoryItem: LiveData<String> get() = _postInventoryItem

     fun postInventory(item: DataClass.InventoryData): String?{
        lateinit var result: String
         viewModelScope.launch {
                result = try {
                    repository.saveInventory(item)

                }catch (e : Exception){
                    "Exception : ${e.message}"
                }
        }
        return result
    }


}
class MobileViewModelFactory(private val repository: InventoryRepository) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(InventoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST")
            return InventoryViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}