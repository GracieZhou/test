
package com.eostek.sciflyui.voicecontroller.command;

import java.util.List;
import java.util.Locale;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.hardware.input.IInputManager;
import android.hardware.input.InputManager;
import android.media.AudioManager;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemClock;
import android.util.Log;
import android.view.KeyEvent;

/**
 * CommandDispatcher used to dispatch voice command.
 */
public class CommandDispatcher {

    private static final int VOLUME_SET_MUTE = 0;

    private static final int VOLUME_SET_RAISE = 1;

    private static final int VOLUME_SET_LOWER = -1;

    private static final int VOLUME_SET_RECOVER = 2;

    private static final int VOLUME_STEP_LENGTH = 5;

    private static final String TAG = "CommandDispatcher";

    protected static final int SEND_KEY_DELAY = 0;

    /**
     * Scifly video package name.
     */
    public static String SCIFLY_VIDEO_PACKAGE = "com.eostek.scifly.video";

    /**
     * Scifly video search activity class name.
     */
    public static String SCIFLY_VIDEO_CLASSNAME = "com.eostek.scifly.video.search.SearchVideoListActivity";

    /**
     * Scifly video application name.
     */
    public static String SCIFLY_VIDEO_APPNAME = "赛飞视频";

    /**
     * Search activity extra string.
     */
    public static String SERARCH_EXTRA = "search";

    private Context mContext;

    private AudioManager mAudioManager;

    /**
     * CommandDispatcher constructor. CommandDispatcher used to dispatch voice
     * command.
     * 
     * @param mContext set Context.
     */
    public CommandDispatcher(Context mContext) {
        this.mContext = mContext;
    }

    /**
     * This method is to dispatch command.
     * 
     * @param command Command object used to lauch application or operate
     *            device.
     * @return Return result code whether dispatch command successed or failed.
     *         PS:result code is not used yet.
     */
    public int dispatchCommand(Command command) {

        int result = 0;
        /** Unkown result process order:apk->video */
        if (command.getResultCode() == Command.RESULT_CODE_UNKNOWN) {
            // 未知类型的命令,我们首先把其中文字提取出来,在整个设备已安装的应用中搜索,
            // 检查是否有名称和文字一样的应用,如果有,则启动该应用.如果没有,则直接把文字发到赛飞视频搜索界面进行搜索.
            dispatchQueryAppOrVideo(command);
            return result;
        }

        switch (command.getCommandType()) {
        /**
         * when unkonw type command comes ,dispathch it as launching app command
         * or scifly video searching command.
         */
            case Command.TYPE_DEFAULT:
            case Command.TYPE_QUERY_APP:
                dispatchQueryAppOrVideo(command);
                break;
            case Command.TYPE_LAUNCH_APP:
                // 启动应用
                dispatchLaunchApp(command);
                break;
            case Command.TYPE_DEVICE_CONTROL:
                // 设备控制
                dispatchDeviceControl(command);
                break;
        }

        return result;
    }

