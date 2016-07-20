package com.utsmta.mstar;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.SystemProperties;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.app.Activity;
import android.app.DialogFragment;

import com.mstar.android.tvapi.common.TvManager;
import com.mstar.android.tvapi.common.exception.TvCommonException;
import com.mstar.android.tvapi.common.vo.MuteType.EnumMuteType;
import com.utsmta.app.MtaApplication;
import com.utsmta.app.R;
import com.utsmta.app.MainActivity;
import com.utsmta.common.BluetoothView;
import com.utsmta.common.ConfigManager;
import com.utsmta.common.DeviceManager;
import com.utsmta.common.FactoryFragment;
import com.utsmta.common.FactoryDevice;
import com.utsmta.common.FactoryGroup;
import com.utsmta.common.FactoryInspect;
import com.utsmta.common.FactoryItem;
import com.utsmta.common.IToolKit;
import com.utsmta.common.ItemInspectListener;
import com.utsmta.common.MtaMessageDialog;
import com.utsmta.common.MtaPagerView;
import com.utsmta.common.MtaProgressDialog;
import com.utsmta.common.WifiInspectListener;
import com.utsmta.common.inspect.BluetoothInspect;
import com.utsmta.common.inspect.WifiInspect;
import com.utsmta.mstar.amta.MstarAmtaFanSpeedView;
import com.utsmta.mstar.inspect.MstarEthInspect;
import com.utsmta.mstar.inspect.MstarGravitySensorInspect;
import com.utsmta.mstar.inspect.MstarNtcSensorInspect;
import com.utsmta.mstar.inspect.MstarStorageInspect;
import com.utsmta.utils.LogUtil;
import com.utsmta.utils.MtaUtils;
import com.utsmta.utils.QRCodeUtil;

public class MstarMtaDevice extends FactoryDevice {
	protected static final String TAG = "MstarMtaDevice";
	
	protected static final String AUTO_START_FLAG = "/data/app/mta.properties";
	
	protected static final String PROPERTY_PVT_VERSION = "ro.scifly.version";
	
	protected static final String PROPERTY_SW_M_NUM = "ro.scifly.version.mod";
	
	protected static final String SN = "sn";
	
	protected static final String VIDEO = "video";
	
	protected static final String OTHERS = "others";
	
	protected static final String FINISH = "finish";
	
	protected MstarToolKit toolKit = new MstarToolKit();
	
	protected MstarOthersFragment.IPolicy policy = null;
	
	private WifiInspect wifiInspect = null;
	
	private BluetoothInspect bluetoothInspect = null;
	
	private MstarStorageInspect storageInspect = null;
	
	private MstarVideoView videoSubView = null;
	
	private MstarBlankView blankSubView = null;
	
	private Activity activity = null;
	
	private MstarOthersFragment othersFragment = null;
	
	@Override
	protected FactoryFragment createFactoryFragment(FactoryGroup group) {
		// TODO Auto-generated method stub
		FactoryFragment fragment = null;
		
		String name = group.getName();
		
		if(SN.equals(name)){
			fragment = new MstarSnFragment(group);	
		}else if(VIDEO.equals(name)){
//			fragment = new MstarVideoFragment(group);
		}else if(OTHERS.equals(name)){
			if(othersFragment == null){
				othersFragment = new MstarOthersFragment(group, policy);
			}
			
			fragment = othersFragment;
		}else if(FINISH.equals(name)){
//			fragment = new MstarFinishFragment(group);
		}
		
		return fragment;
	}

	@Override
	public IToolKit getToolKit() {
		// TODO Auto-generated method stub
		return toolKit;
	}

	protected class MyPolicy implements MstarOthersFragment.IPolicy{

