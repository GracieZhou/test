package com.utsmta.mstar;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import android.app.Activity;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.Message;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;


import com.utsmta.app.R;
import com.mstar.android.tv.TvCommonManager;
import com.utsmta.common.FactoryItem;
import com.utsmta.common.SimpleItemPagerView;
import com.utsmta.utils.LogUtil;
import com.utsmta.utils.MtaUtils;


public class MstarUsbMediaView extends SimpleItemPagerView {
	private String TAG = "MstarUsbMediaView";
		
	private final String USB1_MEADIA_FILE_TAG = "Usb1MeadiaFile";
	
	private final String USB2_MEADIA_FILE_TAG = "Usb2MeadiaFile";	

	protected final String CONFIG_FILE_NAME = "eostek-fmtac.ini";
			
	private MediaPlayer mMediaPlayer = null;	
	
	String usbTag = null;
	
	SurfaceView surfaceView;
	
	private SurfaceHolder surfaceHolder = null;	
	
	private View surfaceContainer = null;
			
	private Handler uiHandler = new Handler(){
		@Override
		public void handleMessage(Message msg) {
			// TODO Auto-generated method stub
			if(msg.what == 1){				
			   MediaPlay(getUsbMediaFileNmae());			   
			}						
		}	
	};
		
	public MstarUsbMediaView(Activity activity, FactoryItem item, String name) {
		super(activity, item);
		// TODO Auto-generated constructor stub				
		if(TvCommonManager.INPUT_SOURCE_STORAGE != TvCommonManager.getInstance().getCurrentTvInputSource()){
			   TvCommonManager.getInstance().setInputSource(TvCommonManager.INPUT_SOURCE_STORAGE);	
			}
		createView();
		this.usbTag = name;
	}

	@Override
	protected View onCreateView(LayoutInflater inflater) {
	// TODO Auto-generated method stub
	View contentView = inflater.inflate(R.layout.amta_usb, null, true);
	surfaceContainer = contentView.findViewById(R.id.surface_container);
	surfaceView = (SurfaceView) contentView
			.findViewById(R.id.video_surface);

	
	surfaceHolder = surfaceView.getHolder();
	surfaceHolder.setKeepScreenOn(true);
	surfaceHolder.addCallback(new SurfaceHolder.Callback() {
	
		@Override
		public void surfaceDestroyed(SurfaceHolder arg0) {
			// TODO Auto-generated method stub
			LogUtil.d(TAG, "surfaceDestroyed");			
		}
	
		@Override
		public void surfaceCreated(SurfaceHolder arg0) {
			LogUtil.d(TAG, "surfaceCreated");
			// MediaPlay(getUsbMediaFileNmae());
			uiHandler.sendEmptyMessageDelayed(1, 800);
		}
			
		@Override
		public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2,
				int arg3) {
				LogUtil.d(TAG, "surfaceChanged");
			}
		});
	
	return contentView;
	}
	
	@Override
	protected void onShown() {
		// TODO Auto-generated method stub
		LogUtil.d(TAG, "onShown");	
	}
	
	@Override
	protected void onHiden() {
		// TODO Auto-generated method stub
		LogUtil.d(TAG, "onHiden");
		MediaStop();
		uiHandler.removeMessages(1);
		
	}
	
	private void MediaStop() {
		
		if(mMediaPlayer != null){			
			try {
				mMediaPlayer.stop();
				mMediaPlayer.release();
				mMediaPlayer = null;
			} catch (IllegalStateException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} 
		}			
	}
	
	private void MediaPlay(String filePath ) {			
		if (mMediaPlayer == null) {
			mMediaPlayer = new MediaPlayer();						
		}			
		try {
			mMediaPlayer.reset();
			mMediaPlayer.setAudioStreamType(AudioManager.STREAM_MUSIC);
			if (filePath != null) {
				mMediaPlayer.setDataSource(filePath);
			} else {
				return;
			}
			
			mMediaPlayer.prepare();
			mMediaPlayer.start();
			mMediaPlayer.setDisplay(surfaceHolder);
		} catch (Exception  e) {
			e.printStackTrace();
			LogUtil.d(TAG, e.getMessage()); 
		} 
	}	

	private String GetUsbMeadiaFilePath(String usbMeadiaFilename){
		String filePath = null;
		
		for(String dirPath : MtaUtils.getMountedUsbDevices("/mnt/usb/")){
			File configFile = new File(dirPath+"/"+CONFIG_FILE_NAME);
			if(configFile.exists()){
				try {
					BufferedReader reader = new BufferedReader(new FileReader(configFile));
					String content = null;
					try {
						while((content = reader.readLine()) != null){
							if(content.contains(usbMeadiaFilename)){
								int index = content.indexOf("=");
								if(index >= 0 && content.length() > index+1){
									filePath = dirPath+"/"+content.substring(index+1);
								}
								break;
							}
						}
					} catch (IOException e) {
						e.printStackTrace();
					} finally {
						if(reader != null){
							try {
								reader.close();
							} catch (IOException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
					}
				} catch (FileNotFoundException e) {
					e.printStackTrace();
				}					
			}
		}
		return filePath;		
	}
	
	private String getUsbMediaFileNmae() {
		String fileNmae = null; 
		if("amta_usb1".equalsIgnoreCase(usbTag)){
			fileNmae = GetUsbMeadiaFilePath(USB1_MEADIA_FILE_TAG);
		}else if("amta_usb2".equalsIgnoreCase(usbTag)) {
			fileNmae = GetUsbMeadiaFilePath(USB2_MEADIA_FILE_TAG);
		}
		
		return fileNmae;
	}
		
}
