package com.utsmta.common.inspect;

import java.util.ArrayList;
import java.util.List;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiConfiguration.AuthAlgorithm;
import android.net.wifi.WifiConfiguration.KeyMgmt;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

import com.utsmta.common.FactoryInspect;
import com.utsmta.utils.LogUtil;

public class WifiInspect extends FactoryInspect {
	private static final String TAG = "WifiInspect";
	
	private static final int WIFI_RESCAN_INTERVAL_MS = 3 * 1000;
	
	private static final int MSG_CONNECT_AP = 0x01;
	
	public static final int WIFI_OPEN_SUCCESS = 0;
	
	public static final int WIFI_CONNECT_SUCCESS = 1;
	
	private Context context = null;
	
	private WifiManager wifiManager = null;
	
	private Scanner scanner = null;
	
	private int signal = 0;
	
	private String apSsid = null;
	
	private String apPassword = null;
	
	private List<ScanResult> scanResults = new ArrayList<ScanResult>();
	
	private BroadcastReceiver receiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			updateWifiInspectResult();
		}
	};
	
	@Override
	protected void handleBackgroundHandlerMessage(Message msg) {
		LogUtil.d(TAG, "handleBackgroundHandlerMessage");

		final List<WifiConfiguration> configs = wifiManager.getConfiguredNetworks();
		
		LogUtil.d(TAG, "apSsid = "+apSsid);
		
		for(WifiConfiguration config : configs){
			LogUtil.d(TAG, "config.SSID = "+config.SSID+" apSsid = "+apSsid);
			if(config.SSID.equals("\""+apSsid+"\"")){
				LogUtil.d(TAG, "find ssid : "+apSsid);
				if(config.status == WifiConfiguration.Status.ENABLED){
					LogUtil.d(TAG, "ssid : "+apSsid+" is ENABLED ");
					wifiManager.forget(config.networkId, forgetListener);					
				}else if(config.status == WifiConfiguration.Status.CURRENT){
					LogUtil.d(TAG, "ssid : "+apSsid+" is CURRENT ");
					Bundle extra = new Bundle();
					extra.putString("ssid", apSsid);
					updateInspectResult(true, WIFI_CONNECT_SUCCESS, extra);
				}
				
				return;
			}
		}

		wifiManager.connect(getWifiConfig(apSsid, apPassword), connectListener);
	};
	
	protected WifiInspect(){

	}
	
	public WifiInspect(Context context){
		this.context = context;
		this.wifiManager = (WifiManager) this.context.getSystemService(Context.WIFI_SERVICE);
		this.scanner = new Scanner();
	}

	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		LogUtil.d(TAG, "onStart");
		signal = 0;
		if(!wifiManager.isWifiEnabled()){
			wifiManager.setWifiEnabled(true);
		}
		IntentFilter intentFilter = new IntentFilter();
		intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
		intentFilter.addAction(WifiManager.NETWORK_STATE_CHANGED_ACTION);
		intentFilter.addAction(WifiManager.SCAN_RESULTS_AVAILABLE_ACTION);
		this.context.registerReceiver(receiver, intentFilter);
		this.scanner.resume();
		updateWifiInspectResult();	
	}
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		LogUtil.d(TAG, "onStop");
		backgroundHandler.removeMessages(MSG_CONNECT_AP);
		this.context.unregisterReceiver(receiver);
		this.scanner.pause();		
		super.onStop();
	}
	
	private void updateWifiInspectResult(){
//		LogUtil.d(TAG, "updateWifiInspectResult");
		if(WifiManager.WIFI_STATE_ENABLED == wifiManager.getWifiState()){
			int level  = 0;
			int _level = 0;
			this.scanResults.clear();
			final List<ScanResult> _scanResults = wifiManager.getScanResults();
			for(ScanResult scanResult:_scanResults){			
				_level = WifiManager.calculateSignalLevel(scanResult.level, 100);
				if(_level > level){
					level = _level;
				}
				
				this.scanResults.add(scanResult);
			}
			
			signal = level;
			Bundle extra = new Bundle();
			extra.putInt("signal", signal);
			updateInspectResult(true, WIFI_OPEN_SUCCESS, extra);
		}
	}
	
	public int getCurrentStrongestSignal(){
		return signal;
	}
	
	public List<ScanResult> getScanResults(){
		return this.scanResults;
	}
	
    private class Scanner extends Handler {
        private int mRetry = 0;

        void resume() {
            if (!hasMessages(0)) {
                sendEmptyMessage(0);
            }
        }

        void forceScan() {
            removeMessages(0);
            sendEmptyMessage(0);
        }

        void pause() {
            mRetry = 0;
            removeMessages(0);
        }

        @Override
        public void handleMessage(Message message) {
            if (wifiManager.startScan()) {
                mRetry = 0;
            } else if (++mRetry >= 3) {
                mRetry = 0;
                return;
            }
            sendEmptyMessageDelayed(0, WIFI_RESCAN_INTERVAL_MS);
        }
    }
    
	private WifiConfiguration getWifiConfig(String ssid, String password){	
		WifiConfiguration config = new WifiConfiguration();
		for(ScanResult result:wifiManager.getScanResults()){
			if(result.SSID.equals(ssid)){				
				config.SSID = "\"" + result.SSID + "\"";
				config.hiddenSSID = true;
		        if (result.capabilities.contains("WEP")) {
	                config.allowedKeyManagement.set(KeyMgmt.NONE);
	                config.allowedAuthAlgorithms.set(AuthAlgorithm.OPEN);
	                config.allowedAuthAlgorithms.set(AuthAlgorithm.SHARED);
	                if (password != null && password.length() != 0) {
	                    int length = password.length();
	                    if ((length == 10 || length == 26 || length == 58) &&
	                            password.matches("[0-9A-Fa-f]*")) {
	                        config.wepKeys[0] = password;
	                    } else {
	                        config.wepKeys[0] = '"' + password + '"';
	                    }
	                }
		        } else if (result.capabilities.contains("PSK")) {
	                config.allowedKeyManagement.set(KeyMgmt.WPA_PSK);
	                if (password != null && password.length() != 0) {
	                    if (password.matches("[0-9A-Fa-f]{64}")) {
	                        config.preSharedKey = password;
	                    } else {
	                        config.preSharedKey = '"' + password + '"';
	                    }
	                }
		        } else if (result.capabilities.contains("EAP")) {
		        }else{
		        	config.allowedKeyManagement.set(KeyMgmt.NONE);
		        }
		        break;
			}
		}
		return config;
	}
	
	private WifiManager.ActionListener connectListener = new WifiManager.ActionListener() {
		@Override
		public void onSuccess() {
			LogUtil.d(TAG, "connect success");
			Bundle extra = new Bundle();
			extra.putString("ssid", apSsid);
			updateInspectResult(true, WIFI_CONNECT_SUCCESS, extra);
		}
		
		@Override
		public void onFailure(int reason) {
			LogUtil.d(TAG, "connect failed");
			backgroundHandler.sendEmptyMessageDelayed(MSG_CONNECT_AP, 1500);
		}
	};
	
	private WifiManager.ActionListener forgetListener = new WifiManager.ActionListener() {
		
		@Override
		public void onSuccess() {
			// TODO Auto-generated method stub
			LogUtil.d(TAG, "forget success");
			wifiManager.connect(getWifiConfig(apSsid, apPassword), connectListener);
		}
		
		@Override
		public void onFailure(int reason) {
			// TODO Auto-generated method stub
			LogUtil.d(TAG, "forget failed");
			backgroundHandler.sendEmptyMessageDelayed(MSG_CONNECT_AP, 1500);
		}
	};
	
	public void connectWifiAp(String ssid, String password){
		backgroundHandler.removeMessages(MSG_CONNECT_AP);
		apSsid = ssid;
		apPassword = password;
		backgroundHandler.sendEmptyMessageDelayed(MSG_CONNECT_AP, 100);
	} 	
}
