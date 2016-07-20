
package scifly.middleware.network;

import scifly.middleware.network.IpConfig.ProxySettings;
import android.os.Parcel;
import android.os.Parcelable;

public class WifiConfig implements Parcelable {

    /**
     * The ID number that the supplicant uses to identify this network
     * configuration entry. This must be passed as an argument to most calls
     * into the supplicant.
     */
    public int networkId;

    /**
     * The network's SSID. Can either be an ASCII string, which must be enclosed
     * in double quotation marks (e.g., {@code "MyNetwork"}, or a string of hex
     * digits,which are not enclosed in quotes (e.g., {@code 01a243f405}).
     */
    public String SSID;

    /**
     * When set, this network configuration entry should only be used when
     * associating with the AP having the specified BSSID. The value is a string
     * in the format of an Ethernet MAC address, e.g.,
     * <code>XX:XX:XX:XX:XX:XX</code> where each <code>X</code> is a hex digit.
     */
    public String BSSID;

    /**
     * This is a network that does not broadcast its SSID, so an SSID-specific
     * probe request must be used for scans.
     */
    public boolean hiddenSSID;

    private IpConfig mIpConfiguration;

    public WifiConfig() {
        networkId = -1;
        SSID = null;
        BSSID = null;
        hiddenSSID = false;
        mIpConfiguration = new IpConfig();
    }

    public IpConfig getIpConfiguration() {
        return mIpConfiguration;
    }

    public void setIpConfiguration(IpConfig ipConfiguration) {
        mIpConfiguration = ipConfiguration;
    }

    public StaticIpConfig getStaticIpConfiguration() {
        return mIpConfiguration.getStaticIpConfig();
    }

    public void setStaticIpConfiguration(StaticIpConfig staticIpConfiguration) {
        mIpConfiguration.setStaticIpConfig(staticIpConfiguration);
    }

    public IpConfig.IpAssignment getIpAssignment() {
        return mIpConfiguration.ipAssignment;
    }

    public void setIpAssignment(IpConfig.IpAssignment ipAssignment) {
        mIpConfiguration.ipAssignment = ipAssignment;
    }

    public IpConfig.ProxySettings getProxySettings() {
        return mIpConfiguration.proxySettings;
    }

    public void setProxySettings(IpConfig.ProxySettings proxySettings) {
        mIpConfiguration.proxySettings = proxySettings;
    }

    public ProxyConfig getHttpProxy() {
        return mIpConfiguration.httpProxy;
    }

    public void setHttpProxy(ProxyConfig httpProxy) {
        mIpConfiguration.httpProxy = httpProxy;
    }

    public void setProxy(ProxySettings settings, ProxyConfig proxy) {
        mIpConfiguration.proxySettings = settings;
        mIpConfiguration.httpProxy = proxy;
    }

    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(networkId);
        dest.writeString(SSID);
        dest.writeString(BSSID);
        dest.writeInt(hiddenSSID ? 1 : 0);
        dest.writeParcelable(mIpConfiguration, flags);
    }

    public static final Creator<WifiConfig> CREATOR = new Creator<WifiConfig>() {
        public WifiConfig createFromParcel(Parcel in) {
            WifiConfig config = new WifiConfig();
            config.networkId = in.readInt();
            config.SSID = in.readString();
            config.BSSID = in.readString();
            config.hiddenSSID = in.readInt() != 0;
            config.mIpConfiguration = in.readParcelable(null);
            return config;
        }

        public WifiConfig[] newArray(int size) {
            return new WifiConfig[size];
        }
    };
}
