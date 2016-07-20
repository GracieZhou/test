/**
 * 20151218
 * Sarah修改：進入「樂」之後， Entertainment3
 */

package com.heran.launcher2.lifearea;

import com.heran.launcher2.HomeActivity;
import com.heran.launcher2.MainViewHolder;
import com.heran.launcher2.PublicFragment;
import com.heran.launcher2.R;
import com.heran.launcher2.util.Constants;
import com.heran.launcher2.util.FocusView;
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
import android.widget.Toast;

public class EntertainmentFragment3 extends PublicFragment {

    private Button nBtnMedia3, nBtnKok3, nBtnPandora3;

    public ImageButton fragmentBtn;

    private MyOnClickListener myOnClickListener;

    private MyOnKeyListener myOnKeyListener;

    private MyOnFocusChangeListener myOnFocusChangeListener;

    private HomeActivity mContext;

    private MainViewHolder mViewHolder;

    private ViewBean mViewBean;

    public ViewBean getmViewBean() {
        return mViewBean;
    }

    public void setmViewBean(ViewBean mViewBean) {
        this.mViewBean = mViewBean;
    }

    public EntertainmentFragment3(HomeActivity context, MainViewHolder mHolder) {
        super();
        this.mContext = context;
        this.mViewHolder = mHolder;
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
        nBtnMedia3 = (Button) mview.findViewById(R.id.btn_media3);
        nBtnKok3 = (Button) mview.findViewById(R.id.btn_kok3);
        nBtnPandora3 = (Button) mview.findViewById(R.id.btn_pandora3);
        fragmentBtn = (ImageButton) getActivity().findViewById(R.id.fragmentBtn);
    }

    private void initViews(View mview) {
        Log.d("newaa", "EntertainmentFragment33333 in init!!");

        myOnClickListener = new MyOnClickListener();
        nBtnMedia3.setOnClickListener(myOnClickListener);
        nBtnKok3.setOnClickListener(myOnClickListener);
        nBtnPandora3.setOnClickListener(myOnClickListener);

        myOnKeyListener = new MyOnKeyListener();
        nBtnMedia3.setOnKeyListener(myOnKeyListener);
        nBtnKok3.setOnKeyListener(myOnKeyListener);
        nBtnPandora3.setOnKeyListener(myOnKeyListener);

        myOnFocusChangeListener = new MyOnFocusChangeListener();
        nBtnMedia3.setOnFocusChangeListener(myOnFocusChangeListener);
        nBtnKok3.setOnFocusChangeListener(myOnFocusChangeListener);
        nBtnPandora3.setOnFocusChangeListener(myOnFocusChangeListener);

        FocusView mFocusView = (FocusView) getActivity().findViewById(R.id.fragment_selector);
        mViewBean.setmFocusObject(mFocusView);

        addViewGlobalLayoutListener(nBtnMedia3, mViewBean);
        addViewGlobalLayoutListener(nBtnKok3, mViewBean);
        addViewGlobalLayoutListener(nBtnPandora3, mViewBean);

        if (mViewBean.getmCurFocusView() == null) {
            nBtnMedia3.requestFocus();
        }
    }

    private class MyOnClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            Log.d("newaa", "success start onclick!!!!!");
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
                        Toast.makeText(mContext, "未安裝KOK APK", Toast.LENGTH_LONG).show();
                    }
                    break;
                case R.id.btn_pandora3:
                    // 全畫面的Pandora
                    Intent intent1 = new Intent(mContext, NewPandoraActivity.class);
                    startActivity(intent1);
                    Toast.makeText(mContext, "開啟Pandora!!", Toast.LENGTH_LONG).show();
                    break;
            }
        }
    }

    private class MyOnKeyListener implements OnKeyListener {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            // TODO Auto-generated method stub
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                Log.d("newaa", "success start onkey!!!!!");
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    mViewHolder.updateFragment(Constants.LIVINGAREAVIEW);
                } else {
                    switch (v.getId()) {
                        case R.id.btn_media3:
                            if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                                nBtnKok3.requestFocus();
                                return true;
                            } else if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                                fragmentBtn.requestFocus();
                                mViewBean.setmCurFocusView(fragmentBtn);
                                mViewBean.getmFocusObject().startAnimation(fragmentBtn);
                            } else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                                return true;
                            }
                            break;
                        case R.id.btn_kok3:
                            if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                                return true;
                            }
                            break;
                        case R.id.btn_pandora3:
                            if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                                fragmentBtn.requestFocus();
                                mViewBean.setmCurFocusView(fragmentBtn);
                                mViewBean.getmFocusObject().startAnimation(fragmentBtn);
                            }
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
        Log.d("newaa", "EntertainmentFragment33333 onResume!!");
        if (Constants.SkipPandora != "1" && Constants.kok_device == true) {
            // pandora & kok & mediabase open
            Log.d("newaa", "Constants.kok_device=" + String.valueOf(Constants.kok_device));
            mViewHolder.updateFragment(Constants.ENTERTAINMENT3);
        } else {
            Log.d("newaa", "Constants.kok_device=" + String.valueOf(Constants.SkipPandora));
            Log.d("newaa", "Constants.kok_device=" + String.valueOf(Constants.kok_device));
            mViewHolder.updateFragment(Constants.ENTERTAINMENT1);
        }
    }
}
