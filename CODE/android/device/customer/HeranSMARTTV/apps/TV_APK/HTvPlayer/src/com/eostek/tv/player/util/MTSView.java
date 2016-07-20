
package com.eostek.tv.player.util;

import com.eostek.tv.player.R;
import com.mstar.android.tv.TvChannelManager;
import com.mstar.android.tv.TvCommonManager;
import com.mstar.android.tvapi.common.vo.EnumAtvAudioModeType;
import com.mstar.android.tvapi.dtv.vo.DtvAudioInfo;

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
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

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

    }

    public MTSView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MTSView(Context context) {
        super(context);
        view = LayoutInflater.from(context).inflate(R.layout.eos_audio_anguage, this, true);
        Mtslayout = (LinearLayout) view.findViewById(R.id.mtstype);
        mMtsTxt = (TextView) view.findViewById(R.id.mtsType_txt);
    }

    public MTSView(Context context, int source) {
        super(context);
        this.mCurSource = source;
        view = LayoutInflater.from(context).inflate(R.layout.eos_audio_anguage, this, true);
        Mtslayout = (LinearLayout) view.findViewById(R.id.mtstype);
        mMtsTxt = (TextView) view.findViewById(R.id.mtsType_txt);
        getMtsInfo(mCurSource);
    }

    public void getMtsInfo(int source) {
        this.mCurSource = source;
        if (mCurSource == TvCommonManager.INPUT_SOURCE_DTV) {
            mAudioInfo = TvChannelManager.getInstance().getAudioInfo();
            mCurDTVAudioIndex = mAudioInfo.currentAudioIndex;
            if (mCurDTVAudioIndex < 0 || mCurDTVAudioIndex >= mAudioInfo.audioLangNum) {
                mCurDTVAudioIndex = 0;
            }
            showMtsInfo();
        } else if (mCurSource == TvCommonManager.INPUT_SOURCE_ATV) {
            mAtvStrId = getSoundFormat();
            showMtsInfo();
        }
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
            showMtsInfo();
            TvChannelManager.getInstance().switchAudioTrack(mCurDTVAudioIndex);
        } else if (mCurSource == TvCommonManager.INPUT_SOURCE_ATV) {
            TvCommonManager.getInstance().setToNextAtvMtsMode();
            mAtvStrId = getSoundFormat();
            showMtsInfo();
        }
        mRefreshHandler.removeMessages(DIMISSCHANNELINFO);
        mRefreshHandler.sendEmptyMessageDelayed(DIMISSCHANNELINFO, DELAYDIMISSTIME);
    }

    public void dismissMTSInfo() {
        Log.e(TAG, "Dismiss MTS information.");
        hasShow = false;
        ObjectAnimator translationUp = ObjectAnimator.ofFloat(Mtslayout, "Y",
                getResources().getInteger(R.integer.mtslayout_value));
        AnimatorSet as = new AnimatorSet();
        as.play(translationUp);
        as.start();
    }

    public void showMtsUI(View v) {
        hasShow = true;
        v.setAlpha(1f);
        float x = getResources().getInteger(R.integer.showmts_ui_x);
        float y = getResources().getInteger(R.integer.showmts_ui_y);
        v.setX(getResources().getInteger(R.integer.showmts_ui_setX));
        v.setY(getResources().getInteger(R.integer.showmts_ui_setY));

        ViewPropertyAnimator vpa = v.animate().x(x).y(y);

        vpa.setDuration(1500);
        vpa.setInterpolator(new BounceInterpolator());
    }

    public boolean isShow() {
        return hasShow;
    }

    private int getSoundFormat() {
        int strId = R.string.mono;
        EnumAtvAudioModeType type = TvCommonManager.getInstance().getAtvMtsMode();
        Log.e(TAG, "get Sound Format atv, " + type);
        switch (type) {
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
