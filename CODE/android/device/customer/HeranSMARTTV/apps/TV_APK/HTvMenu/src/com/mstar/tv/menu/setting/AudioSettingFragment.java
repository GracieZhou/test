
package com.mstar.tv.menu.setting;

import java.util.ArrayList;

import android.annotation.SuppressLint;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;

import com.mstar.android.tv.TvAudioManager;
import com.mstar.android.tvapi.common.TvManager;
import com.mstar.android.tvapi.common.exception.TvCommonException;
import com.mstar.android.tvapi.common.vo.EnumSoundMode;
import com.mstar.android.tvapi.common.vo.EnumSpdifType;
import com.mstar.android.tvapi.common.vo.EnumSurroundMode;
import com.mstar.android.tvapi.common.vo.MuteType.EnumMuteType;
import com.mstar.tv.MenuConstants;
import com.mstar.tv.menu.R;
import com.mstar.tv.menu.setting.util.Tools;

/*
 * @projectName： EOSTVMenu
 * @moduleName： AudioSettingFragment.java
 * @author jachensy.chen
 * @version 1.0.0
 * @time  2013-12-18
 * @Copyright © 2013 EOSTEK, Inc.
 */
@SuppressLint("ValidFragment")
public class AudioSettingFragment extends PublicFragement {

    private static final String TAG = "AudioSettingFragment";

    private EosSettingItem audiomode_item = null;

    private EnumSoundMode soundMode = EnumSoundMode.E_STANDARD;

    private EosSettingItem low_pitch_item = null;

    private EosSettingItem high_item = null;

    private EosSettingItem equilizer_item = null;

    private EosSettingItem balance_item = null;

    private EosSettingItem avc_item = null;

    private EosSettingItem surround_item = null;

    private EosSettingItem spdif_output_item = null;

    private EosSettingItem interalSpeaker_item = null;

    private EosSettingItem extSpeaker_item = null;

    private String[] audio_setting_title;

    private TvAudioManager manager;

    private TvManager tm = TvManager.getInstance();

    private int spdif = "TRUE".equals(Tools.readSysIni("SPDIF_OUT")) ? 1 : 0;

    private final static int PM7 = 0;

    private final static int PM8 = 1;

    @Override
    protected void initItems() {
        setTag("audio");
        manager = TvAudioManager.getInstance();
        mItems = new ArrayList<EosSettingItem>();
        audio_setting_title = getActivity().getResources().getStringArray(R.array.setting_audio);
        audiomode_item = new EosSettingItem(this, audio_setting_title[0], getResources().getStringArray(
                R.array.setting_soundmode_vals), soundMode.ordinal(), MenuConstants.ITEMTYPE_ENUM, true);
        mItems.add(audiomode_item);

        low_pitch_item = new EosSettingItem(this, audio_setting_title[1], 0, 100, 0, MenuConstants.ITEMTYPE_DIGITAL,
                true);
        mItems.add(low_pitch_item);

        high_item = new EosSettingItem(this, audio_setting_title[2], 0, 100, 0, MenuConstants.ITEMTYPE_DIGITAL, true);
        mItems.add(high_item);

        equilizer_item = new EosSettingItem(this, audio_setting_title[3], MenuConstants.ITEMTYPE_BUTTON, true);
        mItems.add(equilizer_item);

        balance_item = new EosSettingItem(this, audio_setting_title[4], -50, 50, 0, MenuConstants.ITEMTYPE_DIGITAL,
                true);
        mItems.add(balance_item);

        avc_item = new EosSettingItem(this, audio_setting_title[5], getResources().getStringArray(R.array.turnon_off),
                true, MenuConstants.ITEMTYPE_BOOL, true);
        mItems.add(avc_item);

        surround_item = new EosSettingItem(this, audio_setting_title[6], getResources().getStringArray(
                R.array.turnon_off), 0, MenuConstants.ITEMTYPE_ENUM, true);
        mItems.add(surround_item);

        spdif_output_item = new EosSettingItem(this, audio_setting_title[7], getResources().getStringArray(
                R.array.setting_spdifoutput_vals), 0, MenuConstants.ITEMTYPE_ENUM, true);
        if (spdif != 0) {
            mItems.add(spdif_output_item);
        }

        interalSpeaker_item = new EosSettingItem(this, audio_setting_title[8], getResources().getStringArray(
                R.array.turnon_off), true, MenuConstants.ITEMTYPE_BOOL, true);
        mItems.add(interalSpeaker_item);

        extSpeaker_item = new EosSettingItem(this, audio_setting_title[9], getResources().getStringArray(
                R.array.turnon_off), true, MenuConstants.ITEMTYPE_BOOL, true);
        mItems.add(extSpeaker_item);
        initDate();
    }

