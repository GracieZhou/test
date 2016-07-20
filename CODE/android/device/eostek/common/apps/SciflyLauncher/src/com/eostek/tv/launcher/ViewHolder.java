
package com.eostek.tv.launcher;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.Rect;
import android.os.Handler;
import android.provider.Settings;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.SurfaceView;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.View.OnFocusChangeListener;
import android.view.View.OnKeyListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.AdapterView.OnItemSelectedListener;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.eostek.tv.launcher.R;
import com.eostek.tv.launcher.business.database.DBManager;
import com.eostek.tv.launcher.model.JsonHeadBean;
import com.eostek.tv.launcher.model.MetroInfo;
import com.eostek.tv.launcher.model.MetroPage;
import com.eostek.tv.launcher.ui.adapter.SourceAdapter;
import com.eostek.tv.launcher.ui.adapter.TitleAdapter;
import com.eostek.tv.launcher.ui.adapter.ViewPagerAdapter;
import com.eostek.tv.launcher.ui.view.FocusView;
import com.eostek.tv.launcher.ui.view.ObservableScrollView;
import com.eostek.tv.launcher.ui.view.PagedGroup;
import com.eostek.tv.launcher.ui.view.ReflectImage;
import com.eostek.tv.launcher.ui.view.ReflectionTView;
import com.eostek.tv.launcher.util.LConstants;
import com.eostek.tv.launcher.util.TvUtils;
import com.eostek.tv.launcher.util.UIUtil;
import com.eostek.tv.launcher.util.ViewPositionUtil;
import com.mstar.android.tv.TvCommonManager;
import com.mstar.android.tvapi.common.vo.TvOsType.EnumInputSource;
import com.nostra13.universalimageloader.core.ImageLoader;
import com.nostra13.universalimageloader.core.assist.FailReason;
import com.nostra13.universalimageloader.core.listener.SimpleImageLoadingListener;

/*
 * projectName： TVLauncher
 * moduleName： ViewHolder.java
 *
 * @author chadm.xiang
 * @version 1.0.0
 * @time  2014-7-11 下午1:47:55
 * @Copyright © 2014 Eos Inc.
 */

public class ViewHolder {

    public final String TAG = ViewHolder.class.getSimpleName();

    private final float ScaleFactor = 1.1f;

    private HomeActivity mContext;

    private Handler mHandler;

    ViewPager viewPager;

    private FocusView focusView;

    GridView titGridView;

    private ReflectionTView mRelativeTvView;

    private ListView mListView;

    private SurfaceView mSurfaceView;

    private ImageButton tvImageButton;

    private ImageView mLogoView;

    private DBManager mDbManager;

    private TitleAdapter titleAdapter;

    private List<String> titleList;

    volatile ViewPagerAdapter vpAdapter;

    private ArrayList<View> pageViewsList;

    private List<MetroPage> mMetroPages = new ArrayList<MetroPage>();

    private JsonHeadBean mHeadBean;

    private int lastSelect = -1;

    private volatile int mCurTitleIndex = 0;

    private int mFocusBorderGap;

    private int focusType;

    private boolean hasTV = false;
    
    private boolean languageChange = false;

    public ViewHolder(Context context, Handler handler) {
        this.mContext = (HomeActivity) context;
        mHandler = handler;
        mDbManager = DBManager.getDbManagerInstance(HomeApplication.getInstance());
        hasTV = HomeApplication.isHasTVModule();
        focusType = HomeApplication.getFocusType();
    }

    /**
     * find views from the xml
     */
    public void findViews() {
        titGridView = (GridView) mContext.findViewById(R.id.title_grid);
        mLogoView = (ImageView) mContext.findViewById(R.id.logo_image);
        pageViewsList = new ArrayList<View>();
        viewPager = (ViewPager) mContext.findViewById(R.id.viewpager);
        mFocusBorderGap = mContext.getResources().getInteger(R.integer.focus_border_gap);
    }

