
package scifly.device;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.FileWriter;
import java.lang.reflect.Method;

import com.eostek.tm.cpe.manager.CpeManager;

import scifly.provider.SciflyStore;
import scifly.provider.SciflyStore.Global;
import android.content.Context;
import android.content.Intent;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.os.SystemProperties;
import android.text.TextUtils;
import android.util.Log;

import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;

import com.eostek.tm.cpe.manager.CpeManager;

//EosTek Patch Begin
import android.media.AudioManager;
import android.provider.Settings.System;
//EosTek Patch End

/**
 * Class for Device info. <br/>
 */
public class Device {

    private static final String TAG = "Device";

    private static final boolean DBG = true;

    public static final String SCIFLY_PLATFORM_DONGLE = "dongle";

    public static final String SCIFLY_PLATFORM_TV = "tv";

    public static final String SCIFLY_PLATFORM_BOX = "box";

    public static final String ACTION_DEVICE_INFO_CHANGED = "com.eostek.scifly.intent.action.ACTION_DEVICE_INFO_CHANGED";

    private static final String AML_KEY_NAME_PATH = "/sys/class/aml_keys/aml_keys/key_name";

    private static final String AML_KEY_READ_PATH = "/sys/class/aml_keys/aml_keys/key_read";

    private static final String DEFAULT_SN_STR = "ES0000000000000000000000"; // 24 bits serial number
	
	private static final String SCIFLY_VIRTUALMAC_PATH = "/data/local/virtual_mac";

    /**
     * Get the MAC of the device according to the config file or database.
     * 
     * @param context {@link Context}.
     * @return MAC of the device.
     * @Author Melody.xu
     */
    public static String getHardwareAddress(Context context) {
        if (context == null) {
            return "";
        }
        // macAddress from ETH or WLAN
        String macAddressfrom = null;
        // macAddress from DataBase
        String macAddress = SciflyStore.Global.getString(context.getContentResolver(), Global.MAC_ADDR);
        // macAddress to return
        String reMacAddress = null;

        // get the mac address from database
        if (!TextUtils.isEmpty(macAddress)) {
            reMacAddress = macAddress;
        }

        String platform = SystemProperties.get("ro.scifly.platform", "");
        if (DBG) {
            Log.d(TAG, "platform : " + platform);
        }
        // get wifi mac
        if (SCIFLY_PLATFORM_DONGLE.equals(platform)) {
            macAddressfrom = getHardwareAddressFromWlan(context);
            // get mac from ethernet
        } else if (SCIFLY_PLATFORM_TV.equals(platform) || SCIFLY_PLATFORM_BOX.equals(platform)) {
            macAddressfrom = getHardwareAddressFromEth(context, "eth0");
        } else {
            // oho no
            macAddressfrom = getHardwareAddressFromWlan(context);
        }

        // Null check
        if (!TextUtils.isEmpty(macAddressfrom)) {
            reMacAddress = macAddressfrom;
            if (!macAddressfrom.equals(macAddress)) {
                setHardwareAddress(context, macAddressfrom);
            }
        }

        return reMacAddress;
    }

    public static String getDeviceName(Context context) {
        if (context == null) {
            return "";
        }

        String devName = SciflyStore.Global.getString(context.getContentResolver(), Global.DEVICE_NAME);
        if (DBG) {
            Log.d(TAG, "devName : " + devName);
        }
        if (TextUtils.isEmpty(devName)) {
            int num = (int) (Math.random() * 9000) + 1000;
            Log.d(TAG,Build.DEVICE);

            if (Build.DEVICE.equals("heran")) {
                devName = "HERAN智慧棒"+num;
            } else if(Build.DEVICE.equals("HeranSMARTTV")){
			    devName = "HERTV" + num; 	
			} else {
                devName = "Scifly" + num;
            }
            setDeviceName(context, devName);
        }
        if (DBG) {
            Log.d(TAG, "devName : " + devName);
        }

        return devName;
    }

    private static boolean setHardwareAddress(Context context, String macAddress) {
        if (context == null) {
            Log.w(TAG, "context is null. please check!");
            return false;
        }

        if (TextUtils.isEmpty(macAddress)) {
            Log.w(TAG, "macAddress is invalid. please check!");
            return false;
        }
        boolean flag = SciflyStore.Global.putString(context.getContentResolver(), Global.MAC_ADDR, macAddress);
        if (DBG) {
            Log.d(TAG, "set macAddress " + (flag ? "succeed" : "failed"));
        }

        return flag;
    }

