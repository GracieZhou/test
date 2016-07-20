
package com.eostek.tv.player;

import android.content.Context;
import android.content.Intent;
import android.util.Log;

import com.eostek.tv.player.util.ChannelManagerExt;
import com.eostek.tv.player.util.Constants;
import com.eostek.tv.player.util.SignalTipView;
import com.mstar.android.tvapi.atv.listener.OnAtvPlayerEventListener;
import com.mstar.android.tvapi.atv.vo.AtvEventScan;

public class AtvPlayerEventListener implements OnAtvPlayerEventListener {

    private Context mContext;

    private SignalTipView mSignalTipView;

    public AtvPlayerEventListener(Context context, SignalTipView view) {
        mContext = context;
        mSignalTipView = view;
    }

    @Override
    public boolean onSignalLock(int arg0) {
        Log.d("lock", "AtvPlayerEventListener: onSignalLock");
        mSignalTipView.dismiss();
        Intent intentStandby = new Intent(Constants.CANCELSTANDBY);
        mContext.sendBroadcast(intentStandby);
        return true;
    }

    @Override
    public boolean onSignalUnLock(int arg0) {
        Log.d("lock", "AtvPlayerEventListener: onSignalUnLock");
        if (ChannelManagerExt.getInstance().getChannels().size() <= 0) {
            mSignalTipView.setText(mContext.getResources().getString(R.string.tuningtip));
        } else {
            mSignalTipView.setText("ATV " + mContext.getResources().getString(R.string.nosignaltips));
        }
        Intent intentStandby = new Intent(Constants.STARTSTANDBY);
        mContext.sendBroadcast(intentStandby);
        return false;
    }

    @Override
    public boolean onAtvAutoTuningScanInfo(int arg0, AtvEventScan arg1) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean onAtvManualTuningScanInfo(int arg0, AtvEventScan arg1) {
        // TODO Auto-generated method stub
        return false;
    }

    @Override
    public boolean onAtvProgramInfoReady(int arg0) {
        // TODO Auto-generated method stub
        return false;
    }
}
