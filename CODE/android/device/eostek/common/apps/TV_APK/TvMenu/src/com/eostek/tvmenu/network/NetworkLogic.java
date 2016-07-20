package com.eostek.tvmenu.network;

import com.mstar.android.tv.TvCommonManager;
import com.mstar.android.tvapi.common.vo.TvOsType.EnumInputSource;

import android.content.ComponentName;
import android.content.Intent;

public class NetworkLogic {

private NetWorkFragment mFragment;
	
	public NetworkLogic(NetWorkFragment f) {
		mFragment = f;
	}

	public void startNetworkSetting(String string) {
        Intent mIntent = new Intent(mFragment.getActivity(), NetworkSettingsActivity.class);
        mIntent.putExtra("NetworkSettings", string);
        mFragment.startActivity(mIntent);
        mFragment.getActivity().finish();
        
    }
	
	protected void startExpertSetting(){
		TvCommonManager.getInstance().setInputSource(EnumInputSource.E_INPUT_SOURCE_STORAGE);
		
		Intent intent = new Intent();
        intent.setClassName("com.android.settings", "com.android.settings.network.NetworkSettingActivity");
        mFragment.getActivity().startActivity(intent);
        mFragment.getActivity().finish();
	}
}
