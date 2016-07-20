
package com.eostek.wasuwidgethost.business;

import java.lang.reflect.Method;

import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.app.DownloadManager;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.os.SystemClock;
import android.util.Log;

/**
 * projectName： WasuWidgetHost.
 * moduleName： DownloadObserver.java
 *
 * @author chadm.xiang
 * @version 1.0.0
 * @time  2014-9-16 2:14:04 pm
 * @Copyright © 2014 Eos Inc.
 */

public class DownloadObserver extends ContentObserver {
    private long mDownid;

    private Handler mHandler;

    private Context mContext;

    private volatile boolean shouldUpdate = true;

    /**
     * construct method.
     * @param handler
     * @param context
     * @param downid
     */
    public DownloadObserver(Handler handler, Context context, long downid) {
        super(handler);
        this.mHandler = handler;
        this.mDownid = downid;
        this.mContext = context;
    }

    @TargetApi(Build.VERSION_CODES.GINGERBREAD)
    @SuppressLint("InlinedApi")
    @Override
    public void onChange(boolean selfChange) {
        if (shouldUpdate) {
            shouldUpdate = false;
            // when /data/data/com.android.providers.download/database/database.db is changing，then onChange，start query.
            Log.w("DownloadObserver", String.valueOf(mDownid));
            super.onChange(selfChange);
            DownloadManager.Query query = new DownloadManager.Query().setFilterById(mDownid);
            DownloadManager downloadManager = (DownloadManager) mContext.getSystemService(Context.DOWNLOAD_SERVICE);
            // downloadManager.setAccessAllDownloads(true);
            setAccessAllDownloads(mContext, true);
            Cursor cursor = downloadManager.query(query);
            if (cursor != null && cursor.moveToFirst()) {
                long mDownloadSoFar = cursor.getLong(cursor
                        .getColumnIndexOrThrow(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                long mDownloadAll = cursor.getLong(cursor
                        .getColumnIndexOrThrow(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));

                int mProgress = (int) ((mDownloadSoFar * 100) / mDownloadAll);

                Message msg = Message.obtain();
                msg.what = 0x102;
                msg.arg1 = mProgress;
                mHandler.sendMessage(msg);

                cursor.close();
                Log.w(getClass().getSimpleName(), String.valueOf(mProgress) + "; total = " + mDownloadAll
                        + "; so_fare = " + mDownloadSoFar);
            }
            SystemClock.sleep(5 * 1000);
            shouldUpdate = true;
        }
    }

    /**
     * set access download.
     * @param context
     * @param value
     */
    @SuppressLint("InlinedApi")
    public static void setAccessAllDownloads(Context context, boolean value) {
        try {
            Object service = context.getSystemService(Context.DOWNLOAD_SERVICE);
            Class<?> claz = Class.forName("android.app.DownloadManager");
            // Parameters Types
            @SuppressWarnings("rawtypes")
            Class[] paramTypes = new Class[1];
            paramTypes[0] = boolean.class;
            Method setVisibleMethod = claz.getMethod("setAccessAllDownloads", paramTypes);
            // Parameters
            Object[] params = new Object[1];
            params[0] = Boolean.valueOf(value);
            setVisibleMethod.setAccessible(true);
            setVisibleMethod.invoke(service, params);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
