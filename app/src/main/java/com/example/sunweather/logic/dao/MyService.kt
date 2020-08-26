package com.example.sunweather.logic.dao

import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.graphics.BitmapFactory
import android.os.Binder
import android.os.Build
import android.os.IBinder
import android.util.Log
import androidx.core.app.NotificationCompat
import androidx.lifecycle.ViewModelProvider
import com.example.sunweather.BaseActivity
import com.example.sunweather.MainActivity
import com.example.sunweather.R
import com.example.sunweather.SunWeatherApplication
import com.example.sunweather.logic.model.Place
import com.example.sunweather.logic.model.Weather
import com.example.sunweather.logic.model.getSky
import com.example.sunweather.ui.place.PlaceViewModel
import com.example.sunweather.ui.weather.WeatherActivity
import com.google.gson.Gson
import kotlinx.android.synthetic.main.now.*
import java.text.SimpleDateFormat
import java.util.*

class MyService : Service(){

    private val mBinder = DownloadBinder()

    var tianqi:String = ""
    var riqi : String = ""
    var tianqitubiao : String = ""


    lateinit var  sp: SharedPreferences

    class DownloadBinder : Binder(){

        fun Change(){
            Log.d("要做的","做了什么")
        }
    }

    override fun onBind(intent: Intent?): IBinder? {
        return mBinder
    }

    override fun onCreate() {
        super.onCreate()

        Log.d("服务Service","启动了")
    }

    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
        upWeatherInfo()
        val manager = getSystemService(Context.NOTIFICATION_SERVICE) as
                NotificationManager
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O){
            val channel = NotificationChannel("my_service","前台Service通知",
                NotificationManager.IMPORTANCE_DEFAULT)
            manager.createNotificationChannel(channel)
        }
        val intent = Intent(this,MainActivity::class.java)
        val pi = PendingIntent.getActivity(this,0,intent,0)
        val notification = NotificationCompat.Builder(this,"my_service")
            .setContentTitle(PlaceDao.getSavedPlace().name)
            .setContentText(riqi+"\t\t"+tianqi)
            .setSmallIcon(R.drawable.ic_clear_day)
            .setLargeIcon(BitmapFactory.decodeResource(resources, getSky(tianqitubiao).icon))
            .setContentIntent(pi)
            .build()
        startForeground(1,notification)
        return super.onStartCommand(intent, flags, startId)
    }

    private fun upWeatherInfo() {
        sp = SunWeatherApplication.context.
            getSharedPreferences("tianqitongzhi",Context.MODE_PRIVATE)
        tianqi = sp.getString("tianqi","")
        tianqitubiao = sp.getString("tianqitubiao","")
        Log.d("是啥",tianqitubiao+ getSky(tianqitubiao).bg)
        riqi = sp.getString("riqi","")

    }

    override fun onDestroy() {
        Log.d("服务","被销毁了")
        super.onDestroy()
    }
}