
package com.android.settings.network;

import android.app.Activity;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class NetworkSettingLogic {

    private NetworkSettingActivity mContext;

    private NetworkSettingHolder mHolder;

    public static final int ETHERNET_STATE_DISABLED = 0;

    public static final int ETHERNET_STATE_ENABLED = 1;

    public NetworkSettingLogic(NetworkSettingActivity networkSettingActivity, NetworkSettingHolder holder) {
        mContext = networkSettingActivity;
        mHolder = holder;
    }

    /**
     * Get ethernet state.
     */
    public int getEthernetState() {
        int state = ETHERNET_STATE_DISABLED;

        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET);
		if (info.isConnected()) {
			state = ETHERNET_STATE_ENABLED;
		} else {
		}
		return state;
    }

    public void updateStates() {
        mHolder.updateEthernetState(getEthernetState());
        mHolder.updateWifiState(getWifiState());
    }

    private NetworkInfo getWifiState() {

        ConnectivityManager cm = (ConnectivityManager) mContext.getSystemService(Activity.CONNECTIVITY_SERVICE);

        return cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
    }

}
