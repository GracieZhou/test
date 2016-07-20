
package com.eostek.scifly.provider;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.HashMap;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import scifly.provider.SciflyStatistics;
import scifly.provider.SciflyStore;
import scifly.provider.metadata.Msg;
import android.app.StatusBarManager;
import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.os.Bundle;
import android.provider.BaseColumns;
import android.text.TextUtils;
import android.util.Log;

import com.eostek.scifly.provider.CommandManager.MsgServiceConnectedCallBack;

/**
 * Provider for operating the databases.
 * 
 * @author charles.tai
 */
public class SciflyProvider extends ContentProvider {

    private static final String TAG = "SciflyProvider";

    private static final boolean DBG = true;

    private static final int CONSTANT_1000 = 1000;

    private static final String TABLE_GLOBAL = "global";

    private static final String TABLE_FOOTPRINTS = "footprints";

    private static final String TABLE_SECURITY = "security";

    private static final String TABLE_USER = "user";

    private static final String TABLE_MESSAGE = "message";

    private static final String TABLE_MSGUSER = "msguser";

    private static final String TABLE_STATISTICS = "statistics";

    private static final String TABLE_PKGUSAGE = "pkgusage";

    private static final String AUTHORITY = "com.eostek.scifly.provider";

    private static final String CONTENT_TYPE = "vnd.android.cursor.dir/scifly.provider";

    private static final String CONTENT_ITEM_TYPE = "vnd.android.cursor.item/scifly.provider";

    private static final int GLOBAL_ALL = 1;

    private static final int GLOBAL_ITEM = 2;

    private static final int FOOTPRINTS_ALL = 3;

    private static final int FOOTPRINTS_ITEM = 4;

    private static final int SECURITY_ALL = 5;

    private static final int SECURITY_ITEM = 6;

    private static final int USER_ALL = 7;

    private static final int USER_ITEM = 8;

    private static final int MESSAGE_ALL = 9;

    private static final int MESSAGE_ITEM = 10;

    private static final int MESSAGE_ITEM_DISTINCT = 11;

    private static final int STATISTICS_ALL = 12;

    private static final int STATISTICS_ITEM = 13;

    private static final int MSGUSER_ALL = 14;

    private static final int MSGUSER_ITEM = 15;

    private static final int PKGUSAGE_ALL = 16;

    private static final int PKGUSAGE_ITEM = 17;

    private static final UriMatcher URI_MATCHER;

    private static HashMap<String, String> mProjectionMap;

    private CommandManager mCommand;

    static {
        URI_MATCHER = new UriMatcher(UriMatcher.NO_MATCH);
        URI_MATCHER.addURI(AUTHORITY, "global", GLOBAL_ALL);
        URI_MATCHER.addURI(AUTHORITY, "global/#", GLOBAL_ITEM);

        URI_MATCHER.addURI(AUTHORITY, "footprints", FOOTPRINTS_ALL);
        URI_MATCHER.addURI(AUTHORITY, "footprints/#", FOOTPRINTS_ITEM);

        URI_MATCHER.addURI(AUTHORITY, "security", SECURITY_ALL);
        URI_MATCHER.addURI(AUTHORITY, "security/#", SECURITY_ITEM);

        URI_MATCHER.addURI(AUTHORITY, "user", USER_ALL);
        URI_MATCHER.addURI(AUTHORITY, "user/#", USER_ITEM);

        URI_MATCHER.addURI(AUTHORITY, "message", MESSAGE_ALL);
        URI_MATCHER.addURI(AUTHORITY, "message/#", MESSAGE_ITEM);
        URI_MATCHER.addURI(AUTHORITY, "messages", MESSAGE_ITEM_DISTINCT);

        URI_MATCHER.addURI(AUTHORITY, "statistics", STATISTICS_ALL);
        URI_MATCHER.addURI(AUTHORITY, "statistics/#", STATISTICS_ITEM);

        URI_MATCHER.addURI(AUTHORITY, "msguser", MSGUSER_ALL);
        URI_MATCHER.addURI(AUTHORITY, "msguser/#", MESSAGE_ITEM);

        URI_MATCHER.addURI(AUTHORITY, "pkgusage", PKGUSAGE_ALL);
        URI_MATCHER.addURI(AUTHORITY, "pkgusage/#", PKGUSAGE_ITEM);

        mProjectionMap = new HashMap<String, String>();
        mProjectionMap.put(Global._ID, Global._ID);
        mProjectionMap.put(Global.KEY, Global.KEY);
        mProjectionMap.put(Global.VALUE, Global.VALUE);

        mProjectionMap.put(Footprints._ID, Footprints._ID);
        mProjectionMap.put(Footprints.USER, Footprints.USER);
        mProjectionMap.put(Footprints.DATA, Footprints.DATA);
        mProjectionMap.put(Footprints.TITLE, Footprints.TITLE);
        mProjectionMap.put(Footprints.TIME, Footprints.TIME);
        mProjectionMap.put(Footprints.CATEGORY, Footprints.CATEGORY);
        mProjectionMap.put(Footprints.RESERVE, Footprints.RESERVE);
        mProjectionMap.put(Footprints.REMARK, Footprints.REMARK);
        mProjectionMap.put(Footprints.THUMBNAIL, Footprints.THUMBNAIL);

        mProjectionMap.put(Security._ID, Security._ID);
        mProjectionMap.put(Security.PACKAGE, Security.PACKAGE);
        mProjectionMap.put(Security.MESSAGE, Security.MESSAGE);
        mProjectionMap.put(Security.LEVEL, Security.LEVEL);

        mProjectionMap.put(User._ID, User._ID);
        mProjectionMap.put(User.ID, User.ID);
        mProjectionMap.put(User.NAME, User.NAME);
        mProjectionMap.put(User.BONUS, User.BONUS);
        mProjectionMap.put(User.COIN, User.COIN);
        mProjectionMap.put(User.TIME, User.TIME);
        mProjectionMap.put(User.REMARK, User.REMARK);
        mProjectionMap.put(User.RESERVE, User.RESERVE);

        mProjectionMap.put(Message._ID, Message._ID);
        mProjectionMap.put(Message.USERID, Message.USERID);
        mProjectionMap.put(Message.TITLE, Message.TITLE);
        mProjectionMap.put(Message.TIME, Message.TIME);
        mProjectionMap.put(Message.STATUS, Message.STATUS);
        mProjectionMap.put(Message.DATA, Message.DATA);
        mProjectionMap.put(Message.EXTRA, Message.EXTRA);
        mProjectionMap.put(Message.CATEGORY, Message.CATEGORY);
        mProjectionMap.put(Message.RESERVE, Message.RESERVE);

        mProjectionMap.put(Msguser._ID, Msguser._ID);
        mProjectionMap.put(Msguser.USERID, Msguser.USERID);
        mProjectionMap.put(Msguser.NICKNAME, Msguser.NICKNAME);
        mProjectionMap.put(Msguser.THUMBNAIL, Msguser.THUMBNAIL);
        mProjectionMap.put(Msguser.SOURCE, Msguser.SOURCE);
        mProjectionMap.put(Msguser.BLOCKED, Msguser.BLOCKED);

        mProjectionMap.put(Statistics._ID, Statistics._ID);
        mProjectionMap.put(Statistics.PKG_NAME, Statistics.PKG_NAME);
        mProjectionMap.put(Statistics.ACTIVITY_NAME, Statistics.ACTIVITY_NAME);
        mProjectionMap.put(Statistics.TYPE, Statistics.TYPE);
        mProjectionMap.put(Statistics.TIME, Statistics.TIME);
        mProjectionMap.put(Statistics.CATEGORY, Statistics.CATEGORY);
        mProjectionMap.put(Statistics.KEY, Statistics.KEY);
        mProjectionMap.put(Statistics.VALUE, Statistics.VALUE);
        mProjectionMap.put(Statistics.PARAMS, Statistics.PARAMS);

        mProjectionMap.put(PkgUsage._ID, PkgUsage._ID);
        mProjectionMap.put(PkgUsage.PKG_NAME, PkgUsage.PKG_NAME);
        mProjectionMap.put(PkgUsage.TIME, PkgUsage.TIME);
    }

