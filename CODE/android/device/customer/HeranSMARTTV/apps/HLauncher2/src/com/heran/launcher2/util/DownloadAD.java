/*
 * 20160622
 * 重複下載機制OK (判斷path和lastModified)
 * 
 */
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

import com.heran.launcher2.HomeActivity;
import com.mstar.android.tvapi.common.TvManager;
import com.mstar.android.tvapi.common.exception.TvCommonException;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
import android.provider.Settings;
import android.util.Log;
import net.extreamax.videoad.ExtreamaxVideoAdService;
import net.extreamax.videoad.bean.VideoAdBean;

public class DownloadAD extends AsyncTask<Void, Void, Void> {

    int back = 0;

    Activity Act;

    VideoAdBean bean;

    String TAG = "DownloadAD";

    String AD_Url;

    String mac;

    boolean deleteFile_swich =false;
    
    boolean file_format = true;
    
    private int adSwitch1;
    
    String videoName = "video.ts";
    
    private HomeActivity mContext;
    
    public DownloadAD(boolean deleteFile_swich, HomeActivity mContext){
    	this.deleteFile_swich = deleteFile_swich;
    	this.mContext = mContext;
    }
    
    @Override
    protected Void doInBackground(Void... params) {
        try {
            mac = TvManager.getInstance().getEnvironment("ethaddr");
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        Log.v(TAG, "*** new Download method3 *** // mac:" + mac.toString());
        
        adSwitch1=Settings.System.getInt(mContext.getContentResolver(), "adSwitch1", 1);
        Log.v(TAG, "Download adSwitch1 :" + adSwitch1);
        if(adSwitch1==1){
        	videoName="video.ts";
        }else{
        	videoName="video_hide.ts";
        }
        
        return_ADLog();
        
        return null;
    }
    
    public void return_ADLog() {
    	String beanLast = Settings.System.getString(mContext.getContentResolver(), "AD_bean");       
        Log.v(TAG, "return ADLog // beanLast:" + beanLast);
        
        if (beanLast != null) {
        	// 廣告播放回報，「影片撥放長度」暫時固定10秒
        	int ok = ExtreamaxVideoAdService.getInstance().sendVideoAdLog(beanLast, 10*1000);
        	Log.v(TAG, "bean.getHash()1:" + beanLast.toString());
        	Log.v(TAG, "bean.getHash()_status:" + ok);
        	
        	if(ok != 200){
        		Settings.System.putString(mContext.getContentResolver(), "AD_bean", null);
        	}
        }
        Get_AD();
    }

    public void Get_AD() {
    	try{
    		bean = ExtreamaxVideoAdService.getInstance().getVideoAd("a2aea910b97ab0a66c196b0581610e5e", mac);
//          bean = ExtreamaxVideoAdService.getInstance().getVideoAd("a2aea910b97ab0a66c196b0581610e5e123456", mac);  //測試用，錯誤的
    	}catch(Exception e){
    		bean=null;
    	}
        
        if (bean != null) {
            Settings.System.putString(mContext.getContentResolver(), "AD_bean", bean.getHash());
            if(bean.getHash() != null){
               Log.v(TAG, "bean.getHash()2:" + bean.getHash().toString());
            }
                        
            AD_Url = bean.getUrl();
        	
    		if(AD_Url!=null){
    			if("".equals(AD_Url)){
    				Log.v(TAG, "Download url:blank");
    			}else{
    				//H828才需要此機制
//    				checkFileFormat(AD_Url);
    				ADVedio(AD_Url);
                	Log.v(TAG, "Download url:" + AD_Url);
    			}
            }else{
            	Log.v(TAG, "Download url:null");
            	
            	//檔案刪除機制
            	Log.v(TAG, "deleteFile_swich:" + deleteFile_swich);
            	String PATH = "/data/video/video.ts";
                File file = new File(PATH);
                if (file.exists()) {
                	if(deleteFile_swich){
                		file.delete();
                		Log.v(TAG, "file delete");
                	}
                }
            }
        }
    }

    @Override
    protected void onPostExecute(Void result) {
        super.onPostExecute(result);
    }

    // 下載影片
    public void ADVedio(final String address) {
        new Thread(new Runnable() {
            @Override
            public void run() {
                FileOutputStream fos = null;
                InputStream is = null;
                try {
                	URL url = new URL(address);
                    HttpURLConnection urlConnection = (HttpURLConnection) url.openConnection();
                    
                    //判斷檔案是否相同，是否需要下載------------------------------------------------------------
                    String path = Settings.System.getString(mContext.getContentResolver(), "AD_path");
                    long lastModified = Settings.System.getLong(mContext.getContentResolver(), "AD_modified", 0);
                    
                    if(lastModified != 0){
                    	Log.d(TAG, "lastModified :" + lastModified);
                    	urlConnection.setIfModifiedSince(lastModified);
                    }
                    
                    urlConnection.connect();
                    Log.d(TAG, "New-lastModified:" + urlConnection.getLastModified());
                    Settings.System.putLong(mContext.getContentResolver(), "AD_modified", urlConnection.getLastModified());
                    Settings.System.putString(mContext.getContentResolver(), "AD_path", url.getPath());
                    urlConnection.disconnect();
                    
                    if(path != null){
                    	Log.d(TAG, "** ppppppppppath :" + path);
                    	Log.d(TAG, "** url.getPath() :" + url.getPath());
                    	if(path.equals(url.getPath())){
                    		Log.d(TAG, "urlConnection.getResponseCode():" + urlConnection.getResponseCode());
                    		if(urlConnection.getResponseCode()!=HttpURLConnection.HTTP_OK){
                    			//檔名相同，lastModified也相同，不重複下載
                    			Log.d(TAG, "same lastModified");
                    			return;
                    		}
                    	}
                    }
                  //--------------------------------------------------------------------------------------
                    HttpParams parameter = new BasicHttpParams();
                    int connectionTO = 10000;
                    int socketTO = 10000;
                    HttpConnectionParams.setConnectionTimeout(parameter, connectionTO);
                    HttpConnectionParams.setSoTimeout(parameter, socketTO);
                    HttpClient httpclient = new DefaultHttpClient(parameter);
                    HttpGet httpGet = new HttpGet(address);

                    Log.d(TAG, "downloading video from:" + httpGet.getURI().toString());
                    HttpResponse response = httpclient.execute(httpGet);
                    Log.d(TAG, "response.getStatusLine().getStatusCode():" + response.getStatusLine().getStatusCode());

                    if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                        String PATH = "/data/video";
                        File file = new File(PATH);
                        file.mkdirs();
                        
                        Log.v(TAG, "PATH:" + PATH);
                        File outputFile = new File(file, videoName);
                        // Log.v(TAG, "outputFile :" + outputFile);
                        fos = new FileOutputStream(outputFile);
                        is = response.getEntity().getContent();
                        
                        byte[] buffer = new byte[1024];
                        int length = 0;
                        long bytesum = 0;
                        int oldProgress = 0;
                        long bytetotal = urlConnection.getContentLength();

                        Log.v(TAG, "file total size:" + bytetotal);
                        
                        while ((length = is.read(buffer)) != -1) {
                            bytesum += length;
                            fos.write(buffer, 0, length);
                            int progress = (int) (bytesum * 100L / bytetotal);
                            // Log.v(TAG, "progress : " + progress);
                            if (progress != oldProgress) {
                            	// updateProgress(progress);
                            }
                            oldProgress = progress;

                        }

                        fos.close();
                        is.close();
                        
                        if(adSwitch1==1){
                        	File file_check = new File("/data/video/video_hide.ts");
                        	if (file_check.exists()) {
                            	file_check.delete();
                        	}
                        }else{
                        	File file_check = new File("/data/video/video.ts");
                        	if (file_check.exists()) {
                            	file_check.delete();
                        	}
                        }
                        
                        // 下載完成
                        Log.v(TAG, "outputFile :" + outputFile);
                    }else{
                    }
                } catch (Exception e) {
                    e.printStackTrace();
                    Log.v(TAG, "catch e:" + e.toString());
//                    String PATH = "/data/video/video.ts";
//                    File file = new File(PATH);
//                    Log.v(TAG, "file download failure, deleteFile_swich:" + deleteFile_swich);
//                    if (file.exists()) {
//                    	if(deleteFile_swich){
//                    		file.delete();
//                    	}
//                    }
                } finally {
                    if (fos != null) {
                        try {
                            fos.close();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                    if (is != null) {
                        try {
                            is.close();
                        } catch (IOException e) {
                            // TODO Auto-generated catch block
                            e.printStackTrace();
                        }
                    }
                    
                    //檔案刪除機制
//                    String PATH = "/data/video/video.ts";
                    String PATH = "/data/video/" + videoName;
                    
                    File file = new File(PATH);
                    Log.v(TAG, "file download failure, deleteFile_swich:" + deleteFile_swich);
                    if (file.exists()) {
                    	if(deleteFile_swich){
                    		file.delete();
                    		Settings.System.putString(mContext.getContentResolver(), "AD_bean", null);
                    		Log.v(TAG, "file delete");
                    	}
                    }
                }
            }
        }).start();
    }

    // 抓取影片長度 <暫時沒用到>
    public long getVideoLength(String your_data_source) {
        Log.v(TAG, "your_data_source :" + your_data_source);
        ContentResolver resolver = Act.getContentResolver();
        String ImagePath = "file://" + your_data_source;
        Uri uri = Uri.parse(ImagePath);
        Cursor cursor = resolver.query(uri, null, null, null, MediaStore.Video.Media.DEFAULT_SORT_ORDER);
        long timeInmillisec = cursor.getInt(cursor.getColumnIndexOrThrow(MediaStore.Video.Media.DURATION));
        Log.v(TAG, "timeInmillisec :" + timeInmillisec);
        return timeInmillisec;
    }

    //H828才需要此機制
//    void checkFileFormat(String address){
//    	Log.v(TAG, "checkFileFormat");
//    	if(address.contains(".ts")){
//    		//下載的影片格式為ts
//    		file_format=true;
//    		Log.v(TAG, "checkFileFormat, file is .ts");
//    	}else if(address.contains(".mp4")){
//    		//下載的影片格式為mp4
//    		file_format=false;
//    		Log.v(TAG, "checkFileFormat, file is .mp4");
//    	}else{
//    		file_format=false;
//    		Log.v(TAG, "checkFileFormat, file is unknow");
//    	}
//    }
}
