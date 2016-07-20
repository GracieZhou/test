package com.eostek.scifly.devicemanager.service;

import java.util.Map;

import android.content.Context;

public class DownloadConfig {
	
	private  final static String DOWNLOAD_ID_MANAGER_CONF = "download_id_manager";
    
    public static void put(Context context, String key, long value) {
        context.getSharedPreferences(DOWNLOAD_ID_MANAGER_CONF, Context.MODE_PRIVATE ).edit().putLong(key, value).commit();
    }
    
    public static long get(Context context , String key, long defVal){
        return context.getSharedPreferences(DOWNLOAD_ID_MANAGER_CONF, Context.MODE_PRIVATE ).getLong(key, defVal);
    }
    
    public static void remove(Context context , String key){
        context.getSharedPreferences(DOWNLOAD_ID_MANAGER_CONF,Context.MODE_PRIVATE ).edit().remove(key).commit();
    }
    
    public static boolean containsKey(Context context , String key){
    	return context.getSharedPreferences(DOWNLOAD_ID_MANAGER_CONF,Context.MODE_PRIVATE ).contains(key);
    }
    
    
    public static Map<String, ?> getAll(Context context){
    	return context.getSharedPreferences(DOWNLOAD_ID_MANAGER_CONF,Context.MODE_PRIVATE ).getAll();
    }
}
