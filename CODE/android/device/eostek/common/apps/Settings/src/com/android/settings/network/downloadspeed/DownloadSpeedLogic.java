package com.android.settings.network.downloadspeed;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Timer;
import java.util.TimerTask;

import scifly.dm.EosDownloadListener;
import scifly.dm.EosDownloadManager;
import scifly.dm.EosDownloadTask;

import android.app.DownloadManager.Request;
import android.content.Context;
import android.net.TrafficStats;
import android.net.Uri;
import android.os.Handler;
import android.os.Message;
import android.util.Log;

public class DownloadSpeedLogic {
    private static final String TAG = "NetworkDiag";
    private static final String DOWNLOAD_URI_FIRST = "https://www.kernel.org/pub/linux/kernel/v3.x/linux-3.0.35.tar.gz";// 92.3MB
    private static final String DOWNLOAD_URI_SECOND = "https://www.kernel.org/pub/linux/kernel/v3.x/linux-3.0.35.tar.bz2";// 73.3MB
    private static final String DOWNLOAD_URI_THIRD = "https://www.kernel.org/pub/linux/kernel/v3.x/linux-3.0.34.tar.xz";// 60.9MB
    private static final String DOWNLOAD_URI_FOURTH = "https://www.kernel.org/pub/linux/kernel/v3.x/linux-3.0.34.tar.gz";// 92.3MB
    private static final int DOWNLOAD_URI_TOTAL_RX_BYTES = 334230254;
    private static final int DOWNLOAD_TIME_LIMIT = 30000;
    private static final int MSG_CALCULATE_DOWNLOAD_SPEED = 0;
    private static final int MSG_DOWNLOAD_TIME_END = 1;
    private Context mContext;
    private DownloadSpeedListener mDownloadSpeedListener;
    private EosDownloadManager mDownloadManager;
    private static HashMap<Integer, Long> mDownloadTask;
    private static ArrayList<Long> mListTaskIds;
    private static int mTask = 0;
    private long mLastRxBytes = 0;
    private int mMinDownloadSpeed = 0;
    private int mMaxDownloadSpeed = 0;
    private int mDownloadDuration = 0;
    private Timer mDownloadSpeedTimer;
    public Timer mDonwloadCountTimer;
    private int mCurrentTaskId = 0;
    private int mCountTime = 0;
    private int mDownloadPercent = 0;

    private Handler mHandler = new Handler() {
        public void handleMessage(Message msg) {
            switch (msg.what) {
            case MSG_CALCULATE_DOWNLOAD_SPEED:
                refreshDownloadData(false);
                break;
            case MSG_DOWNLOAD_TIME_END:
                stopDownload();
                mDonwloadCountTimer.cancel();
                refreshDownloadData(true);
                break;
            default:
                break;
            }
        }
    };

    private class DownloadSpeedThread implements Runnable {
        public void run() {
            mCurrentTaskId = startDownload();
            // download the files in 30s.
            mDonwloadCountTimer = new Timer();
            mDonwloadCountTimer.schedule(new TimerTask() {
                @Override
                public void run() {
                    mHandler.sendEmptyMessageDelayed(MSG_DOWNLOAD_TIME_END, 0);
                    Log.i(TAG, ">>>>>send message MSG_DOWNLOAD_TIME_END");
                }
            }, DOWNLOAD_TIME_LIMIT);
        }
    };

    public DownloadSpeedLogic(Context context) {
        this.mContext = context;
    }

    public void startCheckDownloadSpeed(
            DownloadSpeedListener downloadSpeedListener) {
        Log.i(TAG, ">>>>>Enter into startCheckDownloadSpeed");
        this.mDownloadSpeedListener = downloadSpeedListener;
        mDownloadTask = new HashMap<Integer, Long>();
        mListTaskIds = new ArrayList<Long>();
        DownloadSpeedThread runnable = new DownloadSpeedThread();
        new Thread(runnable).start();
    }

