package com.example.sunweather

import android.annotation.SuppressLint
import android.app.Application
import android.content.Context

class SunWeatherApplication : Application() {
    companion object{

        @SuppressLint("StaticFieldLeak")
        lateinit var context :Context
        const val TOKEN = "GFHV8jmH8Lua6ZAT"
    }

    override fun onCreate() {
        super.onCreate()
        context = applicationContext
    }
}