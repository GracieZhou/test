
package com.eostek.tv.epg;

import android.app.Activity;
import android.view.View;
import android.widget.ListView;

import com.eostek.tv.R;

public class ScheduleListHolder {
    private Activity mContext;

    private ListView mScheduleListView = null;

    private View mChannelselector;

    public ScheduleListHolder(Activity mContext) {
        this.mContext = mContext;
        initViews();
    }

    private void initViews() {
        mScheduleListView = (ListView) mContext.findViewById(R.id.schedule_listview);
        mChannelselector = mContext.findViewById(R.id.selector);
    }

    public ListView getScheduleListView() {
        return mScheduleListView;
    }

    public View getChannelselector() {
        return mChannelselector;
    }

}
