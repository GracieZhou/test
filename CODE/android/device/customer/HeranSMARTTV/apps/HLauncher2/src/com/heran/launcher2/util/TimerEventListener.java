
package com.heran.launcher2.util;

import com.heran.launcher2.widget.OrderReminderDialog;
import com.mstar.android.tvapi.common.TimerManager;
import com.mstar.android.tvapi.common.TimerManager.OnTimerEventListener;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

public class TimerEventListener implements OnTimerEventListener {
    private static final String TAG = TimerEventListener.class.getSimpleName();

    private Context mContext;

    private static TimerEventListener listener;

    private OrderReminderDialog mDialog;

    public static TimerEventListener getInstance(Context context) {
        Log.e(TAG, "TimerEventListener getInstance");
        if (listener == null) {
            listener = new TimerEventListener(context);
        }
        return listener;
    }

    private TimerEventListener(Context context) {
        this.mContext = context;
    }

    @Override
    public boolean onDestroyCountDown(TimerManager arg0, int arg1, int arg2, int arg3) {
        return false;
    }

    @Override
    public boolean onEpgTimeUp(TimerManager arg0, int arg1, int arg2, int arg3) {
        return false;
    }

    @Override
    public boolean onEpgTimerCountDown(TimerManager arg0, int arg1, int leftTime, int arg3) {
        Log.e(TAG, "onEpgTimerCountDown; leftTime is " + leftTime);
        if (leftTime > 0) {
            if (mDialog != null && mDialog.isShowing()) {
                mDialog.updateLeftTime(leftTime);
            } else {
                mDialog = new OrderReminderDialog(mContext, leftTime);
                mDialog.show();
            }
        } else if (leftTime <= 0) {
            if (mDialog == null) {
                mDialog = new OrderReminderDialog(mContext, leftTime);
                mDialog.show();
            }
            mDialog.execEpgTimerAction();
            if (mDialog != null) {
                mDialog.dismiss();
            }
        }
        return false;
    }

    @Override
    public boolean onEpgTimerRecordStart(TimerManager arg0, int arg1, int arg2, int arg3) {
        Log.e(TAG, "onEpgTimerRecordStart from boot");
        Intent intent = new Intent();
        intent.setClassName("com.eostek.tv.player", "com.eostek.tv.player.PlayerActivity");
        intent.putExtra("PVR_ONE_TOUCH_MODE", 1);
        mContext.startActivity(intent);
        return false;
    }

    @Override
    public boolean onLastMinuteWarn(TimerManager arg0, int arg1, int arg2, int arg3) {
        return false;
    }

    @Override
    public boolean onOadTimeScan(TimerManager arg0, int arg1, int arg2, int arg3) {
        return false;
    }

    @Override
    public boolean onOneSecondBeat(TimerManager arg0, int arg1, int arg2, int arg3) {
        return false;
    }

    @Override
    public boolean onPowerDownTime(TimerManager arg0, int arg1, int arg2, int arg3) {
        return false;
    }

    @Override
    public boolean onPvrNotifyRecordStop(TimerManager arg0, int arg1, int arg2, int arg3) {
        return false;
    }

    @Override
    public boolean onSignalLock(TimerManager arg0, int arg1, int arg2, int arg3) {
        return false;
    }

    @Override
    public boolean onSystemClkChg(TimerManager arg0, int arg1, int arg2, int arg3) {
        return false;
    }

    @Override
    public boolean onUpdateLastMinute(TimerManager mgr, int what, int arg1, int arg2) {
        return false;
    }
}
