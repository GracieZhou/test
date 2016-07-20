
package com.eostek.isynergy.setmeup.model;

public class TimeZoneModel {

    private String mTimeZoneId;

    private String mTimeZoneName;

    private String mGMT;

    public String getGmt() {
        return mGMT;
    }

    public void setGmt(String gmt) {
        this.mGMT = gmt;
    }

    public String getTimeZoneName() {
        return mTimeZoneName;
    }

    public void setTimeZoneName(String timeZoneName) {
        this.mTimeZoneName = timeZoneName;
    }

    public String getTimeZoneId() {
        return mTimeZoneId;
    }

    public void setTimeZoneId(String timeZoneId) {
        this.mTimeZoneId = timeZoneId;
    }

}
