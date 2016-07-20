
package com.android.settings.network.wifi;

import scifly.middleware.network.WifiConfig;
import android.content.Context;
import android.net.NetworkInfo.DetailedState;
import android.net.NetworkInfo.State;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiConfiguration.AuthAlgorithm;
import android.net.wifi.WifiConfiguration.KeyMgmt;
import android.net.wifi.WifiEnterpriseConfig;
import android.net.wifi.WifiEnterpriseConfig.Eap;
import android.net.wifi.WifiEnterpriseConfig.Phase2;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.preference.Preference;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.net.ConnectivityManager;
import android.app.Activity;
import android.net.NetworkInfo;

import com.android.settings.R;

/**
 * the package of wifi,has all information of wifi.
 */
public class AccessPoint extends Preference {
    static final String TAG = "Settings.AccessPoint";

    private static final String KEY_DETAILEDSTATE = "key_detailedstate";

    private static final String KEY_WIFIINFO = "key_wifiinfo";

    private static final String KEY_SCANRESULT = "key_scanresult";

    private static final String KEY_CONFIG = "key_config";

    private static final int[] STATE_SECURED = {
        R.attr.state_encrypted
    };

    private OnKeyDownListener mOnKeyDownListener;

    private static final int[] STATE_NONE = {};

    /**
     * These values are matched in string arrays -- changes must be kept in sync
     */
    static final int SECURITY_NONE = 0;

    static final int SECURITY_WEP = 1;

    static final int SECURITY_PSK = 2;

    static final int SECURITY_EAP = 3;

    enum PskType {
        UNKNOWN, WPA, WPA2, WPA_WPA2
    }

    String ssid;

    String bssid;

    int security;

    int networkId;

    boolean wpsAvailable = false;

    PskType pskType = PskType.UNKNOWN;

    private WifiConfig mConfig;

    /* package */ScanResult mScanResult;

    private int mRssi;

    private WifiInfo mInfo;

    private DetailedState mState;

    static int getSecurity(WifiConfig config) {
        if (config.allowedKeyManagement.get(KeyMgmt.WPA_PSK)) {
            return SECURITY_PSK;
        }
        if (config.allowedKeyManagement.get(KeyMgmt.WPA_EAP) || config.allowedKeyManagement.get(KeyMgmt.IEEE8021X)) {
            return SECURITY_EAP;
        }
        return (config.wepKeys[0] != null) ? SECURITY_WEP : SECURITY_NONE;
    }

    /**
     * @param result
     * @return the security of wifi.
     */
    public static int getSecurity(ScanResult result) {
        if (result.capabilities.contains("WEP")) {
            return SECURITY_WEP;
        } else if (result.capabilities.contains("PSK")) {
            return SECURITY_PSK;
        } else if (result.capabilities.contains("EAP")) {
            return SECURITY_EAP;
        }
        return SECURITY_NONE;
    }

    public String getSecurityString(boolean concise) {
        Context context = getContext();
        switch (security) {
            case SECURITY_EAP:
                return concise ? context.getString(R.string.wifi_security_short_eap) : context
                        .getString(R.string.wifi_security_eap);
            case SECURITY_PSK:
                switch (pskType) {
                    case WPA:
                        return concise ? context.getString(R.string.wifi_security_short_wpa) : context
                                .getString(R.string.wifi_security_wpa);
                    case WPA2:
                        return concise ? context.getString(R.string.wifi_security_short_wpa2) : context
                                .getString(R.string.wifi_security_wpa2);
                    case WPA_WPA2:
                        return concise ? context.getString(R.string.wifi_security_short_wpa_wpa2) : context
                                .getString(R.string.wifi_security_wpa_wpa2);
                    case UNKNOWN:
                    default:
                        return concise ? context.getString(R.string.wifi_security_short_psk_generic) : context
                                .getString(R.string.wifi_security_psk_generic);
                }
            case SECURITY_WEP:
                return concise ? context.getString(R.string.wifi_security_short_wep) : context
                        .getString(R.string.wifi_security_wep);
            case SECURITY_NONE:
            default:
                return concise ? "" : context.getString(R.string.wifi_security_none);
        }
    }

    private static PskType getPskType(ScanResult result) {
        boolean wpa = result.capabilities.contains("WPA-PSK");
        boolean wpa2 = result.capabilities.contains("WPA2-PSK");
        if (wpa2 && wpa) {
            return PskType.WPA_WPA2;
        } else if (wpa2) {
            return PskType.WPA2;
        } else if (wpa) {
            return PskType.WPA;
        } else {
            Log.w(TAG, "Received abnormal flag string: " + result.capabilities);
            return PskType.UNKNOWN;
        }
    }

