package com.eostek.scifly.devicemanager.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;

public class UpgradeService extends Service {

    @Override
    public void onCreate() {
        super.onCreate();
    }
    
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        
        return super.onStartCommand(intent, flags, startId);
    }
    
    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }
    
    @Override
    public boolean onUnbind(Intent intent) {
        return super.onUnbind(intent);
    }
    
    @Override
    public void onRebind(Intent intent) {
        super.onRebind(intent);
    }
    
    @Override
    public void onDestroy() {
        super.onDestroy();
    }
    
}
