import 'package:wifi_manager/models/wifi_credentials.dart';

import 'wifi_manager_platform_interface.dart';

class WifiManager {
  Future<String?> getPlatformVersion() {
    return WifiManagerPlatform.instance.getPlatformVersion();
  }

  Future<String?> getConnectionInfo() {
    return WifiManagerPlatform.instance.getConnectionInfo();
  }

  Future<bool> requestWifi({required WifiCredentials wifiCredentials}) {
    return WifiManagerPlatform.instance
        .requestWifi(wifiCredentials: wifiCredentials);
  }

  Future<bool> openWifiSettings() {
    return WifiManagerPlatform.instance.openWifiSettings();
  }

  Future<bool> showToast() {
    return WifiManagerPlatform.instance.showToast();
  }
}
