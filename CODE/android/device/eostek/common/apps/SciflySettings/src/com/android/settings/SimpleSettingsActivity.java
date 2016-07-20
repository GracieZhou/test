
package com.android.settings;

import android.app.FragmentManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import com.android.settings.network.ethernet.SimpleEthernetSettingLogic;
import com.android.settings.userbackup.WifiInfoBackUp;

public class SimpleSettingsActivity extends BaseSettingActivity {
    private static final String TAG = "NetworkSetting";

    private FragmentManager mFragmentManager;

    private SimpleEthernetSettingLogic mEthernetSettingLogic;

    private SimpleSettingsFragment mSettingskFragment;

    private static WifiInfoBackUp mWifiInfoBackUp = null;

    private BroadcastReceiver mNetworkStatusChangedReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "<<<<on receive<<<<");
            String action = intent.getAction();
            if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
                Log.d(TAG, "<<<<network status change<<<<");
                mSettingskFragment.updateNetworkState();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        registerReceiver(mNetworkStatusChangedReceiver, filter);
        mWifiInfoBackUp = new WifiInfoBackUp(getApplicationContext());
        mFragmentManager = getFragmentManager();
        mFragmentManager.beginTransaction()
                .replace(R.id.fragment_content, new SimpleSettingsFragment(), "SimpleSettingsFragment").commit();
        mUpgradeParentLayout.setOnClickListener(new OnClickListener() {
            
            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent();
                intent.setClass(SimpleSettingsActivity.this, SimpleSettingsUpgradeActivity.class);
                SimpleSettingsActivity.this.startActivity(intent);
            }
        });
    }
    
    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    public void setSubTitle() {
        mTitle.setSubTitleText(getString(R.string.network_setting), "");
    }

    @Override
    public void setSubTitle(int resId) {
        mTitle.setSubTitleText(getString(R.string.network_setting), getString(resId));
    }

    @Override
    protected void onResume() {
        Log.d(TAG, "<<<activity<<on resume<<<<<");
        super.onResume();
        mSettingskFragment = (SimpleSettingsFragment) getFragmentManager().findFragmentByTag("SimpleSettingsFragment");
        // update network state
        mSettingskFragment.updateNetworkState();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mNetworkStatusChangedReceiver);
    }

    /**
     * get logics.
     * 
     * @return
     */
    public SimpleEthernetSettingLogic getEthernetSettingLogic() {
        if (mEthernetSettingLogic == null) {
            mEthernetSettingLogic = new SimpleEthernetSettingLogic(this);
        }
        return mEthernetSettingLogic;
    }
}
