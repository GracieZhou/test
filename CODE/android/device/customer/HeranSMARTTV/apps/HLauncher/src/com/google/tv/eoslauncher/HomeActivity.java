
package com.google.tv.eoslauncher;

import java.lang.reflect.InvocationTargetException;
import java.util.List;

import scifly.provider.SciflyStore;
import scifly.provider.metadata.Usr;
import android.app.Activity;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.ResolveInfo;
import android.content.res.Configuration;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.provider.Settings;
import android.provider.Settings.SettingNotFoundException;
import android.util.Log;
import android.view.GestureDetector;
import android.view.IWindowManager;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.webkit.WebView;
import android.widget.ProgressBar;
import android.widget.RelativeLayout;

import com.google.tv.eoslauncher.business.AdAction;
import com.google.tv.eoslauncher.business.AdAppStoreAction;
import com.google.tv.eoslauncher.business.AdTextAction;
import com.google.tv.eoslauncher.business.AppAction;
import com.google.tv.eoslauncher.business.NetworkReceiver;
import com.google.tv.eoslauncher.business.db.DBHelper;
import com.google.tv.eoslauncher.model.AppInfoBean;
import com.google.tv.eoslauncher.model.MyAD;
import com.google.tv.eoslauncher.ui.HelperActivity;
import com.google.tv.eoslauncher.util.ChannelManagerExt;
import com.google.tv.eoslauncher.util.Constants;
import com.google.tv.eoslauncher.util.UIUtil;
import com.google.tv.eoslauncher.util.Utils;
import com.mstar.android.MKeyEvent;
import com.mstar.android.tv.TvCecManager;
import com.mstar.android.tv.TvChannelManager;
import com.mstar.android.tv.TvCommonManager;
import com.mstar.android.tv.TvPipPopManager;
import com.mstar.android.tv.TvCecManager.OnCecCtrlEventListener;
import com.mstar.android.tvapi.common.AudioManager;
import com.mstar.android.tvapi.common.TvManager;
import com.mstar.android.tvapi.common.exception.TvCommonException;
import com.mstar.android.tvapi.common.vo.CecSetting;
import com.mstar.android.tvapi.common.vo.EnumProgramInfoType;
import com.mstar.android.tvapi.common.vo.EnumScalerWindow;
import com.mstar.android.tvapi.common.vo.MuteType.EnumMuteType;
import com.mstar.android.tvapi.common.vo.ProgramInfo;
import com.mstar.android.tvapi.common.vo.ProgramInfoQueryCriteria;
import com.mstar.android.tvapi.common.vo.TvOsType.EnumInputSource;
import com.mstar.android.tvapi.common.vo.VideoWindowType;

/**
 * projectName:EOSLauncher moduleName:HomeActivity.java
 * 
 * @author chadm.xiang
 * @version 1.0.0
 * @time 2013-12-18
 * @Copyright © 2013 Eos, Inc.
 */

public class HomeActivity extends Activity implements GestureDetector.OnGestureListener {

    private static final String TAG = "HomeActivity";

    private MyHandler mHandler;

    // view holder
    private MainViewHolder mViewHolder = null;

    // get ads from web pages
    private AdAction adction = null;

    private AdTextAction adtxtction = null;

    private AdAppStoreAction adAppStoreAction = null;

    private AppAction appAction = null;

    /***************** change source *****************************************/
    private final Object bSync = new Object();

    public EnumInputSource toChangeInputSource = EnumInputSource.E_INPUT_SOURCE_NONE;

    //
    private InputSourceThread inputSourceThread = new InputSourceThread();

    // advertis
    public List<MyAD> homeAdInfoList = null;

    // advertis
    public List<MyAD> appSotreAdInfoList = null;

    // APP Default
    public List<ResolveInfo> defaultAppInfos = null;

    // APP Down
    public List<AppInfoBean> downAppInfos = null;

    // luncher start or restart ,eddy modify true 锛孒omeFragment surfaceCreated
    // postDelayed 1300 ms
    public boolean isPowerOn = false;

    // tv surface size state
    SurfaceView surfaceView = null;

    private static RelativeLayout mainviewcontent = null;

    // pip Manager
    private TvPipPopManager tvPipPopManager;

