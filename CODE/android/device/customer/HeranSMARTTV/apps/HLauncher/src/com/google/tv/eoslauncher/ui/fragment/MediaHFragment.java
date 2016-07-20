
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
import com.google.tv.eoslauncher.util.HistoryRec;
import com.google.tv.eoslauncher.util.Utils;

public class MediaHFragment extends PublicFragment {

    // photo button
    private ImageButton mPhotoView;

    // music button
    private ImageButton mMusicView;

    // video button
    private ImageButton mVideoView;

    // kok button
    private ImageButton mKokView;

    // private ImageView mImage;

    private MyOnClickListener mListener;

    private MyOnFocusChangeListener mFocusChangeListener;

    private HomeActivity mContext;

    private MainViewHolder mHolder;

    private ViewBean mViewBean;

    private String recData;
    
    private final static String TAG = "MediaHFragment";

    public ViewBean getmViewBean() {
        return mViewBean;
    }

    public void setmViewBean(ViewBean mViewBean) {
        this.mViewBean = mViewBean;
    }

    public MediaHFragment(HomeActivity context, MainViewHolder mHolder) {
        super();
        this.mContext = context;
        this.mHolder = mHolder;
        mViewBean = new ViewBean(null, null);
    }

    public MediaHFragment() {
        super();
    }

    @SuppressLint("NewApi")
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mview = inflater.inflate(R.layout.media_main_h, container, false);
        initViews(mview);
        if (android.provider.Settings.System.getString(mContext.getContentResolver(), "SkipPandora") == null) {
            android.provider.Settings.System.putString(mContext.getContentResolver(), "SkipPandora", Constants.PPasswd);
        } else {
            Constants.SkipPandora = android.provider.Settings.System.getString(mContext.getContentResolver(),
                    "SkipPandora");
        }
        setRetainInstance(true);
        Log.v(TAG, "onCreateView");
        return mview;
    }

    private void initViews(View mview) {
        mListener = new MyOnClickListener();
        mFocusChangeListener = new MyOnFocusChangeListener();

        FocusView mFocusView = (FocusView) mview.findViewById(R.id.selector);
        mViewBean.setmFocusObject(mFocusView);

        // init KokView ,add listeners
        mKokView = (ImageButton) mview.findViewById(R.id.imageview_kok);
        mKokView.setOnClickListener(mListener);
        mKokView.setOnFocusChangeListener(mFocusChangeListener);
        mKokView.setOnKeyListener(new View.OnKeyListener() {
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

        addViewGlobalLayoutListener(mKokView, mViewBean);

        // init mVideoView ,add listeners
        mVideoView = (ImageButton) mview.findViewById(R.id.imageview_video);
        mVideoView.setOnClickListener(mListener);
        mVideoView.setOnFocusChangeListener(mFocusChangeListener);

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
            mViewBean.setmCurFocusView(mKokView);
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
                case R.id.imageview_kok:
                	recData = HistoryRec.block[2] + ',' + HistoryRec.block3Action[0] + ',' + "" + ',' + HistoryRec.getCurrentDateTime();
                    Log.d("rec", "tempData:" + recData);
                    HistoryRec.writeToFile(recData);
                    recData = "";
                	
                    if (Utils.isApkExist(mContext, "com.heran.kok")) {
                        mContext.startApk("com.heran.kok", "com.heran.kok.MainActivity", mBundle);
                    }
                    break;
                case R.id.imageview_photo:
                	recData = HistoryRec.block[2] + ',' + HistoryRec.block3Action[2] + ',' + "" + ',' + HistoryRec.getCurrentDateTime();
                    Log.d("rec", "tempData:" + recData);
                    HistoryRec.writeToFile(recData);
                    recData = "";
                	
                    mBundle.putString("gotoMediaPlayer", "photo");
                    mContext.startApk("com.hrtvbic.usb.S6A918", "com.hrtvbic.usb.S6A918.MainActivity", mBundle);
                    break;
                case R.id.imageview_video:
                	recData = HistoryRec.block[2] + ',' + HistoryRec.block3Action[1] + ',' + "" + ',' + HistoryRec.getCurrentDateTime();
                    Log.d("rec", "tempData:" + recData);
                    HistoryRec.writeToFile(recData);
                    recData = "";
                	
                    mBundle.putString("gotoMediaPlayer", "video");
                    mContext.startApk("com.hrtvbic.usb.S6A918", "com.hrtvbic.usb.S6A918.MainActivity", mBundle);
                    break;
                case R.id.imageview_music:
                	recData = HistoryRec.block[2] + ',' + HistoryRec.block3Action[3] + ',' + "" + ',' + HistoryRec.getCurrentDateTime();
                    Log.d("rec", "tempData:" + recData);
                    HistoryRec.writeToFile(recData);
                    recData = "";
                	
                    mBundle.putString("gotoMediaPlayer", "music");
                    mContext.startApk("com.hrtvbic.usb.S6A918", "com.hrtvbic.usb.S6A918.MainActivity", mBundle);
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
        mKokView.setImageBitmap(null);
        System.gc();
    }

}
