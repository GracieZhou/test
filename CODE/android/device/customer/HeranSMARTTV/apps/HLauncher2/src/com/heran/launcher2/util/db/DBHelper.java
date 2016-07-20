
package com.heran.launcher2.util.db;

import java.io.File;

import com.heran.launcher2.advert.MyAD;
import com.heran.launcher2.apps.AppInfoBean;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.util.Log;

public class DBHelper {

    public static final String TAG = "DBHelper";

    public static final String DB_NAME = "launcher.db";

    public static final int DB_VERSION = 1;

    public static final String ADVERTISEMENT_TABLE_NAME = "advertisement";

    public static final String ADVERTISEMENT_TABLE_COLUMN_ID = "_id";

    /**
     * 1)homeAd.08 for home fragment picture ad 2)appStoreAd.08 for app fragment
     * picture ad 3)allAppAd.01 for all app activity picture ad 4)txtAd.05 for
     * text ad
     **/
    public static final String ADVERTISEMENT_COLUMN_TYPE = "ad_type";

    public static final String HOME_AD = "homeAd.08";

    public static final String APP_STORE_AD = "appStoreAd.08";

    public static final String ALL_APP_AD = "allAppAd.01";

    public static final String TEXT_AD = "txtAd.05";

    public static final String ADVERTISEMENT_COLUMN_TITLE = "title";

    public static final String ADVERTISEMENT_COLUMN_SIZE = "ad_size";

    public static final String ADVERTISEMENT_COLUMN_UPDATE_TIME = "update_time";

    public static final String ADVERTISEMENT_COLUMN_PICTURE_URL = "pic_url";

    public static final String ADVERTISEMENT_COLUMN_DESCRIPTION = "ad_description";

    public static final String ADVERTISEMENT_COLUMN_ANIMATION = "animation_type";

    public static final String ADVERTISEMENT_COLUMN_SHOW_TIME = "show_time";

    public static final String ADVERTISEMENT_COLUMN_CP = "cp_info";

    public static final String ADVERTISEMENT_COLUMN_LINK = "ad_link";

    public static final String APPLICATION_TABLE_NAME = "application";

    public static final String APPLICATION_COLUMN_ID = "_id";

    public static final String APPLICATION_COLUMN_TITLE = "title";

    public static final String APPLICATION_COLUMN_PIC_URL = "pic_url";

    public static final String APPLICATION_COLUMN_DOWNLOAD_URL = "download_url";

    public static final String APPLICATION_COLUMN_CLASS_NAME = "classname";

    public static final String APPLICATION_COLUMN_PACKAGE_NAME = "packagename";

    private static SQLiteDatabase mDB;

    private SQLiteHelper sqLiteHelper;

    public DBHelper(Context context) {
        super();
        sqLiteHelper = new SQLiteHelper(context, DB_NAME, null, DB_VERSION);
        if (sqLiteHelper.getWritableDatabase() == null) {
            Log.i(TAG, "sqLiteHelper.getWritableDatabase() == null.....");
            String path = "/data/data/com.heran.launcher2";
            File file = new File(path);
        }
        mDB = sqLiteHelper.getWritableDatabase();
    }

    /**
     * insert a record to database
     * 
     * @param object MyAD object
     * @return The id of the record
     */
    public synchronized long insertAdvertisement(MyAD object) {
        Log.v(TAG, "insertAdvertisement");
        long result = -1;
        ContentValues values = new ContentValues();
        // ad_type
        values.put(ADVERTISEMENT_COLUMN_TYPE, object.getType());
        // title
        values.put(ADVERTISEMENT_COLUMN_TITLE, object.getTi());
        // ad_size
        values.put(ADVERTISEMENT_COLUMN_SIZE, object.getSiz());
        // update_time
        values.put(ADVERTISEMENT_COLUMN_UPDATE_TIME, object.getUpd());
        // pic_url
        values.put(ADVERTISEMENT_COLUMN_PICTURE_URL, object.getPic());
        // ad_description
        values.put(ADVERTISEMENT_COLUMN_DESCRIPTION, object.getDsr());
        // animation_type
        values.put(ADVERTISEMENT_COLUMN_ANIMATION, object.getPlt());
        // show_time
        values.put(ADVERTISEMENT_COLUMN_SHOW_TIME, object.getDit());
        // cp_info
        values.put(ADVERTISEMENT_COLUMN_CP, object.getCpi());
        // ad_link
        values.put(ADVERTISEMENT_COLUMN_LINK, object.getGln());

        result = mDB.insert(ADVERTISEMENT_TABLE_NAME, null, values);
        return result;
    }

