
package scifly.middleware.network.amlogic.kitkat;

import java.net.InetAddress;
import java.util.BitSet;
import java.util.Iterator;
import java.util.List;

import scifly.middleware.network.IWifiManager;
import scifly.middleware.network.IpConfig;
import scifly.middleware.network.IpConfig.IpAssignment;
import scifly.middleware.network.StaticIpConfig;
import scifly.middleware.network.WifiConfig;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.LinkAddress;
import android.net.LinkProperties;
import android.net.NetworkInfo;
import android.net.RouteInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.ActionListener;
import android.util.Log;

import com.google.android.collect.Lists;

public class WifiManagerImpl implements IWifiManager {

    private static final String TAG = "WifiManagerImpl@kitkat";

    private final WifiManager mWifiManager;

    private final ConnectivityManager mConnectivityManager;

    public WifiManagerImpl(Context context) {
        mWifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        mConnectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    @Override
    public void connect(WifiConfig config, ActionListener listener) {
        if (config == null) {
            throw new IllegalArgumentException("config is null.");
        }

        mWifiManager.connect(middleware2Platform(config), listener);
    }

    @Override
    public void save(WifiConfig config, ActionListener listener) {
        if (config == null) {
            throw new IllegalArgumentException("config is null.");
        }
        mWifiManager.save(middleware2Platform(config), listener);
    }

    @Override
    public List<WifiConfig> getConfiguredNetworks() {
        List<WifiConfig> list = Lists.newArrayList();

        List<WifiConfiguration> configuredNetworks = mWifiManager.getConfiguredNetworks();
        if (configuredNetworks != null) {
            for (WifiConfiguration config : mWifiManager.getConfiguredNetworks()) {
                list.add(platform2Middleware(config));
            }
        }
        return list;
    }

    @Override
    public WifiConfig getConfiguredNetworks(int networkId) {
        List<WifiConfiguration> configuredNetworks = mWifiManager.getConfiguredNetworks();
        if (configuredNetworks != null) {
            for (WifiConfiguration configuredNetwork : configuredNetworks) {
                if (configuredNetwork.networkId == networkId) {
                    Log.d(TAG, "networkId : " + networkId + " configuredNetwork : " + configuredNetwork);
                    return platform2Middleware(configuredNetwork);
                }
            }
        }

        return null;
    }

    @Override
    public IpConfig getConfiguration() {
        NetworkInfo networkInfo = mConnectivityManager.getActiveNetworkInfo();
        if (networkInfo != null && networkInfo.getType() == ConnectivityManager.TYPE_WIFI && networkInfo.isConnected()) {
            for (WifiConfig wifiConfig : getConfiguredNetworks()) {
                Log.d(TAG, "configured ssid : " + wifiConfig.SSID + " network extra : " + networkInfo.getExtraInfo());
                if (wifiConfig.SSID.equals(networkInfo.getExtraInfo())) {
                    return wifiConfig.getIpConfiguration();
                }
            }
        }

        return null;
    }

    @Override
    public void setConfiguration(IpConfig config) {
        
    }

    // ------------ data transform ------------//
    private WifiConfiguration middleware2Platform(WifiConfig wifiConfig) {
        WifiConfiguration configuration = new WifiConfiguration();
        configuration.networkId = wifiConfig.networkId;
        configuration.status = wifiConfig.status;
        configuration.disableReason = wifiConfig.disableReason;
        configuration.SSID = wifiConfig.SSID;
        configuration.BSSID = wifiConfig.BSSID;
        configuration.preSharedKey = wifiConfig.preSharedKey;
        for (int i = 0; i < configuration.wepKeys.length; i++) {
            configuration.wepKeys[i] = wifiConfig.wepKeys[i];
        }
        configuration.hiddenSSID = wifiConfig.hiddenSSID;
        configuration.allowedKeyManagement = (BitSet) wifiConfig.allowedKeyManagement.clone();

        LinkProperties linkProperties = new LinkProperties();
        IpConfig ipConfig = wifiConfig.getIpConfiguration();
        if (ipConfig != null) {
            if (ipConfig.ipAssignment == IpAssignment.STATIC) {
                configuration.ipAssignment = android.net.wifi.WifiConfiguration.IpAssignment.STATIC;
            } else if (ipConfig.ipAssignment == IpAssignment.DHCP) {
                configuration.ipAssignment = android.net.wifi.WifiConfiguration.IpAssignment.DHCP;
            }

            StaticIpConfig staticIpConfig = ipConfig.getStaticIpConfig();
            if (staticIpConfig != null) {
                LinkAddress ipAddress = staticIpConfig.ipAddress;
                // ip & prefix length
                if (ipAddress != null) {
                    linkProperties.addLinkAddress(new LinkAddress(ipAddress.getAddress(), ipAddress
                            .getNetworkPrefixLength()));
                }

                // gateway
                InetAddress gateWay = staticIpConfig.gateway;
                if (gateWay != null) {
                    linkProperties.addRoute(new RouteInfo(gateWay));
                }

                // dns
                int N = staticIpConfig.dnsServers == null ? 0 : staticIpConfig.dnsServers.size();
                for (int i = 0; i < N; i++) {
                    linkProperties.addDns(staticIpConfig.dnsServers.get(i));
                }
            }
        }
        configuration.linkProperties = new LinkProperties(linkProperties);

        return configuration;
    }

    private WifiConfig platform2Middleware(WifiConfiguration wifiConfiguration) {
        WifiConfig config = new WifiConfig();
        config.networkId = wifiConfiguration.networkId;
        config.status = wifiConfiguration.status;
        config.disableReason = wifiConfiguration.disableReason;
        config.SSID = wifiConfiguration.SSID;
        config.BSSID = wifiConfiguration.BSSID;
        config.preSharedKey = wifiConfiguration.preSharedKey;
        for (int i = 0; i < config.wepKeys.length; i++) {
            config.wepKeys[i] = wifiConfiguration.wepKeys[i];
        }
        config.hiddenSSID = wifiConfiguration.hiddenSSID;
        config.allowedKeyManagement = (BitSet) wifiConfiguration.allowedKeyManagement.clone();

        IpConfig ipConfig = new IpConfig();
        if (wifiConfiguration.ipAssignment == android.net.wifi.WifiConfiguration.IpAssignment.STATIC) {
            ipConfig.ipAssignment = IpAssignment.STATIC;
        } else if (wifiConfiguration.ipAssignment == android.net.wifi.WifiConfiguration.IpAssignment.DHCP) {
            ipConfig.ipAssignment = IpAssignment.DHCP;
        }
        // FIXME proxy

        StaticIpConfig staticIpConfig = new StaticIpConfig();
        ipConfig.setStaticIpConfig(staticIpConfig);

        if (wifiConfiguration.linkProperties != null) {
            LinkProperties linkProperties = new LinkProperties(wifiConfiguration.linkProperties);
            Log.d(TAG, "linkProperties : " + linkProperties);
            Iterator<LinkAddress> iterator = linkProperties.getLinkAddresses().iterator();
            if (iterator.hasNext()) {
                LinkAddress linkAddress = iterator.next();
                staticIpConfig.ipAddress = new LinkAddress(linkAddress.getAddress(),
                        linkAddress.getNetworkPrefixLength());
            }

            for (RouteInfo routeInfo : linkProperties.getRoutes()) {
                if (routeInfo.isDefaultRoute()) {
                    staticIpConfig.gateway = routeInfo.getGateway();
                }
            }

            // dns
            staticIpConfig.dnsServers.addAll(linkProperties.getDnses());
        }
        Log.d(TAG, "ipConfig : " + ipConfig);
        config.setIpConfiguration(ipConfig);

        return config;
    }
}
