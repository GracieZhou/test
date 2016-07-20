
package com.android.settings.network.connectivity;

import android.app.Activity;
import android.os.Bundle;

import com.android.settings.R;

public class ConnectionActivity extends Activity {
    private ConnectionLogic mConnectionLogic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.connection_begin);
        initUI();
    }

    private void initUI() {
        mConnectionLogic = new ConnectionLogic(this);
        new ConnectionHolder(this, mConnectionLogic);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_out_left, R.anim.fade_out);
    }
}
