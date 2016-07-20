
package com.eostek.sciflyui.voicecontroller.ui;

import android.util.Log;
import android.view.View;

public class RecognitionDialogListener {

    RecognitionDialogHolder mHolder;

    public MicImageClickListener mMicImageClickListener = new MicImageClickListener();

    public TryAgainClickListener mTryAgainClickListener = new TryAgainClickListener();

    RecognitionDialog mRecognitionDialog;

    public RecognitionDialogListener(RecognitionDialog recognitionDialog, RecognitionDialogHolder holder) {
        mRecognitionDialog = recognitionDialog;
        mHolder = holder;
        setListener();
    }

    private void setListener() {
        mHolder.mMicImage.setOnClickListener(mMicImageClickListener);
        mHolder.mTryAgainBtn.setOnClickListener(mTryAgainClickListener);
    }

    public void VoiceAnimationByVolume(int v) {
        if (mHolder.mAnimation != null && mHolder.mAnimation.isRunning()) {
            mHolder.mAnimation.selectDrawable(v);
        }
    }

    public void handleError(String error) {
        if (mRecognitionDialog == null) {
            return;
        }
        if (mHolder.mAnimation.isRunning()) {
            mHolder.mAnimation.stop();
            mHolder.mAnimation.selectDrawable(0);
        }
        mHolder.mRecognitionErrorLayout.setVisibility(View.VISIBLE);
        mHolder.mRecognitionLayout.setVisibility(View.GONE);
        mHolder.mErrorTip.setText(error);

    }

    public class MicImageClickListener implements View.OnClickListener {
        public void onClick(View v) {
            if (mRecognitionDialog == null) {
                Log.e("VoiceControllerBinder", "Dialog is null.");
                return;
            }
            mRecognitionDialog.onStopRecognizer();
        }
    }

    public class TryAgainClickListener implements View.OnClickListener {
        public void onClick(View v) {

            // mHolder.animation.start();

            if (mRecognitionDialog == null) {
                Log.e("VoiceControllerBinder", "Dialog is null.");
                return;
            }
            mHolder.mRecognitionLayout.setVisibility(View.VISIBLE);
            mHolder.mRecognitionErrorLayout.setVisibility(View.GONE);

            mRecognitionDialog.onStartRecognizerAagin();
        }
    }

    public interface IRecognizerCallBack {
        void onStartRecognizerAagin();

        void onStopRecognizer();
    }

}
