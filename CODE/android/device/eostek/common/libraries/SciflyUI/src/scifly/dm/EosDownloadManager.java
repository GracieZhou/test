
package scifly.dm;

import java.io.File;
import android.app.DownloadManager;
import android.app.DownloadManager.Query;
import android.content.Context;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;
import android.util.Log;
import android.util.LongSparseArray;

/**
 * This download manager is a wrapper of {@link android.app.DownloadManager}.
 * Instances of this class should be obtained through the constructor with param
 * {@link android.content.Context} EosDownloadManager manager = new
 * EosDownloadManager(context); Note that the application must have the
 * {@link android.Manifest.permission#INTERNET} and
 * {@link android.Manifest.permission#WRITE_EXTERNAL_STORAGE} permissions to use
 * this class.
 * 
 * @author Psso.Song
 * @since Framework-ext API 2.0
 */
public class EosDownloadManager {
    public static final String TAG = "EosDownloadManager";

    private static final Uri DOWNLOAD_URI = Uri.parse("content://downloads/my_downloads");

    private static final int ALMOST_MAX_PERCENT = 99;

    private static final int MAX_PERCENT = 100;

    private DownloadManager mDownloadManager;

    // Map that contains all the DownloadManager EosDownloadTasks.
    private LongSparseArray<EosDownloadTask> mTaskList;

    /**
     * @param context Application context
     */
    public EosDownloadManager(Context context) {
        if (context != null) {
            // Get DownloadManager.
            mDownloadManager = (DownloadManager) context.getSystemService(Context.DOWNLOAD_SERVICE);
            // Register ContentObserver on Database.
            context.getContentResolver().registerContentObserver(DOWNLOAD_URI, true, mContentObserver);
            // Get list of current download tasks.
            mTaskList = new LongSparseArray<EosDownloadTask>();
        } else {
            Log.e(TAG, "ERROR: Context is null!");
        }
    }

    /**
     * Monitor of DownloadManager task state changed.
     */
    private ContentObserver mContentObserver = new ContentObserver(null) {
        public void onChange(boolean selfChange, Uri uri) {
            Log.d(TAG, "selfChange=" + selfChange + ", uri=" + uri);
            long id = -1;
            try {
                id = Long.parseLong(uri.getLastPathSegment());
                handleContentChanged(id);
            } catch (NumberFormatException e) {
                Log.d(TAG, "Unknown uri received!");
            }
        };
    };

    private void addTaskIntoList(EosDownloadTask task) {
        synchronized (mTaskList) {
            mTaskList.put(task.getTaskId(), task);
        }
    }

    private void removeIdFromList(long id) {
        synchronized (mTaskList) {
            mTaskList.remove(id);
        }
    }

    /**
     * Handle DownloadManager task state changed.
     * 
     * @param id DownloadManager task id.
     */
    private void handleContentChanged(long id) {
        EosDownloadTask task = null;
        synchronized (mTaskList) {
            task = mTaskList.get(id);
        }
        if (task == null) {
            Log.d(TAG, "handleContentChanged [" + id + "] unknown taskId received");
            return;
        }
        EosDownloadListener listener = task.getListener();
        if (listener == null) {
            Log.d(TAG, "handlerContentChanged [" + id + "] no need to listener this task");
            return;
        }

        int status = getDownloadStatus(id);
        if (status != task.getLastStatus()) {
            Log.d(TAG, "handlerContentChanged [" + id + "] state changed from " + task.getLastStatus() + " to "
                    + status);
            listener.onDownloadStatusChanged(status);
            task.setLastStatus(status);
        }

        long size = getDownloadedSize(id);
        long total = getFileSize(id);
        int percent = -1;
        if (total > 0) {
            percent = Long.valueOf(size * MAX_PERCENT / total).intValue();
        }
        Log.d(TAG, "handlerContentChanged [" + id + "] size=" + size + ", total=" + total + ", percent=" + percent);
        if (percent >= MAX_PERCENT && status != DownloadManager.STATUS_SUCCESSFUL) {
            percent = ALMOST_MAX_PERCENT;
        } else if (status == DownloadManager.STATUS_SUCCESSFUL) {
            percent = MAX_PERCENT;
        }
        listener.onDownloadComplete(percent);
        listener.onDownloadSize(size);

        if (status == DownloadManager.STATUS_SUCCESSFUL) {
            Log.d(TAG, "handlerContentChanged [" + id + "] download completed, file exists ? "
                    + isDownloadedFileExist(id));
            removeIdFromList(id);
        }
    }

