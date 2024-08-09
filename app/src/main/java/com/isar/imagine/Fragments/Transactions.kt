package com.isar.imagine.Fragments

data class Transaction(
    val id: String,
    val date: String,
    val brand: String,
    val model: String,
    val variant: String,
    val color: String,
    val quantity: Int,
    val price: Double,
    val totalPrice: Double,
    val condition: String,
    val description: String
)

