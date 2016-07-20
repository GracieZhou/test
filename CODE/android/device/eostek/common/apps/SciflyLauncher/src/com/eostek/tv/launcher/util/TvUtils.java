
package com.eostek.tv.launcher.util;

import java.util.List;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.Cursor;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.util.Log;

import com.eostek.tv.launcher.R;
import com.eostek.tv.launcher.HomeApplication;
import com.mstar.android.tv.TvChannelManager;
import com.mstar.android.tv.TvCommonManager;
import com.mstar.android.tvapi.common.TvManager;
import com.mstar.android.tvapi.common.exception.TvCommonException;
import com.mstar.android.tvapi.common.vo.EnumProgramInfoType;
import com.mstar.android.tvapi.common.vo.EnumScalerWindow;
import com.mstar.android.tvapi.common.vo.ProgramInfo;
import com.mstar.android.tvapi.common.vo.ProgramInfoQueryCriteria;
import com.mstar.android.tvapi.common.vo.MuteType.EnumMuteType;
import com.mstar.android.tvapi.common.vo.TvOsType.EnumInputSource;
import com.mstar.android.tvapi.common.vo.VideoWindowType;

public final class TvUtils {

    private final static String TAG = TvUtils.class.getSimpleName();

    private static final Object LOCK_OBJECT = new Object();

    private TvUtils() {
    }

