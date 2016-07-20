package com.utsmta.common;

import android.app.Activity;
import android.app.Fragment;

public class FactoryFragment extends Fragment {
	private FactoryGroup factoryGroup = null;

	public FactoryFragment(FactoryGroup factoryGroup){
		this.factoryGroup = factoryGroup;
	}
	
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
	}
	
	protected FactoryGroup getFactoryGroup(){
		return this.factoryGroup;
	}
}
