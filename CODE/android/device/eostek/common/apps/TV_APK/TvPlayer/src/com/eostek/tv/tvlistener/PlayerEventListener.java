
package com.eostek.tv.tvlistener;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;

import com.eostek.tv.PlayerActivity;
import com.eostek.tv.PlayerHolder;
import com.eostek.tv.R;
import com.eostek.tv.pvr.PVRActivity;
import com.eostek.tv.utils.ChannelManagerExt;
import com.eostek.tv.utils.Constants;
import com.eostek.tv.utils.Constants.EnumScreenMode;
import com.eostek.tv.utils.Constants.EnumSignalProgSyncStatus;
import com.eostek.tv.utils.UtilsTools;
import com.mstar.android.tv.TvCommonManager;
import com.mstar.android.tv.TvPvrManager;
import com.mstar.android.tvapi.atv.listener.OnAtvPlayerEventListener;
import com.mstar.android.tvapi.atv.vo.AtvEventScan;
import com.mstar.android.tvapi.common.listener.OnTvPlayerEventListener;
import com.mstar.android.tvapi.common.vo.HbbtvEventInfo;
import com.mstar.android.tvapi.dtv.listener.OnDtvPlayerEventListener;
import com.mstar.android.tvapi.dtv.vo.DtvEventScan;

/**
 * @deprecated 没有用到此文件，相关功能分拆到{@link AtvPlayerEventListener}、
 *             {@link DtvPlayerEventListener}、{@link TvPlayerEventListener}
 */
public class PlayerEventListener {
    private Context mContext;

    private PlayerHolder mHolder;

    private Handler mHandler;

    private AtvPlayerEventListener mAtvPlayerEventListener = null;

    private DtvPlayerEventListener mDtvPlayerEventListener = null;

    private TvPlayerEventListener mTvPlayerEventListener = null;

    private int delayTime = 3 * 1000;

    public PlayerEventListener(Context context) {
        this.mContext = context;
        initListener();
    }

    public PlayerEventListener(Context mContext, PlayerHolder mHolder, Handler mHandler) {
        this.mContext = mContext;
        this.mHolder = mHolder;
        this.mHandler = mHandler;
        initListener();
    }

    private void initListener() {
        mAtvPlayerEventListener = new AtvPlayerEventListener();
        mDtvPlayerEventListener = new DtvPlayerEventListener();
        mTvPlayerEventListener = new TvPlayerEventListener();
    }

    public AtvPlayerEventListener getmAtvPlayerEventListener() {
        return mAtvPlayerEventListener;
    }

    public DtvPlayerEventListener getmDtvPlayerEventListener() {
        return mDtvPlayerEventListener;
    }

    public TvPlayerEventListener getmTvPlayerEventListener() {
        return mTvPlayerEventListener;
    }

    public void setmAtvPlayerEventListener(AtvPlayerEventListener mAtvPlayerEventListener) {
        this.mAtvPlayerEventListener = mAtvPlayerEventListener;
    }

    public void setmDtvPlayerEventListener(DtvPlayerEventListener mDtvPlayerEventListener) {
        this.mDtvPlayerEventListener = mDtvPlayerEventListener;
    }

    public void setmTvPlayerEventListener(TvPlayerEventListener mTvPlayerEventListener) {
        this.mTvPlayerEventListener = mTvPlayerEventListener;
    }

    class AtvPlayerEventListener implements OnAtvPlayerEventListener {

        @Override
        public boolean onSignalLock(int arg0) {
            Log.d("lock", "AtvPlayerEventListener: onSignalLock");
            mHandler.removeMessages(Constants.MSG_ATV_SIGNAL_UNLOCK);
            dismissCountDown();
            mHandler.sendEmptyMessage(Constants.MSG_ATV_SIGNAL_LOCK);
            return true;
        }

        @Override
        public boolean onSignalUnLock(int arg0) {
            Log.d("lock", "AtvPlayerEventListener: onSignalUnLock");
            mHandler.sendEmptyMessageDelayed(Constants.MSG_ATV_SIGNAL_UNLOCK, 1000);
            startCountDown();
            return false;
        }

        @Override
        public boolean onAtvAutoTuningScanInfo(int arg0, AtvEventScan arg1) {
            return false;
        }

        @Override
        public boolean onAtvManualTuningScanInfo(int arg0, AtvEventScan arg1) {
            return false;
        }

        @Override
        public boolean onAtvProgramInfoReady(int arg0) {
            return false;
        }
    }

    class DtvPlayerEventListener implements OnDtvPlayerEventListener {

        @Override
        public boolean onAudioModeChange(int arg0, boolean arg1) {
            return false;
        }

        @Override
        public boolean onChangeTtxStatus(int arg0, boolean arg1) {
            return false;
        }

        @Override
        public boolean onCiLoadCredentialFail(int arg0) {
            return false;
        }

        @Override
        public boolean onDtvAutoTuningScanInfo(int arg0, DtvEventScan arg1) {
            return false;
        }

        @Override
        public boolean onDtvAutoUpdateScan(int arg0) {
            return false;
        }

