
package com.eostek.sciflyui.voicecontroller.service.semantic;

import java.util.List;
import java.util.Timer;
import java.util.TimerTask;

import android.content.ActivityNotFoundException;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.DialogInterface.OnCancelListener;
import android.content.DialogInterface.OnDismissListener;
import android.os.Bundle;
import android.os.Handler;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.RemoteException;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.view.WindowManager;

import com.eostek.sciflyui.voicecontroller.command.Command;
import com.eostek.sciflyui.voicecontroller.command.CommandDispatcher;
import com.eostek.sciflyui.voicecontroller.command.VoiceCommander;
import com.eostek.sciflyui.voicecontroller.service.recognition.IResultListener;
import com.eostek.sciflyui.voicecontroller.service.recognition.ResultModel;
import com.eostek.sciflyui.voicecontroller.service.recognition.VoiceRecognitionManager;
import com.eostek.sciflyui.voicecontroller.service.recognition.VoiceRecognitionManager.RecognizerEnum;
import com.eostek.sciflyui.voicecontroller.tool.LanguageUtil;
import com.eostek.sciflyui.voicecontroller.tool.TextSemanticUnderstander;
import com.eostek.sciflyui.voicecontroller.tool.VoiceSemanticUnderstander;
import com.eostek.sciflyui.voicecontroller.ui.RecognitionDialog;
import com.eostek.sciflyui.voicecontroller.ui.RecognitionDialogListener.IRecognizerCallBack;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechUnderstanderListener;
import com.iflytek.cloud.TextUnderstanderListener;
import com.iflytek.cloud.UnderstanderResult;

public class VoiceSemanticManager extends IVoiceSemanticManager.Stub {

    private static final String TAG = "VoiceSemanticManager";

    private Context mContext;

    private VoiceRecognitionManager mVoiceRecognitionManager;

    private VoiceSemanticUnderstander mVoiceSemanticUnderstander;

    private TextSemanticUnderstander mTextSemanticUnderstander;

    private IStatusListener mIStatusListener;

    private RecognitionDialog mRecDialog;

    private VoiceCommander mVoiceCommander;

    public static String SERARCH_EXTRA = "search";

    private static final int RECOGNITION_MIC_DISABLED = 0;

    private static final int RECOGNITION_FAILED = 1;

    private static final int RECOGNITION_SUCCESS = 2;

    private static final int RECOGNITION_STOP_RECOGNIZER = 3;

    private static final int RECOGNITION_TIME_OUT = 4;

    private static final int RECOGNITION_VOICE_1 = 5;

    private static final int RECOGNITION_VOICE_2 = 6;

    private static final int RECOGNITION_DESTROY = 7;

    private static final int RECOGNITION_CHANGE_VOLUME = 8;

    private static final int RECOGNITION_SUCCESS_GOOGLE = 9;

    // 10 seconds wait for result
    private static final int RESULT_TIME_OUT = 10 * 1000;

    Timer timer = new Timer();

    /**
     * Class used to handle semantic recognition messages.
     */
    class SemanticManagerHandler extends Handler {

