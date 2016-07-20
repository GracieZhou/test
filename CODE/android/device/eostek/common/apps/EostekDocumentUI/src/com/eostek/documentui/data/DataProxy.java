
package com.eostek.documentui.data;

import java.net.URI;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import android.app.Activity;
import android.app.DownloadManager;
import android.content.ContentResolver;
import android.content.ContentUris;
import android.content.ContentValues;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.webkit.URLUtil;

import com.eostek.documentui.Constants;

/**
 * @ClassName: DataProxy.
 * @Description:Data table process .
 * @author: albert.luo.
 * @date: Sep 18, 2015 9:38:24 AM.
 * @Copyright: Eostek Co., Ltd. Copyright , All rights reserved.
 */
public class DataProxy {

    private static final String TAG = "DataProxy";

    public static final Uri BASE_URL = Uri.parse("content://downloads/my_downloads");

    private static final Uri SETTING_URL = Uri.parse("content://com.android.providers.downloads.setting/setting");

    private static final String COLUMN_SAVE_DIR = "save_dir_path";

    private static final String COLUMN_TASK_NUMBER = "task_number";

    private static final String COLUMN_SPEED_LIMITE = "speed_limite";

    private static final String IS_DATA_CACHE_DIR = "is_data_cache";

    /**
     * An identifier for a particular download, unique across the system.
     * Clients use this ID to make subsequent calls related to the download.
     */
    private final static String COLUMN_ID = DownloadManager.COLUMN_ID;

    /**
     * The name of the column containing the flags that controls whether the
     * download is displayed by the UI. See the VISIBILITY_* constants for a
     * list of legal values. Type: INTEGER Owner can Init/Read/Write
     */
    private static final String COLUMN_VISIBILITY = "visibility";

    /**
     * The name of the column containing the MIME type of the downloaded data.
     * Type: TEXT Owner can Init/Read
     */
    private static final String COLUMN_MIME_TYPE = "mimetype";

    /**
     * The name of the column containing the filename that the initiating
     * application recommends. When possible, the download manager will attempt
     * to use this filename, or a variation, as the actual name for the file.
     * Type: TEXT Owner can Init
     */
    private static final String COLUMN_FILE_NAME = "title";

    /**
     * The name of the column containing the flag that controls the destination
     * of the download. See the DESTINATION_* constants for a list of legal
     * values. Type: INTEGER Owner can Init
     */
    private static final String COLUMN_DESTINATION = "destination";

    /**
     * Set to true if this download is deleted. It is completely removed from
     * the database when MediaProvider database also deletes the metadata
     * asociated with this downloaded file. Type: BOOLEAN Owner can Read
     */
    private static final String COLUMN_DELETED = "deleted";

    /**
     * The name of the column where the initiating application can provide the
     * description of this download. The description will be displayed to the
     * user in the list of downloads. Type: TEXT Owner can Init/Read/Write
     */
    private static final String COLUMN_DESCRIPTION = "description";

    /**
     * The name of the column containing the total size of the file being
     * downloaded. Type: INTEGER Owner can Read
     */
    private static final String COLUMN_TOTAL_BYTES = "total_bytes";

    /**
     * Total size of the download in bytes. This will initially be -1 and will
     * be filled in once the download starts.
     */
    @SuppressWarnings("unused")
    private final static String COLUMN_TOTAL_SIZE_BYTES = "total_size";

    /**
     * The name of the column containing the component name of the class that
     * will receive notifications associated with the download. The
     * package/class combination is passed to
     * Intent.setClassName(String,String). Type: TEXT Owner can Init/Read
     */
    private static final String COLUMN_NOTIFICATION_CLASS = "notificationclass";

    /**
     * The name of the column containing the package name of the application
     * that initiating the download. The download manager will send
     * notifications to a component in this package when the download completes.
     * Type: TEXT Owner can Init/Read
     */
    private static final String COLUMN_NOTIFICATION_PACKAGE = "notificationpackage";

    /**
     * The name of the column containing the user agent that the initiating
     * application wants the download manager to use for this download. Type:
     * TEXT Owner can Init
     */
    private static final String COLUMN_USER_AGENT = "useragent";

    /**
     * The column that is used to remember whether the media scanner was
     * invoked. It can take the values: null or 0(not scanned), 1(scanned), 2
     * (not scannable). Type: TEXT
     */
    private static final String COLUMN_MEDIA_SCANNED = "scanned";

