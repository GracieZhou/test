
package com.eostek.tv.player.hotkey;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.Time;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.eostek.tv.player.R;
import com.eostek.tv.player.util.AnimatedSelector;
import com.eostek.tv.player.util.UtilsTools;
import com.eostek.tv.player.util.UtilsTools.EnumEventTimerType;
import com.mstar.android.tv.TvChannelManager;
import com.mstar.android.tvapi.common.TimerManager;
import com.mstar.android.tvapi.common.TvManager;
import com.mstar.android.tvapi.common.exception.TvCommonException;
import com.mstar.android.tvapi.common.vo.EnumServiceType;
import com.mstar.android.tvapi.common.vo.EpgEventTimerInfo;
import com.mstar.android.tvapi.dtv.common.DtvManager;
import com.mstar.android.tvapi.dtv.common.EpgManager;
import com.mstar.android.tvapi.dtv.vo.EpgEventInfo;

public class ScheduleListActivity extends Activity {
    private ListView scheduleListView = null;

    private ScheduleAdapter mSheduleadapter = null;

    private List<EpgTimerEvent> mEpgTimerEvents = new ArrayList<EpgTimerEvent>();

    private EpgManager mEpgManager;

    private TimerManager mTimerManager;

    private AnimatedSelector animatedSelector;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.eos_schedule_list);
        try {
            mEpgManager = DtvManager.getEpgManager();
            mTimerManager = TvManager.getInstance().getTimerManager();
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        init();
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_PROG_RED: {
                    try {
                        TvManager.getInstance().getTimerManager()
                                .delEpgEvent(scheduleListView.getSelectedItemPosition());
                        if (mEpgTimerEvents.size() == 1) {
                            finish();
                            return true;
                        }
                    } catch (TvCommonException e) {
                        e.printStackTrace();
                    }
                    constructEpgTimerEventList();
                    return true;
                }
                default:
                    break;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    private void init() {
        Intent i = getIntent();
        if (i != null) {
            Bundle b = i.getExtras();
            mEpgTimerEvents = (List<EpgTimerEvent>) b.getSerializable("epgTimerlist");
        }
        scheduleListView = (ListView) findViewById(R.id.schedule_listview);
        mSheduleadapter = new ScheduleAdapter(this, mEpgTimerEvents);
        scheduleListView.setAdapter(mSheduleadapter);

        View channelselector = findViewById(R.id.selector);
        animatedSelector = new AnimatedSelector(channelselector, scheduleListView.getSelector());
        scheduleListView.setSelector(animatedSelector);

        scheduleListView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

            }
        });
        scheduleListView.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });
    }

    public class ScheduleAdapter extends BaseAdapter {

        List<EpgTimerEvent> mData = null;

        private Context mContext;

        public ScheduleAdapter(Context context, List<EpgTimerEvent> data) {
            mContext = context;
            mData = data;
        }

        @Override
        public int getCount() {
            return mData.size();
        }

        @Override
        public Object getItem(int position) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(final int position, View convertView, ViewGroup parent) {
            if (convertView == null) {
                convertView = LayoutInflater.from(mContext).inflate(
                        R.layout.eos_schedule_list_item, null);
            }

            TextView startTimeTxt = (TextView) convertView.findViewById(R.id.schedule_time);
            TextView durationTxt = (TextView) convertView.findViewById(R.id.schedule_duration);
            TextView channelNumTxt = (TextView) convertView.findViewById(R.id.schedule_channelnum);
            TextView channelNameTxt = (TextView) convertView
                    .findViewById(R.id.schedule_channelname);
            TextView epgNameTxt = (TextView) convertView.findViewById(R.id.schedule_epgname);
            ImageView mscheduleicon = (ImageView) convertView.findViewById(R.id.schedule_type);

            startTimeTxt.setText(UtilsTools.formatDate(mData.get(position).getStartTime(),
                    "MM-dd HH:mm"));
            durationTxt.setText((mData.get(position).getEndTime() - mData.get(position)
                    .getStartTime()) / (1000 * 60) + getString(R.string.schedule_str_time_minute));
            channelNumTxt.setText(mData.get(position).getChannelNum());
            channelNameTxt.setText(mData.get(position).getServiceType());
            epgNameTxt.setText(mData.get(position).getProgramName());
            int type = mEpgTimerEvents.get(position).getEventTimerType();
            if (type == EnumEventTimerType.EPG_EVENT_REMIDER.ordinal()) {
                mscheduleicon.setImageResource(R.drawable.menu_icon_time);
            } else {
                mscheduleicon.setImageResource(R.drawable.pvr_schedule_icon);
            }
            return convertView;
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Intent intent;

        switch (keyCode) {
            case KeyEvent.KEYCODE_BACK:
                intent = new Intent(this, EpgActivity.class);
                intent.setClass(ScheduleListActivity.this, EpgActivity.class);
                startActivity(intent);
                finish();
                return true;

            case KeyEvent.KEYCODE_PROG_RED: {
                try {
                    TvManager.getInstance().getTimerManager()
                            .delEpgEvent(scheduleListView.getSelectedItemPosition());
                    if (mEpgTimerEvents.size() == 1) {
                        finish();
                        return true;
                    }
                } catch (TvCommonException e) {
                    e.printStackTrace();
                }
                constructEpgTimerEventList();
            }
                return true;
        }
        // other key work as default
        return super.onKeyDown(keyCode, event);
    }

    private void constructEpgTimerEventList() {
        List<EpgTimerEvent> tempEpgTimerEvents = new ArrayList<EpgTimerEvent>();
        EpgEventTimerInfo epgEventTimerInfo = new EpgEventTimerInfo();
        EpgEventInfo eventInfo = new EpgEventInfo();
        int epgTimerEventCount;
        Time CurTime = new Time();
        CurTime.setToNow();
        CurTime.toMillis(true);
        try {
            long offsetTimeInMs = mEpgManager.getEpgEventOffsetTime(CurTime, true) * 1000;
            epgTimerEventCount = mTimerManager.getEpgTimerEventCount();
            for (int i = 0; i < epgTimerEventCount; i++) {
                epgEventTimerInfo = mTimerManager.getEpgTimerEventByIndex(i);

                long evTimerStartMS = ((long) epgEventTimerInfo.startTime) * 1000 - offsetTimeInMs;
                long evTimerDurationMS = ((long) epgEventTimerInfo.durationTime) * 1000
                        - offsetTimeInMs;
                EpgTimerEvent mEpgTimerEvent = new EpgTimerEvent();
                mEpgTimerEvent.setEventTimerType(epgEventTimerInfo.enTimerType);
                mEpgTimerEvent.setStartTime(evTimerStartMS);
                mEpgTimerEvent.setEndTime(evTimerStartMS + evTimerDurationMS);
                mEpgTimerEvent.setChannelNum(epgEventTimerInfo.serviceNumber + "");
                try {
                    eventInfo = DtvManager.getEpgManager().getEventInfoById(
                            (short) epgEventTimerInfo.serviceType, epgEventTimerInfo.serviceNumber,
                            (short) epgEventTimerInfo.eventID);
                } catch (TvCommonException e) {
                    e.printStackTrace();
                }
                if (eventInfo != null) {
                    mEpgTimerEvent.setProgramName(eventInfo.name);
                } else {
                    mEpgTimerEvent.setProgramName("Unkown");
                }

                mEpgTimerEvent.setEventId(epgEventTimerInfo.eventID);
                String serviceName = TvChannelManager.getInstance().getProgramName(
                        epgEventTimerInfo.serviceNumber, EnumServiceType.E_SERVICETYPE_DTV, 0);
                mEpgTimerEvent.setServiceType(serviceName);
                tempEpgTimerEvents.add(mEpgTimerEvent);
            }
        } catch (TvCommonException e1) {
            e1.printStackTrace();
        }
        mEpgTimerEvents.clear();
        mEpgTimerEvents.addAll(tempEpgTimerEvents);
        tempEpgTimerEvents.clear();
        mSheduleadapter.notifyDataSetChanged();
        scheduleListView.invalidate();
    }
}
