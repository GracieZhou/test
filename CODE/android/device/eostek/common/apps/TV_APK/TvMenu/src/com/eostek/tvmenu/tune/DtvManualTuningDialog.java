
package com.eostek.tvmenu.tune;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.eostek.tvmenu.R;
import com.eostek.tvmenu.TvMenuActivity;
import com.eostek.tvmenu.TvMenuHolder;
import com.eostek.tvmenu.ui.FocusView;
import com.eostek.tvmenu.utils.ExTvChannelManager;
import com.eostek.tvmenu.utils.Constants;
import com.mstar.android.tv.TvChannelManager;
import com.mstar.android.tv.TvCommonManager;
import com.mstar.android.tvapi.common.exception.TvCommonException;
import com.mstar.android.tvapi.common.vo.DtvProgramSignalInfo;
import com.mstar.android.tvapi.common.vo.EnumFirstServiceInputType;
import com.mstar.android.tvapi.common.vo.EnumFirstServiceType;
import com.mstar.android.tvapi.common.vo.ProgramInfo;
import com.mstar.android.tvapi.dtv.common.DtvManager;
import com.mstar.android.tvapi.dtv.dvb.dvbc.vo.EnumCabConstelType;
import com.mstar.android.tvapi.dtv.listener.OnDtvPlayerEventListener;
import com.mstar.android.tvapi.dtv.vo.DtvEventScan;
import com.mstar.android.tvapi.dtv.vo.RfInfo;

public class DtvManualTuningDialog extends AlertDialog {
    private static final String TAG = "DTVManualTuningDialog";

    private int mChannelNum = 0;

    private final int mWaitExpireTime = 1500;

    private Handler mTimerHandler;

    private Runnable mTimerRunnable;

    private int mInputChannelNumber = -1;

    private int mPreviousChannelNumber = -1;

    private TvChannelManager mTvChannelManager = null;

    private TvCommonManager mTvCommonManager = null;

    private TextView mStartSearch;

    private EditText mChannelNumTxt;

    private TextView mFrequency;

    private TextView mModulationTxt;

    private TextView mSymbolTxt;

    private TextView mFrequencyTxt;

    private TextView mTuningresultTxt;

    private LinearLayout mSignalStrengthLin;

    private LinearLayout mSignalQualityLin;

    private LinearLayout mFrequencyLin;

    private LinearLayout mModulationLin;

    private LinearLayout mSymbolLin;

    private LinearLayout mChannelNumLin;

    private LinearLayout mSearchLin;

    private FocusView focusView;

    private DtvProgramSignalInfo signalInfo;

    private boolean isRunning = true;

    private boolean refresh = true;

    private Handler mDtvUiEventHandler = null;

    private final static short DTV_SIGNAL_REFRESH_UI = 0x01;

    private enum EN_SCAN_RET_STATUS {
        // / None
        STATUS_SCAN_NONE,
        // / auto tuning process
        STATUS_AUTOTUNING_PROGRESS,
        // / signal quality
        STATUS_SIGNAL_QUALITY,
        // / get programes
        STATUS_GET_PROGRAMS,
        // / set region
        STATUS_SET_REGION,
        // / favorite region
        STATUS_SET_FAVORITE_REGION,
        // / exit to OAD download
        STATUS_EXIT_TO_DL,
        // / LCN conflict
        STATUS_LCN_CONFLICT,
        // / end of scan
        STATUS_SCAN_END,
        // /Scan end and rearrange done to set first prog. done
        STATUS_SCAN_SETFIRSTPROG_DONE
    }

    private int antennaType = 0;

    private int modulationindex = 2;

    private short dvbcsymbol = 6875;

    private int dvbcfreq = 474;

    private final int FREQUENCY_MAX = 999;

    private String[] modulationtype = {
            "16 QAM", "32 QAM", "64 QAM", "128 QAM", "256 QAM"
    };

    private EnumCabConstelType QAM_Type = EnumCabConstelType.E_CAB_QAM64;

    private int maxNumber;

    private Activity mContext;

    public DtvManualTuningDialog(Context context) {
        super(context, R.style.dialog);
        this.mContext = (Activity) context;
    }

