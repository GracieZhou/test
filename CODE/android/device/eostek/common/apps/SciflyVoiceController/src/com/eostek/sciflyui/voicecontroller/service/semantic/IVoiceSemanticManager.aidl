package com.eostek.sciflyui.voicecontroller.service.semantic;

import com.eostek.sciflyui.voicecontroller.service.semantic.IStatusListener;

interface IVoiceSemanticManager{
    /**
     * turn on/off prop.
     */
    void setPropVisible(boolean on);
    
    /**
     *
     */
    void setCommand(String command);
    
    /**
     *
     */
    void setStatusListener(IStatusListener listener);
    
    void setVolume(int volume);
       
}