
package com.eostek.isynergy.setmeup;

import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.eostek.isynergy.setmeup.model.TitleModel;

public class TitleAdapter extends BaseAdapter {

    private LayoutInflater mInflater;

    private Context mContext;

    private List<TitleModel> mTitles;

    public TitleAdapter(StateMachineActivity context, List<TitleModel> titles) {
        this.mContext = context;
        this.mTitles = titles;
        this.mInflater = (LayoutInflater) context.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
    }

    @Override
    public int getCount() {
        return mTitles.size();
    }

    @Override
    public Object getItem(int arg0) {
        return null;
    }

    @Override
    public long getItemId(int arg0) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup arg2) {
        if (convertView != null) {
            return convertView;
        }
        TitleModel model = mTitles.get(position);
        convertView = mInflater.inflate(R.layout.gridview_item, null);

        ImageView img = (ImageView) convertView.findViewById(R.id.iv_gridview_item);
        TextView titleView = (TextView) convertView.findViewById(R.id.tv_gridview_item);

        img.setImageResource(model.getRerourceId());
        titleView.setText(model.getTitle());

        return convertView;
    }
}
