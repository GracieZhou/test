
package com.eostek.tv.player.util;

import java.util.List;

import com.eostek.tv.player.R;
import com.eostek.tv.player.business.TvDBManager;
import com.eostek.tv.player.model.AdInfo;
import com.mstar.android.tv.TvChannelManager;
import com.mstar.android.tv.TvCommonManager;
import com.mstar.android.tv.TvPictureManager;
import com.mstar.android.tvapi.common.TvManager;
import com.mstar.android.tvapi.common.exception.TvCommonException;
import com.mstar.android.tvapi.common.vo.VideoInfo;
import com.mstar.android.tvapi.common.vo.VideoInfo.EnumScanType;

import android.content.Context;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

public class OtherInfoControlView extends InfoControlView {
    private static final String TAG = "OtherInfoControlView";

    private TextView mSourceName, mResolution, m4K2KName;

    public OtherInfoControlView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

    }

    public OtherInfoControlView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public OtherInfoControlView(Context context, int source) {
        super(context);
        mContext = context;
        View osdRoot = LayoutInflater.from(context).inflate(R.layout.eos_bottom_info_other, this, true);
        View adRoot = LayoutInflater.from(context).inflate(R.layout.eos_ad_info, this, true);
        mOSDLayout = osdRoot.findViewById(R.id.other_info_rl);
        mSourceName = (TextView) osdRoot.findViewById(R.id.source_name_ad_tv);
        mResolution = (TextView) osdRoot.findViewById(R.id.resolution_ad_tv);
        m4K2KName = (TextView) osdRoot.findViewById(R.id.resolution_ad_tv2);
        mAdView = (ImageView) adRoot.findViewById(R.id.ad_info_iv);
        showOtherADInfo(source);
    }

    public void showOtherADInfo(int source) {
        if (source == TvCommonManager.INPUT_SOURCE_NONE) {
            mOSDLayout.setVisibility(View.GONE);
            mAdView.setVisibility(View.INVISIBLE);
        } else {
            mRefreshHandler.removeMessages(DISMISS_INFO);
            mRefreshHandler.sendEmptyMessageDelayed(DISMISS_INFO, DELAY_DIMISS_INFO_TIME);
            initOtherData(source);
            if (!hasShow) {
                show();
            }
        }
    }

    private void initOtherData(int source) {
        String sourceStr = ChannelManagerExt.getSourceName(source);

        mSourceName.setText(sourceStr);
        mResolution.setText(getVideoInfo(source));
        m4K2KName.setText(getVideo4K2KInfo(source));
        mAdView.setImageBitmap(null);
        List<AdInfo> adinfos = TvDBManager.getInstance(mContext).getCurrentAdInfo(source, null);
        if (null != adinfos && adinfos.size() >= 1) {
            handleImage(adinfos);
        }
    }

    private String getVideo4K2KInfo(int source) {
        VideoInfo videoInfo = TvPictureManager.getInstance().getVideoInfo();
        String resolutionStr = "";
        if (videoInfo.vResolution != 0) {
            Log.v(TAG, "videoInfo.vResolution = " + videoInfo.vResolution + " ; videoInfo.hResolution"
                    + videoInfo.hResolution);
            switch (source) {
                case TvCommonManager.INPUT_SOURCE_HDMI:
                case TvCommonManager.INPUT_SOURCE_HDMI2:
                case TvCommonManager.INPUT_SOURCE_HDMI3:
                case TvCommonManager.INPUT_SOURCE_HDMI4:
                    resolutionStr = changeInfo4K2K(videoInfo.vResolution);
                    break;
                default:
                    resolutionStr = "";
                    break;
            }
        }
        if (!TvChannelManager.getInstance().isSignalStabled() || resolutionStr.equals("X")) {
            resolutionStr = "";
        }
        return resolutionStr;
    }

    private String changeInfo4K2K(int vResolution) {
        String reString = "";
        switch (vResolution) {
            case 2160:
                reString = "UHD(4K)";
                break;
            case 1080:
                reString = "FHD";
                break;
            case 720:
                reString = "HD";
                break;
            case 480:
                reString = "SD";
                break;
            default:
                break;
        }
        return reString;
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
                case TvCommonManager.INPUT_SOURCE_HDMI3:
                case TvCommonManager.INPUT_SOURCE_HDMI4:
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


}
