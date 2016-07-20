//EosTek Patch Begin

package com.android.providers.downloads;

import java.util.HashMap;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.TextUtils;
import android.util.Log;

/**
 * DownloadSettingProvider
 */
public class DownloadSettingProvider extends ContentProvider {
    /** Database filename */
    private static final String DB_NAME = "setting.db";

    private static final int DB_VERSION = 2;

    private static HashMap<String, String> SettingProjectionMap;

    private static final int SETTING = 1;

    private static final int SETTING_ID = 2;

    private static final UriMatcher sUriMatcher;

    private DatabaseHelper mOpenHelper;

    static {
        sUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        sUriMatcher.addURI(SettingColumns.AUTHORITY, "setting", SETTING);
        sUriMatcher.addURI(SettingColumns.AUTHORITY, "setting/#", SETTING_ID);

        SettingProjectionMap = new HashMap<String, String>();
        SettingProjectionMap.put(SettingColumns._ID, SettingColumns._ID);
        SettingProjectionMap.put(SettingColumns.SAVE, SettingColumns.SAVE);
        SettingProjectionMap.put(SettingColumns.TASK_NUMBER, SettingColumns.TASK_NUMBER);
        SettingProjectionMap.put(SettingColumns.SPEED_LIMITE, SettingColumns.SPEED_LIMITE);
        SettingProjectionMap.put(SettingColumns.IS_DATA_CACHE_DIR, SettingColumns.IS_DATA_CACHE_DIR);
    }

    @Override
    public boolean onCreate() {
        mOpenHelper = new DatabaseHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables(SettingColumns.TABLE_NAME);

        switch (sUriMatcher.match(uri)) {
            case SETTING:
                qb.setProjectionMap(SettingProjectionMap);
                break;

            case SETTING_ID:
                qb.setProjectionMap(SettingProjectionMap);
                qb.appendWhere(SettingColumns._ID + "=" + uri.getPathSegments().get(1));
                break;

            default:
                throw new IllegalArgumentException("query-Unknown URI " + uri);
        }

        // If no sort order is specified use the default

        // Get the database and run the query
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, null);

        // Tell the cursor what uri to watch, so it knows when its source data
        // changes
        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Override
    public String getType(Uri uri) {
        switch (sUriMatcher.match(uri)) {
            case SETTING:
                return SettingColumns.CONTENT_TYPE;
            case SETTING_ID:
                return SettingColumns.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("getType-Unknown URI " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        Uri insertUri = null;
        long rowid = 0;
        switch (sUriMatcher.match(uri)) {
            case SETTING_ID:
            case SETTING:
                rowid = db.insert(SettingColumns.TABLE_NAME, null, values);
                insertUri = ContentUris.withAppendedId(uri, rowid);
                Log.i("DownloadSettingProvider", "insert record...values:" + values.toString());
                break;
            default:
                throw new IllegalArgumentException("insert Unkwon Uri:" + uri.toString());
        }
        return insertUri;
    }

    @Override
    public int delete(Uri uri, String where, String[] whereArgs) {
        return 0;
    }

    @Override
    public int update(Uri uri, ContentValues values, String where, String[] whereArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count;
        switch (sUriMatcher.match(uri)) {
            case SETTING:
                count = db.update(SettingColumns.TABLE_NAME, values, where, whereArgs);
                break;

            case SETTING_ID:
                String noteId = uri.getPathSegments().get(1);
                count = db.update(SettingColumns.TABLE_NAME, values,
                        SettingColumns._ID + "=" + noteId + (!TextUtils.isEmpty(where) ? " AND (" + where + ')' : ""),
                        whereArgs);
                break;

            default:
                throw new IllegalArgumentException("update-Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    /**
     * save SettingColumns constant value.
     * 
     * @author albert
     */
    public static final class SettingColumns implements BaseColumns {

        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/setting";

        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/setting";

        public static final String AUTHORITY = "com.android.providers.downloads.setting";

        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/setting");

        public static final String TABLE_NAME = "setting";

        public static final String SAVE = "save_dir_path";

        public static final String TASK_NUMBER = "task_number";

        public static final String SPEED_LIMITE = "speed_limite";

        public static final String IS_DATA_CACHE_DIR = "is_data_cache";

    }

    private final class DatabaseHelper extends SQLiteOpenHelper {
        public DatabaseHelper(final Context context) {
            super(context, DB_NAME, null, DB_VERSION);
        }

        /**
         * Creates database the first time we try to open it.
         */
        @Override
        public void onCreate(final SQLiteDatabase db) {
            onUpgrade(db, 0, DB_VERSION);
        }

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            if (newVersion == DB_VERSION) {
                createSettingTable(db);
            }
        }

        private void createSettingTable(SQLiteDatabase db) {
            db.execSQL("DROP TABLE IF EXISTS " + SettingColumns.TABLE_NAME);
            db.execSQL("CREATE TABLE " + SettingColumns.TABLE_NAME + "(" + "id INTEGER PRIMARY KEY AUTOINCREMENT,"
                    + SettingColumns._ID + " INTEGER NOT NULL," + SettingColumns.SAVE + " TEXT NOT NULL,"
                    + SettingColumns.TASK_NUMBER + " INTEGER NOT NULL DEFAULT 3," + SettingColumns.SPEED_LIMITE
                    + " INTEGER NOT NULL DEFAULT 100000," + SettingColumns.IS_DATA_CACHE_DIR
                    + " INTEGER NOT NULL DEFAULT 1" + ");");
        }
    }

}
// EosTek Patch End