    /**
     * update MyAD object
     * 
     * @param object MyAD object
     * @return The update record id
     */
    public synchronized long udpateAdvertisement(MyAD object) {
        Log.v(TAG, "udpateAdvertisement");
        long result = -1;
        ContentValues values = new ContentValues();
        // ad_type
        values.put(ADVERTISEMENT_COLUMN_TYPE, object.getType());
        // title
        values.put(ADVERTISEMENT_COLUMN_TITLE, object.getTi());
        // ad_size
        values.put(ADVERTISEMENT_COLUMN_SIZE, object.getSiz());
        // update_time
        values.put(ADVERTISEMENT_COLUMN_UPDATE_TIME, object.getUpd());
        // pic_url
        values.put(ADVERTISEMENT_COLUMN_PICTURE_URL, object.getPic());
        // ad_description
        values.put(ADVERTISEMENT_COLUMN_DESCRIPTION, object.getDsr());
        // animation_type
        values.put(ADVERTISEMENT_COLUMN_ANIMATION, object.getPlt());
        // show_time
        values.put(ADVERTISEMENT_COLUMN_SHOW_TIME, object.getDit());
        // cp_info
        values.put(ADVERTISEMENT_COLUMN_CP, object.getCpi());
        // ad_link
        values.put(ADVERTISEMENT_COLUMN_LINK, object.getGln());

        String where = ADVERTISEMENT_TABLE_COLUMN_ID + "=" + object.getId();
        result = mDB.update(ADVERTISEMENT_TABLE_NAME, values, where, null);
        return result;
    }

    /**
     * insert a record to database
     * 
     * @param appInfo AppInfoBean appInfo
     * @return The id of the record
     */
    public synchronized long insertApplication(AppInfoBean appInfo) {
        Log.v(TAG, "insertApplication");
        long result = -1;
        ContentValues values = new ContentValues();
        values.put(APPLICATION_COLUMN_CLASS_NAME, appInfo.getClassName());
        values.put(APPLICATION_COLUMN_PACKAGE_NAME, appInfo.getPackageName());
        values.put(APPLICATION_COLUMN_TITLE, appInfo.getTitle());
        values.put(APPLICATION_COLUMN_DOWNLOAD_URL, appInfo.getDownloadUrl());
        values.put(APPLICATION_COLUMN_PIC_URL, appInfo.getPictureUrl());
        result = mDB.insert(APPLICATION_TABLE_NAME, null, values);
        return result;
    }

    /**
     * update AppInfoBean appInfo
     * 
     * @param appInfo AppInfoBean appInfo
     * @return The update record id
     */
    public synchronized long updateApplication(AppInfoBean appInfo) {
        Log.v(TAG, "updateApplication");
        long result = -1;
        ContentValues values = new ContentValues();
        values.put(APPLICATION_COLUMN_CLASS_NAME, appInfo.getClassName());
        values.put(APPLICATION_COLUMN_PACKAGE_NAME, appInfo.getPackageName());
        values.put(APPLICATION_COLUMN_TITLE, appInfo.getTitle());
        values.put(APPLICATION_COLUMN_DOWNLOAD_URL, appInfo.getDownloadUrl());
        values.put(APPLICATION_COLUMN_PIC_URL, appInfo.getPictureUrl());
        String where = ADVERTISEMENT_TABLE_COLUMN_ID + "=" + appInfo.getId();
        result = mDB.update(APPLICATION_TABLE_NAME, values, where, null);
        return result;
    }

