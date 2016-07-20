
package com.eostek.documentui.util;

import java.io.File;
import java.text.DecimalFormat;

import scifly.storage.StorageManagerExtra;
import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff.Mode;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.graphics.RectF;

@SuppressLint("InlinedApi")
public class Utils {
    public static final int SORT_ORDER_UNKNOWN = 0;

    public static final int SORT_ORDER_DISPLAY_NAME = 1;

    public static final int SORT_ORDER_LAST_MODIFIED = 2;

    public static final int SORT_ORDER_SIZE = 3;

    /**
     * dp to px
     */
    public static int dip2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

    /**
     * px to dp
     */
    public static int px2dip(Context context, float pxValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (pxValue / scale + 0.5f);
    }

    /**
     * @Title: createRoundCornerBitmap.
     * @Description: create RoundCorner Bitmap.
     * @param: @param bitmap
     * @param: @param pixels
     * @param: @return.
     * @return: Bitmap.
     * @throws
     */
    public static Bitmap createRoundCornerBitmap(Bitmap bitmap, int pixels) {
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(), bitmap.getHeight(), Config.ARGB_8888);
        Canvas canvas = new Canvas(output);
        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);
        final float roundPx = pixels;
        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawRoundRect(rectF, roundPx, roundPx, paint);
        paint.setXfermode(new PorterDuffXfermode(Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);
        return output;
    }

    /**
     * @Title: deleteFile.
     * @Description: deleteFile.
     * @param: @param file.
     * @return: void.
     * @throws
     */
    public static void deleteFile(File file) {
        if (file.exists()) {
            if (file.isFile()) {
                file.delete();
            } else if (file.isDirectory()) {
                File files[] = file.listFiles();
                for (int i = 0; i < files.length; i++) {
                    deleteFile(files[i]);
                }
            }
            file.delete();
        }
    }

    /**
     * @Title: FormetFileSize.
     * @Description: transform size .
     * @param: @param fileS
     * @param: @return.
     * @return: String.
     * @throws
     */
    public static String formatFileSize(long fileSize) {
        if (fileSize == 0) {
            return "0KB";
        }
        DecimalFormat df = new DecimalFormat("#.0");
        String fileSizeString = "";
        if (fileSize < 1024) {
            fileSizeString = df.format((double) fileSize) + "B";
        } else if (fileSize < 1048576) {
            fileSizeString = df.format((double) fileSize / 1024) + "KB";
        } else if (fileSize < 1073741824) {
            fileSizeString = df.format((double) fileSize / 1048576) + "MB";
        } else {
            fileSizeString = df.format((double) fileSize / 1073741824) + "GB";
        }
        return fileSizeString;
    }

    /**
     * @Title: formatFloatAccordingDigit.
     * @Description: digits after the decimal point .
     * @param: @param value 12.3456789
     * @param: @param digit 10(1),100(2),1000(3).....
     * @param: @return. 12.3,12.34,12.346......
     * @return: float.
     * @throws
     */
    public static float formatFloatAccordingDigit(float value, int digit) {
        return (float) (Math.round(value * digit)) / digit;
    }

    /**
     * @Title: isHasUDisk.
     * @Description: isHasUDisk
     * @param: @return.
     * @return: boolean.
     * @throws
     */
    public static boolean isHasUDisk(Context context) {
        StorageManagerExtra storageManager = StorageManagerExtra.getInstance(context);
        String[] uDiskPaths = storageManager.getUdiskPaths();
        if (uDiskPaths == null || uDiskPaths.length == 0) {
            return false;
        } else {
            return true;
        }
    }

    public static String getFileName(String uri) {
        int start = uri.lastIndexOf("/");
        int end = uri.length();
        if (start != -1) {
            return uri.substring(start + 1, end);
        } else {
            return "";
        }
    }

    public static String getTypeFromName(String name) {
        int start = name.lastIndexOf(".");
        int end = name.length();
        if (start != -1) {
            return name.substring(start, end);
        } else {
            return "";
        }
    }
}
