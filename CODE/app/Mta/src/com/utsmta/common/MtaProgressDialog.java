package com.utsmta.common;

import android.app.ProgressDialog;
import android.content.Context;

public class MtaProgressDialog {
	private final String TAG = "MtaProgressDialog";
	
	private static ProgressDialog progressDialog = null;
	
	public static void show(Context context, String title, String message){
		if(progressDialog != null && progressDialog.isShowing()){
			progressDialog.dismiss();
		}
		
		progressDialog = ProgressDialog.show(
				context, title, message, true, false);
	}
	
	public static void dismiss(){
		if(progressDialog != null && progressDialog.isShowing()){
			progressDialog.dismiss();
		}
	}
	
	public static ProgressDialog getProgressDialog(){
		return progressDialog;
	}
}
