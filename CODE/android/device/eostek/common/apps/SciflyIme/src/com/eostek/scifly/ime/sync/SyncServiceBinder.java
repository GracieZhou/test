
package com.eostek.scifly.ime.sync;

import android.content.Context;

import com.eostek.scifly.ime.sync.sensors.ISensorSyncManager;
import com.eostek.scifly.ime.sync.sensors.SensorSyncManager;
import com.eostek.scifly.ime.sync.words.IWordsSyncManager;
import com.eostek.scifly.ime.sync.words.WordsSyncManager;

/**
 * implementation of ISyncManager.
 */
public class SyncServiceBinder extends ISyncManager.Stub {

    private static Context mContext;

    /**
     * Constructor.
     * 
     * @param context service.
     */
    public SyncServiceBinder(Context context) {
        mContext = context;
    }

    @Override
    public IWordsSyncManager getWordsSyncManager() {
        return WordsSyncManager.getInstance(mContext);
    }

    @Override
    public ISensorSyncManager getSensorSyncManager() {
        return SensorSyncManager.getInstance(mContext);
    }
}
