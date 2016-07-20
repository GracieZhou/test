
package com.heran.launcher2.weather;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

import com.heran.launcher2.PublicFragment;
import com.heran.launcher2.R;
import com.heran.launcher2.util.Tools;
import com.heran.launcher2.widget.LineChartView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.TextView;

public class WeatherMainFragmentWeek extends PublicFragment {

    private static final String TAG = "WeatherMainFragment_week";

    private Handler mHandler;

    private LineChartView tempGraph;

    private GridView weekWeatherGridView;

    private WeekWeatherAdapter mWeekWeatherAdapter;

    public ArrayList<WeatherWeek> list = null;

    private List<WeatherWeekBean> weather7DaysDatas = new ArrayList<WeatherWeekBean>();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // setRetainInstance(true);
        Log.d(TAG, "onCreateView");
        View mView = inflater.inflate(R.layout.weather_main_content_week, container, false);
        weekWeatherGridView = (GridView) mView.findViewById(R.id.week_list);
        mWeekWeatherAdapter = new WeekWeatherAdapter(getActivity());
        weekWeatherGridView.setAdapter(mWeekWeatherAdapter);
        tempGraph = (LineChartView) mView.findViewById(R.id.temp_grap);

        mHandler = new MyHandler();
        return mView;
    }

    private void updateDatas() {
        weather7DaysDatas = WeatherHelper.getWeatherWeekDataFromSp(WeatherHelper.getCurrentCityName(getActivity()))
                .get7DaysWeatherDatas();
        if (weather7DaysDatas != null && weather7DaysDatas.size() > 0) {
            Log.d(TAG, "weather7DaysDatas size:" + weather7DaysDatas.size());
            mHandler.sendEmptyMessage(0);
            mWeekWeatherAdapter.notifyDataSetChanged();

        }
    }

    @SuppressLint("HandlerLeak")
    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    showWeekLineChart();
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume ");
        updateDatas();
    }

    public void showWeekLineChart() {
        ArrayList<Double> valuesMax = new ArrayList<Double>();
        ArrayList<Double> valuesMin = new ArrayList<Double>();
        for (WeatherWeekBean mWeatherWeekBean : weather7DaysDatas) {
            valuesMin.add(Double.valueOf(
                    mWeatherWeekBean.getTemperature().substring(0, mWeatherWeekBean.getTemperature().indexOf("~"))));
            valuesMax.add(Double.valueOf(
                    mWeatherWeekBean.getTemperature().substring(mWeatherWeekBean.getTemperature().indexOf("~") + 1)));
        }
        double max = 0.0;
        max = Collections.max(valuesMax);
        Log.d(TAG, max + "");
        tempGraph.setData(valuesMax, valuesMin, (int) max, 5);
    }

    private void getWeatherImg(String str, ImageView img) {
        Log.d(TAG, "getWeatherImg");
        // 晴天
        if (str.equals("01")) {
            img.setBackground(getResources().getDrawable(R.drawable.weather_main_day_icon_01));
        }
        // 晴時多雲
        if (str.equals("02") || str.equals("07") || str.equals("08") || str.equals("43") || str.equals("45")
                || str.equals("46")) {
            img.setBackground(getResources().getDrawable(R.drawable.weather_main_day_icon_03));
        }
        // 多雲時陰
        if (str.equals("03") || str.equals("05") || str.equals("06") || str.equals("44") || str.equals("49")) {
            img.setBackground(getResources().getDrawable(R.drawable.weather_main_day_icon_cloudy));
        }
        // 有雨
        if (str.equals("04") || str.equals("12") || str.equals("13") || str.equals("17") || str.equals("18")
                || str.equals("24") || str.equals("26") || str.equals("31") || str.equals("34") || str.equals("57")
                || str.equals("58") || str.equals("59") || str.equals("60")) {
            img.setBackground(getResources().getDrawable(R.drawable.weather_main_day_icon_raining));
        }
        // 雷雨
        if (str.equals("36")) {
            img.setBackground(getResources().getDrawable(R.drawable.weather_main_day_icon_thunder));
        }
    }

    /**
     * TodayWeatherAdapter Adapter.
     * 
     * @author laird
     */
    class WeekWeatherAdapter extends BaseAdapter {

        private LayoutInflater mInflater; // 得到一个LayoutInfalter对象用来导入布局

        public WeekWeatherAdapter(Context context) {
            this.mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return weather7DaysDatas.size();
        }

        @Override
        public Object getItem(int arg0) {
            return null;
        }

        @Override
        public long getItemId(int arg0) {
            return 0;
        }

        @SuppressLint("InflateParams")
        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            ViewHolder holder;
            if (convertView == null) {
                convertView = mInflater.inflate(R.layout.weather_main_item_week, null);
                holder = new ViewHolder();
                /* 得到各个控件的对象 */
                holder.date = (TextView) convertView.findViewById(R.id.date);
                holder.week = (TextView) convertView.findViewById(R.id.week);
                holder.tmp_img = (ImageView) convertView.findViewById(R.id.tmp_img);
                holder.tmp_txt = (TextView) convertView.findViewById(R.id.tmp_txt);
                convertView.setTag(holder); // 绑定ViewHolder对象
            } else {
                holder = (ViewHolder) convertView.getTag(); // 取出ViewHolder对象
            }
            /* 设置显示的内容，即我们存放在动态数组中的数据 */
            // show date
            showTime(weather7DaysDatas.get(position).getTime(), holder.date, holder.week);
            // ------------
            // show tmp img
            getWeatherImg(weather7DaysDatas.get(position).getWeather_num(), holder.tmp_img);

            // show tmp txt
            showTmptxt(weather7DaysDatas.get(position).getTemperature(), holder.tmp_txt);

            return convertView;
        }

        /* 存放控件 的ViewHolder */
        public final class ViewHolder {

            public TextView date;

            public TextView week;

            public ImageView tmp_img;

            public TextView tmp_txt;

        }

    }

    public void showTmptxt(String temperature, TextView tmp_txt) {
        tmp_txt.setText(temperature.substring(0, temperature.indexOf("~")) + " ~ "
                + temperature.substring(temperature.indexOf("~") + 1));
    }

    public void showTime(String time, TextView daTextView, TextView weekTextView) {
        String date = time.substring(0, time.indexOf("T"));

        daTextView.setText(date.substring(date.indexOf("-")));
        Log.d("laird", date);
        String week = Tools.dateToWeek(getActivity(), Tools.converToDate(date));
        Log.d("laird", week);
        weekTextView.setText(week);

        date = date.substring(date.indexOf("-") + 1);
        date = date.replace("-", "/");
        daTextView.setText(date);
        Log.d("laird", "show date:" + date);
    }
}
