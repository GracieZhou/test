package com.utsmta.mstar;

import android.app.Activity;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.utsmta.app.R;
import com.utsmta.common.MtaPagerView;
import com.utsmta.mstar.inspect.MstarFanSpeedInspect;
import com.utsmta.mstar.inspect.MstarNtcSensorInspect;

public class MstarNtcTemperatureView extends MtaPagerView {
	private TextView ntc1 = null;
	
	private TextView ntc2 = null;
	
	private TextView ntc3 = null;
	
	private MstarNtcSensorInspect inspect = new MstarNtcSensorInspect();
	
	Handler uiHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			if(msg.what == 0){
				updateNtcValue();
				uiHandler.sendEmptyMessageDelayed(0, 1500);
			}
		};
	};
	
	protected void updateNtcValue(){
		ntc1.setText(activity.getString(R.string.ntc1_str)+" :  " + inspect.getTemperature(1));
		ntc2.setText(activity.getString(R.string.ntc2_str)+" :  " + inspect.getTemperature(2));
		ntc3.setText(activity.getString(R.string.ntc3_str)+" :  " + inspect.getTemperature(3));
	}
	
	public MstarNtcTemperatureView(Activity activity) {
		super(activity);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected View onCreateView(LayoutInflater inflater) {
		// TODO Auto-generated method stub
		View contentView = inflater.inflate(R.layout.ntc_temperature, null, false);

		ntc1 = (TextView) contentView.findViewById(R.id.ntc1_id);
		ntc2 = (TextView) contentView.findViewById(R.id.ntc2_id);
		ntc3 = (TextView) contentView.findViewById(R.id.ntc3_id);
				
		return contentView;
	}
	
	@Override
	protected void onShown() {
		// TODO Auto-generated method stub
		uiHandler.sendEmptyMessageDelayed(0, 600);
	}
	
	@Override
	protected void onHiden() {
		// TODO Auto-generated method stub
		uiHandler.removeMessages(0);
	}
}
