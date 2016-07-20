
package com.google.tv.eoslauncher.util;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.text.format.Time;
import android.util.Log;

import com.google.tv.eoslauncher.R;
import com.mstar.android.tv.TvChannelManager;
import com.mstar.android.tv.TvPictureManager;
import com.mstar.android.tvapi.common.TvManager;
import com.mstar.android.tvapi.common.exception.TvCommonException;
import com.mstar.android.tvapi.common.vo.EnumDtvSoundMode;
import com.mstar.android.tvapi.common.vo.EnumProgramAttribute;
import com.mstar.android.tvapi.common.vo.EnumProgramCountType;
import com.mstar.android.tvapi.common.vo.EnumProgramInfoType;
import com.mstar.android.tvapi.common.vo.EnumVideoArcType;
import com.mstar.android.tvapi.common.vo.PresentFollowingEventInfo;
import com.mstar.android.tvapi.common.vo.ProgramInfo;
import com.mstar.android.tvapi.common.vo.ProgramInfoQueryCriteria;
import com.mstar.android.tvapi.common.vo.TvOsType.EnumInputSource;
import com.mstar.android.tvapi.dtv.common.DtvManager;
import com.mstar.android.tvapi.dtv.vo.DtvType.EnumEpgDescriptionType;
import com.mstar.android.tvapi.dtv.vo.EpgEventInfo;

/**
 * @projectName： EosTvPlayer
 * @moduleName： ChannelManagerExt.java
 * @author jachensy.chen
 * @version 1.0.0
 * @time 2013-12-27
 * @Copyright © 2013 EOSTEK, Inc.
 */
public class ChannelManagerExt {
    private static final String TAG = "ChannelManagerExt";

    private static ChannelManagerExt mExTvChannelManager;

    private List<ProgramInfo> mChannelsAll = new ArrayList<ProgramInfo>();

    /**
     * The list doesn't include the channels which is skiped.
     */
    private List<ProgramInfo> mChannels = new ArrayList<ProgramInfo>();

    private List<ProgramInfo> mFavoriteChannels = new ArrayList<ProgramInfo>();

    /**
     * For digital select program.
     */
    private List<Integer> mChannelNums = new ArrayList<Integer>();

    /**
     * To record the last channel position.
     */
    private int mLastPosition = 0;

    /**
     * To record current channel position.
     */
    private int mCurPosition = 0;

    private ProgramInfo mCurInfo = null;

    private static final int CHANNELCHANGE = 0x05;

    private static final int DELAYCHANGECHANNEL = 400;

    private EnumInputSource curSource = EnumInputSource.E_INPUT_SOURCE_NONE;

