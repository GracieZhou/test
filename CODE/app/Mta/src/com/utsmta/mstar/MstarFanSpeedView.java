package com.utsmta.mstar;

import android.app.Activity;
import android.os.Handler;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.utsmta.app.R;
import com.utsmta.common.MtaPagerView;
import com.utsmta.mstar.inspect.MstarFanSpeedInspect;

public class MstarFanSpeedView extends MtaPagerView {
	private TextView fan1Tv = null;
	
	private TextView fan2Tv= null;
	
	private MstarFanSpeedInspect inspect = new MstarFanSpeedInspect();
	
	Handler uiHandler = new Handler(){
		public void handleMessage(android.os.Message msg) {
			if(msg.what == 0){
				updateSpeedValue();
				uiHandler.sendEmptyMessageDelayed(0, 1500);
			}
		};
	};
	
	protected void updateSpeedValue(){
		fan1Tv.setText(activity.getString(R.string.fan1_speed_str)+" :  " + inspect.getFanSpeed(0));
		fan2Tv.setText(activity.getString(R.string.fan2_speed_str)+" :  " + inspect.getFanSpeed(1));
	}
	
	public MstarFanSpeedView(Activity activity) {
		super(activity);
		// TODO Auto-generated constructor stub
	}

	@Override
	protected View onCreateView(LayoutInflater inflater) {
		// TODO Auto-generated method stub
		View contentView = inflater.inflate(R.layout.fan_speed, null, false);

		fan1Tv = (TextView) contentView.findViewById(R.id.fan1_id);
		fan2Tv = (TextView) contentView.findViewById(R.id.fan2_id);
				
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
