
package com.eostek.sciflyui.voicecontroller.tool;

import android.content.Context;
import android.util.Log;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.TextUnderstander;
import com.iflytek.cloud.TextUnderstanderListener;

public class TextSemanticUnderstander {

    private static final String TAG = "TextSemanticUnderstander";

    // 语义理解对象（文本到语义）。
    private static TextUnderstander mTextUnderstander;

    private Context mContext;

    public TextSemanticUnderstander(Context context) {
        mContext = context;
        this.initSemanticRecognizer(mContext);
    }

    public TextSemanticUnderstander(Context context, InitListener l) {
        mContext = context;
        this.initSemanticRecognizer(mContext, l);
    }

    /**
     * This method used to initialize a {@link#TextUnderstander} instance with
     * {@link#InitListener}
     */
    private void initSemanticRecognizer(Context context, InitListener l) {

        if (mTextUnderstander == null) {
            mTextUnderstander = TextUnderstander.createTextUnderstander(context, l);
        }
    }

    /**
     * This method used to initialize a {@link#TextUnderstander} instance with
     * default {@link#InitListener}
     */
    private void initSemanticRecognizer(Context context) {
        initSemanticRecognizer(context, mSpeechUnderstanderListener);
    }

    /**
     * Start text understand.
     * 
     * @param text Text used to text understood
     * @param tul Set the TextUnderstanderListener
     * @return return result code. if ret equals 0 ,text understand succeed.
     */
    public int startTextUnderstand(String text, TextUnderstanderListener tul) {

        int ret = 0;

        if (mTextUnderstander.isUnderstanding()) {
            mTextUnderstander.cancel();
            Log.i(TAG, "cancel understanding");
        } else {
            ret = mTextUnderstander.understandText(text, tul);
            if (ret != 0) {
                Log.i("TextSemanticRecognizer", "text understand failed reason : " + ret);
            }
        }
        return ret;
    }

    /**
     * 初始化监听器（语音到语义）。
     */
    private InitListener mSpeechUnderstanderListener = new InitListener() {
        @Override
        public void onInit(int code) {
            if (code != ErrorCode.SUCCESS) {
                Log.i(TAG, "初始化失败,错误码：" + code);
            }
        }
    };

    /** destroy text understander */
    public void destroy() {
        if (mTextUnderstander != null) {
            if (mTextUnderstander.isUnderstanding()) {
                mTextUnderstander.cancel();
            }
            mTextUnderstander.destroy();
        }
    }
}
