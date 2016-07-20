
package com.eostek.tv.tvlistener;

import android.app.Activity;
import android.content.Intent;
import android.util.Log;

import com.eostek.tv.pvr.PVRActivity;
import com.eostek.tv.utils.Constants;
import com.mstar.android.tvapi.common.TimerManager;
import com.mstar.android.tvapi.common.TimerManager.OnTimerEventListener;
import com.eostek.tv.utils.UtilsTools;
import com.eostek.tv.widget.OrderReminderDialog;

/**
 * @deprecated 没有用到此文件，这个类的功能移到{@link EventListener}类里面了
 */
public class TimerEventListener implements OnTimerEventListener {
    private static final String TAG = TimerEventListener.class.getSimpleName();

    private static Activity mContext;

    private OrderReminderDialog mDialog;

    private static TimerEventListener mListener;

    public static TimerEventListener getInstance(Activity context) {
        if (mListener == null) {
            mListener = new TimerEventListener(context);
        }
        mContext = context;
        return mListener;
    }

    private TimerEventListener(Activity context) {
        mContext = context;
    }

    @Override
    public boolean onDestroyCountDown(TimerManager arg0, int arg1, int arg2, int arg3) {
//        Intent intent = new Intent();
//        intent.setAction("com.android.server.tv.TIME_EVENT_DESTROY_COUNT_DOWN");
//        mContext.sendBroadcast(intent);
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
                if (mContext.getParent() != null) {
                    mDialog = new OrderReminderDialog(mContext.getParent(), leftTime);
                } else {
                    mDialog = new OrderReminderDialog(mContext, leftTime);
                }
                mDialog.show();
            }
        } else if (leftTime == 0) {
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
        Log.e(TAG, "onEpgTimerRecordStart");
        if (!UtilsTools.getCurTopActivityName(mContext).equals("com.eostek.tv.PlayerActivity")) {
            UtilsTools.startPlayerActivity(mContext);
        }
        Intent intent = new Intent(mContext, PVRActivity.class);
        intent.putExtra("PVR_ONE_TOUCH_MODE", Constants.PVR_GENERAL_FLAG);
        mContext.startActivity(intent);
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
        Log.e(TAG, "onPvrNotifyRecordStop");
        Intent intent = new Intent(mContext, PVRActivity.class);
        intent.putExtra("PVR_ONE_TOUCH_MODE", Constants.PVR_DISMISS_FLAG);
        mContext.startActivity(intent);
        return false;
    }

    @Override
    public boolean onSignalLock(TimerManager arg0, int arg1, int arg2, int arg3) {
        return false;
    }

    @Override
    public boolean onSystemClkChg(TimerManager arg0, int arg1, int arg2, int arg3) {
        // Intent intent = new Intent();
        // intent.setAction("com.android.server.tv.TIME_EVENT_SYSTEM_CLOCK_CHANGE");
        // mContext.sendBroadcast(intent);
        return false;
    }

    @Override
    public boolean onLastMinuteWarn(TimerManager arg0, int arg1, int arg2, int arg3) {
        // Intent intent = new Intent();
        // intent.setAction("com.android.server.tv.TIME_EVENT_LAST_MINUTE_WARN");
        // mContext.sendBroadcast(intent);
        return false;
    }

    @Override
    public boolean onUpdateLastMinute(TimerManager mgr, int what, int arg1, int arg2) {
        Log.i("lucky", "onUpdateLastMinute");
        Intent counterDownIntent = new Intent(Constants.START_COUNTERDOWN);
        counterDownIntent.putExtra("LeftTime", arg1);
        mContext.startActivity(counterDownIntent);
        return false;
    }
}
