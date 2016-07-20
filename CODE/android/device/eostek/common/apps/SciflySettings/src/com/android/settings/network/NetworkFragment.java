
package com.android.settings.network;

import scifly.device.Device;
import android.app.Activity;
import android.content.pm.PackageManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
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

import com.android.settings.R;
import com.android.settings.SettingPreference;
import com.android.settings.network.connectivity.ConnectionActivity;
import com.android.settings.network.downloadspeed.DownloadSpeedActivity;
import com.android.settings.network.ethernet.EthernetSettingFragment;
import com.android.settings.network.pppoe.PppoeFragment;
import com.android.settings.network.screendisplay.ScreenDisplayFragment;
import com.android.settings.network.wifi.WifiSettingsFragment;
import com.android.settings.network.wlan.WlanFragment;
import com.android.settings.util.Utils;
import com.mstar.android.pppoe.PppoeManager;

/**
 * NetworkFragment.
 * 
 * @date 2015-8-13
 */
public class NetworkFragment extends PreferenceFragment implements OnPreferenceClickListener {

    private static final String TAG = "NetworkSetting";

    private static final String WIRED_KEY = "wired";

    private static final String PLATFORM = SystemProperties.get("ro.scifly.platform", "");

    private static final String WIFI_KEY = "wifi";
    
    private static final String PPPoE = "PPPoE";

    private static final String NET_DIAG_KEY = "netdiag";

    private static final String DOWNLOAD_SPEED_KEY = "speed";

    private static final String BLUE_TOOTH = "blue_tooth";

    private static final String SCRREN_DISPLAY = "screen_display";

    private static final String WLAN = "wlan";

    private SettingPreference mWiredPreference;

    private SettingPreference mWifiPreference;

    private SettingPreference mNetdiagPreference;

    private SettingPreference mDownloadspeedPreference;

    private SettingPreference mBluetoothPreference;

    private SettingPreference mScreenDisplayPreference;

    private SettingPreference mWlanPreference;
    
    private SettingPreference mPPPoEPreference;

