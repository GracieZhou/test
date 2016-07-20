
package com.mstar.tv.menu.setting;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.mstar.android.tv.TvAtscChannelManager;
import com.mstar.android.tv.TvChannelManager;
import com.mstar.android.tvapi.common.TvManager;
import com.mstar.android.tvapi.common.exception.TvCommonException;
import com.mstar.android.tvapi.common.vo.EnumFirstServiceInputType;
import com.mstar.android.tvapi.common.vo.EnumFirstServiceType;
import com.mstar.android.tvapi.common.vo.EnumProgramCountType;
import com.mstar.android.tvapi.common.vo.EnumProgramInfoType;
import com.mstar.android.tvapi.common.vo.ProgramInfo;
import com.mstar.android.tvapi.common.vo.ProgramInfoQueryCriteria;
import com.mstar.android.tvapi.dtv.common.DtvManager;
import com.mstar.android.tvapi.dtv.listener.OnDtvPlayerEventListener;
import com.mstar.android.tvapi.dtv.vo.DtvEventScan;
import com.mstar.android.tvapi.dtv.vo.EnumRfChannelBandwidth;
import com.mstar.tv.ExTvChannelManager;
import com.mstar.tv.ExTvChannelManager.EN_SCAN_RET_STATUS;
import com.mstar.tv.menu.R;

public class AutoTuningActivity extends Activity implements OnDtvPlayerEventListener {
    private static final String TAG = "AutoTuningActivity";

    private enum EN_ANTENNA_TYPE {
        DTMB, DVBC, DVBT, E_ROUTE_MAX,
    }

    private TextView tuningProcessTxt;

    private TextView signalStrengthTxt;

    private TextView signalQualityTxt;

    private TextView tvCountTxt;

    private TextView bcCountTxt;

    private TextView dbcCountTxt;

    private TextView rfTxt;

    private TextView totalcount_txt;

    private TextView totalCountTxt;

    private Button cancelTuningBtn;

    private ProgressBar searchBar;

    private ProgressBar signalStrengthBar;

    private ProgressBar signalQualityBar;

    private static int REFRESHPROGRAM = 0x01667;

    private int refreshTime = 600;

    private int lastSize = 0;

    private List<ProgramInfo> infos;

    private ChannelFullTuningAdapter adapter;

