
package com.eostek.scifly.devicemanager.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.Locale;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.pm.ApplicationInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.content.res.AssetManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.text.TextUtils;

@SuppressLint("NewApi") 
public class Util {
    private static final String TAG = Util.class.getSimpleName();
    
    private static List<String> systemDirList;

    public static void intentForward(Context context, Class<?> forwardClass) {
        Intent intent = new Intent();
        intent.setClass(context, forwardClass);
        context.startActivity(intent);
    }

    public static String formatKB2MB(int kb) {
        DecimalFormat decimalFormat = new DecimalFormat("0.00");
        float mb = (float) kb / 1024;
        return decimalFormat.format(mb) + " MB";
    }

    public static String getSnplusCachePath() {
        BufferedReader br = null;
        String line = null;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(Constants.SNPLUS_CONFIG_PATH)));
            while ((line = br.readLine()) != null) {
                if (line.contains(Constants.CACHE_PATH_START_STR)) {
                    int start = line.indexOf(Constants.CACHE_PATH_START_STR) + Constants.CACHE_PATH_START_STR.length();
                    int end = line.indexOf(Constants.CACHE_PATH_END_STR);
                    return line.substring(start, end);
                }
            }
        } catch (FileNotFoundException e1) {
            Debug.e(TAG, e1.getLocalizedMessage());
        } catch (IOException e) {
            Debug.e(TAG, e.getLocalizedMessage());
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    Debug.e(TAG, e.getLocalizedMessage());
                }
            }
        }

        return null;
    }

    /**
     * Check if this file a system directory.
     * 
     * @param context The application context.
     * @param file File that need to be checked.
     * @return True if this file is a system directory; otherwise false.
     */
    public static boolean isSystemDir(Context context, File file) {
        if (file.isFile()) {
            return false;
        }
        if (!file.getAbsolutePath().contains(Constants.DIR_EXTERNAL_ROOT)) {
            return false;
        }
        if (systemDirList == null) {
            systemDirList = new ArrayList<String>();
            // Get system directories from file system_dir
            AssetManager am = context.getAssets();
            try {
                BufferedReader br = new BufferedReader(new InputStreamReader(am.open(Constants.SYSTEM_DIR_NAME)));
                String str = "";
                Debug.d(TAG, "----------------System directory begin----------------");
                while ((str = br.readLine()) != null) {
                    Debug.d(TAG, "Directory: [" + str + "]");
                    systemDirList.add(str);
                }
                Debug.d(TAG, "-----------------System directory end-----------------");
                br.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }
        String subString = file.getAbsolutePath().substring(Constants.DIR_EXTERNAL_ROOT_LENGTH);
        return systemDirList.contains(subString);
    }

    /**
     * Convert storage size to String with GB, MB, KB or B.
     * 
     * @param number Storage size in byte.
     * @return String represent the storage size with unit.
     */
    public static String sizeToString(long size) {
        if (size > Constants.ONE_GB) {
            long remainder = size % Constants.ONE_GB / Constants.ONE_MB / 100;
            if (remainder > 0) {
                return String.valueOf(size / Constants.ONE_GB) + "." + remainder + Constants.GB;
            }
            return String.valueOf(size / Constants.ONE_GB) + Constants.GB;
        } else if (size > Constants.ONE_MB) {
            return String.valueOf(size / Constants.ONE_MB) + Constants.MB;
        } else if (size > Constants.ONE_KB) {
            return String.valueOf(size / Constants.ONE_KB) + Constants.KB;
        } else {
            return String.valueOf(size) + Constants.BYTE;
        }
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
            value = String.format(Locale.ROOT, "%.2f", result);
        } else if (result < 10) {
            value = String.format(Locale.ROOT, "%.1f", result);
        } else if (result < 100) {
            value = String.format(Locale.ROOT, "%.0f", result);
        } else {
            value = String.format(Locale.ROOT, "%.0f", result);
        }
        
        double convertValue = Math.ceil(Double.parseDouble(value));
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
     * Check the network is avaliable or not
     * 
     * @param context
     * @return
     */
    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getApplicationContext()
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
    
    public static boolean checkApkExist(Context context, String packageName) {
        ApplicationInfo info = null;
        if (TextUtils.isEmpty(packageName))
            return false;
        
        try {
            info = context.getPackageManager().getApplicationInfo(packageName,
                    PackageManager.GET_UNINSTALLED_PACKAGES);
            if(info != null)
                return true;
            else
                return false;
        } catch (NameNotFoundException e) {
            return false;
        }
    }

}
