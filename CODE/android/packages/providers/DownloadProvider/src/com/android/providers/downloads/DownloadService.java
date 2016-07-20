/*
 * Copyright (C) 2008 The Android Open Source Project
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *      http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.android.providers.downloads;

import static android.text.format.DateUtils.MINUTE_IN_MILLIS;
import static com.android.providers.downloads.Constants.TAG;

import android.app.AlarmManager;
import android.app.DownloadManager;
import android.app.PendingIntent;
import android.app.Service;
import android.app.job.JobInfo;
import android.app.job.JobScheduler;
import android.content.ComponentName;
import android.content.ContentResolver;
import android.content.Context;
import android.content.Intent;
import android.content.res.Resources;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Message;
import android.os.Process;
import android.provider.Downloads;
import android.text.TextUtils;
import android.util.Log;

import com.android.internal.annotations.GuardedBy;
import com.android.internal.util.IndentingPrintWriter;
import com.google.android.collect.Maps;
import com.google.common.annotations.VisibleForTesting;
import com.google.common.collect.Lists;
import com.google.common.collect.Sets;

import java.io.File;
import java.io.FileDescriptor;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.concurrent.CancellationException;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Future;
import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;
//EosTek Patch Begin
import java.util.concurrent.Executors;
import android.content.ContentUris;
import android.content.ContentValues;

//EosTek Patch End

/**
 * Performs background downloads as requested by applications that use
 * {@link DownloadManager}. Multiple start commands can be issued at this
 * service, and it will continue running until no downloads are being actively
 * processed. It may schedule alarms to resume downloads in future.
 * <p>
 * Any database updates important enough to initiate tasks should always be
 * delivered through {@link Context#startService(Intent)}.
 */
public class DownloadService extends Service {
    // TODO: migrate WakeLock from individual DownloadThreads out into
    // DownloadReceiver to protect our entire workflow.

    private static final boolean DEBUG_LIFECYCLE = false;

    @VisibleForTesting
    SystemFacade mSystemFacade;

    private AlarmManager mAlarmManager;

    /** Observer to get notified when the content observer's data changes */
    private DownloadManagerContentObserver mObserver;

    /** Class to handle Notification Manager updates */
    private DownloadNotifier mNotifier;

    /** Scheduling of the periodic cleanup job */
    private JobInfo mCleanupJob;

    private static final int CLEANUP_JOB_ID = 1;
    private static final long CLEANUP_JOB_PERIOD = 1000 * 60 * 60 * 24; // one day
    private static ComponentName sCleanupServiceName = new ComponentName(
            DownloadIdleService.class.getPackage().getName(),
            DownloadIdleService.class.getName());

    /**
     * The Service's view of the list of downloads, mapping download IDs to the corresponding info
     * object. This is kept independently from the content provider, and the Service only initiates
     * downloads based on this data, so that it can deal with situation where the data in the
     * content provider changes or disappears.
     */
    @GuardedBy("mDownloads")
    private final Map<Long, DownloadInfo> mDownloads = Maps.newHashMap();

    private final ExecutorService mExecutor = buildDownloadExecutor();

    private static ExecutorService buildDownloadExecutor() {
        final int maxConcurrent = Resources.getSystem().getInteger(
                com.android.internal.R.integer.config_MaxConcurrentDownloadsAllowed);

        // Create a bounded thread pool for executing downloads; it creates
        // threads as needed (up to maximum) and reclaims them when finished.
        final ThreadPoolExecutor executor = new ThreadPoolExecutor(
                maxConcurrent, maxConcurrent, 10, TimeUnit.SECONDS,
                new LinkedBlockingQueue<Runnable>()) {
            @Override
            protected void afterExecute(Runnable r, Throwable t) {
                super.afterExecute(r, t);

                if (t == null && r instanceof Future<?>) {
                    try {
                        ((Future<?>) r).get();
                    } catch (CancellationException ce) {
                        t = ce;
                    } catch (ExecutionException ee) {
                        t = ee.getCause();
                    } catch (InterruptedException ie) {
                        Thread.currentThread().interrupt();
                    }
                }

                if (t != null) {
                    Log.w(TAG, "Uncaught exception", t);
                }
            }
        };
        executor.allowCoreThreadTimeOut(true);
        return executor;
    }