    /**
     * Set fullscreen
     */
    public static void setFullscale() {
        try {
            VideoWindowType videoWindowType = new VideoWindowType();
            videoWindowType.height = 0xFFFF;
            videoWindowType.width = 0xFFFF;
            videoWindowType.x = 0xFFFF;
            videoWindowType.y = 0xFFFF;
            if (TvManager.getInstance() != null) {
                TvManager.getInstance().getPictureManager().selectWindow(EnumScalerWindow.E_MAIN_WINDOW);
                TvManager.getInstance().getPictureManager().setDisplayWindow(videoWindowType);
                TvManager.getInstance().getPictureManager().scaleWindow();
            }
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
    }

    /**
     * Set small screen
     */
    public static void setSmallscale() {
		if (!"com.eostek.tv.launcher.HomeActivity".equals(getCurrentActivity(HomeApplication.getInstance()))) {
            Log.d(TAG, "not in launcher ,no need to change scale ");
            return;
        }
        try {
            VideoWindowType videoWindowType = new VideoWindowType();
            Resources instance = HomeApplication.getInstance().getResources();
            int width = 0;
            int height = 0;
            if (Build.VERSION.SDK_INT > 20) {
                width = instance.getInteger(R.integer.video_window_width_828);
                height = instance.getInteger(R.integer.video_window_height_828);
            } else {
                width = instance.getInteger(R.integer.video_window_width);
                height = instance.getInteger(R.integer.video_window_height);
            }
            // if the sccale is small ,just return
            if (videoWindowType.height == height && videoWindowType.width == width) {
                Log.v("TvUtil", "already in small scale,no need to set again!");
                return;
            }
            videoWindowType.height = height;
            videoWindowType.width = width;
            if (Build.VERSION.SDK_INT > 20) {
                videoWindowType.x = instance.getInteger(R.integer.video_window_x_828);
                videoWindowType.y = instance.getInteger(R.integer.video_window_y_828);
            } else {
                videoWindowType.x = instance.getInteger(R.integer.video_window_x);
                videoWindowType.y = instance.getInteger(R.integer.video_window_y);
            }
            Log.v(TAG, "x=" + videoWindowType.x + " ; y = " + videoWindowType.y + " ; height = "
                    + videoWindowType.height + " ; width = " + videoWindowType.width);
            if (TvManager.getInstance() != null) {
                TvManager.getInstance().getPictureManager().selectWindow(EnumScalerWindow.E_MAIN_WINDOW);
                TvManager.getInstance().getPictureManager().setDisplayWindow(videoWindowType);
                TvManager.getInstance().getPictureManager().scaleWindow();
            }
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
    }

    /**
     * switch program
     * 
     * @param u32Number
     * @param u8ServiceType
     * @return true if program select success,else false
     */
    public static boolean programSel(int u32Number, int u8ServiceType) {
        EnumInputSource currentSource;
        currentSource = TvCommonManager.getInstance().getCurrentInputSource();
        // when the current source is storage,return false
        if (currentSource == EnumInputSource.E_INPUT_SOURCE_STORAGE) {
            return false;
        }
        try {
            if (TvManager.getInstance() != null) {
                TvManager.getInstance().getChannelManager().selectProgram(u32Number, (short) u8ServiceType, 0x00);
            }
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * query the current input source
     * 
     * @return InputSourceType
     */
    public static int queryCurInputSrc(Context context) {
        int value = 0;
        Cursor cursor = context.getContentResolver().query(Uri.parse("content://mstar.tv.usersetting/systemsetting"),
                null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            value = cursor.getInt(cursor.getColumnIndex("enInputSourceType"));
        }
        if (cursor != null) {
            cursor.close();
        }
        return value;
    }

    /**
     * Get the current channel information
     * 
     * @return ProgramInfo
     */
    public static ProgramInfo getCurrProgramInfo() {
        ProgramInfoQueryCriteria qc = new ProgramInfoQueryCriteria();
        return TvChannelManager.getInstance().getProgramInfo(qc, EnumProgramInfoType.E_INFO_CURRENT);
    }

    /**
     * get the current top activity name
     * 
     * @param context
     * @return The name of top activity in activity stack
     */
    public static String getCurrentActivity(Context context) {
        ActivityManager activityManager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningTaskInfo> forGroundActivity = activityManager.getRunningTasks(1);
        RunningTaskInfo currentActivity;
        currentActivity = forGroundActivity.get(0);
        String activityName = currentActivity.topActivity.getClassName();
        if (activityName == null) {
            return "";
        }
        return activityName;
    }

    /**
     * get the current source Position
     * 
     * @param void
     * @return int
     */
    public static int getCurInputSourcePosition(Context context) {
        EnumInputSource curSource = TvCommonManager.getInstance().getCurrentInputSource();
        if (curSource == EnumInputSource.E_INPUT_SOURCE_STORAGE) {
            curSource = EnumInputSource.values()[TvUtils.queryCurInputSrc(context)];
        }
        return getCurInputSourcePosition(curSource);
    }

    public static int getCurInputSourcePosition(EnumInputSource curSource) {
        int curIndex = 0;
        switch (curSource) {
            case E_INPUT_SOURCE_ATV:
                curIndex = 0;
                break;
            case E_INPUT_SOURCE_DTV:
                curIndex = 1;
                break;
            case E_INPUT_SOURCE_HDMI:
                curIndex = 2;
                break;
            case E_INPUT_SOURCE_HDMI2:
                curIndex = 3;
                break;
            case E_INPUT_SOURCE_HDMI3:
                curIndex = 4;
                break;
            case E_INPUT_SOURCE_CVBS:
                curIndex = 5;
                break;
            case E_INPUT_SOURCE_YPBPR:
                curIndex = 6;
                break;
            case E_INPUT_SOURCE_VGA:
                curIndex = 7;
                break;
            default:
                break;
        }
        return curIndex;
    }

    /**
     * getCurInputSource
     * 
     * @param void
     * @return EnumInputSource
     */
    public static EnumInputSource getCurInputSource(int position) {
        EnumInputSource curSource = EnumInputSource.E_INPUT_SOURCE_NONE;
        switch (position) {
            case 0:
                curSource = EnumInputSource.E_INPUT_SOURCE_ATV;
                break;
            case 1:
                curSource = EnumInputSource.E_INPUT_SOURCE_DTV;
                break;
            case 2:
                curSource = EnumInputSource.E_INPUT_SOURCE_HDMI;
                break;
            case 3:
                curSource = EnumInputSource.E_INPUT_SOURCE_HDMI2;
                break;
            case 4:
                curSource = EnumInputSource.E_INPUT_SOURCE_HDMI3;
                break;
            case 5:
                curSource = EnumInputSource.E_INPUT_SOURCE_CVBS;
                break;
            case 6:
                curSource = EnumInputSource.E_INPUT_SOURCE_YPBPR;
                break;
            case 7:
                curSource = EnumInputSource.E_INPUT_SOURCE_VGA;
                break;
            default:
                break;
        }
        return curSource;
    }

    public static void setInputToStorage(final String pkgName) {
        synchronized (LOCK_OBJECT) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    // set the source to storage when start apk except tv player
                    if (HomeApplication.isHasTVModule()
                            && !pkgName.equals(LConstants.TV_PLAYER_PKG)
                            && TvCommonManager.getInstance().getCurrentInputSource() != EnumInputSource.E_INPUT_SOURCE_STORAGE) {
                        TvCommonManager.getInstance().setInputSource(EnumInputSource.E_INPUT_SOURCE_STORAGE.ordinal());
                    }
                }
            }).start();
        }
    }

