
package com.heran.launcher2;

import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;

import com.heran.launcher2.advert.MyAD;
import com.heran.launcher2.apps.AppFragment;
import com.heran.launcher2.eosweb.MyWebViewActivity;
import com.heran.launcher2.lifearea.EntertainmentFragment1;
import com.heran.launcher2.lifearea.EntertainmentFragment3;
import com.heran.launcher2.lifearea.LivingAreaFragment;
import com.heran.launcher2.message.MessageFragment;
import com.heran.launcher2.message.MessageMainFragment;
import com.heran.launcher2.news.NewsFragment;
import com.heran.launcher2.news.NewsMainFragment;
import com.heran.launcher2.smalltv.SmallTvFragment;
import com.heran.launcher2.util.Constants;
import com.heran.launcher2.util.FlipperAnimation;
import com.heran.launcher2.util.FocusView;
import com.heran.launcher2.util.HistoryRec;
import com.heran.launcher2.util.Tools;
import com.heran.launcher2.util.UIUtil;
import com.heran.launcher2.util.Utils;
import com.heran.launcher2.weather.CityselectFragment;
import com.heran.launcher2.weather.WeatherFragment;
import com.heran.launcher2.weather.WeatherMainFragment;
import com.heran.launcher2.widget.ViewBean;
import com.mstar.android.tv.TvCommonManager;

import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.content.ContentResolver;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.animation.AccelerateInterpolator;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.Animation.AnimationListener;
import android.view.animation.AnimationUtils;
import android.webkit.EOSWebView;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ViewFlipper;
import scifly.provider.SciflyStatistics;
import scifly.provider.SciflyStore;
import scifly.provider.metadata.Usr;

/**
 * projectName： HLauncher2 moduleName：MainViewHolder.java
 * 
 * @author laird.li
 * @version 1.0.0
 * @time 2016-03-30
 * @Copyright © 2016 Eos, Inc.
 */
public class MainViewHolder {

    private static final String TAG = "MainViewHolder";

    private final HomeActivity mContext;

    private final Handler mHandler;

    // ------FG Home Choice-------

    public ImageButton videoIcon;

    private ImageButton shoppIcon;

    private ImageButton appIcon;

    private ImageButton setIcon;

    private ImageButton memberIcon;

    private BtnHomeIconFocusListener mBtnHomeIconFocusListener;

    private BtnHomeIconClickListener mBtnHomeIconClickListener;

    private BtnHomeIconOnKeyListener mBtnHomeIconOnKeyListener;

    // ---- ---Home Ads-------------

    public ImageButton mBt1;

    public ImageButton mBt2;

    public int animType;

    public int animTime;

    public ViewFlipper mFlipper = null;

    public FlipperAnimation mFlipperAnimation;

    private AdBtnOnKeyListener mAdBtnOnKeyListener;

    private AdBtnOnClickListener mAdBtnOnClickListener;

    // --------more btn--------
    public ImageButton fragmentBtn;

    public ViewBean mViewBean;

    public MyMoreOnClickListener myMoreOnClickListener;

    public MyMoreOnKeyListener myMoreOnKeyListener;

    // ------- view focus--------------
    public ViewBean getmViewBean() {
        return mViewBean;
    }

    public void setmViewBean(ViewBean mBean) {
        this.mViewBean = mBean;
    }

    public BTn_MyFocusChangeListener mFocusChangeListener;

    // ------webview

    protected EOSWebView mAdWebView;

    protected ProgressBar prgBar;

    // ---------time-----
    private TextView date, dayOfWeek, aPm, currentTime;

    // ---------weather
    public ImageView mWeather;

    public TextView mDegree;

    public TextView mLocation;

    public LinearLayout home_content = null;

    public FrameLayout layout_other = null;

    public LinearLayout layout_home = null;

    public LinearLayout menu_content = null;

    public ImageView thumb_img = null;

    public TextView thum_dsc = null;
    
    public LinearLayout thum_lin ;

    // pi content
    public FrameLayout framelayout_fragment = null;

    // mFGManger
    public FragmentManager mFGManger = null;

    // mPIFGManger
    public FragmentManager mPIFGManger = null;

    public SmallTvFragment mHomeFragment = null;

    public AppFragment mAppFragment = null;

    public LivingAreaFragment mLivingAreaFragment = null;

    public EntertainmentFragment1 mEntertainmentFragment1 = null;

    public EntertainmentFragment3 mEntertainmentFragment3 = null;

    public NewsMainFragment mNewsMainFragment = null;

    public WeatherMainFragment mWeatherMainFragment = null;

    // ------------------------
    // 新聞看板
    public NewsFragment mNewsFragment = null;

    // 天氣看板
    public WeatherFragment mWeatherFragment = null;

    // 訊息看板
    public MessageFragment mMessageFragment = null;

    // 訊息專區
    public MessageMainFragment mMessageMainFragment = null;
    public CityselectFragment mCityselectFragment = null;

    // 讯息区左侧图标
    private ImageView titleName;

    // 当前讯息器位置
    private int mNowPIPosition = -1;

    // 上一次讯息器位置
    private int mLastPIPosition = -1;

    private int mLastPosition = -1;
    private int mPrePosition = -1;
    private ImageView ToLeft_View , ToRight_View ;

    // 目錄 預覽圖
    private int mLastMenuOnclickViews = R.id.index_homebtn_icon01;

    public boolean pipBtnFocus = false;

    public boolean getPipBtnFocus() {
        return pipBtnFocus;
    }

    public void setPipBtnFocus(boolean pipBtnFocus) {
        this.pipBtnFocus = pipBtnFocus;
    }

    public int getmLastPosition() {
        return mLastPosition;
    }
    
    public int getmPrePosition() {
        return mPrePosition;
    }

