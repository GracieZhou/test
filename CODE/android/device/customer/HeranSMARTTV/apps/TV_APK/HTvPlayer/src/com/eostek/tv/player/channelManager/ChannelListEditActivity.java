
package com.eostek.tv.player.channelManager;

import java.util.ArrayList;
import java.util.List;

import com.eostek.tv.player.R;
import com.eostek.tv.player.channelManager.adapter.ChannelEditlistAdapter;
import com.eostek.tv.player.dialog.PasswordCheckDialog;
import com.eostek.tv.player.util.AnimatedSelector;
import com.eostek.tv.player.util.ChannelManagerExt;
import com.mstar.android.tv.TvChannelManager;
import com.mstar.android.tv.TvCommonManager;
import com.mstar.android.tvapi.common.vo.EnumFavoriteId;
import com.mstar.android.tvapi.common.vo.EnumProgramAttribute;
import com.mstar.android.tvapi.common.vo.ProgramInfo;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

/**
 * to edit channel list include favorite\move\ship\lock.
 * 
 * @projectName： EosTvPlayer
 * @moduleName：ChannelListEditActivity.java
 * @author jachensy.chen
 * @version 1.0.0
 * @time 2014-3-7
 * @Copyright © 2014 EOSTEK, Inc.
 */
public class ChannelListEditActivity extends Activity {
    private static final String TAG = ChannelListEditActivity.class.getSimpleName();

    private ListView mChannels_lv;

    private ImageView mSelector;

    private ChannelEditlistAdapter mAdapter;

    private AnimatedSelector channelSelector;

    private List<ProgramInfo> mChannels = new ArrayList<ProgramInfo>();

    private int mCurPosition = 0;

    private boolean isMove = false;

