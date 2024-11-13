package com.isar.imagine.data.model

import android.os.Parcelable
import kotlinx.android.parcel.Parcelize

@Parcelize
data class BarcodeField(val label: String, val value: String,val isEditable : Boolean = false) : Parcelable
