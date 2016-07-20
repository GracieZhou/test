
package com.eostek.tv.launcher.util;

import scifly.device.Device;

import android.os.SystemProperties;

import com.eostek.tv.launcher.HomeApplication;
import com.eostek.tv.launcher.model.MetroInfo;
import com.google.android.gms.analytics.GoogleAnalytics;
import com.google.android.gms.analytics.HitBuilders;
import com.google.android.gms.analytics.Logger;
import com.google.android.gms.analytics.Tracker;

public class GoogleAnalyticsUtil {

    // test account
    // private static final String PROPERTY_ID = "UA-64455182-1";

    // eostek account
    private static String TEST_PROPERTY_ID = "UA-62177441-5";
    
    private static String mPropertyId = null;

    private static final String PROPERTY = "persist.sys.ga.property.id";

    private static Tracker mTracker;

    private static final String APP_NAME = "SciflyLauncher";

    private static final String MYTV = "MYTV";

    private static final String TVRESOURCES = "TVResources";

    private static final String APPLICATIONS = "Applications";

    private static final String MYAPPLICATIONS = "MyApplications";

    private static final String SETTINGS = "Settings";

    private static final double SAMPLE_RATE = 100.0;

    private static HomeApplication mApplication;

    /**
     * init data
     * 
     * @param application
     */
    public static void initData(HomeApplication application) {
        mApplication = application;
        mPropertyId = SystemProperties.get(PROPERTY, TEST_PROPERTY_ID);
    }

    synchronized private static Tracker getTracker() {
        if (mTracker == null) {
            GoogleAnalytics analytics = GoogleAnalytics.getInstance(mApplication);
            analytics.getLogger().setLogLevel(Logger.LogLevel.VERBOSE);
            mTracker = analytics.newTracker(mPropertyId);
            mTracker.setAppName(APP_NAME);
            mTracker.setAppVersion(Device.getVersion());
            mTracker.setClientId(Device.getBb());
            mTracker.setSampleRate(SAMPLE_RATE);
        }
        return mTracker;
    }

    private static void sendEvent(Tracker tracker, String category, String action, String label) {
        tracker.send(new HitBuilders.EventBuilder().setCategory(category).setAction(action).setLabel(label).build());
    }

    /**
     * send app info by Tracker
     * 
     * @param metroInfo
     * @param isInstalled
     * @param isFromAllApp
     */
    public static void sendEvent(MetroInfo metroInfo, boolean isInstalled, boolean isFromAllApp) {
        Tracker tracker = getTracker();
        String pkgName = metroInfo.getPkgName();
        String clsName = metroInfo.getClsName();
        if (isFromAllApp) {
            tracker.setScreenName(MYAPPLICATIONS);
            sendEvent(tracker, "Click", "Launch", pkgName + "," + clsName + "," + metroInfo.getTitle());
        } else {
            if ("com.eostek.scifly.video".equals(pkgName)) {
                tracker.setScreenName(TVRESOURCES);
            } else if ("com.android.settings".equals(pkgName)) {
                tracker.setScreenName(SETTINGS);
            } else {
                tracker.setScreenName(APPLICATIONS);
                if (!isInstalled) {
                    sendEvent(tracker, "Click", "Launch", pkgName + "," + clsName + "," + metroInfo.getTitle() + ","
                            + metroInfo.getApkUrl());
                    return;
                }
            }
            sendEvent(tracker, "Click", "Launch", pkgName + "," + clsName + "," + metroInfo.getTitle());
        }
    }
}
