
package com.heran.launcher.util;

import com.heran.launcher.LauncherActivity;
import com.heran.launcher.R;
import com.mstar.android.tv.TvChannelManager;
import com.mstar.android.tvapi.atv.listener.OnAtvPlayerEventListener;
import com.mstar.android.tvapi.atv.vo.AtvEventScan;
import com.mstar.android.tvapi.common.TvManager;
import com.mstar.android.tvapi.common.exception.TvCommonException;
import com.mstar.android.tvapi.common.listener.OnTvPlayerEventListener;
import com.mstar.android.tvapi.common.vo.HbbtvEventInfo;
import com.mstar.android.tvapi.common.vo.TvOsType.EnumInputSource;
import com.mstar.android.tvapi.dtv.listener.OnDtvPlayerEventListener;
import com.mstar.android.tvapi.dtv.vo.DtvEventScan;

import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

@SuppressWarnings("deprecation")
public class ScreenProtection {

    private static final String TAG = "ScreenProtection";

    private static ScreenProtection sInstance = new ScreenProtection();

    public static ScreenProtection getInstance() {
        return sInstance;
    }

    private ScreenProtection() {
    }

    private TextView mSignalTipView;

    /**
     * monitor HDMI1, HDMI2, HDMI3, AV, YPbPR, VGA
     */

    private TvPlayerEventListener mTvPlayerEventListener = null;

    private AtvPlayerEventListener mAtvPlayerEventListener = null;

    private DtvPlayerEventListener mDtvPlayerEventListener = null;

    private TvChannelManager tvChannelManager = TvChannelManager.getInstance();

    private LauncherActivity mContext;

