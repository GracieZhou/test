package scifly.filetransfer;

import java.util.HashMap;

/**
 * @author Psso.Song
 * @since Framework-ext API 2.0
 * 
 * This class is a wrapper of the upload task.
 *
 */
public class EosUploadTask {
    private int mMethod;
    private String mPath;
    private String mUrl;
    private HashMap<String, String> mParam;
    private EosUploadListener mListener;

    /**
     * @param path file absolute path that ready to upload
     * @param url server path
     */
    public EosUploadTask(String path, String url) {
        mPath = path;
        mUrl = url;
    }

    /**
     * @param method upload method
     * @param path file absolute path that ready to upload
     * @param url server path
     * @param param other params need by this upload
     */
    public EosUploadTask(int method, String path, String url,
            HashMap<String, String> param) {
        this(path, url);
        mMethod = method;
        mParam = param;
    }

    /**
     * @param method method upload method
     * @param path file absolute path that ready to upload
     * @param url server path
     * @param param other params need by this upload
     * @param listener listener that monitor current upload
     */
    public EosUploadTask(int method, String path, String url,
            HashMap<String, String> param,
            EosUploadListener listener) {
        this(method, path, url, param);
        mListener = listener;
    }

    /**
     * @param method upload method
     */
    public final void setMethod(int method) {
        mMethod = method;
    }

    /**
     * @param path file absolute path that ready to upload
     */
    public final void setPath(String path) {
        mPath = path;
    }

    /**
     * @param url server path
     */
    public final void setUrl(String url) {
        mUrl = url;
    }

    /**
     * @param param other params need by this upload
     */
    public final void setParam(HashMap<String, String> param) {
        mParam = param;
    }

    /**
     * @param listener listener that monitor current upload
     */
    public final void setListener(EosUploadListener listener) {
        mListener = listener;
    }

    /**
     * @return current upload method
     */
    public final int getMethod() {
        return mMethod;
    }

    /**
     * @return current file absolute path that ready to upload
     */
    public final String getPath() {
        return mPath;
    }

    /**
     * @return current server path
     */
    public final String getUrl() {
        return mUrl;
    }

    /**
     * @return other params of current upload
     */
    public final HashMap<String, String> getParam() {
        return mParam;
    }

    /**
     * @return listener that monitor current upload
     */
    public final EosUploadListener getListener() {
        return mListener;
    }
}