    private NetworkSettingActivity mActivity;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preference_network_setting);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.setting_preference_fragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = (NetworkSettingActivity) getActivity();
        mActivity.setSubTitle();

        // Wired
        mWiredPreference = (SettingPreference) findPreference(WIRED_KEY);
        if (mWiredPreference != null) {
            mWiredPreference.setOnPreferenceClickListener(this);
        }

        // Wifi
        mWifiPreference = (SettingPreference) findPreference(WIFI_KEY);
        if (mWifiPreference != null) {
            mWifiPreference.setOnPreferenceClickListener(this);
        }

        // Netdiag
        mNetdiagPreference = (SettingPreference) findPreference(NET_DIAG_KEY);
        mNetdiagPreference.setRightText("");
        mNetdiagPreference.setOnPreferenceClickListener(this);

        // Downloadspeed
        mDownloadspeedPreference = (SettingPreference) findPreference(DOWNLOAD_SPEED_KEY);
        mDownloadspeedPreference.setRightText("");
        mDownloadspeedPreference.setOnPreferenceClickListener(this);

        // Bluetooth
        mBluetoothPreference = (SettingPreference) findPreference(BLUE_TOOTH);
        if (mBluetoothPreference != null) {
            mBluetoothPreference.setRightText("");
            mBluetoothPreference.setOnPreferenceClickListener(this);
        }

        // Screen Display
        mScreenDisplayPreference = (SettingPreference) findPreference(SCRREN_DISPLAY);
        if (mScreenDisplayPreference != null) {
            mScreenDisplayPreference.setOnPreferenceClickListener(this);
        }

        // wlan
        mWlanPreference = (SettingPreference) findPreference(WLAN);
        if (mWlanPreference != null) {
            mWlanPreference.setOnPreferenceClickListener(this);
        }
        
     // PPPoE
        mPPPoEPreference = (SettingPreference) findPreference(PPPoE);
        if (mPPPoEPreference != null) {
            mPPPoEPreference.setOnPreferenceClickListener(this);
        }
        // hide some preference in some special case.
        if (!mActivity.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH)
                || Build.DEVICE.equals("BenQ_i500") || PLATFORM.equals(Device.SCIFLY_PLATFORM_BOX)) {
            if (mBluetoothPreference != null) {
                getPreferenceScreen().removePreference(mBluetoothPreference);
            }
        }

        // maybe use system feature
        if ((Build.DEVICE.equals("heran") || Build.DEVICE.equals("scifly_m202_1G"))) {
            if (mWiredPreference != null) {
                getPreferenceScreen().removePreference(mWiredPreference);
            }
        }
        if (PLATFORM.equals(Device.SCIFLY_PLATFORM_BOX)) {
            if (mWifiPreference != null) {
                getPreferenceScreen().removePreference(mWifiPreference);
            }
        }

        if (PLATFORM.equals(Device.SCIFLY_PLATFORM_BOX) || Build.DEVICE.equals("heran")
                || Build.DEVICE.equals("scifly_m202_1G") || Build.DEVICE.equals("BenQ_i500")
                || Build.DEVICE.equals("BenQ_i300") || Build.DEVICE.equals("XShuaiUFO")
                || Build.DEVICE.equals("leader")) {
            if (mWlanPreference != null) {
                getPreferenceScreen().removePreference(mWlanPreference);
            }

            if (mScreenDisplayPreference != null) {
                getPreferenceScreen().removePreference(mScreenDisplayPreference);
            }
        }
    }

    /**
     * set the click listener of preference.
     */
    @Override
    public boolean onPreferenceClick(Preference preference) {
        String key = preference.getKey();
        if (WIRED_KEY.equals(key)) {
            mActivity.addFragment(new EthernetSettingFragment());
        } else if (WIFI_KEY.equals(key)) {
            mActivity.replaceFragment(new WifiSettingsFragment());
        } else if (NET_DIAG_KEY.equals(key)) {
            Utils.intentForward(mActivity, ConnectionActivity.class);
            mActivity.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        } else if (DOWNLOAD_SPEED_KEY.equals(key)) {
            Utils.intentForward(mActivity, DownloadSpeedActivity.class);
            mActivity.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        } else if (BLUE_TOOTH.equals(key)) {
            ((NetworkSettingActivity) getActivity())
                    .startActivity("com.android.settings.bluetooth.BluetoothSettingsActivity");
            mActivity.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
        } else if (SCRREN_DISPLAY.equals(key)) {
            mActivity.replaceFragment(new ScreenDisplayFragment());
        } else if (WLAN.equals(key)) {
            mActivity.replaceFragment(new WlanFragment());
        }
        else if (PPPoE.equals(key)) {
            replacePppoeFragment();
        }
        return false;
    }

    private void replacePppoeFragment() {
        PppoeManager mPppoeManager = null;
        try {
            mPppoeManager = PppoeManager.getInstance(mActivity);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (mPppoeManager!=null) {
            mActivity.replaceFragment(new PppoeFragment());
        }else{
            Utils.showToast(mActivity, R.string.initialize);
        }
    }

    /**
     * update networkState
     */
    public void updateNetworkState() {
        updateEthernetState(getNetworkInfo(ConnectivityManager.TYPE_ETHERNET));
        updateWifiState(getNetworkInfo(ConnectivityManager.TYPE_WIFI));
        updatePppoe();
    }

    private void updatePppoe() {
      if (Utils.isPppoeConnected(mActivity)) {
      mPPPoEPreference.setRightText(mActivity.getResources().getString(R.string.ethernet_enabled));
      mPPPoEPreference.setRightTextColor(true);
     }else{
      mPPPoEPreference.setRightText(mActivity.getResources().getString(R.string.ethernet_disabled));
      mPPPoEPreference.setRightTextColor(false);
     }
    }

    /**
     * get the type of network.
     * 
     * @param type
     * @return
     */
    private NetworkInfo getNetworkInfo(int type) {
        ConnectivityManager cm = (ConnectivityManager) mActivity.getSystemService(Activity.CONNECTIVITY_SERVICE);
        return cm.getNetworkInfo(type);
    }

    /**
     * update state of ethernet.
     * 
     * @param networkInfo
     */
    public void updateEthernetState(NetworkInfo networkInfo) {
        if (mWiredPreference == null) {
            return;
        }
        if (networkInfo != null) {
            Log.d(TAG, "<<<< networkInfo.isConnected()<<<" + networkInfo.isConnected());
            Log.d(TAG, "<<<ethernet<networkInfo.getState() == State.CONNECTED<<<<"
                    + (networkInfo.getState() == State.CONNECTED));
            boolean pppoeStatus = false;
            if (Utils.isPppoeConnected(mActivity)) {
                pppoeStatus =true;
            }else{
                pppoeStatus=false;
            }
            if (networkInfo.getState() == State.CONNECTED||pppoeStatus) {
                mWiredPreference.setRightText(mActivity.getResources().getString(R.string.ethernet_enabled));
                mWiredPreference.setRightTextColor(true);
            } else {
                mWiredPreference.setRightText(mActivity.getResources().getString(R.string.ethernet_disabled));
                mWiredPreference.setRightTextColor(false);
            }
        }
    }

    /**
     * update state of wifi.
     * 
     * @param networkInfo
     */
    public void updateWifiState(NetworkInfo networkInfo) {
        if (mWifiPreference == null) {
            return;
        }
        Log.d(TAG, "<<wifi<<networkInfo == nul<<<<" + (networkInfo == null));
        Log.d(TAG, "<<wifi<<!networkInfo.isAvailable() <<<<" + (!networkInfo.isAvailable()));
        Log.d(TAG, "<<wifi<<networkInfo.getState() == State.DISCONNECTED <<<<"
                + (networkInfo.getState() == State.DISCONNECTED));
        if (networkInfo == null || !networkInfo.isAvailable() || networkInfo.getState() == State.DISCONNECTED) {
            mWifiPreference.setRightText(mActivity.getResources().getString(R.string.ethernet_disabled));
            mWifiPreference.setRightTextColor(false);
        } else {
            String ssid = networkInfo.getExtraInfo();
            mWifiPreference.setRightText((ssid == null ? "" : ssid.replace("\"", "")));
            mWifiPreference.setRightTextColor(true);
        }
    }

    @Override
    public void onResume() {
        Log.d(TAG, "<<<fragment<<on resume<<<<<");
        super.onResume();
        updateNetworkState();
    }
}
