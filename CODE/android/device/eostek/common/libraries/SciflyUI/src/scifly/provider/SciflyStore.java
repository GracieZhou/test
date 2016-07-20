
package scifly.provider;

import java.io.ByteArrayOutputStream;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import scifly.provider.metadata.Blacklist;
import scifly.provider.metadata.Footprint;
import scifly.provider.metadata.Msg;
import scifly.provider.metadata.PkgUsage;
import scifly.provider.metadata.StatisticsRecord;
import scifly.provider.metadata.Usr;
import android.content.ContentResolver;
import android.content.ContentValues;
import android.database.Cursor;
import android.database.SQLException;
import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.media.ThumbnailUtils;
import android.net.Uri;
import android.provider.BaseColumns;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.collect.Lists;

/** @hide **/
public class SciflyStore {

    private static final String TAG = "SciflyStore";

    private static final boolean DBG = true;

    private static final String AUTHORITY = "com.eostek.scifly.provider";

    private static class CacheObject {
        protected final Uri mUri;

        protected final String mPutCommand;

        protected final String mGetCommand;

        public CacheObject(Uri uri, String putCommand, String getCommand) {
            mUri = uri;
            mPutCommand = putCommand;
            mGetCommand = getCommand;
        }
    }

    // Thread-safe.
    private static class GlobalCache extends CacheObject {
        public GlobalCache(Uri uri, String putCommand, String getCommand) {
            super(uri, putCommand, getCommand);
        }

        public boolean putString(ContentResolver cr, String key, String value) {
            ContentValues values = new ContentValues();
            values.put(Global.KEY, key);
            values.put(Global.VALUE, value);
            cr.insert(mUri, values);

            return true;
        }

        public String getString(ContentResolver cr, String key) {
            if (cr == null) {
                Log.w(TAG, "invalid ContentResolver.");
                return "";
            }

            Cursor c = cr.query(mUri, new String[] {
                Global.VALUE
            }, "key=?", new String[] {
                key
            }, null);

            if (c == null) {
                Log.w(TAG, "Can't get key " + key + " from " + mUri);
                return "";
            }

            String value = c.moveToNext() ? c.getString(0) : "";
            if (DBG) {
                Log.d(TAG, "<key,value> : <" + key + "," + value + ">");
            }
            // close the cursor
            if (c != null) {
                c.close();
            }

            return value;
        }
    }

    /**
     * Common base for tables of key/value settings.
     */
    private static class GlobalTable implements BaseColumns {

        public static final String KEY = "key";

        public static final String VALUE = "value";

        protected static boolean putString(ContentResolver resolver, Uri uri, String key, String value) {
            try {
                // The database will take care of replacing duplicates.
                ContentValues values = new ContentValues();
                values.put(KEY, key);
                values.put(VALUE, value);
                resolver.insert(uri, values);
                return true;
            } catch (SQLException e) {
                Log.w(TAG, "Can't set key " + key + " in " + uri, e);
                return false;
            }
        }
    }

    /** @hide **/
    public static final class Global extends GlobalTable {

        private static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/global");

        public static final String TEST = "test";

        public static final String DEVICE_NAME = "device_name";

        public static final String CITY_NAME = "city_name";

        public static final String RECOVERY_LABEL = "recovery_label";

        public static final String MAC_ADDR = "mac_addr";

        public static final String LOG_ENABLE = "log_enable";

        public static final String LOCATION = "location_info";

        public static final String DOWNLOAD_ID = "download_id";

        public static final String IM_STATE = "im_state";

        public static final String MESSAGE_STATE = "message_state";

        public static final String SCREENSHOT_STORAGE_LOCATION = "screenshot_storage_location";

        /**
         * Default install location value. 0 = auto, let system decide 1 =
         * internal 2 = sdcard 3 = usb
         */
        public static final String DEFAULT_INSTALL_LOCATION = "default_install_location";

        public static final String STATISTICS_BATCH = "statistics_batch";

        public static final String STATISTICS_INTERVAL = "statistics_interval";

        public static final String DOWNLOAD_ENGINE = "download_engine";

        public static final String ETAG_APP_BLACK_LIST = "AppBlackListEtag";

        public static final String ETAG_APP_SECURE_LIST = "AppSecureListEtag";

        private static final HashSet<String> GLOBAL;
        static {
            GLOBAL = new HashSet<String>();
            GLOBAL.add(Global.TEST);
            GLOBAL.add(Global.DEVICE_NAME);
            GLOBAL.add(Global.CITY_NAME);
            GLOBAL.add(Global.MAC_ADDR);
            GLOBAL.add(Global.RECOVERY_LABEL);
            GLOBAL.add(Global.LOG_ENABLE);
            GLOBAL.add(Global.STATISTICS_BATCH);
            GLOBAL.add(Global.STATISTICS_INTERVAL);
            GLOBAL.add(Global.LOCATION);
            GLOBAL.add(Global.DOWNLOAD_ID);
            GLOBAL.add(Global.IM_STATE);
            GLOBAL.add(Global.MESSAGE_STATE);
            GLOBAL.add(Global.SCREENSHOT_STORAGE_LOCATION);
            GLOBAL.add(Global.DEFAULT_INSTALL_LOCATION);
            GLOBAL.add(Global.DOWNLOAD_ENGINE);
            GLOBAL.add(Global.ETAG_APP_BLACK_LIST);
            GLOBAL.add(Global.ETAG_APP_SECURE_LIST);
        }

        private static final String CALL_METHOD_PUT_GLOBAL = "method_put_global";

        private static final String CALL_METHOD_GET_GLOBAL = "method_get_global";

        private static GlobalCache sKeyValueCache = new GlobalCache(CONTENT_URI, CALL_METHOD_PUT_GLOBAL,
                CALL_METHOD_GET_GLOBAL);

        public static boolean putString(ContentResolver cr, String key, String value) {
            if (GLOBAL.contains(key)) {
                return sKeyValueCache.putString(cr, key, value);
            }
            Log.w(TAG, "no " + key + " in global");

            return false;
        }

        public static String getString(ContentResolver cr, String key) {
            if (GLOBAL.contains(key)) {
                return sKeyValueCache.getString(cr, key);
            }
            Log.w(TAG, "no " + key + " in global");

            return "";
        }

        public static String getString(ContentResolver cr, String key, String def) {
            if (GLOBAL.contains(key)) {
                String value = sKeyValueCache.getString(cr, key);
                if (TextUtils.isEmpty(value)) {
                    return def;
                } else {
                    return value;
                }
            }
            Log.w(TAG, "no " + key + " in global");

            return "";
        }
    }

    private static class FootprintsCache extends CacheObject {

        protected final String mDelCommand;

        private static final int DEFAULT_WIDTH = 128;

        private static final int DEFAULT_HEIGHT = 72;

        public FootprintsCache(Uri uri, String putCommand, String getCommand, String delCommand) {
            super(uri, putCommand, getCommand);
            this.mDelCommand = delCommand;
        }

        public boolean putFootprints(ContentResolver cr, Footprint footprint) {

            ContentValues values = new ContentValues();
            if (TextUtils.isEmpty(footprint.mUser)) {
                values.put(Footprints.USER, User.USER_GUEST);
            } else {
                values.put(Footprints.USER, footprint.mUser);
            }
            values.put(Footprints.DATA, footprint.mData);
            values.put(Footprints.TITLE, footprint.mTitle);
            values.put(Footprints.RESERVE, footprint.mReserve);
            values.put(Footprints.REMARK, footprint.mRemark);
            values.put(Footprints.CATEGORY, footprint.mCategory);
            
            Calendar calendar = Calendar.getInstance();
            long time = calendar.getTimeInMillis();
            if (footprint.mTime == 0) {
                values.put(Footprints.TIME, time);
            } else {
                values.put(Footprints.TIME, footprint.mTime);
            }
            
            if (footprint.mThumb != null) {
                ByteArrayOutputStream output = new ByteArrayOutputStream();
                int width = footprint.mThumb.getWidth();
                int height = footprint.mThumb.getHeight();
                Log.w(TAG, "bitmap width : " + width + " and height : " + height);
                Bitmap bm = null;
                if (DEFAULT_WIDTH < width || DEFAULT_HEIGHT < height) {
                    bm = ThumbnailUtils.extractThumbnail(footprint.mThumb, DEFAULT_WIDTH, DEFAULT_HEIGHT);
                    bm.compress(CompressFormat.JPEG, 80, output);
                    Log.w(TAG, "scaled bitmap width : " + bm.getWidth() + " and height : " + bm.getHeight());
                } else {
                    footprint.mThumb.compress(CompressFormat.JPEG, 80, output);
                }
                values.put(Footprints.THUMBNAIL, output.toByteArray());
                if (bm != null) {
                    bm.recycle();
                }
            } else {
                values.put(Footprints.THUMBNAIL, new byte[1]);
            }
            
            calendar.set(Calendar.HOUR, 0);
            calendar.set(Calendar.MINUTE, 0);
            calendar.set(Calendar.SECOND, 0);
            calendar.set(Calendar.MILLISECOND, 0);
            time = calendar.getTimeInMillis();
            
            int count = cr.update(mUri, values, "data=? and time>?", new String[]{footprint.mData, String.valueOf(time)});
            if(count <= 0) {
                cr.insert(mUri, values);
                Log.w(TAG, "putFootprints--->insert");
            } else {
                Log.w(TAG, "putFootprints--->update");
            }

            return true;
        }

