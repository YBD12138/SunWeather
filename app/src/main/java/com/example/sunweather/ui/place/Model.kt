package com.example.sunweather.ui.place

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.example.sunweather.logic.network.NetWorkCon

class Model :ViewModel(){
    val Statuss = MutableLiveData<Boolean>()

    fun Change(statuapp: Boolean){
        Statuss.value=statuapp
        Log.d("go","到了呀"+Statuss.value)
    }
}