
package com.heran.launcher2.apps;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.FutureTarget;
import com.heran.launcher2.HomeApplication;
import com.heran.launcher2.R;
import com.heran.launcher2.advert.MyAD;
import com.heran.launcher2.util.Constants;
import com.heran.launcher2.util.ServiceJson;
import com.heran.launcher2.util.UIUtil;
import com.heran.launcher2.util.db.DBHelper;
import com.heran.launcher2.util.db.DBManager;

public class AllAppAction extends ServiceJson {
    private final static String TAG = "AllAppAction";

    private Context mContext;

    private Handler mHandler;

    private MyAD mAd;

    private DBManager mDbManager;

    private final Object mIsUpdate = new Object();
    
    private Runnable mRunnable = new Runnable() {
        
        @Override
        public void run() {

            synchronized (mIsUpdate) {
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
    };

    public AllAppAction(Context mContext, Handler mHandler) {
        this.mContext = mContext;
        this.mHandler = mHandler;
        mDbManager = DBManager.getDBInstance(mContext);
        List<MyAD> list = mDbManager.getAds(DBHelper.ALL_APP_AD);
        Log.v(TAG, "AdAppStoreAction " + (list == null || list.isEmpty()));
        if (list == null || list.isEmpty()) {
            mAd = new MyAD();
            mAd.setGln("http://www.heran.com.tw/");
            mAd.setPic(String.valueOf(R.drawable.ad));
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
        HomeApplication.getInstance().addNetworkTask(mRunnable);
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
        startGlideDownlodImgae(mAd);
    }

    public MyAD getmAd() {
        return mAd;
    }

    public void setmAd(MyAD mAd) {
        this.mAd = mAd;
    }

    private void startGlideDownlodImgae(final MyAD ad) {
        FutureTarget<File> future = Glide.with(mContext).load(ad.getPic()).downloadOnly(400, 900);
        try {
            File cacheFile = future.get();
            if (cacheFile != null) {
                mDbManager.updateAds(ad);
                mHandler.sendEmptyMessage(Constants.ALLAPPUPDATE);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }

}
