
package com.eostek.tvmenu.sound;

import android.app.Fragment;
import android.graphics.Color;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.SeekBar;
import android.widget.TextView;

import com.eostek.tvmenu.R;
import com.eostek.tvmenu.utils.Constants;
import com.mstar.android.tv.TvAudioManager;

public class EquilizerFragment extends Fragment {

    private TvAudioManager mTvAudioManager;

    public final static short T_NRMode_IDX = 0x0E;

    public final static short T_SystemSetting_IDX = 0x19;

    private LinearLayout mItemEquilizer120hzLl;

    private LinearLayout mItemEquilizer500hzLl;

    private LinearLayout mItemEquilizer1500hzLl;

    private LinearLayout mItemEquilizer5khzLl;

    private LinearLayout mItemEquilizer10khzLl;
    
    private TextView m120hzTitleTxt;
    private SeekBar m120hzSeekBar;
    private TextView m120hzSeekBarNum;
    
    private TextView m500hzTitleTxt;
    private SeekBar m500hzSeekBar;
    private TextView m500hzSeekBarNum;
    
    private TextView m1500hzTitleTxt;
    private SeekBar m1500hzSeekBar;
    private TextView m1500hzSeekBarNum;
    
    private TextView m5KhzTitleTxt;
    private SeekBar m5KhzSeekBar;
    private TextView m5KhzSeekBarNum;
    
    private TextView m10KhzTitleTxt;
    private SeekBar m10KhzSeekBar;
    private TextView m10KhzSeekBarNum;
    
    private FrameLayout.LayoutParams paramsStrength1;
    private FrameLayout.LayoutParams paramsStrength2;
    private FrameLayout.LayoutParams paramsStrength3;
    private FrameLayout.LayoutParams paramsStrength4;
    private FrameLayout.LayoutParams paramsStrength5;
    
    private int m120hzVal;
    private int m500hzVal;
    private int m1500hzVal;
    private int m5KhzVal;
    private int m10KhzVal;
    
