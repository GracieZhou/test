
package com.eostek.scifly.devicemanager.manage.appuninstall;

import java.util.List;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.eostek.scifly.devicemanager.R;

public class AppUninstallAdapter extends BaseAdapter {

    private LayoutInflater minflater;

    private List<AppUninstallInfo> list;

    private Context context;

    public AppUninstallAdapter(final Context context, final List<AppUninstallInfo> list) {
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
            convertView = minflater.inflate(R.layout.act_manage_uninstall_gridview_item, null);
            viewHolder.mImageView = (ImageView) convertView.findViewById(R.id.iv_uninstall);
            viewHolder.mName = (TextView) convertView.findViewById(R.id.tv_name);
            viewHolder.mSize = (TextView) convertView.findViewById(R.id.tv_size);
            viewHolder.mResourse = (TextView) convertView.findViewById(R.id.tv_resourse);
            convertView.setTag(viewHolder);
        } else {
            viewHolder = (ViewHolder) convertView.getTag();
        }
        bindView(viewHolder, list.get(position));

        return convertView;
    }

    @SuppressLint("NewApi")
    private void bindView(ViewHolder viewHolder, AppUninstallInfo bigFileInfo) {
        if (bigFileInfo == null) {
            return;
        }

        viewHolder.mImageView.setBackground(bigFileInfo.getmDrawable());
        viewHolder.mName.setText(context.getResources().getString(R.string.name) + bigFileInfo.getmName());
        viewHolder.mSize.setText(context.getResources().getString(R.string.space) + bigFileInfo.getmSize());
        viewHolder.mResourse.setText(context.getResources().getString(R.string.recently_used));
    }

    public static class ViewHolder {
        public ImageView mImageView;

        public TextView mName;

        public TextView mSize;

        public TextView mResourse;
    }
}
