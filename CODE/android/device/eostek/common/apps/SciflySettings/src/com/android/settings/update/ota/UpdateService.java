
package com.android.settings.update.ota;

import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Locale;

import org.json.JSONException;
import org.json.JSONObject;

import scifly.device.Device;

import com.android.settings.R;
import com.android.settings.update.ota.Downloader.DownloadCallback;
import com.android.settings.update.ota.RomChecker.CheckCallback;

import android.app.DownloadManager;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.os.Binder;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.PowerManager;
import android.os.RecoverySystem;
import android.os.RecoverySystem.ProgressListener;
import android.text.TextUtils;
import android.widget.Toast;

public class UpdateService extends Service implements Constants {
    private static Logger sLog = new Logger(UpdateService.class);

    private static PreferenceHelper sPreferenceHelper;

    private Context mContext;

    private volatile Looper mServiceLooper;

    private volatile ServiceHandler mServiceHandler;

    private final class ServiceHandler extends Handler {
        public ServiceHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {

            if (msg.obj instanceof Intent) {

                Intent intent = (Intent) msg.obj;
                String action = intent.getAction();

                if (DownloadManager.ACTION_DOWNLOAD_COMPLETE.equals(action)) {

                    long downloadId = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);

                    if (downloadId > 0 && sPreferenceHelper.getDownloadId() == downloadId) {
                        DownloadHelper.checkDownloadFinished(mContext, downloadId);
                    }

                } else if (ACTION_UPDATE_INCREMENTAL_ALL.equals(action)) {

                    /*
                     * MsgCenter pushed this task "0": incremental package
                     * requested "1": full package requested
                     */
                    String typeStr = intent.getStringExtra(EXTRA_INCREMENTAL_ALL);

                    try {
                        int type = Integer.parseInt(typeStr);
                        if (OTA_TYPE_INCREASE == type || OTA_TYPE_FULL == type) {
                            new AutoUpdate(mContext, type);
                        }

                    } catch (Exception e) {

                    }

                } else if (Intent.ACTION_BOOT_COMPLETED.equals(action)) {

                    registerConnRecevier();

                } else if (ACTION_UPDATE_CLOUD_PUSH.equals(action)) {

                    String taskId = intent.getStringExtra(EXTRA_CLOUD_PUSH_TASKID);
                    boolean force = intent.getBooleanExtra(EXTRA_CLOUD_PUSH_FORCE, false);
                    new CloudUpdate(mContext, taskId, force);

                } else if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {

                    unregisterConnRecevier();
                    if (Utils.lastOTAFail()) {
                        stopSelf();
                    } else {
                        new BootUpdate(mContext);
                    }
                    cleanLegacyPkgs();

                } else if (ACTION_DOWNLOAD_FINISHED.equals(action)) {

                    final String path = intent.getStringExtra(EXTRA_PATH);
                    final String md5 = intent.getStringExtra(EXTRA_MD5);

                    sLog.info("Download Over: " + path + ", md5:" + md5);

                    PackageInfo pkgInfo = sPreferenceHelper.getPackageInfo();
                    if (pkgInfo != null && !pkgInfo.isNewerThanCurrent()) {
                        sLog.info("Old package download finished, skip it !");
                        sPreferenceHelper.setDownloadFinished(false);
                        DownloadHelper.clearDownload();
                        return;
                    }

                    if (!TextUtils.isEmpty(path) && !TextUtils.isEmpty(md5)) {

                        final File file = new File(path);

                        (new Thread() {

                            public void run() {

                                final String calculatedMd5 = MD5Tools.calcMD5(file);
                                sLog.info("calculatedMd5:" + calculatedMd5);

                                if (md5.equals(calculatedMd5)) {
                                    mServiceHandler.obtainMessage(MSG_MD5_MATCH).sendToTarget();
                                } else {
                                    sLog.info("Package's md5sum mismatched ! PLEASE DOWNLOAD AGAIN !!!");
                                    mServiceHandler.obtainMessage(MSG_MD5_MISMATCH).sendToTarget();
                                }
                            }
                        }).start();
                    }
                }

            } else {

                switch (msg.what) {
                    case MSG_NEW_VERSION: {
                        PackageInfo pkgInfo = sPreferenceHelper.getPackageInfo();
                        new NewVersionDialog(mContext, mServiceHandler, pkgInfo);
                    }
                        break;

                    case MSG_UPGRADE: {
                        Toast.makeText(UpdateService.this, R.string.system_net_update_toast, Toast.LENGTH_LONG).show();
                        PackageInfo pkgInfo = sPreferenceHelper.getPackageInfo();
                        DownloadHelper.downloadFile(pkgInfo.getUrl());
                    }
                        break;
                    case MSG_CLOUD_PUSH: {
                        PackageInfo pkgInfo = sPreferenceHelper.getPackageInfo();
                        DownloadHelper.downloadFile(pkgInfo.getUrl());
                    }
                        break;
                    case MSG_CANCEL:
                        DownloadHelper.clearDownload();
                        stopSelf();
                        break;
                    case MSG_REBOOT_NOW:
                        String downloadPath = sPreferenceHelper.getDownloadPath();
                        if (!TextUtils.isEmpty(downloadPath)) {
                            if (!installPackage(mContext, downloadPath)) {
                                sLog.error("Exception happened while installing OTA package !");
                            }
                        }
                        break;
                    case MSG_REBOOT_LATER:
                        stopSelf();
                        break;
                    case MSG_MD5_MATCH:
                        new CountdownDialog(mContext, mServiceHandler);
                        break;
                    case MSG_MD5_MISMATCH:
                        Toast.makeText(UpdateService.this, R.string.verify_md5, Toast.LENGTH_LONG).show();
                        DownloadHelper.clearDownload();
                        sLog.error("Download package was damaged, clear download task.");
                        // process sn+ download task
                        String engine = sPreferenceHelper.getDownloadEngine();
                        if (!TextUtils.isEmpty(engine)) {
                            if (DOWNLOAD_ENGINE_P2P.equals(engine)) {
                                P2pDownloader p2pDownloader = P2pDownloader.getInstance(mContext);
                                p2pDownloader.hardRemoveDownload();
                            }
                        }
                        Utils.captureOTALog();
                        break;
                    default:
                        break;
                }
            }
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        HandlerThread thread = new HandlerThread("UpdateService");
        thread.start();

