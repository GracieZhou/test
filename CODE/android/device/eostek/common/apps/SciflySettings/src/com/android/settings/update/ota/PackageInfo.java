
package com.android.settings.update.ota;

import java.io.Serializable;
import java.util.regex.Matcher;

import android.os.Build;
import android.os.SystemProperties;
import android.text.TextUtils;

public class PackageInfo implements Constants, Serializable {

    private static final long serialVersionUID = -1239227417638114122L;

    private static final String LOCAL_FACVER = SystemProperties.get("ro.scifly.version.alias", VERSION_INVALID);

    private static final String LOCAL_ROMVER = SystemProperties.get("ro.build.version.incremental", VERSION_INVALID);

    private String md5 = null;

    private String filename = null;

    private long size = 0;

    private String version = null;

    // 发生错误时有效,描述信息
    private String ds;

    // 升级功能描述
    private String uds;

    // 升级包请求地址
    private String url;

    // 升级包的下载地址
    private String downloadUrl;

    // 是否强制升级
    private int force;

    private long publishTime;

    // @add 2016-1-4 版本备注，只用来在UI显示
    private String facVer;

    public String getFacVer() {
        String ver = facVer;
        if(TextUtils.isEmpty(facVer) || VERSION_INVALID.equals(LOCAL_FACVER)) {
            ver = version;
        }

        return ver;
    }

    public void setFacVer(String facVer) {
        this.facVer = facVer;
    }

    public PackageInfo(String version, String desc, String url, String md5, long size, long pubTime, int force,
            String facVer) {
        this.version = version;
        this.setUds(desc);
        this.size = size;
        this.setUrl(url);
        this.md5 = md5;
        this.setPublishTime(pubTime);
        this.setForce(force);
        this.facVer = facVer;
    }

    public String getMd5() {
        return md5;
    }

    public String getFilename() {
        return filename;
    }

    public String getVersion() {
        return version;
    }

    public long getSize() {
        return size;
    }

    @Override
    public String toString() {
        return String.format("version:%s, md5:%s, size:%d, publish:%d\n", version, md5, size, publishTime);
    }

    public int getForce() {
        return force;
    }

    public void setForce(int force) {
        this.force = force;
    }

    public long getPublishTime() {
        return publishTime;
    }

    public void setPublishTime(long publishTime) {
        this.publishTime = publishTime;
    }

    public String getDownloadUrl() {
        return downloadUrl;
    }

    public void setDownloadUrl(String downloadUrl) {
        this.downloadUrl = downloadUrl;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUds() {
        return uds;
    }

    public void setUds(String uds) {
        this.uds = uds;
    }

    public String getDs() {
        return ds;
    }

    public void setDs(String ds) {
        this.ds = ds;
    }

    public boolean isLegal() {
        return isLegalVersion(this.version);
    }

    /**
     * Check if this key is legal or not.
     * 
     * @param key Key that needs to be checked.
     * @return True if legal; Otherwise false.
     */
    private boolean isLegalVersion(String version) {
        if (TextUtils.isEmpty(version)) {
            return false;
        }
        Matcher matcher = LEGAL_KEY_PATTERN.matcher(version);
        if (matcher != null) {
            return matcher.matches();
        }
        return false;
    }

    public boolean isNewerThanMe(String version) {
        if (TextUtils.isEmpty(version) || !isLegalVersion(version)) {
            return false;
        }

        String[] myVerParts = this.version.substring(1).split("\\.");
        String[] otherVerParts = version.substring(1).split("\\.");

        for (int i = 0; i < myVerParts.length; i++) {
            if (Integer.parseInt(myVerParts[i]) < Integer.parseInt(otherVerParts[i])) {
                return true;
            } else if (Integer.parseInt(myVerParts[i]) > Integer.parseInt(otherVerParts[i])) {
                return false;
            } else {
                continue;
            }
        }

        return false;
    }

    public static String getCurrentVerion() {
        return Build.VERSION.INCREMENTAL;
    }

    public static String getLocalFacVer() {
        String locVer = LOCAL_FACVER;
        if(VERSION_INVALID.equals(locVer)) {
            locVer = LOCAL_ROMVER;
        }

        return locVer;
    }

    public boolean isNewerThanCurrent() {
        if (TextUtils.isEmpty(version) || !isLegalVersion(version)) {
            return false;
        }

        if (equalsThanCurrent()) {
            return false;
        }

        String[] currVerParts = getCurrentVerion().substring(1).split("\\.");
        String[] otherVerParts = version.substring(1).split("\\.");

        for (int i = 0; i < currVerParts.length; i++) {
            if (Integer.parseInt(currVerParts[i]) < Integer.parseInt(otherVerParts[i])) {
                return true;
            } else if (Integer.parseInt(currVerParts[i]) > Integer.parseInt(otherVerParts[i])) {
                return false;
            } else {
                continue;
            }
        }

        return false;
    }

    public boolean equalsThanCurrent() {
        if (TextUtils.isEmpty(version) || !isLegalVersion(version)) {
            return false;
        }
        return getCurrentVerion().equalsIgnoreCase(version);
    }

    public boolean equalsThanMe(String version) {
        if (TextUtils.isEmpty(version) || !isLegalVersion(version)) {
            return false;
        }
        return this.version.equalsIgnoreCase(version);
    }

}
