
package com.eostek.tv.launcher.util;

/**
 * usage:
 * CrashHandler crashHandler = CrashHandler.getInstance();
 // 注册crashHandler
 crashHandler.init(getApplicationContext());
 */

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.io.Writer;
import java.lang.Thread.UncaughtExceptionHandler;
import java.lang.reflect.Field;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.eostek.tv.launcher.R;

import android.content.Context;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.content.pm.PackageManager.NameNotFoundException;
import android.os.Build;
import android.os.Looper;
import android.util.Log;
import android.widget.Toast;

/*
 * projectName： TVLauncher
 * moduleName： CrashHandler.java
 *
 * @author chadm.xiang
 * @version 1.0.0
 * @time  2014-9-30 下午5:01:41
 * @Copyright © 2014 Eos Inc.
 */

/**
 * Abnormal class, capture Exception, when UncaughtException occurs, save Crash
 * time, and will be uploaded to the server Crash journal, At the same time, add
 * a continuous Crash treatment, avoiding abnormal unrecoverable problems
 **/
public final class CrashHandler implements UncaughtExceptionHandler {

    public static final String TAG = "CrashHandler";

    // The system default UncaughtException handler
    private Thread.UncaughtExceptionHandler mDefaultHandler;

    private static CrashHandler instance;

    private Context mContext;

    // save device infomation and exception infomation
    private Map<String, String> infos = new HashMap<String, String>();

    private DateFormat formatter = new SimpleDateFormat("yyyyMMdd_HH:mm:ss");

    private final long ONE_MINUTE = 60 * 1000;

    private final int CRASHED_TIMES = 3;

    /** Keep single CrashHandler object */
    private CrashHandler() {
    }

    /**
     * get CrashHandler Instance
     * 
     * @return The instance of CrashHandler object
     */
    public static CrashHandler getInstance() {
        if (instance == null) {
            synchronized (CrashHandler.class) {
                if (instance == null) {
                    instance = new CrashHandler();
                }
            }
        }
        return instance;
    }

    /**
     * init some setting
     * 
     * @param context
     */
    public void init(Context context) {
        mContext = context;
        mDefaultHandler = Thread.getDefaultUncaughtExceptionHandler();
        // set default UncaughtException Handler to this
        Thread.setDefaultUncaughtExceptionHandler(this);
    }

    /**
     * handler the uncaught Exception when it occurs
     */
    @Override
    public void uncaughtException(Thread thread, Throwable ex) {
        Log.e(TAG, "Fatal Exception : " + mContext.getPackageName());
        ex.printStackTrace();
        if (!handleException(ex) && mDefaultHandler != null) {
            // if the exception do ont handled,Let the system default exception
            // handler to handle
            mDefaultHandler.uncaughtException(thread, ex);
        } else {
            try {
                Thread.sleep(3000);
            } catch (InterruptedException e) {
                Log.e(TAG, "error : ", e);
            }
            // exit
            android.os.Process.killProcess(android.os.Process.myPid());
            System.exit(1);
        }
    }

