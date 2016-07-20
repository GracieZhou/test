
package com.eostek.isynergy.setmeup.ui;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import android.util.Log;

public class Tools {

    private static final String TAG = "Tools";

    public static String getLocalIpAddress() {
        try {
            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                String inferfaceType = intf.getName();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {

                    while (enumIpAddr.hasMoreElements()) {
                        InetAddress inetAddress = enumIpAddr.nextElement();
                        // leiz 2012-4-9 android4.0上默认是Inet6Address
                        if (inetAddress != null && !inetAddress.isLoopbackAddress()
                                && (inetAddress instanceof Inet4Address)) {
                            // rmnet 表示移动数据
                            if (inferfaceType != null && !inferfaceType.startsWith("rmnet")) {
                                return inetAddress.getHostAddress().toString();
                            }
                        }
                    }
                }
            }

            for (Enumeration<NetworkInterface> en = NetworkInterface.getNetworkInterfaces(); en.hasMoreElements();) {
                NetworkInterface intf = en.nextElement();
                String inferfaceType = intf.getName();
                for (Enumeration<InetAddress> enumIpAddr = intf.getInetAddresses(); enumIpAddr.hasMoreElements();) {

                    while (enumIpAddr.hasMoreElements()) {
                        InetAddress inetAddress = enumIpAddr.nextElement();
                        // leiz 2012-4-9 android4.0上默认是Inet6Address
                        if (inetAddress != null && !inetAddress.isLoopbackAddress()
                                && (inetAddress instanceof Inet4Address)) {
                            if (inferfaceType != null && inferfaceType.startsWith("rmnet")) {
                                return inetAddress.getHostAddress().toString();
                            }
                        }
                    }
                }
            }
        } catch (SocketException ex) {
            Log.e("WifiPreference IpAddress", ex.toString());
        }

        return null;
    }
}
