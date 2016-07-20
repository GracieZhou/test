package com.mstar.tv.menu.setting.update;

import java.io.File;
import java.io.InputStream;
import java.io.RandomAccessFile;
import java.net.HttpURLConnection;
import java.net.URL;

import android.content.Context;
import android.content.SharedPreferences;
import android.os.Handler;
import android.util.Log;

public class UpgradeTask implements Runnable {

    private final static String TAG = "MSettings.UpgradeTask";

    private final static String DOWNLOAD_ADDRESS = "url";

    private final static String VERSION = "version";

    private final static String NAME = "share_pres";

    private final static int DOWNLOAD_ERROR = 3;

    private long mDownloadedSize = 0;

    private long mTotalSize;

    private int mDownloadPercent;

    private IDownloadProgressListener mDownloadProgressListener;

    private String mLocalPath;

    private String mUpgradeURL;

    private String mVersion;

    private Context mContext;

    private Handler mHandler;

    public UpgradeTask(Context context, String upgradeURL, String localPath, String version,
            IDownloadProgressListener dpListener, Handler handler) {
        this.mDownloadProgressListener = dpListener;
        this.mLocalPath = localPath;
        this.mUpgradeURL = upgradeURL;
        this.mVersion = version;
        this.mContext = context;
        this.mHandler = handler;
    }

    private void prepare() {
        File file = new File(mLocalPath);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }

        String versionString = getStringData(VERSION);
        if (versionString.equals(mVersion)) {
            mUpgradeURL = getStringData(DOWNLOAD_ADDRESS);
        } else {
            Log.d(TAG, "delete file");
            file.delete();
        }
    }

    /*
     * @see
     * com.jrm.core.container.cmps.upgrade.task.BaseUpgradeTask#onDownload()
     */
    protected boolean download() {
        File file = new File(mLocalPath);
        if (file.exists()) {
            mDownloadedSize = file.length();
        } else {
            mDownloadedSize = 0;
        }
        Log.d(TAG, "mUpgradeURL, " + mUpgradeURL + " downloadedSize, " + mDownloadedSize);

        HttpURLConnection httpConnection = null;
        URL url = null;
        try {
            url = new URL(mUpgradeURL);
            httpConnection = (HttpURLConnection) url.openConnection();
            mTotalSize = httpConnection.getContentLength();
            Log.d(TAG, "totalSize, " + mTotalSize);
            if (mDownloadedSize == mTotalSize && this.mDownloadProgressListener != null) {
                mDownloadProgressListener.onDownloadSizeChange(100);
                return true;
            } else if (mDownloadedSize > mTotalSize) {
                if (!file.delete()) {
                    return false;
                }
            }
            httpConnection.disconnect();
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (httpConnection != null) {
                    httpConnection.disconnect();
                }
            } catch (Exception e) {
            }
        }

        InputStream inStream = null;
        RandomAccessFile randomAccessFile = null;
        try {
            httpConnection = (HttpURLConnection) url.openConnection();
            httpConnection.setRequestProperty("Accept", "image/gif, " + "image/jpeg, "
                    + "image/pjpeg, " + "image/pjpeg, " + "application/x-shockwave-flash, "
                    + "application/xaml+xml, " + "application/vnd.ms-xpsdocument, "
                    + "application/x-ms-xbap, " + "application/x-ms-application, "
                    + "application/vnd.ms-excel, " + "application/vnd.ms-powerpoint, "
                    + "application/msword, " + "*/*");
            httpConnection.setRequestProperty("Accept-Language", "zh-CN");
            httpConnection.setRequestProperty("Referer", mUpgradeURL);
            httpConnection.setRequestProperty("Charset", "UTF-8");
            httpConnection.setRequestProperty("Range", "bytes=" + mDownloadedSize + "-");
            httpConnection.setRequestProperty("Connection", "Keep-Alive");

            inStream = httpConnection.getInputStream();

            File saveFile = new File(mLocalPath);
            randomAccessFile = new RandomAccessFile(saveFile, "rwd");
            randomAccessFile.seek(mDownloadedSize);

            int offset = 0;
            int count = 0;
            int perUnit = (int) mTotalSize / 5120 / 100;
            byte[] buffer = new byte[5120];
            while ((offset = inStream.read(buffer, 0, 5120)) != -1) {
                randomAccessFile.write(buffer, 0, offset);
                count++;
                if (count == perUnit && mDownloadedSize < mTotalSize) {
                    mDownloadPercent = (int) (mDownloadedSize * 100 / mTotalSize);
                    if (this.mDownloadProgressListener != null) {
                        mDownloadProgressListener.onDownloadSizeChange(mDownloadPercent);
                    }
                    count = 0;
                }
                mDownloadedSize += offset;
            }

            if (mDownloadedSize == mTotalSize && this.mDownloadProgressListener != null) {
                mDownloadProgressListener.onDownloadSizeChange(100);
            }
            Log.d(TAG, "download finished.");

            return true;
        } catch (Exception e) {
            e.printStackTrace();
            return false;
        } finally {
            try {
                if (inStream != null) {
                    inStream.close();
                }
                if (httpConnection != null) {
                    httpConnection.disconnect();
                }
                if (randomAccessFile != null) {
                    randomAccessFile.close();
                }
            } catch (Exception e) {
            }
        }
    }

    @Override
    public void run() {
        prepare();

        if (!download()) {
            Log.d(TAG, "download failed");
            mHandler.sendEmptyMessage(DOWNLOAD_ERROR);
        }
    };

    private String getStringData(String key) {
        SharedPreferences preference = mContext.getSharedPreferences(NAME, Context.MODE_PRIVATE);
        return preference.getString(key, "");
    }

}
