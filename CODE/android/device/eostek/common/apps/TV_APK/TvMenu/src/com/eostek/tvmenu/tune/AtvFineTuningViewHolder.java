
package com.eostek.tvmenu.tune;

import android.graphics.Rect;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.SeekBar;
import android.widget.TextView;

import com.eostek.tvmenu.R;
import com.eostek.tvmenu.utils.ExTvChannelManager;

public class AtvFineTuningViewHolder {

    private AtvFineTuningDialog mAtvFineTuningDialog;

    private Button mSaveBtn;

    private Button mEscBtn;

    private TextView mCurrentChannelNumtext;

    private TextView mCurrentFrequency;

    private SeekBar mFineTuningProgress;

    private TextView value;

    private LinearLayout mLinearLayout;

    public AtvFineTuningViewHolder(AtvFineTuningDialog dialog) {
        this.mAtvFineTuningDialog = dialog;
    }

    public void findViews() {
        this.mSaveBtn = (Button) this.mAtvFineTuningDialog.findViewById(R.id.save_channel);
        this.mEscBtn = (Button) this.mAtvFineTuningDialog.findViewById(R.id.cancel_channel);
        this.mCurrentChannelNumtext = (TextView) this.mAtvFineTuningDialog
                .findViewById(R.id.current_channel);
        mCurrentFrequency = (TextView) this.mAtvFineTuningDialog.findViewById(R.id.current_fre);
        this.mFineTuningProgress = (SeekBar) this.mAtvFineTuningDialog
                .findViewById(R.id.ministrim_progress);
        mFineTuningProgress.setProgressDrawable(mFineTuningProgress.getResources().getDrawable(R.drawable.seekbar_progress2));
        mFineTuningProgress.setThumb(mFineTuningProgress.getResources().getDrawable(R.drawable.seekbar_thumb2));
//        mFineTuningProgress.setOnSeekBarChangeListener(mSeekChange);
        Drawable d = mFineTuningProgress.getResources().getDrawable(R.drawable.seekbar_thumb1);
        mFineTuningProgress.setThumbOffset(d.getIntrinsicWidth()/2);
        value = (TextView) this.mAtvFineTuningDialog.findViewById(R.id.value);
        mLinearLayout = (LinearLayout) this.mAtvFineTuningDialog.findViewById(R.id.llayout);
    }

    public View getViewById(int id) {
        switch (id) {
            case R.id.ministrim_progress:
                return mFineTuningProgress;
            case R.id.current_channel:
                return mCurrentChannelNumtext;
            case R.id.current_fre:
                return mCurrentFrequency;
            case R.id.save_channel: 
                return mSaveBtn;
            case R.id.cancel_channel:
                return mEscBtn;
            case R.id.value:
                return value;
            case R.id.llayout:
                return mLinearLayout;
            default:
                return null;
        }
    }

    public AtvFineTuningDialog getActivity() {
        return this.mAtvFineTuningDialog;
    }
    
    public void setListeners() {
        FineTuningButtonClick onClick = new FineTuningButtonClick();
        mSaveBtn.setOnClickListener(onClick);
        mEscBtn.setOnClickListener(onClick);
        mLinearLayout.setOnFocusChangeListener(
                new OnFocusChangeListener(){
					@Override
					public void onFocusChange(View arg0, boolean hasFocus) {
						// TODO Auto-generated method stub
						
					}
                });
        mSaveBtn.setOnFocusChangeListener(new OnFocusChangeListener(){
					@Override
					public void onFocusChange(View arg0, boolean hasFocus) {
						if(hasFocus){
							mSaveBtn.setBackgroundResource(R.drawable.bar_bg_btn_cyan);
						}else{
							mSaveBtn.setBackgroundResource(R.drawable.bar_bg_btn_grey);
						}
					}	
                });
        mEscBtn.setOnFocusChangeListener(new OnFocusChangeListener(){
					@Override
					public void onFocusChange(View arg0, boolean hasFocus) {
						if(hasFocus){
							mEscBtn.setBackgroundResource(R.drawable.bar_bg_btn_cyan);
						}else{
							mEscBtn.setBackgroundResource(R.drawable.bar_bg_btn_grey);
						}
					}
                });
        mLinearLayout.setOnFocusChangeListener(new OnFocusChangeListener(){
			@Override
			public void onFocusChange(View arg0, boolean hasFocus) {
				if(hasFocus){
					mFineTuningProgress.setProgressDrawable(mFineTuningProgress.getResources().getDrawable(R.drawable.seekbar_progress2));
			        mFineTuningProgress.setThumb(mFineTuningProgress.getResources().getDrawable(R.drawable.seekbar_thumb2));
				}else{
					mFineTuningProgress.setProgressDrawable(mFineTuningProgress.getResources().getDrawable(R.drawable.seekbar_progress3));
			        mFineTuningProgress.setThumb(mFineTuningProgress.getResources().getDrawable(R.drawable.seekbar_thumb1));
				}
			}
        });
    }

    private class FineTuningButtonClick implements OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.save_channel:
                	getActivity().FineTuningStop();
                    ExTvChannelManager.getInstance().saveAtvProgram(
                            ExTvChannelManager.getInstance().getCurrentChannelNumber());
                    getActivity().dismiss();
                    break;
                case R.id.cancel_channel:
                	getActivity().FineTuningStop();
                    getActivity().dismiss();
                    break;
            }
        }
    }
}
