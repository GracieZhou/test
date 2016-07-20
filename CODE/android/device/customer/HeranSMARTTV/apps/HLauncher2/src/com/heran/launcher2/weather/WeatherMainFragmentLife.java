
package com.heran.launcher2.weather;

import org.json.JSONException;
import org.json.JSONObject;

import com.heran.launcher2.HomeActivity;
import com.heran.launcher2.HomeApplication;
import com.heran.launcher2.PublicFragment;
import com.heran.launcher2.R;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

public class WeatherMainFragmentLife extends PublicFragment {

    private static final String TAG = "WeatherMainFragmentLife";

    private ImageView life_img01, life_img02, life_img03, life_img04;

    private TextView life_txt01, life_txt02, life_txt03, life_txt04;

    private final static int UPDATE_UI = 0;

    private WeatherLife mWeatherLife;

    private Handler myHandler;

    public WeatherMainFragmentLife() {
        // TODO Auto-generated constructor stub
    }

    public WeatherMainFragmentLife(HomeActivity mContext) {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Log.d(TAG, "onCreateView");
        View mView = inflater.inflate(R.layout.weather_main_content_life, container, false);
        initView(mView);
        myHandler = new MyHandler();
        return mView;
    }

    private void initView(View mView) {

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
        super.onResume();
        mWeatherLife = getLifeDataFromSp(WeatherHelper.getCurrentCityName(getActivity()));
        Log.d(TAG, "current city life:" + mWeatherLife.toString());
        if (mWeatherLife != null) {
            myHandler.sendEmptyMessage(UPDATE_UI);
        }

    }

    @SuppressLint("HandlerLeak")
    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UPDATE_UI:
                    showLifeImg();
                    showLifeTxt();
                    break;
                default:
                    break;
            }
        }
    }

    private void showLifeImg() {
        life_img01.setBackground(null);
        life_img02.setBackground(null);
        life_img03.setBackground(null);
        life_img04.setBackground(null);

        Log.d(TAG, "clothes_color : " + mWeatherLife.clothes_color);
        Log.d(TAG, "car_color : " + mWeatherLife.car_color);
        Log.d(TAG, "outdoor_color : " + mWeatherLife.outdoor_color);
        Log.d(TAG, "clothesline_color : " + mWeatherLife.clothesline_color);

        if ("1".equals(mWeatherLife.clothes_color)) {
            life_img01.setBackgroundResource(R.drawable.weather_03_o_green);
        } else if ("2".equals(mWeatherLife.clothes_color)) {
            life_img01.setBackgroundResource(R.drawable.weather_03_o_yellow);
        } else if ("3".equals(mWeatherLife.clothes_color)) {
            life_img01.setBackgroundResource(R.drawable.weather_03_o_red);
        }

        if ("1".equals(mWeatherLife.car_color)) {
            life_img02.setBackgroundResource(R.drawable.weather_03_o_green);
        } else if ("2".equals(mWeatherLife.car_color)) {
            life_img02.setBackgroundResource(R.drawable.weather_03_o_yellow);
        } else if ("3".equals(mWeatherLife.car_color)) {
            life_img02.setBackgroundResource(R.drawable.weather_03_o_red);
        }

        if ("1".equals(mWeatherLife.outdoor_color)) {
            life_img03.setBackgroundResource(R.drawable.weather_03_o_green);
        } else if ("2".equals(mWeatherLife.outdoor_color)) {
            life_img03.setBackgroundResource(R.drawable.weather_03_o_yellow);
        } else if ("3".equals(mWeatherLife.outdoor_color)) {
            life_img03.setBackgroundResource(R.drawable.weather_03_o_red);
        }

        if ("1".equals(mWeatherLife.clothesline_color)) {
            life_img04.setBackgroundResource(R.drawable.weather_03_o_green);
        } else if ("2".equals(mWeatherLife.clothesline_color)) {
            life_img04.setBackgroundResource(R.drawable.weather_03_o_yellow);
        } else if ("3".equals(mWeatherLife.clothesline_color)) {
            life_img04.setBackgroundResource(R.drawable.weather_03_o_red);
        }

    }

    private void showLifeTxt() {
        if (!TextUtils.isEmpty(mWeatherLife.clothes)) {
            life_txt01.setText(mWeatherLife.clothes);
        } else {
            life_txt01.setText(R.string.analysing);
        }
        if (!TextUtils.isEmpty(mWeatherLife.car)) {
            life_txt02.setText(mWeatherLife.car);
        } else {
            life_txt02.setText(R.string.analysing);
        }
        if (!TextUtils.isEmpty(mWeatherLife.outdoor)) {
            life_txt03.setText(mWeatherLife.outdoor);
        } else {
            life_txt03.setText(R.string.analysing);
        }
        if (!TextUtils.isEmpty(mWeatherLife.clothesline)) {
            life_txt04.setText(mWeatherLife.clothesline);
        } else {
            life_txt04.setText(R.string.no_analysing);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        myHandler.removeMessages(UPDATE_UI);
    }

    private WeatherLife getLifeDataFromSp(String key) {
        WeatherLife life = new WeatherLife();
        String data = HomeApplication.getInstance().getSharePrefrerence("weather_life", key);
        try {
            JSONObject jobb = new JSONObject(data);
            Log.d(TAG, "jobb:" + jobb.toString());
            life.setClothes_color(jobb.getString("穿衣顏色"));
            life.setClothes(jobb.getString("穿衣字串"));
            life.setCar_color(jobb.getString("行車顏色"));
            life.setCar(jobb.getString("行車字串"));
            life.setOutdoor_color(jobb.getString("戶外顏色"));
            life.setOutdoor(jobb.getString("戶外字串"));
            life.setClothesline_color(jobb.getString("曬衣顏色"));
            life.setClothesline(jobb.getString("曬衣字串"));
        } catch (JSONException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return life;
    }
}
