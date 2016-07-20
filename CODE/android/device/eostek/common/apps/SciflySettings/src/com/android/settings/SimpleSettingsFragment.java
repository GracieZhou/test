
package com.android.settings;

import scifly.device.Device;
import com.android.settings.R;

import android.app.Activity;
import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.app.Fragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.database.Cursor;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.NetworkInfo.State;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemProperties;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.text.TextUtils;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.NetworkInterface;
import java.net.SocketException;
import java.util.Enumeration;

import org.apache.http.HttpResponse;
import org.apache.http.HttpStatus;
import org.apache.http.client.ClientProtocolException;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.params.HttpConnectionParams;
import org.apache.http.params.HttpParams;
import org.apache.http.util.EntityUtils;
import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.android.settings.deviceinfo.business.DeviceInfoLogic;
import com.android.settings.network.ethernet.SimpleEthernetSettingFragment;
import com.android.settings.network.wifi.SimpleWifiSettingsFragment;
import com.android.settings.update.SystemLocalUpdateActivity;

/**
 * NetworkFragment.
 * 
 * @date 2015-8-13
 */
public class SimpleSettingsFragment extends Fragment {

    private static final String TAG = "SettingsFragment";

    private static final String PLATFORM = SystemProperties.get("ro.scifly.platform", "");

    private RelativeLayout mWiredlayout;

    private RelativeLayout mWifilayout;

    private RelativeLayout mMacAddresslayout;

    private RelativeLayout mDeviceInfolayout;

    private RelativeLayout mLocalUpdatelayout;

    private SimpleSettingsActivity mActivity;

    private MyOnClickListener onClickListener;
    
    /**
     * network is available.
     */
    
    private boolean isWiredConnected = false;
    
    private boolean isWifiConnected = false;
    
    /**
     * share prefrence.
     */
    private final String DATA = "data";
    
    private final String VIP = "vip";
    
    private final String UPGRADE = "upgrade";
    
    /**
     * image download path.
     */
    
    private String vipPath;
    
    private String upgradePath;
    
    /**
     * image download id.
     */
    
    private long vipDownLoadID;
    
    private long upgradeDownLoadID;
    
    private final int UNKNOWN_ID = -1;
    
    /**
     * json URL.
     */
    
    private final String URL = "http://www.jowinwin.com/hertv2msd/ad_simple.php?position=simpleset&cust_type=19";
    
    private final Uri BASE_URL = Uri.parse("content://downloads/my_downloads");
    