    private DownloadScanner mScanner;

    private HandlerThread mUpdateThread;
    private Handler mUpdateHandler;

    private volatile int mLastStartId;

    // EosTek Patch Begin
    private DownloadSettingContentObserver mSettingObserver;

    private int mDownloadSpeedLimite = 1024 * 10;

    private int mDownloadNumber = 5;

    public int getDownloadSpeedLimite() {
        return mDownloadSpeedLimite;
    }

    /**
     * @Title: getDownloadNumber.
     * @Description: the limit number of tasks .
     * @param: @return.
     * @return: int.
     * @throws
     */
    public int getDownloadNumber() {
        return mDownloadNumber;
    }

    // EosTek Patch End
    /**
     * Receives notifications when the data in the content provider changes
     */
    private class DownloadManagerContentObserver extends ContentObserver {
        public DownloadManagerContentObserver() {
            super(new Handler());
        }

        @Override
        public void onChange(final boolean selfChange) {
            enqueueUpdate();
        }
    }

    // EosTek patch Begin
    /**
     * @ClassName: DownloadSettingContentObserver.
     * @Description:Receives notifications when the data in the content provider
     *                       changes.
     * @author: lucky.li.
     * @date: Nov 6, 2015 3:14:35 PM.
     * @Copyright: Eostek Co., Ltd. Copyright , All rights reserved.
     */
    private class DownloadSettingContentObserver extends ContentObserver {

        public DownloadSettingContentObserver() {
            super(new Handler());
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
	
	    Log.e("update","==========DownloadSettingContentObserver===========");
            getSetting();
            checkRunningTaskNumber();
        }
    }

    /**
     * @Title: getSetting.
     * @Description: query DownloadSetting.
     * @param: .
     * @return: void.
     * @throws
     */
    public void getSetting() {
        ContentResolver resolver = getContentResolver();
        Cursor underlyingCursor = resolver.query(Constants.SETTING_URL, null, null, null, null);
        if (underlyingCursor == null)
            return;
        if (underlyingCursor.getCount() < 1)
            return;
        if (underlyingCursor.moveToFirst()) {

            int index = underlyingCursor.getColumnIndex(Constants.COLUMN_SPEED_LIMITE);
            if (index >= 0)
                mDownloadSpeedLimite = underlyingCursor.getInt(index);

            index = underlyingCursor.getColumnIndex(Constants.COLUMN_TASK_NUMBER);
            if (index >= 0)
                mDownloadNumber = underlyingCursor.getInt(index);
		Log.e("update","mDownloadNumber : " + mDownloadNumber);
        }
        underlyingCursor.close();
    }

    private void checkRunningTaskNumber() {
        int runningTask = ((ThreadPoolExecutor) mExecutor).getActiveCount();
	Log.e("update","runningTask =====>"+runningTask);
        int differId = runningTask - mDownloadNumber;
        if (differId > 0) {
            if (runningTask > mDownloadNumber) {
                List<DownloadBean> lists = getAllDownload(Downloads.Impl.STATUS_RUNNING);
		Log.e("update","lists ==>"+lists.size());
                Collections.sort(lists);
                for (int i = 0; i < differId; i++) {
                    update(lists.get(i).getId());
                }
            }
        }
    }

