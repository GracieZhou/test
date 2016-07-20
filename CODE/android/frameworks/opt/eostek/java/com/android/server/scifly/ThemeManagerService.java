
package com.android.server.scifly;

import java.io.File;
import java.io.FileFilter;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import scifly.thememanager.IThemeChangeListener;
import scifly.thememanager.IThemeManager;
import android.app.ActivityManager;
import android.content.Context;
import android.os.Environment;
import android.os.FileUtils;
import android.os.RemoteException;
import android.os.SystemProperties;
import android.os.storage.StorageManager;
import android.os.storage.StorageVolume;
import android.util.Log;

import com.android.server.scifly.theme.ThemeManagerUtils;

public class ThemeManagerService extends IThemeManager.Stub {
    private static final String TAG = "ThemeManagerService";

    private ActivityManager mActivityManager;

    private static final String mStoragePath = "/data/eostek";

    private static final String mDefaultFileName = "/theme.zip";

    // key words of theme_manager:
    private static final String mActivityMngClass = "android.app.ActivityManager";

    private static final String mMethodName = "forceStopPackage";

    // share preferences key words:
    private static final String mMD5Key = "md5";

    private static final String DEBUG_KEY = "theme.debug";

    private static final String USB_PATH_KEY = "theme";

    // interface available.
    private boolean mAvailable = true;

    private IThemeChangeListener mListener;

    private Context mContext;

    // private CycleManager excuter;

    private StorageManager mStorageManager;

    private Method mMethod;

    private FileFilter fileFilter = new FileFilter() {

        @Override
        public boolean accept(File pathname) {
            if (pathname.isDirectory()) {
                return true;
            }
            return false;
        }
    };

    /**
     * Constructor.
     * 
     * @param context
     */
    public ThemeManagerService(Context context) {
        this.mContext = context;
        mActivityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);

