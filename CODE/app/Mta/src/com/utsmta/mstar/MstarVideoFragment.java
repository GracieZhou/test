package com.utsmta.mstar;

import com.mstar.android.tvapi.common.exception.TvCommonException;
import com.mstar.android.tvapi.common.vo.PanelProperty;

import com.mstar.android.tvapi.common.PictureManager;
import com.utsmta.app.R;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ListView;

import com.utsmta.common.FactoryGroup;
import com.utsmta.common.FactoryItem;
import com.utsmta.common.MtaListAdapter;
import com.utsmta.common.WizardFragment;
import com.utsmta.mstar.inspect.MstarVideoInspect;
import com.utsmta.utils.LogUtil;

public class MstarVideoFragment extends WizardFragment {
	private final String TAG = "MstarVideoFragment";
	
	private ListView sourceLv = null;
	
	private SurfaceView surfaceView = null;
	
	private SurfaceHolder surfaceHolder = null;
	
	private View surfaceContainer = null;
	
	private int surfaceContainerX = 0;
	
	private int surfaceContainerY = 0;
	
	private int surfaceContainerW = 0;
	
	private int surfaceContainerH = 0;
	
	private float xScale = 1;
	
	private float yScale = 1;
	
	private MtaListAdapter listAdapter = null;
	
	private MstarVideoInspect videoInspect = null;
	
	private void initData(){

		listAdapter = new MtaListAdapter(getActivity(), getFactoryGroup().getAllItems());
		
		videoInspect = new MstarVideoInspect();
	}
	
	OnItemSelectedListener onItemSelectedListener = new AdapterView.OnItemSelectedListener() {

		@Override
		public void onItemSelected(AdapterView<?> parent, View view,
				int position, long id) {
			// TODO Auto-generated method stub
			FactoryItem item = getFactoryGroup().getItem(position);
			videoInspect.setVideoSource(item.getProperty("source"));
		}

		@Override
		public void onNothingSelected(AdapterView<?> parent) {
			// TODO Auto-generated method stub
			videoInspect.setVideoSource("none");
		}
	};
	
	OnItemClickListener onItemClickListener = new OnItemClickListener() {

		@Override
		public void onItemClick(AdapterView<?> parent, View view, int position,
				long id) {
			// TODO Auto-generated method stub
			FactoryItem item = getFactoryGroup().getItem(position);
			item.setResult(!item.getResult());
			listAdapter.notifyDataSetChanged();
		}
	};
	
	public MstarVideoFragment(FactoryGroup factoryGroup) {
		super(factoryGroup);
		// TODO Auto-generated constructor stub		
	}

	@Override
	public void onAttach(Activity activity) {
		// TODO Auto-generated method stub
		super.onAttach(activity);
		LogUtil.d(TAG, "onAttach");
		initData();
		
	}
	
	@Override
	public void onDetach() {
		// TODO Auto-generated method stub
		LogUtil.d(TAG, "onDetach");	
		super.onDetach();
	}
	
	@Override
	public void onStart() {
		// TODO Auto-generated method stub
		super.onStart();
		LogUtil.d(TAG, "onStart");
		videoInspect.startInspect();
	}
	
	@Override
	public void onStop() {
		// TODO Auto-generated method stub
		LogUtil.d(TAG, "onStop");
		videoInspect.stopInspect();
		super.onStop();
	}
	
	@Override
	protected void onMoveToNextMsgReceived() {
		// TODO Auto-generated method stub
		videoInspect.stopInspect();
		super.onMoveToNextMsgReceived();
	}
	
	@Override
	public View onCreateSubView(LayoutInflater inflater) {
		// TODO Auto-generated method stub
		View contentView = inflater.inflate(R.layout.video, null, false);
		
		sourceLv = (ListView) contentView.findViewById(R.id.source_lv);
		sourceLv.setAdapter(listAdapter);
		sourceLv.setOnItemClickListener(onItemClickListener);
		sourceLv.setOnItemSelectedListener(onItemSelectedListener);
		sourceLv.requestFocus();
		
		surfaceContainer = contentView.findViewById(R.id.surface_container);    	   	
    	
		surfaceView = (SurfaceView) contentView.findViewById(R.id.video_surface);
		surfaceHolder = surfaceView.getHolder();
		surfaceHolder.addCallback(new SurfaceHolder.Callback() {
			
			@Override
			public void surfaceDestroyed(SurfaceHolder arg0) {
				// TODO Auto-generated method stub
//				videoInspect.setFullscale();
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
			        getActivity().getWindowManager().getDefaultDisplay().getMetrics(dm);
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
}
