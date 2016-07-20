package com.eostek.scifly.devicemanager.manage.appmanagement;

import android.content.Context;

public class AppManagementConfig {
    private  final static String UPDATE_AUTO_CHECK_CONF = "update_auto_check";
    
    public static void setAutoUpdateCfg(Context context, String key, boolean value) {
        context.getSharedPreferences(UPDATE_AUTO_CHECK_CONF, Context.MODE_PRIVATE ).edit().putBoolean(key, value).commit();
    }
    
    //default value : auto update started
    public static  boolean getAutoUpdateCfg(Context context , String key){
        return context.getSharedPreferences(UPDATE_AUTO_CHECK_CONF,Context.MODE_PRIVATE ).getBoolean(key, true);
    }
}
