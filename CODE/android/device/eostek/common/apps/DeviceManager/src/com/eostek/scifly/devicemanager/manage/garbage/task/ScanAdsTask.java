package com.eostek.scifly.devicemanager.manage.garbage.task;

import java.io.File;
import java.io.FileFilter;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import com.eostek.scifly.devicemanager.FileCollection;
import com.eostek.scifly.devicemanager.BaseTask;
import com.eostek.scifly.devicemanager.BaseTaskListener;
import com.eostek.scifly.devicemanager.util.Constants;
import com.eostek.scifly.devicemanager.util.Debug;
import com.eostek.scifly.devicemanager.util.FileTool;
import com.eostek.scifly.devicemanager.util.Util;

public class ScanAdsTask extends BaseTask {
    
    private static final String TAG = ScanAdsTask.class.getSimpleName();
    
    private File databaseFile;
    private SQLiteDatabase database;

    public ScanAdsTask(final BaseTaskListener listener, final Context context) {
        super(listener, context);
    }

    @Override
    protected void execute() throws TaskCancelledException {
        super.execute();

        databaseFile = FileTool.getDbFile(context);
        if (databaseFile == null) {
            Debug.e(TAG, "Database file missing [" + databaseFile.getAbsolutePath() + "]");
            return;
        }

        //open database filepath.db
        Debug.d(TAG, "ScanAdsTask open database filepath.db...");
        database = SQLiteDatabase.openDatabase(
                databaseFile.getAbsolutePath(),
                null,
                SQLiteDatabase.OPEN_READONLY);

        findGarbageInDir(collection, Environment.getExternalStorageDirectory());

        //close this database
        Debug.d(TAG, "ScanAdsTask close database filepath.db...");
        database.close();
    }

    @Override
    protected void handleCancelEvent() {
        super.handleCancelEvent();

        if (database.isOpen()) {
            Debug.d(TAG, "ScanAdsTask Database is still open after task cancelled, close it now!");
            database.close();
        }
    }

    /**
     * Find all garbage files leave by uninstalled packages.
     * @param collection {@link  com.eostek.scifly.devicemanager.FileCollection}
     * @param file Directory that need to be checked.
     */
    private void findGarbageInDir(FileCollection collection, File file) throws TaskCancelledException {
        if (!file.exists()) {
            Debug.e(TAG, "File not exist [" + file.getPath() + "]");
            return;
        }
        if (!file.canRead() || !file.canWrite()) {
            Debug.e(TAG, "File cannot access [" + file.getPath() + "]");
            return;
        }
        if (file.isFile()) {
            //There are only directories in filepath.db
            Debug.d(TAG, "No need to check a file [" + file.getAbsolutePath() + "]");
            return;
        }

        File[] files = file.listFiles(new FileFilter() {
            
            @Override
            public boolean accept(File arg0) {
                if (arg0.isDirectory()) {
                    return true;
                }
                return false;
            }
        });
        for (File tempfile : files) {
            if (Util.isSystemDir(context, tempfile)) {
                Debug.d(TAG, "System directory [" + tempfile.getAbsolutePath() + "]");
                findGarbageInDir(collection, tempfile);
            } else {
                //List all sub-file of tempfile from filepath.db
                String cmdStr = tempfile.getAbsolutePath().substring(Constants.DIR_EXTERNAL_ROOT_LENGTH + 1);
                Debug.d(TAG, "......cmdStr=" + cmdStr);
                Cursor c = database.rawQuery(Constants.QUERY_CACHE_STR, new String[]{ "%" + cmdStr + "%",});
                while (c.moveToNext()) {
                    String apkname = c.getString(0);
                    String filepath = c.getString(1);
                    Debug.d(TAG, "Database(cache): apkname=" + apkname + "| filepath=" + filepath);
                    if (!filepath.startsWith("/")) {
                        filepath = "/".concat(filepath);
                    }
                    File checkFile = new File(Constants.DIR_EXTERNAL_ROOT.concat(filepath));
                    if (checkFile.exists() && checkFile.canRead() && checkFile.canWrite()) {
                        if (checkFile.isFile()) {
                            Debug.d(TAG, "Find an ads file [" + checkFile.getAbsolutePath() + "]");
                            collection.add(checkFile.getAbsolutePath());
                            listener.onProgress(collection);
                        } else {
                            File[] checkFiles = checkFile.listFiles();
                            for (File delFile : checkFiles) {
                                if (delFile.exists() && delFile.canRead() && delFile.canWrite()) {
                                    Debug.d(TAG, "Find an ads file [" + checkFile.getAbsolutePath() + "]");
                                    collection.add(checkFile.getAbsolutePath());
                                    listener.onProgress(collection);
                                }
                            }
                        }
                    }
                }
                c.close();
            }
        }

        checkTaskCanceled();
    }

}
