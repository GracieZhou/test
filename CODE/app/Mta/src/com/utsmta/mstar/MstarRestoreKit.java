package com.utsmta.mstar;

import java.util.Locale;

import com.utsmta.utils.LogUtil;

import android.app.ActivityManagerNative;
import android.app.IActivityManager;
import android.app.backup.BackupManager;
import android.content.Context;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.net.wifi.WifiManager;
import android.os.RemoteException;
import scifly.permission.Permission;

public class MstarRestoreKit {
    private static final String TAG = "MstarRestoreKit";

    // backup path
    public static final String DLP_DB_BACKUP_DIR = "/tvconfig/TvBackup/Database/";

    public static final String DLP_DB_FILE_DLP = "dlp.db";

    public static final String DLP_DB_FILE_DLP_JOURNAL = "dlp.db-journal";

    public static final String TV_DB_BACKUP_TV_DIR = "/tvconfig/TvBackup/Database/";

    public static final String TV_DB_FILE_USER_SETTING = "user_setting.db";

    public static final String TV_DB_FILE_USER_SETTING_JOURNAL = "user_setting.db-journal";

    public static final String TV_DB_BACKUP_FACTORY_DIR = "/tvconfig/TvBackup/Database/";

    public static final String TV_DB_FILE_FACTORY = "factory.db";

    public static final String TV_DB_FILE_FACTORY_JOURNAL = "factory.db-journal";

    public static final String TV_DB_DIR = "/tvdatabase/Database/";

    private Boolean isResetFactoryDB = false;

    private MstarRestoreKit() {
    }
    
    public static boolean sync(){
    	LogUtil.e(TAG, "sync");
    	
        Permission permission = new Permission("JChebS51dHNtdGEuYXBwanJt");
        String sync = "sync";
        
        return permission.exec(sync);
    }
    
    public static boolean restore(Context context){
        boolean result = false;
        long time = System.currentTimeMillis();
        
        resetVolume(context, 50);
        
        if(!cleanWifiConfig(context)){
        	LogUtil.d(TAG, "cleanWifiConfig error");	
        	return false;
        }  
        
       result = restoreFiles();

       return result;
    }

    private static boolean restoreFiles() {
    	if(!resetDB(TV_DB_BACKUP_TV_DIR, TV_DB_FILE_USER_SETTING) 
    			|| !resetDB(TV_DB_BACKUP_TV_DIR, TV_DB_FILE_USER_SETTING_JOURNAL)
    			|| !resetDB(TV_DB_BACKUP_FACTORY_DIR, TV_DB_FILE_FACTORY)
    			|| !resetDB(TV_DB_BACKUP_FACTORY_DIR, TV_DB_FILE_FACTORY_JOURNAL)
    			){
    		return false;
    	}    	   
        return true;
    }

    /**
     * @param dbDIR  <br/> /tvconfig/TvBackup/Database/ | /tvconfig/TvBackup/Database/ /tvdatabase/DatabaseBackup
     * @param dbname <br/> dlp.db, user_setting.db, factory.db
     * @return TRUE: success, FALSE: fail
     */
    public static boolean resetDB(String dbDIR, String dbname) {
    	LogUtil.d(TAG, "setLanguage"+dbDIR);
        boolean result = false;   
        
        Permission permission = new Permission("JChebS51dHNtdGEuYXBwanJt");
        String copyFile = "busybox cp " + dbDIR + dbname + " /tvdatabase/Database/";
        String chmod = "chmod 666 /tvdatabase/Database/" + dbname;
        
        if(permission.exec(copyFile) && permission.exec(chmod)){
        	result = true;
        }
        
        sync();
        
        return result;
    }
    
    /**
     * reset system Volume
     * @param context   
     * @param volume  int 20 35 50 ...    
     * @return TRUE: success, FALSE: fail
     */
    public static void resetVolume(Context context, int volume) {
        AudioManager audioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        audioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, volume, 0);
    }
    
    /**
     * Format SDcard clean wifi config
     */
    public static boolean cleanWifiConfig(Context mContext) {
        boolean result = false;
        
        WifiManager wifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        int wifiState = wifiManager.getWifiState();
        if ((wifiState == WifiManager.WIFI_STATE_ENABLING) || (wifiState == WifiManager.WIFI_STATE_ENABLED)) {
        	wifiManager.setWifiEnabled(false);
        }
        
        Permission permission = new Permission("JChebS51dHNtdGEuYXBwanJt");
        result = permission.exec("busybox rm -rf /mnt/sdcard/userdata/*");
        result = permission.exec("busybox rm -rf /data/misc/wifi/*");
        result = permission.exec("busybox rm -rf /data/data/com.android.providers.settings/");
        
        return result;
    }

    /**
     * set system Language
     * @param counLanguage       <br/> zh_CN /en_US /zh_TW
     * @param context   
     * @return TRUE: success, FALSE: fail
     */
    public static boolean setLanguage(String counLanguage, Context context) {

        String locallan = Locale.getDefault().getLanguage();
        String localcountry = Locale.getDefault().getCountry();
        String language = counLanguage.substring(0, 2);
        String country = counLanguage.substring(3, 5);
        if ((locallan + "_" + localcountry).equals(counLanguage)) {
        	LogUtil.d(TAG, "do not need to change language");
            return true;
        }
		
        Locale locale = new Locale(language, country);
        try {
            IActivityManager am = ActivityManagerNative.getDefault();
            Configuration config = am.getConfiguration();
            config.locale = locale;	
            com.android.internal.app.LocalePicker.updateLocale(locale);
            // indicate this isn't some passing default - the user wants this
            // remembered
            config.userSetLocale = true;
			
            am.updateConfiguration(config);
            // Trigger the dirty bit for the Settings Provider.
            BackupManager.dataChanged("com.android.providers.settings");	
        } catch (RemoteException e) {
            e.printStackTrace();
            LogUtil.d(TAG, "setLanguage error"+e.getMessage());	 	
            return false;
        }
       
       return true;
    }
}
