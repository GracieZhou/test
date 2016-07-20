
package com.eostek.tv;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.Handler;
import android.provider.Settings;
import android.widget.LinearLayout;

import com.eostek.tv.tvlistener.AtvPlayerEventListener;
import com.eostek.tv.tvlistener.DtvPlayerEventListener;
import com.eostek.tv.tvlistener.EventListener;
import com.eostek.tv.tvlistener.TvPlayerEventListener;
import com.eostek.tv.utils.Constants;
import com.eostek.tv.widget.AtvInfoControlView;
import com.eostek.tv.widget.ChannelInfoView;
import com.eostek.tv.widget.DtvInfoControlView;
import com.eostek.tv.widget.MTSView;
import com.eostek.tv.widget.OtherInfoControlView;
import com.eostek.tv.widget.OtherSourceInfoView;
import com.eostek.tv.widget.SignalTipView;
import com.mstar.android.tv.TvChannelManager;
import com.mstar.android.tv.TvCommonManager;
import com.mstar.android.tv.widget.TvView;
import com.mstar.android.tvapi.common.TvManager;
import com.mstar.android.tvapi.common.vo.ProgramInfo;

@SuppressLint("HandlerLeak")
public class PlayerHolder {
    private Context mContext;

    private Activity mActivity;

    private TvView mTvView;

    private TvChannelManager mTvChannelManager;

    // 当ATV信号有变化时，会回调此监听器
    private AtvPlayerEventListener mAtvPlayerEventListener;

    // 当DTV信号有变化时，会回调此监听器
    private DtvPlayerEventListener mDtvPlayerEventListener;

    // 当除ATV和DTV之外的信号有变化时(如HDMI)，会回调此监听器；screenmode变化时也会回调此监听器，如HDMI分辨变化
    private TvPlayerEventListener mTvPlayerEventListener;

    // EPG中和PVR相关的计时接口
    private EventListener mTimeEventListener;

    private Handler mHandler;

    private SignalTipView mSignalTipView;

    private DtvInfoControlView mDtvInfoControlView;

    private AtvInfoControlView mAtvInfoControlView;

    private OtherInfoControlView mOtherInfoControlView;

    private ChannelInfoView mChannelInfoView;

    private OtherSourceInfoView mOtherSourceInfoView;

    private LinearLayout mAdLayout;

    /**
     * language view
     */
    private MTSView mMtsView;

    public PlayerHolder(Context context, TvChannelManager tvChannelManager, Handler mHandler) {
        this.mContext = context;
        this.mActivity = (Activity) context;
        this.mTvChannelManager = tvChannelManager;
        this.mHandler = mHandler;
        initViews();
    }

    private void initViews() {
        mActivity.setContentView(R.layout.player_activity);
        mTvView = (TvView) mActivity.findViewById(R.id.tvview);
        mTvView.setBackgroundColor(Color.TRANSPARENT);
        Boolean isPowerOn = mActivity.getIntent() != null ? mActivity.getIntent().getBooleanExtra("isPowerOn", false)
                : false;
        mTvView.openView(isPowerOn);
        mSignalTipView = (SignalTipView) mActivity.findViewById(R.id.signalview);
        mAtvInfoControlView = (AtvInfoControlView) mActivity.findViewById(R.id.atvinfoview);
        mDtvInfoControlView = (DtvInfoControlView) mActivity.findViewById(R.id.dtvinfoview);
        mOtherInfoControlView = (OtherInfoControlView) mActivity.findViewById(R.id.otherinfoview);
        mChannelInfoView = (ChannelInfoView) mActivity.findViewById(R.id.channelinfoview);
        mOtherSourceInfoView = (OtherSourceInfoView) mActivity.findViewById(R.id.otherinfo);
        mAdLayout = (LinearLayout) mActivity.findViewById(R.id.ll_layout_ad);
        mMtsView = (MTSView) mActivity.findViewById(R.id.mtsview);
    }

    public void showMtsView(int source) {
        if (mMtsView.isShow()) {
            mMtsView.changeMtsInfo(source);
        } else {
            mMtsView.getMtsInfo(source);
        }
    }

    public void showOtherSourceInfoView(int source, boolean isNeedResetZoom) {
        mOtherSourceInfoView.showOthersInfo(source, isNeedResetZoom);
    }

    public void showSelectChannelInfo(ProgramInfo info) {
        mChannelInfoView.channelChange(info);
    }

    public void showSelectChannelNum(String channelNum) {
        mChannelInfoView.selectChannel(channelNum);
    }

    public SignalTipView getmSignalTipView() {
        return mSignalTipView;
    }

    /**
     * set signalTip text
     * 
     * @param text
     */
    public void setSignalText(String text) {
        mSignalTipView.setText(text);
        /* set unlock program TEMPORARY_UNLOCK to 1 */
        if (mContext.getResources().getString(R.string.passwordtip).equals(text)) {
            Settings.System.putInt(mContext.getContentResolver(), Constants.TEMPORARY_UNLOCK, 1);
        }
    }

