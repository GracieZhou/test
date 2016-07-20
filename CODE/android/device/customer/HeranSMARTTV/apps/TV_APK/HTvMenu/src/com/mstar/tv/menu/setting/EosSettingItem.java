
package com.mstar.tv.menu.setting;

import android.os.AsyncTask;
import android.view.KeyEvent;

import com.mstar.tv.MenuConstants;

/*
 * @projectName： EOSTVMenu
 * @moduleName： EosSettingItem.java
 * @author jachensy.chen
 * @version 1.0.0
 * @time  2013-12-18
 * @Copyright © 2013 EOSTEK, Inc.
 */
public class EosSettingItem {
    private PublicFragement mContext;

    private String mTitle;

    private String[] mValues;

    private int mCurValue = 0;

    private boolean mBoolValue = false;

    private int mStartValue;

    private int mEndValue;

    private int mItemType;

    private boolean mFocusable;

    private boolean mRecursive;

    private int tempValue = -1;

    /**
     * constructor for the enumerate.
     * 
     * @param context
     * @param title
     * @param values
     * @param curValue
     * @param itemType
     */
    public EosSettingItem(PublicFragement context, String title, String[] values, int curValue,
            int itemType, boolean focusable) {
        this.mContext = context;
        this.mTitle = title;
        this.mValues = values;
        this.mCurValue = curValue;
        this.mItemType = itemType;
        this.mFocusable = focusable;
    }

    /**
     * constructor for the enumerate.
     * 
     * @param context
     * @param title
     * @param values
     * @param curValue
     * @param itemType
     */
    public EosSettingItem(PublicFragement context, String title, String[] values, boolean curValue,
            int itemType, boolean focusable) {
        this.mContext = context;
        this.mTitle = title;
        this.mValues = values;
        this.mBoolValue = curValue;
        this.mItemType = itemType;
        this.mFocusable = focusable;
    }

    /**
     * constructor for the digital.
     * 
     * @param context
     * @param title
     * @param startValue
     * @param endValue
     * @param curValue
     * @param itemType
     */
    public EosSettingItem(PublicFragement context, String title, int startValue, int endValue,
            int curValue, int itemType, boolean focusable) {
        this(context, title, startValue, endValue, curValue, itemType, focusable, false);
    }

    /**
     * constructor for the digital.
     * 
     * @param context
     * @param title
     * @param startValue
     * @param endValue
     * @param curValue
     * @param itemType
     * @param recursive
     */
    public EosSettingItem(PublicFragement context, String title, int startValue, int endValue,
            int curValue, int itemType, boolean focusable, boolean recursive) {
        this.mContext = context;
        this.mTitle = title;
        this.mStartValue = startValue;
        this.mEndValue = endValue;
        this.mCurValue = curValue;
        this.mItemType = itemType;
        this.mFocusable = focusable;
        this.mRecursive = recursive;
    }

    /**
     * constructor for the button.
     * 
     * @param context
     * @param title
     * @param itemType
     */
    public EosSettingItem(PublicFragement context, String title, int itemType, boolean focusable) {
        this.mContext = context;
        this.mTitle = title;
        this.mItemType = itemType;
        this.mFocusable = focusable;
    }

    /**
     * the action for the item.
     * 
     * @param keyCode
     * @param event
     * @return
     */
    public boolean onKeyDown(int keyCode, KeyEvent event, int position) {
        tempValue = -1;
        if (mItemType == MenuConstants.ITEMTYPE_ENUM) {
            if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                // if (mCurValue == 0) {
                // return true;
                // }
                if (mCurValue > 0) {
                    mCurValue--;
                } else {
                    mCurValue = mValues.length - 1;
                }
            } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                // if (mCurValue == mValues.length - 1) {
                // return true;
                // }
                if (mCurValue < mValues.length - 1) {
                    mCurValue++;
                } else {
                    mCurValue = 0;
                }
            }
            mContext.updateView(position);
            sendMessage(position);
            // mContext.callBack(mCurValue, position);
        } else if (mItemType == MenuConstants.ITEMTYPE_DIGITAL) {
            if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                if (mCurValue == mStartValue) {
                    return true;
                }
                if (mCurValue > mStartValue) {
                    mCurValue--;
                } else if (mRecursive) {
                    mCurValue = mEndValue;
                }
            } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                if (mCurValue == mEndValue) {
                    return true;
                }
                if (mCurValue < mEndValue) {
                    mCurValue++;
                } else if (mRecursive) {
                    mCurValue = mStartValue;
                }
            }
            mContext.updateView(position);
            sendMessage(position);
            // mContext.callBack(mCurValue, position);
        } else if (mItemType == MenuConstants.ITEMTYPE_BOOL) {
            if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                if (mBoolValue) {
                    mBoolValue = false;
                } else {
                    mBoolValue = true;
                }
                mContext.updateView(position);
                sendMessage(position);
                // mContext.callBack(mBoolValue, position);
            }
        }
        return true;
    }

    private void sendMessage(int position) {
        // myHandler.removeMessages(DELAYUPDATE);
        // Message msg = myHandler.obtainMessage();
        // msg.what = DELAYUPDATE;
        // msg.arg1 = position;
        // myHandler.sendMessageDelayed(msg, DELAYUPDATETIME);
        new MyTask().execute(position);
    }

    /**
     * the item has been click.
     */
    public void itemClicked(int position) {
        if (mItemType == MenuConstants.ITEMTYPE_BUTTON) {
            mContext.callBack(position);
        }
    }

    public String getTitle() {
        return mTitle;
    }

    public void setTitle(String mTitle) {
        this.mTitle = mTitle;
    }

    public int getCurValue() {
        return mCurValue;
    }

    public void setCurValue(int mCurValue) {
        this.mCurValue = mCurValue;
    }

    public String[] getValues() {
        return mValues;
    }

    public void setValues(String[] mValues) {
        this.mValues = mValues;
    }

    public int getItemType() {
        return mItemType;
    }

    public void setItemType(int mItemType) {
        this.mItemType = mItemType;
    }

    public boolean getBoolValue() {
        return mBoolValue;
    }

    public void setBoolValue(boolean mBoolValue) {
        this.mBoolValue = mBoolValue;
    }

    public int getStartValue() {
        return mStartValue;
    }

    public void setmStartValue(int mStartValue) {
        this.mStartValue = mStartValue;
    }

    public int getEndValue() {
        return mEndValue;
    }

    public void setEndValue(int mEndValue) {
        this.mEndValue = mEndValue;
    }

    public boolean getFocusable() {
        return mFocusable;
    }

    public void setFocusable(boolean mFocusable) {
        this.mFocusable = mFocusable;
    }

    class MyTask extends AsyncTask<Integer, Integer, Integer> {

        @Override
        protected Integer doInBackground(Integer... params) {
            if (mContext == null) {
                return null;
            }
            if (mItemType == MenuConstants.ITEMTYPE_ENUM) {
                if (tempValue != mCurValue) {
                    tempValue = mCurValue;
                    mContext.callBack(mCurValue, params[0]);
                }
            } else if (mItemType == MenuConstants.ITEMTYPE_DIGITAL) {
                if (tempValue != mCurValue) {
                    tempValue = mCurValue;
                    mContext.callBack(mCurValue, params[0]);
                }
            } else if (mItemType == MenuConstants.ITEMTYPE_BOOL) {
                mContext.callBack(mBoolValue, params[0]);
            }
            return null;
        }
    }
}
