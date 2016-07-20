
package com.google.tv.eoslauncher.ui.fragment;

import java.io.IOException;
import java.lang.reflect.InvocationTargetException;
import java.util.HashMap;
import java.util.Map;

import scifly.provider.SciflyStatistics;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemProperties;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.webkit.ConsoleMessage;
import android.webkit.EOSWebView;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.ViewFlipper;

import com.google.tv.eoslauncher.HomeActivity;
import com.google.tv.eoslauncher.HomeApplication;
import com.google.tv.eoslauncher.MainViewHolder;
import com.google.tv.eoslauncher.R;
import com.google.tv.eoslauncher.business.MyEOSWebClient;
import com.google.tv.eoslauncher.business.MyWebViewClient;
import com.google.tv.eoslauncher.model.MyAD;
import com.google.tv.eoslauncher.model.ViewBean;
import com.google.tv.eoslauncher.ui.app.AppStoreActivity;
import com.google.tv.eoslauncher.util.ChannelInfoView;
import com.google.tv.eoslauncher.util.ChannelManagerExt;
import com.google.tv.eoslauncher.util.Constants;
import com.google.tv.eoslauncher.util.FlipperAnimation;
import com.google.tv.eoslauncher.util.FocusView;
import com.google.tv.eoslauncher.util.GoogleAnalyticsUtil;
import com.google.tv.eoslauncher.util.HistoryRec;
import com.google.tv.eoslauncher.util.ScreenProtection;
import com.google.tv.eoslauncher.util.UIUtil;
import com.google.tv.eoslauncher.util.Utils;
import com.mstar.android.tv.TvCommonManager;
import com.mstar.android.tvapi.common.TvManager;
import com.mstar.android.tvapi.common.exception.TvCommonException;
import com.mstar.android.tvapi.common.vo.ProgramInfo;
import com.mstar.android.tvapi.common.vo.TvOsType.EnumInputSource;
import com.nostra13.universalimageloader.core.DisplayImageOptions;
import com.nostra13.universalimageloader.core.assist.ImageScaleType;

/**
 * projectName： EosLauncher moduleName： HomeFragment.java
 * 
 * @author chadm.xiang
 * @version 1.0.0
 * @time 2013-12-18 下午2:00:31
 * @Copyright © 2013 Eos Inc.
 */
@SuppressLint("InflateParams")
@SuppressWarnings("deprecation")
public class HomeFragment extends PublicFragment {

    private final static String TAG = "HomeFragment";

    // help tip obj
    private ImageView mHelp_tip;

    // home Ad GridView obj
    private ViewFlipper mFlipper = null;

    // tv sur
    private SurfaceView mTv_sur;

    // imageTV
    private ImageButton mImageTV;

    // FocusChangeListener
    private MyFocusChangeListener mFocusChangeListener;

    // OnClickListener
    private MyOnClickListener mOnClickListener;

    private HomeActivity mContext;

    private ImageButton mBt1;

    private ImageButton mBt2;

    // the two btns in the first view of viewflipperfor for draw foucus view
    private ImageButton mBt1_tmp;

    private ImageButton mBt2_tmp;

    private MainViewHolder mHolder;

    private MyHandler myHandler;

    private ViewBean mViewBean;

    private BtnOneOnKeyListener mBtn1OnKeyListener;

    private BtnTwoOnKeyListener mBtn2OnKeyListener;

    private FlipperAnimation mFlipperAnimation;

    private TextView msignalTip;

    private int animType; // animation type

    private int animTime;

    private static final int SELECTCHANNEL = 0x101;

    private static final int ADD_CHANNEL_INFOVIEW = 0x102;

    private static final int LOAD_CHANNEL = 0x103;
    
    private static final int TV_LISTENER_READY = 0x104;

    private ChannelManagerExt mChannelManagerExt;

    // ------------- add by Jason ------------
    private String hRecString = "";

    private String hRecBlock = "";

    // ---------------------------------------

    // Current input source.
    private EnumInputSource mCurInputSource = EnumInputSource.E_INPUT_SOURCE_NONE;

    private StringBuffer mSelectChannelNum = new StringBuffer();

    private static final int DELAYSELECTCHANNEL = 3000;

    private FrameLayout parent;

    private ChannelInfoView mChannelInfoView;

    // webview ad
    protected EOSWebView mAdWebView;

    private FrameLayout frameLayout = null;

    private WebChromeClient.CustomViewCallback myCallBack = null;

    private View myView = null;

    private ProgressBar prgBar;

    private MyWebViewClient mWebViewClien = null;

    private String loadURL = "http://219.87.154.38/jowintest/index.php?r=activity/hertv1";

    private Handler homeHandler = null;

    private boolean isClickAd = false;
    
//    private String recData;

    public String getLoadURL() {
        return loadURL;
    }

    public void setLoadURL(String loadURL) {
        this.loadURL = loadURL;
        // ---------------------- add by Jason
        // -------------------------------------------------------
        if (HistoryRec.BFS != null) {
            if (HistoryRec.BFS.get(2).functionStatus) {
                // if(bAdHistory) {
                hRecString = hRecString + HistoryRec.block1Action[3] + ',' + loadURL + ',';
                // 取得目前日期和時間的字串
                hRecString = hRecString + HistoryRec.getCurrentDateTime();
                HistoryRec.writeToFile(hRecBlock + hRecString);
                hRecString = "";
            }
        }
        // -------------------------------------------------------------------------------------------
    }

    protected static final FrameLayout.LayoutParams COVER_SCREEN_PARAMS = new FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

    private MyWebChromeClient mWebChromeClient = null;

    private FrameLayout mVideoView = null;

    public ViewBean getmViewBean() {
        return mViewBean;
    }

    public void setmViewBean(ViewBean mBean) {
        this.mViewBean = mBean;
    }

