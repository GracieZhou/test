
package com.heran.launcher2.news;

import java.util.ArrayList;

import com.androidquery.AQuery;
import com.heran.launcher2.HomeActivity;
import com.heran.launcher2.MainViewHolder;
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

    private boolean isStop = false;

    private TextView newsTitle, newsContent;

    private ImageView photo;

    private ArrayList<NewsCategory> newsList = null;

    private final static int NEWS_LOADING = 1;

    private final static int NEWS_CHOICE_INDEX = 2;

    private final static int NEWS_ANIMATION = 3;

    private final static int NEWS_SHOW = 4;

    private final static int NEWS_REQUST_FAILD = 5;

    private int count = 0; // 資料讀取中的計數

    private int newsIndex = 0; // 播放新聞的index

    private int loop = 0; // 整筆內容重複播放的次數

    private String recData = "";

    private AQuery aq;

    private FrameLayout noNetFrameLayout;

    public NewsFragment() {
        super();
        Log.v(TAG, "public newsfragment()");
    }

    public NewsFragment(HomeActivity context, MainViewHolder mHolder) {
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
        if (!Utils.isNet) {
            Log.d(TAG, "no net");
            noNetFrameLayout.setVisibility(View.VISIBLE);
            mHandler.sendEmptyMessage(NEWS_REQUST_FAILD);
        } else {
            Log.d(TAG, "net is ok");
            if (Constants.newsRequesetCodeIsOk) {
                Log.d(TAG, "Constants.newsRequesetCodeIsOk ok");
                noNetFrameLayout.setVisibility(View.GONE);
                recData = HistoryRec.block[9] + ',' + HistoryRec.block10Action[1] + ',' + R.string.news + ',' + "" + ','
                        + "" + ',' + HistoryRec.getCurrentDateTime();
                HistoryRec.writeToFile(recData);
                recData = "";
                mHandler.sendEmptyMessage(NEWS_LOADING);
            } else {
                Log.d(TAG, "Constants.newsRequesetCodeIsOk is not ok");
                noNetFrameLayout.setVisibility(View.VISIBLE);
                mHandler.sendEmptyMessage(NEWS_REQUST_FAILD);
            }
        }

    }

    @Override
    public void onPause() {
        // TODO Auto-generated method stub
        super.onPause();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        isStop = true;
        Log.v(TAG, "onDestroy() ");
    }

    /**
     * setNewsList From HomeActivity
     * 
     * @param newList
     */
    public void setNewsList(ArrayList<NewsCategory> newList) {
        try {
            this.newsList = newList;
            Log.d(TAG, "setNewsList");
            if (Constants.newsRequesetCodeIsOk) {
                Log.d(TAG, "newsRequesetCodeIsOk ok");
                newsIndex = 0;
                if (noNetFrameLayout != null) {
                    noNetFrameLayout.setVisibility(View.GONE);
                }
                mHandler.removeMessages(NEWS_LOADING);
                mHandler.sendEmptyMessage(NEWS_CHOICE_INDEX);
            } else {
                Log.d(TAG, "newsRequesetCodeIsOk is not ok");
                mHandler.sendEmptyMessage(NEWS_REQUST_FAILD);
            }

        } catch (Exception e) {
            Log.d(TAG, "news setList : " + e.toString());
        }
    }

    @SuppressLint("HandlerLeak")
    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {

                case NEWS_LOADING:
                    try {
                        if (newsList != null) {
                            newsList = mContext.mainLogic.newsInfoList;
                            mHandler.sendEmptyMessage(NEWS_CHOICE_INDEX);
                            mHandler.removeMessages(NEWS_LOADING);

                        } else {
                            if (count == 3) {
                                count = 0;
                            }
                            if (count == 0) {
                                newsContent.setText(R.string.read_data1);
                            } else if (count == 1) {
                                newsContent.setText(R.string.read_data2);
                            } else if (count == 2) {
                                newsContent.setText(R.string.read_data3);
                            }
                            count++;
                            mHandler.removeMessages(NEWS_LOADING);
                            mHandler.sendEmptyMessageDelayed(NEWS_LOADING, 1 * 1000);
                        }
                    } catch (Exception e) {
                        Log.d(TAG, "handler NEWS_LOADING error : " + e.toString());
                    }
                    break;

                case NEWS_CHOICE_INDEX:
                    try {
                        if (loop == newsList.size()) {
                            loop = 0;
                        }
                        if (newsIndex == newsList.get(loop).articleList.size() - 1) {
                            newsIndex = 0;
                            loop++;
                            Log.d(TAG, "newsIndex = 0");
                            Log.d(TAG, "loop :" + loop);
                        }

                        mHandler.sendEmptyMessage(NEWS_ANIMATION);
                        mHandler.removeMessages(NEWS_CHOICE_INDEX);
                        mHandler.sendEmptyMessageDelayed(NEWS_CHOICE_INDEX, 30 * 1000);
                    } catch (Exception e) {
                        Log.d(TAG, "handler 2 error : " + e.toString());
                    }
                    break;
                case NEWS_ANIMATION:
                    try {
                        if (isStop || (newsIndex == 0 && loop == 0)) {
                            mHandler.sendEmptyMessage(NEWS_SHOW);
                            newsIndexAdd();
                            isStop = false;
                        } else {
                            newsLayout.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.push_up_in));
                            newsLayout.setAnimation(AnimationUtils.loadAnimation(mContext, R.anim.push_up_out));
                            mHandler.removeMessages(NEWS_SHOW);
                            newsIndexAdd();
                            mHandler.sendEmptyMessageDelayed(NEWS_SHOW, 1 * 1000);
                        }

                        mHandler.removeMessages(3);
                    } catch (Exception e) {
                        Log.d(TAG, "handler 3 error : " + e.toString());
                    }
                    break;
                case NEWS_SHOW:
                    try {
                        Log.d(TAG, "index : " + newsIndex);
                        aq.id(photo).image(newsList.get(loop).articleList.get(newsIndex).imagePath, true, true, 100, 0,
                                null, 0, AQuery.RATIO_PRESERVE);
                        Log.d(TAG, newsList.get(loop).articleList.get(newsIndex).imagePath == null ? "photo: true"
                                : "photo: false");
                        newsTitle.setText(newsList.get(loop).articleList.get(newsIndex).title);
                        Log.d(TAG, newsList.get(loop).articleList.get(newsIndex).title == null ? "newsTitle: true"
                                : "newsTitle: false");
                        newsContent.setText(newsList.get(loop).articleList.get(newsIndex).content);
                        Log.d(TAG, newsList.get(loop).articleList.get(newsIndex).content == null ? "newsContent: true"
                                : "newsContent: false");
                    } catch (Exception e) {
                        Log.d(TAG, "handler NEWS_SHOW error : " + e.toString());
                    }
                    break;
                case NEWS_REQUST_FAILD:
                    try {
                        if (!Utils.isNet) {
                            UIUtil.toastShow(R.string.shop_no_network, getActivity());
                        }
                        if (Utils.isNet && !Constants.newsRequesetCodeIsOk) {
                            UIUtil.toastShow(R.string.server_error, getActivity());

                        }
                    } catch (Exception e) {
                        Log.d(TAG, "toastShow is error : " + e.toString());
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
        aq = new AQuery(getActivity());

    }

    private void newsIndexAdd() {
        if (isStop) {
        } else {
            newsIndex++;
        }
    }

    public int GetNewsNum() {
        return newsIndex;
    }

    public int GetNewsLoop() {
        return loop;
    }

}
