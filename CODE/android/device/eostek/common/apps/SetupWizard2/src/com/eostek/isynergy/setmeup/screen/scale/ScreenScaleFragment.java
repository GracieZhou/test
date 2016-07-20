
package com.eostek.isynergy.setmeup.screen.scale;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.InputDevice;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;

import com.eostek.isynergy.setmeup.R;
import com.eostek.isynergy.setmeup.StateMachineActivity;
import com.eostek.isynergy.setmeup.WizardLogic;
import com.eostek.isynergy.setmeup.screen.ScreenLogic;
import com.eostek.isynergy.setmeup.utils.Utils;

public class ScreenScaleFragment extends Fragment {

    private static final String TAG = ScreenScaleFragment.class.getSimpleName();

    private SeekBar mManaMySeekBar;

    private int mCurrentScale = 13;
    ScreenLogic mScreenLogic;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.new_fragment_screen_scale, container, false);

        mManaMySeekBar = (SeekBar) v.findViewById(R.id.screen_seekbar);
        mManaMySeekBar.setProgress(mCurrentScale);

        mManaMySeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {

            @Override
            public void onStopTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onStartTrackingTouch(SeekBar seekBar) {
            }

            @Override
            public void onProgressChanged(SeekBar seekBar, int progress, boolean fromUser) {
                int seekBarProgress = mManaMySeekBar.getProgress();
                int percent = seekBarProgress + 80;
                mScreenLogic=new  ScreenLogic((StateMachineActivity) getActivity());
                mScreenLogic.scale(percent);
                mScreenLogic.getCurrentScale();
                
                Utils.print(TAG, "getProgress:" + percent);
            }
        });
        mManaMySeekBar.setFocusable(false);

        v.requestFocus();

        return v;
    }

    public int getProgress() {
        Utils.print(TAG, "getProgress:" + mManaMySeekBar.getProgress());
        return mManaMySeekBar.getProgress();
    }

    public void setProgress(int scaleProgress) {
    	if(WizardLogic.isFirst){
    		Log.e("test", "isFirst");
    		if (mManaMySeekBar != null) {
                mManaMySeekBar.setProgress(12);
            }
    		WizardLogic.isFirst = false;
    	}else{
    		this.mCurrentScale = scaleProgress;
            Utils.print(TAG, "currentScale:" + mCurrentScale);
            if (mManaMySeekBar != null) {
                mManaMySeekBar.setProgress(mCurrentScale);
            }
    	}
    }
    
    public boolean onGenericMotionEvent(MotionEvent event) {
    if (0 != (event.getSource() & InputDevice.SOURCE_CLASS_POINTER)) {	
      switch (event.getAction()) {
    	case MotionEvent.ACTION_SCROLL:	
    	if( event.getAxisValue(MotionEvent.AXIS_VSCROLL) < 0.0f){	
    		Log.i("test", "down" );
    	}else{
    		Log.i("test", "up" );
    		}
    			return true;
    		}
    	}
    	return false;
    }

}
