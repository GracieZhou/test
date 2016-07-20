package com.eostek.scifly.ime.sync.words;

/**
 * words changed listener.
 */
interface IWordsChangedListener{
    /**
     * words changed.
     * @param type if it is words or command.
     * @param words character input.
     */
    void onWordsChanged(int type, String words);

    /**
     * socket status changed.
     * @param isAvailable is socket usable.
     */
    void socketStatus(boolean isAvailable);
}