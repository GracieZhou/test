
package com.google.tv.eoslauncher.util;

import scifly.device.Device;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;

import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Logger;
import com.google.android.gms.analytics.Tracker;
import com.google.tv.eoslauncher.HomeApplication;
import com.google.tv.eoslauncher.model.MyAD;

public class GoogleAnalyticsUtil {

    // eostek account
    private static final String PROPERTY_ID = "UA-62177441-12";

    private static Tracker mTracker;

    private static final String APP_NAME = "HLauncher";

    private static final String HOMEPAGE = "HomePage";

    private static final String APPLICATIONS = "Applications";

    private static final String MYAPPLICATIONS = "MyApplications";

    private static final String MEDIA = "Media";

    private static final String PANDORA = "Pandora";

    private static final String SHOPPING = "Shopping";

    private static HomeApplication mApplication;

    public static final int FROM_HOMEPAGE = 0;

    public static final int FROM_APPS = 1;

    public static final int FROM_MYAPPS = 2;

    public static final int FROM_SHOPPING = 3;

    public static final int FROM_PANDORA = 4;

    public static void initData(HomeApplication application) {
        mApplication = application;
    }

    synchronized private static Tracker getTracker() {
        if (mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(mApplication);
            analytics.getLogger().setLogLevel(Logger.LogLevel.VERBOSE);
            mTracker = analytics.newTracker(PROPERTY_ID);
            mTracker.setAppName(APP_NAME);
            mTracker.setAppVersion(Device.getVersion());
            mTracker.setClientId(Device.getBb());
            mTracker.setSampleRate(100.0);
        }
        return mTracker;
    }

    private static void sendEvent(Tracker tracker, String category, String action, String label) {
        tracker.send(new HitBuilders.EventBuilder().setCategory(category).setAction(action).setLabel(label).build());
    }

    /**
     * 上报数据
     * @param from FROM_HOMEPAGE, FROM_APPS, FROM_MYAPPS
     * @param adinfo 广告信息
     * @param index 广告位 The first is from '0'
     */
    public static void sendEvent(int from, MyAD adinfo, int index) {
        Tracker tracker = getTracker();
        if (from == FROM_HOMEPAGE) {
            tracker.setScreenName(HOMEPAGE);
        } else if (from == FROM_APPS) {
            tracker.setScreenName(APPLICATIONS);
        } else if (from == FROM_MYAPPS) {
            tracker.setScreenName(MYAPPLICATIONS);
        } else if (from == FROM_SHOPPING) {
            tracker.setScreenName(SHOPPING);
        } else {
            tracker.setScreenName(PANDORA);
        }
        sendEvent(tracker, "Click", "AdClick", adinfo.getGln() + "," + index);
    }

    /**
     * @param from FROM_APPS, FROM_MYAPPS
     * @param pkgName
     */
    public static void sendEvent(int from, String pkgName) {
        Tracker tracker = getTracker();
        if (from == FROM_APPS) {
            tracker.setScreenName(APPLICATIONS);
        } else {
            tracker.setScreenName(MYAPPLICATIONS);
        }
        sendEvent(tracker, "Click", "Launch", pkgName + "," + getClassName(pkgName) + "," + getAppName(pkgName));
    }

    public static void sendEvent(String pkgName, String clazz, boolean isInstalled, String url) {
        Tracker tracker = getTracker();
        tracker.setScreenName(APPLICATIONS);
        if (isInstalled) {
            sendEvent(tracker, "Click", "Launch", pkgName + "," + clazz + "," + getAppName(pkgName));
        } else {
            sendEvent(tracker, "Click", "Download", pkgName + "," + clazz + "," + getAppName(pkgName) + "," + url);
        }
    }

    private static String getAppName(String pkgName) {
        PackageManager pm = mApplication.getPackageManager();
        String appName = null;
        try {
            appName = pm.getApplicationLabel(pm.getApplicationInfo(pkgName, PackageManager.GET_META_DATA)).toString();
        } catch (NameNotFoundException e) {
            e.printStackTrace();
        }
        return appName;
    }

    private static String getClassName(String pkgName) {
        PackageManager pm = mApplication.getPackageManager();
        String className = null;
        try {
            className = pm.getLaunchIntentForPackage(pkgName).getComponent().getClassName();
        } catch (Exception e) {
            e.printStackTrace();
        }
        return className;
    }

    public static void sendEvent(String pkgName, String clazz, String values) {
        Tracker tracker = getTracker();
        tracker.setScreenName(MEDIA);
        sendEvent(tracker, "Click", "Launch", pkgName + "," + clazz + "," + getAppName(pkgName) + "," + values);
    }
}