    private String[] mTxtAdsStrings = null;

    private int[] mTxtAdsDurations = null;

    private int mAdsIndex = 0;

    private NetworkReceiver mNetworkReceiver;

    public boolean isCancelScale = false;

    private AudioManager am = TvManager.getInstance().getAudioManager();

    private GestureDetector gesture = null;

    private static Bitmap blurBitmap;

    private boolean flg = false;

    public boolean isClickBackBtn = false;

    private CecSetting hdmicecstatus = null;

    private OnCecCtrlEventListener mCecCtrlEventListener = null;

    // Handle different message
    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Constants.TOUPDATETIME: // update date and time
                    ContentResolver cv = getContentResolver();
                    String strTimeFormat = android.provider.Settings.System.getString(cv,
                            android.provider.Settings.System.TIME_12_24);
                    if (strTimeFormat == null || strTimeFormat.equals("24")) {
                        mViewHolder.time_txt.setText(UIUtil.formatDate(System.currentTimeMillis(), "HH:mm"));
                    } else {
                        mViewHolder.time_txt.setText(UIUtil.formatDate(System.currentTimeMillis(), "a hh:mm"));
                    }
                    mViewHolder.date_txt.setText(UIUtil.formatDate(System.currentTimeMillis(),
                            getString(R.string.date_format)));
                    mHandler.removeMessages(Constants.TOUPDATETIME);
                    mHandler.sendEmptyMessageDelayed(Constants.TOUPDATETIME, Constants.DELAYUPDATETIME);
                    break;
                case Constants.TODISMISSTIP: // hide help_tip view
                    if (mViewHolder.mHomeFragment.getHelp_tip() != null) {
                        mViewHolder.mHomeFragment.getHelp_tip().setVisibility(View.GONE);
                    }
                    break;
                case Constants.ADUPDATE: // udpate advertisement pictures
                    if (adction != null) {
                        homeAdInfoList = adction.getAdInfoList();
                        int position = msg.arg1;
                        mViewHolder.mHomeFragment.udpateViewFlipper(position);
                    }
                    break;
                case Constants.ADAPPSTOREUPDATE:
                    if (adAppStoreAction != null) {
                        appSotreAdInfoList = adAppStoreAction.getAppStoreAdInfoList();
                    }
                    break;
                case Constants.APPUPDATE:
                    if (appAction != null) {
                        downAppInfos = appAction.getDownAppInfoList();
                    }
                    break;
                case Constants.OSDMESSAGE: // update advertisement text
                    if (mTxtAdsStrings != null && mTxtAdsStrings.length > 0) {
                        int position = mAdsIndex;
                        mViewHolder.showOSDMessage(mTxtAdsStrings[position % mTxtAdsStrings.length]);
                        mHandler.removeMessages(Constants.OSDMESSAGE);
                        mHandler.sendEmptyMessageDelayed(Constants.OSDMESSAGE, mTxtAdsDurations[position
                                % mTxtAdsDurations.length]);
                        mAdsIndex++;
                    }
                    break;
                case Constants.OSDUPDATE:
                    mTxtAdsStrings = adtxtction.getmTxtAdsStrings();
                    mTxtAdsDurations = adtxtction.getmAdDuration();
                    Log.d(TAG, "Constants.OSDUPDATE mTxtAdsStrings:" + mTxtAdsStrings.toString());
                    break;
                case Constants.UPDATE_AD_FLAG:
                    adction.parsePgmJson();
                    break;
                case Constants.UPDATE_ADTEXT_FLAG:
                    adtxtction.parsePgmJson();
                    break;
                case Constants.UPDATE_APP_STORE:
                    adAppStoreAction.parsePgmJson();
                    break;
                case Constants.UPDATE_APP_INFO:
                    appAction.parsePgmJson();
                    break;
                case Constants.GET_CURRENT_ACTIVITY:
                    if (Utils.getCurrentActivity(HomeActivity.this).equals("com.bq.tv.task.MainActivity")) {
                        flg = false;
                    }
                    break;
                case Constants.SET_SMALL_SCALE:
                    setSmallscale();
                    break;
                case Constants.FINISH_BOOT_AD:
                    new Thread(inputSourceThread).start();
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
        mHandler = new MyHandler();
        mViewHolder = new MainViewHolder(this, mHandler);
        mainviewcontent = (RelativeLayout) findViewById(R.id.homeviewcontent);
        mainviewcontent.setVisibility(View.INVISIBLE);

