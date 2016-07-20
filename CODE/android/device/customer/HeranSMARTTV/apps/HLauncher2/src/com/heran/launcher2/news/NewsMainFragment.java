
package com.heran.launcher2.news;

import java.io.InputStream;
import java.util.ArrayList;

import com.heran.launcher2.HomeActivity;
import com.heran.launcher2.HomeApplication;
import com.heran.launcher2.MainViewHolder;
import com.heran.launcher2.PublicFragment;
import com.heran.launcher2.R;
import com.heran.launcher2.eosweb.MyWebViewActivity;
import com.heran.launcher2.util.Constants;
import com.heran.launcher2.util.FocusView;
import com.heran.launcher2.util.HistoryRec;
import com.heran.launcher2.util.HttpUtil;
import com.heran.launcher2.util.SaxService;
import com.heran.launcher2.util.Utils;
import com.heran.launcher2.widget.ReadView;
import com.heran.launcher2.widget.ViewBean;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

public class NewsMainFragment extends PublicFragment {

    private static final String TAG = "NewsMainFragment";

    private final ViewBean mViewBean;

    private final MainViewHolder mHolder;

    private final HomeActivity mContext;

    public static final int UPDATE_NEWS_RIGTHT = 0;

    public static final int UPDATE_NEWS_LEFT = 1;

    // ------FG首頁功能選單-------

    public ImageButton homeIcon;

    public ImageButton liveIcon;

    public ImageButton appIcon;

    public ImageButton setIcon;

    public ImageButton memberIcon;

    // ------FG新聞清單-------
    public LinearLayout newsItem01;

    public LinearLayout newsItem02;

    public LinearLayout newsItem03;

    public LinearLayout newsItem04;

    public ImageView newsphoto01;

    public ImageView newsphoto02;

    public ImageView newsphoto03;

    public ImageView newsphoto04;

    public TextView newsText01;

    public TextView newsText02;

    public TextView newsText03;

    public TextView newsText04;

    int loop;

    public String URL;

    public String path;

    private TextView news_title;

    private ImageView news_img;

    public ImageButton PrePage, nextPage;

    TextView TotalPage, ChoicePage;

    FocusView mFocusView;

    private ReadView readview, readview2, readview3, readview4, readview5, readview6;

    int position, ComputePage;

    int PagePos1 = 0;

    int PagePos2, PagePos3, PagePos4, PagePos5;

    String news_contents;

    int news_contents_lenght;

    int NumPage = 1; // 目前頁數

    int NumTotal = 0; // 總頁數

    public ArrayList<NewsCategory> list = null;

    private GetNewsCategory getNewsCategory = null;

    private GetNewsArticle getNewsArticle = null;

    public final Handler mHandler;

    // //--------more btn--------
    private final MyBtnNewsMainIconFocusListener mBtnNewsMainIconFocusListener;

    private final MyBtnNewsMainIconClickListener mBtnNewsMainIconClickListener;

    private final MyNewsItemFocusListener mNewsItemFocusListener;

    private final MyNewsItemClickListener mNewsItemClickListener;

    private final MyOnKeyListener mMyOnKeyListener;

    private String recData = "";

    private boolean btn1_isOk = false;

    private boolean btn2_isOk = false;

    private boolean btn3_isOk = false;

    private boolean btn4_isOk = false;

    private int newsIndex01 = 0, newsIndex02 = 1, newsIndex03 = 2, newsIndex04 = 3;

    public NewsMainFragment(HomeActivity context) {
        super();
        this.mContext = context;
        mBtnNewsMainIconFocusListener = new MyBtnNewsMainIconFocusListener();
        mBtnNewsMainIconClickListener = new MyBtnNewsMainIconClickListener();
        mNewsItemFocusListener = new MyNewsItemFocusListener();
        mNewsItemClickListener = new MyNewsItemClickListener();
        mMyOnKeyListener = new MyOnKeyListener();
        mViewBean = new ViewBean(null, null);
        mHandler = new MyHandler();
        mHolder = mContext.mviewHolder;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        Log.d(TAG, "onCreateView");
        View mView = inflater.inflate(R.layout.news_main_content, container, false);
        try {
            initView(mView);
        } catch (Exception e) {
            Log.d(TAG, "init error : " + e.toString());
        }
        return mView;
    }

