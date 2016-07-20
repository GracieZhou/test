
package com.google.tv.eoslauncher.util;

import java.io.IOException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpHead;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;

import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManagerExtra;
import android.app.WallpaperManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.google.tv.eoslauncher.R;

/**
 * projectName： TVAPK
 * 
 * @moduleName： UIUitl.java
 * @author aaron.zhou
 * @modified by jily.jiang
 * @version 1.0.0
 * @time 2012-12-19
 * @Copyright ©2012 MStar Semiconductor, Inc.
 */
public class UIUtil {

    private final static String TAG = "UIUitl";

    private static final int DISABLE_NONE = 0x0;

    private static final int DISABLE_SYSTEM_INFO = 0x100000;

    private static final int DISABLE_NOTIFICATION_ICONS = 0x20000;

    private static final int DELAY_TIME = 600; // Key Delay time

    private static long startTime = 0;

    // last up key click time
    private static long lastUpKeyTime = 0;

    // last down key click time
    private static long lastDownKeyTime = 0;

    private static Toast mToast;
    
    private static final long CLEAR_MEMORY_LIMIT = 90;

    /**
     * format for the date.
     * 
     * @param time time
     * @param format format
     * @return date
     */
    public static String formatDate(long time, String format) {
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        Date date = new Date(time);
        String curTimeStr = formatter.format(date);
        return curTimeStr;
    }

    /**
     * show toast
     * 
     * @param mContext
     * @param txt
     */
    public static void showToast(Context mContext, String txt) {

        Toast toast = Toast.makeText(mContext, txt, Toast.LENGTH_SHORT);
        toast.setGravity(Gravity.CENTER, toast.getXOffset() / 2, toast.getYOffset() / 2);
        toast.show();
    }

    /**
     * Key Delay
     * 
     * @param
     * @return {boolean}
     */
    public static boolean onDelayKey() {
        boolean state = true;
        if (startTime == 0) {
            startTime = System.currentTimeMillis();
        } else {
            long currentTime = System.currentTimeMillis();
            if (currentTime - startTime < DELAY_TIME) {
                startTime = currentTime;
                state = true;
            } else {
                startTime = 0;
                state = false;
            }
        }
        return state;
    }