        // // create and start cycle thread.checking debug mode.
        // excuter = new CycleManager(new UnzipThread());
        // excuter.start();
    }

    /**
     * switch theme.
     * 
     * @param themePath absolute path.
     */
    public synchronized void changeTheme(final String themePath, IThemeChangeListener listener,final boolean isUnzip) {
        this.mListener = listener;

        // code review begin:
        if (!isInterfaceEnable()) {
            print("interface disabled,please wait.");
            return;
        }
        disabled();
        // end.

        new Thread(new Runnable() {

            @Override
            public void run() {
                themeSwitch(themePath,isUnzip);
                enable();
            }
        }).start();

    }

    /**
     * back to default theme.
     * 
     * @param listener
     */
    public synchronized void themeRollBack(final IThemeChangeListener listener) {

        // code review begin:
        if (!isInterfaceEnable()) {
            print("interface disabled,please wait.");
            return;
        }
        disabled();
        // end.

        new Thread(new Runnable() {

            @Override
            public void run() {

                try {
                    // application pkgs collection begin:
                    File targetFile = new File(mStoragePath);
                    File[] pkgs = targetFile.listFiles(fileFilter);
                    // end.

                    // delete all files.
                    ThemeManagerUtils.deleteAllFiles(targetFile, false);

                    // stop applications.
                    for (File f : pkgs) {
                        String pkg = f.getName();
                        print(pkg);
                        exitAppByName(pkg);
                    }

                    // call back.
                    if (listener != null) {
                        try {
                            listener.onSucceed();
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    if (listener != null) {
                        try {
                            listener.onFailed(e.getMessage());
                        } catch (RemoteException e1) {
                            e1.printStackTrace();
                        }
                    }
                }

                enable();
            }
        }).start();

    }

    public void debugEnable(boolean enable) {
        print("ThemeManagerService.debugEnable():" + enable);
        // if (enable) {
        // // start cycle.
        // excuter.startCycle();
        // } else {
        // // stop cycle.
        // excuter.stopCycle();
        // }

        String usbVolume = getDefaultUsbVolume();
        if (usbVolume == null) {
            print("usb storage does not exist, exit.");
            return;
        }

        if (enable) {
            SystemProperties.set(USB_PATH_KEY, usbVolume);
            SystemProperties.set(DEBUG_KEY, "true");
        } else {
            SystemProperties.set(DEBUG_KEY, "false");
        }
    }

    /**
     * unzipping thread.
     * 
     * @author Youpeng
     */
    private class UnzipThread implements Runnable {

        @Override
        public void run() {

            // if (mAvailable) {
            // print("thread is unzipping.");
            // return;
            //
            // }
            // mAvailable = true;

            String usbVolume = getDefaultUsbVolume();
            if (usbVolume == null) {
                print("usb storage does not exist, exit.");
                return;
            }
            String defaultThemePath = usbVolume + mDefaultFileName;
            if (debugMode() && themeChanged(defaultThemePath)) {
                print("theme changed and then execute themeSwitch().");
                themeSwitch(defaultThemePath,true);

            }
            // mAvailable = false;
            // else {
            // print("theme not changed.");
            // }

        }

        /**
         * whether theme changed. old file's md5 not equeals new file's
         * md5,means changed.
         * 
         * @return
         */
        private boolean themeChanged(String sourcePath) {

            String newFileMD5 = ThemeManagerUtils.getFileMD5(sourcePath);
            if (newFileMD5 == null) {
                print("theme file not exists.");
                return false;
            }

            String oldFileMD5 = ThemeManagerUtils.getString(mContext, mMD5Key);
            if (oldFileMD5 == null) {
                print("need to switch theme.");
                return true;
            }

            if (newFileMD5.equals(oldFileMD5)) {
                print("zip not changed.");
                return false;
            }

            print("zip changed.");
            return true;
        }

        /**
         * if it is debug mode.
         * 
         * @return
         */
        private boolean debugMode() {
            // return ThemeManagerUtils.getStatus();
            String value = SystemProperties.get(DEBUG_KEY, "false");
            // print("System.prop:theme.debug:" + value);
            if ("true".equals(value))
                return true;
            return false;
        }

    }

    private void themeSwitch(String source,boolean isUnzip) {

        if (source == null) {
            try {
                mListener.onFailed("source not found.");
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            return;
        }

        // isUnzipping = true;

        if(isUnzip){
            // create folder.
            File file = new File(mStoragePath);
            if (!file.exists()) {

                try {
                    file.mkdir();

                    // chmod 777:
                    FileUtils.setPermissions(mStoragePath, 0777, -1, -1);
                } catch (Exception e) {
                    e.printStackTrace();
                    print(e.getMessage());
                }
            }

            // 1.unzip theme.zip to /data/eostek.
            try {
                ThemeManagerUtils.upZipFile(source, mStoragePath);

                // restore md5.
                String newFileMD5 = ThemeManagerUtils.getFileMD5(source);
                ThemeManagerUtils.setString(mContext, mMD5Key, newFileMD5);
            } catch (Exception e) {
                e.printStackTrace();

                // isUnzipping = false;
                if (mListener != null) {
                    try {
                        mListener.onFailed(e.getMessage());
                    } catch (RemoteException e1) {
                        e1.printStackTrace();
                    }
                }
                return;
            }

            // chmod 777.
            FileUtils.setPermissions(mStoragePath, 0777, -1, -1);

        }

        // 2.get changed pkg.
        File[] files = (new File(mStoragePath)).listFiles();

        if (files != null) {
            for (File f : files) {
                if (f.isDirectory()) {
                    String pkgName = f.getName();
                    if (pkgName.equals("thumbnail")) {
                        continue;
                    }

                    print(pkgName);

                    // 3.restart apps.
                    exitAppByName(pkgName);

                } else {
                    continue;
                }

            }

            // exit launcher.
            // exitAppByName("com.google.launcher");

            if (mListener != null) {
                try {
                    mListener.onSucceed();
                } catch (RemoteException e) {
                    // TODO Auto-generated catch block
                    e.printStackTrace();
                }
            }

        }
        // isUnzipping = false;

    }

    /**
     * exit app.
     * 
     * @param package name.
     */
    private void exitAppByName(String pkgName) {
        try {
            if (mMethod == null) {
                mMethod = Class.forName(mActivityMngClass).getMethod(mMethodName, String.class);
            }

            mMethod.invoke(mActivityManager, pkgName);

        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

    }

    // private String getAbsluteFilePath() {
    // String defaultPath = getDefaultUsbVolume();
    // if (defaultPath == null) {
    // return mDefaultThemePath + mDefaultFileName;
    // } else {
    // return defaultPath + mDefaultFileName;
    // }
    // }

    /**
     * @return default usb volume,null if there's no usb volume.
     */
    private String getDefaultUsbVolume() {
        if (mStorageManager == null) {
            mStorageManager = (StorageManager) mContext.getSystemService(Context.STORAGE_SERVICE);
        }

        // get all disk infos.
        StorageVolume[] volumes = mStorageManager.getVolumeList();

        // no disks.
        if (volumes == null || volumes.length == 0) {
            return null;
        }

        // get all loaded disks.
        for (StorageVolume item : volumes) {
            String path = item.getPath();
            if (path == null) {
                continue;
            }
            String state = mStorageManager.getVolumeState(path);

            // load failed.
            if (state == null || !state.equals(Environment.MEDIA_MOUNTED)) {
                continue;

            }

            print("storagePath:" + path);
            return path;
        }
        return null;

    }

    private void enable() {
        mAvailable = true;
    }

    private void disabled() {
        mAvailable = false;
    }

    private boolean isInterfaceEnable() {
        return mAvailable;
    }

    private void print(String str) {
        Log.i(TAG, str);
    }

}
