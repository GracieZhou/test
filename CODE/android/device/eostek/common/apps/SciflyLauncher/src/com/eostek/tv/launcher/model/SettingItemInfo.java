
package com.eostek.tv.launcher.model;

import android.view.KeyEvent;

import com.eostek.tv.launcher.ui.GeneralSettings;
import com.eostek.tv.launcher.util.LConstants;

public class SettingItemInfo {

    private GeneralSettings context;

    private String title;

    private String[] values;

    private int curValue;

    private int startValue;

    private int endValue;

    private int itemType;

    // constructor for button
    public SettingItemInfo(GeneralSettings ctx, String tit, String[] vals, int typ) {
        this.context = ctx;
        this.title = tit;
        this.values = vals;
        this.itemType = typ;
    }

    // constructor for digital
    public SettingItemInfo(GeneralSettings ctx, String tit, String[] vals, int curVal, int startVal,
            int endval, int ty) {
        this.context = ctx;
        this.title = tit;
        this.values = vals;
        this.curValue = curVal;
        this.startValue = startVal;
        this.endValue = endval;
        this.itemType = ty;
    }

    // constructor for enum
    public SettingItemInfo(GeneralSettings ctx, String tit, String[] vals, int curVal, int typ) {
        this.context = ctx;
        this.title = tit;
        this.values = vals;
        this.curValue = curVal;
        this.itemType = typ;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String tit) {
        this.title = tit;
    }

    public String[] getValues() {
        return values;
    }

    public void setValues(String[] val) {
        this.values = val;
    }

    public int getCurValue() {
        return curValue;
    }

    public void setCurValue(int curVal) {
        this.curValue = curVal;
    }

    public int getStartValue() {
        return startValue;
    }

    public void setStartValue(int startVal) {
        this.startValue = startVal;
    }

    public int getEndValue() {
        return endValue;
    }

    public void setEndValue(int endVal) {
        this.endValue = endVal;
    }

    public int getItemType() {
        return itemType;
    }

    public void setItemType(int tp) {
        this.itemType = tp;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event, int position) {
        if (itemType == LConstants.ITEM_BUTTON) {
            context.itemClick(position);
        } else if (itemType == LConstants.ITEM_ENUM) {
            if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                if (curValue > 0) {
                    --curValue;
                } else {
                    curValue = values.length - 1;
                }
            } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                if (curValue < values.length - 1) {
                    ++curValue;
                } else {
                    curValue = 0;
                }
            }
            context.updateView(position);
        }
        return true;
    }

}
