package com.utsmta.common;

import java.util.ArrayList;
import java.util.HashMap;

import com.utsmta.app.R;
import com.utsmta.common.FactoryInspect.InspectResultListener;
import com.utsmta.common.inspect.BluetoothInspect;

import android.app.Activity;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.ArrayAdapter;
import android.widget.ListView;

public class BluetoothView extends  ItemPagerView{

	public BluetoothView(Activity activity, FactoryItem item, BluetoothInspect inspect) {
		super(activity, item);
		// TODO Auto-generated constructor stub
		this.inspect = inspect;
	}

	private ListView bluetoothLv = null;
	
	private BluetoothInspect inspect = null;
	
	private ArrayAdapter<String> listAdapter = null;
	
	private boolean firstTime = true;
	
	private InspectResultListener bluetoothListener = new InspectResultListener() {
		
		@Override
		public void onResultUpdate(boolean passed, int error, Bundle extra) {
			// TODO Auto-generated method stub
			if(!passed) {
				return ;
			}else{
				if(firstTime){
					item.setResult(true);
					notifyUiUpdate();
					firstTime = false;					
				}
				
				updateListView(inspect.getDeviceList());
			}
		}
	};
	
	private void updateListView(ArrayList<HashMap<String, String>> deviceList){
		listAdapter.clear();
		for(HashMap<String, String> map:deviceList){
			listAdapter.add(map.get("name")+"   "+map.get("addr"));
			listAdapter.notifyDataSetChanged();
		}
	}
	
	@Override
	protected View onCreateView(LayoutInflater inflater) {
		// TODO Auto-generated method stub
		bluetoothLv = new ListView(activity);
		bluetoothLv.setLayoutParams(new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		
		// listAdapter = new ArrayAdapter<String>(getActivity(), android.R.layout.simple_expandable_list_item_1);
		listAdapter = new ArrayAdapter<String>(activity,R.layout.wifi_adapter, R.id.text_ex);
		bluetoothLv.setAdapter(listAdapter);
		
		updateListView(inspect.getDeviceList());
		
		return bluetoothLv;
	}
	
	@Override
	protected void onShown() {
		// TODO Auto-generated method stub
		updateListView(inspect.getDeviceList());
		inspect.registerResultListener(bluetoothListener);
		super.onShown();
	}
	
	@Override
	protected void onHiden() {
		// TODO Auto-generated method stub
		inspect.unregisterResultListener(bluetoothListener);
		super.onHiden();
	}
}
