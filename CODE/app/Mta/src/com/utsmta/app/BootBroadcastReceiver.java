package com.utsmta.app;

import com.utsmta.common.FactoryDevice;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

public class BootBroadcastReceiver extends BroadcastReceiver {
	private final static String TAG = BootBroadcastReceiver.class.toString();
	
	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		FactoryDevice factoryDevice = MtaApplication.getDevice();
		if(factoryDevice != null){
			factoryDevice.onSystemBooted(context);
		}
	}

}
