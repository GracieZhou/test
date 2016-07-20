package com.utsmta.mstar;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;

import android.app.Activity;
import android.content.Intent;
import android.net.Uri;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.Button;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;


import com.mstar.android.tv.TvS3DManager;
import com.utsmta.app.R;
import com.utsmta.common.MtaMessageDialog;
import com.utsmta.common.MtaPagerView;
import com.utsmta.utils.LogUtil;
import com.utsmta.utils.MtaUtils;

public class Mstar3DView extends MtaPagerView {
	protected final String TAG = "Mstar3DView";
	
	protected final String CONFIG_FILE_NAME = "eostek-fmtac.ini";
	
	protected final String USB1_MEADIA_FILE_TAG = "Usb3DMeadiaFile";
	
	public Mstar3DView(Activity activity) {
		super(activity);
		// TODO Auto-generated constructor stub
	}
	
	@Override
	protected View onCreateView(LayoutInflater inflater) {
		// TODO Auto-generated method stub
		FrameLayout contentView = new FrameLayout(activity);
		FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
		lp.gravity = Gravity.CENTER;
		
		Button button = new Button(activity);
		button.setText("3D");
		button.setPadding(10, 5, 10, 5);
		button.setOnClickListener(new View.OnClickListener() {
			
			@Override
			public void onClick(View v) {
				// TODO Auto-generated method stub
				beginTest();
			}
		});
		
		contentView.addView(button, lp);
		return contentView;
	}

	protected void beginTest(){
		String filePath = null;
		
		for(String dirPath : MtaUtils.getMountedUsbDevices("/mnt/usb/")){
			File configFile = new File(dirPath+"/"+CONFIG_FILE_NAME);
			if(configFile.exists()){
				try {
					BufferedReader reader = new BufferedReader(new FileReader(configFile));
					String content = null;
					try {
						while((content = reader.readLine()) != null){
							if(content.contains(USB1_MEADIA_FILE_TAG)){
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
		
		LogUtil.d(TAG, "filePath = "+filePath);
		File mediaFile = null;
		
		if(filePath != null){
			mediaFile = new File(filePath);
		}
		
		if(mediaFile != null && mediaFile.exists()){
			new Thread(new Runnable() {
				
				@Override
				public void run() {
					// TODO Auto-generated method stub
					try {
						Thread.sleep(3000);						
					} catch (InterruptedException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
					
					if (TvS3DManager.getInstance().getCurrent3dType() == TvS3DManager.THREE_DIMENSIONS_TYPE_NONE) {
            					     TvS3DManager.getInstance().set3dDisplayFormat(TvS3DManager.THREE_DIMENSIONS_DISPLAY_FORMAT_AUTO);
					}						
				}
			}).start();		
			
	        Intent intent = new Intent(Intent.ACTION_VIEW);
	        Uri uri = Uri.fromFile(mediaFile);
	        intent.setDataAndType(uri, "video/*");
	        intent.setClassName("com.eostek.mediabrowser", "com.eostek.mediabrowser.videoplayer.VideoPlayerActivity");                    
	        activity.startActivity(intent);
		}else{
			MtaMessageDialog messageDialog = new MtaMessageDialog(activity.getString(R.string.file_3d_not_found), 
					MtaMessageDialog.STYLE_NORMAL, 
					activity.getString(R.string.confirm),
					activity.getString(R.string.cancel));
			
			messageDialog.show(activity.getFragmentManager(), null);
		}			
	}
	
	@Override
	protected void onShown() {
		// TODO Auto-generated method stub
	
	}
	
	@Override
	protected void onHiden() {
		// TODO Auto-generated method stub
		super.onHiden();
	}
}
