
package com.eostek.isynergy.setmeup.config.network;

import java.lang.reflect.Constructor;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.net.InetAddress;
import java.net.UnknownHostException;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.util.Log;

import com.eostek.isynergy.setmeup.common.Constants;
import com.eostek.isynergy.setmeup.common.Constants.ACTION_TYPE;
import com.eostek.isynergy.setmeup.config.ServiceManager;
import com.eostek.isynergy.setmeup.config.nickname.DevNameService;
import com.eostek.isynergy.setmeup.service.WifiService;

/**
 * 对android 系统的AP进行管理 原生Android 对于AP 的管理是隐藏的，所以必须通过反射机制进行调用
 * 
 * @author nickyang
 */
public class WifiAPManager {
    private final String TAG = "WifiAPManager";

    private WifiManager manager;

    // 网络连接列表
    private List<WifiConfiguration> mWifiConfiguration = null;

    // 已经保存的wifi配置数量
    private int size = 0;

    private Context context = null;

    private boolean isMonitor = false;

    private WifiStateMonitorReceiver stateMonitor;

    MonitorHandler handler = new MonitorHandler();

    private final int MONITOR_FINISH = 0;

    private Object obj = new Object();

    private boolean isMonitorHandlerStop = false;

    private Thread monitorThread;

    private NetworkConfigure netConf;

    public WifiAPManager() {
        super();
    }

    public WifiAPManager(Context context) {
        this.context = context;
        manager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        mWifiConfiguration = manager.getConfiguredNetworks();
        if (mWifiConfiguration != null) {
            size = mWifiConfiguration.size();
            Log.d(TAG, "size=" + size);
        } else {
            size = 0;
        }
    }

    /**
     * 管理本地AP的开启和关闭 由于WIFI 和AP不能共存，所以如果开启AP，需要首先关闭WIFI
     * 
     * @param isEnabled true: 开启本地AP，并关闭WIFI false:关闭本地AP
     */
    public boolean setAPEnabled(WifiConfiguration wifiConfig, boolean isEnabled) {
        // 获取相应的类对象名称
        /*
         * Class<?> classType = WifiManager.class;
         * Log.d(TAG,"classType的值为:"+classType); try{ // 返回本类对象 Object
         * invokeOperation = classType.newInstance();
         */
        Log.d(TAG, "setAPEnabled function!");
        try {
            // 根据类对象名称去查找对应的方法
            Method addMethod = manager.getClass().getMethod("setWifiApEnabled", new Class[] {
                    WifiConfiguration.class, boolean.class
            });
            // Log.d(TAG, "得到addMethod");
            // 调用查找 到的方法执行此方法的处理
            Object result = addMethod.invoke(manager, new Object[] {
                    wifiConfig, isEnabled
            });
            // Log.d(TAG, "得到result");
            if (result.toString().equalsIgnoreCase("true")) {
                Log.d(TAG, isEnabled ? "open ap success..." : "close ap success...");
                return true;
            } else
                return false;
        } catch (Exception e) {
            Log.d(TAG, "" + e);
            return false;
        }

    }

    public boolean isWifiApEnabled() {
        boolean ret = false;
        try {
            Method methodObj = manager.getClass().getDeclaredMethod("isWifiApEnabled", new Class[] {});
            ret = (Boolean) methodObj.invoke(manager, new Object[] {});
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }

        return ret;
    }

    /**
     * 获取本机能扫描到的所有AP
     */
    public List<ScanResult> getAPs() {
        List<ScanResult> aps = manager.getScanResults();
        for (ScanResult ap : aps) {
            printAPInfo(ap);
        }
        return aps;
    }

    /**
     * 打印本机扫描到的所有AP信息
     */
    private void printAPInfo(ScanResult apInfo) {
        String con = apInfo.toString();
        Log.d(TAG, con);
    }

