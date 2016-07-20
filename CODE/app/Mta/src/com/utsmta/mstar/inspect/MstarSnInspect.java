package com.utsmta.mstar.inspect;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;

import android.content.ContentValues;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import com.mstar.android.tvapi.common.TvManager;
import com.mstar.android.tvapi.common.exception.TvCommonException;
import com.mstar.android.tvapi.factory.FactoryManager;
import com.utsmta.common.FactoryInspect;
import com.utsmta.utils.LogUtil;
import com.utsmta.utils.MtaUtils;

public class MstarSnInspect extends FactoryInspect {
	private static final String TAG = "MstarSnInspect";
	
	public static final String MTA_DB_FILE = "EOS_MTA.db";
	
	public static final String MTA_HDCP_DB_FILE = "EOS_MTA_HDCP.db";
	
	public static final String MAC_TABLE_NAME = "MTA_MAC_KEY";
	
	//error code
	public static final int ERROR_NONE				= 0x00;
	
	public static final int ERROR_INVALID_MAC 		= 0x01;
	
	public static final int ERROR_DB_NOT_FOUND 		= 0x02;
	
	public static final int ERROR_DB_CANNOT_OPEN 	= 0x03;
	
	public static final int ERROR_DB_READ_EXCEPTION = 0x04;
	
	public static final int ERROR_DB_WRITE_EXCEPTION= 0x05;
	
	public static final int ERROR_MAC_NOT_EXIST = 0x06;
	
	public static final int ERROR_MAC_ALREADY_USED = 0x07;
	
	public static final int ERROR_UPDATE_FAILED = 0x08;
	
	public static final int ERROR_NO_HDCP_1_4_DATA = 0x09;
	
	public static final int ERROR_NO_HDCP_2_2_DATA = 0x0A;
	
	public static final int ERROR_BURN_HDCP_1_4_FAILED = 0x0B;
	
	public static final int ERROR_BURN_HDCP_2_2_FAILED = 0x0C;
	
	public static final int ERROR_UNKNOWN = 0x0D;

	public static final int HDCP_KEY_REBURN_OK			= 0x0F;
	
	private boolean hdcpKey_1_4 = false;
	
	private boolean hdcpKey_2_2 = false;
	
	public void configHdcpKey_1_4(boolean bEnable){
		hdcpKey_1_4 = bEnable;
	}
	
	public void configHdcpKey_2_2(boolean bEnable){
		hdcpKey_2_2 = bEnable;
	}
	
