
package com.eostek.scifly.devicemanager.manage.process;

import android.graphics.drawable.Drawable;

public class ProcessInfo {

    private String mName;                // process name

    private Drawable mIcon;              // id of the process icon

    private int mColor;                  // id of the process icon

    private int mPid;

    private String mProcessName;

    private String mPackageName;

    private int mMemory;                 // the Memory value of app used

    private boolean isAutoStart = false; // is process auto start

    private boolean isUpdate = false;    // is app has new version to update

    private boolean isInstall = false;   // is app has install or not

    public ProcessInfo() {
    }

    public ProcessInfo(String name, Drawable icon, int color, int memory, boolean isAutoStart, boolean isUpdate,
            boolean isIntsall) {
        this.mName = name;
        this.mIcon = icon;
        this.mColor = color;
        this.mMemory = memory;
        this.isAutoStart = isAutoStart;
        this.isUpdate = isUpdate;
        this.isInstall = isIntsall;
    }

    public ProcessInfo(String name, String mPackageName, Drawable icon, int color, int memory) {
        this.mName = name;
        this.mIcon = icon;
        this.mColor = color;
        this.mPackageName = mPackageName;
        this.mMemory = memory;
    }

    public String getName() {
        return mName;
    }

    public void setName(String name) {
        this.mName = name;
    }

    public int getPid() {
        return mPid;
    }

    public void setPid(int pid) {
        this.mPid = pid;
    }

    public String getProcessName() {
        return mProcessName;
    }

    public void setProcessName(String processName) {
        this.mProcessName = processName;
    }

    public String getPackageName() {
        return mPackageName;
    }

    public void setPackageName(String packageName) {
        this.mPackageName = packageName;
    }

    public Drawable getIcon() {
        return mIcon;
    }

    public void setIcon(Drawable icon) {
        this.mIcon = icon;
    }

    public int getColor() {
        return mColor;
    }

    public void setColor(int color) {
        this.mColor = color;
    }

    public int getMemory() {
        return mMemory;
    }

    public void setMemory(int memory) {
        this.mMemory = memory;
    }

    public boolean isAutoStart() {
        return isAutoStart;
    }

    public void setAutoStart(boolean isAutoStart) {
        this.isAutoStart = isAutoStart;
    }

    public boolean isUpdate() {
        return isUpdate;
    }

    public void setUpdate(boolean isUpdate) {
        this.isUpdate = isUpdate;
    }

    public boolean isInstall() {
        return isInstall;
    }

    public void setInstall(boolean isInstall) {
        this.isInstall = isInstall;
    }

    @Override
    public String toString() {
        return "ProcessInfo : Name : " + mName + " mIcon : " + mIcon + " mColor : " + mColor + " mMemory : " + mMemory
                + " isAutoStart : " + isAutoStart + " isUpdate : " + isUpdate + " isInstall : " + isInstall;
    }

    public static ProcessInfo buildProcessInfoItem(String name, String mPackageName, Drawable icon, int color,
            int memory) {

        return new ProcessInfo(name, mPackageName, icon, color, memory);
    }
}
