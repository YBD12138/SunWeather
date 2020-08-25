package com.example.sunweather.logic.model

/**
 * 用于封装实时天气和未来天气的数据模型
 */
data class Weather(val realtime: RealtimeResponse.Realtime,val daily: DailyResponse.Daily)