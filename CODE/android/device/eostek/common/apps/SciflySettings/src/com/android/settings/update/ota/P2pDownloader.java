
package com.android.settings.update.ota;

import java.io.File;
import java.util.List;

import android.content.Context;
import android.os.Handler;
import android.os.RemoteException;
import android.text.TextUtils;

import com.eostek.streamnetplusservice.service.ResultListener;
import com.eostek.streamnetplusservice.service.StreamNetManager;
import com.eostek.streamnetplusservice.service.TaskInfoInternal;
import com.eostek.streamnetplusservice.service.TaskListener;

public class P2pDownloader extends Downloader {
    private static Logger sLog = new Logger(P2pDownloader.class);

    private StreamNetManager sDownloadManager;

    private String mError;

    private static P2pDownloader INSTANCE;

    private Handler mUpdateHandler;

    private boolean mNeedCheckPath = true;

    public static P2pDownloader getInstance(Context context) {
        if (null == INSTANCE) {
            sContext = context;
            INSTANCE = new P2pDownloader(context);
        }
        return INSTANCE;
    }

    private P2pDownloader(Context context) {
        if (sDownloadManager == null) {
            sDownloadManager = new StreamNetManager(context);
        }
        sPreferenceHelper = PreferenceHelper.getInstance(context);
        mUpdateHandler = new Handler(context.getMainLooper());
    }

    private Runnable mRemoveTask = new Runnable() {
        public void run() {
            String taskId = sPreferenceHelper.getP2pDownloadId();
            String downloadDir = Utils.getP2pDownloadDir();
            if (sDownloadManager.isDiskReady(downloadDir)) {
                sDownloadManager.removeTaskAndFile(taskId);
            } else {
                sDownloadManager.addDiskPath(downloadDir);
                mUpdateHandler.postDelayed(this, 1000);
            }
        };
    };

    private void processTaskState(int state, String taskId) {
        switch (state) {
            case SN_TASK_STATE_COMPLETE:
                TaskInfoInternal t = sDownloadManager.getTaskInfo(taskId);
                onDownloadFinished(t.getDetail(), sPreferenceHelper.getPackageMd5());
                break;
            case SN_TASK_STATE_ERROR:
                onDownloadError("Error to start downloads");
            case SN_TASK_STATE_PAUSED:
                sDownloadManager.startDownload(taskId);
            case SN_TASK_STATE_READY:
                sLog.debug("processTaskState --> SN_TASK_STATE_READY");
            case SN_TASK_STATE_RUNNING:
                sLog.debug("processTaskState --> SN_TASK_STATE_RUNNING");
                break;
        }
    }

    @Override
    public boolean restartDownloading() {

        sLog.debug("restartDownloading");

        final String taskId = sPreferenceHelper.getP2pDownloadId();

        final TaskInfoInternal info = sDownloadManager.getTaskInfo(taskId);
        if (info == null) {
            return false;
        }
        // Update last sn+ file path.
        processTaskState(info.getTaskState(), taskId);

        sDownloadManager.setTaskListener(taskId, new TaskListener() {
            @Override
            public void OnInfo(int progress, int speed) throws RemoteException {
                super.OnInfo(progress, speed);
                onDownloadProgress(progress);
            }

            @Override
            public void OnComplete() throws RemoteException {
                super.OnComplete();
                TaskInfoInternal t = sDownloadManager.getTaskInfo(taskId);
                onDownloadFinished(t.getDetail(), sPreferenceHelper.getPackageMd5());
            }

            @Override
            public void OnTaskChanged(int state) throws RemoteException {
                super.OnTaskChanged(state);
                processTaskState(state, taskId);
            }

            @Override
            public void OnError(int code, String detail) throws RemoteException {
                super.OnError(code, detail);
                mError = String.format("SNError:%d, Detail:%s", code, detail);
                if (code == 12) { // TASK_ERRORCODE_NETWORK_DISCONNECT = 12
                    onDownloadPaused("Network Exception");
                } else {
                    onDownloadError(mError);
                }
            }
        }, true);

        return true;

    }

