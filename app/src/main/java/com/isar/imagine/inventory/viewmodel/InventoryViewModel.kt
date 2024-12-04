package com.isar.imagine.inventory.viewmodel

import android.content.Context
import android.os.Environment
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.isar.imagine.billing.InventoryItem
import com.isar.imagine.inventory.data.InventoryData
import com.isar.imagine.inventory.data.repos.BarcodeCallback
import com.isar.imagine.inventory.data.repos.InventoryRepository
import com.isar.imagine.inventory.data.repos.pullAndRemoveLimitedBarcodes
import com.isar.imagine.utils.CommonMethods
import com.isar.imagine.utils.Results
import com.isar.imagine.utils.Utils.now
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlinx.coroutines.withContext
import okhttp3.OkHttpClient
import okhttp3.Request
import org.apache.poi.ss.usermodel.Row
import org.apache.poi.ss.usermodel.Sheet
import org.apache.poi.ss.usermodel.Workbook
import org.apache.poi.xssf.usermodel.XSSFWorkbook
import java.io.File
import java.io.FileOutputStream
import java.text.SimpleDateFormat
import java.util.Date
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException

class InventoryViewModel(private val repository: InventoryRepository) : ViewModel() {

    private val _brands = MutableLiveData<Results<List<String>>>()
    val brands: LiveData<Results<List<String>>> = _brands

    private val _models = MutableLiveData<Results<List<String>>>()
    val models: LiveData<Results<List<String>>> = _models

    private val _variants = MutableLiveData<Results<List<String>>>()
    val variants: LiveData<Results<List<String>>> = _variants
    private val _image = MutableLiveData<Results<String>>()
    val image: LiveData<Results<String>> = _image

    init {
        fetchBrands()
    }

