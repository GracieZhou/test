
package com.heran.launcher2.weather;

import java.util.ArrayList;
import java.util.List;

import com.heran.launcher2.PublicFragment;
import com.heran.launcher2.R;
import com.heran.launcher2.util.Tools;
import com.heran.launcher2.widget.BarChartView;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.GridView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.LinearLayout.LayoutParams;
import android.widget.RelativeLayout;
import android.widget.TextView;

public class WeatherMainFragmentToday extends PublicFragment {

    private static final String TAG = "WeatherMainFragment_today";

    private Handler mHandler;

    private GridView todayWeatherGridView;

    public TextView date_today, date_tommorow;

    private RelativeLayout weather_today_bg;

    private BarChartView mBarChartView;

    private List<WeatherTodayBean> list_today_24time = new ArrayList<WeatherTodayBean>();

    private ArrayList<Integer> humidityDatas = new ArrayList<Integer>();
    
    TodayWeatherAdapter mTodayWeatherAdapter ;

    private int[] bgs = {
            R.drawable.b11, R.drawable.b12, R.drawable.b13, R.drawable.b14, R.drawable.b15, R.drawable.b16,
            R.drawable.b17, R.drawable.b18

    };

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        Log.d(TAG, "onCreateView");
        View mView = inflater.inflate(R.layout.weather_main_content_today, container, false);
        initView(mView);
        return mView;
    }

    private void initView(View mView) {
        date_today = (TextView) mView.findViewById(R.id.date_today);
        date_tommorow = (TextView) mView.findViewById(R.id.date_tommorow);
        todayWeatherGridView = (GridView) mView.findViewById(R.id.today_list);
        weather_today_bg = (RelativeLayout) mView.findViewById(R.id.weather_today_bg);
        mHandler = new MyHandler();
        mTodayWeatherAdapter = new TodayWeatherAdapter(getActivity());
        todayWeatherGridView.setAdapter(mTodayWeatherAdapter);
        mBarChartView = (BarChartView) mView.findViewById(R.id.today_humidity);
    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        updateDatas();
    }

    public void updateDatas() {
        list_today_24time = WeatherHelper.getWeatherTodayDataFromSp(WeatherHelper.getCurrentCityName(getActivity()))
                .get24hWeatherTodayDatas();
        Log.d(TAG, "list_today_24time length:" + list_today_24time.size());
        if (list_today_24time != null && list_today_24time.size() > 0) {
            mHandler.sendEmptyMessage(0);
        }
    }

    @SuppressLint("HandlerLeak")
    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                	mTodayWeatherAdapter.notifyDataSetChanged();
                    showDate();
                    showHhmidity();
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onPause() {
        super.onPause();

    }

    private void showDate() {
        // 時間

        ArrayList<String> dateList = new ArrayList<String>();
        for (WeatherTodayBean weatherTodayBean : list_today_24time) {
            String date = weatherTodayBean.getTime().substring(5, 10);
            date = date.replace("-", "/");
            Log.d(TAG, "data---:" + date);
            dateList.add(date);
        }
        String weekDays[] = getActivity().getResources().getStringArray(R.array.weekDays);
        String todayOfWeek = "", tomorrowOfWeek = "";
        todayOfWeek = weekDays[Tools.getTodayWeek_Index() - 1];
        tomorrowOfWeek = weekDays[(Tools.getTodayWeek_Index()) % 7];

        int a = 0;
        for (int i = 0; i < dateList.size(); i++) {
            if (dateList.get(0).equals(dateList.get(i))) {
                a++;
            }
        }

        Log.d(TAG, "a : " + a);

        LinearLayout.LayoutParams param1 = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.MATCH_PARENT);
        param1.weight = a;
        date_today.setLayoutParams(param1);
        date_today.setText(dateList.get(0) + "(" + todayOfWeek + ")");

        LinearLayout.LayoutParams param2 = new LinearLayout.LayoutParams(LayoutParams.WRAP_CONTENT,
                LayoutParams.MATCH_PARENT);
        param2.weight = 8 - a;
        date_tommorow.setLayoutParams(param2);
        date_tommorow.setGravity(Gravity.CENTER);
        date_tommorow.setText(dateList.get(a) + "(" + tomorrowOfWeek + ")");

        weather_today_bg.setBackgroundResource(bgs[a - 1]);
    }

    private void showTime(int position, TextView timetxt) {
        // 時間
        String time1 = list_today_24time.get(position).getTime();

        String[] data_date1 = time1.split("T|\\+|-");

        String[] data_time1 = data_date1[3].trim().split(":");

        timetxt.setText(data_time1[0] + ":" + data_time1[1]);

    }

    public void showHhmidity() {
        if (humidityDatas != null) {
            humidityDatas.clear();
            for (WeatherTodayBean weatherTodayBean : list_today_24time) {
                Log.d(TAG, "humidityDatas:" + weatherTodayBean.getHumidity());
                humidityDatas.add(Integer.valueOf(weatherTodayBean.getHumidity()));
            }
            if (humidityDatas.size() == 8) {
                mBarChartView.updateThisData((humidityDatas));
            }
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    /**
     * TodayWeatherAdapter Adapter.
     * 
     * @author laird
     */
    class TodayWeatherAdapter extends BaseAdapter {

        private LayoutInflater mInflater; // 得到一个LayoutInfalter对象用来导入布局

        public TodayWeatherAdapter(Context context) {
            this.mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return list_today_24time.size();
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
                convertView = mInflater.inflate(R.layout.weather_main_item_today, null);
                holder = new ViewHolder();
                /* 得到各个控件的对象 */
                holder.time = (TextView) convertView.findViewById(R.id.time);
                holder.tmp_img = (ImageView) convertView.findViewById(R.id.tmp_img);
                holder.tmp_txt = (TextView) convertView.findViewById(R.id.tmp_txt);
                holder.tmp_dsc = (TextView) convertView.findViewById(R.id.tmp_dsc);
                holder.wind_txt = (TextView) convertView.findViewById(R.id.wind_txt);
                holder.wind_img = (ImageView) convertView.findViewById(R.id.wind_img);
                holder.rain = (TextView) convertView.findViewById(R.id.rain);

                convertView.setTag(holder); // 绑定ViewHolder对象
            } else {
                holder = (ViewHolder) convertView.getTag(); // 取出ViewHolder对象
            }
            /* 设置显示的内容，即我们存放在动态数组中的数据 */
            // show date
            showTime(position, holder.time);
            // ------------
            // 天气图

            getWeatherImg(list_today_24time.get(position).getWeather_num(), holder.tmp_img);

            // 溫度
            holder.tmp_txt.setText(list_today_24time.get(position).getTemperature());
            // 温度描述
            holder.tmp_dsc.setText(list_today_24time.get(position).getWeather_str());

            // show 湿度
            // 风级
            holder.wind_txt.setText(list_today_24time.get(position).getWind_lv());

            // 风向
            showWind(list_today_24time.get(position).getWind_direction(), holder.wind_img);

            // 降雨機率
            holder.rain.setText(list_today_24time.get(position).getRain());

            return convertView;
        }

        /* 存放控件 的ViewHolder */
        public final class ViewHolder {

            public TextView time;

            public ImageView tmp_img;

            public TextView tmp_txt;

            public TextView tmp_dsc;

            public TextView wind_txt;

            public ImageView wind_img;

            public TextView rain;

        }

    }

    private void getWeatherImg(String str, ImageView img) {
        // 晴天
        if (str.equals("01")) {
            img.setImageDrawable(getResources().getDrawable(R.drawable.weather_main_day_icon_01));
        }
        // 晴時多雲
        if (str.equals("02") || str.equals("07") || str.equals("08") || str.equals("43") || str.equals("45")
                || str.equals("46")) {
            img.setImageDrawable(getResources().getDrawable(R.drawable.weather_main_day_icon_03));
        }
        // 多雲時陰
        if (str.equals("03") || str.equals("05") || str.equals("06") || str.equals("44") || str.equals("49")) {
            img.setImageDrawable(getResources().getDrawable(R.drawable.weather_main_day_icon_snowwing));
        }
        // 有雨
        if (str.equals("04") || str.equals("12") || str.equals("13") || str.equals("17") || str.equals("18")
                || str.equals("24") || str.equals("26") || str.equals("31") || str.equals("34") || str.equals("57")
                || str.equals("58") || str.equals("59") || str.equals("60")) {
            img.setImageDrawable(getResources().getDrawable(R.drawable.weather_main_day_icon_raining));
        }
        // 雷雨
        if (str.equals("36")) {
            img.setImageDrawable(getResources().getDrawable(R.drawable.weather_main_day_icon_thunder));
        }
    }

    private void showWind(String strwind, ImageView mImageView) {

        String[] winds = getActivity().getResources().getStringArray(R.array.wind);

        if (strwind.trim().equals(winds[0])) {
            mImageView.setImageResource(R.drawable.weather_01_date_wing_right);
        } else if (strwind.trim().equals(winds[1])) {
            mImageView.setImageResource(R.drawable.weather_01_date_wing_left);
        } else if (strwind.trim().equals(winds[2])) {
            mImageView.setImageResource(R.drawable.weather_01_date_wing_down);
        } else if (strwind.trim().equals(winds[3])) {
            mImageView.setImageResource(R.drawable.weather_01_date_wing_up);
        } else if (strwind.trim().equals(winds[4])) {
            mImageView.setImageResource(R.drawable.weather_01_date_wing_upleft);
        } else if (strwind.trim().equals(winds[5])) {
            mImageView.setImageResource(R.drawable.weather_01_date_wing_leftdown);
        } else if (strwind.trim().equals(winds[6])) {
            mImageView.setImageResource(R.drawable.weather_01_date_wing_upright);
        } else if (strwind.trim().equals(winds[7])) {
            mImageView.setImageResource(R.drawable.weather_01_date_wing_rightdown);
        }

    }
}
