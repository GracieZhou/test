
package com.android.settings.network;

import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.android.settings.BaseSettingActivity;
import com.android.settings.R;
import com.android.settings.network.ethernet.EthernetSettingLogic;
import com.android.settings.userbackup.WifiInfoBackUp;
import com.android.settings.util.Utils;

/**
 * NetworkSettingActivity
 */
public class NetworkSettingActivity extends BaseSettingActivity {

    private static final String TAG = "NetworkSetting";

    private FragmentManager mFragmentManager;

    private EthernetSettingLogic mEthernetSettingLogic;

    private NetworkFragment mNetworkFragment;

    private static WifiInfoBackUp mWifiInfoBackUp = null;

    private BroadcastReceiver mNetworkStatusChangedReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "<<<<on receive<<<<");
            String action = intent.getAction();
            if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
                Log.d(TAG, "<<<<network status change<<<<");
                mNetworkFragment.updateNetworkState();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // register for network state changed
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        registerReceiver(mNetworkStatusChangedReceiver, filter);
        mWifiInfoBackUp = new WifiInfoBackUp(getApplicationContext());
        mFragmentManager = getFragmentManager();
        mFragmentManager.beginTransaction().replace(R.id.fragment_content, new NetworkFragment(), "NetworkFragment")
                .commit();
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "<<<activity<<on resume<<<<<");
        super.onResume();
        mNetworkFragment = (NetworkFragment) getFragmentManager().findFragmentByTag("NetworkFragment");
        // update network state
        mNetworkFragment.updateNetworkState();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mNetworkStatusChangedReceiver);
    }

    /**
     * Start activity whose full class name which with package name is cls.
     * 
     * @param cls {@link Class}
     */
    public void startActivity(String cls) {
        if (TextUtils.isEmpty(cls)) {
            Log.w(TAG, "class name is empty");
            return;
        }

        Class<?> bluetoothSettingsActivity = null;
        try {
            bluetoothSettingsActivity = Class.forName(cls);
        } catch (ClassNotFoundException e) {
            Log.e(TAG, "class not found exception. ", e);
            return;
        }
        Utils.intentForward(this, bluetoothSettingsActivity);
    }

    /**
     * get logics.
     * 
     * @return
     */
    public EthernetSettingLogic getEthernetSettingLogic() {
        if (mEthernetSettingLogic == null) {
            mEthernetSettingLogic = new EthernetSettingLogic(this);
        }
        return mEthernetSettingLogic;
    }

    @Override
    public void setSubTitle() {
        mTitle.setSubTitleText(getString(R.string.network_setting), "");
    }

    @Override
    public void setSubTitle(int resId) {
        mTitle.setSubTitleText(getString(R.string.network_setting), getString(resId));
    }
}
