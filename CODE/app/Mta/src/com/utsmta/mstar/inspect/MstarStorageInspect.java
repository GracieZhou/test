package com.utsmta.mstar.inspect;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import android.content.Context;
import android.hardware.usb.UsbManager;
import android.hardware.usb.UsbDevice;
import android.os.Bundle;
import android.os.Environment;
import android.os.Message;

import com.utsmta.common.FactoryInspect;
import com.utsmta.utils.LogUtil;
import scifly.storage.StorageManagerExtra;
import android.text.TextUtils;
import com.utsmta.app.MtaApplication;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import com.utsmta.common.DeviceManager;





public class MstarStorageInspect extends FactoryInspect {
	private static final String TAG = "MstarStorageInspect";
	
	private Context context;
	
	private UsbManager usbManager = null;

	private final static int USB3_0_BUS_ID  = 7000;
	private final static int USB3_0_PORT_USB2_FLAG  = 6001;
	private final static int USB3_0_PORT_USB3_FLAG  = 7001;
	private static final String USB3_0_NAMEUSB2_DEVICE_PATH = "/sys/devices/Mstar-xhci-1.14/usb7/7-1";
	private static final String USB3_0_NAMEUSB3_DEVICE_PATH = "/sys/devices/Mstar-xhci-1.14/usb7/7-2";
		
	@Override
	protected void handleBackgroundHandlerMessage(Message msg) {
		// TODO Auto-generated method stub
		getMountedStorageDevices();
		backgroundHandler.sendEmptyMessageDelayed(0, 1500);
	}
	
	protected MstarStorageInspect(){
		
	}
	
	public MstarStorageInspect(Context context){
		this.context = context;
		this.usbManager = (UsbManager) this.context.getSystemService(Context.USB_SERVICE);
	}
	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		LogUtil.d(TAG, "onStart");	
		getMountedStorageDevices();
		backgroundHandler.sendEmptyMessage(0);
	}
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		LogUtil.d(TAG, "onStop");
		backgroundHandler.removeMessages(0);
		super.onStop();
	}

	private void getMountedStorageDevices(){
		LogUtil.d(TAG, "getMountedStorageDevices");
		HashMap<String, UsbDevice> usbDeviceMap = usbManager.getDeviceList();
		Iterator<Entry<String, UsbDevice>> iterator = usbDeviceMap.entrySet().iterator();
		while(iterator.hasNext()){
			Entry<String, UsbDevice> entry = iterator.next();
			UsbDevice usbDevice = entry.getValue();
			LogUtil.d(TAG, "deviceId:"+usbDevice.getDeviceId());
			Bundle extra = new Bundle();
			extra.putInt("type", 0);

			if(MtaApplication.getDevice().getDeviceName().equals(DeviceManager.DEVICE_H828)
				&& usbDevice.getDeviceId() >= USB3_0_BUS_ID){
				if(USB3_0_NAMEUSB2_DEVICE_PATH.equals(getPathInSysfsOfUsbDevice(usbDevice))){
				   extra.putInt("deviceId", USB3_0_PORT_USB2_FLAG);
				}else if(USB3_0_NAMEUSB3_DEVICE_PATH.equals(getPathInSysfsOfUsbDevice(usbDevice))){
					extra.putInt("deviceId", USB3_0_PORT_USB3_FLAG);
				}else{
					LogUtil.d(TAG, "un known USB  ");
				}									
			}else{
				extra.putInt("deviceId", usbDevice.getDeviceId());
			}			
			updateInspectResult(true, 0, extra);
		}
		
		if(Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)){
			Bundle extra = new Bundle();
			extra.putInt("type", 1);
//			extra.putInt("deviceId", 1);
			if(checkExternalSDcardState()){
			   extra.putBoolean("bexternal_sd", true); 			
			}
			updateInspectResult(true, 0, extra);
		}		
		
	}		

	private boolean checkExternalSDcardState(){
		 StorageManagerExtra storageManager = StorageManagerExtra.getInstance(context);
         String external_sd = storageManager.getExternalSdcardPath();
		 if(TextUtils.isEmpty(external_sd)){
		 	return false;
		 }else{
		 	return true;
		 }		 		 					 
	}

	private String getPathInSysfsOfUsbDevice(UsbDevice usbDevice) {
      	String usbDeviceName = "DEVNAME="+usbDevice.getDeviceName().substring("/dev/".length());
	 
		
         LogUtil.d(TAG, "ashton +++ deviceId:"+usbDevice.getDeviceName().toString());
		File usbDevicesDir = new File("/sys/bus/usb/devices");
		File[] tempList = usbDevicesDir.listFiles();
		for (int i = 0;i < tempList.length; ++i) {
			try {
				String canonicalPath = tempList[i].getCanonicalPath();
				
				File ueventFile = new File(canonicalPath+"/uevent");
				BufferedReader reader = new BufferedReader(new FileReader(ueventFile));
				String tempString = reader.readLine();
				tempString = reader.readLine();
				tempString = reader.readLine();
				
				reader.close();
				if (usbDeviceName.equals(tempString)) {
					return canonicalPath;
				}
			} catch (Exception e) {
				e.printStackTrace();
			}
		}
		return null;
	}
				
}
