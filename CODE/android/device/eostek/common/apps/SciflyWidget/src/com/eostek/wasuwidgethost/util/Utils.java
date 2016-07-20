
package com.eostek.wasuwidgethost.util;

import java.io.IOException;
import java.io.InterruptedIOException;
import java.util.List;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.conn.ConnectTimeoutException;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.DisplayMetrics;
import android.util.Log;

/**
 * projectName： WasuWidgetHost.
 * moduleName： Utils.java
 *
 * @author chadm.xiang
 * @version 1.0.0
 * @time  2014-8-21 上午10:57:32
 * @Copyright © 2014 Eos Inc.
 */
public final class Utils {

    private Utils() {
        super();
        // TODO Auto-generated constructor stub
    }

    private static final String TAG = Utils.class.getSimpleName();

    private static volatile boolean isNetConnected = false;

    /**
     * link to the given url to get the network state.
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
        httpClient.getParams().setIntParameter(HttpConnectionParams.CONNECTION_TIMEOUT, 5 * 1000);
        // set read data timeout
        httpClient.getParams().setIntParameter(HttpConnectionParams.SO_TIMEOUT, 10 * 1000);
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
     * if the network is connected,call getNetworkState to get the network state.
     * 
     * @param context
     * @return true is the network is OK
     */
    public static boolean isNetworkConnected(Context context) {
        if (isNetConnected(context) && getNetworkState(null)) {
            isNetConnected = true;
            return true;
        } else {
            isNetConnected = false;
            return false;
        }

    }

    /**
     * return true if one of wifi,ethernet,pppoe is connected.
     * 
     * @param context
     * @return return true if the net connected, false otherwise. 
     */
    public static boolean isNetConnected(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] infos = manager.getAllNetworkInfo();
        if (infos != null) {
            for (int i = 0; i < infos.length; i++) {
                NetworkInfo info = infos[i];
                if (info.isConnected()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * get screen factor.
     * @param context
     * @return float screen factor
     */
    public static float getScreenFactor(Context context) {
        DisplayMetrics dm = context.getResources().getDisplayMetrics();
        int wScreen = dm.widthPixels;
        return wScreen / 1080f;
    }

    /**
     * to find whether tha apk is installed.
     * 
     * @param context
     * @param pkgName
     * @param clsName
     * @return true if it is installed,else false
     */
    public static boolean isApkInstalled(Context context, String pkgName, String clsName) {
        boolean installed = false;
        PackageManager pm = context.getPackageManager();
        Intent mIntent = new Intent();
        mIntent.setClassName(pkgName, clsName);
        List<ResolveInfo> listAllApps = pm.queryIntentActivities(mIntent, 0);
        if (listAllApps != null && listAllApps.size() > 0) {
            installed = true;
        }
        return installed;
    }

    /**.
     * start application.
     * 
     * @param context
     * @param pkgName
     * @param clsName
     */
    public static void startApp(Context context, String pkgName, String clsName) {
        ComponentName componentName = new ComponentName(pkgName, clsName);
        Intent startSetting = new Intent(Intent.ACTION_MAIN);
        startSetting.addCategory(Intent.CATEGORY_LAUNCHER);
        startSetting.setComponent(componentName);
        startSetting.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        context.startActivity(startSetting);
    }

}
