//EosTek Patch Begin

package com.android.providers.downloads;

public class DownloadBean implements Comparable<DownloadBean> {
    public DownloadBean() {
    }

    private long id;

    /**
     * The actual state of download task
     */
    private int downloadState;

    private long createTime = 0;

    private int controlRun;

    public long getId() {
        return id;
    }

    public void setId(long id) {
        this.id = id;
    }

    public int getDownloadState() {
        return downloadState;
    }

    public void setDownloadState(int downloadState) {
        this.downloadState = downloadState;
    }

    public long getCreateTime() {
        return createTime;
    }

    public void setCreateTime(long createTime) {
        this.createTime = createTime;
    }

    public int getControlRun() {
        return controlRun;
    }

    public void setControlRun(int controlRun) {
        this.controlRun = controlRun;
    }

    @Override
    public int compareTo(DownloadBean another) {
        return (int) (another.getId() - this.getId());
    }

    @Override
    public String toString() {
        String str = "id:" + id + " ";
        str = str + "DownloadState:" + downloadState + " ";
        str = str + "createTime:" + createTime + " \n";
        return str;
    }
}
// EosTek Patch End
