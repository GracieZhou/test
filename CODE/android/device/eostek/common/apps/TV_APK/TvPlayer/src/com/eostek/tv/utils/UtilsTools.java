
package com.eostek.tv.utils;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.FilenameFilter;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import com.eostek.tv.pvr.UsbReceiver;
import com.mstar.android.storage.MStorageManager;

import android.annotation.SuppressLint;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Environment;
import android.os.StatFs;
import android.provider.Settings;

public class UtilsTools {

    public static final String NO_DISK = "NO_DISK";

    public static final String CHOOSE_DISK = "CHOOSE_DISK";

    /**
     * EPG timer event type none/remider/recorder projectName： Tv moduleName：
     * UtilsTools.java
     * 
     * @author lucky.li
     * @version 1.0.0
     * @time 2015-2-9 上午10:25:34
     * @Copyright © 2012 MStar Semiconductor, Inc.
     */
    public static enum EnumEventTimerType {
        EPG_EVENT_NONE, EPG_EVENT_REMIDER, EPG_EVENT_RECORDER,
    }

    public static final String timeformat = "yyyy-MM-dd HH:mm";

    /**
     * formate the date.
     * 
     * @param time
     * @param format
     * @return
     */
    @SuppressLint("SimpleDateFormat")
    public static String formatDate(long time, String format) {
        SimpleDateFormat formatter = new SimpleDateFormat(format);
        Date date = new Date(time);
        String curTimeStr = formatter.format(date);
        return curTimeStr;
    }

    public static void startPlayerActivity(Context context) {
        Intent intent = new Intent(Constants.START_TV_PLAYER_ACTION);
        context.startActivity(intent);
    }

    /**
     * get current top stack activity name
     * 
     * @param context
     * @return The Activity name or empty string
     */
    public static String getCurTopActivityName(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningTaskInfo> forGroundActivity = activityManager.getRunningTasks(1);
        RunningTaskInfo currentActivity;
        currentActivity = forGroundActivity.get(0);
        String activityName = currentActivity.topActivity.getClassName();
        if (activityName == null) {
            return "";
        }
        return activityName;
    }

