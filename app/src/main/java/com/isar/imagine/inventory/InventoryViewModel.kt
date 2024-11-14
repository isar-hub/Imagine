package com.isar.imagine.inventory

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.isar.imagine.data.model.InventoryItem
import com.isar.imagine.inventory.models.DataClass
import com.isar.imagine.utils.CommonMethods
import com.isar.imagine.utils.Results
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.apache.poi.ss.formula.functions.T
import kotlin.random.Random

class InventoryViewModel(private val repository: InventoryRepository) : ViewModel() {

    private val _brands = MutableLiveData<Results<List<String>>>()
    val brands: LiveData<Results<List<String>>> = _brands

    private val _models = MutableLiveData<Results<List<String>>>()
    val models: LiveData<Results<List<String>>> = _models

    private val _variants = MutableLiveData<Results<List<String>>>()
    val variants: LiveData<Results<List<String>>> = _variants

    init {
        fetchBrands()

    }

    private fun fetchBrands() {
        _brands.postValue(Results.Loading())
        viewModelScope.launch {
            try {
                val brandsList = repository.getBrands()
                CommonMethods.showLogs("Firebase","Brand $brandsList")
                _brands.postValue(Results.Success(brandsList))
            } catch (e: Exception) {
                _brands.postValue(Results.Error("Failed to load brands: ${e.message}"))
                CommonMethods.showLogs("Firebase","Error ${e.message}")

            }
        }
    }

    fun fetchModels(brandId: String) {
        _models.postValue(Results.Loading())
        viewModelScope.launch {
            try {
                repository.getModels(brandId, { models ->


                    CommonMethods.showLogs("Inventory", "Models $models")
                    _models.postValue(Results.Success(models))
                }, { error ->
                    _models.postValue(Results.Error("Failed to load brands: ${error.message}"))
                    CommonMethods.showLogs("Inventory", "Failed to load brands: ${error.message}")
                }


                )

            } catch (e: Exception) {
                CommonMethods.showLogs("Inventory", "Failed to load brands: ${e.message}")
                _models.postValue(Results.Error("Failed to load brands: ${e.message}"))
            }
        }
    }

    fun fetchVariants(brandId: String, modelId: String) {
        _variants.postValue(Results.Loading())
        CommonMethods.showLogs("Inventory", "Variants $variants")
        viewModelScope.launch {
            try {
                repository.getVariants(brandId, modelId, { variants ->
                    _variants.postValue(Results.Success(variants))
                    CommonMethods.showLogs("Inventory", "Variants $variants")

                }, { error ->
                    _variants.postValue(
                        Results.Error(
                            "Failed to load brands: ${
                                error.message
                            }"
                        )
                    )
                    CommonMethods.showLogs("Inventory", "Failed to load brands: ${error.message}")
                })
            } catch (e: Exception) {
                _variants.postValue(Results.Error("Failed to load brands: ${e.message}"))
                CommonMethods.showLogs("Inventory", "Failed to load brands: ${e.message}")
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
            quantity, name, model, variant, condition, purchasePrice, sellingPrice, notes
        )

        //temp list of inventory item
        val updatedList = _inventoryList.value?.toMutableList() ?: mutableListOf()

        // Check for duplicates and increase quantity if exists
        val existingItem = updatedList.find {
            it.brand == name && it.model == model && it.variant == variant && it.condition == condition
        }
        if (existingItem != null) {
            existingItem.quantity += quantity
        } else {
            updatedList.add(newItem)
        }

        //adding temp list to our inventory item to show in list view of inventory item
        _inventoryList.value = updatedList
    }

//    private val _generatedBarcodes = MutableLiveData<List<String>>()
//    val generatedBarcodes: LiveData<List<String>> get() = _generatedBarcodes


    private val _inventoryListFinal = MutableLiveData<List<DataClass.InventoryData>>()
    val inventoryListFinal: LiveData<List<DataClass.InventoryData>> get() = _inventoryListFinal


    suspend fun onSave(result : (Boolean) -> Unit) {
        Log.d("Inventory", "Starting to save inventory items")
        postInventory(getItems()) { result(it) }


    }

    suspend fun getItems(): List<DataClass.InventoryData> {

        return withContext(Dispatchers.IO) {
            val tempInventoryItem = mutableListOf<DataClass.InventoryData>()
            _inventoryList.value?.forEach { item ->
                val quantity = item.quantity
                Log.d("Inventory", "Processing item: ${item.brand}, Quantity: $quantity")

                val barCodeListDeferred = async { generateUniqueBarcodes(quantity) }
                val barCodeList = barCodeListDeferred.await() // Wait for completion

                Log.d("Inventory", "Generated barcodes: $barCodeList")

                val inventoryData = DataClass.InventoryData(
                    item.brand,
                    item.model,
                    item.variant,
                    item.condition,
                    item.sellingPrice,
                    item.purchasePrice,
                    item.quantity,
                    item.notes,
                    barCodeList
                )
                tempInventoryItem.add(inventoryData)
            }
            Log.d("Inventory", "Final items count: ${tempInventoryItem.size}") // Log final count

            tempInventoryItem

        }

    }

    private val firestore = FirebaseFirestore.getInstance()

    private suspend fun generateUniqueBarcodes(quantity: Int): List<String> {

        return withContext(Dispatchers.IO) {
            val barCodeSet = mutableSetOf<String>()

            Log.d("Barcode", "Generating unique barcodes for quantity: $quantity")

            while (barCodeSet.size < quantity) {
                val newBarcode = generateSerialNumber()
                Log.d("Barcode", "Generated barcode: $newBarcode")
                barCodeSet.add(newBarcode)
            }
            Log.d("Barcode", "Checked barcodes in Firebase: $barCodeSet")
            val barCodeListDeferred = async { checkBarcodesInFirebase(barCodeSet.toList()) }
            barCodeListDeferred.await()
        }

    }

    private fun generateSerialNumber(): String {
        val digits = ('0'..'9').toList()
        val random = Random(System.currentTimeMillis())

        // Generate the first part (IMG)
        val part1 = "IMG"

        // Generate the second part (123456789)
        val part2 = (1..9).map { digits.random(random) }.joinToString("")

        val serialNumber = "$part1$part2"
        Log.d("SerialNumber", "Generated serial number: $serialNumber")
        return serialNumber
    }

    private suspend fun checkBarcodesInFirebase(barcodes: List<String>): List<String> {
        Log.d("Firebase", "Checking barcodes in Firebase: $barcodes")


        return withContext(Dispatchers.IO) {
            val uniqueBarcodes = mutableListOf<String>()
            for (barcode in barcodes) {
                val exists =
                    firestore.collection("inventory").whereArrayContains("serialNumber", barcode)
                        .get().await().documents.isEmpty()

                if (exists) {
                    Log.d("Firebase", "Barcode exists, generating a new one for: $barcode")
                    uniqueBarcodes.add(barcode)
                } else {
                    Log.d("Firebase", "Barcode is unique: $barcode")
                    uniqueBarcodes.add(generateSerialNumber())
                }
            }
            uniqueBarcodes
        }

    }

//    private val _postInventoryItem = MutableLiveData<String>()
//    val postInventoryItem: LiveData<String> get() = _postInventoryItem

    private suspend fun postInventory(item: List<DataClass. InventoryData>, result: (Boolean) -> Unit){
        repository.saveInventory(item,{result(it)})
    }


}

class MobileViewModelFactory(private val repository: InventoryRepository) :
    ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(InventoryViewModel::class.java)) {
            @Suppress("UNCHECKED_CAST") return InventoryViewModel(repository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}


