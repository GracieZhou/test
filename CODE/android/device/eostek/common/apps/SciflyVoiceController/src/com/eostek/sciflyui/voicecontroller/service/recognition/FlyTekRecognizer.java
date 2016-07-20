
package com.eostek.sciflyui.voicecontroller.service.recognition;

import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;
import org.json.JSONTokener;

import android.content.Context;
import android.os.Bundle;
import android.os.RemoteException;
import android.util.Log;

import com.eostek.sciflyui.voicecontroller.service.recognition.VoiceRecognitionManager.RecognizerEnum;
import com.iflytek.cloud.RecognizerListener;
import com.iflytek.cloud.RecognizerResult;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechError;
import com.iflytek.cloud.SpeechRecognizer;
import com.iflytek.cloud.SpeechUtility;

/**
 * fly tek recognition.
 * 
 * @author youpeng.wan
 * @date 2014-05-29
 */
public class FlyTekRecognizer {

    /**
     * type of recognition.
     */
    public static final int TYPE = RecognizerEnum.IFLY.ordinal();

    private static final String TAG = "FlyTekRecognizer";

    private Context mContext;

    private SpeechRecognizer mIFlySpeechRecognizer;

    private IResultListener mResultListener;

    private boolean mEnable = true;

    /**
     * listener of recognition.
     */
    private RecognizerListener mRecoListener = new RecognizerListener() {

        @Override
        public void onVolumeChanged(int voice) {
            print("onVolumeChanged：" + voice);
            if (mResultListener != null) {
                try {
                    mResultListener.onVolumeChanged(voice);
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }

        @Override
        public void onResult(RecognizerResult result, boolean arg1) {
            print("recognizer result：" + result.getResultString());
            if (mResultListener != null) {
                try {
                    mResultListener.onResults(TYPE, parseIatResult(result.getResultString()));
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            enableEngine();
        }

        @Override
        public void onEvent(int arg0, int arg1, int arg2, Bundle arg3) {
        }

        @Override
        public void onError(SpeechError speechError) {
            print("onError Code：" + speechError.getErrorCode());
            if (mResultListener != null) {
                try {
                    mResultListener.onError(speechError.getPlainDescription(true));
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            enableEngine();
        }

        @Override
        public void onEndOfSpeech() {
            print("onEndOfSpeech");
            if (mResultListener != null) {
                try {
                    mResultListener.onEndOfSpeech();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }

            enableEngine();
        }

        @Override
        public void onBeginOfSpeech() {
            print("onBeginOfSpeech");
            if (mResultListener != null) {
                try {
                    mResultListener.onBeginOfSpeech();
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
        }
    };

    /**
     * Constructor.
     * 
     * @param context context.
     * @param listener result listener.
     */
    public FlyTekRecognizer(Context context, IResultListener listener) {
        this.mContext = context;
        this.mResultListener = listener;

        try {
            SpeechUtility.createUtility(mContext, SpeechConstant.APPID + "=5097a430");

            // 1.create SpeechRecognizer.
            mIFlySpeechRecognizer = SpeechRecognizer.createRecognizer(mContext, null);

            // 2.set params.
            mIFlySpeechRecognizer.setParameter(SpeechConstant.DOMAIN, "iat");
            mIFlySpeechRecognizer.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
            mIFlySpeechRecognizer.setParameter(SpeechConstant.ACCENT, "mandarin");

        } catch (Exception e) {
            e.printStackTrace();
            mIFlySpeechRecognizer = null;
            if (mResultListener != null) {
                try {
                    mResultListener.onError(e.getMessage());
                } catch (RemoteException e1) {
                    e1.printStackTrace();
                }
            }
        } catch (Throwable error) {
            error.printStackTrace();
            mIFlySpeechRecognizer = null;
        }
    }

    /**
     * start recognition.
     */
    public void recognizer() {

        // Testing Code Begin:
        if (!engineAvailable()) {
            if (mResultListener != null) {
                try {
                    mResultListener.onError("engine not available.");
                } catch (RemoteException e) {
                    e.printStackTrace();
                }
            }
            System.out.println("google engine not available.");
            // return;
        }

        disableEngine();
        // Testing Code End.

        if (mIFlySpeechRecognizer != null) {
            mIFlySpeechRecognizer.startListening(mRecoListener);
        } else {
            print("onError：mIat is null.");
        }
    }

    /**
     * stop listening.
     */
    public void stopListening() {
        if (mIFlySpeechRecognizer != null) {
            print("stopListening");
            mIFlySpeechRecognizer.stopListening();
        }
    }

    /**
     * release recognizer.
     */
    public void destroyRecognizer() {
        if (mIFlySpeechRecognizer != null) {
            print("destroyRecognizer");
            mIFlySpeechRecognizer.destroy();
            enableEngine();
        }
    }

    public boolean engineAvailable() {
        print("enable:" + mEnable);
        return mEnable;
    }

    private List<ResultModel> parseIatResult(String json) {
        List<ResultModel> results = new ArrayList<ResultModel>();
        StringBuffer ret = new StringBuffer();
        try {
            JSONTokener tokener = new JSONTokener(json);
            JSONObject joResult = new JSONObject(tokener);

            JSONArray words = joResult.getJSONArray("ws");
            for (int i = 0; i < words.length(); i++) {
                // parse responsed json.
                JSONArray items = words.getJSONObject(i).getJSONArray("cw");
                JSONObject obj = items.getJSONObject(0);
                ret.append(obj.getString("w"));
                // parse other key-fields if much results needed:
                // for(int j = 0; j < items.length(); j++)
                // {
                // JSONObject obj = items.getJSONObject(j);
                // ret.append(obj.getString("w"));
                // }
                print("result:::" + ret.toString());
                ResultModel result = new ResultModel(ret.toString(), 90);
                results.add(result);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return results;
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
            Log.i(TAG, "ifly." + string);
        }
    }
}
