
package com.android.settings.update;

import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.MalformedURLException;
import java.net.URL;
import java.security.MessageDigest;
import java.util.HashMap;
import java.util.Locale;
import java.util.regex.Matcher;
import scifly.device.Device;
import scifly.um.EosUploadListener;
import scifly.um.EosUploadManager;
import scifly.um.EosUploadTask;
import scifly.util.LogUtils;
import scifly.util.RecoverySystemExtra;
import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.Environment;
import android.os.PowerManager;
import android.os.RecoverySystem;
import android.os.StatFs;
import android.os.RecoverySystem.ProgressListener;
import android.text.TextUtils;
import android.util.Log;

public class Utils {
    /**
     * Get available size of path.
     * 
     * @param path Directory path.
     * @return Available size in bytes.
     */
    public static long getAvailableSize(String path) {
        File file = new File(path);
        if (!file.exists()) {
            return -1;
        }
        StatFs sf = new StatFs(path);
        long blockSize = sf.getBlockSizeLong();
        long availCount = sf.getAvailableBlocksLong();
        long availableSize = availCount * blockSize;
        // Check if path is under SD card.
        String externalStorage = Environment.getExternalStorageDirectory().getAbsolutePath();
        if (path.startsWith(externalStorage)) {
            // If SD card is emulated, 1G space is unavailable.
            if (Environment.isExternalStorageEmulated()) {
                availableSize -= 0x40000000L;
            }
        }
        if (availableSize < 0) {
            return 0;
        }
        return availableSize;
    }

    /**
     * Check if this key is legal or not.
     * 
     * @param key Key that needs to be checked.
     * @return True if legal; Otherwise false.
     */
    public static boolean isKeyLegal(String key) {
        if (TextUtils.isEmpty(key)) {
            return false;
        }
        Matcher matcher = Constants.LEGAL_KEY_PATTERN.matcher(key);
        if (matcher != null) {
            return matcher.matches();
        }
        return false;
    }

    /**
     * Compare ver1 and ver2.
     * 
     * @param ver1 First version.
     * @param ver2 Second version.
     * @return Return 2 if ver1 is illegal; return 1 if ver1 is newer; return -2
     *         if ver2 is illegal; return -1 if ver2 is newer; otherwise return
     *         0.
     */
    public static int compareVersion(String ver1, String ver2) {
        if (!isKeyLegal(ver1)) {
            return 2;
        } else if (!isKeyLegal(ver2)) {
            return -2;
        } else if (ver1.equalsIgnoreCase(ver2)) {
            return 0;
        }
        // Assume all version are like: v2.3.0.15756
        String[] v1 = ver1.substring(1).split("\\.");
        String[] v2 = ver2.substring(1).split("\\.");
        for (int i = 0; i < v1.length; i++) {
            if (Integer.parseInt(v1[i]) > Integer.parseInt(v2[i])) {
                return 1;
            } else if (Integer.parseInt(v1[i]) < Integer.parseInt(v2[i])) {
                return -1;
            }
        }
        return 0;
    }

