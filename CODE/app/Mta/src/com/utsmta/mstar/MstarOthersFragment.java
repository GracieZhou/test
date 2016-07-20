package com.utsmta.mstar;

import java.io.File;
import java.util.ArrayList;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.FrameLayout;
import android.widget.ListView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;

import com.utsmta.app.MtaApplication;
import com.utsmta.app.R;
import com.utsmta.common.FactoryGroup;
import com.utsmta.common.FactoryInspect;
import com.utsmta.common.FactoryItem;
import com.utsmta.common.MtaListAdapter;
import com.utsmta.common.MtaPagerView;
import com.utsmta.common.MtaProgressDialog;
import com.utsmta.common.WizardFragment;
import com.utsmta.utils.LogUtil;

public class MstarOthersFragment extends WizardFragment 
implements OnKeyListener{
	//constants
	private static final String TAG = "MstarOthersFragment";
	
	interface IPolicy{
		/**
		 * 
		 * @param item
		 * @return
		 */
		public MtaPagerView getView(FactoryItem item);
		
		/**
		 * 
		 * @param item
		 * @return
		 */
		public FactoryInspect getInspect(FactoryItem item);
		
		/**
		 * 
		 * @param inspect
		 * @param item
		 */
		public void onBind(FactoryInspect inspect, FactoryItem item);
		
		/**
		 * 
		 * @param item
		 */
		public void onItemSelected(FactoryItem item);
		
		/**
		 * 
		 * @param item
		 */
		public void onItemClicked(FactoryItem item);
	}
	
	//list
	private ArrayList<FactoryItem> manualItemList = new ArrayList<FactoryItem>();
	
	private ArrayList<FactoryItem> autoItemList = new ArrayList<FactoryItem>();
	
	private ArrayList<FactoryItem> specialItemList = new ArrayList<FactoryItem>();
	
	private ArrayList<FactoryItem> pagerItemList = new ArrayList<FactoryItem>();
	
	private ArrayList<FactoryInspect> autoInspects = new ArrayList<FactoryInspect>();
	
	//ui	
	private FrameLayout viewContainer = null;
	
	private ListView manualListView = null;
	
	private ListView autoListView = null;
	
	private ListView specialListView = null;
	
	//adapters
	private MtaListAdapter autoListAdapter = null;
	
	private MtaListAdapter manualListAdapter = null;
	
	private ArrayAdapter<String> specialListAdapter = null;
	
	//
	private int curPos = -1;
	
	private IPolicy policy = null;
	
	private MstarOthersFragment(FactoryGroup factoryGroup) {
		super(factoryGroup);
	}
	
	public MstarOthersFragment(FactoryGroup factoryGroup, IPolicy policy) {
		super(factoryGroup);
		// TODO Auto-generated constructor stub
		this.policy = policy;
	}

	@Override
	protected int uiStyle() {
		// TODO Auto-generated method stub
		return WizardFragment.STYLE_NO_PREV_NOR_NEXT;
	}
	
	@Override
	public View onCreateSubView(LayoutInflater inflater) {
		// TODO Auto-generated method stub
		prevButton.setOnKeyListener(prevButtonOnKeyListener);
		
		long time = System.currentTimeMillis();
		LogUtil.d(TAG, "onCreateSubView time:"+time);
		
		View contentView = inflater.inflate(R.layout.others, null, false);
		
		manualListView = (ListView) contentView.findViewById(R.id.manual_items_lv);
		autoListView = (ListView) contentView.findViewById(R.id.auto_items_lv);
		specialListView = (ListView) contentView.findViewById(R.id.reset_lv);
		viewContainer = (FrameLayout) contentView.findViewById(R.id.viewContainer);

		manualListView.setOnFocusChangeListener(onFocusChangeListener);
		specialListView.setOnFocusChangeListener(onFocusChangeListener);
		
		specialListView.setFocusable(false);
		specialListView.setEnabled(false);
		
		init();

		manualListView.setAdapter(manualListAdapter);
		manualListView.setOnItemClickListener(onItemClickListener);
		manualListView.setOnItemSelectedListener(onItemSelectedListener);
		manualListView.setOnKeyListener(manualListOnKeyListener);
		
		autoListView.setAdapter(autoListAdapter);
		autoListView.setEnabled(false);
		autoListView.setFocusable(false);
	
		specialListView.setEnabled(true);
		specialListView.setFocusable(true);
		specialListView.setAdapter(specialListAdapter);		
		
		specialListView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				// TODO Auto-generated method stub
				policy.onItemClicked(specialItemList.get(position));
			}
		});

		specialListView.setNextFocusUpId(manualListView.getId());
		