    // 在整个设备已安装的应用中搜索,检查是否有名称和文字一样的应用,如果有,则启动该应用.
    // 如果没有,则直接把文字发到赛飞视频搜索界面进行搜索
    private void dispatchQueryAppOrVideo(Command command) {

        PackageManager packageManage = mContext.getPackageManager();

        Intent mainIntent = new Intent(Intent.ACTION_MAIN, null);
        mainIntent.addCategory(Intent.CATEGORY_LAUNCHER);

        List<ResolveInfo> resolveInfos = packageManage.queryIntentActivities(mainIntent,
                PackageManager.GET_UNINSTALLED_PACKAGES);

        for (ResolveInfo reInfo : resolveInfos) {
            String appLabel = (String) reInfo.loadLabel(packageManage); // 获得应用程序的Label

            /**
             * remove space and transfer to lower case to make it easier to
             * match result
             */
            appLabel = appLabel.replaceAll(" ", "").toLowerCase(Locale.US);
            String targetAppLabel = "";
            if (command.getApplicationName() != null) {
                targetAppLabel = command.getApplicationName().replaceAll(" ", "").toLowerCase(Locale.US);
            } else {
                targetAppLabel = command.getText().replaceAll(" ", "").toLowerCase(Locale.US);
            }
            // Log.e(TAG, "targetAppLabel " + targetAppLabel + " appLabel " +
            // appLabel);
            // Log.i(TAG, "activityName " + activityName + " pkgName " + pkgName
            // + " appLabel " + appLabel);
            if (appLabel.contains((targetAppLabel)) || targetAppLabel.contains(appLabel)) {

                String activityName = reInfo.activityInfo.name; // 获得该应用程序的启动Activity的name
                String pkgName = reInfo.activityInfo.packageName; // 获得应用程序的包名

                command.setApplicationPackageName(pkgName);
                command.setApplicationClassName(activityName);
            }

        }
        if (command.getApplicationPackageName() == null || command.getApplicationClassName() == null) {
            // 搜索完毕后,如果未获得有效包名或者类名,则直接进入赛飞视频搜索界面.
            dispatchLaunchVideo(command);
        } else {
            // 成功搜索到应用了,尝试启动.
            dispatchLaunchApp(command);
        }

    }

    // 分派启动应用命令
    private int dispatchLaunchApp(Command command) {

        Log.i(TAG, "launch " + command.getApplicationPackageName() + " " + command.getApplicationClassName());

        int result = 0;

        if (command.getApplicationPackageName() == null || command.getApplicationClassName() == null) {
            // 启动应用失败.
            Log.i(TAG, "unable to find app " + command.getApplicationName());
            return result;

        }

        Intent intent = new Intent();
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            // 启动应用操作.
            intent.setClassName(command.getApplicationPackageName(), command.getApplicationClassName());
            mContext.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        } catch (NullPointerException e) {
            e.printStackTrace();
        }

