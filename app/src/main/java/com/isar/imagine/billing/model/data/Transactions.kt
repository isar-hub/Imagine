package com.isar.imagine.billing

import com.isar.imagine.utils.Utils.now

data class Transactions(
    val createdAt: String = now(),
    val billValue: Double,
    val totalQuantity: Long,
    val description: String,
    val retailerId: String,
    val productId: List<String>
)

data class TransactionsResponse(
    val transactionID: String = "",
    val createdAt: String = "",
    val billValue: Double = 0.0,
    val totalQuantity: Long = 0,
    val description: String = "",
    val retailerId: String = "",
    val productId: List<String> = emptyList()
)


enum class Status(val status: String) {
    BILLED("Billed"), SOLD("Sold"), EXPIRED("Expired")
}

data class SingleItem(
    val serialNumber: String = "",
    val brand: String = "",
    val model: String = "",
    val variant: String = "",
    val condition: String = "",
    var sellingPrice: String = "",
    var status: Status = Status.BILLED,
    var retailerId: String = "",
    var notes: String = "",
    var createdAt: String = ""
)

data class InventoryItem(
    var quantity: Int,
    val brand: String,
    val model: String,
    val variant: String,
    val imei1: String,
    val imei2: String,
    val condition: String,
    var purchasePrice: String,
    var sellingPrice: String,
    var notes: String
)
