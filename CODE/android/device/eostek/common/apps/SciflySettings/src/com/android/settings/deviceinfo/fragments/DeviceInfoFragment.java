
package com.android.settings.deviceinfo.fragments;

import scifly.device.Device;
import android.os.Bundle;
import android.os.SystemProperties;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.settings.BaseSettingActivity;
import com.android.settings.R;
import com.android.settings.SettingPreference;
import com.android.settings.deviceinfo.DeviceInfoActivity;
import com.android.settings.deviceinfo.business.DeviceInfoLogic;

/**
 * @ClassName: DeviceInfoFragment
 * @author: lucky.li
 * @date: 2015-8-26 下午5:09:49
 * @Copyright: Eostek Co., Ltd. Copyright , All rights reserved
 */
public class DeviceInfoFragment extends PreferenceFragment implements OnPreferenceClickListener {
    private static final String DEVICEID_KEY = "deviceId";

    private static final String DEVICE_NAME_KEY = "deviceName";

    private static final String BUILD_NUMBER_KEY = "buildNumber";

    private static final String BUILD_MODEL_KEY = "buildModel";

    private static final String STORAGEINFO_KEY = "storageInfo";

    private static final String MOREINFO_KEY = "moreInfo";

    private static final String PLATFORM = SystemProperties.get("ro.board.platform", "");

    private static final String PLATFORM_MADISON = "madison";

    /**
     * DeviceId
     */
    private SettingPreference mDeviceIdPreference;

    /**
     * DeviceName
     */
    private SettingPreference mDeviceNamePreference;

    /**
     * BuildNumber
     */
    private SettingPreference mBuildNumberPreference;

    /**
     * StorageInfo
     */
    private SettingPreference mStorageInfoPreference;

    /**
     * MoreInfo
     */
    private SettingPreference mMoreInfoPreference;

    /**
     * BaseSettingActivity
     */
    private BaseSettingActivity mActivity;

    private DeviceInfoLogic mDeviceInfoLogic;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        addPreferencesFromResource(R.xml.preference_device_info);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.setting_preference_fragment, container, false);
    }

    @SuppressWarnings("static-access")
    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        mActivity = (DeviceInfoActivity) getActivity();
        mActivity.setSubTitle();
        mDeviceInfoLogic = new DeviceInfoLogic(mActivity);

        // DeviceId
        mDeviceIdPreference = (SettingPreference) findPreference(DEVICEID_KEY);
        mDeviceIdPreference.setRightText(Device.getBb());
        // getListView().getChildAt(0).setFocusable(false);

        // DeviceName
        mDeviceNamePreference = (SettingPreference) findPreference(DEVICE_NAME_KEY);
        mDeviceNamePreference.setRightText(mDeviceInfoLogic.getDeviceName());
        // getListView().getChildAt(1).setFocusable(false);

        // BuildNumber
        mBuildNumberPreference = (SettingPreference) findPreference(BUILD_NUMBER_KEY);
        mBuildNumberPreference.setRightText(mDeviceInfoLogic.getVersion());

        // StorageInfo
        mStorageInfoPreference = (SettingPreference) findPreference(STORAGEINFO_KEY);
        mStorageInfoPreference.setOnPreferenceClickListener(this);
        mStorageInfoPreference.setRightText("");

        // MoreInfo
        mMoreInfoPreference = (SettingPreference) findPreference(MOREINFO_KEY);
        mMoreInfoPreference.setOnPreferenceClickListener(this);
        mMoreInfoPreference.setRightText("");

    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        String key = preference.getKey();
        if (key.equals(STORAGEINFO_KEY)) {
            mActivity.addFragment(new StorageInfoFragment());
        } else if (key.equals(MOREINFO_KEY)) {
            mActivity.addFragment(new MoreInfoFragment());
        }
        return false;
    }

}
