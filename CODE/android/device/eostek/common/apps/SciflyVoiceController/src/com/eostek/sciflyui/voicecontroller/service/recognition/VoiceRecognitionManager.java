
package com.eostek.sciflyui.voicecontroller.service.recognition;

import java.util.Locale;

import android.content.Context;
import android.os.RemoteException;
import android.util.Log;

/**
 * Voice Recognition Manager.
 * 
 * @author Youpeng
 * @date 2014-05-29 TODO:add a interface that get recognizer's status.
 */
public class VoiceRecognitionManager extends IVoiceRecognitionManager.Stub {
    public static final String TAG = "VoiceRecognitionManager";

    public static final boolean DEBUG = true;

    private Context mContext;

    public enum RecognizerEnum {
        DEFAULT, GOOGLE, IFLY;

        public static RecognizerEnum valueOf(int ordinal) {
            if (ordinal < 0 || ordinal >= values().length) {
                throw new IndexOutOfBoundsException("Invalid ordinal");
            }
            return values()[ordinal];
        }
    }

    private FlyTekRecognizer mFlyTekRecognizer;

    private GoogleRecognizer mGoogleRecognizer;

    public VoiceRecognitionManager(Context context) {
        mContext = context;
    }

    /**
     * recognize.
     * 
     * @param context context of app.
     * @param type google or ifly.
     * @param listener call-back.
     */
    public void recognize(Context context, RecognizerEnum type, IResultListener listener) {

        print("recognize().type:" + type);

        // Testing Code Begin:
        if (mGoogleRecognizer != null) {
            if (!mGoogleRecognizer.engineAvailable()) {
                // listener.onError("googleEngine.busy.");
                System.out.println("googleEngine.busy.");
                // return;
            }
        }

        if (mFlyTekRecognizer != null) {
            if (!mFlyTekRecognizer.engineAvailable()) {
                // listener.onError("iflyEngine.busy.");
                System.out.println("iflyEngine.busy.");
                // return;
            }
        }
        // Testing Code End.

        try {
            if (type.equals(RecognizerEnum.GOOGLE)) {
                mGoogleRecognizer = new GoogleRecognizer(context, listener);
                mGoogleRecognizer.recognizer();

            } else if (type.equals(RecognizerEnum.IFLY)) {
                // ifly recognize.
                mFlyTekRecognizer = new FlyTekRecognizer(context, listener);
                mFlyTekRecognizer.recognizer();

            } else {
                // default recognizer.
                recognize(context, getLanguageEnum(), listener);
            }
        } catch (Throwable error) {
            error.printStackTrace();
            try {
                listener.onError(error.getMessage());
            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }

    }

    /**
     * recognize with default recognizer.
     * 
     * @param context context of application.
     * @param listener call-back
     */
    public void recognize(Context context, IResultListener listener) {
        recognize(context, getLanguageEnum(), listener);
    }

    /**
     * stop listening.
     */
    public void stopListening() {
        if (mFlyTekRecognizer != null) {
            mFlyTekRecognizer.stopListening();
        }

        if (mGoogleRecognizer != null) {
            mGoogleRecognizer.stopListening();
        }
    }

    /**
     * destroy recognizer and release resources.
     */
    public void destroyRecognizer() {

        if (mFlyTekRecognizer != null) {
            mFlyTekRecognizer.destroyRecognizer();
        }

        if (mGoogleRecognizer != null) {
            mGoogleRecognizer.destroyRecognizer();
        }
    }

    private String getLanguageEnv() {
        Locale locale = Locale.getDefault();
        return locale.getLanguage();
    }

    private RecognizerEnum getLanguageEnum() {
        String language = getLanguageEnv();
        if ("zh".equals(language)) {
            return RecognizerEnum.IFLY;
        }
        return RecognizerEnum.GOOGLE;
    }

    private static void print(String log) {
        if (VoiceRecognitionManager.DEBUG) {
            Log.i(TAG, log);
        }
    }

    @Override
    public void recognize(IResultListener listener) throws RemoteException {
        recognize(mContext, getLanguageEnum(), listener);

    }
}