        public List<Footprint> getFootprints(ContentResolver cr, int category, int period) {
            if (cr == null) {
                Log.w(TAG, "invalid ContentResolver.");
                return Lists.newArrayList();
            }

            // the user login
            String user = getActiveUser(cr);
            Cursor c = null;
            if (period == Footprints.PERIOD_OVER_MONTH) {
                String[] selectArgs = getSelectionArgs(user, category, period);
                c = cr.query(mUri, Footprints.PROJECTION_ALL, "user=? and category=? and time<=?", new String[] {
                        selectArgs[0], selectArgs[1], selectArgs[2]
                }, null);
            } else {
                c = cr.query(mUri, Footprints.PROJECTION_ALL, Footprints.SELECTION_DEFAULT,
                        getSelectionArgs(user, category, period), null);
            }
            if (c == null) {
                Log.w(TAG, "Can't get value from " + mUri);
                return Lists.newArrayList();
            }

            List<Footprint> list = Lists.newArrayList();
            while (c.moveToNext()) {
                Footprint item = new Footprint();
                item.mId = c.getLong(c.getColumnIndex(Footprints._ID));
                item.mUser = c.getString(c.getColumnIndex(Footprints.USER));
                item.mData = c.getString(c.getColumnIndex(Footprints.DATA));
                item.mTitle = c.getString(c.getColumnIndex(Footprints.TITLE));
                item.mTime = c.getLong(c.getColumnIndex(Footprints.TIME));
                item.mCategory = c.getInt(c.getColumnIndex(Footprints.CATEGORY));
                item.mRemark = c.getString(c.getColumnIndex(Footprints.REMARK));
                item.mReserve = c.getString(c.getColumnIndex(Footprints.RESERVE));
                byte[] bitmap = c.getBlob(c.getColumnIndex(Footprints.THUMBNAIL));
                if (bitmap != null) {
                    try {
                        item.mThumb = BitmapFactory.decodeByteArray(bitmap, 0, bitmap.length);
                    } catch (Exception e) {
                    }
                } else {
                    item.mThumb = null;
                }
                // add to list
                list.add(item);
            }
            // close the cursor
            if (c != null) {
                c.close();
            }
            return list;
        }

        public List<Footprint> getFootprints(ContentResolver cr, String[] projection, String selection,
                String[] selectArgs) {
            if (cr == null) {
                Log.w(TAG, "invalid ContentResolver.");
                return Lists.newArrayList();
            }
            Cursor c = cr.query(mUri, projection, selection, selectArgs, null);
            if (c == null) {
                Log.w(TAG, "Can't get value from " + mUri);
                return Lists.newArrayList();
            }

            List<Footprint> list = Lists.newArrayList();
            c.moveToFirst();
            while (c.moveToNext()) {
                Footprint item = new Footprint();
                item.mId = c.getLong(c.getColumnIndex(Footprints._ID));
                item.mUser = c.getString(c.getColumnIndex(Footprints.USER));
                item.mData = c.getString(c.getColumnIndex(Footprints.DATA));
                item.mTitle = c.getString(c.getColumnIndex(Footprints.TITLE));
                item.mTime = c.getLong(c.getColumnIndex(Footprints.TIME));
                item.mCategory = c.getInt(c.getColumnIndex(Footprints.CATEGORY));
                item.mRemark = c.getString(c.getColumnIndex(Footprints.REMARK));
                item.mReserve = c.getString(c.getColumnIndex(Footprints.RESERVE));
                byte[] bitmap = c.getBlob(c.getColumnIndex(Footprints.THUMBNAIL));
                if (bitmap != null) {
                    try {
                        item.mThumb = BitmapFactory.decodeByteArray(bitmap, 0, bitmap.length);
                    } catch (Exception e) {
                    }
                } else {
                    item.mThumb = null;
                }

                // add to list
                list.add(item);
            }

            // close the cursor
            if (c != null) {
                c.close();
            }

            return list;
        }

        public boolean deleteFootprints(ContentResolver cr, String selection, String[] selectArgs) {
            if (cr == null) {
                Log.w(TAG, "invalid ContentResolver.");
                return false;
            }
            cr.delete(mUri, selection, selectArgs);
            return true;
        }

        public boolean deleteFootprints(ContentResolver cr, String pkg) {
            if (cr == null) {
                Log.w(TAG, "invalid ContentResolver.");
                return false;
            }
            String where = "data like '%component=" + pkg + "/%'";
            cr.delete(mUri, where, null);
            return true;
        }

        private String[] getSelectionArgs(String user, int category, int period) {
            Calendar calendar = Calendar.getInstance();
            String timeFrom = "";
            String timeTo = "";
            if (period == Footprints.PERIOD_TODAY) {
                // current time
                timeTo = String.valueOf(calendar.getTimeInMillis());

                // 0:00 of today
                calendar.add(Calendar.DAY_OF_WEEK, -1);
                calendar.set(Calendar.HOUR_OF_DAY, 23);
                calendar.set(Calendar.MINUTE, 59);
                calendar.set(Calendar.SECOND, 59);
                calendar.set(Calendar.MILLISECOND, 999);
                timeFrom = String.valueOf(calendar.getTimeInMillis());
            } else if (period == Footprints.PERIOD_YESTERDAY) {
                // yesterday 0:00
                calendar.add(Calendar.DAY_OF_WEEK, -1);
                calendar.set(Calendar.HOUR_OF_DAY, 23);
                calendar.set(Calendar.MINUTE, 59);
                calendar.set(Calendar.SECOND, 59);
                calendar.set(Calendar.MILLISECOND, 999);
                timeTo = String.valueOf(calendar.getTimeInMillis());

                // day before today
                calendar.add(Calendar.DAY_OF_WEEK, -1);
                calendar.set(Calendar.HOUR_OF_DAY, 23);
                calendar.set(Calendar.MINUTE, 59);
                calendar.set(Calendar.SECOND, 59);
                calendar.set(Calendar.MILLISECOND, 999);
                timeFrom = String.valueOf(calendar.getTimeInMillis());
            } else if (period == Footprints.PERIOD_WEEK) {
                calendar.add(Calendar.DAY_OF_WEEK, -2);
                calendar.set(Calendar.HOUR_OF_DAY, 23);
                calendar.set(Calendar.MINUTE, 59);
                calendar.set(Calendar.SECOND, 59);
                calendar.set(Calendar.MILLISECOND, 999);
                timeTo = String.valueOf(calendar.getTimeInMillis());

                calendar.add(Calendar.DAY_OF_WEEK, -5);
                calendar.set(Calendar.HOUR_OF_DAY, 23);
                calendar.set(Calendar.MINUTE, 59);
                calendar.set(Calendar.SECOND, 59);
                calendar.set(Calendar.MILLISECOND, 999);
                timeFrom = String.valueOf(calendar.getTimeInMillis());
            } else if (period == Footprints.PERIOD_MONTH) {
                calendar.add(Calendar.DAY_OF_WEEK, -8);
                calendar.set(Calendar.HOUR_OF_DAY, 23);
                calendar.set(Calendar.MINUTE, 59);
                calendar.set(Calendar.SECOND, 59);
                calendar.set(Calendar.MILLISECOND, 999);
                timeTo = String.valueOf(calendar.getTimeInMillis());

                calendar.add(Calendar.DAY_OF_WEEK, -22);
                calendar.set(Calendar.HOUR_OF_DAY, 23);
                calendar.set(Calendar.MINUTE, 59);
                calendar.set(Calendar.SECOND, 59);
                calendar.set(Calendar.MILLISECOND, 999);
                timeFrom = String.valueOf(calendar.getTimeInMillis());
            } else if (period == Footprints.PERIOD_OVER_MONTH) {
                calendar.add(Calendar.DAY_OF_WEEK, -31);
                calendar.set(Calendar.HOUR_OF_DAY, 23);
                calendar.set(Calendar.MINUTE, 59);
                calendar.set(Calendar.SECOND, 59);
                calendar.set(Calendar.MILLISECOND, 999);
                timeTo = String.valueOf(calendar.getTimeInMillis());

                calendar.add(Calendar.DAY_OF_WEEK, -1);
                calendar.set(Calendar.HOUR_OF_DAY, 23);
                calendar.set(Calendar.MINUTE, 59);
                calendar.set(Calendar.SECOND, 59);
                calendar.set(Calendar.MILLISECOND, 999);
                timeFrom = String.valueOf(calendar.getTimeInMillis());
            }

            if (DBG) {
                Log.d(TAG, "user : " + user + " category : " + category + " time between " + timeFrom + " and "
                        + timeTo);
            }
            return new String[] {
                    user, String.valueOf(category), timeFrom, timeTo
            };
        }

