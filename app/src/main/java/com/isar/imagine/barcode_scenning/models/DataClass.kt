package com.isar.imagine.barcode_scenning.models

import android.os.Parcel
import android.os.Parcelable

data class BillingDataModel(
    val inventoryId : String,
    val brand: String,
    val model: String,
    val variant: String,
    val condition: String,
    val imei1 : String,
    val imei2 : String,
    var purchasePrice: String,
    var sellingPrice: String,
    var quantity: Long,
    var notes: String,
    val serialNumber: String
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readLong(),
        parcel.readString()!!,
        parcel.readString()!!
    )

    override fun writeToParcel(parcel: Parcel, flag: Int) {
        parcel.writeString(inventoryId)
        parcel.writeString(brand)
        parcel.writeString(model)
        parcel.writeString(variant)
        parcel.writeString(condition)
        parcel.writeString(imei1)
        parcel.writeString(imei2)
        parcel.writeString(purchasePrice)
        parcel.writeString(sellingPrice)
        parcel.writeLong(quantity)
        parcel.writeString(notes)
        parcel.writeString(serialNumber)
    }

    override fun describeContents(): Int {
        return 0
    }

    companion object CREATOR : Parcelable.Creator<BillingDataModel> {
        override fun createFromParcel(p0: Parcel?): BillingDataModel {
            return BillingDataModel(p0!!)
        }

        override fun newArray(size: Int): Array<BillingDataModel?> {
            return arrayOfNulls(size)
        }
    }
}

fun mapToBillingDataModel(data: Map<String, Any?>,id : String): BillingDataModel {
    return BillingDataModel(
        inventoryId = id,
        brand = data["brand"] as? String ?: "",
        model = data["model"] as? String ?: "",
        variant = data["variant"] as? String ?: "",
        condition = data["condition"] as? String ?: "",
        purchasePrice = data["purchasePrice"] as? String ?: "0.0",
        sellingPrice = data["sellingPrice"] as? String ?: "0.0",
        quantity = data["quantity"] as? Long ?: 0,
        notes = data["notes"] as? String ?: "",
        serialNumber = data["serialNumber"] as? String ?: "",
        imei1 = data["imei1"] as? String?: "",
        imei2 = data["imei2"] as? String ?: ""
    )
}