    private static final Bundle NULL_SETTING;
    static {
        NULL_SETTING = new Bundle(1);
        NULL_SETTING.putString("value", null);
    }

    private DatabaseHelper mOpenHelper;

    private static int mStatisticsCount = 0;

    private static int mStatisticsBatch = SciflyStatistics.DEFAULT_STATISTICS_BATCH_COUNT;

    /**
     * offer field for table global.
     */
    static final class Global implements BaseColumns {

        static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + TABLE_GLOBAL);

        static final String TABLE_NAME = TABLE_GLOBAL;

        static final String DEFAULT_SORT_ORDER = "_id desc";

        static final String KEY = "key";

        static final String VALUE = "value";

    }

    /**
     * offer field for table footprints.
     */
    static final class Footprints implements BaseColumns {

        static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + TABLE_FOOTPRINTS);

        static final String TABLE_NAME = TABLE_FOOTPRINTS;

        static final String DEFAULT_SORT_ORDER = "time desc";

        static final String USER = "user";

        static final String DATA = "data";

        static final String THUMBNAIL = "thumbnail";

        static final String TITLE = "title";

        static final String TIME = "time";

        static final String CATEGORY = "category";

        static final String REMARK = "remark";

        static final String RESERVE = "reserve";

    }

    /**
     * offer field for table security.
     */
    static final class Security implements BaseColumns {

        static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + TABLE_SECURITY);

        static final String TABLE_NAME = TABLE_SECURITY;

        static final String PACKAGE = "package";

        static final String MESSAGE = "message";

        static final String LEVEL = "level";

        private static final String DEFAULT_SORT_ORDER = "_id desc";

    }

    /**
     * offer field for table user.
     */
    static final class User implements BaseColumns {

        static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + TABLE_USER);

        static final String TABLE_NAME = TABLE_USER;

        static final String DEFAULT_SORT_ORDER = "time desc";

        static final String ID = "userId";

        static final String NAME = "name";

        static final String BONUS = "bonus";

        static final String COIN = "coin";

        static final String TIME = "time";

        static final String REMARK = "remark";

        static final String RESERVE = "reserve";

        private static final String USER_GUEST = "guest";
    }

    /**
     * offer field for table message.
     */
    static final class Message implements BaseColumns {

        static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + TABLE_MESSAGE);

        static final String TABLE_NAME = TABLE_MESSAGE;

        static final String DEFAULT_SORT_ORDER = "time desc";

        static final String USERID = "userid";

        static final String TITLE = "title";

        static final String TIME = "time";

        static final String STATUS = "status";

        static final String DATA = "data";

        static final String EXTRA = "extra";

        static final String CATEGORY = "category";

        static final String RESERVE = "reserve";

        private static final String[] PROJECTION_DEFAULT = {
                _ID, USERID, TITLE, TIME, STATUS, DATA, EXTRA, CATEGORY, RESERVE
        };
    }

    /**
     * offer user info for table msguser.
     */
    static final class Msguser implements BaseColumns {

        static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + TABLE_MSGUSER);

        static final String TABLE_NAME = TABLE_MSGUSER;

        static final String DEFAULT_SORT_ORDER = "_id desc";

        static final String USERID = "userid";

        static final String NICKNAME = "nickname";

        static final String THUMBNAIL = "thumbnail";

        static final String BLOCKED = "blocked";

        static final String SOURCE = "source";

        private static final String[] PROJECTION_DEFAULT = {
                _ID, USERID, NICKNAME, THUMBNAIL, BLOCKED, SOURCE,
        };
    }

    /**
     * @author Psso.Song offer field for table statistics
     */
    static final class Statistics implements BaseColumns {

        static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + TABLE_STATISTICS);

        static final String TABLE_NAME = TABLE_STATISTICS;

        static final String DEFAULT_SORT_ORDER = "time desc";

        static final String PKG_NAME = "pkgname";

        static final String ACTIVITY_NAME = "activityname";

        static final String TYPE = "type";

        static final String TIME = "time";

        static final String CATEGORY = "category";

        static final String KEY = "key";

        static final String VALUE = "value";

        static final String PARAMS = "params";

    }

    /**
     * @author Psso.Song offer field for table pkgusage
     */
    static final class PkgUsage implements BaseColumns {

        static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + TABLE_PKGUSAGE);

        static final String TABLE_NAME = TABLE_PKGUSAGE;

        static final String DEFAULT_SORT_ORDER = "time desc";

        static final String PKG_NAME = "pkgname";

        static final String TIME = "time";

    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        if (DBG) {
            Log.d(TAG, "delete#uri : " + uri.toString());
        }
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count = 0;
        boolean needNotifyChange = true;
        switch (URI_MATCHER.match(uri)) {
            case GLOBAL_ALL:
                count = db.delete(Global.TABLE_NAME, selection, selectionArgs);
                break;
            case GLOBAL_ITEM:
                String globalId = uri.getPathSegments().get(1);
                count = db.delete(Global.TABLE_NAME, Global._ID + "=" + globalId
                        + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
                break;
            case FOOTPRINTS_ALL:
                count = db.delete(Footprints.TABLE_NAME, selection, selectionArgs);
                break;
            case FOOTPRINTS_ITEM:
                String footprintsId = uri.getPathSegments().get(1);
                count = db.delete(Footprints.TABLE_NAME,
                        Footprints._ID + "=" + footprintsId
                                + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
                break;
            case SECURITY_ALL:
                count = db.delete(Security.TABLE_NAME, selection, selectionArgs);
                break;
            case SECURITY_ITEM:
                String securityId = uri.getPathSegments().get(1);
                count = db.delete(Security.TABLE_NAME, Security._ID + "=" + securityId
                        + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
                break;
            case USER_ALL:
                count = db.delete(User.TABLE_NAME, selection, selectionArgs);
                break;
            case USER_ITEM:
                String userId = uri.getPathSegments().get(1);
                count = db.delete(User.TABLE_NAME, User._ID + "=" + userId
                        + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
                break;
            case MESSAGE_ALL:
            case MESSAGE_ITEM_DISTINCT:
                count = db.delete(Message.TABLE_NAME, selection, selectionArgs);
                break;
            case MESSAGE_ITEM:
                String messageId = uri.getPathSegments().get(1);
                count = db.delete(Message.TABLE_NAME, Message._ID + "=" + messageId
                        + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
                break;
            case MSGUSER_ALL:
                count = db.delete(Msguser.TABLE_NAME, selection, selectionArgs);
                break;
            case MSGUSER_ITEM:
                String msguserId = uri.getPathSegments().get(1);
                count = db.delete(Msguser.TABLE_NAME, Msguser._ID + "=" + msguserId
                        + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
                break;
            case STATISTICS_ALL:
                needNotifyChange = false;
                count = db.delete(Statistics.TABLE_NAME, selection, selectionArgs);
                break;
            case STATISTICS_ITEM:
                needNotifyChange = false;
                String statisticsId = uri.getPathSegments().get(1);
                count = db.delete(Statistics.TABLE_NAME,
                        Statistics._ID + "=" + statisticsId
                                + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
                break;
            case PKGUSAGE_ALL:
                count = db.delete(PkgUsage.TABLE_NAME, selection, selectionArgs);
                break;
            case PKGUSAGE_ITEM:
                String pkgusageId = uri.getPathSegments().get(1);
                count = db.delete(PkgUsage.TABLE_NAME,
                        PkgUsage._ID + "=" + pkgusageId
                                + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        if (needNotifyChange) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return count;
    }

    @Override
    public String getType(Uri uri) {
        if (DBG) {
            Log.d(TAG, "getType#uri : " + uri.toString());
        }
        switch (URI_MATCHER.match(uri)) {
            case GLOBAL_ALL:
            case FOOTPRINTS_ALL:
            case SECURITY_ALL:
            case USER_ALL:
            case STATISTICS_ALL:
            case PKGUSAGE_ALL:
                return CONTENT_TYPE;
            case MESSAGE_ALL:
            case MESSAGE_ITEM_DISTINCT:
            case MSGUSER_ALL:
            case GLOBAL_ITEM:
            case FOOTPRINTS_ITEM:
            case SECURITY_ITEM:
            case USER_ITEM:
            case STATISTICS_ITEM:
            case PKGUSAGE_ITEM:
                return CONTENT_ITEM_TYPE;
            case MESSAGE_ITEM:
            case MSGUSER_ITEM:
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
    }

    @Override
    public Uri insert(Uri uri, ContentValues values) {
        if (DBG) {
            Log.d(TAG, "insert#uri : " + uri.toString());
        }
        ContentValues value;
        if (values != null) {
            value = new ContentValues(values);
        } else {
            value = new ContentValues();
        }
        Msg message = new Msg();
        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        String table = "";
        switch (URI_MATCHER.match(uri)) {
            case GLOBAL_ALL:
                table = Global.TABLE_NAME;
                // Make sure that the fields are all set
                if (!value.containsKey(Global.KEY)) {
                    value.put(Global.KEY, "");
                }
                if (!value.containsKey(Global.VALUE)) {
                    value.put(Global.VALUE, "");
                }
                String key = value.getAsString(Global.KEY);
                if (SciflyStore.Global.STATISTICS_BATCH.equals(key)) {
                    mStatisticsBatch = value.getAsInteger(Global.VALUE);
                    if (DBG) {
                        Log.d(TAG, "insert GLOBAL_ALL: mStatisticsBatch=" + mStatisticsBatch);
                    }
                }
                break;
            case FOOTPRINTS_ALL:
                table = Footprints.TABLE_NAME;
                // Make sure that the fields are all set
                if (!value.containsKey(Footprints.USER)) {
                    value.put(Footprints.USER, "");
                }
                if (!value.containsKey(Footprints.DATA)) {
                    value.put(Footprints.DATA, "");
                }
                if (!value.containsKey(Footprints.TITLE)) {
                    value.put(Footprints.TITLE, "");
                }
                if (!value.containsKey(Footprints.TIME)) {
                    Calendar calendar = Calendar.getInstance();
                    long millis = calendar.getTimeInMillis();
                    value.put(Footprints.TIME, millis);
                }
                if (!value.containsKey(Footprints.CATEGORY)) {
                    value.put(Footprints.CATEGORY, 0);
                }
                if (!value.containsKey(Footprints.REMARK)) {
                    value.put(Footprints.REMARK, "");
                }
                if (!value.containsKey(Footprints.RESERVE)) {
                    value.put(Footprints.RESERVE, "");
                }
                if (!value.containsKey(Footprints.THUMBNAIL)) {
                    value.putNull(Footprints.THUMBNAIL);
                }
                break;
            case SECURITY_ALL:
                table = Security.TABLE_NAME;
                // Make sure that the fields are all set
                if (!value.containsKey(Security.PACKAGE)) {
                    value.put(Security.PACKAGE, "");
                }
                if (!value.containsKey(Security.MESSAGE)) {
                    value.put(Security.MESSAGE, "");
                }
                if (!value.containsKey(Security.LEVEL)) {
                    value.put(Security.LEVEL, 0);
                }
                break;
            case USER_ALL:
                table = User.TABLE_NAME;
                db.delete(table, null, null);
                if (!value.containsKey(User.ID)) {
                    value.put(User.ID, 0);
                }
                if (!value.containsKey(User.NAME)) {
                    value.put(User.NAME, User.USER_GUEST);
                }
                if (!value.containsKey(User.BONUS)) {
                    value.put(User.BONUS, 0);
                }
                if (!value.containsKey(User.COIN)) {
                    value.put(User.COIN, 0);
                }
                if (!value.containsKey(User.TIME)) {
                    Calendar calendar = Calendar.getInstance();
                    long millis = calendar.getTimeInMillis();
                    value.put(Footprints.TIME, millis);
                }
                if (!value.containsKey(User.REMARK)) {
                    value.put(User.REMARK, "");
                }
                if (!value.containsKey(User.RESERVE)) {
                    value.put(User.RESERVE, "");
                }
                break;
            case MESSAGE_ALL:
            case MESSAGE_ITEM_DISTINCT:
                table = Message.TABLE_NAME;
                // Make sure that the fields are all set
                if (!value.containsKey(Message.USERID)) {
                    value.put(Message.USERID, "");
                } else {
                    message.mUserId = value.getAsString(Message.USERID);
                }
                if (!value.containsKey(Message.TITLE)) {
                    value.put(Message.TITLE, "");
                } else {
                    message.mTitle = value.getAsString(Message.TITLE);
                }
                if (!value.containsKey(Message.TIME)) {
                    Calendar calendar = Calendar.getInstance();
                    long millis = calendar.getTimeInMillis();
                    value.put(Message.TIME, millis / CONSTANT_1000);
                } else {
                    message.mTime = value.getAsLong(Message.TIME);
                }
                if (!value.containsKey(Message.STATUS)) {
                    value.put(Message.STATUS, "");
                } else {
                    message.mStatus = value.getAsInteger(Message.STATUS);
                }
                if (!value.containsKey(Message.DATA)) {
                    value.put(Message.DATA, "");
                } else {
                    message.mData = splitJson(value.getAsString(Message.DATA))[1];
                    message.mThumb = splitJson(value.getAsString(Message.DATA))[0];
                }
                if (!value.containsKey(Message.EXTRA)) {
                    value.put(Message.EXTRA, "");
                } else {
                    message.mExtra = value.getAsString(Message.EXTRA);
                }
                if (!value.containsKey(Message.CATEGORY)) {
                    value.put(Message.CATEGORY, "");
                } else {
                    message.mCategory = value.getAsInteger(Message.CATEGORY);
                }
                if (!value.containsKey(Message.RESERVE)) {
                    value.put(Message.RESERVE, "");
                } else {
                    message.mReserve = value.getAsString(Message.RESERVE);
                }
                message.mUserInfo = value.getAsString(Msguser.NICKNAME);
                message.mImgUrl = value.getAsString(Msguser.THUMBNAIL);
                message.mBlocked = value.getAsInteger(Msguser.BLOCKED);
                message.mSource = value.getAsInteger(Msguser.SOURCE);
                value.remove(Msguser.NICKNAME);
                value.remove(Msguser.THUMBNAIL);
                value.remove(Msguser.BLOCKED);
                value.remove(Msguser.SOURCE);
                break;
            case MSGUSER_ALL:
                table = Msguser.TABLE_NAME;
                // Make sure that the fields are all set
                if (!value.containsKey(Msguser.USERID)) {
                    value.put(Msguser.USERID, "");
                } else {
                    message.mUserId = value.getAsString(Msguser.USERID);
                }
                if (!value.containsKey(Msguser.NICKNAME)) {
                    value.put(Msguser.NICKNAME, "");
                } else {
                    message.mUserInfo = value.getAsString(Msguser.NICKNAME);
                }
                if (!value.containsKey(Msguser.THUMBNAIL)) {
                    value.put(Msguser.THUMBNAIL, "");
                } else {
                    message.mImgUrl = value.getAsString(Msguser.THUMBNAIL);
                }
                if (!value.containsKey(Msguser.BLOCKED)) {
                    value.put(Msguser.BLOCKED, "");
                } else {
                    message.mBlocked = value.getAsInteger(Msguser.BLOCKED);
                }
                if (!value.containsKey(Msguser.SOURCE)) {
                    value.put(Msguser.SOURCE, "");
                } else {
                    message.mSource = value.getAsInteger(Msguser.SOURCE);
                }
                break;
            case STATISTICS_ALL:
                mStatisticsCount++;
                if (DBG) {
                    Log.d(TAG, "insert STATISTICS_ALL: mStatisticsBatch=" + mStatisticsBatch + ", mStatisticsCount="
                            + mStatisticsCount);
                }
                if (mStatisticsBatch <= 0) {
                    mStatisticsBatch = SciflyStatistics.DEFAULT_STATISTICS_BATCH_COUNT;
                }
                if (mStatisticsCount >= mStatisticsBatch) {
                    Log.d(TAG, "mStatisticsCount = " + mStatisticsCount + ", we should trigger upload now!");
                    getContext().getContentResolver().notifyChange(uri, null);
                    mStatisticsCount = 0;
                }
                table = Statistics.TABLE_NAME;
                // Make sure that the fields are all set
                if (!value.containsKey(Statistics.PKG_NAME)) {
                    value.put(Statistics.PKG_NAME, "");
                }
                if (!value.containsKey(Statistics.ACTIVITY_NAME)) {
                    value.put(Statistics.ACTIVITY_NAME, "");
                }
                if (!value.containsKey(Statistics.TYPE)) {
                    value.put(Statistics.TYPE, -1);
                }
                if (!value.containsKey(Statistics.TIME)) {
                    value.put(Statistics.TIME, System.currentTimeMillis());
                }
                if (!value.containsKey(Statistics.CATEGORY)) {
                    value.put(Statistics.CATEGORY, "");
                }
                if (!value.containsKey(Statistics.KEY)) {
                    value.put(Statistics.KEY, "");
                }
                if (!value.containsKey(Statistics.VALUE)) {
                    value.put(Statistics.VALUE, "");
                }
                if (!value.containsKey(Statistics.PARAMS)) {
                    value.put(Statistics.PARAMS, "");
                }
                break;
            case PKGUSAGE_ALL:
                table = PkgUsage.TABLE_NAME;
                // Make sure that the fields are all set
                if (!value.containsKey(PkgUsage.PKG_NAME)) {
                    value.put(PkgUsage.PKG_NAME, "");
                }
                if (!value.containsKey(PkgUsage.TIME)) {
                    value.put(PkgUsage.TIME, System.currentTimeMillis());
                }
                break;
            default:
                // Validate the requested uri
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        if (DBG) {
            Log.d(TAG, "insert#value : " + value.toString());
        }

        long rowId = db.insert(table, null, value);
        switch (URI_MATCHER.match(uri)) {
            case GLOBAL_ALL:
                setIMState(values);
                break;
            case MESSAGE_ALL:
            case MESSAGE_ITEM_DISTINCT:
                checkMsgCommand(false, message);
                setMessageState(db, values);
                break;
            default:
                break;
        }
        if (rowId > 0) {
            Uri noteUri = ContentUris.withAppendedId(uri, rowId);
            if (!TABLE_STATISTICS.equals(table)) {
                getContext().getContentResolver().notifyChange(noteUri, null);
            }

            return noteUri;
        }

        throw new SQLException("Failed to insert row into " + uri);
    }

    @Override
    public boolean onCreate() {
        if (DBG) {
            Log.d(TAG, "onCreate");
        }
        mOpenHelper = new DatabaseHelper(getContext());
        if (mOpenHelper == null) {
            Log.w(TAG, "oho, mOpenHelper is null?");
            return false;
        }
        // Init mStatisticsPolicy&mStatisticsParam
        initStatistics();

        checkMsgCommand(true, null);
        return true;
    }

    private void initStatistics() {
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        Cursor c = db.rawQuery("select value from global where key=?", new String[] {
            SciflyStore.Global.STATISTICS_BATCH
        });
        if (c != null && c.getCount() == 1) {
            c.moveToFirst();
            String param = c.getString(0);
            if (param == null) {
                mStatisticsBatch = SciflyStatistics.DEFAULT_STATISTICS_BATCH_COUNT;
            } else {
                try {
                    mStatisticsBatch = Integer.valueOf(param);
                } catch (NumberFormatException e) {
                    mStatisticsBatch = SciflyStatistics.DEFAULT_STATISTICS_BATCH_COUNT;
                    e.printStackTrace();
                }
            }
            Log.d(TAG, "initStatistics, mStatisticsBatch=" + mStatisticsBatch);
        } else {
            Log.d(TAG, "initStatistics, mStatisticsBatch not set!");
            mStatisticsBatch = SciflyStatistics.DEFAULT_STATISTICS_BATCH_COUNT;
        }

        // Close cursor
        c.close();
    }

    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        if (DBG) {
            StringBuffer sb = new StringBuffer();
            sb.append("query#uri[");
            sb.append(uri);
            sb.append("]");

            sb.append(", projection[");
            if (projection != null) {
                for (String item : projection) {
                    sb.append(item);
                    sb.append(", ");
                }
            }
            sb.append("]");

            sb.append(", selection[");
            if (selection != null) {
                sb.append(selection);
            }
            sb.append("]");

            sb.append(", selectionArgs[");
            if (selectionArgs != null) {
                for (String item : selectionArgs) {
                    sb.append(item);
                    sb.append(", ");
                }
            }
            sb.append("]");

            sb.append(", sortOrder[");
            if (sortOrder != null) {
                sb.append(sortOrder);
            }
            sb.append("]");

            Log.d(TAG, sb.toString());
        }

        SQLiteQueryBuilder qb = new SQLiteQueryBuilder();
        String orderBy = "";
        switch (URI_MATCHER.match(uri)) {
            case GLOBAL_ALL:
            case GLOBAL_ITEM:
                qb.setTables(Global.TABLE_NAME);
                // If no sort order is specified use the default
                if (TextUtils.isEmpty(sortOrder)) {
                    orderBy = Global.DEFAULT_SORT_ORDER;
                } else {
                    orderBy = sortOrder;
                }
                break;
            case FOOTPRINTS_ALL:
            case FOOTPRINTS_ITEM:
                qb.setTables(Footprints.TABLE_NAME);
                // If no sort order is specified use the default
                if (TextUtils.isEmpty(sortOrder)) {
                    orderBy = Footprints.DEFAULT_SORT_ORDER;
                } else {
                    orderBy = sortOrder;
                }
                break;
            case SECURITY_ALL:
            case SECURITY_ITEM:
                qb.setTables(Security.TABLE_NAME);
                // If no sort order is specified use the default
                if (TextUtils.isEmpty(sortOrder)) {
                    orderBy = Security.DEFAULT_SORT_ORDER;
                } else {
                    orderBy = sortOrder;
                }
                break;
            case USER_ALL:
            case USER_ITEM:
                qb.setTables(User.TABLE_NAME);
                if (TextUtils.isEmpty(sortOrder)) {
                    orderBy = User.DEFAULT_SORT_ORDER;
                } else {
                    orderBy = sortOrder;
                }
                break;
            case MESSAGE_ALL:
            case MESSAGE_ITEM:
                qb.setTables(Message.TABLE_NAME);
                if (TextUtils.isEmpty(sortOrder)) {
                    orderBy = Message.DEFAULT_SORT_ORDER;
                } else {
                    orderBy = sortOrder;
                }
                break;
            case MESSAGE_ITEM_DISTINCT:
                if (DBG) {
                    Log.d(TAG, "item distinct.");
                }

                SQLiteDatabase db = mOpenHelper.getReadableDatabase();
                Cursor cursor = db.rawQuery("select distinct(userid) from " + TABLE_MESSAGE, null);
                return cursor;
            case MSGUSER_ALL:
            case MSGUSER_ITEM:
                qb.setTables(Msguser.TABLE_NAME);
                if (TextUtils.isEmpty(sortOrder)) {
                    orderBy = Msguser.DEFAULT_SORT_ORDER;
                } else {
                    orderBy = sortOrder;
                }
                break;
            case STATISTICS_ALL:
            case STATISTICS_ITEM:
                qb.setTables(Statistics.TABLE_NAME);
                if (TextUtils.isEmpty(sortOrder)) {
                    orderBy = Statistics.DEFAULT_SORT_ORDER;
                } else {
                    orderBy = sortOrder;
                }
                break;
            case PKGUSAGE_ALL:
            case PKGUSAGE_ITEM:
                qb.setTables(PkgUsage.TABLE_NAME);
                if (TextUtils.isEmpty(sortOrder)) {
                    orderBy = PkgUsage.DEFAULT_SORT_ORDER;
                } else {
                    orderBy = sortOrder;
                }
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        switch (URI_MATCHER.match(uri)) {
            case GLOBAL_ALL:
            case FOOTPRINTS_ALL:
            case SECURITY_ALL:
            case USER_ALL:
            case MESSAGE_ALL:
            case MSGUSER_ALL:
            case MESSAGE_ITEM_DISTINCT:
            case STATISTICS_ALL:
            case PKGUSAGE_ALL:
                qb.setProjectionMap(mProjectionMap);
                break;
            case GLOBAL_ITEM:
                qb.setProjectionMap(mProjectionMap);
                qb.appendWhere(Global._ID + "=" + uri.getPathSegments().get(1));
                break;
            case FOOTPRINTS_ITEM:
                qb.setProjectionMap(mProjectionMap);
                qb.appendWhere(Footprints._ID + "=" + uri.getPathSegments().get(1));
                break;
            case SECURITY_ITEM:
                qb.setProjectionMap(mProjectionMap);
                qb.appendWhere(Security._ID + "=" + uri.getPathSegments().get(1));
                break;
            case USER_ITEM:
                qb.setProjectionMap(mProjectionMap);
                qb.appendWhere(User._ID + "=" + uri.getPathSegments().get(1));
                break;
            case MESSAGE_ITEM:
                qb.setProjectionMap(mProjectionMap);
                qb.appendWhere(Message._ID + "=" + uri.getPathSegments().get(1));
                break;
            case MSGUSER_ITEM:
                qb.setProjectionMap(mProjectionMap);
                qb.appendWhere(Msguser._ID + "=" + uri.getPathSegments().get(1));
                break;
            case STATISTICS_ITEM:
                qb.setProjectionMap(mProjectionMap);
                qb.appendWhere(Statistics._ID + "=" + uri.getPathSegments().get(1));
                break;
            case PKGUSAGE_ITEM:
                qb.setProjectionMap(mProjectionMap);
                qb.appendWhere(PkgUsage._ID + "=" + uri.getPathSegments().get(1));
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }

        // Get the database and run the query
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        Cursor c = qb.query(db, projection, selection, selectionArgs, null, null, orderBy);

        // Tell the cursor what uri to watch, so it knows when its source data
        // changes
        c.setNotificationUri(getContext().getContentResolver(), uri);

        return c;
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[] selectionArgs) {
        if (DBG) {
            Log.d(TAG, "update#uri : " + uri.toString());
        }

        SQLiteDatabase db = mOpenHelper.getWritableDatabase();
        int count = 0;
        boolean needNotifyChange = true;
        switch (URI_MATCHER.match(uri)) {
            case GLOBAL_ALL:
                count = db.update(Global.TABLE_NAME, values, selection, selectionArgs);
                String key = values.getAsString(Global.KEY);
                if (SciflyStore.Global.STATISTICS_BATCH.equals(key)) {
                    mStatisticsBatch = values.getAsInteger(Global.VALUE);
                    if (DBG) {
                        Log.d(TAG, "update GLOBAL_ALL: mStatisticsBatch=" + mStatisticsBatch);
                    }
                }
                break;
            case GLOBAL_ITEM:
                String globalId = uri.getPathSegments().get(1);
                count = db
                        .update(Global.TABLE_NAME, values, Global._ID + "=" + globalId
                                + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
                String keyItem = values.getAsString(Global.KEY);
                if (SciflyStore.Global.STATISTICS_BATCH.equals(keyItem)) {
                    mStatisticsBatch = values.getAsInteger(Global.VALUE);
                    if (DBG) {
                        Log.d(TAG, "update GLOBAL_ITEM: mStatisticsBatch=" + mStatisticsBatch);
                    }
                }
                break;
            case FOOTPRINTS_ALL:
                count = db.update(Footprints.TABLE_NAME, values, selection, selectionArgs);
                break;
            case FOOTPRINTS_ITEM:
                String footprintsId = uri.getPathSegments().get(1);
                count = db.update(Footprints.TABLE_NAME, values,
                        Footprints._ID + "=" + footprintsId
                                + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
                break;
            case SECURITY_ALL:
                count = db.update(Security.TABLE_NAME, values, selection, selectionArgs);
                break;
            case SECURITY_ITEM:
                String securityId = uri.getPathSegments().get(1);
                count = db.update(Security.TABLE_NAME, values,
                        Security._ID + "=" + securityId
                                + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
                break;
            case USER_ALL:
                count = db.update(User.TABLE_NAME, values, selection, selectionArgs);
                break;
            case USER_ITEM:
                String userId = uri.getPathSegments().get(1);
                count = db.update(User.TABLE_NAME, values, User._ID + "=" + userId
                        + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
                break;
            case MESSAGE_ALL:
            case MESSAGE_ITEM_DISTINCT:
                count = db.update(Message.TABLE_NAME, values, selection, selectionArgs);
                setMessageState(db, values);
                break;
            case MESSAGE_ITEM:
                String messageId = uri.getPathSegments().get(1);
                count = db.update(Message.TABLE_NAME, values,
                        User._ID + "=" + messageId + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""),
                        selectionArgs);
                break;
            case MSGUSER_ALL:
                count = db.update(Msguser.TABLE_NAME, values, selection, selectionArgs);
                break;
            case MSGUSER_ITEM:
                String msguserId = uri.getPathSegments().get(1);
                count = db.update(Msguser.TABLE_NAME, values,
                        User._ID + "=" + msguserId + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""),
                        selectionArgs);
                break;
            case STATISTICS_ALL:
                needNotifyChange = false;
                count = db.update(Statistics.TABLE_NAME, values, selection, selectionArgs);
                break;
            case STATISTICS_ITEM:
                needNotifyChange = false;
                String statisticsid = uri.getPathSegments().get(1);
                count = db.update(Statistics.TABLE_NAME, values,
                        Statistics._ID + "=" + statisticsid
                                + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
                break;
            case PKGUSAGE_ALL:
                count = db.update(PkgUsage.TABLE_NAME, values, selection, selectionArgs);
                break;
            case PKGUSAGE_ITEM:
                String pkgusageId = uri.getPathSegments().get(1);
                count = db.update(PkgUsage.TABLE_NAME, values,
                        PkgUsage._ID + "=" + pkgusageId
                                + (!TextUtils.isEmpty(selection) ? " AND (" + selection + ')' : ""), selectionArgs);
                break;
            default:
                throw new IllegalArgumentException("Unknown URI " + uri);
        }
        if (needNotifyChange) {
            getContext().getContentResolver().notifyChange(uri, null);
        }

        return count;
    }

    /**
     * Check whether the Message cmd has handled.
     * 
     * @param boot if ture meaning it is for the first time boot.
     * @param message
     */
    private void checkMsgCommand(boolean boot, Msg message) {
        Log.d(TAG, "checkMagCommand boot : " + boot);
        if (null == mCommand) {
            mCommand = new CommandManager(getContext());
        }
        if (mCommand.mConnected) {
            putCommandAfterConnected(boot, message);
        } else {
            mCommand.setMsgServiceConnectedCallBack(new MsgServiceConnectedListener(boot, message));
        }
    }

    private class MsgServiceConnectedListener implements MsgServiceConnectedCallBack {

        private boolean boot;

        private Msg message;

        public MsgServiceConnectedListener(boolean isBoot, Msg msg) {
            boot = isBoot;
            message = msg;
        }

        public void onServiceConnected() {
            Log.d(TAG, "MsgServiceConnectedCallBack");
            putCommandAfterConnected(boot, message);
        }
    }

    private void putCommandAfterConnected(boolean boot, Msg message) {
        if (boot) {
            List<Msg> messages = queryCommandFromMessage();
            if (null != messages) {
                for (int i = 0; i < messages.size(); i++) {
                    if (messages.get(i).mCategory != SciflyStore.Messages.CATEGORY_UPDATE_RESULT) {
//                        mCommand.putCommand(messages.get(i));
                    }
                }
            }
        } else {
            mCommand.putCommand(message);
        }
    }

    /**
     * query the command from the message.
     * 
     * @return messages
     */
    private List<Msg> queryCommandFromMessage() {
        SQLiteDatabase db = mOpenHelper.getReadableDatabase();
        Cursor cursor = null;
        try {
            cursor = db
                    .rawQuery("select * from message,msguser where message.userid=msguser.userid and status=0", null);
            if (cursor != null) {
                if (DBG) {
                    Log.d(TAG, "cursor count : " + cursor.getCount());
                }
                ArrayList<Msg> messages = new ArrayList<Msg>();
                Msg message = null;
                // cursor.moveToFirst();
                while (cursor.moveToNext()) {
                    message = new Msg();
                    message.mId = cursor.getLong(cursor.getColumnIndex(Message._ID));
                    message.mUserId = cursor.getString(cursor.getColumnIndex(Message.USERID));
                    message.mTitle = cursor.getString(cursor.getColumnIndex(Message.TITLE));
                    message.mTime = cursor.getLong(cursor.getColumnIndex(Message.TIME));
                    message.mStatus = cursor.getInt(cursor.getColumnIndex(Message.STATUS));
                    message.mData = splitJson(cursor.getString(cursor.getColumnIndex(Message.DATA)))[1];
                    message.mThumb = splitJson(cursor.getString(cursor.getColumnIndex(Message.DATA)))[0];
                    message.mExtra = cursor.getString(cursor.getColumnIndex(Message.EXTRA));
                    message.mCategory = cursor.getInt(cursor.getColumnIndex(Message.CATEGORY));
                    message.mReserve = cursor.getString(cursor.getColumnIndex(Message.RESERVE));
                    message.mUserInfo = cursor.getString(cursor.getColumnIndex(Msguser.NICKNAME));
                    message.mImgUrl = cursor.getString(cursor.getColumnIndex(Msguser.THUMBNAIL));
                    message.mSource = cursor.getInt(cursor.getColumnIndex(Msguser.SOURCE));
                    message.mBlocked = cursor.getInt(cursor.getColumnIndex(Msguser.BLOCKED));
                    messages.add(message);
                }
                return messages;
            }
        } catch (SQLiteException e) {
            Log.w(TAG, "lookup error", e);
            return null;
        } finally {
            if (cursor != null) {
                cursor.close();
            }
        }
        return null;
    }

    /**
     * count the size of unread message MessageCenter.
     * 
     * @param db
     * @param values
     */
    private void setMessageState(SQLiteDatabase db, ContentValues values) {
        int category = 0;
        if (values.containsKey(Message.CATEGORY)) {
            category = values.getAsInteger(Message.CATEGORY);
        }
        if (category != SciflyStore.Messages.CATEGORY_UPDATE_RESULT
                || category != SciflyStore.Messages.CATEGORY_UPLOAD_LOG
                || category != SciflyStore.Messages.CATEGORY_UPLOAD_SCREEN_SHOT
                || category != SciflyStore.Messages.CATEGORY_BOOT_LOGO
                || category != SciflyStore.Messages.CATEGORY_BROADCAST
                || category != SciflyStore.Messages.CATEGORY_APK_INSTALL
                || category != SciflyStore.Messages.CATEGORY_APK_UNINSTALL) {
            Cursor cursor = null;
            int count = 0;
            try {
                cursor = db.rawQuery(
                        "select status,count(status) as count from message Group by status having status = 0", null);
                if (cursor != null) {
                    if (cursor.moveToFirst() && cursor.getColumnIndex("count") != -1) {
                        count = cursor.getInt(cursor.getColumnIndex("count"));
                    }
                }
                if (DBG) {
                    Log.d(TAG, "count of unread message : " + count);
                }
                SciflyStore.Global.putString(getContext().getContentResolver(), SciflyStore.Global.MESSAGE_STATE,
                        String.valueOf(count));
            } catch (Exception e) {
                e.printStackTrace();
                Log.w(TAG, "lookup error", e);
                return;
            } finally {
                if (cursor != null) {
                    cursor.close();
                }
            }
        }
    }

    /**
     * set the state of iSynery & MessageCenter.
     * 
     * @param stateValues
     */
    private void setIMState(ContentValues stateValues) {
        if (SciflyStore.Global.IM_STATE.equals(stateValues.getAsString(Global.KEY))
                || SciflyStore.Global.MESSAGE_STATE.equals(stateValues.getAsString(Global.KEY))) {
            int im_state = 2; // default value : im offline
            int msg_state = 0; // default value : no unread message
            String im = SciflyStore.Global.getString(getContext().getContentResolver(), SciflyStore.Global.IM_STATE);
            String msg = SciflyStore.Global.getString(getContext().getContentResolver(),
                    SciflyStore.Global.MESSAGE_STATE);
            if (null != im && "" != im) {
                im_state = Integer.parseInt(im);
            }
            if (null != msg && "" != msg) {
                msg_state = Integer.parseInt(msg);
            }
            if (DBG) {
                Log.d(TAG, "im_state : " + im_state + " msg_state : " + msg_state);
            }
            StatusBarManager statusBarManager = (StatusBarManager) getContext().getSystemService(
                    Context.STATUS_BAR_SERVICE);
            final int NO_UNREAD_MESSAGE = 0;
            final int IM_STATE_ONLINE = 1;
            final int IM_STATE_OFFLINE = 2;
            final int IM_STATE_CONNECTED = 3;
            if ((IM_STATE_ONLINE == im_state) && (msg_state == NO_UNREAD_MESSAGE)) {
                statusBarManager.setIcon("message_state", com.android.internal.R.drawable.status_sys_im_online, 0,
                        "im_state_online & no_unread_msg");
                Log.d(TAG, "set IM State online & there is no unread message.");
            }
            if ((IM_STATE_OFFLINE == im_state) && (msg_state == NO_UNREAD_MESSAGE)) {
                statusBarManager.setIcon("message_state", com.android.internal.R.drawable.status_sys_im_offline, 0,
                        "im_state_offline & no_unread_msg");
                Log.d(TAG, "set IM State offline & there is no unread message.");
            }
            if ((IM_STATE_ONLINE == im_state) && (msg_state != NO_UNREAD_MESSAGE)) {
                statusBarManager.setIcon("message_state", com.android.internal.R.drawable.status_sys_im_online_message,
                        0, "im_state_online  & unread_msg");
                Log.d(TAG, "set IM State online & there is " + msg_state + " unread message.");
            }
            if ((IM_STATE_OFFLINE == im_state) && (msg_state != NO_UNREAD_MESSAGE)) {
                statusBarManager.setIcon("message_state",
                        com.android.internal.R.drawable.status_sys_im_offline_message, 0,
                        "im_state_offline & unread_msg");
                Log.d(TAG, "set IM State offline & there is " + msg_state + " unread message.");
            }
            if ((IM_STATE_CONNECTED == im_state) && (msg_state == NO_UNREAD_MESSAGE)) {
                statusBarManager.setIcon("message_state", com.android.internal.R.drawable.status_sys_im_connected, 0,
                        "im_state_connected & no_unread_msg");
                Log.d(TAG, "set IM State connected & there is no unread message.");
            }
            if ((IM_STATE_CONNECTED == im_state) && (msg_state != NO_UNREAD_MESSAGE)) {
                statusBarManager.setIcon("message_state", com.android.internal.R.drawable.status_sys_im_connected_message,
                        0, "im_state_connected  & unread_msg");
                Log.d(TAG, "set IM State connected & there is " + msg_state + " unread message.");
            }
        }
    }

    /**
     * split the String which was merged by jsonString.
     * 
     * @param str
     * @return
     */
    public static String[] splitJson(String str) {
        if (TextUtils.isEmpty(str)) {
            return new String[] {
                    "", ""
            };
        }
        JSONObject jsonObject;
        String str1 = "";
        String str2 = "";
        try {
            jsonObject = new JSONObject(str);
            str1 = jsonObject.getString("str1");
            str2 = jsonObject.getString("str2");
        } catch (JSONException e) {
            e.printStackTrace();
        }
        String[] strings = {
                str1, str2
        };
        return strings;
    }
}
