package com.utsmta.utils;

import android.util.Log;

public class LogUtil {
	private static boolean gDebug = true;
	
	public static void enableDebug(boolean bEnable){
		gDebug = bEnable;
	}
	
	public static void d(String tag, String msg){
		if(gDebug){
			Log.d("Mta::"+tag, "|%%####		"+msg+"		####%%|");
		}
	}
	
	public static void e(String tag, String msg){
		Log.e("Mta::"+tag, "|%%####		"+msg+"		####%%|");
	}
}
