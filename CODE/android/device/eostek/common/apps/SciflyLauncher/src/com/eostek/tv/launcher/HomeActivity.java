
package com.eostek.tv.launcher;

import java.io.File;
import java.util.Locale;

import android.app.Activity;
import android.app.Instrumentation;
import android.app.ProgressDialog;
import android.appwidget.AppWidgetManager;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.res.Configuration;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.media.AudioManager;
import android.net.ConnectivityManager;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.Message;
import android.provider.Settings;
import android.util.Log;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.widget.ListView;
import android.widget.RelativeLayout;
import android.widget.Toast;
import com.eostek.tm.cpe.manager.CpeManager;


import com.eostek.tv.launcher.business.MetroJsonAction;
import com.eostek.tv.launcher.business.database.DBManager;
import com.eostek.tv.launcher.business.receiver.LocalChangedReceiver;
import com.eostek.tv.launcher.business.receiver.PackageReceiver;
import com.eostek.tv.launcher.business.receiver.TimeChangedReceiver;
import com.eostek.tv.launcher.business.receiver.WallPaperListener;
import com.eostek.tv.launcher.ui.UpgradeActivity;
import com.eostek.tv.launcher.ui.adapter.SourceAdapter;
import com.eostek.tv.launcher.ui.view.LauncherAppWidgetHost;
import com.eostek.tv.launcher.ui.view.ReflectionTView;
import com.eostek.tv.launcher.ui.view.WeatherView;
import com.eostek.tv.launcher.util.ATVListener;
import com.eostek.tv.launcher.util.DTVListener;
import com.eostek.tv.launcher.util.LConstants;
import com.eostek.tv.launcher.util.SettingsObserver;
import com.eostek.tv.launcher.util.TvUtils;
import com.eostek.tv.launcher.util.UIUtil;
import com.eostek.tv.launcher.util.WeatherUtil;
import com.ieostek.tms.feedback.tool.CommonUtil;
import com.ieostek.tms.upgrade.UpgradeBean;
import com.ieostek.tms.upgrade.intface.DefaultUpgradeImp;
import com.ieostek.tms.upgrade.intface.IUpgrade;
import com.mstar.android.tv.TvChannelManager;
import com.mstar.android.tv.TvCommonManager;
import com.mstar.android.tv.TvParentalControlManager;
import com.mstar.android.tvapi.common.TvManager;
import com.mstar.android.tvapi.common.exception.TvCommonException;
import com.mstar.android.tvapi.common.listener.OnTvEventListener;
import com.mstar.android.tvapi.common.vo.MuteType.EnumMuteType;
import com.mstar.android.tvapi.common.vo.ProgramInfo;
import com.mstar.android.tvapi.common.vo.TvOsType.EnumInputSource;
import com.nostra13.universalimageloader.core.ImageLoader;

/**
 * projectName锛�TVLauncher moduleName锛�HomeActivity.java
 * 
 * @author chadm.xiang
 * @version 1.0.0
 * @time 2014-8-29 涓婂崍10:48:36
 * @Copyright 漏 2014 Eos Inc.
 */
public class HomeActivity extends Activity {

    private final String TAG = HomeActivity.class.getSimpleName();

    private ViewHolder viewHolder;

    private MetroJsonAction metroJsonAction = null;

    /***************** change source *****************************************/
    private final Object bSync = new Object();

    public EnumInputSource toChangeInputSource;

    private InputSourceThread inputSourceThread = new InputSourceThread();

    // tv surface size state
    private SurfaceView surfaceView = null;

    private boolean hasTV = false;

    private volatile boolean bExitThread = false;

    private AppWidgetManager appWidgetManager;

    private LauncherAppWidgetHost appWidgetHost;

    private ProgressDialog mDialog;

    /** for change backgroup picture **/
    private static RelativeLayout mHomeLayout;

    private LocalChangedReceiver mLocalReceiver;

    private PackageReceiver mPackageReceiver;

    private TimeChangedReceiver mTimeReceiver;

    private final int mHostViewId = 1024;

    private static Bitmap blurBitmap;

    private final int CHECK_UPGRADE = 2;

    /** the flag whether start tv when start launcher,1 for yes,0 for no **/
    public int mTvRunningStat = -1;

    private IUpgrade upgrade;

    private UpgradeBean upgradeBean = new UpgradeBean();

    private HomeApplication mApplication;

    private TvEventListener tvEventListener;

    private WallPaperListener mWallListener;

    // the current local
    private Locale mLocal;

    // to save tmp locale when locale change
    private Locale mTmpLocal;

    private DTVListener mDtvPlayerEventListener;

    private ATVListener mAtvPlayerEventListener;
    
    private WeatherUtil mWeatherUtil;
    
    private SettingsObserver mSettingsObserver;
    
    private final String TV_PLAYER_828 = "com.eostek.tv.player.PlayerActivity";
    
    private final String FEATURE_628 = "EOS0NK200000TV00";
    
    private final String FEATURE_828 = "EOS0MUJI0000TV00";
    
