
package com.eostek.tvmenu.picture;

import android.view.KeyEvent;

public class SettingItem {
    private PictureSettingFragment mFragment;

    private String mTitle;

    private String[] mValues;

    private int mCurValue = 0;

    private boolean mBoolValue = false;

    private int mStartValue;

    private int mEndValue;

    private int mItemType;

    private boolean mFocusable;

    private boolean mRecursive;

    /**
     * constructor for the enumerate.
     * 
     * @param context
     * @param title
     * @param values
     * @param curValue
     * @param itemType
     */
    public SettingItem(PictureSettingFragment context, String title, String[] values, int curValue,
            int itemType, boolean focusable) {
        this.mFragment = context;
        this.mTitle = title;
        this.mValues = values;
        this.mCurValue = curValue;
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
    public SettingItem(PictureSettingFragment context, String title, int startValue, int endValue,
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
    public SettingItem(PictureSettingFragment context, String title, int startValue, int endValue,
            int curValue, int itemType, boolean focusable, boolean recursive) {
        this.mFragment = context;
        this.mTitle = title;
        this.mStartValue = startValue;
        this.mEndValue = endValue;
        this.mCurValue = curValue;
        this.mItemType = itemType;
        this.mFocusable = focusable;
        this.mRecursive = recursive;
    }

    /**
     * the action for the item(non button type).
     * 
     * @param keyCode
     * @param event
     * @return
     */
    public void updateItemValue(int keyCode, int position) {
        if (mItemType == PictureConstants.TYPE_ITEM_ENUM) {
            if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                if (mCurValue > 0) {
                    mCurValue--;
                } else {
                    mCurValue = mValues.length - 1;
                }
            } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                if (mCurValue < mValues.length - 1) {
                    mCurValue++;
                } else {
                    mCurValue = 0;
                }
            }
        } else if (mItemType == PictureConstants.TYPE_ITEM_DIGITAL) {
            if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                if (mCurValue == mStartValue) {
                    return;
                }
                if (mCurValue > mStartValue) {
                    mCurValue--;
                } else if (mRecursive) {
                    mCurValue = mEndValue;
                }
            } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                if (mCurValue == mEndValue) {
                    return;
                }
                if (mCurValue < mEndValue) {
                    mCurValue++;
                } else if (mRecursive) {
                    mCurValue = mStartValue;
                }
            }
        }
        mFragment.mHolder.updateView(position);
        final int pos = position;
        mFragment.mApplication.addTvApiTask(new Runnable() {
            @Override
            public void run() {
                mFragment.mLogic.setTvValue(mCurValue, pos);
            }
        });
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
}