        @Override
        public boolean onDtvChannelNameReady(int arg0) {
            return false;
        }

        @Override
        public boolean onDtvPriComponentMissing(int arg0) {
            return false;
        }

        @Override
        public boolean onDtvProgramInfoReady(int arg0) {
            return false;
        }

        @Override
        public boolean onEpgTimerSimulcast(int arg0, int arg1) {
            return false;
        }

        @Override
        public boolean onGingaStatusMode(int arg0, boolean arg1) {
            return false;
        }

        @Override
        public boolean onHbbtvStatusMode(int arg0, boolean arg1) {
            return false;
        }

        @Override
        public boolean onMheg5EventHandler(int arg0, int arg1) {
            return false;
        }

        @Override
        public boolean onMheg5ReturnKey(int arg0, int arg1) {
            return false;
        }

        @Override
        public boolean onMheg5StatusMode(int arg0, int arg1) {
            return false;
        }

        @Override
        public boolean onOadDownload(int arg0, int arg1) {
            return false;
        }

        @Override
        public boolean onOadHandler(int arg0, int arg1, int arg2) {
            return false;
        }

        @Override
        public boolean onOadTimeout(int arg0, int arg1) {
            return false;
        }

        @Override
        public boolean onPopupScanDialogFrequencyChange(int arg0) {
            return false;
        }

        @Override
        public boolean onPopupScanDialogLossSignal(int arg0) {
            return false;
        }

        @Override
        public boolean onPopupScanDialogNewMultiplex(int arg0) {
            return false;
        }

        @Override
        public boolean onRctPresence(int arg0) {
            return false;
        }

        @Override
        public boolean onSignalLock(int arg0) {
            Log.d("lock", "DtvPlayerEventListener: onSignalLock");
            dismissCountDown();
            mHolder.dismissSignalView();
            return true;
        }

        @Override
        public boolean onSignalUnLock(int arg0) {
            Log.d("lock", "DtvPlayerEventListener: onSignalUnLock");
            if (ChannelManagerExt.getInstance().getChannels().size() <= 0) {
                mHolder.setSignalText(mContext.getResources().getString(R.string.tuningtip));
            } else {
                mHolder.setSignalText("DTV " + mContext.getResources().getString(R.string.nosignaltips));
            }
            startCountDown();
            return true;
        }

        @Override
        public boolean onTsChange(int arg0) {
            return false;
        }

        @Override
        public boolean onUiOPExitServiceList(int arg0) {
            return false;
        }

        @Override
        public boolean onUiOPRefreshQuery(int arg0) {
            return false;
        }

        @Override
        public boolean onUiOPServiceList(int arg0) {
            return false;
        }
    }

    class TvPlayerEventListener implements OnTvPlayerEventListener {

        private int mSource;

        public TvPlayerEventListener() {
            mSource = getCurInputSource();
        }

        @Override
        public boolean on4k2kHDMIDisableDualView(int arg0, int arg1, int arg2) {
            return false;
        }

        @Override
        public boolean on4k2kHDMIDisablePip(int arg0, int arg1, int arg2) {
            return false;
        }

        @Override
        public boolean on4k2kHDMIDisablePop(int arg0, int arg1, int arg2) {
            return false;
        }

        @Override
        public boolean on4k2kHDMIDisableTravelingMode(int arg0, int arg1, int arg2) {
            return false;
        }

        @Override
        public boolean onEpgUpdateList(int arg0, int arg1) {
            return false;
        }

        @Override
        public boolean onHbbtvUiEvent(int arg0, HbbtvEventInfo arg1) {
            return false;
        }

        @Override
        public boolean onPopupDialog(int arg0, int arg1, int arg2) {
            return false;
        }

        @Override
        public boolean onPvrNotifyAlwaysTimeShiftProgramNotReady(int arg0) {
            return false;
        }

        @Override
        public boolean onPvrNotifyAlwaysTimeShiftProgramReady(int arg0) {
            return false;
        }

        @Override
        public boolean onPvrNotifyCiPlusProtection(int arg0) {
            return false;
        }

        @Override
        public boolean onPvrNotifyCiPlusRetentionLimitUpdate(int arg0, int arg1) {
            return false;
        }

        @Override
        public boolean onPvrNotifyOverRun(int arg0) {
            return false;
        }

        @Override
        public boolean onPvrNotifyParentalControl(int arg0, int arg1) {
            return false;
        }

        @Override
        public boolean onPvrNotifyPlaybackBegin(int arg0) {
            return false;
        }

        @Override
        public boolean onPvrNotifyPlaybackSpeedChange(int arg0) {
            return false;
        }

        @Override
        public boolean onPvrNotifyPlaybackStop(int arg0) {
            return false;
        }

        @Override
        public boolean onPvrNotifyPlaybackTime(int arg0, int arg1) {
            return false;
        }

        @Override
        public boolean onPvrNotifyRecordSize(int arg0, int arg1) {
            return false;
        }

        @Override
        public boolean onPvrNotifyRecordStop(int arg0) {
            return false;
        }

        @Override
        public boolean onPvrNotifyRecordTime(int arg0, int arg1) {
            return false;
        }

