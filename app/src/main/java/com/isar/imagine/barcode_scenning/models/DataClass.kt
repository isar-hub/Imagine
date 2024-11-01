package com.isar.imagine.barcode_scenning.models

import android.os.Parcel
import android.os.Parcelable

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
) : Parcelable {
    constructor(parcel: Parcel) : this(
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readString()!!,
        parcel.readDouble(),
        parcel.readDouble(),
        parcel.readLong(),
        parcel.readString()!!,
        parcel.readString()!!
    )

    override fun writeToParcel(parcel: Parcel, flag: Int) {
        parcel.writeString(brand)
        parcel.writeString(model)
        parcel.writeString(variant)
        parcel.writeString(condition)
        parcel.writeDouble(purchasePrice)
        parcel.writeDouble(sellingPrice)
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
