
package com.eostek.tv.standby;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.CountDownTimer;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.eostek.tv.R;
import com.eostek.tv.utils.Constants;
import com.eostek.tv.utils.LogUtil;
import com.eostek.tv.utils.TVUtils;
import com.eostek.tv.utils.UtilsTools;
import com.eostek.tv.utils.TVUtils.PVRHelper;
import com.mstar.android.tv.TvTimerManager;

public class CounterDownActivity extends Activity {

    private CounterDownHolder mHolder;

    private static final int CLOSE_TIME = 60 * 1000;

    private static final int INTERVAL = 1 * 1000;

    /**
     * The flag of which start the Activity
     */
    private String mCountDownFlag = "";

    /**
     * countDown time
     */
    private int mRetVal = 0;
    
    private RecevierCounter mRecevierCounter;

    /**
     * start counDown task
     * 第一个参数：总时间
     * 第二个参数：表示间隔时间
     */
    private CountDownTimer mTimer = new CountDownTimer(CLOSE_TIME, INTERVAL) {

        @Override
        public void onTick(long millisUntilFinished) {
            mHolder.setLayoutVisible(View.VISIBLE);
            mHolder.setOffTime(millisUntilFinished / 1000 + "");
        }

        @Override
        public void onFinish() {
            TVUtils.enterSleepMode(true, true);
            
            mHolder.setLayoutVisible(View.GONE);
            CounterDownActivity.this.finish();
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHolder = new CounterDownHolder(this);
        if (getIntent() != null) {
            mCountDownFlag = getIntent().getStringExtra(Constants.COUNT_DOWN);
        }
        if (Constants.SHOWCOUTDOWN.equals(mCountDownFlag)) {
            mTimer.start();
        } else if (Constants.DISMISSCOUTDOWN.equals(mCountDownFlag)) {
        	finish();
        }
        mRecevierCounter = new RecevierCounter();
        IntentFilter mIntentFilter = new IntentFilter();
        mIntentFilter.addAction("com.android.server.tv.TIME_EVENT_LAST_MINUTE_UPDATE");
        registerReceiver(mRecevierCounter, mIntentFilter);
    }
    
    class RecevierCounter extends BroadcastReceiver {

		@Override
		public void onReceive(Context context, Intent intent) {
			mRetVal = intent.getIntExtra(Constants.LEFT_TIME, -1);
            LogUtil.i("mRetVal = " + mRetVal);
            if (mRetVal != -1) {
                mHolder.setLayoutVisible(View.VISIBLE);
                mHolder.setOffTime(mRetVal + "");
                if (mRetVal == 0) {
                    CounterDownActivity.this.finish();
                }
            } else {
                mCountDownFlag = intent.getStringExtra(Constants.COUNT_DOWN);
                if (Constants.DISMISSCOUTDOWN.equals(mCountDownFlag)) {
                    if (mTimer != null) {
                        mTimer.cancel();
                        mTimer = null;
                    }
                    CounterDownActivity.this.finish();
                }
            }
		}
    	
    }

    @Override
    protected void onNewIntent(Intent intent) {
        if (intent != null) {
            mRetVal = intent.getIntExtra(Constants.LEFT_TIME, -1);
            LogUtil.i("mRetVal = " + mRetVal);
            if (mRetVal != -1) {
                mHolder.setLayoutVisible(View.VISIBLE);
                mHolder.setOffTime(mRetVal + "");
                if (mRetVal == 0) {
                    CounterDownActivity.this.finish();
                }
            } else {
                mCountDownFlag = intent.getStringExtra(Constants.COUNT_DOWN);
                if (Constants.DISMISSCOUTDOWN.equals(mCountDownFlag)) {
                    if (mTimer != null) {
                        mTimer.cancel();
                        mTimer = null;
                    }
                    CounterDownActivity.this.finish();
                }
            }
        }
        super.onNewIntent(intent);
    }

    @Override
    public void onUserInteraction() {
        super.onUserInteraction();
        if (mHolder.getLayoutVisibility() == View.VISIBLE) {
            PVRHelper.setSleepTimeMode(TvTimerManager.SLEEP_TIME_OFF);//设置休眠模式：有关闭，10min,20min
            Toast.makeText(this, R.string.str_root_toast_userdiableofftimer, Toast.LENGTH_LONG).show();
        }
        PVRHelper.setOffTimerEnable(false);
        finish();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        UtilsTools.setCounterDownStarted(false);
        if (mTimer != null) {
            mTimer.cancel();
            mTimer = null;
        }
        unregisterReceiver(mRecevierCounter);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (Constants.SHOWCOUTDOWN.equals(mCountDownFlag)) {
            if (mTimer != null) {
                mTimer.cancel();   
                mTimer = null;
            }
        } else {
            PVRHelper.setOffTimerEnable(false);
        }
//        finish();
        return true;
    }
}
