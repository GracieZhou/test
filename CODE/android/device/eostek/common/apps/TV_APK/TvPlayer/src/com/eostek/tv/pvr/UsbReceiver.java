
package com.eostek.tv.pvr;

import java.io.File;
import java.util.HashMap;

import com.eostek.tv.utils.LogUtil;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;

public class UsbReceiver extends BroadcastReceiver {

    /** 是否将所有挂载的设备添加到{@link UsbReceiver#mDiskMap} 里面 **/
    private static boolean mFirstStart = true;

    /** the list contains all mounted disk **/
    private static HashMap<String, Integer> mDiskMap = new HashMap<String, Integer>();

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Uri uri = intent.getData();
        final String path = uri.getPath();
        if (action.equals(Intent.ACTION_MEDIA_MOUNTED)) {
            LogUtil.i("MOUNTED path = " + path);
            // add disk to array
            addDiskDesc(path, 0);
        } else if (action.equals(Intent.ACTION_MEDIA_EJECT)) {
            LogUtil.i("EJECT path = " + path);
            // remove the disk from array
            removeDiskDesc(path);
        }
    }

    public UsbReceiver() {
        LogUtil.d("UsbReceiver constructor!!!");
        if (mFirstStart) {
            initDiskDesc();
            mFirstStart = false;
        }
    }

    /**
     * @return disk count
     */
    public static synchronized int getDiskCount() {
        if (mDiskMap != null) {
            return mDiskMap.size();
        } else {
            return 0;
        }
    }

    /**
     * return disk capacity percent by path
     * 
     * @param key
     * @return
     */
    public static synchronized int getDiskCapacityPercent(String key) {
        Integer tmp = mDiskMap.get(key);
        if (tmp == null) {
            return -1;
        } else {
            return tmp;
        }
    }

    public static synchronized boolean isDiskExisted(String path) {
        path = formatKey(path);
        if (mDiskMap.containsKey(path)) {
            return true;
        } else {
            return false;
        }
    }

    private static synchronized void addDiskDesc(String key, Integer value) {
        mDiskMap.put(key, value);
    }

    private static synchronized void removeDiskDesc(String key) {
        mDiskMap.remove(key);
    }

    /**
     * 初始化磁盘信息,将所有挂载在usb路径的设备都添加到数组里面
     */
    private static synchronized void initDiskDesc() {
        String parent = "/mnt/usb/";
        File pFile = new File(parent);
        File[] children = pFile.listFiles();
        if (children != null) {
            String str = null;
            for (File file : children) {
                str = file.getName();
                if (str != null) {
                    LogUtil.i("find disk " + str);
                    addDiskDesc(parent + str, 0);
                }
            }
        }
    }

    private static String formatKey(String value) {
        if (value == null) {
            return null;
        }
        if (!value.startsWith("/")) {
            value = "/" + value;
        }
        if (value.endsWith("/")) {
            value = value.substring(0, value.length() - 1);
            return formatKey(value);
        } else {
            return value;
        }
    }

}
