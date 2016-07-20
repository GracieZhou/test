
package com.eostek.history;


/**
 * Listener of history.
 */
public class HistoryListener {

    private static final String TAG = "HistoryListener";

    private HistoryHolder mHolder;

    private MainActivity mContext;

    /**
     * Constructor of HistoryListener.
     * 
     * @param mHolder
     * @param mContext
     */
    public HistoryListener(HistoryHolder mHolder, MainActivity mContext) {
        this.mHolder = mHolder;
        this.mContext = mContext;
    }

    /**
     * Set listener.
     */
    public void setListener() {
        mHolder.getLeftNavBarContainer().setupTabAndListener();
    }

}
