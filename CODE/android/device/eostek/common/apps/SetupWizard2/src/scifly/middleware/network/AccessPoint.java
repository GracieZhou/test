
package scifly.middleware.network;

import android.content.Context;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration.AuthAlgorithm;
import android.net.wifi.WifiConfiguration.KeyMgmt;
import android.net.wifi.WifiEnterpriseConfig;
import android.net.wifi.WifiEnterpriseConfig.Eap;
import android.net.wifi.WifiEnterpriseConfig.Phase2;
import android.net.wifi.WifiManager;
import android.util.Log;

/**
 * the package of wifi,has all information of wifi.
 */
public class AccessPoint {
    static final String TAG = "AccessPoint";

    /**
     * These values are matched in string arrays -- changes must be kept in sync
     */
    public static final int SECURITY_NONE = 0;

    public static final int SECURITY_WEP = 1;

    public static final int SECURITY_PSK = 2;

    public static final int SECURITY_EAP = 3;

    enum PskType {
        UNKNOWN, WPA, WPA2, WPA_WPA2
    }

    public String ssid;

    public String bssid;

    public int security;

    public int networkId;

    boolean wpsAvailable = false;

    PskType pskType = PskType.UNKNOWN;

    private WifiConfig mConfig;

    /* package */ScanResult mScanResult;

    private int mRssi;

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

    public boolean update(ScanResult result) {
        if (ssid.equals(result.SSID) && security == getSecurity(result)) {
            if (WifiManager.compareSignalLevel(result.level, mRssi) > 0) {
                mRssi = result.level;
            }
            // This flag only comes from scans, is not easily saved in config
            if (security == SECURITY_PSK) {
                pskType = getPskType(result);
            }
            return true;
        }
        return false;
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

    public AccessPoint(Context context, WifiConfig config) {
        loadConfig(config);
    }

    public AccessPoint(Context context, ScanResult result) {
        loadResult(result);
    }

    public void loadConfig(WifiConfig config) {
        ssid = (config.SSID == null ? "" : removeDoubleQuotes(config.SSID));
        bssid = config.BSSID;
        security = getSecurity(config);
        networkId = config.networkId;
        mRssi = Integer.MAX_VALUE;
        mConfig = config;
    }

    private void loadResult(ScanResult result) {
        ssid = result.SSID;
        bssid = result.BSSID;
        security = getSecurity(result);
        wpsAvailable = security != SECURITY_EAP && result.capabilities.contains("WPS");
        if (security == SECURITY_PSK) {
            pskType = getPskType(result);
        }
        networkId = -1;
        mRssi = result.level;
        mScanResult = result;
    }

    public WifiConfig getConfig() {
        return mConfig;
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

    public int getRssi() {
        return mRssi;
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
            default:
                break;
        }

        setRssi(result.level);

        return config;
    }

}
