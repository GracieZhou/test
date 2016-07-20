package com.eostek.tvmenu.sound;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Color;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.provider.Settings.System;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;
import android.widget.LinearLayout;
import android.widget.SeekBar;
import android.widget.TextView;

import com.eostek.tvmenu.R;
import com.eostek.tvmenu.utils.Constants;
import com.mstar.android.tv.TvAudioManager;
import com.mstar.android.tv.TvFactoryManager;
import com.mstar.android.tvapi.common.AudioManager;
import com.mstar.android.tvapi.common.TvManager;
import com.mstar.android.tvapi.common.exception.TvCommonException;
import com.mstar.android.tvapi.common.vo.EnumSoundMode;
import com.mstar.android.tvapi.common.vo.EnumSpdifType;
import com.mstar.android.tvapi.common.vo.MuteType.EnumMuteType;

public class AudioSettingHolder {

    private static final String TAG = "AudioSettingHolder";
    
	//the layout of Item
	private LinearLayout mItemSoundModeLl;

	private LinearLayout mItemLowPitchLl;

	private LinearLayout mItemHighLl;

	private LinearLayout mItemEquilizerLl;

	private LinearLayout mItemBalanceLl;

	private LinearLayout mItemAvcLl;

	private LinearLayout mItemSurroundLl;

	private LinearLayout mItemInteralSpeakerLl;

	private LinearLayout mItemExtSpeakerLl;

	private LinearLayout mItemNotificationSoundLl;
	
	private LinearLayout mItemPowerOnMusicLl;
	
	private LinearLayout mItemSpdifOutputLl;
	
	private LinearLayout mItemSeperateHearLl;

	//the title of  enumItemmSoundModeTitleTxt
	private TextView mSoundModeTitleTxt;
	//the value of enumItem
	private TextView mSoundModeContentTxt;

	//the title of  barItem
	private TextView mLowPitchTitleTxt;
	//the SeekBar of  barItem
	private SeekBar mLowPitchSeekBar;
	//the progress of SeekBar
	private TextView mLowPitchSeekBarNum;

	private TextView mHighTitleTxt;
	private SeekBar mHighSeekBar;
	private TextView mHighSeekBarNum;

	//the title of  buttonItem
	private TextView mEquilizerTitleTxt;
	private TextView mSeperateHearTitleTxt;

	private TextView mBalanceTitleTxt;
	private SeekBar mBalanceSeekBar;
	private TextView mBalanceSeekBarNum;

	private TextView mAvcTitleTxt;
	private TextView mAvcContentTxt;

	private TextView mSurroundTitleTxt;
	private TextView mSurroundContentTxt;

	private TextView mInteralSpeakerTitleTxt;
	private TextView mInteralSpeakerContentTxt;

	private TextView mExtSpeakerTitleTxt;
	private TextView mExtSpeakerContentTxt;

	private TextView mNotificationSoundTitleTxt;
	private TextView mNotificationSoundContentTxt;
	
	private TextView mPowerOnMusicTitleTxt;
    private TextView mPowerOnMusicContentTxt;
    
    private TextView mSpdifOutputTitleTxt;
    private TextView mSpdifOutputContentTxt;

	private int lowPitchVal;
	private int HighVal;
	//Balance value get from tvManager
	private int BalanceVal;
	//Balance value to show
	private int BalanceNum;
	private int mCurSoundMode;
	private int SurroundModeVal;
	private int volume = 0;
	private int mPowerOnMusicVal;//0 代表关，1代表开
	private int mSpdifMode;
	
	//Avc Bool value get from tvManager
	private Boolean AvcBool;
	//interalSpeaker Bool value get from tvManager
	private Boolean interalSpeakerBool;
	//extSpeaker Bool value get from tvManager
	private Boolean extSpeakerBool;

	//string array of AudioSetting title
	private String[] mTitleAudioSettingStr;
	//string array of SoundMode
	private String[] mSoundModeStr;
	//string array of on/off
	private String[] mTurnOn_OffStr;
	//string array of spdif
	private String[] mSpdifStatusStr;

	//LayoutParams of lowPitch
	private FrameLayout.LayoutParams lowPitchParams;
	//LayoutParams of highPitch
	private FrameLayout.LayoutParams highPitchParams;
	//LayoutParams of balance
	private FrameLayout.LayoutParams balanceparams;

	private TvAudioManager mTvAudioManager;
	
	private AudioManager mAudioManager = null;
	
	private TvManager mTvManager;
	
	private AudioSettingFragment mFragment;
	
	Resources mR;

	public AudioSettingHolder(AudioSettingFragment f) {
		mFragment = f;
		mR = mFragment.getActivity().getResources();
	}

