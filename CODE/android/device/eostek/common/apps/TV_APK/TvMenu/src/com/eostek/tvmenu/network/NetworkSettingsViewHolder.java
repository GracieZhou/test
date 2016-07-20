
package com.eostek.tvmenu.network;

import com.eostek.tvmenu.R;

import android.app.Activity;
import android.util.Log;
import android.widget.TextView;

public class NetworkSettingsViewHolder {

    private static final String TAG = "MSettings.NetworkSettingsViewHolder";

    private Activity mNetworkSettingsActivity;

    // current net-setting position.
    private TextView mSettingTitleTextView;


    public NetworkSettingsViewHolder(Activity networkSettingsActivity) {
        super();
        this.mNetworkSettingsActivity = networkSettingsActivity;
        mSettingTitleTextView = (TextView) mNetworkSettingsActivity
                .findViewById(R.id.net_current_position);

    }

    public void refreshSettingTitle(int id) {
        if (id <= 0) {
            Log.d(TAG, "id <= 0");
            return;
        }
        mSettingTitleTextView.setText(id);
    }

}
