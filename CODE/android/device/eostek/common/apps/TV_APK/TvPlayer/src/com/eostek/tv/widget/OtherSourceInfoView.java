
package com.eostek.tv.widget;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.animation.BounceInterpolator;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.eostek.tv.R;
import com.eostek.tv.utils.FactoryDeskImpl;
import com.mstar.android.tv.TvChannelManager;
import com.mstar.android.tv.TvCommonManager;
import com.mstar.android.tv.TvPictureManager;
import com.mstar.android.tvapi.common.TvManager;
import com.mstar.android.tvapi.common.exception.TvCommonException;
import com.mstar.android.tvapi.common.vo.VideoInfo;

public class OtherSourceInfoView extends RelativeLayout {
    private static final String TAG = "OtherSourceInfoView";

    private View view;

    private RelativeLayout mSourceinfo_layout;

    private TextView mSourceNameTxt;

    private TextView mResolutionTxt;

    private static final int DIMISSCHANNELINFO = 0x01;

    private static final int DELAYDIMISSTIME = 4000;
    
    private static final int REFRESHINFO = 0x02;

    private static final int DELAYREFRESHINFO = 100;
    
    private static boolean hasShow = false;

    private Context mContext;

    @SuppressLint("HandlerLeak")
    private Handler mRefreshHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case DIMISSCHANNELINFO:
                    dismissSourceInfo();
                    break;
                case REFRESHINFO:
                    int source = (Integer) msg.obj;
                    String str = getVideoInfo(source, false);
                    mResolutionTxt.setText(str);
                    break;
                    