    /**
     * init view from page.xml or db.
     * 
     * @since V1.2.1
     */
    public void initDataFromLocal(String country) {
        mMetroPages.clear();
        mMetroPages = mDbManager.getMetroPages(country);
        mHeadBean = mDbManager.getJsonHeadBean(country);
        initViewData();
    }

    /**
     * init the view from network json data
     * 
     * @param metropages MetroPage list
     * @param headBean JsonHeadBean object
     * @param updateUI True to udpate data,false not
     */
    public void updateDataFromNetwork(List<MetroPage> metropages, JsonHeadBean headBean, boolean updateUI) {
        Log.v(TAG, "updateDataFromNetwork updateUI = " + updateUI);
        mMetroPages.clear();
        mMetroPages = metropages;
        mHeadBean = headBean;
        if (updateUI) {
            reloadViewData();
        }
    }

    public ReflectionTView getmRelativeTvView() {
        return mRelativeTvView;
    }

    public DBManager getmDbManager() {
        return mDbManager;
    }

    public int getmCurTitleIndex() {
        return mCurTitleIndex;
    }
    
    public void setCurTitleIndex(int index) {
        mCurTitleIndex = index;
    }

    /**
     * release all reflection bitmap,include image reflection and TView
     * reflection
     */
    public void releaseResources() {
        Log.v(TAG, "releaseResources");
        // release image reflection bitmap
        if (vpAdapter != null) {
            for (int i = 0; i < vpAdapter.getCount(); i++) {
                PagedGroup group = vpAdapter.getPagedGroup(i);
                group.releasePageReSource();
            }
            vpAdapter = null;
        }
        // release TView reflection bitmap
        if (mRelativeTvView != null) {
            mRelativeTvView.releaseTViewReflection();
            mRelativeTvView.removeAllViews();
            mRelativeTvView = null;
        }
    }

    /**
     * reload string resource when local change
     */
    public void reloadLocaleString() {
        initTitleGridView();
        if (hasTV && mRelativeTvView != null) {
            mRelativeTvView.notifiDateSetChange();
        }
    }

    /**
     * reload data from local db when locale change
     * 
     * @since V1.2.1
     */
    public void reloadLocalData() {
        // the db do not current country data
        if (!mDbManager.isDBEmpty(UIUtil.getLanguage())) {
            languageChange = true;
            initDataFromLocal(UIUtil.getLanguage());
        } else {
            // just reload local string
            reloadLocaleString();
        }
    }

    // get SurfaceView
    public SurfaceView getMsurfaceView() {
        return mSurfaceView;
    }

    /**
     * load all image resource
     */
    public void setImageResouce() {
        Log.v(TAG, "setImageResouce");
        int tmp = lastSelect;
        if (mMetroPages == null || mMetroPages.isEmpty()) {
            return;
        }
        long time = System.currentTimeMillis();
        // to speed load the all view, load the current page immediately,then
        // delay load the other pages
        if (tmp == -1) {
            setReflectResource(mMetroPages.get(0), 0);
            tmp = 0;
        } else {
            setReflectResource(mMetroPages.get(lastSelect), lastSelect);
        }
        Log.v(TAG, "load page " + tmp);
        for (int i = 0; i < mMetroPages.size(); i++) {
            if (tmp != i) {
                mHandler.postDelayed(new LoadImageRunnable(mMetroPages.get(i), i), i * 100);
            }
        }
        Log.v(TAG, "setImageResouce time = " + (System.currentTimeMillis() - time));
    }

    private void reloadViewData() {
        lastSelect = -1;
        // releaseResources();
        initViewData();
        if (hasTV) {
            // when udpate data from network,we need check source
            mContext.backHomeSource();
            titGridView.setSelection(0);
            mCurTitleIndex = 0;
        }
    }