    private void removeDownload(String taskId) {
        sLog.debug("removeDownload:: id:" + taskId);

        sDownloadManager.setTaskListener(taskId, null, false);
        String downloadPath = sPreferenceHelper.getDownloadPath();
        if (!TextUtils.isEmpty(downloadPath)) {
            sLog.info("stop p2p download task ...");
            sDownloadManager.stopDownload(taskId);
        } else {
            mNeedCheckPath = true;
        }

        sDownloadingRom = false;
        // Backup SN+ taskId in order to delete when download next time
        sPreferenceHelper.setP2pBackupId(taskId);
        sPreferenceHelper.setP2pBackupPath(downloadPath);
        sPreferenceHelper.setDownloadId(null);

    }

    public void hardRemoveDownload() {
        String backupId = sPreferenceHelper.getP2pBackupId();
        sLog.debug("hardRemoveDownload:: id:" + backupId);

        if (TextUtils.isEmpty(backupId)) {
            return;
        }

        String downloadPath = sPreferenceHelper.getDownloadPath();
        String downloadDir = Utils.getP2pDownloadDir();
        if (!TextUtils.isEmpty(downloadPath)) {
            if (sDownloadManager.isDiskReady(downloadDir)) {
                sLog.info("remove p2p download task and file ...");
                sDownloadManager.removeTaskAndFile(backupId);
            } else {
                sLog.error("remove p2p task failed ...");
                mUpdateHandler.postDelayed(mRemoveTask, 1000);
            }
        } else {
            mNeedCheckPath = true;
        }

        sPreferenceHelper.setP2pBackupId(null);
        sPreferenceHelper.setP2pBackupPath(null);
    }

