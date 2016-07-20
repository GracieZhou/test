package com.utsmta.app;

import com.utsmta.common.ConfigManager;
import com.utsmta.common.DeviceManager;
import com.utsmta.common.FactoryDevice;

import android.app.Application;
import android.content.Context;
import android.os.HandlerThread;
import android.os.Looper;

public class MtaApplication extends Application {
	private static final String TAG = MtaApplication.class.toString();
	
	private static ConfigManager configManager = null;
	
	private static FactoryDevice mtaDevice = null;
	
	private static HandlerThread backgroundWorkingTask = new HandlerThread("mta_background_task");
	
	private static Context context = null;
	
	@Override
	public void onCreate() {
		// TODO Auto-generated method stub
		super.onCreate();
	
		context = getApplicationContext();
		
		backgroundWorkingTask.start();
		
		configManager = new ConfigManager(getApplicationContext());
		
		//create device
		mtaDevice = DeviceManager.createMtaDevice(configManager.deviceName(), configManager.deviceBranch());
		
		//parse config
//		configManager.parseConfig(mtaDevice);
	}
	
	@Override
	public void onTerminate() {
		// TODO Auto-generated method stub
		backgroundWorkingTask.quit();
		super.onTerminate();
	}
	
	public static Looper getBackgroundLooper(){
		return backgroundWorkingTask.getLooper();
	}
	
	public static ConfigManager getConfigManager(){
		return configManager;
	}
	
	public static FactoryDevice getDevice(){
		return mtaDevice;
	}
	
	public static Context getContext(){
		return context;
	}
}
