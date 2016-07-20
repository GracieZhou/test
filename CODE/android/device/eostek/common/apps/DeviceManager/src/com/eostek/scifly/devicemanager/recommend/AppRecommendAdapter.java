
package com.eostek.scifly.devicemanager.recommend;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.eostek.scifly.devicemanager.R;
import com.eostek.scifly.devicemanager.ui.DownloadProcessView;
import com.eostek.scifly.devicemanager.util.Util;

public class AppRecommendAdapter extends BaseAdapter {

    private LayoutInflater minflater;

    private List<AppRecommendInfo> list;

    private Context context;

    public AppRecommendAdapter(final Context context, final List<AppRecommendInfo> list) {
        minflater = LayoutInflater.from(context);
        this.list = list;
        this.context = context;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Object getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ViewHolder viewHolder = null;
        if (convertView == null) {
            viewHolder = new ViewHolder();
            convertView = minflater.inflate(R.layout.fragment_recommend_gridview_item, null);
            viewHolder.mImageView = (ImageView) convertView.findViewById(R.id.fragment_recommend_gridview_item_iv_icon);
            viewHolder.mName = (TextView) convertView.findViewById(R.id.fragment_recommend_gridview_item_tv_name);
            viewHolder.mPorcessView = (DownloadProcessView)convertView.findViewById(R.id.fragment_recommend_gridview_item_process_view);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        
        list.get(position).setmPosition(position);
        
        bindView(viewHolder, list.get(position));

        return convertView;
    }

    private void bindView(ViewHolder viewHolder, AppRecommendInfo info) {
        if (info == null) {
            return;
        }
        if (Util.checkApkExist(context, info.getmPkgName())) {
            info.setmIsInstall(true);
            viewHolder.mPorcessView.setProgress(100);
        } else {
            info.setmIsInstall(false);
        }
        if (info.getmIcon() != null) {
            viewHolder.mImageView.setImageDrawable(info.getmIcon());
        } else {
            viewHolder.mImageView.setImageDrawable(context.getResources().getDrawable(R.drawable.default_icon));
        }
        viewHolder.mName.setText(info.getmAppName());
    }

    public static class ViewHolder {
        public ImageView mImageView;
        
        public TextView mName;
        
        public DownloadProcessView mPorcessView;
    }
}
