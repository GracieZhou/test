
package com.eostek.tv.launcher.business;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

import com.eostek.tv.launcher.model.DownloadInfo;
import com.eostek.tv.launcher.util.UIUtil;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.app.DownloadManager.Query;
import android.app.DownloadManager.Request;
import android.os.Environment;
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

    private EosDownloadManager eosDownloadManager;

    private Context mContext;

    private final int FINISH_DOWNLOAD_STATUS = 100;

    public static DownloadManager getDownloadManagerInstance(Context context) {
        if (dManager == null) {
            dManager = new DownloadManager(context);
        }
        return dManager;
    }

    private DownloadManager(Context context) {
        this.mContext = context;
        eosDownloadManager = new EosDownloadManager(mContext);
    }

    /**
     * start download from the given url
     * 
     * @param path download path
     */
    public void startDownload(final String path) {
        Uri uri = Uri.parse(path);
        String fileName = path.substring(path.lastIndexOf("/") + 1);
        Log.d(TAG, "fileName: " + fileName);
        Request request = new Request(uri);
        request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
        EosDownloadListener listener = new EosDownloadListener() {
            public void onDownloadStatusChanged(int status) {
                // Log.d(TAG, "status: " + status);
            }

            public void onDownloadSize(long size) {
                // Log.d(TAG, "size: " + size);
            }

            // the percent is in terms of percentage
            public void onDownloadComplete(int percent) {
                Log.d(TAG, "percent: " + percent);
                if (percent == FINISH_DOWNLOAD_STATUS) {
                    // install apk when finish download
                    String fileName = path.substring(path.lastIndexOf("/") + 1);
                    String filePath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS)
                            .getAbsolutePath() + File.separator + fileName;
                    Log.v(TAG, "filePath = " + filePath);
                    UIUtil.install(mContext, filePath);
                }
            }
        };
        EosDownloadTask task = new EosDownloadTask(request, listener);
        eosDownloadManager.addTask(task);
    }

    /**
     * query the download info in the download list.
     * @return list<DownloadInfo>
     */
    public List<DownloadInfo> queryDownloadInfo() {
        List<DownloadInfo> list = new ArrayList<DownloadInfo>();
        Query query = new Query();
        android.app.DownloadManager dm = (android.app.DownloadManager) mContext.getSystemService(mContext.DOWNLOAD_SERVICE);
        Cursor cursor = dm.query(query);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                DownloadInfo info = new DownloadInfo(null, 0, 0);
                info.setUri(cursor.getString(cursor.getColumnIndex(dm.COLUMN_URI)));
                info.setPresentBytes(cursor.getLong(cursor.getColumnIndex(dm.COLUMN_BYTES_DOWNLOADED_SO_FAR)));
                info.setTotalBytes(cursor.getLong(cursor.getColumnIndex(dm.COLUMN_TOTAL_SIZE_BYTES)));
                list.add(info);
            }
            cursor.close();
        }
        return list;
    }
    
    /**
     * get DownloadInfo from the list.
     * @param url
     * @return DownloadInfo
     */
    public DownloadInfo getDownloadInfo(String url){
        List<DownloadInfo> list = queryDownloadInfo();
        for (DownloadInfo downloadInfo : list) {
            String uri = downloadInfo.getUri();
            if (uri.equals(url)) {
                return downloadInfo;
            }
        }
        return null;
    }
    
}