		@Override
		public MtaPagerView getView(FactoryItem item) {
			// TODO Auto-generated method stub			
			String name = item.getName();
			
			MtaPagerView view = null;
			if(FactoryItem.WIFI.equals(name)){
				if(wifiInspect == null){
					wifiInspect = new WifiInspect(activity);
				}
				
				wifiInspect.registerResultListener(new WifiInspectListener(activity, item));
				view = new MstarWifiView(activity, item, wifiInspect);
			}else if(FactoryItem.BLUETOOTH.equals(name)){
				if(bluetoothInspect == null){
					bluetoothInspect = new BluetoothInspect(activity, amta);
				}
				
				bluetoothInspect.registerResultListener(new ItemInspectListener(activity, item));
				view = new BluetoothView(activity, item, bluetoothInspect);
			}else if(FactoryItem.TOUCHPAD.equals(name)){
				view = new MstarTouchPadView(activity, item);
			}else if(FactoryItem.DLP_POWER.equals(name)){
				view = new MstarDlpPowerView(activity);
			}else if(FactoryItem.BATTERY_POWER.equals(name)){
				view = new MstarBatteryPowerView(activity);
			}else if(FactoryItem.FAN_POWER.equals(name)){
				view = new MstarFanPowerView(activity);
			}else if(FactoryItem.FAN_SPEED.equals(name)){
				if(amta && DeviceManager.DEVICE_BENQI300.equals(deviceName)){
					view = new MstarAmtaFanSpeedView(activity, item);
				}else{
					view = new MstarFanSpeedView(activity);
				}
				
			}else if(FactoryItem.AMTA_NTC_TEMP.equals(name)){
				view = new MstarNtcTemperatureView(activity);	
			}else if(FactoryItem.VIDEO.equals(item.getProperty("sub_group"))){
				if(videoSubView == null){
					videoSubView = new MstarVideoView(activity);
				}
				
				view = videoSubView;
			}else if(FactoryItem.ADC_ADJUST.equals(name)){
				view = new MstarAutoAdcAdjustView(activity);
			}else if(FactoryItem.AMTA_USB1.equals(name)
					||FactoryItem.AMTA_USB2.equals(name) ){									    				      
				    view = new MstarUsbMediaView(activity,item, name);				
			}else if(FactoryItem.LOUDSPEAKER.equals(name)){
				view = new MstarLoudSpeakerView(activity, item);
			}else if(FactoryItem.HEADSET.equals(name)){
				view = new MstarHeadSetView(activity, item);
			}else if(FactoryItem.LINE_OUT.equals(name)){
				view = new MstarLineOutView(activity, item);
			}else if(FactoryItem.SPDIF.equals(name)){
				view = new MstarSpdifView(activity, item);
			}else if("amta_hdcp_key".equals(name)){
				view = new MstarAmtaHdcpView(activity, item);
			}else if("test_pattern".equals(name)){
				view = new MstarDlpGraphicPatternView(activity);
			}else if("white_balance".equals(name)){
				view = new MstarWhiteBalanceView(activity);
			}else if("recheck".equals(name)){
				view = new MstarRecheckView(activity, othersFragment);
			}else if("3d".equals(name)){
				view = new Mstar3DView(activity);
			}
			else {
				if(blankSubView == null){
					blankSubView = new MstarBlankView(activity);
				}
				
				view = blankSubView;
			}
			
			return view;
		}

		@Override
		public FactoryInspect getInspect(FactoryItem item) {
			// TODO Auto-generated method stub
			String name = item.getName();
			
			FactoryInspect inspect = null;
			
			if(FactoryItem.ETH.equals(name)){
				inspect = new MstarEthInspect(activity);
				inspect.registerResultListener(new ItemInspectListener(activity, item));				
			}else if(FactoryItem.GRAVITY_SENSOR.equals(name)){
				inspect = new MstarGravitySensorInspect();
				inspect.registerResultListener(new ItemInspectListener(activity, item));
			}else if(FactoryItem.NTC_SENSOR.equals(name)){
				inspect = new MstarNtcSensorInspect();
				inspect.registerResultListener(new ItemInspectListener(activity, item));
			}else if("storage".equals(item.getProperty("sub_group"))){
				LogUtil.d(TAG, "find storage item");
				if(storageInspect == null){
					storageInspect = new MstarStorageInspect(activity);			
				}	
				
				inspect = storageInspect;
			}else if(FactoryItem.BLUETOOTH.equals(name)){
				if(bluetoothInspect == null){
					bluetoothInspect = new BluetoothInspect(activity, amta);
				}
				
				bluetoothInspect.registerResultListener(new ItemInspectListener(activity, item));
			}else if(FactoryItem.WIFI.equals(name)){
				if(wifiInspect == null){
					wifiInspect = new WifiInspect(activity);
				}
				
				wifiInspect.registerResultListener(new ItemInspectListener(activity, item));
			}
			
			return inspect;
		}

