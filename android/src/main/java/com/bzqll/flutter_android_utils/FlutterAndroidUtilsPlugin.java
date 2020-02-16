package com.bzqll.flutter_android_utils;

import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Network;
import android.net.NetworkCapabilities;
import android.os.Build;
import android.text.TextUtils;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;
import io.flutter.plugin.common.PluginRegistry.Registrar;

/** FlutterAndroidUtilsPlugin */
public class FlutterAndroidUtilsPlugin implements MethodCallHandler {

  private Context context;

  private FlutterAndroidUtilsPlugin(Context context) {
    this.context = context;
  }

  /** Plugin registration. */
  public static void registerWith(Registrar registrar) {
    final MethodChannel channel = new MethodChannel(registrar.messenger(), "flutter_android_utils");
    channel.setMethodCallHandler(new FlutterAndroidUtilsPlugin(registrar.context()));
  }


  /**
   * 是否正在使用VPN
   */
  private static boolean isVpnUsed(Context context) {

    //this method doesn't work below API 21
    if (android.os.Build.VERSION.SDK_INT < Build.VERSION_CODES.LOLLIPOP) {
      return false;
    }


    boolean vpnInUse = false;

    ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);


    if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.M) {

      Network activeNetwork = connectivityManager.getActiveNetwork();
      NetworkCapabilities caps = connectivityManager.getNetworkCapabilities(activeNetwork);

      return caps.hasTransport(NetworkCapabilities.TRANSPORT_VPN);
    }

    Network[] networks = connectivityManager.getAllNetworks();

    for (Network network : networks) {

      NetworkCapabilities caps = connectivityManager.getNetworkCapabilities(network);
      if (caps.hasTransport(NetworkCapabilities.TRANSPORT_VPN)) {
        vpnInUse = true;
        break;
      }
    }

    return vpnInUse;
  }

  @Override
  public void onMethodCall(MethodCall call, Result result) {
    if (call.method.equals("getPlatformVersion")) {
      result.success("Android " + android.os.Build.VERSION.RELEASE);
    } else if (call.method.equals("getVpnNetwork")) {
      result.success(isVpnUsed(context));
    } else if (call.method.equals("getProxyNetwork")) {
      result.success(isWifiProxy());
    } else if (call.method.equals("getUserAgent")) {
      result.success(System.getProperty("http.agent"));
    } else if (call.method.equals("getKuWoEncrypt")) {
      String q = call.argument("q");
      if(q==null){
        q = "";
      }
      result.success(getKuWoEncrypt(q));
    } else {
      result.notImplemented();
    }
  }

  /**
   * 是否正在使用WIFI代理
   */
  private boolean isWifiProxy() {
    String http = System.getProperty("http.proxyHost");
    String http_port = System.getProperty("http.proxyPort");
    String https = System.getProperty("https.proxyHost");
    String https_port = System.getProperty("https.proxyPort");
    return (!TextUtils.isEmpty(http_port) || !TextUtils.isEmpty(http) || !TextUtils.isEmpty(https) || !TextUtils.isEmpty(https_port));
  }

  private static String getKuWoEncrypt(String q) {
    byte[] data = KWDESUtil.encryptKSing(q.getBytes(), "ylzsxkwm".getBytes());
    return new String(KWBase64Util.encode(data, data.length));
  }
}

