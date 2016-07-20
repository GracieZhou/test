
package com.google.tv.eoslauncher.ui.fragment;

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
import android.widget.ImageButton;

import com.google.tv.eoslauncher.HomeActivity;
import com.google.tv.eoslauncher.HomeApplication;
import com.google.tv.eoslauncher.MainViewHolder;
import com.google.tv.eoslauncher.R;
import com.google.tv.eoslauncher.model.MyAD;
import com.google.tv.eoslauncher.model.ViewBean;
import com.google.tv.eoslauncher.ui.app.AppStoreActivity;
import com.google.tv.eoslauncher.util.Constants;
import com.google.tv.eoslauncher.util.FocusView;
import com.google.tv.eoslauncher.util.GoogleAnalyticsUtil;
import com.google.tv.eoslauncher.util.HistoryRec;
import com.google.tv.eoslauncher.util.UIUtil;
import com.google.tv.eoslauncher.util.Utils;

/**
 * projectName： EosLauncher moduleName： ShopFragment.java
 * 
 * @author chadm.xiang
 * @version 1.0.0
 * @time 2013-12-18 下午2:10:03
 * @Copyright © 2013 Eos Inc.
 */
public class ShopFragment extends PublicFragment {

    private HomeActivity mContext;

    private MainViewHolder mHolder;

    private ImageButton bt_left;

    private ImageButton bt_top;

    private ImageButton bt_bottom;

    private FocusView mFocusView;

    private MyOnFocusChangeListener mFocusChangeListener;

    private ShopOnClickListener mClickListener;

    private ViewBean mViewBean;
    
    private String recData;

    public ViewBean getmViewBean() {
        return mViewBean;
    }

    public void setmViewBean(ViewBean mViewBean) {
        this.mViewBean = mViewBean;
    }

    public ShopFragment() {
        super();
    }

    public ShopFragment(HomeActivity context, MainViewHolder mHolder) {
        this.mContext = context;
        this.mHolder = mHolder;
        mViewBean = new ViewBean(null, null);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mview = inflater.inflate(R.layout.shop_main, container, false);

        mFocusChangeListener = new MyOnFocusChangeListener();
        mClickListener = new ShopOnClickListener();

        bt_left = (ImageButton) mview.findViewById(R.id.web_bt_01);
        bt_top = (ImageButton) mview.findViewById(R.id.web_bt_02);
        bt_bottom = (ImageButton) mview.findViewById(R.id.web_bt_03);

        mFocusView = (FocusView) mview.findViewById(R.id.home_selector);
        mViewBean.setmFocusObject(mFocusView);
        mFocusView.setVisibility(View.INVISIBLE);
        if (mViewBean.getmCurFocusView() == null) {
            mViewBean.setmCurFocusView(bt_left);
        } else if (mViewBean.getmCurFocusView() == mHolder.home_content) {
            // if curFoucesView is the mark set in restoreFragmentFocus,set the
            // curFoucesView to the left view
            mViewBean.setmCurFocusView(bt_top);
        }

        if (android.provider.Settings.System.getString(mContext.getContentResolver(), "SkipPandora") == null) {
            android.provider.Settings.System.putString(mContext.getContentResolver(), "SkipPandora", Constants.PPasswd);
        } else {
            Constants.SkipPandora = android.provider.Settings.System.getString(mContext.getContentResolver(),
                    "SkipPandora");
        }
        initButtonListener();

        addViewGlobalLayoutListener(bt_left, mViewBean);
        addViewGlobalLayoutListener(bt_top, mViewBean);
        addViewGlobalLayoutListener(bt_bottom, mViewBean);

        if (!Utils.isNetworkState) {
            bt_left.setBackground(HomeApplication.getInstance().getResources().getDrawable(R.drawable.shop_left));
            bt_top.setBackground(HomeApplication.getInstance().getResources().getDrawable(R.drawable.shop_right_up));
            bt_bottom.setBackground(HomeApplication.getInstance().getResources()
                    .getDrawable(R.drawable.shop_right_down));
        } else {
            bt_left.setBackground(HomeApplication.getInstance().getResources().getDrawable(R.drawable.shop_left));
            bt_top.setBackground(HomeApplication.getInstance().getResources().getDrawable(R.drawable.shop_right_up2));
            bt_bottom.setBackground(HomeApplication.getInstance().getResources()
                    .getDrawable(R.drawable.shop_right_down));
        }

        setRetainInstance(true);

        return mview;
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
        if (mClickListener != null) {
            mClickListener = null;
        }
        super.onDestroy();
        bt_top.setBackground(null);
        bt_bottom.setBackground(null);
        bt_left.setBackground(null);
        System.gc();
    }