    private List<DownloadBean> getAllDownload(int DownloadState) {
        ArrayList<DownloadBean> list = new ArrayList<DownloadBean>();
        ContentResolver resolver = getContentResolver();
        Cursor underlyingCursor = runQuery(resolver, null, Downloads.Impl.ALL_DOWNLOADS_CONTENT_URI, DownloadState);
        if (underlyingCursor == null || underlyingCursor.getCount() < 1) {
            return null;
        }
        if (underlyingCursor.moveToFirst()) {
            do {
                DownloadBean dl = new DownloadBean();
                int index = underlyingCursor.getColumnIndex(Downloads.Impl._ID);
                if (index >= 0) {
                    dl.setId(underlyingCursor.getLong(index));
                }

                index = underlyingCursor.getColumnIndex(DownloadProvider.COLUMN_CREATE_TIME);
                if (index >= 0) {
                    dl.setCreateTime(underlyingCursor.getLong(index));
                }

                index = underlyingCursor.getColumnIndex(Downloads.Impl.COLUMN_STATUS);
                if (index >= 0) {
                    dl.setDownloadState(underlyingCursor.getInt(index));
                }

                index = underlyingCursor.getColumnIndex(Downloads.Impl.COLUMN_CONTROL);
                if (index >= 0) {
                    dl.setControlRun(underlyingCursor.getInt(index));
                }
                list.add(dl);

            } while (underlyingCursor.moveToNext());
        }

        underlyingCursor.close();

        return list;
    }

    private Cursor runQuery(ContentResolver resolver, String[] projection, Uri baseUri, int statusFlags) {
        Uri uri = baseUri;
        ArrayList<String> selectionParts = new ArrayList<String>();
        String[] selectionArgs = null;

        if (statusFlags >= 0) {
            ArrayList<String> parts = new ArrayList<String>();

            if (Downloads.Impl.STATUS_PENDING == statusFlags) {
                parts.add(statusClause("=", Downloads.Impl.STATUS_PENDING));
            }

            if (Downloads.Impl.STATUS_RUNNING == statusFlags) {
                parts.add(statusClause("=", Downloads.Impl.STATUS_RUNNING));
            }

            if (Downloads.Impl.STATUS_SUCCESS == statusFlags) {
                parts.add(statusClause("=", Downloads.Impl.STATUS_SUCCESS));
            }

            if (Downloads.Impl.STATUS_PAUSED_BY_APP == statusFlags) {
                parts.add(statusClause("=", Downloads.Impl.STATUS_PAUSED_BY_APP));
                parts.add(statusClause("=", Downloads.Impl.STATUS_WAITING_TO_RETRY));
                parts.add(statusClause("=", Downloads.Impl.STATUS_WAITING_FOR_NETWORK));
                parts.add(statusClause("=", Downloads.Impl.STATUS_QUEUED_FOR_WIFI));
            }

            selectionParts.add(joinStrings(" OR ", parts));
        }

        // only return rows which are not marked 'deleted = 1'
        selectionParts.add(Downloads.Impl.COLUMN_DELETED + " != '1'");

        String selection = joinStrings(" AND ", selectionParts);

        return resolver.query(uri, projection, selection, selectionArgs, null);
    }

    private String joinStrings(String joiner, Iterable<String> parts) {
        StringBuilder builder = new StringBuilder();
        boolean first = true;
        for (String part : parts) {
            if (!first) {
                builder.append(joiner);
            }
            builder.append(part);
            first = false;
        }
        return builder.toString();
    }

    private String statusClause(String operator, int value) {
        return Downloads.Impl.COLUMN_STATUS + operator + "'" + value + "'";
    }

    private boolean update(long id) {
        ContentResolver resolver = getContentResolver();
        ContentValues values = new ContentValues();
        values.put(Downloads.Impl.COLUMN_CONTROL, Downloads.Impl.CONTROL_PAUSED);
        values.put(Downloads.Impl.COLUMN_STATUS, Downloads.Impl.STATUS_PAUSED_BY_APP);
        Uri temp = ContentUris.withAppendedId(Downloads.Impl.ALL_DOWNLOADS_CONTENT_URI, id);
        int nRet = resolver.update(temp, values, null, null);
	Log.e("update","=============resolver.update===================");
        if (nRet >= 1)
            return true;
        else
            return false;
    }

    // EosTek patch End

