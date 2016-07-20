package com.utsmta.mstar.inspect;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.os.Bundle;
import android.os.Message;

import com.mstar.android.tvapi.common.PictureManager;
import com.utsmta.common.FactoryInspect;

public class MstarGravitySensorInspect extends FactoryInspect {
	@Override
	protected void handleBackgroundHandlerMessage(Message msg) {
		// TODO Auto-generated method stub
		inspectGravitySensor();
		backgroundHandler.sendEmptyMessageDelayed(0, 2500);
	}
	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		backgroundHandler.sendEmptyMessage(0);
	}
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		backgroundHandler.removeMessages(0);
		super.onStop();
	}
	
	private void inspectGravitySensor(){			
		Class<?> pictureManagerClass = PictureManager.class;
		
		try {
			Method method = pictureManagerClass.getMethod("GSensorCalibration");
			final PictureManager pictureManager = PictureManager.getInstance();
			 try {
				if((Boolean) method.invoke(pictureManager)){
					updateInspectResult(true, 0, new Bundle());
					stopInspect(); 
				 }
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
	}
}
