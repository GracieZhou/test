
package scifly.middleware.network;

import java.util.Objects;

import android.os.Parcel;
import android.os.Parcelable;

public class IpConfig implements Parcelable {
    private static final String TAG = "IpConfig";

    public enum IpAssignment {
        /*
         * Use statically configured IP settings. Configuration can be accessed
         * with StaticIpConfig
         */
        STATIC,
        /* Use dynamically configured IP settigns */
        DHCP,
        /*
         * no IP details are assigned, this is used to indicate that any
         * existing IP settings should be retained
         */
        UNASSIGNED
    }

    public IpAssignment ipAssignment;

    public StaticIpConfig staticIpConfig;

    public enum ProxySettings {
        /*
         * No proxy is to be used. Any existing proxy settings should be
         * cleared.
         */
        NONE,
        /*
         * Use statically configured proxy. Configuration can be accessed with
         * httpProxy.
         */
        STATIC,
        /*
         * no proxy details are assigned, this is used to indicate that any
         * existing proxy settings should be retained
         */
        UNASSIGNED,
        /*
         * Use a Pac based proxy.
         */
        PAC
    }

    public ProxySettings proxySettings;

    public ProxyConfig httpProxy;

    private void init(IpAssignment ipAssignment, ProxySettings proxySettings, StaticIpConfig StaticIpConfig,
            ProxyConfig httpProxy) {
        this.ipAssignment = ipAssignment;
        this.proxySettings = proxySettings;
        this.staticIpConfig = (StaticIpConfig == null) ? null : new StaticIpConfig(StaticIpConfig);
        this.httpProxy = (httpProxy == null) ? null : new ProxyConfig(httpProxy);
    }

    public IpConfig() {
        init(IpAssignment.UNASSIGNED, ProxySettings.UNASSIGNED, null, null);
    }

    public IpConfig(IpAssignment ipAssignment, ProxySettings proxySettings, StaticIpConfig StaticIpConfig,
            ProxyConfig httpProxy) {
        init(ipAssignment, proxySettings, StaticIpConfig, httpProxy);
    }

    public IpConfig(IpConfig source) {
        this();
        if (source != null) {
            init(source.ipAssignment, source.proxySettings, source.staticIpConfig, source.httpProxy);
        }
    }

    public IpAssignment getIpAssignment() {
        return ipAssignment;
    }

    public void setIpAssignment(IpAssignment ipAssignment) {
        this.ipAssignment = ipAssignment;
    }

    public StaticIpConfig getStaticIpConfig() {
        return staticIpConfig;
    }

    public void setStaticIpConfig(StaticIpConfig StaticIpConfig) {
        this.staticIpConfig = StaticIpConfig;
    }

    public ProxySettings getProxySettings() {
        return proxySettings;
    }

    public void setProxySettings(ProxySettings proxySettings) {
        this.proxySettings = proxySettings;
    }

    public ProxyConfig getHttpProxy() {
        return httpProxy;
    }

    public void setHttpProxy(ProxyConfig httpProxy) {
        this.httpProxy = httpProxy;
    }

    @Override
    public String toString() {
        StringBuilder sbuf = new StringBuilder();
        sbuf.append(TAG + ", IP assignment: " + ipAssignment.toString());
        sbuf.append("\n");
        if (staticIpConfig != null) {
            sbuf.append("Static configuration: " + staticIpConfig.toString());
            sbuf.append("\n");
        }
        sbuf.append("Proxy settings: " + proxySettings.toString());
        sbuf.append("\n");
        if (httpProxy != null) {
            sbuf.append("HTTP proxy: " + httpProxy.toString());
            sbuf.append("\n");
        }

        return sbuf.toString();
    }

    @Override
    public boolean equals(Object o) {
        if (o == this) {
            return true;
        }

        if (!(o instanceof IpConfig)) {
            return false;
        }

        IpConfig other = (IpConfig) o;
        return this.ipAssignment == other.ipAssignment && this.proxySettings == other.proxySettings
                && Objects.equals(this.staticIpConfig, other.staticIpConfig)
                && Objects.equals(this.httpProxy, other.httpProxy);
    }

    @Override
    public int hashCode() {
        return 13 + (staticIpConfig != null ? staticIpConfig.hashCode() : 0) + 17 * ipAssignment.ordinal() + 47
                * proxySettings.ordinal() + 83 * httpProxy.hashCode();
    }

    /** Implement the Parcelable interface */
    public int describeContents() {
        return 0;
    }

    /** Implement the Parcelable interface */
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(ipAssignment.name());
        dest.writeString(proxySettings.name());
        dest.writeParcelable(staticIpConfig, flags);
        dest.writeParcelable(httpProxy, flags);
    }

    /** Implement the Parcelable interface */
    public static final Creator<IpConfig> CREATOR = new Creator<IpConfig>() {
        public IpConfig createFromParcel(Parcel in) {
            IpConfig config = new IpConfig();
            config.ipAssignment = IpAssignment.valueOf(in.readString());
            config.proxySettings = ProxySettings.valueOf(in.readString());
            config.staticIpConfig = in.readParcelable(null);
            config.httpProxy = in.readParcelable(null);
            return config;
        }

        public IpConfig[] newArray(int size) {
            return new IpConfig[size];
        }
    };
}