    public static void startTV(Context context) {
        Intent intent = new Intent(LConstants.TV_PLAYER_ACTION);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        context.startActivity(intent);
    }

    /**
     * set the source sound to mute,only used for move left or right page to
     * avoid voice.In other
     * 
     * @param context
     * @param value True to enable mute,false disableMute
     */
    public static void pageChangeMute(Context context, boolean value) {
        
        boolean isMute = UIUtil.isMasterMute(context);
        Log.v(TAG, "setMute isMute = " + isMute + "; value = " + value);
        com.mstar.android.tvapi.common.AudioManager am = TvManager.getInstance().getAudioManager();
        if (am != null) {
            try {
                // only when the system is not mute,can disable mute
                if (!value && !isMute) {
                    am.disableMute(EnumMuteType.E_MUTE_ALL);
                } else {
                    am.enableMute(EnumMuteType.E_MUTE_ALL);
                }
            } catch (TvCommonException e) {
                e.printStackTrace();
            }
        }
        
    	
    	setMute(context, value);
    }

    /**
     * set mute
     * 
     * @param context
     * @param value True to enable mute,false disableMute
     */
    public static void setMute(Context context, boolean value) {
    	
    	ProgramInfo pinfo = getCurrProgramInfo();
        int temporaryunlock = Settings.System.getInt(context.getContentResolver(), "temporaryunlock", 0);
        boolean isTemporaryunlock = temporaryunlock == 1;
        Log.d(TAG, "temporaryunlock is ："+temporaryunlock);
        if (pinfo.isLock &&isTemporaryunlock){
            Log.d(TAG, "pinfo is Lock");
            value = true;
        }
		
        boolean isMute = UIUtil.isMasterMute(context);
        Log.v(TAG, "disableMuteAll isMute = " + isMute + "; value = " + value);
        // if the system is mute,do nothing
        if (isMute) {
            return;
        }
        com.mstar.android.tvapi.common.AudioManager am = TvManager.getInstance().getAudioManager();
        if (am != null) {
            try {
                if (value) {
                    //am.enableMute(EnumMuteType.E_MUTE_ALL);
                } else {
                    am.disableMute(EnumMuteType.E_MUTE_ALL);
                }
            } catch (TvCommonException e) {
                e.printStackTrace();
            }
        }
    }

    /**
     * to reture whether the current source is storage
     * 
     * @return true if the current source is storage,else false
     */
    public static boolean isStorage() {
        boolean storage = false;
        int curSource = TvCommonManager.getInstance().getCurrentInputSource().ordinal();
        if (curSource == EnumInputSource.E_INPUT_SOURCE_STORAGE.ordinal()) {
            storage = true;
        }
        return storage;
    }
}
