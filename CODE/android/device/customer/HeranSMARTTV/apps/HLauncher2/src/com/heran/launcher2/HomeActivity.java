
package com.heran.launcher2;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;

import com.heran.launcher2.eosweb.MyWebViewActivity;
import com.heran.launcher2.eosweb.MyWebViewClient;
import com.heran.launcher2.lifearea.HelperActivity;
import com.heran.launcher2.util.ChannelManagerExt;
import com.heran.launcher2.util.Constants;
import com.heran.launcher2.util.UIUtil;
import com.heran.launcher2.util.Utils;
import com.mstar.android.MKeyEvent;
import com.mstar.android.tv.TvChannelManager;
import com.mstar.android.tv.TvCommonManager;
import com.mstar.android.tvapi.common.AudioManager;
import com.mstar.android.tvapi.common.TvManager;
import com.mstar.android.tvapi.common.exception.TvCommonException;
import com.mstar.android.tvapi.common.vo.EnumProgramInfoType;
import com.mstar.android.tvapi.common.vo.EnumScalerWindow;
import com.mstar.android.tvapi.common.vo.MuteType.EnumMuteType;
import com.mstar.android.tvapi.common.vo.ProgramInfo;
import com.mstar.android.tvapi.common.vo.ProgramInfoQueryCriteria;
import com.mstar.android.tvapi.common.vo.TvOsType.EnumInputSource;
import com.mstar.android.tvapi.common.vo.VideoWindowType;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.provider.Settings.SettingNotFoundException;
import android.util.Log;
import android.view.IWindowManager;
import android.view.KeyEvent;
import android.view.SurfaceView;
import android.view.View;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.widget.ProgressBar;

/**
 * projectName： EOSLauncher moduleName：HomeActivity.java
 * 
 * @author laird.li
 * @version 1.0.0
 * @time 2016-03-30
 * @Copyright © 2016 Eos, Inc.
 */
@SuppressLint("NewApi")
public class HomeActivity extends Activity {

    private final AudioManager mTvAudioManager = TvManager.getInstance().getAudioManager();

    private static final String TAG = "HomeActivity";

    private MyHandler mHandler;

    private int mAdsIndex = 0;

    private int Adposition;
    
    private int checkNetCount = 0;
    
    private boolean isRestart = false;

    public static String myMac = "00:88:88:00:00:01";

    // view holder
    public MainViewHolder mviewHolder = null;

    // MainLogic
    public MainLogic mainLogic = null;

    private final Object bSync = new Object();

    public int toChangeInputSource = TvCommonManager.INPUT_SOURCE_NONE;

    // change tv source
    private final InputSourceThread inputSourceThread = new InputSourceThread();

    private boolean HasUpdate = false;

    private boolean FristOn = true;

    // first PowerOn
    public boolean isPowerOn = false;

    // surface size state of TV
    SurfaceView surfaceView = null;

    private android.media.AudioManager mKeyAudioManager;

    public Boolean piFragmentShow = false;

    // add by Sarah for version number
    public static String mVersion;

    private MyWebViewClient mWebViewClien = null;

    protected String loadURL = "http://219.87.154.38/jowintest/index.php?r=activity/hertv1";

    // 輪播個人資料區變數
    public int piViewIndex;

    public Thread t;
    private boolean flg = true;
    
    private boolean hasTuner = false;

    // Handle different message
    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Constants.TIME_UPDATE:
                    Log.d(TAG, "TIME_UPDATE");
                    mviewHolder.updateDateInfo();
                    sendEmptyMessageDelayed(Constants.TIME_UPDATE, Constants.DELAYUPDATETIME);
                    break;
                case Constants.TODISMISSTIP: // hide help_tip view
                    Log.d(TAG, "TODISMISSTIP");
                    if (mviewHolder.mHomeFragment.getHelp_tip() != null) {
                        mviewHolder.mHomeFragment.getHelp_tip().setVisibility(View.GONE);
                    }
                    break;
                case Constants.ADUPDATE: // udpate advertisement pictures
                    Log.d(TAG, "ADUPDATE");
                    if (mainLogic.adction != null) {
                        mainLogic.homeAdInfoList = mainLogic.adction.getAdInfoList();
                        int position = msg.arg1;

                        mviewHolder.udpateViewFlipper(position);
                    }
                    break;
                case Constants.APPUPDATE:
                    Log.d(TAG, "Constants.APPUPDATE");
                    if (mainLogic.appAction != null) {
                        Log.d(TAG, "appAction.getDownAppInfoList");
                        try {
                            mainLogic.downAppInfos = mainLogic.appAction.getDownAppInfoList();
                            if(mviewHolder.getmLastPosition() == Constants.APPVIEW){
                                mviewHolder.mAppFragment.getAppHander().sendEmptyMessage(1);
                            }
                        } catch (Exception e) {
                            Log.i("willy", "Constants.APPUPDATE=====" + e.getMessage());
                        }

                    }
                    break;
                case Constants.UPDATE_APP_INFO:
                    if (mainLogic.appAction != null) {
                        Log.d(TAG, "appAction.parsePgmJson");
                        mainLogic.appAction.parsePgmJson();
                        }
                    
