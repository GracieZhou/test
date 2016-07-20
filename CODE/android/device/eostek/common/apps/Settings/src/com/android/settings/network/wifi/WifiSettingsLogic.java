
package com.android.settings.network.wifi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import android.app.ActionBar;
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
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Switch;
import android.widget.Toast;

import com.android.settings.R;
import com.android.settings.network.wifi.LinkPropertyConfigDialog.OnSubmitClickListener;

public class WifiSettingsLogic implements AccessPoint.OnKeyDownListener {

    private static final String TAG = "WifiSettingsLogic";

    private static final int WIFI_RESCAN_INTERVAL_MS = 10 * 1000;

    public static final int START_ACTIVITY_LINKPROPERTY_CONFIG = 0x01;

    private final AtomicBoolean mConnected = new AtomicBoolean(false);

    private Handler mHander;

    private AccessPoint mSelectedAccessPoint;

    private AccessPoint accessPoint;

    private Preference mEmptyView;

    private CheckBoxPreference mWifiCheckBox;

    private Context mContext;

    WifiSettingsFragment frag;

    private WifiManager mWifiManager;

    private final IntentFilter mFilter;

    private final BroadcastReceiver mReceiver;

    private final Scanner mScanner;

    private WifiEnabler mWifiEnabler;

    private DetailedState mLastState;

    private WifiInfo mLastInfo;

    private WifiManager.ActionListener mConnectListener;

    private WifiManager.ActionListener mSaveListener;

    private WifiManager.ActionListener mForgetListener;

    private PasswordInputDialog mPasswordDialog;

    // the action bar uses a different set of controls for Setup Wizard
    private boolean mSetupWizardMode;

    public WifiSettingsLogic(Context context, WifiSettingsFragment wifiSettingsFragment, Handler mHander,
            boolean setupWizardMode) {
        this.mHander = mHander;
        this.mContext = context;
        frag = wifiSettingsFragment;
        mSetupWizardMode = setupWizardMode;
        final Activity activity = frag.getActivity();
        mWifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);

