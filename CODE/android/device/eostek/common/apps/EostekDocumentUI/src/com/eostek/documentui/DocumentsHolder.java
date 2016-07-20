
package com.eostek.documentui;

import android.R.anim;
import android.content.Intent;
import android.os.Handler;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.widget.ImageView;
import android.widget.TextView;

import com.eostek.documentui.data.DataProxy;
import com.eostek.documentui.fragment.DownloadedFragment;
import com.eostek.documentui.fragment.DownloadingFragment;
import com.google.common.collect.ImmutableList;

/**
 * @ClassName: DocumentsHolder.
 * @Description:deal the things about ui;.
 * @author: lucky.li.
 * @date: Oct 15, 2015 6:14:23 PM.
 * @Copyright: Eostek Co., Ltd. Copyright , All rights reserved.
 */
public class DocumentsHolder implements OnPageChangeListener, OnFocusChangeListener, OnClickListener {
    private final String TAG = "DocumentsHolder";

    private DocumentsActivity mActivity;

    private ViewPager mViewPager;

    private TextView mDownloadedTextView;

    private View mDownloadedLine;

    private TextView mDownloadingTextView;

    private View mDownloadingLine;

    /**
     * the view to show user press it to enter into downloadSettings
     */
    private TextView mSettingsView;

    /**
     * the view to show user press menu key to enter into menuSettings
     */
    private TextView mMenuTipView;

    /**
     * show the number of the downloading task
     */
    private TextView mIndicatorView;

    private ImmutableList<Fragment> mFragmentsList;

    private DownloadedFragment mDownloadedFragment;

    private DownloadingFragment mDownloadingFragment;
    
    private ImageView mPreBlackImageView;

    /**
     * hold fragment datas
     */
    private MyFragmentPageAadpter mAdapter;

    public FragmentManager mFragmentManager;

    private int mCurrentFlag = Constants.DownloadedFragmentIndex;

    /**
     * downloadSetting action
     */
    private final String DOWNLOADSETTINGSACTION = "android.intent.action.DOWNLOADSETTINGS";

    private DataProxy mDataProxy;

    /**
     * @Title: DocumentsHolder.
     * @Description: constructor.
     * @param: @param activity.
     * @throws
     */
    public DocumentsHolder(DocumentsActivity activity, DataProxy proxy) {
        this.mActivity = activity;
        this.mDataProxy = proxy;
        initViewPager();
        findViews();
        mDownloadedTextViewRequestFocus();
    }

    public void findViews() {
        mFragmentManager = mActivity.getSupportFragmentManager();
        mDownloadedTextView = (TextView) mActivity.findViewById(R.id.downloaded_tv);
        mDownloadedTextView.setOnFocusChangeListener(this);
        mDownloadingTextView = (TextView) mActivity.findViewById(R.id.downloading_tv);
        mDownloadingTextView.setOnFocusChangeListener(this);
        mDownloadedLine = mActivity.findViewById(R.id.line1);
        mDownloadingLine = mActivity.findViewById(R.id.line2);
        mSettingsView = (TextView) mActivity.findViewById(R.id.settings);
        mSettingsView.setOnClickListener(this);
        mDownloadedTextView.setOnClickListener(this);
        mDownloadingTextView.setOnClickListener(this);
        mMenuTipView = (TextView) mActivity.findViewById(R.id.menu);
        mMenuTipView.setVisibility(View.GONE);
        mIndicatorView = (TextView) mActivity.findViewById(R.id.indicator);
        mPreBlackImageView = (ImageView) mActivity.findViewById(R.id.pre_black);
        mViewPager = (ViewPager) mActivity.findViewById(R.id.content_pager);
        mAdapter = new MyFragmentPageAadpter(mFragmentManager);
        mViewPager.setAdapter(mAdapter);
        mViewPager.setOnPageChangeListener(this);
    }

    private void initViewPager() {
        if (mDownloadedFragment == null) {
            mDownloadedFragment = new DownloadedFragment(mActivity);
        }
        if (mDownloadingFragment == null) {
            mDownloadingFragment = new DownloadingFragment(mActivity, mDataProxy);
        }
        mFragmentsList = ImmutableList.of(mDownloadedFragment, mDownloadingFragment);
    }

    /**
     * @Title: showSpecifiedPage.
     * @Description: show the Specified Page by index.
     * @param: @param index.
     * @return: void.
     * @throws
     */
    public void showSpecifiedPage(int index) {
        mCurrentFlag = index;
        if (mViewPager != null && index >= 0) {
            try {
                mViewPager.setCurrentItem(index);
            } catch (Exception e) {
                if (Constants.isDebug) {
                    Log.e(TAG, "catch Exception ", e);
                }
            }
        }
    }

