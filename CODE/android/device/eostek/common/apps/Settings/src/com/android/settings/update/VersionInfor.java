
package com.android.settings.update;

/**
 * 从服务器返回的版本信息
 * 
 * @author 杜聪甲(ducj@biaoqi.com.cn)
 * @since 1.0 2011-11-18
 */
public class VersionInfor {

    // 发生错误时有效,描述信息
    private String ds;

    // 版本号
    private String version;

    // 升级功能描述
    private String uds;

    // 升级包的地址
    private String url;

    // 升级包的大小
    private long size;

    // 升级包的签名
    private String md;

    // 是否强制升级
    private int force;

    // storage status
    private boolean storage;

    // last version
    private String lastVersion;

    // last download id
    private long lastId;

    // last file path
    private String lastFilePath;

    // last Sn+ file path
    private String lastSnFilePath;

    // last uds
    private String lastUds;

    // package time
    private String time;

    // last time
    private String lastTime;

    // last package size
    private long lastSize;

    // last sn+ id;
    private String lastSnId;

    // last md5
    private String lastMd;

    // download engine
    private String engine;

    // package type
    private String packageType;

    // last package type
    private String lastPackageType;

    // last download engine
    private String lastEngine;

    public int getForce() {
        return force;
    }

    public void setForce(int force) {
        this.force = force;
    }

    public String getDs() {
        return ds;
    }

    public void setDs(String ds) {
        this.ds = ds;
    }

    public String getVersion() {
        return version;
    }

    public void setVersion(String version) {
        this.version = version;
    }

    public String getUds() {
        return uds;
    }

    public void setUds(String uds) {
        this.uds = uds;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public String getMd() {
        return md;
    }

    public void setMd(String md) {
        this.md = md;
    }

    public boolean getStorage() {
        return storage;
    }

    public void setStorage(boolean storage) {
        this.storage = storage;
    }

    public String getLastVersion() {
        return lastVersion;
    }

    public void setLastVersion(String lastVersion) {
        this.lastVersion = lastVersion;
    }

    public long getLastId() {
        return lastId;
    }

    public void setLastId(long lastId) {
        this.lastId = lastId;
    }

    public String getLastFilePath() {
        return lastFilePath;
    }

    public void setLastFilePath(String lastFilePath) {
        this.lastFilePath = lastFilePath;
    }

    public String getLastSnFilePath() {
        return lastSnFilePath;
    }

    public void setLastSnFilePath(String lastSnFilePath) {
        this.lastSnFilePath = lastSnFilePath;
    }

    public String getLastUds() {
        return lastUds;
    }

    public void setLastUds(String lastUds) {
        this.lastUds = lastUds;
    }

    public String getTime() {
        return time;
    }

    public void setTime(String time) {
        this.time = time;
    }

    public String getLastTime() {
        return lastTime;
    }

    public void setLastTime(String lastTime) {
        this.lastTime = lastTime;
    }

    public long getLastSize() {
        return lastSize;
    }

    public void setLastSize(long lastSize) {
        this.lastSize = lastSize;
    }

    public void setLastSnId(String lastSnId) {
        this.lastSnId = lastSnId;
    }

    public String getLastSnId() {
        return lastSnId;
    }

    public void setLastMd(String lastMd) {
        this.lastMd = lastMd;
    }

    public String getLastMd() {
        return lastMd;
    }

    public void setEngine(String engine) {
        this.engine = engine;
    }

    public String getEngine() {
        return engine;
    }

    public void setPackageType(String packageType) {
        this.packageType = packageType;
    }

    public String getPackageType() {
        return packageType;
    }

    public void setLastPackageType(String lastPackageType) {
        this.lastPackageType = lastPackageType;
    }

    public String getLastPackageType() {
        return lastPackageType;
    }

    public void setLastEngine(String lastEngine) {
        this.lastEngine = lastEngine;
    }

    public String getLastEngine() {
        return lastEngine;
    }
}
