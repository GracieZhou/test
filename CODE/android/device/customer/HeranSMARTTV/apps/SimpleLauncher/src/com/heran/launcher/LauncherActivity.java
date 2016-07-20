
package com.heran.launcher;

import com.heran.launcher.business.NetworkReceiver;
import com.heran.launcher.util.ChannelManagerExt;
import com.heran.launcher.util.Constants;
import com.heran.launcher.util.UIUtil;
import com.heran.launcher.util.Utils;
import com.mstar.android.MKeyEvent;
import com.mstar.android.tv.TvCommonManager;
import com.mstar.android.tvapi.common.TvManager;
import com.mstar.android.tvapi.common.exception.TvCommonException;
import com.mstar.android.tvapi.common.vo.ProgramInfo;

import android.app.Activity;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.provider.Settings.SettingNotFoundException;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Toast;

public class LauncherActivity extends Activity {

    private static final String TAG = "LauncherActivity";

    public LauncherHolder mHolder = null;

    public LauncherLogic mLogic = null;

    public static String myMac = "00:88:88:00:00:01";
    
    private NetworkReceiver mNetworkReceiver ;
    
    public boolean isClickPlayerActivity=false;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.launcher_main);
        mLogic = new LauncherLogic(this);
        mHolder = new LauncherHolder(this);
        mNetworkReceiver  = new NetworkReceiver(this);
        IntentFilter mFilter1 = new IntentFilter();
        mFilter1.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        registerReceiver(mNetworkReceiver, mFilter1);
    }
    
    public static String getMac(){
    	   try {
               myMac = TvManager.getInstance().getEnvironment("ethaddr");
           } catch (TvCommonException e) {
               e.printStackTrace();
               
           }
    	   return myMac;
    }
    

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        // when click power key wake up the OS,click home key,setSmallscale
        SharedPreferences mPreferences = getPreferences(Activity.MODE_PRIVATE);
        String powerState = mPreferences.getString(Constants.POWER_STATE, Constants.POWER_STATE_DEFAULT_VALUE);
        Log.v(TAG, "onNewIntent  powerState = " + powerState);
        if (powerState.equals(Constants.POWER_STATE_CLICKED_VALUE)) {
            mLogic.setSmallscale(mHolder.isCancelScale);
            mPreferences.edit().putString(Constants.POWER_STATE, Constants.POWER_STATE_DEFAULT_VALUE).commit();
            Log.v(TAG, "onNewIntent  setSmallscale ");
        }
        // change source
        mLogic.isSourceChange(intent, mHolder.isCancelScale);
    }

    @Override
    protected void onRestart() {
        super.onRestart();
        if (mHolder == null) {
            mHolder = new LauncherHolder(this);
        }
        mHolder.doOnRestart();
        Log.d(TAG, "onrestart isAdVideoFinish :" + Utils.isAdVideoFinish());
        if (!Utils.isAdVideoFinish()) {
            mLogic.setPowerOnSource();
        }
        Log.v(TAG, "HomeActivity---onRestart mHolder " + (mHolder == null));
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!Utils.isAdVideoFinish()){
           return; 
        }
        System.gc();
        UIUtil.setSystemUIVisible(this, 0);
        mHolder.doOnResume();
        android.provider.Settings.System.putInt(getContentResolver(), "tvapp", 1);
        if (UIUtil.shouldKillMemory(this)) {
            UIUtil.killProcesses();
            Log.v(TAG, "kill backgroud Processes");
        }
        isClickPlayerActivity = false;
    }

    @Override
    protected void onPause() {
        super.onPause();
        Log.v(TAG, "onPause()....");
        mHolder.doOnPause();
    }

    @Override
    protected void onStop() {
        Log.v(TAG, "onStop()....");
        UIUtil.setSystemUIVisible(this, 1);
        if (!isClickPlayerActivity) {
            android.provider.Settings.System.putInt(getContentResolver(), "tvapp", 0);
        }
        super.onStop();
        mHolder.doOnStop();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "HomeActivity---onDestroy ");
        android.os.Process.killProcess(android.os.Process.myPid());
        if (mNetworkReceiver != null) {
            unregisterReceiver(mNetworkReceiver);
            mNetworkReceiver = null;
        }
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
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
        // according to the numerical buttons to display different views
        try {
            new Thread().sleep(300);
        } catch (InterruptedException e1) {
            // TODO Auto-generated catch block
            e1.printStackTrace();
        }
        switch (keyCode) {
        	case KeyEvent.KEYCODE_PROG_RED:
        		Log.d(TAG, "KEYCODE_PROG_RED");
        		mHolder.getHelp_tip().setVisibility(View.GONE);
        		mIntent = new Intent();
        		mIntent.setClass(LauncherActivity.this, HelperActivity.class);
        		startActivity(mIntent);
        		overridePendingTransition(R.anim.photo_push_left_in, 0);
        		return false;
            case KeyEvent.KEYCODE_MENU:
                // only in HomeFragment,response Menu key click
                mIntent = new Intent();
                mIntent.setClassName("com.mstar.tv.menu", "com.mstar.tv.menu.ui.EosCustomSettingActivity");
                startActivity(mIntent);
                return true;
            case KeyEvent.KEYCODE_BACK:
                return true;
            case KeyEvent.KEYCODE_INFO:
                // 「顯示」按鈕，顯示版本號和電視Mac
                Toast.makeText(getApplication(), "v" + this.getString(R.string.version) + " \n" + getMac(),Toast.LENGTH_LONG).show();
                return true;
            case MKeyEvent.KEYCODE_FREEZE:
                try {
                    int source = mHolder
                            .getHotKeySource(android.provider.Settings.System.getInt(getContentResolver(), "hotkey1"));
                    int curSource = mLogic.queryCurInputSrc();
                    Log.d(TAG, "hotkey1 source:" + source + "curSource:" + curSource);
                    if (!(source == TvCommonManager.INPUT_SOURCE_NONE || source == curSource)) {
                        TvCommonManager.getInstance().setInputSource(source);
                        if (source == TvCommonManager.INPUT_SOURCE_ATV || source == TvCommonManager.INPUT_SOURCE_DTV) {
                            ChannelManagerExt cm = ChannelManagerExt.getInstance();
                            cm.getAllChannels(this, source);
                            ProgramInfo pinfo = cm.getCurProgramInfo();
                            mLogic.programSel(pinfo.number, pinfo.serviceType);
                        }
                        mLogic.setSmallscale(false);
                    } else if (source == TvCommonManager.INPUT_SOURCE_NONE) {
                        Intent menuIntent = new Intent();
                        menuIntent.setClassName("com.mstar.tv.menu", "com.mstar.tv.menu.ui.EosCustomSettingActivity");
                        menuIntent.putExtra("gotoadvance", true);
                        startActivity(menuIntent);
                    }
                } catch (SettingNotFoundException e) {
                    e.printStackTrace();
                }
                return true;
            case MKeyEvent.KEYCODE_TTX:
                try {
                    int source = mHolder
                            .getHotKeySource(android.provider.Settings.System.getInt(getContentResolver(), "hotkey2"));
                    int curSource = mLogic.queryCurInputSrc();
                    Log.d(TAG, "hotkey1 source:" + source + "curSource:" + curSource);
                    if (!(source == TvCommonManager.INPUT_SOURCE_NONE || source == curSource)) {
                        TvCommonManager.getInstance().setInputSource(source);
                        if (source == TvCommonManager.INPUT_SOURCE_ATV || source == TvCommonManager.INPUT_SOURCE_DTV) {
                            ChannelManagerExt cm = ChannelManagerExt.getInstance();
                            cm.getAllChannels(this, source);
                            ProgramInfo pinfo = cm.getCurProgramInfo();
                            mLogic.programSel(pinfo.number, pinfo.serviceType);
                        }
                        mLogic.setSmallscale(false);
                    } else if (source == TvCommonManager.INPUT_SOURCE_NONE) {
                        Intent menuIntent = new Intent();
                        menuIntent.setClassName("com.mstar.tv.menu", "com.mstar.tv.menu.ui.EosCustomSettingActivity");
                        menuIntent.putExtra("gotoadvance", true);
                        startActivity(menuIntent);
                    }
                } catch (SettingNotFoundException e) {
                    e.printStackTrace();
                }
                return true;
            default:
                break;

        }
        return super.onKeyDown(keyCode, event);
    }

}