    private ListView program_lv;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.full_tuning);
        DtvManager.getDvbPlayerManager().setOnDtvPlayerEventListener(this);
        infos = new ArrayList<ProgramInfo>();
        adapter = new ChannelFullTuningAdapter(this, infos);
        tuningProcessTxt = (TextView) findViewById(R.id.tuning_process_txt);
        signalStrengthTxt = (TextView) findViewById(R.id.signalStrength_txt);
        program_lv = (ListView) findViewById(R.id.tuning_program_lv);
        signalQualityTxt = (TextView) findViewById(R.id.signalQuality_text);
        tvCountTxt = (TextView) findViewById(R.id.tv_count_txt);
        bcCountTxt = (TextView) findViewById(R.id.bc_count_txt);
        dbcCountTxt = (TextView) findViewById(R.id.dbc_count_txt);
        rfTxt = (TextView) findViewById(R.id.rf_txt);
        totalCountTxt = (TextView) findViewById(R.id.total_count_txt);
        totalcount_txt = (TextView) findViewById(R.id.totalcount_txt);
        cancelTuningBtn = (Button) findViewById(R.id.full_cancel_tuning_btn);
        searchBar = (ProgressBar) findViewById(R.id.full_searchBar);
        signalStrengthBar = (ProgressBar) findViewById(R.id.full_signalStrengthbar);
        signalQualityBar = (ProgressBar) findViewById(R.id.full_signalQualitybar);

        tuningProcessTxt.setText(0 + "%");
        signalStrengthTxt.setText(0 + "dbuv");
        tvCountTxt.setText(0 + "");
        bcCountTxt.setText(0 + "");
        dbcCountTxt.setText(0 + "");
        totalCountTxt.setText(0 + "");
        signalQualityTxt.setText(0 + "");
        program_lv.setAdapter(adapter);
        final TvChannelManager mTvChannelManager = TvChannelManager.getInstance();
        ExTvChannelManager.getInstance().setSystemCountry();
        try {
            TvManager.getInstance().getChannelManager().deleteDtvMainList();
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        mTvChannelManager.switchMSrvDtvRouteCmd(mTvChannelManager.getCurrentDtvRouteIndex());
        mTvChannelManager.startDtvAutoScan();
        
        cancelTuningBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                mTvChannelManager.stopDtvScan();
                mTvChannelManager.changeToFirstService(EnumFirstServiceInputType.E_FIRST_SERVICE_DTV,
                        EnumFirstServiceType.E_DEFAULT);
            }
        });
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (KeyEvent.KEYCODE_TV_INPUT == event.getKeyCode()) {
            dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER));
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public boolean onKeyDown(int keycode, KeyEvent arg1) {
        if (keycode == KeyEvent.KEYCODE_DPAD_UP || keycode == KeyEvent.KEYCODE_DPAD_RIGHT) {
            return true;
        }
        return super.onKeyDown(keycode, arg1);
    }

    @Override
    public void onBackPressed() {
    }

    @Override
    protected void onStop() {
        super.onStop();
        stopTuning();
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
    public boolean onDtvAutoTuningScanInfo(int arg0, DtvEventScan extra) {
        Log.i(TAG, "onDtvAutoTuningScanInfo:");
        int dtv = (int) extra.dtvSrvCount;
        int radio = (int) extra.radioSrvCount;
        int data = (int) extra.dataSrvCount;
        int percent = (int) extra.scanPercentageNum;
        int scan_status = (int) extra.scanStatus;
        int currentChannel = (int) extra.currRFCh;
        int signalStrength = extra.signalStrength;
        int signalQuality = extra.signalQuality;

        Log.i(TAG, "dtvSrvCount:" + dtv);
        Log.i(TAG, "radioSrvCount:" + radio);
        Log.i(TAG, "dataSrvCount:" + data);
        Log.i(TAG, "scanPercentageNum:" + percent);
        Log.i(TAG, "scanStatus:" + scan_status);
        Log.i(TAG, "currRFCh:" + currentChannel);
        // Filter repeat programs
        List<ProgramInfo> list = getProgramList();
        // Filter repeat programs Methods one:
        for (ProgramInfo pgm : list) {
            // int tempNum = pgm.number;
            // String tempName = pgm.serviceName;
            Log.i(TAG,
                    "pgm.number:" + pgm.number + "----->pgm.serviceName:" + pgm.serviceName + "pgm=" + pgm.toString());
        }
        tvCountTxt.setText(dtv + "");
        bcCountTxt.setText(radio + "");
        dbcCountTxt.setText(data + "");
        totalCountTxt.setText((dtv + data + radio) + "");
        // totalcount_txt.setText((dtv + data + radio) + "");
        totalcount_txt.setText(dtv + "");
        if (percent > 0) {
            searchBar.setVisibility(View.VISIBLE);
            FrameLayout.LayoutParams params = (android.widget.FrameLayout.LayoutParams) searchBar.getLayoutParams();
            params.width = percent * 4;
            searchBar.setLayoutParams(params);
        }
        tuningProcessTxt.setText(percent + "%");
        rfTxt.setText(currentChannel + "");
        if (signalStrength > 0) {
            signalStrengthBar.setVisibility(View.VISIBLE);
            FrameLayout.LayoutParams params = (android.widget.FrameLayout.LayoutParams) signalStrengthBar
                    .getLayoutParams();
            params.width = signalStrength * 4;
            signalStrengthBar.setLayoutParams(params);
        } else {
            signalStrengthBar.setVisibility(View.GONE);
        }
        signalStrengthTxt.setText(signalStrength + "dbuv");
        if (signalQuality > 0) {
            signalQualityBar.setVisibility(View.VISIBLE);
            FrameLayout.LayoutParams params = (android.widget.FrameLayout.LayoutParams) signalQualityBar
                    .getLayoutParams();
            params.width = signalQuality * 4;
            signalQualityBar.setLayoutParams(params);
        } else {
            signalQualityBar.setVisibility(View.GONE);
        }
        signalQualityTxt.setText(signalQuality + "");
        if (scan_status == EN_SCAN_RET_STATUS.STATUS_SCAN_END.ordinal()) {
            stopTuning();
        }
        return true;
    }

    private void stopTuning() {
        Log.i(TAG, "stopTuning");
        TvChannelManager.getInstance().stopDtvScan();
        TvChannelManager.getInstance().changeToFirstService(EnumFirstServiceInputType.E_FIRST_SERVICE_DTV,
                EnumFirstServiceType.E_DEFAULT);
        TvChannelManager.getInstance().stopDtvScan();
        Intent localIntent = new Intent();
        localIntent.setClassName("com.eostek.tv.player", "com.eostek.tv.player.PlayerActivity");
        localIntent.putExtra("isRestChannels", true);
        startActivity(localIntent);
        finish();
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
        return false;
    }

    @Override
    public boolean onSignalUnLock(int arg0) {
        return false;
    }

    @Override
    public boolean onTsChange(int arg0) {
        return false;
    }

    class ChannelFullTuningAdapter extends BaseAdapter {

        private List<ProgramInfo> infos;

        public ChannelFullTuningAdapter(Context context, List<ProgramInfo> list) {
            this.infos = list;
        }

        @Override
        public int getCount() {
            return infos.size();
        }

        @Override
        public Object getItem(int arg0) {
            return null;
        }

        @Override
        public long getItemId(int arg0) {
            return 0;
        }

        @Override
        public View getView(int position, View view, ViewGroup arg2) {
            LayoutInflater inflater = LayoutInflater.from(AutoTuningActivity.this);
            View layout = inflater.inflate(R.layout.fulltuning_item, null);
            TextView proName = (TextView) layout.findViewById(R.id.programName);
            proName.setText(infos.get(position).serviceName);
            return layout;
        }
    }

    public List<ProgramInfo> getProgramList() {
        int count = 0;
        List<ProgramInfo> mProgramInfoList = new ArrayList<ProgramInfo>();
        // the list include TV and radio,so the count is TV plus radio.
        count = TvChannelManager.getInstance().getProgramCount(EnumProgramCountType.E_COUNT_DTV_TV)
                + TvChannelManager.getInstance().getProgramCount(EnumProgramCountType.E_COUNT_DTV_RADIO);
        for (int i = 0; i < count; i++) {
            ProgramInfo info = new ProgramInfo();
            ProgramInfoQueryCriteria temp = new ProgramInfoQueryCriteria();
            temp.queryIndex = i;
            info = TvChannelManager.getInstance().getProgramInfo(temp, EnumProgramInfoType.E_INFO_DATABASE_INDEX);
            mProgramInfoList.add(info);
        }
        return mProgramInfoList;
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
