package com.nadhya0065.catatankuliner.ui.screen

import android.graphics.Bitmap
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nadhya0065.catatankuliner.model.Kuliner
import com.nadhya0065.catatankuliner.network.ApiStatus
import com.nadhya0065.catatankuliner.network.KulinerApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaTypeOrNull
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.toRequestBody
import java.io.ByteArrayOutputStream

class MainViewModel : ViewModel() {

    var data = mutableStateOf(emptyList<Kuliner>())
    var status = MutableStateFlow(ApiStatus.LOADING)
        private set

    var errorMessage = mutableStateOf<String?>(null)
        private set

    fun retriveData(userId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            status.value = ApiStatus.LOADING
            try {
                data.value = KulinerApi.service.getKuliner(userId)
                status.value = ApiStatus.SUCCESS
            } catch (e: Exception) {
                Log.e("MainViewModel", "Failure: ${e.message}")
                status.value = ApiStatus.FAILED
            }
        }
    }

    fun saveData(
        userId: String,
        nama_makanan: String,
        lokasi: String,
        review: String,
        bitmap: Bitmap
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = KulinerApi.service.postKuliner(
                    userId,
                    nama_makanan.toRequestBody("text/plain".toMediaTypeOrNull()),
                    lokasi.toRequestBody("text/plain".toMediaTypeOrNull()),
                    review.toRequestBody("text/plain".toMediaTypeOrNull()),
                    bitmap.toMultipart("image")
                )
                if (result.status == "success") {
                    retriveData(userId)
                } else {
                    throw Exception(result.message)
                }
            } catch (e: Exception) {
                Log.e("MainViewModel", "Failure: ${e.message}")
                errorMessage.value = "Error: ${e.message}"
            }
        }
    }

    fun editData(
        id: String,
        userId: String,
        nama_makanan: String,
        lokasi: String,
        review: String,
        bitmap: Bitmap?
    ) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = if (bitmap != null) {
                    KulinerApi.service.editKulinerWithImage(
                        userId = userId,
                        id = id.toRequestBody("text/plain".toMediaTypeOrNull()),
                        nama_makanan = nama_makanan.toRequestBody("text/plain".toMediaTypeOrNull()),
                        lokasi = lokasi.toRequestBody("text/plain".toMediaTypeOrNull()),
                        review = review.toRequestBody("text/plain".toMediaTypeOrNull()),
                        image = bitmap.toMultipart("image")
                    )
                } else {
                    KulinerApi.service.editKulinerWithoutImage(
                        userId = userId,
                        id = id,
                        nama_makanan = nama_makanan,
                        lokasi = lokasi,
                        review = review
                    )
                }

                if (result.status == "success") {
                    retriveData(userId)
                } else {
                    throw Exception(result.message)
                }

            } catch (e: Exception) {
                Log.e("EDIT_ERROR", "Gagal edit data", e)
                errorMessage.value = "Error: ${e.message}"
            }
        }
    }

    fun deleteKuliner(idKuliner: String, userId: String) {
        viewModelScope.launch(Dispatchers.IO) {
            try {
                KulinerApi.service.deleteKuliner(userId, idKuliner)
                retriveData(userId)
            } catch (e: Exception) {
                Log.e("DELETE_ERROR", "Gagal hapus data: $idKuliner", e)
                errorMessage.value = "Gagal menghapus data"
            }
        }
    }

    private fun Bitmap.toMultipart(paramName: String): MultipartBody.Part {
        val stream = ByteArrayOutputStream()
        this.compress(Bitmap.CompressFormat.JPEG, 90, stream)
        val byteArray = stream.toByteArray()
        val requestBody = byteArray.toRequestBody("image/*".toMediaTypeOrNull())
        return MultipartBody.Part.createFormData(paramName, "$paramName.jpg", requestBody)
    }

    fun clearMessage() {
        errorMessage.value = null
    }
}
