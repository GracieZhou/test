
package com.eostek.mkeyeventservice;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;
import java.util.List;

import org.apache.http.conn.util.InetAddressUtils;

import com.mstar.android.tvapi.common.TvManager;
import com.mstar.android.tvapi.common.exception.TvCommonException;
import com.mstar.android.tvapi.factory.vo.EnumScreenMute;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningTaskInfo;
import android.content.Context;
import android.hardware.input.InputManager;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.text.TextUtils;
import android.util.Log;
import android.view.InputDevice;
import android.view.KeyCharacterMap;
import android.view.KeyEvent;
import scifly.view.KeyEventExtra;

public class Utils {
    private static final String FILENAME_MODEL_NUMBER = "/tvconfig/config/sys.ini";

    static final String TAG = "HandlerVirtualEvent";

    public static void HandlerVirtualEvent(int keyCode) {
        switch (keyCode) {
            case KeyEventExtra.KEYCODE_MSTAR_INFO:
                sendKeyDown(KeyEvent.KEYCODE_INFO);
                break;
            case KeyEventExtra.KEYCODE_MSTAR_POWER:
                sendKeyDown(KeyEvent.KEYCODE_POWER);
                break;
            case KeyEventExtra.KEYCODE_MSTAR_EXIT:
                sendInputEventCmd(KeyEvent.KEYCODE_BACK);
                break;
            case KeyEventExtra.KEYCODE_MSTAR_MENU:
                sendKeyDown(KeyEvent.KEYCODE_MENU);
                break;
            case KeyEventExtra.KEYCODE_MSTAR_HOME:
                sendInputEventCmd(KeyEvent.KEYCODE_HOME);
                break;
            case KeyEventExtra.KEYCODE_MSTAR_SOURCE:
                sendKeyDown(KeyEvent.KEYCODE_TV_INPUT);
                break;
            case KeyEventExtra.KEYCODE_MSTAR_NUMB_1:
                sendKeyDown(KeyEvent.KEYCODE_1);
                break;
            case KeyEventExtra.KEYCODE_MSTAR_NUMB_2:
                sendKeyDown(KeyEvent.KEYCODE_2);
                break;
            case KeyEventExtra.KEYCODE_MSTAR_NUMB_3:
                sendKeyDown(KeyEvent.KEYCODE_3);
                break;
            case KeyEventExtra.KEYCODE_MSTAR_NUMB_4:
                sendKeyDown(KeyEvent.KEYCODE_4);
                break;
            case KeyEventExtra.KEYCODE_MSTAR_NUMB_5:
                sendKeyDown(KeyEvent.KEYCODE_5);
                break;
            case KeyEventExtra.KEYCODE_MSTAR_NUMB_6:
                sendKeyDown(KeyEvent.KEYCODE_6);
                break;
            case KeyEventExtra.KEYCODE_MSTAR_NUMB_7:
                sendKeyDown(KeyEvent.KEYCODE_7);
                break;
            case KeyEventExtra.KEYCODE_MSTAR_NUMB_8:
                sendKeyDown(KeyEvent.KEYCODE_8);
                break;
            case KeyEventExtra.KEYCODE_MSTAR_NUMB_9:
                sendKeyDown(KeyEvent.KEYCODE_9);
                break;
            case KeyEventExtra.KEYCODE_MSTAR_NUMB_0:
                sendKeyDown(KeyEvent.KEYCODE_0);
                break;
            case KeyEventExtra.KEYCODE_MSTAR_CH_PLUS:
                sendKeyDown(KeyEvent.KEYCODE_CHANNEL_UP);
                break;
            case KeyEventExtra.KEYCODE_MSTAR_CH_MINUS:
                sendKeyDown(KeyEvent.KEYCODE_CHANNEL_DOWN);
                break;
            case KeyEventExtra.KEYCODE_MSTAR_UP:
                sendKeyDown(KeyEvent.KEYCODE_DPAD_UP);
                break;
            case KeyEventExtra.KEYCODE_MSTAR_DOWN:
                sendKeyDown(KeyEvent.KEYCODE_DPAD_DOWN);
                break;
            case KeyEventExtra.KEYCODE_MSTAR_LEFTD:
                sendKeyDown(KeyEvent.KEYCODE_DPAD_LEFT);
                break;
            case KeyEventExtra.KEYCODE_MSTAR_RIGHT:
                sendKeyDown(KeyEvent.KEYCODE_DPAD_RIGHT);
                break;
            case KeyEventExtra.KEYCODE_MSTAR_SELECT:
                sendInputEventCmd(KeyEvent.KEYCODE_ENTER);
                break;
            default:
                break;
        }
    }
    
