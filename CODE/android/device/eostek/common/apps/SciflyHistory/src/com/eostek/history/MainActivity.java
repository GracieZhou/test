
package com.eostek.history;

import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;

import com.eostek.history.R;

/**
 * Main activity of history.
 */
public class MainActivity extends Activity {

    /**
     * Tag used to show in logcat.
     */
    public static final String TAG = "MainActivity";

    HistoryHolder mHolder;

    HistoryListener mListener;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.i(TAG, "Name:SciflyHistory, Version:1.0.1 Date:2015-09-02, Publisher:Davis.Wang REV:41066");

        setContentView(R.layout.main_layout);

        mHolder = new HistoryHolder(this);

        mListener = new HistoryListener(mHolder, this);

        mHolder.getViews();

        mListener.setListener();

    }

    /**
     * Get holder.
     * 
     * @return
     */
    public HistoryHolder getHolder() {
        return mHolder;
    }

    /**
     * Get listener.
     * 
     * @return
     */
    public HistoryListener getListener() {
        return mListener;
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.i("main ", "" + mHolder.isGridViewFocused());
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_DOWN:
                mHolder.findNextFocus(KeyEvent.KEYCODE_DPAD_DOWN);
                return true;
            case KeyEvent.KEYCODE_DPAD_UP:
                mHolder.findNextFocus(KeyEvent.KEYCODE_DPAD_UP);
                return true;
            case KeyEvent.KEYCODE_DPAD_LEFT:

                if (mHolder.isMoreItemsWindowShowing()) {
                    return false;
                }

                if (!mHolder.isGridViewFirstItemSelected()) {
                    return true;
                }
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                if (mHolder.isGridViewFocused()) {
                    return true;
                }
                break;
        }

        return super.onKeyDown(keyCode, event);
    }

    @Override
    public void onBackPressed() {
        if (mHolder.onBackPressed()) {
            Log.i(TAG, "onBackPressed");
            super.onBackPressed();
        } else {
            Log.i(TAG, "back to normal");
        }
    }

}
