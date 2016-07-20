package com.utsmta.mstar;

import java.io.File;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.Toast;

import com.mstar.android.tvapi.common.TvManager;
import com.mstar.android.tvapi.common.exception.TvCommonException;
import com.utsmta.app.MtaApplication;
import com.utsmta.app.R;
import com.utsmta.common.FactoryDevice;
import com.utsmta.common.FactoryGroup;
import com.utsmta.common.FactoryItem;
import com.utsmta.common.IToolKit;
import com.utsmta.common.MtaProgressDialog;
import com.utsmta.common.WizardFragment;
import com.utsmta.common.FactoryInspect.InspectResultListener;
import com.utsmta.mstar.inspect.MstarSnInspect;
import com.utsmta.utils.LogUtil;
import com.utsmta.utils.MtaUtils;
import com.utsmta.common.DeviceManager;


public class MstarSnFragment extends WizardFragment {
	private final String TAG = "MstarSnFragment";
	
	private final String SN = "sn";
	
	private final String HDCP_KEY = "hdcp_key";
	
	private MstarSnInspect snInspect = new MstarSnInspect();
	
	private EditText macEditBox = null;
	
	private TextView msgTv = null;
	
	private TextView hdcpKeyTitle = null;
	
	private Button hdcpKeyReBurnBn = null;
	
	private MessageDialog messageDialog = null;
	
	private MtaAlertDialog alertDialog = null;
	
	private FactoryItem snItem = null;
	
	private FactoryItem hdcpItem = null;
	
	private RadioButton rb_200Lumens = null;
	
	private RadioButton rb_300Lumens = null;
	
	private RadioGroup radioGroup = null;
	
	private TextView lumensTv = null;
	
	private boolean deviceDlp628 = false;
	
	private int ledLumensIndex = 1;
	
