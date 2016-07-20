
package com.bq.tv.traxex.util;

import java.io.Closeable;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.graphics.Bitmap;
import android.graphics.Bitmap.CompressFormat;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.Log;

/**
 * Class of bitmap utils.
 */
public class BitmapUtils {
    private static final String TAG = "BitmapUtils";

    /**
     * Get Image bitmap.
     * 
     * @param path
     * @param width
     * @param height
     * @return
     */
    public static Bitmap getImageBitmap(String path, int width, int height) {

        BitmapFactory.Options options = new BitmapFactory.Options();

        options.inJustDecodeBounds = true;
        Bitmap bitmap = BitmapFactory.decodeFile(path, options);

        // 计算缩放比
        float be1 = options.outWidth / width;
        float be2 = options.outHeight / height;

        int be = (int) ((be1 > be2) ? be2 : be1);
        if (be <= 0) {
            be = 1;
        }
        options.inSampleSize = be;
        options.inJustDecodeBounds = false;
        options.inPurgeable = false;
        options.inInputShareable = true;
        bitmap = BitmapFactory.decodeFile(path, options);
        return bitmap;
    }

    /**
     * Get Video Bitmap. WARNING: This function is empty now!
     * 
     * @param path
     * @param widthWanted
     * @param heightWanted
     * @return
     */
    public static Bitmap getVideoBitmap(String path, int widthWanted, int heightWanted) {
        // byte[] b = null;
        // String location = Util.PathToURI(path);
        // try {
        // b = LibVLC.getInstance().getThumbnail(location, widthWanted,
        // heightWanted);
        // } catch (LibVlcException e) {
        // e.printStackTrace();
        // }
        //
        // if (b == null) {
        // return null;
        // }
        //
        // Bitmap thumbnail = Bitmap.createBitmap(widthWanted, heightWanted,
        // Config.ARGB_8888);
        // thumbnail.copyPixelsFromBuffer(ByteBuffer.wrap(b));
        // thumbnail = getWantedBitmap(thumbnail, widthWanted, heightWanted);
        // return thumbnail;
        return null;
    }

    /**
     * Get the wanted bitmap.
     * 
     * @param bitmap
     * @param widthWanted
     * @param heightWanted
     * @return
     */
    public static Bitmap getWantedBitmap(Bitmap bitmap, int widthWanted, int heightWanted) {
        int width = bitmap.getWidth();
        int height = bitmap.getHeight();

        float scale;
        int maxSide = width > height ? width : height;
        if (maxSide > (widthWanted * 2 / 3)) {
            scale = Math.min((float) widthWanted / (float) width, (float) heightWanted / (float) height);
        } else {
            scale = 1.0f;
        }

        if (maxSide < heightWanted / 3) {
            Matrix matrix = new Matrix();
            matrix.postScale(2.0f, 2.0f);
            bitmap = Bitmap.createBitmap(bitmap, 0, 0, width, height, matrix, true);

            width = bitmap.getWidth();
            height = bitmap.getHeight();
        }

        int dx = (int) ((widthWanted / scale - width) / 2);
        int dy = (int) ((heightWanted / scale - height) / 2);

        Bitmap b = Bitmap.createBitmap(widthWanted, heightWanted, Bitmap.Config.RGB_565);
        Canvas canvas = new Canvas(b);
        Paint paint = new Paint();
        paint.setDither(true);
        paint.setFilterBitmap(true);
        canvas.drawColor(0);
        canvas.drawBitmap(bitmap, new Rect(-dx, -dy, (int) (widthWanted / scale) - dx, (int) (heightWanted / scale)
                - dy), new Rect(0, 0, widthWanted, heightWanted), paint);
        return b;
    }

    /**
     * Save bitmap.
     * 
     * @param bitmap
     * @param path
     */
    public static void saveBitmap(Bitmap bitmap, String path) {
        if (bitmap == null) {
            return;
        }

        Log.i(TAG, "saveBitmap path: " + path + ", width: " + bitmap.getWidth() + ", height: " + bitmap.getHeight());
        File file = new File(path);
        if (!file.exists()) {
            try {
                file.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                return;
            }
        }

        FileOutputStream out = null;
        try {
            out = new FileOutputStream(file);
        } catch (FileNotFoundException e) {
            e.printStackTrace();
            return;
        }

        bitmap.compress(CompressFormat.PNG, 100, out);
        closeSilently(out);
    }

    /**
     * Close silently.
     * 
     * @param c
     */
    public static void closeSilently(Closeable c) {
        if (c == null)
            return;
        try {
            c.close();
        } catch (Throwable t) {
            Log.w(TAG, "close fail", t);
        }
    }
}
