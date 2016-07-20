
package com.android.server.scifly;

import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.graphics.PixelFormat;
import android.net.Uri;
import android.os.Handler;
import android.os.Looper;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import scifly.provider.SciflyStatistics;
import scifly.provider.SciflyStore;
import android.widget.TextView;

/**
 * Handler for handling Message or UI refresh.
 */
public class SciflyHandler extends Handler {

    private static final String TAG = "SciflyHandler";

    private static final boolean DBG = true;

    static final int SHOW_AUTHORIZE_FAILED_VIEW = 1;

    static final int DISMISS_AUTHORIZE_FAILED_VIEW = 2;

    // This is an alarm clock for SciflyStatistics
    static final int MSG_STATISTICS_TRIGGER_UPLOAD = 3;

    private Intent mStatisticsIntent;

    private long mLastTriggerUploadTime = 0;

    private Context mContext = null;

    private static long mStatisticsInterval = SciflyStatistics.DEFAULT_STATISTICS_INTERVAL_SECOND;

    // add overlay view
    private WindowManager mWindowManager = null;

    // for inflate view
    private LayoutInflater mInflater = null;

    // view for authorize failed
    private View mAuthorizeFailedView = null;

    private ContentObserver mStatisticsContentObserver = new ContentObserver(this) {
        @Override
        public void onChange(boolean selfChange, Uri uri) {
            super.onChange(selfChange, uri);
            if (DBG) {
                Log.d(TAG, "onChange selfChange=" + selfChange + ", uri=" + uri);
            }
            if (SciflyStatistics.STATISTICS_URI_STATISTICS.compareTo(uri) == 0) {
                // StatisticsRecord item count reach the threshold of uploading.
                // For more info, see SciflyProvider.
                Log.d(TAG, "Trigger uploading, mStatisticsInterval=" + mStatisticsInterval);
                // Cancel previous uploading message.
                if (mStatisticsInterval > 0) {
                    removeMessages(MSG_STATISTICS_TRIGGER_UPLOAD);
                    triggerUpload();
                    sendEmptyMessageDelayed(MSG_STATISTICS_TRIGGER_UPLOAD, (mStatisticsInterval * 1000));
                }
            } else if (SciflyStatistics.STATISTICS_URI_GLOBAL.compareTo(uri) < 0) {
                mStatisticsInterval = getLong(SciflyStore.Global.STATISTICS_INTERVAL);
                if (0 < mStatisticsInterval
                        && mStatisticsInterval < SciflyStatistics.DEFAULT_STATISTICS_MIN_INTERVAL_SECOND) {
                    mStatisticsInterval = SciflyStatistics.DEFAULT_STATISTICS_INTERVAL_SECOND;
                }
                Log.d(TAG, "Notify mStatisticsInterval changed, mStatisticsInterval=" + mStatisticsInterval);
            }
        }
    };

    /**
     * Show and refresh overlay UI.
     * 
     * @param looper {@link Looper}
     * @param context {@link Context}
     */
    public SciflyHandler(Looper looper, Context context) {
        super(looper);
        mContext = context;
        mWindowManager = (WindowManager) mContext.getSystemService(Context.WINDOW_SERVICE);
        mInflater = (LayoutInflater) mContext.getSystemService(Context.LAYOUT_INFLATER_SERVICE);

        // Init SciflyStatistics
        initStatistics();
    }

    private void initStatistics() {
        mStatisticsInterval = getLong(SciflyStore.Global.STATISTICS_INTERVAL);
        Log.d(TAG, "initStatistics, mStatisticsInterval=" + mStatisticsInterval);
        if (mStatisticsInterval < SciflyStatistics.DEFAULT_STATISTICS_MIN_INTERVAL_SECOND) {
            mStatisticsInterval = SciflyStatistics.DEFAULT_STATISTICS_INTERVAL_SECOND;
        }
        sendEmptyMessageDelayed(MSG_STATISTICS_TRIGGER_UPLOAD, (mStatisticsInterval * 1000));
        mContext.getContentResolver().registerContentObserver(SciflyStatistics.STATISTICS_URI_GLOBAL, true,
                mStatisticsContentObserver);
        mContext.getContentResolver().registerContentObserver(SciflyStatistics.STATISTICS_URI_STATISTICS, true,
                mStatisticsContentObserver);
    }

