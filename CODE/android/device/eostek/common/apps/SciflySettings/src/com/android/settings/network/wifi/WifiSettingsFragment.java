
package com.android.settings.network.wifi;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import scifly.middleware.network.WifiConfig;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.DetailedState;
import android.net.wifi.SupplicantState;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.preference.CheckBoxPreference;
import android.preference.Preference;
import android.preference.Preference.OnPreferenceClickListener;
import android.preference.PreferenceFragment;
import android.preference.PreferenceScreen;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.android.settings.R;
import com.android.settings.network.NetworkSettingActivity;
import com.android.settings.network.wifi.LinkPropertyConfigDialog.OnSubmitClickListener;
import com.android.settings.util.Utils;

/**
 * wififragment
 */
public class WifiSettingsFragment extends PreferenceFragment implements AccessPoint.OnKeyDownListener {

    protected static final String TAG = "WifiSettingsfragment";

    private WifiSettingsLogic mLogic;

    private AccessPoint mSelectedAccessPoint;

    private Preference mAddWifiView;

    private CheckBoxPreference mWifiCheckBox;

    private WifiEnabler mWifiEnabler;

    private DetailedState mLastState;

    private WifiInfo mLastInfo;

    private static final String EXTRA_IS_FIRST_RUN = "firstRun";

    private boolean mSetupWizardMode;

    private NetworkSettingActivity mActivity;

    private PasswordInputDialog mPasswordDialog;

    private final AtomicBoolean mConnected = new AtomicBoolean(false);

    public void onCreate(Bundle bundle) {
        mSetupWizardMode = getActivity().getIntent().getBooleanExtra(EXTRA_IS_FIRST_RUN, false);
        super.onCreate(bundle);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        return inflater.inflate(R.layout.setting_preference_fragment, container, false);
    }

    @Override
    public void onResume() {
        super.onResume();
        if (getWifiEnabler() != null) {
            getWifiEnabler().resume();
        }
        registerReceiver();
        updateAccessPoints();
    }

    @Override
    public void onPause() {
        super.onPause();
        if (getWifiEnabler() != null) {
            getWifiEnabler().pause();
        }
        unregisterReceiver();
        scannerPause();
    }

    @Override
    public void onActivityCreated(Bundle arg0) {
        super.onActivityCreated(arg0);
        mActivity = (NetworkSettingActivity) this.getActivity();
        mActivity.setSubTitle(R.string.wifi_setting);
        mLogic = new WifiSettingsLogic(getActivity(), this, mSetupWizardMode);
        addPreferencesFromResource(R.xml.preference_wifi_settings);
    }

    public void updateAccessPoints() {
        if (getActivity() == null)
            return;

        final int wifiState = mLogic.mWifiManager.getWifiState();
        Log.v(TAG, "wifiState:" + wifiState);

        switch (wifiState) {
            case WifiManager.WIFI_STATE_ENABLED:
                final List<AccessPoint> accessPoints = mLogic.constructAccessPoints();
                this.getPreferenceScreen().removeAll();
                if (accessPoints.size() == 0) {
                    addMessagePreference(R.string.wifi_empty_list_wifi_on);
                } else {
                    if (this.getPreferenceScreen().findPreference("empty_view") != null)
                        this.getPreferenceScreen().removePreference(mLogic.mEmptyView);
                }
                for (final AccessPoint accessPoint : accessPoints) {
                    this.getPreferenceScreen().addPreference(accessPoint);
                    accessPoint.setOnKeyDownListener(this);
                    accessPoint.setRightArrowClickListener(new OnClickListener() {
                        public void onClick(View arg0) {
                            mSelectedAccessPoint = accessPoint;
                            showConfigDialog(accessPoint);
                        }
                    });
                }
                insertCheckbox();
                insertAddWifi();
                break;

            case WifiManager.WIFI_STATE_ENABLING:
                removeAllPreferences();
                insertCheckbox();
                insertAddWifi();
                break;

            case WifiManager.WIFI_STATE_DISABLING:
                addMessagePreference(R.string.wifi_stopping);
                insertCheckbox();
                break;

            case WifiManager.WIFI_STATE_DISABLED:
                addMessagePreference(R.string.wifi_empty_list_wifi_off);
                insertCheckbox();
                break;
            default:
                insertCheckbox();
                break;
        }
    }

