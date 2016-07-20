
package com.eostek.scifly.ime.util;

import android.content.Context;
import android.util.Log;

/**
 * Constants for application.
 * 
 * @author Youpeng
 */
public class Constans {

    private static final boolean DEBUG = false;

    /**
     * count of each row.
     */
    public static final int ROW_KEY_NUM = 13;

    /**
     * width offset.
     */
    public static final int KEY_WIDTH_OFFSET = -2;

    /**
     * height offset.
     */
    public static final int KEY_HEIGHT_OFFSET = -5;

    /**
     * candidates per page.
     */
    public static final int SUGGESTIONS_PER_PAGE = 22;

    /**
     * command type of words-synchronization. 0:words. 1.ports.
     */
    public static final int TYPE_WORDS = 0;

    /**
     * command type of words-synchronization. 0:words. 1.ports.
     */
    public static final int TYPE_PORT = 1;

    /** Pinyin Ime char input composing string max length */
    public static final int PY_STRING_MAX = 28;

    /** key icon preview x coordinate offset */
    public static final int PREVIEW_Y_OFFSET = -20;

    public static final int PREVIEW_Y_OFFSET_ENTER = -10;

    public static final int PREVIEW_Y_OFFSET_LANGUAGE = -25;

    public static void print(String tag, String msg) {
        if (DEBUG) {
            Log.i(tag, msg);
        }
    }

    public static void printE(String tag, String msg) {
        if (DEBUG) {
            Log.e(tag, msg);
        }
    }
    public static int dip2px(Context context, float dpValue) {  
        final float scale = context.getResources().getDisplayMetrics().density;  
        Log.d("raymond", "屏幕的密度"+scale);
        return (int) (dpValue * scale + 0.5f);  
    }  

}
