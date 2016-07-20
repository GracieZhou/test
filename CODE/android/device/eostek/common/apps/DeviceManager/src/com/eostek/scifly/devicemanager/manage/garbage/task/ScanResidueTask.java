package com.eostek.scifly.devicemanager.manage.garbage.task;

import java.io.File;
import java.io.FileFilter;
import java.util.List;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Environment;

import com.eostek.scifly.devicemanager.FileCollection;
import com.eostek.scifly.devicemanager.BaseTask;
import com.eostek.scifly.devicemanager.BaseTaskListener;
import com.eostek.scifly.devicemanager.util.Constants;
import com.eostek.scifly.devicemanager.util.Debug;
import com.eostek.scifly.devicemanager.util.FileTool;
import com.eostek.scifly.devicemanager.util.PackageUtil;
import com.eostek.scifly.devicemanager.util.Util;

/**
 * This class can be used to scan all the uninstalled-garbage.
 * The result will be returned through {@link com.eostek.scifly.devicemanager.TaskListener}.
 * Callers just need to new an instance and then add into the engine as below:
 * 
 * ScanUninstallTask uninstallTask = new ScanUninstallTask(listener, context);
 * DeviceManager.getInstance().startTask(uninstallTask);
 * 
 * @author Psso.Song
 *
 */
public class ScanResidueTask extends BaseTask {
    private static final String TAG = ScanResidueTask.class.getSimpleName();
    private File databaseFile;
    private SQLiteDatabase database;

    public ScanResidueTask(final BaseTaskListener listener, final Context context) {
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

        //Some packages have already be uninstalled, but their data files may not be deleted.
        findUninstalledPackageData(collection);

        //open database filepath.db
        Debug.d(TAG, "ScanUninstallTask open database filepath.db...");
        database = SQLiteDatabase.openDatabase(
                databaseFile.getAbsolutePath(),
                null,
                SQLiteDatabase.OPEN_READONLY);

        findGarbageInDir(collection, Environment.getExternalStorageDirectory());

        //close this database
        Debug.d(TAG, "ScanUninstallTask close database filepath.db...");
        database.close();
    }

    @Override
    protected void handleCancelEvent() {
        super.handleCancelEvent();

        if (database.isOpen()) {
            Debug.d(TAG, "ScanUninstallTask Database is still open after task cancelled, close it now!");
            database.close();
        }
    }

    /**
     * Find data files of uninstalled packages.
     * @param collection {@link  com.eostek.scifly.devicemanager.FileCollection}
     */
    private void findUninstalledPackageData(FileCollection collection) throws TaskCancelledException {
        PackageManager pm = context.getPackageManager();
        List<ApplicationInfo> uninstalledList = pm.getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
        List<ApplicationInfo> installedList =  pm.getInstalledApplications(PackageManager.GET_META_DATA);
        boolean uninstall = true;
        for (ApplicationInfo uninstallInfo : uninstalledList) {
            for (ApplicationInfo installInfo : installedList) {
                if (uninstallInfo.packageName.equals(installInfo.packageName)) {
                    uninstall = false;
                    break;
                }
                if(uninstallInfo.packageName.contains(Constants.DEVICEMANAGER_PACKAGE_NAME)){
                    uninstall=false;
                    break;
                }
            }
            if (uninstall) {
                //This application is already be uninstalled.
                String datapath = Environment.getDataDirectory().getAbsolutePath()
                        .concat("/data/").concat(uninstallInfo.packageName);
                Debug.d(TAG, "Package has be uninstalled [" + datapath + "]");
                //never scan self application
                if(datapath.contains(Constants.DEVICEMANAGER_PACKAGE_NAME)){
                    return;
                }
                File file = new File(datapath);
                if (file.exists() && file.canRead() && file.canWrite()) {
                    collection.add(datapath);
                    listener.onProgress(collection);
                }
                
                String externalpath = Environment.getExternalStorageDirectory().getAbsolutePath()
                        .concat("/data/").concat(uninstallInfo.packageName);
                if(externalpath.contains(Constants.DEVICEMANAGER_PACKAGE_NAME)){
                    return;
                }
                Debug.d(TAG, "Package has be uninstalled [" + externalpath + "]");
                File externalfile = new File(externalpath);
                if (externalfile.exists() && externalfile.canRead() && externalfile.canWrite()) {
                    collection.add(externalpath);
                    listener.onProgress(collection);
                }
                uninstall = true;
            }

            checkTaskCanceled();
        }
    }

    /**
     * Find all garbage files leave by uninstalled packages.
     * @param collection {@link  com.eostek.scifly.devicemanager.FileCollection}
     * @param file Directory that need to be checked.
     */
    private void findGarbageInDir(FileCollection collection, File file) throws TaskCancelledException {
        if (!file.exists()) {
            Debug.d(TAG, "File not exist [" + file.getPath() + "]");
            return;
        }
        if (!file.canRead() || !file.canWrite()) {
            Debug.d(TAG, "File cannot access [" + file.getPath() + "]");
            return;
        }
        if (file.isFile()) {
            //There are only directories in filepath.db
            Debug.d(TAG, "No need to check a file [" + file.getAbsolutePath() + "]");
            return;
        }
        if(file.getAbsolutePath().contains(Constants.DEVICEMANAGER_PACKAGE_NAME)){
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
          //never scan self application
            if(tempfile.getAbsolutePath().contains(Constants.DEVICEMANAGER_PACKAGE_NAME)){
                continue;
            }
            if (Util.isSystemDir(context, tempfile)) {
                Debug.d(TAG, "System directory [" + tempfile.getAbsolutePath() + "]");
                findGarbageInDir(collection, tempfile);
            } else {
                //List all sub-file of tempfile from filepath.db
                String cmdStr = tempfile.getAbsolutePath().substring(Constants.DIR_EXTERNAL_ROOT_LENGTH + 1);
                if(cmdStr.contains(Constants.DEVICEMANAGER_PACKAGE_NAME)){
                    return;
                }
                Debug.d(TAG, "......cmdStr=" + cmdStr);
                Cursor c = database.rawQuery(Constants.QUERY_SOFTDETAIL_STR, new String[]{ "%" + cmdStr + "%",});
                while (c.moveToNext()) {
                    String apkname = c.getString(0);
                    String filepath = c.getString(1);
                    Debug.d(TAG, "Database(softdetail): apkname=" + apkname + "| filepath=" + filepath);
                    if (!filepath.startsWith("/")) {
                        filepath = "/".concat(filepath);
                    }
                    File checkFile = new File(Constants.DIR_EXTERNAL_ROOT.concat(filepath));
                    if (checkFile.exists() && checkFile.canRead() && checkFile.canWrite()
                            && !PackageUtil.isPackageAlreadyInstalled(context, apkname)
                            &&!checkFile.getAbsolutePath().contains(Constants.DEVICEMANAGER_PACKAGE_NAME)) {
                        Debug.d(TAG, "Find an uninstalled file [" + checkFile.getAbsolutePath() + "]");
                        collection.add(checkFile.getAbsolutePath());
                        listener.onProgress(collection);
                    }
                }
                c.close();
            }
        }

        checkTaskCanceled();
    }

}