    /**
     * The name of the column containing the URI of the data being downloaded.
     * Type: TEXT Owner can Init/Read
     */
    private static final String COLUMN_URI = "uri";

    private static final String COLUMN_SPEED = "speed";

    public static final String COLUMN_CREATE_TIME = "create_time";

    public static final String _ID = "_id";

    /**
     * The name of the column where the initiating application can provided the
     * title of this download. The title will be displayed ito the user in the
     * list of downloads. Type: TEXT Owner can Init/Read/Write
     */
    private static final String COLUMN_TITLE = "title";

    /**
     * The name of the column containing the current status of the download.
     * Applications can read this to follow the progress of each download. See
     * the STATUS_* constants for a list of legal values. Type: INTEGER Owner
     * can Read
     */
    private static final String COLUMN_STATUS = "status";

    /**
     * The name of the column containing the size of the part of the file that
     * has been downloaded so far. Type: INTEGER Owner can Read
     */
    private static final String COLUMN_CURRENT_BYTES = "current_bytes";

    private static final String COLUMN_ALLOW_WRITE = "allow_write";

    /**
     * The name of the column containing the filename where the downloaded data
     * was actually stored.
     */
    private static final String COLUMN_DATA = "_data";

    /**
     * The name of the column containing the current control state of the
     * download. Applications can write to this to control (pause/resume) the
     * download. the CONTROL_* constants for a list of legal values.
     */
    private static final String COLUMN_CONTROL = "control";

    /**
     * This download is allowed to run.
     */
    public static final int CONTROL_RUN = 0;

    /**
     * This download must pause at the first opportunity.
     */
    public static final int CONTROL_PAUSED = 1;

    public static Map<String, Mimetype> MAP_MIMETYPE = new HashMap<String, Mimetype>();

    private Activity mActivity;

    public DataProxy(Activity act) {
        super();
        mActivity = act;
        initMimetype();
    }

    /**
     * @Title: insert.
     * @Description: add a downloaded task.
     * @param: @param dl.
     * @return: void.
     * @throws
     */
    public void insert(DownloadInfor dl) {
        String filename = dl.saveName;
        URI uri = null;

        try {
            // Undo the percent-encoding that KURL may have done.
            String newUrl = new String(URLUtil.decode(dl.fullURL.getBytes()));
            // Parse the url into pieces

            WebAddress w = new WebAddress(newUrl);

            String frag = null;
            String query = null;

            String path = w.getPath();
            // Break the path into path, query, and fragment

            if (path.length() > 0) {
                // Strip the fragment
                int idx = path.lastIndexOf('#');
                if (idx != -1) {
                    frag = path.substring(idx + 1);
                    path = path.substring(0, idx);
                }

                idx = path.lastIndexOf('?');
                if (idx != -1) {
                    query = path.substring(idx + 1);
                    path = path.substring(0, idx);
                }
            }

            uri = new URI(w.getScheme(), w.getAuthInfo(), w.getHost(), w.getPort(), path, query, frag);

        } catch (Exception e) {

            Log.e(TAG, "Could not parse url for download: " + uri.toString(), e);

            return;

        }

        if (uri.getHost() != null)
            Log.d(TAG, "host: " + uri.getHost());
        else
            Log.d(TAG, "uri.getHost() is null ");

        ContentValues values = new ContentValues();

        values.put(
                COLUMN_USER_AGENT,
                "Mozilla/5.0 (linux; U; Android 4.03; en-us; SDK Build/CUPCAKE) AppleWebKit/528.5+ (KHTML, like Gecko) Version/3.1.2 Mobile Safari/525.20.1");

        values.put(COLUMN_URI, uri.toString());
        values.put(COLUMN_NOTIFICATION_PACKAGE, mActivity.getPackageName());
        values.put(COLUMN_NOTIFICATION_CLASS, "HelloWorld");
        values.put(COLUMN_VISIBILITY, 1);
        values.put(COLUMN_MIME_TYPE, dl.mimetype);
        values.put(COLUMN_FILE_NAME, filename);
        values.put(COLUMN_DESTINATION, 1);
        values.put(COLUMN_TOTAL_BYTES, dl.totalBytes);
        values.put(COLUMN_DESCRIPTION, "test");
        values.put(COLUMN_STATUS, dl.downloadState);

        // COLUMN_CURRENT_BYTES
        values.put(COLUMN_CURRENT_BYTES, 0);
        // COLUMN_MEDIA_SCANNED
        values.put(COLUMN_MEDIA_SCANNED, 1);
        values.put(COLUMN_ALLOW_WRITE, 1);
        values.put(COLUMN_TITLE, "");

        ContentResolver mResolver = mActivity.getContentResolver();

        Uri mRet = mResolver.insert(BASE_URL, values);
        Log.d(TAG, "mResolver.insert return " + mRet.toString());
    }

