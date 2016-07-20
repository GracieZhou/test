
package com.bq.tv.task.device;

/**
 *  A class to describable the storage.
 */
public class StorageItem {

    private String storagePath;

    private String label;

    /**
     * integer to show MOUNTED status.
     */
    public static int MOUNTED = 0;

    /**
     * integer to show UNMOUNTING status.
     */
    public static int UNMOUNTING = 1;

    /**
     * integer to show UNMOUNTED status.
     */
    public static int UNMOUNTED = 2;

    private int mCurrentMountStatu = MOUNTED;

    /**
     * Get current mount status.
     * @return
     */
    public int getCurrentMountStatu() {
        return mCurrentMountStatu;
    }

    /**
     * Set current mount status.
     * @param mCurrentMountStatu
     */
    public void setCurrentMountStatu(int mCurrentMountStatu) {
        this.mCurrentMountStatu = mCurrentMountStatu;
    }

    /**
     * Get storage path.
     * @return
     */
    public String getStoragePath() {
        return storagePath;
    }

    /**
     * Set storage path.
     * @param storagePath
     */
    public void setStoragePath(String storagePath) {
        this.storagePath = storagePath;
    }

    /**
     * Get label.
     * @return
     */
    public String getLabel() {
        return label;
    }

    /**
     * Set label.
     * @param label
     */
    public void setLabel(String label) {
        this.label = label;
    }

}
