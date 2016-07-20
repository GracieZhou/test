
package com.eostek.tvmenu.tune;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.drawable.Drawable;
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
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.Toast;

import com.eostek.tvmenu.R;
import com.eostek.tvmenu.TvMenuActivity;
import com.eostek.tvmenu.TvMenuHolder;
import com.mstar.android.tv.TvChannelManager;
import com.mstar.android.tvapi.atv.vo.EnumAtvManualTuneMode;

/**
 * @projectName： EOSTVMenu
 * @moduleName： FineTuningDialog.java
 * @author jachensy.chen
 * @version 1.0.0
 * @time 2014-1-20
 * @Copyright © 2013 EOSTEK, Inc.
 */
public class AtvFineTuningDialog extends AlertDialog {

    private static final String TAG = "FineTuningDialog";

    private static int CHANNEL_MAX_STEP = 64;

    private AtvFineTuningViewHolder mAtvFineTuningViewHolder;

    private Activity mContext;

    private TvChannelManager mTvChannelManager = null;

    private int mCurrentFrequency;

    private int mStep = 32;

    private int mTempValue = -1;

    private boolean TOUPDATE = true;

    private Handler updateHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg); 
            TOUPDATE = true;
        }
    };

    public AtvFineTuningDialog(Context context) {
        super(context, R.style.dialog);
        this.mContext = (Activity) context;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ((TvMenuActivity) mContext).getHandler().removeMessages(
                TvMenuHolder.FINISH);
        ((TvMenuActivity) mContext).getHandler().sendEmptyMessageDelayed(
                TvMenuHolder.FINISH, TvMenuActivity.DIMISS_DELAY_TIME);
        setContentView(R.layout.finetuning);
        mTvChannelManager = TvChannelManager.getInstance();
        init();
        initViews();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        ((TvMenuActivity) mContext).getHandler().removeMessages(
                TvMenuHolder.FINISH);
        ((TvMenuActivity) mContext).getHandler().sendEmptyMessageDelayed(
                TvMenuHolder.FINISH, TvMenuActivity.DIMISS_DELAY_TIME);
        return super.dispatchKeyEvent(event);
    }

    private void init() {
        mAtvFineTuningViewHolder = new AtvFineTuningViewHolder(this);
        mAtvFineTuningViewHolder.findViews();
        mAtvFineTuningViewHolder.setListeners();
    }

    private void initViews() {
        mCurrentFrequency = mTvChannelManager.getAtvCurrentFrequency();
        updateProgress();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_LEFT:
                if (mCurrentFrequency <= 44000) {
                    Toast.makeText(mContext, R.string.finetuningtip, Toast.LENGTH_LONG).show();
                    return true;
                }
                if (mAtvFineTuningViewHolder.getViewById(R.id.llayout).hasFocus()) {
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
                    mCurrentFrequency = mCurrentFrequency - 50;
                    updateProgress();
                    return true;
                }
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                if (mAtvFineTuningViewHolder.getViewById(R.id.llayout).hasFocus()) {
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
                    mCurrentFrequency = mCurrentFrequency + 50;
                    updateProgress();
                    return true;
                }
                break;
            case KeyEvent.KEYCODE_BACK:
                FineTuningStop();
                break;
        }

        return super.onKeyDown(keyCode, event);
    }

    public void FineTuningStop() {
        dismiss();
        mTvChannelManager.stopAtvManualTuning();
        mContext.findViewById(R.id.main).setVisibility(View.VISIBLE);
        mContext.findViewById(R.id.item_fine_tuning_ll).requestFocus();
    }

    private void updateChannelNum() {
        TextView view = (TextView) this.mAtvFineTuningViewHolder.getViewById(R.id.current_channel);
        int currentChannelNum = mTvChannelManager.getCurrentChannelNumber();
        view.setText(String.valueOf(currentChannelNum + 1));
        TextView fre = (TextView) this.mAtvFineTuningViewHolder.getViewById(R.id.current_fre);
        fre.setText(getTuningfrequency());
    }

    private void updateProgress() {
        findViewById(R.id.llayout).setVisibility(View.VISIBLE);
        SeekBar bar = (SeekBar) this.mAtvFineTuningViewHolder
                .getViewById(R.id.ministrim_progress);
        Drawable d = mContext.getResources().getDrawable(R.drawable.seekbar_thumb1);
        bar.setThumbOffset(d.getIntrinsicWidth()/2);
        bar.setProgress(mStep);

        TextView tv = (TextView)this.mAtvFineTuningViewHolder
                .getViewById(R.id.value);
        tv.setText(String.valueOf(mStep));
        FrameLayout.LayoutParams params = (android.widget.FrameLayout.LayoutParams) tv
                .getLayoutParams();
//        params.width = mStep * 6;
        params.leftMargin = mStep * 520/100 + 23;
        tv.setLayoutParams(params);
        Log.i(TAG, "current frequency is " + mCurrentFrequency);
        updateChannelNum();
    }

    private String getTuningfrequency() {
        int minteger = mCurrentFrequency / 1000;
        int mfraction = (mCurrentFrequency % 1000) / 10; // 0.25M not

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
                if (mTempValue != mCurrentFrequency) {
                    mTempValue = mCurrentFrequency;
                    mTvChannelManager.startAtvManualTuning(50 * 1000, mCurrentFrequency,
                            EnumAtvManualTuneMode.E_MANUAL_TUNE_MODE_FINE_TUNE_DOWN);
                }
            } else if (params[0] == 1) {
                if (mTempValue != mCurrentFrequency) {
                    mTempValue = mCurrentFrequency;
                    mTvChannelManager.startAtvManualTuning(50 * 1000, mCurrentFrequency,
                            EnumAtvManualTuneMode.E_MANUAL_TUNE_MODE_FINE_TUNE_UP);
                }
            }
            return null;
        }
    }
}
