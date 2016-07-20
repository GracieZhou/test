
package com.eostek.scifly.devicemanager.manage.garbage;

public class BigFileInfo {

    private String mName;

    private String mAbsolutePath;

    private String mSize;

    private String mResourse;

    private boolean mIsChecked = false;

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
}