    /**
     * Returns an IBinder instance when someone wants to connect to this
     * service. Binding to this service is not allowed.
     * 
     * @throws UnsupportedOperationException
     */
    @Override
    public IBinder onBind(Intent i) {
        throw new UnsupportedOperationException("Cannot bind to Download Manager Service");
    }

    /**
     * Initializes the service when it is first created
     */
    @Override
    public void onCreate() {
        super.onCreate();
        if (Constants.LOGVV) {
            Log.v(Constants.TAG, "Service onCreate");
        }

        if (mSystemFacade == null) {
            mSystemFacade = new RealSystemFacade(this);
        }

        mAlarmManager = (AlarmManager) getSystemService(Context.ALARM_SERVICE);

        mUpdateThread = new HandlerThread(TAG + "-UpdateThread");
        mUpdateThread.start();
        mUpdateHandler = new Handler(mUpdateThread.getLooper(), mUpdateCallback);

        mScanner = new DownloadScanner(this);

        mNotifier = new DownloadNotifier(this);
        
        // EosTek Patch Begin
        // mNotifier.cancelAll();
        // EosTek Patch End

        mObserver = new DownloadManagerContentObserver();
        getContentResolver().registerContentObserver(Downloads.Impl.ALL_DOWNLOADS_CONTENT_URI,
                true, mObserver);
		// EosTek Patch Begin
        mSettingObserver = new DownloadSettingContentObserver();
        getContentResolver().registerContentObserver(Constants.SETTING_URL, true, mSettingObserver);
        // EosTek Patch End
        JobScheduler js = (JobScheduler) getSystemService(Context.JOB_SCHEDULER_SERVICE);
        if (needToScheduleCleanup(js)) {
            final JobInfo job = new JobInfo.Builder(CLEANUP_JOB_ID, sCleanupServiceName)
                    .setPeriodic(CLEANUP_JOB_PERIOD)
                    .setRequiresCharging(true)
                    .setRequiresDeviceIdle(true)
                    .build();
            js.schedule(job);
        }
    }

    private boolean needToScheduleCleanup(JobScheduler js) {
        List<JobInfo> myJobs = js.getAllPendingJobs();
        final int N = myJobs.size();
        for (int i = 0; i < N; i++) {
            if (myJobs.get(i).getId() == CLEANUP_JOB_ID) {
                // It's already been (persistently) scheduled; no need to do it again
                return false;
            }
        }
        return true;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        int returnValue = super.onStartCommand(intent, flags, startId);
        if (Constants.LOGVV) {
            Log.v(Constants.TAG, "Service onStart");
        }
        mLastStartId = startId;
        enqueueUpdate();
        // EosTek Patch Begin
        getSetting();
        // EosTek Patch End
        return returnValue;
    }

    @Override
    public void onDestroy() {
        getContentResolver().unregisterContentObserver(mObserver);
        // EosTek Patch Begin
        getContentResolver().unregisterContentObserver(mSettingObserver);
        // EosTek Patch End
        mScanner.shutdown();
        mUpdateThread.quit();
        if (Constants.LOGVV) {
            Log.v(Constants.TAG, "Service onDestroy");
        }
        super.onDestroy();
    }

    /**
     * Enqueue an {@link #updateLocked()} pass to occur in future.
     */
    public void enqueueUpdate() {
        if (mUpdateHandler != null) {
            mUpdateHandler.removeMessages(MSG_UPDATE);
            mUpdateHandler.obtainMessage(MSG_UPDATE, mLastStartId, -1).sendToTarget();
        }
    }

    /**
     * Enqueue an {@link #updateLocked()} pass to occur after delay, usually to
     * catch any finished operations that didn't trigger an update pass.
     */
    private void enqueueFinalUpdate() {
        mUpdateHandler.removeMessages(MSG_FINAL_UPDATE);
        mUpdateHandler.sendMessageDelayed(
                mUpdateHandler.obtainMessage(MSG_FINAL_UPDATE, mLastStartId, -1),
                5 * MINUTE_IN_MILLIS);
    }

    private static final int MSG_UPDATE = 1;
    private static final int MSG_FINAL_UPDATE = 2;

