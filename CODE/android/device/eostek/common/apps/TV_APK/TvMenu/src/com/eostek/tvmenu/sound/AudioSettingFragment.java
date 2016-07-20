package com.eostek.tvmenu.sound;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.eostek.tvmenu.R;


public class AudioSettingFragment extends Fragment {

	public AudioSettingHolder mHolder;

	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		View view = inflater.inflate(R.layout.audio_setting_fragment, null);

		mHolder = new AudioSettingHolder(this);
		mHolder.initView(view);
		mHolder.setListener();
		return view;
	}

	public void onStart() {
		super.onStart();
		mHolder.initData();
	}

}
