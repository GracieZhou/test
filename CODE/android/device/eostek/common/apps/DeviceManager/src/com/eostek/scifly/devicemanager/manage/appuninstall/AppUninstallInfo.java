
package com.eostek.scifly.devicemanager.manage.appuninstall;

import android.graphics.drawable.Drawable;

public class AppUninstallInfo  {

    private String mName;

    private String mAbsolutePath;

    private String mPkgName;

    private String mSize;

    private String mResourse;

    private boolean mIsChecked = false;
    
    private Drawable mDrawable;

    public String getmName() {
        return mName;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }

    public String getmSize() {
        return mSize;
    }

    public void setmSize(String mSize) {
        this.mSize = mSize;
    }

    public String getmResourse() {
        return mResourse;
    }

    public void setmResourse(String mResourse) {
        this.mResourse = mResourse;
    }

    public boolean ismIsChecked() {
        return mIsChecked;
    }

    public void setmIsChecked(boolean mIsChecked) {
        this.mIsChecked = mIsChecked;
    }

    public String getmAbsolutePath() {
        return mAbsolutePath;
    }

    public void setmAbsolutePath(String mAbsolutePath) {
        this.mAbsolutePath = mAbsolutePath;
    }

    public String getmPkgName() {
        return mPkgName;
    }

    public void setmPkgName(String mPkgName) {
        this.mPkgName = mPkgName;
    }
    
    

    public Drawable getmDrawable() {
        return mDrawable;
    }

    public void setmDrawable(Drawable mDrawable) {
        this.mDrawable = mDrawable;
    }

}
