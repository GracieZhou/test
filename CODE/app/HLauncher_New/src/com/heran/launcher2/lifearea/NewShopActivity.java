/**
 * 新UI的商城，全螢幕
 */

package com.heran.launcher2.lifearea;

import com.heran.launcher2.HomeApplication;
import com.heran.launcher2.PublicFragment;
import com.heran.launcher2.R;
import com.heran.launcher2.advert.MyAD;
import com.heran.launcher2.eosweb.MyWebViewActivity;
import com.heran.launcher2.util.Constants;
import com.heran.launcher2.util.FocusView;
import com.heran.launcher2.util.GoogleAnalyticsUtil;
import com.heran.launcher2.util.UIUtil;
import com.heran.launcher2.util.Utils;
import com.heran.launcher2.widget.ViewBean;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.Window;
import android.widget.ImageButton;

public class NewShopActivity extends Activity {

    private ImageButton btn_shopLeft, btn_shopTop, btn_shopBottom;

    private MyOnClickListener myOnClickListener;

    private MyOnKeyListener myOnKeyListener;

    private MyOnFocusChangeListener myOnFocusChangeListener;

    private ViewBean mViewBean;

    private FocusView myFocusView;

    private final String TAG = "NewShopActivity";

    public ViewBean getmViewBean() {
        return mViewBean;
    }

    public void setmViewBean(ViewBean mViewBean) {
        this.mViewBean = mViewBean;
    }

    public NewShopActivity() {
        mViewBean = new ViewBean(null, null);
        Log.d(TAG, "NewShopFragment ");
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        Log.d(TAG, "NewShopFragment---oncreat");
        requestWindowFeature(Window.FEATURE_NO_TITLE);
        setContentView(R.layout.shop_main);
        Log.d(TAG, "NewShopFragment---go to init");
        findViews();
        initView();
    }

    private void findViews() {
        btn_shopLeft = (ImageButton) this.findViewById(R.id.web_bt_01);
        btn_shopTop = (ImageButton) this.findViewById(R.id.web_bt_02);
        btn_shopBottom = (ImageButton) this.findViewById(R.id.web_bt_03);
        myFocusView = (FocusView) this.findViewById(R.id.shop_selector);
    }

    private void initView() {
        Log.d(TAG, "NewShopFragment---start init!!!");

        myOnClickListener = new MyOnClickListener();
        btn_shopLeft.setOnClickListener(myOnClickListener);
        btn_shopTop.setOnClickListener(myOnClickListener);
        btn_shopBottom.setOnClickListener(myOnClickListener);

        myOnKeyListener = new MyOnKeyListener();
        btn_shopLeft.setOnKeyListener(myOnKeyListener);
        btn_shopTop.setOnKeyListener(myOnKeyListener);
        btn_shopBottom.setOnKeyListener(myOnKeyListener);

        myOnFocusChangeListener = new MyOnFocusChangeListener();
        btn_shopLeft.setOnFocusChangeListener(myOnFocusChangeListener);
        btn_shopTop.setOnFocusChangeListener(myOnFocusChangeListener);
        btn_shopBottom.setOnFocusChangeListener(myOnFocusChangeListener);

        mViewBean.setmFocusObject(myFocusView);
        myFocusView.setVisibility(View.INVISIBLE);

        PublicFragment pu = new PublicFragment();
        pu.addViewGlobalLayoutListener(btn_shopLeft, mViewBean);
        pu.addViewGlobalLayoutListener(btn_shopTop, mViewBean);
        pu.addViewGlobalLayoutListener(btn_shopBottom, mViewBean);

        if (!Utils.isNetworkState) {
            btn_shopLeft.setBackground(HomeApplication.getInstance().getResources().getDrawable(R.drawable.shop_left));
            btn_shopTop
                    .setBackground(HomeApplication.getInstance().getResources().getDrawable(R.drawable.shop_right_up));
            btn_shopBottom
                    .setBackground(HomeApplication.getInstance().getResources().getDrawable(R.drawable.shop_right_up));
        } else {
            btn_shopLeft.setBackground(HomeApplication.getInstance().getResources().getDrawable(R.drawable.shop_left));
            btn_shopTop
                    .setBackground(HomeApplication.getInstance().getResources().getDrawable(R.drawable.shop_right_up1));
            btn_shopBottom
                    .setBackground(HomeApplication.getInstance().getResources().getDrawable(R.drawable.shop_right_up2));
        }
        Log.d(TAG, "NewShopFragment---start end!!!");
    }

    private class MyOnClickListener implements OnClickListener {
        @Override
        public void onClick(View view) {
            // TODO Auto-generated method stub
            String clickUrl = null;
            if (!Utils.isNetworkState) {
                UIUtil.toastShow(R.string.shop_no_network, getApplicationContext());
                return;
            }
            MyAD adinfo = new MyAD();
            switch (view.getId()) {
                case R.id.web_bt_01:
                    clickUrl = "http://test.jowinwin.com/hertv2msd/index.php?r=site/rack";
                    adinfo.setGln((clickUrl));
                    GoogleAnalyticsUtil.sendEvent(GoogleAnalyticsUtil.FROM_SHOPPING, adinfo, 0);
                    goToWebUrl(clickUrl);
                    break;
                case R.id.web_bt_02:
                    clickUrl = "http://www.jowinwin.com/hertv2msd/index.php?r=site/PRack&rid=37&rackname=%E7%A6%BE%E8%81%AF%E5%AE%B6%E9%9B%BB";
                    adinfo.setGln(clickUrl);
                    GoogleAnalyticsUtil.sendEvent(GoogleAnalyticsUtil.FROM_SHOPPING, adinfo, 1);
                    goToWebUrl(clickUrl);
                    break;
                case R.id.web_bt_03:
                    clickUrl = "http://www.jowinwin.com/hertv2msd/index.php?r=site/PRack&rid=8&rackname=%E6%97%A5%E5%85%89%E7%94%9F%E6%B4%BB";
                    adinfo.setGln(clickUrl);
                    GoogleAnalyticsUtil.sendEvent(GoogleAnalyticsUtil.FROM_SHOPPING, adinfo, 2);
                    goToWebUrl(clickUrl);
                    break;
                default:
                    break;
            }
        }
    }

    private class MyOnKeyListener implements OnKeyListener {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            // TODO Auto-generated method stub
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                switch (v.getId()) {
                    case R.id.web_bt_01:
                        if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {

                        }
                        break;
                    case R.id.web_bt_03:
                        if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {

                        }
                        break;
                }
            }
            return false;
        }
    }

    private class MyOnFocusChangeListener implements OnFocusChangeListener {
        @Override
        public void onFocusChange(View view, boolean hasFocus) {
            if (hasFocus) {
                myFocusView.startAnimation(view);
                mViewBean.setmCurFocusView(view);
            }
        }
    }

    private void goToWebUrl(String url) {
        Intent intent = new Intent(this, MyWebViewActivity.class);
        Bundle bundle = new Bundle();
        if (url == null || url.equals("")) {
            url = Constants.defaultURL;
        }
        bundle.putString("URL", url);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    @Override
    public void onStart() {
        super.onStart();

    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onDestroy() {
        if (myOnClickListener != null) {
            myOnClickListener = null;
        }
        super.onDestroy();
        btn_shopTop.setBackground(null);
        btn_shopBottom.setBackground(null);
        btn_shopLeft.setBackground(null);
        System.gc();
    }

}
