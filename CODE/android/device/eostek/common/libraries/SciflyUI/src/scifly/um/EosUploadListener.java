package scifly.um;

/**
 * @author Psso.Song
 * @since Framework-ext API 2.0
 *
 */
public interface EosUploadListener {
    /**
     * @param errCode result of current upload
     */
    void onUploadCompleted(int errCode);
}
