
package com.heran.launcher2.weather;

import java.util.ArrayList;
import java.util.List;

import com.heran.launcher2.HomeActivity;
import com.heran.launcher2.MainViewHolder;
import com.heran.launcher2.PublicFragment;
import com.heran.launcher2.R;
import com.heran.launcher2.widget.ViewBean;

import android.annotation.SuppressLint;
import android.content.res.Resources;
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
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class WeatherMainFragment_city extends PublicFragment {

    private static final String TAG = "WeatherMainFragment_city";

    private final ViewBean mViewBean;

    private final HomeActivity mContext;

    private final Handler mHandler;

    private FrameLayout weatherCity;

    private ImageButton direction;

    private ImageView map;

    private int index = 0;

    private int index_go = 0;

    private final int direction_n = 0;

    private final int direction_e = 3;

    private MyDirectionOnKey myDirectionOnKey;

    private LinearLayout layout01, layout02, layout03, layout04, layout05, layout06, layout07;

    private TextView c1, c2, c3, c4, c5, c6, c7;

    private TextView t1, t2, t3, t4, t5, t6, t7;

    private TextView r1, r2, r3, r4, r5, r6, r7;

    private ImageView w1, w2, w3, w4, w5, w6, w7;

    private List<String> list_t = new ArrayList<String>();

    private List<String> list_r = new ArrayList<String>();

    private List<String> list_w = new ArrayList<String>();

    public ArrayList<WeatherCity> list = null;

    public WeatherMainFragment_city(HomeActivity context, MainViewHolder mHolder) {
        super();
        this.mContext = context;
        mViewBean = new ViewBean(null, null);
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

    @Override
    public void onStart() {
        // TODO Auto-generated method stub
        super.onStart();
    }

    private void initView(View mView) {
        Log.d(TAG, "city initView");
        weatherCity = (FrameLayout) mView.findViewById(R.id.city);
        direction = (ImageButton) mView.findViewById(R.id.direction);
        map = (ImageView) mView.findViewById(R.id.map);

        layout01 = (LinearLayout) mView.findViewById(R.id.layout1);
        layout02 = (LinearLayout) mView.findViewById(R.id.layout2);
        layout03 = (LinearLayout) mView.findViewById(R.id.layout3);
        layout04 = (LinearLayout) mView.findViewById(R.id.layout4);
        layout05 = (LinearLayout) mView.findViewById(R.id.layout5);
        layout06 = (LinearLayout) mView.findViewById(R.id.layout6);
        layout07 = (LinearLayout) mView.findViewById(R.id.layout7);

        c1 = (TextView) mView.findViewById(R.id.c1);
        c2 = (TextView) mView.findViewById(R.id.c2);
        c3 = (TextView) mView.findViewById(R.id.c3);
        c4 = (TextView) mView.findViewById(R.id.c4);
        c5 = (TextView) mView.findViewById(R.id.c5);
        c6 = (TextView) mView.findViewById(R.id.c6);
        c7 = (TextView) mView.findViewById(R.id.c7);

        t1 = (TextView) mView.findViewById(R.id.t1);
        t2 = (TextView) mView.findViewById(R.id.t2);
        t3 = (TextView) mView.findViewById(R.id.t3);
        t4 = (TextView) mView.findViewById(R.id.t4);
        t5 = (TextView) mView.findViewById(R.id.t5);
        t6 = (TextView) mView.findViewById(R.id.t6);
        t7 = (TextView) mView.findViewById(R.id.t7);

        r1 = (TextView) mView.findViewById(R.id.r1);
        r2 = (TextView) mView.findViewById(R.id.r2);
        r3 = (TextView) mView.findViewById(R.id.r3);
        r4 = (TextView) mView.findViewById(R.id.r4);
        r5 = (TextView) mView.findViewById(R.id.r5);
        r6 = (TextView) mView.findViewById(R.id.r6);
        r7 = (TextView) mView.findViewById(R.id.r7);

        w1 = (ImageView) mView.findViewById(R.id.w1);
        w2 = (ImageView) mView.findViewById(R.id.w2);
        w3 = (ImageView) mView.findViewById(R.id.w3);
        w4 = (ImageView) mView.findViewById(R.id.w4);
        w5 = (ImageView) mView.findViewById(R.id.w5);
        w6 = (ImageView) mView.findViewById(R.id.w6);
        w7 = (ImageView) mView.findViewById(R.id.w7);

        myDirectionOnKey = new MyDirectionOnKey();
        direction.setOnKeyListener(myDirectionOnKey);

        direction.requestFocus();

    }

    public void setWeatherList(ArrayList<WeatherCity> weatherlist) {
        Log.d(TAG, "city setWeatherList");
        Log.d("VVVVV", "WEATHER city setWeatherList size :" + weatherlist.size());
        this.list = weatherlist;
        list_t = list.get(0).getData1();
        list_w = list.get(1).getData3();
        list_r = list.get(2).getData2();

    }

    private void showCity(int num) {
        Log.d(TAG, "city showCity");
        try {
            Resources res = mContext.getResources();
            String[] citys = res.getStringArray(R.array.city_array);
            if (num == 0) {
                c1.setText(citys[0]);
                c2.setText(citys[1]);
                c3.setText(citys[2]);
                c4.setText(citys[3]);
                c5.setText(citys[4]);
                c6.setText(citys[5]);
                c7.setText(citys[6]);
                if (list_t != null && list_w != null && list_r != null) {
                    t1.setText(list_t.get(0));
                    r1.setText(list_r.get(0));
                    getWeatherImg(list_w.get(0), w1);
                    t2.setText(list_t.get(1));
                    r2.setText(list_r.get(1));
                    getWeatherImg(list_w.get(1), w2);
                    t3.setText(list_t.get(2));
                    r3.setText(list_r.get(2));
                    getWeatherImg(list_w.get(2), w3);
                    t4.setText(list_t.get(3));
                    r4.setText(list_r.get(3));
                    getWeatherImg(list_w.get(3), w4);
                    t5.setText(list_t.get(4));
                    r5.setText(list_r.get(4));
                    getWeatherImg(list_w.get(4), w5);
                    t6.setText(list_t.get(5));
                    r6.setText(list_r.get(5));
                    getWeatherImg(list_w.get(5), w6);
                    t7.setText(list_t.get(6));
                    r7.setText(list_r.get(6));
                    getWeatherImg(list_w.get(6), w7);
                }

            } else if (num == 1) {
                c1.setText(citys[7]);
                c2.setText(citys[8]);
                c3.setText(citys[9]);
                c4.setText(citys[10]);
                if (list_t != null && list_w != null && list_r != null) {
                    t1.setText(list_t.get(7));
                    r1.setText(list_r.get(7));
                    getWeatherImg(list_w.get(7), w1);
                    t2.setText(list_t.get(8));
                    r2.setText(list_r.get(8));
                    getWeatherImg(list_w.get(8), w2);
                    t3.setText(list_t.get(9));
                    r3.setText(list_r.get(9));
                    getWeatherImg(list_w.get(9), w3);
                    t4.setText(list_t.get(10));
                    r4.setText(list_r.get(10));
                    getWeatherImg(list_w.get(10), w4);

                }

            } else if (num == 2) {
                c1.setText(citys[11]);
                c2.setText(citys[12]);
                c3.setText(citys[13]);
                c4.setText(citys[14]);
                c5.setText(citys[15]);
                if (list_t != null && list_w != null && list_r != null) {
                    t1.setText(list_t.get(11));
                    r1.setText(list_r.get(11));
                    getWeatherImg(list_w.get(11), w1);
                    t2.setText(list_t.get(12));
                    r2.setText(list_r.get(12));
                    getWeatherImg(list_w.get(12), w2);
                    t3.setText(list_t.get(13));
                    r3.setText(list_r.get(13));
                    getWeatherImg(list_w.get(13), w3);
                    t4.setText(list_t.get(14));
                    r4.setText(list_r.get(14));
                    getWeatherImg(list_w.get(14), w4);
                    t5.setText(list_t.get(15));
                    r5.setText(list_r.get(15));
                    getWeatherImg(list_w.get(15), w5);

                }
            } else if (num == 3) {
                c1.setText(citys[16]);
                c2.setText(citys[17]);
                c3.setText(citys[18]);
                if (list_t != null && list_w != null && list_r != null) {
                    t1.setText(list_t.get(16));
                    r1.setText(list_r.get(16));
                    getWeatherImg(list_w.get(16), w1);
                    t2.setText(list_t.get(17));
                    r2.setText(list_r.get(17));
                    getWeatherImg(list_w.get(17), w2);
                    t3.setText(list_t.get(18));
                    r3.setText(list_r.get(18));
                    getWeatherImg(list_w.get(18), w3);

                }
            }
        } catch (Exception e) {
            Log.d(TAG, "show error : " + e.toString());
        }
    }

    private void showTextData(int num) {
        try {
            if (num == 0) {
                showCity(num);
            } else if (num == 1) {
                showCity(num);
            } else if (num == 2) {
                showCity(num);
            } else if (num == 3) {
                showCity(num);
            }
        } catch (Exception e) {
            Log.d(TAG, "showTextData error : " + e.toString());
        }
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

                        direction.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.push_right_in));
                        direction.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.push_left_out));
                    } else if (index == direction_e && index_go == direction_n) {
                        Log.d(TAG, "left");
                        direction.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.push_left_in));
                        direction.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.push_right_out));
                    } else if (index > index_go) {
                        direction.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.push_right_in));
                        direction.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.push_left_out));
                    } else {
                        direction.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.push_left_in));
                        direction.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.push_right_out));
                    }
                    index_go = index;
                    switch (index) {
                        case 0:
                            direction.setBackground(getResources().getDrawable(R.drawable.weather_04_2nav_n_text));
                            map.setBackground(getResources().getDrawable(R.drawable.weather_04_2nav_n_map));
                            weatherCity.setBackground(getResources().getDrawable(R.drawable.weather_04_2nav_n_frame));
                            // weatherCity.getLayoutParams().width = 306; // for
                            // // 628
                            // // 30版
                            // weatherCity.getLayoutParams().height = 366;
                            weatherCity.getLayoutParams().width = 460; // for
                                                                       // 638
                            weatherCity.getLayoutParams().height = 549;
                            layout01.setVisibility(View.VISIBLE);
                            layout02.setVisibility(View.VISIBLE);
                            layout03.setVisibility(View.VISIBLE);
                            layout04.setVisibility(View.VISIBLE);
                            layout05.setVisibility(View.VISIBLE);
                            layout06.setVisibility(View.VISIBLE);
                            layout07.setVisibility(View.VISIBLE);
                            showTextData(0);
                            break;
                        case 1:
                            direction.setBackground(getResources().getDrawable(R.drawable.weather_04_2nav_w_text));
                            map.setBackground(getResources().getDrawable(R.drawable.weather_04_2nav_w_map));
                            weatherCity.setBackground(getResources().getDrawable(R.drawable.weather_04_2nav_w_frame));
                            // weatherCity.getLayoutParams().width = 306; // for
                            // // 628
                            // // 30版
                            // weatherCity.getLayoutParams().height = 224;
                            weatherCity.getLayoutParams().width = 460; // for
                            // 628 31版
                            weatherCity.getLayoutParams().height = 336;
                            layout01.setVisibility(View.VISIBLE);
                            layout02.setVisibility(View.VISIBLE);
                            layout03.setVisibility(View.VISIBLE);
                            layout04.setVisibility(View.VISIBLE);
                            layout05.setVisibility(View.GONE);
                            layout06.setVisibility(View.GONE);
                            layout07.setVisibility(View.GONE);
                            showTextData(1);
                            break;
                        case 2:
                            direction.setBackground(getResources().getDrawable(R.drawable.weather_04_2nav_s_text));
                            map.setBackground(getResources().getDrawable(R.drawable.weather_04_2nav_s_map));
                            weatherCity.setBackground(getResources().getDrawable(R.drawable.weather_04_2nav_s_frame));
                            // weatherCity.getLayoutParams().width = 306; // for
                            // // 628
                            // // 30版
                            // weatherCity.getLayoutParams().height = 271;
                            weatherCity.getLayoutParams().width = 460; // for
                            // 638 31版
                            weatherCity.getLayoutParams().height = 407;
                            layout01.setVisibility(View.VISIBLE);
                            layout02.setVisibility(View.VISIBLE);
                            layout03.setVisibility(View.VISIBLE);
                            layout04.setVisibility(View.VISIBLE);
                            layout05.setVisibility(View.VISIBLE);
                            layout06.setVisibility(View.GONE);
                            layout07.setVisibility(View.GONE);
                            showTextData(2);
                            break;
                        case 3:
                            direction.setBackground(getResources().getDrawable(R.drawable.weather_04_2nav_e_text));
                            map.setBackground(getResources().getDrawable(R.drawable.weather_04_2nav_e_map));
                            weatherCity.setBackground(getResources().getDrawable(R.drawable.weather_04_2nav_e_frame));
                            // weatherCity.getLayoutParams().width = 306; // for
                            // // 628
                            // // 30版
                            // weatherCity.getLayoutParams().height = 177;
                            weatherCity.getLayoutParams().width = 460; // for
                            // 628 31版
                            weatherCity.getLayoutParams().height = 266;
                            layout01.setVisibility(View.VISIBLE);
                            layout02.setVisibility(View.VISIBLE);
                            layout03.setVisibility(View.VISIBLE);
                            layout04.setVisibility(View.GONE);
                            layout05.setVisibility(View.GONE);
                            layout06.setVisibility(View.GONE);
                            layout07.setVisibility(View.GONE);
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
        showCity(0);
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
        Log.d(TAG, "getWeatherImg");
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
        } else {
            Log.d(TAG, "getWeatherImg  ==  null");
        }
    }

}
