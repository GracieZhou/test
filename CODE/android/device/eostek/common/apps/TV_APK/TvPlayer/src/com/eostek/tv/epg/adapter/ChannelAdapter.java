
package com.eostek.tv.epg.adapter;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

import com.eostek.tv.R;
import com.mstar.android.tvapi.common.vo.EnumServiceType;
import com.mstar.android.tvapi.common.vo.ProgramInfo;

public class ChannelAdapter extends BaseAdapter {
    private Context mContext;

    private List<ProgramInfo> mChannels = new ArrayList<ProgramInfo>();

    public ChannelAdapter(Context context, List<ProgramInfo> mChannels) {
        this.mContext = context;
        this.mChannels = mChannels;
    }

    @Override
    public int getCount() {
        return mChannels.size();
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
            convertView = inflater.inflate(R.layout.epg_channel_item, null);
        }
        TextView channelNoTxt = (TextView) convertView.findViewById(R.id.epg_channelno);
        channelNoTxt.setText(mChannels.get(position).number + "");
        TextView channelTypeTxt = (TextView) convertView.findViewById(R.id.epg_channeltype);
        if (EnumServiceType.E_SERVICETYPE_DTV.ordinal() == mChannels.get(position).serviceType) {
            channelTypeTxt.setText(R.string.dtv);
        } else if (EnumServiceType.E_SERVICETYPE_RADIO.ordinal() == mChannels.get(position).serviceType) {
            channelTypeTxt.setText(R.string.radio);
        } else if (EnumServiceType.E_SERVICETYPE_DATA.ordinal() == mChannels.get(position).serviceType) {
            channelTypeTxt.setText(R.string.data);
        }
        TextView channelnameTxt = (TextView) convertView.findViewById(R.id.epg_channelname);
        channelnameTxt.setText(mChannels.get(position).serviceName);
        return convertView;
    }
}
