
package com.eostek.tv.player;

import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.eostek.tv.player.pvr.PVRActivity;
import com.eostek.tv.player.util.Constants.EnumScreenMode;
import com.eostek.tv.player.util.Constants.EnumSignalProgSyncStatus;
import com.eostek.tv.player.util.ChannelManagerExt;
import com.eostek.tv.player.util.Constants;
import com.eostek.tv.player.util.SignalTipView;
import com.mstar.android.tv.TvCommonManager;
import com.mstar.android.tvapi.common.listener.OnTvPlayerEventListener;
import com.mstar.android.tvapi.common.vo.HbbtvEventInfo;
import com.mstar.android.tvapi.common.vo.TvOsType.EnumInputSource;
import com.mstar.android.tv.TvPvrManager;

public class TvPlayerEventListener implements OnTvPlayerEventListener {
    private final static String TAG = TvPlayerEventListener.class.getSimpleName();

    private PlayerActivity mContext;

    private SignalTipView mSignalTipView;

    private EnumInputSource mSource;
    
    private Handler handler;
    
    public TvPlayerEventListener(Context context, Handler handler) {
        mContext = (PlayerActivity) context;
        mSource = getCurInputSource();
        this.handler = handler;
    }
    
    public void fleshInputSource() {
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
//        Toast.makeText(mContext, R.string.pvrrecordusbremove, Toast.LENGTH_LONG).show();
//        if (PVRActivity.isPVRActivityActive) {
//            Intent intent = new Intent(mContext, PVRActivity.class);
//            intent.putExtra("PVR_ONE_TOUCH_MODE", 4);
//            mContext.startActivity(intent);
//        } else {
//            final TvPvrManager pvr = TvPvrManager.getInstance();
//            if (pvr != null) {
//                pvr.stopPvr();
//                if (pvr.getIsBootByRecord()) {
//                    pvr.setIsBootByRecord(false);
//                    TvCommonManager.getInstance().standbySystem("pvr");
//                }
//            }
//        }
        return true;
    }

    @Override
    public boolean onScreenSaverMode(int what, int arg1) {
        switch (mSource) {
            case E_INPUT_SOURCE_DTV:
                if (arg1 == EnumScreenMode.MSRV_DTV_SS_INVALID_SERVICE.ordinal()) {
//                    mSignalTipView.setText(mContext.getResources().getString(R.string.invalidservice));
                } else if (arg1 == EnumScreenMode.MSRV_DTV_SS_NO_CI_MODULE.ordinal()) {
                } else if (arg1 == EnumScreenMode.MSRV_DTV_SS_SCRAMBLED_PROGRAM.ordinal()) {
                } else if (arg1 == EnumScreenMode.MSRV_DTV_SS_CH_BLOCK.ordinal()) {
                    handler.sendEmptyMessage(PlayerActivity.INPUT_PASSWORD);
                } else if (arg1 == EnumScreenMode.MSRV_DTV_SS_PARENTAL_BLOCK.ordinal()) {
                } else if (arg1 == EnumScreenMode.MSRV_DTV_SS_AUDIO_ONLY.ordinal()) {
                } else if (arg1 == EnumScreenMode.MSRV_DTV_SS_DATA_ONLY.ordinal()) {
                } else if (arg1 == EnumScreenMode.MSRV_DTV_SS_COMMON_VIDEO.ordinal()) {
//                    handler.sendEmptyMessage(PlayerActivity.DISMISS);
                }
                break;
            case E_INPUT_SOURCE_ATV:
                break;
            case E_INPUT_SOURCE_HDMI:
            case E_INPUT_SOURCE_HDMI2:
            case E_INPUT_SOURCE_HDMI3:
            case E_INPUT_SOURCE_HDMI4:
                Log.v(TAG, "arg1:" + arg1);
                if (arg1 == EnumSignalProgSyncStatus.E_SIGNALPROC_STABLE_SUPPORT_MODE.ordinal()) {
                    handler.sendEmptyMessageDelayed(PlayerActivity.SHOW_LEFT_INFO, 100);
                }
                break;
            case E_INPUT_SOURCE_VGA:
                if (arg1 == EnumSignalProgSyncStatus.E_SIGNALPROC_STABLE_UN_SUPPORT_MODE.ordinal()) {
                    handler.sendEmptyMessage(PlayerActivity.UNSUPPORT_TIPS);
                } else if (arg1 == EnumSignalProgSyncStatus.E_SIGNALPROC_STABLE_SUPPORT_MODE.ordinal()) {
                    handler.sendEmptyMessage(PlayerActivity.DISMISS_SIGNAL_TIPS);
                    handler.sendEmptyMessageDelayed(PlayerActivity.SHOW_LEFT_INFO, 100);
                } else if (arg1 == EnumSignalProgSyncStatus.E_SIGNALPROC_AUTO_ADJUST.ordinal()) {
                    handler.sendEmptyMessage(PlayerActivity.AUDO_ADJUST);
                }
                break;

            default:
                break;
        }
        return true;
    }

    @Override
    public boolean onSignalLock(int arg0) {
        Log.v(TAG, "TvPlayerEventListener:onSignalLock");
        mSource = getCurInputSource();
        switch (mSource) {
            case E_INPUT_SOURCE_ATV:
            case E_INPUT_SOURCE_DTV:
                return true;
            case E_INPUT_SOURCE_YPBPR:
            case E_INPUT_SOURCE_CVBS:
                handler.sendEmptyMessageDelayed(PlayerActivity.SHOW_LEFT_INFO, 100);
            case E_INPUT_SOURCE_HDMI:
            case E_INPUT_SOURCE_HDMI2:
            case E_INPUT_SOURCE_HDMI3:
            case E_INPUT_SOURCE_HDMI4:
                handler.sendEmptyMessage(PlayerActivity.DISMISS);
                break;
            default:
                break;
        }
        Intent intentStandby = new Intent(Constants.CANCELSTANDBY);
        mContext.sendBroadcast(intentStandby);
        return false;
    }

    @Override
    public boolean onSignalUnLock(int arg0) {
        Log.v(TAG, "TvPlayerEventListener:onSignalUnLock");
        handler.sendEmptyMessage(PlayerActivity.UNSIGNAL);
        Intent intentStandby = new Intent(Constants.STARTSTANDBY);
        mContext.sendBroadcast(intentStandby);
        return false;
    }

    @Override
    public boolean onTvProgramInfoReady(int arg0) {
        return false;
    }

    @Override
    public boolean onDtvChannelInfoUpdate(int arg0, int arg1, int arg2) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean onDtvPsipTsUpdate(int arg0, int arg1, int arg2) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean onEmerencyAlert(int arg0, int arg1, int arg2) {
        // TODO Auto-generated method stub
        return false;
    }
    
    private EnumInputSource getCurInputSource() {
        return TvCommonManager.getInstance().getCurrentInputSource();
    }

}