    /**
     * If the interval of two clicks is less than DELAY_TIME,the second click is
     * quick click,return true
     * 
     * @param keyCode KeyEvent.KEYCODE_DPAD_UP or KeyEvent.KEYCODE_DPAD_DOWN
     * @return true if it is quick click,false not
     */
    public static boolean isQuickDoubleClick(int keyCode) {
        boolean state = false;
        if (lastUpKeyTime == 0 || lastDownKeyTime == 0) {
            // init lastUpKeyTime first time
            long curentTime = System.currentTimeMillis();
            lastUpKeyTime = curentTime;
            lastDownKeyTime = curentTime;
            return state; // first UpKey click ,return false
        }

        long curTime = System.currentTimeMillis();
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_UP:
                if (curTime - lastUpKeyTime < DELAY_TIME) {
                    state = true;
                }
                lastUpKeyTime = curTime; // change the lastUpKeyTime
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                if (curTime - lastDownKeyTime < DELAY_TIME) {
                    state = true;
                }
                lastDownKeyTime = curTime; // change the lastDownKeyTime
                break;
            default:
                break;
        }
        Log.v(TAG, "isQuickDoubleClick keyCode = " + keyCode + " state = " + state);
        return state;
    }

    public static Bitmap decScreenBitmap(Bitmap bitmap, int x, int y, int width, int height) {
        if (bitmap == null) {
            return null;
        }
        int bitmapWidth = bitmap.getWidth();
        int bitmapHeight = bitmap.getHeight();
        int xOffSet = x;
        int yOffSet = y;

        if ((x + width) > bitmapWidth) {
            xOffSet = bitmapWidth - width;
        }

        if ((y + height) > bitmapHeight) {
            yOffSet = bitmapHeight - height;
        }

        return Bitmap.createBitmap(bitmap, xOffSet, yOffSet, width, height);
    }

    /**
     * eostek history
     * 
     * @param Context context ,HomeActity
     * @return void
     */
    public static void updateHistory(Context context, String packageName, String className) {
        if (packageName.equals("com.eostek.history")) {
            return;
        }
    }

    /**
     * set background
     * 
     * @param context
     * @param view
     */
    public static void setDefaultBackground(Context context, View view) {
        final WallpaperManager wallpaperManager = WallpaperManager.getInstance(context);
        final Drawable wallpaperDrawable = wallpaperManager.getFastDrawable();
        Log.v(TAG, "wallpaperDrawable != null = " + (wallpaperDrawable != null));
        if (wallpaperDrawable != null) {
            view.setBackground(wallpaperDrawable);
        } else {
            view.setBackgroundResource(R.drawable.background);
        }
    }
    
    /**
     * Inquiry by the package name corresponding application name quick
     * 
     * @param Context context ,HomeActity
     * @return String app name
     */
    public static String getAppName(Context context, String packageName) {
        String appName = "";
        PackageManager pm = context.getPackageManager();
        Intent mIntent = new Intent(Intent.ACTION_MAIN, null);
        mIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        mIntent.setPackage(packageName);
        List<ResolveInfo> listAllApps = pm.queryIntentActivities(mIntent, 0);
        if (!listAllApps.isEmpty()) {
            ResolveInfo ri = listAllApps.get(0);
            if (ri != null) {
                appName = (String) ri.loadLabel(pm);
                Log.d(TAG, "getAppName:" + appName);
            }
        }
        return appName;
    }

    /**
     * cheak newwork state
     * 
     * @param String url
     * @return int
     */
    public static int getRespStatus(String url) {
        Log.d(TAG, "---URL :"+url);
        int status = -1;
        if (url.equals("") || url.length() < 5) {
            return 404;
        }
        if (url.substring(0, 5).equals("file:")) {
            return 1;
        }
        try {
            HttpHead head = new HttpHead(url);
            HttpClient client = new DefaultHttpClient();
//            // set connect timeout
//            client.getParams().setIntParameter(HttpConnectionParams.CONNECTION_TIMEOUT,
//                    Constants.NETWORK_CONNECTION_TIMEOUT);
//            // set read data timeout
//            client.getParams().setIntParameter(HttpConnectionParams.SO_TIMEOUT, 
//                    Constants.NETWORK_READ_DATA_TIMEOUT);
            HttpConnectionParams.setConnectionTimeout(client.getParams(), 8000); 

            HttpConnectionParams.setSoTimeout(client.getParams(), 80000); 

            HttpResponse resp = client.execute(head);
            status = resp.getStatusLine().getStatusCode();
        } catch (ClientProtocolException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }finally {
            
        }
        Log.d(TAG, "status:" + status);
        return status;
    }

    /**
     * toast Show.
     * 
     * @param resId string conetent.
     * @param Context .
     * @return {void}.
     */
    public static void toastShow(int resId, Context context) {
        if (mToast == null) {
            mToast = new Toast(context);
        }
        LayoutInflater inflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View view = inflater.inflate(R.layout.toast, null);
        TextView MsgShow = (TextView) view.findViewById(R.id.message);
        MsgShow.setGravity(Gravity.CENTER);
        mToast.setDuration(Toast.LENGTH_LONG);
        MsgShow.setTextSize(20);
        MsgShow.setText(resId);
        mToast.setView(view);
        mToast.show();
    }

    public static boolean checkVersion(String mNewVersion) {

        boolean mHasNewVersion = false;
        final String systemVersion = Tools.getSystemVersion();
        Log.d(TAG, "systemVersion : " + systemVersion);

        if (systemVersion == null) {
            mHasNewVersion = true;
            return mHasNewVersion;
        }
        if (systemVersion.equals(mNewVersion)) {
            mHasNewVersion = false;
            return mHasNewVersion;
        }
        Log.v(TAG, "NewVersion" + mNewVersion);
        if (mNewVersion != null && systemVersion.contains(".")) {
            String systemOne = systemVersion.split("\\.")[0];
            String newOne = mNewVersion.split("\\.")[0];
            if (Integer.parseInt(newOne) > Integer.parseInt(systemOne)) {
                mHasNewVersion = true;
            } else if (Integer.parseInt(newOne) == Integer.parseInt(systemOne)) {
                String systemTwo = systemVersion.split("\\.")[1];
                String newTwo = mNewVersion.split("\\.")[1];
                if (Integer.parseInt(newTwo) > Integer.parseInt(systemTwo)) {
                    mHasNewVersion = true;
                } else if (Integer.parseInt(newTwo) == Integer.parseInt(systemTwo)) {
                    String systemThree = systemVersion.split("\\.")[2];
                    String newThree = mNewVersion.split("\\.")[2];
                    if (Integer.parseInt(newThree) > Integer.parseInt(systemThree)) {
                        mHasNewVersion = true;
                    } else if (Integer.parseInt(newThree) == Integer.parseInt(systemThree)) {
                        String systemFour = "";
                        String newFour = "";
                        if (systemVersion.split("\\.").length >= 4) {
                            systemFour = systemVersion.split("\\.")[3];
                        } else {
                            systemFour = "0";
                        }
                        if (mNewVersion.split("\\.").length >= 4) {
                            newFour = mNewVersion.split("\\.")[3];
                        } else {
                            newFour = "0";
                        }
                        if (Integer.parseInt(newFour) > Integer.parseInt(systemFour)) {
                            mHasNewVersion = true;
                        } else {
                            mHasNewVersion = false;
                        }
                    } else {
                        mHasNewVersion = false;
                    }
                } else {
                    mHasNewVersion = false;
                }
            } else {
                mHasNewVersion = false;
            }
        }

        return mHasNewVersion;
    }
    
    /**
     * set system ui visible
     * 
     * @param context
     * @param value 0 is visible,1 is invisible
     */
    public static void setSystemUIVisible(Context context, int value) {
        Log.v(TAG, "setSystemUIVisible " + value);
        try {
            Object service = context.getSystemService("statusbar");
            Class<?> claz = Class.forName("android.app.StatusBarManager");
            // Parameters Types
            @SuppressWarnings("rawtypes")
            Class[] paramTypes = new Class[1];
            paramTypes[0] = int.class;
            Method setVisibleMethod = claz.getMethod("setSystemUiVisibility", paramTypes);
            // Parameters
            Object[] params = new Object[1];
            params[0] = Integer.valueOf(value);
            setVisibleMethod.setAccessible(true);
            setVisibleMethod.invoke(service, params);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    /**
     * get system used memory infomation to find whether kill backgroud progress
     * 
     * @param context Context object
     * @return return true if the used percentage is over
     *         {@link CLEAR_MEMORY_LIMIT},else false
     */
    public static boolean shouldKillMemory(Context context) {
        MemoryInfo mi = new MemoryInfo();
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        activityManager.getMemoryInfo(mi);
        // Percentage can be calculated for API 16+
        long percentUsed = (mi.totalMem - mi.availMem) * 100 / mi.totalMem;
        Log.v(TAG, "percentUsed = " + percentUsed);
        if (percentUsed > CLEAR_MEMORY_LIMIT) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * kill backgroud progress to save cache memory.
     */
    public static void killProcesses() {
        try {
            ActivityManagerExtra ame = ActivityManagerExtra.getInstance();
            ame.killAllBackgroundApks();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

}