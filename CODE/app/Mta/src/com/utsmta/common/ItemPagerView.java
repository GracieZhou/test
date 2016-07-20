package com.utsmta.common;

import android.app.Activity;

import com.utsmta.common.FactoryItem;
import com.utsmta.common.MtaPagerView;

public class ItemPagerView extends MtaPagerView{
	protected FactoryItem item = null;
		
	public ItemPagerView(Activity activity, FactoryItem item){
		super(activity);	
		this.item = item;
	}
}