                default:
                    break;
            }
        }
    };

    public OtherSourceInfoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public OtherSourceInfoView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public OtherSourceInfoView(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        view = LayoutInflater.from(context).inflate(R.layout.other_info, this, true);
        mSourceinfo_layout = (RelativeLayout) view.findViewById(R.id.sourceinfo);
        mSourceNameTxt = (TextView) view.findViewById(R.id.sourcename);
        mResolutionTxt = (TextView) view.findViewById(R.id.resolution);
    }

    public void showOthersInfo(int source, boolean isNeedResetZoom) {
        Log.e(TAG, "Show source information. Current source is " + source);
        if (source == TvCommonManager.INPUT_SOURCE_NONE
                || source == TvCommonManager.INPUT_SOURCE_STORAGE) {
            mSourceinfo_layout.setVisibility(View.GONE);
        } else {
            String sourceStr = "";
            switch (source) {
                case TvCommonManager.INPUT_SOURCE_HDMI:
                    sourceStr = "HDMI";
                    break;
                case TvCommonManager.INPUT_SOURCE_HDMI2:
                    sourceStr = "HDMI2";
                    break;
                case TvCommonManager.INPUT_SOURCE_HDMI3:
                    sourceStr = "HDMI3";
                    break;
                case TvCommonManager.INPUT_SOURCE_CVBS:
                    sourceStr = "AV";
                    break;
                case TvCommonManager.INPUT_SOURCE_YPBPR:
                    sourceStr = "YPBPR";
                    break;
                case TvCommonManager.INPUT_SOURCE_VGA:
                    sourceStr = "VGA";
                    break;
                default:
                    break;
            }
            mSourceNameTxt.setText(sourceStr);
            Message msg = Message.obtain(mRefreshHandler, REFRESHINFO, source);
            mRefreshHandler.sendMessageDelayed(msg, DELAYREFRESHINFO);
            if (!hasShow) {
                mSourceinfo_layout.setBackgroundResource(R.drawable.setting_bg);
                showSourceInfo(mSourceinfo_layout);
            }
            mRefreshHandler.removeMessages(DIMISSCHANNELINFO);
            mRefreshHandler.sendEmptyMessageDelayed(DIMISSCHANNELINFO, DELAYDIMISSTIME);
        }
    }

    public void dismissSourceInfo() {
        hasShow = false;
        ObjectAnimator translationUp = ObjectAnimator.ofFloat(mSourceinfo_layout, "Y", -150);
        AnimatorSet as = new AnimatorSet();
        as.play(translationUp);
        as.start();
    }

    public void showSourceInfo(View v) {
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

    private String getVideoInfo(int source, boolean isNeedResetZoom) {
        VideoInfo videoInfo = TvPictureManager.getInstance().getVideoInfo();
        Log.i(TAG, "videoInfo.vResolution = " + videoInfo.vResolution);
        String resolutionStr = "";
        if (videoInfo.vResolution != 0) {
            Log.v(TAG, "videoInfo.vResolution = " + videoInfo.vResolution
                    + " ; videoInfo.hResolution" + videoInfo.hResolution);
            int s16FrameRateShow = (videoInfo.frameRate + 5) / 10;
            int scanType = VideoInfo.VIDEO_SCAN_TYPE_PROGRESSIVE;
            try {
                scanType = videoInfo.getVideoScanType();
            } catch (TvCommonException e) {
                e.printStackTrace();
            }
            switch (source) {
                case TvCommonManager.INPUT_SOURCE_VGA:
                    resolutionStr = videoInfo.hResolution + "X" + videoInfo.vResolution + "@"
                            + s16FrameRateShow + "Hz";
                    break;
                case TvCommonManager.INPUT_SOURCE_HDMI:
                case TvCommonManager.INPUT_SOURCE_HDMI2:
                    if (TvManager.getInstance().getPlayerManager().isHdmiMode() == true) {
                        if (scanType == VideoInfo.VIDEO_SCAN_TYPE_PROGRESSIVE) {
                            resolutionStr = videoInfo.vResolution + "P";
                        } else {
                            resolutionStr = videoInfo.vResolution + "I";
                        }
                        resolutionStr += "@" + s16FrameRateShow + "Hz";
                    } else {
                        resolutionStr = videoInfo.hResolution + "X" + videoInfo.vResolution + "@"
                                + s16FrameRateShow + "Hz";
                    }
                    if (isNeedResetZoom) {
                        resetZoomMode(source);
                    }
                    break;
                case TvCommonManager.INPUT_SOURCE_YPBPR:
                    if (scanType == VideoInfo.VIDEO_SCAN_TYPE_PROGRESSIVE) {
                        resolutionStr = videoInfo.vResolution + "P";
                    } else {
                        resolutionStr = videoInfo.vResolution + "I";
                    }
                    resolutionStr += "@" + s16FrameRateShow + "Hz";
                    break;
                default:
                    if (scanType == VideoInfo.VIDEO_SCAN_TYPE_PROGRESSIVE) {
                        resolutionStr = videoInfo.vResolution + "P";
                    } else {
                        resolutionStr = videoInfo.vResolution + "I";
                    }
                    break;
            }
        }
        if (!TvChannelManager.getInstance().isSignalStabled() || resolutionStr.equals("X")) {
            resolutionStr = "";
        }
        return resolutionStr;
    }

    public boolean isShow() {
        return hasShow;
    }

    private void resetZoomMode(int curSourceType) {
        if (curSourceType >= TvCommonManager.INPUT_SOURCE_HDMI
                && curSourceType < TvCommonManager.INPUT_SOURCE_HDMI_MAX) {
            // change hdmi full value in tv menu's picture setting.
            int hdmiFull = Settings.System
                    .getInt(mContext.getContentResolver(), "hdmiunderscan", 0);
            if (hdmiFull == 2 || hdmiFull == 0) {
                if (TvChannelManager.getInstance().isSignalStabled()) {
                    if (TvManager.getInstance().getPlayerManager().isHdmiMode()) {
                        if (TvPictureManager.getInstance().getVideoArcType() != TvPictureManager.VIDEO_ARC_16x9) {
                            // only when hdmiFull = 0,cut 20
                            if (hdmiFull == 0) {
                                TvPictureManager.getInstance().setVideoArcType(
                                        TvPictureManager.VIDEO_ARC_16x9);
                                FactoryDeskImpl.getInstance(mContext).setOverScan((short) 20,
                                        (short) 20, (short) 20, (short) 20);
                            }
                        }
                    } else {
                        if (TvPictureManager.getInstance().getVideoArcType() != TvPictureManager.VIDEO_ARC_DOTBYDOT) {
                            TvPictureManager.getInstance().setVideoArcType(
                                    TvPictureManager.VIDEO_ARC_DOTBYDOT);
                            FactoryDeskImpl.getInstance(mContext).setOverScan((short) 0, (short) 0,
                                    (short) 0, (short) 0);
                        }
                    }
                }
            }
        }
    }
}
