
package com.android.packageinstaller.blockinstallation.db;

import android.content.Context;

public class DBManager {

    private static final String TAG = DBManager.class.getSimpleName();

    private static DBManager mDbManager;

    private static DBHelper mDBHelper;

    private DBManager(Context context) {
        mDBHelper = new DBHelper(context);
    }

    public static DBManager getDBInstance(Context context) {
        if (mDbManager == null) {
            synchronized (DBManager.class) {
                if (mDbManager == null) {
                    mDbManager = new DBManager(context);
                }
            }
        }
        return mDbManager;
    }

    public long insertBlockPkg(String pkgname) {
        return mDBHelper.insertBlockPkg(pkgname);
    }

    public synchronized boolean isPkgNameExist(String pkgName) {
        return mDBHelper.isPkgNameExist(pkgName);
    }

    public long deleteBlockTableAll() {
        return mDBHelper.delete(DBHelper.BLOCK_TABLE_NAME, null, null);
    }
    public synchronized boolean isDataExist() {
        return mDBHelper.isDataExist();
    }
}
