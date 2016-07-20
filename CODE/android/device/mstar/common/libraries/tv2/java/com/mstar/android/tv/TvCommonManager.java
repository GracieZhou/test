//<MStar Software>
//******************************************************************************
// MStar Software
// Copyright (c) 2010 - 2015 MStar Semiconductor, Inc. All rights reserved.
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

package com.mstar.android.tv;

import java.util.ArrayList;
import android.util.Log;
import android.content.ComponentName;
import android.content.Context;
import android.content.ServiceConnection;
import android.os.Looper;
import android.os.Handler;
import android.os.IBinder;
import android.os.RemoteException;
import android.os.ServiceManager;
import android.content.Intent;
import android.os.Bundle;
import android.os.Message;
import android.os.RecoverySystem;
import android.os.SystemClock;
import android.os.RecoverySystem.ProgressListener;
import android.view.IWindowManager;
import android.view.KeyEvent;
import android.os.SystemProperties;

import com.mstar.android.tv.ITvCommon;
import com.mstar.android.tv.TvLanguage;
import com.mstar.android.tv.handler.OnTvCommonEventHandler;
import com.mstar.android.tvapi.common.TvManager;
import com.mstar.android.tvapi.common.vo.EnumAtvAudioModeType;
import com.mstar.android.tvapi.common.vo.EnumAudioReturn;
import com.mstar.android.tvapi.common.vo.EnumScreenMuteType;
import com.mstar.android.tvapi.common.vo.TvOsType.EnumInputSource;
import com.mstar.android.tvapi.common.vo.TvTypeInfo;
import com.mstar.android.tvapi.common.listener.OnTvEventListener;
import com.mstar.android.tvapi.common.exception.TvCommonException;
import com.mstar.android.tvapi.impl.PlayerImpl;

import java.util.List;
import java.util.ArrayList;
import java.util.HashMap;

/**
 * <b>TvCommonManager class is for purpose of general management
 * from client APK.</b><br/>
 */
public class TvCommonManager  extends IEventClient.Stub {
    private static final String TAG = "TvCommonManager";

    /**
     * DTV status is ui exist.
     */
    public static final int DTV_STATUS_UI_EXIST = 0;
    /**
     * DTV status is pvr running.
     */
    public static final int DTV_STATUS_PVR_RUNNING = 1;
    /**
     * DTV status is cc running.
     */
    public static final int DTV_STATUS_CC_RUNNING = 2;
    /**
     * DTV status is channel change.
     */
    public static final int DTV_STATUS_CHANNEL_CHANGE = 3;
    /**
     * DTV status is source change.
     */
    public static final int DTV_STATUS_SOURCE_CHANGE = 4;
    /**
     * DTV status is program locked.
     */
    public static final int DTV_STATUS_PROGRAM_LOCKED = 5;

    // Osd Menu show duration
    public static final int DURATION_INDEX_5_SEC = 0;
    public static final int DURATION_INDEX_10_SEC = 1;
    public static final int DURATION_INDEX_15_SEC = 2;
    public static final int DURATION_INDEX_20_SEC = 3;
    public static final int DURATION_INDEX_30_SEC = 4;
    public static final int DURATION_INDEX_ALWAYS = 5;

    /**
     * Input sources.
     */
    public static final int INPUT_SOURCE_VGA = 0;
    public static final int INPUT_SOURCE_ATV = 1;
    public static final int INPUT_SOURCE_CVBS = 2;
    public static final int INPUT_SOURCE_CVBS2 = 3;
    public static final int INPUT_SOURCE_CVBS3 = 4;
    public static final int INPUT_SOURCE_CVBS4 = 5;
    public static final int INPUT_SOURCE_CVBS5 = 6;
    public static final int INPUT_SOURCE_CVBS6 = 7;
    public static final int INPUT_SOURCE_CVBS7 = 8;
    public static final int INPUT_SOURCE_CVBS8 = 9;
    public static final int INPUT_SOURCE_CVBS_MAX = 10;
    public static final int INPUT_SOURCE_SVIDEO = 11;
    public static final int INPUT_SOURCE_SVIDEO2 = 12;
    public static final int INPUT_SOURCE_SVIDEO3 = 13;
    public static final int INPUT_SOURCE_SVIDEO4 = 14;
    public static final int INPUT_SOURCE_SVIDEO_MAX = 15;
    public static final int INPUT_SOURCE_YPBPR = 16;
    public static final int INPUT_SOURCE_YPBPR2 = 17;
    public static final int INPUT_SOURCE_YPBPR3 = 18;
    public static final int INPUT_SOURCE_YPBPR_MAX = 19;
    public static final int INPUT_SOURCE_SCART = 20;
    public static final int INPUT_SOURCE_SCART2 = 21;
    public static final int INPUT_SOURCE_SCART_MAX = 22;
    public static final int INPUT_SOURCE_HDMI = 23;
    public static final int INPUT_SOURCE_HDMI2 = 24;
    public static final int INPUT_SOURCE_HDMI3 = 25;
    public static final int INPUT_SOURCE_HDMI4 = 26;
    public static final int INPUT_SOURCE_HDMI_MAX = 27;
    public static final int INPUT_SOURCE_DTV = 28;
    public static final int INPUT_SOURCE_DVI = 29;
    public static final int INPUT_SOURCE_DVI2 = 30;
    public static final int INPUT_SOURCE_DVI3 = 31;
    public static final int INPUT_SOURCE_DVI4 = 32;
    public static final int INPUT_SOURCE_DVI_MAX = 33;
    public static final int INPUT_SOURCE_STORAGE = 34;
    public static final int INPUT_SOURCE_KTV = 35;
    public static final int INPUT_SOURCE_JPEG = 36;
    public static final int INPUT_SOURCE_DTV2 = 37;
    public static final int INPUT_SOURCE_STORAGE2 = 38;
    public static final int INPUT_SOURCE_DIV3 = 39;
    public static final int INPUT_SOURCE_SCALER_OP = 40;
    public static final int INPUT_SOURCE_RUV = 41;
    public static final int INPUT_SOURCE_VGA2 = 42;
    public static final int INPUT_SOURCE_VGA3 = 43;
    public static final int INPUT_SOURCE_NUM = 44;
    public static final int INPUT_SOURCE_NONE = 44;

    /**
     * Screen Mute types.
     */
    public static final int SCREEN_MUTE_BLACK = 0;
    public static final int SCREEN_MUTE_WHITE = 1;
    public static final int SCREEN_MUTE_RED = 2;
    public static final int SCREEN_MUTE_BLUE = 3;
    public static final int SCREEN_MUTE_GREEN = 4;
    public static final int SCREEN_MUTE_NUM = 5;

    /**
     * Modules belong to compiler flag catagory
     */
    /** Module: PIP */
    public static final int MODULE_PIP = 0;
    /** Module: TRAVELING */
    public static final int MODULE_TRAVELING = 1;
    /** Module: OFFLINE_DETECT */
    public static final int MODULE_OFFLINE_DETECT = 2;
    /** Module: PREVIEW_MODE */
    public static final int MODULE_PREVIEW_MODE = 3;
    /** Module: FREEVIEW_AU */
    public static final int MODULE_FREEVIEW_AU = 4;
    // TODO: deprecated MODULE_CC/MODULE_BRAZIL_CC after SN remove it
    /** Module: CC */
    public static final int MODULE_CC = 5;
    /** Module: BRAZIL_CC */
    public static final int MODULE_BRAZIL_CC = 6;
    /** Module: KOREAN_CC */
    public static final int MODULE_KOREAN_CC = 7;
    /** For ATSC system, enable ATSC_CC_ENABLE and NTSC_CC_ENABLE. */
    /** For ISDB system, enable ISDB_CC_ENABLE and NTSC_CC_ENABLE. */
    /** For ASIA_NTSC system, only enable NTSC_CC_ENABLE. */
    /** Module: ATSC_CC_ENABLE */
    public static final int MODULE_ATSC_CC_ENABLE = 8;
    /** Module: ISDB_CC_ENABLE */
    public static final int MODULE_ISDB_CC_ENABLE = 9;
    /** Module: NTSC_CC_ENABLE */
    public static final int MODULE_NTSC_CC_ENABLE = 10;
    /** Module: ATV_NTSC_ENABLE */
    public static final int MODULE_ATV_NTSC_ENABLE = 11;
    /** Module: ATV_PAL_ENABLE */
    public static final int MODULE_ATV_PAL_ENABLE = 12;
    /** Module: ATV_CHINA_ENABLE */
    public static final int MODULE_ATV_CHINA_ENABLE = 13;
    /** Module: ATV_PAL_M_ENABLE */
    public static final int MODULE_ATV_PAL_M_ENABLE = 14;
    /** Module: HDMITX */
    public static final int MODULE_HDMITX = 15;
    /** Module: HBBTV */
    public static final int MODULE_HBBTV = 16;
    /** Module: INPUT_SOURCE_LOCK */
    public static final int MODULE_INPUT_SOURCE_LOCK = 17;
    /** Module: EPG */
    public static final int MODULE_EPG = 18;
    /** Module: AD_SWITCH */
    public static final int MODULE_AD_SWITCH = 19;

    /**
     * Module not belong to compiler flag catagory
     */
    /** Start number of modules that are not compiler flags */
    public static final int MODULE_NOT_COMPILE_FLAG_START = 0x00001000;
    /** Module: ATV_MANUAL_TUNING */
    public static final int MODULE_TV_CONFIG_ATV_MANUAL_TUNING = MODULE_NOT_COMPILE_FLAG_START;
    /** Module: AUTO_HOH */
    public static final int MODULE_TV_CONFIG_AUTO_HOH = MODULE_NOT_COMPILE_FLAG_START + 1;
    /** Module: AUDIO_DESCRIPTION */
    public static final int MODULE_TV_CONFIG_AUDIO_DESCRIPTION = MODULE_NOT_COMPILE_FLAG_START + 2;
    /** Module: THREED_DEPTH */
    public static final int MODULE_TV_CONFIG_THREED_DEPTH = MODULE_NOT_COMPILE_FLAG_START + 3;
    /** Module: SELF_DETECT */
    public static final int MODULE_TV_CONFIG_SELF_DETECT = MODULE_NOT_COMPILE_FLAG_START + 4;
    /** Module: THREED_CONVERSION_TWODTOTHREED */
    public static final int MODULE_TV_CONFIG_THREED_CONVERSION_TWODTOTHREED = MODULE_NOT_COMPILE_FLAG_START + 5;
    /** Module: THREED_CONVERSION_AUTO */
    public static final int MODULE_TV_CONFIG_THREED_CONVERSION_AUTO = MODULE_NOT_COMPILE_FLAG_START + 6;
    /** Module: THREED_CONVERSION_PIXEL_ALTERNATIVE */
    public static final int MODULE_TV_CONFIG_THREED_CONVERSION_PIXEL_ALTERNATIVE = MODULE_NOT_COMPILE_FLAG_START + 7;
    /** Module: THREED_CONVERSION_FRAME_ALTERNATIVE */
    public static final int MODULE_TV_CONFIG_THREED_CONVERSION_FRAME_ALTERNATIVE = MODULE_NOT_COMPILE_FLAG_START + 8;
    /** Module: THREED_CONVERSION_CHECK_BOARD */
    public static final int MODULE_TV_CONFIG_THREED_CONVERSION_CHECK_BOARD = MODULE_NOT_COMPILE_FLAG_START + 9;
    /** Module: THREED_TWOD_AUTO */
    public static final int MODULE_TV_CONFIG_THREED_TWOD_AUTO = MODULE_NOT_COMPILE_FLAG_START + 10;
    /** Module: THREED_TWOD_PIXEL_ALTERNATIVE */
    public static final int MODULE_TV_CONFIG_THREED_TWOD_PIXEL_ALTERNATIVE = MODULE_NOT_COMPILE_FLAG_START + 11;
    /** Module: THREED_TWOD_FRAME_ALTERNATIVE */
    public static final int MODULE_TV_CONFIG_THREED_TWOD_FRAME_ALTERNATIVE = MODULE_NOT_COMPILE_FLAG_START + 12;
    /** Module: THREED_TWOD_CHECK_BOARD */
    public static final int MODULE_TV_CONFIG_THREED_TWOD_CHECK_BOARD = MODULE_NOT_COMPILE_FLAG_START + 13;

