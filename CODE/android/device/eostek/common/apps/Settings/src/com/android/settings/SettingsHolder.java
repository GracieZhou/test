
package com.android.settings;

import java.util.ArrayList;
import java.util.HashMap;
import scifly.device.Device;
import android.app.Activity;
import scifly.provider.SciflyStore;
import scifly.provider.SciflyStore.Global;
import android.content.Context;
import android.content.Intent;
import android.content.res.TypedArray;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;
import android.os.Build;
import android.util.Log;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.GridView;
import android.widget.SimpleAdapter;
import com.android.settings.bugreport.BugReportActivity;
import com.android.settings.datetimecity.DateTimeCitySettingsActivity;
import com.android.settings.datetimecity.DateTimeCitySettingsLogic;
import com.android.settings.deviceinfo.DeviceInfoActivity;
import com.android.settings.deviceinfo.DeviceInfoLogic;
import com.android.settings.network.NetworkSettingActivity;
import com.android.settings.network.NetworkSettingLogic;
import com.android.settings.system.SystemSettingsActivity;
import com.android.settings.update.SystemUpdateActivity;
import com.android.settings.widget.TitleWidget;
import com.android.settings.R;
import android.net.NetworkInfo.State;

public class SettingsHolder {

    private SettingsActivity mSettingsActivity;

    private TitleWidget mSettingTitleWidget;

    public GridView mGridView;

    public int mPosition;

    public SettingsHolder(SettingsActivity activity) {
        this.mSettingsActivity = activity;
    }

    public void findViews() {
        mGridView = (GridView) mSettingsActivity.findViewById(R.id.gridview);
    }

    public void registerListener() {
        int[] image = new int[] {
                R.drawable.network, R.drawable.display_sound, R.drawable.area_time, R.drawable.user_back,
                R.drawable.system_setting, R.drawable.system_update, R.drawable.about, R.drawable.projector,
        };
        String[] firstText = null;
        // , R.drawable.projector
        if (Build.DEVICE.equals("heran") || Build.DEVICE.equals("scifly_m202_1G")) {
            firstText = new String[] {
                    mSettingsActivity.getResources().getString(R.string.network_settings),
                    mSettingsActivity.getResources().getString(R.string.image_sound),
                    mSettingsActivity.getResources().getString(R.string.area_time),
                    mSettingsActivity.getResources().getString(R.string.user_feedback),
                    mSettingsActivity.getResources().getString(R.string.system_settings),
                    mSettingsActivity.getResources().getString(R.string.system_update),
                    mSettingsActivity.getResources().getString(R.string.about)
            };
        } else {
            firstText = new String[] {
                    mSettingsActivity.getResources().getString(R.string.network_settings),
                    mSettingsActivity.getResources().getString(R.string.sound),
                    mSettingsActivity.getResources().getString(R.string.area_time),
                    mSettingsActivity.getResources().getString(R.string.user_feedback),
                    mSettingsActivity.getResources().getString(R.string.system_settings),
                    mSettingsActivity.getResources().getString(R.string.system_update),
                    mSettingsActivity.getResources().getString(R.string.about)
            };
        }

        String defaults = " ";
        String[] secondText = new String[] {
                getNetworkName(), defaults, getCityTime(), defaults, defaults, getVersionNUmber(), getDeviceDB()
        };

        ArrayList<HashMap<String, Object>> lstImageItem = new ArrayList<HashMap<String, Object>>();
        for (int i = 0; i < 7; i++) {
            HashMap<String, Object> map = new HashMap<String, Object>();
            map.put("ItemImage", image[i]);
            map.put("ItemText", firstText[i]);
            map.put("SecondText", secondText[i]);
            lstImageItem.add(map);
        }
        SimpleAdapter saImageItems = new SimpleAdapter(mSettingsActivity, lstImageItem,
                R.layout.list_item_main_setting, new String[] {
                        "ItemImage", "ItemText", "SecondText"
                }, new int[] {
                        R.id.ItemImage, R.id.firstText, R.id.SecondText
                });
        mGridView.setAdapter(saImageItems);
        mGridView.setOnItemClickListener(new ItemClickListener());
    }

    class ItemClickListener implements OnItemClickListener {
        public void onItemClick(AdapterView<?> arg0, View arg1, int position, long arg3) {
            Intent intent = new Intent();
            mPosition = position;
            mGridView.requestFocus();
            mGridView.setSelection(position);
            switch (position) {
                case 0:
                    intent.setClass(mSettingsActivity, NetworkSettingActivity.class);
                    break;
                case 1:
                    intent.setClass(mSettingsActivity, DisplayAndSoundActivity.class);
                    break;
                case 2:
                    intent.setClass(mSettingsActivity, DateTimeCitySettingsActivity.class);
                    break;
                case 3:
                    intent.setClass(mSettingsActivity, BugReportActivity.class);
                    break;
                case 4:
                    intent.setClass(mSettingsActivity, SystemSettingsActivity.class);
                    break;
                case 5:
                    intent.setClass(mSettingsActivity, SystemUpdateActivity.class);
                    break;
                case 6:
                    intent.setClass(mSettingsActivity, DeviceInfoActivity.class);
                    break;
            }
            mSettingsActivity.startActivity(intent);
        }
    }

    private String getNetworkName() {
        String name;
        int state = NetworkSettingLogic.ETHERNET_STATE_DISABLED;
        ConnectivityManager cm = (ConnectivityManager) mSettingsActivity
                .getSystemService(Activity.CONNECTIVITY_SERVICE);
        NetworkInfo info = cm.getNetworkInfo(ConnectivityManager.TYPE_ETHERNET);
        Log.d("SettingsHolder", "    +nfo.isAvailable()" + info.isAvailable());
        Log.d("SettingsHolder ", "   +nfo.isConnected()" + info.isConnected());
        if (info.isConnected()) {
            state = NetworkSettingLogic.ETHERNET_STATE_ENABLED;
        }
        NetworkInfo wifiStateInfo = cm.getNetworkInfo(ConnectivityManager.TYPE_WIFI);
        if (state == NetworkSettingLogic.ETHERNET_STATE_ENABLED) {
            name = mSettingsActivity.getResources().getString(R.string.ethernet);
        } else {
            if (wifiStateInfo == null || !wifiStateInfo.isAvailable() || wifiStateInfo.getState() == State.DISCONNECTED) {
                name = " ";
            } else {
                WifiManager wifiManager = (WifiManager) mSettingsActivity
                        .getSystemService(Context.WIFI_SERVICE);
                WifiInfo wifiInfo = wifiManager.getConnectionInfo();
                name = wifiInfo.getSSID();
                if (name.contains("\"")) {
                    name = name.replace("\"", "");
                }
            }
        }

        if (name.equals("0x")) {
            name = " ";
        }
        return name;
    }

    private String getCityTime() {
        String name = "";
        name = DateTimeCitySettingsLogic.getCurrentCity(mSettingsActivity);
        if (android.text.format.DateFormat.is24HourFormat(mSettingsActivity)) {
            name = name + "-24h";
        } else {
            name = name + "-12h";
        }
        return name;
    }

    private String getVersionNUmber() {
        return mSettingsActivity.getResources().getString(R.string.about_build_number) + "-"
                + DeviceInfoLogic.getBuildNumber();
    }

    private String getDeviceDB() {
        return mSettingsActivity.getResources().getString(R.string.about_more_device_id) + "-" + Device.getBb();
    }
}
