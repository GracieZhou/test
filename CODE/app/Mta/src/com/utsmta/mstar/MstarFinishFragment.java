package com.utsmta.mstar;

import java.util.ArrayList;

import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.GridView;

import com.utsmta.app.MtaApplication;
import com.utsmta.app.R;
import com.utsmta.common.FactoryGroup;
import com.utsmta.common.FactoryInspect;
import com.utsmta.common.FactoryItem;
import com.utsmta.common.MtaListAdapter;
import com.utsmta.common.WizardFragment;

public class MstarFinishFragment extends WizardFragment {
	private Button resetBtn = null;
	
	private GridView gridView = null;
	
	private GridViewAdapter adapter = null;
	
	private ArrayList<FactoryItem> items = new ArrayList<FactoryItem>();
	
	public MstarFinishFragment(FactoryGroup factoryGroup) {
		super(factoryGroup);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected int uiStyle() {
		// TODO Auto-generated method stub
		return WizardFragment.STYLE_NO_NEXT;
	}
	
	private OnClickListener onClickListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			
		}
	};
	
	private void initGridViewData(Context context){
		items.clear();
		
		for(FactoryGroup group:MtaApplication.getConfigManager().getAllGroups()){
			items.addAll(group.getAllItems());
		}
		
		adapter = new GridViewAdapter(context, items);
	}
	
	@Override
	public View onCreateSubView(LayoutInflater inflater) {
		// TODO Auto-generated method stub
		View contentView = inflater.inflate(R.layout.finish, null, false);
		
		resetBtn = (Button) contentView.findViewById(R.id.resetBtn);
		gridView = (GridView) contentView.findViewById(R.id.gridView);
		
		resetBtn.setOnClickListener(onClickListener);
		
		initGridViewData(getActivity());
		gridView.setFocusable(false);
		gridView.setClickable(false);
		gridView.setEnabled(false);
		gridView.setAdapter(adapter);
		
		return contentView;
	}
	
	public class GridViewAdapter extends MtaListAdapter{

		public GridViewAdapter(Context context, ArrayList<FactoryItem> items) {
			super(context, items);
			// TODO Auto-generated constructor stub
		}
		
		@Override
		protected int layoutResId() {
			// TODO Auto-generated method stub
			return R.layout.grid_item;
		}
	}
}
