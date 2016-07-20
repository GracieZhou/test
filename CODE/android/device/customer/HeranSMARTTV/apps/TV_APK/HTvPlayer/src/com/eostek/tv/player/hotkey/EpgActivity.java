
package com.eostek.tv.player.hotkey;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.text.TextUtils.TruncateAt;
import android.text.format.Time;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.eostek.tv.player.R;
import com.eostek.tv.player.util.AnimatedSelector;
import com.eostek.tv.player.util.ChannelManagerExt;
import com.eostek.tv.player.util.UtilsTools;
import com.eostek.tv.player.util.UtilsTools.EnumEventTimerType;
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

/**
 * TO show the epg UI and implements the function. The function include channel
 * change\reminder\record.
 * 
 * @author jachensy.chen
 */
public class EpgActivity extends Activity {
    private final static String TAG = "EpgActivity";

    private Handler mHandler;

    // the tag for update current time.
    private final static int TOUPDATE_TIME = 0x0001;

    // the time for update current time per minute.
    private final static int DELAYUPDATE_TIME = 60 * 1000;

    // the tag for change channel.
    private final static int TOCHANGE_CHANNEL = 0x0002;

    // delay 1500 seconds to change the channel after the item is selected.
    private final static int DELAYCHANGE_TIME = 1500;

    // the tag for get epg events.
    private final static int TOGETEVENT = 0x0003;

    // to delay 300 seconds to get events.
    private final static int DELAY_GETEVENT = 300;

    // to delay 2000 seconds to get event again.
    private final static int DELAY_GETEVENT_AGAIN = 2000;

    private ListView channelLst;

    private ListView epgLst;

    private AnimatedSelector channelAnimatedSelector;

    private AnimatedSelector epgAnimatedSelector;

    private TextView descriptor_title_txt;

    private TextView descriptor_content_txt;

    private TextView curtime_txt;

    private TextView epg_tip_txt;

    private ProgressBar epg_tip_bar;

    private TextView epg_des_tip_txt;

    private ProgressBar epg_des_tip_bar;

    private int curPosition = 0;

    private ChannelAdapter channelAdapter;

    private EpgAdapter epgAdapter;

    private List<ProgramInfo> mChannels = new ArrayList<ProgramInfo>();

    private List<EpgEventInfo> mEventInfos = new ArrayList<EpgEventInfo>();

    private int mTotalEventCount = 0;

    private static long mOffsetTimeInMs = 0;

    private EpgEventTimerInfo eventTimerInfo = new EpgEventTimerInfo();

    private List<EpgTimerEvent> mEpgTimerEvents = new ArrayList<EpgTimerEvent>();

    private final int EPG_REPEAT_ONCE = 0x81;

    private ChannelManagerExt mChannelManger;

    private EpgManager mEpgManager;

