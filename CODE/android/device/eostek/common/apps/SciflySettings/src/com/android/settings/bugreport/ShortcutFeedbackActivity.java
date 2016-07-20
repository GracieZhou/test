
package com.android.settings.bugreport;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.android.settings.R;
import com.android.settings.util.Utils;
import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.eostek.tm.cpe.manager.CpeManager;
import com.google.gson.Gson;

/**
 * @ClassName: ShortcutFeedbackActivity.
 * @Description:For the user to choose quick feedback, don't let the user write
 *                  their own.
 * @author: lucky.li
 * @date: 2015-8-24 pm 5:24:35
 * @Copyright: Eostek Co., Ltd. Copyright , All rights reserved
 */
public class ShortcutFeedbackActivity extends Activity {
    private final String TAG = "ShortcutFeedbackActivity";

    /**
     * request queue
     */
    private RequestQueue mQueue;

    /**
     * shorcut feedback url
     */
    private final String SHORCUTFEEDBACKURL = "http://bigdata.88popo.com:8088/feedback/deviceQueryProblems";

    /**
     * request params.
     */
    private JSONObject jsonRequest = new JSONObject();

    private Gson gson;

    private ShortcutFeedbackHolder mHolder;

    private SharedPreferences sp;

    /**
     * cache key.
     */
    private final String keyString = "shortcut_feedback";

    /**
     * cache json string.
     */
    private String shotcutJsonString;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shortcut_feedback);
        mHolder = new ShortcutFeedbackHolder(this);
        if (mQueue == null) {
            mQueue = Volley.newRequestQueue(this);
        }
        if (gson == null) {
            gson = new Gson();
        }
        sp = getSharedPreferences("settings", Context.MODE_PRIVATE);
        String shotcutJsonString = sp.getString(keyString, "");
        // if local has no cache
        if (TextUtils.isEmpty(shotcutJsonString)) {
            if (Utils.isNetworkAvailable(this)) {
                mHolder.showProgress();
            } else {
                Utils.showToast(this, R.string.network_disconnected);
            }
        } else {
            // parse json datas
            ShorcutFeedbackBean bean = gson.fromJson(shotcutJsonString, ShorcutFeedbackBean.class);
            // refresh data
            mHolder.refresh(bean);
        }
        constructRequestParam();
        initJsonRequest();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_out_left, R.anim.fade_out);
    }

    /**
     * @Title: constructRequestParam.
     * @Description: construct Request Param.
     * @param:
     * @return: void
     * @throws
     */
    private void constructRequestParam() {
        JSONObject jsonObject1 = new JSONObject();
        try {
            jsonObject1.put("signature", CpeManager.getInstance().getProductClass());
        } catch (JSONException e) {
            e.printStackTrace();
            jsonObject1 = null;
        }
        try {
            jsonRequest.put("bd", jsonObject1);
        } catch (JSONException e) {
            e.printStackTrace();
            jsonRequest = null;
        }
        Log.i(TAG, jsonRequest.toString());
    }

    /**
     * @Title: initJsonRequest
     * @Description: Json request. 
     * @param:
     * @return: void
     * @throws
     */
    private void initJsonRequest() {
        JsonObjectRequest mJsonObjectRequest = new JsonObjectRequest(SHORCUTFEEDBACKURL, jsonRequest,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        Log.i(TAG, response.toString());
                        mHolder.hideProgress();
                        // If the server returns the data and cache, display data server and storage
                        if (!response.toString().equals(shotcutJsonString)) {
                            // cache it 
                            sp.edit().putString(keyString, response.toString()).commit();
                            // parse json datas
                            ShorcutFeedbackBean bean = gson.fromJson(response.toString(), ShorcutFeedbackBean.class);
                            // refresh data
                            mHolder.refresh(bean);
                        }
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i(TAG, "-----request error-----");
                        mHolder.hideProgress();
                    }
                }) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<String, String>();
                headers.put("Accept-Language", getResources().getConfiguration().locale + "");
                return headers;
            }
        };
        mQueue.add(mJsonObjectRequest);
    }
}
