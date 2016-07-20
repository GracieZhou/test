
package com.android.settings.network;

import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.TextView;
import com.android.settings.R;
import com.android.settings.network.connectivity.ConnectionActivity;
import com.android.settings.network.downloadspeed.DownloadSpeedActivity;
import com.android.settings.network.ethernet.EthernetSettingActivity;
import com.android.settings.network.wifi.WifiSettingsAcitivity;
import com.android.settings.widget.TitleWidget;

public class NetworkSettingHolder implements OnClickListener {

    private NetworkSettingActivity mContext;

    View mEthernetView;

    View mWifiView;

    View mConnectivityView;

    View mDownloadSpeedView;

    View mBluetoothView;

    public NetworkSettingHolder(NetworkSettingActivity networkSettingActivity) {
        this.mContext = networkSettingActivity;
    }

    public void findViews() {
        findItems();
        setTitleWidget();
    }

    public void updateEthernetState(int state) {
        TextView tv = (TextView) mEthernetView.findViewById(R.id.network_item_content);
        Log.d("alisa", "<<<<<<<updateEthernetState<<<<<<state<<<<<<"+state);
        Log.d("alisa", "<<<<<<<ETHERNET_STATE_ENABLED<<<<<<"+NetworkSettingLogic.ETHERNET_STATE_ENABLED);
        Log.d("alisa", "<<<<<<<ETHERNET_STATE_DISABLED<<<<<<"+NetworkSettingLogic.ETHERNET_STATE_DISABLED);
        if (state == NetworkSettingLogic.ETHERNET_STATE_ENABLED) {
            tv.setText("" + mContext.getString(R.string.ethernet_enabled));
            tv.setTextColor(mContext.getResources().getColor(R.color.network_green));
            mEthernetView.setOnClickListener(this);
        } else if (state == NetworkSettingLogic.ETHERNET_STATE_DISABLED) {
            tv.setText("" + mContext.getString(R.string.ethernet_disabled));
            tv.setTextColor(mContext.getResources().getColor(R.color.network_red));
            mEthernetView.setOnClickListener(null);
        }
    }

    public void updateWifiState(NetworkInfo wifiStateInfo) {

        TextView tv = (TextView) mWifiView.findViewById(R.id.network_item_content);

        Log.d("alisa", "<<<<<<<<<wifiStateInfo<<<<<<<<<<<<<<<<"+wifiStateInfo);
        Log.d("alisa", "<<<<<<<<wifiStateInfo.isAvailable()<<<<<<<<<<<<<<<<"+wifiStateInfo.isAvailable());
        if (wifiStateInfo == null || !wifiStateInfo.isAvailable() || wifiStateInfo.getState() == State.DISCONNECTED) {
            tv.setText("" + mContext.getString(R.string.ethernet_disabled));
            tv.setTextColor(mContext.getResources().getColor(R.color.network_red));
        } else {
//            tv.setText("" + wifiStateInfo.getExtraInfo().replace("\"", ""));
        	WifiManager wifiManager = (WifiManager)mContext.getSystemService(Context.WIFI_SERVICE);
           WifiInfo wifiInfo = wifiManager.getConnectionInfo();
        	String wifiName= wifiInfo.getSSID();
        	if (wifiName.contains("\"")) {
        		wifiName = wifiName.replace("\"", "");
			}
        	tv.setText(wifiName);
            tv.setTextColor(mContext.getResources().getColor(R.color.network_green));
        }

    }

    private void findItems() {
        mEthernetView = findViewById(R.id.network_ethernet_item);
        mWifiView = findViewById(R.id.network_wifi_item);
        mConnectivityView = findViewById(R.id.network_t_connectivity_item);
        mDownloadSpeedView = findViewById(R.id.network_download_speed_item);

        mBluetoothView = findViewById(R.id.network_bluetooth_item);
        if (!mContext.getPackageManager().hasSystemFeature(PackageManager.FEATURE_BLUETOOTH)) {
            mBluetoothView.setVisibility( View.GONE);
        }

        if (!(Build.DEVICE.equals("heran")||Build.DEVICE.equals("scifly_m202_1G"))){
        	mEthernetView.setVisibility(View.VISIBLE);
        }
        
        mWifiView.setOnClickListener(this);
        mConnectivityView.setOnClickListener(this);
        mDownloadSpeedView.setOnClickListener(this);
        mBluetoothView.setOnClickListener(this);

    }

    public void setListeners(OnClickListener l) {
        mEthernetView.setOnClickListener(l);
        mWifiView.setOnClickListener(l);
        mConnectivityView.setOnClickListener(l);
        mDownloadSpeedView.setOnClickListener(l);
        mBluetoothView.setOnClickListener(l);
    }

    private void setTitleWidget() {
        TitleWidget tw = (TitleWidget) findViewById(R.id.title_widget);
        if (tw != null) {
            tw.setMainTitleText(getString(R.string.action_settings));
            tw.setFirstSubTitleText(getString(R.string.network_settings), true);
        }
    }

    public void registerListener() {

    }

    public View findViewById(int id) {
        return mContext.findViewById(id);
    }

    public String getString(int id) {
        return mContext.getString(id);
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.network_ethernet_item:
                Intent intent = new Intent(mContext, EthernetSettingActivity.class);
                mContext.startActivity(intent);
                break;
            case R.id.network_wifi_item:
                intent = new Intent(mContext, WifiSettingsAcitivity.class);
                mContext.startActivity(intent);
                break;
            case R.id.network_t_connectivity_item:
                intent = new Intent(mContext, ConnectionActivity.class);
                mContext.startActivity(intent);
                break;
            case R.id.network_download_speed_item:
                intent = new Intent(mContext, DownloadSpeedActivity.class);
                mContext.startActivity(intent);
                break;
            case R.id.network_bluetooth_item:
                try {
                    Class<?> bluetoothSettingsActivity = Class
                            .forName("com.android.settings.bluetooth.BluetoothSettingsActivity");
                    intent = new Intent(mContext, bluetoothSettingsActivity);
                    mContext.startActivity(intent);
                } catch (IllegalArgumentException iAE) {
                    throw iAE;
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
        }
    }

}
