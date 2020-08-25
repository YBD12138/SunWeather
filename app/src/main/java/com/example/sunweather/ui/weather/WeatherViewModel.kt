package com.example.sunweather.ui.weather


import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Transformations
import androidx.lifecycle.ViewModel
import com.example.sunweather.logic.Repository

/**
 * UI界面的桥梁，ViewModel层
 */
class WeatherViewModel : ViewModel() {

    private val locationLiveData = MutableLiveData<com.example.sunweather.logic.model.Location>()

    var locationLng = ""
    var locationLat = ""
    var placeName = ""

    val wetherLiveData = Transformations.switchMap(locationLiveData){location ->
        Repository.refreshWeather(location.lng,location.lat)
    }

    fun refreshWeather(lng:String,lat:String){
        locationLiveData.value = com.example.sunweather.logic.model.Location(lng,lat)
    }
}