                    break;
                case Constants.OSDMESSAGE: // update advertisement text
                    Log.d(TAG, "Constants.OSDMESSAGE");
                    if (mainLogic.mTxtAdsStrings != null && mainLogic.mTxtAdsStrings.length > 0) {

                        if (mAdsIndex == 1500000) {
                            mAdsIndex = Adposition % mainLogic.mTxtAdsDurations.length;
                        }
                        if (Utils.isNetworkState) {

                            if (HasUpdate) {
                                mAdsIndex = 0;

                                if (!FristOn) {
                                    showPIViews(Constants.MESSAGEVIEW);
                                    mHandler.sendEmptyMessageDelayed(Constants.PIFRAGMENTCHANGE, 60 * 1000);
                                }
                                FristOn = false;
                                HasUpdate = false;
                            }
                        }

                        Adposition = mAdsIndex;

                        try {
                            mviewHolder.mMessageFragment.showOSDMessage(GetAdText());
                        } catch (Exception e) {
                        }

                        int length = GetAdText().length();
                        int Second = 0;
                        if (length < 41) {
                            Second = 0;
                        } else {
                            Second = length - 41;
                        }

                        mHandler.removeMessages(Constants.OSDMESSAGE);
                        mHandler.sendEmptyMessageDelayed(Constants.OSDMESSAGE,
                                mainLogic.mTxtAdsDurations[Adposition % mainLogic.mTxtAdsDurations.length]
                                        + Second * 675);
                        mAdsIndex++;
                    }

                    break;
                case Constants.OSDUPDATE:
                    Log.d(TAG, "UPDATE_AD_FLAG");
                    mainLogic.mTxtAdsStrings = mainLogic.adtxtction.getmTxtAdsStrings();
                    mainLogic.mTxtAdsDurations = mainLogic.adtxtction.getmAdDuration();
                    HasUpdate = mainLogic.adtxtction.HasUpDate();
                    if (Utils.isNetworkState && Utils.isInMessageFragment && HasUpdate) {
                        mHandler.removeMessages(Constants.OSDMESSAGE);
                        mHandler.sendEmptyMessage(Constants.OSDMESSAGE);
                    }
                    break;
                case Constants.UPDATE_AD_FLAG:
                    Log.d(TAG, "UPDATE_AD_FLAG");
                    mainLogic.adction.parsePgmJson();
                    break;
                case Constants.PIFRAGMENTCHANGE: // 個人資訊區更新
                    Log.d(TAG, "PIFRAGMENTCHANGE");
                    showPIViews(mviewHolder.getNowPIPosition());
                    break;
                case Constants.UPDATE_NEWS_FLAG:
                    Log.d(TAG, "UPDATE_NEWS_FLAG");
                    mainLogic.newsAction.parsePgmJson();
                    break;
                case Constants.NEWSUPDATE:
                    Log.d(TAG, "NEWSUPDATE");
                    if (mainLogic.newsInfoList != null) {
                        mainLogic.newsInfoList = mainLogic.newsAction.getNewsInfoList();
                        mviewHolder.mNewsFragment.setNewsList(mainLogic.newsInfoList);
                        mviewHolder.mNewsMainFragment.setList(mainLogic.newsInfoList);
                    }
                    break;
                case Constants.WEARHER_TODAY:
                    Log.d(TAG, "WEATHER Constants.WEARHER_TODAY");
                    break;
                case Constants.WEARHER_WEEK:
                    Log.d(TAG, "WEATHER Constants.WEARHER_WEEK");
                    break;

                case Constants.WEARHER_City:
                    Log.d(TAG, "WEATHER Constants.WEARHER_City");
                    break;
                case Constants.WEARHER_Life:
                    Log.d(TAG, "WEATHER Constants.WEARHER_life");
                    break;
                case Constants.INIT_HOMEFRAGMENT_VIEWFLIPPER:
                    Log.d(TAG, "INIT_HOMEFRAGMENT_VIEWFLIPPER mviewHolder.mViewBean.getmCurFocusView()： "
                            + mviewHolder.mViewBean.getmCurFocusView());
                    if (mviewHolder.mViewBean.getmCurFocusView() != null
                            && mviewHolder.mViewBean.getmCurFocusView().getId() != mviewHolder.mBt1.getId()
                            && mviewHolder.mViewBean.getmCurFocusView().getId() != mviewHolder.mBt2.getId()) {
                        Log.d(TAG, "mviewHolder.animType:" + mviewHolder.animType);
                        mviewHolder.mFlipperAnimation.loadFlipperAnimation(mviewHolder.animType);
                        // play ads
                    }
                    mHandler.removeMessages(Constants.INIT_HOMEFRAGMENT_VIEWFLIPPER);
                    mHandler.sendEmptyMessageDelayed(Constants.INIT_HOMEFRAGMENT_VIEWFLIPPER, mviewHolder.animTime);
                    break;

