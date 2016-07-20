package com.utsmta.common;

import java.util.ArrayList;

import com.utsmta.app.MtaApplication;

import android.os.Bundle;
import android.os.Handler;
import android.os.Message;

public abstract class FactoryInspect {
	private static final int MSG_UPDATE_RESULT = 99999;
	
	private boolean isAuto = true;
	
	private boolean isStarted = false;
	
	private ArrayList<InspectResultListener> listeners = new ArrayList<InspectResultListener>();
	
	public interface InspectResultListener {
		public void onResultUpdate(boolean passed, int error, Bundle extra);
	}
	
	protected Handler uiHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			if(msg.what == MSG_UPDATE_RESULT){
				Bundle data 	= msg.getData();
				boolean result	= data.getBoolean("inspect_result");
				int error = data.getInt("error");
				
				for(InspectResultListener resultListener:listeners){
					resultListener.onResultUpdate(result, error, data);
				}
				
			}else{
				handleUiHandlerMessage(msg);
			}
		};
	};
	
	protected Handler backgroundHandler = new Handler(MtaApplication.getBackgroundLooper()){
		public void handleMessage(Message msg) {
			handleBackgroundHandlerMessage(msg);
		};
	};
	
	/**
	 * 
	 * @param passed
	 * @param error
	 * @param extra
	 */
	protected void updateInspectResult(boolean passed, int error, Bundle extra){
		Message msg = uiHandler.obtainMessage(MSG_UPDATE_RESULT);
		Bundle data = new Bundle();
		
		data.putBoolean("inspect_result", passed);
		data.putInt("error", error);	
		if(extra != null) {
			data.putAll(extra);
		}
		msg.setData(data);
		
		uiHandler.sendMessage(msg);
	}
	
	public FactoryInspect(){
		
	}
	
	public FactoryInspect(boolean isAuto){
		this.isAuto = isAuto;
	}
	
	/**
	 * 
	 */
	public final void startInspect(){
		if(!isStarted){
			onStart();
			isStarted = true;
		}
	}
	
	/**
	 * 
	 */
	public final void stopInspect(){
		if(isStarted){
			onStop();
			isStarted = false;
		}
	}
	
	/**
	 * 
	 * @param resultListener
	 */
	public void registerResultListener(InspectResultListener resultListener){
		listeners.add(resultListener);
	}
	
	/**
	 * 
	 * @param resultListener
	 */
	public void unregisterResultListener(InspectResultListener resultListener){
		listeners.remove(resultListener);
	}
	
	/**
	 * 
	 */
	public void removeAllListeners(){
		listeners.clear();
	}
	
	public boolean isAuto(){
		return isAuto;
	}
	
	protected void handleUiHandlerMessage(Message msg){
		
	}
	
	protected void handleBackgroundHandlerMessage(Message msg){
		
	}
	
	protected void onStart(){
		
	}
	
	protected void onStop(){
		
	}
}
