
package com.eostek.scifly.browser;

import android.app.Fragment;
import android.app.FragmentManager;
import android.app.FragmentTransaction;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.webkit.WebView;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.android.browser.PageProgressView;
import com.eostek.scifly.browser.business.WebViewHelper;
import com.eostek.scifly.browser.collect.CollectFragment;
import com.eostek.scifly.browser.home.HomeFragment;
import com.eostek.scifly.browser.settool.SetToolFragment;
import com.eostek.scifly.browser.ui.MenuDialog;
import com.eostek.scifly.browser.util.Constants;

/**
 * projectName： Browser moduleName： BrowserHolder.java
 * 
 * @author Shirley.jiang & Ahri.chen
 * @time 2016-1-27 
 */
public class BrowserHolder {

    private final String TAG = "BrowserHolder";

    private BrowserActivity mActivity;

    public LinearLayout mHomeLayout;

    public LinearLayout mHomeMainTitleLl;

    public Button mHomeTitleView;

    public Button mCollectTitleView;

    public Button mSetToolTitleView;

    public LinearLayout mWebviewLayout;

    public RelativeLayout mWebLayout;

    public TextView mWebTitleView;

    public TextView mAdvanceSettingTv;

    public WebView mWebView;

    private PageProgressView mProgress;

    private FragmentTransaction mFragmentTransaction;

    private FragmentManager mFragmentManager;

    private HomeFragment mHomeFragment;

    private CollectFragment mCollectFragment;

    private SetToolFragment mSetToolFragment;

    private Fragment mCurrentFragment;

    private TitleItemClickListener mClickListener;

    private TitleItemFocusListener mFocusListener;

    public MenuDialog mNineDialog;

    public BrowserHolder(BrowserActivity activity) {
        mActivity = activity;
        mActivity.mHolder = this;
        mFragmentManager = mActivity.getFragmentManager();
        findView();
        setListener();
    }

    /**
     * init view.
     */
    public void findView() {
        mHomeLayout = (LinearLayout) mActivity.findViewById(R.id.home_layout);
        mHomeMainTitleLl = (LinearLayout) mActivity.findViewById(R.id.gv_title_bar);
        mHomeTitleView = (Button) mActivity.findViewById(R.id.home_text);
        mCollectTitleView = (Button) mActivity.findViewById(R.id.collect_text);
        mSetToolTitleView = (Button) mActivity.findViewById(R.id.set_tool_text);
        mWebviewLayout = (LinearLayout) mActivity.findViewById(R.id.web_layout);
        mWebLayout = (RelativeLayout) mActivity.findViewById(R.id.webview_layout);
        mWebTitleView = (TextView) mActivity.findViewById(R.id.title_bar);
        mAdvanceSettingTv = (TextView) mActivity.findViewById(R.id.advanced_settingtitle);
        mWebView = (WebView) mActivity.findViewById(R.id.webview);
        mProgress = (PageProgressView) mActivity.findViewById(R.id.progress);

        // add webview to multi-pages(Always keep there has a WebView)
        WebViewHelper.getInstance(mActivity).addWebView(mWebView, mProgress);

        // init three fragment.
        mHomeFragment = new HomeFragment(mActivity);
        mCollectFragment = new CollectFragment(mActivity);
        mSetToolFragment = new SetToolFragment(mActivity);
    }

    /**
     * setListener
     */
    private void setListener() {
        if (mClickListener == null) {
            mClickListener = new TitleItemClickListener();
        }
        mHomeTitleView.setOnClickListener(mClickListener);
        mCollectTitleView.setOnClickListener(mClickListener);
        mSetToolTitleView.setOnClickListener(mClickListener);

        if (mFocusListener == null) {
            mFocusListener = new TitleItemFocusListener();
        }
        mHomeTitleView.setOnFocusChangeListener(mFocusListener);
        mCollectTitleView.setOnFocusChangeListener(mFocusListener);
        mSetToolTitleView.setOnFocusChangeListener(mFocusListener);
    }

    /**
     * TitleItemClickListener
     * @author shirley
     *
     */
    private class TitleItemClickListener implements OnClickListener {

        @Override
        public void onClick(View view) {
            switch (view.getId()) {
                case R.id.home_text:
                    if (mActivity.getPosition() != Constants.POSITION_HOME) {
                        mActivity.mLogic.gotoState(Constants.POSITION_HOME);
                    }
                    break;
                case R.id.collect_text:
                    if (mActivity.getPosition() != Constants.POSITION_COLLECT) {
                        mActivity.mLogic.gotoState(Constants.POSITION_COLLECT);
                    }
                    break;
                case R.id.set_tool_text:
                    if (mActivity.getPosition() != Constants.POSITION_SETTOOL) {
                        mActivity.mLogic.gotoState(Constants.POSITION_SETTOOL);
                    }
                    break;

                default:
                    break;
            }
        }
    }

