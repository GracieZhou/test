
package com.eostek.tv.epg.adapter;

import java.util.ArrayList;
import java.util.List;

import com.eostek.tv.R;
import com.eostek.tv.epg.EpgTimerInfo;
import com.eostek.tv.utils.UtilsTools;
import com.eostek.tv.utils.UtilsTools.EnumEventTimerType;
import com.mstar.android.tvapi.common.vo.ProgramInfo;
import com.mstar.android.tvapi.dtv.vo.EpgEventInfo;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * the epg item for epg listview.
 */
public class EpgAdapter extends BaseAdapter {
    private Context mContext;

    private List<ProgramInfo> mChannels = new ArrayList<ProgramInfo>();

    private List<EpgEventInfo> mEventInfos = new ArrayList<EpgEventInfo>();

    private List<EpgTimerInfo> mEpgTimerEvents = new ArrayList<EpgTimerInfo>();

    private long mOffsetTimeInMs = 0;

    private int mCurPosition = 0;

    public EpgAdapter(Context context, List<ProgramInfo> mChannels, List<EpgEventInfo> mEventInfos,
            List<EpgTimerInfo> mEpgTimerEvents, long mOffsetTimeInMs, int mCurPosition) {
        this.mContext = context;
        this.mChannels = mChannels;
        this.mEventInfos = mEventInfos;
        this.mEpgTimerEvents = mEpgTimerEvents;
        this.mOffsetTimeInMs = mOffsetTimeInMs;
        this.mCurPosition = mCurPosition;
    }

    public void setEpgTimerEvents(List<EpgTimerInfo> mEpgTimerEvents) {
        this.mEpgTimerEvents = mEpgTimerEvents;
    }

    public void setEventInfos(List<EpgEventInfo> mEventInfos) {
        this.mEventInfos = mEventInfos;
    }

    public void clearEventInfos() {
        if (mEventInfos != null) {
            mEventInfos.clear();
        }
    }

    public void setCurPosition(int curPosition) {
        this.mCurPosition = curPosition;
    }

    @Override
    public int getCount() {
        return mEventInfos.size();
    }

    @Override
    public Object getItem(int arg0) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @SuppressLint("InflateParams")
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        LayoutInflater inflater = LayoutInflater.from(mContext);
        if (convertView == null) {
            convertView = inflater.inflate(R.layout.epg_event_item, null);
        }
        TextView channelnoTxt = (TextView) convertView.findViewById(R.id.epg_event_starttime);
        channelnoTxt.setText(UtilsTools.formatDate(((long) mEventInfos.get(position).startTime) * 1000
                - mOffsetTimeInMs, "MM/dd  HH:mm"));
        TextView channelnameTxt = (TextView) convertView.findViewById(R.id.epg_event_channelname);
        ImageView statusiconImag = (ImageView) convertView.findViewById(R.id.epg_event_statusicon);
        statusiconImag.setImageResource(R.drawable.one_px);
        channelnameTxt.setText(mEventInfos.get(position).name);
        // adjust event has reminder/record or not, then to show the flag.
        if (mEpgTimerEvents != null && mEpgTimerEvents.size() != 0) {
            for (int i = 0; i < mEpgTimerEvents.size(); i++) {
                if (String.valueOf(mChannels.get(mCurPosition).number).equals(mEpgTimerEvents.get(i).getChannelNum())
                        && mEventInfos.get(position).eventId == mEpgTimerEvents.get(i).getEventId()) {
                    if (mEpgTimerEvents.get(i).getEventTimerType() == EnumEventTimerType.EPG_EVENT_REMIDER.ordinal()) {
                        statusiconImag.setImageResource(R.drawable.menu_icon_time);
                    } else {
                        statusiconImag.setImageResource(R.drawable.pvr_schedule_icon);
                    }
                }
            }
        }
        return convertView;
    }
}
