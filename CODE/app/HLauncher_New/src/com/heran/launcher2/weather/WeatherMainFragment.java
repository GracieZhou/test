
package com.heran.launcher2.weather;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;

import com.heran.launcher2.HomeActivity;
import com.heran.launcher2.MainViewHolder;
import com.heran.launcher2.PublicFragment;
import com.heran.launcher2.R;
import com.heran.launcher2.eosweb.MyWebViewActivity;
import com.heran.launcher2.util.Constants;
import com.heran.launcher2.util.FocusView;
import com.heran.launcher2.util.HistoryRec;
import com.heran.launcher2.widget.ViewBean;

import android.annotation.SuppressLint;
import android.app.Fragment;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import scifly.provider.SciflyStore.Global;

public class WeatherMainFragment extends PublicFragment {

    private static final String TAG = "WeatherMainFragment";

    private final ViewBean mViewBean;

    private final MainViewHolder mHolder;

    private final HomeActivity mContext;

    // ----------Whole Layout--------
    private LinearLayout weather_main_ll;

    // ------Top Title ------
    private TextView weatherPath;

    private TextView weatherTitle;

    // ------FG Home Page Menu-------

    private ImageButton videoIcon;

    private ImageButton shoppIcon;

    private ImageButton appIcon;

    private ImageButton setIcon;

    private ImageButton memberIcon;

    // ------Weather Menu-------

    public ImageButton weatherBtn_today;

    public ImageButton weatherBtn_week;

    public ImageButton weatherBtn_life;

    public ImageButton weatherBtn_city;

    public ImageButton weatherBtn_ad;

    // ------Left Area---------
    private TextView t, tMax, tMin;

    private ImageView w_img;

    private TextView weatherLocal;

    private TextView weather_Desc;

    // -------Middle Area------
    private ImageView green, red, yellow;

    private TextView green_txt, red_txt, yellow_txt;

    // --------Variables---------

    public ArrayList<WeatherData> list = null;

    public ArrayList<WeatherElement> list_element = null;

    private final MyBtnWeatherMainIconFocusListener mBtnWeatherMainIconFocusListener;

    private final MyBtnWeatherMainIconClickListener mBtnWeatherMainIconClickListener;

    private final MyWeatherItemFocusListener mWeatherItemFocusListener;

    private final MyWeatherItemClickListener mWeatherItemClickListener;

    private final MyWeatherOnKeyListener mWeatherOnKeyListener;

    private final Handler mHandler;

    private final static int UPDATE_UI = 0;

    private final static int CHANGE_BG = 1;

    private String recData = "";

    private String tempH, tempL, weatherDesc, currentTemp, WeatherImg;

    public WeatherMainFragment(HomeActivity context, MainViewHolder mHolder) {
        super();
        this.mContext = context;
        this.mHolder = mHolder;
        mBtnWeatherMainIconFocusListener = new MyBtnWeatherMainIconFocusListener();
        mBtnWeatherMainIconClickListener = new MyBtnWeatherMainIconClickListener();
        mWeatherItemFocusListener = new MyWeatherItemFocusListener();
        mWeatherItemClickListener = new MyWeatherItemClickListener();
        mWeatherOnKeyListener = new MyWeatherOnKeyListener();
        mViewBean = new ViewBean(null, null);
        mHandler = new MyHandler();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Log.d(TAG, "onCreateView");
        View mView = inflater.inflate(R.layout.weather_main_content, container, false);
        findViews(mView);
        registerListeners();
        initDatas();
        return mView;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");
        mHandler.removeMessages(CHANGE_BG);
        mHandler.sendEmptyMessage(CHANGE_BG);
        mHandler.removeMessages(UPDATE_UI);
        mHandler.sendEmptyMessage(UPDATE_UI);
    }

    @Override
    public void onStart() {
        super.onStart();
        mHolder.updateWeatherFragment(Constants.WEATHER_TODAY);

    }

