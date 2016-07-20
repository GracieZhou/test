package com.eostek.tv.launcher.ui.adapter;

import java.util.ArrayList;
import java.util.List;

import com.eostek.tv.launcher.R;

import android.content.Context;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

/*
 * projectName： TVLauncher
 * moduleName： AllAppAdapter.java
 *
 * @author chadm.xiang
 * @version 1.0.0
 * @time  2014-7-24 下午2:13:32
 * @Copyright © 2014 Eos Inc.
 */

public class AllAppAdapter extends BaseAdapter {

    private Context mContext;

    private PackageManager pm;

    private List<ResolveInfo> mList;

    public AllAppAdapter(Context context, List<ResolveInfo> list) {
        mContext = context;
        pm = context.getPackageManager();
        mList = new ArrayList<ResolveInfo>();
        for (int i = 0; i < list.size(); i++) {
            mList.add(list.get(i));
        }
    }

    @Override
    public int getCount() {
        return mList.size();
    }

    @Override
    public Object getItem(int position) {
        return mList.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ResolveInfo appInfo = mList.get(position);
        AppItem appItem;
        if (convertView == null) {
            View v = LayoutInflater.from(mContext).inflate(R.layout.appicon_item, null);
            appItem = new AppItem();
            appItem.mAppIcon = (ImageView) v.findViewById(R.id.appicon);
            appItem.mAppTitle = (TextView) v.findViewById(R.id.apptitle);
            v.setTag(appItem);
            convertView = v;
        } else {
            appItem = (AppItem) convertView.getTag();
        }
        // Get the application icon and name
        appItem.mAppIcon.setImageDrawable(appInfo.loadIcon(pm));
        appItem.mAppTitle.setText(appInfo.loadLabel(pm));

        return convertView;
    }

    // the listview item holder
    private class AppItem {
        ImageView mAppIcon;

        TextView mAppTitle;
    }
}