    /**
     * init view data to show the UI interface
     */
    private void initViewData() {
        Log.v(TAG, "initViewData mCurTitleIndex = " + mCurTitleIndex);
        initLogo();
        // 1.init the title gridview
        initTitleGridView();
        // 2.init the viewpager
        initViewPager();
        // 3. set the default value
        focusView = vpAdapter.getPageFocusView(0);
        setTitleGridViewListener();
        // setTitleGridViewBackgroud(0);
        if (hasTV) {
            // after load all view,init the tv view and set tv listener
            initTView();
            mContext.mTvRunningStat = Settings.System.getInt(mContext.getContentResolver(), "tvplayer_visible", 0);
            if (UIUtil.getBooleanFromXml(mContext, "isLocaleChange")) {
                titGridView.setSelection(mCurTitleIndex);
                if (lastSelect == -1) {
                    lastSelect = 0;
                }
                viewPager.setCurrentItem(lastSelect);
                setTitleGridViewBackgroud(mCurTitleIndex);
            } else if (mContext.mTvRunningStat == 1) {
                titGridView.setSelection(0);
                mCurTitleIndex = 0;
            } else {
                titGridView.setSelection(1);
                mCurTitleIndex = 1;
            }
        } else {
            titGridView.setSelection(0);
            mCurTitleIndex = 0;
        }
        Log.v(TAG, "initViewData mCurTitleIndex = " + mCurTitleIndex);
    }

    private void initLogo() {
        // if in dangle,use the default picture
        if (!hasTV) {
            return;
        }
        if (mHeadBean != null) {
            final int left = mContext.getResources().getInteger(R.integer.logo_iv_margin_left);
            final int top = mContext.getResources().getInteger(R.integer.logo_iv_margin_top);
            if (TextUtils.isEmpty(mHeadBean.getLogoUrl())) {
                mLogoView.setBackgroundResource(R.drawable.logo);
                ViewPositionUtil.setViewLayout(mLogoView, left, top);
            } else {
                ImageLoader imageLoader = ImageLoader.getInstance();
                imageLoader.loadImage(mHeadBean.getLogoUrl(), new SimpleImageLoadingListener() {

                    @Override
                    public void onLoadingComplete(String requestUri, View view, Object dataObject) {
                        super.onLoadingComplete(requestUri, view, dataObject);
                        if (dataObject == null) {
                            mLogoView.setBackgroundResource(R.drawable.logo);
                            ViewPositionUtil.setViewLayout(mLogoView, left, top);
                        } else {
                            mLogoView.setImageBitmap((Bitmap) dataObject);
                            ViewPositionUtil.setViewLayout(mLogoView, mHeadBean.getLogoX(), mHeadBean.getLogoY());
                        }

                    }

                    @Override
                    public void onLoadingFailed(String requestUri, View view, FailReason failReason) {
                        super.onLoadingFailed(requestUri, view, failReason);
                    }

                    @Override
                    public void onLoadingStarted(String requestUri, View view) {
                        super.onLoadingStarted(requestUri, view);
                    }

                });
            }
        }
    }

    /**
     * init title gridview
     */
    private void initTitleGridView() {
        // init title gridview
        if (titleList == null) {
            titleList = new ArrayList<String>();
        } else {
            titleList.clear();
        }
        // only add my_tv when has TV module
        if (hasTV) {
            titleList.add(mContext.getResources().getString(R.string.my_tv));
        }

        for (int i = 0; i < mMetroPages.size(); i++) {
            titleList.add(mMetroPages.get(i).getTitle());
        }
        titleAdapter = new TitleAdapter(mContext, titleList);
        titGridView.setAdapter(titleAdapter);
    }

    /**
     * init viewpager,the main UI interface
     */
    private void initViewPager() {
        synchronized (viewPager) {
            viewPager.removeAllViews();
            releaseResources();
            // load all page view
            if (pageViewsList == null) {
                pageViewsList = new ArrayList<View>();
            } else {
                pageViewsList.clear();
            }
            for (int i = 0; i < titleList.size(); i++) {
                // only add my_tv when has TV module
                if (hasTV) {
                    if (i == 0) {
                        continue; // the first page is my tv,just skip
                    }
                    loadPageView(titleList.get(i), i - 1);
                } else {
                    loadPageView(titleList.get(i), i);
                }
            }
            vpAdapter = new ViewPagerAdapter(pageViewsList, mContext);
            vpAdapter.notifyDataSetChanged();
            viewPager.setAdapter(vpAdapter);
            viewPager.setOnPageChangeListener(new PageListener());
            setImageResouce();
        }
    }

