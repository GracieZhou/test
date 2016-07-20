
package com.android.settings.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.ActivityManager;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.util.Log;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.android.internal.inputmethod.InputMethodUtils;
import com.mstar.android.pppoe.PppoeManager;

/**
 * provide some methods for share.
 */
@SuppressLint("SimpleDateFormat")
public class Utils {
    public static final String SCIFLY_PLATFORM_DONGLE = "dongle";

    public static final String SCIFLY_PLATFORM_TV = "tv";

    public static final String SCIFLY_PLATFORM_BOX = "box";

    public static final String SCIFLY_PLATFORM_PROJECTOR = "projector";

    private static Toast mToast;

    /**
     * return the default location for app install from auto:0, internal:1,
     * sdcard:2.
     * 
     * @param context
     * @return the sign of the DefaultInstallLocation
     * @throws SettingNotFoundException
     */
    public static int getDefaultInstallLocation(Context context) throws SettingNotFoundException {
        return Settings.Global.getInt(context.getContentResolver(), Settings.Global.DEFAULT_INSTALL_LOCATION);
    }

    /**
     * return the current InputMethodId.
     * 
     * @param context
     * @return String
     */
    public static String getDefaultInputMethodId(Context context) {
        String defaultMethodId = Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.DEFAULT_INPUT_METHOD);
        return defaultMethodId == null ? "" : defaultMethodId;
    }

    /**
     * @param context
     * @return the label of the defaultInputMethod.
     */
    public static String getDefaultInputMethodLabel(Context context) {
        String defaultMethodId = getDefaultInputMethodId(context);
        String defaultMethodLabel = null;
        for (InputMethodInfo info : getInputMethodInfoList(context)) {
            if (info.getId().equals(defaultMethodId)) {
                defaultMethodLabel = info.loadLabel(context.getPackageManager()).toString();
                break;
            }
        }
        return defaultMethodLabel;
    }

    /**
     * @param context
     * @return all the inputMethod installed includes the third-party software.
     */
    public static List<InputMethodInfo> getInputMethodInfoList(Context context) {
        List<InputMethodInfo> inputMethods;
        InputMethodManager imm = (InputMethodManager) context.getSystemService(Context.INPUT_METHOD_SERVICE);
        inputMethods = imm.getInputMethodList();
        return inputMethods;
    }

    /**
     * @param context
     * @return only the system InputMethods will be there.
     */
    public static List<InputMethodInfo> getSystemInputMethodInfoList(Context context) {
        List<InputMethodInfo> inputMethods = getInputMethodInfoList(context);
        List<InputMethodInfo> systemInputMethods = new ArrayList<InputMethodInfo>();
        for (InputMethodInfo info : inputMethods) {
            if (InputMethodUtils.isSystemIme(info)) {
                systemInputMethods.add(info);
            }
        }
        return systemInputMethods;
    }

    /**
     * Reads a line from the specified file.
     * 
     * @param filename the file to read from
     * @return the first line, if any.
     * @throws IOException if the file couldn't be read
     */
    public static String readLine(String filename) throws IOException {
        BufferedReader reader = new BufferedReader(new FileReader(filename), 256);
        try {
            return reader.readLine();
        } finally {
            reader.close();
        }
    }

    /**
     * Returns true if Monkey is running.
     */
    public static boolean isMonkeyRunning() {
        return ActivityManager.isUserAMonkey();
    }

    /**
     * whether this the languageCode of item in localeList is unique.
     * 
     * @param languageCode
     * @param locales
     * @return
     */
    public static boolean hasOnlyOneLanguageInstance(String languageCode, String[] locales) {
        int count = 0;
        for (String localeCode : locales) {
            if (localeCode.length() > 2 && localeCode.startsWith(languageCode)) {
                count++;
                if (count > 1) {
                    return false;
                }
            }
        }
        return count == 1;
    }

    /**
     * to formate the storagesize.
     * 
     * @param context,number
     * @return the result by format
     */
    public static String formatStorageSize(Context context, long number) {
        if (context == null) {
            return "";
        }

        float result = number;
        int suffix = com.android.internal.R.string.byteShort;
        if (result > 900) {
            suffix = com.android.internal.R.string.kilobyteShort;
            result = result / 1024;
        }
        if (result > 900) {
            suffix = com.android.internal.R.string.megabyteShort;
            result = result / 1024;
        }
        if (result <= 512) {
            result = result / 1024;
        } else {
            suffix = com.android.internal.R.string.gigabyteShort;
            result = result / 1024;
        }
        if (result > 900) {
            suffix = com.android.internal.R.string.terabyteShort;
            result = result / 1024;
        }
        if (result > 900) {
            suffix = com.android.internal.R.string.petabyteShort;
            result = result / 1024;
        }
        String value;
        if (result < 1) {
            value = formatedouble(result, 2);
        } else if (result < 10) {
            value = formatedouble(result, 1);
        } else if (result < 100) {
            value = formatedouble(result, 0);
        } else {
            value = formatedouble(result, 0);
        }
        double convertValue = Double.parseDouble(value);
        if (convertValue <= 0.5) {
            convertValue = 512;
        } else if (convertValue <= 1.0) {
            convertValue = 1.0;
        } else {
            convertValue = Math.ceil(convertValue);
        }
        return context.getResources().getString(com.android.internal.R.string.fileSizeSuffix, convertValue,
                context.getString(suffix));
    }

    /**
     * to formate the storagesize.
     * 
     * @param context,number
     * @return the result by format
     */
    public static String formatStorageSizeLong(Context context, long number) {
        if (context == null) {
            return "";
        }

        float result = number;
        int suffix = com.android.internal.R.string.byteShort;
        if (result > 900) {
            suffix = com.android.internal.R.string.kilobyteShort;
            result = result / 1024;
        }
        if (result > 900) {
            suffix = com.android.internal.R.string.megabyteShort;
            result = result / 1024;
        }
        if (result > 512) {
            suffix = com.android.internal.R.string.gigabyteShort;
            result = result / 1024;
        }
        if (result > 900) {
            suffix = com.android.internal.R.string.terabyteShort;
            result = result / 1024;
        }
        if (result > 900) {
            suffix = com.android.internal.R.string.petabyteShort;
            result = result / 1024;
        }
        String value;
        if (result < 1) {
            value = formatedouble(result, 2);
        } else if (result < 10) {
            value = formatedouble(result, 1);
        } else if (result < 100) {
            value = formatedouble(result, 0);
        } else {
            value = formatedouble(result, 0);
        }
        double convertValue = Math.ceil(Double.parseDouble(value));
        if (convertValue <= 0.5) {
            convertValue = 512;
        } else if (convertValue <= 1.0) {
            convertValue = 1.0;
        } else if (1.0 < convertValue && convertValue <= 2.0) {
            convertValue = 2.0;
        } else if (2.0 < convertValue && convertValue <= 4.0) {
            convertValue = 4.0;
        } else if (4.0 < convertValue && convertValue <= 8.0) {
            convertValue = 8.0;
        } else if (8.0 < convertValue && convertValue <= 16.0) {
            convertValue = 16.0;
        }
        return context.getResources().getString(com.android.internal.R.string.fileSizeSuffix, convertValue,
                context.getString(suffix));
    }

    /**
     * the boolean of whether this String contains chinese.
     * 
     * @param String
     * @return boolean
     */
    public static boolean isChineseEncoding(String str) {
        boolean ret = false;
        for (int i = 0; i < str.length(); i++) {
            ret = str.substring(i, i + 1).matches("[\\u4e00-\\u9fa5]+");
            if (ret == true) {
                break;
            }
        }
        return ret;
    }

    /**
     * used to toast message.
     * 
     * @param Context,message
     * @return void
     */
    public static void showToast(Context context, int id) {
        String message = context.getResources().getString(id);
        if (mToast == null) {
            mToast = Toast.makeText(context, message, Toast.LENGTH_SHORT);
        } else {
            mToast.setText(message);
        }
        mToast.show();
    }

    /**
     * to judge whether connect network current.
     * 
     * @param Context
     * @return boolean
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager == null) {
            return false;
        } else {
            NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();
            if (networkInfo != null && networkInfo.length > 0) {
                for (int i = 0; i < networkInfo.length; i++) {
                    if (networkInfo[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
                }
            }
        }
        return false;
    }

    /**
     * used to skip between activity.
     * 
     * @param context
     * @param forwardClass
     */
    public static void intentForward(Context context, Class<?> forwardClass) {
        Intent intent = new Intent();
        intent.setClass(context, forwardClass);
        context.startActivity(intent);
    }

    /**
     * @Title: longParseDate.
     * @Description: 将秒转成年份(这里用一句话描述这个方法的作用).
     * @param: @param pushTime
     * @param: @return.
     * @return: String.
     * @throws
     */
    public static String longParseDate(long pushTime) {
        SimpleDateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        Date date = new Date(pushTime * 1000);
        return df.format(date);
    }

    public static String getCurrentWifiName(Context context) {
        ConnectivityManager cm = (ConnectivityManager) context.getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo wifiStateInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        String name;
        if (wifiStateInfo == null || !wifiStateInfo.isAvailable() || wifiStateInfo.getState() == State.DISCONNECTED) {
            name = " ";
        } else {
            WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
            WifiInfo wifiInfo = wifiManager.getConnectionInfo();
            name = wifiInfo.getSSID();
        }
        if (name.contains("\"")) {
            name = name.replace("\"", "");
        }
        return name;
    }

    public static String formatedouble(float result, int n) {
        String resultbyte = result + "";
        String s = "";
        int index = resultbyte.indexOf(".");
        if (index < 0) {
            s = resultbyte;
            if (n > 0) {
                s = resultbyte + ".000".substring(0, (n + 1));
            }
        } else if (index > 0) {
            int number = 1;
            for (int i = 0; i < n; i++) {
                number *= 10;
            }
            s = number * Double.parseDouble(resultbyte) + "";
            s = s.substring(0, index + n);
            if (n > 0) {
                s = Double.parseDouble(s) / number + "";
            }
            if (s.contains(".")) {
                if (s.split("\\.")[1].length() < n) {
                    s += "0";
                }
            }
        }
        return s;
    }
    public static boolean isPppoeConnected(Context context) {
        PppoeManager mPppoeManager = null;
        try {
            mPppoeManager = PppoeManager.getInstance(context);
        } catch (Exception e) {
            e.printStackTrace();
        }
        if (mPppoeManager!=null&&mPppoeManager.getPppoeStatus()!=null&&mPppoeManager.getPppoeStatus().equals(mPppoeManager.PPPOE_STATE_CONNECT)) {
            return true;
        } else
            return false;
    }
}
