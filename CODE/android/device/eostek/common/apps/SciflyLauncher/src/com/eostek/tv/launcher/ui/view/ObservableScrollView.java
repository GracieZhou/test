
package com.eostek.tv.launcher.ui.view;

import com.eostek.tv.launcher.R;
import com.eostek.tv.launcher.HomeApplication;

import android.content.Context;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MotionEvent;
import android.widget.HorizontalScrollView;

/*
 * projectName： TVLauncher
 * moduleName： ObservableScrollView.java
 *
 * @author chadm.xiang
 * @version 1.0.0
 * @time  2014-7-14 下午5:25:00
 * @Copyright © 2014 Eos Inc.
 */

public class ObservableScrollView extends HorizontalScrollView {

    private final String TAG = ObservableScrollView.class.getSimpleName();

    private ScrollViewListener scrollViewListener = null;

    private OnEndScrollListener mOnEndScrollListener;

    private Runnable scrollerTask;

    private int initialPosition;

    private int newCheck = 10;

    private int pageNum = -1;

    private Context mContext;

    public interface OnEndScrollListener {
        void onEndScroll(ObservableScrollView scrollView);
    }

    public interface ScrollViewListener {
        void onScrollChanged(ObservableScrollView scrollView, int x, int y, int oldx, int oldy);
    }

    public ObservableScrollView(Context context) {
        super(context);
        mContext = context;
    }

    public ObservableScrollView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
    }

    public ObservableScrollView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }
    
    @Override
    public boolean executeKeyEvent(KeyEvent event) {
        // do not execute key in the View itself
        return false;
    }

    public void startScrollerTask() {
        scrollerTask = new Runnable() {
            public void run() {
                int newPosition = getScrollX();
                if (initialPosition - newPosition == 0) { // has stopped
                    if (mOnEndScrollListener != null) {
                        mOnEndScrollListener.onEndScroll(ObservableScrollView.this);
                    }
                } else {
                    initialPosition = getScrollX();
                    ObservableScrollView.this.postDelayed(scrollerTask, newCheck);
                }
            }
        };
        initialPosition = getScrollX();
        ObservableScrollView.this.postDelayed(scrollerTask, newCheck);
    }

    public void setScrollViewListener(ScrollViewListener listener) {
        this.scrollViewListener = listener;
    }

    public OnEndScrollListener getOnEndScrollListener() {
        return mOnEndScrollListener;
    }

    public void setOnEndScrollListener(OnEndScrollListener listener) {
        this.mOnEndScrollListener = listener;
    }

    @Override
    protected void onScrollChanged(int x, int y, int oldx, int oldy) {
        super.onScrollChanged(x, y, oldx, oldy);
        if (scrollViewListener != null) {
            scrollViewListener.onScrollChanged(this, x, y, oldx, oldy);
        }
    }

    @Override
    protected int computeScrollDeltaToGetChildRectOnScreen(Rect rect) {
        int off = super.computeScrollDeltaToGetChildRectOnScreen(rect);
//        Log.v(TAG, "computeScrollDeltaToGetChildRectOnScreen = " + rect.toShortString() + "; off = " + off
//                + "; pageNum = " + pageNum);
        if (off > 0) { // scroll right
            off += mContext.getResources().getInteger(R.integer.scroll_off);
        } else if (off < 0) { // scroll left
            if (pageNum == 0) {
                //set scroll distance
                if (HomeApplication.isHasTVModule()) {
                    off -= rect.left - mContext.getResources().getInteger(R.integer.focus_rect_left)
                            + mContext.getResources().getInteger(R.integer.page_tv_view_gap_right);
                } else {
                    off -= mContext.getResources().getInteger(R.integer.scroll_off);
                }
                Log.v(TAG, "after change -->computeScrollDeltaToGetChildRectOnScreen = " + off);
            } else {
                off -= mContext.getResources().getInteger(R.integer.scroll_off);
            }

        }
        return off;
    }

    @Override
    protected int computeHorizontalScrollRange() {
        int scrollRange = super.computeHorizontalScrollRange();
        // Log.v(TAG, "computeHorizontalScrollRange = " + scrollRange);
        // PagedGroup group = (PagedGroup) getChildAt(0);
        return scrollRange;
    }

    @Override
    protected int computeHorizontalScrollOffset() {
        int offset = super.computeHorizontalScrollOffset();
        // Log.v(TAG, "computeHorizontalScrollOffset = " + offset);
        return offset;
    }

    @Override
    public void computeScroll() {
        // Log.v(TAG, "computeScroll");
        super.computeScroll();
    }

    public void setPageNum(int num) {
        this.pageNum = num;
    }
    
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        Log.v(TAG, "onTouchEvent " + getScrollX());
        switch (event.getAction()) {
            case MotionEvent.ACTION_MOVE:
                // if the HorizontalScrollView do not scroll to the end,either
                // the left or right,handler the touch event itself.if scroll to
                // the end,pass the touch event to its parent view to handler
                // the touch event
                int maxScrollX = getChildAt(0).getMeasuredWidth() - getMeasuredWidth();
                if (getScrollX() == 0 || getScrollX() == maxScrollX) {
                    getParent().getParent().requestDisallowInterceptTouchEvent(false);
                } else {
                    getParent().getParent().requestDisallowInterceptTouchEvent(true);
                }
                break;
            case MotionEvent.ACTION_UP:
            case MotionEvent.ACTION_CANCEL:
                getParent().getParent().requestDisallowInterceptTouchEvent(false);
                break;
        }
        return super.onTouchEvent(event);
    }


}
