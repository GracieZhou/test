
package com.mstar.tv.menu.setting.network;

import android.widget.ProgressBar;
import com.mstar.tv.menu.R;

import android.app.Activity;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.CheckBox;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.RelativeLayout;


/**
 * Wi-Fi components.
 */
public class WifiSettingsHolder {

    private static final String TAG = "MSettings.WifiSettingsHolder";

    private Activity mContext;

    // wifi settings root layout
    private LinearLayout mWifiSettingsRootLayout;

    // wifi settings toggle
    private RelativeLayout mWifiToggleLayout;
    private CheckBox mWifiSwitchChbox;
    private ListView mWifiListView;
    private LinearLayout mFooterView;
    
    private ProgressBar mWifipb;

    public WifiSettingsHolder(Activity networkSettingActivity) {
        this.mContext = networkSettingActivity;
        
        Log.v("adm", "WifiSettingsHolder mContext = " + (mContext == null));

        findViews();
        setListeners();
    }

    public void setVisible(boolean visible) {
        if (visible) {
            mWifiSettingsRootLayout.setVisibility(View.VISIBLE);
        } else {
            mWifiSettingsRootLayout.setVisibility(View.GONE);
        }
    }

    public RelativeLayout getWifiToggleLayout() {
        return mWifiToggleLayout;
    }
    
    public ProgressBar   getProgressBar() {
        return mWifipb;
    }
    
    public ListView getWifiListView() {
        return mWifiListView;
    }

    public CheckBox getWifiToogleCheckBox() {
        return mWifiSwitchChbox;
    }

    public LinearLayout getFooterView() {
        return mFooterView;
    }

    public void requestFocus(int position) {
        switch (position) {
            case Network_Constants.SETTING_ITEM_0:
                mWifiSwitchChbox.requestFocus();
                break;
            case Network_Constants.SETTING_ITEM_1:
                mWifiListView.requestFocus();
                break;
            default:
                break;
        }
    }

    public void clearFocus(int position) {
        switch (position) {
            case Network_Constants.SETTING_ITEM_0:
                mWifiSwitchChbox.clearFocus();
                break;
            case Network_Constants.SETTING_ITEM_1:
                mWifiListView.clearFocus();
                break;
            default:
                break;
        }
    }

    private void findViews() {
        Log.v("adm", "mContext = " + (mContext == null));
        // root layout
        mWifiSettingsRootLayout = (LinearLayout) mContext.findViewById(R.id.wireless_setting_ll);
        // wifi toggle
        mWifiToggleLayout = (RelativeLayout) mContext.findViewById(R.id.wifi_switch_rl);
        mWifiSwitchChbox = (CheckBox) mContext.findViewById(R.id.wifi_switch_checkbox);
        // listview contain wifi signal
        mWifiListView = (ListView) mContext.findViewById(R.id.wifi_ssid_listview);
        mWifiListView.setDividerHeight(0);
        // footer view of listview
        LayoutInflater factory = LayoutInflater.from(mContext);
        mFooterView = (LinearLayout) factory.inflate(R.layout.add_wifi_ssid_btn, null);
        mWifipb=(ProgressBar) mContext.findViewById(R.id.progressBar1);
    }

//    private View getViewById(int id) {
//        return mNetworkSettingActivity.findViewById(id);
//    }

    private void setListeners() {
        mWifiSwitchChbox.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View view, boolean hasFocus) {
                Log.d(TAG, "hasFocus, " + hasFocus);
                if (hasFocus) {
                    mWifiToggleLayout.setBackgroundResource(R.drawable.set_button);
                } else {
                    mWifiToggleLayout.setBackgroundResource(R.drawable.one_px);
                }
            }
        });
    }

}
