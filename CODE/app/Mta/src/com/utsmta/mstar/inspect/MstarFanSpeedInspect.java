package com.utsmta.mstar.inspect;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import com.mstar.android.tvapi.common.PictureManager;
import com.utsmta.common.FactoryInspect;
import com.utsmta.utils.LogUtil;

public class MstarFanSpeedInspect extends FactoryInspect {
	private final String TAG = "MstarFanSpeedInspect";
	
	public int getFanSpeed(int fan){
		int speed = 0;
		
		if(fan == 0 || fan == 1){
			Class<?> pictureManagerClass = PictureManager.class;
			
			try {
				Method method = pictureManagerClass.getMethod("dlpGetFanMeasuredSpeed", int.class);
				final PictureManager pictureManager = PictureManager.getInstance();
				 try {
					 speed = (Integer)method.invoke(pictureManager, fan);
					 LogUtil.d(TAG, "speed["+fan+"] = " + speed);
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
		
		return speed;
	}
	
	/**
	 * 
	 * @param fan
	 * @param speed
	 */
	public void setFanSpeed(int fan, int speed){
		if(fan == 0 || fan == 1){
			Class<?> pictureManagerClass = PictureManager.class;
			
			try {
				Method method = pictureManagerClass.getMethod("dlpSetFanPwm", int.class, int.class);
				final PictureManager pictureManager = PictureManager.getInstance();
				 try {
					 method.invoke(pictureManager, fan, speed);
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
}
