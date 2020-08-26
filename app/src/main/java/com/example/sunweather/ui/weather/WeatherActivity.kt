package com.example.sunweather.ui.weather

import android.content.*
import android.graphics.Color
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.IBinder
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.inputmethod.InputMethodManager
import android.widget.ImageView
import android.widget.TextView
import android.widget.Toast
import androidx.core.content.edit
import androidx.core.view.GravityCompat
import androidx.drawerlayout.widget.DrawerLayout
import androidx.lifecycle.Observer
import androidx.lifecycle.ViewModelProvider
import com.example.sunweather.BaseActivity
import com.example.sunweather.R
import com.example.sunweather.SunWeatherApplication
import com.example.sunweather.logic.Repository
import com.example.sunweather.logic.dao.MyService
import com.example.sunweather.logic.model.Weather
import com.example.sunweather.logic.model.getSky
import com.example.sunweather.logic.network.NetWorkCon
import com.example.sunweather.ui.place.Model
import com.google.gson.Gson
import kotlinx.android.synthetic.main.activity_weather.*
import kotlinx.android.synthetic.main.forecast.*
import kotlinx.android.synthetic.main.forecast_item.*
import kotlinx.android.synthetic.main.life_index.*
import kotlinx.android.synthetic.main.now.*
import java.text.SimpleDateFormat
import java.util.*

/**
 * 请求天气数据，并展示到UI界面上
 */
class WeatherActivity : BaseActivity() {

    var resh  = false
    val viewModel by lazy { ViewModelProvider(this).get(WeatherViewModel::class.java) }
    val sp:SharedPreferences = SunWeatherApplication.context.
        getSharedPreferences("tianqitongzhi",Context.MODE_PRIVATE)
    //绑定Activity和Service
    lateinit var downloadBiner : MyService.DownloadBinder

    private val connection = object : ServiceConnection{
        override fun onServiceDisconnected(name: ComponentName?) {}

        override fun onServiceConnected(name: ComponentName?, service: IBinder?) {
            downloadBiner = service as MyService.DownloadBinder
            downloadBiner.Change()
        }

    }
    //绑定Activity和Service

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        //背景图和状态栏融合到一起
        val decorView = window.decorView
        decorView.systemUiVisibility =
                View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN or
                        View.SYSTEM_UI_FLAG_LAYOUT_STABLE
        window.statusBarColor = Color.TRANSPARENT
        //背景图和状态栏融合到一起
        setContentView(R.layout.activity_weather)

        startmyService()//启动和绑定服务

