
package com.eostek.tvmenu.tune;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.eostek.tvmenu.R;
import com.eostek.tvmenu.utils.ExTvChannelManager.EN_SCAN_RET_STATUS;
import com.eostek.tvmenu.utils.Constants;
import com.eostek.tvmenu.utils.ExTvChannelManager;
import com.mstar.android.tv.TvChannelManager;
import com.mstar.android.tv.TvCommonManager;
import com.mstar.android.tvapi.common.TvManager;
import com.mstar.android.tvapi.common.exception.TvCommonException;
import com.mstar.android.tvapi.common.vo.EnumProgramCountType;
import com.mstar.android.tvapi.common.vo.EnumProgramInfoType;
import com.mstar.android.tvapi.common.vo.ProgramInfo;
import com.mstar.android.tvapi.common.vo.ProgramInfoQueryCriteria;
import com.mstar.android.tvapi.dtv.common.DtvManager;
import com.mstar.android.tvapi.dtv.listener.OnDtvPlayerEventListener;
import com.mstar.android.tvapi.dtv.vo.DtvEventScan;
import com.mstar.android.tvapi.common.vo.TvTypeInfo;

public class DtvAutoTuningActivity extends Activity implements OnDtvPlayerEventListener {
    
    private static final String TAG = "AutoTuningActivity";

    private TextView mTuningProcessTxt;

    private TextView mSignalStrengthTxt;

    private TextView mSignalQualityTxt;

    private TextView mTvCountTxt;

    private TextView mBcCountTxt;

    private TextView mDbcCountTxt;

    private TextView mCurrRFChTxt;

    private TextView mTotalcount_txt;

    private TextView mTotalCountTxt;

    private Button mCancelTuningBtn;

    private ProgressBar mSearchBar;

//    private ProgressBar signalStrengthBar;

//    private ProgressBar signalQualityBar;

    private static int REFRESHPROGRAM = 0x01667;

    private int mRefreshTime = 600;

    public List<ProgramInfo> programInfos;

    private int mLastSize = 0;

    private List<ProgramInfo> infos;

    private ChannelFullTuningAdapter adapter;

    private ListView program_lv;

    private Handler mRefreshHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            if (msg.what == REFRESHPROGRAM) {
                if (programInfos.size() > mLastSize) {
                    infos.add(programInfos.get(mLastSize));
                    mLastSize++;
                    if (programInfos.size() == mLastSize && infos.size() > 3) {
                        infos.clear();
                        infos.addAll(programInfos.subList(mLastSize - 3, mLastSize));
                        adapter.notifyDataSetChanged();
                    } else {
                        adapter.notifyDataSetChanged();
                        sendEmptyMessageDelayed(REFRESHPROGRAM, mRefreshTime);
                    }
                }
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.full_tuning);
        DtvManager.getDvbPlayerManager().setOnDtvPlayerEventListener(this);
        infos = new ArrayList<ProgramInfo>();
        adapter = new ChannelFullTuningAdapter(this, infos);
        
        initViews();
        
        TvChannelManager mTvChannelManager = TvChannelManager.getInstance();
        TvTypeInfo tvInfo = TvCommonManager.getInstance().getTvInfo();
        int currentRouteIndex = mTvChannelManager.getCurrentDtvRouteIndex();
        int mCurrentRoute = tvInfo.routePath[currentRouteIndex];
        try {
            TvManager.getInstance().getChannelManager().deleteDtvMainList();
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        if (TvChannelManager.TV_ROUTE_DVBT == mCurrentRoute
                || TvChannelManager.TV_ROUTE_DVBT2 == mCurrentRoute) {
        	ExTvChannelManager.getInstance().setSystemCountry();
            mTvChannelManager.switchMSrvDtvRouteCmd(currentRouteIndex);
        } else if (TvChannelManager.TV_ROUTE_DTMB == mCurrentRoute) {
            mTvChannelManager.switchMSrvDtvRouteCmd(mTvChannelManager.getSpecificDtvRouteIndex(TvChannelManager.TV_ROUTE_DTMB));
        }
        mTvChannelManager.startDtvAutoScan();
        mCancelTuningBtn.setOnClickListener(new OnClickListener() {
            @Override
            public void onClick(View v) {
                stopTuning();
            }
        });
        mCancelTuningBtn.setOnFocusChangeListener(new OnFocusChangeListener(){
			@Override
			public void onFocusChange(View arg0, boolean hasFocus) { 
				if(hasFocus){
					mCancelTuningBtn.setBackgroundResource(R.drawable.bar_bg_btn_cyan);
				}else{
					mCancelTuningBtn.setBackgroundResource(R.drawable.bar_bg_btn_grey);
				}
			}	
        });
    }
    
    

