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
import android.os.Bundle;
import android.os.Looper;
import android.os.ServiceManager;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.text.format.Time;

import com.mstar.android.tv.ITvTimer;
import com.mstar.android.tv.ITimerEventClient;
import com.mstar.android.tvapi.common.exception.TvCommonException;
import com.mstar.android.tvapi.common.vo.OnTimeTvDescriptor;
import com.mstar.android.tvapi.common.vo.StandardTime;
import com.mstar.android.tvapi.common.vo.EnumSleepTimeState;
import com.mstar.android.tvapi.common.vo.TvOsType.EnumTimeZone;
import com.mstar.android.tvapi.common.vo.EpgEventTimerInfo;
import com.mstar.android.tvapi.dtv.vo.EnumEpgTimerCheck;
import com.mstar.android.tvapi.common.TimerManager;
import com.mstar.android.tvapi.common.TimerManager.OnTimerEventListener;

/**
 * <b>TvTimerManager class is for purpose of controlling timer management
 * from client APK.</b><br/>
 */
public class TvTimerManager extends IEventClient.Stub {
    private final static String TAG = "TvTimerManager";

    /*
     * Do not change these values without updating their counterparts in
     * device/mstar/common/libraries/tvapi/common/TimerManager.java
     */
    /**
     * Destroy Countdown.
     *
     * @see com.mstar.android.tv.TvTimerManager.OnCountdownEventListener
     */
    public static final int TVTIMER_DESTROY_COUNTDOWN = 0;

    /**
     * Last minute countdown warning.
     *
     * @see com.mstar.android.tv.TvTimerManager.OnCountdownEventListener
     */
    public static final int TVTIMER_LAST_MINUTE_WARN = 1;

    /**
     * Update countdown time in last minute.
     *
     * @see com.mstar.android.tv.TvTimerManager.OnCountdownEventListener
     */
    public static final int TVTIMER_LAST_MINUTE_UPDATE = 2;

    /**
     * Power down event.
     *
     * @see com.mstar.android.tv.TvTimerManager.OnCountdownEventListener
     */
    public static final int TVTIMER_POWER_DOWNTIME = 3;

    /**
     * One second heartbeat.
     *
     * @see com.mstar.android.tv.TvTimerManager.OnSystemClockEventListener
     */
    public static final int TVTIMER_BEAT_ONE_SECOND = 4;

    /**
     * System clock changed.
     *
     * @see com.mstar.android.tv.TvTimerManager.OnSystemClockEventListener
     */
    public static final int TVTIMER_SYSTEM_CLOCK_CHANGE = 5;

    /**
     * Signal Lock.
     *
     * @see com.mstar.android.tv.TvTimerManager.OnSignalEventListener
     */
    public static final int TVTIMER_SIGNAL_LOCK = 6;

    /**
     * EPG time up.
     *
     * @see com.mstar.android.tv.TvTimerManager.OnEpgTimerEventListener
     */
    public static final int TVTIMER_EPG_TIME_UP = 7;

    /**
     * EPG timer countdown.
     *
     * @see com.mstar.android.tv.TvTimerManager.OnEpgTimerEventListener
     */
    public static final int TVTIMER_EPG_TIMER_COUNTDOWN = 8;

    /**
     * EPG record start
     *
     * @see com.mstar.android.tv.TvTimerManager.OnEpgTimerEventListener
     */
    public static final int TVTIMER_EPG_TIMER_RECORD_START = 9;

    /**
     * Notify PVR record is stopped.
     *
     * @see com.mstar.android.tv.TvTimerManager.OnPvrTimerEventListener
     */
    public static final int TVTIMER_PVR_NOTIFY_RECORD_STOP = 10;

    /**
     * OAD scan timer event.
     *
     * @see com.mstar.android.tv.TvTimerManager.OnOadTimerEventListener
     */
    public static final int TVTIMER_OAD_TIME_SCAN = 11;

    /**
     * Post timezone change event
     *
     *
     */
    public static final int TVTIMER_TIME_ZONE_CHG = TimerManager.TVTIMER_TIME_ZONE_CHG;

    /**
     * Action types for EPG timer.
     */
    /** Action none. */
    public static final int EPG_TIMER_ACTION_NONE = 0;
    /** Action reminder. */
    public static final int EPG_TIMER_ACTION_REMINDER = 1;
    /** Action recorder starts. */
    public static final int EPG_TIMER_ACTION_RECORDER_START = 2;
    /** Action recorder stops. */
    public static final int EPG_TIMER_ACTION_RECORDER_STOP = 3;
    /** Action CI OP refresh */
    public static final int EPG_TIMER_ACTION_CI_OP_REFRESH = 4;

    /**
     * Sleep time state.
     */
    /** Sleep timer off */
    public static final int SLEEP_TIME_OFF = 0;
    /** 10 mins to sleep */
    public static final int SLEEP_TIME_10MIN = 1;
    /** 20 mins to sleep */
    public static final int SLEEP_TIME_20MIN = 2;
    /** 30 mins to sleep */
    public static final int SLEEP_TIME_30MIN = 3;
    /** 60 mins to sleep */
    public static final int SLEEP_TIME_60MIN = 4;
    /** 90 mins to sleep */
    public static final int SLEEP_TIME_90MIN = 5;
    /** 120 mins to sleep */
    public static final int SLEEP_TIME_120MIN = 6;
    /** 180 mins to sleep */
    public static final int SLEEP_TIME_180MIN = 7;
    /** 240 mins to sleep */
    public static final int SLEEP_TIME_240MIN = 8;

    /**
     * EPG timer check result.
     */
    /** EPG timer check result none */
    public static final int EPG_TIMECHECK_NONE = 0;
    /** EPG timer check result success */
    public static final int EPG_TIMECHECK_SUCCESS = 1;
    /** EPG timer check result past */
    public static final int EPG_TIMECHECK_PAST = 2;
    /** EPG timer check result overlay */
    public static final int EPG_TIMECHECK_OVERLAY = 3;
    /** EPG timer check result end time before start */
    public static final int EPG_TIMECHECK_ENDTIME_BEFORE_START = 4;
    /** EPG timer check result end time exceed period */
    public static final int EPG_TIMECHECK_ENDTIME_EXCEED_PERIOD = 5;

