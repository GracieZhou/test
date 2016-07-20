package com.eostek.scifly.devicemanager.data;

import android.view.View;

import scifly.datacache.DataCacheProgressListener;

public class CacheProgressListener extends DataCacheProgressListener{
    
    @Override
    public void onProgressUpdate(String imageUri, View view, int current, int total) {
        super.onProgressUpdate(imageUri, view, current, total);
    }
    
}
