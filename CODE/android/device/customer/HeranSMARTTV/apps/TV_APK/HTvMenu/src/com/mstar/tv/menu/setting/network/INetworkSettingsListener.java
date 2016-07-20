
package com.mstar.tv.menu.setting.network;

import android.view.KeyEvent;

public interface INetworkSettingsListener {

    public void onExit();

    public boolean onKeyEvent(int keyCode, KeyEvent keyEvent);

    public void onFocusChange(boolean hasFocus);

    public void onWifiHWChanged(boolean isOn);

}
