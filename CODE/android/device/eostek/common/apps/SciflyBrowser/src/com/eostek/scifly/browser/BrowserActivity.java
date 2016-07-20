
package com.eostek.scifly.browser;

import android.app.Activity;
import android.app.KeyguardManager;
import android.content.Context;
import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.os.Handler;
import android.os.PowerManager;
import android.provider.MediaStore;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.Toast;

import com.eostek.scifly.browser.business.WebViewHelper;
import com.eostek.scifly.browser.collect.CollectItemBean;
import com.eostek.scifly.browser.util.Constants;
import com.mstar.android.MKeyEvent;

/**
 * projectName： Browser moduleName： BrowserActivity.java
 * 
 * @author Shirley.jiang & Ahri.chen
 * @time 2016-1-27 
 */
public class BrowserActivity extends Activity {

    private String TAG = "BrowserActivity";

    public BrowserLogic mLogic;

    public BrowserHolder mHolder;

    private long mCurTime;

    private int mPosition = Constants.POSITION_HOME;

    private int mLastPosition = Constants.POSITION_HOME;

    private static boolean mIsLaunchFromOtherApp = false;

    private boolean mActivityPaused = true;

    public static final String ACTION_RESTART = "--restart--";

    private static final String EXTRA_STATE = "state";

    private Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {

        };
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "Name:SciflyBrowser, Version:2.5.8, Date:2016-01-22, Publisher:Shirley.jiang,Ahri.chen, REV:49994");

        setContentView(R.layout.main_layout);
        mHolder = new BrowserHolder(this);
        mLogic = new BrowserLogic("BrowserActivity", this);

        handleWebSearchIntent(getIntent());
    }

    @Override
    protected void onResume() {
        mActivityPaused = false;
        super.onResume();
    }

    @Override
    protected void onPause() {
        mActivityPaused = true;
        super.onPause();
    }

    public boolean isActivityPaused() {
        return mActivityPaused;
    }

    private void handleWebSearchIntent(Intent intent) {
        if (!WebViewHelper.getInstance(this).handleWebSearchIntent(intent)) {
            String action = intent.getAction();
            if (Intent.ACTION_VIEW.equals(action)
                    || NfcAdapter.ACTION_NDEF_DISCOVERED.equals(action)
                    || Intent.ACTION_SEARCH.equals(action)
                    || MediaStore.INTENT_ACTION_MEDIA_SEARCH.equals(action)
                    || Intent.ACTION_WEB_SEARCH.equals(action)) {
                setIsLaunchFromOtherApp(true);
            }
            WebViewHelper.getInstance(this).onNewIntent(intent);
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Log.d(TAG, "onNewIntent intent=" + intent.toString());
        if (shouldIgnoreIntents()) return;
        if (ACTION_RESTART.equals(intent.getAction())) {
            Bundle outState = new Bundle();
            finish();
            getApplicationContext().startActivity(
                    new Intent(getApplicationContext(), BrowserActivity.class)
                    .addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                    .putExtra(EXTRA_STATE, outState));
            return;
        }
        WebViewHelper.getInstance(BrowserActivity.this).onNewIntent(intent);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_MENU:
                if (mPosition == Constants.POSITION_SETTOOL && mHolder.getSetToolFragment().mLogic.isHistoryPage
                        && mLogic.isShowHomeLayout()) {
                    mHolder.getSetToolFragment().onKeyDown(keyCode, event);
                } else if (mPosition == Constants.POSITION_COLLECT && !mLogic.isCollectWebPage) {
                    if (!CollectItemBean.isDeleteMode) {
                        mHolder.getCollectFragment().showDelCollectItem();
                    } else {
                        mHolder.getCollectFragment().hideDelCollectItem();
                    }
                } else {
                    mHolder.showNineDialog();
                }
                return true;
            case KeyEvent.KEYCODE_DPAD_UP:
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                break;
            case KeyEvent.KEYCODE_DPAD_LEFT:
                if (mPosition == Constants.POSITION_SETTOOL) {
                    if (mHolder.getSetToolFragment().onKeyDown(keyCode, event)) {
                        return true;
                    }
                }
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                if (mPosition == Constants.POSITION_SETTOOL) {
                    if (mHolder.getSetToolFragment().onKeyDown(keyCode, event)) {
                        return true;
                    }
                }
                break;
            case KeyEvent.KEYCODE_BACK:
                if (mIsLaunchFromOtherApp) {
                    mIsLaunchFromOtherApp = false;
                    finish();
                    return true;
                }

                if (!mLogic.isShowHomeLayout()) {
                    mLogic.showHome();
                    return true;
                }
                if (mHolder.getSetToolFragment().mLogic.isClearChoicePage) {
                    mHolder.getSetToolFragment().mLogic.showAdvancedContent();
                } else if (CollectItemBean.isDeleteMode) {
                    mHolder.getCollectFragment().hideDelCollectItem();
                    CollectItemBean.isDeleteMode = false;
                } else if (mHolder.getSetToolFragment().mLogic.isAdvanceSettingPage
                        || mHolder.getSetToolFragment().mLogic.isHistoryPage) {
                    mHolder.getSetToolFragment().mLogic.showSetToolMainPage();
                    mHolder.showMainTileView();
                } else if (mLogic.isCollectWebPage) {
                    mLogic.exitCollectWeb();
                } else {
                    if ((System.currentTimeMillis() - mCurTime) > 2000) {
                        Object mHelperUtils;
                        Toast.makeText(this, getResources().getString(R.string.exit_tip), Toast.LENGTH_SHORT).show();
                        mCurTime = System.currentTimeMillis();

                    } else {
                        finish();
                    }
                }
                break;
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_UP:
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                break;
            case KeyEvent.KEYCODE_DPAD_LEFT:
                if (mPosition == Constants.POSITION_SETTOOL && mHolder.getSetToolFragment().onKeyUp(keyCode, event)) {
                    return true;
                }
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                if (mPosition == Constants.POSITION_SETTOOL && mHolder.getSetToolFragment().onKeyUp(keyCode, event)) {
                    return true;
                }
                break;
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        Log.d(TAG, "dispatchKeyEvent=" + event.toString());
        if (event.getAction() == KeyEvent.ACTION_DOWN
                && (event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_UP || event.getKeyCode() == KeyEvent.KEYCODE_VOLUME_DOWN)) {
            return false;
        } else if (event.getAction() == KeyEvent.ACTION_DOWN
                && (event.getKeyCode() == MKeyEvent.KEYCODE_TV_SETTING || event.getKeyCode() == KeyEvent.KEYCODE_TV_INPUT)) {
            return false;
        }
        if (!mLogic.isShowHomeLayout()) {
            return WebViewHelper.getInstance(this).handleVirtualMouseKey(event) || super.dispatchKeyEvent(event);
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public boolean dispatchGenericMotionEvent(MotionEvent ev) {
        return WebViewHelper.getInstance(this).dispatchGenericMotionEvent(ev) || super.dispatchGenericMotionEvent(ev);
    }

    /**
     * get current position
     * @return
     */
    public int getPosition() {
        return mPosition;
    }

    /**
     * set current position
     * @param position {@link Constants.POSITION_HOME, POSITION_COLLECT, POSITION_SETTOOL}
     */
    public void setPosition(int position) {
        mLastPosition = mPosition;
        this.mPosition = position;
        Log.d(TAG, "position=" + mPosition + ", last position=" + mLastPosition);
    }

    /**
     * get last position
     * @return
     */
    public int getLastPosition() {
        return mLastPosition;
    }

    public static void setIsLaunchFromOtherApp(boolean mIsLaunchFromOtherApp) {
        BrowserActivity.mIsLaunchFromOtherApp = mIsLaunchFromOtherApp;
    }

    @Override
    protected void onDestroy() {
        mHolder.getHomeFragment().getLogic().unregisterReceiver();
        System.exit(0);
        super.onDestroy();
    }

    private KeyguardManager mKeyguardManager;
    private PowerManager mPowerManager;
    private boolean shouldIgnoreIntents() {
        // Only process intents if the screen is on and the device is unlocked
        // aka, if we will be user-visible
        if (mKeyguardManager == null) {
            mKeyguardManager = (KeyguardManager) getSystemService(Context.KEYGUARD_SERVICE);
        }
        if (mPowerManager == null) {
            mPowerManager = (PowerManager) getSystemService(Context.POWER_SERVICE);
        }
        boolean ignore = !mPowerManager.isScreenOn();
        ignore |= mKeyguardManager.inKeyguardRestrictedInputMode();
        Log.v(TAG, "ignore intents: " + ignore);
        return ignore;
    }
}
