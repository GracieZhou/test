
package com.eostek.tv.tvlistener;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.widget.Toast;

import com.eostek.tv.R;
import com.eostek.tv.pvr.PVRActivity;
import com.eostek.tv.utils.Constants;
import com.eostek.tv.utils.LogUtil;
import com.eostek.tv.utils.TVUtils;
import com.eostek.tv.utils.UtilsTools;
import com.eostek.tv.utils.Constants.EnumScreenMode;
import com.eostek.tv.utils.Constants.EnumSignalProgSyncStatus;
import com.eostek.tv.utils.TVUtils.PVRHelper;
import com.mstar.android.tv.TvCommonManager;
import com.mstar.android.tvapi.common.listener.OnTvPlayerEventListener;
import com.mstar.android.tvapi.common.vo.HbbtvEventInfo;
import com.mstar.android.tvapi.common.vo.TvOsType.EnumInputSource;

/*
 * projectName： Tv
 * moduleName： TvPlayerEventListener.java
 *
 * @author chadm.xiang
 * @version 1.0.0
 * @time  2015-7-28 上午11:05:11
 * @Copyright © 2014 Eos Inc.
 */

public class TvPlayerEventListener implements OnTvPlayerEventListener {

    private Context mContext;

    private Handler mHandler;

    private int mCurSource;

    public TvPlayerEventListener(Context context, Handler handler) {
        this.mContext = context;
        this.mHandler = handler;
        this.mCurSource = TVUtils.getCurTvSource();
    }

    /*
     * (non-Javadoc)
     * @see com.mstar.android.tvapi.common.listener.OnTvPlayerEventListener#
     * on4k2kHDMIDisableDualView(int, int, int)
     */
    @Override
    public boolean on4k2kHDMIDisableDualView(int arg0, int arg1, int arg2) {
        return false;
    }

    /*
     * (non-Javadoc)
     * @see com.mstar.android.tvapi.common.listener.OnTvPlayerEventListener#
     * on4k2kHDMIDisablePip(int, int, int)
     */
    @Override
    public boolean on4k2kHDMIDisablePip(int arg0, int arg1, int arg2) {
        return false;
    }

    /*
     * (non-Javadoc)
     * @see com.mstar.android.tvapi.common.listener.OnTvPlayerEventListener#
     * on4k2kHDMIDisablePop(int, int, int)
     */
    @Override
    public boolean on4k2kHDMIDisablePop(int arg0, int arg1, int arg2) {
        return false;
    }

    /*
     * (non-Javadoc)
     * @see com.mstar.android.tvapi.common.listener.OnTvPlayerEventListener#
     * on4k2kHDMIDisableTravelingMode(int, int, int)
     */
    @Override
    public boolean on4k2kHDMIDisableTravelingMode(int arg0, int arg1, int arg2) {
        return false;
    }

    /*
     * (non-Javadoc)
     * @see com.mstar.android.tvapi.common.listener.OnTvPlayerEventListener#
     * onDtvChannelInfoUpdate(int, int, int)
     */
    @Override
    public boolean onDtvChannelInfoUpdate(int arg0, int arg1, int arg2) {
        return false;
    }

    /*
     * (non-Javadoc)
     * @see com.mstar.android.tvapi.common.listener.OnTvPlayerEventListener#
     * onDtvPsipTsUpdate(int, int, int)
     */
    @Override
    public boolean onDtvPsipTsUpdate(int arg0, int arg1, int arg2) {
        return false;
    }

    /*
     * (non-Javadoc)
     * @see com.mstar.android.tvapi.common.listener.OnTvPlayerEventListener#
     * onEmerencyAlert(int, int, int)
     */
    @Override
    public boolean onEmerencyAlert(int arg0, int arg1, int arg2) {
        return false;
    }

    /*
     * (non-Javadoc)
     * @see com.mstar.android.tvapi.common.listener.OnTvPlayerEventListener#
     * onEpgUpdateList(int, int)
     */
    @Override
    public boolean onEpgUpdateList(int arg0, int arg1) {
        return false;
    }

