
package com.android.settings.datetimecity;

import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.android.settings.R;
/**
*TimeZoneAdapter
 *
 */
public class TimeZoneAdapter extends BaseAdapter {

    private LayoutInflater minflater;;

    private List<Timezone> list;
    private int mCurrentTimeZone;

    public TimeZoneAdapter(Context context, List<Timezone> list) {
        minflater = LayoutInflater.from(context);
        this.list = list;
    }

    @Override
    public int getCount() {
        return list.size();
    }

    @Override
    public Timezone getItem(int position) {
        return list.get(position);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }
/**
 * get view of the list.
 */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ViewHolder holder = null;
        if (convertView == null) {
            holder = new ViewHolder();
            convertView = minflater.inflate(R.layout.list_item_timezone, null);
            holder.textViewKey = (TextView) convertView.findViewById(R.id.timezone_key);
            holder.textViewValue = (TextView) convertView.findViewById(R.id.timezone_value);
            holder.mImageView = (ImageView) convertView.findViewById(R.id.timezone_image);
            convertView.setTag(holder);
        } else {
            holder = (ViewHolder) convertView.getTag();
        }
        holder.textViewKey.setText(list.get(position).getName().toString());
        holder.textViewValue.setText(list.get(position).getGmt() + "");
        TimeZone tz = Calendar.getInstance().getTimeZone();

        System.out.println("" + tz.getID());
	mCurrentTimeZone = TimeZoneLogic.getCurrentTimeZone(this, tz);
	System.out.println(mCurrentTimeZone + "");
	if (mCurrentTimeZone == position) {
            holder.mImageView.setVisibility(View.VISIBLE);
        } else {
            holder.mImageView.setVisibility(View.INVISIBLE);
        }
        return convertView;
    }

	public void setCurrentTimeZone(int defaultTimeZone) {
		mCurrentTimeZone = defaultTimeZone;
		this.notifyDataSetChanged();
	}
    
    class ViewHolder {
        public TextView textViewKey;

        public TextView textViewValue;

        public ImageView mImageView;

    }
}
