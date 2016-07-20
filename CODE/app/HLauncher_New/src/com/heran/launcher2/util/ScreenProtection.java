
package com.heran.launcher2.util;

import android.content.res.Resources;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.heran.launcher2.R;
import com.heran.launcher2.HomeActivity;
import com.mstar.android.tvapi.common.TvManager;
import com.mstar.android.tvapi.common.exception.TvCommonException;
import com.mstar.android.tvapi.common.listener.OnTvEventListener;
import com.mstar.android.tvapi.common.listener.OnTvPlayerEventListener;
import com.mstar.android.tvapi.common.vo.HbbtvEventInfo;
import com.mstar.android.tvapi.common.vo.ProgramInfo;
import com.mstar.android.tvapi.common.vo.TvOsType.EnumInputSource;

public class ScreenProtection {

    private static ScreenProtection sInstance = new ScreenProtection();

    private boolean mIsStarted = false;

    private String mSignalTxt = "";

    public static ScreenProtection getInstance() {
        return sInstance;
    }

    private ScreenProtection() {
    }

    public boolean ismIsStarted() {
        return mIsStarted;
    }

    private TextView mSignalTipView;

    private MyOnTvEventListener mListener;

    private MyTvPlayerEventListener mTvPlayerEventListener;

    private HomeActivity mContext;

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == Constants.DELAY_SHOW_SIGNAL_MSG) {
                EnumInputSource currentInputSource = EnumInputSource.E_INPUT_SOURCE_NONE;
                try {
                    if (TvManager.getInstance() != null) {
                        currentInputSource = TvManager.getInstance().getCurrentInputSource();
                    }
                } catch (TvCommonException e) {
                    e.printStackTrace();
                }
                setSignalTipView(currentInputSource);
                ScreenProtection.this.mSignalTipView.setVisibility(View.VISIBLE);
            }
        }

    };

    public void start(HomeActivity context, TextView radioimgView) {
        this.mSignalTipView = radioimgView;
        this.mContext = context;
        mListener = new MyOnTvEventListener();

        if (!mIsStarted) {
            TvManager.getInstance().getTimerManager().setOnTimerEventListener(TimerEventListener.getInstance(context));
            TvManager.getInstance().setOnTvEventListener(mListener);
            EnumInputSource curSource = null;
            try {
                curSource = EnumInputSource.values()[mContext.queryCurInputSrc()];
            } catch (ArrayIndexOutOfBoundsException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
            mTvPlayerEventListener = new MyTvPlayerEventListener(curSource);
            TvManager.getInstance().getPlayerManager().setOnTvPlayerEventListener(mTvPlayerEventListener);
            Log.d("ScreenProtection", "this.mSignalTxt:"+this.mSignalTxt);
            if (!this.mSignalTxt.equals("")) {
                mHandler.sendEmptyMessageDelayed(Constants.DELAY_SHOW_SIGNAL_MSG, Constants.NDELAY_SHOW_SIGNAL_TIME);
            }
            
            Log.d("ScreenProtection", "ScreenProtection start !");
        }

        this.mIsStarted = true;
    }

    public void stop() {
        this.mIsStarted = false;
        if(ScreenProtection.this.mSignalTipView !=null){
            ScreenProtection.this.mSignalTipView.setText("");
            ScreenProtection.this.mSignalTipView.setVisibility(View.GONE);
        }
        TvManager.getInstance().setOnTvEventListener(null);
        TvManager.getInstance().getTimerManager().setOnTimerEventListener(null);
    }

    private void setSignalTipView(EnumInputSource currentInputSource) {
        switch (currentInputSource) {
            case E_INPUT_SOURCE_DTV:
                ScreenProtection.this.mSignalTipView.setText("DTV"
                        + mContext.getResources().getString(R.string.nosignaltips));
                this.mSignalTxt = "DTV" + mContext.getResources().getString(R.string.nosignaltips);
                break;
            case E_INPUT_SOURCE_ATV:
                ScreenProtection.this.mSignalTipView.setText("ATV"
                        + mContext.getResources().getString(R.string.nosignaltips));
                this.mSignalTxt = "ATV" + mContext.getResources().getString(R.string.nosignaltips);
                break;
            case E_INPUT_SOURCE_HDMI:
                ScreenProtection.this.mSignalTipView.setText("HDMI"
                        + mContext.getResources().getString(R.string.nosignaltips));
                this.mSignalTxt = "HDMI" + mContext.getResources().getString(R.string.nosignaltips);
                break;
            case E_INPUT_SOURCE_HDMI4:
                ScreenProtection.this.mSignalTipView.setText("HDMI2"
                        + mContext.getResources().getString(R.string.nosignaltips));
                this.mSignalTxt = "HDMI2" + mContext.getResources().getString(R.string.nosignaltips);
                break;
            case E_INPUT_SOURCE_CVBS:
                ScreenProtection.this.mSignalTipView.setText("AV"
                        + mContext.getResources().getString(R.string.nosignaltips));
                this.mSignalTxt = "AV" + mContext.getResources().getString(R.string.nosignaltips);
                break;
            case E_INPUT_SOURCE_YPBPR:
                ScreenProtection.this.mSignalTipView.setText("YPBPR"
                        + mContext.getResources().getString(R.string.nosignaltips));
                this.mSignalTxt = "YPBPR" + mContext.getResources().getString(R.string.nosignaltips);
                break;
            case E_INPUT_SOURCE_VGA:
                ScreenProtection.this.mSignalTipView.setText("VGA"
                        + mContext.getResources().getString(R.string.nosignaltips));
                this.mSignalTxt = "VGA" + mContext.getResources().getString(R.string.nosignaltips);
                break;
            default:
                this.mSignalTxt = "";
                break;
        }
    }

    private class MyOnTvEventListener implements OnTvEventListener {

        @Override
        public boolean onDtvReadyPopupDialog(int arg0, int arg1, int arg2) {
            return false;
        }

        @Override
        public boolean onScartMuteOsdMode(int arg0) {
            return false;
        }

        @Override
        public boolean onSignalLock(int arg0) {
            if (ScreenProtection.this.mSignalTipView != null) {
                ScreenProtection.this.mSignalTipView.setText("");
                ScreenProtection.this.mSignalTipView.setVisibility(View.GONE);
            }
            ScreenProtection.this.mSignalTxt = "";
            mHandler.removeMessages(Constants.NDELAY_SHOW_SIGNAL_TIME);
            if (mSignalTipView != null) {
                Log.d("ScreenProtection", "OnTvEventListener---onSignallock start ! mSignalTipView = "
                        + mSignalTipView.getText().toString());
            }
            return true;
        }

        @Override
        public boolean onSignalUnlock(int arg0) {
            mHandler.removeMessages(Constants.NDELAY_SHOW_SIGNAL_TIME);
            EnumInputSource currentInputSource = EnumInputSource.E_INPUT_SOURCE_NONE;
            try {
                if (TvManager.getInstance() != null) {
                    currentInputSource = TvManager.getInstance().getCurrentInputSource();
                }
            } catch (TvCommonException e) {
                e.printStackTrace();
            }
            setSignalTipView(currentInputSource);
            Resources res = mContext.getResources();
            if (currentInputSource == EnumInputSource.E_INPUT_SOURCE_DTV) {
                ProgramInfo pinfo = mContext.getCurrProgramInfo();
                if (pinfo.number == 0) {
                    ScreenProtection.this.mSignalTipView.setText(res.getString(R.string.tuningtip));
                }
            }
            ScreenProtection.this.mSignalTipView.setVisibility(View.VISIBLE);
            Log.d("ScreenProtection", "OnTvEventListener----onSignalUnlock start ! " + currentInputSource + ";"
                    + mSignalTipView.getText().toString());
            return true;
        }

        @Override
        public boolean onUnityEvent(int arg0, int arg1, int arg2) {
            return false;
        }

        @Override
        public boolean on4k2kHDMIDisableDualView(int i, int j, int k) {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public boolean on4k2kHDMIDisablePip(int i, int j, int k) {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public boolean on4k2kHDMIDisablePop(int i, int j, int k) {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public boolean on4k2kHDMIDisableTravelingMode(int i, int j, int k) {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public boolean onAtscPopupDialog(int i, int j, int k) {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public boolean onDeadthEvent(int i, int j, int k) {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public boolean onScreenSaverMode(int i, int j, int k) {
            // TODO Auto-generated method stub
            return false;
        }

    }

    private class MyTvPlayerEventListener implements OnTvPlayerEventListener {

        private EnumInputSource mSource;

        public MyTvPlayerEventListener(EnumInputSource source) {
            this.mSource = source;
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
            // Log.d("ScreenProtection", "onScreenSaverMode !" + arg1 +
            // "; mSource = " + mSource);
            switch (mSource) {
                case E_INPUT_SOURCE_DTV:
                    if (arg1 == EnumScreenMode.MSRV_DTV_SS_INVALID_SERVICE.ordinal()) {
                    } else if (arg1 == EnumScreenMode.MSRV_DTV_SS_NO_CI_MODULE.ordinal()) {
                    } else if (arg1 == EnumScreenMode.MSRV_DTV_SS_SCRAMBLED_PROGRAM.ordinal()) {
                    } else if (arg1 == EnumScreenMode.MSRV_DTV_SS_CH_BLOCK.ordinal()) {
                        mSignalTipView.setText(mContext.getResources().getString(R.string.passwordtip));
                        mSignalTipView.setVisibility(View.VISIBLE);
                    } else if (arg1 == EnumScreenMode.MSRV_DTV_SS_PARENTAL_BLOCK.ordinal()) {
                    } else if (arg1 == EnumScreenMode.MSRV_DTV_SS_AUDIO_ONLY.ordinal()) {
                    } else if (arg1 == EnumScreenMode.MSRV_DTV_SS_DATA_ONLY.ordinal()) {
                    } else if (arg1 == EnumScreenMode.MSRV_DTV_SS_COMMON_VIDEO.ordinal()) {
                        mSignalTipView.setText("");
                        mSignalTipView.setVisibility(View.GONE);
                    }
                    break;
                case E_INPUT_SOURCE_ATV:
                    break;
                case E_INPUT_SOURCE_HDMI:
                case E_INPUT_SOURCE_HDMI2:
                case E_INPUT_SOURCE_HDMI3:
                case E_INPUT_SOURCE_HDMI4:
                    if (arg1 == EnumSignalProgSyncStatus.E_SIGNALPROC_STABLE_UN_SUPPORT_MODE.ordinal()) {
                    } else if (arg1 == EnumSignalProgSyncStatus.E_SIGNALPROC_STABLE_SUPPORT_MODE.ordinal()) {
                    }
                    break;
                case E_INPUT_SOURCE_VGA:
                    if (arg1 == EnumSignalProgSyncStatus.E_SIGNALPROC_STABLE_UN_SUPPORT_MODE.ordinal()) {
                    } else if (arg1 == EnumSignalProgSyncStatus.E_SIGNALPROC_STABLE_SUPPORT_MODE.ordinal()) {
                    } else if (arg1 == EnumSignalProgSyncStatus.E_SIGNALPROC_AUTO_ADJUST.ordinal()) {
                    }
                    break;
                default:
                    break;
            }
            return false;
        }

        @Override
        public boolean onSignalLock(int arg0) {
            mSignalTipView.setText(null);
            ScreenProtection.this.mSignalTipView.setVisibility(View.GONE);
            Log.d("ScreenProtection", "OnTvPlayerEventListener-----onSignalLock !" + arg0);
            return false;
        }

        @Override
        public boolean onSignalUnLock(int arg0) {
            Log.d("ScreenProtection", "onSignalUnLock !" + arg0);
            return false;
        }

        @Override
        public boolean onTvProgramInfoReady(int arg0) {
            return false;
        }

        @Override
        public boolean on4k2kHDMIDisableDualView(int i, int j, int k) {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public boolean on4k2kHDMIDisablePip(int i, int j, int k) {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public boolean on4k2kHDMIDisablePop(int i, int j, int k) {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public boolean on4k2kHDMIDisableTravelingMode(int i, int j, int k) {
            // TODO Auto-generated method stub
            return false;
        }

        public boolean onDtvChannelInfoUpdate(int i, int j, int k) {
            // TODO Auto-generated method stub
            return false;
        }

        public boolean onDtvPsipTsUpdate(int i, int j, int k) {
            // TODO Auto-generated method stub
            return false;
        }

        public boolean onEmerencyAlert(int i, int j, int k) {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public boolean onEpgUpdateList(int i, int j) {
            // TODO Auto-generated method stub
            return false;
        }

        @Override
        public boolean onHbbtvUiEvent(int i, HbbtvEventInfo hbbtveventinfo) {
            // TODO Auto-generated method stub
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
