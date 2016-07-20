package com.android.settings.datetimecity;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import com.android.settings.R;
import com.android.settings.datetimecity.TimeZoneAdapter.ViewHolder;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

public class SearchCityAdapter extends BaseAdapter {

	private LayoutInflater minflater;;
	private Context mContext;
	private List<String> list;

	public SearchCityAdapter(Context context,
			List<String> list) {
		minflater = LayoutInflater.from(context);
		this.mContext = context;
		this.list = list;
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
		ViewHolder holder = null;
		if(convertView==null){
			holder = new ViewHolder();
			convertView = minflater.inflate(R.layout.list_item_city_setting, null);
			holder.city = (TextView) convertView
					.findViewById(R.id.city_setting_list_item);
			convertView.setTag(holder);
		}else{
			holder=(ViewHolder)convertView.getTag();
		}
		holder.city.setText(list.get(position));
		return convertView;
	}


	class ViewHolder {
		public TextView city;
	}
}
