
package com.eostek.streamnetplusservice.service;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.os.IBinder;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;

public class StreamNetManager {
    private static final String TAG = "snm";

    public static final String ACTION_STREAM_NET_SERVICE = "com.eostek.streamnetplusservice.service.StreamNetService";

    private IStreamNetPlusService mService = null;

    private Context mContext;

    public StreamNetManager(Context context) {
        mContext = context;
        init();
    }

    private void init() {
        mContext.bindService(new Intent(ACTION_STREAM_NET_SERVICE), conn, Context.BIND_AUTO_CREATE);
    }

    private ServiceConnection conn = new ServiceConnection() {
        @Override
        public void onServiceDisconnected(ComponentName name) {
            Log.e(TAG, "ERROR onServiceDisconnected: " + name);
        }

        @Override
        public void onServiceConnected(ComponentName name, IBinder service) {
            Log.d(TAG, "StreamNetPlusService connected...");
            mService = IStreamNetPlusService.Stub.asInterface(service);
        }
    };

    public void createDownloadTask(String url, String resumePath, String storagePath, IResultListener listener,
            Map<String, String> params) {
        Log.d(TAG, "create download task");
        if (mService == null) {
            Log.d(TAG, "mService !== null");
            Log.e(TAG, "ERROR failed to get StreamNetPlusService!");
            init();
            return;
        }
        Log.d(TAG, "mService != null");
        if (params == null) {
            params = new HashMap<String, String>();
        }
        if (!params.containsKey("SciflySourceCode")) {
            params.put("SciflySourceCode", "system");
        }
        MyMap extra = new MyMap(params);
        try {
            Log.d(TAG, "mService begin to create download task");
            mService.createDownloadTask(url, resumePath, storagePath, 0, listener, extra);
        } catch (RemoteException e) {
            Log.d(TAG, "mService create download task exception");
            e.printStackTrace();
            init();
        }
    }

    public TaskInfoInternal getTaskInfo(String taskId) {
        if (mService == null) {
            Log.e(TAG, "ERROR failed to get StreamNetPlusService!");
            init();
            return null;
        }
        if (!TextUtils.isEmpty(taskId)) {
            try {
                return mService.getTaskInfo(taskId);
            } catch (RemoteException e) {
                e.printStackTrace();
                init();
            }
        }
        return null;
    }

    public boolean addDiskPath(String path) {
        if (mService == null) {
            Log.e(TAG, "ERROR failed to get StreamNetPlusService!");
            init();
            return false;
        }
        try {
            return mService.addDiskPath(path);
        } catch (RemoteException e) {
            e.printStackTrace();
            init();
        }
        return false;
    }

    public boolean isDiskReady(String path) {
        if (mService == null) {
            Log.e(TAG, "ERROR failed to get StreamNetPlusService!");
            init();
            return false;
        }
        try {
            return mService.IsDiskReady(path);
        } catch (RemoteException e) {
            e.printStackTrace();
            init();
        }
        return false;
    }

    public List<String> getDownloadList() {
        if (mService == null) {
            Log.e(TAG, "ERROR failed to get StreamNetPlusService!");
            init();
            return null;
        }
        try {
            return mService.getDownloadTaskList();
        } catch (RemoteException e) {
            e.printStackTrace();
            init();
        }
        return null;
    }

    public boolean removeDiskPath(String path) {
        if (mService == null) {
            Log.e(TAG, "ERROR failed to get StreamNetPlusService!");
            init();
            return false;
        }
        try {
            return mService.removeDiskPath(path);
        } catch (RemoteException e) {
            e.printStackTrace();
            init();
        }
        return false;
    }

    public void startDownload(String taskId) {
        if (mService == null) {
            Log.e(TAG, "ERROR failed to get StreamNetPlusService!");
            init();
            return;
        }
        try {
            mService.startDownload(taskId);
        } catch (RemoteException e) {
            e.printStackTrace();
            init();
        }
    }

    public void stopDownload(String taskId) {
        if (mService == null) {
            Log.e(TAG, "ERROR failed to get StreamNetPlusService!");
            init();
            return;
        }
        try {
            mService.stopDownload(taskId);
        } catch (RemoteException e) {
            e.printStackTrace();
            init();
        }
    }

    public void removeTask(String taskId) {
        if (mService == null) {
            Log.e(TAG, "ERROR failed to get StreamNetPlusService!");
            init();
            return;
        }
        try {
            mService.removeTask(taskId);
        } catch (RemoteException e) {
            e.printStackTrace();
            init();
        }
    }

    public void removeTaskAndFile(String taskId) {
        if (mService == null) {
            Log.e(TAG, "ERROR failed to get StreamNetPlusService!");
            init();
            return;
        }
        try {
            mService.removeTaskAndFile(taskId);
        } catch (RemoteException e) {
            e.printStackTrace();
            init();
        }
    }

    public boolean setTaskListener(String taskId, ITaskListener listener, boolean listen) {
        if (mService == null) {
            Log.e(TAG, "ERROR failed to get StreamNetPlusService!");
            init();
            return false;
        }
        try {
            return mService.setTaskListener(taskId, listener, listen);
        } catch (RemoteException e) {
            e.printStackTrace();
            init();
        }
        return false;
    }

    public void setEventListener(IEventListener listener, boolean listen) {
        if (mService == null) {
            Log.e(TAG, "ERROR failed to get StreamNetPlusService!");
            init();
            return;
        }
        try {
            mService.setEventListener(listener, listen);
        } catch (RemoteException e) {
            e.printStackTrace();
            init();
        }
    }

    public int getDownloadSpeed(String taskId) {
        if (mService == null) {
            Log.e(TAG, "ERROR failed to get StreamNetPlusService!");
            init();
            return -1;
        }
        try {
            return mService.getDownloadSpeed();
        } catch (RemoteException e) {
            e.printStackTrace();
            init();
        }
        return -1;
    }

}
