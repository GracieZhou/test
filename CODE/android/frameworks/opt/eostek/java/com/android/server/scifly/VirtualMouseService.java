
package com.android.server.scifly;

import android.content.Context;
import android.util.Log;
import android.view.KeyEvent;

import scifly.virtualmouse.IVirtualMouseManager;
import scifly.virtualmouse.VirtualMouseNative;

public class VirtualMouseService extends IVirtualMouseManager.Stub {

    public static final String TAG = "VirtualMouseService";

    private Context mContext;

    private boolean mEnabled;

    private long mLastDownTime = 0;

    private long mCurrentDownTime = 0;

    private static int mStep = 8;

    VirtualMouseService(Context context) {
        mContext = context;
    }

    public boolean isVirtualMouseEnabled() {
        return mEnabled;
    }

    public void setVirtualMouseEnabled(boolean enabled) {
        if (mEnabled == enabled) {
            Log.d(TAG, "virtual mouse have " + (enabled ? "opened" : "closed"));
            return;
        }

        mEnabled = enabled;
        if (enabled) {
            VirtualMouseNative.native_open();
        } else {
            VirtualMouseNative.native_close();
        }
    }

    public void toggle() {
        setVirtualMouseEnabled(!mEnabled);
    }

    public boolean dispatchKeyEvent(KeyEvent event) {
        if (!mEnabled) {
            return false;
        }

        int keyCode = event.getKeyCode();
        if (!(keyCode == KeyEvent.KEYCODE_DPAD_LEFT || keyCode == KeyEvent.KEYCODE_DPAD_RIGHT
                || keyCode == KeyEvent.KEYCODE_DPAD_UP || keyCode == KeyEvent.KEYCODE_DPAD_DOWN
                || keyCode == KeyEvent.KEYCODE_DPAD_CENTER || keyCode == KeyEvent.KEYCODE_ENTER)) {
            return false;
        }

        long downTime = event.getDownTime();
        long interval = ((downTime - mCurrentDownTime) + (mCurrentDownTime - mLastDownTime)) / 2;
        if (interval > 1500) {
            mStep = 10;
        } else if (interval > 0) {
            mStep = (int) ((1 / ((float) interval / 1000)) * 15);
        }

        switch (event.getKeyCode()) {
            case KeyEvent.KEYCODE_DPAD_LEFT:
                if (event.getAction() == KeyEvent.ACTION_UP) {
                    return true;
                }

                VirtualMouseNative.native_move(0 - mStep, 0);
                mLastDownTime = mCurrentDownTime;
                mCurrentDownTime = downTime;

                return true;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                if (event.getAction() == KeyEvent.ACTION_UP) {
                    return true;
                }

                VirtualMouseNative.native_move(mStep, 0);
                mLastDownTime = mCurrentDownTime;
                mCurrentDownTime = downTime;

                return true;
            case KeyEvent.KEYCODE_DPAD_UP:
                if (event.getAction() == KeyEvent.ACTION_UP) {
                    return true;
                }

                VirtualMouseNative.native_move(0, 0 - mStep);
                mLastDownTime = mCurrentDownTime;
                mCurrentDownTime = downTime;

                return true;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                if (event.getAction() == KeyEvent.ACTION_UP) {
                    return true;
                }

                VirtualMouseNative.native_move(0, mStep);
                mLastDownTime = mCurrentDownTime;
                mCurrentDownTime = downTime;

                return true;
            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_ENTER:
                if (event.getAction() == KeyEvent.ACTION_UP) {
                    return true;
                }

                VirtualMouseNative.native_click(KeyEvent.KEYCODE_ENTER);
                mLastDownTime = mCurrentDownTime;
                mCurrentDownTime = downTime;

                return true;
            case KeyEvent.KEYCODE_BACK:
                if (event.getAction() == KeyEvent.ACTION_UP) {
                    return true;
                }

                VirtualMouseNative.native_click(KeyEvent.KEYCODE_BACK);
                mLastDownTime = mCurrentDownTime;
                mCurrentDownTime = downTime;

                return true;
            default:
                break;
        }

        return false;
    }

}
