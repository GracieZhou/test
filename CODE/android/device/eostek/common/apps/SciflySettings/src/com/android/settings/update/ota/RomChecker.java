
package com.android.settings.update.ota;

import org.json.JSONObject;

import com.android.settings.update.ota.RomInfoReader.RomInfoCallback;

import android.content.Context;
import android.text.TextUtils;

public class RomChecker implements RomInfoCallback, Constants {

    public static interface CheckCallback {

        public void onStartChecking();

        public void onVersionFound(PackageInfo info);

        public void onCheckError();
    }

    private static Logger sLog = new Logger(RomChecker.class);

    private Context mContext;

    private static PreferenceHelper sPreferenceHelper;

    private PackageInfo mLastUpdate;

    private CheckCallback mCheckCallback;

    private boolean mScanning = false;

    private String mError = null;

    public RomChecker(Context context) {
        sLog.debug("RomUpdater");
        mContext = context;
        sPreferenceHelper = PreferenceHelper.getInstance(context);
    }

    public synchronized void check(int type) {
        sLog.debug("check");

        if (!mScanning) {
            fireStartChecking();
            startServerCheck(type);
            mScanning = true;
        }

    }

    private void startServerCheck(int type) {
        sLog.debug("startServerCheck");
        if (Utils.lastOTAFail()) {
            type = OTA_TYPE_FULL;
        }
        new RomInfoReader(mContext, this).execute(type);
    }

    public boolean isScanning() {
        return mScanning;
    }

    // ////////////////////////////////////////////////////////////////
    // RomInfoCallback

    @Override
    public void onNotModified() {
        PackageInfo pkgInfo = sPreferenceHelper.getPackageInfo();
        if (pkgInfo != null) {
            fireCheckCompleted(pkgInfo);
        }
    }

    @Override
    public void onReadEnd(String buffer) {
        sLog.debug("onReadEnd:: response: " + buffer);
        try {
            mScanning = false;
            setLastUpdate(null);
            PackageInfo lastRom = createPackageInfo(buffer);
            if (lastRom == null) {
                fireCheckError();
                return;
            }
            setLastUpdate(lastRom);
            fireCheckCompleted(lastRom);
        } catch (Exception ex) {
            mScanning = false;
            sLog.error("onReadEnd:" + ex.getMessage());
            fireCheckError();
        }
    }

    @Override
    public void onReadError(Exception ex) {
        sLog.debug("onReadError:: " + ex.getMessage());
        mScanning = false;
        fireCheckError();
    }

    // ////////////////////////////////////////////////
    private PackageInfo createPackageInfo(String buffer) throws Exception {
        if (!TextUtils.isEmpty(buffer)) {
            JSONObject updateInfo = new JSONObject(buffer);
            int errCode = updateInfo.getInt("err");
            JSONObject body = updateInfo.optJSONObject("bd");

            if (errCode == 0 && body != null && body.length() > 0) {
                String version = body.optString(JSONKEY_VER);
                String description = body.optString(JSONKEY_UDS);
                String url = body.optString(JSONKEY_URL);
                String md5 = body.optString(JSONKEY_MD5);
                long size = body.optLong(JSONKEY_SIZE);
                long publishTime = body.optLong(JSONKEY_PUBTIME);
                int force = body.optInt(JSONKEY_FORCE);
                String facVer = body.optString(JSONKEY_FACVER);

                return new PackageInfo(version, description, url, md5, size, publishTime, force, facVer);
            } else if (errCode != 0 && body != null && body.length() > 0) {
                mError = body.optString("ds");
            }
        }
        return null;
    }

    public String getError() {
        return mError;
    }

    public PackageInfo getLastUpdates() {
        return mLastUpdate;
    }

    public void setLastUpdate(PackageInfo info) {
        mLastUpdate = info;
    }

    public void setCheckCallback(CheckCallback callback) {
        mCheckCallback = callback;
    }

    // ///////////////////////////////////////////////////////////
    // fire check
    private void fireStartChecking() {
        sLog.debug("fireStartChecking");
        if (mCheckCallback != null)
            mCheckCallback.onStartChecking();
    }

    private void fireCheckCompleted(final PackageInfo info) {
        sLog.debug("fireCheckCompleted");
        if (mCheckCallback != null)
            mCheckCallback.onVersionFound(info);
    }

    private void fireCheckError() {
        sLog.debug("fireCheckError");
        if (mCheckCallback != null)
            mCheckCallback.onCheckError();
    }

}
