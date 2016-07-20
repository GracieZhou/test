package com.eostek.sciflyui.voicecontroller.service.recognition;

import com.eostek.sciflyui.voicecontroller.service.recognition.ResultModel;

interface IResultListener {

    /**
     * begin of speech.
     */
    void onBeginOfSpeech();

    /**
     * rms changed.
     */
    void onVolumeChanged(int db);

    /**
     * microphone disabled.
     */
    void micDisabled();

    /**
     * end of speech.
     */
    void onEndOfSpeech();

    /**
     * error occurred.
     * 
     * @param errString error description.
     */
    void onError(String errString);

    /**
     * recognized result.
     * 
     * @param recognizerType which recognize engine used.
     * @param results recognized results.
     */
    void onResults(int recognizerType, out List<ResultModel> result);
}
