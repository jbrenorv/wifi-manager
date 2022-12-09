import 'wifi_manager_platform_interface.dart';

class WifiManager {
  Future<String?> getPlatformVersion() {
    return WifiManagerPlatform.instance.getPlatformVersion();
  }

  Future<String?> getConnectionInfo() {
    return WifiManagerPlatform.instance.getConnectionInfo();
  }

  Future<bool> requestWifi() {
    return WifiManagerPlatform.instance.requestWifi();
  }
}
