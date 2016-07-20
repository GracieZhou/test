package com.eostek.scifly.devicemanager.recommend;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.drawable.BitmapDrawable;
import android.os.Build;
import android.os.SystemProperties;
import android.view.View;

import com.eostek.scifly.devicemanager.data.CacheListener;
import com.eostek.scifly.devicemanager.data.CacheManager;
import com.eostek.scifly.devicemanager.data.CacheProgressListener;
import com.eostek.scifly.devicemanager.util.Debug;
import com.ieostek.tms.upgrade.tool.MD5Tool;
import com.nostra13.universalimageloader.core.assist.FailReason;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import scifly.device.Device;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

public class AppRecommendThread extends Thread{
    
    private static final String TAG = AppRecommendThread.class.getSimpleName();
    
    private static final String SERVER_TVOS_URL = SystemProperties.get("ro.scifly.service.url",
            "http://tvosapp.babao.com/interface/clientService.jsp");
    
    private OnThreadListener mOnThreadListener;
    
    private CacheManager mCacheManager;
    
    private Context mContext;
    
    private int mPageIndex;
    
    private static List<AppRecommendInfo> mAppRecommendInfoList = null;

    public interface OnThreadListener {
        public void onQueryInfoSuccess();
        public void onQueryInfoFailure();
        public void onSyncInfo(AppRecommendInfo info);
    }
    
    public void setOnThreadListener(OnThreadListener onThreadListener) {
        this.mOnThreadListener = onThreadListener;
    }
    
    public static List<AppRecommendInfo> getList() {
        return mAppRecommendInfoList;
    }
    
    public AppRecommendThread(Context context, int page) {
        this.mContext = context;
        this.mPageIndex = page;
    }

    @Override
    public void run() {        
        if(mAppRecommendInfoList == null) {
            mAppRecommendInfoList = new ArrayList<AppRecommendInfo>();
        }
        mAppRecommendInfoList.clear();
        
        mCacheManager = CacheManager.getCacheLoader(mContext);
        mCacheManager.clearCache();
        
        CacheListener listener = new CacheListener() {
            @Override
            public void onLoadingComplete(String imageUri, View view, Object loadedImage) {
                try {
                    JSONObject json = new JSONObject((String) loadedImage);
                    
                    if (json != null) {
                        Debug.d(TAG, "err=" + json.optInt("err", -1));// 0 corret 1 error
                        
                        JSONObject bd = json.optJSONObject("bd");
                        
                        if (bd != null) {
                            JSONArray apkList = bd.optJSONArray("apkList");
                            if (apkList != null && apkList.length() > 0) {

                                Debug.d(TAG, "appRecommendInfoList.size=" + apkList.length());
                                
                                for (int i = 0; i < apkList.length(); i++) {
                                    JSONObject apkJson = apkList.optJSONObject(i);

                                    String ico = apkJson.optString("ico");
                                    String na = apkJson.optString("na");
                                    String url = apkJson.optString("url");
                                    String pkg = apkJson.optString("pkg");
                                    
                                    final AppRecommendInfo info = new AppRecommendInfo(pkg, na, url);
                                    
                                    mAppRecommendInfoList.add(info);
                                    
                                    CacheListener listener = new CacheListener() {
                                        @Override
                                        public void onLoadingComplete(String imageUri, View view, Object loadedImage) {
                                            Bitmap icon1 = (Bitmap) loadedImage;
                                            @SuppressWarnings("deprecation")
                                            BitmapDrawable icon = new BitmapDrawable(icon1);
                                            info.setmIcon(icon);
                                            
                                            if(mOnThreadListener != null) {
                                                mOnThreadListener.onSyncInfo(info);
                                            }
                                        }
                                    };
                                    
                                    mCacheManager.loadImageCache(ico, listener);
                                }
                            }
                        }
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                    Debug.d(TAG, e.getMessage());
                }

                //query update list success and notify activity to update GridView
                if(mOnThreadListener != null) {
                    mOnThreadListener.onQueryInfoSuccess();
                }
            }
            
            @Override
            public void onLoadingFailed(String requestUri, View view, FailReason failReason) {
                
                if(mOnThreadListener != null) {
                    mOnThreadListener.onQueryInfoFailure();
                }
                
                super.onLoadingFailed(requestUri, view, failReason);
            }
            
            @Override
            public void onLoadingStarted(String requestUri, View view) {
                
                super.onLoadingStarted(requestUri, view);
            }
        };
        
        CacheProgressListener progressListener = new CacheProgressListener() {
            @Override
            public void onProgressUpdate(String imageUri, View view, int current, int total) {
                super.onProgressUpdate(imageUri, view, current, total);
                Debug.d(TAG, "percent:" + current + " / " + total + ", url" + imageUri );

            }
        };
        
        JSONObject json = new JSONObject();
        try {
            json.put("ifid", "RcmdApk");
            json.put("tvos", String.valueOf(android.os.Build.VERSION.SDK_INT));
            json.put("type", "rcmd");
            json.put("pgn", "10");
            json.put("pgi", String.valueOf(mPageIndex));
            Debug.d(TAG, "request:" + json.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }

        String mac = Device.getHardwareAddress(mContext);
        String[] macArray = mac.split(":");
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < macArray.length; i++) {
            sb.append(macArray[i]);
        }
        String macstr = sb.toString(); 
        String ttag = Device.getDeviceCode() + "_0.0.3490.1_1";
        String bbNumber = Device.getBb();
        String timestamp = String.valueOf(System.currentTimeMillis());
        String calcMD5 = MD5Tool.calcMD5(bbNumber + SERVER_TVOS_URL + json.toString() + timestamp + timestamp);
        String calcMD52 = MD5Tool.calcMD5(bbNumber + SERVER_TVOS_URL + json.toString()
                + Build.DISPLAY.toString().split(" ")[0] + timestamp);
        String tcip = bbNumber + "_" + calcMD5 + "_" + timestamp + "_" + calcMD52;

        HashMap<String, String> map = new HashMap<String, String>();
        map.put("Content-Type", "text/json; charset=UTF-8");
        map.put("Ttag", ttag);
        map.put("devMac", macstr);
        map.put("Tcip", tcip);
        map.put("post", json.toString());
        Debug.d(TAG, "map=" + map);

        mCacheManager.loadTxtCache(SERVER_TVOS_URL, map, listener, progressListener);
    }
}
