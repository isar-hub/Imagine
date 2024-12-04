package com.isar.imagine.barcode.data

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class BarcodeField(val label: String, val value: String,val isEditable : Boolean = false) : Parcelable
