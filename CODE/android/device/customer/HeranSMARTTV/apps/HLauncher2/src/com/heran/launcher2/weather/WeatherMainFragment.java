
package com.heran.launcher2.weather;

import java.lang.reflect.Field;
import java.text.SimpleDateFormat;
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
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.Intent;
import android.graphics.drawable.Drawable;
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
    
    private LinearLayout CitySelect_Linear ;

    // -------Middle Area------
    private ImageView green, red, yellow;

    private TextView green_txt, red_txt, yellow_txt;

    // -----氣象區內fragment------
    // weatherFGManger
    private FragmentManager weatherFGManger = null;

    public WeatherMainFragmentToday mWeatherMainFragment_today = null;

    private WeatherMainFragmentWeek mWeatherMainFragment_week = null;

    private WeatherMainFragmentLife mWeatherMainFragment_life = null;

    private WeatherMainFragmentCity mWeatherMainFragment_city = null;

    // --------Variables---------

    private final MyBtnWeatherMainIconFocusListener mBtnWeatherMainIconFocusListener;

    private final MyBtnWeatherMainIconClickListener mBtnWeatherMainIconClickListener;

    private final MyWeatherItemFocusListener mWeatherItemFocusListener;

    private final MyWeatherItemClickListener mWeatherItemClickListener;

    private final MyWeatherOnKeyListener mWeatherOnKeyListener;

    private final Handler mHandler;

    private final static int UPDATE_UI = 0;

    private final static int CHANGE_BG = 1;

    private final StringBuffer recData = new StringBuffer();

    private String tempH, tempL, weatherDesc, currentTemp, WeatherImg;
    
    public int Pos = 0 ; //當前天氣Fragmnet

    public WeatherMainFragment(HomeActivity context) {
        super();
        this.mContext = context;
        this.mHolder = mContext.mviewHolder;
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
        updateWeatherFragment(Constants.WEATHER_TODAY);

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
    
    public void UpDateUI(){
    	mHandler.removeMessages(CHANGE_BG);
        mHandler.sendEmptyMessage(CHANGE_BG);
        mHandler.removeMessages(UPDATE_UI);
        mHandler.sendEmptyMessage(UPDATE_UI);
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
        CitySelect_Linear = (LinearLayout)mView.findViewById(R.id.cityselect_linear);
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
        addViewGlobalLayoutListener(CitySelect_Linear, mViewBean);

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
        CitySelect_Linear.setOnFocusChangeListener(mWeatherItemFocusListener);

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
        CitySelect_Linear.setOnClickListener(mWeatherItemClickListener);
        
        weatherBtn_city.setOnKeyListener(mWeatherOnKeyListener);
        weatherBtn_today.setOnKeyListener(mWeatherOnKeyListener);
        weatherBtn_week.setOnKeyListener(mWeatherOnKeyListener);
        weatherBtn_life.setOnKeyListener(mWeatherOnKeyListener);
        weatherBtn_ad.setOnKeyListener(mWeatherOnKeyListener);
        CitySelect_Linear.setOnKeyListener(mWeatherOnKeyListener);
        videoIcon.setOnKeyListener(mWeatherOnKeyListener);
    }

    private void initDatas() {

        mWeatherMainFragment_today = new WeatherMainFragmentToday();
        mWeatherMainFragment_week = new WeatherMainFragmentWeek();
        mWeatherMainFragment_life = new WeatherMainFragmentLife();
        mWeatherMainFragment_city = new WeatherMainFragmentCity();

        mViewBean.setmCurFocusView(weatherBtn_today);
        weatherBtn_today.requestFocus();
        mViewBean.getmFocusObject().startAnimation(mViewBean.getmCurFocusView());

        weatherPath.setText(getActivity().getResources().getString(R.string.weather_today));
        mHandler.sendEmptyMessage(UPDATE_UI);
    }

    // Fix Me
    private void upDateUi() {
        if (tMin != null) {
            weatherLocal.setText(WeatherHelper.getCurrentCityName(getActivity()));
            tMax.setText(tempH + "↓");
            tMin.setText(tempL + "↑");
            t.setText(currentTemp);
            weather_Desc.setText(weatherDesc);
            getWeatherImg(WeatherImg, w_img);
        }
    }

    private void getWeatherImg(String str, ImageView img) {
        if (str == null) {
            return;
        }
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

    private void LifeLight(int v) {
        if (v == Constants.WEATHER_LIFE) {
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

    class MyBtnWeatherMainIconFocusListener implements OnFocusChangeListener {

        @Override
        public void onFocusChange(View v, boolean focus) {
            if (focus) {
                drawFocus(v);
                switch (v.getId()) {
                    case R.id.index_homebtn_icon01:
                        showThums(mContext.mainLogic.mMenuTips[0],
                                mContext.getResources().getDrawable(R.drawable.thumb_home));
                        break;
                    case R.id.index_homebtn_icon02:
                        showThums(mContext.mainLogic.mMenuTips[1],
                                mContext.getResources().getDrawable(R.drawable.thumb_life));
                        break;
                    case R.id.index_homebtn_icon03:
                        showThums(mContext.mainLogic.mMenuTips[2],
                                mContext.getResources().getDrawable(R.drawable.thumb_apps));
                        break;
                    case R.id.index_homebtn_icon04:
                        showThums(mContext.mainLogic.mMenuTips[3],
                                mContext.getResources().getDrawable(R.drawable.thumb_setting));
                        break;
                    case R.id.index_homebtn_icon05:
                        showThums(mContext.mainLogic.mMenuTips[4],
                                mContext.getResources().getDrawable(R.drawable.thumb_shopping));
                        break;
                }
                mHolder.showThumb();
            } else {
                mHolder.dissMissThumb();
            }
        }
    }

    class MyBtnWeatherMainIconClickListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.index_homebtn_icon01:
                    recordChannelSwitch(1, getActivity().getResources().getString(R.string.home_page));
                    mContext.showViews(Constants.HOMEVIEW);
                    break;
                case R.id.index_homebtn_icon02:
                    recordChannelSwitch(1, getActivity().getResources().getString(R.string.media));
                    mHolder.updateFragment(Constants.LIVINGAREAVIEW);
                    break;
                case R.id.index_homebtn_icon03:
                    recordChannelSwitch(1, getActivity().getResources().getString(R.string.app));
                    mHolder.updateFragment(Constants.APPVIEW);
                    break;
                case R.id.index_homebtn_icon04:
                    recordChannelSwitch(1, getActivity().getResources().getString(R.string.home_page));
                    Intent intent = new Intent("android.intent.action.MAIN");
                    intent.setClassName("com.android.settings", "com.android.settings.SettingsActivity");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                    startActivity(intent);
                    break;
                case R.id.index_homebtn_icon05:
                    recordChannelSwitch(1, getActivity().getResources().getString(R.string.member));
                    String clickUrl = "http://www.jowinwin.com/hertv2msd/index.php?r=member/member";
                    intent = new Intent(mContext, MyWebViewActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("URL", clickUrl);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    break;
                default:
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
                    handler_onlick(0, getActivity().getResources().getString(R.string.weather_today),
                            mContext.getResources().getString(R.string.weather_title_txt),  Constants.WEATHER_TODAY);
                    break;
                case R.id.weather_week:
                    handler_onlick(0, getActivity().getResources().getString(R.string.weather_week),
                            mContext.getResources().getString(R.string.weather_future),  Constants.WEATHER_WEEK);
                    break;
                case R.id.weather_life:
                    handler_onlick(0, getActivity().getResources().getString(R.string.weather_life), null,
                            Constants.WEATHER_LIFE);
                    break;
                case R.id.weather_city:
                    handler_onlick(0, getActivity().getResources().getString(R.string.weather_city),
                            mContext.getResources().getString(R.string.weather_tomorrow), Constants.WEATHER_CITY);
                    break;
                case R.id.weather_ad:
                	
                    break;
                case R.id.cityselect_linear:
                	mHolder.updatePIFragment(Constants.CITYSELECT_OPEN,2);
                    break;
                default:
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
                        }
                        if(keyCode == KeyEvent.KEYCODE_DPAD_LEFT)
                        	CitySelect_Linear.requestFocus();
                        break;
                    case R.id.weather_today:
                        if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                            videoIcon.requestFocus();
                        }
                        if(keyCode == KeyEvent.KEYCODE_DPAD_LEFT)
                        	CitySelect_Linear.requestFocus();
                        break;
                    case R.id.weather_life:
                    	if(keyCode == KeyEvent.KEYCODE_DPAD_LEFT)
                        	CitySelect_Linear.requestFocus();
                    	break;
                    case R.id.weather_week:
                    	if(keyCode == KeyEvent.KEYCODE_DPAD_LEFT)
                        	CitySelect_Linear.requestFocus();
                    	break;
                    case R.id.weather_city:
                    	if(keyCode == KeyEvent.KEYCODE_DPAD_LEFT){
                    		
                    		if(Pos == Constants.WEATHER_CITY){
                    			mWeatherMainFragment_city.direction.requestFocus();
                    			return true ;
                    		}else{
                    			CitySelect_Linear.requestFocus();
                    			return true ;
                    		}	
                    	}
                    	if(keyCode == KeyEvent.KEYCODE_DPAD_RIGHT){
                    		if(Pos == Constants.WEATHER_CITY){
                    			mWeatherMainFragment_city.direction.requestFocus();	
                    			return true ;
                    		}
                    	}
                                       	
                    	break;
                    case R.id.cityselect_linear:
                    	if(keyCode == KeyEvent.KEYCODE_DPAD_RIGHT){
                    		weatherBtn_today.requestFocus();
                    	}                       	
                    	if(keyCode == KeyEvent.KEYCODE_DPAD_UP||keyCode == KeyEvent.KEYCODE_DPAD_DOWN||keyCode == KeyEvent.KEYCODE_DPAD_LEFT){
                    		return true ;
                    	}                    	
                    	break;
                    case R.id.index_homebtn_icon01:
                    	if(keyCode == KeyEvent.KEYCODE_DPAD_LEFT){
                    		memberIcon.requestFocus();
                    		return true ;
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

    private void recordChannelSwitch(int index, String weather) {
        recData.append(HistoryRec.block[8] + ',' + HistoryRec.block9Action[index] + ',' + weather + ',' + "" + ',' + ""
                + ',' + HistoryRec.getCurrentDateTime());
        HistoryRec.writeToFile(recData.toString());
        recData.delete(0, recData.length());
    }

    public void handler_onlick(int index, String weather, String title,int constant) {
        recordChannelSwitch(index, weather);
        weatherPath.setText(weather);
        weatherTitle.setText(title);
        LifeLight(constant);
        updateWeatherFragment(constant);
    }

    private void showThums(String tips, Drawable drawable) {
        mHolder.thum_dsc.setText(tips);
        mHolder.thumb_img.setImageDrawable(drawable);
    }

    private void updateWeatherFragment(int position) {
        weatherFGManger = this.getChildFragmentManager();
        weatherFGManger.popBackStack();
        FragmentTransaction ft = weatherFGManger.beginTransaction();
        Pos = position ;
        switch (position) {

            case Constants.WEATHER_TODAY:
                // ft.remove(mWeatherMainFragment_today);
                ft.replace(R.id.weather_main_right, mWeatherMainFragment_today);
                ft.commitAllowingStateLoss();
                break;
            case Constants.WEATHER_WEEK:
                // ft.remove(mWeatherMainFragment_week);
                ft.replace(R.id.weather_main_right, mWeatherMainFragment_week);
                ft.commitAllowingStateLoss();

                break;
            case Constants.WEATHER_LIFE:
                // ft.remove(mWeatherMainFragment_life);
                ft.replace(R.id.weather_main_right, mWeatherMainFragment_life);
                ft.commitAllowingStateLoss();

                break;
            case Constants.WEATHER_CITY:
                // ft.remove(mWeatherMainFragment_city);
                ft.replace(R.id.weather_main_right, mWeatherMainFragment_city);
                ft.commitAllowingStateLoss();

                break;
        }
    }
}
