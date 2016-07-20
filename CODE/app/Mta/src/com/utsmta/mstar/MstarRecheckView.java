package com.utsmta.mstar;

import org.json.JSONException;

import android.app.Activity;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Editable;
import android.text.InputType;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.mstar.android.tvapi.common.TvManager;
import com.utsmta.app.MainActivity;
import com.utsmta.app.MtaApplication;
import com.utsmta.app.R;
import com.utsmta.common.DeviceManager;
import com.utsmta.common.FactoryGroup;
import com.utsmta.common.FactoryItem;
import com.utsmta.common.IToolKit;
import com.utsmta.common.MtaHttpKit;
import com.utsmta.common.MtaMessageDialog;
import com.utsmta.common.MtaPagerView;
import com.utsmta.common.MtaProgressDialog;
import com.utsmta.common.MtaServerHelper;
import com.utsmta.common.FactoryInspect.InspectResultListener;
import com.utsmta.common.MtaHttpKit.OnResponseListener;
import com.utsmta.common.MtaHttpKit.ResponsePacket;
import com.utsmta.mstar.inspect.MstarSnInspect;
import com.utsmta.utils.LogUtil;
import com.utsmta.utils.MtaUtils;

public class MstarRecheckView extends MtaPagerView {
	private final String TAG = "MstarRecheckView";

	private MstarSnInspect snInspect = new MstarSnInspect();
	
	private EditText macEditBox = null;
	
	private TextView msgTv = null;
	
	private TextView crcTitle = null;
	
	private TextView crcTip = null;
	
	private MainActivity mainActivity = null;
	
	private OnKeyListener keyListenerDelegate = null;
	
