package com.utsmta.mstar;

import android.content.Context;
import android.os.IPowerManager;
import android.os.RemoteException;
import android.os.ServiceManager;


import com.mstar.android.tvapi.common.TvManager;
import com.mstar.android.tvapi.common.exception.TvCommonException;
import com.mstar.android.tvapi.common.vo.MuteType.EnumMuteType;
import com.utsmta.common.IToolKit;
import com.utsmta.utils.LogUtil;



import com.eostek.tm.cpe.manager.CpeManager;

public class MstarToolKit implements IToolKit {

	@Override
	public boolean setSerialNumber(String serialNo) {	
		// TODO Auto-generated method stub
		try {
			TvManager.getInstance().setEnvironment("serid", serialNo);
		} catch (TvCommonException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		
		return true;
	}

	@Override
	public String getSerialNumber() {
		// TODO Auto-generated method stub
		String serialNo = null;
		try {
			serialNo = TvManager.getInstance().getEnvironment("serid");
		} catch (TvCommonException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return serialNo;
	}

	@Override
	public boolean setMacAddress(String macAddress) {	
		// TODO Auto-generated method stub
        try {
        	TvManager.getInstance().setEnvironment("macaddr", macAddress);
        	TvManager.getInstance().setEnvironment("ethaddr", macAddress);         
        } catch (TvCommonException e) {
            e.printStackTrace();
            return false;
        }
        
		return true;
	}

	@Override
	public String getMacAddress() {
		// TODO Auto-generated method stub
		String macAddress = null;
		
        try {
        	macAddress = TvManager.getInstance().getEnvironment("macaddr");        
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        
		return macAddress;
	}

	@Override
	public void shutdownSystem() {
		// TODO Auto-generated method stub
       // IPowerManager pm = IPowerManager.Stub.asInterface(ServiceManager.getService(Context.POWER_SERVICE));
       // try {
       //     pm.shutdown(false, true);
       // } catch (RemoteException e) {
       //    e.printStackTrace();
       // }
		try {
			TvManager.getInstance().getAudioManager().enableMute(EnumMuteType. E_MUTE_ALL );
			TvManager.getInstance().getPictureManager().disableBacklight();
			TvManager.getInstance().setGpioDeviceStatus(36, false);
			TvManager.getInstance().setGpioDeviceStatus(38, true);
			TvManager.getInstance().enterSleepMode(true, false);
		} catch (Throwable e) {
			e.printStackTrace();
		}  	

	}

	@Override
	public String getFetureCode() {
		// TODO Auto-generated method stub
		return CpeManager.getInstance().getProductClass();
	}

}
