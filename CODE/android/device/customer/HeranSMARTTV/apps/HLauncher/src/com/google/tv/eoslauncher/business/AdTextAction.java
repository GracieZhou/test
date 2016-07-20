
package com.google.tv.eoslauncher.business;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

import com.google.tv.eoslauncher.HomeApplication;
import com.google.tv.eoslauncher.business.db.DBHelper;
import com.google.tv.eoslauncher.business.db.DBManager;
import com.google.tv.eoslauncher.model.MyAD;
import com.google.tv.eoslauncher.util.Constants;
import com.google.tv.eoslauncher.util.UIUtil;
import com.google.tv.eoslauncher.util.Utils;

public class AdTextAction extends ServiceJson {
    private final static String TAG = "AdTextAction";

    private Handler mHandler;

    private DBManager mDbManager;

    private String[] mTxtAdsStrings = {
        "歡迎使用HERTV，請連結網路，以享受多元影音樂趣"
    };

    private int[] mAdDuration = {
        Constants.UPDATE_AD_TEXT_TIME
    };

    private final Object isUpdate = new Object();

    public int[] getmAdDuration() {
        return mAdDuration;
    }

    public String[] getmTxtAdsStrings() {
        return mTxtAdsStrings;
    }
   
    private Runnable mRunnable = new Runnable() {
        
        @Override
        public void run() {
            synchronized (isUpdate) {
                serverUrl = Constants.serverUrl;
                Log.d(TAG, "AdTextAction().parsePgmJson()");
                String parameter = "{\"ns\": \"ad\",\"nm\": \"GetAd\",\"op\": 1,\"bd\": {\"pos\":\"txtAd.05\",\"typ\": 3,\"siz\": \"300X400\"}}";
                int state = UIUtil.getRespStatus(serverUrl);
                if (state == 404 || state == -1) {
                    if (state == -1 && Utils.isNetworkState) {
                        mHandler.sendEmptyMessageDelayed(Constants.UPDATE_ADTEXT_FLAG, 50 * 1000);
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
                            int length = itsJSONArray.length();
                            mTxtAdsStrings = new String[length];
                            mAdDuration = new int[length];
                            List<MyAD> listAds = new ArrayList<MyAD>();
                            for (int i = 0; i < length; i++) {
                                JSONObject jsonobj = itsJSONArray.getJSONObject(i);
                                Log.d(TAG, "osd:" + jsonobj.optString("dsr"));
                                mTxtAdsStrings[i] = jsonobj.optString("dsr");
                                mAdDuration[i] = Integer.parseInt(jsonobj.optString("dit"));
                                MyAD myAD = new MyAD();
                                myAD.setDsr(mTxtAdsStrings[i]);
                                myAD.setDit(mAdDuration[i]);
                                myAD.setType(DBHelper.TEXT_AD);
                                listAds.add(myAD);
                                Log.v(TAG, "dit = " + mAdDuration[i]);
                            }
                            mDbManager.updateAds("txtAd.05", listAds);
                            mHandler.sendEmptyMessage(Constants.OSDUPDATE);
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
    
    public AdTextAction(Context mContext, Handler mHandler) {
        this.mHandler = mHandler;
        mDbManager = DBManager.getDBInstance(mContext);
        List<MyAD> list = mDbManager.getAds(DBHelper.TEXT_AD);
        Log.v(TAG, "" + (list == null || list.isEmpty()));
        if (list == null || list.isEmpty()) {
            for (int i = 0; i < mTxtAdsStrings.length; i++) {
                MyAD myAD = new MyAD();
                myAD.setDsr(mTxtAdsStrings[i]);
                myAD.setDit(mAdDuration[i]);
                myAD.setType(DBHelper.TEXT_AD);
                mDbManager.insertAd(myAD);
            }
        }else {
            int size = list.size();
            mTxtAdsStrings = new String[size];
            mAdDuration = new int[size];
            for (int i = 0; i < size; i++) {
                MyAD ad = list.get(i);
                mTxtAdsStrings[i] = ad.getDsr();
                mAdDuration[i] = ad.getDit();
            }
        }
    }

    /**
     * parse text json ad
     * 
     * @param pgms
     * @return
     * @throws JSONException
     */
    public void parsePgmJson() {
        HomeApplication.getInstance().addNewTask(mRunnable);
    }

}
