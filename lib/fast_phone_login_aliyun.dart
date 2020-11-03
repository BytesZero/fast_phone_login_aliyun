// You have generated a new plugin project without
// specifying the `--platforms` flag. A plugin project supports no platforms is generated.
// To add platforms, run `flutter create -t plugin --platforms <platforms> .` under the same
// directory. You can also find a detailed instruction on how to add platforms in the `pubspec.yaml` at https://flutter.dev/docs/development/packages-and-plugins/developing-packages#plugin-platforms.

import 'dart:async';
import 'package:flutter/services.dart';

class FastPhoneLoginAliyun {
  static const MethodChannel _channel =
      const MethodChannel('fast_phone_login_aliyun');

  /// 初始化
  /// [secret] 秘钥（阿里云【号码认证服务】=>【认证方案管理】=>【iOS/Android】=>【操作--秘钥】）
  static Future<bool> init({String secret}) async {
    final bool initStatus =
        await _channel.invokeMethod('init', {'secret': secret});
    return initStatus;
  }

  /// 拉起授权页，并且获取 Token
  /// [pageStyle] 页面样式
  /// [timeout] 超时时间
  static Future<String> getLoginToken(
      {int pageStyle = 1, int timeout = 5000}) async {
    final String token = await _channel.invokeMethod('getLoginToken', {
      'pageStyle': pageStyle,
      'timeout': timeout,
    });
    return token;
  }
}
