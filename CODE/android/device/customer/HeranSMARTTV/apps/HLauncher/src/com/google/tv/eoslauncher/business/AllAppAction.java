
package com.google.tv.eoslauncher.business;

import java.io.IOException;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;
import android.util.Log;
import android.view.View;

import com.google.tv.eoslauncher.HomeApplication;
import com.google.tv.eoslauncher.R;
import com.google.tv.eoslauncher.business.db.DBHelper;
import com.google.tv.eoslauncher.business.db.DBManager;
import com.google.tv.eoslauncher.model.MyAD;
import com.google.tv.eoslauncher.util.Constants;
import com.google.tv.eoslauncher.util.UIUtil;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class AllAppAction extends ServiceJson {
    private final static String TAG = "AllAppAction";

    private Context mContext;

    private Handler mHandler;

    private MyAD mAd;

    private final Object isUpdate = new Object();

    private DBManager mDbManager;

    public AllAppAction(Context mContext, Handler mHandler) {
        this.mContext = mContext;
        this.mHandler = mHandler;
        mDbManager = DBManager.getDBInstance(mContext);
        List<MyAD> list = mDbManager.getAds(DBHelper.ALL_APP_AD);
        Log.v(TAG, "AdAppStoreAction " + (list == null || list.isEmpty()));
        if (list == null || list.isEmpty()) {
            mAd = new MyAD();
            mAd.setGln("http://www.heran.com.tw/");
            mAd.setPic("drawable://" + R.drawable.ad);
            mAd.setType(DBHelper.ALL_APP_AD);
            int id = (int) mDbManager.insertAd(mAd);
            mAd.setId(id);
        } else {
            mAd = list.get(0);
        }
    }

    /**
     * parse json file from the url
     * 
     * @param pgms
     * @return
     * @throws JSONException
     */
    public void parsePgmJson() {

        new Thread(new Runnable() {
            @Override
            public void run() {
                synchronized (isUpdate) {
                    serverUrl = Constants.serverUrl;
                    Log.d(TAG, "AllAppAction().parsePgmJson()");
                    String parameter = "{\"ns\": \"ad\",\"nm\": \"GetAd\",\"op\": 1,\"bd\": {\"pos\":\"allAppAd.01\",\"typ\": 2,\"siz\": \"300X400\"}}";
                    int state = UIUtil.getRespStatus(serverUrl);
                    Log.d(TAG, "AdAction().state:" + state);
                    if (state == 404 || state == -1) {
                        return;
                    }
                    try {
                        JSONObject jsonObject = getJSONObject(serverUrl, parameter, true);
                        Log.d(TAG, "jsonObject:" + jsonObject);
                        if (jsonObject != null) {
                            JSONObject bdObject = jsonObject.getJSONObject("bd");
                            if (bdObject != null) {
                                JSONArray itsJSONArray = bdObject.optJSONArray("its");
                                Log.d(TAG, "its:" + itsJSONArray);
                                parseSearchConditionsJson(itsJSONArray);
                            }
                        }
                    } catch (IOException e1) {
                        e1.printStackTrace();
                    } catch (JSONException e1) {
                        e1.printStackTrace();
                    }
                }
            }
        }).start();

    }

    private void parseSearchConditionsJson(JSONArray jsonArray) throws JSONException {
        Log.d(TAG, "jsonObject.length ::" + jsonArray.length());
        JSONObject jsonobj = jsonArray.getJSONObject(0);
        mAd.setTi(jsonobj.optString("ti"));
        mAd.setCpi(jsonobj.optString("cpi"));
        mAd.setDit(Integer.parseInt(jsonobj.optString("dit")));
        mAd.setDsr(jsonobj.optString("dsr"));
        mAd.setPlt(Integer.parseInt(jsonobj.optString("plt")));
        mAd.setUpd(jsonobj.optString("upd"));
        mAd.setGln(jsonobj.optString("gln"));
        mAd.setSiz(jsonobj.optString("siz"));
        String http = jsonobj.optString("pic");
        mAd.setPic(http);
        mAd.setType(DBHelper.ALL_APP_AD);
        Log.w(TAG, "http :" + http);
        startDownloadImage(mAd);
    }
    
    /**
     * start download image
     * 
     * @param ad
     * @param position
     */
    private void startDownloadImage(final MyAD ad) {
        SimpleImageLoadingListener listener = new SimpleImageLoadingListener(){
            @Override
            public void onLoadingCancelled(String requestUri, View view) {
                super.onLoadingCancelled(requestUri, view);
            }

            @Override
            public void onLoadingComplete(String requestUri, View view, Object dataObject) {
                super.onLoadingComplete(requestUri, view, dataObject);
                Log.v(TAG, "onLoadingComplete ");
                mDbManager.updateAds(ad);
                mHandler.sendEmptyMessage(Constants.ALLAPPUPDATE);
            }

            @Override
            public void onLoadingFailed(String requestUri, View view, FailReason failReason) {
                super.onLoadingFailed(requestUri, view, failReason);
                Log.v(TAG, "onLoadingFailed " + failReason.getType());
            }

            @Override
            public void onLoadingStarted(String requestUri, View view) {
                super.onLoadingStarted(requestUri, view);
                Log.v(TAG, "onLoadingStarted ");
            }
        };
        HomeApplication.getInstance().loadImage(ad.getPic(), listener);
        
    }

    public MyAD getmAd() {
        return mAd;
    }

    public void setmAd(MyAD mAd) {
        this.mAd = mAd;
    }

}
