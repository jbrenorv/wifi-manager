package com.jbreno.wifi_manager

import android.annotation.SuppressLint
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.wifi.*
import android.os.Build
import android.widget.Toast
import androidx.annotation.RequiresApi
import com.jbreno.wifi_manager.models.WifiCredentials
import io.flutter.Log

class WifiManagerUtil(private val context: Context) {
    private var wifiManager: WifiManager =
        context.applicationContext.getSystemService(Context.WIFI_SERVICE) as WifiManager
    private var lastSuggestedNetwork: WifiNetworkSuggestion? = null

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

        val connectivityManager =
            context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

        val networkCapabilities =
            connectivityManager.getNetworkCapabilities(connectivityManager.activeNetwork)

        val wifiInfo = networkCapabilities?.transportInfo as WifiInfo?
        val networkCapabilitiesToString = "NCP: $networkCapabilities"
        val wifiInfoToString = "WI: $wifiInfo"

        return "New:\nID: ${wifiInfo?.networkId}\nSSID: ${wifiInfo?.ssid}\n$wifiInfoToString\n\n$networkCapabilitiesToString"
    }

    fun requestWifi(wifiCredentials: WifiCredentials) {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

            connectUsingNetworkSuggestion(
                wifiCredentials.ssid, wifiCredentials.password ?: ""
            )

        } else {

            val conf = WifiConfiguration()
            conf.SSID = "\"${wifiCredentials.ssid}\""
            if (wifiCredentials.hasSecurity) conf.preSharedKey = "\"${wifiCredentials.password}\""
            val netId = wifiManager.addNetwork(conf)
            wifiManager.enableNetwork(netId, true)

        }
    }

    @SuppressLint("MissingPermission")
    @RequiresApi(Build.VERSION_CODES.Q)
    private fun connectUsingNetworkSuggestion(ssid: String, password: String) {
        val wifiNetworkSuggestion =
            WifiNetworkSuggestion.Builder().setSsid(ssid).setWpa2Passphrase(password).build()

        // Optional (Wait for post connection broadcast to one of your suggestions)
        val intentFilter = IntentFilter(WifiManager.ACTION_WIFI_NETWORK_SUGGESTION_POST_CONNECTION)

        val broadcastReceiver = object : BroadcastReceiver() {
            override fun onReceive(context: Context, intent: Intent) {
                if (!intent.action.equals(WifiManager.ACTION_WIFI_NETWORK_SUGGESTION_POST_CONNECTION)) {
                    return
                }
                showToast("Connection Suggestion Succeeded")
                // do post connect processing here
            }
        }

        context.registerReceiver(broadcastReceiver, intentFilter)

        lastSuggestedNetwork?.let {
            val status = wifiManager.removeNetworkSuggestions(listOf(it))
            Log.i("WifiNetworkSuggestion", "Removing Network suggestions status is $status")
        }
        val suggestionsList = listOf(wifiNetworkSuggestion)
        var status = wifiManager.addNetworkSuggestions(suggestionsList)
        Log.i("WifiNetworkSuggestion", "Adding Network suggestions status is $status")
        if (status == WifiManager.STATUS_NETWORK_SUGGESTIONS_ERROR_ADD_DUPLICATE) {
            showToast("Suggestion Update Needed")
            status = wifiManager.removeNetworkSuggestions(suggestionsList)
            Log.i("WifiNetworkSuggestion", "Removing Network suggestions status is $status")
            status = wifiManager.addNetworkSuggestions(suggestionsList)
        }
        if (status == WifiManager.STATUS_NETWORK_SUGGESTIONS_SUCCESS) {
            lastSuggestedNetwork = wifiNetworkSuggestion
            showToast("Suggestion Added")
        }
    }

    //@SuppressLint("MissingPermission")
    @RequiresApi(Build.VERSION_CODES.Q)
    fun connectUsingWifiEasyConnect(activity: Activity) {
        activity.startActivityForResult(
            Intent(android.provider.Settings.ACTION_PROCESS_WIFI_EASY_CONNECT_URI),
            1237
        )
    }

    private fun showToast(text: CharSequence) {
        Toast.makeText(context, text, Toast.LENGTH_LONG).show()
    }
}
