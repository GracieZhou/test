package com.utsmta.mstar;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.regex.Pattern;

import android.app.Activity;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;

import com.utsmta.app.R;
import com.utsmta.common.FactoryInspect;
import com.utsmta.common.FactoryItem;
import com.utsmta.common.ItemPagerView;
import com.utsmta.mstar.inspect.MstarTouchPadInspect;
import com.utsmta.utils.LogUtil;

public class MstarTouchPadView extends ItemPagerView {
	private final String TAG = "MstarTouchPadSubView";
	
	private ArrayList<Integer> tagList = new ArrayList<Integer>();
	
	private ArrayList<Button> btnList = new ArrayList<Button>();
	
	private MstarTouchPadInspect touchPadInspect = new MstarTouchPadInspect();
	
	public MstarTouchPadView(Activity activity, FactoryItem item) {
		super(activity, item);
		// TODO Auto-generated constructor stub
		initTagList(7);
	}

	private void initTagList(int num){
		tagList.clear();
		for(int i=0; i<num; ++i){
			tagList.add(0);
		}
	}
	
	private FactoryInspect.InspectResultListener touchInspectResultListener = new FactoryInspect.InspectResultListener(){

		@Override
		public void onResultUpdate(boolean passed, int error, Bundle extra) {
			// TODO Auto-generated method stub
			int key = extra.getInt("key");
			LogUtil.d(TAG, "key = "+key);
			int index = -1;
			switch (key) {
			case 1:
				index = 0;
				break;
			case 2:
				index = 2;
				break;
			case 3:
				index =1;
				break;
			case 4:
				index = 3;
				break;
			case 5:
				index = 5;
				break;
			case 6:
				index = 6;
				break;
			case 7:
				index = 4;
				break;
			default:
				break;
			}
			
			if(index >= 0 && index <=btnList.size()-1){
				Button button = btnList.get(index);				
				Integer count = tagList.get(index);
				if(count < 3){
					button.performClick();
					count++;
				}
				
				tagList.set(index, count);
				checkAllButtons();
			}
		}
		
	};
	
	@Override
	public void onShown() {
		// TODO Auto-generated method stub
		touchPadInspect.registerResultListener(touchInspectResultListener);
		touchPadInspect.startInspect();
	}
	
	@Override
	public void onHiden() {
		// TODO Auto-generated method stub	
		touchPadInspect.stopInspect();
		touchPadInspect.unregisterResultListener(touchInspectResultListener);
	}
	