	/**
	 * init the view of all items.item_titles and item_values
	 * 
	 * @param view
	 */
	protected void initView(View view) {
		//init string array
		mTitleAudioSettingStr = mFragment.getActivity().getResources().getStringArray(R.array.setting_audio);
		mSoundModeStr = mFragment.getActivity().getResources().getStringArray(R.array.setting_soundmode_vals);
		mTurnOn_OffStr = mFragment.getActivity().getResources().getStringArray(R.array.turnon_off);
		mSpdifStatusStr = mFragment.getActivity().getResources().getStringArray(R.array.spdif_status);
		
		//SoundMode
		mItemSoundModeLl = (LinearLayout) view.findViewById(R.id.mItemSoundMode);
		mSoundModeTitleTxt = (TextView) mItemSoundModeLl.findViewById(R.id.title_txt);
		mSoundModeContentTxt = (TextView) mItemSoundModeLl.findViewById(R.id.value);
		
		//LowPitch
		mItemLowPitchLl = (LinearLayout) view.findViewById(R.id.mItemLowPitch);
		mLowPitchTitleTxt = (TextView) mItemLowPitchLl.findViewById(R.id.title_txt);
		mLowPitchSeekBar = (SeekBar) mItemLowPitchLl.findViewById(R.id.seekbar);
		mLowPitchSeekBarNum = (TextView) mItemLowPitchLl.findViewById(R.id.seekbar_number);

		//HighPitch
		mItemHighLl = (LinearLayout) view.findViewById(R.id.mItemHigh);
		mHighTitleTxt = (TextView) mItemHighLl.findViewById(R.id.title_txt);
		mHighSeekBar = (SeekBar) mItemHighLl.findViewById(R.id.seekbar);
		mHighSeekBarNum = (TextView) mItemHighLl.findViewById(R.id.seekbar_number);

		//Equilizer
		mItemEquilizerLl = (LinearLayout) view.findViewById(R.id.mItemEquilizer);
		mEquilizerTitleTxt = (TextView) mItemEquilizerLl.findViewById(R.id.title_txt);

		//Balance
		mItemBalanceLl = (LinearLayout) view.findViewById(R.id.mItemBalance);
		mBalanceTitleTxt = (TextView) mItemBalanceLl.findViewById(R.id.title_txt);
		mBalanceSeekBar = (SeekBar) mItemBalanceLl.findViewById(R.id.seekbar);
		mBalanceSeekBarNum = (TextView) mItemBalanceLl.findViewById(R.id.seekbar_number);

		//Avc
		mItemAvcLl = (LinearLayout) view.findViewById(R.id.mItemAvc);
		mAvcTitleTxt = (TextView) mItemAvcLl.findViewById(R.id.title_txt);
		mAvcContentTxt = (TextView) mItemAvcLl.findViewById(R.id.value);

		//Surround
		mItemSurroundLl = (LinearLayout) view.findViewById(R.id.mItemSurround);
		mSurroundTitleTxt = (TextView) mItemSurroundLl.findViewById(R.id.title_txt);
		mSurroundContentTxt = (TextView) mItemSurroundLl.findViewById(R.id.value);

		//SPDIF Output
        mItemSpdifOutputLl = (LinearLayout) view.findViewById(R.id.mItemSpidfOutput);
        mSpdifOutputTitleTxt = (TextView) mItemSpdifOutputLl.findViewById(R.id.title_txt);
        mSpdifOutputContentTxt = (TextView) mItemSpdifOutputLl.findViewById(R.id.value);
		
		//InteralSpeaker
		mItemInteralSpeakerLl = (LinearLayout) view.findViewById(R.id.mItemInteralSpeaker);
		mInteralSpeakerTitleTxt = (TextView) mItemInteralSpeakerLl.findViewById(R.id.title_txt);
		mInteralSpeakerContentTxt = (TextView) mItemInteralSpeakerLl.findViewById(R.id.value);

		//ExtSpeaker
		mItemExtSpeakerLl = (LinearLayout) view.findViewById(R.id.mItemExtSpeaker);
		mExtSpeakerTitleTxt = (TextView) mItemExtSpeakerLl.findViewById(R.id.title_txt);
		mExtSpeakerContentTxt = (TextView) mItemExtSpeakerLl.findViewById(R.id.value);

		//NotificationSound
		mItemNotificationSoundLl = (LinearLayout) view.findViewById(R.id.mItemNotificationSound);
		mNotificationSoundTitleTxt = (TextView) mItemNotificationSoundLl.findViewById(R.id.title_txt);
		mNotificationSoundContentTxt = (TextView) mItemNotificationSoundLl.findViewById(R.id.value);
		
		//PowerOnMusic
		mItemPowerOnMusicLl = (LinearLayout) view.findViewById(R.id.mItemPowerOnMusic);
		mPowerOnMusicTitleTxt = (TextView) mItemPowerOnMusicLl.findViewById(R.id.title_txt);
		mPowerOnMusicContentTxt = (TextView) mItemPowerOnMusicLl.findViewById(R.id.value);
		
		//SeperateHear
        mItemSeperateHearLl = (LinearLayout) view.findViewById(R.id.mItemSeperateHear);
        mSeperateHearTitleTxt = (TextView) mItemSeperateHearLl.findViewById(R.id.title_txt);

		//set the value to each title
		mSoundModeTitleTxt.setText(mTitleAudioSettingStr[Constants.SOUND_MODE_TITLE]);
		mLowPitchTitleTxt.setText(mTitleAudioSettingStr[Constants.LOW_PITCH_TITLE]);
		mHighTitleTxt.setText(mTitleAudioSettingStr[Constants.HIGH_TITLE]);
		mEquilizerTitleTxt.setText(mTitleAudioSettingStr[Constants.EQUILIZER_TITLE]);
		mBalanceTitleTxt.setText(mTitleAudioSettingStr[Constants.BALANCE_TITLE]);
		mAvcTitleTxt.setText(mTitleAudioSettingStr[Constants.AVC_TITLE]);
		mSurroundTitleTxt.setText(mTitleAudioSettingStr[Constants.SURROUND_TITLE]);
		mSpdifOutputTitleTxt.setText(mTitleAudioSettingStr[Constants.SPDIF_TITLE]);
		mInteralSpeakerTitleTxt.setText(mTitleAudioSettingStr[Constants.INTERAL_SPEAKER_TITLE]);
		mExtSpeakerTitleTxt.setText(mTitleAudioSettingStr[Constants.EXT_SPEAKER_TITLE]);
		mNotificationSoundTitleTxt.setText(mTitleAudioSettingStr[Constants.NOTIFICATION_SOUNDT_ITLE]);
		mPowerOnMusicTitleTxt.setText(mTitleAudioSettingStr[Constants.POWER_ON_MUSIC_TITLE]);
		mSeperateHearTitleTxt.setText(mTitleAudioSettingStr[Constants.SEPERATE_HEART_TITLE]);

		//set the value to each SeekBar
		mLowPitchSeekBar.setProgressDrawable(mFragment.getActivity().getResources().getDrawable(R.drawable.seekbar_progress1));
		mLowPitchSeekBar.setThumb(mFragment.getActivity().getResources().getDrawable(R.drawable.seekbar_thumb1));
		mLowPitchSeekBarNum.setTextColor(Color.WHITE);

		mHighSeekBar.setProgressDrawable(mFragment.getActivity().getResources().getDrawable(R.drawable.seekbar_progress1));
		mHighSeekBar.setThumb(mFragment.getActivity().getResources().getDrawable(R.drawable.seekbar_thumb1));
		mHighSeekBarNum.setTextColor(Color.WHITE);

		mBalanceSeekBar.setProgressDrawable(mFragment.getActivity().getResources().getDrawable(R.drawable.seekbar_progress1));
		mBalanceSeekBar.setThumb(mFragment.getActivity().getResources().getDrawable(R.drawable.seekbar_thumb1));
		mBalanceSeekBarNum.setTextColor(Color.WHITE);
	}

