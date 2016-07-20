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

import java.util.Calendar;
import java.util.TimeZone;

import android.content.Context;
import android.content.Intent;
import android.hardware.input.InputManager;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.os.SystemClock;
import android.text.format.Time;
import android.view.KeyEvent;
import android.util.Log;
import android.view.WindowManager;

import com.mstar.android.tv.IEventClient;
import com.mstar.android.tv.ITvService;
import com.mstar.android.tv.ITvTimer;
import com.mstar.android.tv.TvTimerManager;
import com.mstar.android.tv.TvChannelManager;
import com.mstar.android.tv.TvCommonManager;
import com.mstar.android.tv.TvS3DManager;
import com.mstar.android.tvapi.common.TvManager;
import com.mstar.android.tvapi.common.exception.TvCommonException;
import com.mstar.android.tvapi.common.vo.EnumEpgTimerActType;
import com.mstar.android.tvapi.common.vo.EnumOffTimerMode;
import com.mstar.android.tvapi.common.vo.EnumSleepTimeState;
import com.mstar.android.tvapi.common.vo.EnumTimerBootType;
import com.mstar.android.tvapi.common.vo.EnumTimerPeriod;
import com.mstar.android.tvapi.common.vo.EpgEventTimerInfo;
import com.mstar.android.tvapi.common.vo.OnTimeTvDescriptor;
import com.mstar.android.tvapi.common.vo.StandardTime;
import com.mstar.android.tvapi.common.vo.TimerPowerOffModeStatus;
import com.mstar.android.tvapi.common.vo.TimerPowerOn;
import com.mstar.android.tvapi.common.vo.TimeZoneChangeInfo;
import com.mstar.android.tvapi.common.vo.EnumTimeOnTimerSource;
import com.mstar.android.tvapi.common.vo.TvOsType.EnumTimeZone;
import com.mstar.tv.ui.CountDownTimerDialog;
import com.mstar.tv.service.DatabaseDesk;
import com.mstar.tv.service.IDatabaseDesk.TimeSetting;

public class TvTimerService extends ITvTimer.Stub {

    private static final String TAG = "TvTimerService";

    private Context mContext = null;

    private Time mCurrentTime = null;

    private Time mOffTime = null;

    private Time mOnTime;

    private OnTimeTvDescriptor mPowerOnConfig;

    private EnumSleepTimeState eSleepMode = EnumSleepTimeState.E_OFF;

    boolean bOffTimerFlag;

    private TimerPowerOn timerPowerOn;

    private TimerPowerOffModeStatus timerPowerOffModeStatus;

    private EnumSleepTimeState enSleepTimeState;

    private Intent targetIntent = null;

    private final int MINUTES_PER_HOUR = 60;
    private final int MILLISECONDS_PER_MINUTE = 60000;
    private final int MILLISECONDS_PER_HOUR = MINUTES_PER_HOUR * MILLISECONDS_PER_MINUTE;

    private final int OFFSET_MINUTE_MINUS_45   = -45;
    private final int OFFSET_MINUTE_MINUS_30   = -30;
    private final int OFFSET_MINUTE_MINUS_15   = -15;
    private final int OFFSET_MINUTE_00         =   0;
    private final int OFFSET_MINUTE_PLUS_15    =  15;
    private final int OFFSET_MINUTE_PLUS_30    =  30;
    private final int OFFSET_MINUTE_PLUS_45    =  45;

