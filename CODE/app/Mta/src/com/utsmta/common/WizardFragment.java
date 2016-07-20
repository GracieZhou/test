package com.utsmta.common;

import com.utsmta.app.MainActivity;
import com.utsmta.app.MtaApplication;
import com.utsmta.app.R;
import com.utsmta.utils.LogUtil;
import android.widget.RelativeLayout;


import android.app.Activity;
import android.app.FragmentTransaction;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.FrameLayout;

public class WizardFragment extends FactoryFragment {
	private final String TAG = "WizardFragment";
	
	public static final int STYLE_NORMAL = 0x00;
	
	public static final int STYLE_NO_PREV = 0x01;
	
	public static final int STYLE_NO_NEXT= 0x02;
	
	public static final int STYLE_NO_PREV_NOR_NEXT = 0x03;
	
	protected MainActivity parent = null;
	
	protected Button prevButton = null;
	
	protected Button nextButton = null;

	protected RelativeLayout rLyt = null;
	
	private FrameLayout subViewContainer = null;
	
	private View subView = null;
	
	private boolean subViewCreated = false;
	
	public WizardFragment(FactoryGroup factoryGroup) {
		super(factoryGroup);
	}
	

	
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
			Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		parent = (MainActivity) getActivity();
		View contentView = inflater.inflate(R.layout.wizard_fg_base, container, false);

		rLyt = (RelativeLayout)contentView.findViewById(R.id.wizard_bn_lyt);
		prevButton = (Button)contentView.findViewById(R.id.prev_button);
		nextButton = (Button)contentView.findViewById(R.id.next_button);
		
		prevButton.setText(R.string.prev);
		prevButton.setBackgroundResource(R.drawable.small_button_bg);
		prevButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onMoveToPrevMsgReceived();
			}
		});
		
		nextButton.setText(R.string.next);
		nextButton.setBackgroundResource(R.drawable.small_button_bg);
		nextButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				onMoveToNextMsgReceived();
			}
		});
		
		subViewContainer = (FrameLayout) contentView.findViewById(R.id.subview_container);
		createSubView();
		if(subView != null){
			subViewContainer.addView(subView, new FrameLayout.LayoutParams(
					FrameLayout.LayoutParams.MATCH_PARENT, 
					FrameLayout.LayoutParams.MATCH_PARENT));			
		}
		
		int style = uiStyle();
		if((style & STYLE_NO_PREV) == STYLE_NO_PREV){
			prevButton.setVisibility(View.GONE);
		}
		
		if((style & STYLE_NO_NEXT) == STYLE_NO_NEXT){
			nextButton.setVisibility(View.GONE);
		}

		if((style & STYLE_NO_PREV_NOR_NEXT) == STYLE_NO_PREV_NOR_NEXT){
			LogUtil.d(TAG,"style ");
			rLyt.setVisibility(View.GONE);
		}
		
		if(MtaApplication.getConfigManager().getFactoryGroupIndex(getFactoryGroup()) == 0){
			prevButton.setVisibility(View.GONE);
		}
		
		return contentView;
	}
	
	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		subView = null;
		subViewCreated = false;
		super.onDestroyView();
	}
	
	public void createSubView(){
		if(!subViewCreated){
			subView = onCreateSubView(LayoutInflater.from(parent));
			subViewCreated = true;
		}	
	}
	
	protected View onCreateSubView(LayoutInflater inflater) {
		// TODO Auto-generated method stub
		return null;
	}
	
	protected int uiStyle(){
		return STYLE_NORMAL;
	}
	
	protected void onMoveToPrevMsgReceived(){
		moveToPrevFragment();
	}
	
	protected void onMoveToNextMsgReceived(){
		moveToNextFragment();
	}
	
	protected void moveToPrevFragment(){
		parent.showPrevFragment();
	}
	
	protected void moveToNextFragment(){
		parent.showNextFragment();
	}
}
