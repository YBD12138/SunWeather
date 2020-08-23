package com.example.sunweather.logic.network

import com.example.sunweather.SunWeatherApplication
import com.example.sunweather.logic.model.PlaceResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Query

/**
 * 搜索城市的API接口(网络层)
 */
interface PlaceService {

    @GET("v2/place?token=${SunWeatherApplication.TOKEN}&lang=zh_CN")
    fun searchPlaces(@Query("query") query: String): Call<PlaceResponse>
}