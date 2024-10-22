package com.isar.imagine.inventory.models

import com.isar.imagine.data.model.InventoryItem

class DataClass{
    data class Brand(
        val id: String = "",
        val name: String = ""
    )
    data class Model(
        val id: String = "",
        val name: String = ""
    )
    data class Variant(
        val id: String = "",
        val ram: Long = 0,
        val rom: Long = 0,
        val color: String = "",
    )

    data class InventoryData(
//        val brand: String,
//        val model: String,
//        val variant: String,
//        val condition: String,
//        var purchasePrice: Double,
//        var sellingPrice: Double,
//        var quantity: Int,
//        var notes: String,
        var inventoryData: InventoryItem,
        var serialNumber: List<String>
    )

}
