
package com.heran.launcher2.apps;

/*
 * projectName： EosLauncher
 * moduleName： AppInfoBean.java
 * @author junling.hou
 * @version 1.0.0
 * @time  2016-3-31 下午8:26:38
 * @Copyright © 2013 Eos Inc.
 */

public class AppInfoBean {

    private int id;

    // the app packageName
    private String packageName;

    // the app className
    private String className;

    // the app name
    private String title;

    // the app donwload downloadUrl
    private String downloadUrl;

    // the url to download picture
    private String pictureUrl;

    private int dowlodPercent;

    private boolean downloadFlag;

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }

    public String getClassName() {
        return className;
    }

    public void setClassName(String className) {
        this.className = className;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String url) {
        this.downloadUrl = url;
    }

    public void setDownloadFlag(boolean downloadFlag) {
        this.downloadFlag = downloadFlag;
    }

    public boolean getDownloadFlag() {
        return this.downloadFlag;
    }

    public String getPictureUrl() {
        return pictureUrl;
    }

    public void setPictureUrl(String pictureUrl) {
        this.pictureUrl = pictureUrl;
    }

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getDowlodPercent() {
        return dowlodPercent;
    }

    public void setDowlodPercent(int dowlodPercent) {
        this.dowlodPercent = dowlodPercent;
    }

    @Override
    public String toString() {
        return "packageName = " + packageName + " , className = " + className + " ,title = " + title
                + " ,downloadUrl = " + downloadUrl;
    }

}
