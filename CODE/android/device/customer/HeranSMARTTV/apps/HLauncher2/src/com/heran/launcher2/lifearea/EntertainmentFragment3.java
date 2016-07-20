
package com.heran.launcher2.lifearea;

import com.heran.launcher2.HomeActivity;
import com.heran.launcher2.PublicFragment;
import com.heran.launcher2.R;
import com.heran.launcher2.util.Constants;
import com.heran.launcher2.util.FocusView;
import com.heran.launcher2.util.UIUtil;
import com.heran.launcher2.util.Utils;
import com.heran.launcher2.widget.ViewBean;

import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageButton;

public class EntertainmentFragment3 extends PublicFragment {

    private static final String TAG = "EntertainmentFragment3";

    private Button mBtnMedia3;

    private Button mBtnKok3;

    private Button mBtnPandora3;

    public ImageButton mfragmentBtn;

    private MyOnClickListener myOnClickListener;

    private MyOnKeyListener myOnKeyListener;

    private MyOnFocusChangeListener myOnFocusChangeListener;

    private final HomeActivity mContext;

    private final ViewBean mViewBean;

    public EntertainmentFragment3(HomeActivity context) {
        super();
        mContext = context;
        mViewBean = new ViewBean(null, null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mview = inflater.inflate(R.layout.livingarea_entertainment3, container, false);
        findViews(mview);
        initViews(mview);
        return mview;
    }

    private void findViews(View mview) {
        mBtnMedia3 = (Button) mview.findViewById(R.id.btn_media3);
        mBtnKok3 = (Button) mview.findViewById(R.id.btn_kok3);
        mBtnPandora3 = (Button) mview.findViewById(R.id.btn_pandora3);
        mfragmentBtn = (ImageButton) getActivity().findViewById(R.id.fragmentBtn);
    }

    private void initViews(View mview) {
        Log.d(TAG, "EntertainmentFragment33333 in init!!");

        myOnClickListener = new MyOnClickListener();
        mBtnMedia3.setOnClickListener(myOnClickListener);
        mBtnKok3.setOnClickListener(myOnClickListener);
        mBtnPandora3.setOnClickListener(myOnClickListener);

        myOnKeyListener = new MyOnKeyListener();
        mBtnMedia3.setOnKeyListener(myOnKeyListener);
        mBtnKok3.setOnKeyListener(myOnKeyListener);
        mBtnPandora3.setOnKeyListener(myOnKeyListener);

        myOnFocusChangeListener = new MyOnFocusChangeListener();
        mBtnMedia3.setOnFocusChangeListener(myOnFocusChangeListener);
        mBtnKok3.setOnFocusChangeListener(myOnFocusChangeListener);
        mBtnPandora3.setOnFocusChangeListener(myOnFocusChangeListener);

        FocusView mFocusView = (FocusView) getActivity().findViewById(R.id.fragment_selector);
        mViewBean.setmFocusObject(mFocusView);

        addViewGlobalLayoutListener(mBtnMedia3, mViewBean);
        addViewGlobalLayoutListener(mBtnKok3, mViewBean);
        addViewGlobalLayoutListener(mBtnPandora3, mViewBean);

        if (mViewBean.getmCurFocusView() == null) {
            mBtnMedia3.requestFocus();
        }
    }

    /*
     * handler click for imageButton
     */
    private class MyOnClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            Log.d(TAG, "success start onclick!!!!!");
            Bundle mBundle = new Bundle();
            switch (v.getId()) {
                case R.id.btn_media3:
                    mBundle.putString("gotoMediaPlayer", "photo");
                    mContext.mainLogic.startApk("com.hrtvbic.usb.S6A918", "com.hrtvbic.usb.S6A918.MainActivity",
                            mBundle);
                    break;
                case R.id.btn_kok3:
                    if (Utils.isApkExist(mContext, "com.heran.kok")) {
                        mContext.mainLogic.startApk("com.heran.kok", "com.heran.kok.MainActivity", mBundle);
                    } else {
                        UIUtil.showToast(mContext, mContext.getResources().getString(R.string.not_install_ko_apk));
                    }
                    break;
                case R.id.btn_pandora3:
                    // 全畫面的Pandora
                    Intent intent1 = new Intent(mContext, NewPandoraActivity.class);
                    startActivity(intent1);
                    UIUtil.showToast(mContext, mContext.getResources().getString(R.string.start_pandora));
                    break;
            }
        }
    }

    /*
     * handler key event
     */
    private class MyOnKeyListener implements OnKeyListener {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            // TODO Auto-generated method stub
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                Log.d(TAG, "success start onkey!!!!!");
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    mContext.mviewHolder.updateFragment(Constants.LIVINGAREAVIEW);
                } else {
                    switch (v.getId()) {
                        case R.id.btn_media3:
                            if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                                mBtnKok3.requestFocus();
                                return true;
                            } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                                mfragmentBtn.requestFocus();
//                                mViewBean.setmCurFocusView(mfragmentBtn);
//                                mViewBean.getmFocusObject().startAnimation(mfragmentBtn);
                            } else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                                return true;
                            }
                            break;
                        case R.id.btn_kok3:
                            if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                                return true;
                            } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                                mContext.mviewHolder.setHomeBtnFocus(0);
                                return true;
                            }
                            break;
                        case R.id.btn_pandora3:
                            if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                                mfragmentBtn.requestFocus();
//                                mViewBean.setmCurFocusView(mfragmentBtn);
//                                mViewBean.getmFocusObject().startAnimation(mfragmentBtn);
                            } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                                mContext.mviewHolder.setHomeBtnFocus(0);
                                return true;
                            }
                            break;
                        default:
                            break;
                    }
                }
            }
            return false;
        }
    }

    /**
     * handle the button focus change event
     */
    private class MyOnFocusChangeListener implements OnFocusChangeListener {
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
    public void onResume() {
        super.onResume();
        Log.d(TAG, "EntertainmentFragment33333 onResume!!");
        if (Constants.SkipPandora != "1" && Constants.kok_device == true) {
            // pandora & kok & mediabase open
            Log.d(TAG, "Constants.kok_device=" + String.valueOf(Constants.kok_device));
            mContext.mviewHolder.updateFragment(Constants.ENTERTAINMENT3);
        } else {
            Log.d(TAG, "Constants.kok_device=" + String.valueOf(Constants.SkipPandora));
            Log.d(TAG, "Constants.kok_device=" + String.valueOf(Constants.kok_device));
            mContext.mviewHolder.updateFragment(Constants.ENTERTAINMENT1);
        }
    }
}
