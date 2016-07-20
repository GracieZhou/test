
package com.eostek.tv.player.channelManager;

import android.app.Activity;
import android.view.View;
import android.widget.ListView;
import android.widget.TextView;

import com.eostek.tv.player.R;
import com.eostek.tv.player.util.AnimatedSelector;

/**
 * @projectName： EosTvPlayer
 * @moduleName： DtvProgramViewHolder.java
 * @author jachensy.chen
 * @version 1.0.0
 * @time 2014-1-20
 * @Copyright © 2013 EOSTEK, Inc.
 */
public class ChannelListViewHolder {
    private Activity mContext;

    private ListView mDtvListView;

    private TextView mDtvPageTxt;

    private TextView mTitleTxt;

    private View mSelector;

    private AnimatedSelector animatedSelector;

    public ChannelListViewHolder(Activity context) {
        this.mContext = context;
    }

    public void findViews() {
        mTitleTxt = (TextView) mContext.findViewById(R.id.list_title);
        mDtvListView = (ListView) mContext.findViewById(R.id.dtvprogram_lv);
        mDtvPageTxt = (TextView) mContext.findViewById(R.id.dtvpage);
        mSelector = mContext.findViewById(R.id.channel_selector);
        animatedSelector = new AnimatedSelector(mSelector, mDtvListView.getSelector());
        mDtvListView.setSelector(animatedSelector);
        animatedSelector.setLeftOffset(20);
    }

    public ListView getDtvList() {
        return mDtvListView;
    }

    public void setDtvList(ListView dtvList) {
        this.mDtvListView = dtvList;
    }

    public TextView getDtvPage() {
        return mDtvPageTxt;
    }

    public void setDtvPage(TextView dtvPage) {
        this.mDtvPageTxt = dtvPage;
    }

    public TextView getTitleTxt() {
        return mTitleTxt;
    }

    public View getSelector() {
        return mSelector;
    }

    public AnimatedSelector getAnimatedSelector() {
        return animatedSelector;
    }
}
