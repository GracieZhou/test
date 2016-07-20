
package com.google.tv.eoslauncher.ui.fragment;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.ViewGroup;
import android.widget.ImageButton;

import com.google.tv.eoslauncher.HomeActivity;
import com.google.tv.eoslauncher.MainViewHolder;
import com.google.tv.eoslauncher.R;
import com.google.tv.eoslauncher.model.ViewBean;
import com.google.tv.eoslauncher.util.Constants;
import com.google.tv.eoslauncher.util.FocusView;
import com.google.tv.eoslauncher.util.GoogleAnalyticsUtil;
import com.google.tv.eoslauncher.util.HistoryRec;

/**
 * projectName： EosLauncher moduleName： MediaFragment.java
 * 
 * @author chadm.xiang
 * @version 1.0.0
 * @time 2013-12-18 下午2:04:41
 * @Copyright © 2013 Eos Inc.
 */

public class MediaFragment extends PublicFragment {

    // photo button
    private ImageButton mPhotoView;

    // music button
    private ImageButton mMusicView;

    // video button
    private ImageButton mVideoView;

    private MyOnClickListener mListener;

    private MyOnFocusChangeListener mFocusChangeListener;

    private HomeActivity mContext;

    private MainViewHolder mHolder;

    private ViewBean mViewBean;
    
    private String recData;

    public ViewBean getmViewBean() {
        return mViewBean;
    }

    public void setmViewBean(ViewBean mViewBean) {
        this.mViewBean = mViewBean;
    }

    public MediaFragment(HomeActivity context, MainViewHolder mHolder) {
        super();
        this.mContext = context;
        this.mHolder = mHolder;
        mViewBean = new ViewBean(null, null);
    }

    public MediaFragment() {
        super();
    }

    @SuppressLint("NewApi")
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mview = inflater.inflate(R.layout.media_main, container, false);
        initViews(mview);
        if (android.provider.Settings.System.getString(mContext.getContentResolver(), "SkipPandora") == null) {
            android.provider.Settings.System.putString(mContext.getContentResolver(), "SkipPandora", Constants.PPasswd);
        } else {
            Constants.SkipPandora = android.provider.Settings.System.getString(mContext.getContentResolver(),
                    "SkipPandora");
        }
        setRetainInstance(true);
        return mview;
    }

    private void initViews(View mview) {
        mListener = new MyOnClickListener();
        mFocusChangeListener = new MyOnFocusChangeListener();

        FocusView mFocusView = (FocusView) mview.findViewById(R.id.selector);
        mVideoView = (ImageButton) mview.findViewById(R.id.imageview_video);
        mViewBean.setmFocusObject(mFocusView);

        // init mVideoView ,add listeners
        mVideoView.setOnClickListener(mListener);
        mVideoView.setOnFocusChangeListener(mFocusChangeListener);
        mVideoView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // if the Fragment animation is running,ignoe key event
                if (isRunning) {
                    return true;
                }
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                        mContext.showViews(Constants.APPVIEW);
                    }
                }
                return false;
            }
        });

        addViewGlobalLayoutListener(mVideoView, mViewBean);

        // init mPhotoView ,add listeners
        mPhotoView = (ImageButton) mview.findViewById(R.id.imageview_photo);
        mPhotoView.setOnClickListener(mListener);
        mPhotoView.setOnFocusChangeListener(mFocusChangeListener);

        // init mMusicView ,add listeners
        mMusicView = (ImageButton) mview.findViewById(R.id.imageview_music);
        mMusicView.setOnClickListener(mListener);
        mMusicView.setOnFocusChangeListener(mFocusChangeListener);
        mMusicView.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                // if the Fragment animation is running,ignoe key event
                if (isRunning) {
                    return true;
                }
                if (event.getAction() == KeyEvent.ACTION_DOWN) {
                    if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                        if (Constants.SkipPandora.equalsIgnoreCase("1")) {
                            mContext.showViews(Constants.SHOPWEBVIEW);
                        } else {
                            mContext.showViews(Constants.PANDORAVIEW);
                        }
                    }
                }
                return false;
            }
        });

        addViewGlobalLayoutListener(mMusicView, mViewBean);

        if (mViewBean.getmCurFocusView() == null) {
            mViewBean.setmCurFocusView(mVideoView);
        } else if (mViewBean.getmCurFocusView() == mHolder.home_content) {
            // if curFoucesView is the mark set in restoreFragmentFocus,set the
            // curFoucesView to the left view
            mViewBean.setmCurFocusView(mMusicView);
        }
    }

    /**
     * handle the imagebutton onclick event
     */
    class MyOnClickListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            Bundle mBundle = new Bundle();
            switch (v.getId()) {
                case R.id.imageview_photo:
                	recData = HistoryRec.block[2] + ',' + HistoryRec.block3Action[2] + ',' + "" + ',' + HistoryRec.getCurrentDateTime();
                    Log.d("rec", "tempData:" + recData);
                    HistoryRec.writeToFile(recData);
                    recData = "";
                    GoogleAnalyticsUtil.sendEvent(Constants.MEDIA_BROWSER_PK_NAME, Constants.MEDIA_BROWSER_CLASS, "photo");
                    mBundle.putString("gotoMediaPlayer", "photo");
                    mContext.startApk(Constants.MEDIA_BROWSER_PK_NAME, Constants.MEDIA_BROWSER_CLASS, mBundle);
                    break;
                case R.id.imageview_video:
                	recData = HistoryRec.block[2] + ',' + HistoryRec.block3Action[1] + ',' + "" + ',' + HistoryRec.getCurrentDateTime();
                    Log.d("rec", "tempData:" + recData);
                    HistoryRec.writeToFile(recData);
                    recData = "";
                    GoogleAnalyticsUtil.sendEvent(Constants.MEDIA_BROWSER_PK_NAME, Constants.MEDIA_BROWSER_CLASS, "video");
                    mBundle.putString("gotoMediaPlayer", "video");
                    mContext.startApk(Constants.MEDIA_BROWSER_PK_NAME, Constants.MEDIA_BROWSER_CLASS, mBundle);
                    break;
                case R.id.imageview_music:
                	recData = HistoryRec.block[2] + ',' + HistoryRec.block3Action[3] + ',' + "" + ',' + HistoryRec.getCurrentDateTime();
                    Log.d("rec", "tempData:" + recData);
                    HistoryRec.writeToFile(recData);
                    recData = "";
                    GoogleAnalyticsUtil.sendEvent(Constants.MEDIA_BROWSER_PK_NAME, Constants.MEDIA_BROWSER_CLASS, "music");
                    mBundle.putString("gotoMediaPlayer", "music");
                    mContext.startApk(Constants.MEDIA_BROWSER_PK_NAME, Constants.MEDIA_BROWSER_CLASS, mBundle);
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * handle the imagebutton focus change event
     */
    class MyOnFocusChangeListener implements OnFocusChangeListener {

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
                mViewBean.setmCurFocusView(v);
                // draw focus at the view position when view has focus
                mViewBean.getmFocusObject().startAnimation(v);
            }
        }
    }

    @Override
    public void onDestroy() {
        // TODO Auto-generated method stub
        super.onDestroy();
        mVideoView.setImageBitmap(null);
        mMusicView.setImageBitmap(null);
        mPhotoView.setImageBitmap(null);
        System.gc();
    }

}
