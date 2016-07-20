
package com.google.tv.eoslauncher.ui.dialog;

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

@TargetApi(Build.VERSION_CODES.HONEYCOMB)
@SuppressLint("NewApi")
public class FocusView extends ImageView {

    String TAG = "FocusImageView";

    private AnimatorSet mAnimatorSet;

    private ValueAnimator mXAnimator, mYAnimator, mWAnimator, mHAnimator;

    private int AnimationDuration = 200;

    private int mTopOffset = 0;

    private int mLeftOffset = 0;

    private Rect rect = new Rect();

    public FocusView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public FocusView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public FocusView(Context context) {
        super(context);
    }

    @TargetApi(Build.VERSION_CODES.HONEYCOMB)
    @SuppressLint("NewApi")
    private void init() {
        mXAnimator = ObjectAnimator.ofFloat(this, "x", 0);
        mYAnimator = ObjectAnimator.ofFloat(this, "y", 0);
        mWAnimator = ObjectAnimator.ofInt(this, "wid", 0);
        mHAnimator = ObjectAnimator.ofInt(this, "hei", 0);
        mAnimatorSet = new AnimatorSet();
        mAnimatorSet.setDuration(AnimationDuration);
        mAnimatorSet.playTogether(mWAnimator, mHAnimator, mXAnimator, mYAnimator);
        mAnimatorSet.addListener(new AnimatorListener() {

            @Override
            public void onAnimationStart(Animator animation) {
            }

            @Override
            public void onAnimationRepeat(Animator animation) {
            }

            @Override
            public void onAnimationEnd(Animator animation) {
                if (getVisibility() != View.VISIBLE) {
                    setVisibility(View.VISIBLE);
                }
            }

            @Override
            public void onAnimationCancel(Animator animation) {

            }
        });
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

    public void setTopOffset(int topOffset) {
        mTopOffset = topOffset;
    }

    public void setLeftOffset(int leftOffset) {
        mLeftOffset = leftOffset;
    }

    public void startAnimation(View focused) {
        if (mAnimatorSet == null || focused == null) {
            return;
        }
        mAnimatorSet.end();
        focused.getGlobalVisibleRect(rect);
        mXAnimator.setFloatValues(rect.left + mLeftOffset);
        mYAnimator.setFloatValues(rect.top + mTopOffset);
        mWAnimator.setIntValues(rect.width());
        mHAnimator.setIntValues(rect.height());
        mAnimatorSet.start();
    }
}