	@Override
	protected View onCreateView(LayoutInflater inflater) {
		// TODO Auto-generated method stub
		LogUtil.d(TAG, "onCreateView");
		
		killKeypadProcess();

		View contentView = inflater.inflate(R.layout.touchpad, null, false);
		
		Button btn1 = (Button) contentView.findViewById(R.id.btn1);
		Button btn2 = (Button) contentView.findViewById(R.id.btn2);
		Button btn3 = (Button) contentView.findViewById(R.id.btn3);
		Button btn4 = (Button) contentView.findViewById(R.id.btn4);
		Button btn5 = (Button) contentView.findViewById(R.id.btn5);
		Button btn6 = (Button) contentView.findViewById(R.id.btn6);
		Button btn7 = (Button) contentView.findViewById(R.id.btn7);
		
		btn1.setFocusable(false);
		btn1.setClickable(false);
		
		btn2.setFocusable(false);
		btn2.setClickable(false);
		
		btn3.setFocusable(false);
		btn3.setClickable(false);
		
		btn4.setFocusable(false);
		btn4.setClickable(false);
		
		btn5.setFocusable(false);
		btn5.setClickable(false);
		
		btn6.setFocusable(false);
		btn6.setClickable(false);
		
		btn7.setFocusable(false);
		btn7.setClickable(false);
		
//		LinearLayout mainLayout = new LinearLayout(context);
//		android.view.ViewGroup.LayoutParams mainLp = new android.view.ViewGroup.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
//		mainLayout.setLayoutParams(mainLp);
//		mainLayout.setGravity(Gravity.CENTER);
//		mainLayout.setOrientation(LinearLayout.VERTICAL);
//		Button btn3 = new Button(context);
//		btn3.setBackgroundResource(R.drawable.circle_button);
//		btn3.setText(R.string.num_3);
//		btn3.setClickable(false);
//		btn3.setFocusable(false);
//		LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
//		mainLayout.addView(btn3,layoutParams);
//		
//		LinearLayout middleBtnGroup = new LinearLayout(context);	
//		middleBtnGroup.setGravity(Gravity.CENTER);
//		middleBtnGroup.setOrientation(LinearLayout.HORIZONTAL);
//		
//		Button btn1 = new Button(context);
//		btn1.setClickable(false);
//		btn1.setFocusable(false);
//		btn1.setText(R.string.num_1);
//		btn1.setBackgroundResource(R.drawable.circle_button);	
//		
//		Button btn2 = new Button(context);
//		btn2.setClickable(false);
//		btn2.setFocusable(false);
//		btn2.setText(R.string.num_2);
//		btn2.setBackgroundResource(R.drawable.circle_button);
//		
//		Button btn4 = new Button(context);
//		btn4.setClickable(false);
//		btn4.setFocusable(false);
//		btn4.setText(R.string.num_4);
//		btn4.setBackgroundResource(R.drawable.circle_button);
//		
//		Button btn6 = new Button(context);
//		btn6.setClickable(false);
//		btn6.setFocusable(false);
//		btn6.setText(R.string.num_6);
//		btn6.setBackgroundResource(R.drawable.circle_button);
//		
//		Button btn7 = new Button(context);
//		btn7.setClickable(false);
//		btn7.setFocusable(false);
//		btn7.setText(R.string.num_7);
//		btn7.setBackgroundResource(R.drawable.circle_button);
//		middleBtnGroup.addView(btn1, layoutParams);
//		middleBtnGroup.addView(btn2, layoutParams);
//		middleBtnGroup.addView(btn4, layoutParams);
//		middleBtnGroup.addView(btn6, layoutParams);
//		middleBtnGroup.addView(btn7, layoutParams);
//		mainLayout.addView(middleBtnGroup, layoutParams);
//		
//		Button btn5 = new Button(context);
//		btn5.setClickable(false);
//		btn5.setFocusable(false);
//		btn5.setText(R.string.num_5);
//		btn5.setBackgroundResource(R.drawable.circle_button);
//		mainLayout.addView(btn5, layoutParams);
		
		btnList.clear();
		btnList.add(btn1);
		btnList.add(btn2);
		btnList.add(btn3);
		btnList.add(btn4);
		btnList.add(btn5);
		btnList.add(btn6);
		btnList.add(btn7);
		
		checkAllButtons();
		return contentView;
	}
	
	private void checkAllButtons(){
		int count = 0;
		boolean allPassed = true;
		
		for(int i=0; i<tagList.size(); ++i){
			count = tagList.get(i);
			if(count < 3){
				allPassed = false;
			}else{
				btnList.get(i).setBackgroundResource(R.drawable.circle_button_pressed);
			}
		}
		
		if(allPassed){
			item.setResult(true);
			notifyUiUpdate();
		}
	}
	
	public void killKeypadProcess(){
		BufferedReader reader = null;
		try {
			Process suProcess = Runtime.getRuntime().exec("ps");
			reader = new BufferedReader(new InputStreamReader(suProcess.getInputStream()));
						
			suProcess.waitFor();
			
			if(reader.ready()){
				String retStr = null;
				while((retStr = reader.readLine()) != null){
					if(retStr.contains("/system/bin/virtualtouchkeypad")){
						Pattern pattern = Pattern.compile("[\\s]+");
						String parts[] = pattern.split(retStr);
						if(parts.length > 1){
							try {
								int pid = Integer.valueOf(parts[1]);
								LogUtil.d(TAG, "pid = "+pid);
								android.os.Process.killProcess(pid);
							} catch (NumberFormatException e){
								
							}
						}
					}
				}
			}

		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			LogUtil.d(TAG, "killKeypadProcess Error:"+e.getLocalizedMessage());
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{
			if(reader != null){
				try {
					reader.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		}
	}
}
