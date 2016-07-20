
package com.eostek.tv.launcher.ui.adapter;

import java.util.ArrayList;
import java.util.List;

import com.eostek.tv.launcher.R;
import com.eostek.tv.launcher.model.MetroInfo;
import com.eostek.tv.launcher.model.MetroPage;
import com.eostek.tv.launcher.ui.view.FocusView;
import com.eostek.tv.launcher.ui.view.ObservableScrollView;
import com.eostek.tv.launcher.ui.view.PagedGroup;
import com.eostek.tv.launcher.ui.view.ReflectImage;
import com.eostek.tv.launcher.util.LConstants;
import com.eostek.tv.launcher.util.UIUtil;

import android.content.Context;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.View;
import android.view.ViewGroup;

/**
 * projectName： TVLauncher moduleName： ViewPagerAdapter.java
 * 
 * @author chadm.xiang
 * @version 1.0.0
 * @time 2014-7-11 下午2:54:16
 * @Copyright © 2014 Eos Inc.
 */
public class ViewPagerAdapter extends PagerAdapter {

    private final String TAG = ViewPagerAdapter.class.getSimpleName();

    private Context mContext;

    private int tvWidth;

    private int pagePadLeft;

    private View mCurrentView;

    private int mPosition;

    private ArrayList<View> views;

    public ViewPagerAdapter(ArrayList<View> view, Context context) {
        this.views = view;
        this.mContext = context;
        tvWidth = mContext.getResources().getInteger(R.integer.tv_view_width);
        pagePadLeft = mContext.getResources().getInteger(R.integer.page_group_left);
    }

    @Override
    public int getCount() {
        if (views != null) {
            return views.size();
        }
        return 0;
    }

    @Override
    public int getItemPosition(Object object) {
        return POSITION_NONE;
    }

    @Override
    public Object instantiateItem(View view, int position) {
        ((ViewPager) view).addView(views.get(position), 0);
        return views.get(position);
    }

    @Override
    public boolean isViewFromObject(View view, Object arg1) {
        return (view == arg1);
    }

    @Override
    public void setPrimaryItem(ViewGroup container, int position, Object object) {
        super.setPrimaryItem(container, position, object);
        mCurrentView = (View) object;
        mPosition = position;
    }

    @Override
    public void destroyItem(View view, int position, Object arg2) {
        if (position < views.size()) {
            ((ViewPager) view).removeView(views.get(position));
        }
    }

    public FocusView getPageFocusView(int pageNum) {
        View view = views.get(pageNum);
        PagedGroup group = (PagedGroup) view.findViewById(R.id.content);
        return group.getFocusView();
    }

    /**
     * get the scroll view for the given page
     * 
     * @param pageNum The page number
     * @return The scroll view
     */
    public ObservableScrollView getScrollView(int pageNum) {
        View view = views.get(pageNum);
        ObservableScrollView hScrollView = (com.eostek.tv.launcher.ui.view.ObservableScrollView) view
                .findViewById(R.id.scroll);
        return hScrollView;
    }

    /**
     * scroll to the left of the page
     * 
     * @param pageNum
     */
    public void scrollToOrigin(int pageNum) {
        scrollToPosition(pageNum, 0, 0);
    }

    private void scrollToPosition(int pageNum, int x, int y) {
        getScrollView(pageNum).smoothScrollTo(x, y);
    }

    /**
     * scroll the distance to hide tv view
     * 
     * @param pageNum
     */
    public void scrollTv(int pageNum) {
        scrollToPosition(pageNum, tvWidth + pagePadLeft, 0);
    }

    /**
     * get the PagedGroup for given page
     * 
     * @param pageNum
     * @return The PagedGroup
     */
    public PagedGroup getPagedGroup(int pageNum) {
        View v = views.get(pageNum);
        PagedGroup group = (PagedGroup) v.findViewById(R.id.content);
        return group;
    }

    /**
     * get the scroll distance
     * 
     * @param pageNum
     * @return The left edge of the displayed part of your view, in pixels.
     */
    public int getScrollDistance(int pageNum) {
        return getScrollView(pageNum).getScrollX();
    }

    public View getmCurrentView() {
        return mCurrentView;
    }

    public void setmCurrentView(View view) {
        this.mCurrentView = view;
    }

    public int getmPosition() {
        return mPosition;
    }

    public void setmPosition(int position) {
        this.mPosition = position;
    }

}
