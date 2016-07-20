
package com.eostek.tv.launcher.business.database;

import com.eostek.tv.launcher.model.JsonHeadBean;
import com.eostek.tv.launcher.model.MetroInfo;
import com.eostek.tv.launcher.model.MetroPage;
import com.eostek.tv.launcher.util.LConstants;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/**
 * projectName： TVLauncher moduleName： DBHelper.java
 * 
 * @author cloud.li
 * @version 1.0.0
 * @time 2014-7-20 上午10:57:42
 * @Copyright © 2014 Eos Inc.
 */

public class DBHelper extends SQLiteOpenHelper {

    public static final String TAG = DBHelper.class.getSimpleName();

    public static final String DB_NAME = "launcher.db";

    public static final int DB_VERSION = 1;

    public static final String PAGE_TABLE_NAME = "metropages";

    public static final String PAGE_COLUMN_ID = "_id";

    public static final String PAGE_COLUMN_TITLE = "Title";

    public static final String PAGE_COLUMN_APPCATEGORY = "AppCategory";

    public static final String PAGE_COLUMN_COUNTRY = "country";

    public static final String METROINFO_TABLE_NAME = "metroinfos";

    public static final String METROINFO_COLUMN_ID = "AppID";

    public static final String METROINFO_COLUMN_TYPE_TITLE = "TypeTitle";

    public static final String METROINFO_COLUMN_APP_TITLE = "AppTitle";

    public static final String METROINFO_COLUMN_CLASS_NAME = "ClassName";

    public static final String METROINFO_COLUMN_PACKAGE_NAME = "PkgName";

    public static final String METROINFO_COLUMN_POSITION_X = "Position_x";

    public static final String METROINFO_COLUMN_POSITION_Y = "Position_y";

    public static final String METROINFO_COLUMN_WIDTH = "Width";

    public static final String METROINFO_COLUMN_HEIGHT = "Height";

    public static final String METROINFO_COLUMN_APP_TYPE = "AppType";

    public static final String METROINFO_COLUMN_APPCATEGORY = "AppCategory";

    public static final String METROINFO_COLUMN_STR_FLAG = "StrFlag";

    public static final String METROINFO_COLUMN_INT_FLAG = "IntFlag";

    public static final String METROINFO_COLUMN_ICON_PATH_BACKGROUND = "IconPathB";

    public static final String METROINFO_COLUMN_ICON_PATH_FOREGROUD = "IconPathF";

    public static final String METROINFO_COLUMN_ICON_APP_URL = "AppURL";

    public static final String METROINFO_COLUMN_COUNTRY = "country";

    public static final String RESPONSE_HEAD_TABLE_NAME = "responsehead";

    public static final String RESPONCE_HEAD_COLUMN_ID = "_id";

    public static final String RESPONCE_HEAD_COLUMN_ERROR = "error";

    public static final String RESPONCE_HEAD_COLUMN_REFLECTION = "reflection";

    public static final String RESPONCE_HEAD_COLUMN_BACKGROUND = "background";

    public static final String RESPONCE_HEAD_COLUMN_LOGO = "logo";

    public static final String RESPONCE_HEAD_COLUMN_LOGO_X = "logoX";

    public static final String RESPONCE_HEAD_COLUMN_LOGO_Y = "logoY";

    public static final String RESPONCE_HEAD_COLUMN_COUNTRY = "country";

    public static final String RESPONCE_HEAD_COLUMN_ETAG = "etag";

    private SQLiteDatabase mDB = null;

