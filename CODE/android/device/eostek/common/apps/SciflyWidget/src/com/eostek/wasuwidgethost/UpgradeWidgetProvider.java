
package com.eostek.wasuwidgethost;

import org.json.JSONException;
import org.json.JSONObject;

import scifly.provider.SciflyStore;
import scifly.provider.SciflyStore.Global;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.RemoteViews;

import com.eostek.scifly.widget.R;
import com.eostek.wasuwidgethost.business.DownloadObserver;
import com.eostek.wasuwidgethost.business.JSONData;
import com.eostek.wasuwidgethost.util.SettingsObserver;
import com.eostek.wasuwidgethost.util.Utils;

/**
 * projectName： WasuWidgetHost.
 * moduleName： UpgradeWidgetProvider.java
 *
 * @author chadm.xiang
 * @version 1.0.0
 * @time  2014-9-12 11:37:39 am
 * @Copyright © 2014 Eos Inc.
 */

public class UpgradeWidgetProvider extends BaseAppWidgetProvider {

    private static final String TAG = UpgradeWidgetProvider.class.getSimpleName();

    private static final String CLICK_NAME_ACTION = "com.eostek.wasuwidgethost.UpgradeWidgetProvider";

    private static final String NETWORK_OK_ACTION = "com.eostek.network_ok";

    // get default download id is -1
    private static final int NO_DOWNLOAD_THREAD = -1;

    // there is a new veriosn,but not start download yet
    private static final int NEW_VERISON_WITHOUT_DOWNLOAD = -2;

    private static final int UPDATE_WIDGET = 0x100;

    private static final int CHECK_NEW_VERSION = 0x101;

    private static final int UPDATE_DOWNLOAD_PERCENT = 0x102;

    private static final int MSG_DOWNLOAD_CHANGE = 101;

    private static Context mContext;

    private static RemoteViews mRemoteViews;

    private volatile long dwID = -1;

    private static int downloadState;

    private static int[] mWidgetIDs;

    private Thread mThread;

    private static DownloadObserver mDownloadObserver;

    private SettingsObserver observer = null;

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UPDATE_WIDGET:
                    if (mWidgetIDs != null) {
                        for (int id : mWidgetIDs) {
                            updateWidget(mContext, id);
                        }
                    }
                    break;

                case CHECK_NEW_VERSION:
                    checkNewVersion();
                    break;

                case UPDATE_DOWNLOAD_PERCENT:
                    downloadState = msg.arg1;
                    mHandler.removeMessages(UPDATE_WIDGET);
                    mHandler.sendEmptyMessageDelayed(UPDATE_WIDGET, 5 * 1000);
                    break;

