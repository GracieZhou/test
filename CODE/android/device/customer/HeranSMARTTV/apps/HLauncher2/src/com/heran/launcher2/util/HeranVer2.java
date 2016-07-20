/**
 * 分眾升級
 * */

package com.heran.launcher2.util;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONObject;

import com.androidquery.callback.AjaxCallback;
import com.androidquery.callback.AjaxStatus;
import com.heran.launcher2.HomeActivity;
import com.heran.launcher2.R;
import com.heran.launcher2.widget.CustomAlertDialog;

import android.app.AlertDialog;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Environment;
import android.os.Handler;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationCompat.Builder;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

public class HeranVer2 extends AjaxCallback<JSONObject> {
    private final Builder mBuilder;

    private final NotificationManager mNotifyManager;

    private final String TAG = "HeranVer2";

    String APK_PATH = "";

    HomeActivity homeActivity;

    private Context mContext;

    public ProgressDialog Waiting;

    public HeranVer2(HomeActivity homeActivity) {
        this.homeActivity = homeActivity;
        mContext = homeActivity;
        // 更改packpage
      //新的自升級連結，有分眾功能，H828 統一包名 com.google.tv.eoslauncher32E2
      		String gw = "http://jowinwin.com/admin/index.php?r=apkOta/apkota_json&"
      				+ "pack_name=com.google.tv.eoslauncher32E3&version="
      				+ homeActivity.getString(R.string.version)
      				+ "&mac="
      				+ HomeActivity.myMac
      				+"&launcher_type="
      				+"0";

        Log.d(TAG, "sarah 222::: distinguish update");
        Log.d(TAG, "joey EOS_version = " + gw);
        url(gw).type(JSONObject.class);

        String appName = homeActivity.getString(homeActivity.getApplicationInfo().labelRes);
        // String appName = "new";
        int icon = homeActivity.getApplicationInfo().icon;
        mNotifyManager = (NotificationManager) homeActivity.getSystemService(Context.NOTIFICATION_SERVICE);
        mBuilder = new NotificationCompat.Builder(homeActivity);
        mBuilder.setContentTitle(appName).setSmallIcon(icon);

    }

    @Override
    public void callback(String url, JSONObject json, AjaxStatus status) {

        Log.d(TAG, "joey Ajax status error = " + status.getError());
        Log.d(TAG, "joey Ajax status message = " + status.getMessage());
        if (json == null) {
            Log.d(TAG, "VersionTask json == null");

            return;
        }
        try {
            Log.d(TAG, "json != null");

            // String urlDownload =
            // json.getJSONObject("data").getString("file");
            // String packName =
            // json.getJSONObject("data").getString("pack_name");
            String urlDownload = json.getString("file");
            String packName = json.getString("pack_name");
            Log.d(TAG, "urlDownload = " + urlDownload);
            Log.d(TAG, "packName = " + packName);

            if (packName != null) {
                APK_PATH = json.getString("file");
                final CustomAlertDialog custom = new CustomAlertDialog(mContext);
                		custom.setTitle(R.string.updata);
                		custom.setMessage(R.string.message);
                		custom.setPositiveButton(R.string.sure, new OnClickListener() {
							
							@Override
							public void onClick(View arg0) {
								  custom.dismiss();
								  progressDialog(homeActivity);
						            Log.d(TAG, "user click update !");
						            // mProgressDialog.show();
						            
						        }														
						});
                		
                		custom.setNegativeButton(R.string.cancle, new OnClickListener() {
							
							@Override
							public void onClick(View arg0) {
								custom.dismiss();								
							}
						});
            } else {
                Log.d(TAG, "no new version and startkok");
            }
        } catch (Exception e) {
        }
    }  

