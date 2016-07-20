
package scifly.app.common;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import java.security.MessageDigest;
import java.util.List;
import java.util.Map;

import org.json.JSONObject;

import scifly.provider.SciflyStore;

import android.R.integer;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

/**
 * Common methods & vars.
 * 
 * @author frankzhang
 */
public final class Commons {

    /**
     * Do not instantiation.
     */
    private Commons() {
    }

    /**
     * Global debug switch.
     */
    public static final boolean DEBUG = true;

    /**
     * Global tag string.
     */
    public static final String TAG = "AppSecurity";

    /**
     * Base of all common message.
     */
    private static final int MSG_COMMON_BASE = 1000;

    // for BadAppAlertDialog's external Handler use
    /**
     * Constant for BadAppAlertDialog's OK button click.
     */
    public static final int MSG_DLG_BTN_OK = MSG_COMMON_BASE + 1;

    /**
     * Constant for BadAppAlertDialog's cancel button click.
     */
    public static final int MSG_DLG_BTN_CANCEL = MSG_COMMON_BASE + 2;

    // for AppInfoChecker external handler use
    /**
     * Constant for requesting security info from server timeout.
     */
    public static final int MSG_REMINDER_SHOW = MSG_COMMON_BASE + 3;

    /**
     * Constant for requesting security info from server timeout.
     */
    public static final int MSG_REMINDER_DISMISS = MSG_COMMON_BASE + 4;

    // for AppBlackList database use
    /**
     * The database URI of blacklist's.
     */
    public static final Uri SECURITY_TABLE_URI = Uri.parse("content://com.eostek.scifly.provider/security");

    /**
     * Constant for column name of package in database.
     */
    public static final String COLUMN_PKG = "package";

    /**
     * Constant for column name of description in database.
     */
    public static final String COLUMN_DESC = "message";

    /**
     * Constant for column name of random factor in database.
     */
    public static final String COLUMN_FACTOR = "level";

    /**
     * Constant for column name of extra in database.
     */
    public static final String COLUMN_EXTRA = "extra";

    /**
     * For request param use.
     * 
     * @author frankzhang
     */
    public static class DeviceInfo {
        /**
         * Request header field of ifid.
         */
        public String ifid;

        /**
         * Request header field of mac.
         */
        public String mac;

        /**
         * Request header field of devName.
         */
        public String devName;

        /**
         * Request header field of devCode.
         */
        public String devCode;

        /**
         * Request header field of bbno.
         */
        public String bbno;

        @Override
        public String toString() {
            // do not change the order , this match with the server decoding
            // order.
            return ifid + mac + devName + devCode + bbno;
        }
    }

    /**
     * For global debug use.
     * 
     * @author frankzhang
     */
    public static class CommonLog {
        private String mClassName;

        /**
         * @param className the class name of who want to debug.
         */
        public CommonLog(String className) {
            mClassName = className;
        }

        /**
         * @param msg debug message.
         */
        public void d(String msg) {
            if (DEBUG) {
                Log.d(TAG, mClassName + "\t" + msg);
            }
        }

        /**
         * @param msg debug message.
         */
        public void i(String msg) {
            Log.i(TAG, mClassName + "\t" + msg);
        }

        /**
         * @param msg debug message.
         */
        public void e(String msg) {
            Log.e(TAG, mClassName + "\t" + msg);
        }

    }

    // -------------------------------------------------------------
    // talk with the server
    /**
     * The default url of remote server.
     */
    public static final String DEFAULT_REQUEST_URL = "http://tvosapp.babao.com/interface/clientService.jsp";

    /**
     * Timeout of request blacklist from server.
     */
    public static final int TIMEOUT_APP_BLACK_LIST = 20 * 1000;

    /**
     * Timeout of request security info from server.
     */
    public static final int TIMEOUT_APP_SECURE_LIST = 10 * 1000;

    /**
     * Digest extra message for tansmition validate check.
     */
    public static final String DIGEST_EXTRA = "EOSTEK027";

