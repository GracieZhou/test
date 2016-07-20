package com.eostek.scifly.ime.sync.words;

import com.eostek.scifly.ime.sync.words.IWordsChangedListener;

/**
 * interface of words-synchronization.
 *
 */
interface IWordsSyncManager{

    /**
     * set listener in case of words changed.
     * @param wordsChangedListener listener.
     *
     */
    boolean registerWordsChangedListener(IWordsChangedListener wordsChangedListener);
    
    /**
     * remove words-change listener.
     * @param wordsChangedListener listener.
     */
    void unregisterWordsChangedListener(IWordsChangedListener wordsChangedListener);
    
    /**
     * send words to iSynergy by Socket.
     * @param type words or commands.
     * @param words words need to send.
     */
    void sendBySocket(int type, String words);
    
    /**
     * is socket usable.
     */
    boolean isSocketAvailable();

    void release();
    
    void onDataAvailable(in byte[] data) ;
    
    void onClientConnect(in String clientIP);
    void onClientDisconnnect(in String clientIP);
}