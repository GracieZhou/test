
package com.android.settings.update.ota;

import java.io.File;

import com.android.settings.R;

import android.app.DownloadManager;
import android.app.DownloadManager.Query;
import android.app.DownloadManager.Request;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.text.TextUtils;

public class HttpDownloader extends Downloader {
    private static Logger sLog = new Logger(HttpDownloader.class);

    private Handler mUpdateHandler;

    private DownloadManager sDownloadManager;

    private static HttpDownloader INSTANCE;

    public static HttpDownloader getInstance(Context context) {
        if (null == INSTANCE) {
            sContext = context;
            INSTANCE = new HttpDownloader(context);
        }
        return INSTANCE;
    }

    private HttpDownloader(Context context) {
        if (sDownloadManager == null) {
            sDownloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
        }

        mUpdateHandler = new Handler(context.getMainLooper());
        sPreferenceHelper = PreferenceHelper.getInstance(context);
    }

    private Runnable mUpdateProgress = new Runnable() {

        public void run() {
            if (!sDownloadingRom) {
                return;
            }

            long idRom = sPreferenceHelper.getDownloadId();

            long[] statusRom = sDownloadingRom ? getDownloadProgress(idRom) : new long[] {
                    DownloadManager.STATUS_SUCCESSFUL, 0, 0, -1
            };

            int status = DownloadManager.STATUS_SUCCESSFUL;
            if (statusRom[0] == DownloadManager.STATUS_FAILED) {
                status = DownloadManager.STATUS_FAILED;
            } else if (statusRom[0] == DownloadManager.STATUS_PENDING) {
                status = DownloadManager.STATUS_PENDING;
            } else if (statusRom[0] == DownloadManager.STATUS_PAUSED) {
                status = DownloadManager.STATUS_PAUSED;
            }

            switch (status) {
                case DownloadManager.STATUS_PENDING:
                    onDownloadProgress(-1);
                    break;
                case DownloadManager.STATUS_PAUSED:
                    onDownloadPaused("Network exception");
                    break;
                case DownloadManager.STATUS_FAILED:
                    int error = (int) statusRom[3];
                    onDownloadError(error == -1 ? null : sContext.getResources().getString(error));
                    break;
                default:
                    long totalBytes = statusRom[1];
                    long downloadedBytes = statusRom[2];
                    long percent = totalBytes == -1 && downloadedBytes == -1 ? -1 : downloadedBytes * 100 / totalBytes;
                    if (totalBytes != -1 && downloadedBytes != -1 && percent != -1) {
                        onDownloadProgress((int) percent);
                    }
                    break;
            }

            if (status != DownloadManager.STATUS_FAILED) {
                mUpdateHandler.postDelayed(this, 1000);
            }
        }
    };

    @Override
    public void registerCallback(DownloadCallback callback) {
        super.registerCallback(callback);
        mUpdateHandler.post(mUpdateProgress);
    }

    @Override
    public void unregisterCallback() {
        super.unregisterCallback();
        mUpdateHandler.removeCallbacks(mUpdateProgress);
    }

    @Override
    public boolean restartDownloading() {
        long romId = sPreferenceHelper.getDownloadId();
        if (romId >= 0L && !sDownloadingRom) {
            sLog.debug("restartDownloading");
            int status = -1;
            Cursor c = sDownloadManager.query(new Query().setFilterById(romId));
            if (c == null) {
                return false;
            }
            if (c.moveToFirst()) {
                status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
            }
            c.close();

            switch (status) {
                case DownloadManager.STATUS_PAUSED:
                    sLog.debug("restart task [" + romId + "]");
                    sDownloadManager.restartDownload(romId);
                    break;
                case DownloadManager.STATUS_PENDING:
                case DownloadManager.STATUS_RUNNING:
                case DownloadManager.STATUS_SUCCESSFUL:
                case DownloadManager.STATUS_FAILED:
                    break;
                default:
                    return false;
            }

            sDownloadingRom = true;
            mUpdateHandler.post(mUpdateProgress);
            return true;
        }

        return false;
    }

