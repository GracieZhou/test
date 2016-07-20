
package com.eostek.isynergy.setmeup.network;

import java.util.List;

import scifly.middleware.network.AccessPoint;

import android.R.integer;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo;
import android.net.NetworkInfo.DetailedState;
import android.net.wifi.ScanResult;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.SystemProperties;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.eostek.isynergy.setmeup.R;
import com.eostek.isynergy.setmeup.utils.Utils;

public class NetworkFragment extends Fragment {

    private static final String TAG = NetworkFragment.class.getSimpleName();

    private NetworkHolder mNetworkHolder;

    private static final String PLATFORM_638 = "monet";
    
    private static final String PLATFORM_828="muji";

    NetworkLogic mNetworkLogic;

    public List<ScanResult> mWifiResultList;

    // public boolean bConnecting = false;

    public AccessPoint mSelectAccessPoint;

    private int mPosition;

    // 忽略第一次开关关闭的广播
    private boolean bShowResult = false;

    private boolean bRegist = false;

    public static boolean autoScan = true;

    /**
     * get position
     * 
     * @return mPosition
     */
    public int getPosition() {
        return mPosition;
    }

    /**
     * set position
     * 
     * @param mPosition
     */
    public void setPosition(int mPosition) {
        this.mPosition = mPosition;
    }

