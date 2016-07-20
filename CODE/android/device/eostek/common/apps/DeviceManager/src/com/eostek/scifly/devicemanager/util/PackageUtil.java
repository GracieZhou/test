/*
 **
 ** Copyright 2007, The Android Open Source Project
 **
 ** Licensed under the Apache License, Version 2.0 (the "License");
 ** you may not use this file except in compliance with the License.
 ** You may obtain a copy of the License at
 **
 **     http://www.apache.org/licenses/LICENSE-2.0
 **
 ** Unless required by applicable law or agreed to in writing, software
 ** distributed under the License is distributed on an "AS IS" BASIS,
 ** WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 ** See the License for the specific language governing permissions and
 ** limitations under the License.
 */

package com.eostek.scifly.devicemanager.util;

import android.app.Activity;
import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageParser;
import android.content.res.AssetManager;
import android.content.res.Resources;
import android.graphics.drawable.Drawable;
import android.os.Build;
import android.util.DisplayMetrics;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.List;

/**
 * This is a utility class for defining some utility methods and constants used
 * in the package installer application.
 */
public class PackageUtil {
    
    private static final String TAG = PackageUtil.class.getSimpleName();
    
    public static final String PREFIX = "com.android.packageinstaller.";

    public static final String INTENT_ATTR_INSTALL_STATUS = PREFIX + "installStatus";

    public static final String INTENT_ATTR_APPLICATION_INFO = PREFIX + "applicationInfo";

    public static final String INTENT_ATTR_PERMISSIONS_LIST = PREFIX + "PermissionsList";

    // intent attribute strings related to uninstall
    public static final String INTENT_ATTR_PACKAGE_NAME = PREFIX + "PackageName";

    /**
     * Utility method to get application information for a given {@link File}
     * 
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws InstantiationException
     * @throws ClassNotFoundException
     */
    public static ApplicationInfo getApplicationInfo(File sourcePath) throws ClassNotFoundException,
            InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
            NoSuchMethodException {
        // final String archiveFilePath = sourcePath.getAbsolutePath();
        // PackageParser packageParser = new PackageParser(archiveFilePath);
        // File sourceFile = new File(archiveFilePath);
        // DisplayMetrics metrics = new DisplayMetrics();
        // metrics.setToDefaults();
        // PackageParser.Package pkg = packageParser.parsePackage(
        // sourceFile, archiveFilePath, metrics, 0);
        // if (pkg == null) {
        // return null;
        // }
        return getPackageInfo(sourcePath).applicationInfo;
    }

    /**
     * get AppInfo not by PackageParser but PackageInfo.
     * 
     * @param sourcePath File
     * @param pm PackageManager
     * @return
     */
    public static ApplicationInfo getApplicationInfo(File sourcePath, PackageManager pm) {
        final String archiveFilePath = sourcePath.getAbsolutePath();
        PackageInfo pkgInfo = pm.getPackageArchiveInfo(archiveFilePath, PackageManager.GET_ACTIVITIES);
        if (pkgInfo == null) {
            Debug.e(TAG, "get NULL PackageInfo!");
            return null;
        }
        ApplicationInfo appInfo = pkgInfo.applicationInfo;
        appInfo.sourceDir = archiveFilePath;
        appInfo.publicSourceDir = archiveFilePath;
        return appInfo;
    }

    /**
     * Utility method to get package information for a given {@link File}
     * 
     * @throws ClassNotFoundException
     * @throws NoSuchMethodException
     * @throws InvocationTargetException
     * @throws IllegalArgumentException
     * @throws IllegalAccessException
     * @throws InstantiationException
     */
    public static PackageParser.Package getPackageInfo(File sourceFile) throws ClassNotFoundException,
            InstantiationException, IllegalAccessException, IllegalArgumentException, InvocationTargetException,
            NoSuchMethodException {
        // final String archiveFilePath = sourceFile.getAbsolutePath();
        // PackageParser packageParser = new PackageParser(archiveFilePath);
        // DisplayMetrics metrics = new DisplayMetrics();
        // metrics.setToDefaults();
        // PackageParser.Package pkg = packageParser.parsePackage(sourceFile,
        // archiveFilePath, metrics, 0);
        // if (pkg == null) {
        // return null;
        // }
        // if (!packageParser.collectManifestDigest(pkg)) {
        // return null;
        // }
        Object packageParser;
        Method method;
        PackageParser.Package pkg = null;
        Class clazz;
        clazz = Class.forName("android.content.pm.PackageParser");

        if (Build.VERSION.SDK_INT > 20) {
            // Android 5.0.1
            packageParser = clazz.getConstructor().newInstance();
            method = clazz.getMethod("parseMonolithicPackage", File.class, int.class);
            pkg = (PackageParser.Package) method.invoke(packageParser, sourceFile, 0);
        } else {
            packageParser = clazz.getConstructor(String.class).newInstance(sourceFile.getAbsolutePath());
            method = clazz.getMethod("parsePackage", File.class, String.class, DisplayMetrics.class, int.class);

            DisplayMetrics metrics = new DisplayMetrics();
            metrics.setToDefaults();
            pkg = (PackageParser.Package) method.invoke(packageParser, sourceFile, sourceFile.getAbsolutePath(),
                    metrics, 0);
        }
        return pkg;
    }

    public static boolean isPackageAlreadyInstalled(Context context, String pkgName) {
        List<PackageInfo> installedList = context.getPackageManager().getInstalledPackages(
                PackageManager.GET_UNINSTALLED_PACKAGES);
        int installedListSize = installedList.size();
        for (int i = 0; i < installedListSize; i++) {
            PackageInfo tmp = installedList.get(i);
            if (pkgName.equalsIgnoreCase(tmp.packageName)) {
                return true;
            }
        }
        return false;
    }

    static public class AppSnippet {
        CharSequence label;

        Drawable icon;

        public AppSnippet(CharSequence label, Drawable icon) {
            this.label = label;
            this.icon = icon;
        }
    }

    /**
     * Utility method to load application label
     * 
     * @param pContext context of package that can load the resources
     * @param appInfo ApplicationInfo object of package whose resources are to
     *            be loaded
     * @param snippetId view id of app snippet view
     */
    public static AppSnippet getAppSnippet(Activity pContext, ApplicationInfo appInfo, File sourceFile) {
        final String archiveFilePath = sourceFile.getAbsolutePath();
        Resources pRes = pContext.getResources();
        AssetManager assmgr = new AssetManager();
        assmgr.addAssetPath(archiveFilePath);
        Resources res = new Resources(assmgr, pRes.getDisplayMetrics(), pRes.getConfiguration());
        CharSequence label = null;
        // Try to load the label from the package's resources. If an app has not
        // explicitly
        // specified any label, just use the package name.
        if (appInfo.labelRes != 0) {
            try {
                label = res.getText(appInfo.labelRes);
            } catch (Resources.NotFoundException e) {
            }
        }
        if (label == null) {
            label = (appInfo.nonLocalizedLabel != null) ? appInfo.nonLocalizedLabel : appInfo.packageName;
        }
        Drawable icon = null;
        // Try to load the icon from the package's resources. If an app has not
        // explicitly
        // specified any resource, just use the default icon for now.
        if (appInfo.icon != 0) {
            try {
                icon = res.getDrawable(appInfo.icon);
            } catch (Resources.NotFoundException e) {
            }
        }
        if (icon == null) {
            icon = pContext.getPackageManager().getDefaultActivityIcon();
        }
        return new PackageUtil.AppSnippet(label, icon);
    }
}
