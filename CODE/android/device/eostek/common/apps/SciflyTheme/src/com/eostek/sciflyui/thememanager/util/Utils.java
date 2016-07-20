
package com.eostek.sciflyui.thememanager.util;

import android.content.Context;
import android.view.Gravity;
import android.widget.Toast;

/**
 * Utils for theme manager.
 */
public class Utils {

    /**
     * @param mContext mContext
     * @param txt txt
     */
    public static void showToast(Context mContext, String txt) {
        Toast toast = Toast.makeText(mContext, txt, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, toast.getXOffset() / 2, toast.getYOffset() / 2);
        toast.show();
    }

    /**
     * @param context context
     * @param pxValue pxValue
     * @return dp
     */
    public static int pxToDp(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * @param context context
     * @param dpValue dpValue
     * @return pixel
     */
    public static int dpToPx(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

}
