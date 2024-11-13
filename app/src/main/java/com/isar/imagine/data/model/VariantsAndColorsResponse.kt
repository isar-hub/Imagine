package com.isar.imagine.data.model

data class InventoryItem(
    val brand: String,
    val model: String,
    val variant: String,
    val condition: String,
    var purchasePrice: Double,
    var sellingPrice: Double,
    var quantity: Int,
    var notes: String
)

data class ItemWithSerialResponse(
    val brand: String,
    val model: String,
    val variant: Variant,
    val color: String,
    val description : String,
    val condition: String,
    var sellingPrice: Double,
    var notes: String
)


data class BrandNamesResponse(
    val success: Boolean,
    val data: List<String>
)

data class VariantsAndColorsResponse(
    val success: Boolean,
    val data: VariantAndColor
)

data class VariantAndColor(
    val color: List<String>,
    val variants: List<Variant>,
)

data class Variant(
    val ram: Int,
    val rom: Int,
    val _id : String
)
