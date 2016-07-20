
package com.eostek.scifly.advertising;

import java.io.File;

import scifly.datacache.DataCacheListener;
import scifly.datacache.DataCacheManager;
import android.app.IntentService;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.SystemProperties;
import android.util.Log;
import android.view.View;

import com.eostek.scifly.advertising.modle.ADModle;
import com.eostek.scifly.advertising.util.Constants;
import com.nostra13.universalimageloader.core.assist.FailReason;

/**
 * advertising service.
 * 
 * @author shirley
 */
public class AdvertisingIntentService extends IntentService {

    private static final String TAG = "AdvertisingIntentService";

    private static final int PARSE_JSON_COMPLETED = 3;

    private static final int PARSE_JSON_FAILED = 4;

    private static final int CACHE_IMAGE_COMPLETED = 5;

    private Context mContext = null;

    private AdvertisingManager mADManager;

    private IBinder mIBinder;

    private static final String AD_SWITCH_PROPERTY = "persist.sys.ads.switch";

    private static final String AD_CACHE_COMPLETED_PROPERTY = "persist.sys.ads.cacheCompleted";

    private int mCacheCount = 0;

    private Object obj = new Object();

    /**
     * package and advertising image store path maping file.
     */
    public final static String FILE_SAVE_CFG = "savaPath";

    private final static String AD_PATH = "/ad";

    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case PARSE_JSON_COMPLETED:
                    for (ADModle ad : mADManager.mADList) {
                        cacheAdImages(ad.mImageUrl);
                    }
                    break;
                case CACHE_IMAGE_COMPLETED:
                    mCacheCount = 0;
                    try {
                        SystemProperties.set(AD_CACHE_COMPLETED_PROPERTY, "1");
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                    break;
                case PARSE_JSON_FAILED:
                    break;
                default:
                    break;
            }
        };
    };

    public AdvertisingIntentService() {
        super("com.eostek.scifly.advertising.AdvertisingIntentService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        String adSwitch = SystemProperties.get(AD_SWITCH_PROPERTY, "0");
        if ("1".equals(adSwitch)) {
            try {
                SystemProperties.set(AD_CACHE_COMPLETED_PROPERTY, "0");
                if (mADManager == null) {
                    mADManager = AdvertisingManager.getInstance();
                }
                mADManager.clearCache();
                File cachePath = new File(Constants.CACHE_PATH);
                if (!cachePath.exists()) {
                    Log.d(TAG, "create cache path : " + cachePath.mkdirs());
                }
                cacheJson(mADManager.getServiceUrl());
            } catch (Exception e) {
                e.printStackTrace();
            }
        }
    }

    private void cacheJson(String uri) {
        mADManager.parseJson(mHandler);
    }

    private void cacheAdImages(String uri) {

        mADManager.mDataManager.loadCache(DataCacheManager.DATA_CACHE_TYPE_IMAGE, uri, new DataCacheListener() {
            @Override
            public void onLoadingComplete(String requestUri, View view, Object dataObject) {
                Log.d(TAG, "mCacheCount :: " + mCacheCount + " , mADList.size() :: " + mADManager.mADList.size());
                if (dataObject != null) {
                    synchronized (obj) {
                        mCacheCount++;
                    }
                    if (mCacheCount == mADManager.mADList.size()) {
                        mHandler.sendEmptyMessage(CACHE_IMAGE_COMPLETED);
                    }
                } else {
                    Log.d(TAG, requestUri + " cache image is null");
                }
                super.onLoadingComplete(requestUri, view, dataObject);
            }
            
            @Override
            public void onLoadingFailed(String requestUri, View view, FailReason failReason) {
                Log.d(TAG, failReason.toString());
                super.onLoadingFailed(requestUri, view, failReason);
            }
        });
    }

    /**
     * get advertising image cache path.
     * 
     * @param context Context
     * @return String
     */
    public static String getCachePath(Context context) {
        return context.getExternalCacheDir().getAbsolutePath() + AD_PATH;
    }

}