    /*
     * (non-Javadoc)
     * @see com.mstar.android.tvapi.common.listener.OnTvPlayerEventListener#
     * onHbbtvUiEvent(int, com.mstar.android.tvapi.common.vo.HbbtvEventInfo)
     */
    @Override
    public boolean onHbbtvUiEvent(int arg0, HbbtvEventInfo arg1) {
        return false;
    }

    /*
     * (non-Javadoc)
     * @see
     * com.mstar.android.tvapi.common.listener.OnTvPlayerEventListener#onPopupDialog
     * (int, int, int)
     */
    @Override
    public boolean onPopupDialog(int arg0, int arg1, int arg2) {
        return false;
    }

    /*
     * (non-Javadoc)
     * @see com.mstar.android.tvapi.common.listener.OnTvPlayerEventListener#
     * onPvrNotifyAlwaysTimeShiftProgramNotReady(int)
     */
    @Override
    public boolean onPvrNotifyAlwaysTimeShiftProgramNotReady(int arg0) {
        return false;
    }

    /*
     * (non-Javadoc)
     * @see com.mstar.android.tvapi.common.listener.OnTvPlayerEventListener#
     * onPvrNotifyAlwaysTimeShiftProgramReady(int)
     */
    @Override
    public boolean onPvrNotifyAlwaysTimeShiftProgramReady(int arg0) {
        return false;
    }

    /*
     * (non-Javadoc)
     * @see com.mstar.android.tvapi.common.listener.OnTvPlayerEventListener#
     * onPvrNotifyCiPlusProtection(int)
     */
    @Override
    public boolean onPvrNotifyCiPlusProtection(int arg0) {
        return false;
    }

    /*
     * (non-Javadoc)
     * @see com.mstar.android.tvapi.common.listener.OnTvPlayerEventListener#
     * onPvrNotifyCiPlusRetentionLimitUpdate(int, int)
     */
    @Override
    public boolean onPvrNotifyCiPlusRetentionLimitUpdate(int arg0, int arg1) {
        return false;
    }

    /*
     * (non-Javadoc)
     * @see com.mstar.android.tvapi.common.listener.OnTvPlayerEventListener#
     * onPvrNotifyOverRun(int)
     */
    @Override
    public boolean onPvrNotifyOverRun(int arg0) {
        return false;
    }

    /*
     * (non-Javadoc)
     * @see com.mstar.android.tvapi.common.listener.OnTvPlayerEventListener#
     * onPvrNotifyParentalControl(int, int)
     */
    @Override
    public boolean onPvrNotifyParentalControl(int arg0, int arg1) {
        return false;
    }

    /*
     * (non-Javadoc)
     * @see com.mstar.android.tvapi.common.listener.OnTvPlayerEventListener#
     * onPvrNotifyPlaybackBegin(int)
     */
    @Override
    public boolean onPvrNotifyPlaybackBegin(int arg0) {
        return false;
    }

    /*
     * (non-Javadoc)
     * @see com.mstar.android.tvapi.common.listener.OnTvPlayerEventListener#
     * onPvrNotifyPlaybackSpeedChange(int)
     */
    @Override
    public boolean onPvrNotifyPlaybackSpeedChange(int arg0) {
        return false;
    }

    /*
     * (non-Javadoc)
     * @see com.mstar.android.tvapi.common.listener.OnTvPlayerEventListener#
     * onPvrNotifyPlaybackStop(int)
     */
    @Override
    public boolean onPvrNotifyPlaybackStop(int arg0) {
        return false;
    }

    /*
     * (non-Javadoc)
     * @see com.mstar.android.tvapi.common.listener.OnTvPlayerEventListener#
     * onPvrNotifyPlaybackTime(int, int)
     */
    @Override
    public boolean onPvrNotifyPlaybackTime(int arg0, int arg1) {
        return false;
    }

    /*
     * (non-Javadoc)
     * @see com.mstar.android.tvapi.common.listener.OnTvPlayerEventListener#
     * onPvrNotifyRecordSize(int, int)
     */
    @Override
    public boolean onPvrNotifyRecordSize(int arg0, int arg1) {
        return false;
    }

