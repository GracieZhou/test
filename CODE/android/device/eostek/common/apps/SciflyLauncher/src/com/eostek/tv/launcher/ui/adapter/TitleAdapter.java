
package com.eostek.tv.launcher.ui.adapter;

import java.util.List;

import com.eostek.tv.launcher.R;

import android.content.Context;
import android.content.res.Configuration;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.RelativeLayout;
import android.widget.TextView;

/*
 * projectName： TVLauncher
 * moduleName： TitleAdapter.java
 *
 * @author chadm.xiang
 * @version 1.0.0
 * @time  2014-7-10 下午1:53:27
 * @Copyright © 2014 Eos Inc.
 */

public class TitleAdapter extends BaseAdapter {

    private Context mContext;

    private List<String> titList;

    public TitleAdapter(Context context, List<String> titles) {
        this.mContext = context;
        this.titList = titles;
    }

    /*
     * (non-Javadoc)
     * @see android.widget.Adapter#getCount()
     */
    @Override
    public int getCount() {
        return titList == null ? 0 : titList.size();
    }

    /*
     * (non-Javadoc)
     * @see android.widget.Adapter#getItem(int)
     */
    @Override
    public Object getItem(int position) {
        return titList.get(position);
    }

    /*
     * (non-Javadoc)
     * @see android.widget.Adapter#getItemId(int)
     */
    @Override
    public long getItemId(int position) {
        return position;
    }

    /*
     * (non-Javadoc)
     * @see android.widget.Adapter#getView(int, android.view.View,
     * android.view.ViewGroup)
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        String name = titList.get(position);
        ViewHolder holder;
        if (convertView == null) {
            holder = new ViewHolder();
            View view = LayoutInflater.from(mContext).inflate(R.layout.title_gridview_item, null);
            holder.mTitle = (TextView) view.findViewById(R.id.title);
            view.setTag(holder);
            convertView = view;
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        int width = mContext.getResources().getInteger(R.integer.title_adapter_title_width);
        int height = mContext.getResources().getInteger(R.integer.title_adapter_title_height);
        int delta = mContext.getResources().getInteger(R.integer.title_adapter_title_delta);
        RelativeLayout.LayoutParams lpParams = new RelativeLayout.LayoutParams(name.length() * width + delta, height
                + delta);
        lpParams.addRule(RelativeLayout.CENTER_IN_PARENT, RelativeLayout.TRUE);
        holder.mTitle.setLayoutParams(lpParams);
        holder.mTitle.setText(name);
        // to solve set big text
        Configuration config = mContext.getResources().getConfiguration();
        if (config.fontScale == 1.0f) {
            holder.mTitle.setTextSize(25);
        } else {
            holder.mTitle.setTextSize(19);
        }

        return convertView;
    }

    class ViewHolder {
        TextView mTitle;
    }

}
