
package com.eostek.wasuwidgethost.business;

import java.io.IOException;
import java.io.InputStream;

import org.json.JSONException;
import org.json.JSONObject;
import org.json.JSONTokener;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;

import com.eostek.wasuwidgethost.util.HttpUtil;

import android.util.Log;
import android.util.Xml;
/**
 * projectName： WasuWidgetHost.
 * moduleName： ServiceJson.java
 *
 */
public abstract class ServiceJson {
    private static final String TAG = "ServiceJson";

    protected String serverUrl = "";

    /**
     * get json file from servers.
     * 
     * @return String
     * @throws IOException
     */
    public final String getJson(String parameter) throws IOException {
        return getJson(parameter, false);
    }

    /**
     * Access to the server json.
     * 
     * @param isPost http post request
     * @return String
     * @throws IOException
     */
    public final String getJson(String parameter, boolean isPost) throws IOException {
        return getJson(getServerUrl(), parameter, false);
    }

    /**
     * Access to the server json.
     * 
     * @param isPost http post request
     * @return String
     * @throws IOException
     */
    public final String getJson(String url, String parameter, boolean isPost) throws IOException {
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
     * get xml file from servers.
     * 
     * @return XmlPullParser
     * @throws IOException
     * @throws XmlPullParserException
     */
    public final XmlPullParser getXML(String parameter) throws IOException, XmlPullParserException {

        return getXML(parameter, false);
    }

    /**
     * Access to the server xml.
     * 
     * @param isPost http post request
     * @return XmlPullParser
     * @throws IOException
     * @throws XmlPullParserException
     */
    public final XmlPullParser getXML(String parameter, boolean isPost) throws IOException, XmlPullParserException {

        return getXML(getServerUrl(), parameter, false);
    }

    /**
     * Access to the server xml.
     * 
     * @param isPost http post request
     * @return XmlPullParser
     * @throws IOException
     * @throws XmlPullParserException
     */
    public final XmlPullParser getXML(String url, String parameter, boolean isPost) throws IOException,
            XmlPullParserException {
        Log.d(TAG, parameter);
        XmlPullParser parser = Xml.newPullParser();
        long time = System.currentTimeMillis();
        HttpUtil httpUtil = new HttpUtil(url, parameter);
        if (isPost) {
            httpUtil.setRequestMethod(HttpUtil.POST);
        }
        InputStream in = httpUtil.getUrlInputStream();
        if (in == null) {
            return null;
        }
        parser.setInput(in, "UTF-8");
        Log.d(TAG, "******api request time****** :" + (System.currentTimeMillis() - time) + "ms");

        return parser;
    }

    /**
     * Determine the validity of the server returns.
     * 
     * @param json
     * @return false if new JSONobject, true otherwise.
     */
    public final boolean isEffectiveJson(String json) {
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
     * @return JSONObject
     * @throws IOException
     * @throws JSONException
     */
    public final JSONObject getJSONObject(String parameter) throws IOException, JSONException {
        return getJSONObject(parameter, false);
    }

    /**
     * @param parameter
     * @return JSONObject
     * @throws IOException
     * @throws JSONException
     */
    public final JSONObject getJSONObject(String parameter, boolean isPost) throws IOException, JSONException {
        return getJSONObject(getServerUrl(), parameter, isPost);
    }

    /**
     * @param url
     * @param parameter
     * @param isPost
     * @return JSONObject
     * @throws IOException
     * @throws JSONException
     */
    public final JSONObject getJSONObject(String url, String parameter, boolean isPost) throws IOException, JSONException {
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

    /**
     * get server url.
     * @return String url
     */
    public final String getServerUrl() {
        return serverUrl;
    }

}