    /*
     * (non-Javadoc)
     * @see com.mstar.android.tvapi.common.listener.OnTvPlayerEventListener#
     * onPvrNotifyRecordStop(int)
     */
    @Override
    public boolean onPvrNotifyRecordStop(int arg0) {
        return false;
    }

    /*
     * (non-Javadoc)
     * @see com.mstar.android.tvapi.common.listener.OnTvPlayerEventListener#
     * onPvrNotifyRecordTime(int, int)
     */
    @Override
    public boolean onPvrNotifyRecordTime(int arg0, int arg1) {
        return false;
    }

    /*
     * (non-Javadoc)
     * @see com.mstar.android.tvapi.common.listener.OnTvPlayerEventListener#
     * onPvrNotifyTimeShiftOverwritesAfter(int, int)
     */
    @Override
    public boolean onPvrNotifyTimeShiftOverwritesAfter(int arg0, int arg1) {
        return false;
    }

    /*
     * (non-Javadoc)
     * @see com.mstar.android.tvapi.common.listener.OnTvPlayerEventListener#
     * onPvrNotifyTimeShiftOverwritesBefore(int, int)
     */
    @Override
    public boolean onPvrNotifyTimeShiftOverwritesBefore(int arg0, int arg1) {
        return false;
    }

    /*
     * (non-Javadoc)
     * @see com.mstar.android.tvapi.common.listener.OnTvPlayerEventListener#
     * onPvrNotifyUsbRemoved(int, int)
     */
    @Override
    public boolean onPvrNotifyUsbRemoved(int arg0, int arg1) {
        Toast.makeText(mContext, R.string.pvrrecordusbremove, Toast.LENGTH_LONG).show();
        if (PVRActivity.mIsPVRActivityActive) {
            Intent intent = new Intent(mContext, PVRActivity.class);
            intent.putExtra(Constants.PVR_ONE_TOUCH_MODE, Constants.PVR_DISMISS_FLAG);
            mContext.startActivity(intent);
        } else {
            PVRHelper.handlePvrNotifyUsbRemoved();
        }
        return true;
    }

    /*
     * (non-Javadoc)
     * @see com.mstar.android.tvapi.common.listener.OnTvPlayerEventListener#
     * onScreenSaverMode(int, int)
     */
    @Override
    public boolean onScreenSaverMode(int arg0, int arg1) {
        switch (mCurSource) {
            case TvCommonManager.INPUT_SOURCE_DTV:
                if (arg1 == EnumScreenMode.MSRV_DTV_SS_INVALID_SERVICE.ordinal()) {
                } else if (arg1 == EnumScreenMode.MSRV_DTV_SS_NO_CI_MODULE.ordinal()) {
                } else if (arg1 == EnumScreenMode.MSRV_DTV_SS_SCRAMBLED_PROGRAM.ordinal()) {
                } else if (arg1 == EnumScreenMode.MSRV_DTV_SS_CH_BLOCK.ordinal()) {
                    sendScreenModeMsg(mHandler, mContext.getResources().getString(R.string.passwordtip));
                } else if (arg1 == EnumScreenMode.MSRV_DTV_SS_PARENTAL_BLOCK.ordinal()) {
                } else if (arg1 == EnumScreenMode.MSRV_DTV_SS_AUDIO_ONLY.ordinal()) {
                } else if (arg1 == EnumScreenMode.MSRV_DTV_SS_DATA_ONLY.ordinal()) {
                } else if (arg1 == EnumScreenMode.MSRV_DTV_SS_COMMON_VIDEO.ordinal()) {
                }
                break;
            case TvCommonManager.INPUT_SOURCE_ATV:
                break;
            case TvCommonManager.INPUT_SOURCE_HDMI:
            case TvCommonManager.INPUT_SOURCE_HDMI2:
            case TvCommonManager.INPUT_SOURCE_HDMI3:
            case TvCommonManager.INPUT_SOURCE_HDMI4:
            case TvCommonManager.INPUT_SOURCE_CVBS:
            case TvCommonManager.INPUT_SOURCE_YPBPR:
                if (arg1 == EnumSignalProgSyncStatus.E_SIGNALPROC_STABLE_SUPPORT_MODE.ordinal()) {
                    mHandler.sendEmptyMessageDelayed(Constants.SHOWINFO, 3 * 1000);
                }
                break;
            case TvCommonManager.INPUT_SOURCE_VGA:
                if (arg1 == EnumSignalProgSyncStatus.E_SIGNALPROC_STABLE_UN_SUPPORT_MODE.ordinal()) {
                    sendScreenModeMsg(mHandler, mContext.getResources().getString(R.string.unsupporttips));
                } else if (arg1 == EnumSignalProgSyncStatus.E_SIGNALPROC_STABLE_SUPPORT_MODE.ordinal()) {
                    // show info view when mode change
                    mHandler.sendEmptyMessage(Constants.MSG_UPDATE_SCREEN_SAVER_UI);
                } else if (arg1 == EnumSignalProgSyncStatus.E_SIGNALPROC_AUTO_ADJUST.ordinal()) {
                    sendScreenModeMsg(mHandler, mContext.getResources().getString(R.string.autoadjust));
                }
                break;

            default:
                break;
        }
        return true;
    }

