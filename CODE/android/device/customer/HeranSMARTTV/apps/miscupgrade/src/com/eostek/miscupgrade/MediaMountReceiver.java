package com.eostek.miscupgrade;

import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;


import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;

public class MediaMountReceiver extends BroadcastReceiver {
	private final static String TAG = UpgradeConstants.TAG;

	@Override
	public void onReceive(Context context, Intent intent) {
		// TODO Auto-generated method stub
		Log.i(TAG, intent.toString());

		String action = intent.getAction();
		Uri uri = intent.getData();
		Log.i(TAG, "MediaMountReceiver" + uri);
		if (Intent.ACTION_MEDIA_MOUNTED.equals(action)) {
			if (Util.hasExternalUpdateFile(uri)
			 && Util.checkAllFileMD5(context)
			) {
				 Log.d(TAG, "Name : UdiskUpgrade, Version : 1.0.0, Date : 2015-1-27, Publisher : Michael.Zhang, Revision : ");
//				Intent intent1 = new Intent(context, UpgradeActivity.class);
//				intent1.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
//				context.startActivity(intent1);
				 Intent intent1= new Intent("com.intent.action.UPGRADESERVICE");
				 context.startService(intent1);
			}

			/*
			 * Bundle args = new Bundle(); args.putString("path", path);
			 * args.putStringArray("files", files); context.startService(new
			 * Intent(context, UdiskUpgradeService.class).putExtras(args));
			 */
		}
	}
}
