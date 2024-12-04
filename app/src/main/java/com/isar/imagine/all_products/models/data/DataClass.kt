package com.isar.imagine.all_products.models.data

import com.isar.imagine.billing.Status
import java.io.Serializable

data class SoldProductInfo(
    val serialNumber: String = "",
    val brand: String = "",//
    val model: String = "",//
    val variant: String = "",
    val condition: String = "",
    var sellingPrice: String = "",

    val name: String = "",
    val phone: String = "",
    val email: String = "",
    val imageUrl: String = "",
    val signatureUrl: String = "",
    val address: String = "",
    val state: String = "",

    val warrantyEnded: String = "",
    val warrantyStarted: String = "",
    var notes: String = "",
    val retailerId: String = "",


    val status: Status = Status.BILLED,
    val totalPrice: String = "",

) : Serializable

data class BilledProductInfo(
    val brand: String = "",
    val condition: String = "",
    val model: String = "",
    var notes: String = "",
    val retailerId: String = "",
    var sellingPrice: Double = 0.0,
    val serialNumber: String = "",
    val status: Status = Status.BILLED,
    val variant: String = "",
):Serializable
