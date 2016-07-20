package com.utsmta.mstar;

import android.app.Activity;
import android.app.ProgressDialog;
import android.os.Handler;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnKeyListener;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.utsmta.app.R;
import com.mstar.android.tv.TvCommonManager;
import com.mstar.android.tvapi.common.PictureManager;
import com.mstar.android.tvapi.common.exception.TvCommonException;
import com.mstar.android.tvapi.common.vo.EnumScalerWindow;
import com.mstar.android.tvapi.common.vo.PanelProperty;
import com.mstar.android.tvapi.common.vo.VideoWindowType;
import com.mstar.android.tvapi.factory.FactoryManager;
import com.mstar.android.tvapi.factory.vo.EnumAdcSetIndexType;
import com.mstar.android.tvapi.factory.vo.PqlCalibrationData;
import com.utsmta.app.MainActivity;
import com.utsmta.common.FactoryItem;
import com.utsmta.common.MtaPagerView;
import com.utsmta.utils.LogUtil;


public class MstarAutoAdcAdjustView extends MtaPagerView {
	
	public static final String TAG = MstarAutoAdcAdjustView.class.toString();
	private Button passBtn = null;
	private Button failBtn = null;
	private Button adjustBtn = null;	
	
//	private ProgressDialog progressDialog = null;
	
	private TextView rGain = null;
	private TextView gGain = null;
	private TextView bGain = null;
	private TextView rOffset = null;
	private TextView gOffset = null;
	private TextView bOffset = null;
	
	private TextView adc_result = null;
	
	private TextView phase = null;
	
	SurfaceView surfaceView;
	private SurfaceHolder surfaceHolder = null;
	private View adc_surfaceContainer = null;
	
	private int surfaceContainerX = 0;
	
	private int surfaceContainerY = 0;
	
	private int surfaceContainerW = 0;
	
	private int surfaceContainerH = 0;
	
	private float xScale = 1;
	private float yScale = 1;
	
	//private TextView operateTipView = null;
		
	private Handler uiHandler = new Handler() {
		public void handleMessage(android.os.Message msg) {
//			if (progressDialog != null) {
//				progressDialog.dismiss();
//			}

			if (msg.what == 0) {
				Toast.makeText(activity, "ADC Adjust Failed!", Toast.LENGTH_SHORT).show();
				adc_result.setText(activity.getString(R.string.adc_test)+":"+activity.getString(R.string.fail)); 
			} else if (msg.what == 1) {
				Toast.makeText(activity, "ADC Adjust Successed!", Toast.LENGTH_SHORT).show();
				FactoryManager fm = FactoryManager.getInstance();
				PqlCalibrationData caliData;
				try {
					caliData = fm.getAdcGainOffset(EnumScalerWindow.E_MAIN_WINDOW, EnumAdcSetIndexType.E_ADC_SET_VGA);
					rGain.setText(activity.getString(R.string.adc_rgain)+":"+caliData.redGain);
					gGain.setText(activity.getString(R.string.adc_ggain)+":"+caliData.greenGain);
					bGain.setText(activity.getString(R.string.adc_bgain)+":"+caliData.blueGain);
					rOffset.setText(activity.getString(R.string.adc_roffset)+":"+caliData.redOffset);
					gOffset.setText(activity.getString(R.string.adc_goffset)+":"+caliData.greenOffset);
					bOffset.setText(activity.getString(R.string.adc_boffset)+":"+caliData.blueOffset);
					adc_result.setText(activity.getString(R.string.adc_test)+": "+activity.getString(R.string.pass));

				} catch (TvCommonException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

			}else if(msg.what == 2){
				if(TvCommonManager.INPUT_SOURCE_VGA != TvCommonManager.getInstance().getCurrentTvInputSource()){
		   			TvCommonManager.getInstance().setInputSource(TvCommonManager.INPUT_SOURCE_VGA);	
				}
			}
		};
	};
	

	private OnClickListener onAdjustClickListener = new OnClickListener() {

		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			startAutoAdjust();
		}
	};

	private OnKeyListener adjusOnKeyListener = new OnKeyListener() {		
		@Override
		public boolean onKey(View v, int keyCode, KeyEvent event) {
			// TODO Auto-generated method stub
			return false;
		}
	};
	
	public MstarAutoAdcAdjustView(Activity activity) {
		super(activity);
		// TODO Auto-generated constructor stub		
		createView();
	}
	