    private Handler.Callback mUpdateCallback = new Handler.Callback() {
        @Override
        public boolean handleMessage(Message msg) {
            Process.setThreadPriority(Process.THREAD_PRIORITY_BACKGROUND);

            final int startId = msg.arg1;
            if (DEBUG_LIFECYCLE) Log.v(TAG, "Updating for startId " + startId);

            // Since database is current source of truth, our "active" status
            // depends on database state. We always get one final update pass
            // once the real actions have finished and persisted their state.

            // TODO: switch to asking real tasks to derive active state
            // TODO: handle media scanner timeouts

            final boolean isActive;
            synchronized (mDownloads) {
                isActive = updateLocked();
            }

            if (msg.what == MSG_FINAL_UPDATE) {
                // Dump thread stacks belonging to pool
                for (Map.Entry<Thread, StackTraceElement[]> entry :
                        Thread.getAllStackTraces().entrySet()) {
                    if (entry.getKey().getName().startsWith("pool")) {
                        Log.d(TAG, entry.getKey() + ": " + Arrays.toString(entry.getValue()));
                    }
                }

                // Dump speed and update details
                // EosTek Patch Begin
                // mNotifier.dumpSpeeds();
                // EosTek Patch End

                Log.wtf(TAG, "Final update pass triggered, isActive=" + isActive
                        + "; someone didn't update correctly.");
            }

            if (isActive) {
                // Still doing useful work, keep service alive. These active
                // tasks will trigger another update pass when they're finished.

                // Enqueue delayed update pass to catch finished operations that
                // didn't trigger an update pass; these are bugs.
                enqueueFinalUpdate();

            } else {
                // No active tasks, and any pending update messages can be
                // ignored, since any updates important enough to initiate tasks
                // will always be delivered with a new startId.

                if (stopSelfResult(startId)) {
                    if (DEBUG_LIFECYCLE) Log.v(TAG, "Nothing left; stopped");
                    getContentResolver().unregisterContentObserver(mObserver);
                    mScanner.shutdown();
                    mUpdateThread.quit();
                }
            }

            return true;
        }
    };

    /**
     * Update {@link #mDownloads} to match {@link DownloadProvider} state.
     * Depending on current download state it may enqueue {@link DownloadThread}
     * instances, request {@link DownloadScanner} scans, update user-visible
     * notifications, and/or schedule future actions with {@link AlarmManager}.
     * <p>
     * Should only be called from {@link #mUpdateThread} as after being
     * requested through {@link #enqueueUpdate()}.
     *
     * @return If there are active tasks being processed, as of the database
     *         snapshot taken in this update.
     */
    private boolean updateLocked() {
        final long now = mSystemFacade.currentTimeMillis();

        boolean isActive = false;
        long nextActionMillis = Long.MAX_VALUE;

        final Set<Long> staleIds = Sets.newHashSet(mDownloads.keySet());

        final ContentResolver resolver = getContentResolver();
        final Cursor cursor = resolver.query(Downloads.Impl.ALL_DOWNLOADS_CONTENT_URI,
                null, null, null, null);
        try {
            final DownloadInfo.Reader reader = new DownloadInfo.Reader(resolver, cursor);
            final int idColumn = cursor.getColumnIndexOrThrow(Downloads.Impl._ID);
            while (cursor.moveToNext()) {
                final long id = cursor.getLong(idColumn);
                staleIds.remove(id);

                DownloadInfo info = mDownloads.get(id);
                if (info != null) {
                    updateDownload(reader, info, now);
                } else {
                    info = insertDownloadLocked(reader, now);
                }

                if (info.mDeleted) {
                    // Delete download if requested, but only after cleaning up
                    if (!TextUtils.isEmpty(info.mMediaProviderUri)) {
                        resolver.delete(Uri.parse(info.mMediaProviderUri), null, null);
                    }

                    deleteFileIfExists(info.mFileName);
                    resolver.delete(info.getAllDownloadsUri(), null, null);

                } else {
                    // Kick off download task if ready
                    final boolean activeDownload = info.startDownloadIfReady(mExecutor);

                    // Kick off media scan if completed
                    final boolean activeScan = info.startScanIfReady(mScanner);

                    if (DEBUG_LIFECYCLE && (activeDownload || activeScan)) {
                        Log.v(TAG, "Download " + info.mId + ": activeDownload=" + activeDownload
                                + ", activeScan=" + activeScan);
                    }

                    isActive |= activeDownload;
                    isActive |= activeScan;
                }

                // Keep track of nearest next action
                nextActionMillis = Math.min(info.nextActionMillis(now), nextActionMillis);
            }
        } finally {
            cursor.close();
        }

        // Clean up stale downloads that disappeared
        for (Long id : staleIds) {
            deleteDownloadLocked(id);
        }

        // Update notifications visible to user
        // EosTek Patch Begin
        // mNotifier.updateWith(mDownloads.values());
        // EosTek Patch End

        // Set alarm when next action is in future. It's okay if the service
        // continues to run in meantime, since it will kick off an update pass.
        if (nextActionMillis > 0 && nextActionMillis < Long.MAX_VALUE) {
            if (Constants.LOGV) {
                Log.v(TAG, "scheduling start in " + nextActionMillis + "ms");
            }

            final Intent intent = new Intent(Constants.ACTION_RETRY);
            intent.setClass(this, DownloadReceiver.class);
            mAlarmManager.set(AlarmManager.RTC_WAKEUP, now + nextActionMillis,
                    PendingIntent.getBroadcast(this, 0, intent, PendingIntent.FLAG_ONE_SHOT));
        }

        return isActive;
    }

