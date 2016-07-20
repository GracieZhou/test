
package com.eostek.tv.tvlistener;

import android.app.Activity;
import android.content.Intent;
import android.os.Message;

import com.eostek.tv.PlayerActivity;
import com.eostek.tv.pvr.PVRActivity;
import com.eostek.tv.utils.Constants;
import com.eostek.tv.utils.LogUtil;
import com.eostek.tv.utils.UtilsTools;
import com.eostek.tv.widget.OrderReminderDialog;
import com.mstar.android.tvapi.common.listener.OnEventListener;

/*
 * projectName： Tv
 * moduleName： EventListener.java
 *
 * @author chadm.xiang
 * @version 1.0.0
 * @time  2015-7-27 下午6:16:01
 * @Copyright © 2014 Eos Inc.
 * 
 * copy the code from TimeEventListener,since the TimeEventListener is deprecated,we should use OnEventListener
 */
public class EventListener implements OnEventListener {

    /**
     * {@link OnTimerEventListener#onUpdateLastMinute}
     * 
     * @see com.mstar.android.tvapi.common#TimerManager
     **/
    private static final int TVTIMER_LAST_MINUTE_UPDATE = 2;

    /**
     * {@link OnTimerEventListener#onEpgTimerCountDown}
     * 
     * @see com.mstar.android.tvapi.common#TimerManager
     **/
    private static final int TVTIMER_EPG_TIMER_COUNTDOWN = 8;

    /**
     * {@link OnTimerEventListener#onEpgTimerRecordStart}
     * 
     * @see com.mstar.android.tvapi.common#TimerManager
     **/
    private static final int TVTIMER_EPG_TIMER_RECORD_START = 9;

    /**
     * {@link OnTimerEventListener#onPvrNotifyRecordStop}
     * 
     * @see com.mstar.android.tvapi.common#TimerManager
     **/
    private static final int TVTIMER_PVR_NOTIFY_RECORD_STOP = 10;

    private Activity mContext;

    private OrderReminderDialog mDialog;

    public EventListener(Activity context) {
        this.mContext = context;
    }

    /*
     * (non-Javadoc)
     * @see
     * com.mstar.android.tvapi.common.listener.OnEventListener#onEvent(android
     * .os.Message)
     */
    @Override
    public boolean onEvent(Message msg) {

        switch (msg.what) {
            case TVTIMER_EPG_TIMER_COUNTDOWN:
                handleEPGTimerCountDown(msg);
                break;
            case TVTIMER_EPG_TIMER_RECORD_START:
                handleRecordStart(msg);
                break;
            case TVTIMER_PVR_NOTIFY_RECORD_STOP:
                handleRecordStop(msg);
                break;
            case TVTIMER_LAST_MINUTE_UPDATE:
//                handleMinuteUpdate(msg);
                break;
            default:
                break;
        }
        return false;
    }

    private boolean handleEPGTimerCountDown(Message msg) {
        int leftTime = msg.arg1;
        LogUtil.i("leftTime is " + leftTime);
        if (leftTime > 0) {
            // show dialog
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
            // 这一段代码逻辑比较奇怪
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

    /**
     * when receive recrod start event,start tv and enter PVR
     * 
     * @param msg
     * @return
     */
    private boolean handleRecordStart(Message msg) {
        LogUtil.i(msg.toString());
        if (!UtilsTools.getCurTopActivityName(mContext).equals(PlayerActivity.class.getName())) {
            UtilsTools.startPlayerActivity(mContext);
        }
        Intent intent = new Intent(mContext, PVRActivity.class);
        intent.putExtra(Constants.PVR_ONE_TOUCH_MODE, Constants.PVR_GENERAL_FLAG);
        mContext.startActivity(intent);
        return false;
    }

    private boolean handleRecordStop(Message msg) {
        LogUtil.i(msg.toString());
        Intent intent = new Intent(mContext, PVRActivity.class);
        intent.putExtra(Constants.PVR_ONE_TOUCH_MODE, Constants.PVR_DISMISS_FLAG);
        mContext.startActivity(intent);
        return false;
    }

    /**
     * update count down time when receive TVTIMER_LAST_MINUTE_UPDATE event
     * 
     * @param msg
     * @return
     */
    private boolean handleMinuteUpdate(Message msg) {
        LogUtil.i(msg.toString());
        Intent counterDownIntent = new Intent(Constants.START_COUNTERDOWN);
        counterDownIntent.putExtra(Constants.LEFT_TIME, msg.arg1);
        mContext.startActivity(counterDownIntent);
        return false;
    }
    

}
