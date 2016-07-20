
package com.eostek.tv.channel.adapter;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.eostek.tv.R;
import com.eostek.tv.channel.ChannelListEditActivity;
import com.mstar.android.tvapi.common.vo.ProgramInfo;

/**
 * @projectName： EosTvPlayer
 * @moduleName： ChannelEditlistAdapter.java
 * @author jachensy.chen
 * @version 1.0.0
 * @time 2014-1-24
 * @Copyright © 2013 EOSTEK, Inc.
 */
public class ChannelEditlistAdapter extends BaseAdapter {

    private List<ProgramInfo> mChannelList = new ArrayList<ProgramInfo>();

    private LayoutInflater mLayoutInflater;

    private Context mContext;

    public ChannelEditlistAdapter(Context context, List<ProgramInfo> channelList) {
        mContext = context;
        mChannelList = channelList;
        mLayoutInflater = LayoutInflater.from(context);
    }

    @Override
    public int getCount() {
        return mChannelList.size();
    }

    @Override
    public Object getItem(int arg0) {
        return mChannelList.get(arg0);
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View arg1, ViewGroup arg2) {
        ViewHolder viewHolder = null;
        ProgramInfo info = mChannelList.get(position);
        if (arg1 == null) {
            viewHolder = new ViewHolder();
            arg1 = mLayoutInflater.inflate(R.layout.channel_edit_list_item, null);
            viewHolder.channelNumberTxt = (TextView) arg1.findViewById(R.id.edit_item_channel_num);
            viewHolder.channelNameTxt = (TextView) arg1.findViewById(R.id.edit_item_channel_name);
            viewHolder.channelLoveImg = (ImageView) arg1.findViewById(R.id.edit_item_channel_love);
            viewHolder.channelLockImg = (ImageView) arg1.findViewById(R.id.edit_item_channel_lock);
            viewHolder.channelMoveImg = (ImageView) arg1.findViewById(R.id.edit_item_channel_move);
            viewHolder.channelHideImg = (ImageView) arg1.findViewById(R.id.edit_item_channel_hide);
            arg1.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) arg1.getTag();
        }
        int proId = info.number;
        if (proId > 0 && proId < 10) {
            viewHolder.channelNumberTxt.setText("00" + proId);
        } else if (proId > 9 && proId < 100) {
            viewHolder.channelNumberTxt.setText("0" + proId);
        } else {
            viewHolder.channelNumberTxt.setText("" + proId);
        }
        viewHolder.channelNameTxt.setText(info.serviceName);

        if (info.favorite == 1) {
            viewHolder.channelLoveImg.setBackgroundResource(R.drawable.icon_fav);
        } else {
            viewHolder.channelLoveImg.setBackgroundResource(R.drawable.one_px);
        }

        if (info.isLock) {
            viewHolder.channelLockImg.setBackgroundResource(R.drawable.icon_lock);
        } else {
            viewHolder.channelLockImg.setBackgroundResource(R.drawable.one_px);
        }

        if (((ChannelListEditActivity) mContext).isMove()
                && position == ((ChannelListEditActivity) mContext).getMoveIndex()) {
            viewHolder.channelMoveImg.setBackgroundResource(R.drawable.icon_arrow);
        } else {
            viewHolder.channelMoveImg.setBackgroundResource(R.drawable.one_px);
        }
        if (info.isSkip) {
            viewHolder.channelHideImg.setBackgroundResource(R.drawable.icon_hide);
        } else {
            viewHolder.channelHideImg.setBackgroundResource(R.drawable.one_px);
        }

        return arg1;
    }

    final class ViewHolder {
        TextView channelNumberTxt;

        TextView channelNameTxt;

        ImageView channelLoveImg;

        ImageView channelLockImg;

        ImageView channelMoveImg;

        ImageView channelHideImg;
    }
}
