
package com.heran.launcher2.util;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.util.Log;

public abstract class ServiceJson {
    private static final String TAG = "ServiceJson";

    protected String serverUrl = "";

    /**
     * get json file from servers
     * 
     * @return
     * @throws IOException
     */
    public String getJson(String parameter) throws IOException {
        return getJson(parameter, false);
    }

    /**
     * Access to the server json
     * 
     * @param isPost http post request
     * @return
     * @throws IOException
     */
    public String getJson(String parameter, boolean isPost) throws IOException {
        return getJson(getServerUrl(), parameter, false);
    }

    /**
     * Access to the server json
     * 
     * @param isPost http post request
     * @return
     * @throws IOException
     */
    public String getJson(String url, String parameter, boolean isPost) throws IOException {
        Log.d(TAG, parameter);
        long time = System.currentTimeMillis();
        HttpUtil httpUtil = new HttpUtil(url, parameter);
        if (isPost) {
            httpUtil.setRequestMethod(HttpUtil.POST);
        }
        String str = httpUtil.getUrlContent();
        Log.d(TAG, "******api request time****** :" + (System.currentTimeMillis() - time) + "ms");

        if (isPost) {
            Log.d(TAG, "content:" + str);
        }
        return str;
    }

    /**
     * Determine the validity of the server returns
     * 
     * @param json
     * @return
     */
    public boolean isEffectiveJson(String json) {
        // return json != null && json.length() > 0;
        try {
            new JSONObject(json);
        } catch (JSONException e) {
            return false;
        }
        return true;
    }

    /**
     * @param parameter
     * @return
     * @throws IOException
     * @throws JSONException
     */
    public JSONObject getJSONObject(String parameter) throws IOException, JSONException {
        return getJSONObject(parameter, false);
    }

    /**
     * @param parameter
     * @return
     * @throws IOException
     * @throws JSONException
     */
    public JSONObject getJSONObject(String parameter, boolean isPost) throws IOException, JSONException {
        return getJSONObject(getServerUrl(), parameter, isPost);
    }

    /**
     * @param url
     * @param parameter
     * @param isPost
     * @return
     * @throws IOException
     * @throws JSONException
     */
    public JSONObject getJSONObject(String url, String parameter, boolean isPost) throws IOException, JSONException {
        String json = getJson(url, parameter, isPost);
        if (isEffectiveJson(json)) {
            JSONTokener jsonTokener = new JSONTokener(json);
            JSONObject jsonObject = (JSONObject) jsonTokener.nextValue();
            return jsonObject;
        } else {
            Log.d(TAG, "!!error json :: " + json);
        }
        return null;
    }

    public String getServerUrl() {
        return serverUrl;
    }

}
