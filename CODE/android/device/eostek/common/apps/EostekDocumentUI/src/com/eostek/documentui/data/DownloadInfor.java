
package com.eostek.documentui.data;

import java.io.Serializable;

@SuppressWarnings("serial")
public class DownloadInfor implements Serializable{
    public long id;

    /**
     * the speed of download
     */
    public int speed;

    public String saveName;

    public String mimetype;

    public String fullURL;

    /**
     * The actual state of download task
     */
    public int downloadState;

    public int totalBytes = 1024;

    public int currentBytes = 0;

    public String fullSavePath;

    public long createTime = 0;

    public int controlRun = DataProxy.CONTROL_RUN;

    public DownloadInfor() {
        id = 0;
        speed = 0;
        saveName = "";
        mimetype = "";
        fullURL = "";
        downloadState = DataProxy.STATUS_FAILED;
        totalBytes = -1;
        currentBytes = -1;
        fullSavePath = "";
        createTime = 0;
        controlRun = DataProxy.CONTROL_RUN;
    }

    @Override
    public String toString() {
        String str = "id:" + id + " ";
        str = str + "speed:" + speed + " ";
        str = str + "saveName:" + saveName + " \n";
        str = str + "mimetype:" + mimetype + " ";
        str = str + "fullURL:" + fullURL + " \n";
        str = str + "DownloadState:" + downloadState + " ";
        str = str + "total_bytes:" + totalBytes + " ";
        str = str + "current_bytes:" + currentBytes + " \n";
        str = str + "fullSavePath:" + fullSavePath + " \n";
        str = str + "createTime:" + createTime + " \n";
        str = str + "controlRun:" + controlRun + " ";
        return str;
    }
}
