
package com.heran.launcher2.util;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.heran.launcher2.apps.AppInfoBean;
import com.heran.launcher2.others.DownloadInfo;

import android.app.DownloadManager.Query;
import android.app.DownloadManager.Request;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.provider.Downloads;
import android.util.Log;
import scifly.dm.EosDownloadListener;
import scifly.dm.EosDownloadManager;
import scifly.dm.EosDownloadTask;

/*
 * projectName： SciflyLauncher2
 * moduleName： DownloadManager.java
 *
 * @author chadm.xiang
 * @version 1.0.0
 * @time  2014-6-20 上午10:57:42
 * @Copyright © 2013 Eos Inc.
 */

/**
 * to handler the download apk logic
 **/
public final class DownloadManager {

    private final String TAG = DownloadManager.class.getSimpleName();

    private static DownloadManager dManager;

    private Handler mHandler;

    private EosDownloadManager eosDownloadManager;

    private Context mContext;

    private final int FINISH_DOWNLOAD_STATUS = 100;

    public static DownloadManager getDownloadManagerInstance(Context context, Handler handler) {
        if (dManager == null) {
            dManager = new DownloadManager(context, handler);
        }
        return dManager;
    }

    private DownloadManager(Context context, Handler handler) {
        this.mContext = context;
        this.mHandler = handler;

        eosDownloadManager = new EosDownloadManager(mContext);
    }

    /**
     * start download from the given url
     * 
     * @param path download path
     */
    public void startDownload(final String path, final AppInfoBean mAppInfoBean) {
        Uri uri = Uri.parse(path);
        String fileName = path.substring(path.lastIndexOf("=") + 1);
        Log.d(TAG, "fileName: " + fileName);
        Request request = new Request(uri);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
        EosDownloadListener listener = new EosDownloadListener() {
            @Override
            public void onDownloadStatusChanged(int status) {
                Log.v(TAG, "The status is " + Downloads.Impl.statusToString(status));
            }

            @Override
            public void onDownloadSize(long size) {
                Log.d(TAG, "size: " + size);
            }

            // the percent is in terms of percentage
            @Override
            public void onDownloadComplete(int percent) {
                Log.d(TAG, "percent: " + percent);
                mAppInfoBean.setDowlodPercent(percent);
                mHandler.sendEmptyMessage(1);
                if (percent == FINISH_DOWNLOAD_STATUS) {
                    // install apk when finish download
                    String fileName = path.substring(path.lastIndexOf("=") + 1);
                    String filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                            .getAbsolutePath() + File.separator + fileName;
                    Log.v(TAG, "filePath = " + filePath);
                    Utils.install(mContext, filePath);
                }
            }
        };
        EosDownloadTask task = new EosDownloadTask(request, listener);
        eosDownloadManager.addTask(task);
    }

    /**
     * query the download info in the download list.
     * 
     * @return list<DownloadInfo>
     */
    public List<DownloadInfo> queryDownloadInfo() {
        List<DownloadInfo> list = new ArrayList<DownloadInfo>();
        Query query = new Query();
        android.app.DownloadManager dm = (android.app.DownloadManager) mContext
                .getSystemService(mContext.DOWNLOAD_SERVICE);
        Cursor cursor = dm.query(query);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                DownloadInfo info = new DownloadInfo(null, 0, 0);
                info.setUri(cursor.getString(cursor.getColumnIndex(dm.COLUMN_URI)));
                info.setPresentBytes(cursor.getLong(cursor.getColumnIndex(dm.COLUMN_BYTES_DOWNLOADED_SO_FAR)));
                info.setTotalBytes(cursor.getLong(cursor.getColumnIndex(dm.COLUMN_TOTAL_SIZE_BYTES)));
                info.setDownloadID(cursor.getLong(cursor.getColumnIndex(dm.COLUMN_ID)));
                info.setDownloadState(cursor.getInt(cursor.getColumnIndex(dm.COLUMN_STATUS)));
                list.add(info);
                Log.i(TAG, "info.uri = " + info.getUri());
            }
            cursor.close();
        }
        return list;
    }

    /**
     * get DownloadInfo from the list.
     * 
     * @param url
     * @return DownloadInfo
     */
    public DownloadInfo getDownloadInfo(String url) {
        List<DownloadInfo> list = queryDownloadInfo();
        for (DownloadInfo downloadInfo : list) {
            String uri = downloadInfo.getUri();
            if (uri.equals(url)) {
                return downloadInfo;
            }
        }
        return null;
    }

    public void restartDownload(DownloadInfo info, final AppInfoBean mAppInfoBean) {
        final String path = info.getUri();
        long id = info.getDownloadID();
        int status = info.getDownloadState();

        Log.v(TAG, "The status of " + id + " is " + Downloads.Impl.statusToString(status));

        EosDownloadListener listener = new EosDownloadListener() {
            @Override
            public void onDownloadStatusChanged(int status) {
                Log.v(TAG, "onDownloadStatusChanged " + status);
                if (status == Downloads.Impl.STATUS_CANNOT_RESUME) {

                }
            }

            @Override
            public void onDownloadSize(long size) {
                // Log.d(TAG, "size: " + size);
            }

            // the percent is in terms of percentage
            @Override
            public void onDownloadComplete(int percent) {
                Log.d(TAG, "percent: " + percent);
                mAppInfoBean.setDowlodPercent(percent);
                mHandler.sendEmptyMessage(1);
                if (percent == FINISH_DOWNLOAD_STATUS) {
                    // install apk when finish download
                    String fileName = path.substring(path.lastIndexOf("=") + 1);
                    String filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                            .getAbsolutePath() + File.separator + fileName;
                    Log.v(TAG, "filePath = " + filePath);
                    Utils.install(mContext, filePath);
                }
            }
        };
        eosDownloadManager.restartTask(id, listener);
    }

}
