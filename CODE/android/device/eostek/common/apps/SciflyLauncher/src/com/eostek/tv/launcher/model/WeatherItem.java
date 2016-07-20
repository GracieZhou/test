
package com.eostek.tv.launcher.model;

/**
 * projectName：WasuWidgetHost. moduleName： WeatherItem.java
 */
public class WeatherItem {

    /** day. **/
    private int mDay;

    /** city. */
    private String mCity;

    /** weather in the daytime. */
    private String mStatus1;

    /** weather in the night. */
    private String mStatus2;

    /** wind direction in the daytime. */
    private String mDirection1;

    /** wind direction in the night. */
    private String mDirection2;

    /** wind level in the daytime. */
    private String mPower1;

    /** wind level in the night. */
    private String mPower2;

    /** temperature in the daytime. */
    private String mTemperature1;

    /** temperature in the night. */
    private String mTemperature2;

    /** body sensitivity in the daytime. */
    private String mTgd1;

    /** body sensitivity in the night. */
    private String mTgd2;

    /** ultraviolet description. */
    private String mZwxl;

    /** dress description. */
    private String mChyl;

    /** pollution description. */
    private String mPollutionl;

    /** sport description. */
    private String mYdl;

    /** date. */
    private String mSavedateWeather;

    public int getDay() {
        return mDay;
    }

    public void setDay(int day) {
        this.mDay = day;
    }

    public final String getCity() {
        return mCity;
    }

    public final void setCity(String city) {
        this.mCity = city;
    }

    public final String getStatus1() {
        return mStatus1;
    }

    public final void setStatus1(String status1) {
        this.mStatus1 = status1;
    }

    public final String getStatus2() {
        return mStatus2;
    }

    public final void setStatus2(String status2) {
        this.mStatus2 = status2;
    }

    public final String getDirection1() {
        return mDirection1;
    }

    public final void setDirection1(String direction1) {
        this.mDirection1 = direction1;
    }

    public final String getDirection2() {
        return mDirection2;
    }

    public final void setDirection2(String direction2) {
        this.mDirection2 = direction2;
    }

    public final String getPower1() {
        return mPower1;
    }

    public final void setPower1(String power1) {
        this.mPower1 = power1;
    }

    public final String getPower2() {
        return mPower2;
    }

    public final void setPower2(String power2) {
        this.mPower2 = power2;
    }

    public final String getTemperature1() {
        return mTemperature1;
    }

    public final void setTemperature1(String temperature1) {
        this.mTemperature1 = temperature1;
    }

    public final String getTemperature2() {
        return mTemperature2;
    }

    public final void setTemperature2(String temperature2) {
        this.mTemperature2 = temperature2;
    }

    public final String getTgd1() {
        return mTgd1;
    }

    public final void setTgd1(String tgd1) {
        this.mTgd1 = tgd1;
    }

    public final String getTgd2() {
        return mTgd2;
    }

    public final void setTgd2(String tgd2) {
        this.mTgd2 = tgd2;
    }

    public final String getZwxl() {
        return mZwxl;
    }

    public final void setZwxl(String zWxl) {
        this.mZwxl = zWxl;
    }

    public String getChyl() {
        return mChyl;
    }

    public final void setChyl(String chyl) {
        this.mChyl = chyl;
    }

    public final String getPollutionl() {
        return mPollutionl;
    }

    public final void setPollutionl(String pollutionl) {
        this.mPollutionl = pollutionl;
    }

    public final String getYdl() {
        return mYdl;
    }

    public final void setYdl(String ydl) {
        this.mYdl = ydl;
    }

    public final String getSavedateWeather() {
        return mSavedateWeather;
    }

    public final void setSavedateWeather(String savedateWeather) {
        this.mSavedateWeather = savedateWeather;
    }

    @Override
    public String toString() {
        return mCity + "," + mStatus1 + "," + mStatus2 + "," + mTemperature2 + "~" + mTemperature1;
    }

}
