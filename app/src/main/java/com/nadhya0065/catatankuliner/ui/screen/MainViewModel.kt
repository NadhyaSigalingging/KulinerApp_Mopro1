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

class MainViewModel : ViewModel(){

    var data = mutableStateOf(emptyList<Kuliner>())

    var status = MutableStateFlow(ApiStatus.LOADING)
        private set

    var errorMessage = mutableStateOf<String?>(null)
        private set

    init {
        retriveData()
    }
    fun retriveData(){
        viewModelScope.launch(Dispatchers.IO) {
            status.value = ApiStatus.LOADING
            try {
                data.value = KulinerApi.service.getKuliner()
                status.value = ApiStatus.SUCCESS
            } catch (e: Exception){
                Log.d("MainViewModel", "Failure: ${e.message}")
                status.value = ApiStatus.FAILED
            }
        }
    }
    fun saveData(userId: String,namakuliner:String,lokasi: String,review: String,bitmap: Bitmap){
        viewModelScope.launch (Dispatchers.IO){
            try {
                val result = KulinerApi.service.postKuliner(
                    userId,
                    namakuliner.toRequestBody("text/plain".toMediaTypeOrNull()),
                    lokasi.toRequestBody("text/plain".toMediaTypeOrNull()),
                    review.toRequestBody("text/plain".toMediaTypeOrNull()),
                    bitmap.toMultipartBody()
                )
                if (result.status == "Succes")
                    retriveData()
                else
                    throw Exception(result.message)
            }catch (e: Exception){
                Log.d("MainViewModel", "Failure: ${e.message}")
                errorMessage.value = "Error: ${e.message}"
            }
        }
    }
    
    private fun Bitmap.toMultipartBody(): MultipartBody.Part{
        val stream = ByteArrayOutputStream()
        compress(Bitmap.CompressFormat.JPEG,80,stream)
        val byteArray = stream.toByteArray()
        val requestBody = byteArray.toRequestBody(
            "image/jpg".toMediaTypeOrNull(),0,byteArray.size)
        return MultipartBody.Part.createFormData(
            "image","image.jpg",requestBody
        )
    }
    fun cleareMassage(){errorMessage.value = null}

}