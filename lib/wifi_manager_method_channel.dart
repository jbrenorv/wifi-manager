import 'package:flutter/foundation.dart';
import 'package:flutter/services.dart';

import 'wifi_manager_platform_interface.dart';

/// An implementation of [WifiManagerPlatform] that uses method channels.
class MethodChannelWifiManager extends WifiManagerPlatform {
  /// The method channel used to interact with the native platform.
  @visibleForTesting
  final methodChannel = const MethodChannel('wifi_manager');

  @override
  Future<String?> getPlatformVersion() async {
    final version =
        await methodChannel.invokeMethod<String>('getPlatformVersion');
    return version;
  }

  @override
  Future<String?> getConnectionInfo() async {
    final networkSSID =
        await methodChannel.invokeMethod<String>('getConnectionInfo');
    return networkSSID;
  }

  @override
  Future<bool> requestWifi() async {
    try {
      final requestCompletedSuccessfully =
          await methodChannel.invokeMethod<bool>('requestWifi');
      return requestCompletedSuccessfully ?? false;
    } on PlatformException catch (e) {
      print("_-_requestWifi $e");
      return false;
    }
  }
}
