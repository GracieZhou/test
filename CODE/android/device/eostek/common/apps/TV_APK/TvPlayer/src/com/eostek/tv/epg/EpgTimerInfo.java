
package com.eostek.tv.epg;

import java.io.Serializable;

public class EpgTimerInfo implements Serializable {

    /**
     * 
     */
    private static final long serialVersionUID = 1L;

    private String mChannelNum;

    private String mProgramName;

    private String mServiceType;

    private int mEventTimerType;

    private long mStartTime;

    private long mEndTime;

    private int mEventId;

    public String getChannelNum() {
        return mChannelNum;
    }

    public void setChannelNum(String channelNum) {
        this.mChannelNum = channelNum;
    }

    public String getProgramName() {
        return mProgramName;
    }

    public void setProgramName(String programName) {
        this.mProgramName = programName;
    }

    public String getServiceType() {
        return mServiceType;
    }

    public void setServiceType(String serviceType) {
        this.mServiceType = serviceType;
    }

    public int getEventTimerType() {
        return mEventTimerType;
    }

    public void setEventTimerType(int eventTimerType) {
        this.mEventTimerType = eventTimerType;
    }

    public long getStartTime() {
        return mStartTime;
    }

    public void setStartTime(long dateTime) {
        this.mStartTime = dateTime;
    }

    public long getEndTime() {
        return mEndTime;
    }

    public void setEndTime(long endTime) {
        this.mEndTime = endTime;
    }

    public int getEventId() {
        return mEventId;
    }

    public void setEventId(int eventId) {
        this.mEventId = eventId;
    }
}
