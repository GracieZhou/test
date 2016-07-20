package com.heran.launcher2.util;

import java.io.InputStream;
import java.net.HttpURLConnection;
import java.net.URL;

import android.util.Log;
public class HttpUtils {
    private final static String TAG = "HttpUtils";
    public HttpUtils() {
    }
    public static InputStream getXML(String path,String str) {
        try {
            URL url = new URL(path);
            if(url != null)
            {
                HttpURLConnection connection = (HttpURLConnection)url.openConnection();
                connection.setDoInput(true);
                connection.setConnectTimeout(3000);
                connection.setRequestMethod("GET");
                int requesetCode = connection.getResponseCode();
                if(requesetCode != 200){
                	Log.d(TAG, "HTTP Request is error");  
                	if(str.equals("news")){
                		Constants.newsRequesetCodeIsOk = false;
                	}
                	if(str.equals("weather")){
                		Constants.weatherRequesetCodeIsOk = false;
                	}
                	
                }
                else
                {
                    //如果執行成功,返回HTTP資料流
                    Log.d(TAG, "HTTP RequestSuccess");
                    if(str.equals("news")){
                    	Constants.newsRequesetCodeIsOk = true;
                    }
                    
                    return connection.getInputStream();
                    
                }
            }
        } catch (Exception e) {
            // TODO: handle exception
            Log.d(TAG, "HTTP Request e = " + e.getMessage().toString());
        }        
        Log.d(TAG, "HTTP Request return null");        
        return null;
    }    
    
}
