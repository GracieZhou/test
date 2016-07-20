
package com.android.settings.network.wifi;

import java.net.InetAddress;

import scifly.middleware.network.IpConfig;
import scifly.middleware.network.IpConfig.IpAssignment;
import scifly.middleware.network.NetworkUtil;
import scifly.middleware.network.StaticIpConfig;
import scifly.middleware.network.WifiConfig;
import android.annotation.SuppressLint;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.net.LinkAddress;
import android.net.LinkProperties;
import android.net.NetworkUtils;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.settings.R;
import com.android.settings.network.ethernet.NetworkSettingUtils;
import com.android.settings.network.wifi.LinkPropertyInputDialog.PropertyChangeListener;
import com.android.settings.util.Utils;
import com.android.settings.widget.TitleWidget;

/**
 * show ip,gateway,dns and subnet mask of current network.
 */
public class LinkPropertyConfigDialog extends AlertDialog {

    public static final int OPERATION_CODE_FORGET = 0;

    public static final int OPERATION_CODE_CONNECT = 1;

    private int mFromWhere = 0;

    public static final int FROM_WIFI = 0;

    public static final int FROM_WIRE = 1;

    private static final String TAG = "LinkPropertyConfigDialog";

    private static final int ANIMATION_LEFT_ARROW_FLASH = 0;

    private static final int ANIMATION_RIGHT_ARROW_FLASH = 1;

    private static final int ANIMATION_LEFT_ARROW_FLASH_RECOVERY = 2;

    private static final int ANIMATION_RIGHT_ARROW_FLASH_RECOVERY = 3;

    private AccessPoint mAccessPoint;

    LinkProperties mLinkProperties;

    private Context mContext;

    private String mIpStr;

    private String mGatewayStr;

    private String mDnsStr;

    private String mSubnetMaskStr;

    private TextView mGatewayTextView;

    private TextView mDnsTextView;

    private Button mSubmitButton;

    private boolean isAutoGetIp = true;

    private OnConfigListener mOnConfigListener;

    private OnSubmitClickListener mOnSubmitClickListener;

