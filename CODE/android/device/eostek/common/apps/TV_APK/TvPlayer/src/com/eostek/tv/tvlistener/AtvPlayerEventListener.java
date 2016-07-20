
package com.eostek.tv.tvlistener;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import com.eostek.tv.R;
import com.eostek.tv.utils.Constants;
import com.eostek.tv.utils.LogUtil;
import com.eostek.tv.utils.UtilsTools;
import com.mstar.android.tvapi.atv.listener.OnAtvPlayerEventListener;
import com.mstar.android.tvapi.atv.vo.AtvEventScan;

/*
 * projectName： Tv
 * moduleName： AtvPlayerEventListener.java
 *
 * @author chadm.xiang
 * @version 1.0.0
 * @time  2015-7-28 上午10:29:54
 * @Copyright © 2014 Eos Inc.
 */

public class AtvPlayerEventListener implements OnAtvPlayerEventListener {

    private Context mContext;

    private Handler mHandler;

    public AtvPlayerEventListener(Context context, Handler handler) {
        this.mContext = context;
        this.mHandler = handler;
    }

    /*
     * (non-Javadoc)
     * @see com.mstar.android.tvapi.atv.listener.OnAtvPlayerEventListener#
     * onAtvAutoTuningScanInfo(int, com.mstar.android.tvapi.atv.vo.AtvEventScan)
     */
    @Override
    public boolean onAtvAutoTuningScanInfo(int arg0, AtvEventScan arg1) {
        return false;
    }

    /*
     * (non-Javadoc)
     * @see com.mstar.android.tvapi.atv.listener.OnAtvPlayerEventListener#
     * onAtvManualTuningScanInfo(int,
     * com.mstar.android.tvapi.atv.vo.AtvEventScan)
     */
    @Override
    public boolean onAtvManualTuningScanInfo(int arg0, AtvEventScan arg1) {
        return false;
    }

    /*
     * (non-Javadoc)
     * @see com.mstar.android.tvapi.atv.listener.OnAtvPlayerEventListener#
     * onAtvProgramInfoReady(int)
     */
    @Override
    public boolean onAtvProgramInfoReady(int arg0) {
        return false;
    }

    /*
     * (non-Javadoc)
     * @see
     * com.mstar.android.tvapi.atv.listener.OnAtvPlayerEventListener#onSignalLock
     * (int)
     */
    @Override
    public boolean onSignalLock(int arg0) {
        LogUtil.i("arg0 = " + arg0);
        // remove the unlock msg and send lock msg
        mHandler.removeMessages(Constants.MSG_ATV_SIGNAL_UNLOCK);
        mHandler.sendEmptyMessage(Constants.MSG_ATV_SIGNAL_LOCK);

        // if in count down activity,finish the Activity
        if (UtilsTools.getCurTopActivityName(mContext).equals(Constants.COUNTDOWNACTIVITY)) {
            Intent intent = new Intent(Constants.START_COUNTERDOWN);
            intent.putExtra(Constants.COUNT_DOWN, Constants.DISMISSCOUTDOWN);
            mContext.startActivity(intent);
        }

        // remove auto sleep message
        mHandler.removeMessages(Constants.STANDBY);
        return true;
    }

    /*
     * (non-Javadoc)
     * @see
     * com.mstar.android.tvapi.atv.listener.OnAtvPlayerEventListener#onSignalUnLock
     * (int)
     */
    @Override
    public boolean onSignalUnLock(int arg0) {
        LogUtil.i("arg0 = " + arg0);
        // send message to update signal view text
        Message msg = Message.obtain();
        msg.what = Constants.MSG_ATV_SIGNAL_UNLOCK;
        msg.obj = "ATV " + mContext.getResources().getString(R.string.nosignaltips);
        mHandler.sendMessageDelayed(msg, 1000);

        // if auto sleep is open ,send message
        if (UtilsTools.isSaveModeOpen(mContext)) {
            mHandler.removeMessages(Constants.STANDBY);
            mHandler.sendEmptyMessageDelayed(Constants.STANDBY, Constants.AUTO_SLEEP_DELAY);
        }
        return false;
    }

}