    private class LoadImageRunnable implements Runnable {

        private MetroPage mPage;

        private int mPageNum;

        public LoadImageRunnable(MetroPage page, int num) {
            this.mPage = page;
            this.mPageNum = num;
        }

        /*
         * (non-Javadoc)
         * @see java.lang.Runnable#run()
         */
        @Override
        public void run() {
            setReflectResource(mPage, mPageNum);
            Log.v(TAG, "load page " + mPageNum);
        }

    }

    /**
     * load each page view
     * 
     * @param title The page title name
     * @param pageNum The current page num
     */
    private void loadPageView(String title, int pageNum) {
        List<MetroInfo> metroInfos = new ArrayList<MetroInfo>();
        metroInfos = mMetroPages.get(pageNum).getListInPage();

        LayoutInflater inflater = mContext.getLayoutInflater();
        View pageView = inflater.inflate(R.layout.page_layout, null);
        focusView = (FocusView) pageView.findViewById(R.id.focusview);
        ObservableScrollView hScrollView = (ObservableScrollView) pageView.findViewById(R.id.scroll);
        hScrollView.setPageNum(pageNum);
        // hScrollView.seton
        PagedGroup pagedGroup = (PagedGroup) pageView.findViewById(R.id.content);
        pagedGroup.setFocusView(focusView);
        pagedGroup.loadAllView(metroInfos, pageNum);

        pageViewsList.add(pageView);
    }

    /**
     * set reflectImage resource
     * 
     * @param metroPage
     * @param pageNum
     */
    private void setReflectResource(MetroPage metroPage, int pageNum) {
        List<MetroInfo> metroInfos = metroPage.getListInPage();
        if (metroInfos != null) {
            PagedGroup group = vpAdapter.getPagedGroup(pageNum);
            // set pic for each view
            for (int j = 0; j < metroInfos.size(); j++) {
                ReflectImage vReflectLayout = (ReflectImage) group.getChildAt(j);
                String filePath = vReflectLayout.getMetroInfo().getIconPathB();
                if (vReflectLayout.getMetroInfo().getItemType() == LConstants.METRO_ITEM_WIDGET) {
                    vReflectLayout.loadWidget(filePath, mContext.getAppWidgetManager(), mContext.getAppWidgetHost());
                } else {
                    if (TextUtils.isEmpty(filePath)) {
                        Log.e(TAG, " illeagle backgroud path " + filePath);
                        UIUtil.uploadNetworkDataError(mContext, "illeagle backgroud path");
                    } else {
                        int length = filePath.length();
                        int resId = UIUtil.getResourceId(mContext, filePath.substring(0, length - 4));
                        if (resId != 0) {
                            vReflectLayout.loadDrawable(resId);
                        } else {
                            vReflectLayout.loadHttp(filePath);
                        }
                    }
                }
            }
        }
    }

