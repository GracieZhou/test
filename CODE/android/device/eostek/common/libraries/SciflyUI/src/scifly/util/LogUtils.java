
package scifly.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStreamReader;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.SystemClock;
import android.util.Log;
import java.lang.System;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;
import scifly.device.Device;
import scifly.um.EosUploadListener;
import scifly.um.EosUploadManager;
import scifly.um.EosUploadTask;
import android.os.SystemProperties;
import scifly.provider.SciflyStore.Global;
import scifly.permission.Permission;
import android.os.FileUtils;
import android.os.Environment;
import android.net.TrafficStats;
import scifly.storage.StorageManagerExtra;
import android.text.TextUtils;

import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.view.WindowManager;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.SurfaceControl;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * @copyright all rights reserved by EosTek
 * @comment
 *      Class LogUtils provided 4 static public methods to catch system log and snplus log and trace file , 
 *      but **NOTICE** that you must add the permission **<uses-permission android:name="android.permission.READ_LOGS" />**
 *      in AndroidManifest.xml before you call them in your APKS, and the APK must be signed .
 *      
 *      a. public static String getSystemLog() 
 *      **It returns the path of log.txt file which was created by "logcat -df ~/log.txt"**
 *     
 *      b. public static String getSystemLog(String packagename)
 *      **It returns the path of log.txt file which was created by "logcat -df ~/log.txt -s packagename"**
 *      
 *      c. public static String getSnplusLog()
 *      **It returns the path of status.html file **
 *      
 *      d. public static String getTracesFile()
 *      **It returns the path of traces.txt file which was created by "/data/anr/traces.txt"**
 *      **You should ensure the apks to be allowed to access this the "traces.txt"**
 *      
 *      At last , you can debug these methods in your shell with "logcat -s LogUtils" . 
 *
 */

/**
 * @author frank.zhang@ieostek.com
 * @since API 2.0
 * @modified at 2014-6-10
 */
public class LogUtils {

    private static final String TAG = "LogUtils";

    private static final boolean DBG = false;

    private static final String LOG_DIR = "/cache/log/";

    private static final String mLogcatCmd = "/system/bin/logcat -v time -df ";

    /**
     * @return System log file path to the caller if failed, than return null;
     */
    private static String getSystemLog() {

        File log_dir = new File(LOG_DIR);
        if (!log_dir.exists()) {
            log_dir.mkdirs();
        }
        long timestamp = System.currentTimeMillis();
        String logPath = LOG_DIR + "system_log-" + timestamp + ".txt";
        String cmd = mLogcatCmd + logPath;
        if (redirectLog(cmd, logPath)) {
            return logPath;
        } else {
            Log.d(TAG, "Pack system log failed !");
            return null;
        }
    }

    /**
     * @param packagename is a filter , which usually as some class name
     * @return log file path to the caller if failed, than return null;
     */
    private static String getSystemLog(String packagename) {
        File log_dir = new File(LOG_DIR);
        if (!log_dir.exists()) {
            log_dir.mkdirs();
        }

        long timestamp = System.currentTimeMillis();
        String logPath = LOG_DIR + packagename + "-" + timestamp + ".txt";
        String cmd = mLogcatCmd + logPath + " -s " + packagename;
        Log.d(TAG, "Starting packing package log:" + cmd);
        if (redirectLog(cmd, logPath)) {
            return logPath;
        } else {
            Log.e(TAG, "Pack package log failed !");
            return null;
        }
    }