        public SemanticManagerHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {

            switch (msg.what) {
                case RECOGNITION_STOP_RECOGNIZER:
                case RECOGNITION_MIC_DISABLED:
                case RECOGNITION_TIME_OUT:
                case RECOGNITION_FAILED:

                    if (mVoiceSemanticUnderstander != null) {
                        mVoiceSemanticUnderstander.destroy();
                    }

                    if (mTextSemanticUnderstander != null) {
                        mTextSemanticUnderstander.destroy();
                    }

                    if (mVoiceRecognitionManager != null) {
                        mVoiceRecognitionManager.destroyRecognizer();
                    }

                    mRecDialog.handleError((String) msg.obj);
                    if (mIStatusListener != null) {
                        try {
                            Log.i(TAG, "mIStatusListener " + (String) msg.obj);
                            mIStatusListener.statusCallBack(false, (String) msg.obj);
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }
                    break;
                case RECOGNITION_DESTROY:
                    if (mVoiceSemanticUnderstander != null) {
                        mVoiceSemanticUnderstander.destroy();
                    }

                    if (mTextSemanticUnderstander != null) {
                        mTextSemanticUnderstander.destroy();
                    }
                    break;

                case RECOGNITION_SUCCESS:

                    if (mVoiceCommander == null) {
                        mVoiceCommander = new VoiceCommander(mContext);
                    }

                    String result = (String) msg.obj;

                    try {
                        if (isRecDialogVisible()) {
                            mRecDialog.dismiss();
                            mRecDialog = null;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    Log.i(TAG, "RECOGNITION_SUCCESS.result:" + result);
                    Command command = mVoiceCommander.processSemanticResult(result);

                    if (mVoiceSemanticUnderstander != null) {
                        mVoiceSemanticUnderstander.destroy();
                    }

                    if (mTextSemanticUnderstander != null) {
                        mTextSemanticUnderstander.destroy();
                    }

                    if (mIStatusListener != null) {
                        try {
                            Log.i(TAG, "mIStatusListener " + command.toString());
                            mIStatusListener.statusCallBack(false, command.toString());
                        } catch (RemoteException e) {
                            e.printStackTrace();
                        }
                    }

                    break;
                case RECOGNITION_CHANGE_VOLUME:
                    changeVolume(msg.arg1);
                    break;
                case RECOGNITION_VOICE_1:
                    break;
                case RECOGNITION_VOICE_2:
                    break;
                case RECOGNITION_SUCCESS_GOOGLE:
                    String result1 = (String) msg.obj;
                    Log.i(TAG, "result= " + result1);

                    try {
                        if (isRecDialogVisible()) {
                            mRecDialog.dismiss();
                            mRecDialog = null;
                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }

                    Command command2 = new Command();
                    command2.setText(result1);
                    CommandDispatcher dispatcher = new CommandDispatcher(mContext);
                    dispatcher.dispatchLaunchVideo(command2);

                    if (mVoiceRecognitionManager != null) {
                        mVoiceRecognitionManager.destroyRecognizer();
                    }
                    break;
            }
            if (mRecDialog != null) {
                mRecDialog.getHolder().getWaittingResultPB().setVisibility(View.GONE);
            }
        }
    }

    private Handler mHandler;

    private Runnable showDialog = new Runnable() {

        @Override
        public void run() {

            mRecDialog = new RecognitionDialog(mContext);

            RecognizerEnum recEnum = LanguageUtil.getLanguageEnum();
            switch (recEnum.ordinal()) {
                case 1:
                    // Google
                    mRecDialog.setRecognizerCallback(new IGoogleRecogizerCallback());
                    break;
                case 2:
                    // Ifly
                    mRecDialog.setRecognizerCallback(new IFlyRecognizerCallback());
                    break;
            }

            mRecDialog.setOnDismissListener(new RecognitionDialogDismissListener());
            mRecDialog.setOnCancelListener(new RecognitionDialogCancelListener());

            mRecDialog.getWindow().setType((WindowManager.LayoutParams.TYPE_SYSTEM_ALERT));

            mRecDialog.show();
            mHandler.sendEmptyMessageDelayed(RECOGNITION_VOICE_1, 100);
        }
    };

    @Override
    public void setPropVisible(boolean visible) throws RemoteException {
        Log.i(TAG, "setPropVisible " + visible);

        if (visible) {

            if (isRecDialogVisible()) {
                return;
            }

            mHandler.post(showDialog);
        } else {
            if (mRecDialog != null) {
                mRecDialog.dismiss();
            }
        }
    }

    /**
     * VoiceSemanticManager constructor.
     * 
     * @param mContext Set Context.
     */
    public VoiceSemanticManager(Context mContext, VoiceRecognitionManager voiceRecognitionManager) {
        this.mContext = mContext;
        mHandler = new SemanticManagerHandler(mContext.getMainLooper());
        mVoiceRecognitionManager = voiceRecognitionManager;
    }

    @Override
    public void setCommand(String command) throws RemoteException {
        Log.i(TAG, "setCommand:" + command);

        if (TextUtils.isEmpty(command)) {
            if (mIStatusListener != null) {
                mIStatusListener.statusCallBack(false, "Command is null.");
            }
        } else {

            if (mTextSemanticUnderstander == null) {
                mTextSemanticUnderstander = new TextSemanticUnderstander(mContext);
            }

            command = command.replace("&quot;", "\\\"");

            mTextSemanticUnderstander.startTextUnderstand(command, textListener);

        }

    }

    @Override
    public void setStatusListener(IStatusListener listener) throws RemoteException {
        mIStatusListener = listener;
    }

    @Override
    public void setVolume(int volume) throws RemoteException {
        if (isRecDialogVisible()) {
            Log.i(TAG, "setVolume " + volume);
            Message msg = mHandler.obtainMessage();
            msg.what = RECOGNITION_CHANGE_VOLUME;
            msg.arg1 = volume;
            mHandler.sendMessage(msg);
        } else {
            Log.e(TAG, "setVolume " + volume);
        }
    }

    private boolean isRecDialogVisible() {

        if (mRecDialog != null && mRecDialog.isShowing()) {
            return true;
        }

        return false;
    }

    /**
     * start voice control with UI
     * 
     * @param context set Context
     */
    public void startRecognitionWithUI(Context context) {
        Log.i(TAG, "startRecognitionWithUI");

        if (mContext == null) {
            mContext = context;
        }

        try {
            setPropVisible(true);
        } catch (RemoteException e) {
            e.printStackTrace();
        }

        RecognizerEnum recEnum = LanguageUtil.getLanguageEnum();
        switch (recEnum.ordinal()) {
            case 1:// google
                Log.i(TAG, "Google recognizer available yet.");

                if (isRecDialogVisible()) {
                    return;
                }

                mRecDialog = new RecognitionDialog(mContext);

                mRecDialog.setRecognizerCallback(new IGoogleRecogizerCallback());

                mRecDialog.setOnDismissListener(new RecognitionDialogDismissListener());
                mRecDialog.setOnCancelListener(new RecognitionDialogCancelListener());
                try {
                    mVoiceRecognitionManager.recognize(mGoogleRecognizerListener);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
                break;
            case 2:// ifly
                Log.i(TAG, "set IFLY recognizer callback 1");

                if (mVoiceSemanticUnderstander == null) {
                    mVoiceSemanticUnderstander = new VoiceSemanticUnderstander(mContext);
                }

                if (isRecDialogVisible()) {
                    return;
                }

                mRecDialog = new RecognitionDialog(mContext);

                mRecDialog.setRecognizerCallback(new IFlyRecognizerCallback());

                mRecDialog.setOnDismissListener(new RecognitionDialogDismissListener());
                mRecDialog.setOnCancelListener(new RecognitionDialogCancelListener());
                mVoiceSemanticUnderstander.startVoiceUnderstand(mSpeechUnderstanderListener);

                break;
        }

    }

    private SpeechUnderstanderListener mSpeechUnderstanderListener = new SpeechUnderstanderListener() {

        @Override
        public void onVolumeChanged(int v) {
            changeVolume(v);
        }

        @Override
        public void onResult(UnderstanderResult result) {
            Log.i(TAG, "speechUnderstander.result:" + result.getResultString());
            timer.cancel();

            Message msg = new Message();
            msg.obj = "" + result.getResultString().replace("&quot;", "\\\"");
            msg.what = RECOGNITION_SUCCESS;
            mHandler.sendMessage(msg);
        }

        @Override
        public void onEvent(int arg0, int arg1, int arg2, Bundle arg3) {
        }

        @Override
        public void onError(SpeechError speechError) {
            String error = speechError.getErrorDescription() + "(error code : " + speechError.getErrorCode() + ")";
            Log.i(TAG, "error:" + error);
            // error happened,time out timer should be canceled
            timer.cancel();

            Message msg = new Message();
            msg.obj = new String(error);
            msg.what = RECOGNITION_FAILED;
            mHandler.sendMessage(msg);
        }

        @Override
        public void onEndOfSpeech() {

            timer = new Timer();
            timer.schedule(new TimerTask() {
                public void run() {
                    Message msg = new Message();
                    msg.obj = new String("Time out");
                    msg.what = RECOGNITION_TIME_OUT;
                    mHandler.sendMessage(msg);
                }
            }, RESULT_TIME_OUT);

        }

        @Override
        public void onBeginOfSpeech() {
        }

    };

    private void changeVolume(int v) {
        int volume = 0;
        if (v <= 0) {
            volume = 0;
        } else if (v > 0 && v <= 10) {
            volume = 1;
        } else if (v > 10 && v <= 20) {
            volume = 2;
        } else if (v > 20) {
            volume = 3;
        }

        Log.i(TAG, "changeVolume volume " + volume);
        if (mRecDialog != null) {
            if (mRecDialog.getHolder() != null && mRecDialog.getHolder().getAnimation() != null) {
                mRecDialog.getHolder().getAnimation().selectDrawable(volume);
            }
        }
    }

    private class IFlyRecognizerCallback implements IRecognizerCallBack {
        public void onStartRecognizerAagin() {
            Log.i(TAG, "IFly start again");
            if (mVoiceSemanticUnderstander != null) {
                mVoiceSemanticUnderstander.startVoiceUnderstand(mSpeechUnderstanderListener);
            }

        }

        public void onStopRecognizer() {
            Log.i(TAG, "onStopRecognizer");
            Message msg = new Message();
            msg.obj = "recognition stop by user";
            msg.what = RECOGNITION_STOP_RECOGNIZER;
            mHandler.sendMessage(msg);
        }
    }

    private IResultListener mGoogleRecognizerListener = new IResultListener() {
        @Override
        public IBinder asBinder() {
            return null;
        }

        @Override
        public void onBeginOfSpeech() throws RemoteException {
        }

        @Override
        public void onVolumeChanged(int v) throws RemoteException {
            changeVolume(v);
        }

        @Override
        public void micDisabled() throws RemoteException {
        }

        @Override
        public void onEndOfSpeech() throws RemoteException {
            timer = new Timer();
            timer.schedule(new TimerTask() {
                public void run() {
                    Message msg = new Message();
                    msg.obj = new String("Time out");
                    msg.what = RECOGNITION_TIME_OUT;
                    mHandler.sendMessage(msg);
                }
            }, RESULT_TIME_OUT);
        }

        @Override
        public void onError(String errString) throws RemoteException {
            Log.i(TAG, "error:" + errString);
            // error happened,time out timer should be canceled
            timer.cancel();

            Message msg = new Message();
            msg.obj = new String(errString);
            msg.what = RECOGNITION_FAILED;
            mHandler.sendMessage(msg);
        }

        @Override
        public void onResults(int recognizerType, List<ResultModel> result) throws RemoteException {
            Log.i(TAG, "查询结果=" + result.size());
            timer.cancel();

            Message msg = new Message();
            msg.obj = "" + result.get(0).getmMeaning();
            msg.what = RECOGNITION_SUCCESS_GOOGLE;
            mHandler.sendMessage(msg);
        }
    };

    private class IGoogleRecogizerCallback implements IRecognizerCallBack {

        public void onStartRecognizerAagin() {
            Log.i(TAG, "Google start again");
            if (mVoiceRecognitionManager != null) {
                try {
                    mVoiceRecognitionManager.recognize(mGoogleRecognizerListener);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

        }

        public void onStopRecognizer() {
            Log.i(TAG, "onStopRecognizer");
            Message msg = new Message();
            msg.obj = "recognition stop by user";
            msg.what = RECOGNITION_STOP_RECOGNIZER;
            mHandler.sendMessage(msg);
        }

    }

    private class RecognitionDialogDismissListener implements OnDismissListener {

        @Override
        public void onDismiss(DialogInterface arg0) {
            Log.i(TAG, "dialog onDismiss");
            mHandler.sendEmptyMessage(RECOGNITION_DESTROY);
        }

    }

    private class RecognitionDialogCancelListener implements OnCancelListener {

        @Override
        public void onCancel(DialogInterface arg0) {
            Log.i(TAG, "dialog onCancel");
            mHandler.sendEmptyMessage(RECOGNITION_DESTROY);
        }

    }

    private TextUnderstanderListener textListener = new TextUnderstanderListener() {
        @Override
        public void onResult(UnderstanderResult result) {
            Log.i(TAG, "TextUnderstander.onResult:" + result.getResultString());

            Message msg = new Message();
            msg.obj = "" + result.getResultString().replace("&quot;", "\\\"");
            Log.i(TAG, "TextUnderstander.onResult.aft: ->" + msg.obj);

            msg.what = RECOGNITION_SUCCESS;
            mHandler.sendMessage(msg);

        }

        @Override
        public void onError(SpeechError speechError) {

            String error = speechError.getErrorDescription() + "(error code : " + speechError.getErrorCode() + ")";
            Log.i(TAG, "" + error);

            Message msg = new Message();
            msg.obj = new String(error);
            msg.what = RECOGNITION_FAILED;
            mHandler.sendMessage(msg);

        }
    };

}
