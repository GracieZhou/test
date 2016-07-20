
package com.eostek.scifly.browser.settool;

import android.app.Activity;
import android.app.Fragment;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.eostek.scifly.browser.BrowserActivity;
import com.eostek.scifly.browser.R;

public class SetToolFragment extends Fragment {

    private final String TAG = "SetToolFragment";

    BrowserActivity mActivity;

    public SetToolLogic mLogic;

    private SetToolHolder mHolder;

    public static final int MSG_SHOW_HISTORY = 1;

    public Handler mHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            switch (msg.what) {
                case MSG_SHOW_HISTORY:
                    mLogic.showHistory();
                    break;

                default:
                    break;
            }
        };
    };
    
    public SetToolFragment() {
    }

    public SetToolFragment(BrowserActivity activity) {
        mActivity = activity;
        mHolder = new SetToolHolder(activity);
        mLogic = new SetToolLogic(activity);
        mHolder.setLogic(mLogic);
        mLogic.setHolder(mHolder);
    }

    @Override
    public void onAttach(Activity activity) {
        Log.d(TAG, "onAttach");
        super.onAttach(activity);
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        Log.d(TAG, "onCreate");
        super.onCreate(savedInstanceState);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        View view = inflater.inflate(R.layout.settool_layout, container, false);
        mHolder.initView(view);
        mLogic.initData();
        mLogic.setListener();
        return view;
    }

    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_MENU:
                mLogic.showDialog();
                return true;
            case KeyEvent.KEYCODE_DPAD_LEFT:
                mHolder.getCurrentFocusItem();
                Log.d(TAG, "focusItemLayoutLeft=" + mHolder.mCurrentFocusItem);
                if (mHolder.mCurrentFocusItem != null) {
                    mHolder.mCurrentFocusItem.findViewById(R.id.arrow_left).setBackgroundResource(R.drawable.arrow_left_green);
                    ((TextView) (mHolder.mCurrentFocusItem.findViewById(R.id.open_or_remove))).setText(R.string.open_history);
                    return true;
                }
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                mHolder.getCurrentFocusItem();
                Log.d(TAG, "focusItemLayoutRight=" + mHolder.mCurrentFocusItem);
                if (mHolder.mCurrentFocusItem != null) {
                    mHolder.mCurrentFocusItem.findViewById(R.id.arrow_right)
                            .setBackgroundResource(R.drawable.arrow_right_green);
                    ((TextView) (mHolder.mCurrentFocusItem.findViewById(R.id.open_or_remove))).setText(R.string.remove_history);
                    return true;
                }
                break;
        }
        return false;
    }

    public boolean onKeyUp(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_LEFT:
                if (mHolder.mCurrentFocusItem != null) {
                    mHolder.mCurrentFocusItem.findViewById(R.id.arrow_left).setBackgroundResource(R.drawable.arrow_left_white);
                    return true;
                }
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                Log.d(TAG, "focusItemLayoutRight=" + mHolder.mCurrentFocusItem);
                if (mHolder.mCurrentFocusItem != null) {
                    mHolder.mCurrentFocusItem.findViewById(R.id.arrow_right)
                            .setBackgroundResource(R.drawable.arrow_right_white);
                    return true;
                }
                break;
        }
        return false;
    }
}