    public int getmLastPIPosition() {
        return mLastPIPosition;
    }

    public int getLastMenuOnclickViews() {
        return this.mLastMenuOnclickViews;
    }

    // save focus view when loop moving
    private HashMap<String, View> mTmpViews;

    public MainViewHolder(HomeActivity content, Handler mHandler) {
        this.mContext = content;
        this.mHandler = mHandler;
        this.mFGManger = content.getFragmentManager();
        this.mPIFGManger = content.getFragmentManager();
    }

    public void initView() {

        //
        mViewBean = new ViewBean(null, null);
        FocusView mFocusView = (FocusView) mContext.findViewById(R.id.fragment_selector);
        mViewBean.setmFocusObject(mFocusView);

        // init message
        titleName = (ImageView) mContext.findViewById(R.id.title_name);
        home_content = (LinearLayout) mContext.findViewById(R.id.home_content);
        framelayout_fragment = (FrameLayout) mContext.findViewById(R.id.framelayout_fragment);
        layout_other = (FrameLayout) mContext.findViewById(R.id.layout_other);
        layout_home = (LinearLayout) mContext.findViewById(R.id.layout_home);
        thumb_img = (ImageView) mContext.findViewById(R.id.thumb_img);
        thum_dsc = (TextView) mContext.findViewById(R.id.thumb_dsc);
        thum_lin = (LinearLayout)mContext.findViewById(R.id.thumb_liner);
        fragmentBtn = (ImageButton) mContext.findViewById(R.id.fragmentBtn);
        ToLeft_View = (ImageView)mContext.findViewById(R.id.left_view);
        ToRight_View = (ImageView)mContext.findViewById(R.id.right_view);

        // init 5 btns
        videoIcon = (ImageButton) mContext.findViewById(R.id.index_homebtn_icon01);
        shoppIcon = (ImageButton) mContext.findViewById(R.id.index_homebtn_icon02);
        appIcon = (ImageButton) mContext.findViewById(R.id.index_homebtn_icon03);
        setIcon = (ImageButton) mContext.findViewById(R.id.index_homebtn_icon04);
        memberIcon = (ImageButton) mContext.findViewById(R.id.index_homebtn_icon05);
        videoIcon.setImageResource(R.drawable.index_icon_01_select);

        // init data
        date = (TextView) mContext.findViewById(R.id.tv_date);
        dayOfWeek = (TextView) mContext.findViewById(R.id.tv_dayOfWeek);
        aPm = (TextView) mContext.findViewById(R.id.tv_aPm);
        currentTime = (TextView) mContext.findViewById(R.id.tv_currentTime);

        // init webview
        mAdWebView = (EOSWebView) mContext.findViewById(R.id.ad_webview);
        prgBar = (ProgressBar) mContext.findViewById(R.id.ad_prg);
        mFlipper = (ViewFlipper) mContext.findViewById(R.id.flipper);
        mTmpViews = new HashMap<String, View>();
        initFragment();
        fadeOutAndHideImage(ToLeft_View);
        fadeOutAndHideImage(ToRight_View);
    }

    // init Fragment
    private void initFragment() {
        mHomeFragment = new SmallTvFragment(mHandler, mContext);
        mAppFragment = new AppFragment(mContext);
        mLivingAreaFragment = new LivingAreaFragment(mContext);
        mEntertainmentFragment1 = new EntertainmentFragment1(mContext);
        mEntertainmentFragment3 = new EntertainmentFragment3(mContext);
        mNewsFragment = new NewsFragment(mContext);
        mWeatherFragment = new WeatherFragment(mContext);
        mMessageFragment = new MessageFragment(mContext);
        mMessageMainFragment = new MessageMainFragment(mContext);
        mCityselectFragment = new CityselectFragment(mContext);
        
        mNewsMainFragment = new NewsMainFragment(mContext);
        mWeatherMainFragment = new WeatherMainFragment(mContext);

    }

    public void registerViewListener() {

        mBtnHomeIconFocusListener = new BtnHomeIconFocusListener();
        mBtnHomeIconClickListener = new BtnHomeIconClickListener();
        mBtnHomeIconOnKeyListener = new BtnHomeIconOnKeyListener();
        mAdBtnOnKeyListener = new AdBtnOnKeyListener();
        myMoreOnClickListener = new MyMoreOnClickListener();
        myMoreOnKeyListener = new MyMoreOnKeyListener();
        mFocusChangeListener = new BTn_MyFocusChangeListener();
        mAdBtnOnClickListener = new AdBtnOnClickListener();

        fragmentBtn.setOnClickListener(myMoreOnClickListener);
        fragmentBtn.setOnKeyListener(myMoreOnKeyListener);
        fragmentBtn.setOnFocusChangeListener(mBtnHomeIconFocusListener);

        videoIcon.setOnFocusChangeListener(mBtnHomeIconFocusListener);
        shoppIcon.setOnFocusChangeListener(mBtnHomeIconFocusListener);
        appIcon.setOnFocusChangeListener(mBtnHomeIconFocusListener);
        setIcon.setOnFocusChangeListener(mBtnHomeIconFocusListener);
        memberIcon.setOnFocusChangeListener(mBtnHomeIconFocusListener);

        videoIcon.setOnClickListener(mBtnHomeIconClickListener);
        shoppIcon.setOnClickListener(mBtnHomeIconClickListener);
        appIcon.setOnClickListener(mBtnHomeIconClickListener);
        setIcon.setOnClickListener(mBtnHomeIconClickListener);
        memberIcon.setOnClickListener(mBtnHomeIconClickListener);

        videoIcon.setOnKeyListener(mBtnHomeIconOnKeyListener);
        shoppIcon.setOnKeyListener(mBtnHomeIconOnKeyListener);
        appIcon.setOnKeyListener(mBtnHomeIconOnKeyListener);
        memberIcon.setOnKeyListener(mBtnHomeIconOnKeyListener);
        setIcon.setOnKeyListener(mBtnHomeIconOnKeyListener);

    }

