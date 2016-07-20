//<MStar Software>
//******************************************************************************
// MStar Software
// Copyright (c) 2010 - 2014 MStar Semiconductor, Inc. All rights reserved.
// All software, firmware and related documentation herein ("MStar Software") are
// intellectual property of MStar Semiconductor, Inc. ("MStar") and protected by
// law, including, but not limited to, copyright law and international treaties.
// Any use, modification, reproduction, retransmission, or republication of all
// or part of MStar Software is expressly prohibited, unless prior written
// permission has been granted by MStar.
//
// By accessing, browsing and/or using MStar Software, you acknowledge that you
// have read, understood, and agree, to be bound by below terms ("Terms") and to
// comply with all applicable laws and regulations:
//
// 1. MStar shall retain any and all right, ownership and interest to MStar
//    Software and any modification/derivatives thereof.
//    No right, ownership, or interest to MStar Software and any
//    modification/derivatives thereof is transferred to you under Terms.
//
// 2. You understand that MStar Software might include, incorporate or be
//    supplied together with third party's software and the use of MStar
//    Software may require additional licenses from third parties.
//    Therefore, you hereby agree it is your sole responsibility to separately
//    obtain any and all third party right and license necessary for your use of
//    such third party's software.
//
// 3. MStar Software and any modification/derivatives thereof shall be deemed as
//    MStar's confidential information and you agree to keep MStar's
//    confidential information in strictest confidence and not disclose to any
//    third party.
//
// 4. MStar Software is provided on an "AS IS" basis without warranties of any
//    kind. Any warranties are hereby expressly disclaimed by MStar, including
//    without limitation, any warranties of merchantability, non-infringement of
//    intellectual property rights, fitness for a particular purpose, error free
//    and in conformity with any international standard.  You agree to waive any
//    claim against MStar for any loss, damage, cost or expense that you may
//    incur related to your use of MStar Software.
//    In no event shall MStar be liable for any direct, indirect, incidental or
//    consequential damages, including without limitation, lost of profit or
//    revenues, lost or damage of data, and unauthorized system use.
//    You agree that this Section 4 shall still apply without being affected
//    even if MStar Software has been modified by MStar in accordance with your
//    request or instruction for your use, except otherwise agreed by both
//    parties in writing.
//
// 5. If requested, MStar may from time to time provide technical supports or
//    services in relation with MStar Software to you for your use of
//    MStar Software in conjunction with your or your customer's product
//    ("Services").
//    You understand and agree that, except otherwise agreed by both parties in
//    writing, Services are provided on an "AS IS" basis and the warranty
//    disclaimer set forth in Section 4 above shall apply.
//
// 6. Nothing contained herein shall be construed as by implication, estoppels
//    or otherwise:
//    (a) conferring any license or right to use MStar name, trademark, service
//        mark, symbol or any other identification;
//    (b) obligating MStar or any of its affiliates to furnish any person,
//        including without limitation, you and your customers, any assistance
//        of any kind whatsoever, or any information; or
//    (c) conferring any license or right under any intellectual property right.
//
// 7. These terms shall be governed by and construed in accordance with the laws
//    of Taiwan, R.O.C., excluding its conflict of law rules.
//    Any and all dispute arising out hereof or related hereto shall be finally
//    settled by arbitration referred to the Chinese Arbitration Association,
//    Taipei in accordance with the ROC Arbitration Law and the Arbitration
//    Rules of the Association by three (3) arbitrators appointed in accordance
//    with the said Rules.
//    The place of arbitration shall be in Taipei, Taiwan and the language shall
//    be English.
//    The arbitration award shall be final and binding to both parties.
//
//******************************************************************************
//<MStar Software>

package com.mstar.tv.service;

import java.io.File;
import java.io.IOException;
import java.security.GeneralSecurityException;
import android.content.Context;
import android.content.Intent;
import android.content.ComponentName;
import android.hardware.input.InputManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.os.RecoverySystem;
import android.os.RecoverySystem.ProgressListener;
import android.os.RemoteException;
import android.os.SystemClock;
import android.os.SystemProperties;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.WindowManager;
import android.view.WindowManager.LayoutParams;
import android.util.Log;
import android.util.Pair;

import com.mstar.android.tv.IEventClient;
import com.mstar.android.tv.IAtvPlayerEventClient;
import com.mstar.android.tv.IAudioEventClient;
import com.mstar.android.tv.ICaEventClient;
import com.mstar.android.tv.IDtvPlayerEventClient;
import com.mstar.android.tv.IPvrEventClient;
import com.mstar.android.tv.ITvCommon;
import com.mstar.android.tv.ITvEventClient;
import com.mstar.android.tv.ITvPlayerEventClient;
import com.mstar.android.tv.ITvService;
import com.mstar.android.tvapi.atv.AtvManager;
import com.mstar.android.tvapi.common.TvManager;
import com.mstar.android.tvapi.common.exception.TvCommonException;
import com.mstar.android.tvapi.common.vo.EnumAudioReturn;
import com.mstar.android.tvapi.common.vo.EnumScalerWindow;
import com.mstar.android.tvapi.common.vo.TvOsType.EnumInputSource;
import com.mstar.android.tvapi.common.vo.EnumAtvAudioModeType;
import com.mstar.android.tvapi.common.vo.EnumScreenMuteType;
import com.mstar.android.tvapi.common.vo.VideoWindowType;
import com.mstar.android.tvapi.common.vo.TvTypeInfo;
import com.mstar.android.tvapi.common.vo.Enum3dType;
import com.mstar.android.tvapi.dtv.common.CaManager;
import com.mstar.android.tvapi.dtv.common.DtvManager;
import com.mstar.tv.service.DatabaseDesk;
import com.mstar.tv.service.IDatabaseDesk.UserSetting;
import com.mstar.tv.service.IDatabaseDesk.UserSubtitleSetting;
import com.mstar.tv.util.XmlParser;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.List;