    private TimerManager mTimerManager;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mChannelManger = ChannelManagerExt.getInstance();
        mChannels = mChannelManger.getChannels();
        // if no channels , don't show the epg ui.
        if (mChannels.size() < 1) {
            Toast.makeText(this, R.string.noprogram_tip, Toast.LENGTH_LONG).show();
            finish();
        }
        setContentView(R.layout.eos_channel_epg);
        Time curTime = new Time();
        curTime.setToNow();
        curTime.toMillis(true);
        try {
            mEpgManager = DtvManager.getEpgManager();
            mTimerManager = TvManager.getInstance().getTimerManager();
            mOffsetTimeInMs = mEpgManager.getEpgEventOffsetTime(curTime, true) * 1000;
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        mHandler = new Handler() {
            @Override
            public void handleMessage(Message msg) {
                super.handleMessage(msg);
                switch (msg.what) {
                    case TOUPDATE_TIME:
                        curtime_txt.setText(UtilsTools.formatDate(System.currentTimeMillis(), UtilsTools.timeformat));
                        mHandler.sendEmptyMessageDelayed(TOUPDATE_TIME, DELAYUPDATE_TIME);
                        break;
                    case TOCHANGE_CHANNEL:
                        // if channel isn't changed ,return it.Else change it.
                        if (curPosition == msg.arg1) {
                            return;
                        }
                        curPosition = msg.arg1;
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
        channelLst = (ListView) findViewById(R.id.channel_lst);
        epgLst = (ListView) findViewById(R.id.epg_lst);
        descriptor_title_txt = (TextView) findViewById(R.id.descriptor_title);
        descriptor_content_txt = (TextView) findViewById(R.id.descriptor_content);
        curtime_txt = (TextView) findViewById(R.id.currenttime);
        epg_tip_txt = (TextView) findViewById(R.id.epg_tip);
        epg_tip_bar = (ProgressBar) findViewById(R.id.epg_tip_progress);
        epg_des_tip_txt = (TextView) findViewById(R.id.epg_des_tip);
        epg_des_tip_bar = (ProgressBar) findViewById(R.id.epg_des_tip_progress);

        curtime_txt.setText(UtilsTools.formatDate(System.currentTimeMillis(), UtilsTools.timeformat));
        mHandler.sendEmptyMessageDelayed(TOUPDATE_TIME, DELAYUPDATE_TIME);

        channelAdapter = new ChannelAdapter(this);
        channelLst.setAdapter(channelAdapter);
        getCurChannelPosition();
        channelLst.setSelection(curPosition);
        setListener();
        epgAdapter = new EpgAdapter(this);
        epgLst.setAdapter(epgAdapter);

        View channelselector = findViewById(R.id.channel_selector);
        channelAnimatedSelector = new AnimatedSelector(channelselector, channelLst.getSelector());
        channelLst.setSelector(channelAnimatedSelector);

        mHandler.sendEmptyMessageDelayed(TOGETEVENT, DELAY_GETEVENT);
        epg_tip_txt.setVisibility(View.VISIBLE);
        epg_des_tip_txt.setVisibility(View.VISIBLE);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            switch (event.getKeyCode()) {
                case KeyEvent.KEYCODE_PROG_RED:// record;
                    return true;
                case KeyEvent.KEYCODE_PROG_GREEN:// reminder;
                    return true;
                case KeyEvent.KEYCODE_PROG_YELLOW:// schedule;
                    return true;
                default:
                    break;
            }
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_LEFT:
                // to request the focus on channel list.
                if (epgLst.hasFocus()) {
                    channelLst.setSelection(curPosition);
                    channelLst.requestFocus();
                }
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                // to request the focus on epg list if the list's size greater
                // than 0.
                if (channelLst.hasFocus() && epgLst.getCount() > 0) {
                    View epgselector = findViewById(R.id.epg_selector);
                    epgAnimatedSelector = new AnimatedSelector(epgselector, epgLst.getSelector());
                    epgAnimatedSelector.setLeftOffset(getResources().getInteger(R.integer.epg_set_left_off));
                    epgLst.setSelector(epgAnimatedSelector);
                    mHandler.removeMessages(TOCHANGE_CHANNEL);
                    epgLst.setSelection(0);
                    epgLst.requestFocus();
                }
                break;
            case KeyEvent.KEYCODE_DPAD_UP:
                // Focus cycle switch.
                if (channelLst.hasFocus() && channelLst.getSelectedItemPosition() == 0) {
                    channelLst.setSelection(mChannels.size() - 1);
                } else if (epgLst.hasFocus() && epgLst.getSelectedItemPosition() == 0) {
                    epgLst.setSelection(mEventInfos.size() - 1);
                }
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                // Focus cycle switch.
                if (channelLst.hasFocus() && channelLst.getSelectedItemPosition() == mChannels.size() - 1) {
                    channelLst.setSelection(0);
                } else if (epgLst.hasFocus() && epgLst.getSelectedItemPosition() == mEventInfos.size() - 1) {
                    epgLst.setSelection(0);
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
            // case KeyEvent.KEYCODE_PROG_RED:// record;
            // if (epgLst.hasFocus()) {
            // int selPos = epgLst.getSelectedItemPosition();
            // boolean isSuccess =
            // doReminderOrRecordEpg(mEventInfos.get(selPos), false);
            // if (isSuccess) {
            // Log.v(TAG, "epg record Success.");
            // constructEpgTimerEventList();
            // }
            // }
            // break;
            // case KeyEvent.KEYCODE_PROG_GREEN:// reminder;
            // if (epgLst.hasFocus()) {
            // int selPos = epgLst.getSelectedItemPosition();
            // boolean isSuccess =
            // doReminderOrRecordEpg(mEventInfos.get(selPos), true);
            // if (isSuccess) {
            // Log.v(TAG, "epg reminder Success.");
            // constructEpgTimerEventList();
            // }
            // }
            // break;
            // case KeyEvent.KEYCODE_PROG_YELLOW:// schedule;
            // if (mEpgTimerEvents.size() == 0) {
            // return true;
            // }
            // Intent i = new Intent();
            // i.setClass(this, ScheduleListActivity.class);
            // Bundle b = new Bundle();
            // b.putSerializable("epgTimerlist", (Serializable)
            // mEpgTimerEvents);
            // i.putExtras(b);
            // this.startActivity(i);
            // finish();
            // break;
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * the channel item adapter for channel listview.
     */
    class ChannelAdapter extends BaseAdapter {
        private Context mContext;

        public ChannelAdapter(Context context) {
            this.mContext = context;
        }

        @Override
        public int getCount() {
            return mChannels.size();
        }

        @Override
        public Object getItem(int arg0) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.eos_epg_channel_item, null);
            }
            TextView channelNoTxt = (TextView) convertView.findViewById(R.id.epg_channelno);
            channelNoTxt.setText(mChannels.get(position).number + "");
            TextView channelTypeTxt = (TextView) convertView.findViewById(R.id.epg_channeltype);
            if (EnumServiceType.E_SERVICETYPE_DTV.ordinal() == mChannels.get(position).serviceType) {
                channelTypeTxt.setText(R.string.dtv);
            } else if (EnumServiceType.E_SERVICETYPE_RADIO.ordinal() == mChannels.get(position).serviceType) {
                channelTypeTxt.setText(R.string.radio);
            } else if (EnumServiceType.E_SERVICETYPE_DATA.ordinal() == mChannels.get(position).serviceType) {
                channelTypeTxt.setText(R.string.data);
            }
            TextView channelnameTxt = (TextView) convertView.findViewById(R.id.epg_channelname);
            channelnameTxt.setText(mChannels.get(position).serviceName);
            return convertView;
        }
    }

    /**
     * the epg item for epg listview.
     */
    class EpgAdapter extends BaseAdapter {
        private Context mContext;

        public EpgAdapter(Context context) {
            this.mContext = context;
        }

        @Override
        public int getCount() {
            return mEventInfos.size();
        }

        @Override
        public Object getItem(int arg0) {
            return null;
        }

        @Override
        public long getItemId(int position) {
            return 0;
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(mContext);
            if (convertView == null) {
                convertView = inflater.inflate(R.layout.eos_epg_event_item, null);
            }
            TextView channelnoTxt = (TextView) convertView.findViewById(R.id.epg_event_starttime);
            channelnoTxt.setText(UtilsTools.formatDate(((long) mEventInfos.get(position).startTime) * 1000
                    - mOffsetTimeInMs, "MM/dd HH:mm"));
            TextView channelnameTxt = (TextView) convertView.findViewById(R.id.epg_event_channelname);
            ImageView statusiconImag = (ImageView) convertView.findViewById(R.id.epg_event_statusicon);
            statusiconImag.setImageResource(R.drawable.one_px);
            channelnameTxt.setText(mEventInfos.get(position).name);
            // adjust event has reminder/record or not, then to show the flag.
            if (mEpgTimerEvents != null && mEpgTimerEvents.size() != 0) {
                for (int i = 0; i < mEpgTimerEvents.size(); i++) {
                    if (String.valueOf(mChannels.get(curPosition).number)
                            .equals(mEpgTimerEvents.get(i).getChannelNum())
                            && mEventInfos.get(position).eventId == mEpgTimerEvents.get(i).getEventId()) {
                        if (mEpgTimerEvents.get(i).getEventTimerType() == EnumEventTimerType.EPG_EVENT_REMIDER
                                .ordinal()) {
                            statusiconImag.setImageResource(R.drawable.menu_icon_time);
                        } else {
                            statusiconImag.setImageResource(R.drawable.pvr_schedule_icon);
                        }
                    }
                }
            }
            return convertView;
        }
    }

    /**
     * get the all EPG events.
     */
    private synchronized void getEpgEvents() {
        constructEventInfoList();
        if (mEventInfos.size() != 0) {
            epgAdapter.notifyDataSetChanged();
        }
        if (mEventInfos.size() > 0) {
            epg_tip_bar.setVisibility(View.GONE);
            epg_des_tip_bar.setVisibility(View.GONE);
            epg_tip_txt.setVisibility(View.GONE);
            epg_des_tip_txt.setVisibility(View.GONE);
        } else {
            epg_tip_bar.setVisibility(View.VISIBLE);
            epg_des_tip_bar.setVisibility(View.VISIBLE);
            epg_tip_txt.setText(R.string.getepg_tip);
            epg_des_tip_txt.setText(R.string.getepgdes_tip);
            mHandler.sendEmptyMessageDelayed(TOGETEVENT, DELAY_GETEVENT_AGAIN);
        }
    }

    /**
     * to get the position where current channel in the list.
     */
    private void getCurChannelPosition() {
        ProgramInfo info = ChannelManagerExt.getInstance().getCurInfo();
        for (int i = 0; i < mChannels.size(); i++) {
            if (mChannels.get(i).number == info.number && (mChannels.get(i).serviceType == info.serviceType)) {
                curPosition = i;
                break;
            }
        }
    }

    /**
     * to change the program.
     */
    private void programSel() {
        mChannelManger.programSel(curPosition);
        mHandler.removeMessages(TOGETEVENT);
        mEventInfos.clear();
        epgAdapter.notifyDataSetChanged();
        setDescriptor(0);
        epg_tip_bar.setVisibility(View.VISIBLE);
        epg_des_tip_bar.setVisibility(View.VISIBLE);
        epg_tip_txt.setVisibility(View.VISIBLE);
        epg_des_tip_txt.setVisibility(View.VISIBLE);
        epg_tip_txt.setText(R.string.getepg_tip);
        epg_des_tip_txt.setText(R.string.getepgdes_tip);
        mHandler.sendEmptyMessageDelayed(TOGETEVENT, DELAY_GETEVENT);
    }

    /**
     * to set descriptor information.
     * 
     * @param index
     */
    private void setDescriptor(int index) {
        if (mEventInfos.size() > index) {
            descriptor_title_txt.setText(mEventInfos.get(index).name);
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
                descriptor_content_txt.setText(R.string.nodescriptor);
            } else {
                descriptor_content_txt.setText(descriptionInfo);
            }
        } else {
            descriptor_title_txt.setText("");
            descriptor_content_txt.setText("");
        }
    }

    /**
     * add the listener.
     */
    public void setListener() {
        channelLst.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                showChannelSelector(hasFocus);
            }
        });
        channelLst.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
                curPosition = position;
                programSel();
            }
        });
        channelLst.setOnGenericMotionListener(new View.OnGenericMotionListener() {
            @SuppressLint("NewApi")
            @Override
            public boolean onGenericMotion(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_SCROLL && !channelLst.isInTouchMode()) {
                    float vscroll = event.getAxisValue(MotionEvent.AXIS_VSCROLL);
                    if (vscroll != 0) {
                        channelAnimatedSelector.hideView();
                    }
                }
                return false;
            }
        });

        channelLst.setOnItemSelectedListener(new OnItemSelectedListener() {

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
                channelAnimatedSelector.hideView();
            }
        });
        epgLst.setOnGenericMotionListener(new View.OnGenericMotionListener() {
            @SuppressLint("NewApi")
            @Override
            public boolean onGenericMotion(View v, MotionEvent event) {
                if (event.getAction() == MotionEvent.ACTION_SCROLL && !epgLst.isInTouchMode()) {
                    float vscroll = event.getAxisValue(MotionEvent.AXIS_VSCROLL);
                    if (vscroll != 0) {
                        epgAnimatedSelector.hideView();
                    }
                }
                return false;
            }
        });
        epgLst.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                showEPGSelector(hasFocus);
            }
        });
        epgLst.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> arg0, View view, int position, long arg3) {
                setDescriptor(position);
                ((TextView) view.findViewById(R.id.epg_event_channelname)).setEllipsize(TruncateAt.MARQUEE);
                ((TextView) view.findViewById(R.id.epg_event_channelname)).setMarqueeRepeatLimit(-1);
            }

            @Override
            public void onNothingSelected(AdapterView<?> arg0) {
                if (epgAnimatedSelector != null) {
                    epgAnimatedSelector.hideView();
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
        Time baseTime = new Time();
        baseTime.setToNow();
        baseTime.toMillis(true);
        // get event offset time
        int offsetTimeInMs = 0;
        ProgramInfo curProgInfo = null;
        try {
            offsetTimeInMs = mEpgManager.getEpgEventOffsetTime(baseTime, true);
            baseTime.set((long) (mEpgEventInfo.startTime - offsetTimeInMs) * 1000 + 1);
            // get current program info and epg event info
            curProgInfo = ChannelManagerExt.getInstance().getCurInfo();

            eventInfoList = (ArrayList<EpgEventInfo>) mEpgManager.getEventInfo((short) curProgInfo.serviceType,
                    curProgInfo.number, baseTime, 1);
        } catch (TvCommonException e1) {
            e1.printStackTrace();
        }
        mEpgEventInfo = eventInfoList.get(0);
        if (isReminder) {
            eventTimerInfo.enTimerType = (short) EnumEventTimerType.EPG_EVENT_REMIDER.ordinal();
        } else {
            eventTimerInfo.enTimerType = (short) EnumEventTimerType.EPG_EVENT_RECORDER.ordinal();
        }
        eventTimerInfo.enRepeatMode = EPG_REPEAT_ONCE;
        eventTimerInfo.startTime = mEpgEventInfo.startTime - (int) (mOffsetTimeInMs / 1000);
        if (eventTimerInfo.startTime < System.currentTimeMillis() / 1000) {
            Toast.makeText(EpgActivity.this, R.string.epgorderpast, Toast.LENGTH_SHORT).show();
            return false;
        }
        eventTimerInfo.durationTime = mEpgEventInfo.durationTime;
        eventTimerInfo.serviceType = curProgInfo.serviceType;
        eventTimerInfo.serviceNumber = curProgInfo.number;
        eventTimerInfo.eventID = mEpgEventInfo.eventId;
        EnumEpgTimerCheck status;
        try {
            status = TvManager.getInstance().getTimerManager().addEpgEvent(eventTimerInfo);
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
    private void constructEpgTimerEventList() {
        List<EpgTimerEvent> tempEpgTimerEvents = new ArrayList<EpgTimerEvent>();
        EpgEventTimerInfo epgEventTimerInfo = new EpgEventTimerInfo();
        EpgEventInfo eventInfo = new EpgEventInfo();
        int epgTimerEventCount;
        try {
            epgTimerEventCount = mTimerManager.getEpgTimerEventCount();
            for (int i = 0; i < epgTimerEventCount; i++) {
                epgEventTimerInfo = mTimerManager.getEpgTimerEventByIndex(i);
                long evTimerStartMS = ((long) epgEventTimerInfo.startTime) * 1000;
                long evTimerDurationMS = ((long) epgEventTimerInfo.durationTime) * 1000;

                EpgTimerEvent mEpgTimerEvent = new EpgTimerEvent();
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
            tempEpgTimerEvents.clear();

        }
        epgAdapter.notifyDataSetChanged();
        epgLst.invalidate();
    }

    /**
     * show channel listview selector.
     * 
     * @param bShow
     */
    private void showChannelSelector(boolean bShow) {
        if (channelAnimatedSelector == null)
            return;
        if (bShow) {
            channelAnimatedSelector.ensureViewVisible();
        } else {
            channelAnimatedSelector.hideView();
        }
    }

    /**
     * show epg listview selector.
     * 
     * @param bShow
     */
    private void showEPGSelector(boolean bShow) {
        if (epgAnimatedSelector == null)
            return;
        if (bShow) {
            epgAnimatedSelector.ensureViewVisible();
        } else {
            epgAnimatedSelector.hideView();
        }
    }
}
