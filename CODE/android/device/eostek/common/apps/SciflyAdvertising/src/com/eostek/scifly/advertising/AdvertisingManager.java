
package com.eostek.scifly.advertising;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import scifly.datacache.DataCacheConfiguration;
import scifly.datacache.DataCacheListener;
import scifly.datacache.DataCacheManager;
import scifly.device.Device;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Handler;
import android.os.SystemProperties;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Display;
import android.view.View;
import android.view.WindowManager;

import com.eostek.scifly.advertising.modle.ADModle;
import com.eostek.scifly.advertising.util.Constants;
import com.eostek.tm.cpe.manager.CpeManager;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.FailReason;

/**
 * AdvertisingManager.
 * 
 * @author shirley
 */
public class AdvertisingManager {

    private static final String TAG = "AdvertisingManager";

    private static AdvertisingManager mADManager;

    public Context mContext;

    public DataCacheManager mDataManager;

    public List<ADModle> mADList;

    public int mSystem;

    public int mSwitch;

    public long mValid;

    public int mErr;

    private static final String FLAG_SWITCH = "switch";

    private static final String FLAG_SYSTEM = "system";

    private static final String FLAG_VALID = "valid";

    private static final String FLAG_BODY = "bd";

    private static final String FLAG_PKGNAME = "pn";

    private static final String FLAG_IMAGEURL = "imageurl";

    private static final String FLAG_TARGETURL = "targeturl";

    private static final String FLAG_PERIOD = "period";

    private static final String FLAG_TIME = "time";

    private static final String FLAG_ADS = "ads";

    private static final String FLAG_ERR = "err";

    private static final int PARSE_JSON_COMPLETED = 3;

    private static final int PARSE_JSON_FAILED = 4;

    private static final String SERVICE_PROPERTY = "ro.scifly.service.url";

    private AdvertisingManager() {

    }

    /**
     * get AdvertisingManager instance.
     * 
     * @return AdvertisingManager
     */
    public static synchronized AdvertisingManager getInstance() {
        if (mADManager == null) {
            mADManager = new AdvertisingManager();
            mADManager.mDataManager = DataCacheManager.getInstance();
        }
        return mADManager;
    }

