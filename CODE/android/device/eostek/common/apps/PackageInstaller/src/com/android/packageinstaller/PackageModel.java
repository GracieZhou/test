
package com.android.packageinstaller;

import android.content.pm.PackageInfo;
import android.graphics.drawable.Drawable;
import android.os.Parcel;
import android.os.Parcelable;

public class PackageModel implements Parcelable{

    private PackageInfo packageInfo;

    private Drawable icon;

    private double size;

    private String path;

    private String pkgName;

    private String appName;

    private String versionName;
    
    private int versionCode;

    private int status;

    public static final int FLAG_INSTALL_SUCCESS = 1;

    public static final int FLAG_INSTALL_NOT_BEGAIN = 2;

    public static final int FLAG_INSTALLING = 3;

    public static final int FLAG_INSTALL_WAITING = 4;

    public static final int FLAG_INSTALL_FAILUER = 5;
    
    public static final int FLAG_SELECTED = 6;
    
    public static final int FLAG_NOT_SELECTED = 7;

    public PackageModel() {
        super();
        // TODO Auto-generated constructor stub
        this.status = FLAG_SELECTED;
    }

    public PackageModel(PackageInfo packageInfo, Drawable icon, double size, String path, String pkgName,
            String appName, String versionName, int versionCode, int status) {
        super();
        this.status = FLAG_SELECTED;
        this.packageInfo = packageInfo;
        this.icon = icon;
        this.size = size;
        this.path = path;
        this.pkgName = pkgName;
        this.appName = appName;
        this.versionName = versionName;
        this.versionCode = versionCode;
        this.status = status;
    }

    public PackageInfo getPackageInfo() {
        return packageInfo;
    }

    public void setPackageInfo(PackageInfo packageInfo) {
        this.packageInfo = packageInfo;
    }

    public Drawable getIcon() {
        return icon;
    }

    public void setIcon(Drawable icon) {
        this.icon = icon;
    }

    public double getSize() {
        return size;
    }

    public void setSize(double size) {
        this.size = size;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }

    public String getPkgName() {
        return pkgName;
    }

    public void setPkgName(String pkgName) {
        this.pkgName = pkgName;
    }

    public int getStatus() {
        return status;
    }

    public void setStatus(int status) {
        this.status = status;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public String getVersionName() {
        return versionName;
    }

    public void setVersionName(String versionName) {
        this.versionName = versionName;
    }

    public int getVersionCode() {
        return versionCode;
    }

    public void setVersionCode(int versionCode) {
        this.versionCode = versionCode;
    }

    @Override
    public int describeContents() {
        // TODO Auto-generated method stub
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        // TODO Auto-generated method stub
        
    }
}
