package com.utsmta.mstar;

import android.app.Activity;

import com.utsmta.app.R;
import com.utsmta.common.FactoryItem;
import com.utsmta.common.MtaSoundPlayer;
import com.utsmta.common.SimpleItemPagerView;
import com.utsmta.utils.LogUtil;

public class MstarLineOutView extends SimpleItemPagerView {
	private final String TAG = "MstarLineOutView";
	
	private MtaSoundPlayer soundPlayer = null;
	
	public MstarLineOutView(Activity activity, FactoryItem item) {
		super(activity, item);
		// TODO Auto-generated constructor stub
		soundPlayer = new MtaSoundPlayer(activity, R.raw.mstarlogo);
	}

	@Override
	protected void onShown() {
		// TODO Auto-generated method stub
		LogUtil.d(TAG, "onShown");
		
		soundPlayer.play();		
	}
	
	@Override
	protected void onHiden() {
		// TODO Auto-generated method stub
		LogUtil.d(TAG, "onHiden");
		
		soundPlayer.stop();
	}
	
}