    /**
	 * get data from system and init the data to each item
	 */
	protected void initData() {
		mTvAudioManager = TvAudioManager.getInstance();
		mAudioManager = TvManager.getInstance().getAudioManager();
		mTvManager = TvManager.getInstance();

		//init layoutParams
		lowPitchParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,FrameLayout.LayoutParams.WRAP_CONTENT);
		highPitchParams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,FrameLayout.LayoutParams.WRAP_CONTENT);
		balanceparams = new FrameLayout.LayoutParams(FrameLayout.LayoutParams.WRAP_CONTENT,FrameLayout.LayoutParams.WRAP_CONTENT);

		//init the lowPitch value
		lowPitchVal = mTvAudioManager.getBass();
		initSeekbarData(lowPitchVal,mLowPitchSeekBar,mLowPitchSeekBarNum,lowPitchParams);
		
		//init the HighPitch value
		HighVal = mTvAudioManager.getTreble();
		initSeekbarData(HighVal,mHighSeekBar,mHighSeekBarNum,highPitchParams);
		
		//init the Balancenum value,show it to user
		BalanceNum = mTvAudioManager.getBalance();
		BalanceVal = mTvAudioManager.getBalance() - 50;//get the Balance value,set it to system
		mBalanceSeekBar.setProgress(BalanceNum);
		mBalanceSeekBarNum.setText(Integer.toString(BalanceVal));
		balanceparams.leftMargin = BalanceNum * Constants.ESEEKBAR_LAYOUTPARAMS + Constants.ESEEKBAR_ADJUST_LAYOUTPARAMS;
		mBalanceSeekBarNum.setLayoutParams(balanceparams);
		mBalanceSeekBarNum.setWidth(70);

		//init the SoundModeVal from TvAudioManager
		mCurSoundMode = mTvAudioManager.getAudioSoundMode();
		//get the SurroundModeVal from TvAudioManager
		SurroundModeVal = mTvAudioManager.getAudioSurroundMode();
		mSoundModeContentTxt.setText(mSoundModeStr[mCurSoundMode]);
		mSurroundContentTxt.setText(mTurnOn_OffStr[SurroundModeVal]);

		//init Spdif Output
		mSpdifMode = mTvAudioManager.getSpdifOutMode().ordinal();
		Log.i(TAG, "initmSpdifMode = " + mSpdifMode);
		if (mSpdifMode == EnumSpdifType.E_PCM.ordinal()) {
            mSpdifOutputContentTxt.setText(mSpdifStatusStr[0]);
        } else if (mSpdifMode == EnumSpdifType.E_NONPCM.ordinal()) {
            mSpdifOutputContentTxt.setText(mSpdifStatusStr[2]);
        } else {
            mSpdifOutputContentTxt.setText(mSpdifStatusStr[1]);
        }
		
		//init Avc,interalSpeaker and extSpeaker value
		AvcBool = mTvAudioManager.getAvcMode();
		
