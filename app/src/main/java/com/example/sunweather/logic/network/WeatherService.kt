package com.example.sunweather.logic.network

import com.example.sunweather.SunWeatherApplication
import com.example.sunweather.logic.model.DailyResponse
import com.example.sunweather.logic.model.RealtimeResponse
import retrofit2.Call
import retrofit2.http.GET
import retrofit2.http.Path

/**
 * 请求天气信息的接口*(网络层)
 */
interface WeatherService {

    @GET("v2.5/${SunWeatherApplication.TOKEN}/{lng},{lat}/realtime.json")
    fun getRealtimeWeather(@Path("lng")lng:String,@Path("lat")lat:String):
            Call<RealtimeResponse>

    @GET("v2.5/${SunWeatherApplication.TOKEN}/{lng},{lat}/daily.json")
    fun getDailyWeather(@Path("lng")lng:String,@Path("lat")lat:String):
    Call<DailyResponse>

}