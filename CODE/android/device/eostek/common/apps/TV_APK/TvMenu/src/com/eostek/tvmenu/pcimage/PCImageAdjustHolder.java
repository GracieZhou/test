
package com.eostek.tvmenu.pcimage;

import com.eostek.tvmenu.R;
import com.eostek.tvmenu.advance.AdvanceSettingFragment;
import com.eostek.tvmenu.picture.SettingItem;
import com.eostek.tvmenu.sound.EquilizerFragment;
import com.eostek.tvmenu.utils.Constants;
import com.mstar.android.tv.TvPictureManager;
import com.mstar.android.tvapi.common.exception.TvCommonException;
import com.mstar.android.tvapi.common.vo.EnumSoundMode;
import com.mstar.android.tvapi.common.vo.EnumSurroundMode;
import com.mstar.android.tvapi.common.vo.MuteType.EnumMuteType;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.res.Resources;
import android.graphics.Color;
import android.provider.Settings;
import android.provider.Settings.System;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.widget.FrameLayout;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;
import android.widget.FrameLayout.LayoutParams;

public class PCImageAdjustHolder {

    private TvPictureManager mTvPictureManager;

    private PCImageAdjustFragment mFragment;

    private LinearLayout mItemPcModeClockLl;// the layout of Item

    private LinearLayout mItemPcModePhaseLl;

    private LinearLayout mItemPcModeHPositionLl;

    private LinearLayout mItemPcModeVPositionLl;

    private LinearLayout mItemPcModeAutoTuneLl;

    private TextView mPcModeClockTitleTxt;// the title of barItem

    private SeekBar mPcModeClockSeekBar;// the SeekBar of barItem

    private TextView mPcModeClockSeekBarNum;// the progress of SeekBar

    private TextView mPcModePhaseTitleTxt;

    private SeekBar mPcModePhaseSeekBar;

    private TextView mPcModePhaseSeekBarNum;

    private TextView mPcModeHPositionTitleTxt;

    private SeekBar mPcModeHPositionSeekBar;

    private TextView mPcModeHPositionSeekBarNum;

    private TextView mPcModeVPositionTitleTxt;

    private SeekBar mPcModeVPositionSeekBar;

    private TextView mPcModeVPositionSeekBarNum;

    private TextView mPcModeAutoTuneTitleTxt;

    private int clockVal;

    private int phaseVal;

    private int hPositionVal;

    private int vPositionVal;

    private FrameLayout.LayoutParams PcModeClockParams;// LayoutParams of
                                                          // PcModeClock

    private FrameLayout.LayoutParams PcModePhaseParams;// LayoutParams of
                                                          // PcModePhase

    private FrameLayout.LayoutParams PcModeHPositionparams;// LayoutParams of
                                                              // PcModeHPosition

    private FrameLayout.LayoutParams PcModeVPositionparams;// LayoutParams of
                                                              // PcModeVPosition

    private String[] mTitlePCImageAdjustStr;// string array of PCImageAdjust
                                            // title

    Resources mR;
    
    public PCImageAdjustHolder(PCImageAdjustFragment f) {
        mFragment = f;
        mR = mFragment.getActivity().getResources();
    }

