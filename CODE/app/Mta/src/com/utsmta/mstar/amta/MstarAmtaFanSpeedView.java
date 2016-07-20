package com.utsmta.mstar.amta;

import java.io.File;

import android.app.Activity;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.utsmta.app.R;
import com.utsmta.common.FactoryItem;
import com.utsmta.common.ItemPagerView;
import com.utsmta.common.MtaProgressDialog;
import com.utsmta.mstar.inspect.MstarFanSpeedInspect;
import com.utsmta.utils.LogUtil;
import com.utsmta.utils.MtaUtils;

public class MstarAmtaFanSpeedView extends ItemPagerView {
	protected static final String TAG = "MstarAmtaFanSpeedView";
	
	protected final String CONFIG_FILE_NAME = "eostek-fmtac.ini";
	
	protected static final int SLOW_MODE_DEFAULT_SPEED = 10;
	
	protected static final int FAST_MODE_DEFAULT_SPEED = 100;
	
	protected static final int SLOW_MODE_DEFAULT_LOWER_BOUND = 3400;
	
	protected static final int SLOW_MODE_DEFAULT_UPPER_BOUND = 4000;
	
	protected static final int FAST_MODE_DEFAULT_LOWER_BOUND = 4000;
	
	protected static final int FAST_MODE_DEFAULT_UPPER_BOUND = 4800;
	
	private int slow_mode_speed = SLOW_MODE_DEFAULT_SPEED;
	
	private int fast_mode_speed = FAST_MODE_DEFAULT_SPEED;
	
	private int slow_mode_lower_bound = SLOW_MODE_DEFAULT_LOWER_BOUND;
	
	private int slow_mode_upper_bound = SLOW_MODE_DEFAULT_UPPER_BOUND;
	
	private int fast_mode_lower_bound = FAST_MODE_DEFAULT_LOWER_BOUND;
	
	private int fast_mode_upper_bound = FAST_MODE_DEFAULT_UPPER_BOUND;
	
	private int fan1LowSpeed = 0;
	
	private int fan1HighSpeed = 0;
	
	private int fan2LowSpeed = 0;
	
	private int fan2HighSpeed = 0;
	
	private TextView standardLowSpeedTv = null;
	
	private TextView standardHighSpeedTv = null;
	
	private TextView fan1LowSpeedTv = null;
	
	private TextView fan1HighSpeedTv = null;
	
	private TextView fan2LowSpeedTv = null;
	
	private TextView fan2HighSpeedTv = null;
	
	private ImageView fan1ResultIv = null;
	
	private ImageView fan2ResultIv = null;
 	
	private Button testButton = null;
	
	private MstarFanSpeedInspect fanInspect = new MstarFanSpeedInspect();
	
	MtaProgressDialog progressDialog = null;
	
