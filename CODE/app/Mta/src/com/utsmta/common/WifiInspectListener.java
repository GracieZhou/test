package com.utsmta.common;

import com.utsmta.common.inspect.WifiInspect;

import android.content.Context;
import android.os.Bundle;

public class WifiInspectListener extends ItemInspectListener {
	private boolean connectAp = false;
	
	public WifiInspectListener(Context context, FactoryItem item) {
		super(context, item);
		// TODO Auto-generated constructor stub
		if("true".equals(item.getProperty("connect_ap"))){
			connectAp = true;
		}
	}

	@Override
	public void onResultUpdate(boolean passed, int error, Bundle extra) {
		// TODO Auto-generated method stub
		if(passed){
			if(error == WifiInspect.WIFI_OPEN_SUCCESS){
				if(!connectAp){
					item.setResult(true);
					notifyUiUpdate();
				}
			}else if(error == WifiInspect.WIFI_CONNECT_SUCCESS){
				if(connectAp){
					item.setResult(true);
					notifyUiUpdate();
				}
			}	
		}
	}
}
