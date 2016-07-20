
package com.eostek.tv.player.util;

import com.eostek.tv.player.R;
import com.mstar.android.tv.TvChannelManager;
import com.mstar.android.tv.TvCommonManager;
import com.mstar.android.tv.TvPictureManager;
import com.mstar.android.tvapi.common.TvManager;
import com.mstar.android.tvapi.common.exception.TvCommonException;
import com.mstar.android.tvapi.common.vo.VideoInfo;
import com.mstar.android.tvapi.common.vo.VideoInfo.EnumScanType;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewPropertyAnimator;
import android.view.animation.BounceInterpolator;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class OtherSourceInfoView extends RelativeLayout {
    private static final String TAG = "OtherSourceInfoView";

    private View view;

    private RelativeLayout mSourceinfo_layout;

    private TextView mSourceNameTxt;

    private TextView mResolutionTxt;

    private static final int DIMISSCHANNELINFO = 0x01;

    private static final int DELAYDIMISSTIME = 4000;

    private static boolean hasShow = false;

    private Context mContext;

    private Handler mRefreshHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case DIMISSCHANNELINFO:
                    dismissSourceInfo();
                    break;
                default:
                    break;
            }
        }

    };

    public OtherSourceInfoView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

    }

    public OtherSourceInfoView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public OtherSourceInfoView(Context context) {
        super(context);
        mContext = context;
        view = LayoutInflater.from(context).inflate(R.layout.eos_other_info, this, true);
        mSourceinfo_layout = (RelativeLayout) view.findViewById(R.id.sourceinfo);
        mSourceNameTxt = (TextView) view.findViewById(R.id.sourcename);
        mResolutionTxt = (TextView) view.findViewById(R.id.resolution);
    }

    public OtherSourceInfoView(Context context, int source) {
        super(context);
        mContext = context;
        view = LayoutInflater.from(context).inflate(R.layout.eos_other_info, this, true);
        mSourceinfo_layout = (RelativeLayout) view.findViewById(R.id.sourceinfo);
        mSourceNameTxt = (TextView) view.findViewById(R.id.sourcename);
        mResolutionTxt = (TextView) view.findViewById(R.id.resolution);
        showOthersInfo(source);
    }

    public void showOthersInfo(int source) {
        source = TvCommonManager.getInstance().getCurrentTvInputSource();
        Log.e(TAG, "Show source information. Current source is " + source);
        if (source <0
                || source == TvCommonManager.INPUT_SOURCE_NONE
                || source == TvCommonManager.INPUT_SOURCE_STORAGE) {
            mSourceinfo_layout.setVisibility(View.GONE);
        } else {
            String sourceStr = ChannelManagerExt.getSourceName(source);
            mSourceNameTxt.setText(sourceStr);
            mResolutionTxt.setText(getVideoInfo(source));
            if (!hasShow) {
                showSourceInfo(mSourceinfo_layout);
            }
            mRefreshHandler.removeMessages(DIMISSCHANNELINFO);
            mRefreshHandler.sendEmptyMessageDelayed(DIMISSCHANNELINFO, DELAYDIMISSTIME);
        }
    }

    public void dismissSourceInfo() {
        hasShow = false;
        Log.d(TAG, "mSourceinfo_layout start dismiss");
        ObjectAnimator translationUp = ObjectAnimator.ofFloat(mSourceinfo_layout, "Y", -150);
        AnimatorSet as = new AnimatorSet();
        as.play(translationUp);
        as.start();
    }

    public void showSourceInfo(View v) {
        hasShow = true;
        v.setAlpha(1f);
        float x = 100f;
        float y = 40f;
        v.setX(100f);
        v.setY(-100f);

        ViewPropertyAnimator vpa = v.animate().x(x).y(y);

        vpa.setDuration(1500);
        vpa.setInterpolator(new BounceInterpolator());
    }

    private String getVideoInfo(int source) {
        VideoInfo videoInfo = TvPictureManager.getInstance().getVideoInfo();
        String resolutionStr = "";
        if (videoInfo.vResolution != 0) {
            Log.v(TAG, "videoInfo.vResolution = " + videoInfo.vResolution + " ; videoInfo.hResolution"
                    + videoInfo.hResolution);
            int s16FrameRateShow = (videoInfo.frameRate + 5) / 10;
            EnumScanType scanType = EnumScanType.E_PROGRESSIVE;
            try {
                scanType = videoInfo.getScanType();
            } catch (TvCommonException e) {
                e.printStackTrace();
            }
            switch (source) {
                case TvCommonManager.INPUT_SOURCE_VGA:
                    resolutionStr = videoInfo.hResolution + "X" + videoInfo.vResolution + "@" + s16FrameRateShow + "Hz";
                    break;
                case TvCommonManager.INPUT_SOURCE_HDMI:
                case TvCommonManager.INPUT_SOURCE_HDMI2:
                    if (TvManager.getInstance().getPlayerManager().isHdmiMode() == true) {
                        if (scanType == EnumScanType.E_PROGRESSIVE) {
                            resolutionStr = videoInfo.vResolution + "P";
                        } else {
                            resolutionStr = videoInfo.vResolution + "I";
                        }
                        resolutionStr += "@" + s16FrameRateShow + "Hz";
                    } else {
                        resolutionStr = videoInfo.hResolution + "X" + videoInfo.vResolution + "@" + s16FrameRateShow
                                + "Hz";
                    }
                    break;
                case TvCommonManager.INPUT_SOURCE_YPBPR:
                    if (scanType == EnumScanType.E_PROGRESSIVE) {
                        resolutionStr = videoInfo.vResolution + "P";
                    } else {
                        resolutionStr = videoInfo.vResolution + "I";
                    }
                    resolutionStr += "@" + s16FrameRateShow + "Hz";
                    break;
                default:
                    if (scanType == EnumScanType.E_PROGRESSIVE) {
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
}
