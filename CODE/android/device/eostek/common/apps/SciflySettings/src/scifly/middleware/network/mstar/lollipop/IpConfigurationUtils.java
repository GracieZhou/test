
package scifly.middleware.network.mstar.lollipop;

import java.net.Inet4Address;
import java.net.InetAddress;

import android.net.ConnectivityManager;
import android.net.IpConfiguration;
import android.net.IpPrefix;
import android.net.LinkAddress;
import android.net.LinkProperties;
import android.net.RouteInfo;
import android.net.StaticIpConfiguration;
import android.util.Log;

import scifly.middleware.network.IpConfig;
import scifly.middleware.network.StaticIpConfig;
import scifly.middleware.network.IpConfig.IpAssignment;

public class IpConfigurationUtils {

    private static final String TAG = "IpConfigurationUtils";

    public static IpConfiguration config2Configuration(IpConfig ipConfig) {
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

        return ipConfiguration;
    }

    public static IpConfig configuration2Config(IpConfiguration ipConfiguration) {
        IpConfig ipConfig = new IpConfig();
        if (ipConfiguration.ipAssignment == android.net.IpConfiguration.IpAssignment.STATIC) {
            ipConfig.ipAssignment = IpAssignment.STATIC;
        } else if (ipConfiguration.ipAssignment == android.net.IpConfiguration.IpAssignment.DHCP) {
            ipConfig.ipAssignment = IpAssignment.DHCP;
        } else {
            ipConfig.ipAssignment = IpAssignment.UNASSIGNED;
        }

        StaticIpConfig staticIpConfig = new StaticIpConfig();
        ipConfig.setStaticIpConfig(staticIpConfig);

        // network configuration
        StaticIpConfiguration staticIpConfiguration = ipConfiguration.getStaticIpConfiguration();
        ipConfiguration.getStaticIpConfiguration();
        Log.d(TAG, "staticIpConfiguration : " + staticIpConfiguration);
        if (staticIpConfiguration == null) {
            ipConfig.setStaticIpConfig(null);
        } else {
            // process ip & netmask
            staticIpConfig.ipAddress = staticIpConfiguration.ipAddress;
            // process gateway
            staticIpConfig.gateway = staticIpConfiguration.gateway;
            // process dns
            staticIpConfig.dnsServers.addAll(staticIpConfiguration.dnsServers);
        }

        return ipConfig;
    }
}