    private void initView(View mView) {

        newsItem01 = (LinearLayout) mView.findViewById(R.id.newsItem01);
        newsphoto01 = (ImageView) newsItem01.findViewById(R.id.news_list_photo);
        newsText01 = (TextView) newsItem01.findViewById(R.id.news_list_text);

        newsItem02 = (LinearLayout) mView.findViewById(R.id.newsItem02);
        newsphoto02 = (ImageView) newsItem02.findViewById(R.id.news_list_photo);
        newsText02 = (TextView) newsItem02.findViewById(R.id.news_list_text);

        newsItem03 = (LinearLayout) mView.findViewById(R.id.newsItem03);
        newsphoto03 = (ImageView) newsItem03.findViewById(R.id.news_list_photo);
        newsText03 = (TextView) newsItem03.findViewById(R.id.news_list_text);

        newsItem04 = (LinearLayout) mView.findViewById(R.id.newsItem04);
        newsphoto04 = (ImageView) newsItem04.findViewById(R.id.news_list_photo);
        newsText04 = (TextView) newsItem04.findViewById(R.id.news_list_text);

        news_title = (TextView) mView.findViewById(R.id.news_title);
        news_img = (ImageView) mView.findViewById(R.id.news_img);
        readview = (ReadView) mView.findViewById(R.id.readView1);
        readview2 = (ReadView) mView.findViewById(R.id.readView2);
        readview3 = (ReadView) mView.findViewById(R.id.readView3);
        readview4 = (ReadView) mView.findViewById(R.id.readView4);
        readview5 = (ReadView) mView.findViewById(R.id.readView5);
        readview6 = (ReadView) mView.findViewById(R.id.readView6);
        PrePage = (ImageButton) mView.findViewById(R.id.prepage);
        nextPage = (ImageButton) mView.findViewById(R.id.nextpage);
        TotalPage = (TextView) mView.findViewById(R.id.totalpage);
        ChoicePage = (TextView) mView.findViewById(R.id.choicepage);

        homeIcon = (ImageButton) mView.findViewById(R.id.index_homebtn_icon01);
        liveIcon = (ImageButton) mView.findViewById(R.id.index_homebtn_icon02);
        appIcon = (ImageButton) mView.findViewById(R.id.index_homebtn_icon03);
        setIcon = (ImageButton) mView.findViewById(R.id.index_homebtn_icon04);
        memberIcon = (ImageButton) mView.findViewById(R.id.index_homebtn_icon05);

        homeIcon.setOnFocusChangeListener(mBtnNewsMainIconFocusListener);
        liveIcon.setOnFocusChangeListener(mBtnNewsMainIconFocusListener);
        appIcon.setOnFocusChangeListener(mBtnNewsMainIconFocusListener);
        setIcon.setOnFocusChangeListener(mBtnNewsMainIconFocusListener);
        memberIcon.setOnFocusChangeListener(mBtnNewsMainIconFocusListener);

        newsItem01.setOnFocusChangeListener(mNewsItemFocusListener);
        newsItem02.setOnFocusChangeListener(mNewsItemFocusListener);
        newsItem03.setOnFocusChangeListener(mNewsItemFocusListener);
        newsItem04.setOnFocusChangeListener(mNewsItemFocusListener);
        nextPage.setOnFocusChangeListener(mBtnNewsMainIconFocusListener);
        PrePage.setOnFocusChangeListener(mBtnNewsMainIconFocusListener);

        homeIcon.setOnClickListener(mBtnNewsMainIconClickListener);
        liveIcon.setOnClickListener(mBtnNewsMainIconClickListener);
        appIcon.setOnClickListener(mBtnNewsMainIconClickListener);
        setIcon.setOnClickListener(mBtnNewsMainIconClickListener);
        memberIcon.setOnClickListener(mBtnNewsMainIconClickListener);
        newsItem01.setOnClickListener(mNewsItemClickListener);
        newsItem02.setOnClickListener(mNewsItemClickListener);
        newsItem03.setOnClickListener(mNewsItemClickListener);
        newsItem04.setOnClickListener(mNewsItemClickListener);
        nextPage.setOnClickListener(mNewsItemClickListener);
        PrePage.setOnClickListener(mNewsItemClickListener);

        homeIcon.setOnKeyListener(mMyOnKeyListener);
        newsItem01.setOnKeyListener(mMyOnKeyListener);
        newsItem02.setOnKeyListener(mMyOnKeyListener);
        newsItem03.setOnKeyListener(mMyOnKeyListener);
        newsItem04.setOnKeyListener(mMyOnKeyListener);
        nextPage.setOnKeyListener(mMyOnKeyListener);
        PrePage.setOnKeyListener(mMyOnKeyListener);

        mFocusView = (FocusView) getActivity().findViewById(R.id.fragment_selector);
        mViewBean.setmFocusObject(mFocusView);

        addViewGlobalLayoutListener(homeIcon, mViewBean);
        addViewGlobalLayoutListener(liveIcon, mViewBean);
        addViewGlobalLayoutListener(appIcon, mViewBean);
        addViewGlobalLayoutListener(setIcon, mViewBean);
        addViewGlobalLayoutListener(memberIcon, mViewBean);
        addViewGlobalLayoutListener(newsItem01, mViewBean);
        addViewGlobalLayoutListener(newsItem02, mViewBean);
        addViewGlobalLayoutListener(newsItem03, mViewBean);
        addViewGlobalLayoutListener(newsItem04, mViewBean);
        addViewGlobalLayoutListener(nextPage, mViewBean);
        addViewGlobalLayoutListener(PrePage, mViewBean);

        Log.d(TAG, "getcurrent view:" + mViewBean.getmCurFocusView());

        if (Constants.newsRequesetCodeIsOk) {

            mHandler.sendEmptyMessage(2);
            nextPage.requestFocus();

        } else {
            mHolder.fragmentBtn.requestFocus();
            mHolder.drawFocus(mHolder.fragmentBtn);
        }

    }

