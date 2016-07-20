package com.utsmta.common;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.UserHandle;

import com.utsmta.common.FactoryItem;
import com.utsmta.common.FactoryInspect;

public class ItemInspectListener implements FactoryInspect.InspectResultListener{
	protected Context context = null;
	
	protected FactoryItem item = null;
	
	public ItemInspectListener(Context context, FactoryItem item){
		this.context = context;
		this.item = item;
	}
	
	protected void notifyUiUpdate(){
		Intent intent = new Intent("mta.update.ui");
		context.sendBroadcastAsUser(intent, UserHandle.OWNER);
	}

	@Override
	public void onResultUpdate(boolean passed, int error, Bundle extra) {
		// TODO Auto-generated method stub
		if(passed){
			item.setResult(true);
			notifyUiUpdate();
		}
	}
}
