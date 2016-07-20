package com.android.settings.system;

import scifly.device.Device;
import android.content.Intent;
import android.graphics.Color;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.settings.R;
import com.android.settings.widget.TitleWidget;

public class SystemSettingsHolder {

    private SystemSettingsActivity mActivity;

    private TitleWidget mTitle;

    private TextView mLanguage;
    private TextView mInputMethod;
    private TextView mDeviceName;
    private TextView mAppInstallLocation;

    private LinearLayout mLanguageLl;
    private LinearLayout mInputMethodLl;
    private LinearLayout mDeviceNameLl;
    private LinearLayout mAppInstallLocationLl;

    public void findViews() {
        mTitle = (TitleWidget) mActivity
                .findViewById(R.id.activity_system_settings_title);
        mTitle.setMainTitleText(mActivity.getString(R.string.action_settings));
        mTitle.setFirstSubTitleText(
                mActivity.getString(R.string.system_settings), true);
        mLanguage = (TextView) mActivity.findViewById(R.id.tv_language);
        mLanguage.setTextColor(Color.rgb(0 , 255, 0));
        mInputMethod = (TextView) mActivity.findViewById(R.id.tv_input_method);
        mDeviceName = (TextView) mActivity.findViewById(R.id.tv_device_name);
        mAppInstallLocation = (TextView) mActivity
                .findViewById(R.id.tv_app_install_location);
        mLanguageLl = (LinearLayout) mActivity.findViewById(R.id.ll_language);
        mLanguageLl.requestFocus();
        mInputMethodLl = (LinearLayout) mActivity
                .findViewById(R.id.ll_input_method);
        mDeviceNameLl = (LinearLayout) mActivity
                .findViewById(R.id.ll_device_name);
        mAppInstallLocationLl = (LinearLayout) mActivity
                .findViewById(R.id.ll_app_install_location);
        refreshUI();
    }

    public void refreshUI() {
        mLanguage.setText(mActivity.getmLogic().getCurrentLanguage());
        mInputMethod.setText(mActivity.getmLogic().getCurrentInputMethodName());
        mDeviceName.setText(Device.getDeviceName(mActivity));
        mAppInstallLocation.setText(mActivity.getmLogic().getInstallLocation());
    }

    public SystemSettingsHolder(SystemSettingsActivity activity) {
        mActivity = activity;
        findViews();
        registerListener();
    }

    public void registerListener() {
        mLanguageLl.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent();
                intent.setClass(mActivity, LanguageSettingsActivity.class);
                mActivity.startActivity(intent);
            }
        });
        mLanguageLl.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View arg0, boolean arg1) {
                if (arg0.isFocused()) {
                    mLanguage.setTextColor(Color.rgb(0, 255, 0));
                } else {
                    mLanguage.setTextColor(Color.rgb(255, 255, 255));
                }
            }
        });
        mInputMethodLl.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent();
                intent.setClass(mActivity, InputMethodSettingsActivity.class);
                mActivity.startActivity(intent);
            }
        });
        mInputMethodLl.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View arg0, boolean arg1) {
                if (arg0.isFocused()) {
                    mInputMethod.setTextColor(Color.rgb(0, 255, 0));
                } else {
                    mInputMethod.setTextColor(Color.rgb(255, 255, 255));
                }
            }
        });
        mDeviceNameLl.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent();
                intent.setClass(mActivity, DeviceNameSettingsActivity.class);
                mActivity.startActivity(intent);
            }
        });
        mDeviceNameLl.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View arg0, boolean arg1) {
                if (arg0.isFocused()) {
                    mDeviceName.setTextColor(Color.rgb(0, 255, 0));
                } else {
                    mDeviceName.setTextColor(Color.rgb(255, 255, 255));
                }
            }
        });
        mAppInstallLocationLl.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent();
                intent.setClass(mActivity, AppInstallLocationActivity.class);
                mActivity.startActivity(intent);
            }
        });
        mAppInstallLocationLl
                .setOnFocusChangeListener(new OnFocusChangeListener() {

                    @Override
                    public void onFocusChange(View arg0, boolean arg1) {
                        if (arg0.isFocused()) {
                            mAppInstallLocation.setTextColor(Color.rgb(0, 255,
                                    0));
                        } else {
                            mAppInstallLocation.setTextColor(Color.rgb(255,
                                    255, 255));
                        }
                    }
                });

    }

}