    /**
     * add messagepreference about the information of the wifi state.
     * 
     * @param messageId
     */
    private void addMessagePreference(int messageId) {
        removeAllPreferences();
        if (this.getPreferenceScreen().findPreference("empty_view") == null)
            this.getPreferenceScreen().addPreference(mLogic.mEmptyView);
        mLogic.mEmptyView.setTitle(messageId);
    }

    /**
     * remove all preference,include wifi check box.
     */
    private void removeAllPreferences() {
        this.getPreferenceScreen().removeAll();
        insertCheckbox();
    }

    /**
     * init add wifi preference.
     */
    private void insertAddWifi() {
        if (mAddWifiView == null) {
            mAddWifiView = new Preference(getActivity());
            mAddWifiView.setTitle(R.string.add_wifi_manually);
            mAddWifiView.setKey("add_wifi");
            mAddWifiView.setLayoutResource(R.layout.preference_wifi_checkbox);
            mAddWifiView.setPersistent(false);
            mAddWifiView.setEnabled(true);
            mAddWifiView.setOnPreferenceClickListener(new OnPreferenceClickListener() {

                @Override
                public boolean onPreferenceClick(Preference arg0) {
                    showPasswordDialog(null);
                    mPasswordDialog.changeUItoAddWifi();
                    return true;
                }
            });
        }
        if (this.getPreferenceScreen().findPreference("add_wifi") == null) {
            this.getPreferenceScreen().addPreference(mAddWifiView);
        }
    }

    /**
     * init wifi checkbox.
     */
    private void insertCheckbox() {
        if (mWifiCheckBox == null) {
            mWifiCheckBox = new CheckBoxPreference(getActivity());
            mWifiCheckBox.setLayoutResource(R.layout.preference_wifi_checkbox);
            mWifiCheckBox.setWidgetLayoutResource(R.layout.checkbox_wifi_enable);
            mWifiCheckBox.setKey("switch");
            mWifiCheckBox.setTitle(R.string.wifi_settings);
            mWifiCheckBox.setPersistent(false);
            mWifiCheckBox.setEnabled(true);
        }
        if (this.getPreferenceScreen().findPreference("switch") == null) {
            this.getPreferenceScreen().addPreference(mWifiCheckBox);
        }
        if (mLogic.mWifiManager.getWifiState() == WifiManager.WIFI_STATE_ENABLING) {
            mWifiCheckBox.setChecked(true);
        } else {
            mWifiCheckBox.setChecked(mLogic.mWifiManager.isWifiEnabled());
        }
    }

    /** A restricted multimap for use in constructAccessPoints */

    private void updateWifiState(int state) {
        switch (state) {
            case WifiManager.WIFI_STATE_ENABLED:
                mLogic.mScanner.resume();
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
        mLogic.mScanner.pause();
    }

    /**
     * According to the state of the network, accept different broadcast,
     * refresh the status of wifi.
     * 
     * @param context
     * @param intent
     */
    void handleEvent(Context context, Intent intent) {

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
            updateAccessPoints();
            updateConnectionState(info.getDetailedState());
        } else if (WifiManager.RSSI_CHANGED_ACTION.equals(action)) {
            updateConnectionState(null);
        }
    }