    private BroadcastReceiver mNetworkReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Utils.print(TAG, "action=" + action);
            if (action.equals(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION)) {
                Log.e("test", "SCAN_RESULTS_remove_CONNECT_FAIL");
                mNetworkHolder.mHandler.removeMessages(mNetworkHolder.SHOW_VIEW_CONNECT_FAIL);
                // showWifiList();
                if (autoScan) {
                    mNetworkHolder.mHandler.sendEmptyMessageDelayed(mNetworkHolder.SHOW_SCAN_RESULT, 3000);
                }
            } else if (action.equals(WifiManager.WIFI_STATE_CHANGED_ACTION)) {
                int state = intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN);
                Utils.print(TAG, "state=" + state);
                if (state == WifiManager.WIFI_STATE_DISABLED) {
                    // if (!bShowResult) {
                    // bShowResult = true;
                    // return;
                    // }
                    // mNetworkHolder.connectFail();
                    Log.e("test", "WIFI_STATE_DISABLED_SHOW_OPEN_WIFI_FAILED_TV");
                    mNetworkHolder.mHandler.sendEmptyMessageDelayed(mNetworkHolder.SHOW_OPEN_WIFI_FAILED_TV, 2000);
                } else if (state == WifiManager.WIFI_STATE_ENABLING) {
                    Log.e("test", "WIFI_STATE_ENABLING_SHOW_ENABLING_TV");
                    mNetworkHolder.mWaitingLl.setVisibility(View.VISIBLE);
                    mNetworkHolder.mConnectingTv.setText(R.string.open_wifi);
                } else if (state == WifiManager.WIFI_STATE_ENABLED) {
                    mNetworkHolder.mWaitingLl.setVisibility(View.VISIBLE);
                    Log.e("test", "WIFI_STATE_ENABLED_SHOW_SCAN_WIFI_TV");
                    mNetworkHolder.mHandler.sendEmptyMessageDelayed(mNetworkHolder.SHOW_SCAN_WIFI_TV, 2000);
                }
            }

            // if (!bConnecting) {
            // return;
            // }

            // if (WifiManager.SUPPLICANT_STATE_CHANGED_ACTION.equals(action)) {
            // SupplicantState state = (SupplicantState)
            // intent.getParcelableExtra(WifiManager.EXTRA_NEW_STATE);
            // Utils.print(TAG, "Wifi DetailedState=" +
            // WifiInfo.getDetailedStateOf(state));
            // if (DetailedState.DISCONNECTED ==
            // WifiInfo.getDetailedStateOf(state)) {
            // List<WifiConfiguration> configs =
            // mNetworkLogic.getConfiguredNetworks();
            // for (WifiConfiguration config : configs) {
            // Utils.print(TAG, " onReceiver.config.SSID =" + config.SSID);
            // Utils.print(TAG, " onReceiver.config.disableReason=" +
            // config.disableReason);
            // if (("\"" + mSelectAccessPoint.ssid + "\"").equals(config.SSID)
            // && config.disableReason ==
            // WifiConfiguration.DISABLED_AUTH_FAILURE) {
            //
            // Utils.print(TAG, " onReceiver.DISABLED_AUTH_FAILURE ");
            // bConnecting = false;
            // mNetworkHolder.wifiConnectFail();
            // mNetworkLogic.forget(config.networkId);
            // }
            // }
            // }
            //
            // }
            else if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(action)) {
                NetworkInfo info = (NetworkInfo) intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                Utils.print(TAG, "isConnected=" + info.isConnected());
                if (info.isConnected()) {
                    // bConnecting = false;
                    mNetworkHolder.wifiConnectSuccess();
                    mNetworkHolder.mHandler.removeMessages(mNetworkHolder.SHOW_VIEW_WIFI_CONNECT_FAIL);
                }
            }
        }
    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.new_fragment_network, container, false);

        this.mNetworkHolder = new NetworkHolder(this, v);
        this.mNetworkLogic = new NetworkLogic(this);
        Log.e("test", "autoScan =" + autoScan);
        v.requestFocus();
        return v;
    }

    protected void showAllNetworks(List<ScanResult> mWifiResultList) {
        Utils.print(TAG, "wifilist.size:" + mWifiResultList.size());
        mNetworkHolder.showAllNetowks(mWifiResultList);
    }

    public NetworkHolder getNetworkHolder() {
        return mNetworkHolder;
    }

    public NetworkLogic getNetworkLogic() {
        return mNetworkLogic;
    }

    @Override
    public void onStart() {
        super.onStart();
        // 启动wifi扫描.
        openWifi();
    }

    private void registerBroadCast() {
        IntentFilter mFilter = new IntentFilter();
        mFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        mFilter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
        mFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        mFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        getActivity().registerReceiver(mNetworkReceiver, mFilter);
    }

    private void openWifi() {
        mNetworkHolder.showWaitingView();
        if (mNetworkLogic.isNetworkConnected()) {
            bRegist = false;
            String pLatform = SystemProperties.get("ro.board.platform", "");// PLATFORM
            Log.d(TAG, "pLatform=" + pLatform);
            if (PLATFORM_638.equals(pLatform)||PLATFORM_828.equals(pLatform)) {
                if ("Ethernet".equals(mNetworkLogic.getNetworkTypeName())) {
                    mNetworkHolder.etheNetConnected();
                }
            } else {
                if ("ETHERNET".equals(mNetworkLogic.getNetworkTypeName())) {
                    mNetworkHolder.etheNetConnected();
                }
            }
            if ("WIFI".equals(mNetworkLogic.getNetworkTypeName())) {
                mNetworkHolder.wifiConnected();
            }
        } else {
            registerBroadCast();
            Log.e("test", "registerBroadCast");
            mNetworkLogic.openWifi();
            mNetworkLogic.mWifiManager.startScan();
            Log.e("test", "openWifi");
            bRegist = true;
            Log.e("test", "SHOW_VIEW_CONNECT_FAIL, 40000");
            mNetworkHolder.mHandler.sendEmptyMessageDelayed(mNetworkHolder.SHOW_VIEW_CONNECT_FAIL, 40000);
            // showWifiList();
            // // 监控wifi变动列表.

        }

    }

    void showWifiList() {
        mWifiResultList = mNetworkLogic.getScanResults();

        for (int i = 0; i < mWifiResultList.size(); i++) {

        }

        if (!mWifiResultList.isEmpty()) {
            showAllNetworks(mWifiResultList);
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mNetworkReceiver != null && bRegist) {
            getActivity().unregisterReceiver(mNetworkReceiver);
        }
    }

    /**
     * wether need to go back
     * 
     * @return true need, false not
     */
    public boolean onBackPressed() {
        return (mNetworkHolder.onBackPressed());
    }

    public AccessPoint getAccessPointBySSID(String ssid) {
        return mNetworkLogic.getAccessPointBySSID(ssid);
    }

    /**
     * connect wifi
     * 
     * @param pwd
     */
    public void connect(String pwd) {
        // bConnecting = true;
        if (mSelectAccessPoint == null) {
            Log.e("test", "mSelectAccessPoint == null");
        } else {
            Log.e("test", "mSelectAccessPoint =" + mSelectAccessPoint);
            Log.e("test", "pwd =" + pwd);
        }
        mNetworkLogic.connectWifi(mSelectAccessPoint, pwd);
    }
}