    /** ATV Audio Mode */
    /* This value is mapping to ATV_AUDIOMODE_TYPE(supernova). */
    /** Audio Mode Invalid */
    public static final int ATV_AUDIOMODE_INVALID = 0;
    /** Audio Mode MONO */
    public static final int ATV_AUDIOMODE_MONO = 1;
    /** Audio Mode Forced MONO */
    public static final int ATV_AUDIOMODE_FORCED_MONO = 2;
    /** Audio Mode G Stereo */
    public static final int ATV_AUDIOMODE_G_STEREO = 3;
    /** Audio Mode K Stereo */
    public static final int ATV_AUDIOMODE_K_STEREO = 4;
    /** Audio Mode Mono SAP */
    public static final int ATV_AUDIOMODE_MONO_SAP = 5;
    /** Audio Mode Stereo SAP */
    public static final int ATV_AUDIOMODE_STEREO_SAP = 6;
    /** Audio Mode Dual A */
    public static final int ATV_AUDIOMODE_DUAL_A = 7;
    /** Audio Mode Dual B */
    public static final int ATV_AUDIOMODE_DUAL_B = 8;
    /** Audio Mode Dual AB */
    public static final int ATV_AUDIOMODE_DUAL_AB = 9;
    /** Audio Mode NICAM MONO */
    public static final int ATV_AUDIOMODE_NICAM_MONO = 10;
    /** Audio Mode NICAM Stereo */
    public static final int ATV_AUDIOMODE_NICAM_STEREO = 11;
    /** Audio Mode NICAM DUAL A */
    public static final int ATV_AUDIOMODE_NICAM_DUAL_A = 12;
    /** Audio Mode NICAM DUAL B */
    public static final int ATV_AUDIOMODE_NICAM_DUAL_B = 13;
    /** Audio Mode NICAM DUAL AB */
    public static final int ATV_AUDIOMODE_NICAM_DUAL_AB = 14;
    /** Audio Mode HIDEV MONO */
    public static final int ATV_AUDIOMODE_HIDEV_MONO = 15;
    /** Audio Mode left left */
    public static final int ATV_AUDIOMODE_LEFT_LEFT = 16;
    /** Audio Mode right right */
    public static final int ATV_AUDIOMODE_RIGHT_RIGHT = 17;
    /** Audio Mode left right */
    public static final int ATV_AUDIOMODE_LEFT_RIGHT = 18;

    /** Audio Function Return Value Type */
    /* This value is mapping to MSRV_SSSND_RET(supernova). */
    /** Return not Okay */
    public static final int AUDIO_RETURN_NOT_OK = 0;
    /** Return Okay */
    public static final int AUDIO_RETURN_OK = 1;
    /** Return unsupport format */
    public static final int AUDIO_RETURN_UNSUPPORT = 2;

    /**
     * Parameter of onPopupDialog Event for show Password Inputbox
     */
    public static final int POPUP_DIALOG_SHOW = 0;
    /**
     * Parameter of onPopupDialog Event for hide Password Inputbox
     */
    public static final int POPUP_DIALOG_HIDE = 1;

    /** Events */
    /** NIT auto update scan notification */
    public static final int EV_DTV_AUTO_UPDATE_SCAN = PlayerImpl.TVPLAYER_DTV_AUTO_UPDATE_SCAN;
    /** EPG update notification*/
    public static final int EV_EPG_UPDATE = PlayerImpl.TVPLAYER_EPG_UPDATE;

    /** NIT Event sub types */
    /** NIT update type - None */
    public static final int NIT_UPDATE_NONE = 0;
    /** NIT update type - Multiplexer add */
    public static final int NIT_UPDATE_MUX_ADD = 1;
    /** NIT update type - Frequency change */
    public static final int NIT_UPDATE_FREQ_CHANGE = 2;
    /** NIT update type - Multiplexer remove */
    public static final int NIT_UPDATE_MUX_REMOVE = 3;
    /** NIT update type - Cell remove */
    public static final int NIT_UPDATE_CELL_REMOVE = 4;

    /** HDMI EDID version */
    /** EDID version default */
    public static final int HDMI_EDID_DEFAULT = 0;
    /** EDID version 1.4 */
    public static final int HDMI_EDID_1_4 = 1;
    /** EDID version 2.0 */
    public static final int HDMI_EDID_2_0 = 2;

    /** Value of each element of HDMI EDID version list */
    /** EDID version unsupport */
    public static final int EDID_VERSION_UNSUPPORT = 0;
    /** EDID version support */
    public static final int EDID_VERSION_SUPPORT = 1;

    private final static int TV_DIALOG_EVENT_START = TvManager.TV_DIALOG_EVENT_START;
    private final static int TV_DIALOG_EVENT_END = TvManager.TV_DIALOG_EVENT_END;
    private final static int TV_SCART_EVENT_START = TvManager.TV_SCART_EVENT_START;
    private final static int TV_SCART_EVENT_END = TvManager.TV_SCART_EVENT_END;
    private final static int TV_SIGNAL_EVENT_START = TvManager.TV_SIGNAL_EVENT_START;
    private final static int TV_SIGNAL_EVENT_END = TvManager.TV_SIGNAL_EVENT_END;
    private final static int TV_UNITY_EVENT_START = TvManager.TV_UNITY_EVENT_START;
    private final static int TV_UNITY_EVENT_END = TvManager.TV_UNITY_EVENT_END;
    private final static int TV_SCREEN_SAVER_EVENT_START = TvManager.TV_SCREEN_SAVER_EVENT_START;
    private final static int TV_SCREEN_SAVER_EVENT_END = TvManager.TV_SCREEN_SAVER_EVENT_END;
    private final static int TV_4K_UHD_EVENT_START = TvManager.TV_4K_UHD_EVENT_START;
    private final static int TV_4K_UHD_EVENT_END = TvManager.TV_4K_UHD_EVENT_END;
    private final static int TV_PREVIEW_EVENT_START = TvManager.TV_PREVIEW_EVENT_START;
    private final static int TV_PREVIEW_EVENT_END = TvManager.TV_PREVIEW_EVENT_END;

    /**
     * When DTV notify to pop-up a dialog event received
     *
     * @see OnDialogEventListener
     */
    public final static int TV_DTV_READY_POPUP_DIALOG = TvManager.TV_DTV_READY_POPUP_DIALOG;

    /**
     * When ATSC system notify to pop-up a dialog event received
     *
     * @see OnDialogEventListener
     */
    public final static int TV_ATSC_POPUP_DIALOG = TvManager.TV_ATSC_POPUP_DIALOG;

    /**
     * When a osd scart mute notify event received
     *
     * @see OnScartEventListener
     */
    public final static int TV_SCART_MUTE_OSD_MODE = TvManager.TV_SCART_MUTE_OSD_MODE;

    /**
     * When a signal unlock notify event received
     *
     * @see OnSignalEventListener
     */
    public final static int TV_SIGNAL_UNLOCK = TvManager.TV_SIGNAL_UNLOCK;

    /**
     * When a signal lock notify event received
     *
     * @see OnSignalEventListener
     */
    public final static int TV_SIGNAL_LOCK = TvManager.TV_SIGNAL_LOCK;

    /**
     * When a unity notify event received
     *
     * @see OnUnityEventListener
     */
    public final static int TV_UNITY_EVENT = TvManager.TV_UNITY_EVENT;

    /**
     * When a screen save notify event received
     *
     * @see OnScreenSaverEventListener
     */
    public final static int TV_SCREEN_SAVER_MODE = TvManager.TV_SCREEN_SAVER_MODE;

    /**
     * When a disable PIP under HDMI event received
     *
     * @see On4kUhdEventListener
     */
    public final static int TV_4K_UHD_HDMI_DISABLE_PIP = TvManager.TV_4K_UHD_HDMI_DISABLE_PIP;

    /**
     * When a disable POP under HDMI event received
     *
     * @see On4kUhdEventListener
     */
    public final static int TV_4K_UHD_HDMI_DISABLE_POP = TvManager.TV_4K_UHD_HDMI_DISABLE_POP;

    /**
     * When a disable dual view under HDMI event received
     *
     * @see On4kUhdEventListener
     */
    public final static int TV_4K_UHD_HDMI_DISABLE_DUAL_VIEW = TvManager.TV_4K_UHD_HDMI_DISABLE_DUAL_VIEW;

    /**
     * When a disable traveling mode under HDMI event received
     *
     * @see On4kUhdEventListener
     */
    public final static int TV_4K_UHD_HDMI_DISABLE_TRAVELING_MODE = TvManager.TV_4K_UHD_HDMI_DISABLE_TRAVELING_MODE;

    /**
     * When refresh preview mode event received
     *
     * @see OnPreviewEventListener
     */
    public final static int TV_REFRESH_PREVIEW_MODE_WINDOW = TvManager.TV_REFRESH_PREVIEW_MODE_WINDOW;

    public interface OnDialogEventListener {
        /**
         * Called when pop-up dialog event received.
         *
         * @param what the type of pop-up dialog event occurred:
         *          <ul>
         *          <li>{@link #TV_DTV_READY_POPUP_DIALOG}
         *          <li>{@link #TV_ATSC_POPUP_DIALOG}
         *          </ul>
         * @param arg1
         *          <ul>
         *          <li>for {@link #TV_DTV_READY_POPUP_DIALOG}: status
         *          <li>for {@link #TV_ATSC_POPUP_DIALOG}: mode
         *          </ul>
         * @param arg2
         *          <ul>
         *          <li>for {@link #TV_ATSC_POPUP_DIALOG}: type
         *          <li>for other events: reserved
         *          </ul>
         * @param obj reserved
         * @return reserved
         */
        boolean onDialogEvent(int what, int arg1, int arg2, Object obj);
    }

    public interface OnScartEventListener {
        /**
         * Called when scart event received.
         *
         * @param what the type of scart event occurred:
         *          <ul>
         *          <li>{@link #TV_SCART_MUTE_OSD_MODE}
         *          </ul>
         * @param arg1 reserved
         * @param arg2 reserved
         * @param obj reserved
         * @return reserved
         */
        boolean onScartEvent(int what, int arg1, int arg2, Object obj);
    }

    public interface OnSignalEventListener {
        /**
         * Called when signal event received.
         *
         * @param what the type of signal event occurred:
         *          <ul>
         *          <li>{@link #TV_SIGNAL_UNLOCK}
         *          <li>{@link #TV_SIGNAL_LOCK}
         *          </ul>
         * @param arg1 reserved
         * @param arg2 reserved
         * @param obj reserved
         * @return reserved
         */
        boolean onSignalEvent(int what, int arg1, int arg2, Object obj);
    }

    public interface OnUnityEventListener {
        /**
         * Called when unity event received.
         *
         * @param what the type of unity event occurred:
         *          <ul>
         *          <li>{@link #TV_UNITY_EVENT}
         *          </ul>
         * @param arg1 option code
         * @param arg2 parameter
         * @param obj reserved
         * @return reserved
         */
        boolean onUnityEvent(int what, int arg1, int arg2, Object obj);
    }

    public interface OnScreenSaverEventListener {
        /**
         * Called when screen saver event received.
         *
         * @param what the type of screen saver event occurred:
         *          <ul>
         *          <li>{@link #TV_SCREEN_SAVER_MODE}
         *          </ul>
         * @param arg1 mode
         * @param arg2 reserved
         * @param obj reserved
         * @return reserved
         */
        boolean onScreenSaverEvent(int what, int arg1, int arg2, Object obj);
    }

