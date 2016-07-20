
package com.android.settings.update.ota;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;

import android.content.Context;
import android.content.Intent;
import android.os.AsyncTask;
import android.text.TextUtils;

public abstract class Downloader implements Constants {
    private static Logger sLog = new Logger(Downloader.class);

    protected static Context sContext;

    protected static PreferenceHelper sPreferenceHelper;

    protected DownloadCallback sCallback;

    protected boolean sDownloadingRom = false;

    private int mLastProgress = -1;

    public static interface DownloadCallback {

        public abstract void onDownloadStarted();

        public abstract void onDownloadProgress(int progress);

        public abstract void onDownloadFinished(String downloadPath, String md5);

        public abstract void onDownloadPaused(String reason);

        public abstract void onDownloadError(String reason);
    }

    // Abstract Methods
    public abstract boolean restartDownloading();

    public abstract boolean checkIfDownloading(String md5);

    public abstract boolean checkIfDownloadCompleted(String md5);

    public abstract String getDownloadDir(long requiredSize);

    protected abstract void realDownloadFile(String url);

    public abstract void clearDownload();

    // For protected callbacks
    protected void onDownloadStarted() {
        if (null != sCallback) {
            sCallback.onDownloadStarted();
        }
    }

    protected void onDownloadProgress(final int progress) {

        if (progress != mLastProgress) {
            mLastProgress = progress;
            sLog.debug("onDownloadProgress --> " + progress);
        }

        if (null != sCallback) {
            sCallback.onDownloadProgress(progress);
        }
    }

    protected void onDownloadFinished(final String path, final String md5) {
        sDownloadingRom = false;
        sPreferenceHelper.setDownloadFinished(true);
        sPreferenceHelper.setDownloadPath(path);

        if (null != sCallback) {
            sCallback.onDownloadFinished(path, md5);
        } else {
            Intent intent = new Intent(ACTION_DOWNLOAD_FINISHED);
            intent.setClass(sContext, UpdateService.class);
            intent.putExtra(EXTRA_PATH, path);
            intent.putExtra(EXTRA_MD5, md5);
            sContext.startService(intent);
        }

        // 判断此下载包是否是推送的强制升级包，如果是则向BCB中写入控制命令，让下次开机直接进recovery，并向/cache/recovery/comand文件中写入命令
        final boolean isForce = sPreferenceHelper.isForceOTA();
        final String forceOTAMd5 = sPreferenceHelper.getForceOTAMd5();
        if (!TextUtils.isEmpty(path) && !TextUtils.isEmpty(md5) && isForce && md5.equals(forceOTAMd5)) {

            (new Thread() {

                public void run() {

                    final String calculatedMd5 = MD5Tools.calcMD5(new File(path));
                    sLog.info("calculatedMd5:" + calculatedMd5);

                    if (isForce && calculatedMd5.equals(forceOTAMd5)) {
                        if (Utils.writeForceOTAFlags(path)) {
                            sPreferenceHelper.setForceOTA(false);
                            sPreferenceHelper.setForceOTAMd5("");
                            sLog.info("Force ota flag has been set !!!");
                        } else {
                            sLog.error("Package's md5sum mismatched ! force ota flag not set !!!");
                        }
                    }
                }
            }).start();
        }
    }

    protected void onDownloadPaused(final String reason) {
        if (null != sCallback) {
            sCallback.onDownloadPaused(reason);
        }
    }

    protected void onDownloadError(final String reason) {
        sDownloadingRom = false;
        if (null != sCallback) {
            sCallback.onDownloadError(reason);
        }
        Utils.captureOTALog();
    }

    // common methods
    public void registerCallback(DownloadCallback callback) {
        sCallback = callback;
    }

    public void unregisterCallback() {
        sCallback = null;
    }

    public void downloadFile(final String url) {
        sLog.debug("downloadFile:: url:" + url);
        onDownloadStarted();

        if (!TextUtils.isEmpty(url)) {

            new AsyncTask<Void, Void, Void>() {

                @Override
                protected Void doInBackground(Void... params) {

                    HttpURLConnection conn = null;
                    try {
                        URL requestUrl = new URL(url);
                        conn = (HttpURLConnection) requestUrl.openConnection();
                        conn.setConnectTimeout(20000);
                        conn.setReadTimeout(5000);
                        conn.setRequestMethod("HEAD");
                        int responseCode = conn.getResponseCode();
                        // compatible with http url redirction
                        String downloadUrl = "";
                        if (200 == responseCode || 302 == responseCode) {
                            downloadUrl = conn.getURL().toString();
                        }
                        if (!TextUtils.isEmpty(downloadUrl)) {
                            realDownloadFile(downloadUrl);
                        } else {
                            onDownloadError("Get download url error");
                        }
                    } catch (MalformedURLException e) {
                        sLog.error(e.toString());
                        onDownloadError(e.toString());
                    } catch (IOException e) {
                        sLog.error(e.toString());
                        onDownloadError(e.toString());
                    } finally {
                        if (null != conn) {
                            conn.disconnect();
                        }
                    }
                    return null;
                }

            }.execute((Void) null);

        }
    }

    public boolean isDownloading() {
        return sDownloadingRom;
    }
}
