package com.isar.imagine.responses

data class InventoryCountResponses(
    val brands: List<Brand>,
    val totalBrands: Int // 9
)
data class Brand(
    val modelNames: List<String>,
    val name: String, // Apple
    val totalModels: Int // 3
)