    @Override
    void callBack(int resultValue, int position) {
        switch (position) {
            case 0:
                manager.setSoundMode(EnumSoundMode.values()[resultValue]);
                mItems.get(1).setCurValue(manager.getBassBySoundMode(resultValue));
                mItems.get(2).setCurValue(manager.getTrebleBySoundMode(resultValue));
                getActivity().runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        mAdapter.notifyDataSetChanged();
                    }
                });
                break;
            case 1:
                manager.setBass(resultValue);
                if (mItems.get(0).getCurValue() != EnumSoundMode.E_USER.ordinal()) {
                    mItems.get(0).setCurValue(EnumSoundMode.E_USER.ordinal());
                    updateView(0, public_lst.getChildAt(0));
                }
                manager.setSoundMode(EnumSoundMode.E_USER);
                break;
            case 2:
                manager.setTreble(resultValue);
                if (mItems.get(0).getCurValue() != EnumSoundMode.E_USER.ordinal()) {
                    mItems.get(0).setCurValue(EnumSoundMode.E_USER.ordinal());
                    updateView(0, public_lst.getChildAt(0));
                }
                manager.setSoundMode(EnumSoundMode.E_USER);
                break;
            case 4:
                manager.setBalance(resultValue + 50);
                break;
            case 6:
                manager.setSurroundMode(EnumSurroundMode.values()[resultValue]);
                break;
            case 7:
                if (spdif == 0)
                    return;
                switch (resultValue) {
                    case 0:
                        manager.setAudioSpdifOutMode(EnumSpdifType.E_PCM.ordinal());
                        break;
                    case 1:
                        manager.setAudioSpdifOutMode(EnumSpdifType.E_OFF.ordinal());
                        break;
                    case 2:
                        manager.setAudioSpdifOutMode(EnumSpdifType.E_NONPCM.ordinal());
                        break;
                    default:
                        break;
                }
                break;
            default:
                break;
        }
    }

    Handler mhandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case PM8:
                    try {
                        tm.setGpioDeviceStatus(57, true);
                    } catch (TvCommonException e) {
                        e.printStackTrace();
                    }
                    break;
                case PM7:
                    try {
                        tm.setGpioDeviceStatus(0, false);
                    } catch (TvCommonException e) {
                        e.printStackTrace();
                    }
                    break;
            }
        }

    };

    @Override
    void callBack(Boolean resultValue, int position) {
        try {
            if (position == 5) {
                manager.setAvcMode(resultValue);
            } else if (position == 7 + spdif) {
                Log.v("AudioSettingFragment", "internal speaker");
                if (tm.getEnvironment("interalSpeaker").equals("off")) {
                    boolean flg = tm.getGpioDeviceStatus(57) != 0;
                    if (flg) {
                        tm.setGpioDeviceStatus(57, false);
                    }
                    tm.setGpioDeviceStatus(0, false);// internal speaker ON
                    TvManager.getInstance().getAudioManager().disableMute(EnumMuteType.E_MUTE_PERMANENT);
                    if (flg) {
                        mhandler.sendEmptyMessageDelayed(PM8, 50);
                    }
                    tm.setEnvironment("interalSpeaker", "on");
                    Log.i(TAG, "environment = " + tm.getEnvironment("interalSpeaker"));
                } else {
                    tm.setGpioDeviceStatus(0, true);// internal speaker OFF
                    android.provider.Settings.System.putInt(getActivity().getContentResolver(), "Gpio", 0);
                    tm.setEnvironment("interalSpeaker", "off");
                    Log.i(TAG, "environment = " + tm.getEnvironment("interalSpeaker"));
                }

            } else if (position == 8 + spdif) {
                Log.v("AudioSettingFragment", "external speaker");
                if (tm.getGpioDeviceStatus(57) == 0) {
                    tm.setGpioDeviceStatus(57, true);// external speaker ON
                    tm.setEnvironment("EarPhone", "on");
                } else {
                    tm.setGpioDeviceStatus(57, false);// external speaker OFF
                    tm.setEnvironment("EarPhone", "off");
                }

            }
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
    }

    @Override
    void callBack(int position) {
        if (position == 3) {
            EquilizerFragment equilizer = new EquilizerFragment();
            FragmentManager fm = getFragmentManager();
            fm.popBackStack();
            FragmentTransaction ft = fm.beginTransaction();
            ft.setCustomAnimations(R.anim.eos_menu_anim_rightin, R.anim.eos_menu_anim_leftout);
            ft.replace(R.id.content_setting, equilizer);
            ft.commit();
        }
    }

    @Override
    protected void initDate() {
        soundMode = manager.getSoundMode();
        audiomode_item.setCurValue(soundMode.ordinal());
        low_pitch_item.setCurValue(manager.getBass());
        high_item.setCurValue(manager.getTreble());
        balance_item.setCurValue(manager.getBalance() - 50);
        avc_item.setBoolValue(manager.getAvcMode());
        surround_item.setCurValue(manager.getSurroundMode().ordinal());
        int spdifOutMode = manager.getAudioSpdifOutMode();
        if (spdifOutMode == EnumSpdifType.E_PCM.ordinal()) {
            spdif_output_item.setCurValue(0);
        } else if (spdifOutMode == EnumSpdifType.E_NONPCM.ordinal()) {
            spdif_output_item.setCurValue(2);
        } else {
            spdif_output_item.setCurValue(1);
        }
        Boolean extSpeaker = null;
        Boolean interalSpeaker = null;
        try {
            extSpeaker = tm.getGpioDeviceStatus(57) == 0 ? false : true;
            // interalSpeaker = tm.getGpioDeviceStatus(0) != 0 ? false : true;
            interalSpeaker = tm.getEnvironment("interalSpeaker").equals("off") ? false : true;
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        extSpeaker_item.setBoolValue(extSpeaker);
        interalSpeaker_item.setBoolValue(interalSpeaker);

        mAdapter.setHasShowValue(true);
    }

    @Override
    boolean doKeyDownOrUp(int keyCode, KeyEvent event) {
        return false;
    }

}