    /**
     * 开启wifi
     */
    public boolean startWifi() {
        Log.d(TAG, "startWifi function!");
        // 获取相应的类对象名称
        Class<?> classType = WifiManager.class;
        try {
            // 根据类对象名称去查找对应的方法
            Method addMethod = manager.getClass().getMethod("startWifi");
            Log.d(TAG, "startWifi():得到addMethod的值");
            // 调用查找 到的方法执行此方法的处理
            Object result = addMethod.invoke(manager);
            Log.d(TAG, "startWifi():得到result的值");
            if (result != null && ((Boolean) result).booleanValue()) {
                return true;
            } else
                return false;
        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "" + e);
            return false;
        }
    }

    /**
     * 获取当前的wifi状态并返回
     */
    public int getWifiState() {
        return manager.getWifiState();
    }

    /**
     * 获取AP的配置并返回
     */
    public WifiConfiguration getWifiApConfiguration() {
        Log.d(TAG, "getWifiApConfiguration function!");
        try {

            // 根据类对象名称去查找对应的方法
            Method addMethod = manager.getClass().getMethod("getWifiApConfiguration");
            // 调用查找 到的方法执行此方法的处理
            Object result = addMethod.invoke(manager);
            // ((WifiConfiguration)result).BSSID = "20:1A:06:3F:8A:74";
            Log.d(TAG, "getAp BSSID:" + ((WifiConfiguration) result).BSSID);
            return (WifiConfiguration) result;

        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "" + e);
            return null;
        }
    }

    /**
     * 创建一个特定的wifi配置，保留了密码和加密方式的设置功能
     */
    public WifiConfiguration CreateWifiConfig(String SSID, String Password, WifiCipherType Type) {
        Log.d(TAG, "trying to create wifi config...");
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = SSID;
        config.BSSID = "20:1A:06:3F:8A:74";

        WifiConfiguration existConfig = exsitingConfig(SSID);
        if (existConfig != null) {
            Log.d(TAG, "SSID :" + SSID + " existing and it will be removed....");
            this.manager.removeNetwork(existConfig.networkId);
        }

        switch (Type) {

        // 无密码
            case WIFICIPHER_NOPASS: {
                Log.d(TAG, "type " + WifiCipherType.WIFICIPHER_NOPASS);
                config.wepKeys[0] = "";
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                config.wepTxKeyIndex = 0;
                Log.d(TAG, "nopass!");
                break;
            }
            // WEP加密
            case WIFICIPHER_WEP: {
                Log.d(TAG, "type " + WifiCipherType.WIFICIPHER_WEP);
                config.preSharedKey = Password;
                config.hiddenSSID = true;
                config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
                config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
                config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
                config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
                config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
                config.wepTxKeyIndex = 0;
                break;
            }
            // WPA加密
            case WIFICIPHER_WPA: {
                Log.d(TAG, "type " + WifiCipherType.WIFICIPHER_WPA);
                config.preSharedKey = Password;
                config.hiddenSSID = true; //
                config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN); //
                config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP); //
                config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK); //
                config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP); //
                config.allowedProtocols.set(WifiConfiguration.Protocol.WPA); //
                config.status = WifiConfiguration.Status.ENABLED;
                break;
            }
            default: {
                return null;
            }
        }
        return config;
    }

    // 加密方式枚举类型
    public enum WifiCipherType {
        WIFICIPHER_NOPASS, WIFICIPHER_WEP, WIFICIPHER_WPA
    }

    /**
     * 重新连接wifi
     */
    public boolean reconnectWifi() {
        return manager.reconnect();
    }

    /**
     * 添加一个网络并连接
     */
    public boolean addconnectNetwork(WifiConfiguration wcg) {
        int wcgID = manager.addNetwork(wcg);
        boolean b = manager.enableNetwork(wcgID, true);
        if (!b) {
            return b;
        }
        b = manager.saveConfiguration();

        return b;
    }

    /**
     * 保存wifi的配置
     */
    public boolean saveConfiguration() {
        Log.d(TAG, "saveConfiguration function!");
        try {

            // 根据类对象名称去查找对应的方法
            Method addMethod = manager.getClass().getMethod("saveConfiguration");
            // 调用查找 到的方法执行此方法的处理
            Object result = addMethod.invoke(manager);
            Log.d(TAG, "saveConfiguration执行中得出result结果");
            if (result != null && ((Boolean) result).booleanValue()) {
                return true;
            } else
                return false;

        } catch (Exception e) {
            e.printStackTrace();
            Log.d(TAG, "" + e);
            return false;
        }
    }

    /**
     * 判断特定ssid的wifi是否存在
     */
    private WifiConfiguration exsitingConfig(String ssid) {
        List<WifiConfiguration> existingConfigs = manager.getConfiguredNetworks();
        if (existingConfigs != null) {
            for (WifiConfiguration existingConfig : existingConfigs) {
                Log.d(TAG, "ssid:" + ssid + " compare to ssid " + existingConfig.SSID);
                if (existingConfig.SSID.equals("\"" + ssid + "\"")) {
                    return existingConfig;
                }
            }
        }
        return null;
    }

    /**
     * 指定配置好的网络进行连接
     */
    public boolean connectConfiguration(int index) {
        Log.d(TAG, "connectConfiguration function!");

        if (mWifiConfiguration == null) {
            Log.d(TAG, "mWifiConfiguration=null");
            return false;
        }
        // 索引大于配置好的网络索引返回
        if (index > mWifiConfiguration.size()) {
            Log.d(TAG, "too big!");
            return false;
        }
        Log.d(TAG, "network index=" + index);
        // 保存当前的SSID到SharedPreferences里面
        savePreferences(index);
        // 连接配置好的指定ID的网络
        Log.d(TAG, "enableNetwork back=" + manager.enableNetwork(mWifiConfiguration.get(index).networkId, true));
        return manager.enableNetwork(mWifiConfiguration.get(index).networkId, true);
    }

    /**
     * 获取已保存配置的wifi数量
     */
    public int getSize() {
        return size;
    }

    /**
     * 将当前连接wifi的ssid保存，用于优先连接
     */
    public void savePreferences(int index) {
        Log.d(TAG, "savePreferences function!");
        SharedPreferences ssid = context.getSharedPreferences("SSID", 0);
        SharedPreferences.Editor editor = ssid.edit();
        editor.putInt("SSID", index);
        editor.commit();
    }

    /**
     * 网络变化的时候进行网络的切换 切换规则： 如果可以连接配置的wifi则连接wifi，如果不能则开启AP
     * 
     * @return SET_ME_UP_SUCCESS:成功 FAILED_SWITCH_NETWORK 失败
     */
    public int switchNetWork() {
        int ret = Constants.SuccessCode.SET_ME_UP_SUCCESS.getValue();
        try {
            // 当尝试30s后遍历wifi网络仍不能发现可用网络时，这时候会开启AP功能
            DevNameService extDevNameService = ServiceManager.getInstance(context).getDevNameService();
            String devName = extDevNameService.getDevName();

            WifiConfiguration ap = CreateWifiConfig(Constants.PREFIX_AP + devName, "", WifiCipherType.WIFICIPHER_NOPASS);
            boolean isSusOpenAP = false;
            if (ap != null) {
                isSusOpenAP = manager.setWifiEnabled(false);
                if (isSusOpenAP) {
                    isSusOpenAP = setAPEnabled(ap, true);
                    if (isSusOpenAP) {
                        Log.d(TAG, "open AP:" + devName + " and is Success " + isSusOpenAP);

                        return Constants.SuccessCode.SET_UP_WIFI_AP.getValue();
                    }
                }
            }

            ret = Constants.ErrorCode.FAILED_SWITCH_NETWORK.getValue();

        } catch (Exception e) {
            e.printStackTrace();
            ret = Constants.ErrorCode.FAILED_SWITCH_NETWORK.getValue();
        }

        return ret;
    }

    /**
     * 增加一个AP配置，并且连接指定AP
     * 
     * @param paras
     * @return
     */
    public int setWifi(String paras) {
        Log.d(TAG, "params =" + (paras == null ? "" : paras));
        try {
            netConf = parseNetConf(paras);
        } catch (NumberFormatException ex) {
            netConf = null;
            ex.printStackTrace();
        }

        if (netConf == null) {
            Log.d(TAG, "netConf = null");
            return Constants.ErrorCode.INVALID_PARA.getValue();
        }

        Log.d(TAG, "netConf =" + netConf.toString());

        settingWifiCallback(Constants.ACTION_TYPE.WIFI_SETTING, netConf.getSsid());

        WifiCipherType cipherType;

        if (String.valueOf(WifiCipherType.WIFICIPHER_NOPASS.ordinal()).equals(netConf.getSecurityMode())) {
            cipherType = WifiCipherType.WIFICIPHER_NOPASS;
        } else if (String.valueOf(WifiCipherType.WIFICIPHER_WEP.ordinal()).equals(netConf.getSecurityMode())) {
            cipherType = WifiCipherType.WIFICIPHER_WEP;
        } else if (String.valueOf(WifiCipherType.WIFICIPHER_WPA.ordinal()).equals(netConf.getSecurityMode())) {
            cipherType = WifiCipherType.WIFICIPHER_WPA;
        } else {
            Log.d(TAG, "INVALID_PARA");
            return Constants.ErrorCode.INVALID_PARA.getValue();
        }

        WifiConfiguration apConfi = getWifiApConfiguration();
        if (apConfi != null) {
            setAPEnabled(apConfi, false);
        }

        // 由于framework的bug，导致切换网络不会自动发出广播，所以在代码里头发送广播
        Log.d(TAG, "send broadcat to notify network switch...");

        Intent intent = new Intent("CUSTOM_CONNECT_STATUS_CHANGED");
        context.sendBroadcast(intent);

        Log.d(TAG, "is wifi enabled = " + (manager.isWifiEnabled()));

        if (!manager.isWifiEnabled()) {
            this.stateMonitor = new WifiStateMonitorReceiver(cipherType);
            IntentFilter filter = new IntentFilter(WifiManager.WIFI_STATE_CHANGED_ACTION);
            context.registerReceiver(stateMonitor, filter);
            isMonitor = true;
            this.manager.setWifiEnabled(true);

            // 由于开启wifi 是异步，为了保证能够正确切换网络，需要在WifiStateMonitorReceiver中异步处理后续操作
            return Constants.SuccessCode.SET_ME_UP_SUCCESS.getValue();
        } else {
            WifiConfiguration configure = CreateWifiInfo(netConf.getSsid(), netConf.getPw(), cipherType.ordinal());

            Log.d(TAG, "Was wifi had been configured = " + (configure == null));
            if (configure != null) {
                boolean isAdvanced = netConf.isAdvanced();
                String assType = netConf.getAssignmentType();

                Log.d(TAG, "Is advanced = " + isAdvanced + "   assignment type  = " + (assType));

                if (isAdvanced && Constants.ASSIGNMENT_TYPE_STATIC.equals(assType)) {
                    configStaticIp(configure, netConf);
                }

                boolean isSus = addconnectNetwork(configure);

                return isSus ? Constants.SuccessCode.SET_ME_UP_SUCCESS.getValue()
                        : Constants.ErrorCode.FAILED_WIFI_SETTING.getValue();
            }
        }

        Log.d(TAG, "INVALID_PARA");
        return Constants.ErrorCode.INVALID_PARA.getValue();

    }

    private NetworkConfigure parseNetConf(String paras) throws NumberFormatException {
        String paraArray[] = paras.split(Constants.SET_ME_UP_PARA_SPLIT);
        boolean isAdvanced = false;
        Log.d(TAG, "the length of params is " + paraArray.length);
        if (paraArray.length == 4)// 非高级设置
        {
            NetworkConfigure netInfo = new NetworkConfigure();
            netInfo.setSsid(paraArray[1]);
            netInfo.setSecurityMode(paraArray[2]);
            netInfo.setPw(paraArray[3]);
            netInfo.setAdvanced(isAdvanced);
            return netInfo;
        } else if (paraArray.length == 9)// 高级设置
        {
            NetworkConfigure netInfo = new NetworkConfigure();
            netInfo.setSsid(paraArray[1]);
            netInfo.setSecurityMode(paraArray[2]);
            netInfo.setPw(paraArray[3]);
            if ("1".equals(paraArray[4])) {
                isAdvanced = true;
            }
            netInfo.setAdvanced(isAdvanced);
            netInfo.setAssignmentType(paraArray[5]);
            netInfo.setIp(paraArray[6]);
            netInfo.setPrefixIp(Integer.valueOf(paraArray[7]));
            netInfo.setGateWay(paraArray[8]);
            return netInfo;
        }

        return null;
    }

    private void configStaticIp(WifiConfiguration configure, NetworkConfigure netInfo) {
        Log.d(TAG, "configStaticIp");
        InetAddress ip;
        String assTypeValue = "";

        if (netInfo == null) {
            return;
        }

        try {
            ip = InetAddress.getByName(netInfo.getIp());

            Log.d(TAG, "configStaticIp::ip" + ip);
            if (Constants.ASSIGNMENT_TYPE_STATIC.equals(netInfo.getAssignmentType())) {
                assTypeValue = Constants.ASSIGNMENT_TYPE_STATIC_VALUE;
            }
            setIpAssignment(assTypeValue, configure);// 静态IP
            setIpAddress(ip, netInfo.getPrefixIp(), configure); // 设置IP
            setGateway(InetAddress.getByName(netInfo.getGateWay()), configure);
            // DNS默认为255.255.255.0
            // setDNS(InetAddress.getByName(netInfo.getSubNetMask()),
            // configure);
        } catch (UnknownHostException e) {
            e.printStackTrace();
        } catch (SecurityException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (NoSuchFieldException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (InstantiationException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        }
    }

    public WifiConfiguration CreateWifiInfo(String SSID, String Password, int Type) {
        Log.d(TAG, "ssid = " + SSID + " Password = " + Password + " type == " + Type);
        WifiConfiguration config = new WifiConfiguration();
        config.allowedAuthAlgorithms.clear();
        config.allowedGroupCiphers.clear();
        config.allowedKeyManagement.clear();
        config.allowedPairwiseCiphers.clear();
        config.allowedProtocols.clear();
        config.SSID = "\"" + SSID + "\"";

        WifiConfiguration tempConfig = exsitingConfig(SSID);
        if (tempConfig != null) {
            manager.removeNetwork(tempConfig.networkId);
        }

        if (Type == 0) // WIFICIPHER_NOPASS
        {
            config.wepKeys[0] = "";
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        }
        if (Type == 1) // WIFICIPHER_WEP
        {
            config.hiddenSSID = true;
            config.wepKeys[0] = "\"" + Password + "\"";
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.SHARED);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP40);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.WEP104);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.NONE);
            config.wepTxKeyIndex = 0;
        }
        if (Type == 2) // WIFICIPHER_WPA
        {
            config.preSharedKey = "\"" + Password + "\"";
            config.hiddenSSID = true;
            config.allowedAuthAlgorithms.set(WifiConfiguration.AuthAlgorithm.OPEN);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.TKIP);
            config.allowedKeyManagement.set(WifiConfiguration.KeyMgmt.WPA_PSK);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.TKIP);
            // config.allowedProtocols.set(WifiConfiguration.Protocol.WPA);
            config.allowedGroupCiphers.set(WifiConfiguration.GroupCipher.CCMP);
            config.allowedPairwiseCiphers.set(WifiConfiguration.PairwiseCipher.CCMP);
            config.status = WifiConfiguration.Status.ENABLED;
        }
        return config;
    }

    /**
     * 关闭ap 并且激活wifi
     */
    public int enableWifi() {
        WifiConfiguration apConfi = getWifiApConfiguration();
        if (apConfi != null) {
            setAPEnabled(apConfi, false);
        }

        if (!manager.isWifiEnabled()) {
            this.manager.setWifiEnabled(true);

        }

        return Constants.SuccessCode.SET_ME_UP_SUCCESS.getValue();
    }

    class WifiStateMonitorReceiver extends BroadcastReceiver {
        private WifiCipherType cipherType;

        WifiStateMonitorReceiver(WifiCipherType cipherType) {
            this.cipherType = cipherType;

            if (monitorThread == null) {
                MonitorHandler task = new MonitorHandler();
                monitorThread = new Thread(task);
                monitorThread.start();
            }
        }

        @Override
        public void onReceive(Context arg0, Intent arg1) {
            String action = arg1.getAction();
            if (WifiManager.WIFI_STATE_CHANGED_ACTION.equalsIgnoreCase(action)) {
                int status = arg1.getIntExtra(WifiManager.EXTRA_WIFI_STATE, WifiManager.WIFI_STATE_UNKNOWN);
                if (status == WifiManager.WIFI_STATE_ENABLED) {
                    if (isMonitor) {
                        WifiConfiguration configure = CreateWifiInfo(netConf.getSsid(), netConf.getPw(),
                                cipherType.ordinal());

                        if (configure != null) {
                            boolean isAdvanced = netConf.isAdvanced();
                            String assType = netConf.getAssignmentType();

                            if (isAdvanced && Constants.ASSIGNMENT_TYPE_STATIC.equals(assType)) {
                                configStaticIp(configure, netConf);
                            }

                            boolean isSus = addconnectNetwork(configure);
                        }

                        isMonitor = false;
                        synchronized (obj) {
                            obj.notifyAll();
                        }

                    }
                }
            }
        }
    }

    @SuppressLint("HandlerLeak")
    class MonitorHandler implements Runnable {
        @Override
        public void run() {
            Log.d(TAG, "monitor handler thread started...");
            while (!isMonitorHandlerStop) {
                Log.d(TAG, "monitor handler thread running...");
                try {
                    Log.d(TAG, "monitor handler thread waitting...");
                    synchronized (obj) {
                        obj.wait();
                    }
                } catch (InterruptedException e) {
                }

                if (stateMonitor != null) {
                    context.unregisterReceiver(stateMonitor);
                    stateMonitor = null;
                }
            }

            Log.d(TAG, "monitor handler thread finished...");
        }
    }

    private int settingWifiCallback(ACTION_TYPE type, String wifi) {
        int ret = Constants.SuccessCode.SET_ME_UP_SUCCESS.getValue();
        Intent intent = new Intent(context, WifiService.class);
        intent.putExtra(Constants.NAME_ACTION_TYPE, type.getValue());
        intent.putExtra(Constants.NAME_PARAMETER, wifi);
        context.startService(intent);
        return ret;
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
