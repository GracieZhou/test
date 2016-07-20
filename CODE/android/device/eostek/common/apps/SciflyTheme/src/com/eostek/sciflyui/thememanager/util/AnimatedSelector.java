
package com.eostek.sciflyui.thememanager.util;

/*
 * Copyright (C) 2011 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

import android.animation.IntEvaluator;
import android.animation.TypeEvaluator;
import android.animation.ValueAnimator;
import android.animation.ValueAnimator.AnimatorUpdateListener;
import android.graphics.Canvas;
import android.graphics.ColorFilter;
import android.graphics.Rect;
import android.graphics.drawable.Animatable;
import android.graphics.drawable.Drawable;
import android.view.View;
import android.view.animation.DecelerateInterpolator;
import android.widget.FrameLayout;
import android.widget.FrameLayout.LayoutParams;

/**
 * Selector which delegates drawable calls to an external view.
 * <p>
 * This the selector view to be animated behind a view while taking advantage of
 * the faster matrix drawing transformation properties of the view.
 */
public final class AnimatedSelector extends Drawable implements Animatable {

    private static final int WIDTH_OFFSET = 28;

    private static final int TOP_OFFSET = 15;

    private static final int LEFT_OFFSET = 15;

    private static final int MOVE_DURATION_MS = 220;

    private View mView;

    private Drawable mDrawable;

    private ValueAnimator mAnimator;

    private PositionEvaluator mPositionEvaluator;

    private int mLeftOffset = -LEFT_OFFSET;

    private int mTopOffset = -TOP_OFFSET;

    private int mWidthOffset = WIDTH_OFFSET;

    private int mHeightOffset = 0;

    /**
     * @param view view
     * @param drawable drawable
     */
    public AnimatedSelector(View view, Drawable drawable) {
        mView = view;
        mDrawable = drawable;
        mPositionEvaluator = new PositionEvaluator();
        mAnimator = ValueAnimator.ofObject(mPositionEvaluator);
        mAnimator.setDuration(MOVE_DURATION_MS);
        mAnimator.setInterpolator(new DecelerateInterpolator(2.0f));
        mAnimator.addUpdateListener(new AnimatorUpdateListener() {

            @Override
            public void onAnimationUpdate(ValueAnimator animation) {

                Rect bounds = getBounds();
                Rect newBounds = (Rect) animation.getAnimatedValue();
                if (!bounds.equals(newBounds)) {
                    AnimatedSelector.super.setBounds(newBounds.left, newBounds.top, newBounds.right, newBounds.bottom);
                    mView.setX(newBounds.left + mLeftOffset);
                    mView.setY(newBounds.top + mTopOffset);
                }
            }
        });
    }

    @Override
    public void setBounds(int left, int top, int right, int bottom) {
        // Animate setting the bounds.
        Rect bounds = getBounds();
        if (bounds.isEmpty()) {
            super.setBounds(left, top, right, bottom);
            mView.setX(left + mLeftOffset);
            mView.setY(top + mTopOffset);
            setSelectorDimensions(right - left, bottom - top);
            ensureViewVisible();
        } else if (bounds.left != left || bounds.top != top) {
            if (mAnimator.isRunning()) {
                mAnimator.cancel();
                mAnimator.setDuration(MOVE_DURATION_MS - ((long) mAnimator.getAnimatedFraction() * MOVE_DURATION_MS));
            } else {
                mAnimator.setDuration(MOVE_DURATION_MS);
            }
            mAnimator.setObjectValues(copyBounds(), new Rect(left, top, right, bottom + 10));
            mAnimator.setEvaluator(mPositionEvaluator);
            mAnimator.start();
        }
    }

    @Override
    public void draw(Canvas canvas) {
    }

    @Override
    public int getOpacity() {
        return mDrawable.getOpacity();
    }

    @Override
    public boolean getPadding(Rect padding) {
        return mDrawable.getPadding(padding);
    }

    @Override
    public void setAlpha(int alpha) {
    }

    @Override
    public void setColorFilter(ColorFilter cf) {
    }

    @Override
    public boolean isRunning() {
        return mAnimator.isRunning();
    }

    @Override
    public void start() {
        mAnimator.start();
    }

    @Override
    public void stop() {
        mAnimator.cancel();
    }

    /**
     * @param left left offset
     */
    public void setLeftOffset(int left) {
        mLeftOffset += left;
    }

    /**
     * @param height height
     */
    public void setheighOffset(int height) {
        mHeightOffset += height;
    }

    /**
     * @param top top
     */
    public void setTopOffset(int top) {
        mTopOffset += top;
    }

    /**
     * ensure View Visible.
     */
    public void ensureViewVisible() {
        if (mView.getVisibility() != View.VISIBLE && !mView.isInTouchMode()) {
            mView.setVisibility(View.VISIBLE);
        }
    }

    /**
     * hide view.
     */
    public void hideView() {
        if (mView.getVisibility() != View.INVISIBLE) {
            mView.setVisibility(View.INVISIBLE);
        }
    }

    /**
     * Sets the size of the selector view.
     */
    private void setSelectorDimensions(int width, int height) {
        LayoutParams params = (FrameLayout.LayoutParams) mView.getLayoutParams();
        params.width = width + mWidthOffset;
        params.height = height + mHeightOffset;
        mView.setLayoutParams(params);
    }

    /**
     * @return get Width Offset
     */
    public int getmWidthOffset() {
        return mWidthOffset;
    }

    /**
     * @param widthOffset Width Offset
     */
    public void setmWidthOffset(int widthOffset) {
        this.mWidthOffset += widthOffset;
    }

    /**
     * Takes Rect objects and evaluates fractional positions between the two
     * points.
     */
    @SuppressWarnings("rawtypes")
    private static class PositionEvaluator implements TypeEvaluator {

        private IntEvaluator mEvaluator;

        public PositionEvaluator() {
            mEvaluator = new IntEvaluator();
        }

        @Override
        public Object evaluate(float fraction, Object startValue, Object endValue) {
            Rect startRect = (Rect) startValue;
            Rect endRect = (Rect) endValue;
            int left = (Integer) mEvaluator.evaluate(fraction, startRect.left, endRect.left);
            int top = (Integer) mEvaluator.evaluate(fraction, startRect.top, endRect.top);
            return new Rect(left, top, left + startRect.width(), top + startRect.height());
        }
    }
}