    private void triggerUpload() {
        long currentTime = System.currentTimeMillis();
        if (currentTime - mLastTriggerUploadTime < (SciflyStatistics.DEFAULT_STATISTICS_MIN_INTERVAL_SECOND * 1000)) {
            Log.d(TAG, "There is an other triggering in less than 5 minutes, discard this one!");
            return;
        } else {
            mLastTriggerUploadTime = currentTime;
        }
        if (mStatisticsIntent == null) {
            mStatisticsIntent = new Intent(SciflyStatistics.ACTION_STATISTICS_TRIGGER_UPLOAD);
        }
        Log.d(TAG, "SendBroadcast to trigger uploading Statistics @" + currentTime);
        mContext.sendBroadcast(mStatisticsIntent);
    }

    private long getLong(String key) {
        String param = null;
        try {
            param = SciflyStore.Global.getString(mContext.getContentResolver(), key);
        } catch (Exception e) {
        }
        long value = SciflyStatistics.DEFAULT_STATISTICS_INTERVAL_SECOND;
        if (TextUtils.isEmpty(param)) {
            return value;
        } else {
            try {
                value = Long.valueOf(param);
            } catch (NumberFormatException e) {
                e.printStackTrace();
            }
        }
        return value;
    }

    @Override
    public void handleMessage(Message msg) {
        super.handleMessage(msg);
        if (DBG) {
            Log.d(TAG, "msg.what : " + msg.what + " msg.obj : " + (String) msg.obj);
        }

        switch (msg.what) {
            case SHOW_AUTHORIZE_FAILED_VIEW:
                showAuthorizeFailedView((String) msg.obj);
                break;
            case DISMISS_AUTHORIZE_FAILED_VIEW:
                dismissAuthorizeFailedView();
                break;
            case MSG_STATISTICS_TRIGGER_UPLOAD:
                triggerUpload();
                sendEmptyMessageDelayed(MSG_STATISTICS_TRIGGER_UPLOAD, (mStatisticsInterval * 1000));
                break;
            default:
                break;
        }
    }

    private void showAuthorizeFailedView(String msg) {
        boolean visible = false;
        if (mAuthorizeFailedView == null) {
            mAuthorizeFailedView = mInflater.inflate(com.android.internal.R.layout.authorize_failed_layout, null);
        } else {
            visible = true;
        }

        TextView warning = (TextView) mAuthorizeFailedView
                .findViewById(com.android.internal.R.id.authorize_failed_message);
        if (TextUtils.isEmpty(msg)) {
            warning.setText("authorize failed!");
        } else {
            warning.setText(msg);
        }
        // TOP & H
        int height = mContext.getResources().getDimensionPixelSize(
                com.android.internal.R.dimen.authorize_failed_layout_height);
        if (height == 0) {
            // default height
            height = 72;
        }
        WindowManager.LayoutParams params = new WindowManager.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, height,
                WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY, WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE,
                PixelFormat.TRANSLUCENT);
        params.type = WindowManager.LayoutParams.TYPE_SYSTEM_OVERLAY;
        params.gravity = Gravity.TOP | Gravity.FILL_HORIZONTAL;
        if (visible) {
            mWindowManager.updateViewLayout(mAuthorizeFailedView, params);
        } else {
            mWindowManager.addView(mAuthorizeFailedView, params);
        }
    }

    private void dismissAuthorizeFailedView() {
        if (mAuthorizeFailedView != null) {
            mWindowManager.removeView(mAuthorizeFailedView);
            mAuthorizeFailedView = null;
        }
    }
}