    private void updateConnectionState(DetailedState state) {
        /* sticky broadcasts can call this when wifi is disabled */
        if (!mLogic.mWifiManager.isWifiEnabled()) {
            mLogic.mScanner.pause();
            return;
        }

        if (state == DetailedState.OBTAINING_IPADDR) {
            mLogic.mScanner.pause();
        } else {
            mLogic.mScanner.resume();
        }

        mLastInfo = mLogic.mWifiManager.getConnectionInfo();
        if (state != null) {
            mLastState = state;
        }

        for (int i = this.getPreferenceScreen().getPreferenceCount() - 1; i >= 0; --i) {
            // Maybe there's a WifiConfigPreference
            Preference preference = this.getPreferenceScreen().getPreference(i);
            if (preference instanceof AccessPoint) {
                final AccessPoint accessPoint = (AccessPoint) preference;
                accessPoint.update(mLastInfo, mLastState);
            }
        }
    }

    /**
     * show password dialog when click the preference of wifi.
     * 
     * @param preferenceScreen
     * @param preference
     * @return
     */
    public boolean onPreferenceTreeClick(PreferenceScreen preferenceScreen, Preference preference) {
        if (preference instanceof AccessPoint) {
            mSelectedAccessPoint = (AccessPoint) preference;
            /** Bypass dialog for unsecured, unsaved networks */
            if (mSelectedAccessPoint.security == AccessPoint.SECURITY_NONE) {
                mSelectedAccessPoint.generateOpenNetworkConfig();
                mLogic.mWifiManagerGlobal.connect(mSelectedAccessPoint.getConfig(), mLogic.mConnectListener);
            } else {
                WifiConfig config = mSelectedAccessPoint.getConfig();
                DetailedState state = mSelectedAccessPoint.getState();
                int level = mSelectedAccessPoint.getLevel();
                int netId = mSelectedAccessPoint.networkId;
                int rssi = mSelectedAccessPoint.getRssi();
                Log.i(TAG, "state::" + state + " level::" + level + " netId::" + netId + " rssi::" + rssi);

                if (rssi == Integer.MAX_VALUE) {
                    Utils.showToast(getActivity(), R.string.wifi_failue_out_of_range);
                    Log.i(TAG, "" + getActivity().getResources().getString(R.string.wifi_failue_out_of_range));
                    return true;
                }

                if (config != null && config.status == WifiConfiguration.Status.DISABLED) {
                    if (config.disableReason == WifiConfiguration.DISABLED_AUTH_FAILURE) {
                        showPasswordDialog(mSelectedAccessPoint);
                        return true;
                    }
                }

                // Saved ap clicked.
                if (netId != WifiConfiguration.INVALID_NETWORK_ID) {
                    if (config != null && mSelectedAccessPoint.isPasswordSaved()) {
                        if (config.status == WifiConfiguration.Status.CURRENT) {
                            Utils.showToast(getActivity(), R.string.wifi_already_connect_tip);
                        } else {
                            mLogic.mWifiManagerGlobal.connect(config, mLogic.mConnectListener);
                            Log.i(TAG, "Try to connect the access point directly!");
                            Utils.showToast(getActivity(), R.string.wifi_already_saved_tip);
                        }
                        return true;
                    }
                }

                showPasswordDialog(mSelectedAccessPoint);
                Log.i(TAG, "show dialog");
            }
        } else if (preference == mWifiCheckBox) {
            boolean enable = mWifiCheckBox.isChecked();
            boolean etherstatus = mActivity.getEthernetSettingLogic().mEthernetManagerGlobal.isEnabled();
            if (enable && mLogic.mWifiManager.isWifiApEnabled()) {
                mLogic.mWifiManager.setWifiApEnabled(null, false);
            }
            ConnectivityManager cm = (ConnectivityManager) mActivity.getSystemService(Activity.CONNECTIVITY_SERVICE);
            NetworkInfo networkInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
            mLogic.mWifiManager.setWifiEnabled(enable);
        } else {
            return false;
        }

        return true;
    }