public class TvCommonService extends ITvCommon.Stub {

    private static final String TAG = "TvCommonService";

    private static final String REBOOT_COMMOND = "reboot";

    private static final String STANDBY_COMMOND = "standby";

    private static final String AUTOSLEEP_COMMOND = "autosleep";

    private static final String PVR_REQUEST_POWERDOWN_COMMAND = "pvr";

    private final int SHOW_SURFACE = 0x01;

    private final int SET_SURFACE = 0x02;

    private final int CLOSE_SURFACE = 0x03;

    private static boolean mSourceDetectResult[] = null;

    private static boolean mIsPreviousSourceDTV = false;

    private EnumInputSource mCurrentSourceType = EnumInputSource.E_INPUT_SOURCE_NONE;

    private Context mContext = null;

    private SurfaceHandler mSurfaceHandler = null;

    private HandlerThread mHandlerThread = null;

    private boolean mSurfaceViewFlag = false;

    private SurfaceView mSufaceView = null;

    private LayoutParams mSurfaceParams = null;

    private ITvService mTvService = null;

    private DeskTvEventListener mTvListener = null;

    private DeskTvCommonEventListener mTvCommonEventListener = null;

    private List<Pair<String, Integer>> mAndroidConfigList = null;

    // option codes of TvManager.TV_UNITY_EVENT
    private static final int SWITCH_INPUT_SOURCE = 0x111;

    private static final int UPDATE_INPUT_SOURCE_STATUS = 0x112;

    private static final int AUTO_SWITCH_INPUT_SOURCE = 0x113;

    private static final int CURRENT_INPUT_SOURCE_PLUG_OUT = 0x114;

    private static final int ENABLE_3D_FORMAT = 0x767;

    private static final int DISABLE_3D_FORMAT = 0x768;

    private static final int SYNC_TIME_ANDROID = 0x789;

    private static final int EXIT_SUBWIN = 0x300;

    private static final int GOLDEN_LEFT_EYE = 0x400;

    private int sourceSwitchLevel[] = {
            37, 1, 2, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 16, 0, 0, 0, 0, 0, 0, 38, 39, 40, 0,
            0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0
    };

    private short sourcestatus[] = null;


    // Used in DeskTvEventListener.onUnityEvent
    static public boolean isPreviousSourceDTV() {
        return mIsPreviousSourceDTV;
    }

    // Used in DeskTvEventListener.onUnityEvent
    static public void SetInputSourceStatus(short[] sourceStatus) {
        if (mSourceDetectResult == null)
            mSourceDetectResult = new boolean[EnumInputSource.E_INPUT_SOURCE_NUM.ordinal()];

        for (int i = 0; i < EnumInputSource.E_INPUT_SOURCE_NUM.ordinal(); i++) {
            if (sourceStatus[i] == 0)
                mSourceDetectResult[i] = false;
            else
                mSourceDetectResult[i] = true;
        }
    }

    public TvCommonService(Context context, ITvService service) {
        mContext = context;
        mTvService = service;

        DeskTvEventListener.getInstance().addClient(new TvEventListener());
        mTvListener = DeskTvEventListener.getInstance();
        TvManager.getInstance().setOnEventListener(mTvListener);

        mTvCommonEventListener = DeskTvCommonEventListener.getInstance();
        TvManager.getInstance().getPlayerManager().setOnEventListener(mTvCommonEventListener);

        mAndroidConfigList = getAndroidConfigList();

    }

