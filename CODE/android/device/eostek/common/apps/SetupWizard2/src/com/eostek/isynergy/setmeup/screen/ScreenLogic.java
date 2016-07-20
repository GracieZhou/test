
package com.eostek.isynergy.setmeup.screen;

import android.app.Activity;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.eostek.isynergy.setmeup.StateMachineActivity;
import com.eostek.isynergy.setmeup.WizardLogic;
import com.eostek.isynergy.setmeup.screen.moving.ScreenManagerAdapter;
import com.eostek.isynergy.setmeup.screen.moving.ScreenPositionManager;
import com.eostek.isynergy.setmeup.utils.Utils;

public class ScreenLogic {
    private static final String TAG = ScreenLogic.class.getSimpleName();

    private ScreenPositionManager mScreenPositionManager;

    private StateMachineActivity mContext;

    // decrease or increase per click
    private static final int VARIES_DIMENS = 3;
    
    private static int mLeft;

    private static int mTop;

    private static int mWidth;

    private static int mHeight;
    
    private int mScreenWidth = 0;

    private int mScreenHeight = 0;
    
    private ScreenManagerAdapter mScreenManager;
    
    private String[] mCurrentSize;
    
    private int mMaxRight = 0;

    private int mMaxBottom = 0;
    
    @SuppressWarnings("unused")
    private static int mRight;

    @SuppressWarnings("unused")
    private static int mBottom;

    public ScreenLogic(StateMachineActivity context) {
    	mContext = context;
    	mScreenPositionManager = new ScreenPositionManager(context);
    	getScreenSize();
    	setWindowSize(mScreenWidth, mScreenHeight);
    	initScreenPostion();
    }
    
	private void getScreenSize() {
        DisplayMetrics displayMetrics = new DisplayMetrics();
        mContext.getWindowManager().getDefaultDisplay().getMetrics(displayMetrics);
        mScreenWidth = displayMetrics.widthPixels;
        mScreenHeight = displayMetrics.heightPixels;
    }
    
    private void setWindowSize(int width, int height) {
        Window window = mContext.getWindow();
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        // Set window size and transparency
        layoutParams.width = width;
        layoutParams.height = height;
        layoutParams.alpha = 0.9f;
        window.setAttributes(layoutParams);
    }
    
    private void initScreenPostion() {
        mScreenManager = new ScreenManagerAdapter(mContext);
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

    /**
     * move the screen according to the cmd
     * 
     * @param cmd
     */
    public void moving(int cmd) {
        switch (cmd) {
            case WizardLogic.CMD_UP:
            	if (mTop - VARIES_DIMENS > 0) {
                    mTop = mTop - VARIES_DIMENS;
                    setScreenPosition();
                    saveScreenPostion();
                }
                break;
            case WizardLogic.CMD_DOWN:
            	if (mTop + VARIES_DIMENS + mHeight < (mMaxBottom-10)) {
                    mTop = mTop + VARIES_DIMENS;
                    setScreenPosition();
                    saveScreenPostion();
                }
                break;
            case WizardLogic.CMD_LEFT:
            	if (mLeft - VARIES_DIMENS > 0) {
                    mLeft = mLeft - VARIES_DIMENS;
                    setScreenPosition();
                    saveScreenPostion();
                }
                break;
            case WizardLogic.CMD_RIGHT:
            	if ((mLeft + VARIES_DIMENS + mWidth) < (mMaxRight-10)) {
                    mLeft = mLeft + VARIES_DIMENS;
                    setScreenPosition();
                    saveScreenPostion();
                }
                break;
            default:
                break;
        }

    }

    /**
     * scale the screen
     * 
     * @param percent
     */
    public void scale(int percent) {
        Utils.print(TAG, "=======>:" + percent);
        mScreenPositionManager.zoomByPercent(percent);
        mScreenPositionManager.savePostion();
    }

    /**
     * get current scale
     * 
     * @return the scale value
     */
    public int getCurrentScale() {
    	Log.e("test", "getCurrentScale =" + mScreenPositionManager.getRateValue());
        return mScreenPositionManager.getRateValue() - 80;
    }
    
    private void setScreenPosition() {
        mScreenManager.setPosition(mLeft, mTop, mLeft + mWidth, mTop + mHeight, 0);
    }

    private void saveScreenPostion() {
        mScreenManager.savePosition(mLeft, mTop, mWidth, mHeight);
    }
}