    // When not saved ap clicked or authentication problem ap clicked,password
    // input dialog will be shown.
    private void showPasswordDialog(AccessPoint accessPoint) {
        mPasswordDialog = new PasswordInputDialog(this.getActivity(), mSelectedAccessPoint);
        mPasswordDialog.show();
        mPasswordDialog.setOnConfirmClickListener(new ConfirmClickListener());
    }

    private class ConfirmClickListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            if (mPasswordDialog != null && mPasswordDialog.isShowing()) {
                if ("INPUT_PASSWORD".equals(mPasswordDialog.getDialogType())) {
                    mLogic.mWifiManagerGlobal.connect(mPasswordDialog.getConfig(), mLogic.mConnectListener);
                    mPasswordDialog.dismiss();
                } else if ("ADD_WIFI".equals(mPasswordDialog.getDialogType())) {
                    String password = "";
                    int wifiSecurity = mPasswordDialog.getSecurity();
                    String ssid = mPasswordDialog.getSsid();
                    if (wifiSecurity != AccessPoint.SECURITY_NONE) {
                        password = mPasswordDialog.getPassword();
                    }
                    mLogic.mWifiManager.addNetwork(mPasswordDialog.CreateWifiInfo(ssid, password, wifiSecurity));
                    int netId = mLogic.mWifiManager.addNetwork(mPasswordDialog.CreateWifiInfo(ssid, password,
                            wifiSecurity));
                    boolean bRet = mLogic.mWifiManager.enableNetwork(netId, true);
                    if (!bRet) {
                        return;
                    }
                    mLogic.mWifiManager.setWifiEnabled(true);
                    mLogic.mWifiManager.saveConfiguration();
                    mLogic.mWifiManager.updateNetwork(mPasswordDialog.CreateWifiInfo(ssid, password, wifiSecurity));
                    mPasswordDialog.dismiss();
                }
            }
        }
    }

    public WifiEnabler getWifiEnabler() {
        return mWifiEnabler;
    }

    public void scannerPause() {
        mLogic.mScanner.pause();
    }

    public void registerReceiver() {
        getActivity().registerReceiver(mLogic.mReceiver, mLogic.mFilter);
    }

    public void unregisterReceiver() {
        Log.i(TAG, "unregisterReceiver");
        getActivity().unregisterReceiver(mLogic.mReceiver);
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
        for (WifiConfig config : mLogic.mWifiManagerGlobal.getConfiguredNetworks()) {
            // Log.d(TAG, "config : " + config.toString() + " ap : " + ap.ssid);
            if (config != null) {
                String ssid = config.SSID == null ? "" : config.SSID.replace("\"", "");
                if (ssid != null && ssid.equals(ap.ssid)) {
                    ap.setConfig(config);
                }
            }
        }

        final LinkPropertyConfigDialog configDialog = new LinkPropertyConfigDialog(getActivity(), ap);
        configDialog.setFromWhere(LinkPropertyConfigDialog.FROM_WIFI);
        configDialog.show();

        configDialog.setOnSubmitClickListener(new OnSubmitClickListener() {
            @Override
            public void onSubmit(View v, int oprerationCode, WifiConfig config) {
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
            public void onConfigFinish(WifiConfig config) {
                if (config == null || config.getIpConfiguration() == null) {
                    return;
                }

                mLogic.mWifiManagerGlobal.save(config, mLogic.mSaveListener);
            }
        });
    }

    void forget(WifiConfig config) {
        if (config == null || config.networkId == WifiConfiguration.INVALID_NETWORK_ID) {
            // Should not happen, but a monkey seems to triger it
            Log.e(TAG, "Failed to forget invalid network " + mSelectedAccessPoint.getConfig());
            return;
        }

        mLogic.mWifiManager.forget(config.networkId, mLogic.mForgetListener);

        if (mLogic.mWifiManager.isWifiEnabled()) {
            mLogic.mScanner.resume();
        }

        updateAccessPoints();
        insertAddWifi();

    }

    public AccessPoint getSelectedAccessPoint() {
        return mSelectedAccessPoint;
    }
}
