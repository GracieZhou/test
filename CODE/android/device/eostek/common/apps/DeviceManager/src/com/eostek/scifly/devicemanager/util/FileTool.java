package com.eostek.scifly.devicemanager.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;

import android.content.Context;
import android.content.res.AssetManager;
import android.os.Environment;

public class FileTool {
    
    private static final String TAG = FileTool.class.getSimpleName();
    /**
     * If file, return the length of this file;
     * If directory, return the length of all the subfiles and this file
     * @param file File that need to get its size.
     * @return The actual size of this file.
     */
    public static long getFileLength(File file) {
        long size = 0;
        if (!file.exists()) {
            return size;
        }
        size += file.length();
        if (file.isDirectory()) {
            File[] files = file.listFiles();
            for (File tempfile : files) {
                size += getFileLength(tempfile);
            }
        }
        return size;
    }

    /**
     * If file, delete it;
     * If directory, delete its subfile first then delete it.
     * @param file File that need to be deleted.
     */
    public static void deleteFile(File file) {
        if (!file.exists()) {
            return;
        }
        if (file.isFile()) {
            file.delete();
        } else {
            File[] files = file.listFiles();
            for (File tempfile : files) {
                deleteFile(tempfile);
            }
            file.delete();
        }
    }

    /**
     * Check if this file an APK.
     * @param file File that need to be checked.
     * @return True if file is an APK or APKs, otherwise false.
     */
    public static boolean isFileApk(File file) {
        if (file.exists() && file.isFile()) {
            String filename = file.getName();
            return filename.toLowerCase().endsWith(".apk")
                    || filename.toLowerCase().endsWith(".apks");
        }
        return false;
    }
    

	
  /**
     * if it is a file return true
     * else return false 
     * @param path
     * @return
     */
    public static boolean fileIsExiested(String path){
    	File file = new File(path);
    	if (!file.exists()) {
			return false ;
		} else if (file.isDirectory()) {
			return false ;
		}else {
			
			return true ;
		}
    }
    /**
     * Get file: /data/data/com.eostek.scifly.devicemanager/databases/filepath.db.
     * If not exist, copy assets/filepath.db to /data/data/com.eostek.scifly.devicemanager/databases/.
     * @param context Context of the caller.
     * @return Return filepath.db if file exists already or copy successfully; otherwise return null.
     */
    public static File getDbFile(Context context) {
        String database = Environment.getDataDirectory().getAbsolutePath()
                .concat("/data/").concat(context.getPackageName()).concat("/databases");
        Debug.d(TAG, "Database path: [" + database + "]");
        File databaseDir = new File(database);
        File databaseFile = new File(databaseDir, Constants.DATABASE_FILE_NAME);
        if (databaseFile.exists() && databaseFile.length() > 0 && databaseFile.canRead()) {
            Debug.d(TAG, "Database already exists [" + databaseFile.getAbsolutePath() + "]");
            return databaseFile;
        }

        if (databaseDir.isDirectory() || databaseDir.mkdir()) {
            AssetManager am = context.getAssets();
            //copy filepath.db to /data/data/com.eostek.scifly.devicemanager/databases/
            try {
                InputStream is = am.open(Constants.DATABASE_FILE_NAME);
                FileOutputStream fos = new FileOutputStream(databaseFile);
                byte[] buffer = new byte[4096];
                int count = 0;
                while ((count = is.read(buffer)) > 0) {
                    fos.write(buffer, 0, count);
                }
                fos.close();
                is.close();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        if (databaseFile.exists() && databaseFile.length() > 0 && databaseFile.canRead()) {
            Debug.d(TAG, "Write database successfully [" + databaseFile.getAbsolutePath() + "]");
            return databaseFile;
        } else {
            Debug.e(TAG, "Fail to write database [" + databaseFile.getAbsolutePath() + "]");
            databaseFile.deleteOnExit();
            return null;
        }
    }
    
    public static void createDir(String path) {
        File file = new File(path);
        //
        if (!file.exists()) {
            file.mkdir();
        }
   }
}