    @Override
    public boolean checkIfDownloading(String md5) {
        sLog.debug("checkIfDownloading:: md5:" + md5);
        String romMd5 = sPreferenceHelper.getPackageMd5();
        long romId = sPreferenceHelper.getDownloadId();
        if (!TextUtils.isEmpty(romMd5) && romMd5.equals(md5)) {

            Cursor cursor = sDownloadManager.query(new DownloadManager.Query().setFilterById(romId));
            if (cursor != null) {
                try {
                    for (cursor.moveToFirst(); !cursor.isAfterLast(); cursor.moveToNext()) {
                        int status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
                        if (status != DownloadManager.STATUS_SUCCESSFUL && status != DownloadManager.STATUS_FAILED) {
                            return true;
                        }
                    }
                } finally {
                    cursor.close();
                }
            }
        }

        return false;
    }

    @Override
    public boolean checkIfDownloadCompleted(String md5) {
        sLog.debug("checkIfDownloadCompleted:: md5:" + md5);

        boolean ret = false;
        String romMd5 = sPreferenceHelper.getPackageMd5();
        if (!TextUtils.isEmpty(romMd5) && romMd5.equals(md5)) {

            if (sPreferenceHelper.isDownloadFinished()) {
                String filePath = sPreferenceHelper.getDownloadPath();
                ret = new File(filePath).exists();
            }

        }

        if (!ret)
            sPreferenceHelper.setDownloadFinished(false);

        return ret;
    }

