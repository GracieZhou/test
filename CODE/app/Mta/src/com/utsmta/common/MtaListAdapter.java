package com.utsmta.common;

import com.utsmta.app.R;

import java.util.ArrayList;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class MtaListAdapter extends BaseAdapter {
	private Context context;
	private ArrayList<FactoryItem> items;
	
	public MtaListAdapter(Context context, ArrayList<FactoryItem> items){
		this.context  = context;
		this.items = items;
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return items.size();
	}

	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return items.get(position);
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		// TODO Auto-generated method stub
		ViewHolder viewHolder = null;
		if(null == convertView){
			viewHolder = new ViewHolder();
			convertView = LayoutInflater.from(this.context).inflate(layoutResId(), null);
			
			viewHolder.indexView = (TextView) convertView.findViewById(R.id.item_index);
			viewHolder.titleView = (TextView) convertView.findViewById(R.id.item_title);
			viewHolder.statusView = (ImageView) convertView.findViewById(R.id.item_state);
			convertView.setTag(viewHolder);
		}else{
			viewHolder = (ViewHolder) convertView.getTag();
		}
		
		FactoryItem item = this.items.get(position);
		if(item.getResult()){
			viewHolder.statusView.setImageResource(R.drawable.checked);
		}else{
			viewHolder.statusView.setImageResource(R.drawable.alert);
		}
		
		viewHolder.indexView.setText(String.valueOf(item.getIndex()));
		viewHolder.titleView.setText(item.getProperty("display_name"));

				
		return convertView;
	}

	protected int layoutResId(){
		return R.layout.list_item;
	}
	
	private class ViewHolder{
		public TextView  indexView;
		public TextView  titleView;
		public ImageView statusView;
	}
}
