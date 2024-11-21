package com.isar.imagine.billing

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.functions.FirebaseFunctions
import com.isar.imagine.barcode_scenning.models.BillingDataModel
import com.isar.imagine.responses.UserDetails
import com.isar.imagine.utils.CommonMethods
import com.isar.imagine.utils.Invoice
import com.isar.imagine.utils.Results
import com.isar.imagine.utils.Utils
import com.isar.imagine.viewmodels.RetailerRepository
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await

class BillingViewModel(
    private val repository: BillingRepository
) : ViewModel() {
    private val firestore = FirebaseFirestore.getInstance()

    private val _retailers = MutableLiveData<Results<List<UserDetails>>>()
    val retailers: LiveData<Results<List<UserDetails>>> get() = _retailers

    init {
        viewModelScope.launch {
            getRetailer()
        }

    }

    private fun postSingleTransaction(data: Transactions, onResult: (Boolean) -> Unit) {
        viewModelScope.launch {
            onResult(repository.postSingleTransaction(data))

        }
    }

    fun addTransaction(
        data: List<BillingDataModel>, uid: String, onResult: (
            Boolean
        ) -> Unit
    ) {

        val pairItem = data.map { Pair(it.inventoryId, it.quantity) }
        viewModelScope.launch {
            val allItem = async { billAllItems(pairItem, uid) }
            repository.addTransaction(allItem.await()) {
                postSingleTransaction(getTransactions(data, uid)) {
                    onResult(
                        it
                    )
                }
            }
        }
    }

    fun getTransactions(data: List<BillingDataModel>, uid: String): Transactions {
        val totalPrice = data.sumOf { it.sellingPrice }
        val totalQuantity = data.sumOf { it.quantity }
        val productsID = data.map { it.serialNumber }
        val transactions = Transactions(
            billValue = totalPrice,
            totalQuantity = totalQuantity,
            description = "",
            retailerId = uid,
            productId = productsID,
        )
        return transactions
    }

    private suspend fun billAllItems(
        listItemId: List<Pair<String, Long>>, uid: String
    ): List<SingleItem> {
        val listSingleItem = mutableListOf<SingleItem>()
        listItemId.forEach {
            val item = billItems(it.first, it.second, uid)
            listSingleItem.addAll(item)
        }
        return listSingleItem
    }

    private suspend fun billItems(
        itemId: String, quantityToBill: Long, uid: String
    ): List<SingleItem> {

        val itemDocument = firestore.collection("inventory").document(itemId).get().await()
        val serials = itemDocument.get("serialNumber") as? MutableList<String> ?: mutableListOf()
        val quantity = itemDocument.get("quantity") as Long

        if (serials.size < quantityToBill) {
            throw IllegalArgumentException("Not enough items in inventory")
        }

        val serialsToBill = serials.take(quantityToBill.toInt())
        val updatedSerials = serials.drop(quantityToBill.toInt())
        val updatedQuantity = quantity - quantityToBill

        val billedItems = serialsToBill.map { serial ->
            SingleItem(
                serialNumber = serial,
                brand = itemDocument.getString("brand") ?: "",
                model = itemDocument.getString("model") ?: "",
                variant = itemDocument.getString("variant") ?: "",
                condition = itemDocument.getString("condition") ?: "",
                sellingPrice = itemDocument.getDouble("sellingPrice") ?: 0.0,
                status = Status.BILLED,
                retailerId = uid,
                notes = ""
            )
        }

        firestore.collection("inventory").document(itemId)
            .update(mapOf("serialNumber" to updatedSerials, "quantity" to updatedQuantity)).await()

        return billedItems  // Return the list of billed items
    }


    fun generateInvoiceData(
        retailerEntity: UserDetails, // Replace with the actual type
        listData: List<BillingDataModel>
    ): Pair<Invoice.UserInformation, List<Invoice.ProductInformation>> {
        val user = Invoice.UserInformation(
            name = retailerEntity.name,
            address = retailerEntity.email,
            transactionNumber = retailerEntity.uid,
            stateName = "Bihar",
            placeOfSupply = "Bihar",
            code = 10,
            date = Utils.now()
        )

        val productInformationList = listData.map { billingItem ->
            Invoice.ProductInformation(
                serialNumber = billingItem.serialNumber,
                description = "${billingItem.brand} ${billingItem.model} ${billingItem.variant} ${billingItem.condition}",
                gst_rate = 18.00,
                quantity = billingItem.quantity,
                rate = billingItem.sellingPrice,
                discount = 0.0,
                total = billingItem.sellingPrice
            )
        }
        return Pair(user, productInformationList)
    }

    private suspend fun getRetailer() {
        CommonMethods.showLogs("BillingViewModel", "Fetching retailers...")
        _retailers.postValue(Results.Loading()) // Optional: explicitly set loading state
        val result = repository.getRetailer()
        CommonMethods.showLogs("BillingViewModel", "Fetch result: $result")
        _retailers.postValue(result)
    }

}


class BillingRepository(
    private val retailerRepository: RetailerRepository,
    private val functions: FirebaseFunctions,
    private val firestore: FirebaseFirestore
) {
    suspend fun getRetailer(): Results<List<UserDetails>> {
        return try {
            CommonMethods.showLogs(
                "BillingRepository", "Calling retailerRepository.getRetailer()..."
            )
            val retailers = retailerRepository.getRetailer(firestore)
            CommonMethods.showLogs("Retailers", "Result is $retailers")
            retailers
        } catch (e: Exception) {
            CommonMethods.showLogs("BillingRepository", "Error fetching retailers: ${e.message}")

            Results.Error(e.message ?: "Unknown error")
        }
    }

    suspend fun addTransaction(item: List<SingleItem>, result: (Boolean) -> Unit) = coroutineScope {
        try {
            val batch = firestore.batch()
            item.forEach {
                val billingItem = firestore.collection("products").document(it.serialNumber)
                batch.set(billingItem, it)
            }
            batch.commit().await()
            result(true)
        } catch (e: Exception) {
            Log.e("Billing", "Batch save failed in billing: ${e.message}")
            result(false)
        }
    }

    suspend fun postSingleTransaction(data: Transactions): Boolean {
        return try {
            val transactions = firestore.collection("transactions").document()
            transactions.set(data).await()
            true
        } catch (e: Exception) {
            false
        }
    }

}


class BillingViewmodelFactory(
    private val billingRepository: BillingRepository
) : ViewModelProvider.Factory {
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        if (modelClass.isAssignableFrom(BillingViewModel::class.java)) {
            return BillingViewModel(billingRepository) as T
        }
        throw IllegalArgumentException("Unknown ViewModel class")
    }
}
