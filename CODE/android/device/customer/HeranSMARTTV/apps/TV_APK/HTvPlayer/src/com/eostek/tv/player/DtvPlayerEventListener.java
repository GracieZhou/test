
package com.eostek.tv.player;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.eostek.tv.player.util.ChannelManagerExt;
import com.eostek.tv.player.util.Constants;
import com.eostek.tv.player.util.SignalTipView;
import com.mstar.android.tvapi.dtv.listener.OnDtvPlayerEventListener;
import com.mstar.android.tvapi.dtv.vo.DtvEventScan;

public class DtvPlayerEventListener implements OnDtvPlayerEventListener {

    private Context mContext;

    private SignalTipView mSignalTipView;

    public DtvPlayerEventListener(Context context, SignalTipView view) {
        mContext = context;
        mSignalTipView = view;
    }

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
        mSignalTipView.dismiss();
        Intent intentStandby = new Intent(Constants.CANCELSTANDBY);
        mContext.sendBroadcast(intentStandby);
        return true;
    }

    @Override
    public boolean onSignalUnLock(int arg0) {
        Log.d("lock", "DtvPlayerEventListener: onSignalUnLock");
        if (ChannelManagerExt.getInstance().getChannels().size() <= 0) {
            mSignalTipView.setText(mContext.getResources().getString(R.string.tuningtip));
        } else {
            mSignalTipView.setText("DTV " + mContext.getResources().getString(R.string.nosignaltips));
        }
        Intent intentStandby = new Intent(Constants.STARTSTANDBY);
        mContext.sendBroadcast(intentStandby);
        return true;
    }

    @Override
    public boolean onTsChange(int arg0) {
        return false;
    }

    @Override
    public boolean onUiOPExitServiceList(int arg0) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean onUiOPRefreshQuery(int arg0) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean onUiOPServiceList(int arg0) {
        // TODO Auto-generated method stub
        return false;
    }
}
