
package com.heran.launcher;

import java.util.HashMap;
import java.util.Map;

import com.heran.launcher.model.ViewBean;
import com.heran.launcher.util.ChannelInfoView;
import com.heran.launcher.util.ChannelManagerExt;
import com.heran.launcher.util.Constants;
import com.heran.launcher.util.FocusView;
import com.heran.launcher.util.ScreenProtection;
import com.heran.launcher.util.UIUtil;
import com.heran.launcher.util.Utils;
import com.mstar.android.tv.TvChannelManager;
import com.mstar.android.tv.TvCommonManager;
import com.mstar.android.tvapi.common.AudioManager;
import com.mstar.android.tvapi.common.TvManager;
import com.mstar.android.tvapi.common.exception.TvCommonException;
import com.mstar.android.tvapi.common.vo.MuteType.EnumMuteType;
import com.mstar.android.tvapi.common.vo.ProgramInfo;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Message;
import android.os.SystemProperties;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.util.Log;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewTreeObserver;
import android.view.ViewTreeObserver.OnGlobalLayoutListener;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;
import scifly.provider.SciflyStatistics;

@SuppressWarnings("deprecation")
public class LauncherHolder {
    private static final String TAG = "LauncherHolder";

    private static final int SELECTCHANNEL = 0x101;

    private static final int ADD_CHANNEL_INFOVIEW = 0x102;

    private static final int LOAD_CHANNEL = 0x103;
    
    private static final int TV_LISTENER_READY = 0x104;

    private AudioManager am = TvManager.getInstance().getAudioManager();

    private ImageButton mSettings;

    private ImageButton mIsynergy;

    private ImageButton mBrowser;

    private ImageButton mMediaBrowser;

    private TextView mTime;

    private TextView mDate;

    private FocusView mFocusView;

    // help tip obj
    public ImageView mHelp_tip;

    // tv sur
    protected SurfaceView mTv_sur;

    public static int adSwitch;

    public static boolean flag = false;

    // imageTV
    private ImageButton mImageTV;

    // FocusChangeListener
    private MyFocusChangeListener mFocusChangeListener;

    // OnClickListener
    private MyOnClickListener mOnClickListener;

    private LauncherActivity mContext;

    public boolean isCancelScale = false;

    private boolean flg = false;

    public boolean isPowerOn = false;

    private ViewBean mViewBean;

    private TextView msignalTip;

    private MyHandler mHandler;

    private ChannelManagerExt mChannelManagerExt;

    // Current input source.
    public int mCurInputSource = TvCommonManager.INPUT_SOURCE_NONE;

    public int mToInputSource = TvCommonManager.INPUT_SOURCE_NONE;

    private StringBuffer mSelectChannelNum = new StringBuffer();

    private static final int DELAYSELECTCHANNEL = 3000;

    public FrameLayout parent;

    private ChannelInfoView mChannelInfoView;

