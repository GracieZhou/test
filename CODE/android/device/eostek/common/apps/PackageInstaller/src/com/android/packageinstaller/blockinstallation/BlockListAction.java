
package com.android.packageinstaller.blockinstallation;

import java.io.IOException;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import scifly.device.Device;
import android.os.SystemProperties;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;

import com.android.packageinstaller.PackageInstallApplication;
import com.android.packageinstaller.blockinstallation.db.DBManager;

public class BlockListAction extends ServiceJson {
    private static final String TAG = "BlockListAction";
    private Context mContext;
	
	private final String DEFAULT_URL = "http://app.heran.babao.com/interface/clientService.jsp";

    private String mServerUrl;

    public BlockListAction(Context context) {
        this.mContext = context;
		mServerUrl = SystemProperties.get("ro.scifly.service.url", DEFAULT_URL);
    }

    private Runnable mRunnable = new Runnable() {
        @Override
        public void run() {

            synchronized (this) {

                int state = HttpUtil.getRespStatus(mServerUrl);
                Log.d(TAG, "state:" + state);
                if (state == 404 || state == -1) {
                    return;
                }
                try {
                    String ifid = "AppSecureList";
                    String mac = Device.getHardwareAddress(mContext);
                    if (!TextUtils.isEmpty(mac) && mac.contains(":")) {
                        mac = mac.replace(":", "");
                    }
                    String devName = Device.getDeviceName(mContext);
                    String devCode = Device.getDeviceCode();
                    String bbno = Device.getBb();
                    String pkg = mContext.getPackageName();
                    long time = System.currentTimeMillis();
                    String Key = "EOSTEK027";
                    Log.i(TAG, "ifid + mac + devName + devCode + bbno + pkg + time + Key" + ifid + mac + devName
                            + devCode + bbno + pkg + time + Key);
                    String sn = MD5Tools.calcMD5(ifid + mac + devName + devCode + bbno + pkg + time + Key);

                    JSONObject json = new JSONObject();
                    if (!TextUtils.isEmpty(mac) && mac.contains(":")) {
                        mac = mac.replace(":", "");
                    }
                    json.put("ifid", ifid);
                    json.put("mac", mac);
                    json.put("devName", devName);
                    json.put("devCode", devCode);
                    json.put("bbno", bbno);
                    json.put("pkg", pkg);
                    json.put("time", time);
                    json.put("sn", sn);
                    JSONObject jsonObject = getJSONObject(mServerUrl, json.toString(), true);
                    Log.d(TAG, "jsonObject:" + jsonObject);
                    if (jsonObject != null) {
                        int error = jsonObject.optInt("err");
                        if (error == 0) {
                            JSONArray jsonArray = jsonObject.getJSONArray("blockList");
                            // clear database
                            DBManager db = DBManager.getDBInstance(mContext);
                            db.deleteBlockTableAll();
                            for (int i = 0; i < jsonArray.length(); i++) {
                                String block = (String) jsonArray.get(i);
                                db.insertBlockPkg(block);
                            }
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

    public void getBlockListData() {
        PackageInstallApplication.getInstance().addNetworkTask(mRunnable);
//        new Thread(mRunnable).start();
    }

}
