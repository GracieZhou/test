
package com.eostek.tv.epg.adapter;

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
import com.eostek.tv.epg.EpgTimerInfo;
import com.eostek.tv.utils.UtilsTools;
import com.eostek.tv.utils.UtilsTools.EnumEventTimerType;

public class ScheduleAdapter extends BaseAdapter {

    List<EpgTimerInfo> mData = null;

    private Context mContext;

    public ScheduleAdapter(Context context, List<EpgTimerInfo> data) {
        mContext = context;
        mData = data;
    }

    @Override
    public int getCount() {
        return mData.size();
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(R.layout.schedule_list_item,
                    null);
        }

        TextView startTimeTxt = (TextView) convertView.findViewById(R.id.schedule_time);
        TextView durationTxt = (TextView) convertView.findViewById(R.id.schedule_duration);
        TextView channelNumTxt = (TextView) convertView.findViewById(R.id.schedule_channelnum);
        TextView channelNameTxt = (TextView) convertView.findViewById(R.id.schedule_channelname);
        TextView epgNameTxt = (TextView) convertView.findViewById(R.id.schedule_epgname);
        ImageView mscheduleicon = (ImageView) convertView.findViewById(R.id.schedule_type);

        startTimeTxt.setText(UtilsTools.formatDate(mData.get(position).getStartTime(),
                "MM/dd HH:mm"));
        durationTxt.setText((mData.get(position).getEndTime() - mData.get(position).getStartTime())
                / (1000 * 60) + " "+mContext.getString(R.string.schedule_str_time_minute));
        channelNumTxt.setText(mData.get(position).getChannelNum());
        channelNameTxt.setText(mData.get(position).getServiceType());
        epgNameTxt.setText(mData.get(position).getProgramName());
        int type = mData.get(position).getEventTimerType();
        if (type == EnumEventTimerType.EPG_EVENT_REMIDER.ordinal()) {
            mscheduleicon.setImageResource(R.drawable.menu_icon_time);
        } else {
            mscheduleicon.setImageResource(R.drawable.pvr_schedule_icon);
        }
        return convertView;
    }
}
