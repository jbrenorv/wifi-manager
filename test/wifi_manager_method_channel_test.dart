import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:wifi_manager/wifi_manager_method_channel.dart';

void main() {
  MethodChannelWifiManager platform = MethodChannelWifiManager();
  const MethodChannel channel = MethodChannel('wifi_manager');

  TestWidgetsFlutterBinding.ensureInitialized();

  setUp(() {
    channel.setMockMethodCallHandler((MethodCall methodCall) async {
      return '42';
    });
  });

  tearDown(() {
    channel.setMockMethodCallHandler(null);
  });

  test('getPlatformVersion', () async {
    expect(await platform.getPlatformVersion(), '42');
  });
}