    @SuppressLint("HandlerLeak")
    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UPDATE_UI:
                    upDateUi();
                    break;
                case CHANGE_BG:
                    changBg();
                    break;
                default:
                    break;
            }
        }
    }

    private void findViews(View mView) {

        // Whole Layout
        weather_main_ll = (LinearLayout) mView.findViewById(R.id.weather_main_ll);

        // Top Title
        weatherTitle = (TextView) mView.findViewById(R.id.weather_title_txt);
        weatherPath = (TextView) mView.findViewById(R.id.weather_path);

        // Right Column
        videoIcon = (ImageButton) mView.findViewById(R.id.index_homebtn_icon01);
        shoppIcon = (ImageButton) mView.findViewById(R.id.index_homebtn_icon02);
        appIcon = (ImageButton) mView.findViewById(R.id.index_homebtn_icon03);
        setIcon = (ImageButton) mView.findViewById(R.id.index_homebtn_icon04);
        memberIcon = (ImageButton) mView.findViewById(R.id.index_homebtn_icon05);

        weatherBtn_today = (ImageButton) mView.findViewById(R.id.weather_today);
        weatherBtn_week = (ImageButton) mView.findViewById(R.id.weather_week);
        weatherBtn_life = (ImageButton) mView.findViewById(R.id.weather_life);
        weatherBtn_city = (ImageButton) mView.findViewById(R.id.weather_city);
        weatherBtn_ad = (ImageButton) mView.findViewById(R.id.weather_ad);

        // Left Area
        weatherLocal = (TextView) mView.findViewById(R.id.weather_Local);
        w_img = (ImageView) mView.findViewById(R.id.weather_img);
        weather_Desc = (TextView) mView.findViewById(R.id.weather_desc);
        t = (TextView) mView.findViewById(R.id.textView_t);
        tMax = (TextView) mView.findViewById(R.id.textView_tmax);
        tMin = (TextView) mView.findViewById(R.id.textView_tmin);

        // Middle Area
        green = (ImageView) mView.findViewById(R.id.green);
        red = (ImageView) mView.findViewById(R.id.red);
        yellow = (ImageView) mView.findViewById(R.id.yellow);
        green_txt = (TextView) mView.findViewById(R.id.green_txt);
        red_txt = (TextView) mView.findViewById(R.id.red_txt);
        yellow_txt = (TextView) mView.findViewById(R.id.yellow_txt);

        // View Focus
        FocusView mFocusView = (FocusView) getActivity().findViewById(R.id.fragment_selector);
        mViewBean.setmFocusObject(mFocusView);
    }

    private void registerListeners() {
        addViewGlobalLayoutListener(videoIcon, mViewBean);
        addViewGlobalLayoutListener(shoppIcon, mViewBean);
        addViewGlobalLayoutListener(appIcon, mViewBean);
        addViewGlobalLayoutListener(setIcon, mViewBean);
        addViewGlobalLayoutListener(memberIcon, mViewBean);
        addViewGlobalLayoutListener(weatherBtn_today, mViewBean);
        addViewGlobalLayoutListener(weatherBtn_week, mViewBean);
        addViewGlobalLayoutListener(weatherBtn_life, mViewBean);
        addViewGlobalLayoutListener(weatherBtn_city, mViewBean);
        addViewGlobalLayoutListener(weatherBtn_ad, mViewBean);

        videoIcon.setOnFocusChangeListener(mBtnWeatherMainIconFocusListener);
        shoppIcon.setOnFocusChangeListener(mBtnWeatherMainIconFocusListener);
        appIcon.setOnFocusChangeListener(mBtnWeatherMainIconFocusListener);
        setIcon.setOnFocusChangeListener(mBtnWeatherMainIconFocusListener);
        memberIcon.setOnFocusChangeListener(mBtnWeatherMainIconFocusListener);

        weatherBtn_today.setOnFocusChangeListener(mWeatherItemFocusListener);
        weatherBtn_week.setOnFocusChangeListener(mWeatherItemFocusListener);
        weatherBtn_life.setOnFocusChangeListener(mWeatherItemFocusListener);
        weatherBtn_city.setOnFocusChangeListener(mWeatherItemFocusListener);
        weatherBtn_ad.setOnFocusChangeListener(mWeatherItemFocusListener);

        videoIcon.setOnClickListener(mBtnWeatherMainIconClickListener);
        shoppIcon.setOnClickListener(mBtnWeatherMainIconClickListener);
        appIcon.setOnClickListener(mBtnWeatherMainIconClickListener);
        setIcon.setOnClickListener(mBtnWeatherMainIconClickListener);
        memberIcon.setOnClickListener(mBtnWeatherMainIconClickListener);

        weatherBtn_today.setOnClickListener(mWeatherItemClickListener);
        weatherBtn_week.setOnClickListener(mWeatherItemClickListener);
        weatherBtn_life.setOnClickListener(mWeatherItemClickListener);
        weatherBtn_city.setOnClickListener(mWeatherItemClickListener);
        weatherBtn_ad.setOnClickListener(mWeatherItemClickListener);

        weatherBtn_today.setOnKeyListener(mWeatherOnKeyListener);
        weatherBtn_ad.setOnKeyListener(mWeatherOnKeyListener);
        videoIcon.setOnKeyListener(mWeatherOnKeyListener);
    }

    private void initDatas() {
        mViewBean.setmCurFocusView(weatherBtn_today);
        weatherBtn_today.requestFocus();
        mViewBean.getmFocusObject().startAnimation(mViewBean.getmCurFocusView());

        weatherPath.setText("今日氣象");
        mHandler.sendEmptyMessage(UPDATE_UI);
    }

    public void upDateUi() {
        weatherLocal.setText(getCurrentCity(getActivity()));
        tMax.setText(tempH + "↓");
        tMin.setText(tempL + "↑");
        t.setText(currentTemp);
        weather_Desc.setText(weatherDesc);
        getWeatherImg(WeatherImg, w_img);
    }

    private void getWeatherImg(String str, ImageView img) {
        if (str == null) {
            return;
        }
        Log.d(TAG, "getWeatherImg");
        // 晴天
        if (str.equals("01")) {
            img.setBackground(getActivity().getResources().getDrawable(R.drawable.weather_main_day_icon_01));
        }
        // 晴時多雲
        if (str.equals("02") || str.equals("07") || str.equals("08") || str.equals("43") || str.equals("45")
                || str.equals("46")) {
            img.setBackground(getActivity().getResources().getDrawable(R.drawable.weather_main_day_icon_03));
        }
        // 多雲時陰
        if (str.equals("03") || str.equals("05") || str.equals("06") || str.equals("44") || str.equals("49")) {
            img.setBackground(getActivity().getResources().getDrawable(R.drawable.weather_main_day_icon_snowwing));
        }
        // 有雨
        if (str.equals("04") || str.equals("12") || str.equals("13") || str.equals("17") || str.equals("18")
                || str.equals("24") || str.equals("26") || str.equals("31") || str.equals("34") || str.equals("57")
                || str.equals("58") || str.equals("59") || str.equals("60")) {
            img.setBackground(getActivity().getResources().getDrawable(R.drawable.weather_main_day_icon_raining));
        }
        // 雷雨
        if (str.equals("36")) {
            img.setBackground(getActivity().getResources().getDrawable(R.drawable.weather_main_day_icon_thunder));
        }
    }

    private void LifeLight(int v) {
        if (v == R.id.weather_life) {
            green.setVisibility(View.VISIBLE);
            red.setVisibility(View.VISIBLE);
            yellow.setVisibility(View.VISIBLE);
            green_txt.setVisibility(View.VISIBLE);
            red_txt.setVisibility(View.VISIBLE);
            yellow_txt.setVisibility(View.VISIBLE);
        } else {
            green.setVisibility(View.GONE);
            red.setVisibility(View.GONE);
            yellow.setVisibility(View.GONE);
            green_txt.setVisibility(View.GONE);
            red_txt.setVisibility(View.GONE);
            yellow_txt.setVisibility(View.GONE);
        }
    }

    private void changBg() {
        SimpleDateFormat sdf = new SimpleDateFormat("HH");
        Calendar c = Calendar.getInstance();
        c.setTimeInMillis(System.currentTimeMillis());
        Date current = new Date();
        current.setTime(System.currentTimeMillis());
        int time = Integer.valueOf(sdf.format(current));
        Log.d(TAG, "HH : " + time);
        if (time >= 6 && time < 18) {
            Log.d(TAG, "am");
            weather_main_ll.setBackground(getActivity().getResources().getDrawable(R.drawable.weather_bg_day));
        } else {
            Log.d(TAG, "pm");
            weather_main_ll.setBackground(getActivity().getResources().getDrawable(R.drawable.weather_bg_night));
        }
    }

    public void setData(String tempH, String tempL, String weatherDesc, String currentTemp, String WeatherImg) {
        this.tempH = tempH;
        this.tempL = tempL;
        this.weatherDesc = weatherDesc;
        this.currentTemp = currentTemp;
        this.WeatherImg = WeatherImg;
    }

    private String getCurrentCity(Context mContext) {
        String[] citys = mContext.getResources().getStringArray(R.array.city_array);
        String cityName = citys[0];

        String city[] = Global.getString(mContext.getContentResolver(), Global.CITY_NAME).split(",");
        if (city.length > 2) {
            Constants.city = city[2];
            for (int i = 0; i < citys.length; i++) {
                if (Constants.city.equals(citys[i].substring(0, 2))) {
                    Log.d("city", "city_num[" + i + "] : " + citys[i].substring(0, 2));
                    cityName = citys[i];
                }
            }
        }
        return cityName;
    }

    class MyBtnWeatherMainIconFocusListener implements OnFocusChangeListener {

        @Override
        public void onFocusChange(View v, boolean focus) {
            if (focus) {
                drawFocus(v);
                switch (v.getId()) {
                    case R.id.index_homebtn_icon01:
                        videoIcon.setImageResource(R.drawable.index_icon_01_select);
                        break;
                    case R.id.index_homebtn_icon02:
                        shoppIcon.setImageResource(R.drawable.index_icon_02_select);
                        break;
                    case R.id.index_homebtn_icon03:
                        appIcon.setImageResource(R.drawable.index_icon_03_select);
                        break;
                    case R.id.index_homebtn_icon04:
                        setIcon.setImageResource(R.drawable.index_icon_04_select);
                        break;
                    case R.id.index_homebtn_icon05:
                        memberIcon.setImageResource(R.drawable.index_icon_05_select);
                        break;
                }
            } else {
                videoIcon.setImageResource(R.drawable.index_icon_01_unselect);
                shoppIcon.setImageResource(R.drawable.index_icon_02_unselect);
                appIcon.setImageResource(R.drawable.index_icon_03_unselect);
                setIcon.setImageResource(R.drawable.index_icon_04_unselect);
                memberIcon.setImageResource(R.drawable.index_icon_05_unselect);
            }
        }

    }

    class MyBtnWeatherMainIconClickListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.index_homebtn_icon01:
                    recData = HistoryRec.block[8] + ',' + HistoryRec.block9Action[1] + ',' + "首頁" + ',' + "" + ',' + ""
                            + ',' + HistoryRec.getCurrentDateTime();
                    HistoryRec.writeToFile(recData);
                    recData = "";
                    mContext.showViews(Constants.HOMEVIEW);
                    break;
                case R.id.index_homebtn_icon02:
                    recData = HistoryRec.block[8] + ',' + HistoryRec.block9Action[1] + ',' + "多媒體" + ',' + "" + ',' + ""
                            + ',' + HistoryRec.getCurrentDateTime();
                    HistoryRec.writeToFile(recData);
                    recData = "";
                    mHolder.updateFragment(Constants.LIVINGAREAVIEW);
                    break;
                case R.id.index_homebtn_icon03:
                    recData = HistoryRec.block[8] + ',' + HistoryRec.block9Action[1] + ',' + "app" + ',' + "" + ',' + ""
                            + ',' + HistoryRec.getCurrentDateTime();
                    HistoryRec.writeToFile(recData);
                    recData = "";
                    mHolder.updateFragment(Constants.APPVIEW);
                    break;
                case R.id.index_homebtn_icon04:
                    recData = HistoryRec.block[8] + ',' + HistoryRec.block9Action[1] + ',' + "設置" + ',' + "" + ',' + ""
                            + ',' + HistoryRec.getCurrentDateTime();
                    HistoryRec.writeToFile(recData);
                    recData = "";
                    Intent intent = new Intent("android.intent.action.MAIN");
                    intent.setClassName("com.android.settings", "com.android.settings.SettingsActivity");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                    startActivity(intent);
                    break;
                case R.id.index_homebtn_icon05:
                    recData = HistoryRec.block[8] + ',' + HistoryRec.block9Action[1] + ',' + "會員登入" + ',' + "" + ','
                            + "" + ',' + HistoryRec.getCurrentDateTime();
                    HistoryRec.writeToFile(recData);
                    recData = "";
                    String clickUrl = "http://www.jowinwin.com/hertv2msd/index.php?r=member/member";
                    intent = new Intent(mContext, MyWebViewActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("URL", clickUrl);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    break;
            }

        }

    }

    class MyWeatherItemFocusListener implements OnFocusChangeListener {

        @Override
        public void onFocusChange(View v, boolean focus) {
            drawFocus(v);

        }

    }

    class MyWeatherItemClickListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.weather_today:
                    recData = HistoryRec.block[8] + ',' + HistoryRec.block9Action[0] + ',' + "今日氣象" + ',' + "" + ','
                            + "" + ',' + HistoryRec.getCurrentDateTime();
                    HistoryRec.writeToFile(recData);
                    recData = "";
                    weatherPath.setText("今日氣象");
                    weatherTitle.setText("未來24小時氣象概況");
                    LifeLight(v.getId());
                    mHolder.updateWeatherFragment(Constants.WEATHER_TODAY);

                    break;
                case R.id.weather_week:
                    recData = HistoryRec.block[8] + ',' + HistoryRec.block9Action[0] + ',' + "一周氣象" + ',' + "" + ','
                            + "" + ',' + HistoryRec.getCurrentDateTime();
                    HistoryRec.writeToFile(recData);
                    recData = "";
                    weatherPath.setText("一周氣象");
                    weatherTitle.setText("未來一周氣象");
                    LifeLight(v.getId());
                    mHolder.updateWeatherFragment(Constants.WEATHER_WEEK);
                    break;
                case R.id.weather_life:
                    recData = HistoryRec.block[8] + ',' + HistoryRec.block9Action[0] + ',' + "生活氣象" + ',' + "" + ','
                            + "" + ',' + HistoryRec.getCurrentDateTime();
                    HistoryRec.writeToFile(recData);
                    recData = "";
                    weatherPath.setText("生活氣象");
                    weatherTitle.setText("生活氣象");
                    LifeLight(v.getId());
                    mHolder.updateWeatherFragment(Constants.WEATHER_LIFE);
                    break;
                case R.id.weather_city:
                    recData = HistoryRec.block[8] + ',' + HistoryRec.block9Action[0] + ',' + "主要城市天氣" + ',' + "" + ','
                            + "" + ',' + HistoryRec.getCurrentDateTime();
                    HistoryRec.writeToFile(recData);
                    recData = "";
                    weatherPath.setText("主要城市天氣");
                    weatherTitle.setText("明日主要城市天氣");
                    LifeLight(v.getId());
                    mHolder.updateWeatherFragment(Constants.WEATHER_CITY);
                    break;

                case R.id.weather_ad:
                    break;
            }

        }

    }

    class MyWeatherOnKeyListener implements OnKeyListener {

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                switch (v.getId()) {
                    case R.id.weather_ad:
                        if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                            mHolder.fragmentBtn.requestFocus();
                            mHolder.drawFocus(mHolder.fragmentBtn);
                        }
                        break;
                    case R.id.weather_today:
                        if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                            videoIcon.requestFocus();
                        }
                        break;
                }
            }
            return false;
        }

    }

    private void drawFocus(View view) {
        mViewBean.setmCurFocusView(view);
        mViewBean.getmFocusObject().startAnimation(view);
    }

    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        Log.d(TAG, "onResume");
        super.onPause();

        if (list != null) {
            list = null;
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.v(TAG, "onDestroy() ");
    }

    @Override
    public void onDetach() {
        super.onDetach();

        try {
            Field childFragmentManager = Fragment.class.getDeclaredField("mChildFragmentManager");
            childFragmentManager.setAccessible(true);
            childFragmentManager.set(this, null);

        } catch (NoSuchFieldException e) {
            throw new RuntimeException(e);
        } catch (IllegalAccessException e) {
            throw new RuntimeException(e);
        }
    }

}
