
package com.eostek.tv.pvr.adapter;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.eostek.tv.R;
import com.eostek.tv.pvr.PVRFilePreviewActivity;
import com.eostek.tv.pvr.bean.ListviewItemBean;

public class PVRFilePreViewAdapter extends BaseAdapter {

    private ArrayList<ListviewItemBean> mData = null;

    private Context mContext;

    private int mCurRecordIndex;

    public PVRFilePreViewAdapter(Context context, ArrayList<ListviewItemBean> data, int index) {
        this.mContext = context;
        this.mData = data;
        this.mCurRecordIndex = index;
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

    @Override
    public View getView(final int position, View convertView, ViewGroup parent) {
        convertView = LayoutInflater.from(mContext).inflate(R.layout.pvr_listview_item, null);
        ImageView tmpImage = (ImageView) convertView.findViewById(R.id.player_recording_file);
        // is recording.
        if (mCurRecordIndex == position) {
            tmpImage.setVisibility(View.VISIBLE);
            ((PVRFilePreviewActivity) mContext).setRecordingItem(true);
        } else {
            tmpImage.setVisibility(View.INVISIBLE);
            ((PVRFilePreviewActivity) mContext).setRecordingItem(false);
        }

        TextView tmpText = (TextView) convertView.findViewById(R.id.pvr_listview_item_index);
        tmpText.setText("" + (position + 1));

        tmpText = (TextView) convertView.findViewById(R.id.pvr_listview_item_lcn);
        tmpText.setText(mData.get(position).getmRvrLcn());

        tmpText = (TextView) convertView.findViewById(R.id.pvr_listview_item_channel);
        tmpText.setText(mData.get(position).getmPvrChannel());

        tmpText = (TextView) convertView.findViewById(R.id.pvr_listview_item_program);
        tmpText.setText(mData.get(position).getmPvrProgramService());

        return convertView;
    }
}
