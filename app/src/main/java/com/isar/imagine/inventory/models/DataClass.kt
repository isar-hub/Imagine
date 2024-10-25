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
        var inventoryData: InventoryItem,
        var serialNumber: List<String>
    )

}