                case MSG_DOWNLOAD_CHANGE:
                    if (dwID > 0) {
                        if (mDownloadObserver == null) {
                            mHandler.removeMessages(UPDATE_WIDGET);
                            // donwload thread exsits,add listener
                            mDownloadObserver = new DownloadObserver(mHandler, mContext, dwID);
                            mContext.getContentResolver().registerContentObserver(
                                    Uri.parse("content://downloads/"), true, mDownloadObserver);
                            mDownloadObserver.onChange(true);
                            Log.v(TAG, "registerContentObserver");
                        }
                    }
                    break;
                default:
                    break;
            }
        }

    };

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        mContext = context;
        String action = intent.getAction();
        Log.v(TAG, "onReceive action = " + action);
        if (action.equals(CLICK_NAME_ACTION)) {
            String clsName = "com.android.settings.update.SystemNetUpdateActivity";
            String pkgName = "com.android.settings";
            boolean isInstalled = Utils.isApkInstalled(mContext, pkgName, clsName);
            if (isInstalled) {
                Utils.startApp(mContext, pkgName, clsName);
            } else {
                Log.e(TAG, "apk not installed");
            }
        } else if (action.equals(NETWORK_OK_ACTION)) {
            mHandler.sendEmptyMessage(CHECK_NEW_VERSION);
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        Log.v(TAG, " onUpdate ");
        mContext = context;
        mWidgetIDs = appWidgetIds;
        clearFormerWidget(UpgradeWidgetProvider.class.getName());

        getDownloadID(context);
        mHandler.sendEmptyMessage(UPDATE_WIDGET);
        mHandler.sendEmptyMessage(MSG_DOWNLOAD_CHANGE);

        if (observer == null) {
            observer = new SettingsObserver(mHandler, context);
            observer.observe(context);
        }
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        if (mDownloadObserver != null) {
            context.getContentResolver().unregisterContentObserver(mDownloadObserver);
        }
        if (observer != null) {
            context.getContentResolver().unregisterContentObserver(observer);
        }
        Log.v(TAG, "onDeleted");
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        Log.v(TAG, "onEnabled");
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        Log.v(TAG, "onDisabled");
    }

    /**
     * update widget view.
     * 
     * @param context
     * @param widgetID
     */
    private void updateWidget(Context context, int widgetID) {
        // Log.v(TAG, "updateWidget widgetID = " + widgetID);
        if (mRemoteViews == null) {
            mRemoteViews = new RemoteViews(context.getPackageName(), R.layout.upgrade_widget);
        }
        int redId = getBackgroud();
        String text = getVersionText(context);
        mRemoteViews.setTextViewText(R.id.version_text, text);
        mRemoteViews.setInt(R.id.upgrade_layout, "setBackgroundResource", redId);
        mAppWidgetManager.updateAppWidget(widgetID, mRemoteViews);
    }

    /**
     * check new version when network is connected,if has new verions,send.
     * message to update widget,else do nothing
     * 
     * @Note only when check new version dwID value can be
     *       {@value #NEW_VERISON_WITHOUT_DOWNLOAD}
     */
    private void checkNewVersion() {
        getDownloadID(mContext);
        // get version info from network only when no download
        // thread
        if (dwID == NO_DOWNLOAD_THREAD) {
            mThread = new Thread(new Runnable() {
                @Override
                public void run() {
                    synchronized (UpgradeWidgetProvider.class) {
                        boolean hasNewVerion = getVersionInfo();
                        if (hasNewVerion) {
                            dwID = NEW_VERISON_WITHOUT_DOWNLOAD;
                            mHandler.removeMessages(UPDATE_WIDGET);
                            mHandler.sendEmptyMessageDelayed(UPDATE_WIDGET, 5 * 1000);
                        }
                    }
                }
            });
            mThread.start();
        }
    }

    /**
     * get text show in version textview.
     * 
     * @param context
     * @return
     */
    private String getVersionText(Context context) {
        // Log.v(TAG, "getVersionText , dwID = " + dwID);
        String text = "";
        if (dwID == NO_DOWNLOAD_THREAD) {
            text = Build.VERSION.INCREMENTAL.substring(1);
        } else if (dwID == NEW_VERISON_WITHOUT_DOWNLOAD) {
            text = context.getResources().getString(R.string.found_new_verions);
        } else if (dwID > 0) {
            text = context.getResources().getString(R.string.download_percentage) + downloadState + "%";
        }
        return text;
    }

    /**
     * get widget backgroud resource id.
     * 
     * @return
     */
    private int getBackgroud() {
        int resID = R.drawable.setting_icon_update_bg;
        if (dwID == NEW_VERISON_WITHOUT_DOWNLOAD) {
            resID = R.drawable.setting_icon_update_bg2;
        }
        return resID;
    }

    /**
     * get version info from network,do not call in main thread.
     * 
     * @return true if there is a new version,else false
     */
    private boolean getVersionInfo() {
        Log.v(TAG, "getVersionInfo");
        // "0" for increment
        String jsonStr = JSONData.getUpgradeInfo(mContext, "0");
        if (jsonStr == null || jsonStr.isEmpty()) {
            Log.e(TAG, "Fail to get incremental package, Try all package...");
            // "1" for all package
            jsonStr = JSONData.getUpgradeInfo(mContext, "1");
        }
        if (jsonStr == null || jsonStr.isEmpty()) {
            Log.e(TAG, "Fail to get new version!");
            return false;
        } else {
            try {
                JSONObject json = new JSONObject(jsonStr);
                // Log.v(TAG, "json = " + json.toString());
                int errCode = json.getInt("err");
                JSONObject body = json.optJSONObject("bd");
                if (errCode == 0 && body != null) {
                    String version = body.optString("ver", "0");
                    Log.v(TAG, "version = " + version);
                    // the version format like V2.3.0.0
                    if (version.length() > 3) {
                        return true;
                    }
                }
            } catch (JSONException e) {
                Log.e(TAG, e.toString());
                return false;
            }
            return false;
        }
    }

    /**
     * get DonwloadId from SciflyStore.
     * 
     * @param context
     * @return
     */
    private void getDownloadID(Context context) {
        if (dwID < 0) {
            String downloadId = SciflyStore.Global.getString(context.getContentResolver(), Global.DOWNLOAD_ID);
            Log.v(TAG, "getDownloadID id = " + downloadId);
            // the default Global.DOWNLOAD_ID value is -1
            if (downloadId != null && !downloadId.isEmpty()) {
                try {
                    dwID = Long.parseLong(downloadId);
                } catch (Exception e) {
                    Log.e(TAG, "getDownloadID id is not long");
                    dwID = -1;
                }
            }
        }
    }

}