    public interface On4kUhdEventListener {
        /**
         * Called when 4k UHD event received.
         *
         * @param what the type of 4k UHD event occurred:
         *          <ul>
         *          <li>{@link #TV_4K_UHD_HDMI_DISABLE_PIP}
         *          <li>{@link #TV_4K_UHD_HDMI_DISABLE_POP}
         *          <li>{@link #TV_4K_UHD_HDMI_DISABLE_DUAL_VIEW}
         *          <li>{@link #TV_4K_UHD_HDMI_DISABLE_DUAL_VIEW}
         *          </ul>
         * @param arg1 reserved
         * @param arg2 reserved
         * @param obj reserved
         * @return reserved
         */
        boolean on4kUhdEvent(int what, int arg1, int arg2, Object obj);
    }

    public interface OnPreviewEventListener {
        /**
         * Called when preview mode event received.
         *
         * @param what the type of preview event occurred:
         *          <ul>
         *          <li>{@link #TV_REFRESH_PREVIEW_MODE_WINDOW}
         *          </ul>
         * @param arg1 input source
         * @param arg2 previewInfo
         * @param obj reserved
         * @return reserved
         */
        boolean onPreviewEvent(int what, int arg1, int arg2, Object obj);
    }

    @Deprecated
    private ArrayList<OnTvEventListener> mTvEventListeners = new ArrayList<OnTvEventListener>();

    private ArrayList<OnDialogEventListener> mDialogEventListeners = new ArrayList<OnDialogEventListener>();

    private ArrayList<OnScartEventListener> mScartEventListeners = new ArrayList<OnScartEventListener>();

    private ArrayList<OnSignalEventListener> mSignalEventListeners = new ArrayList<OnSignalEventListener>();

    private ArrayList<OnUnityEventListener> mUnityEventListeners = new ArrayList<OnUnityEventListener>();

    private ArrayList<OnScreenSaverEventListener> mScreenSaverEventListeners = new ArrayList<OnScreenSaverEventListener>();

    private ArrayList<On4kUhdEventListener> m4kUhdEventListeners = new ArrayList<On4kUhdEventListener>();

    private ArrayList<OnPreviewEventListener> mPreviewEventListeners = new ArrayList<OnPreviewEventListener>();

    static TvCommonManager mInstance = null;

    private static ITvCommon mService = null;

    private final HashMap<Integer, String> mModuleMapTable = new HashMap<Integer, String>();

    private EventHandler mHandler = null;

    private TvEventHandler mTvHandler = null;