    AccessPoint(Context context, WifiConfig config) {
        super(context);
        setLayoutResource(R.layout.preference_wifi_custom);
        loadConfig(config);
        refresh();
    }

    AccessPoint(Context context, ScanResult result) {
        super(context);
        // setWidgetLayoutResource(R.layout.preference_widget_wifi_signal);
        setLayoutResource(R.layout.preference_wifi_custom);
        loadResult(result);
        refresh();
    }

    AccessPoint(Context context, Bundle savedState) {
        super(context);
        // setWidgetLayoutResource(R.layout.preference_widget_wifi_signal);
        setWidgetLayoutResource(R.layout.preference_wifi_custom);
        mConfig = savedState.getParcelable(KEY_CONFIG);
        if (mConfig != null) {
            loadConfig(mConfig);
        }
        mScanResult = (ScanResult) savedState.getParcelable(KEY_SCANRESULT);
        if (mScanResult != null) {
            loadResult(mScanResult);
        }
        mInfo = (WifiInfo) savedState.getParcelable(KEY_WIFIINFO);
        if (savedState.containsKey(KEY_DETAILEDSTATE)) {
            mState = DetailedState.valueOf(savedState.getString(KEY_DETAILEDSTATE));
        }
        update(mInfo, mState);
    }

    /**
     * save the state of wifi, such as saved,connected.
     * 
     * @param savedState
     */
    public void saveWifiState(Bundle savedState) {
        savedState.putParcelable(KEY_CONFIG, mConfig);
        savedState.putParcelable(KEY_SCANRESULT, mScanResult);
        savedState.putParcelable(KEY_WIFIINFO, mInfo);
        if (mState != null) {
            savedState.putString(KEY_DETAILEDSTATE, mState.toString());
        }
    }

    public void loadConfig(WifiConfig config) {
        ssid = (config.SSID == null ? "" : removeDoubleQuotes(config.SSID));
        bssid = config.BSSID;
        security = getSecurity(config);
        networkId = config.networkId;
        mRssi = Integer.MAX_VALUE;
        mConfig = config;
        refresh();
    }

    private void loadResult(ScanResult result) {
        ssid = result.SSID;
        bssid = result.BSSID;
        security = getSecurity(result);
        wpsAvailable = security != SECURITY_EAP && result.capabilities.contains("WPS");
        if (security == SECURITY_PSK)
            pskType = getPskType(result);
        networkId = -1;
        mRssi = result.level;
        mScanResult = result;
    }

    ImageView mWifiConfigImage;

    TextView mWifiStateText;

    ImageView mRightArrowImg;

    View view;

    int i = 0;

    private OnClickListener mRightArrowClickListener;

    @Override
    protected void onBindView(View view) {
        super.onBindView(view);
        this.view = view;
        mWifiStateText = (TextView) view.findViewById(R.id.wifi_state);
        mRightArrowImg = (ImageView) view.findViewById(R.id.wifi_config_detail);
        mRightArrowImg.setOnClickListener(mRightArrowClickListener);
        ImageView signal = (ImageView) view.findViewById(R.id.wifi_signal);
        if (mRssi == Integer.MAX_VALUE) {
            signal.setImageLevel(0);
            signal.setImageDrawable(getContext().getTheme().obtainStyledAttributes(new int[] {
                R.attr.wifi_signal
            }).getDrawable(0));
            signal.setImageState((security != SECURITY_NONE) ? STATE_SECURED : STATE_NONE, true);
            signal.setVisibility(View.INVISIBLE);
        } else {
            signal.setImageLevel(getLevel());
            signal.setImageState((security != SECURITY_NONE) ? STATE_SECURED : STATE_NONE, true);
        }
        refresh();
    }

    @Override
    public int compareTo(Preference preference) {
        if (!(preference instanceof AccessPoint)) {
            return 1;
        }
        AccessPoint other = (AccessPoint) preference;
        // Active one goes first.
        if (mInfo != null && other.mInfo == null)
            return -1;
        if (mInfo == null && other.mInfo != null)
            return 1;

        // Reachable one goes before unreachable one.
        if (mRssi != Integer.MAX_VALUE && other.mRssi == Integer.MAX_VALUE)
            return -1;
        if (mRssi == Integer.MAX_VALUE && other.mRssi != Integer.MAX_VALUE)
            return 1;

        // Configured one goes before unconfigured one.
        if (networkId != WifiConfiguration.INVALID_NETWORK_ID
                && other.networkId == WifiConfiguration.INVALID_NETWORK_ID)
            return -1;
        if (networkId == WifiConfiguration.INVALID_NETWORK_ID
                && other.networkId != WifiConfiguration.INVALID_NETWORK_ID)
            return 1;

        // Sort by signal strength.
        int difference = WifiManager.compareSignalLevel(other.mRssi, mRssi);
        if (difference != 0) {
            return difference;
        }
        // Sort by ssid.
        return ssid.compareToIgnoreCase(other.ssid);
    }

