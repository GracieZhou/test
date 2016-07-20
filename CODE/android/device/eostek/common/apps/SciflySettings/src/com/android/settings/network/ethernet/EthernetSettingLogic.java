
package com.android.settings.network.ethernet;

/**
 * EthernetSettingLogic
 * main function get and save the information of ethernet.
 */
import java.net.Inet4Address;
import com.android.settings.network.NetworkSettingActivity;

import scifly.middleware.network.EthernetManagerGlobal;
import scifly.middleware.network.IpConfig;
import scifly.middleware.network.IpConfig.IpAssignment;
import scifly.middleware.network.StaticIpConfig;
import android.content.Context;
import android.net.LinkAddress;
import android.net.NetworkUtils;
import android.text.TextUtils;
import android.util.Log;

public class EthernetSettingLogic {

    private static final String TAG = "EthernetSetting";

    private Context mContext;

    public final static String DEFAULTIP = "0.0.0.0";

    public String mIpStr;

    public String mGatewayStr;

    public String mDnsStr;

    public String mSubnetMaskStr;

    public boolean isAutoGetIp = true;

    public EthernetManagerGlobal mEthernetManagerGlobal;

    EthernetDisplayInfo mDisplayInfo;

    public EthernetSettingLogic(NetworkSettingActivity activity) {
        Log.d(TAG, "enter into EthernetSettingLogic");
        mContext = activity;
        // init middleware
        mEthernetManagerGlobal = new EthernetManagerGlobal(mContext);
        Log.d(TAG, "<<<<<<<mEthernetManagerGlobal<<<<<<<<" + mEthernetManagerGlobal);
        processInfo();
    }

    /**
     * store the current wired information in the displayinfo
     */
    public void processInfo() {
        IpConfig ipConfig = mEthernetManagerGlobal.getConfiguration();
        if (ipConfig == null) {
            return;
        }

        mDisplayInfo = new EthernetDisplayInfo();
        isAutoGetIp = ipConfig.ipAssignment == IpAssignment.DHCP ? true : false;
        mDisplayInfo.setAutoIp(isAutoGetIp);

        StaticIpConfig staticIpConfig = ipConfig.getStaticIpConfig();
        if (staticIpConfig != null) {
            if (staticIpConfig.ipAddress != null) {
                mIpStr = staticIpConfig.ipAddress.getAddress().getHostAddress();
                mDisplayInfo.setIp(mIpStr);

                mSubnetMaskStr = prefixLengthToNetmask(staticIpConfig.ipAddress.getNetworkPrefixLength());
                mDisplayInfo.setNetmask(mSubnetMaskStr);
            }
            if (staticIpConfig.gateway != null) {
                mGatewayStr = staticIpConfig.gateway.getHostAddress();
                mDisplayInfo.setGateway(mGatewayStr);
            }

            if (staticIpConfig.dnsServers != null && staticIpConfig.dnsServers.size() > 0) {
                mDnsStr = staticIpConfig.dnsServers.get(0).getHostAddress();
                mDisplayInfo.setDNS1(mDnsStr);
            }
        }
        if (TextUtils.isEmpty(mIpStr)) {
            mIpStr = DEFAULTIP;
        }

        if (TextUtils.isEmpty(mGatewayStr)) {
            mGatewayStr = DEFAULTIP;
        }

        if (TextUtils.isEmpty(mDnsStr)) {
            mDnsStr = DEFAULTIP;
        }

        Log.i(TAG, "mIpStr " + mIpStr);
        Log.i(TAG, "mGatewayStr " + mGatewayStr);
        Log.i(TAG, "mDnsStr " + mDnsStr);
        Log.i(TAG, "mSubnetMaskStr " + mSubnetMaskStr);

    }

    /**
     * save the information of current network
     */
    public void saveEthernetConfig() {
        Log.d(TAG, "<<<<enter into saveEthernetConfig<<<<<<");
        if (mEthernetManagerGlobal.isEnabled()) {
            EthernetDisplayInfo displayInfo = new EthernetDisplayInfo();
            displayInfo.setAutoIp(isAutoGetIp);
            displayInfo.setIp(mIpStr);
            displayInfo.setNetmask(mSubnetMaskStr);
            displayInfo.setGateway(mGatewayStr);
            displayInfo.setDNS1(mDnsStr);
            Log.d(TAG, "<<<<need or not <<<<<<" + (mDisplayInfo != null && !mDisplayInfo.equals(displayInfo)));
            if (mDisplayInfo != null && !mDisplayInfo.equals(displayInfo)) {
                Log.d(TAG, "need to config!");
                connectEthernet(displayInfo);
            }
            return;
        }
        Log.w(TAG, "ethernet is disabled.");
    }

