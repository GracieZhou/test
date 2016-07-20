
package com.eostek.tv.launcher.ui.view;

import com.eostek.tv.launcher.R;
import com.eostek.tv.launcher.HomeApplication;
import com.eostek.tv.launcher.util.LConstants;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.animation.ValueAnimator;
import android.annotation.SuppressLint;
import android.annotation.TargetApi;
import android.content.Context;
import android.graphics.Rect;
import android.os.Build;
import android.util.AttributeSet;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;

public class FocusView extends ImageView {

    private final String TAG = FocusView.class.getSimpleName();

    private AnimatorSet mAnimatorSet;

    private ValueAnimator mXAnimator, mYAnimator, mWAnimator, mHAnimator, mScalXAnimator, mScalYAnimator;

    private int mAnimationDuration;

    private Context mContext;

    private int focusType;

    public FocusView(Context context, AttributeSet attrs) {
        super(context, attrs);
        mContext = context;
    }

    public FocusView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        mContext = context;
    }

    public FocusView(Context context) {
        super(context);
        mContext = context;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @SuppressLint("NewApi")
    private void init() {
        focusType = HomeApplication.getFocusType();
        mXAnimator = ObjectAnimator.ofFloat(this, "x", 0);
        mYAnimator = ObjectAnimator.ofFloat(this, "y", 0);
        mWAnimator = ObjectAnimator.ofInt(this, "wid", 0);
        mHAnimator = ObjectAnimator.ofInt(this, "hei", 0);
        mScalXAnimator = ObjectAnimator.ofFloat(this, "scaleX", 0);
        mScalYAnimator = ObjectAnimator.ofFloat(this, "scaleY", 0);
        mAnimatorSet = new AnimatorSet();
        mAnimatorSet.setDuration(mAnimationDuration);
        mAnimatorSet.playTogether(mWAnimator, mHAnimator, mXAnimator, mYAnimator, mScalXAnimator, mScalYAnimator);
        mAnimatorSet.addListener(new AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {
                if (focusType == LConstants.FOCUS_TYPE_STATIC) {
                    setVisibility(View.GONE);
                }
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (focusType == LConstants.FOCUS_TYPE_STATIC) {
                    setVisibility(View.GONE);
                } else if (focusType == LConstants.FOCUS_TYPE_DYNAMIC) {
                    setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }
        });

        mAnimationDuration = mContext.getResources().getInteger(R.integer.scale_animation_duration);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
        setDrawingCacheQuality(DRAWING_CACHE_QUALITY_LOW);
        setDrawingCacheEnabled(true);
        buildDrawingCache();
        setFocusable(false);
        init();
    }

    public int getWid() {
        RelativeLayout.LayoutParams layout = (RelativeLayout.LayoutParams) getLayoutParams();
        return layout.width;
    }

    public void setWid(int wid) {
        RelativeLayout.LayoutParams layout = (RelativeLayout.LayoutParams) getLayoutParams();
        layout.width = wid;
        setLayoutParams(layout);
    }

    public int getHei() {
        RelativeLayout.LayoutParams layout = (RelativeLayout.LayoutParams) getLayoutParams();
        return layout.height;
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    public void setHei(int hei) {
        RelativeLayout.LayoutParams layout = (RelativeLayout.LayoutParams) getLayoutParams();
        layout.height = hei;
        setLayoutParams(layout);
    }

    /**
     * draw the focus object at the position of given view. Get the rect object
     * call View.getGlobalVisibleRect(rect),then call startAnimation(Rect mRect)
     * 
     * @param focused The target view
     * @param isScaled scale the view if true,else not
     */
    public void startAnimation(View focused, boolean isScaled) {
        if (focused == null) {
            return;
        }
        Rect rect = new Rect();
        focused.getGlobalVisibleRect(rect);
        Rect tmpRect = new Rect();
        focused.getFocusedRect(tmpRect);
        int[] pos = {
                0, 0
        };
        focused.getLocationOnScreen(pos);
        startAnimation(rect, isScaled);
    }

    /**
     * draw the focus object at the position of given view. Get the rect object
     * call View.getGlobalVisibleRect(rect),then call startAnimation(Rect mRect)
     * 
     * @param focused The target view
     */
    public void startAnimation(View focused) {
        startAnimation(focused, true);
    }

    public void changeDuration() {
        mAnimationDuration = 0;
    }

    /**
     * draw the focus object depend on the given Rect object
     * 
     * @param mRect
     * @param isScaled scale the view if true,else not
     */
    public void startAnimation(Rect mRect, boolean isScaled) {
        if (mAnimatorSet == null || mRect == null) {
            return;
        }
        mAnimatorSet.end();
        // Log.v(TAG, "rect = " + mRect.toShortString());
        int focusViewGap = getResources().getInteger(R.integer.focus_view_gap);
        mXAnimator.setFloatValues(mRect.left - focusViewGap);
        mYAnimator.setFloatValues(mRect.top - focusViewGap);
        mWAnimator.setIntValues(mRect.width() + focusViewGap * 2);
        mHAnimator.setIntValues(mRect.height() + focusViewGap * 2);
        if (isScaled) {
            mScalXAnimator.setFloatValues(1.1f);
            mScalYAnimator.setFloatValues(1.1f);
        } else {
            mScalXAnimator.setFloatValues(1.0f);
            mScalYAnimator.setFloatValues(1.0f);
        }
        mAnimatorSet.setDuration(mAnimationDuration);
        mAnimatorSet.start();
        mAnimationDuration = mContext.getResources().getInteger(R.integer.scale_animation_duration);
    }

    /**
     * draw the focus object depend on the given Rect object
     * 
     * @param mRect
     */
    public void startAnimation(Rect mRect) {
        startAnimation(mRect, true);
    }
}
