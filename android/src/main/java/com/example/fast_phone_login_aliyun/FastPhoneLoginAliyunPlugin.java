package com.example.fast_phone_login_aliyun;

import android.app.Activity;
import android.app.ProgressDialog;
import android.content.Intent;
import android.util.Log;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.example.fast_phone_login_aliyun.config.BaseUIConfig;
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
import io.flutter.plugin.common.PluginRegistry.Registrar;

/** FastPhoneLoginAliyunPlugin */
public class FastPhoneLoginAliyunPlugin implements FlutterPlugin, ActivityAware, MethodCallHandler {

  private static final String TAG = FastPhoneLoginAliyunPlugin.class.getSimpleName();
  private static final String CHANNEL = "fast_phone_login_aliyun";
  private MethodChannel channel;
  private  Activity activity;
  private  ActivityPluginBinding activityPluginBinding;
  private PhoneNumberAuthHelper mPhoneNumberAuthHelper;
  private TokenResultListener mTokenResultListener;
  private BaseUIConfig mUIConfig;

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
      sdkInit("Z5s/FfHNCi4wRe5TRDriD43i6jJOF8on5N6SetMPrlRCMj4l7Rvo5e2Qp04PMAfE4hpgkIFzpJ5/5F/b9ZB1jg0dII1XgIsIzuD0qjGsTAS1gagVp1g6gFiTZ9tIWBd2yt7lUam3EObPtB77knbAez7lFR1nbbaZmAYXUOpUYoQWRDnGepnkkXBBbMVK+ifK8sI6XGPXR5d5Zy+Z4bjLcHOpzl7K95mEGqsamfmkn2DaYqXw15L15oSMMg1OWSjqt+e7EXAYLOWbV+FE04SPbXMWa86kHjsneHhbBjq2hrtgXUuH838Ux3j/5z+/jT6I");
      mUIConfig = BaseUIConfig.init(4, activity, mPhoneNumberAuthHelper);
      oneKeyLogin();
      result.success("Android " + android.os.Build.VERSION.RELEASE);
    } else {
      result.notImplemented();
    }
  }

  @Override
  public void onDetachedFromEngine(@NonNull FlutterPluginBinding binding) {
    channel.setMethodCallHandler(null);
  }


  @Override
  public void onAttachedToActivity(@NonNull ActivityPluginBinding binding) {
    this.activityPluginBinding=binding;
    this.activity=binding.getActivity();
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
    this.activityPluginBinding=null;
    this.activity=null;
  }


  public void sdkInit(String secretInfo) {
    mTokenResultListener = new TokenResultListener() {
      @Override
      public void onTokenSuccess(String s) {
        TokenRet tokenRet = null;
        try {
          tokenRet = TokenRet.fromJson(s);
          if (ResultCode.CODE_START_AUTHPAGE_SUCCESS.equals(tokenRet.getCode())) {
            Log.i(TAG, "唤起授权页成功：" + s);
          }

          if (ResultCode.CODE_SUCCESS.equals(tokenRet.getCode())) {
            Log.i(TAG, "获取token成功：" + tokenRet.getToken());
            mPhoneNumberAuthHelper.setAuthListener(null);
          }
        } catch (Exception e) {
          e.printStackTrace();
        }
      }

      @Override
      public void onTokenFailed(String s) {
        Log.e(TAG, "获取token失败：" + s);
        TokenRet tokenRet = null;
        try {
          tokenRet = TokenRet.fromJson(s);
          if (ResultCode.CODE_ERROR_USER_CANCEL.equals(tokenRet.getCode())) {
            //模拟的是必须登录 否则直接退出app的场景
//            finish();
          } else {
            Toast.makeText(activity.getApplicationContext(), "一键登录失败切换到其他登录方式", Toast.LENGTH_SHORT).show();
          }
        } catch (Exception e) {
          e.printStackTrace();
        }
        mPhoneNumberAuthHelper.setAuthListener(null);
      }
    };
    mPhoneNumberAuthHelper = PhoneNumberAuthHelper.getInstance(activity, mTokenResultListener);
//    mPhoneNumberAuthHelper.getReporter().setLoggerEnable(true);
    mPhoneNumberAuthHelper.setAuthSDKInfo(secretInfo);
  }

  /**
   * 进入app就需要登录的场景使用
   */
  private void oneKeyLogin() {
    mPhoneNumberAuthHelper = PhoneNumberAuthHelper.getInstance(activity.getApplicationContext(), mTokenResultListener);
    mUIConfig.configAuthPage();
    getLoginToken(5000);
  }

  /**
   * 拉起授权页
   * @param timeout 超时时间
   */
  public void getLoginToken(int timeout) {
    mPhoneNumberAuthHelper.getLoginToken(activity, timeout);
  }
}