    public static boolean setDeviceName(Context context, String devName) {
        boolean flag = false;
        if (context == null) {
            Log.w(TAG, "context is null. please check!");
            return false;
        }

        if (TextUtils.isEmpty(devName)) {
            Log.w(TAG, "devName is invalid. please check!");
            return false;
        }
        String oldDeviceName = SciflyStore.Global.getString(context.getContentResolver(), Global.DEVICE_NAME);
        if (!devName.equals(oldDeviceName)) {
            flag = SciflyStore.Global.putString(context.getContentResolver(), Global.DEVICE_NAME, devName);
            Intent intent0 = new Intent(ACTION_DEVICE_INFO_CHANGED);
            context.sendBroadcast(intent0);
        }
        if (DBG) {
            Log.d(TAG, "set device name " + (flag ? "succeed" : "failed"));
        }

        return flag;
    }

    private static String getHardwareAddressFromEth(Context context, String hwaddr) {
        // check ifname
        if (!hwaddr.equals("eth0") && !hwaddr.equals("eth1")) {
            Log.w(TAG, "please check your ifname");
            return "";
        }

        // read from file
        File file = new File("/sys/class/net/" + hwaddr + "/address");
        return readFromFile(file);
    }

    private static String getHardwareAddressFromWlan(Context context) {
        String strHardwareAddr = "";
        WifiManager wifiManager = (WifiManager) context.getSystemService(Context.WIFI_SERVICE);
        WifiInfo info = wifiManager.getConnectionInfo();
        strHardwareAddr = info.getMacAddress();

        return strHardwareAddr;
    }

    public static String getIfid() {
        // FIXME
        String ifId = "SysIdea";
        return ifId;
    }

    public static String getVersion() {
        String version = null;
        version = Build.VERSION.INCREMENTAL.substring(1);
        if (TextUtils.isEmpty(version)) {
            version = "1.0.0.0";
        }
        Log.d(TAG, "getVersion : " + version);
        return version;
    }

    public static String getDeviceCode() {
        String deviceCode = null;
        CpeManager cpe = CpeManager.getInstance();
        if (cpe != null) {
            deviceCode = cpe.getProductClass();
        }
        // Null & empty check
        if (TextUtils.isEmpty(deviceCode)) {
            deviceCode = "";
            Log.e(TAG, "Get ProductClass from CpeManager failed.");
        }

        return deviceCode;
    }

    public static String getTtag() {
        StringBuffer ttag = new StringBuffer();
        ttag.append(getDeviceCode());
        ttag.append("_");
        ttag.append(getVersion());
        ttag.append("_1");
        if (ttag.toString() != null) {
            Log.d(TAG, "getTtag : " + ttag.toString());
            return ttag.toString();
        }

        return "";
    }
    /**
     * eMode is 1 vip  
     * set vip to mBoot
     */
    public static boolean setVipMode(Context mContext, int eMode){
	/*
		File file = new File(HERAN_VIP_PATH);
		if(!file.exists()){
			try {  
                file.createNewFile();                   
            } catch (IOException e) {  
                // TODO Auto-generated catch block  
                e.printStackTrace();  
            }  
		}
		return writeToFile(String.valueOf(eMode)+"\n",new File(HERAN_VIP_PATH));
	*/
	 boolean rec = false;
     try {
			Class<?> tvManager = Class.forName("com.mstar.android.tvapi.common.TvManager");
            Method getInstance = tvManager.getMethod("getInstance");
            Method getEnvironment = tvManager.getMethod("setEnvironment", String.class,String.class);
            Object snObj = getEnvironment.invoke(getInstance.invoke(tvManager), "EOS_VIP",String.valueOf(eMode));

          if (null != snObj && !"".equals(snObj)) {
			rec = true;
			Log.e(TAG, "is not vip model");
            return rec;
           } else {
                return rec;
           }
         } catch (IllegalArgumentException e) {
             Log.e(TAG, e.getMessage());
             return rec;
			} catch (Exception e) {
                Log.e(TAG, e.getMessage());
			}
		return rec;
    }

    /**
     *   vip is 1 true else false   .
     * get vip mode from mBoot
     * @param context {@link Context}. 
     * @return true | false
     */
    public static boolean isVipMode(Context mContext) {
        boolean mvipMode = true;
        if (mContext == null) {
            Log.w(TAG, "context is null. please check!");
            return mvipMode;
        }
		// read from file
		/*
		File file = new File(HERAN_VIP_PATH);
		String vip = readFromFile(file);
        if("0".equals(vip)){
            mvipMode = false;
        } 
		*/
        try {
            Class<?> tvManager = Class.forName("com.mstar.android.tvapi.common.TvManager");
            Method getInstance = tvManager.getMethod("getInstance");
            Method getEnvironment = tvManager.getMethod("getEnvironment", String.class);
            Object snObj = getEnvironment.invoke(getInstance.invoke(tvManager), "EOS_VIP");

            if (null != snObj && !"".equals(snObj)&&"0".equals((String) snObj)) {
				mvipMode = false;
				Log.e(TAG, "is not vip model");
                return mvipMode;
            } else {
                return mvipMode;
            }
         } catch (IllegalArgumentException e) {
             Log.e(TAG, e.getMessage());
             return mvipMode;
        } catch (Exception e) {
                Log.e(TAG, e.getMessage());
		}
               		
        return mvipMode;
    }
    /**
     * Return the Babao number of the Device.
     * 
     * @return the Babao number.
     * @since API 2.2
     */
    public static String getBb() {
        String bbNum = "";
        CpeManager cpe = CpeManager.getInstance();
        if (cpe != null) {
            bbNum = cpe.getBBNumber();
        }
        // Null & empty check
        if (TextUtils.isEmpty(bbNum)) {
            bbNum = "105075";
        }

        return bbNum;
    }