    /**
     * @param task request&listener of current download task
     * @return id of current task
     */
    public final long addTask(EosDownloadTask task) {
        if (task == null) {
            // Empty EosDownloadTask
            Log.e(TAG, "ERROR: Empty EosDownloadTask!");
            return -1;
        }

        if (task.getRequest() != null) {
            // DownloadManager task.
            if (mDownloadManager == null) {
                Log.e(TAG, "ERROR: EosDownloadManager NOT INITIALIZED");
                return -1;
            }
            long result = mDownloadManager.enqueue(task.getRequest());
            task.setTaskId(result);
            addTaskIntoList(task);
            return result;
        }

        return -1;
    }

    /**
     * @param id taskId of your download task
     * @param listener monitor on your download task
     * @return true is successful and false is failed
     */
    public final boolean restartTask(long id, EosDownloadListener listener) {
        if (mDownloadManager == null) {
            Log.e(TAG, "ERROR: EosDownloadManager NOT INITIALIZED");
            return false;
        }

        switch (getDownloadStatus(id)) {
            case DownloadManager.STATUS_FAILED:
            case DownloadManager.STATUS_SUCCESSFUL:
            case DownloadManager.STATUS_PAUSED:
                Log.d(TAG, "restart FAILED/SUCCESSFUL/PAUSED task [" + id + "]");
                mDownloadManager.restartDownload(id);
                break;
            case DownloadManager.STATUS_PENDING:
            case DownloadManager.STATUS_RUNNING:
                break;
            default:
                Log.e(TAG, "ERROR: Download " + id + " status unknown");
                return false;
        }

        if (listener != null) {
            EosDownloadTask task = new EosDownloadTask(id, listener);
            addTaskIntoList(task);
        }
        return true;
    }

    /**
     * @param taskId id of task that want's to remove
     * @return result
     */
    public final int removeTask(long id) {
        removeIdFromList(id);
        return mDownloadManager.remove(id);
    }

    /**
     * @param taskId id of task that want's to query
     * @return true if file exists, otherwise false.
     */
    public boolean isDownloadedFileExist(long taskId) {
        String filepath = "";
        Cursor c = mDownloadManager.query(new Query().setFilterById(taskId));
        if (c == null) {
            Log.e(TAG, "Task ID " + taskId + " not found!");
            return false;
        }
        if (c.moveToFirst()) {
            filepath = c.getString(1);
        }
        c.close();
        Log.d(TAG, "isDownloadedFileExist [" + taskId + "] path=[" + filepath + "]");
        return new File(filepath).exists();
    }

    /**
     * @param taskId id of task that want's to query
     * @return status of this task.
     */
    public int getDownloadStatus(long taskId) {
        int status = -1;
        Cursor c = mDownloadManager.query(new Query().setFilterById(taskId));
        if (c == null) {
            Log.e(TAG, "Task ID " + taskId + " not found!");
            return -1;
        }
        if (c.moveToFirst()) {
            status = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_STATUS));
        }
        c.close();
        return status;
    }

    /**
     * @param taskId taskId id of task that want's to query
     * @return reason
     */
    public final int getReason(long taskId) {
        int reason = -1;
        Cursor c = mDownloadManager.query(new Query().setFilterById(taskId));
        if (c == null) {
            Log.e(TAG, "Task ID " + taskId + " not found!");
            return -1;
        }
        if (c.moveToFirst()) {
            reason = c.getInt(c.getColumnIndex(DownloadManager.COLUMN_REASON));
        }
        c.close();
        return reason;
    }

    /**
     * @param taskId taskId id of task that want's to query
     * @return bytes of file
     */
    public final long getFileSize(long taskId) {
        long size = -1;
        Cursor c = mDownloadManager.query(new Query().setFilterById(taskId));
        if (c == null) {
            Log.e(TAG, "Task ID " + taskId + " not found!");
            return -1;
        }
        if (c.moveToFirst()) {
            size = c.getLong(c.getColumnIndex(DownloadManager.COLUMN_TOTAL_SIZE_BYTES));
        }
        c.close();
        return size;
    }

    /**
     * @param taskId taskId id of task that want's to query
     * @return bytes of file that has been downloaded
     */
    public final long getDownloadedSize(long taskId) {
        long size = -1;
        Cursor c = mDownloadManager.query(new Query().setFilterById(taskId));
        if (c == null) {
            Log.e(TAG, "Download ID " + taskId + " not found!");
            return -1;
        }
        if (c.moveToFirst()) {
            size = c.getLong(c.getColumnIndex(DownloadManager.COLUMN_BYTES_DOWNLOADED_SO_FAR));
        }
        c.close();
        return size;
    }
}