        private String getActiveUser(ContentResolver cr) {
            Usr user = User.getUser(cr);
            if (user == null || TextUtils.isEmpty(user.mName)) {
                return User.USER_GUEST;
            } else {
                return user.mName;
            }
        }
    }

    /** @hide **/
    public static final class Footprints implements BaseColumns {

        private static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/footprints");

        public static final int CATEGORY_NULL = 0;

        // category for tv
        public static final int CATEGORY_TV_ATV = CATEGORY_NULL + 1;

        public static final int CATEGORY_TV_DTV = CATEGORY_NULL + 2;

        public static final int CATEGORY_TV_HDMI = CATEGORY_NULL + 3;

        // may be has cvbs/vga etc

        // category for media
        public static final int CATEGORY_MEDIA_VIDEO = CATEGORY_NULL + 6;

        public static final int CATEGORY_MEDIA_VOD = CATEGORY_NULL + 7;

        // category for apk install / start / uninstall
        public static final int CATEGORY_APK_INSTALL = CATEGORY_NULL + 11;

        public static final int CATEGORY_APK_ENTRY = CATEGORY_NULL + 12;

        public static final int CATEGORY_APK_UNINSTALL = CATEGORY_NULL + 13;

        // category for web
        public static final int CATEGORY_WEB_PAGE = CATEGORY_NULL + 16;

        // user login/logout
        public static final int CATEGORY_USER_LOGIN = CATEGORY_NULL + 21;

        public static final int CATEGORY_USER_LOGOUT = CATEGORY_NULL + 22;

        public static final int CATEGORY_MAX = CATEGORY_NULL + 25;

        // query period
        public static final int PERIOD_TODAY = 1;

        public static final int PERIOD_YESTERDAY = 2;

        public static final int PERIOD_WEEK = 3;

        public static final int PERIOD_MONTH = 4;

        public static final int PERIOD_OVER_MONTH = 5;

        // database filed
        public static final String USER = "user";

        public static final String DATA = "data";

        public static final String THUMBNAIL = "thumbnail";

        public static final String TITLE = "title";

        public static final String TIME = "time";
        
        public static final String DATE = "date";

        public static final String CATEGORY = "category";

        public static final String REMARK = "remark";

        public static final String RESERVE = "reserve";

        private static final String[] PROJECTION_ALL = {
                _ID, USER, DATA, TITLE, TIME, THUMBNAIL, CATEGORY, REMARK, RESERVE
        };

        private static final String SELECTION_DEFAULT = "user=? and category=? and time>=? and time<=?";

        private static final String BUNDLE_CATEGORY = "bundle_category";

        private static final String BUNDLE_TIME_BEGIN = "bundle_time_begin";

        private static final String BUNDLE_TIME_END = "bundle_time_end";

        private static final String BUNDLE_VALUE = "bundle_value";

        private static final String BUNDLE_MORE_THAN_ONE_MONTH = "bundle_more_than_one_month";

        private static final String CALL_METHOD_PUT_FOOTPRINT = "method_put_footprint";

        private static final String CALL_METHOD_GET_FOOTPRINT = "method_get_footprint";

        private static final String CALL_METHOD_DEL_FOOTPRINT = "method_del_footprint";

        private static FootprintsCache sFootprintsCache = new FootprintsCache(CONTENT_URI, CALL_METHOD_PUT_FOOTPRINT,
                CALL_METHOD_GET_FOOTPRINT, CALL_METHOD_DEL_FOOTPRINT);

        /** @hide **/
        public static boolean putFootprints(ContentResolver cr, Footprint footprint) {
            if (DBG) {
                Log.d(TAG, (footprint == null) ? "footprints is null" : footprint.toString());
            }
            if (footprint == null) {
                Log.w(TAG, "FootPrints must not be null.");
                return false;
            }

            // user & data & titile must not be empty
            if (footprint.mCategory == CATEGORY_USER_LOGIN || footprint.mCategory == CATEGORY_USER_LOGOUT) {
                if (TextUtils.isEmpty(footprint.mUser)) {
                    Log.w(TAG, "User must not be empty.");
                    return false;
                }
            } else {
                if (TextUtils.isEmpty(footprint.mData) || TextUtils.isEmpty(footprint.mTitle)) {
                    Log.w(TAG, "Data and Title must not be empty.");
                    return false;
                }
            }

            return sFootprintsCache.putFootprints(cr, footprint);
        }

        /** @hide **/
        public static List<Footprint> getFootprints(ContentResolver cr, int category, int period) {
            if (DBG) {
                Log.d(TAG, "getFootprints, category : " + category + " period : " + period);
            }
            if (category < CATEGORY_NULL || category > CATEGORY_MAX) {
                return Lists.newArrayList();
            }

            if (period < PERIOD_TODAY || period > PERIOD_OVER_MONTH) {
                return Lists.newArrayList();
            }

            return sFootprintsCache.getFootprints(cr, category, period);
        }

        /** @hide **/
        public static List<Footprint> getFootprints(ContentResolver cr, String[] projection, String selection,
                String[] selectArgs) {
            if (projection == null || projection.length == 0) {
                Log.w(TAG, "projection must not be empty.");
                return Lists.newArrayList();
            }

            if (TextUtils.isEmpty(selection)) {
                Log.w(TAG, "selection must not be empty.");
                return Lists.newArrayList();
            }

            if (selectArgs == null || selectArgs.length == 0) {
                Log.w(TAG, "selectArgs must not be empty.");
                return Lists.newArrayList();
            }

            if (DBG) {
                StringBuffer sb = new StringBuffer();
                sb.append("\n");
                sb.append("projection : [");
                for (String cell : projection) {
                    sb.append(cell);
                    sb.append(" ");
                }
                sb.append("]");
                sb.append("selection : [");
                sb.append(selection);
                sb.append("]");
                sb.append(" selectArgs : [ ");
                for (String item : selectArgs) {
                    sb.append(item);
                    sb.append(" ");
                }
                sb.append("]");
                sb.append("\n");
                Log.d(TAG, "args : " + sb.toString());
            }

            return sFootprintsCache.getFootprints(cr, projection, selection, selectArgs);
        }

        /** @hide **/
        public static boolean deleteFootprints(ContentResolver cr, String selection, String[] selectArgs) {
            // FIXME Permission check
            return sFootprintsCache.deleteFootprints(cr, selection, selectArgs);
        }

        /** @hide **/
        public static boolean deleteFootprints(ContentResolver cr, String pkg) {
            if (TextUtils.isEmpty(pkg)) {
                return false;
            }

            if (DBG) {
                Log.d(TAG, "delete footprints with pkg : " + pkg);
            }

            // FIXME Permission check
            return sFootprintsCache.deleteFootprints(cr, pkg);
        }
    }

    private static class StatisticsRecordsCache extends CacheObject {

        protected final String mDelCommand;

        public StatisticsRecordsCache(Uri uri, String putCommand, String getCommand, String delCommand) {
            super(uri, putCommand, getCommand);
            this.mDelCommand = delCommand;
        }

        public boolean putStatisticsRecord(ContentResolver cr, StatisticsRecord statisticsRecord) {

            ContentValues values = new ContentValues();
            values.put(StatisticsRecords.PKGNAME, statisticsRecord.mPkgname);
            values.put(StatisticsRecords.ACTIVITY_NAME, statisticsRecord.mActivityName);
            values.put(StatisticsRecords.TYPE, statisticsRecord.mType);
            values.put(StatisticsRecords.TIME, statisticsRecord.mTime);
            values.put(StatisticsRecords.CATEGORY, statisticsRecord.mCategory);
            values.put(StatisticsRecords.KEY, statisticsRecord.mKey);
            values.put(StatisticsRecords.VALUE, statisticsRecord.mValue);
            values.put(StatisticsRecords.PARAMS, statisticsRecord.mParams);
            cr.insert(mUri, values);
            return true;
        }

