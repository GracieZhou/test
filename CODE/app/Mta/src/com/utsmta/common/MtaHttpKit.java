package com.utsmta.common;

import java.io.IOException;
import java.io.UnsupportedEncodingException;

import org.apache.http.HttpHost;
import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.HttpVersion;
import org.apache.http.ParseException;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.conn.ClientConnectionManager;
import org.apache.http.conn.params.ConnManagerParams;
import org.apache.http.conn.scheme.PlainSocketFactory;
import org.apache.http.conn.scheme.Scheme;
import org.apache.http.conn.scheme.SchemeRegistry;
import org.apache.http.conn.ssl.SSLSocketFactory;
import org.apache.http.entity.StringEntity;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.impl.conn.tsccm.ThreadSafeClientConnManager;
import org.apache.http.params.BasicHttpParams;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.params.HttpProtocolParams;
import org.apache.http.protocol.HTTP;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import com.utsmta.utils.LogUtil;

public class MtaHttpKit {
	private final static String TAG = "MtaHttpKit";
	
	public static int ERROR_INVALID_URI = 0x01;
	
	public static int ERROR_TIMEOUT = 0x02;
	
	public static int ERROR_IO_EXCEPTION = 0x03;
	
	public static int ERROR_CLIENT_PROTOCOL_EXCEPTION = 0x04;
	
	public static class PostPacket extends JSONObject{
		
	}
	
	public static class ResponsePacket extends JSONObject{

		public ResponsePacket(String json) throws JSONException {
			// TODO Auto-generated constructor stub
			super(json);
		}
		
	}
	
	public interface OnResponseListener{
		public void onResponse(int error, ResponsePacket responsePacket);
	}
	
	public static void post(final String uri, final PostPacket postPacket, final OnResponseListener responseListener){
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				HttpPost httpPost = null;
				
				LogUtil.d(TAG, "uri = "+uri);
				try {
					httpPost = new HttpPost(uri);
				} catch (Exception e) {
					// TODO: handle exception
				}
				
				if (httpPost == null) {
					LogUtil.d(TAG, "httpPost == null");
					if(responseListener != null){
						responseListener.onResponse(ERROR_INVALID_URI, null);
					}
					return ;
				}
				try {
					httpPost.setEntity(new StringEntity(postPacket.toString()));
					int error = 0;
					HttpResponse httpResponse = null;
					try {
						httpResponse = MyHttpClient.getDefaultHttpClient().execute(httpPost);
						if (httpResponse.getStatusLine().getStatusCode() != HttpStatus.SC_OK) {
							LogUtil.d(TAG, "ERROR_TIMEOUT");
							error = ERROR_TIMEOUT;
						} 
					} catch (ClientProtocolException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						error = ERROR_CLIENT_PROTOCOL_EXCEPTION;
						LogUtil.d(TAG, "ERROR_CLIENT_PROTOCOL_EXCEPTION");
					} catch (IOException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
						error = ERROR_IO_EXCEPTION;
						LogUtil.d(TAG, "ERROR_IO_EXCEPTION");
					}
					
					if(responseListener != null){
						ResponsePacket responsePacket = null;
						if(httpResponse != null){
							try {
								responsePacket = new ResponsePacket(EntityUtils.toString(httpResponse.getEntity()));
							} catch (ParseException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (JSONException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						responseListener.onResponse(error, responsePacket);
					}
				} catch (UnsupportedEncodingException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}				
			}
		}).start();
	}
	
	private static class MyHttpClient{
		private static HttpClient gHttpClient = null;	
		
		public static HttpClient getDefaultHttpClient(){
			if(null == gHttpClient){
				synchronized (MyHttpClient.class) {
					if(null == gHttpClient){
						HttpParams httpParams = new BasicHttpParams();
						HttpProtocolParams.setVersion(httpParams, HttpVersion.HTTP_1_1);
						HttpProtocolParams.setContentCharset(httpParams, HTTP.UTF_8);
						HttpProtocolParams.setUseExpectContinue(httpParams, true);
						
						ConnManagerParams.setTimeout(httpParams, 4000);
						
						HttpConnectionParams.setConnectionTimeout(httpParams, 6000);
						HttpConnectionParams.setSoTimeout(httpParams, 6000);
						
			            SchemeRegistry schReg =new SchemeRegistry();
			            schReg.register(new Scheme("http", PlainSocketFactory.
			            		getSocketFactory(), 80));
			            schReg.register(new Scheme("https", SSLSocketFactory
			                    .getSocketFactory(), 443));
			            
			            ClientConnectionManager conMgr =new ThreadSafeClientConnManager(
			                    httpParams, schReg);
			            gHttpClient =new DefaultHttpClient(conMgr, httpParams);
			            java.security.Security.setProperty("networkaddress.cache.ttl" , "0");
					}
				}
			}
			return gHttpClient;
		}
	}
}
