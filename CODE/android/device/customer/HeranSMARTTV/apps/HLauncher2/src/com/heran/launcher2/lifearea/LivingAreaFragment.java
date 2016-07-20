
package com.heran.launcher2.lifearea;

import com.heran.launcher2.HomeActivity;
import com.heran.launcher2.MainViewHolder;
import com.heran.launcher2.PublicFragment;
import com.heran.launcher2.R;
import com.heran.launcher2.util.Constants;
import com.heran.launcher2.util.FocusView;
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

/**
 * projectName： EosLauncher moduleName： LivingAreaFragment.java
 * 
 * @author junling.hou
 * @version 1.0.0
 * @time 2016-3-31 下午2:04:41
 * @Copyright © 2013 Eos Inc.
 */

public class LivingAreaFragment extends PublicFragment {

    private Button mBtnEntertainment;

    private Button mBtnEducation;

    private Button mBtnShopping;

    private MyOnClickListener myOnClickListener;

    private MyOnKeyListener myOnKeyListener;

    private MyOnFocusChangeListener myOnFocusChangeListener;

    private final HomeActivity mContext;

    private final ViewBean mViewBean;

    public ImageButton mFragmentBtn;
    
    MainViewHolder mHolder ;

    public ViewBean getmViewBean() {
        return mViewBean;
    }

    public LivingAreaFragment(HomeActivity context) {
        super();
        mContext = context;
        mViewBean = new ViewBean(null, null);
        mHolder = mContext.mviewHolder;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mview = inflater.inflate(R.layout.livingarea_main, container, false);
        findViews(mview);
        initViews(mview);
        setRetainInstance(true);
        return mview;
    }

    private void findViews(View mview) {
        mBtnEntertainment = (Button) mview.findViewById(R.id.btn_entertainment);
        mBtnEducation = (Button) mview.findViewById(R.id.btn_education);
        mBtnShopping = (Button) mview.findViewById(R.id.btn_shopping);
        mFragmentBtn = (ImageButton) getActivity().findViewById(R.id.fragmentBtn);
    }

    private void initViews(View mview) {
        myOnClickListener = new MyOnClickListener();
        mBtnEntertainment.setOnClickListener(myOnClickListener);
        mBtnEducation.setOnClickListener(myOnClickListener);
        mBtnShopping.setOnClickListener(myOnClickListener);

        myOnKeyListener = new MyOnKeyListener();
        mBtnEntertainment.setOnKeyListener(myOnKeyListener);
        mBtnEducation.setOnKeyListener(myOnKeyListener);
        mBtnShopping.setOnKeyListener(myOnKeyListener);

        myOnFocusChangeListener = new MyOnFocusChangeListener();
        mBtnEntertainment.setOnFocusChangeListener(myOnFocusChangeListener);
        mBtnEducation.setOnFocusChangeListener(myOnFocusChangeListener);
        mBtnShopping.setOnFocusChangeListener(myOnFocusChangeListener);

        FocusView mFocusView = (FocusView) getActivity().findViewById(R.id.fragment_selector);
        mViewBean.setmFocusObject(mFocusView);

        addViewGlobalLayoutListener(mBtnEntertainment, mViewBean);
        addViewGlobalLayoutListener(mBtnEducation, mViewBean);
        addViewGlobalLayoutListener(mBtnShopping, mViewBean);

        if (mViewBean.getmCurFocusView() == null) {
            mBtnEntertainment.requestFocus();
        }
    }

    /*
     * handler click for imageButton of LivingAreaFragement
     */
    private class MyOnClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intent1;
            switch (v.getId()) {
                case R.id.btn_entertainment:
                    if (Constants.SkipPandora != "1" && Constants.kok_device == true) {
                        mContext.mviewHolder.updateFragment(Constants.ENTERTAINMENT3);
                    } else {
                        mContext.mviewHolder.updateFragment(Constants.ENTERTAINMENT1);
                    }
                    break;
                case R.id.btn_education:
                    // 全畫面的商城
                    intent1 = new Intent(mContext, ChargeActivity.class);
                    startActivityForResult(intent1, 1);
                    break;
                case R.id.btn_shopping:
                    // 全畫面的商城
                    intent1 = new Intent(mContext, NewShopActivity.class);
                    startActivity(intent1);
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
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                switch (v.getId()) {
                    case R.id.btn_entertainment:
                        if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                            mFragmentBtn.requestFocus();
//                            mViewBean.setmCurFocusView(mFragmentBtn);
//                            mViewBean.getmFocusObject().startAnimation(mFragmentBtn);
                        } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                            mBtnEducation.requestFocus();
                            return true;
                        } else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                            return true;
                        }
                        break;
                    case R.id.btn_education:
                        if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                            return true;
                        }
                        if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                            mContext.mviewHolder.setHomeBtnFocus(0);
                            return true;
                        }
                        break;
                    case R.id.btn_shopping:
                        if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                            mFragmentBtn.requestFocus();
//                            mViewBean.setmCurFocusView(mFragmentBtn);
//                            mViewBean.getmFocusObject().startAnimation(mFragmentBtn);
                        }
                        if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                            mContext.mviewHolder.setHomeBtnFocus(0);
                            return true;
                        }
                        break;
                    default:
                        break;
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
        if (mViewBean.getmCurFocusView() == mBtnShopping) {
            mBtnShopping.requestFocus();
        } else if (mViewBean.getmCurFocusView() == mBtnEducation) {
            mBtnEducation.requestFocus();
        } else {
            mBtnEntertainment.requestFocus();
        }
        mHolder.updatePIFragment(Constants.CITYSELECT_CLOSE,2);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        Log.d("LivingAreaFragment", "in Destory");
        mBtnEntertainment = null;
        mBtnEducation = null;
        mBtnShopping = null;
        mFragmentBtn = null;
    }

}