    /**
     * query the data from DB
     * 
     * @param table The table name to compile the query against.
     * @param columns A list of which columns to return. Passing null will
     *            return all columns, which is discouraged to prevent reading
     *            data from storage that isn't going to be used.
     * @param selection A filter declaring which rows to return, formatted as an
     *            SQL WHERE clause (excluding the WHERE itself). Passing null
     *            will return all rows for the given table.
     * @param selectionArgs You may include ?s in selection, which will be
     *            replaced by the values from selectionArgs, in order that they
     *            appear in the selection. The values will be bound as Strings.
     * @param groupBy A filter declaring how to group rows, formatted as an SQL
     *            GROUP BY clause (excluding the GROUP BY itself). Passing null
     *            will cause the rows to not be grouped.
     * @return A {@link Cursor} object, which is positioned before the first
     *         entry. Note that {@link Cursor}s are not synchronized, see the
     *         documentation for more details.
     */
    public synchronized Cursor query(String table, String[] columns, String selection, String[] selectionArgs,
            String orderBy) {
        Log.v(TAG, "query");
        return mDB.query(table, columns, selection, selectionArgs, null, null, orderBy);
    }

    /**
     * deleting rows in the database
     * 
     * @param table the table to delete from
     * @param whereClause the optional WHERE clause to apply when deleting.
     *            Passing null will delete all rows.
     * @param whereArgs You may include ?s in the where clause, which will be
     *            replaced by the values from whereArgs. The values will be
     *            bound as Strings.
     * @return the number of rows affected if a whereClause is passed in, 0
     *         otherwise. To remove all rows and get a count pass "1" as the
     *         whereClause.
     */
    public synchronized long delete(String table, String whereClause, String[] whereArgs) {
        Log.v(TAG, "delete");
        return mDB.delete(table, whereClause, whereArgs);
    }

    /**
     * execute the given sql
     * 
     * @param sql the SQL statement to be executed. Multiple statements
     *            separated by semicolons are not supported.
     * @param bindArgs only byte[], String, Long and Double are supported in
     *            bindArgs.
     */
    public synchronized void execSQL(String sql, Object[] bindArgs) {
        Log.v(TAG, "execSQL");
        mDB.execSQL(sql, bindArgs);
    }

    /**
     * add column in table
     * 
     * @param table table name
     * @param column contain the column name and def,like name text
     */
    public synchronized void addColumn(String table, String column) {
        String sql = "alter table " + table + " add column " + column;
        Log.i(TAG, "addColumn sql = " + sql);
        mDB.execSQL(sql);
    }

    /**
     * to find whether table exist in db
     * 
     * @param table The table to check
     * @return true is the table exist in db,else false
     */
    public synchronized boolean isTableExist(String table) {
        Log.v(TAG, "isTableExist");
        boolean isTableExist = false;
        Cursor c = mDB.rawQuery("SELECT count(*) FROM sqlite_master WHERE type='table' AND name='" + table + "'", null);
        if (c.moveToNext()) {
            int count = c.getInt(0);
            if (count > 0) {
                isTableExist = true;
            }
        }
        c.close();
        Log.v(TAG, table + " is exist " + isTableExist);
        return isTableExist;
    }

    /**
     * to find out whether the column in the table,see more
     * {@link Cursor#getColumnIndex(String)}
     * 
     * @since V1.2.1
     * @param tableName the table name
     * @param columnName the column to query
     * @return true if exist ,else false
     */
    public synchronized boolean checkColumnExist(String tableName, String columnName) {
        Log.v(TAG, "checkColumnExist");
        boolean result = false;
        Cursor cursor = null;
        try {
            // query only one column
            cursor = mDB.rawQuery("SELECT * FROM " + tableName + " LIMIT 0", null);
            result = (cursor != null && cursor.getColumnIndex(columnName) != -1);
        } catch (Exception e) {
            Log.e(TAG, "checkColumnExists..." + e.getMessage());
        } finally {
            if (null != cursor && !cursor.isClosed()) {
                cursor.close();
            }
        }
        Log.v(TAG, columnName + " Column Exist");
        return result;
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