	public void updateSerialNumberAndMacAddress(String mac){
		if( mac == null || mac.length() != 12 || !mac.matches("^[0-9A-Fa-f]+$") ){
			//invalid MAC address
			updateInspectResult(false, ERROR_INVALID_MAC, null);
			return ;
		}
		
		String dbPath = getDatabasePath();
		SQLiteDatabase database = null;
		
		if( dbPath == null ){
			//can't find database file
			updateInspectResult(false, ERROR_DB_NOT_FOUND, null);
			return ;
		}
		
		try {
			try {
				database = SQLiteDatabase.openDatabase(
						dbPath, null, SQLiteDatabase.OPEN_READWRITE);
			} catch (Exception e) {
				// TODO: handle exception
				
			}

			if( database == null ){
				//open database with read_write permission failed
				updateInspectResult(false, ERROR_DB_CANNOT_OPEN, null);
				return ;
			}
			
			int flag  = 0;
			String sn = null;
			Cursor cursor = null;
		    byte[] hdcp_1_4 	= null;
		    String hdcp_1_4_md5 = null;
		    byte[] hdcp_2_2 	= null;
		    String hdcp_2_2_md5 = null;
		    
			String args[] = {
				mac
			};
			
			try {
				cursor = database.rawQuery(
						"select FLAG, SN from " + MAC_TABLE_NAME + " where MAC = ?", 
						args);
			} catch (Exception e) {
				// TODO: handle exception
				//read database failed, maybe some column don't exist
				updateInspectResult(false, ERROR_DB_READ_EXCEPTION, null);
				return ;
			}
			
			if( cursor == null || !cursor.moveToNext() ){
				//can't find the specific MAC in database
				updateInspectResult(false, ERROR_MAC_NOT_EXIST, null);
				return ;
			}
			
			flag = cursor.getInt(0);
			sn   = cursor.getString(1);
			
			if( flag == 1 ){
				//the record has been used
				updateInspectResult(false, ERROR_MAC_ALREADY_USED, null);
				return ;		
			}
			
			if(hdcpKey_1_4){
				try {
					cursor = database.rawQuery("select KEY, MD5 from MTA_MAC_KEY" + " where MAC = ?", args);
				} catch (Exception e) {
					// TODO: handle exception
				}
				
		        if(cursor != null && cursor.moveToNext()){
		        	hdcp_1_4 = cursor.getBlob(0);
//		        	hdcp_1_4_md5 = cursor.getString(1);
		        	if(hdcp_1_4 == null || hdcp_1_4.length < 289){
		        		updateInspectResult(false, ERROR_NO_HDCP_1_4_DATA, null);
		        		return ;
		        	}
		        	
		        	hdcp_1_4_md5 = MtaUtils.createHexMD5(hdcp_1_4);
		        	if(hdcp_1_4_md5 == null){
		        		updateInspectResult(false, ERROR_NO_HDCP_1_4_DATA, null);
		        		return ;
		        	}
					
		        	LogUtil.d(TAG, "hdcp_1_4 = "+hdcp_1_4+" hdcp_1_4_md5 = "+hdcp_1_4_md5);
					if(!setHdcpKey(MtaUtils.convertByteArrayToShortArray(hdcp_1_4))){
						updateInspectResult(false, ERROR_BURN_HDCP_1_4_FAILED, null);
						return ;
					}
					
					short[] checkSum = getHdcpKey(289);
					if(null == checkSum && !hdcp_1_4_md5.equals(MtaUtils.createHexMD5(
							MtaUtils.convertShortArrayToByteArray(checkSum)))){
						updateInspectResult(false, ERROR_BURN_HDCP_1_4_FAILED, null);
						return ;
					}
		        }else{
		        	updateInspectResult(false, ERROR_NO_HDCP_1_4_DATA, null);
		        	return;
		        }
			}
			
			if(hdcpKey_2_2){
				try {
					cursor = database.rawQuery("select KEY_V2, MD5_V2 from MTA_MAC_KEY" + " where MAC = ?", args);
				} catch (Exception e) {
					// TODO: handle exception
				}
				
		        if(cursor != null && cursor.moveToNext()){
		        	hdcp_2_2 = cursor.getBlob(0);
//		        	hdcp_2_2_md5 = cursor.getString(1);  

				if(hdcp_2_2 == null || hdcp_2_2.length < 1044){
		        		updateInspectResult(false, ERROR_NO_HDCP_2_2_DATA, null);
		        		return ;
		        	}
						        	
		        	hdcp_2_2_md5 = MtaUtils.createHexMD5(hdcp_2_2);
		        	if(hdcp_2_2_md5 == null){
		        		updateInspectResult(false, ERROR_NO_HDCP_2_2_DATA, null);
		        		return ;
		        	}
					LogUtil.d(TAG, "hdcp_2_2 = "+hdcp_2_2+" hdcp_2_2_md5 = "+hdcp_2_2_md5);
					if(!setHdcpKey_2_2(MtaUtils.convertByteArrayToShortArray(hdcp_2_2))){
						updateInspectResult(false, ERROR_BURN_HDCP_2_2_FAILED, null);
						return ;
					}
					
					short[] checkSum = getHdcpKey_2_2(1044);
					if(null == checkSum && !hdcp_2_2_md5.equals(MtaUtils.createHexMD5(
							MtaUtils.convertShortArrayToByteArray(checkSum)))){
							       updateInspectResult(false, ERROR_BURN_HDCP_2_2_FAILED, null);
							       return ;
					}

		        }else{
		        	updateInspectResult(false, ERROR_NO_HDCP_2_2_DATA, null);
		        	return;
		       }

			}
			
			if( !updateMacAddress(makeColonMac(mac)) || !updateSerialNumber(sn) ){
				updateInspectResult(false, ERROR_UPDATE_FAILED, null);
				return ;	
			}
			
			try {
				ContentValues values = new ContentValues();
				values.put("FLAG", 1);
				database.update(
						MAC_TABLE_NAME, values, " MAC = ?", args);
			} catch (Exception e) {
				// TODO: handle exception
				//failed to modify the database
				updateInspectResult(false, ERROR_DB_WRITE_EXCEPTION, null);
				return ;	
			}
			
			updateInspectResult(true, ERROR_NONE, null);
			
		} catch (Exception e) {
			
		} finally {
			if( database != null ){
				database.close();
			}
		}
	}

