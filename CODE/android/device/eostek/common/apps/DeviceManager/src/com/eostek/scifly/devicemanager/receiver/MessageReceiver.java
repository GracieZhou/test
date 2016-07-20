
package com.eostek.scifly.devicemanager.receiver;

import java.util.ArrayList;
import java.util.List;

import com.eostek.scifly.devicemanager.MemoryFullActicity;
import com.eostek.scifly.devicemanager.service.DownloadService;
import com.eostek.scifly.devicemanager.manage.appmanagement.AppManagementConfig;
import com.eostek.scifly.devicemanager.util.Constants;
import com.eostek.scifly.devicemanager.util.Debug;

import android.app.DownloadManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class MessageReceiver extends BroadcastReceiver {

    private static String TAG = MessageReceiver.class.getSimpleName();
    
    private static List<MessageListener> mMessageListeners = new ArrayList<MessageReceiver.MessageListener>();
    
    public interface MessageListener {
        public void onDownloadComplete(long downloadId);
        public void onPackageAdded(String packName);
        public void onPackageRemoved(String packName);
        public void onPackageReplaced(String packName);
        public void onMediaMounted();
        public void onMediaEject();
    }
    
    public static void addMessageListener(MessageListener listener) {
        if(mMessageListeners != null) {
            mMessageListeners.add(listener);
        }
    }
    
    public static void removeMessageListener(MessageListener listener) {
        if(mMessageListeners != null) {
            mMessageListeners.remove(listener);
        }
    }
    
    @Override
    public void onReceive(final Context context, Intent intent) {

        Debug.d(TAG, "onReceive intent.getAction() : " + intent.getAction());
        
        if (intent.getAction().equals(Intent.ACTION_DEVICE_STORAGE_LOW)) {
            Intent mIntent = new Intent(context, MemoryFullActicity.class);
            mIntent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(mIntent);
        }
        
        if (intent.getAction().equals("android.net.conn.CONNECTIVITY_CHANGE")) {
            if ( AppManagementConfig.getAutoUpdateCfg(context, Constants.CB_AUTOUPDATE)) {
                ConnectivityManager mConnManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
                NetworkInfo netInfo = mConnManager.getActiveNetworkInfo();
                
                Intent mIntent = new Intent();
                mIntent.setClass(context, DownloadService.class);
                mIntent.putExtra(Constants.MSG_AUTO_UPDATE_TYPE, Constants.MSG_AUTO_UPDATE_VALUE);
                if(netInfo != null && netInfo.isAvailable()) {
                    context.startService(mIntent);
                }
            } 
        }

        if (intent.getAction().equals("android.intent.action.PACKAGE_ADDED")) {
            String mPkgName = intent.getData().getSchemeSpecificPart();
            for(MessageListener listener : mMessageListeners) {
                listener.onPackageAdded(mPkgName);
            }
        }
        
        if (intent.getAction().equals("android.intent.action.PACKAGE_REMOVED")) {
            String mPkgName = intent.getData().getSchemeSpecificPart();
            for(MessageListener listener : mMessageListeners) {
                listener.onPackageRemoved(mPkgName);
            }
        }
        
        if (intent.getAction().equals("android.intent.action.PACKAGE_REPLACED")) {
            String mPkgName = intent.getData().getSchemeSpecificPart();
            for(MessageListener listener : mMessageListeners) {
                listener.onPackageReplaced(mPkgName);
            }
        }
        
        if (intent.getAction().equals("android.intent.action.DOWNLOAD_COMPLETE")) {
            long id = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, 0);
            for(MessageListener listener : mMessageListeners) {
                listener.onDownloadComplete(id);
            }
        }
        
        if (intent.getAction().equals("android.intent.action.MEDIA_MOUNTED")) {
            for(MessageListener listener : mMessageListeners) {
                listener.onMediaMounted();
            }
        }
        
        if (intent.getAction().equals("android.intent.action.MEDIA_EJECT")) {
            for(MessageListener listener : mMessageListeners) {
                listener.onMediaEject();
            }
        }
    }
}