    @Override
    protected void realDownloadFile(String url) {
        sLog.debug("realDownloadFile:: url:" + url);

        DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));
        request.setMimeType("application/zip");
        request.setVisibleInDownloadsUi(false);
        request.setNotificationVisibility(Request.VISIBILITY_HIDDEN);

        long requiredSize = sPreferenceHelper.getDownloadSize();
        String downloadDir = getDownloadDir(requiredSize);
        if (TextUtils.isEmpty(downloadDir)) {
            onDownloadError("No enough space for downloading.");
            return;
        }
        File file = new File(downloadDir);
        if (!file.exists()) {
            file.mkdirs();
        }

        String fileName = url.substring(url.lastIndexOf('/') + 1);
        File saveFile = new File(file, fileName);
        if (null != saveFile && saveFile.getAbsolutePath().startsWith(DIR_CACHE)) {
            request.setDestinationToSystemCache();
        } else {
            request.setDestinationUri(Uri.fromFile(saveFile));
        }

        // avoid the unstable DownloadProvider cause uncaught exception
        try {
            long id = sDownloadManager.enqueue(request);

            sDownloadingRom = true;
            sPreferenceHelper.setDownloadId(id);
            mUpdateHandler.post(mUpdateProgress);
        } catch (Exception e) {
            onDownloadError("DownloadProvider Exception");
        }

    }

    @Override
    public void clearDownload() {
        sLog.debug("clearDownload ");
        long id = sPreferenceHelper.getDownloadId();
        if (id > 0L) {
            removeDownload(id);
        }
        sPreferenceHelper.setDownloadFinished(false);
    }

    private void removeDownload(long id) {
        sLog.debug("removeDownload:: id:" + id);

        if (id < 0) {
            sLog.error("UnsupportedOperationException");
            return;
        }

        sDownloadManager.remove(id);

        String downloadPath = sPreferenceHelper.getDownloadPath();
        if (!TextUtils.isEmpty(downloadPath)) {
            downloadPath = getDownloadPath(id);

            if (!TextUtils.isEmpty(downloadPath)) {
                new File(downloadPath).delete();
            }
        }

        sDownloadingRom = false;
        sPreferenceHelper.setDownloadId(-1);

        mUpdateHandler.removeCallbacks(mUpdateProgress);
    }

    public void checkDownloadFinished(long downloadId) {
        sLog.debug("checkDownloadFinished: downloadId:" + downloadId);
        long id = sPreferenceHelper.getDownloadId();
        if (downloadId != id) {
            return;
        }
        String md5 = sPreferenceHelper.getPackageMd5();
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(id);
        Cursor cursor = sDownloadManager.query(query);
        if (cursor != null && cursor.moveToFirst()) {
            int columnIndex = cursor.getColumnIndex(DownloadManager.COLUMN_STATUS);
            int status = cursor.getInt(columnIndex);
            switch (status) {
                case DownloadManager.STATUS_FAILED:
                    removeDownload(id);
                    int reasonText = getDownloadError(cursor);
                    onDownloadError(sContext.getResources().getString(reasonText));

                    break;
                case DownloadManager.STATUS_SUCCESSFUL:
                    String path = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME));
                    if (!TextUtils.isEmpty(path)) {
                        path = path.replace("file://", "");
                        onDownloadFinished(path, md5);
                    }

                    break;
                default:
                    break;
            }
        } else {
            removeDownload(id);
        }
        if (cursor != null) {
            cursor.close();
        }
    }

    private long[] getDownloadProgress(long id) {
        DownloadManager.Query q = new DownloadManager.Query();
        q.setFilterById(id);

        Cursor cursor = sDownloadManager.query(q);
        int status;

        if (cursor == null || !cursor.moveToFirst()) {
            status = DownloadManager.STATUS_FAILED;
        } else {
            status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
        }
        long error = -1;
        long totalBytes = -1;
        long downloadedBytes = -1;

        switch (status) {
            case DownloadManager.STATUS_RUNNING:
                downloadedBytes = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                totalBytes = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                break;
            case DownloadManager.STATUS_PAUSED:
                break;
            case DownloadManager.STATUS_FAILED:
                sDownloadingRom = false;
                error = getDownloadError(cursor);
                break;
        }

        if (cursor != null) {
            cursor.close();
        }

        return new long[] {
                status, totalBytes, downloadedBytes, error
        };
    }

    private int getDownloadError(Cursor cursor) {

        if (cursor == null) {
            return R.string.error_unknown;
        }

        int reason = -1;
        int columnReason = cursor.getColumnIndex(DownloadManager.COLUMN_REASON);
        if (cursor.moveToFirst()) {
            reason = cursor.getInt(columnReason);
        }

        int reasonText = -1;
        switch (reason) {
            case DownloadManager.ERROR_CANNOT_RESUME:
                reasonText = R.string.error_cannot_resume;
                break;
            case DownloadManager.ERROR_DEVICE_NOT_FOUND:
                reasonText = R.string.error_device_not_found;
                break;
            case DownloadManager.ERROR_FILE_ALREADY_EXISTS:
                reasonText = R.string.error_file_already_exists;
                break;
            case DownloadManager.ERROR_FILE_ERROR:
                reasonText = R.string.error_file_error;
                break;
            case DownloadManager.ERROR_HTTP_DATA_ERROR:
                reasonText = R.string.error_http_data_error;
                break;
            case DownloadManager.ERROR_INSUFFICIENT_SPACE:
                reasonText = R.string.error_insufficient_space;
                break;
            case DownloadManager.ERROR_TOO_MANY_REDIRECTS:
                reasonText = R.string.error_too_many_redirects;
                break;
            case DownloadManager.ERROR_UNHANDLED_HTTP_CODE:
                reasonText = R.string.error_unhandled_http_code;
                break;
            case DownloadManager.ERROR_UNKNOWN:
            default:
                reasonText = R.string.error_unknown;
                break;
        }
        return reasonText;
    }

    @Override
    public String getDownloadDir(long requiredSize) {
        long availableSize = Utils.getAvailableSize(DIR_CACHE) / 1024;
        if (requiredSize > availableSize) {
            Utils.reportCacheFullAndClean(sContext);
            availableSize = Utils.getAvailableSize(DIR_CACHE) / 1024;
        }

        if (requiredSize < availableSize) {
            return DIR_CACHE;
        } else {
            String downloadDir = Environment.getExternalStorageDirectory().getAbsolutePath();
            availableSize = Utils.getAvailableSize(downloadDir) / 1024;
            if (requiredSize < availableSize) {
                return Environment.getExternalStorageDirectory().getAbsolutePath();
            } else {
                return null;
            }
        }

    }

    private String getDownloadPath(long id) {
        DownloadManager.Query query = new DownloadManager.Query();
        query.setFilterById(id);
        Cursor cursor = sDownloadManager.query(query);
        if (cursor != null && cursor.moveToFirst()) {
            String path = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME));
            if (!TextUtils.isEmpty(path)) {
                path = path.replace("file://", "");
                sLog.debug("file : " + path);
                return path;
            }
        }
        return null;
    }

}