    // request header key define
    /**
     * Key words for parse response json's ifid.
     */
    public static final String KEY_IFID = "ifid";

    /**
     * Key words for parse response json's mac.
     */
    public static final String KEY_MAC = "mac";

    /**
     * Key words for parse response json's devName.
     */
    public static final String KEY_DEVNAME = "devName";

    /**
     * Key words for parse response json's devCode.
     */
    public static final String KEY_DEVCODE = "devCode";

    /**
     * Key words for parse response json's bbno.
     */
    public static final String KEY_BBNO = "bbno";

    // PKG for common use
    /**
     * Key words for parse response json's pkg.
     */
    public static final String KEY_PKG = "pkg";

    /**
     * Key words for parse response json's time.
     */
    public static final String KEY_STAMP = "time";

    /**
     * Key words for parse response json's sn.
     */
    public static final String KEY_SERIAL = "sn";

    // parse response json key define
    // black list related
    /**
     * Key words for parse response json's err.
     */
    public static final String KEY_ERROR = "err";

    /**
     * Key words for parse response json's bd.
     */
    public static final String KEY_BODY = "bd";

    /**
     * Key words for parse response json's cnt.
     */
    public static final String KEY_COUNT = "cnt";

    /**
     * Key words for parse response json's ds.
     */
    public static final String KEY_DS = "ds";

    /**
     * Key words for parse response json's blacklist.
     */
    public static final String KEY_BLACKLIST = "blacklist";

    /**
     * Key words for parse response json's factor.
     */
    public static final String KEY_FACTOR = "factor";

    /**
     * Key words for parse response json's desc.
     */
    public static final String KEY_DESC = "desc";

    // secure list related
    /**
     * Key words for parse response json's result.
     */
    public static final String KEY_RESULT = "result";

    /**
     * Key words for parse response json's info.
     */
    public static final String KEY_INFO = "info";

    private static final int HTTP_READ_TIMEOUT = 5000;
    
    public static final int CONNECT_TYPE_BLACK_LIST = 1;
    
    public static final int CONNECT_TYPE_SECURE_LIST = 2;
    
    public static final int HTTP_STATUS_NOT_MODIFIED = 304;

    public static enum AppEtagEnum {
        AppBlackListEnum(CONNECT_TYPE_BLACK_LIST, SciflyStore.Global.ETAG_APP_BLACK_LIST, TIMEOUT_APP_BLACK_LIST), 
        AppSecureListEnum(CONNECT_TYPE_SECURE_LIST, SciflyStore.Global.ETAG_APP_SECURE_LIST, TIMEOUT_APP_SECURE_LIST);

        public int type;

        public String key;

        public int timeout;

        AppEtagEnum(int type, String key, int timeout) {
            this.type = type;
            this.key = key;
            this.timeout = timeout;
        }
    }

