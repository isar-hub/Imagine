package com.isar.imagine.billing

import com.isar.imagine.data.model.InventoryItem
import com.isar.imagine.viewmodels.RetailerEntity

data class Transactions(
    val createdAt : String,
    val billValue : Double,
    val totalQuantity : Long,
    val description : String,
    val retailerId : String,
    val retailer : String,
    val items : List<InventoryItem>
)

data class SingleItem(
    val serialNumber : String,
    val brand: String,
    val model: String,
    val variant: String,
    val condition: String,
    var sellingPrice: Double,
    var status : String,
    var retailerId : String,
    var notes: String
)
