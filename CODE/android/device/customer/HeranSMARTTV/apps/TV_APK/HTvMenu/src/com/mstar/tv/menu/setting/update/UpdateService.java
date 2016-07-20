package com.mstar.tv.menu.setting.update;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Bundle;
import android.os.Environment;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.StatFs;
import android.util.Log;
import android.widget.RemoteViews;

import com.mstar.tv.menu.R;

public class UpdateService extends Service {

    private static final String TAG = "MSettings.UpdateService";

    private final static int DOWNLOADING = 0;

    private final static int DOWNLOAD_ERROR = 3;

    private final static String NAME = "share_pres";

    private final static String PERCENT = "percent";

    private final static String PERCENT_CHANGED = "percent_changed";

    private Notification mNotification;

    private NotificationManager mNotificationManager;

    private PendingIntent mPendingIntent;

    private RemoteViews mRemoteViews;

    private String mDownloadUrl;

    private String mNewVersion;

    private int mDownloadPercent;

    private String mSize;

    private Handler mHandler = new Handler() {

        public void handleMessage(android.os.Message msg) {
            super.handleMessage(msg);
            Log.d(TAG, "mHandler msg " + msg.what);
            if (msg.what <= 100) {
                if (msg.what == 0) {
                    mNotification.contentView = mRemoteViews;
                    mNotification.contentIntent = mPendingIntent;
                    mNotificationManager.notify(DOWNLOADING, mNotification);
                }
                if (msg.what == 100) {
                    commitPercentValue(PERCENT, 100);
                    mNotificationManager.cancel(DOWNLOADING);
                    mNotification.flags |= Notification.FLAG_AUTO_CANCEL;
                    showNotification(R.drawable.one_px, R.string.downloading, msg.what);
                }
                mRemoteViews.setTextViewText(R.id.task_percent, msg.what + "%");
                mRemoteViews.setProgressBar(R.id.task_progressbar, 100, msg.what, false);
                sendPercentData();
            }
            mNotification.contentView = mRemoteViews;
            mNotification.contentIntent = mPendingIntent;
            mNotificationManager.notify(DOWNLOADING, mNotification);
        };
    };

    private Handler mErrorHandler = new Handler() {
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if (msg.what == DOWNLOAD_ERROR) {
                commitPercentValue(PERCENT, mDownloadPercent);
                // comment : fix mantis bug 0290118
                onUpdateError(ERROR_DOWNLOAD);
            }
        };
    };

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        mNotificationManager = (NotificationManager) getSystemService(NOTIFICATION_SERVICE);
        mDownloadPercent = getPercentData(PERCENT);
        showNotification(R.drawable.one_px, R.string.downloading, mDownloadPercent);

        Message msg = mHandler.obtainMessage();
        msg.what = mDownloadPercent;
        mHandler.sendMessage(msg);
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        mDownloadUrl = intent.getStringExtra("downUrl");
        mNewVersion = intent.getStringExtra("newVersion");
        mSize = intent.getStringExtra("size");
        Log.d(TAG, "UpdateService-downUrl, " + mDownloadUrl);
        Log.d(TAG, "UpdateService-newVersion, " + mNewVersion);
        Log.d(TAG, "UpdateService-mSize, " + mSize);
        startDownload();

        return super.onStartCommand(intent, flags, startId);
    }

    /**
     * start download the update package.
     */
    private void startDownload() {
        String directoryName = "";
        long cacheFreeSize = getCacheFreeSize();
        if (cacheFreeSize > Long.parseLong(mSize)) {
            directoryName = "/cache/update_signed.zip";
        } else {
            directoryName = Environment.getExternalStorageDirectory().toString()
                    + "/update_signed.zip";
        }

        IDownloadProgressListener downloadProgressListener = new IDownloadProgressListener() {

            @Override
            public void onDownloadSizeChange(int percent) {
                mDownloadPercent = percent;
                Log.d(TAG, "percent:" + percent);
                Message msg = mHandler.obtainMessage();
                msg.what = percent;
                mHandler.sendMessage(msg);
            }
        };

        UpgradeTask upgradeTask = new UpgradeTask(UpdateService.this, mDownloadUrl, directoryName,
                mNewVersion, downloadProgressListener, mErrorHandler);

        new Thread(upgradeTask).start();
    }

    private void showNotification(int drawbale, int titleId, int percent) {
        mNotification = new Notification(drawbale, getString(R.string.update_packages_download),
                System.currentTimeMillis());
        mRemoteViews = new RemoteViews(getApplication().getPackageName(),
                R.layout.download_progress);
        Intent intent = new Intent();
        if (percent == 100) {
            mNotification = new Notification(drawbale, getString(R.string.update_package_download_complete),
                    System.currentTimeMillis());
            intent.setClass(this, SystemNetUpdateActivity.class);
            Log.d(TAG, "showNotification 100");
        } else {
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK
                    | Intent.FLAG_ACTIVITY_RESET_TASK_IF_NEEDED);
            Log.d(TAG, "showNotification downloadPercent, " + mDownloadPercent);
        }
        mPendingIntent = PendingIntent.getActivity(this, 0, intent,
                PendingIntent.FLAG_UPDATE_CURRENT);
    }

    private void commitPercentValue(String key, int percent) {
        SharedPreferences preference = getSharedPreferences(NAME, Context.MODE_PRIVATE);
        Editor edit = preference.edit();
        edit.putInt(key, percent);
        edit.commit();
    }

    private int getPercentData(String key) {
        SharedPreferences preference = getSharedPreferences(NAME, Context.MODE_PRIVATE);
        return preference.getInt(key, 0);
    }

    private void sendPercentData() {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putInt(PERCENT, mDownloadPercent);
        intent.setAction(PERCENT_CHANGED);
        intent.putExtras(bundle);
        sendBroadcast(intent);
    }

    private long getCacheFreeSize() {
        StatFs sf = new StatFs("/cache");
        long blockSize = sf.getBlockSize();
        long availCount = sf.getAvailableBlocks();

        return availCount * blockSize;
    }

    // comment : fix mantis bug 0290118 FIXME : replace with listener?
    private static final String KEY_ERROR = "key_update_error";

    private static final String ACTION_ERROR = "action_update_error";

    private static final int ERROR_DOWNLOAD = 1;

    private void onUpdateError(int errorCode) {
        Intent intent = new Intent();
        Bundle bundle = new Bundle();
        bundle.putInt(KEY_ERROR, errorCode);
        intent.setAction(ACTION_ERROR);
        intent.putExtras(bundle);
        sendBroadcast(intent);
    }

}
