package com.eostek.scifly.devicemanager.manage.appmanagement;

import android.graphics.drawable.Drawable;

public class AppManagementInfo {
    
    private CharSequence mName;
    private String mPkgName;
    private String mAbsolutePath;
    private String mSize;
    private int mPosition;
    private String mVersionName;
    private Drawable mDrawable;
    private boolean mUpdateFlag = false;
    private boolean mSystemApp;
    private boolean isUpdating = false;
    
    public AppManagementInfo() {
        
    }

    public AppManagementInfo(String mSize, String mAppName) {
        this.mSize = mSize;
        this.mName = mAppName;
    }

    public CharSequence getmName() {
        return mName;
    }
    
    public void setmName(CharSequence mName) {
        this.mName = mName;
    }
    
    public String getmPkgName() {
        return mPkgName;
    }

    public void setmPkgName(String mPkgName) {
        this.mPkgName = mPkgName;
    }

    public String getmAbsolutePath() {
        return mAbsolutePath;
    }

    public void setmAbsolutePath(String mAbsolutePath) {
        this.mAbsolutePath = mAbsolutePath;
    }
    
    public String getmSize() {
        return mSize;
    }

    public void setmSize(String mSize) {
        this.mSize = mSize;
    }
    
    public int getmPosition() {
        return mPosition;
    }

    public void setmPosition(int mPosition) {
        this.mPosition = mPosition;
    }
    
    public String getmVersionName() {
        return mVersionName;
    }

    public void setmVersionName(String mVersionCode) {
        this.mVersionName = mVersionCode;
    }
    
    public Drawable getmDrawable() {
        return mDrawable;
    }

    public void setmDrawable(Drawable mDrawable) {
        this.mDrawable = mDrawable;
    }
    
    public boolean getmUpdateFlag() {
        return mUpdateFlag;
    }

    public void setmUpdateFlag(boolean status) {
        this.mUpdateFlag = status;
    }
    
    public boolean getmSystemApp() {
        return mSystemApp;
    }

    public void setmSystemApp(boolean status) {
        this.mSystemApp = status;
    }
    
    public boolean getIsUpdating() {
        return isUpdating;
    }

    public void setIsUpdating(boolean status) {
        this.isUpdating = status;
    }
}