    /**
     * Daylight saving mode
     */
    /** auto */
    public static final int DAYLIGHT_SAVING_AUTO = 0;
    /** user set off */
    public static final int DAYLIGHT_SAVING_USER_OFF = 1;
    /** user set on */
    public static final int DAYLIGHT_SAVING_USER_ON = 2;

    /**
     * Interface defintion of a callback to be invoked when there has a
     * countdown event.
     */
    public interface OnCountdownEventListener {
        /**
         * Called to indicate a countdown event.
         *
         * @param what the type of countdown event that has occurred:
         *            <ul>
         *            <li> {@link #TVTIMER_DESTROY_COUNTDOWN}
         *            <li> {@link #TVTIMER_LAST_MINUTE_WARN}
         *            <li> {@link #TVTIMER_LAST_MINUTE_UPDATE}
         *            <ul>
         *            <li>arg1 - Remaining time
         *            <li>arg2 - Power off mode
         *            </ul>
         *            <li> {@link #TVTIMER_POWER_DOWNTIME}
         *            </ul>
         * @return True if the method handled the event, false if it didn't.
         */
        boolean onCountdownEvent(int what, int arg1, int arg2);
    }

    /**
     * Interface defintion of a callback to be invoked when there has a system
     * clock event.
     */
    public interface OnSystemClockEventListener {
        /**
         * Called to indicate a system clock event.
         *
         * @param what the type of system clock event that has occurred:
         *            <ul>
         *            <li> {@link #TVTIMER_BEAT_ONE_SECOND}
         *            <li> {@link #TVTIMER_SYSTEM_CLOCK_CHANGE}
         *            </ul>
         * @return True if the method handled the event, false if it didn't.
         */
        boolean onSystemClockEvent(int what);
    }

    /**
     * Interface defintion of a callback to be invoked when there has a signal
     * event.
     */
    public interface OnSignalEventListener {
        /**
         * Called to indicate a signal event.
         *
         * @param what the type of signal event that has occurred:
         *            <ul>
         *            <li> {@link #TVTIMER_SIGNAL_LOCK}
         *            </ul>
         * @return True if the method handled the event, false if it didn't.
         */
        boolean onSignalEvent(int what);
    }

    /**
     * Interface defintion of a callback to be invoked when there has a EPG
     * timer event.
     */
    public interface OnEpgTimerEventListener {
        /**
         * Called to indicate a EPG timer event.
         *
         * @param what the type of EPG timer event that has occurred:
         *            <ul>
         *            <li> {@link #TVTIMER_EPG_TIME_UP}
         *            <li> {@link #TVTIMER_EPG_TIMER_COUNTDOWN}
         *            <ul>
         *            <li>arg1 - Remaining time
         *            <li>arg2 - EPG timer action type
         *            </ul>
         *            <li> {@link #TVTIMER_EPG_TIMER_RECORD_START}
         *            <ul>
         *            <li>arg1 - Dtv route type of EPG event
         *            </ul>
         *            </ul>
         * @return True if the method handled the event, false if it didn't.
         * @see TvTimerManager#EPG_TIMER_ACTION_NONE
         * @see TvTimerManager#EPG_TIMER_ACTION_REMINDER
         * @see TvTimerManager#EPG_TIMER_ACTION_RECORDER_START
         * @see TvTimerManager#EPG_TIMER_ACTION_RECORDER_STOP
         * @see TvTimerManager#EPG_TIMER_ACTION_CI_OP_REFRESH
         */
        boolean onEpgTimerEvent(int what, int arg1, int arg2);
    }

    /**
     * Interface defintion of a callback to be invoked when there has a pvr
     * timer event.
     */
    public interface OnPvrTimerEventListener {
        /**
         * Called to indicate a pvr timer event.
         *
         * @param what the type of pvr timer event that has occurred:
         *            <ul>
         *            <li> {@link #TVTIMER_PVR_NOTIFY_RECORD_STOP}
         *            <ul>
         *            <li>arg1 - reserved
         *            <li>arg2 - reserved
         *            </ul>
         *            </ul>
         * @return True if the method handled the event, false if it didn't.
         */
        boolean onPvrTimerEvent(int what, int arg1, int arg2);
    }

    /**
     * Interface defintion of a callback to be invoked when there has a OAD
     * timer event.
     */
    public interface OnOadTimerEventListener {
        /**
         * Called to indicate a OAD timer event.
         *
         * @param what the type of OAD timer event that has occurred:
         *            <ul>
         *            <li> {@link #TVTIMER_OAD_TIME_SCAN}
         *            </ul>
         */
        boolean onOadTimerEvent(int what);
    }

    private static TvTimerManager mInstance = null;

    private static ITvTimer mService = null;

    private EventHandler mHandler = null;

    @Deprecated
    private final ArrayList<OnTimerEventListener> mTimerEventListeners = new ArrayList<OnTimerEventListener>();

    private final ArrayList<OnCountdownEventListener> mCountdownEventListeners = new ArrayList<OnCountdownEventListener>();

    private final ArrayList<OnSystemClockEventListener> mSystemClockEventListeners = new ArrayList<OnSystemClockEventListener>();

    private final ArrayList<OnSignalEventListener> mSignalEventListeners = new ArrayList<OnSignalEventListener>();

    private final ArrayList<OnEpgTimerEventListener> mEpgTimerEventListeners = new ArrayList<OnEpgTimerEventListener>();

    private final ArrayList<OnPvrTimerEventListener> mPvrTimerEventListeners = new ArrayList<OnPvrTimerEventListener>();

    private final ArrayList<OnOadTimerEventListener> mOadTimerEventListeners = new ArrayList<OnOadTimerEventListener>();