    public ImageView getHelp_tip() {
        return mHelp_tip;
    }

    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Constants.TOUPDATETIME: // update date and time
                    ContentResolver cv = mContext.getContentResolver();
                    String strTimeFormat = android.provider.Settings.System.getString(cv,
                            android.provider.Settings.System.TIME_12_24);
                    if (System.currentTimeMillis() * 0.001 < 1388505600) {
                        break;
                    }
                    if (strTimeFormat == null || strTimeFormat.equals("24")) {
                        mTime.setText(UIUtil.formatDate(System.currentTimeMillis(), "HH:mm"));
                    } else {
                        mTime.setText(UIUtil.formatDate(System.currentTimeMillis(), "a hh:mm"));
                    }
                    mDate.setText(
                            UIUtil.formatDate(System.currentTimeMillis(), mContext.getString(R.string.date_format)));
                    mHandler.removeMessages(Constants.TOUPDATETIME);
                    mHandler.sendEmptyMessageDelayed(Constants.TOUPDATETIME, Constants.DELAYUPDATETIME);
                    break;
                case Constants.TODISMISSTIP: // hide help_tip view
                    if (mHelp_tip != null) {
                        mHelp_tip.setVisibility(View.GONE);
                    }
                    break;
                case Constants.FINISH_BOOT_AD:
                    try {
                        int mSysStreamVolume = Settings.System.getInt(mContext.getContentResolver(), "sysStreamVolume");
                        android.media.AudioManager mAudioManager = (android.media.AudioManager) mContext
                                .getSystemService(Context.AUDIO_SERVICE);
                        mAudioManager.setStreamVolume(android.media.AudioManager.STREAM_SYSTEM, mSysStreamVolume, 0);
                    } catch (SettingNotFoundException e1) {
                        Log.d(TAG, "mSysStreamVolume == null");
                    }
                    initTips();
                    break;
                case SELECTCHANNEL:
                    int index = mChannelManagerExt.getChannelNums().indexOf(msg.arg1);
                    mSelectChannelNum = new StringBuffer();
                    if (index != -1 && mChannelInfoView != null) {
                        mChannelManagerExt.programNumSel(index);
                        mChannelInfoView.channelChange(mChannelManagerExt.getCurInfo());
                        if (mCurInputSource == TvCommonManager.INPUT_SOURCE_ATV) {
                            Map<String, String> map = new HashMap<String, String>();
                            map.put("channelNum", index + "");
                            map.put("channelName", mChannelManagerExt.getCurProgramInfo().serviceName);
                            SciflyStatistics.getInstance(mContext).recordEvent(mContext, "atvWatchRecord", map);
                            SciflyStatistics.getInstance(mContext).recordEvent(mContext, "atvChangeChannelRecord",
                                    "channelNum", index + "");
                        } else if (mCurInputSource == TvCommonManager.INPUT_SOURCE_DTV) {
                            Map<String, String> map = new HashMap<String, String>();
                            map.put("channelNum", index + "");
                            map.put("channelName", mChannelManagerExt.getCurProgramInfo().serviceName);
                            SciflyStatistics.getInstance(mContext).recordEvent(mContext, "dtvWatchRecord", map);
                            SciflyStatistics.getInstance(mContext).recordEvent(mContext, "dtvChangeChannelRecord",
                                    "channelNum", index + "");
                        }
                    }

