
package com.google.tv.eoslauncher.business;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map.Entry;

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
import com.google.tv.eoslauncher.util.Utils;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

public class AdAppStoreAction extends ServiceJson {
    private final static String TAG = "AdAppStoreAction";

    private Handler mHandler;

    private DBManager mDbManager;

    private List<MyAD> appStoreAdInfoList = new ArrayList<MyAD>();

    private final Object isUpdate = new Object();

    private final Object cntLock = new Object();

    private volatile int count = 0;

    private int[] appStoreAdItems = {
            R.drawable.a1, R.drawable.a2, R.drawable.a1, R.drawable.a2, R.drawable.a1, R.drawable.a2, R.drawable.a1,
            R.drawable.a2
    };
    
    private Runnable mRunnable = new Runnable() {
        
        @Override
        public void run() {
            synchronized (isUpdate) {
                serverUrl = Constants.serverUrl;
                Log.d(TAG, "AdAppStoreAction().parsePgmJson()");
                String parameter = "{\"ns\": \"ad\",\"nm\": \"GetAd\",\"op\": 1,\"bd\": {\"pos\":\"appStoreAd.08\",\"typ\": 2,\"siz\": \"300X400\"}}";
                int state = UIUtil.getRespStatus(serverUrl);
                Log.d(TAG, "AdAppStoreAction().state:" + state);
                if (state == 404 || state == -1) {
                    if (state == -1 && Utils.isNetworkState) {
                        mHandler.sendEmptyMessageDelayed(Constants.UPDATE_APP_STORE, 120 * 1000);
                    }
                    return;
                }
                try {
                    JSONObject jsonObject = getJSONObject(serverUrl, parameter, true);
                    // Log.d(TAG, "jsonObject:"+jsonObject);
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
    };
    
    public AdAppStoreAction(Context mContext, Handler mHandler) {
        this.mHandler = mHandler;
        mDbManager = DBManager.getDBInstance(mContext);
        appStoreAdInfoList = mDbManager.getAds(DBHelper.APP_STORE_AD);
        Log.v(TAG, "AdAppStoreAction " + (appStoreAdInfoList == null || appStoreAdInfoList.isEmpty()));
        if (appStoreAdInfoList == null || appStoreAdInfoList.isEmpty()) {
            for (int i = 0; i < appStoreAdItems.length; i++) {
                MyAD adinfo = new MyAD();
                adinfo.setGln("http://www.heran.com.tw/");
                adinfo.setPic("drawable://" + appStoreAdItems[i]);
                adinfo.setDit(Constants.DELAY_HOMEFRAGMENT_VIEWFLIPPER);
                adinfo.setPlt(Constants.ANIMATION_DEFAULT);
                adinfo.setType(DBHelper.APP_STORE_AD);
                // insert default data to db
                int id = (int) mDbManager.insertAd(adinfo);
                adinfo.setId(id);
                appStoreAdInfoList.add(adinfo);
            }
        }
    }

    public List<MyAD> getAppStoreAdInfoList() {
        return appStoreAdInfoList;
    }

    /**
     * parse json file from the url
     * 
     * @param pgms
     * @return
     * @throws JSONException
     */
    public void parsePgmJson() {
        HomeApplication.getInstance().addNewTask(mRunnable);
    }

    private void parseSearchConditionsJson(JSONArray jsonArray) throws JSONException {
        Log.d(TAG, "jsonObject.length ::" + jsonArray.length());
        int count = appStoreAdItems.length > jsonArray.length() ? jsonArray.length() : appStoreAdItems.length;
        for (int i = 0; i < count; i++) {
            MyAD adInfo = appStoreAdInfoList.get(i);
            JSONObject jsonobj = jsonArray.getJSONObject(i);
            adInfo.setTi(jsonobj.optString("ti"));
            adInfo.setCpi(jsonobj.optString("cpi"));
            adInfo.setDit(Integer.parseInt(jsonobj.optString("dit")));
            adInfo.setDsr(jsonobj.optString("dsr"));
            adInfo.setPlt(Integer.parseInt(jsonobj.optString("plt")));
            adInfo.setUpd(jsonobj.optString("upd"));
            adInfo.setGln(jsonobj.optString("gln"));
            adInfo.setSiz(jsonobj.optString("siz"));
            String http = jsonobj.optString("pic");
            adInfo.setPic(http);
            adInfo.setType(DBHelper.APP_STORE_AD);
            Log.w(TAG, "http :" + http);
        }
        downloadAllImage();
    }

    private void downloadAllImage() {
        HashMap<String, MyAD> map = new HashMap<String, MyAD>();
        for (MyAD adInfo : appStoreAdInfoList) {
            if (!map.containsKey(adInfo.getPic())) {
                map.put(adInfo.getPic(), adInfo);
            }
        }
        int cunt = map.size();
        Log.v(TAG, "download size = " + cunt);
        for (Entry<String, MyAD> entry : map.entrySet()) {
            downloadImage(entry.getValue(), cunt);
        }
    }

    private void downloadImage(final MyAD adInfo, final int size) {
        SimpleImageLoadingListener listener = new SimpleImageLoadingListener() {
            @Override
            public void onLoadingCancelled(String requestUri, View view) {
                super.onLoadingCancelled(requestUri, view);
            }

            @Override
            public void onLoadingComplete(String requestUri, View view, Object dataObject) {
                super.onLoadingComplete(requestUri, view, dataObject);
                synchronized (cntLock) {
                    count++;
                    Log.v(TAG, "onLoadingComplete count = " + count);
                    // when all picture download finish
                    if (count == size) {
                        Log.v(TAG, "finished download image");
                        // update database data
                        for (MyAD ad : appStoreAdInfoList) {
                            mDbManager.updateAds(ad);
                        }
                        // send message to udpate ui
                        mHandler.sendEmptyMessage(Constants.ADAPPSTOREUPDATE);
                        count = 0;
                    }
                }
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
        HomeApplication.getInstance().loadImage(adInfo.getPic(), listener);
    }
}
