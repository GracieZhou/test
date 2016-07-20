
package com.android.settings.system.fragments;

import scifly.device.Device;
import android.os.Build;
import android.os.Bundle;
import android.os.SystemProperties;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.os.Handler;
import android.os.Message;
import com.android.settings.R;
import com.android.settings.SettingPreference;
import com.android.settings.system.SystemSettingsActivity;
import com.android.settings.system.business.SystemSettingsLogic;

/**
 * SystemSettingFragment.
 * 
 * @author Davis
 * @date 2015-8-13
 */
public class SystemSettingFragment extends PreferenceFragment implements OnPreferenceClickListener {

    private static final String LANGUAGE_KEY = "language";

    private static final String INPUT_METHOD_KEY = "inputMethod";

    private static final String DEVICE_INFO_KEY = "deviceInfo";

    private static final String APP_INSTALL_KEY = "appInstall";

    private static final String APP_WHITE_LIST_KEY = "appWhiteList";

    private SettingPreference mLanguagePreference;

    private SettingPreference mImputMethodPreference;

    private SettingPreference mDeviceInfoPreference;

    private SettingPreference mAppInstallPreference;

    private SettingPreference mAppWhiteListPreference;

    private SystemSettingsActivity mActivity;

    private SystemSettingsLogic mSystemSettingsLogic;
    
     private static final int MESSAGE = 1;

    private static final String WHTIE_LIST_SWITCH_PROPERTY = "persist.sys.scifly.whitelist";
    
        private Handler mHandler = new Handler(){
        
        public void dispatchMessage(Message msg) {
            if (MESSAGE == msg.what) {
                Log.d("tag", "<<<<<<refresh again<<<<<<<<<");
                mImputMethodPreference.setRightText(mSystemSettingsLogic.getCurrentInputMethodName());
            }
           }
    };

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preference_system_setting);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.setting_preference_fragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
         Log.d("tag", "<<<<<<SystemSettingFragment<<<onActivityCreated<<<<<");

        mActivity = (SystemSettingsActivity) getActivity();
        mActivity.setSubTitle();

        mSystemSettingsLogic = new SystemSettingsLogic(mActivity);

        mLanguagePreference = (SettingPreference) findPreference(LANGUAGE_KEY);
        mLanguagePreference.setOnPreferenceClickListener(this);
        mLanguagePreference.setRightText(mSystemSettingsLogic.getCurrentLanguage());

        mImputMethodPreference = (SettingPreference) findPreference(INPUT_METHOD_KEY);
        mImputMethodPreference.setOnPreferenceClickListener(this);
        mImputMethodPreference.setRightText(mSystemSettingsLogic.getCurrentInputMethodName());
        mHandler.sendMessageDelayed(mHandler.obtainMessage(MESSAGE), 100);

        mDeviceInfoPreference = (SettingPreference) findPreference(DEVICE_INFO_KEY);
        mDeviceInfoPreference.setOnPreferenceClickListener(this);
        mDeviceInfoPreference.setRightText(Device.getDeviceName(mActivity));

        mAppInstallPreference = (SettingPreference) findPreference(APP_INSTALL_KEY);

        boolean isInstallOnUsbDisabled = SystemProperties.getBoolean("ro.scifly.ApkOnUsb.disable", true);
        if (mAppInstallPreference != null) {
            mAppInstallPreference.setOnPreferenceClickListener(this);
            mAppInstallPreference.setRightText(mSystemSettingsLogic.getInstallLocation());
            if (Build.DEVICE.equals("BenQ_i500") || Build.DEVICE.equals("BenQ_i300") || isInstallOnUsbDisabled) {
                getPreferenceScreen().removePreference(mAppInstallPreference);
            }
        }
        // getPreferenceScreen().removePreference(findPreference(APP_INSTALL_KEY));
        mAppWhiteListPreference = (SettingPreference) findPreference(APP_WHITE_LIST_KEY);
        mAppWhiteListPreference.setOnPreferenceClickListener(this);
        mAppWhiteListPreference.setChecked(SystemProperties.getInt(WHTIE_LIST_SWITCH_PROPERTY, 1) == 1);

    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        String key = preference.getKey();
        Log.d(getClass().getSimpleName(), "onPreferenceClick=====>" + key);
        if (key.equals(LANGUAGE_KEY)) {
            mActivity.addFragment(new LanguageSettingFragment());
        }

        if (key.equals(INPUT_METHOD_KEY)) {
            mActivity.addFragment(new InputMethodSettingFragment());
        }

        if (key.equals(DEVICE_INFO_KEY)) {
            mActivity.addFragment(new DeviceNameFragment());
        }

        if (key.equals(APP_INSTALL_KEY)) {
            if (mActivity.isHasUDisk()) {
                mActivity.addFragment(new AppInstallLocationFragment());
            }
        }
        if (key.equals(APP_WHITE_LIST_KEY)) {
            mAppWhiteListPreference.toggleButton();
            if (mAppWhiteListPreference.isChecked()) {
                SystemProperties.set(WHTIE_LIST_SWITCH_PROPERTY, "1");
            } else {
                SystemProperties.set(WHTIE_LIST_SWITCH_PROPERTY, "0");
            }
        }
        return false;
    }

}
