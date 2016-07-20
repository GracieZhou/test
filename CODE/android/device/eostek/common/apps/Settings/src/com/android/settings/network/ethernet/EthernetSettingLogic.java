
package com.android.settings.network.ethernet;

import java.lang.reflect.AccessibleObject;
import java.lang.reflect.Constructor;
import java.lang.reflect.Method;
import java.net.Inet4Address;
import java.net.InetAddress;

import scifly.middleware.network.EthernetManagerGlobal;
import scifly.middleware.network.IpConfig;
import scifly.middleware.network.IpConfig.IpAssignment;
import scifly.middleware.network.StaticIpConfig;

import android.content.Context;
import android.net.LinkAddress;
import android.net.NetworkUtils;
import android.os.Build;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.settings.R;
import com.android.settings.network.wifi.LinkPropertyInputDialog;
import com.android.settings.network.wifi.LinkPropertyInputDialog.PropertyChangeListener;

public class EthernetSettingLogic {

    private EthernetSettingFragment mFragment;

    private Context mContext;

    private final static String nullIpInfo = "0.0.0.0";

    private static final String TAG = "EthernetSettingLogic";

    private TextView mIpTextView;

    private TextView mGatewayTextView;

    private TextView mDnsTextView;

    private TextView mSubnetTextView;

    private String mIpStr;

    private String mGatewayStr;

    private String mDnsStr;

    private String mSubnetMaskStr;

    private boolean isAutoGetIp = true;

    private View mView;

    // AbstracEthernetSettingManager mEthernetSettingManager;
    private EthernetManagerGlobal mEthernetManagerGlobal;

    EthernetDisplayInfo mDisplayInfo;

    private static final int ANIMATION_LEFT_ARROW_FLASH = 0;

    private static final int ANIMATION_RIGHT_ARROW_FLASH = 1;

    private static final int ANIMATION_LEFT_ARROW_FLASH_RECOVERY = 2;

    private static final int ANIMATION_RIGHT_ARROW_FLASH_RECOVERY = 3;

