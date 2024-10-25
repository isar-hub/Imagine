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
import com.isar.imagine.utils.Results
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import kotlinx.coroutines.withContext
import org.apache.poi.ss.formula.functions.T
import kotlin.random.Random

class InventoryViewModel(private val repository: InventoryRepository) : ViewModel() {

    private val _brands = MutableLiveData<Results<List<DataClass.Brand>>>()
    val brands: LiveData<Results<List<DataClass.Brand>>> = _brands

    private val _models = MutableLiveData<Results<List<DataClass.Model>>>()
    val models: LiveData<Results<List<DataClass.Model>>> = _models

    private val _variants = MutableLiveData<Results<List<DataClass.Variant>>>()
    val variants: LiveData<Results<List<DataClass.Variant>>> = _variants

    init {
        fetchBrands()
        _brands.postValue(Results.Loading())
    }

    private fun fetchBrands() {
        viewModelScope.launch {
            try {
                val brandsList = repository.getBrands()
                _brands.postValue(Results.Success(brandsList))
            } catch (e: Exception) {
                _brands.postValue(Results.Error("Failed to load brands: ${e.message}"))

            }
        }
    }

    fun fetchModels(brandId: String) {
        _models.postValue(Results.Loading())
        viewModelScope.launch {
            try {
                val modelsList = repository.getModels(brandId)
                _models.postValue(Results.Success(modelsList))
            } catch (e: Exception) {
                _models.postValue(Results.Error("Failed to load brands: ${e.message}"))
            }
        }
    }

    fun fetchVariants(brandId: String, modelId: String) {
        _variants.postValue(Results.Loading())
        viewModelScope.launch {
            try {
                val variantsList = repository.getVariants(brandId, modelId)
                _variants.postValue(Results.Success(variantsList))
            } catch (e: Exception) {
                _models.postValue(Results.Error("Failed to load brands: ${e.message}"))
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
    val inventoryListFinal : LiveData<List<DataClass.InventoryData>> get() = _inventoryListFinal

     suspend fun onSave() {
        Log.d("Inventory", "Starting to save inventory items")

         val items = getItems()
         for (item in items){
             val response = postInventory(item)
             Log.e("submission", "Final post response is $response" )

         }

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

                val inventoryData = DataClass.InventoryData(item, barCodeList)
                tempInventoryItem.add(inventoryData)
            }
            Log.d("Inventory", "Final items count: ${tempInventoryItem.size}") // Log final count

            tempInventoryItem

        }

    }
    private val firestore = FirebaseFirestore.getInstance()

    private suspend fun generateUniqueBarcodes(quantity: Int): List<String> {

        return withContext(Dispatchers.IO){
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


        return withContext(Dispatchers.IO){
            val uniqueBarcodes = mutableListOf<String>()
            for (barcode in barcodes) {
                val exists = firestore.collection("inventory")
                    .whereEqualTo("serialNumber", barcode)
                    .get()
                    .await()
                    .isEmpty // Check if the query returns no documents

                if (!exists) {
                    Log.d("Firebase", "Barcode exists, generating a new one for: $barcode")
                    uniqueBarcodes.add(generateSerialNumber())
                } else {
                    Log.d("Firebase", "Barcode is unique: $barcode")
                    uniqueBarcodes.add(barcode)
                }
            }
            uniqueBarcodes
        }

    }

//    private val _postInventoryItem = MutableLiveData<String>()
//    val postInventoryItem: LiveData<String> get() = _postInventoryItem

     fun postInventory(item: DataClass.InventoryData): String{
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


