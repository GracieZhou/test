
package com.android.settings.network.ethernet;

import java.net.InetAddress;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import android.net.LinkAddress;
import android.net.LinkProperties;
import android.net.RouteInfo;
import android.text.TextUtils;
import android.util.Log;

/**
 * the tools of network,include some format methods
 */
public class NetworkSettingUtils {

    /**
     * @param prop
     * @return ipaddress after format
     */
    static public String formatIpAddresses(LinkProperties prop) {
        if (prop == null)
            return null;
        Iterator<LinkAddress> iter = prop.getLinkAddresses().iterator();
        // If there are no entries, return null
        if (!iter.hasNext())
            return null;
        // Concatenate first available addresses
        String addresses = "";
        while (iter.hasNext()) {
            LinkAddress linkAddress = iter.next();
            addresses = linkAddress.getAddress().getHostAddress();
            break;
        }
        return addresses;
    }

    /**
     * @param prop
     * @return the gateway after format
     */
    static public String formatGateway(LinkProperties prop) {
        if (prop == null)
            return null;
        Iterator<RouteInfo> iter = prop.getAllRoutes().iterator();
        // If there are no entries, return null
        if (!iter.hasNext())
            return null;
        // Concatenate first available addresses
        InetAddress gateway = null;
        while (iter.hasNext()) {
            gateway = iter.next().getGateway();
            if (gateway.toString().replace("/", "").equals("0.0.0.0")) {
                continue;
            }
            break;
        }
        return gateway.toString().replace("/", "");
    }

    /**
     * @param prop
     * @param defaultString
     * @return
     */
    public static String formatMaskString(LinkProperties prop, String defaultString) {

        String maskString = "";
        int length = formatMaskLength(prop);

        if (length == -1) {
            maskString = defaultString;
        } else {
            maskString = getMaskStringByLength(length);
        }

        return maskString;
    }

    public static String getMaskStringByLength(int prefix) {
        int[] ipSplit = new int[4];
        int index = 0;
        int split = 0;
        int remainder = 0;
        split = prefix / 8;
        remainder = prefix % 8;
        while (index < split) {
            ipSplit[index] = 255;
            index++;
        }
        if (remainder == 1)
            ipSplit[index] = 128;
        if (remainder == 2)
            ipSplit[index] = 192;
        if (remainder == 3)
            ipSplit[index] = 224;
        if (remainder == 4)
            ipSplit[index] = 240;
        if (remainder == 5)
            ipSplit[index] = 248;
        if (remainder == 6)
            ipSplit[index] = 252;
        if (remainder == 7)
            ipSplit[index] = 254;
        index++;
        while (index < remainder) {
            ipSplit[index] = 0;
            index++;
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < ipSplit.length - 1; i++) {
            sb.append(ipSplit[i]);
            sb.append(".");
        }
        sb.append(ipSplit[ipSplit.length - 1]);

        return sb.toString();
    }

    public static int formatMaskLength(LinkProperties prop) {
        if (prop == null)
            return -1;
        Iterator<LinkAddress> iter = prop.getLinkAddresses().iterator();
        // If there are no entries, return null
        if (!iter.hasNext())
            return -1;
        // Concatenate first available addresses
        int prefixLength = -1;
        while (iter.hasNext()) {
            prefixLength = iter.next().getNetworkPrefixLength();
            break;
        }
        return prefixLength;
    }

    /**
     * judge whether the input message conform to the rules
     * 
     * @param maskString
     * @return
     */
    public static boolean isValidMaskString(String maskString) {

        boolean result = false;

        if (TextUtils.isEmpty(maskString)) {
            return result;
        }

        String[] ips = maskString.split("\\.");

        if (ips == null || ips.length < 4) {
            return result;
        }

        Log.i("TAG", "" + maskString);

        for (String ip : ips) {
            if ("255".equals(ip) || "254".equals(ip) || "252".equals(ip) || "248".equals(ip) || "240".equals(ip)
                    || "224".equals(ip) || "192".equals(ip) || "128".equals(ip) || "0".equals(ip)) {
                result = true;
            } else {
                result = false;
                break;
            }
        }
        return result;
    }

    /**
     * @param ip
     * @return boolean of whether the ip conform to the rules
     */
    public static boolean matchIP(String ip) {
        if (ip == null) {
            return false;
        }
        String regex = "([1-9]|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])(\\.(\\d|[1-9]\\d|1\\d{2}|2[0-4]\\d|25[0-5])){3}";
        Pattern pattern = Pattern.compile(regex);
        Matcher matcher = pattern.matcher(ip);
        return matcher.matches();
    }
}
