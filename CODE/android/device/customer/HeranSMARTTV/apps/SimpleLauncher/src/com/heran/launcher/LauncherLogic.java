
package com.heran.launcher;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;

import com.heran.launcher.util.Constants;
import com.heran.launcher.util.Utils;
import com.mstar.android.tv.TvChannelManager;
import com.mstar.android.tv.TvCommonManager;
import com.mstar.android.tvapi.common.TvManager;
import com.mstar.android.tvapi.common.exception.TvCommonException;
import com.mstar.android.tvapi.common.vo.EnumProgramInfoType;
import com.mstar.android.tvapi.common.vo.EnumScalerWindow;
import com.mstar.android.tvapi.common.vo.ProgramInfo;
import com.mstar.android.tvapi.common.vo.ProgramInfoQueryCriteria;
import com.mstar.android.tvapi.common.vo.TvOsType.EnumInputSource;
import com.mstar.android.tvapi.common.vo.VideoWindowType;

import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Bundle;
import android.os.ServiceManager;
import android.os.SystemProperties;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.IWindowManager;

@SuppressWarnings("deprecation")
public class LauncherLogic {
    private static final String TAG = "LauncherLogic";

    LauncherActivity mContext;

    public LauncherLogic(LauncherActivity context) {
        mContext = context;
    }

    /**
     * Set small screen
     */
    public void setSmallscale(boolean isCancelScale) {
        Log.v(TAG, "setSmallscale isCancelScale: " + isCancelScale);
        Log.v(TAG, "setSmallscale isAdVideoFinish: " + Utils.isAdVideoFinish());
        if (isCancelScale || !Utils.isAdVideoFinish()) {
            return;
        }
        try {
            Rect rect = new Rect();
            mContext.mHolder.mTv_sur.getGlobalVisibleRect(rect);
            VideoWindowType videoWindowType = new VideoWindowType();
            // videoWindowType.height = 579;
            // videoWindowType.width = 1033;
            //            videoWindowType.x = 442;
            // videoWindowType.y = 249;
            videoWindowType.height = (int) (rect.height() * 2);
            videoWindowType.width = (int) (rect.width() * 2);
            videoWindowType.x = (int) (rect.left * 2);
            videoWindowType.y = (int) (rect.top *2);
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
     * Source Change tv surface change size & programSel
     * 
     * @return void
     */
    public void isSourceChange(Intent intent, boolean isCancelScale) {
        Boolean isSourceChange = intent.getBooleanExtra("isSourceChange", false);
        Boolean isChangeChannel = intent.getBooleanExtra("isChangeChannel", false);
        Log.d(TAG, "isSourceChange:" + isSourceChange);
        Log.d(TAG, "isChangeChannel:" + isChangeChannel);
        // source change tv surface changer
        if (isSourceChange) {
            setSmallscale(isCancelScale);
        }
        // programSel
        if (isChangeChannel) {
            ProgramInfo pinfo = getCurrProgramInfo();
            programSel(pinfo.number, pinfo.serviceType);
        }
    }

    public void setPowerOnSource() {
        int curSourceID = TvCommonManager.getInstance().getPowerOnSource().ordinal();
        Log.v(TAG, "curSourceID:" + curSourceID);
        if ((curSourceID >= 0) && (curSourceID <= EnumInputSource.E_INPUT_SOURCE_NONE.ordinal())) {
            TvCommonManager.getInstance().setInputSource(EnumInputSource.values()[curSourceID].ordinal());
        }
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
     * query the current input source
     * 
     * @return InputSourceType
     */
    public int queryCurInputSrc() {
        int value = 0;
        Cursor cursor = mContext.getContentResolver().query(Uri.parse("content://mstar.tv.usersetting/systemsetting"),
                null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            value = cursor.getInt(cursor.getColumnIndex("enInputSourceType"));
        }
        if (cursor != null) {
            cursor.close();
        }
        return value;
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

    public void handFirstPowerOnEvent() {
        Log.i(TAG, "power1 = " + SystemProperties.get(Constants.FIRST_POWER_ON, "0"));
        if (SystemProperties.get(Constants.FIRST_POWER_ON, "0").equals("0")) {
            Log.d(TAG, "This is first power on");
            judgeToJumpFullTvPlayer();
            setPropertyForSTR(Constants.FIRST_POWER_ON, "1");
        }
        Log.i(TAG, "power2 = " + SystemProperties.get(Constants.FIRST_POWER_ON, "0"));
    }

    private void judgeToJumpFullTvPlayer() {
        if (!Utils.isAdVideoFinish()) {
            startTV();
        }
    }

    private void startTV() {
        String pkgName = "com.eostek.tv.player";
        String clsName = "com.eostek.tv.player.PlayerActivity";
        ComponentName componentName = new ComponentName(pkgName, clsName);
        Intent intent = new Intent();
        intent.putExtra("isAdVideo", true);
        intent.setComponent(componentName);
        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
        mContext.startActivity(intent);
    }

    public void setPropertyForSTR(String key, String value) {
        IWindowManager winService = IWindowManager.Stub
                .asInterface(ServiceManager.checkService(Context.WINDOW_SERVICE));
        if (winService == null) {
            Log.w(TAG, "Unable to find IWindowManger interface.");
        } else {
            SystemProperties.set(key, value);
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
        // if (!pckName.equals("com.eostek.tv.player")) {
        // TvCommonManager.getInstance().setInputSource(TvCommonManager.INPUT_SOURCE_STORAGE);
        // }
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
    
    
}
