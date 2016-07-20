
package com.eostek.wasuwidgethost;

import java.text.SimpleDateFormat;
import java.util.Date;

import android.app.PendingIntent;
import android.appwidget.AppWidgetManager;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.widget.RemoteViews;

import com.eostek.scifly.widget.R;
import com.eostek.wasuwidgethost.business.WeatherHelper;
import com.eostek.wasuwidgethost.model.WeatherItem;
import com.eostek.wasuwidgethost.util.SettingsObserver;
import com.eostek.wasuwidgethost.util.Utils;

/**
 * projectName：LLauncher.
 * moduleName： WeatherWidgetProvider.java
 *
 * @author vicky.wang
 * @version 1.0.0
 * @time  2014-8-14 4:30 pm
 * @Copyright © 2014 Eos Inc.
 */

public class WeatherWidgetProvider extends BaseAppWidgetProvider {

    private static final String TAG = "WeatherWidgetProvider";

    private static final String NETWORK_OK_ACTION = "com.eostek.network_ok";

    private static final String CLICK_NAME_ACTION = "com.eostek.wasuwidgethost.WeatherWidgetProvider";

    private static int[] widgetIds = null;

    private static RemoteViews mRemoteViews = null;

    private static final int MSG_UPDATE = 100;

    private static final int MSG_CITY_CHANGE = 101;

    private static final int MSG_UPDATE_PER_HOUR = 102;

    private static final int REFRESH_HOUR = 2 * 60 * 60 * 1000;

    private static Context mContext;

    private static volatile WeatherItem weatherItem;

    private static WeatherHelper weatherHelper;

    private SettingsObserver observer = null;

    // the lock to synchronized get network data
    private final Object lock = new Object();

    private SharedPreferences mSharedPreferences;

