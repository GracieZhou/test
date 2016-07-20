
package com.android.settings.network.wifi;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;

import scifly.middleware.network.WifiConfig;
import scifly.middleware.network.WifiManagerGlobal;
import android.annotation.SuppressLint;
import android.app.ActionBar;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.NetworkInfo.DetailedState;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Handler;
import android.os.Message;
import android.preference.Preference;
import android.preference.PreferenceActivity;
import android.util.Log;
import android.view.Gravity;
import android.widget.Switch;
import android.widget.Toast;

import com.android.settings.R;
import com.android.settings.util.Utils;

/**
 * WifiSettingsLogic
 */
public class SimpleWifiSettingsLogic {

    private static final String TAG = "WifiSettingsLogic";

    public static final int START_ACTIVITY_LINKPROPERTY_CONFIG = 0x01;

    private Context mContext;

    SimpleWifiSettingsFragment frag;

    public WifiManager mWifiManager;

    public final IntentFilter mFilter;

    public final BroadcastReceiver mReceiver;

    public final Scanner mScanner;

    private DetailedState mLastState;

    private WifiInfo mLastInfo;
    
    public Preference mEmptyView;

    public WifiManager.ActionListener mConnectListener;

    public WifiManager.ActionListener mSaveListener;

    public WifiManager.ActionListener mForgetListener;

    private static final int WIFI_RESCAN_INTERVAL_MS = 10 * 1000;

    // the action bar uses a different set of controls for Setup Wizard
    private boolean mSetupWizardMode;

    public WifiManagerGlobal mWifiManagerGlobal;

    public SimpleWifiSettingsLogic(Context context, SimpleWifiSettingsFragment simplewifiSettingsFragment,
            boolean setupWizardMode) {
        this.mContext = context;
        frag = simplewifiSettingsFragment;
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
                Log.d(TAG, "<<<<<onReceive<<<<<<<<<<<");
                frag.handleEvent(context, intent);
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
        }

        mEmptyView = new Preference(frag.getActivity());
        mEmptyView.setKey("empty_view");
        mEmptyView.setTitle(R.string.wifi_empty_list_wifi_off);
        mEmptyView.setEnabled(false);

        mWifiManagerGlobal = new WifiManagerGlobal(context);
    }

    /**
     * get the list of current wifi.
     * 
     * @return
     */
    public List<AccessPoint> constructAccessPoints() {
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

    @SuppressLint("HandlerLeak") class Scanner extends Handler {
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
                    Utils.showToast(activity, R.string.wifi_fail_to_scan);
                }
                return;
            }
            sendEmptyMessageDelayed(0, WIFI_RESCAN_INTERVAL_MS);
        }
    }
}
