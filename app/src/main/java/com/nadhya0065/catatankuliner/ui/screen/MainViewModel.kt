package com.nadhya0065.catatankuliner.ui.screen

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

class MainViewModel : ViewModel(){

    var data = mutableStateOf(emptyList<Kuliner>())
        private set

    var status = MutableStateFlow(ApiStatus.Loading)
        private set

    init {
        retriveData()
    }
    private fun retriveData(){
        viewModelScope.launch(Dispatchers.IO) {
            status.value = ApiStatus.Loading
            try {
                data.value = KulinerApi.service.getKuliner()
                status.value = ApiStatus.Success
            } catch (e: Exception){
                Log.d("MainViewModel", "Failure: ${e.message}")
            }
        }
    }

}