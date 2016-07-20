
package com.eostek.tv.widget;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
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
import com.mstar.android.tv.TvChannelManager;
import com.mstar.android.tv.TvCommonManager;
import com.mstar.android.tvapi.dtv.vo.DtvAudioInfo;

/**
 * To show MTS/Audio Language information. And change MTS/Audio Language.
 * ATV(Mono/Sap/Stereo). DTV(main/sub).
 * 
 * @projectName： EosTvPlayer
 * @moduleName： MTSView.java
 * @author jachensy.chen
 * @version 1.0.0
 * @time 2014-2-17
 * @Copyright © 2013 EOSTEK, Inc.
 */
public class MTSView extends RelativeLayout {
    private static final String TAG = "MTSView";

    private View view;

    private LinearLayout Mtslayout;

    private TextView mMtsTxt;

    private static final int DIMISSCHANNELINFO = 0x01;

    private static final int DELAYDIMISSTIME = 4000;

    private static boolean hasShow = false;

    private int mCurSource = TvCommonManager.INPUT_SOURCE_NONE;

    private int mCurDTVAudioIndex = 0;

    private DtvAudioInfo mAudioInfo;

    private int mAtvStrId = 0;

    @SuppressLint("HandlerLeak")
    private Handler mRefreshHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case DIMISSCHANNELINFO:
                    dismissMTSInfo();
                    break;
                default:
                    break;
            }
        }
    };

    public MTSView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init(context);
    }

    public MTSView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init(context);
    }

    public MTSView(Context context) {
        super(context);
        init(context);
    }

    private void init(Context context) {
        view = LayoutInflater.from(context).inflate(R.layout.audio_anguage, this, true);
        Mtslayout = (LinearLayout) view.findViewById(R.id.mtstype);
        mMtsTxt = (TextView) view.findViewById(R.id.mtsType_txt);
    }

    public void getMtsInfo(int source) {
        this.mCurSource = source;
        if (mCurSource == TvCommonManager.INPUT_SOURCE_DTV) {
            mAudioInfo = TvChannelManager.getInstance().getAudioInfo();
            mCurDTVAudioIndex = mAudioInfo.currentAudioIndex;
            if (mCurDTVAudioIndex < 0 || mCurDTVAudioIndex >= mAudioInfo.audioLangNum) {
                mCurDTVAudioIndex = 0;
            }
        } else if (mCurSource == TvCommonManager.INPUT_SOURCE_ATV) {
            mAtvStrId = getSoundFormat();
        }
        showMtsInfo();
    }

    private void showMtsInfo() {
        Log.e(TAG, "Show MTS information.");
        if (!hasShow) {
            Mtslayout.setBackgroundResource(R.drawable.setting_bg);
            showMtsUI(Mtslayout);
        }
        if (mCurSource == TvCommonManager.INPUT_SOURCE_DTV) {
            if (mCurDTVAudioIndex == 0) {
                // show main
                mMtsTxt.setText(R.string.audiolanguage_dtv_main);
            } else if (mCurDTVAudioIndex == 1) {
                // show sub
                mMtsTxt.setText(R.string.audiolanguage_dtv_sub);
            }
        } else if (mCurSource == TvCommonManager.INPUT_SOURCE_ATV) {
            mMtsTxt.setText(mAtvStrId);
        }
        mRefreshHandler.removeMessages(DIMISSCHANNELINFO);
        mRefreshHandler.sendEmptyMessageDelayed(DIMISSCHANNELINFO, DELAYDIMISSTIME);
    }

    public void changeMtsInfo(int source) {
        Log.e(TAG, "Change MTS information.");
        this.mCurSource = source;
        if (mCurSource == TvCommonManager.INPUT_SOURCE_DTV) {
            mAudioInfo = TvChannelManager.getInstance().getAudioInfo();
            mCurDTVAudioIndex = mAudioInfo.currentAudioIndex;
            if (mCurDTVAudioIndex < 0 || mCurDTVAudioIndex >= mAudioInfo.audioLangNum) {
                mCurDTVAudioIndex = 0;
            }
            if (mCurDTVAudioIndex == 0 && mCurDTVAudioIndex < mAudioInfo.audioLangNum - 1) {
                mCurDTVAudioIndex = 1;
            } else if (mCurDTVAudioIndex == 1) {
                mCurDTVAudioIndex = 0;
            }
            TvChannelManager.getInstance().switchAudioTrack(mCurDTVAudioIndex);
        } else if (mCurSource == TvCommonManager.INPUT_SOURCE_ATV) {
            TvCommonManager.getInstance().setToNextATVMtsMode();
            mAtvStrId = getSoundFormat();
        }
        showMtsInfo();
        mRefreshHandler.removeMessages(DIMISSCHANNELINFO);
        mRefreshHandler.sendEmptyMessageDelayed(DIMISSCHANNELINFO, DELAYDIMISSTIME);
    }

    public void dismissMTSInfo() {
        Log.e(TAG, "Dismiss MTS information.");
        hasShow = false;
        ObjectAnimator translationUp = ObjectAnimator.ofFloat(Mtslayout, "Y", getResources()
                .getInteger(R.integer.mtslayout_value));
        AnimatorSet as = new AnimatorSet();
        as.play(translationUp);
        as.start();
    }

    public void showMtsUI(View v) {
        hasShow = true;
        v.setAlpha(1f);
        float y = getResources().getInteger(R.integer.showmts_ui_y);
        v.setY(getResources().getInteger(R.integer.showmts_ui_setY));
        ViewPropertyAnimator vpa = v.animate().y(y);
        vpa.setDuration(1500);
        vpa.setInterpolator(new BounceInterpolator());
    }

    public boolean isShow() {
        return hasShow;
    }

    private int getSoundFormat() {
        int strId = R.string.mono;
        int type = TvCommonManager.getInstance().getATVMtsMode();
        Log.e(TAG, "get Sound Format atv, " + type);
        switch (type) {
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
