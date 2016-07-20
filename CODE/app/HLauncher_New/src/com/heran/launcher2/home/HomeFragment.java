
package com.heran.launcher2.home;

import com.heran.launcher2.HomeActivity;
import com.heran.launcher2.MainViewHolder;
import com.heran.launcher2.PublicFragment;
import com.heran.launcher2.R;
import com.heran.launcher2.advert.BootAdActivity;
import com.heran.launcher2.util.ChannelInfoView;
import com.heran.launcher2.util.ChannelManagerExt;
import com.heran.launcher2.util.Constants;
import com.heran.launcher2.util.FocusView;
import com.heran.launcher2.util.GoogleAnalyticsUtil;
import com.heran.launcher2.util.HistoryRec;
import com.heran.launcher2.util.ScreenProtection;
import com.heran.launcher2.util.Utils;
import com.heran.launcher2.widget.ViewBean;
import com.mstar.android.tvapi.common.TvManager;
import com.mstar.android.tvapi.common.exception.TvCommonException;
import com.mstar.android.tvapi.common.vo.ProgramInfo;
import com.mstar.android.tvapi.common.vo.TvOsType.EnumInputSource;

import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.content.SharedPreferences;
import android.graphics.Color;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.os.SystemProperties;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.SurfaceHolder;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.webkit.WebChromeClient;
import android.widget.FrameLayout;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

/**
 * projectName： EosLauncher moduleName： HomeFragment.java
 * 
 * @author laird.li
 * @version 1.0.0
 * @time 2016-3-8 下午2:00:31
 * @Copyright © 2013 Eos Inc.
 */

public class HomeFragment extends PublicFragment {

    private final static String TAG = "HomeFragment";

    // help tip obj
    private ImageView mHelp_tip;

    // tv sur
    private SurfaceView mTv_sur;

    // imageTV
    private ImageButton mImageTV;

    // FocusChangeListener
    private MyFocusChangeListener mFocusChangeListener;

    // OnClickListener
    private MyOnClickListener mOnClickListener;

    private HomeActivity mContext;

    private MainViewHolder mHolder;

    private MyHandler myHandler;

    private ViewBean mViewBean;

    private TextView msignalTip;

    private static final int SELECTCHANNEL = 0x101;

    private static final int ADD_CHANNEL_INFOVIEW = 0x102;

    private static final int LOAD_CHANNEL = 0x103;

    private ChannelManagerExt mChannelManagerExt;

    private View mView;

    private EnumInputSource mCurInputSource = EnumInputSource.E_INPUT_SOURCE_NONE;

    private StringBuffer mSelectChannelNum = new StringBuffer();

    private static final int DELAYSELECTCHANNEL = 3000;

    private FrameLayout parent;

    private ChannelInfoView mChannelInfoView;

    private String loadURL = "http://219.87.154.38/jowintest/index.php?r=activity/hertv1";

    private String recData = "";

    public String getLoadURL() {
        return loadURL;
    }

    public void setLoadURL(String loadURL) {
        this.loadURL = loadURL;
    }

    protected static final FrameLayout.LayoutParams COVER_SCREEN_PARAMS = new FrameLayout.LayoutParams(
            ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.MATCH_PARENT);

    private WebChromeClient.CustomViewCallback customViewCallback;

    private View mCustomView;

    protected FrameLayout mFullscreenContainer;

    private MyWebChromeClient mWebChromeClient;

    public ViewBean getmViewBean() {
        return mViewBean;
    }

