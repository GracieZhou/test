
package com.eostek.tv.epg;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.text.format.Time;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.TextView;
import android.widget.Toast;

import com.eostek.tv.R;
import com.eostek.tv.epg.adapter.ChannelAdapter;
import com.eostek.tv.epg.adapter.EpgAdapter;
import com.eostek.tv.utils.ChannelManagerExt;
import com.eostek.tv.utils.UtilsTools.EnumEventTimerType;
import com.eostek.tv.widget.AnimatedSelector;
import com.mstar.android.MKeyEvent;
import com.mstar.android.tv.TvChannelManager;
import com.mstar.android.tvapi.common.TimerManager;
import com.mstar.android.tvapi.common.TvManager;
import com.mstar.android.tvapi.common.exception.TvCommonException;
import com.mstar.android.tvapi.common.vo.EnumServiceType;
import com.mstar.android.tvapi.common.vo.EpgEventTimerInfo;
import com.mstar.android.tvapi.common.vo.ProgramInfo;
import com.mstar.android.tvapi.dtv.common.DtvManager;
import com.mstar.android.tvapi.dtv.common.EpgManager;
import com.mstar.android.tvapi.dtv.vo.DtvType.EnumEpgDescriptionType;
import com.mstar.android.tvapi.dtv.vo.EnumEpgTimerCheck;
import com.mstar.android.tvapi.dtv.vo.EpgEventInfo;

@SuppressLint({
        "HandlerLeak", "InflateParams"
})
public class EpgActivity extends Activity {
    private static final String TAG = "EpgActivity";

    // the tag for update current time.
    private static final int TOUPDATE_TIME = 0x0001;

    // the time for update current time per minute.
    private static final int DELAYUPDATE_TIME = 60 * 1000;

    // the tag for change channel.
    private static final int TOCHANGE_CHANNEL = 0x0002;

    // delay 1500 seconds to change the channel after the item is selected.
    private static final int DELAYCHANGE_TIME = 1500;

    // the tag for get epg events.
    private static final int TOGETEVENT = 0x0003;

    // to delay 300 seconds to get events.
    private static final int DELAY_GETEVENT = 300;

    // to delay 2000 seconds to get event again.
    private static final int DELAY_GETEVENT_AGAIN = 2000;

    private static final int EPG_REPEAT_ONCE = 0x81;

    private AnimatedSelector mChannelAnimatedSelector;

    private AnimatedSelector mEpgAnimatedSelector;

    private int mCurPosition = 0;

    private ChannelAdapter mChannelAdapter;

    private EpgAdapter mEpgAdapter;

    private List<ProgramInfo> mChannels = new ArrayList<ProgramInfo>();

    private List<EpgEventInfo> mEventInfos = new ArrayList<EpgEventInfo>();

    private EpgEventTimerInfo mEventTimerInfo = new EpgEventTimerInfo();

    private List<EpgTimerInfo> mEpgTimerEvents = new ArrayList<EpgTimerInfo>();

    private int mTotalEventCount = 0;

    public static long mOffsetTimeInMs = 0;

    private ChannelManagerExt mChannelManger;

    private EpgManager mEpgManager;

    private TimerManager mTimerManager;

