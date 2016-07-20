
package com.eostek.tv.launcher.ui.view;

import java.util.Locale;

import android.content.Context;
import android.content.SharedPreferences;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.eostek.tv.launcher.HomeApplication;
import com.eostek.tv.launcher.R;
import com.eostek.tv.launcher.business.WeatherHelper;
import com.eostek.tv.launcher.util.LConstants;
import com.eostek.tv.launcher.util.UIUtil;

public class WeatherView extends LinearLayout {

    private static SharedPreferences mWeatherPrefs;

    private static WeatherHelper mWeatherHelper;

    private static ImageView mImgWeather;

    private static TextView mTvCity;

    private static TextView mTvTemperature;

    private static TextView mTvDate;

    private static LinearLayout mWeatherlayout;

    public static String KEY_WEATHER_ICON = "weather_icon";

    public WeatherView(Context context) {
        super(context);

        View.inflate(context, R.layout.weather_view, this);

        mWeatherlayout = (LinearLayout) findViewById(R.id.ll_weather);
        mImgWeather = (ImageView) findViewById(R.id.iv_weather);
        mTvCity = (TextView) findViewById(R.id.tv_city);
        mTvTemperature = (TextView) findViewById(R.id.tv_temperature);
        mTvDate = (TextView) findViewById(R.id.tv_date);

    }

    public WeatherView(Context context, AttributeSet attrs) {
        super(context, attrs);

        View.inflate(context, R.layout.weather_view, this);

        mWeatherlayout = (LinearLayout) findViewById(R.id.ll_weather);
        mImgWeather = (ImageView) findViewById(R.id.iv_weather);
        mTvCity = (TextView) findViewById(R.id.tv_city);
        mTvTemperature = (TextView) findViewById(R.id.tv_temperature);
        mTvDate = (TextView) findViewById(R.id.tv_date);
    }

    public WeatherView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);

        View.inflate(context, R.layout.weather_view, this);

        mWeatherlayout = (LinearLayout) findViewById(R.id.ll_weather);
        mImgWeather = (ImageView) findViewById(R.id.iv_weather);
        mTvCity = (TextView) findViewById(R.id.tv_city);
        mTvTemperature = (TextView) findViewById(R.id.tv_temperature);
        mTvDate = (TextView) findViewById(R.id.tv_date);
    }

    public static void showWeatherView(Context context) {
        if (mWeatherPrefs == null) {
            mWeatherPrefs = context.getSharedPreferences(LConstants.PREFS_WEATHER, Context.MODE_PRIVATE);
        }
        if (mWeatherHelper == null) {
            mWeatherHelper = new WeatherHelper(context);
        }
        String tc = "°C";
        try {

            String location = mWeatherPrefs.getString(LConstants.PREFS_WEATHER_LOCATION, mWeatherHelper.getCurCity());
            String top = mWeatherPrefs.getString(LConstants.PREFS_WEATHER_TOP, "30") + tc;
            String bottom = mWeatherPrefs.getString(LConstants.PREFS_WEATHER_BOTTOM, "20") + tc;
            String drawableName = mWeatherPrefs.getString(KEY_WEATHER_ICON, "ic_weather_cloudy_l");
            int drawableId = UIUtil.getResourceId(HomeApplication.getInstance(), drawableName);

            mWeatherlayout.setVisibility(View.VISIBLE);
            mImgWeather.setVisibility(View.VISIBLE);
            mImgWeather.setImageResource(drawableId);
            mTvCity.setText(location);
            mTvTemperature.setText("  " + bottom + "~" + top);
            String country = UIUtil.getLanguage();
            String formatDate = getFormatDateEN(context);
            if (country.equals("zh-cn") || country.equals("zh-tw")) {
                formatDate = getFormatDateCN(context);
            }
            mTvDate.setText(formatDate);

        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    public static void updateWeatherTime(Context context) {
        if (null == mTvDate) {
            return;
        }
        String country = UIUtil.getLanguage();
        String formatDate = getFormatDateEN(context);
        if (country.equals("zh-cn") || country.equals("zh-tw")) {
            formatDate = getFormatDateCN(context);
        }
        mTvDate.setText(formatDate);
    }

    public static void hideWeatherView() {
        mWeatherlayout.setVisibility(View.INVISIBLE);
        mImgWeather.setVisibility(View.INVISIBLE);
    }

    private static String getFormatDateEN(Context context) {
        return UIUtil.formatDate("MMM dd yyyy " + UIUtil.getHourFormat(context) + ":mm"
                + (UIUtil.isTime24(context) ? "" : " a"), Locale.ENGLISH);
    }

    private static String getFormatDateCN(Context context) {
        return UIUtil.formatDate("yyyy/MM/dd " + UIUtil.getHourFormat(context) + ":mm"
                + (UIUtil.isTime24(context) ? "" : " a"));
    }

}
