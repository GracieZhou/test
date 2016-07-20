
package com.android.server.scifly;

import java.io.DataOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Queue;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.storage.IMountService;
import android.text.TextUtils;
import android.util.Log;
import scifly.intent.IntentExtra;
import scifly.provider.metadata.Blacklist;
import scifly.provider.SciflyStore;
import scifly.security.ISecurityManager;
import scifly.security.IOnInstallEnableListener;
import scifly.util.JsonParser;
import scifly.app.blacklist.AppScanner;

/**
 * Service of Security Manager.<br>
 * <li>load black list while receive IntentExtra.ACTION_BLACK_LIST_CHANGED action.
 * <li>check apk to be installing whether it is in blacklist or not.
 */
public class SecurityManagerService extends ISecurityManager.Stub {

    private static final String TAG = "SecurityManager";

    private static String mBlacklistDefaultPath = "/data/data/com.jrm.tm.cpe/files/blacklist.json";

    private Object mObject = new Object();

    private Context mContext;

    private IMountService mMountService = null;

    private BlacklistLoadThread mBlacklistLoadThread;

    private BlacklistCheckThread mBlacklistCheckThread;

    private BroadcastReceiver mBlacklistChanged = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            final String path = intent.getStringExtra("blackListPath");
            Log.d(TAG, "load path : " + path);
            if (TextUtils.isEmpty(path)) {
                return;
            }

            // new thread to parse black list
            //mBlacklistLoadThread = new BlacklistLoadThread(path);
            //mBlacklistLoadThread.run();
        }
    };

    private BroadcastReceiver mBootReceiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {

                Log.d(TAG, intent.toString());
                AppScanner scanner = new AppScanner(mContext);
                scanner.start();
        }
    };

    SecurityManagerService(Context context) {
        mContext = context;
        IntentFilter filter = new IntentFilter();
        filter.addAction(IntentExtra.ACTION_BLACK_LIST_CHANGED);
        context.registerReceiver(mBlacklistChanged, filter);

        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_BOOT_COMPLETED);
		context.registerReceiver(mBootReceiver, intentFilter);
    }

    /**
     * Query the black list to check the pkg is included.
     * 
     * @param pkg package name.
     * @param listener callback for notify install is enabled or not.
     * @since API 2.0
     */
    public void checkPkgFromBlacklist(String pkg, IOnInstallEnableListener listener) {
        Log.d(TAG, "checkPkgFromBlacklist#pkg : " + pkg + " listener : " + listener);
        mBlacklistCheckThread = new BlacklistCheckThread(pkg, listener);
        mBlacklistCheckThread.run();
    }

    // callback to package installer
    private void callBack(IOnInstallEnableListener listener, boolean enabled, String message, int level) {
        if (listener != null) {
            try {
                listener.onEnable(enabled, message, level);
            } catch (RemoteException e) {
                Log.e(TAG, "RemoteException", e);
            }
        }
    }

    /**
     * Thread for loading black list from the JSON file.
     */
    private class BlacklistLoadThread extends Thread {

        private String mPath;

        public BlacklistLoadThread(String path) {
            this.mPath = path;
        }

        @Override
        public void run() {
            synchronized (mObject) {
                JSONObject jsonObject = JsonParser.parse(mPath);
                if (jsonObject != null) {
                    JSONArray jsonArray = null;
                    try {
                        jsonArray = jsonObject.getJSONArray("blackList");
                    } catch (JSONException e) {
                        Log.e(TAG, "getJSONArray exception", e);
                    }
                    if (jsonArray == null) {
                        return;
                    }

                    // get all installed apk
                    List<PackageInfo> pkgs = mContext.getPackageManager().getInstalledPackages(
                            PackageManager.GET_ACTIVITIES);
                    int length = jsonArray.length();
                    JSONObject object = null;
                    for (int i = 0; i < length; i++) {
                        Blacklist blacklist = new Blacklist();
                        try {
                            object = (JSONObject) jsonArray.get(i);
                            if (object != null) {
                                blacklist.mPackage = object.getString("pkg");
                                if (TextUtils.isEmpty(blacklist.mPackage)) {
                                    continue;
                                }
                                for (PackageInfo pkg : pkgs) {
                                    if (blacklist.mPackage.equals(pkg.applicationInfo.packageName)) {
                                        blacklist.mMessage = object.getString("info");
                                        blacklist.mLevel = object.getInt("level");
                                        // update black list database
                                        SciflyStore.Security.putBlacklist(mContext.getContentResolver(), blacklist);
                                        break;
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "getJSONxxx", e);
                        }
                    }
                }
            }
        }
    }

    /**
     * Thread for check apk to be installing whether it is in blacklist or not.
     */
    private class BlacklistCheckThread extends Thread {

        private String mPkg;

        private IOnInstallEnableListener mListener;

        BlacklistCheckThread(String pkg, IOnInstallEnableListener listener) {
            this.mPkg = pkg;
            this.mListener = listener;
        }

        @Override
        public void run() {
            synchronized (mObject) {
                Blacklist bk = SciflyStore.Security.getBlacklistForPkg(mContext.getContentResolver(), mPkg);
                if (bk != null && bk.mLevel != -1) {
                    callBack(mListener, false, bk.mMessage, bk.mLevel);
                }

                JSONObject jsonObject = JsonParser.parse(mBlacklistDefaultPath);
                if (jsonObject != null) {
                    JSONArray jsonArray = null;
                    try {
                        jsonArray = jsonObject.getJSONArray("blackList");
                    } catch (JSONException e) {
                        Log.e(TAG, "getJSONArray exception", e);
                    }
                    if (jsonArray == null) {
                        return;
                    }

                    int length = jsonArray.length();
                    JSONObject object = null;
                    for (int i = 0; i < length; i++) {
                        Blacklist blacklist = new Blacklist();
                        try {
                            object = (JSONObject) jsonArray.get(i);
                            if (object != null) {
                                blacklist.mPackage = object.getString("pkg");
                                if (TextUtils.isEmpty(blacklist.mPackage)) {
                                    continue;
                                }
                                if (blacklist.mPackage.equals(mPkg)) {
                                    blacklist.mMessage = object.getString("info");
                                    blacklist.mLevel = object.getInt("level");
                                    // update the database
                                    SciflyStore.Security.putBlacklist(mContext.getContentResolver(), blacklist);
                                    callBack(mListener, false, bk.mMessage, bk.mLevel);
                                    break;
                                }
                            }
                        } catch (JSONException e) {
                            Log.e(TAG, "getJSONArray exception", e);
                        } catch (Exception ex) {
                            Log.e(TAG, "some exception happen", ex);
                        }
                    }
                }

                callBack(mListener, true, null, 0);
            }
        }
    }

}
