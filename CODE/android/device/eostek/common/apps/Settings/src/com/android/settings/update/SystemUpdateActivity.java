
package com.android.settings.update;

import scifly.provider.SciflyStore;
import scifly.provider.SciflyStore.Global;
import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.UserHandle;
import android.provider.Settings;
import android.view.View;
import android.view.View.OnClickListener;
import com.android.settings.widget.TextSelectorSpecialWidget;
import com.android.settings.widget.TitleWidget;
import com.android.settings.widget.ValueChangeListener;
import com.android.settings.R;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.util.Log;
import android.content.ServiceConnection;
import com.android.settings.update.UpdateService.UpdateBinder;
import android.content.ComponentName;
import android.os.IBinder;

public class SystemUpdateActivity extends Activity implements OnClickListener, UpdateService.EngineChangeListener {

    @Override
    public void onEngineChange() {
        log(">>>>>>>>>>>>>>>>>>>>>>>onEngineChange");
        mHandler.obtainMessage().sendToTarget();
    }

    private UpdateService mService;

    private ServiceConnection conn = new ServiceConnection() {

        @Override
        public void onServiceDisconnected(ComponentName arg0) {
            log("onServiceDisconnected...");
        }

        @Override
        public void onServiceConnected(ComponentName arg0, IBinder arg1) {
            log("onServiceConnected...");
            mService = ((UpdateBinder) arg1).getService();
            if (mService != null) {
                mService.regitsterEngineChangeListener(SystemUpdateActivity.this);
            }
        }
    };

    private boolean mIsHttp = true;

    public void setIsHttp(boolean isHttp) {
        this.mIsHttp = isHttp;
    }

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            log(">>>>>>>>>>>>>>>>>>>ChangeText");
            mLoadAccelerationPatternTextSelectorWidget.setSelectorText(isHttp(getIsHttp()));
        }
    };

    @Override
    protected void onCreate(Bundle arg0) {
        super.onCreate(arg0);
        setContentView(R.layout.system_update_activity_layout);
        findViews();
        updateNetUpdateState();
        registerListener();
        Intent intent = new Intent(SystemUpdateActivity.this, UpdateService.class);
        bindService(intent, conn, BIND_AUTO_CREATE);
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
        mLoadAccelerationPatternTextSelectorWidget.setSelectorText(isHttp(getIsHttp()));

    }

    private void setTitleWidget() {
        TitleWidget tw = (TitleWidget) findViewById(R.id.title_widget);
        if (tw != null) {
            tw.setMainTitleText(getString(R.string.action_settings));
            tw.setFirstSubTitleText(getString(R.string.system_update), true);
        }
    }

    @Override
    public void onClick(View v) {

        switch (v.getId()) {
            case R.id.update_net_item:
                Intent intent = new Intent(this, SystemNetUpdateActivity.class);
                startActivity(intent);
                break;
            case R.id.update_local_item:
                intent = new Intent(this, SystemLocalUpdateActivity.class);
                startActivity(intent);
                break;
            case R.id.recovery_item:
                MasterClearDialog dialog = new MasterClearDialog(SystemUpdateActivity.this);
                dialog.show();

                break;
        }

    }

    private int getIsHttp() {
        String engine = "";
        engine = SciflyStore.Global.getString(getContentResolver(), Global.DOWNLOAD_ENGINE, "http");
        if (engine.equals("http")) {
            setIsHttp(true);
            return 1;
        } else {
            setIsHttp(false);
            return 0;
        }
    }

    private String isHttp(int value) {
        String isHttp = null;
        String engine = "";
        if (value == 1) {
            isHttp = getString(R.string.isHttp);
            engine = "http";
        } else if (value == 0) {
            isHttp = getString(R.string.isP2p);
            engine = "p2p";
        }
        SciflyStore.Global.putString(getContentResolver(), Global.DOWNLOAD_ENGINE, engine);
        return isHttp;
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
        unbindService(conn);
        mService.unregitsterEngineChangeListener(SystemUpdateActivity.this);
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
