
package com.heran.launcher.business;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import com.heran.launcher.LauncherApplication;
import com.heran.launcher.util.UIUtil;
import com.heran.launcher.util.Utils;

import android.content.Context;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import scifly.device.Device;

/*
 * projectName： EosLauncher
 * moduleName： VIPAction.java
 * @author laird.li
 * @version 1.0.0
 * @time  2016-4-11 下午4:06:11
 * @Copyright © 2013 Eos Inc.
 */
public class VIPAction extends ServiceJson {

    private final static String TAG = "VIPAction";

    private Context mContext;

    private Handler mHandler;

    private final Object isUpdate = new Object();

    public VIPAction() {
        // TODO Auto-generated constructor stub
    }

    public VIPAction(Context context, Handler handler) {
        this.mContext = context;
        this.mHandler = handler;
    }

    public void getVIPData() {
        LauncherApplication.getInstance().addNetworkTask(mRunnable);
    }

    private Runnable mRunnable = new Runnable() {

        @Override
        public void run() {

            synchronized (isUpdate) {
                serverUrl = "http://scilfyinter.88popo.com:443/interface/clientService.jsp";

                Log.d(TAG, "VIPAction().parsePgmJson()");
                int state = UIUtil.getRespStatus(serverUrl);
                Log.d(TAG, "VIPAction().state:" + state);
                if (state == 404 || state == -1) {
                    if (state == -1 && Utils.isNetworkState) {
                        mHandler.removeMessages(3);
                        mHandler.sendEmptyMessageDelayed(3, 120 * 1000);
                    }
                    return;
                }
                try {
                    JSONObject json = new JSONObject();
                    json.put("ifid", "GetVipConfig");
                    String mac = Device.getHardwareAddress(mContext);
                    Log.d(TAG, "mac old:" + mac);
                    if (!TextUtils.isEmpty(mac) && mac.contains(":")) {
                        mac = mac.replace(":", "");
                    }
                    Log.d(TAG, "mac new:" + mac);
                    json.put("mac", mac);
                    Log.d(TAG, json.toString());

                    JSONObject jsonObject = getJSONObject(serverUrl, json.toString(), true);
                    Log.d(TAG, "jsonObject:" + jsonObject);
                    if (jsonObject != null) {
                        int result = -1;
                        result = jsonObject.optInt("err");
                        Log.d(TAG, "getMyVIP result:" + result);
                        if (result == 0) {
                            JSONObject jsons = jsonObject.getJSONObject(("bd"));
                            int isVip = jsons.getInt("isVip");
                            Log.d(TAG, "isVip :" + isVip + "");
                            Device.setVipMode(mContext, isVip);
                            Log.d(TAG, "get isVip :" + Device.isVipMode(mContext) + "");
                        }else{
                            Device.setVipMode(mContext, 0);
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

}
