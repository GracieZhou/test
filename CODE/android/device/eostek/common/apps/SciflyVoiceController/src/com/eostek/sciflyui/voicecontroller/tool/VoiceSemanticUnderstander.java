
package com.eostek.sciflyui.voicecontroller.tool;

import android.content.Context;
import android.os.Environment;
import android.util.Log;

import com.iflytek.cloud.ErrorCode;
import com.iflytek.cloud.InitListener;
import com.iflytek.cloud.SpeechConstant;
import com.iflytek.cloud.SpeechUnderstander;
import com.iflytek.cloud.SpeechUnderstanderListener;

public class VoiceSemanticUnderstander {

    private static final String SDCARD_PCM_PATH = Environment.getExternalStorageDirectory().getPath()
            + "/Android/data/com.eostek.sciflyui.voicecontroller/wavaudio.pcm";

    private static final String TAG = "VoiceSemanticUnderstander";

//    private Context mContext;

    private static SpeechUnderstander mSpeechUnderstander;

    public VoiceSemanticUnderstander(Context context) {
//        mContext = context;
        initVoiceSemanticRecognizer(context);
    }

    private void initVoiceSemanticRecognizer(Context context) {
        initVoiceSemanticRecognizer(context, speechUnderstanderListener);
    }

    private void initVoiceSemanticRecognizer(Context context, InitListener l) {
        if (mSpeechUnderstander == null) {
            mSpeechUnderstander = SpeechUnderstander.createUnderstander(context, speechUnderstanderListener);
        }
    }

    public void startVoiceUnderstand(SpeechUnderstanderListener l) {
        setParam();

        int ret = 0;

        if (mSpeechUnderstander.isUnderstanding()) {// 开始前检查状态
            mSpeechUnderstander.stopUnderstanding();
            Log.i(TAG, "停止录音");
        } else {
            ret = mSpeechUnderstander.startUnderstanding(l);
            if (ret != 0) {
                Log.i(TAG, "语义理解失败,错误码:" + ret);
            }
        }

    }

    /**
     * 初始化监听器（语音到语义）。
     */
    private InitListener speechUnderstanderListener = new InitListener() {
        @Override
        public void onInit(int code) {
            Log.d(TAG, "speechUnderstanderListener init() code = " + code);
            if (code != ErrorCode.SUCCESS) {
                Log.i(TAG, "初始化失败,错误码：" + code);
            }
        }
    };

    private void setParam() {

        mSpeechUnderstander.setParameter(SpeechConstant.LANGUAGE, "zh_cn");
        // 设置语言区域
        mSpeechUnderstander.setParameter(SpeechConstant.ACCENT, "zh_cn");
        // 设置语音前端点
        mSpeechUnderstander.setParameter(SpeechConstant.VAD_BOS, "4000");
        // 设置语音后端点
        mSpeechUnderstander.setParameter(SpeechConstant.VAD_EOS, "1000");
        // 设置标点符号
        mSpeechUnderstander.setParameter(SpeechConstant.ASR_PTT, "0");
        // 设置音频保存路径
        mSpeechUnderstander.setParameter(SpeechConstant.ASR_AUDIO_PATH, SDCARD_PCM_PATH);
    }

    /** destroy voice understander */
    public void destroy() {
        if (mSpeechUnderstander != null) {
            mSpeechUnderstander.cancel();
            mSpeechUnderstander.destroy();
        }
    }

    /** destroy voice understander */
    public void stop() {
        if (mSpeechUnderstander != null && mSpeechUnderstander.isUnderstanding()) {
            mSpeechUnderstander.stopUnderstanding();
        }
    }

}
