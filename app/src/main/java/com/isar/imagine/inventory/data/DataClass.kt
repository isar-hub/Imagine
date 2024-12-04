package com.isar.imagine.inventory.data
data class InventoryData(
    val brand: String,
    val model: String,
    val variant: String,
    val condition: String,
    var purchasePrice: String,
    var imei_1 : String,
    var imei_2 : String,
    var sellingPrice: String,
    var quantity: Int,
    var notes: String,
    var serialNumber: List<String>
)