    /**
     * TitleItemFocusListener
     * @author shirley
     *
     */
    private class TitleItemFocusListener implements OnFocusChangeListener {

        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
                switch (v.getId()) {
                    case R.id.home_text:
                        mActivity.mLogic.gotoState(Constants.POSITION_HOME);
                        break;
                    case R.id.collect_text:
                        mActivity.mLogic.gotoState(Constants.POSITION_COLLECT);
                        break;
                    case R.id.set_tool_text:
                        mActivity.mLogic.gotoState(Constants.POSITION_SETTOOL);
                        break;

                    default:
                        break;
                }
            }
        }

    }

    /**
     * goto homefragment.
     */
    public void gotoHomeFragment() {
        mFragmentTransaction = mFragmentManager.beginTransaction();
        // add animation. if enter, from left to right; if out, from right to left.
        mFragmentTransaction.setCustomAnimations(R.animator.fragment_slide_right_enter,
                R.animator.fragment_slide_left_exit);

        if (mCurrentFragment != mHomeFragment) {
            if (mHomeFragment.isAdded()) {
                mFragmentTransaction.hide(mCurrentFragment).show(mHomeFragment);
            } else {
                if (mCurrentFragment == null) {
                    mFragmentTransaction.add(R.id.fl_item, mHomeFragment);
                } else {
                    mFragmentTransaction.hide(mCurrentFragment).add(R.id.fl_item, mHomeFragment);
                }
            }
        }
        mCurrentFragment = mHomeFragment;

        try {
            mFragmentTransaction.commit();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    /**
     * goto collect fragment.
     */
    public void gotoCollectFragment() {
        mFragmentTransaction = mFragmentManager.beginTransaction();
        // add animation. from homepage to collect page, from right to left.
        // from setpage to collect page, from left to right.
        if (mActivity.getPosition() > mActivity.getLastPosition()) {
            mFragmentTransaction.setCustomAnimations(R.animator.fragment_slide_left_enter,
                    R.animator.fragment_slide_right_exit);
        } else {
            mFragmentTransaction.setCustomAnimations(R.animator.fragment_slide_right_enter,
                    R.animator.fragment_slide_left_exit);
        }

        if (mCurrentFragment != mCollectFragment) {
            if (mCollectFragment.isAdded()) {
                mFragmentTransaction.hide(mCurrentFragment).show(mCollectFragment);
                mCollectFragment.refreshCollectUI();
            } else {
                if (mCurrentFragment == null) {
                    mFragmentTransaction.add(R.id.fl_item, mCollectFragment);
                } else {
                    mFragmentTransaction.hide(mCurrentFragment).add(R.id.fl_item, mCollectFragment);
                }
            }
        }
        mCurrentFragment = mCollectFragment;
        try {
            mFragmentTransaction.commit();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    /**
     * goto settool fragment.
     */
    public void gotoSetToolFragment() {
        mFragmentTransaction = mFragmentManager.beginTransaction();
        // add animation. if enter, from right to left; if out, from left to right
        mFragmentTransaction.setCustomAnimations(R.animator.fragment_slide_left_enter,
                R.animator.fragment_slide_right_exit);

        if (mCurrentFragment != mSetToolFragment) {
            if (mSetToolFragment.isAdded()) {
                mFragmentTransaction.hide(mCurrentFragment).show(mSetToolFragment);
            } else {
                if (mCurrentFragment == null) {
                    mFragmentTransaction.add(R.id.fl_item, mSetToolFragment);
                } else {
                    mFragmentTransaction.hide(mCurrentFragment).add(R.id.fl_item, mSetToolFragment);
                }
            }
        }
        mCurrentFragment = mSetToolFragment;
        try {
            mFragmentTransaction.commit();
        } catch (IllegalStateException e) {
            e.printStackTrace();
        }
    }

    /**
     * show menu dialog.
     */
    public void showNineDialog() {
        if (mNineDialog == null) {
            mNineDialog = new MenuDialog(mActivity);
        }
        mNineDialog.showDialog();
    }

    /**
     * dismiss menu dialog.
     */
    public void dismissNineDialog() {
        if (mNineDialog != null) {
            mNineDialog.dismiss();
        }
    }

    /**
     * menu dialog is show or not.
     * @return if true, menu dialog is show; if false, is dismiss.
     */
    public boolean isNineDialogShow() {
        if (mNineDialog != null && mNineDialog.isShowing()) {
            return true;
        }
        return false;
    }

    /**
     * get HomeFragment
     * @return
     */
    public HomeFragment getHomeFragment() {
        return mHomeFragment;
    }

    /**
     * CollectFragment
     * @return
     */
    public CollectFragment getCollectFragment() {
        return mCollectFragment;
    }

    /**
     * SetToolFragment
     * @return
     */
    public SetToolFragment getSetToolFragment() {
        return mSetToolFragment;
    }

    /**
     * Show settool subTitle("浏览记录", "更多设置")
     * @param showHistory  To distinguish is history or more settings.if true then show history subTitle; if false then show more settings subTitle.
     */
    public void showSetToolView(boolean showHistory) {
        mHomeMainTitleLl.setVisibility(View.GONE);
        mAdvanceSettingTv.setVisibility(View.VISIBLE);
        if (showHistory) {
            mAdvanceSettingTv.setText(R.string.history_text);
        } else {
            mAdvanceSettingTv.setText(R.string.more_setting);
        }
    }

    /**
     * Show main title view("浏览网页", "我的收藏", "设置工具")
     */
    public void showMainTileView() {
        mHomeMainTitleLl.setVisibility(View.VISIBLE);
        mAdvanceSettingTv.setVisibility(View.GONE);
    }
}