    private TvCommonManager() throws TvCommonException {
        BuildModuleMapTable();

        IBinder b = ServiceManager.getService(Context.TV_SERVICE);

        if (b == null) {
            Log.e(TAG, "TvService doesn't exist!!");
            throw new TvCommonException("TvService doesn't exist.");
        }

        try {
            mService = ITvService.Stub.asInterface(b).getTvCommon();
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        Looper looper;
        if ((looper = Looper.myLooper()) != null) {
            mHandler = new EventHandler(looper);
            mTvHandler = new TvEventHandler(looper);
        } else if ((looper = Looper.getMainLooper()) != null) {
            mHandler = new EventHandler(looper);
            mTvHandler = new TvEventHandler(looper);
        } else {
            mHandler = null;
            mTvHandler = null;
        }

        try {
            mService.addClient("DeskTvEventListener", this);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    public static TvCommonManager getInstance() {
        /* Double-checked locking */
        if (mInstance == null) {
            synchronized (TvCommonManager.class) {
                if (mInstance == null) {
                    try {
                        mInstance = new TvCommonManager();
                    } catch (TvCommonException e) {
                        e.printStackTrace();
                        return null;
                    }
                }
            }
        }

        return mInstance;
    }


    private class TvEventHandler extends Handler {
        TvEventHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {

            if (msg.what > TV_DIALOG_EVENT_START && msg.what < TV_DIALOG_EVENT_END) {
                synchronized (mDialogEventListeners) {
                    for (OnDialogEventListener l : mDialogEventListeners) {
                        l.onDialogEvent(msg.what, msg.arg1, msg.arg2, msg.obj);
                    }
                }
            } else if (msg.what > TV_SCART_EVENT_START && msg.what < TV_SCART_EVENT_END) {
                synchronized (mScartEventListeners) {
                    for (OnScartEventListener l : mScartEventListeners) {
                        l.onScartEvent(msg.what, msg.arg1, msg.arg2, msg.obj);
                    }
                }
            } else if (msg.what > TV_SIGNAL_EVENT_START && msg.what < TV_SIGNAL_EVENT_END) {
                synchronized (mSignalEventListeners) {
                    for (OnSignalEventListener l : mSignalEventListeners) {
                        l.onSignalEvent(msg.what, msg.arg1, msg.arg2, msg.obj);
                    }
                }
            } else if (msg.what > TV_UNITY_EVENT_START && msg.what < TV_UNITY_EVENT_END) {
                synchronized (mUnityEventListeners) {
                    for (OnUnityEventListener l : mUnityEventListeners) {
                        l.onUnityEvent(msg.what, msg.arg1, msg.arg2, msg.obj);
                    }
                }
            } else if (msg.what > TV_SCREEN_SAVER_EVENT_START && msg.what < TV_SCREEN_SAVER_EVENT_END) {
                synchronized (mScreenSaverEventListeners) {
                    for (OnScreenSaverEventListener l : mScreenSaverEventListeners) {
                        l.onScreenSaverEvent(msg.what, msg.arg1, msg.arg2, msg.obj);
                    }
                }
            } else if (msg.what > TV_4K_UHD_EVENT_START && msg.what < TV_4K_UHD_EVENT_END) {
                synchronized (m4kUhdEventListeners) {
                    for (On4kUhdEventListener l : m4kUhdEventListeners) {
                        l.on4kUhdEvent(msg.what, msg.arg1, msg.arg2, msg.obj);
                    }
                }
            } else if (msg.what > TV_PREVIEW_EVENT_START && msg.what < TV_PREVIEW_EVENT_END) {
                synchronized (mPreviewEventListeners) {
                    for (OnPreviewEventListener l : mPreviewEventListeners) {
                        l.onPreviewEvent(msg.what, msg.arg1, msg.arg2, msg.obj);
                    }
                }
            }


            // FIXME: old architecture, remove later
            switch (msg.what) {
                case TV_DTV_READY_POPUP_DIALOG: {
                    synchronized (mTvEventListeners) {
                        for (OnTvEventListener l : mTvEventListeners) {
                            l.onDtvReadyPopupDialog(msg.what, msg.arg1, msg.arg2);
                        }
                    }
                }
                    break;

                case TV_ATSC_POPUP_DIALOG: {
                    synchronized (mTvEventListeners) {
                        for (OnTvEventListener l : mTvEventListeners) {
                            l.onAtscPopupDialog(msg.what, msg.arg1, msg.arg2);
                        }
                    }
                }
                    break;

                case TV_SCART_MUTE_OSD_MODE: {
                    synchronized (mTvEventListeners) {
                        for (OnTvEventListener l : mTvEventListeners) {
                            l.onScartMuteOsdMode(msg.what);
                        }
                    }
                }
                    break;

                case TV_SIGNAL_UNLOCK: {
                    synchronized (mTvEventListeners) {
                        for (OnTvEventListener l : mTvEventListeners) {
                            l.onSignalUnlock(msg.what);
                        }
                    }
                }
                    break;

                case TV_SIGNAL_LOCK: {
                    synchronized (mTvEventListeners) {
                        for (OnTvEventListener l : mTvEventListeners) {
                            l.onSignalUnlock(msg.what);
                        }
                    }
                }
                    break;

                case TV_UNITY_EVENT: {
                    synchronized (mTvEventListeners) {
                        for (OnTvEventListener l : mTvEventListeners) {
                            l.onUnityEvent(msg.what, msg.arg1, msg.arg2);
                        }
                    }
                }
                    break;

                case TV_SCREEN_SAVER_MODE: {
                    synchronized (mTvEventListeners) {
                        for (OnTvEventListener l : mTvEventListeners) {
                            l.onScreenSaverMode(msg.what, msg.arg1, msg.arg2);
                        }
                    }
                }
                    break;

                default: {
                    Log.e(TAG, "Unknown message type " + msg.what);
                }
                    break;
            }
            return;
        }
    }

    private class EventHandler extends Handler {
        EventHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            if (mTvCommonEventHandlers != null) {
                synchronized (mTvCommonEventHandlers) {
                    for (OnTvCommonEventHandler h : mTvCommonEventHandlers) {
                        h.onTvCommonEvent(msg.what, msg.arg1, msg.arg2, msg.obj);
                    }
                }
            }
            return;
        }
    }

    private CommonClient mTvCommonClient;

    private ArrayList<OnTvCommonEventHandler> mTvCommonEventHandlers = new ArrayList<OnTvCommonEventHandler>();

    private class CommonClient extends IEventClient.Stub {
        @Override
        public boolean onEvent(Message msg) throws RemoteException {
            if (mHandler != null) {
                Message msgTmp = mHandler.obtainMessage();
                msgTmp.copyFrom(msg);
                mHandler.sendMessage(msgTmp);
            }
            return false;
        }
    }

    /**
     * Used to register an listener class to TV manager
     *
     * @param handler as an OnTvCommonEventHandler class
     */
    public boolean registerOnTvCommonEventHandler(OnTvCommonEventHandler handler) {
        if (mTvCommonClient == null) {
            mTvCommonClient = new CommonClient();
            try {
                mService.addClient("DeskTvCommonEventListener", mTvCommonClient);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        synchronized (mTvCommonEventHandlers) {
            mTvCommonEventHandlers.add(handler);
        }
        return true;
    }

    public synchronized boolean unregisterOnTvCommonEventHandler(OnTvCommonEventHandler handler) {
        synchronized (mTvCommonEventHandlers) {
            mTvCommonEventHandlers.remove(handler);
        }
        if (mTvCommonEventHandlers.size() == 0 && mTvCommonClient != null) {
            try {
                    mService.removeClient("DeskTvCommonEventListener", mTvCommonClient);
            } catch (RemoteException e) {
                e.printStackTrace();
            }
            mTvCommonClient = null;
        }
        return true;
    }

    /**
     * Change current input source for main window.
     *
     * @param source EnumInputSource
     * @deprecated Use {@link setInputSource(int source)}.
     *
     * Notice: GoogleTV has its own input source
     * control protocol through TunerPlayer,
     * please avoid using setInputSource function directly in TVAPI
     * unless confirmed and carefully reviewed by MStar.
     */
    @Deprecated
    @SuppressWarnings("deprecation")
    public void setInputSource(EnumInputSource source) {
        setInputSource(source.ordinal());
    }

    /**
     * Change current input source for main window.
     *
     * @param source int
     *
     * Notice: GoogleTV has its own input source
     * control protocol through TunerPlayer,
     * please avoid using setInputSource function directly in TVAPI
     * unless confirmed and carefully reviewed by MStar.
     */
    public void setInputSource(int source) {
        Log.d(TAG, "setInputSource, paras source = " + source);
        try {
            mService.setInputSource(source);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Specifies the DVBT tv system.
     */
    public static final int TV_SYSTEM_DVBT = 1;

    /**
     * Specifies the DVBC tv system.
     */
    public static final int TV_SYSTEM_DVBC = 2;

    /**
     * Specifies the DVBS tv system.
     */
    public static final int TV_SYSTEM_DVBS = 3;

    /**
     * Specifies the DVBT2 tv system.
     */
    public static final int TV_SYSTEM_DVBT2 = 4;

    /**
     * Specifies the DVBS2 tv system.
     */
    public static final int TV_SYSTEM_DVBS2 = 5;

    /**
     * Specifies the DTMB tv system.
     */
    public static final int TV_SYSTEM_DTMB = 6;

    /**
     * Specifies the ATSC tv system.
     */
    public static final int TV_SYSTEM_ATSC = 7;

    /**
     * Specifies the ISDB tv system.
     */
    public static final int TV_SYSTEM_ISDB = 8;

    /**
     * Gets current tv system.
     * <p> The supported tv systems are:
     * <ul>
     * <li> {@link #TV_SYSTEM_DVBT}
     * <li> {@link #TV_SYSTEM_DVBC}
     * <li> {@link #TV_SYSTEM_DVBS}
     * <li> {@link #TV_SYSTEM_DVBT2}
     * <li> {@link #TV_SYSTEM_DVBS2}
     * <li> {@link #TV_SYSTEM_DTMB}
     * <li> {@link #TV_SYSTEM_ATSC}
     * <li> {@link #TV_SYSTEM_ISDB}
     * </ul>
     *
     * @return int
     * @see #TV_SYSTEM_DVBT
     * @see #TV_SYSTEM_DVBC
     * @see #TV_SYSTEM_DVBS
     * @see #TV_SYSTEM_DVBT2
     * @see #TV_SYSTEM_DVBS2
     * @see #TV_SYSTEM_DTMB
     * @see #TV_SYSTEM_ATSC
     * @see #TV_SYSTEM_ISDB
     */
    public int getCurrentTvSystem() {
        try {
            return mService.getCurrentTvSystem();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Check if the current source value in database is equal
     * to the value in tvos
     *
     * @return boolean
     */
    public boolean isCurrentSourceEqualToDatabase() {
        try {
            return mService.isCurrentSourceEqualToDatabase();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Gets current input source.
     *
     * @return EnumInputSource
     * @deprecated Use {@link getCurrentTvInputSource()}.
     */
    @Deprecated
    @SuppressWarnings("deprecation")
    public EnumInputSource getCurrentInputSource() {
        EnumInputSource en = null;
        try {
            en = EnumInputSource.values()[mService.getCurrentInputSource()];
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return en;
    }

    /**
     * Gets current input source.
     *
     * @return int
     */
    public int getCurrentTvInputSource() {
        int source = INPUT_SOURCE_NONE;
        try {
            source = mService.getCurrentInputSource();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return source;
    }

    /**
     * Gets current sub input source.
     *
     * @return EnumInputSource
     * @deprecated Use {@link getCurrentTvSubInputSource()}.
     */
    @Deprecated
    @SuppressWarnings("deprecation")
    public EnumInputSource getCurrentSubInputSource() {
        EnumInputSource en = null;
        try {
            en = EnumInputSource.values()[mService.getCurrentSubInputSource()];
            Log.d(TAG, "getCurrentSubInputSource, return EnumInputSource " + en);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return en;
    }

    /**
     * Gets current main input source.
     *
     * @return int
     * @see #INPUT_SOURCE_VGA
     * @see #INPUT_SOURCE_ATV
     * @see #INPUT_SOURCE_CVBS
     * @see #INPUT_SOURCE_CVBS2
     * @see #INPUT_SOURCE_CVBS3
     * @see #INPUT_SOURCE_CVBS4
     * @see #INPUT_SOURCE_CVBS5
     * @see #INPUT_SOURCE_CVBS6
     * @see #INPUT_SOURCE_CVBS7
     * @see #INPUT_SOURCE_CVBS8
     * @see #INPUT_SOURCE_SVIDEO
     * @see #INPUT_SOURCE_SVIDEO2
     * @see #INPUT_SOURCE_SVIDEO3
     * @see #INPUT_SOURCE_SVIDEO4
     * @see #INPUT_SOURCE_YPBPR
     * @see #INPUT_SOURCE_YPBPR2
     * @see #INPUT_SOURCE_YPBPR3
     * @see #INPUT_SOURCE_SCART
     * @see #INPUT_SOURCE_SCART2
     * @see #INPUT_SOURCE_HDMI
     * @see #INPUT_SOURCE_HDMI2
     * @see #INPUT_SOURCE_HDMI3
     * @see #INPUT_SOURCE_HDMI4
     * @see #INPUT_SOURCE_DTV
     * @see #INPUT_SOURCE_DVI
     * @see #INPUT_SOURCE_DVI2
     * @see #INPUT_SOURCE_DVI3
     * @see #INPUT_SOURCE_DVI4
     * @see #INPUT_SOURCE_STORAGE
     * @see #INPUT_SOURCE_KTV
     * @see #INPUT_SOURCE_JPEG
     * @see #INPUT_SOURCE_DTV2
     * @see #INPUT_SOURCE_STORAGE2
     * @see #INPUT_SOURCE_DIV3
     * @see #INPUT_SOURCE_SCALER_OP
     * @see #INPUT_SOURCE_RUV
     * @see #INPUT_SOURCE_VGA2
     * @see #INPUT_SOURCE_VGA3
     *
     */
    public int getCurrentTvMainInputSource() {
        int source = INPUT_SOURCE_NONE;
        try {
            source = mService.getCurrentMainInputSource();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return source;
    }

    /**
     * Gets current sub input source.
     *
     * @return int
     * @see #INPUT_SOURCE_VGA
     * @see #INPUT_SOURCE_ATV
     * @see #INPUT_SOURCE_CVBS
     * @see #INPUT_SOURCE_CVBS2
     * @see #INPUT_SOURCE_CVBS3
     * @see #INPUT_SOURCE_CVBS4
     * @see #INPUT_SOURCE_CVBS5
     * @see #INPUT_SOURCE_CVBS6
     * @see #INPUT_SOURCE_CVBS7
     * @see #INPUT_SOURCE_CVBS8
     * @see #INPUT_SOURCE_SVIDEO
     * @see #INPUT_SOURCE_SVIDEO2
     * @see #INPUT_SOURCE_SVIDEO3
     * @see #INPUT_SOURCE_SVIDEO4
     * @see #INPUT_SOURCE_YPBPR
     * @see #INPUT_SOURCE_YPBPR2
     * @see #INPUT_SOURCE_YPBPR3
     * @see #INPUT_SOURCE_SCART
     * @see #INPUT_SOURCE_SCART2
     * @see #INPUT_SOURCE_HDMI
     * @see #INPUT_SOURCE_HDMI2
     * @see #INPUT_SOURCE_HDMI3
     * @see #INPUT_SOURCE_HDMI4
     * @see #INPUT_SOURCE_DTV
     * @see #INPUT_SOURCE_DVI
     * @see #INPUT_SOURCE_DVI2
     * @see #INPUT_SOURCE_DVI3
     * @see #INPUT_SOURCE_DVI4
     * @see #INPUT_SOURCE_STORAGE
     * @see #INPUT_SOURCE_KTV
     * @see #INPUT_SOURCE_JPEG
     * @see #INPUT_SOURCE_DTV2
     * @see #INPUT_SOURCE_STORAGE2
     * @see #INPUT_SOURCE_DIV3
     * @see #INPUT_SOURCE_SCALER_OP
     * @see #INPUT_SOURCE_RUV
     * @see #INPUT_SOURCE_VGA2
     * @see #INPUT_SOURCE_VGA3
     *
     */
    public int getCurrentTvSubInputSource() {
        int source = INPUT_SOURCE_NONE;
        try {
            source = mService.getCurrentSubInputSource();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return source;
    }

    /**
     * Gets current input source status, what setting by SetInputSourceStatus
     *
     * @return an array to present current each input source status
     */
    public boolean[] GetInputSourceStatus() {
        boolean bSrcStatus[] = null;
        try {
            bSrcStatus = mService.GetInputSourceStatus();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return bSrcStatus;
    }

    /**
     * Save power on source in database.
     *
     * @param eSource
     * @return
     */
    @SuppressWarnings("deprecation")
    public boolean setPowerOnSource(EnumInputSource eSource) {
        Log.d(TAG, "setPowerOnSource, paras eSource = " + eSource);
        try {
            return mService.setPowerOnSource(eSource.ordinal());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Get power on source from database.
     *
     * @return EnumInputSource
     */
    @SuppressWarnings("deprecation")
    public EnumInputSource getPowerOnSource() {
        EnumInputSource en = null;
        try {
            en = EnumInputSource.values()[mService.getPowerOnSource()];
            Log.d(TAG, "getPowerOnSource, return EnumInputSource " + en);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return en;
    }

    /**
     * Save power on av mute in database.
     *
     * @param mute enable
     * @return
     */
    public void setPowerOnAVMute(boolean enable) {
        try {
            mService.setPowerOnAVMute(enable);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get power on av mute from database.
     *
     * @return mute enable
     */
    public boolean getPowerOnAVMute() {
        boolean enable = false;
        try {
            enable = mService.getPowerOnAVMute();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return enable;
    }

    /**
     * Attend into pm sleep mode.
     *
     * @param bMode True : enable standby init; False : disable standby init.
     * @param bNoSignalPwDn True : for no signal power down; False : for not no
     *            signal power down
     */
    public void enterSleepMode(boolean bMode, boolean bNoSignalPwDn) {
        Log.d(TAG, "enterSleepMode paras bMode = " + bMode + ", bNoSignalPwDn" + bNoSignalPwDn);
        try {
            mService.enterSleepMode(bMode, bNoSignalPwDn);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * recoverSystem used to execute Android.os.RecoverySystem
     * it will prcess verifyPackage, rebootWipeUserData and installPackage by sequential
     *
     * @param url the update package to install. Must be on a partition mountable by recovery.
     */
    public void recoverySystem(String url) {
        try {
            mService.recoverySystem(url);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * updateSystem used to execute Android.os.RecoverySystem
     * it will prcess verifyPackage and installPackage by sequential
     *
     * @param url the update package to install. Must be on a partition mountable by recovery.
     */
    public void updateSystem(String url) {
        try {
            mService.updateSystem(url);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Let system goes to standby mode
     *
     * @param Pwd it present by standby mode,
     *            standby
     *            autosleep
     */
    public void standbySystem(String Pwd) {
        try {
            mService.standbySystem(Pwd);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * to open a SurfaceView
     *
     * @param x is start position x-dim
     * @param y is start position y-dim
     * @param width is Surface width
     * @param height is Surface height
     */
    public void openSurfaceView(int x, int y, int width, int height) throws RemoteException {
        try {
            mService.openSurfaceView(x, y, width, height);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * set to indicated window to display
     *
     * @param x is start position x-dim
     * @param y is start position y-dim
     * @param width is displayed width
     * @param height is displayed height
     */
    public void setSurfaceView(int x, int y, int width, int height) throws RemoteException {
        try {
            mService.setSurfaceView(x, y, width, height);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * to close current SurfaceView
     */
    public void closeSurfaceView() throws RemoteException {
        try {
            mService.closeSurfaceView();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Used to trigger process
     *
     * @param Pwd it present by reboot mode, Pwd should be 'reboot'
     */
    public void rebootSystem(String Pwd) {
        try {
            mService.rebootSystem(Pwd);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Used to fetch available source list
     *
     * @return a array to present EnablePort number list
     */
    public int[] getSourceList() {
        try {
            return mService.getSourceList();
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get TvInfo for the information about tv type, atv type, audio type,
     * supported dtv route, etc.
     * dtv route path only support FOUR paths currently.
     *
     * <p> The supported tv route path are:
     * <ul>
     * <li> {@link TvChannelManager#TV_ROUTE_DVBT}
     * <li> {@link TvChannelManager#TV_ROUTE_DVBC}
     * <li> {@link TvChannelManager#TV_ROUTE_DVBS}
     * <li> {@link TvChannelManager#TV_ROUTE_ATSC}
     * <li> {@link TvChannelManager#TV_ROUTE_ISDB}
     * <li> {@link TvChannelManager#TV_ROUTE_DVBT2}
     * <li> {@link TvChannelManager#TV_ROUTE_DVBS2}
     * <li> {@link TvChannelManager#TV_ROUTE_DTMB}
     * </ul>
     *
     * @return TvTypeInfo
     * @TvTypeInfo.routepath can reference TvChannelManager
     * @see TvChannelManager#TV_ROUTE_DVBT
     * @see TvChannelManager#TV_ROUTE_DVBC
     * @see TvChannelManager#TV_ROUTE_DVBS
     * @see TvChannelManager#TV_ROUTE_ATSC
     * @see TvChannelManager#TV_ROUTE_ISDB
     * @see TvChannelManager#TV_ROUTE_DVBT2
     * @see TvChannelManager#TV_ROUTE_DVBS2
     * @see TvChannelManager#TV_ROUTE_DTMB
     */
    public TvTypeInfo getTvInfo() {
        try {
            return mService.getTvInfo();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Set the available ATV MTS mode
     * <p> The supported ATV Audio mode are:
     * <ul>
     * <li> {@link #ATV_AUDIOMODE_INVALID}
     * <li> {@link #ATV_AUDIOMODE_MONO}
     * <li> {@link #ATV_AUDIOMODE_FORCED_MONO}
     * <li> {@link #ATV_AUDIOMODE_G_STEREO}
     * <li> {@link #ATV_AUDIOMODE_K_STEREO}
     * <li> {@link #ATV_AUDIOMODE_MONO_SAP}
     * <li> {@link #ATV_AUDIOMODE_DUAL_A}
     * <li> {@link #ATV_AUDIOMODE_DUAL_B}
     * <li> {@link #ATV_AUDIOMODE_DUAL_AB}
     * <li> {@link #ATV_AUDIOMODE_NICAM_MONO}
     * <li> {@link #ATV_AUDIOMODE_NICAM_STEREO}
     * <li> {@link #ATV_AUDIOMODE_NICAM_DUAL_A}
     * <li> {@link #ATV_AUDIOMODE_NICAM_DUAL_B}
     * <li> {@link #ATV_AUDIOMODE_NICAM_DUAL_AB}
     * <li> {@link #ATV_AUDIOMODE_HIDEV_MONO}
     * <li> {@link #ATV_AUDIOMODE_LEFT_LEFT}
     * <li> {@link #ATV_AUDIOMODE_RIGHT_RIGHT}
     * <li> {@link #ATV_AUDIOMODE_LEFT_RIGHT}
     * </ul>
     *
     * @param int
     * @see #ATV_AUDIOMODE_INVALID
     * @see #ATV_AUDIOMODE_MONO
     * @see #ATV_AUDIOMODE_FORCED_MONO
     * @see #ATV_AUDIOMODE_G_STEREO
     * @see #ATV_AUDIOMODE_K_STEREO
     * @see #ATV_AUDIOMODE_MONO_SAP
     * @see #ATV_AUDIOMODE_STEREO_SAP
     * @see #ATV_AUDIOMODE_DUAL_A
     * @see #ATV_AUDIOMODE_DUAL_B
     * @see #ATV_AUDIOMODE_DUAL_AB
     * @see #ATV_AUDIOMODE_NICAM_MONO
     * @see #ATV_AUDIOMODE_NICAM_STEREO
     * @see #ATV_AUDIOMODE_NICAM_DUAL_A
     * @see #ATV_AUDIOMODE_NICAM_DUAL_B
     * @see #ATV_AUDIOMODE_NICAM_DUAL_AB
     * @see #ATV_AUDIOMODE_HIDEV_MONO
     * @see #ATV_AUDIOMODE_LEFT_LEFT
     * @see #ATV_AUDIOMODE_RIGHT_RIGHT
     * @see #ATV_AUDIOMODE_LEFT_RIGHT
     *
     * @return int
     * @see #AUDIO_RETURN_NOT_OK
     * @see #AUDIO_RETURN_OK
     * @see #AUDIO_RETURN_UNSUPPORT
     */
    public int setATVMtsMode(int mode) {
        try {
            return mService.setAtvMtsMode(mode);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return AUDIO_RETURN_NOT_OK;
    }

    /**
     * Get the available ATV MTS mode
     * <p> The supported ATV Audio mode are:
     * <ul>
     * <li> {@link #ATV_AUDIOMODE_INVALID}
     * <li> {@link #ATV_AUDIOMODE_MONO}
     * <li> {@link #ATV_AUDIOMODE_FORCED_MONO}
     * <li> {@link #ATV_AUDIOMODE_G_STEREO}
     * <li> {@link #ATV_AUDIOMODE_K_STEREO}
     * <li> {@link #ATV_AUDIOMODE_MONO_SAP}
     * <li> {@link #ATV_AUDIOMODE_DUAL_A}
     * <li> {@link #ATV_AUDIOMODE_DUAL_B}
     * <li> {@link #ATV_AUDIOMODE_DUAL_AB}
     * <li> {@link #ATV_AUDIOMODE_NICAM_MONO}
     * <li> {@link #ATV_AUDIOMODE_NICAM_STEREO}
     * <li> {@link #ATV_AUDIOMODE_NICAM_DUAL_A}
     * <li> {@link #ATV_AUDIOMODE_NICAM_DUAL_B}
     * <li> {@link #ATV_AUDIOMODE_NICAM_DUAL_AB}
     * <li> {@link #ATV_AUDIOMODE_HIDEV_MONO}
     * <li> {@link #ATV_AUDIOMODE_LEFT_LEFT}
     * <li> {@link #ATV_AUDIOMODE_RIGHT_RIGHT}
     * <li> {@link #ATV_AUDIOMODE_LEFT_RIGHT}
     * </ul>
     *
     * @return int
     * @see #ATV_AUDIOMODE_INVALID
     * @see #ATV_AUDIOMODE_MONO
     * @see #ATV_AUDIOMODE_FORCED_MONO
     * @see #ATV_AUDIOMODE_G_STEREO
     * @see #ATV_AUDIOMODE_K_STEREO
     * @see #ATV_AUDIOMODE_MONO_SAP
     * @see #ATV_AUDIOMODE_STEREO_SAP
     * @see #ATV_AUDIOMODE_DUAL_A
     * @see #ATV_AUDIOMODE_DUAL_B
     * @see #ATV_AUDIOMODE_DUAL_AB
     * @see #ATV_AUDIOMODE_NICAM_MONO
     * @see #ATV_AUDIOMODE_NICAM_STEREO
     * @see #ATV_AUDIOMODE_NICAM_DUAL_A
     * @see #ATV_AUDIOMODE_NICAM_DUAL_B
     * @see #ATV_AUDIOMODE_NICAM_DUAL_AB
     * @see #ATV_AUDIOMODE_HIDEV_MONO
     * @see #ATV_AUDIOMODE_LEFT_LEFT
     * @see #ATV_AUDIOMODE_RIGHT_RIGHT
     * @see #ATV_AUDIOMODE_LEFT_RIGHT
     */
    public int getATVMtsMode() {
        try {
            return mService.getAtvMtsMode();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return ATV_AUDIOMODE_INVALID;
    }

    /**
     * Set to next ATV MTS Info
     *
     * @return int
     * @see #AUDIO_RETURN_NOT_OK
     * @see #AUDIO_RETURN_OK
     * @see #AUDIO_RETURN_UNSUPPORT
     */
    public int setToNextATVMtsMode() {
        try {
            return mService.setToNextAtvMtsMode();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return AUDIO_RETURN_NOT_OK;
    }

    /**
     * Get the available ATV Sound mode.
     * <p> The supported ATV Audio mode are:
     * <ul>
     * <li> {@link #ATV_AUDIOMODE_INVALID}
     * <li> {@link #ATV_AUDIOMODE_MONO}
     * <li> {@link #ATV_AUDIOMODE_FORCED_MONO}
     * <li> {@link #ATV_AUDIOMODE_G_STEREO}
     * <li> {@link #ATV_AUDIOMODE_K_STEREO}
     * <li> {@link #ATV_AUDIOMODE_MONO_SAP}
     * <li> {@link #ATV_AUDIOMODE_DUAL_A}
     * <li> {@link #ATV_AUDIOMODE_DUAL_B}
     * <li> {@link #ATV_AUDIOMODE_DUAL_AB}
     * <li> {@link #ATV_AUDIOMODE_NICAM_MONO}
     * <li> {@link #ATV_AUDIOMODE_NICAM_STEREO}
     * <li> {@link #ATV_AUDIOMODE_NICAM_DUAL_A}
     * <li> {@link #ATV_AUDIOMODE_NICAM_DUAL_B}
     * <li> {@link #ATV_AUDIOMODE_NICAM_DUAL_AB}
     * <li> {@link #ATV_AUDIOMODE_HIDEV_MONO}
     * <li> {@link #ATV_AUDIOMODE_LEFT_LEFT}
     * <li> {@link #ATV_AUDIOMODE_RIGHT_RIGHT}
     * <li> {@link #ATV_AUDIOMODE_LEFT_RIGHT}
     * </ul>
     *
     * @return int
     * @see #ATV_AUDIOMODE_INVALID
     * @see #ATV_AUDIOMODE_MONO
     * @see #ATV_AUDIOMODE_FORCED_MONO
     * @see #ATV_AUDIOMODE_G_STEREO
     * @see #ATV_AUDIOMODE_K_STEREO
     * @see #ATV_AUDIOMODE_MONO_SAP
     * @see #ATV_AUDIOMODE_STEREO_SAP
     * @see #ATV_AUDIOMODE_DUAL_A
     * @see #ATV_AUDIOMODE_DUAL_B
     * @see #ATV_AUDIOMODE_DUAL_AB
     * @see #ATV_AUDIOMODE_NICAM_MONO
     * @see #ATV_AUDIOMODE_NICAM_STEREO
     * @see #ATV_AUDIOMODE_NICAM_DUAL_A
     * @see #ATV_AUDIOMODE_NICAM_DUAL_B
     * @see #ATV_AUDIOMODE_NICAM_DUAL_AB
     * @see #ATV_AUDIOMODE_HIDEV_MONO
     * @see #ATV_AUDIOMODE_LEFT_LEFT
     * @see #ATV_AUDIOMODE_RIGHT_RIGHT
     * @see #ATV_AUDIOMODE_LEFT_RIGHT
     */
    public int getATVSoundMode() {
        try {
            return mService.getAtvSoundMode();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return ATV_AUDIOMODE_INVALID;
    }

    /**
     * Set the available ATV MTS mode
     *
     * @param mode ATV MTS available mode by enumlator EnumAtvAudioModeType
     * @deprecated Use {@link setATVMtsMode(int mode)}.
     */
    @Deprecated
    public EnumAudioReturn setAtvMtsMode(EnumAtvAudioModeType mode) {
        return EnumAudioReturn.values()[setATVMtsMode(mode.ordinal())];
    }

    /**
     * Get the available ATV MTS mode
     *
     * @return the mode ATV MTS available by enumlator EnumAtvAudioModeType
     * @deprecated Use {@link getATVMtsMode()}.
     */
    @Deprecated
    public EnumAtvAudioModeType getAtvMtsMode() {
        return EnumAtvAudioModeType.values()[getATVMtsMode()];
    }

    /**
     * Set to next ATV MTS Info,
     *
     * @return E_RETURN_OK if setting successful, otherwise E_RETURN_NOTOK
     * @deprecated Use {@link setToNextATVMtsMode()}.
     */
    @Deprecated
    public EnumAudioReturn setToNextAtvMtsMode() {
        return EnumAudioReturn.values()[setToNextATVMtsMode()];
    }

    /**
     * Get the available ATV MTS mode.
     *
     * @return current ATV MTS mode by enumator ATV_AUDIOMODE_TYPE
     * @deprecated Use {@link getATVSoundMode()}.
     */
    @Deprecated
    public EnumAtvAudioModeType getAtvSoundMode() {
        return EnumAtvAudioModeType.values()[getATVSoundMode()];
    }

    /**
     * Set OSD 3D format information
     *
     * @param mode used to prsent 3D format
     *         "0": 3D off
     *         "1": side by side
     *         "2": top and bottom
     *         "3": top and bottom la
     *         "4": line alternavtive
     *         "5": top line alternavtive
     *         "6": bottom line alternavtive
     *         "7": left only
     *         "8": right only
     *         "9": top only
     *         "10": bottom only
     *         "11": Duplicate Left to right and frame sequence output
     *         "12": Normal frame sequence output
     */
    public void OSD_Set3Dformat(int mode) {
        SystemProperties.set("mstar.desk-display-mode", String.valueOf(mode));
    }

    /**
     * Get the available input source signal status.
     *
     * @param cmd by string "GetInputSourceStatus"
     * @return a array what present available input source signal status
     */
    public int[] setTvosCommonCommand(String cmd) {
        try {
            return mService.setTvosCommonCommand(cmd);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * set video to mute/unmute
     *
     * @param bScreenMute to present mute or unmute
     * @param enColor indicated mute color by E_BLACK, E_WHITE, E_RED, E_BLUE and E_GREEN
     * @param screenUnMuteTime delay time, video mute on/off will delay creenUnMuteTime ms
     * @param eSrcType the input source
     * @return a array what present available input source signal status
     * @deprecated Use {@link setVideoMute(int bScreenMute, int color, int screenUnMuteTime, int srcType)}.
     */
    @Deprecated
    @SuppressWarnings("deprecation")
    public boolean setVideoMute(boolean bScreenMute, EnumScreenMuteType enColor,
            int screenUnMuteTime, EnumInputSource eSrcType) {
        return setVideoMute(bScreenMute, enColor.ordinal(), screenUnMuteTime, eSrcType.ordinal());
    }

    /**
     * set video to mute/unmute
     *
     * @param bScreenMute to present mute or unmute
     * @param color indicated mute color by E_BLACK, E_WHITE, E_RED, E_BLUE and E_GREEN
     * @param screenUnMuteTime delay time, video mute on/off will delay creenUnMuteTime ms
     * @param srcType the input source
     * @return a array what present available input source signal status
     */
    public boolean setVideoMute(boolean bScreenMute, int color, int screenUnMuteTime, int srcType) {
        Log.d(TAG, "setVideoMute");
        try {
            return mService.setVideoMute(bScreenMute, color, screenUnMuteTime, srcType);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    /** Need to be review iff mandatory
     * judge if Signal Stable
     *
     * @param inputSource
     * @throws TvCommonException
     */

    public boolean isSignalStable(int inputSource) {
        Log.d(TAG, "isSignalStable");
        try {
            return mService.isSignalStable(inputSource);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Used to register an listener class to TV manager
     *
     * @param listener as an OnTvEventListener class
     * @deprecated Use
     *              <ul>
     *              <li>{@link #registerOnDialogEventListener(OnDialogEventListener listener)}
     *              <li>{@link #registerOnScartEventListener(OnScartEventListener listener)}
     *              <li>{@link #registerOnSignalEventListener(OnSignalEventListener listener)}
     *              <li>{@link #registerOnUnityEventListener(OnUnityEventListener listener)}
     *              <li>{@link #registerOnScreenSaverEventListener(OnScreenSaverEventListener listener)}
     *              <li>{@link #registerOn4kUhdEventListener(On4kUhdEventListener listener)}
     *              <li>{@link #registerOnPreviewEventListener(OnPreviewEventListener listener)}
     *              </ul>
     */
    @Deprecated
    public boolean registerOnTvEventListener(OnTvEventListener listener) {
        Log.d(TAG, "registerOnTvEventListener");
        synchronized (mTvEventListeners) {
            mTvEventListeners.add(listener);
        }
        return true;
    }

    /**
     * Used to unregister an listener class to TV manager
     *
     * @param listener as an OnTvEventListener class
     * @deprecated Use
     *              <ul>
     *              <li>{@link #unregisterOnDialogEventListener(OnDialogEventListener listener)}
     *              <li>{@link #unregisterOnScartEventListener(OnScartEventListener listener)}
     *              <li>{@link #unregisterOnSignalEventListener(OnSignalEventListener listener)}
     *              <li>{@link #unregisterOnUnityEventListener(OnUnityEventListener listener)}
     *              <li>{@link #unregisterOnScreenSaverEventListener(OnScreenSaverEventListener listener)}
     *              <li>{@link #unregisterOn4kUhdEventListener(On4kUhdEventListener listener)}
     *              <li>{@link #unregisterOnPreviewEventListener(OnPreviewEventListener listener)}
     *              </ul>
     */
    @Deprecated
    public boolean unregisterOnTvEventListener(OnTvEventListener listener) {
        synchronized (mTvEventListeners) {
            mTvEventListeners.remove(listener);
            Log.d(TAG, "unregisterOnTvEventListener  size: " + mTvEventListeners.size());
        }
        return true;
    }

    /**
     * Register dialog event listener. Your listener will be triggered when the events
     * posted from native code.
     * Note: Remeber to unregister the listener before your application destroyed.
     *
     * @param listener OnDialogEventListener
     * @return reserved, TRUE - register success, FALSE - register fail.
     */
    public boolean registerOnDialogEventListener(OnDialogEventListener listener) {
        Log.d(TAG, "registerOnDialogEventListener");
        synchronized (mDialogEventListeners) {
            mDialogEventListeners.add(listener);
        }
        return true;
    }

    /**
     * Unregister dialog event listener from service.
     *
     * @param listener OnDialogEventListener
     * @return reserved, TRUE - unregister success, FALSE - unregister fail.
     */
    public boolean unregisterOnDialogEventListener(OnDialogEventListener listener) {
        synchronized (mDialogEventListeners) {
            mDialogEventListeners.remove(listener);
            Log.d(TAG, "unregisterOnDialogEventListener  size: " + mDialogEventListeners.size());
        }
        return true;
    }

    /**
     * Register scart event listener. Your listener will be triggered when the events
     * posted from native code.
     * Note: Remeber to unregister the listener before your application destroyed.
     *
     * @param listener OnScartEventListener
     * @return reserved, TRUE - register success, FALSE - register fail.
     */
    public boolean registerOnScartEventListener(OnScartEventListener listener) {
        Log.d(TAG, "registerOnScartEventListener");
        synchronized (mScartEventListeners) {
            mScartEventListeners.add(listener);
        }
        return true;
    }

    /**
     * Unregister scart event listener from service.
     *
     * @param listener OnScartEventListener
     * @return reserved, TRUE - unregister success, FALSE - unregister fail.
     */
    public boolean unregisterOnScartEventListener(OnScartEventListener listener) {
        synchronized (mScartEventListeners) {
            mScartEventListeners.remove(listener);
            Log.d(TAG, "unregisterOnScartEventListener  size: " + mScartEventListeners.size());
        }
        return true;
    }

    /**
     * Register signal event listener. Your listener will be triggered when the events
     * posted from native code.
     * Note: Remeber to unregister the listener before your application destroyed.
     *
     * @param listener OnSignalEventListener
     * @return reserved, TRUE - register success, FALSE - register fail.
     */
    public boolean registerOnSignalEventListener(OnSignalEventListener listener) {
        Log.d(TAG, "registerOnSignalEventListener");
        synchronized (mSignalEventListeners) {
            mSignalEventListeners.add(listener);
        }
        return true;
    }

    /**
     * Unregister signal event listener from service.
     *
     * @param listener OnSignalEventListener
     * @return reserved, TRUE - unregister success, FALSE - unregister fail.
     */
    public boolean unregisterOnSignalEventListener(OnSignalEventListener listener) {
        synchronized (mSignalEventListeners) {
            mSignalEventListeners.remove(listener);
            Log.d(TAG, "unregisterOnSignalEventListener  size: " + mSignalEventListeners.size());
        }
        return true;
    }

    /**
     * Register unity event listener. Your listener will be triggered when the events
     * posted from native code.
     * Note: Remeber to unregister the listener before your application destroyed.
     *
     * @param listener OnUnityEventListener
     * @return reserved, TRUE - register success, FALSE - register fail.
     */
    public boolean registerOnUnityEventListener(OnUnityEventListener listener) {
        Log.d(TAG, "registerOnUnityEventListener");
        synchronized (mUnityEventListeners) {
            mUnityEventListeners.add(listener);
        }
        return true;
    }

    /**
     * Unregister unity event listener from service.
     *
     * @param listener OnUnityEventListener
     * @return reserved, TRUE - unregister success, FALSE - unregister fail.
     */
    public boolean unregisterOnUnityEventListener(OnUnityEventListener listener) {
        synchronized (mUnityEventListeners) {
            mUnityEventListeners.remove(listener);
            Log.d(TAG, "unregisterOnUnityEventListener  size: " + mUnityEventListeners.size());
        }
        return true;
    }

    /**
     * Register screen saver event listener. Your listener will be triggered when the events
     * posted from native code.
     * Note: Remeber to unregister the listener before your application destroyed.
     *
     * @param listener OnScreenSaverEventListener
     * @return reserved, TRUE - register success, FALSE - register fail.
     */
    public boolean registerOnScreenSaverEventListener(OnScreenSaverEventListener listener) {
        Log.d(TAG, "registerOnScreenSaverEventListener");
        synchronized (mScreenSaverEventListeners) {
            mScreenSaverEventListeners.add(listener);
        }
        return true;
    }

    /**
     * Unregister screen saver event listener from service.
     *
     * @param listener OnScreenSaverEventListener
     * @return reserved, TRUE - unregister success, FALSE - unregister fail.
     */
    public boolean unregisterOnScreenSaverEventListener(OnScreenSaverEventListener listener) {
        synchronized (mScreenSaverEventListeners) {
            mScreenSaverEventListeners.remove(listener);
            Log.d(TAG, "unregisterOnScreenSaverEventListener  size: " + mScreenSaverEventListeners.size());
        }
        return true;
    }

    /**
     * Register 4k uhd event listener. Your listener will be triggered when the events
     * posted from native code.
     * Note: Remeber to unregister the listener before your application destroyed.
     *
     * @param listener On4kUhdEventListener
     * @return reserved, TRUE - register success, FALSE - register fail.
     */
    public boolean registerOn4kUhdEventListener(On4kUhdEventListener listener) {
        Log.d(TAG, "registerOn4kUhdEventListener");
        synchronized (m4kUhdEventListeners) {
            m4kUhdEventListeners.add(listener);
        }
        return true;
    }

    /**
     * Unregister 4k uhd event listener from service.
     *
     * @param listener On4kUhdEventListener
     * @return reserved, TRUE - unregister success, FALSE - unregister fail.
     */
    public boolean unregisterOn4kUhdEventListener(On4kUhdEventListener listener) {
        synchronized (m4kUhdEventListeners) {
            m4kUhdEventListeners.remove(listener);
            Log.d(TAG, "unregisterOn4kUhdEventListener  size: " + m4kUhdEventListeners.size());
        }
        return true;
    }

    /**
     * Register preview mode event listener. Your listener will be triggered when the events
     * posted from native code.
     * Note: Remeber to unregister the listener before your application destroyed.
     *
     * @param listener OnPreviewEventListener
     * @return reserved, TRUE - register success, FALSE - register fail.
     */
    public boolean registerOnPreviewEventListener(OnPreviewEventListener listener) {
        Log.d(TAG, "registerOnPreviewEventListener");
        synchronized (mPreviewEventListeners) {
            mPreviewEventListeners.add(listener);
        }
        return true;
    }

    /**
     * Unregister preview mode event listener from service.
     *
     * @param listener OnPreviewEventListener
     * @return reserved, TRUE - unregister success, FALSE - unregister fail.
     */
    public boolean unregisterOnPreviewEventListener(OnPreviewEventListener listener) {
        synchronized (mPreviewEventListeners) {
            mPreviewEventListeners.remove(listener);
            Log.d(TAG, "unregisterOnPreviewEventListener  size: " + mPreviewEventListeners.size());
        }
        return true;
    }

    /*
     * Disable Ir
     *
     * @throws TvCommonException
     */
    public boolean disableTvosIr() {
        try {
            mService.disableTvosIr();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
       * Get Hdmi Mode
       *
       * @return boolean values to show is hdmi or not
       */
    public boolean isHdmiSignalMode() {
        try {
            return mService.isHdmiSignalMode();
        } catch (RemoteException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return false;
    }

    /**
     * set current event status to DTV player.
     * <p> The supported DTV statuses are:
     * <ul>
     * <li> {@link #DTV_STATUS_UI_EXIST}
     * <li> {@link #DTV_STATUS_PVR_RUNNING}
     * <li> {@link #DTV_STATUS_CC_RUNNING}
     * <li> {@link #DTV_STATUS_CHANNEL_CHANGE}
     * <li> {@link #DTV_STATUS_SOURCE_CHANGE}
     * <li> {@link #DTV_STATUS_PROGRAM_LOCKED}
     * </ul>
     *
     * @param int
     * @see #DTV_STATUS_UI_EXIST
     * @see #DTV_STATUS_PVR_RUNNING
     * @see #DTV_STATUS_CC_RUNNING
     * @see #DTV_STATUS_CHANNEL_CHANGE
     * @see #DTV_STATUS_SOURCE_CHANGE
     * @see #DTV_STATUS_PROGRAM_LOCKED
     * @param boolean flag
     */
    public void setCurrentEventStatus(int type, boolean flag) {
        try {
            mService.setCurrentEventStatus(type, flag);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * get osd menu show duration.
     * <p> The supported Osd Duration are:
     * <ul>
     * <li> {@link #DURATION_INDEX_5_SEC}
     * <li> {@link #DURATION_INDEX_10_SEC}
     * <li> {@link #DURATION_INDEX_15_SEC}
     * <li> {@link #DURATION_INDEX_20_SEC}
     * <li> {@link #DURATION_INDEX_30_SEC}
     * <li> {@link #DURATION_INDEX_ALWAYS}
     * </ul>
     *
     * @return int
     * @see #DURATION_INDEX_5_SEC
     * @see #DURATION_INDEX_10_SEC
     * @see #DURATION_INDEX_15_SEC
     * @see #DURATION_INDEX_20_SEC
     * @see #DURATION_INDEX_30_SEC
     * @see #DURATION_INDEX_ALWAYS
     */
    public int getOsdDuration() {
        int duration = -1;
        try {
            duration = mService.getOsdDuration();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return duration;
    }

    /**
     * set osd menu show duration.
     * <p> The supported Osd Duration are:
     * <ul>
     * <li> {@link #DURATION_INDEX_5_SEC}
     * <li> {@link #DURATION_INDEX_10_SEC}
     * <li> {@link #DURATION_INDEX_15_SEC}
     * <li> {@link #DURATION_INDEX_20_SEC}
     * <li> {@link #DURATION_INDEX_30_SEC}
     * <li> {@link #DURATION_INDEX_ALWAYS}
     * </ul>
     *
     * @param int
     * @see #DURATION_INDEX_5_SEC
     * @see #DURATION_INDEX_10_SEC
     * @see #DURATION_INDEX_15_SEC
     * @see #DURATION_INDEX_20_SEC
     * @see #DURATION_INDEX_30_SEC
     * @see #DURATION_INDEX_ALWAYS
     */
    public void setOsdDuration(int durationIndex) {
        try {
            mService.setOsdDuration(durationIndex);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * get osd menu timeout in second.
     *
     * @return int seconds of menu time out
     */
    public int getOsdTimeoutInSecond() {
        int second = -1;
        try {
            second = mService.getOsdTimeoutInSecond();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return second;
    }

    /**
     * set osd menu language to database
     *
     * @param language {@see TvLanguage}
     * @return void
     */
    public void setOsdLanguage(int language) {
        try {
            mService.setOsdLanguage(language);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * get osd menu language saving in database.
     *
     * @return int {@see TvLanguage}
     */
    public int getOsdLanguage() {
        int lang = TvLanguage.ENGLISH;
        try {
            lang = mService.getOsdLanguage();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return lang;
    }

    /**
     * get subtitle current enabled/disabled status
     *
     * @return boolean, true: subtitle enabled, false: subtitle disabled
     */
    public boolean isSubtitleEnable() {
        boolean bEnable = false;
        try {
            bEnable = mService.isSubtitleEnable();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return bEnable;
    }

    /**
     * enabled/disabled subtitle
     * @param boolean bEnable, true: Enable subtitle, false: Disable subtitle
     *
     */
    public void setSubtitleEnable(boolean bEnable) {
        try {
            mService.setSubtitleEnable(bEnable);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * set default subtitle primary language saving in database.
     *
     * @param language {@see TvLanguage}
     * @return void
     */
    public void setSubtitlePrimaryLanguage(int language) {
        try {
            mService.setSubtitlePrimaryLanguage(language);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    /**
     * set default subtitle secondary language saving in database.
     *
     * @param language {@see TvLanguage}
     * @return void
     */
    public void setSubtitleSecondaryLanguage(int language) {
        try {
            mService.setSubtitleSecondaryLanguage(language);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    /**
     * get default subtitle primary language saving in database.
     *
     * @return int {@see TvLanguage}
     */
    public int getSubtitlePrimaryLanguage() {
        int lang = TvLanguage.ENGLISH;
        try {
            lang = mService.getSubtitlePrimaryLanguage();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return lang;

    }

    /**
     * get default subtitle secondary language saving in database.
     *
     * @return int {@see TvLanguage}
     */
    public int getSubtitleSecondaryLanguage() {
        int lang = TvLanguage.ENGLISH;
        try {
            lang = mService.getSubtitleSecondaryLanguage();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return lang;
    }

    /**
     * set blue screen mode status when tv is no-signal.
     *
     * @param boolean
     * true means tv will show blue screen when no-signal
     * false means tv will show noise when no-signal
     */
    public void setBlueScreenMode(boolean bBlueScreen) {
        try {
            mService.setBlueScreenMode(bBlueScreen);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * get blue screen mode status when tv is no-signal.
     *
     * @return boolean
     * true means tv will show blue screen when no-signal
     * false means tv will show noise when no-signal
     */
    public boolean getBlueScreenMode() {
        boolean bBlueScreen = false;
        try {
            bBlueScreen = mService.getBlueScreenMode();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return bBlueScreen;
    }

    /**
     * set source detect enable state
     *
     * @param int
     */
    public void setSourceIdentState(int currentState) {
        try {
            mService.setSourceIdentState(currentState);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * get source detect enable state
     *
     * @return int
     */
    public int getSourceIdentState() {
        try {
            return mService.getSourceIdentState();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * set source preview state
     *
     * @param int
     */
    public void setSourcePreviewState(int currentState) {
        try {
            mService.setSourcePreviewState(currentState);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * get source preview state
     *
     * @return int
     */
    public int getSourcePreviewState() {
        try {
            return mService.getSourcePreviewState();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * set auto source switch state
     *
     * @param int
     */
    public void setSourceSwitchState(int currentState) {
        try {
            mService.setSourceSwitchState(currentState);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * get auto source switch state
     *
     * @return int
     */
    public int getSourceSwitchState() {
        try {
            return mService.getSourceSwitchState();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Check if module is supported
     * If module catagory is compiler flag, check both SN config and AN config
     * (return true if SN and AN config both support);
     * otherwise, check AN config
     * <p> The supported modules are:
     * <ul>
     * <li> {@link #MODULE_PIP}
     * <li> {@link #MODULE_TRAVELING}
     * <li> {@link #MODULE_OFFLINE_DETECT}
     * <li> {@link #MODULE_PREVIEW_MODE}
     * <li> {@link #MODULE_FREEVIEW_AU}
     * <li> {@link #MODULE_CC}
     * <li> {@link #MODULE_BRAZIL_CC}
     * <li> {@link #MODULE_KOREAN_CC}
     * <li> {@link #MODULE_HDMITX}
     * <li> {@link #MODULE_HBBTV}
     * <li> {@link #MODULE_INPUT_SOURCE_LOCK}
     * <li> {@link #MODULE_EPG}
     * <li> {@link #MODULE_AD_SWITCH}
     * <li> {@link #MODULE_ATV_NTSC_ENABLE}
     * <li> {@link #MODULE_ATV_PAL_ENABLE}
     * <li> {@link #MODULE_ATV_CHINA_ENABLE}
     * <li> {@link #MODULE_ATV_PAL_M_ENABLE}
     * <li> {@link #MODULE_TV_CONFIG_ATV_MANUAL_TUNING}
     * <li> {@link #MODULE_TV_CONFIG_AUTO_HOH}
     * <li> {@link #MODULE_TV_CONFIG_AUDIO_DESCRIPTION}
     * <li> {@link #MODULE_TV_CONFIG_THREED_DEPTH}
     * <li> {@link #MODULE_TV_CONFIG_SELF_DETECT}
     * <li> {@link #MODULE_TV_CONFIG_THREED_CONVERSION_TWODTOTHREED}
     * <li> {@link #MODULE_TV_CONFIG_THREED_CONVERSION_AUTO}
     * <li> {@link #MODULE_TV_CONFIG_THREED_CONVERSION_PIXEL_ALTERNATIVE}
     * <li> {@link #MODULE_TV_CONFIG_THREED_CONVERSION_FRAME_ALTERNATIVE}
     * <li> {@link #MODULE_TV_CONFIG_THREED_CONVERSION_CHECK_BOARD}
     * <li> {@link #MODULE_TV_CONFIG_THREED_TWOD_AUTO}
     * <li> {@link #MODULE_TV_CONFIG_THREED_TWOD_PIXEL_ALTERNATIVE}
     * <li> {@link #MODULE_TV_CONFIG_THREED_TWOD_FRAME_ALTERNATIVE}
     * <li> {@link #MODULE_TV_CONFIG_THREED_TWOD_CHECK_BOARD}
     * </ul>
     *
     * @param module queried module
     * @see #MODULE_PIP
     * @see #MODULE_TRAVELING
     * @see #MODULE_OFFLINE_DETECT
     * @see #MODULE_PREVIEW_MODE
     * @see #MODULE_FREEVIEW_AU
     * @see #MODULE_CC
     * @see #MODULE_BRAZIL_CC
     * @see #MODULE_KOREAN_CC
     * @see #MODULE_HDMITX
     * @see #MODULE_HBBTV
     * @see #MODULE_INPUT_SOURCE_LOCK
     * @see #MODULE_EPG
     * @see #MODULE_AD_SWITCH
     * @see #MODULE_ATV_NTSC_ENABLE
     * @see #MODULE_ATV_PAL_ENABLE
     * @see #MODULE_ATV_CHINA_ENABLE
     * @see #MODULE_ATV_PAL_M_ENABLE
     * @see #MODULE_TV_CONFIG_ATV_MANUAL_TUNING
     * @see #MODULE_TV_CONFIG_AUTO_HOH
     * @see #MODULE_TV_CONFIG_AUDIO_DESCRIPTION
     * @see #MODULE_TV_CONFIG_THREED_DEPTH
     * @see #MODULE_TV_CONFIG_SELF_DETECT
     * @see #MODULE_TV_CONFIG_THREED_CONVERSION_TWODTOTHREED
     * @see #MODULE_TV_CONFIG_THREED_CONVERSION_AUTO
     * @see #MODULE_TV_CONFIG_THREED_CONVERSION_PIXEL_ALTERNATIVE
     * @see #MODULE_TV_CONFIG_THREED_CONVERSION_FRAME_ALTERNATIVE
     * @see #MODULE_TV_CONFIG_THREED_CONVERSION_CHECK_BOARD
     * @see #MODULE_TV_CONFIG_THREED_TWOD_AUTO
     * @see #MODULE_TV_CONFIG_THREED_TWOD_PIXEL_ALTERNATIVE
     * @see #MODULE_TV_CONFIG_THREED_TWOD_FRAME_ALTERNATIVE
     * @see #MODULE_TV_CONFIG_THREED_TWOD_CHECK_BOARD
     * @return boolean true: supported, false: unsupported
     */
    public boolean isSupportModule(int module) {
        String name = "";
        boolean ret = false;
        try {
            if (mModuleMapTable.containsKey(module)) {
                name = mModuleMapTable.get(module);
                if (module < MODULE_NOT_COMPILE_FLAG_START) {
                    String sValue = mService.getCompilerFlag(name);
                    int value = 0;
                    if (sValue.length() > 0) {
                        value = Integer.parseInt(sValue);
                    }
                    boolean configSetting = mService.getAndroidConfigSetting(name) <= 0 ? false : true;
                    if (value != 0 && configSetting) {
                        ret = true;
                    } else {
                        ret = false;
                    }
                    Log.d(TAG, "isSupportModule:" + module + " = " + ret + ", value = " + value + ", AN config = " + configSetting);
                    return ret;
                } else {
                    ret = mService.getAndroidConfigSetting(name) <= 0 ? false : true;
                    Log.d(TAG, "isSupportModule:" + module + " = " + ret);
                    return ret;
                }
            }
        } catch (RemoteException e) {
            e.printStackTrace();
        } catch (NumberFormatException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * set DPMS wake up enable
     *
     * @param enable true: enable, false: disable
     */
    public void setDpmsWakeUpEnable(boolean enable) {
        try {
            mService.setDpmsWakeUpEnable(enable);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * procsee TV async command
     *
     * This API is adding for handling callback event EV_SYSTEM_ASYNC_CMD.
     * After recevie EV_SYSTEM_ASYNC_CMD, upper can invoke this function to wait
     * a asyn command finish.
     *
     * @param enable true: success, false: fail
     */
    public boolean processTvAsyncCommand() {
        boolean ret = false;
        try {
            ret = mService.processTvAsyncCommand();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return ret;
    }

    private void BuildModuleMapTable() {
        /* SN compiler flag catagory */
        mModuleMapTable.put(MODULE_PIP, "PIP_ENABLE");
        mModuleMapTable.put(MODULE_TRAVELING, "TRAVELING_ENABLE");
        mModuleMapTable.put(MODULE_OFFLINE_DETECT, "OFL_DET");
        mModuleMapTable.put(MODULE_PREVIEW_MODE, "PREVIEW_MODE_ENABLE");
        mModuleMapTable.put(MODULE_FREEVIEW_AU, "FREEVIEW_AU_ENABLE");
        mModuleMapTable.put(MODULE_CC, "CC_ENABLE");
        mModuleMapTable.put(MODULE_BRAZIL_CC, "BRAZIL_CC_ENABLE");
        mModuleMapTable.put(MODULE_KOREAN_CC, "KOREAN_CC_ENABLE");
        mModuleMapTable.put(MODULE_ATSC_CC_ENABLE, "ATSC_CC_ENABLE");
        mModuleMapTable.put(MODULE_ISDB_CC_ENABLE, "ISDB_CC_ENABLE");
        mModuleMapTable.put(MODULE_NTSC_CC_ENABLE, "NTSC_CC_ENABLE");
        mModuleMapTable.put(MODULE_ATV_NTSC_ENABLE, "ATV_NTSC_ENABLE");
        mModuleMapTable.put(MODULE_ATV_PAL_ENABLE, "ATV_PAL_ENABLE");
        mModuleMapTable.put(MODULE_ATV_CHINA_ENABLE, "ATV_CHINA_ENABLE");
        mModuleMapTable.put(MODULE_ATV_PAL_M_ENABLE, "ATV_PAL_M_ENABLE");
        mModuleMapTable.put(MODULE_HDMITX, "HDMITX_ENABLE");
        mModuleMapTable.put(MODULE_HBBTV, "HBBTV_ENABLE");
        mModuleMapTable.put(MODULE_INPUT_SOURCE_LOCK, "INPUT_SOURCE_LOCK_ENABLE");
        mModuleMapTable.put(MODULE_EPG, "EPG_ENABLE");
        mModuleMapTable.put(MODULE_AD_SWITCH, "AD_SWITCH_ENABLE");

        /* AN capability catagory */
        mModuleMapTable.put(MODULE_TV_CONFIG_ATV_MANUAL_TUNING, "ATV_MANUAL_TUNING_ENABLE");
        mModuleMapTable.put(MODULE_TV_CONFIG_AUTO_HOH, "AUTO_HOH_ENABLE");
        mModuleMapTable.put(MODULE_TV_CONFIG_AUDIO_DESCRIPTION, "AUDIO_DESCRIPTION_ENABLE");
        mModuleMapTable.put(MODULE_TV_CONFIG_THREED_DEPTH, "THREED_DEPTH_ENABLE");
        mModuleMapTable.put(MODULE_TV_CONFIG_SELF_DETECT, "SELF_DETECT_ENABLE");
        mModuleMapTable.put(MODULE_TV_CONFIG_THREED_CONVERSION_TWODTOTHREED, "THREED_CONVERSION_TWODTOTHREED");
        mModuleMapTable.put(MODULE_TV_CONFIG_THREED_CONVERSION_AUTO, "THREED_CONVERSION_AUTO");
        mModuleMapTable.put(MODULE_TV_CONFIG_THREED_CONVERSION_PIXEL_ALTERNATIVE,
                "THREED_CONVERSION_PIXEL_ALTERNATIVE");
        mModuleMapTable.put(MODULE_TV_CONFIG_THREED_CONVERSION_FRAME_ALTERNATIVE,
                "THREED_CONVERSION_FRAME_ALTERNATIVE");
        mModuleMapTable.put(MODULE_TV_CONFIG_THREED_CONVERSION_CHECK_BOARD, "THREED_CONVERSION_CHECK_BOARD");
        mModuleMapTable.put(MODULE_TV_CONFIG_THREED_TWOD_AUTO, "THREED_TWOD_AUTO");
        mModuleMapTable.put(MODULE_TV_CONFIG_THREED_TWOD_PIXEL_ALTERNATIVE, "THREED_TWOD_PIXEL_ALTERNATIVE");
        mModuleMapTable.put(MODULE_TV_CONFIG_THREED_TWOD_FRAME_ALTERNATIVE, "THREED_TWOD_FRAME_ALTERNATIVE");
        mModuleMapTable.put(MODULE_TV_CONFIG_THREED_TWOD_CHECK_BOARD, "THREED_TWOD_CHECK_BOARD");
    }

    /**
     * used to fetch available HDMI EDID version list
     *
     * @return a array to present support HDMI EDID version list.values of each element are:
     * @see TvCommonManager#EDID_VERSION_UNSUPPORT
     * @see TvCommonManager#EDID_VERSION_SUPPORT
     */
    public int[] getHdmiEdidVersionList() {
        try {
            return mService.getHdmiEdidVersionList();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * set HDMI EDID version to database
     * <p> the supported HDMI EDID version are:
     * <ul>
     * <li> {@link #HDMI_EDID_DEFAULT}
     * <li> {@link #HDMI_EDID_1_4}
     * <li> {@link #HDMI_EDID_2_0}
     * </ul>
     *
     * @param int
     * @see TvCommonManager#HDMI_EDID_DEFAULT
     * @see TvCommonManager#HDMI_EDID_1_4
     * @see TvCommonManager#HDMI_EDID_2_0
     * @return boolean true: success, false: fail
     */
    public boolean setHdmiEdidVersion(int iHdmiEdidVersion) {
        boolean ret = false;
        try {
            ret = mService.setHdmiEdidVersion(iHdmiEdidVersion);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return ret;
    }

    /**
     * get HDMI EDID version saving in database.
     * <p> the supported HDMI EDID version are:
     * <ul>
     * <li> {@link #HDMI_EDID_DEFAULT}
     * <li> {@link #HDMI_EDID_1_4}
     * <li> {@link #HDMI_EDID_2_0}
     * </ul>
     *
     * @return int
     * @see TvCommonManager#HDMI_EDID_DEFAULT
     * @see TvCommonManager#HDMI_EDID_1_4
     * @see TvCommonManager#HDMI_EDID_2_0
     */
    public int getHdmiEdidVersion() {
        int iHdmiEdidVersion = HDMI_EDID_DEFAULT;
        try {
            iHdmiEdidVersion = mService.getHdmiEdidVersion();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return iHdmiEdidVersion;
    }

    @Override
    public boolean onEvent(Message msg) throws RemoteException {
        if (mTvHandler != null) {
            Message msgTmp = mTvHandler.obtainMessage();
            msgTmp.copyFrom(msg);
            mTvHandler.sendMessage(msgTmp);
        }
        return true;
    }

    /**
     * get Input Source Lock
     *
     * @param nInputSource: input source id
     * @return true: input source locked, false :otherwise
     */
    public boolean getInputSourceLock(int nInputSource) {
        boolean ret = false;
        try {
            ret = mService.getInputSourceLock(nInputSource);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return ret;
    }

    /**
     * set Input Source Lock
     *
     * @param bLock: to block the input soure or not
     * @param nInputSource: input source id
     * @return true:operation successed, false: operation failed
     */
    public boolean setInputSourceLock(boolean bLock, int nInputSource) {
        boolean ret = false;
        try {
            ret = mService.setInputSourceLock(bLock, nInputSource);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return ret;
    }

    /**
     * reset input source lock settings
     *
     * @return true:operation successed, false: operation failed
     */
    public boolean resetInputSourceLock() {
        boolean ret = false;
        try {
            ret = mService.resetInputSourceLock();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return ret;
    }

    /**
     * set HDMI EDID version to database
     * <p> the supported HDMI EDID version are:
     * <ul>
     * <li> {@link #HDMI_EDID_DEFAULT}
     * <li> {@link #HDMI_EDID_1_4}
     * <li> {@link #HDMI_EDID_2_0}
     * </ul>
     *
     * @param int
     * @see TvCommonManager#HDMI_EDID_DEFAULT
     * @see TvCommonManager#HDMI_EDID_1_4
     * @see TvCommonManager#HDMI_EDID_2_0
     * @return boolean true: success, false: fail
     */
    public boolean setHdmiEdidVersionBySource(int inputSource, int hdmiEdidVersion) {
        boolean ret = false;
        try {
            ret = mService.setHdmiEdidVersionBySource(inputSource, hdmiEdidVersion);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return ret;
    }

    /**
     * get HDMI EDID version saving in database.
     * <p> the supported HDMI EDID version are:
     * <ul>
     * <li> {@link #HDMI_EDID_DEFAULT}
     * <li> {@link #HDMI_EDID_1_4}
     * <li> {@link #HDMI_EDID_2_0}
     * </ul>
     *
     * @return int
     * @see TvCommonManager#HDMI_EDID_DEFAULT
     * @see TvCommonManager#HDMI_EDID_1_4
     * @see TvCommonManager#HDMI_EDID_2_0
     */
    public int getHdmiEdidVersionBySource(int inputSource) {
        int hdmiEdidVersion = HDMI_EDID_DEFAULT;
        try {
            hdmiEdidVersion = mService.getHdmiEdidVersionBySource(inputSource);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return hdmiEdidVersion;
    }
	
	// EosTek Patch Begin
	 /**
     * Save atv program
     * 
     * @param current program num
     * @return boolean
     */
    public boolean saveAtvProgram(int currentProgramNo){
         try {
            return mService.saveAtvProgram(currentProgramNo);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }
	// EosTek Patch End
}
