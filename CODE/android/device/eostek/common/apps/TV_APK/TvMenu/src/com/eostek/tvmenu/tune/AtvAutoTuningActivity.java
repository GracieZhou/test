
package com.eostek.tvmenu.tune;

import android.app.Activity;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.eostek.tvmenu.R;
import com.eostek.tvmenu.utils.Constants;
import com.eostek.tvmenu.utils.ExTvChannelManager;
import com.mstar.android.tv.TvChannelManager;
import com.mstar.android.tvapi.atv.AtvManager;
import com.mstar.android.tvapi.atv.listener.OnAtvPlayerEventListener;
import com.mstar.android.tvapi.atv.vo.AtvEventScan;
import com.mstar.android.tvapi.common.TvManager;
import com.mstar.android.tvapi.common.exception.TvCommonException;
import com.mstar.android.tvapi.common.vo.EnumFirstServiceInputType;
import com.mstar.android.tvapi.common.vo.EnumFirstServiceType;

public class AtvAutoTuningActivity extends Activity {
    private static final String TAG = AtvAutoTuningActivity.class.getSimpleName();

    // the min frequence of atv scan
    private static int ATV_MIN_FREQ = 48250;

    // the max frequence of atv scan
    private static int ATV_MAX_FREQ = 877250;

    // the EventIntervalMs of atv
    private static int ATV_EVENTINTERVAL = 500 * 1000;
    
    private LinearLayout mItemPercentLl;//the layout of Item

    private TextView mCountText;

    private TextView mFrequencyText;
    
    private TextView mPercentTitleTxt;

    private TextView mPercentText;

    private SeekBar mTunePercentBar;

    private TextView mAtvCurchannelTxt;

    private Button mAtvCancelTuningBtn;
    
    private RelativeLayout.LayoutParams mPercentTextParams;//LayoutParams of PercentText
    private RelativeLayout.LayoutParams mPercentBarParams;//LayoutParams of PercentBar

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.atv_autotuning);
        initView();
        
        ExTvChannelManager.getInstance().setSystemCountry();
       
        atvSetAutoTuningStart();
        
        setListeners();
    }

	private void initView() {
    	mItemPercentLl = (LinearLayout) findViewById(R.id.item_percent_ll);
        mPercentTitleTxt = (TextView) mItemPercentLl.findViewById(R.id.title_txt);
        mPercentText = (TextView) mItemPercentLl.findViewById(R.id.seekbar_number);
        mTunePercentBar = (SeekBar) mItemPercentLl.findViewById(R.id.seekbar);
        mCountText = (TextView) findViewById(R.id.atv_count_txt);
        mFrequencyText = (TextView) findViewById(R.id.atv_frequency_txt);
        mAtvCurchannelTxt = (TextView) findViewById(R.id.atv_curchannel_txt);
        mAtvCancelTuningBtn = (Button) findViewById(R.id.atv_cancel_tuning_btn);
        
        mPercentTextParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        mPercentBarParams = new RelativeLayout.LayoutParams(RelativeLayout.LayoutParams.WRAP_CONTENT, RelativeLayout.LayoutParams.WRAP_CONTENT);
        
        mPercentTitleTxt.setText(R.string.percent);
        mTunePercentBar.setProgressDrawable(getResources().getDrawable(R.drawable.seekbar_progress2));
        mTunePercentBar.setThumb(getResources().getDrawable(R.drawable.seekbar_thumb2));
        mTunePercentBar.setOnSeekBarChangeListener(mSeekChange);
        Drawable d = getResources().getDrawable(R.drawable.seekbar_thumb1);
        mTunePercentBar.setThumbOffset(d.getIntrinsicWidth()/2);
        
        
	}

	 private void setListeners() {
	    	mAtvCancelTuningBtn.setOnClickListener(new OnClickListener() {
	            @Override
	            public void onClick(View v) {
	                stopTuning();
	            }
	        });
	        mAtvCancelTuningBtn.setOnFocusChangeListener(new OnFocusChangeListener(){
				@Override
				public void onFocusChange(View arg0, boolean hasFocus) {
					if(hasFocus){
						mAtvCancelTuningBtn.setBackgroundResource(R.drawable.bar_bg_btn_cyan);
					}else{
						mAtvCancelTuningBtn.setBackgroundResource(R.drawable.bar_bg_btn_grey);
					}
				}
	        });
			
		}
	

    private boolean atvSetAutoTuningStart() {
        boolean result = false;
        try {
            // to support Taiwan atv standard.
            AtvManager.getAtvPlayerManager().setOnAtvPlayerEventListener(new AtvEventListener());

            TvManager.getInstance().getChannelManager().deleteAtvMainList();
            result = TvChannelManager.getInstance().startAtvAutoTuning(ATV_EVENTINTERVAL,
                    ATV_MIN_FREQ, ATV_MAX_FREQ);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        return result;
    }

    protected void stopTuning() {
        Log.e(TAG, "stop Atv Auto Tuning.");
        TvChannelManager.getInstance().pauseAtvAutoTuning();
        TvChannelManager.getInstance().stopAtvAutoTuning();
        TvChannelManager.getInstance().changeToFirstService(TvChannelManager.FIRST_SERVICE_INPUT_TYPE_ATV, TvChannelManager.FIRST_SERVICE_DEFAULT);
        Intent localIntent = new Intent();
        localIntent.setClassName(Constants.TV_PACKAGE_NAME, Constants.TV_CLASS_NAME);
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
            mCountText.setText(Integer.toString(scannedChannelNum));
            mFrequencyText.setText(sFreq);
            if(percent > 0){
            	mPercentText.setText(Integer.toString(percent));
            }
            
            mAtvCurchannelTxt.setText(Integer.toString(currentChannel));
            mTunePercentBar.setProgress(percent);

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
    
    private OnSeekBarChangeListener mSeekChange = new OnSeekBarChangeListener() {

        @Override
        public void onStopTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onStartTrackingTouch(SeekBar seekBar) {
        }

        @Override
        public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
        	mPercentTextParams.leftMargin = progress * 448 / 100 - 6;
            mPercentText.setLayoutParams(mPercentTextParams);
            mPercentText.setWidth(70);
        }
    };
    
    @Override
    public void onBackPressed() {
    }
    
}
