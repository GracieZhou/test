
package com.android.settings.bugreport;

import java.util.HashMap;
import java.util.Map;

import org.json.JSONException;
import org.json.JSONObject;

import scifly.device.Device;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;

import com.android.settings.R;
import com.android.settings.util.Utils;
import com.android.volley.AuthFailureError;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.google.gson.Gson;

/**
 * @ClassName: FeedbackRecordActivity.
 * @Description:Check the user feedback records.
 * @author: lucky.li.
 * @date: Sep 15, 2015 9:28:46 AM.
 * @Copyright: Eostek Co., Ltd. Copyright , All rights reserved.
 */
public class FeedbackRecordActivity extends Activity {
    private final String TAG = "FeedbackRecordActivity";

    private FeedbackRecordHolder mHolder;

    /**
     * The request queue
     */
    private RequestQueue mQueue;

    /**
     * Quick feedback request URL
     */
    private final String FEEDBACKRECORDURL = "http://bigdata.88popo.com:8088/feedback/queryHistory";

    /**
     * request params
     */
    private JSONObject jsonRequest = new JSONObject();

    private int mCurrentPageIndex;

    private static final int PAGECOUNT = 4;

    private Gson gson;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_shortcut_feedback);
        mCurrentPageIndex = 1;
        mHolder = new FeedbackRecordHolder(this);
        if (mQueue == null) {
            mQueue = Volley.newRequestQueue(this);
        }
        if (gson == null) {
            gson = new Gson();
        }
        if (Utils.isNetworkAvailable(this)) {
            mHolder.showProgress();
            constructRequestParam(mCurrentPageIndex);
            initJsonRequest();
        } else {
            Utils.showToast(FeedbackRecordActivity.this, R.string.network_disconnected);
        }
    }

    public void setCurrentPageIndex(int mCurrentPageIndex) {
        this.mCurrentPageIndex = mCurrentPageIndex;
    }

    public int getCurrentPageIndex() {
        return mCurrentPageIndex;
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        overridePendingTransition(R.anim.fade_out_left, R.anim.fade_out);
    }

    /**
     * @Title: constructRequestParam
     * @Description: Construction request shortcut feedback parameters
     * @param:
     * @return: void
     * @throws
     */
    public void constructRequestParam(int pageIndex) {
        mCurrentPageIndex = pageIndex;
        JSONObject jsonObject1 = new JSONObject();
        try {
            jsonObject1.put("userID", Device.getBb());
            jsonObject1.put("pageIndex", mCurrentPageIndex);
            jsonObject1.put("pageCount", PAGECOUNT);
        } catch (JSONException e) {
            e.printStackTrace();
            jsonObject1 = null;
        }
        try {
            jsonRequest.put("bd", jsonObject1);
            Log.i(TAG, "jsonRequest==" + jsonRequest.toString());
        } catch (JSONException e) {
            e.printStackTrace();
            jsonRequest = null;
        }
    }

    /**
     * @Title: initJsonRequest
     * @Description: Json request
     * @param:
     * @return: void
     * @throws
     */
    public void initJsonRequest() {
        JsonObjectRequest mJsonObjectRequest = new JsonObjectRequest(FEEDBACKRECORDURL, jsonRequest,
                new Response.Listener<JSONObject>() {

                    @Override
                    public void onResponse(JSONObject response) {
                        mHolder.hideProgress();
                        Log.i(TAG, response.toString());
                        // parse json datas
                        FeedbackRecordBean feedbackRecord = gson.fromJson(response.toString(), FeedbackRecordBean.class);
                        // refresh datas
                        mHolder.refresh(feedbackRecord);
                    }
                }, new Response.ErrorListener() {

                    @Override
                    public void onErrorResponse(VolleyError error) {
                        Log.i(TAG, "-----request error-----");
                        if (mCurrentPageIndex > 1) {
                            mCurrentPageIndex--;
                        }
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
