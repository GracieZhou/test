
package com.android.settings.display;

import com.android.settings.R;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ProgressBar;

public class ScreenZoomActivity extends Activity {
    public static final String TAG = "ScreenZoomActivity";

    private static final int MAX_HEIGHT = 100;

    private static final int MIN_HEIGHT = 80;

    private int mScreenWidth = 0;

    private int mScreenHeight = 0;

    private int mProgress = 0;

    private ProgressBar mScreenZoomProgressBar;

    private ScreenManagerAdapter mScreenManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_zoom);
        refresh();
    }

    private void initUI() {
        getScreenSize();
        setWindowSize(mScreenWidth, mScreenHeight);
        initProgressBar();

    }

    private void refresh() {
        initScreenPostion();
        initUI();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_LEFT:
                if (mProgress > 0) {
                    mProgress = mProgress - 1;
                    Log.d(TAG, ">>left>>>mProgress = " + mProgress);
                    setScreenZoomByPercent(mProgress + MIN_HEIGHT);
                    mScreenZoomProgressBar.setProgress(mProgress);
                    saveScreenPosition();
                }
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                if (mProgress < 20) {
                    mProgress = mProgress + 1;
                    Log.d(TAG, ">>right>>mProgress = " + mProgress);
                    setScreenZoomByPercent(mProgress + MIN_HEIGHT);
                    mScreenZoomProgressBar.setProgress(mProgress);
                    saveScreenPosition();
                }
                break;
            case KeyEvent.KEYCODE_BACK:
                this.finish();
                break;
        }

        refresh();
        // return true means no reflection to above functions
        return false;
    }

    private void setWindowSize(int width, int height) {
        Window window = getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        // Set window size and transparency.
        layoutParams.width = width;
        layoutParams.height = height;
        layoutParams.alpha = 0.9f;
        window.setAttributes(layoutParams);
    }

    private void getScreenSize() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        mScreenWidth = displayMetrics.widthPixels;
        mScreenHeight = displayMetrics.heightPixels;
    }

    private void initProgressBar() {
        mProgress = (getScreenRateValue() - MIN_HEIGHT);
        Log.e(TAG, ">>init>>>mProgress = " + mProgress);
        mScreenZoomProgressBar = (ProgressBar) findViewById(R.id.screenZoomProgressBar);
        mScreenZoomProgressBar.setProgress(mProgress);
    }

    private void initScreenPostion() {
        mScreenManager = new ScreenManagerAdapter(this);
        mScreenManager.initPostion();
    }

    private void setScreenZoomByPercent(int percent) {
        mScreenManager.zoomByPercent(percent);
    }

    private void saveScreenPosition() {
        mScreenManager.savePostion();
    }

    private int getScreenRateValue() {
        int currentRateValue = mScreenManager.getRateValue();
        if (currentRateValue < MIN_HEIGHT) {
            currentRateValue = MIN_HEIGHT;
        } else if (currentRateValue > MAX_HEIGHT) {
            currentRateValue = MAX_HEIGHT;
        }
        return currentRateValue;
    }
}