    /**
     * init and get DataCacheManager
     * 
     * @return DataCacheManager
     */
    public void initDataCache() {
        File cachePath = new File(Constants.CACHE_PATH);
        if (!cachePath.exists()) {
            Log.d(TAG, "create cache path : " + cachePath.mkdirs());
        }

        try {
            DataCacheConfiguration config = new DataCacheConfiguration.Builder()
                    .maxDiskCacheSize(Constants.TOTAL_CACHE_SIZE).diskCacheSize(Constants.SINGLE_CACHE_SIZE)
                    .diskCacheDir(cachePath).threadPriority(Thread.MAX_PRIORITY).build();
            mDataManager.init(mContext, config);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * clear cache.
     */
    public void clearCache() {
        mDataManager.clearDiskCache();
        mDataManager.clearMemoryCache();
    }

    /**
     * parse json String.
     * 
     * @param jsonStr String
     */
    public void parseJson(final Handler handler) {
        mADList = new ArrayList<ADModle>();

        WindowManager windowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(metrics);

        CpeManager manager = CpeManager.getInstance();
        String devMac = Device.getHardwareAddress(mContext);
        if (devMac != null && !devMac.equals("")) {
            String[] strArray = devMac.split(":");
            StringBuffer modifiedMac = new StringBuffer();
            for (int i = 0; i < strArray.length; i++) {
                modifiedMac.append(strArray[i]);
            }
            devMac = modifiedMac.toString();
        } else {
            devMac = "000000000000";
        }

        Map<String, String> map = new HashMap<String, String>();
        map.put("Content-Type", "text/json; charset=UTF-8");
        map.put("Ttag", manager.getProductClass());
        map.put("Tcip", manager.getBBNumber());

        JSONObject json = new JSONObject();
        try {
            json.put("ifid", "AppAdsList");
            json.put("pla", Build.DISPLAY.split(" ")[0]);
            json.put("lver", Build.VERSION.INCREMENTAL.toString());
            json.put("devId", Device.getBb());
            json.put("mac", devMac);
            json.put("ratx", "" + (int) (metrics.widthPixels * metrics.density));
            json.put("raty", "" + (int) (metrics.heightPixels * metrics.density));
        } catch (JSONException e1) {
            e1.printStackTrace();
        }

        map.put("post", json.toString());

        DisplayImageOptions options = new DisplayImageOptions.Builder().cacheInMemory(true).cacheOnDisk(true)
                .needCheck(true).extraForDownloader(map).build();

        mDataManager.loadCache(DataCacheManager.DATA_CACHE_TYPE_TXT, mADManager.getServiceUrl(), null, options,
                new DataCacheListener() {
                    @Override
                    public void onLoadingComplete(String requestUri, View view, Object dataObject) {
                        String jsonStr = (String) dataObject;
                        writeADsPlanToLocal(jsonStr);
                        Log.d(TAG, "response json string : " + jsonStr);

                        try {
                            if (!TextUtils.isEmpty(jsonStr)) {
                                JSONObject json = new JSONObject(jsonStr);
                                mErr = json.getInt(FLAG_ERR);
                                Log.d(TAG, "err : " + mErr);
                                if (mErr == 0) {
                                    JSONObject chObject = json.getJSONObject(FLAG_BODY);
                                    mSwitch = chObject.getInt(FLAG_SWITCH);
                                    mSystem = chObject.getInt(FLAG_SYSTEM);
                                    mValid = chObject.getLong(FLAG_VALID);
                                    JSONArray jsonArray = chObject.getJSONArray(FLAG_ADS);
                                    for (int i = 0; i < jsonArray.length(); i++) {
                                        JSONObject childJson = jsonArray.getJSONObject(i);
                                        ADModle ad = new ADModle();
                                        ad.mPkgName = childJson.getString(FLAG_PKGNAME);
                                        ad.mImageUrl = childJson.getString(FLAG_IMAGEURL);
                                        ad.mTargetUrl = childJson.getString(FLAG_TARGETURL);
                                        ad.mPeriod = childJson.getString(FLAG_PERIOD);
                                        ad.mTime = childJson.getInt(FLAG_TIME);
                                        mADList.add(ad);
                                    }
                                }
                            }
                        } catch (JSONException e) {
                            e.printStackTrace();
                            handler.sendEmptyMessage(PARSE_JSON_FAILED);
                        }
                        handler.sendEmptyMessage(PARSE_JSON_COMPLETED);
                        super.onLoadingComplete(requestUri, view, dataObject);
                    }

                    @Override
                    public void onLoadingFailed(String requestUri, View view, FailReason failReason) {
                        handler.sendEmptyMessage(PARSE_JSON_FAILED);
                        super.onLoadingFailed(requestUri, view, failReason);
                    }
                }, null);
    }

    /**
     * pkgName is system apk?
     * 
     * @param pkgName String
     * @return true the pkgName is a system apk false the pkgName is not a
     *         system apk
     */
    public boolean isSystemApk(String pkgName) {
        PackageManager mPackageManager = mContext.getPackageManager();
        try {
            PackageInfo info = mPackageManager.getPackageInfo(pkgName, PackageManager.GET_SIGNATURES);
            boolean res = false;
            if (info == null || (info.applicationInfo.flags & ApplicationInfo.FLAG_SYSTEM) == 0) {
                res = false;
            } else {
                res = true;
            }
            Log.d(TAG, "[" + pkgName + "] is" + (res == false ? " NOT " : " ") + "a System-Signature Launcher!");
            return res;
        } catch (PackageManager.NameNotFoundException e) {
            return false;
        }
    }

    public boolean isTimeOk(String period) {
        // "10：30-12：00,14：10-15：30"
        String[] peroids = period.split(",");
        for (String per : peroids) {
            if (isTimeAvaliable(per)) {
                return true;
            }
        }
        return false;
    }

    private boolean isTimeAvaliable(String period) {
        // "10：30-12：00"
        Log.d(TAG, "period : " + period);

        boolean isOk = false;
        if (period != null) {
            try {
                String start = period.substring(0, period.indexOf("-"));
                String end = period.substring(period.indexOf("-") + 1, period.length());

                Calendar c = Calendar.getInstance();
                int hour = c.get(Calendar.HOUR_OF_DAY);
                int min = c.get(Calendar.MINUTE);
                String now = hour + ":" + min;

                Log.d(TAG, "start : " + start + ",  end " + end + ",  now : " + now);

                SimpleDateFormat format = new SimpleDateFormat("HH:mm");
                Date dStart = format.parse(start);
                Date dEnd = format.parse(end);
                Date dNow = format.parse(now);

                long l1 = dNow.getTime() - dStart.getTime();
                long l2 = dEnd.getTime() - dNow.getTime();

                if (l1 >= 0 && l2 >= 0) {
                    isOk = true;
                }

            } catch (Exception e) {
                e.printStackTrace();
            }
        }
        Log.d(TAG, "time is ok : " + isOk);
        return isOk;
    }

    public String canShowAd(String pkgName) {
        String adModle = null;
        if (mADManager != null && mADManager.mADList != null && mADManager.mADList.size() > 0) {
            Log.d(TAG, "ads list length : " + mADManager.mADList.size());
            if (AdvertisingActivity.SWITCH_OFF == mADManager.mSwitch) {
                Log.d(TAG, "switch is off ... ");
            } else if (mADManager.mValid < System.currentTimeMillis() && mADManager.mValid != -1) {
                Log.d(TAG, "valid is unavaliable...");
            } else {
                if (mADManager.isSystemApk(pkgName) && (AdvertisingActivity.SYSTEM_OFF == mADManager.mSystem)) { // 系统应用
                    Log.d(TAG, "system switch is off...");
                } else { // 匹配广告
                    for (ADModle ad : mADManager.mADList) {
                        if (ad.canShowAd(mADManager, pkgName)) {
                            adModle = ad.toString();
                            break;
                        }
                    }
                }
            }
        }
        return adModle;
    }

    private void writeADsPlanToLocal(String adsPlan) {
        String path = Constants.CACHE_PATH + "/adsPlan.txt";
        File file = new File(path);
        File parentDir = new File(file.getParent());
        if (!parentDir.exists()) {
            parentDir.mkdirs();
        }
        BufferedWriter bw = null;
        try {
            if (!file.exists()) {
                file.createNewFile();
            }
            bw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(file)));
            bw.write(adsPlan);
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            try {
                if (bw != null) {
                    bw.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
    }

    public String getServiceUrl() {
        return SystemProperties.get(SERVICE_PROPERTY, Constants.SERVER_URL);
    }
}
