
package com.mstar.tv.menu.setting.network;

import android.widget.ProgressBar;
import android.widget.RelativeLayout;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import android.app.Activity;
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
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.Toast;

import com.mstar.android.wifi.MWifiManager;
import com.mstar.tv.menu.R;

public class WifiSettings extends NetworkSettings implements INetworkSettingsListener {

    private static final String TAG = "MSettings.WifiSettings";
    // Combo scans can take 5-6s to complete - set to 10s.
    private static final int WIFI_RESCAN_INTERVAL_MS = 10 * 1000;

    private Activity mContext;

    private WifiSettingsHolder mWifiSettingsHolder;
    private ListView mWifiSignalListView;
    private CheckBox mWifiToggleCheckBox;
    private LinearLayout mFooterView;

    private WifiManager mWifiManager;
    private List<ScanResult> mResults = new ArrayList<ScanResult>();
    private AtomicBoolean mConnected = new AtomicBoolean(false);
    // scan wifiap around
    private Scanner mScanner;
    // add Wi-Fi dialog
    private WiFiSignalListAdapter mAdapter;
    private ProgressBar mWifipb;
    Delay delay;

    // setting item on the right
    private int mSettingItem = Network_Constants.SETTING_ITEM_0;

    public WifiSettings(Activity networkSettingsActivity) {
        super(networkSettingsActivity);

        mWifiManager = getWifiManager();
        mScanner = new Scanner();
        mContext = networkSettingsActivity;
        mWifiSettingsHolder = new WifiSettingsHolder(mContext);

        mContext = networkSettingsActivity;
        mWifiSignalListView = mWifiSettingsHolder.getWifiListView();
        mWifiToggleCheckBox = mWifiSettingsHolder.getWifiToogleCheckBox();
        mFooterView = mWifiSettingsHolder.getFooterView();
        mWifiSignalListView.addFooterView(mFooterView, null, true);
        mWifipb = mWifiSettingsHolder.getProgressBar();
        mWifipb.setVisibility(View.INVISIBLE);
        setListener();
        addNetworkSettingListener(this);
        registerReceiver();
    }

    public void setVisible(boolean visible) {
        mWifiSettingsHolder.setVisible(visible);
        if (visible) {
            showWifiInfo();
        }
    }

    @Override
    public void onExit() {
        mContext.unregisterReceiver(mWifiReceiver);
        mContext.unregisterReceiver(mWifiHWReceiver);
        mScanner.pause();
    }