        public List<StatisticsRecord> getStatisticsRecord(ContentResolver cr, long time) {
            if (cr == null) {
                Log.w(TAG, "invalid ContentResolver.");
                return Lists.newArrayList();
            }

            Cursor c = null;
            c = cr.query(mUri, StatisticsRecords.PROJECTION_ALL, "time<=?", new String[] {
                String.valueOf(time)
            }, null);
            if (c == null) {
                Log.w(TAG, "Can't get value from " + mUri);
                return Lists.newArrayList();
            }

            List<StatisticsRecord> list = Lists.newArrayList();
            while (c.moveToNext()) {
                StatisticsRecord item = new StatisticsRecord();
                item.mId = c.getLong(c.getColumnIndex(StatisticsRecords._ID));
                item.mPkgname = c.getString(c.getColumnIndex(StatisticsRecords.PKGNAME));
                item.mActivityName = c.getString(c.getColumnIndex(StatisticsRecords.ACTIVITY_NAME));
                item.mType = c.getInt(c.getColumnIndex(StatisticsRecords.TYPE));
                item.mTime = c.getLong(c.getColumnIndex(StatisticsRecords.TIME));
                item.mCategory = c.getString(c.getColumnIndex(StatisticsRecords.CATEGORY));
                item.mKey = c.getString(c.getColumnIndex(StatisticsRecords.KEY));
                item.mValue = c.getString(c.getColumnIndex(StatisticsRecords.VALUE));
                item.mParams = c.getString(c.getColumnIndex(StatisticsRecords.PARAMS));
                // add to list
                list.add(item);
            }
            // close the cursor
            if (c != null) {
                c.close();
            }
            return list;
        }

        public List<StatisticsRecord> getStatisticsRecord(ContentResolver cr, String[] projection, String selection,
                String[] selectArgs, String sortOrder) {
            if (cr == null) {
                Log.w(TAG, "invalid ContentResolver.");
                return Lists.newArrayList();
            }
            Cursor c = cr.query(mUri, projection, selection, selectArgs, sortOrder);
            if (c == null) {
                Log.w(TAG, "Can't get value from " + mUri);
                return Lists.newArrayList();
            }

            List<StatisticsRecord> list = Lists.newArrayList();
            while (c.moveToNext()) {
                StatisticsRecord item = new StatisticsRecord();
                item.mId = c.getLong(c.getColumnIndex(StatisticsRecords._ID));
                item.mPkgname = c.getString(c.getColumnIndex(StatisticsRecords.PKGNAME));
                item.mActivityName = c.getString(c.getColumnIndex(StatisticsRecords.ACTIVITY_NAME));
                item.mType = c.getInt(c.getColumnIndex(StatisticsRecords.TYPE));
                item.mTime = c.getLong(c.getColumnIndex(StatisticsRecords.TIME));
                item.mCategory = c.getString(c.getColumnIndex(StatisticsRecords.CATEGORY));
                item.mKey = c.getString(c.getColumnIndex(StatisticsRecords.KEY));
                item.mValue = c.getString(c.getColumnIndex(StatisticsRecords.VALUE));
                item.mParams = c.getString(c.getColumnIndex(StatisticsRecords.PARAMS));
                // add to list
                list.add(item);
            }
            // close the cursor
            if (c != null) {
                c.close();
            }
            return list;
        }

        public boolean deleteStatisticsRecord(ContentResolver cr, String selection, String[] selectArgs) {
            if (cr == null) {
                Log.w(TAG, "invalid ContentResolver.");
                return false;
            }
            cr.delete(mUri, selection, selectArgs);
            return true;
        }

        public boolean deleteStatisticsRecord(ContentResolver cr, long time) {
            if (cr == null) {
                Log.w(TAG, "invalid ContentResolver.");
                return false;
            }
            String where = "time<" + time;
            cr.delete(mUri, where, null);
            return true;
        }

    }

    /** @hide **/
    public static final class StatisticsRecords implements BaseColumns {

        private static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/statistics");

        // database filed
        public static final String PKGNAME = "pkgname";

        public static final String ACTIVITY_NAME = "activityname";

        public static final String TYPE = "type";

        public static final String TIME = "time";

        public static final String CATEGORY = "category";

        public static final String KEY = "key";

        public static final String VALUE = "value";

        public static final String PARAMS = "params";

        public static final String[] PROJECTION_ALL = {
                _ID, PKGNAME, ACTIVITY_NAME, TYPE, TIME, CATEGORY, KEY, VALUE, PARAMS
        };

        private static final String CALL_METHOD_PUT_STATISTICS_RECORD = "method_put_statistics_record";

        private static final String CALL_METHOD_GET_STATISTICS_RECORD = "method_get_statistics_record";

        private static final String CALL_METHOD_DEL_STATISTICS_RECORD = "method_del_statistics_record";

        private static StatisticsRecordsCache sStatisticsRecordsCache = new StatisticsRecordsCache(CONTENT_URI,
                CALL_METHOD_PUT_STATISTICS_RECORD, CALL_METHOD_GET_STATISTICS_RECORD, CALL_METHOD_DEL_STATISTICS_RECORD);

        /** @hide **/
        public static boolean putStatisticsRecord(ContentResolver cr, StatisticsRecord statisticsRecord) {
            if (DBG) {
                Log.d(TAG, (statisticsRecord == null) ? "statisticsRecord is null" : statisticsRecord.toString());
            }
            if (statisticsRecord == null) {
                Log.w(TAG, "StatisticsRecord must not be null.");
                return false;
            }

            // Pkgname must not be empty
            if (TextUtils.isEmpty(statisticsRecord.mPkgname)) {
                Log.w(TAG, "Pkgname must not be empty.");
                return false;
            }

            return sStatisticsRecordsCache.putStatisticsRecord(cr, statisticsRecord);
        }

        /** @hide **/
        public static List<StatisticsRecord> getStatisticsRecord(ContentResolver cr, long time) {
            if (DBG) {
                Log.d(TAG, "getStatisticsRecord, time : " + time);
            }
            if (time < 0) {
                return Lists.newArrayList();
            }

            return sStatisticsRecordsCache.getStatisticsRecord(cr, time);
        }

        /** @hide **/
        public static List<StatisticsRecord> getStatisticsRecord(ContentResolver cr, String[] projection,
                String selection, String[] selectArgs, String sortOrder) {
            if (projection == null || projection.length == 0) {
                Log.w(TAG, "projection must not be empty.");
                return Lists.newArrayList();
            }

            if (TextUtils.isEmpty(selection)) {
                Log.w(TAG, "selection must not be empty.");
                return Lists.newArrayList();
            }

            if (selectArgs == null || selectArgs.length == 0) {
                Log.w(TAG, "selectArgs must not be empty.");
                return Lists.newArrayList();
            }

            if (DBG) {
                StringBuffer sb = new StringBuffer();
                sb.append("\n");
                sb.append("projection : [");
                for (String cell : projection) {
                    sb.append(cell);
                    sb.append(" ");
                }
                sb.append("]");
                sb.append("selection : [");
                sb.append(selection);
                sb.append("]");
                sb.append(" selectArgs : [ ");
                for (String item : selectArgs) {
                    sb.append(item);
                    sb.append(" ");
                }
                sb.append("]");
                sb.append("\n");
                Log.d(TAG, "args : " + sb.toString());
            }

            return sStatisticsRecordsCache.getStatisticsRecord(cr, projection, selection, selectArgs, sortOrder);
        }

        /** @hide **/
        public static boolean deleteStatisticsRecord(ContentResolver cr, String selection, String[] selectArgs) {
            // FIXME Permission check
            return sStatisticsRecordsCache.deleteStatisticsRecord(cr, selection, selectArgs);
        }

        /** @hide **/
        public static boolean deleteStatisticsRecord(ContentResolver cr, long time) {
            if (time < 0) {
                return false;
            }

            if (DBG) {
                Log.d(TAG, "delete StatisticsRecord before time : " + time);
            }

            // FIXME Permission check
            return sStatisticsRecordsCache.deleteStatisticsRecord(cr, time);
        }
    }

    private static class PkgUsagesCache extends CacheObject {

        protected final String mDelCommand;

        public PkgUsagesCache(Uri uri, String putCommand, String getCommand, String delCommand) {
            super(uri, putCommand, getCommand);
            this.mDelCommand = delCommand;
        }

