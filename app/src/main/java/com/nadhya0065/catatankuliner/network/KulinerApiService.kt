package com.nadhya0065.catatankuliner.network

import com.nadhya0065.catatankuliner.model.Kuliner
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET


private const val BASE_URL = "https://store.sthresearch.site/"

private val moshi = Moshi.Builder()
    .add(KotlinJsonAdapterFactory())
    .build()

private val retrofit = Retrofit.Builder()
    .addConverterFactory(MoshiConverterFactory.create(moshi))
    .baseUrl(BASE_URL)
    .build()

interface KulinerApiService {
    @GET("food_review.php")
    suspend fun getKuliner(): List<Kuliner>
}

object KulinerApi{
    val service:KulinerApiService by lazy {
        retrofit.create(KulinerApiService::class.java)
    }
    fun getKulinerUrl(imageId: String): String{
        return "$BASE_URL$imageId.jpg"
    }
}