    /**
     * @Title: update.
     * @Description: update the status of the task (running to pause,pause to
     *               running).
     * @param: @param dl
     * @param: @return.
     * @return: boolean.
     * @throws
     */
    public boolean update(DownloadInfor dl) {
        ContentResolver resolver = mActivity.getContentResolver();
        ContentValues values = new ContentValues();

        values.put(COLUMN_CONTROL, dl.controlRun);
        values.put(COLUMN_STATUS, dl.downloadState);
        Uri temp = ContentUris.withAppendedId(BASE_URL, dl.id);
        int nRet = resolver.update(temp, values, null, null);

        if (nRet >= 1)
            return true;
        else
            return false;
    }

    /**
     * @Title: getAllDownload.
     * @Description: gain the specified datas of downloaded tasks .
     * @param: @param
     *         DownloadState(STATUS_PENDING,STATUS_RUNNING,STATUS_PAUSED_BY_APP
     *         ,STATUS_SUCCESS ),if DownloadState<0 , gain all datas of
     *         downloaded tasks
     * @param: @return.
     * @return: ArrayList<DownloadInfor>.
     * @throws
     */
    public ArrayList<DownloadInfor> getAllDownload(int DownloadState) {
        ArrayList<DownloadInfor> list = new ArrayList<DownloadInfor>();
        ContentResolver resolver = mActivity.getContentResolver();
        Cursor underlyingCursor = runQuery(resolver, null, BASE_URL, DownloadState);
        if (underlyingCursor == null || underlyingCursor.getCount() < 1)
            return list;

        // String[] cols = underlyingCursor.getColumnNames();
        if (underlyingCursor.moveToFirst()) {
            do {
                DownloadInfor dl = new DownloadInfor();
                int index = underlyingCursor.getColumnIndex(COLUMN_ID);
                if (index >= 0)
                    dl.id = underlyingCursor.getLong(index);

                index = underlyingCursor.getColumnIndex(COLUMN_TOTAL_BYTES);
                if (index >= 0)
                    dl.totalBytes = underlyingCursor.getInt(index);

                index = underlyingCursor.getColumnIndex(COLUMN_SPEED);
                if (index >= 0) {
                    dl.speed = underlyingCursor.getInt(index);
                }
                index = underlyingCursor.getColumnIndex(COLUMN_CREATE_TIME);
                if (index >= 0) {
                    dl.createTime = underlyingCursor.getLong(index);
                }
                index = underlyingCursor.getColumnIndex(COLUMN_FILE_NAME);
                if (index >= 0)
                    dl.saveName = underlyingCursor.getString(index);

                index = underlyingCursor.getColumnIndex(COLUMN_MIME_TYPE);
                if (index >= 0)
                    dl.mimetype = underlyingCursor.getString(index);

                index = underlyingCursor.getColumnIndex(COLUMN_URI);
                if (index >= 0)
                    dl.fullURL = underlyingCursor.getString(index);

                index = underlyingCursor.getColumnIndex(COLUMN_STATUS);
                if (index >= 0)
                    dl.downloadState = underlyingCursor.getInt(index);

                index = underlyingCursor.getColumnIndex(COLUMN_TOTAL_BYTES);
                if (index >= 0)
                    dl.totalBytes = underlyingCursor.getInt(index);

                index = underlyingCursor.getColumnIndex(COLUMN_CURRENT_BYTES);
                if (index >= 0)
                    dl.currentBytes = underlyingCursor.getInt(index);

                index = underlyingCursor.getColumnIndex(COLUMN_DATA);
                if (index >= 0)
                    dl.fullSavePath = underlyingCursor.getString(index);

                index = underlyingCursor.getColumnIndex(COLUMN_CONTROL);
                if (index >= 0)
                    dl.controlRun = underlyingCursor.getInt(index);

                list.add(dl);

            } while (underlyingCursor.moveToNext());
        }

        underlyingCursor.close();

        return list;
    }

