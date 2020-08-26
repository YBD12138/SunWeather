package com.example.sunweather.logic

import android.content.Context
import android.content.SharedPreferences
import androidx.core.content.edit
import androidx.lifecycle.LiveData
import androidx.lifecycle.liveData
import com.example.sunweather.SunWeatherApplication
import com.example.sunweather.logic.dao.PlaceDao
import com.example.sunweather.logic.model.Place
import com.example.sunweather.logic.model.Weather
import com.example.sunweather.logic.network.SunWeatherNetwork
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.coroutineScope
import java.lang.RuntimeException
import kotlin.coroutines.CoroutineContext

/**
 * 统一封闭入口（仓库层）
 */
object Repository {

//    public var sp: SharedPreferences = SunWeatherApplication.context.
//        getSharedPreferences("weather_info", Context.MODE_PRIVATE)

    /**
     * 搜索城市的挂起函数，返回的LiveData对象用于观察
     */
    fun searchPlaces(query: String) = fire(Dispatchers.IO) {
            val placeResponse = SunWeatherNetwork.searchPlaces(query)
            if(placeResponse.status == "ok"){
                val places = placeResponse.places
                Result.success(places)
            }
            else{
                Result.failure(RuntimeException("response status is${placeResponse.status}"))
            }
    }

    /**
     * 请求天气的挂起函数，用于观察天气信息数据的返回
     */
    fun refreshWeather(lng:String,lat:String) = fire(Dispatchers.IO){
            coroutineScope{
                val deferredRealtime = async {
                    SunWeatherNetwork.getRealtimeWeather(lng, lat)
                }
                val deferredDaily = async {
                    SunWeatherNetwork.getDailyWeather(lng, lat)
                }
                val realtimeResponse = deferredRealtime.await()
                val dailyResponse = deferredDaily.await()
                if (realtimeResponse.status == "ok" && dailyResponse.status == "ok"){
                    val weather = Weather(realtimeResponse.result.realtime,
                        dailyResponse.result.daily)
//                    sp.edit {
//                        putString("weatherInfo",Gson().toJson(weather))
//                    }
                    Result.success(weather)
                }else{
                    Result.failure(
                        RuntimeException(
                            "realtime response status is${realtimeResponse.status}"+
                                    "daily response status is ${dailyResponse.status}"
                        )
                    )
                }
            }
    }

    /**
     * 在统一的入口函数中进行封装，只进行一次try~catch就Ok了
     */
    private fun<T> fire(context: CoroutineContext,block :suspend () ->Result<T>) =
        liveData<Result<T>>(context){
            val result = try{
                block()
            }catch (e:Exception){
                Result.failure<T>(e)
            }
            emit(result)
        }

    /**
     * 以下三种方法标准写法是像上面一样，开启子线程进行数据的存取
     */
    fun savePlace(place: Place) = PlaceDao.savePlace(place)

    fun getSavedPlace() = PlaceDao.getSavedPlace()

    fun isPlaceSaved() = PlaceDao.isPlaceSaved()
}
