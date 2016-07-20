
package com.eostek.tv.player.channelManager;

import java.util.List;

import android.annotation.SuppressLint;
import android.os.Handler;
import android.os.Message;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;

import com.eostek.tv.player.util.ChannelManagerExt;
import com.mstar.android.tvapi.common.vo.ProgramInfo;

/**
 * @projectName： EosTvPlayer
 * @moduleName： DtvProgramListener.java
 * @author jachensy.chen
 * @version 1.0.0
 * @time 2014-1-20
 * @Copyright © 2013 EOSTEK, Inc.
 */
public class ChannelListListener {

    public static int sChangeProgram = 0;

    public static int sDelayTime = 1500;

    private ChannelListViewHolder mHolder;

    private ChannelManagerExt mManager;

    private ChannelListActivity mContext;

    public ChannelListListener(ChannelListViewHolder holder, ChannelListActivity context,
            ChannelManagerExt manager, List<ProgramInfo> infos) {
        this.mHolder = holder;
        this.mManager = manager;
        this.mContext = context;
    }

    Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == sChangeProgram) {
                mManager.programSel(msg.arg1);
            }
        }
    };

    public void setListener() {
        mHolder.getDtvList().setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
                mHandler.removeMessages(sChangeProgram);
                mManager.programSel(position + ((mContext.getCurPage() - 1))
                        * ChannelListActivity.ITEM_COUNT_ONE_PAGE);
            }
        });
        mHolder.getDtvList().setOnGenericMotionListener(new View.OnGenericMotionListener() {
            @SuppressLint("NewApi")
            @Override
            public boolean onGenericMotion(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_SCROLL
                        && !mHolder.getDtvList().isInTouchMode()) {
                    float vscroll = event.getAxisValue(MotionEvent.AXIS_VSCROLL);
                    if (vscroll != 0) {
                        mHolder.getAnimatedSelector().hideView();
                    }
                }
                return false;
            }
        });
        mHolder.getDtvList().setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                showSelector(hasFocus);
            }
        });
        mHolder.getDtvList().setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View view, int position, long arg3) {
                mHolder.getAnimatedSelector().ensureViewVisible();
                mHandler.removeMessages(sChangeProgram);
                Message msg = new Message();
                msg.what = sChangeProgram;
                msg.arg1 = position + ((mContext.getCurPage() - 1))
                        * ChannelListActivity.ITEM_COUNT_ONE_PAGE;
                mHandler.sendMessageDelayed(msg, sDelayTime);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                mHolder.getAnimatedSelector().hideView();
            }
        });
    }

    private void showSelector(boolean bShow) {
        if (mHolder.getAnimatedSelector() == null)
            return;
        if (bShow) {
            mHolder.getAnimatedSelector().ensureViewVisible();
        } else {
            mHolder.getAnimatedSelector().hideView();
        }
    }

    public Handler getHandler() {
        return mHandler;
    }
}
