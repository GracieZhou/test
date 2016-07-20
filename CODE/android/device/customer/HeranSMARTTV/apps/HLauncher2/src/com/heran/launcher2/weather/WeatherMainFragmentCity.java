
package com.heran.launcher2.weather;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONException;
import org.json.JSONObject;

import com.heran.launcher2.HomeApplication;
import com.heran.launcher2.PublicFragment;
import com.heran.launcher2.R;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.BaseAdapter;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ListView;
import android.widget.TextView;

public class WeatherMainFragmentCity extends PublicFragment {

    private static final String TAG = "WeatherMainFragment_city";

    private final Handler mHandler;

    private LinearLayout weatherCityLL;

    public ImageButton direction;

    private ImageView map;

    private int index = 0;

    private int index_go = 0;

    private final int direction_n = 0;

    private final int direction_e = 3;

    private MyDirectionOnKey myDirectionOnKey;

    private ListView cityListView;

    private CityWeatherAdapter mCityWeatherAdapter;

    public List<WeatherCity> weatherCityLLList = new ArrayList<WeatherCity>();

    private ArrayList<WeatherCity> showWeatherCityInfoList = new ArrayList<WeatherCity>();

    public WeatherMainFragmentCity() {
        super();
        mHandler = new MyHandler();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Log.d(TAG, "onCreateView");
        Log.d("test", "city onCreateView");
        View mView = inflater.inflate(R.layout.weather_main_content_city, container, false);
        initView(mView);
        return mView;
    }

    private void initView(View mView) {
        Log.d(TAG, "city initView");
        weatherCityLL = (LinearLayout) mView.findViewById(R.id.city);
        direction = (ImageButton) mView.findViewById(R.id.direction);
        map = (ImageView) mView.findViewById(R.id.map);
        weatherCityLL.setBackground(getResources().getDrawable(R.drawable.weather_04_2nav_n_frame));

        cityListView = (ListView) mView.findViewById(R.id.city_list);

        showWeatherCityInfoList.clear();
        initDatas();
        showWeatherCityInfoList.addAll(weatherCityLLList.subList(0, 7));

        mCityWeatherAdapter = new CityWeatherAdapter(getActivity());
        cityListView.setAdapter(mCityWeatherAdapter);
        myDirectionOnKey = new MyDirectionOnKey();
        direction.setOnKeyListener(myDirectionOnKey);

        direction.requestFocus();

    }

    private void showTextData(int num) {
        showWeatherCityInfoList.clear();
        switch (num) {
            case 0:
                showWeatherCityInfoList.addAll(weatherCityLLList.subList(0, 7));
                break;
            case 1:
                showWeatherCityInfoList.addAll(weatherCityLLList.subList(7, 11));
                break;
            case 2:
                showWeatherCityInfoList.addAll(weatherCityLLList.subList(11, 16));
                break;
            case 3:
                showWeatherCityInfoList.addAll(weatherCityLLList.subList(16, 19));
                break;
            default:
                break;
        }
        mCityWeatherAdapter.notifyDataSetChanged();
    }

