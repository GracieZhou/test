
package com.eostek.isynergy.setmeup.network;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import scifly.middleware.network.AccessPoint;
import scifly.middleware.network.WifiConfig;
import scifly.middleware.network.WifiManagerGlobal;
import android.app.Service;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiConfiguration.AuthAlgorithm;
import android.net.wifi.WifiConfiguration.KeyMgmt;
import android.net.wifi.WifiEnterpriseConfig;
import android.net.wifi.WifiEnterpriseConfig.Phase2;
import android.net.wifi.WifiManager;

public class NetworkLogic {
    private Context mContext;

    private NetworkFragment mNetworkFragment;

    WifiManager mWifiManager;

    private WifiManagerGlobal mWifiManagerGlobal;

    private ConnectivityManager mConnManager;

    public boolean isNetworkConnected;

    public WifiConfiguration mWifiCong;

    public NetworkLogic(NetworkFragment networkFragment) {
        this.mNetworkFragment = networkFragment;
        this.mContext = networkFragment.getActivity();

        this.mConnManager = (ConnectivityManager) mContext.getSystemService(Service.CONNECTIVITY_SERVICE);
        this.mWifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        this.mWifiManagerGlobal = new WifiManagerGlobal(mContext);
    }

    public String getNetworkTypeName() {
        return mConnManager.getActiveNetworkInfo().getTypeName();
    }

    /**
     * open wifi switch
     * 
     * @return
     */
    public boolean openWifi() {
        if (!mWifiManager.isWifiEnabled()) {
            mWifiManager.setWifiEnabled(true);
        }
        return true;

    }

    public boolean isNetworkConnected() {
        if (null == mConnManager.getActiveNetworkInfo()) {
            return false;
        }
        return mConnManager.getActiveNetworkInfo().isConnected();
    }

    public List<ScanResult> getScanResults() {
        return mWifiManager.getScanResults();
    }

    /**
     * connect wifi
     * 
     * @param pwd
     */
    public void connectWifi(AccessPoint selectAccessPoint, String wifiPassword) {
        if (wifiPassword != null && null != selectAccessPoint) {
            mWifiManagerGlobal.connect(getConfig(selectAccessPoint, wifiPassword), null);
        }

    }

    private WifiConfig getConfig(AccessPoint accessPoint, String password) {
        WifiConfig config;
        if (null == accessPoint) {
            return null;
        } else if (accessPoint.getConfig() == null) {
            config = accessPoint.buildConfigFromResult(false);
        } else {
            config = accessPoint.getConfig();
        }
        switch ((accessPoint == null) ? AccessPoint.SECURITY_NONE : accessPoint.security) {
            case AccessPoint.SECURITY_NONE:
                config.allowedKeyManagement.set(KeyMgmt.NONE);
                break;
            case AccessPoint.SECURITY_WEP:
                config.allowedKeyManagement.set(KeyMgmt.NONE);
                config.allowedAuthAlgorithms.set(AuthAlgorithm.OPEN);
                config.allowedAuthAlgorithms.set(AuthAlgorithm.SHARED);
                if (password.length() != 0) {
                    int length = password.length();
                    // WEP-40, WEP-104, and 256-bit WEP (WEP-232?)
                    if ((length == 10 || length == 26 || length == 58) && password.matches("[0-9A-Fa-f]*")) {
                        config.wepKeys[0] = password;
                    } else {
                        config.wepKeys[0] = '"' + password + '"';
                    }
                }
                break;
            case AccessPoint.SECURITY_PSK:
                config.allowedKeyManagement.set(KeyMgmt.WPA_PSK);
                if (password.length() != 0) {
                    if (password.matches("[0-9A-Fa-f]{64}")) {
                        config.preSharedKey = password;
                    } else {
                        config.preSharedKey = '"' + password + '"';
                    }
                }
                break;
            case AccessPoint.SECURITY_EAP:
                config.allowedKeyManagement.set(KeyMgmt.WPA_EAP);
                config.allowedKeyManagement.set(KeyMgmt.IEEE8021X);
                config.enterpriseConfig = new WifiEnterpriseConfig();
                config.enterpriseConfig.setPhase2Method(Phase2.NONE);
                break;
            default:
                break;
        }
        return config;
    }

    /**
     * get AccessPoint by ssid
     * 
     * @param ssid
     * @return AccessPoint
     */
    public AccessPoint getAccessPointBySSID(String ssid) {
        List<AccessPoint> accessPoints = constructAccessPoints();
        for (AccessPoint ap : accessPoints) {
            if (ap.ssid.equals(ssid)) {
                return ap;
            }
        }
        return null;
    }

    /**
     * get the list of current wifi.
     * 
     * @return
     */
    private List<AccessPoint> constructAccessPoints() {
        ArrayList<AccessPoint> accessPoints = new ArrayList<AccessPoint>();

        /**
         * Lookup table to more quickly update AccessPoints by only considering
         * objects with the correct SSID. Maps SSID -> List of AccessPoints with
         * the given SSID.
         */
        Multimap<String, AccessPoint> apMap = new Multimap<String, AccessPoint>();

        final List<WifiConfig> configs = mWifiManagerGlobal.getConfiguredNetworks();
        if (configs != null) {
            for (WifiConfig config : configs) {
                AccessPoint accessPoint = new AccessPoint(mContext, config);
                accessPoints.add(accessPoint);
                apMap.put(accessPoint.ssid, accessPoint);
            }
        }

        final List<ScanResult> results = mWifiManager.getScanResults();
        if (results != null) {
            for (ScanResult result : results) {
                // Ignore hidden and ad-hoc networks.
                if (result.SSID == null || result.SSID.length() == 0 || result.capabilities.contains("[IBSS]")) {
                    continue;
                }

                boolean found = false;
                for (AccessPoint accessPoint : apMap.getAll(result.SSID)) {
                    if (accessPoint.update(result)) {
                        found = true;
                    }
                }
                if (!found) {
                    AccessPoint accessPoint = new AccessPoint(mContext, result);
                    accessPoints.add(accessPoint);
                    apMap.put(accessPoint.ssid, accessPoint);
                }
            }
        }

        return accessPoints;
    }

    /** A restricted multimap for use in constructAccessPoints */
    private class Multimap<K, V> {
        private final HashMap<K, List<V>> store = new HashMap<K, List<V>>();

        /** retrieve a non-null list of values with key K */
        List<V> getAll(K key) {
            List<V> values = store.get(key);
            return values != null ? values : Collections.<V> emptyList();
        }

        void put(K key, V val) {
            List<V> curVals = store.get(key);
            if (curVals == null) {
                curVals = new ArrayList<V>(3);
                store.put(key, curVals);
            }
            curVals.add(val);
        }
    }

    public List<WifiConfiguration> getConfiguredNetworks() {
        return mWifiManager.getConfiguredNetworks();
    }

    /**
     * forget the wifi password
     * 
     * @param networkId
     */
    public void forget(int networkId) {
        mWifiManager.forget(networkId, null);
    }
}