    private int mOperationCode = -1;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {

            switch (msg.what) {
                case ANIMATION_LEFT_ARROW_FLASH:
                    View view = (View) msg.obj;
                    if (view != null) {
                        ImageView left = (ImageView) view.findViewById(R.id.auto_way_left_arrow);
                        if (left != null) {
                            left.setImageResource(R.drawable.arrow_left_green);
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
                            right.setImageResource(R.drawable.arrow_right_green);
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
                            left.setImageResource(R.drawable.arrow_left_white);
                        }
                    }
                    break;
                case ANIMATION_RIGHT_ARROW_FLASH_RECOVERY:
                    view = (View) msg.obj;
                    if (view != null) {
                        ImageView right = (ImageView) view.findViewById(R.id.auto_way_right_arrow);
                        if (right != null) {
                            right.setImageResource(R.drawable.arrow_right_white);
                        }
                    }
                    break;
            }

        };
    };

    protected LinkPropertyConfigDialog(Context context, AccessPoint accessPoint) {
        super(context);
        this.mAccessPoint = accessPoint;
        mContext = context;

        if (accessPoint != null) {
            if (accessPoint.getConfig() != null) {
                WifiConfig config = accessPoint.getConfig();
                isAutoGetIp = config.getIpAssignment() == IpAssignment.DHCP ? true : false;
            }
            processInfo(accessPoint);
        }
    }

    private void processInfo(AccessPoint accessPoint) {
        if (mAccessPoint.getConfig() == null) {
            mAccessPoint.setConfig(mAccessPoint.buildConfigFromResult(isAutoGetIp));
        }

        // mLinkProperties = accessPoint.getConfig().linkProperties;
        IpConfig ipConfig = accessPoint.getConfig().getIpConfiguration();
        StaticIpConfig staticIpConfig = ipConfig.getStaticIpConfig();
        if (staticIpConfig != null) {
            if (staticIpConfig.ipAddress != null) {
                mIpStr = staticIpConfig.ipAddress.getAddress().getHostAddress();
                mSubnetMaskStr = NetworkUtil.prefixToNetmask(staticIpConfig.ipAddress.getNetworkPrefixLength());
            }
            if (staticIpConfig.gateway != null) {
                mGatewayStr = staticIpConfig.gateway.getHostAddress();
            }
            if (staticIpConfig.dnsServers != null && staticIpConfig.dnsServers.size() > 0) {
                mDnsStr = staticIpConfig.dnsServers.get(0).getHostAddress();
            }
        }

        if (TextUtils.isEmpty(mIpStr)) {
            mIpStr = mContext.getResources().getString(R.string.wifi_ip_address_hint);
        }

        if (TextUtils.isEmpty(mGatewayStr)) {
            mGatewayStr = mContext.getResources().getString(R.string.wifi_gateway_hint);
        }

        if (TextUtils.isEmpty(mDnsStr)) {
            mDnsStr = mContext.getResources().getString(R.string.wifi_dns_hint);
        }
        if (TextUtils.isEmpty(mSubnetMaskStr)) {
            mDnsStr = mContext.getResources().getString(R.string.wifi_mask_hint);
        }

        Log.i(TAG, "ip : " + mIpStr + " netmask : " + mSubnetMaskStr + " gateway : " + mGatewayStr + " dns : "
                + mDnsStr);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wifi_link_property_config_layout);

        Window window = getWindow();
        WindowManager.LayoutParams p = window.getAttributes();
        p.height = LayoutParams.MATCH_PARENT;
        p.width = LayoutParams.MATCH_PARENT;
        window.setAttributes(p);

        findViews();
    }

    private void findViews() {
        setItem(R.id.auto_get, R.string.wifi_link_property_ip_get, isAutoGetIp ? R.string.wifi_link_property_auto
                : R.string.wifi_link_property_manual);
        setItem(R.id.ip_address, R.string.wifi_ip_address, R.string.wifi_ip_address_hint);
        setItem(R.id.subnet_mask, R.string.wifi_mask, R.string.wifi_mask_hint);
        setItem(R.id.gateway, R.string.wifi_gateway, R.string.wifi_gateway_hint);
        setItem(R.id.dns, R.string.wifi_dns, R.string.wifi_dns_hint);
        setSubmitButton(R.string.wifi_display_options_forget, R.string.wifi_button_connect);
        setItemsBackground(!isAutoGetIp);
        setTitleWidget();
    }

    private void setTitleWidget() {
        TitleWidget tw = (TitleWidget) findViewById(R.id.title_widget);
        if (tw != null) {
            tw.setSubTitleText(mContext.getString(R.string.network_settings),
                    mContext.getString(R.string.wifi_settings));
        }
    }

    private boolean isConfigured() {
        WifiManager wifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);
        for (WifiConfiguration config : wifiManager.getConfiguredNetworks()) {
            if (config.networkId == mAccessPoint.networkId) {
                return true;
            }
        }
        return false;
    }

    /**
     * set the display and listener of sumbmitbutton.
     * 
     * @param forgetTextId
     * @param connectTextId
     */
    public void setSubmitButton(int forgetTextId, int connectTextId) {
        mSubmitButton = (Button) findViewById(R.id.link_property_config_yes);
        mSubmitButton.setVisibility(View.INVISIBLE);
        if (mFromWhere == FROM_WIFI) {
            if (isConfigured()) {
                mSubmitButton.setVisibility(View.VISIBLE);
                mSubmitButton.setText(mContext.getResources().getString(forgetTextId));
            } else {
                mSubmitButton.setVisibility(View.VISIBLE);
                mSubmitButton.setText(mContext.getResources().getString(connectTextId));
            }
        } else if (mFromWhere == FROM_WIRE) {
            mSubmitButton.setText(mContext.getResources().getString(connectTextId));
            mSubmitButton.setVisibility(View.VISIBLE);
        }

        mSubmitButton.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                if (mOnSubmitClickListener != null) {
                    if (isConfigured()) {
                        mOperationCode = OPERATION_CODE_FORGET;
                        mOnSubmitClickListener.onSubmit(v, OPERATION_CODE_FORGET, mAccessPoint.getConfig());
                    } else {
                        mOperationCode = OPERATION_CODE_CONNECT;
                        mOnSubmitClickListener.onSubmit(v, OPERATION_CODE_CONNECT, mAccessPoint.getConfig());
                    }
                }
                dismiss();
            }
        });

        mSubmitButton.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View arg0, boolean hasFocus) {
                if (hasFocus) {
                    mSubmitButton.setTextColor(Color.rgb(255, 255, 255));
                } else {
                    mSubmitButton.setTextColor(Color.rgb(0, 0, 0));
                }
            }
        });
    }

    public void setOnSubmitClickListener(OnSubmitClickListener l) {
        mOnSubmitClickListener = l;
    }

    public interface OnSubmitClickListener {
        void onSubmit(View v, int oprerationCode, WifiConfig config);
    }

    public int getNetworkId() {
        int id = WifiConfiguration.INVALID_NETWORK_ID;
        if (mAccessPoint != null) {
            id = mAccessPoint.networkId;
        }
        return id;
    }

    public void setItem(int id, int titleId, int contentId) {
        setItem(id, mContext.getResources().getString(titleId), mContext.getResources().getString(contentId));
    }

    public void setItem(final int id, String title, String defaultContent) {
        View item = findViewById(id);
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
                if (!TextUtils.isEmpty(mIpStr)) {
                    defaultContent = mIpStr;
                }
                break;
            case R.id.subnet_mask:
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

        selector.setOnKeyListener(new View.OnKeyListener() {

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
                            dialog.setTitle(R.string.wifi_settings, R.string.wifi_ip_address);
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
                            dialog.setTitle(R.string.wifi_settings, R.string.wifi_mask);
                            dialog.setPropertyChangeListener(new PropertyChangeListener() {
                                @Override
                                public void onPropertyChange(String value) {
                                    if (NetworkSettingUtils.isValidMaskString(value)) {
                                        mSubnetMaskStr = value;
                                        setItem(R.id.subnet_mask,
                                                mContext.getResources().getString(R.string.wifi_mask), mGatewayStr);
                                    } else {
                                        Utils.showToast(mContext, R.string.wifi_invliad_subnet_mask);
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
                            dialog.setTitle(R.string.wifi_settings, R.string.wifi_gateway);
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
                            dialog.setTitle(R.string.wifi_settings, R.string.wifi_dns);
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
            mAccessPoint.getConfig().setIpAssignment(IpAssignment.STATIC);

            setItem(R.id.auto_get, R.string.wifi_link_property_ip_get, R.string.wifi_link_property_manual);
            setItemsBackground(true);
        } else {
            mAccessPoint.getConfig().setIpAssignment(IpAssignment.DHCP);

            setItem(R.id.auto_get, R.string.wifi_link_property_ip_get, R.string.wifi_link_property_auto);
            setItemsBackground(false);
        }
        isAutoGetIp = !isAutoGetIp;
    }

    private void setItemsBackground(boolean enabled) {
        setItemsBackground(R.id.ip_address, enabled);
        setItemsBackground(R.id.gateway, enabled);
        setItemsBackground(R.id.subnet_mask, enabled);
        setItemsBackground(R.id.dns, enabled);
    }

    private void setItemsBackground(int id, boolean enabled) {

        View item = findViewById(id);
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

    @Override
    public void dismiss() {
        super.dismiss();

        if (mAccessPoint == null || mOperationCode == OPERATION_CODE_FORGET) {
            return;
        }

        validateIpConfigFields(mAccessPoint.getConfig());

        if (mOnConfigListener != null) {
            mOnConfigListener.onConfigFinish(mAccessPoint.getConfig());
        }
    }

    public void setOnConfigListener(OnConfigListener l) {
        this.mOnConfigListener = l;
    }

    public interface OnConfigListener {
        public void onConfigFinish(WifiConfig config);
    }

    private int validateIpConfigFields(WifiConfig config) {
        if (config == null) {
            return 0;
        }
        IpConfig ipConfig = config.getIpConfiguration();
        StaticIpConfig staticIpConfig = new StaticIpConfig();
        ipConfig.setStaticIpConfig(staticIpConfig);

        String ipAddr = mIpStr;
        if (TextUtils.isEmpty(ipAddr))
            return R.string.wifi_ip_settings_invalid_ip_address;

        InetAddress inetAddr = null;
        try {
            inetAddr = NetworkUtils.numericToInetAddress(ipAddr);
        } catch (IllegalArgumentException e) {
            return R.string.wifi_ip_settings_invalid_ip_address;
        }

        String maskString = mSubnetMaskStr;

        int networkPrefixLength = getNetworkPrefixByString(maskString, 24);

        try {
            if (networkPrefixLength < 0 || networkPrefixLength > 32) {
                return R.string.wifi_ip_settings_invalid_network_prefix_length;
            }
            staticIpConfig.ipAddress = new LinkAddress(inetAddr, networkPrefixLength);
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        String gateway = mGatewayStr;
        if (TextUtils.isEmpty(gateway)) {
            try {
                // Extract a default gateway from IP address
                InetAddress netPart = NetworkUtils.getNetworkPart(inetAddr, networkPrefixLength);
                byte[] addr = netPart.getAddress();
                addr[addr.length - 1] = 1;
                mGatewayTextView.setText(InetAddress.getByAddress(addr).getHostAddress());
            } catch (RuntimeException ee) {
            } catch (java.net.UnknownHostException u) {
            }
        } else {
            InetAddress gatewayAddr = null;
            try {
                gatewayAddr = NetworkUtils.numericToInetAddress(gateway);
            } catch (IllegalArgumentException e) {
                return R.string.wifi_ip_settings_invalid_gateway;
            }
            staticIpConfig.gateway = gatewayAddr;
        }

        String dns = mDnsStr;
        InetAddress dnsAddr = null;

        if (TextUtils.isEmpty(dns)) {
            // If everything else is valid, provide hint as a default option
            mDnsTextView.setText(mContext.getString(R.string.wifi_dns1_hint));
        } else {
            try {
                dnsAddr = NetworkUtils.numericToInetAddress(dns);
            } catch (IllegalArgumentException e) {
                return R.string.wifi_ip_settings_invalid_dns;
            }
            staticIpConfig.dnsServers.add(dnsAddr);
        }

        return 0;
    }

    private int getNetworkPrefixByString(String maskString, int defaultValue) {

        if (TextUtils.isEmpty(maskString) || !NetworkSettingUtils.isValidMaskString(maskString)) {
            return defaultValue;
        }
        String[] masks = maskString.split("\\.");

        if (masks.length != 4) {
            return defaultValue;
        }

        int i = 0;
        for (i = 0; i < 4; i++) {
            if (!"255".equals(masks[i])) {
                break;
            }
        }
        int totalLength = i * 8;
        int remainLength = 0;
        try {
            if (i < 4) {
                int remainder = Integer.parseInt(masks[i]);
                for (int j = 0; j < 8; j++) {
                    if ((remainder & (1 << j)) != 0) {
                        ++remainLength;
                    }
                }
                Log.i(TAG, "remainLength::" + remainLength);
            }
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }

        return totalLength + remainLength;
    }

    public int getFromWhere() {
        return mFromWhere;
    }

    public void setFromWhere(int mFromWhere) {
        this.mFromWhere = mFromWhere;
    }

}
