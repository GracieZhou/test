
package com.eostek.scifly.album.utils.effect;

import android.support.v4.view.ViewPager;
import android.view.View;

import com.nineoldandroids.view.ViewHelper;

/**
 * @ClassName: RotateDownPageTransformer.
 * @Description:Rotate.
 * @author: lucky.li.
 * @date: Dec 7, 2015 2:42:11 PM.
 * @Copyright: Eostek Co., Ltd. Copyright , All rights reserved.
 */
public class RotateDownPageTransformer implements ViewPager.PageTransformer {

    private static final float ROT_MAX = 20.0f;

    private float mRot;

    public void transformPage(View view, float position) {

        if (position < -1) {
            ViewHelper.setRotation(view, 0);
        } else if (position <= 1) {
            if (position < 0) {
                mRot = (ROT_MAX * position);
                ViewHelper.setPivotX(view, view.getMeasuredWidth() * 0.5f);
                ViewHelper.setPivotY(view, view.getMeasuredHeight());
                ViewHelper.setRotation(view, mRot);
            } else {
                mRot = (ROT_MAX * position);
                ViewHelper.setPivotX(view, view.getMeasuredWidth() * 0.5f);
                ViewHelper.setPivotY(view, view.getMeasuredHeight());
                ViewHelper.setRotation(view, mRot);
            }
        } else {
            ViewHelper.setRotation(view, 0);
        }
    }
}
