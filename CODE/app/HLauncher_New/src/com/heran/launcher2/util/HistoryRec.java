
package com.heran.launcher2.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.sql.Date;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.List;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.ResponseHandler;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.entity.mime.HttpMultipartMode;
import org.apache.http.entity.mime.MultipartEntity;
import org.apache.http.entity.mime.content.FileBody;
import org.apache.http.entity.mime.content.StringBody;
import org.apache.http.impl.client.BasicResponseHandler;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.message.BasicNameValuePair;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.protocol.HTTP;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.heran.launcher2.others.BackendFunctionSwitch;
import com.mstar.android.tvapi.common.TvManager;
import com.mstar.android.tvapi.common.exception.TvCommonException;

import android.os.Environment;
import android.util.Log;

/*
 * projectName： EosLauncher
 * moduleName： HistoryRec.java
 * @author Jason.Pan
 * @version 1.0.0
 * @time  2014-11-20 13:40:00
 * @Copyright © 2014 Heran Inc.
 */

public class HistoryRec {

    private static final String TAG = "HistoryRec";

    public static File file;

    public static volatile boolean isRecFileExist = false;

    public static volatile boolean isUploaded = false;

    public static ArrayList<BackendFunctionSwitch> BFS;

    private static String MacAddr = "";

    private static Boolean threadRun = true;

    private static int fetch_time = 21600000; // 此變數應該讀取伺服器來決定多久傳送一次紀錄檔,如果都沒讀到則預設6小時傳送一次

    public static String[] block = {
            "1", /* 首頁 */
            "2", /* App 頁 */
            "3", /* 多媒體頁 */
            "4", /* 商店 */
            "5", /* 潘朵拉 */
            "6", /* 開機廣告 */
            "7", /* 開/關機 */
            "8", /* 新聞區 */
            "9", /* 氣象區 */
            "10" /* 資訊列 */
    };

    // Home Fragment
    public static String[] block1Action = {
            "10", /* atv 轉台 */
            "11", /* dtv 轉台 */
            "12", /* unknow 轉台 */
            "13", /* 廣告點擊 */
            "14", /* 點擊全螢幕 */
            "15" /* 介面選擇 */
    };

    // App Fragment
    public static String[] block2Action = {
            "20", /* 點擊左上方廣告頁 */
            "21", /* 點擊左中的 五小強 */
            "22", /* 點擊右中的 app */
            "23", /* 點擊 All App 頁面中的 app */
            "24", /* 右上setting */
            "25" /* 介面選擇 */
    };

    // MediaFragment
    public static String[] block3Action = {
            "30", /* 點擊卡拉ok圖示 */
            "31", /* 點擊影片圖示 */
            "32", /* 點擊圖片圖示 */
            "33", /* 點擊音樂圖示 */
            "34" /* 介面選擇 */
    };

    // ShopFragment
    public static String[] block4Action = {
            "40", /* 左邊廣告 */
            "41", /* 右上廣告 */
            "42", /* 右下會員 */
            "43" /* 介面選擇 */
    };

    // PandoraFragment
    public static String[] block5Action = {
            "50", /* 永久關閉 */
            "51", /* 離開 */
            "52", /* 進入 */
            "53", /* 介面選擇 */
    };

    // 開機廣告
    public static String[] block6Action = {};

    // 開/關機時間
    public static String[] block7Action = {
            "70", /* 開機時間 */
            "71"
    }; /* 關機時間 */

    // 新聞區
    public static String[] block8Action = {
            "80", /* 按下右側選項 */
            "81" /* 介面選擇 */
    };

    // 氣象區
    public static String[] block9Action = {
            "90", /* 按下右側選項 */
            "91" /* 介面選擇 */
    };

    // 資訊列
    public static String[] block10Action = {
            "100", /* 進入專頁 */
            "101" /* 現在的頁面 */
    };

    public static Boolean createRecFile() {
        File sdPath;
        String MacAddr1 = "";
        if (Environment.getExternalStorageState().equals(android.os.Environment.MEDIA_MOUNTED)) {
            sdPath = Environment.getExternalStorageDirectory();
        } else {
            Log.d(TAG, "sdcard not mounted");
            return false;
        }
        try {
            MacAddr = TvManager.getInstance().getEnvironment("ethaddr");
        } catch (TvCommonException e) {
            Log.d(TAG, "get mac e = " + e.getMessage().toString());
        }
        MacAddr1 = MacAddr.replace(':', '-');

        String fileName = new StringBuilder().append(MacAddr1).toString();

        String filePath = sdPath + "/" + fileName + ".txt";
        Log.d(TAG, filePath);

        file = new File(filePath);
        try {
            if (!file.exists()) {
                Log.d(TAG, "file not exit");
                file.createNewFile();
            }
        } catch (IOException e) {
            Log.d(TAG, "create file e = " + e.getMessage().toString());
        }
        Log.d(TAG, "create file success");
        return true;
    }

