
package com.eostek.tv.channel;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.view.KeyEvent;

import com.eostek.tv.utils.ChannelManagerExt;
import com.mstar.android.tvapi.common.vo.ProgramInfo;

public class FavoriteChannelsActivity extends Activity {
    public final static int ITEM_COUNT_ONE_PAGE = 11;

    /**
     * Load the actions related the views
     */
    private FavoriteChannelsHolder mHolder;

    /**
     * channel info
     */
    private List<ProgramInfo> mInfos;

    /**
     * channel utils
     */
    private ChannelManagerExt mManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mManager = ChannelManagerExt.getInstance();
        mInfos = mManager.getFavoriteChannels();
        mHolder = new FavoriteChannelsHolder(this);
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
            mHolder.dismiss();
        }
        if (mInfos != null && mInfos.size() > 0) {
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
        if (mInfos != null && mInfos.size() > 0
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
        } else {
            return false;
        }
        return super.onKeyUp(keyCode, event);
    }
}
