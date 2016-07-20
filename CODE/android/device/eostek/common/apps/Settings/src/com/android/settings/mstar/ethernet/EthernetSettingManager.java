
package com.android.settings.mstar.ethernet;

import android.content.Context;
import android.os.IBinder;
import android.os.INetworkManagementService;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.util.Log;
import com.android.settings.network.ethernet.AbstracEthernetSettingManager;
import com.android.settings.network.ethernet.EthernetDisplayInfo;
import com.android.settings.network.ethernet.NetworkSettingUtils;
import com.mstar.android.ethernet.EthernetDevInfo;
import com.mstar.android.ethernet.EthernetManager;

//Only Used in platform like s628 or others which mstar.jar can be used.
public class EthernetSettingManager extends AbstracEthernetSettingManager {

    private static final String TAG = "EthernetSettingManager";

    private static EthernetSettingManager mEthernetSettingManager;

    private EthernetManager mEthernetManager;

    public static EthernetSettingManager getInstance() {
        if (mEthernetSettingManager == null) {
            mEthernetSettingManager = new EthernetSettingManager();
        }

        return mEthernetSettingManager;
    }

    private EthernetSettingManager() {
        mEthernetManager = EthernetManager.getInstance();
    }

    @Override
    public EthernetDisplayInfo getEthernetDisplayInfo() {
        EthernetDisplayInfo displayInfo = new EthernetDisplayInfo();

        if (mEthernetManager.isConfigured()) {
            EthernetDevInfo devInfo = mEthernetManager.getSavedConfig();

            String ifName = devInfo.getIfName();
            Log.d(TAG, "ifName, " + ifName);

            String ip = devInfo.getIpAddress();
            String netmask = devInfo.getNetMask();
            String defaultWay = devInfo.getRouteAddr();
            String firstDNS = devInfo.getDnsAddr();
            // Second DNS is not used for now.
            String secDNS = devInfo.getDns2Addr();

            boolean isAutoIp = isAutoIP();
            displayInfo.setIp(ip);
            displayInfo.setNetmask(netmask);
            displayInfo.setGateway(defaultWay);
            displayInfo.setDNS1(firstDNS);
            displayInfo.setAutoIp(isAutoIp);
        }

        return displayInfo;
    }

    @Override
    public boolean isAutoIP() {
        EthernetDevInfo mEthInfo = mEthernetManager.getSavedConfig();
        if (null != mEthInfo && mEthInfo.getConnectMode().equals(EthernetDevInfo.ETHERNET_CONN_MODE_DHCP)) {
            return true;
        }

        return false;
    }

    @Override
    public void configEthernetV4(EthernetDisplayInfo displayInfo) {

        if (displayInfo == null) {
            return;
        }

        boolean isAutoIp = displayInfo.isAutoIp();
        String ip = displayInfo.getIp();
        String netmask = displayInfo.getNetmask();
        String gateway = displayInfo.getGateway();
        String dns1 = displayInfo.getDNS1();

        EthernetDevInfo devInfo = new EthernetDevInfo();
        devInfo.setIfName("eth0");

        if (isAutoIp) {
            devInfo.setConnectMode(EthernetDevInfo.ETHERNET_CONN_MODE_DHCP);
            devInfo.setIpAddress(null);
            devInfo.setNetMask(null);
            devInfo.setRouteAddr(null);
            devInfo.setDnsAddr(null);
            devInfo.setDns2Addr(null);
        } else {
            devInfo.setConnectMode(EthernetDevInfo.ETHERNET_CONN_MODE_MANUAL);

            Log.i(TAG, ">>>configEthernetV4>>>>ip>>>>" + ip);
            if (NetworkSettingUtils.matchIP(ip)) {
                devInfo.setIpAddress(ip);
            } else {
                devInfo.setIpAddress(null);
            }

            Log.i(TAG, ">>>configEthernetV4>>>>netmask>>>>" + netmask);
            if (NetworkSettingUtils.matchIP(netmask)) {
                devInfo.setNetMask(netmask);
            } else {
                devInfo.setNetMask(null);
            }

            Log.i(TAG, ">>>configEthernetV4>>>>gateway>>>>" + gateway);
            if (NetworkSettingUtils.matchIP(gateway)) {
                devInfo.setRouteAddr(gateway);
            } else {
                devInfo.setRouteAddr(null);
            }

            Log.i(TAG, ">>>configEthernetV4>>>>dns1>>>>" + dns1);
            if (NetworkSettingUtils.matchIP(dns1)) {
                devInfo.setDnsAddr(dns1);
            } else {
                devInfo.setDnsAddr(null);
            }
        }
        mEthernetManager.updateDevInfo(devInfo);
    }

    @Override
    public void setEnabled(boolean b) {
        mEthernetManager.setEnabled(b);
    }

    @Override
    public void setNetwork() {
        try {
            final String interfaceName = SystemProperties.get("ethernet.interface", "eth0");

            IBinder b = ServiceManager.getService(Context.NETWORKMANAGEMENT_SERVICE);
            INetworkManagementService service = INetworkManagementService.Stub.asInterface(b);

            service.setInterfaceDown(interfaceName);
            service.setInterfaceUp(interfaceName);
        } catch (RemoteException ex) {
            Log.w(TAG, "Exception getting ethernet config: " + ex);
        }
    }
}