        adction = new AdAction(this, mHandler);
        adtxtction = new AdTextAction(this, mHandler);
        adAppStoreAction = new AdAppStoreAction(this, mHandler);
        appAction = new AppAction(this, mHandler);

        homeAdInfoList = adction.getAdInfoList();
        appSotreAdInfoList = adAppStoreAction.getAppStoreAdInfoList();
        defaultAppInfos = appAction.shouDefault();
        downAppInfos = appAction.getDownAppInfoList();
        mTxtAdsStrings = adtxtction.getmTxtAdsStrings();
        mTxtAdsDurations = adtxtction.getmAdDuration();

        showViews(Constants.HOMEVIEW);

        tvPipPopManager = TvPipPopManager.getInstance();

        // register network receiver
        mNetworkReceiver = new NetworkReceiver(this, adction, adtxtction, adAppStoreAction, appAction);
        IntentFilter mFilter1 = new IntentFilter();
        mFilter1.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(mNetworkReceiver, mFilter1);

        gesture = new GestureDetector(this);
        mViewHolder.mStatusIcon.setOnClickListener(new OnClickListener() {

            @Override
            public void onClick(View arg0) {
                Intent intent = new Intent();
                intent.setClassName("com.eostek.hotkey", "com.eostek.hotkey.ScreenBallotActivity");
                startActivity(intent);
            }
        });

        Settings.System.putString(getContentResolver(), "LauncherVersionCode", "");

        openCEC(1);// 1==>ON 0===>OFF
        
