
package com.eostek.tv.tvlistener;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;

import com.eostek.tv.R;
import com.eostek.tv.utils.Constants;
import com.eostek.tv.utils.LogUtil;
import com.eostek.tv.utils.UtilsTools;
import com.mstar.android.tvapi.dtv.listener.OnDtvPlayerEventListener;
import com.mstar.android.tvapi.dtv.vo.DtvEventScan;

/*
 * projectName： Tv
 * moduleName： DtvPlayerEventListener.java
 *
 * @author chadm.xiang
 * @version 1.0.0
 * @time  2015-7-28 上午10:33:51
 * @Copyright © 2014 Eos Inc.
 */

public class DtvPlayerEventListener implements OnDtvPlayerEventListener {

    private Context mContext;

    private Handler mHandler;

    public DtvPlayerEventListener(Context context, Handler handler) {
        this.mContext = context;
        this.mHandler = handler;
    }

    /*
     * (non-Javadoc)
     * @see com.mstar.android.tvapi.dtv.listener.OnDtvPlayerEventListener#
     * onAudioModeChange(int, boolean)
     */
    @Override
    public boolean onAudioModeChange(int arg0, boolean arg1) {
        return false;
    }

    /*
     * (non-Javadoc)
     * @see com.mstar.android.tvapi.dtv.listener.OnDtvPlayerEventListener#
     * onChangeTtxStatus(int, boolean)
     */
    @Override
    public boolean onChangeTtxStatus(int arg0, boolean arg1) {
        return false;
    }

    /*
     * (non-Javadoc)
     * @see com.mstar.android.tvapi.dtv.listener.OnDtvPlayerEventListener#
     * onCiLoadCredentialFail(int)
     */
    @Override
    public boolean onCiLoadCredentialFail(int arg0) {
        return false;
    }

    /*
     * (non-Javadoc)
     * @see com.mstar.android.tvapi.dtv.listener.OnDtvPlayerEventListener#
     * onDtvAutoTuningScanInfo(int, com.mstar.android.tvapi.dtv.vo.DtvEventScan)
     */
    @Override
    public boolean onDtvAutoTuningScanInfo(int arg0, DtvEventScan arg1) {
        return false;
    }

    /*
     * (non-Javadoc)
     * @see com.mstar.android.tvapi.dtv.listener.OnDtvPlayerEventListener#
     * onDtvAutoUpdateScan(int)
     */
    @Override
    public boolean onDtvAutoUpdateScan(int arg0) {
        return false;
    }

    /*
     * (non-Javadoc)
     * @see com.mstar.android.tvapi.dtv.listener.OnDtvPlayerEventListener#
     * onDtvChannelNameReady(int)
     */
    @Override
    public boolean onDtvChannelNameReady(int arg0) {
        return false;
    }

    /*
     * (non-Javadoc)
     * @see com.mstar.android.tvapi.dtv.listener.OnDtvPlayerEventListener#
     * onDtvPriComponentMissing(int)
     */
    @Override
    public boolean onDtvPriComponentMissing(int arg0) {
        return false;
    }

    /*
     * (non-Javadoc)
     * @see com.mstar.android.tvapi.dtv.listener.OnDtvPlayerEventListener#
     * onDtvProgramInfoReady(int)
     */
    @Override
    public boolean onDtvProgramInfoReady(int arg0) {
        return false;
    }

    /*
     * (non-Javadoc)
     * @see com.mstar.android.tvapi.dtv.listener.OnDtvPlayerEventListener#
     * onEpgTimerSimulcast(int, int)
     */
    @Override
    public boolean onEpgTimerSimulcast(int arg0, int arg1) {
        return false;
    }

    /*
     * (non-Javadoc)
     * @see com.mstar.android.tvapi.dtv.listener.OnDtvPlayerEventListener#
     * onGingaStatusMode(int, boolean)
     */
    @Override
    public boolean onGingaStatusMode(int arg0, boolean arg1) {
        return false;
    }

    /*
     * (non-Javadoc)
     * @see com.mstar.android.tvapi.dtv.listener.OnDtvPlayerEventListener#
     * onHbbtvStatusMode(int, boolean)
     */
    @Override
    public boolean onHbbtvStatusMode(int arg0, boolean arg1) {
        return false;
    }

    /*
     * (non-Javadoc)
     * @see com.mstar.android.tvapi.dtv.listener.OnDtvPlayerEventListener#
     * onMheg5EventHandler(int, int)
     */
    @Override
    public boolean onMheg5EventHandler(int arg0, int arg1) {
        return false;
    }

    /*
     * (non-Javadoc)
     * @see com.mstar.android.tvapi.dtv.listener.OnDtvPlayerEventListener#
     * onMheg5ReturnKey(int, int)
     */
    @Override
    public boolean onMheg5ReturnKey(int arg0, int arg1) {
        return false;
    }

    /*
     * (non-Javadoc)
     * @see com.mstar.android.tvapi.dtv.listener.OnDtvPlayerEventListener#
     * onMheg5StatusMode(int, int)
     */
    @Override
    public boolean onMheg5StatusMode(int arg0, int arg1) {
        return false;
    }

