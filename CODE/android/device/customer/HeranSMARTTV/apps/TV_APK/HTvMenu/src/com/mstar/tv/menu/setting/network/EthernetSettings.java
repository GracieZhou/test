
package com.mstar.tv.menu.setting.network;

import java.net.Inet4Address;
import java.net.InetAddress;
import java.net.InterfaceAddress;
import java.net.NetworkInterface;
import java.util.ArrayList;
import java.util.List;

import com.mstar.tv.menu.R;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.EthernetManager;
import android.net.IpConfiguration;
import android.net.IpConfiguration.IpAssignment;
import android.net.LinkAddress;
import android.net.LinkProperties;
import android.net.NetworkUtils;
import android.net.RouteInfo;
import android.net.StaticIpConfiguration;
import android.net.wifi.WifiManager;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Toast;

public class EthernetSettings extends NetworkSettings implements INetworkSettingsListener {

    private static final String TAG = "EthernetSettings";

    private Activity mContext;

    private EthernetSettingsHolder mEthernetHolder;

    private CheckBox mEthernetToggle;

    private CheckBox mAutoIpToggle;

    private CheckBox mIPv6Toggle;

    private final static String DEFAULT_IP = "0.0.0.0";

    private String ip;

    private String gateway;

    private String subnet;

    private String dns;

    private String dns2 = "";

    private List<String> mDnsList;

    private EthernetManager mEthernetManager;

    private IpConfiguration mIpConfiguration;

    private StaticIpConfiguration staticConfig;

    // foucs item on the right
    private int mSettingItem = Network_Constants.SETTING_ITEM_0;

    private final ConnectivityManager mConnectivityManager;

    public EthernetSettings(Activity networkSettingsActivity) {
        super(networkSettingsActivity);
        mContext = networkSettingsActivity;
        mConnectivityManager = (ConnectivityManager) mContext.getSystemService(Context.CONNECTIVITY_SERVICE);
        mEthernetHolder = new EthernetSettingsHolder(mContext);
        mEthernetToggle = mEthernetHolder.getEthernetToggleCheckBox();
        mAutoIpToggle = mEthernetHolder.getAutoIpCheckBox();
        mIPv6Toggle = mEthernetHolder.getIPv6CheckBox();
        mEthernetToggle.requestFocus();
        setListener();
        addNetworkSettingListener(this);

        mEthernetManager = getEthernetManager(mContext);

        mIpConfiguration = mEthernetManager.getConfiguration();

        staticConfig = new StaticIpConfiguration();

    }

    /**
     * ethernet setting layout visible.
     * 
     * @param visible if visible.
     */
    public void setVisible(boolean visible) {
        Log.d(TAG, "visible, " + visible);
        mEthernetHolder.setEthernetVisible(visible);
        if (visible) {
            showEthernetInfo();
        }
    }

    @Override
    public void onExit() {
        // mContext.unregisterReceiver(mEthStateReceiver);
    }

