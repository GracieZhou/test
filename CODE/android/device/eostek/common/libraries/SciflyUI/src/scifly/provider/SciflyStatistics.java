
package scifly.provider;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import scifly.device.Device;
import scifly.provider.metadata.StatisticsRecord;
import scifly.um.EosUploadManager;
import scifly.um.EosUploadTask;
import scifly.um.EosUploadListener;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

/**
 * This is a tool class to record user's behavior. Can be called like:
 * SciflyStatistics.getInstance(context).recordEvent(activity, "test", "key",
 * "value");
 * 
 * @author Psso.Song
 */
public class SciflyStatistics {
    private static final String TAG = "SciflyStatistics";

    private static final boolean DBG = true;

    private static final long TIME_STAMP_THRESHOLD = 1420041600000L; // 2015-01-01

    private static final long THREE_DATAS = 3 * 24 * 60 * 60 * 1000;

    private static final int UPLOAD_ITEM_THRESHOLD = 500;

    private static final String DEFAULT_SELECTION = "time>=? and time<=?";

    private static final String DEFAULT_ORDER_BY = "time";

    private static final String DEFAULT_SERVER_URL = "http://bigdata.88popo.com:8011/userFeed/UserInfoRep";

    private static final String DATA_START_TIME = "start";

    private static final String DATA_END_TIME = "end";

    private static final String DATA_DEFAULT_TIME = "0";

    public static final int DEFAULT_STATISTICS_BATCH_COUNT = 50;

    public static final long DEFAULT_STATISTICS_MIN_INTERVAL_SECOND = 5 * 60;

    public static final long DEFAULT_STATISTICS_INTERVAL_SECOND = 12 * 60 * 60;

    public static final Uri STATISTICS_URI_GLOBAL = Uri.parse("content://com.eostek.scifly.provider/global");

    public static final Uri STATISTICS_URI_STATISTICS = Uri.parse("content://com.eostek.scifly.provider/statistics");

    public static final String ACTION_STATISTICS_TRIGGER_UPLOAD = "scifly.intent.action.UPLOAD_STATISTICS";

    public static final String ACTION_STATISTICS_UPDATE_CONFIGURATION = "scifly.intent.action.UPDATE_CONFIGURATION";

    public static final String ACTION_BIGDATA_UPDATENOTIFY = "com.eostek.bigdata.updatenotify";

    private static final int MSG_DELETE_STALE_RECORD = 0;

    private static final int MSG_UPDATE_CONFIGURATION = 1;

    public static final String SHARED_PREFERENCE_NAME = "scifly_statistics";

    public static final String PREFERENCE_NEED_TRIGGER = "need_trigger";

    private volatile static SciflyStatistics instance;

    private Context mContext;

    private String mCurrentActivityName;

    private EosUploadManager mUploadManager;

    private ConnectivityManager mConnectivityManager;

    private HandlerThread mHandlerThread;

    private final StatisticsHandler mHandler;

