import 'package:flutter_test/flutter_test.dart';
import 'package:wifi_manager/wifi_manager.dart';
import 'package:wifi_manager/wifi_manager_platform_interface.dart';
import 'package:wifi_manager/wifi_manager_method_channel.dart';
import 'package:plugin_platform_interface/plugin_platform_interface.dart';

class MockWifiManagerPlatform
    with MockPlatformInterfaceMixin
    implements WifiManagerPlatform {

  @override
  Future<String?> getPlatformVersion() => Future.value('42');
}

void main() {
  final WifiManagerPlatform initialPlatform = WifiManagerPlatform.instance;

  test('$MethodChannelWifiManager is the default instance', () {
    expect(initialPlatform, isInstanceOf<MethodChannelWifiManager>());
  });

  test('getPlatformVersion', () async {
    WifiManager wifiManagerPlugin = WifiManager();
    MockWifiManagerPlatform fakePlatform = MockWifiManagerPlatform();
    WifiManagerPlatform.instance = fakePlatform;

    expect(await wifiManagerPlugin.getPlatformVersion(), '42');
  });
}