    private  Instrumentation mInstrumentation;
    

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case LConstants.MSG_GET_UPDATE_DATA:
                    if (metroJsonAction != null) {
                        metroJsonAction.parsePgmJson();
                    }
                    break;
                case LConstants.UPDATE_METRO_DATA:
                    if (metroJsonAction != null) {
                        UIUtil.writeBooleanToXml(HomeActivity.this, "isLocaleChange", false);
                        Log.d(TAG, "mpagelists size:" + metroJsonAction.getMetroPageList().size());
                        if (metroJsonAction.getMetroPageList().size() > 0) {
                            showAutoDismissDialog(getResources().getString(R.string.update_network_data));
                            viewHolder.updateDataFromNetwork(metroJsonAction.getMetroPageList(),
                                    metroJsonAction.getJsonHeadBean(), true);
                        }
                    }
                    break;
                case LConstants.UPDATE_DATA_FAIL:
                    if (UIUtil.getBooleanFromXml(HomeActivity.this, "isLocaleChange")) {
                        Toast.makeText(HomeActivity.this, getResources().getString(R.string.update_network_fail),
                                Toast.LENGTH_LONG).show();
                    }
                    UIUtil.writeBooleanToXml(HomeActivity.this, "isLocaleChange", false);
                    break;
                case LConstants.MSG_DISMISS_DIALOG:
                    if (mDialog != null) {
                        mDialog.dismiss();
                        mDialog = null;
                    }
                    break;
                case LConstants.MSG_SHOW_TOAST:
                    if (UIUtil.isNetConnected(HomeActivity.this)) {
                        Toast.makeText(HomeActivity.this, getResources().getString(R.string.update_network_data),
                                Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(HomeActivity.this, getResources().getString(R.string.no_network),
                                Toast.LENGTH_LONG).show();
                    }
                    UIUtil.writeBooleanToXml(HomeActivity.this, "isLocaleChange", false);
                    break;
                case LConstants.MSG_GET_UPDATE_VERSION:
                    getUpgrade();
                    break;
                case LConstants.PACKAGE_ADDED:
                    break;
                case LConstants.WALL_PAPER_CHANGE:
                    break;
                case LConstants.MSG_CITY_CHANGE:
                case LConstants.GET_WEATHER_INFO:
                    Log.i(TAG, "LocalChangedReceiver.networkConnected:" + LocalChangedReceiver.networkConnected);
                    if (LocalChangedReceiver.networkConnected == true) {
                        mWeatherUtil.updateWeatherInfo();
                    } else {
                        WeatherView.hideWeatherView();
                    }
                    break;
                case LConstants.UPDATE_WEATHER_VIEW:
                    WeatherView.showWeatherView(getApplicationContext());
                    break;
                case LConstants.UPDATE_WEATHER_TIME_VIEW:
                    WeatherView.updateWeatherTime(getApplicationContext());
                    break;
                case LConstants.HIDE_WEATHER_VIEW:
                    WeatherView.hideWeatherView();
                    break;
                default:
                    break;
            }
        }
    };

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        getWindow().setFlags(WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN);
        setContentView(R.layout.activity_home);

        // add for change language ,the Activity will call destroy methon in
        // onDestroy ,then call onCreate,we should make sure ImageLoader is
        // inited
        if (!ImageLoader.getInstance().isInited()) {
            HomeApplication.initImageLoader(getApplicationContext());
        }
        mHomeLayout = (RelativeLayout) findViewById(R.id.home_content);

        // add start setup
        //launchTutorial();

        // check whether to reset ETag
        resetEtag();

        hasTV = HomeApplication.isHasTVModule();
        if (hasTV) {
            mApplication = HomeApplication.getInstance();
            bExitThread = false;

            toChangeInputSource = EnumInputSource.E_INPUT_SOURCE_NONE;
            TvParentalControlManager.getInstance().setSystemLock(true);
            
            new Thread(inputSourceThread).start();

            tvEventListener = new TvEventListener(this);
            TvManager.getInstance().setOnTvEventListener(tvEventListener);
        }

        appWidgetManager = AppWidgetManager.getInstance(this);
        appWidgetHost = new LauncherAppWidgetHost(this, mHostViewId);
        appWidgetHost.startListening();

        viewHolder = new ViewHolder(HomeActivity.this, mHandler);
        viewHolder.findViews();
        viewHolder.initDataFromLocal(UIUtil.getLanguage());
        UIUtil.setDefaultBackground(HomeActivity.this, mHomeLayout);

        metroJsonAction = new MetroJsonAction(this, mHandler);
        mLocalReceiver = new LocalChangedReceiver(this, mHandler);
        IntentFilter intentFilter = new IntentFilter();
        intentFilter.addAction(Intent.ACTION_LOCALE_CHANGED);
        intentFilter.addAction(ConnectivityManager.CONNECTIVITY_ACTION);
        registerReceiver(mLocalReceiver, intentFilter);

        // register application install and uninstall
        mPackageReceiver = new PackageReceiver(this, mHandler);
        IntentFilter intentFilter2 = new IntentFilter();
        intentFilter2.addAction(Intent.ACTION_PACKAGE_ADDED);
        intentFilter2.addAction(Intent.ACTION_PACKAGE_REMOVED);
        intentFilter2.addAction(Intent.ACTION_PACKAGE_REPLACED);
        intentFilter2.addDataScheme("package");
        registerReceiver(mPackageReceiver, intentFilter2);

        mTimeReceiver = new TimeChangedReceiver(this, mHandler);
        IntentFilter intentFilter3 = new IntentFilter();
        intentFilter3.addAction(Intent.ACTION_TIME_TICK);
        intentFilter3.addAction(Intent.ACTION_TIME_CHANGED);
        intentFilter3.addAction(Intent.ACTION_TIMEZONE_CHANGED);
        registerReceiver(mTimeReceiver, intentFilter3);
        
        // mWallListener = new WallPaperListener(mHandler);
        // registerReceiver(mWallListener, new
        // IntentFilter(Intent.ACTION_WALLPAPER_CHANGED));
        
        if (mWeatherUtil == null) {
            mWeatherUtil = new WeatherUtil(this, mHandler);
        }
        if (mSettingsObserver == null) {
            mSettingsObserver = new SettingsObserver(mHandler, this);
            mSettingsObserver.observe(this);
        }

        if (LConstants.DEBUG) {
            Log.i(TAG, " Name:TVLauncher,Version:1.2.2,Date:2015-02-05,Publisher:chadm.xiang,REV:25376");
        }

        Configuration config = getResources().getConfiguration();
        mLocal = config.locale;
        Log.v(TAG, "onCreate " + config.toString());
        
        mInstrumentation = new Instrumentation();
        
        /**
         * handle audio problem.
         */
        /*AudioManager audioManager = (AudioManager) getSystemService(Context.AUDIO_SERVICE);
        //audioManager.setMasterMute(false);
        com.mstar.android.tvapi.common.AudioManager am = TvManager.getInstance().getAudioManager();
        if (am != null) {
            try {
                am.disableMute(EnumMuteType.E_MUTE_ALL);
            } catch (TvCommonException e) {
                e.printStackTrace();
            }
        }*/
    }

    @Override
    protected void onStart() {
        super.onStart();
        Log.i(TAG, "onStart ");
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (hasTV && handlertv != null) {
            Log.i(TAG, "onRestart handlerMute !");
            handlertv.removeCallbacks(handlerMute);
            //handlertv.postDelayed(handlerMute, 5000);
        }
        if (surfaceView != null) {
            surfaceView.setBackgroundColor(Color.TRANSPARENT);
        }
        Log.i(TAG, "onRestart ");
    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.i(TAG, "cursource1 = " + TvCommonManager.getInstance().getCurrentInputSource());
        int source = TvCommonManager.getInstance().getCurrentTvInputSource();
        TvCommonManager.getInstance().setInputSource(source);
        
        System.gc();
        Log.v(TAG, "getmCurTitleIndex = " + viewHolder.getmCurTitleIndex());

        UIUtil.setDefaultBackground(this, mHomeLayout);
        UIUtil.setSystemUIVisible(this, 0);
        if (UIUtil.getBooleanFromXml(HomeActivity.this, "isLocaleChange")) {
            mHandler.sendEmptyMessageDelayed(LConstants.MSG_SHOW_TOAST, 2 * 1000);
            mHandler.sendEmptyMessageDelayed(LConstants.MSG_GET_UPDATE_DATA, 15 * 1000);
            showAutoDismissDialog(getResources().getString(R.string.update_locale_data));
        }
        sendWidgetBroadCast();
        // every time back to launcher,check memory
        if (UIUtil.shouldKillMemory(this)) {
            UIUtil.killProcesses();
            Log.v(TAG, "kill backgroud Processes");
        }
        // only in my tv,change surfaceview
        if (hasTV) {
            if (TvChannelManager.getInstance() != null) {
                mDtvPlayerEventListener = new DTVListener(viewHolder);
                TvChannelManager.getInstance().registerOnDtvPlayerEventListener(mDtvPlayerEventListener);
                mAtvPlayerEventListener = new ATVListener(viewHolder);
                TvChannelManager.getInstance().registerOnAtvPlayerEventListener(mAtvPlayerEventListener);
            }
            Log.v(TAG, "" + TvCommonManager.getInstance().getCurrentInputSource());
//            mTvRunningStat = Settings.System.getInt(getContentResolver(), "tvplayer_visible", 0);
            Log.v(TAG, "mTvRunningStat = " + mTvRunningStat);
            // set the tag to show that we are in launcher,the flag will be used
            // in HotKey, to decide whether start tv or launcher or do nothing
            android.provider.Settings.System.putInt(getContentResolver(), "tvapp", 1);
//            if (mTvRunningStat == 1) {
//                startTV();
//            } else {
                if (viewHolder.getmCurTitleIndex() == 0) {
                    String clsString = TvUtils.getCurrentActivity(HomeActivity.this);
                    Log.v(TAG, "onresume clsString = " + clsString);
                    // to avoid blue screen when start tv apk
                    if (HomeActivity.class.getName().equals(clsString)) {
                        mApplication.addSourceTask(new Runnable() {

                            @Override
                            public void run() {
                                setPowerOnSource("onResume");
                            }
                        });
                    }
                    if (surfaceView == null) {
                        surfaceView = viewHolder.getMsurfaceView();
                        handlertv.postDelayed(handlerRuntv, 3000);
                    } else {
                        Log.d(TAG, "onResume  surfaceView != null:");
                        handlertv.postDelayed(handlerRuntv, 2000);
                    }
                    surfaceView.setBackgroundColor(Color.BLACK);
                    //TvUtils.pageChangeMute(this, false);
                    TvCommonManager.getInstance().setInputSource(EnumInputSource.E_INPUT_SOURCE_STORAGE.ordinal());
                } else {
                    // if not in my tv ,remove handlerMute
                   /* handlertv.removeCallbacks(handlerMute);
                    handlertv.postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            String clsString = TvUtils.getCurrentActivity(HomeActivity.this);
                            if (HomeActivity.class.getName().equals(clsString)) {
                                TvUtils.setSmallscale();
                            }
                        }
                    }, 2500);
                    TvUtils.pageChangeMute(this, true);*/
                }

                ReflectionTView mRelativeTvView = viewHolder.getmRelativeTvView();
                mRelativeTvView.setSourceText(TvUtils.getCurInputSourcePosition(this));
//            }
        }
    }

    @Override
    protected void onNewIntent(Intent intent) {
        Log.d(TAG, "onNewIntent:" + intent);
        super.onNewIntent(intent);
    }

    @Override
    protected void onPause() {
        Log.v(TAG, "onPause");
        if (hasTV) {
            Log.d(TAG, "TvUtils.getCurrentActivity:" + TvUtils.getCurrentActivity(this));
            handlertv.removeCallbacks(handlerRuntv);
            if (TvChannelManager.getInstance() != null) {
                try {
                    if (mDtvPlayerEventListener != null) {
                        TvChannelManager.getInstance().unregisterOnDtvPlayerEventListener(mDtvPlayerEventListener);
                    }
                    if (mAtvPlayerEventListener != null) {
                        TvChannelManager.getInstance().unregisterOnAtvPlayerEventListener(mAtvPlayerEventListener);
                    }
                } catch (NullPointerException e) {
                    Log.e(TAG, "NullPointerException when unregisterOnDtvPlayerEventListener");
                }
            }
        }
        super.onPause();
    }

    @Override
    protected void onStop() {
        super.onStop();
        Log.v(TAG, "onStop");
        if (hasTV) {
            android.provider.Settings.System.putInt(getContentResolver(), "tvapp", 0);
            Log.d(TAG, "TvUtils.getCurrentActivity:" + TvUtils.getCurrentActivity(this));
            String className = TvUtils.getCurrentActivity(this);
            boolean category = !className.equals(LConstants.TV_PLAYER_CLS) && !className.equals(LConstants.MTA_CLS);
            if (category) {
                TvCommonManager.getInstance().setInputSource(EnumInputSource.E_INPUT_SOURCE_STORAGE.ordinal());
            }
            Log.v(TAG, "mTvRunningStat = " + mTvRunningStat);
            if (mTvRunningStat != 1) {
                // if the activity is invisiable,disable mute
                //TvUtils.setMute(this, false);
            }
        }
        UIUtil.setSystemUIVisible(this, 1);
        if (ImageLoader.getInstance().isInited()) {
            ImageLoader.getInstance().clearMemoryCache();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (hasTV) {
            TvManager.getInstance().setOnTvEventListener(null);
            surfaceView = null;
        }
        mHomeLayout.removeAllViews();
        mHomeLayout = null;
        mHandler.removeMessages(LConstants.UPDATE_METRO_DATA);
        viewHolder.releaseResources();

        if (mLocalReceiver != null) {
            unregisterReceiver(mLocalReceiver);
        }
        if (mPackageReceiver != null) {
            unregisterReceiver(mPackageReceiver);
        }
        if (mWallListener != null) {
            unregisterReceiver(mWallListener);
        }

        if (blurBitmap != null) {
            blurBitmap.recycle();
            blurBitmap = null;
        }
        if (appWidgetHost != null) {
            appWidgetHost.stopListening();
        }
        if (mDialog != null) {
            mDialog.dismiss();
            mDialog = null;
        }
        bExitThread = true;
        if (ImageLoader.getInstance().isInited()) {
            ImageLoader.getInstance().clearMemoryCache();
            ImageLoader.getInstance().destroy();
        }
        if (metroJsonAction != null) {
            metroJsonAction.stopThread();
        }
        if (mSettingsObserver != null) {
            mSettingsObserver.unregisterContentObserver();
        }
        Log.v(TAG, "onDestroy ");
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        if (event.getAction() == KeyEvent.ACTION_DOWN) {
            final int code = keyCode;
            Log.v(TAG, "keyCode = " + keyCode);
            switch (keyCode) {
                case KeyEvent.KEYCODE_BACK:
                    return true;
                case KeyEvent.KEYCODE_VOLUME_MUTE:
                case KeyEvent.KEYCODE_VOLUME_DOWN:
                case KeyEvent.KEYCODE_VOLUME_UP:
                    mHandler.postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            if (hasTV) {
                                if (viewHolder.getmCurTitleIndex() == 0) {
                                    TvUtils.setMute(mApplication, false);
                                } else {
                                    // in other page except my tv,only response
                                    // mute key
                                    if (code == KeyEvent.KEYCODE_VOLUME_MUTE) {
                                        TvUtils.setMute(mApplication, true);
                                    }
                                }
                            }
                        }
                    }, 100);
                    break;
                    
                case KeyEvent.KEYCODE_DPAD_DOWN:
                	if(LConstants.FEATURE_BENQ_828.equals(UIUtil.getSpecialCode())){
                		Log.d(TAG,"platform is BenQ 828");
                		if(Settings.System.getInt(HomeActivity.this.getContentResolver(), "bottomimg", 0) == 1){
                    		Settings.System.putInt(HomeActivity.this.getContentResolver(), "bottomimg", 0);
                    		mApplication.addNetworkTask(new Runnable() {
                                @Override
                                public void run() {
                                	mInstrumentation.sendKeyDownUpSync(KeyEvent.KEYCODE_DPAD_UP);
                                }
                            });
                    	}
                	}
                	break;
                	
                case KeyEvent.KEYCODE_DPAD_RIGHT:
                	if(LConstants.FEATURE_BENQ_828.equals(UIUtil.getSpecialCode())){
                		Log.d(TAG,"platform is BenQ 828");
                		if(viewHolder.titGridView.hasFocus()){
                    		viewHolder.viewPager.setCurrentItem(0);
                        	viewHolder.titGridView.setSelection(0);
                    	}else{
                    		viewHolder.setTitleGridViewBackgroud(0);
                    		viewHolder.viewPager.setCurrentItem(0);
                    		viewHolder.titGridView.setSelection(0);
                    		viewHolder.vpAdapter.scrollToOrigin(0);
                    	}
                	}
                
                	break;
                	
                case KeyEvent.KEYCODE_CHANNEL_UP :
                	Log.e(TAG, "KEYCODE_CHANNEL_UP");
                	mApplication.addNetworkTask(new Runnable() {
                        @Override
                        public void run() {
                        	mInstrumentation.sendKeyDownUpSync(KeyEvent.KEYCODE_DPAD_RIGHT);
                        }
                    });
                	break;
                	
                case KeyEvent.KEYCODE_CHANNEL_DOWN :
                	Log.e(TAG, "KEYCODE_CHANNEL_DOWN");
                	mApplication.addNetworkTask(new Runnable() {
                        @Override
                        public void run() {
                        	Instrumentation in = new Instrumentation();
                            in.sendKeyDownUpSync(KeyEvent.KEYCODE_DPAD_DOWN);
                        }
                    });
                	break;
                	
                case KeyEvent.KEYCODE_MENU:
                	mApplication.addNetworkTask(new Runnable() {
                        @Override
                        public void run() {
                        	Instrumentation in = new Instrumentation();
                            in.sendKeyDownUpSync(KeyEvent.KEYCODE_ENTER);
                        }
                    });
                	break;
                	
                default:
                    break;
            }
        }
        return super.onKeyDown(keyCode, event);
    }

    @Override
    public boolean dispatchKeyEvent(KeyEvent event) {
        return super.dispatchKeyEvent(event);
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        Log.v(TAG, "onWindowFocusChanged hasFocus = " + hasFocus);
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        super.onConfigurationChanged(newConfig);
        Log.v(TAG, "onConfigurationChanged " + newConfig);
        mHandler.sendEmptyMessage(LConstants.MSG_CITY_CHANGE);
        mTmpLocal = newConfig.locale;
        if (!mLocal.getCountry().equals(mTmpLocal.getCountry())) {
            mLocal = mTmpLocal;
            Log.v(TAG, "onConfigurationChanged local change");
            UIUtil.writeBooleanToXml(HomeActivity.this, "isLocaleChange", true);
            viewHolder.setCurTitleIndex(0);
            mHandler.postDelayed(new Runnable() {

                @Override
                public void run() {
                    viewHolder.reloadLocalData();
                }
            }, 1000);
        }
    }

    @Override
    public void onTrimMemory(int level) {
        super.onTrimMemory(level);
        Log.v(TAG, "level " + level);
    }

    /**
     * get the current view as a bitmap
     * 
     * @return the current view bitmap
     */
    public static Bitmap getCurrentBackground() {
        mHomeLayout.setDrawingCacheEnabled(true);
        blurBitmap = mHomeLayout.getDrawingCache();
        return blurBitmap;
    }

    /**
     * reset input source when resume
     */
    public void backHomeSource() {
        Log.i(TAG, "BackHomeSource------------ bSync = " + bSync);
        synchronized (bSync) {
            EnumInputSource curSource = TvCommonManager.getInstance().getCurrentInputSource();
            if (curSource == EnumInputSource.E_INPUT_SOURCE_STORAGE) {
                curSource = EnumInputSource.values()[TvUtils.queryCurInputSrc(HomeApplication.getInstance())];
            }
            toChangeInputSource = curSource;
        }
    }

    /**
     * handler the source change when page change or source listview item
     * selected
     * 
     * @param source The source to set
     * @param delay The time to delay
     */
    public void sourceChageHandle(final EnumInputSource source, long delay) {
        handlertv.removeCallbacks(rSourceRunnable);
        if (surfaceView != null) {
            surfaceView.setBackgroundColor(Color.BLACK);
        }
        rSourceRunnable = new SourceRunnable(source);
        handlertv.postDelayed(rSourceRunnable, delay);
    }

    SourceRunnable rSourceRunnable;

    class SourceRunnable implements Runnable {

        private EnumInputSource source;

        public SourceRunnable(EnumInputSource sInputSource) {
            this.source = sInputSource;
        }

        /*
         * (non-Javadoc)
         * @see java.lang.Runnable#run()
         */
        @Override
        public void run() {
            if (surfaceView != null) {
                surfaceView.setBackgroundColor(Color.TRANSPARENT);
                surfaceView.setVisibility(View.VISIBLE);
            }
            mApplication.addSourceTask(new Runnable() {

                @Override
                public void run() {
                    TvCommonManager.getInstance().setInputSource(source.ordinal());
                    TvUtils.setSmallscale();
                    EnumInputSource curSource = EnumInputSource.values()[TvUtils.queryCurInputSrc(HomeApplication
                            .getInstance())];
                    if (curSource == EnumInputSource.E_INPUT_SOURCE_ATV
                            || curSource == EnumInputSource.E_INPUT_SOURCE_DTV) {
                        try {
                            ProgramInfo pinfo = TvUtils.getCurrProgramInfo();
                            TvUtils.programSel(pinfo.number, pinfo.serviceType);
                        } catch (Exception e) {
                            Log.w(TAG, "fail to gain programinfo");
                        }
                    }
                    // if not in my tv page,set enable mute
                    if (viewHolder == null || viewHolder.getmCurTitleIndex() != 0) {
                        //TvUtils.setMute(mApplication, true);
                        TvCommonManager.getInstance().setInputSource(EnumInputSource.E_INPUT_SOURCE_STORAGE.ordinal());
                    }
                }
            });
        }

    }

    public AppWidgetManager getAppWidgetManager() {
        return appWidgetManager;
    }

    public LauncherAppWidgetHost getAppWidgetHost() {
        return appWidgetHost;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        Log.v(TAG, "requestCode = " + requestCode + "; resultCode = " + resultCode);
        switch (requestCode) {
            case CHECK_UPGRADE:
                if (resultCode == RESULT_CANCELED) {
                    // if do not start download new version send message to get
                    // network data
                    mHandler.removeMessages(LConstants.MSG_GET_UPDATE_DATA);
                    mHandler.sendEmptyMessageDelayed(LConstants.MSG_GET_UPDATE_DATA, 5 * 1000);
                }
                break;

            default:
                break;
        }
    }

    /**
     * check upgrade,if there is a new version ,start UpgradeActivity,else send
     * message to get UI update data
     */
    private void getUpgrade() {
        Log.i(TAG, "upgrade()...");
        mHandler.postDelayed(new Runnable() {
            @Override
            public void run() {
                upgrade = new DefaultUpgradeImp();
                String packName = getPackageName();
                int versionCode = UIUtil.getVersionCode(HomeActivity.this);
                Log.i(TAG, "packName::" + packName + ", versionCode::" + versionCode);
                init(UIUtil.getBBCode(), UIUtil.getSpecialCode(), 2, Environment.getExternalStorageDirectory()
                        .getPath() + "/Download/", versionCode, packName, upgrade);
                new Thread(new Runnable() {
                    @Override
                    public void run() {
                        // hide upgrade function
                        // boolean isUpgrade = upgrade.checkUpgrade();
                        boolean isUpgrade = false;
                        Log.e(TAG, "isUpgrade::" + isUpgrade);
                        if (isUpgrade) {
                            Intent intent = new Intent(HomeActivity.this, UpgradeActivity.class);
                            intent.putExtra("newversionname", upgradeBean.getNewVersionName());
                            intent.putExtra("newversionfilesize", upgradeBean.getNewVersionFileSize());
                            intent.putExtra("newversiondesc", upgradeBean.getNewVersionDesc());
                            startActivityForResult(intent, CHECK_UPGRADE);
                            Log.v(TAG, "ver = " + upgradeBean.getNewVersionCode());
                        } else {
                            mHandler.removeMessages(LConstants.MSG_GET_UPDATE_DATA);
                            mHandler.sendEmptyMessageDelayed(LConstants.MSG_GET_UPDATE_DATA, 5 * 1000);
                        }
                    }
                }).start();
            }
        }, 10000);
    }

    /**
     * init upgrade data
     * 
     * @param bbNo bb number
     * @param specialNo Special number
     * @param installType Install type
     * @param dowloadPath Download path
     * @param versionCode the version code
     * @param packName The package name
     * @param upgrad The IUpgrade object
     */
    public void init(String bbNo, String specialNo, int installType, String dowloadPath, int versionCode,
            String packName, IUpgrade upgrad) {
        upgradeBean.setServerUrl(LConstants.UPGRADE_SERVER_URL);
        upgradeBean.setPkgName(packName);
        upgradeBean.setCurrVersionCode(versionCode);
        upgradeBean.setBbNO(bbNo);
        upgradeBean.setDeviceCode(specialNo);
        upgradeBean.setInstallTyle(installType);
        upgrad.init(this, upgradeBean);
        if (dowloadPath == null || dowloadPath.equals("")) {
            upgradeBean.setDownLoadPath(getFilesDir().getParent() + "/" + upgradeBean.getPkgName());
        } else {
            upgradeBean.setDownLoadPath(dowloadPath);
        }
        if (this.upgradeBean.getSysVersion() <= 0) {
            upgradeBean.setSysVersion(CommonUtil.getOsVersionInt());
        }
        cleanDownloadCache();
    }

    /**
     * show dialog when udpate ui interface to avoid any movement when ui
     * change,the dialog will dismiss after
     * {@link LConstants#UPDATE_METRO_DELAY_TIME}
     * 
     * @param text The message to show
     */
    private void showAutoDismissDialog(String text) {
        if (mDialog == null) {
            mDialog = new ProgressDialog(HomeActivity.this, android.R.style.Theme_Translucent);
        }
        // set progress style
        mDialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        mDialog.setMessage(text);
        // set progress Indeterminate
        mDialog.setIndeterminate(false);
        // do not response back key
        mDialog.setCancelable(false);
        mDialog.getWindow().setGravity(Gravity.CENTER);
        mDialog.getWindow().setLayout(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT);
        mDialog.show();
        // viewHolder.setImageResouce();
        mHandler.sendEmptyMessageDelayed(LConstants.MSG_DISMISS_DIALOG, LConstants.UPDATE_METRO_DELAY_TIME);
    }

    /**
     * clear downlado cache
     */
    private boolean cleanDownloadCache() {
        File file = new File(upgradeBean.getDownLoadPath() + "/" + upgradeBean.getPkgName() + ".apk");
        return file.delete();
    }

    /**
     * to make sure that:every time update,launcher can get data from network to
     * refresh UI
     */
    private void resetEtag() {
        try {
            int curVersion = UIUtil.getVersionCode(HomeActivity.this);
            // get former version code
            String versionCode = UIUtil.getStringFromXml(HomeActivity.this, "versionCode");
            int formerVersion = 0;
            if (versionCode != null) {
                formerVersion = Integer.valueOf(versionCode);
            }
            // reset the etag when update
            boolean isNewVersion = curVersion > formerVersion ? true : false;
            if (isNewVersion) {
                UIUtil.writeStringToXml(HomeActivity.this, "versionCode", String.valueOf(curVersion));
                DBManager.getDbManagerInstance(HomeApplication.getInstance()).resetETag(UIUtil.getLanguage());
                Log.d(TAG, "run resetEtag");
            }
        } catch (Exception e) {
            Log.e(TAG, "run resetEtag error");
        }
    }

    /**
     * start setmeup when first time launch
     */
    private void launchTutorial() {
        try {
            // add for problem 0013488
            File file = new File(getFilesDir().getParent() + "/" + "shared_prefs/launcher.xml");
            boolean fileExist = file.exists();
            if (LConstants.DEBUG) {
                Log.v(TAG, "launcher.xml exists = " + fileExist);
            }
            
            boolean setmeup = UIUtil.getBooleanFromXml(HomeActivity.this, "setmeup");
            if (setmeup) {
                return;
            }
            UIUtil.writeBooleanToXml(HomeActivity.this, "setmeup", true);

            ComponentName componentName = new ComponentName("com.eostek.isynergy.setmeup",
                    "com.eostek.isynergy.setmeup.ui.SetmeupMainActivity");
            Intent intent = new Intent(Intent.ACTION_MAIN);
            intent.addCategory(Intent.CATEGORY_LAUNCHER);
            intent.setComponent(componentName);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            startActivity(intent);
            Log.d(TAG, "run com.eostek.isynergy.setmeup");
        } catch (Exception e) {
            Log.e(TAG, "run com.eostek.isynergy.setmeup");
        }
    }

    // handlertv postDelayed chanage source
    Handler handlertv = new Handler();

    Runnable handlerRuntv = new Runnable() {
        @Override
        public void run() {
            initSurfaceView();
        }
    };

    Runnable handlerMute = new Runnable() {
        @Override
        public void run() {
            if (viewHolder != null && viewHolder.getmCurTitleIndex() == 0) {
                //TvUtils.setMute(HomeActivity.this, false);
                TvCommonManager.getInstance().setInputSource(EnumInputSource.E_INPUT_SOURCE_STORAGE.ordinal());
            }
        }
    };

    /**
     * inint Surface view
     * 
     * @param void
     * @return
     */
    private void initSurfaceView() {
        Log.v(TAG, "initSurfaceView");
        if (surfaceView != null) {
            surfaceView.setBackgroundColor(Color.TRANSPARENT);
            surfaceView.setVisibility(View.VISIBLE);
            Log.v(TAG, "set visible and transparent");
        }
        TvUtils.setSmallscale();
        EnumInputSource curSource = EnumInputSource.values()[TvUtils.queryCurInputSrc(HomeApplication.getInstance())];
        if (viewHolder != null) {
            // only in my tv select program,because the voice will disablemute
            // when select program
            if (viewHolder.getmCurTitleIndex() == 0) {
                if (curSource == EnumInputSource.E_INPUT_SOURCE_ATV || curSource == EnumInputSource.E_INPUT_SOURCE_DTV) {
                    ProgramInfo pinfo = TvUtils.getCurrProgramInfo();
                    TvUtils.programSel(pinfo.number, pinfo.serviceType);
                }
            }
            ReflectionTView mRelativeTvView = viewHolder.getmRelativeTvView();
            mRelativeTvView.setSourceText(TvUtils.getCurInputSourcePosition(curSource));
        }
    }

    /**
     * set power on source when boot
     * 
     * @param from The method to call setPowerOnSource
     */
    private void setPowerOnSource(String from) {
        Log.v(TAG, from + " call getPowerOnSource");
        int curSourceID = TvCommonManager.getInstance().getPowerOnSource().ordinal();
        if ((curSourceID >= 0) && (curSourceID <= EnumInputSource.E_INPUT_SOURCE_NONE.ordinal())) {
            TvCommonManager.getInstance().setInputSource(EnumInputSource.values()[curSourceID].ordinal());
        }
    }

    /**
     * start tv when the format power off is in tv
     */
    private void startTV() {
        Settings.System.putInt(getContentResolver(), "tvplayer_visible", 0);
        Log.d(TAG, " set tvplayer_visible to 0");
        TvUtils.startTV(getApplicationContext());
    }

    /**
     * send broadcast to update widgets whick load in launcher onresume
     */
    private void sendWidgetBroadCast() {
        final Intent intent = new Intent();
        intent.setAction("com.eostek.wasuwidgethost.updatewidget");
        String cacheKey = "com.eostek.wasuwidgethost.CacheHandleProvider";
        String weatherKey = "com.eostek.wasuwidgethost.WeatherWidgetProvider";
        String cacheId = UIUtil.getStringFromXml(getApplicationContext(), cacheKey);
        String weatherId = UIUtil.getStringFromXml(getApplicationContext(), weatherKey);
        intent.putExtra("cacheId", cacheId);
        intent.putExtra("weatherId", weatherId);
        sendBroadcast(intent);
    }

    /**
     * The thread to handle input source changes
     */
    class InputSourceThread implements Runnable {
        @Override
        public void run() {
            // to avoid blue screen when start tv apk
            if (mTvRunningStat != 1) {
                setPowerOnSource("InputSourceThread");
            }
            EnumInputSource tmpInputSource;
            while (!bExitThread) {
                synchronized (bSync) {
                    tmpInputSource = toChangeInputSource;
                    toChangeInputSource = EnumInputSource.E_INPUT_SOURCE_NONE;
                }
                if (tmpInputSource != EnumInputSource.E_INPUT_SOURCE_NONE) {
                    EnumInputSource currentSource;
                    if (tmpInputSource == EnumInputSource.E_INPUT_SOURCE_DTV
                            || tmpInputSource == EnumInputSource.E_INPUT_SOURCE_ATV) {
                        currentSource = TvCommonManager.getInstance().getCurrentInputSource();
                        if (currentSource == EnumInputSource.E_INPUT_SOURCE_STORAGE) {
                            EnumInputSource curSource = EnumInputSource.values()[TvUtils
                                    .queryCurInputSrc(HomeApplication.getInstance())];
                            TvCommonManager.getInstance().setInputSource(curSource.ordinal());
                            // setSmallscale();
                            TvUtils.setSmallscale();
                            if (viewHolder != null && viewHolder.getmCurTitleIndex() == 0) {
                                if (curSource == EnumInputSource.E_INPUT_SOURCE_ATV
                                        || curSource == EnumInputSource.E_INPUT_SOURCE_DTV) {
                                    ProgramInfo pinfo = TvUtils.getCurrProgramInfo();
                                    TvUtils.programSel(pinfo.number, pinfo.serviceType);
                                }
                            }
                        } else {
                            TvUtils.setSmallscale();
                            if (viewHolder != null && viewHolder.getmCurTitleIndex() == 0) {
                                ProgramInfo pinfo = TvUtils.getCurrProgramInfo();
                                TvUtils.programSel(pinfo.number, pinfo.serviceType);
                            }
                        }
                    } else {
                        EnumInputSource curSource = TvCommonManager.getInstance().getCurrentInputSource();
                        // if the current source is the source to set,no need to
                        // set again
                        if (curSource != tmpInputSource) {
                            TvCommonManager.getInstance().setInputSource(tmpInputSource.ordinal());
                        }
                        if (mTvRunningStat != 1) {
                            TvUtils.setSmallscale();
                        }
                    }
                }

                try {
                    Thread.sleep(200);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    private class TvEventListener implements OnTvEventListener {

        @SuppressWarnings("unused")
        private Context mContext;

        public TvEventListener(Context context) {
            mContext = context;
        }

        @Override
        public boolean onUnityEvent(int arg0, int arg1, int arg2) {
            try {
                Thread.sleep(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
            android.util.Log.i(TAG, "onUnityEvent");
            ReflectionTView reflectionTView = null;
            ListView sourceListView = null;
            if (null != viewHolder) {
                reflectionTView = viewHolder.getmRelativeTvView();
                if (null != reflectionTView) {
                    sourceListView = reflectionTView.getSourceListView();
                }
                // when onUnityEvent called,change the show source text
                ReflectionTView mRelativeTvView = viewHolder.getmRelativeTvView();
                if (mRelativeTvView != null) {
                    mRelativeTvView.setSourceText(TvUtils.getCurInputSourcePosition(mApplication));
                }
            }
            if (null != sourceListView) {
                SourceAdapter sa = ((SourceAdapter) (sourceListView.getAdapter()));
                boolean[] signalStatus = TvCommonManager.getInstance().GetInputSourceStatus();
                sa.signalStatus[2] = signalStatus[EnumInputSource.E_INPUT_SOURCE_HDMI.ordinal()]; // HDMI1
                sa.signalStatus[3] = signalStatus[EnumInputSource.E_INPUT_SOURCE_HDMI2.ordinal()]; // HDMI2
                sa.signalStatus[4] = signalStatus[EnumInputSource.E_INPUT_SOURCE_HDMI3.ordinal()]; // HDMI3
                sa.signalStatus[5] = signalStatus[EnumInputSource.E_INPUT_SOURCE_CVBS.ordinal()]; // AV
                sa.signalStatus[6] = signalStatus[EnumInputSource.E_INPUT_SOURCE_YPBPR.ordinal()]; // YPBPR
                sa.signalStatus[7] = signalStatus[EnumInputSource.E_INPUT_SOURCE_VGA.ordinal()]; // VGA
                sa.notifyDataSetChanged();
            }
            return false;
        }

        @Override
        public boolean on4k2kHDMIDisableDualView(int arg0, int arg1, int arg2) {
            return false;
        }

        @Override
        public boolean on4k2kHDMIDisablePip(int arg0, int arg1, int arg2) {
            return false;
        }

        @Override
        public boolean on4k2kHDMIDisablePop(int arg0, int arg1, int arg2) {
            return false;
        }

        @Override
        public boolean on4k2kHDMIDisableTravelingMode(int arg0, int arg1, int arg2) {
            return false;
        }

        @Override
        public boolean onAtscPopupDialog(int arg0, int arg1, int arg2) {
            return false;
        }

        @Override
        public boolean onDeadthEvent(int arg0, int arg1, int arg2) {
            return false;
        }

        @Override
        public boolean onDtvReadyPopupDialog(int arg0, int arg1, int arg2) {
            return false;
        }

        @Override
        public boolean onScartMuteOsdMode(int arg0) {
            return false;
        }

        @Override
        public boolean onScreenSaverMode(int arg0, int arg1, int arg2) {
            return false;
        }

        @Override
        public boolean onSignalLock(int arg0) {
            Log.v(TAG, "onSignalLock");
            return false;
        }

        @Override
        public boolean onSignalUnlock(int arg0) {
            Log.v(TAG, "onSignalUnlock");
            return false;
        }
    }

}