		@Override
		public void onBind(FactoryInspect inspect, FactoryItem item) {
			// TODO Auto-generated method stub
			if("storage".equals(item.getProperty("sub_group"))){
				inspect.registerResultListener(new MstarStorageInspectListener(activity, item));
			}
		}

		@Override
		public void onItemClicked(FactoryItem item) {
			// TODO Auto-generated method stub
			if("reset".equals(item.getName())){
				handleResetCommand();
			}
		}

		@Override
		public void onItemSelected(FactoryItem item) {
			// TODO Auto-generated method stub
			
		}
		
	}
	
	@Override
	public void onSystemBooted(Context context) {
		// TODO Auto-generated method stub
		new Thread(new StartupRunnable(context)).start();
	}

	protected class StartupRunnable implements Runnable{
		private Context context = null;
		
		public StartupRunnable(Context context){
			this.context = context;
		}

		@Override
		public void run() {
			// TODO Auto-generated method stub
			File file = new File(AUTO_START_FLAG);
			if(file.exists()){
				startMainActivity(this.context);
				return ;
			}
			
			int times = 0;
			
			
			while (++times < 120) {
				LogUtil.d(TAG, "times = "+times);

				if(foundAutoTag()){
					try {
						Thread.sleep(4500);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					
					startMainActivity(this.context);
					
					return;
				}else{
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}			

			}
			
		}
	}
	
	protected boolean foundAutoTag(){
		boolean found = false;
		
		ArrayList<String> usbPathList = MtaUtils.getMountedUsbDevices("/mnt/usb/");
		
		for(String path : usbPathList){
			File _file = new File(path+"/"+"eostek.mta.auto");
			if(_file.exists()){
				found = true;
				break;
			}
		}
		
		return found;
	}
	private void startMainActivity(Context context){
		Intent intent = new Intent(context, MainActivity.class);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
		context.startActivity(intent);
	}
	
	@Override
	public boolean startInspect() {
		// TODO Auto-generated method stub
		if(wifiInspect != null){
			wifiInspect.startInspect();
		}
		
		if(bluetoothInspect != null){
			bluetoothInspect.startInspect();
		}
		
		return false;
	}

	@Override
	public void stopInspect() {
		// TODO Auto-generated method stub
		if(wifiInspect != null){
			wifiInspect.stopInspect();
		}
		
		if(bluetoothInspect != null){
			bluetoothInspect.stopInspect();
		}
	}

	@Override
	protected void onCreate() {
		// TODO Auto-generated method stub
		policy = new MyPolicy();
	}

	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		
	}

	@Override
	protected boolean onFactoryGroupAdd(FactoryGroup group){
		if(SN.equals(group.getName())){
			if(amta) return false;
		}
		
		return super.onFactoryGroupAdd(group);
	}
	
	@Override
	protected boolean onFactoryItemAdd(FactoryItem item, FactoryGroup group) {
		// TODO Auto-generated method stub		
		if("reset".equals(item.getName())){
			if(amta){
				item.setProperty("display_name", MtaApplication.getContext().getString(R.string.test_result));
			}
		}
		
		return super.onFactoryItemAdd(item, group);
	}