    private fun fetchBrands() {
        _brands.postValue(Results.Loading())
        viewModelScope.launch {
            try {
                val brandsList = repository.getBrands()
                CommonMethods.showLogs("Firebase", "Brand $brandsList")
                _brands.postValue(Results.Success(brandsList))
            } catch (e: Exception) {
                _brands.postValue(Results.Error("Failed to load brands: ${e.message}"))
                CommonMethods.showLogs("Firebase", "Error ${e.message}")

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


    fun fetchImage(brandId: String, modelId: String) {
        _image.postValue(Results.Loading())
        viewModelScope.launch {
            _image.postValue(Results.Success(repository.getImage(brandId, modelId)))
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
        brand: String,
        model: String,
        variant: String,
        imei1: String,
        imei2: String,
        condition: String,
        purchasePrice: Double,
        sellingPrice: Double,
        quantity: Int,
        notes: String
    ) {

        val newItem = InventoryItem(
            quantity = quantity,
            brand = brand,
            model = model,
            variant = variant,
            condition = condition,
            purchasePrice = purchasePrice.toString(),
            sellingPrice = sellingPrice.toString(),
            notes = notes,
            imei1 = imei1,
            imei2 = imei2,
        )

        //temp list of inventory item
        val updatedList = _inventoryList.value?.toMutableList() ?: mutableListOf()

        // Check for duplicates and increase quantity if exists
        val existingItem = updatedList.find {
            it.brand == brand && it.model == model && it.variant == variant && it.condition == condition && it.purchasePrice.toDouble() == purchasePrice && it.sellingPrice.toDouble() == sellingPrice
        }
        if (existingItem != null) {
            existingItem.quantity += quantity
        } else {
            updatedList.add(newItem)
        }
        _inventoryList.value = updatedList
        CommonMethods.showLogs("Inventory", "Inventory List is ${_inventoryList.value}")
    }

    // deleting item from list
    fun deleteFromList(index: Int) {
        val updatedList = _inventoryList.value?.toMutableList() ?: mutableListOf()

        CommonMethods.showLogs(
            "DeleteFromList", "Initial list size: ${_inventoryList.value?.size ?: 0}"
        )
        CommonMethods.showLogs("DeleteFromList", "Index to remove: $index")

        if (index >= 0 && index < updatedList.size) {
            val removedItem = updatedList[index]
            updatedList.removeAt(index)
            _inventoryList.value = updatedList
            CommonMethods.showLogs("DeleteFromList", "Item removed: ${_inventoryList.value}")
            CommonMethods.showLogs("DeleteFromList", "Updated list size: ${updatedList.size}")
        } else {
            CommonMethods.showLogs("DeleteFromList", "Invalid index: $index. No item removed.")
        }
    }


    private val _inventoryListFinal = MutableLiveData<List<InventoryData>>()
    val inventoryListFinal: LiveData<List<InventoryData>> get() = _inventoryListFinal


    fun onSave(context: Context, result: (Boolean) -> Unit) {
        CommonMethods.showLogs("Inventory", "Starting to save inventory items")

        viewModelScope.launch {
            // Log the start of the coroutine
            CommonMethods.showLogs("Inventory", "Coroutine started for saving inventory items")

            val items = getItems()
            // Log the items retrieved
            CommonMethods.showLogs("Inventory", "Items retrieved: ${items.size} items")

            if (items.isNotEmpty()) {
                CommonMethods.showLogs("Inventory", "Posting inventory items to the server")
                postInventory(items) { success ->
                    // Log the result of postInventory
                    if (success) {
                        CommonMethods.showLogs("Inventory", "Inventory items posted successfully")

                        createOnlySerialExcel(context, items)
                        // Log success result
                        CommonMethods.showLogs(
                            "Inventory", "Excel file created and inventory state updated"
                        )
                        result(true)
                    } else {
                        // Log failure to post inventory
                        CommonMethods.showLogs(
                            "Inventory", "Failed to post inventory items to the server"
                        )
                        result(false)
                    }
                }
            } else {
                // Log that no items were found
                CommonMethods.showLogs("Inventory", "No items to save, inventory list is empty")
                result(false)
            }
        }
    }

    private fun createOnlySerialExcel(
        context: Context,
        item: List<InventoryData>,
    ): File {
        val workbook = XSSFWorkbook()
        val now = SimpleDateFormat("yyyy-MM-dd_HH-mm").format(Date())
        val sheet = workbook.createSheet("serialNumbers-${item.size}-$now")

        val headerRow = sheet.createRow(0)
        headerRow.createCell(0).setCellValue("Product")
        headerRow.createCell(1).setCellValue("Serial Number")

        var rowIdxProduct = 2

        item.forEach { inventory ->
            val list = inventory.serialNumber
            // Define the properties to be written to the sheet
            val properties = listOf(
                Pair(inventory.brand, 0),
                Pair(inventory.model, 1),
                Pair(inventory.variant, 2),
                Pair(inventory.condition, 3),
                Pair("${inventory.sellingPrice}", 4),
                Pair("${inventory.quantity}", 5)
            )

            // Write each property to the sheet
            properties.forEach { (productDetail, serialIdx) ->
                sheet.createRow(rowIdxProduct).apply {
                    createCell(0).setCellValue(productDetail)
                    createCell(1).setCellValue(list.getOrNull(serialIdx) ?: "")
                }
                rowIdxProduct++
            }
            for (i in 6 until list.size) {
                sheet.createRow(rowIdxProduct).apply {
                    createCell(0).setCellValue("")
                    createCell(1).setCellValue(list[i])
                }
                rowIdxProduct++
            }

            // Add a blank row between different inventory items
            sheet.createRow(rowIdxProduct).apply {
                createCell(0).setCellValue("")
                createCell(1).setCellValue("")
            }
            rowIdxProduct++
        }


        val documentsDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
        val file = File(documentsDir, "serialNumbers-${item.size}-${now()}.xlsx")
        // Save the file
        FileOutputStream(file).use { fileOut ->
            workbook.write(fileOut)
            CommonMethods.showLogs(
                "EXCEL", "Excel file saved successfully at: ${file.absolutePath}"
            )

        }
        workbook.close()

        return file


    }

    private fun createBarcodeExcel(
        context: Context, items: List<InventoryData>, fileName: String
    ): File {
        // Create a new workbook and sheet
        val workbook: Workbook = XSSFWorkbook()
        val sheet: Sheet = workbook.createSheet("Barcodes")

        // Create a font for Code 128 barcodes
//        val barcodeFont: Font = workbook.createFont().apply {
//            fontName = "Code 128"
//        }

        // Create a style for barcode cells
//        val barcodeCellStyle: CellStyle = workbook.createCellStyle().apply {
//            setFont(barcodeFont)
//        }

        // Create a header row
        val headerRow: Row = sheet.createRow(0)
        headerRow.createCell(0).setCellValue("Plain Serial Number")
        headerRow.createCell(1).setCellValue("Barcode (Code 128 Font)")

        val httpClient = OkHttpClient()

        try {
            // Populate rows with serial numbers
            var rowIndex = 1 // Start from row 1 (after the header)
            items.forEach { item ->
                item.serialNumber.forEach { serialNumber ->
                    // Create a new row for each serial number
                    val row: Row = sheet.createRow(rowIndex)
                    row.createCell(0).setCellValue(serialNumber)


                    // Add Code 128 formatted barcode

                    val barcodeUrl =
                        "https://barcode.tec-it.com/barcode.ashx?data=$serialNumber&code=Code128&translate-esc=on"
                    CommonMethods.showLogs(
                        "EXCEL", "Fetching barcode for serial number: $serialNumber"
                    )

                    val request = Request.Builder().url(barcodeUrl).build()
                    val response = httpClient.newCall(request).execute()

                    if (response.isSuccessful) {
                        CommonMethods.showLogs(
                            "EXCEL", "Successfully fetched barcode for: $serialNumber"
                        )

                        response.body?.byteStream()?.use { imageStream ->
                            val imageBytes = imageStream.readBytes()
                            val pictureIdx =
                                workbook.addPicture(imageBytes, Workbook.PICTURE_TYPE_PNG)
                            val drawing = sheet.createDrawingPatriarch()
                            val helper = workbook.creationHelper
                            val anchor = helper.createClientAnchor().apply {
                                setCol1(1)
                                setCol2(2)
                                row1 = rowIndex
                                row2 = rowIndex + 1
                            }
                            drawing.createPicture(anchor, pictureIdx)
                            CommonMethods.showLogs(
                                "EXCEL", "Barcode image added to Excel for: $serialNumber"
                            )
                        }
                    } else {
                        CommonMethods.showLogs(
                            "EXCEL",
                            "Failed to fetch barcode for: $serialNumber, Response Code: ${response.code}"
                        )
                    }
                    response.close()
                    rowIndex++

//                val barcodeCell: Cell = row.createCell(1)
//                barcodeCell.setCellValue(serialNumber)
//                barcodeCell.cellStyle = barcodeCellStyle
                }
            }
            sheet.setColumnWidth(0, 6000) // Adjust for serial number column
            sheet.setColumnWidth(1, 8000) // Adjust for barcode column


            // Autosize columns for better readability
//            sheet.autoSizeColumn(0)
//        sheet.autoSizeColumn(1)
            // Save the file in the Documents directory
            val documentsDir = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS)
            val file = File(documentsDir, fileName)
            // Save the file
            FileOutputStream(file).use { fileOut ->
                workbook.write(fileOut)
                CommonMethods.showLogs(
                    "EXCEL", "Excel file saved successfully at: ${file.absolutePath}"
                )

            }
            workbook.close()

            return file
        } catch (e: Exception) {
            CommonMethods.showLogs("EXCEL", "An error occurred: ${e.message}")
            throw e
        }

    }

    private suspend fun getItems(): List<InventoryData> = withContext(Dispatchers.IO) {
        CommonMethods.showLogs("Inventory", "Inventory List is ${_inventoryList.value}")
        val tempInventoryItem = mutableListOf<InventoryData>()
        _inventoryList.value?.forEach { item ->
            try {
                val barCodeList = generateUniqueBarcodes(item.quantity)
                Log.e("Inventory", "Generated barcodes: $barCodeList")
                if (barCodeList.isNotEmpty()) {
                    val inventoryData = InventoryData(
                        item.brand,
                        item.model,
                        item.variant,
                        item.condition,
                        item.purchasePrice.toString(),
                        item.imei1,
                        item.imei2,
                        item.sellingPrice.toString(),
                        item.quantity,
                        item.notes,
                        barCodeList
                    )
                    tempInventoryItem.add(inventoryData)
                }
            } catch (e: Exception) {
                CommonMethods.showLogs("Inventory", "Error generating barcodes: ${e.message}")
            }
        }
        tempInventoryItem
    }


    private suspend fun generateUniqueBarcodes(quantity: Int): List<String> {

        return suspendCancellableCoroutine { continuation ->
            val list = mutableListOf<String>()
            pullAndRemoveLimitedBarcodes(
                firestore = FirebaseFirestore.getInstance(),
                quantity,
                object : BarcodeCallback {
                    override fun onBarcodesPulled(barcodes: List<String>) {
                        list.addAll(barcodes)
                        CommonMethods.showLogs("Inventory", "Pulled barcodes: $barcodes")
                        continuation.resume(list)
                    }

                    override fun onFailure(exception: String) {
                        CommonMethods.showLogs("Inventory", "Failed to pull barcodes: $exception")
                        continuation.resumeWithException(Exception(exception))
                    }

                })
        }


    }

    private suspend fun postInventory(items: List<InventoryData>, result: (Boolean) -> Unit) {
        try {
            repository.saveInventory(items) { success ->
                result(success)
            }
        } catch (e: Exception) {
            Log.e("Inventory", "Error saving inventory: ${e.message}")
            result(false)
        }
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


