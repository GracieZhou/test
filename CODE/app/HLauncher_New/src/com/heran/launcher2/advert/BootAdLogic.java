
package com.heran.launcher2.advert;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.json.JSONArray;
import org.json.JSONObject;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.heran.launcher2.HomeActivity;
import com.heran.launcher2.eosweb.MyWebViewActivity;
import com.heran.launcher2.util.HistoryRec;
import com.heran.launcher2.util.Utils;

public class BootAdLogic {

    private static final String TAG = "BootAdLogic";

    private final Context mContext;

    private final BootAdActivity mBootAdActivity;

    public final static int NETWORK_CONNECTION_TIMEOUT = 10 * 1000;

    public final static int NETWORK_READ_DATA_TIMEOUT = 4 * 1000;

    // 存放廣告影片網址
    private static final String mJsonAddress = "http://www.test.jowinwin.com/hertv2msd/ad_test.php?position=startad&cust_type=18";

    // 行為紀錄
    private String mHRecString = "";

    private String mPressTime; // 按下按鈕的時間

    public int mLoopValue = 1; // 當前播放的影片組別

    public String mVideoAddress = "";

    private final String mBtnURL = ""; // 按鈕連接位址

    public String mVideoNumber = "00"; // 影片編號暫時為00

    // 存放廣告按鈕連結
    public int mAdBtnNumber = 0; // 預設為0：只有回首頁、略過廣告 (0<=allAdBtnNumber<=8)

    // 輪播設定
    private int mLoopMaxValue = 1; // 看有幾組影片在輪播

    public int mAllAdBtnNumber = 2; // 預設為2：只有回首頁、略過廣告

    public String[] mAdBtnLink = new String[10]; // 廣告按鈕連結

    public int[] mActionNumber = new int[10]; // 廣告按鈕執行動作

    public final String[] mBtnName = new String[10]; // 按鈕名稱

    public final int[] mBtnOpen = new int[20]; // 紀錄按鈕背景圖

    public final Handler mHandler;

    private String mHRecBlock = "";

    public BootAdLogic(BootAdActivity bootAdActivity, Handler handler) {
        this.mBootAdActivity = bootAdActivity;
        mContext = bootAdActivity;
        mHandler = handler;
        mHRecBlock = HistoryRec.block[5]; // 開機廣告代號為6
    }

    // 行為紀錄
    public void startHistory(int btnId, String btnLastName, int videoLength, String videoNumber) {
        if (btnId == -1) { // 使用者沒有任何選擇，直至影片播放完畢
            Log.d(TAG, "sarah---history::btnID=" + btnId);
            btnLastName = "不做任何選擇-EE";
            mPressTime = String.valueOf(videoLength);
            mHRecString = mHRecBlock + "," + videoNumber + "," + btnLastName + "," + mPressTime + "," + mBtnURL + ","
                    + HistoryRec.getCurrentDateTime();
            Log.d("aa", "sarah-----history::::" + mHRecString + "!!!!!");
            HistoryRec.writeToFile(mHRecString);
            mHRecString = "";

        } else if (btnId == -2) { // 影片2播放錯誤
            Log.d(TAG, "sarah---history::btnID=" + btnId);
            btnLastName = "異常-EE";
            mHRecString = mHRecBlock + "," + videoNumber + "," + btnLastName + "," + mPressTime + "," + mBtnURL + ","
                    + HistoryRec.getCurrentDateTime();
            Log.d(TAG, "sarah-----history::::" + mHRecString + "!!!!!");
            HistoryRec.writeToFile(mHRecString);
            mHRecString = "";
        } else { // 依照使用者選擇執行
            Log.d(TAG, "sarah---history::btnID=" + btnId);
            mHRecString = mHRecBlock + "," + videoNumber + "," + btnLastName + "," + mPressTime + "," + mBtnURL + ","
                    + HistoryRec.getCurrentDateTime();
            Log.d(TAG, "sarah-----history::::" + mHRecString + "!!!!!");
            HistoryRec.writeToFile(mHRecString);
            mHRecString = "";
        }
    }

