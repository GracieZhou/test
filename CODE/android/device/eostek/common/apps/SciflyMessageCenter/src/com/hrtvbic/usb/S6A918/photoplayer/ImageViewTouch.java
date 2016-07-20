package com.hrtvbic.usb.S6A918.photoplayer;
import android.content.Context;
import android.util.AttributeSet;
import android.view.KeyEvent;

public class ImageViewTouch extends ImageViewTouchBase {
	
	private Context mContext = null;
	private boolean mEnableTrackballScroll;

	public ImageViewTouch(Context context) {
		super(context);
		mContext = context;
	}

	public ImageViewTouch(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
	}

	public void setEnableTrackballScroll(boolean enable) {
		mEnableTrackballScroll = enable;
	}

	protected void postTranslateCenter(float dx, float dy) {
		super.postTranslate(dx, dy);
		center(true, true);
	}

	private static final float PAN_RATE = 20;

	// This is the time we allow the dpad to change the image position again.
	private long mNextChangePositionTime;

	// @Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {

		if (keyCode == KeyEvent.KEYCODE_BACK) {
			return false;
		}
		return super.onKeyDown(keyCode, event);
	}
}