    public void setmViewBean(ViewBean mBean) {
        this.mViewBean = mBean;
    }

    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case SELECTCHANNEL:
                    int index = mChannelManagerExt.getChannelNums().indexOf(msg.arg1);
                    mSelectChannelNum = new StringBuffer();
                    if (index != -1 && mChannelInfoView != null) {
                        mChannelManagerExt.programSel(index);
                        mChannelInfoView.channelChange(mChannelManagerExt.getCurInfo());
                        try {
                            mCurInputSource = TvManager.getInstance().getCurrentInputSource();
                        } catch (TvCommonException e) {
                            e.printStackTrace();
                        }
                        if (mCurInputSource == EnumInputSource.E_INPUT_SOURCE_ATV) {
                            recData = HistoryRec.block[0] + ',' + HistoryRec.block1Action[0] + ','
                                    + String.valueOf(index + 1).toString() + ',' + "" + ',' + "" + ','
                                    + HistoryRec.getCurrentDateTime();
                            HistoryRec.writeToFile(recData);
                            recData = "";
                        } else if (mCurInputSource == EnumInputSource.E_INPUT_SOURCE_DTV) {
                            recData = HistoryRec.block[0] + ',' + HistoryRec.block1Action[1] + ','
                                    + String.valueOf(index + 1).toString() + ',' + "" + ',' + "" + ','
                                    + HistoryRec.getCurrentDateTime();
                            HistoryRec.writeToFile(recData);
                        } else {
                            recData = HistoryRec.block[0] + ',' + HistoryRec.block1Action[2] + ','
                                    + String.valueOf(index + 1).toString() + ',' + "" + ',' + "" + ','
                                    + HistoryRec.getCurrentDateTime();
                            HistoryRec.writeToFile(recData);
                        }
                    }
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
                        Log.v(TAG, "addview time = " + (System.currentTimeMillis() - time));
                    }
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
        this.mHolder = mainViewHolder;
        mFocusChangeListener = new MyFocusChangeListener();
        mOnClickListener = new MyOnClickListener();
        myHandler = new MyHandler();
        mViewBean = new ViewBean(null, null);
        mChannelManagerExt = ChannelManagerExt.getInstance();
        Log.d(TAG, "HomeFragment Construct");
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        // mView 已經被設為全局變數 , 稍微破壞封裝性
        Log.d(TAG, "onCreateView");
        mView = inflater.inflate(R.layout.home_main, container, false);
        initView();
        return mView;
    }

    private void initView() {

        Log.d(TAG, "Launcher Version = " + getString(R.string.version));
        FocusView mFocusView = (FocusView) getActivity().findViewById(R.id.fragment_selector);
        mViewBean.setmFocusObject(mFocusView);
        mImageTV = (ImageButton) mView.findViewById(R.id.imagetv);
        mHelp_tip = (ImageView) mView.findViewById(R.id.help_tip);
        msignalTip = (TextView) mView.findViewById(R.id.signal_tips);
        parent = (FrameLayout) mView.findViewById(R.id.home_layout);

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

        if (mViewBean.getmCurFocusView() == null || mViewBean.getmCurFocusView() == mHolder.home_content) {
            mViewBean.setmCurFocusView(mImageTV);
            mImageTV.requestFocus();
            mViewBean.getmFocusObject().startAnimation(mViewBean.getmCurFocusView());
        }

        setRetainInstance(true);

    }

    @Override
    public void onResume() {

        super.onResume();
        Log.v(TAG, "homefragment onResume");
        if (BootAdActivity.isFullscreen) {
            mContext.mainLogic.startApk("com.eostek.tv.player", "com.eostek.tv.player.PlayerActivity", null);
            mContext.mainLogic.isCancelScale = true;
        }
        BootAdActivity.isFullscreen = false;

        if (mViewBean.getmCurFocusView() == null) {
            mViewBean.setmCurFocusView(mImageTV);
            mImageTV.requestFocus();
            mViewBean.getmFocusObject().startAnimation(mViewBean.getmCurFocusView());
        }
        mContext.backHomeSource();
        if (!ScreenProtection.getInstance().ismIsStarted()) {
            ScreenProtection.getInstance().start(mContext, msignalTip);
        }
        try {
            mCurInputSource = TvManager.getInstance().getCurrentInputSource();
            myHandler.sendEmptyMessageDelayed(LOAD_CHANNEL, 2000);

        } catch (TvCommonException e) {
            e.printStackTrace();
        }
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
        Log.v(TAG, "homefragment onPause " + ";currentActivity = " + activityName);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        ScreenProtection.getInstance().stop();
        Log.v(TAG, "homefragment onDestroy");
    }

    @Override
    public void onStop() {
        super.onStop();
        // remove mfilpper animation when the fragment is invisiable
        myHandler.removeMessages(Constants.INIT_HOMEFRAGMENT_VIEWFLIPPER);
        // mFlipper.clearAnimation();
        myHandler.removeMessages(LOAD_CHANNEL);
        myHandler.removeMessages(ADD_CHANNEL_INFOVIEW);
        mChannelInfoView = null;
        Log.v(TAG, "homefragment onstop()");
    }

    /**
     * init mImageTV ,add listeners
     */
    private void setImageTVListeners() {
        // jump to TV player when click the imageTV
        mImageTV.setOnClickListener(mOnClickListener);
        mImageTV.setOnFocusChangeListener(mFocusChangeListener);

        addViewGlobalLayoutListener(mImageTV, mViewBean);
        // ?
        mImageTV.setOnKeyListener(new View.OnKeyListener() {
            @SuppressLint("NewApi")
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // if the fragment animation is running,return true
                if (isRunning) {
                    return true;
                }
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    try {
                        mCurInputSource = TvManager.getInstance().getCurrentInputSource();
                    } catch (TvCommonException e) {
                        // TODO Auto-generated catch block
                        e.printStackTrace();
                    }

                    switch (keyCode) {
                        case KeyEvent.KEYCODE_DPAD_LEFT:
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
                            if (mChannelInfoView != null && (mCurInputSource == EnumInputSource.E_INPUT_SOURCE_ATV
                                    || mCurInputSource == EnumInputSource.E_INPUT_SOURCE_DTV)) {
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
                                        && mSelectChannelNum.length() == 2
                                        && Integer.valueOf(mSelectChannelNum.toString()) > 12)
                                        || mSelectChannelNum.length() == 3) {
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
                            Log.d(TAG, "keycode channel up");
                            if (mChannelInfoView != null && (mCurInputSource == EnumInputSource.E_INPUT_SOURCE_ATV
                                    || mCurInputSource == EnumInputSource.E_INPUT_SOURCE_DTV)) {
                                mChannelManagerExt.channelUp();
                                if (mCurInputSource == EnumInputSource.E_INPUT_SOURCE_ATV) {
                                    String channel = String.valueOf(mChannelManagerExt.getCurInfo().number);
                                    recData = HistoryRec.block[0] + ',' + HistoryRec.block2Action[0] + ',' + channel
                                            + ',' + "" + ',' + "" + ',' + HistoryRec.getCurrentDateTime();
                                    HistoryRec.writeToFile(recData);
                                    recData = "";
                                } else if (mCurInputSource == EnumInputSource.E_INPUT_SOURCE_DTV) {
                                    String channel = String.valueOf(mChannelManagerExt.getCurInfo().number);
                                    recData = HistoryRec.block[0] + ',' + HistoryRec.block2Action[0] + ',' + channel
                                            + ',' + "" + ',' + "" + ',' + HistoryRec.getCurrentDateTime();
                                    HistoryRec.writeToFile(recData);
                                    recData = "";
                                } else {
                                    String channel = String.valueOf(mChannelManagerExt.getCurInfo().number);
                                    recData = HistoryRec.block[0] + ',' + HistoryRec.block2Action[0] + ',' + channel
                                            + ',' + "" + ',' + "" + ',' + HistoryRec.getCurrentDateTime();
                                    HistoryRec.writeToFile(recData);
                                    recData = "";
                                }
                                if (mChannelManagerExt.getCurInfo() != null) {
                                    Log.d(TAG, String.valueOf(mChannelManagerExt.getCurInfo().number));
                                    mChannelInfoView.channelChange(mChannelManagerExt.getCurInfo());
                                }
                            }
                            break;
                        case KeyEvent.KEYCODE_CHANNEL_DOWN:
                            Log.d(TAG, "keycode channel down");
                            if (mChannelInfoView != null && (mCurInputSource == EnumInputSource.E_INPUT_SOURCE_ATV
                                    || mCurInputSource == EnumInputSource.E_INPUT_SOURCE_DTV)) {
                                mChannelManagerExt.channelDown();
                                if (mCurInputSource == EnumInputSource.E_INPUT_SOURCE_ATV) {
                                    String channel = String.valueOf(mChannelManagerExt.getCurInfo().number);
                                    recData = HistoryRec.block[0] + ',' + HistoryRec.block2Action[0] + ',' + channel
                                            + ',' + "" + ',' + "" + ',' + HistoryRec.getCurrentDateTime();
                                    HistoryRec.writeToFile(recData);
                                    recData = "";
                                } else if (mCurInputSource == EnumInputSource.E_INPUT_SOURCE_DTV) {
                                    String channel = String.valueOf(mChannelManagerExt.getCurInfo().number);
                                    recData = HistoryRec.block[0] + ',' + HistoryRec.block2Action[0] + ',' + channel
                                            + ',' + "" + ',' + "" + ',' + HistoryRec.getCurrentDateTime();
                                    HistoryRec.writeToFile(recData);
                                    recData = "";
                                } else {
                                    String channel = String.valueOf(mChannelManagerExt.getCurInfo().number);
                                    recData = HistoryRec.block[0] + ',' + HistoryRec.block2Action[0] + ',' + channel
                                            + ',' + "" + ',' + "" + ',' + HistoryRec.getCurrentDateTime();
                                    HistoryRec.writeToFile(recData);
                                    recData = "";
                                }
                                if (mChannelManagerExt.getCurInfo() != null) {
                                    Log.d(TAG, String.valueOf(mChannelManagerExt.getCurInfo().number));
                                    mChannelInfoView.channelChange(mChannelManagerExt.getCurInfo());
                                }
                            }
                            break;
                        case KeyEvent.KEYCODE_DPAD_DOWN:
                            mContext.mviewHolder.fragmentBtn.requestFocus();
                            mContext.mviewHolder.drawFocus(mContext.mviewHolder.fragmentBtn);
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
     * init SurfaceView 、set SurfaceView small programSel
     */
    private void initSurfaceView() {
        if (mTv_sur != null) {
            mTv_sur.setBackgroundColor(Color.TRANSPARENT);
        }
        EnumInputSource curSource = EnumInputSource.values()[mContext.queryCurInputSrc()];
        if (SystemProperties.get("mstar.str.suspending", Constants.POWER_STATE_DEFAULT_VALUE)
                .equals(Constants.POWER_STATE_CLICKED_VALUE)) {
            mContext.setFullscale();
            SharedPreferences mTmpPreferences = mContext.getPreferences(Activity.MODE_PRIVATE);
            mTmpPreferences.edit().putString(Constants.POWER_STATE, Constants.POWER_STATE_CLICKED_VALUE).commit();
        } else {
            mContext.mainLogic.setSmallscale();
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
                    handlertv.postDelayed(pip_thread, 1500);
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

            if (mTv_sur != null) {
                mTv_sur.setBackgroundColor(Color.TRANSPARENT);
            }
            handlertv.removeCallbacks(handlerRuntv);
        }
    };

    public ImageButton getImageTV() {
        return mImageTV;
    }

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
            if (mTv_sur != null) {
                mTv_sur.setBackgroundColor(Color.BLACK);
            }
            switch (view.getId()) {
                case R.id.imagetv:
                    recData = HistoryRec.block[0] + ',' + HistoryRec.block2Action[4] + ',' + "" + ',' + "" + ',' + ""
                            + ',' + HistoryRec.getCurrentDateTime();
                    HistoryRec.writeToFile(recData);
                    recData = "";
                    mContext.mainLogic.startApk("com.eostek.tv.player", "com.eostek.tv.player.PlayerActivity", null);
                    mContext.mainLogic.isCancelScale = true;
                    GoogleAnalyticsUtil.sendEvent(GoogleAnalyticsUtil.FROM_HOMEPAGE, "com.eostek.tv.player");
                    break;
                default:
                    break;
            }
        }

    }

    static class FullscreenFrameLayout extends FrameLayout {
        public FullscreenFrameLayout(Context ctx) {
            super(ctx);
            setBackgroundColor(Color.BLACK);
        }

        @Override
        public boolean onTouchEvent(MotionEvent paramMotionEvent) {
            return true;
        }
    }

    class MyWebChromeClient extends WebChromeClient {
        private View mVideoProgressView;

        /**
         * play the html5 video in the @param view
         */
        @Override
        public void onShowCustomView(View view, int requestedOrientation, CustomViewCallback callback) {
            Log.v("zb.wu", "onShowCustomView1");
            // if is full screen play,hide play view
            if (mCustomView != null) {
                callback.onCustomViewHidden();
                return;
            }
            // set full screen to play the video
            FrameLayout localFrameLayout = (FrameLayout) getActivity().getWindow().getDecorView();
            mFullscreenContainer = new FullscreenFrameLayout(mContext);
            mFullscreenContainer.addView(view, COVER_SCREEN_PARAMS);
            localFrameLayout.addView(mFullscreenContainer, COVER_SCREEN_PARAMS);
            mCustomView = view;
            setFullscreen(true);
            customViewCallback = callback;
            getActivity().setRequestedOrientation(requestedOrientation);
        }

        @Override
        public void onShowCustomView(View view, CustomViewCallback callback) {
            onShowCustomView(view, getActivity().getRequestedOrientation(), callback);
        }

        @Override
        public View getVideoLoadingProgressView() {
            // set the view when loading video
            if (mVideoProgressView == null) {
                LayoutInflater inflater = LayoutInflater.from(getActivity());
                View view = inflater.inflate(R.layout.video_progress, null);
                mVideoProgressView = view.findViewById(R.id.progress_indicator);
            }
            return mVideoProgressView;
        }

        @Override
        public void onHideCustomView() {
            if (mCustomView == null) {
                return;
            }
            // remove the full screen play view
            setFullscreen(false);
            FrameLayout decor = (FrameLayout) getActivity().getWindow().getDecorView();
            decor.removeView(mFullscreenContainer);
            mFullscreenContainer = null;
            mCustomView = null;
            customViewCallback.onCustomViewHidden();
            // Show the content view.
            getActivity().setRequestedOrientation(getActivity().getRequestedOrientation());

        }
    }

    public boolean inCustomView() {
        return (mCustomView != null);
    }

    public void hideCustomView() {
        mWebChromeClient.onHideCustomView();
    }

    /**
     * set full screen to play video
     * 
     * @param enabled True to set full screen,false else
     */
    public void setFullscreen(boolean enabled) {
        Window win = getActivity().getWindow();
        WindowManager.LayoutParams winParams = win.getAttributes();
        final int bits = WindowManager.LayoutParams.FLAG_FULLSCREEN;
        if (enabled) {
            winParams.flags |= bits;
        } else {
            winParams.flags &= ~bits;
            if (mCustomView != null) {
                mCustomView.setSystemUiVisibility(View.SYSTEM_UI_FLAG_VISIBLE);
            }
        }
        win.setAttributes(winParams);
    }

    public void backCurrentSource() {
        Log.d(TAG, "backCurrentSource");
        mImageTV.setFocusable(true);
        mImageTV.requestFocus();
        handlertv.postDelayed(handlerRuntv, 1500);
    }

}
