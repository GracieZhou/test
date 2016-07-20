/**
 * 
 */

package com.heran.launcher2;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONObject;

import com.androidquery.AQuery;
import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.heran.launcher2.advert.AdAction;
import com.heran.launcher2.apps.AppAction;
import com.heran.launcher2.message.MessageAction;
import com.heran.launcher2.news.NewsAction;
import com.heran.launcher2.util.Constants;
import com.heran.launcher2.util.GoogleAnalyticsUtil;
import com.heran.launcher2.util.HeranVer2;
import com.heran.launcher2.util.HistoryRec;
import com.heran.launcher2.util.Utils;
import com.heran.launcher2.weather.WeatherAction;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Window;
import android.widget.Toast;

/*
 * projectName： EosLauncher
 * moduleName： NetworkReceiver.java
 * @author chadm.xiang
 * @version 1.0.0
 * @time  2014-2-8 下午4:06:11
 * @Copyright © 2013 Eos Inc.
 */

public class NetworkReceiver extends BroadcastReceiver {

    private final String TAG = "NetworkReceiver";

    private final HomeActivity mContext;

    // get ads from web pages
    private final AdAction mAdction;

    private final MessageAction mAdtxtction;

    private final AppAction mAppAction;

    private final NewsAction mNewsAction;

    private final WeatherAction mWeatherAction;

    private final Object mLock = new Object();

    private boolean mIsFirstReceiveNetwork = true;

    private final int DELAY_CHECK = 0x201;

    private final int UPDATE_VERSION = 1;

    private final int UPDATE_APP_INFO = 3;

    private final int UPDATE_NEW_INFO = 4;

    private final int UPDATE_WEATHER_INFO = 5;

    private final int UploadFiles = 6;

    private final int UPDATE_AD_TEXT = 7;

    private final int UpdateLauncher = 8;

    // the flag whether to show ThemeDisplayAct the first time
    public static volatile boolean mIsDelay = true;

    private Boolean mIsFirstPowerOn = true;

    private AQuery aq;

    private ProgressDialog mProgressDialog;

    private String mApkPath;

    private final Runnable mRunnable = new Runnable() {

        @Override
        public void run() {
            synchronized (mLock) {
                if (Utils.isNetworkConnected(mContext)) {
                    Utils.isNetworkState = true;
                    mHandler.sendEmptyMessage(Constants.NETWORK_CHANGE_UPDATE);

                } else {
                    Utils.isNetworkState = false;
                }
                Log.v("NetworkReceiver", "isNetworkState = " + Utils.isNetworkState);
            }
        }
    };

