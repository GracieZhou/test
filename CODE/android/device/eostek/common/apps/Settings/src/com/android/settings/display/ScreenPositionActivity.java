
package com.android.settings.display;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.android.settings.R;

public class ScreenPositionActivity extends Activity {


    // decrease or increase per click
    private static final int VARIES_DIMENS = 3;

    // record the values of screen position
    private static int mLeft;

    private static int mTop;

    private static int mRight;

    private static int mBottom;

    private static int mWidth;

    private static int mHeight;

    // four orientation image

    private int mMaxRight = 0;

    private int mMaxBottom = 0;
    
    private ImageView mUpImage;

    private ImageView mDownImage;

    private ImageView mLeftImage;

    private ImageView mRightImage;

    private int mScreenWidth = 0;

    private int mScreenHeight = 0;

    private ScreenPositionManager mScreenPositionManager;

    private String[] mCurrentSize;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_screen_position);
        initUI();
        initScreenPostion();
    }

    private void initUI() {
        mUpImage = (ImageView) findViewById(R.id.screen_position_up);
        mDownImage = (ImageView) findViewById(R.id.screen_position_down);
        mLeftImage = (ImageView) findViewById(R.id.screen_position_left);
        mRightImage = (ImageView) findViewById(R.id.screen_position_right);
        initOrientation();
        getScreenSize();
        setWindowSize(mScreenWidth, mScreenHeight);
    }

    private void initOrientation() {
        mUpImage.setImageResource(R.drawable.screen_position_up_white);
        mDownImage.setImageResource(R.drawable.screen_position_down_white);
        mLeftImage.setImageResource(R.drawable.screen_position_left_white);
        mRightImage.setImageResource(R.drawable.screen_position_right_white);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_LEFT:
                mLeft = mLeft - VARIES_DIMENS;
                if (mLeft > 0) {
                    initOrientation();
                    mLeftImage.setImageResource(R.drawable.screen_position_left);
                    setScreenPosition();
                    saveScreenPostion();
                    return true;
                }
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                mLeft = mLeft + VARIES_DIMENS;
                if ((mLeft + mWidth) < (mMaxRight-10)) {
                    initOrientation();
                    mRightImage.setImageResource(R.drawable.screen_position_right);
                    setScreenPosition();
                    saveScreenPostion();
                    return true;
                }
                break;
            case KeyEvent.KEYCODE_DPAD_UP:
                mTop = mTop - VARIES_DIMENS;
                if (mTop > 0) {
                    initOrientation();
                    mUpImage.setImageResource(R.drawable.screen_position_up);
                    setScreenPosition();
                    saveScreenPostion();
                    return true;
                }
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                mTop = mTop + VARIES_DIMENS;
                if (mTop + mHeight < (mMaxBottom-10)) {
                    initOrientation();
                    mDownImage.setImageResource(R.drawable.screen_position_down);
                    setScreenPosition();
                    saveScreenPostion();
                    return true;
                }
                break;
            case KeyEvent.KEYCODE_BACK:
                this.finish();
                return true;
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_DPAD_UP
                || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT || keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
            mLeftImage.setImageResource(R.drawable.screen_position_left_white);
            mRightImage.setImageResource(R.drawable.screen_position_right_white);
            mUpImage.setImageResource(R.drawable.screen_position_up_white);
            mDownImage.setImageResource(R.drawable.screen_position_down_white);
            return true;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void setWindowSize(int width, int height) {
        Window window = getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        // 设置窗口的大小及透明度
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

    private void initScreenPostion() {
        mScreenPositionManager = new ScreenPositionManager(this);
        mScreenPositionManager.initPostion();
        mCurrentSize = mScreenPositionManager.getCurrentScreenAxis();
        mMaxRight = mScreenPositionManager.getmMaxRight();
        mMaxBottom = mScreenPositionManager.getmMaxBottom();
        mLeft = Integer.parseInt(mCurrentSize[0]);
        mTop = Integer.parseInt(mCurrentSize[1]);
        mRight = Integer.parseInt(mCurrentSize[2]);
        mBottom = Integer.parseInt(mCurrentSize[3]);
        mWidth = Integer.parseInt(mCurrentSize[2]) - Integer.parseInt(mCurrentSize[0]);
        mHeight = Integer.parseInt(mCurrentSize[3]) - Integer.parseInt(mCurrentSize[1]);
    }

    private void setScreenPosition() {
        mScreenPositionManager.setPosition(mLeft, mTop, mLeft + mWidth, mTop + mHeight, 0);
    }

    private void saveScreenPostion() {
        mScreenPositionManager.savePosition(mLeft, mTop, mWidth, mHeight);
    }

}