    /*
     * (non-Javadoc)
     * @see
     * com.mstar.android.tvapi.common.listener.OnTvPlayerEventListener#onSignalLock
     * (int)
     */
    @Override
    public boolean onSignalLock(int arg0) {
        LogUtil.i("arg0 = " + arg0);
        // 这一段代码有一些疑问，如果有无信号的View在显示，难道不dismiss掉
        // warning: ensure the right mSource value
        mCurSource = TVUtils.getCurTvSource();
        if (mCurSource ==  TvCommonManager.INPUT_SOURCE_ATV || mCurSource == TvCommonManager.INPUT_SOURCE_DTV) {
            // mHolder.dismissSignalView();
        	return false;
        } else {
        	 mHandler.sendEmptyMessage(Constants.MSG_OTHER_SIGNAL_LOCK);
        } 

        // if in count down activity,finish the Activity
        if (UtilsTools.getCurTopActivityName(mContext).equals(Constants.COUNTDOWNACTIVITY)) {
            Intent intent = new Intent(Constants.START_COUNTERDOWN);
            intent.putExtra(Constants.COUNT_DOWN, Constants.DISMISSCOUTDOWN);
            mContext.startActivity(intent);
        }

        // remove auto sleep message
        mHandler.removeMessages(Constants.STANDBY);

        return false;
    }

    /*
     * (non-Javadoc)
     * @see com.mstar.android.tvapi.common.listener.OnTvPlayerEventListener#
     * onSignalUnLock(int)
     */
    @Override
    public boolean onSignalUnLock(int arg0) {
        LogUtil.i("arg0 = " + arg0);
        // send message to update signal view text
        Message msg = Message.obtain();
        msg.what = Constants.MSG_OTHER_SIGNAL_UNLOCK;
        msg.arg1 = TVUtils.getCurTvSource();
        mHandler.sendMessage(msg);

        // if auto sleep is open,send delay message
        if (UtilsTools.isSaveModeOpen(mContext)) {
            mHandler.removeMessages(Constants.STANDBY);
            mHandler.sendEmptyMessageDelayed(Constants.STANDBY, Constants.AUTO_SLEEP_DELAY);
        }
        return false;
    }

    /*
     * (non-Javadoc)
     * @see com.mstar.android.tvapi.common.listener.OnTvPlayerEventListener#
     * onTvProgramInfoReady(int)
     */
    @Override
    public boolean onTvProgramInfoReady(int arg0) {
        return false;
    }

    private void sendScreenModeMsg(Handler handler, Object obj) {
        Message msg = Message.obtain();
        msg.what = Constants.MSG_UPDATE_SCREEN_SAVER_TEXT;
        msg.obj = obj;
        handler.sendMessage(msg);
    }

}
