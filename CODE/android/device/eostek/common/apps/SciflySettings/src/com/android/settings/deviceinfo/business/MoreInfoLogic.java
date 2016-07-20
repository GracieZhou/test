
package com.android.settings.deviceinfo.business;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import android.os.Environment;
import android.os.SystemClock;
import android.text.TextUtils;

import com.android.settings.deviceinfo.DeviceInfoActivity;
import com.android.settings.util.Utils;

/**
 * @ClassName: MoreInfoLogic
 * @author: lucky.li
 * @date: 2015-8-27 下午4:07:05
 * @Copyright: Eostek Co., Ltd. Copyright , All rights reserved
 */
public class MoreInfoLogic {
    private static final String FILENAME_CPU_INFO = "/proc/cpuinfo";

    private static final String FILENAME_MEMERY_INFO = "/proc/meminfo";

    private DeviceInfoActivity mActivity;

    public MoreInfoLogic(DeviceInfoActivity activity) {
        this.mActivity = activity;
    }

    /**
     * @Title: getCpuName
     * @param: @return
     * @return: String
     * @throws
     */
    public String getCpuName() {
        try {
            String text = Utils.readLine(FILENAME_CPU_INFO);
            String[] array = text.split(":\\s+", 2);
            for (int i = 0; i < array.length; i++) {
            }
            return array[1];
        } catch (IOException e) {
            e.printStackTrace();
            return "Unavailable";
        }
    }

    public long getRomTotalSize() {
        File path = Environment.getDataDirectory();
        long fileTotalLength = path.getTotalSpace();
        return fileTotalLength;
    }

    public long getExternalSDTotalSize() {
        File path = Environment.getExternalStorageDirectory();
        long fileTotalLength = path.getTotalSpace();
        return fileTotalLength;
    }

    public long getSystemSpace() {
        File path = Environment.getRootDirectory();
        long fileTotalLength = path.getTotalSpace();
        return fileTotalLength;
    }

    /**
     * @Title: getTotalMemory
     * @Description: get the memory of RAM
     * @param: @return
     * @return: String
     * @throws
     */
    public String getTotalMemory() {
        String line;
        String[] arrayOfString;
        long memory = 0;
        FileReader localFileReader = null;
        BufferedReader localBufferedReader = null;

        try {
            localFileReader = new FileReader(FILENAME_MEMERY_INFO);
            localBufferedReader = new BufferedReader(localFileReader, 8192);
            // MemTotal: 1476828 kB
            line = localBufferedReader.readLine();

            arrayOfString = line.split(":");
            String[] sizeBuf = arrayOfString[1].trim().split(" ");

            memory = Integer.valueOf(sizeBuf[0]) * 1024;
        } catch (IOException e) {
            e.printStackTrace();
        } finally {
            if (localFileReader != null) {
                try {
                    localFileReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }

            if (localBufferedReader != null) {
                try {
                    localBufferedReader.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return Utils.formatStorageSize(mActivity, memory);
    }

    /**
     * @Title: updateTimes
     * @Description: formate the time to h:mm:ss
     * @param:
     * @return: void
     * @throws
     */
    public String updateTimes() {
        long ut = SystemClock.elapsedRealtime() / 1000;
        if (ut == 0) {
            ut = 1;
        }
        int s = (int) (ut % 60);
        int m = (int) ((ut / 60) % 60);
        int h = (int) ((ut / 3600));
        return h + ":" + pad(m) + ":" + pad(s);
    }

    /**
     * @Title: pad
     * @param: @param n
     * @param: @return
     * @return: String 1.....9--->01.....09;
     * @throws
     */
    private String pad(int n) {
        if (n >= 10) {
            return String.valueOf(n);
        } else {
            return "0" + String.valueOf(n);
        }
    }

    /**
     * @Title: getLocalEthernetMacAddress
     * @param: void
     * @return: String mac;
     * @throws
     */
    public String getLocalEthernetMacAddress() {
        String eth_mac = null;
        String wifi_mac = "";
        try {
            Enumeration<NetworkInterface> localEnumeration = NetworkInterface.getNetworkInterfaces();
            while (localEnumeration.hasMoreElements()) {
                NetworkInterface localNetworkInterface = localEnumeration.nextElement();
                String interfaceName = localNetworkInterface.getDisplayName();
                if (interfaceName == null) {
                    continue;
                }
                if (interfaceName.equals("eth0")) {
                    eth_mac = convertToMac(localNetworkInterface.getHardwareAddress());
                    if (eth_mac != null && eth_mac.startsWith("0:")) {
                        eth_mac = "0" + eth_mac;
                    }
                    if (TextUtils.isEmpty(eth_mac)) {
                        continue;
                    } else {
                        break;
                    }
                } else if (interfaceName.equals("wlan0")) {
                    wifi_mac = convertToMac(localNetworkInterface.getHardwareAddress());
                    if (wifi_mac != null && wifi_mac.startsWith("0:")) {
                        wifi_mac = "0" + wifi_mac;
                    }
                    if (TextUtils.isEmpty(wifi_mac)) {
                        continue;
                    } else {
                        if (eth_mac != null) {
                            break;
                        } else {
                            continue;
                        }
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        if (TextUtils.isEmpty(eth_mac)) {
            return wifi_mac;
        } else {
            return eth_mac;
        }
    }

    private String convertToMac(byte[] mac) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < mac.length; i++) {
            byte b = mac[i];
            int value = 0;
            if (b >= 0 && b <= 16) {
                value = b;
                sb.append("0" + Integer.toHexString(value));
            } else if (b > 16) {
                value = b;
                sb.append(Integer.toHexString(value));
            } else {
                value = 256 + b;
                sb.append(Integer.toHexString(value));
            }
            if (i != mac.length - 1) {
                sb.append(":");
            }
        }
        return sb.toString();
    }
}
