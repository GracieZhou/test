
package com.heran.launcher2;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;

import com.heran.launcher2.advert.AdAction;
import com.heran.launcher2.advert.BootAdActivity;
import com.heran.launcher2.advert.MyAD;
import com.heran.launcher2.apps.AppAction;
import com.heran.launcher2.apps.AppInfoBean;
import com.heran.launcher2.message.MessageAction;
import com.heran.launcher2.news.NewsAction;
import com.heran.launcher2.news.NewsCategory;
import com.heran.launcher2.util.Constants;
import com.heran.launcher2.util.HistoryRec;
import com.heran.launcher2.util.Utils;
import com.heran.launcher2.weather.WeatherAction;
import com.mstar.android.tv.TvCommonManager;
import com.mstar.android.tvapi.common.TvManager;
import com.mstar.android.tvapi.common.exception.TvCommonException;
import com.mstar.android.tvapi.common.vo.EnumScalerWindow;
import com.mstar.android.tvapi.common.vo.VideoWindowType;

import android.content.ComponentName;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ResolveInfo;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.Settings;
import android.util.Log;

/**
 * projectName： EOSLauncher moduleName：MainLogic.java
 * 
 * @author laird.li
 * @version 1.0.0
 * @time 2016-03-30
 * @Copyright © 2016 Eos, Inc.
 */
public class MainLogic {

    private static final String TAG = "MainLogic";
    
    private static final String AUTO_START_FLAG = "/data/app/mta.properties";

    private final HomeActivity mContext;

    public AdAction adction = null;

    public MessageAction adtxtction = null;

    public AppAction appAction = null;

    public NewsAction newsAction = null;

    public WeatherAction weatherAction = null;

    // advertis
    public List<MyAD> homeAdInfoList = null;

    // APP Default
    public List<ResolveInfo> defaultAppInfos = null;

    // APP Down
    public List<AppInfoBean> downAppInfos = null;

    // newslist
    public ArrayList<NewsCategory> newsInfoList = null;

    public String[] mTxtAdsStrings = null;

    public int[] mTxtAdsDurations = null;

    public String[] mMenuTips = null;

    private NetworkReceiver mNetworkReceiver;

    public static boolean isCancelScale = false;

    private final Handler mHandler;

    public MainLogic(HomeActivity mActivity, Handler handler) {
        this.mContext = mActivity;
        this.mHandler = handler;
    }

    public void jumpToBootAdActivity() {
        if(!foundMtaStartFlag()){
            Intent startIntent = new Intent();
            startIntent.setClass(mContext, BootAdActivity.class);
            mContext.startActivity(startIntent);
        }
    }

    private boolean foundMtaStartFlag() {
        boolean found = false;
        File file = new File(AUTO_START_FLAG);
        if (file.exists()) {
            return true;
        }
        ArrayList<String> usbPathList = Utils.getMountedUsbDevices("/mnt/usb/");
        for (String path : usbPathList) {
            File _file = new File(path + "/" + "eostek.mta.auto");
            if (_file.exists()) {
                found = true;
                break;
            }
        }
        return found;
    }

    public void initDatas() {
        adction = new AdAction(mContext, mHandler);
        adtxtction = new MessageAction(mContext, mHandler);
        appAction = new AppAction(mContext, mHandler);
        newsAction = new NewsAction(mContext, mHandler);
        weatherAction = new WeatherAction(mContext, mHandler);

        homeAdInfoList = adction.getAdInfoList();
        defaultAppInfos = appAction.shouDefault();
        downAppInfos = appAction.getDownAppInfoList();
        mTxtAdsStrings = adtxtction.getmTxtAdsStrings();
        mTxtAdsDurations = adtxtction.getmAdDuration();
        newsInfoList = newsAction.getNewsInfoList();

        if (!HistoryRec.isRecFileExist) {
            if (HistoryRec.createRecFile()) {
                Log.d(TAG, "createRecFile ok ");
                HistoryRec.isRecFileExist = true;
            } else {
                Log.d(TAG, "createRecFile fail");
                HistoryRec.isRecFileExist = false;
            }
        }
        HomeActivity.mVersion = Build.VERSION.RELEASE;
        try {
            HomeActivity.myMac = TvManager.getInstance().getEnvironment("ethaddr");
        } catch (TvCommonException e) {
            e.printStackTrace();
        }

        mMenuTips = mContext.getResources().getStringArray(R.array.menu_preview_tips);
        Constants.SkipPandora = Settings.System.getString(mContext.getContentResolver(), "SkipPandora");
    }