    /**
     * show osd message
     * 
     * @param osdmsg
     */

    public void cleanActivityFocus(HomeActivity mContext) {
        getmViewBean().setmCurFocusView(null);
    }

    public void clearAllFragment(FragmentTransaction ft) {
        ft.remove(mHomeFragment);
        ft.remove(mAppFragment);
        ft.remove(mLivingAreaFragment);
        ft.remove(mEntertainmentFragment1);
        ft.remove(mEntertainmentFragment1);
        ft.remove(mEntertainmentFragment3);
        ft.remove(mMessageMainFragment);
        ft.remove(mNewsMainFragment);
        ft.remove(mWeatherMainFragment);
    }

    /**
     * update the fragment when press the key right or left.You do the different
     * animation.
     * 
     * @param position
     */
    public void updateFragment(int position) {
        // position :前往畫面 mLastPosition :現在畫面
        if (mLastPosition == position) {
            return;
        }
        mFGManger.popBackStack();
        // 設定畫面移動的方向動畫
        FragmentTransaction ft = mFGManger.beginTransaction();
        switch (position) {
            case Constants.HOMEVIEW:
                layout_other.setVisibility(View.INVISIBLE);
                layout_home.setVisibility(View.VISIBLE);
                unselectHombtnBackGround();
                mLastMenuOnclickViews = R.id.index_homebtn_icon01;
                selectHombtnBackGround();
                clearAllFragment(ft);
                ft.replace(R.id.home_content, mHomeFragment);
                ft.commitAllowingStateLoss();
                break;
            case Constants.LIVINGAREAVIEW:
                layout_other.setVisibility(View.INVISIBLE);
                layout_home.setVisibility(View.VISIBLE);
                unselectHombtnBackGround();
                mLastMenuOnclickViews = R.id.index_homebtn_icon02;
                selectHombtnBackGround();
                if (HomeActivity.mVersion.equals("4.2.1") || HomeActivity.mVersion.equals("5.0.1")) {
                    // try {
                    // Constants.kok_device =
                    // TvManager.getInstance().getFactoryManager().getKokExist();
                    // } catch (TvCommonException e) {
                    // e.printStackTrace();
                    // }
                }

                clearAllFragment(ft);
                ft.replace(R.id.home_content, mLivingAreaFragment);
                ft.commitAllowingStateLoss();
                break;
            case Constants.APPVIEW:
                layout_other.setVisibility(View.INVISIBLE);
                layout_home.setVisibility(View.VISIBLE);
                clearAllFragment(ft);
                unselectHombtnBackGround();
                mLastMenuOnclickViews = R.id.index_homebtn_icon03;
                selectHombtnBackGround();
                ft.replace(R.id.home_content, mAppFragment);
                ft.commitAllowingStateLoss();
                break;

            case Constants.MESSAGEFRAGMENT:
                layout_other.setVisibility(View.INVISIBLE);
                layout_home.setVisibility(View.VISIBLE);
                unselectHombtnBackGround();
                mLastMenuOnclickViews = 0;               
                clearAllFragment(ft);
                ft.replace(R.id.home_content, mMessageMainFragment);
                ft.commitAllowingStateLoss();
                break;

            case Constants.ENTERTAINMENT1:
                ft.remove(mEntertainmentFragment1);
                ft.replace(R.id.home_content, mEntertainmentFragment1);
                ft.commitAllowingStateLoss();

                break;
            case Constants.ENTERTAINMENT3:
                ft.remove(mEntertainmentFragment3);
                ft.replace(R.id.home_content, mEntertainmentFragment3);
                ft.commitAllowingStateLoss();

                break;
            case Constants.NEWSMAINFRAGMENT:
            	unselectHombtnBackGround();
            	mLastMenuOnclickViews = 0;
                layout_other.setVisibility(View.VISIBLE);
                layout_home.setVisibility(View.INVISIBLE);
                clearAllFragment(ft);
                ft.setCustomAnimations(R.anim.eos_menu_anim_leftin, R.anim.eos_menu_anim_rightout);
                ft.replace(R.id.layout_other, mNewsMainFragment);
                ft.commitAllowingStateLoss();
                break;
            case Constants.WEATHERMAINFRAGMENT:
            	unselectHombtnBackGround();
            	mLastMenuOnclickViews = 0;
                layout_other.setVisibility(View.VISIBLE);
                layout_home.setVisibility(View.INVISIBLE);
                clearAllFragment(ft);
                ft.setCustomAnimations(R.anim.eos_menu_anim_leftin, R.anim.eos_menu_anim_rightout);
                ft.replace(R.id.layout_other, mWeatherMainFragment);
                ft.commitAllowingStateLoss();
                break;
            default:
                break;
        }
        if(mLastPosition!=Constants.MESSAGEFRAGMENT&&mLastPosition!=Constants.NEWSMAINFRAGMENT
           &&mLastPosition!=Constants.WEATHERMAINFRAGMENT)
        {
        	mPrePosition = mLastPosition;
        }        
        // 執行完成 當前畫面指定為最後畫面
        mLastPosition = position;
    }

    /**
     * First,save HomeFragment、AppFragmen、LivingFragment Focus; Then set
     * HomeFragment、AppFragmen、LivingFragment Focus to null
     */

