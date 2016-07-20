
package com.eostek.scifly.advertising;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Writer;
import java.net.HttpURLConnection;
import java.net.URISyntaxException;
import java.net.URL;

import org.json.JSONException;
import org.json.JSONObject;

import scifly.datacache.DataCacheListener;
import scifly.datacache.DataCacheManager;
import scifly.device.Device;
import scifly.provider.SciflyStatistics;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ResolveInfo;
import android.graphics.Bitmap;
import android.graphics.drawable.Drawable;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.TextView;

import com.eostek.scifly.advertising.modle.ADModle;
import com.eostek.scifly.advertising.util.Constants;
import com.eostek.tm.cpe.manager.CpeManager;
import com.nostra13.universalimageloader.core.assist.FailReason;

/**
 * To show advertising before start a app.
 * 
 * @author shirley
 */
public class AdvertisingActivity extends Activity {

    private static final String TAG = "AdvertisingActivity";

    private static final int STOP_AD_SHOW = 1;

    private static final int REFRESH_TIME = 2;

    private static final int PARSE_JSON_COMPLETED = 3;

    private static final int PARSE_JSON_FAILED = 4;

    private static final int DEFAULT_TIME_BASE = 1000;

    private static final int REFRESH_SPACE = 1 * 1000;

    public static final int SWITCH_OFF = 0;

    public static final int SYSTEM_OFF = 0;

    private AdvertisingManager mADManager;

    private ADModle mCurrenAd = null;

    private boolean isClick = false;

    private ImageView mImageView;

    private TextView mTimeView;

    private String mPackageName = "com.eostek.scifly.advertising";

    private Intent mTargetIntent = null;