	@Override
	public void onActivityCreate(Activity activity) {
		// TODO Auto-generated method stub
		this.activity = activity;
		
		MainActivity mainActivity = (MainActivity) activity;
        try {
        	String hdcp_1_4 = TvManager.getInstance().getEnvironment("HKEY");
        	if(hdcp_1_4 != null && hdcp_1_4.length() >= 289){
        		mainActivity.setHdcpFlag(true);
        	}else{
			    mainActivity.setHdcpFlag(false);	
			}
        	
			String hdcp_2_2 = TvManager.getInstance().getEnvironment("H2KEY");
        	if(hdcp_2_2 != null && hdcp_2_2.length() >= 1044){
        		mainActivity.setHdcp_2_2_Flag(true);
        	}else{
				mainActivity.setHdcp_2_2_Flag(false);
			}        		
			
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        
            if("BenQi300".equals(deviceName)
				|| "BenQi500".equals(deviceName)){
        	mainActivity.setSystemVersionDisplayValue(mainActivity.getSystemVersion()+"  "+
        			SystemProperties.get(PROPERTY_PVT_VERSION, "")+"-"+
        			SystemProperties.get(PROPERTY_SW_M_NUM, ""));
        }   
	}

	@Override
	public boolean restoreSystem(Context context) {
		// TODO Auto-generated method stub
		boolean bRet = true;
		
		if(!MstarRestoreKit.cleanWifiConfig(activity)
				|| !MstarRestoreKit.resetDB(MstarRestoreKit.TV_DB_BACKUP_TV_DIR, MstarRestoreKit.TV_DB_FILE_USER_SETTING)
				|| !MstarRestoreKit.resetDB(MstarRestoreKit.TV_DB_BACKUP_TV_DIR, MstarRestoreKit.TV_DB_FILE_USER_SETTING_JOURNAL)
				|| !MstarRestoreKit.resetDB(MstarRestoreKit.TV_DB_BACKUP_FACTORY_DIR, MstarRestoreKit.TV_DB_FILE_FACTORY)
				|| !MstarRestoreKit.resetDB(MstarRestoreKit.TV_DB_BACKUP_FACTORY_DIR, MstarRestoreKit.TV_DB_FILE_FACTORY_JOURNAL)){
			LogUtil.d(TAG, "reset tv false");
			bRet = false;
		}
		
		if(bRet && FactoryDevice.DEVICE_BRANCH_DLP.equals(this.deviceBranch)){
			if(!MstarRestoreKit.resetDB(MstarRestoreKit.DLP_DB_BACKUP_DIR, MstarRestoreKit.DLP_DB_FILE_DLP)
					|| !MstarRestoreKit.resetDB(MstarRestoreKit.DLP_DB_BACKUP_DIR, MstarRestoreKit.DLP_DB_FILE_DLP_JOURNAL)){
				LogUtil.d(TAG, "reset dlp false");
				bRet = false;
			}
		}
		
		MstarRestoreKit.sync();
		
		if(bRet){
			MstarRestoreKit.resetVolume(context, 50);
		}

		return bRet;
	}

	@Override
	public boolean shutdownSystem(Context context) {
		// TODO Auto-generated method stub
		try {
			TvManager.getInstance().getAudioManager().enableMute(EnumMuteType. E_MUTE_ALL );
			TvManager.getInstance().getPictureManager().disableBacklight();
			TvManager.getInstance().setGpioDeviceStatus(36, false);
			TvManager.getInstance().setGpioDeviceStatus(38, true);
			TvManager.getInstance().enterSleepMode(true, false);
		} catch (Throwable e) {
			e.printStackTrace();
		}  	
		
		return false;
	}
	
	protected void handleResetCommand(){
		if(!amta){
			reset();
		}else{
			FinishDialog finishDialog = new FinishDialog(this);
			finishDialog.show(activity.getFragmentManager(), null);
		}
	}
	
	protected void reset(){
		MtaProgressDialog.show(activity, null, activity.getString(R.string.restoring_mta));
		
		boolean bRet = this.restoreSystem(activity);
		
		MtaProgressDialog.dismiss();
		
		if(bRet){
			File file = new File(AUTO_START_FLAG);
			if(file.exists()){
				file.delete();
			}
			
			MtaProgressDialog.show(activity, null, activity.getString(R.string.shutdowning));
			
			try {
				Thread.sleep(2000);
			} catch (InterruptedException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			bRet = shutdownSystem(activity);
			
			MtaProgressDialog.dismiss();
		}else{
			MtaMessageDialog messageDialog = new MtaMessageDialog(activity.getString(R.string.reset_result_ng), 
					MtaMessageDialog.STYLE_NORMAL, 
					activity.getString(R.string.confirm),
					activity.getString(R.string.cancel));
			
			messageDialog.show(activity.getFragmentManager(), null);		
		}
	}
	
	//amta
    protected String packAmtaResult() {
    	HashMap<String, String> maps = new HashMap<String, String>(){
    		{
    			put("loudspeaker", "1");
    			put("hdmi1", "2");
    			put("microphone_in", "3");
    			put("test_pattern", "4");
    			put("amta_hdcp_key", "5");
    			put("line_out", "6");
    			put("spdif", "7");
    			put("wifi", "8");
    			put("gravity_sensor", "9");
    			put("ntc_temperature", "10");
    			put("usb1", "11");
    			put("usb2", "12");
    			put("eth", "13");
    			put("fan_speed", "14");
    			put("bluetooth", "15");
    			put("3d", "16");
    			put("reserve2", "17");
    		}
    	};
    	
    	String serialNo = getToolKit().getSerialNumber();
    	
    	String mac = getToolKit().getMacAddress();	
    	 
		StringBuffer details = new StringBuffer();
		
		details.append("eostek:HEAD/");
				
		details.append("FMWV:"+SystemProperties.get("ro.scifly.version", "")+SystemProperties.get("ro.scifly.version.mod", "")+"/");
		
		//SNUM
		if( serialNo!= null && !serialNo.isEmpty()){
			details.append("SNUM:"+serialNo+"/");
		} else {
			details.append("SNUM:/");
		}
				
		// MAC	 
		if(mac != null && !mac.isEmpty()){
			
		  if(mac.contains(":")){  
		        mac = mac.replace(":", "");
		  } 
		  
		  details.append("MAC:"+mac+"/");
		}else {
			details.append("MAC:/");
		}
				
		details.append("/ITEM:");
						
		boolean result 	= true;
		String ngDetail = "";
		int ngCount 	= 0;
		
		ConfigManager configManager = MtaApplication.getConfigManager();
		
		for(Iterator it = maps.keySet().iterator(); it.hasNext();){
			String key = (String) it.next();
			
			FactoryItem item = configManager.getFactoryItem(key);
			if(item != null && !item.getResult()){
				result = false;
				++ngCount;
				ngDetail += "/" + (String) maps.get(key) + ":" + "F";
			}
		}
		
		String hdcpKeyUdid = activity.getSharedPreferences("mta", Context.MODE_PRIVATE)
					.getString("hdcp-key-udid", null);	
		
		if(result){			
			details.append("T/HDK:"+hdcpKeyUdid);
		}else{
			details.append("F/"+ngCount+ngDetail+"/HDK:"+hdcpKeyUdid);
		}
			
		
		details.append("//END");
			
		return details.toString();
	}
    
	protected class FinishDialog extends DialogFragment{
		private Button finishBtn = null;
		
		private ImageView img = null;
		
		private MstarMtaDevice device = null;
		
		public FinishDialog(MstarMtaDevice device){
			this.device = device;
		}
		
		private View.OnClickListener finishClickListener = new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				dismiss();
				device.reset();		
			}
		};
		
		@Override
		public void onCreate(Bundle savedInstanceState) {
			super.onCreate(savedInstanceState);
		};
		
		@Override
		public View onCreateView(LayoutInflater inflater, ViewGroup container,
				Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			View contentView = inflater.inflate(R.layout.amta_finish, container, false);
			finishBtn = (Button) contentView.findViewById(R.id.finish_btn);
			finishBtn.setOnClickListener(finishClickListener);
			
			img = (ImageView) contentView.findViewById(R.id.qrc_img);
			
			return contentView;
		}
		
		@Override
		public void onStart() {
			// TODO Auto-generated method stub
			super.onStart();
			showQRCodeImage();
		}

		private void showQRCodeImage(){
			img.setImageBitmap(QRCodeUtil.createQRCodeBitmap(device.packAmtaResult(), 256, 256));
		
		}
	}
}
