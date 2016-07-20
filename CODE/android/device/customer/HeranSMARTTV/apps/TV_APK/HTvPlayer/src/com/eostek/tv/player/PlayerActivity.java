
package com.eostek.tv.player;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.eostek.tv.player.business.AdManager;
import com.eostek.tv.player.business.TvDBManager;
import com.eostek.tv.player.channelManager.ChannelListActivity;
import com.eostek.tv.player.channelManager.FavoriteChannelsActivity;
import com.eostek.tv.player.dialog.PasswordCheckDialog;
import com.eostek.tv.player.hotkey.EpgActivity;
import com.eostek.tv.player.model.AdInfo;
import com.eostek.tv.player.util.AtvInfoControlView;
import com.eostek.tv.player.util.ChannelInfoView;
import com.eostek.tv.player.util.ChannelManagerExt;
import com.eostek.tv.player.util.Constants;
import com.eostek.tv.player.util.DtvInfoControlView;
import com.eostek.tv.player.util.FactoryDeskImpl;
import com.eostek.tv.player.util.MTSView;
import com.eostek.tv.player.util.OtherInfoControlView;
import com.eostek.tv.player.util.OtherSourceInfoView;
import com.eostek.tv.player.util.ProtectTipView;
import com.eostek.tv.player.util.SignalTipView;
import com.eostek.tv.player.util.UtilsTools;
import com.mstar.android.MKeyEvent;
import com.mstar.android.tv.TvChannelManager;
import com.mstar.android.tv.TvCommonManager;
import com.mstar.android.tv.TvPictureManager;
import com.mstar.android.tv.widget.TvView;
import com.mstar.android.tvapi.common.TimerManager;
import com.mstar.android.tvapi.common.TvManager;
import com.mstar.android.tvapi.common.exception.TvCommonException;
import com.mstar.android.tvapi.common.vo.EnumVideoArcType;
import com.mstar.android.tvapi.common.vo.MuteType.EnumMuteType;
import com.mstar.android.tvapi.common.vo.ProgramInfo;
import com.nostra13.universalimageloader.cache.disc.naming.Md5FileNameGenerator;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.ImageLoaderConfiguration;
import com.nostra13.universalimageloader.core.assist.QueueProcessingType;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Color;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.os.StatFs;
import android.os.SystemProperties;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.provider.Settings.System;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.Toast;
import scifly.provider.SciflyStatistics;

/**
 * To show TV and control it, such as channel change(up/down/return), EPG, MTS,
 * Info, Volume up/down, TV Menu, PVR(record/pause/play/index) and so
 * on. @projectName： EosTvPlayer @moduleName： PlayerActivity.java
 * 
 * @author jachensy.chen
 * @version 1.0.0
 * @time 2014-2-17
 * @Copyright © 2013 EOSTEK, Inc.
 */
public class PlayerActivity extends Activity {
    private static final String TAG = "PlayerActivity";

    private TvView tvView;

    private String SETUPPACKAGENAME = "com.mstar.tv.menu";

    private String SETUPCLASSNAME = "com.mstar.tv.menu.ui.EosCustomSettingActivity";

    private static final String AVTTUNING = "com.mstar.tv.menu.setting.ATVAutoTuningActivity";

    private static final String DVTTUNING = "com.mstar.tv.menu.setting.AutoTuningActivity";

    // Need reset channel or not.
    private boolean isRestChannels = false;

    // is Ad Video finish
    private boolean isAdVideo = false;

    // Current input source.
    private int mCurInputSource = TvCommonManager.INPUT_SOURCE_NONE;

    private ChannelManagerExt mChannelManagerExt;

    private boolean isFirstInPlayer = true;

    private ChannelInfoView mChannelInfoView;

    // private ChannelInfoDetailsView mChannelInfoDetailsView;

    private DtvInfoControlView mDtvInfoControlView;

    private AtvInfoControlView mAtvInfoControlView;

    private OtherInfoControlView mOtherInfoControlView;

    private OtherSourceInfoView mOtherSourceInfoView;

    private SignalTipView mSignalTipView;

    private MTSView mMtsView;

    private StringBuffer mSelectChannelNum = new StringBuffer();

    private FrameLayout parent;

    private ProtectTipView mProtectTipView;

    private static final int SELECTCHANNEL = 0x05;

    private static final int DELAYSELECTCHANNEL = 3000;

    private static final int STARTPVR = 0x06;

    private static final int SHOW_CHANNEL_INFO = 0x07;

    private static final int SHOW_TOGGLE_INFO = 0x08;

    private static final int DELAY_SHOW_TOGGLE_INFO = 500;

    private static final int DELAY_START_PVRF_ROM_BOOT = 2000;

    public static String mLastRecordedFileName = null;

    public static Boolean isLanguageChange = false;

    private static boolean flg = true;

    private HideSystemUIBroadcast mBroadcast;

    public static String UNSUPPORT_TIP = null;

    private com.mstar.android.tvapi.common.AudioManager mTvAudioManager = TvManager.getInstance().getAudioManager();

    private boolean isFirst = true;

    private static final int DELAYTIME = 2000;

    // LowBuleMode Remind Function ,set ReMind Time
    private static final int REMIND_DELAYTIME = 30 * 60 * 1000;

    private static final int TIME = 0;

    private static final int STARTTVMENU = 1;

    public static final int PASSWORDTIPDISMISS = 2;

    public static final int INPUT_PASSWORD = 3;

    public static final int SHOW_LEFT_INFO = 4;

    public static final int UNSUPPORT_TIPS = 5;

    public static final int DISMISS_SIGNAL_TIPS = 6;

    public static final int AUDO_ADJUST = 7;

    public static final int DISMISS = 8;

    public static final int UNSIGNAL = 9;

    public static final int SIGNAL_TUNING = 10;

    public static final int TV_LISTENER_READY = 11;

    public static final int PROTECT_REMIND_TIPS = 12;

