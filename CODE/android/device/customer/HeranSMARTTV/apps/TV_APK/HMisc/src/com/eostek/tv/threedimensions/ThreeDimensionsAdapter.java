
package com.eostek.tv.threedimensions;

import android.app.Activity;
import android.graphics.Color;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.TextView;

public class ThreeDimensionsAdapter extends BaseAdapter {

    private String[] names;

    private String[] values;

    private Activity activity;
    
    private boolean clickable[];

    public ThreeDimensionsAdapter(String[] names, String[] values, boolean clickable[],Activity activity) {
        super();
        this.names = names;
        this.values = values;
        this.activity = activity;
        this.clickable = clickable;
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return names.length;
    }

    @Override
    public Object getItem(int arg0) {
        // TODO Auto-generated method stub
        return names[arg0];
    }

    @Override
    public long getItemId(int arg0) {
        // TODO Auto-generated method stub
        return arg0;
    }

    @Override
    public View getView(int position, View view, ViewGroup arg2) {
        LayoutInflater factory = LayoutInflater.from(activity);
        if (view == null) {
            view = (View) factory.inflate(R.layout.menu_item, null);
        }
        TextView text = (TextView) view.findViewById(R.id.text_txt);
        TextView value = (TextView) view.findViewById(R.id.value_txt);
        if (clickable[position]) {
            text.setTextColor(Color.WHITE);
            value.setTextColor(Color.WHITE);
        } else {
            text.setTextColor(Color.GRAY);
            value.setTextColor(Color.GRAY);
        }
        text.setText(names[position]);
        value.setText(values[position]);
        return view;
    }

}
