package com.utsmta.common.inspect;

import java.util.ArrayList;
import java.util.HashMap;

import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Message;

import com.utsmta.common.FactoryInspect;
import com.utsmta.utils.LogUtil;

public class BluetoothInspect extends FactoryInspect {
	private final String TAG = "BluetoothInspect";
	
	private BluetoothAdapter bluetoothAdapter = null;
	
	private Context context = null;
	
	private boolean isEnabled = false;
	
	private boolean isFirstChecked = true;
	
	private boolean amta = false;
	
	private ArrayList<HashMap<String, String>> bluetoothList = new ArrayList<HashMap<String, String>>();
	
	private BroadcastReceiver receiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			LogUtil.d(TAG, "BroadcastReceiver onReceive");
			String action = intent.getAction();
			
			if(BluetoothDevice.ACTION_FOUND.equals(action) || 
					BluetoothDevice.ACTION_NAME_CHANGED.equals(action)){
				BluetoothDevice device = intent.getParcelableExtra(BluetoothDevice.EXTRA_DEVICE);
				addDevice(device);
			}
		}
	};
	
	private BluetoothAdapter.LeScanCallback leScanCallback = new BluetoothAdapter.LeScanCallback() {
		
		@Override
		public void onLeScan(BluetoothDevice device, int rssi, byte[] scanRecord) {
			// TODO Auto-generated method stub
			addDevice(device);
		}
	};
	
	private void addDevice(BluetoothDevice device){
		LogUtil.d(TAG, "addDevice name = "+device.getName()+" address = "+device.getAddress());
		boolean found = false;
		boolean changed = false;
		
		String addr = device.getAddress();
		String name = device.getName();
		
		for(HashMap<String, String> map: bluetoothList){
			if(map.get("addr").equals(addr)){
				found = true;
				String _name = map.get("name");
				if((_name == null && name != null) || (_name != null && !_name.equals(name))){
					changed = true;
					map.put("name", name);
				}
				break;
			}
		}
		
		if(!found){
			HashMap<String, String> map = new HashMap<String, String>();
			map.put("addr", addr);
			map.put("name", name);
			bluetoothList.add(map);
		}
		
		updateInspectResult(true, 0, null);		
	}
	
	@Override
	protected void handleBackgroundHandlerMessage(Message msg) {
		// TODO Auto-generated method stub
		if(msg.what == 0){
			if(bluetoothAdapter == null){
				LogUtil.d(TAG, "bluetoothAdapter is null");
				bluetoothAdapter = BluetoothAdapter.getDefaultAdapter();
				if(bluetoothAdapter != null){
					for(BluetoothDevice device:bluetoothAdapter.getBondedDevices()){
						LogUtil.d(TAG, "getBondedDevices device name = "+device.getName());
						addDevice(device);
					}
				}
			}

			if(bluetoothAdapter != null){
				if(bluetoothAdapter.isEnabled()) {	
					if(!amta){
						updateInspectResult(true, 0, null);
					}else{
						if(isEnabled == false || isFirstChecked){
							LogUtil.d(TAG, "bluetoothAdapter is Enabled");
							forceSearch();
							bluetoothAdapter.stopLeScan(leScanCallback);
							bluetoothAdapter.startLeScan(leScanCallback);
							isEnabled = true;
							isFirstChecked = false;
						}	
							
					}
					
					if(!bluetoothAdapter.isDiscovering()) bluetoothAdapter.startDiscovery();
				}else{
					backgroundHandler.post(new Runnable() {
						
						@Override
						public void run() {
							// TODO Auto-generated method stub
							LogUtil.d(TAG, "OnRun");
							bluetoothAdapter.enable();
						}
					});
				}
			}	
			
			backgroundHandler.sendEmptyMessageDelayed(0, 1000);
		}
	}
	
	public BluetoothInspect(Context context){
		this(context, false);
	}
	
	public BluetoothInspect(Context context, boolean amta){
		this.context = context;
		this.amta = amta;
	}
	
	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		LogUtil.d(TAG, "startInspect");		
		IntentFilter filter = new IntentFilter();
		filter.addAction(BluetoothDevice.ACTION_FOUND);
		filter.addAction(BluetoothDevice.ACTION_NAME_CHANGED);
		filter.addAction(BluetoothDevice.ACTION_BOND_STATE_CHANGED);
		filter.setPriority(Integer.MAX_VALUE);
		context.registerReceiver(receiver, filter);		
		backgroundHandler.sendEmptyMessage(0);
	}

	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		LogUtil.d(TAG, "stopInspect");
		backgroundHandler.removeMessages(0);
		if(bluetoothAdapter != null){
			context.unregisterReceiver(receiver);
			if(bluetoothAdapter.isDiscovering()) bluetoothAdapter.cancelDiscovery();
			
			bluetoothAdapter.stopLeScan(leScanCallback);
			bluetoothAdapter.disable();
			isEnabled =  false;
		}
	}
	
	public void forceSearch(){
		if(bluetoothAdapter != null){
			if(bluetoothAdapter.isDiscovering()) bluetoothAdapter.cancelDiscovery();
			bluetoothAdapter.startDiscovery();
		}
	}

	public ArrayList<HashMap<String, String>> getDeviceList(){
		return bluetoothList;
	}
}