        mConnectListener = new WifiManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.i(TAG, "mConnectListener:::onSuccess()");
            }

            @Override
            public void onFailure(int reason) {
                Log.i(TAG, "mConnectListener:::onFailure()::" + reason);
                if (activity != null) {
                    Toast.makeText(activity, R.string.wifi_failed_connect_message, Toast.LENGTH_SHORT).show();
                }
            }
        };

        mSaveListener = new WifiManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.i(TAG, "mSaveListener:::onSuccess()");
            }

            @Override
            public void onFailure(int reason) {
                Log.i(TAG, "mSaveListener:::onFailure()::" + reason);
                if (activity != null) {
                    Toast.makeText(activity, R.string.wifi_failed_save_message, Toast.LENGTH_SHORT).show();
                }
            }
        };

        mForgetListener = new WifiManager.ActionListener() {
            @Override
            public void onSuccess() {
                Log.i(TAG, "mForgetListener:::onSuccess()");
            }

            @Override
            public void onFailure(int reason) {
                if (activity != null) {
                    Toast.makeText(activity, R.string.wifi_failed_forget_message, Toast.LENGTH_SHORT).show();
                }
            }
        };

        mFilter = new IntentFilter();
        mFilter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        mFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
        mFilter.addAction(WifiManager.NETWORK_IDS_CHANGED_ACTION);
        mFilter.addAction(WifiManager.SUPPLICANT_STATE_CHANGED_ACTION);
        mFilter.addAction(WifiManager.CONFIGURED_NETWORKS_CHANGED_ACTION);
        mFilter.addAction(WifiManager.LINK_CONFIGURATION_CHANGED_ACTION);
        mFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
        mFilter.addAction(WifiManager.RSSI_CHANGED_ACTION);

        mReceiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                handleEvent(context, intent);
            }
        };

        mScanner = new Scanner();

        if (!mSetupWizardMode) {
            Switch actionBarSwitch = new Switch(activity);

            if (activity instanceof PreferenceActivity) {
                PreferenceActivity preferenceActivity = (PreferenceActivity) activity;
                if (preferenceActivity.onIsHidingHeaders() || !preferenceActivity.onIsMultiPane()) {
                    final int padding = activity.getResources()
                            .getDimensionPixelSize(R.dimen.action_bar_switch_padding);
                    actionBarSwitch.setPaddingRelative(0, 0, padding, 0);
                    activity.getActionBar().setDisplayOptions(ActionBar.DISPLAY_SHOW_CUSTOM,
                            ActionBar.DISPLAY_SHOW_CUSTOM);
                    activity.getActionBar().setCustomView(
                            actionBarSwitch,
                            new ActionBar.LayoutParams(ActionBar.LayoutParams.WRAP_CONTENT,
                                    ActionBar.LayoutParams.WRAP_CONTENT, Gravity.CENTER_VERTICAL | Gravity.END));
                }
            }
            mWifiEnabler = new WifiEnabler(activity, actionBarSwitch);
        }

        mEmptyView = new Preference(frag.getActivity());
        mEmptyView.setKey("empty_view");
        mEmptyView.setTitle(R.string.wifi_empty_list_wifi_off);
        mEmptyView.setEnabled(false);

    }

    public void updateAccessPoints() {

        if (mContext == null)
            return;

        final int wifiState = mWifiManager.getWifiState();

        switch (wifiState) {
            case WifiManager.WIFI_STATE_ENABLED:
                final List<AccessPoint> accessPoints = constructAccessPoints();
                frag.getPreferenceScreen().removeAll();
                if (accessPoints.size() == 0) {
                    addMessagePreference(R.string.wifi_empty_list_wifi_on);
                } else {
                    if (frag.getPreferenceScreen().findPreference("empty_view") != null)
                        frag.getPreferenceScreen().removePreference(mEmptyView);
                }
                for (final AccessPoint accessPoint : accessPoints) {
                    frag.getPreferenceScreen().addPreference(accessPoint);
                    accessPoint.setOnKeyDownListener(this);
                    accessPoint.setRightArrowClickListener(new OnClickListener() {
                        public void onClick(View arg0) {
                            mSelectedAccessPoint = accessPoint;
                            showConfigDialog(accessPoint);
                        }
                    });
                }
                break;

            case WifiManager.WIFI_STATE_ENABLING:
                removeAllPreferences();
                break;

            case WifiManager.WIFI_STATE_DISABLING:
                addMessagePreference(R.string.wifi_stopping);
                break;

            case WifiManager.WIFI_STATE_DISABLED:
                addMessagePreference(R.string.wifi_empty_list_wifi_off);
                break;
        }
        insertCheckbox();
    }

    private void addMessagePreference(int messageId) {
        removeAllPreferences();
        if (frag.getPreferenceScreen().findPreference("empty_view") == null)
            frag.getPreferenceScreen().addPreference(mEmptyView);
        mEmptyView.setTitle(messageId);
    }

    private void removeAllPreferences() {
        frag.getPreferenceScreen().removeAll();
        insertCheckbox();
    }

    private void insertCheckbox() {
        if (mWifiCheckBox == null) {
            mWifiCheckBox = new CheckBoxPreference(mContext);
            mWifiCheckBox.setLayoutResource(R.layout.wifi_checkbox_preference_layout);
            mWifiCheckBox.setWidgetLayoutResource(R.layout.wifi_enable_checkbox);
            mWifiCheckBox.setKey("switch");
            mWifiCheckBox.setTitle(R.string.wifi_settings);
            // mWifiCheckBox.setSummaryOn(R.string.wifi_enabled);
            // mWifiCheckBox.setSummaryOff(R.string.wifi_disabled);
            mWifiCheckBox.setPersistent(false);
            mWifiCheckBox.setEnabled(true);
        }
        if (frag.getPreferenceScreen().findPreference("switch") == null) {
            frag.getPreferenceScreen().addPreference(mWifiCheckBox);
        }
        if (mWifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLING) {
            mWifiCheckBox.setChecked(true);
        } else {
            mWifiCheckBox.setChecked(mWifiManager.isWifiEnabled());
        }
    }

    public List<AccessPoint> constructAccessPoints() {
        ArrayList<AccessPoint> accessPoints = new ArrayList<AccessPoint>();

        /**
         * Lookup table to more quickly update AccessPoints by only considering
         * objects with the correct SSID. Maps SSID -> List of AccessPoints with
         * the given SSID.
         */
        Multimap<String, AccessPoint> apMap = new Multimap<String, AccessPoint>();

        final List<WifiConfiguration> configs = mWifiManager.getConfiguredNetworks();
        if (configs != null) {
            for (WifiConfiguration config : configs) {
                AccessPoint accessPoint = new AccessPoint(mContext, config);
                accessPoint.update(mLastInfo, mLastState);
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
                    if (accessPoint.update(result))
                        found = true;
                }
                if (!found) {
                    AccessPoint accessPoint = new AccessPoint(mContext, result);
                    accessPoints.add(accessPoint);
                    apMap.put(accessPoint.ssid, accessPoint);
                }
            }
        }

        // Pre-sort accessPoints to speed preference insertion
        Collections.sort(accessPoints);
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

    private void updateWifiState(int state) {
        Activity activity = frag.getActivity();

        switch (state) {
            case WifiManager.WIFI_STATE_ENABLED:
                mScanner.resume();
                return; // not break, to avoid the call to pause() below

            case WifiManager.WIFI_STATE_ENABLING:
                addMessagePreference(R.string.wifi_starting);
                break;

            case WifiManager.WIFI_STATE_DISABLED:
                addMessagePreference(R.string.wifi_empty_list_wifi_off);
                break;
        }

        mLastInfo = null;
        mLastState = null;
        mScanner.pause();
    }

    private void handleEvent(Context context, Intent intent) {

        String action = intent.getAction();
        if (WifiManager.WIFI_STATE_CHANGED_ACTION.equals(action)) {
            updateWifiState(intent.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN));
        } else if (WifiManager.SCAN_RESULTS_AVAILABLE_ACTION.equals(action)
                || WifiManager.CONFIGURED_NETWORKS_CHANGED_ACTION.equals(action)
                || WifiManager.LINK_CONFIGURATION_CHANGED_ACTION.equals(action)) {
            updateAccessPoints();
        } else if (WifiManager.SUPPLICANT_STATE_CHANGED_ACTION.equals(action)) {
            SupplicantState state = (SupplicantState) intent.getParcelableExtra(WifiManager.EXTRA_NEW_STATE);
            if (!mConnected.get() && SupplicantState.isHandshakeState(state)) {
                updateConnectionState(WifiInfo.getDetailedStateOf(state));
            } else {
                updateConnectionState(null);
            }
        } else if (WifiManager.NETWORK_STATE_CHANGED_ACTION.equals(action)) {
            NetworkInfo info = (NetworkInfo) intent.getParcelableExtra(WifiManager.EXTRA_NETWORK_INFO);
            mConnected.set(info.isConnected());
            // changeNextButtonState(info.isConnected());
            updateAccessPoints();
            updateConnectionState(info.getDetailedState());
            // if (mAutoFinishOnConnection && info.isConnected()) {
            // Activity activity = getActivity();
            // if (activity != null) {
            // activity.setResult(Activity.RESULT_OK);
            // activity.finish();
            // }
            // return;
            // }
        } else if (WifiManager.RSSI_CHANGED_ACTION.equals(action)) {
            updateConnectionState(null);
        }
    }

    private void updateConnectionState(DetailedState state) {
        /* sticky broadcasts can call this when wifi is disabled */
        if (!mWifiManager.isWifiEnabled()) {
            mScanner.pause();
            return;
        }

        if (state == DetailedState.OBTAINING_IPADDR) {
            mScanner.pause();
        } else {
            mScanner.resume();
        }

        mLastInfo = mWifiManager.getConnectionInfo();
        if (state != null) {
            mLastState = state;
        }

        for (int i = frag.getPreferenceScreen().getPreferenceCount() - 1; i >= 0; --i) {
            // Maybe there's a WifiConfigPreference
            Preference preference = frag.getPreferenceScreen().getPreference(i);
            if (preference instanceof AccessPoint) {
                final AccessPoint accessPoint = (AccessPoint) preference;
                accessPoint.update(mLastInfo, mLastState);
            }
        }
    }

    private class Scanner extends Handler {
        private int mRetry = 0;

        void resume() {
            if (!hasMessages(0)) {
                sendEmptyMessage(0);
            }
        }

        void forceScan() {
            removeMessages(0);
            sendEmptyMessage(0);
        }

        void pause() {
            mRetry = 0;
            removeMessages(0);
        }

        @Override
        public void handleMessage(Message message) {
            if (mWifiManager.startScan()) {
                mRetry = 0;
            } else if (++mRetry >= 3) {
                mRetry = 0;
                Activity activity = (Activity) mContext;
                if (activity != null) {
                    Toast.makeText(activity, R.string.wifi_fail_to_scan, Toast.LENGTH_LONG).show();
                }
                return;
            }
            sendEmptyMessageDelayed(0, WIFI_RESCAN_INTERVAL_MS);
        }
    }

    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (preference instanceof AccessPoint) {
            mSelectedAccessPoint = (AccessPoint) preference;
            /** Bypass dialog for unsecured, unsaved networks */
            if (mSelectedAccessPoint.security == AccessPoint.SECURITY_NONE
                    && mSelectedAccessPoint.networkId == WifiConfiguration.INVALID_NETWORK_ID) {
                mSelectedAccessPoint.generateOpenNetworkConfig();
                mWifiManager.connect(mSelectedAccessPoint.getConfig(), mConnectListener);
            } else {
                WifiConfiguration config = mSelectedAccessPoint.getConfig();
                DetailedState state = mSelectedAccessPoint.getState();
                WifiInfo info = mSelectedAccessPoint.getInfo();
                int level = mSelectedAccessPoint.getLevel();
                int netId = mSelectedAccessPoint.networkId;
                int rssi = mSelectedAccessPoint.getRssi();
                Log.i(TAG, "state::" + state + " level::" + level + " netId::" + netId + " rssi::" + rssi);

                if (rssi == Integer.MAX_VALUE) {
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.wifi_failue_out_of_range),
                            Toast.LENGTH_SHORT).show();
                    Log.i(TAG, "" + mContext.getResources().getString(R.string.wifi_failue_out_of_range));
                    return true;
                }

                if (config != null && config.status == WifiConfiguration.Status.DISABLED) {
                        if (config.disableReason == WifiConfiguration.DISABLED_AUTH_FAILURE) {
                            showPasswordDialog(mSelectedAccessPoint);
                            return true;
                        }
//                    String tip = "";
//                    switch (config.disableReason) {
//                        case WifiConfiguration.DISABLED_AUTH_FAILURE:
//                            tip = mContext.getResources().getString(R.string.wifi_failue_password_check_again);
//                            showPasswordDialog(mSelectedAccessPoint);
//                            break;
//                        case WifiConfiguration.DISABLED_DHCP_FAILURE:
//                        case WifiConfiguration.DISABLED_DNS_FAILURE:
//                            tip = mContext.getResources().getString(R.string.wifi_failue_dhcp_check_again);
//                            break;
//                        case WifiConfiguration.DISABLED_UNKNOWN_REASON:
//                            tip = mContext.getResources().getString(R.string.wifi_failue_unkonwn_reason);
//                            break;
//                    }
//                    Toast.makeText(mContext, tip, Toast.LENGTH_SHORT).show();
//                    Log.i(TAG, "" + tip);
//                    return true;
                }

                // Connected ap clicked.
                if (state != null) {
                    Log.i(TAG, "" + mContext.getResources().getString(R.string.wifi_already_connect_tip));
                    Toast.makeText(mContext, mContext.getResources().getString(R.string.wifi_already_connect_tip),
                            Toast.LENGTH_SHORT).show();
                    return true;
                }

                // Saved ap clicked.
                if (netId != WifiConfiguration.INVALID_NETWORK_ID) {
                    if (mSelectedAccessPoint.getConfig() != null && mSelectedAccessPoint.isPasswordSaved()) {
                        mWifiManager.connect(mSelectedAccessPoint.getConfig(), mConnectListener);
                        Log.i(TAG, "Try to connect the access point directly!");
                        Toast.makeText(mContext, mContext.getResources().getString(R.string.wifi_already_saved_tip),
                                Toast.LENGTH_SHORT).show();
                        return true;
                    }
                }

                showPasswordDialog(mSelectedAccessPoint);
                Log.i(TAG, "show dialog");
            }
        } else if (preference == mWifiCheckBox) {
            boolean enable = mWifiCheckBox.isChecked();
            if (enable && mWifiManager.isWifiApEnabled()) {
                mWifiManager.setWifiApEnabled(null, false);
            }
            mWifiManager.setWifiEnabled(enable);
        } else {
            return false;
        }
        return true;
    }

    // When not saved ap clicked or authentication problem ap clicked,password
    // input dialog will be shown.
    private void showPasswordDialog(AccessPoint accessPoint) {
        mPasswordDialog = new PasswordInputDialog(frag.getActivity(), mSelectedAccessPoint);
        mPasswordDialog.show();
        mPasswordDialog.setOnConfirmClickListener(new ConfirmClickListener());
    }

    private class ConfirmClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            if (mPasswordDialog != null && mPasswordDialog.isShowing()) {
                mWifiManager.connect(mPasswordDialog.getConfig(), mConnectListener);
                mPasswordDialog.dismiss();
            }
        }
    }

    public WifiEnabler getWifiEnabler() {
        return mWifiEnabler;
    }

    public void scannerPause() {
        mScanner.pause();
    }

    public void registerReceiver() {
        frag.getActivity().registerReceiver(mReceiver, mFilter);
    }

    public void unregisterReceiver() {
        Log.i(TAG, "unregisterReceiver");
        frag.getActivity().unregisterReceiver(mReceiver);
    }

    @Override
    public boolean onKeyDown(Preference pre, View v, int keyCode, KeyEvent event) {

        // Only handle right key event now.
        if (keyCode != KeyEvent.KEYCODE_DPAD_RIGHT) {
            return false;
        }

        if (pre instanceof AccessPoint) {
            mSelectedAccessPoint = (AccessPoint) pre;
            showConfigDialog(mSelectedAccessPoint);

        }
        return false;
    }

    private void showConfigDialog(AccessPoint ap) {
        final LinkPropertyConfigDialog configDialog = new LinkPropertyConfigDialog(mContext, ap);
        configDialog.setFromWhere(LinkPropertyConfigDialog.FROM_WIFI);
        configDialog.show();

        configDialog.setOnSubmitClickListener(new OnSubmitClickListener() {
            @Override
            public void onSubmit(View v, int oprerationCode, WifiConfiguration config) {
                switch (oprerationCode) {
                    case LinkPropertyConfigDialog.OPERATION_CODE_CONNECT:
                        showPasswordDialog(mSelectedAccessPoint);
                        break;
                    case LinkPropertyConfigDialog.OPERATION_CODE_FORGET:
                        forget(config);
                        break;
                }
            }
        });

        configDialog.setOnConfigListener(new LinkPropertyConfigDialog.OnConfigListener() {

            @Override
            public void onConfigFinish(WifiConfiguration config) {
//                if (config == null || config.linkProperties == null)
//                    return;

                mWifiManager.save(config, mSaveListener);
            }
        });
    }

    void forget(WifiConfiguration config) {
        if (config == null || config.networkId == WifiConfiguration.INVALID_NETWORK_ID) {
            // Should not happen, but a monkey seems to triger it
            Log.e(TAG, "Failed to forget invalid network " + mSelectedAccessPoint.getConfig());
            return;
        }

        mWifiManager.forget(config.networkId, mForgetListener);

        if (mWifiManager.isWifiEnabled()) {
            mScanner.resume();
        }

        updateAccessPoints();

    }

    public AccessPoint getSelectedAccessPoint() {
        return mSelectedAccessPoint;
    }
}
