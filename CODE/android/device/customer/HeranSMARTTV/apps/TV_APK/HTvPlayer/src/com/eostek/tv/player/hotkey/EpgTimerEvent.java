
package com.eostek.tv.player.hotkey;

import java.io.Serializable;

public class EpgTimerEvent implements Serializable {
    private String channelNum;

    private String programName;

    private String serviceType;

    private int eventTimerType;

    private long startTime;

    private long endTime;

    private int eventId;

    public String getChannelNum() {
        return channelNum;
    }

    public void setChannelNum(String channelNum) {
        this.channelNum = channelNum;
    }

    public String getProgramName() {
        return programName;
    }

    public void setProgramName(String programName) {
        this.programName = programName;
    }

    public String getServiceType() {
        return serviceType;
    }

    public void setServiceType(String serviceType) {
        this.serviceType = serviceType;
    }

    public int getEventTimerType() {
        return eventTimerType;
    }

    public void setEventTimerType(int eventTimerType) {
        this.eventTimerType = eventTimerType;
    }

    public long getStartTime() {
        return startTime;
    }

    public void setStartTime(long dateTime) {
        this.startTime = dateTime;
    }

    public long getEndTime() {
        return endTime;
    }

    public void setEndTime(long endTime) {
        this.endTime = endTime;
    }

    public int getEventId() {
        return eventId;
    }

    public void setEventId(int eventId) {
        this.eventId = eventId;
    }
}