    // the default value is "" currently.
    public static String getDeviceId(Context mContext) {
        // FIXME
        return getBb();
    }

    private static String readFromFile(File file) {
        if ((file != null) && file.exists()) {
            BufferedReader reader = null;
            try {
                reader = new BufferedReader(new FileReader(file));
                return reader.readLine();

            } catch (IOException e) {
                Log.w(TAG, "", e);
            } finally {
                if (reader != null) {
                    try {
                        reader.close();
                    } catch (IOException e) {
                    }
                }
            }
        }

        return "";
    }
    
    /**
     * @author frank.zhang
     * @date 2015-2-27 
     * 
     * Write something to the specified file.
     */
    private static boolean writeToFile(String content, File file) {

        if (!TextUtils.isEmpty(content) && null != file) {
            FileWriter fileWriter = null;

            try {
                fileWriter = new FileWriter(file);
                fileWriter.write(content);
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
                return false;
            } finally {
                if (null != fileWriter) {
                    try {
                        fileWriter.close();
                    } catch (IOException e) {
                        Log.e(TAG, e.getMessage());
                        return false;
                    }
                }
            }

            return true;
        }

        return false;
    }
	
	/**
	*@author laird.li 2016-4-28
	*设置虚拟mac地址
	*/	
	public static boolean setVirtualMac(String content){
		File file = new File(SCIFLY_VIRTUALMAC_PATH);
		if(!file.exists()){
			try {  
                file.createNewFile();                   
            } catch (IOException e) {  
                // TODO Auto-generated catch block  
                e.printStackTrace();  
            }  
		}
		return writeToFile(content+"\n",new File(SCIFLY_VIRTUALMAC_PATH));
	}
    
    /**
     * @author frank.zhang
     * @date 2014-8-5
     * 
     * Provide method to retrieve device serial number
     */
    public static final String getDeviceSN() {
        String platform = SystemProperties.get("ro.scifly.platform", "");
        if (DBG) {
            Log.d(TAG, "platform : " + platform);
        }

        if (SCIFLY_PLATFORM_DONGLE.equals(platform)) {
            // first write "usid" to key_name
            if (writeToFile("usid", new File(AML_KEY_NAME_PATH))) {
                // then read sn from key_read
                String _sn = readFromFile(new File(AML_KEY_READ_PATH));

                if (!TextUtils.isEmpty(_sn)) {
                    return "ES" + _sn.toUpperCase();
                } else {
                    return DEFAULT_SN_STR;
                }
            }
        } else if (SCIFLY_PLATFORM_TV.equals(platform) || SCIFLY_PLATFORM_BOX.equals(platform)) {
            try {
                Class<?> tvManager = Class.forName("com.mstar.android.tvapi.common.TvManager");
                Method getInstance = tvManager.getMethod("getInstance");
                Method getEnvironment = tvManager.getMethod("getEnvironment", String.class);
                Object snObj = getEnvironment.invoke(getInstance.invoke(tvManager), "serid");

                if (null != snObj && !"".equals(snObj)) {
                    return (String) snObj;
                } else {
                    return DEFAULT_SN_STR;
                }

            } catch (IllegalArgumentException e) {
                Log.e(TAG, e.getMessage());
                return DEFAULT_SN_STR;
            } catch (Exception e) {
                Log.e(TAG, e.getMessage());
                return DEFAULT_SN_STR;
            }
        }

        return DEFAULT_SN_STR;
    }
	
	//EosTek Patch Begin
	public static void restoreVolume(Context context) {
		float volume = System.getFloat(context.getContentResolver(), "eostek_restore_volume", 0.20f);
		//AudioSystem.setMasterVolume(volume);
		int vol = (int) (volume * 100);
		AudioManager mAudioManager = (AudioManager) context.getSystemService(Context.AUDIO_SERVICE);
        mAudioManager.setStreamVolume(AudioManager.STREAM_SYSTEM, vol, 0);
	}

	//EosTek Patch End
}
