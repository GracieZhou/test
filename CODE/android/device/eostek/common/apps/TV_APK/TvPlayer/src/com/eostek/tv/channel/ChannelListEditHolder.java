
package com.eostek.tv.channel;

import java.util.List;

import android.annotation.SuppressLint;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.ListView;

import com.eostek.tv.R;
import com.eostek.tv.channel.adapter.ChannelEditlistAdapter;
import com.eostek.tv.widget.AnimatedSelector;
import com.mstar.android.tvapi.common.vo.ProgramInfo;

public class ChannelListEditHolder {
    private ChannelListEditActivity mContext;

    private ListView mChannels_lv;

    private ImageView mSelector;

    private AnimatedSelector mChannelSelector;

    /**
     * channel info
     */
    private List<ProgramInfo> mChannels = null;

    private ChannelEditlistAdapter mAdapter;

    /**
     * @param mContext
     * @param mChannels
     */
    public ChannelListEditHolder(ChannelListEditActivity mContext, List<ProgramInfo> mChannels) {
        this.mContext = mContext;
        this.mChannels = mChannels;
        initViews();
        initAdapter();
        setListener();
    }

    /**
     * initialize the views
     */
    private void initViews() {
        mContext.setContentView(R.layout.channel_list_edit_activity);
        mChannels_lv = (ListView) mContext.findViewById(R.id.channel_edit_list);
        mSelector = (ImageView) mContext.findViewById(R.id.channel_selector);
        mChannelSelector = new AnimatedSelector(mSelector, mChannels_lv.getSelector());
        mChannelSelector.setTopOffset(mContext.getResources().getInteger(
                R.integer.channelList_edit_top_off_set));
    }

    /**
     * initialize the adapter
     */
    private void initAdapter() {
        mAdapter = new ChannelEditlistAdapter(mContext, mChannels);
        mChannels_lv.setAdapter(mAdapter);
        mChannels_lv.setSelector(mChannelSelector);

    }

    /**
     * register the listener
     */
    private void setListener() {
        mChannels_lv.setOnGenericMotionListener(new View.OnGenericMotionListener() {
            @SuppressLint("NewApi")
            @Override
            public boolean onGenericMotion(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_SCROLL && !mChannels_lv.isInTouchMode()) {
                    float vscroll = event.getAxisValue(MotionEvent.AXIS_VSCROLL);
                    if (vscroll != 0) {
                        mChannelSelector.hideView();
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
    }

    /**
     * refresh the adapter
     */
    public void refreshAdapter() {
        mAdapter.notifyDataSetChanged();
    }

    public void showChannelSelector(boolean isShow) {
        if (isShow) {
            mChannelSelector.ensureViewVisible();
        } else {
            mChannelSelector.hideView();
        }
    }

    public ListView getmChannels_lv() {
        return mChannels_lv;
    }
}