    private void initTView() {
        View mTvpageView = pageViewsList.get(0);
        PagedGroup pagedGroup = (PagedGroup) mTvpageView.findViewById(R.id.content);
        mRelativeTvView = pagedGroup.getTvViewRl();
        mRelativeTvView.isInitLV = true;
        mListView = mRelativeTvView.getSourceListView();
        boolean[] signalStatus = TvCommonManager.getInstance().GetInputSourceStatus();
        SourceAdapter sa = ((SourceAdapter) (mListView.getAdapter()));
        // ATV and DTV do not support ,so do not change the ATV and DTV value
        if (signalStatus != null && signalStatus.length != 0) {
            sa.signalStatus[2] = signalStatus[EnumInputSource.E_INPUT_SOURCE_HDMI.ordinal()]; // HDMI1
            sa.signalStatus[3] = signalStatus[EnumInputSource.E_INPUT_SOURCE_HDMI2.ordinal()]; // HDMI2
            sa.signalStatus[4] = signalStatus[EnumInputSource.E_INPUT_SOURCE_HDMI3.ordinal()]; // HDMI3
            sa.signalStatus[5] = signalStatus[EnumInputSource.E_INPUT_SOURCE_CVBS.ordinal()]; // AV
            sa.signalStatus[6] = signalStatus[EnumInputSource.E_INPUT_SOURCE_YPBPR.ordinal()]; // YPBPR
            sa.signalStatus[7] = signalStatus[EnumInputSource.E_INPUT_SOURCE_VGA.ordinal()]; // VGA
        } else {
            Log.i(TAG, "GetInputSourceStatus return empty");
            for (int i = 2; i < sa.signalStatus.length; i++) {
                sa.signalStatus[i] = false;
            }
        }

        mSurfaceView = mRelativeTvView.getSurfaceView();
        tvImageButton = mRelativeTvView.getTvImageButton();

        TViewOnKeyListener onKeyListener = new TViewOnKeyListener();
        TViewOnFocusLisener onFocusLisener = new TViewOnFocusLisener();

        tvImageButton.setOnKeyListener(onKeyListener);
        tvImageButton.setOnFocusChangeListener(onFocusLisener);
        tvImageButton.setOnClickListener(new TvImageButtonOnclickListener());

        mListView.setOnFocusChangeListener(onFocusLisener);
        mListView.setOnItemClickListener(new SouceItemclickListener());
        mListView.setOnKeyListener(onKeyListener);
    }

    /**
     * set the backgroud and text color for the textview in gridview given
     * position
     * 
     * @param position
     */
    void setTitleGridViewBackgroud(int position) {
        Log.v(TAG, "setTitleGridViewBackgroud -->position = " + position + "; mCurTitleIndex = " + mCurTitleIndex);
        if (hasTV) {
            if (position == 1 && mCurTitleIndex == 0 && mRelativeTvView != null) {
                UIUtil.viewScaleDown(mContext, mRelativeTvView, ScaleFactor, ScaleFactor);
            }
        }
        mHandler.removeCallbacks(sourceRunnable);
        for (int i = 0; i < titGridView.getChildCount(); i++) {
            TextView textView = (TextView) titGridView.getChildAt(i).findViewById(R.id.title);
            textView.setTextColor(Color.parseColor("#80FFFFFF"));
            if (i == position) {
                textView.setBackgroundResource(R.drawable.gridview_title_bg);
                if (titGridView.hasFocus()) {
                    textView.setTextColor(Color.parseColor("#FFFFFFFF"));
                }
            } else {
                textView.setBackgroundResource(R.color.transparent);
            }
        }
        if (mCurTitleIndex != position) {
            mHandler.postDelayed(sourceRunnable, 10);
        }else {
            if (languageChange) {
                languageChange = false;
                mHandler.postDelayed(sourceRunnable, 10);
                Log.i(TAG,"disable mute when language change");
            }
        }
        mCurTitleIndex = position;
    }

    private Runnable sourceRunnable = new Runnable() {

        @Override
        public void run() {
            if (hasTV) {
                if (mCurTitleIndex == 0) {
                    EnumInputSource curSource = TvCommonManager.getInstance().getCurrentInputSource();
                    if (curSource == EnumInputSource.E_INPUT_SOURCE_STORAGE) {
                        curSource = EnumInputSource.values()[TvUtils.queryCurInputSrc(HomeApplication.getInstance())];
                    }
                    mContext.toChangeInputSource = curSource;
                    mHandler.postDelayed(new Runnable() {

                        @Override
                        public void run() {
                            mSurfaceView.setBackgroundColor(Color.TRANSPARENT);
                        }
                    }, 300);
                    TvUtils.pageChangeMute(mContext, false);
                } else {
                    TvUtils.pageChangeMute(mContext, true);
                    mSurfaceView.setBackgroundColor(Color.BLACK);
                }
            }
        }
    };

