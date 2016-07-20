
package com.android.settings.bugreport;

import com.android.settings.R;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.view.View;

public class BugReportActivity extends Activity {
    private BugReportHolder mHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bugreport);
        initUI();
    }

    private void initUI() {
        mHolder = new BugReportHolder(this);
        mHolder.findViews();
        mHolder.initViews();
        mHolder.registerListener();
    }

    public boolean isNetworkConnect() {
        ConnectivityManager cm = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo network = cm.getActiveNetworkInfo();

        if (network == null || !network.isConnected()) {
            return false;
        }
        return true;
    }
}
