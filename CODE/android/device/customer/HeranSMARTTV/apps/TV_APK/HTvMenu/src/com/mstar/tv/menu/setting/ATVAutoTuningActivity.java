
package com.mstar.tv.menu.setting;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mstar.android.tv.TvChannelManager;
import com.mstar.android.tvapi.atv.AtvManager;
import com.mstar.android.tvapi.atv.listener.OnAtvPlayerEventListener;
import com.mstar.android.tvapi.atv.vo.AtvEventScan;
import com.mstar.android.tvapi.common.TvManager;
import com.mstar.android.tvapi.common.exception.TvCommonException;
import com.mstar.android.tvapi.common.vo.EnumFirstServiceInputType;
import com.mstar.android.tvapi.common.vo.EnumFirstServiceType;
import com.mstar.tv.ExTvChannelManager;
import com.mstar.tv.menu.R;

public class ATVAutoTuningActivity extends Activity {
    private static final String TAG = ATVAutoTuningActivity.class.getSimpleName();

    // the min frequence of atv scan
    private static int ATV_MIN_FREQ = 48250;

    // the max frequence of atv scan
    private static int ATV_MAX_FREQ = 877250;

    // the EventIntervalMs of atv
    private static int ATV_EVENTINTERVAL = 500 * 1000;

    private TextView countTxt;

    private TextView frequencyTxt;

    private TextView percentTxt;

    private ProgressBar percentBar;

    private TextView atvCurchannelTxt;

    private Button atvCancelTuningBtn;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.atv_autotuning);
        countTxt = (TextView) findViewById(R.id.atv_count_txt);
        frequencyTxt = (TextView) findViewById(R.id.atv_frequency_txt);
        percentTxt = (TextView) findViewById(R.id.atv_percent_txt);
        atvCurchannelTxt = (TextView) findViewById(R.id.atv_curchannel_txt);
        percentBar = (ProgressBar) findViewById(R.id.atv_percentbar);
        atvCancelTuningBtn = (Button) findViewById(R.id.atv_cancel_tuning_btn);
        ExTvChannelManager.getInstance().setSystemCountry();
        atvSetAutoTuningStart();
        atvCancelTuningBtn.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View v) {
                stopTuning();
            }
        });
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopTuning();
    }

    private boolean atvSetAutoTuningStart() {
        boolean result = false;
        try {
            // to support Taiwan atv standard.
            AtvManager.getAtvPlayerManager().setOnAtvPlayerEventListener(new AtvEventListener());
            // ExTvChannelManager.getInstance().forceVideoStandard(
            // EnumAvdVideoStandardType.NTSC_M.ordinal());
            // ExTvChannelManager.getInstance().atvSetForceSoundSystem(EnumAtvSystemStandard.E_M);

            AtvManager.getAtvPlayerManager().setOnAtvPlayerEventListener(new AtvEventListener());
            // already make source ATV in this function
            TvManager.getInstance().getChannelManager().deleteAtvMainList();
            result = TvChannelManager.getInstance().startAtvAutoTuning(ATV_EVENTINTERVAL,
                    ATV_MIN_FREQ, ATV_MAX_FREQ);
        } catch (TvCommonException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return result;
    }

    protected void stopTuning() {
        Log.e(TAG, "stop Atv Auto Tuning.");
        TvChannelManager.getInstance().stopAtvAutoTuning();
        TvChannelManager.getInstance().changeToFirstService(
                EnumFirstServiceInputType.E_FIRST_SERVICE_ATV, EnumFirstServiceType.E_AUTO_SCAN);
        Intent localIntent = new Intent();
        localIntent.setClassName("com.eostek.tv.player", "com.eostek.tv.player.PlayerActivity");
        localIntent.putExtra("isRestChannels", true);
        startActivity(localIntent);
        finish();
    }

    private class AtvEventListener implements OnAtvPlayerEventListener {
        @Override
        public boolean onAtvAutoTuningScanInfo(int arg0, final AtvEventScan extra) {
            final int percent = extra.percent;
            int frequencyKHz = extra.frequencyKHz;
            final int scannedChannelNum = extra.scannedChannelNum;
            final int currentChannel = extra.curScannedChannel;
            final boolean bIsScaningEnable = extra.bIsScaningEnable;
            final String sFreq = " " + (frequencyKHz / 1000) + "." + (frequencyKHz % 1000) / 10
                    + "Mhz";
            Log.e(TAG, "percent:" + percent + " :sFreq:" + sFreq + " :scannedChannelNum:"
                    + scannedChannelNum + " :currentChannel:" + currentChannel);
            countTxt.setText(Integer.toString(scannedChannelNum));
            frequencyTxt.setText(sFreq);
            percentTxt.setText(Integer.toString(percent));
            atvCurchannelTxt.setText(Integer.toString(currentChannel));
            if (percent > 0) {
                percentBar.setVisibility(View.VISIBLE);
                FrameLayout.LayoutParams params = (android.widget.FrameLayout.LayoutParams) percentBar
                        .getLayoutParams();
                params.width = percent * 4;
                percentBar.setLayoutParams(params);
            } else {
                percentBar.setVisibility(View.GONE);
            }

            if ((percent >= 100 && (bIsScaningEnable == false)) || (frequencyKHz > ATV_MAX_FREQ)) {
                stopTuning();
            }
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
            return false;
        }

        @Override
        public boolean onSignalUnLock(int arg0) {
            return false;
        }
    }
}
