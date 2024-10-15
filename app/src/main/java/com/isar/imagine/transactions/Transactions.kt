package com.isar.imagine.transactions

data class Transaction(
    val id: String,
    val date: String,
    val quantity: Int,
    val totalPrice: Double,
    val retailer: String
)