    /**
     * @Title: deleteDownload.
     * @Description: delete the downloaded task.
     * @param: @param dl
     * @param: @return.
     * @return: boolean.
     * @throws
     */
    public boolean deleteDownload(DownloadInfor dl) {
        ContentResolver resolver = mActivity.getContentResolver();
        ContentValues values = new ContentValues();

        values.put(COLUMN_DELETED, 1);
        Uri temp = ContentUris.withAppendedId(BASE_URL, dl.id);
        int nRet = resolver.update(temp, values, null, null);

        if (nRet >= 1)
            return true;
        else
            return false;
    }

    /**
     * @Title: updateSetting.
     * @Description: update the setting params.
     * @param: @param st
     * @param: @return.
     * @return: boolean.
     * @throws
     */
    public boolean updateSetting(SettingInfor st) {
        ContentResolver resolver = mActivity.getContentResolver();
        ContentValues values = new ContentValues();

        values.put(COLUMN_SAVE_DIR, st.saveDir);
        values.put(COLUMN_SPEED_LIMITE, st.downloadSpeedLimite);
        values.put(COLUMN_TASK_NUMBER, st.downloadNumber);
        int temp = 0;
        if (st.bDataCache)
            temp = 1;
        values.put(IS_DATA_CACHE_DIR, temp);
        Uri uri = ContentUris.withAppendedId(SETTING_URL, 1);
        Log.d(TAG, "! " + uri.toString());
        int nRet = resolver.update(uri, values, null, null);

        if (nRet >= 1)
            return true;
        else
            return false;
    }

    /**
     * @Title: getSetting.
     * @Description: query setting params.
     * @param: @return.
     * @return: SettingInfor.
     * @throws
     */
    public SettingInfor getSetting() {
        SettingInfor infor = new SettingInfor();
        ContentResolver resolver = mActivity.getContentResolver();
        Cursor underlyingCursor = resolver.query(SETTING_URL, null, null, null, null);
        if (underlyingCursor == null || underlyingCursor.getCount() < 1) {
            insertSetting();
        } else {
            Log.d(TAG, "getSetting have data! ");
            if (underlyingCursor.moveToFirst()) {
                do {
                    int index = underlyingCursor.getColumnIndex(COLUMN_SAVE_DIR);
                    if (index >= 0)
                        infor.saveDir = underlyingCursor.getString(index);

                    index = underlyingCursor.getColumnIndex(COLUMN_SPEED_LIMITE);
                    if (index >= 0)
                        infor.downloadSpeedLimite = underlyingCursor.getInt(index);

                    index = underlyingCursor.getColumnIndex(COLUMN_TASK_NUMBER);
                    if (index >= 0)
                        infor.downloadNumber = underlyingCursor.getInt(index);

                    index = underlyingCursor.getColumnIndex(IS_DATA_CACHE_DIR);
                    if (index >= 0) {
                        int value = underlyingCursor.getInt(index);
                        if (1 == value) {
                            infor.bDataCache = true;
                        } else {
                            infor.bDataCache = false;
                        }
                    }
                } while (underlyingCursor.moveToNext());
            }
        }
        underlyingCursor.close();
        return infor;
    }

    /**
     * Value of {@link #COLUMN_STATUS} when the download has successfully
     * completed.
     */
    public static final int STATUS_SUCCESS = 200;

    /**
     * Value of {@link #COLUMN_STATUS} when the download is waiting to start.
     * This download hasn't stated yet
     */
    public static final int STATUS_PENDING = 190;

    /**
     * Value of {@link #COLUMN_STATUS} when the download is currently running.
     * This download has started
     */
    public static final int STATUS_RUNNING = 192;

    /**
     * Value of {@link #COLUMN_STATUS} when the download is waiting to retry or
     * resume. This download has been paused by the owning app.
     */
    public static final int STATUS_PAUSED_BY_APP = 193;

    /**
     * This download encountered some network error and is waiting before
     * retrying the request.
     */
    public static final int STATUS_WAITING_TO_RETRY = 194;

    /**
     * This download is waiting for network connectivity to proceed.
     */
    public static final int STATUS_WAITING_FOR_NETWORK = 195;

    /**
     * This download exceeded a size limit for mobile networks and is waiting
     * for a Wi-Fi connection to proceed.
     */
    public static final int STATUS_QUEUED_FOR_WIFI = 196;

