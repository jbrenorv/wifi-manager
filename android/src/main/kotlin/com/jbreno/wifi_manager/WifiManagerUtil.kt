package com.jbreno.wifi_manager

import android.annotation.SuppressLint
import android.content.Context
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.net.NetworkRequest
import android.net.wifi.WifiConfiguration
import android.net.wifi.WifiNetworkSpecifier
import android.net.wifi.WifiInfo
import android.net.wifi.WifiManager
import android.os.Build
import com.jbreno.wifi_manager.models.WifiCredentials
import com.jbreno.wifi_manager.models.WifiSecurityType

class WifiManagerUtil(private val context: Context) {
    private var wifiManager : WifiManager = context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager

    @SuppressLint("MissingPermission")
    fun getConnectionInfo(): String {
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

        return "New:\nID: ${wifiInfo?.networkId}\nSSID: ${wifiInfo?.ssid}\n$wifiInfoToString\n\n$networkCapabilitiesToString"
    }

    fun requestWifi(wifiCredentials: WifiCredentials) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

            val wifiNetworkSpecifier = WifiNetworkSpecifier.Builder().setSsid(wifiCredentials.ssid)
            if (wifiCredentials.hasSecurity) {
                when (wifiCredentials.wifiSecurityType) {
                    WifiSecurityType.WPA2 -> wifiNetworkSpecifier.setWpa2Passphrase(wifiCredentials.password!!)
                    WifiSecurityType.WPA3 -> wifiNetworkSpecifier.setWpa3Passphrase(wifiCredentials.password!!)
                    else -> {}
                }
            }
            val networkRequest = NetworkRequest.Builder()
                .addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
                .setNetworkSpecifier(wifiNetworkSpecifier.build()).build()
            val connectivityManager = context
                .getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

            connectivityManager.requestNetwork(networkRequest, ConnectivityManager.NetworkCallback())

        } else {

            val conf = WifiConfiguration()
            conf.SSID = "\"${wifiCredentials.ssid}\""
            if (wifiCredentials.hasSecurity) conf.preSharedKey = "\"${wifiCredentials.password}\""
            val netId = wifiManager.addNetwork(conf)
            wifiManager.enableNetwork(netId, true)

        }
    }
}
