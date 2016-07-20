
package scifly.filetransfer;
import android.app.DownloadManager.Request;

/**
 * This class is a wrapper of the download task, contains a
 * {@link android.app.DownloadManager.Request} and a EosDownloadListener.
 * 
 * @author Psso.Song
 * @since Framework-ext API 2.0
 */
public class EosDownloadTask {
    private long mTaskId;

    private int mLastStatus = -1;

    private Request mRequest;

    private EosDownloadListener mListener;

    /**
     * @param request {@link android.app.DownloadManager.Request}
     */
    public EosDownloadTask(Request request) {
        if (request != null) {
            mRequest = request;
        }
    }

    /**
     * @param request {@link android.app.DownloadManager.Request}
     * @param listener listen current download progress
     */
    public EosDownloadTask(Request request, EosDownloadListener listener) {
        this(request);
        if (listener != null) {
            mListener = listener;
        }
    }

    /**
     * @param id taskId of existed download task
     * @param listener listen current download progress
     */
    public EosDownloadTask(long id, EosDownloadListener listener) {
        mTaskId = id;
        if (listener != null) {
            mListener = listener;
        }
    }

    /**
     * @param taskId set taskId to current task
     */
    protected final void setTaskId(long taskId) {
        mTaskId = taskId;
    }

    /**
     * @param status set last status of current download
     */
    protected final void setLastStatus(int status) {
        mLastStatus = status;
    }

    /**
     * @param request set request to current task
     */
    public final void setRequest(Request request) {
        if (request != null) {
            mRequest = request;
        }
    }

    /**
     * @param listener set listener to current task
     */
    public final void setListener(EosDownloadListener listener) {
        if (listener != null) {
            mListener = listener;
        }
    }

    /**
     * @return current download id
     */
    public final long getTaskId() {
        return mTaskId;
    }

    /**
     * @return last status of current download
     */
    public final int getLastStatus() {
        return mLastStatus;
    }

    /**
     * @return current download request
     */
    public final Request getRequest() {
        return mRequest;
    }

    /**
     * @return current listener
     */
    public final EosDownloadListener getListener() {
        return mListener;
    }
}
