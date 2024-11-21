package com.isar.imagine.billing

import com.isar.imagine.data.model.InventoryItem
import com.isar.imagine.utils.Utils.now
import com.isar.imagine.viewmodels.RetailerEntity

data class Transactions(
    val createdAt : String = now(),
    val billValue : Double,
    val totalQuantity : Long,
    val description : String,
    val retailerId : String,
    val productId : List<String>
)

enum class Status (val status: String){
    BILLED("Billed"), SOLD("Sold"),EXPIRED("Expired")
}
data class SingleItem(
    val serialNumber : String,
    val brand: String,
    val model: String,
    val variant: String,
    val condition: String,
    var sellingPrice: Double,
    var status : Status,
    var retailerId : String,
    var notes: String
)
