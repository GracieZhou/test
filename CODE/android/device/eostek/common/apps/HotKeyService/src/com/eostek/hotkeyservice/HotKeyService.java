
package com.eostek.hotkeyservice;

import android.app.Dialog;
import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.util.Log;
import android.view.Window;
import android.view.WindowManager;

import com.eostek.hotkeyservice.dialog.AdDialog;
import com.eostek.hotkeyservice.dialog.SourceDialog;
import com.eostek.hotkeyservice.util.Constants;

public class HotKeyService extends Service {

    private final String TAG = "HotKeyService";

    private HandlerThread mThread;

    private HotKeyHandler mHandler;

    private HotKeyReceiver mHotKeyReceiver;

    private HotKeyService mContext;

    private Dialog mDialog;

    private int mCallOn = 0;

    private int mIndex = 0;

    private int mPosition = 0;

    private boolean mLanguage = false;

    private Dialog mLanguageDialog;

    private class HotKeyHandler extends Handler {
        public HotKeyHandler(Looper looper) {
            super(looper);
        }

        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case Constants.SOURCE_INDEX:
                    // if (mDialog != null && mDialog instanceof
                    // BlueToothDialog) {
                    // return;
                    // }
                    break;
                case Constants.AD_INDEX:
                    if (mDialog != null && mDialog instanceof AdDialog) {
                        mDialog.dismiss();
                        mDialog = null;
                        return;
                    }
                    break;
                default:
                    break;
            }
            createDialog(msg.what);
        }
    }

    public boolean isShowDialog() {
        if (mLanguageDialog != null && mLanguageDialog.isShowing()) {
            return true;
        }
        return false;
    }

    private class HotKeyReceiver extends BroadcastReceiver {

        @Override
        public void onReceive(Context context, Intent intent) {
            Log.i(TAG, "---onReceive ---action==" + intent.getAction());
            String action = intent.getAction();
            switch (action) {
                case Constants.SOURCE_ACTION:
                    mHandler.sendEmptyMessage(Constants.SOURCE_INDEX);
                    break;
                case Constants.AD_ACTION:
                    mHandler.sendEmptyMessage(Constants.AD_INDEX);
                    break;
                case Constants.DISMISS_ACTION:
                    if (mDialog != null && mDialog.isShowing()) {
                        mDialog.cancel();
                    }
                    break;

                default:
                    break;
            }
        }
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent == null) {
            return START_REDELIVER_INTENT;
        }
        int index = intent.getIntExtra(Constants.ACTION, -1);
        if (index != -1) {
            switch (index) {
                case Constants.SOURCE_INDEX:
                default:
                    break;
            }
        }
        return START_REDELIVER_INTENT;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        mThread = new HandlerThread("HotKeyThread");
        mThread.start();
        mHandler = new HotKeyHandler(mThread.getLooper());

        IntentFilter filter = new IntentFilter();
        filter.addAction(Constants.AD_ACTION);
        filter.addAction(Constants.SOURCE_ACTION);
        filter.addAction(Constants.DISMISS_ACTION);
        mHotKeyReceiver = new HotKeyReceiver();
        registerReceiver(mHotKeyReceiver, filter);

        mContext = this;
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(mHotKeyReceiver);
    }

    private void createDialog(int id) {

        if (mDialog != null && mDialog.isShowing()) {
            mDialog.cancel();
        }

        switch (id) {
            case Constants.SOURCE_INDEX:
                mDialog = new SourceDialog(mContext, R.style.dialog);
                break;
            case Constants.AD_INDEX:
                mDialog = new AdDialog(mContext, R.style.dialog);
                break;
            default:
                break;
        }

        showDialog(mDialog);
    }

    private void showDialog(Dialog dialog) {

        Window dialogWindow = dialog.getWindow();

        WindowManager.LayoutParams param = dialogWindow.getAttributes();

        param.type = WindowManager.LayoutParams.TYPE_SYSTEM_ALERT;

        dialogWindow.setAttributes(param);

        dialog.show();
    }

    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

}
