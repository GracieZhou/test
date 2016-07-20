package com.utsmta.mstar;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.RandomAccessFile;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.Arrays;
import java.util.List;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.content.DialogInterface.OnClickListener;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteStatement;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.View;
import android.widget.Button;

import com.mstar.android.tvapi.common.TvManager;
import com.mstar.android.tvapi.common.exception.TvCommonException;
import com.mstar.android.tvapi.factory.FactoryManager;
import com.utsmta.app.MainActivity;
import com.utsmta.app.R;
import com.utsmta.common.FactoryItem;
import com.utsmta.common.ItemPagerView;
import com.utsmta.utils.LogUtil;
import com.utsmta.utils.MtaUtils;

public class MstarAmtaHdcpView extends ItemPagerView {
	public static final String TAG = "MstarAmtaHdcpView";
	
	private final String HDCP_DATABASE_NAME = "mta_hdcp_key.db";
	
	private final String HDMI_HDCP_BUNCH_FILE_NAME = "hdmiHdcp.bin";
	
	private boolean bIsBurndHDCPKEY = false;
	
	private ProgressDialog progressDialog = null;
	
	private Button programmeBtn = null;
	
	public MstarAmtaHdcpView(Activity activity, FactoryItem item) {
		super(activity, item);
		// TODO Auto-generated constructor stub
	}
	
