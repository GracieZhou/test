/**
 * 
 */

package com.heran.launcher.business;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;

import org.apache.http.HttpEntity;
import org.apache.http.HttpResponse;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.androidquery.AQuery;
import com.heran.launcher.LauncherApplication;
import com.heran.launcher.util.DownloadAD;
import com.heran.launcher.util.HeranVer2;
import com.heran.launcher.util.Utils;

import android.app.LauncherActivity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

/*
 * projectName： EosLauncher
 * moduleName： NetworkReceiver.java
 * @author laird.li
 * @version 1.0.0
 * @time  2016-4-11 下午4:06:11
 * @Copyright © 2013 Eos Inc.
 */

public class NetworkReceiver extends BroadcastReceiver {

    private final String TAG = "NR";

    private final Object mLock = new Object();

    private Context mContext;

    private VIPAction mVIPAction;

    private static final int DELAY_CHECK = 1;

    private static final int DELAY_CHECK_SUCCESS = 2;

    private static final int GET_VIP = 3;
    
    private static final int DOWNLOADAD =  4;
    
    private static final int UPDATE = 5;
    
    private AQuery aq;
    public NetworkReceiver(Context context) {
        this.mContext=context;
    }
    

    private final Runnable mRunnable = new Runnable() {

        @Override
        public void run() {
            synchronized (mLock) {
                if (Utils.isNetConnected(mContext)) {
                    Utils.isNetworkState = true;
                    mHandler.sendEmptyMessage(DELAY_CHECK_SUCCESS);
                    mHandler.sendEmptyMessageDelayed(DOWNLOADAD, 4*1000);
                    mHandler.sendEmptyMessageDelayed(UPDATE, 10*1000);
                } else {
                    Utils.isNetworkState = false;
                }
                Log.v(TAG, "isNetworkState = " + Utils.isNetworkState);
            }
        }
    };

    private final Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {

                case DELAY_CHECK:
                    Log.i(TAG, "delay check network");
                    startCheckNetwork();
                    break;
                case DELAY_CHECK_SUCCESS:
                    sendEmptyMessage(GET_VIP);
                    break;
                case GET_VIP:
                    if (mVIPAction == null) {
                        mVIPAction = new VIPAction(mContext, mHandler);
                    }
                    mVIPAction.getVIPData();
                    break;
                case DOWNLOADAD :
                	getSwitch();
                break;
                case UPDATE:
//                    LauncherActivity launcher = new LauncherActivity();
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
//        this.mContext = context;
        Log.v(TAG, "action = " + action);
        if ("android.net.conn.CONNECTIVITY_CHANGE".equals(action) && Utils.isNetConnected(mContext)) {
            mHandler.removeMessages(DELAY_CHECK);
            mHandler.sendEmptyMessageDelayed(DELAY_CHECK, 2000);
            Log.d(TAG, "android.net.conn.CONNECTIVITY_CHANGE");
        }
    }

    protected void startCheckNetwork() {
        LauncherApplication.getInstance().addNetworkTask(mRunnable);
    }
    
    
    public  void getSwitch() {
        new Thread() { 

            JSONObject data;
            boolean switcPlayAD = true;
            boolean switchDel = false;
            @Override  
            public void run() {  
                super.run();  
                HttpParams parameter = new BasicHttpParams();
                int connectionTO = 10000;
                int socketTO = 10000;
                HttpConnectionParams.setConnectionTimeout(parameter,
                        connectionTO);
                HttpConnectionParams.setSoTimeout(parameter, socketTO);
                HttpClient httpclient = new DefaultHttpClient(parameter);
                String url = "http://www.jowinwin.com/tedswitch/ws.php?mac=" + com.heran.launcher.LauncherActivity.getMac();
//                String url = "http://www.jowinwin.com/tedswitch/aw.php?mac=" + MacAddr;  // 測試該 api 不存在              
                Log.d(TAG, "url =" + url.toString());
                HttpGet httpGet = new HttpGet(url);
                Log.d(TAG, httpGet.getURI().toString());
                HttpResponse response;
                try{
                    response = httpclient.execute(httpGet);
                    HttpEntity entity = response.getEntity();
                    InputStream is = entity.getContent();
                    BufferedReader reader = new BufferedReader(
                            new InputStreamReader(is));
                    String line = "";
                    StringBuilder sb = new StringBuilder();
                    while ((line = reader.readLine()) != null) {
                        sb.append(line);
                    }
                    is.close();
                    Log.i(TAG, sb.toString());
                    data = new JSONObject(sb.toString());
                    JSONArray arrayObj = data.getJSONArray("content"); 
          
                    int switcPlayAD_int = arrayObj.getJSONObject(6).getInt("vw");
                    int switchDel_int = arrayObj.getJSONObject(8).getInt("vw");
                    
                    if(switcPlayAD_int==1){
                    	switcPlayAD = true;
                    }else{
                    	switcPlayAD = false;
                    }
                    
                    if(switchDel_int == 1){
                    	switchDel = true;
                    }else{
                    	switchDel = false;
                    }
                    
                    Log.d("DownloadAD", "switcPlayAD : "+switcPlayAD);
                    Log.d("DownloadAD", "switchDel : "+switchDel);
                    
                } catch (ClientProtocolException e) {
                    Log.d(TAG, "ClientProtocolException ");
                } catch (IOException e) {
                    Log.d(TAG, "IOException e");
                } catch (JSONException e) {
                    Log.d(TAG, "JSONException e ");
                } catch (NumberFormatException e) {
                    Log.d(TAG, "NumberFormatException e = ");
                } finally{ // 讀不到資料 , 來個預設值
//                	boolean switchDel = HistoryRec.BFS.get(8).functionStatus;
                	
                	if(switcPlayAD){
                		DownloadAD down = new DownloadAD(switchDel);
                    	down.execute();
                	}
                	
                	
//                    if(HistoryRec.BFS == null) {
//                    	HistoryRec.BFS = new ArrayList<BackendFunctionSwitch>(6);
//                    	DownloadAD down = new DownloadAD(switchDel);
//                    	down.execute();
//                    }else{
//                    	if(HistoryRec.BFS.get(6).functionStatus){
//                    		DownloadAD down = new DownloadAD(switchDel);
//                        	down.execute();
//                    	}
//                    }
//
//                    for (int i = 0; i < 6; i++) {
//                        BackendFunctionSwitch bfs = new BackendFunctionSwitch("",false); 
//                        HistoryRec.BFS.add(bfs);
//                    }  
                  
                    
                }
            }  
        }.start();  
    }
    

}
