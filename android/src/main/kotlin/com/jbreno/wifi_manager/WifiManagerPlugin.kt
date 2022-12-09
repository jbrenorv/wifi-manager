package com.jbreno.wifi_manager


import android.content.Context
import androidx.annotation.NonNull
import com.jbreno.wifi_manager.models.WifiCredentials

import io.flutter.embedding.engine.plugins.FlutterPlugin
import io.flutter.plugin.common.MethodCall
import io.flutter.plugin.common.MethodChannel
import io.flutter.plugin.common.MethodChannel.MethodCallHandler
import io.flutter.plugin.common.MethodChannel.Result

/** WifiManagerPlugin */
class WifiManagerPlugin: FlutterPlugin, MethodCallHandler {
  /// The MethodChannel that will the communication between Flutter and native Android
  ///
  /// This local reference serves to register the plugin with the Flutter Engine and unregister it
  /// when the Flutter Engine is detached from the Activity
  private lateinit var channel : MethodChannel
  private lateinit var context : Context
  private lateinit var wifiManagerUtil: WifiManagerUtil

  override fun onAttachedToEngine(@NonNull flutterPluginBinding: FlutterPlugin.FlutterPluginBinding) {
    channel = MethodChannel(flutterPluginBinding.binaryMessenger, "wifi_manager")
    channel.setMethodCallHandler(this)
    context = flutterPluginBinding.applicationContext
    wifiManagerUtil = WifiManagerUtil(context)
  }

  override fun onMethodCall(@NonNull call: MethodCall, @NonNull result: Result) {
    when (call.method) {

      "getPlatformVersion" -> result.success("Android ${android.os.Build.VERSION.RELEASE}")

      "getConnectionInfo" -> result.success("Network SSID: ${wifiManagerUtil.getConnectionInfo()}")

      "requestWifi" -> {
        val arguments = call.arguments<Map<String, Any>>();
        val wifiCredentials = WifiCredentials(arguments!!)
        wifiManagerUtil.requestWifi(wifiCredentials)
        result.success(true)
      }

      else -> result.notImplemented()
    }
  }

  override fun onDetachedFromEngine(@NonNull binding: FlutterPlugin.FlutterPluginBinding) {
    channel.setMethodCallHandler(null)
  }
}
