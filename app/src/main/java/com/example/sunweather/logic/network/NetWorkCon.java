package com.example.sunweather.logic.network;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkInfo;
import android.util.Log;
import android.widget.EditText;
import android.widget.Toast;

import androidx.lifecycle.LiveData;
import androidx.lifecycle.MutableLiveData;
import androidx.lifecycle.Transformations;
import androidx.lifecycle.ViewModelProvider;
import androidx.lifecycle.ViewModelProviders;

import com.example.sunweather.SunWeatherApplication;
import com.example.sunweather.ui.place.Model;
import com.example.sunweather.ui.place.PlaceFragment;
import com.example.sunweather.ui.weather.WeatherActivity;

import static androidx.core.content.ContextCompat.getSystemService;

public class NetWorkCon extends BroadcastReceiver {
    private final String TAG = "NetworkChangedReceiver";;
    public boolean STATS = true;
 //   private WeatherActivity weatherActivity = new WeatherActivity();
    @Override
    public void onReceive(Context context, Intent intent) {
        Log.i(TAG, "网络状态发生变化");

        //检测API是不是小于21，因为到了API 21之后getNetworkInfo(int networkType)方法被弃用
        if (android.os.Build.VERSION.SDK_INT < android.os.Build.VERSION_CODES.LOLLIPOP) {
            Log.i(TAG, "API level 小于21");

            //获得ConnectivityManager对象
            ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            //获取ConnectivityManager对象对应的NetworkInfo对象
            //获取WIFI连接的信息
            NetworkInfo wifiNetworkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            //获取移动数据连接的信息
            NetworkInfo dataNetworkInfo = connMgr.getNetworkInfo(ConnectivityManager.TYPE_MOBILE);
            if (wifiNetworkInfo.isConnected() && dataNetworkInfo.isConnected()) {
                Log.i(TAG, "WIFI已连接,移动数据已连接");
            } else if (wifiNetworkInfo.isConnected() && !dataNetworkInfo.isConnected()) {
                Log.i(TAG, "WIFI已连接,移动数据已断开");
            } else if (!wifiNetworkInfo.isConnected() && dataNetworkInfo.isConnected()) {
                Log.i(TAG, "WIFI已断开,移动数据已连接");
            } else {
                Log.i(TAG, "WIFI已断开,移动数据已断开");
            }
        } else {
            Log.i(TAG, "API level 大于21");

            //获得ConnectivityManager对象
            ConnectivityManager connMgr = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);

            //获取所有当前已有连接上状态的网络连接的信息
            Network[] networks = connMgr.getAllNetworks();

            //用于记录最后的网络连接信息
            int result = 0;//mobile false = 1, mobile true = 2, wifi = 4

            //通过循环将网络信息逐个取出来
            for (int i = 0; i < networks.length; i++) {
                //获取ConnectivityManager对象对应的NetworkInfo对象
                NetworkInfo networkInfo = connMgr.getNetworkInfo(networks[i]);

                //检测到有数据连接，但是并连接状态未生效，此种状态为wifi和数据同时已连接，以wifi连接优先
                if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE && !networkInfo.isConnected()) {
                    result += 1;
                }

                //检测到有数据连接，并连接状态已生效，此种状态为只有数据连接，wifi并未连接上
                if (networkInfo.getType() == ConnectivityManager.TYPE_MOBILE && networkInfo.isConnected()) {
                    result += 2;
                }

                //检测到有wifi连接，连接状态必为true
                if (networkInfo.getType() == ConnectivityManager.TYPE_WIFI) {
                    result += 4;
                }
            }

            //因为存在上述情况的组合情况，以组合相加的唯一值作为最终状态的判断
            switch (result) {
                case 0:
                    Log.i(TAG, "WIFI已断开,移动数据已断开");
                    Toast.makeText(SunWeatherApplication.context,"当前无网络链接",Toast.LENGTH_SHORT).show();
                    STATS = false;
//                    weatherActivity.getMymodel().Nice(STATS);
                    break;
                case 2:
                    Log.i(TAG, "WIFI已断开,移动数据已连接");

                case 4:
                    Log.i(TAG, "WIFI已连接,移动数据已断开");

                case 5:
                    Log.i(TAG, "WIFI已连接,移动数据已连接");
                    Toast.makeText(SunWeatherApplication.context,"当前网络连接正常",Toast.LENGTH_SHORT).show();
                    STATS = true;
//                    weatherActivity.getMymodel().Nice(STATS);
                    break;
            }
        }
    }
}