	public MstarAmtaFanSpeedView(Activity activity, FactoryItem item) {
		super(activity, item);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected View onCreateView(LayoutInflater inflater) {
		// TODO Auto-generated method stub
		View contentView = inflater.inflate(R.layout.amta_fan, null, false);
				
		standardLowSpeedTv = (TextView) contentView.findViewById(R.id.standard_low_speed);
		standardHighSpeedTv = (TextView) contentView.findViewById(R.id.standard_high_speed);
		
		fan1LowSpeedTv = (TextView) contentView.findViewById(R.id.fan1_low_speed);
		fan1HighSpeedTv = (TextView) contentView.findViewById(R.id.fan1_high_speed);
		fan2LowSpeedTv = (TextView) contentView.findViewById(R.id.fan2_low_speed);
		fan2HighSpeedTv = (TextView) contentView.findViewById(R.id.fan2_high_speed);
		
		fan1ResultIv = (ImageView) contentView.findViewById(R.id.fan1_result);
		fan2ResultIv = (ImageView) contentView.findViewById(R.id.fan2_result);
		
		fan1LowSpeedTv.setText("("+activity.getString(R.string.low_speed)+")"+fan1LowSpeed);
		fan1HighSpeedTv.setText("("+activity.getString(R.string.high_speed)+")"+fan1HighSpeed);
		fan2LowSpeedTv.setText("("+activity.getString(R.string.low_speed)+")"+fan2LowSpeed);
		fan2HighSpeedTv.setText("("+activity.getString(R.string.high_speed)+")"+fan2HighSpeed);
		
		fan1ResultIv.setImageResource(R.drawable.alert);
		fan2ResultIv.setImageResource(R.drawable.alert);
		
		testButton = (Button) contentView.findViewById(R.id.test_btn);
		testButton.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				startTesting();
			}
		});
		
		return contentView;
	}
	
	@Override
	protected void onShown() {
		// TODO Auto-generated method stub
		getConfigs();
	}
	
	protected void updateFansInfo(){
		fan1LowSpeedTv.setText("("+activity.getString(R.string.low_speed)+")"+fan1LowSpeed);
		fan1HighSpeedTv.setText("("+activity.getString(R.string.high_speed)+")"+fan1HighSpeed);
		fan2LowSpeedTv.setText("("+activity.getString(R.string.low_speed)+")"+fan2LowSpeed);
		fan2HighSpeedTv.setText("("+activity.getString(R.string.high_speed)+")"+fan2HighSpeed);
		
		boolean fan1 = false;
		boolean fan2 = false;
		
		if(fan1LowSpeed >= slow_mode_lower_bound && fan1LowSpeed <= slow_mode_upper_bound
				&& fan1HighSpeed >= fast_mode_lower_bound && fan1HighSpeed <= fast_mode_upper_bound){
			fan1 = true;
			fan1ResultIv.setImageResource(R.drawable.success);
		}else{
			fan1ResultIv.setImageResource(R.drawable.alert);
		}
		
		if(fan2LowSpeed >= slow_mode_lower_bound && fan2LowSpeed <= slow_mode_upper_bound
				&& fan2HighSpeed >= fast_mode_lower_bound && fan2HighSpeed <= fast_mode_upper_bound){
			fan2ResultIv.setImageResource(R.drawable.success);
			fan2 = true;
		}else{
			fan2ResultIv.setImageResource(R.drawable.alert);
		}		
		
		if(fan1 && fan2){
			item.setResult(true);
			notifyUiUpdate();
		}
	}
	
	protected void startTesting(){
		LogUtil.d(TAG, "startTesting");
		
		getConfigs();
		
		MtaProgressDialog.show(activity, null, activity.getString(R.string.switching_low_speed));
		
		new Thread(new FanTask()).start();
	}
	
	protected void getConfigs(){
		String smSpeed = "";
		String fmSpeed = "";
		
		String smLowerBound = "";
		String smUpperBound = "";
		String fmLowerBound = "";
		String fmUpperBound = "";
		
		for(String dirPath : MtaUtils.getMountedUsbDevices("/mnt/usb/")){
			File file = new File(dirPath+"/"+CONFIG_FILE_NAME);
			
			if(file.exists()){
				smSpeed = MtaUtils.getPropertyFromFile(file, "slow_mode_speed");
				fmSpeed = MtaUtils.getPropertyFromFile(file, "fast_mode_speed");
				
				smLowerBound = MtaUtils.getPropertyFromFile(file, "slow_mode_lower_bound");
				smUpperBound = MtaUtils.getPropertyFromFile(file, "slow_mode_upper_bound");
				fmLowerBound = MtaUtils.getPropertyFromFile(file, "fast_mode_lower_bound");
				fmUpperBound = MtaUtils.getPropertyFromFile(file, "fast_mode_upper_bound");
				break;
			}
		}
		
		if(MtaUtils.isDigit(smSpeed)){
			slow_mode_speed = Integer.valueOf(smSpeed);
		}
		
		if(MtaUtils.isDigit(fmSpeed)){
			fast_mode_speed = Integer.valueOf(fmSpeed);
		}
		
		if(MtaUtils.isDigit(smLowerBound)){
			slow_mode_lower_bound = Integer.valueOf(smLowerBound);
		}
		
		if(MtaUtils.isDigit(smUpperBound)){
			slow_mode_upper_bound = Integer.valueOf(smUpperBound);
		}
		
		if(MtaUtils.isDigit(fmLowerBound)){
			fast_mode_lower_bound = Integer.valueOf(fmLowerBound);
		}
		
		if(MtaUtils.isDigit(fmUpperBound)){
			fast_mode_upper_bound = Integer.valueOf(fmUpperBound);
		}
		
		if(slow_mode_speed < 0 || slow_mode_speed > 100){
			slow_mode_speed = SLOW_MODE_DEFAULT_SPEED;
		}
		
		if(fast_mode_speed < 0 || fast_mode_speed > 100){
			fast_mode_speed = FAST_MODE_DEFAULT_SPEED;
		}
		
		if(slow_mode_lower_bound > slow_mode_upper_bound){
			slow_mode_lower_bound = SLOW_MODE_DEFAULT_LOWER_BOUND;
			slow_mode_upper_bound = SLOW_MODE_DEFAULT_UPPER_BOUND;
		}
		
		if(fast_mode_lower_bound > fast_mode_upper_bound){
			fast_mode_lower_bound = FAST_MODE_DEFAULT_LOWER_BOUND;
			fast_mode_upper_bound = FAST_MODE_DEFAULT_UPPER_BOUND;
		}
		
		standardLowSpeedTv.setText("("+activity.getString(R.string.low_speed)+")"+slow_mode_lower_bound+"~"+slow_mode_upper_bound);
		standardHighSpeedTv.setText("("+activity.getString(R.string.high_speed)+")"+fast_mode_lower_bound+"~"+fast_mode_upper_bound);
	}
	
	Handler uiHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			int what = msg.what;
			
			if(what == 0){
				LogUtil.d(TAG, "FanTask done");
				MtaProgressDialog.dismiss();
				updateFansInfo();
			}else if(what == 1){
				MtaProgressDialog.show(activity, null, activity.getString(R.string.calculating_fan_speed));
			}else if(what == 2){
				MtaProgressDialog.show(activity, null, activity.getString(R.string.switching_high_speed));
			}	
		};
	};

	protected class FanTask implements Runnable{

		@Override
		public void run() {
			// TODO Auto-generated method stub		
			LogUtil.d(TAG, "FanTask run");
			//low speed
			fanInspect.setFanSpeed(0, slow_mode_speed);
			fanInspect.setFanSpeed(1, slow_mode_speed);
			
			try {
				Thread.sleep(5000);
			} catch (Exception e) {
				// TODO: handle exception
			}
			
			uiHandler.sendEmptyMessage(1);
			
			int count = 0;
			int speed = 0;
			int realSpeed = 0;
			int _speed = 0;
			
			//calculate fan1 low speed
			for(int i = 0; i < 10; ++i){
				_speed = fanInspect.getFanSpeed(0);
				realSpeed += _speed;
				if(_speed >= slow_mode_lower_bound && _speed <= slow_mode_upper_bound){
					++count;
					speed += _speed;
				}
				
				try {
					Thread.sleep(250);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			fan1LowSpeed = (count >= 5) ? speed/count : realSpeed/10;
			LogUtil.d(TAG, "count = "+count+" fan1LowSpeed = "+fan1LowSpeed);
			
			//calculate fan2 low speed
			count = 0;
			speed = 0;
			realSpeed = 0;
			
			for(int i = 0; i < 10; ++i){
				_speed = fanInspect.getFanSpeed(1);
				realSpeed += _speed;
				if(_speed >= slow_mode_lower_bound && _speed <= slow_mode_upper_bound){
					++count;
					speed += _speed;
				}
				
				try {
					Thread.sleep(250);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			fan2LowSpeed = (count >= 5) ? speed/count : realSpeed/10;
			LogUtil.d(TAG, "count = "+count+" fan2LowSpeed = "+fan2LowSpeed);
			
			//high speed
			uiHandler.sendEmptyMessage(2);
			
			fanInspect.setFanSpeed(0, fast_mode_speed);
			fanInspect.setFanSpeed(1, fast_mode_speed);
			
			try {
				Thread.sleep(5000);
			} catch (Exception e) {
				// TODO: handle exception
			}
			
			uiHandler.sendEmptyMessage(1);
			
			//calculate fan1 high speed
			count = 0;
			speed = 0;
			realSpeed = 0;
			
			for(int i = 0; i < 10; ++i){
				_speed = fanInspect.getFanSpeed(1);
				realSpeed += _speed;
				if(_speed >= fast_mode_lower_bound && _speed <= fast_mode_upper_bound){
					++count;
					speed += _speed;
				}
				
				try {
					Thread.sleep(250);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			fan1HighSpeed = (count >= 5) ? speed/count : realSpeed/10;
			LogUtil.d(TAG, "count = "+count+" fan1HighSpeed = "+fan1HighSpeed);
			
			//calculate fan1 high speed
			count = 0;
			speed = 0;
			realSpeed = 0;
			
			for(int i = 0; i < 10; ++i){
				_speed = fanInspect.getFanSpeed(1);
				realSpeed += _speed;
				if(_speed >= fast_mode_lower_bound && _speed <= fast_mode_upper_bound){
					++count;
					speed += _speed;
				}
				
				try {
					Thread.sleep(250);
				} catch (InterruptedException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
			
			fan2HighSpeed = (count >= 5) ? speed/count : realSpeed/10;
			LogUtil.d(TAG, "count = "+count+" fan2HighSpeed = "+fan2HighSpeed);
			
			fanInspect.setFanSpeed(0, slow_mode_speed);
			fanInspect.setFanSpeed(1, slow_mode_speed);
			
			uiHandler.sendEmptyMessage(0);
		}
		
	}
}