	private InspectResultListener listener = new InspectResultListener() {
		
		@Override
		public void onResultUpdate(boolean passed, int error, Bundle extra) {
			// TODO Auto-generated method stub			
			//dismiss progress dialog
			MtaProgressDialog.dismiss();
			
			String message = null;
			
			switch (error) {
			case MstarSnInspect.ERROR_NONE:
				message = getString(R.string.write_sn_success);
				break;

			case MstarSnInspect.ERROR_INVALID_MAC:
				message = getString(R.string.sn_error_invalid_mac);
				break;
				
			case MstarSnInspect.ERROR_DB_NOT_FOUND:
				message = getString(R.string.sn_error_database_not_found);
				break;
				
			case MstarSnInspect.ERROR_DB_CANNOT_OPEN:
				message = getString(R.string.sn_error_database_unopenable);
				break;
				
			case MstarSnInspect.ERROR_DB_READ_EXCEPTION:
				message = getString(R.string.sn_error_read_exception);
				break;
				
			case MstarSnInspect.ERROR_DB_WRITE_EXCEPTION:
				message = getString(R.string.sn_error_write_exception);
				break;
				
			case MstarSnInspect.ERROR_MAC_NOT_EXIST:
				message = getString(R.string.mac_error_not_in_db);
				break;
				
			case MstarSnInspect.ERROR_MAC_ALREADY_USED:
				message = getString(R.string.sn_error_been_used);
				break;
				
			case MstarSnInspect.ERROR_UPDATE_FAILED:
				message = getString(R.string.write_sn_failed);
				break;
				
			case MstarSnInspect.ERROR_NO_HDCP_1_4_DATA:
				message = getString(R.string.hdcp_1_4_empty);
				break;
				
			case MstarSnInspect.ERROR_NO_HDCP_2_2_DATA:
				message = getString(R.string.hdcp_2_2_empty);
				break;
				
			case MstarSnInspect.ERROR_BURN_HDCP_1_4_FAILED:
				message = getString(R.string.hdcp_1_4_failed);
				break;
				
			case MstarSnInspect.ERROR_BURN_HDCP_2_2_FAILED:
				message = getString(R.string.hdcp_2_2_failed);
				break;
			case MstarSnInspect.ERROR_UNKNOWN:
				message = getString(R.string.sn_error_unknown);
				break;	

			case MstarSnInspect.HDCP_KEY_REBURN_OK:
				message = getString(R.string.hdcp_re_burn_ok);
				break;	
				
			default:
				break;
			}
			
			if( message != null ){
				Toast.makeText(getActivity(), message, Toast.LENGTH_LONG).show();
			}
			
			if( passed ){
				msgTv.setTextColor(getResources().getColor(R.color.light_green));
				parent.setMacAddress(MtaApplication.getDevice().getToolKit().getMacAddress());
				parent.setSerialNo(MtaApplication.getDevice().getToolKit().getSerialNumber());
				parent.setHdcpFlag(true);
				parent.setHdcp_2_2_Flag(true);
				messageDialog.show(parent.getFragmentManager(), "");
				try {
					Thread.sleep(1200);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				messageDialog.dismiss();
				message += "(MAC = "+MtaApplication.getDevice().getToolKit().getMacAddress()+")";
				moveToNextFragment();
			}else{
				msgTv.setTextColor(getResources().getColor(R.color.light_red));
			}
			
			msgTv.setText(message);
			macEditBox.setText("");
		}
	};
	
	public MstarSnFragment(FactoryGroup factoryGroup) {
		super(factoryGroup);
		// TODO Auto-generated constructor stub
		snItem = factoryGroup.getItem(SN);
		snItem.setResult(true);
		
		hdcpItem = factoryGroup.getItem(HDCP_KEY);
		if(hdcpItem != null){
			if("true".equals(hdcpItem.getProperty("1.4"))){
				snInspect.configHdcpKey_1_4(true);
			}
			
			if("true".equals(hdcpItem.getProperty("2.2"))){
				snInspect.configHdcpKey_2_2(true);
			}
			hdcpItem.setResult(true);
		}
		
		snInspect.registerResultListener(listener);
	}
	
	@Override
	protected int uiStyle() {
		// TODO Auto-generated method stub
		return WizardFragment.STYLE_NO_PREV;
	}
	
	private TextWatcher textWatcher = new TextWatcher() {
		
		@Override
		public void onTextChanged(CharSequence s, int start, int before, int count) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void beforeTextChanged(CharSequence s, int start, int count,
				int after) {
			// TODO Auto-generated method stub
			
		}
		
		@Override
		public void afterTextChanged(Editable s) {
			// TODO Auto-generated method stub
			if(macEditBox.length() == 12){
				onMacAddressScanDone();
			}
		}
	};

	private void onMacAddressScanDone(){
		//show progress dialog
		MtaProgressDialog.show(getActivity(), "", getString(R.string.programming_mac_sn));
		
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				//update serial_number and mac_address 
				snInspect.updateSerialNumberAndMacAddress(macEditBox.getText().toString().trim());			
			}
		}).start();
	}
	
	
	private void reBurnHdcpKey(){
		//show progress dialog				
							
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				//update serial_number and mac_address 
				snInspect.updateHdcpKey(MtaApplication.getDevice().getToolKit().getMacAddress().replace(":", "").trim());			
			}
		}).start();
	}
	
	private OnClickListener onHdcpKeyListener = new OnClickListener() {
		
		@Override
		public void onClick(View arg0) {
			// TODO Auto-generated method stub
			reBurnHdcpKey();
		}
	};
	
	
	private OnKeyListener onKeyListener = new OnKeyListener() {

		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			// TODO Auto-generated method stub
			if ((keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_DPAD_CENTER)) {
				
				if(KeyEvent.ACTION_DOWN == event.getAction() && macEditBox.getText().length() > 0){
					onMacAddressScanDone();
				}

				return true;
			}
			
			return false;
		}
		
	};
	
	@Override
	public View onCreateSubView(LayoutInflater inflater) {
		// TODO Auto-generated method stub
		View contentView = inflater.inflate(R.layout.sn_mac, null, false);
		
		msgTv = (TextView) contentView.findViewById(R.id.last_error_message);
					
		macEditBox = (EditText) contentView.findViewById(R.id.serial_editText);
		macEditBox.setInputType(InputType.TYPE_NULL);
		macEditBox.addTextChangedListener(textWatcher);
		macEditBox.setOnKeyListener(onKeyListener);
		macEditBox.requestFocus(); //request focus immediately when view shown
		
		messageDialog = new MessageDialog(getString(R.string.sn_next_tip));
		messageDialog.setCancelable(false);			
		hdcpKeyTitle = (TextView)contentView.findViewById(R.id.hdcp_title);	
		hdcpKeyReBurnBn = (Button)contentView.findViewById(R.id.hdcp_re_burn);		
		hdcpKeyReBurnBn.setOnClickListener(onHdcpKeyListener);
		
		if(MtaApplication.getDevice().getDeviceName().equals(DeviceManager.DEVICE_H628)
			|| MtaApplication.getDevice().getDeviceName().equals(DeviceManager.DEVICE_H638)
			|| MtaApplication.getDevice().getDeviceName().equals(DeviceManager.DEVICE_H828)){
			hdcpKeyTitle.setVisibility(View.VISIBLE);
			hdcpKeyReBurnBn.setVisibility(View.VISIBLE);
		}
							
//		radioGroup = (RadioGroup) contentView.findViewById(R.id.lumens_group);
//		rb_200Lumens = (RadioButton) contentView.findViewById(R.id.lumens_200);
//		rb_300Lumens = (RadioButton) contentView.findViewById(R.id.lumens_300);
		
		lumensTv = (TextView) contentView.findViewById(R.id.lumens_tv);
		FactoryDevice device = MtaApplication.getDevice();
		if("L628".equals(device.getDeviceName()) 
				 && "dlp".equals(device.getDeviceBranch())){
			deviceDlp628 = true;
			lumensTv.setVisibility(View.VISIBLE);
			lumensTv.setTextSize(80);

			try {
				if("0".equals(TvManager.getInstance().getEnvironment("XShuaiUFO_300LM"))){
					lumensTv.setTextColor(getResources().getColor(R.color.light_red));
					lumensTv.setText(R.string.lumens_200);
					ledLumensIndex = 0;
				}
			} catch (TvCommonException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			
			getDlpLedLumens();
		}
		
		return contentView;
	}
	
	protected OnCheckedChangeListener onCheckedChangeListener = new OnCheckedChangeListener() {
		
		@Override
		public void onCheckedChanged(RadioGroup group, int checkedId) {
			// TODO Auto-generated method stub
			parent.setHdcpLabelText(((RadioButton)group.findViewById(checkedId)).getText().toString());
		}
	};
	
	@Override
	protected void onMoveToNextMsgReceived() {
		// TODO Auto-generated method stub
		IToolKit toolKit = MtaApplication.getDevice().getToolKit();
		
		String serialNo = toolKit.getSerialNumber();
		String macAddress = toolKit.getMacAddress();
		
		if(serialNo != null){
			serialNo = serialNo.toUpperCase();
		}
		
		if(macAddress != null){
			macAddress = macAddress.toUpperCase().replaceAll(":", "");
		}
		
		LogUtil.d(TAG, "macAddress = "+macAddress+" serialNo = "+serialNo);
		if (serialNo != null && macAddress != null 
				&& !serialNo.isEmpty()
				&& !macAddress.isEmpty()
				&& serialNo.contains(macAddress) && (macAddress.length() == 12)) {
			if(deviceDlp628){
				saveDlpLedLumens();
			}
			super.onMoveToNextMsgReceived();
		} else {
			alertDialog = new MtaAlertDialog(getString(R.string.error_sn_alert));
			alertDialog.show(parent.getFragmentManager(), "");
			macEditBox.requestFocus();
		}
		
	}
	
	protected class MessageDialog extends DialogFragment{
		private String message = null;
		
		public MessageDialog(String message) {
			// TODO Auto-generated constructor stub
			this.message = message;
		}
		
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());  
			
			View view = LayoutInflater.from(getActivity()).inflate(R.layout.message_dialog, null);
			TextView messageTv = (TextView) view.findViewById(R.id.message);		
			messageTv.setText(message);
			
			builder.setView(view);
			
			return builder.create();
		}
		
		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			super.onActivityCreated(savedInstanceState);
			
			Window window = getDialog().getWindow();
			WindowManager.LayoutParams lp = window.getAttributes();
			lp.width  = 400;
			lp.height = 320;
			window.setAttributes(lp);
		}
	}
	
	protected class MtaAlertDialog extends DialogFragment{
		private String message = null;
		
		public MtaAlertDialog(String message) {
			// TODO Auto-generated constructor stub
			this.message = message;
		}
		
		@Override
		public Dialog onCreateDialog(Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			LogUtil.d(TAG, "MtaAlertDialog onCreateDialog");
			AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());  
			
			View view = LayoutInflater.from(getActivity()).inflate(R.layout.message_dialog, null);
			TextView messageTv = (TextView) view.findViewById(R.id.message);		
			messageTv.setText(message);
			
			builder.setView(view);
			builder.setPositiveButton(getString(R.string.confirm), 
					new DialogInterface.OnClickListener() {
						
						@Override
						public void onClick(DialogInterface dialog, int which) {
							// TODO Auto-generated method stub
							dismiss();
						}
					});
			
			return builder.create();
		}
		
		@Override
		public void onActivityCreated(Bundle savedInstanceState) {
			// TODO Auto-generated method stub
			super.onActivityCreated(savedInstanceState);
			
			LogUtil.d(TAG, "MtaAlertDialog onActivityCreated");
			Window window = getDialog().getWindow();
			WindowManager.LayoutParams lp = window.getAttributes();
			lp.width  = 400;
			lp.height = 320;
			window.setAttributes(lp);
		}	
	}

	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		if (deviceDlp628) {
			IntentFilter intentFilter = new IntentFilter();
			intentFilter.addAction("android.intent.action.MEDIA_MOUNTED");
			intentFilter.addDataScheme("file");
			parent.registerReceiver(storageReceiver, intentFilter);
		}
	}
	
	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		if(deviceDlp628){
			parent.unregisterReceiver(storageReceiver);
		}
		
		super.onStop();
	}
	
	protected BroadcastReceiver storageReceiver = new BroadcastReceiver() {
		
		@Override
		public void onReceive(Context context, Intent intent) {
			// TODO Auto-generated method stub
			if("android.intent.action.MEDIA_MOUNTED".equals(intent.getAction())){
				getDlpLedLumens();
			}			
		}
	};
	
	protected void getDlpLedLumens(){
		LogUtil.d(TAG, "getDlpLumens");
		final String fileName = "eostek-fmtac.ini";
		final String propertyName = "dlp_led_lumens";
		
		for(String usbDir : MtaUtils.getMountedUsbDevices("/mnt/usb/")){
			File file = new File(usbDir+"/"+fileName);
			
			if(file.exists()){
				String property = MtaUtils.getPropertyFromFile(usbDir+"/"+fileName, propertyName);
				LogUtil.d(TAG, "find eostek-fmtac.ini");
				if("0".equals(property)){
					LogUtil.d(TAG, "property = 0");
					lumensTv.setTextColor(getResources().getColor(R.color.light_red));
					lumensTv.setText(R.string.lumens_200);
					ledLumensIndex = 0;
				}else{
					lumensTv.setTextColor(getResources().getColor(R.color.light_green));
					lumensTv.setText(R.string.lumens_300);
					ledLumensIndex = 1;
				}
				
				return ;
			}
		}
	}
	
	protected void saveDlpLedLumens(){
		if(ledLumensIndex == 0){
			try {
				TvManager.getInstance().setEnvironment("XShuaiUFO_300LM", "0");
				android.provider.Settings.System.putString(parent.getContentResolver(), "XShuaiUFO_300LM", "0");
			} catch (TvCommonException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		}else{
			try {
				TvManager.getInstance().setEnvironment("XShuaiUFO_300LM", "1");
				android.provider.Settings.System.putString(parent.getContentResolver(), "XShuaiUFO_300LM", "1");
			} catch (TvCommonException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
	}
}
