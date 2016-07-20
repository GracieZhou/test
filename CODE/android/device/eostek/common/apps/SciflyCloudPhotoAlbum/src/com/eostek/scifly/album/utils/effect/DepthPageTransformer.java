
package com.eostek.scifly.album.utils.effect;

import android.support.v4.view.ViewPager;
import android.view.View;

import com.nineoldandroids.view.ViewHelper;

public class DepthPageTransformer implements ViewPager.PageTransformer {
    private static final float MIN_SCALE = 0.75f;

    public void transformPage(View view, float position) {
        int pageWidth = view.getWidth();
        if (position < -1) {
            ViewHelper.setAlpha(view, 0);
        } else if (position <= 0) {
            ViewHelper.setAlpha(view, 1);
            ViewHelper.setTranslationX(view, 0);
            ViewHelper.setScaleX(view, 1);
            ViewHelper.setScaleY(view, 1);
        } else if (position <= 1) {
            ViewHelper.setAlpha(view, 1 - position);
            ViewHelper.setTranslationX(view, pageWidth * -position);
            float scaleFactor = MIN_SCALE + (1 - MIN_SCALE) * (1 - position);
            ViewHelper.setScaleX(view, scaleFactor);
            ViewHelper.setScaleY(view, scaleFactor);
        } else {
            ViewHelper.setAlpha(view, 0);
        }
    }
}