    @Override
    public boolean onKeyEvent(int keyCode, KeyEvent keyEvent) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_UP:
                if (mSettingItem > Network_Constants.SETTING_ITEM_0
                        && mSettingItem <= Network_Constants.SETTING_ITEM_8) {
                    mSettingItem--;
                    mEthernetHolder.requestFocus(mSettingItem);
                } else if (mSettingItem == Network_Constants.SETTING_ITEM_9) {
                    mSettingItem -= 2;
                    mEthernetHolder.requestFocus(mSettingItem);
                }
                return true;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                if (mSettingItem == Network_Constants.SETTING_ITEM_0) {
                    if (mEthernetHolder.isEthernetOpened()) {
                        mSettingItem++;
                        mEthernetHolder.requestFocus(mSettingItem);
                    } else {
                        return true;
                    }
                } else if (mSettingItem > Network_Constants.SETTING_ITEM_0
                        && mSettingItem <= Network_Constants.SETTING_ITEM_7) {
                    mSettingItem++;
                    mEthernetHolder.requestFocus(mSettingItem);
                }
                return true;
            case KeyEvent.KEYCODE_DPAD_LEFT:
                if (mSettingItem >= Network_Constants.SETTING_ITEM_3
                        && mSettingItem <= Network_Constants.SETTING_ITEM_7) {
                    return false;
                } else if (mSettingItem == Network_Constants.SETTING_ITEM_9) {
                    mSettingItem--;
                    mEthernetHolder.requestFocus(mSettingItem);
                    return true;
                }
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                if (mSettingItem >= Network_Constants.SETTING_ITEM_0
                        && mSettingItem <= Network_Constants.SETTING_ITEM_2) {
                    return true;
                } else if (mSettingItem >= Network_Constants.SETTING_ITEM_3
                        && mSettingItem <= Network_Constants.SETTING_ITEM_7) {
                    if (isLastFocused()) {
                        return true;
                    }
                } else if (mSettingItem == Network_Constants.SETTING_ITEM_8) {
                    mSettingItem++;
                    mEthernetHolder.requestFocus(mSettingItem);
                    return true;
                } else if (mSettingItem == Network_Constants.SETTING_ITEM_9) {
                    return true;
                }
                break;
            default:
                break;
        }

        return false;
    }

    @Override
    public void onWifiHWChanged(boolean isOn) {
        Log.d(TAG, "isOn, " + isOn);
        if (!isOn) {
            getEthernetManager(mContext).setEnabled(true);
            showEthernetInfo();
        }
    }

    @Override
    public void onFocusChange(boolean hasFocus) {
        if (hasFocus) {
            mEthernetHolder.requestFocus(Network_Constants.SETTING_ITEM_0);
        } else {
            mEthernetHolder.clearFocus(mSettingItem);
            mSettingItem = Network_Constants.SETTING_ITEM_0;
        }
    }

    public boolean isAutoIP() {
        if (mIpConfiguration.getIpAssignment().equals(IpAssignment.DHCP)) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isV4FirstFocused() {
        if (mEthernetHolder.isV4FirstFocused() || mEthernetHolder.isV6Focus()) {
            return true;
        } else {
            return false;
        }
    }

    public boolean isConfigEditTextFocused() {
        if (mSettingItem >= Network_Constants.SETTING_ITEM_3 && mSettingItem <= Network_Constants.SETTING_ITEM_7) {
            return true;
        } else {
            return false;
        }
    }

    private boolean isLastFocused() {
        if (mEthernetHolder.isV4LastFocused() || mEthernetHolder.isV6Focus()) {
            return true;
        } else {
            return false;
        }
    }

    private void saveEthernetConfig(boolean auto) {

        ip = mEthernetHolder.getEthernetV4Address();
        subnet = mEthernetHolder.getEthernetV4Netmask();
        gateway = mEthernetHolder.getEthernetV4Gateway();
        dns = mEthernetHolder.getEthernetV4Dns1();
        dns2 = mEthernetHolder.getEthernetV4Dns2();

        mIpConfiguration.setIpAssignment(auto ? IpAssignment.DHCP : IpAssignment.STATIC);
        if (auto) {
            mIpConfiguration.setStaticIpConfiguration(null);
        } else {
            if (staticConfig == null) {
                staticConfig = new StaticIpConfiguration();
            }
            Inet4Address inetAddr = null;
            try {
                inetAddr = (Inet4Address) NetworkUtils.numericToInetAddress(ip);
                staticConfig.ipAddress = new LinkAddress(inetAddr, calcPrefixLengthByMask(subnet));
                staticConfig.gateway = NetworkUtils.numericToInetAddress(gateway);
            } catch (Exception e) {
                showToast(R.string.check_ip_failure);
                return;
            }
            try {
                staticConfig.dnsServers.add(NetworkUtils.numericToInetAddress(dns));
                staticConfig.dnsServers.add(NetworkUtils.numericToInetAddress(dns2));
            } catch (Exception e) {
            }
            mIpConfiguration.setStaticIpConfiguration(staticConfig);
        }
        mEthernetManager.setConfiguration(mIpConfiguration);
    }

    private void configEthernetV6(boolean auto) {
    }

    @SuppressLint("NewApi")
    private void showEthernetInfoV6(String ifName) {
        InterfaceAddress address = getIPv6Address(ifName);
        if (address == null) {
            mEthernetHolder.refreshNetworkInfoV6("", "", "", "", "");
        } else {
            String ip = formatIPv6Address(address.getAddress().getHostAddress());
            mEthernetHolder.refreshNetworkInfoV6(ip, String.valueOf(address.getNetworkPrefixLength()), "", "", "");
        }
    }

    @SuppressLint("NewApi")
    private InterfaceAddress getIPv6Address(final String interfacename) {
        try {
            NetworkInterface info = NetworkInterface.getByName(interfacename);
            for (InterfaceAddress address : info.getInterfaceAddresses()) {
                // search ipv6 address
                if (address.getBroadcast() == null) {
                    Log.d(TAG, "address, " + address.getAddress().getHostAddress() + " preFixLength, "
                            + address.getNetworkPrefixLength());
                    return address;
                }
            }

            return null;
        } catch (Exception e) {
            Log.e(TAG, "NetworkInterface.getByName");

            return null;
        }
    }

    private String formatIPv6Address(final String ip) {
        if (ip == null) {
            return "";
        }

        if (ip.contains("%")) {
            return ip.substring(0, ip.indexOf("%"));
        }

        return ip;
    }

    private void showEthernetInfo() {
        // ethernet is enabled
        if (isEthernetEnabled()) {
            mEthernetToggle.setChecked(true);
        } else {
            mEthernetToggle.setChecked(false);
            return;
        }
        // ethernet had configured but network may not connect
        if (getConnectedEthernetConfigure()) {
            InputIPAddress.isForwardRightWithTextChange = false;
            if (mDnsList != null && mDnsList.size() == 1) {
                mEthernetHolder.refreshNetworkInfo(ip, subnet, gateway, mDnsList.get(0), null);
            } else if (mDnsList != null && mDnsList.size() >= 2) {
                mEthernetHolder.refreshNetworkInfo(ip, subnet, gateway, mDnsList.get(0), mDnsList.get(1));
            }
            InputIPAddress.isForwardRightWithTextChange = true;
        }
        // auto ip.
        if (isAutoIP()) {
            mEthernetHolder.setAutoIpOpend(true);
        } else {
            mEthernetHolder.setAutoIpOpend(false);
        }
        mEthernetHolder.setV4EditTextWritable(!isAutoIP());
    }

    public String getEthernetNetmask() {
        LinkProperties linkProperties = mConnectivityManager.getLinkProperties(ConnectivityManager.TYPE_ETHERNET);
        if (linkProperties != null && linkProperties.getAllLinkAddresses() != null) {
            for (LinkAddress linkAddress : linkProperties.getAllLinkAddresses()) {
                int length = linkAddress.getNetworkPrefixLength();
                String result = "";
                result = calcMaskByPrefixLength(length);
                return result;
            }
        }
        return "";
    }

    public String getEthernetIpAddress() {
        LinkProperties linkProperties = mConnectivityManager.getLinkProperties(ConnectivityManager.TYPE_ETHERNET);
        if (linkProperties != null && linkProperties.getAllLinkAddresses() != null) {
            for (LinkAddress linkAddress : linkProperties.getAllLinkAddresses()) {
                InetAddress address = linkAddress.getAddress();
                if (address instanceof Inet4Address) {
                    return address.getHostAddress();
                }
            }
        }

        return "";
    }

    public String getEthernetGateway() {
        LinkProperties linkProperties = mConnectivityManager.getLinkProperties(ConnectivityManager.TYPE_ETHERNET);
        if (linkProperties != null) {
            List<RouteInfo> mInfos = linkProperties.getRoutes();
            if (mInfos != null) {
                for (RouteInfo routeInfo : mInfos) {
                    InetAddress address = routeInfo.getGateway();
                    if (address instanceof Inet4Address) {
                        String gateway = address.getHostAddress();
                        if ("0.0.0.0".equals(gateway)) {
                            continue;
                        }
                        return gateway;
                    }
                }
            }
        }
        return "";
    }

    public List<String> getEthernetDnsList() {
        List<String> dnsList = null;
        LinkProperties linkProperties = mConnectivityManager.getLinkProperties(ConnectivityManager.TYPE_ETHERNET);
        if (linkProperties != null) {
            List<InetAddress> mAddresses = linkProperties.getDnsServers();
            if (mAddresses != null && mAddresses.size() > 0) {
                dnsList = new ArrayList<String>(mAddresses.size());
                for (InetAddress inetAddress : mAddresses) {
                    if (inetAddress instanceof Inet4Address) {
                        dnsList.add(inetAddress.getHostAddress());
                    }
                }
            }
        }
        return dnsList;
    }

    private String calcMaskByPrefixLength(int length) {
        int mask = -1 << (32 - length);
        int partsNum = 4;
        int bitsOfPart = 8;
        int maskParts[] = new int[partsNum];
        int selector = 0x000000ff;

        for (int i = 0; i < maskParts.length; i++) {
            int pos = maskParts.length - 1 - i;
            maskParts[pos] = (mask >> (i * bitsOfPart)) & selector;
        }

        String result = "";
        result = result + maskParts[0];
        for (int i = 1; i < maskParts.length; i++) {
            result = result + "." + maskParts[i];
        }
        return result;
    }

    private int calcPrefixLengthByMask(String netMask) {
        int length = 0;
        String[] strs = netMask.split("\\.");
        for (int i = 0; i < strs.length; i++) {
            if (!"0".equals(strs[i])) {
                Log.i("wenxin", Integer.toBinaryString((Integer.parseInt(strs[i]))));
                length += (Integer.toBinaryString((Integer.parseInt(strs[i]))).length());
            }
        }
        return length;
    }

    private boolean getConnectedEthernetConfigure() {
        ip = getEthernetIpAddress();
        subnet = getEthernetNetmask();
        gateway = getEthernetGateway();
        mDnsList = getEthernetDnsList();
        if (ip.length() > 0) {
            return true;
        } else {
            ip = DEFAULT_IP;
        }
        return false;
    }

    @SuppressWarnings("unused")
    private void showToast(int id) {
        if (id <= 0) {
            return;
        }

        Toast.makeText(mContext, id, Toast.LENGTH_SHORT).show();
    }

    private void setListener() {
        mEthernetToggle.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {
                // modify begin by charles.tai [2014-02-10]
                WifiManager wifiManager = getWifiManager();
                /*
                 * if (view instanceof CheckBox) { CheckBox checkBox =
                 * (CheckBox) view; // check wifi state if
                 * (getWifiManager().isWifiEnabled()) { // modify by ken.bi
                 * //showToast(R.string.open_ethernet_hint);
                 * //mEthernetToggle.setChecked(!checkBox.isChecked());
                 * mEthernetToggle.setChecked(checkBox.isChecked()); return; }
                 * else
                 */

                boolean enabled = isEthernetEnabled();
                if (enabled) {
                    // set ethernet disable
                    mEthernetManager.setEnabled(false);
                    // set the wifi is enabled
                    wifiManager.setWifiEnabled(true);
                } else {
                    // set ethernet enable
                    mEthernetManager.setEnabled(true);
                    // set the wifi is disabled
                    wifiManager.setWifiEnabled(false);
                }
                // modify end by charles.tai [2014-02-10]
                showEthernetInfo();
            }
        });

        mAutoIpToggle.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // may be unuseful
            }
        });

        mIPv6Toggle.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                // may be unuseful
                if (isChecked) {
                    // hard code
                    showEthernetInfoV6("eth0");
                }
            }
        });

        mEthernetHolder.getSaveButton().setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {
                // update ethernet v6 config
                if (mIPv6Toggle.isChecked()) {
                    configEthernetV6(mAutoIpToggle.isChecked());
                } else {
                    // configEthernetV4(mAutoIpToggle.isChecked());
                    saveEthernetConfig(mAutoIpToggle.isChecked());
                }
                mContext.finish();
            }

        });

        mEthernetHolder.getCancelButton().setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View view) {
                // refresh ip, dns, etc
                showEthernetInfo();
                mContext.finish();
            }
        });
    }

}
