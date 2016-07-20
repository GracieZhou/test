
package com.heran.launcher2.news;

import java.io.InputStream;
import java.util.ArrayList;

import org.json.JSONException;

import com.heran.launcher2.HomeApplication;
import com.heran.launcher2.news.NewsMainFragment.GetNewsArticle;
import com.heran.launcher2.news.NewsMainFragment.GetNewsCategory;
import com.heran.launcher2.util.Constants;
import com.heran.launcher2.util.HttpUtils;
import com.heran.launcher2.util.SaxService;

import android.content.Context;
import android.os.Handler;
import android.util.Log;

public class NewsAction {
    private final static String TAG = "NewsAction";

    private Context mContext;

    private Handler mHandler;

    public GetNewsCategory getNewsCategory = null;

    public GetNewsArticle getNewsArticle = null;

    public boolean isRuning;

    private ArrayList<NewsCategory> newsInfoList = new ArrayList<NewsCategory>();

    public NewsAction(Context mContext) {
        this.mContext = mContext;
    }

    public NewsAction(Context mContext, Handler mHandler) {
        this.mContext = mContext;
        this.mHandler = mHandler;
    }

    public ArrayList<NewsCategory> getNewsInfoList() {
        return newsInfoList;
    }

    /**
     * parse text json message
     * 
     * @param pgms
     * @return
     * @throws JSONException
     */
    public void parsePgmJson() {
        HomeApplication.getInstance().addNewsTask(mRunnable);
    }

    private Runnable mRunnable = new Runnable() {

        @Override
        public void run() {
            // 設置XML文檔的位置
            try {
                // 讀取服務器上的XML,獲取XML Stream
                InputStream inputStream = HttpUtils.getXML(Constants.NEWS_URL, "news");
                newsInfoList = SaxService.readNewsCategoryXML(inputStream);
                // 讀取服務器上的XML,獲取XML Stream
                Log.d(TAG, "newsInfoList.size() : " + newsInfoList.size());
                for (int i = 0; i < newsInfoList.size(); i++) {
                    Log.d(TAG, "newsInfoList.size[" + i + "]");
                    Log.d(TAG, "i = " + i + "CategoryID = " + newsInfoList.get(i).categoryID + "," + "CategoryName = "
                            + newsInfoList.get(i).name);
                    Log.d(TAG, "path = " + Constants.NEWS_ARTICLE_URL + newsInfoList.get(i).categoryID);
                    InputStream is = HttpUtils.getXML(Constants.NEWS_ARTICLE_URL + newsInfoList.get(i).categoryID,
                            "news");
                    if (is == null) {
                        Log.d(TAG, "inputStream == null");
                    } else {
                        newsInfoList.get(i).articleList = SaxService.readNewsArticleXML(is);
                        // for (int j = 0; j <
                        // newsInfoList.get(i).articleList.size(); j++) {
                        // Log.d(TAG,
                        // "j = " + j + "ContentUrl = " +
                        // newsInfoList.get(i).articleList.get(j).contentUrl);
                        // Log.d(TAG, "j = " + j + "Content = " +
                        // newsInfoList.get(i).articleList.get(j).content);
                        // }
                    }

                }
                Log.d(TAG, "for loop end");
                mHandler.sendEmptyMessage(Constants.NEWSUPDATE);
            } catch (Exception e) {
                Log.d(TAG, "getNewsCategory e = " + e.getMessage().toString());
            }
        }

    };

}
