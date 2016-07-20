
package com.heran.launcher2.util;

import java.io.File;
import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;

import com.mstar.android.pppoe.PppoeManager;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.SystemProperties;
import android.util.Log;

/*
 * projectName： EosLauncher
 * moduleName： Utils.java
 * @author chadm.xiang
 * @version 1.0.0
 * @time  2014-1-16 上午11:43:21
 * @Copyright © 2013 Eos Inc.
 */

public class Utils {
    public static volatile boolean isNetworkState = false;

    public static volatile boolean isInMessageFragment = false;

    public static volatile boolean isInNewsFragment = false;

    private final static String TAG = "Utils";

    public static final String SHARE_PREFENER_XML = "launcher";

    public static final String IS_91Q_INSTALL_PROPERTY = "is_91q_click";

    /**
     * return whether the app has installed, the main activity must contain
     * Intent.CATEGORY_LAUNCHER
     * 
     * @param context
     * @param packageName
     * @param className
     * @return True if the app has installed,else false
     */
    public static boolean isAppInstalled(Context context, String packageName) {
        final PackageManager packageManager = context.getPackageManager();
        final Intent mIntent = new Intent(Intent.ACTION_MAIN, null);
        mIntent.addCategory(Intent.CATEGORY_LAUNCHER);
        // Get all applications installed on the system
        List<ResolveInfo> listAllApps = packageManager.queryIntentActivities(mIntent, 0);
        int size = listAllApps.size();
        for (int i = 0; i < size; i++) {
            if (listAllApps.get(i).activityInfo.packageName.equals(packageName)) {
                return true;
            }
        }
        return false;
    }

    /**
     * return whether the app exist
     * 
     * @param context
     * @param packageName
     * @param className
     * @return True if the app exist,else false
     */
    public static boolean isApkExist(Context context, String packageName) {
        if (packageName == null || "".equals(packageName))
            return false;
        android.content.pm.ApplicationInfo info = null;
        try {
            info = context.getPackageManager().getApplicationInfo(packageName, 0);
            return info != null;
        } catch (NameNotFoundException e) {
            return false;
        }
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
        Log.v("Utils", " url = " + url);

        HttpGet httpGet = new HttpGet(url);
        HttpClient httpClient = new DefaultHttpClient();
        // set connect timeout
        httpClient.getParams().setIntParameter(HttpConnectionParams.CONNECTION_TIMEOUT,
                Constants.NETWORK_CONNECTION_TIMEOUT);
        // set read data timeout
        httpClient.getParams().setIntParameter(HttpConnectionParams.SO_TIMEOUT, Constants.NETWORK_READ_DATA_TIMEOUT);
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
            Log.d(TAG, "finally");
            httpClient.getConnectionManager().shutdown();
        }
        Log.d(TAG, "return isAviable");
        return isAviable;
    }

    /**
     * if the network is connected,call getNetworkState to get the network state
     * 
     * @param context
     * @return true is the network is OK
     */
    public static boolean isNetworkConnected(Context context) {
        if (isNetConnected(context) && getNetworkState("http://www.baidu.com")) {
            return true;
        } else {
            return false;
        }
    }

    /**
     * return true if one of wifi,ethernet,pppoe is connected
     * 
     * @param context
     * @return
     */
    public static boolean isNetConnected(Context context) {

        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo wifiInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        NetworkInfo ethernetInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET);
        NetworkInfo pppoeInfo = manager.getNetworkInfo(ConnectivityManager.TYPE_PPPOE);
        // the state of wifi network
        if (wifiInfo != null && wifiInfo.isConnected()) {
            Log.d(TAG, "wifi is connect");
            return true;
        }
        // the state of ethernet network
        if (ethernetInfo != null && ethernetInfo.isConnected()) {
            Log.d(TAG, "ethernet is connect");
            return true;
        }
        // the state of pppoe network
         if (pppoeInfo != null && pppoeInfo.isConnected()) {
             Log.d(TAG, "pppoe is connect");
             return true;
         }

        return false;
    }

    private static boolean isPppoeConnected(Context context) {
        PppoeManager mPppoeManager = null;
        try {
            mPppoeManager = PppoeManager.getInstance(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (mPppoeManager != null && mPppoeManager.getPppoeStatus() != null
                && mPppoeManager.getPppoeStatus().equals(mPppoeManager.PPPOE_STATE_CONNECT)) {
            return true;
        }
        return false;
    }

    /**
     * get the current top activity name
     * 
     * @param context
     * @return
     */
    public static String getCurrentActivity(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningTaskInfo> forGroundActivity = activityManager.getRunningTasks(1);
        RunningTaskInfo currentActivity;
        currentActivity = forGroundActivity.get(0);
        String activityName = currentActivity.topActivity.getClassName();
        if (activityName == null) {
            return "";
        }
        Log.d(TAG, "current activity:"+activityName);
        return activityName;
    }

    public static boolean getIs91QInstall(Context context) {
        SharedPreferences preferences = context.getSharedPreferences(SHARE_PREFENER_XML, Context.MODE_PRIVATE);
        return preferences.getBoolean(IS_91Q_INSTALL_PROPERTY, false);
    }

    public static void setIs91QInstall(Context context, boolean value) {
        SharedPreferences preferences = context.getSharedPreferences(SHARE_PREFENER_XML, Context.MODE_PRIVATE);
        Editor editor = preferences.edit();
        editor.putBoolean(IS_91Q_INSTALL_PROPERTY, value);
        editor.commit();
    }

    public static void install(Context context, String filePath) {

        Intent i = new Intent(Intent.ACTION_VIEW);
        i.setDataAndType(Uri.parse("file://" + filePath), "application/vnd.android.package-archive");
        i.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        context.startActivity(i);
    }

    public static boolean isAdVideoFinish() {
        if ("1".equals(SystemProperties.get("mstar.videoadvert.finished", "0"))) {
            return true;
        } else {
            return false;
        }
    }

    public static String getResources(Context context, int id) {
        return context.getResources().getString(id);

    }
    
    public static ArrayList<String> getMountedUsbDevices(String rootPath){
        String regex = "sd[a-z][0-9]?";
        Pattern pattern = Pattern.compile(regex);
        
        ArrayList<String> paths = new ArrayList<String>();
        
        File usbMountDir =  new File(rootPath);
        if(usbMountDir.exists()){
            File[] dirs = usbMountDir.listFiles();
            for(File dir:dirs){
                if(dir.isDirectory()){
                    String dirName = dir.getName();
                    Matcher matcher = pattern.matcher(dirName);
                    if(matcher.matches()){
                        paths.add(dir.getAbsolutePath());
                    }
                }
            }
        }
        
        return paths;
    }
}
