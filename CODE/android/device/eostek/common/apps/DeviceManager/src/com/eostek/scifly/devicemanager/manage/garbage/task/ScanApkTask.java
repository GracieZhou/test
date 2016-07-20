
package com.eostek.scifly.devicemanager.manage.garbage.task;

import java.io.File;
import java.io.FilenameFilter;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.os.Environment;

import com.eostek.scifly.devicemanager.FileCollection;
import com.eostek.scifly.devicemanager.BaseTask;
import com.eostek.scifly.devicemanager.BaseTaskListener;
import com.eostek.scifly.devicemanager.util.Debug;
import com.eostek.scifly.devicemanager.util.FileTool;
import com.eostek.scifly.devicemanager.util.PackageUtil;

public class ScanApkTask extends BaseTask {

    private static final String TAG = ScanApkTask.class.getSimpleName();
    
    public ScanApkTask(BaseTaskListener listener, Context context) {
        super(listener, context);
    }

    @Override
    protected void execute() throws TaskCancelledException {
        super.execute();

        File dataDir = Environment.getDataDirectory();
        File storageDir = Environment.getExternalStorageDirectory();

        findApkInDir(collection, dataDir);
        findApkInDir(collection, storageDir);
    }

    private void findApkInDir(FileCollection collection, File file) throws TaskCancelledException {
        if (!file.exists()) {
            Debug.e(TAG, "File not exist [" + file.getPath() + "]");
            return;
        }
        if (!file.canRead() || !file.canWrite()) {
            Debug.e(TAG, "File cannot access [" + file.getPath() + "]");
            return;
        }
        //Debug.d(TAG, "Checking APK in [" + file.getAbsolutePath() + "]");
        if (isInvalidApk(file)) {
            Debug.d(TAG, "Find an invalid apk [" + file.getAbsolutePath() + "]");
            collection.add(file.getAbsolutePath());
            listener.onProgress(collection);
        } else if (file.isDirectory()) {
            File[] files = file.listFiles(new FilenameFilter() {

                @Override
                public boolean accept(File arg0, String name) {
                    if (name.equalsIgnoreCase("asec") || name.equalsIgnoreCase("secure")
                            || arg0.getAbsolutePath().toLowerCase().startsWith("/data/app")
                            || arg0.getAbsolutePath().toLowerCase().startsWith("/data/security")) {
                        return false;
                    }
                    if ((arg0.isFile() && FileTool.isFileApk(arg0)) || arg0.isDirectory()) {
                        return true;
                    }
                    return false;
                }
            });
            for (File tempfile : files) {
                findApkInDir(collection, tempfile);
            }
        }

        checkTaskCanceled();
    }

    /**
     * Check if @param file is an invalid apk. 1.It's a file. 2.Its name ends
     * with apk or apks. 3.It's broken or installed.
     * 
     * @param file file which needs to be checked.
     * @return true if file is an invalid apk, otherwise false.
     */
    private boolean isInvalidApk(File file) {
        if (!file.isFile()) {
            // It's not a file!
            return false;
        }
        if (!FileTool.isFileApk(file)) {
            // It's not an APK/APKS file!
            return false;
        }
        PackageManager pm = context.getPackageManager();
        ApplicationInfo applicationInfo = PackageUtil.getApplicationInfo(file, pm);
        if (applicationInfo == null) {
            // It may be a apk in downloading!
            return false;
        }
        if (PackageUtil.isPackageAlreadyInstalled(context, applicationInfo.packageName)) {
            // It's already installed!
            return true;
        }
        // It's an valid apk or apks file.
        return false;
    }

}
