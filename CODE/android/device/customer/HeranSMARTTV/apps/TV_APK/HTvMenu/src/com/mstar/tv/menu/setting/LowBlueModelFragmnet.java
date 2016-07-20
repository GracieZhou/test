
package com.mstar.tv.menu.setting;

import java.util.ArrayList;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ContentValues;
import android.database.SQLException;
import android.net.Uri;
import android.provider.Settings;
import android.provider.Settings.System;
import android.util.Log;
import android.view.KeyEvent;

import com.mstar.android.tv.TvCommonManager;
import com.mstar.android.tv.TvPictureManager;
import com.mstar.android.tvapi.common.TvManager;
import com.mstar.android.tvapi.common.exception.TvCommonException;
import com.mstar.android.tvapi.factory.vo.WbGainOffsetEx;
import com.mstar.tv.MenuConstants;
import com.mstar.tv.menu.R;

/**
 * @ClassName: LowBlueModelFragmnet
 * @Description:低蓝护眼(这里用一句话描述这个类的作用)
 * @author: lucky.li
 * @date: 2015-7-16 上午10:34:57
 * @Copyright: Eostek Co., Ltd. Copyright , All rights reserved
 */
public class LowBlueModelFragmnet extends PublicFragement {

    public final static short T_NRMode_IDX = 0x0E;

    public final static short T_SystemSetting_IDX = 0x19;

    private EosSettingItem mModel = null;

    private EosSettingItem mLbLightAdjust = null;

    private EosSettingItem mLightSourceAdjust = null;

    private EosSettingItem mProtectEyeNotice = null;
    
    private WbGainOffsetEx wbgain = null;
    
    private final short T_FacrotyColorTempEx_IDX = 0x26;
    
    private static  final String TAG = "LowBlueModelFragmnet";

    @Override
    protected void initItems() {
        setTag("lowBlue");
        mItems = new ArrayList<EosSettingItem>();
        mModel = new EosSettingItem(this, getResources().getStringArray(R.array.setting_low_blue)[0], getResources()
                .getStringArray(R.array.lb_mode), 0, MenuConstants.ITEMTYPE_ENUM, true);
        mItems.add(mModel);

        mLbLightAdjust = new EosSettingItem(this, getResources().getStringArray(R.array.setting_low_blue)[1],
                getResources().getStringArray(R.array.lb_light_adjust), 0, MenuConstants.ITEMTYPE_ENUM, true);
        mItems.add(mLbLightAdjust);

        mLightSourceAdjust = new EosSettingItem(this, getResources().getStringArray(R.array.setting_low_blue)[2], 0,
                100, 0, MenuConstants.ITEMTYPE_DIGITAL, true);
        mItems.add(mLightSourceAdjust);

        mProtectEyeNotice = new EosSettingItem(this, getResources().getStringArray(R.array.setting_low_blue)[3],
                getResources().getStringArray(R.array.turnon_off), true, MenuConstants.ITEMTYPE_BOOL, true);
        mItems.add(mProtectEyeNotice);
        initDate();
    }

    @Override
    protected void initDate() {
        int mode = Settings.System.getInt(getActivity().getContentResolver(), MenuConstants.MODE, 0);
        int index = 0;
        if (mode != 0) {
            index = TvPictureManager.getInstance().getColorTempratureIdx() + 1;
            if(index >= TvPictureManager.COLOR_TEMP_USER1) {
                index = 3;
                TvPictureManager.getInstance().setColorTempratureIdx(TvPictureManager.COLOR_TEMP_WARM);
            }
        }
        
        boolean isFocus = (mode == 1);
        Log.d(TAG," ----index:"+index);
        mModel.setCurValue(index);
        int value = 0;
        //disable mLbLightAdjust Functions       
  /*      try {
            wbgain = TvManager
                    .getInstance()
                    .getFactoryManager()
                    .getWbGainOffsetEx(TvPictureManager.getInstance().getColorTempIdx(),
                            TvCommonManager.getInstance().getCurrentTvInputSource());
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        Log.d(TAG,"------colrTemp: "+wbgain.blueGain);
        if (wbgain.blueGain == 20) {
            value = 0;
        } else if (wbgain.blueGain == 10) {
            value = 1;
        } else if (wbgain.blueGain == 0) {
            value = 2;
        } else {
            value = 0;
        }*/
        
      int  isProtectRemindOn =   Settings.System.getInt(getActivity().getContentResolver(), MenuConstants.PROTECT_REMIND, 0);

        mLbLightAdjust.setCurValue(value);
        mLightSourceAdjust.setCurValue(TvPictureManager.getInstance().getBacklight());
        mProtectEyeNotice.setBoolValue(isProtectRemindOn != 0);
        mLbLightAdjust.setFocusable(isFocus);
        mLightSourceAdjust.setFocusable(isFocus);
        mProtectEyeNotice.setFocusable(isFocus);
        mAdapter.setHasShowValue(true);
    }

