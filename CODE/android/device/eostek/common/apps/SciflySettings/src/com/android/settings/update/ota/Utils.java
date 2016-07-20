
package com.android.settings.update.ota;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.File;
import java.io.FileWriter;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

import scifly.device.Device;
import scifly.um.EosUploadListener;
import scifly.um.EosUploadManager;
import scifly.um.EosUploadTask;
import scifly.util.LogUtils;
import scifly.util.RecoverySystemExtra;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;
import android.widget.Toast;

public class Utils implements Constants {

    private static Logger sLog = new Logger(Utils.class);

    public static String getProp(String prop, String fallback) {
        try {
            Process process = Runtime.getRuntime().exec("getprop " + prop);
            BufferedReader bufferedReader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            StringBuilder log = new StringBuilder();
            String line;
            while ((line = bufferedReader.readLine()) != null) {
                log.append(line);
            }
            return log.toString();
        } catch (IOException e) {
            // Runtime error
        }
        return fallback;
    }

    public static String getDateAndTime() {
        return new SimpleDateFormat("yyyy-MM-dd.HH.mm.ss").format(new Date(System.currentTimeMillis()));
    }

    public static String getDateAndTime(long timestap) {
        return new SimpleDateFormat("yyyy-MM-dd HH:mm:ss").format(new Date(timestap * 1000));
    }

    public static boolean isNetworkAvailable(Context context) {
        ConnectivityManager connectivityManager = (ConnectivityManager) context
                .getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo activeNetworkInfo = connectivityManager.getActiveNetworkInfo();
        return activeNetworkInfo != null && activeNetworkInfo.isConnected();
    }

    public static void showToastOnUiThread(final Context context, final int resourceId) {
        ((Activity) context).runOnUiThread(new Runnable() {

            public void run() {
                Toast.makeText(context, resourceId, Toast.LENGTH_LONG).show();
            }
        });
    }

    public static void showToastOnUiThread(final Context context, final String string) {
        if (context instanceof Activity) {
            ((Activity) context).runOnUiThread(new Runnable() {

                public void run() {
                    Toast.makeText(context, string, Toast.LENGTH_LONG).show();
                }
            });
        }
    }

    public static String su(String[] commands) {
        try {
            Process p = Runtime.getRuntime().exec("su");
            DataOutputStream os = new DataOutputStream(p.getOutputStream());
            for (int i = 0; i < commands.length; i++) {
                os.writeBytes(commands[i] + "\n");
            }
            os.writeBytes("sync\n");
            os.writeBytes("exit\n");
            os.flush();
            p.waitFor();
            return getStreamLines(p.getInputStream());
        } catch (Exception ex) {
            ex.printStackTrace();
            return null;
        }
    }

    @SuppressWarnings("deprecation")
    private static String getStreamLines(final InputStream is) {
        String out = null;
        StringBuffer buffer = null;
        final DataInputStream dis = new DataInputStream(is);

        try {
            if (dis.available() > 0) {
                buffer = new StringBuffer(dis.readLine());
                while (dis.available() > 0) {
                    buffer.append("\n").append(dis.readLine());
                }
            }
            dis.close();
        } catch (Exception ex) {
            ex.printStackTrace();
        }
        if (buffer != null) {
            out = buffer.toString();
        }
        return out;
    }

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
            return;
        }
        // List all files in directory /cache and record their names into
        // StringBuilder.
        StringBuilder sb = new StringBuilder();
        // Never delete lastFile at this time.
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
                deleteFileAndRecord(subFile, sb);
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
        }
        EosUploadListener listener = new EosUploadListener() {
            @Override
            public void onUploadCompleted(int errCode) {
            }
        };
        EosUploadTask task = new EosUploadTask(0, zipLogFile, Constants.DEFAULT_SERVER_URL, map, listener);
        eum.addTask(task);
    }

    /**
     * Get SN+ download path /storage/emulated/legacy/Download/ota/.
     * 
     * @return SN+ download path String.
     */
    public static String getP2pDownloadDir() {
        File downloadDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
        File otaDir = new File(downloadDir, "ota");
        if (otaDir.mkdirs() || otaDir.isDirectory()) {
            return otaDir.getAbsolutePath();
        } else {
            return null;
        }
    }

    /**
     * Record update log to /sdcard/Download/ota/log.txt
     */
    public static void captureOTALog() {
        StringBuilder sbCmd = new StringBuilder("/system/bin/logcat -vtime -df ");
        String downloadDir = getP2pDownloadDir();
        File otaDir = new File(downloadDir);
        if (otaDir.mkdirs() || otaDir.isDirectory()) {
            File log = new File(otaDir, "OTA.log");
            if (log.exists()) {
                log.delete();
            }
            sbCmd.append(log.getAbsolutePath());
            sbCmd.append(" -s OTA DownloadManager StreamNetServer AndroidRuntime MessageQueue");

            Process p = null;
            try {
                p = Runtime.getRuntime().exec(sbCmd.toString());
                p.waitFor();
            } catch (IOException e) {
            } catch (InterruptedException e) {
            } finally {
                if (p != null) {
                    p.destroy();
                }
            }
        }
    }

    public static boolean lastOTAFail() {
        int state = RecoverySystemExtra.getSystemUpgradeState();
        switch (state) {
            case RecoverySystemExtra.LAST_UPGRADE_FAILED:
            case RecoverySystemExtra.UPGRADE_FAILED:
                return true;
            default:
                break;
        }
        return false;
    }

    public static String getHexMac(Context context) {
        String mac = Device.getHardwareAddress(context);
        if (!TextUtils.isEmpty(mac)) {
            mac = macToHex(mac);
        } else {
            mac = "000000000000";
        }
        return mac;
    }

    public static String macToHex(String mac) {
        if (TextUtils.isEmpty(mac)) {
            return null;
        }

        String[] strArray = mac.split(":");
        StringBuffer modifiedMac = new StringBuffer();
        for (int i = 0; i < strArray.length; i++) {
            modifiedMac.append(strArray[i]);
        }

        return modifiedMac.toString();
    }

    public static boolean writeForceOTAFlags(String filePath) {
        BootLoader bootLoader = new BootLoader();
        if (bootLoader.setBootloader()) {

            File file = new File(filePath);
            if (!file.exists()) {
                return false;
            }

            File recoveryDir = new File(RECOVERY_DIR);
            if (recoveryDir.mkdirs() || recoveryDir.isDirectory()) {
                File commandFile = new File(recoveryDir, COMMAND_FILE_NAME);
                commandFile.delete();
                FileWriter fw = null;
                try {
                    fw = new FileWriter(commandFile);
                    StringBuilder b = new StringBuilder();
                    b.append("--update_package=");
                    b.append(filePath);
                    b.append("\n");
                    b.append("--locale=");
                    b.append(Locale.getDefault().toString());
                    b.append("\n");
                    fw.write(b.toString());
                    sLog.info("command:" + b.toString());
                    return true;
                } catch (IOException e) {
                    sLog.error("error:" + e.getMessage());
                    return false;
                } finally {
                    try {
                        if (fw != null)
                            fw.close();
                    } catch (IOException e) {
                        sLog.error("error:" + e.getMessage());
                        return false;
                    }
                }
            }
        }
        return false;
    }

}
