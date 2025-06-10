package com.nadhya0065.catatankuliner.ui.screen

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.nadhya0065.catatankuliner.network.KulinerApi
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainViewModel : ViewModel(){
    init {
        retriveData()
    }
    private fun retriveData(){
        viewModelScope.launch(Dispatchers.IO) {
            try {
                val result = KulinerApi.service.getKuliner()
                Log.d("MainViewModel", "Success: $result")
            } catch (e: Exception){
                Log.d("MainViewModel", "Failure: ${e.message}")
            }
        }
    }

}