
package com.eostek.tv.widget;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
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

import com.eostek.tv.R;
import com.mstar.android.tv.TvCommonManager;
import com.mstar.android.tvapi.common.vo.EnumServiceType;
import com.mstar.android.tvapi.common.vo.ProgramInfo;

@SuppressLint("HandlerLeak")
public class ChannelInfoView extends RelativeLayout {
    private static final String TAG = "ChannelInfoView";

    private View view;

    private LinearLayout channelinfo_layout;

    private TextView channelIdTxt;

    private TextView sourceTypeTxt;

    private TextView channelNameTxt;

    private static final int DIMISSCHANNELINFO = 0x01;

    private static final int DELAYDIMISSTIME = 4000;

    private static boolean hasShow = false;

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
        init(context);
    }

    public ChannelInfoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public ChannelInfoView(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        view = LayoutInflater.from(context).inflate(R.layout.channel_info, this, true);
        channelinfo_layout = (LinearLayout) view.findViewById(R.id.channelinfo);
        channelIdTxt = (TextView) view.findViewById(R.id.channel_id);
        sourceTypeTxt = (TextView) view.findViewById(R.id.sourcetype);
        channelNameTxt = (TextView) view.findViewById(R.id.channel_name);
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
                getResources().getInteger(R.integer.channelinfo_layout_value));
        AnimatorSet as = new AnimatorSet();
        as.play(translationUp);
        as.start();
    }

    public void showChannelInfo(View v) {
        hasShow = true;
        v.setAlpha(1f);
        float x = getResources().getInteger(R.integer.showChannelInfo_value_x);
        float y = getResources().getInteger(R.integer.showChannelInfo_value_y);
        v.setX(getResources().getInteger(R.integer.showChannelInfo_value_setX));
        v.setY(getResources().getInteger(R.integer.showChannelInfo_value_setY));
        ViewPropertyAnimator vpa = v.animate().x(x).y(y);
        vpa.setDuration(1500);
        vpa.setInterpolator(new BounceInterpolator());
    }

    public boolean isShow() {
        return hasShow;
    }

    private int getSoundFormat() {
        int strId = R.string.mono;
        switch (TvCommonManager.getInstance().getATVMtsMode()) {
            case TvCommonManager.ATV_AUDIOMODE_MONO:
            case TvCommonManager.ATV_AUDIOMODE_NICAM_MONO:
            case TvCommonManager.ATV_AUDIOMODE_INVALID:
            case TvCommonManager.ATV_AUDIOMODE_FORCED_MONO:
            case TvCommonManager.ATV_AUDIOMODE_HIDEV_MONO:
                strId = R.string.mono;
                break;
            case TvCommonManager.ATV_AUDIOMODE_DUAL_A:
            case TvCommonManager.ATV_AUDIOMODE_DUAL_AB:
            case TvCommonManager.ATV_AUDIOMODE_DUAL_B:
            case TvCommonManager.ATV_AUDIOMODE_K_STEREO:
            case TvCommonManager.ATV_AUDIOMODE_G_STEREO:
            case TvCommonManager.ATV_AUDIOMODE_LEFT_LEFT:
            case TvCommonManager.ATV_AUDIOMODE_LEFT_RIGHT:
            case TvCommonManager.ATV_AUDIOMODE_NICAM_DUAL_A:
            case TvCommonManager.ATV_AUDIOMODE_NICAM_DUAL_AB:
            case TvCommonManager.ATV_AUDIOMODE_NICAM_DUAL_B:
            case TvCommonManager.ATV_AUDIOMODE_NICAM_STEREO:
            case TvCommonManager.ATV_AUDIOMODE_RIGHT_RIGHT:
                strId = R.string.stereo;
                break;
            case TvCommonManager.ATV_AUDIOMODE_MONO_SAP:
            case TvCommonManager.ATV_AUDIOMODE_STEREO_SAP:
                strId = R.string.sap;
                break;
            default:
                strId = R.string.mono;
        }
        return strId;
    }
}
