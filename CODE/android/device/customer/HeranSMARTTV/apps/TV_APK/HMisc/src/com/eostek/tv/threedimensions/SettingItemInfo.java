
package com.eostek.tv.threedimensions;

import android.os.AsyncTask;
import android.view.KeyEvent;

public class SettingItemInfo {

    private PictureSettingActivity mContext;

    private String title;

    private String[] values;

    private int curValue;

    private int startValue;

    private int endValue;

    private int itemType;

    // constructor for digital
    public SettingItemInfo(PictureSettingActivity mContext, String title, int startVal, int endval) {
        this.mContext = mContext;
        this.title = title;
        this.startValue = startVal;
        this.endValue = endval;
        this.itemType = Constants.ITEM_DIGITAL;
    }

    // constructor for enum
    public SettingItemInfo(PictureSettingActivity mContext, String tit, String[] vals) {
        this.mContext = mContext;
        this.title = tit;
        this.values = vals;
        this.itemType = Constants.ITEM_ENUM;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
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

    public void setItemType(int itemType) {
        this.itemType = itemType;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (itemType == Constants.ITEM_ENUM) {
            if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                if (curValue > 0) {
                    -- curValue;
                } else {
                    curValue = values.length - 1;
                }
            } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                if (curValue < values.length - 1) {
                    ++ curValue;
                } else {
                    curValue = 0;
                }
            }
        } else if (itemType == Constants.ITEM_DIGITAL) {
            if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                if (curValue > startValue) {
                    -- curValue;
                } else {
                    curValue = endValue;
                }
            } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                if (curValue < endValue) {
                    ++ curValue;
                } else {
                    curValue = startValue;
                }
            }
        }
        return true;
    }

    class MyTask extends AsyncTask<Integer, Integer, Integer> {

        @Override
        protected Integer doInBackground(Integer... params) {
            if (mContext == null) {
                return null;
            }
            if (itemType == Constants.ITEM_ENUM) {
                mContext.doSetVideoArc(params[0], curValue);
            } else if (itemType == Constants.ITEM_DIGITAL) {
                mContext.doInBackGround(params[0], curValue);
            }
            return null;
        }
    }

}
