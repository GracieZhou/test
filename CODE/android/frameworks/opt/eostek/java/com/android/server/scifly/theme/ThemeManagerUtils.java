
package com.android.server.scifly.theme;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigInteger;
import java.security.MessageDigest;
import java.util.Enumeration;
import java.util.Properties;
import java.util.zip.ZipEntry;
import java.util.zip.ZipException;
import java.util.zip.ZipFile;

import android.content.Context;
import android.content.SharedPreferences;
import android.content.SharedPreferences.Editor;
import android.os.FileUtils;

public class ThemeManagerUtils {
    private static final String TAG = "ThemeManagerUtils";

    private static final String mDataPath = "config";

    private static final int BUFF_SIZE = 1024 * 1024; // 1M Byte

    private static final String DEBUG_FILE_PATH = "/data/theme_debug.xml";

    private static final String DEBUG_KEY = "theme_debug";

    /**
     * unzip fileName.zip.
     * 
     * @param source source path.
     * @param destPath destination path.
     * @throws ZipException
     * @throws IOException
     */
    public static void upZipFile(String source, String destPath) throws ZipException, IOException {
        // delete folder and files.
        File file = new File(destPath);
        try {
            deleteAllFiles(file, false);
        } catch (Exception e) {
            e.printStackTrace();
        }

        // dest path is same as the zip file
        File destDir = new File(destPath);
        if (!destDir.exists()) {
            destDir.mkdirs();

            // change access right of folder.
            FileUtils.setPermissions(source, 0777, -1, -1);
        }

        ZipFile zf = new ZipFile(source);
        for (Enumeration<?> entries = zf.entries(); entries.hasMoreElements();) {
            ZipEntry entry = ((ZipEntry) entries.nextElement());

            String str = destPath + File.separator + entry.getName();

            if (entry.isDirectory()) {
                File desFile = new File(str);
                if (!desFile.exists()) {
                    desFile.mkdirs();

                    // change access right of folder.
                    FileUtils.setPermissions(str, 0777, -1, -1);

                }
            } else {
                // unzip file.
                InputStream in = zf.getInputStream(entry);
                File desFile = new File(str);
                if (!desFile.exists()) {
                    File fileParentDir = desFile.getParentFile();
                    if (!fileParentDir.exists()) {
                        fileParentDir.mkdirs();
                        FileUtils.setPermissions(fileParentDir.getAbsolutePath(), 0777, -1, -1);
                    }

                    desFile.createNewFile();

                    // change access right of file.
                    FileUtils.setPermissions(str, 0777, -1, -1);
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
        zf.close();
    }

    public static void deleteAllFiles(File file, boolean delParent) {

        if (file.exists()) {
            if (file.isFile()) {
                file.delete();
            } else {
                File[] files = file.listFiles();
                for (File f : files) {
                    if (file.isFile()) {
                        file.delete();
                    } else {
                        deleteAllFiles(f, true);
                    }
                }

                if (delParent) {
                    file.delete();
                }
            }
        }
    }

    /**
     * get current status.
     */
    public static boolean getStatus() {
        FileInputStream inStream = null;
        File file = null;
        String value = "false";
        file = new File(DEBUG_FILE_PATH);
        if (!file.exists()) {
            // System.out.println("file not exists.");
            return false;
        }
        try {
            inStream = new FileInputStream(file);
            Properties properties = new Properties();
            properties.load(inStream);

            value = properties.getProperty(DEBUG_KEY);
            inStream.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        // System.out.println("DebugMode:" + value);

        if (value == null) {
            return false;
        }

        if (value.equals("true")) {
            return true;
        }

        return false;
    }

    /**
     * get md5 value of file.
     * 
     * @param file
     * @return
     */
    public static String getFileMD5(String path) {
        File themeFile = new File(path);
        if (!themeFile.isFile()) {
            return null;
        }
        MessageDigest digest = null;
        FileInputStream in = null;
        byte buffer[] = new byte[1024];
        int len;
        try {
            digest = MessageDigest.getInstance("MD5");
            in = new FileInputStream(themeFile);
            while ((len = in.read(buffer, 0, 1024)) != -1) {
                digest.update(buffer, 0, len);
            }
            in.close();
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
        BigInteger bigInt = new BigInteger(1, digest.digest());
        return bigInt.toString(16).toUpperCase();
    }

    /**
     * @param status
     */
    public static void setString(Context context, String keyWord, String value) {
        try {
            SharedPreferences sp = context.getSharedPreferences(mDataPath, 0);
            Editor path = sp.edit();
            path.putString(keyWord, value).commit();
        } catch (Exception e) {
            e.printStackTrace();
        }

    }

    /**
	 */
    public static String getString(Context context, String keyword) {
        try {
            SharedPreferences sp = context.getSharedPreferences(mDataPath, 0);
            String temp = sp.getString(keyword, null);
            return temp;
        } catch (Exception e) {
            e.printStackTrace();
            return null;
        }
    }
}
