
package com.eostek.tv.launcher.util;

import android.util.Log;

import com.eostek.tv.launcher.HomeApplication;
import com.eostek.tv.launcher.ViewHolder;
import com.mstar.android.tvapi.atv.listener.OnAtvPlayerEventListener;
import com.mstar.android.tvapi.atv.vo.AtvEventScan;

/*
 * projectName： TVLauncher
 * moduleName： ATVListener.java
 *
 * @author chadm.xiang
 * @version 1.0.0
 * @time  2014-11-24 下午5:27:11
 * @Copyright © 2014 Eos Inc.
 */

public class ATVListener implements OnAtvPlayerEventListener {
    
    private final String TAG = ATVListener.class.getSimpleName();

    /** the current page index,0 for my tv, **/
    private int mIndex = -1;
    
    private ViewHolder mHolder;

    public ATVListener(ViewHolder holder) {
        this.mHolder = holder;
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
     * com.mstar.android.tvapi.atv.listener.OnAtvPlayerEventListener#onSignalUnLock
     * (int)
     */
    @Override
    public boolean onSignalUnLock(int arg0) {
        Log.v(TAG, "onSignalUnLock");
        return false;
    }

}
