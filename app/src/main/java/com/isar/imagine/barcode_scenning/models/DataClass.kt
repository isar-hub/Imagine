package com.isar.imagine.barcode_scenning.models

data class BillingDataModel(
    val brand: String,
    val model: String,
    val variant: String,
    val condition: String,
    var purchasePrice: Double,
    var sellingPrice: Double,
    var quantity: Long,
    var notes: String,
    val serialNumber: String
)

fun mapToBillingDataModel(data: Map<String, Any?>): BillingDataModel {
    return BillingDataModel(
        brand = data["brand"] as? String ?: "",
        model = data["model"] as? String ?: "",
        variant = data["variant"] as? String ?: "",
        condition = data["condition"] as? String ?: "",
        purchasePrice = data["purchasePrice"] as? Double ?: 0.0,
        sellingPrice = data["sellingPrice"] as? Double ?: 0.0,
        quantity = data["quantity"] as? Long ?: 0,
        notes = data["notes"] as? String ?: "",
        serialNumber = data["serialNumber"] as? String ?: ""
    )
}