    /**
     * @return internal sdcard size(M)
     */
    public static float getAvailableInternalSDCardSize() {
        try {
            if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                return 0f;
            }
            File path = Environment.getExternalStorageDirectory();
            StatFs statfs = new StatFs(path.getPath());
            return statfs.getFreeBlocksLong() * (statfs.getBlockCountLong() / (1024f * 1024f));
        } catch (Exception e) {
            e.printStackTrace();
            return 0f;
        }
    }

    public static int getAvailablePercent(String path) {
        StatFs sf = new StatFs(path);
        // the free blocks
        long freeBlock = sf.getFreeBlocksLong();
        // The total number of blocks on the file system
        long blockCount = sf.getBlockCountLong();
        int pecent = (int) ((1 - (float) freeBlock / blockCount) * 100);
        return pecent;
    }

    /**
     * whether the External Storage has enough space
     * 
     * @return true if the space is more than 60M,else false
     */
    public static boolean hasDiskCache() {
        boolean hasDisk = true;
        if (getAvailableInternalSDCardSize() < 60) {
            hasDisk = false;
        }
        return hasDisk;
    }

    /**
     * to find out whether the save mode is setted,the value is setted in tv
     * menu
     * 
     * @param context
     * @return true if the savemode is setted to 1,else false
     */
    public static boolean isSaveModeOpen(Context context) {
        int value = Settings.System.getInt(context.getContentResolver(), Constants.SAVE_MODE, 0);
        return value != 0;
    }

    /** the flag whether the count down activity is start **/
    private static boolean isCounterDownStarted = false;

    public static void setCounterDownStarted(boolean isTVCounterActivityStarted) {
        isCounterDownStarted = isTVCounterActivityStarted;
    }

    public static boolean getIsCountDownStarted() {
        return isCounterDownStarted;
    }

    public static int dip2px(Context context, float dipValue) {
        float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * scale + 0.5f);
    }

    /**
     * return whether the file is existed
     * 
     * @param path The file path
     * @return true if file exists,else false
     */
    public static boolean isFileExisted(String path) {
        if (path == null || "".equals(path)) {
            return false;
        } else {
            File file = new File(path);
            if (file.exists()) {
                return true;
            } else {
                return false;
            }
        }
    }

    public static String getFirstUseableDiskAtParentDir(String parent) {
        File file = new File(parent);
        if (file.isDirectory()) {
            File[] list = file.listFiles(new FilenameFilter() {

                @Override
                public boolean accept(File dir, String filename) {
                    if (new File(dir, filename).isDirectory())
                        return true;
                    else {
                        return false;
                    }
                }
            });
            for (File tmp : list) {
                // 依次查看哪个目录下面的文件路径是否存在
                // 此处的tmp是 /mnt/usb/sda1
                // 光检查这个目录不对，需要检查/mnt/usb/sda1/_MSTPVR/这个目录才对
                if ((isFileExisted(tmp.getAbsolutePath() + "/_MSTPVR/") || UsbReceiver.isDiskExisted(new String(tmp
                        .getAbsolutePath())))) {
                    return tmp.getAbsolutePath();
                }
            }
            // 如果循环检测完毕，都没有找到合适路径，则表明没有插入任何设备。
            return null;
        } else {
            return null;
        }
    }

    /**
     * get best disk path,if the path do not set,return {@link #CHOOSE_DISK}
     * ,else when the path do not find ,return {@link #NO_DISK},else return the
     * file path
     * 
     * @param context The Context object
     * @return PVR file path
     */
    public static String getBestDiskPath(Context context) {
        if (getChooseDiskSettings(context)) {
            // 已经选择了磁盘 从文件中读取存储的磁盘路径 注意 此处的label其实不重要，因为有可能和真是的盘符对不上
            // eg: /mnt/usb/sda1/ 注意这个函数
            String path = getChooseDiskPath(context);
            // 如果文件中没有保存 返回的unknown
            // 判断该路径对应的path是否存在
            if (isFileExisted(path + "/_MSTPVR") || UsbReceiver.isDiskExisted(path)) {
                // 该路径对应的文件存在
                return path;
            } else {
                // 该路径对应的路径不存在 从别的目录任意选择一个
                String parent = "/mnt/usb/";
                String firstDisk = getFirstUseableDiskAtParentDir(parent);
                // 没有找到合适的目录
                if (firstDisk == null) {
                    return NO_DISK;
                }
                // 找到合适的目录
                else {
                    return firstDisk;
                }
            }

        } else {
            // 没有设置，则进行选择磁盘 而且此处需要进行控制 如果设备为0 的话 则不需要控制 这里也有可能没有disk 所以还需要判断
            return CHOOSE_DISK;
        }

    }

    /* add by owen.qin begin */
    public static boolean getChooseDiskSettings(Context context) {
        SharedPreferences sp = context.getSharedPreferences("save_setting_select", Context.MODE_PRIVATE);
        return sp.getBoolean("IS_ALREADY_CHOOSE_DISK", false);
    }

    public static String getChooseDiskPath(Context context) {
        SharedPreferences sp = context.getSharedPreferences("save_setting_select", Context.MODE_PRIVATE);
        return sp.getString("DISK_PATH", "unknown");
    }

    public static String getTimeString(int seconds) {
        String hour = "00";
        String minute = "00";
        String second = "00";
        if (seconds % 60 < 10) {
            second = "0" + seconds % 60;
        } else {
            second = "" + seconds % 60;
        }

        int offset = seconds / 60;
        if (offset % 60 < 10) {
            minute = "0" + offset % 60;
        } else {
            minute = "" + offset % 60;
        }

        offset = seconds / 3600;
        if (offset < 10) {
            hour = "0" + offset;
        } else {
            hour = "" + offset;
        }
        return hour + ":" + minute + ":" + second;
    }

    /**
     * get file system
     * 
     * @param file The target file
     * @param path
     * @return The file system,like NFTS or FAT
     */
    public static String getFileSystem(File file, String path) {
        if (file == null) {
            return "";
        }
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
            String line = br.readLine();
            while (line != null) {
                String[] info = line.split(" ");
                if (info[1].equals(path)) {
                    if (info[2].equals("ntfs3g"))
                        return "NTFS";
                    if (info[2].equals("vfat"))
                        return "FAT";
                    else
                        return info[2];
                }
                line = br.readLine();
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return "";
    }

    /**
     * @param path like /mnt/usb/sda1
     * @return According to the path to return to the path of the corresponding
     *         label if we save the label this doesn't work, and should be based
     *         on the path to obtain the corresponding label.
     */
    public static String getUsbLabelByPath(Context context, String diskPath) {
        MStorageManager storageManager = MStorageManager.getInstance(context);
        String[] volumes = storageManager.getVolumePaths();
        int usbDriverCount = 0;
        ArrayList<String> usbDriverLabel = new ArrayList<String>();
        ArrayList<String> usbDriverPath = new ArrayList<String>();
        usbDriverLabel.clear();
        usbDriverPath.clear();
        if (volumes == null) {
            return null;
        }

        File file = new File("proc/mounts");
        if (!file.exists() || file.isDirectory()) {
            file = null;
        }

        for (int i = 0; i < volumes.length; ++i) {
            String state = storageManager.getVolumeState(volumes[i]);
            if (state == null || !state.equals(Environment.MEDIA_MOUNTED)) {
                continue;
            }
            String path = volumes[i];
            String[] pathPartition = path.split("/");
            // the last part
            String label = pathPartition[pathPartition.length - 1];

            String volumeLabel = storageManager.getVolumeLabel(path);
            if (volumeLabel != null) {
                // get rid of the long space in the Label word
                String[] tempVolumeLabel = volumeLabel.split(" ");
                volumeLabel = "";
                for (int j = 0; j < tempVolumeLabel.length; j++) {
                    if (j != tempVolumeLabel.length - 1) {
                        volumeLabel += tempVolumeLabel[j] + " ";
                        continue;
                    }
                    volumeLabel += tempVolumeLabel[j];
                }
            }
            label += ": " + UtilsTools.getFileSystem(file, path) + "\n" + volumeLabel;
            usbDriverLabel.add(usbDriverCount, label);
            usbDriverPath.add(usbDriverCount, path);
            usbDriverCount++;
        }

        // remove diskPath start and end's/
        if (diskPath.startsWith("/")) {
            diskPath = diskPath.substring(1);
        }
        if (diskPath.endsWith("/")) {
            diskPath = diskPath.substring(0, diskPath.length() - 1);
        }
        for (int i = 0; i < usbDriverPath.size(); i++) {
            if (usbDriverPath.get(i).contains(diskPath)) {
                return usbDriverLabel.get(i);
            }
        }
        // If iterate through all the usb did not find the path of the
        // corresponding label will return null.
        return null;
    }

    /**
     * if the given file path is FAT format
     * 
     * @param context
     * @param diskPath
     * @return true if the file is FAT format,else false
     */
    public static boolean isFATDisk(Context context, String diskPath) {
        String label = getUsbLabelByPath(context, new String(diskPath));
        LogUtil.i("label:" + label + " ; diskPath" + diskPath);
        if (label != null && label.contains("FAT")) {
            return true;
        }
        return false;
    }

    /**
     * get avaliable disk for StandBy
     * 
     * @param context
     * @return The avaliable path,null if not find
     */
    public static String getAvaliableDiskForStandBy(Context context) {
        String parent = "/mnt/usb/";
        String firstDisk = UtilsTools.getFirstUseableDiskAtParentDir(parent);
        // only support FAT format disk
        if (firstDisk == null || !isFATDisk(context, firstDisk)) {
            return null;
        } else {
            return firstDisk;
        }
    }

}
