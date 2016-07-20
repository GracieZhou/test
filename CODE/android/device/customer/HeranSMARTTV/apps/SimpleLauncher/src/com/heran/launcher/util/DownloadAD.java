/*
 * 20160512
 * 新的廣告下載，已優化
 * 1.合入「檔案刪除開關」功能
 * 2.合入「下載檔案格式判斷」機制
 */

package com.heran.launcher.util;

import java.io.File;
import java.io.FileInputStream;
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

import com.mstar.android.tvapi.common.TvManager;
import com.mstar.android.tvapi.common.exception.TvCommonException;

import android.app.Activity;
import android.content.ContentResolver;
import android.database.Cursor;
import android.net.Uri;
import android.os.AsyncTask;
import android.provider.MediaStore;
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
    
    boolean file_format = true;
    
    boolean deleteFile_swich =false;

    public DownloadAD(boolean deleteFile_swich){
    	this.deleteFile_swich =deleteFile_swich;
    }
    
    // boolean OPEN_AD;
    @Override
    protected Void doInBackground(Void... params) {
        try {
            mac = TvManager.getInstance().getEnvironment("ethaddr");

        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        Log.v(TAG, "*** new Download method ***");
        Get_AD();

        return null;
    }

    public void Get_AD() {
    	Log.v(TAG, "Get_AD"); 
        bean = ExtreamaxVideoAdService.getInstance().getVideoAd("a2aea910b97ab0a66c196b0581610e5e", mac);
//        bean = ExtreamaxVideoAdService.getInstance().getVideoAd("a2aea910b97ab0a66c196b0581610e5e123456", mac);  //測試用，錯誤的
        if (bean != null) {
            AD_Url = bean.getUrl();
            
        	Log.v(TAG, "Send Records:" + back); 
        	
    		if(AD_Url!=null){
    			if("".equals(AD_Url)){
    				Log.v(TAG, "Download url:blank");
    			}else{
    				checkFileFormat(AD_Url);
    				ADVedio(AD_Url);
                	Log.v(TAG, "Download url:" + AD_Url);
                    VideoTime();
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
        }else{
        	Log.v(TAG, "bean == null");
        }
    }

    // 影片長度固定10秒
    public void VideoTime() {
        if (bean.getHash() != null) {
            int ok = ExtreamaxVideoAdService.getInstance().sendVideoAdLog(bean.getHash(), 10000);
            Log.v(TAG, "bean.getHash()_status:" + ok);
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
                    Log.d(TAG, "HttpStatus.SC_OK" + HttpStatus.SC_OK);

                    if (response.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {
                        String PATH = "/data/video";
                        File file = new File(PATH);
                        file.mkdirs();
                        
                        Log.v(TAG, "PATH:" + PATH);
                        File outputFile = new File(file, "video.ts");
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
                        // 下載完成
                        Log.v(TAG, "outputFile :" + outputFile);                        
                        
                        //比對檔案大小
//                        try {
//                            Long file_size = getFileSize(outputFile);
//                            if (bytetotal == file_size) {
//                            	Log.v(TAG, "size of file is same");
//                            	if(!file_format){
//                            		outputFile.delete();
//                                }
//                            } else {
//                            	Log.v(TAG, "size of file is not same!!" + file_size);
//                            	outputFile.delete();
//                            }
//                        } catch (Exception e) {
//                            Log.d(TAG, "file size is error :" + e.toString());
//                        }
                        
                        
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
                    String PATH = "/data/video/video.ts";
                    File file = new File(PATH);
                    Log.v(TAG, "file download failure, deleteFile_swich:" + deleteFile_swich);
                    if (file.exists()) {
                    	if(deleteFile_swich){
                    		file.delete();
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
    
    void checkFileFormat(String address){
    	Log.v(TAG, "checkFileFormat");
    	if(address.contains(".ts")){
    		//下載的影片格式為ts
    		file_format=true;
    		Log.v(TAG, "checkFileFormat, file is .ts");
    	}else if(address.contains(".mp4")){
    		//下載的影片格式為mp4
    		file_format=false;
    		Log.v(TAG, "checkFileFormat, file is .mp4");
    	}else{
    		file_format=false;
    		Log.v(TAG, "checkFileFormat, file is unknow");
    	}
    }
    
    // 計算檔案的大小，來判斷檔案是否正確
    private long getFileSize(File file) throws IOException {
        long size = 0;
        if (file.exists()) {
            FileInputStream fis = null;
            fis = new FileInputStream(file);
            size = fis.available();
        }

        return size;
    }
}
