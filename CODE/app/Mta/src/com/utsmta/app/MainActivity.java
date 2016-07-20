package com.utsmta.app;

import java.util.ArrayList;

import com.utsmta.common.ConfigManager;
import com.utsmta.common.FactoryFragment;
import com.utsmta.common.FactoryGroup;
import com.utsmta.common.FactoryItem;
import com.utsmta.utils.LogUtil;
import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.app.FragmentTransaction;
import android.content.Context;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class MainActivity extends Activity {
	private static final String TAG = "MainActivity";
	
	private ConfigManager configManager = null;
	
	private int containerViewId = 0;
	
	private ArrayList<FactoryFragment> fragments = new ArrayList<FactoryFragment>();
	
	private int curFragmentIndex = -1;
	boolean amta = false;
	
	private TextView systemVersionTv = null;
	
	private TextView mtaVersionTv = null;
	
	private TextView macAddressTv = null;
	
	private TextView serialNoTv = null;
	
	private ImageView hdcpFlagIv = null;	
	
	private ImageView hdcp_2_2_FlagIv = null;
	
	private LinearLayout hdcpLayout = null;
	
	private LinearLayout hdcp_2_2_Layout = null;
	
	private TextView hdcpTv = null;

	public void setMacAddress(String mac){
		macAddressTv.setText(mac);
	}
	
	public void setSerialNo(String serialNo){
		serialNoTv.setText(serialNo);
	}
	
	public void setHdcpFlag(boolean positive){
		if(positive){
			hdcpFlagIv.setImageResource(R.drawable.checked);
		}else{
			hdcpFlagIv.setImageResource(R.drawable.alert);
		}
	}
	
	public void setHdcp_2_2_Flag(boolean positive){
		if(positive){
			hdcp_2_2_FlagIv.setImageResource(R.drawable.checked);
		}else{
			hdcp_2_2_FlagIv.setImageResource(R.drawable.alert);
		}
	}
	
	public void setHdcpLabelText(String text){
		hdcpTv.setText(text);
	}
	
	public String getSystemVersion(){
		return android.os.Build.VERSION.INCREMENTAL;
	}
	
	public String getMtaVersion(){
		try {
			return getPackageManager().getPackageInfo(getApplicationContext().getPackageName(), 0).versionName;
		} catch (NameNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return null;
		}
	}
	
	public int containerViewId(){
		return containerViewId;
	}
	
	public void setSystemVersionDisplayValue(String text){
		systemVersionTv.setText(text);
	}
	
	public boolean showNextFragment(){
		int nextFragmentIndex = curFragmentIndex+1;
		
		if(curFragmentIndex >= 0 && nextFragmentIndex < fragments.size()){
			FactoryFragment curFragment  = fragments.get(curFragmentIndex);
			FactoryFragment nextFragment = fragments.get(nextFragmentIndex);
			getFragmentManager()
				.beginTransaction()
				.show(nextFragment)
				.hide(curFragment)
				.commit();
			
			curFragmentIndex = nextFragmentIndex;
			return true;
		}
		
		return false;
	}
	
	public boolean showPrevFragment(){
		int prevFragmentIndex = curFragmentIndex-1;
		
		if(curFragmentIndex < fragments.size() && prevFragmentIndex >= 0){
			FactoryFragment curFragment  = fragments.get(curFragmentIndex);
			FactoryFragment prevFragment = fragments.get(prevFragmentIndex);
			getFragmentManager()
				.beginTransaction()
				.show(prevFragment)
				.hide(curFragment)
				.commit();
			
			curFragmentIndex = prevFragmentIndex;
			return true;
		}
		
		return false;
	}

	private void initFragmentList(){
		curFragmentIndex = -1;
		fragments.clear();
		
		FactoryFragment fragment = null;
		ArrayList<FactoryGroup> groups = configManager.getAllGroups();
		
		for (FactoryGroup group : groups){
			LogUtil.d(TAG, "find group : "+group.getName());
			fragment = configManager.getFactoryFragment(group.getName());
			if(fragment != null){
				fragments.add(fragment);
			}			
		}
	}
	
	private void showFirstFragment(){	
		LogUtil.d(TAG, "showFirstFragment");
		if(fragments.size() > 0){
			curFragmentIndex = 0;
			getFragmentManager().beginTransaction()
				.add(containerViewId, fragments.get(curFragmentIndex))
				.commit();
		}		
	}
	
	private void initOtherFragments(){
		LogUtil.d(TAG, "initOtherFragments");
		FragmentTransaction ft = getFragmentManager().beginTransaction();
		FactoryFragment fragment = null;
		
		for (int i = 1; i < fragments.size(); ++i) {
			fragment = fragments.get(i);
			ft.add(containerViewId, fragment);
			ft.hide(fragment);
		}
		
		ft.commit();		
	}
	
	protected void initViews(){
		systemVersionTv = (TextView) findViewById(R.id.sys_version_name);
		mtaVersionTv = (TextView) findViewById(R.id.mta_version);
		macAddressTv = (TextView) findViewById(R.id.mac_addr);
		serialNoTv = (TextView) findViewById(R.id.serial_value);
		hdcpFlagIv = (ImageView) findViewById(R.id.hdcp_key_name);
		hdcp_2_2_FlagIv = (ImageView) findViewById(R.id.hdcp_key_2_2_name);		
		hdcpLayout = (LinearLayout) findViewById(R.id.hdcp_key_ly);		
		hdcp_2_2_Layout = (LinearLayout) findViewById(R.id.hdcp_key_2_2_ly);	
		
		hdcpTv = (TextView) findViewById(R.id.hdcp_tv);
		
		systemVersionTv.setText(getSystemVersion());

		mtaVersionTv.setText(getMtaVersion());
		macAddressTv.setText(MtaApplication.getDevice().getToolKit().getMacAddress());
		serialNoTv.setText(MtaApplication.getDevice().getToolKit().getSerialNumber());
		
		containerViewId = R.id.fg_container;
	}
	
	protected void initHdcpKey(){
		FactoryGroup snGroup = configManager.getFactoryGroup("sn");
		
		if(amta){
		  hdcpLayout.setVisibility(View.VISIBLE);	
		}
		if(snGroup != null){
			FactoryItem hdcpItem = snGroup.getItem("hdcp_key");
			if(hdcpItem != null){
				if("true".equals(hdcpItem.getProperty("1.4"))){
					hdcpLayout.setVisibility(View.VISIBLE);
				}
				
				if("true".equals(hdcpItem.getProperty("2.2"))){
					hdcp_2_2_Layout.setVisibility(View.VISIBLE);
				}
			}
		}	
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		LogUtil.d(TAG, "onCreate");
		
		setContentView(R.layout.main_new);	
		
		Bundle extras = getIntent().getExtras();
		
		
		if(extras != null){
			amta = extras.getBoolean("amta");		
		}
		
		configManager = MtaApplication.getConfigManager();
		
		if(amta){
			MtaApplication.getDevice().activeAmtaMode(true);
			configManager.parseAmtaConfig(MtaApplication.getDevice());
		}else{
			configManager.parseConfig(MtaApplication.getDevice());
		}		
		
		initViews();
		
		MtaApplication.getDevice().onActivityCreate(this);
		
		initHdcpKey();
		
		initFragmentList();
		
		showFirstFragment();
		
		initOtherFragments();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		// TODO Auto-generated method stub
		if(keyCode == KeyEvent.KEYCODE_BACK){
			if(showPrevFragment()){
				return true;
			}
		}
		
		return super.onKeyDown(keyCode, event);
	}
	
	private void killMta(){
		ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		String mtaPackageName = getPackageName();
		for(RunningAppProcessInfo info:am.getRunningAppProcesses()){
			if(mtaPackageName.equalsIgnoreCase(info.processName)){
				android.os.Process.killProcess(info.pid);
			}
		}
	}
	
	@Override
	protected void onDestroy() {
		// TODO Auto-generated method stub
		LogUtil.d(TAG, "onDestroy");
		super.onDestroy();

		killMta();	
	}
	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		LogUtil.d(TAG, "onStart");
	}
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		LogUtil.d(TAG, "onStop");		
		super.onStop();
	}
	
	@Override
	protected void onResume() {
		// TODO Auto-generated method stub
		super.onResume();
		LogUtil.d(TAG, "onResume");
	}
	
	@Override
	protected void onPause() {
		// TODO Auto-generated method stub
		LogUtil.d(TAG, "onPause");
		super.onPause();
	}
	
	@Override
	protected void onSaveInstanceState(Bundle outState) {
		// TODO Auto-generated method stub
		super.onSaveInstanceState(outState);
	}
}
