
package com.eostek.tv.threedimensions;

import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * projectName： LLauncher moduleName： SettingAdapter.java
 * 
 * @author chadm.xiang
 * @version 1.0.0
 * @time 2014-8-6 下午1:57:27
 * @Copyright © 2014 Eos Inc.
 */
public class SettingAdapter extends BaseAdapter {

    private Context mContext;

    private List<SettingItemInfo> items = null;

    public SettingAdapter(Context context, List<SettingItemInfo> items) {
        this.mContext = context;
        this.items = items;
    }

    @Override
    public int getCount() {
        return items.size();
    }

    @Override
    public Object getItem(int position) {
        return items.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView != null) {
            return convertView;
        }
        SettingViewHolder holder = new SettingViewHolder();
        SettingItemInfo item = items.get(position);
        convertView = LayoutInflater.from(mContext).inflate(R.layout.setting_list_item, null);
        holder.mTitle = (TextView) convertView.findViewById(R.id.title);
        holder.mContent = (TextView) convertView.findViewById(R.id.content);
        holder.leftArrow = (ImageView) convertView.findViewById(R.id.arrow_left);
        holder.rightArrow = (ImageView) convertView.findViewById(R.id.arrow_right);
        holder.mTitle.setTextColor(Color.WHITE);
        holder.mContent.setTextColor(Color.WHITE);
        holder.mTitle.setText(item.getTitle());
        if (item.getItemType() == Constants.ITEM_DIGITAL) {
            holder.mContent.setText("" + item.getCurValue());
        } else {
            holder.mContent.setText(item.getValues()[item.getCurValue()]);
        }
        if (item.getItemType() == Constants.ITEM_ENUM
                || item.getItemType() == Constants.ITEM_DIGITAL) {
            holder.leftArrow.setVisibility(View.VISIBLE);
            holder.rightArrow.setVisibility(View.VISIBLE);
        }
        return convertView;
    }

    private class SettingViewHolder {
        TextView mTitle;

        TextView mContent;

        ImageView leftArrow;

        ImageView rightArrow;
    }

}
