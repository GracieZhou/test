package com.utsmta.mstar.amta;


import android.app.Activity;
import android.app.ActivityManager;
import android.app.ActivityManager.RunningAppProcessInfo;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import com.utsmta.app.MainActivity;

public class AssembleMtaActivity extends Activity {

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		// TODO Auto-generated method stub
		super.onCreate(savedInstanceState);
		
		ActivityManager am = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
		String mtaPackageName = getPackageName();
		for(RunningAppProcessInfo info:am.getRunningAppProcesses()){
			if(mtaPackageName.equalsIgnoreCase(info.processName)){
				android.os.Process.killProcess(info.pid);
			}
		}
		
		try {
			Thread.sleep(1000);
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		Intent intent = new Intent(this, MainActivity.class);
		Bundle extras = new Bundle();
		extras.putBoolean("amta", true);
		intent.putExtras(extras);
		intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
		startActivityForResult(intent, 0);	
	}
	
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data) {
		// TODO Auto-generated method stub
		super.onActivityResult(requestCode, resultCode, data);
		finish();
	}
}
