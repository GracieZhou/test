
package com.android.packageinstaller;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import android.annotation.SuppressLint;
import android.content.Context;
import android.os.Environment;
import android.os.StatFs;
import android.text.TextUtils;
import android.util.Log;

public class ZipUtils {
    private static final String TAG = "PackageInstaller";

    private static final int BUFF_SIZE = 1024 * 1024; // 1M Byte

    public static final String UNZIP_RELATIVE_PATH = "/apks";
    
    /**
     * 根据绝对路径获取存储路径.
     * 
     * @param absolutePath
     * @return
     */
    @SuppressLint("NewApi")
    public static String getDestPath(String absolutePath, Context context) throws Exception{
        // String destPath = absolutePath.substring(0,
        // absolutePath.lastIndexOf(File.separator))
        // + UNZIP_RELATIVE_PATH;
        String destPath = context.getExternalCacheDir().getAbsolutePath() + UNZIP_RELATIVE_PATH;

        print("unzipPath:" + destPath);
        return destPath;
    }

    @SuppressLint("NewApi")
    public static void upZipFile(String zipFile, Context context) throws ZipException, IOException {
        if (TextUtils.isEmpty(zipFile)) {
            return;
        }

        // dest path is same as the zip file
        // String destPath = zipFile.substring(0,
        // zipFile.lastIndexOf(File.separator)) + UNZIP_RELATIVE_PATH;
        String destPath = context.getExternalCacheDir().getAbsolutePath() + UNZIP_RELATIVE_PATH;
        print("destPath : " + destPath);
        File destDir = new File(destPath);
        if (!destDir.exists()) {
            print("111" + destDir.mkdirs());
        }

        ZipFile zf = new ZipFile(zipFile);
        for (Enumeration<?> entries = zf.entries(); entries.hasMoreElements();) {
            ZipEntry entry = ((ZipEntry) entries.nextElement());
            InputStream in = zf.getInputStream(entry);
            String str = destPath + File.separator + entry.getName();
            File desFile = new File(str);
            Log.e("PackageInstaller", "str:" + str);
            if (!desFile.exists()) {
                File fileParentDir = desFile.getParentFile();
                if (!fileParentDir.exists()) {
                    print("222" + fileParentDir.mkdirs());
                }
                print("333" + desFile.createNewFile());
            }

            OutputStream out = new FileOutputStream(desFile);
            byte buffer[] = new byte[BUFF_SIZE];
            int realLength;
            while ((realLength = in.read(buffer)) > 0) {
                out.write(buffer, 0, realLength);
            }
            out.flush();
            in.close();
            out.close();
            in = null;
            out = null;
        }
    }

    public static ArrayList<String> getEntriesNames(String zipFile) throws ZipException, IOException {
        File zip = new File(zipFile);
        if (!zip.exists()) {
            return null;
        }

        ArrayList<String> entryNames = new ArrayList<String>();
        Enumeration<?> entries = getEntriesEnumeration(zip);
        while (entries.hasMoreElements()) {
            ZipEntry entry = ((ZipEntry) entries.nextElement());
            entryNames.add(entry.getName());
        }

        return entryNames;
    }

    public static Enumeration<?> getEntriesEnumeration(File zipFile) throws ZipException, IOException {
        ZipFile zf = new ZipFile(zipFile);
        return zf.entries();
    }

    private static final boolean DEBUG = true;

    private static void print(String msg) {
        if (msg != null && DEBUG) {
            Log.i(TAG, msg);
        }
    }
    
    //获取sdcard剩余空间
    public static long getSdcardAvailaleSize(){
        File file = Environment.getExternalStorageDirectory();
        StatFs statFs = new StatFs(file.getPath());
        long availaleBlocks = statFs.getAvailableBlocks();
        long blockSize = statFs.getBlockSize();
        return availaleBlocks * blockSize * 8; // byte
    }

}
