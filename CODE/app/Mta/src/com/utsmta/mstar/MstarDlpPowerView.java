package com.utsmta.mstar;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;

import com.utsmta.app.R;
import com.utsmta.common.MtaPagerView;

public class MstarDlpPowerView extends MtaPagerView {

	public MstarDlpPowerView(Activity activity) {
		super(activity);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected View onCreateView(LayoutInflater inflater) {
		// TODO Auto-generated method stub
		View contentView = inflater.inflate(R.layout.dlp_power, null);
		return contentView;
	}
}
