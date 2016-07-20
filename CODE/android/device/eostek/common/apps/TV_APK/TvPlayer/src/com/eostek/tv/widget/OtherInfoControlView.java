
package com.eostek.tv.widget;

import java.util.List;

import android.content.Context;
import android.provider.Settings;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.eostek.tv.R;
import com.eostek.tv.advertisement.AdInfo;
import com.eostek.tv.utils.FactoryDeskImpl;
import com.eostek.tv.utils.TvDBManager;
import com.mstar.android.tv.TvChannelManager;
import com.mstar.android.tv.TvCommonManager;
import com.mstar.android.tv.TvPictureManager;
import com.mstar.android.tvapi.common.TvManager;
import com.mstar.android.tvapi.common.exception.TvCommonException;
import com.mstar.android.tvapi.common.vo.VideoInfo;

public class OtherInfoControlView extends InfoControlView {
    private static final String TAG = "OtherInfoControlView";

    private TextView mSourceName, mResolution;

    public OtherInfoControlView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public OtherInfoControlView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    private void init(Context context) {
        mContext = context;
        View osdRoot = LayoutInflater.from(context).inflate(R.layout.bottom_info_other, this,
                true);
        View adRoot = LayoutInflater.from(context).inflate(R.layout.ad_info, this, true);
        mOSDLayout = (RelativeLayout) osdRoot.findViewById(R.id.other_info_rl);
        mSourceName = (TextView) osdRoot.findViewById(R.id.source_name_ad_tv);
        mResolution = (TextView) osdRoot.findViewById(R.id.resolution_ad_tv);
        mAdView = (ImageView) adRoot.findViewById(R.id.ad_info_iv);
    }

    public void showOtherADInfo(int source, boolean isNeedResetZoom) {
        if (source == TvCommonManager.INPUT_SOURCE_NONE) {
            mOSDLayout.setVisibility(View.GONE);
            mAdView.setVisibility(View.INVISIBLE);
        } else {
            mRefreshHandler.removeMessages(DISMISS_INFO);
            mRefreshHandler.sendEmptyMessageDelayed(DISMISS_INFO, DELAY_DIMISS_INFO_TIME);
            initOtherData(source, isNeedResetZoom);
            if (!hasShow) {
                show();
            }
        }
    }

    private void initOtherData(int source, boolean isNeedResetZoom) {
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
        mSourceName.setText(sourceStr);
        mResolution.setText(getVideoInfo(source, isNeedResetZoom));
        mAdView.setImageBitmap(null);
        List<AdInfo> adinfos = TvDBManager.getInstance(mContext).getCurrentAdInfo(source, null);
        if (null != adinfos && adinfos.size() >= 1) {
            handleImage(adinfos);
        }
    }

    private String getVideoInfo(int source, boolean isNeedResetZoom) {
        VideoInfo videoInfo = TvPictureManager.getInstance().getVideoInfo();
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
                case TvCommonManager.INPUT_SOURCE_HDMI4:
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
                        FactoryDeskImpl.getInstance(mContext).setOverScan((short) 0, (short) 0,
                                (short) 0, (short) 0);
                    }
                }
            }
        }
    }
}
