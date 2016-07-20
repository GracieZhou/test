/**
 * 新UI的商城，全螢幕
 */

package com.heran.launcher2.lifearea;

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

public class NewShopActivity extends Activity {

    private final String TAG = "NewShopActivity";

    private ImageButton mBtnShopLeft;

    private ImageButton mBtnShopTop;

    private ImageButton mBtnShopBottom;

    private MyOnClickListener myOnClickListener;

    private MyOnKeyListener myOnKeyListener;

    private MyOnFocusChangeListener myOnFocusChangeListener;

    private final ViewBean mViewBean;

    private FocusView myFocusView;

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
        mBtnShopLeft = (ImageButton) this.findViewById(R.id.web_bt_01);
        mBtnShopTop = (ImageButton) this.findViewById(R.id.web_bt_02);
        mBtnShopBottom = (ImageButton) this.findViewById(R.id.web_bt_03);
        myFocusView = (FocusView) this.findViewById(R.id.shop_selector);
    }

    private void initView() {
        myOnClickListener = new MyOnClickListener();
        mBtnShopLeft.setOnClickListener(myOnClickListener);
        mBtnShopTop.setOnClickListener(myOnClickListener);
        mBtnShopBottom.setOnClickListener(myOnClickListener);

        myOnKeyListener = new MyOnKeyListener();
        mBtnShopLeft.setOnKeyListener(myOnKeyListener);
        mBtnShopTop.setOnKeyListener(myOnKeyListener);
        mBtnShopBottom.setOnKeyListener(myOnKeyListener);

        myOnFocusChangeListener = new MyOnFocusChangeListener();
        mBtnShopLeft.setOnFocusChangeListener(myOnFocusChangeListener);
        mBtnShopTop.setOnFocusChangeListener(myOnFocusChangeListener);
        mBtnShopBottom.setOnFocusChangeListener(myOnFocusChangeListener);

        mViewBean.setmFocusObject(myFocusView);
        myFocusView.setVisibility(View.INVISIBLE);

        PublicFragment pu = new PublicFragment();
        pu.addViewGlobalLayoutListener(mBtnShopLeft, mViewBean);
        pu.addViewGlobalLayoutListener(mBtnShopTop, mViewBean);
        pu.addViewGlobalLayoutListener(mBtnShopBottom, mViewBean);

        if (Utils.isNetworkState) {
            mBtnShopLeft.setBackground(HomeApplication.getInstance().getResources().getDrawable(R.drawable.shop_left));
            mBtnShopTop.setBackground(HomeApplication.getInstance().getResources()
                    .getDrawable(R.drawable.shop_right_up1));
            mBtnShopBottom.setBackground(HomeApplication.getInstance().getResources()
                    .getDrawable(R.drawable.shop_right_up2));
        } else {
            mBtnShopLeft.setBackground(HomeApplication.getInstance().getResources().getDrawable(R.drawable.shop_left));
            mBtnShopTop.setBackground(HomeApplication.getInstance().getResources()
                    .getDrawable(R.drawable.shop_right_up));
            mBtnShopBottom.setBackground(HomeApplication.getInstance().getResources()
                    .getDrawable(R.drawable.shop_right_up));
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
                    clickUrl = "http://www.jowinwin.com/hertv2msd/index.php?r=site/hot";
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
                        break;
                    case R.id.web_bt_03:
                        break;
                    default:
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

    /*
     * Jump to a specific web page
     * @param url The web page URL
     */
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
    public void onDestroy() {
        if (myOnClickListener != null) {
            myOnClickListener = null;
        }
        super.onDestroy();
        mBtnShopTop.setBackground(null);
        mBtnShopBottom.setBackground(null);
        mBtnShopLeft.setBackground(null);
    }

}
