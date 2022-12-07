package com.jbreno.wifi_manager

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Build

class WifiManagerUtil(private val context: Context) {
    private var wifiManager : WifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

    @SuppressLint("MissingPermission")
    public fun getConnectionInfo(): String? {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            val wifiInfo = wifiManager.getConnectionInfo()
            return "Old - " + wifiInfo.ssid
        }

        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkCapabilities = connectivityManager.getNetworkCapabilities(null)
        val wifiInfo = networkCapabilities?.transportInfo as WifiInfo?


        return "New - " + wifiInfo?.ssid
    }
}