    @SuppressLint("HandlerLeak")
    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Constants.INIT_HOMEFRAGMENT_VIEWFLIPPER:
                    if (mViewBean.getmCurFocusView() != null
                            && (mViewBean.getmCurFocusView().getId() != mImageTV.getId())) {
                        return;
                    }
                    mFlipperAnimation.loadFlipperAnimation(animType);
                    // play ads
                    myHandler.removeMessages(Constants.INIT_HOMEFRAGMENT_VIEWFLIPPER);
                    myHandler.sendEmptyMessageDelayed(Constants.INIT_HOMEFRAGMENT_VIEWFLIPPER, animTime);
                    break;
                case SELECTCHANNEL:
                    int index = mChannelManagerExt.getChannelNums().indexOf(msg.arg1);
                    mSelectChannelNum = new StringBuffer();
                    if (index != -1 && mChannelInfoView != null) {
                        mChannelManagerExt.programSel(index);
                        mChannelInfoView.channelChange(mChannelManagerExt.getCurInfo());
                        if (mCurInputSource == EnumInputSource.E_INPUT_SOURCE_ATV) {
                            Map<String, String> map = new HashMap<String, String>();
                            map.put("channelNum", index + "");
                            map.put("channelName", mChannelManagerExt.getCurProgramInfo().serviceName);
                            SciflyStatistics.getInstance(mContext).recordEvent(getActivity(), "atvWatchRecord", map);
                            SciflyStatistics.getInstance(mContext).recordEvent(getActivity(), "atvChangeChannelRecord",
                                    "channelNum", index + "");
                        } else if (mCurInputSource == EnumInputSource.E_INPUT_SOURCE_DTV) {
                            Map<String, String> map = new HashMap<String, String>();
                            map.put("channelNum", index + "");
                            map.put("channelName", mChannelManagerExt.getCurProgramInfo().serviceName);
                            SciflyStatistics.getInstance(mContext).recordEvent(getActivity(), "dtvWatchRecord", map);
                            SciflyStatistics.getInstance(mContext).recordEvent(getActivity(), "dtvChangeChannelRecord",
                                    "channelNum", index + "");
                        }
                    }
                    if (HistoryRec.BFS != null) {
                        if (HistoryRec.BFS.get(0).functionStatus) {
                            // if(bChannelSwitchHistory) {
                            try {
                                mCurInputSource = TvManager.getInstance().getCurrentInputSource();
                            } catch (TvCommonException e) {
                                // TODO Auto-generated catch block
                                e.printStackTrace();
                            }
                            recordChannelSwitch();
                        }
                    }
                    // ---------------------------------------------------------------------------------------------------------------------------------

