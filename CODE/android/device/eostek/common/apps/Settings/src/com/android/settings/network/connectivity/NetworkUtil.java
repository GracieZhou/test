package com.android.settings.network.connectivity;

import java.util.HashMap;
import java.util.List;
import scifly.device.Device;
import scifly.um.EosUploadListener;
import scifly.um.EosUploadManager;
import scifly.util.Ping;
import android.app.Activity;
import android.content.ContentResolver;
import android.content.Context;
import android.content.SharedPreferences;
import android.net.ConnectivityManager;
import android.net.DhcpInfo;
import android.net.LinkProperties;
import android.net.NetworkInfo;
import android.net.wifi.WifiConfiguration;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.provider.Settings;
import android.text.format.Formatter;
import android.util.Log;
import android.net.IpConfiguration;
import android.net.IpConfiguration.IpAssignment;
public class NetworkUtil {
	private static final String TAG = "NetworkDiag";
	public static final String ETHERNET_USE_STATIC_IP = "ethernet_use_static_ip";
	private static Context mContext = null;
	private ConnectivityManager mConnManager;
	private static NetworkUtil _sInstanceInfoUtil;

	private NetworkUtil(Context context) {
		this.mContext = context;
		mConnManager = (ConnectivityManager) mContext
				.getSystemService(mContext.CONNECTIVITY_SERVICE);
	}

	public static NetworkUtil getInstance(Context context) {
		if (null == _sInstanceInfoUtil) {
			_sInstanceInfoUtil = new NetworkUtil(context);
		}
		return _sInstanceInfoUtil;
	}

	public boolean isNetworkConnected() {
		if (null == mConnManager.getActiveNetworkInfo()) {
			return false;
		}
		return mConnManager.getActiveNetworkInfo().isConnected();
	}

	private String getNetworkTypeName() {
		Log.i(TAG, ">>>>>network type name  = "
				+ mConnManager.getActiveNetworkInfo().getTypeName());
		return mConnManager.getActiveNetworkInfo().getTypeName();
	}

	public String getIpAddress() {
		Log.d(TAG, "get ip ");
		String ipAddress = null;
		if (getNetworkTypeName().equals("WIFI")) {
			WifiManager wifiManager = (WifiManager) mContext
					.getSystemService(mContext.WIFI_SERVICE);
			DhcpInfo dhcpInfo = wifiManager.getDhcpInfo();
			ipAddress = Formatter.formatIpAddress(dhcpInfo.ipAddress);
			Log.i(TAG, "WIFI_IP_Address: " + ipAddress);
			// } else if (getNetworkTypeName().equals("ETHERNET")) {
		} else if (getNetworkTypeName().equals("Ethernet")) {
			Log.d(TAG, "youxian ip ");
			 if(Build.DEVICE.equals("heran")
			 || Build.DEVICE.equals("scifly_m202_1G")
			 || Build.DEVICE.equalsIgnoreCase("Leader")
			 || Build.DEVICE.equalsIgnoreCase("soniq")){
			 Log.d(TAG, "qita  ip");
			 LinkProperties linkProperties = mConnManager
			 .getLinkProperties(ConnectivityManager.TYPE_ETHERNET);
			 if (null == linkProperties) {
			 return "0.0.0.0";
			 }
			 String ipStr = linkProperties.getAddresses().toString();
			 ipAddress = ipStr.substring(2, ipStr.length() - 2);
			 Log.i(TAG, "ETHERNET_IP_Address: " + ipAddress);
			
			 }else{
			Log.d(TAG, "828ip");
			SharedPreferences settings = mContext.getSharedPreferences("test",
					mContext.MODE_PRIVATE);
			ipAddress = settings.getString("ip", "0.0.0.0");
			Log.i(TAG, ">>>>>828ipAddress = " + ipAddress);
			// }
		}
		}
		Log.i(TAG, ">>>>>ipAddress = " + ipAddress);
		Log.i(TAG, ">>>>>ipAddress = " + ipAddress);
		return ipAddress;
	}