    /**
	 * init the view of all items.item_titles and item_values
	 * 
	 * @param view
	 */
    protected void initView(View view) {
        mTitlePCImageAdjustStr = mFragment.getActivity().getResources().getStringArray(R.array.pc_adjust);

        mItemPcModeClockLl = (LinearLayout) view.findViewById(R.id.item_pc_mode_clock_ll);
        mPcModeClockTitleTxt = (TextView) mItemPcModeClockLl.findViewById(R.id.title_txt);
        mPcModeClockSeekBar = (SeekBar) mItemPcModeClockLl.findViewById(R.id.seekbar);
        mPcModeClockSeekBarNum = (TextView) mItemPcModeClockLl.findViewById(R.id.seekbar_number);

        mItemPcModePhaseLl = (LinearLayout) view.findViewById(R.id.item_pc_mode_phase_ll);
        mPcModePhaseTitleTxt = (TextView) mItemPcModePhaseLl.findViewById(R.id.title_txt);
        mPcModePhaseSeekBar = (SeekBar) mItemPcModePhaseLl.findViewById(R.id.seekbar);
        mPcModePhaseSeekBarNum = (TextView) mItemPcModePhaseLl.findViewById(R.id.seekbar_number);

        mItemPcModeHPositionLl = (LinearLayout) view.findViewById(R.id.item_pc_mode_h_position_ll);
        mPcModeHPositionTitleTxt = (TextView) mItemPcModeHPositionLl.findViewById(R.id.title_txt);
        mPcModeHPositionSeekBar = (SeekBar) mItemPcModeHPositionLl.findViewById(R.id.seekbar);
        mPcModeHPositionSeekBarNum = (TextView) mItemPcModeHPositionLl.findViewById(R.id.seekbar_number);

        mItemPcModeVPositionLl = (LinearLayout) view.findViewById(R.id.item_pc_mode_v_position_ll);
        mPcModeVPositionTitleTxt = (TextView) mItemPcModeVPositionLl.findViewById(R.id.title_txt);
        mPcModeVPositionSeekBar = (SeekBar) mItemPcModeVPositionLl.findViewById(R.id.seekbar);
        mPcModeVPositionSeekBarNum = (TextView) mItemPcModeVPositionLl.findViewById(R.id.seekbar_number);

        mItemPcModeAutoTuneLl = (LinearLayout) view.findViewById(R.id.item_pc_mode_auto_tune_ll);
        mPcModeAutoTuneTitleTxt = (TextView) mItemPcModeAutoTuneLl.findViewById(R.id.title_txt);

        // set the value to each title
        mPcModeClockTitleTxt.setText(mTitlePCImageAdjustStr[0]);
        mPcModePhaseTitleTxt.setText(mTitlePCImageAdjustStr[1]);
        mPcModeHPositionTitleTxt.setText(mTitlePCImageAdjustStr[2]);
        mPcModeVPositionTitleTxt.setText(mTitlePCImageAdjustStr[3]);
        mPcModeAutoTuneTitleTxt.setText(mTitlePCImageAdjustStr[4]);

        // set the value to each SeekBar
        mPcModeClockSeekBar.setProgressDrawable(mFragment.getActivity().getResources()
                .getDrawable(R.drawable.seekbar_progress1));
        mPcModeClockSeekBar.setThumb(mFragment.getActivity().getResources().getDrawable(R.drawable.seekbar_thumb1));
        mPcModeClockSeekBarNum.setTextColor(Color.WHITE);

        mPcModePhaseSeekBar.setProgressDrawable(mFragment.getActivity().getResources()
                .getDrawable(R.drawable.seekbar_progress1));
        mPcModePhaseSeekBar.setThumb(mFragment.getActivity().getResources().getDrawable(R.drawable.seekbar_thumb1));
        mPcModePhaseSeekBarNum.setTextColor(Color.WHITE);

        mPcModeHPositionSeekBar.setProgressDrawable(mFragment.getActivity().getResources()
                .getDrawable(R.drawable.seekbar_progress1));
        mPcModeHPositionSeekBar.setThumb(mFragment.getActivity().getResources().getDrawable(R.drawable.seekbar_thumb1));
        mPcModeHPositionSeekBarNum.setTextColor(Color.WHITE);

        mPcModeVPositionSeekBar.setProgressDrawable(mFragment.getActivity().getResources()
                .getDrawable(R.drawable.seekbar_progress1));
        mPcModeVPositionSeekBar.setThumb(mFragment.getActivity().getResources().getDrawable(R.drawable.seekbar_thumb1));
        mPcModeVPositionSeekBarNum.setTextColor(Color.WHITE);

    }

    /**
	 * get data from system and init the data to each item
	 */
    protected void initData() {
        mTvPictureManager = TvPictureManager.getInstance();
        
        PcModeClockParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
        		FrameLayout.LayoutParams.WRAP_CONTENT);
        PcModePhaseParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
        		FrameLayout.LayoutParams.WRAP_CONTENT);
        PcModeHPositionparams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
        		FrameLayout.LayoutParams.WRAP_CONTENT);
        PcModeVPositionparams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,
        		FrameLayout.LayoutParams.WRAP_CONTENT);

