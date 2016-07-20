package com.utsmta.mstar;

import android.app.Activity;
import android.content.ComponentName;
import android.content.Intent;
import android.view.View;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;

import com.utsmta.app.R;
import com.utsmta.common.MtaPagerView;

public class MstarWhiteBalanceView extends MtaPagerView {

	private static final String TAG = "MstarWhiteBalanceView";
	
	Button starWBn = null;
	
	private View.OnClickListener onAutoWbcListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View view) {
			// TODO Auto-generated method stub
			 ComponentName mComp = new ComponentName("com.utsEosWB.app", "com.utsEosWB.app.SurfaceViewPlayVideo");
			 Intent intent = new Intent();
			 intent.setComponent(mComp);
			 intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
			 activity.startActivity(intent);		
		}
	};  

	@Override
	protected View onCreateView(android.view.LayoutInflater inflater) {
		LinearLayout linearLayout = new LinearLayout(activity);
		linearLayout.setOrientation(LinearLayout.VERTICAL);
		linearLayout.setLayoutParams(new LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT));
		linearLayout.setBackgroundResource(R.drawable.blank_bg);
		
		starWBn = new Button(activity);
		starWBn.setText(activity.getString(R.string.start_wb_str));
		starWBn.setFocusable(true);
		starWBn.setClickable(true);
		starWBn.setLayoutParams(new LayoutParams(
				LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		starWBn.setOnClickListener(onAutoWbcListener);
		linearLayout.addView(starWBn);	
		
			
		return linearLayout;		
	};
	
	public MstarWhiteBalanceView(Activity activity) {
		super(activity);
		// TODO Auto-generated constructor stub
	}
}
