package com.utsmta.mstar;

import java.io.File;
import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.utsmta.app.R;
import com.utsmta.common.FactoryInspect.InspectResultListener;
import com.utsmta.common.FactoryItem;
import com.utsmta.common.ItemPagerView;
import com.utsmta.common.inspect.WifiInspect;
import com.utsmta.utils.LogUtil;
import com.utsmta.utils.MtaUtils;

public class MstarWifiView extends ItemPagerView {
	private final String TAG = "MstarWifiView";
	
	protected final String CONFIG_FILE_NAME = "eostek-fmtac.ini";
	
	private final String PROPERTY_WIFI_SSID = "WifiSSID";
	
	private final String PROPERTY_WIFI_PASSWORD = "WifiPasswd";
	
	private TextView wifiSignalTv = null;
	
	private TextView wifiApInfoTv = null;
	
	private WifiInspect wifiInspect = null;
	
	private ListView ssidLv = null;
	
	private ArrayAdapter<String> ssidAdapter = null;
	
	private boolean firstTime = true;
	
	private boolean showApList = false;
	
	private boolean connectAp = false;
	
	public MstarWifiView(Activity activity, FactoryItem item, WifiInspect wifiInspect) {
		super(activity, item);
		// TODO Auto-generated constructor stub
		this.wifiInspect = wifiInspect;
		
		if("true".equals(item.getProperty("show_ap_list"))){
			showApList = true;
		}
		
		if("true".equals(item.getProperty("connect_ap"))){
			connectAp = true;
		}
	}
	
	private InspectResultListener resultListener = new InspectResultListener() {

		@Override
		public void onResultUpdate(boolean passed, int error, Bundle extra) {
			// TODO Auto-generated method stub
			if(passed){
				if(error == WifiInspect.WIFI_OPEN_SUCCESS){
					if(firstTime && !connectAp){
						item.setResult(true);
						notifyUiUpdate();	
						firstTime = false;
					}	
					refreshUI();
				}else if(error == WifiInspect.WIFI_CONNECT_SUCCESS){
					if(connectAp){
						item.setResult(true);
						notifyUiUpdate();	
						updateWifiApInfo(true, extra.getString("ssid"));
					}
				}			
			}	
		}	
	};

	@Override
	protected View onCreateView(LayoutInflater inflater) {
		// TODO Auto-generated method stub
		LogUtil.d(TAG, "onCreateView");
		View contentView = inflater.inflate(R.layout.wifi, null);
		
		wifiSignalTv = (TextView)contentView.findViewById(R.id.wifi_signal);
		wifiSignalTv.setText(String.valueOf(wifiInspect.getCurrentStrongestSignal())+"%");
		
		wifiApInfoTv = (TextView)contentView.findViewById(R.id.wifi_ssid_title);
		if(connectAp){
			wifiApInfoTv.setVisibility(View.VISIBLE);
		}

		ssidLv = (ListView) contentView.findViewById(R.id.ap_list);
		
		ssidAdapter = new ArrayAdapter<String>(activity, R.layout.wifi_adapter, R.id.text_ex);
		
		ssidLv.setAdapter(ssidAdapter);
//		ssidLv.setFocusable(false);
//		ssidLv.setClickable(false);
		
		if(connectAp){
			connectWifiAp();
		}
		
		return contentView;
	}
	
	protected void refreshUI(){
		int signal = wifiInspect.getCurrentStrongestSignal();
		if(signal > 0){
			wifiSignalTv.setText(String.valueOf(signal)+"%");
		}
		
		if(showApList){
			ssidAdapter.clear();			
			for(ScanResult result:wifiInspect.getScanResults()){							
				ssidAdapter.add(result.SSID+" : "+WifiManager.calculateSignalLevel(result.level, 100)+"%");
			}
			ssidAdapter.notifyDataSetChanged();	
		}
	}
	
	@Override
	public void onShown() {
		// TODO Auto-generated method stub
		LogUtil.d(TAG, "onShown");
		refreshUI();
		wifiInspect.registerResultListener(resultListener);		
		
		if(!connectAp){
			if(wifiInspect.getCurrentStrongestSignal() > 0){
				item.setResult(true);
				notifyUiUpdate();
			}				
		}else{
			connectWifiAp();

			IntentFilter intentFilter = new IntentFilter();
			intentFilter.addAction("android.intent.action.MEDIA_MOUNTED");
			intentFilter.addDataScheme("file");
			activity.registerReceiver(storageReceiver, intentFilter);		
		}
	}
	
	@Override
	public void onHiden() {
		// TODO Auto-generated method stub
		LogUtil.d(TAG, "onHiden");
		if(connectAp){
			activity.unregisterReceiver(storageReceiver);
		}
		wifiInspect.unregisterResultListener(resultListener);
	}
	
	protected BroadcastReceiver storageReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if("android.intent.action.MEDIA_MOUNTED".equals(intent.getAction())){
				connectWifiAp();
			}			
		}
	};
	
	private void connectWifiAp(){
		wifiApInfoTv.setText(activity.getString(R.string.ssid_lable));
		String ssid = getWifiApSsid();
		LogUtil.d(TAG, "connectWifiAp ssid = "+ssid);
		if(ssid != null){
			String password = getWifiApPassword();
			wifiInspect.connectWifiAp(ssid, password);
		}
	}
	
	private void updateWifiApInfo(boolean ok, String ssid){
		if(ok){
			wifiApInfoTv.setText(activity.getString(R.string.ssid_lable)+ssid+"  "+
					activity.getString(R.string.connect_success));
		}
	}
	
	private String getWifiApSsid(){
		String ssid = null;
		
		for(String dirPath : MtaUtils.getMountedUsbDevices("/mnt/usb/")){
			File file = new File(dirPath+"/"+CONFIG_FILE_NAME);
			
			if(file.exists()){
				ssid = MtaUtils.getPropertyFromFile(file, PROPERTY_WIFI_SSID);
				break;
			}
		}
		
		return ssid;
	}
	
	private String getWifiApPassword(){
		String password = null;
		
		for(String dirPath : MtaUtils.getMountedUsbDevices("/mnt/usb/")){
			File file = new File(dirPath+"/"+CONFIG_FILE_NAME);
			
			if(file.exists()){
				password = MtaUtils.getPropertyFromFile(file, PROPERTY_WIFI_PASSWORD);
				break;
			}
		}
		
		return password;
	}
}
