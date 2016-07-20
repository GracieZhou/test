
package com.eostek.tv.player.standby;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.eostek.tv.player.R;
import com.eostek.tv.player.util.Constants;
import com.mstar.android.tv.TvTimerManager;
import com.mstar.android.tvapi.common.vo.EnumSleepTimeState;

public class CounterDownActivity extends Activity {
    private static final String TAG = CounterDownActivity.class.getSimpleName();

    public RootReceiver rootReceiver;

    private LinearLayout linearLayoutPopupOnTime;

    private TextView textViewOnTimeSecond;

    private LinearLayout linearLayoutPopupOffTime;

    private TextView textViewOffTimeSecond;

    private LinearLayout linearLayoutReadySwitch;

    private LinearLayout linearLayoutReadyOff;

    private LinearLayout linearLayoutPopupSystemCLKChange;

    private Thread countDownDetectThread = null;

    private int tvCounterTimeCounter = 0;

    private boolean CounterDownHasDestory = false;

    private final int CLOSE_TVCOUNTER_MENU = 0x5555;

    MyHandler tvCounterHandler = null;

    private String action = null;

    private class RootReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            action = intent.getAction();
            int retVal = 0;
            if (action.equals("com.android.server.tv.TIME_EVENT_DESTROY_COUNT_DOWN")) {
                linearLayoutPopupOffTime.setVisibility(View.GONE);
                finish();
            } else if (action.equals("com.android.server.tv.TIME_EVENT_LAST_MINUTE_WARN")) {
                linearLayoutPopupOffTime.setVisibility(View.GONE);
                // linearLayoutReadyOff.setVisibility(View.VISIBLE);
                tvCounterTimeCounter = 0;
            } else if (action.equals("com.android.server.tv.TIME_EVENT_LAST_MINUTE_UPDATE")) {
                retVal = intent.getIntExtra("LeftTime", -1);
                linearLayoutPopupOffTime.setVisibility(View.VISIBLE);
                textViewOffTimeSecond.setText(retVal + "");
                if (retVal == 0) {
                    finish();
                    Log.i(TAG, "=================== SET TO STANDBY ==============");
                } else if (retVal == -1) {
                    Log.i(TAG, "=================== GET LEFT TIME ERROR ==============");
                    textViewOffTimeSecond.setText("ERROR");
                }
                tvCounterTimeCounter = 0;
            } else if (action.equals("com.android.server.tv.TIME_EVENT_SYSTEM_CLOCK_CHANGE")) {
                TvTimerManager.getInstance().setOffTimerEnable(false);
                TvTimerManager.getInstance().setSleepMode(EnumSleepTimeState.E_OFF);
                linearLayoutPopupOffTime.setVisibility(View.GONE);
                linearLayoutPopupSystemCLKChange.setVisibility(View.VISIBLE);
                BroadcastRev.setCounterDownStarted(false);
                tvCounterTimeCounter = 0;
                // end edit by allen.sun
            } else if (action.equals(Constants.SHOWCOUTDOWN)) {
                retVal = intent.getIntExtra("LeftTime", -1);
                Log.e(TAG, ":::" + retVal);
                linearLayoutPopupOffTime.setVisibility(View.VISIBLE);
                textViewOffTimeSecond.setText(retVal + "");
                tvCounterTimeCounter = 0;
            } else if (action.equals(Constants.DISMISSCOUTDOWN)) {
                linearLayoutPopupOffTime.setVisibility(View.GONE);
                finish();
            }

            if (action.equals("???")) {
                linearLayoutPopupOnTime.setVisibility(View.VISIBLE);
                textViewOnTimeSecond.setText(retVal + "");
            }

