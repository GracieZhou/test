package com.eostek.sciflyui.voicecontroller.service.recognition;

import com.eostek.sciflyui.voicecontroller.service.recognition.IResultListener;

interface IVoiceRecognitionManager {
    
    void recognize(IResultListener listener);
    
}
