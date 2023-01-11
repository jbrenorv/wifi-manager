package com.jbreno.wifi_manager

import android.annotation.SuppressLint
import android.app.Activity
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.net.ConnectivityManager
import android.net.Network
import android.net.NetworkCapabilities
import android.net.NetworkRequest
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

    fun showToast(text: CharSequence) {
        Toast.makeText(context, text, Toast.LENGTH_LONG).show()
    }

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

            return "Old - $connectedTo \n\n $wifiInfo \n\n$configuredNetworks"
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

    fun openWifiSettings(activity: Activity): Boolean {
        val intent = Intent(android.provider.Settings.ACTION_WIFI_SETTINGS)
        if (intent.resolveActivity(context.packageManager) != null) {
            activity.startActivity(intent)
            return true
        }
        return false
    }

    fun requestWifi(wifiCredentials: WifiCredentials): Boolean {
        val ssid = wifiCredentials.ssid
        val password = wifiCredentials.password ?: ""

        return if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {

            requestWifiApi30AndLater(ssid, password)
            // connectUsingNetworkSuggestion(ssid, password)

        } else {

            requestWifiApiLessThan30(ssid, password)

        }
    }

    private fun requestWifiApiLessThan30(ssid: String, password: String): Boolean {
        val wifiConfig = WifiConfiguration().apply {
            SSID = "\"$ssid\""
            preSharedKey = "\"$password\""
        }
        val netId = wifiManager.addNetwork(wifiConfig)
        wifiManager.disconnect()
        wifiManager.enableNetwork(netId, true)
        wifiManager.reconnect()
        return true
    }

    @RequiresApi(Build.VERSION_CODES.Q)
    private fun requestWifiApi30AndLater(ssid: String, password: String): Boolean {
        val connectivityManager =
            context.applicationContext.getSystemService(ConnectivityManager::class.java)
        val wifiConfig =
            WifiNetworkSpecifier.Builder().setSsid(ssid).setWpa2Passphrase(password).build()
        val request = NetworkRequest.Builder().addTransportType(NetworkCapabilities.TRANSPORT_WIFI)
            .setNetworkSpecifier(wifiConfig)
            .build()
        val callback = object : ConnectivityManager.NetworkCallback() {
            override fun onAvailable(network: Network) {
                super.onAvailable(network)
                connectivityManager.bindProcessToNetwork(network)
            }
        }
        connectivityManager.requestNetwork(request, callback)
        return true
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
}