	public void updateHdcpKey(String mac){
		if( mac == null || mac.length() != 12 || !mac.matches("^[0-9A-Fa-f]+$") ){
			//invalid MAC address
			updateInspectResult(false, ERROR_INVALID_MAC, null);
			return ;
		}
		
		String dbPath = getHdcpKeyDatabasePath();
		SQLiteDatabase database = null;
		
		if( dbPath == null ){
			//can't find database file
			updateInspectResult(false, ERROR_DB_NOT_FOUND, null);
			return ;
		}
		
		try {
			try {
				database = SQLiteDatabase.openDatabase(
						dbPath, null, SQLiteDatabase.OPEN_READWRITE);
			} catch (Exception e) {
				// TODO: handle exception
				
			}

			if( database == null ){
				//open database with read_write permission failed
				updateInspectResult(false, ERROR_DB_CANNOT_OPEN, null);
				return ;
			}
			
			Cursor cursor = null;
		    byte[] hdcp_1_4 	= null;
		    String hdcp_1_4_md5 = null;
		    byte[] hdcp_2_2 	= null;
		    String hdcp_2_2_md5 = null;
		    
			String args[] = {
				mac
			};
													
			if(hdcpKey_1_4){
				cursor = database.rawQuery("select KEY, MD5 from MTA_MAC_KEY" + " where MAC = ?", args);
		        if(cursor != null && cursor.moveToNext()){
		        	hdcp_1_4 = cursor.getBlob(0);
//		        	hdcp_1_4_md5 = cursor.getString(1);
		        	if(hdcp_1_4 == null || hdcp_1_4.length < 289){
		        		updateInspectResult(false, ERROR_NO_HDCP_1_4_DATA, null);
		        		return ;
		        	}
		        	
		        	hdcp_1_4_md5 = MtaUtils.createHexMD5(hdcp_1_4);
		        	if(hdcp_1_4_md5 == null){
		        		updateInspectResult(false, ERROR_NO_HDCP_1_4_DATA, null);
		        		return ;
		        	}
					
		        	LogUtil.d(TAG, "hdcp_1_4 = "+hdcp_1_4+" hdcp_1_4_md5 = "+hdcp_1_4_md5);
					if(!setHdcpKey(MtaUtils.convertByteArrayToShortArray(hdcp_1_4))){
						updateInspectResult(false, ERROR_BURN_HDCP_1_4_FAILED, null);
						return ;
					}
					
					short[] checkSum = getHdcpKey(289);
					if(null == checkSum && !hdcp_1_4_md5.equals(MtaUtils.createHexMD5(
							MtaUtils.convertShortArrayToByteArray(checkSum)))){
						updateInspectResult(false, ERROR_BURN_HDCP_1_4_FAILED, null);
						return ;
					}
		        }else{
		        	updateInspectResult(false, ERROR_NO_HDCP_1_4_DATA, null);
		        	return;
		        }
			}
			
			if(hdcpKey_2_2){
				cursor = database.rawQuery("select KEY_V2, MD5_V2 from MTA_MAC_KEY" + " where MAC = ?", args);
		        if(cursor != null && cursor.moveToNext()){
		        	hdcp_2_2 = cursor.getBlob(0);
//		        	hdcp_2_2_md5 = cursor.getString(1);  

				if(hdcp_2_2 == null || hdcp_2_2.length < 1044){
		        		updateInspectResult(false, ERROR_NO_HDCP_2_2_DATA, null);
		        		return ;
		        	}
						        	
		        	hdcp_2_2_md5 = MtaUtils.createHexMD5(hdcp_2_2);
		        	if(hdcp_2_2_md5 == null){
		        		updateInspectResult(false, ERROR_NO_HDCP_2_2_DATA, null);
		        		return ;
		        	}
					LogUtil.d(TAG, "hdcp_2_2 = "+hdcp_2_2+" hdcp_2_2_md5 = "+hdcp_2_2_md5);
					if(!setHdcpKey_2_2(MtaUtils.convertByteArrayToShortArray(hdcp_2_2))){
						updateInspectResult(false, ERROR_BURN_HDCP_2_2_FAILED, null);
						return ;
					}
					
					short[] checkSum = getHdcpKey_2_2(1044);
					if(null == checkSum && !hdcp_2_2_md5.equals(MtaUtils.createHexMD5(
							MtaUtils.convertShortArrayToByteArray(checkSum)))){
							       updateInspectResult(false, ERROR_BURN_HDCP_2_2_FAILED, null);
					}

		        }else{
		        	updateInspectResult(false, ERROR_NO_HDCP_2_2_DATA, null);
		        	return;
		       }

			}
						
			try {
				ContentValues values = new ContentValues();
				values.put("FLAG", 2);
				database.update(
						MAC_TABLE_NAME, values, " MAC = ?", args);
			} catch (Exception e) {
				// TODO: handle exception
				//failed to modify the database
				updateInspectResult(false, ERROR_DB_WRITE_EXCEPTION, null);
				return ;	
			}
			
			updateInspectResult(true, HDCP_KEY_REBURN_OK, null);
			
		} catch (Exception e) {
			
		} finally {
			if( database != null ){
				database.close();
			}
		}
	}
			
