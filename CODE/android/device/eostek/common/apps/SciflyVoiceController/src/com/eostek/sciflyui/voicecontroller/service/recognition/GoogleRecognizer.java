
package com.eostek.sciflyui.voicecontroller.service.recognition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.RemoteException;
import android.speech.RecognitionListener;
import android.speech.RecognizerIntent;
import android.speech.SpeechRecognizer;
import android.util.Log;

import com.eostek.sciflyui.voicecontroller.service.recognition.VoiceRecognitionManager.RecognizerEnum;

/**
 * Google Recognition.
 * 
 * @author youpeng.wan
 * @date 2014-05-29
 */
public class GoogleRecognizer {
    /**
     * type of recognition.
     */
    public final int TYPE = RecognizerEnum.GOOGLE.ordinal();

    private static final String TAG = "GoogleRecognizer";

    // max results of recognition.
    private static final int MAX_RESULTS = 3;

    private SpeechRecognizer mSpeechRecognizer;

    private Context mContext;

    // call-back listener for application.
    private IResultListener mResultListener;

    public GoogleRecognitionListener mGoogleRecognitionListener;

    private static Map<Integer, String> errorCodes = new HashMap<Integer, String>();

    private boolean mEnable = true;

    /**
     * Constructor.
     * 
     * @param context context.
     * @param listener result listener.
     */

    public GoogleRecognizer(Context context, IResultListener listener) {
        this.mContext = context;
        this.mResultListener = listener;
        this.mGoogleRecognitionListener = new GoogleRecognitionListener();

        mSpeechRecognizer = SpeechRecognizer.createSpeechRecognizer(mContext);
        mSpeechRecognizer.setRecognitionListener(mGoogleRecognitionListener);
    }

    /**
     * start recognition.
     * @throws RemoteException 
     */

    public void recognizer() throws RemoteException {
        // Testing Code Begin:
        if (!engineAvailable()) {
            if (mResultListener != null) {
                mResultListener.onError("engine not available.");
            }
            System.out.println("i-fly engine not available.");
            // return;
        }
        disableEngine();
        // Testing Code End.

        Intent intent = new Intent(RecognizerIntent.ACTION_RECOGNIZE_SPEECH);

        // Specify the calling package to identify your application
        intent.putExtra(RecognizerIntent.EXTRA_CALLING_PACKAGE, getClass().getPackage().getName());

        // Display an hint to the user about what he should say.
        intent.putExtra(RecognizerIntent.EXTRA_PROMPT, "Speech recognition demo");

        // Given an hint to the recognizer about what the user is going to say
        intent.putExtra(RecognizerIntent.EXTRA_LANGUAGE_MODEL, RecognizerIntent.LANGUAGE_MODEL_FREE_FORM);

        // return results maximum number
        intent.putExtra(RecognizerIntent.EXTRA_MAX_RESULTS, MAX_RESULTS);

        mSpeechRecognizer.startListening(intent);

    }

    /**
     * stop listsening.
     */
    public void stopListening() {
        if (mSpeechRecognizer != null) {
            print("stopListening");
            mSpeechRecognizer.stopListening();

        }

    }

    /**
     * release recognizer.
     */
    public void destroyRecognizer() {
        if (mSpeechRecognizer != null) {
            print("destroyRecognizer");
            mSpeechRecognizer.destroy();
            enableEngine();
        }

    }

    public boolean engineAvailable() {
        print("enable:" + mEnable);
        return mEnable;
    }

    public class GoogleRecognitionListener implements RecognitionListener {

        public GoogleRecognitionListener() {
            errorCodes.put(1, "Network operation timed out.");
            errorCodes.put(2, "Other network related errors.");
            errorCodes.put(3, "Audio recording error.");
            errorCodes.put(4, "Server sends error status.");
            errorCodes.put(5, "Other client side errors.");
            errorCodes.put(6, "No speech input.");
            errorCodes.put(7, "No recognition result matched.");
            errorCodes.put(8, "RecognitionService busy.");
            errorCodes.put(9, "Insufficient permissions.");
        }

        public void onReadyForSpeech(Bundle params) {
            print("onReadyForSpeech");
        }

        public void onBeginningOfSpeech() {
            print("onBeginningOfSpeech");
            if (mResultListener != null) {
                try {
                    mResultListener.onBeginOfSpeech();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        public void onRmsChanged(float rmsdB) {
            print("onRmsChanged dB= " + rmsdB);
            if (mResultListener != null) {
                try {
                    mResultListener.onVolumeChanged((int) rmsdB);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        public void onBufferReceived(byte[] buffer) {
        }

        public void onEndOfSpeech() {
            print("onEndofSpeech");
            if (mResultListener != null) {
                try {
                    mResultListener.onEndOfSpeech();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            enableEngine();
        }

        public void onError(int error) {

            String reason = errorCodes.get(error);
            print("error:" + reason);

            if (mResultListener != null) {
                try {
                    mResultListener.onError(reason);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            enableEngine();

        }

        public void onResults(Bundle results) {
            print("onResults");
            ArrayList<String> recResults = results.getStringArrayList(SpeechRecognizer.RESULTS_RECOGNITION);

            List<ResultModel> resultModels = new ArrayList<ResultModel>();

            if (mResultListener != null) {

                for (String s : recResults) {
                    ResultModel model = new ResultModel();

                    model.setmMeaning(s);
                    model.setConfidenceScore(-1);

                    resultModels.add(model);
                    print("Google_result:::" + s);

                    break;
                }

                try {
                    mResultListener.onResults(TYPE, resultModels);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }

            }

            enableEngine();

        }

        public void onPartialResults(Bundle partialResults) {
            print("onPartialResults");
        }

        public void onEvent(int eventType, Bundle params) {
            print("onEvent " + eventType);
        }

    }

    private void enableEngine() {
        mEnable = true;
        print("enableEngine.");
    }

    private void disableEngine() {
        mEnable = false;
        print("disableEngine.");
    }

    private void print(String string) {
        if (VoiceRecognitionManager.DEBUG) {
            Log.i(TAG, "google." + string);
        }
    }
}