    @Override
    public void onStop() {
        Log.d(TAG, "onStop");
        mViewBean.setmCurFocusView(null);
        super.onStop();
    }

    @Override
    public void onDestroy() {
        Log.d(TAG, "onDestroy");
        super.onDestroy();
    }

    public void setList(ArrayList<NewsCategory> newList) {
        this.list = newList;
    }

    @SuppressLint("HandlerLeak")
    private class MyHandler extends Handler {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case UPDATE_NEWS_RIGTHT:
                    loop = mContext.GetNewsLoop();
                    updateNewsInfo(loop, newsIndex01, newsIndex02, newsIndex03, newsIndex04);
                    Log.d("NewsMainFragment", "update news");
                    break;
                case UPDATE_NEWS_LEFT:
                    show(mContext.GetNewsNum());
                    break;
            }
        }
    }

    private void ClearNewsPos() {
        NumPage = 1;
        NumTotal = 0;
        ChoicePage.setText(String.valueOf(NumPage));
        position = 0;
        ComputePage = 0;
    }

    private void nextPage() {

        if (position != news_contents_lenght) {

            if (NumPage == 1) {
                PagePos2 = position;
            } else if (NumPage == 2) {
                PagePos3 = position;
            } else if (NumPage == 3) {
                PagePos4 = position;
            } else if (NumPage == 4) {
                PagePos5 = position;
            }

            NumPage++;
            readview.setText(news_contents.substring(position));
            position += readview.getCharNum();
            readview2.setText(news_contents.substring(position));
            position += readview2.getCharNum();
            readview3.setText(news_contents.substring(position));
            position += readview3.getCharNum();
            ChoicePage.setText(String.valueOf(NumPage));
        } else {
        }
    }

    private void PrePage() {
        if (NumPage == 2) {
            position = PagePos1;
        } else if (NumPage == 3) {
            position = PagePos2;
        } else if (NumPage == 4) {
            position = PagePos3;
        } else if (NumPage == 5) {
            position = PagePos4;
        }
        if (NumPage != 1) {
            NumPage--;

            readview.setText(news_contents.substring(position));
            position += readview.getCharNum();
            readview2.setText(news_contents.substring(position));
            position += readview2.getCharNum();
            readview3.setText(news_contents.substring(position));
            position += readview3.getCharNum();
            ChoicePage.setText(String.valueOf(NumPage));
        }

    }

    private void TransformText() {
        news_contents = news_contents.replace('1', '１');
        news_contents = news_contents.replace('2', '２');
        news_contents = news_contents.replace('3', '３');
        news_contents = news_contents.replace('4', '４');
        news_contents = news_contents.replace('5', '５');
        news_contents = news_contents.replace('6', '６');
        news_contents = news_contents.replace('7', '７');
        news_contents = news_contents.replace('8', '８');
        news_contents = news_contents.replace('9', '９');
        news_contents = news_contents.replace('0', '０');

    }

    private void show(int i) {
        if (checkData()) {
            return;
        }
        ClearNewsPos();
        news_contents_lenght = list.get(loop).articleList.get(i).content.length();
        news_title.setText(list.get(loop).articleList.get(i).title);
        HomeApplication.getInstance().glideLoadGif(mContext, list.get(loop).articleList.get(i).imagePath, news_img);
        news_contents = list.get(loop).articleList.get(i).content;
        Log.d(TAG, "news_contents:" + news_contents);
        TransformText();

        readview.setText(news_contents.substring(position));
        position += readview.getCharNum();
        readview2.setText(news_contents.substring(position));
        position += readview2.getCharNum();
        readview3.setText(news_contents.substring(position));
        position += readview3.getCharNum();

        ComputePage();

    }

    private void ComputePage() {
        int LastPage = news_contents_lenght;
        Log.i("zzz", "pos : " + LastPage + "..." + ComputePage + "..." + NumTotal);
        int pp = 0;

        while (LastPage > 0) {
            pp = 0;
            readview4.setText(news_contents.substring(ComputePage));
            ComputePage += readview4.getCharNum();
            pp += readview4.getCharNum();
            readview5.setText(news_contents.substring(ComputePage));
            ComputePage += readview5.getCharNum();
            pp += readview5.getCharNum();
            readview6.setText(news_contents.substring(ComputePage));
            ComputePage += readview6.getCharNum();
            pp += readview6.getCharNum();

            LastPage = LastPage - pp;
            NumTotal++;
            Log.i("zzz", "pos : " + LastPage + "..." + ComputePage);
        }

        TotalPage.setText(String.valueOf(NumTotal));
    }

    @Override
    public void onResume() {
        Log.d(TAG, "onResume");
        newsItem01.requestFocus();
        super.onResume();
        if (Utils.isNetworkState) {
            if (Constants.newsRequesetCodeIsOk) {
                Log.d(TAG, "net_error.setVisibility(View.GONE);");
                Log.d(TAG, "network ok");
                if (list == null) {
                    Log.d(TAG, "list == null");
                    newsText01.setText(R.string.read_data4);
                    newsText02.setText(R.string.read_data4);
                    newsText03.setText(R.string.read_data4);
                    newsText04.setText(R.string.read_data4);
                    try {
                        if (getNewsCategory == null) {
                            // 取得新聞的目錄
                            getNewsCategory = new GetNewsCategory(
                                    "http://ws.chinatimes.com/WS/CtitvWebService.asmx/GetNCCategoryList");

                        }
                        getNewsCategory.start();
                    } catch (Exception e) {
                        Log.d("NewsMainFragment", "news loading error" + e.toString());
                    }
                } else {
                    list = mContext.mainLogic.newsInfoList;
                    Log.d(TAG, "list != null");
                    mHandler.removeMessages(UPDATE_NEWS_RIGTHT);
                    mHandler.sendEmptyMessage(UPDATE_NEWS_RIGTHT);
                    mHandler.removeMessages(UPDATE_NEWS_LEFT);
                    mHandler.sendEmptyMessage(UPDATE_NEWS_LEFT);
                }
            } else {
                Log.d(TAG, "no_net.setVisibility(View.VISIBLE)");
            }
        } else {
            Log.d(TAG, "network not ready");

        }
    }

    private void updateNewsInfo(int loop, int num1, int num2, int num3, int num4) {
        if (list == null || list.size() < 1) {
            return;
        }
        if (num1 <= list.get(loop).articleList.size() - 1) {
            btn1_isOk = true;
            HomeApplication.getInstance().glideLoadGif(mContext, list.get(loop).articleList.get(num1).imagePath,
                    newsphoto01);
            Log.d(TAG, "title:" + list.get(loop).articleList.get(num1).title);
            newsText01.setText(list.get(loop).articleList.get(num1).title);
        } else {
            btn1_isOk = false;
            newsphoto01.setImageDrawable(null);
            newsText01.setText(" ");
            Log.d(TAG, "title:" + "");
        }

        if (num2 <= list.get(loop).articleList.size() - 1) {
            btn2_isOk = true;
            HomeApplication.getInstance().glideLoadGif(mContext, list.get(loop).articleList.get(num2).imagePath,
                    newsphoto02);
            newsText02.setText(list.get(loop).articleList.get(num2).title);
        } else {
            btn2_isOk = false;
            newsphoto02.setImageDrawable(null);
            newsText02.setText(" ");
        }

        if (num3 <= list.get(loop).articleList.size() - 1) {
            btn3_isOk = true;
            HomeApplication.getInstance().glideLoadGif(mContext, list.get(loop).articleList.get(num3).imagePath,
                    newsphoto03);
            newsText03.setText(list.get(loop).articleList.get(num3).title);
        } else {
            btn3_isOk = false;
            newsphoto03.setImageDrawable(null);
            newsText03.setText(" ");
        }

        if (num4 <= list.get(loop).articleList.size() - 1) {
            btn4_isOk = true;
            HomeApplication.getInstance().glideLoadGif(mContext, list.get(loop).articleList.get(num4).imagePath,
                    newsphoto04);
            newsText04.setText(list.get(loop).articleList.get(num4).title);
        } else {
            btn4_isOk = false;
            newsphoto04.setImageDrawable(null);
            newsText04.setText(" ");
        }

    }

    class MyBtnNewsMainIconFocusListener implements OnFocusChangeListener {

        @Override
        public void onFocusChange(View v, boolean focus) {
            if (focus) {
                drawFocus(v);
                switch (v.getId()) {
                    case R.id.index_homebtn_icon01:
                        mHolder.thum_dsc.setText(mContext.mainLogic.mMenuTips[0]);
                        mHolder.thumb_img.setImageDrawable(mContext.getResources().getDrawable(R.drawable.thumb_home));
                        mHolder.showThumb();
                        break;
                    case R.id.index_homebtn_icon02:
                        mHolder.thum_dsc.setText(mContext.mainLogic.mMenuTips[1]);
                        mHolder.thumb_img.setImageDrawable(mContext.getResources().getDrawable(R.drawable.thumb_life));
                        mHolder.showThumb();
                        break;
                    case R.id.index_homebtn_icon03:
                        mHolder.thum_dsc.setText(mContext.mainLogic.mMenuTips[2]);
                        mHolder.thumb_img.setImageDrawable(mContext.getResources().getDrawable(R.drawable.thumb_apps));
                        mHolder.showThumb();
                        break;
                    case R.id.index_homebtn_icon04:
                        mHolder.thum_dsc.setText(mContext.mainLogic.mMenuTips[3]);
                        mHolder.thumb_img
                                .setImageDrawable(mContext.getResources().getDrawable(R.drawable.thumb_setting));
                        mHolder.showThumb();
                        break;
                    case R.id.index_homebtn_icon05:
                        mHolder.thum_dsc.setText(mContext.mainLogic.mMenuTips[4]);
                        mHolder.thumb_img
                                .setImageDrawable(mContext.getResources().getDrawable(R.drawable.thumb_shopping));
                        mHolder.showThumb();
                        break;
                    case R.id.prepage:
                        PrePage.setImageResource(R.drawable.msg_prepage_s);
                        break;
                    case R.id.nextpage:
                        nextPage.setImageResource(R.drawable.msg_nextpage_s);
                        break;
                }
            } else {
                mHolder.dissMissThumb();
                PrePage.setImageResource(R.drawable.msg_prepage_ns);
                nextPage.setImageResource(R.drawable.msg_nextpage_ns);
            }
        }

    }

    class MyBtnNewsMainIconClickListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            switch (v.getId()) {
                case R.id.index_homebtn_icon01:
                    recData = HistoryRec.block[7] + ',' + HistoryRec.block8Action[1] + ','
                            + mContext.getResources().getString(R.string.home_page) + ',' + "" + ',' + "" + ','
                            + HistoryRec.getCurrentDateTime();
                    HistoryRec.writeToFile(recData);
                    recData = "";
                    mContext.showViews(Constants.HOMEVIEW);
                    break;
                case R.id.index_homebtn_icon02:
                    recData = HistoryRec.block[7] + ',' + HistoryRec.block8Action[1] + ','
                            + mContext.getResources().getString(R.string.media) + ',' + "" + ',' + "" + ','
                            + HistoryRec.getCurrentDateTime();
                    HistoryRec.writeToFile(recData);
                    recData = "";
                    mHolder.updateFragment(Constants.LIVINGAREAVIEW);
                    break;
                case R.id.index_homebtn_icon03:
                    recData = HistoryRec.block[7] + ',' + HistoryRec.block8Action[1] + ','
                            + mContext.getResources().getString(R.string.app) + ',' + "" + ',' + "" + ','
                            + HistoryRec.getCurrentDateTime();
                    HistoryRec.writeToFile(recData);
                    recData = "";
                    mHolder.updateFragment(Constants.APPVIEW);
                    break;
                case R.id.index_homebtn_icon04:
                    recData = HistoryRec.block[7] + ',' + HistoryRec.block8Action[1] + ','
                            + mContext.getResources().getString(R.string.setting) + ',' + "" + ',' + "" + ','
                            + HistoryRec.getCurrentDateTime();
                    HistoryRec.writeToFile(recData);
                    recData = "";
                    Intent intent = new Intent("android.intent.action.MAIN");
                    intent.setClassName("com.android.settings", "com.android.settings.SettingsActivity");
                    intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT);
                    startActivity(intent);
                    break;
                case R.id.index_homebtn_icon05:
                    recData = HistoryRec.block[7] + ',' + HistoryRec.block8Action[1] + ','
                            + mContext.getResources().getString(R.string.member) + ',' + "" + ',' + "" + ','
                            + HistoryRec.getCurrentDateTime();
                    HistoryRec.writeToFile(recData);
                    recData = "";
                    String clickUrl = "http://www.jowinwin.com/hertv2msd/index.php?r=member/member";
                    intent = new Intent(mContext, MyWebViewActivity.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("URL", clickUrl);
                    intent.putExtras(bundle);
                    startActivity(intent);

                    break;
            }

        }

    }

    class MyNewsItemFocusListener implements OnFocusChangeListener {

        @Override
        public void onFocusChange(View v, boolean focus) {
            drawFocus(v);
        }

    }

    class MyNewsItemClickListener implements OnClickListener {

        @Override
        public void onClick(View v) {
            Log.d(TAG, "MyNewsItemClickListener onClick");
            if (checkData()) {
                return;
            }
            switch (v.getId()) {
                case R.id.newsItem01:
                    if (Constants.newsRequesetCodeIsOk) {
                        if (btn1_isOk == true) {
                            // newsPath.setText(list.get(0).articleList.get(newsIndex01).title);
                            URL = list.get(loop).articleList.get(newsIndex01).contentUrl;

                            recData = HistoryRec.block[7] + ',' + HistoryRec.block8Action[0] + ',' + "" + ',' + "" + ','
                                    + URL + ',' + HistoryRec.getCurrentDateTime();
                            HistoryRec.writeToFile(recData);
                            recData = "";
                            show(newsIndex01);
                            // loadUrl();
                            nextPage.requestFocus();
                        } else {

                        }
                    }

                    break;
                case R.id.newsItem02:
                    if (Constants.newsRequesetCodeIsOk) {
                        if (btn2_isOk == true) {
                            // newsPath.setText(list.get(0).articleList.get(newsIndex02).title);
                            URL = list.get(loop).articleList.get(newsIndex02).contentUrl;

                            recData = HistoryRec.block[7] + ',' + HistoryRec.block8Action[0] + ',' + "" + ',' + "" + ','
                                    + URL + ',' + HistoryRec.getCurrentDateTime();
                            HistoryRec.writeToFile(recData);
                            recData = "";
                            show(newsIndex02);
                            // loadUrl();
                            nextPage.requestFocus();
                        } else {

                        }
                    }
                    break;
                case R.id.newsItem03:
                    if (Constants.newsRequesetCodeIsOk) {
                        if (btn3_isOk == true) {
                            // newsPath.setText(list.get(0).articleList.get(newsIndex03).title);
                            URL = list.get(loop).articleList.get(newsIndex03).contentUrl;

                            recData = HistoryRec.block[7] + ',' + HistoryRec.block8Action[0] + ',' + "" + ',' + "" + ','
                                    + URL + ',' + HistoryRec.getCurrentDateTime();
                            HistoryRec.writeToFile(recData);
                            recData = "";
                            show(newsIndex03);
                            nextPage.requestFocus();
                        } else {

                        }
                    }
                    break;
                case R.id.newsItem04:
                    if (Constants.newsRequesetCodeIsOk) {
                        if (btn4_isOk == true) {
                            // newsPath.setText(list.get(0).articleList.get(newsIndex04).title);
                            URL = list.get(loop).articleList.get(newsIndex04).contentUrl;

                            recData = HistoryRec.block[7] + ',' + HistoryRec.block8Action[0] + ',' + "" + ',' + "" + ','
                                    + URL + ',' + HistoryRec.getCurrentDateTime();
                            HistoryRec.writeToFile(recData);
                            recData = "";
                            show(newsIndex04);
                            // loadUrl();
                            nextPage.requestFocus();
                        } else {

                        }
                    }
                    break;
                case R.id.nextpage:
                    nextPage();

                    break;
                case R.id.prepage:
                    PrePage();

                    break;
            }

        }
    }

    class NewsItemClickListener implements OnItemClickListener {

        @Override
        public void onItemClick(AdapterView<?> arg0, View arg1, int arg2, long arg3) {

        }

    }

    class MyOnKeyListener implements OnKeyListener {

        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (event.getAction() == KeyEvent.ACTION_DOWN && !checkData()) {
                switch (v.getId()) {
                    case R.id.newsItem04: {
                        if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                            mHolder.fragmentBtn.requestFocus();
//                            mHolder.drawFocus(mHolder.fragmentBtn);
                        }

                        if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                            if (newsIndex01 != 0) {
                                Log.d(TAG, "04 left && !=0");
                                newsIndex01 = newsIndex01 - 4;
                                newsIndex02 = newsIndex02 - 4;
                                newsIndex03 = newsIndex03 - 4;
                                newsIndex04 = newsIndex04 - 4;
                                updateNewsInfo(loop, newsIndex01, newsIndex02, newsIndex03, newsIndex04);
                                return true;
                            } else {
                                nextPage.requestFocus();
                                // drawFocus(nextPage);
                                Log.d(TAG, "04 left && ==0");
                                return true;
                            }
                        }
                        if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                            Log.d(TAG, "list : " + (list.get(loop).articleList.size() - 1));
                            Log.d(TAG, "newsIndex04 : " + (newsIndex04 + 4));
                            if (newsIndex04 <= list.get(loop).articleList.size() - 1) {
                                Log.d(TAG, "04 rght && !=ist.size()");
                                newsIndex01 = newsIndex01 + 4;
                                newsIndex02 = newsIndex02 + 4;
                                newsIndex03 = newsIndex03 + 4;
                                newsIndex04 = newsIndex04 + 4;
                                updateNewsInfo(loop, newsIndex01, newsIndex02, newsIndex03, newsIndex04);
                                return true;
                            } else {
                                Log.d(TAG, "04 right && ==list.size()");
                                return true;
                            }
                        }
                    }
                        break;
                    case R.id.newsItem01:
                        if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                            homeIcon.requestFocus();
                        }

                        if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                            if (newsIndex01 != 0) {
                                Log.d(TAG, "01 left && !=0");
                                newsIndex01 = newsIndex01 - 4;
                                newsIndex02 = newsIndex02 - 4;
                                newsIndex03 = newsIndex03 - 4;
                                newsIndex04 = newsIndex04 - 4;
                                updateNewsInfo(loop, newsIndex01, newsIndex02, newsIndex03, newsIndex04);
                                return true;
                            } else {
                                nextPage.requestFocus();
                                // drawFocus(nextPage);
                                Log.d(TAG, "01 left && ==0");
                                return true;
                            }
                        }
                        if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                            Log.d(TAG, "list : " + (list.get(loop).articleList.size() - 1));
                            Log.d(TAG, "newsIndex04 : " + (newsIndex04 + 4));
                            if (newsIndex04 <= list.get(loop).articleList.size() - 1) {
                                Log.d(TAG, "01 rght && !=ist.size()");
                                newsIndex01 = newsIndex01 + 4;
                                newsIndex02 = newsIndex02 + 4;
                                newsIndex03 = newsIndex03 + 4;
                                newsIndex04 = newsIndex04 + 4;
                                updateNewsInfo(loop, newsIndex01, newsIndex02, newsIndex03, newsIndex04);
                                return true;
                            } else {
                                // nextPage.requestFocus();
                                // drawFocus(nextPage);
                                Log.d(TAG, "01 right && ==list.size()");
                                return true;
                            }
                        }
                        break;
                    case R.id.newsItem02:

                        if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                            if (newsIndex01 != 0) {
                                Log.d(TAG, "02 left && !=0");
                                newsIndex01 = newsIndex01 - 4;
                                newsIndex02 = newsIndex02 - 4;
                                newsIndex03 = newsIndex03 - 4;
                                newsIndex04 = newsIndex04 - 4;
                                updateNewsInfo(loop, newsIndex01, newsIndex02, newsIndex03, newsIndex04);
                                return true;
                            } else {
                                nextPage.requestFocus();
                                Log.d(TAG, "02 left && ==0");
                                return true;
                            }
                        }
                        if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                            Log.d(TAG, "list : " + (list.get(loop).articleList.size() - 1));
                            Log.d(TAG, "newsIndex04 : " + (newsIndex04 + 4));
                            if (newsIndex04 <= list.get(loop).articleList.size() - 1) {
                                Log.d(TAG, "02 rght && !=ist.size()");
                                newsIndex01 = newsIndex01 + 4;
                                newsIndex02 = newsIndex02 + 4;
                                newsIndex03 = newsIndex03 + 4;
                                newsIndex04 = newsIndex04 + 4;
                                updateNewsInfo(loop, newsIndex01, newsIndex02, newsIndex03, newsIndex04);
                                return true;
                            } else {
                                Log.d(TAG, "02 right && ==list.size()");
                                return true;
                            }
                        }

                        break;
                    case R.id.newsItem03:

                        if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                            if (newsIndex01 != 0) {
                                Log.d(TAG, "03 left && !=0");
                                newsIndex01 = newsIndex01 - 4;
                                newsIndex02 = newsIndex02 - 4;
                                newsIndex03 = newsIndex03 - 4;
                                newsIndex04 = newsIndex04 - 4;
                                updateNewsInfo(loop, newsIndex01, newsIndex02, newsIndex03, newsIndex04);
                                return true;
                            } else {
                                nextPage.requestFocus();
                                // drawFocus(nextPage);
                                Log.d(TAG, "03 left && ==0");
                                return true;
                            }
                        }
                        if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                            Log.d(TAG, "list : " + (list.get(loop).articleList.size() - 1));
                            Log.d(TAG, "newsIndex04 : " + (newsIndex04 + 4));
                            if (newsIndex04 <= list.get(loop).articleList.size() - 1) {
                                Log.d(TAG, "03 rght && !=ist.size()");
                                newsIndex01 = newsIndex01 + 4;
                                newsIndex02 = newsIndex02 + 4;
                                newsIndex03 = newsIndex03 + 4;
                                newsIndex04 = newsIndex04 + 4;
                                updateNewsInfo(loop, newsIndex01, newsIndex02, newsIndex03, newsIndex04);
                                return true;
                            } else {
                                Log.d(TAG, "03 right && ==list.size()");
                                return true;
                            }
                        }
                        break;
                    case R.id.nextpage:
                        if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {

                            return true;
                        }
                        if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