                    break;
                case LOAD_CHANNEL:
                    if (mCurInputSource == TvCommonManager.INPUT_SOURCE_STORAGE) {
                        mCurInputSource = mContext.mLogic.queryCurInputSrc();
                    }
                    Log.v(TAG, "mCurInputSource = " + mCurInputSource);
                    if (mCurInputSource == TvCommonManager.INPUT_SOURCE_ATV
                            || mCurInputSource == TvCommonManager.INPUT_SOURCE_DTV) {
                        mChannelManagerExt.getAllChannels(mContext, mCurInputSource);
                        ProgramInfo info = TvChannelManager.getInstance().getCurrentProgramInfo();
                        TvChannelManager.getInstance().selectProgram(info.number, info.serviceType);
                        mHandler.sendEmptyMessage(ADD_CHANNEL_INFOVIEW);
                    }
                    break;
                case ADD_CHANNEL_INFOVIEW:
                    if (mChannelInfoView == null && mChannelManagerExt.getCurInfo() != null && parent != null) {
                        long time = System.currentTimeMillis();
                        mChannelInfoView = new ChannelInfoView(mContext, mChannelManagerExt.getCurInfo());
                        parent.addView(mChannelInfoView);
                        if (mCurInputSource == TvCommonManager.INPUT_SOURCE_ATV) {
                            Map<String, String> map = new HashMap<String, String>();
                            map.put("channelNum", mChannelManagerExt.getCurProgramInfo().number + "");
                            map.put("channelName", mChannelManagerExt.getCurProgramInfo().serviceName);
                            SciflyStatistics.getInstance(mContext).recordEvent(mContext, "atvWatchRecord", map);
                        } else if (mCurInputSource == TvCommonManager.INPUT_SOURCE_DTV) {
                            Map<String, String> map = new HashMap<String, String>();
                            map.put("channelNum", mChannelManagerExt.getCurProgramInfo().number + "");
                            map.put("channelName", mChannelManagerExt.getCurProgramInfo().serviceName);
                            SciflyStatistics.getInstance(mContext).recordEvent(mContext, "dtvWatchRecord", map);
                        }
                        Log.v(TAG, "addview time = " + (System.currentTimeMillis() - time));
                    }
                    break;
                case TV_LISTENER_READY:
                    TvCommonManager.getInstance().setTvosCommonCommand("TVEventListenerReady");
                    break;
                default:
                    break;
            }
        }

    }

    public LauncherHolder(LauncherActivity context) {
        mContext = context;
        mHandler = new MyHandler();
        mFocusChangeListener = new MyFocusChangeListener();
        mOnClickListener = new MyOnClickListener();
        mViewBean = new ViewBean(null, null);
        mChannelManagerExt = ChannelManagerExt.getInstance();
        findViews();
        initData();
        registerListener();
    }

    private void findViews() {
        mSettings = (ImageButton) mContext.findViewById(R.id.settings);
        mIsynergy = (ImageButton) mContext.findViewById(R.id.isynergy);
        mBrowser = (ImageButton) mContext.findViewById(R.id.browser);
        mMediaBrowser = (ImageButton) mContext.findViewById(R.id.mediabrowser);
        mTime = (TextView) mContext.findViewById(R.id.time_txt);
        mDate = (TextView) mContext.findViewById(R.id.date_txt);
        mFocusView = (FocusView) mContext.findViewById(R.id.selector);
        mViewBean.setmFocusObject(mFocusView);
        mImageTV = (ImageButton) mContext.findViewById(R.id.imagetv);
        msignalTip = (TextView) mContext.findViewById(R.id.signal_tips);
        parent = (FrameLayout) mContext.findViewById(R.id.home_layout);
        mTv_sur = (SurfaceView) mContext.findViewById(R.id.tv_sur);
        mHelp_tip = (ImageView) mContext.findViewById(R.id.help_tip);

    }

    private void initData() {
        mHandler.sendEmptyMessage(Constants.TOUPDATETIME);
        mTv_sur.setBackgroundColor(Color.BLACK);
        openSurfaceView();

        adSwitch = Settings.System.getInt(mContext.getContentResolver(), "adSwitch", 1);
        Log.d(TAG, "adSwitch:" + adSwitch);

        if (adSwitch == 1) { // logo2影片可播放完整；使用者可按「首頁」或「離開」鍵結束影片，回到首頁
            mContext.mLogic.handFirstPowerOnEvent();
            new Thread(new Runnable() {
                @Override
                public void run() {
                    while ("0".equals(SystemProperties.get("mstar.videoadvert.finished", "0"))) {
                        try {
                            Thread.sleep(500);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                    }
                    Log.d(TAG, "finish boot ad play");
                    mHandler.sendEmptyMessage(Constants.FINISH_BOOT_AD);
                }
            }).start();
        }

    }

    private void initTips() {
        // the animation when mHelp_tip show
        mHelp_tip.setVisibility(View.VISIBLE);
        AnimatorSet set = new AnimatorSet();
        ObjectAnimator anim1 = ObjectAnimator.ofFloat(mHelp_tip, "alpha", 0f, 1f);
        anim1.setDuration(2000);
        ObjectAnimator anim2 = ObjectAnimator.ofFloat(mHelp_tip, "alpha", 1f, 1f);
        anim2.setDuration(6000);
        ObjectAnimator anim3 = ObjectAnimator.ofFloat(mHelp_tip, "alpha", 1f, 0f);
        anim3.setDuration(2000);
        set.play(anim1).before(anim2);
        set.play(anim3).after(anim2);
        set.start();
    }

    private void registerListener() {
        mSettings.setOnFocusChangeListener(mFocusChangeListener);
        mSettings.setOnClickListener(mOnClickListener);
        mIsynergy.setOnFocusChangeListener(mFocusChangeListener);
        mIsynergy.setOnClickListener(mOnClickListener);
        mBrowser.setOnFocusChangeListener(mFocusChangeListener);
        mBrowser.setOnClickListener(mOnClickListener);
        mMediaBrowser.setOnFocusChangeListener(mFocusChangeListener);
        mMediaBrowser.setOnClickListener(mOnClickListener);

        setImageTVListeners();
        mHandler.sendEmptyMessage(TV_LISTENER_READY);
    }

    /**
     * init mImageTV ,add listeners
     */
    private void setImageTVListeners() {
        // jump to TV player when click the imageTV
        mImageTV.setOnClickListener(mOnClickListener);
        mImageTV.setOnFocusChangeListener(mFocusChangeListener);

        addViewGlobalLayoutListener(mImageTV, mViewBean);

        mImageTV.setOnKeyListener(new View.OnKeyListener() {
            @SuppressLint("NewApi")
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    mCurInputSource = TvCommonManager.getInstance().getCurrentTvInputSource();
                    switch (keyCode) {
                        case KeyEvent.KEYCODE_0:
                        case KeyEvent.KEYCODE_1:
                        case KeyEvent.KEYCODE_2:
                        case KeyEvent.KEYCODE_3:
                        case KeyEvent.KEYCODE_4:
                        case KeyEvent.KEYCODE_5:
                        case KeyEvent.KEYCODE_6:
                        case KeyEvent.KEYCODE_7:
                        case KeyEvent.KEYCODE_8:
                        case KeyEvent.KEYCODE_9:
                            if (mChannelInfoView != null && (mCurInputSource == TvCommonManager.INPUT_SOURCE_ATV
                                    || mCurInputSource == TvCommonManager.INPUT_SOURCE_DTV)) {
                                if (mSelectChannelNum.length() > 0
                                        && Integer.valueOf(String.valueOf(mSelectChannelNum.charAt(0))) == 0) {
                                    mSelectChannelNum.deleteCharAt(0);
                                }
                                if (mSelectChannelNum.length() == 3) {
                                    mSelectChannelNum = new StringBuffer();
                                }
                                mSelectChannelNum.append(String.valueOf(keyCode - KeyEvent.KEYCODE_0));
                                mChannelInfoView.selectChannel(mSelectChannelNum.toString());
                                mHandler.removeMessages(SELECTCHANNEL);
                                if ((mCurInputSource == TvCommonManager.INPUT_SOURCE_ATV
                                        && mSelectChannelNum.length() == 2
                                        && Integer.valueOf(mSelectChannelNum.toString()) > 12)
                                        || mSelectChannelNum.length() == 3) {
                                    Message msg = mHandler.obtainMessage();
                                    msg.what = SELECTCHANNEL;
                                    msg.arg1 = Integer.valueOf(mSelectChannelNum.toString());
                                    mHandler.sendMessage(msg);
                                } else {
                                    Message msg = mHandler.obtainMessage();
                                    msg.what = SELECTCHANNEL;
                                    msg.arg1 = Integer.valueOf(mSelectChannelNum.toString());
                                    mHandler.sendMessageDelayed(msg, DELAYSELECTCHANNEL);
                                }
                            }
                            break;
                        case KeyEvent.KEYCODE_CHANNEL_UP:
                            if (mChannelInfoView != null && (mCurInputSource == TvCommonManager.INPUT_SOURCE_ATV
                                    || mCurInputSource == TvCommonManager.INPUT_SOURCE_DTV)) {
                                mChannelManagerExt.channelUp();
                                if (mChannelManagerExt.getCurInfo() != null) {
                                    mChannelInfoView.channelChange(mChannelManagerExt.getCurInfo());
                                    int index = mChannelManagerExt.getCurInfo().number;
                                    if (mCurInputSource == TvCommonManager.INPUT_SOURCE_ATV) {
                                        Map<String, String> map = new HashMap<String, String>();
                                        map.put("channelNum", index + "");
                                        map.put("channelName", mChannelManagerExt.getCurProgramInfo().serviceName);
                                        SciflyStatistics.getInstance(mContext).recordEvent(mContext, "atvWatchRecord",
                                                map);
                                        SciflyStatistics.getInstance(mContext).recordEvent(mContext,
                                                "atvChangeChannelRecord", "channelNum", index + "");
                                    } else if (mCurInputSource == TvCommonManager.INPUT_SOURCE_DTV) {
                                        Map<String, String> map = new HashMap<String, String>();
                                        map.put("channelNum", index + "");
                                        map.put("channelName", mChannelManagerExt.getCurProgramInfo().serviceName);
                                        SciflyStatistics.getInstance(mContext).recordEvent(mContext, "dtvWatchRecord",
                                                map);
                                        SciflyStatistics.getInstance(mContext).recordEvent(mContext,
                                                "dtvChangeChannelRecord", "channelNum", index + "");
                                    }
                                }
                            }
                            break;
                        case KeyEvent.KEYCODE_CHANNEL_DOWN:
                            if (mChannelInfoView != null && (mCurInputSource == TvCommonManager.INPUT_SOURCE_ATV
                                    || mCurInputSource == TvCommonManager.INPUT_SOURCE_DTV)) {
                                mChannelManagerExt.channelDown();
                                if (mChannelManagerExt.getCurInfo() != null) {
                                    mChannelInfoView.channelChange(mChannelManagerExt.getCurInfo());
                                    int index = mChannelManagerExt.getCurInfo().number;
                                    if (mCurInputSource == TvCommonManager.INPUT_SOURCE_ATV) {
                                        Map<String, String> map = new HashMap<String, String>();
                                        map.put("channelNum", index + "");
                                        map.put("channelName", mChannelManagerExt.getCurProgramInfo().serviceName);
                                        SciflyStatistics.getInstance(mContext).recordEvent(mContext, "atvWatchRecord",
                                                map);
                                        SciflyStatistics.getInstance(mContext).recordEvent(mContext,
                                                "atvChangeChannelRecord", "channelNum", index + "");
                                    } else if (mCurInputSource == TvCommonManager.INPUT_SOURCE_DTV) {
                                        Map<String, String> map = new HashMap<String, String>();
                                        map.put("channelNum", index + "");
                                        map.put("channelName", mChannelManagerExt.getCurProgramInfo().serviceName);
                                        SciflyStatistics.getInstance(mContext).recordEvent(mContext, "dtvWatchRecord",
                                                map);
                                        SciflyStatistics.getInstance(mContext).recordEvent(mContext,
                                                "dtvChangeChannelRecord", "channelNum", index + "");
                                    }
                                }
                            }
                            break;
                        default:
                            break;
                    }
                }
                return false;
            }
        });
    }

    /**
     * init SurfaceView 、set SurfaceView small programSel
     */
    private void initSurfaceView() {
        if (mTv_sur != null) {
            mTv_sur.setBackgroundColor(Color.TRANSPARENT);
        }
        int curSource = mContext.mLogic.queryCurInputSrc();
        mContext.mLogic.setSmallscale(isCancelScale);
        if (curSource == TvCommonManager.INPUT_SOURCE_ATV || curSource == TvCommonManager.INPUT_SOURCE_DTV) {
            ProgramInfo pinfo = mContext.mLogic.getCurrProgramInfo();
            mContext.mLogic.programSel(pinfo.number, pinfo.serviceType);
        }
        ScreenProtection.getInstance().start(mContext, msignalTip);
    }

    /**
     * show SurfaceView
     */
    private void openSurfaceView() {
        mTv_sur.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

            }

            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                Log.v(TAG, "mContext.isPowerOn:" + isPowerOn);
                if (isPowerOn) {
                    handlertv.postDelayed(pip_thread, 1500);
                    isPowerOn = false;
                } else {
                    handlertv.postDelayed(pip_thread, 500);
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {

            }
        });
    }

    // handlertv postDelayed chanage source
    Handler handlertv = new Handler();

    Runnable pip_thread = new Runnable() {
        @Override
        public void run() {
            initSurfaceView();
            handlertv.removeCallbacks(pip_thread);
        }
    };

    Runnable handlerRuntv = new Runnable() {
        @Override
        public void run() {
            if (mTv_sur != null) {
                mTv_sur.setBackgroundColor(Color.TRANSPARENT);
            }
            handlertv.removeCallbacks(handlerRuntv);
        }
    };

    private void drawFocus(View view) {
        mViewBean.setmCurFocusView(view);
        mViewBean.getmFocusObject().startAnimation(view);
    }

    /**
     * handler focus change
     */
    class MyFocusChangeListener implements OnFocusChangeListener {

        @Override
        public void onFocusChange(View view, boolean flag) {
            if (flag) {
                drawFocus(view);
            }
        }
    }

    class MyOnClickListener implements OnClickListener {

        @Override
        public void onClick(View view) {
            if (mTv_sur != null) {
                mTv_sur.setBackgroundColor(Color.BLACK);
            }
            switch (view.getId()) {
                case R.id.imagetv:
                    mContext.isClickPlayerActivity=true;
                    mContext.mLogic.startApk("com.eostek.tv.player", "com.eostek.tv.player.PlayerActivity", null);
                    isCancelScale = true;
                    break;
                case R.id.settings:
                    Intent intent = new Intent();
                    intent.setAction("android.settings.SIMPLESETTINGS");
                    mContext.startActivity(intent);
                    break;
                case R.id.isynergy:
                    mContext.mLogic.startApk("com.biaoqi.filefly", "com.biaoqi.filefly.STBFileFlyMainActivity", null);
                    break;
                case R.id.browser:
                    mContext.mLogic.startApk("com.android.browser", "com.android.browser.BrowserActivity", null);
                    break;
                case R.id.mediabrowser:
                    mContext.mLogic.startApk("com.hrtvbic.usb.S6A918", "com.hrtvbic.usb.S6A918.MainActivity", null);
                    break;
                default:
                    break;
            }
        }

    }

    protected void addViewGlobalLayoutListener(View mView, ViewBean mViewBean) {
        ViewTreeObserver mObserver = mView.getViewTreeObserver();
        if (mObserver.isAlive()) {
            mObserver.addOnGlobalLayoutListener(new MyOnGlobalLayoutListener(mView, mViewBean));
        }
    }

    /**
     * onGlobalLayout called when the view has done onMeasure() , then draw the
     * focus view at the position of the view
     */
    class MyOnGlobalLayoutListener implements OnGlobalLayoutListener {

        private View mView;

        private ViewBean mBean;

        public MyOnGlobalLayoutListener(View view, ViewBean mBean) {
            this.mView = view;
            this.mBean = mBean;
        }

        /*
         * * (non-Javadoc)
         * @see
         * android.view.ViewTreeObserver.OnGlobalLayoutListener#onGlobalLayout()
         */

        @Override
        public void onGlobalLayout() {
            if (mView == null || mBean == null || mBean.getmCurFocusView() == null) {
                return;
            }
            mView.getViewTreeObserver().removeGlobalOnLayoutListener(this);
            if (mView.getId() == mBean.getmCurFocusView().getId()) {
                mView.requestFocus();
                mBean.getmFocusObject().startAnimation(mView);
            }
        }
    }

    // ATV&DTV source plosive
    Runnable mute_thread = new Runnable() {
        @Override
        public void run() {
            try {
                if (am != null) {
                    am.disableMute(EnumMuteType.E_MUTE_PERMANENT);
                }
            } catch (TvCommonException e) {
                e.printStackTrace();
            }
            handlertv.removeCallbacks(mute_thread);
        }
    };

    public void doOnResume() {
        mHandler.removeMessages(Constants.TOUPDATETIME);
        mHandler.sendEmptyMessageDelayed(Constants.TOUPDATETIME, Constants.DELAYUPDATETIME);
        isCancelScale = false;
        mCurInputSource = TvCommonManager.getInstance().getCurrentTvInputSource();
        mHandler.sendEmptyMessageDelayed(LOAD_CHANNEL, 2000);
        if (mTv_sur == null) {
            isPowerOn = true;
            handlertv.postDelayed(handlerRuntv, 2000);
            Log.d(TAG, "onResume  surfaceView = null");
        } else {
            Log.d(TAG, "onResume  surfaceView != null, flg:" + flg);
            if (flg) {
                mTv_sur.setBackgroundColor(Color.BLACK);
                handlertv.postDelayed(handlerRuntv, 100);
                flg = false;
            }
        }
        if (flag && mCurInputSource != mToInputSource) {
            mContext.mLogic.setSmallscale(isCancelScale);
            flag = false;
        }
    }

    public void doOnPause() {
        flag = true;
        mToInputSource = TvCommonManager.getInstance().getCurrentTvInputSource();
    }

    public void doOnStop() {
        flag = false;
        flg = true;
        mHandler.removeMessages(LOAD_CHANNEL);
        mHandler.removeMessages(ADD_CHANNEL_INFOVIEW);
        mChannelInfoView = null;
        String className = Utils.getCurrentActivity(mContext);
        if (!className.equals("com.eostek.tv.player.PlayerActivity")
                && !className.equals("com.utsmta.app.MainActivity")) {
            Log.d(TAG, "in stop");
            TvCommonManager.getInstance().setInputSource(mContext.mLogic.queryCurInputSrc());
            TvCommonManager.getInstance().setInputSource(TvCommonManager.INPUT_SOURCE_STORAGE);
        }
    }

    public void doOnRestart() {
        mCurInputSource = TvCommonManager.getInstance().getCurrentTvInputSource();
        if (mCurInputSource == TvCommonManager.INPUT_SOURCE_STORAGE) {
            mCurInputSource = mContext.mLogic.queryCurInputSrc();
            TvCommonManager.getInstance().setInputSource(mCurInputSource);
        }
    }

    /**
     * get current hot key source
     * 
     * @param int hotkeyValue
     * @return source TvCommonManager.INPUT_SOURCE_ATV E_INPUT_SOURCE_DTV
     *         E_INPUT_SOURCE_HDMI E_INPUT_SOURCE_HDMI4 E_INPUT_SOURCE_SVIDEO
     *         E_INPUT_SOURCE_YPBPR E_INPUT_SOURCE_VGA
     */
    public int getHotKeySource(int hotkeyValue) {
        int source = TvCommonManager.INPUT_SOURCE_NONE;

        switch (hotkeyValue) {
            case 1:
                try {
                    am.enableMute(EnumMuteType.E_MUTE_PERMANENT);
                } catch (TvCommonException e) {
                    e.printStackTrace();
                }
                source = TvCommonManager.INPUT_SOURCE_ATV;
                handlertv.postDelayed(mute_thread, 500);
                break;
            case 2:
                try {
                    am.enableMute(EnumMuteType.E_MUTE_PERMANENT);
                } catch (TvCommonException e) {
                    e.printStackTrace();
                }
                source = TvCommonManager.INPUT_SOURCE_DTV;
                handlertv.postDelayed(mute_thread, 500);
                break;
            case 3:
                source = TvCommonManager.INPUT_SOURCE_HDMI;
                break;
            case 4:
                source = TvCommonManager.INPUT_SOURCE_HDMI2;
                break;
            case 5:
                source = TvCommonManager.INPUT_SOURCE_HDMI3;
                break;
            case 6:
                source = TvCommonManager.INPUT_SOURCE_CVBS;
                break;
            case 7:
                source = TvCommonManager.INPUT_SOURCE_YPBPR;
                break;
            case 8:
                source = TvCommonManager.INPUT_SOURCE_VGA;
                break;
            default:
                break;
        }
        return source;
    }

}
