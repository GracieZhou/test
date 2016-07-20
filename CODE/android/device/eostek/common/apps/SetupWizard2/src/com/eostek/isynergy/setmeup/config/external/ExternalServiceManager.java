
package com.eostek.isynergy.setmeup.config.external;

import android.content.Context;

public class ExternalServiceManager {
    private static ExternalServiceManager exteServiceManager;

    private DeviceNameService devNameService;

    private Context context;

    public ExternalServiceManager(Context context) {
        this.context = context;
    }

    public static ExternalServiceManager getInstance(Context context) {
        if (exteServiceManager == null) {
            exteServiceManager = new ExternalServiceManager(context);
        }

        return exteServiceManager;
    }

    public DeviceNameService getDevNameService() {
        if (devNameService == null) {
            devNameService = new DeviceNameService(context);
        }

        return devNameService;
    }

    public void release() {
        if (devNameService != null) {
            devNameService.release();
        }
    }
}
