
package com.eostek.tv;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.media.AudioManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.provider.Settings.System;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

import com.eostek.tv.channel.ChannelListActivity;
import com.eostek.tv.channel.ChannelListEditActivity;
import com.eostek.tv.channel.FavoriteChannelsActivity;
import com.eostek.tv.epg.EpgActivity;
import com.eostek.tv.pvr.PVRActivity;
import com.eostek.tv.pvr.PVRFilePreviewActivity;
import com.eostek.tv.utils.ChannelManagerExt;
import com.eostek.tv.utils.Constants;
import com.eostek.tv.utils.LogUtil;
import com.eostek.tv.utils.TVUtils;
import com.eostek.tv.utils.UtilsTools;
import com.eostek.tv.widget.PasswordCheckDialog;
import com.mstar.android.MKeyEvent;
import com.mstar.android.tv.TvAudioManager;
import com.mstar.android.tv.TvChannelManager;
import com.mstar.android.tv.TvCommonManager;
import com.mstar.android.tv.TvPictureManager;
import com.mstar.android.tvapi.common.TimerManager;
import com.mstar.android.tvapi.common.TvManager;
import com.mstar.android.tvapi.common.exception.TvCommonException;
import com.mstar.android.tvapi.common.vo.EnumServiceType;
import com.mstar.android.tvapi.common.vo.ProgramInfo;

/**
 * To show TV and control it, such as channel change(up/down/return), EPG, MTS,
 * Info, Volume up/down, TV Menu, PVR(record/pause/play/index) and so on.
 * projectName： Tv_2.13 moduleName： PlayerActivity.java
 * 
 * @author lucky.li
 * @version 1.0.0
 * @time 2015-2-14 上午11:09:43
 * @Copyright © 2012 MStar Semiconductor, Inc.
 */
@SuppressLint("HandlerLeak")
public class PlayerActivity extends Activity {

    private PlayerHolder mHolder;

    /**
     * Current input source.
     */
    private int mCurInputSource = TvCommonManager.INPUT_SOURCE_NONE;

    private ChannelManagerExt mChannelManagerExt;

    private TvChannelManager tvChannelManager = TvChannelManager.getInstance();

    private TvAudioManager mTvAudioManager = TvAudioManager.getInstance();

    private int surroundModeVal = 0;

    /**
     * Need reset channel or not,the first time should reset channels
     */
    private boolean isRestChannels = true;

    public static Boolean isLanguageChange = false;

    private static boolean mSleepFlg = true;

    private StringBuffer mSelectChannelNum = new StringBuffer("");

    private static final int KEY_UP = 1;

    private static final int KEY_DOWN = 2;

    private static final int KEY_RETURN = 3;

    public static final int PASSWORDTIPDISMISS = 0x04;

    private static final int SELECTCHANNEL = 0x05;

    private static final int DELAYSELECTCHANNEL = 3000;

    private static final int STARTPVR = 0x06;

    private static final String TV_AUTO_TIME = "tv_auto_time";

    private static final String AUTO_TIME = "auto_time";

    private static final String MEDIABROSWER = "com.hrtvbic.usb.S6A918.MainActivity";

    // 顶部广告是否显示
    private static boolean isShowingAdView = false;

    private Message msgChnanlNum = null;

    private static final int KEYCODE_TVNUM_ENTER = 321;

    private static final int KEYCODE_BILINGUAL = 266;

    // private static final int DELAY_START_PVRF_ROM_BOOT = 2000;

