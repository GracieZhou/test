
package com.eostek.tv.player.util;

import java.util.List;

import com.eostek.tv.player.R;
import com.eostek.tv.player.business.TvDBManager;
import com.eostek.tv.player.model.AdInfo;
import com.mstar.android.tv.TvCommonManager;
import com.mstar.android.tvapi.common.vo.ProgramInfo;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class AtvInfoControlView extends InfoControlView {
    private static final String TAG = "AtvChannelInfoView";

    private TextView mChannelId, mCurTime;

    public AtvInfoControlView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public AtvInfoControlView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public AtvInfoControlView(Context context, ProgramInfo info) {
        super(context);
        mContext = context;
        View osdRoot = LayoutInflater.from(context).inflate(R.layout.eos_bottom_info_atv, this, true);
        View adRoot = LayoutInflater.from(context).inflate(R.layout.eos_ad_info, this, true);
        mOSDLayout = osdRoot.findViewById(R.id.channelinfo_atv_ll);
        mChannelId = (TextView) osdRoot.findViewById(R.id.channel_num_atv);
        mCurTime = (TextView) osdRoot.findViewById(R.id.channel_time_atv);
        mAdView = (ImageView) adRoot.findViewById(R.id.ad_info_iv);

        showAtvChannelInfo(info);
    }

    public void showAtvChannelInfo(ProgramInfo info) {
        mOSDLayout.setVisibility(View.VISIBLE);
        mAdView.setVisibility(View.VISIBLE);
        mRefreshHandler.removeMessages(DISMISS_INFO);
        mRefreshHandler.sendEmptyMessageDelayed(DISMISS_INFO, DELAY_DIMISS_INFO_TIME);
        initAtvData(info);
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
            if (info.number > 0) {
                mChannelId.setText("" + info.number);
            }
        }
        mCurTime.setText(UtilsTools.formatDate(System.currentTimeMillis(), "HH:mm"));
        mAdView.setImageBitmap(null);
        List<AdInfo> adinfos = TvDBManager.getInstance(mContext).getCurrentAdInfo(TvCommonManager.INPUT_SOURCE_ATV,
                info);
        if(null != adinfos && adinfos.size() >= 1){
            handleImage(adinfos);
        }
    }
    
}
