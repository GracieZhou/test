
package org.wb.app;

import android.app.Activity;
import android.os.Bundle;
import android.view.SurfaceHolder;
import android.view.SurfaceView;

import com.mstar.android.tv.TvCommonManager;
import com.mstar.android.tvapi.common.PictureManager;
import com.mstar.android.tvapi.common.exception.TvCommonException;
import com.mstar.android.tvapi.common.vo.EnumScalerWindow;
import com.mstar.android.tvapi.common.vo.VideoWindowType;

import com.mstar.android.tvapi.factory.FactoryManager;
import com.mstar.android.tvapi.common.exception.TvCommonException; 
import com.mstar.android.tvapi.common.PictureManager;


// ashton debug

/**
 * Description: <br/>
 * site: <a href="http://www.crazyit.org">crazyit.org</a> <br/>
 * Copyright (C), 2001-2014, Yeeku.H.Lee <br/>
 * This program is protected by copyright laws. <br/>
 * Program Name: <br/>
 * Date:
 * 
 * @author Yeeku.H.Lee kongyeeku@163.com
 * @version 1.0
 */
public class SurfaceViewPlayVideo extends Activity implements  SurfaceHolder.Callback {
    SurfaceView surfaceView;

    private int surfaceContainerX = 0;

    private int surfaceContainerY = 0;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.main);
        surfaceView = (SurfaceView) this.findViewById(R.id.surfaceView);
        surfaceView.getHolder().setKeepScreenOn(true);
        surfaceView.getHolder().addCallback(this);
    }

    private void play() {
		if(TvCommonManager.getInstance().getCurrentTvInputSource() != TvCommonManager.INPUT_SOURCE_HDMI){
			TvCommonManager.getInstance().setInputSource(TvCommonManager.INPUT_SOURCE_HDMI);
		}        
        //int[] location = new int[2];
        //surfaceView.getLocationInWindow(location);
        //surfaceContainerX = location[0];
        //surfaceContainerY = location[1];
        // LogUtil.d(TAG,
        // "surfaceContainer width:"+surfaceContainer.getWidth()+" height:"+surfaceContainer.getHeight());
        VideoWindowType videoWindowType = new VideoWindowType();
        videoWindowType.x = 0xffff;
        videoWindowType.y = 0xffff;
        videoWindowType.width = 0xffff;
        videoWindowType.height = 0xffff;
		 
		

        try {
            PictureManager.getInstance().selectWindow(EnumScalerWindow.E_MAIN_WINDOW);
            PictureManager.getInstance().setDisplayWindow(videoWindowType);
            PictureManager.getInstance().scaleWindow();

			PictureManager.getInstance().asGetWbAdjustStar();	
        } catch (TvCommonException e) {
            e.printStackTrace();
        }

    }
	

    @Override
    protected void onDestroy() {	
        super.onDestroy();
    }

    @Override
    public void surfaceCreated(SurfaceHolder holder) {
        play();
    }

    @Override
    public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

    }

    @Override
    public void surfaceDestroyed(SurfaceHolder holder) {

    }
}
