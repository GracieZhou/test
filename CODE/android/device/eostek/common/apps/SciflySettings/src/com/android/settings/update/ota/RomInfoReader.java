
package com.android.settings.update.ota;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;
import org.json.JSONException;
import org.json.JSONObject;

import scifly.device.Device;

import android.content.Context;
import android.os.AsyncTask;
import android.os.Build;
import android.os.SystemProperties;
import android.text.TextUtils;

public class RomInfoReader extends AsyncTask<Integer, Void, Void> implements Constants {

    public static interface RomInfoCallback {
        public abstract void onNotModified();

        public abstract void onReadEnd(String buffer);

        public abstract void onReadError(Exception ex);
    };

    private static Logger sLog = new Logger(RomInfoReader.class);

    private RomInfoCallback mCallback;

    private Context mContext;

    private static PreferenceHelper sPreferenceHelper;

    public static final String KEY_IFID = "ifid";

    public static final String KEY_PLATFORM = "pla";

    public static final String KEY_LOW_VER = "lver";

    public static final String KEY_DEV_ID = "devId";

    public static final String KEY_MAC = "mac";

    public static final String KEY_TYPE = "type";

    private static final String T_TAG = Device.getTtag();

    private static final String BB_NO = Device.getBb();

    private static final String SERVER_URL = SystemProperties.get(DEFAULT_SERVER_PROP, DEFAULT_SERVER_URL);

    public RomInfoReader(Context context, RomInfoCallback callback) {
        mContext = context;
        mCallback = callback;
        sPreferenceHelper = PreferenceHelper.getInstance(context);
    }

    @Override
    protected Void doInBackground(Integer... params) {
        try {
            StringBuffer buffer = new StringBuffer();
            int responseCode = -1;
            if (OTA_TYPE_FULL == params[0]) {
                responseCode = readRomInfo(OTA_TYPE_FULL, buffer);
            } else {
                responseCode = readRomInfo(OTA_TYPE_INCREASE, buffer);
                if (TextUtils.isEmpty(buffer) && responseCode != HTTP_STATUS_NOT_MODIFIED) {
                    responseCode = readRomInfo(OTA_TYPE_FULL, buffer);
                }
            }

            if (mCallback != null) {

                final int status = responseCode;

                switch (status) {
                    case HTTP_STATUS_NOT_MODIFIED:
                        mCallback.onNotModified();
                        break;
                    case HTTP_STATUS_OK:
                        if (!TextUtils.isEmpty(buffer)) {
                            mCallback.onReadEnd(buffer.toString());
                        }
                        break;

                    default:
                        mCallback.onReadError(new Exception("No Version Found !"));
                        break;
                }
            }
            return null;
        } catch (Exception ex) {
            if (mCallback != null) {
                mCallback.onReadError(ex);
            }
        }
        return null;
    }

    private JSONObject makeRequestJsonByType(int type) {
        JSONObject reqestJson = new JSONObject();
        try {
            reqestJson.put(KEY_IFID, "TVOSVerUpdate");
            reqestJson.put(KEY_PLATFORM, Device.getDeviceCode());
            reqestJson.put(KEY_LOW_VER, Build.VERSION.INCREMENTAL.toString());
            reqestJson.put(KEY_DEV_ID, Device.getBb());
            reqestJson.put(KEY_MAC, Utils.getHexMac(mContext));
            reqestJson.put(KEY_TYPE, type);
        } catch (JSONException e) {
            return null;
        }

        return reqestJson;
    }

    private String getCurrentTcip(JSONObject json) {
        String tcip = BB_NO
                + "_"
                + MD5Tools.calcMD5(BB_NO + SERVER_URL + json + System.currentTimeMillis() + System.currentTimeMillis())
                + "_"
                + System.currentTimeMillis()
                + "_"
                + MD5Tools.calcMD5(BB_NO + SERVER_URL + json + Build.DISPLAY.toString().split(" ")[0]
                        + System.currentTimeMillis());
        return tcip;
    }

    public int readRomInfo(int type, StringBuffer buffer) throws Exception {

        JSONObject requestJson = makeRequestJsonByType(type);
        if (null == requestJson) {
            sLog.error("Request json is null.");
            return -1;
        }
        sLog.debug("request json: " + requestJson.toString());
        HttpURLConnection httpUrlConnection = null;
        Writer writer = null;
        InputStream inputStream = null;
        BufferedReader bufferedReader = null;

        int responseCode = -1;

        try {
            String etag = sPreferenceHelper.getEtag();
            URL url = new URL(SERVER_URL);
            sLog.debug("ServerUrl=" + SERVER_URL);
            httpUrlConnection = (HttpURLConnection) url.openConnection();
            httpUrlConnection.setRequestProperty("Content-Type", "text/json; charset=UTF-8");
            httpUrlConnection.setRequestProperty("Ttag", T_TAG);
            httpUrlConnection.setRequestProperty("Tcip", getCurrentTcip(requestJson));
            // if (!TextUtils.isEmpty(etag)) {
            // httpUrlConnection.setRequestProperty("If-None-Match", etag);
            // }
            httpUrlConnection.setRequestMethod("POST");
            httpUrlConnection.setDoOutput(true);
            httpUrlConnection.setConnectTimeout(20000);
            httpUrlConnection.setReadTimeout(5000);

            writer = new OutputStreamWriter(httpUrlConnection.getOutputStream(), "utf-8");
            writer.write(requestJson.toString());
            writer.flush();

            httpUrlConnection.connect();
            // etag = httpUrlConnection.getHeaderField("Etag");
            // if (!TextUtils.isEmpty(etag)) {
            // sPreferenceHelper.setEtag(etag);
            // }

            responseCode = httpUrlConnection.getResponseCode();
            sLog.debug("response code: " + responseCode);
            inputStream = httpUrlConnection.getInputStream();
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"));

            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                buffer.append(line);
            }
        } finally {
            if (httpUrlConnection != null) {
                httpUrlConnection.disconnect();
            }
            if (writer != null) {
                writer.close();
            }
            if (inputStream != null) {
                inputStream.close();
            }
            if (bufferedReader != null) {
                bufferedReader.close();
            }
        }

        return responseCode;
    }
}
