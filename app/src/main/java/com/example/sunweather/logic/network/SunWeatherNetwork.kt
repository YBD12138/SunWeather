package com.example.sunweather.logic.network

import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import retrofit2.await
import kotlin.coroutines.resume
import kotlin.coroutines.resumeWithException
import kotlin.coroutines.suspendCoroutine

/**
 * 网络数据源访问入口（网络层）
 */
object SunWeatherNetwork {
    private val placeService = ServiceCreator.create<PlaceService>()

    suspend fun searchPlaces(query:String) = placeService.searchPlaces(query).await()

    private val weatherService = ServiceCreator.create<WeatherService>()

    suspend fun getDailyWeather(lng:String,lat:String) = weatherService.
        getDailyWeather(lng, lat).await()

    suspend fun getRealtimeWeather(lng: String,lat: String) = weatherService
        .getRealtimeWeather(lng, lat).await()
    
    /**
     * //首先声明一个泛型 T ，并将await()函数定义成了Call<T>的扩展函数，
     * 这样所有返回值是Call类型的Retrofit网络请求接口就都可以直接调用await()函数了
     */
    private suspend fun <T> Call<T>.await(): T {
        return suspendCoroutine { continuation ->
            enqueue(object : Callback<T>{
                override fun onResponse(call:Call<T>, response: Response<T>){
                    val body = response.body()
                    if(body!=null)continuation.resume(body)
                    else continuation.resumeWithException(
                        RuntimeException("response body is null"))
                }
                override fun onFailure(call: Call<T>,t:Throwable){
                    continuation.resumeWithException(t)
                }
            })
        }
    }
}