    @Override
    public boolean onKeyEvent(int keyCode, KeyEvent keyEvent) {
        if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
            if (mSettingItem < Network_Constants.SETTING_ITEM_1) {
                mSettingItem++;
                mWifiSignalListView.setSelection(0);
            }
            mWifiSettingsHolder.requestFocus(mSettingItem);
            return true;
        } else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
            if (mSettingItem > 0) {
                mSettingItem--;
            }
            mWifiSettingsHolder.requestFocus(mSettingItem);
            return true;
        }

        return false;
    }

    @Override
    public void onWifiHWChanged(boolean isOn) {
        Log.d(TAG, "isOn, " + isOn);
        // wifi hw removed
        if (!isOn) {
            // close wifi and open ethernet
            mWifiToggleCheckBox.setChecked(false);
            toggleNetwork(false);
            mWifiSignalListView.setVisibility(View.INVISIBLE);
            mFooterView.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onFocusChange(boolean hasFocus) {
        if (hasFocus) {
            mWifiSettingsHolder.requestFocus(Network_Constants.SETTING_ITEM_0);
        } else {
            mWifiSettingsHolder.clearFocus(mSettingItem);
            mSettingItem = Network_Constants.SETTING_ITEM_0;
        }
    }

    public void setWifiEnabled(boolean enable) {
        if (enable) {
            if (!mWifiManager.isWifiEnabled()) {
                mWifiManager.setWifiEnabled(true);
                mScanner.resume();
            }
        } else {
            int wifiState = mWifiManager.getWifiState();
            if ((wifiState == WifiManager.WIFI_STATE_ENABLING)
                    || (wifiState == WifiManager.WIFI_STATE_ENABLED)) {
                mWifiManager.setWifiEnabled(false);
                mScanner.pause();
            }
        }
    }

    private void showWifiInfo() {
        // init wifiSwitch checkbox:
        int state = mWifiManager.getWifiState();
        if (WifiManager.WIFI_STATE_ENABLING == state || WifiManager.WIFI_STATE_ENABLED == state) {
            mWifiToggleCheckBox.setChecked(true);
            toggleNetwork(true);
        } else {
            mWifiToggleCheckBox.setChecked(false);
            toggleNetwork(false);
        }
        mWifiToggleCheckBox.requestFocus();
    }

    private void refreshWifiSignal() {
        // save current selection
        int selected = mWifiSignalListView.getSelectedItemPosition();

        ArrayList<ScanResult> accessPoints = new ArrayList<ScanResult>();
        // get all scaned wifiap around
        final List<ScanResult> results = mWifiManager.getScanResults();
        if (results != null) {
            for (ScanResult result : results) {
                // Ignore hidden and ad-hoc networks.
                if (TextUtils.isEmpty(result.SSID) || result.capabilities.contains("[IBSS]")) {
                    continue;
                }
                accessPoints.add(result);
            }
        }
        mResults.clear();
        // save access point
        mResults.addAll(accessPoints);

        mAdapter = new WiFiSignalListAdapter(mContext, accessPoints);
        updateConnectionLable();
        mWifiSignalListView.setAdapter(mAdapter);
        mWifiSignalListView.setDividerHeight(0);
        if (selected > (mResults.size() - 1)) {
            mWifiSignalListView.setSelection(mResults.size() - 1);
        } else {
            mWifiSignalListView.setSelection(selected);
        }
    }

    private void registerReceiver() {
        IntentFilter filter = new IntentFilter();
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        filter.addAction(WifiManager.NETWORK_IDS_CHANGED_ACTION);
        filter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        filter.addAction(WifiManager.RSSI_CHANGED_ACTION);
        // filter.addAction(WifiManager.ERROR_ACTION);
        mContext.registerReceiver(mWifiReceiver, filter);

        IntentFilter wifiFilter = new IntentFilter();
        wifiFilter.addAction(MWifiManager.WIFI_DEVICE_ADDED_ACTION);
        wifiFilter.addAction(MWifiManager.WIFI_DEVICE_REMOVED_ACTION);
        mContext.registerReceiver(mWifiHWReceiver, wifiFilter);
    }

    private void updateConnectionState(DetailedState state) {
        WifiConfiguration config = getWifiConfiguredNetwork();
        if (config != null && config.status == WifiConfiguration.Status.DISABLED) {
            Log.d(TAG, "config, " + config.toString());
            switch (config.disableReason) {
                case WifiConfiguration.DISABLED_AUTH_FAILURE:
                    // showt(R.string.wifi_disabled_password_failure));
                    break;
                case WifiConfiguration.DISABLED_DHCP_FAILURE:
                case WifiConfiguration.DISABLED_DNS_FAILURE:
                    // setSummary(context.getString(R.string.wifi_disabled_network_failure));
                    break;
                case WifiConfiguration.DISABLED_UNKNOWN_REASON:
                    // setSummary(context.getString(R.string.wifi_disabled_generic));
            }
        }
    }

    private void updateWifiState(int state) {
        switch (state) {
            case WifiManager.WIFI_STATE_ENABLED:
                mScanner.resume();
                return; // not break, to avoid the call to pause() below
            case WifiManager.WIFI_STATE_ENABLING:
                // addMessagePreference(R.string.wifi_starting);
                break;
            case WifiManager.WIFI_STATE_DISABLING:
            case WifiManager.WIFI_STATE_DISABLED:
                // addMessagePreference(R.string.wifi_empty_list_wifi_off);
                if (mAdapter != null) {
                    mAdapter.updateConnectedSsid("", false);
                }
                break;
        }
        mScanner.pause();
    }

    private void updateConnectionLable() {
        // wifi is connected
        WifiInfo info = mWifiManager.getConnectionInfo();
        if (info != null && info.getNetworkId() != WifiConfiguration.INVALID_NETWORK_ID) {
            Log.d(TAG, "info.getSSID, " + info.getSSID());
            if (mAdapter != null) {
                mAdapter.updateConnectedSsid(info.getSSID(), true);
            }
        }
    }

    /**
     * Description: toggle network.
     * 
     * @param enable true for wifi, false for ethernet.
     */
    private void toggleNetwork(boolean enable) {
        if (enable) {
            // open wifi and close ethernet
            setWifiEnabled(true);
            // close ethernet
//            getEthernetManager(mContext).setEnabled(false);
            mWifiSignalListView.setVisibility(View.VISIBLE);
            mFooterView.setVisibility(View.VISIBLE);
            refreshWifiSignal();
        } else {
            // close wifi and open ethernet
            setWifiEnabled(false);
            //modify by ken.bi [2013-4-12]
//            getEthernetManager(mContext).setEnabled(true);
            mWifiSignalListView.setVisibility(View.INVISIBLE);
            mFooterView.setVisibility(View.INVISIBLE);
        }
    }

    private void showToast(int id) {
        if (id <= 0) {
            Log.d(TAG, "id < 0");
            return;
        }

        Toast.makeText(mContext, id, Toast.LENGTH_SHORT).show();
    }

    private void setListener() {
        mWifiSignalListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == (mResults.size())) {
                    // show dialog to add hidden SSID.
                    Log.d(TAG, "clicked position:" + position);
                    WiFiConnectDialog connectDialog = new WiFiConnectDialog(
                            mContext, WifiSettings.this, null);
                    connectDialog.show();

                } else {
                    ScanResult scanResult = mResults.get(position);
                    WiFiConnectDialog connectDialog = new WiFiConnectDialog(
                            mContext, WifiSettings.this, scanResult);
                    connectDialog.show();
                }
            }
        });

        mWifiToggleCheckBox.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {
                Log.d(TAG, "wifi toggle check box onClick");
                if (view instanceof CheckBox) {
                    CheckBox checkBox = (CheckBox) view;

                    // wifi dongle is ready
                    if (!MWifiManager.getInstance().isWifiDeviceExist()) {
                        showToast(R.string.please_insert_dongle);
                        checkBox.setChecked(false);
                        return;
                    }

                    // fix mantis bug 0328611
                    // wifi ap is active
                    if (mWifiManager.isWifiApEnabled()) {
                        showToast(R.string.close_wifiap_txt);
                        checkBox.setChecked(false);
                        return;
                    }

                    // open wifi
                    toggleNetwork(checkBox.isChecked());
                    mWifiToggleCheckBox.setEnabled(false);
                    mWifiToggleCheckBox.setVisibility(View.INVISIBLE);
                    mWifipb.setVisibility(View.VISIBLE);
                    Log.d(TAG, "disable!");
                    delay = new Delay();
                    Message msg = new Message();
                    delay.sendMessageDelayed(msg, 5000);
                }
            }
        });
    }

    private class Delay extends Handler {

        @Override
        public void handleMessage(Message msg) {
            // TODO Auto-generated method stub
            super.handleMessage(msg);
            mWifipb.setVisibility(View.INVISIBLE);
            mWifiToggleCheckBox.setEnabled(true);
            mWifiToggleCheckBox.setVisibility(View.VISIBLE);
            Log.d(TAG, "enable!");
        }

    }
    
    private class Scanner extends Handler {
        private int mRetry = 0;

        void resume() {
            if (!hasMessages(0)) {
                sendEmptyMessage(0);
            }
        }

        void pause() {
            mRetry = 0;
            removeMessages(0);
        }

        @Override
        public void handleMessage(Message message) {
            if (mWifiManager.isWifiEnabled()) {
                Log.d(TAG, "startScanActive");
                if (mWifiManager.startScan()) {
                    mRetry = 0;
                } else if (++mRetry >= 3) {
                    mRetry = 0;
                    return;
                }
                sendEmptyMessageDelayed(0, WIFI_RESCAN_INTERVAL_MS);
            }
        }
    }

    private BroadcastReceiver mWifiReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.d(TAG, "action, " + action);
            if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(action)) {
                updateWifiState(intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE,
                        WifiManager.WIFI_STATE_UNKNOWN));
            } else if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(action)) {
                NetworkInfo networkInfo = (NetworkInfo) intent
                        .getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
                mConnected.set(networkInfo.isConnected());
                updateConnectionState(networkInfo.getDetailedState());
                refreshWifiSignal();

            } else if (WifiManager.SUPPLICANT_STATE_CHANGED_ACTION.equals(action)) {
                SupplicantState state = (SupplicantState) intent
                        .getParcelableExtra(WifiManager.EXTRA_NEW_STATE);
                if (!mConnected.get() && SupplicantState.isHandshakeState(state)) {
                    updateConnectionState(WifiInfo.getDetailedStateOf(state));
                }

            } else if (WifiManager.RSSI_CHANGED_ACTION.equals(action)) {
                updateConnectionState(null);

                // scan available wifiap
            } else if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(action)
                    || WifiManager.CONFIGURED_NETWORKS_CHANGED_ACTION.equals(action)
                    || WifiManager.LINK_CONFIGURATION_CHANGED_ACTION.equals(action)) {
                refreshWifiSignal();
            }
        }
    };

    private BroadcastReceiver mWifiHWReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            if (MWifiManager.WIFI_DEVICE_REMOVED_ACTION.equals(action)) {
                onWifiHWChanged(false);
            } else if (MWifiManager.WIFI_DEVICE_ADDED_ACTION.equals(action)) {
                onWifiHWChanged(true);
            }
        }
    };

}