    private int mMoveIndex = -1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.eos_channel_edit);
        mChannels = ChannelManagerExt.getInstance().getChannelsAll();
        if (mChannels.size() <= 0) {
            ChannelManagerExt.getInstance().getAllChannels(this, TvCommonManager.getInstance().getCurrentTvInputSource());
            mChannels = ChannelManagerExt.getInstance().getChannelsAll();
        }
        // if no programs,show the toast and exit.
        if (mChannels.size() <= 0) {
            Toast.makeText(this, R.string.noprogram_tip, Toast.LENGTH_SHORT).show();
            finish();
            return;
        }
        mChannels_lv = (ListView) findViewById(R.id.channel_edit_list);
        mAdapter = new ChannelEditlistAdapter(this, mChannels);
        mChannels_lv.setAdapter(mAdapter);

        mSelector = (ImageView) findViewById(R.id.channel_selector);
        channelSelector = new AnimatedSelector(mSelector, mChannels_lv.getSelector());
        channelSelector.setTopOffset(getResources().getInteger(R.integer.channelList_edit_top_off_set));
        mChannels_lv.setSelector(channelSelector);

        setListener();
    }

    @Override
    protected void onStop() {
        super.onStop();
        finish();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            ProgramInfo info = mChannels.get(mCurPosition);
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_BACK:
                    // to reset channel after channels edit.
                    ChannelManagerExt.getInstance().getAllChannels(this, TvCommonManager.INPUT_SOURCE_DTV);
                    finish();
                    return true;
                case KeyEvent.KEYCODE_PROG_RED:
                    dofavorite(info);
                    return true;
                case KeyEvent.KEYCODE_PROG_GREEN:
                    // show password dialog.
                    new PasswordCheckDialog(this).show();
                    return true;
                case KeyEvent.KEYCODE_PROG_YELLOW:
                    doMove();
                    return true;
                case KeyEvent.KEYCODE_PROG_BLUE:
                    doSkip(info);
                    return true;
                default:
                    break;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    /**
     * set favorite channel.
     * 
     * @param info
     */
    private void dofavorite(ProgramInfo info) {
        Log.e(TAG, "favorite:" + mChannels.get(mChannels_lv.getSelectedItemPosition()).favorite);
        if (mChannels.get(mCurPosition).favorite == 0) {
            mChannels.get(mCurPosition).favorite = 1;
            TvChannelManager.getInstance().addProgramToFavorite(EnumFavoriteId.E_FAVORITE_ID_1, info.number,
                    info.serviceType, 0x00);
            mAdapter.notifyDataSetChanged();
        } else {
            mChannels.get(mCurPosition).favorite = 0;
            TvChannelManager.getInstance().deleteProgramFromFavorite(EnumFavoriteId.E_FAVORITE_ID_1, info.number,
                    info.serviceType, 0x00);
            mAdapter.notifyDataSetChanged();
        }
    }

    /**
     * lock channel.
     */
    public void doLock() {
        ProgramInfo info = mChannels.get(mCurPosition);
        boolean isLock = info.isLock;
        isLock = !isLock;
        info.isLock = isLock;
        TvChannelManager.getInstance().setProgramAttribute(TvChannelManager.PROGRAM_ATTRIBUTE_LOCK, info.number, 
                info.serviceType, 0x00, isLock);
        mAdapter.notifyDataSetChanged();
    }

    /**
     * move channel.
     */
    private void doMove() {
        if (!isMove && mMoveIndex == -1) {
            isMove = true;
            mMoveIndex = mCurPosition;
            mAdapter.notifyDataSetChanged();
        } else if (isMove && mMoveIndex != -1) {
            isMove = false;
            mMoveIndex = -1;
            mAdapter.notifyDataSetChanged();
        }
    }

    /**
     * hide channel.
     * 
     * @param info
     */
    private void doSkip(ProgramInfo info) {
        Log.e(TAG, "skip:" + mChannels.get(mChannels_lv.getSelectedItemPosition()).isSkip);
        if (mChannels.get(mCurPosition).isSkip) {
            mChannels.get(mCurPosition).isSkip = false;
            ChannelManagerExt.getInstance().setProgramAttribute(EnumProgramAttribute.E_SKIP, info.number,
                    info.serviceType, 0x00, false);
            mAdapter.notifyDataSetChanged();
        } else {
            int unSipCount = 0;
            for (int i = 0; i < mChannels.size(); i++) {
                if (!mChannels.get(i).isSkip) {
                    unSipCount++;
                    if (unSipCount == 2) {
                        break;
                    }
                }
            }
            // you can't hide all channels.
            if (unSipCount == 1) {
                Toast.makeText(this, R.string.cantskipall, Toast.LENGTH_LONG).show();
            } else {
                mChannels.get(mCurPosition).isSkip = true;
                ChannelManagerExt.getInstance().setProgramAttribute(EnumProgramAttribute.E_SKIP, info.number,
                        info.serviceType, 0x00, true);
                mAdapter.notifyDataSetChanged();
            }
        }
    }

    private void setListener() {
        mChannels_lv.setOnGenericMotionListener(new View.OnGenericMotionListener() {
            @SuppressLint("NewApi")
            @Override
            public boolean onGenericMotion(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_SCROLL && !mChannels_lv.isInTouchMode()) {
                    float vscroll = event.getAxisValue(MotionEvent.AXIS_VSCROLL);
                    if (vscroll != 0) {
                        channelSelector.hideView();
                    }
                }
                return false;
            }
        });
        mChannels_lv.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                showChannelSelector(hasFocus);
            }
        });
        mChannels_lv.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View view, int position, long arg3) {
                mCurPosition = position;
                if (isMove) {
                    if (mMoveIndex == mCurPosition) {
                        return;
                    } else {
                        ChannelManagerExt.getInstance().move(mMoveIndex, mCurPosition);
                        ProgramInfo LastInfo = mChannels.get(mMoveIndex);
                        mChannels.remove(mMoveIndex);
                        mChannels.add(mCurPosition, LastInfo);
                        mAdapter.notifyDataSetChanged();
                        mMoveIndex = mCurPosition;
                    }
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                channelSelector.hideView();
            }
        });
    }

    private void showChannelSelector(boolean bShow) {
        if (channelSelector == null)
            return;
        if (bShow) {
            channelSelector.ensureViewVisible();
        } else {
            channelSelector.hideView();
        }
    }

    public boolean isMove() {
        return isMove;
    }

    public int getMoveIndex() {
        return mMoveIndex;
    }
    
    public ChannelEditlistAdapter getAdapter() {
        return mAdapter;
    }
}
