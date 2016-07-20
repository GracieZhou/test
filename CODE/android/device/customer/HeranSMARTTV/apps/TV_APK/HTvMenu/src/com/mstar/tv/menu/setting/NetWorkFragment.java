
package com.mstar.tv.menu.setting;

import java.util.ArrayList;

import scifly.device.Device;
import android.content.Intent;
import android.util.Log;
import android.view.KeyEvent;

import com.mstar.android.tv.TvCommonManager;
import com.mstar.android.tvapi.common.vo.TvOsType.EnumInputSource;
import com.mstar.tv.MenuConstants;
import com.mstar.tv.menu.R;
import com.mstar.tv.menu.setting.network.NetworkSettingsActivity;

public class NetWorkFragment extends PublicFragement {
    private EosSettingItem wiredNetworkItem = null;

    private EosSettingItem wirelessNetworkItem = null;

    private EosSettingItem expertSettingsItem = null;

    private String[] networkSetting_title;

    @Override
    protected void initItems() {
        setTag("network");
        mItems = new ArrayList<EosSettingItem>();
        networkSetting_title = getActivity().getResources().getStringArray(R.array.network);

        wiredNetworkItem = new EosSettingItem(this, networkSetting_title[0],
                MenuConstants.ITEMTYPE_BUTTON, true);
        mItems.add(wiredNetworkItem);
        wirelessNetworkItem = new EosSettingItem(this, networkSetting_title[1],
                MenuConstants.ITEMTYPE_BUTTON, true);
        mItems.add(wirelessNetworkItem);
        expertSettingsItem = new EosSettingItem(this, networkSetting_title[2],
                MenuConstants.ITEMTYPE_BUTTON, true);
        mItems.add(expertSettingsItem);
        initDate();
    }

    @Override
    void callBack(int resultValue, int position) {
    }

    @Override
    void callBack(Boolean resultValue, int position) {
    }

    @Override
    void callBack(int position) {
        switch (position) {
            case 0: {
                // TODO wired network
                Log.e("test", "startActivity(NetworkSettingsActivity);");
                Intent mIntent = new Intent(getActivity(), NetworkSettingsActivity.class);
                mIntent.putExtra("NetworkSettings", "ethernet");
                startActivity(mIntent);
                getActivity().finish();
                break;
            }
            case 1: {
                // TODO wireless network
                Intent mIntent = new Intent(getActivity(), NetworkSettingsActivity.class);
                mIntent.putExtra("NetworkSettings", "wifi");
                startActivity(mIntent);
                getActivity().finish();
                break;
            }
            case 2: {
                TvCommonManager.getInstance()
                        .setInputSource(EnumInputSource.E_INPUT_SOURCE_STORAGE);
                
                Intent intent = new Intent(Intent.ACTION_MAIN);
                if (Device.isVipMode(getActivity())) {
                    intent.setClassName("com.android.settings", "com.android.settings.network.NetworkSettingActivity");
                } else {
                    intent.setAction("android.settings.SIMPLESETTINGS");
                }
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                startActivity(intent);
                getActivity().finish();
                break;
            }
            default:
                break;
        }
    }

    @Override
    protected void initDate() {
        mAdapter.setHasShowValue(true);
    }

    @Override
    boolean doKeyDownOrUp(int keyCode, KeyEvent event) {
        return false;
    }

}
