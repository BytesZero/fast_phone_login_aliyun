package com.example.fast_phone_login_aliyun.delegate;

import android.app.Activity;
import android.text.TextUtils;
import android.util.Log;
import android.widget.Toast;

import com.example.fast_phone_login_aliyun.config.BaseUIConfig;
import com.mobile.auth.gatewayauth.PhoneNumberAuthHelper;
import com.mobile.auth.gatewayauth.ResultCode;
import com.mobile.auth.gatewayauth.TokenResultListener;
import com.mobile.auth.gatewayauth.model.TokenRet;

import io.flutter.plugin.common.MethodCall;
import io.flutter.plugin.common.MethodChannel;

public class FastPhoneLoginAliyunDelegate {
    private static final String TAG = FastPhoneLoginAliyunDelegate.class.getSimpleName();

    private Activity activity;
    private MethodChannel.Result pendingResult;

    private PhoneNumberAuthHelper mPhoneNumberAuthHelper;
    private TokenResultListener mTokenResultListener;
    private BaseUIConfig mUIConfig;

    public FastPhoneLoginAliyunDelegate(Activity activity) {
        this.activity = activity;
    }

    private void clearMethodCallAndResult() {
        pendingResult = null;
    }


    /**
     * 初始化 SDK
     *
     * @param call   调用参数
     * @param result 返回信息
     */
    public void init(MethodCall call, MethodChannel.Result result) {
        this.pendingResult = result;
        String secret = call.argument("secret");
        if (TextUtils.isEmpty(secret)) {
            result.error("-1000", "secret is not null", "secret is not null");
            return;
        }
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
                        mPhoneNumberAuthHelper.quitLoginPage();
                        // 返回成功
                        pendingResult.success(tokenRet.getToken());
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
                        Toast.makeText(activity.getApplicationContext(), "用户取消", Toast.LENGTH_SHORT).show();
                        pendingResult.error("-300","用户取消","用户取消");
                    } else {
                        Toast.makeText(activity.getApplicationContext(), "一键登录失败切换到其他登录方式", Toast.LENGTH_SHORT).show();
                        pendingResult.error("-200","一键登录失败切换到其他登录方式","一键登录失败切换到其他登录方式");
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
                mPhoneNumberAuthHelper.setAuthListener(null);
            }
        };
        mPhoneNumberAuthHelper = PhoneNumberAuthHelper.getInstance(activity, mTokenResultListener);
        mPhoneNumberAuthHelper.setAuthSDKInfo(secret);
        this.pendingResult.success(true);
    }

    /**
     * 拉起授权页，并且获取 Token
     */
    public void getLoginToken(MethodCall call, MethodChannel.Result result) {
        this.pendingResult = result;
        // 解析参数
        int pageStyle = call.argument("pageStyle");
        int timeout = call.argument("timeout");
        // 设置监听
        mPhoneNumberAuthHelper.setAuthListener(mTokenResultListener);
        /// 配置页面 UI
        mUIConfig = BaseUIConfig.init(pageStyle, activity, mPhoneNumberAuthHelper);
        mUIConfig.configAuthPage();
        // 拉起授权页
        mPhoneNumberAuthHelper.getLoginToken(activity, timeout);
    }

}