                case Constants.SHOW_NEWVIEWS:
                    Log.d(TAG, "SHOW_NEWVIEWS");
                    showPIViews(Constants.NEWSVIEW);
                    break;
                case Constants.FINISH_BOOT_AD:
                    Log.d(TAG, "FINISH_BOOT_AD");
                    new Thread(inputSourceThread).start();
                    break;
                case Constants.BOOTAD_IFFINISH:                  	
                 	  // show News in LowArea after 60s
                       checkNetCount ++;
                	   if (Utils.isNetworkState) {
                       mHandler.sendEmptyMessageDelayed(Constants.SHOW_NEWVIEWS, 60 * 1000);
                       Constants.BOOTAD_FINISH = false ;   
                	  }else{
                	      if(checkNetCount <=3){
                	          removeMessages(Constants.BOOTAD_IFFINISH);
                	          sendEmptyMessageDelayed(Constants.BOOTAD_IFFINISH,10*1000);
                	      }
                	  }
                    break;                      
                default:
                    break;
            }
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Log.d(TAG, "onCreate");
        isRestart = false;

        mHandler = new MyHandler();

        mainLogic = new MainLogic(this, mHandler);
        //mainLogic.jumpToBootAdActivity();
        mainLogic.initDatas();
        mainLogic.registerLister();

        mviewHolder = new MainViewHolder(this, mHandler);
        mviewHolder.initView();
        mviewHolder.registerViewListener();
        // show home ads
        mviewHolder.initFlipper();
        init();
        
        try {
            hasTuner = TvManager.getInstance().getFactoryManager().getTunerStatus();
        } catch (TvCommonException e) {
            e.printStackTrace();
        }

    }

    private void handFirstPowerOnEvent() {
        Log.i(TAG, "power1 = " + SystemProperties.get(Constants.FIRST_POWER_ON, "0"));
        if (SystemProperties.get(Constants.FIRST_POWER_ON, "0").equals("0")) {
            Log.d(TAG, "This is first power on");
            // handleHotelEvent();
            setPropertyForSTR(Constants.FIRST_POWER_ON, "1");
        }
        Log.i(TAG, "power2 = " + SystemProperties.get(Constants.FIRST_POWER_ON, "0"));
    }

    private void setPropertyForSTR(String key, String value) {
        IWindowManager winService = IWindowManager.Stub
                .asInterface(ServiceManager.checkService(Context.WINDOW_SERVICE));
        if (winService == null) {
            Log.w(TAG, "Unable to find IWindowManger interface.");
        } else {
            SystemProperties.set(key, value);
        }
    }

    private void init() {
        // show HomeFragment in UpperArea
        showViews(Constants.HOMEVIEW);

        // show Message in LowArea
        piViewIndex = Constants.MESSAGEVIEW;
        showPIViews(piViewIndex);

        // show News in LowArea after 6s
        // mHandler.sendEmptyMessageDelayed(Constants.SHOW_NEWVIEWS, 60 * 1000);

        // start a thread to listen to source change
        // new Thread(inputSourceThread).start();
        new Thread(new Runnable() {

            @Override
            public void run() {
                while (!Utils.isAdVideoFinish()) {
                    try {
                        Thread.sleep(10);
                    } catch (InterruptedException e) {
                        e.printStackTrace();
                    }
                }
                Log.d(TAG, "finish boot ad play");
                mHandler.sendEmptyMessage(Constants.FINISH_BOOT_AD);
            }
        }).start();

        mKeyAudioManager = (android.media.AudioManager) getSystemService(Context.AUDIO_SERVICE);
        android.provider.Settings.System.putInt(getContentResolver(), "Gpio", 1);

    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        // when click power key wake up the OS,click home key,setSmallscale
        SharedPreferences mPreferences = getPreferences(Activity.MODE_PRIVATE);
        String powerState = mPreferences.getString(Constants.POWER_STATE, Constants.POWER_STATE_DEFAULT_VALUE);
        Log.v(TAG, "onNewIntent  powerState = " + powerState);
        if (powerState.equals(Constants.POWER_STATE_CLICKED_VALUE)) {
            mainLogic.setSmallscale();
            mPreferences.edit().putString(Constants.POWER_STATE, Constants.POWER_STATE_DEFAULT_VALUE).commit();
            Log.v(TAG, "onNewIntent  setSmallscale ");
        }
        // change source
        isSourceChange(intent);

        // Receive menu button to display different views
        mviewHolder.cleanFragmentAllFocus();

        // choice show UpperArea
        String viewName = intent.getStringExtra("gotolanucher");
        if (viewName == null) {
            showViews(Constants.HOMEVIEW);
            return;
        }
        if (viewName.equals("app")) {
            showViews(Constants.APPVIEW);
        } else if (viewName.equals("media")) {
            showViews(Constants.LIVINGAREAVIEW);
        } else if (viewName.equals("heran")) {
            // 全畫面的商城
//            Intent intent1 = new Intent(HomeActivity.this, NewShopActivity.class);
//            startActivity(intent1);
            //成员管理
            String clickUrl = "http://www.jowinwin.com/hertv2msd/index.php?r=member/member";
            intent = new Intent(this, MyWebViewActivity.class);
            Bundle bundle = new Bundle();
            bundle.putString("URL", clickUrl);
            intent.putExtras(bundle);
            startActivity(intent);
        } else {
            showViews(Constants.HOMEVIEW);
        }

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        Log.d(TAG, "onRestart");
        isPowerOn = true;
        if (mviewHolder == null) {
            mviewHolder = new MainViewHolder(this, mHandler);
        }
        Log.v(TAG, "HomeActivity---onRestart mViewHolder " + (mviewHolder == null));
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        mviewHolder.updatePIFragment(Constants.CITYSELECT_CLOSE,2);
        UIUtil.setSystemUIVisible(this, 0);
        MainLogic.isCancelScale = false;
        if (surfaceView == null) {
            isPowerOn = true;
            handlertv.postDelayed(handlerRuntv, 2000);
            backHomeSource();
            Log.d(TAG, "onResume  surfaceView = null");
        } else {
        	if (flg) {
            android.provider.Settings.System.putInt(getContentResolver(), "tvapp", 1);
            Log.d(TAG, "onResume  surfaceView != null");
            handlertv.postDelayed(pip_thread, 1000);
            flg = false;
            }
        }
        getWindow().getDecorView().setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);

        mHandler.removeMessages(Constants.TIME_UPDATE);
        mHandler.sendEmptyMessage(Constants.TIME_UPDATE);

        mHandler.removeMessages(Constants.INIT_HOMEFRAGMENT_VIEWFLIPPER);
        mHandler.sendEmptyMessageDelayed(Constants.INIT_HOMEFRAGMENT_VIEWFLIPPER,
                Constants.DELAY_HOMEFRAGMENT_VIEWFLIPPER);
        
       
       mHandler.removeMessages(Constants.BOOTAD_IFFINISH);
       if(Constants.BOOTAD_FINISH){
       mHandler.sendEmptyMessageDelayed(Constants.BOOTAD_IFFINISH,500);
       }	       
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");

        mviewHolder.mAdWebView.onPause();
        mviewHolder.mAdWebView.setVisibility(View.GONE);
        Log.d(TAG, "before removeCallbacks pi_info_thread");
        Log.d(TAG, "after removeCallbacks pi_info_thread");

        if (Utils.getCurrentActivity(this).equals("com.eostek.tv.player.PlayerActivity")) {
            MainLogic.isCancelScale = true;
            handlertv.removeCallbacks(pip_thread);
        }
        if (Utils.getCurrentActivity(this).equals("com.mstar.tv.menu.ui.EosCustomSettingActivity")
                || Utils.getCurrentActivity(this).equals("com.eostek.hotkey.EosInputActivity")) {
            flg = false;
            }
        if (Utils.getCurrentActivity(this).equals("com.biaoqi.filefly")) {
            toChangeInputSource = TvCommonManager.INPUT_SOURCE_STORAGE;
        }
        if (Utils.getCurrentActivity(this).equals("com.bq.tv.task.MainActivity")) {
            flg = true;
        }
    }

    @Override
    protected void onStop() {
        UIUtil.setSystemUIVisible(this, 1);
        android.provider.Settings.System.putInt(getContentResolver(), "tvapp", 0);
        mHandler.removeMessages(Constants.TIME_UPDATE);
        mHandler.removeMessages(Constants.INIT_HOMEFRAGMENT_VIEWFLIPPER);
        mHandler.removeMessages(Constants.SHOW_NEWVIEWS);
        mHandler.removeMessages(Constants.PIFRAGMENTCHANGE);

        super.onStop();
        Log.d(TAG, "onStop");
        String className = Utils.getCurrentActivity(this);
        if (!className.equals("com.eostek.tv.player.PlayerActivity")
                && !className.equals("com.utsmta.app.MainActivity")) {
            toChangeInputSource = TvCommonManager.INPUT_SOURCE_STORAGE;
        }
        flg = true;
        mviewHolder.mFlipper.clearAnimation();
        try {
            mviewHolder.mAdWebView.getClass().getMethod("stopLoading").invoke(mviewHolder.mAdWebView, (Object[]) null);
            mviewHolder.mAdWebView.getClass().getMethod("onPause").invoke(mviewHolder.mAdWebView, (Object[]) null);
            handlertv.postDelayed(handlerRuntv, 1500);
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "HomeActivity---onDestroy ");
        mainLogic.unregisterLister();
        mainLogic.clearDatas();
    }

    /**
     * change display view
     * 
     * @param num The num corresponding to the view
     */
    public void showViews(int num) {
        if (num == 0) { // when in Home view switching signal, note
                        // synchronization
            toChangeInputSource = TvCommonManager.INPUT_SOURCE_DTV;
            handlertv.postDelayed(mute_thread, 500);
        } else {
            toChangeInputSource = TvCommonManager.INPUT_SOURCE_STORAGE;
        }

        mviewHolder.updateFragment(num);
    }

    public void showPIViews(int num) {
        
        if (isRestart) {
            Log.d(TAG, "Launcher is killed,return");
            isRestart = false;
            return;
        }

        mviewHolder.updatePIFragment(num, 2);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        Log.d(TAG, "dispatchKeyEvent event:" + event.getKeyCode());
        if ((event.getKeyCode() == KeyEvent.KEYCODE_WINDOW)) {
            return true;
        }
        return super.dispatchKeyEvent(event);
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        Log.d(TAG, "onKeyDown:" + keyCode);
        Intent mIntent = null;
        // according to the numerical buttons to display different views
        switch (keyCode) {
            case KeyEvent.KEYCODE_PROG_RED:
                mviewHolder.mHomeFragment.getHelp_tip().setVisibility(View.GONE);
                mIntent = new Intent();
                mIntent.setClass(HomeActivity.this, HelperActivity.class);
                startActivity(mIntent);
                overridePendingTransition(R.anim.photo_push_left_in, 0);
                return false;
            case MKeyEvent.KEYCODE_TV_FREEZE:
                if (mviewHolder.getmLastPosition() == Constants.HOMEVIEW) {
                    try {
                        int source = getHotKeySource(
                                android.provider.Settings.System.getInt(getContentResolver(), "hotkey1")).ordinal();
                        int curSource = queryCurInputSrc();
                        if (!(source == TvCommonManager.INPUT_SOURCE_NONE || source == curSource)) {
                            TvCommonManager.getInstance().setInputSource(source);
                            if (source == TvCommonManager.INPUT_SOURCE_ATV
                                    || source == TvCommonManager.INPUT_SOURCE_DTV) {
                                ChannelManagerExt cm = ChannelManagerExt.getInstance();
                                cm.getAllChannels(this, source);
                                ProgramInfo pinfo = cm.getCurProgramInfo();
                                programSel(pinfo.number, pinfo.serviceType);
                            }
                            mainLogic.setSmallscale();
                        } else if (source == TvCommonManager.INPUT_SOURCE_NONE) {
                            Intent menuIntent = new Intent();
                            menuIntent.setClassName("com.mstar.tv.menu",
                                    "com.mstar.tv.menu.ui.EosCustomSettingActivity");
                            menuIntent.putExtra("gotoadvance", true);
                            startActivity(menuIntent);
                        }
                    } catch (SettingNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                return true;
            case MKeyEvent.KEYCODE_TTX:
                if (mviewHolder.getmLastPosition() == Constants.HOMEVIEW) {
                    try {
                        int source = getHotKeySource(
                                android.provider.Settings.System.getInt(getContentResolver(), "hotkey2")).ordinal();
                        int curSource = queryCurInputSrc();
                        if (!(source == TvCommonManager.INPUT_SOURCE_NONE || source == curSource)) {
                            TvCommonManager.getInstance().setInputSource(source);
                            if (source == TvCommonManager.INPUT_SOURCE_ATV
                                    || source == TvCommonManager.INPUT_SOURCE_DTV) {
                                ChannelManagerExt cm = ChannelManagerExt.getInstance();
                                cm.getAllChannels(this, source);
                                ProgramInfo pinfo = getCurrProgramInfo();
                                programSel(pinfo.number, pinfo.serviceType);
                            }
                            mainLogic.setSmallscale();
                        } else if (source == TvCommonManager.INPUT_SOURCE_NONE) {
                            Intent menuIntent = new Intent();
                            menuIntent.setClassName("com.mstar.tv.menu",
                                    "com.mstar.tv.menu.ui.EosCustomSettingActivity");
                            menuIntent.putExtra("gotoadvance", true);
                            startActivity(menuIntent);
                        }
                    } catch (SettingNotFoundException e) {
                        e.printStackTrace();
                    }
                }
                return true;
            case KeyEvent.KEYCODE_BACK:
                // ignore back key click
                // return true;
                if (surfaceView != null && mviewHolder.getmLastPosition() == Constants.HOMEVIEW) {
                    Log.d(TAG, "mviewHolder.mHomeFragment.backCurrentSource");
                    mviewHolder.mHomeFragment.backCurrentSource();

                    mviewHolder.mBt1.setFocusable(true);
                    mviewHolder.mBt2.setFocusable(true);

                    if (mviewHolder.mAdWebView.getVisibility() == View.VISIBLE) {
                        Log.d("mAdWebView", "mAdWebView==>VISIBLE");
                        mviewHolder.mAdWebView.loadUrl("file:///android_asset/background.html");
                        mviewHolder.mAdWebView.setFocusable(false);
                        mviewHolder.mAdWebView.clearFocus();
                        stopVodPlay(mviewHolder.mAdWebView, mviewHolder.prgBar);
                    }

                    Log.d(TAG, "mviewHolder.mHomeFragment.backCurrentSource");
                }
                if(mviewHolder.getmLastPosition()!=Constants.HOMEVIEW){
                	if(mviewHolder.getmLastPosition()==Constants.NEWSMAINFRAGMENT
                	||mviewHolder.getmLastPosition()==Constants.WEATHERMAINFRAGMENT
                	||mviewHolder.getmLastPosition()==Constants.MESSAGEFRAGMENT){
                		
                		mviewHolder.updateFragment(mviewHolder.getmPrePosition());
                	}else{
                		mviewHolder.setHomeBtnFocus(0);
                	}
                	 
                }else{
                	 mviewHolder.setHomeBtnFocus(1);	
                }
                return true;
            case KeyEvent.KEYCODE_INFO:
                // 「顯示」按鈕，顯示版本號和電視Mac
                UIUtil.showToast(getApplication(),
                        "             v" + this.getString(R.string.version) + "       \n" + myMac);
                return true;              
            case KeyEvent.KEYCODE_MENU:
                if (mviewHolder.getmLastPosition() == Constants.HOMEVIEW) {
                    Intent menuIntent = new Intent();
                    menuIntent.setClassName("com.mstar.tv.menu", "com.mstar.tv.menu.ui.EosCustomSettingActivity");
                    // menuIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(menuIntent);
                }
                break;
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    private EnumInputSource tvHasTuner(int hotkeyValue) {
        EnumInputSource source = EnumInputSource.E_INPUT_SOURCE_NONE;
        switch (hotkeyValue) {
            case 1:
                source = EnumInputSource.E_INPUT_SOURCE_ATV;
                break;
            case 2:
                try {
                    mTvAudioManager.enableMute(EnumMuteType.E_MUTE_PERMANENT);
                } catch (TvCommonException e) {
                    e.printStackTrace();
                }
                source = EnumInputSource.E_INPUT_SOURCE_DTV;
                handlertv.postDelayed(mute_thread, 500);
                break;
            case 3:
                source = EnumInputSource.E_INPUT_SOURCE_HDMI;
                break;
            case 4:
                source = EnumInputSource.E_INPUT_SOURCE_HDMI2;
                break;
            case 5:
                source = EnumInputSource.E_INPUT_SOURCE_HDMI3;
                break;
            case 6:
                source = EnumInputSource.E_INPUT_SOURCE_CVBS;
                break;
            case 7:
                source = EnumInputSource.E_INPUT_SOURCE_YPBPR;
                break;
            case 8:
                source = EnumInputSource.E_INPUT_SOURCE_VGA;
                break;
            default:
                break;
        }
        return source;
    }




    private EnumInputSource tvHasNoTuner(int hotkeyValue) {
        EnumInputSource source = EnumInputSource.E_INPUT_SOURCE_NONE;
        switch (hotkeyValue) {
            case 1:
                source = EnumInputSource.E_INPUT_SOURCE_HDMI;
                break;
            case 2:
                source = EnumInputSource.E_INPUT_SOURCE_HDMI2;
                break;
            case 3:
                source = EnumInputSource.E_INPUT_SOURCE_HDMI3;
                break;
            case 4:
                source = EnumInputSource.E_INPUT_SOURCE_CVBS;
                break;
            case 5:
                source = EnumInputSource.E_INPUT_SOURCE_YPBPR;
                break;
            case 6:
                source = EnumInputSource.E_INPUT_SOURCE_VGA;
                break;
            default:
                break;
        }
        return source;
    }

    
    
    /**
     * get current hot key source
     * 
     * @param int hotkeyValue
     * @return source TvCommonManager.INPUT_SOURCE_ATV E_INPUT_SOURCE_DTV
     *         E_INPUT_SOURCE_HDMI E_INPUT_SOURCE_HDMI4 E_INPUT_SOURCE_SVIDEO
     *         E_INPUT_SOURCE_YPBPR E_INPUT_SOURCE_VGA
     */
    private EnumInputSource getHotKeySource(int hotkeyValue) {
        if (hasTuner) {
            return tvHasTuner(hotkeyValue);
        }
        return tvHasNoTuner(hotkeyValue);
    }

    // set intput source
    public void setToChangeInputSource(int toChangeInputSource) {
        synchronized (bSync) {
            this.toChangeInputSource = toChangeInputSource;
        }

    }

    // handlertv postDelayed chanage source
    Handler handlertv = new Handler();

    Runnable handlerRuntv = new Runnable() {
        @Override
        public void run() {
            surfaceView = mviewHolder.mHomeFragment.getmTv_sur();
            handlertv.removeCallbacks(handlerRuntv);
        }

    };

    Runnable pip_thread = new Runnable() {
        @Override
        public void run() {
            mainLogic.setSmallscale();
            surfaceView.setBackgroundColor(Color.TRANSPARENT);
            handlertv.removeCallbacks(pip_thread);
        }
    };

    Runnable mute_thread = new Runnable() {
        @Override
        public void run() {
            try {
                mTvAudioManager.disableMute(EnumMuteType.E_MUTE_PERMANENT);
            } catch (TvCommonException e) {
                e.printStackTrace();
            }
            handlertv.removeCallbacks(mute_thread);
        }
    };

    // reset input source when resume
    public void backHomeSource() {
        Log.i(TAG, "BackHomeSource------------ bSync = ");
        synchronized (bSync) {
            toChangeInputSource = TvCommonManager.INPUT_SOURCE_DTV;
        }
    }

    /**
     * Set fullscreen
     */
    public void setFullscale() {
        try {
            VideoWindowType videoWindowType = new VideoWindowType();
            videoWindowType.height = 0xFFFF;
            videoWindowType.width = 0xFFFF;
            videoWindowType.x = 0xFFFF;
            videoWindowType.y = 0xFFFF;
            if (TvManager.getInstance() != null) {
                TvManager.getInstance().getPictureManager().selectWindow(EnumScalerWindow.E_MAIN_WINDOW);
                TvManager.getInstance().getPictureManager().setDisplayWindow(videoWindowType);
                TvManager.getInstance().getPictureManager().scaleWindow();
            }
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
    }

    /**
     * switch program
     * 
     * @param u32Number
     * @param u8ServiceType
     * @return
     */
    public boolean programSel(int u32Number, int u8ServiceType) {
        int currentSource;
        currentSource = TvCommonManager.getInstance().getCurrentTvInputSource();
        // when the current source is storage,return false
        if (currentSource == TvCommonManager.INPUT_SOURCE_STORAGE) {
            Log.v(TAG, "programSel, currentSource = E_INPUT_SOURCE_STORAGE");
            return false;
        }
        try {
            if (TvManager.getInstance() != null) {
                TvManager.getInstance().getChannelManager().selectProgram(u32Number, (short) u8ServiceType, 0x00);
            }
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * query the current input source
     * 
     * @return InputSourceType
     */
    public int queryCurInputSrc() {
        int value = 0;
        Cursor cursor = getContentResolver().query(Uri.parse("content://mstar.tv.usersetting/systemsetting"), null,
                null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            value = cursor.getInt(cursor.getColumnIndex("enInputSourceType"));
        }
        if (cursor != null) {
            cursor.close();
        }
        return value;
    }

    /**
     * Get the current channel information
     * 
     * @return ProgramInfo
     */
    public ProgramInfo getCurrProgramInfo() {
        ProgramInfoQueryCriteria qc = new ProgramInfoQueryCriteria();
        return TvChannelManager.getInstance().getProgramInfo(qc, EnumProgramInfoType.E_INFO_CURRENT);
    }

    /**
     * Source Change tv surface change size & programSel
     * 
     * @return void
     */
    public void isSourceChange(Intent intent) {
        Boolean isSourceChange = intent.getBooleanExtra("isSourceChange", false);
        Boolean isChangeChannel = intent.getBooleanExtra("isChangeChannel", false);
        Log.d(TAG, "isSourceChange:" + isSourceChange);
        Log.d(TAG, "isChangeChannel:" + isChangeChannel);
        // source change tv surface changer
        if (isSourceChange) {
            mainLogic.setSmallscale();
        }
        // programSel
        if (isChangeChannel) {
            ProgramInfo pinfo = getCurrProgramInfo();
            programSel(pinfo.number, pinfo.serviceType);
        }
    }

    /**
     * The thread to handle input source changes
     */
    class InputSourceThread implements Runnable {
        @Override
        public void run() {
            int tmpInputSource;
            // to make OnTvEventListener work well when first time Launcher
            // start
            TvCommonManager.getInstance().setInputSource(TvCommonManager.INPUT_SOURCE_STORAGE);
            Log.v(TAG, "InputSourceThread start ,set storage");
            while (true) {
                synchronized (bSync) {
                    tmpInputSource = toChangeInputSource;
                    toChangeInputSource = TvCommonManager.INPUT_SOURCE_NONE;
                }
                if (tmpInputSource != TvCommonManager.INPUT_SOURCE_NONE) {
                    int currentSource;
                    if (tmpInputSource == TvCommonManager.INPUT_SOURCE_DTV
                            || tmpInputSource == TvCommonManager.INPUT_SOURCE_ATV) {
                        currentSource = TvCommonManager.getInstance().getCurrentTvInputSource();
                        if (currentSource == TvCommonManager.INPUT_SOURCE_STORAGE) {
                            int curSource = queryCurInputSrc();
                            TvCommonManager.getInstance().setInputSource(curSource);
                            mainLogic.setSmallscale();
                            if (curSource == TvCommonManager.INPUT_SOURCE_ATV
                                    || curSource == TvCommonManager.INPUT_SOURCE_DTV) {
                                ProgramInfo pinfo = getCurrProgramInfo();
                                programSel(pinfo.number, pinfo.serviceType);
                            }
                        }
                    } else {
                        TvCommonManager.getInstance().setInputSource(tmpInputSource);
                    }
                }

                try {
                    Thread.sleep(100);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * when navigation key clicked which return true,play voice
     */
    public void playVoice(int keyCode) {
        switch (keyCode) {
            case KeyEvent.KEYCODE_DPAD_LEFT:
                mKeyAudioManager.playSoundEffect(android.media.AudioManager.FX_FOCUS_NAVIGATION_LEFT);
                break;
            case KeyEvent.KEYCODE_DPAD_RIGHT:
                mKeyAudioManager.playSoundEffect(android.media.AudioManager.FX_FOCUS_NAVIGATION_RIGHT);
                break;
            case KeyEvent.KEYCODE_DPAD_UP:
                mKeyAudioManager.playSoundEffect(android.media.AudioManager.FX_FOCUS_NAVIGATION_UP);
                break;
            case KeyEvent.KEYCODE_DPAD_DOWN:
                mKeyAudioManager.playSoundEffect(android.media.AudioManager.FX_FOCUS_NAVIGATION_DOWN);
                break;
            default:
                break;
        }
    }

    /**
     * stop vod play
     */
    public void stopVodPlay(WebView mAdWebView, ProgressBar prgBar) {
        backHomeSource();
        handlertv.postDelayed(pip_thread, 1000);
        try {
            mAdWebView.setVisibility(View.GONE);
            prgBar.setVisibility(View.GONE);
            mAdWebView.getClass().getMethod("onPause").invoke(mAdWebView, (Object[]) null);
            mAdWebView.getClass().getMethod("stopLoading").invoke(mAdWebView, (Object[]) null);
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }

    }

    public void loadUrl() {

        mviewHolder.mAdWebView.setVisibility(View.VISIBLE);
        mviewHolder.prgBar.setVisibility(View.VISIBLE);
        mviewHolder.mAdWebView.setHorizontalScrollBarEnabled(false);
        mviewHolder.mAdWebView.setVerticalScrollBarEnabled(false);

        mviewHolder.mHomeFragment.getImageTV().setFocusable(false);
        mviewHolder.mBt1.setFocusable(false);
        mviewHolder.mBt2.setFocusable(false);

        mviewHolder.mAdWebView.clearCache(true);
        if (loadURL == "http://www.jowinwin.com/hertv2msd/index.php?r=activity/initPlay&id=473&movie=0&live=0") {
            mviewHolder.mAdWebView.setInitialScale(150);
        } else {
            mviewHolder.mAdWebView.setInitialScale(100);
        }
        // Log.d(TAG, "scale:" +
        // HomeApplication.getInstance().getResources().getInteger(R.integer.webview_init_scale));
        // mviewHolder.mAdWebView
        // .setInitialScale(HomeApplication.getInstance().getResources().getInteger(R.integer.webview_init_scale));
        mviewHolder.mAdWebView.getSettings().setJavaScriptEnabled(true);
        mviewHolder.mAdWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);

        mviewHolder.mAdWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        mviewHolder.mAdWebView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        mviewHolder.mAdWebView.getSettings().setDefaultTextEncodingName("utf-8");

        mviewHolder.mAdWebView.setHorizontalScrollBarEnabled(false);
        mviewHolder.mAdWebView.setVerticalScrollBarEnabled(false);
        final String USER_AGENT_STRING = mviewHolder.mAdWebView.getSettings().getUserAgentString() + " Rong/2.0";
        mviewHolder.mAdWebView.getSettings().setUserAgentString(USER_AGENT_STRING);
        mviewHolder.mAdWebView.getSettings().setSupportZoom(false);
        mviewHolder.mAdWebView.getSettings().setPluginState(WebSettings.PluginState.ON);
        mviewHolder.mAdWebView.getSettings().setLoadWithOverviewMode(true);

        mWebViewClien = new MyWebViewClient(mviewHolder.mAdWebView, mviewHolder.prgBar, this);
        mWebViewClien.setHomePage(loadURL);
        mviewHolder.mAdWebView.setWebViewClient(mWebViewClien);

        mviewHolder.mAdWebView.setFocusable(true);
        mviewHolder.mAdWebView.requestFocus();
        Log.v("adm", "mAdWebView.addJavascriptInterface");

        try {
            mviewHolder.mAdWebView.getClass().getMethod("onResume").invoke(mviewHolder.mAdWebView, (Object[]) null);
        } catch (IllegalArgumentException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
    }
    
    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // TODO Auto-generated method stub
        isRestart = true;
        mHandler.removeCallbacksAndMessages(null);
        Log.d(TAG, "onSaveInstanceState,Launcher is killed");
        super.onSaveInstanceState(outState);
    }

    public void setLoadURL(String loadURL) {
        this.loadURL = loadURL;
    }

    public void sendMonkey(int keycode) {
        try {
            String keyCommand = "input keyevent " + keycode;
            Runtime runtime = Runtime.getRuntime();
            runtime.exec(keyCommand);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public String GetAdText() {

        if (!Utils.isNetworkState) {
            Log.i("woo", "!Utils.isNet");
            mainLogic.mTxtAdsStrings = mainLogic.adtxtction.getmTxtAdsStrings();
            mainLogic.mTxtAdsDurations = mainLogic.adtxtction.getmAdDuration();
            Log.i("woo", "text" + Adposition + ",,,," + mainLogic.mTxtAdsStrings.length);
        }

        String text = mainLogic.mTxtAdsStrings[Adposition % mainLogic.mTxtAdsStrings.length];
        Log.i("woo", "text" + Adposition + ",,,," + mainLogic.mTxtAdsStrings.length);

        return text;
    }

    public void goToWebUrl(String url) {
        Intent intent = new Intent(HomeActivity.this, MyWebViewActivity.class);
        Bundle bundle = new Bundle();
        if (url == null || url.equals("")) {
            url = Constants.defaultURL;
        }
        bundle.putString("URL", url);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    Runnable handleLoadURL = new Runnable() {
        @Override
        public void run() {
            loadUrl();
        }
    };

    public int GetNewsNum() {
        return mviewHolder.mNewsFragment.GetNewsNum();
    }

    public int GetNewsLoop() {
        return mviewHolder.mNewsFragment.GetNewsLoop();
    }

    public Handler getHomeHandler() {
        return mHandler;
    }
}