	@Override
	protected View onCreateView(LayoutInflater inflater) {
		// TODO Auto-generated method stub
		View contentView = inflater.inflate(R.layout.amta_adc, null, true);
		passBtn = (Button) contentView.findViewById(R.id.pass_btn);
		failBtn = (Button) contentView.findViewById(R.id.fail_btn);
		adjustBtn = (Button) contentView.findViewById(R.id.adjust_btn);
		
		adjustBtn.setOnClickListener(onAdjustClickListener);	
		adjustBtn.setOnKeyListener(adjusOnKeyListener);

		rGain = (TextView) contentView.findViewById(R.id.rgain);
		gGain = (TextView) contentView.findViewById(R.id.ggain);
		bGain = (TextView) contentView.findViewById(R.id.bgain);
		rOffset = (TextView) contentView.findViewById(R.id.roffset);
		gOffset = (TextView) contentView.findViewById(R.id.goffset);
		bOffset = (TextView) contentView.findViewById(R.id.boffset);

		adc_result = (TextView) contentView.findViewById(R.id.adc_result);

		adc_surfaceContainer = contentView.findViewById(R.id.adc_container);

		surfaceView = (SurfaceView) contentView.findViewById(R.id.adc_surface);
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

					if (pp.width != 0 && dm.widthPixels != 0) {
						xScale = (float) pp.width / (float) (dm.widthPixels);
					}

					if (pp.height != 0 && dm.heightPixels != 0) {
			        	yScale = (float)(pp.height)/(float)(dm.heightPixels);
					}
					LogUtil.d(TAG, "xScale" + xScale + " yScale:" + yScale);
				} catch (TvCommonException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}

				int[] location = new int[2];
				adc_surfaceContainer.getLocationOnScreen(location);
				surfaceContainerX = location[0];
				surfaceContainerY = location[1];
				surfaceContainerW = adc_surfaceContainer.getWidth();
				surfaceContainerH = adc_surfaceContainer.getHeight();

				// LogUtil.d(TAG,
				// "surfaceContainer width:"+surfaceContainer.getWidth()+" height:"+surfaceContainer.getHeight());
				VideoWindowType videoWindowType = new VideoWindowType();
				videoWindowType.x = (int) (surfaceContainerX * xScale);
				videoWindowType.y = (int) (surfaceContainerY * yScale);
				videoWindowType.width = (int) (surfaceContainerW * xScale);
				videoWindowType.height = (int) (surfaceContainerH * yScale);

				try {
            PictureManager.getInstance().selectWindow(EnumScalerWindow.E_MAIN_WINDOW);
            PictureManager.getInstance().setDisplayWindow(videoWindowType);
					PictureManager.getInstance().scaleWindow();

				} catch (TvCommonException e) {
					e.printStackTrace();
				}
			}
		});

		return contentView;					      		
	}

	private void startAutoAdjust() {
		rGain.setText(activity.getString(R.string.adc_rgain));
		gGain.setText(activity.getString(R.string.adc_ggain));
		bGain.setText(activity.getString(R.string.adc_bgain));
		rOffset.setText(activity.getString(R.string.adc_roffset));
		gOffset.setText(activity.getString(R.string.adc_goffset));
		bOffset.setText(activity.getString(R.string.adc_boffset));

		adc_result.setText(activity.getString(R.string.adc_test));

		Thread thread = new Thread(new Runnable() {

			@Override
			public void run() {
				// TODO Auto-generated method stub
				try {
					if (FactoryManager.getInstance().autoAdc()) {
						uiHandler.sendEmptyMessage(1);
					} else {
						uiHandler.sendEmptyMessage(0);
					}
				} catch (TvCommonException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
			}
		});
		thread.start();
	}
	
	@Override
	public void onShown() {
		// TODO Auto-generated method stub
		LogUtil.d(TAG, "onShown");
		super.onShown();		
	}
	
	@Override
	public void onHiden() {
		// TODO Auto-generated method stub
		uiHandler.removeMessages(2);
		LogUtil.d(TAG, "onHiden");		
		super.onHiden();
	}

	@Override
	public void onItemSelected(FactoryItem item) {
		// TODO Auto-generated method stub
		uiHandler.sendEmptyMessageDelayed(2, 800);
		
	}
}