    public void goToWeb(int actionNumber, String webContext) {
        mBootAdActivity.mBootAdViewHodler.getHandler().removeCallbacks(
                mBootAdActivity.mBootAdViewHodler.VideoCountDownRunnable);
        Intent in = new Intent();
        switch (actionNumber) {
        // 開啟webview，進入連結網頁
            case 1:
                Log.d(TAG, "click---goto action 111 !!");
                // openTV = true;
                BootAdActivity.isFullscreen = false;
                if (Utils.isNetConnected(mBootAdActivity)) {
                    in.setClass(mContext.getApplicationContext(), MyWebViewActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("URL", webContext);
                    in.putExtras(bundle);
                } else {
                    // isFullscreen = false;
                    in.setClass(mContext.getApplicationContext(), HomeActivity.class);
                }
                break;
            // 回首頁，非全螢幕電視
            case 2:
                BootAdActivity.isFullscreen = false;
                in.setClass(mContext.getApplicationContext(), HomeActivity.class);
                break;
            // 略過廣告，進入全螢幕電視
            case 3:
                Log.d(TAG, "回HomeActivity");
                BootAdActivity.isFullscreen = true;
                in.setClass(mContext.getApplicationContext(), HomeActivity.class);
                break;
        }
        mContext.startActivity(in);
        mBootAdActivity.finish();
    }

    // 讀內存變數
    public void readLoopValue() {
        SharedPreferences settings = mContext.getSharedPreferences("PREF_DEMO", 0);
        mLoopValue = settings.getInt("Loop_Value", 1);
    }

    public Runnable chk_date_run = new Runnable() {
        @Override
        public void run() {
            Log.d(TAG, "启动chk_date_run ");
            String json = "";
            try {
                json = getJson(mJsonAddress);
                Log.d(TAG, "getJson end ");
            } catch (Exception e) {
                Log.d(TAG, "debug 1: " + e.toString());
                // e.printStackTrace();
            }

            try {
                JSONObject obj = new JSONObject(json).getJSONObject("bd");
                JSONArray jArray1 = obj.getJSONArray("its");

                // 判斷json解析的影片數量
                mLoopMaxValue = jArray1.length();

                if (mLoopValue > mLoopMaxValue) {
                    mLoopValue = mLoopMaxValue;
                }

                JSONObject obj2 = jArray1.getJSONObject(mLoopValue - 1);
                mVideoAddress = obj2.getString("pic"); // 影片連結
                mVideoNumber = obj2.getString("dsr"); // 影片編號
                JSONArray jArray2 = obj2.getJSONArray("gln");

                // 判斷json解析的廣告選項數量
                mAdBtnNumber = jArray2.length();
                // 總廣告選項數量
                mAllAdBtnNumber = mAdBtnNumber + 2;

                // 抓每個選項的連結網址
                for (int a = 0; a < 10; a++) {
                    Log.d("aa", "adBtnNumber::" + mAdBtnNumber + "---::" + a);
                    if (a < mAdBtnNumber) { // 連結
                        JSONObject objtest = jArray2.getJSONObject(a);
                        mAdBtnLink[a] = objtest.getString("buurl");
                        Log.d("aa", "url:" + objtest.getString("buurl"));
                        mActionNumber[a] = 1;
                        mBtnOpen[a] = Integer.valueOf(objtest.getString("bunumber"));
                        mBtnName[a] = objtest.getString("butext") + "-" + mBtnOpen[a]; // 按鈕名稱
                        Log.d("aa", "btnName:" + mBtnName[a]);
                    } else if (a == mAdBtnNumber) { // 回首頁
                        mAdBtnLink[a] = "";
                        mActionNumber[a] = 2;
                        mBtnOpen[a] = 4;
                        mBtnName[a] = "回首頁-EE";
                        Log.d(TAG, "btnName:" + mBtnName[a]);
                    } else if (a == mAdBtnNumber + 1) {// 略過廣告
                        mAdBtnLink[a] = "";
                        mActionNumber[a] = 3;
                        mBtnOpen[a] = 5;
                        mBtnName[a] = "略過廣告-EE";
                        Log.d("aa", "btnName:" + mBtnName[a]);
                    } else {
                        mAdBtnLink[a] = "";
                        mActionNumber[a] = 0;
                    }
                }
                Log.d(TAG, "for loop ending!!!!!!!");
                // 影片播放
                mHandler.removeMessages(1);
                mHandler.sendEmptyMessageDelayed(1, 3000);

                writeLoopValue();

                Log.d(TAG, "json :" + json);
                Log.d(TAG, "pic :" + mVideoAddress);
            } catch (Exception e) {
                final int i = -2;
                final String error = "異常-EE";
                Log.d(TAG, "debug 2: " + e.toString());
                Log.d(TAG, "网络状态不佳");
                mBootAdActivity.runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        Toast.makeText(mContext, "网络状态不佳", Toast.LENGTH_SHORT).show();
                        mBootAdActivity.mBootAdViewHodler.resumeHomeActivity(false, i, error);
                    }
                });
            }
        }
    };

    // 寫內存檔案
    public void writeLoopValue() {
        if (mLoopValue < mLoopMaxValue) {
            mLoopValue = mLoopValue + 1;
        } else {
            mLoopValue = 1;
        }

        SharedPreferences settings = mContext.getSharedPreferences("PREF_DEMO", 0);
        SharedPreferences.Editor valueEdit = settings.edit();
        valueEdit.putInt("Loop_Value", mLoopValue);
        valueEdit.commit();
    }

    // json----------------------------------------------------------------
    public String getJson(String url) {
        Log.d(TAG, "getJson ");
        String result = "";
        InputStream is = null;

        try {
            HttpClient httpclient = new DefaultHttpClient();
            // set connect timeout
            httpclient.getParams().setIntParameter(HttpConnectionParams.CONNECTION_TIMEOUT, NETWORK_CONNECTION_TIMEOUT);
            // set read data timeout
            httpclient.getParams().setIntParameter(HttpConnectionParams.SO_TIMEOUT, NETWORK_READ_DATA_TIMEOUT);
            HttpPost httppost = new HttpPost(url);
            HttpResponse response = httpclient.execute(httppost);

            int state = response.getStatusLine().getStatusCode();
            Log.d(TAG, "返回状态码state=" + state);
            if (state == 200) {
                HttpEntity entity = response.getEntity();
                is = entity.getContent();
                Log.d(TAG, "getJason_ok=true2222222222222222");
            } else {
                Log.d(TAG, "返回状态码state=" + state + "==========加载失败");
            }
        } catch (Exception e) {
            Log.d(TAG, "error 1!!" + e.toString());
            // e.printStackTrace();
        }

        try {
            BufferedReader reader = new BufferedReader(new InputStreamReader(is, "utf8"), 9999999);
            StringBuilder sb = new StringBuilder();
            String line = null;
            while ((line = reader.readLine()) != null) {
                sb.append(line + "\n");
            }
            result = sb.toString();
        } catch (Exception e) {
            // e.printStackTrace();
            Log.d(TAG, "error 2!!" + e.toString());
        } finally {
            if (is != null) {
                try {
                    is.close();
                    is = null;
                } catch (IOException e) {
                    // e.printStackTrace();
                }
            }
        }
        return result;
    }
}
