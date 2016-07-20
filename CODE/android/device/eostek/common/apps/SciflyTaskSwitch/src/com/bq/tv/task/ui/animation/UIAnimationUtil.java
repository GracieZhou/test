
package com.bq.tv.task.ui.animation;

import com.nineoldandroids.animation.AnimatorSet;
import com.nineoldandroids.animation.ObjectAnimator;

import android.view.View;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.LayoutAnimationController;
import android.view.animation.ScaleAnimation;
import android.view.animation.TranslateAnimation;

/**
 *  Utils of UI animation.
 */
public class UIAnimationUtil {

    /**
     * Layout animation.
     * 
     * @return
     */

    public static AnimatorSet createTranslationAnimations(View view, float startX, float endX, float startY, float endY) {
        ObjectAnimator animX = ObjectAnimator.ofFloat(view, "translationX", startX, endX);
        ObjectAnimator animY = ObjectAnimator.ofFloat(view, "translationY", startY, endY);
        AnimatorSet animSetXY = new AnimatorSet();
        animSetXY.playTogether(animX, animY);
        return animSetXY;
    }

    /**
     * Get layout show animation.
     * @param mStartDelay
     * @return
     */
    public static LayoutAnimationController getLayoutShowAnimation(int mStartDelay) {
        int duration = 200;
        int delay = mStartDelay >= 0 ? mStartDelay : 0;
        AnimationSet set = new AnimationSet(true);

        Animation animation = new AlphaAnimation(0f, 1.0f);
        animation.setStartOffset(delay);
        animation.setDuration(duration);
        set.addAnimation(animation);

        animation = new ScaleAnimation(0.3f, 1.0f, 0.3f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setStartOffset(delay);
        animation.setDuration(duration);
        set.addAnimation(animation);

        LayoutAnimationController controller = new LayoutAnimationController(set, 0.35f);
        controller.setOrder(LayoutAnimationController.ORDER_NORMAL);
        return controller;
    }

    /**
     * Get layout swap animation.
     * @param mStartDelay
     * @return
     */
    public static LayoutAnimationController getLayoutSwapAnimation(int mStartDelay) {
        int duration = 200;
        int delay = mStartDelay >= 0 ? mStartDelay : 0;
        AnimationSet set = new AnimationSet(true);

        Animation animation = new AlphaAnimation(0f, 1.0f);
        animation.setStartOffset(delay);
        animation.setDuration(duration);
        set.addAnimation(animation);

        animation = new ScaleAnimation(0.3f, 1.0f, 0.3f, 1.0f, Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setStartOffset(delay);
        animation.setDuration(duration);
        set.addAnimation(animation);

        LayoutAnimationController controller = new LayoutAnimationController(set, 0.35f);
        controller.setOrder(LayoutAnimationController.ORDER_REVERSE);
        return controller;
    }

    /**
     * Get view scale hide animation.
     * @param duration
     * @return
     */
    public static AnimationSet getViewScaleHideAnimation(int duration) {
        int dur = duration >= 0 ? duration : 200;
        int delay = 0;
        AnimationSet set = new AnimationSet(true);

        Animation animation = new AlphaAnimation(1.0f, 0f);
        animation.setStartOffset(delay);
        animation.setDuration(dur);
        set.addAnimation(animation);

        animation = new ScaleAnimation(1.0f, 0.0f, 1.0f, 0.0f, Animation.RELATIVE_TO_SELF, 0.5f,
                Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setStartOffset(delay);
        animation.setDuration(dur);
        set.addAnimation(animation);

        return set;
    }

    /**
     * Get view show animation
     * @return
     */
    public static AnimationSet getViewShowAnimation() {
        int duration = 300;
        int delay = 0;
        AnimationSet set = new AnimationSet(true);

        Animation animation = new AlphaAnimation(0.0f, 1.0f);
        animation.setStartOffset(delay);
        animation.setDuration(duration);
        set.addAnimation(animation);

        return set;
    }

    /**
     * Get view alpha hide animation.
     * @return
     */
    public static AnimationSet getViewAlphaHideAnimation() {
        int duration = 400;
        int delay = 0;
        AnimationSet set = new AnimationSet(true);

        Animation animation = new AlphaAnimation(1.0f, 0.0f);
        animation.setStartOffset(delay);
        animation.setDuration(duration);
        set.addAnimation(animation);
        set.setFillAfter(true);

        return set;
    }

    /**
     * Get view move animation.
     * @param sx
     * @param ex
     * @param sy
     * @param ey
     * @param duration
     * @return
     */
    public static AnimationSet getViewMoveAnimation(float sx, float ex, float sy, float ey, int duration) {
        int delay = 0;
        AnimationSet set = new AnimationSet(true);

        Animation animation = new TranslateAnimation(Animation.RELATIVE_TO_SELF, sx, Animation.RELATIVE_TO_SELF, ex,
                Animation.RELATIVE_TO_SELF, sy, Animation.RELATIVE_TO_SELF, ey);
        animation.setStartOffset(delay);
        animation.setDuration(duration);
        set.addAnimation(animation);
        set.setFillAfter(true);

        return set;
    }

    /**
     * Get view move animation.
     * @param sx
     * @param ex
     * @param sy
     * @param ey
     * @return
     */
    public static AnimationSet getViewMoveAnimation(float sx, float ex, float sy, float ey) {

        return getViewMoveAnimation(sx, ex, sy, ey, 800);
    }
}