    public void clearDatas() {
        if (homeAdInfoList != null) {
            homeAdInfoList.clear();
        }
    }

    public void registerLister() {
        // register network listener
        mNetworkReceiver = new NetworkReceiver(mContext, mHandler, adction, adtxtction, appAction, newsAction,
                weatherAction);
        IntentFilter mFilter1 = new IntentFilter();
        mFilter1.addAction("android.net.conn.CONNECTIVITY_CHANGE");
        mFilter1.addAction("com.mstar.android.pppoe.PPPOE_STATE_ACTION");
        // 註冊一個BroadcastReceiver
        mContext.registerReceiver(mNetworkReceiver, mFilter1);

    }

    public void unregisterLister() {
        if (mNetworkReceiver != null) {
            mContext.unregisterReceiver(mNetworkReceiver);
        }
    }

    /**
     * start an application
     * 
     * @param pckName PackageName
     * @param clsName ClassName
     * @param bundle additional parameters, options
     */
    public void startApk(String pckName, String clsName, Bundle bundle) {
        if (!pckName.equals(Constants.TVPLAY_PKG)) {
            mContext.setToChangeInputSource(TvCommonManager.INPUT_SOURCE_STORAGE);
        }
        ComponentName componentName = new ComponentName(pckName, clsName);
        Intent intent = new Intent(Intent.ACTION_MAIN);
        intent.addCategory(Intent.CATEGORY_LAUNCHER);
        intent.setComponent(componentName);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        if (bundle != null) {
            intent.putExtras(bundle);
        }
        mContext.startActivity(intent);
    }

    public String[] GetAdTexts() {

        return mTxtAdsStrings;
    }