    private static void sendInputEventCmd(int keycode) {
        FastRestore.mPermission.exec("input keyevent " + keycode);
    }

    private static void sendKeyDown(int keyCode) {
        long now = SystemClock.uptimeMillis();
        injectKeyEvent(new KeyEvent(now, now, KeyEvent.ACTION_DOWN, keyCode, 0, 0, KeyCharacterMap.VIRTUAL_KEYBOARD, 0,
                0, InputDevice.SOURCE_UNKNOWN));
    }

    private static void injectKeyEvent(KeyEvent event) {
        Log.i(TAG, "injectKeyEvent: " + event);
        InputManager.getInstance().injectInputEvent(event, InputManager.INJECT_INPUT_EVENT_MODE_ASYNC);
    }

    public static boolean setTestPattern(int testPatternMode) {
        try {
            TvManager.getInstance().getFactoryManager().setVideoTestPattern(EnumScreenMute.values()[testPatternMode]);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        return true;
    }

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

    public static String getLocalMac() {
        String mac = "";
        try {
            mac = TvManager.getInstance().getEnvironment("ethaddr");
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        return mac;
    }

    public static String getLocalHostIp() {
        String ipaddress = "";
        try {
            Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces();
            while (en.hasMoreElements()) {
                NetworkInterface nif = en.nextElement();
                Enumeration<InetAddress> inet = nif.getInetAddresses();
                while (inet.hasMoreElements()) {
                    InetAddress ip = inet.nextElement();
                    if (!ip.isLoopbackAddress() && InetAddressUtils.isIPv4Address(ip.getHostAddress())) {
                        return ipaddress = ip.getHostAddress();
                    }
                }

            }
        } catch (SocketException e) {
            Log.e("feige", "获取本地ip地址失败");
            e.printStackTrace();
        }
        return ipaddress;
    }

    public static String getHdcpKey() {
        String HdcpKey = "";
        try {
            HdcpKey = TvManager.getInstance().getEnvironment("HKEY");
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        return HdcpKey;
    }

    public static String getVersion() {
        String hasPQVer = getSysValueFromConfig("PQVer");
        if (TextUtils.isEmpty(hasPQVer)) {
            return SystemProperties.get("ro.build.version.incremental", "");
        } else {
            return SystemProperties.get("ro.build.version.incremental", "") + "." + hasPQVer;
        }
    }

    private static String getSysValueFromConfig(String attriubute) {
        File file = new File(FILENAME_MODEL_NUMBER);
        String line = null;
        String attributeValue = "";
        int position;
        FileInputStream mStream = null;
        BufferedReader reader = null;
        if (file.exists()) {
            try {
                mStream = new FileInputStream(file);
                reader = new BufferedReader(new InputStreamReader(mStream));
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith(attriubute)) {
                        if (line.contains(";")) {
                            position = line.indexOf(";");
                        } else {
                            position = line.length() + 1;
                        }
                        position = line.indexOf(";");
                        String[] tmpStrings = line.subSequence(0, position).toString().split("=");
                        attributeValue = tmpStrings[1].trim();
                    }
                }
            } catch (Exception e) {
                e.printStackTrace();
            } finally {
                if (mStream != null) {
                    try {
                        mStream.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }

                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
            return attributeValue;
        }
        return attributeValue;
    }

}
