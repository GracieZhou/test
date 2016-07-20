
package com.android.settings.update;

import scifly.provider.SciflyStore;
import scifly.provider.SciflyStore.Global;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import com.android.settings.R;
import com.android.settings.widget.TextSelectorSpecialWidget;
import com.android.settings.widget.TitleWidget;
import com.android.settings.widget.ValueChangeListener;
import com.android.settings.R;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.content.ServiceConnection;
import android.content.ComponentName;
import android.os.IBinder;
import com.android.settings.update.ota.SystemNetUpdateActivity;

public class SystemUpdateActivity extends Activity implements OnClickListener{

    private boolean mIsHttp = true;

    public void setIsHttp(boolean isHttp) {
        this.mIsHttp = isHttp;
    }

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.system_update_activity_layout);
        findViews();
        updateNetUpdateState();
        registerListener();
    }

    private void updateNetUpdateState() {
        SharedPreferences sp = getSharedPreferences("system_net_update_state", Activity.MODE_PRIVATE);
    }

    public void findViews() {
        findItems();
        setTitleWidget();
    }

    View mNetUpdateView;

    View mLocalUpdateView;

    TextSelectorSpecialWidget mLoadAccelerationPatternTextSelectorWidget;

    View mRecoveryView;

    private void findItems() {
        mNetUpdateView = findViewById(R.id.update_net_item);
        mLocalUpdateView = findViewById(R.id.update_local_item);
        mLoadAccelerationPatternTextSelectorWidget = (TextSelectorSpecialWidget) findViewById(R.id.load_acceleration_pattern_widget);
        mRecoveryView = findViewById(R.id.recovery_item);
        mNetUpdateView.setOnClickListener(this);
        mLocalUpdateView.setOnClickListener(this);
        mRecoveryView.setOnClickListener(this);
        mLoadAccelerationPatternTextSelectorWidget.setText(this.getString(R.string.load_acceleration_pattern));
        mLoadAccelerationPatternTextSelectorWidget.setValue(1, 0, 1);
        mLoadAccelerationPatternTextSelectorWidget.setSelectorText(isHttp(getEngine()));

    }

    private void setTitleWidget() {
        TitleWidget tw = (TitleWidget) findViewById(R.id.title_widget);
        if (tw != null) {
            tw.setSubTitleText(getString(R.string.system_update));
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.update_net_item:
                Intent intent = new Intent(this, SystemNetUpdateActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
            case R.id.update_local_item:
                intent = new Intent(this, SystemLocalUpdateActivity.class);
                startActivity(intent);
                overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                break;
            case R.id.recovery_item:
                intent = new Intent(this, MasterClearActivity.class);
                startActivity(intent);
                break;
        }

    }

    private int getEngine() {
        String engine = "";
        engine = SciflyStore.Global.getString(getContentResolver(), Global.DOWNLOAD_ENGINE, "http");
        if (engine.equals("http")) {
            setIsHttp(true);
            return 1;
        }
        setIsHttp(false);
        return 0;
    }

    private String isHttp(int value) {
        String engine = "";
        String isHttp = "";
        if (value == 1) {
            engine = getString(R.string.isHttp);
            isHttp = Constants.DOWNLOAD_ENGINE_HTTP;
        } else {
            engine = getString(R.string.isP2p);
            isHttp = Constants.DOWNLOAD_ENGINE_P2P;
        }
        SciflyStore.Global.putString(getContentResolver(), Global.DOWNLOAD_ENGINE, isHttp);
        return engine;
    }

    private void registerListener() {
        mLoadAccelerationPatternTextSelectorWidget.setValueChangeListener(new ValueChangeListener() {

            @Override
            public void onValueChanged(int value) {
                mLoadAccelerationPatternTextSelectorWidget.setSelectorText(isHttp(value));
                if (value == 0) {
                    setIsHttp(true);
                } else {
                    setIsHttp(false);
                }
            }
        });

        mLoadAccelerationPatternTextSelectorWidget.setOnSelectWidgetClickedListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                mLoadAccelerationPatternTextSelectorWidget.getSelectorWidget().calculateValue(1);
            }
        }, new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                mLoadAccelerationPatternTextSelectorWidget.getSelectorWidget().calculateValue(0);
            }
        });
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    private void log(String msg) {
        if (Constants.DBG) {
            Log.d(Constants.TAG, "SystemUpdateActivity: " + msg);
        }
    }

    private void loge(String msg) {
        Log.e(Constants.TAG, "SystemUpdateActivity: " + msg);
    }

}
