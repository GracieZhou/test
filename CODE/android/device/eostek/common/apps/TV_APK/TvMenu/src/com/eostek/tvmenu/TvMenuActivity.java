
package com.eostek.tvmenu;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;
import android.view.KeyEvent;

public class TvMenuActivity extends Activity {

    public static long DIMISS_DELAY_TIME;

    public TvMenuHolder mHolder;

    public TvMenuLogic mLogic;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.menu);
        mHolder = new TvMenuHolder(this);
        mLogic = new TvMenuLogic(this);
        mHolder.initView();
    }

    @Override
    protected void onResume() {
        super.onResume();
        DIMISS_DELAY_TIME = Settings.System.getInt(this.getContentResolver(), "osdtime", 0);
        if(DIMISS_DELAY_TIME == 0){
        	DIMISS_DELAY_TIME = 5 * 1000;
        	Settings.System.putInt(this.getContentResolver(), "osdtime", (int) DIMISS_DELAY_TIME);
        }
        mHolder.mHandler.sendEmptyMessageDelayed(TvMenuHolder.FINISH, DIMISS_DELAY_TIME);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            mHolder.mHandler.removeMessages(TvMenuHolder.FINISH);
            mHolder.mHandler.sendEmptyMessageDelayed(TvMenuHolder.FINISH, DIMISS_DELAY_TIME);
            switch (event.getKeyCode()) {
            // convert key event from remote to the event recognized by standard
            // VIEW system.
                case KeyEvent.KEYCODE_NUMPAD_ENTER:
                case KeyEvent.KEYCODE_DPAD_CENTER:
                    dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_ENTER));
                    return true;
                case KeyEvent.KEYCODE_VOLUME_UP:
                    dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_RIGHT));
                    return true;
                case KeyEvent.KEYCODE_VOLUME_DOWN:
                    dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_LEFT));
                    return true;
                case KeyEvent.KEYCODE_CHANNEL_UP:
                    dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_UP));
                    return true;
                case KeyEvent.KEYCODE_CHANNEL_DOWN:
                    dispatchKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DPAD_DOWN));
                    return true;
                default:
                    break;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_MENU:
            case KeyEvent.KEYCODE_BACK:
                mHolder.mHandler.removeMessages(TvMenuHolder.FINISH);
                finish();
                return true;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                if (mHolder.mTitleGv.getSelectedItemPosition() == mHolder.mDataList.length - 1
                        && mHolder.mTitleGv.isFocused()) {
                	mHolder.mTitleGv.setSelection(0);
                }
                break;
            case KeyEvent.KEYCODE_DPAD_LEFT:
                if (mHolder.mTitleGv.getSelectedItemPosition() == 0 && mHolder.mTitleGv.isFocused()) {
                    mHolder.mTitleGv.setSelection(mHolder.mDataList.length - 1);
                }
                break;
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    public Handler getHandler() {
        return mHolder.mHandler;
    }

    @Override
    protected void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        if (mHolder.mTitleGv != null) {
            mHolder.mTitleGv.removeAllViewsInLayout();
            mHolder.mTitleGv = null;
        }
        if (mHolder.mHandler != null) {
            mHolder.mHandler.removeMessages(TvMenuHolder.UPDATE_FRAGMENT);
            mHolder.mHandler.removeMessages(TvMenuHolder.FINISH);
            mHolder.mHandler = null;
        }
        if (mHolder.mTitleAdapter != null) {
            mHolder.mTitleAdapter = null;
        }
    }
}
