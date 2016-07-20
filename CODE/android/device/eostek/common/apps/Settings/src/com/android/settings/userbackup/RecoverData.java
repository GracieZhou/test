
package com.android.settings.userbackup;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.util.Locale;

import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import scifly.device.Device;
import scifly.permission.Permission;
import scifly.provider.SciflyStore;
import scifly.provider.SciflyStore.Global;
import scifly.security.SecurityManager;

import android.app.ActivityManagerNative;
import android.app.AlarmManager;
import android.app.IActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.net.wifi.WifiManager;
import android.os.Environment;
import android.os.RemoteException;
import android.util.Log;

/**
 * This class used to receive broadcasts, and then restore data, which is
 * triggered by the user to restore the factory settings configuration data.
 * 
 * @author melody.xu
 * @date 2014-6-20
 */
public class RecoverData extends BroadcastReceiver {
    private static final Locale DEFAULT_LOCALE = Locale.SIMPLIFIED_CHINESE;

    private static String TAG = "RecoverData";

    private static String DIRFILE = Environment.getExternalStorageDirectory().getAbsolutePath() + "/userdata";

    private static String XMLFILE = ".user_config_data.xml";

    public static final String CITY_NAME = "city_name";

    private static String mDevcie = null;

    private static String mCity = null;

    private static String mTimeZone = null;

    private static String mLocales = null;

    private static String WIFICONFIG_OLD = "/data/misc/wifi/wpa_supplicant.conf";

    private static String USERDATA_RECOVER = "android.userbackup.action.USERDATA_RECOVER";

    /**
     * This method is an interface that is used to restore the user to recover
     * data.
     * 
     * @param Context
     * @return When restoring data is successful, it returns true, otherwise it
     *         returns false.
     */
    public boolean reData(Context mContext) {
        AlarmManager timeZone = (AlarmManager) mContext.getSystemService(Context.ALARM_SERVICE);
        // recover device name
        mDevcie = getValue("device", "device_name");
        Log.d(TAG, "mDevcie:" + mDevcie);
        if (!"".equals(mDevcie) && mDevcie != null && !mDevcie.equals("none"))
            Device.setDeviceName(mContext, mDevcie);
        // recover city
        mCity = getValue("city", "my_city");
        Log.d(TAG, "mCity:" + mCity);
        if (!"".equals(mCity) && mCity != null && !mCity.equals("none"))
            SciflyStore.Global.putString(mContext.getContentResolver(), CITY_NAME, mCity);
        // recover time zone
        mTimeZone = getValue("timezone", "time_zone");
        Log.d(TAG, "mTimeZone:" + mTimeZone);
        if (!"".equals(mTimeZone) && mTimeZone != null && !mTimeZone.equals("none"))
            timeZone.setTimeZone(mTimeZone);
        // recover locale
        mLocales = getValue("locale", "my_locale");
        Log.d(TAG, "mLocales:" + mLocales);
        Locale loc = null;
        if (!"".equals(mLocales) && mLocales != null && !mLocales.equals("none")) {
            if (mLocales.length() > 3) {
                loc = new Locale(mLocales.substring(0, 2), mLocales.substring(2, 4));
            } else {
                loc = new Locale(mLocales.substring(0, 2));
            }
            Log.d(TAG, "loc  1" + loc.getLanguage() + "    " + loc.getCountry());
            IActivityManager am = ActivityManagerNative.getDefault();
            Configuration config = null;
            try {
                config = am.getConfiguration();
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            config.locale = loc;
            Log.d(TAG, "config  1" + config.locale.getLanguage() + "  " + config.locale.getCountry());
            try {
                am.updateConfiguration(config);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        // recover wifi info.
        File file = new File(DIRFILE + "/wpa_supplicant.conf");
        if (file.exists()) {
            copyFile();
            restartWifi(mContext);
        }

        return true;
    }

    private void restartWifi(Context mContext) {
        WifiManager wifiManager = (WifiManager) mContext.getSystemService(Context.WIFI_SERVICE);

        wifiManager.setWifiEnabled(false);
        wifiManager.setWifiEnabled(true);
    }

    /**
     * This method is used to return the user data backup.
     * 
     * @param strType the first parameter is the type of data, such as "device".
     * @param strName the second parameter is the name of the data, such as
     *            "device_name".
     * @return If you get the value, that value is returned, otherwise return
     *         null.
     */
    public static String getValue(String strType, String strName) {
        Log.d(TAG, "GetValue");
        File file = new File(DIRFILE + "/" + XMLFILE);
        if (file.exists()) {
            Document document = BackUpData.load(DIRFILE + "/" + XMLFILE);
            Node root = document.getDocumentElement();
            int mtypecount = 0;
            if (root.hasChildNodes()) {
                /** typelist */
                NodeList typelist = root.getChildNodes();
                /** 循环取得所有类型的节点 ，当所得节点为传入类型时，再更新该类型下满足条件的名字的值 */
                for (int i = 0; i < typelist.getLength(); i++) {
                    Node type = typelist.item(i);
                    if (type.getNodeType() == Node.ELEMENT_NODE && type.getNodeName().equals(strType))
                        mtypecount = i;
                }
                NodeList namelist = typelist.item(mtypecount).getChildNodes();
                for (int k = 0; k < namelist.getLength(); k++) {
                    Node name = namelist.item(k);
                    /** 查询符合条件的名字的值,如 device_name的值 */
                    if (name.getNodeType() == Node.ELEMENT_NODE && name.getNodeName().equals(strName)) {
                        return name.getFirstChild().getNodeValue();
                    }
                }
            }
        }
        return null;
    }

    /**
     * This method is used to recover the user's wifi configuration data.
     * 
     * @param null
     * @return When restoring data is successful, it returns true, otherwise it
     *         returns false.
     */
    public boolean copyFile() {
        try {
            File file = new File(DIRFILE + "/wpa_supplicant.conf");
            if (file.exists()) {
                Log.d(TAG, "cp " + DIRFILE + "/wpa_supplicant.conf" + " " + WIFICONFIG_OLD);
                Permission eshell = new Permission("JCheb2lkLnNldHRpbmdzanJt");
                boolean b_shell = eshell.exec("cp " + DIRFILE + "/wpa_supplicant.conf" + " " + WIFICONFIG_OLD);
                if (b_shell) {
                    Log.d(TAG, "copy wpa_supplicant.conf succeed .");
                    boolean chmod = eshell.exec("chmod 0660" + " " + WIFICONFIG_OLD);
                    boolean chown = eshell.exec("chown system.wifi" + " " + WIFICONFIG_OLD);
                    if (chmod && chown) {
                        Log.d(TAG, "set wpa_supplicant.conf permission succeed .");
                    } else {
                        Log.d(TAG, "set wpa_supplicant.conf permission failed.");
                        return false;
                    }
                } else {
                    Log.d(TAG, "copy wpa_supplicant.conf failed .");
                }
                return b_shell;
            }

        } catch (Exception e) {
            System.out.println("复制单个文件操作出错");
            e.printStackTrace();

        }
        return false;

    }

    @Override
    public void onReceive(Context context, Intent intent) {
        Boolean bl = true;
        if (USERDATA_RECOVER.equals(intent.getAction())) {
            if (bl) {
                SciflyStore.Global.putString(context.getContentResolver(), Global.RECOVERY_LABEL, "no_recovery");
                reData(context);
            }
        }

    }
}
