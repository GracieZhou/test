
package com.eostek.scifly.devicemanager;

import java.io.File;
import java.util.List;
import com.eostek.scifly.devicemanager.util.Debug;
import android.annotation.TargetApi;
import android.app.ActivityManager;
import android.app.ActivityManager.MemoryInfo;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.ActivityManagerExtra;
import android.content.Context;
import android.os.Build;
import android.os.Environment;
import android.os.StatFs;

/**
 * @author Psso This class provides some APIs for managing Scifly devices. Get
 *         an instance of this class by calling {@link
 *         com.eostek.scifly.devicemanager.DeviceManager.getInstance()}
 */
public class DeviceManager {

    private static final String TAG = DeviceManager.class.getSimpleName();
    
    private volatile static DeviceManager instance;

    private DeviceEngine engine;

    // The size of this device manager's engine.
    private final int THREAD_POOL_SIZE = 1;

    public DeviceManager() {
        engine = new DeviceEngine(THREAD_POOL_SIZE);
    }

    /**
     * @return The static instance of this DeviceManager
     */
    public static DeviceManager getInstance() {
        if (instance == null) {
            synchronized (DeviceManager.class) {
                if (instance == null) {
                    instance = new DeviceManager();
                }
            }
        }
        return instance;
    }

    /**
     * Get available memory size.
     * 
     * @param context Current activity context.
     * @return Current available memory size in byte.
     */
    public long getAvailMemSize(Context context) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo outInfo = new MemoryInfo();
        am.getMemoryInfo(outInfo);
        Debug.d(TAG, "Available Ram size: " + outInfo.availMem + " bytes");
        return outInfo.availMem;
    }

    /**
     * Get total memory size. Requires API level 16.
     * 
     * @param context Current activity context.
     * @return Current total memory size in byte.
     */
    @TargetApi(Build.VERSION_CODES.JELLY_BEAN)
    public long getTotalMemSize(Context context) {
        Debug.d(TAG, "Current SDK version is " + Build.VERSION.SDK_INT);
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        ActivityManager.MemoryInfo outInfo = new MemoryInfo();
        am.getMemoryInfo(outInfo);
        Debug.d(TAG, "Total Ram size: " + outInfo.totalMem + " bytes");
        return outInfo.totalMem;
    }

    /**
     * Get sciflyVideo whitelist.
     * 
     * @return A {@link java.util.List} of SciflyVideo whitelist.
     */
    public List<String> getWhiteList() {
        return ActivityManagerExtra.getInstance().getAutoStartWhiteListForApk();
    }

    /**
     * Kill process with @pkgName
     * 
     * @param context Current activity context.
     * @param pkgName Package name of process that need to be killed.
     */
    public void killProcess(Context context, String pkgName) {
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        am.killBackgroundProcesses(pkgName);
    }

    /**
     * Kill all background processes.
     */
    public void killAllProcess(Context context) {
        //ActivityManagerExtra.getInstance().killAllBackgroundApks();
        ActivityManager am = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningAppProcessInfo> list = am.getRunningAppProcesses();
        for (RunningAppProcessInfo app : list) {
            // kill the process not in white list
            am.killBackgroundProcesses(app.processName);
        }
    }

    /**
     * Get auto start white list
     * 
     * @return A {@link java.util.List} of auto start white list.
     */
    public List<String> getSelfStartingProcessList() {
        return ActivityManagerExtra.getInstance().getAutoStartWhiteListForApk();
    }

    /**
     * Set @pkgName auto start enable if @enable is true or disable if @enable
     * is false.
     * 
     * @param pkgName Package name that need to be set.
     * @param enable Enable if true or disable if false.
     */
    public void setSelfStartingProcessEnable(String pkgName, boolean enable) {
        ActivityManagerExtra.getInstance().setAutoStartEnabledForApk(pkgName, enable);
    }

    public long install() {
        return 0;
    }

    private long getRomTotalSize() {
        File path = Environment.getDataDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSizeLong();
        long totalBlocks = stat.getBlockCountLong();
        return blockSize * totalBlocks;
    }
    
    private long getSystemSpace() {
        File path = Environment.getRootDirectory();
        long fileTotalLength = path.getTotalSpace();
        return fileTotalLength;
    }
    
    private long getCacheSpace() {
        File path = Environment.getDownloadCacheDirectory();
        long fileTotalLength = path.getTotalSpace();
        return fileTotalLength;
    }
    
    private long getExternalSDTotalSize() {
        if (Environment.isExternalStorageEmulated()) {
            return 0;
        }
        File path = Environment.getExternalStorageDirectory();
        StatFs stat = new StatFs(path.getPath());
        long blockSize = stat.getBlockSizeLong();
        long totalBlocks = stat.getBlockCountLong();
        return blockSize * totalBlocks;
    }
    
    public long getTotalStorageSize() {
        return getRomTotalSize() + getSystemSpace() + getExternalSDTotalSize() + getCacheSpace();
    }
    
    /**
     * Get free storage size in byte including internal and external.
     * 
     * @return Free storage size in byte.
     */
    public long getAvailStorageSize() {
        StatFs statInternal = new StatFs(Environment.getDataDirectory().getPath());
        long availInternal = statInternal.getAvailableBlocksLong() * statInternal.getBlockSizeLong();
        return availInternal;
    }

    /**
     * Add @task into this device engine.
     * 
     * @param task A {@link com.eostek.scifly.devicemanager.task.BaseTask} that
     *            need to be done.
     * @return The id of the task.
     */
    public int startTask(BaseTask task) {
        task.setEngine(engine);
        return engine.submit(task);
    }

    /**
     * Cancel the {@link com.eostek.scifly.devicemanager.task.BaseTask} with
     * 
     * @id.
     * @param id Id of the task that needs to be canceled.
     * @return True if the task is done or canceled, false otherwise.
     */
    public boolean cancelTask(int id) {
        return engine.cancel(id);
    }

}