        public boolean putPkgUsage(ContentResolver cr, PkgUsage pkgUsage) {
            ContentValues values = new ContentValues();
            values.put(PkgUsages.PKGNAME, pkgUsage.mPkgname);
            values.put(PkgUsages.TIME, pkgUsage.mTime);
            cr.insert(mUri, values);
            return true;
        }

        public boolean updatePkgUsage(ContentResolver cr, PkgUsage pkgUsage) {
            ContentValues values = new ContentValues();
            values.put(PkgUsages.PKGNAME, pkgUsage.mPkgname);
            values.put(PkgUsages.TIME, pkgUsage.mTime);
            int count = cr.update(mUri, values, "pkgname=?", new String[] {
                pkgUsage.mPkgname
            });
            if (count <= 0) {
                return false;
            }
            return true;
        }

        public List<PkgUsage> getPkgUsage(ContentResolver cr) {
            if (cr == null) {
                Log.w(TAG, "invalid ContentResolver.");
                return Lists.newArrayList();
            }

            Cursor c = null;
            c = cr.query(mUri, PkgUsages.PROJECTION_ALL, null, null, null);
            if (c == null) {
                Log.w(TAG, "Can't get value from " + mUri);
                return Lists.newArrayList();
            }

            List<PkgUsage> list = Lists.newArrayList();
            while (c.moveToNext()) {
                PkgUsage item = new PkgUsage();
                item.mId = c.getLong(c.getColumnIndex(PkgUsages._ID));
                item.mPkgname = c.getString(c.getColumnIndex(PkgUsages.PKGNAME));
                item.mTime = c.getLong(c.getColumnIndex(PkgUsages.TIME));
                // add to list
                list.add(item);
            }
            // close the cursor
            if (c != null) {
                c.close();
            }
            return list;
        }

        public List<PkgUsage> getPkgUsage(ContentResolver cr, String[] projection, String selection,
                String[] selectArgs, String sortOrder) {
            if (cr == null) {
                Log.w(TAG, "invalid ContentResolver.");
                return Lists.newArrayList();
            }
            Cursor c = cr.query(mUri, projection, selection, selectArgs, sortOrder);
            if (c == null) {
                Log.w(TAG, "Can't get value from " + mUri);
                return Lists.newArrayList();
            }

            List<PkgUsage> list = Lists.newArrayList();
            while (c.moveToNext()) {
                PkgUsage item = new PkgUsage();
                item.mId = c.getLong(c.getColumnIndex(PkgUsages._ID));
                item.mPkgname = c.getString(c.getColumnIndex(PkgUsages.PKGNAME));
                item.mTime = c.getLong(c.getColumnIndex(PkgUsages.TIME));
                // add to list
                list.add(item);
            }
            // close the cursor
            if (c != null) {
                c.close();
            }
            return list;
        }

        public boolean deletePkgUsage(ContentResolver cr, String selection, String[] selectArgs) {
            if (cr == null) {
                Log.w(TAG, "invalid ContentResolver.");
                return false;
            }
            cr.delete(mUri, selection, selectArgs);
            return true;
        }

        public boolean deletePkgUsage(ContentResolver cr, String pkgname) {
            if (cr == null) {
                Log.w(TAG, "invalid ContentResolver.");
                return false;
            }
            cr.delete(mUri, "pkgname=?", new String[] {
                pkgname
            });
            return true;
        }

    }

    /** @hide **/
    public static final class PkgUsages implements BaseColumns {

        private static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/pkgusage");

        // database filed
        public static final String PKGNAME = "pkgname";

        public static final String TIME = "time";

        public static final String[] PROJECTION_ALL = {
                _ID, PKGNAME, TIME
        };

        private static final String CALL_METHOD_PUT_PKGUSAGE = "method_put_pkgusage";

        private static final String CALL_METHOD_GET_PKGUSAGE = "method_get_pkgusage";

        private static final String CALL_METHOD_DEL_PKGUSAGE = "method_del_pkgusage";

        private static PkgUsagesCache sPkgUsagesCache = new PkgUsagesCache(CONTENT_URI, CALL_METHOD_PUT_PKGUSAGE,
                CALL_METHOD_GET_PKGUSAGE, CALL_METHOD_DEL_PKGUSAGE);

        /** @hide **/
        public static boolean putPkgUsage(ContentResolver cr, PkgUsage pkgUsage) {
            if (DBG) {
                Log.d(TAG, (pkgUsage == null) ? "pkgUsage is null" : pkgUsage.toString());
            }
            if (pkgUsage == null) {
                Log.w(TAG, "PkgUsage must not be null.");
                return false;
            }

            // Pkgname must not be empty
            if (TextUtils.isEmpty(pkgUsage.mPkgname)) {
                Log.w(TAG, "Pkgname must not be empty.");
                return false;
            }

            return sPkgUsagesCache.putPkgUsage(cr, pkgUsage);
        }

        /** @hide **/
        public static boolean updatePkgUsage(ContentResolver cr, PkgUsage pkgUsage) {
            if (DBG) {
                Log.d(TAG, (pkgUsage == null) ? "pkgUsage is null" : pkgUsage.toString());
            }
            if (pkgUsage == null) {
                Log.w(TAG, "PkgUsage must not be null.");
                return false;
            }

            // Pkgname must not be empty
            if (TextUtils.isEmpty(pkgUsage.mPkgname)) {
                Log.w(TAG, "Pkgname must not be empty.");
                return false;
            }

            return sPkgUsagesCache.updatePkgUsage(cr, pkgUsage);
        }

        /** @hide **/
        public static List<PkgUsage> getPkgUsage(ContentResolver cr) {
            if (DBG) {
                Log.d(TAG, "getPkgUsage...");
            }

            return sPkgUsagesCache.getPkgUsage(cr);
        }

        /** @hide **/
        public static List<PkgUsage> getPkgUsage(ContentResolver cr, String[] projection, String selection,
                String[] selectArgs, String sortOrder) {
            if (projection == null || projection.length == 0) {
                Log.w(TAG, "projection must not be empty.");
                return Lists.newArrayList();
            }

            if (TextUtils.isEmpty(selection)) {
                Log.w(TAG, "selection must not be empty.");
                return Lists.newArrayList();
            }

            if (selectArgs == null || selectArgs.length == 0) {
                Log.w(TAG, "selectArgs must not be empty.");
                return Lists.newArrayList();
            }

            if (DBG) {
                StringBuffer sb = new StringBuffer();
                sb.append("\n");
                sb.append("projection : [");
                for (String cell : projection) {
                    sb.append(cell);
                    sb.append(" ");
                }
                sb.append("]");
                sb.append("selection : [");
                sb.append(selection);
                sb.append("]");
                sb.append(" selectArgs : [ ");
                for (String item : selectArgs) {
                    sb.append(item);
                    sb.append(" ");
                }
                sb.append("]");
                sb.append("\n");
                Log.d(TAG, "args : " + sb.toString());
            }

            return sPkgUsagesCache.getPkgUsage(cr, projection, selection, selectArgs, sortOrder);
        }

        /** @hide **/
        public static boolean deletePkgUsage(ContentResolver cr, String selection, String[] selectArgs) {
            // FIXME Permission check
            return sPkgUsagesCache.deletePkgUsage(cr, selection, selectArgs);
        }

        /** @hide **/
        public static boolean deletePkgUsage(ContentResolver cr, String pkgname) {
            if (TextUtils.isEmpty(pkgname)) {
                Log.d(TAG, "pkgname == null, no need to delete PkgUsage");
                return false;
            }

            // FIXME Permission check
            return sPkgUsagesCache.deletePkgUsage(cr, pkgname);
        }
    }

    private static class SecurityCache extends CacheObject {

        public SecurityCache(Uri uri, String putCommand, String getCommand) {
            super(uri, putCommand, getCommand);
        }

        public boolean putBlacklist(ContentResolver cr, Blacklist blacklist) {
            if (cr == null) {
                Log.w(TAG, "invalid ContentResolver.");
                return false;
            }

            ContentValues values = new ContentValues();
            values.put(Security.PACKAGE, blacklist.mPackage);
            values.put(Security.MESSAGE, blacklist.mMessage);
            values.put(Security.LEVEL, blacklist.mLevel);
            cr.insert(mUri, values);

            return true;
        }

