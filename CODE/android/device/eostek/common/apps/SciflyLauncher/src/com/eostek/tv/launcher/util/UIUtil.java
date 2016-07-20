
package com.eostek.tv.launcher.util;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.lang.reflect.Method;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.json.JSONException;
import org.json.JSONObject;

import scifly.util.LogUtils;

import com.eostek.tv.launcher.R;
import com.eostek.tm.cpe.manager.CpeManager;
import com.eostek.tv.launcher.HomeApplication;

import android.app.ActivityManager;
import android.app.ActivityManagerExtra;
import android.app.WallpaperManager;
import android.app.ActivityManager.MemoryInfo;
import android.content.ActivityNotFoundException;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.content.pm.PackageManager.NameNotFoundException;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Build;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.View;
import android.view.animation.ScaleAnimation;
import android.widget.Toast;

/*
 * projectName： TVLauncher
 * moduleName： UIUtil.java
 *
 * @author chadm.xiang
 * @version 1.0.0
 * @time  2014-7-17 下午1:44:32
 * @Copyright © 2014 Eos Inc.
 */

public final class UIUtil {

    private static final String TAG = UIUtil.class.getSimpleName();

    private static final String LAUNCHER_PREF = "launcher";

    private static final float SCALE_CENTER = 0.5f;

    private static final long CLEAR_MEMORY_LIMIT = 80;

    private UIUtil() {
    }

    /**
     * @param mContext
     * @param path
     * @return the resource id if exsits,else return 0
     */
    public static int getResourceId(Context mContext, String path) {
        int resID = mContext.getResources().getIdentifier(path, "drawable", "com.eostek.tv.launcher");
        return resID;
    }

