package com.jbreno.wifi_manager

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.Network
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Build

class WifiManagerUtil(private val context: Context) {
    private var wifiManager : WifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

    @SuppressLint("MissingPermission")
    fun getConnectionInfo(): String? {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.S) {
            val wifiInfo = wifiManager.connectionInfo
            var connectedTo = "*** Connected to: ID=${wifiInfo.networkId} "
            var configuredNetworks = ""
            wifiManager.configuredNetworks.forEach {
                if (it.networkId == wifiInfo.networkId) {
                    connectedTo += "SSID=${it.SSID}"
                }
                configuredNetworks += "SSID: ${it.SSID} - ID: ${it.networkId}\n"
            }

            return "Old - $connectedTo \n\n $wifiInfo \n\n $configuredNetworks"
        }

        val connectivityManager = context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

//        val network = context.getSystemService(Context.NETWORK_STATS_SERVICE)
//        val network = Network()

        val networkCapabilities = connectivityManager
            .getNetworkCapabilities(connectivityManager.activeNetwork)

        val wifiInfo = networkCapabilities?.transportInfo as WifiInfo?
        val networkCapabilitiesToString = "NCP: $networkCapabilities"
        val wifiInfoToString = "WI: $wifiInfo"

        return "New - " + wifiInfo?.toString() + "\n\n$networkCapabilitiesToString\n\n$wifiInfoToString"
    }
}