        public Blacklist getBlacklistForPkg(ContentResolver cr, String pkg) {
            if (cr == null) {
                Log.w(TAG, "invalid ContentResolver.");
                return new Blacklist();
            }
            Cursor c = cr.query(mUri, new String[] {
                    "package", "message", "level"
            }, "package=?", new String[] {
                pkg
            }, null);

            // must be only one column
            if (c == null || c.getCount() != 1) {
                Log.w(TAG, "Can't get blacklist item from " + mUri + " for " + pkg);

                return new Blacklist();
            }

            c.moveToFirst();
            Blacklist item = new Blacklist();
            item.mPackage = c.getString(c.getColumnIndex(Security.PACKAGE));
            item.mMessage = c.getString(c.getColumnIndex(Security.MESSAGE));
            item.mLevel = c.getInt(c.getColumnIndex(Security.LEVEL));

            // close the cursor
            if (c != null) {
                c.close();
            }

            return item;
        }
    }

    /** @hide **/
    public static class Security implements BaseColumns {

        private static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/security");

        public static final String PACKAGE = "package";

        public static final String MESSAGE = "message";

        public static final String LEVEL = "level";

        public static final int LEVEL_DEFAULT = 0;

        public static final int LEVEL_WARNING = LEVEL_DEFAULT + 1;

        public static final int LEVEL_FORBIDDEN = LEVEL_DEFAULT + 2;

        private static final String BUNDLE_KEY = "bundle_key";

        private static final String BUNDLE_VALUE = "bundle_value";

        private static final String CALL_METHOD_PUT_BLACKLIST = "method_put_blacklist";

        private static final String CALL_METHOD_GET_BLACKLIST = "method_get_blacklist";

        private static SecurityCache sSecurityCache = new SecurityCache(CONTENT_URI, CALL_METHOD_PUT_BLACKLIST,
                CALL_METHOD_GET_BLACKLIST);

        public static boolean putBlacklist(ContentResolver cr, Blacklist blacklist) {
            if (blacklist == null) {
                return false;
            }

            if (TextUtils.isEmpty(blacklist.mPackage)) {
                return false;
            }

            return sSecurityCache.putBlacklist(cr, blacklist);
        }

        public static Blacklist getBlacklistForPkg(ContentResolver cr, String pkg) {
            if (TextUtils.isEmpty(pkg)) {
                return new Blacklist();
            }

            return sSecurityCache.getBlacklistForPkg(cr, pkg);
        }
    }

    private static class UserCache extends CacheObject {

        public UserCache(Uri uri, String putCommand, String getCommand) {
            super(uri, putCommand, getCommand);
        }

        public boolean putUser(ContentResolver cr, Usr user) {
            if (cr == null) {
                Log.w(TAG, "invalid ContentResolver.");
                return false;
            }

            ContentValues values = new ContentValues();
            values.put(User.NAME, user.mName);
            values.put(User.BONUS, user.mBonus);
            values.put(User.COIN, user.mCoin);
            values.put(User.TIME, user.mTime);
            values.put(User.REMARK, user.mRemark);
            values.put(User.RESERVE, user.mReserve);
            cr.insert(mUri, values);
            return true;
        }

        public Usr getUser(ContentResolver cr) {
            if (cr == null) {
                Log.w(TAG, "invalid ContentResolver.");
                return new Usr();
            }

            Cursor c = cr.query(mUri, User.PROJECTION, null, null, null);
            // must be only one column
            if (c == null || c.getCount() != 1) {
                Log.w(TAG, "Can't get user from " + mUri);
                return new Usr();
            }

            c.moveToFirst();
            Usr item = new Usr();
            item.mName = c.getString(c.getColumnIndex(User.NAME));
            item.mBonus = c.getLong(c.getColumnIndex(User.BONUS));
            item.mCoin = c.getLong(c.getColumnIndex(User.COIN));
            item.mTime = c.getLong(c.getColumnIndex(User.TIME));
            item.mRemark = c.getString(c.getColumnIndex(User.REMARK));
            item.mReserve = c.getString(c.getColumnIndex(User.RESERVE));
            // close cursor
            if (c != null) {
                c.close();
            }

            return item;
        }
    }

    /** @hide **/
    public static class User implements BaseColumns {

        private static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/user");

        static final String NAME = "name";

        static final String BONUS = "bonus";

        static final String COIN = "coin";

        static final String TIME = "time";

        static final String REMARK = "remark";

        static final String RESERVE = "reserve";

        private static final String CALL_METHOD_PUT_USER = "method_put_user";

        private static final String CALL_METHOD_GET_USER = "method_get_user";

        private static final String USER_GUEST = "guest";

        private static final String PROJECTION[] = new String[] {
                NAME, BONUS, COIN, TIME, REMARK, RESERVE
        };

        private static UserCache sUserCache = new UserCache(CONTENT_URI, CALL_METHOD_PUT_USER, CALL_METHOD_GET_USER);

        public static boolean putUser(ContentResolver cr, Usr user) {
            if (user == null) {
                Log.e(TAG, "Object is empty, please check");
                return false;
            }

            if (TextUtils.isEmpty(user.mName)) {
                Log.w(TAG, "name must not be empty");
                return false;
            }

            return sUserCache.putUser(cr, user);
        }

        public static Usr getUser(ContentResolver cr) {
            return sUserCache.getUser(cr);
        }
    }

    private static class MessagesCache extends CacheObject {

        public MessagesCache(Uri uri, String putCommand, String getCommand) {
            super(uri, putCommand, getCommand);
        }

        public boolean putMessage(ContentResolver cr, Msg message) {
            if (cr == null) {
                Log.w(TAG, "invalid ContentResolver.");
                return false;
            }

            List<Msg> msgUserList = Msgusers.getMsguser(cr, Msgusers.PROJECTION_DEFAULT, Messages.SELECTION_DEFAULT,
                    new String[] {
                        message.mUserId
                    });
            if (msgUserList.size() == 0) {
                Msgusers.putMsguser(cr, message);
            } else if (msgUserList.get(0).mBlocked == 1) {
                Log.d(TAG, "The current user has been blocked.");
                return false;
            } else {
                ContentValues msgUserValues = new ContentValues();
                msgUserValues.put(Msgusers.NICKNAME, message.mUserInfo);
                msgUserValues.put(Msgusers.THUMBNAIL, message.mImgUrl);
                msgUserValues.put(Msgusers.SOURCE, message.mSource);
                msgUserValues.put(Msgusers.BLOCKED, message.mBlocked);
                Msgusers.updateMsguser(cr, msgUserValues, Messages.SELECTION_DEFAULT, new String[] {
                    message.mUserId
                });
            }

            ContentValues values = new ContentValues();
            values.put(Messages.USERID, message.mUserId);
            values.put(Messages.TITLE, message.mTitle);
            values.put(Messages.STATUS, message.mStatus);
            values.put(Messages.DATA, mergeStr(message.mThumb, message.mData));
            values.put(Messages.EXTRA, message.mExtra);
            values.put(Messages.CATEGORY, message.mCategory);
            values.put(Messages.RESERVE, message.mReserve);
            if (message.mTime == 0) {
                Calendar calendar = Calendar.getInstance();
                values.put(Messages.TIME, (calendar.getTimeInMillis()) / 1000);
            } else {
                values.put(Messages.TIME, message.mTime);
            }
            values.put(Msgusers.NICKNAME, message.mUserInfo);
            values.put(Msgusers.THUMBNAIL, message.mImgUrl);
            values.put(Msgusers.SOURCE, message.mSource);
            values.put(Msgusers.BLOCKED, message.mBlocked);
            cr.insert(mUri, values);
            return true;
        }

        public List<Msg> getMessage(ContentResolver cr) {
            if (cr == null) {
                Log.w(TAG, "invalid ContentResolver.");
                return Lists.newArrayList();
            }

            Cursor cursor = cr.query(mUri, Messages.PROJECTION_DEFAULT, null, null, null);
            // Null check
            if (cursor == null) {
                Log.w(TAG, "Can't get message from " + mUri);
                return Lists.newArrayList();
            }

            List<Msg> list = Lists.newArrayList();
            while (cursor.moveToNext()) {
                Msg item = new Msg();
                item.mId = cursor.getLong(cursor.getColumnIndex(Messages._ID));
                item.mUserId = cursor.getString(cursor.getColumnIndex(Messages.USERID));
                item.mTitle = cursor.getString(cursor.getColumnIndex(Messages.TITLE));
                item.mTime = cursor.getLong(cursor.getColumnIndex(Messages.TIME));
                item.mStatus = cursor.getInt(cursor.getColumnIndex(Messages.STATUS));
                item.mData = splitStr(cursor.getString(cursor.getColumnIndex(Messages.DATA)))[1];
                item.mThumb = splitStr(cursor.getString(cursor.getColumnIndex(Messages.DATA)))[0];
                item.mExtra = cursor.getString(cursor.getColumnIndex(Messages.EXTRA));
                item.mCategory = cursor.getInt(cursor.getColumnIndex(Messages.CATEGORY));
                item.mReserve = cursor.getString(cursor.getColumnIndex(Messages.RESERVE));

                list.add(item);
            }
            // close the cursor
            if (cursor != null) {
                cursor.close();
            }

            return list;
        }

