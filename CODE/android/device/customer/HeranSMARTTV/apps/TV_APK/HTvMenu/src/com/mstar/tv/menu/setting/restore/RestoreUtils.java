/**
 * 
 */

package com.mstar.tv.menu.setting.restore;

import java.util.Locale;

import scifly.permission.Permission;
import android.app.ActivityManagerNative;
import android.app.IActivityManager;
import android.app.backup.BackupManager;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.media.AudioManager;
import android.net.wifi.WifiManager;
import android.os.RemoteException;
import android.provider.Settings;
import android.util.Log;

import com.mstar.android.tv.TvAudioManager;
import com.mstar.android.tv.TvChannelManager;
import com.mstar.android.tv.TvCommonManager;
import com.mstar.android.tvapi.common.TvManager;
import com.mstar.android.tvapi.common.exception.TvCommonException;
import com.mstar.android.tvapi.common.vo.EnumProgramInfoType;
import com.mstar.android.tvapi.common.vo.ProgramInfo;
import com.mstar.android.tvapi.common.vo.ProgramInfoQueryCriteria;
import com.mstar.android.tvapi.common.vo.TvOsType.EnumInputSource;
import com.mstar.android.tvapi.common.vo.TvOsType.EnumLanguage;

/*
 * projectName： EOSTVMenu
 * moduleName： RestoreUtils.java
 * @author chadm.xiang
 * @version 1.0.0
 * @time  2014-3-11 下午9:18:10
 * @Copyright © 2013 Eos Inc.
 */

public class RestoreUtils {

    /**
     * reset the system volume to 20
     * 
     * @param context
     */
    public static void resetVolume(Context context) {
        AudioManager mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        mAudioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, 20, 0);
    }

    /**
     * reset the source to ATV and reset program to 13
     */
    public static void resetTVChannel() {
        TvCommonManager.getInstance().setInputSource(EnumInputSource.E_INPUT_SOURCE_ATV);
        ProgramInfo programInfo = getCurProgramInfo();
        Log.v("RestoreUtils", "programInfo.number = " + programInfo.number);
        try {
            if (TvManager.getInstance() != null) {
                TvManager.getInstance().getChannelManager().selectProgram(12, (short) programInfo.serviceType, 0x00);
            }
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
    }

    /**
     * Format SDcard clean wifi config
     */
    public static void cleanWifiConfig(Context mContext) {
        WifiManager mWifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        int wifiState = mWifiManager.getWifiState();
        if ((wifiState == WifiManager.WIFI_STATE_ENABLING) || (wifiState == WifiManager.WIFI_STATE_ENABLED)) {
            mWifiManager.setWifiEnabled(false);
        }
        Permission mPermission = new Permission("JChec3Rhci50di5tZW51anJt");
        mPermission.exec("busybox rm -rf /mnt/sdcard/userdata/*");
        mPermission.exec("busybox rm -rf /data/misc/wifi/*");
        mPermission.exec("busybox rm -rf /data/data/com.heran.launcher2/");
        mPermission.exec("busybox rm -rf /data/data/com.android.providers.settings/");
    }

    /**
     * get current program information.
     * 
     * @return
     */
    public static ProgramInfo getCurProgramInfo() {
        ProgramInfoQueryCriteria qc = new ProgramInfoQueryCriteria();
        ProgramInfo mCurInfo = TvChannelManager.getInstance().getProgramInfo(qc, EnumProgramInfoType.E_INFO_CURRENT);
        return mCurInfo;
    }

    public static void setLanguage(String counLanguage, Context context) {
        String locallan = Locale.getDefault().getLanguage();
        String localcountry = Locale.getDefault().getCountry();
        String language = counLanguage.substring(0, 2);
        String country = counLanguage.substring(3, 5);
        if ((locallan + "_" + localcountry).equals(counLanguage)) {
            Log.v("RestoreUtils", "do not need to change language");
            return;
        }
        Locale locale = new Locale(language, country);
        try {
            IActivityManager am = ActivityManagerNative.getDefault();
            Configuration config = am.getConfiguration();
            config.locale = locale;
            // setLocaleLanguage(locale,context);
            com.android.internal.app.LocalePicker.updateLocale(locale);
            // indicate this isn't some passing default - the user wants this
            // remembered
            config.userSetLocale = true;
            am.updateConfiguration(config);
            // Trigger the dirty bit for the Settings Provider.
            BackupManager.dataChanged("com.android.providers.settings");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private static void setLocaleLanguage(Locale locale, Context context) {
        int value = 0;
        String language = locale.getLanguage();
        if (language.equals("zh")) {
            value = EnumLanguage.E_CHINESE.ordinal();
        } else if (language.equals("en")) {
            value = EnumLanguage.E_ENGLISH.ordinal();
        } else {
            value = EnumLanguage.E_CHINESE.ordinal();
        }

        TvManager tvManager = TvManager.getInstance();
        try {
            Intent intent = new Intent("com.android.settings");
            intent.putExtra("lang", value);
            context.sendBroadcast(intent);
            tvManager.setLanguage(value);
        } catch (Exception e) {
        }
    }

    /**
     * reset TV setting data to default
     * 
     * @param context
     */
    public static void resetTvData(Context context) {
        resetTVChannel();
        resetVolume(context);
        setLanguage("zh_TW", context);
        Settings.System.putInt(context.getContentResolver(), "savemode", 0);
        try {
            TvManager.getInstance().setGpioDeviceStatus(0, false);
            TvManager.getInstance().setGpioDeviceStatus(57, true);
        } catch (TvCommonException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        TvAudioManager.getInstance().setAvcMode(true);
        Settings.System.putInt(context.getContentResolver(), "reset", 1);
    }

    /** MPEG noise reduction setting */
    public static enum EN_MS_MPEG_NR {
        // / MPEG noise reduction off
        MS_MPEG_NR_OFF,
        // / MPEG noise reduction low
        MS_MPEG_NR_LOW,
        // / MPEG noise reduction middle
        MS_MPEG_NR_MIDDLE,
        // / MPEG noise reduction high
        MS_MPEG_NR_HIGH,
        // / total mpeg noise reduction type number
        MS_MPEG_NR_NUM,
    }

}
