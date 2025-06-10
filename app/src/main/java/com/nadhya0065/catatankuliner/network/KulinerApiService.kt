package com.nadhya0065.catatankuliner.network

import retrofit2.Retrofit
import retrofit2.converter.scalars.ScalarsConverterFactory
import retrofit2.http.GET


private const val BASE_URL = "https://store.sthresearch.site/"

private val retrofit = Retrofit.Builder()
    .addConverterFactory(ScalarsConverterFactory.create())
    .baseUrl(BASE_URL)
    .build()

interface KulinerApiService {
    @GET("food_review.php")
    suspend fun getKuliner(): String
}

object KulinerApi{
    val service:KulinerApiService by lazy {
        retrofit.create(KulinerApiService::class.java)
    }
}