//		interalSpeakerBool = System.getInt(mFragment.getActivity().getContentResolver(), "SPEAKER", 1) == 0 ? false : true;
		try {
            interalSpeakerBool = mTvManager.getEnvironment("interalSpeaker").equals("off");
            Log.i("AudioSetting", "interalSpeakerBool = " + interalSpeakerBool);
        } catch (TvCommonException e1) {
            e1.printStackTrace();
        }
		
		extSpeakerBool = System.getInt(mFragment.getActivity().getContentResolver(), "HP", 1) == 0 ? false : true;
		
		//Avc
		if (AvcBool) {
			mAvcContentTxt.setText(mTurnOn_OffStr[Constants.AVC_ON]);
		} else {
			mAvcContentTxt.setText(mTurnOn_OffStr[Constants.AVC_OFF]);
		}
		
		//interalSpeaker
		if (!interalSpeakerBool) {
			mInteralSpeakerContentTxt.setText(mTurnOn_OffStr[Constants.INTERAL_SPEAKER_ON]);
		} else {
			mInteralSpeakerContentTxt.setText(mTurnOn_OffStr[Constants.INTERAL_SPEAKER_OFF]);
		}
		
		//extSpeaker
		try {
            if (mTvManager.getGpioDeviceStatus(57) == 0) {
            	mExtSpeakerContentTxt.setText(mTurnOn_OffStr[Constants.EXT_SPEAKER_OFF]);
            	Log.e("rick","init extSpeaker off");
            } else {
            	mExtSpeakerContentTxt.setText(mTurnOn_OffStr[Constants.EXT_SPEAKER_ON]);
            	Log.e("rick","init extSpeaker on");
            }
        } catch (TvCommonException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
		
		//systemNotificationSound
//		try {
//			volume = Settings.System.getInt(mFragment.getActivity().getContentResolver(), "sound_effects_enabled");
//			volume = Settings.System.getInt(mFragment.getActivity().getContentResolver(), Settings.System.NOTIFICATION_SOUND);//get the system volume value
//		} catch (SettingNotFoundException e) {
//			e.printStackTrace();
//		}
		
		boolean isNotificationMute = TextUtils.isEmpty(Settings.System.getString(mFragment.getActivity().getContentResolver(),
                Settings.System.NOTIFICATION_SOUND)) ? false : true;

		if (isNotificationMute) {
			mNotificationSoundContentTxt.setText(mTurnOn_OffStr[Constants.NOTIFICATION_SOUND_ON]);
		} else {
			mNotificationSoundContentTxt.setText(mTurnOn_OffStr[Constants.NOTIFICATION_SOUND_OFF]);
		}
		
//		//PowerOnMusic
//		mPowerOnMusicVal = Settings.System.getInt(mFragment.getActivity().getContentResolver(), "powerOnMusic", 0);
//		
//		if(mPowerOnMusicVal == Constants.POWER_ON_MUSIC_OFF){
//		    mPowerOnMusicContentTxt.setText(mTurnOn_OffStr[Constants.POWER_ON_MUSIC_OFF]);
//		}else if(mPowerOnMusicVal == Constants.POWER_ON_MUSIC_ON){
//		    mPowerOnMusicContentTxt.setText(mTurnOn_OffStr[Constants.POWER_ON_MUSIC_ON]);
//		}
		mPowerOnMusicVal = TvFactoryManager.getInstance().getPowerOnMusicMode();
		Log.i("test","the ordinal mPowerOnMusicVal is"+mPowerOnMusicVal);
		if(mPowerOnMusicVal==Constants.POWER_ON_MUSIC_OFF){
		    mPowerOnMusicContentTxt.setText(mTurnOn_OffStr[Constants.POWER_ON_MUSIC_OFF]);
		}else if(mPowerOnMusicVal == Constants.POWER_ON_MUSIC_ON){
       mPowerOnMusicContentTxt.setText(mTurnOn_OffStr[Constants.POWER_ON_MUSIC_ON]);
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
				case R.id.mItemSoundMode: {
					if (event.getAction() == KeyEvent.ACTION_DOWN) {
						switch (keyCode) {
						case KeyEvent.KEYCODE_DPAD_RIGHT: {
							if (!view.isFocusable()) {
								return true;
							}
							// 0:E_STANDARD 1:E_MUSIC 2:E_MOVIE 3:E_SPORTS 4:E_USER 5:E_ONSITE1 6:E_ONSITE2
							if (mCurSoundMode == Constants.STANDARD) {
								mSoundModeContentTxt.setText(mSoundModeStr[++mCurSoundMode]);
								//set SoundMode to MUSIC
								mTvAudioManager.setAudioSoundMode(Constants.MUSIC);
								//update the lowPitch value and highpitch  value
								updateView();
							} else if (mCurSoundMode == Constants.MUSIC) {
								mSoundModeContentTxt.setText(mSoundModeStr[++mCurSoundMode]);
								mTvAudioManager.setAudioSoundMode(Constants.MOVIE);
								updateView();
							} else if (mCurSoundMode == Constants.MOVIE) {
								mSoundModeContentTxt.setText(mSoundModeStr[++mCurSoundMode]);
								mTvAudioManager.setAudioSoundMode(Constants.SPORTS);
								updateView();
							} else if (mCurSoundMode == Constants.SPORTS) {
								mSoundModeContentTxt.setText(mSoundModeStr[++mCurSoundMode]);
								mTvAudioManager.setAudioSoundMode(Constants.USER);
								updateView();
							} else if (mCurSoundMode == Constants.USER) {
								mSoundModeContentTxt.setText(mSoundModeStr[++mCurSoundMode]);
								mTvAudioManager.setAudioSoundMode(Constants.ONSITE1);
								updateView();
							} else if (mCurSoundMode == Constants.ONSITE1) {
								mSoundModeContentTxt.setText(mSoundModeStr[++mCurSoundMode]);
								mTvAudioManager.setAudioSoundMode(Constants.ONSITE2);
								updateView();
							} else if (mCurSoundMode == Constants.ONSITE2) {
								mSoundModeContentTxt.setText(mSoundModeStr[Constants.STANDARD]);
								mTvAudioManager.setAudioSoundMode(Constants.STANDARD);
								mCurSoundMode = Constants.STANDARD;
								updateView();
							}
						}
							return true;
						case KeyEvent.KEYCODE_DPAD_LEFT: {
							if (!view.isFocusable()) {
								return true;
							}
							if (mCurSoundMode == Constants.ONSITE2) {
								mSoundModeContentTxt.setText(mSoundModeStr[--mCurSoundMode]);
								mTvAudioManager.setAudioSoundMode(Constants.ONSITE1);
								updateView();
							} else if (mCurSoundMode == Constants.ONSITE1) {
								mSoundModeContentTxt.setText(mSoundModeStr[--mCurSoundMode]);
								mTvAudioManager.setAudioSoundMode(Constants.USER);
								updateView();
							} else if (mCurSoundMode == Constants.USER) {
								mSoundModeContentTxt.setText(mSoundModeStr[--mCurSoundMode]);
								mTvAudioManager.setAudioSoundMode(Constants.SPORTS);
								updateView();
							} else if (mCurSoundMode == Constants.SPORTS) {
								mSoundModeContentTxt.setText(mSoundModeStr[--mCurSoundMode]);
								mTvAudioManager.setAudioSoundMode(Constants.MOVIE);
								updateView();
							} else if (mCurSoundMode == Constants.MOVIE) {
								mSoundModeContentTxt.setText(mSoundModeStr[--mCurSoundMode]);
								mTvAudioManager.setAudioSoundMode(Constants.MUSIC);
								updateView();
							} else if (mCurSoundMode == Constants.MUSIC) {
								mSoundModeContentTxt.setText(mSoundModeStr[--mCurSoundMode]);
								mTvAudioManager.setAudioSoundMode(Constants.STANDARD);
								updateView();
							} else if (mCurSoundMode == Constants.STANDARD) {
								mSoundModeContentTxt.setText(mSoundModeStr[Constants.ONSITE2]);
								mTvAudioManager.setAudioSoundMode(Constants.ONSITE2);
								mCurSoundMode = Constants.ONSITE2;
								updateView();
							}

						}
						break;
						}
					}
				}
				break;

				case R.id.mItemLowPitch: {
					if (event.getAction() == KeyEvent.ACTION_DOWN) {
						switch (keyCode) {
						case KeyEvent.KEYCODE_DPAD_RIGHT: {
							if (!view.isFocusable()) {
								return true;
							}
							if (lowPitchVal >= 0 && lowPitchVal < 100) {
								lowPitchVal++;
	                    		updateSeekbarUi(lowPitchVal,mLowPitchSeekBar,mLowPitchSeekBarNum,lowPitchParams);
							}
							mTvAudioManager.setSoundModeAll(EnumSoundMode.E_USER.ordinal(),lowPitchVal, mTvAudioManager.getTreble());
							//if change the lowPitch value,the soundMode will change to User
							changeSoundModetoUser();
						}
							break;
						case KeyEvent.KEYCODE_DPAD_LEFT: {
							if (!view.isFocusable()) {
								return true;
							}
							if (lowPitchVal > 0 && lowPitchVal <= 100) {
								lowPitchVal--;
	                    		updateSeekbarUi(lowPitchVal,mLowPitchSeekBar,mLowPitchSeekBarNum,lowPitchParams);
							}
							mTvAudioManager.setSoundModeAll(EnumSoundMode.E_USER.ordinal(),lowPitchVal, mTvAudioManager.getTreble());

							changeSoundModetoUser();
						}
						break;
						}
					}
				}
				break;

				case R.id.mItemHigh: {
					if (event.getAction() == KeyEvent.ACTION_DOWN) {
						switch (keyCode) {
						case KeyEvent.KEYCODE_DPAD_RIGHT: {
							if (!view.isFocusable()) {
								return true;
							}
							if (HighVal >= 0 && HighVal < 100) {
								HighVal++;
	                    		updateSeekbarUi(HighVal,mHighSeekBar,mHighSeekBarNum,highPitchParams);
							}
							mTvAudioManager.setSoundModeAll(EnumSoundMode.E_USER.ordinal(),mTvAudioManager.getBass(), HighVal);
							//if change the highPitch value,the soundMode will change to User
							changeSoundModetoUser();
						}
							break;
						case KeyEvent.KEYCODE_DPAD_LEFT: {
							if (!view.isFocusable()) {
								return true;
							}
							if (HighVal > 0 && HighVal <= 100) {
								HighVal--;
	                    		updateSeekbarUi(HighVal,mHighSeekBar,mHighSeekBarNum,highPitchParams);
							}
							mTvAudioManager.setSoundModeAll(EnumSoundMode.E_USER.ordinal(),mTvAudioManager.getBass(), HighVal);

							changeSoundModetoUser();
						}
							break;
						}
					}
				}
					break;

				case R.id.mItemEquilizer: {
					if (event.getAction() == KeyEvent.ACTION_DOWN) {
						switch (keyCode) {
						case KeyEvent.KEYCODE_ENTER:
						case KeyEvent.KEYCODE_DPAD_LEFT:
						case KeyEvent.KEYCODE_DPAD_RIGHT: {
							if (!view.isFocusable()) {
								return true;
							}
							//start EquilizerFragment
							EquilizerFragment equilizer = new EquilizerFragment();
							FragmentManager fm = mFragment.getFragmentManager();
							// fm.popBackStack();
							FragmentTransaction ft = fm.beginTransaction();
							ft.setCustomAnimations(R.anim.menu_anim_rightin,R.anim.menu_anim_leftout);
							ft.replace(R.id.content_setting, equilizer);
							ft.commit();
						}
						break;

						}
					}
				}
				break;

				case R.id.mItemBalance: {
					if (event.getAction() == KeyEvent.ACTION_DOWN) {
						switch (keyCode) {
						case KeyEvent.KEYCODE_DPAD_RIGHT: {
							if (!view.isFocusable()) {
								return true;
							}
							if (BalanceVal >= -50 && BalanceVal < 50) {
								mBalanceSeekBar.setProgress(++BalanceNum);
								mTvAudioManager.setBalance(BalanceNum);
								BalanceVal++;
								mBalanceSeekBarNum.setText(Integer.toString(BalanceVal));
								balanceparams.leftMargin = BalanceNum * Constants.ESEEKBAR_LAYOUTPARAMS + Constants.ESEEKBAR_ADJUST_LAYOUTPARAMS;
								mBalanceSeekBarNum.setLayoutParams(balanceparams);
								mBalanceSeekBarNum.setWidth(70);
							}

						}
						break;
						case KeyEvent.KEYCODE_DPAD_LEFT: {
							if (!view.isFocusable()) {
								return true;
							}
							if (BalanceVal > -50 && BalanceVal <= 50) {
								Log.e("test1", "BalanceVal =" + BalanceVal);
								mBalanceSeekBar.setProgress(--BalanceNum);
								mTvAudioManager.setBalance(BalanceNum);
								BalanceVal--;
								mBalanceSeekBarNum.setText(Integer.toString(BalanceVal));
								balanceparams.leftMargin = BalanceNum * Constants.ESEEKBAR_LAYOUTPARAMS + Constants.ESEEKBAR_ADJUST_LAYOUTPARAMS;
								mBalanceSeekBarNum.setLayoutParams(balanceparams);
								mBalanceSeekBarNum.setWidth(70);
							}
						}
						break;
						}
					}
				}
					break;

				case R.id.mItemAvc: {
					if (event.getAction() == KeyEvent.ACTION_DOWN) {
						switch (keyCode) {
						case KeyEvent.KEYCODE_DPAD_LEFT:
						case KeyEvent.KEYCODE_DPAD_RIGHT: {
							if (!view.isFocusable()) {
								return true;
							}
							if (AvcBool) {
								// manager.setAvcMode(resultValue);
								mAvcContentTxt.setText(mTurnOn_OffStr[Constants.AVC_OFF]);
								AvcBool = false;
								//set AVC Mode to off
								mTvAudioManager.setAvcMode(AvcBool);
							} else {
								mAvcContentTxt.setText(mTurnOn_OffStr[Constants.AVC_ON]);
								AvcBool = true;
								//set AVC Mode to on
								mTvAudioManager.setAvcMode(AvcBool);
							}
						}
						return true;
						}
					}
				}
				break;

				case R.id.mItemSurround: {
					if (event.getAction() == KeyEvent.ACTION_DOWN) {
						switch (keyCode) {
						case KeyEvent.KEYCODE_DPAD_LEFT:
						case KeyEvent.KEYCODE_DPAD_RIGHT: {
							if (!view.isFocusable()) {
								return true;
							}
							if (SurroundModeVal == 1) {
								mSurroundContentTxt.setText(mTurnOn_OffStr[Constants.SurroundMode_OFF]);
								SurroundModeVal = 0;
								//set Surround Mode to off
								mTvAudioManager.setAudioSurroundMode(Constants.SurroundMode_OFF);
							} else {
								mSurroundContentTxt.setText(mTurnOn_OffStr[Constants.SurroundMode_ON]);
								SurroundModeVal = 1;
								//set Surround Mode to on
								mTvAudioManager.setAudioSurroundMode(Constants.SurroundMode_ON);
							}
						}
						return true;
						}
					}
				}
				break;
				
				case R.id.mItemSpidfOutput: {
				    if (event.getAction() == KeyEvent.ACTION_DOWN) {
				        switch (keyCode) {
				            case KeyEvent.KEYCODE_DPAD_LEFT:
				                if (!view.isFocusable()) {
                                    return true;
                                }
				                mSpdifMode = mTvAudioManager.getSpdifOutMode().ordinal();
				                if (mSpdifMode == EnumSpdifType.E_PCM.ordinal()) {
				                    mSpdifOutputContentTxt.setText(mSpdifStatusStr[2]);
				                    mTvAudioManager.setSpdifOutMode(EnumSpdifType.E_NONPCM);
				                } else if (mSpdifMode == EnumSpdifType.E_NONPCM.ordinal()) {
				                    mSpdifOutputContentTxt.setText(mSpdifStatusStr[1]);
                                    mTvAudioManager.setSpdifOutMode(EnumSpdifType.E_OFF);
				                } else if (mSpdifMode == EnumSpdifType.E_OFF.ordinal()) {
				                    mSpdifOutputContentTxt.setText(mSpdifStatusStr[0]);
                                    mTvAudioManager.setSpdifOutMode(EnumSpdifType.E_PCM);
				                }
				                break;
				            case KeyEvent.KEYCODE_DPAD_RIGHT:
				                mSpdifMode = mTvAudioManager.getSpdifOutMode().ordinal();
				                if (mSpdifMode == EnumSpdifType.E_PCM.ordinal()) {
                                    mSpdifOutputContentTxt.setText(mSpdifStatusStr[1]);
                                    mTvAudioManager.setSpdifOutMode(EnumSpdifType.E_OFF);
                                } else if (mSpdifMode == EnumSpdifType.E_OFF.ordinal()) {
				                    mSpdifOutputContentTxt.setText(mSpdifStatusStr[2]);
				                    mTvAudioManager.setSpdifOutMode(EnumSpdifType.E_NONPCM);
				                } else if (mSpdifMode == EnumSpdifType.E_NONPCM.ordinal()) {
                                    mSpdifOutputContentTxt.setText(mSpdifStatusStr[0]);
                                    mTvAudioManager.setSpdifOutMode(EnumSpdifType.E_PCM);
                                }
				                return true;
				        }
				    }
				}
				break;

				case R.id.mItemInteralSpeaker: {
					if (event.getAction() == KeyEvent.ACTION_DOWN) {
						switch (keyCode) {
						case KeyEvent.KEYCODE_DPAD_LEFT:
						case KeyEvent.KEYCODE_DPAD_RIGHT: {
							if (!view.isFocusable()) {
								return true;
							}
							if (!interalSpeakerBool) {
								mInteralSpeakerContentTxt.setText(mTurnOn_OffStr[Constants.INTERAL_SPEAKER_OFF]);
								interalSpeakerBool = true;

								try {
                                    mTvManager.setGpioDeviceStatus(0, true);//internal speaker OFF
                                    mTvManager.setEnvironment("interalSpeaker", "off");
                                    Log.i("AudioSetting", "setGpioDeviceStatus(0, true)");
                                    Log.i("AudioSetting", "setEnvironment(interalSpeaker, off)");
                                } catch (TvCommonException e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                                android.provider.Settings.System.putInt(mFragment.getActivity().getContentResolver(), "Gpio", 0);
								 
								//save interalSpeaker state to deb
//								System.putInt(mFragment.getActivity().getContentResolver(), "SPEAKER",interalSpeakerBool ? 1 : 0);
							} else {
								mInteralSpeakerContentTxt.setText(mTurnOn_OffStr[Constants.INTERAL_SPEAKER_ON]);
								interalSpeakerBool = false;
								boolean flg = false;
                                try {
                                    flg = mTvManager.getGpioDeviceStatus(57) != 0;
                                } catch (TvCommonException e1) {
                                    // TODO Auto-generated catch block
                                    e1.printStackTrace();
                                }
			                    if (flg) {
			                        try {
                                        mTvManager.setGpioDeviceStatus(57, false);
                                        Log.i("AudioSetting", "setGpioDeviceStatus(57, false)");
                                    } catch (TvCommonException e) {
                                        // TODO Auto-generated catch block
                                        e.printStackTrace();
                                    }
			                    }
			                    try {
                                    mTvManager.setGpioDeviceStatus(0, false);//internal speaker ON
                                    Log.i("AudioSetting", "setEnvironment(setGpioDeviceStatus(0, false)");
                                    TvManager.getInstance().getAudioManager().disableMute(EnumMuteType.E_MUTE_PERMANENT);
                                    Log.i("AudioSetting", "disableMute(EnumMuteType.E_MUTE_PERMANENT)");
                                    mTvManager.setEnvironment("interalSpeaker", "on");
                                    Log.i("AudioSetting", "setEnvironment(interalSpeaker, on)");
                                    Log.i("AudioSetting", "environment = " + mTvManager.getEnvironment("interalSpeaker"));
                                } catch (TvCommonException e1) {
                                    // TODO Auto-generated catch block
                                    e1.printStackTrace();
                                }
								
//								System.putInt(mFragment.getActivity().getContentResolver(), "SPEAKER",interalSpeakerBool ? 1 : 0);
							}
						}
						return true;
						}
					}
				}
				break;

				case R.id.mItemExtSpeaker: {
                        if (event.getAction() == KeyEvent.ACTION_DOWN) {
                            switch (keyCode) {
                            case KeyEvent.KEYCODE_DPAD_LEFT:
                            case KeyEvent.KEYCODE_DPAD_RIGHT: {
                                if (!view.isFocusable()) {
                                    return true;
                                }
                                
                                try {
                                    if (mTvManager.getGpioDeviceStatus(57) == 0) {
                                        Log.e("rick","getGpioDeviceStatus(57) == 0------>turn on");
                                        mExtSpeakerContentTxt.setText(mTurnOn_OffStr[Constants.EXT_SPEAKER_ON]);
                                        mTvManager.setGpioDeviceStatus(57, true);// external speaker ON
                                        mTvManager.setEnvironment("EarPhone", "on");
                                    } else {
                                        Log.e("rick","getGpioDeviceStatus(57) != 0------>turn off");
                                        mExtSpeakerContentTxt.setText(mTurnOn_OffStr[Constants.EXT_SPEAKER_OFF]);
                                        mTvManager.setGpioDeviceStatus(57, false);// external speaker OFF
                                        mTvManager.setEnvironment("EarPhone", "off");
                                    }
                                } catch (TvCommonException e) {
                                    e.printStackTrace();
                                }
                                
                            }
                            return true;
                            }
                        }
                    }
                    break;

				case R.id.mItemNotificationSound: {
					if (event.getAction() == KeyEvent.ACTION_DOWN) {
						switch (keyCode) {
						case KeyEvent.KEYCODE_DPAD_LEFT:
						case KeyEvent.KEYCODE_DPAD_RIGHT: {
							if (!view.isFocusable()) {
								return true;
							}
							
							boolean isNotificationMute = mNotificationSoundContentTxt.getText().equals(mTurnOn_OffStr[Constants.NOTIFICATION_SOUND_OFF]);
							Settings.System.putString(mFragment.getActivity().getContentResolver(), Settings.System.NOTIFICATION_SOUND,
							        isNotificationMute ? "content://media/internal/audio/media/98" : "");
							if (isNotificationMute) {
								mNotificationSoundContentTxt.setText(mTurnOn_OffStr[Constants.NOTIFICATION_SOUND_ON]);
								//set ExtSpeaker to on
//								Settings.System.putInt(mFragment.getActivity().getContentResolver(),"sound_effects_enabled", 1);
							} else {
								mNotificationSoundContentTxt.setText(mTurnOn_OffStr[Constants.NOTIFICATION_SOUND_OFF]);
								//set ExtSpeaker to off
//								Settings.System.putInt(mFragment.getActivity().getContentResolver(),"sound_effects_enabled", 0);
							}
						}
						return true;

						}
					}
				}
				break;
				
				case R.id.mItemPowerOnMusic: {
                    if (event.getAction() == KeyEvent.ACTION_DOWN) {
                        switch (keyCode) {
                        case KeyEvent.KEYCODE_DPAD_LEFT:
                        case KeyEvent.KEYCODE_DPAD_RIGHT: {
                            if (!view.isFocusable()) {
                                return true;
                            }
                            if (mPowerOnMusicVal == Constants.POWER_ON_MUSIC_OFF) {
                                mPowerOnMusicContentTxt.setText(mTurnOn_OffStr[Constants.POWER_ON_MUSIC_ON]);
                                mPowerOnMusicVal = Constants.POWER_ON_MUSIC_ON;
                                
//                                Settings.System.putInt(mFragment.getActivity().getContentResolver(), "powerOnMusic", 1);
                                try {
//                                    mTvManager.enablePowerOnMusic();
                                    TvFactoryManager.getInstance().setPowerOnMusicMode(mPowerOnMusicVal);
                                } catch (Exception e) {
                                    // TODO Auto-generated catch block
                                    e.printStackTrace();
                                }
                            } else {
                                mPowerOnMusicContentTxt.setText(mTurnOn_OffStr[Constants.POWER_ON_MUSIC_OFF]);
                                mPowerOnMusicVal = Constants.POWER_ON_MUSIC_OFF;
                                
   //                            Settings.System.putInt(mFragment.getActivity().getContentResolver(), "powerOnMusic", 0);
                                //      mTvManager.disablePowerOnMusic();
                                TvFactoryManager.getInstance().setPowerOnMusicMode(mPowerOnMusicVal);
                            }
                        }
                        return true;

                        }
                    }
                }
                break;
                
				case R.id.mItemSeperateHear:
				    if(event.getAction() == KeyEvent.ACTION_DOWN){
				        switch (keyCode) {
                            case KeyEvent.KEYCODE_ENTER:
                                Intent intent = new Intent(mFragment.getActivity(), SeperateHearActivity.class);
                                mFragment.getActivity().startActivity(intent);
                                break;

                            default:
                                break;
                        }
				    }
				    break;
				    

				}
				return false;
			}
		};
		//set Items OnKeyListener
		mItemSoundModeLl.setOnKeyListener(OnKeyListener);
		mItemLowPitchLl.setOnKeyListener(OnKeyListener);
		mItemHighLl.setOnKeyListener(OnKeyListener);
		mItemEquilizerLl.setOnKeyListener(OnKeyListener);
		mItemBalanceLl.setOnKeyListener(OnKeyListener);
		mItemAvcLl.setOnKeyListener(OnKeyListener);
		mItemSurroundLl.setOnKeyListener(OnKeyListener);
		mItemSpdifOutputLl.setOnKeyListener(OnKeyListener);
		mItemInteralSpeakerLl.setOnKeyListener(OnKeyListener);
		mItemExtSpeakerLl.setOnKeyListener(OnKeyListener);
		mItemNotificationSoundLl.setOnKeyListener(OnKeyListener);
		mItemPowerOnMusicLl.setOnKeyListener(OnKeyListener);
		mItemSeperateHearLl.setOnKeyListener(OnKeyListener);

        OnFocusChangeListener onFocusChangeListener = new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View view, boolean haiFocus) {

                switch (view.getId()) {
                    case R.id.mItemSoundMode: {
                        if (haiFocus) {
                            // change Title,Background to blue
                            enumItemFocused(view, mSoundModeTitleTxt);
                        } else {
                            // change Title,Background to grey
                            enumItemUnfocused(view, mSoundModeTitleTxt);
                        }
                    }
                        break;
                    case R.id.mItemLowPitch: {
                        if (haiFocus) {
                            // change Title,seekBar and thumb to blue
                            seekbarItemFocused(mLowPitchTitleTxt, mLowPitchSeekBar);
                        } else {
                            // change Title,seekBar and thumb to grey
                            seekbarItemUnfocused(mLowPitchTitleTxt, mLowPitchSeekBar);
                        }
                    }
                        break;
                    case R.id.mItemHigh: {
                        if (haiFocus) {
                            seekbarItemFocused(mHighTitleTxt, mHighSeekBar);
                        } else {
                            seekbarItemUnfocused(mHighTitleTxt, mHighSeekBar);
                        }
                    }
                        break;
                    case R.id.mItemEquilizer: {
                        if (haiFocus) {
                            buttomItemFocused(view, mEquilizerTitleTxt);
                        } else {
                            buttomItemUnfocused(view, mEquilizerTitleTxt);
                        }
                    }
                        break;
                    case R.id.mItemBalance: {
                        if (haiFocus) {
                            seekbarItemFocused(mBalanceTitleTxt, mBalanceSeekBar);
                        } else {
                            seekbarItemUnfocused(mBalanceTitleTxt, mBalanceSeekBar);
                        }
                    }
                        break;
                    case R.id.mItemAvc: {
                        if (haiFocus) {
                            enumItemFocused(view, mAvcTitleTxt);
                        } else {
                            enumItemUnfocused(view, mAvcTitleTxt);
                        }
                    }
                        break;
                    case R.id.mItemSurround: {
                        if (haiFocus) {
                            enumItemFocused(view, mSurroundTitleTxt);
                        } else {
                            enumItemUnfocused(view, mSurroundTitleTxt);
                        }
                    }
                        break;
                    case R.id.mItemSpidfOutput: {
                        if (haiFocus) {
                            enumItemFocused(view, mSpdifOutputTitleTxt);
                        } else {
                            enumItemUnfocused(view, mSpdifOutputTitleTxt);
                        }
                    }
                        break;
                    case R.id.mItemInteralSpeaker: {
                        if (haiFocus) {
                            enumItemFocused(view, mInteralSpeakerTitleTxt);
                        } else {
                            enumItemUnfocused(view, mInteralSpeakerTitleTxt);
                        }
                    }
                        break;
                    case R.id.mItemExtSpeaker: {
                        if (haiFocus) {
                            enumItemFocused(view, mExtSpeakerTitleTxt);
                        } else {
                            enumItemUnfocused(view, mExtSpeakerTitleTxt);
                        }
                    }
                        break;
                    case R.id.mItemNotificationSound: {
                        if (haiFocus) {
                            enumItemFocused(view, mNotificationSoundTitleTxt);
                        } else {
                            enumItemUnfocused(view, mNotificationSoundTitleTxt);
                        }
                    }
                        break;

                    case R.id.mItemPowerOnMusic: {
                        if (haiFocus) {
                            enumItemFocused(view, mPowerOnMusicTitleTxt);
                        } else {
                            enumItemUnfocused(view, mPowerOnMusicTitleTxt);
                        }
                    }
                        break;
                    case R.id.mItemSeperateHear: {
                        if (haiFocus) {
                            buttomItemFocused(view, mSeperateHearTitleTxt);
                        } else {
                            buttomItemUnfocused(view, mSeperateHearTitleTxt);
                        }
                    }
                        break;

                }
            }
        };

		//set Items OnFocusChangeListener
		mItemSoundModeLl.setOnFocusChangeListener(onFocusChangeListener);
		mItemLowPitchLl.setOnFocusChangeListener(onFocusChangeListener);
		mItemHighLl.setOnFocusChangeListener(onFocusChangeListener);
		mItemEquilizerLl.setOnFocusChangeListener(onFocusChangeListener);
		mItemBalanceLl.setOnFocusChangeListener(onFocusChangeListener);
		mItemAvcLl.setOnFocusChangeListener(onFocusChangeListener);
		mItemSurroundLl.setOnFocusChangeListener(onFocusChangeListener);
		mItemSpdifOutputLl.setOnFocusChangeListener(onFocusChangeListener);
		mItemInteralSpeakerLl.setOnFocusChangeListener(onFocusChangeListener);
		mItemExtSpeakerLl.setOnFocusChangeListener(onFocusChangeListener);
		mItemNotificationSoundLl.setOnFocusChangeListener(onFocusChangeListener);
		mItemPowerOnMusicLl.setOnFocusChangeListener(onFocusChangeListener);
		mItemSeperateHearLl.setOnFocusChangeListener(onFocusChangeListener);
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
	   seekBarNum.setWidth(70);
	}
	
	/**
     * change the UI when buttomItem dosen't has focused
     * @param view
     * @param titleTxt
     */
	private void buttomItemUnfocused(View view, TextView titleTxt) {
        titleTxt.setTextColor(android.graphics.Color.WHITE);
        view.findViewById(R.id.button_context).setBackgroundResource(R.drawable.bar_bg_btn_grey);
    }

	/**
     * change the UI when buttomItem has focused
     * @param view
     * @param titleTxt
     */
    private void buttomItemFocused(View view, TextView titleTxt) {
        titleTxt.setTextColor(mR.getColor(R.color.cyan));
        view.findViewById(R.id.button_context).setBackgroundResource(R.drawable.bar_bg_btn_cyan);
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
	
	/**
	 * change the UI when EnumItem has focused
	 * @param view
	 * @param titleTxt
	 */
	 private void enumItemUnfocused(View view, TextView titleTxt) {
	     titleTxt.setTextColor(android.graphics.Color.WHITE);
         view.findViewById(R.id.enum_context).setBackgroundResource(R.drawable.bar_bg_enum_grey);
     }

	 /**
	  * change the UI when EnumItem dosen't has focused
	  * @param view
	  * @param titleTxt
	  */
     private void enumItemFocused(View view, TextView titleTxt) {
         titleTxt.setTextColor(mR.getColor(R.color.cyan));
         view.findViewById(R.id.enum_context).setBackgroundResource(R.drawable.bar_bg_enum_cyan);
     }

	/**
	 * once change the LowPitch Value and HighPitch Value ,view SoundMode Item will update
	 */
	protected void changeSoundModetoUser() {
		mTvAudioManager.setAudioSoundMode(Constants.USER);
		mCurSoundMode = Constants.USER;
		mSoundModeContentTxt.setText(mSoundModeStr[mCurSoundMode]);
	}

	/**
	 * once change the SoundMode,view of LowPitch Item and HighPitch Item will update
	 */
	protected void updateView() {
		mLowPitchSeekBarNum.setText(Integer.toString(mTvAudioManager.getBassBySoundMode(mCurSoundMode)));
		mLowPitchSeekBar.setProgress(mTvAudioManager.getBassBySoundMode(mCurSoundMode));
		mHighSeekBarNum.setText(Integer.toString(mTvAudioManager.getTrebleBySoundMode(mCurSoundMode)));
		mHighSeekBar.setProgress(mTvAudioManager.getTrebleBySoundMode(mCurSoundMode));
		lowPitchParams.leftMargin = mTvAudioManager.getBassBySoundMode(mCurSoundMode) * Constants.ESEEKBAR_LAYOUTPARAMS + Constants.ESEEKBAR_ADJUST_LAYOUTPARAMS;
		mLowPitchSeekBarNum.setLayoutParams(lowPitchParams);
		highPitchParams.leftMargin = mTvAudioManager.getTrebleBySoundMode(mCurSoundMode) * Constants.ESEEKBAR_LAYOUTPARAMS + Constants.ESEEKBAR_ADJUST_LAYOUTPARAMS;
		mHighSeekBarNum.setLayoutParams(highPitchParams);
	}
	
}
