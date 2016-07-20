
package com.eostek.isynergy.setmeup.config.external;

import scifly.device.Device;
import android.content.Context;
import android.os.Handler;
import android.os.HandlerThread;
import android.util.Log;

import com.eostek.isynergy.setmeup.common.Constants;
import com.eostek.isynergy.setmeup.common.TimeoutThread;

/**
 * 通过外部接口获取系统名称
 * 
 * @author nickyang
 */
public class DeviceNameService {
    private final String TAG = "External_DeviceNameService";

    private Context context;

    private String deviceName = null;

    private Object objLock = new Object();

    private HandlerThread thread;

    private Handler handler;

    private SetDeviceNameTask task;

    private boolean isTimeout = false;

    protected DeviceNameService(Context context) {
        this.context = context;

        thread = new HandlerThread("DeviceNameService_thread");
        thread.start();

        handler = new Handler(thread.getLooper());
    }

    /**
     * @return
     */
    public String getDeviceName() {
        Log.d(TAG, "current device name is " + ((deviceName == null || deviceName.length() == 0) ? "" : deviceName));
        /*
         * if(this.deviceName == null || this.deviceName.length() == 0) {
         */
        GetDevNameTask task = new GetDevNameTask();
        TimeoutThread thread = new TimeoutThread(2000, task);
        thread.start();

        synchronized (objLock) {
            Log.d(TAG, "waitting for external interface return...");
            try {
                isTimeout = false;
                objLock.wait(3000);
                isTimeout = true;
            } catch (InterruptedException e) {
                deviceName = null;
            }
        }

        if (deviceName == null || deviceName.length() == 0) {
            deviceName = Constants.DEFAULT_DEV_NAME;
            Log.d(TAG, "can't get dev name from external interface " + deviceName);
        } else {
            Log.d(TAG, "get dev name from external interface " + deviceName);
        }
        // }

        Log.d(TAG, "return  device name is " + ((deviceName == null || deviceName.length() == 0) ? "" : deviceName));
        return this.deviceName;
    }

    class GetDevNameTask implements Runnable {
        @Override
        public void run() {
            try {
                Thread.sleep(10);
                Log.d(TAG, "current time " + System.currentTimeMillis());
                String temp = Device.getDeviceName(context);
                Log.d(TAG, "current time " + System.currentTimeMillis());
                if (!isTimeout) {
                    deviceName = temp;
                }
            } catch (InterruptedException ex) {
                Log.d(TAG, "Thread that is time out is interrupted...");
            }

            synchronized (objLock) {
                Log.d(TAG, "notify dev name ...");

                objLock.notifyAll();
            }
        }
    }

    /**
     * 设置设备系统名称 1、upnp协议设备名称 2、硬件设备名称（外部系统接口） 3、AP名称
     * 
     * @param devName
     * @return 0：设置成功 1：设置失败
     */
    public int setDeviceName(String devName) {
        if (devName == null || devName.length() == 0) {
            return 1;
        }

        if (task != null) {
            handler.removeCallbacks(task);
        }

        task = new SetDeviceNameTask(devName);

        handler.post(task);

        this.deviceName = devName;

        int ret = Constants.SuccessCode.SET_ME_UP_SUCCESS.getValue();

        return ret;
    }

    class SetDeviceNameTask implements Runnable {
        private String devName;

        SetDeviceNameTask(String devName) {
            this.devName = devName;
        }

        @Override
        public void run() {
            Log.d(TAG, "strt to set external device name " + devName);
            Device.setDeviceName(context, this.devName);
            Log.d(TAG, "end to set external device name " + devName);
        }

    }

    public void release() {
        if (thread != null) {
            if (task != null) {
                handler.removeCallbacks(task);
            }

            thread.quit();

            thread = null;

            handler = null;
        }
    }
}
