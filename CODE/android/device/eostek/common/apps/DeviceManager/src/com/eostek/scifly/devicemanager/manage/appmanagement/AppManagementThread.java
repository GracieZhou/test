package com.eostek.scifly.devicemanager.manage.appmanagement;

import android.content.Context;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
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

public class AppManagementThread extends Thread{
    
    private static final String TAG = AppManagementThread.class.getSimpleName();
    
    private static final String SERVER_TVOS_URL = SystemProperties.get("ro.scifly.service.url",
            "http://tvosapp.babao.com/interface/clientService.jsp");
    
    private OnThreadListener mOnThreadListener;
    
    private CacheManager mCacheManager;
    
    private Context mContext;
        
    private static List<AppUpdateInfo> mAppManagementUpdateList;
    private static List<ApplicationInfo> mAppcationInfoList = null;
    
    public interface OnThreadListener {
        public void onQueryInfoSuccess();
        public void onQueryInfoFailure();
        public void onSyncInfo(AppUpdateInfo info);
    }
    
    public void setOnThreadListener(OnThreadListener onThreadListener) {
        this.mOnThreadListener = onThreadListener;
    }
    
    public static List<AppUpdateInfo> getUpdateList() {
        return mAppManagementUpdateList;
    }
    
    public AppManagementThread(Context context) {
        this.mContext = context;
    }
    
    @Override
    public void run() {
        if(mAppManagementUpdateList == null) {
            mAppManagementUpdateList = new ArrayList<AppUpdateInfo>();
        }
        mAppcationInfoList = mContext.getPackageManager().getInstalledApplications(PackageManager.GET_UNINSTALLED_PACKAGES);
        mAppManagementUpdateList.clear();
        
        mCacheManager = CacheManager.getCacheLoader(mContext);
        //mCacheManager.stop();
        mCacheManager.clearCache();
        
        CacheListener listener = new CacheListener() {
            @Override
            public void onLoadingComplete(String requestUri, View view, Object dataObject) {
                try {
                    JSONObject json = new JSONObject((String) dataObject);
                    
                    if (json == null) {
                        return;
                    }
                    // 0 success
                    // 1 failure
                    int err = json.optInt("err", -1);
                    Debug.d(TAG, "err=" + err);
                    
                    JSONObject bd = json.optJSONObject("bd");
                    if (bd == null) {
                        return;
                    }
                    
                    JSONArray apkList = bd.optJSONArray("apkList");
                    if (apkList != null && apkList.length() > 0) {
                        Debug.d(TAG, "apkList.length:" + apkList.length());
                        for (int i = 0; i < apkList.length(); i++) {
                            JSONObject apkJson = apkList.optJSONObject(i);

                            final String ico = apkJson.optString("ico");
                            final String na = apkJson.optString("na");
                            final String url = apkJson.optString("url");
                            final String size = apkJson.optString("size");
                            final String pkg = apkJson.optString("pkg");
                            final String ver = apkJson.optString("ver");
                            
                            boolean versionNameFlag = false;     
                            PackageInfo pkginfo = null;
                            for (final ApplicationInfo appInfo : mAppcationInfoList) {
                                if (appInfo.packageName.equals(pkg)) {
                                    pkginfo = mContext.getPackageManager().getPackageInfo(appInfo.packageName, 0);
                                    if (pkginfo.versionCode >= Integer.parseInt(ver)) {
                                        versionNameFlag = false;
                                    } else {
                                        versionNameFlag = true;
                                    }
                                    break;
                                }
                            }
                            
                            if (!versionNameFlag) {
                                continue;
                            }
                            
                            AppUpdateInfo info = new AppUpdateInfo();
                            info.setmPkgName(pkg);
                            info.setmUrlPath(url);
                            info.setmUpdateFlag(versionNameFlag);
                            
                            mAppManagementUpdateList.add(info);
                            Debug.d(TAG, "add update fine:[" + info.mPackName + "]");
                            if(mOnThreadListener != null) {
                                mOnThreadListener.onSyncInfo(info);
                            }
                        }
                    }
                } catch (Exception e) {
                    e.printStackTrace();
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
        };
        
        CacheProgressListener progressListener = new CacheProgressListener() {
            @Override
            public void onProgressUpdate(String imageUri, View view, int current, int total) {
                // TODO Auto-generated method stub
                super.onProgressUpdate(imageUri, view, current, total);
                Debug.d(TAG, "percent:" + current + " / " + total + ", url" + imageUri );

            }
        };
        
        JSONObject json = new JSONObject();
        
        try {
            json.put("ifid", "DetailApk");
            json.put("tvos", String.valueOf(android.os.Build.VERSION.SDK_INT));
            json.put("type", "rcmd");
            JSONArray pkgName = new JSONArray();
            for (ApplicationInfo info : mAppcationInfoList) {
                pkgName.put(info.packageName);
            }
            json.put("pkgName", pkgName);
            Debug.d(TAG, "request:" + json.toString());
        } catch (JSONException e) {
            e.printStackTrace();
        }
        
        String mac = Device.getHardwareAddress(mContext);
        Debug.d(TAG, "mac:" + mac);
        String[] macArray = mac.split(":");
        StringBuffer sb = new StringBuffer();
        for (int i = 0; i < macArray.length; i++) {
            sb.append(macArray[i]);
        }
        String macstr = sb.toString(); 

        String ttag = Device.getDeviceCode() + "_0.0.3490.1_1";
        String bbNumber = Device.getBb();
        String timestamp = String.valueOf(System.currentTimeMillis());
        String calcMD5 = MD5Tool.calcMD5(bbNumber + SERVER_TVOS_URL
                + json.toString() + timestamp + timestamp);
        Debug.d(TAG, "json.toString():" + json.toString());
        String calcMD52 = MD5Tool.calcMD5(bbNumber + SERVER_TVOS_URL
                + json.toString() + Build.DISPLAY.toString().split(" ")[0] + timestamp);
        String tcip = bbNumber + "_" + calcMD5 + "_" + timestamp + "_" + calcMD52;

        HashMap<String, String> map = new HashMap<String, String>();
        map.put("Content-Type", "text/json; charset=UTF-8");
        map.put("Ttag", ttag);
        map.put("devMac", macstr);
        map.put("Tcip", tcip);
        map.put("post", json.toString());
        Debug.d(TAG, "map=" + map);

        try {
            mCacheManager.loadTxtCache(SERVER_TVOS_URL, map, listener, progressListener);
        } catch (IllegalStateException e) {
            e.printStackTrace();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    
    public class AppUpdateInfo {
        
        private String mPackName;
        
        private String mUrlPath;
        
        private boolean mUpdateFlag;
        
        public String getmPkgName() {
            return mPackName;
        }

        public void setmPkgName(String mPkgName) {
            this.mPackName = mPkgName;
        }

        public String getmUrlPath() {
            return mUrlPath;
        }

        public void setmUrlPath(String mUrlPath) {
            this.mUrlPath = mUrlPath;
        }
        
        public boolean getmUpdateFlag() {
            return mUpdateFlag;
        }

        public void setmUpdateFlag(boolean status) {
            this.mUpdateFlag = status;
        }
    }
    
}
