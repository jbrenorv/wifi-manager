import 'package:plugin_platform_interface/plugin_platform_interface.dart';

import 'models/wifi_credentials.dart';
import 'wifi_manager_method_channel.dart';

abstract class WifiManagerPlatform extends PlatformInterface {
  /// Constructs a WifiManagerPlatform.
  WifiManagerPlatform() : super(token: _token);

  static final Object _token = Object();

  static WifiManagerPlatform _instance = MethodChannelWifiManager();

  /// The default instance of [WifiManagerPlatform] to use.
  ///
  /// Defaults to [MethodChannelWifiManager].
  static WifiManagerPlatform get instance => _instance;

  /// Platform-specific implementations should set this with their own
  /// platform-specific class that extends [WifiManagerPlatform] when
  /// they register themselves.
  static set instance(WifiManagerPlatform instance) {
    PlatformInterface.verifyToken(instance, _token);
    _instance = instance;
  }

  Future<String?> getPlatformVersion() {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }

  Future<String?> getConnectionInfo() {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }

  Future<bool> requestWifi({required WifiCredentials wifiCredentials}) {
    throw UnimplementedError('platformVersion() has not been implemented.');
  }
}
