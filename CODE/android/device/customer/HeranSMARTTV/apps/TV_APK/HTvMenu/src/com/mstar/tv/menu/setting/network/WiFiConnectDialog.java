
package com.mstar.tv.menu.setting.network;

import java.net.InetAddress;
import java.util.Iterator;
import java.util.List;

import android.R.integer;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.graphics.Point;
import android.net.ConnectivityManager;
import android.net.IpConfiguration.IpAssignment;
import android.net.LinkAddress;
import android.net.LinkProperties;
import android.net.NetworkUtils;
import android.net.RouteInfo;
import android.net.StaticIpConfiguration;
import android.net.wifi.ScanResult;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiConfiguration.AuthAlgorithm;
import android.net.wifi.WifiConfiguration.KeyMgmt;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.net.wifi.WifiManager.ActionListener;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.Display;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.CompoundButton.OnCheckedChangeListener;
import android.widget.Toast;

import com.mstar.tv.menu.R;

//import android.net.wifi.WifiConfiguration.IpAssignment;

public class WiFiConnectDialog extends Dialog {

    private static final String TAG = "MSettings.WiFiConnectDialog";

    public static final int SECURE_OPEN = 0;

    public static final int SECURE_WEP = 1;

    public static final int SECURE_PSK = 2;

    public static final int SECURE_EAP = 3;

    private Activity mNetworkSettingsActivity;

    private WiFiConnectDialogHolder mWifiConnectDialogHolder;

    private WifiSettings mWifiSettings;

    private WifiManager mWifiManager;

    private ScanResult mScanResult;
    
    // auto ip config check box
    private CheckBox mAutoIpCheckBox;

    // save and cancel button
    private Button mSaveButton;

    private Button mCancelButton;

    private Button mForgetButton;

    private boolean mHasConfiged;

    // current secure
    private int mSecureType = SECURE_OPEN;

    private final ConnectivityManager mConnectivityManager;
    
    //laird add 
    private List<WifiConfiguration> wifiConfigList;
    private List<ScanResult>  scanResultList;
    private int wifiId;

    /**
     * @param activity {@link NetworkSettingsActivity}.
     * @param scanResult {@link ScanResult}. null to connect hide ssid,
     *            otherwise connect or edit select.
     */
    public WiFiConnectDialog(Activity activity, WifiSettings wifiSettings, ScanResult scanResult) {
        super(activity);
        this.mNetworkSettingsActivity = activity;
        this.mWifiSettings = wifiSettings;
        this.mScanResult = scanResult;
        mWifiManager = (WifiManager) activity.getSystemService(Context.WIFI_SERVICE);
        mConnectivityManager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.wifi_connect);