    /**
     * @Title: reuqestFocus.
     * @Description: gain the focus.
     * @param: @param index.
     * @return: void.
     * @throws
     */
    public void reuqestFocus(int index) {
        if (index == Constants.DownloadedFragmentIndex) {
            mDownloadedTextView.requestFocus();
        } else if (index == Constants.DownloadingFragmentIndex) {
            mDownloadingTextView.requestFocus(); 
        }
    }

    private void mDownloadedTextViewRequestFocus(){
        new Handler().postDelayed(new Runnable() {  
            @Override  
            public void run() {  
                mDownloadedTextView.requestFocus(); 
                mPreBlackImageView.setVisibility(View.GONE);
                //mPreBlackImageView.setBackgroundColor(android.graphics.Color.TRANSPARENT);
            }  
          }, 200); 
    }
    /**
     * @Title: setIndicatorText.
     * @Description: show the number of the downloading task.
     * @param: @param text.
     * @return: void.
     * @throws
     */
    public void setIndicatorText(String text) {
        mIndicatorView.setText(text);
    }

    /**
     * @Title: setIndicatorText.
     * @Description: show or hide the view.
     * @param: @param text.
     * @return: void.
     * @throws
     */
    public void setIndicatorVisibility(int visibility) {
        mIndicatorView.setVisibility(visibility);
    }

    public void setMenuTipsVisibility(int visibility) {
        mMenuTipView.setVisibility(visibility);
    }

    /**
     * @ClassName: MyFragmentPageAadpter.
     * @Description:MyFragmentPageAadpter.
     * @author: lucky.li.
     * @date: Sep 16, 2015 10:45:04 AM.
     * @Copyright: Eostek Co., Ltd. Copyright , All rights reserved.
     */
    class MyFragmentPageAadpter extends FragmentPagerAdapter {

        public MyFragmentPageAadpter(FragmentManager fm) {
            super(fm);
        }

        @Override
        public Fragment getItem(int position) {
            return mFragmentsList.get(position % mFragmentsList.size());
        }

        @Override
        public int getCount() {
            return mFragmentsList.size();
        }
    }

    @Override
    public void onPageScrollStateChanged(int arg0) {

    }

    @Override
    public void onPageScrolled(int arg0, float arg1, int arg2) {

    }

    @Override
    public void onPageSelected(int arg0) {
        mCurrentFlag = arg0;
        switch (arg0) {
            case Constants.DownloadedFragmentIndex:
                if (!mDownloadedTextView.isFocused()) {
                    mDownloadedLine.setVisibility(View.VISIBLE);
                    mDownloadingLine.setVisibility(View.INVISIBLE);
                }
                mMenuTipView.setVisibility(View.GONE);
                break;
            case Constants.DownloadingFragmentIndex:
                if (!mDownloadingTextView.isFocused()) {
                    mDownloadingLine.setVisibility(View.VISIBLE);
                    mDownloadedLine.setVisibility(View.INVISIBLE);
                }
                break;
            default:
                break;
        }
    }

    @Override
    public void onFocusChange(View v, boolean hasFocus) {
        switch (v.getId()) {
            case R.id.downloaded_tv:
                if (hasFocus) {
                    showSpecifiedPage(Constants.DownloadedFragmentIndex);
                    mDownloadedLine.setVisibility(View.INVISIBLE);
                    mDownloadingLine.setVisibility(View.INVISIBLE);
                    mMenuTipView.setVisibility(View.GONE);
                } else {
                    if (mCurrentFlag == Constants.DownloadedFragmentIndex) {
                        mDownloadedLine.setVisibility(View.VISIBLE);
                    }
                }
                break;
            case R.id.downloading_tv:
                if (hasFocus) {
                    showSpecifiedPage(Constants.DownloadingFragmentIndex);
                    mDownloadedLine.setVisibility(View.INVISIBLE);
                    mDownloadingLine.setVisibility(View.INVISIBLE);
                    mMenuTipView.setVisibility(View.GONE);
                } else {
                    if (mCurrentFlag == Constants.DownloadingFragmentIndex) {
                        mDownloadingLine.setVisibility(View.VISIBLE);
                    }
                }
                break;
            default:
                break;
        }

    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.settings:
                Intent intent = new Intent(DOWNLOADSETTINGSACTION);
                mActivity.startActivity(intent);
                break;
            case R.id.downloaded_tv:
                v.requestFocus();
                break;
            case R.id.downloading_tv:
                v.requestFocus();
                break;
            default:
                break;
        }
    }

}