    private void updateProgress(int progress) {
        // "正在下载:" + progress + "%"
        Log.v(TAG, "joey updateProgress : " + progress);
        mBuilder.setContentText(this.homeActivity.getString(R.string.download_progress, progress)).setProgress(100,
                progress, false);
        // setContentInent如果不设置在4.0+上没有问题，在4.0以下会报异常
        PendingIntent pendingintent = PendingIntent.getActivity(this.homeActivity, 0, new Intent(),
                PendingIntent.FLAG_CANCEL_CURRENT);
        mBuilder.setContentIntent(pendingintent);
        mNotifyManager.notify(0, mBuilder.build());
        Waiting.incrementProgressBy(1);
    }

    private void progressDialog(final HomeActivity homeActivity) {
        Waiting = new ProgressDialog(homeActivity, R.style.ProgressDialog);
        Waiting.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);
        Waiting.setCanceledOnTouchOutside(false);

        Waiting.setMessage("下載更新中...");
        Waiting.show();
        
        final Handler handler = new Handler();
        new Thread(new Runnable() {
            @Override
            public void run() {
                FileOutputStream fos = null;
                InputStream is = null;
                try {
                    // joey new
                    URL url = new URL(APK_PATH);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    Log.v(TAG, "joey urlConnection : " + urlConnection);

                    Log.v(TAG, "joey update -1");
                    HttpParams parameter = new BasicHttpParams();
                    int connectionTO = 10000;
                    int socketTO = 10000;
                    HttpConnectionParams.setConnectionTimeout(parameter, connectionTO);
                    HttpConnectionParams.setSoTimeout(parameter, socketTO);
                    HttpClient httpclient = new DefaultHttpClient(parameter);
                    HttpGet httpGet = new HttpGet(APK_PATH);

                    Log.v(TAG, "joey update -2");
                    Log.d(TAG, "downloading apk from " + httpGet.getURI().toString());
                    HttpResponse response = httpclient.execute(httpGet);
                    if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                        String PATH = Environment.getExternalStorageDirectory() + "/Download/";
                        File file = new File(PATH);
                        file.mkdirs();
                        // 更改apk的名稱
                        File outputFile = new File(file, "out_H638.apk");
                        Log.v(TAG, "joey outputFile :" + outputFile);
                        fos = new FileOutputStream(outputFile);
                        is = response.getEntity().getContent();
                        // 記憶體分配
                        byte[] buffer = new byte[1024];
                        int length = 0;
                        // joey new
                        long bytesum = 0;
                        int oldProgress = 0;
                        long bytetotal = urlConnection.getContentLength();

                        while ((length = is.read(buffer)) != -1) {
                            // joey new
                            bytesum += length;

                            fos.write(buffer, 0, length);
                            // joey new
                            int progress = (int) (bytesum * 100L / bytetotal);
                            Log.v(TAG, "joey bytetotal : " + bytetotal);
                            // 如果进度与之前进度相等，则不更新，如果更新太频繁，否则会造成界面卡顿
                            if (progress != oldProgress) {
                                updateProgress(progress);
                            }
                            oldProgress = progress;
                        }
                        fos.close();
                        is.close();
                        // 下載完成
                        Waiting.dismiss();
                        // 下载完成
                        mBuilder.setContentText(homeActivity.getString(R.string.download_success)).setProgress(0, 0,
                                false);

                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                openFile(new File(
                                        Environment.getExternalStorageDirectory() + "/Download/" + "out_H638.apk"));
                            }
                        });
                    } else {
                        handler.post(new Runnable() {
                            @Override
                            public void run() {
                                UIUtil.showToast(mContext,
                                        homeActivity.getResources().getString(R.string.update_failed));
                            }
                        });
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            UIUtil.showToast(mContext,
                                    homeActivity.getResources().getString(R.string.update_failed));
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
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    }
                }
            }
        }).start();
    }

    // 打開APK
    private void openFile(File file) {
        Log.e("OpenFile", file.getName());
        Intent intent = new Intent();
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        intent.setAction(android.content.Intent.ACTION_VIEW);
        intent.setDataAndType(Uri.fromFile(file), "application/vnd.android.package-archive");
        homeActivity.startActivity(intent);
    }

}
