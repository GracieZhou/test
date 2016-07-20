package android.webkit;

import android.view.KeyEvent;

public class EOSWebClient {

    public EOSWebClient() {
    }

    public boolean handerKey(int keyCode, KeyEvent event) {
        return true;
    }

    public String getUser() {
        return "";
    }

    public String getIpAddr() {
        return "";
    }

    public String getEthAddr() {
        return "";
    }

    public void setUsrInfo(String user, String ip, String log, String time) {

    }

    public void setValue(String key, String value) {

    }

    public String getValue(String key) {
        return "";
    }

    public void launcherPageLeft() {

    }

    public void launcherPageRight() {

    }

    public boolean jowinlogin(String usrname, String hy, String coin) {
        return false;
    }

    public boolean jowinlogin(int id, String usrname, String hy, String coin) {
        return false;
    }

    public boolean jowinout(String usrname) {
        return false;
    }

    public void requestWebViewFocus() {

    }

    public void clearWebViewFocus() {

    }

    public void AppStoreBackEvent() {

    }
	
		public void cleanWebViewHistory(String url){
	
	}
	
	public String getHeranFn(String fun , String value){
	    return "";
	}
	
	public String getHeranFn1(int i,int j,int k){
		return "";
	}
	
	public int getHeranFn2(int i,int j,int k){
		return 0;
	}
}
