
package com.eostek.tv.player.util;

import java.util.ArrayList;
import java.util.List;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.format.Time;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.animation.BounceInterpolator;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.eostek.tv.player.R;
import com.mstar.android.tv.TvChannelManager;
import com.mstar.android.tvapi.common.vo.EnumVideoType;
import com.mstar.android.tvapi.common.vo.PresentFollowingEventInfo;
import com.mstar.android.tvapi.common.vo.ProgramInfo;
import com.mstar.android.tvapi.dtv.vo.DtvAudioInfo;
import com.mstar.android.tvapi.dtv.vo.EpgEventInfo;

public class ChannelInfoDetailsView extends RelativeLayout {
    private static final String TAG = "ChannelInfoDetailsView";

    private Context mContext;

    private View view;

    private LinearLayout channelinfoDetails_layout;

    private TextView channelId, curTime, output, videotype, channelName, curPgmName, nextTime1,
            nextName1, nextTime2, nextName2;

    private ImageView mIsfavorite;

    private DtvAudioInfo mAudioInfo;

    private int mCurAudioIndex = 0;

    private static final int DIMISSCHANNELINFODETAIL = 0x01;

    private static final int DELAYDIMISSTIME = 5000;

    private static boolean hasShow = false;

    private long mOffsetTime;

    private int mTrack;

    private EpgEventInfo mInfo;

    private String mVideoType = "";

    private List<EpgEventInfo> mInfos;

    private ChannelManagerExt mChannelManagerExt;

    private static final String format = "HH:mm";