	protected boolean updateSerialNumber(String sn){
		try {
			TvManager.getInstance().setEnvironment("serid", sn);
		} catch (TvCommonException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			return false;
		}
		
		return true;
	}
	
	protected boolean updateMacAddress(String mac){
        try {
        	TvManager.getInstance().setEnvironment("macaddr", mac);
        	TvManager.getInstance().setEnvironment("ethaddr", mac);         
        } catch (TvCommonException e) {
            e.printStackTrace();
            return false;
        }
        
		return true;
	}
	
	private String makeColonMac(String mac){
        if(mac.length() < 12){
	    	return null;
	    }
        
	    StringBuffer stringBuffer = new StringBuffer();
	    
	    for(int i = 0; i < 5; i++){
	        stringBuffer.append(mac.substring(i*2, i*2+2)).append(":");
	    }
	    
	    stringBuffer.append(mac.substring(10));
	    
	    return stringBuffer.toString();
	}	
	
	private String getDatabasePath(){		
    	ArrayList<String> usbDirs = MtaUtils.getMountedUsbDevices("/mnt/usb/");
    	for(String dir : usbDirs){
			File file = new File(dir+"/"+MTA_DB_FILE);
		    if(file.exists()){
		    	return file.getAbsolutePath();
		    }	
    	}
    	
		return null;
	}
	
	private String getHdcpKeyDatabasePath(){		
    	ArrayList<String> usbDirs = MtaUtils.getMountedUsbDevices("/mnt/usb/");
    	for(String dir : usbDirs){
			File file = new File(dir+"/"+MTA_HDCP_DB_FILE);
		    if(file.exists()){
		    	return file.getAbsolutePath();
		    }	
    	}
    	
		return null;
	}
	
	protected boolean setHdcpKey(short[] data){
		boolean bRet = false;
		
		Class<?> factoryManagerClass = FactoryManager.class;
	
		FactoryManager instance = FactoryManager.getInstance();
		
		Method void_method = null;
		
		try {
			void_method = factoryManagerClass.getMethod("EosSetHDCPKey", short [].class);
			try {
				bRet = (Boolean)void_method.invoke(instance, data);
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(void_method == null){
			try {
				Method boolean_method = factoryManagerClass.getMethod("EosSetHDCPKey", short [].class, boolean.class);
				try {
					bRet = (Boolean) boolean_method.invoke(instance, data, false);
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		}


		return bRet;
	}
	
	protected short[] getHdcpKey(int size){
		short[] data = null;
		
		Class<?> factoryManagerClass = FactoryManager.class;
		
		FactoryManager instance = FactoryManager.getInstance();
		
		Method void_method = null;
		
		try {
			void_method = factoryManagerClass.getMethod("EosGetHDCPKey", int.class);
			try {
				data = (short[])void_method.invoke(instance, size);
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		if(void_method == null){
			try {
				Method boolean_method = factoryManagerClass.getMethod("EosGetHDCPKey", int.class, boolean.class);
				try {
					data= (short[]) boolean_method.invoke(instance, size, false);
				} catch (IllegalAccessException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (IllegalArgumentException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				} catch (InvocationTargetException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			} catch (NoSuchMethodException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}			
		}

		
		return data;
	}
	
	protected boolean setHdcpKey_2_2(short[] data){
		Class<?> factoryManagerClass = FactoryManager.class;
		
		try {
			Method boolean_method = factoryManagerClass.getMethod("EosSetHDCPKey", short[].class, boolean.class);
			try {
				return (Boolean) boolean_method.invoke(FactoryManager.getInstance(), data, true);
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return false;
	}
	
	protected short[] getHdcpKey_2_2(int size){
		Class<?> factoryManagerClass = FactoryManager.class;
		
		try {
			Method boolean_method = factoryManagerClass.getMethod("EosGetHDCPKey", int.class, boolean.class);
			try {
				return (short[])boolean_method.invoke(FactoryManager.getInstance(), size, true);
			} catch (IllegalAccessException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		} catch (NoSuchMethodException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		return null;
	}
}
