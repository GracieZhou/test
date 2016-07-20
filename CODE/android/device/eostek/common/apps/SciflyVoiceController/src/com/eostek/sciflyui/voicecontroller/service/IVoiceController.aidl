
package com.eostek.sciflyui.voicecontroller.service;

import com.eostek.sciflyui.voicecontroller.service.semantic.IVoiceSemanticManager;
import com.eostek.sciflyui.voicecontroller.service.recognition.IVoiceRecognitionManager;

interface IVoiceController {

    /** get instance of VoiceRecognitionManager */
    IVoiceRecognitionManager getVoiceRecognitionManager();

    /** get instance of VoiceRecognitionManager */
    IVoiceSemanticManager getVoiceSemanticManager();

}
