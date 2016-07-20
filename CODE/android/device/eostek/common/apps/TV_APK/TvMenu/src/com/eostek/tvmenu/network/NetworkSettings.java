
package com.eostek.tvmenu.network;

import java.util.ArrayList;
import java.util.List;

import com.mstar.android.pppoe.PppoeManager;

import android.content.Context;
import android.net.EthernetManager;
import android.net.IEthernetManager;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.util.Log;

public class NetworkSettings {

    private static final String TAG = "MSettings.NetworkSettings";

    private Context mContext;

    private WifiManager mWifiManager;

    private EthernetManager mEthernetManager;

    private PppoeManager mPPPoEManager;


    private static List<INetworkSettingsListener> mSettingListener = new ArrayList<INetworkSettingsListener>();

    public NetworkSettings(Context context) {
        this.mContext = context;
    }

    public WifiManager getWifiManager() {
        if (mWifiManager == null) {
            mWifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        }

        return mWifiManager;
    }

    public EthernetManager getEthernetManager(Context Context) {
        mContext = Context;
        if (mEthernetManager == null) {
            mEthernetManager = (EthernetManager) Context.getSystemService(Context.ETHERNET_SERVICE);
        }

        return mEthernetManager;
    }

    public PppoeManager getPPPoEManager() {
        if (mPPPoEManager == null) {
            mPPPoEManager = PppoeManager.getInstance(mContext);
        }

        return mPPPoEManager;
    }

    public boolean isWifiConnected() {
        WifiManager wifiManager = getWifiManager();
        // wifi is disabled
        if (!wifiManager.isWifiEnabled()) {
            return false;
        }

        // wifi have not connected
        WifiInfo info = wifiManager.getConnectionInfo();
        if (info == null || info.getSSID() == null
                || info.getNetworkId() == WifiConfiguration.INVALID_NETWORK_ID) {
            return false;
        }

        return true;
    }

    public boolean isEthernetEnabled() {
        EthernetManager ethernet = getEthernetManager(mContext);
//        if (EthernetManager.ETHERNET_STATE_ENABLED == ethernet.getState()) {
        if(ethernet.isEnabled()){
            return true;
        }

        return false;
    }

    public WifiConfiguration getWifiConfiguredNetwork() {
        if (mWifiManager == null) {
            getWifiManager();
        }
        //
        if (mWifiManager.isWifiEnabled()) {
            WifiInfo wifi = mWifiManager.getConnectionInfo();
            Log.d(TAG, "ssid, " + wifi.getSSID());

            List<WifiConfiguration> configs = mWifiManager.getConfiguredNetworks();
            if (configs == null) {
                return null;
            }
            String ssid = wifi.getSSID();
            for (WifiConfiguration config : configs) {
                Log.d(TAG, "config.SSID, " + config.SSID);
                if (ssid.equals(config.SSID)) {
                    return config;
                }
            }
        }

        return null;
    }
    
    public WifiConfiguration getWifiConfiguredNetwork(String ssid) {
        if (mWifiManager == null) {
            getWifiManager();
        }
        ssid ="\""+ssid+"\"";
        Log.d(TAG, "now SSID, " + ssid);
        //
        if (mWifiManager.isWifiEnabled()) {
            List<WifiConfiguration> configs = mWifiManager.getConfiguredNetworks();
            if (configs == null) {
                return null;
            }
            for (WifiConfiguration config : configs) {
                Log.d(TAG, "config.SSID, " + config.SSID);
                if (ssid.equals(config.SSID)) {
                    return config;
                }
            }
        }

        return null;
    }

    public void addNetworkSettingListener(INetworkSettingsListener listener) {
        mSettingListener.add(listener);
        Log.d(TAG, "size, " + mSettingListener.size());
    }

}