    /**
     * start an application
     * 
     * @param context
     * @param pckName
     * @param clsName
     */
    public static void startApk(Context context, String pckName, String clsName, String extraStr, int extraInt) {
        // Log.v(TAG, "pckName = " + pckName);
        // for wasu apk
        if (pckName.equals("cn.com.wasu.main") && !TextUtils.isEmpty(extraStr)) {
            Intent intent = new Intent();
            intent.setAction("com.sihuatech.broadcast_WEB_URL");
            intent.putExtra("WEB_URL", extraStr);
            intent.putExtra("packageName", "cn.com.wasu.main");
            intent.putExtra("startActivity", "WasuTVMainActivity");
            intent.setFlags(Intent.FLAG_INCLUDE_STOPPED_PACKAGES);
            context.sendBroadcast(intent);
            Log.v(TAG, "startApk sendBroadcast");
        } else if (pckName.equals("com.voole.epg")) {
            Intent intent = new Intent();
            intent.setAction(clsName);

            if (!TextUtils.isEmpty(extraStr)) {
                try {
                    JSONObject jsonObject = new JSONObject(extraStr);
                    Iterator<?> iterator = jsonObject.keys();

                    while (iterator.hasNext()) {
                        String key = iterator.next().toString();
                        intent.putExtra(key, jsonObject.getString(key));
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }

            Log.d(TAG, "startApk intent==>" + intent + "\n" + "extra==>" + intent.getExtras());
            context.startActivity(intent);
        }else if (pckName.equals("com.android.settings")) {
            Intent intent = new Intent();
            ComponentName componentName = new ComponentName(pckName, clsName);
            intent.setComponent(componentName);
            context.startActivity(intent);
        }
        else {
            ComponentName componentName = new ComponentName(pckName, clsName);
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.setComponent(componentName);
            if (clsName.equals("com.android.settings.Settings$DevelopmentSettingsActivity")) {
                intent.putExtra("report", true);
            } else if (pckName.equals(LConstants.SCIFLY_VIDEO)) {
                if (!TextUtils.isEmpty(extraStr)) {
                    // for SciflyVideo
                    intent.putExtra("code", extraStr);
                }
            }
            // if tha acitivity in current package,can not start in a new task
            if (!pckName.equals(context.getPackageName())) {
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            }
            try {
                context.startActivity(intent);
            } catch (ActivityNotFoundException e) {
                Log.v(TAG, "Failed to start " + clsName);
            }
            Log.v(TAG, "startApk startActivity");
        }

    }

    /**
     * @param context
     * @param pkgName
     * @param clsName
     * @param type 1 stand for apk,2 for webview ,3 for widget
     * @return true if apk is installed,else false
     */
    public static boolean isApkInstalled(Context context, String pkgName, String clsName, int type) {
        boolean installed = false;
        PackageManager pm = context.getPackageManager();
        Intent mIntent = new Intent();
        mIntent.setClassName(pkgName, clsName);

        List<ResolveInfo> listAllApps;
        if (type == LConstants.METRO_ITEM_WIDGET) {
            // widget
            listAllApps = pm.queryBroadcastReceivers(mIntent, 0);
        } else if (type == LConstants.METRO_ITEM_APK) {
            // apk
            listAllApps = pm.queryIntentActivities(mIntent, 0);
        } else {
            listAllApps = null;
        }
        if (listAllApps != null && listAllApps.size() > 0) {
            installed = true;
        }
        return installed;
    }

    /**
     * show a toast
     * 
     * @param context
     */
    public static void showToast(Context context) {
        Toast.makeText(context, context.getResources().getString(R.string.app_not_installed), Toast.LENGTH_SHORT)
                .show();
    }

    public static void showToast(Context context, String resid) {
        Toast.makeText(context, resid, Toast.LENGTH_SHORT).show();
    }

    /**
     * uninstall the given application
     * 
     * @param context
     * @param packageName
     */
    public static void uninstallAPK(Context context, String packageName) {
        Uri uri = Uri.parse("package:" + packageName);
        Intent intent = new Intent(Intent.ACTION_DELETE, uri);
        context.startActivity(intent);
    }

    /**
     * install apk
     * 
     * @param context
     * @param filePath The apk file path,like /sdcard/qq.apk
     */
    public static void install(Context context, String filePath) {
        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setDataAndType(Uri.parse("file://" + filePath), "application/vnd.android.package-archive");
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }

    public static int[] getScreenHieghtPX(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        int wScreen = dm.widthPixels;
        int hScreen = dm.heightPixels;
        return new int[] {
                wScreen, hScreen
        };
    }

    /**
     * link to the given url to get the network state
     * 
     * @return true if the response is HttpStatus.SC_OK,else false
     */
    public static boolean getNetworkState(String url) {
        boolean isAviable = false;
        if (url == null) {
            url = "http://www.baidu.com/";
        }
        Log.v(TAG, " url = " + url);
        HttpGet httpGet = new HttpGet(url);
        HttpClient httpClient = new DefaultHttpClient();
        // set connect timeout
        httpClient.getParams().setIntParameter(HttpConnectionParams.CONNECTION_TIMEOUT,
                LConstants.NETWORK_CONNECTION_TIMEOUT);
        // set read data timeout
        httpClient.getParams().setIntParameter(HttpConnectionParams.SO_TIMEOUT, LConstants.NETWORK_READ_DATA_TIMEOUT);
        try {
            HttpResponse response = httpClient.execute(httpGet);
            if (response != null && response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                isAviable = true;
            }
        } catch (ConnectTimeoutException e) {
            Log.e(TAG, "network state : CONNECTION_TIMEOUT");
        } catch (InterruptedIOException e) {
            Log.e(TAG, "network state : SO_TIMEOUT");
        } catch (ClientProtocolException e) {
            Log.e(TAG, "network state : ClientProtocolException");
        } catch (IOException e) {
            Log.e(TAG, "network state : IOException");
        } finally {
            // close the connection
            httpClient.getConnectionManager().shutdown();
        }
        return isAviable;
    }

    /**
     * if the network is connected,call getNetworkState to get the network state
     * 
     * @param context
     * @return true is the network is OK
     */
    public static boolean isNetworkConnected(Context context) {
        return isNetConnected(context) && getNetworkState(null);
    }

    /**
     * return true if one of wifi,ethernet,pppoe is connected
     * 
     * @param context
     * @return return if the net is connected,else false
     */
    public static boolean isNetConnected(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] infos = manager.getAllNetworkInfo();
        if (infos != null) {
            for (int i = 0; i < infos.length; i++) {
                NetworkInfo info = infos[i];
                if (info.isConnected()) {
                    Log.v(TAG, "isNetConnected = true");
                    return true;
                }
            }
        }
        Log.v(TAG, "isNetConnected = false");
        return false;
    }

    /**
     * get version name of the application
     * 
     * @param mContext
     * @return The apk version name
     */
    public static String getVersionName(Context mContext) {
        String versionName = "";
        PackageInfo info;
        try {
            info = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
            versionName = info.versionName;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionName;
    }

    /**
     * get the version code of application
     * 
     * @param mContext
     * @return The apk version code
     */
    public static int getVersionCode(Context mContext) {
        int versionCode = 0;
        PackageInfo info;
        try {
            info = mContext.getPackageManager().getPackageInfo(mContext.getPackageName(), 0);
            versionCode = info.versionCode;
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return versionCode;
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
            view.setBackgroundResource(R.drawable.bg);
        }
    }

    /**
     * scale image
     */
    public static Drawable scaleImage(Context context, Drawable image, float scaleFactor) {
        if ((image == null) || !(image instanceof BitmapDrawable)) {
            return image;
        }
        Bitmap b = ((BitmapDrawable) image).getBitmap();
        int sizeX = Math.round(image.getIntrinsicWidth() * scaleFactor);
        int sizeY = Math.round(image.getIntrinsicHeight() * scaleFactor);
        Bitmap bitmapResized = Bitmap.createScaledBitmap(b, sizeX, sizeY, false);
        image = new BitmapDrawable(context.getResources(), bitmapResized);
        bitmapResized.recycle();
        bitmapResized = null;
        return image;
    }

    /**
     * to find out whether the application is installed in the device's system
     * 
     * @param context context
     * @param pkgName The application package name
     * @return true if the app is installed in the device's system,else false
     */
    public static boolean isSystemApp(Context context, String pkgName) {
        PackageManager manager = context.getPackageManager();
        try {
            PackageInfo packageInfo = manager.getPackageInfo(pkgName, PackageManager.GET_CONFIGURATIONS);
            if ((packageInfo.applicationInfo.flags & android.content.pm.ApplicationInfo.FLAG_SYSTEM) != 0) {
                return true;
            }
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return false;
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
            params[0] = new Integer(value);
            setVisibleMethod.setAccessible(true);
            setVisibleMethod.invoke(service, params);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * to get {@link android.media.AudioManager #isMasterMute} value,because the
     * isMasterMute is hide,we use reflect to do this
     * 
     * @param context Context object
     * @return true if the system is mute,else false
     */
    public static boolean isMasterMute(Context context) {
        try {
            Object service = context.getSystemService(Context.AUDIO_SERVICE);
            Class<?> claz = Class.forName("android.media.AudioManager");
            Method setVisibleMethod = claz.getMethod("isMasterMute");
            setVisibleMethod.setAccessible(true);
            return (Boolean) setVisibleMethod.invoke(service);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * get boolean value from xml file {@link SharedPreferences#getBoolean}
     * 
     * @param ctx Context object
     * @param key The name of the preference to retrieve
     * @return Returns the preference value if it exists, or defValue. Throws
     *         ClassCastException if there is a preference with this name that
     *         is not a boolean.
     */
    public static boolean getBooleanFromXml(Context ctx, String key) {
        SharedPreferences sPreferences = ctx.getSharedPreferences(LAUNCHER_PREF, Context.MODE_PRIVATE);
        return sPreferences.getBoolean(key, false);
    }

    /**
     * write boolean value to xml file {@link SharedPreferences#putBoolean}
     * 
     * @param ctx Context object
     * @param key The name of the preference to retrieve
     * @param value The new value for the preference
     */
    public static void writeBooleanToXml(Context ctx, String key, boolean value) {
        SharedPreferences sPreferences = ctx.getSharedPreferences(LAUNCHER_PREF, Context.MODE_PRIVATE);
        Editor editor = sPreferences.edit();
        editor.putBoolean(key, value);
        editor.commit();
    }

    /**
     * get string value from xml file {@link SharedPreferences#getString}
     * 
     * @param ctx Context object
     * @param key The name of the preference to retrieve
     * @return Returns the preference value if it exists, or defValue. Throws
     *         ClassCastException if there is a preference with this name that
     *         is not a boolean.
     */
    public static String getStringFromXml(Context ctx, String key) {
        SharedPreferences sPreferences = ctx.getSharedPreferences(LAUNCHER_PREF, Context.MODE_PRIVATE);
        return sPreferences.getString(key, null);
    }

    /**
     * write string value to xml file {@link SharedPreferences#putString}
     * 
     * @param ctx Context object
     * @param key The name of the preference to retrieve
     * @param value The new value for the preference
     */
    public static void writeStringToXml(Context ctx, String key, String value) {
        SharedPreferences sPreferences = ctx.getSharedPreferences(LAUNCHER_PREF, Context.MODE_PRIVATE);
        Editor editor = sPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }

    /**
     * kill the app itself
     * 
     * @param killSafely
     */
    public static void killApp(boolean killSafely) {
        if (killSafely) {
            /*
             * Notify the system to finalize and collect all objects of the app
             * on exit so that the virtual machine running the app can be killed
             * by the system without causing issues. NOTE: If this is set to
             * true then the virtual machine will not be killed until all of its
             * threads have closed.
             */
            // System.runFinalizersOnExit(true);

            /*
             * Force the system to close the app down completely instead of
             * retaining it in the background. The virtual machine that runs the
             * app will be killed. The app will be completely created as a new
             * app in a new virtual machine running in a new process if the user
             * starts the app again.
             */
            System.exit(0);
        } else {
            /*
             * Alternatively the process that runs the virtual machine could be
             * abruptly killed. This is the quickest way to remove the app from
             * the device but it could cause problems since resources will not
             * be finalized first. For example, all threads running under the
             * process will be abruptly killed when the process is abruptly
             * killed. If one of those threads was making multiple related
             * changes to the database, then it may have committed some of those
             * changes but not all of those changes when it was abruptly killed.
             */
            android.os.Process.killProcess(android.os.Process.myPid());
        }

    }

    /**
     * get BB code
     * 
     * @return The bb code
     */
    public static String getBBCode() {
        CpeManager manager = CpeManager.getInstance();
        String bbNum = manager.getBBNumber();
        Log.v(TAG, "getBBCode = " + bbNum);
        return bbNum;
    }

    /**
     * get platform special code
     * 
     * @return The special code
     */
    public static String getSpecialCode() {
        CpeManager manager = CpeManager.getInstance();
        String codeStr = manager.getProductClass();
        Log.v(TAG, "getSpecialCode = " + codeStr);
        if (codeStr == null) {
            if (HomeApplication.isHasTVModule()) {
                codeStr = "EOS0NK200000TV00";
            } else {
                codeStr = "EOS0RK3066A0DGB0";
            }
        }
        return codeStr;
    }

    public static void viewScaleUp(Context context, View view, float xValue, float yValue) {
        ScaleAnimation inAnimation = new ScaleAnimation(1.0f, xValue, 1.0f, yValue, ScaleAnimation.RELATIVE_TO_SELF,
                SCALE_CENTER, ScaleAnimation.RELATIVE_TO_SELF, SCALE_CENTER);
        inAnimation.setDuration(context.getResources().getInteger(R.integer.scale_animation_duration));
        inAnimation.setFillAfter(true);
        view.startAnimation(inAnimation);
    }

    public static void viewScaleDown(Context context, View view, float xValue, float yValue) {
        ScaleAnimation inAnimation = new ScaleAnimation(xValue, 1.0f, yValue, 1.0f, ScaleAnimation.RELATIVE_TO_SELF,
                SCALE_CENTER, ScaleAnimation.RELATIVE_TO_SELF, SCALE_CENTER);
        inAnimation.setDuration(context.getResources().getInteger(R.integer.scale_animation_duration));
        inAnimation.setFillAfter(true);
        view.startAnimation(inAnimation);
    }

    /**
     * udpate log when error
     * 
     * @param context
     * @param title
     */
    public static void uploadLog(Context context, String title) {
        LogUtils.IResultListener resultListener = new LogUtils.IResultListener() {

            @Override
            public void captureResult(boolean result) {
                Log.d(TAG, "captureLog result:" + result);
            }
        };
        LogUtils.captureLog(context, title, resultListener);
    }

    /**
     * upload network data error ,the title is the error etag
     * 
     * @param context
     */
    public static void uploadNetworkDataError(Context context,String log) {
        if (isSystemApp(context, context.getPackageName())) {
            String title = "ETag:" + getStringFromXml(context, "ETag") + ";" + log;
            uploadLog(context, title);
        }
    }

    /**
     * Get the value for the given key.
     * 
     * @return an empty string if the key isn't found
     */
    public static String get(Context context, String key) {
        String ret = "";
        try {
            ClassLoader cl = context.getClassLoader();
            @SuppressWarnings("rawtypes")
            Class systemProperty = cl.loadClass("android.os.SystemProperties");

            // Parameters Types
            @SuppressWarnings("rawtypes")
            Class[] paramTypes = new Class[1];
            paramTypes[0] = String.class;

            Method get = systemProperty.getMethod("get", paramTypes);

            // Parameters
            Object[] params = new Object[1];
            params[0] = new String(key);

            ret = (String) get.invoke(systemProperty, params);
        } catch (IllegalArgumentException iAE) {
            throw iAE;
        } catch (Exception e) {
            ret = "";
        }
        return ret;
    }

    /**
     * change drawable object to bitmap object
     * 
     * @param drawable The object to convert
     * @return The bitmap object
     */
    public static Bitmap drawableToBitmap(Drawable drawable) {
        if (drawable instanceof BitmapDrawable) {
            return ((BitmapDrawable) drawable).getBitmap();
        }

        Bitmap bitmap = Bitmap.createBitmap(drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(),
                Config.ARGB_8888);
        Canvas canvas = new Canvas(bitmap);
        drawable.setBounds(0, 0, canvas.getWidth(), canvas.getHeight());
        drawable.draw(canvas);

        return bitmap;
    }

    /**
     * get the size of bitmap
     * 
     * @param data The target bitmap
     * @return The bitmap size
     */
    public static int sizeOfBitmap(Bitmap data) {
        if (Build.VERSION.SDK_INT < Build.VERSION_CODES.HONEYCOMB_MR1) {
            return data.getRowBytes() * data.getHeight() / 1024;
        } else {
            return data.getByteCount() / 1024;
        }
    }

    /**
     * get the country language and country
     * 
     * @return the country flag,contains language and country,like zh-cn
     */
    public static String getLanguage() {
        String lan = Locale.getDefault().getLanguage();
        String country = Locale.getDefault().getCountry();
        return lan + "-" + country.toLowerCase();
    }

    public static String formatDate(String format) {
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        Date date = new Date();
        String curTimeStr = formatter.format(date);
        return curTimeStr;
    }

    public static String formatDate(String format, Locale locale) {
        SimpleDateFormat formatter = new SimpleDateFormat(format, locale);
        Date date = new Date();
        String curTimeStr = formatter.format(date);
        return curTimeStr;
    }

    public static boolean isTime24(Context ctx) {
        ContentResolver cv = ctx.getContentResolver();
        String strTimeFormat = android.provider.Settings.System.getString(cv,
                android.provider.Settings.System.TIME_12_24);
        Log.v(TAG, "isTime24 strTimeFormat = " + strTimeFormat);
        return null != strTimeFormat && strTimeFormat.equals("24");
    }

    public static String getHourFormat(Context context) {
        return UIUtil.isTime24(context) ? "HH" : "hh";
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
        long percentUsed = (mi.totalMem - mi.availMem) * LConstants.HUNDRED / mi.totalMem;
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
