package com.utsmta.common;

import android.app.Activity;
import android.content.Intent;
import android.os.UserHandle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

public class MtaPagerView {
	private boolean created = false;
	
	protected View view = null;
	
	protected Activity activity = null;
	
	private Object lock = new Object();
	
	private boolean isAdded = false;
	
	public MtaPagerView(Activity activity) {
		this.activity = activity;
	}

	public View getView(){
		synchronized (lock) {
			if(!created){
				this.view = onCreateView(LayoutInflater.from(this.activity));
				created = true;
			}			
		}
		
		return this.view;
	}
	
	public void createView(){
		synchronized (lock) {
			if(!created){
				this.view = onCreateView(LayoutInflater.from(this.activity));
				created = true;
			}			
		}
	}
	
	protected View onCreateView(LayoutInflater inflater){
		return null;
	}
	
	protected void notifyUiUpdate(){
		Intent intent = new Intent("mta.update.ui");
		activity.sendBroadcastAsUser(intent, UserHandle.OWNER);
	}
	
	public void onItemSelected(FactoryItem item){
		
	}
	
	public void show(ViewGroup container){
		if(view == null){
			createView();
		}
		
		if(view != null && !isAdded){
			container.addView(view);
			onShown();	
			isAdded = true;
		}	
	}
	
	public void hide(ViewGroup container){
		if(view != null && isAdded){
			container.removeView(view);	
			onHiden();
			isAdded = false;
		}
	}
	
	public boolean isShown(){
		return isAdded;
	}
	
	protected void onShown(){
		
	}
	
	protected void onHiden(){
		
	}
}
