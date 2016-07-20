
package com.heran.launcher2.advert;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ExecutionException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.FutureTarget;
import com.heran.launcher2.HomeApplication;
import com.heran.launcher2.R;
import com.heran.launcher2.util.Constants;
import com.heran.launcher2.util.ServiceJson;
import com.heran.launcher2.util.UIUtil;
import com.heran.launcher2.util.Utils;
import com.heran.launcher2.util.db.DBHelper;
import com.heran.launcher2.util.db.DBManager;

import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class AdAction extends ServiceJson {
    private final static String TAG = "AdAction";

    private Context mContext;

    private Handler mHandler;

    private DBManager mDbManager;

    private List<MyAD> homeAdInfoList = new ArrayList<MyAD>();

    private final Object isUpdate = new Object();

    private int[] homeAdItems = {
            R.drawable.heran01, R.drawable.heran02, R.drawable.heran03, R.drawable.heran04, R.drawable.heran05,
            R.drawable.heran06, R.drawable.heran07, R.drawable.heran08
    };

    private Runnable mRunnable = new Runnable() {

        @Override
        public void run() {

            synchronized (isUpdate) {
                serverUrl = Constants.serverUrl_home + "?position=newadhome&cust_type=16";
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

    public AdAction(Context mContext, Handler mHandler) {
        this.mContext = mContext;
        this.mHandler = mHandler;

        mDbManager = DBManager.getDBInstance(mContext);
        homeAdInfoList = mDbManager.getAds(DBHelper.HOME_AD);
        // init ad informations
        if (homeAdInfoList == null || homeAdInfoList.isEmpty()) {
            homeAdInfoList = new ArrayList<MyAD>();
            Log.v(TAG, "AdAction " + (homeAdInfoList == null || homeAdInfoList.isEmpty()));
            for (int i = 0; i < homeAdItems.length; i++) {
                MyAD adinfo = new MyAD();
                adinfo.setGln("http://www.heran.com.tw/");
                adinfo.setPic(String.valueOf(homeAdItems));
                adinfo.setDit(Constants.DELAY_HOMEFRAGMENT_VIEWFLIPPER);
                adinfo.setPlt(Constants.ANIMATION_DEFAULT);
                adinfo.setType(DBHelper.HOME_AD);
                // add default data to db
                int id = (int) mDbManager.insertAd(adinfo);
                adinfo.setId(id);
                // mDbManager.updateAds(DBHelper.HOME_AD, homeAdInfoList);
                homeAdInfoList.add(adinfo);
            }
            Log.w(TAG, "homeAdInfoListAction :" + homeAdInfoList.size());
        }
    }

    public List<MyAD> getAdInfoList() {
        return homeAdInfoList;
    }

    /**
     * parse json file from the url
     */
    public void parsePgmJson() {
        HomeApplication.getInstance().addNetworkTask(mRunnable);
    }

    private void parseSearchConditionsJson(JSONArray jsonArray) throws JSONException {
        Log.d(TAG, "jsonObject.length ::" + jsonArray.length());
        int count = jsonArray.length();
        Log.w(TAG, "count :" + count);
        Log.w(TAG, "homeAdInfoList :" + homeAdInfoList.size());
        for (int i = 0; i < count; i++) {
            MyAD adInfo = homeAdInfoList.get(i);
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
            adInfo.setType(DBHelper.HOME_AD);
            Log.w(TAG, "http :" + http);
            mDbManager.updateAds(adInfo);
            startGlideDownlodImgae(adInfo, i);
        }

    }

    private void sendAdUpdateMsg(final MyAD ad, final int position) {
        Message msg = Message.obtain();
        msg.what = Constants.ADUPDATE;
        msg.arg1 = position;
        mHandler.sendMessage(msg);
    }

    private void startGlideDownlodImgae(final MyAD ad, final int position) {
        FutureTarget<File> future = Glide.with(mContext).load(ad.getPic()).downloadOnly(320, 413);
        try {
            File cacheFile = future.get();
            if (cacheFile != null) {
                Log.v(TAG, "GlideDownlodImgae position = " + position + "---path:" + cacheFile.getPath());
                sendAdUpdateMsg(ad, position);
            }
        } catch (InterruptedException e) {
            e.printStackTrace();
        } catch (ExecutionException e) {
            e.printStackTrace();
        }
    }
}
