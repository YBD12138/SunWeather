package com.example.sunweather

import android.content.IntentFilter
import android.net.ConnectivityManager
import android.os.Bundle
import android.os.PersistableBundle
import androidx.appcompat.app.AppCompatActivity
import com.example.sunweather.logic.network.NetWorkCon

/**
 * 知晓自己处在哪一个Activity里面
 */
open class BaseActivity :AppCompatActivity(){

    lateinit var netWorkStats:NetWorkCon

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        ActivityCollector.addActivity(this)
        netWorkStats = NetWorkCon()
        var filter = IntentFilter()
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(netWorkStats, filter)
    }

    override fun onDestroy() {
        super.onDestroy()
        unregisterReceiver(netWorkStats)
        ActivityCollector.removeActivity(this)
    }
}