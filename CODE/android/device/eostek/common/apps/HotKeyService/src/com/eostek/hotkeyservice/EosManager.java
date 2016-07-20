package com.eostek.hotkeyservice;

import com.mstar.android.tv.TvCommonManager;
import com.mstar.android.tvapi.common.TvManager;
import com.mstar.android.tvapi.common.exception.TvCommonException;

public class EosManager {
    
    private static EosManager mInstance = null;
    
    public static EosManager getInstance() {
        if (mInstance == null) {
            mInstance = new EosManager();
        }
        return mInstance;
    }
    
    private EosManager() {
        
    }
    
    public boolean[] GetInputSourceStatus() {
        boolean[] mSourceDetectResult = new boolean[TvCommonManager.INPUT_SOURCE_NUM];
        try {
            short[] sourceStatus = TvManager.getInstance().setTvosCommonCommand("GetInputSourceStatus");
            for (int i = 0; i < TvCommonManager.INPUT_SOURCE_NUM; i++) {
                mSourceDetectResult[i] = (sourceStatus[i] != 0);
            }
        } catch (TvCommonException e) {
            e.printStackTrace();
        }
        return mSourceDetectResult;
    }
}
