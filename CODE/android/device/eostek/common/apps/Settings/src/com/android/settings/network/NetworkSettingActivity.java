
package com.android.settings.network;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.ConnectivityManager;
import android.os.Bundle;
import com.android.settings.R;

public class NetworkSettingActivity extends Activity {

    private NetworkSettingHolder mHolder;

    private NetworkSettingLogic mLogic;

    private IntentFilter mIntentFilter;

    private BroadcastReceiver mNetworkStatusChangeReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context arg0, Intent arg1) {
            if (mLogic != null) {
                mLogic.updateStates();
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // getWindow().requestFeature(Window.FEATURE_CUSTOM_TITLE);
        super.onCreate(savedInstanceState);
        setContentView(R.layout.network_activity_layout);
        mHolder = new NetworkSettingHolder(this);
        mLogic = new NetworkSettingLogic(this, mHolder);
        mHolder.findViews();

        mIntentFilter = new IntentFilter(ConnectivityManager.CONNECTIVITY_ACTION);
        this.registerReceiver(mNetworkStatusChangeReceiver, mIntentFilter);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mLogic.updateStates();
    }
}