    @Override
    public boolean checkIfDownloading(String md5) {
        sLog.debug("checkIfDownloading");
        String romMd5 = sPreferenceHelper.getPackageMd5();
        String taskId = sPreferenceHelper.getP2pDownloadId();

        if (!TextUtils.isEmpty(romMd5) && romMd5.equals(md5)) {

            final TaskInfoInternal taskInfo = sDownloadManager.getTaskInfo(taskId);
            if (taskInfo == null) {
                sLog.debug("Fail to get Sn+ download task info");
                return false;
            }

            int status = taskInfo.getTaskState();
            if (SN_TASK_STATE_COMPLETE != status && SN_TASK_STATE_ERROR != status) {
                return true;
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

            String filePath = sPreferenceHelper.getDownloadPath();
            File file = new File(filePath);
            if (sPreferenceHelper.isDownloadFinished()) {
                ret = file.exists();
            } else {
                String taskId = sPreferenceHelper.getP2pDownloadId();
                final TaskInfoInternal taskInfo = sDownloadManager.getTaskInfo(taskId);
                if (taskInfo != null) {
                    int status = taskInfo.getTaskState();
                    if (SN_TASK_STATE_COMPLETE == status && file.exists()) {
                        sPreferenceHelper.setDownloadFinished(true);
                        ret = true;
                    }
                }
            }

        }

        if (!ret)
            sPreferenceHelper.setDownloadFinished(false);

        return ret;
    }

    @Override
    protected void realDownloadFile(String url) {
        sLog.debug("realDownloadFile:: url:" + url);

        long requiredSize = sPreferenceHelper.getDownloadSize();
        String downloadDir = getDownloadDir(requiredSize);
        if (TextUtils.isEmpty(downloadDir)) {
            onDownloadError("No enough space for downloading.");
            if (mP2pTaskListener != null) {
                mP2pTaskListener.onTaskError("No enough space for downloading.");
            }
            return;
        }
        File file = new File(downloadDir);
        if (!file.exists()) {
            file.mkdirs();
        }
        ResultListener listener = new ResultListener() {
            @Override
            public void OnCreated(List<TaskInfoInternal> taskList) throws RemoteException {
                super.OnCreated(taskList);

                final TaskInfoInternal info = taskList.get(0);

                if (info == null) {
                    sLog.debug("Create task failed, notify all...");
                    if (mP2pTaskListener != null) {
                        mP2pTaskListener.onTaskError("Create task failed, notify all...");
                    }
                    return;
                }

                final String taskId = info.getPlayURL();

                if (TextUtils.isEmpty(taskId)) {
                    onDownloadError("taskId is null");
                    if (mP2pTaskListener != null) {
                        mP2pTaskListener.onTaskError("taskId is null");
                    }
                    return;
                }

                // When download a new version package , then delete old package
                String backupId = sPreferenceHelper.getP2pBackupId();
                if (!taskId.equals(backupId)) {
                    hardRemoveDownload();
                }

                sPreferenceHelper.setDownloadId(taskId);
                sDownloadingRom = true;

                sDownloadManager.startDownload(taskId);

                if (mP2pTaskListener != null) {
                    mP2pTaskListener.onTaskStarted(taskId);
                }
                mP2pTaskListener = null;

                sDownloadManager.setTaskListener(taskId, new TaskListener() {
                    @Override
                    public void OnInfo(int progress, int speed) throws RemoteException {
                        super.OnInfo(progress, speed);

                        onDownloadProgress(progress);

                        // record download path for further use.
                        if (mNeedCheckPath) {
                            TaskInfoInternal t = sDownloadManager.getTaskInfo(taskId);
                            if (t != null && !TextUtils.isEmpty(t.getDetail())) {
                                sPreferenceHelper.setDownloadPath(t.getDetail());
                                mNeedCheckPath = false;
                            }
                        }
                    }

                    @Override
                    public void OnComplete() throws RemoteException {
                        super.OnComplete();
                        sLog.debug("StreamNetPlus download completed ...");

                        TaskInfoInternal t = sDownloadManager.getTaskInfo(taskId);

                        onDownloadFinished(t.getDetail(), sPreferenceHelper.getPackageMd5());
                    }

                    @Override
                    public void OnTaskChanged(int state) throws RemoteException {
                        super.OnTaskChanged(state);
                        sLog.debug("OnTaskChanged --> state:" + state);
                        switch (state) {
                            case SN_TASK_STATE_COMPLETE:
                                sLog.debug("StreamNetPlus download finished.");
                                TaskInfoInternal t = sDownloadManager.getTaskInfo(taskId);
                                onDownloadFinished(t.getDetail(), sPreferenceHelper.getPackageMd5());
                                break;
                            case SN_TASK_STATE_ERROR:
                            case SN_TASK_STATE_PAUSED:
                            case SN_TASK_STATE_READY:
                            case SN_TASK_STATE_RUNNING:
                                break;
                        }
                    }

                    @Override
                    public void OnError(int code, String detail) throws RemoteException {
                        super.OnError(code, detail);
                        mError = detail;
                        if (code == 12) { // TASK_ERRORCODE_NETWORK_DISCONNECT =
                                          // 12
                            onDownloadPaused("Network Exception");
                        } else {
                            onDownloadError(mError);
                        }

                    }
                }, true);

            }
        };

        sDownloadManager.createDownloadTask(url, file.getAbsolutePath(), file.getAbsolutePath(), listener, null);
    }

    @Override
    public void clearDownload() {
        sLog.debug("clearDownload ");
        String romId = sPreferenceHelper.getP2pDownloadId();
        if (!TextUtils.isEmpty(romId)) {
            removeDownload(romId);
        }
        sPreferenceHelper.setDownloadFinished(false);
    }

    @Override
    public String getDownloadDir(long requiredSize) {
        long availableSize = 0;
        String downloadDir = Utils.getP2pDownloadDir();
        if(TextUtils.isEmpty(downloadDir)) { // Fix mantis 0041062
            sLog.error("Get P2p Download Dir Error !");
            return null;
        }
        availableSize = Utils.getAvailableSize(downloadDir) / 1024;
        // now we need think the sn+ old package size , because we will delete
        // it when a new download started.
        String path = sPreferenceHelper.getP2pBackupPath();
        if (!TextUtils.isEmpty(path)) {
            File file = new File(path);
            availableSize += file.length();
        }

        if (requiredSize < availableSize) {
            return Utils.getP2pDownloadDir();
        } else {
            return null;
        }
    }

    // Special add for track cloud push status begin
    private P2pTaskListener mP2pTaskListener = null;

    public void setP2pTaskListener(P2pTaskListener listener) {
        mP2pTaskListener = listener;
    }

    public static interface P2pTaskListener {
        public abstract void onTaskStarted(String taskId);

        public abstract void onTaskError(String reason);
    }
    // Special add for track cloud push status end

}
