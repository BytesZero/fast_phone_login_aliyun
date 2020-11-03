package com.example.fast_phone_login_aliyun;

import android.app.Activity;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.fast_phone_login_aliyun.config.BaseUIConfig;
import com.example.fast_phone_login_aliyun.delegate.FastPhoneLoginAliyunDelegate;
import com.mobile.auth.gatewayauth.PhoneNumberAuthHelper;
import com.mobile.auth.gatewayauth.ResultCode;
import com.mobile.auth.gatewayauth.TokenResultListener;
import com.mobile.auth.gatewayauth.model.TokenRet;

import io.flutter.embedding.engine.plugins.FlutterPlugin;
import io.flutter.embedding.engine.plugins.activity.ActivityAware;
import io.flutter.embedding.engine.plugins.activity.ActivityPluginBinding;
import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;
import io.flutter.plugin.common.MethodChannel.MethodCallHandler;
import io.flutter.plugin.common.MethodChannel.Result;

/** FastPhoneLoginAliyunPlugin */
public class FastPhoneLoginAliyunPlugin implements FlutterPlugin, ActivityAware, MethodCallHandler {

  private static final String TAG = FastPhoneLoginAliyunPlugin.class.getSimpleName();
  private static final String CHANNEL = "fast_phone_login_aliyun";
  private MethodChannel channel;
  private FastPhoneLoginAliyunDelegate delegate;

  @Override
  public void onAttachedToEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    setupEngine(flutterPluginBinding);
  }

  private void setupEngine(@NonNull FlutterPluginBinding flutterPluginBinding) {
    channel = new MethodChannel(flutterPluginBinding.getBinaryMessenger(), CHANNEL);
    channel.setMethodCallHandler(this);
  }

  @Override
  public void onMethodCall(@NonNull MethodCall call, @NonNull Result result) {
    if (call.method.equals("getPlatformVersion")) {
      result.success("Android " + android.os.Build.VERSION.RELEASE);
    }else  if (call.method.equals("init")) {
        this.delegate.init(call, result);
    } else  if (call.method.equals("getLoginToken")) {
      this.delegate.getLoginToken(call, result);
    }else {
      result.notImplemented();
    }
  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    channel.setMethodCallHandler(null);
  }


  @Override
  public void onAttachedToActivity(@NonNull ActivityPluginBinding binding) {
    this.delegate= new FastPhoneLoginAliyunDelegate(binding.getActivity());
  }

  @Override
  public void onReattachedToActivityForConfigChanges(@NonNull ActivityPluginBinding binding) {
    onAttachedToActivity(binding);
  }

  @Override
  public void onDetachedFromActivityForConfigChanges() {
    onDetachedFromActivity();
  }



  @Override
  public void onDetachedFromActivity() {
    this.delegate=null;
  }


}
