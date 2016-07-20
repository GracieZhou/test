
package com.eostek.scifly.advertising;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;

import android.net.Uri;
import android.util.Log;

/**
 * image cache class.
 * 
 * @author shirley
 */
public class ImageCache {

    private final static String TAG = "ImageCache";

    private static final int SIZE = 1024;

    private static byte[] buffer = new byte[SIZE];

    /**
     * generate constructor with no field.
     */
    public ImageCache() {
        super();
        // TODO Auto-generated constructor stub
    }

    /**
     * download image from server.
     * 
     * @param imgUrl ImgUrl
     * @param path Path
     */
    public static void getImage(String imgUrl, String path) {
        Log.d(TAG, " cache path : " + path);

        File file = new File(path);
        File parentDir = new File(file.getParent());
        if (!parentDir.exists()) {
            Log.d(TAG, "make cache dir : " + parentDir.mkdirs());
        }

        Uri uri = Uri.parse(imgUrl);
        Log.d(TAG, "uri : " + uri);

        URL url;
        InputStream is = null;
        FileOutputStream os = null;
        int count;
        try {
            url = new URL(imgUrl);
            URLConnection conn = url.openConnection();
            conn.connect();
            is = new BufferedInputStream(conn.getInputStream());
            os = new FileOutputStream(path);
            while (true) {
                count = is.read(buffer, 0, SIZE);
                if (count == 0 || count == -1) {
                    break;
                }
                if (count == SIZE) {
                    os.write(buffer);
                    os.flush();
                } else {
                    byte[] b = new byte[count];
                    for (int i = 0; i < count; i++) {
                        b[i] = buffer[i];
                    }
                    os.write(b);
                    os.flush();
                }
            }

        } catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } catch (IOException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        } finally {
            try {
                if (is != null) {
                    is.close();
                }
                if (os != null) {
                    os.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

    }

}