        return result;

    }

    // 启动赛飞视频搜索界面.
    public void dispatchLaunchVideo(Command command) {

        String searchExtraString;

        /**
         * maybe this command is come from app search request,but we can't find
         * matched app,so it comes here,we send application name as the search
         * string to scifly video,so we can get better result.
         */
        if (command.getApplicationName() != null) {
            searchExtraString = command.getApplicationName();
        } else {
            searchExtraString = command.getText();
        }

        command.setOperation(Command.OPERATION_QUERY);
        command.setApplicationName(SCIFLY_VIDEO_APPNAME);

        Intent intent = new Intent();
        intent.setClassName(SCIFLY_VIDEO_PACKAGE, SCIFLY_VIDEO_CLASSNAME);
        intent.putExtra(SERARCH_EXTRA, searchExtraString);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
        try {
            mContext.startActivity(intent);
        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    // 分派设备控制命令.
    private void dispatchDeviceControl(Command command) {

        if (Command.OPERATION_DEVICE_CONTROL_SHUTDWON.equals(command.getCustomCommand())) {
            // 关机操作
            deviceShutdown();
        } else if (Command.OPERATION_DEVICE_CONTROL_VOLUME_MUTE.equals(command.getCustomCommand())) {
            // 静音操作
            deviceVolumeSet(VOLUME_SET_MUTE);
        } else if (Command.OPERATION_DEVICE_CONTROL_VOLUME_PLUS.equals(command.getCustomCommand())) {
            // 音量加操作
            deviceVolumeSet(VOLUME_SET_RAISE);
        } else if (Command.OPERATION_DEVICE_CONTROL_VOLUME_MINUS.equals(command.getCustomCommand())) {
            // 音量减操作
            deviceVolumeSet(VOLUME_SET_LOWER);
        } else if (Command.OPERATION_DEVICE_CONTROL_VOLUME_RECOVERY.equals(command.getCustomCommand())) {
            // 音量恢复操作
            deviceVolumeSet(VOLUME_SET_RECOVER);
        } else if (Command.OPERATION_DEVICE_CONTROL_CHANNEL_PLUS.equals(command.getCustomCommand())) {
            // 上一频道操作
            deviceKeyPress(KeyEvent.KEYCODE_CHANNEL_UP);
        } else if (Command.OPERATION_DEVICE_CONTROL_CHANNEL_MINUS.equals(command.getCustomCommand())) {
            // 下一频道操作
            deviceKeyPress(KeyEvent.KEYCODE_CHANNEL_DOWN);
        } else if (Command.OPERATION_DEVICE_CONTROL_KEY_BACK.equals(command.getCustomCommand())) {
            // 模拟按返回键
            deviceKeyPress(KeyEvent.KEYCODE_BACK);
        } else if (Command.OPERATION_DEVICE_CONTROL_KEY_HOME.equals(command.getCustomCommand())) {
            // 模拟按主页键
            deviceKeyPress(KeyEvent.KEYCODE_HOME);
        } else if (Command.OPERATION_DEVICE_CONTROL_KEY_ENTER.equals(command.getCustomCommand())) {
            // 模拟按确定键
            deviceKeyPress(KeyEvent.KEYCODE_ENTER);
        }

    }

    // 模拟按键
    private void deviceKeyPress(int keyCode) {
        Log.i(TAG, "deviceKeyPress " + keyCode);
        sendKey(keyCode);
    }

    private static int mPreviousVolume;

    // 设备音量设置,每次加/减步长为5.
    private void deviceVolumeSet(int i) {
        Log.i(TAG, "deviceVolumeSet " + i);

        if (mAudioManager == null) {
            mAudioManager = (AudioManager) mContext.getSystemService(Context.AUDIO_SERVICE);
        }

        int currentVolume = mAudioManager.getStreamVolume(AudioManager.STREAM_MUSIC);
        int maxVolume = mAudioManager.getStreamMaxVolume(AudioManager.STREAM_MUSIC);

        Log.i(TAG, "maxVolume = " + maxVolume);
        Log.i(TAG, "currentVolume = " + maxVolume);

        switch (i) {
            case VOLUME_SET_MUTE:

                if (currentVolume > 0) {
                    mPreviousVolume = currentVolume;
                }

                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, 0, 1);
                break;
            case VOLUME_SET_RAISE:

                mPreviousVolume = currentVolume;

                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume + VOLUME_STEP_LENGTH, 1);
                break;
            case VOLUME_SET_LOWER:

                mPreviousVolume = currentVolume;

                mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, currentVolume - VOLUME_STEP_LENGTH, 1);
                break;
            case VOLUME_SET_RECOVER:
                Log.i(TAG, "mPreviousVolume = " + mPreviousVolume);
                if (mPreviousVolume > 0) {
                    mAudioManager.setStreamVolume(AudioManager.STREAM_MUSIC, mPreviousVolume, 1);
                }

                break;
        }
    }

    // 关机操作,需要是系统应用才能成功关机.
    private void deviceShutdown() {
        Log.i(TAG, "deviceShutdown");
        Intent intent = new Intent(Intent.ACTION_REQUEST_SHUTDOWN);

        intent.putExtra(Intent.EXTRA_KEY_CONFIRM, false);

        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

        mContext.startActivity(intent);

    }

    IInputManager sInstance;

    // 模拟按键,使用了隐藏的API,需要打设备签名才能成功运行.
    private void sendKey(int keyCode) {
        if (sInstance == null) {
            IBinder b = ServiceManager.getService(Context.INPUT_SERVICE);
            sInstance = IInputManager.Stub.asInterface(b);
        }

        long now = SystemClock.uptimeMillis();
        KeyEvent down = new KeyEvent(now, now, KeyEvent.ACTION_DOWN, keyCode, 0);
        KeyEvent up = new KeyEvent(now, now, KeyEvent.ACTION_UP, keyCode, 0);

        try {
            sInstance.injectInputEvent(down, InputManager.INJECT_INPUT_EVENT_MODE_ASYNC);
            sInstance.injectInputEvent(up, InputManager.INJECT_INPUT_EVENT_MODE_ASYNC);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

}
