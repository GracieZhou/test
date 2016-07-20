
package scifly.middleware.network.amlogic.kitkat;

import static android.net.ethernet.EthernetManager.ETH_STATE_ENABLED;

import java.net.Inet4Address;

import scifly.middleware.network.IEthernetManager;
import scifly.middleware.network.IpConfig;
import scifly.middleware.network.IpConfig.IpAssignment;
import scifly.middleware.network.StaticIpConfig;
import android.content.Context;
import android.net.DhcpInfo;
import android.net.LinkAddress;
import android.net.NetworkUtils;
import android.net.ethernet.EthernetDevInfo;
import android.net.ethernet.EthernetManager;
import android.text.TextUtils;
import android.util.Log;

public class EthernetManagerImpl implements IEthernetManager {

    private static final String TAG = "EthernetManagerImpl@kitkat";

    private final EthernetManager mEthernetManager;

    public EthernetManagerImpl(Context context) {
        mEthernetManager = (EthernetManager) context.getSystemService(Context.ETH_SERVICE);
    }

    public IpConfig getConfiguration() {
        if (!isEnabled()) {
            return null;
        }

        EthernetDevInfo devInfo = mEthernetManager.getSavedEthConfig();
        if (devInfo == null) {
            return null;
        }

        if (EthernetDevInfo.ETH_CONN_MODE_MANUAL.equals(devInfo.getConnectMode())) {
            return getConfiguration(devInfo);
        } else if (EthernetDevInfo.ETH_CONN_MODE_DHCP.equals(devInfo.getConnectMode())) {
            DhcpInfo dhcpInfo = mEthernetManager.getDhcpInfo();
            return getConfiguration(dhcpInfo);
        }

        return null;
    }

    public void setConfiguration(IpConfig config) {
        if (config == null) {
            throw new IllegalArgumentException("illegal argument IpConfig");
        }

        EthernetDevInfo devInfo = new EthernetDevInfo();
        // FIXME eth0/eth1.../ethn
        devInfo.setIfName("eth0");
        if (config.ipAssignment == IpAssignment.DHCP) {
            devInfo.setConnectMode(EthernetDevInfo.ETH_CONN_MODE_DHCP);
            devInfo.setIpAddress(null);
            devInfo.setNetMask(null);
            devInfo.setRouteAddr(null);
            devInfo.setDnsAddr(null);
        } else {
            StaticIpConfig staticIpConfig = config.getStaticIpConfig();

            devInfo.setConnectMode(EthernetDevInfo.ETH_CONN_MODE_MANUAL);
            devInfo.setIpAddress(staticIpConfig.ipAddress.getAddress().getHostAddress());
            devInfo.setNetMask(prefixToNetmask(staticIpConfig.ipAddress.getNetworkPrefixLength()));
            devInfo.setRouteAddr(staticIpConfig.gateway.getHostAddress());
            int N = staticIpConfig.dnsServers.size();
            if (N == 1) {
                devInfo.setDnsAddr(staticIpConfig.dnsServers.get(0).getHostAddress());
            }
        }

        mEthernetManager.updateEthDevInfo(devInfo);
    }

    public boolean isEnabled() {
        return mEthernetManager.getEthState() == ETH_STATE_ENABLED;
    }

    public void setEnabled(boolean enable) {
        mEthernetManager.setEthEnabled(enable);
    }

    // ------------ data transform ------------//
    private IpConfig getConfiguration(EthernetDevInfo devInfo) {
        Log.d(TAG, "devInfo : " + devInfo.getIpAddress() + "," + devInfo.getNetMask() + "," + devInfo.getRouteAddr()
                + "," + devInfo.getDnsAddr());

        IpConfig config = new IpConfig();
        config.ipAssignment = IpConfig.IpAssignment.STATIC;

        // ip addr
        StaticIpConfig staticIpConfig = new StaticIpConfig();
        config.setStaticIpConfig(staticIpConfig);

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
        } catch (IllegalArgumentException e) {
            return config;
        }

        return config;
    }

    private IpConfig getConfiguration(DhcpInfo dhcpInfo) {
        Log.d(TAG, "dhcpInfo : " + convert(dhcpInfo.ipAddress) + "," + convert(dhcpInfo.netmask) + ","
                + convert(dhcpInfo.gateway) + "," + convert(dhcpInfo.dns1) + "," + convert(dhcpInfo.dns2));

        IpConfig config = new IpConfig();
        // connect mode
        config.ipAssignment = IpConfig.IpAssignment.DHCP;

        // ip addr
        StaticIpConfig staticIpConfig = new StaticIpConfig();
        config.setStaticIpConfig(staticIpConfig);

        Inet4Address inetAddr = (Inet4Address) NetworkUtils.intToInetAddress(dhcpInfo.ipAddress);

        int networkPrefixLength = NetworkUtils.netmaskIntToPrefixLength(dhcpInfo.netmask);
        if (networkPrefixLength < 0 || networkPrefixLength > 32) {
            Log.w(TAG, "illegal argument network prefixLength.");
            return config;
        }
        staticIpConfig.ipAddress = new LinkAddress(inetAddr, networkPrefixLength);

        // getway
        staticIpConfig.gateway = NetworkUtils.intToInetAddress(dhcpInfo.gateway);
        staticIpConfig.dnsServers.add(NetworkUtils.intToInetAddress(dhcpInfo.dns1));
        staticIpConfig.dnsServers.add(NetworkUtils.intToInetAddress(dhcpInfo.dns2));

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

    private String convert(int address) {
        return NetworkUtils.intToInetAddress(address).getHostAddress();
    }
}
