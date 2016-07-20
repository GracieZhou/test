
package com.heran.launcher2.util;

import com.heran.launcher2.R;
import com.mstar.android.tv.TvCommonManager;
import com.mstar.android.tvapi.common.vo.EnumServiceType;
import com.mstar.android.tvapi.common.vo.ProgramInfo;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.animation.BounceInterpolator;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class ChannelInfoView extends RelativeLayout {
    private static final String TAG = "ChannelInfoView";

    private View view;

    private LinearLayout channelinfo_layout;

    private TextView channelIdTxt;

    private TextView sourceTypeTxt;

    private TextView channelNameTxt;

    private static final int DIMISSCHANNELINFO = 0x01;

    private static final int DELAYDIMISSTIME = 4000;

    private boolean hasShow = false;

    private Context mContext;

    private Handler mRefreshHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case DIMISSCHANNELINFO:
                    dismissChannelInfo();
                    break;
                default:
                    break;
            }
        }

    };

    public ChannelInfoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

    }

    public ChannelInfoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public ChannelInfoView(Context context) {
        super(context);
        this.mContext = context;
        view = LayoutInflater.from(context).inflate(R.layout.eos_channel_info, this, true);
        channelIdTxt = (TextView) view.findViewById(R.id.channel_id);
        sourceTypeTxt = (TextView) view.findViewById(R.id.sourcetype);
        channelNameTxt = (TextView) view.findViewById(R.id.channel_name);
    }

    public ChannelInfoView(Context context, ProgramInfo info) {
        super(context);
        this.mContext = context;
        view = LayoutInflater.from(context).inflate(R.layout.eos_channel_info, this, true);
        channelinfo_layout = (LinearLayout) view.findViewById(R.id.channelinfo);
        channelIdTxt = (TextView) view.findViewById(R.id.channel_id);
        sourceTypeTxt = (TextView) view.findViewById(R.id.sourcetype);
        channelNameTxt = (TextView) view.findViewById(R.id.channel_name);
        channelChange(info);
    }

    public void channelChange(ProgramInfo info) {
        Log.e(TAG, "program change.");
        if (info == null) {
            Log.e(TAG, "program information is null.");
            channelinfo_layout.setBackgroundResource(Color.TRANSPARENT);
            return;
        } else {
            Log.e(TAG, "program number is " + info.number + " , name is " + info.serviceName + ".");
            if (!hasShow) {
                channelinfo_layout.setBackgroundResource(R.drawable.setting_bg);
                showChannelInfo(channelinfo_layout);
            }
            channelIdTxt.setText(info.number + "");
            if (info.serviceType == EnumServiceType.E_SERVICETYPE_ATV.ordinal()) {
                sourceTypeTxt.setText(R.string.atv);
                channelNameTxt.setText(getSoundFormat());
            } else {
                sourceTypeTxt.setText(R.string.dtv);
                if (info.serviceName != null) {
                    channelNameTxt.setText(info.serviceName);
                }
            }
            mRefreshHandler.removeMessages(DIMISSCHANNELINFO);
            mRefreshHandler.sendEmptyMessageDelayed(DIMISSCHANNELINFO, DELAYDIMISSTIME);
        }
    }

    public void selectChannel(String channelNum) {
        Log.e(TAG, "program selected. program number is " + channelNum);
        if (!hasShow) {
            channelinfo_layout.setBackgroundResource(R.drawable.setting_bg);
            showChannelInfo(channelinfo_layout);
        }
        channelIdTxt.setText(channelNum);
        channelNameTxt.setText("");
        mRefreshHandler.removeMessages(DIMISSCHANNELINFO);
        mRefreshHandler.sendEmptyMessageDelayed(DIMISSCHANNELINFO, DELAYDIMISSTIME);
    }

    public void dismissChannelInfo() {
        hasShow = false;
        ObjectAnimator translationUp = ObjectAnimator.ofFloat(channelinfo_layout, "Y",
                mContext.getResources().getInteger(R.integer.channel_info_view_dismiss_y));
        AnimatorSet as = new AnimatorSet();
        as.play(translationUp);
        as.start();
    }

    public void showChannelInfo(View v) {
        hasShow = true;
        v.setAlpha(1f);

        float x = mContext.getResources().getInteger(R.integer.channel_info_view_to_x);
        float y = mContext.getResources().getInteger(R.integer.channel_info_view_to_y);
        v.setX(mContext.getResources().getInteger(R.integer.channel_info_view_from_x));
        v.setY(mContext.getResources().getInteger(R.integer.channel_info_view_from_y));
        ViewPropertyAnimator vpa = v.animate().x(x).y(y);

        vpa.setDuration(1500);
        vpa.setInterpolator(new BounceInterpolator());
    }

    public boolean isShow() {
        return hasShow;
    }

    private int getSoundFormat() {
        int strId = R.string.mono;
        switch (TvCommonManager.getInstance().getAtvMtsMode()) {
            case E_ATV_AUDIOMODE_MONO:
            case E_ATV_AUDIOMODE_NICAM_MONO:
            case E_ATV_AUDIOMODE_NUM:
            case E_ATV_AUDIOMODE_INVALID:
            case E_ATV_AUDIOMODE_FORCED_MONO:
            case E_ATV_AUDIOMODE_HIDEV_MONO:
                strId = R.string.mono;
                break;
            case E_ATV_AUDIOMODE_DUAL_A:
            case E_ATV_AUDIOMODE_DUAL_AB:
            case E_ATV_AUDIOMODE_DUAL_B:
            case E_ATV_AUDIOMODE_K_STEREO:
            case E_ATV_AUDIOMODE_G_STEREO:
            case E_ATV_AUDIOMODE_LEFT_LEFT:
            case E_ATV_AUDIOMODE_LEFT_RIGHT:
            case E_ATV_AUDIOMODE_NICAM_DUAL_A:
            case E_ATV_AUDIOMODE_NICAM_DUAL_AB:
            case E_ATV_AUDIOMODE_NICAM_DUAL_B:
            case E_ATV_AUDIOMODE_NICAM_STEREO:
            case E_ATV_AUDIOMODE_RIGHT_RIGHT:
                strId = R.string.stereo;
                break;
            case E_ATV_AUDIOMODE_MONO_SAP:
            case E_ATV_AUDIOMODE_STEREO_SAP:
                strId = R.string.sap;
                break;
            default:
                strId = R.string.mono;
        }
        return strId;
    }
}