    private EpgHolder mHolder;

    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case TOUPDATE_TIME:
                    mHolder.getTime();
                    mHandler.sendEmptyMessageDelayed(TOUPDATE_TIME, DELAYUPDATE_TIME);
                    break;
                case TOCHANGE_CHANNEL:
                    // if channel isn't changed ,return it.Else change it.
                    if (mCurPosition == msg.arg1) {
                        return;
                    }
                    mCurPosition = msg.arg1;
                    mEpgAdapter.setCurPosition(mCurPosition);
                    programSel();
                    break;
                case TOGETEVENT:
                    getEpgEvents();
                    setDescriptor(0);
                    constructEpgTimerEventList();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mHolder = new EpgHolder(this);
        Time curTime = getTime();
        getManager(curTime);
        init();
        setListener();
        mHandler.sendEmptyMessageDelayed(TOUPDATE_TIME, DELAYUPDATE_TIME);
        mHandler.sendEmptyMessageDelayed(TOGETEVENT, DELAY_GETEVENT);
        mHolder.setEpgInfoVisible();
    }

    /**
     * get the EpgManager,TimeManager and set the OffsetTimeInMs
     * 
     * @param curTime
     */
    private void getManager(Time curTime) {
        try {
            mEpgManager = DtvManager.getEpgManager();
            mTimerManager = TvManager.getInstance().getTimerManager();
            mOffsetTimeInMs = mEpgManager.getEpgEventOffsetTime(curTime, true) * 1000;
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
    }

    /**
     * get the current time
     * 
     * @return time
     */
    private Time getTime() {
        Time curTime = new Time();
        curTime.setToNow();
        curTime.toMillis(true);
        return curTime;
    }

    /**
     * set adapter and selector
     */
    private void init() {
        mChannelManger = ChannelManagerExt.getInstance();
        mChannels = mChannelManger.getChannels();
        mChannelAdapter = new ChannelAdapter(this, mChannels);
        mHolder.getChannelLst().setAdapter(mChannelAdapter);
        initCurChannelPosition();
        mHolder.getChannelLst().setSelection(mCurPosition);
        mEpgAdapter = new EpgAdapter(this, mChannels, mEventInfos, mEpgTimerEvents, mOffsetTimeInMs, mCurPosition);
        if (mEpgAdapter != null) {
            mHolder.getEpgLst().setAdapter(mEpgAdapter);
            mHolder.getEPGInfo(); // 隐藏进度条布局
        }

        mChannelAnimatedSelector = new AnimatedSelector(mHolder.getChannelselector(), mHolder.getChannelLst()
                .getSelector());
        mHolder.getChannelLst().setSelector(mChannelAnimatedSelector);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_LEFT:
                // to request the focus on channel list.
                if (mHolder.getEpgLst().hasFocus()) {
                    mHolder.getChannelLst().setSelection(mCurPosition);
                    mHolder.getChannelLst().requestFocus();
                }
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                // to request the focus on epg list if the list's size greater
                // than 0.
                if (mHolder.getChannelLst().hasFocus() && mHolder.getEpgLst().getCount() > 0) {
                    View epgselector = findViewById(R.id.epg_selector);
                    mEpgAnimatedSelector = new AnimatedSelector(epgselector, mHolder.getEpgLst().getSelector());
                    mEpgAnimatedSelector.setLeftOffset(getResources().getInteger(R.integer.epg_set_left_off));
                    mHolder.getEpgLst().setSelector(mEpgAnimatedSelector);
                    mHandler.removeMessages(TOCHANGE_CHANNEL);
                    mHolder.getEpgLst().setSelection(0);
                    mHolder.getEpgLst().requestFocus();
                }
                break;
            case KeyEvent.KEYCODE_DPAD_UP:
                // Focus cycle switch.
                if (mHolder.getChannelLst().hasFocus() && mHolder.getChannelLst().getSelectedItemPosition() == 0) {
                    mHolder.getChannelLst().setSelection(mChannels.size() - 1);
                } else if (mHolder.getEpgLst().hasFocus() && mHolder.getEpgLst().getSelectedItemPosition() == 0) {
                    mHolder.getEpgLst().setSelection(mEventInfos.size() - 1);
                }
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                // Focus cycle switch.
                if (mHolder.getChannelLst().hasFocus()
                        && mHolder.getChannelLst().getSelectedItemPosition() == mChannels.size() - 1) {
                    mHolder.getChannelLst().setSelection(0);
                } else if (mHolder.getEpgLst().hasFocus()
                        && mHolder.getEpgLst().getSelectedItemPosition() == mEventInfos.size() - 1) {
                    mHolder.getEpgLst().setSelection(0);
                }
                break;
            case MKeyEvent.KEYCODE_EPG:
            case KeyEvent.KEYCODE_BACK:
                // to remove all the handler message.
                mHandler.removeMessages(TOCHANGE_CHANNEL);
                mHandler.removeMessages(TOGETEVENT);
                mHandler.removeMessages(TOUPDATE_TIME);
                finish();
                break;
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    protected void onStop() {
        super.onStop();
        mHandler.removeMessages(TOCHANGE_CHANNEL);
        mHandler.removeMessages(TOGETEVENT);
        mHandler.removeMessages(TOUPDATE_TIME);
        finish();
    }

    /**
     * get the all EPG events.
     */
    private synchronized void getEpgEvents() {
        constructEventInfoList();
        if (mEventInfos.size() != 0) {
            mEpgAdapter.notifyDataSetChanged();
        }
        if (mEventInfos.size() > 0) {
            mHolder.getEPGInfo();
        } else {
            mHolder.loadingEPGInfo();
            mHandler.sendEmptyMessageDelayed(TOGETEVENT, DELAY_GETEVENT_AGAIN);
        }
    }

    /**
     * to get the position where current channel in the list.
     */
    private void initCurChannelPosition() {
        ProgramInfo info = ChannelManagerExt.getInstance().getCurInfo();
        for (int i = 0; i < mChannels.size(); i++) {
            if (mChannels.get(i).number == info.number && (mChannels.get(i).serviceType == info.serviceType)) {
                mCurPosition = i;
                break;
            }
        }
    }

    /**
     * to change the program.
     */
    private void programSel() {
        mChannelManger.programSel(mCurPosition);
        mHandler.removeMessages(TOGETEVENT);
        mEventInfos.clear();
        mEpgAdapter.clearEventInfos();
        mEpgAdapter.notifyDataSetChanged();
        setDescriptor(0);
        mHolder.EpgInfoChanged();
        mHandler.sendEmptyMessageDelayed(TOGETEVENT, DELAY_GETEVENT);
    }

    /**
     * to set descriptor information.
     * 
     * @param index
     */
    private void setDescriptor(int index) {
        if (mEventInfos.size() > index) {
            mHolder.setTitleText(mEventInfos, index);
            ProgramInfo channelInfo = ChannelManagerExt.getInstance().getCurInfo();
            Time starttime = new Time();
            starttime.set(((long) mEventInfos.get(index).startTime) * 1000 - mOffsetTimeInMs);
            String descriptionInfo = null;
            try {
                descriptionInfo = mEpgManager.getEventDescriptor(channelInfo.serviceType, channelInfo.number,
                        starttime, EnumEpgDescriptionType.E_DETAIL_DESCRIPTION);
            } catch (TvCommonException e) {
                e.printStackTrace();
            }
            if (descriptionInfo == null || TextUtils.isEmpty(descriptionInfo)) {
                mHolder.getContentTexxt().setText(R.string.nodescriptor);
            } else {
                mHolder.getContentTexxt().setText(descriptionInfo);
            }
        } else {
            mHolder.setContentNone();
        }
    }

    /**
     * add the listener.
     */
    public void setListener() {
        mHolder.getChannelLst().setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                showSelector(hasFocus, false);
            }
        });
        mHolder.getChannelLst().setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
                mCurPosition = position;
                mEpgAdapter.setCurPosition(mCurPosition);
                programSel();
            }
        });
        // 滚轮事件
        mHolder.getChannelLst().setOnGenericMotionListener(new View.OnGenericMotionListener() {
            @SuppressLint("NewApi")
            @Override
            public boolean onGenericMotion(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_SCROLL && !mHolder.getChannelLst().isInTouchMode()) {
                    float vscroll = event.getAxisValue(MotionEvent.AXIS_VSCROLL);
                    if (vscroll != 0) {
                        mChannelAnimatedSelector.hideView();
                    }
                }
                return false;
            }
        });
        mHolder.getChannelLst().setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View view, int position, long arg3) {
                mHandler.removeMessages(TOCHANGE_CHANNEL);
                Message msg = new Message();
                msg.what = TOCHANGE_CHANNEL;
                msg.arg1 = position;
                mHandler.sendMessageDelayed(msg, DELAYCHANGE_TIME);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                /* channelAnimatedSelector.hideView(); */
            }
        });
        mHolder.getEpgLst().setOnGenericMotionListener(new View.OnGenericMotionListener() {
            @SuppressLint("NewApi")
            @Override
            public boolean onGenericMotion(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_SCROLL && !mHolder.getEpgLst().isInTouchMode()) {
                    float vscroll = event.getAxisValue(MotionEvent.AXIS_VSCROLL);
                    if (vscroll != 0) {
                        mEpgAnimatedSelector.hideView();
                    }
                }
                return false;
            }
        });
        mHolder.getEpgLst().setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                showSelector(hasFocus, true);
            }
        });
        mHolder.getEpgLst().setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View view, int position, long arg3) {
                setDescriptor(position);
                ((TextView) view.findViewById(R.id.epg_event_channelname)).setEllipsize(TruncateAt.MARQUEE);
                ((TextView) view.findViewById(R.id.epg_event_channelname)).setMarqueeRepeatLimit(-1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                if (mEpgAnimatedSelector != null) {
                    mEpgAnimatedSelector.hideView();
                }
            }
        });
    }

    /**
     * to construct epg event info list.
     * 
     * @return
     */
    private boolean constructEventInfoList() {
        ProgramInfo curProgInfo = new ProgramInfo();
        ArrayList<EpgEventInfo> eventInfoListTemp = new ArrayList<EpgEventInfo>();

        curProgInfo = ChannelManagerExt.getInstance().getCurInfo();
        Time eventBaseTime = new Time();
        eventBaseTime.setToNow();
        eventBaseTime.set(eventBaseTime.toMillis(true));
        try {
            mTotalEventCount = mEpgManager.getEventCount((short) curProgInfo.serviceType, curProgInfo.number,
                    eventBaseTime);
            if (mTotalEventCount == 0) {
                return false;
            }
            eventInfoListTemp.clear();
            eventInfoListTemp = (ArrayList<EpgEventInfo>) mEpgManager.getEventInfo((short) curProgInfo.serviceType,
                    curProgInfo.number, eventBaseTime, mTotalEventCount);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        if (eventInfoListTemp != null && eventInfoListTemp.size() > mEventInfos.size()) {
            mEventInfos.clear();
            mEventInfos.addAll(eventInfoListTemp);
            mEpgAdapter.setEventInfos(mEventInfos);
        }
        return true;
    }

    /**
     * to reminder/record the epg event.
     * 
     * @param mEpgEventInfo
     * @param isReminder
     * @return
     */
    private boolean doReminderOrRecordEpg(EpgEventInfo mEpgEventInfo, boolean isReminder) {
        // current time
        ArrayList<EpgEventInfo> eventInfoList = new ArrayList<EpgEventInfo>();
        Time baseTime = getTime();
        // get event offset time
        int offsetTimeInMs = 0;
        ProgramInfo curProgInfo = null;
        try {
            offsetTimeInMs = mEpgManager.getEpgEventOffsetTime(baseTime, true);
            baseTime.set((long) (mEpgEventInfo.startTime - offsetTimeInMs) * 1000 + 1);
            // get current program info and epg event info
            curProgInfo = ChannelManagerExt.getInstance().getCurProgramInfo();

            eventInfoList = (ArrayList<EpgEventInfo>) mEpgManager.getEventInfo((short) curProgInfo.serviceType,
                    curProgInfo.number, baseTime, 1);
        } catch (TvCommonException e1) {
            e1.printStackTrace();
        }
        mEpgEventInfo = eventInfoList.get(0);
        if (isReminder) {
            mEventTimerInfo.enTimerType = (short) EnumEventTimerType.EPG_EVENT_REMIDER.ordinal();
        } else {
            mEventTimerInfo.enTimerType = (short) EnumEventTimerType.EPG_EVENT_RECORDER.ordinal();
        }
        mEventTimerInfo.enRepeatMode = EPG_REPEAT_ONCE;
        mEventTimerInfo.startTime = mEpgEventInfo.startTime - (int) (mOffsetTimeInMs / 1000);
        if (mEventTimerInfo.startTime < System.currentTimeMillis() / 1000) {
            Toast.makeText(EpgActivity.this, R.string.epgorderpast, Toast.LENGTH_SHORT).show();
            return false;
        }
        mEventTimerInfo.durationTime = mEpgEventInfo.durationTime;
        mEventTimerInfo.serviceType = curProgInfo.serviceType;
        mEventTimerInfo.serviceNumber = curProgInfo.number;
        mEventTimerInfo.eventID = mEpgEventInfo.eventId;
        EnumEpgTimerCheck status;
        try {
            status = TvManager.getInstance().getTimerManager().addEpgEvent(mEventTimerInfo);
            int strId = R.string.epgordersuccess;
            switch (status) {
                case E_SUCCESS:
                    strId = R.string.epgordersuccess;
                    break;
                case E_OVERLAY:
                    strId = R.string.epgorderoverlay;
                    break;
                case E_PAST:
                    strId = R.string.epgorderpast;
                    break;
                default:
                    strId = R.string.epgorderfail;
                    break;
            }
            Toast.makeText(EpgActivity.this, strId, Toast.LENGTH_SHORT).show();
            if (status.ordinal() == EnumEpgTimerCheck.E_SUCCESS.ordinal()) {
                return true;
            } else {
                return false;
            }
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * to construct epg timer events. and it shows in the schedule list.
     */
    @SuppressWarnings("deprecation")
    private void constructEpgTimerEventList() {
        List<EpgTimerInfo> tempEpgTimerEvents = new ArrayList<EpgTimerInfo>();
        EpgEventTimerInfo epgEventTimerInfo = new EpgEventTimerInfo();
        EpgEventInfo eventInfo = new EpgEventInfo();
        int epgTimerEventCount;
        try {
            epgTimerEventCount = mTimerManager.getEpgTimerEventCount();
            for (int i = 0; i < epgTimerEventCount; i++) {
                epgEventTimerInfo = mTimerManager.getEpgTimerEventByIndex(i);
                long evTimerStartMS = ((long) epgEventTimerInfo.startTime) * 1000;
                long evTimerDurationMS = ((long) epgEventTimerInfo.durationTime) * 1000;

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
        if (tempEpgTimerEvents != null && tempEpgTimerEvents.size() != 0) {
            mEpgTimerEvents.clear();
            mEpgTimerEvents.addAll(tempEpgTimerEvents);
            mEpgAdapter.setEpgTimerEvents(mEpgTimerEvents);
            tempEpgTimerEvents.clear();

        }
        mEpgAdapter.notifyDataSetChanged();
        mHolder.getEpgLst().invalidate();
    }

    /**
     * show the EPGSelector or ChannelSelector
     * 
     * @param bShow
     * @param isEPGSelector
     */
    private void showSelector(boolean bShow, boolean isEPGSelector) {
        if (isEPGSelector) {
            if (mEpgAnimatedSelector == null)
                return;
            if (bShow) {
                mEpgAnimatedSelector.ensureViewVisible();
            } else {
                mEpgAnimatedSelector.hideView();
            }
        } else {
            if (mChannelAnimatedSelector == null)
                return;
            if (bShow) {
                mChannelAnimatedSelector.ensureViewVisible();
            }
        }
    }
}