	private View.OnClickListener onProgrammeBtnListener = new View.OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			if(bIsBurndHDCPKEY){
				showMessageDialog(activity.getString(R.string.hdcp_is_burnded),true);
			}else{
				beginProgrammeHdcpKey();	
			}			
		}
	};
	
	private Handler uiHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			int what = msg.what;
			
			if(((what >> 8) & 0xFF) == 1){
				if(progressDialog != null)
					progressDialog.dismiss();
				
				int id = (what & 0xFF);
				if(0 == id){
					showMessageDialog(activity.getString(R.string.can_not_find_hdcp_bunch_file));
					return ;
				}
				
				if(1 == id){
					showMessageDialog(activity.getString(R.string.hdcp_bunch_file_too_small));
					return ;
				}
				
				if(2 == id){
					showMessageDialog(activity.getString(R.string.hdcp_bunch_file_not_match));
					return ;
				}
				
				if(3 == id){
					showMessageDialog(activity.getString(R.string.create_hdcp_database_failed));
					return ;
				}
				
				if(4 == id){
					showMessageDialog(activity.getString(R.string.read_hdcp_file_error));
					return ;
				}
				
				if(5 == id){
					showMessageDialog("Finish Creating Database!The number of records : "
							+msg.getData().getLong("recordNum")+". Cost Time : "+(float)(msg.getData().getLong("time"))/1000+"seconds", true);
					return ;
				}
			}
			
			if(((what >> 8) & 0xFF) == 2){
				if(progressDialog != null)
					progressDialog.dismiss();
				int id = (what & 0xFF);
				String dialogMessage = null;
				
				switch (id) {
				case 0:
					dialogMessage = activity.getString(R.string.open_hdcp_database_failed);
					break;
					
				case 1:
					dialogMessage = activity.getString(R.string.no_hdcp_key_left);
					break;
					
				case 2:
					dialogMessage = activity.getString(R.string.bad_hdcp_key_data);
					break;
					
				case 3:
					dialogMessage = activity.getString(R.string.have_been_modified);
					break;
					
				case 4:
					dialogMessage = activity.getString(R.string.write_hdcp_key_error);
					break;
					
				case 5:
					dialogMessage = activity.getString(R.string.read_hdcp_key_error);
					break;
					
				case 6:
					((MainActivity)activity).setHdcpFlag(true);
					item.setResult(true);
					dialogMessage = activity.getString(R.string.programme_hdcp_key_successed);
					bIsBurndHDCPKEY = true;
					break;

				default:
					break;
				}
				
				showMessageDialog(dialogMessage);
			}
		}
	};
	
	@Override
	protected View onCreateView(android.view.LayoutInflater inflater) {
		View contentView = inflater.inflate(R.layout.amta_hdcp, null, false);
		
		programmeBtn = (Button) contentView.findViewById(R.id.program_btn);
		programmeBtn.setOnClickListener(onProgrammeBtnListener);
	
		try {
			String hdcp_1_4 = TvManager.getInstance().getEnvironment("HKEY");
			if(hdcp_1_4.length() >= 289)
			{
				bIsBurndHDCPKEY = true;
			}
			else
			{
				bIsBurndHDCPKEY = false;
			}	
		} catch (TvCommonException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}	
		
		return contentView;
	};
	
	private void beginProgrammeHdcpKey(){
		List<String> usbList = MtaUtils.getMountedUsbDevices("/mnt/usb/");
		if(usbList.isEmpty()){
			showMessageDialog(activity.getString(R.string.can_not_found_usb_device));
			return ;
		}
		
		String usbPath = null;
		for(String path:usbList){
			if (isHdcpDatabaseFileExisted(path)) {
				usbPath = path;
				break;
			}
		}
		
		if(usbPath == null){
			progressDialog = ProgressDialog.show(activity,
					activity.getString(R.string.generating_hdcp_db), null);
			
			Thread generateThread = new GenerateHdcpDatabaseThread(usbList);
			generateThread.start();			
		}else{
			progressDialog = ProgressDialog.show(activity,
					activity.getString(R.string.programming_hdcp_key), null);
			
			Thread programmdThread = new ProgrammeThread(usbPath);
			programmdThread.start();	
		}
	}
	
	private class GenerateHdcpDatabaseThread extends Thread{
		private List<String> usbPaths = null;
		
		public GenerateHdcpDatabaseThread(List<String> usbPaths){
			this.usbPaths = usbPaths;
		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
			long curTime = System.currentTimeMillis();
			String usbPath = null;
			String hdmiHdcpFilePath = null;
			for(String path:usbPaths){
				if((hdmiHdcpFilePath = findHdmiHdcpBunchFile(path)) != null) {
					usbPath = path;
					break;
				}
			}

			if(null == hdmiHdcpFilePath){
				uiHandler.sendEmptyMessage(0x100);
				return ;
			}
			
			String databasePath = usbPath+"/"+HDCP_DATABASE_NAME;	
			RandomAccessFile hdmiHdcpFile = null;
			try {
				hdmiHdcpFile = new RandomAccessFile(hdmiHdcpFilePath, "r");
				try {
					long hdmiFileLength = hdmiHdcpFile.length();
					
					if(hdmiFileLength < 336){
						uiHandler.sendEmptyMessage(0x101);
						return ;
					}
					
					int recordNum = (int)((hdmiFileLength - 32)/304);
					LogUtil.d(TAG, "record num : "+recordNum);
					
					SQLiteDatabase hdcpDatabase = activity.openOrCreateDatabase(databasePath, Context.MODE_PRIVATE, null);
					if(hdcpDatabase == null){
						uiHandler.sendEmptyMessage(0x103);
						return ;
					}
					
					hdcpDatabase.execSQL("CREATE TABLE IF NOT EXISTS hdcp (id INTEGER PRIMARY KEY AUTOINCREMENT,"
							+ "used INTEGER, hdcpKeyUdid CHAR(16), hdmiCheckSum CHAR(16), "
							+"hdmiHdcpKey BLOB);");
					int offset = 32;
					
					String sqlStr = "insert into hdcp (id, used, hdcpKeyUdid, hdmiCheckSum, hdmiHdcpKey) values "+
					"(?,?,?,?,?);";
					SQLiteStatement statement = hdcpDatabase.compileStatement(sqlStr);
					hdcpDatabase.beginTransaction();
					int length = 304*recordNum;
					byte[] hdmiKey = new byte[length]; 
					hdmiHdcpFile.seek(offset);
					if(hdmiHdcpFile.read(hdmiKey, 0, length) != length){
						uiHandler.sendEmptyMessage(0x104);
					}
					
					for(int i= 0; i < recordNum; ++i){
						int start = 304*i;
						int end = start+289;
						int indexStart = end;
						int indexEnd   = indexStart+15;
						
						Record record = new Record();
						record.index = i;
						record.hdmiHdcp = Arrays.copyOfRange(hdmiKey, start, end);
						record.hdmiHdcpMD5 = MtaUtils.createHexMD5(record.hdmiHdcp);
						byte data[] = Arrays.copyOfRange(hdmiKey, indexStart, indexEnd);
						record.hdcpKeyUdid = new String(Arrays.copyOfRange(data, 0, 4));
						
						for(int j = 4; j < 7; ++j)
						{
							record.hdcpKeyUdid += String.format("%02d", (int)data[j]);
						}
						
						int no = ((int)data[7] << 24) + ((int)data[8] << 16) + ((int)data[9] << 8) + ((int)data[10]);
						LogUtil.d(TAG, "no = " + no);
						record.hdcpKeyUdid += String.format("%05d", no);
						
						statement.bindLong(1, record.index);
						statement.bindLong(2, 0);
						statement.bindString(3, record.hdcpKeyUdid);
						statement.bindString(4, record.hdmiHdcpMD5);
						statement.bindBlob(5, record.hdmiHdcp );
						statement.executeInsert();
					}
					
					hdcpDatabase.setTransactionSuccessful();
					hdcpDatabase.endTransaction();
					hdcpDatabase.close();
					
					Message msg = uiHandler.obtainMessage(0x105);
					Bundle data = new Bundle();
					data.putLong("recordNum", recordNum);
					data.putLong("time", System.currentTimeMillis()-curTime);
					msg.setData(data);
					uiHandler.sendMessage(msg);
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
			} catch (FileNotFoundException e) {
				// TODO Auto-generated catch block
				uiHandler.sendEmptyMessage(0x104);
				e.printStackTrace();
			}finally{
				LogUtil.d(TAG, "GenerateHdcpDatabaseThread finally");
				try {
					if(hdmiHdcpFile != null)
						hdmiHdcpFile.close();
				} catch (IOException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}				
			}
		}
	}
	
	private class ProgrammeThread extends Thread{
		private String usbPath = null;
		
		public ProgrammeThread(String usbPath){
			this.usbPath = usbPath;
		}
		
		@Override
		public void run() {
			// TODO Auto-generated method stub
//			bIsBurndHDCPKEY = true;
			SQLiteDatabase hdcpDatabase = null;
			try{
				String databasePath = usbPath+"/"+HDCP_DATABASE_NAME;
				hdcpDatabase = activity.openOrCreateDatabase(databasePath, Context.MODE_PRIVATE, null);
				if(hdcpDatabase == null){
					uiHandler.sendEmptyMessage(0x200);
					return ;
				}
				
				String[] args = {"0"};
				Cursor cursor = null;
			 int index = 0;
		        String hdcpKeyUdid  = null;
		        String hdmiCheckSum = null;
		        byte[] hdmiHdcp = {0};				
			  try {	
		        cursor = hdcpDatabase.rawQuery("select * from hdcp where used = ?" , args);

		        if(cursor == null || !cursor.moveToNext()){
		        	uiHandler.sendEmptyMessage(0x201);
		        	return ;
		        }
		        
		        	index = cursor.getInt(0);
		        	hdcpKeyUdid  = cursor.getString(2);
		        	hdmiCheckSum = cursor.getString(3);
		        	hdmiHdcp = cursor.getBlob(4);
		        if(hdmiCheckSum == null || hdmiHdcp == null ){
		        		uiHandler.sendEmptyMessage(0x202);
		        		return;
		        	}
			  } catch (Exception e) {
					e.printStackTrace();
					LogUtil.e(TAG,"HDCP KEY SQLiteDatabase err");
		        	uiHandler.sendEmptyMessage(0x202);
		        	return;
		        }	
		        
		        String hdmiMD5 = MtaUtils.createHexMD5(hdmiHdcp);
		        if(!hdmiCheckSum.equals(hdmiMD5)){
		        	uiHandler.sendEmptyMessage(0x203);
		        	return ;
		        }

				if(!setHdcpKey(MtaUtils.convertByteArrayToShortArray(hdmiHdcp))){
					uiHandler.sendEmptyMessage(0x204);
					return ;
				}
				
				byte[] checkHdmi = MtaUtils.convertShortArrayToByteArray(getHdcpKey(289));					
				String checkHdmiMD5 = MtaUtils.createHexMD5(checkHdmi);
				
				LogUtil.d(TAG, "hdmiCheckSum = " + hdmiCheckSum);
				LogUtil.d(TAG, "checkHdmiMD5 = " + checkHdmiMD5);
				if(!hdmiCheckSum.equalsIgnoreCase(checkHdmiMD5)){
					uiHandler.sendEmptyMessage(0x205);
					return ;
				}
				
				String[] indexValue = {String.valueOf(index)};
				ContentValues values = new ContentValues();
				values.put("used", 1);
				hdcpDatabase.update("hdcp", values, " id = ?", indexValue);
				
				//save  key index
				SharedPreferences sharedPreferences = activity.getSharedPreferences("mta", Context.MODE_PRIVATE);					
				sharedPreferences.edit().putString("hdcp-key-udid", hdcpKeyUdid).commit();
				
				uiHandler.sendEmptyMessage(0x206);
			}catch (Exception e) {
				uiHandler.sendEmptyMessage(0x205);
				bIsBurndHDCPKEY = false;
				LogUtil.d(TAG, "e.getMessage()");
			}finally{
				if(hdcpDatabase != null)
					hdcpDatabase.close();
			}

		}
	}
	
	private class Record{
		public int index = -1;
		public boolean used = false;
		public byte[] hdmiHdcp = null;
		public String hdmiHdcpMD5 = "";
		public String hdcpKeyUdid = "";
	}
	
	private boolean isHdcpDatabaseFileExisted(String usbPath){
		File file = new File(usbPath+"/"+HDCP_DATABASE_NAME);
		if(file.exists())
			return true;

		return false;
	}
	
	private String findHdmiHdcpBunchFile(String usbPath){
		String filePath = usbPath+"/hdmi/"+HDMI_HDCP_BUNCH_FILE_NAME;
		File file = new File(filePath);
		if(file.exists())
			return filePath;
		return null;
	}

	private void showMessageDialog(String msg, final boolean programme){
		AlertDialog.Builder dialogBuilder = new AlertDialog.Builder(activity);
		dialogBuilder.setMessage(msg);
		if(bIsBurndHDCPKEY && programme){
			dialogBuilder.setNegativeButton(activity.getString(R.string.cancel), null);			
		}
		
		dialogBuilder.setPositiveButton(activity.getString(R.string.confirm), new OnClickListener() {  
			
			@Override
			public void onClick(DialogInterface dialog, int which) {
				// TODO Auto-generated method stub
				dialog.dismiss();
				if(programme){
					beginProgrammeHdcpKey();
				}
			}
		});

		AlertDialog alert = dialogBuilder.create();
		alert.show();
		if(bIsBurndHDCPKEY && programme){
		       alert.getButton(AlertDialog.BUTTON_NEGATIVE).requestFocus();		
		}
	}
	
	private void showMessageDialog(String msg){
		showMessageDialog(msg, false);
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
}
