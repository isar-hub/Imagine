package com.isar.imagine.retrofit
import okhttp3.OkHttpClient
import okhttp3.logging.HttpLoggingInterceptor
import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

object RetrofitInstance{
    //
    private const val BASE_URL ="https://restapitutorial-production.up.railway.app/"


    //https://restapitutorial-production.up.railway.app/
    fun getInstance(): Retrofit {
        val client = OkHttpClient()
        val interceptor = HttpLoggingInterceptor().setLevel(HttpLoggingInterceptor.Level.BODY)
        val clientBuilder: OkHttpClient.Builder =
            client.newBuilder().addInterceptor(interceptor)

        return Retrofit.Builder().baseUrl(BASE_URL)
            .addConverterFactory(GsonConverterFactory.create())
            .client(clientBuilder.build())
            .build()
    }



//
//     fun getApiInterface(): ApiInterface {
//       return getInstance().create(ApiInterface::class.java)
//    }
}