    private final Runnable mUpDateApkRunnable = new Runnable() {

        @Override
        public void run() {

            FileOutputStream fos = null;
            InputStream is = null;
            try {
                HttpParams parameter = new BasicHttpParams();
                int connectionTO = 10000;
                int socketTO = 10000;
                HttpConnectionParams.setConnectionTimeout(parameter, connectionTO);
                HttpConnectionParams.setSoTimeout(parameter, socketTO);
                HttpClient httpclient = new DefaultHttpClient(parameter);
                HttpGet httpGet = new HttpGet(mApkPath);
                Log.d(TAG, "downloading apk from " + httpGet.getURI().toString());
                HttpResponse response = httpclient.execute(httpGet);
                Log.d(TAG, "File size = " + response.getEntity().getContentLength()); // get
                if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                    String PATH = Environment.getExternalStorageDirectory() + "/Download/";
                    File file = new File(PATH);
                    file.mkdirs();
                    File outputFile = new File(file, "EosLauncher.apk");
                    fos = new FileOutputStream(outputFile);
                    is = response.getEntity().getContent();
                    byte[] buffer = new byte[1024];
                    int length = 0;
                    while ((length = is.read(buffer)) != -1) {
                        fos.write(buffer, 0, length);
                    }
                    mContext.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            mProgressDialog.dismiss();
                            Intent intent = new Intent(Intent.ACTION_VIEW);
                            intent.setDataAndType(Uri.fromFile(new File(
                                    Environment.getExternalStorageDirectory() + "/Download/" + "EosLauncher.apk")),
                                    "application/vnd.android.package-archive");
                            mContext.startActivity(intent);
                        }
                    });
                } else {
                    mContext.runOnUiThread(new Runnable() {

                        @Override
                        public void run() {
                            mProgressDialog.dismiss();
                            Toast.makeText(mContext, "更新失敗", Toast.LENGTH_SHORT).show();
                        }
                    });
                }
            } catch (Exception e) {
                e.printStackTrace();
                mContext.runOnUiThread(new Runnable() {

                    @Override
                    public void run() {
                        mProgressDialog.dismiss();
                        Toast.makeText(mContext, "更新失敗", Toast.LENGTH_SHORT).show();
                    }
                });
            } finally {
                if (fos != null) {
                    try {
                        fos.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
                if (is != null) {
                    try {
                        is.close();
                        is = null;
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        }
    };

    public NetworkReceiver(HomeActivity context, Handler handler, AdAction adAction, MessageAction adTextAction,
            AppAction appAction, NewsAction newsAction, WeatherAction weatherAction) {
        this.mContext = context;
        this.mAdction = adAction;
        this.mAdtxtction = adTextAction;
        this.mAppAction = appAction;
        this.mNewsAction = newsAction;
        this.mWeatherAction = weatherAction;
    }

    private final Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case Constants.NETWORK_CHANGE_UPDATE:
                    Log.d(TAG, "NETWORK_CHANGE_UPDATE");
                    mAdction.parsePgmJson();
                    mHandler.sendEmptyMessage(UPDATE_AD_TEXT);
                    mHandler.sendEmptyMessage(UPDATE_NEW_INFO);
                    mHandler.sendEmptyMessage(UPDATE_WEATHER_INFO);
                    mHandler.sendEmptyMessageDelayed(UPDATE_VERSION, 4 * 1000);
                    mHandler.sendEmptyMessageDelayed(UpdateLauncher, 10 * 1000);
                    mHandler.sendEmptyMessageDelayed(UPDATE_APP_INFO, 60 * 1000);

                    if (mIsFirstPowerOn) {
                        Log.d(TAG, "first_PowerOn");
                        mIsFirstPowerOn = false;
                        mHandler.sendEmptyMessageDelayed(UploadFiles, 30 * 1000);
                    }

                    break;

                case UPDATE_VERSION:
                    mContext.getVersionInfo();
                    break;

                case DELAY_CHECK:
                    Log.i(TAG, "delay check network");
                    mIsDelay = false;
                    startCheckNetwork();
                    break;
                case UPDATE_APP_INFO:
                    mAppAction.parsePgmJson();
                    break;
                case UPDATE_NEW_INFO:
                    Log.d(TAG, "UPDATE_NEW_INFO");
                    mNewsAction.parsePgmJson();
                    mHandler.removeMessages(UPDATE_NEW_INFO);
                    mHandler.sendEmptyMessageDelayed(UPDATE_NEW_INFO, 60 * 60 * 1000);
                    break;
                case UPDATE_WEATHER_INFO:
                    Log.d(TAG, "WEATHER update");
                    mWeatherAction.getWeatherTodayData();
                    mWeatherAction.getWeatherWeekData();
                    mWeatherAction.getWeatherCity();
                    mWeatherAction.getWeatherLife();
                    mHandler.removeMessages(UPDATE_WEATHER_INFO);
                    mHandler.sendEmptyMessageDelayed(UPDATE_NEW_INFO, 3600 * 1000);
                    break;
                case UploadFiles:
                    Log.d(TAG, "handler UploadFiles");
                    HistoryRec.GetFetchTime();
                    try {
                        String recData = HistoryRec.block[6] + ',' + HistoryRec.block7Action[0] + ',' + "" + ',' + ""
                                + ',' + "" + ',' + android.provider.Settings.System
                                        .getString(mContext.getContentResolver(), "openTvRecord");
                        HistoryRec.writeToFile(recData);
                        recData = "";
                        GoogleAnalyticsUtil.sendEvent(0);
                        //
                        recData = HistoryRec.block[6] + ',' + HistoryRec.block7Action[1] + ',' + "" + ',' + "" + ','
                                + "" + ',' + android.provider.Settings.System.getString(mContext.getContentResolver(),
                                        "closeTvRecord");
                        HistoryRec.writeToFile(recData);
                        recData = "";
                        GoogleAnalyticsUtil.sendEvent(1);
                    } catch (Exception e) {
                        Log.d(TAG, "writeToFile error :" + e.toString());
                    }
                    HistoryRec.GetBackendSwitch();
                    break;
                case UPDATE_AD_TEXT:
                    mAdtxtction.parsePgmJson();
                    mHandler.removeMessages(UPDATE_AD_TEXT);
                    mHandler.sendEmptyMessageDelayed(UPDATE_AD_TEXT, 45 * 1000);
                    break;
                case UpdateLauncher:
                    // 自升級 by sarah
                    aq = new AQuery(mContext);
                    aq.ajax(new HeranVer2(mContext));
                    break;
                default:
                    break;
            }
        }

    };

    @Override
    public void onReceive(Context context, Intent intent) {
        String action = intent.getAction();
        Log.v(TAG, "action = " + action);
        if ("android.net.conn.CONNECTIVITY_CHANGE".equals(action)) {
            if (mIsFirstReceiveNetwork) {
                // -------- add by Jason
                HistoryRec.GetFetchTime();
                HistoryRec.UploadFiles(HistoryRec.file.toString()); // always
                aq = new AQuery(mContext);
                aq.ajax(new VersionTask());
                mProgressDialog = new ProgressDialog(mContext);
                mProgressDialog.requestWindowFeature(Window.FEATURE_NO_TITLE);
                mProgressDialog.setCancelable(false);
                mProgressDialog.setMessage("更新檔下載中,請稍後...");
                mHandler.removeMessages(DELAY_CHECK);
                mHandler.sendEmptyMessageDelayed(DELAY_CHECK, 3000);
                mIsFirstReceiveNetwork = false;
            } else {
                mHandler.removeMessages(DELAY_CHECK);
                mHandler.sendEmptyMessageDelayed(DELAY_CHECK, 2000);
            }
            Log.d(TAG, "android.net.conn.CONNECTIVITY_CHANGE");
        }
    }

    private void startCheckNetwork() {
        HomeApplication.getInstance().addNetworkTask(mRunnable);
    }

    private class VersionTask extends AjaxCallback<JSONObject> {
        public VersionTask() {
            String gw = "http://219.87.154.38/tvad2/webservice/kok_update.php?"
                    + "pack_name=com.google.tv.eoslauncher&version=" + mContext.getString(R.string.version);
            Log.d(TAG, "gw = " + gw);
            Log.d(TAG, "1");
            url(gw).type(JSONObject.class);
            Log.d(TAG, "2");
        }

        @Override
        public void callback(String url, JSONObject json, AjaxStatus status) {
            Log.d(TAG, "Ajax status error = " + status.getError());
            Log.d(TAG, "Ajax status message = " + status.getMessage());
            if (json == null) {
                Log.d(TAG, "VersionTask json == null");
                return;
            }
            try {
                Log.d(TAG, "json != null");
                int statusCode = Integer.valueOf(json.getString("status"));
                Log.e(TAG, "" + statusCode);

                final String urlDownload = json.getJSONObject("data").getString("url");
                Log.d(TAG, "urlDownload = " + urlDownload);

                Log.e(TAG, urlDownload);
                if (statusCode == 0) {
                    mApkPath = json.getJSONObject("data").getString("url");
                    new AlertDialog.Builder(mContext).setTitle(R.string.updata).setMessage(R.string.message)
                            .setPositiveButton(R.string.sure, update).setNegativeButton(R.string.cancle, startkok)
                            .setCancelable(false).show();
                } else {
                    Log.d(TAG, "no new version and startkok");
                }
            } catch (Exception e) {
            }
        }
    }

    private final DialogInterface.OnClickListener update = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            Log.d(TAG, "user click update !");
            mProgressDialog.show();
            HomeApplication.getInstance().addNetworkTask(mUpDateApkRunnable);
        }
    };

    private final DialogInterface.OnClickListener startkok = new DialogInterface.OnClickListener() {
        @Override
        public void onClick(DialogInterface dialog, int which) {
            Log.d(TAG, "user cancel update and startkok");
        }
    };

}