    public DBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
        try {
            mDB = getWritableDatabase();
        } catch (SQLiteException e) {
            e.printStackTrace();
            Log.e("DBHelper", "getWritableDatabase error");
            mDB = null;
        }
    }

    public DBHelper(Context context, String name, CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.v(TAG, "onCreate");
        db.execSQL("CREATE TABLE IF NOT EXISTS metropages (_id INTEGER PRIMARY KEY AUTOINCREMENT," + "Title TEXT,"
                + "AppCategory INT,country TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS metroinfos (AppID INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "TypeTitle TEXT," + "AppTitle TEXT," + "ClassName TEXT," + "PkgName TEXT," + "Position_x INT,"
                + "Position_y INT," + "Width INT," + "Height INT," + "AppType INT," + "AppCategory INT,"
                + "StrFlag TEXT," + "IntFlag INT," + "IconPathB TEXT," + "IconPathF TEXT,"
                + "AppURL TEXT,country TEXT)");
        db.execSQL("CREATE TABLE IF NOT EXISTS responsehead (_id INTEGER PRIMARY KEY AUTOINCREMENT," + "error INT,"
                + "reflection INT," + "background TEXT," + "logo TEXT," + "logoX INT," + "logoY INT," + "country TEXT,"
                + "etag TEXT)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.v(TAG, "onUpgrade");
        db.execSQL("drop table if exists metropages");
        db.execSQL("drop table if exists metroinfos");
        db.execSQL("drop table if exists responsehead");
    }

    /**
     * insert the info object to table metroinfos
     * 
     * @param info
     * @return The id of item in db
     */
    public long insertMetroInfo(MetroInfo info) {
        long result = -1;
        ContentValues cv = new ContentValues();
        cv.put(METROINFO_COLUMN_TYPE_TITLE, info.getTypeTitle());
        cv.put(METROINFO_COLUMN_APPCATEGORY, info.getAppCategory());
        cv.put(METROINFO_COLUMN_APP_TITLE, info.getTitle());
        cv.put(METROINFO_COLUMN_PACKAGE_NAME, info.getPkgName());
        cv.put(METROINFO_COLUMN_CLASS_NAME, info.getClsName());
        cv.put(METROINFO_COLUMN_POSITION_X, info.getX());
        cv.put(METROINFO_COLUMN_POSITION_Y, info.getY());
        cv.put(METROINFO_COLUMN_WIDTH, info.getWidthSize());
        cv.put(METROINFO_COLUMN_HEIGHT, info.getHeightSize());
        cv.put(METROINFO_COLUMN_APP_TYPE, info.getItemType());
        cv.put(METROINFO_COLUMN_STR_FLAG, info.getExtraStrInfo());
        cv.put(METROINFO_COLUMN_INT_FLAG, info.getExtraIntInfo());
        cv.put(METROINFO_COLUMN_ICON_PATH_BACKGROUND, info.getIconPathB());
        cv.put(METROINFO_COLUMN_ICON_PATH_FOREGROUD, info.getIconPathF());
        cv.put(METROINFO_COLUMN_ICON_APP_URL, info.getApkUrl());
        cv.put(METROINFO_COLUMN_COUNTRY, info.getCounLang());
        result = mDB.insert(METROINFO_TABLE_NAME, null, cv);
        return result;
    }

    /**
     * insert the page data to db
     * 
     * @param page the data to insert to db
     * @return The id of insert record
     */
    public long insertMetroPage(MetroPage page) {
        long result = -1;
        ContentValues cv = new ContentValues();
        cv.put(PAGE_COLUMN_APPCATEGORY, page.getAppCategory());
        cv.put(PAGE_COLUMN_TITLE, page.getTitle());
        cv.put(PAGE_COLUMN_COUNTRY, page.getCounLang());
        result = mDB.insert(PAGE_TABLE_NAME, null, cv);
        return result;
    }

    /**
     * insert the JsonHeadBean data to db
     * 
     * @param bean the data to insert to db
     * @return The id of insert record
     */
    public long insertResponseHead(JsonHeadBean bean) {
        long result = -1;
        ContentValues cv = new ContentValues();
        cv.put(RESPONCE_HEAD_COLUMN_ERROR, bean.getResponse());
        cv.put(RESPONCE_HEAD_COLUMN_REFLECTION, bean.isHasReflection() ? 1 : 0);
        cv.put(RESPONCE_HEAD_COLUMN_BACKGROUND, bean.getBackgroundUrl());
        cv.put(RESPONCE_HEAD_COLUMN_LOGO, bean.getLogoUrl());
        cv.put(RESPONCE_HEAD_COLUMN_LOGO_X, bean.getLogoX());
        cv.put(RESPONCE_HEAD_COLUMN_LOGO_Y, bean.getLogoY());
        cv.put(RESPONCE_HEAD_COLUMN_COUNTRY, bean.getCounLang());
        cv.put(RESPONCE_HEAD_COLUMN_ETAG, bean.geteTag());
        result = mDB.insert(RESPONSE_HEAD_TABLE_NAME, null, cv);
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
    public Cursor query(String table, String[] columns, String selection, String[] selectionArgs, String orderBy) {
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
    public long delete(String table, String whereClause, String[] whereArgs) {
        return mDB.delete(table, whereClause, whereArgs);
    }

    /**
     * update data
     * 
     * @param table the table to update in
     * @param info the object to update
     * @param whereClause the optional WHERE clause to apply when updating.
     *            Passing null will update all rows.
     * @param whereArgs You may include ?s in the where clause, which will be
     *            replaced by the values from whereArgs. The values will be
     *            bound as Strings.
     * @return the number of rows affected
     */
    public long update(String table, MetroInfo info, String whereClause, String[] whereArgs) {
        ContentValues cv = new ContentValues();
        cv.put(METROINFO_COLUMN_TYPE_TITLE, info.getTypeTitle());
        cv.put(METROINFO_COLUMN_APPCATEGORY, info.getAppCategory());
        cv.put(METROINFO_COLUMN_APP_TITLE, info.getTitle());
        cv.put(METROINFO_COLUMN_PACKAGE_NAME, info.getPkgName());
        cv.put(METROINFO_COLUMN_CLASS_NAME, info.getClsName());
        cv.put(METROINFO_COLUMN_POSITION_X, info.getX());
        cv.put(METROINFO_COLUMN_POSITION_Y, info.getY());
        cv.put(METROINFO_COLUMN_WIDTH, info.getWidthSize());
        cv.put(METROINFO_COLUMN_HEIGHT, info.getHeightSize());
        cv.put(METROINFO_COLUMN_APP_TYPE, info.getItemType());
        cv.put(METROINFO_COLUMN_STR_FLAG, info.getExtraStrInfo());
        cv.put(METROINFO_COLUMN_INT_FLAG, info.getExtraIntInfo());
        cv.put(METROINFO_COLUMN_ICON_PATH_BACKGROUND, info.getIconPathB());
        cv.put(METROINFO_COLUMN_ICON_PATH_FOREGROUD, info.getIconPathF());
        cv.put(METROINFO_COLUMN_ICON_APP_URL, info.getApkUrl());
        cv.put(METROINFO_COLUMN_COUNTRY, info.getCounLang());
        return mDB.update(table, cv, whereClause, whereArgs);
    }

    /**
     * execute the given sql
     * 
     * @param sql the SQL statement to be executed. Multiple statements
     *            separated by semicolons are not supported.
     */
    public void execSQL(String sql) {
        mDB.execSQL(sql);
    }

    /**
     * delete all data in table
     * 
     * @since V1.2.1
     * @param country The country flag
     * @param tablename The table name
     * @return The count of delete record
     */
    private long emptyTableData(String tablename, String country) {
        if (isTableExist(tablename)) {
            String[] whereArgs = {
                country
            };
            String selections = METROINFO_COLUMN_COUNTRY + "=? ";
            return mDB.delete(tablename, selections, whereArgs);
        } else {
            mDB.execSQL("CREATE TABLE IF NOT EXISTS responsehead (_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + "error INT," + "reflection INT," + "background TEXT," + "logo TEXT," + "logoX INT,"
                    + "logoY INT," + "country TEXT," + "etag TEXT)");
            return -1;
        }
    }

    /**
     * delete the db data
     * 
     * @since V1.2.1
     * @param country The country flag
     * @return The count of delete data
     */
    public long emptyDBData(String country) {
        long t1 = emptyTableData(METROINFO_TABLE_NAME, country);
        long t2 = emptyTableData(PAGE_TABLE_NAME, country);
        long t3 = emptyTableData(RESPONSE_HEAD_TABLE_NAME, country);
        return (t1 + t2 + t3);
    }

    /**
     * close the db
     */
    public void closeDB() {
        if (mDB != null) {
            mDB.close();
        }
    }

    /**
     * get the total conut of data
     * 
     * @return int
     */
    public int getCount() {
        int i = 0;
        Cursor c = null;
        c = mDB.rawQuery("select count(*)as count from metroinfos", null);
        while (c.moveToNext()) {
            Log.d("sql", "c.getString(0):" + c.getString(0));
            i = Integer.parseInt(c.getString(0));
        }
        c.close();
        return i;
    }

    /**
     * get the total conut of data
     * 
     * @since V1.2.1
     * @param country The country flag
     * @return The count of metroinfos item
     */
    public int getCount(String country) {
        int i = 0;
        Cursor c = null;
        c = mDB.rawQuery("select count(*)as count from metroinfos where " + METROINFO_COLUMN_COUNTRY + "='" + country
                + "'", null);
        while (c.moveToNext()) {
            Log.d("sql", country + " c.getString(0):" + c.getString(0));
            i = Integer.parseInt(c.getString(0));
        }
        c.close();
        return i;
    }

    /**
     * get cursor which select all data from db
     * 
     * @return The cursor
     */
    public Cursor getMetroPageCursor() {
        Cursor cursor = mDB.rawQuery("select * from metropages", null);
        return cursor;
    }

    /**
     * get the given country metro page
     * 
     * @since V1.2.1
     * @param country The country flag
     * @return The cursor
     */
    public Cursor getMetroPageCursor(String country) {
        Cursor cursor = mDB.rawQuery("select * from metropages where " + METROINFO_COLUMN_COUNTRY + "='" + country
                + "'", null);
        return cursor;
    }

    /**
     * to find whether table exist in db
     * 
     * @param table The table to check
     * @return true is the table exist in db,else false
     */
    public boolean isTableExist(String table) {
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
    public boolean checkColumnExist(String tableName, String columnName) {
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
     * update the table in database, ,add support to different country,so add
     * country column in each table
     * 
     * @since V1.2.1
     */
    public void updateDatabase() {
        // 1.drop the table
        mDB.execSQL("drop table if exists metropages");
        mDB.execSQL("drop table if exists metroinfos");
        mDB.execSQL("drop table if exists responsehead");
        // 2.recreate the table
        mDB.execSQL("CREATE TABLE IF NOT EXISTS metropages (_id INTEGER PRIMARY KEY AUTOINCREMENT," + "Title TEXT,"
                + "AppCategory INT,country TEXT)");
        mDB.execSQL("CREATE TABLE IF NOT EXISTS metroinfos (AppID INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "TypeTitle TEXT," + "AppTitle TEXT," + "ClassName TEXT," + "PkgName TEXT," + "Position_x INT,"
                + "Position_y INT," + "Width INT," + "Height INT," + "AppType INT," + "AppCategory INT,"
                + "StrFlag TEXT," + "IntFlag INT," + "IconPathB TEXT," + "IconPathF TEXT,"
                + "AppURL TEXT,country TEXT)");
        mDB.execSQL("CREATE TABLE IF NOT EXISTS responsehead (_id INTEGER PRIMARY KEY AUTOINCREMENT," + "error INT,"
                + "reflection INT," + "background TEXT," + "logo TEXT," + "logoX INT," + "logoY INT," + "country TEXT,"
                + "etag TEXT)");
    }

    /**
     * get the country etag
     * 
     * @since V1.2.1
     * @param country The country flag
     * @return the country eTag,null if not exist
     */
    public String getCountryETag(String country) {
        String eTag = null;
        Cursor cursor = null;
        try {
            String sql = "SELECT * FROM " + RESPONSE_HEAD_TABLE_NAME + " where " + RESPONCE_HEAD_COLUMN_COUNTRY + "='"
                    + country + "'";
            Log.v(TAG, "getCountryETag sql = " + sql);
            cursor = mDB.rawQuery(sql, null);
            if (cursor != null && cursor.moveToNext()) {
                eTag = cursor.getString(cursor.getColumnIndex(RESPONCE_HEAD_COLUMN_ETAG));
            }
        } catch (Exception e) {
            Log.e(TAG, "checkColumnExists..." + e.getMessage());
        } finally {
            if (null != cursor && !cursor.isClosed()) {
                cursor.close();
            }
        }
        if (eTag == null) {
            eTag = LConstants.DEFAULT_ETAG;
        }
        return eTag;
    }

    /**
     * udpate the etag with the given country
     * 
     * @since V.1.2.1
     * @param country The country flag
     * @param eatg The etag to udpate
     */
    public void udpateCountryETag(String country, String eatg) {
        String sql = "update " + RESPONSE_HEAD_TABLE_NAME + " set " + RESPONCE_HEAD_COLUMN_ETAG + "=? where "
                + RESPONCE_HEAD_COLUMN_COUNTRY + "=?";
        mDB.execSQL(sql, new Object[] {
                eatg,country
        });
    }
}