        @Override
        public boolean onPvrNotifyTimeShiftOverwritesAfter(int arg0, int arg1) {
            return false;
        }

        @Override
        public boolean onPvrNotifyTimeShiftOverwritesBefore(int arg0, int arg1) {
            return false;
        }

        @Override
        public boolean onPvrNotifyUsbRemoved(int arg0, int arg1) {
            Toast.makeText(mContext, R.string.pvrrecordusbremove, Toast.LENGTH_LONG).show();
            if (PVRActivity.mIsPVRActivityActive) {
                Intent intent = new Intent(mContext, PVRActivity.class);
                intent.putExtra("PVR_ONE_TOUCH_MODE", Constants.PVR_DISMISS_FLAG);
                mContext.startActivity(intent);
            } else {
                final TvPvrManager pvr = TvPvrManager.getInstance();
                if (pvr != null) {
                    pvr.stopPvr();
                    if (pvr.getIsBootByRecord()) {
                        pvr.setIsBootByRecord(false);
                        TvCommonManager.getInstance().standbySystem("pvr");
                    }
                }
            }
            return true;
        }

        @Override
        public boolean onScreenSaverMode(int what, int arg1) {
            switch (mSource) {
                case TvCommonManager.INPUT_SOURCE_DTV:
                    if (arg1 == EnumScreenMode.MSRV_DTV_SS_INVALID_SERVICE.ordinal()) {
                    } else if (arg1 == EnumScreenMode.MSRV_DTV_SS_NO_CI_MODULE.ordinal()) {
                    } else if (arg1 == EnumScreenMode.MSRV_DTV_SS_SCRAMBLED_PROGRAM.ordinal()) {
                    } else if (arg1 == EnumScreenMode.MSRV_DTV_SS_CH_BLOCK.ordinal()) {
                        mHolder.setSignalText(mContext.getResources().getString(R.string.passwordtip));
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
                        mHandler.sendEmptyMessageDelayed(Constants.SHOWINFO, delayTime);
                    }
                    break;
                case TvCommonManager.INPUT_SOURCE_VGA:
                    if (arg1 == EnumSignalProgSyncStatus.E_SIGNALPROC_STABLE_UN_SUPPORT_MODE.ordinal()) {
                        mHolder.setSignalText(mContext.getResources().getString(R.string.unsupporttips));
                    } else if (arg1 == EnumSignalProgSyncStatus.E_SIGNALPROC_STABLE_SUPPORT_MODE.ordinal()) {
                        // show info view when mode change
                        if (mHolder.getmSignalTipView().isShow()) {
                            mHolder.getmSignalTipView().dismiss();
                        } else {
                            ((PlayerActivity) mContext).showInfoView();
                        }
                    } else if (arg1 == EnumSignalProgSyncStatus.E_SIGNALPROC_AUTO_ADJUST.ordinal()) {
                        mHolder.setSignalText(mContext.getResources().getString(R.string.autoadjust));
                    }
                    break;

                default:
                    break;
            }
            return true;
        }

        @Override
        public boolean onSignalLock(int arg0) {
            mSource = getCurInputSource();// warning: ensure the right mSource
                                          // value
            Log.v("lock", "TvPlayerEventListener:onSignalLock");
            dismissCountDown();
            if (getCurInputSource() != TvCommonManager.INPUT_SOURCE_VGA) {
                mHolder.dismissSignalView();
            }
            return false;
        }

        @Override
        public boolean onSignalUnLock(int arg0) {
            Log.v("lock", "TvPlayerEventListener:onSignalUnLock");
            mHolder.setNosignalTips(getCurInputSource());
            startCountDown();
            return false;
        }

        @Override
        public boolean onTvProgramInfoReady(int arg0) {
            return false;
        }

        @Override
        public boolean onDtvChannelInfoUpdate(int arg0, int arg1, int arg2) {
            return false;
        }

        @Override
        public boolean onDtvPsipTsUpdate(int arg0, int arg1, int arg2) {
            return false;
        }

        @Override
        public boolean onEmerencyAlert(int arg0, int arg1, int arg2) {
            return false;
        }

        private int getCurInputSource() {
            return TvCommonManager.getInstance().getCurrentTvInputSource();
        }
    }

    /**
     * finish countDownActivity
     */
    private void dismissCountDown() {
        if (UtilsTools.getCurTopActivityName(mContext).equals(Constants.COUNTDOWNACTIVITY)) {
            Intent intent = new Intent(Constants.START_COUNTERDOWN);
            intent.putExtra("countDown", Constants.DISMISSCOUTDOWN);
            mContext.startActivity(intent);
        }
        mHandler.removeMessages(Constants.STANDBY);
    }

    /**
     * delay start countDownActivity
     */
    private void startCountDown() {
        if (Settings.System.getInt(mContext.getContentResolver(), "savemode", 0) != 0) {
            mHandler.removeMessages(Constants.STANDBY);
            mHandler.sendEmptyMessageDelayed(Constants.STANDBY, Constants.AUTO_SLEEP_DELAY);
        }
    }
}