    private Handler mChannelHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (CHANNELCHANGE == msg.what) {
                programSel(msg.arg1, msg.arg2);
            }
        }

    };

    public static ChannelManagerExt getInstance() {
        if (mExTvChannelManager == null) {
            mExTvChannelManager = new ChannelManagerExt();
        }
        return mExTvChannelManager;
    }

    /**
     * get current program information.
     * 
     * @return
     */
    public ProgramInfo getCurProgramInfo() {
        ProgramInfoQueryCriteria qc = new ProgramInfoQueryCriteria();
        ProgramInfo mCurInfo = TvChannelManager.getInstance().getProgramInfo(qc, EnumProgramInfoType.E_INFO_CURRENT);
        return mCurInfo;
    }

    /**
     * get program information by index in database.
     * 
     * @param programIndex
     * @return
     */
    public ProgramInfo getProgramInfoByIndex(int programIndex) {
        ProgramInfoQueryCriteria qc = new ProgramInfoQueryCriteria();
        qc.queryIndex = programIndex;
        ProgramInfo pi = TvChannelManager.getInstance().getProgramInfo(qc, EnumProgramInfoType.E_INFO_DATABASE_INDEX);
        return pi;
    }

    /**
     * select program.
     * 
     * @param u32Number
     * @param u8ServiceType
     * @return
     */
    public boolean programSel(int number, int serviceType) {
        if (curSource == EnumInputSource.E_INPUT_SOURCE_ATV) {
            number = number - 1;
        }
        Log.e(TAG, "channel change(down/up). channelNumber:" + number + ", serviceType:" + serviceType);
        try {
            if (TvManager.getInstance() != null) {
                TvManager.getInstance().getChannelManager().selectProgram(number, (short) serviceType, 0x00);
            }
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * 此方法需要配合getCurProgramInfo这个方法使用，一般是在tv画面切换时，首先要进行切台等动作显示TV画面。
     * 
     * @param number
     * @param serviceType
     * @return
     */
    public boolean programSelect(int number, int serviceType) {
        Log.e(TAG, "channel change(down/up). channelNumber:" + number + ", serviceType:" + serviceType);
        try {
            if (TvManager.getInstance() != null) {
                TvManager.getInstance().getChannelManager().selectProgram(number, (short) serviceType, 0x00);
            }
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * select program by position in list.
     * 
     * @param position
     */
    public void programSel(int position) {
        if (position >= mChannels.size()) {
            return;
        }
        mLastPosition = mCurPosition;
        mCurPosition = position;

        mCurInfo = mChannels.get(mCurPosition);
        programSel(mCurInfo.number, mCurInfo.serviceType);
    }

    /**
     * get all channels to init the lists.
     * 
     * @param context
     * @param inputSource
     */
    public void getAllChannels(Context context, EnumInputSource inputSource) {
        Log.e(TAG, "current source is " + inputSource + ", get all channels.");
        curSource = inputSource;
        mLastPosition = 0;
        mCurPosition = 0;
        mCurInfo = getCurProgramInfo();
        mChannelsAll.clear();
        mChannelNums.clear();
        mFavoriteChannels.clear();
        mChannels.clear();
        // DTV channels count.
        int indexBase = 0;
        int channelconunt = 0;
        switch (inputSource) {
            case E_INPUT_SOURCE_ATV: {
                mCurInfo.number = mCurInfo.number + 1;
                channelconunt = TvChannelManager.getInstance().getProgramCount(EnumProgramCountType.E_COUNT_ATV_DTV);
                indexBase = TvChannelManager.getInstance().getProgramCount(EnumProgramCountType.E_COUNT_DTV);
                if (indexBase == 0xFFFFFFFF) {
                    indexBase = 0;
                }
                Log.v(TAG, "ATV total count =" + (channelconunt - indexBase));
                for (int i = indexBase; i < channelconunt; i++) {
                    ProgramInfo pi = null;
                    pi = getProgramInfoByIndex(i);
                    if (pi.number > 999 || pi.number < 0) {
                        pi.number = 1;
                    } else {
                        pi.number = pi.number + 1;
                    }
                    mChannelNums.add(pi.number);
                    mChannelsAll.add(pi);
                    mChannels.add(pi);
                    if (mCurInfo != null && pi.number == mCurInfo.number) {
                        mCurPosition = i - indexBase;
                        mLastPosition = mCurPosition;
                    }
                }
                break;
            }
            case E_INPUT_SOURCE_DTV: {
                int dataCount = TvChannelManager.getInstance().getProgramCount(EnumProgramCountType.E_COUNT_DTV_DATA);
                channelconunt = TvChannelManager.getInstance().getProgramCount(EnumProgramCountType.E_COUNT_DTV)
                        - dataCount;
                Log.v(TAG, "DTV total count is " + channelconunt);
                for (int i = indexBase; i < channelconunt; i++) {
                    ProgramInfo pi = null;
                    pi = getProgramInfoByIndex(i);
                    mChannelNums.add(pi.number);
                    mChannelsAll.add(pi);
                    if (pi.favorite == 1 && !pi.isSkip) {
                        mFavoriteChannels.add(pi);
                    }
                    mChannels.add(pi);
                    if (mCurInfo != null && pi.number == mCurInfo.number) {
                        mCurPosition = i - (mChannelsAll.size() - mChannels.size());
                        mLastPosition = mCurPosition;
                    }
                }
                break;
            }
            default:
                break;
        }
        if (mChannels.size() == 0) {
            UIUtil.toastShow(R.string.noprogram_tip, context);
        }
    }

    /**
     * channel up.
     */
    public void channelUp() {
        Log.e(TAG, "mCurPosition is " + mCurPosition + ". up, available Channels size is " + mChannels.size());
        if (mChannels.size() <= 0) {
            return;
        }
        int temp = mCurPosition;
        if (mChannels.size() > 0 && mCurPosition == mChannels.size() - 1) {
            mLastPosition = mCurPosition;
            mCurPosition = 0;
        } else {
            mLastPosition = mCurPosition;
            mCurPosition++;
        }
        if (mCurPosition >= mChannels.size()) {
            return;
        }
        while (mChannels.get(mCurPosition).isSkip && mCurPosition != temp) {
            if (mCurPosition == mChannels.size() - 1) {
                mCurPosition = 0;
            } else {
                mCurPosition++;
            }
        }
        sendMessage();
    }

    /**
     * channel down.
     */
    public void channelDown() {
        Log.e(TAG, "mCurPosition is " + mCurPosition + ". down, available Channels size is " + mChannels.size());
        if (mChannels.size() <= 0) {
            return;
        }
        int temp = mCurPosition;
        if (mChannels.size() > 0 && mCurPosition == 0) {
            mLastPosition = mCurPosition;
            mCurPosition = mChannels.size() - 1;
        } else {
            mLastPosition = mCurPosition;
            mCurPosition--;
        }
        if (mCurPosition >= mChannels.size()) {
            return;
        }
        while (mChannels.get(mCurPosition).isSkip && mCurPosition != temp) {
            if (mCurPosition == 0) {
                mCurPosition = mChannels.size() - 1;
            } else {
                mCurPosition--;
            }
        }
        sendMessage();
    }

    /**
     * channel return.
     */
    public void channelReturn() {
        if (mChannels.size() <= 0) {
            return;
        }
        if (mLastPosition != mCurPosition) {
            int temp = 0;
            temp = mCurPosition;
            mCurPosition = mLastPosition;
            mLastPosition = temp;
            if (mCurPosition >= mChannels.size()) {
                return;
            }
            sendMessage();
        }
    }

    public void sendMessage() {
        mCurInfo = mChannels.get(mCurPosition);
        mChannelHandler.removeMessages(CHANNELCHANGE);
        Message msg = mChannelHandler.obtainMessage();
        msg.what = CHANNELCHANGE;
        msg.arg1 = mCurInfo.number;
        msg.arg2 = mCurInfo.serviceType;
        mChannelHandler.sendMessageDelayed(msg, DELAYCHANGECHANNEL);
    }

    /**
     * reset index for channel changes.
     * 
     * @param channelNum
     */
    public void resetIndex(int channelNum) {
        if (channelNum != -1 && getCurInfo() != null && channelNum != getCurInfo().number) {
            int index = mChannelNums.indexOf(channelNum);
            if (index != -1) {
                mLastPosition = mCurPosition;
                mCurPosition = index;
                mCurInfo = mChannels.get(mCurPosition);
            }
        }
    }

    public int getLastPosition() {
        return mLastPosition;
    }

    public void setLastPosition(int lastPosition) {
        this.mLastPosition = lastPosition;
    }

    public int getCurPosition() {
        return mCurPosition;
    }

    public void setCurPosition(int curPosition) {
        this.mCurPosition = curPosition;
    }

    public ProgramInfo getCurInfo() {
        if (curSource == EnumInputSource.E_INPUT_SOURCE_DTV && mChannels.size() <= 0) {
            return null;
        }
        return mCurInfo;
    }

    public void setCurInfo(ProgramInfo curInfo) {
        this.mCurInfo = curInfo;
    }

    /**
     * The list doesn't include the channels which is skiped.
     * 
     * @return
     */
    public List<ProgramInfo> getChannels() {
        return mChannels;
    }

    /**
     * For digital select program.
     * 
     * @return
     */
    public List<Integer> getChannelNums() {
        return mChannelNums;
    }

    /**
     * The list include all channels that get from database.
     * 
     * @return
     */
    public List<ProgramInfo> getChannelsAll() {
        return mChannelsAll;
    }

    /**
     * This list is the favorite after channel edit.
     * 
     * @return
     */
    public List<ProgramInfo> getFavoriteChannels() {
        return mFavoriteChannels;
    }

    public int getTrack() {
        int trackIndex = 0;
        try {
            EnumDtvSoundMode enDtvSoundMode = TvManager.getInstance().getAudioManager().getDtvOutputMode();

            if (enDtvSoundMode == EnumDtvSoundMode.E_STEREO) {
                trackIndex = 0;
            } else if (enDtvSoundMode == EnumDtvSoundMode.E_LEFT) {
                trackIndex = 1;
            } else if (enDtvSoundMode == EnumDtvSoundMode.E_RIGHT) {
                trackIndex = 2;
            } else if (enDtvSoundMode == EnumDtvSoundMode.E_MIXED) {
                trackIndex = 3;
            }

        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        return trackIndex;
    }

    /**
     * get current current epg event information.
     * 
     * @param programInfo
     * @return
     */
    public EpgEventInfo getCurEpgEventInfo(ProgramInfo programInfo) {
        Time time = new Time();
        time.setToNow();
        time.set(time.toMillis(true));
        EpgEventInfo info = new EpgEventInfo();
        try {
            info = DtvManager.getEpgManager().getEventInfoByTime(programInfo.serviceType, programInfo.number, time);
        } catch (TvCommonException e) {
            if (e != null && e.getMessage() != null) {
                Log.e(TAG, e.getMessage());
            }
        }
        return info;
    }

    /**
     * Get next event information after current event. It shows on information
     * menu.
     * 
     * @param type
     * @param programId
     * @param flag
     * @return
     */
    public PresentFollowingEventInfo getPresentFollowingEventInfo(short type, int programId, boolean flag) {
        PresentFollowingEventInfo info = null;
        try {
            info = DtvManager.getEpgManager().getPresentFollowingEventInfo(type, programId, flag,
                    EnumEpgDescriptionType.E_DETAIL_DESCRIPTION);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        return info;
    }

    /**
     * Get the offset time.
     * 
     * @return
     */
    public long getEpgEventOffsetTime() {
        Time time = new Time();
        time.setToNow();
        time.set(time.toMillis(true));
        try {
            return DtvManager.getEpgManager().getEpgEventOffsetTime(time, true) * 1000;
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        return 0;
    }

    /**
     * Get the event list for currnt channel.
     * 
     * @param serviceType
     * @param number
     * @param time
     * @param count
     * @return
     */
    public List<EpgEventInfo> getEventInfo(short serviceType, int number, Time time, int count) {
        try {
            return DtvManager.getEpgManager().getEventInfo(serviceType, number, time, count);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        return null;
    }

    /**
     * To execute the timer for epg reminder.
     */
    public void execEpgTimerAction() {
        try {
            TvManager.getInstance().getTimerManager().execEpgTimerAction();
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
    }

    /**
     * set the channel's information for channel edit.
     * 
     * @param enpa
     * @param programNo
     * @param programType
     * @param programId
     * @param bv
     */
    public void setProgramAttribute(EnumProgramAttribute enpa, int programNo, int programType, int programId, boolean bv) {
        Log.d(TAG, "setProgramAttribute, paras enpa is " + enpa + " programNo is " + programNo + " programType is "
                + programType + " programId is " + programId + " value " + bv);
        try {
            TvManager.getInstance().getChannelManager()
                    .setProgramAttribute(enpa, programNo, (short) programType, programId, bv);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
    }

    /**
     * Unlock channel if the channel has been locked.
     * 
     * @return
     */
    public boolean unlockChannel() {
        try {
            return DtvManager.getDvbPlayerManager().unlockChannel();
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        return false;
    }

    /**
     * Move channel.
     * 
     * @param sourcePosition
     * @param targetPosition
     */
    public void move(int sourcePosition, int targetPosition) {
        try {
            TvManager.getInstance().getChannelManager().moveProgram(sourcePosition, targetPosition);
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
    }

    /**
     * Get Current input source. If current source is storage, we can get the tv
     * source(un storage) from database by this function.
     * 
     * @param context
     * @return
     */
    public int queryCurInputSrc(Context context) {
        int value = 0;
        Cursor cursor = context.getContentResolver().query(Uri.parse("content://mstar.tv.usersetting/systemsetting"),
                null, null, null, null);
        if (cursor != null && cursor.moveToFirst()) {
            value = cursor.getInt(cursor.getColumnIndex("enInputSourceType"));
        }
        if (cursor != null) {
            cursor.close();
        }
        return value;
    }

    public void setNosignalTips(Context context, EnumInputSource source, SignalTipView mSignalTipView) {
        switch (source) {
            case E_INPUT_SOURCE_ATV:
                mSignalTipView.setText("ATV " + context.getResources().getString(R.string.nosignaltips));
                break;
            case E_INPUT_SOURCE_DTV:
                mSignalTipView.setText("DTV " + context.getResources().getString(R.string.nosignaltips));
                break;
            case E_INPUT_SOURCE_HDMI:
                mSignalTipView.setText("HDMI " + context.getResources().getString(R.string.nosignaltips));
                break;
            case E_INPUT_SOURCE_HDMI4:
                mSignalTipView.setText("HDMI2 " + context.getResources().getString(R.string.nosignaltips));
                break;
            case E_INPUT_SOURCE_CVBS:
                mSignalTipView.setText("AV " + context.getResources().getString(R.string.nosignaltips));
                break;
            case E_INPUT_SOURCE_YPBPR:
                mSignalTipView.setText("YPBPR " + context.getResources().getString(R.string.nosignaltips));
                break;
            case E_INPUT_SOURCE_VGA:
                mSignalTipView.setText("VGA " + context.getResources().getString(R.string.nosignaltips));
                break;
            default:
                mSignalTipView.setText(context.getResources().getString(R.string.nosignaltips));
                break;
        }
    }

    /**
     * to cut off the TV window for Heran taiwan.
     * 
     * @param curSourceType
     * @param context
     */
    public void setTVWindow(EnumInputSource curSourceType, Activity context) {
        EnumVideoArcType zoomMode = TvPictureManager.getInstance().getVideoArc();
        FactoryDeskImpl impl = FactoryDeskImpl.getInstance(context);
        switch (zoomMode) {
            case E_16x9:
            case E_4x3:
            case E_AUTO:
            case E_Panorama: {
                switch (curSourceType) {
                    case E_INPUT_SOURCE_ATV:
                    case E_INPUT_SOURCE_CVBS:
                    case E_INPUT_SOURCE_YPBPR:
                        impl.setOverScan((short) 40, (short) 40, (short) 40, (short) 38);
                        break;
                    case E_INPUT_SOURCE_DTV:
                    case E_INPUT_SOURCE_HDMI:
                    case E_INPUT_SOURCE_HDMI4:
                        impl.setOverScan((short) 20, (short) 20, (short) 20, (short) 20);
                        break;
                    default:
                        impl.setOverScan((short) 0, (short) 0, (short) 0, (short) 0);
                        break;
                }
                break;
            }
            case E_Zoom1: {
                switch (curSourceType) {
                    case E_INPUT_SOURCE_ATV:
                    case E_INPUT_SOURCE_CVBS:
                    case E_INPUT_SOURCE_YPBPR:
                    case E_INPUT_SOURCE_DTV:
                    case E_INPUT_SOURCE_HDMI:
                    case E_INPUT_SOURCE_HDMI4:
                        impl.setOverScan((short) 26, (short) 22, (short) 26, (short) 26);
                        break;
                    default:
                        impl.setOverScan((short) 0, (short) 0, (short) 0, (short) 0);
                        break;
                }
                break;
            }
            case E_Zoom2: {
                switch (curSourceType) {
                    case E_INPUT_SOURCE_ATV:
                    case E_INPUT_SOURCE_CVBS:
                    case E_INPUT_SOURCE_YPBPR:
                        impl.setOverScan((short) 40, (short) 0, (short) 40, (short) 38);
                        break;
                    case E_INPUT_SOURCE_DTV:
                    case E_INPUT_SOURCE_HDMI:
                    case E_INPUT_SOURCE_HDMI4:
                        impl.setOverScan((short) 20, (short) 0, (short) 20, (short) 20);
                        break;
                    default:
                        impl.setOverScan((short) 0, (short) 0, (short) 0, (short) 0);
                        break;
                }
                break;
            }
            case E_AR_DotByDot: {
                impl.setOverScan((short) 0, (short) 0, (short) 0, (short) 0);
                break;
            }
            default:
                break;
        }
    }
}
