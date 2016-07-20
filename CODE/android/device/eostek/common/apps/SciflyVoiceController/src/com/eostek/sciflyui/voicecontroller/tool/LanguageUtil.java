
package com.eostek.sciflyui.voicecontroller.tool;

import java.util.Locale;

import com.eostek.sciflyui.voicecontroller.service.recognition.VoiceRecognitionManager.RecognizerEnum;


public class LanguageUtil {

    public static String TAG = "LanguagUtil";

    public static String getLanguageEnv() {
        Locale locale = Locale.getDefault();
        return locale.getLanguage();
    }

    public static RecognizerEnum getLanguageEnum() {
        String language = getLanguageEnv();
        if ("zh".equals(language)) {
            return RecognizerEnum.IFLY;
        }
        return RecognizerEnum.GOOGLE;
    }
}