    private void initViews() {
    	 mTuningProcessTxt = (TextView) findViewById(R.id.tuning_process_txt);
         mSignalStrengthTxt = (TextView) findViewById(R.id.signalStrength_txt);
         program_lv = (ListView) findViewById(R.id.tuning_program_lv);
         mSignalQualityTxt = (TextView) findViewById(R.id.signalQuality_text);
         mTvCountTxt = (TextView) findViewById(R.id.tv_count_txt);
         mBcCountTxt = (TextView) findViewById(R.id.bc_count_txt);
         mDbcCountTxt = (TextView) findViewById(R.id.dbc_count_txt);
         mCurrRFChTxt = (TextView) findViewById(R.id.rf_txt);
         mTotalCountTxt = (TextView) findViewById(R.id.total_count_txt);
         mTotalcount_txt = (TextView) findViewById(R.id.totalcount_txt);
         mCancelTuningBtn = (Button) findViewById(R.id.full_cancel_tuning_btn);
         mSearchBar = (ProgressBar) findViewById(R.id.full_searchBar);
//         signalStrengthBar = (ProgressBar) findViewById(R.id.full_signalStrengthbar);
//         signalQualityBar = (ProgressBar) findViewById(R.id.full_signalQualitybar);
         mTuningProcessTxt.setText(0 + "%");
         mSignalStrengthTxt.setText(0 + "dbuv");
         mTvCountTxt.setText(0 + "");
         mBcCountTxt.setText(0 + "");
         mDbcCountTxt.setText(0 + "");
         mTotalCountTxt.setText(0 + "");
         mSignalQualityTxt.setText(0 + "");
         program_lv.setAdapter(adapter);
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
    
    Handler handler  = new Handler();
    
    @Override
    protected void onPause() {
        super.onPause();
        Log.e(TAG,"DTV AutoTuning onPause");
        stopTuning();
        Log.e(TAG,"DTV AutoTuning delay");
        handler.postDelayed(finishDelay, 10000); 
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.e(TAG,"DTV AutoTuning onStop");
    }
    
    private Runnable finishDelay = new Runnable() {  
        public void run() { 
            Log.e(TAG,"finish DTV AutoTuning");
            finish();
        }  
    };

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
        Map<Integer, String> channelNums = new HashMap<Integer, String>();

        programInfos = new ArrayList<ProgramInfo>();

        // Filter repeat programs Methods one:
        for (ProgramInfo pgm : list) {
            int tempNum = pgm.number;
            String tempName = pgm.serviceName;

            if (channelNums.get(tempNum) == null) {// not repeat,add to list:
                programInfos.add(pgm);
                channelNums.put(tempNum, tempName);

            } else {
                if (channelNums.get(tempNum).equals(tempName)) {
                    if (pgm.serviceType == 1) {
                        dtv--;
                    } else if (pgm.serviceType == 2) {
                        radio--;
                    }
                } else {
                    programInfos.add(pgm);
                    channelNums.put(tempNum, tempName);

                }
            }
        }
        mTvCountTxt.setText(dtv + "");
        mBcCountTxt.setText(radio + "");
        mDbcCountTxt.setText(data + "");
        mTotalCountTxt.setText((dtv + data + radio) + "");
        mTotalcount_txt.setText(dtv + "");
        if (percent > 0) {
            mSearchBar.setVisibility(View.VISIBLE);
            FrameLayout.LayoutParams params = (android.widget.FrameLayout.LayoutParams) mSearchBar
                    .getLayoutParams();
            params.width = percent * 4;
            mSearchBar.setLayoutParams(params);
        };
        mSearchBar.setProgress(percent);
        mTuningProcessTxt.setText(percent + "%");
        mCurrRFChTxt.setText(currentChannel + "");
        Log.e("test","currentChannel = " + currentChannel);
//        if (signalStrength > 0) {
//            signalStrengthBar.setVisibility(View.VISIBLE);
//            FrameLayout.LayoutParams params = (android.widget.FrameLayout.LayoutParams) signalStrengthBar
//                    .getLayoutParams();
//            params.width = signalStrength * 4;
//            signalStrengthBar.setLayoutParams(params);
//        } else {
//            signalStrengthBar.setVisibility(View.GONE);
//        }
        mSignalStrengthTxt.setText(signalStrength + "dbuv");
//        if (signalQuality > 0) {
//            signalQualityBar.setVisibility(View.VISIBLE);
//            FrameLayout.LayoutParams params = (android.widget.FrameLayout.LayoutParams) signalQualityBar
//                    .getLayoutParams();
//            params.width = signalQuality * 4;
//            signalQualityBar.setLayoutParams(params);
//        } else {
//            signalQualityBar.setVisibility(View.GONE);
//        }
        mSignalQualityTxt.setText(signalQuality + "");

        // if (programInfos != null && programInfos.size() > lastSize) {
        // mRefreshHandler.removeMessages(REFRESHPROGRAM);
        // mRefreshHandler.sendEmptyMessage(REFRESHPROGRAM);
        // }
        if (scan_status == EN_SCAN_RET_STATUS.STATUS_SCAN_END.ordinal()) {
            stopTuning();
        }
        return true;
    }

    private void stopTuning() {
        Log.e(TAG, "stopTuning");
        TvChannelManager.getInstance().stopDtvScan();
        TvChannelManager.getInstance().changeToFirstService(TvChannelManager.FIRST_SERVICE_INPUT_TYPE_DTV, TvChannelManager.FIRST_SERVICE_DEFAULT);
        TvChannelManager.getInstance().stopDtvScan();
        Intent localIntent = new Intent();
        localIntent.setClassName(Constants.TV_PACKAGE_NAME, Constants.TV_CLASS_NAME);
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
            LayoutInflater inflater = LayoutInflater.from(DtvAutoTuningActivity.this);
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
        count = TvChannelManager.getInstance().getProgramCount(TvChannelManager.PROGRAM_COUNT_DTV_TV) + TvChannelManager.getInstance().getProgramCount(TvChannelManager.PROGRAM_COUNT_DTV_RADIO);
        for (int i = 0; i < count; i++) {
            ProgramInfo info = new ProgramInfo();
            info = TvChannelManager.getInstance().getProgramInfoByIndex(i);
            mProgramInfoList.add(info);
        }
        return mProgramInfoList;
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
