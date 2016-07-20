package com.eostek.scifly.devicemanager.manage.garbage.task;

import java.io.File;
import java.io.FileFilter;
import java.io.FilenameFilter;
import java.util.List;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;

import com.eostek.scifly.devicemanager.FileCollection;
import com.eostek.scifly.devicemanager.BaseTask;
import com.eostek.scifly.devicemanager.BaseTaskListener;
import com.eostek.scifly.devicemanager.util.Constants;
import com.eostek.scifly.devicemanager.util.Debug;
import com.eostek.scifly.devicemanager.util.Util;

public class ScanCacheTask extends BaseTask {

    private static final String TAG = ScanCacheTask.class.getSimpleName();
    
    public ScanCacheTask(final BaseTaskListener listener, final Context context) {
        super(listener, context);
    }

    @Override
    protected void execute() throws TaskCancelledException {
        super.execute();

        //add all files in /cache/log
        File log = new File(Constants.LOG_DIR);
        if (!log.exists()) {
            //file not exists or have no access
            Debug.d(TAG, "Can not find log file in dir [/cache/log]");
        }
        Debug.d(TAG, "Find log file in dir [" + log.getAbsolutePath() + "]");
        if (log.exists() && log.isDirectory() && log.canRead() && log.canWrite()) {
            File[] logFiles = log.listFiles();
            for (File tempfile : logFiles) {
                if (tempfile.exists() && tempfile.canRead() && tempfile.canWrite()) {
                    collection.add(tempfile.getAbsolutePath());
                    listener.onProgress(collection);
                }
            }
        }

        checkTaskCanceled();

        //add last_log.* files in /cache/recovery
        File recovery = new File(Constants.RECOVERY_LOG_DIR);
        Debug.d(TAG, "Find last_log.* file in dir [" + recovery.getAbsolutePath() + "]");
        if (recovery.exists() && recovery.isDirectory() && recovery.canRead() && recovery.canWrite()) {
            File[] logFiles = recovery.listFiles(new FilenameFilter() {

                @Override
                public boolean accept(File arg0, String arg1) {
                    if (arg1.toLowerCase().startsWith("last_log.")) {
                        return true;
                    }
                    return false;
                }
            });
            for (File tempfile : logFiles) {
                if (tempfile.exists() && tempfile.canRead() && tempfile.canWrite()) {
                    collection.add(tempfile.getAbsolutePath());
                    listener.onProgress(collection);
                }
            }
        }

        checkTaskCanceled();

        // add *.dmp files in SnplusCache

        File dump = null;
        String cachePath=Util.getSnplusCachePath();
        if (cachePath == null) {
            Debug.d(TAG, "the SnplusCachePath config not exist!");
        } else {
            dump = new File(cachePath.concat("/play"));
            Debug.d(TAG, "Find *.dmp file in dir [" + dump.getAbsolutePath() + "]");
            findDumpInDir(collection, dump);

            checkTaskCanceled();
        }

        //add cache file in installed package
        if (context != null) {
            List<PackageInfo> installedList = context.getPackageManager().getInstalledPackages(
                    PackageManager.GET_UNINSTALLED_PACKAGES);
            for (PackageInfo info : installedList) {
                findCacheInPkg(collection, info.packageName);
            }
        }
    }

    /**
     * Find all cache files in following dir:
     * 1./data/data/packageName/cache/
     * 2./mnt/sdcard/Android/packageName/cache/
     * 3./data/data/packageName/database/webview.db*
     * 4./data/data/packageName/database/webviewCache.db*
     * @param collection {@link com.eostek.scifly.devicemanager.FileCollection}
     * @param packageName Searching package name.
     */
    private void findCacheInPkg(FileCollection collection, String packageName) {
        String cacheDir = Environment.getDataDirectory().getAbsolutePath()
                             .concat("/data/").concat(packageName).concat("/cache");
        // never scan devicemanager cache
        if (cacheDir.contains(Constants.DEVICEMANAGER_PACKAGE_NAME)) {
            return;
        }
        addCache(collection, cacheDir);
        listener.onProgress(collection);

        String externalCacheDir = Environment.getExternalStorageDirectory().getAbsolutePath()
                .concat("/Android/").concat(packageName).concat("/cache");
        addCache(collection, externalCacheDir);
        listener.onProgress(collection);

        String webviewCacheDir = Environment.getDataDirectory().getAbsolutePath()
                .concat("/data/").concat(packageName).concat("/database");
        //Debug.d(TAG, "Add all webview cache file in [" + webviewCacheDir + "]");
        File webviewDir = new File(webviewCacheDir);
        if (webviewDir.exists() && webviewDir.canRead() && webviewDir.canWrite()
                && webviewDir.isDirectory()) {
            File[] files = webviewDir.listFiles(new FilenameFilter() {

                @Override
                public boolean accept(File arg0, String arg1) {
                    if (arg0.canRead() && arg0.canWrite()
                            && (arg1.toLowerCase().contains("webview.db")
                            || arg1.toLowerCase().contains("webviewcache.db"))) {
                        return true;
                    }
                    return false;
                }
            });
            for (File file : files) {
                collection.add(file.getAbsolutePath());
            }
        }
        listener.onProgress(collection);
    }

    /**
     * Add all files in @param dir except itself.
     * @param collection {@link com.eostek.scifly.devicemanager.FileCollection}
     * @param dir Searching directory name.
     */
    private void addCache(FileCollection collection, String dir) {
        //Debug.d(TAG, "Add all cache file in [" + dir + "]");
        File cacheDir = new File(dir);
        if (!cacheDir.exists() || !cacheDir.canRead() || !cacheDir.canWrite()
                || !cacheDir.isDirectory()) {
            return;
        }
        File[] files = cacheDir.listFiles(new FileFilter() {

            @Override
            public boolean accept(File arg0) {
                if (arg0.canRead() && arg0.canWrite()) {
                    return true;
                }
                return false;
            }
        });
        for (File file : files) {
            collection.add(file.getAbsolutePath());
        }
    }

    /**
     * Find all *.dmp files in @param file.
     * @param collection {@link com.eostek.scifly.devicemanager.FileCollection}
     * @param file Searching directory.
     */
    private void findDumpInDir(FileCollection collection, File file) {
        if (!file.exists()) {
            Debug.e(TAG, "File not exist [" + file.getPath() + "]");
            return;
        }
        if (!file.canRead() || !file.canWrite()) {
            Debug.e(TAG, "File cannot access [" + file.getPath() + "]");
            return;
        }
        Debug.e(TAG, "Checking dump in [" + file.getAbsolutePath() + "]");
        if (file.isFile() && file.getName().toLowerCase().endsWith(".dmp")) {
            collection.add(file.getAbsolutePath());
            listener.onProgress(collection);
        } else if (file.isDirectory()){
            File[] files = file.listFiles(new FilenameFilter() {

                @Override
                public boolean accept(File arg0, String arg1) {
                    if (arg0.isDirectory() || arg1.toLowerCase().endsWith(".dmp")) {
                        return true;
                    }
                    return false;
                }
            });
            for (File tempfile : files) {
                if (tempfile.isFile() && tempfile.canRead() && tempfile.canWrite()) {
                    collection.add(tempfile.getAbsolutePath());
                    listener.onProgress(collection);
                } else if (tempfile.isDirectory()) {
                    findDumpInDir(collection, tempfile);
                }
            }
        }
    }

}
