
package com.heran.launcher2.message;

import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.heran.launcher2.HomeApplication;
import com.heran.launcher2.util.Constants;
import com.heran.launcher2.util.ServiceJson;
import com.heran.launcher2.util.UIUtil;
import com.heran.launcher2.util.Utils;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

public class MessageAction extends ServiceJson {
    private final static String TAG = "AdTextAction";

    private int[] mTxtAdUpDateime = {
            1601010000
    };

    private int LastUpDate = 1601010000;

    private Context mContext;

    private Handler mHandler;

    private String[] mTxtAdsStrings = {
            "歡迎使用HERTV，請連結網路，以享受多元影音樂趣"
    };

    private Runnable mRunnable = new Runnable() {

        @Override
        public void run() {

            synchronized (isUpdate) {
                serverUrl = Constants.serverUrl_home + "?position=newadTxt&cust_type=16";
                Log.d(TAG, "AdTextAction().parsePgmJson()");
                String parameter = "{\"ns\": \"ad\",\"nm\": \"GetAd\",\"op\": 1,\"bd\": {\"pos\":\"txtAd.05\",\"typ\": 3,\"siz\": \"300X400\"}}";
                int state = UIUtil.getRespStatus(serverUrl);
                if (state == 404 || state == -1) {
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
                            mTxtAdUpDateime = new int[length];
                            for (int i = 0; i < length; i++) {
                                JSONObject jsonobj = itsJSONArray.getJSONObject(i);
                                Log.d(TAG, "osd:" + jsonobj.optString("pic"));
                                mTxtAdsStrings[i] = jsonobj.optString("pic");
                                mAdDuration[i] = Integer.parseInt(jsonobj.optString("dit"));
                                Log.v(TAG, "dit = " + mAdDuration[i]);
                                mTxtAdUpDateime[i] = Integer.parseInt(ReplaceStr(jsonobj.optString("upd")));
                                Log.v(TAG, "mTxtAdUpDateime = " + mTxtAdUpDateime[i]);
                            }
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

    private int[] mAdDuration = {
            Constants.UPDATE_AD_TEXT_TIME
    };

    private final Object isUpdate = new Object();

    public int[] getmAdDuration() {
        if (!Utils.isNet) {
            int[] Default = {
                    Constants.UPDATE_AD_TEXT_TIME
            };
            mAdDuration = Default;
        }
        return mAdDuration;
    }

    public String[] getmTxtAdsStrings() {
        String[] Default = {
                "歡迎使用HERTV，請連結網路，以享受多元影音樂趣"
        };
        if (!Utils.isNet) {
            mTxtAdsStrings = Default;
        }
        return mTxtAdsStrings;
    }

    public MessageAction(Context mContext, Handler mHandler) {
        this.mContext = mContext;
        this.mHandler = mHandler;

    }

    public boolean HasUpDate() {

        if (mTxtAdUpDateime[0] > LastUpDate) {
            LastUpDate = mTxtAdUpDateime[0];
            return true;
        } else {
            return false;
        }
    }

    private String ReplaceStr(String str) {
        str = str.replace('-', ' ');
        str = str.replace(':', ' ');
        str = str.replaceAll("\\s", "");
        str = str.substring(2, 12);

        return str;
    }

    /**
     * parse text json message
     * 
     * @param pgms
     * @return
     * @throws JSONException
     */
    public void parsePgmJson() {
        HomeApplication.getInstance().addNetworkTask(mRunnable);
    }
}