    private TvTimerManager() throws TvCommonException {
        IBinder b = ServiceManager.getService(Context.TV_SERVICE);

        if (b == null) {
            Log.e(TAG, "TvService doesn't exist!!");
            throw new TvCommonException("TvService doesn't exist.");
        }

        try {
            mService = ITvService.Stub.asInterface(b).getTvTimer();
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        Looper looper;
        if ((looper = Looper.myLooper()) != null) {
            mHandler = new EventHandler(looper);
        } else if ((looper = Looper.getMainLooper()) != null) {
            mHandler = new EventHandler(looper);
        } else {
            mHandler = null;
        }

        try {
            mService.addClient(this);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    private class EventHandler extends Handler {
        EventHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            if (msg.what <= TVTIMER_POWER_DOWNTIME) {
                synchronized (mCountdownEventListeners) {
                    for (OnCountdownEventListener l : mCountdownEventListeners) {
                        l.onCountdownEvent(msg.what, msg.arg1, msg.arg2);
                    }
                }
            } else if (msg.what <= TVTIMER_SYSTEM_CLOCK_CHANGE) {
                synchronized (mSystemClockEventListeners) {
                    for (OnSystemClockEventListener l : mSystemClockEventListeners) {
                        l.onSystemClockEvent(msg.what);
                    }
                }
            } else if (msg.what <= TVTIMER_SIGNAL_LOCK) {
                synchronized (mSignalEventListeners) {
                    for (OnSignalEventListener l : mSignalEventListeners) {
                        l.onSignalEvent(msg.what);
                    }
                }
            } else if (msg.what <= TVTIMER_EPG_TIMER_RECORD_START) {
                synchronized (mEpgTimerEventListeners) {
                    for (OnEpgTimerEventListener l : mEpgTimerEventListeners) {
                        l.onEpgTimerEvent(msg.what, msg.arg1, msg.arg2);
                    }
                }
            } else if (msg.what <= TVTIMER_PVR_NOTIFY_RECORD_STOP) {
                synchronized (mPvrTimerEventListeners) {
                    for (OnPvrTimerEventListener l : mPvrTimerEventListeners) {
                        l.onPvrTimerEvent(msg.what, msg.arg1, msg.arg2);
                    }
                }
            } else if (msg.what <= TVTIMER_OAD_TIME_SCAN) {
                synchronized (mOadTimerEventListeners) {
                    for (OnOadTimerEventListener l : mOadTimerEventListeners) {
                        l.onOadTimerEvent(msg.what);
                    }
                }
            }

            // FIXME: old architecture, remove later
            switch (msg.what) {
                case TVTIMER_DESTROY_COUNTDOWN: {
                    synchronized (mTimerEventListeners) {
                        for (OnTimerEventListener l : mTimerEventListeners) {
                            l.onDestroyCountDown(null, msg.what, msg.arg1, msg.arg2);
                        }
                    }
                }
                    break;
                case TVTIMER_LAST_MINUTE_WARN: {
                    synchronized (mTimerEventListeners) {
                        for (OnTimerEventListener l : mTimerEventListeners) {
                            l.onLastMinuteWarn(null, msg.what, msg.arg1, msg.arg2);
                        }
                    }
                }
                    break;
                case TVTIMER_LAST_MINUTE_UPDATE: {
                    synchronized (mTimerEventListeners) {
                        for (OnTimerEventListener l : mTimerEventListeners) {
                            l.onUpdateLastMinute(null, msg.what, msg.arg1, msg.arg2);
                        }
                    }
                }
                    break;
                case TVTIMER_POWER_DOWNTIME: {
                    synchronized (mTimerEventListeners) {
                        for (OnTimerEventListener l : mTimerEventListeners) {
                            l.onPowerDownTime(null, msg.what, msg.arg1, msg.arg2);
                        }
                    }
                }
                    break;
                case TVTIMER_BEAT_ONE_SECOND: {
                    synchronized (mTimerEventListeners) {
                        for (OnTimerEventListener l : mTimerEventListeners) {
                            l.onOneSecondBeat(null, msg.what, msg.arg1, msg.arg2);
                        }
                    }
                }
                    break;
                case TVTIMER_SYSTEM_CLOCK_CHANGE: {
                    synchronized (mTimerEventListeners) {
                        for (OnTimerEventListener l : mTimerEventListeners) {
                            l.onSystemClkChg(null, msg.what, msg.arg1, msg.arg2);
                        }
                    }
                }
                    break;
                case TVTIMER_SIGNAL_LOCK: {
                    synchronized (mTimerEventListeners) {
                        for (OnTimerEventListener l : mTimerEventListeners) {
                            l.onSignalLock(null, msg.what, msg.arg1, msg.arg2);
                        }
                    }
                }
                    break;
                case TVTIMER_EPG_TIME_UP: {
                    synchronized (mTimerEventListeners) {
                        for (OnTimerEventListener l : mTimerEventListeners) {
                            l.onEpgTimeUp(null, msg.what, msg.arg1, msg.arg2);
                        }
                    }
                }
                    break;
                case TVTIMER_EPG_TIMER_COUNTDOWN: {
                    synchronized (mTimerEventListeners) {
                        for (OnTimerEventListener l : mTimerEventListeners) {
                            l.onEpgTimerCountDown(null, msg.what, msg.arg1, msg.arg2);
                        }
                    }
                }
                    break;
                case TVTIMER_EPG_TIMER_RECORD_START: {
                    synchronized (mTimerEventListeners) {
                        for (OnTimerEventListener l : mTimerEventListeners) {
                            l.onEpgTimerRecordStart(null, msg.what, msg.arg1, msg.arg2);
                        }
                    }
                }
                    break;
                case TVTIMER_PVR_NOTIFY_RECORD_STOP: {
                    synchronized (mTimerEventListeners) {
                        for (OnTimerEventListener l : mTimerEventListeners) {
                            l.onPvrNotifyRecordStop(null, msg.what, msg.arg1, msg.arg2);
                        }
                    }
                }
                    break;
                case TVTIMER_OAD_TIME_SCAN: {
                    synchronized (mTimerEventListeners) {
                        for (OnTimerEventListener l : mTimerEventListeners) {
                            l.onOadTimeScan(null, msg.what, msg.arg1, msg.arg2);
                        }
                    }
                }
                    break;
                default: {
                    Log.e(TAG, "Unknown message type " + msg.what);
                }
                    break;
            }
        }
    }

    public static TvTimerManager getInstance() {
        /* Double-checked locking */
        if (mInstance == null) {
            synchronized (TvTimerManager.class) {
                if (mInstance == null) {
                    try {
                        mInstance = new TvTimerManager();
                    } catch (TvCommonException e) {
                        e.printStackTrace();
                        return null;
                    }
                }
            }
        }
        return mInstance;
    }

    /**
     * Get current time for Android standard format
     *
     * @return StandardTime extends Time(android standard format)
     * @deprecated Use {@link getCurrentTvTime()}
     */
    @Deprecated
    public StandardTime getCurTimer() {
        StandardTime time = null;
        try {
            time = mService.getCurTimer();
            Log.d(TAG, "getCurTimer:" + time.year + "." + time.month + "." + time.monthDay + "."
                    + time.hour + "." + time.minute + "." + time.second);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return time;
    }

    /**
     * Get Current TV System Time
     *
     * @return Time. The Current TV System Time
     */
    public Time getCurrentTvTime() {
        Time time = null;
        try {
            time = new Time(mService.getCurrentTvTime());
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return time;
    }

    /**
     * Set auto power on time. Note: just store input time to local variable. it
     * will not work until call setOnTimerEnable
     *
     * @param time StandardTime extends Time(android standard format)
     * @return boolean true: success, or false: fail
     * @deprecated Use {@link setTvOnTimer(Time time)}
     */
    @Deprecated
    public boolean setOnTimer(StandardTime time) {
        Log.d(TAG, "setOnTimer:" + time.year + "." + time.month + "." + time.monthDay + "."
                + time.hour + "." + time.minute + "." + time.second);
        try {
            return mService.setOnTimer(time);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Set auto power on time. Note: just store input time to local variable. it
     * will not work until call setOnTimerEnable
     *
     * @param time StandardTime extends Time(android standard format)
     * @return boolean true: success, or false: fail
     */
    public boolean setTvOnTimer(Time time) {
        Log.d(TAG, "setTvOnTimer:" + time.year + "." + time.month + "." + time.monthDay + "."
                + time.hour + "." + time.minute + "." + time.second);
        StandardTime stdTime = new StandardTime();
        stdTime.set(time);
        try {
            return mService.setOnTimer(stdTime);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Get auto power on time
     *
     * @return StandardTime extends Time(android standard format)
     * @deprecated Use {@link getTvOnTimer()}
     */
    @Deprecated
    public StandardTime getOnTimer() {
        StandardTime time = null;
        try {
            time = mService.getOnTimer();
            Log.d(TAG, "getOnTimer, return StandardTime year = " + time.year + ", month = "
                    + time.month + ", day = " + time.monthDay + ", hour = " + time.hour
                    + ", minute = " + time.minute + ", second = " + time.second);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return time;
    }

    /**
     * Get auto power on time
     *
     * @return Time
     */
    public Time getTvOnTimer() {
        Time time = null;
        try {
            time = new Time(mService.getOnTimer());
            Log.d(TAG, "getTvOnTimer, return Time year = " + time.year + ", month = "
                    + time.month + ", day = " + time.monthDay + ", hour = " + time.hour
                    + ", minute = " + time.minute + ", second = " + time.second);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return time;
    }

    /**
     * Set auto power on event Note: just store it to local variable. it will
     * not work until call setOnTimerEnable
     *
     * @param stEvent OnTimeTvDescriptor contains input source, channel and
     *            volume.
     * @return boolean true: success, or false: fail
     */
    public boolean setOnTimeEvent(OnTimeTvDescriptor stEvent) {
        Log.d(TAG, "setOnTimeEvent, paras OnTimeTvDescriptor stEvent.mChNo = " + stEvent.mChNo
                + ", stEvent.mVol = " + stEvent.mVol + ", stEvent.enTVSrc = " + stEvent.enTVSrc);
        try {
            return mService.setOnTimeEvent(stEvent);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Get auto power on event
     *
     * @return OnTimeTvDescriptor contains input source, channel and volume.
     */
    public OnTimeTvDescriptor getOnTimeEvent() {
        OnTimeTvDescriptor descriptor = null;
        try {
            descriptor = mService.getOnTimeEvent();
            Log.d(TAG, "setOnTimeEvent, return OnTimeTvDescriptor mChNo = " + descriptor.mChNo
                    + ", mVol = " + descriptor.mVol + ", enTVSrc = " + descriptor.enTVSrc);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return descriptor;
    }

    /**
     * Enalbe auto power on feature. This function will load time and event
     * params set by setOnTimer and setOnTimeEvent to enable auto power on
     * feature.
     *
     * @param bEnable true: enable, false: disable
     * @return boolean true: success, or false: fail
     */
    public boolean setOnTimerEnable(boolean bEnable) {
        Log.d(TAG, "setAtvChannel(), paras bEnable = " + bEnable);
        try {
            return mService.setOnTimerEnable(bEnable);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * check if auto power on feature enable or disable
     *
     * @return boolean true: success, or false: fail
     */
    public boolean isOnTimerEnable() {
        try {
            return mService.isOnTimerEnable();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * set auto power off time Note: just store input time to local variable. it
     * will not work until call setOnTimerEnable
     *
     * @param time OnTimeTvDescriptor contains input source, channel and volume.
     * @return boolean true: success, or false: fail
     * @deprecated Use {@link setTvOffTimer(Time time)}
     */
    @Deprecated
    public boolean setOffTimer(StandardTime time) {
        Log.d(TAG, "setOffTimer:" + time.year + "." + time.month + "." + time.monthDay + "."
                + time.hour + "." + time.minute + "." + time.second);
        try {
            return mService.setOffTimer(time);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * set auto power off time Note: just store input time to local variable. it
     * will not work until call setOnTimerEnable
     *
     * @param time OnTimeTvDescriptor contains input source, channel and volume.
     * @return boolean true: success, or false: fail
     */
    public boolean setTvOffTimer(Time time) {
        Log.d(TAG, "setTvOffTimer:" + time.year + "." + time.month + "." + time.monthDay + "."
                + time.hour + "." + time.minute + "." + time.second);
        StandardTime stdTime = new StandardTime();
        stdTime.set(time);
        try {
            return mService.setOffTimer(stdTime);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * get auto power off time
     *
     * @return OnTimeTvDescriptor contains input source, channel and volume.
     * @deprecated Use {@link getTvOffTimer()}
     */
    @Deprecated
    public StandardTime getOffTimer() {
        StandardTime time = null;
        try {
            time = mService.getOffTimer();
            Log.d(TAG, "getOffTimer:" + time.year + "." + time.month + "." + time.monthDay + "."
                    + time.hour + "." + time.minute + "." + time.second);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return time;
    }

    /**
     * get auto power off time
     *
     * @return Time
     */
    public Time getTvOffTimer() {
        Time time = null;
        try {
            time = new Time(mService.getOffTimer());
            Log.d(TAG, "getTvOffTimer:" + time.year + "." + time.month + "." + time.monthDay + "."
                    + time.hour + "." + time.minute + "." + time.second);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return time;
    }
    /**
     * Enalbe auto power off feature. This function will load time set by
     * setOffTimer to enable auto power off feature.
     *
     * @param bEnable true: enable, false: disable
     * @return boolean true: success, or false: fail
     */
    public boolean setOffTimerEnable(boolean bEnable) {
        Log.d(TAG, "setAtvChannel(), paras bEnable = " + bEnable);
        try {
            return mService.setOffTimerEnable(bEnable);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Check if off timer is enable or diable
     *
     * @return boolean true: enable, false: disable
     */
    public boolean isOffTimerEnable() {
        try {
            return mService.isOffTimerEnable();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Set sleep mode: sleep after certain time.
     *
     * @param eMode sleep time mode
     * @return boolean true: success, false: fail
     * @deprecated Use {@link setSleepTimeMode(int mode)}
     */
    @Deprecated
    public boolean setSleepMode(EnumSleepTimeState eMode) {
        return setSleepTimeMode(eMode.ordinal());
    }

    /**
     * Get sleep time mode.
     *
     * @return EnumSleepTimeState mode enum
     * @deprecated Use {@link getSleepTimeMode()}
     */
    @Deprecated
    public EnumSleepTimeState getSleepMode() {
        return EnumSleepTimeState.values()[getSleepTimeMode()];
    }

    /**
     * Set sleep time mode: sleep after certain time.
     * <p>
     * The supported type are
     * <ul>
     * <li> {@link #SLEEP_TIME_OFF}
     * <li> {@link #SLEEP_TIME_10MIN}
     * <li> {@link #SLEEP_TIME_20MIN}
     * <li> {@link #SLEEP_TIME_30MIN}
     * <li> {@link #SLEEP_TIME_60MIN}
     * <li> {@link #SLEEP_TIME_90MIN}
     * <li> {@link #SLEEP_TIME_120MIN}
     * <li> {@link #SLEEP_TIME_180MIN}
     * <li> {@link #SLEEP_TIME_240MIN}
     * </ul>
     *
     * @param mode sleep time mode
     * @see #SLEEP_TIME_OFF
     * @see #SLEEP_TIME_10MIN
     * @see #SLEEP_TIME_20MIN
     * @see #SLEEP_TIME_30MIN
     * @see #SLEEP_TIME_60MIN
     * @see #SLEEP_TIME_90MIN
     * @see #SLEEP_TIME_120MIN
     * @see #SLEEP_TIME_180MIN
     * @see #SLEEP_TIME_240MIN
     * @return boolean true: success, false: fail
     */
    public boolean setSleepTimeMode(int mode) {
        Log.d(TAG, "setSleepTimeMode(), paras mode = " + mode);
        try {
            return mService.setSleepMode(mode);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }
	
	//EosTek Patch Begin
	public boolean setSleepTimeModeOff() {
        Log.d(TAG, "setSleepTimeMode(), paras mode = OFF");
        try {
            return mService.setSleepTimeModeOff();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }
	//EosTek Patch End
	
    /**
     * Set sleep time in mins: sleep after certain time.
     *
     * @param minutesTime sleep time, range from 0 to 60
     * @return boolean true: success. false: fail
     */
    public boolean setSleepTimeInMins(int minutesTime) {
        Log.d(TAG, "setSleepTimeInMins(), paras minutesTime = " + minutesTime);
        try {
            return mService.setSleepTimeInMins(minutesTime);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Get sleep time.
     *
     * @return int -1:fail, the others:success
     */
    public int getSleepTimeRemainMins() {
        Log.d(TAG, "GetSleepTimeRemainMins ");
        try {
            return mService.getSleepTimeRemainMins();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return -1;
    }

    /**
     * Get sleep time mode.
     * <p>
     * The supported type are
     * <ul>
     * <li> {@link #SLEEP_TIME_OFF}
     * <li> {@link #SLEEP_TIME_10MIN}
     * <li> {@link #SLEEP_TIME_20MIN}
     * <li> {@link #SLEEP_TIME_30MIN}
     * <li> {@link #SLEEP_TIME_60MIN}
     * <li> {@link #SLEEP_TIME_90MIN}
     * <li> {@link #SLEEP_TIME_120MIN}
     * <li> {@link #SLEEP_TIME_180MIN}
     * <li> {@link #SLEEP_TIME_240MIN}
     * </ul>
     *
     * @return int sleep time mode
     * @see #SLEEP_TIME_OFF
     * @see #SLEEP_TIME_10MIN
     * @see #SLEEP_TIME_20MIN
     * @see #SLEEP_TIME_30MIN
     * @see #SLEEP_TIME_60MIN
     * @see #SLEEP_TIME_90MIN
     * @see #SLEEP_TIME_120MIN
     * @see #SLEEP_TIME_180MIN
     * @see #SLEEP_TIME_240MIN
     */
    public int getSleepTimeMode() {
        int mode = SLEEP_TIME_OFF;
        try {
            mode = mService.getSleepMode();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        Log.d(TAG, "getSleepTimeMode(), return int " + mode);
        return mode;
    }

    /**
     * Get time zone.
     *
     * @return EnumTimeZone mode enum
     */
    public EnumTimeZone getTimeZone() {
        EnumTimeZone en = null;
        int iReturn;
        int iordinal;
        try {
            iReturn = mService.getTimeZone();
            iordinal = EnumTimeZone.getOrdinalThroughValue(iReturn);
            if (iordinal != -1) {
                en = EnumTimeZone.values()[iordinal];
            }
            Log.d(TAG, "getTimeZone(), return int " + iReturn);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return en;
    }

    /**
     * Set time zone.
     *
     * @param zone EnumTimeZone Timezone state, one of EnumTimeZone
     * @param isSave is save to EEPROM
     */
    public void setTimeZone(EnumTimeZone zone, boolean isSaved) {
        try {
            mService.setTimeZone(zone.ordinal(), isSaved);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return;
    }

    /**
     * Sync android timezone to tvos.
     */
    public void updateTimeZone() {
        try {
            mService.updateTimeZone();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return;
    }

    /**
     * Get RTC clock.
     *
     * @return int RTC clock time
     */
    public int getRtcClock() {
        int iReturn = 0;
        try {
            iReturn = mService.getRtcClock();
            Log.d(TAG, "getRtcClock(), return int " + iReturn);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return iReturn;
    }

    /**
     * Get clock offset.
     *
     * @return int clock offset
     */
    public int getClockOffset() {
        int iReturn = 0;
        try {
            iReturn = mService.getClockOffset();
            Log.d(TAG, "getRtcClock(), return int " + iReturn);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return iReturn;
    }

    /**
     * Set CLK Time.
     *
     * @param time CLK time
     * @param isSave is save to EEPROM
     */
    public void setClkTime(long time, boolean isSave) {
        /* check if timer service head is null */
        try {
            mService.setClkTime(time, isSave);
            Log.d(TAG, "set CLK Time");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Set auto sync setting
     *
     * @param bSync true: enable, false: disable
     * @return boolean true: success, or false: fail
     */
    public boolean setAutoSync(boolean bSync) {
        boolean result = false;
        try {
            result = mService.setAutoSync(bSync);
            Log.d(TAG, "set Auto Sync");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Set summer time setting
     *
     * @param flag true: enable, false: disable
     * @deprecated Use {@link setDaylightSavingMode(int mode)}
     */
    @Deprecated
    public void setDaylightSavingState(boolean flag) {
        try {
            mService.setDaylightSavingState(flag);
            Log.d(TAG, "set daylightsavingstate");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get summer time setting
     *
     * @param boolean true: enable, false: disable
     * @deprecated Use {@link getDaylightSavingMode()}
     */
    @Deprecated
    public boolean getDaylightSavingState() {
        boolean result = false;
        try {
            result = mService.getDaylightSavingState();
            Log.d(TAG, "get daylightsavingstate");
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return result;
    }

    /**
     * Get EPG Timer Event information by index
     *
     * @param index the EPG event index
     * @return structure EpgEventTimerInfo, it define EPG event related timer
     *         information
     */
    public EpgEventTimerInfo getEpgTimerEventByIndex(int index) {
        try {
            return mService.getEpgTimerEventByIndex(index);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Get a count is accumulated number from EPG timer related event
     *
     * @return int of number for all EPG timer count, 0~25
     */
    public int getEpgTimerEventCount() {
        try {
            return mService.getEpgTimerEventCount();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Add new EPG Event with structure EpgEventTimerInfo
     *
     * @param EpgEventTimerInfo a structure with EPG timer related information
     * @return the status which present adding event to database result by enum
     *         named EnumEpgTimerCheck
     * @deprecated Use {@link addEpgNewEvent(EpgEventTimerInfo vo)}
     */
    @Deprecated
    public EnumEpgTimerCheck addEpgEvent(EpgEventTimerInfo vo) {
        return EnumEpgTimerCheck.values()[addEpgNewEvent(vo)];
    }

    /**
     * Add new EPG Event with structure EpgEventTimerInfo
     * <p>
     * The supported type are
     * <ul>
     * <li> {@link #EPG_TIMECHECK_NONE}
     * <li> {@link #EPG_TIMECHECK_SUCCESS}
     * <li> {@link #EPG_TIMECHECK_PAST}
     * <li> {@link #EPG_TIMECHECK_OVERLAY}
     * <li> {@link #EPG_TIMECHECK_ENDTIME_BEFORE_START}
     * <li> {@link #EPG_TIMECHECK_ENDTIME_EXCEED_PERIOD}
     * </ul>
     *
     * @param vo a structure with EPG timer related information
     * @return int the status which present adding event to database result
     * @see #EPG_TIMECHECK_NONE
     * @see #EPG_TIMECHECK_SUCCESS
     * @see #EPG_TIMECHECK_PAST
     * @see #EPG_TIMECHECK_OVERLAY
     * @see #EPG_TIMECHECK_ENDTIME_BEFORE_START
     * @see #EPG_TIMECHECK_ENDTIME_EXCEED_PERIOD
     */
    public int addEpgNewEvent(EpgEventTimerInfo vo) {
        int ret = EPG_TIMECHECK_NONE;
        try {
            ret = mService.addEpgEvent(vo);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return ret;
    }

    /**
     * Delete an EPG Event by index
     *
     * @param epgEvent it used to present the EPG event index wnat to delete
     * @return boolean false if index over range, otherwise true
     */
    public boolean delEpgEvent(int epgEvent) {
        try {
            return mService.delEpgEvent(epgEvent);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Check if EPG reminder/recorder exists within input seconds.( used for
     * decide Auto channel update continue or not)
     *
     * @param secondsFromNow the time seconds from now
     * @return boolean true, if exist, otherwise false
     */
    public boolean isEpgScheduleRecordRemiderExist(int secondsFromNow) {
        try {
            return mService.isEpgScheduleRecordRemiderExist(secondsFromNow);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Delete all EPG timer event
     *
     * @return boolean true if success, false if no EPG timer.
     */
    public boolean delAllEpgEvent() {
        try {
            return mService.delAllEpgEvent();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Reconfig EPG timer list and setting monitors.
     *
     * @param timeActing the time before valid list items.
     * @param checkEndTime delete according to end time.
     */
    public void cancelEpgTimerEvent(int timeActing, boolean checkEndTime) {
        try {
            mService.cancelEpgTimerEvent(timeActing, checkEndTime);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get timer recording program
     *
     * @return EpgEventTimerInfo the current EPG timer information
     */
    public EpgEventTimerInfo getEpgTimerRecordingProgram() {
        try {
            return mService.getEpgTimerRecordingProgram();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * Check if the given event can be added to timer database successfully.
     *
     * @param timerInfoVo the given event(the EPG timer info)
     * @return EPG_TIMER_CHECK_SUCCESS if the event can be added, otherwise the
     *         error code returned
     * @deprecated Use {@link isEpgEventValid(int timerInfoVo)}
     */
    @Deprecated
    public EnumEpgTimerCheck isEpgTimerSettingValid(EpgEventTimerInfo timerInfoVo) {
        return EnumEpgTimerCheck.values()[isEpgEventValid(timerInfoVo)];
    }

    /**
     * Check if the given event can be added to timer database successfully.
     * <p>
     * The supported type are
     * <ul>
     * <li> {@link #EPG_TIMECHECK_NONE}
     * <li> {@link #EPG_TIMECHECK_SUCCESS}
     * <li> {@link #EPG_TIMECHECK_PAST}
     * <li> {@link #EPG_TIMECHECK_OVERLAY}
     * <li> {@link #EPG_TIMECHECK_ENDTIME_BEFORE_START}
     * <li> {@link #EPG_TIMECHECK_ENDTIME_EXCEED_PERIOD}
     * </ul>
     *
     * @param timerInfoVo a structure with EPG timer related information
     * @return int the EPG time check result
     * @see #EPG_TIMECHECK_NONE
     * @see #EPG_TIMECHECK_SUCCESS
     * @see #EPG_TIMECHECK_PAST
     * @see #EPG_TIMECHECK_OVERLAY
     * @see #EPG_TIMECHECK_ENDTIME_BEFORE_START
     * @see #EPG_TIMECHECK_ENDTIME_EXCEED_PERIOD
     */
    public int isEpgEventValid(EpgEventTimerInfo timerInfoVo) {
        int ret = EPG_TIMECHECK_NONE;
        try {
            ret = mService.isEpgTimerSettingValid(timerInfoVo);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return ret;
    }

    /**
     * Need to be review iff mandatory
     */
    /**
     * EPG timer action.
     *
     * @return boolean true: EPG timer action success, false: EPG timer action
     *         failure.
     * @throws TvCommonException
     */
    public boolean execEpgTimerAction() {
        Log.d(TAG, "execEpgTimerAction");
        try {
            return mService.execEpgTimerAction();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return false;
    }

     /**
     * Get daylight saving mode
     * <p>
     * The supported modes are
     * <ul>
     * <li> {@link #DAYLIGHT_SAVING_AUTO}
     * <li> {@link #DAYLIGHT_SAVING_USER_OFF}
     * <li> {@link #DAYLIGHT_SAVING_USER_ON}
     * </ul>
     *
     * @return int daylight saving mode
     * @see #DAYLIGHT_SAVING_AUTO
     * @see #DAYLIGHT_SAVING_USER_OFF
     * @see #DAYLIGHT_SAVING_USER_ON
     */
    public int getDaylightSavingMode() {
        int mode = -1;
        try {
            mode = mService.getDaylightSavingMode();
        } catch (RemoteException e) {
            e.printStackTrace();
        }
        return mode;
    }

    /**
     * Set daylight saving mode
     * <p>
     * The supported modes are
     * <ul>
     * <li> {@link #DAYLIGHT_SAVING_AUTO}
     * <li> {@link #DAYLIGHT_SAVING_USER_OFF}
     * <li> {@link #DAYLIGHT_SAVING_USER_ON}
     * </ul>
     *
     * @param mode daylight saving mode
     * @see #DAYLIGHT_SAVING_AUTO
     * @see #DAYLIGHT_SAVING_USER_OFF
     * @see #DAYLIGHT_SAVING_USER_ON
     */
    public void setDaylightSavingMode(int mode) {
        try {
            mService.setDaylightSavingMode(mode);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }

    @Override
    public boolean onEvent(Message msg) throws RemoteException {
        if (mHandler != null) {
            Message msgTmp = mHandler.obtainMessage();
            msgTmp.copyFrom(msg);
            mHandler.sendMessage(msgTmp);
        }
        return true;
    }

    /**
     * Register registerOnTimerEventListener(OnTimerEventListener listener),
     * your listener will be triggered when the events posted from native code.
     * Note: Remember to unregister the listener before your application on
     * destroy.
     *
     * @param listener OnTimerEventListener
     * @return TRUE - register success, FALSE - register fail.
     * @deprecated Use
     *             <ul>
     *             <li>
     *             {@link #registerOnCountdownEventListener(OnCountdownEventListener)}
     *             <li>
     *             {@link #registerOnSystemClockEventListener(OnSystemClockEventListener)}
     *             <li>
     *             {@link #registerOnSignalEventListener(OnSignalEventListener)}
     *             <li>
     *             {@link #registerOnEpgTimerEventListener(OnEpgTimerEventListener)}
     *             <li>
     *             {@link #registerOnPvrTimerEventListener(OnPvrTimerEventListener)}
     *             <li>
     *             {@link #registerOnOadTimerEventListener(OnOadTimerEventListener)}
     *             </ul>
     */
    @Deprecated
    public boolean registerOnTimerEventListener(OnTimerEventListener listener) {
        synchronized (mTimerEventListeners) {
            mTimerEventListeners.add(listener);
        }
        return true;
    }

    /**
     * Unegister your unregisterOnTimerEventListener class to service. Remember
     * to unregister the listener before your application on destroy.
     *
     * @param listener unregisterOnTimerEventListener
     * @deprecated Use
     *             <ul>
     *             <li>
     *             {@link #unregisterOnCountdownEventListener(OnCountdownEventListener)}
     *             <li>
     *             {@link #unregisterOnSystemClockEventListener(OnSystemClockEventListener)}
     *             <li>
     *             {@link #unregisterOnSignalEventListener(OnSignalEventListener)}
     *             <li>
     *             {@link #unregisterOnEpgTimerEventListener(OnEpgTimerEventListener)}
     *             <li>
     *             {@link #unregisterOnPvrTimerEventListener(OnPvrTimerEventListener)}
     *             <li>
     *             {@link #unregisterOnOadTimerEventListener(OnOadTimerEventListener)}
     *             </ul>
     */
    @Deprecated
    public boolean unregisterOnTimerEventListener(OnTimerEventListener listener) {
        synchronized (mTimerEventListeners) {
            mTimerEventListeners.remove(listener);
        }
        return true;
    }

    /**
     * Register registerOnCountdownEventListener(OnCountdownEventListener
     * listener), your listener will be triggered when the events posted from
     * native code. Note: Remember to unregister the listener before your
     * application on destroy.
     *
     * @param listener OnCountdownEventListener
     * @return boolean true: register success, false: register fail
     */
    public boolean registerOnCountdownEventListener(OnCountdownEventListener listener) {
        synchronized (mCountdownEventListeners) {
            mCountdownEventListeners.add(listener);
        }
        return true;
    }

    /**
     * Unegister your unregisterOnCountdownEventListener class to service.
     * Remember to unregister the listener before your application on destroy.
     *
     * @param listener unregisterOnCountdownEventListener
     * @return boolean true: unregister success, false: unregister fail
     */
    public boolean unregisterOnCountdownEventListener(OnCountdownEventListener listener) {
        synchronized (mCountdownEventListeners) {
            mCountdownEventListeners.remove(listener);
        }
        return true;
    }

    /**
     * Register registerOnSystemClockEventListener(OnSystemClockEventListener
     * listener), your listener will be triggered when the events posted from
     * native code. Note: Remember to unregister the listener before your
     * application on destroy.
     *
     * @param listener OnSystemClockEventListener
     * @return boolean true: register success, false: register fail
     */
    public boolean registerOnSystemClockEventListener(OnSystemClockEventListener listener) {
        synchronized (mSystemClockEventListeners) {
            mSystemClockEventListeners.add(listener);
        }
        return true;
    }

    /**
     * Unegister your unregisterOnSystemClockEventListener class to service.
     * Remember to unregister the listener before your application on destroy.
     *
     * @param listener unregisterOnSystemClockEventListener
     * @return boolean true: unregister success, false: unregister fail
     */
    public boolean unregisterOnSystemClockEventListener(OnSystemClockEventListener listener) {
        synchronized (mSystemClockEventListeners) {
            mSystemClockEventListeners.remove(listener);
        }
        return true;
    }

    /**
     * Register registerOnSignalEventListener(OnSignalEventListener listener),
     * your listener will be triggered when the events posted from native code.
     * Note: Remember to unregister the listener before your application on
     * destroy.
     *
     * @param listener OnSignalEventListener
     * @return boolean true: register success, false: register fail
     */
    public boolean registerOnSignalEventListener(OnSignalEventListener listener) {
        synchronized (mSignalEventListeners) {
            mSignalEventListeners.add(listener);
        }
        return true;
    }

    /**
     * Unegister your unregisterOnSignalEventListener class to service. Remember
     * to unregister the listener before your application on destroy.
     *
     * @param listener unregisterOnSignalEventListener
     * @return boolean true: unregister success, false: unregister fail
     */
    public boolean unregisterOnSignalEventListener(OnSignalEventListener listener) {
        synchronized (mSignalEventListeners) {
            mSignalEventListeners.remove(listener);
        }
        return true;
    }

    /**
     * Register registerOnEpgTimerEventListener(OnEpgTimerEventListener
     * listener), your listener will be triggered when the events posted from
     * native code. Note: Remember to unregister the listener before your
     * application on destroy.
     *
     * @param listener OnEpgTimerEventListener
     * @return boolean true: register success, false: register fail
     */
    public boolean registerOnEpgTimerEventListener(OnEpgTimerEventListener listener) {
        synchronized (mEpgTimerEventListeners) {
            mEpgTimerEventListeners.add(listener);
        }
        return true;
    }

    /**
     * Unegister your unregisterOnEpgTimerEventListener class to service.
     * Remember to unregister the listener before your application on destroy.
     *
     * @param listener unregisterOnEpgTimerEventListener
     * @return boolean true: unregister success, false: unregister fail
     */
    public boolean unregisterOnEpgTimerEventListener(OnEpgTimerEventListener listener) {
        synchronized (mEpgTimerEventListeners) {
            mEpgTimerEventListeners.remove(listener);
        }
        return true;
    }

    /**
     * Register registerOnPvrTimerEventListener(OnPvrTimerEventListener
     * listener), your listener will be triggered when the events posted from
     * native code. Note: Remember to unregister the listener before your
     * application on destroy.
     *
     * @param listener OnPvrTimerEventListener
     * @return boolean true: register success, false: register fail
     */
    public boolean registerOnPvrTimerEventListener(OnPvrTimerEventListener listener) {
        synchronized (mPvrTimerEventListeners) {
            mPvrTimerEventListeners.add(listener);
        }
        return true;
    }

    /**
     * Unegister your unregisterOnPvrTimerEventListener class to service.
     * Remember to unregister the listener before your application on destroy.
     *
     * @param listener unregisterOnPvrTimerEventListener
     * @return boolean true: unregister success, false: unregister fail
     */
    public boolean unregisterOnPvrTimerEventListener(OnPvrTimerEventListener listener) {
        synchronized (mPvrTimerEventListeners) {
            mPvrTimerEventListeners.remove(listener);
        }
        return true;
    }

    /**
     * Register registerOnOadTimerEventListener(OnOadTimerEventListener
     * listener), your listener will be triggered when the events posted from
     * native code. Note: Remember to unregister the listener before your
     * application on destroy.
     *
     * @param listener OnOadTimerEventListener
     * @return boolean true: register success, false: register fail
     */
    public boolean registerOnOadTimerEventListener(OnOadTimerEventListener listener) {
        synchronized (mOadTimerEventListeners) {
            mOadTimerEventListeners.add(listener);
        }
        return true;
    }

    /**
     * Unegister your unregisterOnOadTimerEventListener class to service.
     * Remember to unregister the listener before your application on destroy.
     *
     * @param listener unregisterOnOadTimerEventListener
     * @return boolean true: unregister success, false: unregister fail
     */
    public boolean unregisterOnOadTimerEventListener(OnOadTimerEventListener listener) {
        synchronized (mOadTimerEventListeners) {
            mOadTimerEventListeners.remove(listener);
        }
        return true;
    }

    protected void finalize() throws Throwable {
        try {
            mService.removeClient(this);
        } catch (RemoteException e) {
            e.printStackTrace();
        }
    }
}