        public List<Msg> getMessage(ContentResolver cr, String[] projection, String selection, String[] selectArgs) {
            if (cr == null) {
                Log.w(TAG, "invalid ContentResolver.");
                return Lists.newArrayList();
            }

            Cursor cursor = cr.query(mUri, projection, selection, selectArgs, null);
            // Null check
            if (cursor == null) {
                Log.w(TAG, "Can't get value from " + mUri);
                return Lists.newArrayList();
            }

            List<Msg> list = Lists.newArrayList();
            while (cursor.moveToNext()) {
                Msg item = new Msg();
                item.mId = cursor.getLong(cursor.getColumnIndex(Messages._ID));
                item.mUserId = cursor.getString(cursor.getColumnIndex(Messages.USERID));
                item.mTitle = cursor.getString(cursor.getColumnIndex(Messages.TITLE));
                item.mTime = cursor.getLong(cursor.getColumnIndex(Messages.TIME));
                item.mStatus = cursor.getInt(cursor.getColumnIndex(Messages.STATUS));
                item.mData = splitStr(cursor.getString(cursor.getColumnIndex(Messages.DATA)))[1];
                item.mThumb = splitStr(cursor.getString(cursor.getColumnIndex(Messages.DATA)))[0];
                item.mExtra = cursor.getString(cursor.getColumnIndex(Messages.EXTRA));
                item.mCategory = cursor.getInt(cursor.getColumnIndex(Messages.CATEGORY));
                item.mReserve = cursor.getString(cursor.getColumnIndex(Messages.RESERVE));
                // add to list
                list.add(item);
            }
            // close the cursor
            if (cursor != null) {
                cursor.close();
            }

            return list;
        }

        public List<Msg> getMessage(ContentResolver cr, String userId) {
            if (cr == null) {
                Log.w(TAG, "invalid ContentResolver.");
                return Lists.newArrayList();
            }

            Cursor cursor = cr.query(mUri, Messages.PROJECTION_DEFAULT, Messages.SELECTION_DEFAULT, new String[] {
                userId
            }, null);
            // must be only one column
            if (cursor == null) {
                Log.w(TAG, "Can't get message from " + mUri);
                return Lists.newArrayList();
            }

            List<Msg> list = Lists.newArrayList();
            cursor.moveToFirst();
            while (cursor.moveToNext()) {
                Msg item = new Msg();
                item.mId = cursor.getLong(cursor.getColumnIndex(Messages._ID));
                item.mUserId = cursor.getString(cursor.getColumnIndex(Messages.USERID));
                item.mTitle = cursor.getString(cursor.getColumnIndex(Messages.TITLE));
                item.mTime = cursor.getLong(cursor.getColumnIndex(Messages.TIME));
                item.mStatus = cursor.getInt(cursor.getColumnIndex(Messages.STATUS));
                item.mData = splitStr(cursor.getString(cursor.getColumnIndex(Messages.DATA)))[1];
                item.mThumb = splitStr(cursor.getString(cursor.getColumnIndex(Messages.DATA)))[0];
                item.mExtra = cursor.getString(cursor.getColumnIndex(Messages.EXTRA));
                item.mCategory = cursor.getInt(cursor.getColumnIndex(Messages.CATEGORY));
                item.mReserve = cursor.getString(cursor.getColumnIndex(Messages.RESERVE));

                list.add(item);
            }

            // close the cursor
            if (cursor != null) {
                cursor.close();
            }

            return list;
        }

        public boolean deleteMessage(ContentResolver cr) {
            if (cr == null) {
                Log.w(TAG, "invalid ContentResolver.");
                return false;
            }
            cr.delete(mUri, null, null);
            return true;
        }

        public boolean deleteMessage(ContentResolver cr, Msg message) {
            if (cr == null) {
                Log.w(TAG, "invalid ContentResolver.");
                return false;
            }
            cr.delete(mUri, "_id=?", new String[] {
                Long.toString(message.mId)
            });
            return true;
        }

        public boolean deleteMessage(ContentResolver cr, String selection, String[] selectArgs) {
            if (cr == null) {
                Log.w(TAG, "invalid ContentResolver.");
                return false;
            }
            cr.delete(mUri, selection, selectArgs);
            return true;
        }

        public boolean updateMessage(ContentResolver cr, Msg message) {
            if (cr == null) {
                Log.w(TAG, "invalid ContentResolver.");
                return false;
            }
            ContentValues values = new ContentValues();
            values.put(Messages._ID, message.mId);
            values.put(Messages.STATUS, message.mStatus);

            cr.update(mUri, values, "_id=?", new String[] {
                Long.toString(message.mId)
            });
            return true;
        }

