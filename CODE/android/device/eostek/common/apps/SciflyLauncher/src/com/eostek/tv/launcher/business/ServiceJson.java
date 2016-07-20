
package com.eostek.tv.launcher.business;

import java.io.IOException;
import java.util.HashMap;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;

import com.eostek.tv.launcher.util.HttpUtil;

import android.util.Log;

/**
 * 
 * projectName： TVLauncher
 * moduleName： ServiceJson.java
 *
 * @author chadm.xiang
 * @version 1.0.0
 * @time  2014-11-6 上午11:18:49
 * @Copyright © 2014 Eos Inc.
 */
/**
 * The service to get json from network
 **/
public abstract class ServiceJson {

    protected String serverUrl = "";

    private static final String TAG = "ServiceJson";

    private HashMap<String, String> headParams = new HashMap<String, String>();

    private String eTag;

    public String geteTag() {
        return eTag;
    }

    public void seteTag(String tag) {
        this.eTag = tag;
    }

    /**
     * get json file from servers
     * 
     * @param parameter the params add to http request
     * @throws IOException
     * @return The json string
     */
    public String getJson(String parameter) throws IOException {
        return getJson(parameter, false);
    }

    /**
     * Access to the server json
     * 
     * @param parameter the params add to http request
     * @param isPost http post request
     * @throws IOException Exception
     * @return The json string
     */
    public String getJson(String parameter, boolean isPost) throws IOException {
        return getJson(getServerUrl(), parameter, false);
    }

    /**
     * Access to the server json
     * 
     * @param url The url string
     * @param isPost http post request
     * @throws IOException
     * @return The json string
     */
    public String getJson(String url, String parameter, boolean isPost) throws IOException {
        Log.d(TAG, parameter);
        long time = System.currentTimeMillis();
        HttpUtil httpUtil = new HttpUtil(url, parameter);
        if (isPost) {
            httpUtil.setRequestMethod(HttpUtil.POST);
        }
        httpUtil.addHeadMaps(headParams);
        String str = httpUtil.getUrlContent();
        seteTag(httpUtil.geteTag());
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
     * @return true if the json is effective,else false
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
     * @throws IOException
     * @throws JSONException
     * @return The JSONObject
     */
    public JSONObject getJSONObject(String parameter) throws IOException, JSONException {
        return getJSONObject(parameter, false);
    }

    /**
     * @param parameter
     * @throws IOException
     * @throws JSONException
     * @return The JSONObject
     */
    public JSONObject getJSONObject(String parameter, boolean isPost) throws IOException, JSONException {
        return getJSONObject(getServerUrl(), parameter, isPost);
    }

    /**
     * @param url
     * @param parameter
     * @param isPost
     * @throws IOException
     * @throws JSONException
     * @return The JSONObject
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

    /**
     * add param to http request
     */
    protected void addHeadMaps(HashMap<String, String> params) {
        if (params == null) {
            return;
        }
        this.headParams.clear();
        headParams = params;
    }

}
