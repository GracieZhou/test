package com.utsmta.mstar;

import android.content.Context;
import android.os.Bundle;

import com.utsmta.common.FactoryItem;
import com.utsmta.common.ItemInspectListener;
import com.utsmta.utils.LogUtil;

public class MstarStorageInspectListener extends ItemInspectListener {
	private final String TAG = "MstarStorageInspectListener";
	
	public MstarStorageInspectListener(Context context, FactoryItem item) {
		super(context, item);
		// TODO Auto-generated constructor stub
	}

	@Override
	public void onResultUpdate(boolean passed, int error, Bundle extra) {
		// TODO Auto-generated method stub

		Boolean bexternal_sd = false;
		bexternal_sd = extra.getBoolean("bexternal_sd");
		int deviceId = extra.getInt("deviceId");	
		int mainId = deviceId/1000;	
		
		
		String idStr = item.getProperty("device_id");
		String idStr_3_0 = item.getProperty("device_id_3_0");
		
		if(idStr != null && idStr.matches("^[0-9]*")){
			int id = Integer.parseInt(idStr);
			if(id == mainId){
				item.setResult(true);
				notifyUiUpdate();
			}
		}
		
		if(idStr_3_0 != null && idStr_3_0.matches("^[0-9]*")){
			int id = Integer.parseInt(idStr_3_0);
			if(id == mainId){
				item.setResult(true);
				notifyUiUpdate();
			}
		}

		if( bexternal_sd && item.getName().equals("sdcard")){					
			item.setResult(true);
			notifyUiUpdate();
		}
		
	}
}