    private static String getSnplusCachePath() {
        String SNPLUS_CONFIG_PATH = "/system/etc/snplus_config.ini";
        String CACHE_PATH_START_STR = "CACHE_PATH = ";
        String CACHE_PATH_END_STR = ";";

        BufferedReader br = null;
        String line = null;
        try {
            br = new BufferedReader(new InputStreamReader(new FileInputStream(SNPLUS_CONFIG_PATH)));
            while ((line = br.readLine()) != null) {
                if (line.contains(CACHE_PATH_START_STR)) {
                    int start = line.indexOf(CACHE_PATH_START_STR) + CACHE_PATH_START_STR.length();
                    int end = line.indexOf(CACHE_PATH_END_STR);
                    return new String(line.substring(start, end));
                }
            }
        } catch (FileNotFoundException e1) {
            Log.e(TAG, e1.getLocalizedMessage());
        } catch (IOException e) {
            Log.e(TAG, e.getLocalizedMessage());
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    Log.e(TAG, e.getLocalizedMessage());
                }
            }
        }

        return null;
    }

    /**
     * @return sn+ log path to the caller if failed, than return null;
     */
    private static String getSnplusLog() {
        String SNPLUS_LOG_SUFFIX = "/play/status.html";
        String statusFile = getSnplusCachePath() + SNPLUS_LOG_SUFFIX;
        File logFile = new File(statusFile);
        if (logFile.exists() && logFile.canRead()) {
            return statusFile;
        } else {
            Log.e(TAG, "Can't open " + statusFile);
            return null;
        }
    }

    /**
     * @since API 2.1
     * @return /data/anr/trace.txt path to the caller if failed, than return
     *         null;
     */
    private static String getTracesFile() {
        String traceFile = "/data/anr/traces.txt";
        File logFile = new File(traceFile);
        if (logFile.exists() && logFile.canRead()) {
            return traceFile;
        } else {
            Log.e(TAG, "Can't open " + traceFile);
            return null;
        }
    }

    private static boolean redirectLog(String cmd, String logPath) {
        if (cmd.equals("") || cmd == null) {
            return false;
        }
        Log.d(TAG, "packingCmd = " + cmd);

        try {
            File logFile = new File(logPath);
            if (logFile.exists()) {
                logFile.delete();
            }
            Process p = Runtime.getRuntime().exec(cmd);
            p.waitFor();

            File log = new File(logPath);
            if (log.exists()) {
                Log.d(TAG, "logfile lenght:" + log.length() + " Byte(s)");
            }

            return true;
        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            Log.e(TAG, e.getLocalizedMessage());
            return false;
        }
    }

    public static void collectDeviceInfo(Context ctx) {
        Map<String, String> infos = new HashMap<String, String>();
        try {
            PackageManager pm = ctx.getPackageManager();
            PackageInfo pi = pm.getPackageInfo(ctx.getPackageName(), PackageManager.GET_ACTIVITIES);
            if (pi != null) {
                String versionName = pi.versionName == null ? "null" : pi.versionName;
                String versionCode = pi.versionCode + "";
                infos.put("versionName", versionName);
                infos.put("versionCode", versionCode);
            }
        } catch (NameNotFoundException e) {
            Log.e(TAG, "an error occured when collect package info", e);
        }
        Field[] fields = Build.class.getDeclaredFields();
        for (Field field : fields) {
            try {
                field.setAccessible(true);
                infos.put(field.getName(), field.get(null).toString());
                Log.d(TAG, field.getName() + " : " + field.get(null));
            } catch (Exception e) {
                Log.e(TAG, "an error occured when collect crash info", e);
            }
        }
    }

    public static boolean compressLog(String[] srcFiles, String zipFile) throws IOException {
        if (srcFiles.length == 0 || zipFile.equals("") || zipFile == null) {
            return false;
        }

        for (int i = 0; i < srcFiles.length; i++) {
            Log.d(TAG, "Source File:" + srcFiles[i]);
        }
        Log.d(TAG, "Zip File:" + zipFile);

        File zipLog = new File(zipFile);
        if (zipLog.exists()) {
            zipLog.delete();
        }

        boolean flag = true;
        ZipOutputStream zos = null;
        try {
            zos = new ZipOutputStream(new FileOutputStream(zipFile));
            byte[] buffer = new byte[1024];

            for (int i = 0; i < srcFiles.length; i++) {
                File file = new File(srcFiles[i]);

                String fileName = null;
                if (file.getAbsolutePath().contains(LOG_DIR) && file.getAbsolutePath().contains("-")) {
                    int index = file.getName().lastIndexOf('-');
                    int suffix = file.getName().lastIndexOf('.');
                    fileName = file.getName().substring(0, index) + file.getName().substring(suffix);
                } else if (file.getAbsolutePath().contains("recovery/last_log")) {
                    fileName = "Recovery.log";
                } else {
                    fileName = file.getName();
                }

                zos.putNextEntry(new ZipEntry(fileName));

                FileInputStream is = new FileInputStream(srcFiles[i]);
                int len = 0;
                while ((len = is.read(buffer)) != -1)
                    zos.write(buffer, 0, len);
                is.close();
            }
        } catch (IOException e) {
            Log.e(TAG, e.getLocalizedMessage());
            flag = false;
        } finally {
            if (zos != null)
                zos.close();

            for (int i = 0; i < srcFiles.length; i++) {
                if (srcFiles[i].contains(LOG_DIR)) {
                    File logFile = new File(srcFiles[i]);
                    if (logFile.exists()) {
                        logFile.delete();
                    }
                }
            }
        }

        return flag;
    }

    public static String saveDeviceInfo2File(HashMap<String, String> param, Context ctx) throws IOException {
        if (param == null || param.isEmpty()) {
            return null;
        }

        File log_dir = new File(LOG_DIR);
        if (!log_dir.exists()) {
            log_dir.mkdirs();
        }

        boolean flag = true;
        StringBuffer sb = new StringBuffer();
        long timestamp = System.currentTimeMillis();
        String path = LOG_DIR + "Deviceinfo-" + timestamp + ".txt";
        DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.US);
        long rtc = SystemClock.elapsedRealtime() / 1000;
        String time = formatter.format(new Date());
        String wifidisconnect = SystemProperties.get("net.wifi_disconnect_times", "0");
        String log_debug = Global.getString(ctx.getContentResolver(), Global.LOG_ENABLE);
        if (log_debug == null) {
            log_debug = "0";
        }

        String fsStat = MyExec("df /data /system /cache /snplus /mnt/sdcard/ /mnt/external_sd/");

        sb.append("Timestamp=" + timestamp + "\n");
        sb.append("Time=" + time + "\n");
        sb.append("PowerOnTime=" + rtc + "s\n");
        sb.append(String.format("TotalDownload=%.2fMB\n", ((double) TrafficStats.getTotalRxBytes()) / 1024 / 1024));
        sb.append(String.format("TotalUpload=%.2fMB\n", ((double) TrafficStats.getTotalTxBytes()) / 1024 / 1024));
        sb.append("WifiDisconnectTimes=" + wifidisconnect + "\n");
        sb.append("scifly.debug.log.enable=" + log_debug + "\n");
        sb.append("SN=" + Device.getDeviceSN() + "\n\n");

        for (Map.Entry<String, String> entry : param.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(key + "=" + value + "\n");
        }

        if (fsStat != null && !fsStat.isEmpty()) {
            sb.append("\nFileSystem State:\n" + fsStat + "\n");
        }

        FileOutputStream fos = null;
        try {
            fos = new FileOutputStream(path);
            fos.write(sb.toString().getBytes());
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
            flag = false;
        } finally {
            if (fos != null)
                fos.close();
        }

        if (flag) {
            if (!getMemRankInfo(ctx, path)) {
                Log.e(TAG, "getMemRankInfo failed .");
            }
        }

        return flag ? path : null;
    }

    private static String[] getSnplusDumpFiles() {
        String PLAY_DUMP_DIR = getSnplusCachePath() + "/play";
        File[] dumpFiles = null;
        try {
            File play_dir = new File(PLAY_DUMP_DIR);
            dumpFiles = play_dir.listFiles(new FilenameFilter() {

                @Override
                public boolean accept(File dir, String filename) {
                    if (filename.contains(".dmp")) {
                        return true;
                    } else {
                        return false;
                    }
                }
            });

        } catch (NullPointerException e) {
            Log.e(TAG, e.getMessage());
            return new String[0];
        } catch (SecurityException e) {
            Log.e(TAG, e.getMessage());
            return new String[0];
        }

        if (dumpFiles != null && dumpFiles.length > 0) {
            List<String> srcFiles = new ArrayList<String>();
            for (int i = 0; i < dumpFiles.length; i++) {
                if (dumpFiles[i].length() < 5 * 1024 * 1024 && dumpFiles[i].canRead())
                    srcFiles.add(dumpFiles[i].getAbsolutePath());
            }
            String[] files = new String[srcFiles.size()];
            srcFiles.toArray(files);
            return files;
        } else {
            Log.e(TAG, "No dump file found .");
            return new String[0];
        }
    }

    private static String getRecoveryLog() {
        String LOG_PATH = "/cache/recovery/last_log";
        File logFile = new File(LOG_PATH);
        if (logFile.exists() && logFile.length() > 0 && logFile.canRead()) {
            return LOG_PATH;
        } else {
            Log.e(TAG, "Recovery last_log is empty !");
            return null;
        }
    }

    private static boolean takeScreenShot(Context context, String savePath) {
        if (TextUtils.isEmpty(savePath)) {
            Log.e(TAG, "save path is null.");
            return false;
        }

        Display display = ((WindowManager) context.getSystemService(Context.WINDOW_SERVICE)).getDefaultDisplay();
        DisplayMetrics displayMetrics = new DisplayMetrics();
        display.getRealMetrics(displayMetrics);
        float[] dims = {
                displayMetrics.widthPixels, displayMetrics.heightPixels
        };
        Bitmap screenBitmap = SurfaceControl.screenshot((int) dims[0], (int) dims[1]);
        if (screenBitmap == null) {
            Log.e(TAG, "take screen shot failed.");
            return false;
        }
        // Optimizations
        screenBitmap.setHasAlpha(false);
        screenBitmap.prepareToDraw();

        // convert format to RGB_565
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        screenBitmap.compress(Bitmap.CompressFormat.JPEG, 100, baos);

        BitmapFactory.Options newOpts = new BitmapFactory.Options();
        newOpts.inJustDecodeBounds = false;
        newOpts.inSampleSize = 1;
        newOpts.inPreferredConfig = Config.RGB_565;
        ByteArrayInputStream isBm = new ByteArrayInputStream(baos.toByteArray());

        screenBitmap.recycle();
        screenBitmap = BitmapFactory.decodeStream(isBm, null, newOpts);

        // now wirte bitmap to image file
        File f = new File(savePath);
        if (f.exists()) {
            f.delete();
        }

        FileOutputStream out = null;
        try {
            out = new FileOutputStream(f);
            screenBitmap.compress(Bitmap.CompressFormat.JPEG, 50, out);
            out.flush();
            screenBitmap.recycle();
        } catch (FileNotFoundException e) {
            Log.e(TAG, e.getMessage());
            return false;
        } catch (IOException e) {
            Log.e(TAG, e.getMessage());
            return false;
        } finally {
            if (null != out) {
                try {
                    out.close();
                } catch (IOException e) {
                    Log.e(TAG, e.getMessage());
                    return false;
                }
            }
        }

        return true;
    }

    public static final int TYPE_AUTO_UPLOAD_LOG = 1;

    public static final int TYPE_MANUAL_UPLOAD_LOG = 2;

    public static void captureLog(final Context ctx, String text, final IResultListener resultListener, int type) {
        doCaptureLog(ctx, text, false, resultListener, type);
    }

    public static void captureLog(final Context ctx, String text, final IResultListener resultListener) {
        doCaptureLog(ctx, text, false, resultListener, TYPE_AUTO_UPLOAD_LOG);
    }

    public static void captureLog(final Context ctx, String text, final boolean takeScreenShot,
            final IResultListener resultListener) {
        doCaptureLog(ctx, text, takeScreenShot, resultListener, TYPE_AUTO_UPLOAD_LOG);
    }

    private static void doCaptureLog(final Context ctx, String text, final boolean takeScreenShot,
            final IResultListener resultListener, final int type) {
        if (ctx == null) {
            resultListener.captureResult(false);
            return;
        }

        final long start = System.currentTimeMillis();

        if (text.equals("") || text == null) {
            text = "NONE";
        }

        if (DBG) {
            collectDeviceInfo(ctx);
        }

        final String remarks = text;

        new Thread() {
            public void run() {
                try {
                    long timestamp = System.currentTimeMillis();
                    final String zipLogFile = LOG_DIR + "capture_log-" + timestamp + ".zip";

                    HashMap<String, String> param = new HashMap<String, String>();
                    param.put("ifid", Device.getIfid());
                    param.put("devName", Device.getDeviceName(ctx));
                    param.put("mac", Device.getHardwareAddress(ctx));
                    param.put("ver", Device.getVersion());
                    param.put("text", remarks);
                    param.put("type", type + "");
                    param.put("bb", Device.getBb());
                    param.put("devCode", Device.getDeviceCode());
                    param.put("ttag", Device.getTtag());

                    String deviceInfo = saveDeviceInfo2File(param, ctx);
                    String snplusLog = getSnplusLog();
                    String tracesFile = getTracesFile();
                    String recoveryLog = getRecoveryLog();
                    String[] dumpFiles = getSnplusDumpFiles();
                    String systemLog = getSystemLog();

                    String[] srcFiles = null;
                    List<String> srcfileList = new ArrayList<String>();
                    if (deviceInfo != null && !deviceInfo.isEmpty()) {
                        srcfileList.add(deviceInfo);
                    }
                    if (systemLog != null && !systemLog.isEmpty()) {
                        srcfileList.add(systemLog);
                    }
                    if (snplusLog != null && !snplusLog.isEmpty()) {
                        srcfileList.add(snplusLog);
                    }
                    if (tracesFile != null && !tracesFile.isEmpty()) {
                        srcfileList.add(tracesFile);
                    }
                    if (recoveryLog != null && !recoveryLog.isEmpty()) {
                        srcfileList.add(recoveryLog);
                    }
                    if (dumpFiles.length > 0) {
                        for (int i = 0; i < dumpFiles.length; i++) {
                            srcfileList.add(dumpFiles[i]);
                        }
                    }
                    if (takeScreenShot) {
                        String ssPath = LOG_DIR + "ScreenShot-" + timestamp + ".jpg";
                        if (takeScreenShot(ctx, ssPath)) {
                            srcfileList.add(ssPath);
                        }
                    }

                    if (srcfileList.size() > 0) {
                        if (DBG) {
                            Log.d(TAG, srcfileList.size() + " file(s) will upload to server.");
                        }
                        srcFiles = new String[srcfileList.size()];
                        srcfileList.toArray(srcFiles);
                    }

                    if (srcFiles.length > 0) {
                        if (compressLog(srcFiles, zipLogFile)) {
                            EosUploadListener listener = new EosUploadListener() {
                                @Override
                                public void onUploadCompleted(int errCode) {
                                    if (0 == errCode) {

                                        File file = new File(zipLogFile);
                                        if (file.exists()) {
                                            file.delete();
                                        }

                                        resultListener.captureResult(true);
                                    } else {

                                        // copy zip log file to /sdcard/syslog/
                                        StorageManagerExtra storageManager = StorageManagerExtra.getInstance(ctx);
                                        String external_sd = storageManager.getExternalSdcardPath();
                                        File mediaRootDir = null;

                                        if (!TextUtils.isEmpty(external_sd)) {
                                            mediaRootDir = new File(external_sd);
                                        } else {
                                            mediaRootDir = Environment.getExternalStorageDirectory();
                                        }

                                        File logDir = new File(mediaRootDir, "syslog");
                                        if (null != logDir) {
                                            if (!logDir.exists()) {
                                                logDir.mkdir();
                                            }

                                            DateFormat formatter = new SimpleDateFormat("yyyy-MM-dd-HH-mm-ss",
                                                    Locale.US);
                                            String time = formatter.format(new Date());
                                            String readableFile = logDir.toString() + "/capture_log-" + time + ".zip";
                                            boolean bcopy = FileUtils.copyFile(new File(zipLogFile), new File(
                                                    readableFile));
                                            if (bcopy) {
                                                Log.i(TAG, "Dump log " + zipLogFile + " to " + readableFile);
                                            }
                                        }

                                        resultListener.captureResult(false);
                                    }

                                    long end = System.currentTimeMillis();
                                    Log.i(TAG, "Upload Completed, code:" + errCode + "\ntatol spent:" + (end - start)
                                            + "ms");
                                }
                            };

                            //String requestUrl = "http://acs.babao.com:8013/TMS/interface/clientService.jsp";
                            String requestUrl = "http://bigdata.88popo.com:8011/userFeed/SysIdea";
                            Log.d(TAG, "requestUrl=" + requestUrl);
                            EosUploadTask task = new EosUploadTask(0, zipLogFile, requestUrl, param, listener);
                            EosUploadManager uploadManager = new EosUploadManager();
                            uploadManager.addTask(task);
                            // now process other files
                            File logCacheDir = new File(LOG_DIR);
                            if (logCacheDir.exists()) {
                                String[] files = logCacheDir.list(new FilenameFilter() {
                                    @Override
                                    public boolean accept(File dir, String filename) {
                                        if (filename.endsWith(".zip") && !zipLogFile.equals(LOG_DIR + filename)) {
                                            return true;
                                        } else {
                                            return false;
                                        }
                                    }
                                });

                                if (null != files && files.length > 0) {
                                    for (final String file : files) {
                                        EosUploadListener listener0 = new EosUploadListener() {
                                            @Override
                                            public void onUploadCompleted(int errCode) {
                                                if (0 == errCode) {
                                                    File f = new File(LOG_DIR + file);
                                                    if (f.exists()) {
                                                        f.delete();
                                                        Log.i(TAG, "delete " + f.toString() + " done.");
                                                    } else {
                                                        Log.i(TAG, f.toString() + " does not exists !");
                                                    }
                                                }

                                                Log.i(TAG, "Upload old log " + file + " ,errCode:" + errCode);
                                            }
                                        };

                                        uploadManager.addTask(new EosUploadTask(0, LOG_DIR + file, requestUrl, param,
                                                listener0));
                                    }
                                }
                            }

                        } else {
                            Log.e(TAG, "Compress source files failed !");
                            resultListener.captureResult(false);
                        }
                    }
                } catch (IOException e) {
                    Log.e(TAG, e.getMessage());
                    resultListener.captureResult(false);
                }
            }
        }.start();
    }

    public static String MyExec(String cmd) {
        if (cmd == null || cmd.isEmpty()) {
            return null;
        }

        if (DBG) {
            Log.d(TAG, cmd);
        }

        Process process = null;
        BufferedReader reader = null;
        StringBuffer output = new StringBuffer();

        try {
            process = Runtime.getRuntime().exec(cmd);
            process.waitFor();

            reader = new BufferedReader(new InputStreamReader(process.getInputStream()));
            int read;
            char[] buffer = new char[4096];
            while ((read = reader.read(buffer)) > 0) {
                output.append(buffer, 0, read);
            }

            if (DBG) {
                Log.d(TAG, output.toString());
            }

        } catch (IOException e) {
            throw new RuntimeException(e);
        } catch (InterruptedException e) {
            throw new RuntimeException(e);
        } finally {
            process.destroy();

            try {
                if (reader != null)
                    reader.close();
            } catch (IOException e) {
                Log.e(TAG, e.getMessage());
            }
        }

        return output.toString();
    }

    private static boolean getMemRankInfo(Context ctx, String path) {

        long start = System.currentTimeMillis();
        String packageName = ctx.getPackageName();
        String digitSingnature = "";
        if (packageName.equals("com.android.settings")) {
            digitSingnature = "JCheb2lkLnNldHRpbmdzanJt";
        } else if (packageName.contains("com.eostek.scifly.messagecenter")) {
            digitSingnature = "JCheZW50ZXI6cmVtb3RlanJt";
        } else if (packageName.equals("android")) { // for
                                                    // ActivityManagerService
                                                    // capture crash
            digitSingnature = "JCheeXN0ZW1fc2VydmVyanJt"; // actually is
                                                          // system_server
        }

        Permission shell = new Permission(digitSingnature);
        shell.exec("echo procrank: >> " + path);
        boolean b = shell.exec("procrank >> " + path);

        if (true) {
            long end = System.currentTimeMillis();
            Log.d(TAG, "[" + packageName + "] request exec [procrank >> " + path + "] result:" + b + " spent:"
                    + (end - start) + "ms");
        }

        return b;
    }

    public interface IResultListener {
        void captureResult(boolean result);
    }

    private LogUtils() {
    }
}
