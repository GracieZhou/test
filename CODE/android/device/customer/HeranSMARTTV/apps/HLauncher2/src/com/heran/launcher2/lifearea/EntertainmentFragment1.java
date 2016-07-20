
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
import android.widget.ImageView;

public class EntertainmentFragment1 extends PublicFragment {

    private static final String TAG = "EntertainmentFragment1";

    private Button mBtnMedia1;

    private Button mBtnKok1;

    private ImageView mKokshadow1;

    public ImageButton mFragmentBtn;

    private MyOnClickListener myOnClickListener;

    private MyOnKeyListener myOnKeyListener;

    private MyOnFocusChangeListener myOnFocusChangeListener;

    // 記錄當下frafment狀態 → 0:只有媒體庫，1:媒體庫+KOK，2:媒體庫+KOK
    private int mBtnState = 2;

    private final HomeActivity mContext;

    private final ViewBean mViewBean;

    public EntertainmentFragment1(HomeActivity context) {
        super();
        mContext = context;
        mViewBean = new ViewBean(null, null);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mview = inflater.inflate(R.layout.livingarea_entertainment1, container, false);
        findViews(mview);
        initViews(mview);
        return mview;
    }

    private void findViews(View mview) {
        mBtnMedia1 = (Button) mview.findViewById(R.id.btn_media1);
        mBtnKok1 = (Button) mview.findViewById(R.id.btn_kok1);
        mKokshadow1 = (ImageView) mview.findViewById(R.id.kok_shadow1);
        mFragmentBtn = (ImageButton) getActivity().findViewById(R.id.fragmentBtn);
    }

    private void initViews(View mview) {
        Log.d(TAG, "EntertainmentFragment1 in init!!");

        myOnClickListener = new MyOnClickListener();
        mBtnMedia1.setOnClickListener(myOnClickListener);
        mBtnKok1.setOnClickListener(myOnClickListener);

        myOnKeyListener = new MyOnKeyListener();
        mBtnMedia1.setOnKeyListener(myOnKeyListener);
        mBtnKok1.setOnKeyListener(myOnKeyListener);

        myOnFocusChangeListener = new MyOnFocusChangeListener();
        mBtnMedia1.setOnFocusChangeListener(myOnFocusChangeListener);
        mBtnKok1.setOnFocusChangeListener(myOnFocusChangeListener);

        changeBackgroundPic();

        FocusView mFocusView = (FocusView) getActivity().findViewById(R.id.fragment_selector);
        mViewBean.setmFocusObject(mFocusView);

        addViewGlobalLayoutListener(mBtnMedia1, mViewBean);
        addViewGlobalLayoutListener(mBtnKok1, mViewBean);

        if (mViewBean.getmCurFocusView() == null) {
            mViewBean.setmCurFocusView(mBtnMedia1);
        }
    }

    /*
     * check the satate and set background
     */
    public void changeBackgroundPic() {
        // 判斷畫面狀態和按鈕圖檔
        if (Constants.kok_device == false) {
            if (Constants.SkipPandora != null && Constants.SkipPandora.equals("1")) {
                // mediabase only
                mBtnKok1.setVisibility(View.GONE);
                mKokshadow1.setVisibility(View.GONE);
                mBtnState = 0;
            } else {
                // mediabase & pandora
                mBtnKok1.setBackgroundResource(R.drawable.pandora_m);
                mKokshadow1.setBackgroundResource(R.drawable.pandora_shadow_m);
                mBtnState = 1;
            }
        } else {
            // mediabase & kok
            mBtnState = 2;
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
                case R.id.btn_media1:
                    mBundle.putString("gotoMediaPlayer", "photo");
                    mContext.mainLogic.startApk("com.hrtvbic.usb.S6A918", "com.hrtvbic.usb.S6A918.MainActivity",
                            mBundle);
                    break;
                case R.id.btn_kok1:
                    if (mBtnState == 2) {
                        if (Utils.isApkExist(mContext, "com.heran.kok")) {
                            mContext.mainLogic.startApk("com.heran.kok", "com.heran.kok.MainActivity", mBundle);
                            UIUtil.showToast(mContext, mContext.getResources().getString(R.string.ko_apk));
                        } else {
                            UIUtil.showToast(mContext, mContext.getResources().getString(R.string.not_install_ko_apk));
                        }
                    } else if (mBtnState == 1) {
                        Intent intent1 = new Intent(mContext, NewPandoraActivity.class);
                        startActivity(intent1);
                    }
                    break;
                default:
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
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    mContext.mviewHolder.updateFragment(Constants.LIVINGAREAVIEW);
                } else {
                    switch (v.getId()) {
                        case R.id.btn_media1:
                            if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                                mFragmentBtn.requestFocus();
//                                mViewBean.setmCurFocusView(mFragmentBtn);
//                                mViewBean.getmFocusObject().startAnimation(mFragmentBtn);
                            } else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                                return true;
                            }
                            break;
                        case R.id.btn_kok1:
                            if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                                mFragmentBtn.requestFocus();
//                                mViewBean.setmCurFocusView(mFragmentBtn);
//                                mViewBean.getmFocusObject().startAnimation(mFragmentBtn);
                            } else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                                return true;
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
        changeBackgroundPic();
    }
}