    private Handler mHandler = new Handler() {

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case MSG_UPDATE:
                    if (widgetIds != null) {
                        // udpate all widget
                        for (int widgetID : widgetIds) {
                            updateWidget(mContext, widgetID);
                        }
                    }
                    break;
                case MSG_UPDATE_PER_HOUR:
                case MSG_CITY_CHANGE:
                    // get weather and update widget
                    updWeatherInfo();
                    if (msg.what == MSG_UPDATE_PER_HOUR) {
                        mHandler.sendEmptyMessageDelayed(MSG_UPDATE, REFRESH_HOUR);
                    }
                    break;
                default:
                    break;
            }
        }

    };

    @Override
    public void onReceive(Context context, Intent intent) {
        super.onReceive(context, intent);
        final String action = intent.getAction();
        Log.d(TAG, "OnReceive :Action: " + action);
        mContext = context;
        // handle action
        if (action.equals(NETWORK_OK_ACTION)) {
            updWeatherInfo();
        } else if (action.equals(Intent.ACTION_LOCALE_CHANGED)) {
            mHandler.sendEmptyMessage(MSG_UPDATE);
        } else if (action.equals(CLICK_NAME_ACTION)) {
            // update weather
            updWeatherInfo();
            String clsName = "com.android.settings.Settings$CitySettingsActivity";
            String pkgName = "com.android.settings";
            boolean isInstalled = Utils.isApkInstalled(mContext, pkgName, clsName);
            if (isInstalled) {
                Utils.startApp(mContext, pkgName, clsName);
            } else {
                Log.e(TAG, "apk not installed");
            }
        }
    }

    @Override
    public void onUpdate(Context context, AppWidgetManager appWidgetManager, int[] appWidgetIds) {
        super.onUpdate(context, appWidgetManager, appWidgetIds);
        Log.d(TAG, "*****onUpdate********** ");
        // clear former widget to avoid create too many widget object
        clearFormerWidget(WeatherWidgetProvider.class.getName());

        // init object
        mContext = context;
        widgetIds = appWidgetIds;

        if (observer == null) {
            observer = new SettingsObserver(mHandler, context);
            observer.observe(context);
        }
        if (weatherHelper == null) {
            weatherHelper = new WeatherHelper(context);
        }

        // get weather info from network and update widget
        updWeatherInfo();

        mHandler.sendEmptyMessageDelayed(MSG_UPDATE, REFRESH_HOUR);
    }

    @Override
    public void onDeleted(Context context, int[] appWidgetIds) {
        super.onDeleted(context, appWidgetIds);
        Log.v(TAG, "onDeleted");
        if (observer != null) {
            observer.unregisterContentObserver();
        }
    }

    @Override
    public void onEnabled(Context context) {
        super.onEnabled(context);
        Log.v(TAG, "onEnabled");
    }

    @Override
    public void onDisabled(Context context) {
        super.onDisabled(context);
        Log.v(TAG, "onDisabled");
    }

    /**
     * update the given id widget,get the weather info from.
     * weatherPreference.xml
     * 
     * @param context
     * @param id The widget ID
     */
    private void updateWidget(Context context, int id) {
        if (mSharedPreferences == null) {
            mSharedPreferences = mContext.getSharedPreferences("weatherPreference", Context.MODE_PRIVATE);
        }
        String tc = context.getResources().getString(R.string.temp_c);
        if (mRemoteViews == null) {
            mRemoteViews = new RemoteViews(context.getPackageName(), R.layout.weather_widget);
            mRemoteViews.setTextViewText(R.id.c_char, tc);
            Intent intent = new Intent(context, WeatherWidgetProvider.class);
            PendingIntent pendingIntent = PendingIntent.getActivity(context, 0, intent, 0);
            mRemoteViews.setOnClickPendingIntent(R.id.relativeLayout, pendingIntent);
        }

        String location = mSharedPreferences.getString("location", weatherHelper.getCurCity());
        String condition = mSharedPreferences.getString("condition", context.getString(R.string.sun));
        String temperature = mSharedPreferences.getString("temperature", "26");
        String updateTime = context.getResources().getString(R.string.last_update) + " "
                + mSharedPreferences.getString("lastUpdate", getLastUpdateTime());
        String top = context.getResources().getString(R.string.high_tem) + " "
                + mSharedPreferences.getString("top", "30") + tc;
        String bottom = context.getResources().getString(R.string.low_tem) + " "
                + mSharedPreferences.getString("bottom", "20") + tc;
        int drawableId = mSharedPreferences.getInt("drawable", R.drawable.ic_weather_cloudy_l);
        Log.v(TAG, "updateWidget = " + location + ";" + condition + ";" + temperature);

        mRemoteViews.setImageViewResource(R.id.weather_img, drawableId);
        mRemoteViews.setTextViewText(R.id.weather_city, location);
        mRemoteViews.setTextViewText(R.id.weather_text, condition);
        mRemoteViews.setTextViewText(R.id.weather_temp, temperature);
        mRemoteViews.setTextViewText(R.id.top, top);
        mRemoteViews.setTextViewText(R.id.bottom, bottom);
        mRemoteViews.setTextViewText(R.id.weather_update_time, updateTime);

        if (mAppWidgetManager == null) {
            mAppWidgetManager = AppWidgetManager.getInstance(mContext);
        }
        mAppWidgetManager.updateAppWidget(id, mRemoteViews);
    }

    /**
     * The method do this: (1).get weather from network ,if the data is not
     * null, save the info to weatherPreference.xml. (2).send message to update
     * widget view
     */
    private void updWeatherInfo() {
        new Thread(new Runnable() {

            @Override
            public void run() {
                synchronized (lock) {
                    if (weatherHelper == null) {
                        weatherHelper = new WeatherHelper(mContext);
                    }
                    if (mSharedPreferences == null) {
                        mSharedPreferences = mContext.getSharedPreferences("weatherPreference", Context.MODE_PRIVATE);
                    }
                    weatherItem = weatherHelper.getCurrentWeather(0);
                    if (weatherItem != null) {
//                        Log.v(TAG, "getWeather = " + weatherHelper.getCurCity() + ";" + weatherItem.getStatus1() + ";"
//                                + weatherItem.getTgd1());
                        String location = weatherHelper.getCurCity();
                        int redId;
                        if (weatherItem.getStatus1() == null) {
                            Log.i(TAG, "status1 is null.");
                            if (weatherItem.getStatus2() == null) {
                                redId = R.drawable.ic_weather_clear_day_l;
                            } else {
                                redId = weatherHelper.getIcon(weatherItem.getStatus2());
                            }
                        } else {
                            Log.i(TAG, "status1 has data.");
                            redId = weatherHelper.getIcon(weatherItem.getStatus1());
                        }
                        String condition = mSharedPreferences.getString("condition", mContext.getString(R.string.sun));
                        if (weatherHelper.getCondition() != null) {
                            condition = weatherHelper.getCondition();
                        }
                        String temperature = mSharedPreferences.getString("temperature", "26");
                        if (weatherItem.getTgd1() != null) {
                            temperature = weatherItem.getTgd1();
                        }
                        String top = mSharedPreferences.getString("top", "30");
                        if (weatherItem.getTemperature1() != null) {
                            top = weatherItem.getTemperature1();
                        }
                        String bottom = mSharedPreferences.getString("bottom", "20");
                        if (weatherItem.getTemperature2() != null) {
                            bottom = weatherItem.getTemperature2();
                        }
                        saveWeatherInfo(location, condition, temperature, redId, top, bottom);
                    } else {
                        Log.e(TAG, "weatherItem = null ");
                    }
                    // update widget ignore whether get weather info
                    mHandler.sendEmptyMessage(MSG_UPDATE);
                }
            }
        }).start();
    }

    /**
     * save weather infomation for next use if the network is not connected.
     * 
     * @param location
     * @param condition
     * @param temperature
     * @param redId
     * @param top
     * @param bottom
     */
    private void saveWeatherInfo(String location, String condition, String temperature, int redId, String top,
            String bottom) {
        if (mSharedPreferences == null) {
            mSharedPreferences = mContext.getSharedPreferences("weatherPreference", Context.MODE_PRIVATE);
        }
        Editor editor = mSharedPreferences.edit();
        editor.putString("location", location).commit();
        editor.putString("condition", condition).commit();
        editor.putString("temperature", temperature).commit();
        editor.putInt("drawable", redId).commit();
        editor.putString("lastUpdate", getLastUpdateTime()).commit();
        editor.putString("top", top).commit();
        editor.putString("bottom", bottom).commit();
    }

    private String getLastUpdateTime() {
        SimpleDateFormat bartDateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm");
        Date date = new Date();
        return bartDateFormat.format(date);
    }

}
