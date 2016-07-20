
package com.eostek.tv.player.channelManager.adapter;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.eostek.tv.player.R;
import com.eostek.tv.player.channelManager.ChannelListActivity;
import com.mstar.android.tvapi.common.vo.ProgramInfo;

/**
 * @projectName： EosTvPlayer
 * @moduleName： DtvProgramAdapter.java
 * @author jachensy.chen
 * @version 1.0.0
 * @time 2014-1-20
 * @Copyright © 2013 EOSTEK, Inc.
 */
public class ChannelListAdapter extends BaseAdapter {
    private Context mContext;

    private int mItemTotal = ChannelListActivity.ITEM_COUNT_ONE_PAGE;

    private List<ProgramInfo> mList;

    // Initial value is 0.
    private int mCurrentPage = 0;

    public ChannelListAdapter(Context context, List<ProgramInfo> list) {
        this.mContext = context;
        this.mList = list;
    }

    @Override
    public int getCount() {
        if ((mCurrentPage + 1) * mItemTotal <= mList.size()) {
            return mItemTotal;
        } else {
            return mList.size() % mItemTotal;
        }
    }

    public int getTotalCount() {
        int totalCount = 0;
        totalCount = mList.size();
        return totalCount;
    }

    @Override
    public Object getItem(int arg0) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    public void setCurrentPage(int curpage) {
        mCurrentPage = curpage;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LinearLayout layout = (LinearLayout) LayoutInflater.from(mContext).inflate(
                R.layout.eos_channelitem, null);
        ProgramInfo info = mList.get(mItemTotal * mCurrentPage + position);
        layout.setTag(mItemTotal * mCurrentPage + position);
        TextView channelNum = (TextView) layout.findViewById(R.id.dtvprogram_num);
        int proId = info.number;
        if (proId > 0 && proId < 10) {
            channelNum.setText("00" + proId);
        } else if (proId > 9 && proId < 100) {
            channelNum.setText("0" + proId);
        } else {
            channelNum.setText("" + proId);
        }

        TextView channelName = (TextView) layout.findViewById(R.id.dtvprogram_name);
        channelName.setText(info.serviceName);
        return layout;
    }
}
