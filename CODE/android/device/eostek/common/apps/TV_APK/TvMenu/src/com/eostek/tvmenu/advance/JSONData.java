package com.eostek.tvmenu.advance;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import android.os.Build;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;


import com.eostek.tvmenu.utils.MD5Tools;
import com.jrm.util.GetDevice;

public class JSONData {

    private static final String TAG = "MSettings.JSONData";

    private static final String JSON_URL = "http://app.heran.babao.com/interface/clientService.jsp";

    private static String getJsonString(JSONObject json) {
        GetDevice getDevice = new GetDevice();
        String bbNumber = null;
        try {
            bbNumber = getDevice.getBbNumer();
        } catch (RemoteException e) {
        }

        if (TextUtils.isEmpty(bbNumber)) {
            return "";
        }
        Log.d(TAG, "bbNumber, " + bbNumber);

        String tcip = bbNumber
                + "_"
                + MD5Tools.calcMD5(bbNumber + JSON_URL + json + System.currentTimeMillis()
                        + System.currentTimeMillis())
                + "_"
                + System.currentTimeMillis()
                + "_"
                + MD5Tools.calcMD5(bbNumber + JSON_URL + json
                        + Build.DISPLAY.toString().split(" ")[0] + System.currentTimeMillis());

		Log.d(TAG, "tcip:"+tcip);
        Log.d(TAG, "json:" + json.toString());

        StringBuffer buffer = new StringBuffer();
        HttpURLConnection httpUrlConnection = null;
        Writer writer = null;
        InputStream inputStream = null;
        BufferedReader bufferedReader = null;
        try {
            URL url = new URL(JSON_URL);
            httpUrlConnection = (HttpURLConnection) url.openConnection();
            httpUrlConnection.setRequestProperty("Content-Type", "text/json; charset=UTF-8");
            httpUrlConnection.setRequestProperty("Ttag", Build.DISPLAY.toString().split(" ")[0]
                    + "_0.0.3490.1_1");
            httpUrlConnection.setRequestProperty("Tcip", tcip);
            httpUrlConnection.setDoOutput(true);
            httpUrlConnection.connect();

            writer = new OutputStreamWriter(httpUrlConnection.getOutputStream(), "utf-8");
            writer.write(json.toString());
            writer.flush();

            Log.d(TAG, "response code, " + httpUrlConnection.getResponseCode());
            inputStream = httpUrlConnection.getInputStream();
            bufferedReader = new BufferedReader(new InputStreamReader(inputStream, "utf-8"));

            String line = "";
            while ((line = bufferedReader.readLine()) != null) {
                buffer.append(line);
            }
        } catch (Exception e) {
            e.printStackTrace();
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
            }
        }
        Log.d(TAG, "buffer.toString, " + buffer.toString());

        return buffer.toString();
    }

    public static String getUpgradeInfo() {
        JSONObject json = new JSONObject();
        try {
            json.put("ifid", "TVOSVerUpdate");
            json.put("pla", Build.DISPLAY.toString().split(" ")[0]);
            json.put("lver", "V"+Build.VERSION.INCREMENTAL.toString());
            Log.d(TAG, "Build.DISPLAY------------- " +Build.DISPLAY.split(" ")[0]);
            Log.d(TAG, "Build.VERSION.INCREMENTAL------------- " + Build.VERSION.INCREMENTAL.toString());
        } catch (JSONException e) {
            e.printStackTrace();

            return "";
        }

        return getJsonString(json);
    }

}