    // 清除所有Focus
    public void cleanFragmentAllFocus() {

        if (mHomeFragment != null) {
            mTmpViews.put("HomeFragment", mHomeFragment.getmViewBean().getmCurFocusView());
            mHomeFragment.getmViewBean().setmCurFocusView(null);
        }
        if (mAppFragment != null) {
            mTmpViews.put("AppFragment", mAppFragment.getmViewBean().getmCurFocusView());
            mAppFragment.getmViewBean().setmCurFocusView(null);
        }
        if (mLivingAreaFragment != null) {
            mTmpViews.put("MediaFragment", mLivingAreaFragment.getmViewBean().getmCurFocusView());
            mLivingAreaFragment.getmViewBean().setmCurFocusView(null);
        }
    }

    /**
     * restore HomeFragment、AppFragmen、MediaFragment Focus
     */
    public void restoreFragmentFocus() {
        // when mTmpViews is empty,set a view for mark ,to avoid first start the
        // app,then pressed left key ,the focus view at the wrong place
        if (mTmpViews.get("HomeFragment") == null) {
            mTmpViews.put("HomeFragment", home_content);
        }
        mHomeFragment.getmViewBean().setmCurFocusView(mTmpViews.get("HomeFragment"));

        if (mTmpViews.get("AppFragment") == null) {
            mTmpViews.put("AppFragment", home_content);
        }
        mAppFragment.getmViewBean().setmCurFocusView(mTmpViews.get("AppFragment"));

        if (mTmpViews.get("MediaFragment") == null) {
            mTmpViews.put("MediaFragment", home_content);
        }
        mLivingAreaFragment.getmViewBean().setmCurFocusView(mTmpViews.get("MediaFragment"));

        if (mTmpViews.get("ShopFragment") == null) {
            mTmpViews.put("ShopFragment", home_content);
        }
    }

    // 由HomeActivity呼叫設定輪播
    public void updatePIFragment(int position, int i) {

        if (mLastPIPosition == position) {
            return;
        }
        mPIFGManger.popBackStack();
        FragmentTransaction ft = mPIFGManger.beginTransaction();

        if (i == 0) {
            ft.setCustomAnimations(R.anim.eos_menu_anim_rightin, R.anim.eos_menu_anim_leftout);
        } else if (i == 1) {
            ft.setCustomAnimations(R.anim.eos_menu_anim_leftin, R.anim.eos_menu_anim_rightout);
        } else if (i == 2) {
            // 一開始進入 不設動畫
        }

        switch (position) {

            case Constants.NEWSVIEW:

                ft.replace(R.id.framelayout_fragment, mNewsFragment);
                ft.commitAllowingStateLoss();

                titleName.setImageResource(R.drawable.index_bottom_nws_title);
                fragmentBtn.setImageResource(R.drawable.index_bottom_nws_btw);

                break;
            case Constants.WEATHERVIEW:
                ft.replace(R.id.framelayout_fragment, mWeatherFragment);
                ft.commitAllowingStateLoss();

                titleName.setImageResource(R.drawable.index_bottom_weather_title);
                fragmentBtn.setImageResource(R.drawable.index_bottom_weather_btw);

                break;
            case Constants.MESSAGEVIEW:
                ft.replace(R.id.framelayout_fragment, mMessageFragment);
                ft.commitAllowingStateLoss();

                titleName.setImageResource(R.drawable.system_message);
                fragmentBtn.setImageResource(R.drawable.index_bottom_nws_btw);
                break;
            default:
                break;
            case Constants.CITYSELECT_OPEN:
                // ft.remove(mWeatherMainFragment_city);
                ft.replace(R.id.city_frame, mCityselectFragment );
                ft.commitAllowingStateLoss();
                break;
            case Constants.CITYSELECT_CLOSE:
                ft.remove(mCityselectFragment);
                ft.commitAllowingStateLoss();
                break;
        }
        if(i!=2){
        mHandler.removeMessages(Constants.SHOW_NEWVIEWS);
        mHandler.removeMessages(Constants.PIFRAGMENTCHANGE);
        }
      
        //城市選擇 預防下面訊息無法移動
        if (position == Constants.NEWSVIEW || position == Constants.WEATHERVIEW || position == Constants.MESSAGEVIEW
        	) {

            mNowPIPosition = mLastPIPosition;

            mLastPIPosition = position;
        }
    }

    public int getNowPIPosition() {
        return mNowPIPosition;
    }
    private void fadeOutAndHideImage(final ImageView img){
        Animation fadeOut = new AlphaAnimation(1, 0);
        fadeOut.setInterpolator(new AccelerateInterpolator());
        fadeOut.setDuration(800);
        fadeOut.setStartOffset(1000);

        fadeOut.setAnimationListener(new AnimationListener()
        {
            @Override
            public void onAnimationEnd(Animation animation) 
            {

                  fadeInAndShowImage(img);
            }
            @Override
            public void onAnimationRepeat(Animation animation) {}
            @Override
            public void onAnimationStart(Animation animation) {}
        });

        img.startAnimation(fadeOut);
    }
    private void fadeInAndShowImage(final ImageView img){
        Animation fadeIn = new AlphaAnimation(0, 1);
        fadeIn.setInterpolator(new AccelerateInterpolator());
        fadeIn.setDuration(500);

        fadeIn.setAnimationListener(new AnimationListener()
        {
            @Override
            public void onAnimationEnd(Animation animation) 
            {

					fadeOutAndHideImage(img);
                  
            }
            @Override
            public void onAnimationRepeat(Animation animation) {}
            @Override
            public void onAnimationStart(Animation animation) {}
        });

        img.startAnimation(fadeIn);
    }
    
    class BtnHomeIconOnKeyListener implements OnKeyListener {

