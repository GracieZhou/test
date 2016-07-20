
package com.android.settings.display;

import android.app.Activity;
import android.os.Bundle;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;

import com.android.settings.R;

public class ScreenPositionActivity extends Activity {

    // decrease or increase per click
    private static final int VARIES_DIMENS = 3;

    // record the values of screen position
    private int mLeft;

    private int mTop;

    @SuppressWarnings("unused")
    private int mRight;

    @SuppressWarnings("unused")
    private int mBottom;

    private int mWidth;

    private int mHeight;

    // four orientation image

    private int mMaxRight = 0;

    private int mMaxBottom = 0;

    private ImageView mUpImage;

    private ImageView mDownImage;

    private ImageView mLeftImage;

    private ImageView mRightImage;

    private int mScreenWidth = 0;

    private int mScreenHeight = 0;

    private ScreenManagerAdapter mScreenManager;

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
                if (mLeft - VARIES_DIMENS > 0) {
                    mLeft = mLeft - VARIES_DIMENS;
                    initOrientation();
                    mLeftImage.setImageResource(R.drawable.screen_position_left);
                    setScreenPosition();
                    saveScreenPostion();
                    return true;
                }
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                if ((mLeft + VARIES_DIMENS + mWidth) < (mMaxRight - 10)) {
                    mLeft = mLeft + VARIES_DIMENS;
                    initOrientation();
                    mRightImage.setImageResource(R.drawable.screen_position_right);
                    setScreenPosition();
                    saveScreenPostion();
                    return true;
                }
                break;
            case KeyEvent.KEYCODE_DPAD_UP:
                if (mTop - VARIES_DIMENS > 0) {
                    mTop = mTop - VARIES_DIMENS;
                    initOrientation();
                    mUpImage.setImageResource(R.drawable.screen_position_up);
                    setScreenPosition();
                    saveScreenPostion();
                    return true;
                }
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                if (mTop + VARIES_DIMENS + mHeight < (mMaxBottom - 10)) {
                    mTop = mTop + VARIES_DIMENS;
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
        // Set window size and transparency
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
        mScreenManager = new ScreenManagerAdapter(this);
        mScreenManager.initPostion();
        mCurrentSize = mScreenManager.getCurrentScreenAxis();
        mMaxRight = mScreenManager.getmMaxRight();
        mMaxBottom = mScreenManager.getmMaxBottom();
        mLeft = Integer.parseInt(mCurrentSize[0]);
        mTop = Integer.parseInt(mCurrentSize[1]);
        mRight = Integer.parseInt(mCurrentSize[2]);
        mBottom = Integer.parseInt(mCurrentSize[3]);
        mWidth = Integer.parseInt(mCurrentSize[2]) - Integer.parseInt(mCurrentSize[0]);
        mHeight = Integer.parseInt(mCurrentSize[3]) - Integer.parseInt(mCurrentSize[1]);
    }

    private void setScreenPosition() {
        mScreenManager.setPosition(mLeft, mTop, mLeft + mWidth, mTop + mHeight, 0);
    }

    private void saveScreenPostion() {
        mScreenManager.savePosition(mLeft, mTop, mWidth, mHeight);
    }

}
