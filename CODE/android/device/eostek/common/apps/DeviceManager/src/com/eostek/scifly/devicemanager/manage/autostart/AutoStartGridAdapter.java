
package com.eostek.scifly.devicemanager.manage.autostart;

import java.util.List;

import com.eostek.scifly.devicemanager.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class AutoStartGridAdapter extends BaseAdapter {
    private LayoutInflater inflater;
    private List<AutoStartApplicantionInfo> list;
    private boolean enable;

    public AutoStartGridAdapter(final Context context, final List<AutoStartApplicantionInfo> list, boolean enable) {
        inflater = LayoutInflater.from(context);
        this.list = list;
        this.enable = enable;
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
        View view;
        if (convertView == null) {
            view = inflater.inflate(R.layout.manage_auto_start_gird_item, null);
            final ViewHolder holder = new ViewHolder(view);
            holder.appSwitch.setTag(enable);
            view.setTag(holder);
        } else {
            view = convertView;
        }
        bindView(view, list.get(position));
        return view;
    }

    private final void bindView(View view, AutoStartApplicantionInfo info) {
        final ViewHolder holder = (ViewHolder) view.getTag();
        if (info != null) {
            holder.appIcon.setImageDrawable(info.getIcon());
            holder.appName.setText(info.getLabel());
            holder.appName.setTag(info.getPkgName());
            //view.setBackground(context.getResources().getDrawable(R.drawable.bg_color));
//            view.setBackgroundColor(info.getmColor());
//            view.getBackground().setAlpha(159);
            //view.setBackgroundColor(Color.argb(255, 204, 204, 204));

        }
        if ((Boolean) holder.appSwitch.getTag()) {
        	 holder.appSwitch.setVisibility(View.GONE);
//            holder.appSwitch.setBackgroundResource(R.drawable.on);
            view.findViewById(R.id.iv_switch1).setBackgroundResource(R.drawable.pic_bg);
        } else {
        	holder.appSwitch.setVisibility(View.VISIBLE);
            holder.appSwitch.setImageResource(R.drawable.forbidden);
            view.findViewById(R.id.iv_switch1).setBackgroundResource(R.drawable.pic_bg);
        }
    }

    class ViewHolder {
        public ImageView appSwitch;
        public ImageView appIcon;
        public TextView appName;

        public ViewHolder(View view) {
            appSwitch = (ImageView) view.findViewById(R.id.iv_switch);
            appIcon = (ImageView) view.findViewById(R.id.iv_app_icon);
            appName = (TextView) view.findViewById(R.id.tv_app_name);
        }
    }

}
