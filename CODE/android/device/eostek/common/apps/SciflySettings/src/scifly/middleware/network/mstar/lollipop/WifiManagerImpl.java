
package scifly.middleware.network.mstar.lollipop;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.util.BitSet;
import java.util.List;

import scifly.middleware.network.IWifiManager;
import scifly.middleware.network.IpConfig;
import scifly.middleware.network.StaticIpConfig;
import scifly.middleware.network.WifiConfig;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.IpPrefix;
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

    private static final String TAG = "WifiManagerImpl@lollipop";
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
        if (mWifiManager.getConfiguredNetworks() != null) {
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
                    if (false) {
                        Log.d(TAG, "networkId : " + networkId + " configuredNetwork : " + configuredNetwork);
                    }
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
        configuration.setIpConfiguration(IpConfigurationUtils.config2Configuration(wifiConfig.getIpConfiguration()));

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
        IpConfig ipConfig = IpConfigurationUtils.configuration2Config(wifiConfiguration.getIpConfiguration());
        Log.d(TAG, "StaticIpConfig : " + ipConfig.getStaticIpConfig());
        if (ipConfig.getStaticIpConfig() == null) {
            StaticIpConfig staticIpConfig = new StaticIpConfig();
            ipConfig.setStaticIpConfig(staticIpConfig);

            LinkProperties linkProperties = mConnectivityManager.getLinkProperties(ConnectivityManager.TYPE_WIFI);
            int prefixLength = 0;
            if (linkProperties != null) {
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
        }
        }
        config.setIpConfiguration(ipConfig);

        return config;
    }
}
