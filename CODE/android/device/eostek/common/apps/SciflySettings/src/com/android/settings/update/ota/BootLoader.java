
package com.android.settings.update.ota;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.FileInputStream;
import java.io.InputStreamReader;

import android.os.SystemProperties;
import android.text.TextUtils;
import android.util.Log;
import scifly.permission.Permission;

public class BootLoader {

    private static final String FSTAB_PREFIX = "/fstab.";

    private static final String TAG = "BootLoader";

    static {
        try {
            System.loadLibrary("jni_bootloader_scifly");
        } catch (UnsatisfiedLinkError e) {
            Log.e(TAG, "load library jni_bootloader_scifly failed:", e);
        }
    }

    Permission mShell;

    public BootLoader() {
        mShell = new Permission("JCheb2lkLnNldHRpbmdzanJt");
    }

    public String getBootloader() {
        String dev = getDevicePath();

        if (!TextUtils.isEmpty(dev)) {
            return getBootloaderMessage(dev);
        }

        return "";
    }

    public boolean setBootloader() {
        String dev = getDevicePath();

        if (!TextUtils.isEmpty(dev)) {
            mShell.exec("chmod 666 " + dev);

            return setBootloaderMessage(dev);
        } else {
            // 这个地方来处理m805
            mShell.exec("touch /cache/recovery/last_force.ota");
            return true;
        }
    }

    public void clearBootloader() {

        String dev = getDevicePath();

        if (!TextUtils.isEmpty(dev)) {
            clearBootloaderMessage(dev);
        }
    }

    /**
     * 目前只有Mstar平台支持在android平台向misc块写命令后，下次开机直接进入recovery，而m805则不行，只能走点弯路
     * m805就向/cache/recovery下面生成一个空文件force.ota，在开机进入init里面判断是否存在这个文件，如果存在则重启到recovery
     * 
     * @return
     */
    private String getDevicePath() {

        File fstab = getFstab();
        String dev = getMiscDev(fstab);

        return dev;
    }

    public String getMiscDev(File f) {
        if (f == null) {
            Log.e(TAG, "fstab does not exist !");
            return null;
        }
        String oneLine = "";
        BufferedReader br = null;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(f)));
            while ((oneLine = br.readLine()) != null) {
                String[] parts = oneLine.split(" ");
                for (int i = 0; i < parts.length; i++) {
                    if ("".equals(parts[i])) {
                        continue;
                    }

                    if (parts[i].equals("/misc")) {
                        return parts[0];
                    }

                }
            }
        } catch (FileNotFoundException e) {
            Log.e(TAG, e.getMessage());
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (final IOException e) {
                    Log.e(TAG, e.getMessage());
                }
            }
        }
        return null;
    }

    /**
     * mstar平台的设备都是以/fstab.${ro.hardware}这种方式来找fstab的 amlogic的则在此处会返回空
     * 
     * @return
     */
    private File getFstab() {

        String hardware = SystemProperties.get("ro.hardware", "");

        File f = new File(FSTAB_PREFIX + hardware);
        if (f.exists()) {
            return f;
        }

        return null;
    }

    private native String getBootloaderMessage(String devicePath);

    private native boolean setBootloaderMessage(String devicePath);

    private native void clearBootloaderMessage(String devicePath);
}
