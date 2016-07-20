
package com.android.settings.update;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.json.JSONException;
import org.json.JSONObject;

import scifly.dm.EosDownloadManager;
import scifly.dm.EosDownloadTask;
import scifly.provider.SciflyStore;
import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Binder;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.util.Log;
import android.widget.Toast;

import com.android.settings.R;
import com.eostek.streamnetplusservice.service.ResultListener;
import com.eostek.streamnetplusservice.service.StreamNetManager;
import com.eostek.streamnetplusservice.service.TaskInfoInternal;
import com.eostek.streamnetplusservice.service.TaskListener;

public class UpdateService extends Service {

    public interface EngineChangeListener {
        public void onEngineChange();
    }

    private EngineChangeListener mEngineChangeListener;

    /**
     * All version info.
     */
    private VersionInfor mInfo;

    /**
     * Thread pool, handle all network request.
     */
    private ExecutorService mThreadPool;

    /**
     * Android DownloadManager.
     */
    private EosDownloadManager mManager;

    /**
     * StreamNetManager.
     */
    private StreamNetManager mSnManager;

    /**
     * Handle all Dialog.
     */
    private Handler mHandler = new Handler() {
        private String filePath;

        private CountDownDialog countDownDialog;

        private UpdateDialog updateDialog;

        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Constants.MSG_SHOW_COUNT_DIALOG:
                    filePath = (String) msg.obj;
                    if (countDownDialog == null || !countDownDialog.isShowing()) {
                        log("Showing CountDownDialog...");
                        countDownDialog = new CountDownDialog(UpdateService.this, mHandler,
                                Constants.COUNTING_DIALOG_TIME, Constants.COUNTING_DIALOG_INTERVAL_TIME);
                    }
                    break;
                case Constants.MSG_DIALOG_OK:
                    log("Dialog press OK, Going to reboot and install package...");
                    if (Utils.isEmpty(filePath) || !Utils.verify(filePath, mInfo.getLastMd())
                            || !Utils.install(getApplicationContext(), filePath)) {
                        loge("Failed to install package, clear version info!");
                        /*
                         * Capture update log and record on
                         * /sdcard/Download/ota/log.txt
                         */
                        Utils.captureUpdateLog();
                        Toast.makeText(UpdateService.this, R.string.verify_package_failed_toast, Toast.LENGTH_LONG)
                                .show();
                        clear();
                        stopSelf();
                    }
                    break;
                case Constants.MSG_DIALOG_CANCEL:
                    log("Dialog press Cancel, trying to stop UpdateService...");
                    stopSelf();
                    break;
                case Constants.MSG_CPE_VERSION_INFO_DIALOG:
                    if (updateDialog == null || !updateDialog.isShowing()) {
                        log("Showing UpdateDialog...");

                        DisplayInfo info = new DisplayInfo();
                        info.setVersion(mInfo.getVersion());
                        info.setTime(mInfo.getTime());
                        info.setDescription(mInfo.getUds());
                        info.setSize(mInfo.getSize());
                        updateDialog = new UpdateDialog(UpdateService.this, mHandler, info);
                    }
                    break;
                case Constants.MSG_CPE_DIALOG_OK:
                    log("CPE Updating OK...");
                    Toast.makeText(UpdateService.this, R.string.system_net_update_toast, Toast.LENGTH_LONG).show();
                    startUpdate();
                    break;
                case Constants.MSG_CPE_DIALOG_CANCEL:
                    log("CPE Updating CANCEL, clear version info, trying to stop UpdateService...");
                    clear();
                    stopSelf();
                    break;
                case Constants.MSG_DELAY_DOWNLOAD:
                    continueUpdate();
                    break;
                case Constants.MSG_DELAY_DELETE_SN_TASK:
                    String lastSnId = (String) msg.obj;
                    String downloadDir = Utils.getSnDownloadPath();
                    if (mSnManager.isDiskReady(downloadDir)) {
                        log("mHandler: removeTaskAndFile snId=" + lastSnId);
                        mSnManager.removeTaskAndFile(lastSnId);
                    } else {
                        log("mHandler: Disk " + downloadDir + " is not ready, delay deleting for 1 second.");
                        mSnManager.addDiskPath(downloadDir);
                        Message delayMsg = mHandler.obtainMessage(Constants.MSG_DELAY_DELETE_SN_TASK, lastSnId);
                        mHandler.sendMessage(delayMsg);
                    }
                    break;
               case Constants.MSG_STORAGE_EXTERNAL_FULL:
                    Toast.makeText(UpdateService.this, R.string.sdcard_full_for_ota, Toast.LENGTH_LONG).show();
                    break;
               default:
                    break;
            }
        };
    };

    public boolean regitsterEngineChangeListener(EngineChangeListener listener) {
        mEngineChangeListener = listener;
        return true;
    }

    public void unregitsterEngineChangeListener(EngineChangeListener listener) {
        mEngineChangeListener = null;
    }

    /**
     * Check if cache has enough space.
     *
     * @return True if enough, otherwise false.
     */
    private boolean isCacheAvailable() {
        if (mInfo == null) {
            return true;
        }
        long availableCacheSize = Utils.getAvailableSize("/cache") / 1024;
        long requiredSize = mInfo.getSize();
        // The space of the last update package is available.
        String lastFilePath = Utils.getString(getApplicationContext(), Constants.PREFERENCE_LAST_FILE_PATH, null);
        if (!Utils.isEmpty(lastFilePath) && lastFilePath.startsWith("/cache")) {
            File lastFile = new File(lastFilePath);
            if (lastFile != null && lastFile.exists()) {
                requiredSize -= (lastFile.length() / 1024);
            }
        }
        log("First check cache: availableCacheSize=" + availableCacheSize + "KB, requiredSize=" + requiredSize + "KB");
        if (requiredSize > availableCacheSize) {
            // No enough space in /cache, delete useless files and report.
            Utils.reportCacheFullAndClean(getApplicationContext());
        } else {
            return true;
        }
        // Get available space again.
        availableCacheSize = Utils.getAvailableSize("/cache") / 1024;
        if (requiredSize > availableCacheSize) {
            log("Second check cache: availableCacheSize=" + availableCacheSize + "KB, requiredSize=" + requiredSize
                    + "KB");
            return false;
        } else {
            return true;
        }
    }

    /**
     * Check if external storage has enough space.
     * 
     * @return True if enough, otherwise false.
     */
    private boolean isExternalStorageAvailable() {
        if (mInfo == null) {
            return true;
        }
        String downloadDir = Environment.getExternalStorageDirectory().getAbsolutePath();
        long availableExternalSize = Utils.getAvailableSize(downloadDir) / 1024;
        long requiredSize = mInfo.getSize();
        // The space of the last update package is available.
        String lastSnFilePath = Utils.getString(getApplicationContext(), Constants.PREFERENCE_LAST_SN_FILE_PATH, null);
        if (!Utils.isEmpty(lastSnFilePath)) {
            File lastSnFile = new File(lastSnFilePath);
            if (lastSnFile != null && lastSnFile.exists()) {
                requiredSize -= (lastSnFile.length() / 1024);
            }
        }
        // In case of that http download file exists in external storage.
        String lastFilePath = Utils.getString(getApplicationContext(), Constants.PREFERENCE_LAST_FILE_PATH, null);
        if (!Utils.isEmpty(lastFilePath) && lastFilePath.startsWith(downloadDir)) {
            File lastFile = new File(lastFilePath);
            if (lastFile != null && lastFile.exists()) {
                requiredSize -= (lastFile.length() / 1024);
            }
        }
        log("Check external storage: availableExternalSize=" + availableExternalSize + "KB, requiredSize="
                + requiredSize + "KB");
        if (requiredSize > availableExternalSize) {
            return false;
        } else {
            return true;
        }
    }

    /**
     * Get version info and record it in SharedPreferences. After completed,
     * sendBroadcast to notify others.
     * 
     * @author Psso.Song
     */
    private class GetVersionInfoTask implements Runnable {

        private int source;

        private String packageType;

        public GetVersionInfoTask(int source, String packageType) {
            this.source = source;
            this.packageType = packageType;
        }

        @Override
        public void run() {
            mInfo.setPackageType(packageType);
            String jsonStr = JSONData.getUpgradeInfo(getApplicationContext(), packageType);
            if (jsonStr == null || jsonStr.isEmpty()) {
                loge("Fail to get incremental package, Try full package...");
                if (!Constants.EXTRA_ALL.equals(packageType)) {
                    mInfo.setPackageType(Constants.EXTRA_ALL);
                }
                jsonStr = JSONData.getUpgradeInfo(getApplicationContext(), Constants.EXTRA_ALL);
            }
            if (jsonStr == null || jsonStr.isEmpty()) {
                loge("Fail to get new version!");
                mInfo.setVersion(Constants.INVALID_STRING);
                mInfo.setSize(0);
            } else {
                try {
                    JSONObject json = new JSONObject(jsonStr);
                    int errCode = json.getInt("err");
                    log("errCode = " + errCode);
                    JSONObject body = json.optJSONObject("bd");
                    if (errCode == 0 && body != null) {
                        log("body:" + body.toString());
                        mInfo.setDs(body.optString("ds"));
                        mInfo.setVersion(body.optString("ver", Constants.INVALID_STRING));
                        mInfo.setUds(body.optString("uds"));
                        mInfo.setUrl(Utils.getUrl(body.optString("url")));
                        mInfo.setSize(body.optLong("size", 0));
                        mInfo.setMd(body.optString("md5"));
                        mInfo.setForce(body.optInt("fd", 0));
                        String time = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(body.optLong(
                                "pubTime", 0) * 1000));
                        mInfo.setTime(time);
                    }
                } catch (JSONException e) {
                    loge("JSONException" + e.toString());
                    mInfo.setVersion(Constants.INVALID_STRING);
                    mInfo.setSize(0);
                } catch (Exception e) {
                    loge("Exception" + e.toString());
                    mInfo.setVersion(Constants.INVALID_STRING);
                    mInfo.setSize(0);
                }
            }
            // Load last update record.
            Utils.loadLastVersionInfo(getApplicationContext(), mInfo);

            // Get download engine.
            String engine = SciflyStore.Global.getString(getContentResolver(), SciflyStore.Global.DOWNLOAD_ENGINE,
                    Constants.DOWNLOAD_ENGINE_HTTP);
            log("GetVersionInfo, SciflyStore download engine is " + engine);
            if (Constants.DOWNLOAD_ENGINE_HTTP.equalsIgnoreCase(engine)) {
                mInfo.setEngine(Constants.DOWNLOAD_ENGINE_HTTP);
                if (isCacheAvailable() || isExternalStorageAvailable()) {
                    mInfo.setStorage(true);
                } else {
                    mInfo.setStorage(false);
                }
            } else if (Constants.DOWNLOAD_ENGINE_P2P.equalsIgnoreCase(engine)) {
                if (isExternalStorageAvailable()) {
                    mInfo.setEngine(Constants.DOWNLOAD_ENGINE_P2P);
                    mInfo.setStorage(true);
                } else {
                    mInfo.setEngine(Constants.DOWNLOAD_ENGINE_HTTP);
                    mInfo.setStorage(isCacheAvailable());
                }
            }
            log("GetVersionInfo, finally download engine is " + mInfo.getEngine() + ", Storage status is "
                    + mInfo.getStorage());

            boolean needCheckUpdate = Utils.getBoolean(getApplicationContext(), Constants.PREFERENCE_BOOT_COMPLETED,
                    true);
            if (needCheckUpdate) {
                Utils.putBoolean(getApplicationContext(), Constants.PREFERENCE_BOOT_COMPLETED, false);
            }

            onVersionInfoAvailable(source);
        }
    }

    /**
     * Handle version info.
     * 
     * @param source Indicate who triggered this update.
     */
    private void onVersionInfoAvailable(int source) {
        String lastVersion = mInfo.getLastVersion();
        String newVersion = mInfo.getVersion();
        String curVersion = android.os.Build.VERSION.INCREMENTAL;
        log("onVersionInfoAvailable {source=" + Utils.int2String(source) + ", current package type="
                + mInfo.getPackageType() + ", last package type=" + mInfo.getLastPackageType() + ", lastVersion="
                + lastVersion + ", curVersion=" + curVersion + ", newVersion=" + newVersion + "}");

        if (!Utils.isEmpty(lastVersion)) {
            /**
             * The following situations ensure last version invalid: 1. This
             * request is a full package and last is incremental package; 2.
             * Last version is not newer than current; 3. Last version is not
             * newer than network.
             */
            if ((Constants.EXTRA_ALL.equals(mInfo.getPackageType()) && Constants.EXTRA_INCREMENTAL.equals(mInfo
                    .getLastPackageType()))
                    || (Utils.compareVersion(lastVersion, curVersion) != 1)
                    || (Utils.compareVersion(newVersion, lastVersion) == 1)) {
                log("Current package type=" + mInfo.getPackageType() + ", last package type="
                        + mInfo.getLastPackageType() + "; lastVersion expired, clear version info");
                clear();
                lastVersion = Constants.INVALID_STRING;
            }
        }

        int version = Constants.VERSION_STATUS_INIT;
        if (!Utils.isEmpty(lastVersion)) {
            // Last version is available.
            version = Constants.VERSION_STATUS_LAST;
        } else if (!mInfo.getStorage()) {
            // Storage not enough.
            version = Constants.VERSION_STORAGE_FAIL;
        } else if (Utils.isEmpty(newVersion) || (Utils.compareVersion(newVersion, curVersion) == -1) 
            /*Ignore the situation of newVersion lower than curVersion*/) {
            // Fail to get new version.
            version = Constants.VERSION_STATUS_FAILED;
        } else if (Utils.compareVersion(newVersion, curVersion) == 1) {
            // Find a new version.
            version = Constants.VERSION_STATUS_NETWORK;
        } else {
            // Current version is the latest.
            version = Constants.VERSION_STATUS_CURRENT;
        }

        displayVersionInfo(source, version);
    }

    /**
     * Determine how to display version info.
     * 
     * @param source Indicate who triggered this update.
     * @param version How to display version info.
     */
    private void displayVersionInfo(int source, int version) {
        log("displayVersionInfo {source=" + Utils.int2String(source) + ", packageType=" + mInfo.getPackageType()
                + ", version=" + Utils.int2String(version) + "}");
        if (Constants.VERSION_STATUS_LAST == version) {
            // Last version available.
            continueUpdate();
        }
        switch (source) {
            case Constants.SOURCE_ACTIVITY:
                if (Constants.VERSION_STATUS_LAST != version) {
                    // Send broadcast to Activity.
                    sendBroadcast(version);
                }
                break;
            case Constants.SOURCE_BOOT:
                // Only if find a new version, show the dialog.
                if (Constants.VERSION_STATUS_NETWORK == version) {
                    Message msg = mHandler.obtainMessage(Constants.MSG_CPE_VERSION_INFO_DIALOG);
                    mHandler.sendMessage(msg);
                }
                break;
            case Constants.SOURCE_CPE:
                // Only if find a new version, download the package.
                if (Constants.VERSION_STATUS_NETWORK == version) {
                    startUpdate();
                }
                break;
        }
    }

    private void sendBroadcast(int version) {
        Intent intent = new Intent(Constants.ACTION_VERSION_INFO_AVAILABLE);
        intent.putExtra(Constants.EXTRA_VERSION_INFO, version);
        getApplicationContext().sendBroadcast(intent);
    }

    private void clear() {
        SciflyStore.Global.putString(getContentResolver(), SciflyStore.Global.DOWNLOAD_ID, Long.toString(-1));
        if (mInfo == null) {
            return;
        }
        // Delete last download file.
        File lastDownloadFile = new File(mInfo.getLastFilePath());
        if (lastDownloadFile != null && lastDownloadFile.exists()) {
            lastDownloadFile.delete();
        }
        mInfo.setLastFilePath(Constants.INVALID_STRING);
        // Delete Android download record.
        if (mInfo.getLastId() != -1) {
            mManager.removeTask(mInfo.getLastId());
            mInfo.setLastId(-1);
        }
        mInfo.setLastEngine(Constants.INVALID_STRING);
        mInfo.setLastVersion(Constants.INVALID_STRING);
        mInfo.setLastUds(Constants.INVALID_STRING);
        mInfo.setLastTime(Constants.INVALID_STRING);
        mInfo.setLastSize(-1);
        mInfo.setLastPackageType(Constants.INVALID_STRING);
        mInfo.setLastMd(Constants.INVALID_STRING);
        // Update SharedPreferences.
        Utils.updatePreference(getApplicationContext(), mInfo);
    }

    private void handlerServiceStarted(Intent intent, int flags, int startId) {
        if (intent == null) {
            loge("Unknown intent received, discard it...");
            return;
        }
        String action = intent.getAction();
        if (action.equals(DownloadManager.ACTION_DOWNLOAD_COMPLETE)) {
            /*
             * listen action ACTION_DOWNLOAD_COMPLETE if the completed download
             * task is an updating task, we should finish the updating progress.
             */
            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);
            String lastVersion = Utils.getString(getApplicationContext(), Constants.PREFERENCE_LAST_VER,
                    Constants.INVALID_STRING);
            long lastId = Utils.getLong(getApplicationContext(), Constants.PREFERENCE_LAST_DOWNLOAD_ID, -1);
            String lastFilePath = Utils.getString(getApplicationContext(), Constants.PREFERENCE_LAST_FILE_PATH,
                    Constants.INVALID_STRING);
            String curVersion = android.os.Build.VERSION.INCREMENTAL;
            if (id == lastId && (Utils.compareVersion(lastVersion, curVersion) == 1)) {
                showCountingDialog(lastFilePath);
            }
        } else if (action.equals(Constants.ACTION_UPDATE_INCREMENTAL_ALL)) {
            /*
             * MsgCenter pushed this task "0": incremental package requested
             * "1": full package requested
             */
            String incrementalAll = intent.getStringExtra(Constants.EXTRA_INCREMENTAL_ALL);
            log("Get ACTION_UPDATE_INCREMENTAL_ALL, extra=" + incrementalAll);
            if (incrementalAll.equals(Constants.EXTRA_ALL) || incrementalAll.equals(Constants.EXTRA_INCREMENTAL)) {
                mThreadPool.submit(new GetVersionInfoTask(Constants.SOURCE_CPE, incrementalAll));
            }
        } else if (action.equals(ConnectivityManager.CONNECTIVITY_ACTION)) {
            boolean needCheckUpdate = Utils.getBoolean(getApplicationContext(), Constants.PREFERENCE_BOOT_COMPLETED,
                    true);
            if (!needCheckUpdate) {
                log("Receive CONNECTIVITY_ACTION, but it's not the first time after booting, discard it!");
                return;
            }
            ConnectivityManager connectivityManager = (ConnectivityManager) UpdateService.this
                    .getSystemService(Context.CONNECTIVITY_SERVICE);
            if (connectivityManager == null) {
                loge("UpdateService: Fail to get ConnectivityManager!");
                return;
            }
            NetworkInfo[] networkInfos = connectivityManager.getAllNetworkInfo();
            for (NetworkInfo info : networkInfos) {
                if (info.getState() != NetworkInfo.State.CONNECTED) {
                    continue;
                }
                if (!Utils.isLastUpdateFailed()) {
                    log("Boot completed and network is OK, check for incremental package...");
                    mThreadPool.submit(new GetVersionInfoTask(Constants.SOURCE_BOOT, Constants.EXTRA_INCREMENTAL));
                } else {
                    log("Boot completed and network is OK, but last update failed, discard!");
                }
                // We don't want another checking again.
                Utils.putBoolean(getApplicationContext(), Constants.PREFERENCE_BOOT_COMPLETED, false);
                return;
            }
        }
    }

    private void deleteLastSnFile() {
        if (!Utils.isEmpty(mInfo.getLastSnId())) {
            String downloadDir = Utils.getSnDownloadPath();
            if (mSnManager.isDiskReady(downloadDir)) {
                log("deleteLastSnFile snId=" + mInfo.getLastSnId());
                mSnManager.removeTaskAndFile(mInfo.getLastSnId());
            } else {
                log("deleteLastSnFile: Disk " + downloadDir + " is not ready, delay deleting for 1 second.");
                mSnManager.addDiskPath(downloadDir);
                Message msg = mHandler.obtainMessage(Constants.MSG_DELAY_DELETE_SN_TASK, mInfo.getLastSnId());
                mHandler.sendMessage(msg);
            }

            mInfo.setLastSnId(Constants.INVALID_STRING);
        }
    }

    public void checkVersion(String incremental) {
        boolean isLastUpdateFailed = Utils.isLastUpdateFailed();
        log("Start checking version with " + incremental + ", isLastUpdateFailed=" + isLastUpdateFailed);
        if (isLastUpdateFailed) {
            mThreadPool.submit(new GetVersionInfoTask(Constants.SOURCE_ACTIVITY, Constants.EXTRA_ALL));
        } else {
            mThreadPool.submit(new GetVersionInfoTask(Constants.SOURCE_ACTIVITY, incremental));
        }
    }

    public void startUpdate() {
        if (mInfo == null) {
            loge("Get version info failed!");
            return;
        }

        String engine = SciflyStore.Global.getString(getContentResolver(), SciflyStore.Global.DOWNLOAD_ENGINE,
                Constants.DOWNLOAD_ENGINE_HTTP);
        if (!Utils.isEmpty(engine) && (!engine.equals(mInfo.getEngine()))) {
            log("Change download automatically ...");
            mHandler.obtainMessage(Constants.MSG_STORAGE_EXTERNAL_FULL).sendToTarget();
            SciflyStore.Global.putString(getContentResolver(), SciflyStore.Global.DOWNLOAD_ENGINE, mInfo.getEngine());
            if (null != mEngineChangeListener) {
                mEngineChangeListener.onEngineChange();
            }
        }

        log("Start update with engine " + mInfo.getEngine());
        if (Constants.DOWNLOAD_ENGINE_P2P.equalsIgnoreCase(mInfo.getEngine())) {
            // Sn+ download.
            String downloadDir = Utils.getSnDownloadPath();
            if (!Utils.isEmpty(downloadDir)) {
                // Delete last sn+ download file first.
                deleteLastSnFile();
                log("Create task under " + downloadDir);
                mSnManager.createDownloadTask(mInfo.getUrl(), downloadDir, downloadDir, new ResultListener() {
                    @Override
                    public void OnCreated(List<TaskInfoInternal> taskList) throws RemoteException {
                        super.OnCreated(taskList);
                        final TaskInfoInternal info = taskList.get(0);
                        if (info == null) {
                            loge("Create task failed, notify all...");
                            sendBroadcast(Constants.VERSION_STATUS_LAST);
                            return;
                        }
                        mSnManager.startDownload(info.getPlayURL());
                        mSnManager.setTaskListener(info.getPlayURL(), new TaskListener() {
                            @Override
                            public void OnComplete() throws RemoteException {
                                super.OnComplete();
                                log("startUpdate->completed...");
                                if (Utils.isEmpty(info.getDetail())) {
                                    TaskInfoInternal t = mSnManager.getTaskInfo(mInfo.getLastSnId());
                                    if (t != null && !Utils.isEmpty(t.getDetail())) {
                                        mInfo.setLastSnFilePath(t.getDetail());
                                        Utils.putString(getApplicationContext(), Constants.PREFERENCE_LAST_SN_FILE_PATH, t.getDetail());
                                        showCountingDialog(t.getDetail());
                                    } else {
                                        loge("ERROR Fail to get last sn download file path 1!!!");
                                    }
                                } else {
                                    showCountingDialog(info.getDetail());
                                }
                            }
                        }, true);
                        mInfo.setLastSnId(info.getPlayURL());
                        // Delete last Android download record.
                        if (mInfo.getLastId() != -1) {
                            mManager.removeTask(mInfo.getLastId());
                            File lastDownloadFile = new File(mInfo.getLastFilePath());
                            if (lastDownloadFile != null && lastDownloadFile.exists()) {
                                lastDownloadFile.delete();
                            }
                            mInfo.setLastId(-1);
                            mInfo.setLastFilePath(Constants.INVALID_STRING);
                        }
                        mInfo.setLastEngine(mInfo.getEngine());
                        mInfo.setLastVersion(mInfo.getVersion());
                        mInfo.setLastUds(mInfo.getUds());
                        mInfo.setLastTime(mInfo.getTime());
                        mInfo.setLastSize(mInfo.getSize());
                        mInfo.setLastMd(mInfo.getMd());
                        mInfo.setLastPackageType(mInfo.getPackageType());
                        SciflyStore.Global.putString(getContentResolver(), SciflyStore.Global.DOWNLOAD_ID,
                                info.getPlayURL());
                        Utils.updatePreference(getApplicationContext(), mInfo);
                        sendBroadcast(Constants.VERSION_STATUS_LAST);
                    }
                }, null);
                return;
            } else {
                loge("Fail to start update with engine P2P, try HTTP...");
                mInfo.setEngine(Constants.DOWNLOAD_ENGINE_HTTP);
                SciflyStore.Global.putString(getContentResolver(), SciflyStore.Global.DOWNLOAD_ENGINE,
                        Constants.DOWNLOAD_ENGINE_HTTP);
            }
        }
        // If Sn+ download failed, use Android download.
        final String downloadUrl = mInfo.getUrl();
        log("Start HTTP download url=" + downloadUrl);
        Request request = new Request(Uri.parse(downloadUrl));
        request.setMimeType("application/zip");
        request.setVisibleInDownloadsUi(false);
        request.setNotificationVisibility(Request.VISIBILITY_HIDDEN);

        // Determine file location.
        String fileName = downloadUrl.substring(downloadUrl.lastIndexOf("/") + 1);
        if (isCacheAvailable()) {
            mInfo.setLastFilePath("/cache/" + fileName);
            log("Start http download, file=" + mInfo.getLastFilePath());
            request.setDestinationToSystemCache();
        } else if (isExternalStorageAvailable()) {
            File downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
            mInfo.setLastFilePath(new File(downloadDir, fileName).getAbsolutePath());
            log("Start http download, file=" + mInfo.getLastFilePath());
            request.setDestinationInExternalPublicDir(Environment.DIRECTORY_DOWNLOADS, fileName);
        }
        EosDownloadTask task = new EosDownloadTask(request, null);
        long id = mManager.addTask(task);

        mInfo.setLastId(id);
        mInfo.setLastEngine(mInfo.getEngine());
        mInfo.setLastVersion(mInfo.getVersion());
        mInfo.setLastUds(mInfo.getUds());
        mInfo.setLastTime(mInfo.getTime());
        mInfo.setLastSize(mInfo.getSize());
        mInfo.setLastMd(mInfo.getMd());
        mInfo.setLastPackageType(mInfo.getPackageType());
        SciflyStore.Global.putString(getContentResolver(), SciflyStore.Global.DOWNLOAD_ID, Long.toString(id));
        Utils.updatePreference(getApplicationContext(), mInfo);
        sendBroadcast(Constants.VERSION_STATUS_LAST);
    }

    public void continueUpdate() {
        if (mInfo == null) {
            loge("continueUpdate->Get version info failed!");
            return;
        }
        if (Utils.isEmpty(mInfo.getLastSnId()) && mInfo.getLastId() == -1) {
            loge("continueUpdate->No last version record found!");
            return;
        }
        if (Constants.DOWNLOAD_ENGINE_P2P.equalsIgnoreCase(mInfo.getEngine())) {
            if (!Constants.DOWNLOAD_ENGINE_P2P.equalsIgnoreCase(mInfo.getLastEngine())) {
                // If last download engine is not Sn+, start update.
                log("Download engine may be changed, we need to start a new update with engine P2P");
                clear();
                startUpdate();
            } else {
                // Continue Sn+ download.
                String lastSnDownloadPath = Utils.getSnDownloadPath();
                log("continueUpdate->lastSnDownloadPath=" + lastSnDownloadPath);

                if (mSnManager.isDiskReady(lastSnDownloadPath)) {
                    final TaskInfoInternal info = mSnManager.getTaskInfo(mInfo.getLastSnId());
                    if (info == null) {
                        loge("continueUpdate->Fail to find sn task, clear version info, snId=" + mInfo.getLastSnId());
                        clear();
                        sendBroadcast(Constants.VERSION_STATUS_LAST);
                        return;
                    }
                    // Update last sn+ file path.
                    String lastSnFilePath = info.getDetail();
                    if (!Utils.isEmpty(lastSnFilePath)) {
                        mInfo.setLastSnFilePath(lastSnFilePath);
                        log("Continue update, last sn file path=" + lastSnFilePath);
                        Utils.putString(getApplicationContext(), Constants.PREFERENCE_LAST_SN_FILE_PATH, lastSnFilePath);
                    }
                    switch (info.getTaskState()) {
                        case Constants.SN_TASK_STATE_COMPLETE:
                            // Download completed, show counting dialog.
                            if (Utils.isEmpty(info.getDetail())) {
                                TaskInfoInternal t = mSnManager.getTaskInfo(mInfo.getLastSnId());
                                if (t != null && !Utils.isEmpty(t.getDetail())) {
                                    mInfo.setLastSnFilePath(t.getDetail());
                                    Utils.putString(getApplicationContext(), Constants.PREFERENCE_LAST_SN_FILE_PATH, t.getDetail());
                                    showCountingDialog(t.getDetail());
                                } else {
                                    loge("ERROR Fail to get last sn download file path 2!!!");
                                }
                            } else {
                                showCountingDialog(info.getDetail());
                            }
                            break;
                        case Constants.SN_TASK_STATE_ERROR:
                        case Constants.SN_TASK_STATE_PAUSED:
                            // Continue download.
                            mSnManager.startDownload(mInfo.getLastSnId());
                        case Constants.SN_TASK_STATE_READY:
                        case Constants.SN_TASK_STATE_RUNNING:
                            // Sn+ is downloading now.
                            break;
                    }
                    log("continueUpdate->Listen on task " + mInfo.getLastSnId());
                    mSnManager.setTaskListener(mInfo.getLastSnId(), new TaskListener() {
                        @Override
                        public void OnComplete() throws RemoteException {
                            super.OnComplete();
                            log("continueUpdate->completed...");
                            if (Utils.isEmpty(info.getDetail())) {
                                TaskInfoInternal t = mSnManager.getTaskInfo(mInfo.getLastSnId());
                                if (t != null && !Utils.isEmpty(t.getDetail())) {
                                    mInfo.setLastSnFilePath(t.getDetail());
                                    Utils.putString(getApplicationContext(), Constants.PREFERENCE_LAST_SN_FILE_PATH, t.getDetail());
                                    showCountingDialog(t.getDetail());
                                } else {
                                    loge("ERROR Fail to get last sn download file path 3!!!");
                                }
                            } else {
                                showCountingDialog(info.getDetail());
                            }
                        }
                    }, true);
                    sendBroadcast(Constants.VERSION_STATUS_LAST);
                } else {
                    loge("continueUpdate->Sn+ disk " + lastSnDownloadPath + " not ready, download 1 second later!");
                    mSnManager.addDiskPath(lastSnDownloadPath);
                    mHandler.sendEmptyMessageDelayed(Constants.MSG_DELAY_DOWNLOAD, 1000);
                }
            }
        } else if (Constants.DOWNLOAD_ENGINE_HTTP.equalsIgnoreCase(mInfo.getEngine())) {
            if (!Constants.DOWNLOAD_ENGINE_HTTP.equalsIgnoreCase(mInfo.getLastEngine())) {
                // Last download engine is not Android, start update.
                log("Download engine may be changed, we need to start a new update with engine HTTP");
                clear();
                startUpdate();
            } else {
                if (!mManager.restartTask(mInfo.getLastId(), null)) {
                    loge("continueUpdate->restartTask failed, clear version info, id=" + mInfo.getLastId());
                    clear();
                }
                sendBroadcast(Constants.VERSION_STATUS_LAST);
            }
        }
    }

    public VersionInfor getVersionInfo() {
        return mInfo;
    }

    /**
     * Showing a 30 seconds counting down dialog after download completed
     * 
     * @param filePath Updating package file path.
     */
    public void showCountingDialog(String filePath) {
        log("Showing counting Dialog now, path=" + filePath);
        Message msg = mHandler.obtainMessage(Constants.MSG_SHOW_COUNT_DIALOG);
        msg.obj = filePath;
        mHandler.sendMessage(msg);
    }

    @Override
    public void onCreate() {
        log("onCreate...");
        super.onCreate();
        mInfo = new VersionInfor();
        mThreadPool = Executors.newFixedThreadPool(3);
        mManager = new EosDownloadManager(getApplicationContext());
        mSnManager = new StreamNetManager(getApplicationContext());
    }

    @Override
    public void onRebind(Intent intent) {
        log("onRebind...");
        super.onRebind(intent);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        log("onStartCommand...");
        handlerServiceStarted(intent, flags, startId);
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent arg0) {
        log("onBind...");
        return mBinder;
    }

    @Override
    public boolean onUnbind(Intent intent) {
        log("onUnbind...");
        return super.onUnbind(intent);
    }

    @Override
    public void onDestroy() {
        log("onDestroy...");
        mThreadPool.shutdown();
        super.onDestroy();
    }

    public class UpdateBinder extends Binder {
        public UpdateService getService() {
            return UpdateService.this;
        }
    }

    private UpdateBinder mBinder = new UpdateBinder();

    private void log(String msg) {
        if (Constants.DBG) {
            Log.d(Constants.TAG, "Service: " + msg);
        }
    }

    private void loge(String msg) {
        Log.e(Constants.TAG, "Service: " + msg);
    }

}
