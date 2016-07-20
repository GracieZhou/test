
package com.android.settings.network.ethernet;

import java.net.InetAddress;

import android.content.Context;
import android.net.NetworkUtils;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceChangeListener;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.android.settings.R;
import com.android.settings.SettingPreference;
import com.android.settings.SimpleSettingsActivity;
import com.android.settings.network.wifi.LinkPropertyInputDialog;
import com.android.settings.network.wifi.LinkPropertyInputDialog.PropertyChangeListener;
import com.android.settings.util.Utils;

/**
 * EthernetSettingFragment
 */
public class SimpleEthernetSettingFragment extends PreferenceFragment implements OnPreferenceClickListener,
        OnPreferenceChangeListener {

    private static final String TAG = "EthernetSetting";

    private SimpleSettingsActivity mActivity;

    private SimpleEthernetSettingLogic mSimpleEthernetSettingLogic;

    private static final String ETHERNET_SWITCH = "ethernet_switch";

    private static final String AUTO_KEY = "auto_get_ip";

    private static final String IPADRESS = "IP_address";

    private static final String MASK = "wifi_mask";

    private static final String GATEWAY = "wifi_gateway";

    private static final String DNS = "wifi_dns";

    private SettingPreference mEthernetPreference;

    private SettingPreference mAutoPreference;

    private SettingPreference mIpPreference;

    private SettingPreference mMaskPreference;

    private SettingPreference mGatePreference;

    private SettingPreference mDnsPreference;

    public WifiManager mWifiManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // Load the preferences from an XML resource
        addPreferencesFromResource(R.xml.preference_ethernet);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.setting_preference_fragment, container, false);
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = (SimpleSettingsActivity) this.getActivity();
        mActivity.setSubTitle(R.string.ethernet_setting);

        mWifiManager = (WifiManager) mActivity.getSystemService(Context.WIFI_SERVICE);
        // get the information of current network.
        mSimpleEthernetSettingLogic = mActivity.getEthernetSettingLogic();
        mSimpleEthernetSettingLogic.processInfo();
        // find preference
        mEthernetPreference = (SettingPreference) findPreference(ETHERNET_SWITCH);
        mAutoPreference = (SettingPreference) findPreference(AUTO_KEY);
        mIpPreference = (SettingPreference) findPreference(IPADRESS);
        mMaskPreference = (SettingPreference) findPreference(MASK);
        mGatePreference = (SettingPreference) findPreference(GATEWAY);
        mDnsPreference = (SettingPreference) findPreference(DNS);

        mEthernetPreference.setOnPreferenceClickListener(this);
        mEthernetPreference.setChecked(mSimpleEthernetSettingLogic.mEthernetManagerGlobal.isEnabled());
        isRemovePreference(!mSimpleEthernetSettingLogic.mEthernetManagerGlobal.isEnabled());
        // set contains of preference
        mAutoPreference.setOnPreferenceChangeListener(this);
        // set contains of preference
        initViews();
        changItemBackground(mSimpleEthernetSettingLogic.isAutoGetIp);
        Log.d(TAG, "<<<<<<<mEthernetSettingLogic.isAutoGetIp<<<<<<<<<" + mSimpleEthernetSettingLogic.isAutoGetIp);
    }

    @Override
    public boolean onPreferenceClick(Preference preference) {
        showInputDialog(preference.getKey());
        return false;
    }

    /**
     * change the click status of preference.
     * 
     * @param enable
     */
    private void changItemBackground(boolean enable) {
        mIpPreference.setRightTextColor(enable);
        mMaskPreference.setRightTextColor(enable);
        mGatePreference.setRightTextColor(enable);
        mDnsPreference.setRightTextColor(enable);
        mIpPreference.setOnPreferenceClickListener(enable ? null : this);
        mMaskPreference.setOnPreferenceClickListener(enable ? null : this);
        mGatePreference.setOnPreferenceClickListener(enable ? null : this);
        mDnsPreference.setOnPreferenceClickListener(enable ? null : this);
    }

    /**
     * show the inputdialog to input ip,netmask,gateway or dns.
     */
    public void showInputDialog(String key) {
        Log.d(TAG, "<<<<<key<<<<<<" + key);
        // show ip dailog and set ip
        if (ETHERNET_SWITCH.equals(key)) {
            mEthernetPreference.toggleButton();
            mSimpleEthernetSettingLogic.mEthernetManagerGlobal.setEnabled(mEthernetPreference.isChecked());
            isRemovePreference(!mEthernetPreference.isChecked());
        }
        if (IPADRESS.equals(key)) {
            LinkPropertyInputDialog dialog = new LinkPropertyInputDialog(((SimpleSettingsActivity) getActivity()),
                    ((SimpleSettingsActivity) getActivity()).getEthernetSettingLogic().mIpStr);
            dialog.show();
            dialog.setTitle(R.string.ethernet_setting, R.string.wifi_ip_address);
            dialog.setPropertyChangeListener(new PropertyChangeListener() {
                @Override
                public void onPropertyChange(String value) {
                    Log.d(TAG, "mGatewayStr value received ::" + value);
                    if (TextUtils.isEmpty(value)) {
                        Log.d(TAG, "wifi_ip_settings_invalid_ip_address");
                        return;
                    }
                    InetAddress inetAddr = null;
                    try {
                        inetAddr = NetworkUtils.numericToInetAddress(value);
                        if (inetAddr != null) {
                            mSimpleEthernetSettingLogic.mIpStr = value;
                            Log.d(TAG, "<<<ip<value<<<<<<<<<<<<<<<<<" + value);
                            mIpPreference.setRightText(value);
                        }
                    } catch (IllegalArgumentException e) {
                        Log.d(TAG, "wifi_ip_settings_invalid_ip_address");
                        return;
                    }
                }
            });
        }
        // show SubnetMask dailog and set SubnetMask
        if (MASK.equals(key)) {
            LinkPropertyInputDialog dialog = new LinkPropertyInputDialog((SimpleSettingsActivity) getActivity(),
                    ((SimpleSettingsActivity) getActivity()).getEthernetSettingLogic().mSubnetMaskStr);
            dialog.show();
            dialog.setTitle(R.string.ethernet_setting, R.string.wifi_mask);
            dialog.setPropertyChangeListener(new PropertyChangeListener() {
                public void onPropertyChange(String value) {
                    Log.i(TAG, "mGatewayStr value received ::" + value);
                    if (NetworkSettingUtils.isValidMaskString(value)) {
                        mSimpleEthernetSettingLogic.mSubnetMaskStr = value;
                        Log.d(TAG, "<<<mSubnetMaskStr<value<<<<<<<<<<<<<<<<<" + value);
                        mMaskPreference.setRightText(value);
                    } else {
                        Utils.showToast(mActivity, R.string.wifi_invliad_subnet_mask);
                        Log.i(TAG, "Invalid subnet mask!");
                    }
                }
            });
        }
        // show gateway dailog and set gateway
        if (GATEWAY.equals(key)) {
            LinkPropertyInputDialog dialog = new LinkPropertyInputDialog((SimpleSettingsActivity) getActivity(),
                    ((SimpleSettingsActivity) getActivity()).getEthernetSettingLogic().mGatewayStr);
            dialog.show();
            dialog.setTitle(R.string.ethernet_setting, R.string.gateway);
            dialog.setPropertyChangeListener(new PropertyChangeListener() {
                @Override
                public void onPropertyChange(String value) {
                    Log.d(TAG, "mGatewayStr value received ::" + value);
                    if (TextUtils.isEmpty(value)) {
                        Log.i(TAG, "wifi_ip_settings_invalid_ip_address");
                        return;
                    }

                    InetAddress gatewayAddr = null;
                    try {
                        gatewayAddr = NetworkUtils.numericToInetAddress(value);
                        if (gatewayAddr != null) {
                            mSimpleEthernetSettingLogic.mGatewayStr = value;
                            Log.d(TAG, "<<<gateway<value<<<<<<<<<<<<<<<<<" + value);
                            mGatePreference.setRightText(value);
                        }
                    } catch (IllegalArgumentException e) {
                        return;
                    }
                }
            });
        }
        // show dns dailog and set dns
        if (DNS.equals(key)) {
            LinkPropertyInputDialog dialog = new LinkPropertyInputDialog(((SimpleSettingsActivity) getActivity()),
                    ((SimpleSettingsActivity) getActivity()).getEthernetSettingLogic().mDnsStr);
            dialog.show();
            dialog.setTitle(R.string.ethernet_setting, R.string.wifi_dns);
            dialog.setPropertyChangeListener(new PropertyChangeListener() {
                @Override
                public void onPropertyChange(String value) {
                    Log.i(TAG, "mDnsStr value received ::" + value);
                    if (TextUtils.isEmpty(value)) {
                        Log.i(TAG, "wifi_ip_settings_invalid_dns_address");
                        return;
                    }
                    InetAddress dnsAddr = null;
                    try {
                        dnsAddr = NetworkUtils.numericToInetAddress(value);
                        if (dnsAddr != null) {
                            mSimpleEthernetSettingLogic.mDnsStr = value;
                            Log.d(TAG, "<<<dns<value<<<<<<<<<<<<<<<<<" + value);
                            mDnsPreference.setRightText(value);
                        }
                    } catch (IllegalArgumentException e) {
                        Log.i(TAG, "wifi_ip_settings_invalid_dns_address");
                        return;
                    }
                }
            });
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        initViews();
    }

    private void initViews() {
        if (mSimpleEthernetSettingLogic.isAutoGetIp) {
            mAutoPreference.setRightText(getActivity().getString(R.string.wifi_link_property_auto));
        } else {
            mAutoPreference.setRightText(getActivity().getString(R.string.wifi_link_property_manual));
        }

        Log.d(TAG, "<mIpStr<<<<<<<<<<<<<<" + mSimpleEthernetSettingLogic.mIpStr);
        Log.d(TAG, "<mSubnetMaskStr<<<<<<<<<<<<<<" + mSimpleEthernetSettingLogic.mSubnetMaskStr);
        Log.d(TAG, "<mGatewayStr<<<<<<<<<<<<<<" + mSimpleEthernetSettingLogic.mGatewayStr);
        Log.d(TAG, "<mDnsStr<<<<<<<<<<<<<<" + mSimpleEthernetSettingLogic.mDnsStr);
        mIpPreference.setRightText(mSimpleEthernetSettingLogic.mIpStr);
        mMaskPreference.setRightText(mSimpleEthernetSettingLogic.mSubnetMaskStr);
        mGatePreference.setRightText(mSimpleEthernetSettingLogic.mGatewayStr);
        mDnsPreference.setRightText(mSimpleEthernetSettingLogic.mDnsStr);
    }

    @Override
    public boolean onPreferenceChange(Preference arg0, Object newValue) {
        if (AUTO_KEY.equals(arg0.getKey())) {
            String[] strArray = mActivity.getResources().getStringArray(R.array.auto_ip);
            if (newValue.equals(strArray[0])) {
                mSimpleEthernetSettingLogic.isAutoGetIp = true;
            } else if (newValue.equals(strArray[1])) {
                mSimpleEthernetSettingLogic.isAutoGetIp = false;
            }
            changItemBackground(mSimpleEthernetSettingLogic.isAutoGetIp);
        }
        return true;
    }

    @Override
    public void onPause() {
        super.onPause();
        mSimpleEthernetSettingLogic.saveEthernetConfig();
    }

    /**
     * add or delete the preference.
     * 
     * @param b
     */
    public void isRemovePreference(boolean b) {
        if (b) {
            this.getPreferenceScreen().removePreference(mAutoPreference);
            this.getPreferenceScreen().removePreference(mIpPreference);
            this.getPreferenceScreen().removePreference(mGatePreference);
            this.getPreferenceScreen().removePreference(mMaskPreference);
            this.getPreferenceScreen().removePreference(mDnsPreference);
        } else {
            this.getPreferenceScreen().addPreference(mAutoPreference);
            this.getPreferenceScreen().addPreference(mIpPreference);
            this.getPreferenceScreen().addPreference(mGatePreference);
            this.getPreferenceScreen().addPreference(mMaskPreference);
            this.getPreferenceScreen().addPreference(mDnsPreference);
        }
    }
}