    /**
     * @param prefixLength
     * @return the string of network according to the length of network
     */
    private String prefixLengthToNetmask(int prefixLength) {
        if (prefixLength < 0 || prefixLength > 32) {
            Log.w(TAG, "Invalid prefix length (0 <= prefix <= 32)");
            throw new IllegalArgumentException("illegal argument prefixLength");
        }

        int value = 0xffffffff << (32 - prefixLength);
        int netmask = Integer.reverseBytes(value);

        StringBuffer sb = new StringBuffer();
        sb.append(String.valueOf(netmask & 0xff));
        sb.append('.');
        sb.append(String.valueOf((int) ((netmask >> 8) & 0xff)));
        sb.append('.');
        sb.append(String.valueOf((int) ((netmask >> 16) & 0xff)));
        sb.append('.');
        sb.append(String.valueOf((int) ((netmask >> 24) & 0xff)));

        return sb.toString();
    }

    /**
     * @param netmask
     * @return the length of netmask
     */
    private int netmaskToPrefixLength(String netmask) {
        if (TextUtils.isEmpty(netmask)) {
            return -1;
        }

        String[] tmp = netmask.split("\\.");
        int cnt = 0;
        for (String cell : tmp) {
            int i = Integer.parseInt(cell);
            cnt += Integer.bitCount(i);
        }

        return cnt;
    }

    /**
     * connect the current network by settings
     * 
     * @param displayInfo
     */
    private void connectEthernet(EthernetDisplayInfo displayInfo) {
        if (displayInfo == null) {
            Log.w(TAG, "illegal argument EthernetDisplayInfo");
            return;
        }
        Log.d(TAG, "displayInfo : " + displayInfo.toString());

        IpConfig ipConfig = new IpConfig();
        Log.d(TAG, "<<<<displayInfo.isAutoIp()<<<<" + displayInfo.isAutoIp());
        if (displayInfo.isAutoIp()) {
            ipConfig.ipAssignment = IpAssignment.DHCP;
            ipConfig.setStaticIpConfig(null);
        } else {
            ipConfig.ipAssignment = IpAssignment.STATIC;
            StaticIpConfig staticConfig = new StaticIpConfig();
            ipConfig.setStaticIpConfig(staticConfig);

            String ipAddr = displayInfo.getIp();
            if (TextUtils.isEmpty(ipAddr)) {
                Log.w(TAG, "ip is null.");
                return;
            }

            Inet4Address inetAddr = null;
            try {
                inetAddr = (Inet4Address) NetworkUtils.numericToInetAddress(ipAddr);
            } catch (IllegalArgumentException e) {
                Log.e(TAG, e.getMessage());
                return;
            }

            int networkPrefixLength = netmaskToPrefixLength(displayInfo.getNetmask());
            if (networkPrefixLength < 0 || networkPrefixLength > 32) {
                Log.w(TAG, "illegal argument netmask");
                return;
            }

            try {
                staticConfig.ipAddress = new LinkAddress(inetAddr, networkPrefixLength);
            } catch (NumberFormatException e) {
                Log.e(TAG, e.getMessage());
                return;
            }

            String gateway = displayInfo.getGateway();
            if (!TextUtils.isEmpty(gateway)) {
                try {
                    staticConfig.gateway = (Inet4Address) NetworkUtils.numericToInetAddress(gateway);
                } catch (IllegalArgumentException e) {
                    return;
                }
            }

            String dns1 = displayInfo.getDNS1();
            if (!TextUtils.isEmpty(dns1)) {
                try {
                    staticConfig.dnsServers.add((Inet4Address) NetworkUtils.numericToInetAddress(dns1));
                } catch (IllegalArgumentException e) {
                    return;
                }
            }
        }
            
        mEthernetManagerGlobal.setConfiguration(ipConfig);
    }
}
