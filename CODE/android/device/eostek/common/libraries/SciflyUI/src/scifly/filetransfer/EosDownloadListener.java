package scifly.filetransfer;

/**
 * This interface should be used for monitoring current download progress.
 * 
 * @author Psso.Song
 * @since Framework-ext API 2.0
 */
public interface EosDownloadListener {
    /**
     * @param size bytes that has been downloaded
     */
    void onDownloadSize(long size);

    /**
     * @param percent 0-100 percent that has been downloaded
     */
    void onDownloadComplete(int percent);

    /**
     * @param status latest download status
     */
    void onDownloadStatusChanged(int status);
}
