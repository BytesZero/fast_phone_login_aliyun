package com.example.fast_phone_login_aliyun.config;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.os.Build;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

import com.example.fast_phone_login_aliyun.R;
import com.example.fast_phone_login_aliyun.utils.AppUtils;
import com.mobile.auth.gatewayauth.AuthRegisterViewConfig;
import com.mobile.auth.gatewayauth.AuthRegisterXmlConfig;
import com.mobile.auth.gatewayauth.AuthUIConfig;
import com.mobile.auth.gatewayauth.CustomInterface;
import com.mobile.auth.gatewayauth.PhoneNumberAuthHelper;
import com.mobile.auth.gatewayauth.ui.AbstractPnsViewDelegate;

import static com.example.fast_phone_login_aliyun.utils.AppUtils.dp2px;


public class DialogLandConfig extends BaseUIConfig{

    private int mOldScreenOrientation;


    public DialogLandConfig(Activity activity, PhoneNumberAuthHelper authHelper) {
        super(activity, authHelper);
    }

    @Override
    public void configAuthPage() {
        mAuthHelper.removeAuthRegisterXmlConfig();
        mAuthHelper.removeAuthRegisterViewConfig();
        int authPageOrientation = ActivityInfo.SCREEN_ORIENTATION_SENSOR_LANDSCAPE;
        if (Build.VERSION.SDK_INT == 26) {
            mOldScreenOrientation = mActivity.getRequestedOrientation();
            mActivity.setRequestedOrientation(authPageOrientation);
            authPageOrientation = ActivityInfo.SCREEN_ORIENTATION_BEHIND;
        }
        updateScreenSize(authPageOrientation);
        final int dialogWidth = (int) (mScreenWidthDp * 0.63);
        final int dialogHeight = (int) (mScreenHeightDp * 0.6);

        //sdk默认控件的区域是marginTop50dp
        int designHeight = dialogHeight - 50;
        int unit = designHeight / 10;
        int logBtnHeight = (int) (unit * 1.2);
        final int logBtnOffsetY = unit * 3;

        final View switchContainer = createLandDialogCustomSwitchView();
        mAuthHelper.addAuthRegistViewConfig("number_logo", new AuthRegisterViewConfig.Builder()
                .setView(initNumberView())
                .setRootViewId(AuthRegisterViewConfig.RootViewId.ROOT_VIEW_ID_NUMBER)
                .setCustomInterface(new CustomInterface() {
                    @Override
                    public void onClick(Context context) {

                    }
                }).build());
        mAuthHelper.addAuthRegistViewConfig("switch_other", new AuthRegisterViewConfig.Builder()
                .setRootViewId(AuthRegisterViewConfig.RootViewId.ROOT_VIEW_ID_NUMBER)
                .setView(switchContainer).build());
        mAuthHelper.addAuthRegisterXmlConfig(new AuthRegisterXmlConfig.Builder()
                .setLayout(R.layout.custom_land_dialog, new AbstractPnsViewDelegate() {
                    @Override
                    public void onViewCreated(View view) {
                        findViewById(R.id.tv_title).setVisibility(View.GONE);
                        findViewById(R.id.btn_close).setOnClickListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                mAuthHelper.quitLoginPage();
                            }
                        });
                        int iconTopMargin = dp2px(getContext(), logBtnOffsetY + 50);
                        View iconContainer = findViewById(R.id.container_icon);
                        RelativeLayout.LayoutParams iconLayout = (RelativeLayout.LayoutParams) iconContainer.getLayoutParams();
                        iconLayout.topMargin = iconTopMargin;
                        iconLayout.width = dp2px(getContext(), dialogWidth / 2 - 60);
                    }
                })
                .build());
        mAuthHelper.setAuthUIConfig(new AuthUIConfig.Builder()
                .setAppPrivacyOne("《自定义隐私协议》", "https://www.baidu.com")
                .setAppPrivacyColor(Color.GRAY, Color.RED)
                .setNavHidden(true)
                .setCheckboxHidden(true)
                .setLogoHidden(true)
                .setSloganHidden(true)
                .setSwitchAccHidden(true)
                .setNumberFieldOffsetX(60)
                .setNumberLayoutGravity(Gravity.LEFT)
                .setNumberSize(24)
                .setNumFieldOffsetY(0)
                .setPrivacyOffsetY_B(20)
                .setPageBackgroundPath("dialog_page_background")
                .setLogBtnOffsetY(logBtnOffsetY)
                .setLogBtnOffsetX(30)
                .setLogBtnMarginLeftAndRight(0)
                .setLogBtnWidth(174)
                .setLogBtnLayoutGravity(Gravity.LEFT)
                .setLogBtnHeight(51)
                .setLogBtnBackgroundPath("login_btn_bg")
                .setDialogWidth(dialogWidth)
                .setDialogHeight(dialogHeight)
                .setDialogBottom(false)
                .setAuthPageActIn("in_activity", "out_activity")
                .setAuthPageActOut("in_activity", "out_activity")
                .setVendorPrivacyPrefix("《")
                .setVendorPrivacySuffix("》")
                .setProtocolGravity(Gravity.CENTER_VERTICAL)
                .setScreenOrientation(authPageOrientation)
                .create());
    }

    private ImageView createLandDialogPhoneNumberIcon(int leftMargin) {
        ImageView imageView = new ImageView(mContext);
        int size = dp2px(mContext, 23);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(size, size);
        layoutParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
        layoutParams.leftMargin = leftMargin;
        imageView.setLayoutParams(layoutParams);
        imageView.setBackgroundResource(R.drawable.phone);
        imageView.setScaleType(ImageView.ScaleType.CENTER);
        return imageView;
    }

    private View createLandDialogCustomSwitchView() {
        View v = LayoutInflater.from(mContext).inflate(R.layout.custom_switch_other, new RelativeLayout(mContext), false);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.MATCH_PARENT,
                RelativeLayout.LayoutParams.WRAP_CONTENT);
        layoutParams.addRule(RelativeLayout.CENTER_VERTICAL, RelativeLayout.TRUE);
        v.setLayoutParams(layoutParams);
        return v;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (mOldScreenOrientation != mActivity.getRequestedOrientation()) {
            mActivity.setRequestedOrientation(mOldScreenOrientation);
        }
    }

    private ImageView initNumberView() {
        ImageView pImageView = new ImageView(mContext);
        pImageView.setImageResource(R.drawable.phone);
        pImageView.setScaleType(ImageView.ScaleType.FIT_XY);
        RelativeLayout.LayoutParams pParams = new RelativeLayout.LayoutParams(dp2px(mContext, 30), dp2px(mContext, 30));
        pParams.setMargins(dp2px(mContext, 30), 0, 0, 0);
        pImageView.setLayoutParams(pParams);
        return pImageView;
    }
}
