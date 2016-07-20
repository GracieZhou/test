
package com.eostek.mkeyeventservice;

import java.util.Locale;

import android.app.ActivityManagerNative;
import android.app.IActivityManager;
import android.app.backup.BackupManager;
import android.content.Context;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.net.wifi.WifiManager;
import android.os.RemoteException;
import android.util.Log;
import scifly.permission.Permission;

public class FastRestore {
    private static final String TAG = "FastRestore";

    // backup path
    private static final String TV_DB_BACKUP_TV_DIR = "/tvconfig/TvBackup/Database/";

    private static final String TV_DB_FILE_USER_SETTING = "user_setting.db";

    private static final String TV_DB_FILE_USER_SETTING_JOURNAL = "user_setting.db-journal";

    private static final String TV_DB_FILE_FACTORY = "factory.db";

    private static final String TV_DB_FILE_FACTORY_JOURNAL = "factory.db-journal";

    private Context mContext;
    
    public static Permission mPermission = new Permission("JCheZXZlbnRzZXJ2aWNlanJt");

    private static FastRestore single = null;

    public FastRestore(Context Context) {
        mContext = Context;
    }

    public synchronized static FastRestore getInstance(Context context) {
        if (single == null) {
            single = new FastRestore(context);
        }
        return single;
    }

    public boolean doRestore() {
        boolean result = false;
        long time = System.currentTimeMillis();

        resetVolume(mContext, 20);

        if (!setLanguage("zh_CN", mContext)) {
            Log.d(TAG, "setLanguage error");
            return false;
        }

        if (!cleanWifiConfig(mContext)) {
            Log.d(TAG, "cleanWifiConfig error");
            return false;
        }

        result = restoreFiles();
        Log.v(TAG, "resetTvData---time = " + (System.currentTimeMillis() - time));
        return result;
    }

    private boolean restoreFiles() {
        if (!resetDB(TV_DB_BACKUP_TV_DIR, TV_DB_FILE_USER_SETTING)
                || !resetDB(TV_DB_BACKUP_TV_DIR, TV_DB_FILE_USER_SETTING_JOURNAL)) {
            return false;
        }
        return true;
    }

    /**
     * @param dbDIR <br/>
     *            /tvconfig/TvBackup/Database/ | /tvconfig/TvBackup/Database/
     *            /tvdatabase/DatabaseBackup
     * @param dbname <br/>
     *             user_setting.db
     * @return TRUE: success, FALSE: fail
     */
    public static boolean resetDB(String dbDIR, String dbname) {
        Log.d(TAG, "setLanguage" + dbDIR);
        boolean result = false;
        String copyFile = "busybox cp " + dbDIR + dbname + " /tvdatabase/Database/";
        String chmod = "chmod 666 /tvdatabase/Database/" + dbname;
        result = mPermission.exec(copyFile);
        result = mPermission.exec(chmod);

        return result;
    }

    /**
     * reset system Volume
     * 
     * @param context
     * @param volume int 20 35 50 ...
     * @return TRUE: success, FALSE: fail
     */
    public static void resetVolume(Context context, int volume) {
        AudioManager mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        mAudioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, volume, 0);
    }

    /**
     * Format SDcard clean wifi config
     */
    public static boolean cleanWifiConfig(Context mContext) {
        boolean result = false;
        WifiManager mWifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        int wifiState = mWifiManager.getWifiState();
        if ((wifiState == WifiManager.WIFI_STATE_ENABLING) || (wifiState == WifiManager.WIFI_STATE_ENABLED)) {
            mWifiManager.setWifiEnabled(false);
        }
        Permission mPermission = new Permission("JCheZXZlbnRzZXJ2aWNlanJt");
        result = mPermission.exec("busybox rm -rf /mnt/sdcard/userdata/*");
        result = mPermission.exec("busybox rm -rf /data/misc/wifi/*");
        result = mPermission.exec("busybox rm -rf /data/data/com.android.providers.settings/");
        return result;
    }

    /**
     * set system Language
     * 
     * @param counLanguage <br/>
     *            zh_CN /en_US /zh_TW
     * @param context
     * @return TRUE: success, FALSE: fail
     */
    public boolean setLanguage(String counLanguage, Context context) {
        String locallan = Locale.getDefault().getLanguage();
        String localcountry = Locale.getDefault().getCountry();
        String language = counLanguage.substring(0, 2);
        String country = counLanguage.substring(3, 5);
        if ((locallan + "_" + localcountry).equals(counLanguage)) {
            Log.v("RestoreUtils", "do not need to change language");
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
            return false;
        }

        return true;
    }

}