    /**
     * show nosignal Tips
     * 
     * @param source
     */
    public void setNosignalTips(int source) {
        String sourceName = "";
        switch (source) {
            case TvCommonManager.INPUT_SOURCE_ATV:
                sourceName = "ATV ";
                break;
            case TvCommonManager.INPUT_SOURCE_DTV:
                sourceName = "DTV ";
                break;
            case TvCommonManager.INPUT_SOURCE_HDMI:
                sourceName = "HDMI1 ";
                break;
            case TvCommonManager.INPUT_SOURCE_HDMI2:
                sourceName = "HDMI2 ";
                break;
            case TvCommonManager.INPUT_SOURCE_HDMI3:
                sourceName = "HDMI3 ";
                break;
            case TvCommonManager.INPUT_SOURCE_CVBS:
                sourceName = "AV ";
                break;
            case TvCommonManager.INPUT_SOURCE_YPBPR:
                sourceName = "YPBPR ";
                break;
            case TvCommonManager.INPUT_SOURCE_VGA:
                sourceName = "VGA ";
                break;
            default:
                break;
        }
        setSignalText(sourceName + mContext.getResources().getString(R.string.nosignaltips));
    }

    public void dismissSignalView() {
        if (mSignalTipView.isShow()) {
            mSignalTipView.dismiss();
        }
    }

    public void dismissAtvInfoView() {
        if (mAtvInfoControlView.isShow()) {
            mAtvInfoControlView.dismissView();
        }
    }

    public void dismissDtvInfoView() {
        if (mDtvInfoControlView.isShow()) {
            mDtvInfoControlView.dismissView();
        }
    }

    public void dismissOtherInfoView() {
        if (mOtherInfoControlView.isShow()) {
            mOtherInfoControlView.dismissView();
        }
    }

    public void showAtvInfoView(ProgramInfo info) {
        if (mAtvInfoControlView.isShow()) {
            mAtvInfoControlView.dismissView();
        } else {
            mAtvInfoControlView.showAtvChannelInfo(info);
        }
    }

    public void showDtvInfoView(ProgramInfo info) {
        if (mDtvInfoControlView.isShow()) {
            mDtvInfoControlView.dismissView();
        } else {
            mDtvInfoControlView.showDtvChannelInfo(info);
        }
    }

    /**
     * show the channel info
     * 
     * @param info
     * @param mCurInputSource
     */
    public void toggleInfoView(ProgramInfo info, int mCurInputSource) {
        if (mCurInputSource == TvCommonManager.INPUT_SOURCE_ATV) {
            if (info == null) {
                return;
            }
            mAtvInfoControlView.showAtvChannelInfo(info);
        } else if (mCurInputSource == TvCommonManager.INPUT_SOURCE_DTV) {
            if (info == null) {
                return;
            }
            if (TvChannelManager.getInstance().isSignalStabled()) {
                mDtvInfoControlView.showDtvChannelInfo(info);
            }
        }
    }

    /**
     * register the listener
     */
    public void registerListener() {
        mAtvPlayerEventListener = new AtvPlayerEventListener(mContext, mHandler);
        mTvChannelManager.registerOnAtvPlayerEventListener(mAtvPlayerEventListener);

        mDtvPlayerEventListener = new DtvPlayerEventListener(mContext, mHandler);
        mTvChannelManager.registerOnDtvPlayerEventListener(mDtvPlayerEventListener);

        mTvPlayerEventListener = new TvPlayerEventListener(mContext, mHandler);
        mTvChannelManager.registerOnTvPlayerEventListener(mTvPlayerEventListener);

        mTimeEventListener = new EventListener((Activity) mContext);
        TvManager mManager = TvManager.getInstance();
        if (mManager != null) {
            mManager.getTimerManager().setOnEventListener(mTimeEventListener);
        }
    }

    /**
     * unregister the listener
     */
    public void unRegisterListener() {
        if (mTvChannelManager != null) {
            if (mAtvPlayerEventListener != null) {
                mTvChannelManager.unregisterOnAtvPlayerEventListener(mAtvPlayerEventListener);
            }
            if (mDtvPlayerEventListener != null) {
                mTvChannelManager.unregisterOnDtvPlayerEventListener(mDtvPlayerEventListener);
            }
            if (mTvPlayerEventListener != null) {
                mTvChannelManager.unregisterOnTvPlayerEventListener(mTvPlayerEventListener);
            }
            if (mTimeEventListener != null) {
                TvManager mManager = TvManager.getInstance();
                if (mManager != null) {
                    mManager.getTimerManager().setOnEventListener(null);
                }
            }
        }
    }

    public LinearLayout getAdLayout() {
        return mAdLayout;
    }

}
