
package com.android.settings.miscupgrade;

import java.io.File;
import java.lang.reflect.Field;
import java.lang.reflect.Modifier;
import java.util.List;

import scifly.permission.Permission;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.content.pm.ApplicationInfo;
import android.content.pm.IPackageInstallObserver;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.FileUtils;
import android.text.TextUtils;
import android.util.Log;
import android.util.SparseArray;

public class UpgradeHelper {

    private static final String TAG = UpgradeConstants.TAG;

    private static UpgradeHelper _sInstance = null;

    private static Context mContext = null;

    private Permission mShell = null;

    private UpgradeHelper(Context context) {
        mContext = context;
        // signature_tool -e com.android.settings
        mShell = new Permission("JCheb2lkLnNldHRpbmdzanJt");
    }

    public static UpgradeHelper geInstance(Context context) {

        if (null == _sInstance) {
            _sInstance = new UpgradeHelper(context);
        }

        return _sInstance;
    }

    public UpgradeResult upgrade(String externalFilePath, String systemFilePath) {
        if (TextUtils.isEmpty(externalFilePath) || TextUtils.isEmpty(systemFilePath)) {
            return UpgradeResult.UPGRADE_INVALID_PATH;
        }

        File extFile = new File(externalFilePath);
        if (extFile.exists() && extFile.canRead()) {
            File sysFile = new File(systemFilePath);

            if (sysFile.exists()) {
                // check md5sum now
                String sysMd5 = Util.getFileMD5(sysFile);
                String extMd5 = Util.getFileMD5(extFile);

                if (null == sysMd5 || null == extMd5) {
                    return UpgradeResult.UPGRADE_CHECK_MD5SUM_FAILED;
                }

                if (sysMd5.equals(extMd5)) {
                    Log.i(TAG, String.format("sys file(%s), ext file(%s), md5sum(%s)\n", systemFilePath,
                            externalFilePath, sysMd5));

                    return UpgradeResult.UPGRADE_THE_SAME_MD5SUM;
                }
            }

            // upgrade now
            boolean isVideo = UpgradeConstants.USER_BOOTVIDEO_NAME.equals(extFile.getName());
            boolean bcopy = false;

            if (isVideo) {
                File parent = sysFile.getParentFile();

                if (!parent.exists()) {
                    parent.mkdirs();
                }

                bcopy = FileUtils.copyFile(extFile, sysFile);
            } else {
                bcopy = mShell.exec("cp " + externalFilePath + " " + systemFilePath);
            }

            if (bcopy) {

                if (isVideo) {
                    int ret = FileUtils.setPermissions(sysFile, 0644, 1000, 1000);

                    if (0 != ret) {
                        Log.e(TAG, "Set permissions failed with file :" + systemFilePath);
                        return UpgradeResult.UPGRADE_SET_PERMISSION_FAILED;
                    }
                } else {
                    bcopy = mShell.exec("chmod 644 " + systemFilePath);
                    if (!bcopy) {
                        Log.e(TAG, "eshell chmod 644 " + systemFilePath + " failed.");
                        return UpgradeResult.UPGRADE_SET_PERMISSION_FAILED;
                    }
                }

                return UpgradeResult.UPGRADE_SUCCESS;
            } else {
                Log.e(TAG, "Copy file " + externalFilePath + " to " + systemFilePath + " failed.");
                return UpgradeResult.UPGRADE_COPY_FILE_FAILED;
            }

        } else {
            Log.i(TAG, "External file dose not exist .");

            return UpgradeResult.UPGRADE_EXT_FILE_NOT_EXIST;
        }
    }

    public UpgradeResult upgrade(String extLauncherPath) {

        if (TextUtils.isEmpty(extLauncherPath)) {
            return UpgradeResult.UPGRADE_INVALID_PATH;
        }

        File extFile = new File(extLauncherPath);

        if (extFile.exists()) {
            String userLauncherPath = getUserLauncherPath(mContext);
            if (null == userLauncherPath) {
                // pm install -r external launcher
                return installUserLauncher(extLauncherPath);

            } else {
                // check md5sum first
                String userLauncherMd5 = Util.getFileMD5(new File(userLauncherPath));
                String extMd5 = Util.getFileMD5(extFile);

                if (null == userLauncherMd5 || null == extMd5) {
                    return UpgradeResult.UPGRADE_CHECK_MD5SUM_FAILED;
                }

                if (userLauncherMd5.equals(extMd5)) {
                    return UpgradeResult.UPGRADE_THE_SAME_MD5SUM;
                }

                return installUserLauncher(extLauncherPath);
                // then install -r external launcher
            }

        } else {

            Log.i(TAG, "External launcher dose not exist .");

            return UpgradeResult.UPGRADE_EXT_FILE_NOT_EXIST;
        }
    }