    /**
     * set the listener for title gridview
     */
    private void setTitleGridViewListener() {
        if (hasTV) {
            setGridViewListenerHasTV();
        } else {
            setGridViewListenerWithoutTV();
        }

        titGridView.setOnFocusChangeListener(new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    Log.v(TAG, "titGridView onFocusChange gain focus");
                    titGridView.setSelection(mCurTitleIndex);
                    focusView.setVisibility(View.GONE);
                } else {
                    if (focusType == LConstants.FOCUS_TYPE_STATIC) {
                        focusView.setVisibility(View.GONE);
                    } else if (focusType == LConstants.FOCUS_TYPE_DYNAMIC) {
                        focusView.setVisibility(View.VISIBLE);
                    }
                }
                setTitleGridViewBackgroud(mCurTitleIndex);
            }
        });
    }

    private void setGridViewListenerHasTV() {
        titGridView.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.v(TAG, "onItemSelected postion = " + position);
                if (position == 0) {
                    if (viewPager.getCurrentItem() != 0) {
                        viewPager.setCurrentItem(0);
                    } else {
                        vpAdapter.scrollToOrigin(0);
                    }
                } else if (position == 1) {
                    if (viewPager.getCurrentItem() != 0) {
                        viewPager.setCurrentItem(0);
                    } else {
                        if (vpAdapter.getScrollDistance(0) == 0) {
                            vpAdapter.scrollTv(0);
                        }
                    }
                } else {
                    if (viewPager.getCurrentItem() != position - 1) {
                        viewPager.setCurrentItem(position - 1);
                    }
                }
                setTitleGridViewBackgroud(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        titGridView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if (position == 0) {
                    if (viewPager.getCurrentItem() != 0) {
                        viewPager.setCurrentItem(0);
                    } else {
                        vpAdapter.scrollToOrigin(0);
                    }
                } else if (position == 1) {
                    if (viewPager.getCurrentItem() != 0) {
                        viewPager.setCurrentItem(0);
                    } else {
                        vpAdapter.scrollTv(0);
                    }
                } else {
                    viewPager.setCurrentItem(position - 1);
                }
                setTitleGridViewBackgroud(position);
                if (!titGridView.hasFocus()) {
                    mListView.clearFocus();
                    titGridView.requestFocus();
                }
                Log.v(TAG, "onItemClick postion = " + position);
            }
        });
    }

    private void setGridViewListenerWithoutTV() {
        titGridView.setOnItemSelectedListener(new OnItemSelectedListener() {

            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                Log.v(TAG, "onItemSelected postion = " + position);
                viewPager.setCurrentItem(position);
                setTitleGridViewBackgroud(position);
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        titGridView.setOnItemClickListener(new OnItemClickListener() {

            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Log.v(TAG, "onItemClick");
                viewPager.setCurrentItem(position);
                setTitleGridViewBackgroud(position);
            }
        });
    }

    private class PageListener implements OnPageChangeListener {

        @Override
        public void onPageScrollStateChanged(int arg0) {

        }

        /**
         */
        @Override
        public void onPageScrolled(int arg0, float arg1, int arg2) {
            // Log.v(TAG_LISTENER, "onPageScrolled = " + arg1);
        }

        @Override
        public void onPageSelected(int position) {
            focusView = vpAdapter.getPageFocusView(position);
            focusView.setVisibility(View.GONE);
            if (lastSelect == -1) {
                lastSelect = 0;
            }

            if (hasTV) {
                if (position == 0) {
                    if (vpAdapter.getScrollDistance(position) == 0) {
                        setTitleGridViewBackgroud(0);
                    } else {
                        setTitleGridViewBackgroud(1);
                    }
                } else {
                    setTitleGridViewBackgroud(position + 1);
                }
            } else {
                setTitleGridViewBackgroud(position);
            }

            Log.v(TAG, "lastSelect = " + lastSelect + "; position = " + position);
            lastSelect = position;

        }
    }

    private class TViewOnFocusLisener implements OnFocusChangeListener {

        /*
         * (non-Javadoc)
         * @see
         * android.view.View.OnFocusChangeListener#onFocusChange(android.view
         * .View, boolean)
         */
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
                Log.v(TAG, "TViewOnkeyListener onFocusChange ");
                focusView.setVisibility(View.GONE);
                if (mCurTitleIndex != 0) {
                    setTitleGridViewBackgroud(0);
                }
                vpAdapter.scrollToOrigin(0);
                if (v.getId() == R.id.tv_image) {
                    if (focusType == LConstants.FOCUS_TYPE_STATIC) {
                        tvImageButton.setBackgroundResource(R.drawable.imagebutton_focus_border);
                    } else if (focusType == LConstants.FOCUS_TYPE_DYNAMIC) {
                        Rect rect = new Rect();
                        tvImageButton.getGlobalVisibleRect(rect);
                        if (rect.top > mContext.getResources().getInteger(R.integer.view_holder_off)) {
                            rect.top -= mContext.getResources().getInteger(R.integer.view_holder_off);
                            rect.bottom -= mContext.getResources().getInteger(R.integer.view_holder_off);
                        }
                        rect.left -= mFocusBorderGap;
                        rect.top -= mFocusBorderGap;
                        rect.bottom += mFocusBorderGap;
                        rect.right += mFocusBorderGap;
                        focusView.startAnimation(rect, false);
                    }
                    if (null != mRelativeTvView.tmpView) {
                        UIUtil.viewScaleDown(mContext, mRelativeTvView.tmpView, ScaleFactor, ScaleFactor);
                        mRelativeTvView.tmpView = null;
                    }
                } else if (v.getId() == R.id.source_list) {
                    if (null != mRelativeTvView.tmpView) {
                        UIUtil.viewScaleDown(mContext, mRelativeTvView.tmpView, ScaleFactor, ScaleFactor);
                    }
                    if (null != mListView.getSelectedView()) {
                        UIUtil.viewScaleUp(mContext, mListView.getSelectedView(), ScaleFactor, ScaleFactor);
                    }
                    mRelativeTvView.tmpView = mListView.getSelectedView();
                    if (mListView.getSelectedView() != null) {
                        ((ViewGroup) (mListView.getSelectedView())).getChildAt(0).setBackgroundResource(
                                R.drawable.source_item_backgroud);
                    }
                }
            } else {
                if (null != mRelativeTvView.tmpView) {
                    UIUtil.viewScaleDown(mContext, mRelativeTvView.tmpView, ScaleFactor, ScaleFactor);
                    mRelativeTvView.tmpView = null;
                }
                if (focusType == LConstants.FOCUS_TYPE_STATIC) {
                    tvImageButton.setBackgroundResource(R.color.transparent);
                }
                if (mListView.getSelectedView() != null) {
                    ((ViewGroup) (mListView.getSelectedView())).getChildAt(0).setBackgroundResource(
                            R.color.defaultsource_color);
                }
            }
        }
    };

    private class TViewOnKeyListener implements OnKeyListener {

        /*
         * (non-Javadoc)
         * @see android.view.View.OnKeyListener#onKey(android.view.View, int,
         * android.view.KeyEvent)
         */
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            if (event.getAction() == KeyEvent.ACTION_DOWN) {
                if (keyCode == KeyEvent.KEYCODE_DPAD_RIGHT) {
                    Log.v(TAG, "onKey KEYCODE_DPAD_RIGHT ");
                    setTitleGridViewBackgroud(1);
                    vpAdapter.scrollTv(0);
                    focusView.setVisibility(View.VISIBLE);
                }
            }
            return false;
        }

    };

    private class TvImageButtonOnclickListener implements OnClickListener {
        @Override
        public void onClick(View v) {
            mSurfaceView.setBackgroundColor(Color.BLACK);
            TvUtils.startTV(mContext);
        }

    };

    private class SouceItemclickListener implements OnItemClickListener {
        @Override
        public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
            EnumInputSource curSource = EnumInputSource.values()[TvUtils.queryCurInputSrc(HomeApplication
                    .getInstance())];
            if (curSource == TvUtils.getCurInputSource(position)) {
                Log.d(TAG, "the source is current source .");
                return;
            }
            mRelativeTvView.setSourceText(position);
            mContext.sourceChageHandle(TvUtils.getCurInputSource(position), 0);
            mRelativeTvView.setSourceText(position);
        }
    }
    
}
