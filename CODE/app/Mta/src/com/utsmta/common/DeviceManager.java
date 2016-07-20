package com.utsmta.common;

import com.utsmta.utils.LogUtil;

public class DeviceManager {
	private static final String TAG = "DeviceManager";
	
	private static final String MSTAR_DEVICE_CLASS_NAME = "com.utsmta.mstar.MstarMtaDevice";
	
	public static final String DEVICE_H628 = "H628";
	
	public static final String DEVICE_L628 = "L628";
	
	public static final String DEVICE_H638 = "H638";
		
	public static final String DEVICE_H828 = "H828";
	
	public static final String DEVICE_BENQI300 = "BenQi300";
	
	public static final String DEVICE_BENQI500 = "BenQi500";
	
	public static FactoryDevice createMtaDevice(String name, String branch){
		LogUtil.d(TAG, "createDeviceWithName name = "+name);
		
		FactoryDevice device = null;
		Class<?> deviceClass = null;
		
		if(DEVICE_H628.equals(name) 
			|| DEVICE_L628.equals(name)
			|| DEVICE_H638.equals(name)
			|| DEVICE_H828.equals(name)
			|| DEVICE_BENQI300.equals(name)
			|| DEVICE_BENQI500.equals(name)){
			try {
				deviceClass = Class.forName(MSTAR_DEVICE_CLASS_NAME);
			} catch (ClassNotFoundException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		
		if(deviceClass != null){
			try {
				device = (FactoryDevice) deviceClass.newInstance();
				device.setDeviceName(name);
				device.setDeviceBranch(branch);
				device.onCreate();
			} catch (InstantiationException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}

		return device;
	}
}
