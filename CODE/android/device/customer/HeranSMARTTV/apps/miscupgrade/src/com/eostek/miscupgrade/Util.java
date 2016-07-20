
package com.eostek.miscupgrade;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.math.BigInteger;
import java.security.MessageDigest;

import android.content.Context;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;

public class Util {
    private static final String TAG = UpgradeConstants.TAG;

    public static boolean hasAnimFile = false;

    public static boolean hasVideoFile = false;

    public static boolean hasLauncherFile = false;

    public static String path = null;

    public static String[] files = null;

    public static boolean videoUpdateResult = false;

    public static boolean animUpdateResult = false;

    public static boolean launchUpdateResult = false;

    public static void init() {
        hasAnimFile = false;
        hasVideoFile = false;
        hasLauncherFile = false;
        path = null;
        files = null;
        videoUpdateResult = false;
        animUpdateResult = false;
        launchUpdateResult = false;
    }

    public static boolean ifNeedUpgrade() {
        if (hasAnimFile || hasVideoFile || hasLauncherFile) {
            return true;
        }
        return false;
    }

    public static boolean hasExternalUpdateFile(Uri uri) {
        if (null != uri && uri.getScheme().equals("file")) {
            String path = uri.getPath();

            try {
                path = new File(path).getCanonicalPath();
            } catch (IOException e) {
                Log.e(TAG, "couldn't canonicalize " + path);
                return false;
            }

            if ("/mnt/sdcard".equals(path)) {
                Log.i(TAG, "skip internal sdcard.");
                return false;
            }
            // now detect MiscUpgrade dir in media
            File upgradeDirFile = new File(path + UpgradeConstants.PARENT_DIR);
            if (upgradeDirFile.exists()) {
                Util.init();
                String[] files = upgradeDirFile.list(new FilenameFilter() {

                    @Override
                    public boolean accept(File dir, String filename) {

                        if (UpgradeConstants.USER_BOOTVIDEO_NAME.equals(filename)) {
                            Util.hasVideoFile = true;
                            return true;
                        } else if (UpgradeConstants.USER_BOOTANIMATION_NAME.equals(filename)) {
                            Util.hasAnimFile = true;
                            return true;
                        } else if (UpgradeConstants.USER_LAUNCHER_NAME.equals(filename)) {
                            Util.hasLauncherFile = true;
                            return true;
                        }

                        return false;

                    }
                });

                if (null != files && files.length > 0) {
                    Util.path = path;
                    Util.files = files;
                    
                    return true;
                }
            }

        }
        return false;
    }

    public static boolean checkResult(UpgradeResult result) {
        if (result != null) {
            if (result.compareTo(UpgradeResult.UPGRADE_SUCCESS) == 0) {
                return true;
            }
        }
        return false;
    }

    public static String getFileMD5(File file) {
        if (!file.isFile()) {
            return null;
        }
        MessageDigest digest = null;
        FileInputStream in = null;
        byte buffer[] = new byte[1024];
        int len;
        try {
            digest = MessageDigest.getInstance("MD5");
            in = new FileInputStream(file);
            while ((len = in.read(buffer, 0, 1024)) != -1) {
                digest.update(buffer, 0, len);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return null;
        } finally {
            try {
                in.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        BigInteger bigInt = new BigInteger(1, digest.digest());

        return bigInt.toString(16);
    }

    public static boolean checkMd5(String externalFilePath, String internalFilePath) {
        Log.i(TAG, "checkMD5::external-->" + externalFilePath + "\tinternal-->" + internalFilePath);
        if (TextUtils.isEmpty(externalFilePath) || TextUtils.isEmpty(internalFilePath)) {
            return false;
        }

        File externalFile = new File(externalFilePath);
        if (externalFile.exists() && externalFile.canRead()) {
            File internalFile = new File(internalFilePath);

            if (internalFile.exists()) {
                // check md5sum now
                String internalMd5 = Util.getFileMD5(internalFile);
                String externalMd5 = Util.getFileMD5(externalFile);

                if (null == internalMd5 || null == externalMd5) {
                    return false;
                }

                if (internalMd5.equals(externalMd5)) {
                    Log.i(TAG, String.format("Two files have the same md5sum-->%s\n", internalMd5));

                    return false;
                } else {
                    return true;
                }
            } else {
                return true;
            }

        }
        return false;
    }

    public static boolean checkAllFileMD5(Context context) {

        if (hasAnimFile) {
            hasAnimFile = checkMd5(Util.path + UpgradeConstants.PARENT_DIR + UpgradeConstants.USER_BOOTANIMATION_NAME,
                    UpgradeConstants.USER_BOOTANIMATION_PATH);
            if (!hasAnimFile) {
                Log.i(TAG, "Boot Anim has same  Md5 ");
            }
        }

        if (hasVideoFile) {
            hasVideoFile = checkMd5(Util.path + UpgradeConstants.PARENT_DIR + UpgradeConstants.USER_BOOTVIDEO_NAME,
                    UpgradeConstants.USER_BOOTVIDEO_PATH);
            if (!hasVideoFile) {
                Log.i(TAG, "Boot Video has same  Md5 ");
            }

        }
        if (hasLauncherFile) {

            String userLauncherPath = UpgradeHelper.getUserLauncherPath(context);

            hasLauncherFile = checkMd5(Util.path + UpgradeConstants.PARENT_DIR + UpgradeConstants.USER_LAUNCHER_NAME,
                    userLauncherPath);
            if (!hasLauncherFile) {
                Log.i(TAG, "Launcher has same  Md5 ");
            }

        }

        return ifNeedUpgrade();
    }
}
