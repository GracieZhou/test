
package com.heran.launcher2.weather;

import com.heran.launcher2.HomeActivity;
import com.heran.launcher2.PublicFragment;
import com.heran.launcher2.R;
import com.heran.launcher2.util.HistoryRec;
import com.heran.launcher2.util.Utils;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * projectName： EosLauncher moduleName： AdFragment.java
 * 
 * @author Jason.Pan
 * @version 1.0.0
 * @time 2014-12-03 下午14:33:00
 * @Copyright © 2014 Heran Inc.
 */

public class WeatherFragment extends PublicFragment {

    private final static String TAG = "WeatherFragment";

    private HomeActivity mContext;

    private TextView location, tempH, tempL, weatherDesc, currentTemp;

    private ImageView weatherImg;

    private String recData = "";

    private FrameLayout noNetFL;

    private FrameLayout noDataFL;

    private Handler mHandler;

    private WeatherToday mWeatherToday;

    private WeatherWeek mWeatherWeek;

    private final static int SHOW_TEMP = 1;

    public WeatherFragment() {
        super();
        Log.v(TAG, "public weatherfragment()");
    }

    public WeatherFragment(HomeActivity context) {
        super();
        this.mContext = context;
        mHandler = new MyHandler();
        Log.d(TAG, "(HomeActivity context, MainViewHolder mHolder)");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mview = inflater.inflate(R.layout.weather_main, container, false);
        setRetainInstance(true);

        location = (TextView) mview.findViewById(R.id.tv_location);
        tempH = (TextView) mview.findViewById(R.id.tv_tempH);
        tempL = (TextView) mview.findViewById(R.id.tv_tempL);
        weatherDesc = (TextView) mview.findViewById(R.id.tv_weatherDesc);
        currentTemp = (TextView) mview.findViewById(R.id.tv_currentTemp);
        weatherImg = (ImageView) mview.findViewById(R.id.iv_weatherImg);
        noNetFL = (FrameLayout) mview.findViewById(R.id.no_net);
        noDataFL = (FrameLayout) mview.findViewById(R.id.loading_data);
        return mview;
    }

    @Override
    public void onStart() {
        super.onStart();
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        if (!Utils.isNetworkState) {
            // 網路未通 , 只更新跟網路無關的 UI
            Log.d(TAG, "no network");
            noNetFL.setVisibility(View.VISIBLE);
        } else { // 網路有通 , 要 取得天氣資訊
            Log.d(TAG, "network ok");
            noNetFL.setVisibility(View.GONE);
            recData = HistoryRec.block[9] + ',' + HistoryRec.block10Action[1] + ',' + R.string.meteorological + ',' + ""
                    + ',' + "" + ',' + HistoryRec.getCurrentDateTime();
            HistoryRec.writeToFile(recData);
            recData = "";
            mHandler.sendEmptyMessage(SHOW_TEMP);
        }

    }

    @Override
    public void onPause() {
        super.onPause();
        Log.d(TAG, "onPause");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d(TAG, "onDestroy");
        mHandler.removeMessages(SHOW_TEMP);

    }

    @SuppressLint("HandlerLeak")
    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {

                case SHOW_TEMP:
                    String cityName = WeatherHelper.getCurrentCityName(getActivity());
                    ;
                    mWeatherToday = WeatherHelper.getWeatherTodayDataFromSp(cityName);
                    mWeatherWeek = WeatherHelper.getWeatherWeekDataFromSp(cityName);
                    if (mWeatherToday != null && mWeatherWeek != null && mWeatherWeek.get7DaysWeatherDatas().size() > 0
                            && mWeatherToday.get24hWeatherTodayDatas().size() > 0) {
                        noDataFL.setVisibility(View.GONE);
                        location.setText(cityName);
                        String[] t1 = mWeatherWeek.get7DaysWeatherDatas().get(0).getTemperature().trim().split("~");
                        Log.d(TAG, mWeatherWeek.get7DaysWeatherDatas().get(0).getTemperature());
                        for (int i = 0; i < t1.length; i++) {
                            Log.d(TAG, "t1[" + i + "] : " + t1[i].toString());
                        }
                        tempH.setText(t1[1].trim() + "↓");
                        tempL.setText(t1[0].trim() + "↑");
                        weatherDesc.setText(mWeatherToday.get24hWeatherTodayDatas().get(0).getWeather_str());
                        currentTemp.setText(mWeatherToday.get24hWeatherTodayDatas().get(0).getTemperature());
                        getWeatherImg(mWeatherToday.get24hWeatherTodayDatas().get(0).getWeather_num(), weatherImg);
                        mContext.mviewHolder.mWeatherMainFragment.setData(t1[1].trim(), t1[0].trim(),
                                mWeatherToday.get24hWeatherTodayDatas().get(0).getWeather_str(),
                                mWeatherToday.get24hWeatherTodayDatas().get(0).getTemperature(),
                                mWeatherToday.get24hWeatherTodayDatas().get(0).getWeather_num());
                    } else {
                        removeMessages(SHOW_TEMP);
                        sendEmptyMessageDelayed(SHOW_TEMP, 2000);
                        noDataFL.setVisibility(View.VISIBLE);
                    }
                    break;
                default:
                    break;
            }

        }

    }

    private void getWeatherImg(String str, ImageView img) {
        Log.d(TAG, "getWeatherImg");
        // 晴天
        if (str.equals("01")) {
            img.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.weather_main_day_icon_01));
        }
        // 晴時多雲
        if (str.equals("02") || str.equals("07") || str.equals("08") || str.equals("43") || str.equals("45")
                || str.equals("46")) {
            img.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.weather_main_day_icon_03));
        }
        // 多雲時陰
        if (str.equals("03") || str.equals("05") || str.equals("06") || str.equals("44") || str.equals("49")) {
            img.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.weather_main_day_icon_snowwing));
        }
        // 有雨
        if (str.equals("04") || str.equals("12") || str.equals("13") || str.equals("17") || str.equals("18")
                || str.equals("24") || str.equals("26") || str.equals("31") || str.equals("34") || str.equals("57")
                || str.equals("58") || str.equals("59") || str.equals("60")) {
            img.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.weather_main_day_icon_raining));
        }
        // 雷雨
        if (str.equals("36")) {
            img.setImageDrawable(getActivity().getResources().getDrawable(R.drawable.weather_main_day_icon_thunder));
        }
    }

    
    public void setCityText(){
    	location.setText(WeatherHelper.getCurrentCityName(mContext));
    	mHandler.sendEmptyMessage(SHOW_TEMP);
    }

}
