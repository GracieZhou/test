
package com.eostek.tv.channel;

import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.KeyEvent;

import com.eostek.tv.utils.ChannelManagerExt;
import com.mstar.android.tvapi.common.vo.ProgramInfo;

/**
 * @projectName： EosTvPlayer
 * @moduleName：ChannelListActivity.java
 * @author jachensy.chen
 * @version 1.0.0.10
 * @time 2014-3-7
 * @Copyright © 2014 EOSTEK, Inc.
 */
public class ChannelListActivity extends Activity {
    /**
     * one page max item number
     */
    public final static int ITEM_COUNT_ONE_PAGE = 11;

    /**
     * Load the actions related the views
     */
    private ChannelListHolder mHolder;

    /**
     * channel info
     */
    private List<ProgramInfo> mProgramInfos;

    /**
     * channel utils
     */
    private ChannelManagerExt mManager;

    /**
     * the msg flag
     */
    public static int CHANGEPROGRAM = 0;

    /**
     * the msg delayTime
     */
    public static int DELAYTIME = 1500;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == CHANGEPROGRAM) {
                mManager.programSel(msg.arg1);
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mManager = ChannelManagerExt.getInstance();
        mProgramInfos = mManager.getChannels();
        mHolder = new ChannelListHolder(this, mHandler);
    }

    @Override
    protected void onResume() {
        super.onResume();
        mHolder.show();
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (keyCode == KeyEvent.KEYCODE_BACK) {
            mHandler.removeMessages(CHANGEPROGRAM);
            mHolder.dismiss();
        }
        if (mProgramInfos != null && mProgramInfos.size() > 0) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_DPAD_UP:
                    mHolder.refreshUpEvent();
                    break;
                case KeyEvent.KEYCODE_DPAD_DOWN:
                    mHolder.refreshDownEvent();
                    break;
                default:
                    break;
            }
        } else {
            return false;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean onKeyUp(int keyCode, KeyEvent event) {
        if (mProgramInfos != null && mProgramInfos.size() > 0
                && (keyCode == KeyEvent.KEYCODE_PAGE_UP || keyCode == KeyEvent.KEYCODE_PAGE_DOWN)) {
            switch (keyCode) {
                case KeyEvent.KEYCODE_PAGE_UP:
                    mHolder.refreshPageUpEvent();
                    break;
                case KeyEvent.KEYCODE_PAGE_DOWN:
                    mHolder.refreshPageDownEvent();
                    break;
                default:
                    break;
            }
            mHolder.updateSelectChannel();
        } else {
            return false;
        }
        return super.onKeyUp(keyCode, event);
    }
}