    public static String getCurrentDateTime() {
        String DateTime = null;
        // int year,month,day,hour,min,sec;

        // Calendar c = Calendar.getInstance();
        //
        // year = c.get(Calendar.YEAR);
        // month = c.get(Calendar.MONTH) + 1;
        // day = c.get(Calendar.DAY_OF_MONTH);
        // hour = c.get(Calendar.HOUR_OF_DAY);
        // min = c.get(Calendar.MINUTE);
        // sec = c.get(Calendar.SECOND);

        if (Constants.date.equals("")) {
            SimpleDateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
            Date date = new Date(System.currentTimeMillis());
            DateTime = formatter.format(date);
        } else {
            SimpleDateFormat formatter = new SimpleDateFormat("HH:mm:ss");
            java.util.Date date = new java.util.Date();
            DateTime = Constants.date + " " + formatter.format(date);
        }

        // DateTime = new
        // StringBuilder().append(String.valueOf(year)).append('-').append(String.valueOf(month)).append("-")
        // .append(String.valueOf(day)).append('-').append(String.valueOf(hour))
        // .append(':').append(String.valueOf(min)).append(':').append(String.valueOf(sec)).toString();
        return DateTime;
    }

    public static void writeToFile(String data) {
        BufferedWriter bw = null;
        FileWriter fw = null;
        try {
            if (Utils.isNet && Constants.rec) {
                Log.d("rec", "rec : " + data);
                fw = new FileWriter(file.toString(), true);
                bw = new BufferedWriter(fw);
                bw.write(data + "\r\n");
                // bw.newLine();
                bw.close();
            }
        } catch (Exception e) {
            Log.d(TAG, "write to file e = " + e.getMessage().toString());
        } finally {
            try {
                if (Utils.isNet && Constants.rec) {
                    fw.close();
                    bw.close();
                }
            } catch (Exception e) {
                Log.d(TAG, "writeToFile Exception = " + e.getMessage().toString());
            }
        }
    }

    public static void UploadFiles(final String PathFile) {
        new Thread() {
            @Override
            public void run() {
                super.run();

                // 這邊要加個 while(threadRun)
                while (threadRun) {
                    Log.d(TAG, "UploadFiles");
                    List<NameValuePair> params = new ArrayList<NameValuePair>();
                    params.add(new BasicNameValuePair("file", PathFile));

                    HttpClient client = new DefaultHttpClient();
                    HttpPost post = new HttpPost(
                            "http://www.launcher-log.jowinwin.com/newlauncherLog/webservice/upload.php"); // 上傳SERVER
                                                                                                          // 訪問網頁路徑
                    // HttpPost post = new
                    // HttpPost("http://219.87.154.38/launcherLog/webservice/upload.php");
                    // //上傳SERVER 訪問網頁路徑
                    // HttpPost post = new
                    // HttpPost("http://www.record.jowinwin.com/launcherLog/webservice/upload.php");
                    // //上傳SERVER 訪問網頁路徑
                    // HttpPost post = new
                    // HttpPost("http://www.tads.jowinwin.com/FGRecord/webservice/upload.php");
                    // //上傳SERVER 訪問網頁路徑
                    try {
                        // setup multipart entity
                        MultipartEntity entity = new MultipartEntity(HttpMultipartMode.BROWSER_COMPATIBLE);

                        for (int i = 0; i < params.size(); i++) {
                            // identify param type by Key
                            if (params.get(i).getName().equals("file")) {
                                Log.d(TAG, "==>" + params.get(i).getValue());
                                File f = new File(params.get(i).getValue());
                                FileBody fileBody = new FileBody(f);
                                entity.addPart("image" + i, fileBody); // image這字串
                                                                       // 會根據01.php修改
                                Log.d(TAG, "image");
                            } else {
                                Log.e(TAG, "2");
                                entity.addPart(params.get(i).getName(), new StringBody(params.get(i).getValue()));
                            }
                        }
                        post.setEntity(entity);
                        // create response handler
                        ResponseHandler<String> handler = new BasicResponseHandler();
                        // execute and get response
                        String UploadFilesResponse = new String(client.execute(post, handler).getBytes(), HTTP.UTF_8);
                        Log.d(TAG, "--- response ---" + UploadFilesResponse);
                        String[] data = UploadFilesResponse.split(":|<");
                        Log.d("rec", "UploadFilesResponse : " + UploadFilesResponse);
                        Log.d("rec", "data[0] : " + data[0]);
                        Log.d("rec", "data[1] : " + data[1]);

                        // 如果上傳成功 則要把檔案刪除
                        if (data[0].trim().equals("uploaded")) {
                            Constants.date = data[1];
                            isUploaded = true;
                            Log.d(TAG, "file uploaded");
                            File f = new File(PathFile);
                            Boolean isDeleted = f.delete();
                            if (isDeleted) {
                                Log.d(TAG, "file deleted");
                            } else {
                                Log.d(TAG, "file not deleted");
                            }
                        } else {
                            Constants.date = data[1];
                            isUploaded = false;
                            Log.d(TAG, "file not uploaded");
                        }
                    } catch (Exception e) {
                        Log.e(TAG, "image2" + e.getMessage().toString());
                    }
                    try {
                        Thread.sleep(fetch_time); // sleep 一段時間再繼續傳
                    } catch (InterruptedException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }
                } // while(threadRun)
            }
        }.start();
    }

