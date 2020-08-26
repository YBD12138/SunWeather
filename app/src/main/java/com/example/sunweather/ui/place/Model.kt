package com.example.sunweather.ui.place

import android.util.Log
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.sunweather.logic.Repository
import com.example.sunweather.logic.network.NetWorkCon

class Model :ViewModel(){
    val Statuss = MutableLiveData<Boolean>()


//    val placeLiveData = Transformations.switchMap(Statuss){
//        NetWorkCon().STATS
//    }
    fun Nice(boolean: Boolean){
        Statuss.value = boolean
    }
}