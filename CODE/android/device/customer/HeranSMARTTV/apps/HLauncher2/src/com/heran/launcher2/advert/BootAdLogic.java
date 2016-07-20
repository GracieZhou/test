
package com.heran.launcher2.advert;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;

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
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;

import com.heran.launcher2.HomeActivity;
import com.heran.launcher2.R;
import com.heran.launcher2.eosweb.MyWebViewActivity;
import com.heran.launcher2.util.HistoryRec;
import com.heran.launcher2.util.Utils;

public class BootAdLogic {

    private static final String TAG = "BootAdLogic";

    private final Context mContext;

    private final BootAdActivity mBootAdActivity;

    public final static int NETWORK_CONNECTION_TIMEOUT = 10 * 1000;

    public final static int NETWORK_READ_DATA_TIMEOUT = 4 * 1000;

    private String mHRecString = "";// 行為紀錄

    private String mPressTime; // 按下按鈕的時間

    public int mLoopValue = 1; // 當前播放的影片組別

    public String mVideoAddress = "";

    private final String mBtnURL = ""; // 按鈕連接位址

    public String mVideoNumber = "00"; // 影片編號暫時為00

    public int mAdBtnNumber = 0; // 預設為0：只有回首頁、略過廣告 (0<=allAdBtnNumber<=8)

    private int mLoopMaxValue = 1; // 看有幾組影片在輪播

    public int mAllAdBtnNumber = 2; // 預設為2：只有回首頁、略過廣告
    
    private Bitmap AD_img ;
    
    private String AD_Url ;

    public String[] mAdBtnLink = new String[10]; // 廣告按鈕連結

    public int[] mActionNumber = new int[10]; // 廣告按鈕執行動作

    public final String[] mBtnName = new String[10]; // 按鈕名稱

    public final int[] mBtnOpen = new int[20]; // 紀錄按鈕背景圖

    private String[] mBtnArraysName;

    public final Handler mHandler;

    private String mHRecBlock = "";

    private static final int WEBVIEW_ACTION = 1;

    private static final int HOME_ACTIVITY_ACTION = WEBVIEW_ACTION + 1;

    private static final int WEBVIEW_FULLSCREEN_ACTION = WEBVIEW_ACTION + 2;

    public BootAdLogic(BootAdActivity bootAdActivity, Handler handler) {
        this.mBootAdActivity = bootAdActivity;
        mContext = bootAdActivity;
        mHandler = handler;
        mBtnArraysName = mContext.getResources().getStringArray(R.array.ad_btn_name);
        mHRecBlock = HistoryRec.block[5]; // 開機廣告代號為6
    }

    /**
     * 行為紀錄
     */
    public void startHistory(int btnId, String btnLastName, int videoLength, String videoNumber) {
        Log.d(TAG, "history:btnID=" + btnId);
        if (btnId == -1) {
            // 使用者沒有任何選擇，直至影片播放完畢
            btnLastName = mBtnArraysName[2];
            mPressTime = String.valueOf(videoLength);
            mHRecString = mHRecBlock + "," + videoNumber + "," + btnLastName + "," + mPressTime + "," + mBtnURL + ","
                    + HistoryRec.getCurrentDateTime();
            Log.d(TAG, "mHRecString=" + mHRecString);
            HistoryRec.writeToFile(mHRecString);
            mHRecString = "";

        } else if (btnId == -2) {
            // 影片2播放錯誤
            btnLastName = mBtnArraysName[3];
            mHRecString = mHRecBlock + "," + videoNumber + "," + btnLastName + "," + mPressTime + "," + mBtnURL + ","
                    + HistoryRec.getCurrentDateTime();
            Log.d(TAG, "mHRecString=" + mHRecString);
            HistoryRec.writeToFile(mHRecString);
            mHRecString = "";
        } else {
            // 依照使用者選擇執行
            mHRecString = mHRecBlock + "," + videoNumber + "," + btnLastName + "," + mPressTime + "," + mBtnURL + ","
                    + HistoryRec.getCurrentDateTime();
            HistoryRec.writeToFile(mHRecString);
            mHRecString = "";
        }
    }