    @Override
    public void disableTvosIr() {
        try {
            TvManager.getInstance().disableTvosIr();
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean[] GetInputSourceStatus() throws RemoteException {
        // TODO: modify hard coded "GetInputSourceStatus" ?

        if (mSourceDetectResult == null) {
            mSourceDetectResult = new boolean[EnumInputSource.E_INPUT_SOURCE_NUM.ordinal()];
            try {
                short[] sourceStatus = TvManager.getInstance().setTvosCommonCommand(
                        "GetInputSourceStatus");
                for (int i = 0; i < EnumInputSource.E_INPUT_SOURCE_NUM.ordinal(); i++) {
                    mSourceDetectResult[i] = (sourceStatus[i] != 0);
                }
            } catch (TvCommonException e) {
                e.printStackTrace();
            }
        }
        return mSourceDetectResult;
    }

    @Override
    public void enterSleepMode(boolean mode, boolean isNoSignalPowerDown) throws RemoteException {
        try {
            TvManager.getInstance().enterSleepMode(mode, isNoSignalPowerDown);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getAtvMtsMode() throws RemoteException {
        // TODO: modify return type from int to EnumAtvAudioModeType
        // need to modify TV api as well

        try {
            return TvManager.getInstance().getAudioManager().getAtvMtsMode().ordinal();
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public int getAtvSoundMode() throws RemoteException {
        // TODO: modify return type from int to EnumAtvAudioModeType
        // it's totally the same with "getAtvMtsMode", do we need both of them??

        try {
            return TvManager.getInstance().getAudioManager().getAtvSoundMode().ordinal();
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public int getCurrentTvSystem() throws RemoteException {
        try {
            return TvManager.getInstance().getCurrentTvSystem();
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public boolean isCurrentSourceEqualToDatabase() throws RemoteException {
        int dbValue = DatabaseDesk.getInstance(mContext).queryCurrentInputSource();
        int tvosValue = 0;

        try {
            tvosValue = TvManager.getInstance().getCurrentInputSource().ordinal();
        } catch (TvCommonException e) {
            e.printStackTrace();
        }

        if (dbValue == tvosValue) {
            return true;
        } else {
            return false;
        }
    }

    @Override
    public int getCurrentInputSource() throws RemoteException {
        // TODO: modify return type from int to EnumInputSource
        // need to modify TV api as well

        try {
            mCurrentSourceType = TvManager.getInstance().getCurrentInputSource();
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        return mCurrentSourceType.ordinal();
    }

    @Override
    public int getCurrentSubInputSource() throws RemoteException {
        // TODO: modify return type from int to EnumInputSource
        // this function is totally the same with "getCurrentInputSource"
        // should we keep both of them??

        try {
            mCurrentSourceType = TvManager.getInstance().getCurrentSubInputSource();
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        return mCurrentSourceType.ordinal();
    }

    @Override
    public int getCurrentMainInputSource() throws RemoteException {
        try {
            return TvManager.getInstance().getCurrentMainInputSource();
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public boolean getPowerOnAVMute() throws RemoteException {
        return DatabaseDesk.getInstance(mContext).queryPowerOnAVMute();
    }

    @Override
    public int getPowerOnSource() throws RemoteException {
        return DatabaseDesk.getInstance(mContext).queryPowerOnSource();
    }

    @Override
    public int[] getSourceList() throws RemoteException {
        try {
            return TvManager.getInstance().getSourceList();
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        return null;

    }

    @Override
    public TvTypeInfo getTvInfo() throws RemoteException {
        try {
            return TvManager.getInstance().getTvInfo();
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void rebootSystem(String Pwd) throws RemoteException {
        if (Pwd.equals(REBOOT_COMMOND)) {
            Intent intent = new Intent(Intent.ACTION_REBOOT);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            mContext.startActivity(intent);
        }
    }

    @Override
    public void recoverySystem(String url) throws RemoteException {
        final File file = new File(url);
        if (file.exists()) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    RecoverySystem.ProgressListener progressListener = new ProgressListener() {

                        @Override
                        public void onProgress(int progress) {
                        }
                    };

                    try {
                        RecoverySystem.verifyPackage(file, progressListener, null);
                        RecoverySystem.rebootWipeUserData(mContext);
                        RecoverySystem.installPackage(mContext, file);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (GeneralSecurityException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    @Override
    public void updateSystem(String url) throws RemoteException {
        final File file = new File(url);
        if (file.exists()) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    RecoverySystem.ProgressListener progressListener = new ProgressListener() {

                        @Override
                        public void onProgress(int progress) {
                        }
                    };

                    try {
                        RecoverySystem.verifyPackage(file, progressListener, null);
                        RecoverySystem.installPackage(mContext, file);
                    } catch (IOException e) {
                        e.printStackTrace();
                    } catch (GeneralSecurityException e) {
                        e.printStackTrace();
                    }
                }
            }).start();
        }
    }

    @Override
    public int setAtvMtsMode(int mode) throws RemoteException {
        // TODO: modify return type from int to EnumAudioReturn
        // need to modify TV api as well

        try {
            return TvManager.getInstance().getAudioManager()
                    .setAtvMtsMode(EnumAtvAudioModeType.values()[mode]).ordinal();
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public void setInputSource(int source) throws RemoteException {
        // TODO: modify (int source) to (EnumInputSource source)
        // need to modify TV api as well

        mCurrentSourceType = EnumInputSource.values()[source];
        if (EnumInputSource.E_INPUT_SOURCE_ATV == EnumInputSource.values()[source]) {
            mIsPreviousSourceDTV = false;
        } else if (EnumInputSource.E_INPUT_SOURCE_DTV == EnumInputSource.values()[source]) {
            mIsPreviousSourceDTV = true;
        }
        try {
            TvManager.getInstance().setInputSource(EnumInputSource.values()[source]);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setPowerOnAVMute(boolean isEnable) throws RemoteException {
        DatabaseDesk.getInstance(mContext).updatePowerOnAVMute(isEnable);
    }

    @Override
    public boolean setPowerOnSource(int eSource) throws RemoteException {
        // TODO: do we need to return??

        DatabaseDesk.getInstance(mContext).updatePowerOnSource(eSource);
        return true;
    }

    @Override
    public int setToNextAtvMtsMode() throws RemoteException {
        // TODO: modify return from int to EnumAudioReturn
        // need to modify TV api as well

        int result = EnumAudioReturn.E_RETURN_NOTOK.ordinal();
        try {
            result = TvManager.getInstance().getAudioManager().setToNextAtvMtsMode().ordinal();
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        return result;
    }

    @Override
    public int[] setTvosCommonCommand(String command) throws RemoteException {
        try {
            short[] ret = TvManager.getInstance().setTvosCommonCommand(command);
            if (ret.length == 0)
                return null;

            // Convert return value from short[] to int[] because AIDL doesn't
            // support short.
            int[] result = new int[ret.length];
            for (int i = 0; i < ret.length; i++) {
                result[i] = ret[i];
            }
            return result;
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean setVideoMute(boolean bScreenMute, int enColor, int screenUnMuteTime, int eSrcType)
            throws RemoteException {
        try {
            return TvManager.getInstance().setVideoMute(bScreenMute,
                    EnumScreenMuteType.values()[enColor], screenUnMuteTime,
                    EnumInputSource.values()[eSrcType]);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean isSignalStable(int inputSource) throws RemoteException {
        try {
            return TvManager.getInstance().isSignalStable(inputSource);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean isHdmiSignalMode() {
        return TvManager.getInstance().getPlayerManager().isHdmiMode();
    }

    @Override
    public void standbySystem(String password) throws RemoteException {
        boolean ENABLE_STR = SystemProperties.getBoolean("mstar.str.enable", false);
        if (ENABLE_STR == true) {
            if (password.equals(PVR_REQUEST_POWERDOWN_COMMAND)) {
                //Just STR StandBy
                if ("sn".equals(android.os.SystemProperties.get("mstar.build.mstartv"))) {
                    try {
                        TvManager.getInstance().sendStrCommand(
                        TvManager.getInstance().getCurrentInputSource().ordinal(),
                        TvManager.STR_CMD_SET_LAST_INPUT_SOURCE,0);
                        TvManager.getInstance().enterStrMode();
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }
            } else {
                Thread sendPowerKeyDelay = new Thread() {
                    public void run() {
                        int eventCode = KeyEvent.KEYCODE_POWER;
                        long now = SystemClock.uptimeMillis();
                        KeyEvent down = new KeyEvent(now, now, KeyEvent.ACTION_DOWN, eventCode, 0);
                        KeyEvent up = new KeyEvent(now, now, KeyEvent.ACTION_UP, eventCode, 0);

                        InputManager.getInstance().injectInputEvent(down,
                        InputManager.INJECT_INPUT_EVENT_MODE_WAIT_FOR_FINISH);
                        InputManager.getInstance().injectInputEvent(up,
                        InputManager.INJECT_INPUT_EVENT_MODE_WAIT_FOR_FINISH);
                    }
                };
                sendPowerKeyDelay.start();
            }
        } else {
            if (password.equals(STANDBY_COMMOND)) {
                Intent intent = new Intent(Intent.ACTION_REQUEST_SHUTDOWN);
                intent.putExtra(Intent.EXTRA_KEY_CONFIRM, false);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            } else if (password.equals(AUTOSLEEP_COMMOND)) {
                Intent intent = new Intent(Intent.ACTION_REQUEST_SHUTDOWN);
                intent.putExtra(Intent.EXTRA_KEY_CONFIRM, false);
                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                mContext.startActivity(intent);
            }
        }
    }

    @Override
    public void addClient(String name, IBinder client) throws RemoteException {
        String key = "" + getCallingPid();

        // TODO: use constants to instead of hard coding

        if (name.equals("DeskTvEventListener")) {
            DeskTvEventListener.getInstance().addClient(client);
        } else if (name.equals("DeskTvCommonEventListener")) {
            DeskTvCommonEventListener.getInstance().setClient(client);
        }
    }

    @Override
    public IBinder getClient(String name) throws RemoteException {
        return null;
    }

    @Override
    public void removeClient(String name, IBinder client) throws RemoteException {
        if (name.equals("DeskTvCommonEventListener")) {
            DeskTvCommonEventListener.getInstance().releaseClient(client);
        } else if (name.equals("DeskTvEventListener")) {
            DeskTvEventListener.getInstance().removeClient(client);
        }
    }

    @Override
    public void openSurfaceView(int x, int y, int width, int height) throws RemoteException {
        if (mSurfaceHandler == null) {
            mHandlerThread = new HandlerThread("tv_view_handler");
            mHandlerThread.start();
            mSurfaceHandler = new SurfaceHandler(mHandlerThread);
        }

        Message msg = new Message();
        msg.what = SHOW_SURFACE;
        Bundle data = new Bundle();
        data.putInt("x", x);
        data.putInt("y", y);
        data.putInt("width", width);
        data.putInt("height", height);
        msg.setData(data);
        mSurfaceHandler.sendMessage(msg);
    }

    @Override
    public void setSurfaceView(int x, int y, int width, int height) throws RemoteException {
        if (mSurfaceHandler == null) {
            mHandlerThread = new HandlerThread("tv_view_handler");
            mHandlerThread.start();
            mSurfaceHandler = new SurfaceHandler(mHandlerThread);
        }

        Message msg = new Message();
        msg.what = SET_SURFACE;
        Bundle data = new Bundle();
        data.putInt("x", x);
        data.putInt("y", y);
        data.putInt("width", width);
        data.putInt("height", height);
        msg.setData(data);
        mSurfaceHandler.sendMessage(msg);
    }

    @Override
    public void closeSurfaceView() throws RemoteException {
        if (mSurfaceHandler == null) {
            mHandlerThread = new HandlerThread("tv_view_handler");
            mHandlerThread.start();
            mSurfaceHandler = new SurfaceHandler(mHandlerThread);
        }

        mSurfaceHandler.sendEmptyMessage(CLOSE_SURFACE);
    }

    @Override
    public void setCurrentEventStatus(int type, boolean flag) throws RemoteException {
        try {
            DtvManager.getDvbPlayerManager().setCurrentEventStatus(type, flag);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
    }

    /**
     * Return value is mapping with TvCommonManager OSD_DURATION_XXX
     * 0:TvCommonManager#OSD_DURATION_5_SEC
     * 1: TvCommonManager#OSD_DURATION_10_SEC
     * 2: TvCommonManager#OSD_DURATION_15_SEC
     * 3: TvCommonManager#OSD_DURATION_20_SEC
     * 4: TvCommonManager#OSD_DURATION_30_SEC
     * 5: TvCommonManager#OSD_DURATION_ALWAYS
     */
    @Override
    public int getOsdDuration() {
        UserSetting usersetting = DatabaseDesk.getInstance(mContext).queryUserSysSetting();
        int msec = (int) usersetting.menuTimeOut;
        switch (msec) {
            case 5000:
                return 0;
            case 10000:
                return 1;
            case 15000:
                return 2;
            case 20000:
                return 3;
            case 30000:
                return 4;
            default:
                return 5;
        }
    }

    @Override
    public void setOsdDuration(int durationIndex) {
        UserSetting usersetting = DatabaseDesk.getInstance(mContext).queryUserSysSetting();
        switch (durationIndex) {
            case 0:
                usersetting.menuTimeOut = 5000;
                break;
            case 1:
                usersetting.menuTimeOut = 10000;
                break;
            case 2:
                usersetting.menuTimeOut = 15000;
                break;
            case 3:
                usersetting.menuTimeOut = 20000;
                break;
            case 4:
                usersetting.menuTimeOut = 30000;
                break;
            case 5:
                usersetting.menuTimeOut = 0xFFFF;
                break;
        }
        DatabaseDesk.getInstance(mContext).updateUserSysSetting(usersetting);
    }

    @Override
    public int getOsdTimeoutInSecond() {
        UserSetting usersetting = DatabaseDesk.getInstance(mContext).queryUserSysSetting();
        return (int) (usersetting.menuTimeOut / 1000);
    }

    @Override
    public void setOsdLanguage(int language) {
        UserSetting usersetting = DatabaseDesk.getInstance(mContext).queryUserSysSetting();
        usersetting.osdLanguage = language;
        DatabaseDesk.getInstance(mContext).updateUserSysSetting(usersetting);
    }

    @Override
    public int getOsdLanguage() {
        UserSetting usersetting = DatabaseDesk.getInstance(mContext).queryUserSysSetting();
        return usersetting.osdLanguage;
    }

    @Override
    public boolean isSubtitleEnable() {
        UserSubtitleSetting setting = DatabaseDesk.getInstance(mContext).queryUserSubtitleSetting();
        return setting.isSubtitleEnable;
    }

    @Override
    public void setSubtitleEnable(boolean bEnable) {
        UserSubtitleSetting setting = DatabaseDesk.getInstance(mContext).queryUserSubtitleSetting();
        setting.isSubtitleEnable = bEnable;
        DatabaseDesk.getInstance(mContext).updateUserSubtitleSetting(setting);
        try {
            TvManager.getInstance().getPlayerManager().changeSetting(TvManager.SETTING_CHANGE_SUBTITLE_OPTION_CHANGE);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setSubtitlePrimaryLanguage(int language) {
        UserSubtitleSetting setting = DatabaseDesk.getInstance(mContext).queryUserSubtitleSetting();
        setting.subtitleLanguage1 = language;
        DatabaseDesk.getInstance(mContext).updateUserSubtitleSetting(setting);
        try {
            TvManager.getInstance().getPlayerManager().changeSetting(TvManager.SETTING_CHANGE_PRIMARY_SUBTITLE_LANG_CHANGE);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setSubtitleSecondaryLanguage(int language) {
        UserSubtitleSetting setting = DatabaseDesk.getInstance(mContext).queryUserSubtitleSetting();
        setting.subtitleLanguage2 = language;
        DatabaseDesk.getInstance(mContext).updateUserSubtitleSetting(setting);
        try {
            TvManager.getInstance().getPlayerManager().changeSetting(TvManager.SETTING_CHANGE_SECONDARY_SUBTITLE_LANG_CHANGE);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
    }

    @Override
    public int getSubtitlePrimaryLanguage() {
        UserSubtitleSetting setting = DatabaseDesk.getInstance(mContext).queryUserSubtitleSetting();
        return setting.subtitleLanguage1;
    }

    @Override
    public int getSubtitleSecondaryLanguage() {
        UserSubtitleSetting setting = DatabaseDesk.getInstance(mContext).queryUserSubtitleSetting();
        return setting.subtitleLanguage2;
    }

    @Override
    public void setBlueScreenMode(boolean bBlueScreen) {
        UserSetting usersetting = DatabaseDesk.getInstance(mContext).queryUserSysSetting();
        usersetting.bBlueScreen = bBlueScreen;
        DatabaseDesk.getInstance(mContext).updateUserSysSetting(usersetting);
    }

    @Override
    public boolean getBlueScreenMode() {
        UserSetting usersetting = DatabaseDesk.getInstance(mContext).queryUserSysSetting();
        return usersetting.bBlueScreen;
    }

    @Override
    public int getSourceIdentState() {
        return DatabaseDesk.getInstance(mContext).querySourceIdent();
    }

    @Override
    public void setSourceIdentState(int currentState) {
        DatabaseDesk.getInstance(mContext).updateSourceIdent(currentState);
    }

    @Override
    public int getSourcePreviewState() {
        return DatabaseDesk.getInstance(mContext).querySourcePreview();
    }

    @Override
    public void setSourcePreviewState(int currentState) {
        DatabaseDesk.getInstance(mContext).updateSourcePreview(currentState);
    }

    @Override
    public int getSourceSwitchState() {
        return DatabaseDesk.getInstance(mContext).querySourceSwitch();
    }

    @Override
    public void setSourceSwitchState(int currentState) {
        DatabaseDesk.getInstance(mContext).updateSourceSwitch(currentState);
    }

    @Override
    public int getAndroidConfigSetting(String configName) {
        if (mAndroidConfigList != null) {
            for (Pair<String, Integer> p : mAndroidConfigList) {
                if (p.first.equals(configName)) {
                    Log.d(TAG, "getAndroidConfigSetting return = " + p.second);
                    return p.second;
                }
            }
        }
        return -1;
    }

    @Override
    public String getCompilerFlag(String flag) throws RemoteException {
        try {
            return TvManager.getInstance().getCompilerFlag(flag);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public void setDpmsWakeUpEnable(boolean enable) throws RemoteException {
        try {
            TvManager.getInstance().setDpmsWakeUpEnable(enable);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean processTvAsyncCommand() throws RemoteException {
        try {
            return TvManager.getInstance().processTvAsyncCommand();
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public int[] getHdmiEdidVersionList() throws RemoteException {
        try {
            return TvManager.getInstance().getHdmiEdidVersionList();
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int getHdmiEdidVersion() {
        return DatabaseDesk.getInstance(mContext).queryHdmiEdidVersion();
    }

    @Override
    public boolean setHdmiEdidVersion(int iHdmiEdidVersion) {
        boolean ret = false;
        try {
            ret = TvManager.getInstance().setHdmiEdidVersion(iHdmiEdidVersion);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        if (ret == true) {
            DatabaseDesk.getInstance(mContext).updataHdmiEdidVersion(iHdmiEdidVersion);
        }
        return ret;
    }

    @Override
    public int getHdmiEdidVersionBySource(int inputSource) {
        try {
            return TvManager.getInstance().getPlayerManager().getHdmiEdidVersionBySource(inputSource);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public boolean setHdmiEdidVersionBySource(int inputSource, int hdmiEdidVersion) {
        try {
            return TvManager.getInstance().getPlayerManager().setHdmiEdidVersionBySource(inputSource, hdmiEdidVersion);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        return false;
    }

    public boolean getInputSourceLock(int nInputSource) throws RemoteException {
        Log.d(TAG, "getInputSourceLock nInputSource = " + nInputSource);
        try {
            return TvManager.getInstance().getInputSourceLock(nInputSource);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean setInputSourceLock(boolean bLock, int nInputSource) throws RemoteException {
        Log.d(TAG, "setInputSourceLock bLock = " + bLock + " , nInputSource = " + nInputSource);
        try {
            return TvManager.getInstance().setInputSourceLock(bLock, nInputSource);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean resetInputSourceLock() throws RemoteException {
        Log.d(TAG, "resetInputSourceLock()");
        try {
            return TvManager.getInstance().resetInputSourceLock();
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        return false;
    }

    private List<Pair<String, Integer>> getAndroidConfigList() {
        XmlParser parser = XmlParser.getInstance();
        if (parser.parseXml("/etc/feature.xml")) {
            Log.d(TAG, "getAndroidConfigList: parse xml successfully");
            return parser.getList();
        } else {
            Log.d(TAG, "getAndroidConfigList: parse xml failed");
        }
        return null;
    }

    private class SurfaceHandler extends Handler {
        public SurfaceHandler(HandlerThread thread) {
            super(thread.getLooper());
        }

        @Override
        public void handleMessage(Message msg) {
            switch (msg.what) {
                case SHOW_SURFACE: {
                    Bundle bundle = msg.getData();
                    this.openSurfaceView(bundle.getInt("x", 0), bundle.getInt("y", 0),
                            bundle.getInt("width", 500), bundle.getInt("height", 500));
                    break;
                }
                case SET_SURFACE: {
                    Bundle bundle = msg.getData();
                    this.setSurfaceView(bundle.getInt("x", 0), bundle.getInt("y", 0),
                            bundle.getInt("width", 500), bundle.getInt("height", 500));
                    break;
                }
                case CLOSE_SURFACE: {
                    this.closeSurfaceView();
                }
            }
        }

        private void closeSurfaceView() {
            if (mSurfaceViewFlag) {
                ((WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE))
                        .removeView(mSufaceView);
                mSurfaceViewFlag = false;
            }

            try {
                mTvService.getTvPicture().setVideoArc(mTvService.getTvPicture().getVideoArc());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

        private void setSurfaceView(int x, int y, int width, int height) {
            if (mSurfaceViewFlag) {

                mSurfaceParams.x = x;
                mSurfaceParams.y = y;
                mSurfaceParams.width = width;
                mSurfaceParams.height = height;

                VideoWindowType videoWindowType = new VideoWindowType();
                videoWindowType.height = mSurfaceParams.height;
                videoWindowType.width = mSurfaceParams.width;
                videoWindowType.x = mSurfaceParams.x;
                videoWindowType.y = mSurfaceParams.y;
                try {
                    TvManager.getInstance().getPictureManager()
                            .selectWindow(EnumScalerWindow.E_MAIN_WINDOW);
                    TvManager.getInstance().getPictureManager().setDisplayWindow(videoWindowType);
                    TvManager.getInstance().getPictureManager().scaleWindow();
                } catch (Throwable e) {
                    e.printStackTrace();
                }

                ((WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE))
                        .updateViewLayout(mSufaceView, mSurfaceParams);
            }
        }

        private void openSurfaceView(int x, int y, int width, int height) {
            if (mSurfaceViewFlag) {
                return;
            }

            mSufaceView = new SurfaceView(mContext);
            mSufaceView.getHolder().setType(SurfaceHolder.SURFACE_TYPE_PUSH_BUFFERS);
            mSurfaceParams = new LayoutParams();
            mSurfaceParams.x = x;
            mSurfaceParams.y = y;
            mSurfaceParams.width = width;
            mSurfaceParams.height = height;
            mSurfaceParams.type = WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;
            mSurfaceParams.gravity = Gravity.TOP | Gravity.LEFT;
            android.view.SurfaceHolder.Callback callback = new android.view.SurfaceHolder.Callback() {

                @Override
                public void surfaceDestroyed(SurfaceHolder holder) {
                }

                @Override
                public void surfaceCreated(SurfaceHolder holder) {
                    try {
                        TvManager.getInstance().getPlayerManager().setDisplay(holder);

                        VideoWindowType videoWindowType = new VideoWindowType();
                        videoWindowType.height = mSurfaceParams.height;
                        videoWindowType.width = mSurfaceParams.width;
                        videoWindowType.x = mSurfaceParams.x;
                        videoWindowType.y = mSurfaceParams.y;
                        TvManager.getInstance().getPictureManager()
                                .selectWindow(EnumScalerWindow.E_MAIN_WINDOW);
                        TvManager.getInstance().getPictureManager()
                                .setDisplayWindow(videoWindowType);
                        TvManager.getInstance().getPictureManager().scaleWindow();
                    } catch (Throwable e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
                }
            };

            mSufaceView.getHolder().addCallback(callback);

            ((WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE)).addView(
                    mSufaceView, mSurfaceParams);

            mSurfaceViewFlag = true;
        }
    }

    private class TvEventListener extends IEventClient.Stub {
        @Override
        public boolean onEvent(Message msg) {
            switch (msg.what) {
                case TvManager.TV_UNITY_EVENT: {
                    if (SWITCH_INPUT_SOURCE == msg.arg1) {
                        EnumInputSource inputSource = EnumInputSource.values()[msg.arg2];
                        EnumInputSource curInputSource = EnumInputSource.E_INPUT_SOURCE_NONE;
                        try {
                            curInputSource = TvManager.getInstance().getCurrentInputSource();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        {
                            if ((inputSource.ordinal() >= EnumInputSource.E_INPUT_SOURCE_STORAGE.ordinal())
                                && (curInputSource.ordinal() != EnumInputSource.E_INPUT_SOURCE_VGA2.ordinal())
                                && (curInputSource.ordinal() != EnumInputSource.E_INPUT_SOURCE_VGA3.ordinal())) {
                                Intent intent = new Intent();

                                ComponentName comp = new ComponentName("com.mstar.ui.activity.main",
                                        "com.mstar.ui.activity.main.MainActivity");

                                intent.setComponent(comp);
                                intent.setAction("android.intent.action.VIEW");
                                intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);

                                mContext.startActivity(intent);
                            } else {
                                if ((curInputSource.ordinal() >= EnumInputSource.E_INPUT_SOURCE_STORAGE.ordinal())
                                    && (curInputSource.ordinal() != EnumInputSource.E_INPUT_SOURCE_VGA2.ordinal())
                                    && (curInputSource.ordinal() != EnumInputSource.E_INPUT_SOURCE_VGA3.ordinal())) {
                                    Intent intent = new Intent(
                                            "com.mstar.tvsetting.hotkey.intent.action.ProgressActivity");
                                    intent.putExtra("task_tag", "input_source_changed");
                                    intent.putExtra("inputSource", EnumInputSource.values()[msg.arg2]);
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    try {
                                        mContext.startActivity(intent);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                } else {
                                    try {
                                        TvManager.getInstance().setInputSource(
                                                EnumInputSource.values()[inputSource.ordinal()]);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                    Intent intent = new Intent("mstar.tvsetting.ui.intent.action.RootActivity");
                                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                    try {
                                        mContext.startActivity(intent);
                                    } catch (Exception e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                        }
                    } else if (ENABLE_3D_FORMAT == msg.arg1) {
                        if (msg.arg2 > Enum3dType.EN_3D_NONE.ordinal()) {
                            Log.i(TAG, "Enable 3D Moden");
                        }
                    } else if (DISABLE_3D_FORMAT == msg.arg1) {
                        Log.i(TAG, "Disable 3D Mode\n");
                    } else if (SYNC_TIME_ANDROID == msg.arg1) {
                        if (msg.arg2 > 0) {
                            long millis = (long) msg.arg2 * 1000;
                            SystemClock.setCurrentTimeMillis(millis);
                        }
                    } else if (EXIT_SUBWIN == msg.arg1) {
                        Log.i(TAG, "Exit Sub Win\n");

                        Intent intent = new Intent("com.mstar.pipservice");
                        intent.putExtra("cmd", "removepip");
                        try {
                            intent.setComponent(new ComponentName("com.mstar.miscsetting", "com.mstar.miscsetting.service.PipService"));
                            mContext.startService(intent);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    } else if (UPDATE_INPUT_SOURCE_STATUS == msg.arg1) {
                        try {
                            sourcestatus = TvManager.getInstance().setTvosCommonCommand("GetInputSourceStatus");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        TvCommonService.SetInputSourceStatus(sourcestatus);

                        // Broadcast input source status changes
                        Intent intent = new Intent();
                        intent.setAction("com.mstar.tv.service.COMMON_EVENT_SIGNAL_STATUS_UPDATE");
                        intent.putExtra("SwitchSourceIndex", msg.arg2);
                        Log.d(TAG, "Broadcast, input source status changes! ");
                        mContext.sendBroadcast(intent);
                    } else if (AUTO_SWITCH_INPUT_SOURCE == msg.arg1) {
                        try {
                            sourcestatus = TvManager.getInstance().setTvosCommonCommand("GetInputSourceStatus");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        TvCommonService.SetInputSourceStatus(sourcestatus);

                        // close auto switch function on Main Line
                        Intent intent = new Intent();
                        intent.setAction("com.mstar.tv.service.COMMON_EVENT_SIGNAL_AUTO_SWITCH");
                        intent.putExtra("SwitchSourceIndex", msg.arg2);
                        Log.d(TAG, "Broadcast, auto switch input source status changes!");
                        mContext.sendBroadcast(intent);
                    } else if (CURRENT_INPUT_SOURCE_PLUG_OUT == msg.arg1) {
                        try {
                            sourcestatus = TvManager.getInstance().setTvosCommonCommand("GetInputSourceStatus");
                        } catch (Exception e) {
                            e.printStackTrace();
                        }

                        TvCommonService.SetInputSourceStatus(sourcestatus);

                        // close auto switch function on Main Line
                        Intent intent = new Intent();
                        intent.setAction("com.mstar.tv.service.COMMON_EVENT_SIGNAL_PLUG_OUT");
                        intent.putExtra("SwitchSourceIndex", msg.arg2);
                        Log.d(TAG, "Broadcast, plug out input source status changes! ");
                        mContext.sendBroadcast(intent);
                    } else if (GOLDEN_LEFT_EYE == msg.arg1) {
                        Intent intent = new Intent();
                        intent.setAction("com.mstar.tv.service.GOLDEN_LEFT_EYE");
                        intent.putExtra("position_x", msg.arg2);
                        Log.d(TAG, "Broadcast, position x  changes! ");
                        mContext.sendBroadcast(intent);
                    }
                }

                default: {
                }
                    break;
            }
            return true;
        }
    }
	
	// EosTek Patch Begin
	@Override
	public boolean saveAtvProgram(int currentProgramNo){
        Log.d(TAG, "saveAtvProgram , paras current programNo = " + currentProgramNo);
        if (AtvManager.getAtvPlayerManager()!= null) {
            try {
                return AtvManager.getAtvPlayerManager().saveAtvProgram(currentProgramNo);
            } catch (TvCommonException e) {
                // TODO Auto-generated catch block
                e.printStackTrace();
            }
        }
        return false;
	}
	// EosTek Patch End
}