    /**
     * Value of {@link #COLUMN_STATUS} when the download has failed (and will
     * not be retried).
     */
    public final static int STATUS_FAILED = 400;

    private Cursor runQuery(ContentResolver resolver, String[] projection, Uri baseUri, int statusFlags) {
        Uri uri = baseUri;
        ArrayList<String> selectionParts = new ArrayList<String>();
        String[] selectionArgs = null;

        if (statusFlags >= 0) {
            ArrayList<String> parts = new ArrayList<String>();

            if (STATUS_PENDING == statusFlags) {
                parts.add(statusClause("=", STATUS_PENDING));
            }

            if (STATUS_RUNNING == statusFlags) {
                parts.add(statusClause("=", STATUS_RUNNING));
            }

            if (STATUS_SUCCESS == statusFlags) {
                parts.add(statusClause("=", STATUS_SUCCESS));
            }

            if (STATUS_FAILED == statusFlags) {
                parts.add("(" + statusClause(">=", 400) + " AND " + statusClause("<", 600) + ")");
            }

            if (STATUS_PAUSED_BY_APP == statusFlags) {
                parts.add(statusClause("=", STATUS_PAUSED_BY_APP));
                parts.add(statusClause("=", STATUS_WAITING_TO_RETRY));
                parts.add(statusClause("=", STATUS_WAITING_FOR_NETWORK));
                parts.add(statusClause("=", STATUS_QUEUED_FOR_WIFI));
            }

            selectionParts.add(joinStrings(" OR ", parts));
        }

        // only return rows which are not marked 'deleted = 1'
        selectionParts.add(COLUMN_DELETED + " != '1'");

        String selection = joinStrings(" AND ", selectionParts);

        return resolver.query(uri, projection, selection, selectionArgs, null);
    }

    private String statusClause(String operator, int value) {
        return COLUMN_STATUS + operator + "'" + value + "'";
    }

    private String joinStrings(String joiner, Iterable<String> parts) {
        StringBuilder builder = new StringBuilder();
        boolean first = true;
        for (String part : parts) {
            if (!first) {
                builder.append(joiner);
            }
            builder.append(part);
            first = false;
        }
        return builder.toString();
    }

    public void insertSetting() {
        ContentValues values = new ContentValues();
        values.put(_ID, 1);
        values.put(COLUMN_SAVE_DIR, Constants.DOWNLOAD_INNER_LOCATION);
        values.put(COLUMN_SPEED_LIMITE, 100000);
        values.put(COLUMN_TASK_NUMBER, 3);
        values.put(IS_DATA_CACHE_DIR, 1);
        ContentResolver mResolver = mActivity.getContentResolver();
        mResolver.insert(SETTING_URL, values);
    }

    public class Mimetype {
        public static final String TYPE_DOCUMENT = "document";

        public static final String TYPE_INSTALLATION_PACKAGE = "Installation_package";

        public static final String TYPE_IMAGE = "image";

        public static final String TYPE_VIDEO = "video";

        public static final String TYPE_AUDIO = "audio";

        public String customType = TYPE_DOCUMENT;

        public String mineType = "text/plain";

        public Mimetype() {
            super();
        }

        public Mimetype(String type, String mineType) {
            super();
            this.customType = type;
            this.mineType = mineType;
        }
    }

