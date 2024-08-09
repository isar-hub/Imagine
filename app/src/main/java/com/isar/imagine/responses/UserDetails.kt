package com.isar.imagine.responses

import com.google.gson.annotations.SerializedName
import okhttp3.MultipartBody

data class UserDetails(
    @SerializedName("_id") val id: String,
    val name: String,
    val email: String,
    val mobile: Long,
    val address: String,
    val city: String,
    val state: String,
    val zipcode: Int,
    val image: String,
    val signature: String,
)

data class creaeteUser(
    val name: String,
    val email: String,
    val mobile: Long,
    val address: String,
    val city: String,
    val state: String,
    val zipcode: Long,
    val image: String,
    val signature: String
)