    private void initButtonListener() {
        bt_left.setOnFocusChangeListener(mFocusChangeListener);
        bt_top.setOnFocusChangeListener(mFocusChangeListener);
        bt_bottom.setOnFocusChangeListener(mFocusChangeListener);

        bt_left.setOnClickListener(mClickListener);
        bt_top.setOnClickListener(mClickListener);
        bt_bottom.setOnClickListener(mClickListener);

        bt_left.setOnKeyListener(new OnKeyListener() {

            @Override
            public boolean onKey(View arg0, int arg1, KeyEvent arg2) {
                // if the fragment animation is running,return true
                if (isRunning) {
                    return true;
                }
                if (arg2.getAction() == KeyEvent.ACTION_DOWN && arg1 == KeyEvent.KEYCODE_DPAD_LEFT) {
                    mViewBean.setmCurFocusView(bt_left);
                    Log.d("ShopFragment", "sam onCreate Constants.kok_device " + Constants.kok_device);
                    if (Constants.SkipPandora.equalsIgnoreCase("1")) {
                        mContext.showViews(Constants.MEDIAVIEW);
                    } else {
                        mContext.showViews(Constants.PANDORAVIEW);
                    }
                    return true;
                }
                return false;
            }
        });

        bt_top.setOnKeyListener(new OnKeyListener() {

            @Override
            public boolean onKey(View arg0, int arg1, KeyEvent arg2) {
                // if the fragment animation is running,return true
                if (isRunning) {
                    return true;
                }
                if (arg2.getAction() == KeyEvent.ACTION_DOWN && arg1 == KeyEvent.KEYCODE_DPAD_RIGHT) {
                    mViewBean.setmCurFocusView(bt_top);
                    mContext.showViews(Constants.HOMEVIEW);
                    mHolder.cleanFragmentAllFocus();
                    return true;
                }
                return false;
            }
        });

        bt_bottom.setOnKeyListener(new OnKeyListener() {

            @Override
            public boolean onKey(View arg0, int arg1, KeyEvent arg2) {
                // if the fragment animation is running,return true
                if (isRunning) {
                    return true;
                }
                if (arg2.getAction() == KeyEvent.ACTION_DOWN && arg1 == KeyEvent.KEYCODE_DPAD_RIGHT) {
                    mViewBean.setmCurFocusView(bt_bottom);
                    mContext.showViews(Constants.HOMEVIEW);
                    mHolder.cleanFragmentAllFocus();
                    return true;
                }
                return false;
            }
        });

    }

    private void goToWebUrl(String url) {
        Intent intent = new Intent(mContext, AppStoreActivity.class);
        Bundle bundle = new Bundle();
        if (url == null || url.equals("")) {
            url = Constants.defaultURL;
        }
        bundle.putString("URL", url);
        intent.putExtras(bundle);
        startActivity(intent);
    }

    class MyOnFocusChangeListener implements OnFocusChangeListener {
        @Override
        public void onFocusChange(View view, boolean hasFocus) {
            if (hasFocus) {
                mFocusView.startAnimation(view);
                mViewBean.setmCurFocusView(view);
            }
        }
    }

    class ShopOnClickListener implements OnClickListener {

        @Override
        public void onClick(View view) {
            String clickUrl = null;
            if (!Utils.isNetworkState) {
                UIUtil.toastShow(R.string.shop_no_network, getActivity());
                return;
            }
            MyAD adinfo = new MyAD();
            switch (view.getId()) {
                case R.id.web_bt_01:
                    clickUrl = "http://www.jowinwin.com/hertv2msd/index.php?r=site/hot";

                    adinfo.setGln(clickUrl);
                    GoogleAnalyticsUtil.sendEvent(GoogleAnalyticsUtil.FROM_SHOPPING, adinfo, 0);

                    recData = HistoryRec.block[3] + ',' + HistoryRec.block4Action[0] + ',' + clickUrl + ',' + HistoryRec.getCurrentDateTime();
                  Log.d("rec", "tempData:" + recData);
                  HistoryRec.writeToFile(recData);
                  recData = "";
                    
                    goToWebUrl(clickUrl);
                    break;
                case R.id.web_bt_02:
                    clickUrl = "http://www.jowinwin.com/hertv2msd/index.php?r=site/rack";

                    adinfo.setGln(clickUrl);
                    GoogleAnalyticsUtil.sendEvent(GoogleAnalyticsUtil.FROM_SHOPPING, adinfo, 1);
                    
                    recData = HistoryRec.block[3] + ',' + HistoryRec.block4Action[1] + ',' + clickUrl + ',' + HistoryRec.getCurrentDateTime();
                  Log.d("rec", "tempData:" + recData);
                  HistoryRec.writeToFile(recData);
                  recData = "";

                    goToWebUrl(clickUrl);
                    break;
                case R.id.web_bt_03:
                    clickUrl = "http://www.jowinwin.com/hertv2msd/index.php?r=member/member";

                    adinfo.setGln(clickUrl);
                    GoogleAnalyticsUtil.sendEvent(GoogleAnalyticsUtil.FROM_SHOPPING, adinfo, 2);

                    recData = HistoryRec.block[3] + ',' + HistoryRec.block4Action[2] + ',' + clickUrl + ',' + HistoryRec.getCurrentDateTime();
                  Log.d("rec", "tempData:" + recData);
                  HistoryRec.writeToFile(recData);
                  recData = "";
                    
                    goToWebUrl(clickUrl);
                    break;
                default:
                    break;
            }
        }
    }

}
