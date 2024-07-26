package com.isar.imagine.retrofit

import com.isar.imagine.responses.BrandNameResponseItem
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Headers

interface ApiInterface {


    @GET("all-brands/")
    @Headers(
        "x-rapidapi-key: e44e12cd54msh12d3eef5ca57bfap13fb92jsnc6fc0b156adc",
        "x-rapidapi-host: mobile-phone-specs-database.p.rapidapi.com"
    )
    fun getExampleData(): Call<ArrayList<BrandNameResponseItem>>
}