
package com.android.settings.update;

/**
 * This class contains update description.
 * 
 * @author Psso.Song
 */
public class DisplayInfo {
    private String version;

    private String description;

    private String time;

    private long size;

    /**
     * Get the version.
     * 
     * @return the version.
     */
    public String getVersion() {
        return version;
    }

    /**
     * Get the description.
     * 
     * @return the description.
     */
    public String getDescription() {
        return description;
    }

    /**
     * Get the time.
     * 
     * @return the time string
     */
    public String getTime() {
        return time;
    }

    /**
     * Get the size.
     * 
     * @return the size
     */
    public long getSize() {
        return size;
    }

    /**
     * Set version.
     * 
     * @param version:version
     */
    public void setVersion(String version) {
        this.version = version;
    }

    /**
     * Set description.
     * 
     * @param description description string
     */
    public void setDescription(String description) {
        this.description = description;
    }

    /**
     * Set time.
     * 
     * @param time:string
     */
    public void setTime(String time) {
        this.time = time;
    }

    /**
     * Set size.
     * 
     * @param size:long
     */
    public void setSize(long size) {
        this.size = size;
    }
}
