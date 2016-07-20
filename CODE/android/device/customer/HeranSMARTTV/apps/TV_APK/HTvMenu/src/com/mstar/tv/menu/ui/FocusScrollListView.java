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

package com.mstar.tv.menu.ui;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ListView;
import android.widget.Scroller;

public class FocusScrollListView extends ListView {

    private int mDuration = 300;

    private Scroller mScroller;

    private Bitmap mBitmap;

    private int mCurrentY;

    private int mItemHeight;

    private boolean mHadHeight = false;

    private int mTop;

    private Matrix mMatrix;

    private int mTrimming = 0;

    private boolean mIsSetSelection = false;

    private int mLastSelectItemPosition = 0;

    public FocusScrollListView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mScroller = new Scroller(context);
        mMatrix = new Matrix();
    }

    @Override
    protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
        super.onMeasure(widthMeasureSpec, heightMeasureSpec);
    }

    @Override
    protected void onLayout(boolean changed, int l, int t, int r, int b) {
        super.onLayout(changed, l, t, r, b);

        // add the adjust avoid the error when the listview initial.
        if (getChildCount() > 0) {
            if (!mHadHeight) {
                mItemHeight = getChildAt(0).getHeight();
                mHadHeight = true;
            }
            int itemWidth = getWidth();

            float sx = (float) itemWidth / mBitmap.getWidth();
            float sy = (float) (mItemHeight + mTrimming) / mBitmap.getHeight();

            mMatrix.setScale(sx, sy);

            mBitmap = Bitmap.createBitmap(mBitmap, 0, 0, mBitmap.getWidth(), mBitmap.getHeight(),
                    mMatrix, true);
        }
    }

    @Override
    public void setSelection(int position) {
        super.setSelection(position);
        mLastSelectItemPosition = position;
        mIsSetSelection = true;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        if (mIsSetSelection) {
            if (null != getSelectedView()) {
                canvas.drawBitmap(mBitmap, 0, getSelectedView().getTop(), null);
                mScroller.setFinalY(getSelectedView().getTop());
                mIsSetSelection = false;
                return;
            }
        }

        if (mScroller.computeScrollOffset()) {
            invalidate();
        }
        mCurrentY = mScroller.getCurrY();
        canvas.drawBitmap(mBitmap, 0, mCurrentY, null);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_PAGE_DOWN || keyCode == KeyEvent.KEYCODE_PAGE_UP) {
            return false;
        }

        View view = getSelectedView();

        if (null != view) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_DPAD_DOWN:
                    if (getSelectedItemPosition() < getLastVisiblePosition()) {
                        mLastSelectItemPosition = getSelectedItemPosition() + 1;
                        mTop = view.getTop() + mItemHeight + getDividerHeight();
                        // whether the next view's bottom is out of the screen
                        // or not.
                        if (mTop + mItemHeight + ((View) view.getParent()).getTop() > ((View) view
                                .getParent()).getBottom()) {
                            mScroller.startScroll(0, view.getTop(), 0,
                                    ((View) view.getParent()).getBottom() - view.getBottom()
                                            - ((View) view.getParent()).getTop(), mDuration);
                        } else {
                            mScroller.startScroll(0, view.getTop(), 0, mTop - view.getTop(),
                                    mDuration);
                        }
                    }
                    break;
                case KeyEvent.KEYCODE_DPAD_UP:
                    if (getSelectedItemPosition() > getFirstVisiblePosition()) {
                        mLastSelectItemPosition = getSelectedItemPosition() - 1;
                        mTop = view.getTop() - mItemHeight - getDividerHeight();
                        if (mTop < 0) {
                            mTop = getDividerHeight();
                        }
                        mScroller.startScroll(0, view.getTop(), 0, mTop - view.getTop(), mDuration);
                    }
                    break;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_PAGE_DOWN || keyCode == KeyEvent.KEYCODE_PAGE_UP) {
            return false;
        }
        return super.onKeyUp(keyCode, event);
    }

    /**
     * set the focus bitmap.
     * 
     * @param resourceId
     */
    public void setFocusBitmap(int resourceId) {
        mBitmap = BitmapFactory.decodeResource(getResources(), resourceId);
    }

    /**
     * set the trimming width for the background if you want the background in
     * the middle.
     * 
     * @param width
     */
    public void settrimming(int width) {
        mTrimming = width;
    }

    /**
     * get the last select item position.
     * 
     * @return
     */
    public int getLastSelectedItemPosition() {
        return mLastSelectItemPosition;
    }

    public void setDuration(int duration) {
        this.mDuration = duration;
    }
}