    /**
     * Keeps a local copy of the info about a download, and initiates the
     * download if appropriate.
     */
    private DownloadInfo insertDownloadLocked(DownloadInfo.Reader reader, long now) {
        final DownloadInfo info = reader.newDownloadInfo(this, mSystemFacade, mNotifier);
        mDownloads.put(info.mId, info);

        if (Constants.LOGVV) {
            Log.v(Constants.TAG, "processing inserted download " + info.mId);
        }

        return info;
    }

    /**
     * Updates the local copy of the info about a download.
     */
    private void updateDownload(DownloadInfo.Reader reader, DownloadInfo info, long now) {
        reader.updateFromDatabase(info);
        if (Constants.LOGVV) {
            Log.v(Constants.TAG, "processing updated download " + info.mId +
                    ", status: " + info.mStatus);
        }
    }

    /**
     * Removes the local copy of the info about a download.
     */
    private void deleteDownloadLocked(long id) {
        DownloadInfo info = mDownloads.get(id);
        if (info.mStatus == Downloads.Impl.STATUS_RUNNING) {
            info.mStatus = Downloads.Impl.STATUS_CANCELED;
        }
        if (info.mDestination != Downloads.Impl.DESTINATION_EXTERNAL && info.mFileName != null) {
            if (Constants.LOGVV) {
                Log.d(TAG, "deleteDownloadLocked() deleting " + info.mFileName);
            }
            deleteFileIfExists(info.mFileName);
        }
        mDownloads.remove(info.mId);
    }

    private void deleteFileIfExists(String path) {
        if (!TextUtils.isEmpty(path)) {
            if (Constants.LOGVV) {
                Log.d(TAG, "deleteFileIfExists() deleting " + path);
            }
            final File file = new File(path);
            if (file.exists() && !file.delete()) {
                Log.w(TAG, "file: '" + path + "' couldn't be deleted");
            }
        }
    }

    @Override
    protected void dump(FileDescriptor fd, PrintWriter writer, String[] args) {
        final IndentingPrintWriter pw = new IndentingPrintWriter(writer, "  ");
        synchronized (mDownloads) {
            final List<Long> ids = Lists.newArrayList(mDownloads.keySet());
            Collections.sort(ids);
            for (Long id : ids) {
                final DownloadInfo info = mDownloads.get(id);
                info.dump(pw);
            }
        }
    }
}
