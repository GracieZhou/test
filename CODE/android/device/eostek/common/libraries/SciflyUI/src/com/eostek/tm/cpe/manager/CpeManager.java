package com.eostek.tm.cpe.manager;

import android.content.Context;
import android.os.IBinder;
import android.os.ServiceManager;
import android.os.RemoteException;

public class CpeManager {

	public static CpeManager mInstance;
	
	public static ICpeManager mCpeManager;
	
	private CpeManager(ICpeManager service) {
		mCpeManager = service;
	}
	
	public static CpeManager getInstance(){
		if(mInstance == null){
			synchronized (CpeManager.class) {
				if(mInstance == null){
					IBinder b = ServiceManager.getService(Context.CPE_SERVICE);
					mInstance = new CpeManager(ICpeManager.Stub.asInterface(b));
				}
			}
		}
		return  mInstance;
	}
	
	public String getBBNumber(){
		try {
			return mCpeManager.getBBNuber();
		} catch (RemoteException e) {
			e.printStackTrace();
			return "";
		}
	}
	
	public String getProductClass(){
		try{
			return mCpeManager.getProductClass();
		} catch(RemoteException e){
			e.printStackTrace();
			return "";
		}
	}
	
	public String getSerialNumber(){
		try{
			return mCpeManager.getSerialNumber();
		} catch(RemoteException e){
			e.printStackTrace();
			return "";
		}
	}
	
	public String getCpeStatus(){
		try{
			return mCpeManager.getCpeStatus();
		} catch(RemoteException e){
			e.printStackTrace();
			return "";
		}
	}
}
