
package com.android.settings.network.ethernet;

public class EthernetSettingManagerEmpty extends AbstracEthernetSettingManager {

    private static EthernetSettingManagerEmpty mEthernetSettingManager;

    public static EthernetSettingManagerEmpty getInstance() {
        if (mEthernetSettingManager == null) {
            mEthernetSettingManager = new EthernetSettingManagerEmpty();
        }

        return mEthernetSettingManager;
    }

    private EthernetSettingManagerEmpty() {
    }

    @Override
    public EthernetDisplayInfo getEthernetDisplayInfo() {
        return null;
    }

    @Override
    public boolean isAutoIP() {
        return false;
    }

    @Override
    public void configEthernetV4(EthernetDisplayInfo displayInfo) {
    }

    @Override
    public void setEnabled(boolean b) {
    }

    @Override
    public void setNetwork() {
    }

}