    private Handler mHandler = new Handler() {

        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case ANIMATION_LEFT_ARROW_FLASH:
                    View view = (View) msg.obj;
                    if (view != null) {
                        ImageView left = (ImageView) view.findViewById(R.id.auto_way_left_arrow);
                        if (left != null) {
                            left.setImageResource(R.drawable.arrow_left_pressed);
                            Message newMsg = obtainMessage();
                            newMsg.obj = view;
                            newMsg.what = ANIMATION_LEFT_ARROW_FLASH_RECOVERY;
                            sendMessageDelayed(newMsg, 100);
                        }
                    }
                    break;
                case ANIMATION_RIGHT_ARROW_FLASH:
                    view = (View) msg.obj;
                    if (view != null) {
                        ImageView right = (ImageView) view.findViewById(R.id.auto_way_right_arrow);
                        if (right != null) {
                            right.setImageResource(R.drawable.arrow_right_pressed);
                            Message newMsg = obtainMessage();
                            newMsg.obj = view;
                            newMsg.what = ANIMATION_RIGHT_ARROW_FLASH_RECOVERY;
                            sendMessageDelayed(newMsg, 100);
                        }
                    }
                    break;
                case ANIMATION_LEFT_ARROW_FLASH_RECOVERY:
                    view = (View) msg.obj;
                    if (view != null) {
                        ImageView left = (ImageView) view.findViewById(R.id.auto_way_left_arrow);
                        if (left != null) {
                            left.setImageResource(R.drawable.arrow_left_normal);
                        }
                    }
                    break;
                case ANIMATION_RIGHT_ARROW_FLASH_RECOVERY:
                    view = (View) msg.obj;
                    if (view != null) {
                        ImageView right = (ImageView) view.findViewById(R.id.auto_way_right_arrow);
                        if (right != null) {
                            right.setImageResource(R.drawable.arrow_right_normal);
                        }
                    }
                    break;
            }
        }
    };

    public EthernetSettingLogic(EthernetSettingFragment f, View root) {
        mFragment = f;
        mContext = f.getActivity();
        mView = root;

        // init middleware
        mEthernetManagerGlobal = new EthernetManagerGlobal(mContext);
        processInfo();
    }

    private void processInfo() {
        IpConfig ipConfig = mEthernetManagerGlobal.getConfiguration();
        if (ipConfig == null) {
            return;
        }

        mDisplayInfo = new EthernetDisplayInfo();
        isAutoGetIp = ipConfig.ipAssignment == IpAssignment.DHCP ? true : false;
        mDisplayInfo.setAutoIp(isAutoGetIp);

        StaticIpConfig staticIpConfig = ipConfig.getStaticIpConfig();
        if (staticIpConfig != null) {
            mIpStr = staticIpConfig.ipAddress.getAddress().getHostAddress();
            mDisplayInfo.setIp(mIpStr);

            mGatewayStr = staticIpConfig.gateway.getHostAddress();
            mDisplayInfo.setGateway(mGatewayStr);

            mDnsStr = staticIpConfig.dnsServers.get(0).getHostAddress();
            mDisplayInfo.setDNS1(mDnsStr);

            mSubnetMaskStr = prefixLengthToNetmask(staticIpConfig.ipAddress.getNetworkPrefixLength());
            mDisplayInfo.setNetmask(mSubnetMaskStr);
        }

        if (TextUtils.isEmpty(mIpStr)) {
            mIpStr = nullIpInfo;
        }

        if (TextUtils.isEmpty(mGatewayStr)) {
            mGatewayStr = nullIpInfo;
        }

        if (TextUtils.isEmpty(mDnsStr)) {
            mDnsStr = nullIpInfo;
        }

        Log.i(TAG, "mIpStr " + mIpStr);
        Log.i(TAG, "mGatewayStr " + mGatewayStr);
        Log.i(TAG, "mDnsStr " + mDnsStr);
        Log.i(TAG, "mSubnetMaskStr " + mSubnetMaskStr);

        initViews();
    }

    private void initViews() {
        setItem(R.id.auto_get, R.string.wifi_link_property_ip_get, isAutoGetIp ? R.string.wifi_link_property_auto
                : R.string.wifi_link_property_manual);
        setItem(R.id.ip_address, R.string.wifi_ip_address, R.string.ethernet_default_address);
        setItem(R.id.subnet_mask, R.string.wifi_mask, R.string.ethernet_default_address);
        setItem(R.id.gateway, R.string.wifi_gateway, R.string.ethernet_default_address);
        setItem(R.id.dns, R.string.wifi_dns, R.string.ethernet_default_address);
        setItemsBackground(!isAutoGetIp);
    }

    public void setItem(int id, int titleId, int contentId) {
        setItem(id, mContext.getResources().getString(titleId), mContext.getResources().getString(contentId));
    }

    public void setItem(final int id, String title, String defaultContent) {
        View item = mView.findViewById(id);
        if (item == null) {
            return;
        }

        View selector = item.findViewById(R.id.link_property_selector_part);
        TextView titleTv = (TextView) item.findViewById(R.id.config_item_title);
        TextView contentTv = (TextView) item.findViewById(R.id.config_item_content);
        switch (id) {
            case R.id.auto_get:
                break;
            case R.id.ip_address:
                mIpTextView = contentTv;

                if (!TextUtils.isEmpty(mIpStr)) {
                    defaultContent = mIpStr;
                }
                break;
            case R.id.subnet_mask:
                mSubnetTextView = contentTv;

                if (!TextUtils.isEmpty(mSubnetMaskStr)) {
                    defaultContent = mSubnetMaskStr;
                }
                break;
            case R.id.gateway:
                mGatewayTextView = contentTv;

                if (!TextUtils.isEmpty(mGatewayStr)) {
                    defaultContent = mGatewayStr;
                }
                break;
            case R.id.dns:
                mDnsTextView = contentTv;

                if (!TextUtils.isEmpty(mDnsStr)) {
                    defaultContent = mDnsStr;
                }
                break;
        }

        if (titleTv != null) {
            titleTv.setText("" + title);
        }
        if (contentTv != null) {
            contentTv.setText("" + defaultContent);
        }

        selector.setOnKeyListener(new OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_UP) {
                    return false;
                }

                if (keyCode != KeyEvent.KEYCODE_DPAD_LEFT && keyCode != KeyEvent.KEYCODE_DPAD_RIGHT) {
                    return false;
                }

                switch (id) {
                    case R.id.auto_get:
                        changeAutoWay();

                        Message msg = mHandler.obtainMessage();
                        if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                            msg.obj = v;
                            msg.what = ANIMATION_LEFT_ARROW_FLASH;
                            mHandler.sendMessage(msg);
                        } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                            msg.obj = v;
                            msg.what = ANIMATION_RIGHT_ARROW_FLASH;
                            mHandler.sendMessage(msg);
                        }
                        return true;
                }
                return false;
            }
        });

        selector.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                switch (id) {
                    case R.id.auto_get:
                        changeAutoWay();
                        break;
                    case R.id.ip_address:
                        if (!isAutoGetIp) {
                            LinkPropertyInputDialog dialog = new LinkPropertyInputDialog(mContext, mIpStr);
                            dialog.show();
                            dialog.setTitleWidgetText(mContext.getString(R.string.ethernet_setting) + " > "
                                    + mContext.getString(R.string.IP_address));
                            dialog.setPropertyChangeListener(new PropertyChangeListener() {
                                @Override
                                public void onPropertyChange(String value) {
                                    Log.i(TAG, "mIpStr value received ::" + value);

                                    if (TextUtils.isEmpty(value)) {
                                        Log.i(TAG, "wifi_ip_settings_invalid_ip_address");
                                        return;
                                    }

                                    InetAddress inetAddr = null;
                                    try {
                                        inetAddr = NetworkUtils.numericToInetAddress(value);
                                        if (inetAddr != null) {
                                            mIpStr = value;

                                            setItem(R.id.ip_address,
                                                    mContext.getResources().getString(R.string.wifi_ip_address), mIpStr);
                                        }

                                    } catch (IllegalArgumentException e) {
                                        Log.i(TAG, "wifi_ip_settings_invalid_ip_address");
                                        return;
                                    }
                                }
                            });
                        }
                        break;
                    case R.id.subnet_mask:
                        if (!isAutoGetIp) {
                            LinkPropertyInputDialog dialog = new LinkPropertyInputDialog(mContext, mSubnetMaskStr);
                            dialog.show();
                            dialog.setTitleWidgetText(mContext.getString(R.string.ethernet_setting) + " > "
                                    + mContext.getString(R.string.wifi_mask));
                            dialog.setPropertyChangeListener(new PropertyChangeListener() {
                                @Override
                                public void onPropertyChange(String value) {
                                    if (NetworkSettingUtils.isValidMaskString(value)) {

                                        mSubnetMaskStr = value;
                                        setItem(R.id.subnet_mask,
                                                mContext.getResources().getString(R.string.wifi_mask), mGatewayStr);
                                    } else {
                                        Toast.makeText(mContext,
                                                mContext.getResources().getString(R.string.wifi_invliad_subnet_mask),
                                                Toast.LENGTH_SHORT).show();
                                        Log.i(TAG, "Invalid subnet mask!");
                                    }
                                }
                            });
                        }
                        break;
                    case R.id.gateway:
                        if (!isAutoGetIp) {
                            LinkPropertyInputDialog dialog = new LinkPropertyInputDialog(mContext, mGatewayStr);
                            dialog.show();
                            dialog.setTitleWidgetText(mContext.getString(R.string.ethernet_setting) + " > "
                                    + mContext.getString(R.string.gateway));
                            dialog.setPropertyChangeListener(new PropertyChangeListener() {
                                @Override
                                public void onPropertyChange(String value) {
                                    Log.i(TAG, "mGatewayStr value received ::" + value);

                                    if (TextUtils.isEmpty(value)) {
                                        Log.i(TAG, "wifi_ip_settings_invalid_ip_address");
                                        return;
                                    }

                                    InetAddress gatewayAddr = null;
                                    try {
                                        gatewayAddr = NetworkUtils.numericToInetAddress(value);
                                        if (gatewayAddr != null) {
                                            mGatewayStr = value;
                                            setItem(R.id.gateway,
                                                    mContext.getResources().getString(R.string.wifi_gateway),
                                                    mGatewayStr);
                                        }

                                    } catch (IllegalArgumentException e) {
                                        return;
                                    }
                                }
                            });
                        }
                        break;
                    case R.id.dns:
                        if (!isAutoGetIp) {
                            LinkPropertyInputDialog dialog = new LinkPropertyInputDialog(mContext, mDnsStr);
                            dialog.show();
                            dialog.setTitleWidgetText(mContext.getString(R.string.ethernet_setting) + " > "
                                    + mContext.getString(R.string.wifi_dns));
                            dialog.setPropertyChangeListener(new PropertyChangeListener() {
                                @Override
                                public void onPropertyChange(String value) {
                                    Log.i(TAG, "mDnsStr value received ::" + value);

                                    if (TextUtils.isEmpty(value)) {
                                        Log.i(TAG, "wifi_ip_settings_invalid_dns_address");
                                        return;
                                    }

                                    InetAddress dnsAddr = null;
                                    try {
                                        dnsAddr = NetworkUtils.numericToInetAddress(value);
                                        if (dnsAddr != null) {
                                            mDnsStr = value;
                                            setItem(R.id.dns, mContext.getResources().getString(R.string.wifi_dns),
                                                    mDnsStr);
                                        }
                                    } catch (IllegalArgumentException e) {
                                        Log.i(TAG, "wifi_ip_settings_invalid_dns_address");
                                        return;
                                    }
                                }
                            });
                        }
                        break;
                }
            }
        });
    }

    private void changeAutoWay() {
        if (isAutoGetIp) {
            setItem(R.id.auto_get, R.string.wifi_link_property_ip_get, R.string.wifi_link_property_manual);
            setItemsBackground(true);
        } else {
            setItem(R.id.auto_get, R.string.wifi_link_property_ip_get, R.string.wifi_link_property_auto);
            setItemsBackground(false);
        }
        isAutoGetIp = !isAutoGetIp;
    }

    protected void setItemsBackground(boolean enabled) {
        setItemsBackground(R.id.ip_address, enabled);
        setItemsBackground(R.id.gateway, enabled);
        setItemsBackground(R.id.subnet_mask, enabled);
        setItemsBackground(R.id.dns, enabled);
    }

    private void setItemsBackground(int id, boolean enabled) {
        View item = mView.findViewById(id);
        if (item == null) {
            return;
        }

        TextView contentTv = (TextView) item.findViewById(R.id.config_item_content);
        if (contentTv != null) {
            if (enabled) {
                contentTv.setTextColor(mContext.getResources().getColor((android.R.color.white)));
            } else {
                contentTv.setTextColor(mContext.getResources().getColor((android.R.color.darker_gray)));
            }
        }
    }

    public void saveEthernetConfig() {
        EthernetDisplayInfo displayInfo = new EthernetDisplayInfo();
        displayInfo.setAutoIp(isAutoGetIp);
        displayInfo.setIp(mIpStr);
        displayInfo.setNetmask(mSubnetMaskStr);
        displayInfo.setGateway(mGatewayStr);
        displayInfo.setDNS1(mDnsStr);
        if (mDisplayInfo != null && !mDisplayInfo.equals(displayInfo)) {
            // mEthernetManagerGlobal.setEnabled(true);
            Log.i(TAG, "need to config!");
            configEthernet(displayInfo);
        } else {
            Log.i(TAG, "no change ,no need to config!");
        }
    }

    private String prefixLengthToNetmask(int prefixLength) {
        if (prefixLength < 0 || prefixLength > 32) {
            Log.w(TAG, "Invalid prefix length (0 <= prefix <= 32)");
            throw new IllegalArgumentException("illegal argument prefixLength");
        }

        int value = 0xffffffff << (32 - prefixLength);
        int netmask = Integer.reverseBytes(value);

        StringBuffer sb = new StringBuffer();
        sb.append(String.valueOf(netmask & 0xff));
        sb.append('.');
        sb.append(String.valueOf((int) ((netmask >> 8) & 0xff)));
        sb.append('.');
        sb.append(String.valueOf((int) ((netmask >> 16) & 0xff)));
        sb.append('.');
        sb.append(String.valueOf((int) ((netmask >> 24) & 0xff)));

        return sb.toString();
    }

    private int netmaskToPrefixLength(String netmask) {
        String[] tmp = netmask.split("\\.");
        int cnt = 0;
        for (String cell : tmp) {
            int i = Integer.parseInt(cell);
            cnt += Integer.bitCount(i);
        }

        return cnt;
    }

    private void configEthernet(EthernetDisplayInfo displayInfo) {
        if (displayInfo == null) {
            Log.w(TAG, "illegal argument EthernetDisplayInfo");
            return;
        }
        Log.d(TAG, "displayInfo : " + displayInfo.toString());

        IpConfig ipConfig = new IpConfig();
        if (displayInfo.isAutoIp()) {
            ipConfig.ipAssignment = IpAssignment.DHCP;
            ipConfig.setStaticIpConfig(null);
        } else {
            ipConfig.ipAssignment = IpAssignment.STATIC;
            StaticIpConfig staticConfig = new StaticIpConfig();
            ipConfig.setStaticIpConfig(staticConfig);

            String ipAddr = displayInfo.getIp();
            if (TextUtils.isEmpty(ipAddr)) {
                Log.w(TAG, "ip is null.");
                return;
            }

            Inet4Address inetAddr = null;
            try {
                inetAddr = (Inet4Address) NetworkUtils.numericToInetAddress(ipAddr);
            } catch (IllegalArgumentException e) {
                Log.e(TAG, e.getMessage());
                return;
            }

            int networkPrefixLength = netmaskToPrefixLength(displayInfo.getNetmask());
            if (networkPrefixLength < 0 || networkPrefixLength > 32) {
                Log.w(TAG, "illegal argument netmask");
                return;
            }

            try {
                staticConfig.ipAddress = new LinkAddress(inetAddr, networkPrefixLength);
            } catch (NumberFormatException e) {
                Log.e(TAG, e.getMessage());
                return;
            }

            String gateway = displayInfo.getGateway();
            if (!TextUtils.isEmpty(gateway)) {
                try {
                    staticConfig.gateway = (Inet4Address) NetworkUtils.numericToInetAddress(gateway);
                } catch (IllegalArgumentException e) {
                    return;
                }
            }

            String dns1 = displayInfo.getDNS1();
            if (!TextUtils.isEmpty(dns1)) {
                try {
                    staticConfig.dnsServers.add((Inet4Address) NetworkUtils.numericToInetAddress(dns1));
                } catch (IllegalArgumentException e) {
                    return;
                }
            }
        }

        mEthernetManagerGlobal.setConfiguration(ipConfig);
    }
}
