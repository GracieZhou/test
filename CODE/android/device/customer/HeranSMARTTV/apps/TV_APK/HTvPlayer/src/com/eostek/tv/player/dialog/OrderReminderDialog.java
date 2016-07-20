
package com.eostek.tv.player.dialog;

import java.util.List;

import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Rect;
import android.os.Bundle;
import android.text.format.Time;
import android.view.View;
import android.view.View.OnFocusChangeListener;
import android.widget.TextView;

import com.eostek.tv.player.PlayerActivity;
import com.eostek.tv.player.R;
import com.eostek.tv.player.util.ChannelManagerExt;
import com.eostek.tv.player.util.FocusView;
import com.eostek.tv.player.util.UtilsTools;
import com.eostek.tv.player.util.UtilsTools.EnumEventTimerType;
import com.mstar.android.tv.TvChannelManager;
import com.mstar.android.tv.TvCommonManager;
import com.mstar.android.tvapi.common.TimerManager;
import com.mstar.android.tvapi.common.TvManager;
import com.mstar.android.tvapi.common.exception.TvCommonException;
import com.mstar.android.tvapi.common.vo.EnumProgramInfoType;
import com.mstar.android.tvapi.common.vo.EnumServiceType;
import com.mstar.android.tvapi.common.vo.EpgEventTimerInfo;
import com.mstar.android.tvapi.common.vo.ProgramInfoQueryCriteria;
import com.mstar.android.tvapi.common.vo.TvOsType.EnumInputSource;
import com.mstar.android.tvapi.dtv.vo.EpgEventInfo;

public class OrderReminderDialog extends AlertDialog {
    private int mLeftTime = 0;

    private Context mContext;

    private TimerManager mTimerManager;

    private ChannelManagerExt mChannelManager;

    private TextView mTimeTxt = null;

    private FocusView focusView;

    private final int EPGTIMER_COUNTDOWN_LEADING_TIMES = 10;

    private final int EPGTIMER_RECORDER_LEADING_TIMES = 5;

    private int mChannelNum = -1;

    protected OrderReminderDialog(Context context) {
        super(context);
    }

    public OrderReminderDialog(Context context, int leftTime) {
        super(context, R.style.dialog);
        mContext = context;
        mLeftTime = leftTime;
        mChannelManager = ChannelManagerExt.getInstance();
        mTimerManager = TvManager.getInstance().getTimerManager();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.eos_orderremind_dialog);
        TvCommonManager.getInstance().setInputSource(EnumInputSource.E_INPUT_SOURCE_DTV);
        init();
    }

    private void init() {
        TextView pgmInfo = (TextView) findViewById(R.id.pgm_info);
        TextView eventInfo = (TextView) findViewById(R.id.event_info);
        focusView = (FocusView) findViewById(R.id.focus_selector);
        int count = 0;
        try {
            count = mTimerManager.getEpgTimerEventCount();
            for (int i = 0; i < count; i++) {
                EpgEventTimerInfo info = mTimerManager.getEpgTimerEventByIndex(i);
                if (info.startTime > (int) ((System.currentTimeMillis() / 1000))
                        && info.startTime <= (int) ((System.currentTimeMillis() / 1000)
                                + EPGTIMER_COUNTDOWN_LEADING_TIMES + EPGTIMER_RECORDER_LEADING_TIMES)) {
                    ProgramInfoQueryCriteria temp = new ProgramInfoQueryCriteria();
                    temp.setServiceType(EnumServiceType.values()[info.serviceType]);
                    mChannelNum = info.serviceNumber;
                    temp.number = info.serviceNumber;
                    if (EnumEventTimerType.values()[info.enTimerType] == EnumEventTimerType.EPG_EVENT_REMIDER) {
                        ((TextView) findViewById(R.id.reminder_title))
                                .setText(R.string.order_remind);
                    } else if (EnumEventTimerType.values()[info.enTimerType] == EnumEventTimerType.EPG_EVENT_RECORDER) {
                        ((TextView) findViewById(R.id.reminder_title))
                                .setText(R.string.order_record_remind);
                    }
                    pgmInfo.setText(TvChannelManager.getInstance().getProgramInfo(temp,
                            EnumProgramInfoType.E_INFO_PROGRAM_NUMBER).serviceName
                            + " ");
                    Time startTime = new Time();
                    startTime.set((long) info.startTime * 1000);
                    List<EpgEventInfo> mEpgEventInfo = mChannelManager.getEventInfo(
                            (short) info.serviceType, info.serviceNumber, startTime, 1);
                    if (mEpgEventInfo != null && mEpgEventInfo.size() > 0) {
                        eventInfo.setText(mEpgEventInfo.get(0).name + " ");
                    }
                }
            }
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        mTimeTxt = (TextView) findViewById(R.id.time);
        mTimeTxt.setText(mLeftTime + "");
        setListener();
    }

    private void setListener() {
        findViewById(R.id.switch_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                execEpgTimerAction();
                dismiss();
            }
        });
        findViewById(R.id.cancle_btn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Time currTime = new Time();
                currTime.setToNow();
                currTime.set(currTime.toMillis(true) + (long) (mLeftTime) * 1000);
                try {
                    mTimerManager.cancelEpgTimerEvent((int) ((currTime.toMillis(true) / 1000)
                            + EPGTIMER_COUNTDOWN_LEADING_TIMES + EPGTIMER_RECORDER_LEADING_TIMES),
                            false);
                } catch (TvCommonException e) {
                    e.printStackTrace();
                }
                dismiss();
            }
        });
        FocusChangeListener focusChangeListener = new FocusChangeListener();
        findViewById(R.id.cancle_btn).setOnFocusChangeListener(focusChangeListener);
        findViewById(R.id.switch_btn).setOnFocusChangeListener(focusChangeListener);
    }

    class FocusChangeListener implements OnFocusChangeListener {

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
                Rect rect = new Rect();
                v.getGlobalVisibleRect(rect);
                focusView.startAnimation(v);
            }
        }
    }

    public void updateLeftTime(int leftTime) {
        this.mLeftTime = leftTime;
        mTimeTxt.setText(mLeftTime + "");
    }

    public void execEpgTimerAction() {
    	if (!UtilsTools.getCurrentActivity(mContext).equals("com.eostek.tv.player.PlayerActivity")) {
    		UtilsTools.startPlayerActivity(mContext);
        }
        mChannelManager.execEpgTimerAction();
        TvChannelManager.getInstance().playDtvCurrentProgram();
        mChannelManager.resetIndex(mChannelNum);
        ((PlayerActivity) mContext).channelChange();
    }
}
