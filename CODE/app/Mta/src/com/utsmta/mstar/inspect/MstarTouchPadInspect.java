package com.utsmta.mstar.inspect;

import android.os.Bundle;
import android.os.Message;

import com.utsmta.common.FactoryInspect;
import com.utsmta.mstar.MstarNative;

public class MstarTouchPadInspect extends FactoryInspect {
	private MstarNative mstarNative = MstarNative.getInstance();
	
	@Override
	protected void handleBackgroundHandlerMessage(Message msg) {
		// TODO Auto-generated method stub
		int key = mstarNative.scanTouchPadKey();
		
		if(key >= 0){
			Bundle extra = new Bundle();
			extra.putInt("key", key);
			updateInspectResult(true, 0, extra);
		}
		
		backgroundHandler.sendEmptyMessageDelayed(0, 50);
	}
	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		backgroundHandler.sendEmptyMessageDelayed(0, 500);
	}
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		backgroundHandler.removeMessages(0);
		super.onStop();
	}
}
