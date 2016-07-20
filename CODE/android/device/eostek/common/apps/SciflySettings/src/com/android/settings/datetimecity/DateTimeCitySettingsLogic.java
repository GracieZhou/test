
package com.android.settings.datetimecity;

import java.net.URL;
import java.net.URLConnection;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import com.android.settings.R;
import com.android.settings.SettingsApplication;
import android.annotation.SuppressLint;
import android.net.SntpClient;
import android.os.SystemClock;
import android.util.Log;

/**
 * main function:correct time by net and show time according to the time formate
 */
public class DateTimeCitySettingsLogic {

    private static final String TAG = "DateTimeCitySettings";

    private DateTimeCitySettingsActivity mActivity;

    private final int TIMESYNC_TIMEOUT = 20000;

    public Calendar mDummyDate;

    private String mNtpServer;

    public void setmNtpServer(String mNtpServer) {
        this.mNtpServer = mNtpServer;
    }

    private final static String NTP_CHINA = "1.cn.pool.ntp.org";

    private final static String NTP_TAIWAN = "1.tw.pool.ntp.org";

    private final static String NTP_AMERICAN = "time.nist.gov";

    private class TimeSyncThread implements Runnable {
        @Override
        public void run() {
            autoRefreshTime(mNtpServer, TIMESYNC_TIMEOUT);
        }
    }

    /**
     * get ntpserver by selected.
     * 
     * @return
     */
    public String getmNtpServer() {
        int ntp = mActivity.getApplicationContext().getSharedPreferences("settings", 0).getInt("ntp_server_id", 0);
        switch (ntp) {
            case 0:
                mNtpServer = NTP_CHINA;
                break;
            case 1:
                mNtpServer = NTP_TAIWAN;
                break;
            case 2:
                mNtpServer = NTP_AMERICAN;
                break;
        }
        return mNtpServer;
    }

    /**
     * open thread
     */
    public void startTimeSync() {
        TimeSyncThread runnable = new TimeSyncThread();
        SettingsApplication.execute(runnable);
    }

    public DateTimeCitySettingsLogic(DateTimeCitySettingsActivity dateTimeCitySettingsActivity) {
        this.mActivity = dateTimeCitySettingsActivity;
        // init the mNtpServer:
    }

    /**
     * get the timeformat of current system.
     * 
     * @return
     */
    public boolean getis24Hour() {
        return android.text.format.DateFormat.is24HourFormat(mActivity);
    }

    /**
     * @return the current time in the current time format
     */
    @SuppressLint("SimpleDateFormat")
    public String getSystemTime() {
        SimpleDateFormat mFormat = new SimpleDateFormat(mActivity.getString(R.string.date_formate));
        String date = mFormat.format(new Date());
        // turn the 24hours to 12hours
        if (!getis24Hour()) {
            int index = date.lastIndexOf(' ') + 1;
            String cut = date.substring(index, index + 2);
            if (Integer.parseInt(cut) >= 12) {
                date = date.replace("" + cut + ":", (Integer.parseInt(cut) - 12) + ":") + " "
                        + mActivity.getString(R.string.afternoon);
            } else {
                date = date + " " + mActivity.getString(R.string.morning);
            }
        }
        return date;
    }

    /**
     * if correct time success,it will set the time get from net to system
     * 
     * @param mNtpServer
     * @param timeout
     * @return the boolean of correct time
     */
    synchronized public boolean autoRefreshTime(String mNtpServer, final int timeout) {
        Log.d(TAG, "<<<autoRefreshTime<<<mNtpServer<<<<<<<<" + mNtpServer);
        if (mNtpServer == null) {
            mNtpServer = NTP_CHINA;
        }
        Log.d(TAG, ">>>autoRefreshTime>>>mNtpServer>>>>>>" + mNtpServer);
        SntpClient client = new SntpClient();
        if (client.requestTime(mNtpServer, timeout)) {
            long mCachedNtpTime = client.getNtpTime();
            long mCachedNtpElapsedRealtime = client.getNtpTimeReference();
            long systemCurrentTime = System.currentTimeMillis();
            long ntpTime = mCachedNtpTime + SystemClock.elapsedRealtime() - mCachedNtpElapsedRealtime;
            if (Math.abs(ntpTime - systemCurrentTime) > 5 * 1000) {
                SystemClock.setCurrentTimeMillis(ntpTime);
            }
            mActivity.mHandler.sendEmptyMessage(0);
            return true;
        } else {

            URLConnection urlConnection = null;
            try {
                final URL url = new URL("http://" + mNtpServer);
                urlConnection = url.openConnection();
                urlConnection.setConnectTimeout(5000);
                urlConnection.connect();
            } catch (Exception e) {
                mActivity.mHandler.sendEmptyMessage(1);
                return false;
            }
            final long timestamp = urlConnection.getDate();
            final long systemCurrentTime = System.currentTimeMillis();
            if (Math.abs(timestamp - systemCurrentTime) > 5 * 1000) {
                // Make sure we don't overflow, since it's going to be converted
                // to an int
                if (timestamp / 1000 < Integer.MAX_VALUE) {
                    SystemClock.setCurrentTimeMillis(timestamp);
                    mActivity.mHandler.sendEmptyMessage(0);
                    return true;
                }
            }
            mActivity.mHandler.sendEmptyMessage(0);
            return true;
        }
    }

}
