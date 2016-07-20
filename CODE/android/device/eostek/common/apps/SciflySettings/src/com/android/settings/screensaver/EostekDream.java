/*
 * Copyright (C) 2012 The Android Open Source Project
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

package com.android.settings.screensaver;

import java.io.File;

import com.android.settings.R;
import com.nineoldandroids.view.animation.AnimatorProxy;

import android.animation.Animator;
import android.animation.Animator.AnimatorListener;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.UserHandle;
import android.service.dreams.DreamService;
import android.text.TextUtils;
import android.util.Log;
import android.widget.FrameLayout;
import android.widget.ImageView;
import static android.view.ViewGroup.LayoutParams.MATCH_PARENT;

public class EostekDream extends DreamService {

    private static final String TAG = EostekDream.class.getSimpleName();

    public ImageView mImageViewOne;

    private String mFilePath = "/system/media/image/natural";

    private String[] mFileList = new String[] {
        ""
    };

    private Bitmap mBitmap;

    final AnimatorSet mAnimatorSet = new AnimatorSet();

    int mCurIndex = 0;

    @Override
    public void onDreamingStarted() {
        final FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(MATCH_PARENT, MATCH_PARENT);
        mImageViewOne = new ImageView(this);
        mImageViewOne.setLayoutParams(lp);

        initImageFiles();
        setImage(mCurIndex);
        setContentView(mImageViewOne);

        startAnimation();
        mAnimatorSet.addListener(new AnimatorListener() {

            @Override
            public void onAnimationStart(Animator arg0) {

            }

            @Override
            public void onAnimationRepeat(Animator arg0) {
                
            }

            @Override
            public void onAnimationEnd(Animator arg0) {
                //Log.v(TAG, "onAnimationEnd");
                // repeat the animation and change picture every time
                mCurIndex++;
                startAnimation();
            }

            @Override
            public void onAnimationCancel(Animator arg0) {
                Log.v(TAG, "onAnimationEnd");
            }
        });

    }

    @Override
    public void onDreamingStopped() {
        super.onDreamingStopped();
        Log.v(TAG, "onDreamingStopped");
        mAnimatorSet.cancel();
        mAnimatorSet.removeAllListeners();
        if (mBitmap != null) {
            mBitmap.recycle();
        }
    }

    @Override
    public void onWindowFocusChanged(boolean hasFocus) {
        super.onWindowFocusChanged(hasFocus);
        if (!hasFocus) {
            Intent mDreamingStoppedIntent = new Intent(Intent.ACTION_DREAMING_STOPPED);
            mDreamingStoppedIntent.addFlags(Intent.FLAG_RECEIVER_REGISTERED_ONLY);
            sendBroadcastAsUser(mDreamingStoppedIntent, UserHandle.ALL);
            Log.v(TAG, "stop dream when lost focus");
        }
    }

    private void startAnimation() {
        setImage(mCurIndex);

        final int index = mCurIndex % 4;
        switch (index) {
            case 0:
                mAnimatorSet.playTogether(ObjectAnimator.ofFloat(mImageViewOne, "scaleX", 1.5f, 1f),
                        ObjectAnimator.ofFloat(mImageViewOne, "scaleY", 1.5f, 1f));
                break;
            case 1:
                mAnimatorSet.playTogether(ObjectAnimator.ofFloat(mImageViewOne, "scaleX", 1, 1.5f),
                        ObjectAnimator.ofFloat(mImageViewOne, "scaleY", 1, 1.5f));
                break;
            case 2:
                AnimatorProxy.wrap(mImageViewOne).setScaleX(1.5f);
                AnimatorProxy.wrap(mImageViewOne).setScaleY(1.5f);
                mAnimatorSet.playTogether(ObjectAnimator.ofFloat(mImageViewOne, "translationY", 80f, 0f));
                break;
            case 3:
                AnimatorProxy.wrap(mImageViewOne).setScaleX(1.5f);
                AnimatorProxy.wrap(mImageViewOne).setScaleY(1.5f);
                mAnimatorSet.playTogether(ObjectAnimator.ofFloat(mImageViewOne, "translationX", 0f, 40f));
                break;
            default:
                break;
        }

        mAnimatorSet.setDuration(10000);
        mAnimatorSet.start();

    }

    public void initImageFiles() {
        File file = new File(mFilePath);
        if (file.isDirectory()) {
            mFileList = file.list();
        } else {
            Log.e(TAG, "the path should be a directory!");
        }
    }

    private Bitmap getBitmap(int index) {
        String path = mFilePath + File.separator + mFileList[index];
        Log.v(TAG, path);
        if (TextUtils.isEmpty(mFileList[index]) || !new File(path).exists()) {
            Log.e(TAG, "picture do not exists");
            return null;
        }
        mBitmap = BitmapFactory.decodeFile(path);
        return mBitmap;
    }

    private void setImage(int index) {
        //mRecycleBitmap = mBitmap;
        mImageViewOne.setImageBitmap(null);
        // recycle the former picture
        if (mBitmap != null) {
            mBitmap.recycle();
            mBitmap = null;
            System.gc();
        }
        mBitmap = getBitmap(index % mFileList.length);
        if (mBitmap == null) {
            mImageViewOne.setImageResource(R.drawable.default_screensaver);
        } else {
            mImageViewOne.setImageBitmap(mBitmap);
        }

    }

}
