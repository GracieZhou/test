
package com.eostek.isynergy.setmeup.config;

import android.content.Context;
import android.util.Log;

import com.eostek.isynergy.setmeup.common.Constants;
import com.eostek.isynergy.setmeup.common.Constants.ACTION_TYPE;
import com.eostek.isynergy.setmeup.config.network.WifiAPManager;
import com.eostek.isynergy.setmeup.config.nickname.DevNameService;
import com.eostek.isynergy.setmeup.config.restore.RestoreFactoryService;
import com.eostek.isynergy.setmeup.config.timezone.TimeZoneService;
import com.eostek.isynergy.setmeup.devsel.SelDeviceService;

public class ServiceManager implements IfService {

    private final String TAG = "ServiceManager";

    private static ServiceManager serManager;

    private TimeZoneService tzService;

    private RestoreFactoryService facService;

    // private AdvancedNetConfigService adnetService;

    private DevNameService dnService;

    private SelDeviceService sdSerivce;

    private WifiAPManager wifiAPService;

    private Context context;

    private ServiceManager(Context context) {
        Log.d(TAG, "ServiceManager construct!");
        this.context = context;

        tzService = new TimeZoneService(context);

        facService = new RestoreFactoryService(context);

        // adnetService = new AdvancedNetConfigService(context);

        dnService = new DevNameService(context);

        sdSerivce = new SelDeviceService(context);

        wifiAPService = new WifiAPManager(context);
    }

    public static ServiceManager getInstance(Context context) {
        if (serManager == null) {
            serManager = new ServiceManager(context);
        }

        return serManager;
    }

    @Override
    public int doAction(ACTION_TYPE type, String paras) {
        switch (type) {
            case REQ_PAIRING_CODE: {
                Log.d(TAG, "REQ_PAIRING_CODE");
                // service = new SelDeviceService(context);
                int ret = sdSerivce.doAction(type, paras);
                return ret;
            }
            case WIFI_SETTING: {
                Log.d(TAG, "WIFI_SETTING");
                /*
                 * WifiAPManager apManager = new WifiAPManager(context); int ret
                 * = apManager.setWifi(paras);
                 */

                WifiSettingTask task = new WifiSettingTask(paras);
                new Thread(task).start();

                return Constants.SuccessCode.SET_ME_UP_SUCCESS.getValue();

            }
            case TIMEZONE_SETTING: {
                Log.d(TAG, "TIMEZONE_SETTING");
                // service = new TimeZoneService(context);
                int ret = tzService.doAction(type, paras);
                return ret;
            }
            case RESTORE_FACTORY: {
                Log.d(TAG, "RESTORE_FACTORY");
                int ret = facService.doAction(type, paras);
                return ret;
            }
            /*
             * case ADNET_SETTING: { Log.d(TAG,"ADNET_SETTING"); int ret =
             * adnetService.doAction(type,paras); return ret; }
             */
            case DEVICE_NAME_SETTING: {
                Log.d(TAG, "DEVICE_NAME_SETTING");
                // service = new DevNameService(context);
                int ret = dnService.doAction(type, paras);

                return ret;
            }
            case SWITCH_NETWORK: {
                Log.d(TAG, "SWITCH_NETWORK");
                // WifiAPManager apManager = new WifiAPManager(context);
                int ret = wifiAPService.switchNetWork();
                return ret;
            }
            case WIFI_ENABLED: {
                Log.d(TAG, "Enable wifi");
                WifiEnabledTask task = new WifiEnabledTask();
                new Thread(task).start();

                return Constants.SuccessCode.SET_ME_UP_SUCCESS.getValue();
            }
            default:
                return Constants.ErrorCode.SERVICE_NOT_SUPPORTED.getValue();
        }
    }

    public TimeZoneService getTimeZoneService() {
        return this.tzService;
    }

    public RestoreFactoryService getRestoreFactoryService() {
        return this.facService;
    }

    /*
     * public AdvancedNetConfigService getAdvancedNetService() { return
     * adnetService; }
     */

    public DevNameService getDevNameService() {
        return dnService;
    }

    public SelDeviceService getSelDevService() {
        return sdSerivce;
    }

    public WifiAPManager getWifiApService() {
        return wifiAPService;
    }

    class WifiSettingTask implements Runnable {

        private String paras;

        WifiSettingTask(String paras) {
            this.paras = paras;
        }

        @Override
        public void run() {
            WifiAPManager apManager = new WifiAPManager(context);
            apManager.setWifi(paras);
        }

    }

    class WifiEnabledTask implements Runnable {
        @Override
        public void run() {
            WifiAPManager apManager = new WifiAPManager(context);
            apManager.enableWifi();
        }

    }
}