    protected static final int FINISH_AD_VIDEO = 13;

    TvChannelManager tvChannelManager = TvChannelManager.getInstance();

    AtvPlayerEventListener mAtvPlayerEventListener = null;

    DtvPlayerEventListener mDtvPlayerEventListener = null;

    TvPlayerEventListener mTvPlayerEventListener = null;

    public static Boolean sHasDiskCache = true;

    private static final int KEY_UP = 1;

    private static final int KEY_DOWN = 2;

    private static final int KEY_RETURN = 3;
    
    boolean hasTuner = false;

    // TvEventListener mTvEventListener = null;

    private Handler handler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case STARTTVMENU:
                    startTVMenu();
                    break;
                case TIME:
                    isFirst = false;
                    break;
                case PASSWORDTIPDISMISS:
                    mSignalTipView.setText("");
                    break;
                case INPUT_PASSWORD:
                    mSignalTipView.setText(PlayerActivity.this.getResources().getString(R.string.passwordtip));
                    break;
                case SHOW_LEFT_INFO:
                    showInfoView();
                    break;
                case UNSUPPORT_TIPS:
                    mSignalTipView.setText(PlayerActivity.this.getResources().getString(R.string.unsupporttips));
                    break;
                case DISMISS_SIGNAL_TIPS:
                    if (mSignalTipView.isShow()) {
                        mSignalTipView.dismiss();
                    }
                    break;
                case AUDO_ADJUST:
                    mSignalTipView.setText(PlayerActivity.this.getResources().getString(R.string.autoadjust));
                    break;
                case DISMISS:
                    mSignalTipView.dismiss();
                    break;
                case UNSIGNAL:
                    int curInputSource = TvCommonManager.getInstance().getCurrentTvInputSource();
                    ChannelManagerExt.getInstance().setNosignalTips(PlayerActivity.this, curInputSource,
                            mSignalTipView);
                    break;
                case SIGNAL_TUNING:
                    int curSource = TvCommonManager.getInstance().getCurrentTvInputSource();
                    if (TvChannelManager.getInstance().isSignalStabled()) {
                        // DTV 对时
                        if (mCurInputSource == TvCommonManager.INPUT_SOURCE_DTV) {
                            try {
                                Log.e(TAG, "start TimeSync with DTV");
                                TvManager.getInstance().getTimerManager()
                                        .setLinuxTimeSource(TimerManager.LINUX_TIMESOURCE_DTV);
                            } catch (TvCommonException e) {
                                Log.e(TAG, "TimeSyncReceiver set time source fail !!!!");
                            }
                        }
                        mSignalTipView.dismiss();
                    } else {
                        if (curSource == TvCommonManager.INPUT_SOURCE_ATV
                                || curSource == TvCommonManager.INPUT_SOURCE_DTV) {
                            mChannelManagerExt.getAllChannels(PlayerActivity.this, curSource);
                            if (mChannelManagerExt.getChannels().size() <= 0) {
                                Log.v(TAG, "is tuningtip");
                                mSignalTipView.setText(getResources().getString(R.string.tuningtip));
                            }
                        } else {
                            mChannelManagerExt.setNosignalTips(PlayerActivity.this, curSource, mSignalTipView);
                        }
                        boolean isVGA = curSource == TvCommonManager.INPUT_SOURCE_VGA;
                        Log.d(TAG, "isVGA = " + isVGA);
                        if (Settings.System.getInt(PlayerActivity.this.getContentResolver(), "savemode", 1) == 1
                                || isVGA) {
                            Intent intentStandby = new Intent(Constants.STARTSTANDBY);
                            PlayerActivity.this.sendBroadcast(intentStandby);
                        }
                    }
                    break;
                case TV_LISTENER_READY:
                    TvCommonManager.getInstance().setTvosCommonCommand("TVEventListenerReady");
                    break;
                case PROTECT_REMIND_TIPS:
                    mProtectTipView.showTipView();
                    break;
                case FINISH_AD_VIDEO:
                    finish();
                default:
                    break;
            }
        }

    };

    private Handler mSelectChannelHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SELECTCHANNEL:
                    int index = mChannelManagerExt.getChannelNums().indexOf(msg.arg1);
                    mSelectChannelNum = new StringBuffer();
                    if (index != -1) {
                        mChannelManagerExt.programNumSel(index);
                        mChannelInfoView.channelChange(mChannelManagerExt.getCurInfo());
                        sendEmptyMessageDelayed(SHOW_TOGGLE_INFO, DELAY_SHOW_TOGGLE_INFO);
                        // toggleInfoViews();

                        // data uploading. add by vicent
                        if (mCurInputSource == TvCommonManager.INPUT_SOURCE_ATV) {
                            Map<String, String> map = new HashMap<String, String>();
                            map.put("channelNum", index + "");
                            map.put("channelName", mChannelManagerExt.getCurProgramInfo().serviceName);
                            SciflyStatistics.getInstance(getApplicationContext()).recordEvent(PlayerActivity.this,
                                    "atvWatchRecord", map);
                            SciflyStatistics.getInstance(getApplicationContext()).recordEvent(PlayerActivity.this,
                                    "atvChangeChannelRecord", "channelNum", index + "");
                        } else if (mCurInputSource == TvCommonManager.INPUT_SOURCE_DTV) {
                            Map<String, String> map = new HashMap<String, String>();
                            map.put("channelNum", index + "");
                            map.put("channelName", mChannelManagerExt.getCurProgramInfo().serviceName);
                            SciflyStatistics.getInstance(getApplicationContext()).recordEvent(PlayerActivity.this,
                                    "dtvWatchRecord", map);
                            SciflyStatistics.getInstance(getApplicationContext()).recordEvent(PlayerActivity.this,
                                    "dtvChangeChannelRecord", "channelNum", index + "");
                        }
                    }
                    break;
                case SHOW_TOGGLE_INFO:
                    toggleInfoViews();
                    break;
                case STARTPVR:
                    // Intent intent = new Intent(PlayerActivity.this,
                    // PVRActivity.class);
                    // intent.putExtra("PVR_ONE_TOUCH_MODE", 1);
                    // startActivity(intent);
                    break;
                case SHOW_CHANNEL_INFO:
                    if (mChannelManagerExt.getCurInfo() != null) {
                        mChannelInfoView.channelChange(mChannelManagerExt.getCurInfo());
                        if (mCurInputSource == TvCommonManager.INPUT_SOURCE_DTV && mDtvInfoControlView != null) {
                            mDtvInfoControlView.showDtvChannelInfo(mChannelManagerExt.getCurInfo());
                        } else if (mCurInputSource == TvCommonManager.INPUT_SOURCE_ATV
                                && mAtvInfoControlView != null) {
                            mAtvInfoControlView.showAtvChannelInfo(mChannelManagerExt.getCurInfo());
                        }
                    }
                    break;
                default:
                    break;
            }
        }
    };

    private Handler mSystemUIHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            PlayerActivity.this.getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);
            Log.v(TAG, "mSystemUIHandler = " + getWindow().getDecorView().getSystemUiVisibility());
        }

    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        
        
        try {
            hasTuner = TvManager.getInstance().getFactoryManager().getTunerStatus();
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        if (!hasTuner) {
            int source = TvCommonManager.getInstance().getCurrentTvInputSource();
            if (source == TvCommonManager.INPUT_SOURCE_ATV || source == TvCommonManager.INPUT_SOURCE_DTV) {
                TvCommonManager.getInstance().setInputSource(TvCommonManager.INPUT_SOURCE_HDMI);
            }
        }
        // if internal sdcard size < 60M, set diskcache disable
        if (getAvailableInternalSDCardSize() < 60) {
            sHasDiskCache = false;
        }
        // init imageloader configuration
        ImageLoaderConfiguration config = new ImageLoaderConfiguration.Builder(getApplicationContext())
                .threadPriority(Thread.NORM_PRIORITY).denyCacheImageMultipleSizesInMemory()
                .diskCacheFileNameGenerator(new Md5FileNameGenerator()).tasksProcessingOrder(QueueProcessingType.LIFO)
                .diskCacheSize(50 * 1024 * 1024) // 50 Mb
                .build();
        ImageLoader.getInstance().init(config);
        // parse network data and then persist them;
        new AdManager(this, handler).parseJson();
        mChannelManagerExt = ChannelManagerExt.getInstance();
        setContentView(R.layout.eos_player);
        tvView = (TvView) findViewById(R.id.tvview);
        Boolean isPowerOn = getIntent() != null ? getIntent().getBooleanExtra("isPowerOn", false) : false;
        tvView.openView(isPowerOn);
        Intent intent = this.getIntent();
        isAdVideo = intent.getBooleanExtra("isAdVideo", false);
        Log.d(TAG, "----------isAdVideo：" + isAdVideo);
        if (isAdVideo) {
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
                    Log.d(TAG, "FINISH_AD_VIDEO");
                    handler.sendEmptyMessage(FINISH_AD_VIDEO);
                }
            }).start();
        } else {
            initTvUI();
        }

    }

    private void initTvUI() {
        parent = (FrameLayout) findViewById(R.id.main);
        if (getIntent().getIntExtra("PVR_ONE_TOUCH_MODE", -1) == 1) {
            Log.e(TAG, "go to pvr record.");
            mSelectChannelHandler.sendEmptyMessageDelayed(STARTPVR, DELAY_START_PVRF_ROM_BOOT);
        }

        if (mSignalTipView == null) {
            mSignalTipView = new SignalTipView(this);
            parent.addView(mSignalTipView);
        }

        if (mProtectTipView == null) {
            mProtectTipView = new ProtectTipView(this);
            parent.addView(mProtectTipView);
        }
        mAtvPlayerEventListener = new AtvPlayerEventListener(this, mSignalTipView);
        mDtvPlayerEventListener = new DtvPlayerEventListener(this, mSignalTipView);
        mTvPlayerEventListener = new TvPlayerEventListener(this, handler);

        tvChannelManager.registerOnAtvPlayerEventListener(mAtvPlayerEventListener);
        tvChannelManager.registerOnDtvPlayerEventListener(mDtvPlayerEventListener);
        tvChannelManager.registerOnTvPlayerEventListener(mTvPlayerEventListener);

        mBroadcast = new HideSystemUIBroadcast();
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction("android.net.ethernet.ETHERNET_STATE_CHANGED");
        intentFilter.addAction("android.net.wifi.STATE_CHANGE");
        intentFilter.addAction("android.net.pppoe.PPPOE_STATE_ACTION");
        registerReceiver(mBroadcast, intentFilter);

        IntentFilter filter = new IntentFilter();
        filter.addAction(Intent.ACTION_MEDIA_MOUNTED);
        filter.addAction(Intent.ACTION_MEDIA_EJECT);
        filter.addDataScheme("file");
        registerReceiver(mBroadcast, filter);

        UNSUPPORT_TIP = getResources().getString(R.string.unsupporttips);

        Log.i(TAG, "LTvPlay,Version:1.0.1,Date:2014-07-31,Publisher:Fenoss,REV:15206");
    }

    public Handler getPlayerActivityHandler() {
        return handler;
    }

    /**
     * @return internal sdcard size(M)
     */
    private float getAvailableInternalSDCardSize() {
        try {
            if (!Environment.getExternalStorageState().equals(Environment.MEDIA_MOUNTED)) {
                return 0f;
            }
            File path = Environment.getExternalStorageDirectory();
            StatFs statfs = new StatFs(path.getPath());
            return statfs.getAvailableBlocks() * (statfs.getBlockSize() / (1024f * 1024f));
        } catch (Exception e) {
            Log.w(TAG, "read intenal sdcard error");
            return 0f;
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        isRestChannels = intent.getBooleanExtra("isRestChannels", false);
        Log.i(TAG, "PlayerActivity onNewIntent. Need to reset channels:" + isRestChannels);
        super.onNewIntent(intent);
    }

    /**
     * show info view when mode change
     */
    public void showInfoView() {
        if (mOtherSourceInfoView != null) {
            mOtherSourceInfoView.showOthersInfo(mCurInputSource);
        }
    }

    private void startTVMenu() {
        Intent menuIntent = new Intent();
        menuIntent.setClassName(SETUPPACKAGENAME, SETUPCLASSNAME);
        startActivity(menuIntent);
    }

    @Override
    protected void onResume() {
        Log.i(TAG, "PlayerActivity onResume.");
        if ("1".equals(SystemProperties.get("mstar.videoadvert.finished", "0"))) {
            handleTVData();
        }
        super.onResume();

    }

    private void handleTVData() {
        SciflyStatistics.getInstance(getApplicationContext()).recordPageStart(this, "PlayerActivity");
        mTvPlayerEventListener.fleshInputSource();
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_LOW_PROFILE);

        int source = TvCommonManager.getInstance().getCurrentTvInputSource();
        // If current source is storage, we should change the source to show TV.
        if (source == TvCommonManager.INPUT_SOURCE_STORAGE) {
            source = mChannelManagerExt.queryCurInputSrc(this);
            TvCommonManager.getInstance().setInputSource(source);
            if (source == TvCommonManager.INPUT_SOURCE_ATV || source == TvCommonManager.INPUT_SOURCE_DTV) {
                ProgramInfo info = mChannelManagerExt.getCurProgramInfo();
                mChannelManagerExt.programSelect(info.number, info.serviceType);
            }
        }

        TvManager mManager = TvManager.getInstance();
        if (mManager != null) {
            mManager.getTimerManager().setOnTimerEventListener(TimerEventListener.getInstance(this));
        }

        // 1 stand for launcher, 2 stand for tvplayer.
        System.putInt(getContentResolver(), "tvapp", 2);
        tvView.setBackgroundColor(Color.TRANSPARENT);
        if (mCurInputSource != source) {
            mCurInputSource = source;
            isRestChannels = true;
        }
        if ((mCurInputSource == TvCommonManager.INPUT_SOURCE_ATV
                || mCurInputSource == TvCommonManager.INPUT_SOURCE_DTV)) {
            // If we need reset channels or start tvplayer, we get all channels.
            if (isFirstInPlayer || isRestChannels) {
                mChannelManagerExt.getAllChannels(this, mCurInputSource);
                if (mChannelInfoView == null) {
                    mChannelInfoView = new ChannelInfoView(this, mChannelManagerExt.getCurInfo());
                    parent.addView(mChannelInfoView);
                }
                new resetZoomModeThread(source).start();
                isFirstInPlayer = false;
            }
            // We need to select program,otherwise TV is black.
            if (isRestChannels && mChannelManagerExt.getChannels().size() > 0) {
                ProgramInfo info = mChannelManagerExt.getCurInfo();
                Log.i(TAG, "program select, " + info.number);
                if (info.number >= 0 && info.number < 999) {
                    mChannelManagerExt.programSel(info.number, info.serviceType);
                    mChannelInfoView.channelChange(info);
                }
            } else if ((isFirstInPlayer || isRestChannels) && mChannelManagerExt.getCurInfo() != null) {
                mChannelManagerExt.programSel(mChannelManagerExt.getCurInfo().number,
                        mChannelManagerExt.getCurInfo().serviceType);
            }
        } else {
            // To show source information such as HDMI1, HDMI2, VGA,
            // YPBPR,SVIDEO.
            if (mOtherSourceInfoView == null) {
                mOtherSourceInfoView = new OtherSourceInfoView(this, mCurInputSource);
                parent.addView(mOtherSourceInfoView);
            } else {
                mOtherSourceInfoView.showOthersInfo(mCurInputSource);
            }
        }
        handler.sendEmptyMessageDelayed(SIGNAL_TUNING, 200);
        isRestChannels = false;
        handler.sendEmptyMessageDelayed(TIME, DELAYTIME);
        handler.sendEmptyMessage(TV_LISTENER_READY);

        toggleInfoView();       
       
        Log.d(TAG, "------has msg :" + handler.hasMessages(PROTECT_REMIND_TIPS));
        if (Settings.System.getInt(PlayerActivity.this.getContentResolver(), "protect_remind", 0) == 1
                && !handler.hasMessages(PROTECT_REMIND_TIPS)) {
            Log.d(TAG, "protect_remind function is open in resume");
            handler.removeMessages(PROTECT_REMIND_TIPS);
            handler.sendEmptyMessageDelayed(PROTECT_REMIND_TIPS, REMIND_DELAYTIME);
        }

    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            if (mProtectTipView.isShow()) {
                Log.d(TAG, "protect tips is show");
                mProtectTipView.dismissTipView();
                if (Settings.System.getInt(PlayerActivity.this.getContentResolver(), "protect_remind", 0) == 1) {
                    Log.d(TAG, "protect_remind function is open in dispatchKeyEvent");
                    handler.removeMessages(PROTECT_REMIND_TIPS);
                    handler.sendEmptyMessageDelayed(PROTECT_REMIND_TIPS, REMIND_DELAYTIME);
                }
            }
        }
        return super.dispatchKeyEvent(event);
    }
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.e(TAG, "keyCode:" + keyCode + " KeyEvent:" + event.getAction());
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
                if (mCurInputSource == TvCommonManager.INPUT_SOURCE_ATV
                        || mCurInputSource == TvCommonManager.INPUT_SOURCE_DTV) {
                    if (mSelectChannelNum.length() > 0
                            && Integer.valueOf(String.valueOf(mSelectChannelNum.charAt(0))) == 0) {
                        mSelectChannelNum.deleteCharAt(0);
                    }
                    if (mSelectChannelNum.length() == 3) {
                        mSelectChannelNum = new StringBuffer();
                    }
                    mSelectChannelNum.append(String.valueOf(keyCode - KeyEvent.KEYCODE_0));
                    mChannelInfoView.selectChannel(mSelectChannelNum.toString());
                    mSelectChannelHandler.removeMessages(SELECTCHANNEL);
                    if ((mCurInputSource == TvCommonManager.INPUT_SOURCE_ATV && mSelectChannelNum.length() == 2
                            && Integer.valueOf(mSelectChannelNum.toString()) > 12) || mSelectChannelNum.length() == 3) {
                        Message msg = mSelectChannelHandler.obtainMessage();
                        msg.what = SELECTCHANNEL;
                        msg.arg1 = Integer.valueOf(mSelectChannelNum.toString());
                        mSelectChannelHandler.sendMessage(msg);
                    } else {
                        Message msg = mSelectChannelHandler.obtainMessage();
                        msg.what = SELECTCHANNEL;
                        msg.arg1 = Integer.valueOf(mSelectChannelNum.toString());
                        mSelectChannelHandler.sendMessageDelayed(msg, DELAYSELECTCHANNEL);
                    }
                }
                break;
            case KeyEvent.KEYCODE_SLEEP:
                Log.v("TVPlayer", "=============================\n==========================");
                if (flg) {
                    TvPictureManager.getInstance().freezeImage();
                    flg = false;
                } else {
                    TvPictureManager.getInstance().unFreezeImage();
                    flg = true;
                }
                return true;
            case KeyEvent.KEYCODE_DPAD_LEFT:
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                // to change the volume.
                AudioManager audiomanager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
                if (audiomanager != null) {
                    int flags = AudioManager.FLAG_SHOW_UI | AudioManager.FLAG_VIBRATE;
                    audiomanager.adjustVolume(keyCode == KeyEvent.KEYCODE_DPAD_RIGHT ? AudioManager.ADJUST_RAISE
                            : AudioManager.ADJUST_LOWER, flags);
                }
                break;
            case KeyEvent.KEYCODE_CHANNEL_UP:
            case KeyEvent.KEYCODE_DPAD_UP:
                tvChannelChange(KEY_UP);
                break;
            case KeyEvent.KEYCODE_CHANNEL_DOWN:
            case KeyEvent.KEYCODE_DPAD_DOWN:
                tvChannelChange(KEY_DOWN);
                break;
            case KeyEvent.KEYCODE_LAST_CHANNEL:
                tvChannelChange(KEY_RETURN);
                break;
            case KeyEvent.KEYCODE_MENU: {
                if (isFirst) {
                    handler.sendEmptyMessageDelayed(STARTTVMENU, DELAYTIME);
                    isFirst = false;
                } else {
                    startTVMenu();
                }
                break;
            }
            case MKeyEvent.KEYCODE_TV_EPG: {
                if (mCurInputSource == TvCommonManager.INPUT_SOURCE_DTV
                        && mChannelManagerExt.getChannels().size() > 0) {
                    Intent epgIntent = new Intent();
                    epgIntent.setClass(this, EpgActivity.class);
                    startActivity(epgIntent);
                }
                break;
            }
            case MKeyEvent.KEYCODE_TV_MTS: {
                if ((mCurInputSource == TvCommonManager.INPUT_SOURCE_DTV
                        || mCurInputSource == TvCommonManager.INPUT_SOURCE_ATV)
                        && mChannelManagerExt.getChannels().size() > 0) {
                    if (mMtsView == null) {
                        mMtsView = new MTSView(this, mCurInputSource);
                        parent.addView(mMtsView);
                        return true;
                    }
                    if (mMtsView.isShow()) {
                        mMtsView.changeMtsInfo(mCurInputSource);
                    } else if (!mMtsView.isShow()) {
                        mMtsView.getMtsInfo(mCurInputSource);
                    }
                }
                break;
            }
            case KeyEvent.KEYCODE_ENTER:
            case KeyEvent.KEYCODE_DPAD_CENTER: {
                if (mCurInputSource == TvCommonManager.INPUT_SOURCE_DTV
                        && mChannelManagerExt.getChannels().size() > 0) {
                    if (mSignalTipView.getText().equals(getResources().getString(R.string.passwordtip))) {
                        // to show password dialog.
                        new PasswordCheckDialog(this).show();
                    } else {
                        // to show channel list.
                        Intent channelListIntent = new Intent();
                        channelListIntent.setClass(this, ChannelListActivity.class);
                        startActivity(channelListIntent);
                    }
                } else if ((mCurInputSource == TvCommonManager.INPUT_SOURCE_ATV
                        || mCurInputSource == TvCommonManager.INPUT_SOURCE_DTV)
                        && mChannelManagerExt.getChannels().size() <= 0) {
                    if (mSignalTipView.getText().equals(getResources().getString(R.string.tuningtip))) {
                        // to tuning.
                        if (mCurInputSource == TvCommonManager.INPUT_SOURCE_ATV) {
                            mSignalTipView.setText("");
                            Intent atv = new Intent();
                            atv.setClassName(SETUPPACKAGENAME, AVTTUNING);
                            startActivity(atv);
                        } else if (mCurInputSource == TvCommonManager.INPUT_SOURCE_DTV) {
                            Intent dtv = new Intent();
                            dtv.setClassName(SETUPPACKAGENAME, DVTTUNING);
                            startActivity(dtv);
                        }
                    }
                }
                break;
            }
            case KeyEvent.KEYCODE_INFO: {
                toggleInfoView();
                if (mCurInputSource == TvCommonManager.INPUT_SOURCE_DTV
                        && mChannelManagerExt.getCurProgramInfo() != null) {
                    mChannelInfoView.channelChange(mChannelManagerExt.getCurProgramInfo());
                } else if (mCurInputSource == TvCommonManager.INPUT_SOURCE_ATV
                        && mChannelManagerExt.getCurInfo() != null) {
                    mChannelInfoView.channelChange(mChannelManagerExt.getCurInfo());
                } else {
                    mOtherSourceInfoView.showOthersInfo(mCurInputSource);
                }
                break;
            }
            case KeyEvent.KEYCODE_DVR:
                // if (mCurInputSource == TvCommonManager.INPUT_SOURCE_DTV
                // && mChannelManagerExt.getChannels().size() > 0
                // && mChannelManagerExt.getCurInfo().serviceType ==
                // EnumServiceType.E_SERVICETYPE_DTV.ordinal()) {
                // Intent intent = new Intent(this, PVRActivity.class);
                // intent.putExtra("PVR_ONE_TOUCH_MODE", 1);
                // startActivity(intent);
                // }
                break;
            case KeyEvent.KEYCODE_MEDIA_PLAY:
                // if (mCurInputSource == TvCommonManager.INPUT_SOURCE_DTV
                // && mChannelManagerExt.getChannels().size() > 0
                // && mChannelManagerExt.getCurInfo().serviceType ==
                // EnumServiceType.E_SERVICETYPE_DTV.ordinal()) {
                // Intent intent = new Intent(this, PVRActivity.class);
                // intent.putExtra("PVR_ONE_TOUCH_MODE", 3);
                // startActivity(intent);
                // }
                break;
            case MKeyEvent.KEYCODE_TV_INDEX:
                // if (mCurInputSource == TvCommonManager.INPUT_SOURCE_DTV) {
                // Intent intent = new Intent(this,
                // PVRFullPageBrowserActivity.class);
                // startActivity(intent);
                // }
                break;
            case MKeyEvent.KEYCODE_TV_LIST:
                if (mCurInputSource == TvCommonManager.INPUT_SOURCE_DTV) {
                    if (mChannelManagerExt.getFavoriteChannels().size() > 0) {
                        Intent favListIntent = new Intent();
                        favListIntent.setClass(this, FavoriteChannelsActivity.class);
                        startActivity(favListIntent);
                    } else if (mChannelManagerExt.getChannelsAll().size() > 0) {
                        Toast.makeText(this, R.string.favoritetip, Toast.LENGTH_LONG).show();
                        Intent intent = new Intent("com.eostek.tv.player.channellistedit");
                        startActivity(intent);
                    }
                }
                break;
            case MKeyEvent.KEYCODE_TV_FREEZE:
                try {
                    int source = getHotKeySource(Settings.System.getInt(getContentResolver(), "hotkey1"));
                    if (!(source == TvCommonManager.INPUT_SOURCE_NONE || source == mCurInputSource)) {
                        TvCommonManager.getInstance().setInputSource(source);
                        onResume();
                    } else if (source == TvCommonManager.INPUT_SOURCE_NONE) {
                        Intent menuIntent = new Intent();
                        menuIntent.setClassName(SETUPPACKAGENAME, SETUPCLASSNAME);
                        menuIntent.putExtra("gotoadvance", true);
                        startActivity(menuIntent);
                    }
                } catch (SettingNotFoundException e) {
                    e.printStackTrace();
                }
                break;
            case KeyEvent.KEYCODE_TV_TELETEXT:
                try {
                    int source = getHotKeySource(Settings.System.getInt(getContentResolver(), "hotkey2"));
                    if (!(source == TvCommonManager.INPUT_SOURCE_NONE || source == mCurInputSource)) {
                        TvCommonManager.getInstance().setInputSource(source);
                        onResume();
                    } else if (source == TvCommonManager.INPUT_SOURCE_NONE) {
                        Intent menuIntent = new Intent();
                        menuIntent.setClassName(SETUPPACKAGENAME, SETUPCLASSNAME);
                        menuIntent.putExtra("gotoadvance", true);
                        startActivity(menuIntent);
                    }
                } catch (SettingNotFoundException e) {
                    e.printStackTrace();
                }
                break;
            // AD key invoke
            case MKeyEvent.KEYCODE_TV_CC:
                List<AdInfo> adinfos = null;
                switch (mCurInputSource) {
                    case TvCommonManager.INPUT_SOURCE_ATV:
                    case TvCommonManager.INPUT_SOURCE_DTV:
                        adinfos = TvDBManager.getInstance(this).getCurrentAdInfo(mCurInputSource,
                                mChannelManagerExt.getCurInfo());
                        break;
                    case TvCommonManager.INPUT_SOURCE_HDMI:
                    case TvCommonManager.INPUT_SOURCE_HDMI4:
                    case TvCommonManager.INPUT_SOURCE_CVBS:
                    case TvCommonManager.INPUT_SOURCE_YPBPR:
                    case TvCommonManager.INPUT_SOURCE_VGA:
                        adinfos = TvDBManager.getInstance(this).getCurrentAdInfo(mCurInputSource, null);
                        break;
                    default:
                        break;
                }
                if (adinfos != null) {
                    if (adinfos.size() > 0) {
                        // all the queried element in adinfos should have the
                        // same webview url, it's ok get the first one
                        AdInfo adinfo = adinfos.get(0);
                        Intent intent = new Intent(Intent.ACTION_MAIN);
                        intent.setClassName(Constants.LAUNCHER_PACKAGE, Constants.LAUNCHER_ACTIVITY_APPSTORE);
                        Bundle bundle = new Bundle();
                        bundle.putString("URL", adinfo.getWebview_url());
                        bundle.putBoolean("tv_tag", true);
                        intent.putExtras(bundle);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);
                    }
                }
                break;
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    private void toggleInfoView() {
        if (mCurInputSource == TvCommonManager.INPUT_SOURCE_ATV) {
            if (mChannelManagerExt.getCurInfo() == null) {
                return;
            }
            if (mAtvInfoControlView == null) {
                mAtvInfoControlView = new AtvInfoControlView(this, mChannelManagerExt.getCurInfo());
                parent.addView(mAtvInfoControlView);
            } else if (mAtvInfoControlView.isShow()) {
                mAtvInfoControlView.dismissView();
            } else {
                ProgramInfo mCurInfo = mChannelManagerExt.getCurProgramInfo();
                mCurInfo.number = mCurInfo.number + 1;
                mChannelManagerExt.setCurInfo(mCurInfo);
                mAtvInfoControlView.showAtvChannelInfo(mChannelManagerExt.getCurInfo());
            }
        } else if (mCurInputSource == TvCommonManager.INPUT_SOURCE_DTV) {
            if (mChannelManagerExt.getCurInfo() == null) {
                return;
            }
            if (mDtvInfoControlView == null) {
                mDtvInfoControlView = new DtvInfoControlView(this, mChannelManagerExt.getCurInfo());
                parent.addView(mDtvInfoControlView);
            } else if (mDtvInfoControlView.isShow()) {
                mDtvInfoControlView.dismissView();
            } else {
                mDtvInfoControlView.showDtvChannelInfo(mChannelManagerExt.getCurInfo());
            }
        } else {
            if (mOtherInfoControlView == null) {
                mOtherInfoControlView = new OtherInfoControlView(this, mCurInputSource);
                parent.addView(mOtherInfoControlView);
            } else if (mOtherInfoControlView.isShow()) {
                mOtherInfoControlView.dismissView();
            } else {
                mOtherInfoControlView.showOtherADInfo(mCurInputSource);
            }
        }
    }

    private void toggleInfoViews() {
        if (mCurInputSource == TvCommonManager.INPUT_SOURCE_ATV) {
            if (mChannelManagerExt.getCurInfo() == null) {
                return;
            }
            if (mAtvInfoControlView == null) {
                mAtvInfoControlView = new AtvInfoControlView(this, mChannelManagerExt.getCurInfo());
                parent.addView(mAtvInfoControlView);
            } else {
                mAtvInfoControlView.showAtvChannelInfo(mChannelManagerExt.getCurInfo());
            }
        } else if (mCurInputSource == TvCommonManager.INPUT_SOURCE_DTV) {
            if (mChannelManagerExt.getCurInfo() == null) {
                return;
            }
            if (mDtvInfoControlView == null) {
                mDtvInfoControlView = new DtvInfoControlView(this, mChannelManagerExt.getCurInfo());
                parent.addView(mDtvInfoControlView);
            } else {
                mDtvInfoControlView.showDtvChannelInfo(mChannelManagerExt.getCurInfo());
            }
        } else {
            if (mOtherInfoControlView == null) {
                mOtherInfoControlView = new OtherInfoControlView(this, mCurInputSource);
                parent.addView(mOtherInfoControlView);
            } else {
                mOtherInfoControlView.showOtherADInfo(mCurInputSource);
            }
        }
    }

    private void tvChannelChange(int key) {
        if (mCurInputSource == TvCommonManager.INPUT_SOURCE_ATV
                || mCurInputSource == TvCommonManager.INPUT_SOURCE_DTV) {
            switch (key) {
                case KEY_UP:
                    mChannelManagerExt.channelUp();
                    break;
                case KEY_DOWN:
                    mChannelManagerExt.channelDown();
                    break;
                case KEY_RETURN:
                    mChannelManagerExt.channelReturn();
                    break;
                default:
            }
            if (mChannelManagerExt.getCurInfo() != null) {
                mSelectChannelHandler.sendEmptyMessageDelayed(SHOW_CHANNEL_INFO, 600);
                // data uploading. add by vicent
                if (mCurInputSource == TvCommonManager.INPUT_SOURCE_ATV) {
                    Map<String, String> map = new HashMap<String, String>();
                    int index = mChannelManagerExt.getCurInfo().number;
                    map.put("channelNum", index + "");
                    map.put("channelName", mChannelManagerExt.getCurProgramInfo().serviceName);
                    SciflyStatistics.getInstance(getApplicationContext()).recordEvent(PlayerActivity.this,
                            "atvWatchRecord", map);
                    SciflyStatistics.getInstance(getApplicationContext()).recordEvent(PlayerActivity.this,
                            "atvChangeChannelRecord", "channelNum", index + "");
                } else if (mCurInputSource == TvCommonManager.INPUT_SOURCE_DTV) {
                    Map<String, String> map = new HashMap<String, String>();
                    int index = mChannelManagerExt.getCurInfo().number;
                    map.put("channelNum", index + "");
                    map.put("channelName", mChannelManagerExt.getCurProgramInfo().serviceName);
                    SciflyStatistics.getInstance(getApplicationContext()).recordEvent(PlayerActivity.this,
                            "dtvWatchRecord", map);
                    SciflyStatistics.getInstance(getApplicationContext()).recordEvent(PlayerActivity.this,
                            "dtvChangeChannelRecord", "channelNum", index + "");
                }
            }
        }
    }

    @Override
    protected void onPause() {
        super.onPause();
        SciflyStatistics.getInstance(getApplicationContext()).recordPageEnd();
        if (mOtherInfoControlView != null) {
            mOtherInfoControlView.dismissView();
        }
        if (mDtvInfoControlView != null) {
            mDtvInfoControlView.dismissView();
        }
        if (mAtvInfoControlView != null) {
            mAtvInfoControlView.dismissView();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        if (mDtvInfoControlView != null && mDtvInfoControlView.isShow()) {
            mDtvInfoControlView.dismissView();
            return;
        }
        // new ExitDialog(this).show();
        Intent mHomeIntent = new Intent(Intent.ACTION_MAIN, null);
        mHomeIntent.addCategory(Intent.CATEGORY_HOME);
        mHomeIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        startActivity(mHomeIntent);
    }

    @Override
    protected void onStop() {
        Log.d(TAG, "in onStop ");
        Log.e(TAG, "onStop. is the language changed: " + isLanguageChange);
        Log.e(TAG, "onStop. getCurrentActivity " + UtilsTools.getCurrentActivity(this));
        // change the source to storage,not include launcher status
        boolean isNotTv = !UtilsTools.getCurrentActivity(this).equals(UtilsTools.getLauncherActivityName((this)))
                && !UtilsTools.getCurrentActivity(this).equals("com.eostek.tv.player.PlayerActivity")
                && !UtilsTools.getCurrentActivity(this).equals("com.mstar.tv.menu.ui.EosCustomSettingActivity");
        if (isNotTv) {
            TvCommonManager.getInstance().setInputSource(TvCommonManager.INPUT_SOURCE_STORAGE);
        }
        if (!isLanguageChange) {
            finish();
        }
        isLanguageChange = false;
        super.onStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mBroadcast != null) {
            unregisterReceiver(mBroadcast);
        }
        if (tvChannelManager != null) {
            tvChannelManager.unregisterOnAtvPlayerEventListener(mAtvPlayerEventListener);
            mAtvPlayerEventListener = null;
            tvChannelManager.unregisterOnDtvPlayerEventListener(mDtvPlayerEventListener);
            mDtvPlayerEventListener = null;
            tvChannelManager.unregisterOnTvPlayerEventListener(mTvPlayerEventListener);
            mTvPlayerEventListener = null;

        }
    }

    public void channelChange() {
        mChannelInfoView.channelChange(mChannelManagerExt.getCurInfo());
    }
    
    private int tvHasTuner(int hotkeyValue){
        int source = TvCommonManager.INPUT_SOURCE_NONE;
        switch (hotkeyValue) {
            case 1:
                source = TvCommonManager.INPUT_SOURCE_ATV;
                break;
            case 2:
                try {
                    mTvAudioManager.enableMute(EnumMuteType.E_MUTE_PERMANENT);
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
    
    private int tvHasNoTuner(int hotkeyValue){
        int source = TvCommonManager.INPUT_SOURCE_NONE;
        switch (hotkeyValue) {
            case 1:
                source = TvCommonManager.INPUT_SOURCE_HDMI;
                break;
            case 2:
                source = TvCommonManager.INPUT_SOURCE_HDMI2;
                break;
            case 3:
                source = TvCommonManager.INPUT_SOURCE_HDMI3;
                break;
            case 4:
                source = TvCommonManager.INPUT_SOURCE_CVBS;
                break;
            case 5:
                source = TvCommonManager.INPUT_SOURCE_YPBPR;
                break;
            case 6:
                source = TvCommonManager.INPUT_SOURCE_VGA;
                break;
            default:
                break;
        }
        return source;
    }
    
    private int getHotKeySource(int hotkeyValue) {
        if (hasTuner) {
            return tvHasTuner(hotkeyValue);
        }
        return tvHasNoTuner(hotkeyValue);
    }

    class HideSystemUIBroadcast extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            String action = intent.getAction();
            Log.v(TAG, "HideSystemUIBroadcast action = " + action);
            if ("android.net.ethernet.ETHERNET_STATE_CHANGED".equals(action)
                    || "android.net.wifi.STATE_CHANGE".equals(action)
                    || "android.net.pppoe.PPPOE_STATE_ACTION".equals(action)
                    || action.equals(Intent.ACTION_MEDIA_MOUNTED) || action.equals(Intent.ACTION_MEDIA_EJECT)) {
                mSystemUIHandler.removeMessages(0);
                mSystemUIHandler.sendEmptyMessageDelayed(0, 2 * 1000);
            }

        }

    }

    Handler handlertv = new Handler();

    Runnable mute_thread = new Runnable() {
        @Override
        public void run() {
            try {
                if (mTvAudioManager != null) {
                    mTvAudioManager.disableMute(EnumMuteType.E_MUTE_PERMANENT);
                }
            } catch (TvCommonException e) {
                e.printStackTrace();
            }
            handlertv.removeCallbacks(mute_thread);
        }
    };

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // TODO Auto-generated method stub
        super.onConfigurationChanged(newConfig);
    }
    
    private void resetZoomMode(int curSourceType) {
        if (curSourceType >= TvCommonManager.INPUT_SOURCE_HDMI
                && curSourceType < TvCommonManager.INPUT_SOURCE_HDMI_MAX) {
            // change hdmi full value in tv menu's picture setting.
            int hdmiFull = Settings.System.getInt(getContentResolver(), "hdmiunderscan", 0);
            if (hdmiFull == 2 || hdmiFull == 0) {
                if (TvChannelManager.getInstance().isSignalStabled()) {
                    if (TvManager.getInstance().getPlayerManager().isHdmiMode()) {
                        if (TvPictureManager.getInstance().getVideoArc() != EnumVideoArcType.E_16x9) {
                            // only when hdmiFull = 0,cut 20
                            if (hdmiFull == 0) {
                                TvPictureManager.getInstance().setVideoArc(EnumVideoArcType.E_16x9);
                                FactoryDeskImpl.getInstance(this).setOverScan((short) 20, (short) 20, (short) 20,
                                        (short) 20);
                            }
                        }
                    } else {
                        FactoryDeskImpl.getInstance(this).setOverScan((short) 0, (short) 0, (short) 0, (short) 0);
                    }
                }
            }
        }
    }
    class resetZoomModeThread extends Thread{
        private int mInputSource; 
        public resetZoomModeThread(int curSourceType) {
            mInputSource = curSourceType;
        }
        @Override
        public void run() {
            switch (mInputSource) {
                case TvCommonManager.INPUT_SOURCE_HDMI:
                case TvCommonManager.INPUT_SOURCE_HDMI2:
                case TvCommonManager.INPUT_SOURCE_HDMI3:
                case TvCommonManager.INPUT_SOURCE_HDMI4: 
                case TvCommonManager.INPUT_SOURCE_HDMI_MAX: 
                    resetZoomMode(mInputSource);
                    break;
                default:
                    break;
            }
           
            super.run();
        }
    }
}