        this.mWifiConnectDialogHolder = new WiFiConnectDialogHolder(this);
        this.mAutoIpCheckBox = mWifiConnectDialogHolder.getAutoIpCheckBox();
        this.mSaveButton = mWifiConnectDialogHolder.getSaveButton();
        this.mCancelButton = mWifiConnectDialogHolder.getCancelButton();
        this.mForgetButton = mWifiConnectDialogHolder.getForgetButton();
        // init all controller
        registerListener();
        initUi();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
            if (mWifiConnectDialogHolder.isSecureTypeFocused()) {
                mSecureType--;
                mSecureType = (mSecureType + 3) % 3;
                mWifiConnectDialogHolder.setSecure(mSecureType);
                setWindowSize();

                return true;
            }
            // make sure move focus to save button
            if (mCancelButton.isFocused() || mSaveButton.isFocused()) {
                return false;
            } else {
                return true;
            }

        } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
            if (mWifiConnectDialogHolder.isSecureTypeFocused()) {
                mSecureType++;
                mSecureType %= 3;
                mWifiConnectDialogHolder.setSecure(mSecureType);
                setWindowSize();

                return true;
            }
            // make sure move focus to cancel button
            if (mSaveButton.isFocused() || mForgetButton.isFocused()) {
                return false;
            } else {
                return true;
            }
        }

        return super.onKeyDown(keyCode, event);
    }

    private void initUi() {
        // add hide ssid
        if (mScanResult == null) {
            mHasConfiged = false;

            mWifiConnectDialogHolder.setSsidLayoutVisible(true);
            mWifiConnectDialogHolder.setSecureTypeFocusable(true);
            mWifiConnectDialogHolder.refreshConnectTitle(R.string.add_wifi_net);
            mAutoIpCheckBox.setChecked(true);
            mSecureType = SECURE_OPEN;

            // edit or connect select ssid
        } else {
            // wifi secirity type.
            mSecureType = getSecurity(mScanResult);
            String selectSsid = mScanResult.SSID;
            Tools.logd(TAG, "select ssid, " + selectSsid);
            //laird add ,judge ssid isconfiged
            scanResultList = getScanResults();
            getConfiguration();
            int netid = IsConfiguration("\""+selectSsid+"\"");
            if(netid!=-1){
            	//configured
            	Log.d(TAG, "Laird modify----"+"this wifi is configured,wifiID is "+netid);
            	mHasConfiged = true;
            	
            	refreshConfigUi();
                mSaveButton.setText(R.string.connect_to);
                mForgetButton.setVisibility(View.VISIBLE);
                mWifiConnectDialogHolder.setPasswdHintFocusable(false);
            	 wifiId = netid;
            	//ConnectWifi(netid);            	
            }else {
            	Log.d(TAG, "Laird modify----"+"this wifi is not configured");  
                mHasConfiged = false;
                // connect
                mAutoIpCheckBox.setChecked(true);
                mSaveButton.setText(R.string.save);
                mForgetButton.setVisibility(View.GONE);
                mWifiConnectDialogHolder.setPasswdHintFocusable(true);
			}
            

            mWifiConnectDialogHolder.refreshConnectTitle(selectSsid);
            mWifiConnectDialogHolder.setSsidLayoutVisible(false);
            //Laird modify 
            /*
            WifiInfo wifiInfo = mWifiManager.getConnectionInfo();
            String ssid = wifiInfo.getSSID().replace("\"", "");
            // show dialog to connect/edit the selected SSID.
            if (selectSsid.equals(ssid)) {
                mHasConfiged = true;
               // refresh ip fields and auto checkbox
              refreshConfigUi();
            } else {
               mHasConfiged = false;
                // connect
                mAutoIpCheckBox.setChecked(true);
                mForgetButton.setVisibility(View.GONE);
            }
            */
        }
        mWifiConnectDialogHolder.setSecure(mSecureType);

        // calc height of window
        setWindowSize();
    }
    
  //得到Scan结果  
    public List<ScanResult> getScanResults(){  
        return mWifiManager.getScanResults();//得到扫描结果  
    } 
    
  //得到Wifi配置好的信息  
    public void getConfiguration(){      	
       wifiConfigList = mWifiManager.getConfiguredNetworks();//得到配置好的网络信息  
        for(int i =0;i<wifiConfigList.size();i++){  
            Log.i(TAG,"Laird modify----"+"wifiSSID"+wifiConfigList.get(i).SSID);  
            Log.i(TAG,"Laird modify----"+"wifiId"+String.valueOf(wifiConfigList.get(i).networkId));  
        }  
    } 
   //判定指定WIFI是否已经配置好,依据WIFI的地址BSSID,返回NetId  
    public int IsConfiguration(String SSID){  
        Log.i(TAG,"Laird modify----"+"Configed wifi num:  "+String.valueOf(wifiConfigList.size()));  
        for(int i = 0; i < wifiConfigList.size(); i++){  
            Log.i(wifiConfigList.get(i).SSID,String.valueOf( wifiConfigList.get(i).networkId));  
            if(wifiConfigList.get(i).SSID.equals(SSID)){//地址相同  
                return wifiConfigList.get(i).networkId;  
            }  
        }  
        return -1;  
    }  
  //添加指定WIFI的配置信息,原列表不存在此SSID  
    public int AddWifiConfig(List<ScanResult> wifiList,String ssid,String pwd){  
        int wifiId = -1;  
        for(int i = 0;i < wifiList.size(); i++){  
            ScanResult wifi = wifiList.get(i);  
            if(wifi.SSID.equals(ssid)){  
                Log.i(TAG,"Laird modify----"+"AddWifiConfig equals");  
                WifiConfiguration wifiCong = new WifiConfiguration();  
                wifiCong.SSID = "\""+wifi.SSID+"\"";//\"转义字符，代表"  
                wifiCong.preSharedKey = "\""+pwd+"\"";//WPA-PSK密码  
                wifiCong.hiddenSSID = false;  
                wifiCong.status = WifiConfiguration.Status.ENABLED;  
                wifiId = mWifiManager.addNetwork(wifiCong);//将配置好的特定WIFI密码信息添加,添加完成后默认是不激活状态，成功返回ID，否则为-1  
                if(wifiId != -1){  
                    return wifiId;  
                }  
            }  
        }  
        return wifiId;  
    } 
    //连接指定Id的WIFI  
    public boolean ConnectWifi(int wifiId) {
        for (int i = 0; i < wifiConfigList.size(); i++) {
            WifiConfiguration wifi = wifiConfigList.get(i);
            if (wifi.networkId == wifiId) {
                int state = wifiConfigList.get(i).status; // status:0--已经连接，1--不可连接，2--可以连接
                Log.d(TAG, "---connect status" + String.valueOf(state));
                if (state == 0) {
                    showToast(R.string.wifi_display_status_connected);
                    return true;
                } else if (state == 1) {
                    showToast(R.string.wifi_enable);
                    return false;
                } else if (state == 2) {
                    mWifiManager.enableNetwork(wifiId, true);// 激活该Id，建立连接
                }
                // while(!(mWifiManager.enableNetwork(wifiId,
                // true))){//激活该Id，建立连接
                // //status:0--已经连接，1--不可连接，2--可以连接
                // Log.d(TAG,"---connect status"+String.valueOf(wifiConfigList.get(wifiId).status));
                // }
                return true;
            }
        }
        return false;
    }

    @SuppressLint("NewApi")
    private void setWindowSize() {
        Window w = getWindow();
        w.setBackgroundDrawableResource(R.drawable.dialog_bg);
        w.setTitle(null);

        Point point = new Point();
        Display display = w.getWindowManager().getDefaultDisplay();
        display.getSize(point);
        int width = (int) (point.x * 0.5);
        int height = (int) (point.y * 0.6);

        // calc height of dialog according to the security type
        switch (mSecureType) {
            case SECURE_OPEN:
                mWifiConnectDialogHolder.setPasswdLayoutVisible(false);
                if (mScanResult == null) {
                    if (mAutoIpCheckBox.isChecked()) {
                        height = (int) (point.y * 0.4);
                    } else {
                        height = (int) (point.y * 0.7);
                    }
                } else {
                    if (mAutoIpCheckBox.isChecked()) {
                        height = (int) (point.y * 0.36);
                    } else {
                        height = (int) (point.y * 0.7);
                    }
                }
                break;
            case SECURE_WEP:
            case SECURE_PSK:
            case SECURE_EAP:
                mWifiConnectDialogHolder.setPasswdLayoutVisible(true);
                if(mHasConfiged){
                    mWifiConnectDialogHolder.setShowPasswdLayoutVisible(false);
                }else {
                    mWifiConnectDialogHolder.setShowPasswdLayoutVisible(true);
                }
                if (mAutoIpCheckBox.isChecked()) {
                    height = (int) (point.y * 0.5);
                } else {
                    height = (int) (point.y * 0.8);
                }
                break;
            default:
                break;
        }

        w.setLayout(width, height);
        w.setGravity(Gravity.CENTER);
        WindowManager.LayoutParams wl = w.getAttributes();
        w.setAttributes(wl);
    }

    /**
     * get the security level.
     */
    private int getSecurity(ScanResult result) {
        Log.d(TAG, "ScanResult.capabilities, " + result.capabilities);
        if (result.capabilities.contains("WEP")) {
            return SECURE_WEP;
        } else if (result.capabilities.contains("PSK")) {
            return SECURE_PSK;
        }

        return SECURE_OPEN;
    }

    private WifiConfiguration getNewConfig() {
        WifiConfiguration config = new WifiConfiguration();
        if (mScanResult == null) {
            config.SSID = convertToQuotedString(mWifiConnectDialogHolder.getSsid());
            // If the user adds a network manually, assume that it is hidden.
            config.hiddenSSID = true;
        } else {
            config.SSID = convertToQuotedString(mScanResult.SSID);
        }

        String passwd = mWifiConnectDialogHolder.getPasswd();
        switch (mSecureType) {
            case SECURE_OPEN:
                config.allowedKeyManagement.set(KeyMgmt.NONE);
                break;
            case SECURE_WEP:
                config.allowedKeyManagement.set(KeyMgmt.NONE);
                config.allowedAuthAlgorithms.set(AuthAlgorithm.OPEN);
                config.allowedAuthAlgorithms.set(AuthAlgorithm.SHARED);
                if (passwd.length() != 0) {
                    int length = passwd.length();
                    // WEP-40, WEP-104, and 256-bit WEP (WEP-232?)
                    if ((length == 10 || length == 26 || length == 58) && passwd.matches("[0-9A-Fa-f]*")) {
                        config.wepKeys[0] = passwd;
                    } else {
                        config.wepKeys[0] = '"' + passwd + '"';
                    }
                }
                break;
            case SECURE_PSK:
                config.allowedKeyManagement.set(KeyMgmt.WPA_PSK);
                if (passwd.length() != 0) {
                    if (passwd.matches("[0-9A-Fa-f]{64}")) {
                        config.preSharedKey = passwd;
                    } else {
                        config.preSharedKey = '"' + passwd + '"';
                    }
                }
                break;
            default:
                return null;
        }

        return config;
    }

    private void refreshConfigUi() {
        WifiConfiguration config = mWifiSettings.getWifiConfiguredNetwork();
        if (config == null) {
            mAutoIpCheckBox.setChecked(true);
            mWifiConnectDialogHolder.refreshPasswd("");
            mWifiConnectDialogHolder.refreshIp("");
            mWifiConnectDialogHolder.refreshNetmask("");
            mWifiConnectDialogHolder.refreshGateway("");
            mWifiConnectDialogHolder.refreshDns1("");
            mWifiConnectDialogHolder.refreshDns2("");

        } else {
            mForgetButton.setVisibility(View.VISIBLE);
            // refresh password        
            mWifiConnectDialogHolder.refreshPasswdHint();

            // is auto config
            if (IpAssignment.DHCP == config.getIpAssignment()) {
                mAutoIpCheckBox.setChecked(true);
            } else if (IpAssignment.STATIC == config.getIpAssignment()) {
                mAutoIpCheckBox.setChecked(false);
            }
            

            // link properties contain ip/gateway/dns
            LinkProperties linkProperties = mConnectivityManager.getLinkProperties(ConnectivityManager.TYPE_WIFI);
            Iterator<LinkAddress> iterator = linkProperties.getLinkAddresses().iterator();
            // ip info
            String ip = null;
            if (iterator.hasNext()) {
                LinkAddress linkAddress = iterator.next();
                ip = linkAddress.getAddress().getHostAddress();
                mWifiConnectDialogHolder.refreshIp(ip);
            }

            // gateway info
            String gateway = null;
            for (RouteInfo route : linkProperties.getRoutes()) {
                if (route.isDefaultRoute()) {
                    gateway = route.getGateway().getHostAddress();
                    mWifiConnectDialogHolder.refreshGateway(gateway);
                    break;
                }
            }
            // dns1
            Iterator<InetAddress> dnsIterator = linkProperties.getDnsServers().iterator();
            if (dnsIterator.hasNext()) {
                String dns = dnsIterator.next().getHostAddress();
                if (Tools.matchIP(dns)) {
                    mWifiConnectDialogHolder.refreshDns1(dns);
                }
            }
            // dns2
            if (dnsIterator.hasNext()) {
                String dns = dnsIterator.next().getHostAddress();
                if (Tools.matchIP(dns)) {
                    mWifiConnectDialogHolder.refreshDns2(dns);
                }
            }

            // spell netmask
            mWifiConnectDialogHolder.refreshNetmask(Tools.buildUpNetmask(ip, gateway));
        }
    }

    private boolean setSecurity(WifiConfiguration config, WifiConfiguration currentConfig) {
        if (currentConfig.allowedKeyManagement.get(KeyMgmt.WPA_PSK)) {
            config.allowedKeyManagement.set(KeyMgmt.WPA_PSK);
        } else if (currentConfig.allowedKeyManagement.get(KeyMgmt.WPA_EAP)
                || currentConfig.allowedKeyManagement.get(KeyMgmt.IEEE8021X)) {
            config.allowedKeyManagement.set(KeyMgmt.WPA_EAP);
            config.allowedKeyManagement.set(KeyMgmt.IEEE8021X);
            /*
             * config.eap.setValue(getSecurity(config));
             * config.phase2.setValue(""); config.ca_cert.setValue("");
             * config.client_cert.setValue(""); config.key_id.setValue("");
             * config.identity.setValue("");
             * config.anonymous_identity.setValue("");
             */
        }

        if (currentConfig.wepKeys[0] == null) {
            config.allowedKeyManagement.set(KeyMgmt.NONE);
        } else {
            config.allowedKeyManagement.set(KeyMgmt.NONE);
            config.allowedAuthAlgorithms.set(AuthAlgorithm.OPEN);
            config.allowedAuthAlgorithms.set(AuthAlgorithm.SHARED);
        }

        return true;
    }

    private void setIpFields(WifiConfiguration config, StaticIpConfiguration mConfiguration) {
        // is auto ip
        if (mAutoIpCheckBox.isChecked()) {
            config.setIpAssignment(IpAssignment.DHCP);
        } else {
            config.setIpAssignment(IpAssignment.STATIC);
            // config linkProperties

            String ip = mWifiConnectDialogHolder.getIp();
            String gateway = mWifiConnectDialogHolder.getGateway();
            String dns1 = mWifiConnectDialogHolder.getDns1();
            String dns2 = mWifiConnectDialogHolder.getDns2();
            try {
                InetAddress inetIp = NetworkUtils.numericToInetAddress(ip);
                mConfiguration.ipAddress = new LinkAddress(inetIp, 24);

                InetAddress inetGateway = NetworkUtils.numericToInetAddress(gateway);
                mConfiguration.gateway = inetGateway;

                InetAddress inetDns1 = NetworkUtils.numericToInetAddress(dns1);
                mConfiguration.dnsServers.add(inetDns1);

                InetAddress inetDns2 = NetworkUtils.numericToInetAddress(dns2);
                mConfiguration.dnsServers.add(inetDns2);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
                Tools.logd(TAG, "ip fields invalid.");
            }
        }
    }

    private boolean checkIpFields() {
        String ip = mWifiConnectDialogHolder.getIp();
        String netMask = mWifiConnectDialogHolder.getNetmask();
        String gateWay = mWifiConnectDialogHolder.getGateway();
        String dns1 = mWifiConnectDialogHolder.getDns1();
        String dns2 = mWifiConnectDialogHolder.getDns2();

        boolean flag = mWifiConnectDialogHolder.getAutoIpCheckBox().isChecked();

        // auto ip is checked
        if (flag) {
            return true;
        }

        if (ip == null || netMask == null || gateWay == null) {
            showToast(R.string.check_ip_failure);
            return false;
        } else if (ip.trim().equals("") || netMask.trim().equals("") || gateWay.trim().equals("")) {
            showToast(R.string.check_ip_failure);
            return false;
        }

        return true;
    }

    private void editConfig() {
        WifiConfiguration currentConfig = mWifiSettings.getWifiConfiguredNetwork();
        if (currentConfig == null) {
            Tools.logd(TAG, "unbelievable");
            return;
        }

        WifiConfiguration config = new WifiConfiguration();
        boolean isSupportSecurity = setSecurity(config, currentConfig);
        if (isSupportSecurity) {
            config.networkId = mWifiManager.getConnectionInfo().getNetworkId();
            StaticIpConfiguration mConfiguration = new StaticIpConfiguration();
            setIpFields(config, mConfiguration);
            // init linkProperties
            config.setStaticIpConfiguration(mConfiguration);
            // save config
            mWifiManager.save(config, mSaveListener);
        } else {
            // hint
            showToast(R.string.not_support_security_hint);
        }
    }

    private void newConnect() {
        String ssid = mWifiConnectDialogHolder.getSsid();
        // connect hide ssid
        if (mScanResult == null) {
            // invalid ssid
            if (TextUtils.isEmpty(ssid)) {
                showToast(R.string.ssid_password_error);
                return;
            }
        }

        String passwd = mWifiConnectDialogHolder.getPasswd();
        // invalid passwd
        if (mSecureType != SECURE_OPEN && TextUtils.isEmpty(passwd)) {
            showToast(R.string.input_password);
            return;
        }
        //laird add
        int netId = AddWifiConfig(scanResultList,ssid, passwd);

        WifiConfiguration config = getNewConfig();
        if (config == null) {
            Tools.logd(TAG, "unkown secure type");
        } else {
            StaticIpConfiguration mStaticIpConfiguration = new StaticIpConfiguration();
            setIpFields(config, mStaticIpConfiguration);
            // init linkProperties
            config.setStaticIpConfiguration(mStaticIpConfiguration);
            // connect to ssid
            mWifiManager.connect(config, mConnectListener);
        }
    }

    private String getSecurity(WifiConfiguration config) {
        if (config.allowedKeyManagement.get(KeyMgmt.WPA_PSK)) {
            return "WPA_PSK";
        } else if (config.allowedKeyManagement.get(KeyMgmt.WPA_EAP)
                || config.allowedKeyManagement.get(KeyMgmt.IEEE8021X)) {
            return "WPA_EAP";
        }

        return (config.wepKeys[0] != null) ? "SECURITY_WEP" : "SECURITY_NONE";
    }

    private String convertToQuotedString(String string) {
        return "\"" + string + "\"";
    }

    private void showToast(int id) {
        if (id <= 0) {
            return;
        }

        Toast.makeText(mNetworkSettingsActivity, id, Toast.LENGTH_SHORT).show();
    }

    private void registerListener() {
        mAutoIpCheckBox.setOnCheckedChangeListener(new OnCheckedChangeListener() {

            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    setWindowSize();
                    mWifiConnectDialogHolder.setIpConfigLayoutVisible(false);
                } else {
                    setWindowSize();
                    mWifiConnectDialogHolder.setIpConfigLayoutVisible(true);
                }
            }
        });

        mSaveButton.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                // check ip/gateway
                if (checkIpFields()) {
                    // connect select ssid
                    if (mHasConfiged) {
                    	Log.d(TAG, "This wifi is configed");
                        editConfig();
                        //Laird modify
                        ConnectWifi(wifiId);
                        
                    } else {
                        newConnect();
                    }
                } else {
                }
                dismiss();
            }
        });

        mForgetButton.setOnClickListener(new Button.OnClickListener() {

            @Override
            public void onClick(View v) {
                WifiConfiguration config = mWifiSettings.getWifiConfiguredNetwork(mScanResult.SSID);
                Log.d(TAG,"get forget conig :"+config.SSID);
                if (config != null) {
                	  Log.d(TAG,"in forget conig :");
                    mWifiManager.forget(config.networkId, mForgetListener);
                }
                // dismiss dialog
                dismiss();
            }
        });
    }

    private WifiManager.ActionListener mSaveListener = new WifiManager.ActionListener() {

        public void onSuccess() {
            Log.d(TAG, "save success");
        }

        public void onFailure(int reason) {
            showToast(R.string.wifi_failed_save_message);
        }
    };

    private WifiManager.ActionListener mConnectListener = new WifiManager.ActionListener() {

        @Override
        public void onSuccess() {
            Log.d(TAG, "connect success");
        }

        @Override
        public void onFailure(int reason) {
            showToast(R.string.wifi_failed_connect_message);
        }
    };

    private WifiManager.ActionListener mForgetListener = new ActionListener() {

        @Override
        public void onSuccess() {
            Log.d(TAG, "forget success");
        }

        @Override
        public void onFailure(int reason) {
            showToast(R.string.wifi_failed_forget_message);
        }
    };

}
