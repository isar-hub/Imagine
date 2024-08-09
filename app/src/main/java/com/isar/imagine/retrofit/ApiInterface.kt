package com.isar.imagine.retrofit

import com.isar.imagine.data.model.BrandNamesResponse
import com.isar.imagine.data.model.InventoryItem
import com.isar.imagine.data.model.ItemWithSerialResponse
import com.isar.imagine.data.model.VariantsAndColorsResponse
import com.isar.imagine.responses.BrandNameResponseItem
import com.isar.imagine.responses.InventoryCountResponses
import com.isar.imagine.responses.UserDetails
import com.isar.imagine.responses.creaeteUser
import okhttp3.ResponseBody
import retrofit2.Call
import retrofit2.http.Body
import retrofit2.http.GET
import retrofit2.http.Headers
import retrofit2.http.POST
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiInterface {


    @GET("all-brands/")
    @Headers(
        "x-rapidapi-key: e44e12cd54msh12d3eef5ca57bfap13fb92jsnc6fc0b156adc",
        "x-rapidapi-host: mobile-phone-specs-database.p.rapidapi.com"
    )
    fun getExampleData(): Call<ArrayList<BrandNameResponseItem>>




    @Headers("Content-Type: application/json")
    @POST("users/")
    fun createCustomer(@Body customer: creaeteUser): Call<ResponseBody>

    @GET("users/{id}")
    fun getUserId(@Path("id") id: String): Call<UserDetails>


    @GET("users")
    fun getAllUsers(@Path("id") id: String): Call<UserDetails>



    @GET("brands/totalBrand")
    fun getItemCount(): Call<InventoryCountResponses>

    @GET("brands/")
    fun getBrandNames(): Call<BrandNamesResponse>

    @GET("brands/{brandName}")
    fun getModels(@Path("brandName") brandName: String): Call<BrandNamesResponse>

    @GET("brands/{brandName}/{brandModel}")
    fun getVariantsAndColors(
        @Path("brandName") brandName: String,
        @Path("brandModel") brandModel: String
    ): Call<VariantsAndColorsResponse>


    @GET("/inventory/{serialNum}")
    fun getItemWithId(
        @Path("serialNum") serialNum : String
    ): Call<ItemWithSerialResponse>

    @POST("/inventory")
    fun addNewInventoryItem(
        @Body items : MutableList<InventoryItem>
    ):Call<ResponseBody>

}