package com.eostek.scifly.devicemanager.data;

import android.view.View;

import com.nostra13.universalimageloader.core.assist.FailReason;

import scifly.datacache.DataCacheListener;

public class CacheListener extends DataCacheListener{
    
    @Override
    public void onLoadingStarted(String requestUri, View view) {
        super.onLoadingStarted(requestUri, view);
    }
    
    @Override
    public void onLoadingFailed(String requestUri, View view, FailReason failReason) {
        super.onLoadingFailed(requestUri, view, failReason);
    }
    
    @Override
    public void onLoadingComplete(String requestUri, View view, Object dataObject) {
        super.onLoadingComplete(requestUri, view, dataObject);
    }
    
    @Override
    public void onLoadingCancelled(String requestUri, View view) {
        super.onLoadingCancelled(requestUri, view);
    }
    
    @Override
    public void onCheckingComplete(String requestUri, View view, Object dataObject) {
        super.onCheckingComplete(requestUri, view, dataObject);
    }
}
