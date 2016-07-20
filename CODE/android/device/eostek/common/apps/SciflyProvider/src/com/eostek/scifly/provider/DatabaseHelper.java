
package com.eostek.scifly.provider;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.text.TextUtils;
import com.eostek.scifly.provider.SciflyProvider.Message;
import com.eostek.scifly.provider.SciflyProvider.Msguser;

/**
 * The Helper Util to operate the databases.
 * 
 * @author charles.tai, Psso.Song, frank.zhang, troy.fang
 * @version database_version : 7
 */
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String TAG = "SciflyProvider";

    private static final boolean DBG = true;

    private static final String DATABASE_NAME = "scifly.db";

    private static final int DATABASE_VERSION = 10;

    private static final HashSet<String> VALID_TABLES = new HashSet<String>();

    // table for user footprints, such as login/logout etc. for H project
    private static final String TABLE_FOOTPRINTS = "footprints";

    // table for all application
    private static final String TABLE_GLOBAL = "global";

    private static final String TABLE_SECURITY = "security";

    private static final String TABLE_USER = "user";

    private static final String TABLE_MESSAGE = "message";

    private static final String TABLE_MSGUSER = "msguser";

    private static final String TABLE_STATISTICS = "statistics";

    private static final String TABLE_PKGUSAGE = "pkgusage";

    static {
        VALID_TABLES.add(TABLE_FOOTPRINTS);
        VALID_TABLES.add(TABLE_GLOBAL);
        VALID_TABLES.add(TABLE_SECURITY);
        VALID_TABLES.add(TABLE_USER);
        VALID_TABLES.add(TABLE_MESSAGE);
        VALID_TABLES.add(TABLE_MSGUSER);
        VALID_TABLES.add(TABLE_STATISTICS);
        VALID_TABLES.add(TABLE_PKGUSAGE);
    }

    // may be used for loading default value
    private Context mContext;

    /**
     * @param context {@link Context}
     */
    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        mContext = context;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        if (DBG) {
            Log.d(TAG, "create new database");
        }

        createFootprintsTable(db);
        createGlobalTable(db);
        createSecurityTable(db);
        createUserTable(db);
        createMessageTable(db);
        createMsguserTable(db);
        // may be load default data

        // Create a new table for SciflyStatistics on Database version 4.
        createStatisticsTable(db);
        // Create a new table for package usage on Database version 6.
        createPkgUsageTable(db);
        initDevice(db);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (DBG) {
            Log.d(TAG, "upgrade database from version " + oldVersion + " to " + newVersion);
        }

        if (oldVersion == 1) {
            createFootprintsTable(db);
            createMessageTable(db);
            createUserTable(db);
            createStatisticsTable(db);
            createPkgUsageTable(db);
        } else if (oldVersion == 2) {
            updateMessageTable(db);
            createStatisticsTable(db);
            createPkgUsageTable(db);
        } else if (oldVersion == 3) {
            createStatisticsTable(db);
            createMsguserTable(db);
            updateMessage2Msguser(db);
            createPkgUsageTable(db);
        } else if (oldVersion == 4) {
            createMsguserTable(db);
            updateMessage2Msguser(db);
            createPkgUsageTable(db);
        } else if (oldVersion == 5) {
            createPkgUsageTable(db);
        } else if (oldVersion == 6) {
            upgradeSecurityTable(db);
        } else if(oldVersion > 7) {
            updateFootprintsTable(db);
        }

        if (oldVersion <= 9) {
            initDevice(db);
        }
    }

    private void initDevice(SQLiteDatabase db) {
        if (DBG) {
            Log.d(TAG, "initDevice ");
        }
        Cursor cursor = null;
        ContentValues values = new ContentValues();
        db.beginTransaction();
        try {
            cursor = db.query(TABLE_GLOBAL, new String[] {
                "value"
            }, "key=?", new String[] {
                "device_name"
            }, null, null, null);
            if (cursor != null) {
                String devName = "";
                if (cursor.moveToFirst()) {
                    devName = cursor.getString(cursor.getColumnIndex("value"));
                }
                if (TextUtils.isEmpty(devName)) {
                    int num = (int) (Math.random() * 9000) + 1000;
                    devName = mContext.getResources().getString(R.string.device_name);
                    devName = String.format(devName, num);
                    values.put(SciflyProvider.Global.KEY, "device_name");
                    values.put(SciflyProvider.Global.VALUE, devName);
                }
            }
            db.insert(TABLE_GLOBAL, null, values);
            db.setTransactionSuccessful();
        } catch (SQLiteException e) {
            Log.w(TAG, "lookup error", e);
        } finally {
            if (cursor != null) {
                db.endTransaction();
                cursor.close();
            }
        }
    }

    /**
     * whether the table is valid.
     * 
     * @param name table name
     * @return true if contain this table, else false.
     */
    public static boolean isValidTable(String name) {
        return VALID_TABLES.contains(name);
    }

    private void createFootprintsTable(SQLiteDatabase db) {
        if (DBG) {
            Log.d(TAG, "createFootprintsTable");
        }

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FOOTPRINTS);
        db.execSQL("CREATE TABLE footprints ("
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "user TEXT,"
                + "data TEXT,"
                + "title TEXT,"
                + "thumbnail BLOB,"
                + "time INTEGER,"
                + "category INTEGER,"
                + "remark TEXT,"
                + "reserve TEXT"
                + ");");
    }
    
    private void updateFootprintsTable(SQLiteDatabase db) {
        if (DBG) {
            Log.d(TAG, "updateFootprintsTable");
        }

        //rename old table for backup
        db.execSQL("alter table footprints rename to _temp_footprints;");
        
        //create new table
        db.execSQL("CREATE TABLE footprints ("
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "user TEXT,"
                + "data TEXT,"
                + "title TEXT,"
                + "thumbnail BLOB,"
                + "time INTEGER,"
                + "category INTEGER,"
                + "remark TEXT,"
                + "reserve TEXT"
                + ");");
        
        //copy data to new table from old table
        db.execSQL("insert into footprints(_id, user, data, title, thumbnail, time, category, remark, reserve) select _id, user, data, title, thumbnail, time, category, remark, reserve from _temp_footprints");
        
        //delete old table
        db.execSQL("drop table if exists _temp_footprints");
    }

    private void createGlobalTable(SQLiteDatabase db) {
        if (DBG) {
            Log.d(TAG, "createGlobalTable");
        }

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GLOBAL);
        db.execSQL("CREATE TABLE global ("
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "key TEXT UNIQUE ON CONFLICT REPLACE,"
                + "value TEXT"
                + ");");
    }

    private void createSecurityTable(SQLiteDatabase db) {
        if (DBG) {
            Log.d(TAG, "createSecurity");
        }

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SECURITY);
        db.execSQL("CREATE TABLE security ("
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "package TEXT UNIQUE ON CONFLICT REPLACE,"
                + "message TEXT,"
                + "level INTEGER,"
                + "extra INTEGER"
                + ");");
    }

    private void upgradeSecurityTable(SQLiteDatabase db) {
        if (DBG) {
            Log.d(TAG, "upgradeSecurityTable");
        }

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_SECURITY);
        db.execSQL("CREATE TABLE security ("
                + "_id INTEGER PRIMARY KEY AUTOINCREMENT,"
                + "package TEXT UNIQUE ON CONFLICT REPLACE,"
                + "message TEXT,"
                + "level INTEGER,"
                + "extra INTEGER"
                + ");");
    }

    private void createUserTable(SQLiteDatabase db) {
        if (DBG) {
            Log.d(TAG, "createUser");
        }

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_USER);
        db.execSQL("CREATE TABLE user ("
               +  "_id INTEGER PRIMARY KEY AUTOINCREMENT,"
               +  "userId INTEGER,"
               + "name TEXT,"
               + "bonus INTEGER,"
               +  "coin INTEGER,"
               + "time INTEGER,"
               + "remark TEXT,"
               + "reserve TEXT"
               + ");");
    }

    private void createMessageTable(SQLiteDatabase db) {
        if (DBG) {
            Log.d(TAG, "createMessageCenterTable");
        }

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MESSAGE);
        db.execSQL("CREATE TABLE message ("
               + "_id INTEGER PRIMARY KEY AUTOINCREMENT,"
               + "userid TEXT,"
               + "title TEXT,"
               + "time DATETIME,"
               + "status INTEGER DEFAULT 0,"
               + "data TEXT,"
               + "extra TEXT,"
               + "category INTEGER,"
               + "reserve TEXT"
               + ");");
    }

    private void createMsguserTable(SQLiteDatabase db) {
        if (DBG) {
            Log.d(TAG, "createMsguserTable");
        }

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_MSGUSER);
        db.execSQL("CREATE TABLE msguser ("
               + "_id INTEGER PRIMARY KEY AUTOINCREMENT,"
               + "userid TEXT UNIQUE ON CONFLICT REPLACE,"
               + "nickname TEXT,"
               + "thumbnail TEXT,"
               + "blocked INTEGER DEFAULT 0,"
               + "source INTEGER"
               + ");");
    }

    private void createStatisticsTable(SQLiteDatabase db) {
        if (DBG) {
            Log.d(TAG, "createStatisticsTable");
        }

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_STATISTICS);
        db.execSQL("CREATE TABLE statistics ("
               + "_id INTEGER PRIMARY KEY AUTOINCREMENT,"
               + "pkgname TEXT,"
               + "activityname TEXT,"
               + "type INTEGER,"
               + "time DATETIME,"
               + "category TEXT,"
               + "key TEXT,"
               + "value TEXT,"
               + "params TEXT"
               + ");");
    }

    private void createPkgUsageTable(SQLiteDatabase db) {
        if (DBG) {
            Log.d(TAG, "createPkgUsageTable");
        }

        db.execSQL("DROP TABLE IF EXISTS " + TABLE_PKGUSAGE);
        db.execSQL("CREATE TABLE pkgusage ("
               + "_id INTEGER PRIMARY KEY AUTOINCREMENT,"
               + "pkgname TEXT,"
               + "time DATETIME"
               + ");");
    }

    private void updateMessage2Msguser(SQLiteDatabase db) {
        Cursor cursor = null;
        ContentValues values = new ContentValues();
        db.beginTransaction();
        try {
            cursor = db.query(TABLE_MESSAGE, new String[] {
                    "_id", "userid", "userinfo", "title", "time", "status", "data", "extra", "source", "category",
                    "reserve"
            }, null, null, null, null, null);
            if (cursor != null) {
                String userId = "";
                String nickName = "";
                String thumbnail = "";
                int source;
                while (cursor.moveToNext()) {
                    userId = cursor.getString(cursor.getColumnIndex("userid"));
                    source = cursor.getInt(cursor.getColumnIndex("source"));
                    thumbnail = SciflyProvider.splitJson(cursor.getString(cursor.getColumnIndex("userinfo")))[0];
                    nickName = SciflyProvider.splitJson(cursor.getString(cursor.getColumnIndex("userinfo")))[1];
                    values.put(Msguser.USERID, userId);
                    values.put(Msguser.NICKNAME, nickName);
                    values.put(Msguser.THUMBNAIL, thumbnail);
                    values.put(Msguser.SOURCE, source);
                    if (DBG) {
                        Log.d(TAG, "insert#value : " + values.toString());
                    }
                    db.insert(TABLE_MSGUSER, null, values);
                }
            }
            db.setTransactionSuccessful();
        } catch (SQLiteException e) {
            Log.w(TAG, "lookup error", e);
        } finally {
            if (cursor != null) {
                db.endTransaction();
                cursor.close();
            }
        }
    }

    private void updateMessageTable(SQLiteDatabase db) {
        Cursor cursor = null;
        ContentValues values = new ContentValues();
        db.beginTransaction();
        try {
            cursor = db.query(TABLE_MESSAGE, new String[] {
                    Message._ID, "userinfo", Message.DATA
            }, null, null, null, null, Message.DEFAULT_SORT_ORDER, null);
            if (cursor != null) {
                String startString = "{\"str2\":\"";
                String userInfo = "";
                String data = "";
                boolean isUserInfoUpdate = false;
                boolean isDataUpdate = false;
                if (DBG) {
                    Log.d(TAG, "cursor count : " + cursor.getCount());
                }
                while (cursor.moveToNext()) {
                    long id = cursor.getLong(cursor.getColumnIndex(Message._ID));
                    userInfo = cursor.getString(cursor.getColumnIndex("userinfo"));
                    data = cursor.getString(cursor.getColumnIndex(Message.DATA));
                    if (!userInfo.startsWith(startString)) {
                        String imgUrl = splitStr(userInfo)[0];
                        String user = splitStr(userInfo)[1];
                        userInfo = mergeStr(imgUrl, user);
                        values.put("userinfo", userInfo);
                        if (DBG) {
                            Log.d(TAG, "userInfo : " + userInfo);
                        }
                        isUserInfoUpdate = true;
                    }
                    if (!data.startsWith(startString)) {
                        String dataTmp = splitStr(data)[1];
                        String thumb = splitStr(data)[0];
                        data = mergeStr(thumb, dataTmp);
                        values.put(Message.DATA, data);
                        if (DBG) {
                            Log.d(data, "data : " + data);
                        }
                        isDataUpdate = true;
                    }
                    if (isUserInfoUpdate || isDataUpdate) {
                        if (DBG) {
                            Log.d(TAG, "update values....");
                        }
                        db.update(TABLE_MESSAGE, values, "_id=?", new String[] {
                            Long.toString(id)
                        });
                    }
                }
            }
            db.setTransactionSuccessful();
        } catch (SQLiteException e) {
            Log.w(TAG, "lookup error", e);
        } finally {
            if (cursor != null) {
                db.endTransaction();
                cursor.close();
            }
        }
    }

    private String[] splitStr(String str) {
        return str.split(",", 2);
    }

    private String mergeStr(String str1, String str2) {
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("str1", str1);
        map.put("str2", str2);
        JSONObject msgJson = new JSONObject();
        for (Map.Entry<String, String> entry : map.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            try {
                msgJson.put(key, value);
            } catch (JSONException e) {
                Log.e(TAG, e.getLocalizedMessage());
            }
        }
        return msgJson.toString();
    }
}