    private final Runnable runnable = new Runnable() {
        @Override
        public void run() {
            while (isRunning) {

                if (refresh) {

                    signalInfo = mTvChannelManager.getCurrentSignalInformation();

                    dtvSignalHandler.sendEmptyMessage(DTV_SIGNAL_REFRESH_UI);

                }
                try {
                    Thread.sleep(600);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
            // dtvSignalHandler.postDelayed(runnable, 600);
        }
    };

    private Handler dtvSignalHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);

            switch (msg.what) {
                case DTV_SIGNAL_REFRESH_UI:
                    if (null == signalInfo) {
                        return;
                    }
                    if (signalInfo.quality <= 0) {
                        setProgressValueForSignalQuality(0);
                        setProgressValueForSignalStrengh(0);
                    } else {
                        setProgressValueForSignalQuality(signalInfo.quality / 10);
                        setProgressValueForSignalStrengh(signalInfo.strength / 10);
                    }
                    break;

                default:
                    break;
            }

        }
    };

    @SuppressWarnings("deprecation")
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((TvMenuActivity) mContext).getHandler().removeMessages(TvMenuHolder.FINISH);
        ((TvMenuActivity) mContext).getHandler().sendEmptyMessageDelayed(TvMenuHolder.FINISH,
                TvMenuActivity.DIMISS_DELAY_TIME);

        setContentView(R.layout.dtvmanualtuning);
        ExTvChannelManager.getInstance().setSystemCountry();
        findViews();
        setListener();

        initScanResult();

        mTvChannelManager = TvChannelManager.getInstance();
        DtvManager.getDvbPlayerManager().setOnDtvPlayerEventListener(onDtvPlayerEventListener);
        mTvCommonManager = TvCommonManager.getInstance();

        if (mTvCommonManager.getCurrentTvInputSource() != TvCommonManager.INPUT_SOURCE_DTV) {
            mTvCommonManager.setInputSource(TvCommonManager.INPUT_SOURCE_DTV);
        }

        TvChannelManager.getInstance().stopDtvScan();
        updateDtvManualtuningComponents();
        mTimerHandler = new Handler();
        mTimerRunnable = new Runnable() {

            @Override
            public void run() {
                mInputChannelNumber = -1;

                if (antennaType == TvChannelManager.TV_ROUTE_DVBC) {
                    if (mChannelNum < 1 || mChannelNum > 9999) {
                        mChannelNum = mPreviousChannelNumber;
                        mSymbolTxt.setText(Integer.toString(mChannelNum));
                        dvbcsymbol = (short) mChannelNum;
                    }
                    mPreviousChannelNumber = mChannelNum;

                } else if (antennaType == TvChannelManager.TV_ROUTE_DVBT
                        || antennaType == TvChannelManager.TV_ROUTE_DVBT2) {

                    if (mChannelNum < 7 || mChannelNum > 69) {
                        mChannelNum = mPreviousChannelNumber;
                        mChannelNumTxt.setText(String.valueOf(mChannelNum));
                    }

                    // Disable arrow in channel number layout.
                    if (mChannelNum != mPreviousChannelNumber) {
                        mPreviousChannelNumber = mChannelNum;
                    } else {
                        mPreviousChannelNumber = mChannelNum;
                    }
                }
            }
        };

        new Thread(runnable).start();
    }

    private void findViews() {
        // focusView = (FocusView) findViewById(R.id.focus_selector);
        // focusView.setTopOffset(getContext().getResources().getInteger(R.integer.dtv_manualtuning_focus_selector_top_off_set));
        mStartSearch = (TextView) findViewById(R.id.dtv_manualtuning_starttuning_txt);
        mChannelNumLin = (LinearLayout) findViewById(R.id.dtv_manualtuning_channelnum);
        mSearchLin = (LinearLayout) findViewById(R.id.dtv_manualtuning_starttuning_lin);
        antennaType = ExTvChannelManager.getInstance().judgeAntennaCategory();
        if (antennaType == TvChannelManager.TV_ROUTE_DVBT || antennaType == TvChannelManager.TV_ROUTE_DVBT2) {
            mFrequency = (TextView) findViewById(R.id.dtv_manualtuning_channel_val);
            mChannelNumTxt = (EditText) findViewById(R.id.dtv_manualtuning_channelnum_val);
            maxNumber = 10;
        } else if (antennaType == TvChannelManager.TV_ROUTE_DTMB) {
            mChannelNumTxt = (EditText) findViewById(R.id.dtv_manualtuning_channelnum_val);
            mFrequency = (TextView) findViewById(R.id.dtv_manualtuning_channel_val);
            mFrequency.setText(R.string.str_cha_dtvmanualtuning_frequency);
        } else if (antennaType == TvChannelManager.TV_ROUTE_DVBC) {
            ProgramInfo pi = ExTvChannelManager.getInstance().getCurrProgramInfo();

            mFrequencyLin = (LinearLayout) findViewById(R.id.dtv_manualtuning_frequency_lin);
            mFrequencyLin.setVisibility(View.VISIBLE);
            mFrequencyTxt = (TextView) findViewById(R.id.dtv_manualtuning_frequency_val);
            if (pi.frequency != 0 && pi.frequency < 999999) {
                dvbcfreq = pi.frequency / 1000;
            }
            mFrequencyTxt.setText(Integer.toString(dvbcfreq));
            mSymbolLin = (LinearLayout) findViewById(R.id.dtv_manualtuning_symbol_lin);
            mSymbolLin.setVisibility(View.VISIBLE);
            mSymbolTxt = (TextView) findViewById(R.id.dtv_manualtuning_symbol_val);
            mSymbolTxt.setText(Integer.toString(dvbcsymbol));
            mModulationLin = (LinearLayout) findViewById(R.id.dtv_manualtuning_modulation_lin);
            mModulationLin.setVisibility(View.VISIBLE);
            mModulationTxt = (TextView) findViewById(R.id.dtv_manualtuning_modulation_val);
            mChannelNumLin.setVisibility(View.GONE);
            maxNumber = 1000;
        }
        mSignalStrengthLin = (LinearLayout) findViewById(R.id.dtv_manualtuning_signalstrength_val);
        mSignalQualityLin = (LinearLayout) findViewById(R.id.linearlayout_cha_dtvmanualtuning_signalquality_val);
        mTuningresultTxt = (TextView) findViewById(R.id.textview_cha_dtvmanualtuning_tuningresult_dtv_val);
    }

    private void setListener() {
        FocusChangeListener focusChangeListener = new FocusChangeListener();
        mChannelNumLin.setOnFocusChangeListener(focusChangeListener);
        if (antennaType == TvChannelManager.TV_ROUTE_DVBC) {
            mModulationLin.setOnFocusChangeListener(focusChangeListener);
            mFrequencyLin.setOnFocusChangeListener(focusChangeListener);
            mSymbolLin.setOnFocusChangeListener(focusChangeListener);
        }
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        ((TvMenuActivity) mContext).getHandler().removeMessages(TvMenuHolder.FINISH);
        ((TvMenuActivity) mContext).getHandler().sendEmptyMessageDelayed(TvMenuHolder.FINISH,
                TvMenuActivity.DIMISS_DELAY_TIME);
        switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_TV_INPUT:
                if (mSearchLin.hasFocus()) {
                    startDtvManutuning();
                }
                return true;
            case KeyEvent.KEYCODE_VOLUME_UP:
                dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_RIGHT));
                return true;
            case KeyEvent.KEYCODE_VOLUME_DOWN:
                dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_LEFT));
                return true;
            case KeyEvent.KEYCODE_CHANNEL_UP:
                dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_DOWN));
                return true;
            case KeyEvent.KEYCODE_CHANNEL_DOWN:
                dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_UP));
                return true;
            default:
                break;
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        String str_val = new String();

        if (getCurrentFocus() == null) {
            return true;
        }
        int currentid = getCurrentFocus().getId();
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                switch (currentid) {
                    case R.id.dtv_manualtuning_channelnum:
                        if (mChannelNum < 7 || mChannelNum > 69) {
                            mChannelNum = mPreviousChannelNumber;
                        } else {
                            RfInfo rfInfo = null;
                            try {
                                if (DtvManager.getDvbPlayerManager() != null) {
                                    rfInfo = DtvManager.getDvbPlayerManager().getRfInfo(RfInfo.EnumInfoType.E_NEXT_RF,
                                            mChannelNum);
                                }
                            } catch (TvCommonException e) {
                                e.printStackTrace();
                            }
                            mChannelNum = rfInfo.rfPhyNum;
                        }

                        if (antennaType != TvChannelManager.TV_ROUTE_DVBC) {
                            str_val = Integer.toString(mChannelNum);
                            mChannelNumTxt.setText(str_val);
                        }

                        refreshTimer();
                        // initScanResult();
                        mTvChannelManager.stopDtvScan();
                        break;
                    case R.id.dtv_manualtuning_modulation_lin:
                        if (modulationindex == EnumCabConstelType.E_CAB_QAM256.ordinal()) {
                            modulationindex = 0;
                        } else {
                            modulationindex++;
                        }
                        QAM_Type = EnumCabConstelType.values()[modulationindex];
                        mModulationTxt.setText(modulationtype[modulationindex]);
                    case R.id.dtv_manualtuning_symbol_lin:
                        break;
                    case R.id.dtv_manualtuning_frequency_lin: {
                        dvbcfreq = (dvbcfreq + 1) % FREQUENCY_MAX;
                        mFrequencyTxt.setText(Integer.toString(dvbcfreq));
                    }
                        break;
                    default:
                        break;
                }
                break;
            case KeyEvent.KEYCODE_DPAD_LEFT:
                switch (currentid) {
                    case R.id.dtv_manualtuning_channelnum:

                        if (mChannelNum < 7 || mChannelNum > 69) {
                            Toast.makeText(mContext, "CH is illegality!!", Toast.LENGTH_SHORT).show();
                            mChannelNum = mPreviousChannelNumber;
                        } else {
                            RfInfo rfInfo = null;
                            try {
                                if (DtvManager.getDvbPlayerManager() != null) {
                                    rfInfo = DtvManager.getDvbPlayerManager().getRfInfo(RfInfo.EnumInfoType.E_PREV_RF,
                                            mChannelNum);
                                }
                            } catch (TvCommonException e) {
                                e.printStackTrace();
                            }
                            mChannelNum = rfInfo.rfPhyNum;
                        }

                        if (antennaType != TvChannelManager.TV_ROUTE_DVBC) {
                            str_val = Integer.toString(mChannelNum);
                            mChannelNumTxt.setText(str_val);
                        }
                        refreshTimer();
                        // initScanResult();
                        mTvChannelManager.stopDtvScan();
                        break;
                    case R.id.dtv_manualtuning_modulation_lin:
                        if (modulationindex == EnumCabConstelType.E_CAB_QAM16.ordinal()) {
                            modulationindex = EnumCabConstelType.E_CAB_QAM256.ordinal();
                        } else {
                            modulationindex--;
                        }
                        mModulationTxt.setText(modulationtype[modulationindex]);
                        break;
                    case R.id.dtv_manualtuning_frequency_lin:
                        dvbcfreq = (dvbcfreq + FREQUENCY_MAX - 1) % FREQUENCY_MAX;
                        mFrequencyTxt.setText(Integer.toString(dvbcfreq));
                        break;
                    default:
                        break;
                }
                break;
            case KeyEvent.KEYCODE_0:
            case KeyEvent.KEYCODE_1:
            case KeyEvent.KEYCODE_2:
            case KeyEvent.KEYCODE_3:
            case KeyEvent.KEYCODE_4:
            case KeyEvent.KEYCODE_5:
            case KeyEvent.KEYCODE_6:
            case KeyEvent.KEYCODE_7:
            case KeyEvent.KEYCODE_8:
            case KeyEvent.KEYCODE_9:
                if (currentid == R.id.dtv_manualtuning_channelnum || currentid == R.id.dtv_manualtuning_symbol_lin) {
                    // InputChannelNumber is -1,means that this is the first
                    // letter input.

                    if (-1 == mInputChannelNumber) {
                        // Just ignore input when first input is 0.
                        if (KeyEvent.KEYCODE_0 == keyCode) {
                            return true;
                        }
                        mInputChannelNumber = (keyCode - KeyEvent.KEYCODE_0);
                    } else if (mInputChannelNumber >= maxNumber) {
                        mInputChannelNumber = (keyCode - KeyEvent.KEYCODE_0);
                    } else {
                        mInputChannelNumber = mInputChannelNumber * 10 + (keyCode - KeyEvent.KEYCODE_0);
                    }
                    if (antennaType != TvChannelManager.TV_ROUTE_DVBC) {
                        mChannelNumTxt.setText(String.valueOf(mInputChannelNumber));
                        mChannelNum = mInputChannelNumber;
                    }
                    refreshTimer();
                    initScanResult();
                    mTvChannelManager.stopDtvScan();
                }

                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                if (currentid == R.id.dtv_manualtuning_starttuning_lin) {
                    if (antennaType == TvChannelManager.TV_ROUTE_DVBC) {
                        mFrequencyLin.requestFocus();
                        return true;
                    } else {
                        mChannelNumLin.requestFocus();
                        return true;
                    }
                }

                break;
            case KeyEvent.KEYCODE_DPAD_UP:
                if (currentid == R.id.dtv_manualtuning_channelnum || currentid == R.id.dtv_manualtuning_frequency_lin) {
                    mSearchLin.requestFocus();
                    return true;
                }
                break;
            case KeyEvent.KEYCODE_ENTER:
                switch (currentid) {
                    case R.id.dtv_manualtuning_starttuning_lin: {
                        startDtvManutuning();
                        refresh = false;
                    }
                        break;
                    default:
                        break;
                }
                break;
            case KeyEvent.KEYCODE_BACK:
            case KeyEvent.KEYCODE_MENU:
                mTvChannelManager.stopDtvScan();
                dismiss();
                return true;
            default:
                break;
        }
        if (keyCode < KeyEvent.KEYCODE_0 || keyCode > KeyEvent.KEYCODE_9) {
            mInputChannelNumber = -1;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void startDtvManutuning() {
        TvChannelManager mTvChannelManager = TvChannelManager.getInstance();
        Intent intentStandby = new Intent(Constants.CANCELSTANDBY);
        mContext.sendBroadcast(intentStandby);
        int curRouteIndex = mTvChannelManager.getCurrentDtvRouteIndex();
        Log.v("channel", "curRouteIndex:" + curRouteIndex);
        Log.v("channel", "DVBT:" + mTvChannelManager.getSpecificDtvRouteIndex(TvChannelManager.TV_ROUTE_DVBT));
        Log.v("channel", "DVBT2:" + mTvChannelManager.getSpecificDtvRouteIndex(TvChannelManager.TV_ROUTE_DVBT2));
        if (mTvChannelManager.getSpecificDtvRouteIndex(TvChannelManager.TV_ROUTE_DVBT) == curRouteIndex
                || mTvChannelManager.getSpecificDtvRouteIndex(TvChannelManager.TV_ROUTE_DVBT2) == curRouteIndex) {
            Log.v("channel", "channelno:" + mChannelNum);
            // mTvChannelManager.switchMSrvDtvRouteCmd(curRouteIndex);
            mTvChannelManager.setDtvManualScanByRF(mChannelNum);
            mPreviousChannelNumber = mChannelNum;
            mTvChannelManager.startDtvManualScan();
        } else if (mTvChannelManager.getSpecificDtvRouteIndex(TvChannelManager.TV_ROUTE_DTMB) == curRouteIndex) {
            if ("".equals(mChannelNumTxt.getText().toString()) || ".".equals(mChannelNumTxt.getText().toString())) {
                return;
            }
            ExTvChannelManager.getInstance().startManualScan();
            int freq = (int) (Double.parseDouble(mChannelNumTxt.getText().toString()) * 1000);
            Log.v("freq", "frequency:" + freq);
            mTvChannelManager.setDtvManualScanByRF(mChannelNum);
            mTvChannelManager.startDtvManualScan();
        }
        // else if (antennaType ==
        // ExTvChannelManager.EN_ANTENNA_TYPE.E_ROUTE_DVBC.ordinal()) {
        // ExTvChannelManager.getInstance().dvbcsetScanParam(dvbcsymbol,
        // QAM_Type, 0, 0,
        // (short) 0X000);
        // ExTvChannelManager.getInstance().dtvManualScanFreq(dvbcfreq * 1000);
        // tvChannelMgr.startDtvManualScan();
        // }

    }

    private void initScanResult() {
        mTuningresultTxt.setText(" 0");
        InitialProgressValueForSignalQuality();
        InitialProgressValueForSignalStrengh();
    }

    private void updateDtvManualtuningComponents() {
        RfInfo rfInfo = null;
        try {
            if (DtvManager.getDvbPlayerManager() != null) {
                rfInfo = DtvManager.getDvbPlayerManager().getRfInfo(RfInfo.EnumInfoType.E_FIRST_TO_SHOW_RF, 0);
            }
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        if (antennaType != TvChannelManager.TV_ROUTE_DVBC) {
            if (rfInfo != null) {
                mChannelNum = rfInfo.rfPhyNum;
                mPreviousChannelNumber = mChannelNum;
                mChannelNumTxt.setText("" + mChannelNum);
                Log.e(TAG, "aaachannelno:" + mChannelNum);
            }
        }

    }

    private void setProgressValueForSignalQuality(int val) {
        if (val <= 10 && val > 0) {
            for (int i = 0; i <= val - 1; i++) {
                ImageView searchImage = (ImageView) (mSignalQualityLin.getChildAt(i));
                searchImage.setImageDrawable(
                        mContext.getResources().getDrawable(R.drawable.picture_serchprogressbar_solid));
            }
            for (int i = val; i <= 9; i++) {
                ImageView searchImage = (ImageView) (mSignalQualityLin.getChildAt(i));
                searchImage.setImageDrawable(
                        mContext.getResources().getDrawable(R.drawable.picture_serchprogressbar_empty));
            }
        } else if (val > 10) {
            for (int i = 0; i <= 9; i++) {
                ImageView searchImage = (ImageView) (mSignalQualityLin.getChildAt(i));
                searchImage.setImageDrawable(
                        mContext.getResources().getDrawable(R.drawable.picture_serchprogressbar_solid));
            }
        } else if (val == 0) {
            for (int i = 0; i <= 9; i++) {
                ImageView searchImage = (ImageView) (mSignalQualityLin.getChildAt(i));
                searchImage.setImageDrawable(
                        mContext.getResources().getDrawable(R.drawable.picture_serchprogressbar_empty));
            }
        }
    }

    private void InitialProgressValueForSignalQuality() {
        for (int i = 0; i <= 9; i++) {
            ImageView searchImage = (ImageView) (mSignalQualityLin.getChildAt(i));
            searchImage
                    .setImageDrawable(mContext.getResources().getDrawable(R.drawable.picture_serchprogressbar_empty));
        }
    }

    private void setProgressValueForSignalStrengh(int val) {

        if (val <= 10 && val > 0) {
            for (int i = 0; i <= val - 1; i++) {
                ImageView searchImage = (ImageView) (mSignalStrengthLin.getChildAt(i));
                searchImage.setImageDrawable(
                        mContext.getResources().getDrawable(R.drawable.picture_serchprogressbar_solid));
            }
            for (int i = val; i <= 9; i++) {
                ImageView searchImage = (ImageView) (mSignalStrengthLin.getChildAt(i));
                searchImage.setImageDrawable(
                        mContext.getResources().getDrawable(R.drawable.picture_serchprogressbar_empty));
            }
        } else if (val > 10) {
            for (int i = 0; i <= 9; i++) {
                ImageView searchImage = (ImageView) (mSignalStrengthLin.getChildAt(i));
                searchImage.setImageDrawable(
                        mContext.getResources().getDrawable(R.drawable.picture_serchprogressbar_solid));
            }
        } else if (val == 0) {
            for (int i = 0; i <= 9; i++) {
                ImageView searchImage = (ImageView) (mSignalStrengthLin.getChildAt(i));
                searchImage.setImageDrawable(
                        mContext.getResources().getDrawable(R.drawable.picture_serchprogressbar_empty));
            }
        }
    }

    private void InitialProgressValueForSignalStrengh() {
        for (int i = 0; i <= 9; i++) {
            ImageView searchImage = (ImageView) (mSignalStrengthLin.getChildAt(i));
            searchImage
                    .setImageDrawable(mContext.getResources().getDrawable(R.drawable.picture_serchprogressbar_empty));
        }
    }

    private void refreshTimer() {
        if (mTimerHandler == null) {
            mTimerHandler = new Handler();
        }
        mTimerHandler.removeCallbacks(mTimerRunnable);
        mTimerHandler.postDelayed(mTimerRunnable, 2 * mWaitExpireTime);
    }

    private OnDtvPlayerEventListener onDtvPlayerEventListener = new OnDtvPlayerEventListener() {
        @Override
        public boolean onTsChange(int arg0) {
            return false;
        }

        @Override
        public boolean onSignalUnLock(int arg0) {
            return false;
        }

        @Override
        public boolean onSignalLock(int arg0) {
            return false;
        }

        @Override
        public boolean onRctPresence(int arg0) {
            return false;
        }

        @Override
        public boolean onPopupScanDialogNewMultiplex(int arg0) {
            return false;
        }

        @Override
        public boolean onPopupScanDialogLossSignal(int arg0) {
            return false;
        }

        @Override
        public boolean onPopupScanDialogFrequencyChange(int arg0) {
            return false;
        }

        @Override
        public boolean onOadTimeout(int arg0, int arg1) {
            return false;
        }

        @Override
        public boolean onOadHandler(int arg0, int arg1, int arg2) {
            return false;
        }

        @Override
        public boolean onOadDownload(int arg0, int arg1) {
            return false;
        }

        @Override
        public boolean onMheg5StatusMode(int arg0, int arg1) {
            return false;
        }

        @Override
        public boolean onMheg5ReturnKey(int arg0, int arg1) {
            return false;
        }

        @Override
        public boolean onMheg5EventHandler(int arg0, int arg1) {
            return false;
        }

        @Override
        public boolean onHbbtvStatusMode(int arg0, boolean arg1) {
            return false;
        }

        @Override
        public boolean onGingaStatusMode(int arg0, boolean arg1) {
            return false;
        }

        @Override
        public boolean onEpgTimerSimulcast(int arg0, int arg1) {
            return false;
        }

        @Override
        public boolean onDtvProgramInfoReady(int arg0) {
            return false;
        }

        @Override
        public boolean onDtvPriComponentMissing(int arg0) {
            return false;
        }

        @Override
        public boolean onDtvChannelNameReady(int arg0) {
            return false;
        }

        @Override
        public boolean onDtvAutoUpdateScan(int arg0) {
            return false;
        }

        @Override
        public boolean onDtvAutoTuningScanInfo(int arg0, DtvEventScan extra) {
            final int dtv = (int) extra.dtvSrvCount;
            int scan_status = (int) extra.scanStatus;
            final int quality = extra.signalQuality;
            final int strength = extra.signalStrength;
            Log.d(TAG, "quality:" + quality + "");
            Log.d(TAG, "strength:" + strength + "");
            Log.i(TAG, "dtv:::" + dtv);
            Log.i(TAG, "scan status::" + scan_status);
            ((Activity) mContext).runOnUiThread(new Runnable() {

                @Override
                public void run() {
                    mTuningresultTxt.setText(Integer.toString(dtv));
                    setProgressValueForSignalQuality(quality / 10);
                    setProgressValueForSignalStrengh(strength / 10);
                }
            });

            if (scan_status == EN_SCAN_RET_STATUS.STATUS_SCAN_END.ordinal()) {
                if (dtv > 0) {
                    Log.i(TAG, "change to first service");
                    mTvChannelManager.changeToFirstService(EnumFirstServiceInputType.E_FIRST_SERVICE_DTV,
                            EnumFirstServiceType.E_DEFAULT);
                    updateDTVChannel();
                }
                mTvChannelManager.stopDtvScan();
                mTimerHandler.removeCallbacks(mTimerRunnable);
                refresh = true;
                // Comment out this line for fix 0000258
                // dismiss();
            }
            return true;
        }

        @Override
        public boolean onCiLoadCredentialFail(int arg0) {
            return false;
        }

        @Override
        public boolean onChangeTtxStatus(int arg0, boolean arg1) {
            return false;
        }

        @Override
        public boolean onAudioModeChange(int arg0, boolean arg1) {
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
    };

    private void updateDTVChannel() {
        Intent i = new Intent("com.eos.tv.player.channellistupdate");
        mContext.sendBroadcast(i);
    }

    @Override
    public void dismiss() {
        isRunning = false;
        if (mContext != null && !mContext.isFinishing()) {
            super.dismiss();
        }
        ((TvMenuActivity) mContext).getHandler().removeMessages(TvMenuHolder.FINISH);
        mTvChannelManager.stopDtvScan();
        Intent localIntent = new Intent();
        localIntent.setClassName("com.eostek.tv", "com.eostek.tv.PlayerActivity");
        localIntent.putExtra("isRestChannels", true);
        mContext.startActivity(localIntent);
        mContext.finish();
    }

    class FocusChangeListener implements OnFocusChangeListener {

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
                mChannelNumTxt.setBackgroundResource(R.drawable.enumbar2_bg);
                // mStartSearch.setTextColor(0xffffff);
                mFrequency.setTextColor(0xff0ab6a8);
                mStartSearch.setTextColor(android.graphics.Color.WHITE);
            } else {
                mChannelNumTxt.setBackgroundResource(R.drawable.enumbar1_bg);
                mStartSearch.setTextColor(0xff0ab6a8);
                mFrequency.setTextColor(android.graphics.Color.WHITE);
            }
        }
    }

}
