
package com.android.settings.deviceinfo;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.InputStreamReader;
import java.io.IOException;

import com.android.settings.util.Utils;

import android.os.Build;
import android.util.Log;
import android.view.View;
import scifly.device.Device;

public class DeviceInfoLogic {
    
    private static final String FILENAME_MODEL_NUMBER = "/tvconfig/config/sys.ini";

    private static final String FILENAME_MSV = "/sys/board_properties/soc/msv";

    private DeviceInfoActivity mActivity;

    public DeviceInfoLogic(DeviceInfoActivity activity) {
        this.mActivity = activity;

    }

    public String getModel() {
        return Build.MODEL + getMsvSuffix();
    }

    public String getProductModel() {
        return getSysValueFromConfig("ProductModel");
    }

    public String getDeviceName() {
        return Device.getDeviceName(mActivity);
    }

    public static String getBuildNumber() {
        if (Build.DEVICE.equalsIgnoreCase("Leader")
				|| Build.DEVICE.equalsIgnoreCase("soniq")){
        	return Build.VERSION.INCREMENTAL+"."+getSysValueFromConfig("PQVer");
        }else{
        		return Build.VERSION.INCREMENTAL;
        }
    }

    public String getSerialNumber() {
        return Device.getDeviceSN();
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

    private static String getSysValueFromConfig(String attriubute) {
        File file = new File(FILENAME_MODEL_NUMBER);
        String line = null;
        String attributeValue = "";
        int position;
        if (file.exists()) {
            try {
                FileInputStream mStream = new FileInputStream(file);
                BufferedReader reader = new BufferedReader(new InputStreamReader(mStream));
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
                reader.close();
                mStream.close();
            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return attributeValue;
        }
        return attributeValue;
    }
}