    private final int OFFSET_HOUR_MINUS_11     = -11;
    private final int OFFSET_HOUR_MINUS_10     = -10;
    private final int OFFSET_HOUR_MINUS_09     =  -9;
    private final int OFFSET_HOUR_MINUS_08     =  -8;
    private final int OFFSET_HOUR_MINUS_07     =  -7;
    private final int OFFSET_HOUR_MINUS_06     =  -6;
    private final int OFFSET_HOUR_MINUS_05     =  -5;
    private final int OFFSET_HOUR_MINUS_04     =  -4;
    private final int OFFSET_HOUR_MINUS_03     =  -3;
    private final int OFFSET_HOUR_MINUS_02     =  -2;
    private final int OFFSET_HOUR_MINUS_01     =  -1;
    private final int OFFSET_HOUR_00           =   0;
    private final int OFFSET_HOUR_PLUS_01      =   1;
    private final int OFFSET_HOUR_PLUS_02      =   2;
    private final int OFFSET_HOUR_PLUS_03      =   3;
    private final int OFFSET_HOUR_PLUS_04      =   4;
    private final int OFFSET_HOUR_PLUS_05      =   5;
    private final int OFFSET_HOUR_PLUS_06      =   6;
    private final int OFFSET_HOUR_PLUS_07      =   7;
    private final int OFFSET_HOUR_PLUS_08      =   8;
    private final int OFFSET_HOUR_PLUS_09      =   9;
    private final int OFFSET_HOUR_PLUS_10      =  10;
    private final int OFFSET_HOUR_PLUS_11      =  11;
    private final int OFFSET_HOUR_PLUS_12      =  12;
    private final int OFFSET_HOUR_PLUS_13      =  13;

    public TvTimerService(Context context) {
        mContext = context;
        initParams();

        DeskTimerEventListener.getInstance().addClient(new TimerEventListener());

        TvManager.getInstance().getTimerManager()
                .setOnEventListener(DeskTimerEventListener.getInstance());
    }

    private void initParams() {
        try {
            timerPowerOn = TvManager.getInstance().getTimerManager().getOnTime();
            timerPowerOffModeStatus = TvManager.getInstance().getTimerManager().getOffModeStatus();
            enSleepTimeState = TvManager.getInstance().getTimerManager().getSleeperState();
        } catch (TvCommonException e) {
            e.printStackTrace();
        }

        /* init curTime */
        mCurrentTime = new Time();

        /* init offTime */
        mOffTime = new Time();
        mOffTime.set(timerPowerOffModeStatus);
        mOffTime.year += 1900;
        mOffTime.month++;

        /* init Ontime */
        mOnTime = new Time();
        mOffTime.set(timerPowerOn);
        mOnTime.year += 1900;
        mOnTime.month++;

        /* init powerOnConfig */
        mPowerOnConfig = new OnTimeTvDescriptor(EnumTimeOnTimerSource.values()[timerPowerOn
                .getTvSource().ordinal()], timerPowerOn.channelNumber, timerPowerOn.volume);

        /* init eSleepMode */
        eSleepMode = enSleepTimeState;

        bOffTimerFlag = false;
    }