    /**
     * Custom error handling, collect error information to send ,error reports
     * and other operations are completed in this
     * 
     * @param ex
     * @return true if handle,else false
     */
    private boolean handleException(Throwable ex) {
        if (ex == null) {
            return false;
        }
        // shwo toast
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                Toast.makeText(mContext, mContext.getResources().getString(R.string.crash_toast), Toast.LENGTH_LONG)
                        .show();
                Looper.loop();
            }
        }.start();
        // collect device infomation
        // collectDeviceInfo(mContext);
        // save carsh infomation
        // saveCrashInfo2File(ex);

        // save the time when crash
        saveCrashTime2File();
        return true;
    }

    /**
     * collect device infomation
     * 
     * @param ctx
     */
    public void collectDeviceInfo(Context ctx) {
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

    /**
     * save the crash infomation fo file
     * 
     * @param ex
     * @return File name
     */
    private String saveCrashInfo2File(Throwable ex) {
        StringBuffer sb = new StringBuffer();
        for (Map.Entry<String, String> entry : infos.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            sb.append(key + "=" + value + "\n");
        }

        Writer writer = new StringWriter();
        PrintWriter printWriter = new PrintWriter(writer);
        ex.printStackTrace(printWriter);
        Throwable cause = ex.getCause();
        while (cause != null) {
            cause.printStackTrace(printWriter);
            cause = cause.getCause();
        }
        printWriter.close();
        String result = writer.toString();
        sb.append(result);
        try {
            long timestamp = System.currentTimeMillis();
            String time = formatter.format(new Date());

            String fileName = "crash-" + time + "-" + timestamp + ".log";
            String path = mContext.getFilesDir().getParent() + File.separator + "crashed" + File.separator;
            File dir = new File(path);
            if (!dir.exists()) {
                dir.mkdirs();
            }
            FileOutputStream fos = new FileOutputStream(path + fileName);
            fos.write(sb.toString().getBytes());
            fos.close();

            // write the crash time to the file
            String crashTime = "crashTime.txt";
            fos = new FileOutputStream(path + crashTime, true);
            fos.write((String.valueOf(timestamp) + "\n").getBytes());
            fos.close();

            // to avoid upload crash log when debug
            if (UIUtil.isSystemApp(mContext, mContext.getPackageName())) {
                // upload the log to server
                UIUtil.uploadLog(mContext, mContext.getPackageName() + " crashed.");
            }

            // check crash time
            checkCrashTime();

            return fileName;
        } catch (Exception e) {
            Log.e(TAG, "an error occured while writing file...", e);
        }
        return null;
    }

    /**
     * save the crash time to file
     */
    private void saveCrashTime2File() {
        long timestamp = System.currentTimeMillis();
        String path = mContext.getFilesDir().getParent() + File.separator + "crashed";
        // write the crash time to the file
        String crashTime = "crashTime.txt";
        FileOutputStream fos;
        try {
            File dir = new File(path);
            if (!dir.exists()) {
                Log.v(TAG, "crashed diretory do not exists");
                dir.mkdirs();
            }
            File file = new File(path + File.separator + crashTime);
            if (!file.exists()) {
                Log.v(TAG, "crashTime.txt do not exists");
                file.createNewFile();
            }
            fos = new FileOutputStream(file, true);
            fos.write((String.valueOf(timestamp) + "\n").getBytes());
            fos.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // to avoid upload crash log when debug
        if (UIUtil.isSystemApp(mContext, mContext.getPackageName())) {
            // upload the log to server
            UIUtil.uploadLog(mContext, mContext.getPackageName() + " crashed.");
        }

        // check crash time
        try {
            checkCrashTime();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * check the crashTime.txt,if the interval time of the last 3 crash record
     * less than 1 min,delete the db file
     * 
     * @throws IOException
     */
    private void checkCrashTime() throws IOException {
        String path = mContext.getFilesDir().getParent() + "/crashed/crashTime.txt";
        File file = new File(path);
        if (!file.exists()) {
            return;
        }
        try {
            FileReader reader = new FileReader(file);
            BufferedReader buf = new BufferedReader(reader);
            String string = buf.readLine();
            List<String> line = new ArrayList<String>();
            while (string != null) {
                line.add(string);
                string = buf.readLine();
            }
            buf.close();
            reader.close();

            // get the last three crash record,
            if (line.size() >= CRASHED_TIMES) {
                int size = line.size();
                long crash3 = Long.valueOf(line.get(size - 1).trim());
                long crash2 = Long.valueOf(line.get(size - 2).trim());
                long crash1 = Long.valueOf(line.get(size - 3).trim());
                long interval1 = crash2 - crash1;
                long interval2 = crash3 - crash2;

                // if the interval time is less ONE_MINUTE,clear db record
                if (interval1 < ONE_MINUTE && interval2 < ONE_MINUTE) {
                    deleteDBFile();
                }
            }
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * if lanuncher crashed too much,delete the db file
     */
    private void deleteDBFile() {
        File dbDirectoryFile = new File(mContext.getFilesDir().getParent() + "/databases");
        if (dbDirectoryFile.isDirectory()) {
            File[] listFiles = dbDirectoryFile.listFiles();
            if (listFiles != null) {
                for (File file : listFiles) {
                    file.delete();
                }
                Log.v(TAG, "delete db file");
            }
        }
        // show toast
        new Thread() {
            @Override
            public void run() {
                Looper.prepare();
                Toast.makeText(mContext, mContext.getResources().getString(R.string.crash_reset_toast),
                        Toast.LENGTH_LONG).show();
                Looper.loop();
            }
        }.start();
    }
}
