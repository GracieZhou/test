//<MStar Software>
//******************************************************************************
// MStar Software
// Copyright (c) 2010 - 2012 MStar Semiconductor, Inc. All rights reserved.
// All software, firmware and related documentation herein ("MStar Software") are
// intellectual property of MStar Semiconductor, Inc. ("MStar") and protected by
// law, including, but not limited to, copyright law and international treaties.
// Any use, modification, reproduction, retransmission, or republication of all
// or part of MStar Software is expressly prohibited, unless prior written
// permission has been granted by MStar.
//
// By accessing, browsing and/or using MStar Software, you acknowledge that you
// have read, understood, and agree, to be bound by below terms ("Terms") and to
// comply with all applicable laws and regulations:
//
// 1. MStar shall retain any and all right, ownership and interest to MStar
//    Software and any modification/derivatives thereof.
//    No right, ownership, or interest to MStar Software and any
//    modification/derivatives thereof is transferred to you under Terms.
//
// 2. You understand that MStar Software might include, incorporate or be
//    supplied together with third party's software and the use of MStar
//    Software may require additional licenses from third parties.
//    Therefore, you hereby agree it is your sole responsibility to separately
//    obtain any and all third party right and license necessary for your use of
//    such third party's software.
//
// 3. MStar Software and any modification/derivatives thereof shall be deemed as
//    MStar's confidential information and you agree to keep MStar's
//    confidential information in strictest confidence and not disclose to any
//    third party.
//
// 4. MStar Software is provided on an "AS IS" basis without warranties of any
//    kind. Any warranties are hereby expressly disclaimed by MStar, including
//    without limitation, any warranties of merchantability, non-infringement of
//    intellectual property rights, fitness for a particular purpose, error free
//    and in conformity with any international standard.  You agree to waive any
//    claim against MStar for any loss, damage, cost or expense that you may
//    incur related to your use of MStar Software.
//    In no event shall MStar be liable for any direct, indirect, incidental or
//    consequential damages, including without limitation, lost of profit or
//    revenues, lost or damage of data, and unauthorized system use.
//    You agree that this Section 4 shall still apply without being affected
//    even if MStar Software has been modified by MStar in accordance with your
//    request or instruction for your use, except otherwise agreed by both
//    parties in writing.
//
// 5. If requested, MStar may from time to time provide technical supports or
//    services in relation with MStar Software to you for your use of
//    MStar Software in conjunction with your or your customer's product
//    ("Services").
//    You understand and agree that, except otherwise agreed by both parties in
//    writing, Services are provided on an "AS IS" basis and the warranty
//    disclaimer set forth in Section 4 above shall apply.
//
// 6. Nothing contained herein shall be construed as by implication, estoppels
//    or otherwise:
//    (a) conferring any license or right to use MStar name, trademark, service
//        mark, symbol or any other identification;
//    (b) obligating MStar or any of its affiliates to furnish any person,
//        including without limitation, you and your customers, any assistance
//        of any kind whatsoever, or any information; or
//    (c) conferring any license or right under any intellectual property right.
//
// 7. These terms shall be governed by and construed in accordance with the laws
//    of Taiwan, R.O.C., excluding its conflict of law rules.
//    Any and all dispute arising out hereof or related hereto shall be finally
//    settled by arbitration referred to the Chinese Arbitration Association,
//    Taipei in accordance with the ROC Arbitration Law and the Arbitration
//    Rules of the Association by three (3) arbitrators appointed in accordance
//    with the said Rules.
//    The place of arbitration shall be in Taipei, Taiwan and the language shall
//    be English.
//    The arbitration award shall be final and binding to both parties.
//
//******************************************************************************
//<MStar Software>

package com.mstar.tv.menu.setting;

import com.mstar.tv.MenuConstants;

import android.view.KeyEvent;

public class SettingItem {
    private BaseActivity mContext;

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
    public SettingItem(BaseActivity context, String title, String[] values, int curValue,
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
    public SettingItem(BaseActivity context, String title, String[] values, boolean curValue,
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
    public SettingItem(BaseActivity context, String title, int startValue, int endValue,
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
    public SettingItem(BaseActivity context, String title, int startValue, int endValue,
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
    public SettingItem(BaseActivity context, String title, int itemType, boolean focusable) {
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
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (mItemType == MenuConstants.ITEMTYPE_ENUM) {
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
            mContext.callBack(mCurValue);
        } else if (mItemType == MenuConstants.ITEMTYPE_DIGITAL) {
            if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                if (mCurValue > mStartValue) {
                    mCurValue--;
                } else if (mRecursive) {
                    mCurValue = mEndValue;
                }
            } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                if (mCurValue < mEndValue) {
                    mCurValue++;
                } else if (mRecursive) {
                    mCurValue = mStartValue;
                }
            }
            mContext.callBack(mCurValue);
        } else if (mItemType == MenuConstants.ITEMTYPE_BOOL) {
            if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                if (mBoolValue) {
                    mBoolValue = false;
                } else {
                    mBoolValue = true;
                }
                mContext.callBack(mBoolValue);
            }
        }
        return true;
    }

    /**
     * the item has been click.
     */
    public void itemClicked() {
        if (mItemType == MenuConstants.ITEMTYPE_BUTTON) {
            mContext.callBack();
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

}