    @Override
    public boolean equals(Object other) {
        if (!(other instanceof AccessPoint))
            return false;
        return (this.compareTo((AccessPoint) other) == 0);
    }

    @Override
    public int hashCode() {
        int result = 0;
        if (mInfo != null)
            result += 13 * mInfo.hashCode();
        result += 19 * mRssi;
        result += 23 * networkId;
        result += 29 * ssid.hashCode();
        return result;
    }

    boolean update(ScanResult result) {
        if (ssid.equals(result.SSID) && security == getSecurity(result)) {
            if (WifiManager.compareSignalLevel(result.level, mRssi) > 0) {
                int oldLevel = getLevel();
                mRssi = result.level;
                if (getLevel() != oldLevel) {
                    notifyChanged();
                }
            }
            // This flag only comes from scans, is not easily saved in config
            if (security == SECURITY_PSK) {
                pskType = getPskType(result);
            }
            refresh();
            return true;
        }
        return false;
    }

    void update(WifiInfo info, DetailedState state) {
        boolean reorder = false;
        if (info != null && networkId != WifiConfiguration.INVALID_NETWORK_ID && networkId == info.getNetworkId()) {
            reorder = (mInfo == null);
            mRssi = info.getRssi();
            mInfo = info;
            mState = state;
            refresh();
        } else if (mInfo != null) {
            reorder = true;
            mInfo = null;
            mState = null;
            refresh();
        }
        if (reorder) {
            notifyHierarchyChanged();
        }
    }

    /**
     * get the level of wifi.
     * 
     * @return
     */
    int getLevel() {
        if (mRssi == Integer.MAX_VALUE) {
            return -1;
        }
        return WifiManager.calculateSignalLevel(mRssi, 4);
    }

    WifiConfig getConfig() {
        return mConfig;
    }

    WifiInfo getInfo() {
        return mInfo;
    }

    DetailedState getState() {
        return mState;
    }

    static String removeDoubleQuotes(String string) {
        int length = string.length();
        if ((length > 1) && (string.charAt(0) == '"') && (string.charAt(length - 1) == '"')) {
            return string.substring(1, length - 1);
        }
        return string;
    }

    static String convertToQuotedString(String string) {
        return "\"" + string + "\"";
    }

    /** Updates the title and summary; may indirectly call notifyChanged() */
    public void refresh() {
        setTitle(ssid);

        Context context = getContext();

        if (mConfig != null && mConfig.status == WifiConfiguration.Status.DISABLED) {
            switch (mConfig.disableReason) {
                case WifiConfiguration.DISABLED_AUTH_FAILURE:
                    setSummary(context.getString(R.string.wifi_disabled_password_failure));
                    setWifiStateText(context.getString(R.string.wifi_disabled_password_failure));
                    break;
                case WifiConfiguration.DISABLED_DHCP_FAILURE:
                case WifiConfiguration.DISABLED_DNS_FAILURE:
                    setSummary(context.getString(R.string.wifi_disabled_network_failure));
                    break;
                case WifiConfiguration.DISABLED_UNKNOWN_REASON:
                    setSummary(context.getString(R.string.wifi_disabled_generic));
            }
        } else if (mRssi == Integer.MAX_VALUE) { // Wifi out of range
            setSummary(context.getString(R.string.wifi_not_in_range));
        } else if (mState != null) { // This is the active connection
            setWifiStateText(context.getString(R.string.wifi_display_status_connecting));
            String stateStr = getSummaryState(context, mState);
            setSummary(stateStr);
            if (mState == DetailedState.CONNECTED) {
                setWifiStateText(context.getString(R.string.wifi_display_status_connected));
            }
        } else { // In range, not disabled.
            StringBuilder summary = new StringBuilder();
            if (mConfig != null) { // Is saved network

                if (getCurrentWifiName().equals(ssid)) {
                    setWifiStateText(context.getString(R.string.wifi_display_status_connected));
                } else {
                    if (isPasswordSaved()) {
                        summary.append(context.getString(R.string.wifi_remembered));
                        setWifiStateText(context.getString(R.string.wifi_remembered));
                    } else {
                        summary.append(context.getString(R.string.wifi_configured));
                    }
                }
            }

            if (security != SECURITY_NONE) {
                String securityStrFormat;
                if (summary.length() == 0) {
                    securityStrFormat = context.getString(R.string.wifi_secured_first_item);
                } else {
                    securityStrFormat = context.getString(R.string.wifi_secured_second_item);
                }
                summary.append(String.format(securityStrFormat, getSecurityString(true)));
            }

            if (mConfig == null && wpsAvailable) { // Only list WPS available
                                                   // for unsaved networks
                if (summary.length() == 0) {
                    summary.append(context.getString(R.string.wifi_wps_available_first_item));
                } else {
                    summary.append(context.getString(R.string.wifi_wps_available_second_item));
                }
            }
            setSummary(summary.toString());
        }
    }

