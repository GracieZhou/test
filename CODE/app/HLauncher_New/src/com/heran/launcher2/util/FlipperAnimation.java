/**
 * 
 */
package com.heran.launcher2.util;

import android.animation.Animator;
import android.animation.AnimatorInflater;
import android.animation.ObjectAnimator;
import android.animation.Animator.AnimatorListener;
import android.content.Context;
import android.view.animation.AnimationUtils;
import android.widget.ViewFlipper;

import com.heran.launcher2.R;

/*
 * projectName： EosLauncher
 * moduleName： FlipperAnimation.java
 * @author chadm.xiang
 * @version 1.0.0
 * @time  2014-1-23 上午10:46:16
 * @Copyright © 2013 Eos Inc.
 */

public class FlipperAnimation {
    
    private Context mContext;
    
    private ViewFlipper mFlipper;
    
    public FlipperAnimation(Context context,ViewFlipper flipper){
        this.mContext = context;
        this.mFlipper = flipper;
    }
    
    /**
     * change the viewflipper animation every time called
     * 
     * @param num
     */
    public void loadFlipperAnimation(int num) {
        switch (num) {
            case Constants.FADE_IN_FADE_OUT:
                //FADE_IN_FADE_OUT
                mFlipper.setInAnimation(AnimationUtils.loadAnimation(mContext, R.anim.photo_fade_enter));
                mFlipper.setOutAnimation(AnimationUtils.loadAnimation(mContext, R.anim.photo_fade_exit));
                mFlipper.showNext();
                break;
            case Constants.LETF_SHIFT:
                //LETF_SHIFT
                mFlipper.setInAnimation(AnimationUtils.loadAnimation(mContext, R.anim.push_left_in));
                mFlipper.setOutAnimation(AnimationUtils.loadAnimation(mContext, R.anim.push_left_out));
                mFlipper.showNext();
                break;
            case Constants.SLIDE_UP:
                //SLIDE_UP
                mFlipper.setInAnimation(AnimationUtils.loadAnimation(mContext, R.anim.push_up_in));
                mFlipper.setOutAnimation(AnimationUtils.loadAnimation(mContext, R.anim.push_up_out));
                mFlipper.showNext();
                break;
            case Constants.ZOOM_IN_OUT:
                //ZOOM_IN_OUT
                mFlipper.setInAnimation(AnimationUtils.loadAnimation(mContext, R.anim.scale_in));
                mFlipper.setOutAnimation(AnimationUtils.loadAnimation(mContext, R.anim.scale_out));
                mFlipper.showNext();
                break;
            case Constants.ROTATION_RIGHT:
                //ROTATION_RIGHT
                mFlipper.setInAnimation(AnimationUtils.loadAnimation(mContext, R.anim.rotaion_left_in));
                mFlipper.setOutAnimation(AnimationUtils.loadAnimation(mContext, R.anim.rotation_right_out));
                mFlipper.showNext();
                break;
            case Constants.FLIP_HORIZONTAL:
                //Flip Horizontal
                mFlipper.setInAnimation(null);
                mFlipper.setOutAnimation(null);
                ObjectAnimator out = (ObjectAnimator) AnimatorInflater.loadAnimator(mContext, R.anim.rotation_out);
                out.setTarget(mFlipper);
                out.start();
                final ObjectAnimator in = (ObjectAnimator) AnimatorInflater.loadAnimator(mContext, R.anim.rotation_in);
                out.addListener(new AnimatorListener() {
                    @Override
                    public void onAnimationStart(Animator animation) {
                    }

                    @Override
                    public void onAnimationRepeat(Animator animation) {
                    }

                    @Override
                    public void onAnimationEnd(Animator animation) {
                        mFlipper.showNext();
                        in.setTarget(mFlipper);
                        in.start();
                    }

                    @Override
                    public void onAnimationCancel(Animator animation) {
                    }
                });
                break;
            default:
                break;
        }
    }

}
