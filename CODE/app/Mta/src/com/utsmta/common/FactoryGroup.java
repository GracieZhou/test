package com.utsmta.common;

import java.util.ArrayList;
import java.util.Properties;

import com.utsmta.common.FactoryItem;
import com.utsmta.utils.LogUtil;

public class FactoryGroup extends Properties {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	private static final String TAG = "FactoryGroup";
	
	private ArrayList<FactoryItem> items = new ArrayList<FactoryItem>();
	
	private String name = null;
	
	private boolean isActive = false;
	
	public FactoryGroup(String name){
		this.name = name;
	}
	
	public String getName(){
		return this.name;
	}
	
	public boolean isActive(){
		return this.isActive;
	}
	
	public void setActive(boolean active){
		this.isActive = active;
	}
	
	//for debug
	public void printAllItems(){
		for(FactoryItem item : items){
			LogUtil.d(TAG, "subitem : " + item.getName());
			item.printAllProperties();
		}
	}
	
	public void addItem(FactoryItem item){
		items.add(item);
	}
	
	public FactoryItem getItem(int index){
		FactoryItem item = null;
		if(index >= 0 && index < items.size()){
			item = items.get(index);
		}
		
		return item;
	}
	
	public FactoryItem getItem(String name){
		FactoryItem item = null;
		
		for(FactoryItem _item : items){
			if (_item.getName().equalsIgnoreCase(name)) {
				item = _item;
				break;
			}
		}
		
		return item;
	}
	
	public int getItemIndex(FactoryItem item){
		for(int i = 0; i < items.size(); ++i){
			if(items.get(i).getName().equals(item.getName())){
				return i;
			}
		}
		
		return -1;
	}
	
	public int getItemIndex(String name){
		for(int i = 0; i < items.size(); ++i){
			if(items.get(i).getName().equals(name)){
				return i;
			}
		}
		
		return -1;
	}
	
	public ArrayList<FactoryItem> getAllItems(){
		return items;
	}
}
