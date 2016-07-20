
package com.android.settings.update.ota;

import android.content.Context;
import android.content.SharedPreferences;
import android.text.TextUtils;

public class PreferenceHelper implements Constants {

    private SharedPreferences settings;

    private static PreferenceHelper INSTANCE;

    private PreferenceHelper(Context context) {
        settings = context.getSharedPreferences(PREFERENCE_NAME, Context.MODE_PRIVATE);
    }

    public static PreferenceHelper getInstance(Context context) {
        if (null == INSTANCE) {
            INSTANCE = new PreferenceHelper(context);
        }
        return INSTANCE;
    }

    public void setDownloadId(long id) {
        if (id == -1) {
            removePreference(DOWNLOAD_HTTP_ID);
        } else {
            savePreference(DOWNLOAD_HTTP_ID, id);
        }
    }

    public long getDownloadId() {
        return settings.getLong(DOWNLOAD_HTTP_ID, -1);
    }

    public void setDownloadId(String taskId) {
        if (taskId == null) {
            removePreference(DOWNLOAD_P2P_ID);
        } else {
            savePreference(DOWNLOAD_P2P_ID, taskId);
        }
    }

    public String getP2pDownloadId() {
        return settings.getString(DOWNLOAD_P2P_ID, "");
    }

    public void setP2pBackupId(String taskId) {
        if (taskId == null) {
            removePreference(P2P_BACKUP_ID);
        } else {
            savePreference(P2P_BACKUP_ID, taskId);
        }
    }

    public String getP2pBackupId() {
        return settings.getString(P2P_BACKUP_ID, "");
    }

    public void setP2pBackupPath(String path) {
        if (path == null) {
            removePreference(P2P_BACKUP_PATH);
        } else {
            savePreference(P2P_BACKUP_PATH, path);
        }
    }

    public String getP2pBackupPath() {
        return settings.getString(P2P_BACKUP_PATH, "");
    }

    public void setDownloadEngine(String engine) {
        savePreference(DOWNLOAD_ENGINE, engine);
    }

    public String getDownloadEngine() {
        return settings.getString(DOWNLOAD_ENGINE, DOWNLOAD_ENGINE_HTTP);
    }

    public boolean isDownloadFinished() {
        return settings.getBoolean(DOWNLOAD_FINISHED, false);
    }

    public boolean setDownloadFinished(boolean finished) {
        return savePreference(DOWNLOAD_FINISHED, finished);
    }

    public boolean setDownloadPath(String path) {
        return savePreference(DOWNLOAD_PATH, path);
    }

    public String getDownloadPath() {
        return settings.getString(DOWNLOAD_PATH, "");
    }

    public long getDownloadSize() {
        return settings.getLong(JSONKEY_SIZE, 0);
    }

    public String getPackageMd5() {
        return settings.getString(JSONKEY_MD5, "");
    }

    public boolean setEtag(String value) {
        if (!TextUtils.isEmpty(value)) {
            saveEtagChecksum(value);
        }
        return savePreference(JSONKEY_ETAG, value);
    }

    public String getEtag() {
        String etag = settings.getString(JSONKEY_ETAG, "");
        if (!TextUtils.isEmpty(etag)) {
            String checksum = genEtagChecksum(etag);
            String oldChecksum = getEtagChecksum();
            if (!checksum.equals(oldChecksum)) {
                setEtag("");
                return "";
            }
        }
        return etag;
    }

    public void saveEtagChecksum(String etag) {
        if (TextUtils.isEmpty(etag)) {
            removePreference(ETAG_CHECKSUM);
        } else {
            savePreference(ETAG_CHECKSUM, genEtagChecksum(etag));
        }

    }

    public String getEtagChecksum() {
        return settings.getString(ETAG_CHECKSUM, "");
    }

    public String genEtagChecksum(String etag) {
        return etag + PackageInfo.getCurrentVerion();
    }

    public void setForceOTA(boolean value) {
        savePreference(FORCE_OTA, value);
    }

    public boolean isForceOTA() {
        return settings.getBoolean(FORCE_OTA, false);
    }

    public void setForceOTAMd5(String value) {
        savePreference(FORCE_OTA_MD5, value);
    }

    public String getForceOTAMd5() {
        return settings.getString(FORCE_OTA_MD5, "");
    }

    public void savePackageInfo(PackageInfo pkgInfo) {
        savePreference(JSONKEY_VER, pkgInfo.getVersion());
        savePreference(JSONKEY_UDS, pkgInfo.getUds());
        savePreference(JSONKEY_URL, pkgInfo.getUrl());
        savePreference(JSONKEY_MD5, pkgInfo.getMd5());
        savePreference(JSONKEY_SIZE, pkgInfo.getSize());
        savePreference(JSONKEY_PUBTIME, pkgInfo.getPublishTime());
        savePreference(JSONKEY_FORCE, pkgInfo.getForce());
        savePreference(JSONKEY_FACVER, pkgInfo.getFacVer());
    }

    public PackageInfo getPackageInfo() {
        String version = settings.getString(JSONKEY_VER, VERSION_INVALID);
        String uds = settings.getString(JSONKEY_UDS, "");
        String url = settings.getString(JSONKEY_URL, "");
        String md5 = settings.getString(JSONKEY_MD5, "");
        long size = settings.getLong(JSONKEY_SIZE, 0);
        long pubTime = settings.getLong(JSONKEY_PUBTIME, 0);
        int force = settings.getInt(JSONKEY_FORCE, 0);
        String facVer = settings.getString(JSONKEY_FACVER, "");
        return new PackageInfo(version, uds, url, md5, size, pubTime, force, facVer);
    }

    public void clearPackageInfo() {
        removePreference(JSONKEY_VER);
        removePreference(JSONKEY_UDS);
        removePreference(JSONKEY_URL);
        removePreference(JSONKEY_MD5);
        removePreference(JSONKEY_SIZE);
        removePreference(JSONKEY_PUBTIME);
        removePreference(JSONKEY_FORCE);
        removePreference(JSONKEY_FACVER);
    }

    // //////////////////////////////////////////////////////////////////////////////
    // private quick methods
    private boolean savePreference(String preference, String value) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putString(preference, value);
        return editor.commit();
    }

    private boolean savePreference(String preference, long value) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putLong(preference, value);
        return editor.commit();
    }

    private boolean savePreference(String preference, int value) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putInt(preference, value);
        return editor.commit();
    }

    private boolean savePreference(String preference, boolean value) {
        SharedPreferences.Editor editor = settings.edit();
        editor.putBoolean(preference, value);
        return editor.commit();
    }

    private void removePreference(String preference) {
        SharedPreferences.Editor editor = settings.edit();
        editor.remove(preference);
        editor.commit();
    }
    // end
    // //////////////////////////////////////////////////////////////////////////////
}