                    break;
                case LOAD_CHANNEL:
                    if (mCurInputSource == EnumInputSource.E_INPUT_SOURCE_STORAGE) {
                        mCurInputSource = EnumInputSource.values()[mContext.queryCurInputSrc()];
                    }
                    Log.v(TAG, "mCurInputSource = " + mCurInputSource);
                    if (mCurInputSource == EnumInputSource.E_INPUT_SOURCE_ATV
                            || mCurInputSource == EnumInputSource.E_INPUT_SOURCE_DTV) {
                        mChannelManagerExt.getAllChannels(getActivity(), mCurInputSource);
                        myHandler.sendEmptyMessage(ADD_CHANNEL_INFOVIEW);
                    }
                    break;
                case ADD_CHANNEL_INFOVIEW:
                    if (mChannelInfoView == null && mChannelManagerExt.getCurInfo() != null && parent != null) {
                        long time = System.currentTimeMillis();
                        mChannelInfoView = new ChannelInfoView(getActivity(), mChannelManagerExt.getCurInfo());
                        parent.addView(mChannelInfoView);
                        if (mCurInputSource == EnumInputSource.E_INPUT_SOURCE_ATV) {
                            Map<String, String> map = new HashMap<String, String>();
                            map.put("channelNum", mChannelManagerExt.getCurProgramInfo().number + "");
                            map.put("channelName", mChannelManagerExt.getCurProgramInfo().serviceName);
                            SciflyStatistics.getInstance(mContext).recordEvent(getActivity(), "atvWatchRecord", map);
                        } else if (mCurInputSource == EnumInputSource.E_INPUT_SOURCE_DTV) {
                            Map<String, String> map = new HashMap<String, String>();
                            map.put("channelNum", mChannelManagerExt.getCurProgramInfo().number + "");
                            map.put("channelName", mChannelManagerExt.getCurProgramInfo().serviceName);
                            SciflyStatistics.getInstance(mContext).recordEvent(getActivity(), "dtvWatchRecord", map);
                        }
                        Log.v(TAG, "addview time = " + (System.currentTimeMillis() - time));
                    }
                    break;
                case TV_LISTENER_READY:
                    TvCommonManager.getInstance().setTvosCommonCommand("TVEventListenerReady");
                    break;
                default:
                    break;
            }
        }
    }

    public HomeFragment() {
        super();
    }

    public HomeFragment(Handler mHandler, HomeActivity context, MainViewHolder mainViewHolder) {
        super();
        this.mContext = context;
        this.homeHandler = mHandler;
        this.mHolder = mainViewHolder;
        mFocusChangeListener = new MyFocusChangeListener();
        mOnClickListener = new MyOnClickListener();
        mBtn1OnKeyListener = new BtnOneOnKeyListener();
        mBtn2OnKeyListener = new BtnTwoOnKeyListener();
        myHandler = new MyHandler();
        mViewBean = new ViewBean(null, null);
        mChannelManagerExt = ChannelManagerExt.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mView = inflater.inflate(R.layout.home_main, container, false);
        FocusView mFocusView = (FocusView) mView.findViewById(R.id.home_selector);
        mViewBean.setmFocusObject(mFocusView);
        mFlipper = (ViewFlipper) mView.findViewById(R.id.flipper);
        mImageTV = (ImageButton) mView.findViewById(R.id.imagetv);
        mHelp_tip = (ImageView) mView.findViewById(R.id.help_tip);
        msignalTip = (TextView) mView.findViewById(R.id.signal_tips);
        parent = (FrameLayout) mView.findViewById(R.id.home_layout);

        // the animation when mHelp_tip show
        mHelp_tip.setVisibility(View.VISIBLE);
        AnimatorSet set = new AnimatorSet();
        ObjectAnimator anim1 = ObjectAnimator.ofFloat(mHelp_tip, "alpha", 0f, 1f);
        anim1.setDuration(2000);
        ObjectAnimator anim2 = ObjectAnimator.ofFloat(mHelp_tip, "alpha", 1f, 1f);
        anim2.setDuration(6000);
        ObjectAnimator anim3 = ObjectAnimator.ofFloat(mHelp_tip, "alpha", 1f, 0f);
        anim3.setDuration(2000);
        set.play(anim1).before(anim2);
        set.play(anim3).after(anim2);
        set.start();

        mTv_sur = (SurfaceView) mView.findViewById(R.id.tv_sur);
        mTv_sur.setBackgroundColor(Color.BLACK);
        openSurfaceView();

        setImageTVListeners();
        initFlipper();

        if (mViewBean.getmCurFocusView() == null) {
            mViewBean.setmCurFocusView(mImageTV);
            mImageTV.requestFocus();
            mViewBean.getmFocusObject().startAnimation(mViewBean.getmCurFocusView());
        } else if (mViewBean.getmCurFocusView() == mHolder.home_content) {
            // if curFoucesView is the mark set in restoreFragmentFocus,set the
            // curFoucesView to the left view
            mViewBean.setmCurFocusView(mBt1_tmp);
        }
        Log.d(TAG, "HomeFragment onCreateView");
        // when first create and mImageTV has focus,start ads animation
        if (mViewBean.getmCurFocusView().getId() == mImageTV.getId()) {
            myHandler.sendEmptyMessageDelayed(Constants.INIT_HOMEFRAGMENT_VIEWFLIPPER,
                    Constants.DELAY_HOMEFRAGMENT_VIEWFLIPPER);
        }
        setRetainInstance(true);
        frameLayout = (FrameLayout) mView.findViewById(R.id.framelayout);
        mAdWebView = (EOSWebView) mView.findViewById(R.id.ad_webview);
        prgBar = (ProgressBar) mView.findViewById(R.id.ad_prg);

        // -------- add by Jason ------------------------
        hRecBlock = HistoryRec.block[0] + ',';
        // ----------------------------------------------

        if (android.provider.Settings.System.getString(mContext.getContentResolver(), "IAgree") == null) {
            android.provider.Settings.System.putString(mContext.getContentResolver(), "IAgree", Constants.PPasswd);
            Constants.IAgree = android.provider.Settings.System.getString(mContext.getContentResolver(), "IAgree");
        } else {
            Constants.IAgree = android.provider.Settings.System.getString(mContext.getContentResolver(), "IAgree");
        }

        if (!Constants.IAgree.equalsIgnoreCase("1") && !Constants.CheckJowin) {
            Infomsbox(R.string.enter_allapp_dialog_title, R.string.enter_allapp_dialog_content);
        }

        return mView;
    }

    @SuppressLint("SetJavaScriptEnabled")
    public void loadUrl() {
        mAdWebView.setVisibility(View.VISIBLE);
        prgBar.setVisibility(View.VISIBLE);
        mImageTV.setFocusable(false);
        mBt1.setFocusable(false);
        mBt2.setFocusable(false);

        mAdWebView.clearCache(true);
        mAdWebView.setInitialScale(HomeApplication.getInstance().getResources()
                .getInteger(R.integer.webview_init_scale));
        mAdWebView.getSettings().setJavaScriptEnabled(true);
        mAdWebView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        mAdWebView.getSettings().setCacheMode(WebSettings.LOAD_NO_CACHE);
        mAdWebView.getSettings().setLayoutAlgorithm(WebSettings.LayoutAlgorithm.NARROW_COLUMNS);
        mAdWebView.getSettings().setDefaultTextEncodingName("utf-8");
        mAdWebView.setHorizontalScrollBarEnabled(false);
        mAdWebView.setVerticalScrollBarEnabled(false);
        final String USER_AGENT_STRING = mAdWebView.getSettings().getUserAgentString() + " Rong/2.0";
        mAdWebView.getSettings().setUserAgentString(USER_AGENT_STRING);
        mAdWebView.getSettings().setSupportZoom(false);
        mAdWebView.getSettings().setPluginState(WebSettings.PluginState.ON);
        mAdWebView.getSettings().setLoadWithOverviewMode(true);

        // add WebChromeClient to support play HTML5 video
        mWebChromeClient = new MyWebChromeClient();
        mAdWebView.setWebChromeClient(mWebChromeClient);

        mWebViewClien = new MyWebViewClient(mAdWebView, prgBar, mContext);
        mWebViewClien.setHomePage(loadURL);
        mAdWebView.setWebViewClient(mWebViewClien);

        MyEOSWebClient eosWebClient = new MyEOSWebClient(mContext, mAdWebView, this);
        mAdWebView.setEosWebClient(eosWebClient);

        mAdWebView.setFocusable(true);
        mAdWebView.requestFocus();
        Log.v("adm", "mAdWebView.addJavascriptInterface");
        try {
            mAdWebView.getClass().getMethod("onResume").invoke(mAdWebView, (Object[]) null);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.v(TAG, "homefragment onResume");
        // AdActitiy KEYCODE_BACK
        mContext.backHomeSource();
        new Thread() {
            public void run() {
                try {
                    mCurInputSource = TvManager.getInstance().getCurrentInputSource();
                    myHandler.sendEmptyMessageDelayed(LOAD_CHANNEL, 2000);

                } catch (TvCommonException e) {
                    e.printStackTrace();
                }
            }
        }.start();
        
        SciflyStatistics.getInstance(mContext).recordPageStart(getActivity(), "HomeFragment");
        myHandler.sendEmptyMessage(TV_LISTENER_READY);

    }

    @Override
    public void onPause() {
        super.onPause();
        String activityName = Utils.getCurrentActivity(getActivity());
        EnumInputSource tmpInputSource = EnumInputSource.E_INPUT_SOURCE_NONE;
        try {
            tmpInputSource = TvManager.getInstance().getCurrentInputSource();
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        if (activityName.equals("com.eostek.hotkey.EosInputActivity") && mCurInputSource != tmpInputSource) {
            parent.removeView(mChannelInfoView);
            mChannelInfoView = null;
        } else if (activityName.equals("com.eostek.tv.player.PlayerActivity")) {
            mContext.setFullscale();
        }
        mAdWebView.onPause();
        mAdWebView.setVisibility(View.GONE);
        Log.v(TAG, "homefragment onPause " + ";currentActivity = " + activityName);
        SciflyStatistics.getInstance(mContext).recordPageEnd();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mFlipper.removeAllViews();
        System.gc();
        ScreenProtection.getInstance().stop();
        Log.v(TAG, "homefragment onDestroy");
    }

    @Override
    public void onStop() {
        super.onStop();
        // remove mfilpper animation when the fragment is invisiable
        myHandler.removeMessages(Constants.INIT_HOMEFRAGMENT_VIEWFLIPPER);
        mFlipper.clearAnimation();
        myHandler.removeMessages(LOAD_CHANNEL);
        myHandler.removeMessages(ADD_CHANNEL_INFOVIEW);
        mChannelInfoView = null;
        Log.v(TAG, "homefragment onstop()");

        try {
            mAdWebView.getClass().getMethod("stopLoading").invoke(mAdWebView, (Object[]) null);
            mAdWebView.getClass().getMethod("onPause").invoke(mAdWebView, (Object[]) null);
            handlertv.postDelayed(handlerRuntv, 1500);
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        }
    }

    /**
     * init mImageTV ,add listeners
     */
    private void setImageTVListeners() {
        // jump to TV player when click the imageTV
        mImageTV.setOnClickListener(mOnClickListener);
        mImageTV.setOnFocusChangeListener(mFocusChangeListener);

        addViewGlobalLayoutListener(mImageTV, mViewBean);

        mImageTV.setOnKeyListener(new View.OnKeyListener() {
            @SuppressLint("NewApi")
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // if the fragment animation is running,return true
                if (isRunning || mAdWebView.isFocused()) {
                    return true;
                }
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    try {
                        mCurInputSource = TvManager.getInstance().getCurrentInputSource();
                    } catch (TvCommonException e) {
                        e.printStackTrace();
                    }

                    switch (keyCode) {
                        case KeyEvent.KEYCODE_DPAD_LEFT:
                            mHolder.restoreFragmentFocus(); // restore focus
                            mContext.showViews(Constants.SHOPWEBVIEW);
                            break;
                        case KeyEvent.KEYCODE_0:
                        case KeyEvent.KEYCODE_1:
                        case KeyEvent.KEYCODE_2:
                        case KeyEvent.KEYCODE_3:
                        case KeyEvent.KEYCODE_4:
                        case KeyEvent.KEYCODE_5:
                        case KeyEvent.KEYCODE_6:
                        case KeyEvent.KEYCODE_7:
                        case KeyEvent.KEYCODE_8:
                        case KeyEvent.KEYCODE_9:
                            if (mChannelInfoView != null
                                    && (mCurInputSource == EnumInputSource.E_INPUT_SOURCE_ATV || mCurInputSource == EnumInputSource.E_INPUT_SOURCE_DTV)) {
                                if (mSelectChannelNum.length() > 0
                                        && Integer.valueOf(String.valueOf(mSelectChannelNum.charAt(0))) == 0) {
                                    mSelectChannelNum.deleteCharAt(0);
                                }
                                if (mSelectChannelNum.length() == 3) {
                                    mSelectChannelNum = new StringBuffer();
                                }
                                mSelectChannelNum.append(String.valueOf(keyCode - KeyEvent.KEYCODE_0));
                                mChannelInfoView.selectChannel(mSelectChannelNum.toString());
                                myHandler.removeMessages(SELECTCHANNEL);
                                if ((mCurInputSource == EnumInputSource.E_INPUT_SOURCE_ATV
                                        && mSelectChannelNum.length() == 2 && Integer.valueOf(mSelectChannelNum
                                        .toString()) > 12) || mSelectChannelNum.length() == 3) {
                                    Message msg = myHandler.obtainMessage();
                                    msg.what = SELECTCHANNEL;
                                    msg.arg1 = Integer.valueOf(mSelectChannelNum.toString());
                                    myHandler.sendMessage(msg);
                                } else {
                                    Message msg = myHandler.obtainMessage();
                                    msg.what = SELECTCHANNEL;
                                    msg.arg1 = Integer.valueOf(mSelectChannelNum.toString());
                                    myHandler.sendMessageDelayed(msg, DELAYSELECTCHANNEL);
                                }
                            }
                            break;
                        case KeyEvent.KEYCODE_CHANNEL_UP:
                            if (mChannelInfoView != null
                                    && (mCurInputSource == EnumInputSource.E_INPUT_SOURCE_ATV || mCurInputSource == EnumInputSource.E_INPUT_SOURCE_DTV)) {
                                mChannelManagerExt.channelUp();
                                // ---------------------- add by Jason
                                // -----------------
                                if (HistoryRec.BFS != null) {
                                    if (HistoryRec.BFS.get(0).functionStatus) {
                                        // if(bChannelSwitchHistory) {
                                        recordChannelSwitch();
                                    }
                                }
                                // ------------------------------------------------------
                                if (mChannelManagerExt.getCurInfo() != null) {
                                    mChannelInfoView.channelChange(mChannelManagerExt.getCurInfo());
                                    int index = mChannelManagerExt.getCurInfo().number;
                                    if (mCurInputSource == EnumInputSource.E_INPUT_SOURCE_ATV) {
                                        Map<String, String> map = new HashMap<String, String>();
                                        map.put("channelNum", index + "");
                                        map.put("channelName", mChannelManagerExt.getCurProgramInfo().serviceName);
                                        SciflyStatistics.getInstance(mContext).recordEvent(getActivity(), "atvWatchRecord", map);
                                        SciflyStatistics.getInstance(mContext).recordEvent(getActivity(), "atvChangeChannelRecord", "channelNum", index + "");
                                    } else if (mCurInputSource == EnumInputSource.E_INPUT_SOURCE_DTV) {
                                        Map<String, String> map = new HashMap<String, String>();
                                        map.put("channelNum", index + "");
                                        map.put("channelName", mChannelManagerExt.getCurProgramInfo().serviceName);
                                        SciflyStatistics.getInstance(mContext).recordEvent(getActivity(), "dtvWatchRecord", map);
                                        SciflyStatistics.getInstance(mContext).recordEvent(getActivity(), "dtvChangeChannelRecord", "channelNum", index + "");
                                    }
                                }
                            }
                            break;
                        case KeyEvent.KEYCODE_CHANNEL_DOWN:
                            if (mChannelInfoView != null
                                    && (mCurInputSource == EnumInputSource.E_INPUT_SOURCE_ATV || mCurInputSource == EnumInputSource.E_INPUT_SOURCE_DTV)) {
                                mChannelManagerExt.channelDown();
                                // ---------------------- add by Jason
                                // -----------------
                                if (HistoryRec.BFS != null) {
                                    if (HistoryRec.BFS.get(0).functionStatus) {
                                        // if(bChannelSwitchHistory) {
                                        recordChannelSwitch();
                                    }
                                }
                                // ------------------------------------------------------
                                if (mChannelManagerExt.getCurInfo() != null) {
                                    mChannelInfoView.channelChange(mChannelManagerExt.getCurInfo());
                                    int index = mChannelManagerExt.getCurInfo().number;
                                    if (mCurInputSource == EnumInputSource.E_INPUT_SOURCE_ATV) {
                                        Map<String, String> map = new HashMap<String, String>();
                                        map.put("channelNum", index + "");
                                        map.put("channelName", mChannelManagerExt.getCurProgramInfo().serviceName);
                                        SciflyStatistics.getInstance(mContext).recordEvent(getActivity(), "atvWatchRecord", map);
                                        SciflyStatistics.getInstance(mContext).recordEvent(getActivity(), "atvChangeChannelRecord", "channelNum", index + "");
                                    } else if (mCurInputSource == EnumInputSource.E_INPUT_SOURCE_DTV) {
                                        Map<String, String> map = new HashMap<String, String>();
                                        map.put("channelNum", index + "");
                                        map.put("channelName", mChannelManagerExt.getCurProgramInfo().serviceName);
                                        SciflyStatistics.getInstance(mContext).recordEvent(getActivity(), "dtvWatchRecord", map);
                                        SciflyStatistics.getInstance(mContext).recordEvent(getActivity(), "dtvChangeChannelRecord", "channelNum", index + "");
                                    }
                                }
                            }
                            break;
                        default:
                            break;
                    }
                }
                return false;
            }
        });
    }

    /**
     * initFlipper add views and listener for each
     */
    @SuppressLint("NewApi")
    private void initFlipper() {
        mFlipperAnimation = new FlipperAnimation(mContext, mFlipper);
        int adcount = mContext.homeAdInfoList.size();
        if (adcount <= 0) {
            return;
        }
        for (int i = 0; i < (adcount / 2); i++) {
            View mview = LayoutInflater.from(mContext).inflate(R.layout.ad_item, null);
            MyAD adinf1 = mContext.homeAdInfoList.get(i);
            mBt1 = (ImageButton) mview.findViewById(R.id.bt1);
            mBt1.setBackground(null);
            // mBt1.setBackgroundDrawable(new
            // BitmapDrawable(mContext.getResources(), adinf1.getBt()));
            DisplayImageOptions option1 = new DisplayImageOptions.Builder().cacheInMemory(false).cacheOnDisk(true)
                    .considerExifParams(true).bitmapConfig(Bitmap.Config.RGB_565)
                    .showImageOnLoading(R.drawable.heran01).imageScaleType(ImageScaleType.EXACTLY_STRETCHED).build();
            HomeApplication.getInstance().displayImage(adinf1.getPic(), option1, mBt1);
            if (i == 0) {
                // save btn1 in the first view for draw focus view
                mBt1_tmp = mBt1;
            }
            mBt1.setOnFocusChangeListener(mFocusChangeListener);
            mBt1.setOnClickListener(mOnClickListener);
            mBt1.setOnKeyListener(mBtn1OnKeyListener);

            MyAD adinf2 = mContext.homeAdInfoList.get((adcount - 1) - i);
            mBt2 = (ImageButton) mview.findViewById(R.id.bt2);
            mBt2.setBackground(null);
            // mBt2.setBackgroundDrawable(new
            // BitmapDrawable(mContext.getResources(), adinf2.getBt()));
            DisplayImageOptions option2 = new DisplayImageOptions.Builder().cacheInMemory(false).cacheOnDisk(true)
                    .considerExifParams(true).bitmapConfig(Bitmap.Config.RGB_565)
                    .showImageOnLoading(R.drawable.heran08).imageScaleType(ImageScaleType.EXACTLY_STRETCHED).build();
            HomeApplication.getInstance().displayImage(adinf2.getPic(), option2, mBt2);
            if (i == 0) {
                mBt2_tmp = mBt2; // save btn2 in the first view for draw focus
                                 // view
            }
            mBt2.setOnFocusChangeListener(mFocusChangeListener);
            mBt2.setOnClickListener(mOnClickListener);
            mBt2.setOnKeyListener(mBtn2OnKeyListener);
            mFlipper.addView(mview);
        }

        addViewGlobalLayoutListener(mBt1_tmp, mViewBean);
        addViewGlobalLayoutListener(mBt2_tmp, mViewBean);

        animType = mContext.homeAdInfoList.get(0).getPlt();
        animTime = mContext.homeAdInfoList.get(0).getDit();
        mFlipper.setDisplayedChild(0);
    }

    /**
     * update viewflipper ad picture
     */
    public void udpateViewFlipper(int position) {
        // if the viewflipper is null or empty,return
        if (mFlipper == null || mFlipper.getChildCount() == 0) {
            return;
        }
        int count = mContext.homeAdInfoList.size();
        MyAD tmpAd = mContext.homeAdInfoList.get(position);
        ImageButton tmpBt = null;
        DisplayImageOptions options = null;
        if (position < (count / 2)) {
            View tmpView = mFlipper.getChildAt(position);
            tmpBt = (ImageButton) tmpView.findViewById(R.id.bt1);
            options = new DisplayImageOptions.Builder().cacheInMemory(false).cacheOnDisk(true).considerExifParams(true)
                    .bitmapConfig(Bitmap.Config.RGB_565).showImageOnLoading(R.drawable.heran01)
                    .imageScaleType(ImageScaleType.EXACTLY_STRETCHED).build();
        } else {
            View tmpView = mFlipper.getChildAt((count - 1) - position);
            tmpBt = (ImageButton) tmpView.findViewById(R.id.bt2);
            options = new DisplayImageOptions.Builder().cacheInMemory(false).cacheOnDisk(true).considerExifParams(true)
                    .bitmapConfig(Bitmap.Config.RGB_565).showImageOnLoading(R.drawable.heran08)
                    .imageScaleType(ImageScaleType.EXACTLY_STRETCHED).build();
        }

        HomeApplication.getInstance().displayImage(tmpAd.getPic(), options, tmpBt);
    }

    /**
     * init SurfaceView 、set SurfaceView small programSel
     */
    private void initSurfaceView() {
        if (mTv_sur != null) {
            mTv_sur.setBackgroundColor(Color.TRANSPARENT);
        }
        EnumInputSource curSource = EnumInputSource.values()[mContext.queryCurInputSrc()];
        if (SystemProperties.get("mstar.str.suspending", Constants.POWER_STATE_DEFAULT_VALUE).equals(
                Constants.POWER_STATE_CLICKED_VALUE)) {
            mContext.setFullscale();
            SharedPreferences mTmpPreferences = mContext.getPreferences(Activity.MODE_PRIVATE);
            mTmpPreferences.edit().putString(Constants.POWER_STATE, Constants.POWER_STATE_CLICKED_VALUE).commit();
        } else {
            mContext.setSmallscale();
        }
        if (curSource == EnumInputSource.E_INPUT_SOURCE_ATV || curSource == EnumInputSource.E_INPUT_SOURCE_DTV) {
            ProgramInfo pinfo = mContext.getCurrProgramInfo();
            mContext.programSel(pinfo.number, pinfo.serviceType);
        }

        ScreenProtection.getInstance().start(mContext, msignalTip);
    }

    /**
     * show SurfaceView
     */
    private void openSurfaceView() {
        mTv_sur.getHolder().addCallback(new SurfaceHolder.Callback() {
            @Override
            public void surfaceDestroyed(SurfaceHolder holder) {

            }

            @Override
            public void surfaceCreated(SurfaceHolder holder) {
                Log.v(TAG, "mContext.isPowerOn:" + mContext.isPowerOn);
                if (mContext.isPowerOn) {
                    handlertv.postDelayed(pip_thread,1000);
                    mContext.isPowerOn = false;
                } else {
                    handlertv.postDelayed(pip_thread, 500);
                }
            }

            @Override
            public void surfaceChanged(SurfaceHolder holder, int format, int width, int height) {
            }
        });
    }

    // handlertv postDelayed chanage source
    Handler handlertv = new Handler();

    Runnable pip_thread = new Runnable() {
        @Override
        public void run() {
            initSurfaceView();
            handlertv.removeCallbacks(pip_thread);
        }
    };

    Runnable handlerRuntv = new Runnable() {
        @Override
        public void run() {
            try {
                mAdWebView.setVisibility(View.GONE);
                prgBar.setVisibility(View.GONE);
                mAdWebView.getClass().getMethod("onPause").invoke(mAdWebView, (Object[]) null);
                mAdWebView.getClass().getMethod("stopLoading").invoke(mAdWebView, (Object[]) null);
            } catch (IllegalArgumentException e) {
                e.printStackTrace();
            } catch (IllegalAccessException e) {
                e.printStackTrace();
            } catch (InvocationTargetException e) {
                e.printStackTrace();
            } catch (NoSuchMethodException e) {
                e.printStackTrace();
            }

            if (mTv_sur != null) {
                mTv_sur.setBackgroundColor(Color.TRANSPARENT);
            }
            handlertv.removeCallbacks(handlerRuntv);
        }
    };

    Runnable handleLoadURL = new Runnable() {
        @Override
        public void run() {
            loadUrl();
        }
    };

    /**
     * get help_tip object for MainViewHolder
     */
    public ImageView getHelp_tip() {
        return mHelp_tip;
    }

    /**
     * get SurfaceView object for MainViewHolder
     */
    public SurfaceView getmTv_sur() {
        return mTv_sur;
    }

    private void drawFocus(View view) {
        mViewBean.setmCurFocusView(view);
        mViewBean.getmFocusObject().startAnimation(view);
    }

    class BtnOneOnKeyListener implements OnKeyListener {

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            // if the fragment animation is running,return true
            if (isRunning || mAdWebView.isFocused()) {
                return true;
            }
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if (keyCode == KeyEvent.KEYCODE_DPAD_UP && !UIUtil.isQuickDoubleClick(keyCode)) {
                    // up animation
                    mFlipper.setInAnimation(AnimationUtils.loadAnimation(mContext, R.anim.push_down_in));
                    mFlipper.setOutAnimation(AnimationUtils.loadAnimation(mContext, R.anim.push_down_out));
                    mFlipper.showPrevious();
                    v.requestFocus();
                    mViewBean.getmFocusObject().startAnimation(v);
                } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                    // delay to play ads
                    myHandler.removeMessages(Constants.INIT_HOMEFRAGMENT_VIEWFLIPPER);
                    myHandler.sendEmptyMessageDelayed(Constants.INIT_HOMEFRAGMENT_VIEWFLIPPER,
                            Constants.DELAY_HOMEFRAGMENT_VIEWFLIPPER);
                } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                    mContext.showViews(Constants.APPVIEW);
                }
            }
            return false;
        }
    }

    class BtnTwoOnKeyListener implements OnKeyListener {

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            // if the Fragment animation is running,ignoe key event
            if (isRunning || mAdWebView.isFocused()) {
                return true;
            }
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN && !UIUtil.isQuickDoubleClick(keyCode)) {
                    // down animation
                    mFlipper.setInAnimation(AnimationUtils.loadAnimation(mContext, R.anim.push_up_in));
                    mFlipper.setOutAnimation(AnimationUtils.loadAnimation(mContext, R.anim.push_up_out));
                    mFlipper.showNext();
                    v.requestFocus();
                    mViewBean.getmFocusObject().startAnimation(v);
                } else if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                    // delay to play ads
                    myHandler.removeMessages(Constants.INIT_HOMEFRAGMENT_VIEWFLIPPER);
                    myHandler.sendEmptyMessageDelayed(Constants.INIT_HOMEFRAGMENT_VIEWFLIPPER,
                            Constants.DELAY_HOMEFRAGMENT_VIEWFLIPPER);
                } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                    mContext.showViews(Constants.APPVIEW);
                }
            }
            return false;
        }

    }

    /**
     * handler focus change
     */
    class MyFocusChangeListener implements OnFocusChangeListener {

        @Override
        public void onFocusChange(View view, boolean flag) {
            if (flag) {
                drawFocus(view);
            }
        }
    }

    class MyOnClickListener implements OnClickListener {

        @Override
        public void onClick(View view) {
            int currpage = mFlipper.getDisplayedChild();
            if (mTv_sur != null) {
                mTv_sur.setBackgroundColor(Color.BLACK);
            }
            MyAD adinfo = null;
            switch (view.getId()) {
                case R.id.imagetv:
                    // start tv player
                    mContext.startApk("com.eostek.tv.player", "com.eostek.tv.player.PlayerActivity", null);
                    mContext.isCancelScale = true;
                    // --------------- add by Jason
                    // -----------------------------------------------------------------------------------------
                    if (HistoryRec.BFS != null) {
                        if (HistoryRec.BFS.get(1).functionStatus) {
                            // if(bFullScreen) {
                            hRecString = hRecString + HistoryRec.block1Action[4] + ',' + "" + ",";
                            // 取得目前日期和時間的字串
                            hRecString = hRecString + HistoryRec.getCurrentDateTime();
                            HistoryRec.writeToFile(hRecBlock + hRecString);
                            hRecString = "";
                        }
                    }
                    // ----------------------------------------------------------------------------------------------------------------------
                    break;
                case R.id.bt1: // just to ad web page
                    if (!Utils.isNetworkState) {
                        UIUtil.toastShow(R.string.shop_no_network, getActivity());
                        return;
                    }

                    adinfo = mContext.homeAdInfoList.get(currpage);

                    GoogleAnalyticsUtil.sendEvent(GoogleAnalyticsUtil.FROM_HOMEPAGE, adinfo, 0);

                    sendMonkey(KeyEvent.KEYCODE_DPAD_LEFT);
                    if (adinfo != null && adinfo.getTi() != null && adinfo.getTi().contains("WebPage")) {
                        goToWebUrl(adinfo.getGln());
                        SciflyStatistics.getInstance(mContext).recordEvent(getActivity(), "adClickRecord", "adUrl", adinfo.getGln());
                    } else {
                        mAdWebView.setVisibility(View.GONE);
                        isClickAd = true;
                        
                        mHolder.showDefaultMsg();
                        homeHandler.removeMessages(Constants.OSDMESSAGE);
                        handlertv.postDelayed(handleLoadURL, 1000);
                        mContext.setToChangeInputSource(EnumInputSource.E_INPUT_SOURCE_STORAGE);
                    }
                    setLoadURL(adinfo.getGln());
                    
                    break;
                case R.id.bt2:
                    if (!Utils.isNetworkState) {
                        UIUtil.toastShow(R.string.shop_no_network, getActivity());
                        return;
                    }
                    sendMonkey(KeyEvent.KEYCODE_DPAD_LEFT);
                    adinfo = mContext.homeAdInfoList.get((mContext.homeAdInfoList.size() - 1) - currpage);

                    GoogleAnalyticsUtil.sendEvent(GoogleAnalyticsUtil.FROM_HOMEPAGE, adinfo, 2);

                    if (adinfo != null && adinfo.getTi() != null && adinfo.getTi().contains("WebPage")) {
                        goToWebUrl(adinfo.getGln());
                        SciflyStatistics.getInstance(mContext).recordEvent(getActivity(), "adClickRecord", "adUrl", adinfo.getGln());
                    } else {
                        mAdWebView.setVisibility(View.GONE);
                        isClickAd = true;
                        mHolder.showDefaultMsg();
                        homeHandler.removeMessages(Constants.OSDMESSAGE);
                        handlertv.postDelayed(handleLoadURL, 1000);
                        mContext.setToChangeInputSource(EnumInputSource.E_INPUT_SOURCE_STORAGE);
                    }
                    setLoadURL(adinfo.getGln());
                    
                    break;
                default:
                    break;
            }
        }

    }

    class MyWebChromeClient extends WebChromeClient {
        private View mVideoProgressView;

        @Override
        public void onShowCustomView(View view, CustomViewCallback callback) {
            if (myView != null) {
                callback.onCustomViewHidden();
                return;
            }
            view.setFocusable(true);
            view.requestFocus();
            mVideoView = new FrameLayout(mContext);
            mVideoView.addView(view, COVER_SCREEN_PARAMS);
            frameLayout.addView(mVideoView, COVER_SCREEN_PARAMS);
            myView = view;
            myCallBack = callback;
        }

        @Override
        public void onHideCustomView() {
            Log.i("lucky", "onHideCustomView（）方法调用了");
            if (myView == null) {
                return;
            }
            if (!mContext.isClickBackBtn) {
                mAdWebView.loadUrl("javascript:myFunction()");
            }
            frameLayout.removeView(mVideoView);
            mVideoView = null;
            myView = null;
            myCallBack.onCustomViewHidden();
        }

        @Override
        public View getVideoLoadingProgressView() {
            // set the view when loading video
            if (mVideoProgressView == null) {
                LayoutInflater inflater = LayoutInflater.from(HomeFragment.this.getActivity());
                View view = inflater.inflate(R.layout.video_progress, null);
                mVideoProgressView = (LinearLayout) view.findViewById(R.id.progress_indicator);
            }
            return mVideoProgressView;
        }

        @Override
        public boolean onConsoleMessage(ConsoleMessage consoleMessage) {
            Log.d("ZR",
                    consoleMessage.message() + " at " + consoleMessage.sourceId() + ":" + consoleMessage.lineNumber());
            return super.onConsoleMessage(consoleMessage);
        }
    }

    public void backCurrentSource() {
        Log.d(TAG, "backCurrentSource");
        mImageTV.setFocusable(true);
        mBt1.setFocusable(true);
        mBt2.setFocusable(true);
        if (mAdWebView.getVisibility() == View.VISIBLE) {
            Log.d(TAG, "mAdWebView==>VISIBLE");
            mAdWebView.loadUrl("file:///android_asset/background.html");
            if (myView != null) {
                mWebChromeClient.onHideCustomView();
            }
        }
        mContext.isClickBackBtn = false;
        if (isClickAd) {
            mHolder.addAnimation();
            isClickAd = false;
        }
        homeHandler.sendEmptyMessage(Constants.OSDMESSAGE);
        mAdWebView.setFocusable(false);
        mAdWebView.clearFocus();
        mImageTV.requestFocus();
        mContext.stopVodPlay(mAdWebView, prgBar);
        handlertv.postDelayed(handlerRuntv, 1500);
    }

    public void sendMonkey(int keycode) {
        try {
            String keyCommand = "input keyevent " + keycode;
            Runtime runtime = Runtime.getRuntime();
            @SuppressWarnings("unused")
            Process proc = runtime.exec(keyCommand);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    // ------------------ add by Jason
    // ---------------------------------------------------------------------------------------
    private void recordChannelSwitch() {

        String channel = String.valueOf(mChannelManagerExt.getCurInfo().number);
        if (mCurInputSource == EnumInputSource.E_INPUT_SOURCE_ATV) {
            hRecString = hRecString + HistoryRec.block1Action[0] + ',' + channel + ',';
        } else if (mCurInputSource == EnumInputSource.E_INPUT_SOURCE_DTV) {
            hRecString = hRecString + HistoryRec.block1Action[1] + ',' + channel + ',';
        } else {
            hRecString = hRecString + HistoryRec.block1Action[2] + ',' + channel + ',';
        }
        // 取得目前日期和時間的字串
        hRecString = hRecString + HistoryRec.getCurrentDateTime();
        HistoryRec.writeToFile(hRecBlock + hRecString);
        hRecString = "";
    }

    private void goToWebUrl(String url) {
        Intent intent = new Intent(mContext, AppStoreActivity.class);
        Bundle bundle = new Bundle();
        if (url == null || url.equals("")) {
            url = Constants.defaultURL;
        }
        bundle.putString("URL", url);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    // -----------------------------------------------------------------------------------------------------------------------

    public void Infomsbox(int enterPandoraDialogTitle, int enterPandoraDialogContent) {
        AlertDialog.Builder dlgAlert = new AlertDialog.Builder(mContext);
        dlgAlert.setTitle(enterPandoraDialogTitle);
        dlgAlert.setMessage(enterPandoraDialogContent);

        dlgAlert.setPositiveButton(R.string.enter_allapp_dialog_yes, new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
                Constants.CheckJowin = true;
                Constants.IAgree = "1";
                android.provider.Settings.System.putString(mContext.getContentResolver(), "IAgree", Constants.IAgree);
                dialog.cancel();
            }
        });
        dlgAlert.setCancelable(false);
        dlgAlert.create().show();
    }
}