    private void setWifiStateText(String string) {
        if (mWifiStateText != null) {
            mWifiStateText.setText("" + string);
        }
    }

    /**
     * Generate and save a default wifiConfiguration with common values. Can
     * only be called for unsecured networks.
     * 
     * @hide
     */
    protected void generateOpenNetworkConfig() {
        if (security != SECURITY_NONE)
            throw new IllegalStateException();
        if (mConfig != null)
            return;
        mConfig = new WifiConfig();
        mConfig.SSID = AccessPoint.convertToQuotedString(ssid);
        mConfig.allowedKeyManagement.set(KeyMgmt.NONE);
    }

    public int getRssi() {
        return mRssi;
    }

    public OnKeyDownListener getOnKeyDownListener() {
        return mOnKeyDownListener;
    }

    public void setOnKeyDownListener(OnKeyDownListener mOnKeyDownListener) {
        this.mOnKeyDownListener = mOnKeyDownListener;
    }

    public ScanResult getScanResult() {
        return mScanResult;
    }

    public void setConfig(WifiConfig mConfig) {
        this.mConfig = mConfig;
    }

    public void setRssi(int mRssi) {
        this.mRssi = mRssi;
    }

    public boolean isPasswordSaved() {
        if (mConfig != null) {
            if (!TextUtils.isEmpty(mConfig.wepKeys[0]) || !TextUtils.isEmpty(mConfig.preSharedKey)) {
                return true;
            }
        }
        return false;
    }

    public WifiConfig buildConfigFromResult(boolean isAutoGetIp) {
        WifiConfig config = new WifiConfig();
        ScanResult result = getScanResult();
        config.SSID = AccessPoint.convertToQuotedString(result.SSID);
        config.BSSID = result.BSSID;
        config.networkId = this.networkId;
        config.hiddenSSID = false;
        switch (AccessPoint.getSecurity(result)) {
            case AccessPoint.SECURITY_NONE:
                config.allowedKeyManagement.set(KeyMgmt.NONE);
                break;
            case AccessPoint.SECURITY_WEP:
                config.allowedKeyManagement.set(KeyMgmt.NONE);
                config.allowedAuthAlgorithms.set(AuthAlgorithm.OPEN);
                config.allowedAuthAlgorithms.set(AuthAlgorithm.SHARED);
                break;
            case AccessPoint.SECURITY_PSK:
                config.allowedKeyManagement.set(KeyMgmt.WPA_PSK);
                break;
            case AccessPoint.SECURITY_EAP:
                config.allowedKeyManagement.set(KeyMgmt.WPA_EAP);
                config.allowedKeyManagement.set(KeyMgmt.IEEE8021X);
                config.enterpriseConfig = new WifiEnterpriseConfig();
                config.enterpriseConfig.setEapMethod(Eap.PEAP);
                config.enterpriseConfig.setPhase2Method(Phase2.NONE);
                break;
        }

        setRssi(result.level);

        return config;
    }

    public boolean isAccessPointSaved() {
        if (mConfig != null && isPasswordSaved()) {
            return true;
        }
        return false;
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {

        // Do nothing when key up.
        if (event.getAction() == KeyEvent.ACTION_UP) {
            return super.onKey(v, keyCode, event);
        }

        if (mOnKeyDownListener != null) {
            return mOnKeyDownListener.onKeyDown(this, v, keyCode, event);
        }
        return super.onKey(v, keyCode, event);
    }

    public interface OnKeyDownListener {

        boolean onKeyDown(Preference pre, View v, int keyCode, KeyEvent event);

    }

    public void setRightArrowClickListener(OnClickListener l) {
        mRightArrowClickListener = l;
    }

    @Override
    public void setSummary(CharSequence summary) {
    }

    static String getSummaryState(Context context, DetailedState state) {
        String[] formats = context.getResources().getStringArray(R.array.wifi_status);
        int index = state.ordinal();
        if (index >= formats.length || formats[index].length() == 0) {
            return null;
        }
        return String.format(formats[index]);
    }

    private String getCurrentWifiName() {
        ConnectivityManager cm = (ConnectivityManager) getContext().getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo networkInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (networkInfo == null || !networkInfo.isAvailable() || networkInfo.getState() == State.DISCONNECTED) {
            return "";
        } else {
            String ssid = networkInfo.getExtraInfo();
            return ssid == null ? "" : ssid.replace("\"", "");
        }
    }
}
