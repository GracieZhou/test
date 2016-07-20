
package com.android.settings.util;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;
import android.app.ActivityManager;
import android.content.Context;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.view.inputmethod.InputMethodInfo;
import android.view.inputmethod.InputMethodManager;

import com.android.internal.inputmethod.InputMethodUtils;

public class Utils {
    public static final String SCIFLY_PLATFORM_DONGLE = "dongle";

    public static final String SCIFLY_PLATFORM_TV = "tv";

    public static final String SCIFLY_PLATFORM_BOX = "box";

    public static final String SCIFLY_PLATFORM_PROJECTOR = "projector";

    /**
     * return the default location for app install from auto:0 internal:1
     * sdcard:2
     * 
     * @param context
     * @return
     * @throws SettingNotFoundException
     */
    public static int getDefaultInstallLocation(Context context) throws SettingNotFoundException {
        return Settings.Global.getInt(context.getContentResolver(), Settings.Global.DEFAULT_INSTALL_LOCATION);
    }

    public static String getDefaultInputMethodId(Context context) {
        String defaultMethodId = Settings.Secure.getString(context.getContentResolver(),
                Settings.Secure.DEFAULT_INPUT_METHOD);
        return defaultMethodId == null ? "" : defaultMethodId;
    }

    /**
     * @param context
     * @return the label of the defaultInputMethod
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
     * @return only the system InputMethods will be there
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

    public static Locale parseLanguage(String localeStr) {
        String spilt = "_";
        String[] parts = localeStr.split(spilt);
        Locale loc = null;
        switch (parts.length) {
            case 1:
                loc = new Locale(parts[0]);
                break;
            case 2:
                loc = new Locale(parts[0], parts[1]);
                break;
            default:
                break;
        }
        return loc;
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
     * whether this the languageCode of item in localeList is unique
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
            value = String.format("%.2f", result);
        } else if (result < 10) {
            value = String.format("%.1f", result);
        } else if (result < 100) {
            value = String.format("%.0f", result);
        } else {
            value = String.format("%.0f", result);
        }
        double convertValue = Math.ceil(Double.parseDouble(value));
        if(convertValue<=0.5){
        	  convertValue=512;
        }
        else if(convertValue<=1.0){
            convertValue=1.0;
        }else  if(1.0<convertValue&&convertValue<=2.0){
            convertValue=2.0;
        }else if(2.0<convertValue&&convertValue<=4.0){
            convertValue=4.0;
        }else if(4.0<convertValue&&convertValue<=8.0){
            convertValue=8.0;
        }else if(8.0<convertValue&&convertValue<=16.0){
            convertValue=16.0;
        }
        return context.getResources().getString(com.android.internal.R.string.fileSizeSuffix, convertValue,
                context.getString(suffix));
    }
    /**
     * the boolean of whether this String contains chinese
     * @param String
     * @return boolean
     * 
     */
    public static boolean isChineseEncoding(String str){
        boolean ret=false;
        for(int i=0;i<str.length();i++){
            ret=str.substring(i,   i+1).matches("[\\u4e00-\\u9fa5]+");
            if(ret==true){
                break;
            }
        }
        return ret;
    }
    /**
     * the boolean of whether this String is Chinese-simple
     * @param String
     * @return boolean
     * 
     */
    public static boolean isChineseSimple(String str){
        boolean ret=false;
        String encode = "GB2312";
        try {
            if (str.equals(new String(str.getBytes(encode), encode))) {
                ret=true;
            }else{
                ret=false;
            }
        } catch (Exception exception3) {
        }
        return ret;
    }
}