    private void initMimetype() {
        MAP_MIMETYPE.put(".txt", new Mimetype(Mimetype.TYPE_DOCUMENT, "text/plain"));
        MAP_MIMETYPE.put(".xml", new Mimetype(Mimetype.TYPE_DOCUMENT, "text/xml"));
        MAP_MIMETYPE.put(".html", new Mimetype(Mimetype.TYPE_DOCUMENT, "text/html"));
        MAP_MIMETYPE.put(".css", new Mimetype(Mimetype.TYPE_DOCUMENT, "text/css"));
        MAP_MIMETYPE.put(".jar", new Mimetype(Mimetype.TYPE_DOCUMENT, "application/java-archive"));
        MAP_MIMETYPE.put(".pdf", new Mimetype(Mimetype.TYPE_DOCUMENT, "application/pdf"));
        MAP_MIMETYPE.put(".xls", new Mimetype(Mimetype.TYPE_DOCUMENT, "application/vnd.ms-excel"));
        MAP_MIMETYPE.put(".doc", new Mimetype(Mimetype.TYPE_DOCUMENT, "application/msword"));
        MAP_MIMETYPE.put(".7z", new Mimetype(Mimetype.TYPE_DOCUMENT, "application/x-7z-compressed"));
        // MAP_MIMETYPE.put(".rar", new Mimetype(Mimetype.TYPE_DOCUMENT,
        // "application/x-rar-compressed"));
        // MAP_MIMETYPE.put(".zip", new Mimetype(Mimetype.TYPE_DOCUMENT,
        // "application/zip"));

        MAP_MIMETYPE.put(".gif", new Mimetype(Mimetype.TYPE_IMAGE, "image/gif"));
        MAP_MIMETYPE.put(".jpeg", new Mimetype(Mimetype.TYPE_IMAGE, "image/jpeg"));
        MAP_MIMETYPE.put(".jpg", new Mimetype(Mimetype.TYPE_IMAGE, "image/jpeg"));
        MAP_MIMETYPE.put(".jpe", new Mimetype(Mimetype.TYPE_IMAGE, "image/jpeg"));
        MAP_MIMETYPE.put(".png", new Mimetype(Mimetype.TYPE_IMAGE, "image/png"));
        MAP_MIMETYPE.put(".tiff", new Mimetype(Mimetype.TYPE_IMAGE, "image/tiff"));
        MAP_MIMETYPE.put(".bmp", new Mimetype(Mimetype.TYPE_IMAGE, "image/bmp"));
        MAP_MIMETYPE.put(".icon", new Mimetype(Mimetype.TYPE_IMAGE, "image/x-icon"));
        MAP_MIMETYPE.put(".svg", new Mimetype(Mimetype.TYPE_IMAGE, "image/svg+xml"));

        MAP_MIMETYPE.put(".apk", new Mimetype(Mimetype.TYPE_INSTALLATION_PACKAGE,
                "application/vnd.android.package-archive"));

        MAP_MIMETYPE.put(".3gpp", new Mimetype(Mimetype.TYPE_VIDEO, "video/3gpp"));
        MAP_MIMETYPE.put(".mpeg", new Mimetype(Mimetype.TYPE_VIDEO, "video/mpeg"));
        MAP_MIMETYPE.put(".mov", new Mimetype(Mimetype.TYPE_VIDEO, "video/quicktime"));
        MAP_MIMETYPE.put(".flv", new Mimetype(Mimetype.TYPE_VIDEO, "video/x-flv"));
        MAP_MIMETYPE.put(".mp4", new Mimetype(Mimetype.TYPE_VIDEO, "video/mp4"));
        MAP_MIMETYPE.put(".asf", new Mimetype(Mimetype.TYPE_VIDEO, "video/x-ms-asf"));
        MAP_MIMETYPE.put(".wmv", new Mimetype(Mimetype.TYPE_VIDEO, "video/x-ms-wmv"));
        MAP_MIMETYPE.put(".avi", new Mimetype(Mimetype.TYPE_VIDEO, "video/x-msvideo"));
        MAP_MIMETYPE.put(".ts", new Mimetype(Mimetype.TYPE_VIDEO, "video/ts"));
        MAP_MIMETYPE.put(".m3u8", new Mimetype(Mimetype.TYPE_VIDEO, "video/*"));

        MAP_MIMETYPE.put(".midi", new Mimetype(Mimetype.TYPE_AUDIO, "audio/midi"));
        MAP_MIMETYPE.put(".mp3", new Mimetype(Mimetype.TYPE_AUDIO, "audio/mpeg"));
        MAP_MIMETYPE.put(".ra", new Mimetype(Mimetype.TYPE_AUDIO, "audio/x-realaudio"));
        MAP_MIMETYPE.put(".wav", new Mimetype(Mimetype.TYPE_AUDIO, "audio/x-wav"));
        MAP_MIMETYPE.put(".wma", new Mimetype(Mimetype.TYPE_AUDIO, "audio/x-ms-wma"));
        MAP_MIMETYPE.put(".wmv", new Mimetype(Mimetype.TYPE_AUDIO, "audio/x-ms-wmv"));
        MAP_MIMETYPE.put(".m3u", new Mimetype(Mimetype.TYPE_AUDIO, "audio/mpegurl"));
        MAP_MIMETYPE.put(".m4a", new Mimetype(Mimetype.TYPE_AUDIO, "audio/mpeg"));
    }
}
