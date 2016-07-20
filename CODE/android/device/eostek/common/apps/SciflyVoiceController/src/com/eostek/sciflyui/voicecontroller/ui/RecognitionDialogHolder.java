
package com.eostek.sciflyui.voicecontroller.ui;

import android.app.Dialog;
import android.graphics.drawable.AnimationDrawable;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.eostek.sciflyui.voicecontroller.R;

public class RecognitionDialogHolder {

    Dialog mRecDialog;

    AnimationDrawable mAnimation;

    AnimationDrawable mAnimations[];

    ImageView mMicImage;

    Button mTryAgainBtn;

    LinearLayout mRecognitionErrorLayout;

    LinearLayout mRecognitionLayout;

    TextView mErrorTip;

    ProgressBar mWaittingResultPB;

    public ProgressBar getWaittingResultPB() {
        return mWaittingResultPB;
    }

    public AnimationDrawable getAnimation() {
        return mAnimation;
    }

    public RecognitionDialogHolder(Dialog dialog) {
        mRecDialog = dialog;
    }

    public void findViews() {
        mMicImage = (ImageView) mRecDialog.findViewById(R.id.mic_bg);
        mMicImage.setBackgroundResource(R.anim.recognizer_mic);

        mAnimation = (AnimationDrawable) mMicImage.getBackground();
        mAnimation.setOneShot(false);
        // animation.start();// start automaticly

        mTryAgainBtn = (Button) mRecDialog.findViewById(R.id.rec_try_again);
        mTryAgainBtn.requestFocus();
        mRecognitionErrorLayout = (LinearLayout) mRecDialog.findViewById(R.id.rec_error_LLayout);
        mRecognitionLayout = (LinearLayout) mRecDialog.findViewById(R.id.rec_LLayout);

        mErrorTip = (TextView) mRecDialog.findViewById(R.id.error_tip2);

        mWaittingResultPB = (ProgressBar) mRecDialog.findViewById(R.id.progressBar1);
    }

}
