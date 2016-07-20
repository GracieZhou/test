package com.eostek.scifly.devicemanager.service;

import android.app.DownloadManager;
import android.app.IPackageInstallListener;
import android.app.PackageManagerExtra;
import android.app.Service;
import android.app.DownloadManager.Request;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Binder;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;

import com.eostek.scifly.devicemanager.manage.appmanagement.AppManagementThread;
import com.eostek.scifly.devicemanager.manage.appmanagement.AppManagementThread.AppUpdateInfo;
import com.eostek.scifly.devicemanager.manage.appmanagement.AppManagementThread.OnThreadListener;
import com.eostek.scifly.devicemanager.receiver.MessageListenerBase;
import com.eostek.scifly.devicemanager.receiver.MessageReceiver;
import com.eostek.scifly.devicemanager.util.Constants;
import com.eostek.scifly.devicemanager.util.Debug;

import java.io.File;
import java.util.HashMap;

public class DownloadService extends Service{

    private static final String TAG = DownloadService.class.getSimpleName();
    
    public static final int DOWNLOAD_STATUS_RUNNING = 0;
    public static final int DOWNLOAD_STATUS_OVER    = 1;
    public static final int DOWNLOAD_STATUS_PAUSE   = 2;
    public static final int DOWNLOAD_STATUS_FAILED  = 3;
    public static final int DOWNLOAD_STATUS_PENDING = 4;
    
    private DownloadManager mDownloadManager;
    private Handler mServiceHandler = new Handler();
    private BroadcastReceiverListener mBroadcastListener;
    private DownlaodServiceBinder mDownlaodServiceBinder = new DownlaodServiceBinder();
    private OnServiceListener mOnServiceListener;
    
    public interface OnServiceListener {
        public void onToastDownloadStart(String packname);
        public void onToastDownloadRunning(String packname);
        public void onToastInstallSuccess(String packname);
        public void onToastInstallFailure(String packname);
        public void onUpdateProcess(String packname, int status, int percent);
    }
    
    public void registerListener(OnServiceListener listener) {
        if(listener != null) {
            mOnServiceListener = listener;
        }
    }
    
    public void unregisterListener() {
        if(mOnServiceListener != null) {
            mOnServiceListener = null;
        }
    }
    
    @Override
    public void onCreate() {
        // TODO Auto-generated method stub
        Debug.d(TAG, "onCreate");
        super.onCreate();
        mDownloadManager = (DownloadManager)getSystemService(Context.DOWNLOAD_SERVICE);
        mBroadcastListener = new BroadcastReceiverListener();
        MessageReceiver.addMessageListener(mBroadcastListener);
        mServiceHandler.post(mUpdateProgress);
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        Debug.d(TAG, "onStartCommand:startId = " + startId);
        if(intent != null) {
            int autoUpdate = intent.getIntExtra(Constants.MSG_AUTO_UPDATE_TYPE, 0);
            if(autoUpdate == Constants.MSG_AUTO_UPDATE_VALUE ) {
                queryUpdate();
            }
        }
        return super.onStartCommand(intent, flags, startId);
    }
    
    private void queryUpdate() {
        AppManagementThread mThread = new AppManagementThread(this);
        mThread.setOnThreadListener(new OnThreadListener() {
            @Override
            public void onSyncInfo(AppUpdateInfo info) {
                
            }

            @Override
            public void onQueryInfoSuccess() {
                for(AppUpdateInfo info : AppManagementThread.getUpdateList()) {
                    startDownloadItem(info.getmUrlPath(), info.getmPkgName());
                }
            }

            @Override
            public void onQueryInfoFailure() {

            }
        });
        mThread.start();
    }
    
    @Override
    public IBinder onBind(Intent arg0) {
        // TODO Auto-generated method stub
        Debug.d(TAG, "onBind");
        return mDownlaodServiceBinder;
    }
    