        mServiceLooper = thread.getLooper();
        mServiceHandler = new ServiceHandler(mServiceLooper);

        mContext = this;
        sPreferenceHelper = PreferenceHelper.getInstance(mContext);
    }

    @Override
    public void onStart(Intent intent, int startId) {
        Message msg = mServiceHandler.obtainMessage();
        msg.arg1 = startId;
        msg.obj = intent;
        mServiceHandler.sendMessage(msg);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        onStart(intent, startId);
        return START_NOT_STICKY;
    }

    @Override
    public void onDestroy() {
        mServiceLooper.quit();
    }

    public class UpdateBinder extends Binder {
        public UpdateService getService() {
            return UpdateService.this;
        }
    }

    private Binder mBinder = new UpdateBinder();

    @Override
    public IBinder onBind(Intent paramIntent) {
        return mBinder;
    }

    // //////////////////////////////////////////////////////////////////////////
    private BroadcastReceiver mConnectivityRecevier = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            sLog.debug("onReceive::" + intent.toString());
            if (Utils.isNetworkAvailable(mContext)) {
                onStart(intent, 0);
            }
        }
    };

    private void registerConnRecevier() {
        IntentFilter iFilter = new IntentFilter();
        iFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        mConnectivityRecevier.setDebugUnregister(true);
        mContext.registerReceiver(mConnectivityRecevier, iFilter);
    }

    private void unregisterConnRecevier() {
        if (mConnectivityRecevier.getDebugUnregister()) {
            mContext.unregisterReceiver(mConnectivityRecevier);
        }
    }

    class BaseUpdate {
        protected Context mContext;

        protected RomChecker mRomChecker;

        protected DownloadCallback mDownloadCallback = new DownloadCallback() {

            @Override
            public void onDownloadStarted() {
                onDownloadProgress(0);
            }

            @Override
            public void onDownloadProgress(int progress) {
                if (progress < 0 || progress > 100) {
                    sLog.error("impossible progress ...");
                    return;
                }

            }

            @Override
            public void onDownloadFinished(String path, final String md5) {
                sLog.debug("BaseUpdate::onDownloadFinished: " + path);
                // than broadcast download finished intent.
                Intent intent = new Intent(ACTION_DOWNLOAD_FINISHED);
                intent.setClass(mContext, UpdateService.class);
                intent.putExtra(EXTRA_PATH, path);
                intent.putExtra(EXTRA_MD5, md5);
                mContext.startService(intent);
            }

            @Override
            public void onDownloadError(String reason) {

            }

            @Override
            public void onDownloadPaused(String reason) {

            }
        };

        protected BaseUpdate(Context context) {
            mContext = context;
            mRomChecker = new RomChecker(context);
        }
    }

    class AutoUpdate extends BaseUpdate {

        private CheckCallback mAutoCheckCallback = new CheckCallback() {

            @Override
            public void onStartChecking() {
                DownloadHelper.init(mContext, mDownloadCallback);
            }

            @Override
            public void onVersionFound(PackageInfo pkgInfo) {

                if (null == pkgInfo || !pkgInfo.isLegal()) {
                    return;
                }
                sLog.debug("versionFound: " + pkgInfo.toString());

                if (DownloadHelper.checkIfDownloading(pkgInfo.getMd5())) {
                    DownloadHelper.restartDownloading();
                    DownloadHelper.registerCallback(mDownloadCallback);
                } else {

                    if (pkgInfo.isNewerThanCurrent()) {

                        if (DownloadHelper.checkIfDownloadCompleted(pkgInfo.getMd5())) {
                            sLog.info("OTA package had download finished.");
                            mDownloadCallback.onDownloadFinished(sPreferenceHelper.getDownloadPath(), pkgInfo.getMd5());
                        } else {
                            DownloadHelper.clearDownload();
                            sPreferenceHelper.savePackageInfo(pkgInfo);
                            boolean hasEnoughSpace = DownloadHelper.hasEnoughSpace(pkgInfo.getSize());
                            if (hasEnoughSpace) {
                                DownloadHelper.registerCallback(mDownloadCallback);
                                mServiceHandler.obtainMessage(MSG_UPGRADE).sendToTarget();
                            }
                        }

                    } else {
                        sPreferenceHelper.setEtag("");
                        DownloadHelper.clearDownload();
                    }
                }
            }

            @Override
            public void onCheckError() {

            }
        };

        public AutoUpdate(Context context, int type) {
            super(context);
            mRomChecker.setCheckCallback(mAutoCheckCallback);
            boolean checking = mRomChecker.isScanning();
            if (!checking) {
                mRomChecker.check(type);
            }

        }
    }

    class BootUpdate extends BaseUpdate {
        private CheckCallback mBootCheckCallback = new CheckCallback() {

            @Override
            public void onStartChecking() {
                DownloadHelper.init(mContext, mDownloadCallback);
            }

            @Override
            public void onVersionFound(PackageInfo pkgInfo) {

                if (null == pkgInfo || !pkgInfo.isLegal()) {
                    return;
                }
                sLog.debug("versionFound: " + pkgInfo.toString());

                if (DownloadHelper.checkIfDownloading(pkgInfo.getMd5())) {
                    DownloadHelper.restartDownloading();
                    DownloadHelper.registerCallback(mDownloadCallback);
                } else {

                    if (pkgInfo.isNewerThanCurrent()) {

                        if (DownloadHelper.checkIfDownloadCompleted(pkgInfo.getMd5())) {
                            sLog.info("OTA package had download finished.");
                            mDownloadCallback.onDownloadFinished(sPreferenceHelper.getDownloadPath(), pkgInfo.getMd5());
                        } else {
                            DownloadHelper.clearDownload();
                            sPreferenceHelper.savePackageInfo(pkgInfo);
                            boolean hasEnoughSpace = DownloadHelper.hasEnoughSpace(pkgInfo.getSize());
                            if (hasEnoughSpace) {
                                DownloadHelper.registerCallback(mDownloadCallback);
                                mServiceHandler.obtainMessage(MSG_NEW_VERSION).sendToTarget();
                            }
                        }

                    } else {
                        sPreferenceHelper.setEtag("");
                        DownloadHelper.clearDownload();
                    }
                }
            }

            @Override
            public void onCheckError() {

            }
        };

        public BootUpdate(Context context) {
            super(context);
            mRomChecker.setCheckCallback(mBootCheckCallback);
            boolean checking = mRomChecker.isScanning();
            if (!checking) {
                mRomChecker.check(OTA_TYPE_INCREASE);
            }
        }
    }

    // class for cloud push ota
    class CloudUpdate {

        protected Context mContext;

        protected RomChecker mRomChecker;

        private P2pDownloader mP2pDownloader;

        private String mTaskId;

        public CloudUpdate(Context context, String taskId, boolean force) {
            mContext = context;
            // Cloud Push must be p2p
            boolean bRet = DownloadHelper.setDownloadEngine(mContext, DOWNLOAD_ENGINE_P2P);
            if (bRet) {
                mTaskId = taskId;
                sPreferenceHelper.setForceOTA(force);
                mP2pDownloader = P2pDownloader.getInstance(context);
                mRomChecker = new RomChecker(context);
                mRomChecker.setCheckCallback(mCheckCallback);
                boolean checking = mRomChecker.isScanning();
                if (!checking) {
                    mRomChecker.check(OTA_TYPE_INCREASE);
                }

            }
        }

        protected DownloadCallback mDownloadCallback = new DownloadCallback() {

            @Override
            public void onDownloadStarted() {
                onDownloadProgress(0);
            }

            @Override
            public void onDownloadProgress(int progress) {
                if (progress < 0 || progress > 100) {
                    sLog.error("impossible progress ...");
                    return;
                }
            }

            @Override
            public void onDownloadFinished(String path, final String md5) {
                sLog.debug("CloudUpdate::onDownloadFinished: " + path);
            }

            @Override
            public void onDownloadError(String reason) {

            }

            @Override
            public void onDownloadPaused(String reason) {

            }
        };

        protected CheckCallback mCheckCallback = new CheckCallback() {

            @Override
            public void onStartChecking() {
                DownloadHelper.init(mContext, mDownloadCallback);
            }

            @Override
            public void onVersionFound(PackageInfo pkgInfo) {

                if (null == pkgInfo || !pkgInfo.isLegal()) {
                    return;
                }
                sLog.debug("versionFound: " + pkgInfo.toString());

                boolean isValidPush = false;
                String pushResult = "OK";
                if (DownloadHelper.checkIfDownloading(pkgInfo.getMd5())) {
                    DownloadHelper.restartDownloading();
                    DownloadHelper.registerCallback(mDownloadCallback);
                    pushResult = "Already downloading ota package .";
                } else {

                    if (pkgInfo.isNewerThanCurrent()) {

                        if (sPreferenceHelper.isForceOTA()) {
                            sPreferenceHelper.setForceOTAMd5(pkgInfo.getMd5());
                        }

                        if (DownloadHelper.checkIfDownloadCompleted(pkgInfo.getMd5())) {
                            sLog.info("OTA package had download finished.");
                            pushResult = "OTA package had download finished.";
                            mDownloadCallback.onDownloadFinished(sPreferenceHelper.getDownloadPath(), pkgInfo.getMd5());
                        } else {
                            DownloadHelper.clearDownload();
                            sPreferenceHelper.savePackageInfo(pkgInfo);
                            boolean hasEnoughSpace = DownloadHelper.hasEnoughSpace(pkgInfo.getSize());
                            if (hasEnoughSpace) {
                                DownloadHelper.registerCallback(mDownloadCallback);
                                mServiceHandler.obtainMessage(MSG_CLOUD_PUSH).sendToTarget();
                                mP2pDownloader.setP2pTaskListener(mP2pTaskListener);
                                isValidPush = true;
                            } else {
                                pushResult = "Disk full , have not enough space.";
                            }
                        }

                    } else {
                        sPreferenceHelper.setEtag("");
                        DownloadHelper.clearDownload();
                        pushResult = "Version downgraded, drop it.";
                    }
                }

                if (!isValidPush) {
                    if (reportCloudPushstatus(mContext, mTaskId, pkgInfo.getVersion(), pushResult)) {
                        sLog.info("report cloud push status OK !");
                    } else {
                        sLog.info("report cloud push status Failed !");
                    }
                }
            }

            @Override
            public void onCheckError() {

            }
        };

        private P2pDownloader.P2pTaskListener mP2pTaskListener = new P2pDownloader.P2pTaskListener() {

            @Override
            public void onTaskStarted(String taskId) {
                PackageInfo pkgInfo = sPreferenceHelper.getPackageInfo();
                String targetVer = PackageInfo.getCurrentVerion();
                if (pkgInfo != null) {
                    targetVer = pkgInfo.getVersion();
                }

                if (reportCloudPushstatus(mContext, mTaskId, targetVer, "OK")) {
                    sLog.info("report cloud push status OK !");
                } else {
                    sLog.info("report cloud push status Failed !");
                }
            }

            @Override
            public void onTaskError(String reason) {
                PackageInfo pkgInfo = sPreferenceHelper.getPackageInfo();
                String targetVer = PackageInfo.getCurrentVerion();
                if (pkgInfo != null) {
                    targetVer = pkgInfo.getVersion();
                }
                if (reportCloudPushstatus(mContext, mTaskId, targetVer, reason)) {
                    sLog.info("report cloud push status OK !");
                } else {
                    sLog.info("report cloud push status Failed !");
                }
            }
        };

        // report cloud push status
        private boolean reportCloudPushstatus(Context context, String taskId, String targetVer, String result) {

            JSONObject requestJson = getReportJson(context, taskId, targetVer, result);
            if (null == requestJson) {
                sLog.error("Request json is null.");
                return false;
            }
            sLog.debug("request json: " + requestJson.toString());

            HttpURLConnection httpUrlConnection = null;
            Writer writer = null;

            int responseCode = -1;

            try {
                URL url = new URL("http://tvosapp.babao.com/interface/clientService.jsp");
                httpUrlConnection = (HttpURLConnection) url.openConnection();
                httpUrlConnection.setRequestProperty("Content-Type", "text/json; charset=UTF-8");
                httpUrlConnection.setRequestMethod("POST");
                httpUrlConnection.setDoOutput(true);
                httpUrlConnection.setConnectTimeout(20000);
                httpUrlConnection.setReadTimeout(5000);

                writer = new OutputStreamWriter(httpUrlConnection.getOutputStream(), "utf-8");
                writer.write(requestJson.toString());
                writer.flush();

                httpUrlConnection.connect();

                responseCode = httpUrlConnection.getResponseCode();
                sLog.debug("response code: " + responseCode);
            } catch (Exception e) {
                sLog.error(e.getMessage());
                return false;
            } finally {
                if (httpUrlConnection != null) {
                    httpUrlConnection.disconnect();
                }
                if (writer != null) {
                    try {
                        writer.close();
                    } catch (IOException e) {
                        sLog.error(e.getMessage());
                        return false;
                    }
                }
            }

            return responseCode == 200 ? true : false;
        }

        private JSONObject getReportJson(Context context, String taskId, String targetVer, String result) {
            JSONObject reqestJson = new JSONObject();
            try {
                reqestJson.put("ifid", "cloudPushReport");
                reqestJson.put("taskId", taskId);
                reqestJson.put("productClass", Device.getDeviceCode());
                reqestJson.put("currentVer", PackageInfo.getCurrentVerion());
                reqestJson.put("targetVer", targetVer);
                reqestJson.put("execTime", TextUtils.isEmpty(taskId) ? 0 : System.currentTimeMillis() / 1000);
                reqestJson.put("mac", Utils.getHexMac(context));
                reqestJson.put("bbNo", Device.getBb());
                reqestJson.put("result", result);
            } catch (JSONException e) {
                return null;
            }

            return reqestJson;
        }
    }

    // ////////////////////////////////////////////////////////////////////////
    /**
     * Install package and reboot to recovery.
     * 
     * @param context Application context.
     * @param filePath Package location.
     * @return True if install successfully, otherwise false.
     */
    public boolean installPackage(Context context, String filePath) {
        sLog.info("Install ota package : " + filePath);

        File file = new File(filePath);
        if (!file.exists()) {
            sLog.error(filePath + " not exists!");
            return false;
        }

        try {
            RecoverySystem.ProgressListener progressListener = new ProgressListener() {
                @Override
                public void onProgress(int progress) {
                    sLog.info("VerfyPackage onProgress: " + progress);
                }
            };
            RecoverySystem.verifyPackage(file, progressListener, null);

            File recoveryDir = new File(RECOVERY_DIR);
            if (recoveryDir.mkdirs() || recoveryDir.isDirectory()) {
                File commandFile = new File(recoveryDir, COMMAND_FILE_NAME);
                commandFile.delete();
                FileWriter fw = new FileWriter(commandFile);
                try {
                    StringBuilder b = new StringBuilder();
                    b.append("--update_package=");
                    b.append(filePath);
                    b.append("\n");
                    b.append("--locale=");
                    b.append(Locale.getDefault().toString());
                    b.append("\n");
                    fw.write(b.toString());
                    sLog.info("command:" + b.toString());
                } finally {
                    fw.close();
                }
            }
        } catch (Exception e) {
            sLog.error(e.toString());
            return false;
        }
        sLog.info("Verify successfully, rebooting now ...");

        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        pm.reboot("recovery");
        return true;
    }

    private void cleanLegacyPkgs() {
        SharedPreferences sp = mContext.getSharedPreferences(OLD_PREFERENCE_NAME, Context.MODE_PRIVATE);

        // process legacy http downloaded pkg
        String lastFile = sp.getString(OLD_PREFERENCE_LAST_FILE_PATH, INVALID_STRING);
        if (!TextUtils.isEmpty(lastFile) && !INVALID_STRING.equals(lastFile)) {
            File file = new File(lastFile);
            if (file != null && file.exists()) {
                file.delete();
                sLog.info("deleted legacy ota package :" + lastFile);
            }
        }

        // process legacy p2p downloaded pkg
        String lastSnFile = sp.getString(OLD_PREFERENCE_LAST_SN_FILE_PATH, INVALID_STRING);
        if (!TextUtils.isEmpty(lastSnFile) && !INVALID_STRING.equals(lastSnFile)) {
            File file = new File(lastSnFile);
            if (file != null && file.exists()) {
                file.delete();
                sLog.info("deleted legacy ota package :" + lastSnFile);
            }
        }

        Editor editor = sp.edit();
        editor.clear();
        editor.commit();

    }
}