    private int mTime;

    Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case STOP_AD_SHOW:
                    if (!isClick) {
                        removeMessages(STOP_AD_SHOW);
                        stopAD();
                    }
                    break;
                case REFRESH_TIME:
                    mTimeView.setText("" + String.format(getResources().getString(R.string.ad_time_tip), --mTime));
                    if (mTime > 0) {
                        mHandler.removeMessages(REFRESH_TIME);
                        mHandler.sendEmptyMessageDelayed(REFRESH_TIME, REFRESH_SPACE);
                    }
                    break;
                case PARSE_JSON_COMPLETED:
                    afterParseJsonCompleted();
                    break;
                case PARSE_JSON_FAILED:
                    removeCallbacksAndMessages(null);
                    stopAD();
                    break;
                default:
                    break;
            }
        };
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "SciflyAdvertising, Version:1.0.0 Date:2015-08-18, Publisher:Shirley.jiang REV:39990");
        initData();
    }

    private void initData() {
        Intent intent = getIntent();
        mADManager = AdvertisingManager.getInstance();
        // get target Intent
        String uri = intent.getStringExtra("targetIntent");
        try {
            mTargetIntent = Intent.parseUri(uri, 0);
        } catch (URISyntaxException e) {
            e.printStackTrace();
            finish();
            return;
        }

        if (mTargetIntent == null) {
            finish();
            return;
        }

        if (mTargetIntent.getComponent() == null) {
            stopAD();
        } else {
            mPackageName = mTargetIntent.getComponent().getPackageName();
        }

        Log.d(TAG, "targetIntent : " + mTargetIntent);
        if (mPackageName == null || "com.eostek.scifly.advertising".equals(mPackageName)
                || mPackageName.equals(getLauncherPkgName(getApplicationContext()))) {
            stopAD();
            return;
        } else {
            // showADLogic(mPackageName);
            String value = intent.getStringExtra("adModle");
            if (TextUtils.isEmpty(value)) {
                stopAD();
                return;
            } else {
                String[] values = value.split(";");
                mCurrenAd = new ADModle(values[0], values[1], values[2], values[3], Integer.parseInt(values[4]),
                        values[5]);
                showAD(mCurrenAd);
            }
        }
    }

    @Override
    protected void onRestart() {
        super.onRestart();
    }

    @Override
    protected void onPause() {
        if (!isClick) {
            String topActivityPkgName = getRunningActivityName();
            Log.d(TAG, "topActivityPkgName : " + topActivityPkgName);
            if (!mPackageName.equals(topActivityPkgName)) {
                mHandler.removeCallbacksAndMessages(null);
                finish();
            }
        } else {
            mHandler.removeCallbacksAndMessages(null);
        }
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    protected void onStart() {
        super.onStart();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    private void stopAD() {
        Log.d(TAG, "stop show AD.");
        finish();
        startApk(mTargetIntent);
    }

    private void startApk(Intent intent) {
        if (intent != null) {
            try {
                startActivity(intent);
            } catch (Exception e) {
                e.printStackTrace();
                Log.d(TAG, intent.toString() + "---> the intent is wrong");
            }
        }
    }

    private String getRunningActivityName() {
        ActivityManager activityManager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        String runningActivity = activityManager.getRunningTasks(1).get(0).topActivity.getPackageName();
        return runningActivity;
    }

    /**
     * advertising image click listener.
     * 
     * @author shirley
     */
    private class ImageClickListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            if (!TextUtils.isEmpty(mCurrenAd.mTargetUrl)) {
                isClick = true;
                mHandler.removeCallbacksAndMessages(null);
                Intent intent = new Intent(AdvertisingActivity.this, ADBrowserActivity.class);
                intent.putExtra("url", mCurrenAd.mTargetUrl);
                intent.putExtra("fromAD", true);
                intent.putExtra("targetIntent", mTargetIntent.toUri(0));

                finish();
                startActivity(intent);
            }
        }
    }

    private String getLauncherPkgName(Context context) {
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_HOME);
        ResolveInfo res = context.getPackageManager().resolveActivity(intent, 0);

        String lunchPkgName = "";
        if (res == null || res.activityInfo == null || "android".equals(res.activityInfo.packageName)) {
            lunchPkgName = "";
        } else {
            lunchPkgName = res.activityInfo.packageName;
        }
        Log.d(TAG, "lunchPkgName : " + lunchPkgName);
        return lunchPkgName;
    }

    private void afterParseJsonCompleted() {

        if (mADManager.mADList == null || mADManager.mADList.size() == 0) {
            mHandler.removeCallbacksAndMessages(null);
            stopAD();
        } else {
            Log.d(TAG, "ads list length : " + mADManager.mADList.size());
            if (SWITCH_OFF == mADManager.mSwitch) {
                Log.d(TAG, "switch is off ... ");
                stopAD();
                return;
            } else if (mADManager.mValid < System.currentTimeMillis() && mADManager.mValid != -1) {
                Log.d(TAG, "valid is unavaliable...");
                stopAD();
                return;
            } else {
                if (mADManager.isSystemApk(mPackageName) && (SYSTEM_OFF == mADManager.mSystem)) { // 系统应用
                    Log.d(TAG, "system switch is off...");
                    stopAD();
                    return;
                } else { // 匹配广告
                    for (ADModle ad : mADManager.mADList) {
                        if (ad.canShowAd(mADManager, mPackageName)) {
                            mCurrenAd = ad;
                            break;
                        }
                    }
                    if (mCurrenAd == null) {
                        Log.d(TAG, "current apk is no ad...");
                        stopAD();
                        return;
                    } else {
                        showAD(mCurrenAd);
                    }
                }

                // if (mADManager.isSystemApk(mPackageName)) {
                // if (SYSTEM_OFF == mADManager.mSystem) {
                // Log.d(TAG, "system switch is off...");
                // stopAD();
                // return;
                // } else {
                // for (ADModle ad : mADManager.mADList) {
                // if (mPackageName.equals(ad.mPkgName) && isTimeOk(ad.mPeriod))
                // {
                // mCurrenAd = ad;
                // break;
                // }
                // }
                // }
                // } else {
                // for (ADModle ad : mADManager.mADList) {
                // if (mPackageName.equals(ad.mPkgName) && isTimeOk(ad.mPeriod))
                // {
                // mCurrenAd = ad;
                // break;
                // }
                // }
                // if (mCurrenAd == null) {
                // for (ADModle ad : mADManager.mADList) {
                // Pattern pattern = Pattern.compile(ad.mPkgName);
                // Matcher matcher = pattern.matcher(mPackageName);
                // Log.d(TAG, "match : " + ad.mPkgName + "," + mPackageName +
                // " : " + matcher.matches());
                // if (matcher.matches() && isTimeOk(ad.mPeriod)) {
                // mCurrenAd = ad;
                // break;
                // }
                // }
                // }
                // }
                // if (mCurrenAd == null) {
                // Log.d(TAG, "current apk is no ad...");
                // stopAD();
                // return;
                // } else {
                // showAD(mCurrenAd);
                // }
            }
        }

    }

    private void showADLogic(String pkgName) {
        Log.d(TAG, "show ad logic ... ");
        mADManager.parseJson(mHandler);
    }

    private void showAD(final ADModle ad) {
        Log.d(TAG, "begin to load image ...");

        setContentView(R.layout.activity_main);
        mImageView = (ImageView) findViewById(R.id.image);
        mTimeView = (TextView) findViewById(R.id.time);
        mTime = ad.mTime;

        mADManager.mDataManager.loadCache(DataCacheManager.DATA_CACHE_TYPE_IMAGE, ad.mImageUrl,
                new DataCacheListener() {
                    @Override
                    public void onLoadingComplete(String requestUri, View view, Object dataObject) {
                        Log.d(TAG, "image load completed....");
                        initInterface((Bitmap) dataObject, ad);
                        super.onLoadingComplete(requestUri, view, dataObject);
                    }

                    @Override
                    public void onLoadingFailed(String requestUri, View view, FailReason failReason) {
                        stopAD();
                        super.onLoadingFailed(requestUri, view, failReason);
                    }
                });
    }

    private void initInterface(Object obj, ADModle ad) {
        adDataRecord();
        if (obj instanceof Bitmap) {
            mImageView.setImageBitmap((Bitmap) obj);
        } else if (obj instanceof Drawable) {
            mImageView.setImageDrawable((Drawable) obj);
        }
        mTimeView.setText("" + String.format(getResources().getString(R.string.ad_time_tip), ad.mTime));

        mHandler.removeMessages(REFRESH_TIME);
        mHandler.sendEmptyMessageDelayed(REFRESH_TIME, REFRESH_SPACE);

        mImageView.setOnClickListener(new ImageClickListener());

        mHandler.removeMessages(STOP_AD_SHOW);
        mHandler.sendEmptyMessageDelayed(STOP_AD_SHOW, ad.mTime * DEFAULT_TIME_BASE);
    }

    private void adDataRecord() {
        // 上报大数据
        SciflyStatistics.getInstance(this).recordEvent(AdvertisingActivity.this, "ADS", "url", mCurrenAd.mImageUrl);
        // 上报服务端
        new Thread(new Runnable() {

            @Override
            public void run() {
                String json = getRequestString();
                Log.d(TAG, "request json=" + json);
                String response = getJsonString(json);
                Log.d(TAG, "response json=" + response);
            }
        }).start();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    return true;
                default:
                    break;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    private boolean isNetAvailable() {
        ConnectivityManager manager = (ConnectivityManager) getSystemService(CONNECTIVITY_SERVICE);
        if (manager == null) {
            return false;
        }
        NetworkInfo netInfo = manager.getActiveNetworkInfo();
        if (netInfo != null && netInfo.isConnected() && netInfo.isAvailable()) {
            return true;
        }
        return false;
    }

    private String getRequestString() {
        WindowManager windowManager = (WindowManager) getSystemService(Context.WINDOW_SERVICE);
        DisplayMetrics metrics = new DisplayMetrics();
        windowManager.getDefaultDisplay().getMetrics(metrics);

        String devMac = Device.getHardwareAddress(getApplicationContext());
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

        try {
            JSONObject json = new JSONObject();
            json.put("ifid", "AppAdsIdea");
            json.put("pkgName", mPackageName);
            json.put("id", mCurrenAd.mAdId);
            json.put("plaCode", Build.DISPLAY.split(" ")[0]);
            json.put("plaVer", Build.VERSION.INCREMENTAL.toString());
            json.put("devMac", devMac);
            json.put("ratx", "" + (int) (metrics.widthPixels * metrics.density));
            json.put("raty", "" + (int) (metrics.heightPixels * metrics.density));
            return json.toString();
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return null;
    }

    private String getJsonString(String json) {
        CpeManager manager = CpeManager.getInstance();
        HttpURLConnection conn = null;
        Writer writer = null;
        BufferedReader reader = null;
        StringBuffer buffer = new StringBuffer();
        try {
            URL url = new URL(mADManager.getServiceUrl());
            conn = (HttpURLConnection) url.openConnection();
            conn.setRequestProperty("Content-Type", "text/json; charset=UTF-8");
            conn.setRequestProperty("Ttag", manager.getProductClass());
            conn.setRequestProperty("Tcip", manager.getBBNumber());
            conn.setRequestMethod("POST");
            conn.setConnectTimeout(20 * 1000);
            conn.setReadTimeout(20 * 1000);
            conn.setDoOutput(true);

            conn.connect();

            writer = new OutputStreamWriter(conn.getOutputStream(), "UTF-8");
            writer.write(json);
            writer.flush();

            reader = new BufferedReader(new InputStreamReader(conn.getInputStream(), "UTF-8"));
            String line = "";
            while ((line = reader.readLine()) != null) {
                buffer.append(line);
                buffer.append("\r\n");
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (writer != null) {
                    writer.close();
                }
                if (reader != null) {
                    reader.close();
                }
                if (conn != null) {
                    conn.disconnect();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        return buffer.toString();
    }
}