    @Override
    public boolean onUnbind(Intent intent) {
        // TODO Auto-generated method stub
        Debug.d(TAG, "onUnbind");
        return super.onUnbind(intent);
    }
    
    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        Debug.d(TAG, "onDestroy");
        super.onDestroy();
        MessageReceiver.removeMessageListener(mBroadcastListener);
    }
    
    public class DownlaodServiceBinder extends Binder {
        public DownloadService getService() {
            Debug.d(TAG, "getService");
            return DownloadService.this;
        }
    }
    
    private boolean isDownloadFileExist(String packname){
        String path = Environment.getExternalStoragePublicDirectory(
                Constants.DOWNLOAD_DIRECTORY).getPath() + "/" + packname + ".apk";
        File file = new File(path);
        return file.exists();
    }
    
    private void deletePriviousDownloadFile(String packname){
        String path = Environment.getExternalStoragePublicDirectory(
                Constants.DOWNLOAD_DIRECTORY).getPath() + "/" + packname + ".apk";
        File file = new File(path);
        if(file.exists())
        {
            Debug.d(TAG, "delete " + path + " success ");
            file.delete();
        }
    }
    
    private int[] queryDownloadStatus(long id){
        long totalBytes = -1;
        long downloadedBytes = -1;
        int downloadPercent = -1;
        
        DownloadManager.Query q = new DownloadManager.Query();
        q.setFilterById(id);

        Cursor cursor = mDownloadManager.query(q);
        int status;

        if (cursor == null || !cursor.moveToFirst()) {
            status = DownloadManager.STATUS_FAILED;
        } else {
            status = cursor.getInt(cursor.getColumnIndex(DownloadManager.COLUMN_STATUS));
        }
        
        switch (status) {
            case DownloadManager.STATUS_PAUSED:
            case DownloadManager.STATUS_RUNNING:
                downloadedBytes = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
                totalBytes = cursor.getLong(cursor.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
                break;
            case DownloadManager.STATUS_FAILED:
                break;
        }

        if (cursor != null) {
            cursor.close();
        }
        downloadPercent = (int)((downloadedBytes * 100) / totalBytes);
        if(downloadedBytes <= 0)
        {
            downloadPercent = 0;
        }
        
        return new int[] {status, downloadPercent};
    }
    
    public void startDownloadItem(String url, String packname) {
        long downloadId = -1;

        if(DownloadConfig.containsKey(DownloadService.this, packname)){
            downloadId = DownloadConfig.get(DownloadService.this, packname, -1);
        }
        
        if(downloadId != -1) {
            int[] status = queryDownloadStatus(downloadId);
            
            if(status[0] == DownloadManager.STATUS_SUCCESSFUL) {
                
                Debug.d(TAG, "[" + packname + "] download success already");
                if(DownloadConfig.containsKey(DownloadService.this, packname)){
                    DownloadConfig.remove(DownloadService.this, packname);
                }

            } else if(status[0] == DownloadManager.STATUS_FAILED ) {
                
                Debug.d(TAG, "[" + packname + "] download restart");
                if(DownloadConfig.containsKey(DownloadService.this, packname)){
                    mDownloadManager.restartDownload(downloadId);
                }
                return;
                
            } else if(status[0] == DownloadManager.STATUS_PAUSED || status[0] == DownloadManager.STATUS_PENDING || status[0] == DownloadManager.STATUS_RUNNING ) {
                
                Debug.d(TAG, "[" + packname + "] download running");
                if(mOnServiceListener != null) {
                    mOnServiceListener.onToastDownloadRunning(packname);
                }
                return;
            }
            else {
                return;
            }
        }
        
        deletePriviousDownloadFile(packname);
        
        Request request = new Request(Uri.parse(url));
        request.setMimeType("application/vnd.android.package-archive");
        request.setDestinationInExternalPublicDir(Constants.DOWNLOAD_DIRECTORY, packname + ".apk");
        request.setVisibleInDownloadsUi(false);
        
        downloadId = mDownloadManager.enqueue(request);
        
        DownloadConfig.put(DownloadService.this, packname, downloadId);
        if(mOnServiceListener != null) {
            mOnServiceListener.onToastDownloadStart(packname);
        }
        Debug.d(TAG, "[" + packname + "] start to download" );
    }
    
    private void installDownloadItem(final String filepath) {
        Debug.d(TAG, "install filepath:[" + filepath + "]");
        PackageManagerExtra managerExtra = PackageManagerExtra.getInstance();
        Uri packageURI = Uri.fromFile(new File(filepath));
        managerExtra.installPackage(DownloadService.this, packageURI, new IPackageInstallListener() {
            @Override
            public void packageInstalled(String packname, int ret) {
                Debug.d(TAG, "install packname:[" + packname + "] ret:[" + ret + "]");
                if(ret == 1) {
                    if(mOnServiceListener != null)
                        mOnServiceListener.onToastInstallSuccess(packname);
                } else {
                    if(mOnServiceListener != null)
                        mOnServiceListener.onToastInstallFailure(packname);
                }
                if(DownloadConfig.containsKey(DownloadService.this, packname)){
                    DownloadConfig.remove(DownloadService.this, packname);
                }
                deletePriviousDownloadFile(packname);
                
                if(mOnServiceListener != null)
                    mOnServiceListener.onUpdateProcess(packname, DOWNLOAD_STATUS_OVER, 0);
            }
        });
    }
    
    private Runnable mUpdateProgress = new Runnable() {
        @Override
        public void run() {
            HashMap<String, Long> cloneMap = (HashMap)DownloadConfig.getAll(DownloadService.this);
            for(String packname : cloneMap.keySet()) {
                if(cloneMap.get(packname) > 0) {
                    int[] status = queryDownloadStatus(cloneMap.get(packname));
                    switch (status[0]) {
                    case DownloadManager.STATUS_PENDING:
                        if(mOnServiceListener != null)
                            mOnServiceListener.onUpdateProcess(packname, DOWNLOAD_STATUS_PENDING, status[1]);
                        break;
                    case DownloadManager.STATUS_RUNNING:
                        if(mOnServiceListener != null)
                            mOnServiceListener.onUpdateProcess(packname, DOWNLOAD_STATUS_RUNNING, status[1]);
                        break;
                    case DownloadManager.STATUS_PAUSED:
                        if(mOnServiceListener != null)
                            mOnServiceListener.onUpdateProcess(packname, DOWNLOAD_STATUS_PAUSE, status[1]);
                        break;                  
                    case DownloadManager.STATUS_FAILED:
                        if(DownloadConfig.containsKey(DownloadService.this, packname)){
                            if(!isDownloadFileExist(packname)) {
                                DownloadConfig.remove(DownloadService.this, packname);
                                if(mOnServiceListener != null)
                                    mOnServiceListener.onUpdateProcess(packname, DOWNLOAD_STATUS_OVER, 0);
                            } else {
                                if(mOnServiceListener != null)
                                    mOnServiceListener.onUpdateProcess(packname, DOWNLOAD_STATUS_FAILED, status[1]);
                            }
                        }
                        break;  
                    default:
                        break;
                    }
                }
            }

            mServiceHandler.postDelayed(this, 1000);
        }
    };
    
    private class BroadcastReceiverListener extends MessageListenerBase {
        @Override
        public void onDownloadComplete(long downloadId) {
            super.onDownloadComplete(downloadId);
            
            String filepath = null;
            boolean install = false;
            
            //find packname in info list
            HashMap<String, Long> cloneMap = (HashMap)DownloadConfig.getAll(DownloadService.this);
            for(String packname : cloneMap.keySet()) {
                if(downloadId == cloneMap.get(packname)) {
                    install = true;
                    Debug.d(TAG, "download packname:[" + packname + "]");
                    break;
                }
            }
            
            if(install == true) {
                Cursor cursor = mDownloadManager.query(new DownloadManager.Query().setFilterById(downloadId));
                if(cursor != null && cursor.getCount() > 0){ 
                    if(cursor.moveToFirst()) {
                        filepath = cursor.getString(cursor.getColumnIndex(DownloadManager.COLUMN_LOCAL_FILENAME));
                    }
                    cursor.close();
                    Debug.d(TAG, "download filepath:[" + filepath + "]");
                    if(filepath != null)
                        installDownloadItem(filepath);
                } 
            }
        }
    }

}
