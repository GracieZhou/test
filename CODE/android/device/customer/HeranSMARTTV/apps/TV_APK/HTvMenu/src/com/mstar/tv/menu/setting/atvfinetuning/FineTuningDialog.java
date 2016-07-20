
package com.mstar.tv.menu.setting.atvfinetuning;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.AsyncTask;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.mstar.android.tv.TvChannelManager;
import com.mstar.android.tvapi.atv.vo.EnumAtvManualTuneMode;
import com.mstar.tv.menu.R;
import com.mstar.tv.menu.ui.EosCustomSettingActivity;

/**
 * @projectName： EOSTVMenu
 * @moduleName： FineTuningDialog.java
 * @author jachensy.chen
 * @version 1.0.0
 * @time 2014-1-20
 * @Copyright © 2013 EOSTEK, Inc.
 */
public class FineTuningDialog extends AlertDialog {

    private static final String TAG = "FineTuningDialog";

    private static int CHANNEL_MAX_STEP = 64;

    private FineTuningViewHolder mCMiniStrimViewHolder;

    private FineTuningListeners mCMiniStrimListeners;

    private Activity mContext;

    private TvChannelManager tvChannelMgr = null;

    private int currentFrequency;

    private int mStep = 32;

    private int tempValue = -1;

    private boolean TOUPDATE = true;

    private Handler updateHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            TOUPDATE = true;
        }
    };

    public FineTuningDialog(Context context) {
        super(context, R.style.dialog);
        this.mContext = (Activity) context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((EosCustomSettingActivity) mContext).getHandler().removeMessages(
                EosCustomSettingActivity.DELAYFINISH);
        ((EosCustomSettingActivity) mContext).getHandler().sendEmptyMessageDelayed(
                EosCustomSettingActivity.DELAYFINISH, EosCustomSettingActivity.TODIMISSDELAYTIME);
        setContentView(R.layout.eos_finetuning);
        tvChannelMgr = TvChannelManager.getInstance();
        init();
        initViews();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        ((EosCustomSettingActivity) mContext).getHandler().removeMessages(
                EosCustomSettingActivity.DELAYFINISH);
        ((EosCustomSettingActivity) mContext).getHandler().sendEmptyMessageDelayed(
                EosCustomSettingActivity.DELAYFINISH, EosCustomSettingActivity.TODIMISSDELAYTIME);
        return super.dispatchKeyEvent(event);
    }

    private void init() {
        mCMiniStrimViewHolder = new FineTuningViewHolder(this);
        mCMiniStrimViewHolder.findViews();

        mCMiniStrimListeners = new FineTuningListeners(mCMiniStrimViewHolder);
        mCMiniStrimListeners.setListeners();
    }

    private void initViews() {
        currentFrequency = tvChannelMgr.getAtvCurrentFrequency();
        updateProgress();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_LEFT:
                if (currentFrequency <= 44000) {
                    Toast.makeText(mContext, R.string.finetuningtip, Toast.LENGTH_LONG).show();
                    return true;
                }
                if (mCMiniStrimViewHolder.getViewById(R.id.llayout).hasFocus()) {
                    if (!TOUPDATE) {
                        return true;
                    }
                    TOUPDATE = false;
                    updateHandler.sendEmptyMessageDelayed(0, 3);
                    if (mStep > 0) {
                        mStep--;
                    } else {
                        return true;
                    }
                    new MyTask().execute(0);
                    currentFrequency = currentFrequency - 50;
                    updateProgress();
                    return true;
                }
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                if (mCMiniStrimViewHolder.getViewById(R.id.llayout).hasFocus()) {
                    if (!TOUPDATE) {
                        return true;
                    }
                    TOUPDATE = false;
                    updateHandler.sendEmptyMessageDelayed(0, 3);
                    if (mStep < CHANNEL_MAX_STEP) {
                        mStep++;
                    } else {
                        return true;
                    }
                    new MyTask().execute(1);
                    currentFrequency = currentFrequency + 50;
                    updateProgress();
                    return true;
                }
                break;
            case KeyEvent.KEYCODE_BACK:
                channelMiniStrimStop();
                break;
        }

        return super.onKeyDown(keyCode, event);
    }

    public void channelMiniStrimStop() {
        dismiss();
        tvChannelMgr.stopAtvManualTuning();
        mContext.findViewById(R.id.main).setVisibility(View.VISIBLE);
        ((ListView) mContext.findViewById(R.id.context_lst)).requestFocus();
    }

    private void updateChannelNo() {
        TextView view = (TextView) this.mCMiniStrimViewHolder.getViewById(R.id.current_channel);
        int currentChannelNo = tvChannelMgr.getCurrentChannelNumber();
        view.setText(String.valueOf(currentChannelNo + 1));
        TextView fre = (TextView) this.mCMiniStrimViewHolder.getViewById(R.id.current_fre);
        fre.setText(getTuningfreq());
    }

    private void updateProgress() {
        findViewById(R.id.llayout).setVisibility(View.VISIBLE);
        ProgressBar bar = (ProgressBar) this.mCMiniStrimViewHolder
                .getViewById(R.id.ministrim_progress);
        FrameLayout.LayoutParams params = (android.widget.FrameLayout.LayoutParams) bar
                .getLayoutParams();
        params.width = mStep * 6;
        bar.setLayoutParams(params);
        if (mStep == 0) {
            bar.setVisibility(View.GONE);
        } else {
            bar.setVisibility(View.VISIBLE);
        }
        ((TextView) this.mCMiniStrimViewHolder.getViewById(R.id.value)).setText(String
                .valueOf(mStep));
        Log.i(TAG, "current frequency is " + currentFrequency);
        updateChannelNo();
    }

    private String getTuningfreq() {
        int minteger = currentFrequency / 1000;
        int mfraction = (currentFrequency % 1000) / 10; // 0.25M not

        if (mfraction <= 5) {
            mfraction = 0;
        } else if ((mfraction >= 20) && (mfraction <= 30)) {
            mfraction = 25;
        } else if ((mfraction >= 45) && (mfraction <= 55)) {
            mfraction = 50;
        } else if ((mfraction >= 70) && (mfraction <= 80)) {
            mfraction = 75;
        }
        return Integer.toString(minteger) + "." + Integer.toString(mfraction)
                + mContext.getResources().getString(R.string.str_cha_atvmanualtuning_frequency_mhz);
    }

    class MyTask extends AsyncTask<Integer, Integer, Integer> {

        @Override
        protected Integer doInBackground(Integer... params) {
            if (params[0] == 0) {
                if (tempValue != currentFrequency) {
                    tempValue = currentFrequency;
                    tvChannelMgr.startAtvManualTuning(50 * 1000, currentFrequency,
                            EnumAtvManualTuneMode.E_MANUAL_TUNE_MODE_FINE_TUNE_DOWN);
                }
            } else if (params[0] == 1) {
                if (tempValue != currentFrequency) {
                    tempValue = currentFrequency;
                    tvChannelMgr.startAtvManualTuning(50 * 1000, currentFrequency,
                            EnumAtvManualTuneMode.E_MANUAL_TUNE_MODE_FINE_TUNE_UP);
                }
            }
            return null;
        }
    }
}
