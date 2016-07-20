
package scifly.middleware.network.mstar.kitkat;

import java.net.Inet4Address;

import scifly.middleware.network.IEthernetManager;
import scifly.middleware.network.IpConfig;
import scifly.middleware.network.StaticIpConfig;
import scifly.middleware.network.IpConfig.IpAssignment;


import android.content.Context;
import android.net.LinkAddress;
import android.net.NetworkUtils;
import android.text.TextUtils;
import android.util.Log;

import com.mstar.android.ethernet.EthernetDevInfo;
import com.mstar.android.ethernet.EthernetManager;

public class EthernetManagerImpl implements IEthernetManager {

    private static final String TAG = "EthernetManagerImpl@kitkat";

    private static EthernetManager mEthernetManager;

    public EthernetManagerImpl(Context context) {
        mEthernetManager = EthernetManager.getInstance();
    }

    public IpConfig getConfiguration() {
        if (!isEnabled()) {
            return null;
        }

        EthernetDevInfo devInfo = mEthernetManager.getSavedConfig();
        if (devInfo == null) {
            return null;
        }

        // construct IpConfig according EthernetDevInfo
        return getConfiguration(devInfo);
    }

    public void setConfiguration(IpConfig config) {
        if (config == null) {
            throw new IllegalArgumentException("illegal argument IpConfig");
        }

        EthernetDevInfo devInfo = new EthernetDevInfo();
        // FIXME eth0/eth1.../ethn
        devInfo.setIfName("eth0");
        if (config.ipAssignment == IpAssignment.DHCP) {
            devInfo.setConnectMode(EthernetDevInfo.ETHERNET_CONN_MODE_DHCP);
            devInfo.setIpAddress(null);
            devInfo.setNetMask(null);
            devInfo.setRouteAddr(null);
            devInfo.setDnsAddr(null);
            devInfo.setDns2Addr(null);
        } else {
            StaticIpConfig staticIpConfig = config.getStaticIpConfig();

            devInfo.setConnectMode(EthernetDevInfo.ETHERNET_CONN_MODE_MANUAL);
            devInfo.setIpAddress(staticIpConfig.ipAddress.getAddress().getHostAddress());
            devInfo.setNetMask(prefixToNetmask(staticIpConfig.ipAddress.getNetworkPrefixLength()));
            devInfo.setRouteAddr(staticIpConfig.gateway.getHostAddress());
            int N = staticIpConfig.dnsServers.size();
            if (N == 1) {
                devInfo.setDnsAddr(staticIpConfig.dnsServers.get(0).getHostAddress());
            } else if (N >= 2) {
                devInfo.setDnsAddr(staticIpConfig.dnsServers.get(0).getHostAddress());
                devInfo.setDns2Addr(staticIpConfig.dnsServers.get(1).getHostAddress());
            }
        }

        mEthernetManager.updateDevInfo(devInfo);
    }

    public boolean isEnabled() {
        return (mEthernetManager.getState() == EthernetManager.ETHERNET_STATE_ENABLED);
    }

    public void setEnabled(boolean enable) {
        mEthernetManager.setEnabled(enable);
    }

    // ------------ data transform ------------//
    private IpConfig getConfiguration(EthernetDevInfo devInfo) {
        IpConfig config = new IpConfig();
        // connect mode
        if (EthernetDevInfo.ETHERNET_CONN_MODE_MANUAL.equals(devInfo.getConnectMode())) {
            config.ipAssignment = IpConfig.IpAssignment.STATIC;
        } else if (EthernetDevInfo.ETHERNET_CONN_MODE_DHCP.equals(devInfo.getConnectMode())) {
            config.ipAssignment = IpConfig.IpAssignment.DHCP;
        } else {
            config.ipAssignment = IpConfig.IpAssignment.UNASSIGNED;
        }

        // ip addr
        StaticIpConfig staticIpConfig = new StaticIpConfig();
        config.setStaticIpConfig(staticIpConfig);

        // FIXME AdvancedWifiOptionsFlow EthernetSettingsDialog NetworkActivity
        String ipAddr = devInfo.getIpAddress();
        if (TextUtils.isEmpty(ipAddr)) {
            Log.w(TAG, "ip is null");
            return config;
        }

        Inet4Address inetAddr = null;
        try {
            inetAddr = (Inet4Address) NetworkUtils.numericToInetAddress(ipAddr);
        } catch (IllegalArgumentException e) {
            return config;
        }

        int networkPrefixLength = netmaskToPrefixLength(devInfo.getNetMask());
        if (networkPrefixLength < 0 || networkPrefixLength > 32) {
            return config;
        }
        staticIpConfig.ipAddress = new LinkAddress(inetAddr, networkPrefixLength);

        // getway
        try {
            staticIpConfig.gateway = NetworkUtils.numericToInetAddress(devInfo.getRouteAddr());
            if (!TextUtils.isEmpty(devInfo.getDnsAddr())) {
                staticIpConfig.dnsServers.add(NetworkUtils.numericToInetAddress(devInfo.getDnsAddr()));
            }
            if (!TextUtils.isEmpty(devInfo.getDns2Addr())) {
                staticIpConfig.dnsServers.add(NetworkUtils.numericToInetAddress(devInfo.getDns2Addr()));
            }
        } catch (IllegalArgumentException e) {
            return config;
        }

        return config;
    }

    private int netmaskToPrefixLength(String netmask) {
        String[] tmp = netmask.split("\\.");
        int cnt = 0;
        for (String cell : tmp) {
            int i = Integer.parseInt(cell);
            cnt += Integer.bitCount(i);
        }

        return cnt;
    }

    private String prefixToNetmask(int prefixLength) {
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
}