//        int[] values = mTvPictureManager.getPCImage();
//        for (int i = 0; i < 4; i++) {
//            Log.e("chensen", "values[i] = " + values[i]);
//        }
        clockVal = mTvPictureManager.getPCClock();
        phaseVal = mTvPictureManager.getPCPhase();
        hPositionVal = mTvPictureManager.getPCHPos();
        vPositionVal = mTvPictureManager.getPCVPos();
        
        Log.e("chensen","clockVal =" + clockVal);
        Log.e("chensen","phaseVal =" + phaseVal);
        Log.e("chensen","hPositionVal =" + hPositionVal);
        Log.e("chensen","vPositionVal =" + vPositionVal);
       
        if (clockVal < 0) {
            initSeekbarData(0,mPcModeClockSeekBar,mPcModeClockSeekBarNum,PcModeClockParams);
        } else {
            initSeekbarData(clockVal,mPcModeClockSeekBar,mPcModeClockSeekBarNum,PcModeClockParams);
        }
        if (phaseVal < 0) {
            initSeekbarData(0,mPcModePhaseSeekBar,mPcModePhaseSeekBarNum,PcModePhaseParams);
        } else {
        	initSeekbarData(phaseVal,mPcModePhaseSeekBar,mPcModePhaseSeekBarNum,PcModePhaseParams);
        }
        if (hPositionVal < 0) {
            initSeekbarData(0,mPcModeHPositionSeekBar,mPcModeHPositionSeekBarNum,PcModeHPositionparams);
        } else {
        	initSeekbarData(hPositionVal,mPcModeHPositionSeekBar,mPcModeHPositionSeekBarNum,PcModeHPositionparams);
        }
        if (vPositionVal < 0) {
            initSeekbarData(0,mPcModeVPositionSeekBar,mPcModeVPositionSeekBarNum,PcModeVPositionparams);
        } else {
        	initSeekbarData(vPositionVal,mPcModeVPositionSeekBar,mPcModeVPositionSeekBarNum,PcModeVPositionparams);
        }

    }

    /**
	 * set OnKeyListener and OnFocusListener
	 */
    protected void setListener() {

        OnKeyListener OnKeyListener = new OnKeyListener() {
            @Override
            public boolean onKey(View view, int keyCode, KeyEvent event) {
                switch (view.getId()) {
                    case R.id.item_pc_mode_clock_ll: {
                        if (event.getAction() == KeyEvent.ACTION_DOWN) {
                            switch (keyCode) {
                                case KeyEvent.KEYCODE_DPAD_RIGHT: {
                                    if (!view.isFocusable()) {
                                        return true;
                                    }
                                    if (clockVal >= 0 && clockVal < 100) {
                                        mPcModeClockSeekBar.setProgress(++clockVal);
                                        mTvPictureManager.setPCClock(clockVal);
                                        mPcModeClockSeekBarNum.setText(Integer.toString(clockVal));
                                        PcModeClockParams.leftMargin = clockVal * 601 / 100 - 4;
                                        mPcModeClockSeekBarNum.setLayoutParams(PcModeClockParams);
                                        mPcModeClockSeekBarNum.setWidth(70);
                                    }
                                }
                                    break;
                                case KeyEvent.KEYCODE_DPAD_LEFT: {
                                    if (!view.isFocusable()) {
                                        return true;
                                    }
                                    if (clockVal > 0 && clockVal <= 100) {
                                        mPcModeClockSeekBar.setProgress(--clockVal);
                                        mTvPictureManager.setPCClock(clockVal);
                                        mPcModeClockSeekBarNum.setText(Integer.toString(clockVal));
                                        PcModeClockParams.leftMargin = clockVal * 601 / 100 - 4;
                                        mPcModeClockSeekBarNum.setLayoutParams(PcModeClockParams);
                                        mPcModeClockSeekBarNum.setWidth(70);
                                    }
                                }
                                    break;
                            }
                        }
                    }
                        break;

                    case R.id.item_pc_mode_phase_ll: {
                        if (event.getAction() == KeyEvent.ACTION_DOWN) {
                            switch (keyCode) {
                                case KeyEvent.KEYCODE_DPAD_RIGHT: {
                                    if (!view.isFocusable()) {
                                        return true;
                                    }
                                    if (phaseVal >= 0 && phaseVal < 100) {
                                        mPcModePhaseSeekBar.setProgress(++phaseVal);
                                        mTvPictureManager.setPCPhase(phaseVal);
                                        mPcModePhaseSeekBarNum.setText(Integer.toString(phaseVal));
                                        PcModePhaseParams.leftMargin = phaseVal * 601 / 100 - 4;
                                        mPcModePhaseSeekBarNum.setLayoutParams(PcModePhaseParams);
                                    }
                                }
                                    break;
                                case KeyEvent.KEYCODE_DPAD_LEFT: {
                                    if (!view.isFocusable()) {
                                        return true;
                                    }
                                    if (phaseVal > 0 && phaseVal <= 100) {
                                        mPcModePhaseSeekBar.setProgress(--phaseVal);
                                        mTvPictureManager.setPCPhase(phaseVal);
                                        mPcModePhaseSeekBarNum.setText(Integer.toString(phaseVal));
                                        PcModePhaseParams.leftMargin = phaseVal * 601 / 100 - 4;
                                        mPcModePhaseSeekBarNum.setLayoutParams(PcModePhaseParams);
                                    }
                                }
                                    break;
                            }
                        }
                    }
                        break;

                    case R.id.item_pc_mode_h_position_ll: {
                        if (event.getAction() == KeyEvent.ACTION_DOWN) {
                            switch (keyCode) {
                                case KeyEvent.KEYCODE_DPAD_RIGHT: {
                                    if (!view.isFocusable()) {
                                        return true;
                                    }
                                    if (hPositionVal >= 0 && hPositionVal < 100) {
                                        mPcModeHPositionSeekBar.setProgress(++hPositionVal);
                                        mTvPictureManager.setPCHPos(hPositionVal);
                                        mPcModeHPositionSeekBarNum.setText(Integer.toString(hPositionVal));
                                        PcModeHPositionparams.leftMargin = hPositionVal * 601 / 100 - 4;
                                        mPcModeHPositionSeekBarNum.setLayoutParams(PcModeHPositionparams);
                                    }
                                }
                                    break;
                                case KeyEvent.KEYCODE_DPAD_LEFT: {
                                    if (!view.isFocusable()) {
                                        return true;
                                    }
                                    if (hPositionVal > 0 && hPositionVal <= 100) {
                                        mPcModeHPositionSeekBar.setProgress(--hPositionVal);
                                        mTvPictureManager.setPCHPos(hPositionVal);
                                        mPcModeHPositionSeekBarNum.setText(Integer.toString(hPositionVal));
                                        PcModeHPositionparams.leftMargin = hPositionVal * 601 / 100 - 4;
                                        mPcModeHPositionSeekBarNum.setLayoutParams(PcModeHPositionparams);
                                    }
                                }
                                    break;
                            }
                        }
                    }
                        break;

                    case R.id.item_pc_mode_v_position_ll: {
                        if (event.getAction() == KeyEvent.ACTION_DOWN) {
                            switch (keyCode) {
                                case KeyEvent.KEYCODE_DPAD_RIGHT: {
                                    if (!view.isFocusable()) {
                                        return true;
                                    }
                                    if (vPositionVal >= 0 && vPositionVal < 100) {
                                        mPcModeVPositionSeekBar.setProgress(++vPositionVal);
                                        mTvPictureManager.setPCVPos(vPositionVal);
                                        mPcModeVPositionSeekBarNum.setText(Integer.toString(vPositionVal));
                                        PcModeVPositionparams.leftMargin = vPositionVal * 601 / 100 - 4;
                                        mPcModeVPositionSeekBarNum.setLayoutParams(PcModeVPositionparams);
                                    }
                                }
                                    break;
                                case KeyEvent.KEYCODE_DPAD_LEFT: {
                                    if (!view.isFocusable()) {
                                        return true;
                                    }
                                    if (vPositionVal > 0 && vPositionVal <= 100) {
                                        mPcModeVPositionSeekBar.setProgress(--vPositionVal);
                                        mTvPictureManager.setPCVPos(vPositionVal);
                                        mPcModeVPositionSeekBarNum.setText(Integer.toString(vPositionVal));
                                        PcModeVPositionparams.leftMargin = vPositionVal * 601 / 100 - 4;
                                        mPcModeVPositionSeekBarNum.setLayoutParams(PcModeVPositionparams);
                                    }

                                }
                                    break;
                            }
                        }
                    }
                        break;

                    case R.id.item_pc_mode_auto_tune_ll: {
                        if (event.getAction() == KeyEvent.ACTION_DOWN) {
                            switch (keyCode) {
                                case KeyEvent.KEYCODE_ENTER:
                                case KeyEvent.KEYCODE_DPAD_LEFT:
                                case KeyEvent.KEYCODE_DPAD_RIGHT: {
                                    if (!view.isFocusable()) {
                                        return true;
                                    }

                                    mFragment.mLogic.showAutoTuneDialog();
                                }
                                    break;

                            }
                        }
                    }
                        break;

                }
                return false;
            }
        };

        // set Items OnFocusChangeListener
        mItemPcModeClockLl.setOnKeyListener(OnKeyListener);
        mItemPcModePhaseLl.setOnKeyListener(OnKeyListener);
        mItemPcModeHPositionLl.setOnKeyListener(OnKeyListener);
        mItemPcModeVPositionLl.setOnKeyListener(OnKeyListener);
        mItemPcModeAutoTuneLl.setOnKeyListener(OnKeyListener);

        OnFocusChangeListener onFocusChangeListener = new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View view, boolean haiFocus) {

                switch (view.getId()) {
                    case R.id.item_pc_mode_clock_ll: {
                        if (haiFocus) {
                            // change Title,Background to blue
                            seekbarItemFocused(mPcModeClockTitleTxt,mPcModeClockSeekBar);
                        } else {
                            // change Title,Background to grey
                        	seekbarItemUnfocused(mPcModeClockTitleTxt,mPcModeClockSeekBar);
                        }
                    }
                        break;
                    case R.id.item_pc_mode_phase_ll: {
                        if (haiFocus) {
                            // change Title,seekBar and thumb to blue
                            seekbarItemFocused(mPcModePhaseTitleTxt,mPcModePhaseSeekBar);
                        } else {
                            // change Title,seekBar and thumb to grey
                        	seekbarItemUnfocused(mPcModePhaseTitleTxt,mPcModePhaseSeekBar);
                        }
                    }
                        break;
                    case R.id.item_pc_mode_h_position_ll: {
                        if (haiFocus) {
                            seekbarItemFocused(mPcModeHPositionTitleTxt,mPcModeHPositionSeekBar);
                        } else {
                        	seekbarItemUnfocused(mPcModeHPositionTitleTxt,mPcModeHPositionSeekBar);
                        }
                    }
                        break;
                    case R.id.item_pc_mode_v_position_ll: {
                        if (haiFocus) {
                            seekbarItemFocused(mPcModeVPositionTitleTxt,mPcModeVPositionSeekBar);
                        } else {
                        	seekbarItemUnfocused(mPcModeVPositionTitleTxt,mPcModeVPositionSeekBar);
                        }
                    }
                        break;
                    case R.id.item_pc_mode_auto_tune_ll: {
                        if (haiFocus) {
                            mPcModeAutoTuneTitleTxt.setTextColor(mR.getColor(R.color.cyan));
                            view.findViewById(R.id.button_context).setBackgroundResource(R.drawable.bar_bg_btn_cyan);
                        } else {
                            mPcModeAutoTuneTitleTxt.setTextColor(android.graphics.Color.WHITE);
                            view.findViewById(R.id.button_context).setBackgroundResource(R.drawable.bar_bg_btn_grey);
                        }
                    }
                        break;

                }
            }
        };

        // set Items OnFocusChangeListener
        mItemPcModeClockLl.setOnFocusChangeListener(onFocusChangeListener);
        mItemPcModePhaseLl.setOnFocusChangeListener(onFocusChangeListener);
        mItemPcModeHPositionLl.setOnFocusChangeListener(onFocusChangeListener);
        mItemPcModeVPositionLl.setOnFocusChangeListener(onFocusChangeListener);
        mItemPcModeAutoTuneLl.setOnFocusChangeListener(onFocusChangeListener);

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
	   seekBarNum.setWidth(70);
	}
	
	/**
	 *  set ui of seekbarItem when adjusting the picture
	 */
	void updateSeekbarUi() {
		clockVal = mTvPictureManager.getPCClock();
        phaseVal = mTvPictureManager.getPCPhase();
        hPositionVal = mTvPictureManager.getPCHPos();
        vPositionVal = mTvPictureManager.getPCVPos();
        
        Log.e("chensen","clockVal =" + clockVal);
        Log.e("chensen","phaseVal =" + phaseVal);
        Log.e("chensen","hPositionVal =" + hPositionVal);
        Log.e("chensen","vPositionVal =" + vPositionVal);
       
        if (clockVal < 0) {
        	updateSeekbarData(0,mPcModeClockSeekBar,mPcModeClockSeekBarNum,PcModeClockParams);
        } else {
        	updateSeekbarData(clockVal,mPcModeClockSeekBar,mPcModeClockSeekBarNum,PcModeClockParams);
        }
        if (phaseVal < 0) {
        	updateSeekbarData(0,mPcModePhaseSeekBar,mPcModePhaseSeekBarNum,PcModePhaseParams);
        } else {
        	updateSeekbarData(phaseVal,mPcModePhaseSeekBar,mPcModePhaseSeekBarNum,PcModePhaseParams);
        }
        if (hPositionVal < 0) {
        	updateSeekbarData(0,mPcModeHPositionSeekBar,mPcModeHPositionSeekBarNum,PcModeHPositionparams);
        } else {
        	updateSeekbarData(hPositionVal,mPcModeHPositionSeekBar,mPcModeHPositionSeekBarNum,PcModeHPositionparams);
        }
        if (vPositionVal < 0) {
        	updateSeekbarData(0,mPcModeVPositionSeekBar,mPcModeVPositionSeekBarNum,PcModeVPositionparams);
        } else {
        	updateSeekbarData(vPositionVal,mPcModeVPositionSeekBar,mPcModeVPositionSeekBarNum,PcModeVPositionparams);
        }
	}
	
	 /**
		 * update data and set ui of seekbarItem
		 * @param progressVal
		 * @param seekbar
		 * @param seekBarNum
		 * @param Params
		 */
		private void updateSeekbarData(int progressVal, SeekBar seekbar, TextView seekBarNum,LayoutParams Params) {
			seekbar.setProgress(progressVal);
			seekBarNum.setText(Integer.toString(progressVal));
			Params.leftMargin = progressVal * Constants.ESEEKBAR_LAYOUTPARAMS + Constants.ESEEKBAR_ADJUST_LAYOUTPARAMS;
			seekBarNum.setLayoutParams(Params);
			seekBarNum.setWidth(70);
		}
    
    /**
     * change the UI when seekbarItem dosen't has focused
     * @param view
     * @param titleTxt
     */
	private void seekbarItemUnfocused(TextView titleTxt, SeekBar seekBar) {
        titleTxt.setTextColor(android.graphics.Color.WHITE);
        seekBar.setProgressDrawable(mFragment.getActivity().getResources().getDrawable(R.drawable.seekbar_progress1));
        seekBar.setThumb(mFragment.getActivity().getResources().getDrawable(R.drawable.seekbar_thumb1));
    }

	/**
     * change the UI when seekbarItem has focused
     * @param view
     * @param titleTxt
     */
    private void seekbarItemFocused(TextView titleTxt, SeekBar seekBar) {
        titleTxt.setTextColor(mR.getColor(R.color.cyan));
        seekBar.setProgressDrawable(mFragment.getActivity().getResources().getDrawable(R.drawable.seekbar_progress2));
        seekBar.setThumb(mFragment.getActivity().getResources().getDrawable(R.drawable.seekbar_thumb2));
    }

}
