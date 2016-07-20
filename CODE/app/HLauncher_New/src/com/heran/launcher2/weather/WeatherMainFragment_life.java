
package com.heran.launcher2.weather;

import java.util.ArrayList;

import com.heran.launcher2.HomeActivity;
import com.heran.launcher2.MainViewHolder;
import com.heran.launcher2.PublicFragment;
import com.heran.launcher2.R;
import com.heran.launcher2.util.FocusView;
import com.heran.launcher2.widget.ViewBean;

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

public class WeatherMainFragment_life extends PublicFragment {

    private static final String TAG = "WeatherMainFragment_life";

    View mView;

    private final ViewBean mViewBean;

    private final MainViewHolder mHolder;

    private final HomeActivity mContext;

    private final Handler mHandler;

    private FrameLayout weatherMainRight;

    private ImageView life_img01, life_img02, life_img03, life_img04;

    private TextView life_txt01, life_txt02, life_txt03, life_txt04;

    public ArrayList<WeatherLife> list = null;

    private int city_num = 0;

    public WeatherMainFragment_life(HomeActivity context, MainViewHolder mHolder) {
        super();
        this.mContext = context;
        this.mHolder = mHolder;
        mViewBean = new ViewBean(null, null);
        mHandler = new MyHandler();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Log.d(TAG, "onCreateView");
        Log.d("test", "life onCreateView");
        mView = inflater.inflate(R.layout.weather_main_content_life, container, false);
        try {
            initView();
        } catch (Exception e) {
            Log.d(TAG, e.toString());
        }
        return mView;
    }

    public void setWeatherList(ArrayList<WeatherLife> weatherlist) {
        this.list = weatherlist;
        city_num = (int) WeatherHelper.getCurrentCity(1).get("cityIndex");
    }

    private void initView() {
        FocusView mFocusView = (FocusView) mView.findViewById(R.id.home_selector);
        mViewBean.setmFocusObject(mFocusView);
        weatherMainRight = (FrameLayout) mView.findViewById(R.id.weather_main_right);

        life_img01 = (ImageView) mView.findViewById(R.id.life_img01);
        life_img02 = (ImageView) mView.findViewById(R.id.life_img02);
        life_img03 = (ImageView) mView.findViewById(R.id.life_img03);
        life_img04 = (ImageView) mView.findViewById(R.id.life_img04);

        life_txt01 = (TextView) mView.findViewById(R.id.life_txt01);
        life_txt02 = (TextView) mView.findViewById(R.id.life_txt02);
        life_txt03 = (TextView) mView.findViewById(R.id.life_txt03);
        life_txt04 = (TextView) mView.findViewById(R.id.life_txt04);

    }

    @Override
    public void onResume() {
        // TODO Auto-generated method stub
        super.onResume();
        if (list != null) {
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
                    showImg();
                    showTxt();
                    break;
                default:
                    break;
            }
        }
    }

    private void showImg() {
        life_img01.setBackground(null);
        life_img02.setBackground(null);
        life_img03.setBackground(null);
        life_img04.setBackground(null);
        city_num = (int) WeatherHelper.getCurrentCity(1).get("cityIndex");

        Log.d("iii", "clothes_color : " + list.get(city_num).clothes_color);
        Log.d("iii", "car_color : " + list.get(city_num).car_color);
        Log.d("iii", "outdoor_color : " + list.get(city_num).outdoor_color);
        Log.d("iii", "Clothesline_color : " + list.get(city_num).Clothesline_color);

        if (list.get(city_num).clothes_color.equals("1")) {
            life_img01.setBackgroundResource(R.drawable.weather_03_o_green);
        } else if (list.get(city_num).clothes_color.equals("2")) {
            life_img01.setBackgroundResource(R.drawable.weather_03_o_yellow);
        } else if (list.get(city_num).clothes_color.equals("3")) {
            life_img01.setBackgroundResource(R.drawable.weather_03_o_red);
        } else {

        }

        if (list.get(city_num).car_color.equals("1")) {
            life_img02.setBackgroundResource(R.drawable.weather_03_o_green);
        } else if (list.get(city_num).car_color.equals("2")) {
            life_img02.setBackgroundResource(R.drawable.weather_03_o_yellow);
        } else if (list.get(city_num).car_color.equals("3")) {
            life_img02.setBackgroundResource(R.drawable.weather_03_o_red);
        } else {

        }

        if (list.get(city_num).outdoor_color.equals("1")) {
            life_img03.setBackgroundResource(R.drawable.weather_03_o_green);
        } else if (list.get(city_num).outdoor_color.equals("2")) {
            life_img03.setBackgroundResource(R.drawable.weather_03_o_yellow);
        } else if (list.get(city_num).outdoor_color.equals("3")) {
            life_img03.setBackgroundResource(R.drawable.weather_03_o_red);
        } else {

        }

        if (list.get(city_num).Clothesline_color.equals("1")) {
            life_img04.setBackgroundResource(R.drawable.weather_03_o_green);
        } else if (list.get(city_num).Clothesline_color.equals("2")) {
            life_img04.setBackgroundResource(R.drawable.weather_03_o_yellow);
        } else if (list.get(city_num).Clothesline_color.equals("3")) {
            life_img04.setBackgroundResource(R.drawable.weather_03_o_red);
        } else {

        }

    }

    private void showTxt() {
        if (list.get(city_num).clothes != null && list.get(city_num).clothes != "") {
            life_txt01.setText(list.get(city_num).clothes);
        } else {
            life_txt01.setText(R.string.analysing);
        }
        if (list.get(city_num).car != null && list.get(city_num).car != "") {
            life_txt02.setText(list.get(city_num).car);
        } else {
            life_txt02.setText(R.string.analysing);
        }
        if (list.get(city_num).outdoor != null && list.get(city_num).outdoor != "") {
            life_txt03.setText(list.get(city_num).outdoor);
        } else {
            life_txt03.setText(R.string.analysing);
        }
        if (list.get(city_num).Clothesline != null && list.get(city_num).Clothesline != "") {
            life_txt04.setText(list.get(city_num).Clothesline);
        } else {
            life_txt04.setText(R.string.no_analysing);
        }
    }
}