    private String[] mTitleEquilizer;
    
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.equilizer_fragment, null);
        
        mTitleEquilizer = getActivity().getResources().getStringArray(R.array.setting_equilizer);
        
        mItemEquilizer120hzLl = (LinearLayout)view.findViewById(R.id.item_equilizer120hz_ll);
        m120hzTitleTxt = (TextView) mItemEquilizer120hzLl.findViewById(R.id.title_txt);
        m120hzSeekBar = (SeekBar) mItemEquilizer120hzLl.findViewById(R.id.seekbar);
        m120hzSeekBarNum = (TextView) mItemEquilizer120hzLl.findViewById(R.id.seekbar_number);
        
        mItemEquilizer500hzLl = (LinearLayout)view.findViewById(R.id.item_equilizer500hz_ll);
        m500hzTitleTxt = (TextView) mItemEquilizer500hzLl.findViewById(R.id.title_txt);
        m500hzSeekBar = (SeekBar) mItemEquilizer500hzLl.findViewById(R.id.seekbar);
        m500hzSeekBarNum = (TextView) mItemEquilizer500hzLl.findViewById(R.id.seekbar_number);
        
        mItemEquilizer1500hzLl = (LinearLayout)view.findViewById(R.id.item_equilizer1500hz_ll);
        m1500hzTitleTxt = (TextView) mItemEquilizer1500hzLl.findViewById(R.id.title_txt);
        m1500hzSeekBar = (SeekBar) mItemEquilizer1500hzLl.findViewById(R.id.seekbar);
        m1500hzSeekBarNum = (TextView) mItemEquilizer1500hzLl.findViewById(R.id.seekbar_number);
        
        mItemEquilizer5khzLl = (LinearLayout)view.findViewById(R.id.item_equilizer5khz_ll);
        m5KhzTitleTxt = (TextView) mItemEquilizer5khzLl.findViewById(R.id.title_txt);
        m5KhzSeekBar = (SeekBar) mItemEquilizer5khzLl.findViewById(R.id.seekbar);
        m5KhzSeekBarNum = (TextView) mItemEquilizer5khzLl.findViewById(R.id.seekbar_number);
        
        mItemEquilizer10khzLl = (LinearLayout)view.findViewById(R.id.item_equilizer10khz_ll);
        m10KhzTitleTxt = (TextView) mItemEquilizer10khzLl.findViewById(R.id.title_txt);
        m10KhzSeekBar = (SeekBar) mItemEquilizer10khzLl.findViewById(R.id.seekbar);
        m10KhzSeekBarNum = (TextView) mItemEquilizer10khzLl.findViewById(R.id.seekbar_number);
        
        initView();
        initData();
        setListener();
        return view;
    }
   
    /**
	 * init the view of all items.item_titles and item_values
	 * 
	 * @param view
	 */
    protected void initView() {
    	m120hzTitleTxt.setText(mTitleEquilizer[Constants.TITLE_120HZ]);
    	m500hzTitleTxt.setText(mTitleEquilizer[Constants.TITLE_500HZ]);
    	m1500hzTitleTxt.setText(mTitleEquilizer[Constants.TITLE_1500HZ]);
    	m5KhzTitleTxt.setText(mTitleEquilizer[Constants.TITLE_5KHZ]);
    	m10KhzTitleTxt.setText(mTitleEquilizer[Constants.TITLE_10KHZ]);
    	
    	m120hzSeekBar.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.seekbar_progress1));
    	m120hzSeekBar.setThumb(getActivity().getResources().getDrawable(R.drawable.seekbar_thumb1));
    	m120hzSeekBarNum.setTextColor(Color.WHITE);
		
    	m500hzSeekBar.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.seekbar_progress1));
    	m500hzSeekBar.setThumb(getActivity().getResources().getDrawable(R.drawable.seekbar_thumb1));
    	m500hzSeekBarNum.setTextColor(Color.WHITE);
		
    	m1500hzSeekBar.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.seekbar_progress1));
    	m1500hzSeekBar.setThumb(getActivity().getResources().getDrawable(R.drawable.seekbar_thumb1));
    	m1500hzSeekBarNum.setTextColor(Color.WHITE);
		
    	m5KhzSeekBar.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.seekbar_progress1));
    	m5KhzSeekBar.setThumb(getActivity().getResources().getDrawable(R.drawable.seekbar_thumb1));
    	m5KhzSeekBarNum.setTextColor(Color.WHITE);
		
    	m10KhzSeekBar.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.seekbar_progress1));
    	m10KhzSeekBar.setThumb(getActivity().getResources().getDrawable(R.drawable.seekbar_thumb1));
    	m10KhzSeekBarNum.setTextColor(Color.WHITE);
    }

    /**
  	 * get data from system and init the data to each item
  	 */
    protected void initData() {
        mTvAudioManager = TvAudioManager.getInstance();
        
        //get value of each Item
        m120hzVal = mTvAudioManager.getEqBand120();
        m500hzVal = mTvAudioManager.getEqBand500();
        m1500hzVal = mTvAudioManager.getEqBand1500();
        m5KhzVal = mTvAudioManager.getEqBand5k();
        m10KhzVal = mTvAudioManager.getEqBand10k();
        
        //init layoutParams
        paramsStrength1 = new FrameLayout.LayoutParams(
        		FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
		paramsStrength2 = new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
		paramsStrength3 = new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
		paramsStrength4 = new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
		paramsStrength5 = new FrameLayout.LayoutParams(
				FrameLayout.LayoutParams.WRAP_CONTENT, FrameLayout.LayoutParams.WRAP_CONTENT);
		
		//init seekBar data of each Item
		initSeekbarData(m120hzVal,m120hzSeekBar,m120hzSeekBarNum,paramsStrength1);
		initSeekbarData(m500hzVal,m500hzSeekBar,m500hzSeekBarNum,paramsStrength2);
		initSeekbarData(m1500hzVal,m1500hzSeekBar,m1500hzSeekBarNum,paramsStrength3);
		initSeekbarData(m5KhzVal,m5KhzSeekBar,m5KhzSeekBarNum,paramsStrength4);
		initSeekbarData(m10KhzVal,m10KhzSeekBar,m10KhzSeekBarNum,paramsStrength5);
		
    }
    
    /**
	 * set OnKeyListener and OnFocusListener
	 */
    protected void setListener(){
    	
    	OnKeyListener OnKeyListener = new OnKeyListener(){
			@Override
			public boolean onKey(View view, int keyCode, KeyEvent event) {
				switch(view.getId()){
					case R.id.item_equilizer120hz_ll:{
						if (event.getAction() == KeyEvent.ACTION_DOWN) {
							 switch (keyCode) {
							 	case KeyEvent.KEYCODE_DPAD_RIGHT: {
			                    	 if (!view.isFocusable()) {
			                             return true;
			                         }
			                    	 if(m120hzVal >= 0 && m120hzVal < 100){
			                    		 m120hzVal++;
			                    		 //update ui when change the value
			                    		 updateSeekbarUi(m120hzVal,m120hzSeekBar,m120hzSeekBarNum,paramsStrength1);
			                    		 mTvAudioManager.setEqBand120(m120hzVal);
			                    	 }
							 	}
							 	break;
							 	case KeyEvent.KEYCODE_DPAD_LEFT:{
							 		if (!view.isFocusable()) {
			                             return true;
			                         }
							 		 if(m120hzVal > 0 && m120hzVal <= 100){
							 			 m120hzVal--;
			                    		 updateSeekbarUi(m120hzVal,m120hzSeekBar,m120hzSeekBarNum,paramsStrength1);
			                    		 mTvAudioManager.setEqBand120(m120hzVal);
			                    	 }
							 	}
							 	break;
							 }
						 }
					}
					break;
				
					case R.id.item_equilizer500hz_ll:{
						if (event.getAction() == KeyEvent.ACTION_DOWN) {
							 switch (keyCode) {
							 	case KeyEvent.KEYCODE_DPAD_RIGHT: {
			                    	 if (!view.isFocusable()) {
			                             return true;
			                         }
			                    	 if(m500hzVal >= 0 && m500hzVal < 100){
			                    		 m500hzVal++;
			                    		 updateSeekbarUi(m500hzVal,m500hzSeekBar,m500hzSeekBarNum,paramsStrength2);
			                    		 mTvAudioManager.setEqBand500(m500hzVal);
			                    	 }
								 }
							 	break;
							 	case KeyEvent.KEYCODE_DPAD_LEFT:{
							 		if (!view.isFocusable()) {
			                             return true;
			                         }
							 		if(m500hzVal > 0 && m500hzVal <= 100){
							 			 m500hzVal--;
			                    		 updateSeekbarUi(m500hzVal,m500hzSeekBar,m500hzSeekBarNum,paramsStrength2);
			                    		 mTvAudioManager.setEqBand500(m500hzVal);
							 		}
							 	}
							 	break;
							 }
						 }
					}
					break;
					
					case R.id.item_equilizer1500hz_ll:{
						if (event.getAction() == KeyEvent.ACTION_DOWN) {
							 switch (keyCode) {
							 	case KeyEvent.KEYCODE_DPAD_RIGHT: {
			                    	 if (!view.isFocusable()) {
			                             return true;
			                         }
			                    	 if(m1500hzVal >= 0 && m1500hzVal < 100){
			                    		 m1500hzVal++;
			                    		 updateSeekbarUi(m1500hzVal,m1500hzSeekBar,m1500hzSeekBarNum,paramsStrength3);
			                    		 mTvAudioManager.setEqBand1500(m1500hzVal);
			                    	 }
								 }
							 	break;
							 	case KeyEvent.KEYCODE_DPAD_LEFT:{
							 		if (!view.isFocusable()) {
			                             return true;
			                         }
							 		
							 		if(m1500hzVal > 0 && m1500hzVal <= 100){
			                    		 m1500hzVal--;
			                    		 updateSeekbarUi(m1500hzVal,m1500hzSeekBar,m1500hzSeekBarNum,paramsStrength3);
			                    		 mTvAudioManager.setEqBand1500(m1500hzVal);
			                    	 }
							 	}
							 	break;
							 }
						 }
					}
					break;
					
					case R.id.item_equilizer5khz_ll:{
						if (event.getAction() == KeyEvent.ACTION_DOWN) {
							 switch (keyCode) {
							 	case KeyEvent.KEYCODE_DPAD_RIGHT: {
			                    	 if (!view.isFocusable()) {
			                             return true;
			                         }
			                    	 if(m5KhzVal >= 0 && m5KhzVal < 100){
			                    		 m5KhzVal++;
			                    		 updateSeekbarUi(m5KhzVal,m5KhzSeekBar,m5KhzSeekBarNum,paramsStrength4);
			                    		 mTvAudioManager.setEqBand5k(m5KhzVal);
			                    	 }
								 }
							 	break;
							 	case KeyEvent.KEYCODE_DPAD_LEFT:{
							 		if (!view.isFocusable()) {
			                             return true;
			                         }
							 		if(m5KhzVal > 0 && m5KhzVal <= 100){
							 			 m5KhzVal--;
			                    		 updateSeekbarUi(m5KhzVal,m5KhzSeekBar,m5KhzSeekBarNum,paramsStrength4);
			                    		 mTvAudioManager.setEqBand5k(m5KhzVal);
							 		}
							 	}
							 	break;
							 }
						 }
					}
					break;
					
					case R.id.item_equilizer10khz_ll:{
						if (event.getAction() == KeyEvent.ACTION_DOWN) {
							 switch (keyCode) {
							 	case KeyEvent.KEYCODE_DPAD_RIGHT: {
			                    	 if (!view.isFocusable()) {
			                             return true;
			                         }
			                    	 if(m10KhzVal >= 0 && m10KhzVal < 100){
			                    		 m10KhzVal++;
			                    		 updateSeekbarUi(m10KhzVal,m10KhzSeekBar,m10KhzSeekBarNum,paramsStrength5);
			                    		 mTvAudioManager.setEqBand10k(m10KhzVal);
			                    	 }
								 }
							 	break;
							 	case KeyEvent.KEYCODE_DPAD_LEFT:{
							 		if(m10KhzVal > 0 && m10KhzVal <= 100){
							 			 m10KhzVal--;
			                    		 updateSeekbarUi(m10KhzVal,m10KhzSeekBar,m10KhzSeekBarNum,paramsStrength5);
			                    		 mTvAudioManager.setEqBand10k(m10KhzVal);
			                    	 }
							 	}
							 }
						 }
					}
					break;
					
				}
				return false;  
			}
		};
    	
		//set Items OnKeyListener
    	mItemEquilizer120hzLl.setOnKeyListener(OnKeyListener);
		mItemEquilizer500hzLl.setOnKeyListener(OnKeyListener);
		mItemEquilizer1500hzLl.setOnKeyListener(OnKeyListener);
		mItemEquilizer5khzLl.setOnKeyListener(OnKeyListener);
		mItemEquilizer10khzLl.setOnKeyListener(OnKeyListener);
    	
    	OnFocusChangeListener onFocusChangeListener = new OnFocusChangeListener(){

			@Override
			public void onFocusChange(View view, boolean haiFocus) {
				
				switch(view.getId()){
					case R.id.item_equilizer120hz_ll:{ 
						if(haiFocus){
							//if seekBarItem has focused ,change ui
							seekbarItemFocused(m120hzTitleTxt,m120hzSeekBar);
						}else{
							//if seekBarItem does not has focused ,change ui
							seekbarItemUnfocused(m120hzTitleTxt,m120hzSeekBar);
						}
					}
					break;
					case R.id.item_equilizer500hz_ll:{
						if(haiFocus){
					        seekbarItemFocused(m500hzTitleTxt,m500hzSeekBar);
						}else{
					        seekbarItemUnfocused(m500hzTitleTxt,m500hzSeekBar);
						}
					}
					break;
					case R.id.item_equilizer1500hz_ll:{
						if(haiFocus){
							seekbarItemFocused(m1500hzTitleTxt,m1500hzSeekBar);
						}else{
							seekbarItemUnfocused(m1500hzTitleTxt,m1500hzSeekBar);
						}
					}
					break;
					case R.id.item_equilizer5khz_ll:{ 
						if(haiFocus){
							seekbarItemFocused(m5KhzTitleTxt,m5KhzSeekBar);
						}else{
							seekbarItemUnfocused(m5KhzTitleTxt,m5KhzSeekBar);
						}
					}
					break;
					case R.id.item_equilizer10khz_ll:{
						if(haiFocus){
							seekbarItemFocused(m10KhzTitleTxt,m10KhzSeekBar);
						}else{
							seekbarItemUnfocused(m10KhzTitleTxt,m10KhzSeekBar);
						}
					}
					break;
					
				}
			}
		};
		
		//set Items OnFocusChangeListener
		mItemEquilizer120hzLl.setOnFocusChangeListener(onFocusChangeListener);
		mItemEquilizer500hzLl.setOnFocusChangeListener(onFocusChangeListener);
		mItemEquilizer1500hzLl.setOnFocusChangeListener(onFocusChangeListener);
		mItemEquilizer5khzLl.setOnFocusChangeListener(onFocusChangeListener);
		mItemEquilizer10khzLl.setOnFocusChangeListener(onFocusChangeListener);
		
    }
    
    /**
     * update ui when seekbar has moved
     * 
     * @param progressVal
     * @param seekbar
     * @param seekBarNum
     * @param Params
     */
    private void updateSeekbarUi(int progressVal, SeekBar seekbar, TextView seekBarNum,LayoutParams Params){
    	seekbar.setProgress(progressVal);
        seekBarNum.setText(Integer.toString(progressVal));
        Params.leftMargin = progressVal * Constants.ESEEKBAR_LAYOUTPARAMS + Constants.ESEEKBAR_ADJUST_LAYOUTPARAMS;
        seekBarNum.setLayoutParams(Params);
        seekBarNum.setWidth(70);
    }
    
    /**
     * init data and set ui of seekbarItem
     * @param progressVal
     * @param seekbar
     * @param seekBarNum
     * @param Params
     */
    private void initSeekbarData(int progressVal, SeekBar seekbar, TextView seekBarNum,LayoutParams Params) {
       seekbar.setProgress(progressVal);
       seekBarNum.setText(Integer.toString(progressVal));
       Params.leftMargin = progressVal * Constants.ESEEKBAR_LAYOUTPARAMS + Constants.ESEEKBAR_ADJUST_LAYOUTPARAMS;
       seekBarNum.setLayoutParams(Params);
       seekBarNum.setWidth(Constants.TEXTVIEW_WIDTH);
    }
    
    /**
     * change the UI when seekbarItem dosen't has focused
     * @param seekBar
     * @param titleTxt
     */
	private void seekbarItemUnfocused(TextView titleTxt, SeekBar seekBar) {
        titleTxt.setTextColor(android.graphics.Color.WHITE);
        seekBar.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.seekbar_progress1));
        seekBar.setThumb(getActivity().getResources().getDrawable(R.drawable.seekbar_thumb1));
    }

	/**
     * change the UI when seekbarItem has focused
     * @param seekBar
     * @param titleTxt
     */
    private void seekbarItemFocused(TextView titleTxt, SeekBar seekBar) {
        titleTxt.setTextColor(0xff0ab6a8);
        seekBar.setProgressDrawable(getActivity().getResources().getDrawable(R.drawable.seekbar_progress2));
        seekBar.setThumb(getActivity().getResources().getDrawable(R.drawable.seekbar_thumb2));
    }
}
