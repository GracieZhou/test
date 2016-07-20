
package com.eostek.mkeyeventservice;

import com.mstar.android.tv.TvFactoryManager;
import com.mstar.android.tvapi.factory.vo.EnumAcOnPowerOnMode;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.provider.Settings;
import scifly.view.KeyEventExtra;
import android.util.Log;

import java.util.Calendar;

public class MKeyEventService extends Service {

    MKeyEventHolder mHolder;
	
	public static final int MIN_CLICK_DELAY_TIME = 1000;
	
    private long lastClickTime = 0;

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @SuppressWarnings("deprecation")
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
		
		long currentTime = Calendar.getInstance().getTimeInMillis();
		if(currentTime - lastClickTime < MIN_CLICK_DELAY_TIME){
			Log.d("laird","quick key");
			return 0;
		}else{
			lastClickTime = currentTime;
			Log.d("laird","normal key");
		}
        int KeyCode = intent.getIntExtra("keyCode", 0);
        if (Settings.System.getInt(getContentResolver(), "MModel", 0) == 0
                && (KeyCode != KeyEventExtra.KEYCODE_MSTAR_EXIT && KeyCode != KeyEventExtra.KEYCODE_MSTAR_MENU
                        && KeyCode != KeyEventExtra.KEYCODE_MSTAR_SOURCE
                        && KeyCode != KeyEventExtra.KEYCODE_MSTAR_INFO)) {
            if (mHolder != null) {
                mHolder.removeLogo();
                BurnInThreadRunnable.getInstance(this).setClose(true);
            }
            TvFactoryManager.getInstance().setPowerOnMode(EnumAcOnPowerOnMode.E_ACON_POWERON_SECONDARY);
            stopSelf();
        } else if (Settings.System.getInt(getContentResolver(), "MModel", 0) == 1
                || (KeyCode == KeyEventExtra.KEYCODE_MSTAR_EXIT || KeyCode == KeyEventExtra.KEYCODE_MSTAR_MENU
                        || KeyCode == KeyEventExtra.KEYCODE_MSTAR_SOURCE
                        || KeyCode == KeyEventExtra.KEYCODE_MSTAR_INFO)) {
            if (mHolder == null) {
                mHolder = new MKeyEventHolder(getApplicationContext());
            }
			if(BurnInThreadRunnable.getInstance(this).isClose()){
				Log.d("laird","start BurnInThreadRunnable");
				BurnInThreadRunnable.getInstance(this).setClose(false);
				new Thread(BurnInThreadRunnable.getInstance(this)).start();				
			}
            if (Settings.System.getInt(getContentResolver(), "MModel", 0) == 1) {
                mHolder.showLogo();
            }
            mHolder.handlerMKeyEvent(KeyCode);
        }
        return super.onStartCommand(intent, flags, startId);
    }
}
