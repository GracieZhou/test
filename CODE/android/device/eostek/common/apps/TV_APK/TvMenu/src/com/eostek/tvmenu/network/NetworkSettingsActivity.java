
package com.eostek.tvmenu.network;

import com.eostek.tvmenu.R;
import com.eostek.tvmenu.TvMenuActivity;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;

public class NetworkSettingsActivity extends Activity {

    private static final String TAG = "MSettings.NetworkSettingsActivity";

    // all Settings object
    private EthernetSettings mEthernetSettings;

    private WifiSettings mWifiSettings;

    NetworkSettingsViewHolder mNetworkSettingsHolder;

    private boolean mIsFocusRight = false;

    private int mCurrentPosition = Network_Constants.NETWORK_STATUS;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // set layout.
        setContentView(R.layout.net_setting);

        mNetworkSettingsHolder = new NetworkSettingsViewHolder(this);
        // initialize setting tools:
        initSettings();

        Intent mIntent = getIntent();
        String string = mIntent.getStringExtra("NetworkSettings");
        if (string.equals("ethernet")) {
            swapSelected(Network_Constants.ETHERNET_SETTINGS);
        } else if (string.equals("wifi")) {
            swapSelected(Network_Constants.WIFI_SETTINGS);
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_MENU:
                Intent mIntent = new Intent(NetworkSettingsActivity.this,TvMenuActivity.class);
                startActivity(mIntent);
                finish();
                return true;
            default:
                break;
        }

        NetworkTools.logd(TAG, "mFocusIsRight : " + mIsFocusRight + ",mCurrentPosition : " + mCurrentPosition);
        // dispatch key event to Settings
        if (mIsFocusRight) {
            boolean flag = dispatchKeyEvent(keyCode, event);
            NetworkTools.logd(TAG, "dispatchKeyEvent return " + flag);

            // handle key event done
            if (flag) {
                return true;
            } else {
                if (mCurrentPosition == Network_Constants.ETHERNET_SETTINGS) {
                    if (mEthernetSettings.isConfigEditTextFocused() && !mEthernetSettings.isV4FirstFocused()) {
                        return false;
                    }
                }
            }

            // focus on left
        } else {
            if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                NetworkTools.logd(TAG, "KeyEvent.KEYCODE_DPAD_RIGHT");
                return entrySettings(true);
            }
        }

        Log.d(TAG, "onKeyDown, default onkey");
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        NetworkTools.logd(TAG, "mFocusIsRight : " + mIsFocusRight + ",mCurrentPosition : " + mCurrentPosition);

        return super.onKeyUp(keyCode, event);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mEthernetSettings.onExit();
        mWifiSettings.onExit();
    }

    public EthernetSettings getEthernetSettings() {
        return mEthernetSettings;
    }

    public WifiSettings getWifiSettings() {
        return mWifiSettings;
    }

    private void initSettings() {
        mWifiSettings = new WifiSettings(this);
        mEthernetSettings = new EthernetSettings(this);
    }

    private boolean dispatchKeyEvent(int keyCode, KeyEvent event) {
        switch (mCurrentPosition) {
            case Network_Constants.ETHERNET_SETTINGS:
                return mEthernetSettings.onKeyEvent(keyCode, event);
            case Network_Constants.WIFI_SETTINGS:
                return mWifiSettings.onKeyEvent(keyCode, event);
            default:
                break;
        }

        return false;
    }

    private boolean entrySettings(boolean flag) {
        // save flag
        mIsFocusRight = flag;
        switch (mCurrentPosition) {
            case Network_Constants.ETHERNET_SETTINGS:
                mEthernetSettings.onFocusChange(flag);
                return true;
            case Network_Constants.WIFI_SETTINGS:
                mWifiSettings.onFocusChange(flag);
                return true;
            default:
                break;
        }

        return false;
    }

    private void swapSelected(int position) {
        NetworkTools.logd(TAG, "position, " + position);
        View focusView = getCurrentFocus();
        if (focusView != null) {
            // hide softInputMethod
            ((InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE)).hideSoftInputFromWindow(
                    focusView.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
        }
        switch (position) {
            case Network_Constants.ETHERNET_SETTINGS:
                mNetworkSettingsHolder.refreshSettingTitle(R.string.ethernet_settings_title);
                mEthernetSettings.setVisible(true);
                break;
            case Network_Constants.WIFI_SETTINGS:
                mNetworkSettingsHolder.refreshSettingTitle(R.string.wifi_settings_title);
                mWifiSettings.setVisible(true);
                break;
            default:
                break;
        }
    }

}
