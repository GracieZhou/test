package android.webkit;

import android.content.Context;
import android.util.Log;
import java.util.HashMap;
import java.util.Map;
import android.os.Handler;

public class EOSUtility {

    private final static String TAG = "==>EOSUtility";
    private WebView mWebview = null;
    private EOSWebClient mClient = null;
    private Map<String, String> mMap;

    private boolean mIsZoomEnable = true;
    private boolean mIsMenuEnable = true;

    private Handler handler = new Handler();

    public EOSUtility(WebView context) {
        Log.d(TAG, "EOSUtility construct");
        mWebview = context;
        mMap = new HashMap<String, String>();
    }

    public void setEOSClient(EOSWebClient client) {
        Log.d(TAG, "setEOSClient");
        mClient = client;
    }

    // app implement
    @JavascriptInterface
    public String getUser() {
        if (null != mClient) {
            return mClient.getUser();
        }
        return "";
    }

    // app implement
    @JavascriptInterface
    public String getIpAddr() {
        if (null != mClient) {
            return mClient.getIpAddr();
        }
        return "";
    }

    // app implement
    @JavascriptInterface
    public void setUsrInfo(String user, String ip, String log, String time) {
        Log.d(TAG, "====>=user" + user + "ip=" + ip + "log=" + log + "time="
                + time);
        if (null != mClient) {
            mClient.setUsrInfo(user, ip, log, time);
        }
    }

    // app implement
    @JavascriptInterface
    public void setValue(String key, String value) {
        Log.d(TAG, "key:" + key + " value:" + value);
        if (null != key && null != value) {
            mMap.put(key, value);
        }
    }

    // app implement
    @JavascriptInterface
    public String getValue(String key) {
        if (mMap.containsKey(key))
            return mMap.get(key);

        return "";
    }

    @JavascriptInterface
    public String getEthAddr() {
        if (null != mClient) {
            return mClient.getEthAddr();
        }

        return "";
    }

    @JavascriptInterface
    public void setEnableZoom(boolean enable) {
        Log.d(TAG, "setEnableZoom:" + enable);
    }

    @JavascriptInterface
    public void setEnableWebMenu(boolean enable) {
        Log.d(TAG, "setEnableWebMenu:" + enable);
    }

    @JavascriptInterface
    public void setMousePos(int x, int y) {
    }

    @JavascriptInterface
    public void launcherPageLeft() {
        if (null != mClient) {
            mClient.launcherPageLeft();
        }
    }

    @JavascriptInterface
    public void launcherPageRight() {
        if (null != mClient) {
            mClient.launcherPageRight();
        }
    }

    @JavascriptInterface
    public boolean jowinlogin(String usrname, String hy, String coin) {
        if (null != mClient) {
            return mClient.jowinlogin(usrname, hy, coin);
        }
        return false;
    }

    @JavascriptInterface
    public boolean jowinlogin(int id, String usrname, String hy, String coin) {
        if (null != mClient) {
            return mClient.jowinlogin(id, usrname, hy, coin);
        }
        return false;
    }

    @JavascriptInterface
    public boolean jowinout(String usrname) {
        if (null != mClient) {
            return mClient.jowinout(usrname);
        }
        return false;
    }

    @JavascriptInterface
    public boolean setFocusStyle(String color, int width) {
        return false;
    }

    @JavascriptInterface
    public void requestWebViewFocus() {
        Log.d(TAG, "EOSUtility requestWebViewFocus");
        if (null != mClient) {
            mClient.requestWebViewFocus();
        }
    }

    @JavascriptInterface
    public void clearWebViewFocus() {
        Log.d(TAG, "EOSUtility clearWebViewFocus");
        if (null != mClient) {
            mClient.clearWebViewFocus();
        }
    }

    @JavascriptInterface
    public void AppStoreBackEvent() {
        Log.d(TAG, "EOSUtility AppStoreBackEvent");
        if (null != mClient) {
            mClient.AppStoreBackEvent();
        }
    }
	
		@JavascriptInterface
	public void cleanWebViewHistory(String url){
	    Log.d(TAG,"EOSUtility cleanWebViewHistory");
		if (null != mClient){
			mClient.cleanWebViewHistory(url);
		}
	}
	
	@JavascriptInterface
	public String getHeranFn(String fun , String value){
	    Log.d(TAG,"EOSUtility getHeranFn");
		if (null != mClient){
			return mClient.getHeranFn(fun,value);
		}
		return "";
	}
	
	@JavascriptInterface
	public String getHeranFn1(int i,int j,int k){
	    Log.d(TAG,"EOSUtility getHeranFn1");
		if (null != mClient){
			return mClient.getHeranFn1(i,j,k);
		}
		return "";
	}
	
	@JavascriptInterface
	public int getHeranFn2(int i,int j,int k){
	    Log.d(TAG,"EOSUtility getHeranFn2");
		if (null != mClient){
			return mClient.getHeranFn2(i,j,k);
		}
		return 0;
	}
}
