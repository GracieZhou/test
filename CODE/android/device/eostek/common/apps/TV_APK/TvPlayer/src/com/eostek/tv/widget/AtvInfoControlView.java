
package com.eostek.tv.widget;

import java.util.List;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.eostek.tv.R;
import com.eostek.tv.advertisement.AdInfo;
import com.eostek.tv.utils.TvDBManager;
import com.eostek.tv.utils.UtilsTools;
import com.mstar.android.tv.TvCommonManager;
import com.mstar.android.tvapi.common.vo.ProgramInfo;

public class AtvInfoControlView extends InfoControlView {

    private TextView mChannelId, mCurTime, mChannelName;

    public AtvInfoControlView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public AtvInfoControlView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public AtvInfoControlView(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        View osdRoot = LayoutInflater.from(context).inflate(R.layout.bottom_info_atv, this,
                true);
        View adRoot = LayoutInflater.from(context).inflate(R.layout.ad_info, this, true);
        mOSDLayout = osdRoot.findViewById(R.id.channelinfo_atv_ll);
        mChannelId = (TextView) osdRoot.findViewById(R.id.channel_num_atv);
        mCurTime = (TextView) osdRoot.findViewById(R.id.channel_time_atv);
        mChannelName = (TextView) osdRoot.findViewById(R.id.channel_name_atv);
        mAdView = (ImageView) adRoot.findViewById(R.id.ad_info_iv);
    }

    public void showAtvChannelInfo(ProgramInfo info) {
        mOSDLayout.setVisibility(View.VISIBLE);
        mOSDLayout.setBackgroundResource(R.drawable.setting_bg);
        mRefreshHandler.removeMessages(DISMISS_INFO);
        mRefreshHandler.sendEmptyMessageDelayed(DISMISS_INFO, DELAY_DIMISS_INFO_TIME);
        initAtvData(info);
        if (!hasShow) {
            show();
        }
    }

    public void showAtvAdInfo(ProgramInfo info) {
        mOSDLayout.setVisibility(View.INVISIBLE);
        mRefreshHandler.removeMessages(DISMISS_INFO);
        mRefreshHandler.sendEmptyMessageDelayed(DISMISS_INFO, DELAY_DIMISS_INFO_TIME);
        initAtvAdData(info);
        if (!hasShow) {
            show();
        }
    }

    /**
     * refresh the info dialog text
     * 
     * @param channelInfo
     */
    private void initAtvData(ProgramInfo info) {
        if (info != null) {
            if (info.number > 0 && info.number < 10) {
                mChannelId.setText("00" + info.number);
            } else if (info.number > 9 && info.number < 100) {
                mChannelId.setText("0" + info.number);
            } else {
                mChannelId.setText("" + info.number);
            }
            mChannelName.setText(info.serviceName);
        }
        mCurTime.setText(UtilsTools.formatDate(System.currentTimeMillis(), "HH:mm"));
        mAdView.setImageBitmap(null);
        List<AdInfo> adinfos = TvDBManager.getInstance(mContext).getCurrentAdInfo(
                TvCommonManager.INPUT_SOURCE_ATV, info);
        if (null != adinfos && adinfos.size() >= 1) {
            handleImage(adinfos);
        }
    }

    private void initAtvAdData(ProgramInfo info) {
        mAdView.setImageBitmap(null);
        List<AdInfo> adinfos = TvDBManager.getInstance(mContext).getCurrentAdInfo(
                TvCommonManager.INPUT_SOURCE_ATV, info);
        if (null != adinfos && adinfos.size() >= 1) {
            handleImage(adinfos);
        }
    }
}