    synchronized private Integer startDownload() {
        Log.i(TAG, ">>>>>Enter into startDownload");
        if (mTask > 10) {
            return -1;
        }

        if (null == mDownloadManager) {
            mDownloadManager = new EosDownloadManager(mContext);
        }

        Request firstReq = new Request(Uri.parse(DOWNLOAD_URI_FIRST));
        Request secondReq = new Request(Uri.parse(DOWNLOAD_URI_SECOND));
        Request thirdReq = new Request(Uri.parse(DOWNLOAD_URI_THIRD));
        Request fourthReq = new Request(Uri.parse(DOWNLOAD_URI_FOURTH));

        EosDownloadListener listener = new EosDownloadListener() {

            @Override
            public void onDownloadStatusChanged(int status) {
                Log.i(TAG, ">>>>>>onDownloadStatusChanged");
            }

            @Override
            public void onDownloadSize(long size) {

            }

            @Override
            public void onDownloadComplete(int percent) {
            }
        };

        long firstTaskId = mDownloadManager.addTask(new EosDownloadTask(
                firstReq, listener));
        long secondTaskId = mDownloadManager.addTask(new EosDownloadTask(
                secondReq, listener));
        long thirdTaskId = mDownloadManager.addTask(new EosDownloadTask(
                thirdReq, listener));
        long fourthTaskId = mDownloadManager.addTask(new EosDownloadTask(
                fourthReq, listener));

        mListTaskIds.add(firstTaskId);
        mListTaskIds.add(secondTaskId);
        mListTaskIds.add(thirdTaskId);
        mListTaskIds.add(fourthTaskId);

        Integer DownloadTaskId = (int) System.currentTimeMillis() % 10000 ^ 0x19900101;
        Log.i(TAG, ">>>>>DownloadTaskId =" + DownloadTaskId);
        mLastRxBytes = TrafficStats.getTotalRxBytes();

        mDownloadTask.put(DownloadTaskId, mLastRxBytes);

        mDownloadSpeedTimer = new Timer();
        mDownloadSpeedTimer.schedule(new TimerTask() {
            @Override
            public void run() {
                calculateDownloadSpeed();
                Log.i(TAG, ">>>>>mCountTime = " + mCountTime);
                mHandler.sendEmptyMessageDelayed(MSG_CALCULATE_DOWNLOAD_SPEED,
                        0);
                Log.i(TAG, ">>>>>send message MSG_CALCULATE_DOWNLOAD_SPEED");
            }
        }, 1000, 1000);

        mTask++;

        return DownloadTaskId;
    }

    public void stopDownload() {
        Log.i(TAG, ">>>>>>Enter into stopDownload");
        if (null == mDownloadManager) {
            Log.e(TAG, "Error-->DownloadManager did not init.");
            return;
        }

        mDownloadSpeedTimer.cancel();
        for (Long taskId : mListTaskIds) {
            mDownloadManager.removeTask(taskId);
        }
    }

    private void refreshDownloadData(boolean complete) {
        caculateDownloadProgress(complete);
            mDownloadSpeedListener.onDownloadSpeedChanged(
                    mMaxDownloadSpeed, mMinDownloadSpeed,
                    getAvgDownloadSpeed(mCurrentTaskId), complete);
        mDownloadSpeedListener.onDownloadProgress(mDownloadPercent);
    }

    private void calculateDownloadSpeed() {
        Log.i(TAG, ">>>>>Enter into calculateDownloadSpeed");
        mDownloadDuration++;
        long currRxBytes = TrafficStats.getTotalRxBytes();
        Log.d(TAG, "currRxBytes-->" + currRxBytes + ",\t mLastRxBytes-->"
                + mLastRxBytes);
        int downloadSpeed = (int) (currRxBytes - mLastRxBytes);
        mLastRxBytes = currRxBytes;

        if (mMaxDownloadSpeed == mMinDownloadSpeed && mMinDownloadSpeed == 0) {
            Log.d(TAG, "init speed --> " + downloadSpeed);
            mMaxDownloadSpeed = mMinDownloadSpeed = downloadSpeed;
            return;
        }

        if (downloadSpeed > mMaxDownloadSpeed) {
            mMaxDownloadSpeed = downloadSpeed;
        } else if (downloadSpeed < mMinDownloadSpeed) {
            mMinDownloadSpeed = downloadSpeed;
        }
        Log.d(TAG, mDownloadDuration + "-->\t min:" + mMinDownloadSpeed
                + ",\t curr:" + downloadSpeed + ",\t Max:" + mMaxDownloadSpeed);
    }

    private int getAvgDownloadSpeed(Integer downloadTaskId) {
        long rxBytes = mDownloadTask.get(downloadTaskId);
        if (rxBytes > 0 && mDownloadDuration > 0) {
            int downloadAvg = (int) ((TrafficStats.getTotalRxBytes() - rxBytes) / mDownloadDuration);
            Log.d(TAG, "downloadAvg = " + downloadAvg);
            return downloadAvg;
        }
        return -1;
    }

    private void caculateDownloadProgress(boolean complete) {
        Log.d(TAG, ">>>>caculateProgress");
        int donwloadAvg = getAvgDownloadSpeed(mCurrentTaskId);
        int futureDownloadRxBytes = donwloadAvg * DOWNLOAD_TIME_LIMIT / 1000;

        int progress = 0;
        if (complete) {
            progress = 100;
        } else {
            progress = donwloadAvg * mCountTime / DOWNLOAD_URI_TOTAL_RX_BYTES;
        }
        Log.i(TAG, ">>>>futureDownloadRxBytes =  " + futureDownloadRxBytes * 30);
        mCountTime = mCountTime + 1;
        if (donwloadAvg * 30 < DOWNLOAD_URI_TOTAL_RX_BYTES) {
            Log.i(TAG, ">>>> the progress get from time percent");
            if (mCountTime * 100 > DOWNLOAD_TIME_LIMIT) {
                mCountTime = 0;
            }
            Log.i(TAG, ">>>> mCountTime*100/30 = " + mCountTime * 100 / 30);
            if (mCountTime * 100 / 30 < progress) {
                mDownloadPercent = progress;
            } else {
                mDownloadPercent = mCountTime * 100 / 30;
            }
        } else {
            Log.i(TAG, ">>>> the progress get from download percent");
            mDownloadPercent = progress;
        }
    }
}