    private Handler mRefreshHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case DIMISSCHANNELINFODETAIL:
                    dismissChannelInfo();
                    break;
                default:
                    break;
            }
        }

    };

    public ChannelInfoDetailsView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

    }

    public ChannelInfoDetailsView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ChannelInfoDetailsView(Context context) {
        super(context);
        mContext = context;
        view = LayoutInflater.from(context).inflate(R.layout.eos_channelinfo_details, this, true);
        channelinfoDetails_layout = (LinearLayout) view.findViewById(R.id.channelinfo_detail);
        channelId = (TextView) view.findViewById(R.id.channel_num);
        curTime = (TextView) view.findViewById(R.id.channel_time);
        output = (TextView) view.findViewById(R.id.output);
        videotype = (TextView) view.findViewById(R.id.videotype);
        channelName = (TextView) view.findViewById(R.id.channel_name);
        mIsfavorite = (ImageView) view.findViewById(R.id.isfavorite);
        curPgmName = (TextView) view.findViewById(R.id.cur_pgm_name);
        nextTime1 = (TextView) view.findViewById(R.id.next_pgm_time1);
        nextName1 = (TextView) view.findViewById(R.id.next_pgm_name1);
        nextTime2 = (TextView) view.findViewById(R.id.next_pgm_time2);
        nextName2 = (TextView) view.findViewById(R.id.next_pgm_name2);

        mChannelManagerExt = ChannelManagerExt.getInstance();
    }

    public ChannelInfoDetailsView(Context context, ProgramInfo info) {
        super(context);
        mContext = context;
        view = LayoutInflater.from(context).inflate(R.layout.eos_channelinfo_details, this, true);
        channelinfoDetails_layout = (LinearLayout) view.findViewById(R.id.channelinfo_detail);
        channelId = (TextView) view.findViewById(R.id.channel_num);
        curTime = (TextView) view.findViewById(R.id.channel_time);
        output = (TextView) view.findViewById(R.id.output);
        videotype = (TextView) view.findViewById(R.id.videotype);
        channelName = (TextView) view.findViewById(R.id.channel_name);
        mIsfavorite = (ImageView) view.findViewById(R.id.isfavorite);
        curPgmName = (TextView) view.findViewById(R.id.cur_pgm_name);
        nextTime1 = (TextView) view.findViewById(R.id.next_pgm_time1);
        nextName1 = (TextView) view.findViewById(R.id.next_pgm_name1);
        nextTime2 = (TextView) view.findViewById(R.id.next_pgm_time2);
        nextName2 = (TextView) view.findViewById(R.id.next_pgm_name2);
        mInfos = new ArrayList<EpgEventInfo>();

        mChannelManagerExt = ChannelManagerExt.getInstance();
        showChannelInfoDetail(info);
    }

    public void showChannelInfoDetail(ProgramInfo info) {
        Log.e(TAG, "show channel information detail.");
        mRefreshHandler.removeMessages(DIMISSCHANNELINFODETAIL);
        mRefreshHandler.sendEmptyMessageDelayed(DIMISSCHANNELINFODETAIL, DELAYDIMISSTIME);
        mTrack = mChannelManagerExt.getTrack();
        mInfo = mChannelManagerExt.getCurEpgEventInfo(info);
        if (info.favorite == 0) {
            mIsfavorite.setVisibility(View.GONE);
        } else {
            mIsfavorite.setVisibility(View.VISIBLE);
        }
        mAudioInfo = TvChannelManager.getInstance().getAudioInfo();
        mCurAudioIndex = mAudioInfo.currentAudioIndex;
        if (mCurAudioIndex < 0 || mCurAudioIndex >= mAudioInfo.audioLangNum) {
            mCurAudioIndex = 0;
        }
        PresentFollowingEventInfo presentFollowingEventInfo = null;
        if (info != null) {
            presentFollowingEventInfo = mChannelManagerExt.getPresentFollowingEventInfo(
                    info.serviceType, info.number, true);
        }
        if (presentFollowingEventInfo != null && presentFollowingEventInfo.componentInfo != null) {
            EnumVideoType type = presentFollowingEventInfo.componentInfo.getVideoType();
            switch (type) {
                case E_VIDEOTYPE_MPEG:
                    mVideoType = "MPEG";
                    break;
                case E_VIDEOTYPE_H264:
                    mVideoType = "H.264";
                    break;
                case E_VIDEOTYPE_AVS:
                    mVideoType = "AVS";
                    break;
                case E_VIDEOTYPE_VC1:
                    mVideoType = "VC1";
                    break;
                default:
                    mVideoType = "";
                    break;
            }
        }
        Time curTime = new Time();
        curTime.setToNow();
        curTime.set(curTime.toMillis(true));
        mOffsetTime = mChannelManagerExt.getEpgEventOffsetTime();
        Time time = new Time();
        time.setToNow();
        time.set(time.toMillis(true));
        mInfos = mChannelManagerExt.getEventInfo(info.serviceType, info.number, time, 3);
        initDate(info);
        if (!hasShow) {
            show();
        }
    }

    private void show() {
        hasShow = true;
        channelinfoDetails_layout.setAlpha(1f);
        float x = getResources().getInteger(R.integer.ChannelInfo_details_animator_x);
        float y = getResources().getInteger(R.integer.ChannelInfo_details_animator_y);
        channelinfoDetails_layout.setX(getResources().getInteger(R.integer.ChannelInfo_details_animator_x));
        channelinfoDetails_layout.setY(getResources().getInteger(R.integer.ChannelInfo_details_animator_height));

        ViewPropertyAnimator vpa = channelinfoDetails_layout.animate().x(x).y(y);

        vpa.setDuration(1500);
        vpa.setInterpolator(new BounceInterpolator());
    }

    public void dismissChannelInfo() {
        hasShow = false;
        ObjectAnimator translationUp = ObjectAnimator.ofFloat(channelinfoDetails_layout, "Y", getResources().getInteger(R.integer.ChannelInfo_details_animator_height));
        AnimatorSet as = new AnimatorSet();
        as.play(translationUp);
        as.start();
    }

    /**
     * refresh the info dialog text
     * 
     * @param channelInfo
     */
    private void initDate(ProgramInfo info) {
        if (info != null) {
            if (info.number > 0 && info.number < 10) {
                channelId.setText("00" + info.number);
            } else if (info.number > 9 && info.number < 100) {
                channelId.setText("0" + info.number);
            } else {
                channelId.setText("" + info.number);
            }
            channelName.setText(info.serviceName);
        }
        curTime.setText(UtilsTools.formatDate(System.currentTimeMillis(), "HH:mm"));
        if (mCurAudioIndex == 0) {
            // show main
            output.setText(R.string.audiolanguage_dtv_main);
        } else if (mCurAudioIndex == 1) {
            // show sub
            output.setText(R.string.audiolanguage_dtv_sub);
        }
        videotype.setText(mVideoType);
        if (mInfo != null) {
            curPgmName.setText(mInfo.name);
        }
        nextTime1.setText("");
        nextName1.setText("");
        nextTime2.setText("");
        nextName2.setText("");
        if (mInfos != null) {
            if (mInfos.size() > 1) {
                nextTime1.setText(formateTime((long) mInfos.get(1).startTime * 1000,
                        (long) mInfos.get(1).endTime * 1000));
                nextName1.setText(mInfos.get(1).name);
            }
            if (mInfos.size() > 2) {
                nextTime2.setText(formateTime((long) mInfos.get(2).startTime * 1000,
                        (long) mInfos.get(2).endTime * 1000));
                nextName2.setText(mInfos.get(2).name);
            }
        }
    }

    private String formateTime(long startTime, long endTime) {
        String time = UtilsTools.formatDate(startTime - mOffsetTime, format) + "-"
                + UtilsTools.formatDate(endTime - mOffsetTime, format);
        return time;
    }

    public boolean isShow() {
        return hasShow;
    }
}