	private Handler uiHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
				Bundle data = msg.getData();
			if(msg.what == 0){				
				if(data != null){
					String message = data.getString("message");
					Toast.makeText(activity, message, Toast.LENGTH_LONG).show();
				}
				
				try {
					Thread.sleep(500);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				keyListenerDelegate.onKey(macEditBox, 
						KeyEvent.KEYCODE_DPAD_LEFT, 
						new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_LEFT));
			}
		};
	};
	
	public MstarRecheckView(Activity activity, OnKeyListener keyListenerDelegate) {
		super(activity);
		// TODO Auto-generated constructor stub
		mainActivity = (MainActivity) activity;
		snInspect.registerResultListener(listener);
		this.keyListenerDelegate = keyListenerDelegate;
	}
	
	private InspectResultListener listener = new InspectResultListener() {
		
		@Override
		public void onResultUpdate(boolean passed, int error, Bundle extra) {
			// TODO Auto-generated method stub			
			//dismiss progress dialog
			MtaProgressDialog.dismiss();
			
			String message = null;
			
			switch (error) {
			case MstarSnInspect.ERROR_NONE:
				message = activity.getString(R.string.write_sn_success);
				break;

			case MstarSnInspect.ERROR_INVALID_MAC:
				message = activity.getString(R.string.sn_error_invalid_mac);
				break;
				
			case MstarSnInspect.ERROR_DB_NOT_FOUND:
				message = activity.getString(R.string.sn_error_database_not_found);
				break;
				
			case MstarSnInspect.ERROR_DB_CANNOT_OPEN:
				message = activity.getString(R.string.sn_error_database_unopenable);
				break;
				
			case MstarSnInspect.ERROR_DB_READ_EXCEPTION:
				message = activity.getString(R.string.sn_error_read_exception);
				break;
				
			case MstarSnInspect.ERROR_DB_WRITE_EXCEPTION:
				message = activity.getString(R.string.sn_error_write_exception);
				break;
				
			case MstarSnInspect.ERROR_MAC_NOT_EXIST:
				message = activity.getString(R.string.mac_error_not_in_db);
				break;
				
			case MstarSnInspect.ERROR_MAC_ALREADY_USED:
				message = activity.getString(R.string.sn_error_been_used);
				break;
				
			case MstarSnInspect.ERROR_UPDATE_FAILED:
				message = activity.getString(R.string.write_sn_failed);
				break;
				
			default:
				break;
			}
			
			if( message != null ){
				Toast.makeText(activity, message, Toast.LENGTH_LONG).show();
			}
			
			if( passed ){
				mainActivity.setMacAddress(MtaApplication.getDevice().getToolKit().getMacAddress());
				mainActivity.setSerialNo(MtaApplication.getDevice().getToolKit().getSerialNumber());
				mainActivity.setHdcpFlag(true);
				mainActivity.setHdcp_2_2_Flag(true);
				showPositiveMessage(message);
			}else{
				showNegativeMessage(message);
			}
			
			onMacChecked(passed);
			
			//clear edit_box content
			macEditBox.setText(""); 
		}
	};

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
				onMacScanDone();
			}
		}
	};
	
	protected void onMacChecked(boolean passed){
		if(!passed){
			setCrcResult(false);
			return ;
		}
		
		final String uri = MtaServerHelper.findMtaServerAddress();
		
		StringBuffer details = new StringBuffer();
		
		boolean ok = true;
		
		for(FactoryGroup group:MtaApplication.getConfigManager().getAllGroups()){
			for(FactoryItem item:group.getAllItems()){
				if("special".equals(item.getProperty("tag"))){
					continue;
				}
				
				if(!item.getResult()){
					ok = false;
					details.append(item.getName()).append(",");								
				}					
			}
		}
		
		setCrcResult(ok);
		
		if(ok){	
			showPositiveMessage("FMTAC : "+uri);
			postInspectResult(uri);
		}else{
			showNegativeMessage(activity.getString(R.string.test_crc_tag)+" : "+details.toString()+
					"\n"+"FMTAC : "+uri);
			
			MtaMessageDialog messageDialog = new MtaMessageDialog(activity.getString(R.string.about_to_upload_message), 
					MtaMessageDialog.STYLE_NORMAL, 
					activity.getString(R.string.cancel),
					activity.getString(R.string.confirm));
			
			messageDialog.setButtonClickListener(new MtaMessageDialog.OnDialogButtonClickListener() {
				
				@Override
				public void OnPositiveButtonClick(DialogInterface dialog) {
					// TODO Auto-generated method stub
					dialog.dismiss();			
				}
				
				@Override
				public void OnNegativeButtonClick(DialogInterface dialog) {
					// TODO Auto-generated method stub						
					dialog.dismiss();
					postInspectResult(uri);
				}
			});
					
			messageDialog.show(activity.getFragmentManager(), "");
		}
	}
	
	private void setCrcResult(Boolean ret){
		crcTip.setTextSize(100);		
		if(ret){
			crcTip.setTextColor(android.graphics.Color.GREEN);
			crcTip.setText(activity.getString(R.string.pass_en));
		}else{
			crcTip.setTextColor(android.graphics.Color.RED);
			crcTip.setText(activity.getString(R.string.fail_en));
		}		
	}
	
	private void onMacScanDone(){
		final String scanMacAddr = macEditBox.getText().toString().trim();
		macEditBox.setText(""); 
												
		String curMacAddr = MtaApplication.getDevice().getToolKit().getMacAddress();
		
		if(curMacAddr != null){
			curMacAddr = curMacAddr.trim().toUpperCase().replaceAll(":", "");
		}
		
		LogUtil.d(TAG, "scanMacAddr = "+scanMacAddr+" curMacAddr = "+curMacAddr);
		if(scanMacAddr.equals(curMacAddr)){
			onMacChecked(true);
		}else{								
			String message = activity.getString(R.string.mac_crc_tag)+"\n"
					+activity.getString(R.string.mac_old)+":"+curMacAddr
					+activity.getString(R.string.mac_new)+":"+scanMacAddr+"\n"
					+activity.getString(R.string.mac_erc_confirm);
			
			MtaMessageDialog messageDialog = new MtaMessageDialog(message, 
					MtaMessageDialog.STYLE_NORMAL, 
					activity.getString(R.string.confirm),
					activity.getString(R.string.cancel));
			
			messageDialog.setButtonClickListener(new MtaMessageDialog.OnDialogButtonClickListener() {
				
				@Override
				public void OnPositiveButtonClick(DialogInterface dialog) {
					// TODO Auto-generated method stub
					dialog.dismiss();
					
					//show progress dialog
					MtaProgressDialog.show(activity, "", "updating ...... ");
					
					//update serial_number and mac_address 
					snInspect.updateSerialNumberAndMacAddress(scanMacAddr);
				}
				
				@Override
				public void OnNegativeButtonClick(DialogInterface dialog) {
					// TODO Auto-generated method stub						
					dialog.dismiss();
				}
			});
					
			messageDialog.show(activity.getFragmentManager(), "");
		}
		
	}
	
	private OnKeyListener onKeyListener = new OnKeyListener() {

		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			// TODO Auto-generated method stub
			if ((keyCode == KeyEvent.KEYCODE_ENTER || keyCode == KeyEvent.KEYCODE_DPAD_CENTER)) {
				
				if(KeyEvent.ACTION_DOWN == event.getAction() && macEditBox.getText().length() > 0){
					onMacScanDone();
				}

				return true;
			} else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT || 
					keyCode == KeyEvent.KEYCODE_DPAD_RIGHT || 
					keyCode == KeyEvent.KEYCODE_DPAD_UP ||
					keyCode == KeyEvent.KEYCODE_DPAD_DOWN){
				if(keyListenerDelegate != null){
					return keyListenerDelegate.onKey(v, keyCode, event);
				}
			}
			
			return false;
		}
		
	};
	
	@Override
	protected View onCreateView(LayoutInflater inflater) {
		// TODO Auto-generated method stub
		View contentView = inflater.inflate(R.layout.sn_mac, null, false);
		
		msgTv = (TextView) contentView.findViewById(R.id.last_error_message);
		
		crcTip = (TextView) contentView.findViewById(R.id.crc_tip);
		
		crcTitle = (TextView) contentView.findViewById(R.id.crc_title);
		
		crcTitle.setText(activity.getString(R.string.crc_title_str));		
		
		macEditBox = (EditText) contentView.findViewById(R.id.serial_editText);
		macEditBox.setInputType(InputType.TYPE_NULL);
		macEditBox.addTextChangedListener(textWatcher);
		macEditBox.setOnKeyListener(onKeyListener);
		macEditBox.requestFocus(); //request focus immediately when view shown
		
		return contentView;
	}
	
	@Override
	public void onHiden() {
		// TODO Auto-generated method stub
		LogUtil.d(TAG, "onHiden");
		super.onHiden();
	}
	
	@Override
	public void onShown() {
		// TODO Auto-generated method stub
		super.onShown();
		LogUtil.d(TAG, "onHiden");
		macEditBox.requestFocus();
	}
	
	protected void showPositiveMessage(String message){
		msgTv.setTextColor(activity.getResources().getColor(R.color.light_green));
		msgTv.setText(message);
	}
	
	protected void showNegativeMessage(String message){
		msgTv.setTextColor(activity.getResources().getColor(R.color.light_red));
		msgTv.setText(message);
	}
	
	protected void postInspectResult(final String uri){		
		if(uri == null){
			MtaMessageDialog messageDialog = new MtaMessageDialog(activity.getString(R.string.no_url), 
					MtaMessageDialog.STYLE_NORMAL, 
					activity.getString(R.string.confirm),
					activity.getString(R.string.cancel));
			
			messageDialog.show(activity.getFragmentManager(), "");
			return ;
		}
		
		String finalResult = "T";
		StringBuffer details = new StringBuffer();
		
		for(FactoryGroup group:MtaApplication.getConfigManager().getAllGroups()){
			for(FactoryItem item:group.getAllItems()){
				if("special".equals(item.getProperty("tag"))){
					continue;
				}
				
				details.append(item.getName()).append(",");
				if(item.getResult()){
					details.append("T");				
				}else{
					details.append("F");
					finalResult = "F";
				}
				
				details.append(";");
			}
		}
		
		if(DeviceManager.DEVICE_L628.equals(MtaApplication.getDevice().getDeviceName())){
			boolean lumens300 = true;
			try {
				if("0".equals(TvManager.getInstance().getEnvironment("XShuaiUFO_300LM"))){
					lumens300 = false;
				}
			} catch (Exception e) {
				// TODO: handle exception
			}

			if(lumens300){
				details.append("LM300,T;");
			}else{
				details.append("LM300,F;");
			}
		}

		IToolKit toolKit = MtaApplication.getDevice().getToolKit();
		MtaHttpKit.PostPacket postPacket = new MtaHttpKit.PostPacket();
		String ver = mainActivity.getMtaVersion();
		String gw = "";
		String mac = toolKit.getMacAddress();
		String sn = toolKit.getSerialNumber();
		String tf = toolKit.getFetureCode();
		String hd = details.toString();
		String rt = finalResult;
		String md5 = MtaUtils.createHexMD5(ver+gw+mac+sn+tf+hd+rt);
		try {
			postPacket.put("ver", ver);
			postPacket.put("gw", gw);
			postPacket.put("mac", mac);
			postPacket.put("sn", sn);
			postPacket.put("tf", tf);
			postPacket.put("hd", hd);
			postPacket.put("rt", rt);
			postPacket.put("md5", md5);
		} catch (JSONException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	
		MtaProgressDialog.show(activity, null, activity.getString(R.string.uploading_mta_result));
		
		MtaHttpKit.post(uri.trim(), postPacket, new OnResponseListener() {
			
			@Override
			public void onResponse(int error, ResponsePacket responsePacket) {
				// TODO Auto-generated method stub
				MtaProgressDialog.dismiss();
				
				boolean failed = false;
				boolean needRetry = false;
				
				String message = activity.getString(R.string.test_result_send_ok);
				
				if (error == 0) {
					if(responsePacket != null){
						int err = 0, ct = 0;
						
						try {
							err = responsePacket.getInt("err");
							ct  = responsePacket.getInt("ct");
						} catch (JSONException e) {
							// TODO Auto-generated catch block
							e.printStackTrace();
						}		
						
						if(err == 1){
							failed = true;
							message = activity.getString(R.string.crc_err);
						}else if(err == 2 || ct >= 2){
							failed = true;
							message = activity.getString(R.string.mac_repeat);
						}
					}
				} else {	
					failed = true;
					needRetry = true;
					message = activity.getString(R.string.test_result_send_ng);
					
					if(error == MtaHttpKit.ERROR_INVALID_URI){
						message = activity.getString(R.string.invalid_uri);
					}else if(error == MtaHttpKit.ERROR_TIMEOUT){
						message = activity.getString(R.string.upload_timeout);
					}					
					else {
						message = activity.getString(R.string.test_result_send_ng);
					}
				}
				
				if(failed){
					int style = needRetry ? MtaMessageDialog.STYLE_NORMAL : MtaMessageDialog.STYLE_NEGATIVE;
					
					MtaMessageDialog messageDialog = new MtaMessageDialog(message, 
							style, 
							activity.getString(R.string.retry),
							activity.getString(R.string.confirm));
					
					messageDialog.setButtonClickListener(new MtaMessageDialog.OnDialogButtonClickListener() {
						
						@Override
						public void OnPositiveButtonClick(DialogInterface dialog) {
							// TODO Auto-generated method stub
							postInspectResult(uri);
						}
						
						@Override
						public void OnNegativeButtonClick(DialogInterface dialog) {
							// TODO Auto-generated method stub						
							dialog.dismiss();
						}
					});
					
					messageDialog.show(activity.getFragmentManager(), "");
				}else{
					Message msg = uiHandler.obtainMessage(0);
					Bundle data = new Bundle();
					data.putString("message", message);
					msg.setData(data);
					uiHandler.sendMessage(msg);
				}
			}
		});
	}
}
