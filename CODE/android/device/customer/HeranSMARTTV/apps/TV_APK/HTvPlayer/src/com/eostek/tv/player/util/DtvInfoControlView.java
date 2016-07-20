
package com.eostek.tv.player.util;

import java.util.ArrayList;
import java.util.List;

import com.eostek.tv.player.R;
import com.eostek.tv.player.business.TvDBManager;
import com.eostek.tv.player.model.AdInfo;
import com.mstar.android.tv.TvCommonManager;
import com.mstar.android.tvapi.common.exception.TvCommonException;
import com.mstar.android.tvapi.common.vo.EnumVideoType;
import com.mstar.android.tvapi.common.vo.PresentFollowingEventInfo;
import com.mstar.android.tvapi.common.vo.ProgramInfo;
import com.mstar.android.tvapi.dtv.common.DtvManager;
import com.mstar.android.tvapi.dtv.vo.DtvAudioInfo;
import com.mstar.android.tvapi.dtv.vo.EpgEventInfo;

import android.content.Context;
import android.text.format.Time;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class DtvInfoControlView extends InfoControlView{
    private static final String TAG = "ChannelInfoDetailsView";

    private TextView mChannelId, mCurTime, mOutput, mVideotype, mChannelName, mCurPgmName, mNextTime1, mNextName1, mNextTime2,
            mNextName2;

    private ImageView mIsfavorite;

    private DtvAudioInfo mAudioInfo;

    private int mCurAudioIndex = 0;

    private long mOffsetTime;

    private int mTrack;

    private EpgEventInfo mInfo;

    private String mVideoType = "";

    private List<EpgEventInfo> mInfos;

    private ChannelManagerExt mChannelManagerExt;

    private static final String format = "HH:mm";

    public DtvInfoControlView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

    }

    public DtvInfoControlView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }
    
    public DtvInfoControlView(Context context, ProgramInfo info) {
        super(context);
        mContext = context;
        View osdRoot = LayoutInflater.from(context).inflate(R.layout.eos_bottom_info_dtv, this, true);
        View adRoot = LayoutInflater.from(context).inflate(R.layout.eos_ad_info, this, true);
        mOSDLayout = osdRoot.findViewById(R.id.channelinfo_dtv_ll);
        mChannelId = (TextView) osdRoot.findViewById(R.id.channel_num);
        mCurTime = (TextView) osdRoot.findViewById(R.id.channel_time);
        mOutput = (TextView) osdRoot.findViewById(R.id.output);
        mVideotype = (TextView) osdRoot.findViewById(R.id.videotype);
        mChannelName = (TextView) osdRoot.findViewById(R.id.channel_name);
        mIsfavorite = (ImageView) osdRoot.findViewById(R.id.isfavorite);
        mCurPgmName = (TextView) osdRoot.findViewById(R.id.cur_pgm_name);
        mNextTime1 = (TextView) osdRoot.findViewById(R.id.next_pgm_time1);
        mNextName1 = (TextView) osdRoot.findViewById(R.id.next_pgm_name1);
        mNextTime2 = (TextView) osdRoot.findViewById(R.id.next_pgm_time2);
        mNextName2 = (TextView) osdRoot.findViewById(R.id.next_pgm_name2);
        mAdView = (ImageView) adRoot.findViewById(R.id.ad_info_iv);
        mInfos = new ArrayList<EpgEventInfo>();
        mChannelManagerExt = ChannelManagerExt.getInstance();
        showDtvChannelInfo(info);
    }

    public void showDtvChannelInfo(ProgramInfo info) {
        mOSDLayout.setVisibility(View.VISIBLE);
        mAdView.setVisibility(View.VISIBLE);
        //every changing programme action will reset channel info duration time
        mRefreshHandler.removeMessages(DISMISS_INFO);
        mRefreshHandler.sendEmptyMessageDelayed(DISMISS_INFO, DELAY_DIMISS_INFO_TIME);
        initDtvData(info);
        if (!hasShow) {
            show();
        }
    }
    
    public void showDtvAdInfo(ProgramInfo info) {
        mOSDLayout.setVisibility(View.INVISIBLE);
        mAdView.setVisibility(View.VISIBLE);
        //every changing programme action will reset channel info duration time
        mRefreshHandler.removeMessages(DISMISS_INFO);
        mRefreshHandler.sendEmptyMessageDelayed(DISMISS_INFO, DELAY_DIMISS_INFO_TIME);
        initDtvAdData(info);
        if (!hasShow) {
            show();
        }
    }

    private void initDtvData(ProgramInfo info) {
        mAdView.setImageBitmap(null);
        mTrack = mChannelManagerExt.getTrack();
        mInfo = mChannelManagerExt.getCurEpgEventInfo(info);
        if (info.favorite == 0) {
            mIsfavorite.setVisibility(View.GONE);
        } else {
            mIsfavorite.setVisibility(View.VISIBLE);
        }
        try {
            mAudioInfo = DtvManager.getDvbPlayerManager().getAudioInfo();
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        if (mAudioInfo != null) {
            mCurAudioIndex = mAudioInfo.currentAudioIndex;
            if (mCurAudioIndex < 0 || mCurAudioIndex >= mAudioInfo.audioLangNum) {
                mCurAudioIndex = 0;
            }
        }
        PresentFollowingEventInfo presentFollowingEventInfo = null;
        if (info != null) {
            presentFollowingEventInfo = mChannelManagerExt.getPresentFollowingEventInfo(info.serviceType, info.number,
                    true);
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
        Time currentTime = new Time();
        currentTime.setToNow();
        currentTime.set(currentTime.toMillis(true));
        mOffsetTime = mChannelManagerExt.getEpgEventOffsetTime();
        Time time = new Time();
        time.setToNow();
        time.set(time.toMillis(true));
        mInfos = mChannelManagerExt.getEventInfo(info.serviceType, info.number, time, 3);
        if (info != null) {
            if (info.number > 0) {
                mChannelId.setText("" + info.number);
            }
            mChannelName.setText(info.serviceName);
        }
        mCurTime.setText(UtilsTools.formatDate(System.currentTimeMillis(), "HH:mm"));
        if (mCurAudioIndex == 0) {
            // show main
            mOutput.setText(R.string.audiolanguage_dtv_main);
        } else if (mCurAudioIndex == 1) {
            // show sub
            mOutput.setText(R.string.audiolanguage_dtv_sub);
        }
        mVideotype.setText(mVideoType);
        mCurPgmName.setText(mChannelManagerExt.getCurProgramName(info));
        mNextTime1.setText("");
        mNextName1.setText("");
        mNextTime2.setText("");
        mNextName2.setText("");
        if (mInfos != null) {
            if (mInfos.size() > 1) {
                mNextTime1.setText(formateTime((long) mInfos.get(1).startTime * 1000,
                        (long) mInfos.get(1).endTime * 1000));
                mNextName1.setText(mInfos.get(1).name);
            }
            if (mInfos.size() > 2) {
                mNextTime2.setText(formateTime((long) mInfos.get(2).startTime * 1000,
                        (long) mInfos.get(2).endTime * 1000));
                mNextName2.setText(mInfos.get(2).name);
            }
        }
        mAdView.setImageBitmap(null);
        List<AdInfo> adinfos = TvDBManager.getInstance(mContext).getCurrentAdInfo(TvCommonManager.INPUT_SOURCE_DTV, info);
        if(null != adinfos && adinfos.size() >= 1){
            handleImage(adinfos);
        }
    }
    
    private void initDtvAdData(ProgramInfo info) {
        mAdView.setImageBitmap(null);
        List<AdInfo> adinfos = TvDBManager.getInstance(mContext).getCurrentAdInfo(TvCommonManager.INPUT_SOURCE_DTV, info);
        if(null != adinfos && adinfos.size() >= 1){
            handleImage(adinfos);
        }
    }

    private String formateTime(long startTime, long endTime) {
        String time = UtilsTools.formatDate(startTime - mOffsetTime, format) + "-"
                + UtilsTools.formatDate(endTime - mOffsetTime, format);
        return time;
    }
}
