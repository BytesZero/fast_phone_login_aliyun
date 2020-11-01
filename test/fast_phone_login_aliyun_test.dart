import 'package:flutter/services.dart';
import 'package:flutter_test/flutter_test.dart';
import 'package:fast_phone_login_aliyun/fast_phone_login_aliyun.dart';

void main() {
  const MethodChannel channel = MethodChannel('fast_phone_login_aliyun');

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
    expect(await FastPhoneLoginAliyun.platformVersion, '42');
  });
}