    @SuppressLint("HandlerLeak")
    private Handler mHandler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case PASSWORDTIPDISMISS:
                    mHolder.setSignalText("");
                    break;
                case Constants.STANDBY:
                    removeMessages(Constants.STANDBY);
                    // Laird modify ,mix Mantis 0033462
                    // if
                    // (!UtilsTools.getCurTopActivityName(PlayerActivity.this).equals(PlayerActivity.this))
                    // {
                    // return;
                    // }
                    if (UtilsTools.isSaveModeOpen(PlayerActivity.this)
                            && !TvChannelManager.getInstance().isSignalStabled()) {
                        Intent intent = new Intent(Constants.START_COUNTERDOWN);
                        intent.putExtra(Constants.COUNT_DOWN, Constants.SHOWCOUTDOWN);
                        PlayerActivity.this.startActivity(intent);
                    }
                    break;
                case SELECTCHANNEL:
                    int index = mChannelManagerExt.getChannelNums().indexOf(msg.arg1);
                    mSelectChannelNum = new StringBuffer();
                    if (index != -1) {
                        mChannelManagerExt.programNumSel(index);
                        mHolder.showSelectChannelInfo(mChannelManagerExt.getCurInfo());
                        mHolder.toggleInfoView(mChannelManagerExt.getCurInfo(), mCurInputSource);
                    }
                    break;
                case STARTPVR:
                    Intent intent = new Intent(PlayerActivity.this, PVRActivity.class);
                    intent.putExtra(Constants.PVR_ONE_TOUCH_MODE, Constants.PVR_GENERAL_FLAG);
                    startActivity(intent);
                    break;
                case Constants.SHOWINFO:
                    showInfoView();
                    break;
                case Constants.MSG_ATV_SIGNAL_UNLOCK:
                    if (ChannelManagerExt.getInstance().getChannels().size() <= 0) {
                        mHolder.setSignalText(PlayerActivity.this.getResources().getString(R.string.tuningtip));
                    }
                    break;
                case Constants.MSG_DTV_SIGNAL_UNLOCK:
                    if (ChannelManagerExt.getInstance().getChannels().size() <= 0) {
                        mHolder.setSignalText(PlayerActivity.this.getResources().getString(R.string.tuningtip));
                    } else {
                        mHolder.setSignalText((String) msg.obj);
                    }
                    break;
                case Constants.MSG_OTHER_SIGNAL_UNLOCK:
                    mHolder.setNosignalTips(msg.arg1);
                    break;
                case Constants.MSG_DTV_SIGNAL_LOCK:
                    syncDtvTime();
                case Constants.MSG_ATV_SIGNAL_LOCK:
                case Constants.MSG_OTHER_SIGNAL_LOCK:
                    mHolder.dismissSignalView();
                    break;
                case Constants.MSG_UPDATE_SCREEN_SAVER_TEXT:
                    mHolder.setSignalText((String) msg.obj);
                    break;
                case Constants.MSG_UPDATE_SCREEN_SAVER_UI:
                    // show info view when mode change
                    if (mHolder.getmSignalTipView().isShow()) {
                        mHolder.getmSignalTipView().dismiss();
                    } else {
                        showInfoView();
                    }
                    break;
                default:
                    break;
            }
        }
    };

    
    private void syncDtvTime() {
        try {
            LogUtil.i("start TimeSync with DTV");
            TvManager.getInstance().getTimerManager().setLinuxTimeSource(TimerManager.LINUX_TIMESOURCE_DTV);
        } catch (TvCommonException e) {
            LogUtil.i("TimeSyncReceiver set time source fail !!!!");
        }
    }
    
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        // TV_AUTO_TIME 1 stands system time equals tv time.
        Settings.Global.putInt(getContentResolver(), TV_AUTO_TIME, 1);
        Settings.Global.putInt(getContentResolver(), AUTO_TIME, 0);
        mChannelManagerExt = ChannelManagerExt.getInstance();
        surroundModeVal = mTvAudioManager.getAudioSurroundMode();
        mHolder = new PlayerHolder(this, tvChannelManager, mHandler);
        mHolder.registerListener();
    }

    @Override
    protected void onResume() {
        super.onResume();
        LogUtil.i("mCurInputSource = " + mCurInputSource);
        // STR,此处不是很明白
        Settings.System.putInt(getContentResolver(), "tvplayer_str_status", 1);
        updateTVInfomation();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mHolder.dismissAtvInfoView();
        mHolder.dismissDtvInfoView();
        mHolder.dismissOtherInfoView();
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        mHolder.dismissDtvInfoView();
    }

    @Override
    protected void onStop() {
        super.onStop();
        LogUtil.i("onStop. is the language changed: " + isLanguageChange);
        // 难道只进入多媒体的时候才切Storage,退出TV时应该切到Storage下面
        if (UtilsTools.getCurTopActivityName(this).equals(MEDIABROSWER)) {
            int source = TvCommonManager.getInstance().getCurrentTvInputSource();
            TVUtils.setInputSource(source);
            TVUtils.setInputSource(TvCommonManager.INPUT_SOURCE_STORAGE);
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Settings.Global.putInt(getContentResolver(), TV_AUTO_TIME, 0);
        Settings.Global.putInt(getContentResolver(), AUTO_TIME, 1);
        mHolder.unRegisterListener();
        if (!mSleepFlg) {
            TvPictureManager.getInstance().unFreezeImage();
            mSleepFlg = true;
        }
    }

    /**
     * select the current channel and show the channel info or nosignal tips
     */
    private void updateTVInfomation() {
        // 每次进入TV放开声音
        // TVUtils.enableMute(false);
        // 获取当前Source
        int source;
        source = TvCommonManager.getInstance().getCurrentTvInputSource();
        LogUtil.i("source = " + source);
        // 进入TV后设置合适的Source
        if (source == TvCommonManager.INPUT_SOURCE_STORAGE) {
            source = mChannelManagerExt.queryCurInputSrc(this);
            TVUtils.setInputSource(source);
            if (TVUtils.isAtvOrDTV(source)) {
                ProgramInfo info = mChannelManagerExt.getCurProgramInfo();
                mChannelManagerExt.programSelect(info.number, info.serviceType);
            }
        }

        // 如果Source变化，则设置需要重新搜台标记为true
        if (mCurInputSource != source) {
            mCurInputSource = source;
            isRestChannels = true;
        }
        // 只有在ATV或DTV时才搜台
        if (TVUtils.isAtvOrDTV(source)) {
            System.putInt(getContentResolver(), Constants.SOURCE, source);
            // If we need reset channels or start tvplayer, we get all channels.
            if (isRestChannels) {
                mChannelManagerExt.getAllChannels(this, mCurInputSource);
                if (mChannelManagerExt.getChannels().size() > 0) {
                    // 切台
                    ProgramInfo info = mChannelManagerExt.getCurInfo();
                    if (info != null && info.number >= 0 && info.number < 999) {
                        mChannelManagerExt.programSel(info.number, info.serviceType);
                        mHolder.showSelectChannelInfo(info);
                    }
                } else if (mChannelManagerExt.getCurInfo() != null) {
                    // 疑问：没有台时，当前频道有不为空的情况？
                    mChannelManagerExt.programSel(mChannelManagerExt.getCurInfo().number,
                            mChannelManagerExt.getCurInfo().serviceType);
                }
            }
        } else {
            // To show source information such as HDMI1, HDMI2, VGA...
            mHandler.sendEmptyMessageDelayed(Constants.SHOWINFO, DELAYSELECTCHANNEL);
        }
        mHolder.toggleInfoView(mChannelManagerExt.getCurInfo(), mCurInputSource);

        if (TvChannelManager.getInstance().isSignalStabled()) {
            if(source == TvCommonManager.INPUT_SOURCE_DTV){
                syncDtvTime();
            }
            // 信号稳定则移除无信号提示
            mHolder.dismissSignalView();
        } else {
            // 如果待机打开，则发延时消息
            if (UtilsTools.isSaveModeOpen(PlayerActivity.this)) {
                mHandler.removeMessages(Constants.STANDBY);
                mHandler.sendEmptyMessageDelayed(Constants.STANDBY, Constants.AUTO_SLEEP_DELAY);
            }

            // 显示无信号提示
            if (TVUtils.isAtvOrDTV(source)) {
                if (mChannelManagerExt.getChannels().size() <= 0) {
                    mHolder.setSignalText(getResources().getString(R.string.tuningtip));
                } else {
                    mHolder.dismissSignalView();
                }
            } else {
                mHolder.setNosignalTips(mCurInputSource);
            }
        }
        
        // Notfiy event queue to start sending pending event
        try {
            TvCommonManager.getInstance().setTvosCommonCommand("TVEventListenerReady");
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * show info view when mode change
     */
    public void showInfoView() {
        mHolder.showOtherSourceInfoView(mCurInputSource, false);
    }

    /**
     * start Menu
     */
    private void startTVMenu() {
        System.putInt(getContentResolver(), "locale", 1);
        Intent menuIntent = new Intent();
        menuIntent.setClassName(Constants.SETUPPACKAGENAME, Constants.SETUPCLASSNAME);
        startActivity(menuIntent);
    }

    private void responseHotKeyForATVAndDTV() {
        int source = TvCommonManager.getInstance().getCurrentTvInputSource();
        if (source == TvCommonManager.INPUT_SOURCE_ATV) {
            TvCommonManager.getInstance().setInputSource(TvCommonManager.INPUT_SOURCE_DTV);
        } else if (source == TvCommonManager.INPUT_SOURCE_DTV) {
            TvCommonManager.getInstance().setInputSource(TvCommonManager.INPUT_SOURCE_ATV);
        } else {
            TvCommonManager.getInstance().setInputSource(
                    System.getInt(getContentResolver(), Constants.SOURCE, TvCommonManager.INPUT_SOURCE_ATV));
        }
        updateTVInfomation();
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        mCurInputSource = TvCommonManager.getInstance().getCurrentTvInputSource();
        LogUtil.i("keyCode:" + keyCode + " KeyEvent:" + event.getAction());
        LogUtil.i("keyCode:" + keyCode + " mCurInputSource" + mCurInputSource);
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
                responseNumCodeEvent(keyCode);
                break;
            case KEYCODE_TVNUM_ENTER:
                Log.d("laird", "responseNumEnterEvent");
                responseNumEnterEvent();
                break;
            case KEYCODE_BILINGUAL:
                Log.d("laird", "responseBilingualEvent");
                responseBilingualEvent();
                break;
            case KeyEvent.KEYCODE_SLEEP:
                LogUtil.i("TVPlayer", "=============================\n==========================");
                if (mSleepFlg) {
                    TvPictureManager.getInstance().freezeImage();
                    mSleepFlg = false;
                } else {
                    TvPictureManager.getInstance().unFreezeImage();
                    mSleepFlg = true;
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
            case MKeyEvent.KEYCODE_CHANNEL_RETURN:
                tvChannelChange(KEY_RETURN);
                break;
            case KeyEvent.KEYCODE_MENU: {
                startTVMenu();
                break;
            }
            case MKeyEvent.KEYCODE_MSTAR_PVR_BROWSER:
                responseHotKeyForATVAndDTV();
                break;
            case MKeyEvent.KEYCODE_TV_EPG: {
                if (mCurInputSource == TvCommonManager.INPUT_SOURCE_DTV && mChannelManagerExt.getChannels().size() > 0) {
                    Intent epgIntent = new Intent();
                    epgIntent.setClass(this, EpgActivity.class);
                    startActivity(epgIntent);
                } else {
                    Toast.makeText(this, R.string.noprogram_tip, Toast.LENGTH_LONG).show();
                }
                break;
            }
            case KeyEvent.KEYCODE_NUMPAD_ENTER:
            case KeyEvent.KEYCODE_DPAD_CENTER:
            case KeyEvent.KEYCODE_ENTER:
                responseEnterEvent();
                break;
            case KeyEvent.KEYCODE_INFO: {
                // 显示当前频道信息
                mHolder.toggleInfoView(mChannelManagerExt.getCurInfo(), mCurInputSource);
                if (TVUtils.isAtvOrDTV(mCurInputSource)) {
                    if (mChannelManagerExt.getCurProgramInfo() != null) {
                        mHolder.showSelectChannelInfo(mChannelManagerExt.getCurProgramInfo());
                    }
                } else {
                    mHolder.showOtherSourceInfoView(mCurInputSource, false);
                }
                break;
            }
            case KeyEvent.KEYCODE_DVR:
            case KeyEvent.KEYCODE_MEDIA_PAUSE:
                 if (mCurInputSource == TvCommonManager.INPUT_SOURCE_DTV &&
                 mChannelManagerExt.getChannels().size() > 0
                 && mChannelManagerExt.getCurInfo().serviceType ==
                 EnumServiceType.E_SERVICETYPE_DTV.ordinal()) {
                 Intent intent = new Intent(this, PVRActivity.class);
                 if (keyCode == KeyEvent.KEYCODE_DVR) {
                 intent.putExtra(Constants.PVR_ONE_TOUCH_MODE,
                 Constants.PVR_GENERAL_FLAG);
                 } else {
                 intent.putExtra(Constants.PVR_ONE_TOUCH_MODE,
                 Constants.PVR_TIMESHIFT_FLAG);
                 }
                 startActivity(intent);
                 }
                break;
            case MKeyEvent.KEYCODE_LIST:
                 if (mCurInputSource == TvCommonManager.INPUT_SOURCE_DTV) {
                 Intent intent = new Intent(this,
                 PVRFilePreviewActivity.class);
                 startActivity(intent);
                 }
                break;
            case MKeyEvent.KEYCODE_MSTAR_INDEX:
                if (mCurInputSource == TvCommonManager.INPUT_SOURCE_DTV) {
                    if (mChannelManagerExt.getFavoriteChannels().size() > 0) {
                        Intent favListIntent = new Intent();
                        favListIntent.setClass(this, FavoriteChannelsActivity.class);
                        startActivity(favListIntent);
                    } else if (mChannelManagerExt.getChannelsAll().size() > 0) {
                        Toast.makeText(this, R.string.favoritetip, Toast.LENGTH_LONG).show();
                        Intent intent = new Intent(PlayerActivity.this, ChannelListEditActivity.class);
                        startActivity(intent);
                    }
                }
                break;
            case MKeyEvent.KEYCODE_FREEZE:
                try {
                    int source = getHotKeySource(Settings.System.getInt(getContentResolver(), "hotkey1"));
                    if (!(source == TvCommonManager.INPUT_SOURCE_NONE || source == mCurInputSource)) {
                        TVUtils.setInputSource(source);
                        // 此处是什么意思
                        onResume();
                    } else if (source == TvCommonManager.INPUT_SOURCE_NONE) {
                        Intent menuIntent = new Intent();
                        menuIntent.setClassName(Constants.SETUPPACKAGENAME, Constants.SETUPCLASSNAME);
                        menuIntent.putExtra("gotoadvance", true);
                        startActivity(menuIntent);
                    }
                } catch (SettingNotFoundException e) {
                    e.printStackTrace();
                }
                break;
            case MKeyEvent.KEYCODE_TTX:
                try {
                    int source = getHotKeySource(Settings.System.getInt(getContentResolver(), "hotkey2"));
                    if (!(source == TvCommonManager.INPUT_SOURCE_NONE || source == mCurInputSource)) {
                        TvCommonManager.getInstance().setInputSource(source);
                        onResume();
                    } else if (source == TvCommonManager.INPUT_SOURCE_NONE) {
                        Intent menuIntent = new Intent();
                        menuIntent.setClassName(Constants.SETUPPACKAGENAME, Constants.SETUPCLASSNAME);
                        menuIntent.putExtra("gotoadvance", true);
                        startActivity(menuIntent);
                    }
                } catch (SettingNotFoundException e) {
                    e.printStackTrace();
                }
                break;
            case KeyEvent.KEYCODE_PROG_YELLOW: // 黄色键
                if (!isShowingAdView) {
                    mHolder.getAdLayout().setVisibility(View.VISIBLE);
                } else {
                    mHolder.getAdLayout().setVisibility(View.INVISIBLE);
                }
                isShowingAdView = !isShowingAdView;
                break;
            case 320: // 环绕音键
                if (surroundModeVal == 1) {
                    surroundModeVal = 0;
                    // set Surround Mode to off
                    mTvAudioManager.setAudioSurroundMode(Constants.SURROUND_MODE_OFF);
                    Toast.makeText(this, getResources().getString(R.string.surround_mode_off), Toast.LENGTH_SHORT)
                            .show();
                } else {
                    surroundModeVal = 1;
                    // set Surround Mode to on
                    mTvAudioManager.setAudioSurroundMode(Constants.SURROUND_MODE_ON);
                    Toast.makeText(this, getResources().getString(R.string.surround_mode_on), Toast.LENGTH_SHORT)
                            .show();
                }
                break;

            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    /**
     * 处理数字按键，3秒内输入的连续输入的数字键是有效的，当输入数字达到3位时，立即切台
     * 
     * @param keyCode
     */
    private void responseNumCodeEvent(int keyCode) {
        if (TVUtils.isAtvOrDTV(mCurInputSource)) {
            if (mSelectChannelNum.length() == 0 && keyCode == KeyEvent.KEYCODE_0) {
                return;
            }

            // 最多支持3位数台，当超过时，则清空重新开始计数
            if (mSelectChannelNum.length() == 3) {
                mSelectChannelNum = new StringBuffer("");
            }
            // 显示当前输入数字键信息
            mSelectChannelNum.append(keyCode - KeyEvent.KEYCODE_0 + "");
            mHolder.showSelectChannelNum(mSelectChannelNum.toString());
            mHandler.removeMessages(SELECTCHANNEL);

            int delay = DELAYSELECTCHANNEL;
            if (mSelectChannelNum.length() == 3) {
                // 输入3位时立即切台
                delay = 0;
            } else if (mSelectChannelNum.length() == 2) {
                if (mCurInputSource == TvCommonManager.INPUT_SOURCE_ATV
                        && Integer.valueOf(mSelectChannelNum.toString()) > 12) {
                    // 如果是ATV，且长度为2(ATV最多只有99套台？)，大于12就切台(不知道什么意思)
                    delay = 0;
                }
            }
            msgChnanlNum = mHandler.obtainMessage();
            msgChnanlNum.what = SELECTCHANNEL;
            msgChnanlNum.arg1 = Integer.valueOf(mSelectChannelNum.toString());
            mHandler.sendMessageDelayed(msgChnanlNum, delay);
        }
    }

    /**
     * 处理数字确认键，仅处理ATV和DTV,如果有之前有数字键按下，立即切台。
     */
    private void responseNumEnterEvent() {
        if (TVUtils.isAtvOrDTV(mCurInputSource)) {
            if (mHandler.hasMessages(SELECTCHANNEL)) {
                Log.d("laird", "hasMessages SELECTCHANNEL");
                mHandler.removeMessages(SELECTCHANNEL);
                msgChnanlNum = mHandler.obtainMessage();
                msgChnanlNum.what = SELECTCHANNEL;
                msgChnanlNum.arg1 = Integer.valueOf(mSelectChannelNum.toString());
                mHandler.sendMessage(msgChnanlNum);
            }
        }
    }

    /**
     * 处理双语按键，用於ATV source切換SAP/Mono/Stereo , DTV 切換audio language。
     */
    private void responseBilingualEvent() {
        if (TVUtils.isAtvOrDTV(mCurInputSource) && mChannelManagerExt.getChannels().size() > 0) {
            mHolder.showMtsView(mCurInputSource);
        }
    }

    /**
     * 处理确认键逻辑,仅处理ATV和DTV，如果有台，则显示相应信息，如果无台，则跳转到自动搜台
     */
    private void responseEnterEvent() {
        String info = mHolder.getmSignalTipView().getText();
        if (TVUtils.isAtvOrDTV(mCurInputSource)) {
            if (mChannelManagerExt.getChannels().size() > 0) {
                // 只处理了DTV(ATV无密码锁和频道列表？)
                if (mCurInputSource == TvCommonManager.INPUT_SOURCE_DTV) {
                    if (info.equals(getResources().getString(R.string.passwordtip))) {
                        // to show password dialog.
                        new PasswordCheckDialog(this, PasswordCheckDialog.UNLOCK, mHandler).show();
                    } else {
                        // to show channel list.
                        Intent channelListIntent = new Intent();
                        channelListIntent.setClass(this, ChannelListActivity.class);
                        startActivity(channelListIntent);
                    }
                }
            } else {
                // 无台时跳转到自动搜台
                if (info.equals(getString(R.string.tuningtip))) {
                    mHolder.dismissSignalView();
                    Intent intent = new Intent();
                    if (mCurInputSource == TvCommonManager.INPUT_SOURCE_DTV) {
                        intent.setClassName(Constants.SETUPPACKAGENAME, Constants.DVTTUNING);
                    } else {
                        intent.setClassName(Constants.SETUPPACKAGENAME, Constants.AVTTUNING);
                    }
                    startActivity(intent);
                }
            }
        }
    }

    /**
     * 处理频道变化按键
     * 
     * @param key
     */
    private void tvChannelChange(int key) {
        if (TVUtils.isAtvOrDTV(mCurInputSource)) {
            // 切台
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
                    break;
            }
            // 显示当前台相关信息
            if (mChannelManagerExt.getCurInfo() != null) {
                mHolder.showSelectChannelInfo(mChannelManagerExt.getCurInfo());
                if (mCurInputSource == TvCommonManager.INPUT_SOURCE_DTV) {
                    mHolder.showDtvInfoView(mChannelManagerExt.getCurInfo());
                }
                if (mCurInputSource == TvCommonManager.INPUT_SOURCE_ATV) {
                    mHolder.showAtvInfoView(mChannelManagerExt.getCurInfo());
                }
            }
        }
    }

    public void channelChange() {
        mHolder.showSelectChannelInfo(mChannelManagerExt.getCurInfo());
    }

    /**
     * Heren客户TVMenu设置里面，有2个热键设置
     * 
     * @param hotkeyValue
     * @return
     */
    private int getHotKeySource(int hotkeyValue) {
        int source = TvCommonManager.INPUT_SOURCE_NONE;
        switch (hotkeyValue) {
            case 1:
                source = TvCommonManager.INPUT_SOURCE_ATV;
                break;
            case 2:
                TVUtils.enableMute(true);
                source = TvCommonManager.INPUT_SOURCE_DTV;
                mHandler.postDelayed(mute_thread, 500);
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

    Runnable mute_thread = new Runnable() {
        @Override
        public void run() {
            TVUtils.enableMute(false);
            mHandler.removeCallbacks(mute_thread);
        }
    };

}
