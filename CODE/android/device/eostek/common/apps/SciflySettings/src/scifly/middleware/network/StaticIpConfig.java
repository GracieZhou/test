
package scifly.middleware.network;

import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

import android.net.LinkAddress;
import android.net.RouteInfo;
import android.os.Parcel;
import android.os.Parcelable;

public class StaticIpConfig implements Parcelable {
    public LinkAddress ipAddress;

    public InetAddress gateway;

    public final ArrayList<InetAddress> dnsServers;

    public String domains;

    public StaticIpConfig() {
        dnsServers = new ArrayList<InetAddress>();
    }

    public StaticIpConfig(StaticIpConfig source) {
        this();
        if (source != null) {
            // All of these except dnsServers are immutable, so no need to make
            // copies.
            ipAddress = source.ipAddress;
            gateway = source.gateway;
            dnsServers.addAll(source.dnsServers);
            domains = source.domains;
        }
    }

    public void clear() {
        ipAddress = null;
        gateway = null;
        dnsServers.clear();
        domains = null;
    }

    /**
     * Returns the network routes specified by this object. Will typically
     * include a directly-connected route for the IP address's local subnet and
     * a default route.
     */
    public List<RouteInfo> getRoutes(String iface) {
        List<RouteInfo> routes = new ArrayList<RouteInfo>(2);
        if (ipAddress != null) {
            routes.add(new RouteInfo(ipAddress, null, iface));
        }
        if (gateway != null) {
            routes.add(new RouteInfo((LinkAddress) null, gateway, iface));
        }
        return routes;
    }

    public String toString() {
        StringBuffer str = new StringBuffer();

        str.append("IP address ");
        if (ipAddress != null) {
            str.append(ipAddress).append(" ");
        }

        str.append("Gateway ");
        if (gateway != null) {
            str.append(gateway.getHostAddress()).append(" ");
        }

        str.append(" DNS servers: [");
        for (InetAddress dnsServer : dnsServers) {
            str.append(" ").append(dnsServer.getHostAddress());
        }

        str.append(" ] Domains");
        if (domains != null) {
            str.append(domains);
        }

        return str.toString();
    }

    public int hashCode() {
        int result = 13;
        result = 47 * result + (ipAddress == null ? 0 : ipAddress.hashCode());
        result = 47 * result + (gateway == null ? 0 : gateway.hashCode());
        result = 47 * result + (domains == null ? 0 : domains.hashCode());
        result = 47 * result + dnsServers.hashCode();
        return result;
    }

    @Override
    public boolean equals(Object obj) {
        if (this == obj) {
            return true;
        }

        if (!(obj instanceof StaticIpConfig)) {
            return false;
        }

        StaticIpConfig other = (StaticIpConfig) obj;

        return other != null && Objects.equals(ipAddress, other.ipAddress) && Objects.equals(gateway, other.gateway)
                && dnsServers.equals(other.dnsServers) && Objects.equals(domains, other.domains);
    }

    /** Implement the Parcelable interface */
    public static Creator<StaticIpConfig> CREATOR = new Creator<StaticIpConfig>() {
        public StaticIpConfig createFromParcel(Parcel in) {
            StaticIpConfig s = new StaticIpConfig();
            readFromParcel(s, in);
            return s;
        }

        public StaticIpConfig[] newArray(int size) {
            return new StaticIpConfig[size];
        }
    };

    /** Implement the Parcelable interface */
    public int describeContents() {
        return 0;
    }

    /** Implement the Parcelable interface */
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeParcelable(ipAddress, flags);
        parcelInetAddress(dest, gateway, flags);
        dest.writeInt(dnsServers.size());
        for (InetAddress dnsServer : dnsServers) {
            parcelInetAddress(dest, dnsServer, flags);
        }
    }

    protected static void readFromParcel(StaticIpConfig s, Parcel in) {
        s.ipAddress = in.readParcelable(null);
        s.gateway = unparcelInetAddress(in);
        s.dnsServers.clear();
        int size = in.readInt();
        for (int i = 0; i < size; i++) {
            s.dnsServers.add(unparcelInetAddress(in));
        }
    }

    /**
     * Writes an InetAddress to a parcel. The address may be null. This is
     * likely faster than calling writeSerializable.
     */
    protected static void parcelInetAddress(Parcel parcel, InetAddress address, int flags) {
        byte[] addressArray = (address != null) ? address.getAddress() : null;
        parcel.writeByteArray(addressArray);
    }

    /**
     * Reads an InetAddress from a parcel. Returns null if the address that was
     * written was null or if the data is invalid.
     */
    protected static InetAddress unparcelInetAddress(Parcel in) {
        byte[] addressArray = in.createByteArray();
        if (addressArray == null) {
            return null;
        }
        try {
            return InetAddress.getByAddress(addressArray);
        } catch (UnknownHostException e) {
            return null;
        }
    }
}
