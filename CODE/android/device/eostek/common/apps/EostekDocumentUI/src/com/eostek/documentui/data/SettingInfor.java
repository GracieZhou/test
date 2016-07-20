
package com.eostek.documentui.data;

import com.eostek.documentui.Constants;

public class SettingInfor {
    public String saveDir;

    public int downloadSpeedLimite;// KB为单位

    public int downloadNumber; // 默认3
    
    public boolean bDataCache=true;

    public SettingInfor() {
        super();
        downloadSpeedLimite = 100000;
        downloadNumber = 3;
        saveDir = Constants.DOWNLOAD_INNER_LOCATION;
        bDataCache=true;
    }

    @Override
    public String toString() {
        String str = "saveDir:" + saveDir + " \n";
        str = str + "downloadSpeedLimite:" + downloadSpeedLimite + "kB\n";
        str = str + "downloadNumber:" + downloadNumber + " \n";
        str = str + "bDataCache:" + bDataCache + " \n";
        return str;
    }
}
