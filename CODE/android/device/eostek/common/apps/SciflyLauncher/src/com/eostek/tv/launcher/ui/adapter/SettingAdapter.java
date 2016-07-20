
package com.eostek.tv.launcher.ui.adapter;

import java.util.ArrayList;
import java.util.List;

import com.eostek.tv.launcher.R;
import com.eostek.tv.launcher.model.SettingItemInfo;
import com.eostek.tv.launcher.util.LConstants;
import com.eostek.tv.launcher.util.UIUtil;

import android.content.Context;
import android.content.SharedPreferences;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * projectName： TVLauncher moduleName： SettingAdapter.java
 * 
 * @author chadm.xiang
 * @version 1.0.0
 * @time 2014-8-6 下午1:57:27
 * @Copyright © 2014 Eos Inc.
 */
public class SettingAdapter extends BaseAdapter {

    private Context mContext;

    private String[] titles;

    // the video setting item count
    private final int videoItems = 3;

    private List<View> views = new ArrayList<View>();

    private List<String> contentList = new ArrayList<String>();

    private int shareness = -1;

    private int aspectRate = -1;

    private int skipStartEnd = -1;

    private final String SHARPNESS = "sharpness";

    private final String ASPECT_RATIO = "aspect_ratio";

    private final String SKIP_START_END = "skip_start_end";

    private List<SettingItemInfo> items = null;

    public SettingAdapter(Context context, SharedPreferences preferences) {
        this.mContext = context;
        shareness = preferences.getInt(SHARPNESS, 0);
        aspectRate = preferences.getInt(ASPECT_RATIO, 0);
        skipStartEnd = preferences.getInt(SKIP_START_END, 0);
        titles = mContext.getResources().getStringArray(R.array.setting_item_title);
        contentList.add(mContext.getResources().getStringArray(R.array.sharpness_array)[shareness]);
        contentList.add(mContext.getResources().getStringArray(R.array.aspect_ratio_array)[aspectRate]);
        contentList.add(mContext.getResources().getStringArray(R.array.skip_start_end_array)[skipStartEnd]);
        contentList.add(mContext.getResources().getString(R.string.get_lastest_launcher_data));
        contentList.add("V" + UIUtil.getVersionName(mContext));
        contentList.add(mContext.getResources().getString(R.string.reset_application));
    }

    public SettingAdapter(Context context, List<SettingItemInfo> itms) {
        this.mContext = context;
        this.items = itms;
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

        holder.mTitle.setText(item.getTitle());
        holder.mContent.setText(item.getValues()[item.getCurValue()]);
        if (item.getItemType() == LConstants.ITEM_ENUM) {
            holder.leftArrow.setVisibility(View.VISIBLE);
            holder.rightArrow.setVisibility(View.VISIBLE);
        } 
        return convertView;
    }

    // @Override
    // public int getCount() {
    // return titles.length;
    // }
    //
    // @Override
    // public Object getItem(int position) {
    // return titles[position];
    // }
    //
    // @Override
    // public long getItemId(int position) {
    // return position;
    // }
    // @Override
    // public View getView(int position, View convertView, ViewGroup parent) {
    // SettingViewHolder holder;
    // if (convertView == null) {
    // holder = new SettingViewHolder();
    // View view =
    // LayoutInflater.from(mContext).inflate(R.layout.setting_list_item, null);
    // holder.mTitle = (TextView) view.findViewById(R.id.title);
    // holder.mContent = (TextView) view.findViewById(R.id.content);
    // holder.leftArrow = (ImageView) view.findViewById(R.id.arrow_left);
    // holder.rightArrow = (ImageView) view.findViewById(R.id.arrow_right);
    // view.setTag(holder);
    // convertView = view;
    // views.add(convertView);
    // } else {
    // holder = (SettingViewHolder) convertView.getTag();
    // }
    // // holder.mTitle.setLayoutParams(new
    // LayoutParams(LayoutParams.MATCH_PARENT,60));
    // holder.mTitle.setText(titles[position]);
    // holder.mContent.setText(contentList.get(position));
    // if (position < videoItems) {
    // holder.leftArrow.setVisibility(View.VISIBLE);
    // holder.rightArrow.setVisibility(View.VISIBLE);
    // }
    // return convertView;
    // }
    /**
     * set text to content TextView
     * 
     * @param position The item postion
     * @param content The text to show
     */
    public void setTextContent(int position, String content) {
        View view = views.get(position);
        SettingViewHolder holder = (SettingViewHolder) view.getTag();
        holder.mContent.setText(content);
    }

    private class SettingViewHolder {
        TextView mTitle;

        TextView mContent;

        ImageView leftArrow;

        ImageView rightArrow;
    }

}