	public String getGateWay() {
		String gwAddress = null;
		if (getNetworkTypeName().equals("WIFI")) {
			WifiManager wm = (WifiManager) mContext
					.getSystemService(mContext.WIFI_SERVICE);
			DhcpInfo dhcpInfo = wm.getDhcpInfo();

			gwAddress = Formatter.formatIpAddress(dhcpInfo.gateway);
			Log.i(TAG, "WIFI_GATEWAY_Address: " + gwAddress);
			// } else if (getNetworkTypeName().equals("ETHERNET")) {
		} else if (getNetworkTypeName().equals("Ethernet")) {
			Log.d(TAG, "youxian gateway ");
			if (Build.DEVICE.equals("heran")
					|| Build.DEVICE.equals("scifly_m202_1G")
					|| Build.DEVICE.equalsIgnoreCase("Leader")
					|| Build.DEVICE.equalsIgnoreCase("soniq")) {
				Log.d(TAG, "qita  wangguan");
				LinkProperties linkProperties = mConnManager
						.getLinkProperties(ConnectivityManager.TYPE_ETHERNET);
				if (null == linkProperties) {
					return "";
				}
				String gatewayStr = linkProperties.getRoutes().toString();
				gwAddress = gatewayStr.substring(gatewayStr.length() - 14,
						gatewayStr.length() - 1);
				if (gwAddress.contains(">")) {
					gwAddress = gwAddress.substring(1, gwAddress.length());
				}
			} else {
				Log.d(TAG, "828wangguan");
				SharedPreferences settings = mContext.getSharedPreferences(
						"test", mContext.MODE_PRIVATE);
				gwAddress = settings.getString("gateway", "0.0.0.0");
			}

			Log.i(TAG, "ETHERNET_GATEWAY_Address :  " + gwAddress);

		}
		return gwAddress;
	}

	public String getIpAssignment() {
		Log.d(TAG, "<<<<<<<<<<<getNetworkTypeName() <<<<<<<<<"+getNetworkTypeName());
		if (getNetworkTypeName().equals("WIFI")) {
			Log.d(TAG, "<<<<<<<<<<<当前网络为wifi<<<<<<<<<");
			WifiManager wm = (WifiManager) mContext
					.getSystemService(mContext.WIFI_SERVICE);
			if (wm.getWifiState() == WifiManager.WIFI_STATE_ENABLED) {
				WifiInfo wifiInfo = wm.getConnectionInfo();
				String ssid = wifiInfo.getSSID();
				List<WifiConfiguration> wifiConfigurations = wm
						.getConfiguredNetworks();
				for (WifiConfiguration config : wifiConfigurations) {
					if (config.SSID.equals(ssid)) {
						String IP_Assignment = config.toString();
						Log.i(TAG, "WIFI_IP_Assignment: " + IP_Assignment);
						String[] sss =IP_Assignment.split("\n") ;
						Log.i(TAG, "<<<<<<String[] sss<<<<<<< " +sss.length+"<<<<<<");
						IP_Assignment=sss[20];
						IP_Assignment=IP_Assignment.substring(IP_Assignment.lastIndexOf(":")+1).toString();
						return IP_Assignment;
					}
				}
			}
			return "UNASSIGNED";
		} else {
			String Ip_Assignment = null;
			// ContentResolver cr = mContext.getContentResolver();
			// try {
			// if (Settings.System.getInt(cr, ETHERNET_USE_STATIC_IP) == 0) {
			// Log.d(TAG,
			// "checkUseStaticIp() : user set to use DHCP, about to Return.");
			//
			// Ip_Assignment = "DHCP";
			// }
			// Ip_Assignment = "STATIC";
			// } catch (Settings.SettingNotFoundException e) {
			// Ip_Assignment = "DHCP";
			// }
			// Log.i(TAG, "ETHERNET_IP_Assignment: " + Ip_Assignment);
			IpConfiguration mIpConfiguration = new IpConfiguration();
			mIpConfiguration.getIpAssignment();
			Log.d(TAG, "ipassignment"+mIpConfiguration.getIpAssignment());
			// return Ip_Assignment;
			if (mIpConfiguration.getIpAssignment().equals(IpAssignment.DHCP)) {
				return "DHCP";
			} else {
				return "STATIC";
			}
//			return mIpConfiguration.getIpAssignment();
		}
	}

	public boolean isReachable(String hostip) {
		Ping ping = new Ping(hostip);
		return ping.run();
	}

	public void uploadLog(String logPath, EosUploadListener listener) {
		HashMap<String, String> param = new HashMap<String, String>();
		param.put("ifid", Device.getIfid());
		param.put("devName", Device.getDeviceName(mContext));
		param.put("mac", Device.getHardwareAddress(mContext));
		param.put("ver", Device.getVersion());
		param.put("text", "NetworkDiagnosis");
		param.put("bb", Device.getBb());
		param.put("devCode", Device.getDeviceCode());
		param.put("ttag", Device.getTtag());

		String requestUrl = "http://acs.babao.com:8013/TMS/interface/clientService.jsp";
		// EosUploadTask task = new EosUploadTask(0, logPath, requestUrl, param,
		// listener);
		EosUploadManager uploadManager = new EosUploadManager();
		// uploadManager.addTask(task);
	}
}