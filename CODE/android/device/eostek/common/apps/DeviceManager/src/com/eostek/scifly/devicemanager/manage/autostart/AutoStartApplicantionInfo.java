
package com.eostek.scifly.devicemanager.manage.autostart;

import android.graphics.drawable.Drawable;

public class AutoStartApplicantionInfo {

    private CharSequence label;

    private Drawable icon;

    private String pkgName;

    private int mColor;// background color

    public AutoStartApplicantionInfo(CharSequence label, Drawable icon, String pkgName) {
        this.label = label;
        this.icon = icon;
        this.pkgName = pkgName;
    }

    public AutoStartApplicantionInfo(CharSequence label, Drawable icon, String pkgName, int mColor) {
        this.label = label;
        this.icon = icon;
        this.pkgName = pkgName;
        this.mColor = mColor;
    }

    public int getmColor() {
        return mColor;
    }

    public void setmColor(int mColor) {
        this.mColor = mColor;
    }

    public CharSequence getLabel() {
        return label;
    }

    public Drawable getIcon() {
        return icon;
    }

    public String getPkgName() {
        return pkgName;
    }
}
