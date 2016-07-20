
package com.eostek.tv.utils;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;

public class TvDBHelper extends SQLiteOpenHelper {
    public static String DB_NAME = "tv.db";

    public static int DB_VERSION = 1;

    public final static String AD_TABLE_NAME = "adinfo";

    public final static String AD_COLUMN_ID = "_id";

    public final static String AD_COLUMN_TITLE = "title";

    public final static String AD_COLUMN_SIZE = "size";

    public final static String AD_COLUMN_UPDATEDATE = "update_date";
    
    public final static String AD_COLUMN_UPDATETIME = "update_time";

    public final static String AD_COLUMN_PICTURE_URL = "pic_url";

    public final static String AD_COLUMN_DESCRITPTION = "description";
    
    public final static String AD_COLUMN_DISMISS_TIME = "dismiss_time";

    public final static String AD_COLUMN_PROGRAMME_ID = "programme_id";

    public final static String AD_COLUMN_SOURCE = "source";

    public final static String AD_COLUMN_WEBVIEW_URL = "webview_url";

    public final static String AD_COLUMN_POSITION_X = "pos_x";

    public final static String AD_COLUMN_POSITION_Y = "pos_y";

    public TvDBHelper(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    public TvDBHelper(Context context, String name, CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE IF NOT EXISTS adinfo (_id INTEGER PRIMARY KEY AUTOINCREMENT," 
                + AD_COLUMN_TITLE + " TEXT,"
                + AD_COLUMN_SIZE + " TEXT,"
                + AD_COLUMN_UPDATEDATE + " TEXT,"
                + AD_COLUMN_UPDATETIME + " TEXT,"
                + AD_COLUMN_PICTURE_URL + " TEXT NOT NULL,"
                + AD_COLUMN_DESCRITPTION + " TEXT,"
                + AD_COLUMN_DISMISS_TIME + " INTEGER,"
                + AD_COLUMN_PROGRAMME_ID + " INTEGER NOT NULL,"
                + AD_COLUMN_SOURCE + " TEXT NOT NULL,"
                + AD_COLUMN_WEBVIEW_URL + " TEXT NOT NULL,"
                + AD_COLUMN_POSITION_X + " INTEGER,"
                + AD_COLUMN_POSITION_Y + " INTEGER)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("drop table if exists adinfo");
    }
}