        if(viewModel.locationLng.isEmpty()){
            viewModel.locationLng = intent.getStringExtra("location_lng") ?: ""
            Log.d("lng",viewModel.locationLng)
        }
        if (viewModel.locationLat.isEmpty()){
            viewModel.locationLat = intent.getStringExtra("location_lat") ?: ""
            Log.d("lat",viewModel.locationLat)
        }
        if (viewModel.placeName.isEmpty()){
            viewModel.placeName = intent.getStringExtra("place_name") ?: ""
            Log.d("lat",viewModel.placeName)
            sp.edit{
                putString("placename",viewModel.placeName)
                apply()
            }
        }
        viewModel.wetherLiveData.observe(this, Observer { result->
            val weather = result.getOrNull()
            if(weather!=null){
                resh = true
                showWeatherInfo(weather)
            }else{
                resh = false
                Toast.makeText(this,"刷新失败\n无法成功获取天气信息",Toast.LENGTH_SHORT).show()
                result.exceptionOrNull()?.printStackTrace()
            }
            swipeRefresh.isRefreshing = false
            if (resh)
            Toast.makeText(this,"刷新成功",Toast.LENGTH_SHORT).show()
        })
        //设置下拉刷新的
        swipeRefresh.setColorSchemeResources(R.color.colorPrimary)
        refreshWeather()
        swipeRefresh.setOnRefreshListener {
            refreshWeather()
        }
        //设置下拉刷新的
        //--------------------------------------------//
        //滑动菜单的逻辑
        navBtn.setOnClickListener {
            drawerLayout.openDrawer(GravityCompat.START)
        }
        drawerLayout.addDrawerListener(object: DrawerLayout.DrawerListener{
            override fun onDrawerStateChanged(newState: Int) {}

            override fun onDrawerSlide(drawerView: View, slideOffset: Float) {}

            override fun onDrawerOpened(drawerView: View) {}
            //关闭滑动菜单的时候也要关闭输入法
            override fun onDrawerClosed(drawerView: View) {
                val manager = getSystemService(Context.INPUT_METHOD_SERVICE)
                        as InputMethodManager
                manager.hideSoftInputFromWindow(drawerView.windowToken,
                    InputMethodManager.HIDE_NOT_ALWAYS)
            }
        })
        //滑动菜单的逻辑
    }

    private fun startmyService() {
        //启动服务Service
        val intent = Intent(this,MyService::class.java)
        startService(intent)
        //*******绑定Activity和Service
        bindService(intent,connection,Context.BIND_AUTO_CREATE)
        //*******绑定Activity和Service
        //启动服务Service
    }

    fun refreshWeather() {
        resh = true
        Log.d("网络状态",NetWorkCon().STATS.toString())
        viewModel.refreshWeather(viewModel.locationLng,viewModel.locationLat)
        swipeRefresh.isRefreshing = true
    }

    private fun showWeatherInfo(weather: Weather) {

        val placenname = viewModel.placeName
        placeName.text = placenname
        val realtime = weather.realtime
        val daily = weather.daily
        //填充now.xml布局中的数据
        val currentTempText = "${realtime.temperature.toInt()}°C"
        currentTemp.text = currentTempText
        val currsky = getSky(realtime.skycon).info
        currentSky.text = currsky
        val currentPM25Text = "空气指数 ${realtime.airQuality.aqi.chn.toInt()}"
        currentAQI.text = currentPM25Text
        nowLayout.setBackgroundResource(getSky(realtime.skycon).bg)
        //填充forecast.sml布局中的数据
        foreastLayout.removeAllViews()
        val days = daily.skycon.size
        for(i in 0 until days){
            val skycon = daily.skycon[i]
            val temperature = daily.temperature[i]
            val view = LayoutInflater.from(this).inflate(R.layout.forecast_item,
                foreastLayout,false)
            val dateInfo = view.findViewById(R.id.dateInfo) as TextView
            val skyIcon = view.findViewById(R.id.skyIcon) as ImageView
            val skyInfo = view.findViewById(R.id.skyInfo) as TextView
            val temperatureInfo = view.findViewById(R.id.temperatureInfo) as TextView
            val simpleDateFormat = SimpleDateFormat("yyyy-MM-dd", Locale.getDefault())
            dateInfo.text = simpleDateFormat.format(skycon.date)
            val sky = getSky(skycon.value)
            skyIcon.setImageResource(sky.icon)
            skyInfo.text = sky.info
            val tempText = "${temperature.min.toInt()} ~ ${temperature.max.toInt()}°C"
            temperatureInfo.text = tempText
            foreastLayout.addView(view)
        }
        //填充life_index.xml布局中的数据
        val lifeIndex = daily.lifeIndex
        coldRiskText.text = lifeIndex.coldRisk[0].desc
        dressingText.text = lifeIndex.dressing[0].desc
        ultravioletText.text = lifeIndex.ultraviolet[0].desc
        carWashingText.text = lifeIndex.carWashing[0].desc
        weatherLayout.visibility = View.VISIBLE
        //保存到本地缓存中
        sp.edit {
            putString("tianqi",temperatureInfo.text.toString())
            putString("riqi","${dateInfo.text.toString()}")
            putString("tianqitubiao", weather.daily.skycon[1].value.toString())
            apply()
        }
    }


    override fun onDestroy() {
//        //解绑Activity和Service
//        unbindService(connection)
//        //停止服务Service
//        val intent = Intent(this,MyService::class.java)
//        stopService(intent)
//        //停止服务Service
        super.onDestroy()
    }
}
