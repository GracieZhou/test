
package com.eostek.tv.epg;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.text.format.Time;
import android.view.KeyEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;

import com.eostek.tv.R;
import com.eostek.tv.epg.adapter.ScheduleAdapter;
import com.eostek.tv.widget.AnimatedSelector;
import com.mstar.android.tv.TvChannelManager;
import com.mstar.android.tvapi.common.TimerManager;
import com.mstar.android.tvapi.common.TvManager;
import com.mstar.android.tvapi.common.exception.TvCommonException;
import com.mstar.android.tvapi.common.vo.EnumServiceType;
import com.mstar.android.tvapi.common.vo.EpgEventTimerInfo;
import com.mstar.android.tvapi.dtv.common.DtvManager;
import com.mstar.android.tvapi.dtv.common.EpgManager;
import com.mstar.android.tvapi.dtv.vo.EpgEventInfo;

@SuppressWarnings("deprecation")
public class ScheduleListActivity extends Activity {

    private ScheduleAdapter mSheduleadapter = null;

    private List<EpgTimerInfo> mEpgTimerEvents = new ArrayList<EpgTimerInfo>();

    private EpgManager mEpgManager;

    private TimerManager mTimerManager;

    private AnimatedSelector mAnimatedSelector;

    private ScheduleListHolder mHolder;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.schedule_list_activity);
        mHolder = new ScheduleListHolder(this);
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
                                .delEpgEvent(mHolder.getScheduleListView().getSelectedItemPosition());
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

    @SuppressWarnings("unchecked")
    private void init() {
        Intent i = getIntent();
        if (i != null) {
            Bundle b = i.getExtras();
            mEpgTimerEvents = (List<EpgTimerInfo>) b.getSerializable("epgTimerlist");
        }
        mSheduleadapter = new ScheduleAdapter(this, mEpgTimerEvents);
        mHolder.getScheduleListView().setAdapter(mSheduleadapter);
        mAnimatedSelector = new AnimatedSelector(mHolder.getChannelselector(), mHolder.getScheduleListView()
                .getSelector());
        mHolder.getScheduleListView().setSelector(mAnimatedSelector);
        mHolder.getScheduleListView().setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

            }
        });
        mHolder.getScheduleListView().setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View arg1, int position, long arg3) {

            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {

            }
        });
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
                            .delEpgEvent(mHolder.getScheduleListView().getSelectedItemPosition());
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
        List<EpgTimerInfo> tempEpgTimerEvents = new ArrayList<EpgTimerInfo>();
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
                long evTimerDurationMS = ((long) epgEventTimerInfo.durationTime) * 1000 - offsetTimeInMs;
                EpgTimerInfo mEpgTimerEvent = new EpgTimerInfo();
                mEpgTimerEvent.setEventTimerType(epgEventTimerInfo.enTimerType);
                mEpgTimerEvent.setStartTime(evTimerStartMS);
                mEpgTimerEvent.setEndTime(evTimerStartMS + evTimerDurationMS);
                mEpgTimerEvent.setChannelNum(epgEventTimerInfo.serviceNumber + "");
                try {
                    eventInfo = DtvManager.getEpgManager().getEventInfoById((short) epgEventTimerInfo.serviceType,
                            epgEventTimerInfo.serviceNumber, (short) epgEventTimerInfo.eventID);
                } catch (TvCommonException e) {
                    e.printStackTrace();
                }
                if (eventInfo != null) {
                    mEpgTimerEvent.setProgramName(eventInfo.name);
                } else {
                    mEpgTimerEvent.setProgramName("Unkown");
                }

                mEpgTimerEvent.setEventId(epgEventTimerInfo.eventID);
                String serviceName = TvChannelManager.getInstance().getProgramName(epgEventTimerInfo.serviceNumber,
                        EnumServiceType.E_SERVICETYPE_DTV, 0);
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
        mHolder.getScheduleListView().invalidate();
    }
}
