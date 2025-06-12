package com.nadhya0065.catatankuliner.network

import com.nadhya0065.catatankuliner.model.Kuliner
import com.nadhya0065.catatankuliner.model.OpStatus
import com.squareup.moshi.Moshi
import com.squareup.moshi.kotlin.reflect.KotlinJsonAdapterFactory
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Retrofit
import retrofit2.converter.moshi.MoshiConverterFactory
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part


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
    suspend fun getKuliner(
        @Header("Authorization")userId: String
    ): List<Kuliner>

    @Multipart
    @POST ("food_review.php")
    suspend fun postKuliner(
        @Header("Authorization") userId: String,
        @Part("nama_makanan") nama_makanan: RequestBody,
        @Part("lokasi") lokasi: RequestBody,
        @Part("review") review: RequestBody,
        @Part image: MultipartBody.Part
    ):OpStatus
}

object KulinerApi{
    val service:KulinerApiService by lazy {
        retrofit.create(KulinerApiService::class.java)
    }
    fun getKulinerUrl(imageId: String): String{
        return "$BASE_URL$imageId.jpg"
    }
}
enum class ApiStatus {LOADING, SUCCESS, FAILED}