
package com.eostek.scifly.devicemanager.util;

import android.os.Environment;

public class Constants {
    public static final String DEVICEMANAGER_PACKAGE_NAME = "com.eostek.scifly.devicemanager";

    public static final String SNPLUS_CONFIG_PATH = "/system/etc/snplus_config.ini";

    public static final String CACHE_PATH_START_STR = "CACHE_PATH = ";

    public static final String CACHE_PATH_END_STR = ";";

    public static final String DATABASE_FILE_NAME = "filepath.db";

    public static final String DIR_EXTERNAL_ROOT = Environment.getExternalStorageDirectory().getAbsolutePath();

    public static final int DIR_EXTERNAL_ROOT_LENGTH = DIR_EXTERNAL_ROOT.length();

    public static final String SYSTEM_DIR_NAME = "system_dir";

    public static final String LOG_DIR = "/cache/log";

    public static final String RECOVERY_LOG_DIR = "/cache/recovery";

    public static final String QUERY_SOFTDETAIL_STR = "select apkname,filepath from softdetail"
            + " where filepath like ?";

    public static final String QUERY_CACHE_STR = "select package,filepath from cache" + " where filepath like ?";

    public static final String EXTERNAL_ROOT = "/mnt";

    public static final long ONE_GB = 1024 * 1024 * 1024;

    public static final String GB = "GB";

    public static final long HALF_A_HUNDRED_MB = 50 * 1024 * 1024;

    public static final long ONE_HUNDRED_MB = 100 * 1024 * 1024;

    public static final long ONE_MB = 1024 * 1024;

    public static final String MB = "MB";

    public static final long ONE_KB = 1024;

    public static final String KB = "KB";

    public static final String BYTE = "B";

    public static final int GARBAGE_EVENT_SCAN = 0;

    public static final int GARBAGE_EVENT_CANCEL = 1;

    public static final int GARBAGE_EVENT_CLEAR = 2;

    public static final int GARBAGE_EVENT_DONE = 3;

    public static final int GARBAGE_MSG_CACHE_AVAILABLE = 0;

    public static final int GARBAGE_MSG_UNINSTALL_AVAILABLE = 1;

    public static final int GARBAGE_MSG_ADS_AVAILABLE = 2;

    public static final int GARBAGE_MSG_APK_AVAILABLE = 3;

    public static final int GARBAGE_MSG_CLEAN_COMPLETED = 4;

    public static final int GARBAGE_MSG_BIG_FILE_AVAILABLE = 5;

    public static final long MIN_SPACE_TIME = 500;

    public static final long HALF_A_YEAR = 6 * 30 * 24 * 60 * 60 * 1000;
    
    public static final long TIME_TO_JUDGE_SUCESS = 1420027200000l;
    
    public static final String CURRENT_PATH = "/data/data/com.eostek.scifly.devicemanager" ;
    
    public static final String FILES_RELPATH =  CURRENT_PATH +"/files" ;
    
    public static final String FILES_CONFIG_NAME = "/appupdateinfo2.xml" ;
    
	public static final String FILES_APP_UPDATINFO = FILES_RELPATH + FILES_CONFIG_NAME ;

	public static final String SPCONFIG = "config" ;

	public static final String CB_AUTOUPDATE = "cb_autoupdate";
	
	public static final String DATACAHCE_DIRECTORY = "datacache";
	
	public static final String DOWNLOAD_DIRECTORY = "download";
	
	public static final String RECOMMEND_DOWNLOAD_DIRECTORY = "recommend_download";

	public static final String UPDATE_DOWNLOAD_DIRECTORY = "update_download";
	
	public static final String MSG_AUTO_UPDATE_TYPE = "request";
	
	public static final int MSG_AUTO_UPDATE_VALUE = 1;
	
	//ACTION
	
	public static final String MENUACTION = "android.intent.action.DELETE_SELECT_MENU";//MenuActivity
}

