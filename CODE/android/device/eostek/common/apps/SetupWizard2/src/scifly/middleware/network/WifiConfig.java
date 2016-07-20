
package scifly.middleware.network;

import java.util.Arrays;
import java.util.BitSet;

import scifly.middleware.network.IpConfig.ProxySettings;
import android.net.wifi.WifiEnterpriseConfig;
import android.net.wifi.WifiConfiguration.AuthAlgorithm;
import android.net.wifi.WifiConfiguration.KeyMgmt;
import android.net.wifi.WifiConfiguration.Status;
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
     * The current status of this network configuration entry.
     * @see Status
     */
    public int status;

    /**
     * The code referring to a reason for disabling the network
     * Valid when {@link #status} == Status.DISABLED
     * @hide
     */
    public int disableReason;

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
     * Pre-shared key for use with WPA-PSK.
     * <p/>
     * When the value of this key is read, the actual key is
     * not returned, just a "*" if the key has a value, or the null
     * string otherwise.
     */
    public String preSharedKey;

    /**
     * Up to four WEP keys. Either an ASCII string enclosed in double quotation
     * marks (e.g., {@code "abcdef"} or a string of hex digits (e.g.,
     * {@code 0102030405}).
     * <p/>
     * When the value of one of these keys is read, the actual key is not
     * returned, just a "*" if the key has a value, or the null string
     * otherwise.
     */
    public String[] wepKeys;

    /**
     * This is a network that does not broadcast its SSID, so an SSID-specific
     * probe request must be used for scans.
     */
    public boolean hiddenSSID;

    /**
     * The set of key management protocols supported by this configuration. See
     * {@link KeyMgmt} for descriptions of the values. Defaults to WPA-PSK
     * WPA-EAP.
     */
    public BitSet allowedKeyManagement;

    /**
     * The set of authentication protocols supported by this configuration.
     * See {@link AuthAlgorithm} for descriptions of the values.
     * Defaults to automatic selection.
     */
    public BitSet allowedAuthAlgorithms;

    /**
     * The enterprise configuration details specifying the EAP method,
     * certificates and other settings associated with the EAP.
     */
    public WifiEnterpriseConfig enterpriseConfig;

    private IpConfig mIpConfiguration;

    public WifiConfig() {
        networkId = -1;
        SSID = null;
        BSSID = null;
        hiddenSSID = false;
        disableReason = 0;
        allowedKeyManagement = new BitSet();
        allowedAuthAlgorithms = new BitSet();
        wepKeys = new String[4];
        for (int i = 0; i < wepKeys.length; i++) {
            wepKeys[i] = null;
        }
        enterpriseConfig = new WifiEnterpriseConfig();
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

    private static BitSet readBitSet(Parcel src) {
        int cardinality = src.readInt();

        BitSet set = new BitSet();
        for (int i = 0; i < cardinality; i++) {
            set.set(src.readInt());
        }

        return set;
    }

    private static void writeBitSet(Parcel dest, BitSet set) {
        int nextSetBit = -1;

        dest.writeInt(set.cardinality());

        while ((nextSetBit = set.nextSetBit(nextSetBit + 1)) != -1) {
            dest.writeInt(nextSetBit);
        }
    }

    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeInt(networkId);
        dest.writeInt(status);
        dest.writeInt(disableReason);
        dest.writeString(SSID);
        dest.writeString(BSSID);
        dest.writeString(preSharedKey);
        for (String wepKey : wepKeys) {
            dest.writeString(wepKey);
        }
        dest.writeInt(hiddenSSID ? 1 : 0);
        writeBitSet(dest, allowedKeyManagement);
        writeBitSet(dest, allowedAuthAlgorithms);
        dest.writeParcelable(enterpriseConfig, flags);
        dest.writeParcelable(mIpConfiguration, flags);
    }

    public static final Creator<WifiConfig> CREATOR = new Creator<WifiConfig>() {
        public WifiConfig createFromParcel(Parcel in) {
            WifiConfig config = new WifiConfig();
            config.networkId = in.readInt();
            config.status = in.readInt();
            config.disableReason = in.readInt();
            config.SSID = in.readString();
            config.preSharedKey = in.readString();
            for (int i = 0; i < config.wepKeys.length; i++) {
                config.wepKeys[i] = in.readString();
            }
            config.hiddenSSID = in.readInt() != 0;
            config.allowedKeyManagement = readBitSet(in);
            config.allowedAuthAlgorithms  = readBitSet(in);
            config.enterpriseConfig = in.readParcelable(null);
            config.mIpConfiguration = in.readParcelable(null);

            return config;
        }

        public WifiConfig[] newArray(int size) {
            return new WifiConfig[size];
        }
    };

    public String toString() {
        StringBuffer sb = new StringBuffer();
        sb.append("networkId : " + networkId);
        sb.append(" status : " + status);
        sb.append(" disableReason : " + disableReason);
        sb.append(" SSID : " + SSID);
        sb.append(" preSharedKey : " + preSharedKey);
        sb.append(" wepKeys : " + Arrays.asList(wepKeys).toString());
        sb.append(" hiddenSSID : " + hiddenSSID);
        sb.append(" allowedKeyManagement : " + allowedKeyManagement);
        sb.append(" allowedAuthAlgorithms : " + allowedAuthAlgorithms);
        sb.append(" enterpriseConfig : " + enterpriseConfig);
        sb.append(" mIpConfiguration : " + mIpConfiguration);

        return sb.toString();
    }
}
