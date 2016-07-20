
package com.eostek.scifly.devicemanager.manage.garbage.task;

import java.io.File;
import java.io.FilenameFilter;

import android.content.Context;
import android.os.Environment;

import com.eostek.scifly.devicemanager.FileCollection;
import com.eostek.scifly.devicemanager.BaseTask;
import com.eostek.scifly.devicemanager.BaseTaskListener;
import com.eostek.scifly.devicemanager.util.Debug;

public class ScanBigFileTask extends BaseTask {
    private static final String TAG = ScanBigFileTask.class.getSimpleName();

    public ScanBigFileTask(BaseTaskListener listener, Context context, long size) {
        super(listener, context, size);
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
            //Debug.e(TAG, "File not exist [" + file.getPath() + "]");
            return;
        }
        if (!file.canRead() || !file.canWrite()) {
            Debug.e(TAG, "File cannot access [" + file.getPath() + "]");
            return;
        }
        //Debug.d(TAG, "Checking APK in [" + file.getAbsolutePath() + "]");
        if (isBigFile(file)) {
            Debug.d(TAG, "Find an big file [" + file.getAbsolutePath() + "]");
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
                    if (arg0.isFile() || arg0.isDirectory()) {
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
    private boolean isBigFile(File file) {
        if (!file.isFile()) {
            // It's not a file!
            return false;
        }
        if (file.length() >= size) {
            // It's a big file !
            return true;
        }
        return false;
    }

}
