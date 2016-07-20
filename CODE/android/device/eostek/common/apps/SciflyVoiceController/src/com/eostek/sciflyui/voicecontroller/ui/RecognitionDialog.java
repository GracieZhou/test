
package com.eostek.sciflyui.voicecontroller.ui;

import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.ViewGroup.LayoutParams;
import android.view.Window;
import android.view.WindowManager;

import com.eostek.sciflyui.voicecontroller.R;
import com.eostek.sciflyui.voicecontroller.ui.RecognitionDialogListener.IRecognizerCallBack;

public class RecognitionDialog extends AlertDialog {

    Context mContext;

    RecognitionDialogHolder mHolder;

    RecognitionDialogListener mListener;

    private IRecognizerCallBack mRecognizerCallback;

    public RecognitionDialogHolder getHolder() {
        return mHolder;
    }

    // public RecognitionDialogListener getListener() {
    // Log.i("VoiceControllerBinder", "mListener:" + mListener);
    // return mListener;
    // }

    public RecognitionDialog(Context context) {
        super(context);
        this.mContext = context;

        Log.e("VoiceControllerBinder", "RecognitionDialog Constructor");

    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.recognizer);

        mHolder = new RecognitionDialogHolder(this);
        mHolder.findViews();

        Log.e("VoiceControllerBinder", "RecognitionDialog onCreate.");
        mListener = new RecognitionDialogListener(this, mHolder);

        Window window = getWindow();
        WindowManager.LayoutParams p = window.getAttributes();
        p.width = LayoutParams.MATCH_PARENT;
        p.height = LayoutParams.MATCH_PARENT;
        window.setAttributes(p);

    }

    public void handleError(String error) {
        Log.i("handleError", "handleError");
        mListener.handleError(error);
    }

    @Override
    public void onBackPressed() {
        if (this != null && this.isShowing()) {
            this.dismiss();
        }
    }

    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        return super.onKeyDown(keyCode, event);
    }

    public void setRecognizerCallback(IRecognizerCallBack recognizerCallback) {
        Log.i("VoiceControllerBinder", "setRecognizerCallBack." + recognizerCallback);
        this.mRecognizerCallback = recognizerCallback;
    }

    protected void onStartRecognizerAagin() {

        if (mRecognizerCallback == null) {
            Log.e("onStartRecognizerAagin", "mRecognizerCallback null.");
            return;
        }
        mRecognizerCallback.onStartRecognizerAagin();
    }

    protected void onStopRecognizer() {
        if (mRecognizerCallback == null) {
            return;
        }
        mRecognizerCallback.onStopRecognizer();
    }
}
