
package com.eostek.sciflyui.voicecontroller.service;

import android.app.Service;
import android.content.Intent;
import android.os.IBinder;
import android.os.RemoteException;
import android.util.Log;

import com.eostek.sciflyui.voicecontroller.service.recognition.IVoiceRecognitionManager;
import com.eostek.sciflyui.voicecontroller.service.recognition.VoiceRecognitionManager;
import com.eostek.sciflyui.voicecontroller.service.semantic.IVoiceSemanticManager;
import com.eostek.sciflyui.voicecontroller.service.semantic.VoiceSemanticManager;

/**
 * core-part of voice-recognition.
 * 
 * @author Youpeng
 * @date 2014-10-14
 */
public class VoiceService extends Service {

    private static final String TAG = "VoiceControlService";

    private VoiceBinder mVoiceControllerBinder;

    private static VoiceSemanticManager mVoiceSemanticManager;

    @Override
    public IBinder onBind(Intent arg0) {

        if (mVoiceControllerBinder == null) {
            mVoiceControllerBinder = new VoiceBinder();
        }

        return mVoiceControllerBinder;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        print("onCreate()");

        mVoiceControllerBinder = new VoiceBinder();

        Log.i(TAG,
                "Name:SciflyVoiceController, Version:2.4.4, Date:2015-09-02, Publisher:Shirley.Jiang, REV:40278");
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        
        
        Log.i(TAG, "startCommand.");
        if (mVoiceControllerBinder != null) {
            // show dialog and start recognition.
            try {
                mVoiceSemanticManager = (VoiceSemanticManager) mVoiceControllerBinder.getVoiceSemanticManager();
                mVoiceSemanticManager.startRecognitionWithUI(getApplicationContext());
                // mVoiceSemanticManager.setCommand("我想玩愤怒的小鸟");

            } catch (RemoteException e) {
                e.printStackTrace();
            }
        }
        return START_NOT_STICKY;
    }

    private void print(String str) {
        Log.i(TAG, str);
    }

    public class VoiceBinder extends IVoiceController.Stub {

        private IVoiceSemanticManager mVoiceSemanticManager;

        private IVoiceRecognitionManager mVoiceRecognitionManager;

        @Override
        public IVoiceSemanticManager getVoiceSemanticManager() throws RemoteException {

            if (mVoiceSemanticManager == null) {
                mVoiceSemanticManager = new VoiceSemanticManager(getApplicationContext(), (VoiceRecognitionManager)getVoiceRecognitionManager());
            }

            return mVoiceSemanticManager;
        }

        @Override
        public IVoiceRecognitionManager getVoiceRecognitionManager() throws RemoteException {

            if (mVoiceRecognitionManager == null) {
                mVoiceRecognitionManager = new VoiceRecognitionManager(getApplicationContext());
            }

            return mVoiceRecognitionManager;
        }

    }

}
