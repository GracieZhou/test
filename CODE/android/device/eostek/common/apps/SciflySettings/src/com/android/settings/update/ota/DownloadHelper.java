
package com.android.settings.update.ota;

import com.android.settings.update.ota.Downloader.DownloadCallback;

import scifly.provider.SciflyStore;

import android.content.Context;
import android.text.TextUtils;

public class DownloadHelper implements Constants {
    private static Logger sLog = new Logger(DownloadHelper.class);

    private static Context sContext;

    private static PreferenceHelper sPreferenceHelper;

    private static DownloadCallback sCallback;

    private static Downloader sDownloader;

    private static HttpDownloader sHttpDownloader;

    private static P2pDownloader sP2pDownloader;

    private static boolean sInitializing = false;

    public synchronized static void init(Context context, DownloadCallback callback) {

        if (sInitializing) {
            sLog.error(" on initializing.");
            return;
        }
        sInitializing = true;
        sContext = context;
        sCallback = callback;
        sPreferenceHelper = PreferenceHelper.getInstance(context);

        String currEngine = getDownloadEngine();

        if (DOWNLOAD_ENGINE_P2P.equals(currEngine)) {
            sP2pDownloader = P2pDownloader.getInstance(context);
            sDownloader = sP2pDownloader;
        } else {
            sHttpDownloader = HttpDownloader.getInstance(context);
            sDownloader = sHttpDownloader;
        }

        // handle the engine switch event during downloading .
        String prevEngine = sPreferenceHelper.getDownloadEngine();
        if (!TextUtils.isEmpty(prevEngine) && !prevEngine.equals(currEngine)) {

            String md5 = sPreferenceHelper.getPackageMd5();
            if (DOWNLOAD_ENGINE_HTTP.equals(prevEngine)) {
                if (sHttpDownloader == null) {
                    sHttpDownloader = HttpDownloader.getInstance(context);
                }

                if (sHttpDownloader.checkIfDownloading(md5)) {
                    sHttpDownloader.clearDownload();
                }
            } else {
                if (sP2pDownloader == null) {
                    sP2pDownloader = P2pDownloader.getInstance(context);
                }

                if (sP2pDownloader.checkIfDownloading(md5)) {
                    sP2pDownloader.clearDownload();
                }
            }

        }

        sPreferenceHelper.setDownloadEngine(currEngine);
        sInitializing = false;
    }

    private static String getDownloadEngine() {
        if (null != sContext) {
            String engine = SciflyStore.Global.getString(sContext.getContentResolver(),
                    SciflyStore.Global.DOWNLOAD_ENGINE, DOWNLOAD_ENGINE_HTTP);
            sLog.info("Download engine:" + engine);
            return engine;
        }

        return null;
    }

    public static void registerCallback(DownloadCallback callback) {
        sDownloader.registerCallback(callback);
    }

    public static void unregisterCallback() {
        sDownloader.unregisterCallback();
    }

    public static void clearDownload() {
        sDownloader.clearDownload();
    }

    public static void checkDownloadFinished(Context context, long downloadId) {
        if (null == sHttpDownloader) {
            sHttpDownloader = HttpDownloader.getInstance(context);
        }
        sHttpDownloader.registerCallback(sCallback);
        sHttpDownloader.checkDownloadFinished(downloadId);
    }

    public static boolean isDownloading() {
        return sDownloader.isDownloading();
    }

    public static boolean hasEnoughSpace(long requiredSize) {

        String downloadDir = sDownloader.getDownloadDir(requiredSize);
        if (TextUtils.isEmpty(downloadDir)) {
            return false;
        }

        return true;
    }

    public static void downloadFile(final String url) {
        sDownloader.downloadFile(url);
    }

    public static boolean checkIfDownloading(String md5) {
        return sDownloader.checkIfDownloading(md5);
    }

    public static boolean checkIfDownloadCompleted(String md5) {
        return sDownloader.checkIfDownloadCompleted(md5);
    }

    public static boolean restartDownloading() {
        return sDownloader.restartDownloading();
    }

    /**
     * NOTICE: if you need to force set download engine , you must call this
     * before init()
     * 
     * @param context the component's context
     * @param engine one of the http or p2p
     * @return true if set success otherwise false
     */
    public static boolean setDownloadEngine(Context context, String engine) {
        if (DOWNLOAD_ENGINE_HTTP.equals(engine) || DOWNLOAD_ENGINE_P2P.equals(engine)) {
            if (null != context) {
                boolean bRet = SciflyStore.Global.putString(context.getContentResolver(),
                        SciflyStore.Global.DOWNLOAD_ENGINE, engine);

                if (bRet) {
                    sPreferenceHelper = PreferenceHelper.getInstance(context);
                    sPreferenceHelper.setDownloadEngine(engine);
                }

                sLog.info("Force set download engine:" + engine);
                return bRet;
            }
        }

        return false;
    }
}
