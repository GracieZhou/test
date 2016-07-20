
package scifly.middleware.network.mstar.lollipop;

import java.io.IOException;
import java.net.Inet4Address;
import java.net.InetAddress;

import com.mstar.android.pppoe.PppoeManager;

import scifly.middleware.network.IEthernetManager;
import scifly.middleware.network.IpConfig;
import scifly.middleware.network.StaticIpConfig;
import scifly.middleware.network.IpConfig.IpAssignment;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.EthernetManager;
import android.net.IpConfiguration;
import android.net.IpPrefix;
import android.net.LinkAddress;
import android.net.LinkProperties;
import android.net.NetworkUtils;
import android.net.RouteInfo;
import android.net.StaticIpConfiguration;
import android.text.TextUtils;
import android.util.Log;

public class EthernetManagerImpl implements IEthernetManager {

    private static final String TAG = "EthernetManagerImpl@lollipop";

    private final EthernetManager mEthernetManager;
    private final ConnectivityManager mConnectivityManager;
    private Context mContext;

    public EthernetManagerImpl(Context context) {
        Log.w(TAG, "EthernetManagerImpl constructor");
        mEthernetManager = (EthernetManager) context.getSystemService(Context.ETHERNET_SERVICE);
        mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        mContext = context;
    }

    @Override
    public IpConfig getConfiguration() {
        if (!isEnabled()) {
            Log.w(TAG, "ethernet is disabled.");
            return null;
        }

        IpConfiguration ipConfig = mEthernetManager.getConfiguration();
        if (ipConfig == null) {
            Log.d(TAG, "IpConfiguration is null.");
            return null;
        }

        return getConfiguration(ipConfig);
    }

    @Override
    public void setConfiguration(IpConfig ipConfig) {
        if (!isEnabled()) {
            Log.w(TAG, "ethernet is disabled.");
            return;
        }

        // null check
        if (ipConfig == null) {
            throw new IllegalArgumentException("illegal argument IpConfig");
        }

        IpConfiguration ipConfiguration = new IpConfiguration();
        if (ipConfig.ipAssignment == IpAssignment.DHCP) {
            ipConfiguration.ipAssignment = android.net.IpConfiguration.IpAssignment.DHCP;
            ipConfiguration.setStaticIpConfiguration(null);
        } else if (ipConfig.ipAssignment == IpAssignment.STATIC) {
            ipConfiguration.ipAssignment = android.net.IpConfiguration.IpAssignment.STATIC;

            StaticIpConfiguration staticIpConfiguration = new StaticIpConfiguration();
            ipConfiguration.setStaticIpConfiguration(staticIpConfiguration);

            StaticIpConfig staticIpConfig = ipConfig.staticIpConfig;
            // process ip & netmask
            staticIpConfiguration.ipAddress = staticIpConfig.ipAddress;

            // process gateway
            staticIpConfiguration.gateway = staticIpConfig.gateway;

            // process dns
            staticIpConfiguration.dnsServers.addAll(staticIpConfig.dnsServers);
        }

        mEthernetManager.setConfiguration(ipConfiguration);
    }

    @Override
    public boolean isEnabled() {
        return mEthernetManager.isEnabled();
    }

    @Override
    public void setEnabled(boolean enable) {
        mEthernetManager.setEnabled(enable);
    }

    // ------------ data transform ------------//
    private IpConfig getConfiguration(IpConfiguration ipConfiguration) {IpConfig ipConfig = new IpConfig();
    if (ipConfiguration.ipAssignment == android.net.IpConfiguration.IpAssignment.STATIC) {
        ipConfig.ipAssignment = IpAssignment.STATIC;
    } else if (ipConfiguration.ipAssignment == android.net.IpConfiguration.IpAssignment.DHCP) {
        ipConfig.ipAssignment = IpAssignment.DHCP;
    } else {
        ipConfig.ipAssignment = IpAssignment.UNASSIGNED;
    }
    StaticIpConfig staticIpConfig = new StaticIpConfig();
    ipConfig.setStaticIpConfig(staticIpConfig);
    PppoeManager mPppoeManager = null;
    try {
        mPppoeManager = PppoeManager.getInstance(mContext);
    } catch (Exception e) {
        e.printStackTrace();
    }
    if (mPppoeManager!=null&&mPppoeManager.getPppoeStatus().equals(mPppoeManager.PPPOE_STATE_CONNECT)) {
        StaticIpConfiguration staticIpConfiguration = ipConfiguration.getStaticIpConfiguration();
     // process ip & netmask
        staticIpConfig.ipAddress = staticIpConfiguration.ipAddress;

        // process gateway
        staticIpConfig.gateway = staticIpConfiguration.gateway;

        // process dns
        staticIpConfig.dnsServers.addAll(staticIpConfiguration.dnsServers);

        return ipConfig;
    }else{
     // network configuration  setStaticIpConfig
        StaticIpConfiguration staticIpConfiguration = ipConfiguration.getStaticIpConfiguration();
        ipConfiguration.getStaticIpConfiguration();
        if (staticIpConfiguration == null) {
            LinkProperties linkProperties = mConnectivityManager.getLinkProperties(ConnectivityManager.TYPE_ETHERNET);
            if (linkProperties != null) {
                for (LinkAddress linkAddress : linkProperties.getAllLinkAddresses()) {
                    InetAddress address = linkAddress.getAddress();
                    if (address instanceof Inet4Address) {
                        Log.d("alisa", "<<<<address.getHostAddress()<<<<<" + address.getHostAddress());
                    }
                }

                // IPv6 address will not be shown like WifiInfo internally does.

                int prefixLength = 0;
                for (InetAddress dns : linkProperties.getDnsServers()) {
                    if (dns != null) {
                        Log.d(TAG, "dns : " + dns.getHostAddress());
                        staticIpConfig.dnsServers.add(dns);
                    }
                }

            for (RouteInfo route : linkProperties.getRoutes()) {
                InetAddress gateway = route.getGateway();
                if (!"0.0.0.0".equals(gateway.getHostAddress()) && gateway.getHostAddress() != null) {
                    staticIpConfig.gateway = gateway;
                    Log.d(TAG, "gateway : " + gateway.getHostAddress());
                }
                IpPrefix prefix = route.getDestination();
                if (prefix != null && prefix.getPrefixLength() > 0 && prefix.getPrefixLength() < 32) {
                    prefixLength = prefix.getPrefixLength();
                    Log.d(TAG, "prefixLength : " + prefixLength);
                }
            }

                for (LinkAddress linkAddress : linkProperties.getAllLinkAddresses()) {
                    InetAddress address = linkAddress.getAddress();
                    if (address instanceof Inet4Address) {
                        staticIpConfig.ipAddress = new LinkAddress(address, prefixLength);
                        Log.d(TAG, "ipaddr : " + address.getHostAddress());
                        break;
                    }
                }
                return ipConfig;
            }
            return null;
        }

        // process ip & netmask
        staticIpConfig.ipAddress = staticIpConfiguration.ipAddress;

        // process gateway
        staticIpConfig.gateway = staticIpConfiguration.gateway;

        // process dns
        staticIpConfig.dnsServers.addAll(staticIpConfiguration.dnsServers);

        return ipConfig;
        }
    }
}
