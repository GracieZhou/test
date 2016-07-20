
package com.heran.launcher2.news;

import java.util.ArrayList;

import com.heran.launcher2.HomeActivity;
import com.heran.launcher2.HomeApplication;
import com.heran.launcher2.PublicFragment;
import com.heran.launcher2.R;
import com.heran.launcher2.util.Constants;
import com.heran.launcher2.util.HistoryRec;
import com.heran.launcher2.util.UIUtil;
import com.heran.launcher2.util.Utils;

import android.annotation.SuppressLint;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.animation.AnimationUtils;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

/**
 * projectName： EosLauncher moduleName： NewsFragment.java
 * 
 * @author Laird.li
 * @version 1.0.0
 * @time 2016-03-17 下午14:33:00
 * @Copyright © 2016 Heran Inc.
 */

public class NewsFragment extends PublicFragment {

    private final static String TAG = "NewsFragment";

    private HomeActivity mContext;

    private Handler mHandler;

    private LinearLayout newsLayout;

    private TextView newsTitle, newsContent;

    private ImageView photo;

    private ArrayList<NewsCategory> newsList = null;

    private final static int NEWS_LOADING = 1;

    private final static int NEWS_CHOICE_INDEX = 2;

    private final static int NEWS_ANIMATION = 3;

    private final static int NEWS_SHOW = 4;

    private final static int NEWS_REQUST_FAILD = 5;

    private int newsIndex = 0; // 播放新聞的index

    private FrameLayout noDataFL;

    private int loop = 0; // 整筆內容重複播放的次數

    private String recData = "";

    private FrameLayout noNetFrameLayout;

    public NewsFragment() {
        super();
        Log.v(TAG, "public newsfragment()");
    }

    public NewsFragment(HomeActivity context) {
        super();
        this.mContext = context;
        mHandler = new MyHandler();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View mview = inflater.inflate(R.layout.news_main, container, false);
        initView(mview);
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
        Log.d(TAG, "onResume");
        if (!Utils.isNetworkState) {
            Log.d(TAG, "no net");
            noNetFrameLayout.setVisibility(View.VISIBLE);
            mHandler.sendEmptyMessage(NEWS_REQUST_FAILD);
        } else {
            Log.d(TAG, "net is ok");
            Log.d(TAG, "Constants.newsRequesetCodeIsOk ok");
            noNetFrameLayout.setVisibility(View.GONE);
            recData = HistoryRec.block[9] + ',' + HistoryRec.block10Action[1] + ',' + R.string.news + ',' + "" + ','
                    + "" + ',' + HistoryRec.getCurrentDateTime();
            HistoryRec.writeToFile(recData);
            recData = "";
            mHandler.sendEmptyMessage(NEWS_LOADING);
        }

    }

    /**
     * setNewsList From HomeActivity
     * 
     * @param newList
     */
    public void setNewsList(ArrayList<NewsCategory> newList) {
        this.newsList = newList;
        Log.d(TAG, "setNewsList");
        newsIndex = 0;
    }

    @SuppressLint("HandlerLeak")
    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {

                case NEWS_LOADING:
                    mHandler.removeMessages(NEWS_LOADING);
                    if (newsList != null) {
                        noDataFL.setVisibility(View.GONE);
                        mHandler.removeMessages(NEWS_CHOICE_INDEX);
                        mHandler.sendEmptyMessage(NEWS_CHOICE_INDEX);

                    } else {
                        noDataFL.setVisibility(View.VISIBLE);
                        mHandler.sendEmptyMessageDelayed(NEWS_LOADING, 2 * 1000);
                    }
                    break;

                case NEWS_CHOICE_INDEX:
                    if (loop == newsList.size()) {
                        loop = 0;
                    }
                    if (newsList.get(loop).articleList != null
                            && newsIndex == newsList.get(loop).articleList.size() - 1) {
                        newsIndex = 0;
                        loop++;
                        Log.d(TAG, "newsIndex = 0");
                        Log.d(TAG, "loop :" + loop);
                    }

                    mHandler.sendEmptyMessage(NEWS_ANIMATION);
                    mHandler.removeMessages(NEWS_CHOICE_INDEX);
                    mHandler.sendEmptyMessageDelayed(NEWS_CHOICE_INDEX, 30 * 1000);
                    break;
                case NEWS_ANIMATION:
                    if ((newsIndex == 0 && loop == 0)) {
                        mHandler.sendEmptyMessage(NEWS_SHOW);
                    } else {
                        newsLayout.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.push_up_in));
                        newsLayout.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.push_up_out));
                        mHandler.removeMessages(NEWS_SHOW);
                        mHandler.sendEmptyMessageDelayed(NEWS_SHOW, 1 * 1000);
                    }
                    newsIndex++;
                    mHandler.removeMessages(NEWS_ANIMATION);
                    break;
                case NEWS_SHOW:
                    HomeApplication.getInstance().glideLoadGif(mContext,
                            newsList.get(loop).articleList.get(newsIndex).imagePath, photo);
                    newsTitle.setText(newsList.get(loop).articleList.get(newsIndex).title);
                    newsContent.setText(newsList.get(loop).articleList.get(newsIndex).content);
                    break;
                case NEWS_REQUST_FAILD:
                    if (!Utils.isNetworkState) {
                        UIUtil.toastShow(R.string.shop_no_network, getActivity());
                    }
                    if (Utils.isNetworkState && !Constants.newsRequesetCodeIsOk) {
                        UIUtil.toastShow(R.string.server_error, getActivity());
                    }
                    break;
                default:
                    break;
            }
        }
    }

    /**
     * init views and add listeners for views
     * 
     * @param mview The view inflate from xml
     */
    private void initView(View mview) {
        Log.d(TAG, "initView");
        newsLayout = (LinearLayout) mview.findViewById(R.id.newslayout);
        photo = (ImageView) mview.findViewById(R.id.iv_photo);
        newsTitle = (TextView) mview.findViewById(R.id.tv_newsTitle);
        newsContent = (TextView) mview.findViewById(R.id.tv_newsContent);
        noNetFrameLayout = (FrameLayout) mview.findViewById(R.id.no_net);
        noDataFL = (FrameLayout) mview.findViewById(R.id.loading_data);

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        mHandler.removeCallbacksAndMessages(null);
    }

    public int GetNewsNum() {
        return newsIndex;
    }

    public int GetNewsLoop() {
        return loop;
    }

}