            if (action.equals("???")) {
                linearLayoutPopupOnTime.setVisibility(View.GONE);
                linearLayoutReadySwitch.setVisibility(View.VISIBLE);
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
        setContentView(R.layout.eos_counterdown);
        linearLayoutPopupOnTime = (LinearLayout) findViewById(R.id.linear_layout_popup_ontime);
        textViewOnTimeSecond = (TextView) findViewById(R.id.text_view_second);
        linearLayoutPopupOffTime = (LinearLayout) findViewById(R.id.linear_layout_popup_offtime);
        linearLayoutPopupSystemCLKChange = (LinearLayout) findViewById(R.id.linear_layout_popup_system_clkchange);

        textViewOffTimeSecond = (TextView) findViewById(R.id.text_view_offsecond);
        linearLayoutReadySwitch = (LinearLayout) findViewById(R.id.linear_layout_popup_ready_switch);
        linearLayoutReadyOff = (LinearLayout) findViewById(R.id.linear_layout_popup_ready_off);
        rootReceiver = new RootReceiver();
        IntentFilter filter = new IntentFilter();
        filter.addAction("com.android.server.tv.TIME_EVENT_DESTROY_COUNT_DOWN");
        filter.addAction("com.android.server.tv.TIME_EVENT_LAST_MINUTE_WARN");
        filter.addAction("com.android.server.tv.TIME_EVENT_LAST_MINUTE_UPDATE");
        filter.addAction(Constants.DISMISSCOUTDOWN);
        filter.addAction(Constants.SHOWCOUTDOWN);
        // ADD FOR SUPPORT SYSTEM CLOCK CHANGE
        filter.addAction("com.android.server.tv.TIME_EVENT_SYSTEM_CLOCK_CHANGE");
        this.registerReceiver(rootReceiver, filter);

        tvCounterTimeCounter = 0;
        CounterDownHasDestory = false;
        tvCounterHandler = new MyHandler();

        countDownDetectThread = new Thread(new Runnable() {
            @Override
            public void run() {
                while (!CounterDownHasDestory) {
                    try {
                        Thread.sleep(1000);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                    tvCounterTimeCounter++;
                    if (tvCounterTimeCounter >= 5) {
                        Log.e(TAG, "\ntimeout, send CLOSE_TVCOUNTER_MENU to close menu");
                        tvCounterHandler.sendEmptyMessageDelayed(CLOSE_TVCOUNTER_MENU, 0);
                    }
                }
            }
        });

        if (countDownDetectThread != null) {
            countDownDetectThread.start();
        }
    }

    @Override
    public void onUserInteraction() {
        if (linearLayoutPopupOffTime.getVisibility() == View.VISIBLE) {
            Log.d(TAG, "setOffTimerEnable(false)");
            TvTimerManager.getInstance().setOffTimerEnable(false);
            TvTimerManager.getInstance().setSleepMode(EnumSleepTimeState.E_OFF);
            Toast.makeText(this, R.string.str_root_toast_userdiableofftimer, Toast.LENGTH_LONG)
                    .show();
        }
        if (linearLayoutPopupOnTime.getVisibility() == View.VISIBLE) {
            Log.d(TAG, "setOnTimerEnable(false)");
            TvTimerManager.getInstance().setOnTimerEnable(false);
        }

        super.onUserInteraction();

        finish();
    }

    @Override
    protected void onDestroy() {
        CounterDownHasDestory = true;
        BroadcastRev.setCounterDownStarted(false);
        this.unregisterReceiver(rootReceiver);
        super.onDestroy();
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (action != null
                && (action.equals(Constants.DISMISSCOUTDOWN) || action
                        .equals(Constants.SHOWCOUTDOWN))) {
            Intent intentStandby = new Intent(Constants.CANCELSTANDBY);
            sendBroadcast(intentStandby);
        } else {
            TvTimerManager.getInstance().setOffTimerEnable(false);
        }
        finish();
        return true;
    }

    class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            int what = msg.what;
            if (what == CLOSE_TVCOUNTER_MENU) {
                Toast.makeText(CounterDownActivity.this, "Off timer meet error, disable it now!",
                        Toast.LENGTH_LONG).show();
                finish();
            }

            super.handleMessage(msg);
        }
    }
}
