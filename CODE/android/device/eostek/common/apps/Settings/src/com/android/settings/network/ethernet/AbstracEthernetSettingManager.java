
package com.android.settings.network.ethernet;

public abstract class AbstracEthernetSettingManager {

    public static AbstracEthernetSettingManager getInstance() {
        return null;
    }

    public abstract EthernetDisplayInfo getEthernetDisplayInfo();

    public abstract boolean isAutoIP();

    public abstract void configEthernetV4(EthernetDisplayInfo displayInfo);

    public abstract void setEnabled(boolean b);

    public abstract void setNetwork();

}
