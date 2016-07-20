package com.utsmta.mstar;

import android.app.Activity;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.widget.TextView;

import com.mstar.android.tvapi.common.PictureManager;
import com.mstar.android.tvapi.common.exception.TvCommonException;
import com.mstar.android.tvapi.common.vo.PanelProperty;
import com.utsmta.app.R;
import com.utsmta.common.FactoryItem;
import com.utsmta.common.MtaPagerView;
import com.utsmta.mstar.inspect.MstarVideoInspect;
import com.utsmta.utils.LogUtil;

public class MstarVideoView extends MtaPagerView {
	private final String TAG = "MstarVideoSubView";
	
	private SurfaceView surfaceView = null;
	
	private SurfaceHolder surfaceHolder = null;
	
	private View surfaceContainer = null;
	
	private int surfaceContainerX = 0;
	
	private int surfaceContainerY = 0;
	
	private int surfaceContainerW = 0;
	
	private int surfaceContainerH = 0;
	
	private float xScale = 1;
	
	private float yScale = 1;	
	
	private MstarVideoInspect videoInspect = null;
	
	private TextView operateTipView = null;
	
	public MstarVideoView(Activity activity) {
		super(activity);
		// TODO Auto-generated constructor stub
		videoInspect = new MstarVideoInspect();
		createView();
	}
	
	@Override
	protected View onCreateView(LayoutInflater inflater) {
		// TODO Auto-generated method stub
		View contentView = inflater.inflate(R.layout.video, null, true);
		
		operateTipView = (TextView) contentView.findViewById(R.id.operate_tip);
		
		surfaceContainer = contentView.findViewById(R.id.surface_container);    	   	
    	
		surfaceView = (SurfaceView) contentView.findViewById(R.id.video_surface);
		surfaceHolder = surfaceView.getHolder();
		surfaceHolder.addCallback(new SurfaceHolder.Callback() {
			
			@Override
			public void surfaceDestroyed(SurfaceHolder arg0) {
				// TODO Auto-generated method stub
				LogUtil.d(TAG, "surfaceDestroyed");				
			}
			
			@Override
			public void surfaceCreated(SurfaceHolder arg0) {
				// TODO Auto-generated method stub
				LogUtil.d(TAG, "surfaceCreated");
			}
			
			@Override
			public void surfaceChanged(SurfaceHolder arg0, int arg1, int arg2, int arg3) {
				// TODO Auto-generated method stub
				LogUtil.d(TAG, "surfaceChanged");
                try {
					PanelProperty pp = PictureManager.getInstance().getPanelWidthHeight();
					LogUtil.d(TAG, "pp width:"+pp.width+" height:"+pp.height);
					DisplayMetrics dm = new DisplayMetrics();  
			        activity.getWindowManager().getDefaultDisplay().getMetrics(dm);
			        LogUtil.d(TAG, "dm width:"+dm.widthPixels+" height:"+dm.heightPixels);
			        
			        if(pp.width != 0 && dm.widthPixels != 0){
			        	xScale = (float)pp.width/(float)(dm.widthPixels);
			        }
			        
			        if(pp.height != 0 && dm.heightPixels != 0){
			        	yScale = (float)(pp.height)/(float)(dm.heightPixels);
			        }
			        LogUtil.d(TAG, "xScale"+xScale+" yScale:"+yScale);
				} catch (TvCommonException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
                
                int[] location =new int[2];
                surfaceContainer.getLocationOnScreen(location);
                surfaceContainerX = location[0];
                surfaceContainerY = location[1];
                
                surfaceContainerW = surfaceContainer.getWidth();
                surfaceContainerH = surfaceContainer.getHeight();

        		if(videoInspect != null){
                    LogUtil.d(TAG, "surfaceContainer width:"+surfaceContainerW+" height:"+surfaceContainerH);
                    videoInspect.setPipDimens((int)(surfaceContainerX*xScale), (int)(surfaceContainerY*yScale), 
                    		(int)(surfaceContainerW*xScale), (int)(surfaceContainerH*yScale));
                    videoInspect.setPipscale();
        		}
			}
		});
		
		return contentView;
	}
	
	@Override
	public void onShown() {
		// TODO Auto-generated method stub
		LogUtil.d(TAG, "onShown");
		super.onShown();
		videoInspect.startInspect();
	}
	
	@Override
	public void onHiden() {
		// TODO Auto-generated method stub
		LogUtil.d(TAG, "onHiden");
		videoInspect.stopInspect();
		super.onHiden();
	}

	@Override
	public void onItemSelected(FactoryItem item) {
		// TODO Auto-generated method stub
		operateTipView.setText(item.getProperty("operate_tip"));
		videoInspect.setVideoSource(item.getProperty("source"));
	}
}
