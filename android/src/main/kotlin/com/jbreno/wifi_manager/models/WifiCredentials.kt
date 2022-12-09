package com.jbreno.wifi_manager.models

enum class WifiSecurityType { NONE, WPA2, WPA3 }

data class WifiCredentials(
    val ssid: String,
    val password: String? = null,
    val wifiSecurityType: WifiSecurityType = WifiSecurityType.NONE
) {

    constructor(wifiCredentialsMap: Map<String, Any>) : this(
        wifiCredentialsMap["ssid"] as String,
        wifiCredentialsMap["password"] as String?,
        wifiCredentialsMap["wifiSecurityType"] as WifiSecurityType,
    )

    val hasSecurity get() = (password != null && wifiSecurityType != WifiSecurityType.NONE)
}
