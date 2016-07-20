
package com.eostek.tv.launcher.util;

import android.view.View;
import android.view.ViewGroup.MarginLayoutParams;
import android.widget.RelativeLayout;

/*
 * projectName： TVLauncher
 * moduleName： ViewPositionUtil.java
 *
 * @author chadm.xiang
 * @version 1.0.0
 * @time  2014-11-11 上午10:58:37
 * @Copyright © 2014 Eos Inc.
 */

public final class ViewPositionUtil {

    /**
     * get view width
     * 
     * @param view The target view
     * @return The width of the given view
     */
    public static int getWidth(View view) {
        int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(w, h);
        return (view.getMeasuredWidth());
    }

    /**
     * get view height
     * 
     * @param view The target view
     * @return The height of the given view
     */
    public static int getHeight(View view) {
        int w = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        int h = View.MeasureSpec.makeMeasureSpec(0, View.MeasureSpec.UNSPECIFIED);
        view.measure(w, h);
        return (view.getMeasuredHeight());
    }

    /**
     * set the view layout x postion,y is 0 as default
     * 
     * @param view The view to set position
     * @param x the left margin size
     */
    public static void setViewLayoutX(View view, int x) {
        setViewLayout(view, x, 0);
    }

    /**
     * set the view layout y postion,x is 0 as default
     * 
     * @param view The view to set position
     * @param x the top margin size
     */
    public static void setViewLayoutY(View view, int y) {
        setViewLayout(view, 0, y);
    }

    /**
     * set the view layout position
     * 
     * @param view The view to set position
     * @param x the left margin size
     * @param y the top margin size
     */
    public static void setViewLayout(View view, int x, int y) {
        MarginLayoutParams margin = new MarginLayoutParams(view.getLayoutParams());
        margin.setMargins(x, y, x + margin.width, y + margin.height);
        RelativeLayout.LayoutParams layoutParams = new RelativeLayout.LayoutParams(margin);
        view.setLayoutParams(layoutParams);
    }
}
