
package com.heran.launcher2.weather;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import com.heran.launcher2.HomeActivity;
import com.heran.launcher2.MainViewHolder;
import com.heran.launcher2.PublicFragment;
import com.heran.launcher2.R;
import com.heran.launcher2.util.FocusView;
import com.heran.launcher2.util.Utils;
import com.heran.launcher2.widget.LineChartView;
import com.heran.launcher2.widget.ViewBean;

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

public class WeatherMainFragment_week extends PublicFragment {

    private static final String TAG = "WeatherMainFragment_week";

    View mView;

    private final ViewBean mViewBean;

    private final HomeActivity mContext;

    private final Handler mHandler;

    private LineChartView tempGraph;

    private GridView weekWeatherGridView;

    public ArrayList<WeatherWeek> list = null;

    private int city_num = 0;

    private List<WeatherWeekBean> weather7DaysDatas = new ArrayList<WeatherWeekBean>();

    public WeatherMainFragment_week(HomeActivity context, MainViewHolder mHolder) {
        super();
        this.mContext = context;
        mViewBean = new ViewBean(null, null);
        mHandler = new MyHandler();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // setRetainInstance(true);
        Log.d(TAG, "onCreateView");
        Log.d("test", "week onCreateView");
        if (mView == null) {
            mView = inflater.inflate(R.layout.weather_main_content_week2, container, false);
        }
        // 缓存的rootView需要判断是否已经被加过parent，
        // 如果有parent需要从parent删除，要不然会发生这个rootview已经有parent的错误。
        ViewGroup parent = (ViewGroup) mView.getParent();
        if (parent != null) {
            parent.removeView(mView);
        }
        // mView = inflater.inflate(R.layout.weather_main_content_week,
        // container, false);
        try {
            initView();
        } catch (Exception e) {
            Log.d(TAG, e.toString());
        }
        return mView;
    }

    private void initView() {
        Log.d("test", "week init");
        FocusView mFocusView = (FocusView) mView.findViewById(R.id.home_selector);
        mViewBean.setmFocusObject(mFocusView);
        weekWeatherGridView = (GridView) mView.findViewById(R.id.week_list);
        WeekWeatherAdapter mWeekWeatherAdapter = new WeekWeatherAdapter(mContext);
        weekWeatherGridView.setAdapter(mWeekWeatherAdapter);
        tempGraph = (LineChartView) mView.findViewById(R.id.temp_grap);

    }

    public void setWeatherList(ArrayList<WeatherWeek> weatherlist) {
        Log.d("VVVVV", "WEATHER week setWeatherList size :" + weatherlist.size());
        this.list = weatherlist;
        city_num = (int) WeatherHelper.getCurrentCity(0).get("cityIndex");
        updateDatas();

    }

    private void updateDatas() {

        weather7DaysDatas = list.get(city_num).get7DaysWeatherDatas();
        Log.d(TAG, "weather7DaysDatas size:" + weather7DaysDatas.size());

    }

    @SuppressLint("HandlerLeak")
    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    Log.d("test", "MyHandler 0");
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
        city_num = (int) WeatherHelper.getCurrentCity(0).get("cityIndex");
        updateDatas();
        if (Utils.isNet) {
            Log.d(TAG, "network ok");
            if (list == null) {

            } else {
                Log.d("test", "onResume ");
                mHandler.sendEmptyMessage(0);
            }
        } else {
            Log.d(TAG, "network not ready");

        }
    }

    public void showWeekLineChart() {
        ArrayList<Double> valuesMax = new ArrayList<Double>();
        ArrayList<Double> valuesMin = new ArrayList<Double>();
        ArrayList<String> values_x = new ArrayList<String>(Arrays.asList("0", "1", "2", "3", "4", "5", "6"));
        for (WeatherWeekBean mWeatherWeekBean : weather7DaysDatas) {
            valuesMin.add(Double.valueOf(
                    mWeatherWeekBean.getTemperature().substring(0, mWeatherWeekBean.getTemperature().indexOf("~"))));
            valuesMax.add(Double.valueOf(
                    mWeatherWeekBean.getTemperature().substring(mWeatherWeekBean.getTemperature().indexOf("~") + 1)));
        }

        double max = 0.0;
        for (int i = 0; i < valuesMax.size(); i++) {
            if (i != 0) {
                if (valuesMax.get(i) > valuesMax.get(i - 1)) {
                    max = valuesMax.get(i);
                } else {
                    max = valuesMax.get(i - 1);
                }
            }
        }
        Log.d("laird--max", max + "");
        tempGraph.setData(valuesMax, valuesMin, values_x, (int) max, 5);
    }

    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();

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
                convertView = mInflater.inflate(R.layout.weather_main_week_item, null);
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
        try {
            String week = DateToWeek(ConverToDate(date));
            Log.d("laird", week);
            weekTextView.setText(week);
        } catch (Exception e) {
            Log.d("laird", e.toString());
            e.printStackTrace();
        }

        date = date.substring(date.indexOf("-") + 1);
        date = date.replace("-", "/");
        daTextView.setText(date);
        Log.d("laird", "show date:" + date);
    }

    // 把字符串转为日期
    public static Date ConverToDate(String strDate) throws Exception {
        DateFormat df = new SimpleDateFormat("yyyy-MM-dd");
        return df.parse(strDate);
    }

    public static final int WEEKDAYS = 7;

    public static String[] WEEK = {
            // "SUNDAY", "MONDAY", "TUESDAY", "WEDNESDAY", "THRUSDAY", "FRIDAY",
            // "SATURDAY"
            "星期天", "星期一", "星期二", "星期三", "星期四", "星期五", "星期六"
    };

    /**
     * 日期变量转成对应的星期字符串
     * 
     * @param date
     * @return
     */
    public static String DateToWeek(Date date) {
        Calendar calendar = Calendar.getInstance();
        calendar.setTime(date);
        int dayIndex = calendar.get(Calendar.DAY_OF_WEEK);
        if (dayIndex < 1 || dayIndex > WEEKDAYS) {
            return null;
        }

        return WEEK[dayIndex - 1];
    }
}
