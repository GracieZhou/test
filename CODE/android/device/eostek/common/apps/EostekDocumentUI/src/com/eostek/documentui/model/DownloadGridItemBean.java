
package com.eostek.documentui.model;

import java.io.Serializable;
import com.eostek.documentui.Constants;
import com.eostek.documentui.data.DataProxy;
import com.eostek.documentui.data.DownloadInfor;

/**
 * @ClassName: DownloadingGridItemBean.
 * @Description:DownloadingGridItemBean.
 * @author: alisa.xu.
 * @date: Sep 18, 2015 10:12:38 AM.
 * @Copyright: Eostek Co., Ltd. Copyright , All rights reserved.
 */
public class DownloadGridItemBean implements Comparable<DownloadGridItemBean> ,Serializable{

    private boolean isMutilDeleteMode = false;

    public boolean isMutilDeleteMode() {
        return isMutilDeleteMode;
    }

    public void setMutilDeleteMode(boolean isMutilDeleteMode) {
        this.isMutilDeleteMode = isMutilDeleteMode;
    }

    private String fullURL;

    private String mimetype;

    private String saveName;

    private int total_bytes = 1024;

    public int currentBytes = 0;

    /**
     * download speed
     */
    private int speed;

    /**
     * download state
     */
    private int downloadState;

    private long id;

    /**
     * view state
     */
    private int downloadViewStatus = Constants.WAITINGFLAG;

    /**
     * the save path
     */
    private String fullSavePath;

    /**
     * the time of create task
     */
    private long createTime = 0;

    private DownloadInfor downloadInfor;

    public DownloadInfor getDownloadInfor() {
        return downloadInfor;
    }

    public void setDownloadInfor(DownloadInfor downloadInfor) {
        this.downloadInfor = downloadInfor;
    }

    public DownloadGridItemBean(DownloadInfor info) {
        this.downloadInfor = info;
        this.fullURL = info.fullURL;
        this.mimetype = info.mimetype;
        this.saveName = info.saveName;
        this.total_bytes = info.totalBytes;
        this.speed = info.speed;
        this.downloadState = info.downloadState;
        this.id = info.id;
        this.fullSavePath = info.fullSavePath;
        this.currentBytes = info.currentBytes;
        this.createTime = info.createTime;
        switch (downloadState) {
            case DataProxy.STATUS_RUNNING:
                downloadViewStatus = Constants.DOWNLOADINGFLAG;
                break;
            case DataProxy.STATUS_PENDING:
                downloadViewStatus = Constants.WAITINGFLAG;
                break;
            case DataProxy.STATUS_PAUSED_BY_APP:
            case DataProxy.STATUS_WAITING_TO_RETRY:
            case DataProxy.STATUS_WAITING_FOR_NETWORK:
            case DataProxy.STATUS_QUEUED_FOR_WIFI:
                downloadViewStatus = Constants.PAUSEFLAG;
                break;
            case DataProxy.STATUS_FAILED:
                downloadViewStatus = Constants.FAILFLAG;
                break;
            default:
                downloadViewStatus = Constants.FAILFLAG;
                break;
        }
    }

    public String getFullSavePath() {
        return fullSavePath;
    }

    public int getFileSize() {
        return total_bytes;
    }

    public int getCurrentBytes() {
        return currentBytes;
    }

    public int getStatus() {
        return downloadViewStatus;
    }

    public void setStatus(int status) {
        downloadViewStatus = status;
    }

    public int getDownloadState() {
        return downloadState;
    }

    public String getFullURL() {
        return fullURL;
    }

    public String getMimetype() {
        return mimetype;
    }

    public String getSaveName() {
        return saveName;
    }

    public long getId() {
        return id;
    }

    public int getSpeed() {
        return speed;
    }

//    public boolean isSingleDeleteMode() {
//        return isSingleDeleteMode;
//    }

//    public void setSingleDeleteMode(boolean isSingleDeleteMode) {
//        this.isSingleDeleteMode = isSingleDeleteMode;
//    }

    public long getCreateTime() {
        return createTime;
    }

    @Override
    public String toString() {
        String str = "id:" + id + " ";
        str = str + "speed:" + speed + " ";
        str = str + "saveName:" + saveName + " \n";
        str = str + "mimetype:" + mimetype + " ";
        str = str + "fullURL:" + fullURL + " \n";
        str = str + "DownloadState:" + downloadState + " ";
        str = str + "total_bytes:" + total_bytes + " ";
        str = str + "current_bytes:" + currentBytes + " \n";
        str = str + "fullSavePath:" + fullSavePath + " \n";
        str = str + "downloadViewStatus:" + downloadViewStatus + " ";
//        str = str + "isSingleDeleteMode:" + isSingleDeleteMode + " \n";
        str = str + "isMutilDeleteMode:" + isMutilDeleteMode + " \n";
        str = str + "createTime:" + createTime + " \n";
        return str;
    }

    @Override
    public int compareTo(DownloadGridItemBean another) {
        return (int) (this.getId() - another.getId());
    }
}
