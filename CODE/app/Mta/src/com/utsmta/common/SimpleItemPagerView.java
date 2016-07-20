package com.utsmta.common;

import com.utsmta.app.R;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

public class SimpleItemPagerView extends ItemPagerView {
	public SimpleItemPagerView(Activity activity, FactoryItem item) {
		super(activity, item);
		// TODO Auto-generated constructor stub
	}

	private TextView operateTipView = null;
	

	@Override
	protected View onCreateView(LayoutInflater inflater) {
		// TODO Auto-generated method stub
		View contentView = inflater.inflate(R.layout.simple_pager_view, null, true);
		
		operateTipView = (TextView) contentView.findViewById(R.id.operate_tip);
		setOperateTip(item.getProperty("operate_tip"));
		
		return contentView;
	}
	
	protected void setOperateTip(String operateTip){
		operateTipView.setText(operateTip);
	}
}