    public void setSmallscale() {
        Log.v(TAG, "isCancelScale: " + isCancelScale);
        if (isCancelScale||!Utils.isAdVideoFinish()) {
            return;
        }
        try {
            VideoWindowType videoWindowType = new VideoWindowType();
            String panelInfo = readPanelInfo();
            int width = Integer.parseInt(panelInfo.split(";")[0]);
            int height = Integer.parseInt(panelInfo.split(";")[1]);
            // 判斷是否為39吋
            if (readCustomerIni().equals("config/panel/C390X15-E4-A_G3.ini")) {
                Log.d("123", "config/panel/C390X15-E4-A_G3.ini");
                if (width == 1366 && height == 768) {
                    Log.i(TAG, "1366 * 768 panel");
                    videoWindowType.height = 589;
                    videoWindowType.width = 1020;
                    videoWindowType.x = 10;
                    videoWindowType.y = 4;
                } else {
                    Log.d("123", "not 1366 * 768 panel");
                    videoWindowType.height = mContext.getResources().getInteger(R.integer.videoWindowType_height);
                    videoWindowType.width = mContext.getResources().getInteger(R.integer.videoWindowType_width);
                    videoWindowType.x = mContext.getResources().getInteger(R.integer.videoWindowType_x);
                    videoWindowType.y = mContext.getResources().getInteger(R.integer.videoWindowType_y);
                }
            }

            else {
                Log.d("123", " not config/panel/C390X15-E4-A_G3.ini");
                if (width == 1366 && height == 768) {
                    Log.d("123", " 1366 * 768 panel");
                    Log.i(TAG, "1366 * 768 panel");
                    videoWindowType.height = 580;
                    videoWindowType.width = 1020;
                    videoWindowType.x = 10;
                    videoWindowType.y = 10;
                } else {
                    if (readSysIni().equals("MS39E1-CH1")) {
                        Log.d("123", "equals MS39E1-CH1");
                        videoWindowType.height = 900;
                        videoWindowType.width = 1600;
                        videoWindowType.x = 0;
                        videoWindowType.y = 0;
                    } else {
                        Log.d("123", " not MS39E1-CH1");
                        videoWindowType.height = mContext.getResources().getInteger(R.integer.videoWindowType_height);
                        videoWindowType.width = mContext.getResources().getInteger(R.integer.videoWindowType_width);
                        videoWindowType.x = mContext.getResources().getInteger(R.integer.videoWindowType_x);
                        videoWindowType.y = mContext.getResources().getInteger(R.integer.videoWindowType_y);
                    }
                }
            }

            if (TvManager.getInstance() != null) {
                TvManager.getInstance().getPictureManager().selectWindow(EnumScalerWindow.E_MAIN_WINDOW);
                TvManager.getInstance().getPictureManager().setDisplayWindow(videoWindowType);
                TvManager.getInstance().getPictureManager().scaleWindow();
            }
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
    }

    private String readSysIni() {
        String line = null;
        String panelName = "";
        try {
            File file = new File("tvconfig/config/sys.ini");
            FileInputStream mStream = new FileInputStream(file);
            BufferedReader reader = new BufferedReader(new InputStreamReader(mStream));
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("project_panel_version")) {
                    int position = line.indexOf(";");
                    String[] tmpStrings = line.subSequence(0, position).toString().split("=");
                    panelName = tmpStrings[1].trim().substring(0, tmpStrings[1].trim().length() - 12);
                }
            }
            reader.close();
            mStream.close();
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
        Log.i(TAG, panelName);
        return panelName;
    }

    private String readCustomerIni() {
        String line = null;
        String panelName = "";
        try {
            File file = new File("config/model/Customer_1.ini");
            if (!file.exists()) {
                return "config/panel/FullHD_CMO216_H1L01.ini";
            }
            FileInputStream mStream = new FileInputStream(file);
            BufferedReader reader = new BufferedReader(new InputStreamReader(mStream));
            while ((line = reader.readLine()) != null) {
                if (line.startsWith("m_pPanelName")) {
                    int position = line.indexOf(";");
                    String[] tmpStrings = line.subSequence(0, position).toString().split("=");
                    panelName = tmpStrings[1].trim().substring(2, tmpStrings[1].trim().length() - 1);
                    // "/config/panel/FullHD_CMO216_H1L01.ini"
                    Log.i(TAG, "panelName = " + panelName);
                }
            }
            reader.close();
            mStream.close();
        } catch (FileNotFoundException e1) {
            e1.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return panelName;
    }

    private String readPanelInfo() {
        String panelName = readCustomerIni();
        String width = "1280";
        String height = "720";
        if (panelName == null || panelName.isEmpty()) {
            return width + ";" + height;
        } else {
            String line = null;
            try {
                File file = new File(panelName);
                if (!file.exists()) {
                    return width + ";" + height;
                }
                FileInputStream mStream = new FileInputStream(file);
                BufferedReader reader = new BufferedReader(new InputStreamReader(mStream));
                while ((line = reader.readLine()) != null) {
                    if (line.startsWith("m_wPanelWidth")) {
                        int position = line.indexOf(";");
                        String[] tmpStrings = line.subSequence(0, position).toString().split("=");
                        width = tmpStrings[1].trim();
                        Log.i(TAG, "width = " + width);
                    }
                    if (line.startsWith("m_wPanelHeight")) {
                        int position = line.indexOf(";");
                        String[] tmpStrings = line.subSequence(0, position).toString().split("=");
                        height = tmpStrings[1].trim();
                        Log.i(TAG, "height = " + height);
                    }
                }
                reader.close();
                mStream.close();
            } catch (FileNotFoundException e1) {
                e1.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
            return width + ";" + height;
        }
    }
}