    private String tips = "";

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Constants.DELAY_SHOW_SIGNAL_MSG:
                    @SuppressWarnings("deprecation")
                    EnumInputSource currentInputSource = EnumInputSource.E_INPUT_SOURCE_NONE;
                    try {
                        if (TvManager.getInstance() != null) {
                            currentInputSource = TvManager.getInstance().getCurrentInputSource();
                        }
                    } catch (TvCommonException e) {
                        e.printStackTrace();
                    }
                    setSignalTipView(currentInputSource);
                    mSignalTipView.setVisibility(View.VISIBLE);
                    break;
                case Constants.SHOW_SIGNAL_TIPS:
                    @SuppressWarnings("deprecation")
                    EnumInputSource curInputSource = EnumInputSource.E_INPUT_SOURCE_NONE;
                    try {
                        if (TvManager.getInstance() != null) {
                            curInputSource = TvManager.getInstance().getCurrentInputSource();
                        }
                    } catch (TvCommonException e) {
                        e.printStackTrace();
                    }
                    setSignalTipView(curInputSource);
                    // Resources res = mContext.getResources();
                    // ProgramInfo pinfo = mContext.getCurrProgramInfo();
                    // if (pinfo.number == 0) {
                    // mSignalTipView.setText(res.getString(R.string.tuningtip));
                    // }
                    mSignalTipView.setVisibility(View.VISIBLE);
                    break;
                case Constants.DISMISS_SIGNAL_TIPS:
                    if (mSignalTipView != null) {
                        mSignalTipView.setText("");
                        tips = "";
                        mSignalTipView.setVisibility(View.GONE);
                    }
                    break;
                case Constants.SHOW_LOCKED_TIPS:
                    mSignalTipView.setText(mContext.getResources().getString(R.string.locked));
                    mSignalTipView.setVisibility(View.VISIBLE);
                    break;
                default:
                    break;
            }
        }

    };

    @SuppressWarnings("deprecation")
    public void start(LauncherActivity context, TextView radioimgView) {
        this.mSignalTipView = radioimgView;
        this.mContext = context;

        TvManager.getInstance().getTimerManager().setOnTimerEventListener(TimerEventListener.getInstance(context));
        mTvPlayerEventListener = new TvPlayerEventListener();
        mAtvPlayerEventListener = new AtvPlayerEventListener();
        mDtvPlayerEventListener = new DtvPlayerEventListener();

        tvChannelManager.registerOnTvPlayerEventListener(mTvPlayerEventListener);
        tvChannelManager.registerOnAtvPlayerEventListener(mAtvPlayerEventListener);
        tvChannelManager.registerOnDtvPlayerEventListener(mDtvPlayerEventListener);

        if (!TvChannelManager.getInstance().isSignalStabled()) {
            mHandler.sendEmptyMessage(Constants.SHOW_SIGNAL_TIPS);
        }

        Log.d(TAG, "ScreenProtection start !");

    }

    @SuppressWarnings("deprecation")
    public void stop() {
        if (mSignalTipView != null) {
            mSignalTipView.setText("");
            mSignalTipView.setVisibility(View.GONE);
        }
        if (tvChannelManager != null) {
            tvChannelManager = TvChannelManager.getInstance();
        }
        tvChannelManager.unregisterOnTvPlayerEventListener(mTvPlayerEventListener);
        mTvPlayerEventListener = null;
        tvChannelManager.unregisterOnAtvPlayerEventListener(mAtvPlayerEventListener);
        mAtvPlayerEventListener = null;
        tvChannelManager.unregisterOnDtvPlayerEventListener(mDtvPlayerEventListener);
        mDtvPlayerEventListener = null;
        TvManager.getInstance().setOnTvEventListener(null);
        TvManager.getInstance().getTimerManager().setOnTimerEventListener(null);
    }

    @SuppressWarnings("deprecation")
    public void setSignalTipView(EnumInputSource currentInputSource) {
        Log.v(TAG, "current input source:" + currentInputSource.toString());
        String curSourceName = "";
        String noSignalTips = mContext.getResources().getString(R.string.nosignaltips);
        switch (currentInputSource) {
            case E_INPUT_SOURCE_ATV:
                curSourceName = "ATV ";
                break;
            case E_INPUT_SOURCE_DTV:
                curSourceName = "DTV ";
                break;
            case E_INPUT_SOURCE_HDMI:
                curSourceName = "HDMI1 ";
                break;
            case E_INPUT_SOURCE_HDMI2:
                curSourceName = "HDMI2 ";
                break;
            case E_INPUT_SOURCE_HDMI3:
                curSourceName = "HDMI3 ";
                break;
            case E_INPUT_SOURCE_CVBS:
                curSourceName = "AV ";
                break;
            case E_INPUT_SOURCE_YPBPR:
                curSourceName = "YPbPr ";
                break;
            case E_INPUT_SOURCE_VGA:
                curSourceName = "VGA ";
                break;
            default:
                break;
        }
        if (EnumInputSource.E_INPUT_SOURCE_ATV == currentInputSource) {
            mSignalTipView.setText(" ");
        } else {
            mSignalTipView.setText(curSourceName + noSignalTips);
        }
        tips = curSourceName + noSignalTips;
    }

    private class TvPlayerEventListener implements OnTvPlayerEventListener {

        EnumInputSource mSource;

        public TvPlayerEventListener() {
            try {
                if (TvManager.getInstance() != null) {
                    mSource = TvManager.getInstance().getCurrentInputSource();
                }
            } catch (TvCommonException e) {
                e.printStackTrace();
            }
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
        public boolean onPvrNotifyUsbRemoved(int what, int arg1) {
            return false;
        }

        @Override
        public boolean onScreenSaverMode(int arg0, int arg1) {
            switch (mSource) {
                case E_INPUT_SOURCE_DTV:
                    if (arg1 == EnumScreenMode.MSRV_DTV_SS_CH_BLOCK.ordinal()) {
                        mHandler.sendEmptyMessage(Constants.SHOW_LOCKED_TIPS);
                    }
                    break;

                default:
                    break;
            }
            // switch (mSource) {
            // case E_INPUT_SOURCE_DTV:
            // if (arg1 == EnumScreenMode.MSRV_DTV_SS_INVALID_SERVICE.ordinal())
            // {
            // } else if (arg1 ==
            // EnumScreenMode.MSRV_DTV_SS_NO_CI_MODULE.ordinal()) {
            // } else if (arg1 ==
            // EnumScreenMode.MSRV_DTV_SS_SCRAMBLED_PROGRAM.ordinal()) {
            // } else if (arg1 == EnumScreenMode.MSRV_DTV_SS_CH_BLOCK.ordinal())
            // {
            // mSignalTipView.setText(mContext.getResources().getString(R.string.passwordtip));
            // mSignalTipView.setVisibility(View.VISIBLE);
            // } else if (arg1 ==
            // EnumScreenMode.MSRV_DTV_SS_PARENTAL_BLOCK.ordinal()) {
            // } else if (arg1 ==
            // EnumScreenMode.MSRV_DTV_SS_AUDIO_ONLY.ordinal()) {
            // } else if (arg1 ==
            // EnumScreenMode.MSRV_DTV_SS_DATA_ONLY.ordinal()) {
            // } else if (arg1 ==
            // EnumScreenMode.MSRV_DTV_SS_COMMON_VIDEO.ordinal()) {
            // mSignalTipView.setText("");
            // mSignalTipView.setVisibility(View.GONE);
            // }
            // break;
            // case E_INPUT_SOURCE_ATV:
            // break;
            // case E_INPUT_SOURCE_HDMI:
            // case E_INPUT_SOURCE_HDMI2:
            // case E_INPUT_SOURCE_HDMI3:
            // case E_INPUT_SOURCE_HDMI4:
            // if (arg1 ==
            // EnumSignalProgSyncStatus.E_SIGNALPROC_STABLE_UN_SUPPORT_MODE.ordinal())
            // {
            // } else if (arg1 ==
            // EnumSignalProgSyncStatus.E_SIGNALPROC_STABLE_SUPPORT_MODE.ordinal())
            // {
            // }
            // break;
            // case E_INPUT_SOURCE_VGA:
            // if (arg1 ==
            // EnumSignalProgSyncStatus.E_SIGNALPROC_STABLE_UN_SUPPORT_MODE.ordinal())
            // {
            // } else if (arg1 ==
            // EnumSignalProgSyncStatus.E_SIGNALPROC_STABLE_SUPPORT_MODE.ordinal())
            // {
            // } else if (arg1 ==
            // EnumSignalProgSyncStatus.E_SIGNALPROC_AUTO_ADJUST.ordinal()) {
            // }
            // break;
            // default:
            // break;
            // }
            return false;
        }

        @Override
        public boolean onSignalLock(int arg0) {
            Log.v(TAG, "OnTvPlayerEventListener:onSignalLock");
            try {
                if (TvManager.getInstance() != null) {
                    mSource = TvManager.getInstance().getCurrentInputSource();
                }
            } catch (TvCommonException e) {
                e.printStackTrace();
            }
            if ("".equals(mSignalTipView.getText().toString())) {
                return true;
            }
            mHandler.sendEmptyMessage(Constants.DISMISS_SIGNAL_TIPS);
            return true;
        }

        @Override
        public boolean onSignalUnLock(int arg0) {
            Log.d(TAG, "OnTvPlayerEventListener:onSignalUnLock");
            mHandler.sendEmptyMessage(Constants.SHOW_SIGNAL_TIPS);
            return true;
        }

        @Override
        public boolean onTvProgramInfoReady(int arg0) {
            return false;
        }

        @Override
        public boolean on4k2kHDMIDisableDualView(int i, int j, int k) {
            return false;
        }

        @Override
        public boolean on4k2kHDMIDisablePip(int i, int j, int k) {
            return false;
        }

        @Override
        public boolean on4k2kHDMIDisablePop(int i, int j, int k) {
            return false;
        }

        @Override
        public boolean on4k2kHDMIDisableTravelingMode(int i, int j, int k) {
            return false;
        }

        public boolean onDtvChannelInfoUpdate(int i, int j, int k) {
            return false;
        }

        public boolean onDtvPsipTsUpdate(int i, int j, int k) {
            return false;
        }

        public boolean onEmerencyAlert(int i, int j, int k) {
            return false;
        }

        @Override
        public boolean onEpgUpdateList(int i, int j) {
            return false;
        }

        @Override
        public boolean onHbbtvUiEvent(int i, HbbtvEventInfo hbbtveventinfo) {
            return false;
        }

    }

    private class AtvPlayerEventListener implements OnAtvPlayerEventListener {

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

        @Override
        public boolean onSignalLock(int arg0) {
            Log.v(TAG, "OnAtvPlayerEventListener:onSignalLock");
            if ("".equals(mSignalTipView.getText().toString())) {
                return true;
            }
            mHandler.sendEmptyMessage(Constants.DISMISS_SIGNAL_TIPS);
            return true;
        }

        @Override
        public boolean onSignalUnLock(int arg0) {
            Log.v(TAG, "OnAtvPlayerEventListener:onSignalUnLock");
            mHandler.sendEmptyMessage(Constants.SHOW_SIGNAL_TIPS);
            return true;
        }

    }

    private class DtvPlayerEventListener implements OnDtvPlayerEventListener {

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
            Log.v(TAG, "OnDtvPlayerEventListener:onSignalLock");
            if ("".equals(mSignalTipView.getText().toString())) {
                return true;
            }
            mHandler.sendEmptyMessage(Constants.DISMISS_SIGNAL_TIPS);
            return true;
        }

        @Override
        public boolean onSignalUnLock(int arg0) {
            Log.v(TAG, "OnDtvPlayerEventListener:onSignalUnLock");
            mHandler.sendEmptyMessage(Constants.SHOW_SIGNAL_TIPS);
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

    public enum EnumScreenMode {
        // / The screen saver mode is invalid service.
        MSRV_DTV_SS_INVALID_SERVICE,
        // / The screen saver mode is no CI module.
        MSRV_DTV_SS_NO_CI_MODULE,
        // / The screen saver mode is CI+ Authentication.
        MSRV_DTV_SS_CI_PLUS_AUTHENTICATION,
        // / The screen saver mode is scrambled program.
        MSRV_DTV_SS_SCRAMBLED_PROGRAM,
        // / The screen saver mode is channel block.
        MSRV_DTV_SS_CH_BLOCK,
        // / The screen saver mode is parental block.
        MSRV_DTV_SS_PARENTAL_BLOCK,
        // / The screen saver mode is audio only.
        MSRV_DTV_SS_AUDIO_ONLY,
        // / The screen saver mode is data only.
        MSRV_DTV_SS_DATA_ONLY,
        // / The screen saver mode is common video.
        MSRV_DTV_SS_COMMON_VIDEO,
        // / The screen saver mode is Unsupported Format.
        MSRV_DTV_SS_UNSUPPORTED_FORMAT,
        // / The screen saver mode is invalid pmt.
        MSRV_DTV_SS_INVALID_PMT,
        // / The screen saver mode support type.
        MSRV_DTV_SS_MAX,

        MSRV_DTV_SS_CA_NOTIFY
    };

    public enum EnumSignalProgSyncStatus {
        // /< Input timing stable, no input sync detected
        E_SIGNALPROC_NOSYNC,
        // /< Input timing stable, has stable input sync and support this timing
        E_SIGNALPROC_STABLE_SUPPORT_MODE,
        // /< Input timing stable, has stable input sync but this timing is not
        // supported
        E_SIGNALPROC_STABLE_UN_SUPPORT_MODE,
        // /< Timing change, has to wait InfoFrame if HDMI input
        E_SIGNALPROC_UNSTABLE,
        // /< Timing change, has to auto adjust if PCRGB input
        E_SIGNALPROC_AUTO_ADJUST,
    };

}
