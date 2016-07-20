/**
 * 20151221
 * Sarah修改：生活專區進入「樂」之後，Entertainment1 
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
import android.widget.ImageView;
import android.widget.Toast;

public class EntertainmentFragment1 extends PublicFragment {

    private Button nBtnMedia1, nBtnKok1;

    private ImageView kok_shadow1;

    public ImageButton fragmentBtn;

    private MyOnClickListener myOnClickListener;

    private MyOnKeyListener myOnKeyListener;

    private MyOnFocusChangeListener myOnFocusChangeListener;

    // 記錄當下frafment狀態 → 0:只有媒體庫，1:媒體庫+KOK，2:媒體庫+KOK
    private int btnState = 2;

    private HomeActivity mContext;

    private MainViewHolder mViewHolder;

    private ViewBean mViewBean;

    public ViewBean getmViewBean() {
        return mViewBean;
    }

    public void setmViewBean(ViewBean mViewBean) {
        this.mViewBean = mViewBean;
    }

    public EntertainmentFragment1(HomeActivity context, MainViewHolder mHolder) {
        super();
        this.mContext = context;
        this.mViewHolder = mHolder;
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
        nBtnMedia1 = (Button) mview.findViewById(R.id.btn_media1);
        nBtnKok1 = (Button) mview.findViewById(R.id.btn_kok1);
        kok_shadow1 = (ImageView) mview.findViewById(R.id.kok_shadow1);
        fragmentBtn = (ImageButton) getActivity().findViewById(R.id.fragmentBtn);
    }

    private void initViews(View mview) {
        Log.d("newaa", "EntertainmentFragment1 in init!!");

        myOnClickListener = new MyOnClickListener();
        nBtnMedia1.setOnClickListener(myOnClickListener);
        nBtnKok1.setOnClickListener(myOnClickListener);

        myOnKeyListener = new MyOnKeyListener();
        nBtnMedia1.setOnKeyListener(myOnKeyListener);
        nBtnKok1.setOnKeyListener(myOnKeyListener);

        myOnFocusChangeListener = new MyOnFocusChangeListener();
        nBtnMedia1.setOnFocusChangeListener(myOnFocusChangeListener);
        nBtnKok1.setOnFocusChangeListener(myOnFocusChangeListener);

        changeBackgroundPic();

        FocusView mFocusView = (FocusView) getActivity().findViewById(R.id.fragment_selector);
        mViewBean.setmFocusObject(mFocusView);

        addViewGlobalLayoutListener(nBtnMedia1, mViewBean);
        addViewGlobalLayoutListener(nBtnKok1, mViewBean);

        if (mViewBean.getmCurFocusView() == null) {
            mViewBean.setmCurFocusView(nBtnMedia1);
        }
    }

    public void changeBackgroundPic() {
        // 判斷畫面狀態和按鈕圖檔
        if (Constants.kok_device == false) {
            if (Constants.SkipPandora != null && Constants.SkipPandora.equals("1")) {
                // mediabase only
                nBtnKok1.setVisibility(View.GONE);
                kok_shadow1.setVisibility(View.GONE);
                btnState = 0;
            } else {
                // mediabase & pandora
                nBtnKok1.setBackgroundResource(R.drawable.pandora_m);
                kok_shadow1.setBackgroundResource(R.drawable.pandora_shadow_m);
                btnState = 1;
            }
        } else {
            // mediabase & kok
            btnState = 2;
        }
    }

    private class MyOnClickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            // TODO Auto-generated method stub
            Log.d("newaa", "success start onclick!!!!!");
            Bundle mBundle = new Bundle();
            switch (v.getId()) {
                case R.id.btn_media1:
                    mBundle.putString("gotoMediaPlayer", "photo");
                    mContext.mainLogic.startApk("com.hrtvbic.usb.S6A918", "com.hrtvbic.usb.S6A918.MainActivity",
                            mBundle);
                    break;
                case R.id.btn_kok1:
                    if (btnState == 2) {
                        if (Utils.isApkExist(mContext, "com.heran.kok")) {
                            mContext.mainLogic.startApk("com.heran.kok", "com.heran.kok.MainActivity", mBundle);
                            Toast.makeText(mContext, "開啟KOK!!", Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(mContext, "未安裝KOK APK", Toast.LENGTH_LONG).show();
                        }
                    } else if (btnState == 1) {
                        Intent intent1 = new Intent(mContext, NewPandoraActivity.class);
                        startActivity(intent1);
                    }
                    break;
            }
        }
    }

    private class MyOnKeyListener implements OnKeyListener {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            // TODO Auto-generated method stub
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if (keyCode == KeyEvent.KEYCODE_BACK) {
                    mViewHolder.updateFragment(Constants.LIVINGAREAVIEW);
                } else {
                    switch (v.getId()) {
                        case R.id.btn_media1:
                            if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                                fragmentBtn.requestFocus();
                                mViewBean.setmCurFocusView(fragmentBtn);
                                mViewBean.getmFocusObject().startAnimation(fragmentBtn);
                            } else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                                return true;
                            }
                            break;
                        case R.id.btn_kok1:
                            if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                                fragmentBtn.requestFocus();
                                mViewBean.setmCurFocusView(fragmentBtn);
                                mViewBean.getmFocusObject().startAnimation(fragmentBtn);
                            } else if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                                return true;
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
        changeBackgroundPic();
    }
}