        new Thread(new Runnable() {

            @Override
            public void run() {
//                while (!Utils.isAdVideoFinish()) {
//                    try {
//                        Thread.sleep(20);
//                    } catch (InterruptedException e) {
//                        e.printStackTrace();
//                    }
//                }
//                Log.d(TAG, "finish boot ad play");
                mHandler.sendEmptyMessage(Constants.FINISH_BOOT_AD);
            }
        }).start();
    }

    private void openCEC(int status) {
        hdmicecstatus = TvCecManager.getInstance().getCecConfiguration();
        hdmicecstatus.autoStandby = (short) status;
        TvCecManager.getInstance().setCecConfiguration(hdmicecstatus);
        TvCommonManager.getInstance().setSourceSwitchState(status);
    }

    @Override
    protected void onStart() {
        super.onStart();
        mCecCtrlEventListener = new CecCtrlEventListener();
        TvCecManager.getInstance().registerOnCecCtrlEventListener(mCecCtrlEventListener);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        String viewName = intent.getStringExtra("gotolanucher");
        // when click power key wake up the OS,click home key,setSmallscale
        SharedPreferences mPreferences = getPreferences(Activity.MODE_PRIVATE);
        String powerState = mPreferences.getString(Constants.POWER_STATE, Constants.POWER_STATE_DEFAULT_VALUE);
        Log.v(TAG, "onNewIntent  powerState = " + powerState);
        if (powerState.equals(Constants.POWER_STATE_CLICKED_VALUE)) {
            setSmallscale();
            mPreferences.edit().putString(Constants.POWER_STATE, Constants.POWER_STATE_DEFAULT_VALUE).commit();
            Log.v(TAG, "onNewIntent  setSmallscale ");
        }
        // change source
        isSourceChange(intent);

        // Receive menu button to display different views
        mViewHolder.cleanFragmentAllFocus();
        if (viewName == null) {
            showViews(Constants.HOMEVIEW);
            return;
        }
        if (viewName.equals("app")) {
            showViews(Constants.APPVIEW);
        } else if (viewName.equals("media")) {
            showViews(Constants.MEDIAVIEW);
        } else if (viewName.equals("heran")) {
            showViews(Constants.SHOPWEBVIEW);
        } else {
            showViews(Constants.HOMEVIEW);
        }

    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (mViewHolder == null) {
            mViewHolder = new MainViewHolder(this, mHandler);
        }
        showUserName();
        Log.v(TAG, "HomeActivity---onRestart mViewHolder " + (mViewHolder == null));
    }

    @Override
    protected void onResume() {
        super.onResume();
        System.gc();
        UIUtil.setSystemUIVisible(this, 0);
        isCancelScale = false;
        if (surfaceView == null) {
            isPowerOn = true;
            handlertv.postDelayed(handlerRuntv, 1500);
            backHomeSource();
            Log.d(TAG, "onResume  surfaceView = null");
        } else {
            android.provider.Settings.System.putInt(getContentResolver(), "tvapp", 1);
            Log.d(TAG, "onResume  surfaceView != null, flg:" + flg);
            if (flg) {
                surfaceView.setBackgroundColor(Color.BLACK);
                handlertv.postDelayed(pip_thread, 100);
                flg = false;
            }
        }
        if (tvPipPopManager.isPipModeEnabled()) {
            tvPipPopManager.disablePip();
            tvPipPopManager.setPipOnFlag(false);
        }
        showUserName();

        if (UIUtil.shouldKillMemory(this)) {
            UIUtil.killProcesses();
            Log.v(TAG, "kill backgroud Processes");
        }
        Log.i(TAG, "power1 = " + SystemProperties.get(Constants.FIRST_POWER_ON, "0"));
        // String openingmode =
        // android.provider.Settings.System.getString(getContentResolver(),
        // "openingmode");
        if (!Utils.isAdVideoFinish() && SystemProperties.get(Constants.FIRST_POWER_ON, "0").equals("0")) {
            //            startTV();
            // if (openingmode != null) {
            // if (openingmode.equals("TV")) {
            // startTV();
            // }
            // }
            setPropertyForSTR(Constants.FIRST_POWER_ON, "1");
        }
        Log.i(TAG, "power2 = " + SystemProperties.get(Constants.FIRST_POWER_ON, "0"));
    }

    private void startTV() {
        String pkgName = "com.eostek.tv.player";
        String clsName = "com.eostek.tv.player.PlayerActivity";
        ComponentName componentName = new ComponentName(pkgName, clsName);
        Intent intent = new Intent();
        intent.putExtra("isAdVideo", true);
        intent.setComponent(componentName);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        startActivity(intent);
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

    @Override
    protected void onPause() {
        super.onPause();
        Log.v(TAG, "onPause()....");
        Log.v(TAG, "current activity:" + Utils.getCurrentActivity(this));
        if (Utils.getCurrentActivity(this).equals("com.eostek.tv.player.PlayerActivity")) {
            isCancelScale = true;
            handlertv.removeCallbacks(pip_thread);
        } else if (Utils.getCurrentActivity(this).equals("com.mstar.tv.menu.ui.EosCustomSettingActivity")
                || Utils.getCurrentActivity(this).equals("com.eostek.hotkey.EosInputActivity")) {
            flg = false;
        } else if (Utils.getCurrentActivity(this).equals("com.biaoqi.filefly")) {
            toChangeInputSource = EnumInputSource.E_INPUT_SOURCE_STORAGE;
        }
        mHandler.sendEmptyMessageDelayed(Constants.GET_CURRENT_ACTIVITY, Constants.DELAY_TIME);
    }

    @Override
    protected void onStop() {
        Log.v(TAG, "onStop()....");
        UIUtil.setSystemUIVisible(this, 1);
        android.provider.Settings.System.putInt(getContentResolver(), "tvapp", 0);
        super.onStop();
        String className = Utils.getCurrentActivity(this);
        if (!className.equals("com.eostek.tv.player.PlayerActivity")
                && !className.equals("com.utsmta.app.MainActivity")) {
            toChangeInputSource = EnumInputSource.E_INPUT_SOURCE_STORAGE;
        }
        flg = true;
        TvCecManager.getInstance().unregisterOnCecCtrlEventListener(mCecCtrlEventListener);
        mCecCtrlEventListener = null;
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mNetworkReceiver != null) {
            unregisterReceiver(mNetworkReceiver);
            mNetworkReceiver = null;
        }
        Log.v(TAG, "HomeActivity---onDestroy ");
        if (homeAdInfoList != null) {
            homeAdInfoList.clear();
        }
        if (appSotreAdInfoList != null) {
            appSotreAdInfoList.clear();
        }

        if (blurBitmap != null) {
            blurBitmap.recycle();
            blurBitmap = null;
        }

        DBHelper.closeDB();
        android.os.Process.killProcess(android.os.Process.myPid());
    }

    /**
     * change display view
     * 
     * @param num The num corresponding to the view
     */
    public void showViews(int num) {
        if (num == 0) { // when in Home view switching signal, note
                        // synchronization
            toChangeInputSource = EnumInputSource.E_INPUT_SOURCE_DTV;
        } else {
            toChangeInputSource = EnumInputSource.E_INPUT_SOURCE_STORAGE;
        }

        mViewHolder.updateFragment(num);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        // TODO Auto-generated method stub
        super.onSaveInstanceState(outState);
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
        Bundle mBundle = new Bundle();
        // according to the numerical buttons to display different views
        switch (keyCode) {
            case KeyEvent.KEYCODE_PROG_RED:
                mViewHolder.mHomeFragment.getHelp_tip().setVisibility(View.GONE);
                mIntent = new Intent();
                mIntent.setClass(HomeActivity.this, HelperActivity.class);
                startActivity(mIntent);
                overridePendingTransition(R.anim.photo_push_left_in, 0);
                return false;
            case KeyEvent.KEYCODE_MENU:
                // only in HomeFragment,response Menu key click
                if (mViewHolder.getmLastPosition() == Constants.HOMEVIEW) {
                    mIntent = new Intent();
                    mIntent.setClassName("com.mstar.tv.menu", "com.mstar.tv.menu.ui.EosCustomSettingActivity");
                    startActivity(mIntent);
                }
                return true;
            case MKeyEvent.KEYCODE_TV_FREEZE:
                if (mViewHolder.getmLastPosition() == Constants.HOMEVIEW) {
                    try {
                        EnumInputSource source = getHotKeySource(android.provider.Settings.System.getInt(
                                getContentResolver(), "hotkey1"));
                        EnumInputSource curSource = EnumInputSource.values()[queryCurInputSrc()];
                        if (!(source == EnumInputSource.E_INPUT_SOURCE_NONE || source == curSource)) {
                            TvCommonManager.getInstance().setInputSource(source);
                            if (source == EnumInputSource.E_INPUT_SOURCE_ATV
                                    || source == EnumInputSource.E_INPUT_SOURCE_DTV) {
                                ChannelManagerExt cm = ChannelManagerExt.getInstance();
                                cm.getAllChannels(this, source);
                                ProgramInfo pinfo = cm.getCurProgramInfo();
                                programSel(pinfo.number, pinfo.serviceType);
                            }
                            setSmallscale();
                        } else if (source == EnumInputSource.E_INPUT_SOURCE_NONE) {
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
            case KeyEvent.KEYCODE_TV_TELETEXT:
                if (mViewHolder.getmLastPosition() == Constants.HOMEVIEW) {
                    try {
                        EnumInputSource source = getHotKeySource(android.provider.Settings.System.getInt(
                                getContentResolver(), "hotkey2"));
                        EnumInputSource curSource = EnumInputSource.values()[queryCurInputSrc()];
                        if (!(source == EnumInputSource.E_INPUT_SOURCE_NONE || source == curSource)) {
                            TvCommonManager.getInstance().setInputSource(source.ordinal());
                            if (source == EnumInputSource.E_INPUT_SOURCE_ATV
                                    || source == EnumInputSource.E_INPUT_SOURCE_DTV) {
                                ChannelManagerExt cm = ChannelManagerExt.getInstance();
                                cm.getAllChannels(this, source);
                                ProgramInfo pinfo = getCurrProgramInfo();
                                programSel(pinfo.number, pinfo.serviceType);
                            }
                            setSmallscale();
                        } else if (source == EnumInputSource.E_INPUT_SOURCE_NONE) {
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
                if (surfaceView != null && mViewHolder.getmLastPosition() == Constants.HOMEVIEW) {
                    Log.d(TAG, "mviewHolder.mHomeFragment.backCurrentSource");
                    isClickBackBtn = true;
                    mViewHolder.mHomeFragment.backCurrentSource();
                    Log.d(TAG, "mviewHolder.mHomeFragment.backCurrentSource");
                }
                return true;
            case MKeyEvent.KEYCODE_TV_HOLD:
                if (Utils.isApkExist(this, "com.heran.kok") && Constants.kok_device) {
                    startApk("com.heran.kok", "com.heran.kok.MainActivity", mBundle);
                }
                return true;
            default:
                break;
        }
        return super.onKeyDown(keyCode, event);
    }

    private boolean isDrag = false;

    @Override
    public boolean dispatchTouchEvent(MotionEvent ev) {
        if (ev.getAction() == MotionEvent.ACTION_DOWN) {
            isDrag = false;
        } else if (ev.getAction() == MotionEvent.ACTION_MOVE) {
            isDrag = true;
        }
        gesture.onTouchEvent(ev);
        Log.v(TAG, "dispatchTouchEvent " + ev.getAction() + "; isDrag = " + isDrag);
        if (isDrag) {
            return true;
        } else {
            return super.dispatchTouchEvent(ev);
        }
    }

    /**
     * get current hot key source
     * 
     * @param int hotkeyValue
     * @return source EnumInputSource.E_INPUT_SOURCE_ATV E_INPUT_SOURCE_DTV
     *         E_INPUT_SOURCE_HDMI E_INPUT_SOURCE_HDMI4 E_INPUT_SOURCE_SVIDEO
     *         E_INPUT_SOURCE_YPBPR E_INPUT_SOURCE_VGA
     */
    private EnumInputSource getHotKeySource(int hotkeyValue) {
        EnumInputSource source = EnumInputSource.E_INPUT_SOURCE_NONE;
        switch (hotkeyValue) {
            case 1:
                try {
                    am.enableMute(EnumMuteType.E_MUTE_PERMANENT);
                } catch (TvCommonException e) {
                    e.printStackTrace();
                }
                source = EnumInputSource.E_INPUT_SOURCE_ATV;
                handlertv.postDelayed(mute_thread, 500);
                break;
            case 2:
                try {
                    am.enableMute(EnumMuteType.E_MUTE_PERMANENT);
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

    /**
     * start an application
     * 
     * @param pckName PackageName
     * @param clsName ClassName
     * @param bundle additional parameters, options
     */
    public void startApk(String pckName, String clsName, Bundle bundle) {
        if (!pckName.equals("com.eostek.tv.player")) {
            setToChangeInputSource(EnumInputSource.E_INPUT_SOURCE_STORAGE);
        }
        ComponentName componentName = new ComponentName(pckName, clsName);
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setComponent(componentName);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        UIUtil.updateHistory(this, pckName, clsName);
        HomeActivity.this.startActivity(intent);
    }

    /**
     * show user name
     */
    private void showUserName() {
        Usr user = SciflyStore.User.getUser(getContentResolver());
        if (user != null) {
            Log.d(TAG, "showUserName if " + user);
            if (user.mName == null) {
                user.mName = "Guest";
            }
        } else {
            Log.d(TAG, "showUserName else " + user);
            user = new Usr();
            user.mName = "Guest";
        }
        mViewHolder.mUserName.setText(user.mName + getString(R.string.shopping_gold) + user.mCoin
                + getString(R.string.bonus_gold) + user.mBonus);
        // mViewHolder.mUserName.setText(user.mName);
    }

    // set intput source
    public void setToChangeInputSource(EnumInputSource toChangeInputSource) {
        synchronized (bSync) {
            this.toChangeInputSource = toChangeInputSource;
        }

    }

    // handlertv postDelayed chanage source
    Handler handlertv = new Handler();

    Runnable handlerRuntv = new Runnable() {
        @Override
        public void run() {
            surfaceView = mViewHolder.mHomeFragment.getmTv_sur();
            mainviewcontent.setVisibility(View.VISIBLE);
            handlertv.removeCallbacks(handlerRuntv);
        }

    };

    Runnable pip_thread = new Runnable() {
        @Override
        public void run() {
            surfaceView.setBackgroundColor(Color.TRANSPARENT);
            handlertv.removeCallbacks(pip_thread);
        }
    };

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

    // reset input source when resume
    public void backHomeSource() {
        Log.i(TAG, "BackHomeSource------------ bSync = " + bSync);
        synchronized (bSync) {
            toChangeInputSource = EnumInputSource.E_INPUT_SOURCE_DTV;
        }
    }

    /**
     * Set fullscreen
     */
    public void setFullscale() {
        try {
            Log.d(TAG, "setFullscale start.....");
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
            Log.d(TAG, "setFullscale finish.....");
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
    }

    /**
     * Set small screen
     */
    public void setSmallscale() {
        Log.v(TAG, "isCancelScale: " + isCancelScale);
        if (isCancelScale || !Utils.isAdVideoFinish()) {
            return;
        }
        try {
            VideoWindowType videoWindowType = new VideoWindowType();
            videoWindowType.height = getResources().getInteger(R.integer.videoWindowType_height);
            videoWindowType.width = getResources().getInteger(R.integer.videoWindowType_width);
            videoWindowType.x = getResources().getInteger(R.integer.videoWindowType_x);
            videoWindowType.y = getResources().getInteger(R.integer.videoWindowType_y);
            if (TvManager.getInstance() != null) {
                TvManager.getInstance().getPictureManager().selectWindow(EnumScalerWindow.E_MAIN_WINDOW);
                TvManager.getInstance().getPictureManager().setDisplayWindow(videoWindowType);
                TvManager.getInstance().getPictureManager().scaleWindow();
            }
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        Log.v(TAG, "setSmallscale finish");
    }

    /**
     * switch program
     * 
     * @param u32Number
     * @param u8ServiceType
     * @return
     */
    public boolean programSel(int u32Number, int u8ServiceType) {
        EnumInputSource currentSource;
        currentSource = TvCommonManager.getInstance().getCurrentInputSource();
        // when the current source is storage,return false
        if (currentSource == EnumInputSource.E_INPUT_SOURCE_STORAGE) {
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
            setSmallscale();
        }
        // programSel
        if (isChangeChannel) {
            ProgramInfo pinfo = getCurrProgramInfo();
            programSel(pinfo.number, pinfo.serviceType);
        }
    }

    private void setPowerOnSource() {
        int curSourceID = TvCommonManager.getInstance().getPowerOnSource().ordinal();
        Log.v(TAG, "curSourceID:" + curSourceID);
        if ((curSourceID >= 0) && (curSourceID <= EnumInputSource.E_INPUT_SOURCE_NONE.ordinal())) {
            TvCommonManager.getInstance().setInputSource(EnumInputSource.values()[curSourceID].ordinal());
        }
    }

    /**
     * The thread to handle input source changes
     */
    class InputSourceThread implements Runnable {
        @Override
        public void run() {
            setPowerOnSource();
            EnumInputSource tmpInputSource;
            // to make OnTvEventListener work well when first time Launcher
            // start
            TvCommonManager.getInstance().setInputSource(EnumInputSource.E_INPUT_SOURCE_STORAGE);
            Log.v(TAG, "InputSourceThread start ,set storage");
            mHandler.sendEmptyMessageDelayed(Constants.SET_SMALL_SCALE, 500);
            while (true) {
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
                            EnumInputSource curSource = EnumInputSource.values()[queryCurInputSrc()];
                            TvCommonManager.getInstance().setInputSource(curSource);
                             setSmallscale();
                            if (curSource == EnumInputSource.E_INPUT_SOURCE_ATV
                                    || curSource == EnumInputSource.E_INPUT_SOURCE_DTV) {
                                ProgramInfo pinfo = getCurrProgramInfo();
                                programSel(pinfo.number, pinfo.serviceType);
                            }
                        }
                    } else {
                        EnumInputSource curSource = TvCommonManager.getInstance().getCurrentInputSource();
                        if (curSource != EnumInputSource.E_INPUT_SOURCE_STORAGE) {
                            TvCommonManager.getInstance().setInputSource(curSource);
                            TvCommonManager.getInstance().setInputSource(tmpInputSource.ordinal());
                        }
                        Log.v("channel", "current source:"
                                + TvCommonManager.getInstance().getCurrentInputSource().ordinal());
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
     * stop vod play
     */
    public void stopVodPlay(WebView mAdWebView, ProgressBar prgBar) {
        backHomeSource();
        if (EnumInputSource.E_INPUT_SOURCE_STORAGE == TvCommonManager.getInstance().getCurrentInputSource()) {
            handlertv.postDelayed(pip_thread, 1000);
        }
        try {
            mAdWebView.setVisibility(View.GONE);
            prgBar.setVisibility(View.GONE);
            mAdWebView.getClass().getMethod("onPause").invoke(mAdWebView, (Object[]) null);
            mAdWebView.getClass().getMethod("stopLoading").invoke(mAdWebView, (Object[]) null);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }

    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        return gesture.onTouchEvent(event);
    }

    @Override
    public boolean onDown(MotionEvent arg0) {
        return false;
    }

    @Override
    public boolean onFling(MotionEvent arg0, MotionEvent arg1, float arg2, float arg3) {
        Log.v(TAG, "onFling...");
        if (mViewHolder.mHomeFragment.isRunning) {
            return false;
        }
        int position = mViewHolder.getmLastPosition();
        mViewHolder.cleanFragmentAllFocus();

        int value = 0;

        if (arg0.getX() - arg1.getX() > 30) {
            value = 1;
        } else if (arg0.getX() - arg1.getX() < -30) {
            value = -1;
        }

        int tmp = position + value;

        if (tmp < 0) {
            tmp = Constants.SHOPWEBVIEW;
        }
        if (Constants.SkipPandora != null && Constants.SkipPandora.equals("1")) {
            if (tmp == Constants.PANDORAVIEW) {
                tmp = Constants.PANDORAVIEW + value;
            }
        }
        showViews(tmp % 5);
        return false;
    }

    public static Bitmap getCurrentBackground() {
        mainviewcontent.setDrawingCacheEnabled(true);
        blurBitmap = mainviewcontent.getDrawingCache();
        return blurBitmap;
    }

    @Override
    public void onLongPress(MotionEvent arg0) {
    }

    @Override
    public boolean onScroll(MotionEvent arg0, MotionEvent arg1, float arg2, float arg3) {
        return false;
    }

    @Override
    public void onShowPress(MotionEvent arg0) {

    }

    @Override
    public boolean onSingleTapUp(MotionEvent arg0) {
        return false;
    }

    @Override
    public void onConfigurationChanged(Configuration newConfig) {
        // TODO Auto-generated method stub
        super.onConfigurationChanged(newConfig);

        mHandler.sendEmptyMessageDelayed(Constants.MSG_CITY_CHANGE, 1000);
    }

    private class CecCtrlEventListener implements OnCecCtrlEventListener {
        @Override
        public boolean onCecCtrlEvent(int what, int arg1, int arg2) {
            switch (what) {
                case TvCecManager.TVCEC_STANDBY: {
                    Log.i(TAG, "EV_CEC_STANDBY");
                    TvCommonManager.getInstance().standbySystem("cec");
                }
                    break;
                case TvCecManager.TVCEC_SET_MENU_LANGUAGE: {
                }
                    break;
                case TvCecManager.TVCEC_SOURCE_SWITCH: {
                    Log.i(TAG, "EV_CEC_SOURCE_SWITCH");
                    Log.i(TAG, "-----arg1:" + arg1);
                    if (arg1 == TvCommonManager.INPUT_SOURCE_HDMI) {
                        arg1 = TvCommonManager.INPUT_SOURCE_HDMI3;
                    } else if (arg1 == TvCommonManager.INPUT_SOURCE_HDMI3) {
                        arg1 = TvCommonManager.INPUT_SOURCE_HDMI;
                    }
                    Log.i(TAG, "-----final arg1:" + arg1);
                    TvCommonManager.getInstance().setInputSource(arg1);
                    int source = TvCommonManager.getInstance().getCurrentTvInputSource();
                    if (!(source == TvCommonManager.INPUT_SOURCE_HDMI || source == TvCommonManager.INPUT_SOURCE_HDMI2 || source == TvCommonManager.INPUT_SOURCE_HDMI3)) {
                        TvCommonManager.getInstance().setInputSource(source);
                        TvCommonManager.getInstance().setInputSource(arg1);
                        Log.i(TAG, "-----final source:" + source);
                    }
                }
                    break;
                case TvCecManager.TVCEC_SEL_DIGITAL_SERVICE_DVB: {
                }
                    break;
                default: {
                    Log.i(TAG, "Unknown message type " + what);
                }
                    break;
            }
            return true;
        }
    }

}
