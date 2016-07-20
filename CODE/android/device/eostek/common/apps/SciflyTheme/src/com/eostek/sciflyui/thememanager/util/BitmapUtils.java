
package com.eostek.sciflyui.thememanager.util;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.os.FileUtils;
import android.util.Log;

/**
 * BitmapUtils.
 * 
 * @author
 */
public final class BitmapUtils {
    private static final int QUALITY = 100;

    private static final String TAG = "BitmapUtils";

    /**
     * constructor.
     */
    private BitmapUtils() {
    }

    /**
     * zoom out the bitmap.
     * 
     * @param bitmap source bitmap
     * @param rate the scale
     * @return bitmap after zoom out
     */
    public static Bitmap zoomOut(Bitmap bitmap, float rate) {
        if (bitmap == null) {
            return bitmap;
        }
        Matrix matrix = new Matrix();
        matrix.postScale(rate, rate); // 长和宽放大缩小的比例
        Bitmap resizeBmp = Bitmap.createBitmap(bitmap, 0, 0, bitmap.getWidth(), bitmap.getHeight(), matrix, true);
        return resizeBmp;
    }

    /**
     * get Bitmap Scale.
     * 
     * @param src src bitmap
     * @param itemWidth item width
     * @return return result
     */
    public static float getBitmapScale(Bitmap src, int itemWidth) {
        if (src == null) {
            return 1;
        }
        float rate = 1;

        int sWid = src.getWidth();
        int sHei = src.getHeight();
        if (sWid == 0 || sHei == 0) {
            return rate;
        }

        if (itemWidth != 0) {
            rate = (float) itemWidth / sWid;
        }
        return rate;
    }

    /**
     * @param src source bitmap
     * @param imgPath image path
     */
    public static void saveBitmap(Bitmap src, String imgPath) {

        if (src == null) {
            return;
        }

        File saveFile = new File(imgPath); // task.getLocalUrl()

        if (!saveFile.exists()) {
            try {

                File parentFile = saveFile.getParentFile();
                if (!parentFile.exists()) {
                    parentFile.mkdirs();
                }

                saveFile.createNewFile();
            } catch (IOException e) {
                e.printStackTrace();
                Log.i(TAG, "failed：" + e.toString());
            }

            FileOutputStream fOut = null;
            try {
                fOut = new FileOutputStream(saveFile);
            } catch (FileNotFoundException e) {
                e.printStackTrace();
            }
            src.compress(Bitmap.CompressFormat.PNG, QUALITY, fOut);
            try {
                if (fOut != null) {
                    fOut.flush();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            try {
                if (fOut != null) {
                    fOut.close();
                }
            } catch (IOException e) {
                e.printStackTrace();
            }
            FileUtils.setPermissions(imgPath, 0777, -1, -1);
        }
    }

    /**
     * add watermark on the bitmap which from file.
     * 
     * @param src src
     * @param fileName fileName
     * @param res res
     * @param resId resId
     * @return Bitmap
     */
    public static Bitmap addWatermark(Bitmap src, String fileName, Resources res, int resId) {

        Bitmap watermark = BitmapFactory.decodeResource(res, resId);
        if (src == null || watermark == null) {
            return src;
        }

        int sWid = src.getWidth();
        int sHei = src.getHeight();
        int wWid = watermark.getWidth();
        int wHei = watermark.getHeight();
        if (sWid == 0 || sHei == 0) {
            return null;
        }

        if (sWid < wWid || sHei < wHei) {
            return src;
        }

        Bitmap bitmap = null;
        try {

            bitmap = Bitmap.createBitmap(sWid, sHei, Config.ARGB_8888);
            Canvas cv = new Canvas(bitmap);
            cv.drawBitmap(src, 0, 0, null);
            cv.drawBitmap(watermark, (sWid - wWid) / 2, (sHei - wHei) / 2, null);
            cv.save(Canvas.ALL_SAVE_FLAG);
            cv.restore();

        } catch (Exception e) {
            bitmap = null;
            e.getStackTrace();
        }
        return bitmap;
    }

}
