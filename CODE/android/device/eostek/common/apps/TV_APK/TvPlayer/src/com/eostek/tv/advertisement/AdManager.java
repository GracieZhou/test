
package com.eostek.tv.advertisement;

import java.io.IOException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import android.content.Context;
import android.os.Handler;
import android.os.Message;

import com.eostek.tv.utils.Constants;
import com.eostek.tv.utils.LogUtil;
import com.eostek.tv.utils.TvDBHelper;
import com.eostek.tv.utils.TvDBManager;

public class AdManager extends ServiceJson {
    private final static String TAG = "AdManager";

    private Handler mHandler;

    private TvDBManager mDBManager;

    public AdManager(Context mContext, Handler mHandler) {
        this.mHandler = mHandler;
        mDBManager = TvDBManager.getInstance(mContext);
    }

    /**
     * parse json string from the url
     * 
     * @param pgms
     * @return
     * @throws JSONException
     */
    public void parseJson() {
        new Thread(new Runnable() {
            @Override
            public void run() {
                serverUrl = Constants.serverUrl;
                String parameter = "{\"ns\": \"ad\",\"nm\": \"getChannelAd\"}";
                int state = HttpUtil.getRespStatus(serverUrl);
                LogUtil.i("parseJson state:" + state);
                if (state == 404 || state == -1) {
                    return;
                }
                try {
                    JSONObject jsonObject = getJSONObject(serverUrl, parameter, true);
                    LogUtil.d("jsonObject:" + jsonObject);
                    if (jsonObject != null) {
                        JSONObject bdObject = jsonObject.getJSONObject("bd");
                        if (bdObject != null) {
                            JSONArray itsJSONArray = bdObject.optJSONArray("its");
                            // if receive updated data, empty current data
                            // table adinfo
                            long i = mDBManager.emptyData(TvDBHelper.AD_TABLE_NAME);
                            LogUtil.i("emptyData count : " + i);
                            persistJSONData(itsJSONArray);
                        }
                    }
                } catch (IOException e1) {
                    e1.printStackTrace();
                } catch (JSONException e1) {
                    e1.printStackTrace();
                }
            }
        }).start();
    }

    private void persistJSONData(JSONArray jsonArray) throws JSONException {
        LogUtil.i("jsonObject.length ::" + jsonArray.length());
        //image url regex pattern
        Pattern p = Pattern.compile("^(http|https|ftp)://[a-zA-Z0-9.-/:]+$");
        
        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonobj = jsonArray.getJSONObject(i);
            if (jsonobj.optString("pic").equals("") || jsonobj.optString("mde").equals("")
                    || jsonobj.optString("pid").equals("") || jsonobj.optString("gln").equals("")) {
                LogUtil.w(TAG,"data is null, ignore current entry");
                continue;
            }
            Matcher m = p.matcher(jsonobj.optString("pic"));
            if(!m.matches()){
                LogUtil.w(TAG,"invalid image url, ignore current entry");
                continue;
            }
            AdInfo info = new AdInfo();
            info.setTitle(jsonobj.optString("ti"));
            info.setUpdate_date(jsonobj.optString("upd"));
            info.setUpdate_time(jsonobj.optString("upt"));
            info.setPic_url(jsonobj.optString("pic"));
            info.setDescription(jsonobj.optString("dsr"));
            info.setDismiss_time(Integer.parseInt(jsonobj.optString("dit")));
            info.setWebview_url(jsonobj.optString("gln"));
            info.setProgramme_id(Integer.parseInt(jsonobj.optString("pid")));
            info.setSource(jsonobj.optString("mde"));
            info.setPos_x(Integer.parseInt(jsonobj.optString("PosX")));
            info.setPos_y(Integer.parseInt(jsonobj.optString("PosY")));
            mDBManager.insertAdInfo(info);
        }
    }

    @SuppressWarnings("unused")
    private void sendAdUpdateMsg(final int position) {
        Message msg = Message.obtain();
        msg.arg1 = position;
        mHandler.sendMessage(msg);
    }
}