    /**
     * @param urlString the server's url.
     * @param requestJson request json.
     * @param connectTimeout connect time out.
     * @return string of response json.
     */
    public static String getServerResponse(Context context, JSONObject requestJson, int connType) {
        if (null == requestJson) {
            return null;
        }
        AppEtagEnum etagEnum = null;
        switch (connType) {
            case CONNECT_TYPE_BLACK_LIST:
                etagEnum = AppEtagEnum.AppBlackListEnum;
                break;

            case CONNECT_TYPE_SECURE_LIST:
                etagEnum = AppEtagEnum.AppSecureListEnum;
                break;

            default:
                break;
        }
        if (null == etagEnum) {
            Log.e(TAG, "invalid connection type .");
            return null;
        }
        String targetUrl = android.os.SystemProperties.get("ro.scifly.service.url", DEFAULT_REQUEST_URL);
        Log.i(TAG, "url:" + targetUrl + "\trequestJson:" + requestJson.toString() + "\ttimeout:" + etagEnum.timeout);
        if (TextUtils.isEmpty(targetUrl)) {
            return null;
        }
        StringBuffer buffer = new StringBuffer();
        HttpURLConnection httpUrlConnection = null;
        Writer writer = null;
        InputStream inputStream = null;
        BufferedReader bufferedReader = null;
        try {
            URL url = new URL(targetUrl);
            httpUrlConnection = (HttpURLConnection) url.openConnection();
            httpUrlConnection.setRequestProperty("Content-Type", "text/json; charset=UTF-8");
            String etag = null;
            if (CONNECT_TYPE_BLACK_LIST == connType) {
                etag = getEtag(context, etagEnum.key);
                if (!TextUtils.isEmpty(etag)) {
                    httpUrlConnection.setRequestProperty("If-None-Match", etag);
                }
            }

            httpUrlConnection.setRequestMethod("POST");
            httpUrlConnection.setDoOutput(true);
            httpUrlConnection.setConnectTimeout(etagEnum.timeout);
            httpUrlConnection.setReadTimeout(HTTP_READ_TIMEOUT);

            writer = new OutputStreamWriter(httpUrlConnection.getOutputStream(), "utf-8");
            writer.write(requestJson.toString());
            writer.flush();
            httpUrlConnection.connect();
            if (CONNECT_TYPE_BLACK_LIST == connType) {
                Map<String, List<String>> headerMap = httpUrlConnection.getHeaderFields();
                for(Map.Entry<String, List<String>> entry: headerMap.entrySet()){
                    Log.d(TAG, "key:" + entry.getKey() + ",\t values:" + entry.getValue());
                }
                etag = httpUrlConnection.getHeaderField("Etag");
                Log.d(TAG, "Etag:" + etag);
            }

            int responseCode = httpUrlConnection.getResponseCode();
            Log.d(TAG, "JSONData: response code: " + responseCode);
            if (HTTP_STATUS_NOT_MODIFIED != responseCode) {
                if (CONNECT_TYPE_BLACK_LIST == connType && !TextUtils.isEmpty(etag)) {
                    putEtag(context, etagEnum.key, etag);
                }
                inputStream = httpUrlConnection.getInputStream();
                bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"));

                String line = "";
                while ((line = bufferedReader.readLine()) != null) {
                    buffer.append(line);
                }
            }
        } catch (Exception e) {
            Log.e(TAG, "exception:" + e.getMessage());
        } finally {
            if (httpUrlConnection != null) {
                httpUrlConnection.disconnect();
            }
            try {
                if (writer != null) {
                    writer.close();
                }
                if (inputStream != null) {
                    inputStream.close();
                }
                if (bufferedReader != null) {
                    bufferedReader.close();
                }
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
            }
        }
        return buffer.toString();
    }

    // -------------------------------------
    // network related
    /**
     * @param context Context.
     * @return true if device connected networks, else false.
     */
    public static boolean isNetworkConnected(Context context) {
        ConnectivityManager connManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (null == connManager.getActiveNetworkInfo()) {
            return false;
        }
        return connManager.getActiveNetworkInfo().isConnected();
    }

    // --------------------------------------
    // Message Digest
    /**
     * @param message wanted to digest.
     * @return MD5 of the message's.
     */
    public static String caclMd5(String message) {
        if (TextUtils.isEmpty(message)) {
            return null;
        }
        Log.d(TAG, "msg:" + message);
        MessageDigest digest = null;
        try {
            digest = MessageDigest.getInstance("MD5");
            digest.update(message.getBytes());
        } catch (Exception e) {
            Log.e(TAG, e.getMessage());
            return null;
        }

        byte[] md5 = digest.digest();
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < md5.length; i++) {
            int v = md5[i] & 0xFF;
            if (v < 16) {
                sb.append("0");
            }

            sb.append(Integer.toString(v, 16));
        }
        return sb.toString();
    }

    // ------------------------------------------------
    // Etag related
    private static boolean putEtag(Context ctx, String key, String value) {

        return SciflyStore.Global.putString(ctx.getContentResolver(), key, value);
    }

    private static String getEtag(Context ctx, String key) {

        return SciflyStore.Global.getString(ctx.getContentResolver(), key);
    }
}
