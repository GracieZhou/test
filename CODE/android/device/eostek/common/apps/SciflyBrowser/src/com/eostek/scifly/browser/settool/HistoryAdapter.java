package com.eostek.scifly.browser.settool;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.eostek.scifly.browser.R;
import com.eostek.scifly.browser.modle.UrlModle;

public class HistoryAdapter extends BaseAdapter{

    private Context mContext;

    private LayoutInflater mInflater;

    private List<UrlModle> mList;

    public HistoryAdapter(Context context, List<UrlModle> list) {
        mContext = context;
        mInflater = LayoutInflater.from(mContext);
        mList = list;
    }

    public void setDataList(ArrayList<UrlModle> list) {
        if (list != null) {
            mList = (List<UrlModle>) list.clone();
            notifyDataSetChanged();
        }
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return position;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = (LinearLayout)mInflater.inflate(R.layout.historys_item, null);
        }
        TextView textView = (TextView) convertView.findViewById(R.id.title);
        String title = mList.get(position).mTitle;
        String url = mList.get(position).mUrl;
        if (title.length() > 20) {
            title = title.substring(0, 20) + "...";
        }
        if (url.length() > 40) {
            url = url.substring(0, 40) + "...";
        }
        textView.setText(title + "    " + url);
        return convertView;
    }
}