//		manualListView.requestFocus();
//		manualListView.setSelection(0);
		LogUtil.d(TAG, "onCreateSubView done:"+(System.currentTimeMillis()-time));
		
		return contentView;
	}
	
	@Override
	public void onDestroyView() {
		// TODO Auto-generated method stub
		super.onDestroyView();
		LogUtil.d(TAG, "onDestroyView");
	}
	
	OnItemSelectedListener specialListOnItemSelectedListener = new OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			// TODO Auto-generated method stub
			if(position == 0){
				onSelected(pagerItemList.size()-specialItemList.size()+position);
			}else if(position == 1){
				onSelected(-1);
			}
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			// TODO Auto-generated method stub
			
		}
	};
	
	View.OnFocusChangeListener onFocusChangeListener = new View.OnFocusChangeListener() {
		
		@Override
		public void onFocusChange(View v, boolean hasFocus) {
			// TODO Auto-generated method stub	
			
			if(hasFocus){
				LogUtil.d(TAG, "onFocusChange");
				if(v instanceof ListView){
					ListView lv = (ListView) v;
					int position = lv.getSelectedItemPosition();
					LogUtil.d(TAG, "getSelectedItemPosition = "+position);
					lv.setSelection(position);
					if(manualListView == v){
						LogUtil.d(TAG, "manualListView onFocusChange");
						onSelected(position);
					}else if(specialListView == v){
						onSelected(pagerItemList.size()-specialItemList.size()+position);
						
						specialListView.setOnItemSelectedListener(specialListOnItemSelectedListener);
						LogUtil.d(TAG, "lastListView onFocusChange");
					}
				}	
			} else {
				if(specialListView == v){
					specialListView.setOnItemSelectedListener(null);
				}
			}
		}
	};
	
	private void init(){		
		manualItemList.clear();
		autoItemList.clear();
		pagerItemList.clear();
		autoInspects.clear();
		
		specialListAdapter = new ArrayAdapter<String>(getActivity(), 
				R.layout.simple_list_item, R.id.item_title);
		
		for(FactoryItem item : getFactoryGroup().getAllItems()){
			String tag  = item.getProperty("tag");	
			if("manual".equals(tag)){		
				manualItemList.add(item);
				onManualItemAdded(item, item.getName());			
				pagerItemList.add(item);
			}else if("auto".equals(tag)){	
				autoItemList.add(item);
				onAutoItemAdded(item, item.getName());
			}else if("special".equals(tag)){
				specialItemList.add(item);
				specialListAdapter.add(item.getProperty("display_name"));
				onSpecialItemAdded(item, item.getName());
				pagerItemList.add(item);
			}
		}
		
		int index = 0;
		for(FactoryItem item : manualItemList){
			item.setIndex(++index);
		}
		
		for(FactoryItem item : autoItemList){
			item.setIndex(++index);
		}
		
		manualListAdapter = new MtaListAdapter(getActivity(), manualItemList);
		autoListAdapter = new MtaListAdapter(getActivity(), autoItemList);
		
		new Thread(new Runnable() {
		
			@Override
			public void run() {
				// TODO Auto-generated method stub
				for(FactoryItem item : pagerItemList){
					MtaPagerView view = (MtaPagerView) item.get("view");
					if(view != null) {
						view.createView();
					}				
				}
			}
		}).start();
	}
	
	protected void onManualItemAdded(FactoryItem item, String name){
		MtaPagerView view = policy.getView(item);
		
		if(view != null){
			item.put("view", view);
//			view.createView();
		}	
	}
	
	protected void onAutoItemAdded(FactoryItem item, String name){
		FactoryInspect inspect = policy.getInspect(item);
		
		if(inspect != null){
			policy.onBind(inspect, item);
			
			if(autoInspects.indexOf(inspect) < 0){
				autoInspects.add(inspect);
			}		
		}		
	}
	
	protected void onSpecialItemAdded(FactoryItem item, String name){
		MtaPagerView view = policy.getView(item);
		
		if(view != null){
			item.put("view", view);
//			view.createView();
		}	
	}
	
	private void onSelected(int position){
		LogUtil.d(TAG, "onSelected position = " + position);
		
		int oldPos = curPos;
		curPos = position;
		
		MtaPagerView prevView = null;
		MtaPagerView curView  = null;
		
		if(oldPos >= 0 && oldPos < pagerItemList.size()){
			prevView =  (MtaPagerView) pagerItemList.get(oldPos).get("view");
		}
		
		if(curPos >= 0 && curPos < pagerItemList.size()){
			curView = (MtaPagerView) pagerItemList.get(curPos).get("view");
		}
	
		if(prevView != null && prevView != curView){			
			prevView.hide(viewContainer);
		}
		
		if(curView != null){
//			boolean show = false;
			
			if(curView != prevView) {
//				show = true;
				curView.show(viewContainer);
			}

			if(position < pagerItemList.size()){
				curView.onItemSelected(pagerItemList.get(position));
			}			
			
//			uiHandler.removeMessages(MSG_SHOW_NEXT_VIEW);
//			Message msg = uiHandler.obtainMessage(MSG_SHOW_NEXT_VIEW);
//			Bundle data = new Bundle();
//			data.putBoolean("show", show);
//			data.putInt("position", position);
//			msg.setData(data);
//			uiHandler.sendMessageDelayed(msg, 600);
		}
	}
	
	private OnItemClickListener onItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// TODO Auto-generated method stub
			FactoryItem item = manualItemList.get(position);
			item.setResult(!item.getResult());
			manualListAdapter.notifyDataSetChanged();
			
			if(item.getResult()){
				int next = 0;
				for(next = position +1; next < manualItemList.size(); ++next){
					FactoryItem _item = manualItemList.get(next);
					if(!_item.getResult()){
						manualListView.setSelection(next);
						break;
					}else{
						continue;
					}
				}
				
				if(next >= manualItemList.size()){
					specialListView.requestFocus();
					specialListView.setSelection(0);
				}			
			}
		}
	};
	
	private OnItemSelectedListener onItemSelectedListener = new OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			// TODO Auto-generated method stub
			LogUtil.d(TAG, "onItemSelected position = "+position);
			onSelected(position);
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			// TODO Auto-generated method stub
			onSelected(-1);
		}
		
	};
	
	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		LogUtil.d(TAG, "onAttach");
	}
	
	@Override
	public void onDetach() {
		// TODO Auto-generated method stub
		super.onDetach();
		LogUtil.d(TAG, "onDetach");
	}
	
	@Override
	public void onHiddenChanged(boolean hidden) {
		// TODO Auto-generated method stub
		super.onHiddenChanged(hidden);
		LogUtil.d(TAG, "onHiddenChanged : " + hidden);
		
		if(!hidden){
			resume();
			
			if(manualListView.isFocused()){
				manualListView.setSelection(manualListView.getSelectedItemPosition());
			}
		}else{
			pause();
		}
	}
	
	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		LogUtil.d(TAG, "onStart");
	}
	
	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		super.onStop();
		LogUtil.d(TAG, "onStop");
	}
	
	protected void resume(){
		refreshUi();
		
		if(curPos >= 0){
			MtaPagerView pagerView = (MtaPagerView) pagerItemList.get(curPos).get("view");
			if(pagerView != null && !pagerView.isShown()){
				pagerView.show(viewContainer);
			}
		}
		
		MtaApplication.getDevice().startInspect();
		
		for(FactoryInspect inspect : autoInspects){
			inspect.startInspect();
		}		
	}
	
	@Override
	public void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		LogUtil.d(TAG, "onResume");
		
		IntentFilter filter = new IntentFilter("mta.update.ui");
		getActivity().registerReceiver(receiver, filter);
		
		resume();
	}
	
	protected void pause(){
		if(curPos >= 0){
			MtaPagerView pagerView = (MtaPagerView) pagerItemList.get(curPos).get("view");
			if(pagerView != null && pagerView.isShown()){
				pagerView.hide(viewContainer);
			}
		}
		
		MtaApplication.getDevice().stopInspect();
		
		for(FactoryInspect inspect : autoInspects){
			inspect.stopInspect();
		}		
	}
	
	@Override
	public void onPause() {
		// TODO Auto-generated method stub
		LogUtil.d(TAG, "onPause");
		
		getActivity().unregisterReceiver(receiver);
		
		pause();
		
		super.onPause();
	}
	
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onActivityCreated(savedInstanceState);
		LogUtil.d(TAG, "onActivityCreated");
	}
	
	private BroadcastReceiver receiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			refreshUi();
		}
	};
	
	private void refreshUi(){
		if(manualListAdapter != null) manualListAdapter.notifyDataSetChanged();
		
		if(autoListAdapter != null) autoListAdapter.notifyDataSetChanged();
	}
	
	private static final int MSG_SHOW_NEXT_VIEW = 0x01;
	
	private Handler uiHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			int what = msg.what;
			
			if(what == MSG_SHOW_NEXT_VIEW){
				Bundle data = msg.getData();
				boolean show = data.getBoolean("show");
				int position = data.getInt("position");
				
				MtaPagerView curView = null;
				
				if(position >= 0 && position < pagerItemList.size()){
					curView = (MtaPagerView) pagerItemList.get(curPos).get("view");
				}
				
				if(curView != null){
					if(show){
						curView.show(viewContainer);
					}
					
					if(position < pagerItemList.size()){
						curView.onItemSelected(pagerItemList.get(position));
					}	
				}
			}
		};
	};
	
	OnKeyListener prevButtonOnKeyListener = new OnKeyListener() {
		
		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			// TODO Auto-generated method stub
			
			if(keyCode == KeyEvent.KEYCODE_DPAD_LEFT && event.getAction() == KeyEvent.ACTION_DOWN){
				if(curPos >=0 && curPos < manualItemList.size()){
					manualListView.requestFocus();
					manualListView.setSelection(curPos);
				}else{
					specialListView.requestFocus();
				}
			}
			
			return false;
		}
	};
	
	@Override
	public boolean onKey(View v, int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(keyCode == KeyEvent.KEYCODE_DPAD_UP || 
				keyCode == KeyEvent.KEYCODE_DPAD_DOWN ||
				keyCode == KeyEvent.KEYCODE_DPAD_LEFT ||
				keyCode == KeyEvent.KEYCODE_DPAD_RIGHT){
			
			if(event.getAction() == KeyEvent.ACTION_DOWN){
				if(keyCode == KeyEvent.KEYCODE_DPAD_DOWN){
					prevButton.requestFocus();
				}else if(keyCode == KeyEvent.KEYCODE_DPAD_LEFT){
					specialListView.requestFocus();
				}			
			}

			return true;
		}
		
		return false;
	}
	
	OnKeyListener manualListOnKeyListener = new OnKeyListener() {
		
		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			// TODO Auto-generated method stub
			if(keyCode == KeyEvent.KEYCODE_DPAD_UP || 
					keyCode == KeyEvent.KEYCODE_DPAD_DOWN){
				
				if(event.getRepeatCount() > 0) return true;
				
				if(event.getAction() == KeyEvent.ACTION_DOWN){
					int position = manualListView.getSelectedItemPosition();
					int size = manualItemList.size();
					
					int next = position;
					if(keyCode == KeyEvent.KEYCODE_DPAD_UP){
						next -= 1;
						if(next >= 0){
							manualListView.setSelection(next);
						}
					}else{
						next += 1;
						if(next < size){
							manualListView.setSelection(next);
						}else{
							specialListView.requestFocus();
							specialListView.setSelection(0);
						}
					}					
				}				
				return true;
			}
			
			return false;
		}
	};
}
