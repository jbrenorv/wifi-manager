package com.jbreno.wifi_manager

import android.Manifest
import android.content.Context
import android.content.pm.PackageManager
import android.net.ConnectivityManager
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Build
import androidx.core.app.ActivityCompat.requestPermissions
import androidx.core.content.ContextCompat

class WifiManagerUtil(private val context: Context) {
    private var wifiManager : WifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

    public fun getConnectionInfo(): String? {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            val wifiInfo = wifiManager.getConnectionInfo()
            return wifiInfo.ssid
        }

        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager
        val networkCapabilities = connectivityManager.getNetworkCapabilities(null)
        val wifiInfo = networkCapabilities?.transportInfo as WifiInfo?


        return wifiInfo?.ssid
    }
}
