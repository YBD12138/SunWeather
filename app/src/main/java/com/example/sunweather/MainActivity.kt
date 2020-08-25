package com.example.sunweather

import android.content.IntentFilter
import android.net.ConnectivityManager
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.example.sunweather.logic.network.NetWorkCon

class MainActivity : AppCompatActivity() {

    public lateinit var netWorkStats:NetWorkCon
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        netWorkStats = NetWorkCon()
        var filter = IntentFilter()
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION)
        registerReceiver(netWorkStats, filter)
    }

    override fun onDestroy() {
        unregisterReceiver(netWorkStats)
        super.onDestroy()
    }
}
