
package com.eostek.isynergy.setmeup.timezone;

import java.util.ArrayList;
import java.util.List;
import java.util.TimeZone;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.eostek.isynergy.setmeup.R;
import com.eostek.isynergy.setmeup.model.TimeZoneModel;
import com.eostek.isynergy.setmeup.utils.Utils;

public class TimeZoneAdapter extends BaseAdapter {
    private static final String TAG = TimeZoneAdapter.class.getSimpleName();

    private Context mContext;

    private List<TimeZoneModel> mTimeZones = new ArrayList<TimeZoneModel>();

    private LayoutInflater mInflater;

    private String mTimezoneId = "";

    private int mPosition;

    public TimeZoneAdapter(Context context, List<TimeZoneModel> languages) {
        this.mContext = context;
        this.mTimeZones = languages;

        this.mTimezoneId = TimeZone.getDefault().getID();

        mInflater = LayoutInflater.from(context.getApplicationContext());
    }

    @Override
    public int getCount() {
        return mTimeZones.size();
    }

    @Override
    public Object getItem(int arg0) {
        return null;
    }

    @Override
    public long getItemId(int arg0) {
        return arg0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {

        ListItemView view = null;
        if (convertView == null) {
            view = new ListItemView();
            convertView = mInflater.inflate(R.layout.listview_item_timezone, null);
            view.timezoneId = (TextView) convertView.findViewById(R.id.tv_timezone_id);
            view.timezoneName = (TextView) convertView.findViewById(R.id.tv_timezone_name);
            view.pointImage = (ImageView) convertView.findViewById(R.id.iv_timezone_id);
            convertView.setTag(view);
        } else {
            view = (ListItemView) convertView.getTag();
        }

        TimeZoneModel currentItem = mTimeZones.get(position);

        view.timezoneId.setText(currentItem.getTimeZoneName());
        view.timezoneName.setText(currentItem.getGmt());
        Utils.print(TAG, "Current:" + mTimezoneId + " ,But: " + currentItem.getTimeZoneId());

        if (mTimezoneId.equals(currentItem.getTimeZoneId())) {
            mPosition = position;
            view.pointImage.setVisibility(View.VISIBLE);
        } else {
            view.pointImage.setVisibility(View.INVISIBLE);
        }

        return convertView;
    }

    public final class ListItemView {
        public ImageView pointImage;

        public TextView timezoneId;

        public TextView timezoneName;
    }

    public int getCurrentTimeZonePosition() {
        Utils.print(TAG, "timeZonePosition:" + mPosition);
        return mPosition;
    }

    public void setPosition(int posi, ListView listView) {
        if (posi == mPosition) {
            return;
        }
        mTimezoneId = TimeZone.getDefault().getID();
        int visibleFirstPosi = listView.getFirstVisiblePosition();  
        int visibleLastPosi = listView.getLastVisiblePosition();  
        if (posi >= visibleFirstPosi && posi <= visibleLastPosi) {  
            View view = listView.getChildAt(posi - visibleFirstPosi);  
            ListItemView holder = (ListItemView) view.getTag();
            holder.pointImage.setVisibility(View.VISIBLE);
        }
        if (mPosition >= visibleFirstPosi && mPosition <= visibleLastPosi) {  
            View lastView = listView.getChildAt(mPosition - visibleFirstPosi);
            ListItemView holder = (ListItemView) lastView.getTag();
            holder.pointImage.setVisibility(View.INVISIBLE);
            
        }
        mPosition = posi;
    }

}