    @Override
    void callBack(int resultVaule, int position) {
        switch (position) {
            case 0:
                if (resultVaule == 0) {
                    System.putInt(getActivity().getContentResolver(), MenuConstants.MODE, 0);
                    mLbLightAdjust.setFocusable(false);
                    mLightSourceAdjust.setFocusable(false);
                    mProtectEyeNotice.setFocusable(false);
                } else {
                    System.putInt(getActivity().getContentResolver(), MenuConstants.MODE, 1);
                    TvPictureManager.getInstance().setColorTempratureIdx(resultVaule - 1);
                    mLbLightAdjust.setFocusable(true);
                    mLightSourceAdjust.setFocusable(true);
                    mProtectEyeNotice.setFocusable(true);
                }
                getActivity().runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        mAdapter.notifyDataSetChanged();
                    }
                });
                break;
            case 1:
                //disable mLbLightAdjust Functions
/*                int value = 0;
                if (resultVaule == 0) {
                    value = 20;
                } else if (resultVaule == 1) {
                    value = 10;
                } else if (resultVaule == 2) {
                    value = 0;
                }
                try {
                    TvManager
                            .getInstance()
                            .getFactoryManager()
                            .setWbGainOffsetEx(TvPictureManager.getInstance().getColorTempIdx(), wbgain.redGain,
                                    wbgain.greenGain, value, wbgain.redOffset, wbgain.greenOffset, wbgain.blueGain,
                                    TvCommonManager.getInstance().getCurrentInputSource());
                } catch (TvCommonException e) {
                    e.printStackTrace();
                }
                updateDataBase(value);*/
                break;
            case 2:
                TvPictureManager.getInstance().setBacklight(resultVaule);
                break;
            default:
                break;
        }
    }
    
    private void updateDataBase(int value) {
        long ret = -1;
        ContentValues vals = new ContentValues();
        vals.put("u16RedGain", wbgain.redGain);
        vals.put("u16GreenGain", wbgain.greenGain);
        vals.put("u16BlueGain", value);
        vals.put("u16RedOffset", wbgain.redOffset);
        vals.put("u16GreenOffset", wbgain.greenOffset);
        vals.put("u16BlueOffset", wbgain.blueOffset);
        try {
            ret = getActivity().getContentResolver().update(
                    Uri.parse("content://mstar.tv.factory/factorycolortempex/inputsourceid/"
                            + TvCommonManager.getInstance().getCurrentTvInputSource() + "/colortemperatureid/"
                            + TvPictureManager.getInstance().getColorTempratureIdx()), vals, null, null);
        } catch (SQLException e) {
        }
        if (ret == -1) {
            Log.v(TAG, "update DB fail");
        }
        try {
            TvManager.getInstance().getDatabaseManager()
                    .setDatabaseDirtyByApplication(T_FacrotyColorTempEx_IDX);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }

    }

    @Override
    void callBack(Boolean resultVaule, int position) {
        if (position == 3) {
            if(resultVaule){
                System.putInt(getActivity().getContentResolver(), MenuConstants.PROTECT_REMIND, 1);
               }else {
                   System.putInt(getActivity().getContentResolver(), MenuConstants.PROTECT_REMIND, 0);
            }
        }

    }

    @Override
    void callBack(int position) {

    }

    @Override
    boolean doKeyDownOrUp(int keyCode, KeyEvent event) {
        if (KeyEvent.KEYCODE_BACK == keyCode) {
            PictureSettingFragment picFragment = new PictureSettingFragment();
            FragmentManager fm = getFragmentManager();
            fm.popBackStack();
            FragmentTransaction ft = fm.beginTransaction();
            ft.setCustomAnimations(R.anim.eos_menu_anim_rightin, R.anim.eos_menu_anim_leftout);
            ft.replace(R.id.content_setting, picFragment);
            ft.commit();
            return true;
        }
        return false;
    }

}
