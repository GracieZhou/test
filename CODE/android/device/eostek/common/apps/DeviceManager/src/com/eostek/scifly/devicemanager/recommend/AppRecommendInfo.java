
package com.eostek.scifly.devicemanager.recommend;

import android.graphics.drawable.Drawable;

public class AppRecommendInfo {

    private Drawable mIcon;

    private String mPkgName;

    private String mAppName;

    private String mPath;

    private int mPosition;

    private boolean mIsInstall = false;

    public AppRecommendInfo() {
    }

    public AppRecommendInfo(Drawable icon, String pkgName, String mAppName, String mPath) {
        this.mIcon = icon;
        this.mPkgName = pkgName;
        this.mAppName = mAppName;
        this.mPath = mPath;
    }

    public AppRecommendInfo(String pkgName, String mAppName, String mPath) {
        this.mPkgName = pkgName;
        this.mAppName = mAppName;
        this.mPath = mPath;
    }

    public AppRecommendInfo(Drawable icon, String pkgName) {
        this.mIcon = icon;
        this.mPkgName = pkgName;
    }

    public Drawable getmIcon() {
        return mIcon;
    }

    public void setmIcon(Drawable mIcon) {
        this.mIcon = mIcon;
    }
    
    public String getmPkgName() {
        return mPkgName;
    }

    public void setmPkgName(String mPkgName) {
        this.mPkgName = mPkgName;
    }

    public String getmAppName() {
        return mAppName;
    }

    public void setmAppName(String mAppName) {
        this.mAppName = mAppName;
    }

    public String getmPath() {
        return mPath;
    }

    public void setmPath(String mPath) {
        this.mPath = mPath;
    }

    public int getmPosition() {
        return mPosition;
    }

    public void setmPosition(int mPosition) {
        this.mPosition = mPosition;
    }

    public boolean ismIsInstall() {
        return mIsInstall;
    }

    public void setmIsInstall(boolean mIsInstall) {
        this.mIsInstall = mIsInstall;
    }
}