    /*
     * (non-Javadoc)
     * @see
     * com.mstar.android.tvapi.dtv.listener.OnDtvPlayerEventListener#onOadDownload
     * (int, int)
     */
    @Override
    public boolean onOadDownload(int arg0, int arg1) {
        return false;
    }

    /*
     * (non-Javadoc)
     * @see
     * com.mstar.android.tvapi.dtv.listener.OnDtvPlayerEventListener#onOadHandler
     * (int, int, int)
     */
    @Override
    public boolean onOadHandler(int arg0, int arg1, int arg2) {
        return false;
    }

    /*
     * (non-Javadoc)
     * @see
     * com.mstar.android.tvapi.dtv.listener.OnDtvPlayerEventListener#onOadTimeout
     * (int, int)
     */
    @Override
    public boolean onOadTimeout(int arg0, int arg1) {
        return false;
    }

    /*
     * (non-Javadoc)
     * @see com.mstar.android.tvapi.dtv.listener.OnDtvPlayerEventListener#
     * onPopupScanDialogFrequencyChange(int)
     */
    @Override
    public boolean onPopupScanDialogFrequencyChange(int arg0) {
        return false;
    }

    /*
     * (non-Javadoc)
     * @see com.mstar.android.tvapi.dtv.listener.OnDtvPlayerEventListener#
     * onPopupScanDialogLossSignal(int)
     */
    @Override
    public boolean onPopupScanDialogLossSignal(int arg0) {
        return false;
    }

    /*
     * (non-Javadoc)
     * @see com.mstar.android.tvapi.dtv.listener.OnDtvPlayerEventListener#
     * onPopupScanDialogNewMultiplex(int)
     */
    @Override
    public boolean onPopupScanDialogNewMultiplex(int arg0) {
        return false;
    }

    /*
     * (non-Javadoc)
     * @see
     * com.mstar.android.tvapi.dtv.listener.OnDtvPlayerEventListener#onRctPresence
     * (int)
     */
    @Override
    public boolean onRctPresence(int arg0) {
        return false;
    }

    /*
     * (non-Javadoc)
     * @see
     * com.mstar.android.tvapi.dtv.listener.OnDtvPlayerEventListener#onSignalLock
     * (int)
     */
    @Override
    public boolean onSignalLock(int arg0) {
        // 当接入信号的时候，此调用此回调方法
        LogUtil.i("arg0 = " + arg0);
        // if in count down activity,finish the Activity
        if (UtilsTools.getCurTopActivityName(mContext).equals(Constants.COUNTDOWNACTIVITY)) {
            Intent intent = new Intent(Constants.START_COUNTERDOWN);
            intent.putExtra(Constants.COUNT_DOWN, Constants.DISMISSCOUTDOWN);
            mContext.startActivity(intent);      //启动CounterDownActivity
        }
        
     // remove auto sleep message
        mHandler.removeMessages(Constants.STANDBY);
        // send message to dismiss the signal view
        mHandler.sendEmptyMessage(Constants.MSG_DTV_SIGNAL_LOCK);
        return true;
    }

    /*
     * (non-Javadoc)
     * @see
     * com.mstar.android.tvapi.dtv.listener.OnDtvPlayerEventListener#onSignalUnLock
     * (int)
     */
    @Override
    public boolean onSignalUnLock(int arg0) {
        // 当信号丢失的时候，此调用此回调方法
        LogUtil.i("arg0 = " + arg0);
        Message msg = Message.obtain();
        msg.what = Constants.MSG_DTV_SIGNAL_UNLOCK;
        msg.obj = "DTV " + mContext.getResources().getString(R.string.nosignaltips);
        mHandler.sendMessage(msg);

        // if auto sleep is open,send delay message
        if (UtilsTools.isSaveModeOpen(mContext)) {
            mHandler.removeMessages(Constants.STANDBY);
            mHandler.sendEmptyMessageDelayed(Constants.STANDBY, Constants.AUTO_SLEEP_DELAY);
        }

        return true;
    }

    /*
     * (non-Javadoc)
     * @see
     * com.mstar.android.tvapi.dtv.listener.OnDtvPlayerEventListener#onTsChange
     * (int)
     */
    @Override
    public boolean onTsChange(int arg0) {
        return false;
    }

    /*
     * (non-Javadoc)
     * @see com.mstar.android.tvapi.dtv.listener.OnDtvPlayerEventListener#
     * onUiOPExitServiceList(int)
     */
    @Override
    public boolean onUiOPExitServiceList(int arg0) {
        return false;
    }

    /*
     * (non-Javadoc)
     * @see com.mstar.android.tvapi.dtv.listener.OnDtvPlayerEventListener#
     * onUiOPRefreshQuery(int)
     */
    @Override
    public boolean onUiOPRefreshQuery(int arg0) {
        return false;
    }

    /*
     * (non-Javadoc)
     * @see com.mstar.android.tvapi.dtv.listener.OnDtvPlayerEventListener#
     * onUiOPServiceList(int)
     */
    @Override
    public boolean onUiOPServiceList(int arg0) {
        return false;
    }

}
