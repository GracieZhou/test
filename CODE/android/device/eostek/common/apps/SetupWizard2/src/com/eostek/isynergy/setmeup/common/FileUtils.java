
package com.eostek.isynergy.setmeup.common;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

import android.content.Context;
import android.util.Log;

public class FileUtils {
    private static final String TAG = "FileUtils";

    /**
     * 将assets下面的目录或者文件拷贝到 指定目录
     * 
     * @param context
     * @param source 目录位于assets 是相对于assets的相对路径
     * @param dest 目的路径，需要是一个目录
     * @throws IOException
     */
    public static void copyAssetFolder(Context context, String source, String dest) throws IOException {
        Log.d(TAG, "source dir = " + source + " destination dir = " + dest);

        int readLen = -1;

        byte buffer[] = new byte[Constants.MAX_READ_BUFFER_LEN];

        String sourceFiles[] = context.getAssets().list(source);

        File destFile = new File(dest);

        if (!destFile.exists()) {
            Log.d(TAG, "destination is not existing ,make it " + dest);
            destFile.mkdirs();
        }

        for (String sourceFile : sourceFiles) {
            Log.d(TAG, "sourceFile = " + source + File.separator + sourceFile + " destination = " + dest);

            String tempFileName = dest + File.separator + sourceFile;

            File tempFile = new File(tempFileName);

            boolean isBlankFile = isBlankFile(tempFileName);

            if (isBlankFile) {
                Log.d(TAG, "deleting file:" + tempFileName);
                boolean isSu = tempFile.delete();
                Log.d(TAG, (isSu ? "success" : "fail") + " to delete file:" + tempFileName);
            }

            if (!tempFile.exists()) {
                InputStream is = null;

                OutputStream os = null;

                try {

                    is = context.getAssets().open(source + File.separator + sourceFile);

                    os = new FileOutputStream(tempFile);

                    while ((readLen = is.read(buffer)) != -1) {
                        os.write(buffer, 0, readLen);
                    }
                } catch (IOException ex) {
                    throw ex;
                } finally {
                    if (is != null) {
                        is.close();
                    }

                    if (os != null) {
                        os.close();
                    }
                }
            }
        }
    }

    /**
     * 获取设备描述文件的路径
     * 
     * @return 设备描述文件的路径，如果没有找到则返回null
     */
    public static String getDescriptionFilePath(Context context) {
        File descriptionFile = context.getDir(Constants.CONFIG_CUSTOM_DIR, Context.MODE_PRIVATE);
        if (descriptionFile != null) {
            String desFiles[] = descriptionFile.list();
            if (desFiles != null && desFiles.length == 0) {
                return null;
            }

            for (String fileName : desFiles) {
                if (Constants.CONFIG_FILE_DESCRIPTION.equals(fileName)) {
                    try {
                        return descriptionFile.getCanonicalPath() + File.separator + fileName;
                    } catch (IOException e) {
                        return null;
                    }
                }
            }

        }
        return null;
    }

    public static boolean isBlankFile(String fileName) {
        File file = new File(fileName);

        Log.d(TAG, "file:" + fileName + " size:" + file.length());
        if (file.exists() && file.length() <= 1) {
            return true;
        }

        return false;
    }
}
