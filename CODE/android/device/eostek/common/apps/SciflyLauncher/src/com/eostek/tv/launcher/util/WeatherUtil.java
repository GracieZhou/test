
package com.eostek.tv.launcher.util;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Handler;
import android.util.Log;

import com.eostek.tv.launcher.business.WeatherHelper;
import com.eostek.tv.launcher.model.WeatherItem;
import com.eostek.tv.launcher.ui.view.WeatherView;

public class WeatherUtil {

    protected static final String TAG = "WeatherUtil";

    private static WeatherHelper weatherHelper;

    private SharedPreferences mSharedPreferences;

    // the lock to synchronized get network data
    private final Object lock = new Object();

    private static volatile WeatherItem weatherItem;

    private Context mContext;

    private Handler mHandler;

    private static final int WEATHER_MSG_DELAY = 2000;

    public WeatherUtil(Context context, Handler handler) {
        this.mContext = context;
        this.mHandler = handler;
    }

    public void updateWeatherInfo() {
        new Thread(new Runnable() {
            @Override
            public void run() {

                synchronized (lock) {
                    try {
                        if (mSharedPreferences == null) {
                            mSharedPreferences = mContext.getSharedPreferences("weatherPreference",
                                    Context.MODE_PRIVATE);
                        }
                        if (weatherHelper == null) {
                            weatherHelper = new WeatherHelper(mContext);
                        }
                        // weatherItem = weatherHelper.getCurrentWeather(0);
                        weatherItem = weatherHelper.getWeather(0);

                        if (weatherItem != null) {
                            String location = weatherHelper.getCurCity();
                            Log.i(TAG, "getCurCity" + weatherHelper.getCurCity());
                            // int redId = R.drawable.ic_weather_clear_day_l;
                            String redId = "ic_weather_clear_day_l";
                            if (weatherItem.getStatus1() == null || weatherItem.getStatus1().isEmpty()) {
                                if (!(weatherItem.getStatus2() == null || weatherItem.getStatus2().isEmpty())) {
                                    // redId = weatherHelper.getIcon(weatherItem.getStatus2());
                                    redId = weatherHelper.getNewIcon(weatherItem.getStatus2());
                                }
                            } else {
                                // redId = weatherHelper.getIcon(weatherItem.getStatus1());
                                redId = weatherHelper.getNewIcon(weatherItem.getStatus1());
                            }
                            String top = mSharedPreferences.getString("top", "30");
                            if (weatherItem.getTemperature1() != null && !weatherItem.getTemperature1().isEmpty()) {
                                top = weatherItem.getTemperature1();
                            }
                            String bottom = mSharedPreferences.getString("bottom", "20");
                            if (weatherItem.getTemperature2() != null && !weatherItem.getTemperature2().isEmpty()) {
                                bottom = weatherItem.getTemperature2();
                            }
                            saveWeatherInfo(location, redId, top, bottom);
                        } else {
                            String location = weatherHelper.getCurCity();
                            Log.e(TAG, "weatherItem = null ");
                            saveCity(location);
                        }
                        // update widget ignore whether get weather info
                        mHandler.sendEmptyMessageDelayed(LConstants.UPDATE_WEATHER_VIEW, WEATHER_MSG_DELAY);
                    } catch (Exception e) {
                        Log.e(TAG, "get the wrong weather message");
                        e.printStackTrace();
                        mHandler.sendEmptyMessage(LConstants.HIDE_WEATHER_VIEW);
                    }
                }
            }
        }).start();
    }

    public void saveWeatherInfo(String location, String redId, String top, String bottom) {
        if (mSharedPreferences == null) {
            mSharedPreferences = mContext.getSharedPreferences(LConstants.PREFS_WEATHER, Context.MODE_PRIVATE);
        }

        Editor editor = mSharedPreferences.edit();
        editor.putString("location", location).commit();
        // editor.putInt("drawable", redId).commit();
        editor.putString(WeatherView.KEY_WEATHER_ICON, redId).commit();
        editor.putString("top", top).commit();
        editor.putString("bottom", bottom).commit();
    }

    private void saveCity(String location) {
        if (mSharedPreferences == null) {
            mSharedPreferences = mContext.getSharedPreferences("weatherPreference", Context.MODE_PRIVATE);
        }
        Log.e(TAG, "WeatherUtil.saveCity.");

        Editor editor = mSharedPreferences.edit();
        editor.putString("location", location).commit();
    }

}
