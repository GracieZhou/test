package com.utsmta.common;

import android.app.Activity;
import android.content.Context;

public abstract class FactoryDevice {
	public static final String DEVICE_BRANCH_TV = "tv";
	
	public static final String DEVICE_BRANCH_DLP = "dlp";
	
	protected String deviceName = null;
	
	protected String deviceBranch = null;
	
	protected boolean amta = false;
	
	public void activeAmtaMode(boolean bEnable){
		this.amta = bEnable;
	}
	
	protected void setDeviceName(String deviceName){
		this.deviceName = deviceName;
	}
	
	protected void setDeviceBranch(String deviceBranch){
		this.deviceBranch = deviceBranch;
	}
		
	protected void onCreate(){
		
	}
	
	protected void onDestroy(){
		
	}
	
	/**
	 * 
	 * @param group
	 * @return true  -- group will be added
	 * 		   flase -- group will be abandoned
	 */
	protected boolean onFactoryGroupAdd(FactoryGroup group) {
		return group.isActive();
	}
	
	/**
	 * 
	 * @param item
	 * @param group
	 * @return true  -- item will be added
	 * 		   flase -- item will be abandoned
	 */
	protected boolean onFactoryItemAdd(FactoryItem item, FactoryGroup group){
		return item.isActive();
	}
	
	/**
	 * 
	 * @param group
	 * @return
	 */
	protected FactoryFragment createFactoryFragment(FactoryGroup group){
		return null;
	}
	
	public String getDeviceName(){
		return this.deviceName;
	}
	
	public String getDeviceBranch(){
		return this.deviceBranch;
	}
	
	public void onActivityCreate(Activity activity){
		
	}
	
	public abstract boolean startInspect();
	
	public abstract void stopInspect();

	public abstract IToolKit getToolKit();

	public abstract void onSystemBooted(Context context);
	
	public abstract boolean restoreSystem(Context context);
	
	public abstract boolean shutdownSystem(Context context);
}