    class MyOnClickListener implements View.OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.wired:
                    mActivity.replaceFragment(new SimpleEthernetSettingFragment());
                    break;
                case R.id.wifi:
                    mActivity.replaceFragment(new SimpleWifiSettingsFragment());
                    break;
                case R.id.localupdate:
                    Intent intent = new Intent(mActivity, SystemLocalUpdateActivity.class);
                    mActivity.overridePendingTransition(R.anim.push_left_in, R.anim.push_left_out);
                    mActivity.startActivity(intent);
                    break;
                case R.id.mac_address:
                    break;
                case R.id.deviceId:
                    break;
                default:
                    break;
            }
        }

    }
    
    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (isWiredConnected || isWifiConnected) {
                mActivity.mUpgrade.setImageResource(R.drawable.upgrade_no_network);
                updateImage();
            } else {
                mActivity.mUpgradeParentLayout.setFocusable(false);
                mActivity.mUpgrade.setImageResource(R.drawable.upgrade_no_network);
            }
            
        }
        
    };
    
    
    
    private String getStringFromXml(Context ctx, String key) {
        SharedPreferences sPreferences = ctx.getSharedPreferences(DATA, Context.MODE_PRIVATE);
        return sPreferences.getString(key, null);
    }
    
    private void writeStringToXml(Context ctx, String key, String value) {
        SharedPreferences sPreferences = ctx.getSharedPreferences(DATA, Context.MODE_PRIVATE);
        Editor editor = sPreferences.edit();
        editor.putString(key, value);
        editor.commit();
    }
    private long getLongFromXml(Context ctx, String key) {
        SharedPreferences sPreferences = ctx.getSharedPreferences(DATA, Context.MODE_PRIVATE);
        return sPreferences.getLong(key, UNKNOWN_ID);
    }
    
    private void writeLongToXml(Context ctx, String key, long value) {
        SharedPreferences sPreferences = ctx.getSharedPreferences(DATA, Context.MODE_PRIVATE);
        Editor editor = sPreferences.edit();
        editor.putLong(key, value);
        editor.commit();
    }

   
    
    BroadcastReceiver receiver = new BroadcastReceiver() { 
      @Override 
      public void onReceive(Context context, Intent intent) { 
        long reference = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1); 
        if (getLongFromXml(getActivity(), "vip_id") == reference) { 
          updateImage();
        } 
      } 
    }; 
    

    
    
    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        IntentFilter filter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);
        getActivity().registerReceiver(receiver, filter); 
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
        getActivity().unregisterReceiver(receiver);
    }
    
    private String getPathById(long id) {
        String str = null;
        Cursor cursor = getActivity().getContentResolver().query(BASE_URL, null, "_id=?", new String[]{String.valueOf(id)}, null);
        if (cursor.moveToFirst()) {
            str = cursor.getString(cursor.getColumnIndex("_data"));
        }
        cursor.close();
        return str;
    }
    
    private String connServerForResult(String strUrl) {

    
        HttpGet httpRequest = new HttpGet(strUrl);

        String strResult = "";

        try {

    
            HttpClient httpClient = new DefaultHttpClient();
            HttpParams params = httpClient.getParams();
            params.setParameter("tpg", "1");
            params.setIntParameter("tnm", 2);
      
            HttpConnectionParams.setConnectionTimeout(params, 5000);
            HttpConnectionParams.setSoTimeout(params, 5000);
        
            HttpResponse httpResponse = httpClient.execute(httpRequest);
            if (httpResponse.getStatusLine().getStatusCode() == HttpStatus.SC_OK) {

           
                Log.v("tag", "status:======200");
                strResult = EntityUtils.toString(httpResponse.getEntity());

            } else {
                Log.v("tag", "status:=====!=200");
                return strResult;
            }

        } catch (ClientProtocolException e) {

            System.out.println("protocol error");

            e.printStackTrace();

        } catch (IOException e) {

            System.out.println("IO error");

            e.printStackTrace();

        }
        return strResult;

    }
    
    private void getImagePatch() {
        String str = connServerForResult(URL);
        if (TextUtils.isEmpty(str)) {
            Log.v("tag", "path:==============>>>**********");
            Log.v("tag", "path2:=============>>>**********");
            return;
        }
        Log.v("tag", "content:\n" + str);
        try {
            JSONObject jsonObj = new JSONObject(str);
            if ("0".equals(jsonObj.getString("err"))) {
                JSONObject jsonObjBody = jsonObj.getJSONObject("bd");
                JSONArray array = jsonObjBody.getJSONArray("its");
                JSONObject obj = array.getJSONObject(0);
//                JSONObject obj2 = array.getJSONObject(1);
                vipPath = obj.getString("pic");
                String webViewURL = obj.getString("url");
                writeStringToXml(getActivity(), "webview_url", webViewURL);
//                upgradePath = obj2.getString("pic");
                
                long vip = getLongFromXml(getActivity(), "vip_id");
                if (isResponse(vipPath, vip, true)) {
                    vipDownLoadID = startDownLoadImage(vipPath, true);
                    writeLongToXml(getActivity(), "vip_id", vipDownLoadID);
                    writeStringToXml(getActivity(), "vip_path", vipPath);
                }
                
//                long upgrade = getLongFromXml(getActivity(), "upgrade_id");
//                if (isResponse(upgradePath, upgrade, false)) {
//                    upgradeDownLoadID = startDownLoadImage(upgradePath, false);
//                    writeLongToXml(getActivity(), "upgrade_id", upgradeDownLoadID);
//                    writeStringToXml(getActivity(), "upgrade_path", upgradePath);
//                }
                
            } else {
                Log.v("tag", "path:==============");
                Log.v("tag", "path2:=============");
                
            }
        } catch (JSONException e) {
            Log.v("tag", "path:==============>>>");
            Log.v("tag", "path2:=============>>>");
            e.printStackTrace();
        }
    }
    
    private boolean isResponse(String path, long id, boolean isVIP) {
        if (TextUtils.isEmpty(path)) {
            return false;
        }
        if (id == UNKNOWN_ID) {
            return true;
        }
        if (isVIP) {
            if (!path.equals(getStringFromXml(getActivity(), "vip_path"))) {
                return true;
            }
        } else {
            if (!path.equals(getStringFromXml(getActivity(), "upgrade_path"))) {
                return true;
            }
        }
        return false;
    }
    
    private void updateImage() {
        String path = getPathById(getLongFromXml(getActivity(), "vip_id"));
        if (TextUtils.isEmpty(path)) {
            mActivity.mUpgradeParentLayout.setFocusable(false);
            return;
        }
        if (!new File(path).exists()) {
            mActivity.mUpgradeParentLayout.setFocusable(false);
            return;
        }
        
        try {
            FileInputStream fis = new FileInputStream(path);
            mActivity.mUpgrade.setImageBitmap(BitmapFactory.decodeStream(fis));
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        mActivity.mUpgradeParentLayout.setFocusable(true);
    }
    
    private boolean isNetworkAvailable()
    {
         ConnectivityManager connectivityManager = (ConnectivityManager)getActivity().getSystemService(Context.CONNECTIVITY_SERVICE);
         NetworkInfo[] networkInfo = connectivityManager.getAllNetworkInfo();
         for(int i=0;i<networkInfo.length;i++){
             if (networkInfo[i].getState() == NetworkInfo.State.CONNECTED)
             {
                   return true;
             }
         }
         return false;
    }
    
    private long startDownLoadImage(String url, boolean isVIP) {
        DownloadManager dm = (DownloadManager) getActivity().getSystemService(Context.DOWNLOAD_SERVICE);
        Uri uri = Uri.parse(url);
        DownloadManager.Request request = new Request(uri);
        request.setMimeType("image/png");
        if (isVIP) {
            request.setDestinationInExternalPublicDir(getActivity().getCacheDir().getPath(), "vip.png");
        } else {
            request.setDestinationInExternalPublicDir(getActivity().getCacheDir().getPath(), "upgrade.png");
        }
        return dm.enqueue(request);
    }
    
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.setting_layout, container, false);

        mWiredlayout = (RelativeLayout) view.findViewById(R.id.wired);
        mWifilayout = (RelativeLayout) view.findViewById(R.id.wifi);
        mMacAddresslayout = (RelativeLayout) view.findViewById(R.id.mac_address);
        mDeviceInfolayout = (RelativeLayout) view.findViewById(R.id.deviceId);
        mLocalUpdatelayout = (RelativeLayout) view.findViewById(R.id.localupdate);

        TextView wiredTitle = (TextView) mWiredlayout.findViewById(R.id.left_title);
        TextView wifiTitle = (TextView) mWifilayout.findViewById(R.id.left_title);
        TextView macAddressTitle = (TextView) mMacAddresslayout.findViewById(R.id.left_title);
        TextView deviceInfoTitle = (TextView) mDeviceInfolayout.findViewById(R.id.left_title);
        TextView localUpdateTitle = (TextView) mLocalUpdatelayout.findViewById(R.id.left_title);
        
        wiredTitle.setText(R.string.ethernet_setting);
        wifiTitle.setText(R.string.wireless_setting);
        macAddressTitle.setText(R.string.wifi_advanced_mac_address_title);
        deviceInfoTitle.setText(R.string.about_build_number);
        localUpdateTitle.setText(R.string.localupdate);
        
        TextView macAddress = (TextView) mMacAddresslayout.findViewById(R.id.right_value);
        macAddress.setText( getLocalEthernetMacAddress());
        
        TextView deviceInfo = (TextView) mDeviceInfolayout.findViewById(R.id.right_value);
        deviceInfo.setText(DeviceInfoLogic.getBuildNumber());
        
        if ((Build.DEVICE.equals("heran") || Build.DEVICE.equals("scifly_m202_1G"))) {
            mWiredlayout.setVisibility(View.GONE);
        }
        if (PLATFORM.equals(Device.SCIFLY_PLATFORM_BOX)) {
            mWifilayout.setVisibility(View.GONE);
        }

        /**
         * register click listener.
         */
        onClickListener = new MyOnClickListener();
        mWiredlayout.setOnClickListener(onClickListener);
        mWifilayout.setOnClickListener(onClickListener);
        mMacAddresslayout.setOnClickListener(onClickListener);
        mDeviceInfolayout.setOnClickListener(onClickListener);
        mLocalUpdatelayout.setOnClickListener(onClickListener);
        return view;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mActivity = (SimpleSettingsActivity) getActivity();
        mActivity.setSubTitle();
        mActivity.mUpgradeParentLayout.setOnFocusChangeListener(new OnFocusChangeListener() {
            
            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                if (hasFocus) {
                    mActivity.mUpgradeParentLayout.setBackgroundResource(R.drawable.simple_setting_vip);
                } else {
                    mActivity.mUpgradeParentLayout.setBackgroundColor(Color.TRANSPARENT);
                }
            }
        });
        new Thread(new Runnable() {
            
            @Override
            public void run() {
                getImagePatch();
            }
        }).start();
    }

    /**
     * update networkState
     */
    public void updateNetworkState() {
        updateEthernetState(getNetworkInfo(ConnectivityManager.TYPE_ETHERNET));
        updateWifiState(getNetworkInfo(ConnectivityManager.TYPE_WIFI));
    }

    private void updateState() {
        updateEthernetState(getNetworkInfo(ConnectivityManager.TYPE_ETHERNET));
        updateWifiState(getNetworkInfo(ConnectivityManager.TYPE_WIFI));
        mActivity.mUpgradeParentLayout.setVisibility(View.VISIBLE);
        mActivity.mUpgradeParentLayout.setBackgroundColor(Color.TRANSPARENT);
        mHandler.sendEmptyMessage(0);
    }

    /**
     * get the type of network.
     * 
     * @param type
     * @return
     */
    private NetworkInfo getNetworkInfo(int type) {
        ConnectivityManager cm = (ConnectivityManager) mActivity.getSystemService(Activity.CONNECTIVITY_SERVICE);
        return cm.getNetworkInfo(type);
    }
    
    private BroadcastReceiver networkStatusChanged = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.d(TAG, "<<<<on receive<<<<");
            String action = intent.getAction();
            if (ConnectivityManager.CONNECTIVITY_ACTION.equals(action)) {
                Log.d(TAG, "<<<<network status change<<<<");
//                updateImage();
                updateState();
            }
        }
    };

    /**
     * update state of ethernet.
     * 
     * @param networkInfo
     */
    public void updateEthernetState(NetworkInfo networkInfo) {
        if (mWiredlayout.getVisibility() == View.GONE) {
            return;
        }
        if (networkInfo != null) {
            Log.d(TAG, "<<<< networkInfo.isConnected()<<<" + networkInfo.isConnected());
            Log.d(TAG, "<<<< networkInfo.isConnected()<<<" + networkInfo.isConnected() + networkInfo.getType()
                    + networkInfo.getTypeName());
            Log.d(TAG, "<<<ethernet<networkInfo.getState() == State.CONNECTED<<<<"
                    + (networkInfo.getState() == State.CONNECTED));
            TextView wired = (TextView) mWiredlayout.findViewById(R.id.right_value);
            if (networkInfo.getState() == State.CONNECTED) {
                wired.setText(mActivity.getResources().getString(R.string.ethernet_enabled));
                wired.setTextColor(mActivity.getResources().getColor(R.color.green));
                isWiredConnected = true;
            } else {
                wired.setText(mActivity.getResources().getString(R.string.ethernet_disabled));
                wired.setTextColor(mActivity.getResources().getColor(android.R.color.holo_red_light));
                isWiredConnected = false;
            }
        }
    }

    /**
     * update state of wifi.
     * 
     * @param networkInfo
     */
    public void updateWifiState(NetworkInfo networkInfo) {
        if (mWifilayout.getVisibility() == View.GONE) {
            return;
        }
        Log.d(TAG, "<<wifi<<networkInfo == nul<<<<" + (networkInfo == null));
        Log.d(TAG, "<<wifi<<!networkInfo.isAvailable() <<<<" + (!networkInfo.isAvailable()));
        Log.d(TAG, "<<wifi<<networkInfo.getState() == State.DISCONNECTED <<<<"
                + (networkInfo.getState() == State.DISCONNECTED));
        TextView wifi = (TextView) mWifilayout.findViewById(R.id.right_value);
        if (networkInfo == null || !networkInfo.isAvailable() || networkInfo.getState() == State.DISCONNECTED) {
            wifi.setText(mActivity.getResources().getString(R.string.ethernet_disabled));
            wifi.setTextColor(mActivity.getResources().getColor(android.R.color.holo_red_light));
            isWifiConnected = false;
        } else {
            String ssid = networkInfo.getExtraInfo();
            wifi.setText((ssid == null ? "" : ssid.replace("\"", "")));
            wifi.setTextColor(mActivity.getResources().getColor(R.color.green));
            isWifiConnected = true;
        }
    }
    
    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "<<<fragment<<on stop<<<<<");
        getActivity().unregisterReceiver(networkStatusChanged);
        mActivity.mUpgradeParentLayout.setVisibility(View.GONE);
    }
    
    @Override
    public void onResume() {
        Log.d(TAG, "<<<fragment<<on resume<<<<<");
        super.onResume();
        /**
         * register network change.
         */
        IntentFilter filter = new IntentFilter();
        filter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        filter.addAction(WifiManager.WIFI_STATE_CHANGED_ACTION);
        getActivity().registerReceiver(networkStatusChanged, filter);
        
        mWiredlayout.requestFocus();
        updateState();
    }
	
		 /**
     * @Title: getLocalEthernetMacAddress
     * @param: void
     * @return: String mac;
     * @throws
     */
    private String getLocalEthernetMacAddress() {
        String eth_mac = null;
        String wifi_mac = "";
        try {
            Enumeration<NetworkInterface> localEnumeration = NetworkInterface.getNetworkInterfaces();

            while (localEnumeration.hasMoreElements()) {
                NetworkInterface localNetworkInterface = localEnumeration.nextElement();
                String interfaceName = localNetworkInterface.getDisplayName();

                if (interfaceName == null) {
                    continue;
                }

                if (interfaceName.equals("eth0")) {
                    eth_mac = convertToMac(localNetworkInterface.getHardwareAddress());
                    if (eth_mac != null && eth_mac.startsWith("0:")) {
                        eth_mac = "0" + eth_mac;
                    }
                    if (TextUtils.isEmpty(eth_mac)) {
                        continue;
                    } else {
                        break;
                    }
                } else if (interfaceName.equals("wlan0")) {
                    wifi_mac = convertToMac(localNetworkInterface.getHardwareAddress());
                    if (wifi_mac != null && wifi_mac.startsWith("0:")) {
                        wifi_mac = "0" + wifi_mac;
                    }
                    if (TextUtils.isEmpty(wifi_mac)) {
                        continue;
                    } else {
                        if (eth_mac != null) {
                            break;
                        } else {
                            continue;
                        }
                    }
                }
            }
        } catch (SocketException e) {
            e.printStackTrace();
        }
        if (TextUtils.isEmpty(eth_mac)) {
            return wifi_mac;
        } else {
            return eth_mac;
        }
    }

    private String convertToMac(byte[] mac) {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < mac.length; i++) {
            byte b = mac[i];
            int value = 0;
            if (b >= 0 && b <= 16) {
                value = b;
                sb.append("0" + Integer.toHexString(value));
            } else if (b > 16) {
                value = b;
                sb.append(Integer.toHexString(value));
            } else {
                value = 256 + b;
                sb.append(Integer.toHexString(value));
            }
            if (i != mac.length - 1) {
                sb.append(":");
            }
        }
        return sb.toString();
    }
}
