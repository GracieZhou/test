package com.eostek.wasuwidgethost.util;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.annotation.SuppressLint;
import android.content.Context;
import android.text.format.Time;

import com.eostek.scifly.widget.R;

/**
 * projectName： WasuWidgetHost.
 * moduleName： WeatherUtil.java
 *
 * @author vicky.wang
 * @version 1.0.0
 * @time  2014-8-14 4:30 pm
 * @Copyright © 2014 Eos Inc.
 */
public final class WeatherUtil {

    private WeatherUtil() {
        super();
        // TODO Auto-generated constructor stub
    }

    private static final int DAYTIME_BEGIN_HOUR = 8;

    private static final int DAYTIME_END_HOUR = 20;

    private static final String LOADPIC_URL = 
            "http://php.weather.sina.com.cn/images/yb3/180_180/%s_0.png";

    /**
     * 
     * @return true if time.hour >= DAYTIME_BEGIN_HOUR && time.hour <= DAYTIME_END_HOUR, false otherwise.
     */
    public static boolean isDaytime() {
        Time time = new Time();
        time.setToNow();
        return (time.hour >= DAYTIME_BEGIN_HOUR && time.hour <= DAYTIME_END_HOUR);
    }

    /**
     * transfer the current day into the day of week.
     * @param c
     * @param day
     * @return string
     */
     public static String getDayofWeek(Context c, int day) {
        String week = null;
        switch (day) {
        case 1:
            week = c.getString(R.string.sunday);
            break;
        case 2:
            week = c.getString(R.string.monday);
            break;
        case 3:
            week = c.getString(R.string.tuesday);
            break;
        case 4:
            week = c.getString(R.string.wednesday);
            break;
        case 5:
            week = c.getString(R.string.thursday);
            break;
        case 6:
            week = c.getString(R.string.friday);
            break;
        case 7:
            week = c.getString(R.string.saturday);
            break;
        default:
            break;
        }

        return week;
    }

    /**
     * get the date.
     * @param formate
     * @return String date.
     */
    @SuppressLint("SimpleDateFormat")
    public static String getDate(String formate) {
        SimpleDateFormat dateFormate = new SimpleDateFormat(formate);
        return dateFormate.format(new Date(System.currentTimeMillis()));
    }
}
