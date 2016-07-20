package com.utsmta.mstar.inspect;

import android.os.Message;

import com.mstar.android.tv.TvChannelManager;
import com.mstar.android.tv.TvCommonManager;
import com.mstar.android.tvapi.common.PictureManager;
import com.mstar.android.tvapi.common.exception.TvCommonException;
import com.mstar.android.tvapi.common.vo.EnumProgramInfoType;
import com.mstar.android.tvapi.common.vo.EnumScalerWindow;
import com.mstar.android.tvapi.common.vo.ProgramInfo;
import com.mstar.android.tvapi.common.vo.ProgramInfoQueryCriteria;
import com.mstar.android.tvapi.common.vo.VideoWindowType;
import com.utsmta.common.FactoryInspect;
import com.utsmta.utils.LogUtil;
import com.utsmta.common.DeviceManager;
import com.utsmta.app.MtaApplication;
import com.mstar.android.tvapi.atv.vo.EnumAtvManualTuneMode;
import com.utsmta.utils.MtaUtils;
import java.io.File;






public class MstarVideoInspect extends FactoryInspect {
	private final String TAG = "MstarVideoInspect";
	
	private int x,y,w,h = 0;
	
	private int inputSource = TvCommonManager.INPUT_SOURCE_STORAGE;
	
	private TvCommonManager tvManager = TvCommonManager.getInstance();

	protected final String CONFIG_FILE_NAME = "eostek-fmtac.ini";
	
	private final String ATVFREQTAG = "atvcurrFrequnce";
	
	@SuppressWarnings("deprecation")
	@Override
	protected void handleBackgroundHandlerMessage(Message msg) {
		// TODO Auto-generated method stub
			
		switch(msg.what){
		case 0:
			LogUtil.d(TAG, "handleBackgroundHandlerMessage inputSource = "+inputSource);
			backgroundHandler.sendEmptyMessageDelayed(0, 1500);
			
			if(inputSource == TvCommonManager.INPUT_SOURCE_NONE)
				return ;							
			if(tvManager.getCurrentTvInputSource() != inputSource){
				tvManager.setInputSource(inputSource);
								
				if(TvCommonManager.INPUT_SOURCE_DTV == inputSource ||
						TvCommonManager.INPUT_SOURCE_ATV == inputSource){

					if(MtaApplication.getDevice().getDeviceName().equals(DeviceManager.DEVICE_H828)){
						String atvFrequecy = getAtvFrequency();
					    if(TvCommonManager.INPUT_SOURCE_ATV == inputSource && atvFrequecy  != null){
							int nCurrentFrequency = 0;
							int curChannelNumber = TvChannelManager.getInstance().getCurrentChannelNumber();
															
							nCurrentFrequency = Integer.valueOf(atvFrequecy).intValue();

							TvChannelManager.getInstance().startAtvManualTuning(
											5 * 1000,
											nCurrentFrequency,
											EnumAtvManualTuneMode.E_MANUAL_TUNE_MODE_FINE_TUNE_ONE_FREQUENCY);

							try {
								Thread.sleep(1000);
							} catch (InterruptedException e) {
							// TODO Auto-generated catch block
								e.printStackTrace();
							}
							
							TvChannelManager.getInstance().stopAtvManualTuning();
							TvChannelManager.getInstance().saveAtvProgram(curChannelNumber);
						}else{
							ProgramInfoQueryCriteria qc = new ProgramInfoQueryCriteria();
			        		ProgramInfo pinfo = TvChannelManager.getInstance().getProgramInfo(qc, EnumProgramInfoType.E_INFO_CURRENT);
			        		TvChannelManager.getInstance().selectProgram(pinfo.number, (short) pinfo.serviceType);	
						}		
										   	
					}else{							
			        	ProgramInfoQueryCriteria qc = new ProgramInfoQueryCriteria();
			        	ProgramInfo pinfo = TvChannelManager.getInstance().getProgramInfo(qc, EnumProgramInfoType.E_INFO_CURRENT);
			        	TvChannelManager.getInstance().selectProgram(pinfo.number, (short) pinfo.serviceType);
					}	
				}
				
				setPipscale();
			}
			break;
			
		case 1:
			LogUtil.d(TAG, "handleBackgroundHandlerMessage 1");
			tvManager.setInputSource(TvCommonManager.INPUT_SOURCE_STORAGE);
			break;
		}
		
	}
	
	@Override
	protected void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		LogUtil.d(TAG, "onStart");
		backgroundHandler.sendEmptyMessageDelayed(0, 1500);
	}
	
	@Override
	protected void onStop() {
		// TODO Auto-generated method stub
		LogUtil.d(TAG, "onStop");
		backgroundHandler.removeMessages(0);
		backgroundHandler.sendEmptyMessage(1);
//		tvManager.setInputSource(TvCommonManager.INPUT_SOURCE_STORAGE);
		super.onStop();
	}

	public void setVideoSource(String sourceName){		
		if("hdmi1".equals(sourceName)){
			inputSource = TvCommonManager.INPUT_SOURCE_HDMI;
		}else if("hdmi2".equals(sourceName)){
			inputSource = TvCommonManager.INPUT_SOURCE_HDMI2;
		}else if("hdmi3".equals(sourceName)){
			inputSource = TvCommonManager.INPUT_SOURCE_HDMI3;
		}else if("vga".equals(sourceName)){
			inputSource = TvCommonManager.INPUT_SOURCE_VGA;
		}else if("cvbs".equals(sourceName)){
			inputSource = TvCommonManager.INPUT_SOURCE_CVBS;
		}else if("atv".equals(sourceName)){
			inputSource = TvCommonManager.INPUT_SOURCE_ATV;
		}else if("dtv".equals(sourceName)){
			inputSource = TvCommonManager.INPUT_SOURCE_DTV;
		}else if("ktv".equals(sourceName)){
			inputSource = TvCommonManager.INPUT_SOURCE_KTV;
		}else if("ypbpr1".equals(sourceName)){
			inputSource = TvCommonManager.INPUT_SOURCE_YPBPR;
		}else if("ypbpr2".equals(sourceName)){
			inputSource = TvCommonManager.INPUT_SOURCE_YPBPR2;
		}else{
			inputSource = TvCommonManager.INPUT_SOURCE_STORAGE;
		}
	}
	
	public void setPipDimens(int x, int y, int w, int h) {
		// TODO Auto-generated method stub
		this.x = x;
		this.y = y;
		this.w = w;
		this.h = h;
	}
	
	public void setPipscale() {
		// TODO Auto-generated method stub
		LogUtil.d(TAG, "setPipscale");
        VideoWindowType videoWindowType = new VideoWindowType();
        videoWindowType.x = this.x;
        videoWindowType.y = this.y;
        videoWindowType.width = this.w;
        videoWindowType.height = this.h;
        try {
        	PictureManager.getInstance().selectWindow(EnumScalerWindow.E_MAIN_WINDOW);
        	PictureManager.getInstance().setDisplayWindow(videoWindowType);
        	PictureManager.getInstance().scaleWindow();
        } catch (TvCommonException e) {
              // TODO Auto-generated catch block
          e.printStackTrace();
        }
	}
	
	private String getAtvFrequency(){
		String atvFrequeency = null;
		
		for(String dirPath : MtaUtils.getMountedUsbDevices("/mnt/usb/")){
			File file = new File(dirPath+"/"+CONFIG_FILE_NAME);			
			if(file.exists()){
				atvFrequeency = MtaUtils.getPropertyFromFile(file, ATVFREQTAG);
				break;
			}
		}		
		return atvFrequeency;
	}
		
}
