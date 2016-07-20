/**
 * Sarah修改：生活專區(LivingArea) 
 */

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
 * projectName： EosLauncher moduleName： MediaFragment.java
 * 
 * @author chadm.xiang
 * @version 1.0.0
 * @time 2013-12-18 下午2:04:41
 * @Copyright © 2013 Eos Inc.
 */

public class LivingAreaFragment extends PublicFragment {

    private Button nBtnEntertainment, nBtnEducation, nBtnShopping;

    private MyOnClickListener myOnClickListener;

    private MyOnKeyListener myOnKeyListener;

    private MyOnFocusChangeListener myOnFocusChangeListener;

    private HomeActivity mContext;

    private MainViewHolder mViewHolder;

    private ViewBean mViewBean;

    public ImageButton fragmentBtn;

    public ViewBean getmViewBean() {
        return mViewBean;
    }

    public void setmViewBean(ViewBean mViewBean) {
        this.mViewBean = mViewBean;
    }

    public LivingAreaFragment(HomeActivity context, MainViewHolder mViewHolder) {
        super();
        this.mContext = context;
        this.mViewHolder = mViewHolder;
        mViewBean = new ViewBean(null, null);
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
        nBtnEntertainment = (Button) mview.findViewById(R.id.btn_entertainment);
        nBtnEducation = (Button) mview.findViewById(R.id.btn_education);
        nBtnShopping = (Button) mview.findViewById(R.id.btn_shopping);
        fragmentBtn = (ImageButton) getActivity().findViewById(R.id.fragmentBtn);
    }

    private void initViews(View mview) {
        myOnClickListener = new MyOnClickListener();
        nBtnEntertainment.setOnClickListener(myOnClickListener);
        nBtnEducation.setOnClickListener(myOnClickListener);
        nBtnShopping.setOnClickListener(myOnClickListener);

        myOnKeyListener = new MyOnKeyListener();
        nBtnEntertainment.setOnKeyListener(myOnKeyListener);
        nBtnEducation.setOnKeyListener(myOnKeyListener);
        nBtnShopping.setOnKeyListener(myOnKeyListener);

        myOnFocusChangeListener = new MyOnFocusChangeListener();
        nBtnEntertainment.setOnFocusChangeListener(myOnFocusChangeListener);
        nBtnEducation.setOnFocusChangeListener(myOnFocusChangeListener);
        nBtnShopping.setOnFocusChangeListener(myOnFocusChangeListener);

        FocusView mFocusView = (FocusView) getActivity().findViewById(R.id.fragment_selector);
        mViewBean.setmFocusObject(mFocusView);

        addViewGlobalLayoutListener(nBtnEntertainment, mViewBean);
        addViewGlobalLayoutListener(nBtnEducation, mViewBean);
        addViewGlobalLayoutListener(nBtnShopping, mViewBean);

        if (mViewBean.getmCurFocusView() == null) {
            nBtnEntertainment.requestFocus();
        }
    }

    private class MyOnClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            Intent intent1;
            switch (v.getId()) {
                case R.id.btn_entertainment:
                    if (Constants.SkipPandora != "1" && Constants.kok_device == true) {
                        mViewHolder.updateFragment(Constants.ENTERTAINMENT3);
                    } else {
                        mViewHolder.updateFragment(Constants.ENTERTAINMENT1);
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
            }
        }
    }

    private class MyOnKeyListener implements OnKeyListener {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                switch (v.getId()) {
                    case R.id.btn_entertainment:
                        if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                            fragmentBtn.requestFocus();
                            mViewBean.setmCurFocusView(fragmentBtn);
                            mViewBean.getmFocusObject().startAnimation(fragmentBtn);
                        } else if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                            nBtnEducation.requestFocus();
                            return true;
                        } else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                            return true;
                        }
                        break;
                    case R.id.btn_education:
                        if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                            return true;
                        }
                        break;
                    case R.id.btn_shopping:
                        if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                            fragmentBtn.requestFocus();
                            mViewBean.setmCurFocusView(fragmentBtn);
                            mViewBean.getmFocusObject().startAnimation(fragmentBtn);
                        }
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
        if (mViewBean.getmCurFocusView() == nBtnShopping) {
            nBtnShopping.requestFocus();
        } else if (mViewBean.getmCurFocusView() == nBtnEducation) {
            nBtnEducation.requestFocus();
        } else {
            nBtnEntertainment.requestFocus();
        }
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
