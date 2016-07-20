
package com.google.tv.eoslauncher.business;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
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

public class AdAction extends ServiceJson {
    private final static String TAG = "AdAction";

    private Handler mHandler;

    private List<MyAD> homeAdInfoList = new ArrayList<MyAD>();

    private final Object isUpdate = new Object();

    private DBManager mDbManager;

    private int[] homeAdItems = {
            R.drawable.heran01, R.drawable.heran02, R.drawable.heran03, R.drawable.heran04, R.drawable.heran05,
            R.drawable.heran06, R.drawable.heran07, R.drawable.heran08
    };
    
    private Runnable mRunnable = new Runnable() {
        
        @Override
        public void run() {
            synchronized (isUpdate) {
                serverUrl = Constants.serverUrl;
                Log.d(TAG, "AdAction().parsePgmJson()");
                String parameter = "{\"ns\": \"ad\",\"nm\": \"GetAd\",\"op\": 1,\"bd\": {\"pos\":\"homeAd.08\",\"typ\": 2,\"siz\": \"300X400\"}}";
                int state = UIUtil.getRespStatus(serverUrl);
                Log.d(TAG, "AdAction().state:" + state);
                if (state == 404 || state == -1) {
                    if (state == -1 && Utils.isNetworkState) {
                        mHandler.sendEmptyMessageDelayed(Constants.UPDATE_AD_FLAG, 120 * 1000);
                    }
                    return;
                }
                try {
                    JSONObject jsonObject = getJSONObject(serverUrl, parameter, true);
                    // Log.d(TAG, "jsonObject:" + jsonObject);
                    if (jsonObject != null) {
                        JSONObject bdObject = jsonObject.getJSONObject("bd");
                        if (bdObject != null) {
                            JSONArray itsJSONArray = bdObject.optJSONArray("its");
                            // Log.d(TAG, "its:" + itsJSONArray);
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
    
    public AdAction(Context mContext, Handler mHandler) {
        
        this.mHandler = mHandler;
        mDbManager = DBManager.getDBInstance(mContext);
        homeAdInfoList = mDbManager.getAds(DBHelper.HOME_AD);
        Log.v(TAG, "AdAction " + (homeAdInfoList == null || homeAdInfoList.isEmpty()));
        if (homeAdInfoList == null || homeAdInfoList.isEmpty()) {
            // init ad informations
            for (int i = 0; i < homeAdItems.length; i++) {
                MyAD adinfo = new MyAD();
                adinfo.setGln("http://www.heran.com.tw/");
                adinfo.setPic("drawable://" + homeAdItems[i]);
                adinfo.setDit(Constants.DELAY_HOMEFRAGMENT_VIEWFLIPPER);
                adinfo.setPlt(Constants.ANIMATION_DEFAULT);
                adinfo.setType(DBHelper.HOME_AD);
                // add default data to db
                int id = (int) mDbManager.insertAd(adinfo);
                adinfo.setId(id);
                homeAdInfoList.add(adinfo);
            }
        }
    }

    public List<MyAD> getAdInfoList() {
        return homeAdInfoList;
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
        // get the min value of jsonArray.length() and homeAdItems.length
        int count = homeAdItems.length > jsonArray.length() ? jsonArray.length() : homeAdItems.length;
        for (int i = 0; i < count; i++) {
            MyAD adInfo = homeAdInfoList.get(i);
            JSONObject jsonobj = jsonArray.getJSONObject(i);
            Log.v(TAG, "" + jsonobj.toString());
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
            adInfo.setType(DBHelper.HOME_AD);
            startDownloadImage(adInfo, i);
        }
    }

    /**
     * start download image
     * 
     * @param ad
     * @param position
     */
    private void startDownloadImage(final MyAD ad, final int position) {
        SimpleImageLoadingListener listener = new SimpleImageLoadingListener() {
            @Override
            public void onLoadingCancelled(String requestUri, View view) {
                super.onLoadingCancelled(requestUri, view);
            }

            @Override
            public void onLoadingComplete(String requestUri, View view, Object dataObject) {
                super.onLoadingComplete(requestUri, view, dataObject);
                Log.v(TAG, "onLoadingComplete position = " + position);
                sendAdUpdateMsg(ad, position);
            }

            @Override
            public void onLoadingFailed(String requestUri, View view, FailReason failReason) {
                super.onLoadingFailed(requestUri, view, failReason);
                Log.v(TAG, "onLoadingFailed " + failReason.getType());
            }

            @Override
            public void onLoadingStarted(String requestUri, View view) {
                super.onLoadingStarted(requestUri, view);
                Log.v(TAG, "onLoadingStarted " + ad.getPic());
            }
        };
        HomeApplication.getInstance().loadImage(ad.getPic(), listener);
    }

    private void sendAdUpdateMsg(final MyAD ad, final int position) {
        // update database
        mDbManager.updateAds(ad);
        // send message to udpate ui
        Message msg = Message.obtain();
        msg.what = Constants.ADUPDATE;
        msg.arg1 = position;
        mHandler.sendMessage(msg);
    }
}
