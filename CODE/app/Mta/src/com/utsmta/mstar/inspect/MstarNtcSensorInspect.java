package com.utsmta.mstar.inspect;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.os.Bundle;
import android.os.Message;

import com.utsmta.app.MtaApplication;
import com.mstar.android.tvapi.common.PictureManager;
import com.utsmta.common.FactoryInspect;
import com.utsmta.utils.LogUtil;

public class MstarNtcSensorInspect extends FactoryInspect {
	private final String TAG = "MstarNtcSensorInspect";
	@Override
	protected void handleBackgroundHandlerMessage(Message msg) {
		// TODO Auto-generated method stub
		if(msg.what == 0){			
			if(checkNtcState()){
				updateInspectResult(true, 0, new Bundle());
				stopInspect(); 
			}else{
				backgroundHandler.sendEmptyMessageDelayed(0, 3000);
			}
		}
	}
	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		backgroundHandler.sendEmptyMessageDelayed(0, 500);
	}
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		backgroundHandler.removeMessages(0);
	}
	
	public int getTemperature(int ntcNmb){
			int temperature = -100;
			Class<?> pictureManagerClass = PictureManager.class;
			try {
				Method method = pictureManagerClass.getMethod("dlpGetTemperature", int.class);
				final PictureManager pictureManager = PictureManager.getInstance();
				 try {
					 temperature = (Integer)method.invoke(pictureManager, ntcNmb);				 
					 LogUtil.d(TAG, "temperature = "+temperature);
				} catch (IllegalAccessException e) {
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					e.printStackTrace();
				} catch (Exception e){
				}
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			}
			return temperature;
	}
	protected int getTemperature(){
		int temperature = -100;
		
		Class<?> pictureManagerClass = PictureManager.class;
		
		try {
			Method method = pictureManagerClass.getMethod("dlpGetTemperature", int.class);
			final PictureManager pictureManager = PictureManager.getInstance();
			 try {
				 temperature = (Integer)method.invoke(pictureManager, 1);
				 LogUtil.d(TAG, "temperature = "+temperature);
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (Exception e){
				
			}
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return temperature;
	}
	private boolean checkNtcState() {
		if("BenQi300".equals(MtaApplication.getDevice().getDeviceName())){			
			if(getTemperature() > -50){
				return true;
			}
		}else if("BenQi500".equals(MtaApplication.getDevice().getDeviceName())){
			if((getTemperature(1) > -50) 
				&& (getTemperature(2) > -50)
				&& (getTemperature(3) > -50)){
				return true;
			}
		}		
		return false;
	}
}