        @Override
        public boolean onKey(View view, int keyCode, KeyEvent event) {
            if (getmLastPosition() == Constants.MESSAGEFRAGMENT) {
                return false;
            }
            Log.d(TAG, "view.getId():" + view.getId());
            Log.d(TAG, "mLastMenuOnclickViews:" + mLastMenuOnclickViews);
            if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                switch (view.getId()) {
                	case R.id.index_homebtn_icon01:
                	   	if(mLastPosition==Constants.APPVIEW){                   	
                            mAppFragment.getPopApp().requestFocus();  
                    		}
                		break;
                    case R.id.index_homebtn_icon02:
                        if (mLastMenuOnclickViews == R.id.index_homebtn_icon01) {
                            mHomeFragment.getImageTV().requestFocus();
                        } else {
                            videoIcon.requestFocus();
                        }
                        break;
                    case R.id.index_homebtn_icon03:
                        if (mLastMenuOnclickViews == R.id.index_homebtn_icon02) {
                            videoIcon.requestFocus();
                        } else {
                            shoppIcon.requestFocus();
                        }
                        break;
                    case R.id.index_homebtn_icon05:
                        if (mLastMenuOnclickViews == R.id.index_homebtn_icon03) {
                            shoppIcon.requestFocus();
                        } else {
                            appIcon.requestFocus();
                        }
                        break;
                    case R.id.index_homebtn_icon04:
                        memberIcon.requestFocus();
                        break;
                    default:
                        return false;
                }
                return true;

            } else if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                switch (view.getId()) {
                    case R.id.index_homebtn_icon01:
                        if (mLastMenuOnclickViews == R.id.index_homebtn_icon02) {
                            appIcon.requestFocus();
                        } else {
                            shoppIcon.requestFocus();
                        }
                        break;
                    case R.id.index_homebtn_icon02:
                        if (mLastMenuOnclickViews == R.id.index_homebtn_icon03) {
                            memberIcon.requestFocus();
                        } else {
                            appIcon.requestFocus();
                        }
                        break;
                    case R.id.index_homebtn_icon03:
                        memberIcon.requestFocus();
                        break;
                    case R.id.index_homebtn_icon05:
                        setIcon.requestFocus();
                        break;
                    case R.id.index_homebtn_icon04:
                        if (mLastMenuOnclickViews == R.id.index_homebtn_icon01) {
                            shoppIcon.requestFocus();
                        } else {
                            videoIcon.requestFocus();
                        }
                        break;
                    default:
                        return false;
                }
                return true;
            }
            return false;
        }

    }

    class BtnHomeIconFocusListener implements OnFocusChangeListener {

        @Override
        public void onFocusChange(View v, boolean focus) {
            if (focus) {
                drawFocus(v);
                switch (v.getId()) {
                    case R.id.index_homebtn_icon01:
//                        if (getLastMenuOnclickViews() == R.id.index_homebtn_icon01) {
//                        	homeIcon.setImageResource(R.drawable.index_icon_01_select);
//                        } else {
                        videoIcon.setImageResource(R.drawable.index_icon_01_unselect);
//                        }
                        thum_dsc.setText(mContext.mainLogic.mMenuTips[0]);
                        thumb_img.setImageDrawable(mContext.getResources().getDrawable(R.drawable.thumb_home));
                        showThumb();
                        break;
                    case R.id.index_homebtn_icon02:
                        shoppIcon.setImageResource(R.drawable.index_icon_02_unselect);
                        thum_dsc.setText(mContext.mainLogic.mMenuTips[1]);
                        thumb_img.setImageDrawable(mContext.getResources().getDrawable(R.drawable.thumb_life));
                        showThumb();
                        break;
                    case R.id.index_homebtn_icon03:

                        thum_dsc.setText(mContext.mainLogic.mMenuTips[2]);
                        appIcon.setImageResource(R.drawable.index_icon_03_unselect);
                        thumb_img.setImageDrawable(mContext.getResources().getDrawable(R.drawable.thumb_apps));
                        showThumb();
                        break;
                    case R.id.index_homebtn_icon05:
                        thum_dsc.setText(mContext.mainLogic.mMenuTips[3]);
                        memberIcon.setImageResource(R.drawable.index_icon_05_unselect);
                        thumb_img.setImageDrawable(mContext.getResources().getDrawable(R.drawable.thumb_shopping));
                        showThumb();
                        break;
                    case R.id.index_homebtn_icon04:
                        thum_dsc.setText(mContext.mainLogic.mMenuTips[4]);
                        setIcon.setImageResource(R.drawable.index_icon_04_unselect);
                        thumb_img.setImageDrawable(mContext.getResources().getDrawable(R.drawable.thumb_setting));
                        showThumb();
                        break;
                    default:
                        break;
                }
            } else {
                dissMissThumb();
            }

        }

    }

    public void showThumb() {
        thum_lin.setVisibility(View.VISIBLE);
        thumb_img.setVisibility(View.VISIBLE);
    }

    public void dissMissThumb() {
        thum_lin.setVisibility(View.GONE);
        thumb_img.setVisibility(View.GONE);
    }

    class BtnHomeIconClickListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            String recData;
            Intent intent;
            switch (v.getId()) {
                case R.id.index_homebtn_icon01:
                    mContext.showViews(Constants.HOMEVIEW);
                    break;
                case R.id.index_homebtn_icon02:
                    mContext.showViews(Constants.LIVINGAREAVIEW);
                    break;
                case R.id.index_homebtn_icon03:
                    recData = HistoryRec.block[0] + ',' + HistoryRec.block1Action[5] + ','
                            + mContext.getResources().getString(R.string.app) + ',' + "" + ',' + "" + ','
                            + HistoryRec.getCurrentDateTime();
                    HistoryRec.writeToFile(recData);
                    recData = "";
                    mContext.showViews(Constants.APPVIEW);
                    break;
                case R.id.index_homebtn_icon04:
                    recData = HistoryRec.block[0] + ',' + HistoryRec.block1Action[5] + ','
                            + mContext.getResources().getString(R.string.setting) + ',' + "" + ',' + "" + ','
                            + HistoryRec.getCurrentDateTime();
                    HistoryRec.writeToFile(recData);
                    recData = "";
                    intent = new Intent("android.intent.action.MAIN");
                    intent.setClassName("com.android.settings", "com.android.settings.SettingsActivity");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                    mContext.startActivity(intent);
                    break;
                case R.id.index_homebtn_icon05:
                    recData = HistoryRec.block[0] + ',' + HistoryRec.block1Action[5] + ','
                            + mContext.getResources().getString(R.string.member) + ',' + "" + ',' + "" + ','
                            + HistoryRec.getCurrentDateTime();
                    HistoryRec.writeToFile(recData);
                    recData = "";
                    String clickUrl = "http://www.jowinwin.com/hertv2msd/index.php?r=member/member";
                    intent = new Intent(mContext, MyWebViewActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("URL", clickUrl);
                    intent.putExtras(bundle);
                    mContext.startActivity(intent);
                    break;
            }
        }

    }

    public void unselectHombtnBackGround() {
        switch (getLastMenuOnclickViews()) {
            case R.id.index_homebtn_icon01:
                videoIcon.setImageResource(R.drawable.index_icon_01_unselect);
                break;
            case R.id.index_homebtn_icon02:
                shoppIcon.setImageResource(R.drawable.index_icon_02_unselect);
                break;
            case R.id.index_homebtn_icon03:
                appIcon.setImageResource(R.drawable.index_icon_03_unselect);
                break;
            default:
                break;
        }
    }

    public void selectHombtnBackGround() {
        switch (getLastMenuOnclickViews()) {
            case R.id.index_homebtn_icon01:
                videoIcon.setImageResource(R.drawable.index_icon_01_select);
                break;
            case R.id.index_homebtn_icon02:
                shoppIcon.setImageResource(R.drawable.index_icon_02_select);
                break;
            case R.id.index_homebtn_icon03:
                appIcon.setImageResource(R.drawable.index_icon_03_select);
                break;
            default:
                break;
        }
    }

    public void setHomeBtnFocus(int index) {
        switch (index) {
            case 0:
                videoIcon.requestFocus();
                break;
            case 1:
                shoppIcon.requestFocus();
                break;
            default:
                break;
        }
    }

    protected void updateDateInfo() {

        SimpleDateFormat sdf = new SimpleDateFormat(mContext.getResources().getString(R.string.DateFormat));

        Calendar mCalendar = Calendar.getInstance();
        mCalendar.setTimeInMillis(System.currentTimeMillis());
        Date current = new Date();
        current.setTime(System.currentTimeMillis());
        date.setText(sdf.format(current));
        
        //判斷為24或12小時制
        ContentResolver cv = mContext.getContentResolver();
        String strTimeFormat = android.provider.Settings.System.getString(cv,
                android.provider.Settings.System.TIME_12_24);
        sdf = null;        
        if(strTimeFormat==null){
        	strTimeFormat = "12";
        }
        if(strTimeFormat.equals("24")){
        	sdf = new SimpleDateFormat("HH : mm");
        	aPm.setText("");
        }else{
        	sdf = new SimpleDateFormat("hh : mm");       	       	
        	aPm.setText((mCalendar.get(Calendar.AM_PM) == 0) ? "AM" : "PM");
        }
        dayOfWeek.setText("/"+Tools.dateToWeek(mContext, current));
        currentTime.setText(sdf.format(current));

    }

    /**
     * initFlipper add views and listener for each
     */
    @SuppressWarnings("deprecation")
    public void initFlipper() {
        mFlipperAnimation = new FlipperAnimation(mContext, mFlipper);
        int adcount = mContext.mainLogic.homeAdInfoList.size();
        Log.d(TAG, "adcount:" + adcount);
        if (adcount <= 0) {
            return;
        }
        for (int i = 0; i < (adcount / 2); i++) {
            View mview = LayoutInflater.from(mContext).inflate(R.layout.ad_item, null);
            MyAD adinf1 = mContext.mainLogic.homeAdInfoList.get(i);
            mBt1 = (ImageButton) mview.findViewById(R.id.bt1);
            Log.d(TAG, "with:" + mBt1.getWidth() + "--height:" + mBt1.getHeight());
            HomeApplication.getInstance().glideLoadGif(mContext, adinf1.getPic(), mBt1);
            mBt1.setOnFocusChangeListener(mFocusChangeListener);
            mBt1.setOnClickListener(mAdBtnOnClickListener);
            mBt1.setOnKeyListener(mAdBtnOnKeyListener);

            MyAD adinf2 = mContext.mainLogic.homeAdInfoList.get((adcount - 1) - i);
            mBt2 = (ImageButton) mview.findViewById(R.id.bt2);
            HomeApplication.getInstance().glideLoadGif(mContext, adinf2.getPic(), mBt2);
            mBt2.setOnFocusChangeListener(mFocusChangeListener);
            mBt2.setOnClickListener(mAdBtnOnClickListener);
            mBt2.setOnKeyListener(mAdBtnOnKeyListener);
            mFlipper.addView(mview);
        } // end of for
        animType = mContext.mainLogic.homeAdInfoList.get(0).getPlt();
        animTime = mContext.mainLogic.homeAdInfoList.get(0).getDit();
        mFlipper.setDisplayedChild(0);
    }

    public void drawFocus(View view) {
        mViewBean.setmCurFocusView(view);
        mViewBean.getmFocusObject().startAnimation(view);
    }

    public void udpateViewFlipper(int position) {
        // if the viewflipper is null or empty,return
        if (mFlipper == null || mFlipper.getChildCount() == 0) {
            return;
        }
        int count = mContext.mainLogic.homeAdInfoList.size();
        MyAD tmpAd = mContext.mainLogic.homeAdInfoList.get(position);
        ImageButton tmpBt = null;
        if (position < (count / 2)) {
            View tmpView = mFlipper.getChildAt(position);
            tmpBt = (ImageButton) tmpView.findViewById(R.id.bt1);
            Log.d(TAG, "weight" + tmpBt.getWidth() + "--height:" + tmpBt.getHeight());
        } else {
            View tmpView = mFlipper.getChildAt((count - 1) - position);
            tmpBt = (ImageButton) tmpView.findViewById(R.id.bt2);
        }
        HomeApplication.getInstance().glideLoadGif(mContext, tmpAd.getPic(), tmpBt);
    }

    class AdBtnOnKeyListener implements OnKeyListener {

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            // if the fragment animation is running,return true
            if (mAdWebView.isFocused()) {
                return true;
            }
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                	if(mLastPosition==Constants.APPVIEW){
                		switch (v.getId()) {
                    case R.id.bt1:
                        break;
                    case R.id.bt2:
                        mAppFragment.getAppSetting().requestFocus();
                        break;  
                		}
                	}
                    mContext.playVoice(KeyEvent.KEYCODE_DPAD_LEFT);
                    mHandler.removeMessages(Constants.INIT_HOMEFRAGMENT_VIEWFLIPPER);
                    mHandler.sendEmptyMessageDelayed(Constants.INIT_HOMEFRAGMENT_VIEWFLIPPER,
                            Constants.DELAY_HOMEFRAGMENT_VIEWFLIPPER);
                } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT && !UIUtil.isQuickDoubleClick(keyCode)) {
                    mFlipper.setInAnimation(
                            AnimationUtils.loadAnimation(mContext.getApplication(), R.anim.push_left_in));
                    mFlipper.setOutAnimation(
                            AnimationUtils.loadAnimation(mContext.getApplication(), R.anim.push_left_out));
                    mFlipper.showPrevious();
                    v.requestFocus();
                    mViewBean.getmFocusObject().startAnimation(v);
                } else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                    switch (v.getId()) {
                        case R.id.bt1:
                            mHandler.removeMessages(Constants.INIT_HOMEFRAGMENT_VIEWFLIPPER);
                            mHandler.sendEmptyMessageDelayed(Constants.INIT_HOMEFRAGMENT_VIEWFLIPPER,
                                    Constants.DELAY_HOMEFRAGMENT_VIEWFLIPPER);
                            if (mLastMenuOnclickViews == R.id.index_homebtn_icon01
                                    && Constants.MESSAGEFRAGMENT != getmLastPosition()) {
                                shoppIcon.requestFocus();
                                Log.d(TAG, "shoppIcon requestFocus ");
                            } else {
                                videoIcon.requestFocus();
                                Log.d(TAG, "video requestFocus ");
                            }
                            break;
                        case R.id.bt2:
                            break;
                    }
                } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                    switch (v.getId()) {
                        case R.id.bt1:
                            break;
                        case R.id.bt2:
                            mHandler.removeMessages(Constants.INIT_HOMEFRAGMENT_VIEWFLIPPER);
                            mHandler.sendEmptyMessageDelayed(Constants.INIT_HOMEFRAGMENT_VIEWFLIPPER,
                                    Constants.DELAY_HOMEFRAGMENT_VIEWFLIPPER);
                            fragmentBtn.requestFocus();
                            break;
                    }
                }
            }
            return false;
        }
    }

    public class MyMoreOnKeyListener implements OnKeyListener {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                if (!UIUtil.isQuickDoubleClick(keyCode)) {
                    switch (getmLastPIPosition()) {
                        case Constants.NEWSVIEW:
                            updatePIFragment(Constants.MESSAGEVIEW, 0);

                            break;
                        case Constants.WEATHERVIEW:
                            updatePIFragment(Constants.NEWSVIEW, 0);

                            break;
                        case Constants.MESSAGEVIEW:
                            updatePIFragment(Constants.WEATHERVIEW, 0);
                            break;
                    }

                }
                fragmentBtn.requestFocus();

                return true;
            }

            else if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                if (!UIUtil.isQuickDoubleClick(keyCode)) {
                    switch (getmLastPIPosition()) {
                        case Constants.NEWSVIEW:
                            updatePIFragment(Constants.WEATHERVIEW, 1);
                            break;
                        case Constants.WEATHERVIEW:
                            updatePIFragment(Constants.MESSAGEVIEW, 1);
                            break;
                        case Constants.MESSAGEVIEW:
                            updatePIFragment(Constants.NEWSVIEW, 1);
                            break;
                    }
                }
                fragmentBtn.requestFocus();

                return true;
            }

            else if (event.getAction() == KeyEvent.ACTION_DOWN && keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                return true;
            }

            return false;
        }

    }

    public class MyMoreOnClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            String recData;
            mViewBean.setmCurFocusView(null);
            switch (getmLastPIPosition()) {
                case Constants.NEWSVIEW:
                    if (!Utils.isNetworkState) {
                        UIUtil.toastShow(R.string.shop_no_network, mContext);
                        return;
                    }
                    if(mLastPosition==Constants.NEWSMAINFRAGMENT){                   	
                    	mNewsMainFragment.mHandler.removeMessages(mNewsMainFragment.UPDATE_NEWS_RIGTHT);
                    	mNewsMainFragment.mHandler.sendEmptyMessage(mNewsMainFragment.UPDATE_NEWS_RIGTHT);
                    	mNewsMainFragment.mHandler.removeMessages(mNewsMainFragment.UPDATE_NEWS_LEFT);
                    	mNewsMainFragment.mHandler.sendEmptyMessage(mNewsMainFragment.UPDATE_NEWS_LEFT);
                    	mNewsMainFragment.nextPage.requestFocus();
                    }else{
                    	mContext.showViews(Constants.NEWSMAINFRAGMENT);
                    	
                    }
                    	recData = HistoryRec.block[9] + ',' + HistoryRec.block10Action[0] + ',' + "新聞" + ',' + "" + ',' + ""
                            + ',' + HistoryRec.getCurrentDateTime();
                    HistoryRec.writeToFile(recData);
                    recData = "";
                    break;
                case Constants.WEATHERVIEW:
                    if (!Utils.isNetworkState) {
                        UIUtil.toastShow(R.string.shop_no_network, mContext);
                        return;
                    }
                    mContext.showViews(Constants.WEATHERMAINFRAGMENT);
                    recData = HistoryRec.block[9] + ',' + HistoryRec.block10Action[0] + ',' + "氣象" + ',' + "" + ',' + ""
                            + ',' + HistoryRec.getCurrentDateTime();
                    HistoryRec.writeToFile(recData);
                    recData = "";
                    break;
                case Constants.MESSAGEVIEW:
                    if (!Utils.isNetworkState) {
                        UIUtil.toastShow(R.string.shop_no_network, mContext);
                        return;
                    }
                    mContext.showViews(Constants.MESSAGEFRAGMENT);
                    break;

            }
        }

    }

    /**
     * handler focus change
     */
    class BTn_MyFocusChangeListener implements OnFocusChangeListener {

        @Override
        public void onFocusChange(View view, boolean flag) {
            if (flag) {
                drawFocus(view);
            }
        }
    }

    class AdBtnOnClickListener implements OnClickListener {

        @Override
        public void onClick(View view) {
            int currpage = mFlipper.getDisplayedChild();
            Log.d(TAG, "currpage = " + currpage); // 8個廣告共 Page 0~3
            MyAD adinfo = null;
            String recData;
            switch (view.getId()) {
                case R.id.bt1: // just to ad web page 沒有網路 跳出提示
                    if (!Utils.isNetworkState) {
                        UIUtil.toastShow(R.string.shop_no_network, mContext);
                        return;
                    }
                    mContext.sendMonkey(KeyEvent.KEYCODE_DPAD_LEFT);
                    adinfo = mContext.mainLogic.homeAdInfoList.get(currpage);
                    if (adinfo != null) {
                        if (adinfo.getTi() != null && adinfo.getTi().contains("WebPage")) {
                            mContext.goToWebUrl(adinfo.getGln());
                            SciflyStatistics.getInstance(mContext).recordEvent(mContext, "adClickRecord", "adUrl",
                                    adinfo.getGln());
                        } else {
                            mAdWebView.setVisibility(View.GONE);
                            mContext.setLoadURL(adinfo.getGln());
                            mContext.handlertv.postDelayed(mContext.handleLoadURL, 1000);
                            mContext.setToChangeInputSource(TvCommonManager.INPUT_SOURCE_STORAGE);
                        }
                    }
                    recData = HistoryRec.block[0] + ',' + HistoryRec.block1Action[3] + ',' + "" + ',' + "" + ','
                            + mContext.loadURL + ',' + HistoryRec.getCurrentDateTime();
                    HistoryRec.writeToFile(recData);
                    recData = "";

                    break;
                case R.id.bt2:
                    if (!Utils.isNetworkState) {
                        UIUtil.toastShow(R.string.shop_no_network, mContext);
                        return;
                    }
                    mContext.sendMonkey(KeyEvent.KEYCODE_DPAD_LEFT);
                    adinfo = mContext.mainLogic.homeAdInfoList
                            .get((mContext.mainLogic.homeAdInfoList.size() - 1) - currpage);
                    if (adinfo != null) {
                        if (adinfo.getTi() != null && adinfo.getTi().contains("WebPage")) {
                            mContext.goToWebUrl(adinfo.getGln());
                            SciflyStatistics.getInstance(mContext).recordEvent(mContext, "adClickRecord", "adUrl",
                                    adinfo.getGln());
                        } else {
                            mAdWebView.setVisibility(View.GONE);
                            mContext.setLoadURL(adinfo.getGln());
                            mContext.handlertv.postDelayed(mContext.handleLoadURL, 1000);
                            mContext.setToChangeInputSource(TvCommonManager.INPUT_SOURCE_STORAGE);
                        }
                    }
                    recData = HistoryRec.block[0] + ',' + HistoryRec.block1Action[3] + ',' + "" + ',' + "" + ','
                            + mContext.loadURL + ',' + HistoryRec.getCurrentDateTime();
                    HistoryRec.writeToFile(recData);
                    recData = "";
                    break;
                default:
                    break;
            }
        }

    }
    
    /**
     * show user name
     */
    public void showUserName() {
        Usr user = SciflyStore.User.getUser(mContext.getContentResolver());
        if (user != null) {
            Log.d(TAG, "showUserName if " + user);
            if (user.mName == null) {
                user.mName = "Guest";
            }
        } else {
            Log.d(TAG, "showUserName else " + user);
            user = new Usr();
            user.mName = "Guest";
        }
//        if(mBikeFragment!=null&&ADFunction.getInstance().getXBikeConnectionStatus()==ADFunction.XBikeConnectionStatusConnected){       	
//        	
//        	if(!Constants.USERNAME.equals(user.mName)){
//        	mBikeFragment.GetJson =true;
//        	StopSport();
//        	BikeUploadData.PutDataToJowin(mContext);
//        	}        	       	      	
//        }
        Constants.USERNAME = user.mName;    
    }

}