    public void goToWeb(int actionNumber, String webContext) {
        mBootAdActivity.mBootAdViewHodler.getHandler().removeCallbacks(
                mBootAdActivity.mBootAdViewHodler.VideoCountDownRunnable);
        mBootAdActivity.mBootAdViewHodler.mBootVideoView.stopPlayer();
        Intent in = new Intent();
        switch (actionNumber) {
        // 開啟webview，進入連結網頁
            case WEBVIEW_ACTION:
                if (Utils.isNetConnected(mBootAdActivity)) {
                    in.setClass(mContext.getApplicationContext(), MyWebViewActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("URL", webContext);
                    in.putExtras(bundle);
                } else {
                    in.setClass(mContext.getApplicationContext(), HomeActivity.class);
                }
                break;
            // 回首頁，非全螢幕電視
            case HOME_ACTIVITY_ACTION:
                in.setClass(mContext.getApplicationContext(), HomeActivity.class);
                break;
            // 略過廣告，進入全螢幕電視
            case WEBVIEW_FULLSCREEN_ACTION:
                in.setClass(mContext.getApplicationContext(), HomeActivity.class);
                break;
        }
        mContext.startActivity(in);
        mBootAdActivity.finish();
    }

    /**
     * 讀內存變數
     */
    public void readLoopValue() {
        SharedPreferences settings = mContext.getSharedPreferences("PREF_DEMO", 0);
        mLoopValue = settings.getInt("Loop_Value", 1);
    }
    
    private void readLoopUrl(int i) {
        SharedPreferences settings = mContext.getSharedPreferences("BootAD", 0);
        AD_Url = settings.getString(String.valueOf(i),"");
    }

    public void parseJson(String json) {
    	readLoopUrl(mLoopValue);
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

            if(!AD_Url.equals(obj2.getString("nnw"))){ 
            	AD_Url = obj2.getString("nnw"); 
            	AD_img = getUrlPic(AD_Url);                      
            	
            	if(AD_img!=null){
            	saveBitmap(AD_img, String.valueOf(mLoopValue));
            	}	
            }
            
            
            
            JSONArray jArray2 = obj2.getJSONArray("gln");

            // 判斷json解析的廣告選項數量
            mAdBtnNumber = jArray2.length();
            // 總廣告選項數量
            mAllAdBtnNumber = mAdBtnNumber + 2;

            // 抓每個選項的連結網址
            for (int a = 0; a < 10; a++) {
                if (a < mAdBtnNumber) { // 連結
                    JSONObject objtest = jArray2.getJSONObject(a);
                    mAdBtnLink[a] = objtest.getString("buurl");
                    Log.d(TAG, "url:" + objtest.getString("buurl"));
                    mActionNumber[a] = 1;
                    mBtnOpen[a] = Integer.valueOf(objtest.getString("bunumber"));
                    mBtnName[a] = objtest.getString("butext") + "-" + mBtnOpen[a]; // 按鈕名稱
                } else if (a == mAdBtnNumber) {
                    mAdBtnLink[a] = "";
                    mActionNumber[a] = 2;
                    mBtnOpen[a] = 4;
                    mBtnName[a] = mBtnArraysName[0];
                } else if (a == mAdBtnNumber + 1) {
                    mAdBtnLink[a] = "";
                    mActionNumber[a] = 3;
                    mBtnOpen[a] = 5;
                    mBtnName[a] = mBtnArraysName[1];
                } else {
                    mAdBtnLink[a] = "";
                    mActionNumber[a] = 0;
                }
            }
            Log.d(TAG, "for loop ending");
            writeLoopValue();
            Log.d(TAG, "pic :" + mVideoAddress);
        } catch (Exception e) {
            Log.d(TAG, "parseJson 1: " + e);
        }
    }

    /**
     * 寫內存檔案
     */
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
    
    
    public void writeLoopUrl(int i ,String Url) {  
        SharedPreferences settings = mContext.getSharedPreferences("BootAD", 0);
        SharedPreferences.Editor valueEdit = settings.edit();
        valueEdit.putString(String.valueOf(i),Url);
        valueEdit.commit();
    }

    /**
     * 获取服务端视频相关数据
     * 
     * @param url
     * @return
     */
    public String getJson(String url) {
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
            Log.d(TAG, "state=" + state);
            if (state == 200) {
                HttpEntity entity = response.getEntity();
                is = entity.getContent();
            } else {
                Log.d(TAG, "state=" + state + "error");
            }
        } catch (Exception e) {
            Log.d(TAG, "HttpEntityerror" + e.toString());
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
            Log.d(TAG, "reader error:" + e.toString());
        } finally {
            if (is != null) {
                try {
                    is.close();
                    is = null;
                } catch (IOException e) {
                    Log.d(TAG, "error" + e);
                }
            }
        }
        return result;
    }
    
    public synchronized Bitmap getUrlPic(String url) {

        Bitmap webImg = null;

        try {
            URL imgUrl = new URL(url);
            HttpURLConnection httpURLConnection 
                    = (HttpURLConnection) imgUrl.openConnection();
            httpURLConnection.connect();
            InputStream inputStream = httpURLConnection.getInputStream();
            int length = (int) httpURLConnection.getContentLength();
            int tmpLength = 512;
            int readLen = 0,desPos = 0;
            byte[] img = new byte[length];
            byte[] tmp = new byte[tmpLength];
            if (length != -1) {
                while ((readLen = inputStream.read(tmp)) > 0) {
                    System.arraycopy(tmp, 0, img, desPos, readLen);
                    desPos += readLen;
                }
                webImg = BitmapFactory.decodeByteArray(img, 0,img.length);
                if(desPos != length){
                    throw new IOException("Only read" + desPos +"bytes");
                }
            }
            httpURLConnection.disconnect();
        }
        catch (IOException e) {
        	webImg = null;
            Log.e("IOException",e.toString());
        }
        return webImg;
    }
    
    public void saveBitmap(Bitmap bitmap ,String str) {
        FileOutputStream fOut;
        try {
                File dir = new File("/data/video/");
                if (!dir.exists()) {
                        dir.mkdirs();
                }
                File file = new File(dir, str+".png");
                fOut = new FileOutputStream(file);
                bitmap.compress(Bitmap.CompressFormat.PNG, 100, fOut);
                try {
                        fOut.flush();
                        fOut.close();
                        writeLoopUrl(mLoopValue, AD_Url);
                } catch (IOException e) {
                        e.printStackTrace();
                }

        } catch (FileNotFoundException e) {
                e.printStackTrace();
        }
    }
}
