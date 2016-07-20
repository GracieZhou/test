
package com.mstar.tv.menu.setting;

import java.util.ArrayList;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.util.Log;
import android.view.KeyEvent;

import com.mstar.android.tv.TvAudioManager;
import com.mstar.tv.MenuConstants;
import com.mstar.tv.menu.R;

/**
 * @projectName： EOSTVMenu
 * @moduleName： PictureSettingFragment.java
 * @author jachensy.chen
 * @version 1.0.0
 * @time 2013-12-16
 * @Copyright © 2013 MStar Semiconductor, Inc.
 */
public class EquilizerFragment extends PublicFragement {

    private TvAudioManager manager;

    public final static short T_NRMode_IDX = 0x0E;

    public final static short T_SystemSetting_IDX = 0x19;

    private EosSettingItem equilizer_120hz_item = null;

    private EosSettingItem equilizer_500hz_item = null;

    private EosSettingItem equilizer_1500hz_item = null;

    private EosSettingItem equilizer_5khz_item = null;

    private EosSettingItem equilizer_10khz_item = null;

    @Override
    protected void initItems() {
        setTag("equilizer");
        manager = TvAudioManager.getInstance();
        mItems = new ArrayList<EosSettingItem>();
        equilizer_120hz_item = new EosSettingItem(this, getResources().getStringArray(
                R.array.setting_equilizer)[0], 0, 100, 0, MenuConstants.ITEMTYPE_DIGITAL, true);
        mItems.add(equilizer_120hz_item);

        equilizer_500hz_item = new EosSettingItem(this, getResources().getStringArray(
                R.array.setting_equilizer)[1], 0, 100, 0, MenuConstants.ITEMTYPE_DIGITAL, true);
        mItems.add(equilizer_500hz_item);

        equilizer_1500hz_item = new EosSettingItem(this, getResources().getStringArray(
                R.array.setting_equilizer)[2], 0, 100, 0, MenuConstants.ITEMTYPE_DIGITAL, true);
        mItems.add(equilizer_1500hz_item);

        equilizer_5khz_item = new EosSettingItem(this, getResources().getStringArray(
                R.array.setting_equilizer)[3], 0, 100, 0, MenuConstants.ITEMTYPE_DIGITAL, true);
        mItems.add(equilizer_5khz_item);

        equilizer_10khz_item = new EosSettingItem(this, getResources().getStringArray(
                R.array.setting_equilizer)[4], 0, 100, 0, MenuConstants.ITEMTYPE_DIGITAL, true);
        mItems.add(equilizer_10khz_item);
        initDate();
    }

    @Override
    protected void initDate() {
        equilizer_120hz_item.setCurValue(manager.getEqBand120());
        equilizer_500hz_item.setCurValue(manager.getEqBand500());
        equilizer_1500hz_item.setCurValue(manager.getEqBand1500());
        equilizer_5khz_item.setCurValue(manager.getEqBand5k());
        equilizer_10khz_item.setCurValue(manager.getEqBand10k());
        mAdapter.setHasShowValue(true);
    }

    @Override
    void callBack(int resultValue, int position) {
        switch (position) {
            case 0:
                manager.setEqBand120(resultValue);
                break;
            case 1:
                manager.setEqBand500(resultValue);
                break;
            case 2:
                manager.setEqBand1500(resultValue);
                break;
            case 3:
                manager.setEqBand5k(resultValue);
                break;
            case 4:
                manager.setEqBand10k(resultValue);
                break;
            default:
                break;
        }

    }

    @Override
    void callBack(Boolean resultVaule, int position) {
    }

    @Override
    void callBack(int position) {
    }

    @Override
    boolean doKeyDownOrUp(int keyCode, KeyEvent event) {
        if (KeyEvent.KEYCODE_BACK == keyCode) {
            AudioSettingFragment audio = new AudioSettingFragment();
            FragmentManager fm = getFragmentManager();
            fm.popBackStack();
            FragmentTransaction ft = fm.beginTransaction();
            ft.setCustomAnimations(R.anim.eos_menu_anim_rightin, R.anim.eos_menu_anim_leftout);
            ft.replace(R.id.content_setting, audio);
            ft.commit();
            return true;
        }
        return false;
    }
}