    private UpgradeResult installUserLauncher(String codePath) {

        final Uri apkURI;
        int installFlags = PackageManager.INSTALL_ALL_USERS | PackageManager.INSTALL_REPLACE_EXISTING;

        Log.i(TAG, "\tpkg: " + codePath);
        if (codePath != null) {
            apkURI = Uri.fromFile(new File(codePath));
        } else {
            Log.e(TAG, "Error: no package specified");
            return UpgradeResult.UPGRADE_INVALID_PATH;
        }

        PackageInstallObserver obs = new PackageInstallObserver();

        PackageManager mPm = mContext.getPackageManager();
        if (mPm == null) {
            Log.e(TAG, "Package Manager not ready.");
            return UpgradeResult.UPGRADE_GET_PACKAGE_MANAGER_FAILED;
        }
        mPm.installPackage(apkURI, obs, installFlags, null);

        synchronized (obs) {
            while (!obs.finished) {
                try {
                    obs.wait();
                } catch (InterruptedException e) {
                    Log.e(TAG, e.getMessage());
                    return UpgradeResult.UPGRADE_UNKNOW_FAILED;
                }
            }
            if (obs.result == PackageManager.INSTALL_SUCCEEDED) {
                Log.i(TAG, "Success");
                return UpgradeResult.UPGRADE_SUCCESS;
            } else {
                Log.i(TAG, "Failure [" + installFailureToString(obs.result) + "]");

                return UpgradeResult.UPGRADE_UNKNOW_FAILED;
            }
        }

    }

    private String installFailureToString(int result) {
        Field[] fields = PackageManager.class.getFields();
        for (Field f : fields) {
            if (f.getType() == int.class) {
                int modifiers = f.getModifiers();
                // only look at public final static fields.
                if (((modifiers & Modifier.FINAL) != 0) && ((modifiers & Modifier.PUBLIC) != 0)
                        && ((modifiers & Modifier.STATIC) != 0)) {
                    String fieldName = f.getName();
                    if (fieldName.startsWith("INSTALL_FAILED_") || fieldName.startsWith("INSTALL_PARSE_FAILED_")) {
                        // get the int value and compare it to result.
                        try {
                            if (result == f.getInt(null)) {
                                return fieldName;
                            }
                        } catch (IllegalAccessException e) {
                            // this shouldn't happen since we only look for
                            // public static fields.
                        }
                    }
                }
            }
        }

        // couldn't find a matching constant? return the value
        return Integer.toString(result);
    }

    class PackageInstallObserver extends IPackageInstallObserver.Stub {
        boolean finished;

        int result;

        public void packageInstalled(String name, int status) {
            synchronized (this) {
                finished = true;
                result = status;
                Log.i(TAG, "finished install:" + name);
                notifyAll();
            }
        }
    }

    // Add by frank.zhang@20150601
    private static boolean isSystemApp(PackageInfo pInfo) {
        return ((pInfo.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) != 0);
    }

    public static String getUserLauncherPath(Context context) {
        long start = System.currentTimeMillis();
        PackageManager packageManager = context.getPackageManager();
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);

        List<ResolveInfo> resolveInfos = packageManager
                .queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);

        if (null == resolveInfos || resolveInfos.size() < 1) {
            Log.e(TAG, "do not found any launcher !");
            return null;
        }
        // use to store the filename/versionCode pair
        final SparseArray<String> launcherMap = new SparseArray<String>();

        for (ResolveInfo resolveInfo : resolveInfos) {
            ActivityInfo activityInfo = resolveInfo.activityInfo;
            if (activityInfo != null) {
                try {
                    PackageInfo pkgInfo = context.getPackageManager().getPackageInfo(activityInfo.packageName, 0);

                    if (null != pkgInfo && isSystemApp(pkgInfo)) {
                        Log.d(TAG, "found " + pkgInfo.applicationInfo.sourceDir);
                        launcherMap.put(pkgInfo.versionCode, pkgInfo.applicationInfo.sourceDir);
                    }
                } catch (NameNotFoundException e) {
                    Log.e(TAG, e.getMessage());
                    continue;
                }

            }
        }
        long spent = System.currentTimeMillis() - start;
        Log.i(TAG, "getUserLauncher total spent " + String.valueOf(spent) + " ms");
        if (launcherMap.size() > 0) {
            String targetPath = launcherMap.valueAt(launcherMap.size() - 1);
            Log.i(TAG, "found target at " + targetPath);
            return targetPath;
        }
        Log.i(TAG, "do not found user upgrade launcher.");
        return null;
    }
}
