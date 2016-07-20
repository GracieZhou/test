
package com.android.packageinstaller.blockinstallation.db;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DBHelper {

    public static final String TAG = "DBHelper";

    public static final String DB_NAME = "block.db";

    public static final int DB_VERSION = 1;

    public static final String BLOCK_TABLE_NAME = "blockpkg";

    public static final String BLOCK_TABLE_COLUMN_ID = "_id";

    public static final String PAKNAME_TABLE_COLUMN_ID = "pkgname";

    private static SQLiteDatabase mDB;

    private static SQLiteHelper sqLiteHelper;

    public DBHelper(Context context) {
        super();
        sqLiteHelper = new SQLiteHelper(context, DB_NAME, null, DB_VERSION);
        if (sqLiteHelper.getWritableDatabase() == null) {
            Log.i(TAG, "sqLiteHelper.getWritableDatabase() == null.....");
            // String path = "/data/data/com.google.tv.eoslauncher";
            // File file = new File(path);

        }

        mDB = sqLiteHelper.getWritableDatabase();
    }

    public synchronized long insertBlockPkg(String pkgname) {
        Log.v(TAG, "insertBlockPkg");
        long result = -1;
        ContentValues values = new ContentValues();
        values.put(PAKNAME_TABLE_COLUMN_ID, pkgname);
        result = mDB.insert(BLOCK_TABLE_NAME, null, values);
        return result;
    }

    public synchronized long delete(String table, String whereClause, String[] whereArgs) {
        Log.v(TAG, "delete");
        return mDB.delete(table, whereClause, whereArgs);
    }

    public synchronized boolean isPkgNameExist(String pkgName) {
        Log.v(TAG, "pkgName");
        boolean isPkgNamet = false;
        String sql = "select count(*) from blockpkg where pkgname='" + pkgName + "'";
        Log.i("aaa", sql);
        Cursor c = mDB.rawQuery(sql, null);
        if (c != null) {
            if (c.moveToNext()) {
                int count = c.getInt(0);
                if (count > 0) {
                    isPkgNamet = true;
                }
            }
        }

        c.close();
        Log.v(TAG, pkgName + " is exist " + isPkgNamet);
        return isPkgNamet;
    }

    /**
     * 表中是否有數據
     * 
     * @param pkgName
     * @return
     */
    public synchronized boolean isDataExist() {
        Log.v(TAG, "pkgName");
        boolean isData = false;
        String sql = "select count(*) from blockpkg";
        Log.i("aaa", sql);
        Cursor c = mDB.rawQuery(sql, null);
        if (c != null) {
            if (c.moveToNext()) {
                int count = c.getInt(0);
                if (count > 0) {
                    isData = true;
                }
            }
        }

        c.close();
        Log.v(TAG, isData + " is exist " + isData);
        return isData;
    }

    /**
     * close the db
     */
    public static synchronized void closeDB() {
        Log.v(TAG, "closeDB");
        if (mDB != null) {
            mDB.close();
        }
    }
}
