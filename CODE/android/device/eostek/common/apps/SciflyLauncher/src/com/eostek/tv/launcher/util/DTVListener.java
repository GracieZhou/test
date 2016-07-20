
package com.eostek.tv.launcher.util;

import android.util.Log;

import com.eostek.tv.launcher.HomeApplication;
import com.eostek.tv.launcher.ViewHolder;
import com.mstar.android.tvapi.dtv.listener.OnDtvPlayerEventListener;
import com.mstar.android.tvapi.dtv.vo.DtvEventScan;

/*
 * projectName： TVLauncher
 * moduleName： DTVListener.java
 *
 * @author chadm.xiang
 * @version 1.0.0
 * @time  2014-11-24 下午5:27:49
 * @Copyright © 2014 Eos Inc.
 */

public class DTVListener implements OnDtvPlayerEventListener {

    private final String TAG = DTVListener.class.getSimpleName();

    /** the current page index,0 for my tv, **/
    private int mIndex;
    
    private ViewHolder mHolder;

    public DTVListener(ViewHolder holder) {
        this.mHolder = holder;
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
     * when there is a dtv signal on,this method will be called (non-Javadoc)
     * @see
     * com.mstar.android.tvapi.dtv.listener.OnDtvPlayerEventListener#onSignalLock
     * (int)
     */
    @Override
    public boolean onSignalLock(int arg0) {
        Log.v(TAG, "onSignalLock");
        mIndex = mHolder.getmCurTitleIndex();
        // when the index is not my tv ,enable mute
        if (mIndex != 0) {
            TvUtils.pageChangeMute(HomeApplication.getInstance(), true);
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * @see
     * com.mstar.android.tvapi.dtv.listener.OnDtvPlayerEventListener#onSignalUnLock
     * (int)
     */
    @Override
    public boolean onSignalUnLock(int arg0) {
        Log.v(TAG, "onSignalUnLock");
        return false;
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
