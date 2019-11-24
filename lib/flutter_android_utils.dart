import 'dart:async';

import 'package:flutter/services.dart';

class FlutterAndroidUtils {
  static const MethodChannel _channel =
      const MethodChannel('flutter_android_utils');

  static Future<String> get platformVersion async {
    final String version = await _channel.invokeMethod('getPlatformVersion');
    return version;
  }

  static Future<bool> isVpnNetwork() async {
    bool result = false;
    try {
      // 调用是否VPN网络
      result = await _channel.invokeMethod("getVpnNetwork");
    } on PlatformException catch (e) {
      print(e.toString());
    }
    return result;
  }

  static Future<bool> isProxyNetWork() async {
    bool result = false;
    try {
      // 调用是否代理网络
      result = await _channel.invokeMethod("getProxyNetwork");
    } on PlatformException catch (e) {
      print(e.toString());
    }
    return result;
  }


  static Future<String> getKuWoEncrypt(String params) async {
    // 调用酷我加密
    final String result = await _channel.invokeMethod("getKuWoEncrypt",{'q':params});
    return result;
  }
}