    /**
     * Update SharedPreferences.
     * 
     * @param context Application context.
     * @param info Version info.
     * @return True if successful, otherwise false.
     */
    public static boolean updatePreference(Context context, VersionInfor info) {
        SharedPreferences sp = context.getSharedPreferences(Constants.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        Editor edit = sp.edit();
        edit.putString(Constants.PREFERENCE_LAST_VER, info.getLastVersion());
        edit.putLong(Constants.PREFERENCE_LAST_DOWNLOAD_ID, info.getLastId());
        edit.putString(Constants.PREFERENCE_LAST_SN_ID, info.getLastSnId());
        edit.putString(Constants.PREFERENCE_LAST_FILE_PATH, info.getLastFilePath());
        edit.putString(Constants.PREFERENCE_LAST_SN_FILE_PATH, info.getLastSnFilePath());
        edit.putString(Constants.PREFERENCE_LAST_UDS, info.getLastUds());
        edit.putString(Constants.PREFERENCE_LAST_PUB_TIME, info.getLastTime());
        edit.putLong(Constants.PREFERENCE_LAST_SIZE, info.getLastSize());
        edit.putString(Constants.PREFERENCE_LAST_MD5, info.getLastMd());
        edit.putString(Constants.PREFERENCE_LAST_PACKAGE_TYPE, info.getLastPackageType());
        edit.putString(Constants.PREFERENCE_LAST_ENGINE, info.getLastEngine());
        return edit.commit();
    }

    /**
     * Load last version info from SharedPreferences.
     * 
     * @param context Application context.
     * @param info VersionInfor.
     */
    public static void loadLastVersionInfo(Context context, VersionInfor info) {
        SharedPreferences sp = context.getSharedPreferences(Constants.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        info.setLastVersion(sp.getString(Constants.PREFERENCE_LAST_VER, Constants.INVALID_STRING));
        info.setLastId(sp.getLong(Constants.PREFERENCE_LAST_DOWNLOAD_ID, -1));
        info.setLastSnId(sp.getString(Constants.PREFERENCE_LAST_SN_ID, Constants.INVALID_STRING));
        info.setLastFilePath(sp.getString(Constants.PREFERENCE_LAST_FILE_PATH, Constants.INVALID_STRING));
        info.setLastSnFilePath(sp.getString(Constants.PREFERENCE_LAST_SN_FILE_PATH, Constants.INVALID_STRING));
        info.setLastUds(sp.getString(Constants.PREFERENCE_LAST_UDS, Constants.INVALID_STRING));
        info.setLastTime(sp.getString(Constants.PREFERENCE_LAST_PUB_TIME, Constants.INVALID_STRING));
        info.setLastSize(sp.getLong(Constants.PREFERENCE_LAST_SIZE, -1));
        info.setLastMd(sp.getString(Constants.PREFERENCE_LAST_MD5, Constants.INVALID_STRING));
        info.setLastPackageType(sp.getString(Constants.PREFERENCE_LAST_PACKAGE_TYPE, Constants.INVALID_STRING));
        info.setLastEngine(sp.getString(Constants.PREFERENCE_LAST_ENGINE, Constants.INVALID_STRING));
    }

    /**
     * Put String value into SharedPreferences.
     * 
     * @param context Application context.
     * @param key Key.
     * @param value Value.
     * @return True if successful, otherwise false.
     */
    public static boolean putString(Context context, String key, String value) {
        SharedPreferences sp = context.getSharedPreferences(Constants.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        Editor edit = sp.edit();
        edit.putString(key, value);
        return edit.commit();
    }

    /**
     * Get String value from SharedPreferences.
     * 
     * @param context Application context.
     * @param key Key.
     * @param defValue Default value if no key found.
     * @return Value of the key.
     */
    public static String getString(Context context, String key, String defValue) {
        SharedPreferences sp = context.getSharedPreferences(Constants.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        return sp.getString(key, defValue);
    }

    /**
     * Put int value into SharedPreferences.
     * 
     * @param context Application context.
     * @param key Key.
     * @param value Value.
     * @return True if successful, otherwise false.
     */
    public static boolean putInt(Context context, String key, int value) {
        SharedPreferences sp = context.getSharedPreferences(Constants.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        Editor edit = sp.edit();
        edit.putInt(key, value);
        return edit.commit();
    }

    /**
     * Get int value from SharedPreferences.
     * 
     * @param context Application context.
     * @param key Key.
     * @param defValue Default value if no key found.
     * @return Value of the key.
     */
    public static int getInt(Context context, String key, int defValue) {
        SharedPreferences sp = context.getSharedPreferences(Constants.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        return sp.getInt(key, defValue);
    }

    /**
     * Put boolean value into SharedPreferences.
     * 
     * @param context Application context.
     * @param key Key.
     * @param value Value.
     * @return True if successful, otherwise false.
     */
    public static boolean putBoolean(Context context, String key, boolean value) {
        SharedPreferences sp = context.getSharedPreferences(Constants.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        Editor edit = sp.edit();
        edit.putBoolean(key, value);
        return edit.commit();
    }

    /**
     * Get boolean value from SharedPreferences.
     * 
     * @param context Application context.
     * @param key Key.
     * @param defValue Default value if no key found.
     * @return Value of the key.
     */
    public static boolean getBoolean(Context context, String key, boolean defValue) {
        SharedPreferences sp = context.getSharedPreferences(Constants.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        return sp.getBoolean(key, defValue);
    }

    /**
     * Put long value into SharedPreferences.
     * 
     * @param context Application context.
     * @param key Key.
     * @param value Value.
     * @return True if successful, otherwise false.
     */
    public static boolean putLong(Context context, String key, long value) {
        SharedPreferences sp = context.getSharedPreferences(Constants.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        Editor edit = sp.edit();
        edit.putLong(key, value);
        return edit.commit();
    }

    /**
     * Get long value from SharedPreferences.
     * 
     * @param context Application context.
     * @param key Key.
     * @param defValue Default value if no key found.
     * @return Value of the key.
     */
    public static long getLong(Context context, String key, long defValue) {
        SharedPreferences sp = context.getSharedPreferences(Constants.SHARED_PREFERENCE_NAME, Context.MODE_PRIVATE);
        return sp.getLong(key, defValue);
    }

    /**
     * Delete directory and file.
     * 
     * @param file File or Directory that needs to be deleted.
     */
    public static void deleteFile(File file) {
        if (file == null) {
            return;
        }
        if (!file.exists() || !file.canWrite()) {
            loge("File[" + file.getAbsolutePath() + "] no exists or cannot access!");
            return;
        }
        if (file.isFile()) {
            file.delete();
        } else if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File subFile : files) {
                deleteFile(subFile);
            }
            file.delete();
        }
    }

    /**
     * Delete directory and file, record all deleted files' name.
     * 
     * @param file File or Directory that needs to be deleted.
     * @param sb Record all files' name into sb.
     */
    public static void deleteFileAndRecord(File file, StringBuilder sb) {
        if (file == null) {
            return;
        }
        if (!file.exists() || !file.canWrite()) {
            loge("File[" + file.getAbsolutePath() + "] no exists or cannot access!");
            return;
        }
        sb.append(file.getAbsolutePath());
        sb.append("\n");
        if (file.isFile()) {
            file.delete();
        } else if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File subFile : files) {
                deleteFileAndRecord(subFile, sb);
            }
            file.delete();
        }
    }

    /**
     * Collect all file names in /cache and report it to Server. After uploading
     * completed, empty directory /cache.
     * 
     * @param context Application context.
     */
    public static void reportCacheFullAndClean(Context context) {
        File cachedir = Environment.getDownloadCacheDirectory();
        if (!cachedir.exists() || !cachedir.canRead()) {
            loge("File[" + cachedir.getAbsolutePath() + "] no exists or cannot access!");
            return;
        }
        // List all files in directory /cache and record their names into
        // StringBuilder.
        StringBuilder sb = new StringBuilder();
        // Never delete lastFile at this time.
        String lastFileName = getString(context, Constants.PREFERENCE_LAST_FILE_PATH, null);
        if (cachedir.isDirectory()) {
            File[] files = cachedir.listFiles(new FilenameFilter() {
                @Override
                public boolean accept(File dir, String filename) {
                    if (filename.equals("backup") || filename.equals("lost+found") || filename.equals("recovery")) {
                        return false;
                    } else {
                        return true;
                    }
                }
            });
            for (File subFile : files) {
                if (!subFile.getAbsolutePath().equalsIgnoreCase(lastFileName)) {
                    deleteFileAndRecord(subFile, sb);
                }
            }
        }
        // Write StringBuilder into a File.
        File txtfile = new File(Environment.getExternalStorageDirectory().getAbsolutePath()
                + "/Android/data/com.android.settings/cachefilename.txt");
        if (sb.length() > 0) {
            try {
                if (!txtfile.exists()) {
                    txtfile.createNewFile();
                }
                FileWriter fileWritter = new FileWriter(txtfile.getAbsolutePath(), false);
                BufferedWriter bufferWritter = new BufferedWriter(fileWritter);
                bufferWritter.write(sb.toString());
                bufferWritter.close();
            } catch (IOException e) {
                loge(e.toString());
            }
        }
        // Upload cachefilename.txt to server.
        EosUploadManager eum = new EosUploadManager();
        HashMap<String, String> map = new HashMap<String, String>();
        map.put("text", "None");
        map.put("devCode", Device.getDeviceCode());
        map.put("mac", Device.getHardwareAddress(context));
        map.put("devName", Device.getDeviceName(context));
        map.put("ifid", Device.getIfid());
        map.put("ver", Device.getVersion());
        map.put("bb", Device.getBb());
        map.put("ttag", Device.getTtag());

        // Compress cachefilename.txt to cachefilename.txt2010202939.zip
        long timestamp = System.currentTimeMillis();
        final String zipLogFile = txtfile.getAbsolutePath() + timestamp + ".zip";
        String[] srcFiles = new String[1];
        srcFiles[0] = txtfile.getAbsolutePath();
        try {
            LogUtils.compressLog(srcFiles, zipLogFile);
        } catch (IOException e) {
            loge(e.toString());
        }
        EosUploadListener listener = new EosUploadListener() {
            @Override
            public void onUploadCompleted(int errCode) {
                log("Upload File[" + zipLogFile + "], onUploadCompleted: " + errCode);
            }
        };
        EosUploadTask task = new EosUploadTask(0, zipLogFile, Constants.DEFAULT_SERVER_URL, map, listener);
        eum.addTask(task);
    }

    /**
     * Get real download url of the given urlStr.
     * 
     * @param urlStr The given urlStr.
     * @return Real download url.
     */
    public static String getUrl(String urlStr) {
        URL url;
        String downloadUrl = "";
        try {
            url = new URL(urlStr);
            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setConnectTimeout(20000);
            conn.setReadTimeout(5000);
            conn.setRequestMethod("HEAD");
            if (conn.getResponseCode() == 200) {
                downloadUrl = conn.getURL().toString();
            }
            conn.disconnect();
        } catch (MalformedURLException e) {
            loge(e.toString());
        } catch (IOException e) {
            loge(e.toString());
        }
        return downloadUrl;
    }

    /**
     * Check if last update process failed or not.
     * 
     * @return True if last update process failed, otherwise false.
     */
    public static boolean isLastUpdateFailed() {
        int state = RecoverySystemExtra.getSystemUpgradeState();
        return (state == RecoverySystemExtra.LAST_UPGRADE_FAILED || state == RecoverySystemExtra.UPGRADE_FAILED);
    }

    /**
     * Verify package through md5.
     * 
     * @param path Package path.
     * @param md5 Md5 String from Server.
     * @return True if successful, otherwise false.
     */
    public static boolean verify(String path, String md5) {
        File packageFile = new File(path);
        if (!packageFile.exists()) {
            loge("Verify MD5, file " + path + " not exists");
            return false;
        }
        if (isEmpty(md5)) {
            loge("Verify MD5, path=" + path + ", md5 is empty, force Successful");
            return true;
        }
        String fileMd5 = calcMD5(packageFile);
        log("Verifly MD5, path=" + path + ", md5=" + md5 + ", fileMd5=" + fileMd5);
        return md5.equals(fileMd5);
    }

    /**
     * Calculate MD5 code of the given file.
     * 
     * @param file File that need to be calculate.
     * @return MD5 code String.
     */
    public static String calcMD5(File file) {
        FileInputStream is = null;
        MessageDigest digest = null;
        byte buffer[] = new byte[1024];
        int len;
        try {
            digest = MessageDigest.getInstance("MD5");
            is = new FileInputStream(file);
            while ((len = is.read(buffer, 0, 1024)) != -1) {
                digest.update(buffer, 0, len);
            }
            byte[] hash = digest.digest();
            StringBuffer sbRet = new StringBuffer();
            for (int i = 0; i < hash.length; i++) {
                int v = hash[i] & 0xFF;
                if (v < 16)
                    sbRet.append("0");
                sbRet.append(Integer.toString(v, 16));
            }
            return sbRet.toString();
        } catch (Exception e) {
            return null;
        } finally {
            if (is != null) {
                try {
                    is.close();
                } catch (IOException e) {
                }
            }
        }
    }

    /**
     * Install package and reboot to recovery.
     * 
     * @param context Application context.
     * @param filePath Package location.
     * @return True if install successfully, otherwise false.
     */
    public static boolean install(Context context, String filePath) {
        File file = new File(filePath);
        if (!file.exists()) {
            loge("Install package, File " + filePath + " not exists!");
            return false;
        }
        log("Install file path:" + file.getPath() + ", file length:" + file.length());
        try {
            RecoverySystem.ProgressListener progressListener = new ProgressListener() {
                @Override
                public void onProgress(int progress) {
                    log("VerfyPackage onProgress: " + progress);
                }
            };
            RecoverySystem.verifyPackage(file, progressListener, null);
            /*
             * write command to file /cache/recovery/command
             * ===============================================
             * --update_package=/cache/v2.3.2.16452.zip
             */
            File recoveryDir = new File(Constants.RECOVERY_DIR);
            if (recoveryDir.mkdirs() || recoveryDir.isDirectory()) {
                File commandFile = new File(recoveryDir, Constants.COMMAND_FILE_NAME);
                if (!commandFile.exists()) {
                    commandFile.createNewFile();
                }
                FileWriter fw = new FileWriter(commandFile, false);
                try {
                    StringBuilder b = new StringBuilder();
                    b.append("--update_package=");
                    b.append(filePath);
                    b.append("\n");
                    b.append("--locale=");
                    b.append(Locale.getDefault().toString());
                    b.append("\n");
                    log("Recovery command=" + b.toString());
                    fw.write(b.toString());
                } finally {
                    fw.close();
                }
            }
        } catch (Exception e) {
            loge(e.toString());
            return false;
        }
        log("Verify successfully, Goodbye world...");
        captureUpdateLog();

        PowerManager pm = (PowerManager) context.getSystemService(Context.POWER_SERVICE);
        pm.reboot("recovery");
        return true;
    }

    /**
     * Get SN+ download path.
     * 
     * @param version Current version code.
     * @return Download path if successful, otherwise empty String.
     */
    @Deprecated
    public static String getSnDownloadPath(String version) {
        if (isEmpty(version) || !isKeyLegal(version)) {
            loge("Fail to get Sn+ download path with illegal version [" + version + "]");
            return "";
        }
        File downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        StringBuilder sb = new StringBuilder(downloadDir.getAbsolutePath());
        sb.append("/ota/");
        sb.append(version);
        File snDownloadDir = new File(sb.toString());
        if (snDownloadDir.mkdirs() || snDownloadDir.isDirectory()) {
            return sb.toString();
        } else {
            loge("Fail to get Sn+ download path with mkdir failed [" + snDownloadDir.getAbsolutePath() + "]");
            return Constants.INVALID_STRING;
        }
    }

    /**
     * Get SN+ download path /storage/emulated/legacy/Download/ota/.
     * 
     * @return SN+ download path String.
     */
    public static String getSnDownloadPath() {
        File downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File otaDir = new File(downloadDir, "ota");
        if (otaDir.mkdirs() || otaDir.isDirectory()) {
            return otaDir.getAbsolutePath();
        } else {
            loge("Fail to get Sn+ download path with mkdir failed [" + otaDir.getAbsolutePath() + "]");
            return Constants.INVALID_STRING;
        }
    }

    /**
     * Check if str is empty or invalid.
     * 
     * @param str String needs to be checked.
     * @return True if str is empty or invalid, otherwise false.
     */
    public static boolean isEmpty(String str) {
        if (TextUtils.isEmpty(str) || Constants.INVALID_STRING.equalsIgnoreCase(str)) {
            return true;
        }
        return false;
    }

    /**
     * Record update log to /sdcard/Download/ota/log.txt
     */
    public static void captureUpdateLog() {
        StringBuilder sbCmd = new StringBuilder("/system/bin/logcat -vtime -df ");
        String downloadDir = getSnDownloadPath();
        File otaDir = new File(downloadDir);
        if (otaDir.mkdirs() || otaDir.isDirectory()) {
            File log = new File(otaDir, "log.txt");
            if (log.exists()) {
                log.delete();
            }
            sbCmd.append(log.getAbsolutePath());
            sbCmd.append(" -s Update");
            log("Capture log cmd=" + sbCmd.toString());

            Process p = null;
            try {
                p = Runtime.getRuntime().exec(sbCmd.toString());
                p.waitFor();
            } catch (IOException e) {
                log("IOException: " + e.toString());
            } catch (InterruptedException e) {
                log("InterruptedException: " + e.toString());
            } finally {
                if (p != null) {
                    p.destroy();
                }
            }
        }
    }

    /**
     * Transform int to String.
     * 
     * @param i Int that needs to be transform.
     * @return String of the result.
     */
    public static String int2String(int i) {
        switch (i) {
            case Constants.SOURCE_ACTIVITY:
                return "Activity";
            case Constants.SOURCE_BOOT:
                return "Boot";
            case Constants.SOURCE_CPE:
                return "Cpe";
            case Constants.VERSION_STATUS_CURRENT:
                return "Current";
            case Constants.VERSION_STATUS_FAILED:
                return "Failed";
            case Constants.VERSION_STATUS_INIT:
                return "Init";
            case Constants.VERSION_STATUS_LAST:
                return "Last";
            case Constants.VERSION_STATUS_NETWORK:
                return "Network";
            case Constants.VERSION_STORAGE_FAIL:
                return "StorageFail";
            default:
                return String.valueOf(i);
        }
    }

    private static void log(String msg) {
        if (Constants.DBG) {
            Log.d(Constants.TAG, "Utils: " + msg);
        }
    }

    private static void loge(String msg) {
        Log.e(Constants.TAG, "Utils: " + msg);
    }

}
