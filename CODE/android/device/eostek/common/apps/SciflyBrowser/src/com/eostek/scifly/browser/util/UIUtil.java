package com.eostek.scifly.browser.util;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;

public class UIUtil {

    private final static String TAG = "UIUtil";

    private static Object mLock = new Object();

    public static boolean isNetConnected(Context context) {
        ConnectivityManager manager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        NetworkInfo[] infos = manager.getAllNetworkInfo();
        if (infos != null) {
            for (int i = 0; i < infos.length; i++) {
                NetworkInfo info = infos[i];
                if (info.isConnected()) {
                    Log.v(TAG, "isNetConnected = true");
                    return true;
                }
            }
        }
        Log.v(TAG, "isNetConnected = false");
        return false;
    }

    public static void saveBitmap(Bitmap bitmap, String fileName) {
        
    }

    /**
     * save string to file.
     * @param string
     * @param filePath
     */
    public static void saveFileFromString(String string, String filePath) {
        synchronized (mLock) {
            BufferedWriter bWriter = null;
            try {
                bWriter = new BufferedWriter(new OutputStreamWriter(new FileOutputStream(new File(filePath))));
                bWriter.write(string);
                bWriter.flush();
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (bWriter != null) {
                        bWriter.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * change file to string
     * @param filePath
     * @return
     */
    public static String getStringFromFile (String filePath) {
        synchronized (mLock) {
            File file =new File(filePath);
            if (!file.exists()) {
                return null;
            }
            
            BufferedReader bReader = null;
            StringBuffer sBuffer = new StringBuffer();
            String line = null;
            try {
                bReader = new BufferedReader(new InputStreamReader(new FileInputStream(file)));
                while ((line = bReader.readLine()) != null) {
                    sBuffer.append(line);
                }
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } finally {
                try {
                    if (bReader != null) {
                        bReader.close();
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            return sBuffer.toString();
        }
    }
}