    private class StatisticsHandler extends Handler {
        public StatisticsHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_DELETE_STALE_RECORD:
                    Bundle data = msg.getData();
                    String startTime = data.getString(DATA_START_TIME, DATA_DEFAULT_TIME);
                    String endTime = data.getString(DATA_END_TIME, DATA_DEFAULT_TIME);
                    if (DBG) {
                        Log.d(TAG, "Upload successfully, trying to delete data from " + startTime + " to " + endTime
                                + "!");
                    }
                    SciflyStore.StatisticsRecords.deleteStatisticsRecord(mContext.getContentResolver(),
                            DEFAULT_SELECTION, new String[] {
                                    startTime, endTime
                            });
                    break;
                case MSG_UPDATE_CONFIGURATION:
                    updateConfiguration();
                    break;
                default:
            }
        }

        private void updateConfiguration() {
            try {
                URL url = new URL(DEFAULT_SERVER_URL);
                HttpURLConnection conn = (HttpURLConnection) url.openConnection();
                conn.setRequestProperty("Content-Type", "text/html; charset=UTF-8");
                conn.setRequestProperty("Accept-Encoding", "json");
                conn.setRequestMethod("POST");
                conn.setDoInput(true);
                conn.setDoOutput(true);
                conn.setConnectTimeout(5000);

                OutputStreamWriter osw = new OutputStreamWriter(conn.getOutputStream(), "utf-8");
                JSONObject json = new JSONObject();
                json.put("ifid", "config");
                String mac = Device.getHardwareAddress(mContext);
                String[] macArray = mac.split(":");
                StringBuffer sb = new StringBuffer();
                for(int i = 0; i < macArray.length; i++){
                 sb. append(macArray[i]);
                }
                String macstr = sb.toString();
                json.put("mac", macstr);
                String updtime = null;
                SharedPreferences preference = mContext.getSharedPreferences(SHARED_PREFERENCE_NAME,
                        Context.MODE_PRIVATE);
                if (preference.getString("updtime", "") == null ||(preference.getString("updtime", "").length() == 0)) {
                    updtime = "0";
                } else {
                    updtime = preference.getString("updtime", "");
                }
                json.put("updTime", updtime);
                json.put("devCode", Device.getDeviceCode());
      Log.d(TAG, "Accept-Encoding1 = " + conn.getRequestProperty("Accept-Encoding"));
                osw.write(json.toString());
                osw.flush();
                osw.close();

                Log.d(TAG, "Response Code = " + conn.getResponseCode());

                InputStream is = conn.getInputStream();
                BufferedReader br = new BufferedReader(new InputStreamReader(is, "utf-8"));
                StringBuffer buffer = new StringBuffer();
                String line = "";
                while ((line = br.readLine()) != null) {
                    buffer.append(line);
                }
                String result = buffer.toString();
                br.close();
                is.close();
                Log.d(TAG, "Response InputStream = " + result);
                JSONObject res = new JSONObject(result);
                int err = res.optInt("err");
                if (err == 0) {
                    Editor edit = preference.edit();
                    edit.putString("updtime", String.valueOf(System.currentTimeMillis()/1000));
                    edit.commit();
                    int batch = res.optInt("batch");
                    if (batch > 0) {
                        SciflyStore.Global.putString(mContext.getContentResolver(),
                                SciflyStore.Global.STATISTICS_BATCH, String.valueOf(batch));
                    }
                    long interval = res.optLong("interval");
                    if (interval > 0) {
                        SciflyStore.Global.putString(mContext.getContentResolver(),
                                SciflyStore.Global.STATISTICS_INTERVAL, String.valueOf(interval));
                    }
                }
            } catch (MalformedURLException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (JSONException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * This function will request a http connection to server and modify
     * database statistics_batch and statistics_interval.
     */
    public void updateConfiguration() {
        if (mHandler != null) {
            mHandler.sendEmptyMessage(MSG_UPDATE_CONFIGURATION);
        }
    }

    /**
     * @param enable If true, StatisticsManager will trigger uploading when
     *            network gets ready, otherwise false;
     */
    public void enableTriggerLater(boolean enable) {
        SharedPreferences preference = mContext.getSharedPreferences(SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        Editor edit = preference.edit();
        edit.putBoolean(PREFERENCE_NEED_TRIGGER, enable);
        edit.commit();
    }

    /**
     * This is only method to get an instance of SciflyStatistics.
     * 
     * @param context The application context.
     * @return Get the static Instance of SciflyStatistics.
     */
    public static SciflyStatistics getInstance(Context context) {
        if (instance == null) {
            synchronized (SciflyStatistics.class) {
                if (instance == null) {
                    instance = new SciflyStatistics(context);
                }
            }
        }
        return instance;
    }

    protected SciflyStatistics(Context context) {
        this.mContext = context;
        Log.d(TAG, "SciflyStatistics engine starting...");
        mHandlerThread = new HandlerThread("SciflyStatisticsHandlerThread");
        mHandlerThread.start();
        mHandler = new StatisticsHandler(mHandlerThread.getLooper());
    }

    /** @hide **/
    public boolean recordSessionStart(String pkgname) {
        if (mContext == null) {
            Log.e(TAG, "mContext not Initialize...");
            return false;
        }
        if (TextUtils.isEmpty(pkgname)) {
            Log.e(TAG, "pkgname cannot be empty!");
            return false;
        }
        long time = System.currentTimeMillis();
        if (time < TIME_STAMP_THRESHOLD) {
            Log.e(TAG, "Error failing to get current time [" + time + "]");
            return false;
        }
        StatisticsRecord record = new StatisticsRecord(pkgname, StatisticsRecord.STATISTICS_TYPE_SESSION_IN, time);
        return SciflyStore.StatisticsRecords.putStatisticsRecord(mContext.getContentResolver(), record);
    }

    /** @hide **/
    public boolean recordSessionEnd(String pkgname) {
        if (mContext == null) {
            Log.e(TAG, "mContext not Initialize...");
            return false;
        }
        if (TextUtils.isEmpty(pkgname)) {
            Log.e(TAG, "pkgname cannot be empty!");
            return false;
        }
        long time = System.currentTimeMillis();
        if (time < TIME_STAMP_THRESHOLD) {
            Log.e(TAG, "Error failing to get current time [" + time + "]");
            return false;
        }
        StatisticsRecord record = new StatisticsRecord(pkgname, StatisticsRecord.STATISTICS_TYPE_SESSION_OUT, time);
        return SciflyStore.StatisticsRecords.putStatisticsRecord(mContext.getContentResolver(), record);
    }

    /**
     * Upload all statistics records before current time. Returning true does
     * not mean uploading successfully, but only uploading successfully triggers
     * deleting uploaded data.
     * 
     * @return true if trigger successfully, otherwise false.
     */
    /** @hide **/
    public boolean triggerUpload() {
        if (mContext == null) {
            Log.e(TAG, "mContext not Initialize...");
            return false;
        }
        if (!isNetworkAvailable()) {
            Log.e(TAG, "Network is not available, cancel this uploading!");
            enableTriggerLater(true);
            return false;
        }
        long currentTime = System.currentTimeMillis();

        // Delete data 3 days ago.
        SciflyStore.StatisticsRecords
                .deleteStatisticsRecord(mContext.getContentResolver(), (currentTime - THREE_DATAS));

        // Get record array from database.
        List<StatisticsRecordPiece> records = getStringFromRecords(currentTime);
        if (records == null || records.isEmpty()) {
            Log.e(TAG, "Fail to get StatisticsRecordPiece!");
            return false;
        }

        // Upload record to server.
        for (StatisticsRecordPiece record : records) {
            if (DBG) {
                Log.d(TAG, "StatisticsRecordPiece(" + record.getStartTime() + "-" + record.getEndTime() + "): "
                        + record.getData());
            }
            upload(record);
        }

        return true;
    }

    private boolean isNetworkAvailable() {
        if (mConnectivityManager == null) {
            mConnectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        }
        NetworkInfo info = mConnectivityManager.getActiveNetworkInfo();
        if (info != null) {
            return info.isAvailable();
        }
        return false;
    }

    /**
     * Upload data to server.
     * 
     * @param record StatisticsRecordPiece that need to be uploaded.
     */
    private void upload(final StatisticsRecordPiece record) {
        if (mUploadManager == null) {
            mUploadManager = new EosUploadManager();
        }
        String mac = Device.getHardwareAddress(mContext);
        String[] macArray = mac.split(":");
        StringBuffer sb = new StringBuffer();
        for(int i = 0; i < macArray.length; i++){
         sb. append(macArray[i]);
        }
        String macstr = sb.toString();

        String updtime = null;
        SharedPreferences preference = mContext.getSharedPreferences(SHARED_PREFERENCE_NAME,
                Context.MODE_PRIVATE);
        if (preference.getString("updtime", "") == null ||(preference.getString("updtime", "").length() == 0)) {
            updtime = "0";
        } else {
            updtime = preference.getString("updtime", "");
        }

        HashMap<String, String> map = new HashMap<String, String>();
        map.put("mac", macstr);
        map.put("updTime", updtime);
        map.put("devName", Device.getDeviceName(mContext));
        map.put("ifid", "upload");
        map.put("ver", Device.getVersion());

        map.put("userId",Device.getBb());
        map.put("devCode", Device.getDeviceCode());
        map.put("ifVer", "1.0");
        map.put("type", "statistics");
        EosUploadListener listener = new EosUploadListener() {
            @Override
            public void onUploadCompleted(int errCode) {
                if (DBG) {
                    Log.d(TAG, "onUploadCompleted: " + errCode);
                }
                if (EosUploadManager.RESULT_SUCCESSFUL == errCode || EosUploadManager.RESULT_NEED_UPDATE == errCode) {
                    Message msg = mHandler.obtainMessage(MSG_DELETE_STALE_RECORD, record);
                    Bundle data = new Bundle();
                    data.putString(DATA_START_TIME, String.valueOf(record.getStartTime()));
                    data.putString(DATA_END_TIME, String.valueOf(record.getEndTime()));
                    msg.setData(data);
                    mHandler.sendMessage(msg);
                } else {
                    Log.d(TAG, "This uploading failed, triggers when network is ready!");
                    enableTriggerLater(true);
                }
                if (EosUploadManager.RESULT_NEED_UPDATE == errCode) {
                    mHandler.sendEmptyMessage(MSG_UPDATE_CONFIGURATION);
                }
            }
        };
        EosUploadTask task = new EosUploadTask(0, record.getData(), DEFAULT_SERVER_URL, map, listener);
        mUploadManager.addTask(task);
    }

    /**
     * Get data from database. If data items are more than 500, cut it into
     * pieces.
     * 
     * @param time Deadline time of data.
     * @return List<StatisticsRecordPiece> contains all the data.
     */
    private List<StatisticsRecordPiece> getStringFromRecords(long time) {
        List<StatisticsRecordPiece> result = new ArrayList<StatisticsRecordPiece>();
        result.clear();

        // Get data in the last 3 days.
        List<StatisticsRecord> records = SciflyStore.StatisticsRecords.getStatisticsRecord(
                mContext.getContentResolver(), SciflyStore.StatisticsRecords.PROJECTION_ALL, DEFAULT_SELECTION,
                new String[] {
                        String.valueOf(time - THREE_DATAS), String.valueOf(time)
                }, DEFAULT_ORDER_BY);
        if (records == null || records.size() == 0) {
            Log.e(TAG, "Fail to get data between " + (time - THREE_DATAS) + " and " + time);
            return result;
        }

        int pieceCount = records.size() / UPLOAD_ITEM_THRESHOLD + 1;
        for (int i = 0; i < pieceCount; i++) {
            JSONArray jsonArray = new JSONArray();
            for (int j = 0; j < UPLOAD_ITEM_THRESHOLD; j++) {
                int currentRecordIndex = i * UPLOAD_ITEM_THRESHOLD + j;
                if (currentRecordIndex >= records.size()) {
                    // Reach the end of records list.
                    if (DBG) {
                        Log.d(TAG, "Reach the end of records, i=" + i + ", j=" + j + ", size=" + records.size());
                    }
                    break;
                } else {
                    JSONObject json = recordToJson(records.get(currentRecordIndex));
                    jsonArray.put(json);
                }
            }
            int startIndex = i * UPLOAD_ITEM_THRESHOLD;
            int endIndex = 0;
            if ((endIndex = (i + 1) * UPLOAD_ITEM_THRESHOLD - 1) >= records.size()) {
                endIndex = records.size() - 1;
            }
            if (DBG) {
                Log.d(TAG, "Piece " + i + ": startIndex=" + startIndex + ", endIndex=" + endIndex);
            }
            result.add(new StatisticsRecordPiece(records.get(startIndex).mTime, records.get(endIndex).mTime, jsonArray
                    .toString()));
        }

        return result;
    }

    /**
     * Transform StatisticsRecord to JSONObject
     * 
     * @param record StatisticsRecord that needs to be transformed.
     * @return JSONObject
     */
    private JSONObject recordToJson(StatisticsRecord record) {
        JSONObject json = new JSONObject();
        try {
            json.put(SciflyStore.StatisticsRecords.PKGNAME, record.mPkgname);
            json.put(SciflyStore.StatisticsRecords.ACTIVITY_NAME, record.mActivityName);
            json.put(SciflyStore.StatisticsRecords.TYPE, record.mType);
            json.put(SciflyStore.StatisticsRecords.TIME, record.mTime);
            json.put(SciflyStore.StatisticsRecords.CATEGORY, record.mCategory);
            json.put(SciflyStore.StatisticsRecords.KEY, record.mKey);
            json.put(SciflyStore.StatisticsRecords.VALUE, record.mValue);
            json.put(SciflyStore.StatisticsRecords.PARAMS, new JSONObject(record.mParams));
            if (DBG) {
                Log.d(TAG, "recordToJson(" + json.toString().getBytes("UTF-8").length + "):" + json.toString());
            }
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return json;
    }

    /**
     * Record entrance time of this activity, recommend to be call in
     * onCreate(). If pagename is null or 0-length, record activity class name
     * instead.
     * 
     * @param activity {@link android.app.Activity}
     * @param pagename Custom name of activity, can be null.
     * @return true if record successfully, otherwise false.
     */
    public boolean recordPageStart(Activity activity, String pagename) {
        if (mContext == null) {
            Log.e(TAG, "mContext not Initialize...");
            return false;
        }
        String pkgname = mContext.getPackageName();
        if (TextUtils.isEmpty(pkgname)) {
            Log.e(TAG, "pkgname cannot be empty!");
            return false;
        }
        if (TextUtils.isEmpty(pagename)) {
            mCurrentActivityName = activity.getLocalClassName();
        } else {
            mCurrentActivityName = pagename;
        }
        if (TextUtils.isEmpty(mCurrentActivityName)) {
            Log.e(TAG, "Failed to get current Activity name!");
            return false;
        }
        long time = System.currentTimeMillis();
        if (time < TIME_STAMP_THRESHOLD) {
            Log.e(TAG, "Error failing to get current time [" + time + "]");
            return false;
        }
        StatisticsRecord record = new StatisticsRecord(pkgname, mCurrentActivityName,
                StatisticsRecord.STATISTICS_TYPE_PAGE_IN, time, null, null, null);
        return SciflyStore.StatisticsRecords.putStatisticsRecord(mContext.getContentResolver(), record);
    }

    /**
     * Record exit time of the former activity, must be called after
     * recordPageStart.
     * 
     * @return true if record successfully, otherwise false.
     */
    public boolean recordPageEnd() {
        if (mContext == null) {
            Log.e(TAG, "mContext not Initialize...");
            return false;
        }
        String pkgname = mContext.getPackageName();
        if (TextUtils.isEmpty(pkgname)) {
            Log.e(TAG, "pkgname cannot be empty!");
            return false;
        }
        if (TextUtils.isEmpty(mCurrentActivityName)) {
            Log.e(TAG, "mCurrentActivityName is Empty, you should call recordPageStart first!");
            return false;
        }
        long time = System.currentTimeMillis();
        if (time < TIME_STAMP_THRESHOLD) {
            Log.e(TAG, "Error failing to get current time [" + time + "]");
            return false;
        }
        StatisticsRecord record = new StatisticsRecord(pkgname, mCurrentActivityName,
                StatisticsRecord.STATISTICS_TYPE_PAGE_OUT, time, null, null, null);
        boolean res = SciflyStore.StatisticsRecords.putStatisticsRecord(mContext.getContentResolver(), record);
        // Allow user to recordPageEnd twice if record failed.
        if (res) {
            mCurrentActivityName = null;
        }
        return res;
    }

    /**
     * Record custom event with no more than one parameter. If this event
     * contains more than one parameters, you should call recordEvent(Activity
     * activity, String category, Map<String, String> map).
     * 
     * @param activity {@link android.app.Activity}.
     * @param category Category of this event.
     * @param key Key of parameter.
     * @param value Value of parameter.
     * @return true if record successfully, otherwise false.
     */
    public boolean recordEvent(Activity activity, String category, String key, String value) {
        if (mContext == null) {
            Log.e(TAG, "mContext not Initialize...");
            return false;
        }
        String pkgname = mContext.getPackageName();
        if (TextUtils.isEmpty(pkgname)) {
            Log.e(TAG, "pkgname cannot be empty!");
            return false;
        }
        String pagename = activity.getLocalClassName();
        if (TextUtils.isEmpty(pagename)) {
            Log.e(TAG, "Failed to get current Activity name!");
            return false;
        }
        long time = System.currentTimeMillis();
        if (time < TIME_STAMP_THRESHOLD) {
            Log.e(TAG, "Error failing to get current time [" + time + "]");
            return false;
        }
        StatisticsRecord record = new StatisticsRecord(pkgname, pagename,
                StatisticsRecord.STATISTICS_TYPE_CUSTOM_EVENT, time, category, key, value);
        return SciflyStore.StatisticsRecords.putStatisticsRecord(mContext.getContentResolver(), record);
    }

    /**
     * Record custom event with no more than one parameter. If this event
     * contains more than one parameters, you should call recordEvent(Activity
     * activity, String category, Map<String, String> map).
     * 
     * @param pagename Name of current page.
     * @param category Category of this event.
     * @param key Key of parameter.
     * @param value Value of parameter.
     * @return true if record successfully, otherwise false.
     */
    public boolean recordEvent(String pagename, String category, String key, String value) {
        if (mContext == null) {
            Log.e(TAG, "mContext not Initialize...");
            return false;
        }
        String pkgname = mContext.getPackageName();
        if (TextUtils.isEmpty(pkgname)) {
            Log.e(TAG, "pkgname cannot be empty!");
            return false;
        }
        if (TextUtils.isEmpty(pagename)) {
            Log.e(TAG, "Failed to get current Activity name!");
            return false;
        }
        long time = System.currentTimeMillis();
        if (time < TIME_STAMP_THRESHOLD) {
            Log.e(TAG, "Error failing to get current time [" + time + "]");
            return false;
        }
        StatisticsRecord record = new StatisticsRecord(pkgname, pagename,
                StatisticsRecord.STATISTICS_TYPE_CUSTOM_EVENT, time, category, key, value);
        return SciflyStore.StatisticsRecords.putStatisticsRecord(mContext.getContentResolver(), record);
    }

    /**
     * Record custom event with more than one parameter. Otherwise you should
     * call recordEvent(Activity activity, String category, String key, String
     * value). Note that size of Map must be no more than 10.
     * 
     * @param activity {@link android.app.Activity}.
     * @param category Category of this event.
     * @param map {@link java.util.Map} contains all the parameters.
     * @return true if record successfully, otherwise false.
     */
    public boolean recordEvent(Activity activity, String category, Map<String, String> map) {
        if (mContext == null) {
            Log.e(TAG, "mContext not Initialize...");
            return false;
        }
        String pkgname = mContext.getPackageName();
        if (TextUtils.isEmpty(pkgname)) {
            Log.e(TAG, "pkgname cannot be empty!");
            return false;
        }
        String pagename = activity.getLocalClassName();
        if (TextUtils.isEmpty(pagename)) {
            Log.e(TAG, "Failed to get current Activity name!");
            return false;
        }
        long time = System.currentTimeMillis();
        if (time < TIME_STAMP_THRESHOLD) {
            Log.e(TAG, "Error failing to get current time [" + time + "]");
            return false;
        }
        if (map.size() > 10) {
            Log.e(TAG, "Error size of param map is " + map.size());
            return false;
        }
        String params = new JSONObject(map).toString();
        StatisticsRecord record = new StatisticsRecord(pkgname, pagename,
                StatisticsRecord.STATISTICS_TYPE_CUSTOM_EVENT, time, category, params);
        return SciflyStore.StatisticsRecords.putStatisticsRecord(mContext.getContentResolver(), record);
    }

    private class StatisticsRecordPiece {
        private long mStartTime;

        private long mEndTime;

        private String mData;

        public StatisticsRecordPiece(long startTime, long endTime, String data) {
            this.mStartTime = startTime;
            this.mEndTime = endTime;
            this.mData = data;
        }

        public long getStartTime() {
            return mStartTime;
        }

        public long getEndTime() {
            return mEndTime;
        }

        public String getData() {
            return mData;
        }
    }
}