//                            homeIcon.requestFocus();
                            // drawFocus(newsItem01);
                        	newsItem01.requestFocus();
                            return true;
                        }
                        if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                            PrePage.requestFocus();
                            // drawFocus(PrePage);
                            return true;
                        }
                        if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                            mHolder.fragmentBtn.requestFocus();
                            return true;
                        }
                        if (keyCode == KeyEvent.KEYCODE_BACK) {
                            newsItem01.requestFocus();
                            return true;
                        }
                        break;
                    case R.id.prepage:
                        if (keyCode == KeyEvent.KEYCODE_DPAD_UP) {
                            return true;
                        }
                        if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                            nextPage.requestFocus();

                            return true;
                        }
                        if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                            return true;
                        }
                        if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                            mHolder.fragmentBtn.requestFocus();
                            return true;
                        }
                        if (keyCode == KeyEvent.KEYCODE_BACK) {
                            newsItem01.requestFocus();
                            return true;
                        }
                        break;
                    case R.id.index_homebtn_icon01:
                        if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                            liveIcon.requestFocus();
                            return true;
                        }
                        if (keyCode == KeyEvent.KEYCODE_DPAD_LEFT) {
                            nextPage.requestFocus();
                            return true;
                        }
                        if (keyCode == KeyEvent.KEYCODE_DPAD_DOWN) {
                            newsItem01.requestFocus();
                            return true;
                        }
                        break;

                }
            }
            return false;
        }

    }

    private boolean checkData() {
        if (list != null && list.size() > 0 && list.get(0) != null && list.get(0).articleList != null
                && list.get(0).articleList.size() > 0) {
            return false;
        }
        return true;
    }

    private void drawFocus(View view) {
        Log.d(TAG, "draw view :" + view);
        mViewBean.setmCurFocusView(view);
        mViewBean.getmFocusObject().startAnimation(view);
    }

    public class GetNewsCategory extends Thread {
        private final String path;

        public GetNewsCategory(String path) {
            this.path = path;
        }

        @Override
        public void run() {
            // 設置XML文檔的位置
            try {
                // 讀取服務器上的XML,獲取XML Stream
                InputStream inputStream = HttpUtil.getXML(path, "news");
                list = SaxService.readNewsCategoryXML(inputStream);
                for (int i = 0; i < list.size(); i++)
                    Log.d(TAG, "i = " + i + "CategoryID = " + list.get(i).categoryID + "," + "CategoryName = "
                            + list.get(i).name);
                if (getNewsArticle == null) {
                    getNewsArticle = new GetNewsArticle(
                            "http://ws.chinatimes.com/WS/CtitvWebService.asmx/GetNCNewsbyCtitv?CategoryID=");
                    getNewsArticle.start();
                }
            } catch (Exception e) {
                Log.d(TAG, "getNewsCategory e = " + e.getMessage().toString());
            }
        }
    };

    public class GetNewsArticle extends Thread {
        private final String path;

        public GetNewsArticle(String path) {
            this.path = path;
        }

        @Override
        public void run() {
            // 設置XML文檔的位置
            try {

                // 讀取服務器上的XML,獲取XML Stream
                for (int i = 0; i < list.size(); i++) {
                    Log.d(TAG, "path = " + path + list.get(i).categoryID);
                    InputStream inputStream = HttpUtil.getXML(path + list.get(i).categoryID, "news");
                    list.get(i).articleList = SaxService.readNewsArticleXML(inputStream);

                }
                mHandler.removeMessages(UPDATE_NEWS_RIGTHT);
                mHandler.sendEmptyMessage(UPDATE_NEWS_RIGTHT);
                mHandler.removeMessages(UPDATE_NEWS_LEFT);
                mHandler.sendEmptyMessage(UPDATE_NEWS_LEFT);

            } catch (Exception e) {
                Log.d(TAG, "GetNewsArticle e = " + e.getMessage().toString());
            }
        }
    };

}
