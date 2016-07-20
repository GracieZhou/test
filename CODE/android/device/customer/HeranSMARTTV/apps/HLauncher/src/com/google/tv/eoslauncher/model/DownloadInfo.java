package com.google.tv.eoslauncher.model;

/**
 * storage the download info
 * 
 * @author Vicent
 */
public class DownloadInfo {
    
    private long downloadID;
    
    private int downloadState;
    
    // download uri
    private String uri;
    
    // present download bytes
    private long presentBytes;
    
    // total bytes;
    private long totalBytes;

    public DownloadInfo(String uri, long presentBytes, long totalBytes) {
        super();
        this.uri = uri;
        this.presentBytes = presentBytes;
        this.totalBytes = totalBytes;
    }

    public DownloadInfo() {
        super();
    }

    public String getUri() {
        return uri;
    }

    public void setUri(String uri) {
        this.uri = uri;
    }

    public long getPresentBytes() {
        return presentBytes;
    }

    public void setPresentBytes(long presentBytes) {
        this.presentBytes = presentBytes;
    }

    public long getTotalBytes() {
        return totalBytes;
    }

    public void setTotalBytes(long totalBytes) {
        this.totalBytes = totalBytes;
    }

    public long getDownloadID() {
        return downloadID;
    }

    public void setDownloadID(long downloadID) {
        this.downloadID = downloadID;
    }

    public int getDownloadState() {
        return downloadState;
    }

    public void setDownloadState(int downloadState) {
        this.downloadState = downloadState;
    }
    
}