    @Override
    public boolean execEpgTimerAction() throws RemoteException {
        try {
            return TvManager.getInstance().getTimerManager().execEpgTimerAction();
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public int getClockOffset() throws RemoteException {
        try {
            return TvManager.getInstance().getTimerManager().getClockOffset();
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        return -1;
    }

    @Override
    public StandardTime getCurTimer() throws RemoteException {
        try {
            mCurrentTime = TvManager.getInstance().getTimerManager().getClkTime();
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        StandardTime curStandardTime = new StandardTime();
        curStandardTime.year = 1900 + mCurrentTime.year;
        curStandardTime.month = (short) (1 + mCurrentTime.month);
        curStandardTime.hour = mCurrentTime.hour;
        curStandardTime.minute = mCurrentTime.minute;
        curStandardTime.second = mCurrentTime.second;
        curStandardTime.monthDay = mCurrentTime.monthDay;
        curStandardTime.weekDay = mCurrentTime.weekDay;
        curStandardTime.isDst = mCurrentTime.isDst;

        return curStandardTime;
    }

    @Override
    public StandardTime getCurrentTvTime() throws RemoteException {
        try {
            mCurrentTime = TvManager.getInstance().getTimerManager().getClkTime();
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        StandardTime curStandardTime = new StandardTime();
        curStandardTime.year = 1900 + mCurrentTime.year;
        curStandardTime.month = mCurrentTime.month;
        curStandardTime.hour = mCurrentTime.hour;
        curStandardTime.minute = mCurrentTime.minute;
        curStandardTime.second = mCurrentTime.second;
        curStandardTime.monthDay = mCurrentTime.monthDay;
        curStandardTime.weekDay = mCurrentTime.weekDay;
        curStandardTime.isDst = mCurrentTime.isDst;

        return curStandardTime;
    }

    @Override
    public boolean getDaylightSavingState() throws RemoteException {
        try {
            return TvManager.getInstance().getTimerManager().getDaylightSavingState();
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public StandardTime getOffTimer() throws RemoteException {
        StandardTime standardTime = new StandardTime();
        standardTime.monthDay = (short) mOffTime.monthDay;
        standardTime.month = (short) mOffTime.month;
        standardTime.year = mOffTime.year;
        standardTime.hour = (short) mOffTime.hour;
        standardTime.minute = (short) mOffTime.minute;
        standardTime.second = (short) mOffTime.second;

        return standardTime;
    }

    @Override
    public OnTimeTvDescriptor getOnTimeEvent() throws RemoteException {
        return mPowerOnConfig;
    }

    @Override
    public StandardTime getOnTimer() throws RemoteException {
        StandardTime standardTime = new StandardTime();
        standardTime.monthDay = (short) mOnTime.monthDay;
        standardTime.month = (short) mOnTime.month;
        standardTime.year = mOnTime.year;
        standardTime.hour = (short) mOnTime.hour;
        standardTime.minute = (short) mOnTime.minute;
        standardTime.second = (short) mOnTime.second;

        return standardTime;
    }

    @Override
    public int getRtcClock() throws RemoteException {
        try {
            return TvManager.getInstance().getTimerManager().getRtcClock();
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        return -1;
    }

    public boolean setSleepTimeInMins(int minutesTime) throws RemoteException {
        Log.d(TAG, "setSleepTimeInMins, paras minutesTime is " + minutesTime);
        try {
            if (TvManager.getInstance() != null) {
                TvManager.getInstance().getTimerManager().setSleepTime(minutesTime);
            }
        } catch (TvCommonException e) {
            e.printStackTrace();
            return false;
        }
        return true;
    }

    public int getSleepTimeRemainMins() throws RemoteException {
        Log.d(TAG, "GetSleepTimeRemainMins ");
        int remainTime = 0;
        try {
            if (TvManager.getInstance() != null) {
                remainTime = TvManager.getInstance().getTimerManager().getSleepModeTime();
            }
        } catch (TvCommonException e) {
            e.printStackTrace();
            // return invalid remain time if exception happens
            remainTime = -1;
        }
        return remainTime;
    }

    @Override
    public int getSleepMode() throws RemoteException {
        // TODO: modify return type from int to EnumSleepTimeState
        // need to modify TV api as well

        return eSleepMode.ordinal();

    }

    @Override
    public int getTimeZone() throws RemoteException {
        // TODO: modify return type from int to EnumTimeZone
        // need to modify TV api as well

        EnumTimeZone timeZone = null;
        try {
            timeZone = TvManager.getInstance().getTimerManager().getTimeZone();
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        return timeZone.getValue();
    }

    @Override
    public boolean isOffTimerEnable() throws RemoteException {
        return bOffTimerFlag;
    }

    @Override
    public boolean isOnTimerEnable() throws RemoteException {
        try {
            timerPowerOn = TvManager.getInstance().getTimerManager().getOnTime();
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        if (EnumTimerPeriod.EN_Timer_Off == timerPowerOn.getTimerPeriod()) {
            return false;
        } else {
            return true;
        }
    }

    @Override
    public boolean setAutoSync(boolean isSync) throws RemoteException {
        try {
            return TvManager.getInstance().getPlayerManager().setAutoSync(isSync);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public void setClkTime(long time, boolean isSave) throws RemoteException {
        Time basetime = new Time();
        basetime.set(time);
        basetime.month += 1;
        try {
            TvManager.getInstance().getTimerManager().setClkTime(basetime, isSave);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void setDaylightSavingState(boolean isEnable) throws RemoteException {
        try {
            TvManager.getInstance().getTimerManager().setDaylightSavingState(isEnable);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean setOffTimer(StandardTime time) throws RemoteException {
        mOffTime.monthDay = time.monthDay;
        mOffTime.month = time.month;
        mOffTime.year = time.year;
        mOffTime.hour = time.hour;
        mOffTime.minute = time.minute;
        mOffTime.second = time.second;
        return true;
    }

    @Override
    public boolean setOffTimerEnable(boolean isEnable) throws RemoteException {
        bOffTimerFlag = isEnable;
        if (true == bOffTimerFlag) {
            timerPowerOffModeStatus.set(mOffTime);
            timerPowerOffModeStatus.setTimerPeriod(EnumTimerPeriod.EN_Timer_Once);
            try {
                TvManager.getInstance().getTimerManager()
                        .setOffModeStatus(timerPowerOffModeStatus, true);
            } catch (TvCommonException e) {
                e.printStackTrace();
            }
        } else {
            timerPowerOffModeStatus.set(mOffTime);
            timerPowerOffModeStatus.setTimerPeriod(EnumTimerPeriod.EN_Timer_Off);
            try {
                TvManager.getInstance().getTimerManager()
                        .disablePowerOffMode(EnumOffTimerMode.OFF_MODE_TIMER);
                TvManager.getInstance().getTimerManager()
                        .setOffModeStatus(timerPowerOffModeStatus, true);
            } catch (TvCommonException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    @Override
    public boolean setOnTimeEvent(OnTimeTvDescriptor event) throws RemoteException {
        mPowerOnConfig = event;
        return true;
    }

    @Override
    public boolean setOnTimer(StandardTime time) throws RemoteException {
        mOnTime.monthDay = time.monthDay;
        mOnTime.month = time.month;
        mOnTime.year = time.year;
        mOnTime.hour = time.hour;
        mOnTime.minute = time.minute;
        mOnTime.second = time.second;
        return true;
    }

    @Override
    public boolean setOnTimerEnable(boolean isEnable) throws RemoteException {
        if (true == isEnable) {
            timerPowerOn.set(mOnTime);
            timerPowerOn.channelNumber = mPowerOnConfig.mChNo;
            timerPowerOn.volume = mPowerOnConfig.mVol;
            timerPowerOn.setTvSource(EnumTimeOnTimerSource.values()[mPowerOnConfig.enTVSrc
                    .ordinal()]);
            timerPowerOn.setBootMode(EnumTimerBootType.EN_TIMER_BOOT_ON_TIMER);
            timerPowerOn.setTimerPeriod(EnumTimerPeriod.EN_Timer_Once);
            EnumEpgTimerActType enEPGTimerActionMode = EnumEpgTimerActType.EN_EPGTIMER_ACT_NONE;

            try {
                TvManager.getInstance().getTimerManager()
                        .setOnTime(timerPowerOn, true, true, enEPGTimerActionMode);
            } catch (TvCommonException e) {
                e.printStackTrace();
            }
        } else {
            timerPowerOn.set(mOnTime);
            timerPowerOn.channelNumber = mPowerOnConfig.mChNo;
            timerPowerOn.volume = mPowerOnConfig.mVol;
            timerPowerOn.setTvSource(EnumTimeOnTimerSource.values()[mPowerOnConfig.enTVSrc
                    .ordinal()]);
            timerPowerOn.setBootMode(EnumTimerBootType.EN_TIMER_BOOT_ON_TIMER);
            timerPowerOn.setTimerPeriod(EnumTimerPeriod.EN_Timer_Off);
            EnumEpgTimerActType enEPGTimerActionMode = EnumEpgTimerActType.EN_EPGTIMER_ACT_NONE;
            try {
                TvManager.getInstance().getTimerManager()
                        .setOnTime(timerPowerOn, true, true, enEPGTimerActionMode);
            } catch (TvCommonException e) {
                e.printStackTrace();
            }
        }
        return true;
    }

    @Override
    public boolean setSleepMode(int mode) throws RemoteException {
        // TODO: what's the different between eSleepMode & enSleepTimeState ?
        // modify (int mode) to (EnumSleepTimeState mode)
        // need to modify TV api as well

        eSleepMode = EnumSleepTimeState.values()[mode];
        enSleepTimeState = EnumSleepTimeState.values()[eSleepMode.ordinal()];
        try {
            TvManager.getInstance().getTimerManager().setSleepModeTime(enSleepTimeState);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        return true;
    }
	
	//EosTek Patch Begin
	public boolean setSleepTimeModeOff() throws RemoteException {
        // TODO: what's the different between eSleepMode & enSleepTimeState ?
        // modify (int mode) to (EnumSleepTimeState mode)
        // need to modify TV api as well

        eSleepMode = EnumSleepTimeState.E_OFF;
        enSleepTimeState = EnumSleepTimeState.E_OFF;
        return true;
    }
	//EosTek Patch End
	
    @Override
    public void setTimeZone(int zone, boolean isSaved) throws RemoteException {
        // TODO: modify (int zone) to (EnumTimeZone zone)
        // need to modify TV api as well

        EnumTimeZone timezone = EnumTimeZone.values()[zone];
        try {
            TvManager.getInstance().getTimerManager().setTimeZone(timezone, isSaved);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void updateTimeZone() throws RemoteException {
        try {
            EnumTimeZone timezone = EnumTimeZone.E_TIMEZONE_GMT_0_START;
            int minoffset = getMinuteOffset();
            int houroffset = getHourOffset();
            boolean bUpdateTZ = true;

            Log.d(TAG, "getMinuteOffset() = " + minoffset);
            Log.d(TAG, "getHourOffset() = " + houroffset);

            if (minoffset == OFFSET_MINUTE_00) {
                switch (houroffset) {
                    case OFFSET_HOUR_MINUS_11:
                        timezone = EnumTimeZone.E_TIMEZONE_GMT_MINUS11_START;
                        break;
                    case OFFSET_HOUR_MINUS_10:
                        timezone = EnumTimeZone.E_TIMEZONE_GMT_MINUS10_START;
                        break;
                    case OFFSET_HOUR_MINUS_09:
                        timezone = EnumTimeZone.E_TIMEZONE_GMT_MINUS9_START;
                        break;
                    case OFFSET_HOUR_MINUS_08:
                        timezone = EnumTimeZone.E_TIMEZONE_GMT_MINUS8_START;
                        break;
                    case OFFSET_HOUR_MINUS_07:
                        timezone = EnumTimeZone.E_TIMEZONE_GMT_MINUS7_START;
                        break;
                    case OFFSET_HOUR_MINUS_06:
                        timezone = EnumTimeZone.E_TIMEZONE_GMT_MINUS6_START;
                        break;
                    case OFFSET_HOUR_MINUS_05:
                        timezone = EnumTimeZone.E_TIMEZONE_GMT_MINUS5_START;
                        break;
                    case OFFSET_HOUR_MINUS_04:
                        timezone = EnumTimeZone.E_TIMEZONE_GMT_MINUS4_START;
                        break;
                    case OFFSET_HOUR_MINUS_03:
                        timezone = EnumTimeZone.E_TIMEZONE_GMT_MINUS3_START;
                        break;
                    case OFFSET_HOUR_MINUS_02:
                        timezone = EnumTimeZone.E_TIMEZONE_GMT_MINUS2_START;
                        break;
                    case OFFSET_HOUR_MINUS_01:
                        timezone = EnumTimeZone.E_TIMEZONE_GMT_MINUS1_START;
                        break;
                    case OFFSET_HOUR_00:
                        timezone = EnumTimeZone.E_TIMEZONE_GMT_0_START;
                        break;
                    case OFFSET_HOUR_PLUS_01:
                        timezone = EnumTimeZone.E_TIMEZONE_GMT_1_START;
                        break;
                    case OFFSET_HOUR_PLUS_02:
                        timezone = EnumTimeZone.E_TIMEZONE_GMT_2_START;
                        break;
                    case OFFSET_HOUR_PLUS_03:
                        timezone = EnumTimeZone.E_TIMEZONE_GMT_3_START;
                        break;
                    case OFFSET_HOUR_PLUS_04:
                        timezone = EnumTimeZone.E_TIMEZONE_GMT_4_START;
                        break;
                    case OFFSET_HOUR_PLUS_05:
                        timezone = EnumTimeZone.E_TIMEZONE_GMT_5_START;
                        break;
                    case OFFSET_HOUR_PLUS_06:
                        timezone = EnumTimeZone.E_TIMEZONE_GMT_6_START;
                        break;
                    case OFFSET_HOUR_PLUS_07:
                        timezone = EnumTimeZone.E_TIMEZONE_GMT_7_START;
                        break;
                    case OFFSET_HOUR_PLUS_08:
                        timezone = EnumTimeZone.E_TIMEZONE_GMT_8_START;
                        break;
                    case OFFSET_HOUR_PLUS_09:
                        timezone = EnumTimeZone.E_TIMEZONE_GMT_9_START;
                        break;
                    case OFFSET_HOUR_PLUS_10:
                        timezone = EnumTimeZone.E_TIMEZONE_GMT_10_START;
                        break;
                    case OFFSET_HOUR_PLUS_11:
                        timezone = EnumTimeZone.E_TIMEZONE_GMT_11_START;
                        break;
                    case OFFSET_HOUR_PLUS_12:
                        timezone = EnumTimeZone.E_TIMEZONE_GMT_12_START;
                        break;
                    case OFFSET_HOUR_PLUS_13:
                        timezone = EnumTimeZone.E_TIMEZONE_GMT_13_START;
                        break;
                    default:
                        bUpdateTZ = false;
                        Log.e(TAG, "this case is minutes offset 0. default case: set time zone fail!");
                        break;
                }
            } else if (minoffset == OFFSET_MINUTE_PLUS_30 || minoffset == OFFSET_MINUTE_MINUS_30) {
                switch (houroffset) {
                    case OFFSET_HOUR_MINUS_04:
                        timezone = EnumTimeZone.E_TIMEZONE_GMT_MINUS4_5_START;
                        break;
                    case OFFSET_HOUR_MINUS_03:
                        timezone = EnumTimeZone.E_TIMEZONE_GMT_MINUS3_5_START;
                        break;
                    case OFFSET_HOUR_MINUS_02:
                        timezone = EnumTimeZone.E_TIMEZONE_GMT_MINUS2_5_START;
                        break;
                    case OFFSET_HOUR_PLUS_03:
                        timezone = EnumTimeZone.E_TIMEZONE_GMT_3POINT5_START;
                        break;
                    case OFFSET_HOUR_PLUS_04:
                        timezone = EnumTimeZone.E_TIMEZONE_GMT_4POINT5_START;
                        break;
                    case OFFSET_HOUR_PLUS_05:
                        timezone = EnumTimeZone.E_TIMEZONE_GMT_5POINT5_START;
                        break;
                    case OFFSET_HOUR_PLUS_06:
                        timezone = EnumTimeZone.E_TIMEZONE_GMT_6POINT5_START;
                        break;
                    case OFFSET_HOUR_PLUS_09:
                        timezone = EnumTimeZone.E_TIMEZONE_GMT_9POINT5_START;
                        break;
                    case OFFSET_HOUR_PLUS_10:
                        timezone = EnumTimeZone.E_TIMEZONE_GMT_10POINT5_START;
                        break;
                    default:
                        bUpdateTZ = false;
                        Log.e(TAG, "this case is minutes offset 30. default case: set timezone fail!");
                        break;
                }
            } else if (minoffset == OFFSET_MINUTE_PLUS_45 || minoffset == OFFSET_MINUTE_MINUS_45) {
                switch (houroffset) {
                    case OFFSET_HOUR_PLUS_05:
                        timezone = EnumTimeZone.E_TIMEZONE_GMT_5POINT45_START;
                        break;
                    default:
                        bUpdateTZ = false;
                        Log.e(TAG, "this case is minutes offset 45. default case: set timezone fail!");
                        break;
                }
            } else {
                bUpdateTZ = false;
                Log.e(TAG, "this case is minutes offset unknown. set timezone fail!");
            }

            if (bUpdateTZ == true)
                TvManager.getInstance().getTimerManager().setTimeZone(timezone, true);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
    }

    private int getHourOffset() {
        TimeZone tz = Calendar.getInstance().getTimeZone();
        int gmtOffset = tz.getRawOffset();
        int houroffset = gmtOffset / MILLISECONDS_PER_HOUR;
        return houroffset;
    }

    private int getMinuteOffset() {
        TimeZone tz = Calendar.getInstance().getTimeZone();
        int gmtOffset = tz.getRawOffset();
        int minoffset = gmtOffset / MILLISECONDS_PER_MINUTE;
        minoffset %= MINUTES_PER_HOUR;
        return minoffset;
    }

    @Override
    public int addEpgEvent(EpgEventTimerInfo info) throws RemoteException {
        // TODO: modify return type from int to EnumEpgTimerCheck

        try {
            return TvManager.getInstance().getTimerManager().addEpgEvent(info).ordinal();
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public void cancelEpgTimerEvent(int timeActing, boolean checkEndTime) throws RemoteException {
        try {
            TvManager.getInstance().getTimerManager().cancelEpgTimerEvent(timeActing, checkEndTime);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean delAllEpgEvent() throws RemoteException {
        try {
            return TvManager.getInstance().getTimerManager().delAllEpgEvent();
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public boolean delEpgEvent(int epgEvent) throws RemoteException {
        try {
            return TvManager.getInstance().getTimerManager().delEpgEvent(epgEvent);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public EpgEventTimerInfo getEpgTimerEventByIndex(int index) throws RemoteException {
        try {
            return TvManager.getInstance().getTimerManager().getEpgTimerEventByIndex(index);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public int getEpgTimerEventCount() throws RemoteException {
        try {
            return TvManager.getInstance().getTimerManager().getEpgTimerEventCount();
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public EpgEventTimerInfo getEpgTimerRecordingProgram() throws RemoteException {
        try {
            return TvManager.getInstance().getTimerManager().getEpgTimerRecordingProgram();
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Override
    public boolean isEpgScheduleRecordRemiderExist(int secondsFromNow) throws RemoteException {
        try {
            return TvManager.getInstance().getTimerManager()
                    .isEpgScheduleRecordRemiderExist(secondsFromNow);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        return false;
    }

    @Override
    public int isEpgTimerSettingValid(EpgEventTimerInfo timerInfoVo) throws RemoteException {

        // TODO: modify return type from int to EnumEpgTimerCheck
        // and the function name "isEpgxxxxxx", let user thought it will return
        // boolean
        // refine the function name

        try {
            return TvManager.getInstance().getTimerManager().isEpgTimerSettingValid(timerInfoVo)
                    .ordinal();
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        return 0;
    }

    @Override
    public void addClient(IBinder client) throws RemoteException {
        DeskTimerEventListener.getInstance().addClient(client);
    }

    @Override
    public void removeClient(IBinder client) throws RemoteException {
        DeskTimerEventListener.getInstance().removeClient(client);
    }

    @Override
    public int getDaylightSavingMode() {
        TimeSetting setting = DatabaseDesk.getInstance(mContext).queryTimeSetting();
        return setting.daylightSavingMode;
    }

    @Override
    public void setDaylightSavingMode(int mode) {
        try {
            TimeSetting setting = DatabaseDesk.getInstance(mContext).queryTimeSetting();
            setting.daylightSavingMode = mode;
            DatabaseDesk.getInstance(mContext).updateTimeSetting(setting);
            if (TvTimerManager.DAYLIGHT_SAVING_USER_ON == mode) {
                TvManager.getInstance().getTimerManager().setDaylightSavingState(true);
            } else {
                TvManager.getInstance().getTimerManager().setDaylightSavingState(false);
            }
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
    }

    private class TimerEventListener extends IEventClient.Stub {
        @Override
        public boolean onEvent(Message msg) {
            switch (msg.what) {
                case TvTimerManager.TVTIMER_DESTROY_COUNTDOWN: {
                    // Broadcast for MTvHotKey
                    mContext.sendBroadcast(new Intent(
                            "com.android.server.tv.TIME_EVENT_DESTROY_COUNT_DOWN"));
                }
                    break;
                case TvTimerManager.TVTIMER_LAST_MINUTE_WARN: {
                    // Broadcast for MTvHotKey
                    mContext.sendBroadcast(new Intent(
                            "com.android.server.tv.TIME_EVENT_LAST_MINUTE_WARN"));
                }
                    break;
                case TvTimerManager.TVTIMER_LAST_MINUTE_UPDATE: {
                    // Broadcast for MTvHotKey
                    Intent intent = new Intent(
                            "com.android.server.tv.TIME_EVENT_LAST_MINUTE_UPDATE");
                    intent.putExtra("LeftTime", msg.arg1);
                    intent.putExtra("OffMode", msg.arg2);
                    mContext.sendBroadcast(intent);
                }
                    break;
                case TvTimerManager.TVTIMER_POWER_DOWNTIME: {
                        // Broadcast for MTvHotKey
                        Intent intent = new Intent(
                                "com.android.server.tv.TIME_EVENT_POWER_DOWNTIME");
                        mContext.sendBroadcast(intent);

                        Thread sendPowerKey = new Thread() {
                            public void run() {
                                int eventCode = KeyEvent.KEYCODE_POWER;
                                long now = SystemClock.uptimeMillis();

                                KeyEvent down = new KeyEvent(now, now, KeyEvent.ACTION_DOWN,
                                        eventCode, 0);
                                KeyEvent up = new KeyEvent(now, now, KeyEvent.ACTION_UP, eventCode,
                                        0);
                                InputManager.getInstance().injectInputEvent(down,
                                        InputManager.INJECT_INPUT_EVENT_MODE_WAIT_FOR_FINISH);
                                InputManager.getInstance().injectInputEvent(up,
                                        InputManager.INJECT_INPUT_EVENT_MODE_WAIT_FOR_FINISH);
                            }
                        };
                        sendPowerKey.start();
                }
                    break;
                case TvTimerManager.TVTIMER_SYSTEM_CLOCK_CHANGE: {
                    // Broadcast for MTvHotKey
                    mContext.sendBroadcast(new Intent(
                            "com.android.server.tv.TIME_EVENT_SYSTEM_CLOCK_CHANGE"));
                }
                    break;
                case TvTimerManager.TVTIMER_EPG_TIMER_COUNTDOWN: {
                    int leftTimeValue = msg.arg1;
                    if (leftTimeValue == 0) {
                        // reset countdown dialog status
                        CountDownTimerDialog.isEPGTimerActionInvoked = false;

                        targetIntent = new Intent("source.switch.from.storage");
                        mContext.sendBroadcast(targetIntent);
						//EosTek Patch Begin
						/*
                        new Thread(new Runnable() {
                            @Override
                            public void run() {
                                try {
                                    Thread.sleep(1000);
                                } catch (InterruptedException e) {
                                    e.printStackTrace();
                                }
                                TvCommonManager tvCommonmanager = TvCommonManager.getInstance();
                                final int currentTvInputSource = tvCommonmanager.getCurrentTvInputSource();
                                TvS3DManager.getInstance().setDisplayFormatForUI(
                                        TvS3DManager.THREE_DIMENSIONS_DISPLAY_FORMAT_NONE);
                                targetIntent = new Intent(
                                        "com.mstar.tv.tvplayer.ui.intent.action.RootActivity");
                                targetIntent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                                mContext.startActivity(targetIntent);
                                if (currentTvInputSource != TvCommonManager.INPUT_SOURCE_DTV) {
                                    tvCommonmanager.setInputSource(TvCommonManager.INPUT_SOURCE_DTV);
                                }
                                Log.i(TAG,
                                        "leftTimeValue==0 and in current CH -> execEpgTimerAction ");
                                TvTimerManager.getInstance().execEpgTimerAction();
                            }
                        }).start();
                    } else {
                        Log.i(TAG, "leftTimeValue > 0 show count Down page");
                        if (CountDownTimerDialog.isCountDownTimerDialogShowing == false) {
                            if (CountDownTimerDialog.isEPGTimerActionInvoked == true) {
                                Log.i(TAG, "Bypass TvTimerManager.TVTIMER_EPG_TIMER_COUNTDOWN since EPG action be invoked directly..");
                            } else {
                                CountDownTimerDialog coundDownTimerDialog = new CountDownTimerDialog(
                                        mContext, android.R.style.Theme_Panel);
                                coundDownTimerDialog.getWindow().setType(
                                        WindowManager.LayoutParams.TYPE_SYSTEM_ALERT);
                                coundDownTimerDialog.show();
                            }
                        }*/
						//EosTek Patch End
                    }
                }
                    break;
                case TvTimerManager.TVTIMER_TIME_ZONE_CHG: {
                    Intent intent = new Intent(
                            "com.mstar.tv.tvplayer.ui.intent.action.TV_TIME_ZONE_CHANGE");
                    intent.putExtra("timezoneChangeString", ((TimeZoneChangeInfo)msg.obj).timezoneString);
                    mContext.sendBroadcast(intent);
                }
                    break;
                default: {
                }
                    break;
            }
            return true;
        }
    }
}
