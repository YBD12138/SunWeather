package com.example.sunweather.logic.dao

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import com.example.sunweather.SunWeatherApplication
import com.example.sunweather.logic.model.Place
import com.google.gson.Gson

/**
 * 存取选中城市的单例类
 */
object PlaceDao {
    fun savePlace(place: Place){
        sharedPreferences().edit{
            putString("place",Gson().toJson(place))
        }
    }

    fun getSavedPlace(): Place{
        val placeJson = sharedPreferences().getString("place","")
        return Gson().fromJson(placeJson,Place::class.java)
    }

    fun isPlaceSaved() = sharedPreferences().contains("place")

    private fun sharedPreferences() = SunWeatherApplication.context.
        getSharedPreferences("sunny_weather",Context.MODE_PRIVATE)
}