    public static void GetFetchTime() {
        new Thread() {
            @Override
            public void run() {
                super.run();
                Log.d(TAG, "GetFetchTime");
                HttpClient client = new DefaultHttpClient();
                // HttpPost post = new
                // HttpPost("http://launcher-log.jowinwin.com//webservice/schedule.php");
                HttpPost post = new HttpPost(
                        "http://www.launcher-log.jowinwin.com/launcherLog/webservice/schedule.php");
                try {
                    ResponseHandler<String> handler = new BasicResponseHandler();

                    String GetFetchTimeResponse = new String(client.execute(post, handler).getBytes(), HTTP.UTF_8);
                    Log.d(TAG, "--- response ---" + GetFetchTimeResponse);

                    JSONObject data = new JSONObject(GetFetchTimeResponse.toString());
                    fetch_time = data.getInt("fetch_time");
                    Log.d(TAG, "fetch_time = " + String.valueOf(fetch_time));
                } catch (Exception e) {
                    Log.e(TAG, e.getMessage().toString());
                }
            }
        }.start();
    }

    public static void GetBackendSwitch() {
        Log.d("jjj", "GetBackendSwitch");
        new Thread() {
            JSONObject data;

            @Override
            public void run() {
                super.run();
                HttpParams parameter = new BasicHttpParams();
                int connectionTO = 10000;
                int socketTO = 10000;
                HttpConnectionParams.setConnectionTimeout(parameter, connectionTO);
                HttpConnectionParams.setSoTimeout(parameter, socketTO);
                HttpClient httpclient = new DefaultHttpClient(parameter);
                String url = "http://www.jowinwin.com/tedswitch/ws.php?mac=" + MacAddr;
                // String url = "http://www.jowinwin.com/tedswitch/aw.php?mac="
                // + MacAddr; // 測試該 api 不存在
                Log.d(TAG, "url =" + url.toString());
                HttpGet httpGet = new HttpGet(url);
                Log.d(TAG, httpGet.getURI().toString());
                HttpResponse response;
                try {
                    response = httpclient.execute(httpGet);
                    HttpEntity entity = response.getEntity();
                    InputStream is = entity.getContent();
                    BufferedReader reader = new BufferedReader(new InputStreamReader(is));
                    String line = "";
                    StringBuilder sb = new StringBuilder();
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }
                    is.close();
                    Log.i(TAG, sb.toString());
                    data = new JSONObject(sb.toString());
                    JSONArray arrayObj = data.getJSONArray("content");
                    BFS = new ArrayList<BackendFunctionSwitch>();
                    for (int i = 0; i < arrayObj.length(); i++) {
                        JSONObject d = arrayObj.getJSONObject(i);

                        BackendFunctionSwitch bfs = new BackendFunctionSwitch(d.getString("name"),
                                (d.getInt("vw") == 1 ? true : false));
                        BFS.add(bfs);
                    }
                    if (HistoryRec.BFS.get(3).functionStatus) {
                        Constants.rec = true;
                        Log.d("rec", "Constants.rec :" + Constants.rec);
                        Log.d(TAG, "excute thread of UploadFiles");
                        HistoryRec.UploadFiles(HistoryRec.file.toString()); // always
                                                                            // start
                    }

                } catch (ClientProtocolException e) {
                    Log.d(TAG, "ClientProtocolException e = " + e);
                } catch (IOException e) {
                    Log.d(TAG, "IOException e = " + e);
                } catch (JSONException e) {
                    Log.d(TAG, "JSONException e = " + e);
                } catch (NumberFormatException e) {
                    Log.d(TAG, "NumberFormatException e = " + e);
                } finally { // 讀不到資料 , 來個預設值
                    if (BFS == null) {
                        BFS = new ArrayList<BackendFunctionSwitch>(6);
                    }
                    for (int i = 0; i < 6; i++) {
                        BackendFunctionSwitch bfs = new BackendFunctionSwitch("", false);
                        BFS.add(bfs);
                    }

                }
            }
        }.start();
    }

}