        public boolean updateMessage(ContentResolver cr, ContentValues values, String selection, String[] selectArgs) {
            if (cr == null) {
                Log.w(TAG, "invalid ContentResolver.");
                return false;
            }
            cr.update(mUri, values, selection, selectArgs);
            return true;
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

        private String[] splitStr(String str) {
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

    /** @hide **/
    public static class Messages implements BaseColumns {

        private static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/message");

        // the source of the message from the different applications or system.
        public static final int SOURCE_NULL = 0;

        public static final int SOURCE_ISYNERGY = SOURCE_NULL + 2;

        public static final int SOURCE_CPE = SOURCE_NULL + 3;

        public static final int SOURCE_WECHAT = SOURCE_NULL + 4;

        public static final int SOURCE_SCIFLYKU = SOURCE_NULL + 5;

        public static final String USERID_CPE = "cpe" + SOURCE_CPE;

        // the category of the message
        public static final int CATEGORY_NULL = 0;

        public static final int CATEGORY_TEXT = CATEGORY_NULL + 1;

        public static final int CATEGORY_APK = CATEGORY_NULL + 2;

        public static final int CATEGORY_IMAGE = CATEGORY_NULL + 3;

        public static final int CATEGORY_URL = CATEGORY_NULL + 4;

        public static final int CATEGORY_VIDEO = CATEGORY_NULL + 5;

        public static final int CATEGORY_MUSIC = CATEGORY_NULL + 6;

        public static final int CATEGORY_VOICE = CATEGORY_NULL + 7;

        public static final int CATEGORY_EPG = CATEGORY_NULL + 8;

        public static final int CATEGORY_BROADCAST = CATEGORY_NULL + 9;

        public static final int CATEGORY_UPDATE = CATEGORY_NULL + 10;

        public static final int CATEGORY_UPDATE_FORCED = CATEGORY_NULL + 11;

        public static final int CATEGORY_UPDATE_RESULT = CATEGORY_NULL + 12;

        public static final int CATEGORY_EPG_CACHE = CATEGORY_NULL + 13;

        public static final int CATEGORY_UPLOAD_LOG = CATEGORY_NULL + 14;

        public static final int CATEGORY_BOOT_LOGO = CATEGORY_NULL + 15;

        public static final int CATEGORY_APK_INSTALL = CATEGORY_NULL + 16;

        public static final int CATEGORY_APK_UNINSTALL = CATEGORY_NULL + 17;

        public static final int CATEGORY_DOCUMENT = CATEGORY_NULL + 18;

        public static final int CATEGORY_APK_STARTUP = CATEGORY_NULL + 19;

        public static final int CATEGORY_ADS_SWITCH = CATEGORY_NULL + 20;

        public static final int CATEGORY_UPLOAD_SCREEN_SHOT = CATEGORY_NULL + 21;

        public static final int CATEGORY_MSG_LIVE = CATEGORY_NULL + 22;

        public static final int CATEGORY_BOOT_VIDEO = CATEGORY_NULL + 23;

        public static final int CATEGORY_BOOT_VIDEO_RECOVERY = CATEGORY_NULL + 24;

        public static final int CATEGORY_UPDATE_CLOUD_PUSH = CATEGORY_NULL + 25;

        static final String DEFAULT_SORT_ORDER = "time";

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

        private static final String SELECTION_DEFAULT = "userid=?";

        private static final String CALL_METHOD_PUT_MESSAGE = "method_put_message";

        private static final String CALL_METHOD_GET_MESSAGE = "method_get_message";

        private static MessagesCache sMessageCache = new MessagesCache(CONTENT_URI, CALL_METHOD_PUT_MESSAGE,
                CALL_METHOD_GET_MESSAGE);

        /** @hide **/
        public static boolean putMessage(ContentResolver cr, Msg message) {
            if (DBG) {
                Log.d(TAG, (message == null) ? "messages is null" : message.toString());
            }
            if (message == null) {
                Log.w(TAG, "MessageCenter must not be null.");
                return false;
            }
            if (TextUtils.isEmpty(message.mTitle)) {
                Log.w(TAG, "Title must not be empty.");
                return false;
            }

            return sMessageCache.putMessage(cr, message);
        }

        /** @hide **/
        public static List<Msg> getMessage(ContentResolver cr) {
            if (DBG) {
                Log.d(TAG, "getMessage of all.");
            }

            return sMessageCache.getMessage(cr);
        }

        /** @hide **/
        public static List<Msg> getMessage(ContentResolver cr, String[] projection, String selection,
                String[] selectArgs) {
            if (projection == null || projection.length == 0) {
                Log.w(TAG, "projection must not be empty.");
                return Lists.newArrayList();
            }

            if (TextUtils.isEmpty(selection)) {
                Log.w(TAG, "selection must not be empty.");
                return Lists.newArrayList();
            }

            if (selectArgs == null || selectArgs.length == 0) {
                Log.w(TAG, "selectArgs must not be empty.");
                return Lists.newArrayList();
            }

            if (DBG) {
                StringBuffer sb = new StringBuffer();
                sb.append("\n");
                sb.append("projection : [");
                for (String cell : projection) {
                    sb.append(cell);
                    sb.append(" ");
                }
                sb.append("]");
                sb.append("selection : [");
                sb.append(selection);
                sb.append("]");
                sb.append(" selectArgs : [ ");
                for (String item : selectArgs) {
                    sb.append(item);
                    sb.append(" ");
                }
                sb.append("]");
                sb.append("\n");
                Log.d(TAG, "args : " + sb.toString());
            }

            return sMessageCache.getMessage(cr, projection, selection, selectArgs);
        }

        /** @hide **/
        public static List<Msg> getMessage(ContentResolver cr, String userId) {
            if (DBG) {
                Log.d(TAG, "getMessage by userId.");
            }

            return sMessageCache.getMessage(cr, userId);
        }

        /** @hide **/
        public static boolean deleteMessage(ContentResolver cr) {
            return sMessageCache.deleteMessage(cr);
        }

        /** @hide **/
        public static boolean deleteMessage(ContentResolver cr, Msg message) {
            return sMessageCache.deleteMessage(cr, message);
        }

        /** @hide **/
        public static boolean deleteMessage(ContentResolver cr, String selection, String[] selectArgs) {
            // FIXME Permission check
            return sMessageCache.deleteMessage(cr, selection, selectArgs);
        }

        /** @hide **/
        public static boolean updateMessage(ContentResolver cr, Msg message) {
            return sMessageCache.updateMessage(cr, message);
        }

        /** @hide **/
        public static boolean updateMessage(ContentResolver cr, ContentValues values, String selection,
                String[] selectArgs) {
            return sMessageCache.updateMessage(cr, values, selection, selectArgs);
        }
    }

    private static class MsgusersCache extends CacheObject {

        public MsgusersCache(Uri uri, String putCommand, String getCommand) {
            super(uri, putCommand, getCommand);
        }

        public boolean putMsguser(ContentResolver cr, Msg message) {
            if (cr == null) {
                Log.w(TAG, "invalid ContentResolver.");
                return false;
            }
            ContentValues values = new ContentValues();
            values.put(Msgusers.USERID, message.mUserId);
            values.put(Msgusers.NICKNAME, message.mUserInfo);
            values.put(Msgusers.THUMBNAIL, message.mImgUrl);
            values.put(Msgusers.BLOCKED, message.mBlocked);
            values.put(Msgusers.SOURCE, message.mSource);
            cr.insert(mUri, values);
            return true;
        }

        public List<Msg> getMsguser(ContentResolver cr, String[] projection, String selection, String[] selectArgs) {
            if (cr == null) {
                Log.w(TAG, "invalid ContentResolver.");
                return Lists.newArrayList();
            }
            Cursor cursor = cr.query(mUri, projection, selection, selectArgs, null);
            // Null check
            if (cursor == null) {
                Log.w(TAG, "Can't get value from " + mUri);
                return Lists.newArrayList();
            }

            List<Msg> list = Lists.newArrayList();
            while (cursor.moveToNext()) {
                Msg item = new Msg();
                item.mId = cursor.getLong(cursor.getColumnIndex(Msgusers._ID));
                item.mUserId = cursor.getString(cursor.getColumnIndex(Msgusers.USERID));
                item.mUserInfo = cursor.getString(cursor.getColumnIndex(Msgusers.NICKNAME));
                item.mImgUrl = cursor.getString(cursor.getColumnIndex(Msgusers.THUMBNAIL));
                item.mBlocked = cursor.getInt(cursor.getColumnIndex(Msgusers.BLOCKED));
                item.mSource = cursor.getInt(cursor.getColumnIndex(Msgusers.SOURCE));
                // add to list
                list.add(item);
            }
            // close the cursor
            if (cursor != null) {
                cursor.close();
            }

            return list;
        }

        public boolean deleteMsguser(ContentResolver cr, String selection, String[] selectArgs) {
            if (cr == null) {
                Log.w(TAG, "invalid ContentResolver.");
                return false;
            }
            cr.delete(mUri, selection, selectArgs);
            return true;
        }

        public boolean updateMsguser(ContentResolver cr, ContentValues values, String selection, String[] selectArgs) {
            if (cr == null) {
                Log.w(TAG, "invalid ContentResolver.");
                return false;
            }
            cr.update(mUri, values, selection, selectArgs);
            return true;
        }
    }

    /** @hide **/
    public static class Msgusers implements BaseColumns {

        private static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/msguser");

        static final String DEFAULT_SORT_ORDER = "_id";

        static final String USERID = "userid";

        static final String NICKNAME = "nickname";

        static final String THUMBNAIL = "thumbnail";

        static final String BLOCKED = "blocked";

        static final String SOURCE = "source";

        private static final String[] PROJECTION_DEFAULT = {
                _ID, USERID, NICKNAME, THUMBNAIL, BLOCKED, SOURCE
        };

        private static final String CALL_METHOD_PUT_MSGUSER = "method_put_msguser";

        private static final String CALL_METHOD_GET_MSGUSER = "method_get_msguser";

        private static MsgusersCache sMsguserCache = new MsgusersCache(CONTENT_URI, CALL_METHOD_PUT_MSGUSER,
                CALL_METHOD_GET_MSGUSER);

        /** @hide **/
        public static boolean putMsguser(ContentResolver cr, Msg message) {
            if (DBG) {
                Log.d(TAG, (message == null) ? "message is null" : message.toString());
            }
            if (message == null) {
                Log.w(TAG, "MessageCenter must not be null.");
                return false;
            }
            if (TextUtils.isEmpty(message.mUserId)) {
                Log.w(TAG, "UserId must not be empty.");
                return false;
            }

            return sMsguserCache.putMsguser(cr, message);
        }

        /** @hide **/
        public static List<Msg> getMsguser(ContentResolver cr, String[] projection, String selection,
                String[] selectArgs) {
            if (projection == null || projection.length == 0) {
                Log.w(TAG, "projection must not be empty.");
                return Lists.newArrayList();
            }

            if (TextUtils.isEmpty(selection)) {
                Log.w(TAG, "selection must not be empty.");
                return Lists.newArrayList();
            }

            if (selectArgs == null || selectArgs.length == 0) {
                Log.w(TAG, "selectArgs must not be empty.");
                return Lists.newArrayList();
            }

            if (DBG) {
                StringBuffer sb = new StringBuffer();
                sb.append("\n");
                sb.append("projection : [");
                for (String cell : projection) {
                    sb.append(cell);
                    sb.append(" ");
                }
                sb.append("]");
                sb.append("selection : [");
                sb.append(selection);
                sb.append("]");
                sb.append(" selectArgs : [ ");
                for (String item : selectArgs) {
                    sb.append(item);
                    sb.append(" ");
                }
                sb.append("]");
                sb.append("\n");
                Log.d(TAG, "args : " + sb.toString());
            }

            return sMsguserCache.getMsguser(cr, projection, selection, selectArgs);
        }

        /** @hide **/
        public static boolean deleteMsguser(ContentResolver cr, String selection, String[] selectArgs) {
            // FIXME Permission check
            return sMsguserCache.deleteMsguser(cr, selection, selectArgs);
        }

        /** @hide **/
        public static boolean updateMsguser(ContentResolver cr, ContentValues values, String selection,
                String[] selectArgs) {
            return sMsguserCache.updateMsguser(cr, values, selection, selectArgs);
        }
    }

}
