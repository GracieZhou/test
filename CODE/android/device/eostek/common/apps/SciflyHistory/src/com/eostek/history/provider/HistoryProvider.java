
package com.eostek.history.provider;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.DatabaseUtils;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.TextUtils;

/**
 * Provider of history.
 */
public class HistoryProvider extends ContentProvider {
    private static final String TAG = "HistoryProvider";

    /**
     * Value of AUTHORITY .
     */
    public static final String AUTHORITY = "com.eostek.history";

    private Context mContext;

    private final static HashMap<String, String> mNotesProjectionMap = new HashMap<String, String>();

    private static final UriMatcher mUriMatcher = new UriMatcher(UriMatcher.NO_MATCH);

    // The incoming URI matches the main table URI pattern
    private static final int MAIN = 1;

    // The incoming URI matches the main table row ID URI pattern
    private static final int MAIN_ID = 2;

    // Handle to a new DatabaseHelper.
    private DatabaseHelper mOpenHelper;

    /**
     * Definition of the contract for the main table of our provider.
     */
    public static final class MainTable implements BaseColumns {

        // This class cannot be instantiated
        private MainTable() {
        }

        public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/main");

        public static final Uri CONTENT_ID_URI_BASE = Uri.parse("content://" + AUTHORITY + "/main/");

        public static final String CONTENT_TYPE = "vnd.android.cursor.dir/vnd.example.api-demos-throttle";

        public static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/vnd.example.api-demos-throttle";

        public static final String DEFAULT_SORT_ORDER = "data COLLATE LOCALIZED ASC";
    }

    /**
     * This class helps open, create, and upgrade the database file.
     */
    static class DatabaseHelper extends SQLiteOpenHelper {

        private static final String DATABASE_NAME = "loader_throttle.db";

        private static final int DATABASE_VERSION = 2;

        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        /**
         * Creates the underlying database with table name and column names
         * taken from the NotePad class.
         */
        @Override
        public void onCreate(SQLiteDatabase db) {
            db.execSQL("CREATE TABLE IF NOT EXISTS main (" + "_id INTEGER PRIMARY KEY," + "pkg TEXT," + "cls TEXT,"
                    + "data TEXT," + "date TEXT," + "display_name TEXT," + "time_in_millis INTEGER,"
                    + "category_name TEXT" + ");");
        }

        /**
         * Demonstrates that the provider must consider what happens when the
         * underlying datastore is changed. In this sample, the database is
         * upgraded the database by destroying the existing data. A real
         * application should upgrade the database in place.
         */
        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
            // Kills the table and existing data
            db.execSQL("DROP TABLE IF EXISTS notes");
            // Recreates the database with a new version
            onCreate(db);
        }
    }

    static {
        // Create and initialize URI matcher.
        mUriMatcher.addURI(AUTHORITY, "main", MAIN);
        mUriMatcher.addURI(AUTHORITY, "main/#", MAIN_ID);

        // Create and initialize projection map for all columns. This is
        // simply an identity mapping.
        mNotesProjectionMap.put(MainTable._ID, MainTable._ID);
        mNotesProjectionMap.put("pkg", "pkg");
        mNotesProjectionMap.put("cls", "cls");
        mNotesProjectionMap.put("data", "data");
        mNotesProjectionMap.put("date", "date");
        mNotesProjectionMap.put("display_name", "display_name");
        mNotesProjectionMap.put("time_in_millis", "time_in_millis");
        mNotesProjectionMap.put("category_name", "category_name");
    }

    @Override
    public boolean onCreate() {
        mContext = getContext();
        mOpenHelper = new DatabaseHelper(getContext());
        return true;
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        // Constructs a new query builder and sets its table name
        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        qb.setTables("main");

        switch (mUriMatcher.match(uri)) {
            case MAIN:
                // If the incoming URI is for main table.
                qb.setProjectionMap(mNotesProjectionMap);
                break;

            case MAIN_ID:
                // The incoming URI is for a single row.
                qb.setProjectionMap(mNotesProjectionMap);
                qb.appendWhere(MainTable._ID + "=?");
                selectionArgs = DatabaseUtils.appendSelectionArgs(selectionArgs, new String[] {
                    uri.getLastPathSegment()
                });
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        if (TextUtils.isEmpty(sortOrder)) {
            sortOrder = MainTable.DEFAULT_SORT_ORDER;
        }

        SQLiteDatabase db = mOpenHelper.getReadableDatabase();

        Cursor c = qb.query(db, projection, selection, selectionArgs, null /*
                                                                            * no
                                                                            * group
                                                                            */, null /*
                                                                                      * no
                                                                                      * filter
                                                                                      */, sortOrder);

        c.setNotificationUri(getContext().getContentResolver(), uri);
        return c;
    }

    @Override
    public String getType(Uri uri) {
        switch (mUriMatcher.match(uri)) {
            case MAIN:
                return MainTable.CONTENT_TYPE;
            case MAIN_ID:
                return MainTable.CONTENT_ITEM_TYPE;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues initialValues) {
        if (mUriMatcher.match(uri) != MAIN) {
            // Can only insert into to main URI.
            throw new IllegalArgumentException("Unknown URI " + uri);
        }

        ContentValues values;

        if (initialValues != null) {
            values = new ContentValues(initialValues);
        } else {
            values = new ContentValues();
        }

        fill(values);

        SQLiteDatabase db = mOpenHelper.getWritableDatabase();

        long rowId = db.insert("main", null, values);

        // If the insert succeeded, the row ID exists.
        if (rowId > 0) {
            Uri noteUri = ContentUris.withAppendedId(MainTable.CONTENT_ID_URI_BASE, rowId);
            getContext().getContentResolver().notifyChange(noteUri, null);
            return noteUri;
        }

        throw new SQLException("Failed to insert row into " + uri);
    }

    @Override
    public int delete(Uri uri, String where, String[] whereArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        String finalWhere;

        int count;

        switch (mUriMatcher.match(uri)) {
            case MAIN:
                // If URI is main table, delete uses incoming where clause and
                // args.
                count = db.delete("main", where, whereArgs);
                break;

            // If the incoming URI matches a single note ID, does the delete
            // based
            // on the
            // incoming data, but modifies the where clause to restrict it to
            // the
            // particular note ID.
            case MAIN_ID:
                // If URI is for a particular row ID, delete is based on
                // incoming
                // data but modified to restrict to the given ID.
                finalWhere = DatabaseUtils.concatenateWhere(MainTable._ID + " = " + ContentUris.parseId(uri), where);
                count = db.delete("main", finalWhere, whereArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    @Override
    public int update(Uri uri, ContentValues values, String where, String[] whereArgs) {
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count;
        String finalWhere;

        switch (mUriMatcher.match(uri)) {
            case MAIN:
                // If URI is main table, update uses incoming where clause and
                // args.
                count = db.update("main", values, where, whereArgs);
                break;

            case MAIN_ID:
                // If URI is for a particular row ID, update is based on
                // incoming
                // data but modified to restrict to the given ID.
                finalWhere = DatabaseUtils.concatenateWhere(MainTable._ID + " = " + ContentUris.parseId(uri), where);
                count = db.update("main", values, finalWhere, whereArgs);
                break;

            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }

    private void fill(ContentValues values) {
        if (!values.containsKey("data")) {
            values.put("data", "");
        }

        Calendar calendar = Calendar.getInstance();

        long time_in_millis = calendar.getTimeInMillis();
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date(time_in_millis));

        if (!values.containsKey("date")) {
            values.put("date", date);
        }

        if (!values.containsKey("time_in_millis")) {
            values.put("time_in_millis", time_in_millis);
        }
    }
}
