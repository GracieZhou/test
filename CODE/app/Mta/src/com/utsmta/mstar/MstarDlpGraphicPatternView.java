package com.utsmta.mstar;

import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

import android.app.Activity;
import android.app.ProgressDialog;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.Button;

import com.mstar.android.tvapi.common.PictureManager;
import com.utsmta.app.R;
import com.utsmta.common.MtaPagerView;

public class MstarDlpGraphicPatternView extends MtaPagerView {
	private static final String TAG = "MstarDlpGraphicPatternView";

	private Button showBtn = null;
	
	private ProgressDialog progressDialog = null;
	
	public MstarDlpGraphicPatternView(Activity activity) {
		super(activity);
		// TODO Auto-generated constructor stub
	}
	
	private OnClickListener onShowListener = new OnClickListener() {
		
		@Override
		public void onClick(View v) {
			// TODO Auto-generated method stub
			showDlpPatterns();
		}
	};
	
	@Override
	protected View onCreateView(android.view.LayoutInflater inflater) {
		View contentView = inflater.inflate(R.layout.amta_pattern, null, false);
		showBtn = (Button) contentView.findViewById(R.id.show_btn);
		showBtn.setOnClickListener(onShowListener);
		return contentView;		
	};
		
	private void showDlpPatterns(){
		progressDialog = ProgressDialog.show(activity, "", "");
		new Thread(new Runnable() {
			
			@Override
			public void run() {
				// TODO Auto-generated method stub
				String enumNames [] = {
						"SolidField", "HorizontalRamp", "VerticalRamp", "HorizontalLines",
						"DiagonalLines", "VerticalLines", "Grid", "Checkboard",
						"ANSICheckboard", "None"
				};
				
				try {
					Class enumCls = Class.forName("com.mstar.android.tvapi.common.PictureManager$TPGraphics");
					Class<?> pictureManagerCls = PictureManager.class;
					try {
						Method method = pictureManagerCls.getMethod("dlpSetTestPattern", enumCls);
						
						PictureManager instance = PictureManager.getInstance();
						for(int i = 0; i < enumNames.length; ++i){	
							try {
								method.invoke(instance, Enum.valueOf(enumCls, enumNames[i]));
							} catch (IllegalAccessException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							} catch (IllegalArgumentException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							} catch (InvocationTargetException e1) {
								// TODO Auto-generated catch block
								e1.printStackTrace();
							}

							try {
								Thread.sleep(2500);
							} catch (InterruptedException e) {
								// TODO Auto-generated catch block
								e.printStackTrace();
							}
						}
						
					} catch (NoSuchMethodException e) {
						// TODO Auto-generated catch block
						e.printStackTrace();
					}
				} catch (ClassNotFoundException e1) {
					// TODO Auto-generated catch block
					e1.printStackTrace();
				}
				
				if(progressDialog != null && progressDialog.isShowing()){
					progressDialog.dismiss();
				}
			}
		}).start();
	}

}