    @SuppressLint("HandlerLeak")
    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 0:
                    Log.d(TAG, "index_go : " + index_go);
                    Log.d(TAG, "index : " + index);
                    if (index == direction_n && index_go == direction_e) {
                        Log.d(TAG, "right");

                        direction.setAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.push_right_in));
                        direction.setAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.push_left_out));
                    } else if (index == direction_e && index_go == direction_n) {
                        Log.d(TAG, "left");
                        direction.setAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.push_left_in));
                        direction.setAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.push_right_out));
                    } else if (index > index_go) {
                        direction.setAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.push_right_in));
                        direction.setAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.push_left_out));
                    } else {
                        direction.setAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.push_left_in));
                        direction.setAnimation(AnimationUtils.loadAnimation(getActivity(), R.anim.push_right_out));
                    }
                    index_go = index;
                    switch (index) {
                        case 0:
                            direction.setBackground(getResources().getDrawable(R.drawable.weather_04_2nav_n_text));
                            map.setBackground(getResources().getDrawable(R.drawable.weather_04_2nav_n_map));
                            weatherCityLL.setBackground(getResources().getDrawable(R.drawable.weather_04_2nav_n_frame));
                            // weatherCityLL.getLayoutParams().width = 306; //
                            // for
                            // // 628
                            // // 30版
                            // weatherCityLL.getLayoutParams().height = 366;
                            // weatherCityLL.getLayoutParams().width = 460; //
                            // for
                            // 638
                            weatherCityLL.getLayoutParams().height = 549;
                            showTextData(0);
                            break;
                        case 1:
                            direction.setBackground(getResources().getDrawable(R.drawable.weather_04_2nav_w_text));
                            map.setBackground(getResources().getDrawable(R.drawable.weather_04_2nav_w_map));
                            weatherCityLL.setBackground(getResources().getDrawable(R.drawable.weather_04_2nav_w_frame));
                            // weatherCityLL.getLayoutParams().width = 306; //
                            // for
                            // // 628
                            // // 30版
                            // weatherCityLL.getLayoutParams().height = 224;
                            // weatherCityLL.getLayoutParams().width = 460; //
                            // for
                            // 628 31版
                            weatherCityLL.getLayoutParams().height = 336;
                            showTextData(1);
                            break;
                        case 2:
                            direction.setBackground(getResources().getDrawable(R.drawable.weather_04_2nav_s_text));
                            map.setBackground(getResources().getDrawable(R.drawable.weather_04_2nav_s_map));
                            weatherCityLL.setBackground(getResources().getDrawable(R.drawable.weather_04_2nav_s_frame));
                            // weatherCityLL.getLayoutParams().width = 306; //
                            // for
                            // // 628
                            // // 30版
                            // weatherCityLL.getLayoutParams().height = 271;
                            // weatherCityLL.getLayoutParams().width = 460; //
                            // for
                            // 638 31版
                            weatherCityLL.getLayoutParams().height = 407;
                            showTextData(2);
                            break;
                        case 3:
                            direction.setBackground(getResources().getDrawable(R.drawable.weather_04_2nav_e_text));
                            map.setBackground(getResources().getDrawable(R.drawable.weather_04_2nav_e_map));
                            weatherCityLL.setBackground(getResources().getDrawable(R.drawable.weather_04_2nav_e_frame));
                            // weatherCityLL.getLayoutParams().width = 306; //
                            // for
                            // // 628
                            // // 30版
                            // weatherCityLL.getLayoutParams().height = 177;
                            // weatherCityLL.getLayoutParams().width = 460; //
                            // for
                            // // 628 31版
                            weatherCityLL.getLayoutParams().height = 266;
                            showTextData(3);
                            break;
                        default:
                            break;
                    }
                    break;
                default:
                    break;
            }
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        mCityWeatherAdapter.notifyDataSetChanged();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    class MyDirectionOnKey implements OnKeyListener {

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                switch (v.getId()) {
                    case R.id.direction:
                        if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                            if (index == 3) {
                                index = 0;
                                mHandler.sendEmptyMessage(0);
                                return true;
                            }
                            index++;

                            mHandler.sendEmptyMessage(0);
                            return true;
                        }
                        if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                            if (index == 0) {
                                index = 3;
                                mHandler.sendEmptyMessage(0);
                                return true;
                            }
                            index--;

                            mHandler.sendEmptyMessage(0);
                            return true;
                        }

                        break;
                }
            }
            return false;
        }

    }

    private void getWeatherImg(String str, ImageView img) {
        if (str == null) {
            return;
        }
        // 晴天
        if (str.trim().equals("01")) {
            img.setBackground(getResources().getDrawable(R.drawable.weather_main_day_icon_01));
        }
        // 晴時多雲
        else if (str.trim().equals("02") || str.equals("07") || str.equals("08") || str.equals("43") || str.equals("45")
                || str.equals("46")) {
            img.setBackground(getResources().getDrawable(R.drawable.weather_main_day_icon_03));
        }
        // 多雲時陰
        else if (str.trim().equals("03") || str.equals("05") || str.equals("06") || str.equals("44")
                || str.equals("49")) {
            img.setBackground(getResources().getDrawable(R.drawable.weather_main_day_icon_cloudy));
        }
        // 有雨
        else if (str.trim().equals("04") || str.equals("12") || str.equals("13") || str.equals("17") || str.equals("18")
                || str.equals("24") || str.equals("26") || str.equals("31") || str.equals("34") || str.equals("57")
                || str.equals("58") || str.equals("59") || str.equals("60")) {
            img.setBackground(getResources().getDrawable(R.drawable.weather_main_day_icon_raining));
        }
        // 雷雨
        else if (str.trim().equals("36")) {
            img.setBackground(getResources().getDrawable(R.drawable.weather_main_day_icon_thunder));
        }
    }

    /**
     * CityWeatherAdapter Adapter.
     * 
     * @author laird
     */
    class CityWeatherAdapter extends BaseAdapter {

        private LayoutInflater mInflater; // 得到一个LayoutInfalter对象用来导入布局

        public CityWeatherAdapter(Context context) {
            this.mInflater = LayoutInflater.from(context);
        }

        @Override
        public int getCount() {
            return showWeatherCityInfoList.size();
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
                convertView = mInflater.inflate(R.layout.weather_main_item_city, null);
                holder = new ViewHolder();
                /* 得到各个控件的对象 */
                holder.city = (TextView) convertView.findViewById(R.id.city_name);
                holder.tmp = (TextView) convertView.findViewById(R.id.tmp);
                holder.tmp_img = (ImageView) convertView.findViewById(R.id.weather_img);
                holder.rain = (TextView) convertView.findViewById(R.id.rain);
                convertView.setTag(holder); // 绑定ViewHolder对象
            } else {
                holder = (ViewHolder) convertView.getTag(); // 取出ViewHolder对象
            }
            Log.d("laird", "positon:" + position);
            /* 设置显示的内容，即我们存放在动态数组中的数据 */
            // show city
            holder.city.setText(showWeatherCityInfoList.get(position).getCityName());
            // show tmp txt
            holder.tmp.setText(showWeatherCityInfoList.get(position).getTemperature());
            // show rain
            holder.rain.setText(showWeatherCityInfoList.get(position).getRian());
            // show tmp img
            getWeatherImg(showWeatherCityInfoList.get(position).getWeatherNum(), holder.tmp_img);

            return convertView;
        }

        /* 存放控件 的ViewHolder */
        public final class ViewHolder {

            public TextView city;

            public TextView tmp;

            public TextView rain;

            public ImageView tmp_img;

        }

    }

    private void initDatas() {
        String[] mCityArrays = getActivity().getResources().getStringArray(R.array.city_array1);
        for (int i = 0; i < mCityArrays.length; i++) {
            weatherCityLLList.add(getWeatherCityDataFromSp(mCityArrays[i]));
        }
    }

    private WeatherCity getWeatherCityDataFromSp(String key) {
        WeatherCity mWeatherCity = new WeatherCity();
        String data = HomeApplication.getInstance().getSharePrefrerence("weather_city", key);
        try {
            JSONObject jobb = new JSONObject(data);
            Log.d(TAG, "jobb:" + jobb.toString());
            mWeatherCity.setCity(jobb.getString("city"));
            mWeatherCity.setTemperature(jobb.getString("tmp"));
            mWeatherCity.setWeatherNum(jobb.getString("tmp_num"));
            mWeatherCity.setRain(jobb.getString("rain"));
        } catch (JSONException e) {
            e.printStackTrace();
        }
        return mWeatherCity;
    }

}
