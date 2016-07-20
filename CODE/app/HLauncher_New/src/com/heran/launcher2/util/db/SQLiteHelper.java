
package com.heran.launcher2.util.db;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteDatabase.CursorFactory;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

/*
 * projectName： EosLauncher
 * moduleName： DBHelper.java
 *
 * @author chadm.xiang
 * @version 1.0.0
 * @time  2014-12-10 上午9:52:03
 * @Copyright © 2014 Eos Inc.
 */

public class SQLiteHelper extends SQLiteOpenHelper {

    public static final String TAG = SQLiteHelper.class.getSimpleName();

//    private static SQLiteHelper instance = null;
//    
//    private SQLiteHelper(Context context, String name, CursorFactory factory, int version) {
//        super(context, name, factory, version);
//    }
//    
//    public static synchronized SQLiteHelper getInstance(Context context, String name, CursorFactory factory, int version) {
//        if (instance == null ) {
//            instance = new SQLiteHelper(context, name, factory, version);
//        }
//        return instance;
//    }
    
    public SQLiteHelper(Context context, String name, CursorFactory factory, int version) {
        super(context, name, factory, version);
    }

    /*
     * (non-Javadoc)
     * @see
     * android.database.sqlite.SQLiteOpenHelper#onCreate(android.database.sqlite
     * .SQLiteDatabase)
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.v(TAG, "onCreate");
        db.execSQL("create table if not exists advertisement (_id integer primary key autoincrement," + "ad_type text,"
                + "title text," + "ad_size text," + "update_time text," + "pic_url text," + "ad_description text,"
                + "animation_type int," + "show_time int," + "cp_info text," + "ad_link text)");
        db.execSQL("create table if not exists application (_id integer primary key autoincrement," + "title text,"
                + "pic_url text," + "download_url text," + "classname text," + "packagename text)");
    }

    /*
     * (non-Javadoc)
     * @see
     * android.database.sqlite.SQLiteOpenHelper#onUpgrade(android.database.sqlite
     * .SQLiteDatabase, int, int)
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int arg1, int arg2) {
        Log.v(TAG, "onUpgrade");
        db.execSQL("drop table if exists advertisement");
        db.execSQL("drop table if exists application");
    }

}
