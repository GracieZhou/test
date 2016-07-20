
package com.android.settings.deviceinfo.business;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;

import scifly.device.Device;
import android.os.Build;
import android.os.SystemProperties;
import android.text.TextUtils;

import com.android.settings.BaseSettingActivity;
import com.android.settings.util.Utils;

/**
 * @ClassName: DeviceInfoLogic
 * @Description:DeviceInfoLogic
 * @author: lucky.li
 * @date: 2015-8-27 下午4:29:29
 * @Copyright: Eostek Co., Ltd. Copyright , All rights reserved
 */
public class DeviceInfoLogic {

    private static final String FILENAME_MODEL_NUMBER = "/tvconfig/config/sys.ini";

    private static final String FILENAME_MSV = "/sys/board_properties/soc/msv";

    private BaseSettingActivity mActivity;

    /**
     * @Title: DeviceInfoLogic
     * @param: @param activity
     * @throws
     */
    public DeviceInfoLogic(BaseSettingActivity activity) {
        this.mActivity = activity;

    }

    public String getModel() {
        return Build.MODEL + getMsvSuffix();
    }

    public static String getVersion() {
        String hasPQVer = getSysValueFromConfig("PQVer");
        if (TextUtils.isEmpty(hasPQVer)) {
            return SystemProperties.get("ro.scifly.version.alias", "");
        } else {
            return SystemProperties.get("ro.scifly.version.alias", "") + "." + hasPQVer;
        }
    }

    /**
     * @Title: getDeviceName
     * @Description: get the name of device
     * @param: @return
     * @return: String
     * @throws
     */
    public String getDeviceName() {
        return Device.getDeviceName(mActivity);
    }

    /**
     * @Title: getSerialNumber
     * @param: @return
     * @return: String
     * @throws
     */
    public String getSerialNumber() {
        return Device.getDeviceSN();
    }

    /**
     * @Title: getBuildNumber
     * @param: @return
     * @return: String
     * @throws
     */
    public static String getBuildNumber() {
        String hasPQVer = getSysValueFromConfig("PQVer");
        if (TextUtils.isEmpty(hasPQVer)) {
            return SystemProperties.get("ro.build.version.incremental", "");
        } else {
            return SystemProperties.get("ro.build.version.incremental", "") + "." + hasPQVer;
        }
    }

    /**
     * Returns " (ENGINEERING)" if the msv file has a zero value, else returns
     * "".
     * 
     * @return a string to append to the model number description.
     */
    private String getMsvSuffix() {
        // Production devices should have a non-zero value. If we can't read it,
        // assume it's a
        // production device so that we don't accidentally show that it's an
        // ENGINEERING device.
        try {
            String msv = Utils.readLine(FILENAME_MSV);
            // Parse as a hex number. If it evaluates to a zero, then it's an
            // engineering build.
            if (Long.parseLong(msv, 16) == 0) {
                return " (ENGINEERING)";
            }
        } catch (IOException ioe) {
            // Fail quietly, as the file may not exist on some devices.
        } catch (NumberFormatException nfe) {
            // Fail quietly, returning empty string should be sufficient
        }
        return "";
    }

    /**
     * @Title: getSysValueFromConfig
     * @param: @param attriubute
     * @param: @return
     * @return: String
     * @throws
     */
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
