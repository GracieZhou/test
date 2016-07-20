
package com.eostek.isynergy.setmeup.config.adnetconfig;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.util.Log;

import com.eostek.isynergy.setmeup.common.Constants;
import com.eostek.isynergy.setmeup.common.Constants.ACTION_TYPE;
import com.eostek.isynergy.setmeup.config.IfService;

import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.provider.Settings;

public class AdvancedNetConfigService implements IfService {
    private final String TAG = "AdvancedNetConfigService";

    private Context context;

    private WifiManager manager;

    // private String currentSSID;
    private WifiConfiguration wifiConf = null;

    // private AdvancedNetConfigService adnetService;

    public AdvancedNetConfigService(Context context) {
        Log.d(TAG, "AdvancedNetConfigService construct!");
        this.context = context;

        manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        // 下面两行是保存当前连接wifi的ssid
        WifiInfo wifiInfo = manager.getConnectionInfo();

        List<WifiConfiguration> configuredNetworks = manager.getConfiguredNetworks();
        for (WifiConfiguration conf : configuredNetworks) {
            if (conf.networkId == wifiInfo.getNetworkId()) {
                wifiConf = conf;
                break;
            }
        }
    }

    @Override
    public int doAction(ACTION_TYPE type, String paras) {
        Log.d(TAG, "paras = " + paras);
        if (null == paras) {
            Log.d(TAG, "Advanced net config paras is null!");

        } else {
            Log.d(TAG, "Advanced net config paras is not null!");

        }

        int ret = Constants.ErrorCode.INVALID_PARA.getValue();

        if (paras != null && paras.length() > 0) {
            String paraArray[] = paras.split(Constants.SET_ME_UP_PARA_SPLIT);
            for (int i = 0; i < paraArray.length; i++) {
                Log.d(TAG, "paraArray[" + i + "]=" + paraArray[i]);
            }
            settingAdNetConfig(paraArray);
        }

        return ret;
    }

    // 设置高级网络的函数，接受参数para就是手机端传过来的参数
    private void settingAdNetConfig(String[] para) {
        switch (para.length) {
            case 2:
                break;
            case 3:
                break;
            case 4:
                try {
                    InetAddress ip = InetAddress.getByName(para[3]);
                    Log.d(TAG, "ip=" + ip);
                    setIpAssignment("STATIC", wifiConf);// 静态IP
                    setIpAddress(ip, 24, wifiConf); // 设置IP
                    setGateway(InetAddress.getByName("255.255.255.0"), wifiConf);// 设置网关默认255.255.255.0
                    setDNS(InetAddress.getByName("255.255.255.0"), wifiConf);// DNS默认为255.255.255.0
                    manager.updateNetwork(wifiConf); // 这里一定要进行这个更新操作，不然设置的内容可能会不生效
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            case 5: // 高级选项的四个参数，可能存在只传了部分参数的情况，要注意！！！
                try {
                    InetAddress ip = InetAddress.getByName(para[3]);
                    Log.d(TAG, "ip=" + ip);
                    InetAddress gateway = InetAddress.getByName(para[4]);
                    setIpAssignment("STATIC", wifiConf);// 静态IP
                    setIpAddress(ip, 24, wifiConf); // 设置IP
                    setGateway(gateway, wifiConf);// 设置网关
                    setDNS(InetAddress.getByName("255.255.255.0"), wifiConf);// DNS默认为255.255.255.0
                    manager.updateNetwork(wifiConf); // 这里一定要进行这个更新操作，不然设置的内容可能会不生效
                } catch (Exception e) {
                    e.printStackTrace();
                }
                break;
            default:
                ;
        }
        return;
    }

    // 设置ipAssignment 字段，有三个值：STATIC,DHCP和UNASSIGNED，代码中是直接设为静态STATIC
    public static void setIpAssignment(String assign, WifiConfiguration wifiConf) throws SecurityException,
            IllegalArgumentException, NoSuchFieldException, IllegalAccessException {
        setEnumField(wifiConf, assign, "ipAssignment");
    }

    // 设置IP地址的函数，注意，prefixLength在这里使用的时候用的是长度24
    public static void setIpAddress(InetAddress addr, int prefixLength, WifiConfiguration wifiConf)
            throws SecurityException, IllegalArgumentException, NoSuchFieldException, IllegalAccessException,
            NoSuchMethodException, ClassNotFoundException, InstantiationException, InvocationTargetException {
        Object linkProperties = getField(wifiConf, "linkProperties");
        if (linkProperties == null)
            return;
        Class laClass = Class.forName("android.net.LinkAddress");
        Constructor laConstructor = laClass.getConstructor(new Class[] {
                InetAddress.class, int.class
        });
        Object linkAddress = laConstructor.newInstance(addr, prefixLength);

        ArrayList mLinkAddresses = (ArrayList) getDeclaredField(linkProperties, "mLinkAddresses");
        mLinkAddresses.clear();
        mLinkAddresses.add(linkAddress);
    }

    // 设置网关的函数
    public static void setGateway(InetAddress gateway, WifiConfiguration wifiConf) throws SecurityException,
            IllegalArgumentException, NoSuchFieldException, IllegalAccessException, ClassNotFoundException,
            NoSuchMethodException, InstantiationException, InvocationTargetException {
        Object linkProperties = getField(wifiConf, "linkProperties");
        if (linkProperties == null)
            return;
        Class routeInfoClass = Class.forName("android.net.RouteInfo");
        Constructor routeInfoConstructor = routeInfoClass.getConstructor(new Class[] {
            InetAddress.class
        });
        Object routeInfo = routeInfoConstructor.newInstance(gateway);

        ArrayList mRoutes = (ArrayList) getDeclaredField(linkProperties, "mRoutes");
        mRoutes.clear();
        mRoutes.add(routeInfo);
    }

    // 设置DNS地址的函数
    public static void setDNS(InetAddress dns, WifiConfiguration wifiConf) throws SecurityException,
            IllegalArgumentException, NoSuchFieldException, IllegalAccessException {
        Object linkProperties = getField(wifiConf, "linkProperties");
        if (linkProperties == null)
            return;

        ArrayList<InetAddress> mDnses = (ArrayList<InetAddress>) getDeclaredField(linkProperties, "mDnses");
        mDnses.clear(); // or add a new dns address , here I just want to
                        // replace DNS1
        mDnses.add(dns);
    }

    public static Object getField(Object obj, String name) throws SecurityException, NoSuchFieldException,
            IllegalArgumentException, IllegalAccessException {
        Field f = obj.getClass().getField(name);
        Object out = f.get(obj);
        return out;
    }

    public static Object getDeclaredField(Object obj, String name) throws SecurityException, NoSuchFieldException,
            IllegalArgumentException, IllegalAccessException {
        Field f = obj.getClass().getDeclaredField(name);
        f.setAccessible(true);
        Object out = f.get(obj);
        return out;
    }

    public static void setEnumField(Object obj, String value, String name) throws SecurityException,
            NoSuchFieldException, IllegalArgumentException, IllegalAccessException {
        Field f = obj.getClass().getField(name);
        f.set(obj, Enum.valueOf((Class<Enum>) f.getType(), value));
    }
}
